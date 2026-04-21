package software.sava.idl.clients.loopscale.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record StakeEvent(Discriminator discriminator,
                         PublicKey user,
                         PublicKey address,
                         PublicKey vaultAddress,
                         PublicKey principalMint,
                         int durationType,
                         long amount,
                         int actionType,
                         long principalAmount) implements LoopscaleEvent {

  public static final int BYTES = 154;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(226, 134, 188, 173, 19, 33, 75, 175);

  public static final int USER_OFFSET = 8;
  public static final int ADDRESS_OFFSET = 40;
  public static final int VAULT_ADDRESS_OFFSET = 72;
  public static final int PRINCIPAL_MINT_OFFSET = 104;
  public static final int DURATION_TYPE_OFFSET = 136;
  public static final int AMOUNT_OFFSET = 137;
  public static final int ACTION_TYPE_OFFSET = 145;
  public static final int PRINCIPAL_AMOUNT_OFFSET = 146;

  public static StakeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var user = readPubKey(_data, i);
    i += 32;
    final var address = readPubKey(_data, i);
    i += 32;
    final var vaultAddress = readPubKey(_data, i);
    i += 32;
    final var principalMint = readPubKey(_data, i);
    i += 32;
    final var durationType = _data[i] & 0xFF;
    ++i;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var actionType = _data[i] & 0xFF;
    ++i;
    final var principalAmount = getInt64LE(_data, i);
    return new StakeEvent(discriminator,
                          user,
                          address,
                          vaultAddress,
                          principalMint,
                          durationType,
                          amount,
                          actionType,
                          principalAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    user.write(_data, i);
    i += 32;
    address.write(_data, i);
    i += 32;
    vaultAddress.write(_data, i);
    i += 32;
    principalMint.write(_data, i);
    i += 32;
    _data[i] = (byte) durationType;
    ++i;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) actionType;
    ++i;
    putInt64LE(_data, i, principalAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
