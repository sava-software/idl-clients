package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum RevokeArgs implements Borsh.Enum {

  CollectionV1,
  SaleV1,
  TransferV1,
  DataV1,
  UtilityV1,
  StakingV1,
  StandardV1,
  LockedTransferV1,
  ProgrammableConfigV1,
  MigrationV1,
  AuthorityItemV1,
  DataItemV1,
  CollectionItemV1,
  ProgrammableConfigItemV1,
  PrintDelegateV1;

  public static RevokeArgs read(final byte[] _data, final int _offset) {
    return Borsh.read(RevokeArgs.values(), _data, _offset);
  }
}