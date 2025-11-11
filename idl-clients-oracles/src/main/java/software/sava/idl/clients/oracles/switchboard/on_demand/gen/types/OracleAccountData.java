package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param enclave Represents the state of the quote verifiers enclave.
/// @param authority The authority of the EnclaveAccount which is permitted to make account changes.
/// @param queue Queue used for attestation to verify a MRENCLAVE measurement.
/// @param createdAt The unix timestamp when the quote was created.
/// @param lastHeartbeat The last time the quote heartbeated on-chain.
/// @param gatewayUri URI location of the verifier's gateway.
/// @param isOnQueue Whether the quote is located on the AttestationQueues buffer.
public record OracleAccountData(PublicKey _address,
                                Discriminator discriminator,
                                Quote enclave,
                                PublicKey authority,
                                PublicKey queue,
                                long createdAt,
                                long lastHeartbeat,
                                byte[] secpAuthority,
                                byte[] gatewayUri,
                                long permissions,
                                int isOnQueue,
                                byte[] padding1,
                                long lutSlot,
                                long lastRewardEpoch,
                                PublicKey operator,
                                byte[] ebuf3,
                                byte[] ebuf2,
                                byte[] ebuf1) implements Borsh {

  public static final int BYTES = 4816;
  public static final int SECP_AUTHORITY_LEN = 64;
  public static final int GATEWAY_URI_LEN = 64;
  public static final int PADDING_1_LEN = 7;
  public static final int EBUF_3_LEN = 16;
  public static final int EBUF_2_LEN = 64;
  public static final int EBUF_1_LEN = 1024;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(128, 30, 16, 241, 170, 73, 55, 54);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int ENCLAVE_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 3440;
  public static final int QUEUE_OFFSET = 3472;
  public static final int CREATED_AT_OFFSET = 3504;
  public static final int LAST_HEARTBEAT_OFFSET = 3512;
  public static final int SECP_AUTHORITY_OFFSET = 3520;
  public static final int GATEWAY_URI_OFFSET = 3584;
  public static final int PERMISSIONS_OFFSET = 3648;
  public static final int IS_ON_QUEUE_OFFSET = 3656;
  public static final int PADDING_1_OFFSET = 3657;
  public static final int LUT_SLOT_OFFSET = 3664;
  public static final int LAST_REWARD_EPOCH_OFFSET = 3672;
  public static final int OPERATOR_OFFSET = 3680;
  public static final int EBUF_3_OFFSET = 3712;
  public static final int EBUF_2_OFFSET = 3728;
  public static final int EBUF_1_OFFSET = 3792;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createQueueFilter(final PublicKey queue) {
    return Filter.createMemCompFilter(QUEUE_OFFSET, queue);
  }

  public static Filter createCreatedAtFilter(final long createdAt) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, createdAt);
    return Filter.createMemCompFilter(CREATED_AT_OFFSET, _data);
  }

  public static Filter createLastHeartbeatFilter(final long lastHeartbeat) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastHeartbeat);
    return Filter.createMemCompFilter(LAST_HEARTBEAT_OFFSET, _data);
  }

  public static Filter createPermissionsFilter(final long permissions) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, permissions);
    return Filter.createMemCompFilter(PERMISSIONS_OFFSET, _data);
  }

  public static Filter createIsOnQueueFilter(final int isOnQueue) {
    return Filter.createMemCompFilter(IS_ON_QUEUE_OFFSET, new byte[]{(byte) isOnQueue});
  }

  public static Filter createLutSlotFilter(final long lutSlot) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lutSlot);
    return Filter.createMemCompFilter(LUT_SLOT_OFFSET, _data);
  }

  public static Filter createLastRewardEpochFilter(final long lastRewardEpoch) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastRewardEpoch);
    return Filter.createMemCompFilter(LAST_REWARD_EPOCH_OFFSET, _data);
  }

  public static Filter createOperatorFilter(final PublicKey operator) {
    return Filter.createMemCompFilter(OPERATOR_OFFSET, operator);
  }

  public static OracleAccountData read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static OracleAccountData read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static OracleAccountData read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], OracleAccountData> FACTORY = OracleAccountData::read;

  public static OracleAccountData read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var enclave = Quote.read(_data, i);
    i += Borsh.len(enclave);
    final var authority = readPubKey(_data, i);
    i += 32;
    final var queue = readPubKey(_data, i);
    i += 32;
    final var createdAt = getInt64LE(_data, i);
    i += 8;
    final var lastHeartbeat = getInt64LE(_data, i);
    i += 8;
    final var secpAuthority = new byte[64];
    i += Borsh.readArray(secpAuthority, _data, i);
    final var gatewayUri = new byte[64];
    i += Borsh.readArray(gatewayUri, _data, i);
    final var permissions = getInt64LE(_data, i);
    i += 8;
    final var isOnQueue = _data[i] & 0xFF;
    ++i;
    final var padding1 = new byte[7];
    i += Borsh.readArray(padding1, _data, i);
    final var lutSlot = getInt64LE(_data, i);
    i += 8;
    final var lastRewardEpoch = getInt64LE(_data, i);
    i += 8;
    final var operator = readPubKey(_data, i);
    i += 32;
    final var ebuf3 = new byte[16];
    i += Borsh.readArray(ebuf3, _data, i);
    final var ebuf2 = new byte[64];
    i += Borsh.readArray(ebuf2, _data, i);
    final var ebuf1 = new byte[1024];
    Borsh.readArray(ebuf1, _data, i);
    return new OracleAccountData(_address,
                                 discriminator,
                                 enclave,
                                 authority,
                                 queue,
                                 createdAt,
                                 lastHeartbeat,
                                 secpAuthority,
                                 gatewayUri,
                                 permissions,
                                 isOnQueue,
                                 padding1,
                                 lutSlot,
                                 lastRewardEpoch,
                                 operator,
                                 ebuf3,
                                 ebuf2,
                                 ebuf1);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += Borsh.write(enclave, _data, i);
    authority.write(_data, i);
    i += 32;
    queue.write(_data, i);
    i += 32;
    putInt64LE(_data, i, createdAt);
    i += 8;
    putInt64LE(_data, i, lastHeartbeat);
    i += 8;
    i += Borsh.writeArrayChecked(secpAuthority, 64, _data, i);
    i += Borsh.writeArrayChecked(gatewayUri, 64, _data, i);
    putInt64LE(_data, i, permissions);
    i += 8;
    _data[i] = (byte) isOnQueue;
    ++i;
    i += Borsh.writeArrayChecked(padding1, 7, _data, i);
    putInt64LE(_data, i, lutSlot);
    i += 8;
    putInt64LE(_data, i, lastRewardEpoch);
    i += 8;
    operator.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(ebuf3, 16, _data, i);
    i += Borsh.writeArrayChecked(ebuf2, 64, _data, i);
    i += Borsh.writeArrayChecked(ebuf1, 1024, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
