package software.sava.idl.clients.jupiter.borrow.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// User borrow position
///
public record UserBorrowPosition(PublicKey _address,
                                 Discriminator discriminator,
                                 PublicKey protocol,
                                 PublicKey mint,
                                 int withInterest,
                                 long amount,
                                 long debtCeiling,
                                 long lastUpdate,
                                 int expandPct,
                                 int expandDuration,
                                 long baseDebtCeiling,
                                 long maxDebtCeiling,
                                 int status) implements SerDe {

  public static final int BYTES = 120;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(73, 126, 65, 123, 220, 126, 197, 24);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int PROTOCOL_OFFSET = 8;
  public static final int MINT_OFFSET = 40;
  public static final int WITH_INTEREST_OFFSET = 72;
  public static final int AMOUNT_OFFSET = 73;
  public static final int DEBT_CEILING_OFFSET = 81;
  public static final int LAST_UPDATE_OFFSET = 89;
  public static final int EXPAND_PCT_OFFSET = 97;
  public static final int EXPAND_DURATION_OFFSET = 99;
  public static final int BASE_DEBT_CEILING_OFFSET = 103;
  public static final int MAX_DEBT_CEILING_OFFSET = 111;
  public static final int STATUS_OFFSET = 119;

  public static Filter createProtocolFilter(final PublicKey protocol) {
    return Filter.createMemCompFilter(PROTOCOL_OFFSET, protocol);
  }

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createWithInterestFilter(final int withInterest) {
    return Filter.createMemCompFilter(WITH_INTEREST_OFFSET, new byte[]{(byte) withInterest});
  }

  public static Filter createAmountFilter(final long amount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, amount);
    return Filter.createMemCompFilter(AMOUNT_OFFSET, _data);
  }

  public static Filter createDebtCeilingFilter(final long debtCeiling) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, debtCeiling);
    return Filter.createMemCompFilter(DEBT_CEILING_OFFSET, _data);
  }

  public static Filter createLastUpdateFilter(final long lastUpdate) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastUpdate);
    return Filter.createMemCompFilter(LAST_UPDATE_OFFSET, _data);
  }

  public static Filter createExpandPctFilter(final int expandPct) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, expandPct);
    return Filter.createMemCompFilter(EXPAND_PCT_OFFSET, _data);
  }

  public static Filter createExpandDurationFilter(final int expandDuration) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, expandDuration);
    return Filter.createMemCompFilter(EXPAND_DURATION_OFFSET, _data);
  }

  public static Filter createBaseDebtCeilingFilter(final long baseDebtCeiling) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, baseDebtCeiling);
    return Filter.createMemCompFilter(BASE_DEBT_CEILING_OFFSET, _data);
  }

  public static Filter createMaxDebtCeilingFilter(final long maxDebtCeiling) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, maxDebtCeiling);
    return Filter.createMemCompFilter(MAX_DEBT_CEILING_OFFSET, _data);
  }

  public static Filter createStatusFilter(final int status) {
    return Filter.createMemCompFilter(STATUS_OFFSET, new byte[]{(byte) status});
  }

  public static UserBorrowPosition read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static UserBorrowPosition read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static UserBorrowPosition read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], UserBorrowPosition> FACTORY = UserBorrowPosition::read;

  public static UserBorrowPosition read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var protocol = readPubKey(_data, i);
    i += 32;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var withInterest = _data[i] & 0xFF;
    ++i;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var debtCeiling = getInt64LE(_data, i);
    i += 8;
    final var lastUpdate = getInt64LE(_data, i);
    i += 8;
    final var expandPct = getInt16LE(_data, i);
    i += 2;
    final var expandDuration = getInt32LE(_data, i);
    i += 4;
    final var baseDebtCeiling = getInt64LE(_data, i);
    i += 8;
    final var maxDebtCeiling = getInt64LE(_data, i);
    i += 8;
    final var status = _data[i] & 0xFF;
    return new UserBorrowPosition(_address,
                                  discriminator,
                                  protocol,
                                  mint,
                                  withInterest,
                                  amount,
                                  debtCeiling,
                                  lastUpdate,
                                  expandPct,
                                  expandDuration,
                                  baseDebtCeiling,
                                  maxDebtCeiling,
                                  status);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    protocol.write(_data, i);
    i += 32;
    mint.write(_data, i);
    i += 32;
    _data[i] = (byte) withInterest;
    ++i;
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, debtCeiling);
    i += 8;
    putInt64LE(_data, i, lastUpdate);
    i += 8;
    putInt16LE(_data, i, expandPct);
    i += 2;
    putInt32LE(_data, i, expandDuration);
    i += 4;
    putInt64LE(_data, i, baseDebtCeiling);
    i += 8;
    putInt64LE(_data, i, maxDebtCeiling);
    i += 8;
    _data[i] = (byte) status;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
