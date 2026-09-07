#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <fix|monthly>" >&2
  exit 1
fi

case "$1" in
  fix|monthly) ;;
  *) echo "Usage: $0 <fix|monthly>" >&2; exit 1 ;;
esac

if ! command -v gh >/dev/null 2>&1; then
  echo 'GitHub CLI (gh) is required. Install it and authenticate with gh auth login.' >&2
  exit 1
fi

repository=sava-software/idl-clients
release_tag=$(gh release view --repo "$repository" --json tagName --jq '.tagName')
if [[ ! "$release_tag" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Latest release has an unsupported tag: $release_tag" >&2
  exit 1
fi

echo "Dispatching Maven Central upload for $release_tag ($1)."
exec gh workflow run publish-central.yml --repo "$repository" --ref main \
  -f "tag=$release_tag" -f "reason=$1"
