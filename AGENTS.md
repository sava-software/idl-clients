# AGENTS.md

Guidance for AI coding agents (and humans) working in this repository.

## Commit messages

Conventional commits, written for **users of this library** — what changed for a
caller and why — not for the people developing it. Never append tool-specific
trailers of any kind: no `Claude-Session:`, no `Co-Authored-By:` for an assistant,
no generator footers. The log is a consumer-facing changelog, and with
`always-bump-patch` versioning it is the only place a breaking change is
announced, so keep it free of noise that is about how the work was done.

## What this repository is

This repo makes it easy to interact with on-chain Solana programs from Java —
both calling instructions and reading/deserializing on-chain data. It contains:

- **Generated source** — per-program Java code (instruction builders, account
  and type (de)serialization, PDA helpers, error enums) generated from each
  program's IDL by the companion **idl-src-gen** project (a separate repository).
  Generation is driven by `main_net_programs.json`, which maps each program to
  its module, package, program id, IDL type (Anchor/Codama/Shank), and IDL
  source. Locally stored IDLs live under `idls/`; each generated package also
  keeps the `idl.json` it was generated from next to the source in its `gen/`
  directory.
- **Hand-written convenience layers** — clients, accounts registries, PDA
  helpers, and utilities layered on top of the generated static functions to
  ease integration (e.g. `MeteoraDlmmClient`/`Impl`, `OrcaWhirlpoolsClient`/`Impl`,
  `*Accounts`, `*PDAs`, `*RemainingAccounts`).

### Module layout

- `idl-clients-spl/` — SPL / core programs (token, ATA, lookup tables, ...) and
  shared generated commons.
- `idl-clients-bundle/` — the bulk of third-party programs (Jupiter, Kamino,
  Meteora, Orca, Marginfi, Metaplex, Squads, oracles, ...), plus REST API
  clients for off-chain services (e.g. Jupiter swap/ultra APIs).
- `idl-clients-drift/` — Drift. Dead code for now — not actively maintained;
  don't invest effort here unless asked.

Within a program's package, `gen/` is generated code — **never hand-edit it**.
Fixes to generated output belong in the idl-src-gen generator; then regenerate.
Everything outside `gen/` is hand-written and safe to edit.

## Why the hand-written layer exists (IDL limitations)

An IDL can only communicate so much:

- It declares an instruction's account list, but often not **how to derive**
  those accounts (PDA seeds, ATAs), which ones are optional, which have
  sentinel/default values, or which are auto-wired well-known programs
  (system, token, memo, sysvars).
- It says nothing about **remaining accounts** conventions — e.g. Token-2022
  transfer-hook account resolution, or supplemental tick arrays on Whirlpool
  swaps — which must be appended beyond the declared account list.
- Argument semantics (units, scaling, valid ranges, sentinel values) and
  multi-instruction flows are not expressible.
- Some teams **forget to publish the IDL** when deploying, or publish stale
  IDLs that lag the deployed program.

Figuring out the accounts and data an instruction actually needs is therefore
often real investigative work. The handwritten clients encode the results of
that work — which is exactly why they must be kept in sync with the programs
themselves, not just the IDL.

## Keeping in sync with the Rust source

The IDL is a lossy artifact; the program's **Rust source is the ground truth**.
When writing or updating convenience functionality, reference the actual Rust
source whenever it is available:

- Read the instruction handler and its `#[derive(Accounts)]` context (Anchor)
  or manual account parsing (native/Shank) to confirm account order,
  writability, signers, optionality, PDA seeds, and any `remaining_accounts`
  handling.
- Check constants, error enums, and event/discriminator definitions against
  the source rather than trusting the IDL alone.
- When a program has no published IDL (or a stale one), the Rust source — or,
  failing that, on-chain transaction inspection — is the only reliable way to
  build correct instructions.

### Where an IDL is fetched from, and whether it is current

**Default to the on-chain IDL account** — it is the only artifact bound to the
program address we actually call. An IDL committed to a repo or SDK carries the
opposite risk: the default branch may describe code that is **not yet deployed**,
which breaks the client just as quietly because it still compiles.

Two traps are worth carrying around even when you are not investigating:

- **"The on-chain IDL agrees with our generated code" proves nothing.** It shows
  our code matches *the IDL* — a separate account that a deploy does not update.
- **A repo under a different org may still be the program's home.** Teams
  rebrand; treat provenance as a question to answer, not a disqualifier.

The program can be asked directly, and there is a tool for it:

