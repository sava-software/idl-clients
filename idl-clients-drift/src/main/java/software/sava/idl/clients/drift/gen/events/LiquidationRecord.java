package software.sava.idl.clients.drift.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.drift.gen.types.LiquidateBorrowForPerpPnlRecord;
import software.sava.idl.clients.drift.gen.types.LiquidatePerpPnlForDepositRecord;
import software.sava.idl.clients.drift.gen.types.LiquidatePerpRecord;
import software.sava.idl.clients.drift.gen.types.LiquidateSpotRecord;
import software.sava.idl.clients.drift.gen.types.LiquidationType;
import software.sava.idl.clients.drift.gen.types.PerpBankruptcyRecord;
import software.sava.idl.clients.drift.gen.types.SpotBankruptcyRecord;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LiquidationRecord(Discriminator discriminator,
                                long ts,
                                LiquidationType liquidationType,
                                PublicKey user,
                                PublicKey liquidator,
                                BigInteger marginRequirement,
                                BigInteger totalCollateral,
                                long marginFreed,
                                int liquidationId,
                                boolean bankrupt,
                                int[] canceledOrderIds,
                                LiquidatePerpRecord liquidatePerp,
                                LiquidateSpotRecord liquidateSpot,
                                LiquidateBorrowForPerpPnlRecord liquidateBorrowForPerpPnl,
                                LiquidatePerpPnlForDepositRecord liquidatePerpPnlForDeposit,
                                PerpBankruptcyRecord perpBankruptcy,
                                SpotBankruptcyRecord spotBankruptcy) implements DriftEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int LIQUIDATION_TYPE_OFFSET = 16;
  public static final int USER_OFFSET = 17;
  public static final int LIQUIDATOR_OFFSET = 49;
  public static final int MARGIN_REQUIREMENT_OFFSET = 81;
  public static final int TOTAL_COLLATERAL_OFFSET = 97;
  public static final int MARGIN_FREED_OFFSET = 113;
  public static final int LIQUIDATION_ID_OFFSET = 121;
  public static final int BANKRUPT_OFFSET = 123;
  public static final int CANCELED_ORDER_IDS_OFFSET = 124;

  public static LiquidationRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var liquidationType = LiquidationType.read(_data, i);
    i += liquidationType.l();
    final var user = readPubKey(_data, i);
    i += 32;
    final var liquidator = readPubKey(_data, i);
    i += 32;
    final var marginRequirement = getInt128LE(_data, i);
    i += 16;
    final var totalCollateral = getInt128LE(_data, i);
    i += 16;
    final var marginFreed = getInt64LE(_data, i);
    i += 8;
    final var liquidationId = getInt16LE(_data, i);
    i += 2;
    final var bankrupt = _data[i] == 1;
    ++i;
    final var canceledOrderIds = SerDeUtil.readintVector(4, _data, i);
    i += SerDeUtil.lenVector(4, canceledOrderIds);
    final var liquidatePerp = LiquidatePerpRecord.read(_data, i);
    i += liquidatePerp.l();
    final var liquidateSpot = LiquidateSpotRecord.read(_data, i);
    i += liquidateSpot.l();
    final var liquidateBorrowForPerpPnl = LiquidateBorrowForPerpPnlRecord.read(_data, i);
    i += liquidateBorrowForPerpPnl.l();
    final var liquidatePerpPnlForDeposit = LiquidatePerpPnlForDepositRecord.read(_data, i);
    i += liquidatePerpPnlForDeposit.l();
    final var perpBankruptcy = PerpBankruptcyRecord.read(_data, i);
    i += perpBankruptcy.l();
    final var spotBankruptcy = SpotBankruptcyRecord.read(_data, i);
    return new LiquidationRecord(discriminator,
                                 ts,
                                 liquidationType,
                                 user,
                                 liquidator,
                                 marginRequirement,
                                 totalCollateral,
                                 marginFreed,
                                 liquidationId,
                                 bankrupt,
                                 canceledOrderIds,
                                 liquidatePerp,
                                 liquidateSpot,
                                 liquidateBorrowForPerpPnl,
                                 liquidatePerpPnlForDeposit,
                                 perpBankruptcy,
                                 spotBankruptcy);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    i += liquidationType.write(_data, i);
    user.write(_data, i);
    i += 32;
    liquidator.write(_data, i);
    i += 32;
    putInt128LE(_data, i, marginRequirement);
    i += 16;
    putInt128LE(_data, i, totalCollateral);
    i += 16;
    putInt64LE(_data, i, marginFreed);
    i += 8;
    putInt16LE(_data, i, liquidationId);
    i += 2;
    _data[i] = (byte) (bankrupt ? 1 : 0);
    ++i;
    i += SerDeUtil.writeVector(4, canceledOrderIds, _data, i);
    i += liquidatePerp.write(_data, i);
    i += liquidateSpot.write(_data, i);
    i += liquidateBorrowForPerpPnl.write(_data, i);
    i += liquidatePerpPnlForDeposit.write(_data, i);
    i += perpBankruptcy.write(_data, i);
    i += spotBankruptcy.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 8
         + liquidationType.l()
         + 32
         + 32
         + 16
         + 16
         + 8
         + 2
         + 1
         + SerDeUtil.lenVector(4, canceledOrderIds)
         + liquidatePerp.l()
         + liquidateSpot.l()
         + liquidateBorrowForPerpPnl.l()
         + liquidatePerpPnlForDeposit.l()
         + perpBankruptcy.l()
         + spotBankruptcy.l();
  }
}
