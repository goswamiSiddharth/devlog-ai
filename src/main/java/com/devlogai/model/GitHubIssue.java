package com.devlogai.model;

/**
 * Represents a single GitHub Issue
 */
public class GitHubIssue {
    private final int number;
    private final String title;
    private final String label;
    private final String createdDate;
    private final String owner;
    private final String repo;

    public GitHubIssue(int number, String title, String label,
                       String createdDate, String owner, String repo) {
        this.number      = number;
        this.title       = title;
        this.label       = label;
        this.createdDate = createdDate;
        this.owner       = owner;
        this.repo        = repo;
    }

    public int    getNumber()      { return number; }
    public String getTitle()       { return title; }
    public String getLabel()       { return label; }
    public String getCreatedDate() { return createdDate; }
    public String getUrl()         {
        return "https://github.com/" + owner + "/" + repo + "/issues/" + number;
    }

    @Override
    public String toString() {
        return "#" + number + " [" + label + "] " + title;
    }
}
