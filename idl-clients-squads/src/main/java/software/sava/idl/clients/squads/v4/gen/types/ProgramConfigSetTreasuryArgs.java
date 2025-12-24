package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record ProgramConfigSetTreasuryArgs(PublicKey newTreasury) implements SerDe {

  public static final int BYTES = 32;

  public static ProgramConfigSetTreasuryArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var newTreasury = readPubKey(_data, _offset);
    return new ProgramConfigSetTreasuryArgs(newTreasury);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    newTreasury.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
