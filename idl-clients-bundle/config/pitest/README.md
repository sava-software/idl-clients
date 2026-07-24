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

## Mutator set, and the BigInteger/BigDecimal gap

The suites run PIT's **`STRONGER`** group. Decompiling
`StandardMutatorGroups` in the pitest jar gives its exact membership:
`DEFAULTS` + `CONDITIONALS_BOUNDARY`, `INCREMENTS`, `INVERT_NEGS`, `MATH`,
`RETURNS`, `VOID_METHOD_CALLS`, the four `REMOVE_CONDITIONALS_*`, and
`EXPERIMENTAL_SWITCH` — that last one being why `SwitchMutator` rows appear in
the baselines at all.

**What `STRONGER` does not include is the BigInteger/BigDecimal mutators.** They
live in `org.pitest.mutationtest.engine.gregor.mutators.experimental`
(`EXPERIMENTAL_BIG_INTEGER`, `EXPERIMENTAL_BIG_DECIMAL`) and must be named
explicitly.

That gap matters more here than in a typical codebase. `MathMutator` rewrites
*primitive bytecode arithmetic* — `IADD`, `ISUB`, `LMUL` and friends. Arithmetic
on `BigInteger`/`BigDecimal` is **method calls** (`add`, `subtract`, `multiply`,
`divide`), which those opcodes never touch. So every Q64.64 fixed-point
conversion, fee computation and liquidity estimate in `OrcaUtil`,
`WhirlpoolQuote` and `DlmmUtils` — the arithmetic most likely to hide an
off-by-one that silently misprices — was *completely unmutated* under the
default configuration. A high kill rate on those classes measured the
conditionals and the return values around the math, not the math itself.

pitest **1.25.8** fixed these mutators for Java 25 (they misbehaved before), so
enabling them is viable for the first time. Measured on 2026-07-20:

| Suite | `STRONGER` | + Big mutators | New | New killed | New survivors |
|---|---|---|---|---|---|
| `orca` | 541 | 655 | +114 | 110 (96%) | 4 |
| `clients` | 1367 | 1417 | +50 | 49 (98%) | 1 |
| `scope` | 354 | 354 | 0 | — | — |

Two things worth knowing before repeating this experiment:

- **`EXPERIMENTAL_BIG_DECIMAL` contributed nothing.** All 164 new mutations came
  from `EXPERIMENTAL_BIG_INTEGER`; the BigDecimal mutator fired zero times across
  all three suites, including `scope`, which is the most `BigDecimal`-heavy code
  in the module. Enabling it costs nothing but buys nothing here.
- **The existing tests already kill 97% of them**, which is a real endorsement of
  the property-style assertions: they were strong enough to catch a mutation
  class they were never aimed at. The five survivors are the actual new signal:

  | Class | Method | Line |
  |---|---|---|
  | `OrcaUtil` | `reverseApplyTransferFee` | 379 |
  | `OrcaUtil` | `sqrtFloor` | 492 |
  | `OrcaUtil` | `sqrtPriceX64ToTickIndex` | 681 |
  | `WhirlpoolQuote` | `tokenBFromLiquidity` | 249 |
  | `DlmmUtils` | `computeFeeFromAmount` | 414 |

  Note that `sqrtPriceX64ToTickIndex` survives despite the whole-domain
  round-trip sweep, so it is not merely a coverage gap.

**Enabled on 2026-07-20** for `orca` and `clients`, via `build.gradle.kts`:

```kotlin
mutators = "STRONGER,EXPERIMENTAL_BIG_INTEGER"
```

`scope` is left off `EXPERIMENTAL_BIG_INTEGER` because the mutator fires zero
times there; the same was measured for `idl-clients-spl`'s own suite (742
mutations with and without), so it is not enabled there either.
Of the five new survivors, three were killed and two accepted (below).

### `EXPERIMENTAL_NAKED_RECEIVER` (enabled 2026-07-23, all suites)

The structural sibling of the BigInteger gap: a call whose return type is its
receiver type is an *expression*, so `VoidMethodCallMutator` never fires on it —
builder-style writes, `StringBuilder.append` chains, `BigDecimal`/`BigInteger`
fluent math. sava-build 21.5.9's scripted trial
(`pitestMutatorTrial -PtrialMutators=EXPERIMENTAL_NAKED_RECEIVER`), 2026-07-23:

| Suite | Generated | Killed by existing tests | Unkilled |
|---|---|---|---|
| `scope` | 6 | 6 (100%) | 0 |
| `orca` | 119 | 113 (94%) | 6 |
| `clients` | 245 | 179 (73%) | 66 |
| `spl` (idl-clients-spl) | 19 | 15 (78%) | 4 |

Fired everywhere, so it is enabled everywhere. Every `SURVIVED` row from the
intake is now resolved (2026-07-23); only the 52 `NO_COVERAGE` rows on the
untested client-impl tranche (priority 3 below) remain `# untriaged`:

- **Killed — dropped `stripTrailingZeros` (5)**: `Fee.toRatio` /
  `StakePoolState.calculateSolPrice` (spl) and `KaminoUtil.fromSf` here. The
  recurring reason these survive is scale-blind `compareTo` assertions; the
  kills assert the canonical stripped form with `BigDecimal.equals`.
- **Killed — URL wiring and response mapping (4 + 2 siblings)**: the local
  `swapURI`, `executeUltraOrderURI`, and token `v2RecentTokenPath` resolves,
  and the query-only `quote` overload's `thenApply`. These survived a harness
  that *does* assert paths because the shared client is built in a field
  initializer under `PER_CLASS` lifecycle — coverage attaches to whichever
  test runs first, so PIT can never pair the wiring with the request that
  exercises it. `urlWiringIsCoveredFromInsideTheTest` (both client test
  classes) builds the client inside the test body. The same fixture divergence
  killed both `JupiterSwapInstructions.feePayer` guard siblings: every real
  response has setup payer == swap signer, so only a fixture where they differ
  can observe the documented setup-first preference.
- **Killed — `preSerialize` separator and closing brace (2)**: the fee/tip
  JSON both contain commas and braces, so `contains(",")`-style checks pass
  with the separator dropped; the test now asserts the exact
  `prioritizationFeeLamports` object built from the same `toJson()` calls.
- **Killed — `DlmmUtils.pow:200` initial mask (1)**: the Rust `u128` parameter
  boundary, pinned by `pow(base) == pow(base + 2^128)`.
- **Killed — `collectRewardsQuote:144` dropped `divide(poolLiquidity)` (1)**:
  a real gap — every prior fixture's emissions were sub-X64 dust that
  truncated to zero divided or not; the new fixture uses X64-scaled emissions.
- **Accepted (families below)**: the four redundant explicit `.GET()` calls
  (`HttpRequest.Builder` defaults to GET; killable only via an `extendRequest`
  that sets another method), `sqrtFloor`'s initial guess (sweep-verified),
  the three u128 truncation masks (tick masks sweep-verified over every valid
  tick; the rewards mask guard-bounded), `DlmmUtils.pow`'s two loop masks
  (bounded post-inversion), and `sqrtPriceX64ToTickIndex:681` joining the
  lower-error-margin family (since sweep-verified equivalent — see that
  section).

## Baseline composition (seeded 2026-07-18)

