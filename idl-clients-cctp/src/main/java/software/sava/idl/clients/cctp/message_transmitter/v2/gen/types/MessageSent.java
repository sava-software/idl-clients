package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record MessageSent(PublicKey _address,
                          Discriminator discriminator,
                          PublicKey rentPayer,
                          long createdAt,
                          byte[] message) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(131, 100, 133, 56, 166, 225, 151, 60);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int RENT_PAYER_OFFSET = 8;
  public static final int CREATED_AT_OFFSET = 40;
  public static final int MESSAGE_OFFSET = 48;

  public static Filter createRentPayerFilter(final PublicKey rentPayer) {
    return Filter.createMemCompFilter(RENT_PAYER_OFFSET, rentPayer);
  }

  public static Filter createCreatedAtFilter(final long createdAt) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, createdAt);
    return Filter.createMemCompFilter(CREATED_AT_OFFSET, _data);
  }

  public static MessageSent read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static MessageSent read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static MessageSent read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], MessageSent> FACTORY = MessageSent::read;

  public static MessageSent read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var rentPayer = readPubKey(_data, i);
    i += 32;
    final var createdAt = getInt64LE(_data, i);
    i += 8;
    final var message = SerDeUtil.readbyteVector(4, _data, i);
    return new MessageSent(_address, discriminator, rentPayer, createdAt, message);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    rentPayer.write(_data, i);
    i += 32;
    putInt64LE(_data, i, createdAt);
    i += 8;
    i += SerDeUtil.writeVector(4, message, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + 8 + SerDeUtil.lenVector(4, message);
  }
}
