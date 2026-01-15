package software.sava.idl.clients.drift.gen.events;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.drift.gen.types.OrderParams;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SignedMsgOrderRecord(Discriminator discriminator,
                                   PublicKey user,
                                   String hash, byte[] _hash,
                                   OrderParams matchingOrderParams,
                                   int userOrderId,
                                   long signedMsgOrderMaxSlot,
                                   byte[] signedMsgOrderUuid,
                                   long ts) implements DriftEvent {

  public static final int SIGNED_MSG_ORDER_UUID_LEN = 8;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int USER_OFFSET = 8;
  public static final int HASH_OFFSET = 40;

  public static SignedMsgOrderRecord createRecord(final Discriminator discriminator,
                                                  final PublicKey user,
                                                  final String hash,
                                                  final OrderParams matchingOrderParams,
                                                  final int userOrderId,
                                                  final long signedMsgOrderMaxSlot,
                                                  final byte[] signedMsgOrderUuid,
                                                  final long ts) {
    return new SignedMsgOrderRecord(discriminator,
                                    user,
                                    hash, hash == null ? null : hash.getBytes(UTF_8),
                                    matchingOrderParams,
                                    userOrderId,
                                    signedMsgOrderMaxSlot,
                                    signedMsgOrderUuid,
                                    ts);
  }

  public static SignedMsgOrderRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var user = readPubKey(_data, i);
    i += 32;
    final int _hashLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _hash = Arrays.copyOfRange(_data, i, i + _hashLength);
    final var hash = new String(_hash, UTF_8);
    i += _hash.length;
    final var matchingOrderParams = OrderParams.read(_data, i);
    i += matchingOrderParams.l();
    final var userOrderId = getInt32LE(_data, i);
    i += 4;
    final var signedMsgOrderMaxSlot = getInt64LE(_data, i);
    i += 8;
    final var signedMsgOrderUuid = new byte[8];
    i += SerDeUtil.readArray(signedMsgOrderUuid, _data, i);
    final var ts = getInt64LE(_data, i);
    return new SignedMsgOrderRecord(discriminator,
                                    user,
                                    hash, hash == null ? null : hash.getBytes(UTF_8),
                                    matchingOrderParams,
                                    userOrderId,
                                    signedMsgOrderMaxSlot,
                                    signedMsgOrderUuid,
                                    ts);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    user.write(_data, i);
    i += 32;
    i += SerDeUtil.writeVector(4, _hash, _data, i);
    i += matchingOrderParams.write(_data, i);
    putInt32LE(_data, i, userOrderId);
    i += 4;
    putInt64LE(_data, i, signedMsgOrderMaxSlot);
    i += 8;
    i += SerDeUtil.writeArrayChecked(signedMsgOrderUuid, 8, _data, i);
    putInt64LE(_data, i, ts);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + _hash.length
         + matchingOrderParams.l()
         + 4
         + 8
         + SerDeUtil.lenArray(signedMsgOrderUuid)
         + 8;
  }
}
