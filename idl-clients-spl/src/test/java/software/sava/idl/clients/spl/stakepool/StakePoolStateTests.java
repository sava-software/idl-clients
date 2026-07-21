package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.spl.stake.LockUp;
import software.sava.idl.clients.spl.stakepool.StakePoolState.Fee;
import software.sava.idl.clients.spl.stakepool.StakePoolState.FutureEpoch;
import software.sava.idl.clients.spl.stakepool.StakePoolState.FutureEpochFee;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Base64;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.*;

// Drives PIT mutation testing of StakePoolState.parseProgramData(PublicKey, byte[]).
// There is no writer for StakePoolState, so every blob is assembled by hand with the
// Blob helper below and the parser's exact field order is asserted back byte for byte.
// Field order verified against the SPL stake-pool Rust source (program/src/state.rs).
//
// NOTE: stakeReferralFee / solReferralFee are parsed as unsigned u8 (data[offset] & 0xFF);
// these tests pin that current behavior (a 0xC8 byte -> 200), matching the recent fix.
final class StakePoolStateTests {

  private static final MathContext MC = new MathContext(20);

  private static final String JITO_B64 =
      "AUUePdUNO3uFNgRcK3rC7CWUc+vCWuO8vh++sX1S+8e+eXhXwruGsaac0PTcoWwisNzj3eyWuEBcCPHEcDrQj9NUtd6+o5sz4PHc"
          + "+gqPYiqVuLTrluhPL6HjF2cPHpbB2P0j4HUJut3t/bUWqQuRl7tQR0MlXQ43xf9dzookHu3EMZ6naP7fZEyKrpuOIYit0GvFUPv3"
          + "Fsgiuc5jx3g9lS4f/NFB6YMsrxCtkXSVyg8nG1spPNRwJ+pzcAftQOs5oL12jyijo7L7UvNaWKfli1lcRMd47QEs9xqm+cI2cAxk"
          + "Jwbd9uHXZaGT2cvhRs7reawctIXtX1s3kTqM9YV+/wCp/2ThYsyWIwB3wt3iy5EbAOsDAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAZAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADoAwAAAAAAAAEA"
          + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA6AMAAAAAAAABAAAAAAAAAAB8RuAUq5AbALrEb1O0kiMAAwAAAAAAAAEAAAAAAAAA"
          + "APdO5G5V4RQApItSanmLFgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
          + "AAAAAAAAAAAAAAA=";

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static StakePoolState parse(final PublicKey address, final byte[] data) {
    return StakePoolState.parseProgramData(address, data);
  }

  // Hand-rolled little-endian blob builder mirroring the on-chain layout.
  private static final class Blob {

    private final byte[] buf = new byte[2048];
    private int len = 0;

    Blob u8(final int v) {
      buf[len++] = (byte) v;
      return this;
    }

    Blob key(final PublicKey k) {
      final byte[] a = k.toByteArray();
      System.arraycopy(a, 0, buf, len, a.length);
      len += a.length;
      return this;
    }

    Blob u64(long v) {
      for (int i = 0; i < 8; ++i) {
        buf[len++] = (byte) (v & 0xFF);
        v >>>= 8;
      }
      return this;
    }

    Blob i64(final long v) {
      return u64(v);
    }

    Blob fee(final long denominator, final long numerator) {
      return u64(denominator).u64(numerator);
    }

    // FutureEpochFee option: 0 -> just the option byte; else option byte + 16-byte fee.
    Blob future(final int option, final long denominator, final long numerator) {
      u8(option);
      if (option != 0) {
        fee(denominator, numerator);
      }
      return this;
    }

    Blob optionNone() {
      return u8(0);
    }

    Blob optionKey(final PublicKey k) {
      return u8(1).key(k);
    }

    byte[] build() {
      return Arrays.copyOf(buf, len);
    }
  }

