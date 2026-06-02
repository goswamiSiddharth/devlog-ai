# 📋 Changelog

All notable changes to **DevLog AI** are documented here.

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [Unreleased]

### Planned
- VS Code extension
- Brew / npm global install support
- Weekly email digest
- GitLab / Bitbucket support

---

## [1.1.0] — 2026-06-02

### Added
- `devlog init` command — one-shot setup for any git project
  - Auto-detects GitHub owner/repo from remote URL
  - Creates `devlogs/`, `docs/`, `.devlog-config`
  - Generates GitHub Actions workflow automatically
  - Prints guided next steps
- `--issues owner/repo` flag — fetch open GitHub Issues into devlog
  - No API token required (uses public GitHub API)
  - Issues rendered as table in Markdown + HTML site
- `--tag <type>` filter — filter commits by conventional commit type
  - Supports: `feat`, `fix`, `docs`, `refactor`, `chore`, `test`
  - Tagged devlogs saved as separate files (e.g. `2026-06-02-feat.md`)
  - Can combine with `--week` and `--issues`
- Community Prompts Library (`community/prompts/README.md`)
  - 8 community prompts for different use cases
  - Contributing guide for new prompts

### Changed
- Redesigned static site with stats dashboard
  - Total DevLogs, Total Commits, Lines Written counters
  - Tag badges with color coding on devlog cards
  - Responsive mobile layout
  - Docs and GitHub buttons in hero section
- Improved Markdown → HTML converter with proper `<table>` support
- `docs/site/` path consolidated to `docs/` for cleaner GitHub Pages setup

### Fixed
- Windows cross-platform fix for `ProcessBuilder` (git commands)
- `devlog init` null parent file crash for root-level config
- Duplicate GitHub Actions workflow detection in `init` command
- Console output path corrected from `docs/site/index.html` to `docs/index.html`
- XML entity encoding in `pom.xml` (`&` → `&amp;`)

---

## [1.0.0] — 2026-06-01

### Added
- Initial release 🔥
- Java CLI with commands: `generate`, `site`, `help`
- `GitLogReader` — reads and parses git log with diff stats
- `OllamaClient` — local LLM integration (no API key, no cost)
  - Graceful fallback if Ollama not running
  - Configurable model via `DEVLOG_MODEL` env variable
- `MarkdownGenerator` — generates beautiful `.md` devlogs
  - AI summary section
  - Commit table with hash, author, date, insertions/deletions
  - Stats table (total commits, files changed, lines added/deleted)
- `StaticSiteGenerator` — builds static HTML site from devlogs
  - Dark GitHub-style theme
  - Individual pages per devlog
  - Auto-deploys via GitHub Actions to GitHub Pages
- `--week` flag for weekly devlog generation
- `--since YYYY-MM-DD` flag for custom date range
- GitHub Actions workflow for Pages deployment
- MIT License
- Professional README with badges, demo output, usage guide

---

## How to Contribute

See [CONTRIBUTING.md](CONTRIBUTING.md) for details on submitting PRs.

---

*Built with ❤️ by **ForkAndFire** · sid0x03*
