package com.devlogai.git;

import com.devlogai.model.Commit;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads git log from the current repository using git CLI commands.
 * Parses commit hash, message, author, date, and diff stats.
 */
public class GitLogReader {

    private final String repoPath;
    private static final DateTimeFormatter GIT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public GitLogReader(String repoPath) {
        this.repoPath = repoPath;
    }

    /**
     * Returns list of commits between from and to dates (inclusive).
     */
    public List<Commit> getCommits(LocalDate from, LocalDate to) throws Exception {
        List<Commit> commits = new ArrayList<>();
       
            String logCommand = String.format(
            "git log --after=\"%s\" --before=\"%s\" --format=%%H|%%an|%%cs|%%s --shortstat",
            from.minusDays(1).toString(),
            to.plusDays(1).toString()
        );

        // Use appropriate shell based on OS
        ProcessBuilder pb;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd.exe", "/c", logCommand);
        } else {
            pb = new ProcessBuilder("bash", "-c", logCommand);
        }
        pb.directory(new File(repoPath));
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        String currentMeta = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains("|")) {
                // This is a commit metadata line
                currentMeta = line;
            } else if (currentMeta != null && line.contains("changed")) {
                // This is the --shortstat line: "2 files changed, 45 insertions(+), 3 deletions(-)"
                Commit commit = parseCommit(currentMeta, line);
                if (commit != null) commits.add(commit);
                currentMeta = null;
            } else if (currentMeta != null) {
                // Commit with no stat changes (e.g., merge commits)
                Commit commit = parseCommit(currentMeta, "");
                if (commit != null) commits.add(commit);
                currentMeta = null;
            }
        }

        // Handle last commit if no stat line followed
        if (currentMeta != null) {
            Commit commit = parseCommit(currentMeta, "");
            if (commit != null) commits.add(commit);
        }

        process.waitFor();
        return commits;
    }

    private Commit parseCommit(String meta, String stat) {
        try {
            String[] parts = meta.split("\\|", 4);
            if (parts.length < 4) return null;

            String hash    = parts[0].trim();
            String author  = parts[1].trim();
            LocalDate date = LocalDate.parse(parts[2].trim(), GIT_DATE_FORMAT);
            String message = parts[3].trim();

            int filesChanged = 0, insertions = 0, deletions = 0;

            if (!stat.isEmpty()) {
                // Parse: "2 files changed, 45 insertions(+), 3 deletions(-)"
                if (stat.contains("file")) {
                    filesChanged = extractNumber(stat, "file");
                }
                if (stat.contains("insertion")) {
                    insertions = extractNumber(stat, "insertion");
                }
                if (stat.contains("deletion")) {
                    deletions = extractNumber(stat, "deletion");
                }
            }

            return new Commit(hash, message, author, date, filesChanged, insertions, deletions);

        } catch (Exception e) {
            System.err.println("⚠️  Could not parse commit: " + meta + " | Error: " + e.getMessage());
            return null;
        }
    }

    private int extractNumber(String text, String keyword) {
        try {
            String[] tokens = text.split(" ");
            for (int i = 0; i < tokens.length - 1; i++) {
                if (tokens[i + 1].startsWith(keyword)) {
                    return Integer.parseInt(tokens[i].replaceAll("[^0-9]", ""));
                }
            }
        } catch (NumberFormatException ignored) {}
        return 0;
    }

    /**
     * Returns the current git repo's remote URL (for linking in devlog)
     */
    public String getRemoteUrl() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("git", "remote", "get-url", "origin");
        pb.directory(new File(repoPath));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String url = new BufferedReader(new InputStreamReader(p.getInputStream()))
                .readLine();
        p.waitFor();
        return url != null ? url.trim() : "";
    }

    /**
     * Returns current branch name
     */
    public String getCurrentBranch() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("git", "branch", "--show-current");
        pb.directory(new File(repoPath));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String branch = new BufferedReader(new InputStreamReader(p.getInputStream()))
                .readLine();
        p.waitFor();
        return branch != null ? branch.trim() : "main";
    }
}
