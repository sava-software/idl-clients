package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LPMintRedeemRecord(Discriminator discriminator,
                                 long ts,
                                 long slot,
                                 PublicKey authority,
                                 int description,
                                 BigInteger amount,
                                 BigInteger fee,
                                 int spotMarketIndex,
                                 int constituentIndex,
                                 long oraclePrice,
                                 PublicKey mint,
                                 long lpAmount,
                                 long lpFee,
                                 BigInteger lpPrice,
                                 long mintRedeemId,
                                 BigInteger lastAum,
                                 long lastAumSlot,
                                 long inMarketCurrentWeight,
                                 long inMarketTargetWeight,
                                 PublicKey lpPool) implements DriftEvent {

  public static final int BYTES = 245;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int SLOT_OFFSET = 16;
  public static final int AUTHORITY_OFFSET = 24;
  public static final int DESCRIPTION_OFFSET = 56;
  public static final int AMOUNT_OFFSET = 57;
  public static final int FEE_OFFSET = 73;
  public static final int SPOT_MARKET_INDEX_OFFSET = 89;
  public static final int CONSTITUENT_INDEX_OFFSET = 91;
  public static final int ORACLE_PRICE_OFFSET = 93;
  public static final int MINT_OFFSET = 101;
  public static final int LP_AMOUNT_OFFSET = 133;
  public static final int LP_FEE_OFFSET = 141;
  public static final int LP_PRICE_OFFSET = 149;
  public static final int MINT_REDEEM_ID_OFFSET = 165;
  public static final int LAST_AUM_OFFSET = 173;
  public static final int LAST_AUM_SLOT_OFFSET = 189;
  public static final int IN_MARKET_CURRENT_WEIGHT_OFFSET = 197;
  public static final int IN_MARKET_TARGET_WEIGHT_OFFSET = 205;
  public static final int LP_POOL_OFFSET = 213;

  public static LPMintRedeemRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var description = _data[i] & 0xFF;
    ++i;
    final var amount = getInt128LE(_data, i);
    i += 16;
    final var fee = getInt128LE(_data, i);
    i += 16;
    final var spotMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var constituentIndex = getInt16LE(_data, i);
    i += 2;
    final var oraclePrice = getInt64LE(_data, i);
    i += 8;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var lpAmount = getInt64LE(_data, i);
    i += 8;
    final var lpFee = getInt64LE(_data, i);
    i += 8;
    final var lpPrice = getInt128LE(_data, i);
    i += 16;
    final var mintRedeemId = getInt64LE(_data, i);
    i += 8;
    final var lastAum = getInt128LE(_data, i);
    i += 16;
    final var lastAumSlot = getInt64LE(_data, i);
    i += 8;
    final var inMarketCurrentWeight = getInt64LE(_data, i);
    i += 8;
    final var inMarketTargetWeight = getInt64LE(_data, i);
    i += 8;
    final var lpPool = readPubKey(_data, i);
    return new LPMintRedeemRecord(discriminator,
                                  ts,
                                  slot,
                                  authority,
                                  description,
                                  amount,
                                  fee,
                                  spotMarketIndex,
                                  constituentIndex,
                                  oraclePrice,
                                  mint,
                                  lpAmount,
                                  lpFee,
                                  lpPrice,
                                  mintRedeemId,
                                  lastAum,
                                  lastAumSlot,
                                  inMarketCurrentWeight,
                                  inMarketTargetWeight,
                                  lpPool);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    putInt64LE(_data, i, slot);
    i += 8;
    authority.write(_data, i);
    i += 32;
    _data[i] = (byte) description;
    ++i;
    putInt128LE(_data, i, amount);
    i += 16;
    putInt128LE(_data, i, fee);
    i += 16;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt16LE(_data, i, constituentIndex);
    i += 2;
    putInt64LE(_data, i, oraclePrice);
    i += 8;
    mint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lpAmount);
    i += 8;
    putInt64LE(_data, i, lpFee);
    i += 8;
    putInt128LE(_data, i, lpPrice);
    i += 16;
    putInt64LE(_data, i, mintRedeemId);
    i += 8;
    putInt128LE(_data, i, lastAum);
    i += 16;
    putInt64LE(_data, i, lastAumSlot);
    i += 8;
    putInt64LE(_data, i, inMarketCurrentWeight);
    i += 8;
    putInt64LE(_data, i, inMarketTargetWeight);
    i += 8;
    lpPool.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
