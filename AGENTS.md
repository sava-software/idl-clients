# AGENTS.md

Guidance for AI coding agents (and humans) working in this repository.

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
wired into Gradle or CI — `qualityGate` is the gate; these are investigative
aids whose output needs triage. See [tools/README.md](tools/README.md).

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

<!-- hardening-template sha256:2c504992c917 -->

Full policy: sava-build's `HARDENING.md` — the canonical source for the ratchet,
its equivalence families, and the lifecycle. Per-module acceptance records:
`<module>/config/pitest/README.md`. The load-bearing points, with this repo's
specifics:

- **Scale verification to the change.** Iterate with the module's `test` task;
  before handing off, run only the `pitest<Suite>`(s) whose mutated code the
  change can *reach*: `pitestSpl`, `pitestScope` (~12s each), `pitestClients`,
  `pitestOrca` (~28s each; timings indicative — re-measure rather than trust
  this line). Reachability, not file paths: editing an API also owes any suite
  — including in a dependent module — whose mutated code calls it, and a
  test-only edit still owes the suite those tests kill mutants in (a weakened
  test is exactly what the ratchet catches). Doc and build-script changes owe
  no suite.
- **The full `qualityGate`** (every suite, serialized, diffed against
  `config/pitest/`, ~75s) is the pre-release check, and — because CI here runs
  `check`, not `qualityGate` — it must be run locally before a release and
  after cross-cutting changes; it has no other enforcement point. Running it
  per small change re-learns results the change could not have moved.
- A new unkilled mutant has exactly three legal outcomes: **kill it** with a test
  (prefer asserting the property it breaks — an overflow guard rejecting, a
  quote's rounding direction, exact encoded byte positions — over restating the
  implementation), **refactor** it out of existence, or **accept it** with a
  written reason in that module's `config/pitest/README.md`. Never run
  `-PupdateMutationBaseline` just to make the build pass.
- **`SURVIVED` and `NO_COVERAGE` are different problems.** The first is a
  judgment call about equivalence; the second is an untested line and is
  mechanical work. Never accept a `NO_COVERAGE` mutant as "equivalent" — its
  behaviour has not been observed. Every accepted-equivalent note in this repo
  refers to `SURVIVED` rows; keep it that way.
- **A suite's percentage is not a target.** An accepted mutant with a written
  reason is finished work. Before trying to raise a number, check whether the
  remainder is `NO_COVERAGE` (real work) or documented equivalents (closed).
- Pure line drift — every new baseline entry a same-status shift of a stale
  one, populations unchanged — passes on its own with a notice; refresh at a
  convenient moment. Anything mixed in (newly covered, unexplained, changed
  counts) still fails and is triage first, refresh after.
- **Iterate with `-PmutateOnly=<class-glob>`** while killing a cluster —
  seconds instead of the full suite — then re-run unscoped before any refresh;
  the tooling refuses to let a scoped report touch the baseline.
  `pitest<Suite>Debt` ranks where the remaining debt lives; `-PlistUnkilled`
  annotates each unkilled row with PIT's mutation description.
- Identical baseline rows are sibling mutants of one compound condition and
  the comparison is a multiset: never hand-dedupe. When one sibling survives,
  the verify names the killed sibling's test — the survivor is the opposite
  branch direction; triage it as its own mutant.
- **The baselines are triage debt, not acceptance.** They were seeded with the
  full pre-existing survivor population in 2026-07 when the ratchet was adopted;
  each module's README ranks what to pay down first. Shrinking a baseline is
  always an improvement.
- **Randomized tests use fixed seeds, and never sleep**: the ratchet needs
  deterministic kills, and PIT re-runs the covering tests once per mutant, so
  one real wait costs minutes across a suite. Exploration belongs to the fuzz
  targets.
- **A flaky harness is worse than recorded debt.** If an interleaving or a
  boundary cannot be made deterministic, accept the mutant with a written
  reason rather than chasing it with sleeps, spin-waits, or thin-margin
  measurement bounds.
- **Build the client under test inside the test body, not in a field.** Under
  `PER_CLASS` lifecycle a field-initialized client's construction coverage
  attaches to whichever test runs first, so URL-wiring mutants can never pair
  with the test that drives what they wire — they survive even under a
  harness that asserts every request (observed here: the Jupiter REST
  clients; `urlWiringIsCoveredFromInsideTheTest` in both client test classes
  is the pattern).
- **A wandering unkilled count is a defect, not noise** — chase it before
  refreshing any baseline. Known causes: real waits, `TIMED_OUT` load flips,
  and coverage attributed to field initializers. (Observed here:
  unkilled-count flapping, fixed by exercising a builder from inside a `@Test`
  — coverage attributed to a field initializer is unstable under PIT. A
  same-commit annotation change was a no-op: at JUnit 6.1.2
  `@Execution`/`@TestInstance` are both `@Inherited`, and `@Execution` is moot
  without parallel execution — `javap` the resolved jar before restructuring
  tests on inheritance theories.)
- **Allocation and timing harnesses are a last resort**, reserved for
  properties that are a stated design goal. They re-run once per mutant, need
  a `volatile` sink so escape analysis cannot delete what they measure, and
  flap when the margin is thin.
