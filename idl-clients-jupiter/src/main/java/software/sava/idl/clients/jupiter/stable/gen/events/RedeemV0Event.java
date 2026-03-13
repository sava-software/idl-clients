package software.sava.idl.clients.jupiter.stable.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RedeemV0Event(Discriminator discriminator,
                            long amount,
                            long netAmount,
                            long oraclePrice,
                            long oneToOneAmount,
                            long oracleAmount,
                            long redeemAmount) implements JupStableEvent {

  public static final int BYTES = 56;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(50, 202, 68, 30, 122, 77, 84, 153);

  public static final int AMOUNT_OFFSET = 8;
  public static final int NET_AMOUNT_OFFSET = 16;
  public static final int ORACLE_PRICE_OFFSET = 24;
  public static final int ONE_TO_ONE_AMOUNT_OFFSET = 32;
  public static final int ORACLE_AMOUNT_OFFSET = 40;
  public static final int REDEEM_AMOUNT_OFFSET = 48;

  public static RedeemV0Event read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var netAmount = getInt64LE(_data, i);
    i += 8;
    final var oraclePrice = getInt64LE(_data, i);
    i += 8;
    final var oneToOneAmount = getInt64LE(_data, i);
    i += 8;
    final var oracleAmount = getInt64LE(_data, i);
    i += 8;
    final var redeemAmount = getInt64LE(_data, i);
    return new RedeemV0Event(discriminator,
                             amount,
                             netAmount,
                             oraclePrice,
                             oneToOneAmount,
                             oracleAmount,
                             redeemAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, netAmount);
    i += 8;
    putInt64LE(_data, i, oraclePrice);
    i += 8;
    putInt64LE(_data, i, oneToOneAmount);
    i += 8;
    putInt64LE(_data, i, oracleAmount);
    i += 8;
    putInt64LE(_data, i, redeemAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
