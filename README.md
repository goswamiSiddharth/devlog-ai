# 🔥 DevLog AI

> **Open Source Standup & Progress Tracker for Solo Developers**
>
> Stop forgetting what you built. Let AI read your git commits and write your standup for you.

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java"/>
  <img src="https://img.shields.io/badge/Ollama-Local%20LLM-purple?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Open%20Source-%F0%9F%94%A5-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Team-ForkAndFire-red?style=for-the-badge"/>
  <img src="https://img.shields.io/github/license/goswamiSiddharth/devlog-ai?style=for-the-badge"/>
</p>

---

## 🧠 The Problem

Every solo developer and open source contributor faces the same issue:

- ❌ You forget what you worked on last week
- ❌ Writing standups / progress logs is tedious
- ❌ Your GitHub contributions graph exists but tells you *nothing* about what you actually built
- ❌ Team standup tools exist — but **nothing exists for solo devs**

## ✅ The Solution

**DevLog AI** is a Java CLI tool that:

1. **Reads your git commits** from any local repository
2. **Sends them to Ollama** (a free, local LLM — no API key, no cost, no cloud)
3. **Generates a human-readable standup summary** in plain English
4. **Outputs a beautiful Markdown devlog** saved in your repo
5. **Builds a static website** (deployable to GitHub Pages for free)

All 100% local. All 100% open source. Zero API costs.

---

## 🎬 Demo

```
╔═══════════════════════════════════════════╗
║   🔥 DevLog AI — by ForkAndFire           ║
║   Open Source Standup for Solo Devs       ║
║   Powered by Ollama (local LLM)           ║
╚═══════════════════════════════════════════╝

📂 Reading git log from: 2026-06-01 → 2026-06-01
✅ Found 4 commit(s)
🤖 Sending to Ollama for summarization...
📝 Generating Markdown devlog...
✅ Devlog saved → devlogs/2026-06-01.md
🌐 Regenerating static site...
✅ Site updated → docs/index.html

🔥 ForkAndFire | DevLog AI — Done!
```

**Generated devlog looks like this:**

> 📅 DevLog — June 01, 2026
>
> **AI Summary:**
> - Built the CLI entry point with command routing (generate, site, help)
> - Implemented git log reader that parses commit stats including insertions/deletions
> - Added Ollama integration for local AI summarization with graceful fallback
> - Created markdown and static site generators for beautiful output

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Java | 21+ | [adoptium.net](https://adoptium.net) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org) |
| Ollama | Latest | [ollama.ai](https://ollama.ai) |
| Git | Any | Already installed? |

### Installation

```bash
# 1. Clone the repo
git clone https://github.com/goswamiSiddharth/devlog-ai.git
cd devlog-ai

# 2. Build the JAR
mvn package

# 3. Install Ollama and pull a model (one time setup)
ollama pull llama3

# 4. Start Ollama (keep this running in background)
ollama serve
```

### Usage

```bash
# Generate today's devlog
java -jar target/devlog-ai.jar generate

# Generate weekly devlog (last 7 days)
java -jar target/devlog-ai.jar generate --week

# Generate from a specific date
java -jar target/devlog-ai.jar generate --since 2026-05-25

# Rebuild static site only (from existing devlogs)
java -jar target/devlog-ai.jar site

# Help
java -jar target/devlog-ai.jar help
```

---

## 📁 Project Structure

```
devlog-ai/
├── src/main/java/com/devlogai/
│   ├── cli/
│   │   └── DevLogCLI.java          # CLI entry point & command routing
│   ├── git/
│   │   └── GitLogReader.java       # Reads & parses git log output
│   ├── ollama/
│   │   └── OllamaClient.java       # Local LLM API client
│   ├── generator/
│   │   ├── MarkdownGenerator.java  # Generates .md devlog files
│   │   └── StaticSiteGenerator.java# Generates static HTML site
│   └── model/
│       └── Commit.java             # Commit data model
├── devlogs/                        # Your generated devlogs live here
├── docs/                          # Static site (GitHub Pages source)
├── .github/workflows/
│   └── deploy-site.yml             # Auto-deploy site on push
├── pom.xml
└── README.md
```

---

## 🌐 GitHub Pages Deployment

Once you push your devlogs:

1. Go to your repo → **Settings → Pages**
2. Source: **Deploy from branch**
3. Branch: `main` / Folder: `/docs`
4. Your devlog site is live at: `https://goswamiSiddharth.github.io/devlog-ai`

The GitHub Actions workflow auto-regenerates the site every time you push new devlogs!

---

## 🔧 Configuration

| Environment Variable | Default | Description |
|---------------------|---------|-------------|
| `DEVLOG_MODEL` | `llama3` | Ollama model to use (try `mistral`, `phi3`, `gemma2`) |

```bash
# Use a different model
DEVLOG_MODEL=mistral java -jar target/devlog-ai.jar generate
```

---

## 🗺️ Roadmap

- [x] Git log reader with diff stats
- [x] Ollama AI summarization
- [x] Markdown devlog generation
- [x] Static site generator
- [x] GitHub Pages auto-deploy
- [ ] `--tag` support to filter commits by keyword
- [ ] Jira / Linear integration
- [ ] GitHub Issues integration
- [ ] Weekly email digest
- [ ] VS Code extension
- [ ] Brew / npm global install

---

## 🤝 Contributing

Contributions are welcome! This is an open source project built for the community.

```bash
# Fork → Clone → Branch → PR
git checkout -b feature/your-feature-name
```



---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

<p align="center">
  Built with ❤️ by <strong>ForkAndFire</strong> · sid0x03<br/>
  Open Source Hackathon 2026 · Elite Coders × JB Institute of Engineering and Technology
</p>