  // A minimal blob: all four Option<Pubkey> authorities ABSENT, fixed recognizable filler for
  // every other field, and the three FutureEpochFee variants supplied by the caller. Used to pin
  // that fields after the options still line up regardless of the option/future branches taken.
  private static byte[] minimalBlob(final long totalLamports,
                                    final long poolTokenSupply,
                                    final int nextEpochOpt,
                                    final int nextStakeWithdrawalOpt,
                                    final int nextSolWithdrawalOpt,
                                    final long futureDen,
                                    final long futureNum) {
    return new Blob()
        .u8(AccountType.StakePool.ordinal())
        .key(key(0x11)).key(key(0x12)).key(key(0x13))
        .u8(0) // stakeWithdrawBumpSeed
        .key(key(0x14)).key(key(0x15)).key(key(0x16)).key(key(0x17)).key(key(0x18))
        .u64(totalLamports).u64(poolTokenSupply)
        .u64(0x0102030405060708L) // lastUpdateEpoch
        .i64(-1000L).u64(2000L).key(key(0x19)) // lockUp
        .fee(1000L, 25L) // epochFee
        .future(nextEpochOpt, futureDen, futureNum)
        .optionNone() // preferredDeposit
        .optionNone() // preferredWithdraw
        .fee(2000L, 13L) // stakeDepositFee
        .fee(3000L, 17L) // stakeWithdrawalFee
        .future(nextStakeWithdrawalOpt, futureDen, futureNum)
        .u8(200) // stakeReferralFee
        .optionNone() // solDepositAuthority
        .fee(4000L, 19L) // solDepositFee
        .u8(150) // solReferralFee
        .optionNone() // solWithdrawAuthority
        .fee(5000L, 23L) // solWithdrawalFee
        .future(nextSolWithdrawalOpt, futureDen, futureNum)
        .u64(0x1122334455667788L) // lastEpochPoolTokenSupply
        .u64(0x2233445566778899L) // lastEpochTotalLamports
        .build();
  }

  @Test
  void fullyPopulatedAllOptionsPresent() {
    final var address = key(0x10);
    final byte[] data = new Blob()
        .u8(AccountType.StakePool.ordinal())
        .key(key(0x11)).key(key(0x12)).key(key(0x13))
        .u8(0xFE) // stakeWithdrawBumpSeed, high bit set -> 254 unsigned
        .key(key(0x14)).key(key(0x15)).key(key(0x16)).key(key(0x17)).key(key(0x18))
        .u64(0xFFFFFFFFFFFFFFFFL) // totalLamports, max u64
        .u64(0xFFFFFFFFFFFFFFFFL) // poolTokenSupply, max u64
        .u64(0x0102030405060708L) // lastUpdateEpoch
        .i64(-123456789L).u64(987654321L).key(key(0x19)) // lockUp
        .fee(1000L, 25L) // epochFee
        .future(1, 500L, 7L) // nextEpochFee = ONE
        .optionKey(key(0x1A)) // preferredDeposit PRESENT
        .optionKey(key(0x1B)) // preferredWithdraw PRESENT
        .fee(2000L, 13L) // stakeDepositFee
        .fee(3000L, 17L) // stakeWithdrawalFee
        .future(2, 750L, 9L) // nextStakeWithdrawalFee = TWO
        .u8(0xC8) // stakeReferralFee, 0xC8 -> 200 unsigned
        .optionKey(key(0x1C)) // solDepositAuthority PRESENT
        .fee(4000L, 19L) // solDepositFee
        .u8(0x96) // solReferralFee, 0x96 -> 150 unsigned
        .optionKey(key(0x1D)) // solWithdrawAuthority PRESENT
        .fee(5000L, 23L) // solWithdrawalFee
        .future(1, 600L, 11L) // nextSolWithdrawalFee = ONE
        .u64(0x1122334455667788L) // lastEpochPoolTokenSupply
        .u64(0x2233445566778899L) // lastEpochTotalLamports
        .build();

    final var state = parse(address, data);

    assertEquals(address, state.address());
    assertEquals(AccountType.StakePool, state.accountType());
    assertEquals(key(0x11), state.manager());
    assertEquals(key(0x12), state.staker());
    assertEquals(key(0x13), state.stakeDepositAuthority());
    assertEquals(254, state.stakeWithdrawBumpSeed());
    assertEquals(key(0x14), state.validatorList());
    assertEquals(key(0x15), state.reserveStake());
    assertEquals(key(0x16), state.poolMint());
    assertEquals(key(0x17), state.managerFeeAccount());
    assertEquals(key(0x18), state.tokenProgramId());
    assertEquals(new BigDecimal("18446744073709551615"), state.totalLamports());
    assertEquals(new BigDecimal("18446744073709551615"), state.poolTokenSupply());
    assertEquals(0x0102030405060708L, state.lastUpdateEpoch());
    assertEquals(new LockUp(-123456789L, 987654321L, key(0x19)), state.lockUp());
    assertEquals(new Fee(1000L, 25L), state.epochFee());
    assertEquals(new FutureEpochFee(FutureEpoch.ONE, new Fee(500L, 7L)), state.nextEpochFee());
    assertEquals(key(0x1A), state.preferredDepositValidatorVoteAddress());
    assertEquals(key(0x1B), state.preferredWithdrawValidatorVoteAddress());
    assertEquals(new Fee(2000L, 13L), state.stakeDepositFee());
    assertEquals(new Fee(3000L, 17L), state.stakeWithdrawalFee());
    assertEquals(new FutureEpochFee(FutureEpoch.TWO, new Fee(750L, 9L)), state.nextStakeWithdrawalFee());
    assertEquals(200, state.stakeReferralFee());
    assertEquals(key(0x1C), state.solDepositAuthority());
    assertEquals(new Fee(4000L, 19L), state.solDepositFee());
    assertEquals(150, state.solReferralFee());
    assertEquals(key(0x1D), state.solWithdrawAuthority());
    assertEquals(new Fee(5000L, 23L), state.solWithdrawalFee());
    assertEquals(new FutureEpochFee(FutureEpoch.ONE, new Fee(600L, 11L)), state.nextSolWithdrawalFee());
    assertEquals(0x1122334455667788L, state.lastEpochPoolTokenSupply());
    assertEquals(0x2233445566778899L, state.lastEpochTotalLamports());
  }

