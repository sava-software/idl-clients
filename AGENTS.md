# AGENTS.md

Guidance for AI coding agents (JetBrains AI Assistant, Junie, etc.) working in
this repository.

## Project

`idl-clients` — generated Java source to (de)serialize instructions and
accounts for common Solana programs, plus convenience methods and clients to
ease integration.

Build: Gradle (`settings.gradle.kts`, multi-module) using the
`software.sava.build` plugin. Build/check with:

```sh
./gradlew check
```

### Modules

Top-level modules, one per Solana program family plus shared support:

- `idl-clients-core/` — shared base types and client utilities used by all
  per-program modules.
- `idl-clients-cctp/`, `idl-clients-drift/`, `idl-clients-jupiter/`,
  `idl-clients-kamino/`, `idl-clients-loopscale/`, `idl-clients-marinade/`,
  `idl-clients-metaplex/`, `idl-clients-meteora/`,
  `idl-clients-neutral_trade/`, `idl-clients-oracles/`,
  `idl-clients-phoenix/`, `idl-clients-spl/`, `idl-clients-squads/` —
  per-program generated clients, accounts, instructions, and convenience
  wrappers.

Each module follows the convention:

- Source under `src/main/java/software/sava/idl/clients/<module>/...`, where
  `<module>` matches the module suffix (e.g., `kamino`, `phoenix`).
- A module may contain one or more programs. Each program lives in its own
  sub-package: `src/main/java/software/sava/idl/clients/<module>/<program>/`
  (e.g., `software/sava/idl/clients/kamino/lend/`,
  `software/sava/idl/clients/phoenix/perpetuals/`).
- Generated code for each program is placed in a `gen` sub-package:
  `src/main/java/software/sava/idl/clients/<module>/<program>/gen/...`. This
  typically includes generated account/state classes (e.g., `VaultState`),
  their `*_OFFSET` layout constants, instruction builders, PDA helpers, and
  type definitions under `gen/types/`. These files are the source of truth
  for on-chain account layouts and instruction encoding.
- The IDL used to generate a program's code is checked in alongside the
  generated sources as `idl.json` inside that program's `gen` package
  (e.g., `src/main/java/software/sava/idl/clients/<module>/<program>/gen/idl.json`).
  This is the authoritative IDL input for regenerating that program's
  client; the top-level `idls/` directory may also contain IDL inputs, but
  the per-program `gen/idl.json` is what the generated code in that package
  was produced from.
- Hand-written convenience code (e.g., `*Client` / `*ClientImpl` wrappers,
  higher-level helpers) lives alongside — but **outside** — the `gen`
  sub-package, directly under `software/sava/idl/clients/<module>/<program>/`
  or `software/sava/idl/clients/<module>/`.

Supporting files: `idls/` (input IDL definitions), `main_net_programs.json`,
`release-please-config.json`.

## Related source code (symlinks/)

The `symlinks/` directory at the repository root contains symlinks to source
trees of dependencies and related codebases that live outside this repository
on the local machine. Agents should read from these locations when they need to
understand, cross-reference, or debug behavior that originates in a dependency
rather than in `idl-clients` itself.

The `symlinks/` tree is organized into two groups:

