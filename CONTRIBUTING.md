# 🔥 Contributing to DevLog AI

First off — thanks for taking the time to contribute! DevLog AI is built for the developer community, by the developer community.

---

## 🧠 What is DevLog AI?

A Java CLI tool that reads your git commits, summarizes them using Ollama (local LLM), and generates a beautiful daily/weekly devlog in Markdown + a static site deployable to GitHub Pages.

---

## 🚀 How to Contribute

### 1. Fork the repo
Click the **Fork** button on the top right of the repo page.

### 2. Clone your fork
```bash
git clone https://github.com/YOUR_USERNAME/devlog-ai.git
cd devlog-ai
```

### 3. Create a branch
```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

### 4. Make your changes

### 5. Build and test
```bash
mvn package
java -jar target/devlog-ai.jar generate
```

### 6. Commit with a clear message
```bash
git commit -m "feat: add your feature description"
```

### 7. Push and open a PR
```bash
git push origin feature/your-feature-name
```
Then open a Pull Request on GitHub against the `main` branch.

---

## 📁 Project Structure

```
devlog-ai/
├── src/main/java/com/devlogai/
│   ├── cli/DevLogCLI.java           # CLI entry point
│   ├── git/GitLogReader.java        # Git log parser
│   ├── ollama/OllamaClient.java     # Local LLM client
│   ├── generator/
│   │   ├── MarkdownGenerator.java   # .md devlog generator
│   │   └── StaticSiteGenerator.java # HTML site generator
│   └── model/Commit.java            # Commit data model
├── devlogs/                         # Generated devlogs
├── docs/                            # GitHub Pages site
└── pom.xml
```

---

## 💡 Ideas for Contributions

- 🐛 Bug fixes
- 🌐 GitHub Issues integration
- 🏷️ `--tag` filter for commits
- 🎨 Site theme improvements
- 📦 `devlog init` command
- 🌍 Support for GitLab / Bitbucket
- 📧 Weekly email digest feature
- 🧪 Unit tests

---

## 📝 Commit Message Convention

```
feat: add new feature
fix: fix a bug
docs: update documentation
refactor: code refactoring
test: add tests
chore: maintenance tasks
```

---

## ⚙️ Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.8+ |
| Ollama | Latest |
| Git | Any |

---

## 🤝 Code of Conduct

Be respectful. Be kind. We're all here to build cool things together.

---

<p align="center">Built with ❤️ by <strong>ForkAndFire</strong> · sid0x03</p>
