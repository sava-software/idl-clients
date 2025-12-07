package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum Key implements Borsh.Enum {

  Uninitialized,
  EditionV1,
  MasterEditionV1,
  ReservationListV1,
  MetadataV1,
  ReservationListV2,
  MasterEditionV2,
  EditionMarker,
  UseAuthorityRecord,
  CollectionAuthorityRecord,
  TokenOwnedEscrow,
  TokenRecord,
  MetadataDelegate,
  EditionMarkerV2,
  HolderDelegate;

  public static Key read(final byte[] _data, final int _offset) {
    return Borsh.read(Key.values(), _data, _offset);
  }
}