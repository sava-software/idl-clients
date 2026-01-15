package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param circulatingTicketCount count tickets for delayed-unstake
/// @param circulatingTicketBalance total lamports amount of generated and not claimed yet tickets
public record State(PublicKey msolMint,
                    PublicKey adminAuthority,
                    PublicKey operationalSolAccount,
                    PublicKey treasuryMsolAccount,
                    int reserveBumpSeed,
                    int msolMintAuthorityBumpSeed,
                    long rentExemptForTokenAcc,
                    Fee rewardFee,
                    StakeSystem stakeSystem,
                    ValidatorSystem validatorSystem,
                    LiqPool liqPool,
                    long availableReserveBalance,
                    long msolSupply,
                    long msolPrice,
                    long circulatingTicketCount,
                    long circulatingTicketBalance,
                    long lentFromReserve,
                    long minDeposit,
                    long minWithdraw,
                    long stakingSolCap,
                    long emergencyCoolingDown) implements SerDe {

  public static final int BYTES = 568;

  public static final int MSOL_MINT_OFFSET = 0;
  public static final int ADMIN_AUTHORITY_OFFSET = 32;
  public static final int OPERATIONAL_SOL_ACCOUNT_OFFSET = 64;
  public static final int TREASURY_MSOL_ACCOUNT_OFFSET = 96;
  public static final int RESERVE_BUMP_SEED_OFFSET = 128;
  public static final int MSOL_MINT_AUTHORITY_BUMP_SEED_OFFSET = 129;
  public static final int RENT_EXEMPT_FOR_TOKEN_ACC_OFFSET = 130;
  public static final int REWARD_FEE_OFFSET = 138;
  public static final int STAKE_SYSTEM_OFFSET = 142;
  public static final int VALIDATOR_SYSTEM_OFFSET = 256;
  public static final int LIQ_POOL_OFFSET = 377;
  public static final int AVAILABLE_RESERVE_BALANCE_OFFSET = 488;
  public static final int MSOL_SUPPLY_OFFSET = 496;
  public static final int MSOL_PRICE_OFFSET = 504;
  public static final int CIRCULATING_TICKET_COUNT_OFFSET = 512;
  public static final int CIRCULATING_TICKET_BALANCE_OFFSET = 520;
  public static final int LENT_FROM_RESERVE_OFFSET = 528;
  public static final int MIN_DEPOSIT_OFFSET = 536;
  public static final int MIN_WITHDRAW_OFFSET = 544;
  public static final int STAKING_SOL_CAP_OFFSET = 552;
  public static final int EMERGENCY_COOLING_DOWN_OFFSET = 560;

  public static State read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var msolMint = readPubKey(_data, i);
    i += 32;
    final var adminAuthority = readPubKey(_data, i);
    i += 32;
    final var operationalSolAccount = readPubKey(_data, i);
    i += 32;
    final var treasuryMsolAccount = readPubKey(_data, i);
    i += 32;
    final var reserveBumpSeed = _data[i] & 0xFF;
    ++i;
    final var msolMintAuthorityBumpSeed = _data[i] & 0xFF;
    ++i;
    final var rentExemptForTokenAcc = getInt64LE(_data, i);
    i += 8;
    final var rewardFee = Fee.read(_data, i);
    i += rewardFee.l();
    final var stakeSystem = StakeSystem.read(_data, i);
    i += stakeSystem.l();
    final var validatorSystem = ValidatorSystem.read(_data, i);
    i += validatorSystem.l();
    final var liqPool = LiqPool.read(_data, i);
    i += liqPool.l();
    final var availableReserveBalance = getInt64LE(_data, i);
    i += 8;
    final var msolSupply = getInt64LE(_data, i);
    i += 8;
    final var msolPrice = getInt64LE(_data, i);
    i += 8;
    final var circulatingTicketCount = getInt64LE(_data, i);
    i += 8;
    final var circulatingTicketBalance = getInt64LE(_data, i);
    i += 8;
    final var lentFromReserve = getInt64LE(_data, i);
    i += 8;
    final var minDeposit = getInt64LE(_data, i);
    i += 8;
    final var minWithdraw = getInt64LE(_data, i);
    i += 8;
    final var stakingSolCap = getInt64LE(_data, i);
    i += 8;
    final var emergencyCoolingDown = getInt64LE(_data, i);
    return new State(msolMint,
                     adminAuthority,
                     operationalSolAccount,
                     treasuryMsolAccount,
                     reserveBumpSeed,
                     msolMintAuthorityBumpSeed,
                     rentExemptForTokenAcc,
                     rewardFee,
                     stakeSystem,
                     validatorSystem,
                     liqPool,
                     availableReserveBalance,
                     msolSupply,
                     msolPrice,
                     circulatingTicketCount,
                     circulatingTicketBalance,
                     lentFromReserve,
                     minDeposit,
                     minWithdraw,
                     stakingSolCap,
                     emergencyCoolingDown);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    msolMint.write(_data, i);
    i += 32;
    adminAuthority.write(_data, i);
    i += 32;
    operationalSolAccount.write(_data, i);
    i += 32;
    treasuryMsolAccount.write(_data, i);
    i += 32;
    _data[i] = (byte) reserveBumpSeed;
    ++i;
    _data[i] = (byte) msolMintAuthorityBumpSeed;
    ++i;
    putInt64LE(_data, i, rentExemptForTokenAcc);
    i += 8;
    i += rewardFee.write(_data, i);
    i += stakeSystem.write(_data, i);
    i += validatorSystem.write(_data, i);
    i += liqPool.write(_data, i);
    putInt64LE(_data, i, availableReserveBalance);
    i += 8;
    putInt64LE(_data, i, msolSupply);
    i += 8;
    putInt64LE(_data, i, msolPrice);
    i += 8;
    putInt64LE(_data, i, circulatingTicketCount);
    i += 8;
    putInt64LE(_data, i, circulatingTicketBalance);
    i += 8;
    putInt64LE(_data, i, lentFromReserve);
    i += 8;
    putInt64LE(_data, i, minDeposit);
    i += 8;
    putInt64LE(_data, i, minWithdraw);
    i += 8;
    putInt64LE(_data, i, stakingSolCap);
    i += 8;
    putInt64LE(_data, i, emergencyCoolingDown);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
