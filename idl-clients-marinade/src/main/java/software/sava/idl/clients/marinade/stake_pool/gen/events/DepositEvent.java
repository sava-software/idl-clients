package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DepositEvent(Discriminator discriminator,
                           PublicKey state,
                           PublicKey solOwner,
                           long userSolBalance,
                           long userMsolBalance,
                           long solLegBalance,
                           long msolLegBalance,
                           long reserveBalance,
                           long solSwapped,
                           long msolSwapped,
                           long solDeposited,
                           long msolMinted,
                           long totalVirtualStakedLamports,
                           long msolSupply) implements MarinadeFinanceEvent {

  public static final int BYTES = 160;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static DepositEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var state = readPubKey(_data, i);
    i += 32;
    final var solOwner = readPubKey(_data, i);
    i += 32;
    final var userSolBalance = getInt64LE(_data, i);
    i += 8;
    final var userMsolBalance = getInt64LE(_data, i);
    i += 8;
    final var solLegBalance = getInt64LE(_data, i);
    i += 8;
    final var msolLegBalance = getInt64LE(_data, i);
    i += 8;
    final var reserveBalance = getInt64LE(_data, i);
    i += 8;
    final var solSwapped = getInt64LE(_data, i);
    i += 8;
    final var msolSwapped = getInt64LE(_data, i);
    i += 8;
    final var solDeposited = getInt64LE(_data, i);
    i += 8;
    final var msolMinted = getInt64LE(_data, i);
    i += 8;
    final var totalVirtualStakedLamports = getInt64LE(_data, i);
    i += 8;
    final var msolSupply = getInt64LE(_data, i);
    return new DepositEvent(discriminator,
                            state,
                            solOwner,
                            userSolBalance,
                            userMsolBalance,
                            solLegBalance,
                            msolLegBalance,
                            reserveBalance,
                            solSwapped,
                            msolSwapped,
                            solDeposited,
                            msolMinted,
                            totalVirtualStakedLamports,
                            msolSupply);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    state.write(_data, i);
    i += 32;
    solOwner.write(_data, i);
    i += 32;
    putInt64LE(_data, i, userSolBalance);
    i += 8;
    putInt64LE(_data, i, userMsolBalance);
    i += 8;
    putInt64LE(_data, i, solLegBalance);
    i += 8;
    putInt64LE(_data, i, msolLegBalance);
    i += 8;
    putInt64LE(_data, i, reserveBalance);
    i += 8;
    putInt64LE(_data, i, solSwapped);
    i += 8;
    putInt64LE(_data, i, msolSwapped);
    i += 8;
    putInt64LE(_data, i, solDeposited);
    i += 8;
    putInt64LE(_data, i, msolMinted);
    i += 8;
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
