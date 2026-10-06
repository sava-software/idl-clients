# Mutation-testing records — `idl-clients-bundle`

This file is the registry of what the mutation ratchet accepts in this module and why: one
argument per family label the accepted-baseline rows carry, one cause per audited timeout member,
what each row with no argument in force is owed, and the measurements behind each suite's
mutator set. The policy it applies is sava-build's `HARDENING.md` and the task reference is
`hardeningHelp`; this file restates neither, beyond the rules for writing its own records. The
counts of rows and labels are what `pitest<Suite>Verify` and `pitest<Suite>Debt` print, never
prose. The journal this registry replaced is `HISTORY.md` beside it: this README as it stood on
2026-10-05, kept verbatim and unmaintained.

Row format in `<suite>-accepted.csv`: `class,method,mutator,STATUS # <family> # line N`. The key
is line-less; the `# line` tag and the current PIT report are the only source locators, which is
why the arguments below name classes, methods and branches and never source lines. Identical rows
are sibling mutants, compared as a multiset, and a family's "Covers" list says which sites they
are. Every label on a row must appear in this file as the literal `# <label>` text, which is how
the build resolves it (the verify and debt listings warn when one does not), so a family renamed
on its rows is renamed here in the same change; `# untriaged`, the label a refresh seeds, marks
debt not yet argued and needs no definition. Each suite's section argues that suite's rows, so a
label shared by suites has a bullet in the section of each suite where it covers a member, scoped
to that suite's members.

An accepted row that is not `# untriaged` must have its reason here, and the reason is one of
two: a mutant equivalent in observable behaviour, or a specifically named capability the
deterministic harness lacks; never "hard to test". Each family bullet states the mechanism and its
independent oracle and, where its premise yields one, the escape that would make the mutant
observable; its "Covers" list names the members it was argued for, and a label never authorises a
superficially similar mutant. A row whose label's argument does not hold for it is in no "Covers"
list: its suite's "Declined and untriaged debt" names it with the label it still carries, what
the mutant changes and what would pay it, and it is debt, not an acceptance, until it is paid,
argued or relabelled `# untriaged`. Accepted rows and the provenance stamps beside them
(`<suite>-pitest-version`, `<suite>-pitest-toolchain.tsv`) are written only through the plugin's
writer tasks, and a `# line` tag is refreshed with `pitest<Suite>BaselineRetag`, never by hand;
the one hand edit a row takes is its label, replaced in triage with a fresh history-free run of
the suite behind it. An argument is updated in place when its code or its members change; what a
run counted, added or pruned is left to the run's output and to git.

The last part of this file, from the heading "Ground-truthing account order against the
programs' Rust" on, is not mutation content: it is the program-verification and IDL-override
record that `AGENTS.md` and `docs/PROGRAM_VERIFICATION.md` point at. Its sections are carried
over from the journal as they stood, dated findings included, and were not re-read against the
current configuration when this registry was written.

## Suites

The `hardening {}` block of `idl-clients-bundle/build.gradle.kts` is the authoritative
declaration of each suite's targets, exclusions, tests and mutators; this is the reading guide.
Every suite targets a package wildcard with exclusions, never an allowlist, so a new hand-written
class is mutated by default. The split into three suites means the final mutation gate before a
push runs only the suites the unpushed range can reach. It does not change coverage:
`pitestClients` is a catch-all by exclusion, taking every hand-written class the other two do not.
Each suite's own section says what it mutates and which tests it runs.

All three exclude:

- Generated `software.sava.idl.clients.*.gen.*` code. It is idl-src-gen output and never edited
  here, so a ratchet on it would pin code this repository cannot change, and its boilerplate
  would bury the hand-written signal. Each suite records the exclusion with
  `declineExclusionAudit`, whose reason names what carries that code's correctness instead.
- Test and fuzz sources, which share the recompiled root: the `*Test*` and `*Fuzz*` globs, and
  `software.sava.idl.clients.kamino.scope.entries.ResourceUtil`, a test helper neither glob
  matches.
- The git-ignored `Integ` scratch mains, present on a developer machine and absent in CI:
  `recompileExcludes` keeps `Integ.java` out of the PIT and Jazzer recompile, and the
  `software.sava.idl.clients.*.Integ` exclusion names them too, so neither the mutated
  population nor the tool class path differs between the two.

## Mutator sets and their trials

Every suite runs PIT's `STRONGER` group, which `StandardMutatorGroups` in the PIT 1.30.0 jar
defines as `DEFAULTS` (`CONDITIONALS_BOUNDARY`, `INCREMENTS`, `INVERT_NEGS`, `MATH`, `RETURNS`,
`VOID_METHOD_CALLS`, `REMOVE_CONDITIONALS_ORDER_ELSE` and `REMOVE_CONDITIONALS_EQUAL_ELSE`) plus
`REMOVE_CONDITIONALS_ORDER_IF`, `REMOVE_CONDITIONALS_EQUAL_IF` and `EXPERIMENTAL_SWITCH`. Neither
`DEFAULTS` nor `STRONGER` includes the three experimental mutators below, so each suite names the
ones it runs:

- `pitestOrca`: `STRONGER` plus `EXPERIMENTAL_BIG_INTEGER` and `EXPERIMENTAL_NAKED_RECEIVER`.
- `pitestScope`: `STRONGER` plus `EXPERIMENTAL_NAKED_RECEIVER`.
- `pitestClients`: `STRONGER` plus `EXPERIMENTAL_BIG_INTEGER`, `EXPERIMENTAL_BIG_DECIMAL` and
  `EXPERIMENTAL_NAKED_RECEIVER`.

The trials below all ran on open-source PIT 1.25.8, before ArcMutate and PIT 1.30.0 joined the
toolchain, so today's populations differ from their numbers; they are the measurements each
choice was made on.

### `EXPERIMENTAL_BIG_INTEGER` and `EXPERIMENTAL_BIG_DECIMAL`

`MathMutator` rewrites primitive bytecode arithmetic (`IADD`, `ISUB`, `LMUL` and the rest).
Arithmetic on `BigInteger` and `BigDecimal` is method calls (`add`, `subtract`, `multiply`,
`divide`), which those opcodes never touch, so under `STRONGER` alone every Q64.64 fixed-point
conversion, fee computation and liquidity estimate in `OrcaUtil`, `WhirlpoolQuote` and
`DlmmUtils`, the arithmetic most likely to hide an off-by-one that silently misprices, would go
unmutated, and a high kill rate there would measure the conditionals and the return values around
the math rather than the math. These two mutators rewrite those calls, and they work on Java 25
from PIT 1.25.8. Trialled on 2026-07-20 under PIT 1.25.8, comparing each suite's `STRONGER`
population with `STRONGER` plus both:

| Suite | `STRONGER` | + Big mutators | New | New killed | New survivors |
|---|---|---|---|---|---|
| `orca` | 541 | 655 | +114 | 110 (96%) | 4 |
| `clients` | 1367 | 1417 | +50 | 49 (98%) | 1 |
| `scope` | 354 | 354 | 0 | — | — |

Every new mutant came from `EXPERIMENTAL_BIG_INTEGER`; `EXPERIMENTAL_BIG_DECIMAL` fired in no
suite. `EXPERIMENTAL_BIG_INTEGER` was enabled on `orca` and `clients` the same day and left off
`scope`, where it generated nothing.

`EXPERIMENTAL_BIG_DECIMAL`'s zero did not last as code was added. sava-build 21.5.14 added a
blind-spot scan to every `pitest<Suite>` run, which warns when the classes a suite mutates hold
`BigInteger` or `BigDecimal` arithmetic that no enabled mutator rewrites. On 2026-07-25 it found
`BigDecimal` arithmetic in `clients`, three classes and eight call sites, and a re-trial there
(PIT 1.25.8) generated 2, both killed, in `DlmmUtils.binStepBase` and `KaminoUtil.toSf`; the
mutator was enabled on `clients` that day. The same re-trial measured `EXPERIMENTAL_BIG_INTEGER`
again: 114 generated on `orca` with 2 unkilled, and 48 on `clients` with none. `orca` holds no
`BigDecimal` arithmetic and `scope` neither kind, so the scan has nothing to advise in either
suite and no decline is recorded.

### `EXPERIMENTAL_NAKED_RECEIVER`

The structural sibling of the gap above: a call whose declared return type is the type it is
invoked on is an expression, so `VoidMethodCallMutator`, which removes only calls that return
nothing, never fires on it. This mutator replaces such a non-static call with its receiver,
whether the call returns that receiver, as a builder-style write or a `StringBuilder.append`
does, or a new value, as fluent `BigInteger` and `BigDecimal` math does. Trialled on 2026-07-23
with sava-build 21.5.9's `pitestMutatorTrial -PtrialMutators=EXPERIMENTAL_NAKED_RECEIVER`
(PIT 1.25.8):

| Suite | Generated | Killed by existing tests | Unkilled |
|---|---|---|---|
| `scope` | 6 | 6 (100%) | 0 |
| `orca` | 119 | 113 (94%) | 6 |
| `clients` | 245 | 179 (73%) | 66 |

It fired in every suite, so it is enabled in every suite.

## orca (`pitestOrca`)

`pitestOrca` mutates `software.sava.idl.clients.orca.*`: `OrcaUtil`'s tick, sqrt-price, fee,
slippage, position-bundle and token-delta math with its PDA derivations, the `WhirlpoolQuote` fee,
reward and liquidity quotes with their records, `WhirlpoolRemainingAccounts`, `OrcaAccounts` and the
`OrcaWhirlpoolsClient` builders. Of the module's shared exclusions, the generated-code one takes
the `orca.whirlpools.gen` client here. Its tests are the `software.sava.idl.clients.orca.*Test*`
classes except `OrcaTickMarginSweepTests` and `OrcaSqrtFloorSweepTests`, which `excludeTestClass`
keeps out: they run under the module's `test` task, but their accepted-equivalence sweeps are too
large to repeat against every mutant and would distort the suite's audited timeout budget, so they
can report a divergence and never kill a mutant. Its mutator set, `STRONGER` with
`EXPERIMENTAL_BIG_INTEGER` and `EXPERIMENTAL_NAKED_RECEIVER`, is argued under "Mutator sets and
their trials".

### Families

- `# zero-fast-path family` — a redundant short-circuit forced not to take its shortcut: the general
  path runs on the input the shortcut would have answered and computes the identical result the long
  way. Each is a deliberate fast path that saves allocation or work, and its removal is
  unobservable; for the members that read a current sqrt price, the liquidity quotes by token amount
  and `WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity`, that holds for a non-null one.
  `OrcaUtil.applyTransferFee` with a non-zero rate takes a zero amount the long way: a fee of ceil(0
  × feeBps / `BPS_DENOMINATOR`), which is 0 under any cap, and 0 − 0 returned.
  `OrcaUtil.reverseApplyTransferFee` with a zero rate returns 0 for a zero amount at the next test,
  and for any other amount recovers a pre-fee amount equal to the amount and a fee of 0, so either
  side of the max-fee cap returns the amount. `OrcaUtil.firstUnoccupiedPositionInBundle` scans a
  full byte bit by bit, finds no clear bit and moves on to the next byte, as the skip does.
  `OrcaUtil.priceToSqrtPriceX64` lets a scaled value of exactly zero through to the `BigDecimal`
  conversion, which yields a `BigInteger` equal to the `BigInteger.ZERO` the clamp returns.
  `WhirlpoolQuote.collectRewardsQuote`, with a non-zero growth delta and zero position liquidity,
  multiplies to a zero product, which passes the u128 overflow guard and shifts to a zero owed
  delta. The liquidity quotes by token amount carry a zero token delta (the amount adjusted for its
  transfer fee) to zero liquidity in every position-status branch, and `decreaseLiquidityQuote` and
  `increaseLiquidityQuote` answer zero liquidity with the same shared `DecreaseLiquidityQuote.ZERO`
  and `IncreaseLiquidityQuote.ZERO` the shortcuts return;
  `WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity` turns zero liquidity into zero token amounts in
  every branch, a fresh `{0, 0}` array either way. Only the long paths of those quote members read
  the current sqrt price, in `OrcaUtil.positionStatus`, which dereferences it whenever the ticks'
  sqrt prices differ. Their neighbours are killed: `applyTransferFee`'s zero-rate leg forced always
  to fire, returning every amount unchanged (`OrcaUtilTests.applyTransferFeeBoundaries`);
  `collectRewardsQuote`'s zero-growth leg forced always to fire, zeroing every owed delta
  (`CollectRewardsQuoteTests.productAtExactlyU128MaxIsNotAnOverflow`); and
  `reverseApplyTransferFee`'s zero-amount return, which is no fast path, since at a full rate the
  long path answers the max fee (`OrcaUtilTests.reverseApplyTransferFeeBoundaries`). Escape: a long
  path that stops being exact at the shortcut's input; for the members that read a current sqrt
  price, a test passing a null one with ticks whose sqrt prices differ, which makes the long path
  throw `NullPointerException` and kills them. Covers: `OrcaUtil.applyTransferFee`, the zero-amount
  leg of its `feeBps == 0 || amount == 0L` return, forced never to fire;
  `OrcaUtil.reverseApplyTransferFee`, its zero-rate return (`feeBps == 0`), forced never to fire;
  `OrcaUtil.firstUnoccupiedPositionInBundle`, the full-byte skip (`b != 0xFF`), forced to scan every
  byte; `OrcaUtil.priceToSqrtPriceX64`, the clamp's `scaled <= 0.0` narrowed to `< 0.0`;
  `WhirlpoolQuote.collectRewardsQuote`, the zero-liquidity leg of the owed-delta shortcut
  (`rewardGrowthDelta.signum() == 0 || position.liquidity().signum() == 0`), forced never to fire;
  `WhirlpoolQuote.decreaseLiquidityQuoteA`, `WhirlpoolQuote.decreaseLiquidityQuoteB`,
  `WhirlpoolQuote.increaseLiquidityQuoteA` and `WhirlpoolQuote.increaseLiquidityQuoteB`, each its
  zero token-delta return (`tokenDeltaA == 0L` or `tokenDeltaB == 0L`), forced never to fire;
  `WhirlpoolQuote.tryGetTokenEstimatesFromLiquidity`, its zero-liquidity return
  (`liquidityDelta.signum() == 0`), forced never to fire.
