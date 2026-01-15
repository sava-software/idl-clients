package software.sava.idl.clients.jupiter.lend_borrow.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogUpdateRates(Discriminator discriminator, long tokenExchangePrice, long liquidityExchangePrice) implements LendingEvent {

  public static final int BYTES = 24;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(222, 11, 113, 60, 147, 15, 68, 217);

  public static final int TOKEN_EXCHANGE_PRICE_OFFSET = 8;
  public static final int LIQUIDITY_EXCHANGE_PRICE_OFFSET = 16;

  public static LogUpdateRates read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var tokenExchangePrice = getInt64LE(_data, i);
    i += 8;
    final var liquidityExchangePrice = getInt64LE(_data, i);
    return new LogUpdateRates(discriminator, tokenExchangePrice, liquidityExchangePrice);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, tokenExchangePrice);
    i += 8;
    putInt64LE(_data, i, liquidityExchangePrice);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