  @Test
  void minimalAllOptionsAbsentFuturesNone() {
    final var address = key(0x10);
    final byte[] data = minimalBlob(0L, 0L, 0, 0, 0, 0L, 0L);

    final var state = parse(address, data);

    assertEquals(address, state.address());
    assertEquals(AccountType.StakePool, state.accountType());
    assertEquals(key(0x11), state.manager());
    assertEquals(key(0x12), state.staker());
    assertEquals(key(0x13), state.stakeDepositAuthority());
    assertEquals(0, state.stakeWithdrawBumpSeed());
    assertEquals(key(0x14), state.validatorList());
    assertEquals(key(0x15), state.reserveStake());
    assertEquals(key(0x16), state.poolMint());
    assertEquals(key(0x17), state.managerFeeAccount());
    assertEquals(key(0x18), state.tokenProgramId());
    assertEquals(new BigDecimal("0"), state.totalLamports());
    assertEquals(new BigDecimal("0"), state.poolTokenSupply());
    assertEquals(0x0102030405060708L, state.lastUpdateEpoch());
    assertEquals(new LockUp(-1000L, 2000L, key(0x19)), state.lockUp());
    assertEquals(new Fee(1000L, 25L), state.epochFee());

    // Absent options -> null authorities; NONE futures.
    assertEquals(FutureEpoch.NONE, state.nextEpochFee().futureEpoch());
    assertNull(state.nextEpochFee().fee());
    assertNull(state.preferredDepositValidatorVoteAddress());
    assertNull(state.preferredWithdrawValidatorVoteAddress());
    assertEquals(FutureEpoch.NONE, state.nextStakeWithdrawalFee().futureEpoch());
    assertNull(state.nextStakeWithdrawalFee().fee());
    assertNull(state.solDepositAuthority());
    assertNull(state.solWithdrawAuthority());
    assertEquals(FutureEpoch.NONE, state.nextSolWithdrawalFee().futureEpoch());
    assertNull(state.nextSolWithdrawalFee().fee());

    // Fields after the (shorter) option/future branches must still line up.
    assertEquals(new Fee(2000L, 13L), state.stakeDepositFee());
    assertEquals(new Fee(3000L, 17L), state.stakeWithdrawalFee());
    assertEquals(200, state.stakeReferralFee());
    assertEquals(new Fee(4000L, 19L), state.solDepositFee());
    assertEquals(150, state.solReferralFee());
    assertEquals(new Fee(5000L, 23L), state.solWithdrawalFee());
    assertEquals(0x1122334455667788L, state.lastEpochPoolTokenSupply());
    assertEquals(0x2233445566778899L, state.lastEpochTotalLamports());
  }

