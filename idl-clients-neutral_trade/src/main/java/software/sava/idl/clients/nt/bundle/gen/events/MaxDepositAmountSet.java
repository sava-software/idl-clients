package software.sava.idl.clients.nt.bundle.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MaxDepositAmountSet(Discriminator discriminator, long maxDepositAmount) implements NtbundleEvent {

  public static final int BYTES = 16;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(204, 243, 42, 160, 116, 144, 207, 38);

  public static final int MAX_DEPOSIT_AMOUNT_OFFSET = 8;

  public static MaxDepositAmountSet read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var maxDepositAmount = getInt64LE(_data, i);
    return new MaxDepositAmountSet(discriminator, maxDepositAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, maxDepositAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
