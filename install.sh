#!/bin/bash
set -e

echo "================================================================"
echo " Building and Installing cwsgit globally for macOS / Linux..."
echo "================================================================"

mvn clean package -DskipTests

JAR_FILE=$(ls target/*.jar | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "[ERROR] Could not find compiled JAR file in target/!"
    exit 1
fi

INSTALL_DIR="/usr/local/bin"
JAR_DIR="/usr/local/share/cwsgit"

SUDO=""
if [ ! -w "$INSTALL_DIR" ] || [ ! -w "$JAR_DIR" ]; then
    SUDO="sudo"
fi

$SUDO mkdir -p "$JAR_DIR"
$SUDO mkdir -p "$INSTALL_DIR"

$SUDO cp "$JAR_FILE" "$JAR_DIR/cwsgit.jar"

# Create global wrapper script
cat << 'EOF' | $SUDO tee "$INSTALL_DIR/cwsgit" > /dev/null
#!/bin/bash
exec java -jar /usr/local/share/cwsgit/cwsgit.jar "$@"
EOF

$SUDO chmod +x "$INSTALL_DIR/cwsgit"

echo "================================================================"
echo " ✓ Success! 'cwsgit' installed globally."
echo " Run 'cwsgit install' inside any Git repo to hook it up."
echo "================================================================"