  @Test
  void futureEpochFeeVariants() {
    // All NONE.
    final var none = parse(null, minimalBlob(1L, 1L, 0, 0, 0, 0L, 0L));
    assertEquals(new FutureEpochFee(FutureEpoch.NONE, null), none.nextEpochFee());
    assertEquals(new FutureEpochFee(FutureEpoch.NONE, null), none.nextStakeWithdrawalFee());
    assertEquals(new FutureEpochFee(FutureEpoch.NONE, null), none.nextSolWithdrawalFee());

    // All ONE (option byte 1 + fee).
    final var one = parse(null, minimalBlob(1L, 1L, 1, 1, 1, 111L, 3L));
    assertEquals(new FutureEpochFee(FutureEpoch.ONE, new Fee(111L, 3L)), one.nextEpochFee());
    assertEquals(new FutureEpochFee(FutureEpoch.ONE, new Fee(111L, 3L)), one.nextStakeWithdrawalFee());
    assertEquals(new FutureEpochFee(FutureEpoch.ONE, new Fee(111L, 3L)), one.nextSolWithdrawalFee());

    // All TWO (option byte 2 + fee).
    final var two = parse(null, minimalBlob(1L, 1L, 2, 2, 2, 222L, 5L));
    assertEquals(new FutureEpochFee(FutureEpoch.TWO, new Fee(222L, 5L)), two.nextEpochFee());
    assertEquals(new FutureEpochFee(FutureEpoch.TWO, new Fee(222L, 5L)), two.nextStakeWithdrawalFee());
    assertEquals(new FutureEpochFee(FutureEpoch.TWO, new Fee(222L, 5L)), two.nextSolWithdrawalFee());
  }

  @Test
  void futureEpochFeeOptionThreeThrows() {
    // Option byte > 2 is rejected at each of the three parse sites.
    for (final int[] opts : new int[][]{{3, 0, 0}, {0, 3, 0}, {0, 0, 3}}) {
      final var ex = assertThrows(IllegalStateException.class,
          () -> parse(null, minimalBlob(1L, 1L, opts[0], opts[1], opts[2], 1L, 1L))
      );
      assertTrue(ex.getMessage().contains("3"), ex.getMessage());
    }
  }

