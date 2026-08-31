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

**Ask the program.** Simulate a transaction carrying nothing but the candidate
dispatch key, with `sigVerify: false` and `replaceRecentBlockhash: true`. The fee
payer must be a real funded account, or simulation aborts with `AccountNotFound`
before reaching the program.

**Send the key at its declared width.** Read it from the IDL entry —
`"discriminator": [...]` or `"discriminant": {"type": "u8", "value": n}` — and do
not pad. Padding can still dispatch while shifting the argument boundary, which
is how Exponent's clients were wrong; `idl-clients-bundle/config/pitest/README.md`
has that case.

**Establish the dispatcher's oracle before reading anything into the result.**
What an error means is a property of the program, not of Solana, and this
document does not attempt to enumerate it — Anchor's own dispatch, including the
user `#[fallback]` path, is in
`anchor/lang/syn/src/codegen/program/dispatch.rs`. Two things are worth knowing
before you start, because both have produced confident wrong answers here:

- An error matching what an undeclared key returns does **not** prove absence
  unless the dispatcher distinguishes "no such instruction" from "ran and
  rejected the empty arguments". The Solana Attestation Service returns
  `InvalidInstructionData` for both.
- A differing error does not prove presence either, if the program has a user
  fallback that can answer two absent keys differently.

Where neither can be established, the honest result is inconclusive. That is not
a gap to work around; it is most of why the automated probe was removed —
`tools/README.md`.

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
  `dataSlice{offset:0,length:45}` decodes as `u32 enum (3) || u64 slot ||
  Option<Pubkey> upgrade authority`; `getBlockTime` dates it. Shows the program
  was *touched* after the IDL, not what changed — and *touched* rather than
  *deployed* is deliberate, because the runtime writes this field too. Read the
  authority tag at offset 12 for context, never as a licence to skip the slot: a
  `00` there says no transaction can upgrade the program, and says nothing about
  whether the executable will be replaced. The fourth worked example below is a
  program where the tag is `00` and the slot has moved anyway.
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

An override needs independent evidence that the deployed program is *ahead* of
its IDL, and the replacement to match what is deployed. The dispatch check above
is one way to get it, and often will not settle anything — where the dispatcher
gives no oracle, it cannot. Marinade's override rested on the deploy slot plus
live account layout instead. Any evidence that is actually conclusive will do;
the requirement is on the evidence, not on the method.

**Check how the URL versions itself.** A path tracking a branch
(`marinade_finance.json`) keeps following upstream; a version-pinned filename
(`marginfi_0.1.9.json`) silently freezes at that release and fails closed into
the staleness the override was meant to fix.

**It fails the other way too, and that is the one that shipped.** A pinned
filename can also be *chased*: when upstream deletes it, the URL starts
answering 404, and the obvious repair — repoint at the new filename beside it —
generates the client from the version the team is preparing rather than the one
they deployed. Marginfi did exactly this on 2026-08-18 with no deploy behind it,
and the `.json` diff read as an ordinary IDL update. Neither direction is
detectable from the document; only `lastDeploySlot` distinguishes them.

A `vcs` source pinned with `commit` freezes deliberately, and does not fail closed:
the generator also reads the branch behind the pin as the `vcsHead` channel, so the
frozen input still generates while upstream movement is still reported. That is the
combination to reach for when a repository copy is the *deployed* description — a
program with no on-chain IDL — where an unpinned source would let an upstream edit
become the deployed client on the next regeneration with nothing to compare it
against. A pinned *filename* has no such counterpart and is still the trap above.

**That reporting half holds only while the pinned path still exists upstream.**
`headURL()` asks for the *same path* on the tracked branch, so a pin over a
deleted file — marginfi's `src/idl/marginfi_0.1.9.json`, gone from `main` since
p0's `d769882` — resolves to a 404, which `Entrypoint` logs as a WARNING and
skips. The run stays green, the client still generates from the pin, and no
`vcsHead` channel is ever recorded: change detection is gone, silently, in the
one case where the pin was forced rather than chosen. Where that happens, the
deploy-slot and payload-hash fields in `sources.json` are the whole of the
signal, which is the right thing to be watching anyway.

