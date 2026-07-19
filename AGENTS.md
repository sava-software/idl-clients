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
- When behavior changes upstream (new instruction versions, re-ordered or
  auto-wired accounts), update the generated code via idl-src-gen where the
  IDL covers it, and update the hand-written clients for everything the IDL
  cannot express.

Reference clones of program repositories (and core repos like Agave and the
Solana SDK) are kept locally for this purpose. Machine-specific locations —
where idl-src-gen and the reference clones live on this machine — are recorded
in `AGENTS.local.md`, which is git-ignored; consult it (and add to it) rather
than putting local paths in this file. If a needed program repo is not yet
cloned, clone it into the reference directory listed there.

### Reporting issues found in a third-party program's Rust source

The Rust source is ground truth, but our job is to mirror the program's actual
on-chain behavior, not to critique its code. When cross-checking against a
**third-party** program's Rust (anything that is *not* a sava-software repo,
e.g. the SPL, Kamino, Meteora, Orca, Jupiter, Marinade sources):

- **Only surface a discrepancy if it is a genuine bug we would open an upstream
  PR for.** If you find one, say so precisely (file, symbol, wrong vs correct
  behavior) so a PR can follow.
- **Do not report harmless divergences.** A Rust builder fn whose account flags
  disagree with its own doc comment, dead code, style, naming — none of that is
  actionable for us. Match whatever the deployed program actually enforces
  (verify on-chain when in doubt) and move on without a note. These reports are
  noise, not signal.

This restriction is only for third-party sources. Issues in sava-software's own
repos (sava, idl-src-gen, sava-build, ...) are reported and fixed normally.

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

The process contract for any change to main sources (full policy: sava-build's
`HARDENING.md`; per-module acceptance records: `<module>/config/pitest/README.md`):

- **Run `./gradlew qualityGate` after changing main sources** — unit tests plus
  every PIT suite, each diffed against its accepted baseline in
  `<module>/config/pitest/`. It is the definition of "safe to commit", and it
  costs about 40s for the whole repo.
- **While iterating, run only the suite that owns the code you touched**:
  `pitestSpl` (~10s), `pitestOrca` (~20s), `pitestScope` (~5s), `pitestClients`
  (~12s). `qualityGate` is the before-commit command, not the inner-loop one.
- A new unkilled mutant has exactly three legal outcomes: **kill it** with a test
  (prefer asserting the property it breaks — an overflow guard rejecting, a
  quote's rounding direction, exact encoded byte positions — over restating the
  implementation), **refactor** it out of existence, or **accept it** with a
  written reason in that module's `config/pitest/README.md`. Never run
  `-PupdateMutationBaseline` just to make the build pass.
- Line-number churn from editing a mutated file shows up as paired stale + "new"
  baseline entries; confirm they're the shifted old ones before refreshing.
- **The baselines are triage debt, not acceptance.** They were seeded with the
  full pre-existing survivor population in 2026-07 when the ratchet was adopted;
  each module's README ranks what to pay down first. Shrinking a baseline is
  always an improvement.
- **Randomized tests use fixed seeds**: the ratchet needs deterministic kills,
  and per-run exploration is the fuzz targets' job.
- Fuzz findings become a committed seed input **and** a named regression test,
  never just a fix.

Mutation suites are targeted by **package wildcard with explicit exclusions,
never by allowlist** — an allowlist silently exempts every class added after it
was written. Adding a hand-written class therefore puts it under mutation
automatically; if it lands with no coverage, the gate says so. Generated
`**.gen.*` code is excluded everywhere (its correctness belongs to idl-src-gen),
as are the git-ignored `Integ.*` scratch files, which would otherwise make the
baseline differ between a dev machine and CI.

CI runs `check`, not `qualityGate` (the shared sava-build workflows), so the gate
is enforced at the point of commit rather than in the pipeline.

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