- **Time-dependent code takes a clock**, so tests advance time instead of
  waiting. Give test clocks a non-zero origin — a clock starting at 0 makes
  every "start timestamp mutated to 0" mutant equivalent by accident.
- **Do not rely on PIT's timeout to detect a mutant.** `TIMED_OUT` counts as
  detected and is load-dependent: the same mutant can report `SURVIVED` alone
  and `TIMED_OUT` under `qualityGate`. If a suite reports timed-out rows,
  verify its baseline in both modes; union only rows observed to flip, never
  every `TIMED_OUT` row. The comparison is scripted: `pitestModeSnapshot
  -PpitestMode=<label>` / `pitestModeCompare`, with `-PunionModeFlips` writing
  the observed-flip unions; `pitestConverge` checks run-to-run determinism.
- When a test you believe in will not go green, **suspect the code before you
  soften the assertion** — most of this repo's shipped-defect finds (the
  Jupiter fee-payer crash among them) surfaced exactly this way.
- **Verify by the absence of failures, not the presence of passes**: check the
  failure count and that the task actually executed — a green cached build
  proves nothing ran, and a *failed* PIT run leaves the previous run's report in
  place, so a summary can describe a run that never happened. Trust the exit
  code; delete report directories when comparing runs.
- **A suite that got faster without getting narrower is a bug report.** Real
  speedups come from fewer mutants or faster covering tests; an unexplained
  one usually means the run did less than you think. (Arcmutate incremental
  history — whose `[history]` summary marker is the one legitimate exception —
  is not active in this repo: there is no `arcmutate-licence.txt`.)
- **Transient infra failures are not results.** PIT `MINION_DIED` fails before
  writing a report, so it cannot corrupt one — re-run the suite; a
  Gradle-worker `EOFException` death is the same shape, and a per-mutant
  `RUN_ERROR` under load is the same shape smaller (the summary names it, and
  it is not counted as detected). The daemon log
  (`~/.gradle/daemon/<version>/daemon-<pid>.out.log`) keeps a failed build's
  full output even when the shell discarded it — read it before calling a
  failure unexplained.
- Fuzz findings become a committed seed input **and** a named regression test,
  never just a fix — and the committed corpus is replayed by a plugin-generated
  `<Harness>SeedReplayTest` inside `check` (it fails on a missing or emptied
  corpus), so it cannot rot between fuzz runs. Seed provenance lives in the
  README next to (never inside) each corpus directory.
- **PIT minions run on the class path**, even though this repo's tasks run on
  the module path: `module-info` services are invisible to them, and a
  test-resources `META-INF/services` is invisible to the module-path `test`
  task. Real services are declared in both places; a harness whose result
  depends on which task ran it is never committed.

Mutator selection is per-suite. The default is PIT's `STRONGER` group; `orca`
and `clients` add `EXPERIMENTAL_BIG_INTEGER` because `MathMutator` only
rewrites *primitive* bytecode arithmetic — `BigInteger`/`BigDecimal` arithmetic
is method calls, so fixed-point and fee math would otherwise go entirely
unmutated — and every suite adds `EXPERIMENTAL_NAKED_RECEIVER` because fluent
calls returning their receiver are expressions, invisible to
`VoidMethodCallMutator` (dropped `stripTrailingZeros`, `URI::resolve`, and
`StringBuilder.append` calls were all inexpressible before it). See each
module's `config/pitest/README.md` for the trial measurements, including why
`EXPERIMENTAL_BIG_DECIMAL` is deliberately omitted.

Mutation suites are targeted by **package wildcard with explicit exclusions,
never by allowlist** — an allowlist silently exempts every class added after it
was written. Adding a hand-written class therefore puts it under mutation
automatically; if it lands with no coverage, the gate says so. Generated
`**.gen.*` code is excluded everywhere (its correctness belongs to idl-src-gen),
as are the git-ignored `Integ.*` scratch files, which would otherwise make the
baseline differ between a dev machine and CI.

Exclusions must cover the **test source set**, not a naming convention — give
patterns a trailing wildcard (`*Test*`, `*Fuzz*`, not `*Fuzz`) so nested
helpers inside test classes stay excluded, and remember that shared fakes named
for their role (`RecordingFoo`, `ResourceUtil`) match no pattern and need an
explicit entry. `pitest<Suite>Verify` cross-references mutated classes against
the test source directories and warns when a suite is mutating its own
scaffolding.

CI runs `check`, not `qualityGate` (the shared sava-build workflows) — see the
first two bullets above for what that makes a local responsibility.

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
- A committed seed corpus is replayed on every `check` by a plugin-generated
  `<Harness>SeedReplayTest` (e.g. `ScopeReaderFuzzSeedReplayTest`) — do not
  hand-write replay tests. Record what each seed pins in the README next to the
  corpus directory (`src/test/resources/fuzz/README.md`), never inside the
  corpus directory itself, where the file would be fed to the harness as a seed.
- **When one thing has two representations, fuzz the differential** — an
  encode/decode round trip, a fast path beside a reference path: assert the two
  *agree* rather than that neither crashes. Crash-only fuzzing cannot see a wrong
  answer. The re-parse determinism checks in the existing harnesses are the minimal
  form of this.

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