- `# equal-operands family` — a comparison's boundary shifted where, at the tie, both branches
  produce the same value. `OrcaUtil.boundTickIndex` clamps a tick of exactly `MIN_TICK_INDEX` to
  `MIN_TICK_INDEX`, which `Math.min(tickIndex, MAX_TICK_INDEX)` returns as well.
  `OrcaUtil.orderPrices` and `WhirlpoolQuote.orderTicks` swap equal operands, and both orderings are
  the same pair; the callers of `orderPrices`, `tryGetAmountDeltaA` and `tryGetAmountDeltaB`, use
  only the values. Narrowed, `OrcaUtil.reverseApplyTransferFee`'s max-fee cap lets a recovered fee
  of exactly `maxFee` through, and the uncapped pre-fee amount it returns is then amount + `maxFee`,
  the value the cap returns. `OrcaUtil.tickIndexToSqrtPriceX64` routes tick 0 to the negative
  ladder, whose `NEG_BASE_EVEN` is 2^64 with no factor bit set, the value the positive ladder's
  `POS_BASE_EVEN` (2^96) gives after its 32-bit shift. Escape: arms that stop agreeing at the tie,
  such as ladders that disagree at tick 0 (`OrcaBoundaryTests.tickSqrtPriceRoundTrips` pins tick 0
  at 2^64), or a reader of the instances `orderPrices` returns rather than their values. Covers:
  `OrcaUtil.boundTickIndex`, the lower clamp `tickIndex < MIN_TICK_INDEX` widened to `<=`;
  `OrcaUtil.orderPrices`, `a.compareTo(b) <= 0` narrowed to `< 0`; `WhirlpoolQuote.orderTicks`,
  `t1 <= t2` narrowed to `<`; `OrcaUtil.reverseApplyTransferFee`, the max-fee cap
  `feeAmount.compareTo(maxFeeBi) >= 0` narrowed to `> 0`; `OrcaUtil.tickIndexToSqrtPriceX64`, the
  ladder choice `tickIndex >= 0` narrowed to `> 0`.
- `# defensive-guard family` — a guard, or the half of one, against a state no producer can
  construct, or that no tick from `MIN_TICK_INDEX` to `MAX_TICK_INDEX` carries, so that widening or
  removing it is unobservable. A comparison widened by its equal half, where an earlier check
  already excludes the equality: `OrcaUtil.firstUnoccupiedPositionInBundle` scans the bits of a byte
  only when it is not 0xFF, so a clear bit among bits 0 to 7 returns before the scan's bound could
  admit 8; `OrcaUtil.isValidStartTickIndex` reaches its `startTickIndex > MIN_TICK_INDEX` test only
  for a start index outside `MIN_TICK_INDEX` to `MAX_TICK_INDEX`, which `MIN_TICK_INDEX` is not,
  and with that test's early `return false` removed, a start index above `MAX_TICK_INDEX` falls
  through to the comparison with the left-edge array start, which lies below `MIN_TICK_INDEX` for
  every span `ticksPerArray` admits (it rejects a spacing whose span would overflow an `int`
  rather than wrapping it), so the comparison answers false as the return did;
  `OrcaUtil.positionStatus` has returned `INVALID` for equal bounds before its `cmp < 0` ordering
  test; and the xor in `OrcaUtil.startTickIndex`'s floor-division sign test
  `(tickIndex ^ ticksInArray) < 0` is zero only at `tickIndex == ticksInArray`, whose remainder is
  zero, so the first conjunct has already skipped the correction. `OrcaUtil.requireU128` returning
  null instead of its argument changes nothing, because every caller (`tryGetAmountDeltaA`,
  `tryGetAmountDeltaB`, `tryGetNextSqrtPriceFromA`, `tryGetNextSqrtPriceFromB`) calls it for the
  throw and discards the value. `OrcaUtil.sqrtPriceFromPositiveTick` and
  `OrcaUtil.sqrtPriceFromNegativeTick` multiply in one factor per set bit of the tick's magnitude,
  from bit 1 (mask `2 << 0`) to bit 18 (mask `2 << 17`); widened, each factor loop makes a pass for
  bit 19, whose mask `2 << 18`, 524,288, exceeds `MAX_TICK_INDEX` and the magnitude of
  `MIN_TICK_INDEX`, so no tick in that range sets it and the lookup past the factor table never
  runs. That range is the one `tickIndexToSqrtPriceX64` documents its precision for, and the
  acceptance covers nothing outside it. Outside it, a tick with bit 19 set makes the mutant throw
  `ArrayIndexOutOfBoundsException` where the original returns a value, and both public entries can
  hand the ladder such a tick: `tickIndexToSqrtPriceX64`, which does not check its argument, and
  `sqrtPriceX64ToTickIndex`, whose refinement converts a `tickHigh` estimate that can carry bit 19
  for a sqrt price outside `MIN_SQRT_PRICE_X64` to `MAX_SQRT_PRICE_X64`. Escape: the earlier check
  moving after the mutated test or going away; `ticksPerArray` letting an overflowing span through,
  which can wrap the left-edge start above `MAX_TICK_INDEX`; a caller that uses `requireU128`'s
  return value; for the factor loops, a test passing a tick with bit 19 set, which kills them, or the tick range
  reaching bit 19. Covers: `OrcaUtil.firstUnoccupiedPositionInBundle`, the bit scan's `bit < 8`
  widened to `<= 8`; `OrcaUtil.isValidStartTickIndex`, the out-of-bounds branch's
  `startTickIndex > MIN_TICK_INDEX` widened to `>=`, and the same branch's early `return false`
  removed; `OrcaUtil.positionStatus`, the bound-ordering
  test `cmp < 0` widened to `<= 0`; `OrcaUtil.startTickIndex`, the correction's
  `(tickIndex ^ ticksInArray) < 0` widened to `<= 0`; `OrcaUtil.requireU128`, its return value
  replaced with null; `OrcaUtil.sqrtPriceFromPositiveTick` and `OrcaUtil.sqrtPriceFromNegativeTick`,
  each factor loop's bound (`i < POS_FACTORS.length`, `i < NEG_FACTORS.length`) widened to `<=`, a
  pass for bit 19.
- `# callee-subsumed-guard family` — a guard that the callee it protects performs itself, forced
  false so that the callee decides. `OrcaUtil.tryGetAmountDeltaA` rejects a zero denominator (a zero
  sqrt price) with `ArithmeticException`; without the guard, `BigInteger.divideAndRemainder` rejects
  it with the same exception type and its own message, and `OrcaUtilTests.tryGetAmountDeltaAErrors`
  asserts the type. `WhirlpoolRemainingAccounts.append` returns the instruction unchanged for empty
  extras; without that leg it calls `Instruction.extraAccounts` with the empty list, which
  sava-core's `InstructionRecord`, the instruction every `Instruction.createInstruction` overload
  builds, answers with itself, so for a non-null instruction the `assertSame` in
  `OrcaBoundaryTests.remainingAccountsEmptySlicesAreDropped` holds either way; the null leg beside
  it, forced always to fire, returning every instruction unchanged, is killed by
  `OrcaWhirlpoolsClientWiringTests.swapV2AppendedRemainingAccountsFollowTheOracleDirectly`.
  `WhirlpoolRemainingAccounts$Builder.addSupplementalTickArrays` returns early for no tick arrays;
  without that it builds an empty meta list, which `addSlice` drops itself, and returns the builder
  unchanged. Escape: an assertion on the guard's message, which the callee's
  (`BigInteger divide by zero`) does not match; an `Instruction` whose `extraAccounts` does not
  return itself for an empty list, or a test appending empty extras to a null instruction, which the
  guard hands back and the mutant dereferences, killing it; an `addSlice` that records an empty
  slice. Covers: `OrcaUtil.tryGetAmountDeltaA`, the zero-denominator guard
  `denominator.signum() == 0` forced false; `WhirlpoolRemainingAccounts.append`, the empty-extras
  leg (`extras.accounts.isEmpty()`) of its pass-through guard forced false;
  `WhirlpoolRemainingAccounts$Builder.addSupplementalTickArrays(AccountsType, PublicKey...)`, its
  `tickArrays.length == 0` return forced false.
- `# u128-mask family` — a truncation mask that is the identity under the guard above it.
  `WhirlpoolQuote.collectRewardsQuote` computes the owed delta as
  `product.shiftRight(64).and(SafeMath.U64_MAX)`, Rust's `(product >> 64) as u64`, and the mutant
  replaces the `and` with its receiver. The mask sits directly under the `product > U128` overflow
  guard (`product.compareTo(SafeMath.U128_MASK) > 0` answers 0, the mirror of Rust's
  `unwrap_or(0)`), so `product >> 64` already fits u64 and the mask changes nothing. Covers:
  `WhirlpoolQuote.collectRewardsQuote`, the u64 mask on the shifted reward product, replaced by its
  receiver.
- `# shift-symmetry family` — `OrcaUtil.logbpX64` normalizes the sqrt price to bit 63 with
  `msb >= 64 ? sqrtPriceX64.shiftRight(msb - 63) : sqrtPriceX64.shiftLeft(63 - msb)`.
  `BigInteger.shiftLeft(-n)` is `shiftRight(n)`, so both branches compute the same expression and
  the conditional is purely cosmetic: narrowed to `msb > 64`, an msb of exactly 64 shifts left by
  −1, which is a right shift by 1; forced true, every msb shifts right by `msb - 63`, which below 63
  is a left shift. Escape: shifts without that symmetry, such as primitive shifts, which take their
  distance modulo the operand width. Covers: `OrcaUtil.logbpX64`, the normalization test
  `msb >= 64`, narrowed to `msb > 64` and forced true.
- `# log-approx-headroom family` — extra passes of `OrcaUtil.logbpX64`'s precision loop, a widened
  loop test that adds none, or the slow path of `OrcaUtil.sqrtPriceX64ToTickIndex` where the
  original takes its fast return. The tick is derived from a 14-bit (`BIT_PRECISION`) log
  approximation with error margins (`LOG_B_P_ERR_MARGIN_LOWER_X64`, `LOG_B_P_ERR_MARGIN_UPPER_X64`)
  sized so that extra iterations cannot change the resolved tick for a sqrt price from
  `MIN_SQRT_PRICE_X64` to `MAX_SQRT_PRICE_X64`. The loop bound `precision < BIT_PRECISION` widened
  to `<=` adds a fifteenth pass, and `precision++` turned into a decrement leaves the `bit` test
  alone to end the loop, after all 64 fractional bits. That claim is reasoned, not swept: the
  suite's round-trip tests sample it at exact tick prices across the domain, both extremes included
  (`OrcaBoundaryTests.tickSqrtPriceRoundTripsAcrossTheWholeDomain` and `tickSqrtPriceRoundTrips`,
  `OrcaUtilTests.tickSqrtPriceRoundTripAndMonotonic`), and
  `OrcaBoundaryTests.tickIndexResolvesPricesBetweenTicks` samples prices between ticks, but they are
  the suite's own tests, which every surviving mutant passes. Outside that range, which the public
  `sqrtPriceX64ToTickIndex` accepts unchecked, the precision-loop members can resolve a different
  tick. The rest hold for every input, whatever the margins: the loop's other comparison,
  `bit.signum() > 0` widened to `>= 0`, adds no pass, because `bit` starts at 2^63 and halves each
  pass, so it is still positive when the precision bound ends the loop; and the forced slow path
  differs from the original only where the two estimates agree, and there the refinement chooses
  between `tickHigh` and `tickLow`, which are the same tick. Escape, for the precision-loop members:
  a change to the log constants or the margins; or a test at a sqrt price outside
  `MIN_SQRT_PRICE_X64` to `MAX_SQRT_PRICE_X64` where one resolves a different tick, which kills it.
  Covers: `OrcaUtil.sqrtPriceX64ToTickIndex`, the equal-estimates return (`tickLow == tickHigh`)
  forced never to fire; `OrcaUtil.logbpX64`'s loop condition at two sites, one per comparison, whose
  rows are identical: `precision < BIT_PRECISION` widened to `<=`, and `bit.signum() > 0` widened to
  `>= 0` (the same key's remaining row, the normalization test's boundary, is argued under
  `# shift-symmetry family`); `OrcaUtil.logbpX64`, the loop's `precision++` turned into a decrement.

