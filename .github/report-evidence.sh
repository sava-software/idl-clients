#!/usr/bin/env bash
# Every commit that moves a generated channel record must carry that generation's
# movement evidence.
#
# AGENTS.md states the invariant: "A change to a generated `sources.json` hash without a
# matching report change means the generation was run without retaining its
# channel-movement evidence." This is that sentence as a gate. It has fired six times in
# thirty commits, each time discovered a generation later — by which point the evidence
# is unrecoverable, because movement is an event and the next run reports nothing.
#
# Keyed on the movement-implying *lines* of a modified record, never on the file being
# touched. Two facts force that narrowing:
#
#   * `programDataStateSlot` can be restamped fleet-wide with no movement anywhere
#     (idl-src-gen#4: a fatal between the record read and the per-program restore leaves
#     the corpus recordless, and the next run takes the silent first-generation path).
#     A file-level check would fail every one of those ~43 files for nothing.
#   * The report carries no timestamp by design, so two consecutive no-movement runs
#     write it byte-identical. A file-level check would demand a diff that must not exist.
#
# Only modified records count. An *added* `sources.json` is a program's first generation:
# it has no baseline to have moved against, and reports no movement by construction.
#
# Usage:  .github/report-evidence.sh <range>     # e.g. HEAD~30..HEAD, or $before..$after
#         .github/report-evidence.sh <commit>    # a single commit
#
# A commit that legitimately moves a record without a generation behind it — a hand-fixed
# hash, a package repointed at another address — says so in a `Report-Evidence:` trailer,
# which the audit trail then carries with it.
set -uo pipefail

RANGE="${1:-}"
if [ -z "$RANGE" ]; then
  echo "usage: $0 <range|commit>" >&2
  exit 2
fi
case "$RANGE" in
  *..*) ;;
  *) RANGE="${RANGE}^!" ;;   # a single commit, as a range
esac

# The movement report. Named explicitly rather than globbed: after idl-src-gen#3 splits
# the file, the standing-gap dashboard becomes `idl-change-report-gap.txt` and is NOT
# evidence of anything — it re-renders whether or not this run saw movement.
REPORT='idl-change-report.txt'
KEYS='"(hash|lastDeploySlot|programDataPayloadSha256)"'

violations=0
checked=0
while read -r commit; do
  [ -n "$commit" ] || continue
  checked=$((checked + 1))

  moving=$(git show --format='' --unified=0 --diff-filter=M "$commit" -- '*/gen/sources.json' \
    | grep -cE "^[+-][[:space:]]*${KEYS}") || true
  [ "${moving:-0}" -gt 0 ] || continue

  if [ -n "$(git show --format='' --name-only "$commit" -- "$REPORT")" ]; then
    continue
  fi
  if git show -s --format='%(trailers:key=Report-Evidence,valueonly)' "$commit" | grep -q '[^[:space:]]'; then
    echo "note  $(git show -s --format='%h %s' "$commit")"
    echo "      record moved with no report; excused by Report-Evidence: $(git show -s --format='%(trailers:key=Report-Evidence,valueonly)' "$commit" | tr -d '\n')"
    continue
  fi

  violations=$((violations + 1))
  echo "FAIL  $(git show -s --format='%h %s' "$commit")"
  echo "      $moving movement-implying line(s) in $(git show --format='' --name-only --diff-filter=M "$commit" -- '*/gen/sources.json' | wc -l | tr -d ' ') record(s), and no change to $REPORT"
  git show --format='' --unified=0 --diff-filter=M "$commit" -- '*/gen/sources.json' \
    | grep -E "^[+-][[:space:]]*${KEYS}" | sed 's/^/        /' | head -4
done < <(git rev-list --no-merges "$RANGE")

echo
echo "checked $checked commit(s); $violations without movement evidence"
[ "$violations" -eq 0 ]
