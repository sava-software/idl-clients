package software.sava.idl.clients.jupiter.voter.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record IncreaseLockedAmountEvent(Discriminator discriminator,
                                        PublicKey locker,
                                        PublicKey escrowOwner,
                                        PublicKey tokenMint,
                                        long amount,
                                        long lockerSupply) implements LockedVoterEvent {

  public static final int BYTES = 120;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(179, 231, 197, 195, 129, 224, 201, 14);

  public static final int LOCKER_OFFSET = 8;
  public static final int ESCROW_OWNER_OFFSET = 40;
  public static final int TOKEN_MINT_OFFSET = 72;
  public static final int AMOUNT_OFFSET = 104;
  public static final int LOCKER_SUPPLY_OFFSET = 112;

  public static IncreaseLockedAmountEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var locker = readPubKey(_data, i);
    i += 32;
    final var escrowOwner = readPubKey(_data, i);
    i += 32;
    final var tokenMint = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var lockerSupply = getInt64LE(_data, i);
    return new IncreaseLockedAmountEvent(discriminator,
                                         locker,
                                         escrowOwner,
                                         tokenMint,
                                         amount,
                                         lockerSupply);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    locker.write(_data, i);
    i += 32;
    escrowOwner.write(_data, i);
    i += 32;
    tokenMint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, lockerSupply);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
