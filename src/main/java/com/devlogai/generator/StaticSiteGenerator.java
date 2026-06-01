package com.devlogai.generator;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a static HTML site from all markdown devlog files.
 * Output: docs/site/index.html  (GitHub Pages serves from /docs)
 *
 * Deploy: Enable GitHub Pages → Source: /docs folder → branch: main
 */
public class StaticSiteGenerator {

    private static final String DEVLOGS_DIR  = "devlogs";
    private static final String SITE_DIR     = "docs";

    public void regenerate() throws Exception {
        File siteDir = new File(SITE_DIR);
        if (!siteDir.exists()) siteDir.mkdirs();

        // Collect all devlog .md files sorted newest first
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

    private void generateIndexPage(List<File> devlogs, File siteDir) throws Exception {
        StringBuilder entries = new StringBuilder();

        for (File f : devlogs) {
            String name = f.getName().replace(".md", "");
            String displayName = name.startsWith("week-")
                    ? "📅 Week of " + name.replace("week-", "")
                    : "📝 " + name;
            String href = name + ".html";

            // Extract first line (summary preview) from file
            String preview = getFirstLine(f);

            entries.append("""
                <a href="%s" class="card">
                  <div class="card-title">%s</div>
                  <div class="card-preview">%s</div>
                </a>
                """.formatted(href, displayName, preview));
        }

        if (devlogs.isEmpty()) {
            entries.append("""
                <div class="empty">
                  <p>No devlogs yet. Run <code>devlog-ai generate</code> to create your first one!</p>
                </div>
                """);
        }

        String html = BASE_HTML.formatted(
                "DevLog AI — ForkAndFire",
                SITE_STYLES,
                """
                <div class="hero">
                  <h1>🔥 DevLog AI</h1>
                  <p class="subtitle">Open Source Standup for Solo Developers</p>
                  <p class="badge">by <strong>ForkAndFire</strong> · sid0x03</p>
                  <a href="https://github.com/goswamiSiddharth/devlog-ai" class="github-btn">
                    ⭐ Star on GitHub
                  </a>
                </div>
                <div class="section-title">📋 All DevLogs</div>
                <div class="grid">
                %s
                </div>
                """.formatted(entries.toString())
        );

        writeFile(new File(siteDir, "index.html"), html);
    }

    private void generateDevlogPage(File mdFile, File siteDir) throws Exception {
        String name    = mdFile.getName().replace(".md", "");
        String content = Files.readString(mdFile.toPath());
        String htmlContent = markdownToHtml(content);

        String html = BASE_HTML.formatted(
                name + " — DevLog AI",
                SITE_STYLES,
                """
                <div class="back-bar">
                  <a href="index.html">← All DevLogs</a>
                </div>
                <div class="devlog-content">
                %s
                </div>
                """.formatted(htmlContent)
        );

        writeFile(new File(siteDir, name + ".html"), html);
    }

    /** Very basic Markdown → HTML conversion for devlog pages */
    private String markdownToHtml(String md) {
        StringBuilder html = new StringBuilder();
        String[] lines = md.split("\n");

        for (String line : lines) {
            if (line.startsWith("# "))       html.append("<h1>").append(esc(line.substring(2))).append("</h1>\n");
            else if (line.startsWith("## ")) html.append("<h2>").append(esc(line.substring(3))).append("</h2>\n");
            else if (line.startsWith("### "))html.append("<h3>").append(esc(line.substring(4))).append("</h3>\n");
            else if (line.startsWith("> "))  html.append("<blockquote>").append(esc(line.substring(2))).append("</blockquote>\n");
            else if (line.startsWith("- "))  html.append("<li>").append(esc(line.substring(2))).append("</li>\n");
            else if (line.startsWith("---")) html.append("<hr/>\n");
            else if (line.startsWith("|"))   html.append(tableRow(line)).append("\n");
            else if (line.isBlank())         html.append("<br/>\n");
            else                             html.append("<p>").append(esc(line)).append("</p>\n");
        }
        return html.toString();
    }

    private String tableRow(String line) {
        if (line.replaceAll("[|\\-\\s]", "").isEmpty()) return "<tr></tr>";
        String[] cells = line.split("\\|");
        StringBuilder row = new StringBuilder("<tr>");
        for (String cell : cells) {
            if (!cell.isBlank()) row.append("<td>").append(esc(cell.trim())).append("</td>");
        }
        row.append("</tr>");
        return row.toString();
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
                if (!line.isEmpty() && !line.startsWith("#") && !line.startsWith(">") && !line.startsWith("---")) {
                    return line.length() > 120 ? line.substring(0, 120) + "..." : line;
                }
            }
        }
        return "No preview available.";
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
          <div class="container">
            %s
          </div>
          <footer>
            <p>🔥 DevLog AI · Open Source · by
              <a href="https://github.com/goswamiSiddharth">goswamiSiddharth</a> ·
              <a href="https://github.com/goswamiSiddharth/devlog-ai">GitHub</a>
            </p>
          </footer>
        </body>
        </html>
        """;

    private static final String SITE_STYLES = """
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
          font-family: 'Segoe UI', system-ui, sans-serif;
          background: #0d1117;
          color: #e6edf3;
          min-height: 100vh;
        }
        .container { max-width: 900px; margin: 0 auto; padding: 40px 20px; }
        .hero {
          text-align: center;
          padding: 60px 20px 40px;
          border-bottom: 1px solid #21262d;
          margin-bottom: 40px;
        }
        .hero h1 { font-size: 3rem; margin-bottom: 12px; }
        .subtitle { color: #8b949e; font-size: 1.2rem; margin-bottom: 8px; }
        .badge { color: #f78166; margin-bottom: 24px; }
        .github-btn {
          display: inline-block;
          background: #238636;
          color: white;
          text-decoration: none;
          padding: 10px 24px;
          border-radius: 6px;
          font-weight: 600;
          transition: background 0.2s;
        }
        .github-btn:hover { background: #2ea043; }
        .section-title {
          font-size: 1.3rem;
          font-weight: 700;
          margin-bottom: 20px;
          color: #f0883e;
        }
        .grid { display: grid; gap: 16px; }
        .card {
          background: #161b22;
          border: 1px solid #21262d;
          border-radius: 10px;
          padding: 20px 24px;
          text-decoration: none;
          color: #e6edf3;
          transition: border-color 0.2s, transform 0.15s;
          display: block;
        }
        .card:hover { border-color: #f78166; transform: translateY(-2px); }
        .card-title { font-size: 1.1rem; font-weight: 600; margin-bottom: 6px; }
        .card-preview { color: #8b949e; font-size: 0.9rem; }
        .empty { text-align: center; color: #8b949e; padding: 60px; }
        .empty code {
          background: #21262d; padding: 2px 8px; border-radius: 4px; color: #f0883e;
        }
        .back-bar { margin-bottom: 24px; }
        .back-bar a { color: #58a6ff; text-decoration: none; }
        .back-bar a:hover { text-decoration: underline; }
        .devlog-content h1 { font-size: 2rem; margin: 24px 0 12px; color: #f0883e; }
        .devlog-content h2 { font-size: 1.4rem; margin: 20px 0 10px; color: #58a6ff; }
        .devlog-content h3 { font-size: 1.1rem; margin: 16px 0 8px; }
        .devlog-content p  { line-height: 1.7; margin-bottom: 10px; color: #c9d1d9; }
        .devlog-content li { margin: 6px 0 6px 20px; color: #c9d1d9; line-height: 1.6; }
        .devlog-content code {
          background: #21262d; padding: 2px 6px; border-radius: 4px; color: #f0883e;
        }
        .devlog-content blockquote {
          border-left: 3px solid #f78166;
          padding: 8px 16px;
          margin: 12px 0;
          color: #8b949e;
          background: #161b22;
          border-radius: 0 6px 6px 0;
        }
        .devlog-content hr { border: none; border-top: 1px solid #21262d; margin: 24px 0; }
        .devlog-content tr { border-bottom: 1px solid #21262d; }
        .devlog-content td { padding: 8px 12px; }
        .devlog-content strong { color: #f0883e; font-weight: 700; }
        footer {
          text-align: center;
          padding: 32px;
          color: #8b949e;
          font-size: 0.85rem;
          border-top: 1px solid #21262d;
          margin-top: 60px;
        }
        footer a { color: #58a6ff; text-decoration: none; }
        """;
}
