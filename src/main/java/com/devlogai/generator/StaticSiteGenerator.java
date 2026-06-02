package com.devlogai.generator;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a static HTML site from all markdown devlog files.
 * Output: docs/index.html (GitHub Pages serves from /docs folder)
 *
 * Features:
 *  - Beautiful dark theme matching GitHub's aesthetic
 *  - Stats dashboard on homepage (total commits, lines, streak)
 *  - Tag badges on devlog cards
 *  - Individual devlog pages with full content
 *  - Responsive design
 */
public class StaticSiteGenerator {

    private static final String DEVLOGS_DIR = "devlogs";
    private static final String SITE_DIR    = "docs";

    public void regenerate() throws Exception {
        File siteDir = new File(SITE_DIR);
        if (!siteDir.exists()) siteDir.mkdirs();

        File devlogsDir = new File(DEVLOGS_DIR);
        List<File> devlogs = new ArrayList<>();

        if (devlogsDir.exists()) {
            File[] files = devlogsDir.listFiles(f -> f.getName().endsWith(".md"));
            if (files != null) {
                devlogs = Arrays.stream(files)
                        .sorted(Comparator.comparing(File::getName).reversed())
                        .collect(Collectors.toList());
            }
        }

        generateIndexPage(devlogs, siteDir);
        for (File devlog : devlogs) {
            generateDevlogPage(devlog, siteDir);
        }
    }

    // ─── Stats extraction ────────────────────────────────────────────

    private int extractTotalCommits(List<File> devlogs) {
        int total = 0;
        for (File f : devlogs) {
            try {
                String content = Files.readString(f.toPath());
                for (String line : content.split("\n")) {
                    if (line.startsWith("| Total Commits |")) {
                        String val = line.replaceAll("[^0-9]", "").trim();
                        if (!val.isEmpty()) total += Integer.parseInt(val);
                    }
                }
            } catch (Exception ignored) {}
        }
        return total;
    }

    private int extractTotalLines(List<File> devlogs) {
        int total = 0;
        for (File f : devlogs) {
            try {
                String content = Files.readString(f.toPath());
                for (String line : content.split("\n")) {
                    if (line.startsWith("| Lines Added")) {
                        String val = line.replaceAll("[^0-9]", "").trim();
                        if (!val.isEmpty()) total += Integer.parseInt(val);
                    }
                }
            } catch (Exception ignored) {}
        }
        return total;
    }

    private String detectTag(String filename) {
        // filename like: 2026-06-02-feat.md or week-2026-06-02-fix.md
        String[] parts = filename.replace(".md", "").split("-");
        String last = parts[parts.length - 1];
        Set<String> knownTags = Set.of("feat", "fix", "docs", "refactor", "chore", "test");
        return knownTags.contains(last) ? last : null;
    }

    private String tagColor(String tag) {
        if (tag == null) return "#58a6ff";
        return switch (tag) {
            case "feat"     -> "#3fb950";
            case "fix"      -> "#f85149";
            case "docs"     -> "#58a6ff";
            case "refactor" -> "#d2a8ff";
            case "chore"    -> "#8b949e";
            case "test"     -> "#ffa657";
            default         -> "#58a6ff";
        };
    }

    // ─── Page generators ─────────────────────────────────────────────

    private void generateIndexPage(List<File> devlogs, File siteDir) throws Exception {

        // Compute stats
        int totalDevlogs  = devlogs.size();
        int totalCommits  = extractTotalCommits(devlogs);
        int totalLines    = extractTotalLines(devlogs);

        // Stats bar HTML
        String statsHtml = totalDevlogs > 0 ? """
            <div class="stats-bar">
              <div class="stat">
                <div class="stat-value">%d</div>
                <div class="stat-label">DevLogs</div>
              </div>
              <div class="stat">
                <div class="stat-value">%d</div>
                <div class="stat-label">Total Commits</div>
              </div>
              <div class="stat">
                <div class="stat-value">+%d</div>
                <div class="stat-label">Lines Written</div>
              </div>
            </div>
            """.formatted(totalDevlogs, totalCommits, totalLines) : "";

        // Cards HTML
        StringBuilder entries = new StringBuilder();
        for (File f : devlogs) {
            String name     = f.getName().replace(".md", "");
            String tag      = detectTag(f.getName());
            String tagBadge = tag != null
                ? "<span class=\"tag-badge\" style=\"background:%s\">%s</span>"
                  .formatted(tagColor(tag), tag)
                : "";

            String displayName;
            if (name.startsWith("week-")) {
                String datePart = name.replace("week-", "").replaceAll("-[a-z]+$", "");
                displayName = "📅 Week of " + datePart;
            } else {
                String datePart = name.replaceAll("-[a-z]+$", "");
                displayName = "📝 " + datePart;
            }

            String preview = getFirstLine(f);
            String href    = name + ".html";

            entries.append("""
                <a href="%s" class="card">
                  <div class="card-header">
                    <div class="card-title">%s</div>
                    %s
                  </div>
                  <div class="card-preview">%s</div>
                </a>
                """.formatted(href, displayName, tagBadge, preview));
        }

        if (devlogs.isEmpty()) {
            entries.append("""
                <div class="empty">
                  <div class="empty-icon">🚀</div>
                  <p>No devlogs yet!</p>
                  <p>Run <code>java -jar devlog-ai.jar generate</code> to create your first one.</p>
                </div>
                """);
        }

        String body = """
            <div class="hero">
              <h1>🔥 DevLog AI</h1>
              <p class="subtitle">Open Source Standup for Solo Developers</p>
              <p class="badge">by <strong>ForkAndFire</strong> · sid0x03</p>
              <div class="hero-btns">
                <a href="https://github.com/goswamiSiddharth/devlog-ai" class="github-btn">⭐ Star on GitHub</a>
                <a href="https://github.com/goswamiSiddharth/devlog-ai#readme" class="docs-btn">📖 Docs</a>
              </div>
            </div>
            %s
            <div class="section-header">
              <div class="section-title">📋 All DevLogs</div>
              <div class="section-count">%d entries</div>
            </div>
            <div class="grid">%s</div>
            """.formatted(statsHtml, totalDevlogs, entries.toString());

        writeFile(new File(siteDir, "index.html"),
                  BASE_HTML.formatted("DevLog AI — ForkAndFire", SITE_STYLES, body));
    }

