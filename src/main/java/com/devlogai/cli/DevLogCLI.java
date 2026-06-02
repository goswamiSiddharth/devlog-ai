package com.devlogai.cli;

import com.devlogai.git.GitLogReader;
import com.devlogai.model.Commit;
import com.devlogai.ollama.OllamaClient;
import com.devlogai.generator.MarkdownGenerator;
import com.devlogai.generator.StaticSiteGenerator;

import java.time.LocalDate;
import java.util.List;

/**
 * DevLog AI - CLI Entry Point
 * Team: ForkAndFire | Author: sid0x03
 *
 * Usage:
 *   java -jar devlog-ai.jar generate                        → today's devlog
 *   java -jar devlog-ai.jar generate --week                 → weekly devlog
 *   java -jar devlog-ai.jar generate --since 2026-05-25     → from specific date
 *   java -jar devlog-ai.jar generate --tag feat             → only 'feat' commits
 *   java -jar devlog-ai.jar generate --tag fix --week       → only 'fix' commits this week
 *   java -jar devlog-ai.jar site                            → regenerate static site
 */
public class DevLogCLI {

    public static void main(String[] args) throws Exception {
        printBanner();

        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0].toLowerCase();

        switch (command) {
            case "generate" -> handleGenerate(args);
            case "site"     -> handleSite();
            case "help"     -> printHelp();
            default         -> {
                System.out.println("❌ Unknown command: " + command);
                printHelp();
            }
        }
    }

    private static void handleGenerate(String[] args) throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to   = LocalDate.now();
        String mode    = "daily";
        String tag     = null; // null means no filter

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--week"  -> { from = LocalDate.now().minusDays(7); mode = "weekly"; }
                case "--since" -> { if (i + 1 < args.length) from = LocalDate.parse(args[++i]); }
                case "--tag"   -> { if (i + 1 < args.length) tag = args[++i].toLowerCase(); }
            }
        }

        System.out.println("📂 Reading git log from: " + from + " → " + to);
        if (tag != null) System.out.println("🏷️  Filtering by tag: " + tag);

        GitLogReader reader = new GitLogReader(".");
        List<Commit> commits = reader.getCommits(from, to);

        // Apply --tag filter if provided
        if (tag != null) {
            final String tagFilter = tag;
            commits = commits.stream()
                .filter(c -> c.getMessage().toLowerCase().startsWith(tagFilter))
                .toList();
        }

        if (commits.isEmpty()) {
            if (tag != null) {
                System.out.println("⚠️  No commits found with tag '" + tag + "' in this range.");
                System.out.println("💡 Try: generate --tag feat | fix | docs | refactor | chore");
            } else {
                System.out.println("⚠️  No commits found in this range. Make a commit first!");
            }
            return;
        }

        System.out.println("✅ Found " + commits.size() + " commit(s)");
        System.out.println("🤖 Sending to Ollama for summarization...");

        OllamaClient ollama = new OllamaClient();
        String summary = ollama.summarizeCommits(commits);

        System.out.println("📝 Generating Markdown devlog...");
        MarkdownGenerator md = new MarkdownGenerator();
        String filePath = md.generate(commits, summary, from, to, mode, tag);
        System.out.println("✅ Devlog saved → " + filePath);

        System.out.println("🌐 Regenerating static site...");
        StaticSiteGenerator site = new StaticSiteGenerator();
        site.regenerate();
        System.out.println("✅ Site updated → docs/index.html");
        System.out.println("\n🔥 ForkAndFire | DevLog AI — Done!");
    }

    private static void handleSite() throws Exception {
        System.out.println("🌐 Regenerating static site from existing devlogs...");
        StaticSiteGenerator site = new StaticSiteGenerator();
        site.regenerate();
        System.out.println("✅ Site updated → docs/index.html");
    }

    private static void printBanner() {
        System.out.println("""
            ╔═══════════════════════════════════════════╗
            ║   🔥 DevLog AI — by ForkAndFire           ║
            ║   Open Source Standup for Solo Devs       ║
            ║   Powered by Ollama (local LLM)           ║
            ╚═══════════════════════════════════════════╝
            """);
    }

    private static void printHelp() {
        System.out.println("""
            📖 Usage:
              generate                        → Generate today's devlog
              generate --week                 → Generate last 7 days devlog
              generate --since YYYY-MM-DD     → Generate from a specific date
              generate --tag <keyword>        → Filter commits by type
              generate --tag feat --week      → Combine filters!
              site                            → Rebuild static site only
              help                            → Show this help

            🏷️  Tag examples (conventional commits):
              --tag feat      → only feature commits
              --tag fix       → only bug fix commits
              --tag docs      → only documentation commits
              --tag refactor  → only refactor commits
              --tag chore     → only maintenance commits

            💡 Examples:
              java -jar devlog-ai.jar generate
              java -jar devlog-ai.jar generate --week
              java -jar devlog-ai.jar generate --tag feat
              java -jar devlog-ai.jar generate --tag fix --week
              java -jar devlog-ai.jar generate --since 2026-06-01
            """);
    }
}
