package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.OptionalLong;

import software.sava.core.borsh.Borsh;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record CreateMasterEditionArgs(OptionalLong maxSupply) implements Borsh {

  public static CreateMasterEditionArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final OptionalLong maxSupply;
    if (_data[_offset] == 0) {
      maxSupply = OptionalLong.empty();
    } else {
      maxSupply = OptionalLong.of(getInt64LE(_data, _offset + 1));
    }
    return new CreateMasterEditionArgs(maxSupply);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeOptional(maxSupply, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (maxSupply == null || maxSupply.isEmpty() ? 1 : (1 + 8));
  }
}
