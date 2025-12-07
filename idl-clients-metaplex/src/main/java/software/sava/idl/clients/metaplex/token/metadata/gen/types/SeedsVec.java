package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.borsh.Borsh;

public record SeedsVec(byte[][] seeds) implements Borsh {

  public static SeedsVec read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var seeds = Borsh.readMultiDimensionbyteVector(_data, _offset);
    return new SeedsVec(seeds);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(seeds, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return Borsh.lenVector(seeds);
  }
}
