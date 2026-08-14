# tools/

Standalone verification scripts. Python 3 only — no packages to install, nothing
wired into Gradle, nothing run by CI. They exist because the checks they automate
were otherwise re-derived by hand each time, and each re-derivation reintroduced
the same false positives.

They are **investigative aids, not gates**. `qualityGate` is the gate.

`idl_probe.py` was removed on 2026-08-14. It simulated declared instructions
against the deployed program to find ones the program no longer dispatches. The
failure it looked for reports itself: an instruction that is gone fails every
call, immediately and visibly, for anyone using it — so the probe bought advance
notice of something that announces itself, at the cost of one simulation per
instruction against a rate-limited endpoint.

It never covered the whole corpus, and an earlier draft of this note priced it as
though it did. Its selection was `if i.get('discriminator')`, so it probed only
instructions carrying an Anchor-style `discriminator` and skipped every Shank
`discriminant` one — 1,039 against 70 when the corpus was counted on 2026-08-14,
and `idl-clients-bundle/config/pitest/README.md` records 1026 across 34
Anchor-shaped programs at the time of its own sweep —
blind to precisely the instructions whose one-byte dispatch was this repository's
largest generator defect. It also had no path for Codama's nested instruction
arrays, latent today only because the corpus has none.

Two defects, with distinct effects rather than one shared label. Reading the
`Program log: Instruction: …` line as the dispatch signal reported `route` and
`route_v2` **dead** when both were live, because Jupiter strips that `msg!` on
the hot path — a false finding. Calibrating only on error 101 made Jupiter come
back **inconclusive**, because it answers a garbage discriminator with
`InvalidAccountData`; that one produced no answer rather than a wrong one. The
first cost a day's investigation to disprove. Neither was covered by a test,
because the file had none, and a check nobody should fully trust is worse than no
check.

| Script | Answers | Cost |
|---|---|---|
| `ground_truth.py` | Does our account order match the program's Rust? | instant, local |


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