| Date | Suite | Rows | `NO_COVERAGE` | `SURVIVED` | Killed | Test strength |
|---|---|---|---|---|---|---|
| seeded 2026-07-18 | `orca` | 142 | 86 | 56 | 395/541 (73%) | 87% |
| seeded 2026-07-18 | `scope` | 272 | 232 | 40 | 25/352 (7%) | 37% |
| seeded 2026-07-18 | `clients` | 1107 | 1065 | 42 | 229/1357 (17%) | 85% |
| 2026-07-19 | `orca` | 109 | 55 | 54 | 430/541 (79%) | 88% |
| 2026-07-19 | `scope` | 42 | 1 | 41 | 305/354 (86%) | 87% |
| 2026-07-19 | `clients` | 794 | 746 | 48 | 559/1358 (41%) | 89% |
| 2026-07-19 | `clients` | 747 | 681 | 66 | 608/1360 (45%) | 90% |
| 2026-07-19 | `clients` | 722 | 665 | 57 | 632/1359 (47%) | 92% |
| 2026-07-19 | `clients` | 655 | 590 | 65 | 707/1367 (52%) | 91% |
| 2026-07-19 | `clients` | 643 | 578 | 65 | 719/1367 (53%) | 92% |
| 2026-07-19 | `orca` | 100 | 46 | 54 | 439/541 (81%) | 89% |
| 2026-07-20 | `clients` | 591 | 517 | 74 | 774/1367 (57%) | 91% |
| 2026-07-20 | `clients` | 574 | 499 | 75 | 791/1367 (58%) | 91% |
| 2026-07-20 | `orca` | 100 | 46 | 54 | 553/655 (84%) | 91% |
| 2026-07-20 | `clients` | 574 | 499 | 75 | 839/1415 (59%) | 92% |
| 2026-07-23 | `orca` | 108 | 46 | 62 | 666/774 (86%) | 91% |
| 2026-07-23 | `scope` | 49 | 1 | 48 | 314/363 (86%) | 87% |
| 2026-07-23 | `clients` | 645 | 554 | 91 | 1043/1688 (61%) | 92% |
| 2026-07-23 | `orca` | 106 | 46 | 60 | 668/774 (86%) | 91% |
| 2026-07-23 | `clients` | 635 | 554 | 81 | 1053/1688 (62%) | 92% |

The second pair of 2026-07-23 rows is the NAKED_RECEIVER survivor triage (see
that section): every intake `SURVIVED` row killed or accepted with a reason,
including two bonus kills of pre-existing rows the strengthened assertions
reached (`preSerialize:46`, `collectRewardsQuote:128`).

| 2026-07-23 | `clients` | 436 | 361 | 75 | 1250/1688 (74%) | 94% |
| 2026-07-23 | `clients` | 57 | 19 | 38 | 1630/1688 (96%) | 97% |
| 2026-07-23 | `orca` | 52 | 0 | 52 | 722/774 (93%) | 93% |

The 57-row 2026-07-23 entry is the priority-3 discharge — the client-impl and
request/response tranche worked down from 436 rows. In order of volume:
Marinade (wiring tests mirroring the generated builders, plus the
validator-list reverse-lookup sort pinned with descending keys and the
exact-fit scan boundaries), Loopscale (full wiring + PDA property tests — 27
rows to zero), the Jupiter request builders (every builder accessor read from
inside a test body; the whole `rest.request` package now kills 322/322),
RouteV2Data/JupiterQuote/JupiterSwapIx (deterministic companions to the fuzz
harness; both v2 discriminators, `amountsMatch` quadrants, price-helper
delegation), MeteoraPDAs (property tests for the four pair flavors),
JupiterVoteClient (locker-admin/proposal builders, the `ClaimProof`-derived
overloads, the partial-unstaking identity fallbacks, and the ASR envelope
parser), the lend/borrow clients (full positional mirrors, including the
33-account `operate`), and the HTTP client builders (a direct test subclass
observing `setDefaults`, plus build-time URL resolution — see below). Every
remaining row is annotated in the CSV; the `# untriaged` marker is gone.

**One shipped defect surfaced**: `JupiterVoteClientImpl.newVote(proposal,
payer)` passed the *escrow* as the `new_vote` voter argument while deriving
the vote key from the escrow *owner* — a pair the program rejects
unconditionally (the vote PDA is `["Vote", proposal, voter]`), so the
two-arg overload could never execute. Verified against mainnet before fixing:
real Vote accounts carry the owner's wallet as `voter` (system-owned, and each
sampled voter owns an escrow), and the derivation is now pinned to on-chain
Vote `8XCPonZ4rVg914tNFFXBnLActCnQY4863LmAqwD5ykdK` in `JupiterAccountsTests`.
Same shape as the mis-bound `newEscrow` this client already had: a same-typed
key in the wrong slot, in code no test reached.

**A prior acceptance was retracted as wrong**: `setRemoteURLs`'s
default-endpoint guard was recorded as "observable only by omitting the
endpoint — the test would hit the hosted API". False: every Jupiter builder
resolves its URLs at *build* time, so a no-endpoint `createClient()` observes
the default without issuing any request (`ClientBuildersTests`
`defaultEndpointsResolveAtBuildTime` now pins the hosted and local URL sets
exactly, killing that whole guard family plus the price/token equivalents).

