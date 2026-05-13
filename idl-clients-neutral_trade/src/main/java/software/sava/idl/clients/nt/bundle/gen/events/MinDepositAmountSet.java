package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MinDepositAmountSet(Discriminator discriminator, long minDepositAmount, PublicKey bundleAccountKey) implements NtbundleEvent {

  public static final int BYTES = 48;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(143, 114, 126, 18, 32, 207, 22, 19);

  public static final int MIN_DEPOSIT_AMOUNT_OFFSET = 8;
  public static final int BUNDLE_ACCOUNT_KEY_OFFSET = 16;

  public static MinDepositAmountSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var minDepositAmount = getInt64LE(_data, i);
    i += 8;
    final var bundleAccountKey = readPubKey(_data, i);
    return new MinDepositAmountSet(discriminator, minDepositAmount, bundleAccountKey);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, minDepositAmount);
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
