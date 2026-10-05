# Mutation-testing records — `idl-clients-spl`

This file is the registry of what the mutation ratchet accepts in this module and why: one
argument per family label the accepted-baseline rows carry, the cause of each audited timeout
member, and the measurements behind the suite's mutator set. The policy it applies is
sava-build's `HARDENING.md` and the task and option reference is `hardeningHelp`; neither is
restated here. The counts are what `pitestSplVerify` and `pitestSplDebt` print, never prose. The
journal this registry replaced is `HISTORY.md` beside it, the README as it stood on 2026-10-05,
kept verbatim and unmaintained.

Row format in `spl-accepted.csv`: `class,method,mutator,STATUS # <family> # line N`. The key is
line-less, and identical rows are sibling mutants, compared as a multiset. The `# line` tag is
review metadata: `pitestSplBaselineRetag` refreshes it once the argument here has been re-read
against the moved code, and it is never edited by hand. That is why the arguments below name
classes, methods and branches and never source lines.

Every label on a row appears in this file literally, as `# <label>` in backticks: the verify and
the debt listing resolve a label by searching this file for that text and warn when it does not
resolve (`# untriaged`, seeded debt, needs no entry). A label is an argument for the members its
bullet lists, never for a superficially similar mutant: each bullet states the mechanism, what
stays equal, the escape that would make the mutant observable, and the members it covers.

Acceptance is for a mutant equivalent in observable behaviour, or for one whose observation
needs a capability the deterministic harness lacks, named in its argument; never for "hard to
test". Records are written only through the plugin's writer tasks; the one hand edit a row takes
is its label, replaced in triage with a fresh history-free run of the suite behind it. An
argument here is updated in place when its code or its members change; what a run counted, added
or pruned is that run's output and git's history.

## Suite

The `hardening {}` block of `idl-clients-spl/build.gradle.kts` is the authoritative declaration
of the suite, its targets, exclusions and mutators; this is the reading guide.

`pitestSpl` is the module's one suite and a catch-all by exclusion: `targetClasses` is
`software.sava.idl.clients.spl.*` and `software.sava.idl.clients.core.*`, so a new hand-written
class is mutated by default rather than silently skipped. The module is account encode/decode,
instruction building and the unsigned arithmetic beneath them, the money-critical shape
throughout, so nothing is scoped out. What the exclusions leave is the hand-written layer: the
SPL and stake-pool clients and instruction builders, the stake, stake-pool, nonce and
precompile parsers and offset records, and `core.math.SafeMath`, the `u64`/`u128` arithmetic
that the bundle's protocol math also calls.

Excluded: generated `software.sava.idl.clients.*.gen.*` code, the per-program bindings and the
shared commons in `core.gen`, whose correctness belongs to idl-src-gen and whose boilerplate
would bury the hand-written signal (the `declineExclusionAudit(...)` call beside the exclusion
records what carries that correctness instead); the test and fuzz classes, which share the
recompiled root; and `software.sava.idl.clients.*.Integ`, the git-ignored scratch mains, present
on a dev machine and absent in CI, so mutating one would make the baseline machine-dependent.
`targetTests` is every `software.sava.idl.clients.*Test*` class.

## Mutator set

`STRONGER,EXPERIMENTAL_BIG_INTEGER,EXPERIMENTAL_NAKED_RECEIVER`, with `EXPERIMENTAL_BIG_DECIMAL`
declined through `declineMutator(...)`, whose message is the authority for the decline. The
numbers are trial measurements no listing reconstructs, read as generated / killed by existing
tests / unkilled.

- `EXPERIMENTAL_BIG_INTEGER`, enabled. `core.math.SafeMath`'s full-width and `u128` arithmetic
  (`mulDivU64`, `mulShiftRight`, `mulShiftTruncateU128`, `wrappingAddU128`, `wrappingSubU128`)
  is `BigInteger` method calls, which `MathMutator` (primitive bytecode arithmetic) cannot see.
  Trialed 2026-07-25 with sava-build 21.5.14's blind-spot advice: 3 / 3 / 0, and enabled at no
  baseline cost.
- `EXPERIMENTAL_NAKED_RECEIVER`, enabled. It replaces a fluent call with its receiver: a call
  returning its own receiver type is an expression, invisible to `VoidMethodCallMutator`.
  Trialed 2026-07-23 with sava-build 21.5.9's
  `pitestMutatorTrial -PtrialMutators=EXPERIMENTAL_NAKED_RECEIVER`: 19 / 15 / 4. The unkilled
  were the dropped `stripTrailingZeros` calls in both `BigDecimal` overloads of
  `StakePoolState.calculateSolPrice` and `StakePoolState$Fee.toRatio`, which
  `StakePoolStateTests.feeToRatio` and `calculateSolPriceStripsToCanonicalForm` pin with
  scale-sensitive `BigDecimal.equals` on inputs whose division leaves trailing zeros.
- `EXPERIMENTAL_BIG_DECIMAL`, declined. Trialed 2026-07-25: 0 generated against the four
  `BigDecimal` arithmetic call sites the blind-spot scan reported. It rewrites only `BigDecimal`
  calls that take one `BigDecimal` or nothing and return one (`add`, `subtract`, `multiply`,
  `divide`, `remainder`, `max`, `min`; `negate`, `plus`, `abs`), and every `BigDecimal` arithmetic
  call in the suite is a `divide(BigDecimal, MathContext)` or
  `divide(BigDecimal, int, RoundingMode)` overload in
  `StakePoolState.calculateSolPrice` or `StakePoolState$Fee.toRatio`, which it does not rewrite.
  Those calls are covered instead by `EXPERIMENTAL_NAKED_RECEIVER`, whose dropped-`divide` and
  dropped-`stripTrailingZeros` mutants there are killed, and by `StakePoolStateTests`'
  exact-value assertions. The decline holds while every `BigDecimal` arithmetic call in the
  suite is one of those overloads.