Current overrides and the evidence for each: `idl-clients-bundle/config/pitest/README.md`.

### If this ever becomes a service

Running the probe automatically — at generation time, or as a monitor — was
considered and deferred. One conclusion is worth not rediscovering.

**Probing only the instructions an IDL adds or removes is not sufficient.** That
diff compares IDL to IDL, but the defect lives between the IDL and the *deployed
program*, and a stale IDL is stale precisely because it stops changing. Marginfi
is the counterexample: re-fetching returned the same 0.1.8 IDL, so an
added/removed diff was empty and would have reported clean while
`clearEmissions` was dead on mainnet.

### A worked example: Kamino Lend, 2026-08-12

The one redeploy this repo has caught in the act, recorded here because the report
that first carried it is one commit in a branch and a squash would take it with it.

`ProgramData.last_deploy_slot` for `KLend2g3cP87fffoy8q1mQqGKjrxjC8boSyAYavgmjD` moved
**422723185 → 438843135**, and the on-chain IDL moved with it, version **1.23.0 → 1.24.0**:

| | sha256 |
|---|---|
| before | `7d8f55dca835e9c1890aec99e9d939241ca3df9bc3ef6006e138cbdda9bd6eb2` |
| after | `c229f91d6a79a4a100ce6adc51597eaf0688ed29fa2ff4a38a4238da7ad24875` |

Full hashes on purpose: a truncated prefix is enough to read a story and not enough to
re-verify one, and `sources.json` is the only other place this pair exists.

What changed: three instructions added (`calculate_ctoken_exchange_rate`,
`fill_borrow_order_v2`, `set_borrow_order_v2`), fifteen changed, the
`ExchangeRateWithDecimals` type added, six types changed, one error added and one changed.

Two things are worth keeping from it. The deployed client moved under us during unrelated
work — the case the deploy-slot signal exists for. The IDL-to-IDL diff *did* report the
movement here, since the document itself changed; what it could not do is establish that a
**redeploy** was the cause. That distinction matters because the failure mode this repo
keeps meeting is the other one: marginfi's IDL stopped changing while its program moved
on, so an IDL-to-IDL diff read clean. Movement in the document is evidence the document
moved, and only the deploy slot is evidence the program did.

And the staged (`next`) client *survived* the deploy — the SDK copy still differs from the
deployed document at the same version string — so a deploy does not retire a `next/`
package and the version string is not what decides it.

The generating run's own report is in commit `c2563a4`; `sources.json` carries the
slot and hashes at all times.

### A second worked example: the SPL programs, 2026-08-13

Five SPL clients moved their `vcs` hash on this branch, in **two separate generations**.
Recorded together because a squash flattens them into one diff in which four have their
evidence and the fifth does not, and telling those apart afterwards means re-deriving it
from `sources.json` blame.

**Event one — commit `c2563a4`, the parser-recovery run.** Four programs, all reported
in that commit's `idl-change-report.txt` under `== Movement (6) ==` (the other two are
Kamino's IDL move and redeploy, above).

| program | before → after |
|---|---|
| Address Lookup Table | `f1b98bfdea01eabba840427ca7461a790e70fbdc70d392a42d042f7e954bb6b0` → `ce3a6296a726387fb7d9167e06751395c133a44cc16603ded34b1ce7beef3a86` |
| Compute Budget | `72815adca6fb6f9127ccb31216a335a7bcb2ed955e710ebd033a767c60236489` → `a2f12fbebef6b7a467e71efc5d74c92198b3a63ceaf06953080cd0d583528fb2` |
| System | `2139d7aebc9bb6411af2c8c2d58d25df079b498ce5f199961dba1946e13849be` → `e6e1a93dee7b9e07c08bd177f452b77926509748299f9211e5ffa6aedc11998b` |
| Token | `94491b922b4c820f2f4f74228d067c5d48793dc6cebda495c83db61bfbe0370e` → `ce401c7ffa66b424bc6568dff5cc49adfb20db1ac486c91f756b6327218bbf74` |

