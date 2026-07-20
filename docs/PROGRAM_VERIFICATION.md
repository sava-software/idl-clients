# Verifying a client against the program it calls

Long-form methodology split out of `AGENTS.md`, which keeps only the summary.
Read this when you are **investigating** — adding a client for a new program,
chasing an instruction that fails on chain, or auditing an existing one. Day-to-day
work does not need it.

The short version: an IDL is a lossy artifact and a *separate on-chain account*
that a deploy does not update. Three things can disagree — the deployed
bytecode, the published IDL, and the program's Rust source — and each
disagreement has a different fix.

---

## 1. Is the IDL describing the deployed program?

**Ask the program.** Simulate a transaction carrying nothing but a candidate
8-byte discriminator, with `sigVerify: false` and `replaceRecentBlockhash: true`.
The fee payer must be a real funded account, or simulation aborts with
`AccountNotFound` before reaching the program.

| Result | Meaning |
|---|---|
| `InstructionFallbackNotFound` (custom error 101) | **not deployed** — identical to what a garbage discriminator returns, so always probe one as a control |
| logs `Instruction: <Name>`, then fails on validation (102, 3005, …) | **deployed** |

Probe an instruction the candidate IDL *adds* and one it *removes* and both
halves are settled at once.

```shell
python3 tools/idl_probe.py          # sweeps every program in main_net_programs.json
```

The tool carries an `ACCEPTED_UNDEPLOYED` set for known-benign cases, so it
exits non-zero only on something new. Re-run it after any upstream deploy.

**This only works for Anchor-dispatch programs.** Native, Shank and pinocchio
programs have no fallback handler, so the control probe does not return 101 and
the tool reports them `INCONCLUSIVE`. Those need §2 instead.

### Two traps

- **"The on-chain IDL agrees with our generated code" proves nothing.** It shows
  our code matches *the IDL*, not the program.
- **A repo under a different org may still be the program's home.** Teams
  rebrand — marginfi's `mrgnlabs` became `0dotxyz`/p0 — so treat provenance as a
  question to answer, not a disqualifier. Dismissing that repo as "a fork" once
  hid two live client bugs.

### Weaker signals, and why they disappoint

- `ProgramData.last_deploy_slot` — `getAccountInfo` on the program (jsonParsed)
  gives the programData address; `getAccountInfo` on that with
  `dataSlice{offset:0,length:13}` decodes as `u32 enum (3) || u64 slot`;
  `getBlockTime` dates it. Shows the program was *touched* after the IDL, not
  what changed.
- Grepping the deployed `.so` for account-name literals gives real hits, but a
  discriminator scan of the same buffer matched only 13 of 88 known-present
  instructions — too noisy to rely on.
- A changed account struct often cannot distinguish versions: a new field carved
  from former padding reads as zero under both layouts.

### Choosing an `idlURL` override

Default to the on-chain IDL account — it is the only artifact bound to the
address we actually call. An IDL in a repo or SDK carries the opposite risk: the
default branch may describe code that is **not yet deployed**, which breaks the
client just as quietly because it still compiles.

An override needs the dispatch probe to show the deployed program is *ahead* of
its IDL, and the replacement to match what is deployed.

**Check how the URL versions itself.** A path tracking a branch
(`marinade_finance.json`) keeps following upstream; a version-pinned filename
(`marginfi_0.1.9.json`) silently freezes at that release and fails closed into
the staleness the override was meant to fix.

Current overrides and the evidence for each: `idl-clients-bundle/config/pitest/README.md`.

---

## 2. Does the account order match the program's Rust?

```shell
python3 tools/ground_truth.py anchor <rust-dir>          <Program.java>
python3 tools/ground_truth.py shank  <instructions.rs>   <Program.java>
```

This is what surfaced most of the account-ordering defects this repo has fixed.
A transposed pair of same-typed `PublicKey` accounts compiles cleanly, produces
two real addresses, and fails only on chain.

The tool is **assistive, not an oracle** — every difference needs triage, and
most are artifacts. Its docstring lists the traps; the ones that cost the most
time:

- **Auto-wired accounts.** The client resolves well-known programs and sysvars
  internally rather than taking them as parameters, so `rent` reads as
  `solanaAccounts.rentSysVar()`. Normalised, but extend `AUTOWIRED` rather than
  "fixing" the client.
- **Wrong-struct matches.** Structs are matched by name across a whole
  monorepo, so `PostMessage` from an example program can pair with the real one
  and report every slot as different. Check which file a struct came from before
  believing a wholesale mismatch.