```shell
python3 tools/idl_probe.py     # every program in main_net_programs.json
```

Run it after any upstream deploy. Full method — the dispatch probe, the weaker
signals and why they disappoint, and the bar for an `idlURL` override — is in
**[docs/PROGRAM_VERIFICATION.md](docs/PROGRAM_VERIFICATION.md)**. Current
overrides and their evidence: `idl-clients-bundle/config/pitest/README.md`.

### Diffing account order against the Rust

```shell
python3 tools/ground_truth.py anchor <rust-dir>        <Program.java>
python3 tools/ground_truth.py shank  <instructions.rs> <Program.java>
```

This is what has surfaced most of the account-ordering defects fixed here — a
transposed pair of same-typed `PublicKey` accounts compiles cleanly and fails
only on chain. The tool is **assistive, not an oracle**: most differences it
reports are artifacts, and `compared 0` means it matched no names, not that
everything passed. Triage guidance and the per-program traps are in
[docs/PROGRAM_VERIFICATION.md](docs/PROGRAM_VERIFICATION.md); some programs
(Meteora, Loopscale) have no independent source and cannot be ground-truthed at
all.

### Extra (`remaining_accounts`) conventions

An IDL expresses neither accounts read from `ctx.remaining_accounts` nor a
**trailing optional** account, so both are invisible to the generated builders
and live in a hand-written `*RemainingAccounts` helper with the derivation cited
in its javadoc. Two shapes that have already shipped as defects — variable-size
per-account groups, and an account consumed off the *front* of the list — are
described in [docs/PROGRAM_VERIFICATION.md](docs/PROGRAM_VERIFICATION.md).

When behaviour changes upstream (new instruction versions, re-ordered or
auto-wired accounts), update the generated code via idl-src-gen where the IDL
covers it, and update the hand-written clients for everything the IDL cannot
express.

Reference clones of program repositories (and core repos like Agave and the
Solana SDK) are kept locally for this purpose. Machine-specific locations —
where idl-src-gen and the reference clones live on this machine — are recorded
in `AGENTS.local.md`, which is git-ignored; consult it (and add to it) rather
than putting local paths in this file. If a needed program repo is not yet
cloned, clone it into the reference directory listed there.

### Reporting issues found in a third-party program's Rust source

Mirror what the deployed program enforces; do not critique its code. Only
surface a discrepancy if it is a genuine bug worth an upstream PR, and say it
precisely (file, symbol, wrong vs correct behaviour). Harmless divergences —
doc comments disagreeing with code, dead code, style — are noise, not signal.
This restriction is for third-party sources only; issues in sava-software's own
repos are reported and fixed normally. Detail:
[docs/PROGRAM_VERIFICATION.md](docs/PROGRAM_VERIFICATION.md).

### PDA helpers require the official program source

Never write or fix a PDA-derivation helper from the IDL alone. A PDA seed is
an opaque byte array on-chain, and an **Anchor IDL cannot express how a
numeric seed value is encoded** (an arg seed only references the typed
instruction argument) — programs differ: Kamino derives withdraw-ticket PDAs
from `sequence_number.to_le_bytes()` (little-endian), while Orca whirlpools
derives tick-array and bundled-position PDAs from
`start_tick_index.to_string()` (decimal-ASCII). Guessing produces addresses
that verifiably do not exist on-chain (see `OrcaUtil`'s history for the scar
tissue). Codama IDLs are not ambiguous by design — a seed carries a full type
node (`numberTypeNode` declares endianness; a decimal-string seed is a
`stringTypeNode`) — but a mis-modeled codama IDL is still wrong, so the
source-verification rule below applies regardless of IDL flavor.

When adding PDA helpers for a program:

