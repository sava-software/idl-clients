package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.marinade.stake_pool.gen.types.InitializeData;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record InitializeEvent(Discriminator discriminator,
                              PublicKey state,
                              InitializeData params,
                              PublicKey stakeList,
                              PublicKey validatorList,
                              PublicKey msolMint,
                              PublicKey operationalSolAccount,
                              PublicKey lpMint,
                              PublicKey lpMsolLeg,
                              PublicKey treasuryMsolAccount) implements MarinadeFinanceEvent {

  public static final int BYTES = 408;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static InitializeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var params = InitializeData.read(_data, i);
    i += params.l();
    final var stakeList = readPubKey(_data, i);
    i += 32;
    final var validatorList = readPubKey(_data, i);
    i += 32;
    final var msolMint = readPubKey(_data, i);
    i += 32;
    final var operationalSolAccount = readPubKey(_data, i);
    i += 32;
    final var lpMint = readPubKey(_data, i);
    i += 32;
    final var lpMsolLeg = readPubKey(_data, i);
    i += 32;
    final var treasuryMsolAccount = readPubKey(_data, i);
    return new InitializeEvent(discriminator,
                               state,
                               params,
                               stakeList,
                               validatorList,
                               msolMint,
                               operationalSolAccount,
                               lpMint,
                               lpMsolLeg,
                               treasuryMsolAccount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    i += params.write(_data, i);
    stakeList.write(_data, i);
    i += 32;
    validatorList.write(_data, i);
    i += 32;
    msolMint.write(_data, i);
    i += 32;
    operationalSolAccount.write(_data, i);
    i += 32;
    lpMint.write(_data, i);
    i += 32;
    lpMsolLeg.write(_data, i);
    i += 32;
    treasuryMsolAccount.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
