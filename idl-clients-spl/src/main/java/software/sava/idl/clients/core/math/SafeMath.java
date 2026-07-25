package software.sava.idl.clients.core.math;

import software.sava.core.encoding.ByteUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

/// The arithmetic an on-chain Solana program does, in the semantics it does it
/// with: unsigned integers that Java's types cannot carry directly — `u64`
/// fields, which a generated type must surface as a signed {@code long} because
/// Java has no unsigned 64-bit primitive — and the wrapping / checked /
/// saturating operations Rust names explicitly and Java has no operator for.
///
/// These primitives belong here rather than in each protocol's util class
/// because they are the same operation wherever they appear, and a private copy
/// per package is how one gets fixed and the others do not: `toU64` was
/// corrected in `OrcaUtil` in July 2026 and its byte-identical sibling in
/// `WhirlpoolQuote` kept the same defect for a week, because nothing connected
/// them. Protocol-specific math — Kamino's scaled fractions, Meteora's Q64.64
/// `pow`, Orca's tick tables — stays with its protocol; only operations whose
/// definition is the integer type itself live here.
///
/// The reinterpretation itself is never re-implemented here:
/// [software.sava.core.encoding.ByteUtil#toUnsignedBigInteger] owns it. What
/// the widening methods below add is the non-negative fast path, which is the
/// only reason to route through this class rather than call `ByteUtil`
/// directly. `gen.SerDeUtil` carries the same conversion a third time and
/// stays where it is on purpose: for a generated reader it is a **serde**
/// concern — the field never fit the primitive Java offers, so widening is
/// part of decoding it — not an arithmetic one.
///
/// Raw little-endian decoding is likewise `ByteUtil`'s, publicly for `u128` and
/// `u256`; a width the public API does not yet cover (a `u192` pod decimal) is
/// zero-extended into the next width up rather than re-implemented — a second
/// decoder in a second module is how the two helpers drift.
///
/// There is deliberately **no** signed-to-unsigned `u128` reinterpretation
/// helper. Generated readers decode `u128` fields with `getUInt128LE`, so a
/// generated value is never negative; a hand-read one that needs repair was
/// read with the wrong method, and the fix belongs at the read site. Offering
/// the repair as a utility would make "read signed, then correct" look like a
/// supported pattern — one downstream port carried exactly that correction
/// for months after the generator was fixed to decode unsigned, by which time
/// it was unreachable code holding three permanently unkillable mutants.
public final class SafeMath {

  /// `u64::MAX`, the inclusive upper bound of a `u64` field.
  public static final BigInteger U64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

  /// `u128::MAX`, which doubles as the mask that truncates an intermediate to
  /// `u128` the way Rust's wrapping operators do.
  public static final BigInteger U128_MASK = BigInteger.ONE.shiftLeft(128).subtract(BigInteger.ONE);

  private static final BigInteger TWO_POW_128 = U128_MASK.add(BigInteger.ONE);

  /// Reinterprets a `u64` field decoded into a signed {@code long}: a
  /// negative value is a high-bit unsigned quantity, not a negative one.
  ///
  /// The reinterpretation is [ByteUtil#toUnsignedBigInteger]'s. What this adds
  /// is the non-negative fast path that `ByteUtil`'s own javadoc tells hot
  /// callers to keep — most values read off an account are below the sign bit,
  /// and `valueOf` is cheaper than any reinterpretation. Holding it here is
  /// what stops each parser from open-coding the same ternary, which is how the
  /// copies in `OrcaUtil`, `DlmmUtils` and the scope readers came about.
  public static BigInteger toUnsignedBigInteger(final long value) {
    return value < 0 ? ByteUtil.toUnsignedBigInteger(value) : BigInteger.valueOf(value);
  }

  /// [#toUnsignedBigInteger] for callers whose next operation is decimal —
  /// oracle prices, pool ratios — so that the reinterpretation is not
  /// re-derived through `Long.toUnsignedString` at each such site.
  ///
  /// Scale 0 either way, which the callers depend on: a `scaleByPowerOfTen` or
  /// `movePointRight` downstream lands in the wrong place otherwise.
  public static BigDecimal toUnsignedBigDecimal(final long value) {
    return value < 0 ? new BigDecimal(ByteUtil.toUnsignedBigInteger(value)) : BigDecimal.valueOf(value);
  }

  /// Narrows an intermediate back to a `u64` field, as Rust's
  /// `u128 -> u64 try_into()` does: out-of-range is an error, not a truncation.
  ///
  /// Returns the value as raw unsigned bits, so results at or above `2^63`
  /// come back as a negative {@code long} — the same representation the field
  /// was read from. Deliberately not `longValueExact`, which throws for the
  /// perfectly valid `u64` range `[2^63, 2^64)`.
  public static long toU64(final BigInteger value) {
    // A negative does not "exceed" anything, and saying so sends the reader
    // looking for an overflow that is not there. The two are different
    // diagnoses: one means the arithmetic went below zero, the other that it
    // outgrew the field.
    if (value.signum() < 0) {
      throw new ArithmeticException("amount is negative, not a u64: " + value);
    }
    if (value.compareTo(U64_MAX) > 0) {
      throw new ArithmeticException("amount exceeds u64: " + value);
    }
    return value.longValue();
  }

