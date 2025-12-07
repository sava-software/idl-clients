package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ClaimEvent(Discriminator discriminator,
                         PublicKey state,
                         long epoch,
                         PublicKey ticket,
                         PublicKey beneficiary,
                         long circulatingTicketBalance,
                         long circulatingTicketCount,
                         long reserveBalance,
                         long userBalance,
                         long amount) implements MarinadeFinanceEvent {

  public static final int BYTES = 152;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static ClaimEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var epoch = getInt64LE(_data, i);
    i += 8;
    final var ticket = readPubKey(_data, i);
    i += 32;
    final var beneficiary = readPubKey(_data, i);
    i += 32;
    final var circulatingTicketBalance = getInt64LE(_data, i);
    i += 8;
    final var circulatingTicketCount = getInt64LE(_data, i);
    i += 8;
    final var reserveBalance = getInt64LE(_data, i);
    i += 8;
    final var userBalance = getInt64LE(_data, i);
    i += 8;
    final var amount = getInt64LE(_data, i);
    return new ClaimEvent(discriminator,
                          state,
                          epoch,
                          ticket,
                          beneficiary,
                          circulatingTicketBalance,
                          circulatingTicketCount,
                          reserveBalance,
                          userBalance,
                          amount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    putInt64LE(_data, i, epoch);
    i += 8;
    ticket.write(_data, i);
    i += 32;
    beneficiary.write(_data, i);
    i += 32;
    putInt64LE(_data, i, circulatingTicketBalance);
    i += 8;
    putInt64LE(_data, i, circulatingTicketCount);
    i += 8;
    putInt64LE(_data, i, reserveBalance);
    i += 8;
    putInt64LE(_data, i, userBalance);
    i += 8;
    putInt64LE(_data, i, amount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
