package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record QueueRemoveMrEnclaveEvent(Discriminator discriminator, PublicKey queue, byte[] mrEnclave) implements SbOnDemandEvent {

  public static final int BYTES = 72;
  public static final int MR_ENCLAVE_LEN = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(4, 105, 196, 60, 84, 122, 203, 196);

  public static final int QUEUE_OFFSET = 8;
  public static final int MR_ENCLAVE_OFFSET = 40;

  public static QueueRemoveMrEnclaveEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var queue = readPubKey(_data, i);
    i += 32;
    final var mrEnclave = new byte[32];
    SerDeUtil.readArray(mrEnclave, _data, i);
    return new QueueRemoveMrEnclaveEvent(discriminator, queue, mrEnclave);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    queue.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(mrEnclave, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
