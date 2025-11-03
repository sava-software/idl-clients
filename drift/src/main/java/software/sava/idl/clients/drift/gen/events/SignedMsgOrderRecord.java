package software.sava.idl.clients.drift.gen.events;

import java.lang.String;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
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
                                    hash, hash.getBytes(UTF_8),
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
    final var hash = Borsh.string(_data, i);
    i += (Integer.BYTES + getInt32LE(_data, i));
    final var matchingOrderParams = OrderParams.read(_data, i);
    i += Borsh.len(matchingOrderParams);
    final var userOrderId = getInt32LE(_data, i);
    i += 4;
    final var signedMsgOrderMaxSlot = getInt64LE(_data, i);
    i += 8;
    final var signedMsgOrderUuid = new byte[8];
    i += Borsh.readArray(signedMsgOrderUuid, _data, i);
    final var ts = getInt64LE(_data, i);
    return new SignedMsgOrderRecord(discriminator,
                                    user,
                                    hash, hash.getBytes(UTF_8),
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
    i += Borsh.writeVector(_hash, _data, i);
    i += Borsh.write(matchingOrderParams, _data, i);
    putInt32LE(_data, i, userOrderId);
    i += 4;
    putInt64LE(_data, i, signedMsgOrderMaxSlot);
    i += 8;
    i += Borsh.writeArrayChecked(signedMsgOrderUuid, 8, _data, i);
    putInt64LE(_data, i, ts);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + Borsh.lenVector(_hash)
         + Borsh.len(matchingOrderParams)
         + 4
         + 8
         + Borsh.lenArray(signedMsgOrderUuid)
         + 8;
  }
}
