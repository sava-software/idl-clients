package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// Address table lookups describe an on-chain address lookup table to use
/// for loading more readonly and writable accounts into a transaction.
///
/// @param accountKey Address lookup table account key.
/// @param writableIndexes List of indexes used to load writable accounts.
/// @param readonlyIndexes List of indexes used to load readonly accounts.
public record MultisigMessageAddressTableLookup(PublicKey accountKey,
                                                byte[] writableIndexes,
                                                byte[] readonlyIndexes) implements SerDe {

  public static MultisigMessageAddressTableLookup read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var accountKey = readPubKey(_data, i);
    i += 32;
    final var writableIndexes = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, writableIndexes);
    final var readonlyIndexes = SerDeUtil.readbyteVector(4, _data, i);
    return new MultisigMessageAddressTableLookup(accountKey, writableIndexes, readonlyIndexes);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    accountKey.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, writableIndexes, _data, i);
    i += SerDeUtil.writeVector(4, readonlyIndexes, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 32 + SerDeUtil.lenVector(4, writableIndexes) + SerDeUtil.lenVector(4, readonlyIndexes);
  }
}
