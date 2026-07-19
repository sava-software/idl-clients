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

| Date | Suite | Rows | `NO_COVERAGE` | `SURVIVED` | Killed | Test strength |
|---|---|---|---|---|---|---|
| seeded 2026-07-18 | `orca` | 142 | 86 | 56 | 395/541 (73%) | 87% |
| seeded 2026-07-18 | `scope` | 272 | 232 | 40 | 25/352 (7%) | 37% |
| seeded 2026-07-18 | `clients` | 1107 | 1065 | 42 | 229/1357 (17%) | 85% |
| 2026-07-19 | `orca` | 109 | 55 | 54 | 430/541 (79%) | 88% |
| 2026-07-19 | `scope` | 42 | 1 | 41 | 305/354 (86%) | 87% |

**The seeded baseline is triage debt made explicit, not acceptance.** Priorities
1 and 2 below have been worked down; every `SURVIVED` row remaining in `scope`
and `orca` is analyzed (see the accepted-equivalents section). Priority 3 —
the client/DTO plumbing, including `orca`'s `OrcaWhirlpoolsClient(Impl)` block —
is the remaining tranche.

## Untriaged debt, in priority order

1. ~~**`kamino.scope`**~~ — *discharged 2026-07-19*, 272 rows → 42, 7% → 86%
   killed. Rather than more corpus dumps, `ScopeComputeEntryTests` drives every
   entry type through `computeEntry` with hand-built 512-slot mappings
   (per-type `generic` payloads, ref prices, EMA bitmasks, the frozen-flag
   mask, beyond-enum ordinals, backward/self references), and
   `ScopeEntryEqualityTests` / `ScopeEntriesRecordTests` /
   `ScopeProgramClientTests` cover the hand-written equals/hashCode overrides,
   chain resolution, and the program client. **Two defects surfaced**:
   `ScopeProgramClient.initialize(Configuration, feedName)` passed
   `oracleMappings()` and `oraclePrices()` into each other's slots —
   transposed accounts on-chain — and `NotYetSupported.hashCode` omitted the
   `index` its equals compares. The remaining 42 rows: 41 accepted equivalents
   (below) plus the `readPriceChains(Reserve)` overload, which needs a full
   kamino `Reserve` fixture for a two-line delegation.
2. ~~**Money math with unasserted boundaries.**~~ — *largely discharged
   2026-07-19*, `orca` 142 → 109 rows. `OrcaBoundaryTests` asserts the guard
   rejections and exact-boundary behavior the happy-path tests skipped: u64
   max round-trips through the transfer-fee math, feeBps range ends, position
   status at the exact lower/upper sqrt-price, tick<->sqrt-price round trips at
   the extremes, non-positive sqrt-price rejection, the unvalidated zero-amount
   fast path, u64 overflow in liquidity estimates, and the remaining-accounts
   builder. A rewards-quote delta-invariance test pins the timestamp
   subtraction the all-zero fixture could not see. `meteora.dlmm.DlmmUtils`
   (16) remains untouched — same shape, next tranche.
3. **`NO_COVERAGE` in RPC-facing plumbing** — `*Client`/`*ClientImpl`, request
   and response DTOs and their builders (`KaminoLendClientImpl` 68,
   `JupiterVoteClientImpl` 48, `MarinadeProgramClient` 44, `MeteoraDlmmClient`
   42, `OrcaWhirlpoolsClient(Impl)` ~57 in the `orca` suite, the Jupiter REST
   request/response records). Instruction assembly and JSON binding with no
   unit coverage. The pattern to apply is idl-clients-spl's client-layer tests:
   distinct keys per role, account lists asserted for order and
   signer/writable flags, data decoded back through the generated `IxData`
   records. That pass over the SPL module surfaced two real wiring bugs, and
   the scope client's transposed-initialize above makes three — this block is
   where the bugs live.

Shrinking the baseline is always an improvement; growing it requires a reason
written here.

## Triaged equivalent mutants (accepted with reasons)

