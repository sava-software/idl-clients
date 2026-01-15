package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AddValidatorEvent(Discriminator discriminator,
                                PublicKey state,
                                PublicKey validator,
                                int index,
                                int score) implements MarinadeFinanceEvent {

  public static final int BYTES = 80;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static final int STATE_OFFSET = 8;
  public static final int VALIDATOR_OFFSET = 40;
  public static final int INDEX_OFFSET = 72;
  public static final int SCORE_OFFSET = 76;

  public static AddValidatorEvent read(final byte[] _data, final int _offset) {
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
    final var score = getInt32LE(_data, i);
    return new AddValidatorEvent(discriminator,
                                 state,
                                 validator,
                                 index,
                                 score);
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
    putInt32LE(_data, i, score);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
