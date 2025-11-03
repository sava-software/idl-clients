package software.sava.idl.clients.switchboard.on_demand.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record QueueAddMrEnclaveEvent(Discriminator discriminator, PublicKey queue, byte[] mrEnclave) implements SbOnDemandEvent {

  public static final int BYTES = 72;
  public static final int MR_ENCLAVE_LEN = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(170, 186, 175, 38, 216, 51, 69, 175);

  public static QueueAddMrEnclaveEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var queue = readPubKey(_data, i);
    i += 32;
    final var mrEnclave = new byte[32];
    Borsh.readArray(mrEnclave, _data, i);
    return new QueueAddMrEnclaveEvent(discriminator, queue, mrEnclave);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    queue.write(_data, i);
    i += 32;
    i += Borsh.writeArrayChecked(mrEnclave, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
