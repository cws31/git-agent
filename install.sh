#!/bin/bash
set -e

echo "================================================================"
echo " Building and Installing cwsgit globally for macOS / Linux..."
echo "================================================================"

mvn clean package -DskipTests

INSTALL_DIR="/usr/local/bin"
JAR_DIR="/usr/local/share/cwsgit"

if [ ! -w "$INSTALL_DIR" ] || [ ! -w "$JAR_DIR" ]; then
    echo "Administrative privileges required to install to /usr/local/bin."
    SUDO="sudo"
else
    SUDO=""
fi

$SUDO mkdir -p "$JAR_DIR"
$SUDO mkdir -p "$INSTALL_DIR"

$SUDO cp target/GitAgent-0.0.1-SNAPSHOT.jar "$JAR_DIR/cwsgit.jar"

# Create global wrapper script
cat << 'EOF' | $SUDO tee "$INSTALL_DIR/cwsgit" > /dev/null
#!/bin/bash
java -jar /usr/local/share/cwsgit/cwsgit.jar "$@"
EOF

$SUDO chmod +x "$INSTALL_DIR/cwsgit"

echo ""
echo "================================================================"
echo "  ✓ Success! 'cwsgit' installed globally at $INSTALL_DIR/cwsgit"
echo ""
echo "  Open ANY Git repository on your machine and run:"
echo "      cwsgit install"
echo "================================================================"