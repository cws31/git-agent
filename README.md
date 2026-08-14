Set-Content -Path README.md -Value @"
# cwsgit — Universal AI-Powered Git Agent

\`cwsgit\` is a lightweight, cross-platform CLI tool that intercepts \`git push\` commands, analyzes your code diffs via an AI proxy service, and generates production-grade Conventional Commit messages prior to pushing to remote repositories.

---

## Key Features

* **Automated Commit Review:** Generates standardized Conventional Commit messages using Llama 3.3 via Groq.
* **Secret Redaction:** Automatically scans and redacts sensitive credentials (AWS keys, private tokens, API keys) before transmitting diffs.
* **Noise Reduction:** Excludes minified files and lockfiles (\`package-lock.json\`, \`go.sum\`, \`pom.xml\` targets) from analysis payloads.
* **Interactive CLI:** Allows developers to approve, edit, or reject AI-generated commit messages directly in the terminal.

---

## Prerequisites

* **Java Development Kit (JDK):** Version 21 or higher installed (\`java -version\`).
* **Git:** Installed and available on system \`PATH\`.

---

## Installation

### Windows (PowerShell)
Run the following command in PowerShell:

\`\`\`powershell
iwr -useb https://raw.githubusercontent.com/cws31/git-agent/main/install.ps1 | iex
\`\`\`

### macOS & Linux (Terminal)
Run the following command in terminal:

\`\`\`bash
curl -sSL https://raw.githubusercontent.com/cws31/git-agent/main/install.sh | bash
\`\`\`

> **Note:** Restart your terminal session after installation to ensure system \`PATH\` variables are refreshed.

---

## Getting Started

### 1. Enable \`cwsgit\` in Your Repository
Navigate to any local Git project directory and run:

\`\`\`bash
cwsgit install
\`\`\`

This registers the AI pre-push hook at \`.git/hooks/pre-push\`.

### 2. Commit & Push Code
Make changes and execute standard Git workflow:

\`\`\`bash
git add .
git commit -m "temp commit"
git push origin main
\`\`\`

### 3. Interactive Review
Before code is pushed, \`cwsgit\` intercepts the action and provides an interactive prompt:

\`\`\`text
AI Git Agent
────────────────────────────────────────
Original commit:
temp commit

Suggested commit:
refactor(exceptions): enhance global exception handling logic
────────────────────────────────────────

Use generated commit? [Yes / Edit / No]:
\`\`\`

* **Yes (\`y\`):** Amends current commit to the suggested message and proceeds with push.
* **Edit (\`e\`):** Opens system default editor to modify the generated message prior to push.
* **No (\`n\`):** Retains original commit message or cancels operation.

---

## Bypass & Uninstallation

To bypass AI review for a single push operation:
\`\`\`bash
git push --no-verify origin main
\`\`\`

To remove \`cwsgit\` integration from a specific repository, delete the pre-push hook file:
\`\`\`bash
rm .git/hooks/pre-push
\`\`\`

---

## License

Distributed under the MIT License. See \`LICENSE\` for details.
"@