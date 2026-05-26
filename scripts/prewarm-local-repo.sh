#!/bin/sh

set -eu

SOURCE_REPO="${1:-$HOME/.m2/repository}"
TARGET_REPO="$(cd "$(dirname "$0")/.." && pwd)/.mvn/local-repo"

if [ ! -d "$SOURCE_REPO" ]; then
  echo "source repo not found: $SOURCE_REPO" >&2
  exit 1
fi

mkdir -p "$TARGET_REPO"
rsync -a "$SOURCE_REPO"/ "$TARGET_REPO"/

find "$TARGET_REPO" \
  \( -name "_remote.repositories" -o -name "*.lastUpdated" -o -name "m2e-lastUpdated.properties" -o -name "resolver-status.properties" \) \
  -delete

echo "local repo prepared at $TARGET_REPO"