### Hash-mixing arithmetic in hand-written hashCode (30 mutants, scope)

`MathMutator` hits on the `31 * result + component` mixing steps in
`MostRecentOfEntry`, `CappedMostRecentOf`, `Conditional`, `NotYetSupported`,
`ScopeEntriesRecord`, and `PriceChainsRecord`. Any deterministic function of
the compared components satisfies the `hashCode` contract — the tests assert
equal-objects-equal-hashes and that each component perturbs the hash, and both
properties hold under any mixing formula. Killing these would mean asserting
literal hash values, which restates the implementation.

### Record-pattern deconstruction conditionals (10 mutants, scope)

`RemoveConditionalMutator_EQUAL_IF` on the `o instanceof Type(...)` record
deconstruction lines in the same equals methods. The tests cover a matching
twin, a mismatching variant per component, a null, and a different type; the
surviving conditionals are the compiler-synthesized component-extraction checks
inside the pattern, which cannot take their alternate branch once the
`instanceof` has matched.

### Zero fast paths in front of arithmetic that yields zero anyway (scope + orca)

The `bitmask != 0` short-circuit in `ScopeReaderRecord.emaTypes` (an empty
`EnumSet` equals `Set.of()`), the `value < 0` boundaries at exactly zero in
`FixedPrice.createEntry` and `ScopeReader.scaleScopePrice` (both zero
representations produce an equal `BigDecimal`), and the `amount == 0`/
`numeratorFactor == 0` fast paths in `OrcaUtil.mulDivU64` and
`applyTransferFee`/`reverseApplyTransferFee` (`0/d` and a zero fee compute the
same result the long way). Same principle as idl-clients-spl's `Fee.toRatio`
acceptances: the guard is a deliberate allocation-avoiding fast path whose
removal is unobservable.

### Trim-on-exact-fit copies (4 mutants, scope)

`ConditionalsBoundary`/`ORDER_IF` on `j < entries.length` in
`ScopeReaderRecord.parseEntries` and `ScopeEntriesRecord.parseChain`: forcing
the trim branch when nothing was trimmed copies the full array — a distinct
array with identical content, indistinguishable through every consumer.

### Defensive guards no producer can trip (scope + orca)

The `i < 0` half of `ScopeReaderRecord.entry`'s bounds check (every index is
read masked-unsigned), the `emaTypes != null` clause in `validateNoEmaTypes`
(its one producer never returns null), `OrcaUtil.u64`'s `> U64_MAX` check (an
unsigned long cannot exceed it), the `signum() < 0` halves of both `toU64`s
behind subtractions that cannot go negative, and `requireU128`'s return value
(callers use it for the throw, not the value). Removing any of them is
unobservable without constructing states the codebase cannot produce.

### BigInteger shift symmetry (5 mutants, orca)

The `msb >= 64` normalize conditional and its boundary variants in
`sqrtPriceX64ToTickIndex`: `BigInteger.shiftLeft(-n)` **is**
`shiftRight(n)`, so both branches compute the same expression and the
conditional is purely cosmetic.

### Log-approximation precision headroom (3 mutants, orca)

The `precision < BIT_PRECISION` loop bound and `precision++` in
`sqrtPriceX64ToTickIndex`, and the `tickLow == tickHigh` fast return: the
tick is derived from a 14-bit log approximation with error margins
(`LOG_B_P_ERR_MARGIN_*`) sized so that *extra* iterations or taking the slow
path cannot change the resolved tick — verified by round-trip tests across the
full tick range including both extremes.

### Rewards-quote domain equivalents (4 mutants, orca)

`collectRewardsQuote`'s zero fast path (`rewardGrowthDelta == 0 ||
liquidity == 0` computes zero the long way too), the `poolLiquidity != 0`
accrual skip under a zero-emission fixture, and the `product > U128` overflow
boundary at exactly `U128` (Rust's `unwrap_or(0)` mirror), plus
`orderTicks`/`orderPrices` at equal operands where both orderings are the same
pair.