## spl (`pitestSpl`)

### Families

- `# zero-fast-path family` — a zero short-circuit in front of a division with one clause
  forced false, so the input it answered falls through to the division, which for a positive
  denominator computes the same zero the long way.
  `SafeMath.mulDivU64(long, BigInteger, BigInteger, boolean)` first throws for a zero
  `denominator`, so the fall-through never divides by zero, and then returns `0L` when
  `amount == 0L || numeratorFactor.signum() == 0`. With the second clause forced false, a zero
  factor beside a non-zero amount takes the general path, which for a positive `denominator`
  returns the short-circuit's own answer: the product is zero, the quotient is zero, the
  remainder is zero so `roundUp` adds nothing, and `toU64` returns `0L`. The comment above the
  clause makes the same claim, that the fall-through computes this zero the long way and the
  short-circuit only skips the allocation; it holds for a positive `denominator`.
  `SafeMathTests.mulDivRoundsAwayFromZeroOnlyWhenAsked` runs a zero factor against a positive
  denominator with `roundUp` and asserts `0L`, which the mutant also returns. The sibling
  mutant on the `amount == 0L` clause always takes that clause's jump, so the method returns
  zero whatever the amount and factor; `SafeMathTests.mulDivComputesTheProductAtFullWidth`
  kills it. Escape: a negative `denominator` with `roundUp`, an input the method accepts today.
  The method's up-front check rejects only zero, and its javadoc's "Both operands are unsigned"
  does not say which of the three parameters it means. On that input the long way's
  `BigInteger.mod` throws for a non-positive modulus where the short-circuit returned `0L`, so
  a test passing one separates the two; without `roundUp` the long way never calls `mod`, and
  `BigInteger.divide` by a negative divisor returns the same zero. Covers:
  `SafeMath.mulDivU64(long, BigInteger, BigInteger, boolean)`, the
  `numeratorFactor.signum() == 0` clause of the zero short-circuit forced false, which is the
  second of the short-circuit's two conditional jumps.
- `# equal-operands family` — a comparison boundary whose two branches compute the same value at
  the one input the boundary moves. `SafeMath.saturatingSubU64` is
  `Long.compareUnsigned(a, b) < 0 ? 0L : a - b`, and widening the comparison to `<= 0` moves
  only the equal-operand case `a == b`, the only one `compareUnsigned` answers 0 for, into the
  clamp, where the subtraction the mutant skips would have produced `0` anyway; no input
  separates them. The label means the branches agree on a value, never that a boundary is
  merely hard to reach: the neighbouring `checkedAddU64` boundary looks the same and is not
  equivalent, because its equality case (`b == 0`) selects between returning the sum and
  throwing, and `SafeMathTests.checkedAddTreatsBothOperandsAsUnsigned` kills it. The comparison
  forced false, which never clamps, is killed by
  `SafeMathTests.saturatingSubClampsInsteadOfThrowing`. Escape: none while the clamp is `0L` and
  the other arm is `a - b`; an arm that differs from the other at equal operands makes the
  boundary observable. Covers: `SafeMath.saturatingSubU64`, the `compareUnsigned(a, b) < 0`
  boundary widened to `<= 0`.
- `# fast-path-guard family` — a guard that picks the cheaper of two computations that agree on
  every input it can route either way. `SafeMath.toUnsignedBigInteger` and
  `SafeMath.toUnsignedBigDecimal` are `value < 0 ? <reinterpret the bits> : <valueOf(value)>`,
  the reinterpretation being `ByteUtil.toUnsignedBigInteger`'s. sava-core's javadoc for that
  method calls it correct for every `long` and says that for a non-negative value `valueOf` is
  equal and cheaper, so hot paths can branch on `val < 0`: the test is not a correctness branch
  but picks the cheaper of two equal answers. Widening `< 0` to `<= 0` moves only `0` to the
  reinterpretation, and both arms return zero there: an equal `BigInteger`, and for the decimal
  an equal `BigDecimal` at the same scale 0.
  `SafeMathTests.u64ReinterpretsTheSignBitNotTheMagnitude` and
  `unsignedDecimalCarriesTheIntegerValueAtScaleZero` assert each method's result at `0L`, the
  second with scale-sensitive `equals`, and the mutants pass them. The test forced false, which
  hands a negative `long` to `valueOf` and so returns a negative number rather than a `u64`, is
  killed by the same tests. The guard stays: calling `ByteUtil.toUnsignedBigInteger`
  unconditionally would erase these rows but costs an allocation per non-negative read on the
  oracle and pool parsers, and holding the branch here, once, is what keeps each parser from
  open-coding it. Escape: an arm that stops returning an equal zero at `0`, a scale other than 0
  included, or a reader that compares the result by reference: `valueOf` returns the cached
  `BigInteger.ZERO` or `BigDecimal.ZERO` for `0` and the reinterpretation a fresh zero, an
  identity neither method promises. Covers: `SafeMath.toUnsignedBigInteger` and
  `SafeMath.toUnsignedBigDecimal`, the `value < 0` test widened to `value <= 0` in each.

### Audited timeout-detected mutants

`spl-timeouts.csv` arms the verify's timeout audit and holds no member, so a first timed-out
mutant in this suite is an unaudited newcomer: a reviewer stop, not detection, until its cause
is argued here.

### Declined and untriaged debt

None: every accepted row of this suite is covered by a bullet under "Families".
