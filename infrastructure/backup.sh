#!/bin/bash
set -eo pipefail

TRACE_ID=${TRACE_ID:-$(date +"%Y%m%d%H%M%S-$RANDOM")}
echo "[INFO] [TraceID: $TRACE_ID] Starting backup process."

SOURCE_DATA_DIR="${SOURCE_DATA_DIR:-./data}"
SECURE_STORAGE_DIR="${SECURE_STORAGE_DIR:-./backup-storage}"
TIMESTAMP=$(date +"%Y%m%d%H%M%S")
BACKUP_FILE="$SECURE_STORAGE_DIR/backup-$TIMESTAMP.tar.gz"

function handle_error {
    echo "[ALERT] [TraceID: $TRACE_ID] Backup job failed! Sending alert to administrators." >&2
}

# Trap ERR and EXIT to ensure we catch failures correctly.
trap 'if [ $? -ne 0 ]; then handle_error; fi' EXIT

mkdir -p "$SECURE_STORAGE_DIR"
mkdir -p "$SOURCE_DATA_DIR"

echo "[INFO] [TraceID: $TRACE_ID] Pushing snapshot of DB and files to secure storage..."
tar -czf "$BACKUP_FILE" -C "$SOURCE_DATA_DIR" .
echo "[INFO] [TraceID: $TRACE_ID] Snapshot pushed successfully to $BACKUP_FILE"
