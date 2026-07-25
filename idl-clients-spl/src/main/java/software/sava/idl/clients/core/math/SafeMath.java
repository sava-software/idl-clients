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
    if (value.signum() < 0 || value.compareTo(U64_MAX) > 0) {
      throw new ArithmeticException("amount exceeds u64: " + value);
    }
    return value.longValue();
  }

  /// `a.checked_add(b)` over `u64`: both operands are unsigned bit patterns,
  /// and a sum that will not fit throws rather than wrapping.
  public static long checkedAddU64(final long a, final long b) {
    return toU64(toUnsignedBigInteger(a).add(toUnsignedBigInteger(b)));
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