#### `sqrtFloor` initial guess

`# sqrtFloor-guess family` — `OrcaUtil.sqrtFloor` seeds Newton's integer square root with
`value.shiftRight(1)`; the `BigIntegerMutator` seeds it with `shiftLeft(1)`, and the
`NakedReceiverMutator` drops the shift, seeding with `value` itself. The iteration
`next = (prev + value / prev) / 2` descends monotonically to `floor(sqrt(value))` from any starting
point at or above the true root, and `v / 2`, `2v` and `v` all qualify for `v >= 2` (`v < 2` returns
early). Only the iteration count changes.

Verified as well as reasoned, by `OrcaSqrtFloorSweepTests`, which reimplements the iteration with
the seed left open, so that the production seed and the mutated seeds run the same code, differing
only where PIT differs them. `theRecordedInputSetsAreTheOnesBeingSwept` asserts the sizes of its two
input sets, so that a silently smaller sweep cannot pass as this one. Over 200,490 inputs, every
value below 200,000 and `2^e - 3` through `2^e + 3` for `e` in 60..129,
`everySeedReachesTheSameRoot` compares the production seed with the mutated seeds; over 122,765
more, the values 0..1999, `2^k - 1`, `2^k` and `2^k + 1` for `k` in 2..256, and 120,000 values of up
to 256 bits from a `Random` seeded with `0x5EED`, it compares the unshifted seed with the production
one. Neither set separates them. `everySeedReachesTheTrueIntegerRoot` holds the production seed over
the first set, and the unshifted seed over the second, to `BigInteger.sqrt()`, and
`theReimplementationMatchesTheProduction` holds the reimplementation to `OrcaUtil.sqrtFloor` over
the first. The class runs under the module's `test` task and is excluded from the suite, so it can
report a divergence but never kill a mutant. Escape: a change to the iteration under which some seed
at or above the root no longer descends to it, which the sweep reports. Covers:
`OrcaUtil.sqrtFloor`, the seed's `shiftRight(1)` turned into `shiftLeft(1)`, and replaced by its
receiver.

#### Tick-index lower error margin

`# log-margin family` — `OrcaUtil.sqrtPriceX64ToTickIndex` computes `tickLow` from
`logbpX64.subtract(LOG_B_P_ERR_MARGIN_LOWER_X64)`, and the `NakedReceiverMutator` drops the
subtraction. Writing `x = logbpX64 / 2^64`, the margins are about 0.01 and 0.856 of a tick: the
original brackets the tick between `tickLow = floor(x - 0.01)` and `tickHigh = floor(x + 0.856)`,
returns at once when they agree and otherwise refines against the exact forward ladder, and the
mutant's `tickLow` is `floor(x)`. The two diverge only where the mutant's `tickLow` collapses onto
`tickHigh`, taking the equal-estimates return and skipping the refinement that would have stepped
back down. That needs `frac(x) < 0.01` and the approximation to overshoot the boundary: some price
`p` below the tick-`k` boundary with `x(p) >= k`. Because `x(p)` is weakly monotone in `p`,
overshoot at boundary `k` is equivalent to `x(sqrtPrice(k) - 1) >= k`, so one evaluation per
boundary is an exhaustive search. The sibling that adds the margin instead of subtracting it needs
only that `x` lands within the margin below a boundary, is not equivalent, and is killed by
`OrcaUtilTests.theLowerMarginMustBeSubtractedNotAdded`.

`OrcaTickMarginSweepTests` runs that search over every boundary of the valid domain, `k` from
`MIN_TICK_INDEX + 1` to `MAX_TICK_INDEX`, 887,272 boundaries, under the module's `test` task:
`theApproximationNeverOvershootsATickBoundary` finds no overshoot and asserts that `x` does not
decrease from one boundary's sample to the next, and
`onlyTheNakedReceiverVariantAgreesAtEveryBoundary` compares the tick the original and the mutant
resolve at every boundary and finds them equal. It runs the same comparison on the sibling that adds
the margin and asserts that this one resolves a different tick at 10,452 boundaries, so the
comparison is shown able to separate a variant. The sweep seeds from the shipped `OrcaUtil.logbpX64`
and margins rather than from a copy: a copy is evidence only while it still matches, and a shifted
log leaves `sqrtPriceX64ToTickIndex` correct, the refinement absorbing it, while making this mutant
behavioural. It mirrors the forward ladder, the oracle the refinement consults, and
`theMirrorStillMatchesTheProduction` holds that mirror to `OrcaUtil.tickIndexToSqrtPriceX64` at
every tick of the domain and to `MIN_SQRT_PRICE_X64` and `MAX_SQRT_PRICE_X64` at its ends. The tick
selection that `onlyTheNakedReceiverVariantAgreesAtEveryBoundary` compares is also a
re-implementation, of `sqrtPriceX64ToTickIndex`'s bracketing and refinement, and no assertion holds
it to production. The class is excluded from the suite, so it can report a divergence but never kill
a mutant. `OrcaTickMathFuzz`, the `orcaTickMath` fuzz target, independently drives the bracketing
contract on the unmutated code over in-range prices.

The equivalence clears by little: at the tightest boundary the headroom is less than one
`LOG_B_2_X32` quantum, and biasing the log up by one such quantum puts two boundaries over (measured
2026-08-15). The sweep prints the tightest headroom, and the boundary it sits at, on every run. The
equivalence is a statement about sqrt prices from `MIN_SQRT_PRICE_X64` to `MAX_SQRT_PRICE_X64`:
outside that range, which the sweep does not cover and the public `sqrtPriceX64ToTickIndex` accepts
unchecked, the mutant can resolve a different tick. Escape: a change to the log constants, the
margins or the factor tables that lets the approximation overshoot a boundary, which the sweep
reports; or a test at a sqrt price outside that range where the mutant resolves a different tick,
which kills it. Covers: `OrcaUtil.sqrtPriceX64ToTickIndex`, the lower margin's subtraction replaced
by its receiver.

### Audited timeout-detected mutants

- `OrcaUtil.sqrtFloor` `ConditionalsBoundaryMutator` — cause:liveness: the Newton convergence check
  `while (next.compareTo(prev) < 0)` widened to `<= 0`. At the fixed point (`next == prev`) the
  iteration recomputes the same value forever instead of exiting. It is the weakened loop exit of an
  otherwise-correct Newton iteration, so no assertion can observe wrongness: the loop never returns.
  `OrcaUtilTests.sqrtFloor` reaches the fixed point at its first input past the early return, 2,
  whose seed 1 is already the root. The key's other mutants, the boundaries of the negative-value
  guard and of the early return below 2, terminate and are killed by `OrcaUtilTests.sqrtFloor`.

### Declined and untriaged debt

None: every accepted row of this suite is covered by a bullet under "Families".

## scope (`pitestScope`)

`pitestScope` mutates `software.sava.idl.clients.kamino.scope.*`, the Scope oracle readers, in
which a wrong branch produces a believable wrong price instead of an error: the walk of the
fixed-layout `OracleMappings` account into `ScopeEntry` graphs, the entry records with their
hand-written equality, hashing and rendering, and price-chain resolution; beside them
`ScopeProgramClient` with its implementation, the `ScopeFeedAccounts` feed constants, and
`ExponentTranchingMarket`, which reads an Exponent tranching market into the accounts an
`ExponentTranching` entry's refresh takes. It runs every `software.sava.idl.clients.kamino.*Test*`
class against these classes. Of the module's shared exclusions, the generated-code one takes the
`kamino.scope.gen.*` builders and decoders here. Its mutator set is `STRONGER` with
`EXPERIMENTAL_NAKED_RECEIVER`, which here reaches the `BigDecimal` `scaleByPowerOfTen` of both
`ScopeReader.scaleScopePrice` overloads and of `FixedPrice.createEntry`, the latter's
`stripTrailingZeros`, the stream filter of `ScopeEntriesRecord.oracleEntries`, and the
`Instruction.extraAccounts` calls of `ScopeProgramClient.refreshPriceList`.
`EXPERIMENTAL_BIG_INTEGER` and `EXPERIMENTAL_BIG_DECIMAL` are off because the suite holds none of
the arithmetic they rewrite (`add`, `subtract`, `multiply`, `divide` and their kin): its
`BigDecimal` use is decoding a stored price and scaling it by a power of ten, and it does no
`BigInteger` arithmetic. The trials behind both choices are under "Mutator sets and their
trials".

### Families

