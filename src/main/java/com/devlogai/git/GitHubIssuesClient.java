package com.devlogai.git;

import com.devlogai.model.GitHubIssue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches open GitHub Issues from a public repository.
 * Uses GitHub's public REST API — no token required for public repos.
 *
 * API: GET https://api.github.com/repos/{owner}/{repo}/issues?state=open
 */
public class GitHubIssuesClient {

    private static final String GITHUB_API = "https://api.github.com/repos/%s/%s/issues?state=open&per_page=20";
    private final String owner;
    private final String repo;

    public GitHubIssuesClient(String owner, String repo) {
        this.owner = owner;
        this.repo  = repo;
    }

    /**
     * Fetch open issues. Returns empty list if API fails (graceful degradation).
     */
    public List<GitHubIssue> fetchOpenIssues() {
        List<GitHubIssue> issues = new ArrayList<>();
        try {
            String apiUrl = GITHUB_API.formatted(owner, repo);
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github+json");
            conn.setRequestProperty("User-Agent", "DevLog-AI/1.0");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            if (conn.getResponseCode() != 200) {
                System.out.println("⚠️  GitHub API returned " + conn.getResponseCode() + " — skipping issues.");
                return issues;
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) response.append(line);
            }

            issues = parseIssues(response.toString());
            System.out.println("✅ Fetched " + issues.size() + " open issue(s) from GitHub");

        } catch (Exception e) {
            System.out.println("⚠️  Could not fetch GitHub issues: " + e.getMessage());
        }
        return issues;
    }

    /**
     * Simple JSON parser — no external library needed.
     * Parses array of issue objects extracting number, title, labels, created_at.
     */
    private List<GitHubIssue> parseIssues(String json) {
        List<GitHubIssue> issues = new ArrayList<>();

        // Split by issue objects — each starts with {"url":
        String[] objects = json.split("\\{\"url\":");
        for (int i = 1; i < objects.length; i++) {
            String obj = objects[i];
            try {
                // Skip pull requests (they also appear in issues API)
                if (obj.contains("\"pull_request\"")) continue;

                int number      = extractInt(obj, "\"number\":");
                String title    = extractString(obj, "\"title\":");
                String state    = extractString(obj, "\"state\":");
                String created  = extractString(obj, "\"created_at\":");
                String label    = extractFirstLabel(obj);

                if (number > 0 && title != null && "open".equals(state)) {
                    // Format date: 2026-06-01T12:00:00Z → 2026-06-01
                    String date = created != null && created.length() >= 10
                            ? created.substring(0, 10) : "unknown";
                    issues.add(new GitHubIssue(number, title, label, date, owner, repo));
                }
            } catch (Exception ignored) {}
        }
        return issues;
    }

    private int extractInt(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return -1;
        int start = idx + key.length();
        StringBuilder num = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isDigit(c)) num.append(c);
            else if (num.length() > 0) break;
        }
        return num.isEmpty() ? -1 : Integer.parseInt(num.toString());
    }

    private String extractString(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return null;
        int start = json.indexOf('"', idx + key.length());
        if (start == -1) return null;
        start++;
        StringBuilder val = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) break;
            val.append(c);
        }
        return val.toString();
    }

    private String extractFirstLabel(String json) {
        int labelsIdx = json.indexOf("\"labels\":[");
        if (labelsIdx == -1) return "none";
        int nameIdx = json.indexOf("\"name\":", labelsIdx);
        if (nameIdx == -1) return "none";
        String name = extractString(json.substring(nameIdx), "\"name\":");
        return name != null ? name : "none";
    }
}
