# tools/

Standalone verification scripts. Nothing wired into Gradle, nothing run by CI.
They exist because the checks they automate were otherwise re-derived by hand each
time, and each re-derivation reintroduced the same false positives.

**Everything here needs something outside the repository.** That is the rule for what
belongs in `tools/` rather than in a test: `GroundTruth.java` needs a checkout of the
program's Rust, and `stake-vectors.mjs` needs a `solana-program/stake` checkout to
resolve against. A check that needs nothing outside the repo is a test, and lives with
the code it reasons about — `tick_margin_sweep.py` was here until 2026-08-15 and is
now `OrcaTickMarginSweep` in `idl-clients-bundle`'s test sources, beside the
`OrcaUtil` whose accepted mutants it clears.

`GroundTruth.java` runs straight from source on the JDK the build already requires —
`java tools/GroundTruth.java`, no build step — and is deliberately **not** a Gradle
module: a module would join the publish and would owe `mutationOwnershipAudit` either
a mutation suite or an argued decline, which is a lot of ceremony for a diff tool.
`stake-vectors.mjs` installs nothing here.

These were Python until 2026-08-14. The port was verified byte-for-byte against the
scripts it replaced, over every invocation recorded below plus three error paths, with
outputs and exit statuses matching exactly — the only intended difference being the
`java …` command in the usage text. Two defects found while doing that are described
under `GroundTruth.java`.

They are **investigative aids, not gates** — with one exception, noted below:
`stake-vectors.mjs` writes a test fixture, so what it produces *is* checked by
`qualityGate`, even though running it is not.

`idl_probe.py` was removed on 2026-08-14. It simulated declared instructions to
find ones the deployed program no longer dispatches; that failure reports itself,
because an instruction which is gone fails every call immediately for anyone
using it. It also had known defects and no tests. `docs/PROGRAM_VERIFICATION.md`
has the manual procedure and its limits; the reasoning and the coverage
measurements are in the commits that removed it.

`stake-idl.mjs` was removed on 2026-08-25, and it was never a check — it *produced*
the IDL Stake was generated from. It applied `solana-program/stake`'s own `before`
visitors to the intermediate `idl.json` at that repository's root, and committed the
result as `idls/codama/spl/stake.json` for idl-src-gen to read, because that root
`idl.json` was not a usable IDL on its own. Upstream's #501 split the intermediate
out to `interface-idl.json` and now publishes the *visited* tree as `idl.json`,
leaving the derivation nothing to do: the file it wrote and the file upstream
publishes differ only in JSON key order and one empty `accounts: []` that idl-src-gen
defaults anyway, and regenerating across the switch changed no Java. Stake now reads
`idlURL` off `refs/heads/main` like every other codama program in
`main_net_programs.json`, which it had been the sole exception to. What that gives up
is the pin: the derived file was committed, so upstream could not reach generated code
until someone re-ran the script and reviewed the diff. Stake tracks upstream head on
every run now, and what catches a change is the movement report. Not the two Stake
tests: both are about instruction *data* and are structurally blind to an account
list. #520 (merged 2026-08-31) is the measurement — it dropped the clock, rent and
stake-history sysvars and the retired stake config from ten instructions, touched no
argument, and left `StakeOnChainInstructionTests` and `StakeReferenceEncodingTests`
green and unedited. Ten assertions in `SPLClientTests` failed instead, and only
because they are transcribed from the same `idl.json` the builders are generated
from, so they can see a list that *moved* and never one that moved *wrongly*.

| Script | Answers | Cost |
|---|---|---|
| `GroundTruth.java` | Does our account order match the program's Rust? | instant, local |
| `stake-vectors.mjs` | Does our Stake encoder agree with upstream's generated JS client? | seconds, needs their checkout |


## `GroundTruth.java`

```shell
java tools/GroundTruth.java anchor <rust-dir>        <Program.java>
java tools/GroundTruth.java shank  <instructions.rs> <Program.java>

# per-program normalisations, see the class doc
--strip-suffix=Context            # CCTP names its structs AcceptOwnershipContext
--drop-trailing=whirlpoolProgram  # Orca's IDL adds an account its Rust has not
```

**Read the output critically.** Most differences it reports are artifacts, and
the class doc enumerates the traps that have each cost real time: auto-wired
sysvars, structs matched against the wrong program in a monorepo, per-program
struct naming, and published IDLs that do not match their repo. `compared 0` is
a failure to compare, not a pass — hence the compared count is always printed.

What the six recorded invocations print, **measured 2026-08-14** against the
reference clones as they stood that day:

| Program | Output |
|---|---|
| Squads v4 | `compared 23 match 23` |
| CCTP Message Transmitter V2 (`--strip-suffix=Context`) | `compared 15 match 15` |
| Orca Whirlpools (`--drop-trailing=whirlpoolProgram`) | `compared 61 match 61` |
| Pyth Solana Receiver | `compared 7 match 7` |
| Metaplex Token Metadata (shank) | `compared 58 match 58` |
| Solana Attestation Service (shank) | `compared 12 match 12` |

These are a smoke test for *the tool*: change it, re-run, expect the same output
against the same clones. They are not a standing claim about the clients, because
both inputs move — a `git pull` in a reference clone or a regeneration here shifts
the counts without anything being wrong. Date any number you put in this table.

Squads read `compared 18 match 18` when it was last recorded and has gained account
structs upstream since. Metaplex read `compared 58 match 57`, described as a `print`
IDL gap; both halves of that were wrong, and finding out why fixed two defects in
this tool rather than in any client — see below.

Paths to the Rust live in `AGENTS.local.md`.

### What the 2026-08-14 triage found

The tool reported nine differences against Metaplex Token Metadata. **All nine were
its own fault**, and the two causes were costing coverage everywhere, not just there.

