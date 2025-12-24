package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum TokenStandard implements RustEnum {

  NonFungible,
  FungibleAsset,
  Fungible,
  NonFungibleEdition,
  ProgrammableNonFungible,
  ProgrammableNonFungibleEdition;

  public static TokenStandard read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, TokenStandard.values(), _data, _offset);
  }
}