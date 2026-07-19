# Mutation-testing baseline & triage policy — `idl-clients-spl`

Each `pitest<Suite>` run is finalized by `pitest<Suite>Verify`, which diffs the
run's unkilled mutants (`SURVIVED` and `NO_COVERAGE`) against the accepted
baseline in `<suite>-accepted.csv` and **fails on anything new**. Baseline row
format: `class,method,line,mutator,status`. The canonical policy is sava-build's
`HARDENING.md`; this file records what is accepted *here* and why.

A new unkilled mutant has exactly three legal outcomes:

1. **Kill it** — add or strengthen a test. Prefer asserting the property the
   mutant breaks (a length prefix rejected, an offset record's exact bytes, the
   state a parsed account reports) over restating the implementation.
2. **Refactor** — restructure so the mutant cannot exist.
3. **Accept it knowingly** — re-run with `-PupdateMutationBaseline` and record
   the reason under "Triaged equivalent mutants" below. Acceptance is for
   mutants *equivalent with respect to observable behavior*, not for "hard to
   test".

Line numbers are part of the baseline key, so unrelated edits to a mutated file
shift entries: the verify task then reports both stale and "new" rows. Confirm
the new rows are the shifted old ones, then refresh with
`-PupdateMutationBaseline`. Baseline rows are deduplicated by key, so the
suite's row count is at or below the mutant count its report prints.

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

## Baseline composition

| Date | Rows | `NO_COVERAGE` | `SURVIVED` | Killed | Test strength |
|---|---|---|---|---|---|
| seeded 2026-07-18 | 293 | 258 | 35 | 429/743 (58%) | 92% |
| 2026-07-19 | 175 | 170 | 5 | 563/741 (76%) | 99% |
| 2026-07-19 | 10 | 5 | 5 | 732/742 (99%) | 99% |
| 2026-07-19 | 7 | 2 | 5 | 735/742 (99%) | 99% |

The baseline was seeded with the full pre-existing survivor population when the
ratchet was adopted, per HARDENING.md's adoption path — triage debt made
explicit, not acceptance. **That debt is now discharged.** All three priorities
below were worked down; every one of the 7 remaining rows is analyzed:

| Rows | Group | Status |
|---|---|---|
| 5 | Redundant zero short-circuits | equivalent — accepted, reasoned below |
| 2 | `fetchProgramState` / `fetchValidatorList` | need a live `SolanaRpcClient` |

The two RPC fetchers are one-line delegations to `rpcClient.getAccountInfo`
with a parser reference; covering them means standing up an HTTP mock for no
behavior this module owns. Left uncovered deliberately.

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

### Redundant zero short-circuits in front of a division (5 mutants)

| Class | Method | Line |
|---|---|---|
| `stakepool.StakePoolState$Fee` | `toRatio(MathContext)` | 225 |
| `stakepool.StakePoolState$Fee` | `toRatio(int, RoundingMode)` | 230 |
| `stakepool.StakePoolState$Fee` | `toRatio()` | 235 |
| `stakepool.StakePoolState` | `calculateSolPrice(MathContext)` | 67 |
| `stakepool.StakePoolState` | `calculateSolPrice(int, RoundingMode)` | 73 |

All five are `RemoveConditionalMutator_EQUAL_IF` against the **first** clause of
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
