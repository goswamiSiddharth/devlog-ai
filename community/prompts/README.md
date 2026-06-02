# 🔥 DevLog AI — Community Prompts Library

A collection of community-contributed prompts for customizing how Ollama summarizes your devlogs.

> **How to use:** Copy any prompt below and set it as your custom prompt in `OllamaClient.java` or contribute your own via PR!

---

## 📋 Default Prompt (built-in)

```
You are a developer assistant. Below are git commit messages from today.
Write a short, clear, human-friendly developer standup summary (3-5 bullet points).
Focus on WHAT was built, not just the commit message wording.
Be concise. Use plain English. No technical jargon unless necessary.
```

**Best for:** General daily standups, solo developers

---

## 🚀 Prompts by Style

### 1. Tweet-Style Summary
```
Summarize these git commits as if writing a tweet thread (max 3 tweets, each under 280 chars).
Start each with a number. Be punchy and exciting. Focus on impact, not implementation.
Commits:
```
**Best for:** Public build-in-public posts, social media updates

---

### 2. Technical Deep Dive
```
You are a senior software engineer writing a technical progress report.
Summarize these commits with technical precision. Include:
- What was implemented
- Key technical decisions made
- Potential risks or follow-up needed
Use bullet points. Be specific about technologies used.
Commits:
```
**Best for:** Team standups, technical documentation

---

### 3. Manager-Friendly Summary
```
Summarize these git commits for a non-technical manager.
Avoid jargon. Focus on business value and progress.
Format: 2-3 sentences max. Start with "Today we..."
Commits:
```
**Best for:** Reporting to stakeholders, sprint reviews

---

### 4. Open Source Contributor Log
```
Write a contributor update for an open source project.
Format it like a GitHub discussion post. Include:
- What was fixed or added
- How it helps the community
- What's coming next (based on context)
Use markdown formatting with emojis.
Commits:
```
**Best for:** Open source maintainers, ECSoC participants

---

### 5. Learning Journal
```
I am a developer learning by building. Summarize these commits as a learning journal entry.
Include:
- What I built today
- What I learned
- What was challenging
- What I want to explore next
Write in first person, casual and reflective tone.
Commits:
```
**Best for:** Students, bootcamp participants, junior developers

---

### 6. Weekly Retrospective
```
Write a weekly retrospective based on these commits. Include:
- 🟢 What went well
- 🟡 What could be improved
- 🔴 Blockers or challenges
- 📅 Goals for next week
Keep each section to 2-3 bullet points.
Commits:
```
**Best for:** Weekly devlogs (use with --week flag)

---

### 7. Changelog Entry
```
Generate a changelog entry in Keep a Changelog format (keepachangelog.com).
Categorize commits into: Added, Changed, Fixed, Removed, Security.
Only include categories that have relevant commits.
Format each item as a concise single line.
Commits:
```
**Best for:** Release notes, versioned projects

---

### 8. Minimal One-Liner
```
Summarize ALL of these commits in exactly ONE sentence under 100 characters.
Be direct. No fluff.
Commits:
```
**Best for:** Commit descriptions, quick status updates

---

## 🤝 Contributing Your Prompt

Have a great prompt? Submit a PR!

1. Fork the repo
2. Add your prompt to this file following the format above
3. Include: prompt name, the prompt text, and **Best for** description
4. Submit a PR with title: `community: add [name] prompt`

---

## 🔧 How to Use Custom Prompts

In `OllamaClient.java`, find `buildPrompt()` and replace the default prompt:

```java
private String buildPrompt(List<Commit> commits) {
    StringBuilder sb = new StringBuilder();
    
    // Replace this section with your custom prompt:
    sb.append("YOUR CUSTOM PROMPT HERE\n\n");
    sb.append("Commits:\n");
    
    for (Commit c : commits) {
        sb.append("- [").append(c.getShortHash()).append("] ")
          .append(c.getMessage()).append("\n");
    }
    return sb.toString();
}
```

---

*Community Prompts Library — maintained by ForkAndFire & contributors 🔥*
