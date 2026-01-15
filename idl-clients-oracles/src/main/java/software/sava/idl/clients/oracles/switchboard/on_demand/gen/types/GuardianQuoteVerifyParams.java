package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record GuardianQuoteVerifyParams(long timestamp,
                                        byte[] mrEnclave,
                                        int reserved1,
                                        PublicKey ed25519Key,
                                        byte[] secp256k1Key,
                                        long slot,
                                        byte[] signature,
                                        int recoveryId,
                                        int[] advisories) implements SerDe {

  public static final int MR_ENCLAVE_LEN = 32;
  public static final int SECP_222K_1_KEY_LEN = 64;
  public static final int SIGNATURE_LEN = 64;
  public static final int TIMESTAMP_OFFSET = 0;
  public static final int MR_ENCLAVE_OFFSET = 8;
  public static final int RESERVED_1_OFFSET = 40;
  public static final int ED_22222_KEY_OFFSET = 44;
  public static final int SECP_222K_1_KEY_OFFSET = 76;
  public static final int SLOT_OFFSET = 140;
  public static final int SIGNATURE_OFFSET = 148;
  public static final int RECOVERY_ID_OFFSET = 212;
  public static final int ADVISORIES_OFFSET = 213;

  public static GuardianQuoteVerifyParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var timestamp = getInt64LE(_data, i);
    i += 8;
    final var mrEnclave = new byte[32];
    i += SerDeUtil.readArray(mrEnclave, _data, i);
    final var reserved1 = getInt32LE(_data, i);
    i += 4;
    final var ed25519Key = readPubKey(_data, i);
    i += 32;
    final var secp256k1Key = new byte[64];
    i += SerDeUtil.readArray(secp256k1Key, _data, i);
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var signature = new byte[64];
    i += SerDeUtil.readArray(signature, _data, i);
    final var recoveryId = _data[i] & 0xFF;
    ++i;
    final var advisories = SerDeUtil.readintVector(4, _data, i);
    return new GuardianQuoteVerifyParams(timestamp,
                                         mrEnclave,
                                         reserved1,
                                         ed25519Key,
                                         secp256k1Key,
                                         slot,
                                         signature,
                                         recoveryId,
                                         advisories);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, timestamp);
    i += 8;
    i += SerDeUtil.writeArrayChecked(mrEnclave, 32, _data, i);
    putInt32LE(_data, i, reserved1);
    i += 4;
    ed25519Key.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(secp256k1Key, 64, _data, i);
    putInt64LE(_data, i, slot);
    i += 8;
    i += SerDeUtil.writeArrayChecked(signature, 64, _data, i);
    _data[i] = (byte) recoveryId;
    ++i;
    i += SerDeUtil.writeVector(4, advisories, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8
         + SerDeUtil.lenArray(mrEnclave)
         + 4
         + 32
         + SerDeUtil.lenArray(secp256k1Key)
         + 8
         + SerDeUtil.lenArray(signature)
         + 1
         + SerDeUtil.lenVector(4, advisories);
  }
}
