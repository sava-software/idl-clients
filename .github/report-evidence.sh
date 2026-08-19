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
# Modified and renamed records count; a renamed record whose content also moved is
# detected under its destination path. An *added* record is a program's first
# generation — no baseline to have moved against — and a deleted one is a program
# leaving the corpus; neither demands evidence.
#
# Usage:  .github/report-evidence.sh <range>          # e.g. HEAD~30..HEAD, or $before..$after
#         .github/report-evidence.sh <commit>         # that commit alone
#         .github/report-evidence.sh <sha> --not --remotes   # everything not yet published
#
# Arguments are handed to `git rev-list` as given, so any commit set it names can be
# audited; the single-commit form is the one convenience on top of that.
#
# A commit that legitimately moves a record without a generation behind it — a hand-fixed
# hash, a package repointed at another address — says so in trailers the audit trail then
# carries with it, and the excuse is checked, not just required to exist:
#
#   * `Report-Evidence:` — why no report accompanies the movement, in prose.
#   * `Report-Evidence-Path:` — one per moved record, naming its repo-relative path.
#
# The path set must equal, exactly, the set of records the audit itself detects — a
# superset would let a list be padded until it covers whatever moved, and a subset is an
# omission, which is the error this exists to catch: the first excused commit written
# here summarized four redeploys and silently omitted a fifth record's channel-hash move.
# Prose cannot be validated; the paths can be, so the checkable half of the claim lives
# in them. On failure the audit prints the exact set it detected, so complying is a
# copy-paste rather than a guess. The report counts as evidence only when its
# regular-file blob arrives or changes — a mode-only touch or a deletion is not
# providing it.
#
# This is a mistake-catcher, not a security boundary. Anyone who can push can edit this
# script in the same push, so a deliberate adversary is out of scope by construction.
# Shapes deliberately left out rather than detected, because closing them was tried and
# cost a small semantic-diff system that changed no verdict across the 381 commits of
# real history: movement laundered through a delete and a re-add (same commit or later —
# the re-add is indistinguishable from a first generation), a record rewritten compact
# (the generator always pretty-prints; the anchored grep assumes it), and merge commits
# (--no-merges; this repository has no human merges and release-please's never touch a
# record).
set -uo pipefail

# Pathspecs below are repo-relative; run from anywhere inside the repository.
cd "$(git rev-parse --show-toplevel)" || exit 2

if [ "$#" -eq 0 ]; then
  echo "usage: $0 <range|commit|rev-list arguments...>" >&2
  exit 2
fi
# One bare commit means that commit alone. Without this it would read as "everything
# reachable from it", which on a mature branch audits the entire history every time.
if [ "$#" -eq 1 ]; then
  case "$1" in
    *..*|-*) ;;
    *) set -- "$1^!" ;;
  esac
fi

# The movement report. Named explicitly rather than globbed: after idl-src-gen#3 splits
# the file, the standing-gap dashboard becomes `idl-change-report-gap.txt` and is NOT
# evidence of anything — it re-renders whether or not this run saw movement.
REPORT='idl-change-report.txt'
KEYS='"(hash|lastDeploySlot|programDataPayloadSha256)"'

# Every git invocation that prints diff or file-name output runs through this: color
# codes prefix each line and blind the anchored ^[+-] grep — a clone with
# color.ui=always would pass every movement silently — and quotePath escaping would
# make a non-ASCII record path match no pathspec.
GIT=(git -c color.ui=false -c core.quotePath=false)

# Materialized before the loop so a rev-list failure is its own loud answer: an
# unresolvable range must exit 2, never read as an empty one that passed.
commits=$(git rev-list --no-merges "$@") || {
  echo "report-evidence: git rev-list could not resolve: $*" >&2
  exit 2
}