These are the programs the silent-failure defect had been dropping — a codama `display`
node threw out of the parser, the exception escaped its worker without setting `fatal`,
and the run exited 0 with them missing. The first run after the parser fix compared each
against the record last written before they started vanishing, and **reported every one
of them.** The mechanism worked; nothing is owed here.

**Event two — commit `1722033`, Token-2022 only.**

| program | before → after |
|---|---|
| Token-2022 | `e3e5e9ffeb0ce567174e1e8995d7b8209d484bd9f8a0e5b79239c1a6d2b3db0f` → `f5f3375f2f19e831732e76792078cdee253206d4ba09fef3150c54b94bbd564f` |

This one has **no report entry**, and that is the rule above firing exactly as intended:
a changed `sources.json` hash with no matching report change means a generation ran
without retaining its channel-movement evidence. `1722033` is a test commit; a
regeneration inside it moved the hash and its report was not carried with it. Token-2022
is not one of the four above and did not move with them — it is later and separate.

**What changed, in both events, is presentational.** The added nodes are
`structFieldDisplayNode` (237 in Token-2022), `instructionAccountDisplayNode` (148),
`instructionDisplayNode` (103), `amountNumberDisplayNode` (21) and similar — codama's
rendering hints. Verified rather than assumed: every generated `.java` file under all
five packages is byte-identical to its previous version once whitespace is stripped, so
the visible Java diff is this branch's trailing-whitespace normalisation and not these
movements. No wire layout, discriminator, account order or field offset moved.

Two lessons, and the second is the one that cost a correction here. The deploy-slot
lesson, in a new place: **a channel that stops being read stops being compared, and
silence then looks like agreement.** And: *a missing report entry is a claim about a
specific generation*, so it has to be checked against that generation's report rather
than against the branch's net diff. An earlier draft of this section asserted all
five had lost their evidence and named Token-2022 among the parser-recovery four; both
were wrong, and reading `c2563a4`'s own report is what settles it.

The trigger to key on is the **deploy**, not the IDL edit:

| Signal | Probe scope |
|---|---|
| `ProgramData.last_deploy_slot` changed since the last clean probe | that program's full instruction set |
| IDL content changed | the added and removed instructions — the right check for validating a deliberate `idlURL` switch |
| neither | skip |

Cache `(program id, last_deploy_slot, idl hash) -> clean`. The deploy-slot read
is two `getAccountInfo` calls per program and batches 20 to a request, so the
steady state is a handful of round-trips against ~52 for a full sweep.
Programs outside the upgradeable loader — native, or loader v1/v2/v4 — have no
`ProgramData` account and cannot go stale by redeploy, so key those on the IDL
hash alone, but handle the absent account rather than erroring on it. In this
corpus that is two programs, System and Compute Budget, and `sources.json` names
them: `programDataState` reads `nativeProgram` for those two and `upgradeable`
for the rest.

**Do not extend that to a program that merely has no upgrade authority.** A
finalized loader-v3 program still has a `ProgramData` account, and a Core BPF
program still has its executable replaced — by the runtime, at feature
activation, with no transaction anywhere. Stake is both at once: `Option::None`
as its upgrade authority, and a `last_deploy_slot` that has moved under it. Key
those on the slot like every other program. The fourth worked example below is
how to tell which install put a slot there.

---

### A third worked example: Jupiter Swap, 2026-08-13

Caught in passing by a regeneration run, and recorded here because the report that carried it is
one commit in a branch: `idl-change-report.txt` reports movement as an *event*, so the next run
after it says "Nothing moved" and a squash leaves the receipt nowhere.

`ProgramData.last_deploy_slot` for `JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4` moved
**437349164 → 438982144**, and **no IDL channel moved with it**. Both channel hashes, in full, so
this survives the squash that will flatten the commit they were read from:

| | sha256 |
|---|---|
| `anchor`, before and after | `27a84dee64afa4527b4bf1c567babced3c0e59e538c1c10938042b30936ffbe1` |
| `vcs`, before and after | `c7353b35a80cd93cd4f73cf1f720524599ec126236627e7309f3008ea6d817ad` |

Unchanged is the whole point: the slot moved and the documents did not, so there is no diff to
read and nothing but the slot to notice it by.

