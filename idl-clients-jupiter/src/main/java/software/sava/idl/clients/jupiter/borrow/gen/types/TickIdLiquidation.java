package software.sava.idl.clients.jupiter.borrow.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Tick ID liquidation data
///
public record TickIdLiquidation(PublicKey _address,
                                Discriminator discriminator,
                                int vaultId,
                                int tick,
                                int tickMap,
                                int isFullyLiquidated1,
                                int liquidationBranchId1,
                                long debtFactor1,
                                int isFullyLiquidated2,
                                int liquidationBranchId2,
                                long debtFactor2,
                                int isFullyLiquidated3,
                                int liquidationBranchId3,
                                long debtFactor3) implements SerDe {

  public static final int BYTES = 57;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(41, 28, 190, 197, 68, 213, 31, 181);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int VAULT_ID_OFFSET = 8;
  public static final int TICK_OFFSET = 10;
  public static final int TICK_MAP_OFFSET = 14;
  public static final int IS_FULLY_LIQUIDATED_1_OFFSET = 18;
  public static final int LIQUIDATION_BRANCH_ID_1_OFFSET = 19;
  public static final int DEBT_FACTOR_1_OFFSET = 23;
  public static final int IS_FULLY_LIQUIDATED_2_OFFSET = 31;
  public static final int LIQUIDATION_BRANCH_ID_2_OFFSET = 32;
  public static final int DEBT_FACTOR_2_OFFSET = 36;
  public static final int IS_FULLY_LIQUIDATED_3_OFFSET = 44;
  public static final int LIQUIDATION_BRANCH_ID_3_OFFSET = 45;
  public static final int DEBT_FACTOR_3_OFFSET = 49;

  public static Filter createVaultIdFilter(final int vaultId) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, vaultId);
    return Filter.createMemCompFilter(VAULT_ID_OFFSET, _data);
  }

  public static Filter createTickFilter(final int tick) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, tick);
    return Filter.createMemCompFilter(TICK_OFFSET, _data);
  }

  public static Filter createTickMapFilter(final int tickMap) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, tickMap);
    return Filter.createMemCompFilter(TICK_MAP_OFFSET, _data);
  }

  public static Filter createIsFullyLiquidated1Filter(final int isFullyLiquidated1) {
    return Filter.createMemCompFilter(IS_FULLY_LIQUIDATED_1_OFFSET, new byte[]{(byte) isFullyLiquidated1});
  }

  public static Filter createLiquidationBranchId1Filter(final int liquidationBranchId1) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, liquidationBranchId1);
    return Filter.createMemCompFilter(LIQUIDATION_BRANCH_ID_1_OFFSET, _data);
  }

  public static Filter createDebtFactor1Filter(final long debtFactor1) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, debtFactor1);
    return Filter.createMemCompFilter(DEBT_FACTOR_1_OFFSET, _data);
  }

  public static Filter createIsFullyLiquidated2Filter(final int isFullyLiquidated2) {
    return Filter.createMemCompFilter(IS_FULLY_LIQUIDATED_2_OFFSET, new byte[]{(byte) isFullyLiquidated2});
  }

  public static Filter createLiquidationBranchId2Filter(final int liquidationBranchId2) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, liquidationBranchId2);
    return Filter.createMemCompFilter(LIQUIDATION_BRANCH_ID_2_OFFSET, _data);
  }

  public static Filter createDebtFactor2Filter(final long debtFactor2) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, debtFactor2);
    return Filter.createMemCompFilter(DEBT_FACTOR_2_OFFSET, _data);
  }

  public static Filter createIsFullyLiquidated3Filter(final int isFullyLiquidated3) {
    return Filter.createMemCompFilter(IS_FULLY_LIQUIDATED_3_OFFSET, new byte[]{(byte) isFullyLiquidated3});
  }

  public static Filter createLiquidationBranchId3Filter(final int liquidationBranchId3) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, liquidationBranchId3);
    return Filter.createMemCompFilter(LIQUIDATION_BRANCH_ID_3_OFFSET, _data);
  }

  public static Filter createDebtFactor3Filter(final long debtFactor3) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, debtFactor3);
    return Filter.createMemCompFilter(DEBT_FACTOR_3_OFFSET, _data);
  }

  public static TickIdLiquidation read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TickIdLiquidation read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TickIdLiquidation read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TickIdLiquidation> FACTORY = TickIdLiquidation::read;

  public static TickIdLiquidation read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vaultId = getInt16LE(_data, i);
    i += 2;
    final var tick = getInt32LE(_data, i);
    i += 4;
    final var tickMap = getInt32LE(_data, i);
    i += 4;
    final var isFullyLiquidated1 = _data[i] & 0xFF;
    ++i;
    final var liquidationBranchId1 = getInt32LE(_data, i);
    i += 4;
    final var debtFactor1 = getInt64LE(_data, i);
    i += 8;
    final var isFullyLiquidated2 = _data[i] & 0xFF;
    ++i;
    final var liquidationBranchId2 = getInt32LE(_data, i);
    i += 4;
    final var debtFactor2 = getInt64LE(_data, i);
    i += 8;
    final var isFullyLiquidated3 = _data[i] & 0xFF;
    ++i;
    final var liquidationBranchId3 = getInt32LE(_data, i);
    i += 4;
    final var debtFactor3 = getInt64LE(_data, i);
    return new TickIdLiquidation(_address,
                                 discriminator,
                                 vaultId,
                                 tick,
                                 tickMap,
                                 isFullyLiquidated1,
                                 liquidationBranchId1,
                                 debtFactor1,
                                 isFullyLiquidated2,
                                 liquidationBranchId2,
                                 debtFactor2,
                                 isFullyLiquidated3,
                                 liquidationBranchId3,
                                 debtFactor3);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt16LE(_data, i, vaultId);
    i += 2;
    putInt32LE(_data, i, tick);
    i += 4;
    putInt32LE(_data, i, tickMap);
    i += 4;
    _data[i] = (byte) isFullyLiquidated1;
    ++i;
    putInt32LE(_data, i, liquidationBranchId1);
    i += 4;
    putInt64LE(_data, i, debtFactor1);
    i += 8;
    _data[i] = (byte) isFullyLiquidated2;
    ++i;
    putInt32LE(_data, i, liquidationBranchId2);
    i += 4;
    putInt64LE(_data, i, debtFactor2);
    i += 8;
    _data[i] = (byte) isFullyLiquidated3;
    ++i;
    putInt32LE(_data, i, liquidationBranchId3);
    i += 4;
    putInt64LE(_data, i, debtFactor3);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