  @Test
  void accountTypeOutOfRangeThrows() {
    // AccountType.values() has 3 entries; ordinal 3 is out of range.
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> parse(null, new byte[]{3}));
  }

  @Test
  void accountTypeNegativeThrows() {
    // A high-bit byte is a negative int index into AccountType.values().
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> parse(null, new byte[]{(byte) 0xFF}));
  }

  @Test
  void truncatedBlobThrows() {
    final byte[] full = minimalBlob(1L, 1L, 0, 0, 0, 0L, 0L);
    final byte[] truncated = Arrays.copyOf(full, full.length - 4);
    assertThrows(IndexOutOfBoundsException.class, () -> parse(null, truncated));
  }

  @Test
  void feeToRatio() {
    final var fee = new Fee(4L, 1L); // 1/4

    assertEquals(0, new BigDecimal("0.25").compareTo(fee.toRatio(MC)));
    assertEquals(0, new BigDecimal("0.25").compareTo(fee.toRatio(6, HALF_UP)));
    assertEquals(0.25d, fee.toRatio());

    // Zero numerator -> ZERO / 0.0.
    final var zeroNum = new Fee(4L, 0L);
    assertSame(ZERO, zeroNum.toRatio(MC));
    assertSame(ZERO, zeroNum.toRatio(6, HALF_UP));
    assertEquals(0.0d, zeroNum.toRatio());

    // Zero denominator -> ZERO / 0.0 (guards division by zero).
    final var zeroDen = new Fee(0L, 5L);
    assertSame(ZERO, zeroDen.toRatio(MC));
    assertSame(ZERO, zeroDen.toRatio(6, HALF_UP));
    assertEquals(0.0d, zeroDen.toRatio());
  }

  @Test
  void feeCompareTo() {
    // Zero-numerator ordering falls back to comparing raw numerators.
    assertTrue(new Fee(100L, 0L).compareTo(new Fee(100L, 5L)) < 0);
    assertTrue(new Fee(100L, 5L).compareTo(new Fee(100L, 0L)) > 0);
    assertEquals(0, new Fee(100L, 0L).compareTo(new Fee(200L, 0L)));

    // Non-zero numerators compare by ratio, not by raw numerator.
    assertEquals(0, new Fee(1000L, 100L).compareTo(new Fee(10L, 1L))); // 0.1 == 0.1
    assertTrue(new Fee(1000L, 100L).compareTo(new Fee(10L, 5L)) < 0);  // 0.1 < 0.5 though 100 > 5
    assertTrue(new Fee(10L, 5L).compareTo(new Fee(1000L, 100L)) > 0);

    // A zero *denominator* also ratios to zero, so ratio comparison cannot separate
    // Fee(0, 5) from Fee(anything, 0) — only the raw-numerator branch can. These two cases
    // pin that the zero-numerator test picks the branch, on either side of the comparison.
    assertEquals(0.0d, new Fee(0L, 5L).toRatio()); // undefined ratio, reported as zero
    assertTrue(new Fee(0L, 0L).compareTo(new Fee(0L, 5L)) < 0, "0 numerator sorts below 5");
    assertTrue(new Fee(0L, 5L).compareTo(new Fee(100L, 0L)) > 0, "5 sorts above a 0 numerator");
  }

  @Test
  void calculateSolPrice() {
    final var priced = parse(null, minimalBlob(10L, 4L, 0, 0, 0, 0L, 0L));
    assertEquals(0, new BigDecimal("2.5").compareTo(priced.calculateSolPrice(MC)));
    assertEquals(0, new BigDecimal("2.5").compareTo(priced.calculateSolPrice(6, HALF_UP)));
    assertTrue(priced.calculateSolPrice(MC).signum() > 0);

    // Zero supply -> ZERO branch (both overloads).
    final var zeroSupply = parse(null, minimalBlob(10L, 0L, 0, 0, 0, 0L, 0L));
    assertSame(ZERO, zeroSupply.calculateSolPrice(MC));
    assertSame(ZERO, zeroSupply.calculateSolPrice(6, HALF_UP));

    // Zero total lamports -> ZERO branch too.
    final var zeroTotal = parse(null, minimalBlob(0L, 10L, 0, 0, 0, 0L, 0L));
    assertSame(ZERO, zeroTotal.calculateSolPrice(MC));
    assertSame(ZERO, zeroTotal.calculateSolPrice(6, HALF_UP));
  }

  /// The address-less overload parses the same bytes and leaves the address null, rather
  /// than being a distinct code path.
  @Test
  void parseWithoutAddress() {
    final byte[] data = minimalBlob(10L, 4L, 0, 0, 0, 0L, 0L);

    final var anonymous = StakePoolState.parseProgramData(data);

    assertNull(anonymous.address());
    assertEquals(AccountType.StakePool, anonymous.accountType());
    assertEquals(0, new BigDecimal("2.5").compareTo(anonymous.calculateSolPrice(MC)));
    // identical to the addressed parse in every other respect
    assertEquals(parse(null, data), anonymous);
  }

  @Test
  void parsesRealJitoStakePoolAccount() {
    final var address = PublicKey.fromBase58Encoded("Jito4APyf642JPZPx3hGc6WWJ8zPKtRbRs4P815Awbb");
    final byte[] data = Base64.getDecoder().decode(JITO_B64);

    final var state = parse(address, data);

    assertEquals(AccountType.StakePool, state.accountType());
    assertEquals(address, state.address());
    assertEquals("Jito4APyf642JPZPx3hGc6WWJ8zPKtRbRs4P815Awbb", state.address().toBase58());

    // SOL price should be positive and roughly >= 1 (never below 1:1 for a live pool).
    final var solPrice = state.calculateSolPrice(MC);
    assertTrue(solPrice.signum() > 0, "sol price must be positive: " + solPrice);
    assertTrue(solPrice.compareTo(BigDecimal.ONE) >= 0, "sol price should be >= 1: " + solPrice);

    // Referral fees are percentages in [0, 100].
    assertTrue(state.stakeReferralFee() >= 0 && state.stakeReferralFee() <= 100, "stakeReferralFee");
    assertTrue(state.solReferralFee() >= 0 && state.solReferralFee() <= 100, "solReferralFee");

    // Every fee must be a sane fraction: numerator <= denominator.
    assertSaneFee(state.epochFee());
    assertSaneFee(state.stakeDepositFee());
    assertSaneFee(state.stakeWithdrawalFee());
    assertSaneFee(state.solDepositFee());
    assertSaneFee(state.solWithdrawalFee());
  }

  private static void assertSaneFee(final Fee fee) {
    assertTrue(fee.numerator() >= 0, "fee numerator must be non-negative");
    assertTrue(fee.denominator() >= 0, "fee denominator must be non-negative");
    assertTrue(fee.numerator() <= fee.denominator(), "fee numerator must be <= denominator: " + fee);
  }
}