    private void generateDevlogPage(File mdFile, File siteDir) throws Exception {
        String name       = mdFile.getName().replace(".md", "");
        String content    = Files.readString(mdFile.toPath());
        String htmlContent = markdownToHtml(content);

        String body = """
            <div class="back-bar">
              <a href="index.html">← All DevLogs</a>
            </div>
            <div class="devlog-content">
            %s
            </div>
            """.formatted(htmlContent);

        writeFile(new File(siteDir, name + ".html"),
                  BASE_HTML.formatted(name + " — DevLog AI", SITE_STYLES, body));
    }

    // ─── Markdown → HTML ─────────────────────────────────────────────

    private String markdownToHtml(String md) {
        StringBuilder html = new StringBuilder();
        boolean inTable = false;

        for (String line : md.split("\n")) {
            if (line.startsWith("# "))        html.append("<h1>").append(esc(line.substring(2))).append("</h1>\n");
            else if (line.startsWith("## "))  html.append("<h2>").append(esc(line.substring(3))).append("</h2>\n");
            else if (line.startsWith("### ")) html.append("<h3>").append(esc(line.substring(4))).append("</h3>\n");
            else if (line.startsWith("> "))   html.append("<blockquote>").append(esc(line.substring(2))).append("</blockquote>\n");
            else if (line.startsWith("- "))   html.append("<li>").append(esc(line.substring(2))).append("</li>\n");
            else if (line.startsWith("---"))  html.append("<hr/>\n");
            else if (line.startsWith("|")) {
                if (!inTable) { html.append("<table>\n"); inTable = true; }
                html.append(tableRow(line)).append("\n");
            } else {
                if (inTable) { html.append("</table>\n"); inTable = false; }
                if (line.isBlank()) html.append("<br/>\n");
                else html.append("<p>").append(esc(line)).append("</p>\n");
            }
        }
        if (inTable) html.append("</table>\n");
        return html.toString();
    }

    private String tableRow(String line) {
        if (line.replaceAll("[|\\-\\s]", "").isEmpty()) return "";
        String[] cells = line.split("\\|");
        StringBuilder row = new StringBuilder("<tr>");
        for (String cell : cells) {
            if (!cell.isBlank()) row.append("<td>").append(esc(cell.trim())).append("</td>");
        }
        return row.append("</tr>").toString();
    }

