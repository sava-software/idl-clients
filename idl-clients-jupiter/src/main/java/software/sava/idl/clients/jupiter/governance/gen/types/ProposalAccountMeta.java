package software.sava.idl.clients.jupiter.governance.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// Account metadata used to define Instructions
///
/// @param pubkey An account's public key
/// @param isSigner True if an Instruction requires a Transaction signature matching `pubkey`.
/// @param isWritable True if the `pubkey` can be loaded as a read-write account.
public record ProposalAccountMeta(PublicKey pubkey,
                                  boolean isSigner,
                                  boolean isWritable) implements SerDe {

  public static final int BYTES = 34;

  public static final int PUBKEY_OFFSET = 0;
  public static final int IS_SIGNER_OFFSET = 32;
  public static final int IS_WRITABLE_OFFSET = 33;

  public static ProposalAccountMeta read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var pubkey = readPubKey(_data, i);
    i += 32;
    final var isSigner = _data[i] == 1;
    ++i;
    final var isWritable = _data[i] == 1;
    return new ProposalAccountMeta(pubkey, isSigner, isWritable);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    pubkey.write(_data, i);
    i += 32;
    _data[i] = (byte) (isSigner ? 1 : 0);
    ++i;
    _data[i] = (byte) (isWritable ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