That is the marginfi shape, and the reason the deploy-slot signal exists. An IDL-to-IDL diff reads
clean here, because the document genuinely did not change; only the program did. Per the table
above, a deploy-slot move with no IDL movement warrants probing that program's **full instruction
set**, not the added-and-removed subset — there is no subset to derive.

**Narrowed 2026-08-13, not closed.** Of the 17 declared discriminators, `route` and `route_v2`
are confirmed live by real mainnet transactions carrying them. The other 15 answered *differently*
from the control, which is weaker than it was first written up as: §1 no longer treats a differing
error as proof of dispatch, because a user `#[fallback]` can answer two absent keys differently and
Jupiter's dispatcher-specific oracle was never established. So 15 are observed-differing, not
confirmed. That is the whole of what was established, and it is narrower than "the
interface did not change", which an earlier revision of this section claimed: dispatch says nothing
about account lists, argument layouts or semantics, any of which a redeploy can move while every
discriminator still resolves. The upgrade landed at **2026-08-13T08:17:56Z**, the previous at
**2026-08-05T08:57:58Z** — an eight-day cadence, under an upgrade authority
(`CvQZZ23qYDWF2RUpxYJ8y9K4skmuvYEEjH7fK58jtipQ`) that has not changed. Consistent with a routine
release rather than an interface move — but only consistent with. Nothing here confirms it: 15 of
the 17 remain uncharacterised, and cadence plus a stable upgrade authority look identical either
way.

Two things had to be worked around during this investigation. Recorded because they are the
reasoning anyone simulating by hand still needs:

- **Calibrating on error 101 alone tells you nothing here.** Jupiter answers a garbage
  discriminator with `InvalidAccountData` at 71 compute units, and a *declared* discriminator with
  `InstructionDidNotDeserialize` (102) or `NotEnoughAccountKeys`. Error 101 is one shape of "no
  such instruction", not the only one, and `idl_probe.py` reported `INCONCLUSIVE` until it was
  taught the second. Widening it made the responses *readable*; it did not make them decisive —
  §1 no longer treats a differing error as proof of dispatch, which is why the conclusion above is
  2 confirmed and 15 uncharacterised rather than 17 live.
- **Logs are not a dispatch signal.** `route` and `route_v2` — the two most used instructions in a
  sample of recent traffic — emit no `Program log: Instruction: …` line, almost certainly because
  that `msg!` is stripped on the hot path to save compute. A scratch check written during this
  investigation required exactly that line, and so read a program's busiest instructions as dead.
  They are not: they answer 102, and real transactions carrying their discriminators succeed, which
  is why these two are the only ones confirmed above.

  That scratch check is the one this trap belongs to. Tracked `idl_probe.py` never required the
  instruction-name line: the first version classified logs containing `FallbackNotFound` as dead
  and any other non-empty logs as live, and the last classified recognised errors first while
  keeping `elif logs:` as a fallback. An earlier revision of this section attributed the trap to
  the tool.

The second is the sharper trap. A check requiring a particular log line does not fail loudly; it names a program's
busiest instructions as broken, which reads as a serious finding and is an artifact of the check.

**Historic note.** Before this was resolved the entry read "attempted, structurally inconclusive". `tools/idl_probe.py`, removed 2026-08-14, calibrated each program with a
garbage discriminator; in its earliest form it expected `InstructionFallbackNotFound` only and
reported `INCONCLUSIVE` for Jupiter, though the version finally deleted accepted
`InvalidAccountData` as well. Either way the result proves only that the program is **not
Anchor-fallback-shaped** — an Anchor program carrying its own fallback handler looks identical from
outside — so it is not evidence about the dispatch style, and no re-run of that tool will settle it.

So the deployed-source comparison **remains unresolved**, and so does most of the cheaper question
beneath it. Two instructions are confirmed present by real transactions; the other 15 produced
responses differing from the control, which §1 no longer treats as proof of dispatch. Nothing
establishes that no declared instruction is gone. Settling the rest needs the deployed source
identified; the account-order diff in §2 covers one axis of that.

The upgrade cadence and unchanged channel hashes make a routine rebuild the likeliest reading, but
that is an inference from process, not a verification.

