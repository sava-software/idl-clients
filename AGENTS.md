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
