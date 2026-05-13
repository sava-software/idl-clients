package software.sava.idl.clients.nt.bundle.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DustSharesBurned(Discriminator discriminator,
                               BigInteger amount,
                               long timestamp,
                               PublicKey bundleAccountKey) implements NtbundleEvent {

  public static final int BYTES = 64;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(166, 218, 110, 236, 33, 135, 162, 139);

  public static final int AMOUNT_OFFSET = 8;
  public static final int TIMESTAMP_OFFSET = 24;
  public static final int BUNDLE_ACCOUNT_KEY_OFFSET = 32;

  public static DustSharesBurned read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var amount = getInt128LE(_data, i);
    i += 16;
    final var timestamp = getInt64LE(_data, i);
    i += 8;
    final var bundleAccountKey = readPubKey(_data, i);
    return new DustSharesBurned(discriminator, amount, timestamp, bundleAccountKey);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt128LE(_data, i, amount);
    i += 16;
    putInt64LE(_data, i, timestamp);
    i += 8;
    bundleAccountKey.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