- `symlinks/deps/` — sibling Java projects this repo depends on, developed in
  lockstep with `idl-clients`. **Code changes in these repos are permitted**
  when a task requires it (e.g., adding a missing API, fixing a bug in a shared
  library). Coordinate the change with this repo's update. If a
  [sava-software](https://github.com/sava-software)-owned project dependency is
  missing from `symlinks/deps/`, the folder may be created and the repository
  checked out from `https://github.com/sava-software/<COMPONENT_NAME>.git`.
- `symlinks/external/` — third-party / non-sibling repos. Treat as read-only
  reference material; do not modify unless the task explicitly requests it.

Contents of `symlinks/deps/` (sibling Java dependencies — editable):

- `deps/sava` → core Java libraries (RPC, transactions, accounts, Anchor
  codegen support) that this repo's generated clients build on. When you need
  the implementation of base client interfaces, encoding helpers, or
  `PublicKey`/`AccountInfo` semantics referenced from generated code, look
  here first.
- `deps/solana-programs` → Java program bindings and helpers maintained
  alongside `idl-clients`. Useful when investigating program-side behavior or
  shared instruction builders.
- `deps/idl-src-gen` → the source generator that produces the Java code under
  every `gen/` package in this repo. Reference it when investigating why code
  was generated incorrectly, why generation failed, or to regenerate clients.
  See "Regenerating generated code" below for the relevant scripts.

Contents of `symlinks/external/` (third-party / non-sibling repos —
read-only):

- `external/rust-solana-programs` → upstream Rust sources for Solana programs
  whose Java clients are generated here. Authoritative reference for account
  layouts, instruction discriminators, and field semantics when the generated
  Java appears ambiguous.
- `external/scope` → upstream Rust source for the Scope oracle program,
  referenced by `idl-clients-oracles` and downstream consumers.
- `external/solana-program-sdks` → official program SDKs that live in their
  own repos (typically JavaScript/TypeScript) rather than alongside the
  on-chain program under `external/rust-solana-programs`. Useful when
  porting off-chain utility code (math helpers, quotes, slippage/fee
  adjustments, account decoders) into this repo's `*Util` classes or
  sibling `quote/` packages. See the "Referencing upstream SDKs for
  utility code" subsection under "Writing hand-written client wrappers".

### How to use them

- Code changes are allowed in `symlinks/deps/` when the task requires updating
  a sibling Java dependency; everything else under `symlinks/` is read-only by
  default and must not be modified unless the task explicitly requests it.
- When investigating types, IDL definitions, account layouts, or program logic
  referenced from this codebase, prefer reading the corresponding source under
  `symlinks/` over guessing from class/method names. For account/instruction
  semantics that look incorrect or under-documented in the generated Java,
  cross-check against the upstream Rust under
  `symlinks/external/rust-solana-programs` (or `external/scope` for Scope).
- For shared Java behavior (RPC, encoding, base client classes), look under
  `symlinks/deps/sava` or `symlinks/deps/solana-programs` as appropriate.
- Within any repo under `symlinks/deps/`, **ignore nested `symlinks/`
  directories** (e.g., `symlinks/deps/sava/symlinks/`). Those are that repo's
  own dependency links and following them leads out of the intended reference
  scope. Focus instead on the project's own source — primarily `src/main/java/`
  — along with any in-repo documentation (top-level `README*`, `AGENTS.md`,
  `docs/`, Javadoc, and module-level `build.gradle*`). Build outputs,
  generated sources, IDE config, and test fixtures can also be skipped unless
  directly relevant.

### Keeping symlinked repos up to date

The repositories under `symlinks/external/` should be refreshed via `git pull`
before relying on their contents. Entries under `symlinks/deps/` are sibling
Java projects developed in lockstep with this repo; their checkouts are
managed by the developer and should not be pulled implicitly as part of a task
here.

Each symlink target is an independent git checkout on the local machine, so
run `git pull` in each relevant one (on its current branch) at the start of a
task that depends on it:

```sh
for d in symlinks/external/*/; do
  echo "=== $d ==="
  git -C "$d" pull --ff-only || echo "skip: $d"
done
```

Notes:

- Use `--ff-only` to avoid creating accidental merge commits in those external
  repos. If a pull is rejected, investigate that repo manually rather than
  forcing it from this project.
- Do not commit changes inside the symlinked repos as part of work on
  `idl-clients` unless the task explicitly requests it. When you do make code
  changes under `symlinks/deps/`, commit them in their own repository, not
  here.

## Generated vs. hand-written code

Much of the per-program source under `idl-clients-<module>/src/main/java/...`
is generated from IDLs in `idls/` via sava's Anchor codegen. The rule of thumb
is simple: **the package boundary is the contract**.

- Any file under a `gen/` package
  (`software/sava/idl/clients/<module>/<program>/gen/...`, including nested
  packages like `gen/types/` and `gen/anchor/`) is **generated and must never
  be hand-edited**. These files are overwritten on every regeneration and any
  manual changes will be silently lost.
- If you find a bug or missing feature in code under `gen/`, **do not patch
  the generated Java**. Instead, report the issue with as much detail as
  possible, including steps to reproduce and any relevant context. The
  underlying source generator lives at `symlinks/deps/idl-src-gen` and can be
  consulted to understand why a particular file was emitted the way it was
  (or why generation failed). See "Regenerating generated code" below.
- Convenience clients, PDA helpers, and `*Client` / `*ClientImpl` classes that
  wrap generated types live **outside** any `gen/` package and are
  hand-written. These are safe to edit directly.
- When in doubt about whether a file is generated, check its package path: if
  any segment is named `gen`, treat it as generated and off-limits for manual
  edits.

### Regenerating generated code

The source generator lives at `symlinks/deps/idl-src-gen` and ships with two
shell scripts at its root:

- `compile.sh` — builds the generator itself.
- `genSrc.sh` — runs the generator against a JSON configuration to (re)emit
  Java sources into this repo. **Always pass the configuration explicitly via
  `--config <path-to-config.json>`** — `genSrc.sh` does not assume a default
  config, so omitting `--config` will not produce the expected output.

The canonical configuration is `./main_net_programs.json` in this repo, which
lists every IDL/program for which clients are generated here (source module,
target package, program ID, optional IDL URL/file, type-ref rules, etc.). A
typical full regeneration therefore looks like:

```sh
./genSrc.sh --config /absolute/path/to/idl-clients/main_net_programs.json
```

When debugging a single program (or a small subset), it is fine to create a
new, minimal JSON configuration alongside `main_net_programs.json` containing
only the entries you care about, and pass that file via `--config` instead.
This keeps regeneration fast and limits diff noise to the program(s) under
investigation. Do not commit throwaway debug configurations unless the task
explicitly calls for it.

### Including a `symlinks/deps/` project in this build

When a task requires changes in a sibling Java dependency under
`symlinks/deps/` (for example, adding a missing API or fixing a bug in
`deps/sava`) and you want to exercise those changes from `idl-clients`
without publishing a new artifact, you can wire the dependency project into
this Gradle build as a composite/included build. Commented-out examples are
already in place to show the pattern:

- `settings.gradle.kts` — there is a commented `includeBuild("../sava")`
  line. Uncomment the one you need to make Gradle treat the corresponding checkout as part
  of this build.
- `idl-clients-kamino/build.gradle.kts` — the `dependencies { ... }` block
  contains commented `project(":sava:sava-core")` and
  `project(":sava:sava-rpc")` entries. Uncomment the modules you actually
  depend on so the included build's sources are used instead of the
  published artifacts.

The paths in those examples (`../sava`, `:sava:sava-*`) assume the sibling
project is checked out next to `idl-clients` on disk, which is what the
`symlinks/deps/` links already point at. Apply the same pattern to other
`deps/` projects (e.g., `solana-programs`) when needed:
add an `includeBuild(...)` in `settings.gradle.kts` and the matching
`project(":<included-build>:<module>")` dependency in the consuming
module's `build.gradle.kts`.

These edits are intended as local, temporary development aids. Revert them
(restore the comments) before committing unless the task explicitly asks
to make the included build permanent.

## Writing hand-written client wrappers

Most modules expose a hand-written `<Program>Client` interface plus a
package-private `<Program>ClientImpl` that wraps the generated
`<Program>Program` instruction builders. New wrappers (and additions to
existing ones) should follow the conventions below — they are consistent
across modules but not otherwise documented.

Quick jump to the subsections in this section:

- [Layout and shape](#layout-and-shape)
- [Parameter conventions](#parameter-conventions)
- [Convenience `default` methods (PDA / ATA derivation)](#convenience-default-methods-pda--ata-derivation)
- [Extra (non-IDL) accounts (`remaining_accounts`)](#extra-non-idl-accounts-remaining_accounts)
- [Calling the generated builders](#calling-the-generated-builders)
- [Instruction coverage policy](#instruction-coverage-policy)
- [Numeric helpers / `*Util` placement](#numeric-helpers--util-placement)
- [Referencing upstream SDKs for utility code](#referencing-upstream-sdks-for-utility-code)
- [Cross-checking semantics](#cross-checking-semantics)
- [Imports / `module-info.java`](#imports--module-infojava)
- [Test module setup](#test-module-setup)
- [Fast feedback loop](#fast-feedback-loop)

### Layout and shape

- Place both files directly under `software/sava/idl/clients/<module>/`
  (outside any `gen/` package). The interface is public; the `Impl` class is
  package-private and `final`.
- Expose construction via `static createClient(...)` factories on the
  interface, providing two overloads:
  - `createClient(SolanaAccounts solanaAccounts, <Module>Accounts accounts)`
  - `createClient(<Module>Accounts accounts)` — defaults to
    `SolanaAccounts.MAIN_NET`.
- Expose accessors on the interface:
  - `SolanaAccounts solanaAccounts()`
  - `<Module>Accounts <module>Accounts()`
- Each method on the interface returns a single `Instruction` (or a small
  composite where it genuinely makes sense) and mirrors one generated
  instruction builder.

### Parameter conventions

Decide where each account comes from using this rule of thumb:

- **Global / program-wide accounts** — program ids, log authorities, global
  config/state PDAs, global indices, shared vaults, the system program, and
  default token program — come from `<Module>Accounts` / `SolanaAccounts`
  and are **not** method parameters. If a global account needed by a new
  instruction is missing from `<Module>Accounts`, **add it to the accounts
  record** (e.g. `<Module>AccountsRecord`) rather than threading it through
  every method signature.
- **Per-user / per-market accounts** — trader wallets, trader accounts,
  orderbooks, asset maps, splines, conditional-orders accounts, escrow
  accounts, stop-loss accounts, user token accounts, mints — are method
  parameters of type `PublicKey`. The abstract method on the interface
  must take these as explicit `PublicKey` parameters and the `Impl` must
  not derive PDAs in that path (keeps the impl allocation-free and
  seed-agnostic, and lets callers pass overrides such as non-ATA token
  accounts). Convenience derivation belongs in `default` methods on the
  interface (see below).
- **Single-key-derivable PDAs** — when a PDA's seeds are entirely
  determined by one account the caller already passes (e.g.
  `lending_market_auth` is `["lma", market]`), expose it as a derivation
  method on `<Module>Accounts` (returning a `ProgramDerivedAddress` or
  `PublicKey`) and call it from the `Impl` rather than threading the
  derived key through the method signature. This sits between "global on
  `<Module>Accounts`" and "per-call `default` derivation helper on the
  interface": the caller still picks the input (e.g. which market), but
  the derived key never appears in the method signature.

### Convenience `default` methods (PDA / ATA derivation)

The wrapper interface should also expose `default` overloads that derive
program-seeded PDAs and associated token accounts from caller context and
delegate to the explicit-account methods above. This gives callers a
one-call experience for the common path while preserving an escape hatch
for the explicit form.

Rules:

- Derive only accounts whose value is fully determined by other inputs:
  - Program-seeded PDAs whose seeds are constants plus accounts the caller
    already passes (or the program id from `<Module>Accounts`) — use the
    generated `<Program>PDAs` helpers.
  - Associated token accounts derived from `(owner, tokenProgram, mint)` —
    use `AssociatedTokenPDAs.associatedTokenPDA(...)` with the ATA program
    id from `SolanaAccounts.associatedTokenAccountProgram()`. If the
    consuming module doesn't already depend on `idl-clients-spl`, add
    `project(":idl-clients-spl")` to its `build.gradle.kts` and
    `requires transitive software.sava.idl.clients.spl;` to its
    `module-info.java`.
- The `default` method's parameter list should be a strict subset of the
  abstract method's (replace derived params with the inputs needed to
  derive them, e.g. `positionMint` instead of `(position, positionMint,
  positionTokenAccount)`).
- The `default` method body must simply call the abstract method with the
  derived values — no extra logic, branching, or validation.
- Expose the individual derivations as small `default` helpers on the
  interface (e.g. `derivePositionKey`, `deriveOracleKey`, `deriveATA`) so
  callers can reuse them without depending on the convenience overload.
- For globally-fixed program-seeded PDAs (singletons whose seeds are
  constants only), add them to `<Module>Accounts` and auto-wire them in
  the `Impl`; they are not method parameters at all.
- Generated `<Program>PDAs.*` methods return `ProgramDerivedAddress`
  (a `(publicKey, bump)` pair), **not** `PublicKey` — call `.publicKey()`
  before passing the result to a generated builder.

### Extra (non-IDL) accounts (`remaining_accounts`)

Some programs read additional accounts from `ctx.remaining_accounts` that
are **not** declared in the IDL's `accounts:` list and therefore are **not**
emitted by the generated builders. Common cases:

- **Token-2022 transfer-hook accounts** — required whenever a mint used by
  the instruction has the `TransferHook` extension enabled. Per such mint,
  append the hook program id, the per-mint `extra-account-metas` PDA
  (seeds: `["extra-account-metas", mint]`, owned by the hook program), and
  every account listed by that PDA after resolving any seed-based metas.
- **Per-instruction "supplemental" slices** — e.g. extra tick arrays on
  Whirlpool swaps, extra oracle/market keys on perp cranks, etc. These are
  usually paired with an instruction arg (often `RemainingAccountsInfo` or
  similar) that tells the program how many remaining accounts belong to
  each category and in what order they appear.

The generated `Instruction` only carries the IDL accounts; the caller must
append the rest via `Instruction.extraAccount(...)` / `extraAccounts(List)`
**after** calling the builder. When such extras exist for a program:

- Add a small helper next to the `<Program>Client` (e.g.
  `<Program>RemainingAccounts`) that builds both the instruction-arg
  payload (when present) and the matching ordered `List<AccountMeta>`, and
  exposes a static `append(Instruction, ...)` to wire them onto the
  produced `Instruction`. See `idl-clients-orca/.../WhirlpoolRemainingAccounts`
  for the reference pattern.
- Document the requirement on the affected interface methods (or on the
  client type itself) — at minimum: which slice kinds apply to which
  instruction, the order they must appear in, and that the matching
  arg/info must be passed to the builder.
- Cross-check the upstream Rust (`parse_remaining_accounts`, manual
  `ctx.remaining_accounts.iter()`, or transfer-hook helpers like
  `spl_token_2022::onchain::invoke_transfer_checked`) to confirm exactly
  which accounts are read and in what order. The IDL alone will not show
  these.
- **Writability / signer flags must match the handler.** Every appended
  `AccountMeta` must reflect what the program actually does with it —
  the IDL won't help. Read the Rust handler's `next_account_info(...)`
  chain (or `for r in ctx.remaining_accounts` loop) and look at every
  borrow: `try_borrow_mut_data()` / writes via `Account::reload` / token
  transfers out of it ⇒ writable; `try_borrow_data()` / read-only
  oracle/reserve lookups ⇒ read-only. Signer flags on remaining accounts
  are extremely rare; default to non-signer and only flip when the
  handler calls `is_signer`. Wrong writability is the single biggest
  source of "compiles but transaction rejected" bugs.

### Calling the generated builders

Each generated `<Program>Program` exposes two overloads per instruction: a
long `(AccountMeta invokedProgram, PublicKey programId, ...explicit
accounts..., params)` form and a shorter convenience form that resolves
some accounts from defaults. **Hand-written wrappers call the long form**,
passing `accounts.invoked<Program>Program()` as the `AccountMeta` and its
`.publicKey()` as `programId`. This makes the wrapper's account sourcing
explicit and avoids hidden defaults leaking through.

### Instruction coverage policy

By default, only wrap **trader / end-user facing** instructions. Skip
admin and permissioned instructions unless a specific non-admin use case
requires them. Typical categories to skip:

- Permission / authority management (e.g. `set*Permission*`, role or
  authority setters).
- Parameter / configuration setters intended for program admins (e.g.
  `update*`, `set*` on global or market config).
- Lifecycle management of program-owned accounts (e.g. `init*`,
  `close*`, `tombstone*` on markets, pools, or other shared state).
- Forced / privileged user-state mutations (e.g. `forceCancel*`,
  `liquidate*`, forced position closes or transfers).
- Diagnostic / logging-only instructions (e.g. `log*`).

List **every skipped instruction** in the wrapper's PR/commit
description, grouped by the skip categories above, so they can be added
later if a non-admin caller appears.

### Numeric helpers / `*Util` placement

Pure numeric helpers ported from upstream SDK math modules go on
`<Module>Util` (or `<Module><Program>Util` when the module has more than
one program). Helpers that return composite results (quotes, bounds,
status enums) live in a sibling `quote/` package as small records,
exported separately so `<Module>Util` stays primitive-typed. See
`idl-clients-orca/.../OrcaUtil` and `idl-clients-orca/.../quote/` for
the reference pattern.

### Referencing upstream SDKs for utility code

Beyond the on-chain Rust program sources, many Solana programs ship an
official SDK — typically a Rust crate (under `rust-sdk/`) and/or a
JavaScript/TypeScript package — that contains substantial off-chain utility
code worth surfacing in this repo's hand-written wrappers and `*Util`
classes. Examples include math conversions (tick ↔ sqrt-price, price ↔
tick), quote / amount-estimation helpers, slippage and transfer-fee
adjustments, fixed-point primitives, and account/bitmap decoders.

When wiring or extending a `<Program>Client`, look for an upstream SDK and
port the relevant helpers into the matching module (`<Module>Util`, a
sibling `quote/` package, etc.). The two reference locations are:

- **Same-repo SDK** — when the SDK lives alongside the on-chain program,
  it is reachable from `symlinks/external/rust-solana-programs/<program>/`.
  Typical layout: `rust-sdk/core/src/{math,quote,...}` for the Rust crate
  and `ts-sdk/` for the JS/TS package. The Whirlpools SDK under
  `symlinks/external/rust-solana-programs/whirlpools/{rust-sdk,ts-sdk}/`
  is the canonical example — `OrcaUtil` and the `quote/` package mirror
  `rust-sdk/core/src/math/` and `rust-sdk/core/src/quote/` byte-exactly.
- **Separate-repo SDK** — when the SDK is published in its own repo
  (common for JS/TS-only SDKs), it is symlinked under
  `symlinks/external/solana-program-sdks/<program>/`. Treat it as
  read-only reference material like the rest of `symlinks/external/`.
  If the upstream SDK for a program is not already symlinked there,
  **stop and ask the user before cloning anything** — the same rules as
  the "Cross-checking semantics" subsection below apply (no guessed URLs,
  no "close enough" repos).

Guidance for porting:

- Prefer the Rust SDK when both exist — the TS SDK is often a thin
  re-export over the Rust crate (e.g. Whirlpools' `ts-sdk/core/src/lib.rs`
  is literally `pub use orca_whirlpools_core::*;`), so the Rust source is
  the authoritative reference for numeric / fixed-point routines.
- Port pure functions verbatim (same constants, same shifts, same
  rounding) so the Rust crate's `#[cfg(test)]` vectors can be translated
  one-to-one as JUnit tests; this is the cheapest way to prove parity.
- Reuse generated `gen/types/` records (e.g. `Whirlpool`, `Position`,
  `Tick`) directly when a helper consumes on-chain state — do not invent
  parallel facade types.
- When a helper depends on values that aren't part of any on-chain
  account (e.g. Token-2022 `TransferFee`), add a small input record next
  to the helpers and document that callers must populate it from the
  appropriate mint extension at runtime.
- Skip SDK code that is purely a Rust/TS-side instruction builder — that
  job is already covered by the generated `<Program>Program` and the
  hand-written `<Program>Client` in this repo.
- Note in the PR/commit description which SDK modules were ported and
  which were intentionally deferred (e.g. a heavy swap quoter that
  iterates an on-chain account sequence) so follow-up work is discoverable.

### Cross-checking semantics

When the generated Java account list is ambiguous (e.g. two
`globalConfig`-shaped parameters, or accounts whose role isn't obvious
from the name), the authoritative reference is the upstream Rust under
`symlinks/external/rust-solana-programs`, not the IDL JSON or the
generated method/parameter names.

If the program's Rust source isn't already present under
`symlinks/external/`, **stop and ask the user before cloning anything**.
Some deployed programs (notably the Phoenix `eternal` / `ember` programs
this repo currently targets) have **no public open-source repo for the
deployed version** — older public repos like `phoenix-v1` do **not**
correspond to what's on chain and must not be used as a cross-check
source. Do not guess a URL, do not clone a "close enough" repo, and do
not symlink one in unilaterally. Ask the user to point at (or provide) a
trustworthy Rust checkout; if none exists, note the ambiguity in the
PR/commit description and proceed conservatively rather than fabricating
a reference.

### Imports / `module-info.java`

Pulling generated `types/` records from a sibling `gen/` package within
the same module does **not** require a `module-info.java` change. Adding
a brand-new top-level module **does** require updating both the module's
`module-info.java` and `gradle/aggregation/build.gradle.kts`.

### Test module setup

When adding the first unit tests to a module, mirror the JUnit setup
from `idl-clients-kamino/build.gradle.kts`:

```kotlin
// build.gradle.kts
testModuleInfo {
  requires("org.junit.jupiter.api")
  runtimeOnly("org.junit.jupiter.engine")
}
```

No `module-info.java` is needed under `src/test/java/` — the build's
`testModuleInfo` block configures the test module on the fly. `idl-clients-kamino`
and `idl-clients-orca` are the canonical references.

### Fast feedback loop

While iterating on a single module's wrapper, prefer:

```sh
./gradlew :idl-clients-<module>:compileJava
```

over the full `./gradlew check` — it gives a much faster turnaround and
catches the kinds of errors (wrong builder overload, missing import,
type mismatch on a generated `types/` record) that dominate this work.

When the module has tests, `./gradlew :idl-clients-<module>:test`
rebuilds only what's needed and runs the module's JUnit suite.
