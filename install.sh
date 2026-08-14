#!/bin/bash
set -e

# ================================================================
# CONFIGURATION - Replace with your GitHub details
# ================================================================
REPO_OWNER="SonuKumar"      # Your GitHub username/org
REPO_NAME="git-agent"       # Your repository name
JAR_ASSET_NAME="cwsgit.jar" # Name of the JAR file uploaded to GitHub Release

INSTALL_DIR="/usr/local/bin"
JAR_DIR="/usr/local/share/cwsgit"

echo "================================================================"
echo " Installing cwsgit globally for macOS / Linux..."
echo "================================================================"

# 1. Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed on this machine."
    echo "   Please install JDK 21 or higher to use cwsgit."
    exit 1
fi

# 2. Handle permissions
SUDO=""
if [ ! -w "$INSTALL_DIR" ] || [ ! -w "$JAR_DIR" ]; then
    SUDO="sudo"
fi

$SUDO mkdir -p "$JAR_DIR"
$SUDO mkdir -p "$INSTALL_DIR"

# 3. Download pre-compiled binary from GitHub Releases
echo "⬇️  Downloading latest release from GitHub..."
RELEASE_URL="https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/latest/download/${JAR_ASSET_NAME}"

if ! $SUDO curl -sSL -o "$JAR_DIR/cwsgit.jar" "$RELEASE_URL"; then
    echo "❌ Failed to download release binary from GitHub."
    echo "   Please make sure the release exists at: $RELEASE_URL"
    exit 1
fi

# 4. Create executable wrapper script
cat << 'EOF' | $SUDO tee "$INSTALL_DIR/cwsgit" > /dev/null
#!/bin/bash
exec java -jar /usr/local/share/cwsgit/cwsgit.jar "$@"
EOF

$SUDO chmod +x "$INSTALL_DIR/cwsgit"

echo "================================================================"
echo " ✓ Success! 'cwsgit' installed globally to $INSTALL_DIR/cwsgit"
echo ""
echo " Run 'cwsgit install' inside any Git repo to enable AI reviews."
echo "================================================================"