- `# hash-mixing family` — `MathMutator` turns the `+` of a hand-written hash's mixing step,
  `31 * result + component`, into `-`. The composites (`CappedFloored`, `CappedMostRecentOf`,
  `Conditional`, `MostRecentOfEntry`, `MultiplicationChain`) start the mix from their slot and mix
  in their scalar fields and their inputs, each input through `EntryGraph.hash`, which reads an
  input by its slot and type (`31 * index` plus the type's hash), the parts equal inputs share,
  rather than by its subgraph, and mixes a list of inputs with the same step per entry, starting
  from 1. `NotYetSupported` also starts from its slot and, like `ScopeEntriesRecord` and
  `PriceChainsRecord`, mixes its components directly. Any deterministic function of the compared
  components satisfies the `hashCode` contract. The tests assert the two properties that matter,
  that equal objects hash equal (each type's twin, built from distinct instances) and that each
  compared component perturbs the hash (`ScopeEntryEqualityTests.assertBothDiffer` on one variant
  per component, and for a composite's source list a variant moving only its first entry), and
  both properties hold when a step subtracts its component instead of adding it. Killing these
  would mean asserting literal hash values, which restates the implementation. The family is the
  `+` alone: a step's `31 *` turned into a division is killed. At the first step of a mix that
  starts from the slot it divides by the slot and throws at slot 0
  (`ScopeEntryEqualityTests.anEntryAtSlotZeroHashes`); in the list mix it drops every entry but
  the last once the running hash passes 31, which the `firstSourceMoved` variants catch; the
  per-type tests in `ScopeEntryEqualityTests` kill the rest. Escape: a reader of the hash value
  itself, such as an output ordered by these hashes. Covers: `CappedFloored.hashCode`, the `+` of
  each of its four steps (the source, cap and floor inputs, `sourcesMaxAgeS`);
  `CappedMostRecentOf.hashCode`, each of its four (sources, `maxDivergenceBps`, `sourcesMaxAgeS`,
  the cap input); `Conditional.hashCode`, each of its three (`condition`, `toleranceBps`,
  sources); `MostRecentOfEntry.hashCode`, each of its five (sources, `maxDivergenceBps`,
  `sourcesMaxAgeS`, the `refPrice` input, `refPriceToleranceBps`);
  `MultiplicationChain.hashCode`, each of its two (`sourceEntries`, `sourcesMaxAgeS`);
  `NotYetSupported.hashCode`, each of its six (`priceAccount`, `oracleType`, `emaTypes`,
  `refPrice`, `refPriceToleranceBps`, `generic`); `PriceChainsRecord.hashCode`, its one step
  (`twapChain` onto `priceChain`); `ScopeEntriesRecord.hashCode`, each of its three
  (`oraclePrices`, `slot`, `scopeEntries`, onto `pubKey`); `EntryGraph.hash`, one site in each
  overload (adding an input's type hash to `31 * index`, and the list mix's per-entry step).
- `# record-pattern family` — `NotYetSupported.equals` deconstructs its argument with a record
  pattern whose first component is the primitive `int i`. javac compiles that unconditional
  primitive pattern to a constant-true test after the component's accessor (`iconst_1; ifeq` to
  the no-match return): a compiler-synthesized check inside the pattern, which cannot take its
  alternate branch once the `instanceof` has matched. `RemoveConditionalMutator_EQUAL_IF` removes
  that jump, which a constant true never takes, so the mutant is the identity. Every source-level
  input stays pinned by `ScopeEntryEqualityTests.notYetSupported` (a matching twin, a mismatching
  variant per component, a null and a different type), which also kills the constant test forced
  to fail and the `instanceof` test's mutants. `PriceChainsRecord.equals` deconstructs only
  reference components, so javac emits no such test there. Escape: none, since no input makes a
  constant test take its other branch. Covers: `NotYetSupported.equals`, the record pattern's
  constant-true test for its `int` component, its jump removed.
- `# trim-on-exact-fit family` — `ConditionalsBoundaryMutator` widens a resolver's trim test,
  `j < entries.length`, to `<=`. `j` counts the entries kept and never exceeds the array's length,
  so the mutant differs only when nothing was cut: it then copies the full array instead of
  returning it, a distinct array with identical content. Every consumer reads content: the
  composites' comparison, hash and rendering through `EntryGraph`, `PriceChainsRecord`'s
  `Arrays.equals`, `Arrays.hashCode` and `Arrays.toString`, and the accessors. The trim forced off
  is killed (`ScopeEntriesRecordTests.chainsResolveAndTrimAtTheTerminator`,
  `ScopeComputeEntryTests.multiplicationChainResolvesItsSources`). Escape: none, since the array
  is allocated by the call and no reader holds it before it is returned, so none can tell the copy
  from it by identity. Covers: `ScopeReaderRecord.parseEntries`, the boundary of the trim test on
  a composite's source list; `ScopeEntriesRecord.parseChain`, the boundary of the trim test on a
  price or TWAP chain.
- `# zero-fast-path family` — `ScopeReaderRecord.emaTypes`'s `bitmask != 0` short-circuit forced
  true, so a zero bitmask also runs the decoding loop, which sets no type and returns an empty
  `EnumSet` wrapped unmodifiable instead of `Set.of()`. The two are equal, hash to the same zero,
  print as the same `[]` and both refuse `add`, so every entry built from either compares, hashes
  and prints the same, and the library's one other reader of the set, `OracleEntry.twapEnabled()`,
  asks only whether it is empty. The guard is a deliberate allocation-avoiding fast path whose
  removal nothing short of the set's class can observe. The short-circuit forced false, which reads
  every bitmask as empty, is killed by `ScopeComputeEntryTests.emaBitmaskDecodesEachBitToItsType`,
  which also asserts the zero bitmask's set by equality and that neither set accepts `add`. Escape:
  a reader of the returned set's class, or the wrapping dropped, which would hand the mutant's set
  out mutable where `Set.of()` is not. Covers: `ScopeReaderRecord.emaTypes`, the `bitmask != 0`
  short-circuit forced true.
- `# defensive-guard family` — `ScopeReaderRecord.entry`'s bounds check,
  `i < 0 || i >= priceInfoAccounts.length`, with the `i < 0` leg's jump removed: a negative index
  would pass the length test and read `entries[i]`, throwing `ArrayIndexOutOfBoundsException`
  where the original answers null. No decoder produces a negative index, because every index
  reaching `entry` is read masked-unsigned: the slot indices in an entry's `generic` payload by the
  generated decoders (`CappedFlooredData`, `CappedMostRecentOfData`, `MostRecentOfData`,
  `MultiplicationChainData`, `ConditionalData`, `PythLazerEmaRefData`), and the `refPrice` and
  `twapSourceOrRefPriceToleranceBps` arrays by `OracleMappings.read`, each reading a `u16` through
  `Short.toUnsignedInt`, directly or in `SerDeUtil.readUnsignedShortArray`, so each lies in
  `[0, 65535]`, while `readEntries`' own walk passes slots counted up from 0. Removing the leg is
  unobservable without constructing a state the decoders cannot produce. The leg's `i <= 0`
  boundary is killed, and so are the length leg's boundary (`i > priceInfoAccounts.length`) and
  the length leg forced true, which answers null for every slot. Escape: an `OracleMappings` built
  through its public record constructor rather than `OracleMappings.read`, holding a negative
  `refPrice` entry on any slot, or a negative `twapSourceOrRefPriceToleranceBps` entry on a
  `ScopeTwap1h`, `ScopeTwap8h`, `ScopeTwap24h` or `ScopeTwap7d` slot (the only types that read that
  field as a slot index), and passed to `ScopeReader.parseEntries`: the original resolves that
  index as absent and the mutant throws. Covers: `ScopeReaderRecord.entry`, the `i < 0` leg of the
  bounds check, its jump removed.

### Audited timeout-detected mutants

`scope-timeouts.csv` is armed and holds no member, so a first timed-out mutant in this suite is a
reviewer stop.

### Declined and untriaged debt

None: every accepted row of this suite is covered by a bullet under "Families".

## clients (`pitestClients`)

`pitestClients` is the module's catch-all. It mutates `software.sava.idl.clients.*` less the
packages other suites own: `orca.*` (`pitestOrca`), `kamino.scope.*` (`pitestScope`) and `spl.*`
(idl-clients-spl's `pitestSpl`). Every other hand-written class therefore lands here by default
rather than being skipped: the program clients with their PDA and remaining-accounts helpers, the
Jupiter REST clients with their request and response records, and the DLMM and Kamino math
utilities. Of the module's shared exclusions, the generated-code one swallows every bundled
program's generated package here, the orca and kamino.scope ones included. Every
`software.sava.idl.clients.*Test*` class runs against it, the two Orca sweeps included, though they
reach no class it mutates. The pattern for testing its client implementations is a distinct key per
role, account lists asserted by slot rather than by membership, and the invoked program asserted
explicitly, because a same-typed key in the wrong slot compiles and fails only on chain. Its mutator
set is `STRONGER` with all three experimental mutators: the two arithmetic ones reach `DlmmUtils`'
fee, price, bitmap and Q64.64 power math (`BigInteger`) and `DlmmUtils.binStepBase` and
`KaminoUtil.toSf` (`BigDecimal`), and `EXPERIMENTAL_NAKED_RECEIVER` reaches the REST request
builders and serializers, the response parsers and `DlmmUtils`' `BigInteger` arithmetic and masks.
The trials behind the set are under "Mutator sets and their trials".

### Families

- `# capacity-hint family` — the arithmetic of an `ArrayList`'s initial capacity mutated, never a
  value the list holds. A list grows from whatever non-negative capacity it is handed, so its
  contents and order are the same and the cost is extra growth copies, or unused slots, in a list
  local to the call: `ArrayList` exposes no capacity, and each list is copied out before the method
  returns (sava-core's `InstructionRecord.extraAccounts` copies the metas into the new instruction,
  and `MarginfiRemainingAccounts$Builder.build` returns `List.copyOf`). That holds while the mutated
  hint stays non-negative, which it does in every covered member for any input its method's
  documented contract allows. `KaminoVaultsRemainingAccounts.appendVaultReserves` halves the reserve
  count where it doubled it. `MarginfiRemainingAccounts$Builder.build` counts the Token-2022 mint
  whether or not `bankMint` is set, or never counts it.
  `KaminoLendingRemainingAccounts.appendObligationRefreshAccounts` leaves the referrer count out, or
  subtracts it; its contract is a referrer list that is either absent or one `ReferrerTokenState`
  per borrow reserve, so the difference is at least the deposit count. The tests that pin each
  payload's contents and order cannot tell
  (`KaminoVaultsRemainingAccountsTests.reservesThenTheirMarkets`,
  `MarginfiRemainingAccountsTests.theTokenTwentyTwoMintLeadsTheWholePayload`,
  `KaminoLendingRemainingAccountsTests.obligationRefreshOrdersDepositsBorrowsThenReferrers`).
  Escape: a reader of the capacity; for the referrer subtraction, also a referrer list longer than
  the deposits and borrows together, which the method does not reject and which makes the hint
  negative, so that the `ArrayList` constructor throws. Covers:
  `KaminoVaultsRemainingAccounts.appendVaultReserves`, the `reserves.size() * 2` hint turned into a
  division, in both overloads, the `Reserve`-taking one and the key-taking one;
  `MarginfiRemainingAccounts$Builder.build`, the hint's mint term `bankMint == null ? 0 : 1` forced
  to each arm; `KaminoLendingRemainingAccounts.appendObligationRefreshAccounts`, the hint's referrer
  term forced to zero, and, of the hint's two additions, the second, of the referrer count, turned
  into a subtraction.
- `# equal-operands family` — each of `MeteoraPDAs`' LbPair helpers seeds the pair with the smaller
  of its two mints first, and the member widens the `< 0` test of the mint comparison that picks it
  to `<= 0`. The two spellings differ only where the comparison answers 0, which it does only for
  two mints with the same bytes, that is, the same key; then either assignment yields the same seed
  pair and the same address, so the boundary is unreachable with distinct mints and harmless
  without. The comparison forced false is killed by the mint-order tests in `MeteoraPDATests`
  (`lbPairPDA`, `customizablePermissionlessLbPairIsMintOrderInvariant`,
  `permissionLbPairIsMintOrderInvariantAndBindsEverySeed`,
  `lbPairWithPresetParamIsMintOrderInvariantAndBindsThePreset`). Escape: a mint comparison that
  answers 0 for two different keys. Covers: `MeteoraPDAs.lbPairPDA`,
  `customizablePermissionlessLbPairPDA`, `permissionLbPairPDA` and `lbPairWithPresetParamPDA`, each
  the mint sort's `< 0` widened to `<= 0`.
- `# zero-fast-path family` — a shortcut forced not to fire, where the general path builds an equal
  result. `MeteoraDlmmClient.deriveBinAccounts(programId, lbPairKey, lowerBinId, upperBinId)`, the
  static overload the client's others delegate to, and
  `MeteoraDlmmRemainingAccounts.deriveBinAccounts` return a `List.of` holding the one writable
  bin-array meta when both ends of the bin range map to the same array index, and the member forces
  that `lowerBinIndex == upperBinIndex` shortcut false. The general path,
  `IntStream.rangeClosed(lowerBinIndex, upperBinIndex)`, then visits that one index and maps it
  through the same `MeteoraPDAs.binArrayPdA` and `AccountMeta.CREATE_WRITE`
  (`AccountMeta::createWrite`), so the result holds an equal meta: sava-core's writable meta is
  equal by key (25.11.2). The two lists are equal and both unmodifiable; they differ only in class,
  and only a null probe such as `contains(null)` tells them apart, which `List.of`'s list rejects
  and `Stream.toList`'s answers false. The shortcut forced true is killed by
  `MeteoraDlmmClientTests.rangeStraddlingZeroSpansTheNegativeArray` and
  `MeteoraDlmmRemainingAccountsTests.binAccountsCoverTheArrayRangeInOrderAndWritable`. Escape: a
  caller that reads the returned list's class or probes it for null. Covers:
  `MeteoraDlmmClient.deriveBinAccounts(programId, lbPairKey, lowerBinId, upperBinId)` and
  `MeteoraDlmmRemainingAccounts.deriveBinAccounts`, each the single-array shortcut forced false.
- `# callee-subsumed-guard family` — a hand-written guard forced not to fire, where the callee it
  hands the value to performs the same check or substitution.
  `MeteoraDlmmClientImpl.hostFeeInOrSentinel` puts `dlmmProgram()` in place of an absent host-fee
  account, the Anchor convention for an absent optional account (the invoked program's id, which
  keeps the positional account list intact). Its callers, `swap`, `swapExactOut` and
  `swapWithPriceImpact`, pass the key to the generated `LbClmmProgram.swap2`, `swapExactOut2` and
  `swapWithPriceImpact2`, whose key builders already write
  `requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())`, and the invoked meta
  they receive is `meteoraAccounts.invokedDlmmProgram()`, which `MeteoraAccounts.createAccounts`
  builds as `AccountMeta.createInvoked(dlmmProgram)`. With the null test forced false the null
  reaches the generated builder, which substitutes the same key:
  `MeteoraDlmmClientTests.absentHostFeeSubstitutesTheDlmmProgram` finds the program id in the
  host-fee slot either way, and kills the test forced true.
  `MeteoraDlmmRemainingAccounts$TransferHookExtras.append` returns the instruction unchanged when no
  transfer-hook metas were added; forced past that test it calls `instruction.extraAccounts` with
  the empty list, and sava-core's `InstructionRecord.extraAccounts` (25.11.2) returns the same
  instruction for an empty list.
  `MeteoraDlmmRemainingAccountsTests.transferHookSlicesPairWithTheirMetasInOrder` kills the test
  forced true. Escape: for the host fee, a `MeteoraAccounts` whose `invokedDlmmProgram()` names
  another key than `dlmmProgram()`, which `MeteoraAccountsRecord`'s public constructor allows, or a
  regenerated `LbClmmProgram` that stops substituting; for `append`, an `Instruction` whose
  `extraAccounts` does not return itself for an empty list. Covers:
  `MeteoraDlmmClientImpl.hostFeeInOrSentinel`, the `hostFeeInKey == null` test forced false;
  `MeteoraDlmmRemainingAccounts$TransferHookExtras.append`, the `metas.isEmpty()` test forced false.

#### `# domain-guard family` — `DlmmUtils` fee, Q64.64 `pow` and bin-index bounds

Each member mutates a conditional that guards a state the method's own inputs cannot reach, or moves
a boundary at which both spellings answer the same, except the dropped mask, which is the identity
on every value it reaches. The fee bounds come from `LbClmmConstants`: `FEE_DENOMINATOR` is 10^9 and
`MAX_FEE_RATE` is 10^8, `getTotalFee` caps the base plus variable rate at `MAX_FEE_RATE`, and both
rates are non-negative, being built from unsigned fields. The `pow` bounds come from the method
itself, which mirrors the dlmm SDK's `u64x64_math::pow`. The guards are worth keeping, since they
are cheap and document the domain, but no input a caller can construct distinguishes them from their
mutants.

- `computeFee`'s underflow guard, `denominator.signum() <= 0`, narrowed to `< 0`. The denominator is
  `FEE_DENOMINATOR` minus a rate of at most 10^8, so it is never below 9 x 10^8, and the narrowing
  changes only the zero case it never reaches. Escape: a total fee rate reaching `FEE_DENOMINATOR`,
  which needs `getTotalFee`'s cap at or above it.
- The u64 overflow guards of `computeFee` and `computeFeeFromAmount`, `fee.bitLength() > 63`, each
  with its boundary moved to `>= 63` and forced false. The amount is read as a u64, so the worst
  cases are (2^64 - 1) x 10^8 / (9 x 10^8), about 2^60.8, and (2^64 - 1) x 10^8 / 10^9, about
  2^60.7: both fees fit in 61 bits, short of the guard and of the 63-bit boundary the `>=` spelling
  tests. Even for a fee past the guard, forcing it false changes only the exception's message,
  because `fee.longValueExact()` then throws `ArithmeticException` too, in BigInteger's own wording.
  `computeProtocolFee`'s matching guard is reachable, because a full protocol share of
  `BASIS_POINT_MAX` keeps the whole u64 fee rather than dividing it down, and
  `DlmmUtilsTests.computeProtocolFee` asserts that guard's own message for that reason. Escape: a
  total fee rate far enough above 10^8, through `getTotalFee` losing its cap or a regenerated
  `LbClmmConstants` raising `MAX_FEE_RATE`, for a u64 amount's fee to reach 63 bits, where the
  `>= 63` spellings throw and the original does not; the guards forced false need a fee of 64 bits
  and a test that asserts the message.
- `computeVariableFee`'s `variableFeeControl() <= 0` shortcut, narrowed to `< 0`. The accessor is a
  u32 widened to `long`, so it is never negative, and at exactly zero the fall-through multiplies
  the squared volatility term by zero and ceil-divides it to zero, equal to the `BigInteger.ZERO`
  the shortcut returns. Escape: none; the spellings differ only at zero, where both answer zero.
- `pow`'s invert flag, `exp < 0`, widened to `<= 0`. `exp == 0` has already returned `Q64X64_ONE`,
  so the moved boundary is unreachable. Escape: that early return removed, so that `exp == 0`
  reaches the flag's uses.
- `pow`'s `exp == Integer.MIN_VALUE` special case forced false. Equivalent because the cast to
  `long` precedes the `abs`: `Math.abs((long) Integer.MIN_VALUE)` is already 2^31, so the special
  case documents the hazard rather than avoiding it. Escape: the `abs` taken before the widening
  cast, which leaves `Integer.MIN_VALUE` negative.
- `pow`'s zero test inside the inversion step, `squaredBase.signum() == 0`, forced false. It is
  reached only when `squaredBase` is at least `Q64X64_ONE`, that is 2^64, which is never zero.
  Escape: a zero `squaredBase` admitted into the inversion branch, where the mutant reaches
  `U128_MAX.divide(squaredBase)` and throws `ArithmeticException` where the original returns `null`.
- `pow`'s loop bound, `bit < 19`, widened to `<= 19`. `|exp|` is below `Q64X64_MAX_EXPONENTIAL`,
  2^19, so bit 19 of the exponent is never set; one more iteration only squares `squaredBase`, which
  nothing reads after the loop, and never reaches `result`. Escape: `Q64X64_MAX_EXPONENTIAL` raised
  above 2^19.
- `pow`'s result update with its `.and(U128_MASK)` dropped, and its overflow guard
  `result.bitLength() > 128` with the boundary moved to `>= 128` and forced false. After the
  inversion step `squaredBase` is at most 2^64, and each update is `(result x squaredBase) >> 64`,
  so `result` stays within 65 bits: the mask is the identity, and the result is far below both the
  guard and its boundary. The initial mask on `base` is different: it is the Rust `u128` parameter
  boundary, observable to a Java caller passing a base with bit 128 set, and
  `DlmmUtilsTests.powExactValues` kills it by asserting that `pow(base)` equals `pow(base + 2^128)`.
  Escape: for the dropped mask and the `>= 128` boundary, a squared base above 2^64 reaching the
  loop, which the inversion step prevents; the guard forced false has none while the mask precedes
  it, since a value masked to 128 bits never exceeds 128 bits.
- `binIdToArrayIndex`'s floor adjustment, `binId < 0`, widened to `<= 0`. At `binId == 0` the second
  conjunct, `binId % MAX_BIN_PER_ARRAY != 0`, is false, so both spellings return the truncated
  quotient. Escape: none.

Covers: `DlmmUtils.computeFee`, both of its boundary sites, the underflow guard's `<= 0` and the
overflow guard's `> 63`, and the overflow guard forced false; `DlmmUtils.computeFeeFromAmount`, the
overflow guard's boundary and the guard forced false; `DlmmUtils.computeVariableFee`, the
zero-control shortcut's boundary; `DlmmUtils.pow`, its three boundary sites, the invert flag, the
loop bound and the result overflow guard, its two equality tests forced false, the
`Integer.MIN_VALUE` special case and the inversion step's zero test, the result update's dropped
mask, and the result overflow guard forced false; `DlmmUtils.binIdToArrayIndex`, the negative-id
adjustment's boundary.

### Audited timeout-detected mutants

The audit is armed (`clients-timeouts.csv` exists) and holds no member, so a first `TIMED_OUT`
mutant in this suite is a reviewer stop.

### Declined and untriaged debt

None: every accepted row of this suite is covered by a bullet under "Families".

## Ground-truthing account order against the programs' Rust

Anchor account order is positional and fixed by the `#[derive(Accounts)]` field
order, so a program's Rust source is the authority. With the reference clones
present (see `AGENTS.local.md`), the generated key builders were diffed against
it mechanically — extract each struct's fields, flatten nested `Accounts`
composites the way Anchor does, append the `event_authority` + `program` pair
for any struct carrying `#[event_cpi]` (including a *nested* one, where the
pair lands mid-list), then compare positionally to each `*Keys(...)` method.

Result — **150 instructions verified, all matching** after one fix:

| Program | Instructions | Result |
|---|---|---|
| klend | 59 | all match |
| kfarms | 24 | all match |
| kvault | 20 | all match |
| marinade-finance | 27 | all match *(after re-pointing the IDL — see below)* |
| jupiter-lend | 3 | all match (reference CPI files, not Anchor structs) |
| phoenix (`rise-public`) | 12 | one fix — see below |
| marginfi-v2 | 75 | two fixes — the on-chain IDL was stale, see below |

Phoenix's Rust is not Anchor: `rise/rust/ix/src/*.rs` build their metas by hand
in numbered `build_accounts()` blocks, so those were the authority — and, per the
staleness sweep below, the *only* authority available, since the dispatch probe
cannot speak to non-Anchor programs.

The full diff (14 builders, all 16 `build_accounts` blocks) needed two extractor
fixes before it meant anything: the helpers `push_trader_index_accounts` /
`push_writable_accounts` append accounts **inline at their call position**, so
without expanding them every builder looked mis-ordered by two slots; and
`if let Some(..)` pushes are *optional* trailing accounts. With those modelled,
**every account order matched**. Two flag differences remained:

- **`SyncParentToChild.traderWallet` — a real defect, and an IDL one.** The IDL
  declares it `signer: false`; the SDK pushes `AccountMeta::readonly_signer`. The
  generated builder faithfully followed the IDL, so the fix belongs in the
  hand-written layer, which now rebuilds the metas. Checked across every mapped
  Eternal instruction: this is the *only* signer disagreement, so it is an
  upstream omission rather than a systemic gap. It survives in the wild because
  the trader wallet is usually also the fee payer, and message compilation then
  marks it a signer regardless — masking the bug for most callers.
- **`CancelStopLoss.globalConfiguration`** — the IDL marks it writable where the
  SDK has it read-only. Harmless: an unnecessary write lock, not a failure. Left
  as-is rather than diverging from the IDL for no behavioural gain.

The earlier pass had already found the fund-movement bug: they showed
`deposit_funds` / `withdraw_funds` taking the per-mint **global vault** in a slot
where our client passed `eternalGlobalConfig()` — the same value it already
supplies two slots earlier. The vault's seeds are not in the IDL; they come from
the program's own `constants.rs::get_global_vault_address` (`["vault", mint]`),
now `PhoenixAccounts.globalVaultPDA`. Both methods gained a `globalVaultKey`
parameter (**breaking**).

### Shank programs: Metaplex and SAS (2026-07-20)

The last two of the eight programs the dispatch probe cannot reach. Both are
**Shank**, not Anchor: account order is declared as indexed attributes on the
instruction enum rather than in a `#[derive(Accounts)]` struct, so they need
`extract_shank.py` rather than `extract_rust.py`.

| Program | Instructions | Result |
|---|---|---|
| Solana Attestation Service | 12 | all match |
| Metaplex Token Metadata | 58 | 57 match, 1 IDL gap |

Two things the extractor has to get right:

- **Attributes wrap across lines**, and `desc = ".."` strings may contain
  parentheses, so a line-based regex silently drops accounts. The parser is
  position-aware and quote-aware over the whole file.
- **Shank declares an explicit index per account**, which is a free correctness
  check the Anchor path does not have: after parsing, every instruction's indices
  must read `0..n-1`. That check caught the multi-line drift on SAS immediately —
  worth keeping, since a dropped account otherwise looks like a length mismatch
  against the IDL and reads as a defect in *our* code.

**`print` is missing two accounts, and it is an IDL gap rather than a bug.** The
Rust declares 20; the IDL declares 18, omitting both trailing **optional**
accounts:

| Index | Account | Flags |
|---|---|---|
| 18 | `holder_delegate_record` | optional |
| 19 | `delegate` | optional, **signer** |

Together they let a *holder delegate*, rather than the token holder, authorize
printing an edition. Since the IDL omits them the generated positional builder
has no parameters for them, so that authority path was unreachable. Added
`TokenMetadataRemainingAccounts.printHolderDelegate(..)`, which supplies the pair
in program order with the delegate as a signer, to be appended to the account
list. This is the same shape as Phoenix's optional `permission_account`: the IDL
cannot express a trailing optional, so the hand-written layer carries it.

### Squads, CCTP, Pyth and the Wormhole shims (2026-07-20)

Six more programs diffed against newly cloned Rust. **45 instructions verified,
zero defects.**

| Program | Instructions | Result |
|---|---|---|
| Squads V4 | 18 | all match |
| CCTP Message Transmitter V2 | 15 | all match |
| Pyth Solana Receiver | 7 | all match |
| Pyth Push Oracle | 1 | all match |
| Wormhole Verify VAA Shim | 3 | all match |
| Wormhole Post Message Shim | 1 | verified by hand — order *and* flags |
| Pyth Lazer | 0 | Solana contract is not in `pyth-crosschain` |

Three extractor traps, all worth knowing before repeating this:

- **Match structs by program, not by name.** A monorepo contains several
  programs, and `extract_rust.py` matches on struct name alone. It paired our
  `postMessage` with `PostMessage` from
  `anchor/programs/wormhole-integrator-example` — a *different* program that CPIs
  into the shim — producing an alarming all-slots-differ diff that was pure
  noise. Same trap hit Pyth Lazer, where the matched `Initialize` came from
  pyth-solana-receiver.
- **The shims are not Anchor.** They are hand-rolled for CU efficiency, so the
  wire order lives in the `AccountMeta::new(..)` sequence inside
  `crates/shim/src/post_message.rs::instruction()`, not in a `#[derive(Accounts)]`
  struct. Checked against that: `core_bridge_config, message, emitter, sequence,
  payer, fee_collector, clock, system_program, wormhole_program, event_authority,
  program` — exactly our IDL, with `bridge` = `core_bridge_config`, and every
  writability and signer flag agreeing.
- **CCTP suffixes its account structs `Context`** (`AcceptOwnershipContext` for
  `acceptOwnership`), so a name-keyed comparison silently matches nothing and
  reports a clean zero. Strip the suffix before comparing — "0 compared, 0
  differ" is a failure to compare, not a pass.

Squads' only two flagged instructions were also artifacts: `config_transaction_execute`
declares `rent_payer` and `system_program` as `Option<..>`, and the generated
code implements Anchor's absent-optional convention (substitute the invoked
program id) via a ternary the extractor could not parse. `extract_java2.py`
handles both that shape and `requireNonNullElse(..)`.

### Orca and Meteora: what could and could not be ground-truthed

**Orca — 61 instructions, zero ordering defects.** The clone at
`orca/whirlpools` carries the real program (`programs/whirlpool/src/instructions`,
Anchor `#[derive(Accounts)]`). All 17 reported differences were the auto-wired
class already seen with jupiter-lend and marginfi — `rent`,
`associated_token_program` and `memo_program` resolved internally by the client
instead of taken as parameters — with positions matching exactly. The memo
program was checked rather than assumed: the IDL pins
`MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr` and `solanaAccounts.memoProgramV2()`
is that address, so v2-vs-v1 is correct.

One structural surprise worth recording, together with the wrong conclusion it
was given at the time: the **anchor IDL account declares `whirlpool_program` as
a trailing account on all 66 instructions, and the repo's Rust declares it on
none.** It was "verified against the live on-chain IDL account" — the account the
client was generated from, so the check was circular. The program-metadata PDA
and Orca's own SDK copy carry no such account, Orca's generated TypeScript client
passes none, and the Rust never has. Nor was it noise: it arrived as the first
remaining account and shifted every transfer-hook and supplemental-tick-array
slice on the nine V2 instructions that take `remaining_accounts_info` by one. Since
2026-09-26 the client is generated from the metadata channel
(`"deployed": "metadata"`) and the diff is clean with no normalisation.

**Meteora — cannot be ground-truthed at all.** `dlmm-sdk` contains only
`commons` (math) and `cli`; there is no `programs/` directory, and
`commons/src/lib.rs` opens with `declare_program!(dlmm)`, which generates the
SDK's account structs *from `idls/dlmm.json`*. The SDK is therefore a sibling of
our generated client, not an independent source — diffing against it would be
circular. The program itself is closed-source. What *is* worth doing, and was
done: comparing their IDL copy to ours as a staleness check. Identical —
version 0.12.0, 76 instructions, every account list matching.

### Extra (`remaining_accounts`) coverage

Four programs already had helpers with derivation notes: `WhirlpoolRemainingAccounts`,
`KaminoLendingRemainingAccounts`, `KaminoVaultsRemainingAccounts`,
`MeteoraDlmmRemainingAccounts`. **Marginfi had none, and its client javadoc was
wrong.**

The javadoc said the risk engine reads `<bank1, oracle1, bank2, oracle2, ...>`.
The program's own `get_remaining_accounts_per_bank` says the group size is *per
bank*, from one to five:

| Bank | Accounts |
|---|---|
| `OracleSetup.Fixed` | 1 — bank only |
| `FixedKamino` / `FixedDrift` / `FixedJuplend` | 2 — bank + venue state |
| `PTFixed` | 2 — bank + Exponent vault |
| `PythMSOL` | 3 — bank + Pyth feed + Marinade state |
| `PythLST` | 3 — bank + Pyth feed + SPL stake pool |
| `PTPyth` | 3 — bank + Pyth feed + Exponent vault |
| `KaminoMSOL` / `JuplendMSOL` | 4 — bank + Pyth feed + venue state + Marinade state |
| `KaminoLST` / `JuplendLST` | 4 — bank + Pyth feed + venue state + SPL stake pool |
| asset tag `DEFAULT`(0) / `SOL`(1) | 2 — bank + oracle |
| asset tag `KAMINO`/`DRIFT`/`SOLEND`/`JUPLEND`(3-6) | 3 — bank + oracle + reserve |
| asset tag `STAKED`(2) | 5 — bank + oracle + lst mint + sol pool + onramp |

The setup rows win; only a setup absent from them falls through to the asset tag.
`Scope`, added in 0.1.11, is one such — the ordinary pair, with the feed's
`OraclePrices` account as the oracle and `BankConfig.scopeEntryIndex` selecting
the entry inside it.

**0.1.11 (deployed at slot 444313123) reopened this.** It appended nine
`OracleSetup` constants; the switch still named only the four `Fixed*` arms, so
seven of the nine fell through to the asset tag and came back one account short —
`WrongNumberOfOracleAccounts` again, for exactly the reason the helper exists.
`PTFixed` and `Scope` escaped only because the program pins their asset tags to
`DEFAULT`/`SOL`. Fixed by extending the switch and by
`MarginfiRemainingAccountsTests.everyOracleSetupIsClassified`, which names every
constant against an expected table so the next appended setup fails a test
instead of mispricing a bank.

A wrong count fails on chain with `WrongNumberOfOracleAccounts`. Separately,
`maybe_take_bank_mint` splits the **first** remaining account off on the
token-moving instructions and requires it to equal `bank.mint` — but only for
Token-2022 banks; for SPL Token it consumes nothing and the mint must be absent.
Getting that wrong fails with `T22MintRequired`. Transfer-hook accounts trail
everything, since the program forwards the whole slice to the transfer CPI.

Fixed by correcting the javadoc and adding `MarginfiRemainingAccounts`, a builder
that validates each group against the bank it describes, so a miscount throws at
build time with the expected and actual counts rather than surfacing as an opaque
on-chain error.

#### klend: the scope feed map covered two of four live feeds (2026-08-06)

`KaminoAccounts.scopeFeed(priceFeed)` was built from the two feeds the scope SDK
publishes, `hubble` and `klend`, so it returned null for any reserve pointing
elsewhere. Surveying all 558 live klend reserves' `scopeConfiguration.priceFeed`:

| feed | reserves |
|---|---|
| `3NJYftD5…` (hubble) | 227 |
| `3t4JZcue…` (klend) | 221 |
| `nu111…` / all-zero (disabled) | 103 |
| `82tcZDwU…` | 5 |
| `575gnsnE…` | 2 |

`82tcZDwU…` is a real third feed the SDK does not list. The scope program owns
exactly four `Configuration` accounts, so all four are now enumerated in
`ScopeFeedAccounts.MAINNET_FEEDS` and the map is built from that list — the
lookup is total for mainnet rather than a subset.

They have to be enumerated because they cannot be derived: the `Configuration`
PDA seeds on the feed's *name* (`["conf", name]`), which the reserve does not
carry. `hubble` and `klend` do reproduce their configurations from their names,
which is now pinned as a test — and is exactly why the other two, whose names
upstream does not publish, are constants read off chain instead.

`575gnsnE…` is not a gap here: it is the *configuration* account of the third
feed, sitting in a slot that wants an oracle-prices account. klend only checks
the passed key equals `price_feed` and then parses it as `OraclePrices`, so those
two reserves cannot refresh at all. Upstream configuration, recorded so the null
is not mistaken for a missing entry.

#### klend: two remaining-accounts contracts documented wrong (2026-08-06)

Re-reading the handlers against the helper's javadoc turned up two entries that
would send a caller straight into an on-chain failure:

- **`repayObligationLiquidity[V2]`** was listed as taking a trailing optional
  permission account "only". It takes neither. `process_impl` maps the *whole*
  remaining slice to `FatAccountLoader<Reserve>` and hands it to
  `update_elevation_group_debt_trackers_on_repay`, which walks it in lockstep
  with `obligation.active_deposits_mut()`, `require_keys_eq!`s each one, and
  writes its per-elevation-group debt tracker. So: the obligation's active
  deposit reserves, in the obligation's order, writable — and only when the
  obligation is in an elevation group; outside one the branch consumes nothing.
  Repay is not a `PermissionedOp` at all.
- **`requestElevationGroup`** was grouped with `borrowObligationLiquidity` as
  `[deposit_reserves, (optional) permission]`, and the client javadoc pointed at
  `appendDepositReserves`. It actually takes the full `refreshObligation` triple —
  deposit reserves, borrow reserves, and one `ReferrerTokenState` per borrow when
  the obligation has a referrer — and checks
  `remaining_accounts.len() != expected` *before* reading any of them, so a list
  built from the deposits alone fails with `InvalidAccountInput`. It now points at
  `appendObligationRefreshAccounts`. No permission account here either.

`borrowObligationLiquidity[V2]` and the three deposit paths were right as
documented. The permission account is always last: the deposit handlers read
`remaining_accounts.last()`, and the paths that also iterate reserves strip it
first (`check_permissions_and_strip`).

#### marginfi: the convenience overloads hardcoded SPL Token (2026-08-06)

`MarginfiClient.deposit`, `repay`, `withdraw` and `borrow` each had a `mint`-only
overload that resolved `solanaAccounts().tokenProgram()` internally. For a
Token-2022 bank that is wrong twice over: it lands the legacy program in the
instruction's `token_program` slot, and — because the token program is an ATA
seed — derives a source/destination token account that does not exist. Five of
marginfi's 201 distinct live bank mints are Token-2022 (`CASHx9…`, `pumpCmXq…`,
`susdabGD…`, `2b1kV6Dk…`, `2u1tszSe…`).

The four overloads now take the token program; the fully-explicit forms already
did. Callers on a Token-2022 bank must also append the mint as the first
remaining account, which `MarginfiRemainingAccounts` already models.

While checking this, the bank vault derivation was ground-truthed the way klend's
was: `liquidity_vault` seeded on `[b"liquidity_vault", bank]` reproduces the
stored vault for **all 434 live banks** at each bank's stored bump. Unlike klend's
reserves there is no second live scheme, so these stay derived.

#### Which IDL channel actually describes the deployed program (2026-08-06)

Phoenix Ember raised the general question, so every configured program was swept.
The cheap signal: compare the **last write to the on-chain IDL account** against
the **program's last deploy**. When the deploy is newer, the on-chain IDL is
unverified — `matchesDeployed` only says we faithfully copied what upstream
published, which it cannot distinguish from upstream having stopped publishing.

Six of the twelve programs whose channels disagree are in that state:

| package | IDL published | program deployed |
|---|---|---|
| `squads.v4` | 2024-01-27 | 2024-11-20 |
| `jupiter.order_engine` | 2024-10-23 | 2025-08-04 |
| `phoenix.ember` | 2025-10-30 | 2026-01-21 |
| `meteora.dlmm` | 2026-05-13 | 2026-06-03 |
| `nt.bundle` | 2026-04-27 | 2026-07-21 |
| `phoenix.perpetuals` | 2026-06-05 | 2026-08-05 |

Age alone is not a reason to switch, so each was confirmed against chain:

- **`squads.v4` → `"deployed": "vcs"`.** The repository IDL declares five
  transaction-buffer instructions and a `TransactionBuffer` account the on-chain
  copy does not, and **15 live accounts carry that account's discriminator**. It
  also shows `multisig_create` taking no args, matching the `MultisigCreateDeprecated`
  error it adds — the deployed program rejects the call the on-chain IDL still
  describes.
- **`nt.bundle` → `"deployed": "vcs"`.** Sampling the program's recent
  transactions turned up six distinct instruction discriminators, and one —
  `process_switch`, `baaa0042685139c3` — exists **only** in the repository IDL. The
  deployed program executes instructions the on-chain copy does not declare.
- **`jupiter.order_engine` → `"deployed": "vcs"`.** Instruction, account, type and
  error sets are identical, so nothing behavioural rides on it; the repository copy
  carries the real program address (the on-chain document declares the placeholder
  `RderEngine111…`) and the `constants` the on-chain copy omits entirely.
- **`meteora.dlmm` — left on chain.** The only difference is one extra type,
  `LimitOrderBinData`, reachable solely through `DummyZcAccount` — the dummy
  zero-copy struct Anchor uses to force types into an IDL. No instruction or
  account differs, so neither document is more correct about the interface.
- **Both Phoenix programs — nothing to switch to.** `Ellipsis-Labs/rise-public`
  ships a hand-rolled SDK (discriminator tables and TS builders), not an Anchor
  IDL, so the stale metadata account is the only published description. This is why
  the Ember fixes live in the hand-written layer.

The four Kamino programs, `jupiter.swap` and `orca.whirlpools` publish their IDL
in the same deploy, so the on-chain copy stays authoritative for them. Kamino
Vaults stopped being one on 2026-10-01, when the document published with a deploy
described a later build; see "Kamino Vaults: `"deployed": "vcs"`" below.

**Switching `squads.v4` drops four generated types**, three of which were
duplicates the on-chain IDL carried alongside the names the program actually uses:
`TransactionMessage`, `CompiledInstruction` and `MessageAddressTableLookup` have
field-for-field equivalents in `VaultTransactionMessage`,
`MultisigCompiledInstruction` and `MultisigMessageAddressTableLookup`, all three
already generated before the switch. Only `Permission` — the `Initiate`/`Vote`/
`Execute` enum — goes without replacement; `Permissions` survives as the `u8` mask
it has always been, so a caller composing one now needs the bit values rather than
the names. Nothing in either IDL referenced any of the four structurally.

#### Phoenix Ember: the IDL's PDA seeds derive accounts that do not exist (2026-08-06)

`PhoenixAccounts` took Ember's state and vault from the generated
`EmberPDAs.statePDA` / `vaultPDA`, whose seeds are `["state"]` and `["vault"]`
alone. Both derived addresses have never been created on chain, so every
`deposit` and `withdraw` the client built named two accounts that do not exist.

The real seeds are `[phoenix_program_id, "state"]` and
`[phoenix_program_id, "vault"]` against the Ember program — `phoenix_program_id`
being `EtrnLzgb…`, what this library calls the Eternal program. One Ember
deployment serves both Phoenix deployments, so an un-keyed state could not work;
the beta program's state is live at its own address under the same formula. The
Rust says so (`rise/rust/ix/src/constants.rs::get_ember_state_address`) and a
live deposit confirms it: Ember owns exactly two accounts, `6ur7v6…` (prod, with
`EtrnLzgb…` in its trailing field) and `HVpfk2…` (beta, `phDEVv4w…`), and the
prod deposit `2inFMjAp…` passes `6ur7v6…` in slot 1 and `FKcEb4Td…` in slot 6.

Fixed by deriving both in `PhoenixAccounts`, alongside the Eternal
`globalVaultPDA` that the IDL also fails to declare. `EmberPDAs` is generated and
untouched; `PhoenixClientTests` asserts the derivations equal the live keys and
that the IDL's seeds do *not* reach them, so the two cannot be confused again.

This is upstream's own published metadata (`sources.json` resolves the IDL from
program-metadata account `HBWQuFtc…`, `matchesDeployed: true`) — the check
confirms the IDL is the one the program published, not that it describes the
program. **The same IDL is stale about `EmberState` itself**, in two ways, only
one of which is fixed (2026-08-16):

