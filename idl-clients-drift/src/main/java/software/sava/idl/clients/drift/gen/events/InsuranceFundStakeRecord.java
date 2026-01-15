package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.drift.gen.types.StakeAction;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record InsuranceFundStakeRecord(Discriminator discriminator,
                                       long ts,
                                       PublicKey userAuthority,
                                       StakeAction action,
                                       long amount,
                                       int marketIndex,
                                       long insuranceVaultAmountBefore,
                                       BigInteger ifSharesBefore,
                                       BigInteger userIfSharesBefore,
                                       BigInteger totalIfSharesBefore,
                                       BigInteger ifSharesAfter,
                                       BigInteger userIfSharesAfter,
                                       BigInteger totalIfSharesAfter) implements DriftEvent {

  public static final int BYTES = 163;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int USER_AUTHORITY_OFFSET = 16;
  public static final int ACTION_OFFSET = 48;
  public static final int AMOUNT_OFFSET = 49;
  public static final int MARKET_INDEX_OFFSET = 57;
  public static final int INSURANCE_VAULT_AMOUNT_BEFORE_OFFSET = 59;
  public static final int IF_SHARES_BEFORE_OFFSET = 67;
  public static final int USER_IF_SHARES_BEFORE_OFFSET = 83;
  public static final int TOTAL_IF_SHARES_BEFORE_OFFSET = 99;
  public static final int IF_SHARES_AFTER_OFFSET = 115;
  public static final int USER_IF_SHARES_AFTER_OFFSET = 131;
  public static final int TOTAL_IF_SHARES_AFTER_OFFSET = 147;

  public static InsuranceFundStakeRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var userAuthority = readPubKey(_data, i);
    i += 32;
    final var action = StakeAction.read(_data, i);
    i += action.l();
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var marketIndex = getInt16LE(_data, i);
    i += 2;
    final var insuranceVaultAmountBefore = getInt64LE(_data, i);
    i += 8;
    final var ifSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var userIfSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var totalIfSharesBefore = getInt128LE(_data, i);
    i += 16;
    final var ifSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var userIfSharesAfter = getInt128LE(_data, i);
    i += 16;
    final var totalIfSharesAfter = getInt128LE(_data, i);
    return new InsuranceFundStakeRecord(discriminator,
                                        ts,
                                        userAuthority,
                                        action,
                                        amount,
                                        marketIndex,
                                        insuranceVaultAmountBefore,
                                        ifSharesBefore,
                                        userIfSharesBefore,
                                        totalIfSharesBefore,
                                        ifSharesAfter,
                                        userIfSharesAfter,
                                        totalIfSharesAfter);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    userAuthority.write(_data, i);
    i += 32;
    i += action.write(_data, i);
    putInt64LE(_data, i, amount);
    i += 8;
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, insuranceVaultAmountBefore);
    i += 8;
    putInt128LE(_data, i, ifSharesBefore);
    i += 16;
    putInt128LE(_data, i, userIfSharesBefore);
    i += 16;
    putInt128LE(_data, i, totalIfSharesBefore);
    i += 16;
    putInt128LE(_data, i, ifSharesAfter);
    i += 16;
    putInt128LE(_data, i, userIfSharesAfter);
    i += 16;
    putInt128LE(_data, i, totalIfSharesAfter);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
