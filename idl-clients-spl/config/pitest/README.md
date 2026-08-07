# Mutation-testing baseline & triage policy — `idl-clients-spl`

Each `pitest<Suite>` run is finalized by `pitest<Suite>Verify`, which diffs the
run's unkilled mutants (`SURVIVED` and `NO_COVERAGE`) against the accepted
baseline in `<suite>-accepted.csv` and **fails on anything new**. Baseline row
format: `class,method,mutator,STATUS`, followed by a `# family` label and a
diagnostic `# line` tag. The canonical policy is sava-build's `HARDENING.md`,
and `hardeningHelp` is the installed-version task reference; this file records
what is accepted *here* and why.

A new unkilled mutant has exactly three legal outcomes:

1. **Kill it** — add or strengthen a test. Prefer asserting the property the
   mutant breaks (a length prefix rejected, an offset record's exact bytes, the
   state a parsed account reports) over restating the implementation.
2. **Refactor** — restructure so the mutant cannot exist.
3. **Accept it knowingly** — with the installed plugin's named writer task
   (`pitestSplBaselineUpdate`/`Union`/`Prune`/`Rebase`; `hardeningHelp` lists
   which does what) and record the reason under "Triaged equivalent mutants"
   below. Acceptance is for mutants *equivalent with respect to observable
   behavior*, not for "hard to test". Never hand-edit record structure or
   provenance stamps, and run `-PnoMutationHistory` before any record decision.

Baseline keys are **line-less**, so editing above a mutated method churns
nothing and the `# line` tags are review metadata rather than anchors; source
movement alone never warns or requires re-anchoring. Duplicate rows are sibling
mutants of one compound condition and the comparison is a **multiset** — never
hand-dedupe the file.

## Suite

One catch-all suite, `pitestSpl` (~10s), targeting
`software.sava.idl.clients.spl.*` by wildcard with exclusions rather than an
allowlist, so a new hand-written class is mutated by default rather than
silently skipped. The whole module is account encode/decode and instruction
building — the money-critical shape — so there is nothing to scope out.
`build.gradle.kts` is the authoritative definition.

Excluded: generated `**.gen.*` code (correctness belongs to idl-src-gen), test
and fuzz sources sharing the recompiled root, and the git-ignored `Integ.*`
scratch files — present on a dev machine and absent in CI, so mutating them
would make the baseline machine-dependent.

## Mutator set

`STRONGER,EXPERIMENTAL_BIG_INTEGER,EXPERIMENTAL_NAKED_RECEIVER`.

`EXPERIMENTAL_BIG_INTEGER` was off until 2026-07-25, correctly: it fired zero
times when measured 2026-07-20 (742 mutations with and without). It fires now
because `core.math.SafeMath` arrived — the u64/u128 helpers are `BigInteger` method
calls, which `MathMutator` (primitive bytecode ops) cannot see. Re-trialed
2026-07-25 with sava-build 21.5.14's blind-spot advice: **3 generated, 3 killed
by existing tests, 0 unkilled** — after the consolidation the same three sit at
`checkedAddU64` line 93 and `wrappingSubU128` lines 100-101, all killed by
`SafeMathTests`. Enabled at zero baseline cost. The lesson is the mechanism's,
not the module's: a measured "fires zero times" decays as code is added, and
only a per-run scan notices.

`EXPERIMENTAL_BIG_DECIMAL` is declined on the suite (`declineMutator` in
`build.gradle.kts`), which is what silences the advice for the four
`BigDecimal` arithmetic call sites the scan sees. Trialed 2026-07-25:
**0 generated**. It rewrites only `(BigDecimal)BigDecimal` arithmetic, and
every one of those sites is a `divide(BigDecimal, MathContext)` or
`divide(BigDecimal, int, RoundingMode)` overload in
`StakePoolState.calculateSolPrice` / `Fee.toRatio`, which it does not rewrite.
Those four sites are covered instead by `EXPERIMENTAL_NAKED_RECEIVER` (the
dropped `stripTrailingZeros` family below) and by `StakePoolStateTests`'
exact-value assertions.

