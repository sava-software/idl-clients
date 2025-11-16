package software.sava.idl.clients.jupiter.governance.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.jupiter.governance.gen.types.GovernanceParameters;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record GovernorSetParamsEvent(Discriminator discriminator,
                                     PublicKey governor,
                                     GovernanceParameters prevParams,
                                     GovernanceParameters params) implements GovernEvent {

  public static final int BYTES = 104;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(234, 121, 246, 143, 42, 244, 8, 229);

  public static GovernorSetParamsEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var governor = readPubKey(_data, i);
    i += 32;
    final var prevParams = GovernanceParameters.read(_data, i);
    i += prevParams.l();
    final var params = GovernanceParameters.read(_data, i);
    return new GovernorSetParamsEvent(discriminator, governor, prevParams, params);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    governor.write(_data, i);
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
