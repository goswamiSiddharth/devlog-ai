package com.devlogai.cli;

import com.devlogai.git.GitLogReader;
import com.devlogai.git.GitHubIssuesClient;
import com.devlogai.model.Commit;
import com.devlogai.model.GitHubIssue;
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
 *   java -jar devlog-ai.jar generate                          → today's devlog
 *   java -jar devlog-ai.jar generate --week                   → weekly devlog
 *   java -jar devlog-ai.jar generate --since 2026-05-25       → from specific date
 *   java -jar devlog-ai.jar generate --tag feat               → only 'feat' commits
 *   java -jar devlog-ai.jar generate --issues owner/repo      → include GitHub issues
 *   java -jar devlog-ai.jar generate --tag fix --week         → only 'fix' commits this week
 *   java -jar devlog-ai.jar generate --tag fix --issues owner/repo → combine!
 *   java -jar devlog-ai.jar site                              → regenerate static site
 */
public class DevLogCLI {

    public static void main(String[] args) throws Exception {
        printBanner();

        if (args.length == 0) { printHelp(); return; }

        switch (args[0].toLowerCase()) {
            case "generate" -> handleGenerate(args);
            case "site"     -> handleSite();
            case "help"     -> printHelp();
            default         -> { System.out.println("❌ Unknown command: " + args[0]); printHelp(); }
        }
    }

    private static void handleGenerate(String[] args) throws Exception {
        LocalDate from   = LocalDate.now();
        LocalDate to     = LocalDate.now();
        String mode      = "daily";
        String tag       = null;
        String issueRepo = null; // format: "owner/repo"

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--week"   -> { from = LocalDate.now().minusDays(7); mode = "weekly"; }
                case "--since"  -> { if (i + 1 < args.length) from = LocalDate.parse(args[++i]); }
                case "--tag"    -> { if (i + 1 < args.length) tag = args[++i].toLowerCase(); }
                case "--issues" -> { if (i + 1 < args.length) issueRepo = args[++i]; }
            }
        }

        System.out.println("📂 Reading git log from: " + from + " → " + to);
        if (tag != null)       System.out.println("🏷️  Filtering by tag: " + tag);
        if (issueRepo != null) System.out.println("🐛 Fetching issues from: " + issueRepo);

        // 1. Get commits
        GitLogReader reader = new GitLogReader(".");
        List<Commit> commits = reader.getCommits(from, to);

        if (tag != null) {
            final String t = tag;
            commits = commits.stream()
                .filter(c -> c.getMessage().toLowerCase().startsWith(t))
                .toList();
        }

        if (commits.isEmpty()) {
            System.out.println(tag != null
                ? "⚠️  No commits found with tag '" + tag + "'. Try: feat | fix | docs | refactor"
                : "⚠️  No commits found in this range. Make a commit first!");
            return;
        }
        System.out.println("✅ Found " + commits.size() + " commit(s)");

        // 2. Fetch GitHub Issues (optional)
        List<GitHubIssue> issues = List.of();
        if (issueRepo != null && issueRepo.contains("/")) {
            String[] parts = issueRepo.split("/");
            GitHubIssuesClient issuesClient = new GitHubIssuesClient(parts[0], parts[1]);
            issues = issuesClient.fetchOpenIssues();
        }

        // 3. AI summarization
        System.out.println("🤖 Sending to Ollama for summarization...");
        OllamaClient ollama = new OllamaClient();
        String summary = ollama.summarizeCommits(commits);

        // 4. Generate markdown
        System.out.println("📝 Generating Markdown devlog...");
        MarkdownGenerator md = new MarkdownGenerator();
        String filePath = md.generate(commits, summary, from, to, mode, tag, issues);
        System.out.println("✅ Devlog saved → " + filePath);

        // 5. Regenerate site
        System.out.println("🌐 Regenerating static site...");
        new StaticSiteGenerator().regenerate();
        System.out.println("✅ Site updated → docs/index.html");
        System.out.println("\n🔥 ForkAndFire | DevLog AI — Done!");
    }

    private static void handleSite() throws Exception {
        System.out.println("🌐 Regenerating static site from existing devlogs...");
        new StaticSiteGenerator().regenerate();
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
              generate                            → Generate today's devlog
              generate --week                     → Last 7 days
              generate --since YYYY-MM-DD         → From specific date
              generate --tag <type>               → Filter by commit type
              generate --issues owner/repo        → Include GitHub issues
              generate --tag feat --issues o/r    → Combine filters!
              site                                → Rebuild static site only
              help                                → Show this help

            🏷️  Tag examples:
              --tag feat | fix | docs | refactor | chore | test

            🐛 Issues example:
              generate --issues goswamiSiddharth/devlog-ai

            💡 Full example:
              java -jar devlog-ai.jar generate --week --tag feat --issues goswamiSiddharth/devlog-ai
            """);
    }
}
