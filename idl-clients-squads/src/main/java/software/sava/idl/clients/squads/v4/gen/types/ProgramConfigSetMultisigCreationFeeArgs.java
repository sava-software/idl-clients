package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ProgramConfigSetMultisigCreationFeeArgs(long newMultisigCreationFee) implements SerDe {

  public static final int BYTES = 8;

  public static final int NEW_MULTISIG_CREATION_FEE_OFFSET = 0;

  public static ProgramConfigSetMultisigCreationFeeArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var newMultisigCreationFee = getInt64LE(_data, _offset);
    return new ProgramConfigSetMultisigCreationFeeArgs(newMultisigCreationFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, newMultisigCreationFee);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
