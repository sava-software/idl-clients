package software.sava.idl.clients.marinade.stake_pool.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record AddLiquidityEvent(Discriminator discriminator,
                                PublicKey state,
                                PublicKey solOwner,
                                long userSolBalance,
                                long userLpBalance,
                                long solLegBalance,
                                long lpSupply,
                                long solAddedAmount,
                                long lpMinted,
                                long totalVirtualStakedLamports,
                                long msolSupply) implements MarinadeFinanceEvent {

  public static final int BYTES = 136;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(9, 100, 48, 232, 83, 169, 174, 85);

  public static AddLiquidityEvent read(final byte[] _data, final int _offset) {
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
    final var userLpBalance = getInt64LE(_data, i);
    i += 8;
    final var solLegBalance = getInt64LE(_data, i);
    i += 8;
    final var lpSupply = getInt64LE(_data, i);
    i += 8;
    final var solAddedAmount = getInt64LE(_data, i);
    i += 8;
    final var lpMinted = getInt64LE(_data, i);
    i += 8;
    final var totalVirtualStakedLamports = getInt64LE(_data, i);
    i += 8;
    final var msolSupply = getInt64LE(_data, i);
    return new AddLiquidityEvent(discriminator,
                                 state,
                                 solOwner,
                                 userSolBalance,
                                 userLpBalance,
                                 solLegBalance,
                                 lpSupply,
                                 solAddedAmount,
                                 lpMinted,
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
    putInt64LE(_data, i, userLpBalance);
    i += 8;
    putInt64LE(_data, i, solLegBalance);
    i += 8;
    putInt64LE(_data, i, lpSupply);
    i += 8;
    putInt64LE(_data, i, solAddedAmount);
    i += 8;
    putInt64LE(_data, i, lpMinted);
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
