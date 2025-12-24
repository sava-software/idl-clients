package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param authority The authority that can configure the program config: change the treasury, etc.
/// @param multisigCreationFee The fee that is charged for creating a new multisig.
/// @param treasury The treasury where the creation fee is transferred to.
public record ProgramConfigInitArgs(PublicKey authority,
                                    long multisigCreationFee,
                                    PublicKey treasury) implements SerDe {

  public static final int BYTES = 72;

  public static ProgramConfigInitArgs read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var multisigCreationFee = getInt64LE(_data, i);
    i += 8;
    final var treasury = readPubKey(_data, i);
    return new ProgramConfigInitArgs(authority, multisigCreationFee, treasury);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    authority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, multisigCreationFee);
    i += 8;
    treasury.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