`sources.json` carries the slot and both hashes at all times, and the table above is the durable
copy — the generating run's own report was in commit `40e1533`, which a squash will take.

---

### A fourth worked example: Stake, 2026-08-31

The first **Core BPF** program here — a former builtin now running as BPF under
`BPFLoaderUpgradeab1e11111111111111111111111`, installed and replaced by the
runtime rather than by anyone's transaction. Every reflex above gives the wrong
answer for it, and one of them gives a confidently wrong one. All measurements
against `api.mainnet-beta.solana.com` on the date in the heading.

**The ordinary read, and the trap in it.** `getAccountInfo` on
`Stake11111111111111111111111111111111111111` (jsonParsed) gives programData
`6WU8Nxarf9fudRK5atWwjLY4vFaw5UrrWhL88qz7iCMJ`. Forty-five bytes off that — not
the thirteen the weaker-signals section used to suggest — decode as
`u32 enum (3) || u64 slot || Option<Pubkey>`:

| field | value |
|---|---|
| enum | `3` (ProgramData) |
| `last_deploy_slot` | **427248000** — 2026-06-18T08:04:17Z |
| upgrade authority | tag byte `0x00` then 32 zero bytes: `Option::None` |

A `None` authority is what everything calls *immutable*, *frozen*, *finalized*.
It is true and it is narrower than it sounds: **`None` forecloses
transaction-driven upgrades and nothing else.**

**The feature gate is the provenance signal.** For a Core BPF program the deploy
*is* a feature activation, and the feature account is a nine-byte record anyone
can read:

```
getAccountInfo STk5Xj8hdAx3sTzmtJ3QysKkq6X2A3yj73JtxttiRyk  (base64 -> hex)
  -> 01 8049771900000000        activated, u64 LE = 427248000
```

Three shapes, three different answers: `01` + `u64` little-endian is **activated
at that slot**; nine zero bytes is **allocated and awaiting activation**; a
`null` account is **nobody has funded the gate**. The owner is always
`Feature111111111111111111111111111111111111`. 427248000 is byte-identical to
`last_deploy_slot`, and that equality is the finding — the gate is
`upgrade_bpf_stake_program_to_v5`, so the deployed image is v5.

What the equality does *not* establish is which ELF. It says the runtime
installed something in that activation and nothing has written the account
since. The payload hash is what names the binary.

**Identification at artifact level.** The ProgramData payload — bytes 45.. — is
202280 bytes and hashes to
`c9cba7f3d9fe0fac1f32e7a5e012284104a25d27ec97d6eb6d99a3afcd2352a8`, which is
already in `sources.json` as `programDataPayloadSha256`. Upstream's
`program@v5.0.0` release asset `solana_stake_program.so` is 202280 bytes with
the same digest — readable straight off the GitHub API without downloading it
(`gh release view program@v5.0.0 --repo solana-program/stake --json assets`).
So the deployed image is upstream commit
`6ed2c60c7e665e145fb54b272f87092c1791ca3c`, named without an attestation of any
kind.

Note the difference from marginfi above, and try both forms: there the hash
reconciled only after stripping trailing zeros from an over-allocated account.
Here the runtime sized the account to the ELF exactly and the *unstripped* hash
is the one that matches. Trailing-zero trimming is a property of a particular
deploy, not a rule.

**The otter-verify record is stale, and read alone it inverts the verdict.** One
exists — `9dC8TtQdg5co5ep5ENQLpVez5EVdJosXwe2ALvg3Fyaj`, owner
`verifycLy8mB96wd9wqq3WDXQwM4oU6r42Th37Db9fC`, 291 bytes. It names version
`0.4.11`, repo `https://github.com/solana-program/stake`, commit
`ff89b6b4dcacf84ae28f92850f500e7aa47b29b4`, base image
`solanafoundation/solana-verifiable-build:2.2.19`, and carries **409968000** in
its trailing `u64` `deployed_slot`. That commit is 2025-12-01 — a week before
`5cf845b`, the change that taught each processor to accept both the
sysvar-bearing and the sysvar-free account list. Believe the record and you date
the program to before that change and conclude the opposite of the truth.