*Names bound to the wrong account list.* The Java side was read with one regex,
`List<AccountMeta> (\w+)Keys\(.*?\)\s*\{\s*return List\.of\(` under DOTALL. A builder
with an optional account does not return `List.of(..)` — it fills an `ArrayList` with
`keys.add(..)` behind a null check — so from such a builder's name the `.*?` ran on
past the whole method and paired it with the *next* builder's accounts. Eight of the
nine were that: `mintNewEditionFromMasterEditionViaVaultProxy` was reported with one
account against the Rust's seventeen, while its builder had all seventeen in the right
order and `puffMetadata`'s list had been read in its place. Across this repository the
regex mis-bound 18 of the 1,337 `*Keys` builders and never saw another 44 at all. It
now locates each declaration and reads only that declaration's body, in both shapes.

*Commented-out Rust counted as declarations.* The ninth was `print`, rust=20 java=18.
Metaplex keeps `#[account(18, ..)]` and `#[account(19, ..)]` commented out above
`Print`, with a doc comment saying those accounts arrive through remaining-accounts
instead. The Anchor path had always stripped comments; the Shank path had not. Our
18-account client was correct and the IDL was correct.

Both fixes narrow what the tool reports rather than widen it, which is the direction
that matters: this thing is cited in both modules' `declineExclusionAudit` as part of
what carries the correctness of generated code, so a confident false positive spends
someone's afternoon and a false negative hides a real transposition. Every one of the
six invocations now matches.

## `stake-vectors.mjs`

```shell
cd <solana-program/stake checkout>/clients/js && pnpm install --frozen-lockfile
cd <this repo> && node tools/stake-vectors.mjs <that checkout>
```

Writes `idl-clients-spl/src/test/resources/stake/reference-vectors.txt`: one
instruction-data encoding per line, produced by upstream's own generated
JavaScript client. `StakeReferenceEncodingTests` builds the same arguments through
our generated builders and compares, which is the only Stake check that is not a
round trip through code this repository generated — a builder and the reader
beside it come from one IDL and agree by construction, so on their own they cannot
see a systematic change to the wire format.

Two things it is not. It is not ground truth: since 2026-08-25 our client and
upstream's JavaScript one are generated from the same file — the visited `idl.json`
upstream publishes — so they share an input exactly rather than merely descending
from a common pipeline, and the only evidence that input matches the deployed
program is the mainnet fixture in `StakeOnChainInstructionTests` — which records one
instruction's `data` and nothing else, so it ties the wire format to chain and is
silent on the account lists declared beside it in the same file. And it is not a
check you run — the bytes are committed and `qualityGate` compares against them on
every build.

**Do not re-run this on a schedule.** Not because the program cannot move — it can —
but because these vectors track instruction *data*, and upstream's repository moving
is not evidence that data moved. Measured twice: regenerating on 2026-08-25 across
upstream #498, #500 and #501 and a client bump from 0.8.0 to 0.9.0
(`@solana/kit` 7 to 8) reproduced all 38 vectors byte for byte, changing only the
three provenance lines in the header; re-deriving on 2026-08-31 against head
`32c334e` — #520, which removed four accounts from each of ten instructions —
reproduced all 38 again, because #520 changed no argument.

**What is not the reason is immutability, which this section used to claim.**
`6WU8Nxarf9fudRK5atWwjLY4vFaw5UrrWhL88qz7iCMJ` does carry `authority: null` (checked
against mainnet on 2026-08-25 at slot 441627724), but `Option::None` forecloses only
*transaction*-driven upgrades. The runtime replaces the ELF at feature activation,
and that is how this program reached `program@v5.0.0`: the Agave gate
`upgrade_bpf_stake_program_to_v5` (`STk5Xj8hdAx3sTzmtJ3QysKkq6X2A3yj73JtxttiRyk`)
activated at slot 427248000, which is byte-identical to
`ProgramData.last_deploy_slot` — the install left a deploy-slot move and no
transaction to read it from. `upgrade_bpf_stake_program_to_v5_1`
(`s51VGwCAgebo2745DSUris72RavoLkXGUmVJosESCXr`) is staged behind it, nine zero bytes
on mainnet as of 2026-08-31. Seventeen instructions and this wire format are what is
deployed today, not what is guaranteed; a gate activating on mainnet is the trigger
to look, and a calendar still is not. `docs/PROGRAM_VERIFICATION.md` has the full
method.

Two things would justify running it. If the comparison ever fails, the committed
bytes say *that* the generated encoder diverged and re-deriving says *who is right*.
And if upstream's IDL grows an instruction, `everyInstructionHasAVector` fails until
one has a vector — hand-authoring those bytes would void the whole point, which is
that they come from an implementation neither this repository nor its generator
produced. A new instruction is itself worth a look before it is worth a vector: this
instruction set grows by feature gate, not by deploy, so an addition upstream is a
question about which gate carries it and whether that gate is live on mainnet before
it is a question about bytes. Either way the diff is the review.

## Adding to these

First ask whether it belongs here at all: if the check needs nothing outside this
repository, it is a test, and it should live next to the code it reasons about where
`check` will run it. Otherwise keep it runnable from the repo root, and keep what it
needs out of the repo — `GroundTruth.java` uses nothing but the JDK and stays out of
`settings.gradle.kts`, and `stake-vectors.mjs` borrows a checkout's `node_modules`
rather than adding a package manifest here. A new language is a decision, not a
convenience: it needs a `.gitignore` whitelist rule, and whoever runs the tool next
has to already have it. If a script starts needing per-program special cases beyond
a flag, that is a sign the case belongs in `docs/PROGRAM_VERIFICATION.md` as prose
rather than in code — the analysis is the durable part, the parsing is not.
