package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.lang.Boolean;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record UpdateMetadataAccountArgsV2(DataV2 data,
                                          PublicKey updateAuthority,
                                          Boolean primarySaleHappened,
                                          Boolean isMutable) implements SerDe {

  public static UpdateMetadataAccountArgsV2 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final DataV2 data;
    if (_data[i] == 0) {
      data = null;
      ++i;
    } else {
      ++i;
      data = DataV2.read(_data, i);
      i += data.l();
    }
    final PublicKey updateAuthority;
    if (_data[i] == 0) {
      updateAuthority = null;
      ++i;
    } else {
      ++i;
      updateAuthority = readPubKey(_data, i);
      i += 32;
    }
    final Boolean primarySaleHappened;
    if (_data[i] == 0) {
      primarySaleHappened = null;
      ++i;
    } else {
      ++i;
      primarySaleHappened = _data[i] == 1;
      ++i;
    }
    final Boolean isMutable;
    if (_data[i] == 0) {
      isMutable = null;
    } else {
      ++i;
      isMutable = _data[i] == 1;
    }
    return new UpdateMetadataAccountArgsV2(data,
                                           updateAuthority,
                                           primarySaleHappened,
                                           isMutable);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeOptional(1, data, _data, i);
    i += SerDeUtil.writeOptional(1, updateAuthority, _data, i);
    i += SerDeUtil.writeOptional(1, primarySaleHappened, _data, i);
    i += SerDeUtil.writeOptional(1, isMutable, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (data == null ? 1 : (1 + data.l())) + (updateAuthority == null ? 1 : (1 + 32)) + (primarySaleHappened == null ? 1 : (1 + 1)) + (isMutable == null ? 1 : (1 + 1));
  }
}
