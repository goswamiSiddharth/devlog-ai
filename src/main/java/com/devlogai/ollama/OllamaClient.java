package com.devlogai.ollama;

import com.devlogai.model.Commit;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Client for Ollama local LLM API.
 * Ollama runs on http://localhost:11434 by default.
 *
 * Prerequisites:
 *   1. Install Ollama: https://ollama.ai
 *   2. Pull a model: ollama pull llama3
 *   3. Run: ollama serve
 */
public class OllamaClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String DEFAULT_MODEL = "llama3";

    private final String model;

    public OllamaClient() {
        this.model = System.getenv().getOrDefault("DEVLOG_MODEL", DEFAULT_MODEL);
    }

    public OllamaClient(String model) {
        this.model = model;
    }

    /**
     * Sends commits to Ollama and returns a human-readable summary
     */
    public String summarizeCommits(List<Commit> commits) throws Exception {
        String prompt = buildPrompt(commits);

        String requestBody = """
            {
                "model": "%s",
                "prompt": %s,
                "stream": false,
                "options": {
                    "temperature": 0.4,
                    "num_predict": 500
                }
            }
            """.formatted(model, toJsonString(prompt));

        HttpURLConnection conn = (HttpURLConnection) new URL(OLLAMA_URL).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout(120_000); // LLMs can be slow

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            System.err.println("⚠️  Ollama returned status " + status);
            return fallbackSummary(commits);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        return extractResponse(response.toString());
    }

    private String buildPrompt(List<Commit> commits) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a developer assistant. Below are git commit messages from today.\n");
        sb.append("Write a short, clear, human-friendly developer standup summary (3-5 bullet points).\n");
        sb.append("Focus on WHAT was built, not just the commit message wording.\n");
        sb.append("Be concise. Use plain English. No technical jargon unless necessary.\n\n");
        sb.append("Commits:\n");

        for (Commit c : commits) {
            sb.append("- [").append(c.getShortHash()).append("] ")
              .append(c.getMessage());
            if (c.getFilesChanged() > 0) {
                sb.append(" (").append(c.getFilesChanged()).append(" files, +")
                  .append(c.getInsertions()).append("/-").append(c.getDeletions()).append(")");
            }
            sb.append("\n");
        }

        sb.append("\nGenerate a developer standup summary:");
        return sb.toString();
    }

    private String extractResponse(String json) {
        // Simple JSON parsing — extract "response" field value
        int start = json.indexOf("\"response\":\"");
        if (start == -1) return fallbackFromJson(json);
        start += 12;
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                switch (c) {
                    case 'n' -> result.append('\n');
                    case 't' -> result.append('\t');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    default  -> result.append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                break;
            } else {
                result.append(c);
            }
        }
        return result.toString().trim();
    }

    private String fallbackFromJson(String json) {
        // If parsing fails, return a clean message
        return "⚠️ Could not parse Ollama response. Raw: " + json.substring(0, Math.min(200, json.length()));
    }

    /**
     * Fallback if Ollama is not running — generates a basic summary from commits
     */
    private String fallbackSummary(List<Commit> commits) {
        System.out.println("⚠️  Ollama not reachable. Generating basic summary without AI.");
        StringBuilder sb = new StringBuilder();
        sb.append("*Note: Ollama not available. Basic summary generated.*\n\n");
        for (Commit c : commits) {
            sb.append("- ").append(c.getMessage()).append("\n");
        }
        return sb.toString();
    }

    private String toJsonString(String text) {
        // Escape the prompt for JSON
        return "\"" + text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }

    /**
     * Check if Ollama is running
     */
    public boolean isAvailable() {
        try {
            HttpURLConnection conn = (HttpURLConnection)
                    new URL("http://localhost:11434").openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
