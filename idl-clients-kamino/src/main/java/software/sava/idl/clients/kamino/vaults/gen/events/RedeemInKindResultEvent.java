package software.sava.idl.clients.kamino.vaults.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RedeemInKindResultEvent(Discriminator discriminator, long sharesToBurn, long ctokensToSendToUser) implements KaminoVaultEvent {

  public static final int BYTES = 24;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(149, 244, 122, 223, 193, 62, 199, 31);

  public static final int SHARES_TO_BURN_OFFSET = 8;
  public static final int CTOKENS_TO_SEND_TO_USER_OFFSET = 16;

  public static RedeemInKindResultEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var sharesToBurn = getInt64LE(_data, i);
    i += 8;
    final var ctokensToSendToUser = getInt64LE(_data, i);
    return new RedeemInKindResultEvent(discriminator, sharesToBurn, ctokensToSendToUser);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, sharesToBurn);
    i += 8;
    putInt64LE(_data, i, ctokensToSendToUser);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
