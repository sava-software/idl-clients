package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RequestedWithdrawalToDriftVault(Discriminator discriminator,
                                              long amount,
                                              PublicKey from,
                                              PublicKey to,
                                              long timestamp) implements NtbundleEvent {

  public static final int BYTES = 88;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(143, 146, 104, 155, 23, 243, 14, 231);

  public static final int AMOUNT_OFFSET = 8;
  public static final int FROM_OFFSET = 16;
  public static final int TO_OFFSET = 48;
  public static final int TIMESTAMP_OFFSET = 80;

  public static RequestedWithdrawalToDriftVault read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var from = readPubKey(_data, i);
    i += 32;
    final var to = readPubKey(_data, i);
    i += 32;
    final var timestamp = getInt64LE(_data, i);
    return new RequestedWithdrawalToDriftVault(discriminator,
                                               amount,
                                               from,
                                               to,
                                               timestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, amount);
    i += 8;
    from.write(_data, i);
    i += 32;
    to.write(_data, i);
    i += 32;
    putInt64LE(_data, i, timestamp);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
