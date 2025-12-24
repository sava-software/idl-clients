package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// Unvalidated instruction data, must be treated as untrusted.
///
/// @param numSigners The number of signer pubkeys in the account_keys vec.
/// @param numWritableSigners The number of writable signer pubkeys in the account_keys vec.
/// @param numWritableNonSigners The number of writable non-signer pubkeys in the account_keys vec.
/// @param accountKeys The list of unique account public keys (including program IDs) that will be used in the provided instructions.
/// @param instructions The list of instructions to execute.
/// @param addressTableLookups List of address table lookups used to load additional accounts
///                            for this transaction.
public record TransactionMessage(int numSigners,
                                 int numWritableSigners,
                                 int numWritableNonSigners,
                                 PublicKey[] accountKeys,
                                 CompiledInstruction[] instructions,
                                 MessageAddressTableLookup[] addressTableLookups) implements SerDe {

  public static TransactionMessage read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var numSigners = _data[i] & 0xFF;
    ++i;
    final var numWritableSigners = _data[i] & 0xFF;
    ++i;
    final var numWritableNonSigners = _data[i] & 0xFF;
    ++i;
    final var accountKeys = SerDeUtil.readPublicKeyVector(1, _data, i);
    i += SerDeUtil.lenVector(1, accountKeys);
    final var instructions = SerDeUtil.readVector(1, CompiledInstruction.class, CompiledInstruction::read, _data, i);
    i += SerDeUtil.lenVector(1, instructions);
    final var addressTableLookups = SerDeUtil.readVector(1, MessageAddressTableLookup.class, MessageAddressTableLookup::read, _data, i);
    return new TransactionMessage(numSigners,
                                  numWritableSigners,
                                  numWritableNonSigners,
                                  accountKeys,
                                  instructions,
                                  addressTableLookups);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) numSigners;
    ++i;
    _data[i] = (byte) numWritableSigners;
    ++i;
    _data[i] = (byte) numWritableNonSigners;
    ++i;
    i += SerDeUtil.writeVector(1, accountKeys, _data, i);
    i += SerDeUtil.writeVector(1, instructions, _data, i);
    i += SerDeUtil.writeVector(1, addressTableLookups, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1
         + 1
         + 1
         + SerDeUtil.lenVector(1, accountKeys)
         + SerDeUtil.lenVector(1, instructions)
         + SerDeUtil.lenVector(1, addressTableLookups);
  }
}
