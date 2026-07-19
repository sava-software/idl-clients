# Mutation-testing baseline & triage policy — `idl-clients-bundle`

Each `pitest<Suite>` run is finalized by `pitest<Suite>Verify`, which diffs the
run's unkilled mutants (`SURVIVED` and `NO_COVERAGE`) against the accepted
baseline in `<suite>-accepted.csv` and **fails on anything new**. Baseline row
format: `class,method,line,mutator,status`. The canonical policy is sava-build's
`HARDENING.md`; this file records what is accepted *here* and why.

A new unkilled mutant has exactly three legal outcomes:

1. **Kill it** — add or strengthen a test. Prefer asserting the property the
   mutant breaks (an overflow guard rejecting, a quote's rounding direction, the
   entry a price chain resolves to) over restating the implementation.
2. **Refactor** — restructure so the mutant cannot exist.
3. **Accept it knowingly** — re-run with `-PupdateMutationBaseline` and record
   the reason under "Triaged equivalent mutants" below. Acceptance is for
   mutants *equivalent with respect to observable behavior*, not for "hard to
   test".

Line numbers are part of the baseline key, so unrelated edits to a mutated file
shift entries: the verify task then reports both stale and "new" rows. Confirm
the new rows are the shifted old ones, then refresh with
`-PupdateMutationBaseline`. Baseline rows are deduplicated by key, so a suite's
row count is at or below the mutant count its report prints.

## Suites

Targeting is wildcard-with-exclusions, never an allowlist, so a new hand-written
class is mutated by default rather than silently skipped. `build.gradle.kts` is
the authoritative definition; the split exists for inner-loop speed, not for
coverage.

| Suite | Covers | Runtime |
|---|---|---|
| `pitestOrca` | `orca.*` — quote math, tick/sqrt-price conversion, PDA derivation | ~20s |
| `pitestScope` | `kamino.scope.*` — the oracle price readers | ~5s |
| `pitestClients` | everything else hand-written in the module | ~12s |

Generated `**.gen.*` code is excluded from all three: its correctness belongs to
idl-src-gen, and mutating ~1590 classes of boilerplate would bury the
hand-written signal. Also excluded: test/fuzz sources sharing the recompiled
root (including `ResourceUtil`, a helper matching neither `*Test*` nor `*Fuzz`),
the git-ignored `Integ.*` scratch files (present locally, absent in CI, so
mutating them would make the baseline machine-dependent), and
`software.sava.idl.clients.spl.*`, which reaches this classpath as a project
dependency and is owned by `idl-clients-spl`'s own suite.

## Baseline composition (seeded 2026-07-18)

| Suite | Rows | `NO_COVERAGE` | `SURVIVED` | Killed | Test strength |
|---|---|---|---|---|---|
| `orca` | 142 | 86 | 56 | 395/541 (73%) | 87% |
| `scope` | 272 | 232 | 40 | 25/352 (7%) | 37% |
| `clients` | 1107 | 1065 | 42 | 229/1357 (17%) | 85% |

**This baseline is triage debt made explicit, not acceptance.** It was seeded
with the full pre-existing survivor population when the ratchet was adopted, per
HARDENING.md's adoption path. Nothing in it has been analyzed as equivalent yet
— the "Triaged equivalent mutants" section below is deliberately empty. Its
purpose today is to stop the population from *growing*: every new unkilled
mutant now has to be argued for.

## Untriaged debt, in priority order

1. **`kamino.scope` — the weakest area and the highest stakes.** Test strength
   37% against 7% killed: the price readers are a fixed-layout account walked by
   offset, where a wrong branch yields a plausible wrong price rather than a
   failure. The `SURVIVED` mass is in `ScopeReaderRecord` (35) and `FixedPrice`
   (4); the `NO_COVERAGE` mass is the entry types that the two committed corpus
   dumps happen not to exercise — `ScopeEntriesRecord` (36),
   `MostRecentOfEntry` (26), `NotYetSupported` (24), `CappedMostRecentOf` (19),
   `Conditional` (18). More seed dumps covering those entry types would convert
   a large share of this to killed mutants without writing a test per type.
2. **Money math with unasserted boundaries.** `OrcaUtil` (35 `SURVIVED`) and
   `orca.quote.WhirlpoolQuote` (21) are dense in `ConditionalsBoundaryMutator`
   and `RemoveConditional` hits on the overflow/range guards — `toU64`,
   `mulDivU64`, `requireU128`, `boundTickIndex`, `isValidStartTickIndex`,
   `sqrtPriceX64ToTickIndex`. Each is the signature of a guard whose rejection
   case is never asserted: the tests prove the happy path returns the right
   number but never that the guard fires. `meteora.dlmm.DlmmUtils` (16) is the
   same shape. This is the category HARDENING.md means by "turning equivalent
   mutants into killable ones" — the property is real and assertable.
3. **`NO_COVERAGE` in RPC-facing plumbing** — `*Client`/`*ClientImpl`, request
   and response DTOs and their builders (`KaminoLendClientImpl` 68,
   `JupiterVoteClientImpl` 48, `MarinadeProgramClient` 44, `MeteoraDlmmClient`
   42, the Jupiter REST request/response records). These are instruction
   assembly and JSON binding with no unit coverage. Lowest priority per mutant,
   but the largest block by far, and the reason `clients` shows 17% killed
   against 85% test strength: where it is covered, the tests kill well.

Shrinking the baseline is always an improvement; growing it requires a reason
written here.

## Triaged equivalent mutants (accepted with reasons)

*(empty — nothing in the seeded baseline has been analyzed as equivalent yet.
Add a group here when you accept a mutant, grouped by the principle that makes
it equivalent, following `json-iterator/config/pitest/README.md` for the shape.)*
