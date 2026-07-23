#!/bin/bash
# OryxOS Start Script

set -e

PORT=${1:-8080}
WORKSPACE=${ORYXOS_WORKSPACE:-".oryxos"}

echo "=== Starting OryxOS ==="
echo "Port: $PORT"
echo "Workspace: $WORKSPACE"

# Check if JAR exists
JAR=$(find oryxos-boot/target -name "oryxos-boot-*.jar" -type f 2>/dev/null | head -1)

if [ -z "$JAR" ]; then
    echo "Error: oryxos-boot JAR not found. Run 'mvn clean package' first."
    exit 1
fi

echo "Using JAR: $JAR"

# Set environment
export ORYXOS_WORKSPACE="$WORKSPACE"
export ORYXOS_DATA_DIR="$WORKSPACE"

# Create workspace if not exists
mkdir -p "$WORKSPACE"

# Start server in background
echo "Starting server..."
nohup java -jar "$JAR" serve --server.port="$PORT" > logs/oryxos.log 2>&1 &

PID=$!
echo "$PID" > .oryxos.pid

echo "OryxOS started (PID: $PID)"
echo "Logs: logs/oryxos.log"
echo ""
echo "Dashboard: http://localhost:$PORT/admin/"
echo "API: http://localhost:$PORT/api/v1/"
