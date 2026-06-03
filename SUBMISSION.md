# 🔥 DevLog AI — Hackathon Submission

**Team:** ForkAndFire  
**Author:** Siddharth Goswami (sid0x03)  
**GitHub:** [goswamiSiddharth/devlog-ai](https://github.com/goswamiSiddharth/devlog-ai)  
**Live Site:** [goswamisiddharth.github.io/devlog-ai](https://goswamisiddharth.github.io/devlog-ai)

---

## 🧠 Problem Statement

Every solo developer and open source contributor faces the same problem — they forget what they built. Team standup tools exist for organizations, but **nothing exists for solo developers and open source contributors**.

DevLog AI solves this by automatically generating a human-readable progress log from your git commits using a local AI model — completely free, completely open source.

---

## ✅ What We Built

**DevLog AI** — A Java CLI tool that:

1. Reads your git commits from any repository
2. Sends them to **Ollama** (local LLM — no API key, no cost)
3. Generates a human-readable AI standup summary
4. Outputs a beautiful **Markdown devlog**
5. Builds a **static website** auto-deployed to GitHub Pages
6. Fetches **open GitHub Issues** to track pending work
7. Sets up any project in one command with `devlog init`

---

## 🚀 Key Features

| Feature | Description |
|---------|-------------|
| `generate` | Generate today's devlog from git commits |
| `generate --week` | Weekly devlog for last 7 days |
| `generate --tag feat` | Filter by conventional commit type |
| `generate --issues owner/repo` | Include open GitHub Issues |
| `devlog init` | One-shot setup for any git project |
| `site` | Rebuild static site from existing devlogs |

---

## 🛠️ Tech Stack

| Technology | Usage |
|-----------|-------|
| Java 21 | Core CLI application |
| Maven | Build system |
| Ollama (llama3) | Local AI summarization |
| GitHub REST API | Fetches open issues (no token needed) |
| HTML/CSS | Static site generation |
| GitHub Actions | Auto-deploy to GitHub Pages |

---

## 📁 Project Structure

```
devlog-ai/
├── src/main/java/com/devlogai/
│   ├── cli/          # CLI commands (generate, init, site)
│   ├── git/          # Git log reader + GitHub Issues client
│   ├── ollama/       # Local LLM integration
│   ├── generator/    # Markdown + Static site generators
│   └── model/        # Data models
├── community/prompts/ # Community prompt library
├── devlogs/          # Generated devlogs
├── docs/             # GitHub Pages static site
├── CHANGELOG.md
├── CONTRIBUTING.md
└── README.md
```

---

## 🌟 What Makes This Special

- **Zero cost** — Ollama runs 100% locally, no API keys
- **Zero dependencies** — Pure Java, no external libraries
- **Works on any project** — `devlog init` sets up any git repo
- **Community-first** — Prompt library, issue templates, CONTRIBUTING.md
- **Self-documenting** — DevLog AI documents its own development!
- **Scalable** — Easy to extend (VS Code extension, email digest, GitLab support planned)

---

## 📊 Project Stats

- **7 days** of active development
- **Java source files:** 8
- **Commands:** 4 (generate, init, site, help)
- **Flags:** 4 (--week, --tag, --since, --issues)
- **Community prompts:** 8
- **Live site:** Deployed on GitHub Pages

---

## 🔗 Links

- **GitHub Repo:** https://github.com/goswamiSiddharth/devlog-ai
- **Live Site:** https://goswamisiddharth.github.io/devlog-ai
- **Devlogs:** https://github.com/goswamiSiddharth/devlog-ai/tree/main/devlogs
- **Community Prompts:** https://github.com/goswamiSiddharth/devlog-ai/tree/main/community/prompts

---

## 🗺️ Future Roadmap

- [ ] VS Code extension
- [ ] Brew / npm global install
- [ ] Weekly email digest
- [ ] GitLab / Bitbucket support
- [ ] `--tag` multi-filter support

---

*Built with ❤️ by **ForkAndFire** · Open Source Hackathon 2026 · Elite Coders*
