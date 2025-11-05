package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Main state of the MessageTransmitter program
///
public record MessageTransmitter(PublicKey _address,
                                 Discriminator discriminator,
                                 PublicKey owner,
                                 PublicKey pendingOwner,
                                 PublicKey attesterManager,
                                 PublicKey pauser,
                                 boolean paused,
                                 int localDomain,
                                 int version,
                                 int signatureThreshold,
                                 PublicKey[] enabledAttesters,
                                 long maxMessageBodySize) implements Borsh {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(71, 40, 180, 142, 19, 203, 35, 252);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int OWNER_OFFSET = 8;
  public static final int PENDING_OWNER_OFFSET = 40;
  public static final int ATTESTER_MANAGER_OFFSET = 72;
  public static final int PAUSER_OFFSET = 104;
  public static final int PAUSED_OFFSET = 136;
  public static final int LOCAL_DOMAIN_OFFSET = 137;
  public static final int VERSION_OFFSET = 141;
  public static final int SIGNATURE_THRESHOLD_OFFSET = 145;
  public static final int ENABLED_ATTESTERS_OFFSET = 149;

  public static Filter createOwnerFilter(final PublicKey owner) {
    return Filter.createMemCompFilter(OWNER_OFFSET, owner);
  }

  public static Filter createPendingOwnerFilter(final PublicKey pendingOwner) {
    return Filter.createMemCompFilter(PENDING_OWNER_OFFSET, pendingOwner);
  }

  public static Filter createAttesterManagerFilter(final PublicKey attesterManager) {
    return Filter.createMemCompFilter(ATTESTER_MANAGER_OFFSET, attesterManager);
  }

  public static Filter createPauserFilter(final PublicKey pauser) {
    return Filter.createMemCompFilter(PAUSER_OFFSET, pauser);
  }

  public static Filter createPausedFilter(final boolean paused) {
    return Filter.createMemCompFilter(PAUSED_OFFSET, new byte[]{(byte) (paused ? 1 : 0)});
  }

  public static Filter createLocalDomainFilter(final int localDomain) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, localDomain);
    return Filter.createMemCompFilter(LOCAL_DOMAIN_OFFSET, _data);
  }

  public static Filter createVersionFilter(final int version) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, version);
    return Filter.createMemCompFilter(VERSION_OFFSET, _data);
  }

  public static Filter createSignatureThresholdFilter(final int signatureThreshold) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, signatureThreshold);
    return Filter.createMemCompFilter(SIGNATURE_THRESHOLD_OFFSET, _data);
  }

  public static MessageTransmitter read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static MessageTransmitter read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static MessageTransmitter read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], MessageTransmitter> FACTORY = MessageTransmitter::read;

  public static MessageTransmitter read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var owner = readPubKey(_data, i);
    i += 32;
    final var pendingOwner = readPubKey(_data, i);
    i += 32;
    final var attesterManager = readPubKey(_data, i);
    i += 32;
    final var pauser = readPubKey(_data, i);
    i += 32;
    final var paused = _data[i] == 1;
    ++i;
    final var localDomain = getInt32LE(_data, i);
    i += 4;
    final var version = getInt32LE(_data, i);
    i += 4;
    final var signatureThreshold = getInt32LE(_data, i);
    i += 4;
    final var enabledAttesters = Borsh.readPublicKeyVector(_data, i);
    i += Borsh.lenVector(enabledAttesters);
    final var maxMessageBodySize = getInt64LE(_data, i);
    return new MessageTransmitter(_address,
                                  discriminator,
                                  owner,
                                  pendingOwner,
                                  attesterManager,
                                  pauser,
                                  paused,
                                  localDomain,
                                  version,
                                  signatureThreshold,
                                  enabledAttesters,
                                  maxMessageBodySize);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    owner.write(_data, i);
    i += 32;
    pendingOwner.write(_data, i);
    i += 32;
    attesterManager.write(_data, i);
    i += 32;
    pauser.write(_data, i);
    i += 32;
    _data[i] = (byte) (paused ? 1 : 0);
    ++i;
    putInt32LE(_data, i, localDomain);
    i += 4;
    putInt32LE(_data, i, version);
    i += 4;
    putInt32LE(_data, i, signatureThreshold);
    i += 4;
    i += Borsh.writeVector(enabledAttesters, _data, i);
    putInt64LE(_data, i, maxMessageBodySize);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 32
         + 32
         + 1
         + 4
         + 4
         + 4
         + Borsh.lenVector(enabledAttesters)
         + 8;
  }
}
