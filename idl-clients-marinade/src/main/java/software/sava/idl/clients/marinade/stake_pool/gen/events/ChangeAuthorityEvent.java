package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.marinade.stake_pool.gen.types.PubkeyValueChange;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ChangeAuthorityEvent(Discriminator discriminator,
                                   PublicKey state,
                                   PubkeyValueChange adminChange,
                                   PubkeyValueChange validatorManagerChange,
                                   PubkeyValueChange operationalSolAccountChange,
                                   PubkeyValueChange treasuryMsolAccountChange,
                                   PubkeyValueChange pauseAuthorityChange) implements MarinadeFinanceEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static ChangeAuthorityEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final PubkeyValueChange adminChange;
    if (_data[i] == 0) {
      adminChange = null;
      ++i;
    } else {
      ++i;
      adminChange = PubkeyValueChange.read(_data, i);
      i += adminChange.l();
    }
    final PubkeyValueChange validatorManagerChange;
    if (_data[i] == 0) {
      validatorManagerChange = null;
      ++i;
    } else {
      ++i;
      validatorManagerChange = PubkeyValueChange.read(_data, i);
      i += validatorManagerChange.l();
    }
    final PubkeyValueChange operationalSolAccountChange;
    if (_data[i] == 0) {
      operationalSolAccountChange = null;
      ++i;
    } else {
      ++i;
      operationalSolAccountChange = PubkeyValueChange.read(_data, i);
      i += operationalSolAccountChange.l();
    }
    final PubkeyValueChange treasuryMsolAccountChange;
    if (_data[i] == 0) {
      treasuryMsolAccountChange = null;
      ++i;
    } else {
      ++i;
      treasuryMsolAccountChange = PubkeyValueChange.read(_data, i);
      i += treasuryMsolAccountChange.l();
    }
    final PubkeyValueChange pauseAuthorityChange;
    if (_data[i] == 0) {
      pauseAuthorityChange = null;
    } else {
      ++i;
      pauseAuthorityChange = PubkeyValueChange.read(_data, i);
    }
    return new ChangeAuthorityEvent(discriminator,
                                    state,
                                    adminChange,
                                    validatorManagerChange,
                                    operationalSolAccountChange,
                                    treasuryMsolAccountChange,
                                    pauseAuthorityChange);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    i += SerDeUtil.writeOptional(1, adminChange, _data, i);
    i += SerDeUtil.writeOptional(1, validatorManagerChange, _data, i);
    i += SerDeUtil.writeOptional(1, operationalSolAccountChange, _data, i);
    i += SerDeUtil.writeOptional(1, treasuryMsolAccountChange, _data, i);
    i += SerDeUtil.writeOptional(1, pauseAuthorityChange, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 32
         + (adminChange == null ? 1 : (1 + adminChange.l()))
         + (validatorManagerChange == null ? 1 : (1 + validatorManagerChange.l()))
         + (operationalSolAccountChange == null ? 1 : (1 + operationalSolAccountChange.l()))
         + (treasuryMsolAccountChange == null ? 1 : (1 + treasuryMsolAccountChange.l()))
         + (pauseAuthorityChange == null ? 1 : (1 + pauseAuthorityChange.l()));
  }
}
