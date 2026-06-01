package com.devlogai.cli;

import com.devlogai.git.GitLogReader;
import com.devlogai.model.Commit;
import com.devlogai.ollama.OllamaClient;
import com.devlogai.generator.MarkdownGenerator;
import com.devlogai.generator.StaticSiteGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * DevLog AI - CLI Entry Point
 * Team: ForkAndFire | Author: sid0x03
 *
 * Usage:
 *   java -jar devlog-ai.jar generate          → today's devlog
 *   java -jar devlog-ai.jar generate --week   → weekly devlog
 *   java -jar devlog-ai.jar generate --since 2026-05-25
 *   java -jar devlog-ai.jar site              → regenerate static site
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

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--week"  -> { from = LocalDate.now().minusDays(7); mode = "weekly"; }
                case "--since" -> { if (i + 1 < args.length) from = LocalDate.parse(args[++i]); }
            }
        }

        System.out.println("📂 Reading git log from: " + from + " → " + to);
        GitLogReader reader = new GitLogReader(".");
        List<Commit> commits = reader.getCommits(from, to);

        if (commits.isEmpty()) {
            System.out.println("⚠️  No commits found in this range. Make a commit first!");
            return;
        }

        System.out.println("✅ Found " + commits.size() + " commit(s)");
        System.out.println("🤖 Sending to Ollama for summarization...");

        OllamaClient ollama = new OllamaClient();
        String summary = ollama.summarizeCommits(commits);

        System.out.println("📝 Generating Markdown devlog...");
        MarkdownGenerator md = new MarkdownGenerator();
        String filePath = md.generate(commits, summary, from, to, mode);
        System.out.println("✅ Devlog saved → " + filePath);

        System.out.println("🌐 Regenerating static site...");
        StaticSiteGenerator site = new StaticSiteGenerator();
        site.regenerate();
        System.out.println("✅ Site updated → docs/site/index.html");
        System.out.println("\n🔥 ForkAndFire | DevLog AI — Done!");
    }

    private static void handleSite() throws Exception {
        System.out.println("🌐 Regenerating static site from existing devlogs...");
        StaticSiteGenerator site = new StaticSiteGenerator();
        site.regenerate();
        System.out.println("✅ Site updated → docs/site/index.html");
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
              generate            → Generate today's devlog
              generate --week     → Generate last 7 days devlog
              generate --since YYYY-MM-DD → Generate from a specific date
              site                → Rebuild static site only
              help                → Show this help

            💡 Examples:
              java -jar devlog-ai.jar generate
              java -jar devlog-ai.jar generate --week
              java -jar devlog-ai.jar generate --since 2026-06-01
            """);
    }
}