1. **Reference the program's official Rust and/or TypeScript codebase** and
   read the actual seed construction (`seeds = [...]` in the handler/context,
   or the SDK's derive functions).
2. If the official repo is not already cloned in the reference directory
   (`AGENTS.local.md`), **clone it there** before writing the helper.
3. If it is not clear which repository is the official one for a program,
   **stop and ask** — do not infer seed encodings from forks, third-party
   SDKs, or the IDL.

**Test PDA helpers against real on-chain addresses, not the source you read.**
A real account *is* the program's own PDA output, so it is authoritative
ground truth a re-derivation can be pinned to — a wrong seed derives an
address that does not exist on-chain. The established pattern (see
`OrcaPDATests`, `kamino/lend/KaminoPDATests`, `meteora/MeteoraPDATests`,
`loopscale/LoopscalePDATests`): fetch a real account during development,
extract its seed inputs and the PDA(s) it stores (or its own address when the
account is itself a PDA of a stored nonce), then bake those as
`PublicKey.fromBase58Encoded` constants and assert the helper reproduces them.
Tests never hit the network. If a derivation cannot be anchored to a real
instance, **skip it and say so** rather than assert a value you only computed
from the helper under test.

## Build & test

GitHub Packages credentials are required for dependency resolution, in
`~/.gradle/gradle.properties`:

```properties
savaGithubPackagesUsername=GITHUB_USERNAME
savaGithubPackagesPassword=GITHUB_TOKEN
```

```shell
./gradlew check                                # full build + tests
./gradlew :idl-clients-bundle:compileJava      # compile one module
./gradlew :idl-clients-bundle:test             # test one module
```

Integration-style tests named `Integ.*` are git-ignored scratch files.

### Verification tools

`tools/` holds two dependency-free scripts (Python 3 + `curl`) for the checks
that are otherwise re-derived by hand: `idl_probe.py` asks each deployed program
whether it still has the instructions our IDL declares, and `ground_truth.py`
diffs a generated client's account order against the program's Rust. Neither is
wired into Gradle or CI — `hardeningCertify` is the release gate; these are
investigative aids whose output needs triage. They are also what carries the
correctness of the generated `**.gen.*` code the mutation suites deliberately do
not mutate. See [tools/README.md](tools/README.md).

## Hardening: mutation testing & fuzzing

Money-critical hand-written parsers and math are covered by PIT mutation testing
(`pitest<Name>` — mutates the classes and expects the tests to kill the mutants)
and Jazzer fuzzing (`fuzz<Name>` — feeds a parse method arbitrary bytes), via the
shared `software.sava.build.feature.hardening` convention plugin (from the
sava-build repo). Each target is declared in a module's `hardening {}` block
(`idl-clients-spl/build.gradle.kts`, `idl-clients-bundle/build.gradle.kts`) —
**that block is the authoritative list**; the class each suite mutates and the
harness/seed each fuzz target uses live there, so read it rather than trusting a
copy here. List the generated tasks with:

```shell
./gradlew :<module>:tasks --all | grep -iE '^(fuzz|pitest)'
```

These tasks are **not** part of `check`; run the relevant one when you change a
targeted class — a fuzzer with `./gradlew :<module>:fuzz<Name> -PmaxFuzzTime=<seconds>`,
a PIT suite with `./gradlew :<module>:pitest<Name>`.

### Quality gate & mutation ratchet

Full policy: sava-build's `HARDENING.md`. For anything a hardening *task* does —
what it writes, what it refuses, which flag a decision needs — `./gradlew
hardeningHelp` and `./gradlew hardeningAgentTemplate` are the authorities for
the installed version, and nothing here restates them. Per-module acceptance
records and the argument behind every accepted row:
`<module>/config/pitest/README.md`.

The generated operator rules follow verbatim; the repo-specific facts are in
"This repository" below them.

<!-- hardening-template sha256:46f7174e51fb -->

- **Scale verification to the change.** Iterate with the module's `test`
  task; before handing off, run only the `pitest<Suite>`(s) whose mutated
  code the change can reach — including suites in dependent modules that
  call a changed API, and the owning suite for test-only edits (a weakened
  test is exactly what the ratchet catches). When the production-class inventory
  changes (add/remove/rename/move), or mutation target/exclusion rules change,
  also run the cheap whole-population
  `mutationOwnershipAudit` before handoff. The full `hardeningCertify` — every
  suite freshly observed, serialized, provenance-bound, diffed against
  `config/pitest/`, with strict timeout and ownership audits — is the pre-release
  check, owned by CI or by the release checklist (this repo records which); it is
  not the inner loop.
- A new unkilled mutant has exactly three legal outcomes: **kill it** with a
  test (prefer asserting the property it breaks over restating the
  implementation), **refactor** it out of existence, or **accept it** with a
  written reason in `config/pitest/README.md` **and a short family label on
  the row itself** — refreshes seed new rows `# untriaged`, and triage means
  replacing that label, so the baseline always says which rows are argued
  and which are debt. Never run a baseline-update task just to make the build
  pass.
- **A mutant is a question, not a specification.** Before writing a killing
  test, state the externally intended property and an oracle independent of the
  current implementation: public contract, protocol specification, caller
  invariant, reference implementation, or domain rule. If it contradicts current
  behavior, first demonstrate the bug with a regression test that fails against
  the unmutated code, then fix production; never add a passing assertion that
  merely locks in the bug. At PR or handoff, report each nontrivial behavioral
  cluster — not each mutant — as `Property: ... | Oracle: ... | Outcome: missing
  assertion / production bug / accepted equivalent`. Test names and assertions
  normally carry the durable property; comment only when the oracle or unusual
  setup would otherwise be lost, and never embed PIT coordinates or line numbers.
- Baseline keys are line-less (`class,method,mutator,STATUS`) — editing
  above a mutated method churns nothing, and `# line` tags are review
  metadata. A new mutant replacing a killed one at the same key can inherit
  its acceptance, so treat a line-drift advisory whose written argument no
  longer fits the code as that swap until shown otherwise. Use the installed
  plugin's named writer tasks and heed their candidate previews; never hand-edit
  record structure or provenance stamps. A PIT, PIT-plugin/tool-artifact,
  ArcMutate-base, or certificate change uses `pitest<Suite>BaselineRebase`: it
  preserves every old row, seeds new rows `# untriaged`, and stamps the reviewed
  toolchain only after a successful fresh observation. Perform a schema
  migration/rollback only with a fleet pin plan. A `[history]` report may check
  the ratchet but cannot support adding, removing, or relabelling
  accepted/timeout records; run `pitest<Suite> -PnoMutationHistory` first.
- Consumer hardening notes contain only local ownership, measurements, acceptance
  reasons, and provenance. `AGENTS.md` may carry this exact generated,
  digest-pinned template plus those local facts, but no independently maintained
  copy of plugin task semantics; use `hardeningHelp` and
  `hardeningAgentTemplate` as the installed-version authorities.
- **Iterate with `-PmutateOnly=<class-glob>`** while killing a cluster —
  seconds instead of the full suite — then re-run unscoped with
  `-PnoMutationHistory` before any record decision; the tooling refuses to let
  a scoped report touch the baseline.
- Identical baseline rows are sibling mutants of one compound condition and
  the comparison is a multiset: never hand-dedupe. When one sibling
  survives, the verify names the killed sibling's test — the survivor is
  the opposite branch direction; triage it as its own mutant.
- **A survivor contradicted by an existing oracle may be contaminated evidence.**
  Open PIT's HTML **Covering tests** list, then compare the same scoped,
  history-free population with and without isolation:
  `-PmutateOnly=<class> -PnoMutationHistory`, then
  `-PmutateOnly=<class> -PisolateMutants`. An isolation-only kill points
  to state leaked between mutants — commonly a thread, executor, handler, or
  static fixture whose cleanup an earlier assertion failure skipped. Put
  teardown in `finally`/`try`-with-resources and rerun normally, history-free;
  isolated execution is diagnostic evidence, never a baseline decision.
- **Stubs and fixtures return distinguishable, non-default values.** A stub
  returning null/0/""/true/empty makes the matching return-value mutant
  equivalent by accident of the fixture — the clock non-zero-origin rule
  generalized to every stubbed return.
- **Copy-on-write clusters split by direction.** Assert immutability of
  returned collections (`assertThrows(UnsupportedOperationException, ...)`)
  at every size: the mutable-escape direction is a kill, not an acceptance;
  only the content-equal siblings are family-accepted equivalents.
- **Randomized tests use fixed seeds, and never sleep**: the ratchet needs
  deterministic kills, and PIT re-runs the suite per mutant, so one real wait
  costs minutes. Exploration belongs to the fuzz targets.
- **Do not rely on PIT's timeout to detect a mutant.** `TIMED_OUT` counts as
  detected and is not written to the baseline, and it is load-dependent — the
  same mutant can report `SURVIVED` alone and `TIMED_OUT` under
  `qualityGate`. Verify a baseline in both modes; union only rows observed to
  flip, never every `TIMED_OUT` row.
- **A new timed-out mutant is a reviewer-stop, not detection noise.** For
  exactly these mutants the ratchet cannot see a weakened covering
  assertion — a timeout keeps "detecting" whatever the test asserts — so
  each suite's timeouts are an audited set, not a count:
  `config/pitest/<suite>-timeouts.csv` holds line-less `class,method,mutator`
  keys plus a comment category; `# line` tags are diagnostic metadata only. Only
  `cause:liveness` is admissible watchdog detection after deterministic
  seams/budgets are exhausted: the mutated path has no path-owned finite
  completion guarantee. A fixture's emergency exit does not demote that
  liveness loss to resource work; record the fixture bound in the README. If that
  bound is the claimed deterministic oracle, compare it with PIT's
  `duration × timeoutFactor + timeoutConst`: a bound that cannot fail first
  contributes no cause evidence, so shorten it and re-observe history-free. A
  later emergency ceiling may coexist with production liveness but cannot prove it.
  A straight-line path with no loop, retry, lock, wait, blocking
  call, or external completion dependency is not credible liveness evidence.
  Before
  admitting liveness, prove the mutated path receives the clock/budget the test
  observes, and check for a synchronous state reader that can expose the defect
  without waiting. A `TestClock` on a collaborator cannot observe a subject using
  the system clock. Seeded
  `cause:untriaged`, missing/unknown categories, finite `cause:resource`, and
  `cause:harness` work are reviewer-stops. `cause:harness` is the explicit
  non-certifying holding state for a demonstrated finite covering-path/watchdog
  race; it never makes the timeout admissible. Resource behavior gets a
  deterministic contract test/fix when promised, otherwise a stable `SURVIVED`
  equivalence argument —
  never silent timeout membership. Liveness authorizes valid `TIMED_OUT`
  evidence only, never `MEMORY_ERROR`: if a non-advancing loop races the heap
  against the watchdog, make every covering path fail deterministically without
  relying on PIT test order, or refactor the manual progress mutation site out
  while preserving the tested contract.
  `config/pitest/README.md` still holds the
  full structural cause per member. The verify warns on any timeout outside
  the set — paste the printed row, classify it, then write the cause — and on
  members matching no mutant. Membership and cause are key-level, so a liveness
  token claims every sibling under that key. A key proven to mix liveness and
  finite causes is not representable as an honest certifying row: split/refactor
  it into distinct method keys or eliminate the ambiguous site, then re-observe
  history-free. A source-line qualifier cannot fix the identity without making
  formatting a release gate. Positive multiplicity drift prints all current
  line-full candidates for review;
  source-line movement itself never warns, fails, or requires re-anchoring. Adding
  a method, moving imports, or reflowing an expression is not a hardening record
  change. Strict workflows run the
  committed-file half before PIT; use `pitest<Suite>Debt` for the same quick
  manual preview. `TimeoutAuditInit` deliberately seeds an uncertifiable file —
  classify every row before certification. For an otherwise admissible liveness
  member, do not retire it until the tool emits its 3+ distinct fresh full-run quiet
  notice over identical evidence inputs and the absence is confirmed under the
  relevant solo/gate load. A finite KILLED↔TIMED_OUT race is benign only to baseline
  arithmetic, never certifying evidence; repair/retime its covering path instead of
  admitting it or waiting on the liveness-retirement rule. The quiet stash
  is a machine-local nomination: never copy or merge it, and retain the row when a
  same-input gate confirmation is unavailable. Assisted reports are
  previews and do not
  advance timeout status or quiet-run evidence.
- **A flaky harness is worse than recorded debt.** If an interleaving or a
  boundary cannot be made deterministic, accept the mutant with a written
  reason rather than chasing it with sleeps or spin-waits.
- **A suite's percentage is not a target.** An accepted mutant with a written
  reason is finished work, not debt. Before trying to raise a number, check
  whether the remainder is `NO_COVERAGE` (real work) or documented
  equivalents (already closed).
- **Allocation and timing harnesses are a last resort for thin constant-factor
  differences**, reserved for properties that are a stated design goal. A
  removed growth/capacity/amortisation guard that changes complexity class is
  not “allocation-size only”: use a small input with an orders-of-magnitude
  margin and the correct path through the mutated code. Harnesses re-run once
  per mutant, need a `volatile` sink so escape analysis cannot delete what they
  measure, and flap when the margin is thin.
- When a test you believe in will not go green, **suspect the code before you
  soften the assertion** — that is where this process finds real bugs.
- **A wandering unkilled count is a defect, not noise** — chase it before
  changing any baseline. Reproduce it under the relevant solo/gate loads,
  inspect per-mutant coordinates, remove real waits, and move construction
  coverage into the test body before deciding whether it is a product defect,
  a load-dependent timeout, or a harness defect.
- **Build the subject under test inside the test body, not in a field.**
  Under `PER_CLASS` lifecycle a field-initialized client's construction
  coverage attaches to whichever test runs first, so wiring mutants can
  never pair with the test that drives what they wire — they survive even
  under a harness that asserts every request. One test that constructs the
  client in the test method and drives each configured URL restores the
  pairing.
- **Kill rates are bounded by the mutator set.** `BigInteger`/`BigDecimal`
  arithmetic and receiver-returning fluent calls can be invisible to the
  enabled defaults. Follow the plugin's trial advice per suite, enable only
  mutators proved to fire, and record the measured numbers and declines.
- Module-path and mutation-test service discovery can differ. Declare real
  services in every runtime representation the project supports, probe the
  active environment in test-only scaffolding, and never commit a harness
  whose pass/fail result depends on which task launched it.
- `SURVIVED` and `NO_COVERAGE` are different problems: the first is a
  judgment call about equivalence, the second is usually an untested line
  and is mechanical. Never accept a `NO_COVERAGE` mutant as "equivalent" —
  you have not observed its behaviour. One structural exception: a block
  that always exits by throw reads `NO_COVERAGE` forever, executed or not
  (PIT probes a block at its end), and its return-value mutants can never
  change status. Such a line is owed a test asserting the throw's contract,
  not coverage — and never leave one untested fearing a covered-line
  `SURVIVED` conversion, which would require the block to complete.
- Exclusions must cover the **test source set**, not a naming convention:
  shared fakes are named `RecordingFoo` / `StubFoo` and match no `*Test*`
  pattern. After registering or widening a suite, list the mutated classes and
  confirm none live under `src/test`.
- **Verify by the absence of failures, not the presence of passes.** Counting
  `PASSED` lines hides a failure sitting next to them, and a green
  `clean build` can mean the build cache short-circuited rather than that
  tests ran. Check the failure count and confirm the task actually executed.
  A mutation run has a second version of this: a *failed* PIT run leaves the
  previous run's report in place, so the summary you read can describe a run
  that never happened. Trust the exit code, and delete report directories
  when comparing runs.
- **A suite that got faster without getting narrower is a bug report.** Real
  speedups come from fewer mutants or faster covering tests; an unexplained
  one usually means the run did less than you think. Read the task's evidence
  markers and scope; only a fresh full certification may support a release.
  The process itself needs no ArcMutate licence and applies to any Java package.
- **Invalid execution outcomes are not results.** PIT `MINION_DIED` fails
  before writing a report, so it cannot corrupt one — re-run the suite; a
  Gradle-worker `EOFException` death is the same shape, and a per-mutant
  `RUN_ERROR` often first observed in a multi-suite run is the same
  shape smaller (load average itself proves nothing; the hardening parser refuses
  the report rather than certifying PIT's detected score). The refusal and
  `pitest<Suite>Debt` name every offending row; retain the coordinate before a
  quiet re-run replaces the report. `RUN_ERROR` alone diagnoses neither load nor
  memory and never justifies changing threads or heap; record load/RSS as context,
  retry once quietly, and tune only when PIT explicitly diagnoses a process-resource
  failure. A repeat at the same coordinate is not evidence
  of load: investigate the mutated bytecode, its covering tests, and the tool failure.
  The daemon log
  (`~/.gradle/daemon/<version>/daemon-<pid>.out.log`) keeps a failed build's
  full output even when the shell discarded it — read it before calling a
  failure unexplained.
- Fuzz findings become a committed seed input **and** a named regression
  test, never just a fix — and the committed corpus is replayed by a unit
  test inside `check`, so it cannot rot between fuzz runs.
- **Run fuzz campaigns explicitly and locally.** `fuzzAll` is derived from every
  registered target, so it cannot drift from a hand-written workflow task list;
  set and record `-PmaxFuzzTime=<seconds>` and
  `-PmaxParallelFuzzTargets=<count>` before release. Scheduled GitHub fuzz
  workflows are optional and are not release evidence.
- **When one thing has two representations, fuzz the differential.** Two
  parsers for one config, an encode/decode round trip, a fast path beside a
  reference path: assert the two *agree* rather than that neither crashes.
  Crash-only fuzzing cannot see a wrong answer.
- **Time-dependent code takes a clock**, so tests advance time instead of
  waiting. Give test clocks a non-zero origin — a clock starting at 0 makes
  every "start timestamp mutated to 0" mutant equivalent by accident.

#### This repository

Local ownership, measurements and provenance only — the rules above are the
policy, and `hardeningHelp` is the task reference.

**Suites and what they own.** `pitestSpl` (idl-clients-spl) and `pitestOrca`,
`pitestScope`, `pitestClients` (idl-clients-bundle). `clients` is a catch-all by
exclusion, so a new hand-written class lands in some suite by default rather
than being silently skipped. Reachability decides which to run, not file paths:
editing an API also owes any suite — including one in a dependent module —
whose mutated code calls it. Doc and build-script changes owe no suite.

**Measured 2026-08-07 by `hardeningCertify` on sava-build 21.5.25** (re-measure
rather than trust this line; PIT engine time, one suite at a time):

| suite | detected | survived | timed out | accepted rows | engine |
|---|---|---|---|---|---|
| `spl` | 826/830 (99%) | 4 | 0 | 4 | 27s |
| `orca` | 596/634 (94%) | 38 | 1 (audited) | 38 | 19s |
| `scope` | 300/337 (89%) | 37 | 0 | 37 | 19s |
| `clients` | 1586/1621 (97%) | 35 | 0 | 35 | 43s |

Every accepted row matches a mutant in the current population — the baselines
carry no stale rows.

**Who owns certification.** CI runs `check`, which includes
`agentsTemplateInSync` but no mutation suite. `hardeningCertify` and a local
`fuzzAll` are therefore release-checklist items here, not CI's.

**What `agentsTemplateInSync` actually proves** — measured 2026-08-07 on
sava-build 21.5.25; re-check on a plugin bump rather than trusting this line.
It compares only the `<!-- hardening-template sha256:… -->` marker against the
installed plugin's digest. It passed with one rule reworded to state its own
opposite, and passed again with the entire template body deleted; it fails only
when the digest itself is wrong, and the marker's position relative to the block
does not matter. So adopting a template change means diffing the body against
`hardeningAgentTemplate` output by hand: **bumping the marker is not the
adoption, and CI cannot tell the two apart.** Capture the previous version's
output before changing the pin — that is the only clean "before" side of the
diff once the plugin is gone from the build.

**ArcMutate is active.** A root `arcmutate-licence.txt` (**tracked** — a
`!/arcmutate-licence.txt` re-include in `.gitignore` keeps it in the tree on
purpose, so CI and anyone who clones can run the suites without a secret. It is
an `OSSS` open-source licence scoped to `software.sava.*`, grants access to
nothing, and is not a credential — leave it committed) puts
`com.arcmutate:base` on the PIT toolchain for all four suites, so the licensed
mutant population differs from the unlicensed one — three loop-exit
`RemoveConditionalMutator_ORDER_IF` mutants that used to time out are no longer
generated at all. The licence sha and expiry (**2027-08-15**) are bound into
each suite's `config/pitest/<suite>-pitest-toolchain.tsv`, so renewing or
replacing the certificate moves the provenance stamp in all four suites at
once — it is a toolchain transition, not a file swap. Result-reuse history is
not in play today (no `.hist` file exists), but every record decision still
runs `-PnoMutationHistory`.

**Baseline provenance.** All four baselines were rebased onto 21.5.24 on
2026-08-06 (`pitest<Suite>BaselineRebase`), which is what bound the PIT version
and toolchain that the pre-21.5.22 records left unstated. The committed
provenance lives beside each baseline as `<suite>-pitest-version` and
`<suite>-pitest-toolchain.tsv`. The rebase carried all 134 rows forward; 22 of
them then matched no mutant in the licensed population and were pruned after two
independent history-free measurements — one solo, one under `qualityGate` load —
produced identical unmatched sets. Eleven of the 22 were coordinates the
licensed population no longer generates at all; the rest were surplus siblings
at coordinates that now report fewer survivors than the baseline held.

**Audited timeouts.** `orca` holds the only member, `OrcaUtil.sqrtFloor`
(`cause:liveness`). `spl`, `scope` and `clients` are armed but empty, so a first
timeout in any of them is a reviewer-stop. The structural argument for the live
member, and for the four rows retired during the 21.5.24 adoption, is in the
bundle README under "Audited timeout-detected mutants".

**Exclusion ownership.** Suites are targeted by package wildcard with explicit
exclusions, **never by allowlist** — an allowlist silently exempts every class
added after it was written. Generated `**.gen.*` code is excluded everywhere
(its correctness belongs to idl-src-gen, and `tools/idl_probe.py` /
`tools/ground_truth.py` are what check it), as are the git-ignored `Integ.*`
scratch mains, which would otherwise make the baseline differ between a dev
machine and CI. Both arguments are declared to the ownership audit as
`declineExclusionAudit(...)` in each module's `hardening {}` block — that DSL
call is the record, and it is per-suite, so it has to be repeated in every
suite the glob actually swallows classes in. Exclusion patterns need a trailing
wildcard (`*Test*`, `*Fuzz*`, not `*Fuzz`) so nested helpers inside test classes
stay excluded, and shared fakes named for their role (`ResourceUtil`) match no
pattern and need an explicit entry.

**Mutator selection, per suite, measured.** All four add
`EXPERIMENTAL_NAKED_RECEIVER`, because fluent calls returning their receiver are
expressions and invisible to `VoidMethodCallMutator` (dropped
`stripTrailingZeros`, `URI::resolve` and `StringBuilder.append` calls were all
inexpressible before it). `spl`, `orca` and `clients` add
`EXPERIMENTAL_BIG_INTEGER`, and `clients` also `EXPERIMENTAL_BIG_DECIMAL`,
because `MathMutator` only rewrites *primitive* bytecode arithmetic — the
fixed-point and fee math is method calls and would otherwise go unmutated.
`scope` adds neither: it holds no such arithmetic, so there is nothing to
enable and nothing to decline. `spl` declines `EXPERIMENTAL_BIG_DECIMAL` with
the measurement in the `declineMutator(...)` call itself, which is the
authority; the trial numbers behind each choice are in each module's
`config/pitest/README.md`.

**Local worked examples** worth reusing:

- The `PER_CLASS` field-initializer trap in REST-client costume: the Jupiter
  clients' URL-wiring mutants could not be paired with the tests that drove
  those URLs until the client was built inside the test body —
  `urlWiringIsCoveredFromInsideTheTest` in `JupiterSwapApiClientTests` and
  `JupiterTokenClientTests` is the pattern.
- The same trap is what made an unkilled count wander here. A same-commit
  annotation change was a no-op: at JUnit 6.1.2 `@Execution`/`@TestInstance` are
  both `@Inherited`, and `@Execution` is moot without parallel execution —
  `javap` the resolved jar before restructuring tests on inheritance theories.
- PIT minions run on the **class path** even though this repo's tasks run on the
  module path, so `module-info` services are invisible to them and a
  test-resources `META-INF/services` is invisible to the module-path `test`
  task. Real services are declared in both places, and a harness whose result
  depends on which task ran it is never committed.
- Most of this repo's shipped-defect finds surfaced as a test that would not go
  green — the Jupiter fee-payer crash among them.

Conventions when adding a target:

- A fuzz harness is a `*Fuzz.java` in the test sources with
  `public static void fuzzerTestOneInput(byte[])` and **no Jazzer imports** (so it
  compiles with the regular test sources). Register it with `fuzz.register(...)`.
- Malformed-input contract is **garbage in → `RuntimeException` out**: the harness
  tolerates any `RuntimeException` from a parse; a `StackOverflowError`, `OutOfMemoryError`,
  or any other non-`RuntimeException` throwable is a finding. Beyond that, assert the
  cross-method invariants that must hold on a successful parse (round-trip / determinism /
  length-vs-header), which is what catches offset and logic bugs.
- **Seed structured parsers.** A fixed/large account layout (e.g. a ~29KB Scope
  mapping, a ~600B stake pool) is unreachable from a scratch mutator, so commit real
  account dumps under `src/test/resources/fuzz/<name>/` and point the target's
  `seedCorpus` at that directory. Skip seeding only when every input prefix is already
  valid (a small count-prefixed record parser reaches its whole space from scratch).
- Record what each seed pins in the README **next to** the corpus directory
  (`src/test/resources/fuzz/README.md`), never inside it, where the file would be
  fed to the harness as a seed. Replay tests are generated, not hand-written.
- **When one thing has two representations, fuzz the differential** — an
  encode/decode round trip, a fast path beside a reference path: assert the two
  *agree* rather than that neither crashes. The re-parse determinism checks in the
  existing harnesses are the minimal form of this.

## Conventions

- Conventional commits; releases are cut by release-please (`fix:`/`feat:`
  bump patch/minor; a `BREAKING CHANGE:` footer bumps major).
- The root `.gitignore` is a **recursive** whitelist: every path in the repo is
  ignored unless a rule re-includes it, not just the top level. A new kind of
  tracked file — a new resource extension, a new config directory — needs an
  explicit rule there or it will be silently untracked. The upside is that build
  output, PIT reports and Jazzer reproducers (`crash-*`, `slow-unit-*`) cannot be
  committed by accident, which had happened before the whitelist was tightened.
- Generated code style (two-space indent, `final` params, records) is set by
  the generator; hand-written code follows the same style.
