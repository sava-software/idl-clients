package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum TokenStandard implements Borsh.Enum {

  NonFungible,
  FungibleAsset,
  Fungible,
  NonFungibleEdition,
  ProgrammableNonFungible,
  ProgrammableNonFungibleEdition;

  public static TokenStandard read(final byte[] _data, final int _offset) {
    return Borsh.read(TokenStandard.values(), _data, _offset);
  }
}