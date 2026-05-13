package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record StrategyRemoved(Discriminator discriminator, PublicKey receiverAddress) implements NtbundleEvent {

  public static final int BYTES = 40;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(118, 162, 92, 185, 73, 29, 245, 144);

  public static final int RECEIVER_ADDRESS_OFFSET = 8;

  public static StrategyRemoved read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var receiverAddress = readPubKey(_data, i);
    return new StrategyRemoved(discriminator, receiverAddress);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    receiverAddress.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