    private String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("`", "<code>").replace("**", "<strong>").replace("*", "<em>");
    }

    private String getFirstLine(File f) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")
                        && !line.startsWith(">") && !line.startsWith("---")
                        && !line.startsWith("*Generated")) {
                    return line.length() > 120 ? line.substring(0, 120) + "..." : line;
                }
            }
        }
        return "Click to read this devlog.";
    }

    private void writeFile(File file, String content) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print(content);
        }
    }

    // ─── HTML Template ───────────────────────────────────────────────

    private static final String BASE_HTML = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8"/>
          <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          <title>%s</title>
          <style>%s</style>
        </head>
        <body>
          <div class="container">%s</div>
          <footer>
            <p>🔥 DevLog AI &nbsp;·&nbsp; Open Source &nbsp;·&nbsp;
              <a href="https://github.com/goswamiSiddharth">goswamiSiddharth</a> &nbsp;·&nbsp;
              <a href="https://github.com/goswamiSiddharth/devlog-ai">GitHub</a>
            </p>
          </footer>
        </body>
        </html>
        """;

    private static final String SITE_STYLES = """
        *{box-sizing:border-box;margin:0;padding:0}
        body{font-family:'Segoe UI',system-ui,sans-serif;background:#0d1117;color:#e6edf3;min-height:100vh}
        .container{max-width:960px;margin:0 auto;padding:40px 20px}

        /* Hero */
        .hero{text-align:center;padding:64px 20px 40px;border-bottom:1px solid #21262d;margin-bottom:36px}
        .hero h1{font-size:3rem;margin-bottom:12px;letter-spacing:-1px}
        .subtitle{color:#8b949e;font-size:1.15rem;margin-bottom:6px}
        .badge{color:#f78166;margin-bottom:28px;font-size:.95rem}
        .hero-btns{display:flex;gap:12px;justify-content:center;flex-wrap:wrap}
        .github-btn{background:#238636;color:#fff;text-decoration:none;padding:10px 22px;border-radius:6px;font-weight:600;transition:background .2s}
        .github-btn:hover{background:#2ea043}
        .docs-btn{background:#21262d;color:#e6edf3;text-decoration:none;padding:10px 22px;border-radius:6px;font-weight:600;border:1px solid #30363d;transition:background .2s}
        .docs-btn:hover{background:#30363d}

        /* Stats bar */
        .stats-bar{display:flex;gap:16px;justify-content:center;flex-wrap:wrap;margin-bottom:36px;padding:24px;background:#161b22;border:1px solid #21262d;border-radius:12px}
        .stat{text-align:center;min-width:100px}
        .stat-value{font-size:2rem;font-weight:700;color:#f0883e}
        .stat-label{font-size:.8rem;color:#8b949e;margin-top:4px;text-transform:uppercase;letter-spacing:.5px}

        /* Section header */
        .section-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px}
        .section-title{font-size:1.2rem;font-weight:700;color:#f0883e}
        .section-count{font-size:.85rem;color:#8b949e}

        /* Cards */
        .grid{display:grid;gap:12px}
        .card{background:#161b22;border:1px solid #21262d;border-radius:10px;padding:18px 22px;text-decoration:none;color:#e6edf3;transition:border-color .2s,transform .15s;display:block}
        .card:hover{border-color:#f78166;transform:translateY(-2px)}
        .card-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:6px}
        .card-title{font-size:1rem;font-weight:600}
        .card-preview{color:#8b949e;font-size:.88rem;line-height:1.5}
        .tag-badge{font-size:.72rem;font-weight:700;padding:2px 8px;border-radius:20px;color:#0d1117;text-transform:uppercase;letter-spacing:.5px}

        /* Empty state */
        .empty{text-align:center;color:#8b949e;padding:60px 20px}
        .empty-icon{font-size:3rem;margin-bottom:16px}
        .empty p{margin-bottom:8px}
        .empty code{background:#21262d;padding:2px 8px;border-radius:4px;color:#f0883e;font-size:.9rem}

        /* Back bar */
        .back-bar{margin-bottom:28px}
        .back-bar a{color:#58a6ff;text-decoration:none;font-size:.95rem}
        .back-bar a:hover{text-decoration:underline}

        /* Devlog content */
        .devlog-content h1{font-size:1.9rem;margin:28px 0 12px;color:#f0883e}
        .devlog-content h2{font-size:1.35rem;margin:22px 0 10px;color:#58a6ff;border-bottom:1px solid #21262d;padding-bottom:6px}
        .devlog-content h3{font-size:1.1rem;margin:16px 0 8px;color:#e6edf3}
        .devlog-content p{line-height:1.75;margin-bottom:10px;color:#c9d1d9}
        .devlog-content li{margin:6px 0 6px 22px;color:#c9d1d9;line-height:1.65}
        .devlog-content code{background:#21262d;padding:2px 6px;border-radius:4px;color:#f0883e;font-size:.9em}
        .devlog-content blockquote{border-left:3px solid #f78166;padding:10px 16px;margin:14px 0;color:#8b949e;background:#161b22;border-radius:0 8px 8px 0}
        .devlog-content hr{border:none;border-top:1px solid #21262d;margin:24px 0}
        .devlog-content table{width:100%;border-collapse:collapse;margin:16px 0}
        .devlog-content tr{border-bottom:1px solid #21262d}
        .devlog-content tr:first-child{background:#161b22;color:#8b949e;font-size:.85rem}
        .devlog-content td{padding:10px 14px;font-size:.9rem}
        .devlog-content strong{color:#f0883e;font-weight:700}
        .devlog-content em{color:#d2a8ff}

        /* Footer */
        footer{text-align:center;padding:32px;color:#8b949e;font-size:.82rem;border-top:1px solid #21262d;margin-top:64px}
        footer a{color:#58a6ff;text-decoration:none}
        footer a:hover{text-decoration:underline}

        /* Responsive */
        @media(max-width:600px){
          .hero h1{font-size:2.2rem}
          .stats-bar{gap:24px}
          .stat-value{font-size:1.6rem}
        }
        """;
}
