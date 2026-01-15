package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// @param numSigners The number of signer pubkeys in the account_keys vec.
/// @param numWritableSigners The number of writable signer pubkeys in the account_keys vec.
/// @param numWritableNonSigners The number of writable non-signer pubkeys in the account_keys vec.
/// @param accountKeys Unique account pubkeys (including program IDs) required for execution of the tx.
///                    The signer pubkeys appear at the beginning of the vec, with writable pubkeys first, and read-only pubkeys following.
///                    The non-signer pubkeys follow with writable pubkeys first and read-only ones following.
///                    Program IDs are also stored at the end of the vec along with other non-signer non-writable pubkeys:
///                    
///                    ```plaintext
///                    pubkey1, pubkey2, pubkey3, pubkey4, pubkey5, pubkey6, pubkey7, pubkey8
///                    |---writable---|  |---readonly---|  |---writable---|  |---readonly---|
///                    |------------signers-------------|  |----------non-singers-----------|
///                    ```
/// @param instructions List of instructions making up the tx.
/// @param addressTableLookups List of address table lookups used to load additional accounts
///                            for this transaction.
public record VaultTransactionMessage(int numSigners,
                                      int numWritableSigners,
                                      int numWritableNonSigners,
                                      PublicKey[] accountKeys,
                                      MultisigCompiledInstruction[] instructions,
                                      MultisigMessageAddressTableLookup[] addressTableLookups) implements SerDe {

  public static final int NUM_SIGNERS_OFFSET = 0;
  public static final int NUM_WRITABLE_SIGNERS_OFFSET = 1;
  public static final int NUM_WRITABLE_NON_SIGNERS_OFFSET = 2;
  public static final int ACCOUNT_KEYS_OFFSET = 3;

  public static VaultTransactionMessage read(final byte[] _data, final int _offset) {
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
    final var accountKeys = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, accountKeys);
    final var instructions = SerDeUtil.readVector(4, MultisigCompiledInstruction.class, MultisigCompiledInstruction::read, _data, i);
    i += SerDeUtil.lenVector(4, instructions);
    final var addressTableLookups = SerDeUtil.readVector(4, MultisigMessageAddressTableLookup.class, MultisigMessageAddressTableLookup::read, _data, i);
    return new VaultTransactionMessage(numSigners,
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
    i += SerDeUtil.writeVector(4, accountKeys, _data, i);
    i += SerDeUtil.writeVector(4, instructions, _data, i);
    i += SerDeUtil.writeVector(4, addressTableLookups, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1
         + 1
         + 1
         + SerDeUtil.lenVector(4, accountKeys)
         + SerDeUtil.lenVector(4, instructions)
         + SerDeUtil.lenVector(4, addressTableLookups);
  }
}
