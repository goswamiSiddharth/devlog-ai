package com.devlogai.model;

import java.time.LocalDate;

/**
 * Represents a single Git commit
 */
public class Commit {
    private final String hash;
    private final String message;
    private final String author;
    private final LocalDate date;
    private final int filesChanged;
    private final int insertions;
    private final int deletions;

    public Commit(String hash, String message, String author, LocalDate date,
                  int filesChanged, int insertions, int deletions) {
        this.hash = hash;
        this.message = message;
        this.author = author;
        this.date = date;
        this.filesChanged = filesChanged;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public String getHash()        { return hash; }
    public String getMessage()     { return message; }
    public String getAuthor()      { return author; }
    public LocalDate getDate()     { return date; }
    public int getFilesChanged()   { return filesChanged; }
    public int getInsertions()     { return insertions; }
    public int getDeletions()      { return deletions; }

    public String getShortHash()   { return hash.length() >= 7 ? hash.substring(0, 7) : hash; }

    @Override
    public String toString() {
        return "[" + getShortHash() + "] " + message + " (" + date + ")";
    }
}
