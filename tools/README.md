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

Running this at generation time inside idl-src-gen was considered and rejected:
doing it properly means tracking program deploys over time, which is a
monitoring service rather than a generator feature. See "If this ever becomes a
service" in [../docs/PROGRAM_VERIFICATION.md](../docs/PROGRAM_VERIFICATION.md)
for the design conclusion, which is worth reading first — the obvious approach
does not work.

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

## `tick_margin_sweep.py`

Proves the equivalence of the `OrcaUtil.sqrtPriceX64ToTickIndex` lower-margin
mutants (the `log-margin family` rows in
`idl-clients-bundle/config/pitest/orca-accepted.csv`): a Python mirror of both
tick ladders and the 14-bit log approximation, pinned to
`MIN/MAX_SQRT_PRICE_X64` and tick 0, that checks every one of the 887,272 tick
boundaries for an approximation overshoot — the only condition under which the
mutants could diverge (see the sweep's docstring and the acceptance section in
the bundle's `config/pitest/README.md`). Zero overshoots as of 2026-07-23;
re-run after any change to the log constants, error margins, or factor tables.

## Adding to these

Keep them dependency-free and runnable from the repo root. If a script starts
needing per-program special cases beyond a flag, that is a sign the case belongs
in `docs/PROGRAM_VERIFICATION.md` as prose rather than in code — the analysis is
the durable part, the parsing is not.
