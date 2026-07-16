package software.sava.idl.clients.spl.stakepool;

import software.sava.core.accounts.PublicKey;

/// Jazzer entry point for the SPL stake-pool `StakePoolState` account parser. The layout
/// is a long sequential read whose `Option<Pubkey>` presence bytes and `FutureEpochFee`
/// option bytes steer the offset arithmetic — a drifted offset can still land on a
/// structurally "valid" parse, which the invariants below catch.
///
/// Malformed-input contract: garbage in -> [RuntimeException] out. Truncation throws
/// `IndexOutOfBoundsException`, an out-of-range `AccountType`/`FutureEpoch` throws
/// `ArrayIndexOutOfBoundsException`/`IllegalStateException`; all are tolerated. Jazzer
/// flags what the contract forbids — any non-[RuntimeException] throwable — plus the
/// consistency invariants that must hold whenever a parse fully succeeds.
///
/// Seeded from a real mainnet stake-pool account under
/// src/test/resources/fuzz/stakePoolState; the ~600-byte layout is unreachable from
/// scratch.
///
/// Deliberately free of Jazzer imports so it compiles with the regular test sources.
///
/// Run with `./gradlew :idl-clients-spl:fuzzStakePoolState [-PmaxFuzzTime=<seconds>]`.
public final class StakePoolStateFuzz {

  public static void fuzzerTestOneInput(final byte[] data) {
    final StakePoolState state;
    try {
      state = StakePoolState.parseProgramData(null, data);
    } catch (final RuntimeException tolerated) {
      // truncation, a bad AccountType/FutureEpoch option — rejection is in contract
      return;
    }

    // Every fixed pubkey field is read from a bounded slice and can never be null.
    if (state.manager() == null || state.staker() == null || state.stakeDepositAuthority() == null
        || state.validatorList() == null || state.reserveStake() == null || state.poolMint() == null
        || state.managerFeeAccount() == null || state.tokenProgramId() == null || state.lockUp() == null) {
      throw new AssertionError("a mandatory field parsed as null");
    }
    // u8 fields must round-trip inside the unsigned byte range.
    if (state.stakeReferralFee() < 0 || state.stakeReferralFee() > 0xFF
        || state.solReferralFee() < 0 || state.solReferralFee() > 0xFF
        || state.stakeWithdrawBumpSeed() < 0 || state.stakeWithdrawBumpSeed() > 0xFF) {
      throw new AssertionError("a u8 field is outside [0, 255]");
    }
    // FutureEpochFee is a tagged union: NONE carries no fee, ONE/TWO carry one.
    assertFutureFee(state.nextEpochFee());
    assertFutureFee(state.nextStakeWithdrawalFee());
    assertFutureFee(state.nextSolWithdrawalFee());
    // The unsigned-lamports conversion is non-negative, and the price helper never throws
    // (its zero-supply branch is guarded).
    if (state.totalLamports().signum() < 0 || state.poolTokenSupply().signum() < 0) {
      throw new AssertionError("unsigned lamports decoded negative");
    }
    if (state.calculateSolPrice(java.math.MathContext.DECIMAL64).signum() < 0) {
      throw new AssertionError("negative SOL price");
    }

    // Parsing is a pure function of the bytes: a re-parse must produce an equal record.
    final var again = StakePoolState.parseProgramData((PublicKey) null, data);
    if (!state.equals(again)) {
      throw new AssertionError("parseProgramData is not deterministic");
    }
  }

  private static void assertFutureFee(final StakePoolState.FutureEpochFee futureFee) {
    final boolean isNone = futureFee.futureEpoch() == StakePoolState.FutureEpoch.NONE;
    if (isNone != (futureFee.fee() == null)) {
      throw new AssertionError("FutureEpochFee tag/fee mismatch: " + futureFee.futureEpoch());
    }
  }

  private StakePoolStateFuzz() {
  }
}
