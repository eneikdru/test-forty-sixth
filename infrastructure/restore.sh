#!/bin/bash
set -eo pipefail

TRACE_ID=${TRACE_ID:-$(date +"%Y%m%d%H%M%S-$RANDOM")}
echo "[INFO] [TraceID: $TRACE_ID] Starting restore process."

TARGET_DATA_DIR="${TARGET_DATA_DIR:-${SOURCE_DATA_DIR:-./data}}"
SECURE_STORAGE_DIR="${SECURE_STORAGE_DIR:-./backup-storage}"

function handle_error {
    echo "[ALERT] [TraceID: $TRACE_ID] Restore job failed! Sending alert to administrators." >&2
}

# Trap ERR and EXIT to ensure we catch failures correctly.
trap 'if [ $? -ne 0 ]; then handle_error; fi' EXIT

START_NANO=$(date +%s%N 2>/dev/null || true)
if [[ "$START_NANO" =~ ^[0-9]+$ ]]; then
    START_TIME_MS=$(( START_NANO / 1000000 ))
else
    START_TIME_MS=$(( $(date +%s) * 1000 ))
fi

if [ -n "$BACKUP_FILE" ]; then
    LATEST_BACKUP="$BACKUP_FILE"
else
    LATEST_BACKUP=$(ls -1t "$SECURE_STORAGE_DIR"/backup-*.tar.gz 2>/dev/null | head -n 1 || true)
fi

if [ -z "$LATEST_BACKUP" ] || [ ! -f "$LATEST_BACKUP" ]; then
    echo "[ERROR] [TraceID: $TRACE_ID] No valid backup file found to restore." >&2
    exit 1
fi

echo "[INFO] [TraceID: $TRACE_ID] Restoring snapshot from $LATEST_BACKUP to $TARGET_DATA_DIR..."

mkdir -p "$TARGET_DATA_DIR"
rm -rf "${TARGET_DATA_DIR:?}"/*

tar -xzf "$LATEST_BACKUP" -C "$TARGET_DATA_DIR"

END_NANO=$(date +%s%N 2>/dev/null || true)
if [[ "$END_NANO" =~ ^[0-9]+$ ]]; then
    END_TIME_MS=$(( END_NANO / 1000000 ))
else
    END_TIME_MS=$(( $(date +%s) * 1000 ))
fi

RTO_MS=$(( END_TIME_MS - START_TIME_MS ))
echo "[INFO] [TraceID: $TRACE_ID] Restore completed successfully. RTO measured: ${RTO_MS}ms."
