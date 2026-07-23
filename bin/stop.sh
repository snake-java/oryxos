#!/bin/bash
# OryxOS Stop Script

PID_FILE=".oryxos.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    echo "Stopping OryxOS (PID: $PID)..."
    kill "$PID" 2>/dev/null || true
    rm -f "$PID_FILE"
    echo "Stopped"
else
    echo "No PID file found. Trying to find running process..."
    PID=$(pgrep -f "oryxos-boot" | head -1)
    if [ -n "$PID" ]; then
        echo "Found PID: $PID"
        kill "$PID" 2>/dev/null || true
        echo "Stopped"
    else
        echo "No running OryxOS process found"
    fi
fi
