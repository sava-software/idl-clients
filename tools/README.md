# tools/

Standalone verification scripts. Python 3 and `curl` only — no packages to
install, nothing wired into Gradle, nothing run by CI. They exist because the
checks they automate were otherwise re-derived by hand each time, and each
re-derivation reintroduced the same false positives.

Both are **investigative aids, not gates**. `qualityGate` is the gate.

| Script | Answers | Cost |
|---|---|---|
| `idl_probe.py` | Does the deployed program still have every instruction our IDL declares? | a few min, read-only RPC |
| `ground_truth.py` | Does our account order match the program's Rust? | instant, local |

## `idl_probe.py`

```shell
python3 tools/idl_probe.py                    # from the repo root
SOLANA_RPC=https://my-endpoint python3 tools/idl_probe.py
```

Simulates a transaction carrying nothing but each instruction's discriminator
and reads the dispatch result. Never signs or sends. Exits non-zero only on an
*unexpected* undeployed instruction — known-benign ones live in
`ACCEPTED_UNDEPLOYED` with a reason, so it is safe to re-run as a periodic check.

Worth running after any upstream program deploy. It found a shipped bug once:
marginfi removed `lending_account_clear_emissions`, and our client kept calling
it, failing 100% of the time on mainnet.

Reports non-Anchor programs as `INCONCLUSIVE` rather than guessing — they have
no fallback handler, so the signal does not apply. Use `ground_truth.py` there.

The classification and wire encoding also live in idl-src-gen as
`DeployedInstructionProbe`, unit-tested and pinned byte-for-byte against the
message this script sends. **Change both if you change the encoding.**

That duplication is deliberate and expected to persist. Running the probe at
generation time was considered and deferred — doing it properly means tracking
program deploys over time, which is a monitoring service rather than a generator
feature (the reasoning, including why an added/removed IDL diff is *not*
sufficient, is recorded in idl-src-gen's `AGENTS.md`). So this script stays the
runnable version, and the Java class is the tested primitive if that project
happens.

## `ground_truth.py`

```shell
python3 tools/ground_truth.py anchor <rust-dir>        <Program.java>
python3 tools/ground_truth.py shank  <instructions.rs> <Program.java>

# per-program normalisations, see the docstring
--strip-suffix=Context            # CCTP names its structs AcceptOwnershipContext
--drop-trailing=whirlpoolProgram  # Orca's IDL adds an account its Rust has not
```

**Read the output critically.** Most differences it reports are artifacts, and
the docstring enumerates the traps that have each cost real time: auto-wired
sysvars, structs matched against the wrong program in a monorepo, per-program
struct naming, and published IDLs that do not match their repo. `compared 0` is
a failure to compare, not a pass — hence the compared count is always printed.

Known-good invocations, useful as a smoke test after changing the script:

| Program | Expected |
|---|---|
| Squads v4 | `compared 18 match 18` |
| CCTP Message Transmitter V2 (`--strip-suffix=Context`) | `compared 15 match 15` |
| Orca Whirlpools (`--drop-trailing=whirlpoolProgram`) | `compared 61 match 61` |
| Pyth Solana Receiver | `compared 7 match 7` |
| Metaplex Token Metadata (shank) | `compared 58 match 57` — the known `print` IDL gap |
| Solana Attestation Service (shank) | `compared 12 match 12` |

Paths to the Rust live in `AGENTS.local.md`.

## Adding to these

Keep them dependency-free and runnable from the repo root. If a script starts
needing per-program special cases beyond a flag, that is a sign the case belongs
in `docs/PROGRAM_VERIFICATION.md` as prose rather than in code — the analysis is
the durable part, the parsing is not.
