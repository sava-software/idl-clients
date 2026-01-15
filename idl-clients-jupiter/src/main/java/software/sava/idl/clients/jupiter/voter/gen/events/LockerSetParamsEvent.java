package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.jupiter.voter.gen.types.LockerParams;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LockerSetParamsEvent(Discriminator discriminator,
                                   PublicKey locker,
                                   LockerParams prevParams,
                                   LockerParams params) implements LockedVoterEvent {

  public static final int BYTES = 90;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(179, 231, 197, 195, 129, 224, 201, 14);

  public static final int LOCKER_OFFSET = 8;
  public static final int PREV_PARAMS_OFFSET = 40;
  public static final int PARAMS_OFFSET = 65;

  public static LockerSetParamsEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var locker = readPubKey(_data, i);
    i += 32;
    final var prevParams = LockerParams.read(_data, i);
    i += prevParams.l();
    final var params = LockerParams.read(_data, i);
    return new LockerSetParamsEvent(discriminator, locker, prevParams, params);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    locker.write(_data, i);
    i += 32;
    i += prevParams.write(_data, i);
    i += params.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