| | IDL | generated | on chain |
|---|---|---|---|
| discriminator | `[0, 208, 11, 177, 63, 157, 55, 98]` | `[142, 206, 11, 177, 63, 157, 55, 98]` | `[142, 206, 11, 177, 63, 157, 55, 98]` |
| size | 104 | 104 | 136 |

Both live accounts agree, and the trailing 32 bytes are the Phoenix program the
state is keyed on — a fourth field the IDL does not declare.

The **discriminator is overridden** — `accountDiscriminators` in
`main_net_programs.json` supplies the seed `state_account`, which the generator
hashes to the value the program writes, the same one Phoenix's own SDK computes
in `EmberStateView::load`. Without it `readChecked` threw for 100% of live
accounts, and the correct value is independently derivable, so carrying it costs
one line and settles it. `DISCRIMINATOR_FILTER` now matches both live accounts.

The **short layout is deliberately not fixed**. `BYTES` stays 104, so
`SIZE_FILTER` still asks for a size no account has and a size-filtered scan still
returns empty; scan on `DISCRIMINATOR_FILTER` instead. A field-list override
would be a private fork of someone else's account definition maintained forever,
and Phoenix publishes no IDL in their repo to track — that fix belongs upstream.
`EmberState.read` returns the right `authority`, `inputMint` and `outputMint`
(their offsets are unchanged) and drops the fourth key.