- **`compared 0` is a failure to compare, not a pass.** CCTP suffixes its
  structs `Context`; use `--strip-suffix=Context`. The compared count is always
  printed for this reason.
- **A published IDL may not match the repo.** Orca's IDL declares a trailing
  `whirlpool_program` on all 66 instructions and its Rust declares it on none —
  verified against the live on-chain IDL, versions matching, so it is not
  staleness. Use `--drop-trailing=whirlpoolProgram`.

### Anchor specifics

Anchor **inlines** a nested `#[derive(Accounts)]` composite into the account
list, and `#[event_cpi]` appends `event_authority` + `program` to whichever
struct carries it — including a nested one, where the pair lands *mid-list*. A
naive field scan gets both wrong and reports phantom length mismatches. The tool
handles both.

Anchor signals an **absent optional account** by passing the *invoked program's*
id. The generated code emits that as `requireNonNullElse(k, invokedProgram)` or a
`k == null ? …` ternary; both are seen through.

### Shank specifics

Shank has no `#[derive(Accounts)]` struct — the order *is* the order of indexed
attributes on the instruction enum. Two consequences:

- Attributes wrap across lines and `desc = ".."` strings may contain
  parentheses, so a line-based regex silently drops accounts.
- Each account carries an **explicit index**, which is a free correctness check
  Anchor does not offer: after parsing, every instruction's indices must read
  `0..n-1`. The tool aborts if they do not, because a dropped account otherwise
  looks like a length mismatch and reads as a defect in *our* code.

### Programs that cannot be ground-truthed

Not every program has an independent source. Check before assuming a diff is
meaningful:

- **Meteora DLMM** — no `programs/` directory. `commons/src/lib.rs` uses
  `declare_program!(dlmm)`, generating its structs *from* `idls/dlmm.json`, and
  the TS client builds accounts via `program.methods(..).accounts({..})` against
  a transcription of the same IDL. Every public artifact derives from the IDL, so
  a diff would be circular. Compare their IDL copy to ours for staleness instead.
- **Loopscale** — no public repo.
- **Pyth Lazer, Switchboard On-Demand** — contracts absent from the repos we have.

---

## 3. Extra (`remaining_accounts`) conventions

An IDL cannot express accounts read from `ctx.remaining_accounts`, nor a
**trailing optional** account. Both are invisible to the generated builders, so
they live in a hand-written `*RemainingAccounts` helper with the derivation
recorded in its javadoc.

Existing helpers: `WhirlpoolRemainingAccounts`, `KaminoLendingRemainingAccounts`,
`KaminoVaultsRemainingAccounts`, `MeteoraDlmmRemainingAccounts`,
`MarginfiRemainingAccounts`, `TokenMetadataRemainingAccounts`.

Two shapes worth recognising, because both have already shipped as defects:

- **Variable-size groups.** Marginfi's risk engine reads one group *per bank*,
  and the group size is 1–5 depending on the bank's oracle setup and asset tag —
  not the flat `<bank, oracle>` pairs the obvious mental model suggests. A wrong
  count fails with `WrongNumberOfOracleAccounts`. A helper that validates each
  group against the bank it describes turns that into a build-time error.
- **Accounts consumed off the front.** Marginfi's `maybe_take_bank_mint` splits
  the *first* remaining account off on token-moving instructions and requires it
  to equal `bank.mint` — but only for Token-2022 banks; for SPL Token it consumes
  nothing and the mint must be **absent**. Getting it wrong fails with
  `T22MintRequired`.

When adding a helper, derive the layout from the Rust handler, cite the file in
the javadoc, and validate what you can at build time rather than leaving the
caller an opaque on-chain error.

---

## 4. Reporting issues in third-party Rust

Our job is to mirror the program's actual on-chain behaviour, not to critique
its code. When cross-checking a **third-party** program (anything not a
sava-software repo):

- **Only surface a genuine bug** — one we would open an upstream PR for. Say it
  precisely: file, symbol, wrong vs correct behaviour.
- **Do not report harmless divergences.** A builder whose account flags disagree
  with its own doc comment, dead code, style, naming — none of it is actionable.
  Match what the deployed program enforces (verify on-chain when in doubt) and
  move on. These reports are noise.

Issues in sava-software's own repos (sava, idl-src-gen, sava-build, …) are
reported and fixed normally.
