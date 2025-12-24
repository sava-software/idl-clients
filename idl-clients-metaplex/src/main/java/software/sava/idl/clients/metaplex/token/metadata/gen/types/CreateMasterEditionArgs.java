package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.OptionalLong;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record CreateMasterEditionArgs(OptionalLong maxSupply) implements SerDe {

  public static CreateMasterEditionArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final OptionalLong maxSupply;
    if (SerDeUtil.isAbsent(1, _data, _offset)) {
      maxSupply = OptionalLong.empty();
    } else {
      maxSupply = OptionalLong.of(getInt64LE(_data, _offset + 1));
    }
    return new CreateMasterEditionArgs(maxSupply);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, maxSupply, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (maxSupply == null || maxSupply.isEmpty() ? 1 : (1 + 8));
  }
}
