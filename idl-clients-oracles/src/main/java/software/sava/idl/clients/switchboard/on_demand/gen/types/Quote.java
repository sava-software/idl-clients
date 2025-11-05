package software.sava.idl.clients.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param enclaveSigner The address of the signer generated within an enclave.
/// @param mrEnclave The quotes MRENCLAVE measurement dictating the contents of the secure enclave.
/// @param verificationStatus The VerificationStatus of the quote.
/// @param verificationTimestamp The unix timestamp when the quote was last verified.
/// @param validUntil The unix timestamp when the quotes verification status expires.
/// @param quoteRegistry The off-chain registry where the verifiers quote can be located.
/// @param registryKey Key to lookup the buffer data on IPFS or an alternative decentralized storage solution.
/// @param secp256k1Signer The secp256k1 public key of the enclave signer. Derived from the enclave_signer.
/// @param ebuf4 Reserved.
public record Quote(PublicKey enclaveSigner,
                    byte[] mrEnclave,
                    int verificationStatus,
                    byte[] padding1,
                    long verificationTimestamp,
                    long validUntil,
                    byte[] quoteRegistry,
                    byte[] registryKey,
                    byte[] secp256k1Signer,
                    PublicKey lastEd25519Signer,
                    byte[] lastSecp256k1Signer,
                    long lastRotateSlot,
                    PublicKey[] guardianApprovers,
                    int guardianApproversLen,
                    byte[] padding2,
                    PublicKey stagingEd25519Signer,
                    byte[] stagingSecp256k1Signer,
                    byte[] ethSigner,
                    byte[] ebuf4,
                    byte[] ebuf3,
                    byte[] ebuf2,
                    byte[] ebuf1) implements Borsh {

  public static final int BYTES = 3432;
  public static final int MR_ENCLAVE_LEN = 32;
  public static final int PADDING_1_LEN = 7;
  public static final int QUOTE_REGISTRY_LEN = 32;
  public static final int REGISTRY_KEY_LEN = 64;
  public static final int SECP_222K_1_SIGNER_LEN = 64;
  public static final int LAST_SECP_222K_1_SIGNER_LEN = 64;
  public static final int GUARDIAN_APPROVERS_LEN = 64;
  public static final int PADDING_2_LEN = 7;
  public static final int STAGING_SECP_222K_1_SIGNER_LEN = 64;
  public static final int ETH_SIGNER_LEN = 20;
  public static final int EBUF_4_LEN = 12;
  public static final int EBUF_3_LEN = 128;
  public static final int EBUF_2_LEN = 256;
  public static final int EBUF_1_LEN = 512;

  public static Quote read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var enclaveSigner = readPubKey(_data, i);
    i += 32;
    final var mrEnclave = new byte[32];
    i += Borsh.readArray(mrEnclave, _data, i);
    final var verificationStatus = _data[i] & 0xFF;
    ++i;
    final var padding1 = new byte[7];
    i += Borsh.readArray(padding1, _data, i);
    final var verificationTimestamp = getInt64LE(_data, i);
    i += 8;
    final var validUntil = getInt64LE(_data, i);
    i += 8;
    final var quoteRegistry = new byte[32];
    i += Borsh.readArray(quoteRegistry, _data, i);
    final var registryKey = new byte[64];
    i += Borsh.readArray(registryKey, _data, i);
    final var secp256k1Signer = new byte[64];
    i += Borsh.readArray(secp256k1Signer, _data, i);
    final var lastEd25519Signer = readPubKey(_data, i);
    i += 32;
    final var lastSecp256k1Signer = new byte[64];
    i += Borsh.readArray(lastSecp256k1Signer, _data, i);
    final var lastRotateSlot = getInt64LE(_data, i);
    i += 8;
    final var guardianApprovers = new PublicKey[64];
    i += Borsh.readArray(guardianApprovers, _data, i);
    final var guardianApproversLen = _data[i] & 0xFF;
    ++i;
    final var padding2 = new byte[7];
    i += Borsh.readArray(padding2, _data, i);
    final var stagingEd25519Signer = readPubKey(_data, i);
    i += 32;
    final var stagingSecp256k1Signer = new byte[64];
    i += Borsh.readArray(stagingSecp256k1Signer, _data, i);
    final var ethSigner = new byte[20];
    i += Borsh.readArray(ethSigner, _data, i);
    final var ebuf4 = new byte[12];
    i += Borsh.readArray(ebuf4, _data, i);
    final var ebuf3 = new byte[128];
    i += Borsh.readArray(ebuf3, _data, i);
    final var ebuf2 = new byte[256];
    i += Borsh.readArray(ebuf2, _data, i);
    final var ebuf1 = new byte[512];
    Borsh.readArray(ebuf1, _data, i);
    return new Quote(enclaveSigner,
                     mrEnclave,
                     verificationStatus,
                     padding1,
                     verificationTimestamp,
                     validUntil,
                     quoteRegistry,
                     registryKey,
                     secp256k1Signer,
                     lastEd25519Signer,
                     lastSecp256k1Signer,
                     lastRotateSlot,
                     guardianApprovers,
                     guardianApproversLen,
                     padding2,
                     stagingEd25519Signer,
                     stagingSecp256k1Signer,
                     ethSigner,
                     ebuf4,
                     ebuf3,
                     ebuf2,
                     ebuf1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    enclaveSigner.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(mrEnclave, 32, _data, i);
    _data[i] = (byte) verificationStatus;
    ++i;
    i += Borsh.writeArrayChecked(padding1, 7, _data, i);
    putInt64LE(_data, i, verificationTimestamp);
    i += 8;
    putInt64LE(_data, i, validUntil);
    i += 8;
    i += Borsh.writeArrayChecked(quoteRegistry, 32, _data, i);
    i += Borsh.writeArrayChecked(registryKey, 64, _data, i);
    i += Borsh.writeArrayChecked(secp256k1Signer, 64, _data, i);
    lastEd25519Signer.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(lastSecp256k1Signer, 64, _data, i);
    putInt64LE(_data, i, lastRotateSlot);
    i += 8;
    i += Borsh.writeArrayChecked(guardianApprovers, 64, _data, i);
    _data[i] = (byte) guardianApproversLen;
    ++i;
    i += Borsh.writeArrayChecked(padding2, 7, _data, i);
    stagingEd25519Signer.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(stagingSecp256k1Signer, 64, _data, i);
    i += Borsh.writeArrayChecked(ethSigner, 20, _data, i);
    i += Borsh.writeArrayChecked(ebuf4, 12, _data, i);
    i += Borsh.writeArrayChecked(ebuf3, 128, _data, i);
    i += Borsh.writeArrayChecked(ebuf2, 256, _data, i);
    i += Borsh.writeArrayChecked(ebuf1, 512, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
