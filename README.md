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

<p align="center">
  <a href="https://goswamisiddharth.github.io/devlog-ai">🌐 Live Demo</a> &nbsp;·&nbsp;
  <a href="CONTRIBUTING.md">🤝 Contributing</a> &nbsp;·&nbsp;
  <a href="CHANGELOG.md">📋 Changelog</a> &nbsp;·&nbsp;
  <a href="community/prompts/README.md">💡 Community Prompts</a>
</p>

---

## 🧠 The Problem

Every solo developer and open source contributor faces the same issue:

- ❌ You forget what you worked on last week
- ❌ Writing standups / progress logs is tedious
- ❌ Your GitHub contributions graph tells you *nothing* about what you actually built
- ❌ Team standup tools exist — but **nothing exists for solo devs**

## ✅ The Solution

**DevLog AI** is a Java CLI tool that:

1. **Reads your git commits** from any local repository
2. **Sends them to Ollama** (free, local LLM — no API key, no cost, no cloud)
3. **Generates a human-readable standup summary** in plain English
4. **Outputs a beautiful Markdown devlog** saved in your repo
5. **Builds a static website** auto-deployed to GitHub Pages
6. **Fetches open GitHub Issues** to track what's pending
7. **Sets up any project** in one command with `devlog init`

All 100% local. All 100% open source. Zero API costs.

---

## 🎬 Demo Output

```
╔═══════════════════════════════════════════╗
║   🔥 DevLog AI — by ForkAndFire           ║
║   Open Source Standup for Solo Devs       ║
║   Powered by Ollama (local LLM)           ║
╚═══════════════════════════════════════════╝

📂 Reading git log from: 2026-06-02 → 2026-06-02
✅ Found 5 commit(s)
🐛 Fetching issues from: goswamiSiddharth/devlog-ai
✅ Fetched 3 open issue(s) from GitHub
🤖 Sending to Ollama for summarization...
📝 Generating Markdown devlog...
✅ Devlog saved → devlogs/2026-06-02.md
🌐 Regenerating static site...
✅ Site updated → docs/index.html

🔥 ForkAndFire | DevLog AI — Done!
```

**Generated AI Summary looks like:**
> - Added GitHub Issues integration using public API — no token required
> - Implemented `--tag` filter for filtering commits by conventional type
> - Redesigned static site with stats dashboard showing total commits and lines written
> - Fixed Windows cross-platform compatibility in git log reader
> - Created `devlog init` command for one-shot project setup

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version | Install |
|------|---------|---------|
| Java | 21+ | [adoptium.net](https://adoptium.net) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org) |
| Ollama | Latest | [ollama.ai](https://ollama.ai) |
| Git | Any | Pre-installed on most systems |

### Installation

```bash
# 1. Clone the repo
git clone https://github.com/goswamiSiddharth/devlog-ai.git
cd devlog-ai

# 2. Build the JAR
mvn package

# 3. Install Ollama model (one time setup)
ollama pull llama3

# 4. Start Ollama (keep running in background)
ollama serve
```

### Quick Start — Use in any project

```bash
# Go to any git project
cd your-project

# Initialize DevLog AI (one time per project)
java -jar /path/to/devlog-ai/target/devlog-ai.jar init

# Generate your first devlog!
java -jar /path/to/devlog-ai/target/devlog-ai.jar generate
```

---

## 📖 Usage

```bash
# Generate today's devlog
java -jar target/devlog-ai.jar generate

# Generate weekly devlog (last 7 days)
java -jar target/devlog-ai.jar generate --week

# Filter by commit type
java -jar target/devlog-ai.jar generate --tag feat
java -jar target/devlog-ai.jar generate --tag fix --week

# Include open GitHub Issues
java -jar target/devlog-ai.jar generate --issues goswamiSiddharth/devlog-ai

# Full power combo!
java -jar target/devlog-ai.jar generate --week --tag feat --issues goswamiSiddharth/devlog-ai

# Setup DevLog AI in any project
java -jar target/devlog-ai.jar init

# Rebuild static site only
java -jar target/devlog-ai.jar site
```

---

## 📁 Project Structure

```
devlog-ai/
├── src/main/java/com/devlogai/
│   ├── cli/
│   │   ├── DevLogCLI.java           # CLI entry point & command routing
│   │   └── InitCommand.java         # devlog init command
│   ├── git/
│   │   ├── GitLogReader.java        # Reads & parses git log output
│   │   └── GitHubIssuesClient.java  # Fetches open GitHub Issues
│   ├── ollama/
│   │   └── OllamaClient.java        # Local LLM API client
│   ├── generator/
│   │   ├── MarkdownGenerator.java   # Generates .md devlog files
│   │   └── StaticSiteGenerator.java # Generates static HTML site
│   └── model/
│       ├── Commit.java              # Commit data model
│       └── GitHubIssue.java         # GitHub Issue data model
├── community/
│   └── prompts/
│       └── README.md                # Community prompt library
├── devlogs/                         # Your generated devlogs
├── docs/                            # GitHub Pages static site
├── .github/workflows/
│   └── deploy-site.yml              # Auto-deploy to GitHub Pages
├── CHANGELOG.md                     # Version history
├── CONTRIBUTING.md                  # How to contribute
├── pom.xml
└── README.md
```

---

## 🌐 GitHub Pages Deployment

1. Push your repo to GitHub
2. Go to **Settings → Pages → Source → GitHub Actions**
3. Your devlog site is live at: `https://YOUR_USERNAME.github.io/devlog-ai`

The workflow auto-deploys every time you push! ✅

---

## 🔧 Configuration

| Environment Variable | Default | Description |
|---------------------|---------|-------------|
| `DEVLOG_MODEL` | `llama3` | Ollama model (try `mistral`, `phi3`, `gemma2`) |

```bash
# Use a different model
DEVLOG_MODEL=mistral java -jar target/devlog-ai.jar generate
```

---

## 💡 Community Prompts

Want a different summary style? Check out our [Community Prompts Library](community/prompts/README.md)!

Includes prompts for:
- 🐦 Tweet-style build-in-public updates
- 🔧 Technical deep dives
- 👔 Manager-friendly summaries
- 📖 Learning journal entries
- 🔄 Weekly retrospectives
- 📋 Changelog entries

---

## 🗺️ Roadmap

- [x] Git log reader with diff stats
- [x] Ollama AI summarization
- [x] `--tag` commit filter
- [x] GitHub Issues integration
- [x] `devlog init` command
- [x] Static site with stats dashboard
- [x] GitHub Pages auto-deploy
- [x] Community prompts library
- [ ] VS Code extension
- [ ] Brew / npm global install
- [ ] Weekly email digest
- [ ] GitLab / Bitbucket support

---

## 🤝 Contributing

Contributions are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

```bash
git checkout -b feature/your-feature-name
# make changes
git commit -m "feat: your feature"
git push origin feature/your-feature-name
# open PR!
```

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

<p align="center">
  Built with ❤️ by <strong>ForkAndFire</strong> · sid0x03<br/>
  Open Source Hackathon 2026 · Elite Coders × JB Institute of Engineering and Technology<br/>
  <a href="https://goswamisiddharth.github.io/devlog-ai">🌐 Live Site</a>
</p>
