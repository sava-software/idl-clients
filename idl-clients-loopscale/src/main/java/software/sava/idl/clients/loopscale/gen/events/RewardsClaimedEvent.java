package software.sava.idl.clients.loopscale.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record RewardsClaimedEvent(Discriminator discriminator,
                                  PublicKey vaultAddress,
                                  PublicKey userAddress,
                                  PublicKey stakeAccountAddress,
                                  long amount,
                                  PublicKey mint,
                                  long timestamp) implements LoopscaleEvent {

  public static final int BYTES = 152;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(22, 1, 42, 183, 250, 8, 157, 146);

  public static final int VAULT_ADDRESS_OFFSET = 8;
  public static final int USER_ADDRESS_OFFSET = 40;
  public static final int STAKE_ACCOUNT_ADDRESS_OFFSET = 72;
  public static final int AMOUNT_OFFSET = 104;
  public static final int MINT_OFFSET = 112;
  public static final int TIMESTAMP_OFFSET = 144;

  public static RewardsClaimedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vaultAddress = readPubKey(_data, i);
    i += 32;
    final var userAddress = readPubKey(_data, i);
    i += 32;
    final var stakeAccountAddress = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var timestamp = getInt64LE(_data, i);
    return new RewardsClaimedEvent(discriminator,
                                   vaultAddress,
                                   userAddress,
                                   stakeAccountAddress,
                                   amount,
                                   mint,
                                   timestamp);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    vaultAddress.write(_data, i);
    i += 32;
    userAddress.write(_data, i);
    i += 32;
    stakeAccountAddress.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    mint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, timestamp);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
