#!/bin/bash
# OryxOS Package Script
# Build, package, and sync to remote

set -e

VERSION=${1:-"0.1.0-SNAPSHOT"}
REMOTE=${REMOTE:-"origin"}
BRANCH=${BRANCH:-"main"}

echo "=== OryxOS Package Script ==="
echo "Version: $VERSION"
echo "Remote: $REMOTE"
echo "Branch: $BRANCH"
echo ""

# Step 1: Clean and build
echo "[1/4] Cleaning and building..."
mvn clean package -DskipTests -q

# Step 2: Create release directory
echo "[2/4] Creating release package..."
RELEASE_DIR="oryxos-release-$VERSION"
rm -rf "$RELEASE_DIR"
mkdir -p "$RELEASE_DIR"/{bin,config,libs}

# Copy fat JAR
cp oryxos-boot/target/oryxos-boot-*.jar "$RELEASE_DIR/bin/oryxos-server"

# Copy config
cp -r config/* "$RELEASE_DIR/config/" 2>/dev/null || true

# Copy libs (dependencies)
cp oryxos-*/target/*.jar "$RELEASE_DIR/libs/" 2>/dev/null || true

# Create tar.gz
tar -czvf "$RELEASE_DIR.tar.gz" "$RELEASE_DIR"
rm -rf "$RELEASE_DIR"

echo "[3/4] Package created: $RELEASE_DIR.tar.gz"

# Step 3: Git operations (only if on correct branch)
if git rev-parse --abbrev-ref HEAD | grep -q "$BRANCH"; then
    echo "[4/4] Committing changes..."
    git add -A
    git commit -m "Release: $VERSION

Built from package.sh
Timestamp: $(date -u '+%Y-%m-%d %H:%M:%S UTC')"
    git push "$REMOTE" "$BRANCH"
    echo "Pushed to $REMOTE/$BRANCH"
else
    echo "[4/4] Skipping git push (not on $BRANCH branch)"
fi

echo ""
echo "=== Package Complete ==="
ls -la *.tar.gz 2>/dev/null || true