violations=0
checked=0
while read -r commit; do
  [ -n "$commit" ] || continue
  checked=$((checked + 1))

  # Movement is detected per record, not per commit, because the path set is part of the
  # contract: a record restamped only at `programDataStateSlot` is modified but did not
  # move, and must be neither counted nor demanded in the trailers. Statuses M and R both
  # count — a rename that also moved content is the "package repointed" case itself — and
  # an R record is keyed to its destination. -M pins rename detection on rather than
  # inheriting it.
  moving=0
  moved_paths=''
  sample=''
  while IFS=$'\t' read -r status p1 p2; do
    [ -n "$status" ] || continue
    case "$status" in
      M*) record="$p1" ;;
      R*) record="$p2" ;;
      *) continue ;;
    esac
    case "$record" in */gen/sources.json) ;; *) continue ;; esac
    hunks=$("${GIT[@]}" show --format='' --unified=0 -M "$commit" -- "$p1" ${p2:+"$p2"}) || {
      echo "report-evidence: git show failed for $commit -- $record" >&2
      exit 2
    }
    lines=$(printf '%s\n' "$hunks" | grep -E "^[+-][[:space:]]*${KEYS}") || true
    [ -n "$lines" ] || continue
    moving=$((moving + $(printf '%s\n' "$lines" | grep -c .)))
    moved_paths="${moved_paths}${record}"$'\n'
    sample="${sample}${lines}"$'\n'
  done < <("${GIT[@]}" show --format='' --name-status -M "$commit" -- '*/gen/sources.json')
  [ "$moving" -gt 0 ] || continue

  # Evidence is the report's *content* arriving or changing in this commit — a status
  # letter is not enough, since a mode-only change is status M with an untouched blob,
  # and a deleted report is the opposite of evidence. The object must be a regular
  # file: a symlink or tree at the report path is not a report.
  report_now=$(git ls-tree "$commit" -- "$REPORT" | awk '$1 ~ /^100/ {print $3}')
  report_before=$(git ls-tree "$commit^" -- "$REPORT" | awk '$1 ~ /^100/ {print $3}')
  if [ -n "$report_now" ] && [ "$report_now" != "$report_before" ]; then
    continue
  fi

  detected=$(printf '%s' "$moved_paths" | sort -u)
  excuse=$(git show -s --format='%(trailers:key=Report-Evidence,valueonly,unfold)' "$commit" | tr -d '\n')
  # sort -u: the contract is a set, so a duplicated trailer is harmless rather than a
  # phantom "declared but did not move".
  declared=$(git show -s --format='%(trailers:key=Report-Evidence-Path,valueonly,unfold)' "$commit" \
    | grep . | sort -u) || true

  if printf '%s' "$excuse" | grep -q '[^[:space:]]' && [ "$declared" = "$detected" ]; then
    echo "note  $(git show -s --format='%h %s' "$commit")"
    echo "      record moved with no report; excused by Report-Evidence: ${excuse}"
    printf '%s\n' "$detected" | sed 's/^/        /'
    continue
  fi

  violations=$((violations + 1))
  record_count=$(printf '%s' "$detected" | grep -c .)
  echo "FAIL  $(git show -s --format='%h %s' "$commit")"
  echo "      $moving movement-implying line(s) in $record_count record(s), and no change to $REPORT"
  # The sample is the detection pass's own saved lines, so an exempt record (a first
  # generation, a restamp) can never occupy it while being absent from the demanded set.
  printf '%s' "$sample" | grep . | sed 's/^/        /' | head -4
  if ! printf '%s' "$excuse" | grep -q '[^[:space:]]'; then
    echo "      no Report-Evidence trailer. A record moved by hand needs one, plus exactly"
    echo "      these Report-Evidence-Path trailers — the records the audit detected:"
    printf '%s\n' "$detected" | grep . | sed 's/^/        Report-Evidence-Path: /'
  else
    echo "      Report-Evidence is present, but its Report-Evidence-Path set does not match"
    echo "      the records that moved. The audit detected:"
    printf '%s\n' "$detected" | grep . | sed 's/^/        Report-Evidence-Path: /'
    missing=$(comm -23 <(printf '%s\n' "$detected" | grep .) <(printf '%s\n' "$declared" | grep .))
    extra=$(comm -13 <(printf '%s\n' "$detected" | grep .) <(printf '%s\n' "$declared" | grep .))
    [ -n "$missing" ] && { echo "      missing from the trailers:"; printf '%s\n' "$missing" | sed 's/^/        /'; }
    [ -n "$extra" ] && { echo "      declared but did not move:"; printf '%s\n' "$extra" | sed 's/^/        /'; }
  fi
done <<< "$commits"

echo
echo "checked $checked commit(s); $violations without movement evidence"
[ "$violations" -eq 0 ]
