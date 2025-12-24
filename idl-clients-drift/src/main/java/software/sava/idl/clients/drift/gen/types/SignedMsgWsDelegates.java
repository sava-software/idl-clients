package software.sava.idl.clients.drift.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// * Used to store authenticated delegates for swift-like ws connections
///
public record SignedMsgWsDelegates(PublicKey _address, Discriminator discriminator, PublicKey[] delegates) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(190, 115, 111, 44, 216, 252, 108, 85);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int DELEGATES_OFFSET = 8;

  public static SignedMsgWsDelegates read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static SignedMsgWsDelegates read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static SignedMsgWsDelegates read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], SignedMsgWsDelegates> FACTORY = SignedMsgWsDelegates::read;

  public static SignedMsgWsDelegates read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var delegates = SerDeUtil.readPublicKeyVector(4, _data, i);
    return new SignedMsgWsDelegates(_address, discriminator, delegates);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeVector(4, delegates, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + SerDeUtil.lenVector(4, delegates);
  }
}
