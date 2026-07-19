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
| 2026-07-19 | `clients` | 794 | 746 | 48 | 559/1358 (41%) | 89% |
| 2026-07-19 | `clients` | 747 | 681 | 66 | 608/1360 (45%) | 90% |

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

   Remaining, in rough value order: the rest of the client impls
   (`MeteoraDlmmClient(Impl)` ~65 still, `LoopscaleClientImpl` 17,
   `KaminoVaultsClient(Impl)` 20, plus `OrcaWhirlpoolsClient(Impl)` ~57 in the
   `orca` suite), the Jupiter
   response parsers (`JupiterSwapInstructions` 39, `JupiterSwapApiClientImpl`
   37), and `DlmmUtils` 22 — the last of which is mostly unreachable guards, and
   whose `variableFeeControl` is already widened to `long`, so there is no
   signedness bug hiding there.

   The pattern that works for the client impls is idl-clients-spl's: distinct
   keys per role, account lists asserted by *slot* rather than membership, and
   the invoked program asserted explicitly. Running score for this approach:
   nine real defects across the two modules — two in SPL, the scope client's
   transposed `initialize`, the lend client's wrong reserve mint, the vote
   client's mis-bound `newEscrow`, Marinade's unbounded list scan, Phoenix's
   global config standing in for the global vault, and marginfi's dead
   `clearEmissions` and off-by-one-slot `closeOrder`. Nearly all were a
   same-typed value in the wrong position, in code no test reached.

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
