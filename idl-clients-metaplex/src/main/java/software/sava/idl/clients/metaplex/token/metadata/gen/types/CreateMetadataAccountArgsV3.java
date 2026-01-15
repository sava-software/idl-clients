package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record CreateMetadataAccountArgsV3(DataV2 data,
                                          boolean isMutable,
                                          CollectionDetails collectionDetails) implements SerDe {

  public static final int DATA_OFFSET = 0;

  public static CreateMetadataAccountArgsV3 read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var data = DataV2.read(_data, i);
    i += data.l();
    final var isMutable = _data[i] == 1;
    ++i;
    final CollectionDetails collectionDetails;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      collectionDetails = null;
    } else {
      ++i;
      collectionDetails = CollectionDetails.read(_data, i);
    }
    return new CreateMetadataAccountArgsV3(data, isMutable, collectionDetails);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += data.write(_data, i);
    _data[i] = (byte) (isMutable ? 1 : 0);
    ++i;
    i += SerDeUtil.writeOptional(1, collectionDetails, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return data.l() + 1 + (collectionDetails == null ? 1 : (1 + collectionDetails.l()));
  }
}