`EXPERIMENTAL_NAKED_RECEIVER` (replace a fluent call with its receiver — calls
returning their own receiver type are expressions, invisible to
`VoidMethodCallMutator`) was trialed 2026-07-23 with sava-build 21.5.9's
`pitestMutatorTrial -PtrialMutators=EXPERIMENTAL_NAKED_RECEIVER`: **19
generated, 15 killed by existing tests, 4 unkilled**. Enabled the same day. The
four survivors were all dropped `stripTrailingZeros` calls in
`StakePoolState.Fee.toRatio` and `StakePoolState.calculateSolPrice` (both
`BigDecimal` overloads of each): the existing assertions used scale-blind
`compareTo`, so the canonical-form contract was unpinned. Killed by asserting
the stripped representation with scale-sensitive `BigDecimal.equals` on inputs
whose division leaves trailing zeros (`StakePoolStateTests.feeToRatio`,
`calculateSolPriceStripsToCanonicalForm`).

## Baseline composition

| Date | Rows | `NO_COVERAGE` | `SURVIVED` | Killed | Test strength |
|---|---|---|---|---|---|
| seeded 2026-07-18 | 293 | 258 | 35 | 429/743 (58%) | 92% |
| 2026-07-19 | 175 | 170 | 5 | 563/741 (76%) | 99% |
| 2026-07-19 | 10 | 5 | 5 | 732/742 (99%) | 99% |
| 2026-07-19 | 7 | 2 | 5 | 735/742 (99%) | 99% |
| 2026-07-23 | 7 | 2 | 5 | 754/761 (99%) | 99% |
| 2026-07-24 | 5 | 0 | 5 | 756/761 (99%) | 99% |
| 2026-07-25 | 5 | 0 | 5 | 778/783 (99%) | 99% |
| 2026-07-25 | 9 | 0 | 9 | 778/787 (99%) | 99% |
| 2026-07-25 | 10 | 0 | 10 | 834/844 (99%) | 99% |
| 2026-07-25 | 12 | 0 | 12 | 850/862 (99%) | 99% |

The 2026-07-23 row is the `EXPERIMENTAL_NAKED_RECEIVER` intake (below): +19
mutants, all four initial survivors killed, baseline unchanged.

The first 2026-07-25 row is `core.math.SafeMath` arriving with its tests, plus the
`EXPERIMENTAL_BIG_INTEGER` intake it triggered: +22 mutants (19 `STRONGER` /
`NAKED_RECEIVER`, 3 `BIG_INTEGER`), all 22 killed, baseline unchanged.

The second is the u64-helper consolidation: `toU64`, the unsigned widening,
`wrappingSubU128` and the `U64_MAX` / `U128_MASK` constants moved out of
`OrcaUtil`, `WhirlpoolQuote`, `DlmmUtils` and the scope/oracle readers into
`SafeMath`. This suite absorbs the moved population (+4 rows, the fast-path
guard argued below) while `orca` loses 3, `scope` 6 and `clients` 2 — 143
accepted rows across the four suites became 136.

The baseline was seeded with the full pre-existing survivor population when the
ratchet was adopted, per HARDENING.md's adoption path — triage debt made
explicit, not acceptance. **That debt is now discharged.** All three priorities
below were worked down; every one of the 12 remaining rows is analyzed:

| Rows | Group | Status |
|---|---|---|
| 7 | Redundant zero short-circuits | equivalent — accepted, reasoned below |
| 4 | Unsigned-widening fast path | equivalent — accepted, reasoned below |
| 1 | Saturating-subtraction boundary | equivalent — accepted, reasoned below |