The record is not wrong; it is a statement about an earlier image, and **nothing
has replaced it because nothing can.** The PDA is seeded on the upgrade
authority, and a feature-gate install has no authority to sign a new one. So for
a Core BPF program an otter record is evidence about *some* image and never about
the current one, and its absence is not evidence either. The check that catches
it is the one the marginfi entry already performs: compare the record's
`deployed_slot` against `ProgramData.last_deploy_slot`. 409968000 ≠ 427248000.
**The comparison is the finding; the commit it names is not.**

**"No upgrade authority" is not "never redeployed", and the next one is already
readable.** The successor gate `upgrade_bpf_stake_program_to_v5_1`
(`s51VGwCAgebo2745DSUris72RavoLkXGUmVJosESCXr`) reads nine zero bytes on
mainnet — allocated, unactivated. Its buffer is live and can be hashed today:

| | |
|---|---|
| buffer | `p51x11QCYMHwuVS1MBcLHKb3MezWyqGS5BEB41CA1dk`, 212093 bytes |
| header | `u32 enum (1 = Buffer) \|\| Option<Pubkey> authority` — 37 bytes |
| payload | 212056 bytes, sha256 `3d2d39c596ce8be2d47816b4ee5db9fc759d80fde54b08c930ad0b6daed64c2c` |
| = | upstream `program@v5.1.0` asset `solana_stake_program.so`, byte for byte |

So for this one class of program the *next* binary is identifiable before it
ships, which nothing else here offers. What it does not give is a date: a gate
activates when enough stake has voted it in, and staged is not scheduled.

**A corroborating heuristic, and its limit.** `getEpochSchedule` reports
`slotsPerEpoch` 432000 with `firstNormalSlot` 0, so a feature activation always
lands on a multiple of 432000. Both slots in this program's record are —
427248000 = 989 × 432000, and the otter record's 409968000 = 949 × 432000 — while
a deploy transaction lands wherever the fee market puts it. Use a round slot to
raise the question, never to answer it: it does not name *which* gate.

**And the change that prompted this section was invisible to every signal
above.** solana-program/stake#520 merged 2026-08-31 and removed the clock, rent
and stake-history sysvars and the retired stake-config account from ten
instructions' account lists. `last_deploy_slot` did not move, the payload did not
change, the feature accounts read exactly as before. Only the IDL-to-IDL diff saw
it. That is the marginfi lesson run backwards — there the document held still
while the program moved, here the program held still while the document moved —
and it is why the trigger table above carries both rows rather than preferring
one. Instruction *data* did not change either, so nothing that tests encodings
could have caught it.

What kept the client working through it is a fact about the deployed processors
and nothing else: `program/src/processor.rs` carries a `// diverge` block in each
affected handler that peeks at the account in the old sysvar slot and branches on
`Clock::check_id` / `Rent::check_id`, so v5.0.0 accepts both lists. Specified by
SIMD-0490, landed in `5cf845b` (2025-12-08), first shipped in `program@v5.0.0`.
No slot, hash or gate could have told you that. Reading the Rust is what tells
you that — and §2's tool cannot read this program's Rust, which is the gap
recorded under *Programs that cannot be ground-truthed* below.

---

## 2. Does the account order match the program's Rust?

```shell
java tools/GroundTruth.java anchor <rust-dir>          <Program.java>
java tools/GroundTruth.java shank  <instructions.rs>   <Program.java>
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

Not every program can be ground-truthed, and there are two reasons: no
independent source, or a source in a shape the tool cannot read. Check which
before assuming a diff is meaningful:

- **Stake** — has an independent source, and it is not readable here.
  `solana-program/stake`'s `interface/src/instruction.rs` declares each account
  through codama macros on the `StakeInstruction` variants
  (`codama(account(name = "stake", writable, …))`), which is neither the Anchor
  `#[derive(Accounts)]` struct nor Shank's indexed attributes. Both modes print
  `rust structs 0 … compared 0` — a failure to compare, not a pass (measured
  2026-08-31). Read the Rust by hand, and prefer the `// diverge` blocks in
  `program/src/processor.rs`: they say what the deployed program *accepts*,
  which for this program is wider than what the IDL declares.

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
