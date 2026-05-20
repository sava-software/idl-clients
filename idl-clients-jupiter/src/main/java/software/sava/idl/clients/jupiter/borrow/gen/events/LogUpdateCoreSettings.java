package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogUpdateCoreSettings(Discriminator discriminator,
                                    int supplyRateMagnifier,
                                    int borrowRateMagnifier,
                                    int collateralFactor,
                                    int liquidationThreshold,
                                    int liquidationMaxLimit,
                                    int withdrawGap,
                                    int liquidationPenalty,
                                    int borrowFee) implements VaultsEvent {

  public static final int BYTES = 24;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(233, 65, 32, 7, 230, 115, 122, 197);

  public static final int SUPPLY_RATE_MAGNIFIER_OFFSET = 8;
  public static final int BORROW_RATE_MAGNIFIER_OFFSET = 10;
  public static final int COLLATERAL_FACTOR_OFFSET = 12;
  public static final int LIQUIDATION_THRESHOLD_OFFSET = 14;
  public static final int LIQUIDATION_MAX_LIMIT_OFFSET = 16;
  public static final int WITHDRAW_GAP_OFFSET = 18;
  public static final int LIQUIDATION_PENALTY_OFFSET = 20;
  public static final int BORROW_FEE_OFFSET = 22;

  public static LogUpdateCoreSettings read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var supplyRateMagnifier = getInt16LE(_data, i);
    i += 2;
    final var borrowRateMagnifier = getInt16LE(_data, i);
    i += 2;
    final var collateralFactor = getInt16LE(_data, i);
    i += 2;
    final var liquidationThreshold = getInt16LE(_data, i);
    i += 2;
    final var liquidationMaxLimit = getInt16LE(_data, i);
    i += 2;
    final var withdrawGap = getInt16LE(_data, i);
    i += 2;
    final var liquidationPenalty = getInt16LE(_data, i);
    i += 2;
    final var borrowFee = getInt16LE(_data, i);
    return new LogUpdateCoreSettings(discriminator,
                                     supplyRateMagnifier,
                                     borrowRateMagnifier,
                                     collateralFactor,
                                     liquidationThreshold,
                                     liquidationMaxLimit,
                                     withdrawGap,
                                     liquidationPenalty,
                                     borrowFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt16LE(_data, i, supplyRateMagnifier);
    i += 2;
    putInt16LE(_data, i, borrowRateMagnifier);
    i += 2;
    putInt16LE(_data, i, collateralFactor);
    i += 2;
    putInt16LE(_data, i, liquidationThreshold);
    i += 2;
    putInt16LE(_data, i, liquidationMaxLimit);
    i += 2;
    putInt16LE(_data, i, withdrawGap);
    i += 2;
    putInt16LE(_data, i, liquidationPenalty);
    i += 2;
    putInt16LE(_data, i, borrowFee);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