The two RPC fetchers (`fetchProgramState` / `fetchValidatorList`) were
covered 2026-07-24 by `StakePoolRpcFetcherTests` against an in-JVM JSON-RPC
capture server (the compact sibling of the bundle's
`SolanaRpcCaptureTests`): the earlier "needs a live `SolanaRpcClient`"
acceptance had gone stale — `SolanaRpcClient.build()` takes any endpoint —
and the requests each fetcher emits are the wiring worth pinning: the pool
account it was given, and the validator-list key *stored in the fetched
state*. The state response is the committed real Jito account.

## Audited timeout-detected mutants (armed 2026-07-28, sava-build 21.5.17)

`spl-timeouts.csv` is present but empty: the suite currently has no
timeout-detected mutants. The file exists to arm the verify's audit — a first
timed-out mutant here warns as an unaudited newcomer (a reviewer-stop, since
for a timed-out mutant the ratchet cannot see a weakened covering assertion)
instead of passing as an anonymous count. Admit a member only with its
structural cause written here (HARDENING.md, the audited-set bullet).

## Untriaged debt, in priority order

1. ~~**`precompiles.SignatureVerifyProgram` and the offsets records**~~ —
   *discharged 2026-07-19.* All 100 rows killed. `PrecompileOffsetsTests` covers
   the parse and resolve side (which record field lands at which byte, which
   buffer a component resolves against, and the bounds rejections in `slice`);
   `SignatureVerifyProgramTests` gained builder→parser round trips with
   **non-zero** payloads — the previous all-zero fixtures could not tell "copied
   correctly" from "never copied" — plus the two previously uncovered overloads
   and the `MAX_OFFSET` range boundaries. One mutant in `slice` was removed by
   refactoring instead: the `currentSentinel != -1` guard was dead, since every
   caller derives `instructionIndex` from a masked read and so can never produce
   `-1`.
