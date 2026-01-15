package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RandomnessCommitEvent(Discriminator discriminator,
                                    PublicKey randomnessAccount,
                                    PublicKey oracle,
                                    long slot,
                                    byte[] slothash) implements SbOnDemandEvent {

  public static final int BYTES = 112;
  public static final int SLOTHASH_LEN = 32;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(88, 60, 172, 90, 112, 10, 206, 147);

  public static final int RANDOMNESS_ACCOUNT_OFFSET = 8;
  public static final int ORACLE_OFFSET = 40;
  public static final int SLOT_OFFSET = 72;
  public static final int SLOTHASH_OFFSET = 80;

  public static RandomnessCommitEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var randomnessAccount = readPubKey(_data, i);
    i += 32;
    final var oracle = readPubKey(_data, i);
    i += 32;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var slothash = new byte[32];
    SerDeUtil.readArray(slothash, _data, i);
    return new RandomnessCommitEvent(discriminator,
                                     randomnessAccount,
                                     oracle,
                                     slot,
                                     slothash);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    randomnessAccount.write(_data, i);
    i += 32;
    oracle.write(_data, i);
    i += 32;
    putInt64LE(_data, i, slot);
    i += 8;
    i += SerDeUtil.writeArrayChecked(slothash, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