New accepted families (annotated in the CSV): the four `MeteoraPDAs`
min/max-sort `ConditionalsBoundary` mutants (at `compareTo == 0` the two mints
are identical, so either assignment yields the same seed pair — the boundary
is unreachable with distinct mints and harmless without),
`OracleUtil.scalePrice`'s unsigned-decode fast path (both branches produce the
same `BigDecimal` for any non-negative long; the branch only avoids string
parsing), `MeteoraDlmmClientImpl.hostFeeInOrSentinel` (the generated
`swap2Keys` already substitutes the program id via `requireNonNullElse`, so
the hand-written sentinel is belt-and-braces the callee duplicates), and the
`MarginfiRemainingAccounts.Builder` capacity-hint arithmetic (same
allocation-size-only family as Kamino's).

The `orca` 52-row 2026-07-23 entry is the `OrcaWhirlpoolsClient(Impl)` block
discharge plus the `WhirlpoolQuote`/`WhirlpoolRemainingAccounts` triage — the
suite's last `NO_COVERAGE` rows are gone, and every remaining row is a
documented `SURVIVED` equivalent. `OrcaWhirlpoolsClientWiringTests` mirrors
all 28 impl builders (including both two-hop swaps) and checks every `default`
overload against the explicit call it delegates to — the property pinned is
which derived key (position PDA, ATA, lock config, bundled position, oracle)
lands in which slot, and that the position families derive their ATAs under
the right token program (classic vs 2022 differs per family). The quote kills
each needed a case the ported Rust tests had no reason to construct: quotes
with *reversed* tick arguments (the sort is only observable when the input is
unsorted), an increase quote with a real `TransferFee` (grossing *up* the
estimate), an exact division under `roundUp` (delta = sqrtPriceUpper with
tick-0's exact 2^64 lower bound), the u64 boundary from both sides, and a
`liquidity * growthDelta` product landing on exactly u128::MAX.

**One shipped defect surfaced (fixed)**: `WhirlpoolQuote.toU64` used
`longValueExact`, which rejects the valid u64 range above `Long.MAX_VALUE` —
any fee, reward, or token estimate in `[2^63, 2^64)` threw
`ArithmeticException` instead of quoting. This is the *same* defect found and
fixed in `OrcaUtil.toU64` on 2026-07-16; the private sibling copy in the quote
layer was missed. Both boundary tests now pin the unsigned-bits contract
(`u64::MAX` survives, one past it throws). New accepted families are
annotated in the CSV: the quote zero fast paths whose fall-through computes
the same shared `ZERO` result the long way, `orderTicks` at equal operands,
the rewards u64 mask under the u128 guard, `toU64`'s unreachable-negative
half, and the remaining-accounts guards subsumed by their callees
(`Instruction.extraAccounts` and `addSlice` both drop empty input themselves).

The 436-row 2026-07-23 entry is two follow-up passes over the `NO_COVERAGE`
tranche. (1) **Parser unknown-field alignment**: every Jupiter REST
request/response parser fed fixtures with unknown neighbors at each nesting
level (`JupiterResponseParserAlignmentTests`, `ClaimProofTests`, and the
request-record parse tests) — 34 targeted rows plus ~100 collateral kills, and
the newly-covered triage that followed pinned the route `percent`/`bps` null
sentinels, the full audit field set, and the `stats` suffix arithmetic.
(2) **Remaining-accounts wiring**: `MeteoraDlmmRemainingAccountsTests`
(bin-array ranges, transfer-hook slice/meta pairing, the u8 slice-length
boundary), Kamino lend/vaults append order and flags, the Marginfi
fetched-`Bank` overload, Marinade `claimTickets`, and the Jupiter remote-URL +
`extendRequest`-composition path. The fluent-return `NullReturnVals` cluster
on builder methods dies by asserting the chaining contract (`assertSame` on
each returned builder).

The 2026-07-23 rows fold in two sava-build 21.5.9 effects at once: the
`EXPERIMENTAL_NAKED_RECEIVER` intake (above), and the ratchet's comparison
tightening from unique-row to **multiset** — sibling mutants sharing a
`class,method,line,mutator` key are now counted, so 11 pre-existing survivors
the unique comparison had collapsed became visible rows (7 `scope`
equals/hashCode siblings, 2 `orca` line-663 siblings, 2 `clients`). They are
annotated in the CSVs and folded into their families below — surfaced debt,
not new mutants.

**The seeded baseline is triage debt made explicit, not acceptance — and it is
now fully discharged across all three suites.** Every `SURVIVED` row remaining
in `scope`, `orca` and `clients` is analyzed (see the accepted-equivalents
section and the per-row CSV annotations); the only `NO_COVERAGE` rows left in
the module are the `clients` RPC-fetcher delegations, and `orca` has none.

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
3. **`NO_COVERAGE` in RPC-facing plumbing** — *in progress; 1107 rows → 794,
   17% → 41% killed as of 2026-07-19.* Done so far:

   - **Address constants and PDA helpers** (`KaminoAccounts`, `JupiterAccounts`,
     `MarinadeAccounts`). These are tested by *property* rather than by pinned
     address, since AGENTS.md forbids deriving a PDA helper's expected output
     from the IDL alone: a derivation must be deterministic, every input must
     participate (change one, the address moves), same-shaped neighbours must be
     separated (Marinade's four authorities take identical `(program, state)`
     inputs and differ only by seed suffix), and each `default` overload must
     bind the *correct* program — Kamino's helpers span three. The exact seed
     encodings stay pinned against real mainnet accounts in the per-program PDA
     tests.
   - **`MarinadeProgramClient`'s pure helpers.** `accountIndex` scans a raw list
     account past an 8-byte discriminator at an item-size stride, and the index
     it returns is passed straight into instructions acting on that validator or
     stake account. Tested for stride, discriminator skipping, key-only
     comparison, and per-list item sizes. **Fixed**: the scan had no tail bound
     check, so a truncated account — or an item size disagreeing with the data —
     read off the end and threw `ArrayIndexOutOfBoundsException` instead of
     reporting "not found". It now stops when fewer than a whole key remains.
   - **`JupiterVoteClient`.** The client spans three programs (locked-voter,
     governance, merkle distributor), so the property under test is which one
     each builder invokes. **Fixed**: `newEscrow` builds a `LockedVoterProgram`
     instruction but invoked the *governance* program — the escrow PDA is
     derived under the vote program, so it could never execute. Its twelve
     sibling locked-voter builders all bind the vote program, which is precisely
     what made the odd one out invisible. The interface's `newClaimAndStake`
     parameter names also disagreed with the implementation's (`distributor,
     claimStatusKey, fromKey` vs the actual `claimStatusKey, fromKey,
     distributor`); behavior was correct — the default overload matches
     positionally — but the names were a trap for a fresh implementor, and are
     now aligned.
   - **`KaminoLendClient`.** Its distinctive shape is *sentinel substitution*:
     absent optional accounts are replaced by a program id to keep the
     positional account list intact, with two different sentinels in play (the
     kLend program for oracles and referrers, the farms program for scope
     prices). Each substitution is asserted independently, by slot, so a
     swapped-in sentinel at the wrong oracle position is caught — that failure
     yields a plausible wrong price rather than an error. Both null sentinels
     (`PublicKey.NONE` and Kamino's `nu111…`) are covered alongside `null`.
   - **Jupiter quote and Ultra-order requests.** The query string *is* the
     request: a dropped parameter silently reverts to a Jupiter-side default and
     a misspelled key is ignored rather than rejected. Every parameter is
     asserted under its own API name (which differs from the accessor name —
     `inputMint`, not `inputTokenMint`), every optional one is asserted absent
     when unset, and the five Ultra account parameters (`taker`/`receiver`/
     `payer`/`closeAuthority`/`referralAccount`) use distinct keys so a
     transposition between them is visible.

   - **Jupiter request builders** (`JupiterQuoteRequest`,
     `JupiterUltraOrderRequest`, `JupiterSwapRequest`). The serialized request
     *is* the call: a dropped parameter silently reverts to a Jupiter-side
     default and a misspelled key is ignored rather than rejected. Every
     parameter is asserted under its own API name (which differs from the
     accessor name — `inputMint`, not `inputTokenMint`), every optional one is
     asserted absent when unset, and mutually-exclusive pairs are pinned
     (`dexes` beats `excludeDexes`; `destinationTokenAccount` beats
     `nativeDestinationAccount`; `dynamicComputeUnitLimit` suppresses an
     explicit unit price). The swap body inverts the usual polarity — three of
     its booleans default to *true*, so an omission means "leave the API
     default", not "unset".
   - **`MeteoraDlmmClient.deriveBinAccounts`.** Liquidity operations span a
     range of bins whose covering bin-arrays must be appended as extra
     accounts; too few and the program cannot reach its bins, wrong indices and
     it touches the wrong liquidity. The range→array map floors toward negative
     infinity, so the tests cover ranges straddling zero and landing exactly on
     an array boundary. Its swap builder uses the same optional-account sentinel
     convention as Kamino (absent host fee → the DLMM program id).

   - **`MarginfiClient`.** Its convenience overloads derive three keys off one
     `bank` — the liquidity vault, that vault's *authority*, and the caller's
     ATA — and `withdraw`/`borrow` take the vault authority in the slot directly
     *before* the vault. Transposing two same-typed PDAs of the same bank
     compiles and produces two real addresses, so only a positional assertion
     catches it. The tests also pin the two genuine differences between the
     keypair and PDA account variants (the derived account cannot sign; the PDA
     form carries the instructions sysvar) and the fact that `placeOrder`'s
     position banks are instruction *data*, not accounts.

   - **`JupiterSwapInstructions`.** The response-to-transaction merge writes
     into an array sized by `numInstructions()`, with deliberately asymmetric
     index arithmetic — the leading loops post-increment, the swap is written
     *without* advancing, and everything after pre-increments, so a null cleanup
     leaves no hole. Every combination of present/absent cleanup and
     empty/non-empty lists is now driven, along with the parsers' rewind-and-retry
     field lookup (`skipUntil` then `reset(0).skipUntil`, which finds a field
     positioned before the cursor). **Fixed**: `createAccountsMap` read the fee
     payer from `setupInstructions.getFirst()` unconditionally, so a response
     with no setup instructions — which Jupiter returns whenever the ATAs already
     exist and no SOL wrapping is needed — threw `NoSuchElementException` out of
     `serializeTransaction`. The setup instruction is still preferred (it funds
     the ATA, so its first account is the payer by construction); otherwise the
     wallet is recovered from the swap instruction's signer.

   - **`JupiterVoteClient`'s convenience overloads.** Two shapes recur across
     the client: read the four keys out of a fetched `Escrow` (locker, address,
     owner, token account), or fall back to the client's own escrow identity.
     They are the ergonomic entry points a caller actually reaches for, and none
     were covered — the existing tests all used the explicit full-argument forms,
     which is why the rows read as `NO_COVERAGE` despite the method names
     appearing in the suite. Each overload is now checked against the explicit
     call it delegates to, with every `Escrow` field varied independently, since
     all four are `PublicKey` and a transposition compiles. `increaseLockedAmount`
     gets an explicit anti-symmetry assertion: its short form supplies the
     escrow's ATA as destination and the owner's as source, and swapping them
     would move tokens the wrong way.

   - **`OrcaWhirlpoolsClient`'s `default` overloads.** The full-argument
     builders are thin delegations, but the short forms *derive* accounts the
     caller would otherwise compute — the position PDA, the owner's ATA for the
     position mint, and the whirlpool's oracle. Each is now checked against the
     explicit call it delegates to, with the derivations themselves tested by
     property (deterministic, every input participates, distinct from
     same-shaped neighbours and from the mint/pool they derive from).

     A note on where *not* to spend effort here: `OrcaUtil`'s remaining 31 rows
     were re-examined and are the already-documented equivalents below
     (BigInteger shift symmetry, log-approximation precision headroom). A
     round-trip sweep of ~3600 ticks across the whole domain plus a strict
     monotonicity check was added anyway — it killed nothing, as expected, but
     it is a far better regression guard than the ten hand-picked ticks it
     joins, and it is the test that would catch a real change to the tick math.
     **Read the accepted-equivalents section before picking a target**; these
     rows look like untested math and are not.

   - **`JupiterSwapApiClient` over a mock HTTP server** — 34 rows -> 1. There
     was no HTTP mocking in this module, so the harness (`JupiterRestTests`) is
     adapted from sava-rpc's `RpcRequestTests`: an in-JVM `HttpServer` on an
     ephemeral port, a queue of expected exchanges, and an `@AfterEach` that
     fails on anything left unconsumed — so a client that silently stops issuing
     a request cannot pass. The differences from the RPC version are that Jupiter
     is REST (match on method + path + body, not a JSON-RPC envelope) and that
     the response *status* has to be controllable, because the client's own
     error handling keys off it. Build the client with `createLocalClient()`,
     which uses unprefixed paths.

     Two pieces here are logic rather than plumbing. `swap-instructions` gates
     its raw response on an explicit `200 <= status < 300` before handing back
     bytes, so an error page cannot be mistaken for instruction data; the
     `>= 300` boundary is pinned at exactly 300. And the dex-label map inverts
     `programId -> label` into a case-insensitive `label -> programId`, throwing
     on a collision rather than letting one DEX silently overwrite another's
     program id.

   - **`JupiterTokenClient`** — 7 rows -> 0, and it took the
     `JupiterTokenV2$Parser` rows with it, so the suite dropped 17. Every method
     on this client is a *path builder*: the response parsing is shared, so what
     distinguishes `search` from `forTag` from `forCategory` is only the URL
     each resolves. A wrong segment returns a well-formed token map from the
     wrong endpoint, so the tests assert the exact path and query rather than the
     parsed result alone. `forCategory` is the one worth care — category and
     interval are *path* segments while the limit is a query parameter, so its
     three arguments each land somewhere different and transposing the first two
     still yields a valid URL for a different listing.

     `JupiterRestTests` is the shared harness for this and the swap client;
     subclasses build their own client against its `endpoint`. Note that
     `JupiterPriceClientTest` predates it and has its own bespoke `HttpServer`
     setup — it is already at zero rows, so it was left alone rather than
     churned onto the shared base for no gain.

   - **The `EXPERIMENTAL_BIG_INTEGER` intake** (see the mutator section above).
     Three of the five new survivors were killed, and each needed a case the
     existing tests had no reason to construct:
     `OrcaUtil.reverseApplyTransferFee` — the recovered fee feeds *only* the
     max-fee cap, so with an unbounded cap the subtraction is unobservable; it
     took a cap the correct fee stays under but the mutated one trips.
     `WhirlpoolQuote.tokenBFromLiquidity` — the round-up flag is detected by
     masking off the low 64 bits, and `and` and `or` agree on every input except
     a product that is a whole multiple of 2^64, so the test constructs one via
     `gcd`. `DlmmUtils.computeFeeFromAmount` — the u64 mask is a no-op below
     2^63, so it only becomes observable for a negative `long` denoting a u64
     above that.

   Remaining in this suite: nothing untriaged — the client-impl tranche was
   discharged 2026-07-23 (see the 57-row baseline entry above). The one block
   left from priority 3 is `OrcaWhirlpoolsClient(Impl)` ~57 rows, which lives
   in the `orca` suite's baseline, not this one. `DlmmUtils`'s remaining rows
   are the documented unreachable-guard family; its `variableFeeControl` is
   already widened to `long`, so there is no signedness bug hiding there.

   The pattern that works for the client impls is idl-clients-spl's: distinct
   keys per role, account lists asserted by *slot* rather than membership, and
   the invoked program asserted explicitly. Running score for this approach:
   ten real defects across the two modules — two in SPL, the scope client's
   transposed `initialize`, the lend client's wrong reserve mint, the vote
   client's mis-bound `newEscrow` and dead two-arg `newVote` (the escrow in
   the voter slot), Marinade's unbounded list scan, Phoenix's global config
   standing in for the global vault, and marginfi's dead `clearEmissions` and
   off-by-one-slot `closeOrder`. Nearly all were a same-typed value in the
   wrong position, in code no test reached.

Shrinking the baseline is always an improvement; growing it requires a reason
written here.

## Ground-truthing account order against the programs' Rust

Anchor account order is positional and fixed by the `#[derive(Accounts)]` field
order, so a program's Rust source is the authority. With the reference clones
present (see `AGENTS.local.md`), the generated key builders were diffed against
it mechanically — extract each struct's fields, flatten nested `Accounts`
composites the way Anchor does, append the `event_authority` + `program` pair
for any struct carrying `#[event_cpi]` (including a *nested* one, where the
pair lands mid-list), then compare positionally to each `*Keys(...)` method.

Result — **150 instructions verified, all matching** after one fix:

| Program | Instructions | Result |
|---|---|---|
| klend | 59 | all match |
| kfarms | 24 | all match |
| kvault | 20 | all match |
| marinade-finance | 27 | all match *(after re-pointing the IDL — see below)* |
| jupiter-lend | 3 | all match (reference CPI files, not Anchor structs) |
| phoenix (`rise-public`) | 12 | one fix — see below |
| marginfi-v2 | 75 | two fixes — the on-chain IDL was stale, see below |

Phoenix's Rust is not Anchor: `rise/rust/ix/src/*.rs` build their metas by hand
in numbered `build_accounts()` blocks, so those were the authority — and, per the
staleness sweep below, the *only* authority available, since the dispatch probe
cannot speak to non-Anchor programs.

The full diff (14 builders, all 16 `build_accounts` blocks) needed two extractor
fixes before it meant anything: the helpers `push_trader_index_accounts` /
`push_writable_accounts` append accounts **inline at their call position**, so
without expanding them every builder looked mis-ordered by two slots; and
`if let Some(..)` pushes are *optional* trailing accounts. With those modelled,
**every account order matched**. Two flag differences remained:

- **`SyncParentToChild.traderWallet` — a real defect, and an IDL one.** The IDL
  declares it `signer: false`; the SDK pushes `AccountMeta::readonly_signer`. The
  generated builder faithfully followed the IDL, so the fix belongs in the
  hand-written layer, which now rebuilds the metas. Checked across every mapped
  Eternal instruction: this is the *only* signer disagreement, so it is an
  upstream omission rather than a systemic gap. It survives in the wild because
  the trader wallet is usually also the fee payer, and message compilation then
  marks it a signer regardless — masking the bug for most callers.
- **`CancelStopLoss.globalConfiguration`** — the IDL marks it writable where the
  SDK has it read-only. Harmless: an unnecessary write lock, not a failure. Left
  as-is rather than diverging from the IDL for no behavioural gain.

The earlier pass had already found the fund-movement bug: they showed
`deposit_funds` / `withdraw_funds` taking the per-mint **global vault** in a slot
where our client passed `eternalGlobalConfig()` — the same value it already
supplies two slots earlier. The vault's seeds are not in the IDL; they come from
the program's own `constants.rs::get_global_vault_address` (`["vault", mint]`),
now `PhoenixAccounts.globalVaultPDA`. Both methods gained a `globalVaultKey`
parameter (**breaking**).

### A flaky mutant, and the misattributed fix

The mock-HTTP suites made the `clients` kill count wander (855/856/858 across
runs), which the ratchet caught as `JupiterClientBuilder.extendRequest`
appearing as a *new* unkilled mutant after a baseline happened to be written
from a lucky run.

**The real cause was field-initializer coverage.** The only caller of
`extendRequest` was the test base's client field initializer, and coverage
attributed to a field initializer is unstable under PIT — the same shape that
bit the factory `NullReturnVals` mutants earlier. The fix is exercising it from
inside a `@Test`, which also produced a genuinely useful assertion: the builder
attaches the `x-api-key` auth header, so a null return means every request goes
out unauthenticated.

**A second fix was applied at the same time and was a no-op** — recorded here
because the original version of this note attributed the convergence to it.
`@Execution(SAME_THREAD)`/`@TestInstance` were copied from the abstract test
base onto the concrete classes on the theory that neither annotation is
`@Inherited`. That claim is version-dependent and false here: at JUnit 6.1.2
both carry `@Inherited` (verified in the resolved jar's bytecode, not docs),
and `@Execution` is moot regardless because parallel execution is not enabled
in this repo. The duplicate annotations have been removed; the base's own
annotations remain, and would take effect through inheritance if parallel
execution were ever turned on. One `javap` settles this class of question
before any test restructuring *(HARDENING_CASEBOOK: @Inherited is
version-dependent)*.

Post-correction convergence was re-verified: consecutive runs agree, and per
the shared doc's method the report directories were checked to actually
re-generate between runs (they do here — the pitest tasks re-execute on
unchanged inputs, so back-to-back runs are real comparisons).

A wandering kill count is worth chasing rather than re-ratcheting past: the
baseline records whichever run wrote it, so a lucky run bakes in a row that
later runs fail on.

### Fuzz corpus replay (open item)

The shared doc now expects committed seed corpora to be replayed inside
`check`. `stakePoolState`'s seed is loaded by `StakePoolStateTests` already;
the `scopeReader` corpus (2 seeds) is read only by the fuzz harness, so it can
rot between fuzz runs. A small replay test in the scope suite would close it —
see json-iterator's `TestFuzzCorpusReplay` for the pattern.

### Shank programs: Metaplex and SAS (2026-07-20)

The last two of the eight programs the dispatch probe cannot reach. Both are
**Shank**, not Anchor: account order is declared as indexed attributes on the
instruction enum rather than in a `#[derive(Accounts)]` struct, so they need
`extract_shank.py` rather than `extract_rust.py`.

| Program | Instructions | Result |
|---|---|---|
| Solana Attestation Service | 12 | all match |
| Metaplex Token Metadata | 58 | 57 match, 1 IDL gap |

Two things the extractor has to get right:

- **Attributes wrap across lines**, and `desc = ".."` strings may contain
  parentheses, so a line-based regex silently drops accounts. The parser is
  position-aware and quote-aware over the whole file.
- **Shank declares an explicit index per account**, which is a free correctness
  check the Anchor path does not have: after parsing, every instruction's indices
  must read `0..n-1`. That check caught the multi-line drift on SAS immediately —
  worth keeping, since a dropped account otherwise looks like a length mismatch
  against the IDL and reads as a defect in *our* code.

**`print` is missing two accounts, and it is an IDL gap rather than a bug.** The
Rust declares 20; the IDL declares 18, omitting both trailing **optional**
accounts:

| Index | Account | Flags |
|---|---|---|
| 18 | `holder_delegate_record` | optional |
| 19 | `delegate` | optional, **signer** |

Together they let a *holder delegate*, rather than the token holder, authorize
printing an edition. Since the IDL omits them the generated positional builder
has no parameters for them, so that authority path was unreachable. Added
`TokenMetadataRemainingAccounts.printHolderDelegate(..)`, which supplies the pair
in program order with the delegate as a signer, to be appended to the account
list. This is the same shape as Phoenix's optional `permission_account`: the IDL
cannot express a trailing optional, so the hand-written layer carries it.

### Squads, CCTP, Pyth and the Wormhole shims (2026-07-20)

Six more programs diffed against newly cloned Rust. **45 instructions verified,
zero defects.**

| Program | Instructions | Result |
|---|---|---|
| Squads V4 | 18 | all match |
| CCTP Message Transmitter V2 | 15 | all match |
| Pyth Solana Receiver | 7 | all match |
| Pyth Push Oracle | 1 | all match |
| Wormhole Verify VAA Shim | 3 | all match |
| Wormhole Post Message Shim | 1 | verified by hand — order *and* flags |
| Pyth Lazer | 0 | Solana contract is not in `pyth-crosschain` |

Three extractor traps, all worth knowing before repeating this:

- **Match structs by program, not by name.** A monorepo contains several
  programs, and `extract_rust.py` matches on struct name alone. It paired our
  `postMessage` with `PostMessage` from
  `anchor/programs/wormhole-integrator-example` — a *different* program that CPIs
  into the shim — producing an alarming all-slots-differ diff that was pure
  noise. Same trap hit Pyth Lazer, where the matched `Initialize` came from
  pyth-solana-receiver.
- **The shims are not Anchor.** They are hand-rolled for CU efficiency, so the
  wire order lives in the `AccountMeta::new(..)` sequence inside
  `crates/shim/src/post_message.rs::instruction()`, not in a `#[derive(Accounts)]`
  struct. Checked against that: `core_bridge_config, message, emitter, sequence,
  payer, fee_collector, clock, system_program, wormhole_program, event_authority,
  program` — exactly our IDL, with `bridge` = `core_bridge_config`, and every
  writability and signer flag agreeing.
- **CCTP suffixes its account structs `Context`** (`AcceptOwnershipContext` for
  `acceptOwnership`), so a name-keyed comparison silently matches nothing and
  reports a clean zero. Strip the suffix before comparing — "0 compared, 0
  differ" is a failure to compare, not a pass.

Squads' only two flagged instructions were also artifacts: `config_transaction_execute`
declares `rent_payer` and `system_program` as `Option<..>`, and the generated
code implements Anchor's absent-optional convention (substitute the invoked
program id) via a ternary the extractor could not parse. `extract_java2.py`
handles both that shape and `requireNonNullElse(..)`.

### Orca and Meteora: what could and could not be ground-truthed

**Orca — 61 instructions, zero ordering defects.** The clone at
`orca/whirlpools` carries the real program (`programs/whirlpool/src/instructions`,
Anchor `#[derive(Accounts)]`). All 17 reported differences were the auto-wired
class already seen with jupiter-lend and marginfi — `rent`,
`associated_token_program` and `memo_program` resolved internally by the client
instead of taken as parameters — with positions matching exactly. The memo
program was checked rather than assumed: the IDL pins
`MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr` and `solanaAccounts.memoProgramV2()`
is that address, so v2-vs-v1 is correct.

One structural surprise worth recording: the **published IDL declares
`whirlpool_program` as a trailing account on all 66 instructions, and the repo's
Rust declares it on none.** Verified against the live on-chain IDL account, not
just our stored copy. Versions match (0.9.0 both, repo HEAD 2026-07-15), so this
is not staleness — the published IDL simply does not correspond 1:1 to the
repo's `#[derive(Accounts)]`. Our client follows the IDL, which is what every
other Orca client does too. Normalise this away before reading an Orca diff or
it swamps the real signal.

**Meteora — cannot be ground-truthed at all.** `dlmm-sdk` contains only
`commons` (math) and `cli`; there is no `programs/` directory, and
`commons/src/lib.rs` opens with `declare_program!(dlmm)`, which generates the
SDK's account structs *from `idls/dlmm.json`*. The SDK is therefore a sibling of
our generated client, not an independent source — diffing against it would be
circular. The program itself is closed-source. What *is* worth doing, and was
done: comparing their IDL copy to ours as a staleness check. Identical —
version 0.12.0, 76 instructions, every account list matching.

### Extra (`remaining_accounts`) coverage

Four programs already had helpers with derivation notes: `WhirlpoolRemainingAccounts`,
`KaminoLendingRemainingAccounts`, `KaminoVaultsRemainingAccounts`,
`MeteoraDlmmRemainingAccounts`. **Marginfi had none, and its client javadoc was
wrong.**

The javadoc said the risk engine reads `<bank1, oracle1, bank2, oracle2, ...>`.
The program's own `get_remaining_accounts_per_bank` says the group size is *per
bank*, from one to five:

| Bank | Accounts |
|---|---|
| `OracleSetup.Fixed` | 1 — bank only |
| `FixedKamino` / `FixedDrift` / `FixedJuplend` | 2 — bank + venue state |
| asset tag `DEFAULT`(0) / `SOL`(1) | 2 — bank + oracle |
| asset tag `KAMINO`/`DRIFT`/`SOLEND`/`JUPLEND`(3-6) | 3 — bank + oracle + reserve |
| asset tag `STAKED`(2) | 5 — bank + oracle + lst mint + stake pool + onramp |

A wrong count fails on chain with `WrongNumberOfOracleAccounts`. Separately,
`maybe_take_bank_mint` splits the **first** remaining account off on the
token-moving instructions and requires it to equal `bank.mint` — but only for
Token-2022 banks; for SPL Token it consumes nothing and the mint must be absent.
Getting that wrong fails with `T22MintRequired`. Transfer-hook accounts trail
everything, since the program forwards the whole slice to the transfer CPI.

Fixed by correcting the javadoc and adding `MarginfiRemainingAccounts`, a builder
that validates each group against the bank it describes, so a miscount throws at
build time with the expected and actual counts rather than surfacing as an opaque
on-chain error.

### Marginfi: a stale on-chain IDL hiding two live client bugs

The diff reported 30 mismatches against `0dotxyz/marginfi-v2`; 27 were extractor
artefacts (auto-wired `solanaAccounts.*()` sysvars and program ids the client
resolves internally). Three were real: `kamino_init_obligation` (23 accounts vs
our 27), `lending_pool_add_bank_permissionless` (17, adding `pool_onramp` and
`validator_vote_account`), and `panic_pause` (`pause_authority` vs
`global_fee_admin`).

**The first pass got this wrong** and is worth recording as a trap. The
reasoning was: the on-chain IDL is 0.1.8 and matches our client exactly, the repo
is 0.1.9, and `0dotxyz` is not `mrgnlabs` — therefore a fork describing
undeployed code, so leave it. Two errors. `0dotxyz`/**p0** is the marginfi team
after a rebrand, not a third party. And "the on-chain IDL matches our client"
only proves our client matches *the IDL* — it says nothing about the program.
The IDL is a separate account that a deploy does not update.

**The program is the authority, and it can be asked directly.** Simulate a
transaction carrying just an 8-byte discriminator with `sigVerify: false` and
`replaceRecentBlockhash: true` (the fee payer must be a real funded account, or
simulation aborts with `AccountNotFound` before reaching the program):

- **Not deployed** → `InstructionFallbackNotFound` (error 101), byte-identical to
  a garbage discriminator.
- **Deployed** → the program logs `Instruction: <Name>` first, then fails later
  on account/arg validation (102, 3005, ...).

That probe showed `lending_account_clear_emissions` returning 101 while
0.1.9-only instructions (`lending_pool_emissions_deposit`,
`init_global_fee_state_v2`) dispatched: **the deployed program is 0.1.9.** Two
shipped client bugs followed, both in code no test reached:

| Method | Defect |
|---|---|
| `clearEmissions` | 0.1.9 removed the instruction. Every call failed with 0x65. Wrapper **deleted**. |
| `closeOrder` | 0.1.9 prepends `group`, shifting all five accounts down one slot. Now passes `marginfiGroup()` (**breaking**: the generated builder gained a `groupKey` parameter). |

Everything else the client wraps — deposit, repay, withdraw, borrow, flashloans,
`placeOrder`, the account lifecycle — is byte-identical across the two versions.

Fixed by pointing the config at the IDL the team publishes in their TS SDK and
regenerating (31 files, 0.1.8 → 0.1.9):

```json
"idlURL": "https://raw.githubusercontent.com/0dotxyz/p0-ts-sdk/refs/heads/main/src/idl/marginfi_0.1.9.json"
```

⚠️ **This URL is version-pinned.** Unlike Marinade's `marinade_finance.json`,
which tracks `main`, a future 0.1.10 lands at a *new filename* and this override
silently freezes at 0.1.9 — failing closed into exactly the staleness it was
added to fix. Re-run the dispatch probe on a 0.1.9-only instruction after any
marginfi deploy, and bump the path when the version changes.

Techniques that did *not* settle this, for the record: grepping the deployed
`.so` for account-name string literals found all three disputed names, but a
discriminator scan of the same buffer matched only 13 of 88 known-present
instructions, so byte-level searching is too noisy to rely on. And the live
`FeeState` could not distinguish the versions — 0.1.9's new
`pause_delegate_admin` is carved from the old `reserved1` and is currently zero,
which both layouts predict.

### Bundle-wide staleness sweep (2026-07-19)

Marginfi's `clearEmissions` was a *dead* method — not wrong, but incapable of
succeeding — and nothing in the mutation baseline could have found it. So the
dispatch probe was run across every configured program to bound how much more of
that exists: **1026 declared instructions across 34 Anchor programs**, each
probed for `InstructionFallbackNotFound`, with a garbage-discriminator control
per program to confirm the program is Anchor-dispatch-shaped before trusting any
verdict.

**Result: zero actionable defects.** Two instructions are declared but not
deployed, both benign and neither wrapped by hand-written code:

| Program | Instruction | Why it is fine |
|---|---|---|
| Meteora DLMM | `for_idl_type_generation_do_not_call` | A stub — one `dummy_zc_account`, one `_ix` arg — existing only to force the IDL to emit zero-copy types. Never deployed by design. |
| Switchboard On-Demand | `pull_feed_submit_response_svm` | An other-SVM-chain variant not enabled on Solana mainnet; its four `pull_feed_submit_response*` siblings all dispatch. |

Marginfi re-probes clean (100 instructions, 0 dead), which also serves as the
positive control: the same sweep would have flagged `clearEmissions` before the
fix.

**Eight programs are INCONCLUSIVE** — their garbage-discriminator control did not
return 101, meaning they do not use Anchor's dispatch (native, Shank, or a real
fallback handler): Jupiter Swap, Metaplex Token Metadata, Phoenix Ember, Phoenix
Perpetuals (+ Dev), Solana Attestation Service, and the two Wormhole shims. The
probe cannot speak to these; they need the Rust-diff treatment instead — which is
exactly how Phoenix's global-vault bug was found, so the gap is real rather than
theoretical.

Worth re-running after any upstream deploy. The script lives in the session
scratchpad (`idl_staleness_sweep.py`); committing it would mean whitelisting a
new tracked-file kind in `.gitignore`, which is a deliberate decision and has not
been taken.

### Marinade: a stale *on-chain* IDL

The diff first showed `update_deactivated` one account short — the Rust
`UpdateCommon` carries `validator_list` as its 13th field (added Aug 2023), but
our IDL modelled `common` as 12 accounts, shifting `operational_sol_account` and
`system_program` down a slot.

The cause was not a stale local file. Marinade's program has **no IDL committed
in `liquid-staking-program`** (any branch, any point in history), so the config
fetched it from the on-chain IDL account — and that account has not been
re-uploaded since before Aug 2023, while the program itself was last deployed
**2026-07-16**. The team upgraded the bytecode without refreshing the IDL, so
the on-chain IDL describes a program that no longer exists.

Verified by decoding the `ProgramData` account's `last_deploy_slot`
(433290841 → 2026-07-16) against the IDL account's contents, and cross-checked
against the Rust at both `main` and `mainnet`. Beyond the missing account, the
stale IDL exposed a `redelegate` instruction the deployed program no longer has,
and omitted `create_canonical_stake` / `finalize_delinquent_upgrade`, plus the
`delinquentUpgrader` field on `State` and `delinquentUpgraderActiveBalance` on
`ValidatorRecord`.

Fixed by pointing this one entry at the IDL Marinade publish in their official
TS SDK (`marinade-ts-sdk`, updated 2026-06-25 with "align SDK with program
upgrade"):

```json
"idlURL": "https://raw.githubusercontent.com/marinade-finance/marinade-ts-sdk/refs/heads/main/src/programs/idl/json/marinade_finance.json"
```

### Policy: the on-chain IDL stays the default

**Keep fetching from the on-chain IDL account.** It is the only artifact bound
to the program address we actually call, and a team failing to re-upload it on
deploy should be the exception, not the assumption. An IDL in a repo or SDK
carries the opposite risk: `main` may describe code that is **not yet deployed**,
which breaks the client just as badly and more subtly, because everything still
compiles.

So an `idlURL` override needs evidence that the on-chain IDL is stale *and* that
the replacement matches what is deployed — read from the chain, not from the
repo. For Marinade that was:

1. **The program is newer than its IDL.** Decode `ProgramData.last_deploy_slot`
   — `getAccountInfo` on the program (jsonParsed) gives the programData address;
   `getAccountInfo` on that with `dataSlice{offset:0,length:13}` and base64 gives
   `u32 enum (3 = ProgramData) || u64 last_deploy_slot`; `getBlockTime` dates it.
   Marinade: slot 433290841 → 2026-07-16, versus an IDL account describing
   pre-Aug-2023 code.
2. **Live account data matches the new layout, not the old.** The upgrade
   appended `delinquentUpgrader` to `State` at offset 638, exactly where the old
   layout ended. On the live state account
   (`8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC`) byte 638 is `2` — the `Done`
   variant ordinal — and it is the *only* non-zero byte past the old boundary.
   Under the old layout the program would never write there, so this is the
   deployed program's own output confirming the field exists.

Without both, prefer the on-chain IDL and leave the discrepancy documented
instead. Re-verify step 2 if the SDK IDL later moves ahead of a deploy.

The same diff also confirmed the Kamino oracle sentinels are correct: klend's
`RefreshReserve` and kfarms' `RefreshFarm` both declare their optional accounts
as `Option<...>`, and Anchor signals an absent optional account by passing the
*invoked program's* id — which is exactly why `refreshReserve` substitutes the
kLend program and the farms builders substitute the farms program. The generated
`refreshReserveKeys` already encodes this as
`requireNonNullElse(oracle, invokedProgram)`; the hand-written layer adds the
mapping from Kamino's *semantic* null keys (`PublicKey.NONE`, `nu111…`) onto
that positional convention, which `requireNonNullElse` alone would not catch.

## Triaged equivalent mutants (accepted with reasons)

### Row-label legend (2026-07-23, sava-build 21.5.12 convention)

Every accepted row carries a short `# <name> family` label; the verify summary
counts rows per label, and the full argument for each family lives in the
sections below. The families:

| Label | Argument (section below / note) |
|---|---|
| `zero-fast-path` | a redundant short-circuit; the fall-through computes the identical result (zero the long way, the shared `ZERO` quote constants, `scaled <= 0` doubles, the unsigned-decode branch, `rangeClosed(lo,lo)`, a full bitmap byte scanned bit-by-bit) |
| `defensive-guard` | guards a state no producer can construct (unreachable negative/equal halves, the tick-ladder's bit-19 iteration — `2 << 18` exceeds `MAX_TICK_INDEX`, so the factor lookup never runs — and the floor-division/`isValidStartTickIndex` branches already excluded by earlier checks) |
| `callee-subsumed-guard` | the callee performs the identical check or substitution (`Instruction.extraAccounts` on empty, `addSlice` dropping empty slices, `swap2Keys`'s `requireNonNullElse` host-fee sentinel, a zero denominator that `divide` rejects with the same exception) |
| `equal-operands` | a comparison boundary where both branches produce the same value (`orderTicks`/`orderPrices` at equal operands, clamps at their exact bound, the transfer-fee cap at exactly `maxFee`, tick 0 routing to either ladder, min/max mint sort with equal mints) |
| `shift-symmetry` | `BigInteger.shiftLeft(-n)` **is** `shiftRight(n)` |
| `log-approx-headroom` | extra precision iterations / the equality fast return cannot change the resolved tick |
| `log-margin` | the lower error margin — sweep-verified equivalent, see its section |
| `sqrtFloor-guess` | Newton seed variants — sweep-verified, see its section |
| `u128-mask` | truncation masks that are identity under the guards above them |
| `domain-guard` | DlmmUtils fee/pow bounds unreachable from `LbClmmConstants` |
| `hash-mixing` / `record-pattern` / `trim-on-exact-fit` | the scope families below |
| `capacity-hint` | allocation-size-only arithmetic feeding an `ArrayList` capacity |
| `redundant-GET` | `HttpRequest.Builder` defaults to GET |
| `http-1xx-unreachable` | the JDK client never surfaces a 1xx final status |
| `rpc-fetcher` | one-line RPC delegations; need a live `SolanaRpcClient` |
| `needs-reserve-fixture` | `readPriceChains(Reserve)`, a two-line delegation awaiting a full kamino `Reserve` fixture |

### BigInteger `sqrtFloor` initial guess (2 mutants, orca)

`OrcaUtil.sqrtFloor:492` seeds Newton's integer square root with
`value.shiftRight(1)`; the `EXPERIMENTAL_BIG_INTEGER` mutant seeds it with
`shiftLeft(1)`, and the `NakedReceiver` mutant drops the shift entirely,
seeding with `value` itself. The iteration `next = (prev + value/prev) / 2`
descends monotonically to `floor(sqrt(value))` from *any* starting point at or
above the true root, and `v/2`, `2v` and `v` all qualify for `v >= 2` (`v < 2`
returns early). Only the iteration count changes.

Verified as well as reasoned: the variants were reimplemented and compared over
200,490 inputs — every value below 200,000 plus `2^e ± 3` for `e` in 60..129 —
with **zero** differences; the `guess = v` variant additionally over 122,765
cases (0..1999, `2^k ± 1` for k in 2..256, and 120k random values up to
256 bits), all also agreeing with `math.isqrt`.

### BigInteger tick-index lower error margin (2 mutants, orca)

`OrcaUtil.sqrtPriceX64ToTickIndex:681` computes `tickLow` as
`logbpX64.subtract(LOG_B_P_ERR_MARGIN_LOWER_X64)`; the `BIG_INTEGER` mutant
adds instead, and the `NakedReceiver` mutant drops the subtraction —
`tickLow = floor(x)` rather than `floor(x - 0.01)`, which diverges in the same
`frac(x) < 0.01` sliver the analysis below covers, so both siblings share it.

**Sweep-verified equivalent over the whole valid domain (2026-07-23;
previously "accepted as untriaged").** Writing `x = logbpX64 / 2^64`, the
margins are ~0.01 and ~0.856 of a tick, so `tickLow = floor(x - 0.01)`, the
mutants' `floor(x)` / `floor(x + 0.01)`, and `tickHigh = floor(x + 0.856)`.
Working through every case: the only reachable divergence is when
`frac(x) < 0.01` collapses the mutant's `tickLow` onto `tickHigh` and skips
the refinement that would have chosen `tickLow` — which requires the 14-bit
log approximation to *overshoot*: some price `p` below the tick-`k` boundary
with `x(p) >= k`. Because `x(p)` is (weakly) monotone in `p`, overshoot at
boundary `k` is equivalent to `x(sqrtPrice(k) - 1) >= k` — one evaluation per
boundary is an exhaustive search. `tools/tick_margin_sweep.py` (a Python
mirror of both ladders, pinned to `MIN/MAX_SQRT_PRICE_X64` and tick 0) ran
all 887,272 boundaries: **zero overshoots**, monotonicity violated nowhere.
The approximation only ever errs downward in the valid domain, so the lower
margin never changes an output — it is genuine safety margin, kept as such.
`fuzzOrcaTickMath` independently drives the same bracketing contract on the
unmutated code (21M+ random in-range prices, clean). Re-run the sweep if the
log constants, margins, or factor tables ever change.

### HTTP 1xx unreachable through the JDK client (1 mutant, clients)

`JupiterSwapApiClientImpl`'s raw `swap-instructions` handler rejects anything
outside `200 <= status < 300`. The `>= 300` half is tested at exactly 300; the
`< 200` half is not, and cannot be through this harness — `java.net.http`
treats 1xx as *interim* responses and never surfaces one as a final status, so a
server replying 199 just hangs up and the exchange dies with an `EOFException`
before the client's own check runs.

This is accepted as **untestable in-harness rather than equivalent**: a real 199
would distinguish the mutant, so the guard is not dead code, and it should stay.
Killing it would need a raw-socket stub speaking HTTP/1.1 by hand instead of an
`HttpServer`, which is not worth it for one mutant.

### DlmmUtils fee and Q64.64 `pow` domain equivalents (17 mutants, clients)

Every remaining conditional in `DlmmUtils` guards a state its own inputs cannot
reach. The bounds come from `LbClmmConstants`: `FEE_DENOMINATOR = 10^9` and
`MAX_FEE_RATE = 10^8`, with `getTotalFee` capped at the latter.

- **`computeFee:398`** (`denominator.signum() <= 0`, 2 mutants). `denominator =
  10^9 - totalFeeRate` and `totalFeeRate <= 10^8`, so it is never below
  `9 x 10^8`. The underflow guard cannot fire.
- **`computeFee:404` / `computeFeeFromAmount:417`** (`fee.bitLength() > 63`,
  4 mutants). `amt` is masked to u64, so the worst case is
  `(2^64 - 1) x 10^8 / 9 x 10^8 ~ 2^60.8` and `(2^64 - 1) x 10^8 / 10^9 ~ 2^60.7`
  respectively — both bounded at 61 bits, short of the 63-bit guard and of the
  62/63 boundary a `>=` mutant would test. (`computeProtocolFee`'s matching
  guard *is* reachable, because `protocolShare` scales by up to 10000/10000
  rather than dividing down — it is tested, and asserts the guard's own message,
  since without the guard `longValueExact()` throws `ArithmeticException` too.)
- **`computeVariableFee:365`** (`variableFeeControl() <= 0`, 2 mutants). The
  accessor is a widened u32, so it is never negative; at exactly zero the
  fall-through computes `0 x sqVfaBin`, ceil-divided to `0` — the same
  `BigInteger.ZERO` the guard returns.
- **`pow:194`** (`exp < 0` -> `<= 0`). `exp == 0` returns at line 191, so the
  mutated boundary is unreachable.
- **`pow:195`** (`exp == Integer.MIN_VALUE ? 1L << 31 : Math.abs((long) exp)`).
  Equivalent *because the cast to `long` precedes the `abs`*:
  `Math.abs((long) Integer.MIN_VALUE)` is already `2^31`, so removing the special
  case changes nothing. The branch documents the hazard rather than avoiding it.
- **`pow:205`** (`squaredBase.signum() == 0`). Reached only inside
  `squaredBase >= Q64X64_ONE`, i.e. `>= 2^64`, which is never zero.
- **`pow:213`** (`bit < 19` -> `<= 19`). `absExp < Q64X64_MAX_EXPONENTIAL = 2^19`,
  so bit 19 is never set; a twentieth iteration only squares `squaredBase` and
  never reaches `result`.
- **`pow:219`** (`result.bitLength() > 128`, 2 mutants). After the inversion
  step `squaredBase <= 2^64`, and each update is `(result x squaredBase) >> 64`
  masked to 128 bits, so `result` stays at 65 bits — far below both the guard and
  its boundary.
- **`pow:215` / `pow:218`** (`NakedReceiver` dropping the loop `.and(U128_MASK)`,
  2 mutants). Identity by the same bounds: post-inversion `squaredBase <= 2^64`
  and `result <= 2^65`, so both `>> 64` results already fit far inside 128 bits.
  The *initial* mask at `pow:200` is different — it is the Rust `u128` parameter
  type boundary, observable to a Java caller passing a bit-128 base — and is
  killed by `powExactValues` asserting `pow(base) == pow(base + 2^128)`.
- **`binIdToArrayIndex:60`** (`binId < 0` -> `<= 0`). At `binId == 0` the second
  conjunct `(0 % MAX_BIN_PER_ARRAY) != 0` is false, so both spellings yield `idx`.

These are guards worth keeping — they are cheap, and they document the domain —
but no input a caller can construct distinguishes them from their mutants.

### Hash-mixing arithmetic in hand-written hashCode (34 mutants, scope)

`MathMutator` hits on the `31 * result + component` mixing steps in
`MostRecentOfEntry`, `CappedMostRecentOf`, `Conditional`, `NotYetSupported`,
`ScopeEntriesRecord`, and `PriceChainsRecord`. Any deterministic function of
the compared components satisfies the `hashCode` contract — the tests assert
equal-objects-equal-hashes and that each component perturbs the hash, and both
properties hold under any mixing formula. Killing these would mean asserting
literal hash values, which restates the implementation. Four of the 34 are
same-coordinate siblings (a `31 * result + c` line holds one multiplication and
one addition mutant) surfaced by the 21.5.9 multiset comparison — same
reasoning, annotated `# hash-mixing sibling` in the CSV.

### Record-pattern deconstruction conditionals (13 mutants, scope)

`RemoveConditionalMutator_EQUAL_IF` on the `o instanceof Type(...)` record
deconstruction lines in the same equals methods. The tests cover a matching
twin, a mismatching variant per component, a null, and a different type; the
surviving conditionals are the compiler-synthesized component-extraction checks
inside the pattern, which cannot take their alternate branch once the
`instanceof` has matched. Three of the 13 are same-coordinate siblings (one per
synthesized extraction check on the deconstruction line) surfaced by the
21.5.9 multiset comparison — annotated `# record-pattern sibling` in the CSV.

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

### Log-approximation precision headroom (5 mutants, orca)

The `precision < BIT_PRECISION` loop bound and `precision++` in
`sqrtPriceX64ToTickIndex`, and the `tickLow == tickHigh` fast return: the
tick is derived from a 14-bit log approximation with error margins
(`LOG_B_P_ERR_MARGIN_*`) sized so that *extra* iterations or taking the slow
path cannot change the resolved tick — verified by round-trip tests across the
full tick range including both extremes. Two of the 5 are same-coordinate
siblings on the line-663 comparison surfaced by the 21.5.9 multiset
comparison — annotated `# log-approx headroom sibling` in the CSV.

### u128 truncation masks that cannot fire (3 mutants, orca)

The `NakedReceiver` mutants dropping `.and(U128_MASK)` in
`sqrtPriceFromPositiveTick:599` / `sqrtPriceFromNegativeTick:612`, and
`.and(U64_MAX)` in `collectRewardsQuote:173`. The tick masks mirror the Rust
reference's `as_u128()` truncation, but no valid tick reaches it:
**sweep-verified** — both variants reimplemented from the factor tables and
compared over every tick in `[-443636, 443636]`, zero differences (invalid
ticks are rejected by the public entry point's bounds guard, pinned by the
guard-rejection tests). The rewards mask sits directly under the
`product > U128` overflow guard, so `product >> 64` already fits u64 and the
mask is identity by the guard above it.

### Rewards-quote domain equivalents (4 mutants, orca)

`collectRewardsQuote`'s zero fast path (`rewardGrowthDelta == 0 ||
liquidity == 0` computes zero the long way too), the `poolLiquidity != 0`
accrual skip under a zero-emission fixture, and the `product > U128` overflow
boundary at exactly `U128` (Rust's `unwrap_or(0)` mirror), plus
`orderTicks`/`orderPrices` at equal operands where both orderings are the same
pair.
