package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record OrderUnstakeEvent(Discriminator discriminator,
                                PublicKey state,
                                long ticketEpoch,
                                PublicKey ticket,
                                PublicKey beneficiary,
                                long circulatingTicketBalance,
                                long circulatingTicketCount,
                                long userMsolBalance,
                                long burnedMsolAmount,
                                long solAmount,
                                int feeBpCents,
                                long totalVirtualStakedLamports,
                                long msolSupply) implements MarinadeFinanceEvent {

  public static final int BYTES = 172;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static OrderUnstakeEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var ticketEpoch = getInt64LE(_data, i);
    i += 8;
    final var ticket = readPubKey(_data, i);
    i += 32;
    final var beneficiary = readPubKey(_data, i);
    i += 32;
    final var circulatingTicketBalance = getInt64LE(_data, i);
    i += 8;
    final var circulatingTicketCount = getInt64LE(_data, i);
    i += 8;
    final var userMsolBalance = getInt64LE(_data, i);
    i += 8;
    final var burnedMsolAmount = getInt64LE(_data, i);
    i += 8;
    final var solAmount = getInt64LE(_data, i);
    i += 8;
    final var feeBpCents = getInt32LE(_data, i);
    i += 4;
    final var totalVirtualStakedLamports = getInt64LE(_data, i);
    i += 8;
    final var msolSupply = getInt64LE(_data, i);
    return new OrderUnstakeEvent(discriminator,
                                 state,
                                 ticketEpoch,
                                 ticket,
                                 beneficiary,
                                 circulatingTicketBalance,
                                 circulatingTicketCount,
                                 userMsolBalance,
                                 burnedMsolAmount,
                                 solAmount,
                                 feeBpCents,
                                 totalVirtualStakedLamports,
                                 msolSupply);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    putInt64LE(_data, i, ticketEpoch);
    i += 8;
    ticket.write(_data, i);
    i += 32;
    beneficiary.write(_data, i);
    i += 32;
    putInt64LE(_data, i, circulatingTicketBalance);
    i += 8;
    putInt64LE(_data, i, circulatingTicketCount);
    i += 8;
    putInt64LE(_data, i, userMsolBalance);
    i += 8;
    putInt64LE(_data, i, burnedMsolAmount);
    i += 8;
    putInt64LE(_data, i, solAmount);
    i += 8;
    putInt32LE(_data, i, feeBpCents);
    i += 4;
    putInt64LE(_data, i, totalVirtualStakedLamports);
    i += 8;
    putInt64LE(_data, i, msolSupply);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
