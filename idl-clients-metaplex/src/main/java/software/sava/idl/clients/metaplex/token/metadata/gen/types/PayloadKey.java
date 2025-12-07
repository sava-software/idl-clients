package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public enum PayloadKey implements Borsh.Enum {

  Amount,
  Authority,
  AuthoritySeeds,
  Delegate,
  DelegateSeeds,
  Destination,
  DestinationSeeds,
  Holder,
  Source,
  SourceSeeds;

  public static PayloadKey read(final byte[] _data, final int _offset) {
    return Borsh.read(PayloadKey.values(), _data, _offset);
  }
}