`EmberStateTests` pins all of this against a committed mainnet fixture, including
the shortfall, so if `BYTES` ever becomes 136 the test fails and says upstream
shipped a corrected IDL.

**The same IDL is stale about `withdraw`'s argument too, and that one is
corrected (2026-09-27).** It declares `WithdrawParams.amount` as `u64`; the
deployed program reads `Option<u64>`, `None` meaning the full withdrawal. The
evidence is independent of the IDL:

- Phoenix's SDK, `Ellipsis-Labs/rise-public` at `e688686`: `rust/ix/src/cpi.rs`
  declares `EmberWithdrawArgs { amount: Option<u64> }` and writes 9 or 17 bytes,
  and `instructions.json` carries `"EmberWithdraw": "b712469c946da122010100000000000000"`.
- Every sampled mainnet withdraw is 17 bytes, the discriminator, `01`, then the
  amount (`24tXGERM…` carried `Some(121_960_000)`).
- Simulated against live accounts: `Some(1)` and `None` succeeded, and every
  16-byte `u64` encoding panicked, whatever its first byte.

So the client generated from the IDL failed on every call. `"fieldTypes":
{"WithdrawParams.amount": {"option": "u64"}}` in `main_net_programs.json`
corrects it: `WithdrawParams(OptionalLong amount)`. The correction reaches the
generated code only. `idl.json` and the channel record stay what Phoenix
published, `sources.json` records the override on its own line, and
`idl-change-report-gap.txt` carries it as a standing entry. The generator
refuses the run once upstream declares the field `Option<u64>` itself, so the
override cannot outlive its reason; remove it then.

