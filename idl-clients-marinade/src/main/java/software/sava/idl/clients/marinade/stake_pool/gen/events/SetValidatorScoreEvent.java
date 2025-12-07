package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.marinade.stake_pool.gen.types.U32ValueChange;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record SetValidatorScoreEvent(Discriminator discriminator,
                                     PublicKey state,
                                     PublicKey validator,
                                     int index,
                                     U32ValueChange scoreChange) implements MarinadeFinanceEvent {

  public static final int BYTES = 84;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static SetValidatorScoreEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var validator = readPubKey(_data, i);
    i += 32;
    final var index = getInt32LE(_data, i);
    i += 4;
    final var scoreChange = U32ValueChange.read(_data, i);
    return new SetValidatorScoreEvent(discriminator,
                                      state,
                                      validator,
                                      index,
                                      scoreChange);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    validator.write(_data, i);
    i += 32;
    putInt32LE(_data, i, index);
    i += 4;
    i += scoreChange.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