2. ~~**`StakePoolState$Fee`, `StakePoolState`, `Token2022Instructions$UpdateTokenMetadataFieldIxData`,
   `stake.StakeAccount`**~~ — *discharged 2026-07-19.* The `isSet` trio needed a
   multi-bit mask (a single-bit mask cannot distinguish "all bits" from "any
   bit"); `Fee.compareTo` needed a fee with a zero *denominator*, which ratios to
   zero and so collides with a zero-numerator fee under ratio comparison but not
   under raw-numerator comparison; the metadata field ordinal needed its
   enum-bounds case; `write` needed a non-zero offset to show it returns a length
   and not an end position. Five mutants here were not killable — see the
   equivalent-mutant section. `ValidatorStakeInfo.write` was picked up in passing
   for the same reason as the precompiles: a zero `unused` word written into a
   zeroed buffer hides a dropped write.
3. ~~**`NO_COVERAGE` in the client layer**~~ — *discharged 2026-07-19.* All of
   `SPLClient`/`SPLClientImpl`/`SPLAccountClient(Impl)`/`StakePoolProgramClient(Impl)`
   except the three blocked and two RPC-bound methods above. The builders are
   thin, so the property under test is *wiring*: every fixture uses a distinct
   key per role, account lists are asserted for order **and** signer/writable
   flags, and instruction data is decoded back through the generated `IxData`
   records rather than by re-deriving byte offsets. That shape is what catches
   the failure that matters here — not a crash, but an instruction that
   authorizes the wrong party. `SPLClient.createClient(SolanaAccounts.MAIN_NET)`
   needs no RPC connection, so this was volume rather than scaffolding.
4. ~~**Small `NO_COVERAGE` leftovers**~~ — *discharged 2026-07-19.*
   `NonceAccount`, `LockUp`, `StakePoolAccounts`, `ValidatorList`, `MemoProgram`.

### Two latent defects this work surfaced

Both were in code that had *no* test reaching it, which is the argument for
paying down `NO_COVERAGE` rather than treating it as cosmetic:

- **`LockUp.write` dropped the epoch** — it wrote `unixTimestamp` into the epoch
  slot, so `write` → `read` never round-tripped. `read` was always correct and is
  the only direction used in-repo, which is why nothing caught it. Fixed, with a
  round-trip test using two distinct non-zero fields.
- **`NonceAccount.setNonce` discarded the advance instruction** —
  `Transaction.prependIx` returns a new transaction rather than mutating in
  place, and the result was being dropped. The transaction got the nonce as its
  blockhash but no `AdvanceNonceAccount`, which the runtime rejects. Fixed by
  returning the new transaction; **this changes the method's signature from
  `void` to `Transaction`.**

### Resolved upstream: `authorizeStakeAccount*WithSeed`

`SPLClient.authorizeStakeAccountWithSeed` and
`authorizeStakeAccountCheckedWithSeed` used to throw
`ArrayIndexOutOfBoundsException` for **every** input: the generated
`AuthorizeWithSeedArgs.l()` / `AuthorizeCheckedWithSeedArgs.l()` omitted the
8-byte u64 length prefix their own `write()` emits ahead of the seed string, so
the caller under-allocated by exactly 8 bytes.

Fixed in idl-src-gen (`codama/StringTypeNode.generateLength` and the anchor
`string` equivalent — every size-prefixed string field's `l()` term was short by
its prefix, in both pipelines) and the `gen/` sources regenerated, which also
corrected `l()` in ~30 string-bearing records across idl-clients-bundle
(`ShortUrl`, `CreateVaultParams`, the squads `*Args` family, metaplex `Data`,
...). The previously `@Disabled` tests in `SPLClientTests` encoding the intended
contract are re-enabled and green.

Shrinking the baseline is always an improvement; growing it requires a reason
written here.

## Triaged equivalent mutants (accepted with reasons)

Rows are labeled per the sava-build convention (the verify resolves each
label by searching this file for its literal text):

| Label | Rows | Argument |
|---|---|---|
| `# zero-fast-path family` | 7 | redundant zero short-circuits in front of a division |
| `# fast-path-guard family` | 4 | a guard choosing the cheaper of two identical computations |
| `# equal-operands family` | 1 | a comparison boundary where both branches compute the same value |

### Redundant zero short-circuits in front of a division (5 mutants)

| Class | Method | Line |
|---|---|---|
| `stakepool.StakePoolState$Fee` | `toRatio(MathContext)` | 226 |
| `stakepool.StakePoolState$Fee` | `toRatio(int, RoundingMode)` | 231 |
| `stakepool.StakePoolState$Fee` | `toRatio()` | 236 |
| `stakepool.StakePoolState` | `calculateSolPrice(MathContext)` | 68 |
| `stakepool.StakePoolState` | `calculateSolPrice(int, RoundingMode)` | 74 |
| `core.math.SafeMath` | `mulDivU64(long, BigInteger, BigInteger, boolean)` | 183 |
| `core.math.SafeMath` | `mulDivU64(long, BigInteger, BigInteger, boolean)` | 183 |

The last two arrived on 2026-07-25 with `OrcaUtil.mulDivU64`, which moved here
whole; they were carrying the identical acceptance in the orca baseline. Its
guard is `amount == 0L || numeratorFactor.signum() == 0`, and each clause's
replace-with-**false** mutant falls through to compute the same zero the long
way (`0 * factor / denominator`, remainder zero so no rounding). The
replace-with-**true** siblings return zero unconditionally and are killed.

All five of the originals are `RemoveConditionalMutator_EQUAL_IF` against the **first** clause of
a two-clause guard: `numerator == 0 || denominator == 0`, and the
`totalLamports.signum() == 0 || poolTokenSupply.signum() == 0` analogue.

The second clause is the real guard — it prevents a division by zero. The first
is only a short-circuit, and it is unobservable: when the denominator is
non-zero, computing `0 / d` produces exactly what the short-circuit returns.
For the `BigDecimal` overloads this is identity, not merely equality, because
`stripTrailingZeros()` on any zero returns the cached `BigDecimal.ZERO`
instance — so even the `assertSame(ZERO, …)` assertions in `StakePoolStateTests`
cannot separate the two paths. For the `double` overload, `0L / (double) d` is
`0.0`.

Enumerating the input space confirms there is no distinguishing case:

| numerator | denominator | guard | mutant |
|---|---|---|---|
| `0` | `0` | `ZERO` (1st clause) | `ZERO` (2nd clause) |
| `0` | `≠ 0` | `ZERO` (1st clause) | `0 / d` → `ZERO` |
| `≠ 0` | `0` | `ZERO` (2nd clause) | `ZERO` (2nd clause) |
| `≠ 0` | `≠ 0` | divides | divides |

Accepted rather than refactored: deleting the clause would also be correct, but
it is a deliberate fast path that avoids `BigDecimal` division and allocation
for the common zero-fee case, and a mutation score is not a reason to give that
up. Note the same `numerator == 0` test in `Fee.compareTo` is *not* equivalent —
there it selects a genuinely different ordering, and it is covered.

### The saturating-subtraction boundary (1 mutant)

| Class | Method | Line | Mutator |
|---|---|---|---|
| `core.math.SafeMath` | `saturatingSubU64` | 152 | `ConditionalsBoundaryMutator` |

`saturatingSubU64` is `Long.compareUnsigned(a, b) < 0 ? 0L : a - b`, and widening
the comparison to `<=` moves exactly one case across: `a == b`, where the
subtraction the mutant skips would have produced `0` anyway. Equal operands make
the two branches agree, so no input separates them.

Distinguish this from the neighbouring `checkedAddU64` boundary, which looks
identical and is **not** equivalent: there the equality case (`b == 0`) selects
between returning a value and throwing, so it is a kill — pinned by
`checkedAddTreatsBothOperandsAsUnsigned`. The shared label is
`# equal-operands family`, and it means the branches agree *on a value*, never
that a boundary is merely hard to reach.

### The unsigned-widening fast path (4 mutants)

| Class | Method | Line | Mutators |
|---|---|---|---|
| `core.math.SafeMath` | `toUnsignedBigInteger` | 66 | `ConditionalsBoundaryMutator`, `RemoveConditionalMutator_ORDER_IF` |
| `core.math.SafeMath` | `toUnsignedBigDecimal` | 76 | `ConditionalsBoundaryMutator`, `RemoveConditionalMutator_ORDER_IF` |

Both methods are `value < 0 ? <reinterpret the bits> : <valueOf(value)>`, and
the two branches **agree on every input the guard can route either way**. The
`< 0` test is not a correctness branch at all: the reinterpretation is correct
for non-negative values too, and is merely more expensive, so the guard picks
the cheaper of two identical answers. Every mutant that widens the negative
branch is therefore equivalent by construction:

- `ORDER_IF` (always take the reinterpretation) is the unconditional
  implementation — correct, just slower. Its sibling `ORDER_ELSE` is **not**
  equivalent and is killed, because `valueOf` on a negative value returns a
  negative number rather than a `u64`.
- `ConditionalsBoundaryMutator` (`< 0` becomes `<= 0`) moves only the value
  `0` across, and both branches return zero there.

This is the structural shape to recognize, because it will recur: a guard
introduced purely as an optimization, whose arms compute the same value, can
never be killed from the outside — the only way to make it observable would be
to make one arm wrong. The alternative is to drop the guard and call
`ByteUtil.toUnsignedBigInteger` unconditionally, which erases all four rows —
that was tried on 2026-07-25 and reverted, because it costs an allocation per
non-negative read on the oracle and pool parsers, and sava-core's own javadoc
explicitly tells hot callers to keep the guard. Four permanently accepted rows
is the price of the fast path, recorded here so the trade is not re-litigated
from the CSV alone. Holding the guard in one place is also what stops each
parser from open-coding it — this family replaced nine hand-written copies.