This does not reverse the layout decision above. An argument every call gets
wrong makes the builder unusable, and correcting it is one checked line,
where the account's missing fourth key would mean adding a field to someone
else's definition, which `"fieldTypes"` cannot do. `EmberWithdrawTests` holds
the encoding to the SDK's vector and the mainnet bytes.

#### kvault: the lending-market block was missing entirely (2026-08-06)

`KaminoVaultsRemainingAccounts.appendVaultReserves` appended the vault's
reserves and nothing else, and its javadoc asserted that the lending market
"is **not** passed as a separate remaining account" and that
`withdrawFromAvailable` takes no remaining accounts at all. Both are wrong.

`vault_operations.rs` reads the first `get_reserves_count()` remaining accounts
as the reserves and hands them to `klend_operations::cpi_refresh_reserves`,
which builds `[reserve(writable), lending_market(readonly)]` pairs for klend's
`RefreshReservesBatch` — loading each market key *out of the reserve account it
just read*. A CPI can only reference accounts the caller already holds, so every
one of those markets has to be in the outer instruction. Without them the
transaction fails before it can do anything. `handler_withdraw.rs` reaches the
same `refresh_allocation_reserve_accounts`, so `withdrawFromAvailable` is not
the exception the javadoc claimed.

The layout is two slot-ordered blocks — every reserve writable, then every market
read-only — matching `kvault-interface`'s `refresh_remaining_accounts`, whose own
`test_refresh` executes a two-market deposit on chain with no shim. Note the
blocks are *not* the interleaved pair order the inner CPI uses; that ordering is
internal to the metas it builds.

Two further traps, both pinned against chain state:

- The reserves must be `vault_allocation_strategy[]` in slot order **with the
  empty slots dropped** — `check_allocation_reserve_accounts_match` skips
  `Pubkey::default()` entries while walking what you sent, so a hole shifts every
  later slot into a `ReserveAccountAndKeyMismatch`. 13 of the 172 live vaults have
  a hole. `allocatedReserves(VaultState)` does the compaction; it tests the
  all-zero key specifically, not `KaminoAccounts.isNullKey`, whose `nu111…`
  sentinel would drop a slot the program still counts.
- The markets are per reserve, not per vault. The fixture vault `67dqmR…` has a
  hole at slot 5 and eight reserves in **eight distinct lending markets**, so a
  block built from the vault's own market, or from the first reserve's, is wrong
  in seven slots. Its `VaultState` and its eight reserve accounts are checked in
  as gzipped fixtures (62 KiB and 67 KiB raw, 1.8 KiB and 4.6 KiB on disk).

The `Reserve`-taking overload reads each market off its reserve, so the two
blocks cannot disagree; the key-taking overload is positional and rejects a
length mismatch rather than truncating. The two `MathMutator` survivors on the
`size() * 2` capacity hints are the same allocation-size-only family as the
klend and marginfi rows.

#### kvault: the permissioning authority goes last (2026-10-01)

2.3.0 lets a vault name a `permissioning_authority`. When one is set,
`check_permissioning_authority_and_strip` takes the **last** remaining account,
requires it to be that key (`InvalidPermissioningAuthority`, 7065) and a signer
(`AccountNotSigner`, 3010), and strips it before `refresh_allocation_reserve_accounts`
reads the two blocks above. It runs in `handler_deposit` (`deposit`,
`deposit_with_min_shares_out`, `buy`, `buy_with_min_shares_out`), in
`withdraw_utils::withdraw` (`withdraw`, `sell`, `withdraw_from_available`) and in
`redeem_in_kind`; `invest` and `invest_with_max_amount` do not take it. For the
all-zero key the program strips nothing. `appendPermissioningAuthority` appends a
read-only signer, and nothing for the all-zero key, which is the rule `klend-sdk`
13.0.1 applies in `getPermissioningAuthorityAccount`.

No mainnet vault sets one yet: on 2026-10-01 all 185 `VaultState` accounts (data size
62552) held zeros at offset 58840. So the tests write a key into the `67dqmR…`
fixture at `PERMISSIONING_AUTHORITY_OFFSET` rather than pin a live instance.

The contract was run on the deployed binary instead, on a surfpool fork of mainnet
whose cloned program data hashed to the same trimmed `b2bb00e4…`. USDC Prime
(`9E69U4GzWhryRaPe8DYpco6Z9vTZY6gg8w6W2QsBACEj`, three reserves) was given a throwaway
authority with `surfnet_setAccount`, and deposits were built with `KaminoVaultsClient`
and both helpers. Without the authority the deposit failed with 7065, the program
logging the last market as the key it compared. With `appendPermissioningAuthority` it
minted shares. With the authority last but unsigned it failed with 3010 at
`permissioning_authority.rs:21`, which is after the key matched. With the authority
signing but placed before the market block it failed with 7065. `withdrawFromAvailable`
failed with 7065 without the authority and burned shares with it. The same deposit on
the vault before the authority was set passed, and the helper appended nothing.

### Marginfi: a stale on-chain IDL hiding two live client bugs

The diff reported 30 mismatches against `0dotxyz/marginfi-v2`; 27 were extractor
artefacts (auto-wired `solanaAccounts.*()` sysvars and program ids the client
resolves internally). Three were real: `kamino_init_obligation` (23 accounts vs
our 27), `lending_pool_add_bank_permissionless` (17, adding `pool_onramp` and
`validator_vote_account`), and `panic_pause` (`pause_authority` vs
`global_fee_admin`).

