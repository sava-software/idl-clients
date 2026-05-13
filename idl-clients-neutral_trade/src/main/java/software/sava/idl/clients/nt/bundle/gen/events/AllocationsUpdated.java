package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AllocationsUpdated(Discriminator discriminator, PublicKey receiverAddress, int allocationBps) implements NtbundleEvent {

  public static final int BYTES = 44;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(97, 171, 21, 101, 243, 48, 182, 32);

  public static final int RECEIVER_ADDRESS_OFFSET = 8;
  public static final int ALLOCATION_BPS_OFFSET = 40;

  public static AllocationsUpdated read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var receiverAddress = readPubKey(_data, i);
    i += 32;
    final var allocationBps = getInt32LE(_data, i);
    return new AllocationsUpdated(discriminator, receiverAddress, allocationBps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    receiverAddress.write(_data, i);
    i += 32;
    putInt32LE(_data, i, allocationBps);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
