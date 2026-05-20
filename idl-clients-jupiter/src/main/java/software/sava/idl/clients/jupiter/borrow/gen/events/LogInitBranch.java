package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogInitBranch(Discriminator discriminator, PublicKey branch, int branchId) implements VaultsEvent {

  public static final int BYTES = 44;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(127, 182, 211, 219, 140, 189, 193, 101);

  public static final int BRANCH_OFFSET = 8;
  public static final int BRANCH_ID_OFFSET = 40;

  public static LogInitBranch read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var branch = readPubKey(_data, i);
    i += 32;
    final var branchId = getInt32LE(_data, i);
    return new LogInitBranch(discriminator, branch, branchId);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    branch.write(_data, i);
    i += 32;
    putInt32LE(_data, i, branchId);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
