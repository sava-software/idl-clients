package software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record SendMessageParams(int destinationDomain,
                                PublicKey recipient,
                                PublicKey destinationCaller,
                                int minFinalityThreshold,
                                byte[] messageBody) implements SerDe {

  public static SendMessageParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var destinationDomain = getInt32LE(_data, i);
    i += 4;
    final var recipient = readPubKey(_data, i);
    i += 32;
    final var destinationCaller = readPubKey(_data, i);
    i += 32;
    final var minFinalityThreshold = getInt32LE(_data, i);
    i += 4;
    final var messageBody = SerDeUtil.readbyteVector(4, _data, i);
    return new SendMessageParams(destinationDomain,
                                 recipient,
                                 destinationCaller,
                                 minFinalityThreshold,
                                 messageBody);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt32LE(_data, i, destinationDomain);
    i += 4;
    recipient.write(_data, i);
    i += 32;
    destinationCaller.write(_data, i);
    i += 32;
    putInt32LE(_data, i, minFinalityThreshold);
    i += 4;
    i += SerDeUtil.writeVector(4, messageBody, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 4
         + 32
         + 32
         + 4
         + SerDeUtil.lenVector(4, messageBody);
  }
}
