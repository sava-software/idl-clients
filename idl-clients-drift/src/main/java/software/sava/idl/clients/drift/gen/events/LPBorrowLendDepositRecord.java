package software.sava.idl.clients.drift.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.drift.gen.types.DepositDirection;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LPBorrowLendDepositRecord(Discriminator discriminator,
                                        long ts,
                                        long slot,
                                        int spotMarketIndex,
                                        int constituentIndex,
                                        DepositDirection direction,
                                        long tokenBalance,
                                        long lastTokenBalance,
                                        long interestAccruedTokenAmount,
                                        long amountDepositWithdraw,
                                        PublicKey lpPool) implements DriftEvent {

  public static final int BYTES = 93;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static LPBorrowLendDepositRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var spotMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var constituentIndex = getInt16LE(_data, i);
    i += 2;
    final var direction = DepositDirection.read(_data, i);
    i += direction.l();
    final var tokenBalance = getInt64LE(_data, i);
    i += 8;
    final var lastTokenBalance = getInt64LE(_data, i);
    i += 8;
    final var interestAccruedTokenAmount = getInt64LE(_data, i);
    i += 8;
    final var amountDepositWithdraw = getInt64LE(_data, i);
    i += 8;
    final var lpPool = readPubKey(_data, i);
    return new LPBorrowLendDepositRecord(discriminator,
                                         ts,
                                         slot,
                                         spotMarketIndex,
                                         constituentIndex,
                                         direction,
                                         tokenBalance,
                                         lastTokenBalance,
                                         interestAccruedTokenAmount,
                                         amountDepositWithdraw,
                                         lpPool);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    putInt64LE(_data, i, slot);
    i += 8;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt16LE(_data, i, constituentIndex);
    i += 2;
    i += direction.write(_data, i);
    putInt64LE(_data, i, tokenBalance);
    i += 8;
    putInt64LE(_data, i, lastTokenBalance);
    i += 8;
    putInt64LE(_data, i, interestAccruedTokenAmount);
    i += 8;
    putInt64LE(_data, i, amountDepositWithdraw);
    i += 8;
    lpPool.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