  /// Narrows an intermediate back to a `u128` field, as Rust's
  /// `try_into()` does. The counterpart to [#toU64] and, like it, an
  /// **arithmetic** contract: the operand is a computed result that has to fit,
  /// so out-of-range throws [ArithmeticException].
  ///
  /// Not to be confused with validating a caller-supplied argument, which is an
  /// [IllegalArgumentException] contract and stays with the API that takes the
  /// argument — `OrcaUtil.requireU128` looks like a duplicate of this method and
  /// is deliberately not one.
  public static BigInteger toU128(final BigInteger value) {
    if (value.signum() < 0) {
      throw new ArithmeticException("amount is negative, not a u128: " + value);
    }
    if (value.compareTo(U128_MASK) > 0) {
      throw new ArithmeticException("amount exceeds u128: " + value);
    }
    return value;
  }

  /// `a.checked_add(b)` over `u64`: both operands are unsigned bit patterns,
  /// and a sum that will not fit throws rather than wrapping.
  ///
  /// Computed in `long`, not through [BigInteger]: the wrapped sum is below
  /// either operand exactly when the true sum overflowed, which is the standard
  /// unsigned carry test. The allocating form would undo the fast path
  /// [#toUnsignedBigInteger] exists to preserve.
  public static long checkedAddU64(final long a, final long b) {
    final long sum = a + b;
    if (Long.compareUnsigned(sum, a) < 0) {
      throw new ArithmeticException(
          "sum exceeds u64: " + Long.toUnsignedString(a) + " + " + Long.toUnsignedString(b));
    }
    return sum;
  }

  /// `a.checked_sub(b)` over `u64`: an unsigned difference that would go below
  /// zero throws rather than wrapping to a huge value.
  ///
  /// The counterpart to [#checkedAddU64], and the one that actually bites:
  /// subtraction is where an unsigned underflow turns a small negative into
  /// something near `2^64`, which downstream reads as a real balance.
  public static long checkedSubU64(final long a, final long b) {
    if (Long.compareUnsigned(a, b) < 0) {
      throw new ArithmeticException(
          "difference is negative, not a u64: " + Long.toUnsignedString(a) + " - " + Long.toUnsignedString(b));
    }
    return a - b;
  }

  /// `a.saturating_sub(b)` over `u64`: an underflow clamps to zero instead of
  /// throwing, matching the Rust operator of that name.
  public static long saturatingSubU64(final long a, final long b) {
    return Long.compareUnsigned(a, b) < 0 ? 0L : a - b;
  }

  /// `a.checked_mul(b).checked_div(c)` over `u64` with the product computed at
  /// full width, as Rust's `checked_mul_div` helpers do: the intermediate is
  /// allowed to exceed `u64` so long as the quotient does not.
  ///
  /// Rounds toward zero, like integer division on chain. Division by zero
  /// throws [ArithmeticException] rather than returning a sentinel — a zero
  /// denominator here is a liquidity or supply that should have been checked.
  public static long mulDivU64(final long a, final long b, final long c) {
    final var denominator = toUnsignedBigInteger(c);
    if (denominator.signum() == 0) {
      throw new ArithmeticException("mulDiv denominator is zero");
    }
    return toU64(toUnsignedBigInteger(a).multiply(toUnsignedBigInteger(b)).divide(denominator));
  }

  /// `a.wrapping_add(b)` over `u128`, the counterpart to [#wrappingSubU128]:
  /// a sum past `u128::MAX` truncates rather than widening, which is how an
  /// on-chain growth accumulator is defined to roll over.
  public static BigInteger wrappingAddU128(final BigInteger a, final BigInteger b) {
    return a.add(b).and(U128_MASK);
  }

  /// `(a * b) >> shift`, optionally rounding up when any bit is shifted out —
  /// Rust's `checked_mul_shift_right` family, the Q64.64 arithmetic every
  /// concentrated-liquidity program is written in.
  ///
  /// The product is not truncated: callers narrow with [#toU64] or [#toU128]
  /// according to the field they are writing back to, because the same shift
  /// feeds both an uncapped result and an `as u64` cast depending on the
  /// caller.
  public static BigInteger mulShiftRight(final BigInteger a,
                                         final BigInteger b,
                                         final int shift,
                                         final boolean roundUp) {
    final var product = a.multiply(b);
    final var quotient = product.shiftRight(shift);
    if (roundUp && product.and(BigInteger.ONE.shiftLeft(shift).subtract(BigInteger.ONE)).signum() > 0) {
      return quotient.add(BigInteger.ONE);
    }
    return quotient;
  }

  /// `(a * b) >> shift` truncated to `u128`, mirroring a Rust fixed-point step
  /// whose intermediate is a `u128` register: the high bits are discarded, not
  /// carried.
  public static BigInteger mulShiftTruncateU128(final BigInteger a, final BigInteger b, final int shift) {
    return a.multiply(b).shiftRight(shift).and(U128_MASK);
  }

  /// `a.wrapping_sub(b)` over `u128`, matching on-chain Rust accumulator
  /// semantics — a checkpoint ahead of its global counter wraps mod 2^128
  /// instead of going negative.
  public static BigInteger wrappingSubU128(final BigInteger a, final BigInteger b) {
    final var delta = a.subtract(b);
    return delta.signum() < 0 ? delta.add(TWO_POW_128) : delta;
  }

  private SafeMath() {
  }
}