**The first pass got this wrong** and is worth recording as a trap. The
reasoning was: the on-chain IDL is 0.1.8 and matches our client exactly, the repo
is 0.1.9, and `0dotxyz` is not `mrgnlabs` — therefore a fork describing
undeployed code, so leave it. Two errors. `0dotxyz`/**p0** is the marginfi team
after a rebrand, not a third party. And "the on-chain IDL matches our client"
only proves our client matches *the IDL* — it says nothing about the program.
The IDL is a separate account that a deploy does not update.

**The program is the authority, and it can be asked directly.** Simulate a
transaction carrying just an 8-byte discriminator with `sigVerify: false` and
`replaceRecentBlockhash: true` (the fee payer must be a real funded account, or
simulation aborts with `AccountNotFound` before reaching the program):

- **Not deployed** → `InstructionFallbackNotFound` (error 101), byte-identical to
  a garbage discriminator.
- **Deployed** → the program logs `Instruction: <Name>` first, then fails later
  on account/arg validation (102, 3005, ...).

That probe showed `lending_account_clear_emissions` returning 101 while
0.1.9-only instructions (`lending_pool_emissions_deposit`,
`init_global_fee_state_v2`) dispatched: **the deployed program is 0.1.9.** Two
shipped client bugs followed, both in code no test reached:

| Method | Defect |
|---|---|
| `clearEmissions` | 0.1.9 removed the instruction. Every call failed with 0x65. Wrapper **deleted**. |
| `closeOrder` | 0.1.9 prepends `group`, shifting all five accounts down one slot. Now passes `marginfiGroup()` (**breaking**: the generated builder gained a `groupKey` parameter). |

Everything else the client wraps — deposit, repay, withdraw, borrow, flashloans,
`placeOrder`, the account lifecycle — is byte-identical across the two versions.

Fixed by pointing the config at the IDL the team publishes in their TS SDK and
regenerating (31 files, 0.1.8 → 0.1.9). The source is now the commit that
published it, because upstream deleted the file:

```json
"vcs": {
  "repo": "0dotxyz/p0-ts-sdk",
  "path": "src/idl/marginfi_0.1.9.json",
  "ref": "main",
  "commit": "a33b98018984412693fb7b9dadc2794af5392ab8"
},
"deployed": "vcs"
```

⚠️ **The version-pinned filename went wrong in the direction this warning did
not name.** The original note predicted a *freeze*: 0.1.10 lands at a new
filename and the override silently keeps serving 0.1.9. What happened on
2026-08-18 (`fe6f1a26`) was the opposite — the path was *chased* to
`marginfi_0.1.10.json` because p0 deleted `marginfi_0.1.9.json` from `main` the
day before (their `d769882`), so the old URL began answering 404. There was no
deploy behind it: `lastDeploySlot` sat at 432875565 across the switch, and the
client spent 2026-08-18 → 2026-08-19 generated from an undeployed version. A
version-pinned filename fails in both directions, and upstream deleting it is
the one an unpinned `ref` cannot survive.

**The trigger is the deploy, never the filename.** Bump only after
`ProgramData.last_deploy_slot` moves off 432875565, confirmed by simulating one
version-exclusive discriminator against a control the program does not declare.
Upstream schedules the 0.1.10 mainnet upgrade for **2026-08-25T15:00:00Z**
(`p0-ts-sdk/src/dialect.ts`, `MARGINFI_V0_1_10_ACTIVATION`), and it is
explicitly reschedulable — the date is a heads-up, not the signal.
`docs/PROGRAM_VERIFICATION.md` has the procedure; the tool that automated it was
removed on 2026-08-14.

**What 0.1.10 costs while it is not deployed** (measured 2026-08-19, the reason
for the revert): `MarginfiGroup` grows 1064 → 9256 bytes and `FeeState` 264 →
520, so both readers throw on every live account and both `SIZE_FILTER`s match
nothing — 162 live groups at 1064, one `FeeState` at 264, zero at either 0.1.10
size. Five builders are dead on chain; five more insert an account mid-list —
`lending_account_end_flashloan` puts `group` where the deployed program requires
`authority: Signer`. `lending_account_pulse_health` is the quiet one: 0.1.9
declares one account and reads the rest as `remaining_accounts`, and its handler
swallows the engine error, so the extra key shifts the bank/oracle list and the
transaction *succeeds* with a bogus health cache.

**The deployed image is identified at artifact level, not inferred.** The
`ProgramData` payload, trailing zeros stripped, is byte-identical to upstream's
own `mrgn-0.1.9-rc3` release `marginfi.so` — sha256
`26dda5e1a060d8fa5d8cf122518f26be3bdaab68e1dc525f74061e2e74cb38f4`, whose prefix
is verbatim the entry in marginfi-v2's `guides/ADMIN/DEPLOY_GUIDE.md`: "0.1.9
July 14, 2026 ~11am ET -- Hash 26dda5e". An on-chain otter-verify record
(`GRJ6g9JSPBjYRBRB54ej9YJrURKcHnw3mnmPXaFk5iAp`, seeds `["otter_verify",
<upgrade authority>, <program>]`, owner `verifycLy8mB96wd9wqq3WDXQwM4oU6r42Th37Db9fC`)
names the build commit, `d4c70c84f8a9692405a2c32cbd7095bb1fe3f428`, and the same
deploy slot. That deploy log is a checkable oracle for the next upgrade, and the
hash only reconciles once the trailing zeros are stripped — `sources.json`
records `programDataPayloadSha256` over the padded payload.

Techniques that did *not* settle this, for the record: grepping the deployed
`.so` for account-name string literals found all three disputed names, but a
discriminator scan of the same buffer matched only 13 of 88 known-present
instructions, so byte-level searching is too noisy to rely on. And the live
`FeeState` could not distinguish the versions — 0.1.9's new
`pause_delegate_admin` is carved from the old `reserved1` and is currently zero,
which both layouts predict.

### Bundle-wide staleness sweep (2026-07-19)

Marginfi's `clearEmissions` was a *dead* method — not wrong, but incapable of
succeeding — and nothing in the mutation baseline could have found it. So the
dispatch probe was run to bound how much more of that exists. Its coverage was
narrower than this section originally claimed: it selected only instructions
carrying a top-level Anchor `discriminator`, so Shank instructions and Codama
programs were never probed at all, and programs whose control was inconclusive
dropped out too. The figures below are neither corpus-wide totals nor a complete
count of what was simulated. Each selected instruction was
probed for `InstructionFallbackNotFound`, with a garbage-discriminator control
per program to confirm the program is Anchor-dispatch-shaped before trusting any
verdict.

**Result: zero actionable defects.** Two instructions are declared but not
deployed, both benign and neither wrapped by hand-written code:

| Program | Instruction | Why it is fine |
|---|---|---|
| Meteora DLMM | `for_idl_type_generation_do_not_call` | A stub — one `dummy_zc_account`, one `_ix` arg — existing only to force the IDL to emit zero-copy types. Never deployed by design. |
| Switchboard On-Demand | `pull_feed_submit_response_svm` | An other-SVM-chain variant not enabled on Solana mainnet; its four `pull_feed_submit_response*` siblings all dispatch. |

Marginfi re-probes clean (100 instructions, 0 dead), which also serves as the
positive control: the same sweep would have flagged `clearEmissions` before the
fix.

**Eight programs are INCONCLUSIVE** — their garbage-discriminator control did not
return 101. That is the whole of what it means, and an earlier revision of this
line drew more from it than it carries: **it does not establish that a program is
not Anchor-dispatch.** A native, Shank or pinocchio program emits no fallback
error, and neither does an Anchor program whose own `#[fallback]` handles the
unknown discriminator — the two read identically from outside. Jupiter Swap is on
this list and is an ordinary Anchor program.

**Seven, since 2026-08-13.** Jupiter Swap left the list when the probe stopped keying dispatch on
error 101 alone: it answers a garbage discriminator with `InvalidAccountData` and a declared one
with 102. It read `OK 17 ix, 0 dead` — which overstates it. A differing response is not proof of
dispatch where the dispatcher's oracle is unestablished and a user `#[fallback]` is possible, so
the standing position is 2 confirmed by real transactions and 15 uncharacterised. See
`docs/PROGRAM_VERIFICATION.md`.

The seven: Metaplex Token Metadata, Phoenix Ember, Phoenix
Perpetuals (+ Dev), Solana Attestation Service, and the two Wormhole shims. The
probe cannot speak to these; they need the Rust-diff treatment instead — which is
exactly how Phoenix's global-vault bug was found, so the gap is real rather than
theoretical.

Worth re-checking after any upstream deploy, by hand — the sweep this described
lived in a session scratchpad (`idl_staleness_sweep.py`) that no longer exists,
and pointing at it was pointing at nothing. Committing it would have meant
whitelisting a new tracked-file kind in `.gitignore`, which is a deliberate
decision and has not
been taken.

### Marinade: a stale *on-chain* IDL

The diff first showed `update_deactivated` one account short — the Rust
`UpdateCommon` carries `validator_list` as its 13th field (added Aug 2023), but
our IDL modelled `common` as 12 accounts, shifting `operational_sol_account` and
`system_program` down a slot.

The cause was not a stale local file. Marinade's program has **no IDL committed
in `liquid-staking-program`** (any branch, any point in history), so the config
fetched it from the on-chain IDL account — and that account has not been
re-uploaded since before Aug 2023, while the program itself was last deployed
**2026-07-16**. The team upgraded the bytecode without refreshing the IDL, so
the on-chain IDL describes a program that no longer exists.

Verified by decoding the `ProgramData` account's `last_deploy_slot`
(433290841 → 2026-07-16) against the IDL account's contents, and cross-checked
against the Rust at both `main` and `mainnet`. Beyond the missing account, the
stale IDL exposed a `redelegate` instruction the deployed program no longer has,
and omitted `create_canonical_stake` / `finalize_delinquent_upgrade`, plus the
`delinquentUpgrader` field on `State` and `delinquentUpgraderActiveBalance` on
`ValidatorRecord`.

Fixed by pointing this one entry at the IDL Marinade publish in their official
TS SDK (`marinade-ts-sdk`, updated 2026-06-25 with "align SDK with program
upgrade"):

```json
"idlURL": "https://raw.githubusercontent.com/marinade-finance/marinade-ts-sdk/refs/heads/main/src/programs/idl/json/marinade_finance.json"
```

### Policy: the on-chain IDL stays the default

**Keep fetching from the on-chain IDL account.** It is the only artifact bound
to the program address we actually call, and a team failing to re-upload it on
deploy should be the exception, not the assumption. An IDL in a repo or SDK
carries the opposite risk: `main` may describe code that is **not yet deployed**,
which breaks the client just as badly and more subtly, because everything still
compiles.

So an `idlURL` override needs evidence that the on-chain IDL is stale *and* that
the replacement matches what is deployed — read from the chain, not from the
repo. For Marinade that was:

1. **The program is newer than its IDL.** Decode `ProgramData.last_deploy_slot`
   — `getAccountInfo` on the program (jsonParsed) gives the programData address;
   `getAccountInfo` on that with `dataSlice{offset:0,length:13}` and base64 gives
   `u32 enum (3 = ProgramData) || u64 last_deploy_slot`; `getBlockTime` dates it.
   Marinade: slot 433290841 → 2026-07-16, versus an IDL account describing
   pre-Aug-2023 code.
2. **Live account data matches the new layout, not the old.** The upgrade
   appended `delinquentUpgrader` to `State` at offset 638, exactly where the old
   layout ended. On the live state account
   (`8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC`) byte 638 is `2` — the `Done`
   variant ordinal — and it is the *only* non-zero byte past the old boundary.
   Under the old layout the program would never write there, so this is the
   deployed program's own output confirming the field exists.

Without both, prefer the on-chain IDL and leave the discrepancy documented
instead. Re-verify step 2 if the SDK IDL later moves ahead of a deploy.

#### Exponent: `"deployed": "vcs"` because there is no on-chain IDL at all

Exponent (`ExponentnaRg3CQbW6dqQNZKXp7gtZ9DGMp1cwC4HAS7`) is **not** the same
kind of override as the entries above. It is not a case of preferring a repo
copy over a stale chain copy — there is nothing on chain to prefer over.

1. **Neither on-chain IDL location exists.** The Anchor IDL account
   (`JAE1nrFzC37Q6Gh7xxAxiE7J7WJ84rdiEMXHqdP7nbU5`, derived as
   `createWithSeed(findProgramAddress([], program), "anchor:idl", program)`) and
   the program-metadata PDA both return null. The derivation was validated
   against three programs that *do* publish — Kamino Lend, Jupiter Perpetuals and
   Orca Whirlpools all resolve to present accounts under the same code — so the
   absence is a fact about Exponent, not a broken derivation. Same for
   `exponent_admin` (`3D6ojc8vBfDteLBDTTRznZbZRh7bkEGQaYqNkudoTCBQ`), which is
   why no client is generated for it.
2. **The repository copy is what the deployed program answers to.** All 42
   declared discriminators dispatch on mainnet, against a garbage-discriminator
   control returning `InstructionFallbackNotFound` (measured with the since-removed
   `tools/idl_probe.py`; the observation stands, the tool does not).

**The dispatch probe does not validate discriminator width, and that matters
here.** Exponent declares one-byte instruction discriminators
(`#[instruction(discriminator = [N])]`, N = 0..41). Anchor dispatch is
`data.starts_with(&DISCRIMINATOR)` and slices only `DISCRIMINATOR.len()`, so a
client emitting the eight-byte zero-padded form still *matches* — and hands the
seven surplus bytes to Borsh as the leading bytes of the arguments. The probe
goes green either way. Generating a correct client required teaching idl-src-gen
to honour the declared width; `ExponentOnChainTests` pins it against a real
mainnet instruction whose data is nine bytes, not sixteen.

The same diff also confirmed the Kamino oracle sentinels are correct: klend's
`RefreshReserve` and kfarms' `RefreshFarm` both declare their optional accounts
as `Option<...>`, and Anchor signals an absent optional account by passing the
*invoked program's* id — which is exactly why `refreshReserve` substitutes the
kLend program and the farms builders substitute the farms program. The generated
`refreshReserveKeys` already encodes this as
`requireNonNullElse(oracle, invokedProgram)`; the hand-written layer adds the
mapping from Kamino's *semantic* null keys (`PublicKey.NONE`, `nu111…`) onto
that positional convention, which `requireNonNullElse` alone would not catch.

#### Kamino Vaults: `"deployed": "vcs"` because the on-chain IDL ran *ahead* of the deploy (2026-10-01)

The reverse of Marinade. Kamino Vaults redeployed at slot 452240450 and rewrote its
anchor IDL account (`CEMRqxNQ9UKW5LaxGwFSbYY63DCyHwpX2XYKcyDmmdVL`) ten minutes
later, at slot 452242685, as it has after each recent deploy. But the document it
wrote describes a build that is not deployed: withdraw tickets
(`update_klend_queue_accounting`, `VaultState.withdraw_ticket_rent_budget`,
`VaultAllocation.klend_queued_ctokens`), conditional reserves
(`invest_in_conditional_reserve`, `ReserveType`, `allocation_priority`,
`GlobalConfig.conditional_liquidity_enabled`), `update_reserve_allocation_v3`, and
three more trailing accounts on `invest` and `invest_with_max_amount`. The client
generated from it compiled.

1. **The deployed image is identified at artifact level.** The ProgramData payload,
   trimmed of trailing zeros, hashes to `b2bb00e4…` and equals the `kamino_vault.so`
   asset of the GitHub release `release/v2.3.0` byte for byte. The otter-verify PDA
   names `de2daca`, the commit that tag points at. None of the twelve error messages
   the unreleased build adds occurs in the binary, while `Invalid permissioning
   authority` does. No public branch, tag or pull-request head of
   `Kamino-Finance/kvault` carries the unreleased code.
2. **The replacement matches the deployed program.** `klend-sdk` 13.0.1 ships the
   2.3.0 document (`src/idl/kvault.json`, still versioned `2.2.2`). Generated from it,
   the client differs from the 2.2.2-era one only by the 2.2.2→2.3.0 source diff
   (`permissioning_authority` taken from `padding_3`,
   `VaultConfigField::PermissioningAuthority`, error 7065) and by the doc comments it
   loses, which that document does not carry. `GroundTruth` over `de2daca` compares 20
   builders and all 20 match; over the anchor document it flags `invest` at 20
   accounts against the Rust's 17.
3. **The extra `invest` accounts break the call, and the extra instructions are
   absent.** Simulated on mainnet with `sigVerify` off against Galaxy USDT
   (`FPQNECVaw9qCHTJ7JRQXQUV94YffmhX3he5khUgx4m3j`, two reserves) at slot 452309938,
   `invest` built from the 2.3.0 document ran to completion and deposited into its
   reserve. The same call with the anchor document's three trailing accounts
   panicked in `allocation_reserve_accounts_iter` (`vault_operations.rs:1048`,
   `AccountOwnedByWrongProgram`) on its first remaining account: the kvault program
   id, read as reserve 0. The three instructions only the anchor document declares
   answer `InstructionFallbackNotFound` (101), as a garbage discriminator does, while
   `update_reserve_allocation_v2` dispatches and fails decoding its missing arguments
   (102). 2.3.0 declares no `#[fallback]`, so 101 is an absence oracle here.

The `vcs` source is pinned to `@kamino-finance/klend-sdk@13.0.1`. A deployed channel
following `@latest` would generate the client from whatever Kamino publishes next,
which can lead a deploy just as this anchor document did. A bare URL has no
`vcsHead`, so the pin gives up the SDK's early warning: the anchor channel, now a
standing entry in the gap report, and `lastDeploySlot` are what will move first.
When the monitor reports the next redeploy, compare the image with the release
assets before advancing the pin.
