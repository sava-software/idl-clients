package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

public enum PayloadKey implements RustEnum {

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
    return SerDeUtil.read(1, PayloadKey.values(), _data, _offset);
  }
}