package software.sava.idl.clients.drift.gen.events;

import software.sava.core.borsh.Borsh;

public sealed interface DriftEvent extends Borsh permits
    NewUserRecord,
    DepositRecord,
    SpotInterestRecord,
    FundingPaymentRecord,
    FundingRateRecord,
    CurveRecord,
    SignedMsgOrderRecord,
    OrderRecord,
    OrderActionRecord,
    LPRecord,
    LiquidationRecord,
    SettlePnlRecord,
    InsuranceFundRecord,
    InsuranceFundStakeRecord,
    InsuranceFundSwapRecord,
    TransferProtocolIfSharesToRevenuePoolRecord,
    SwapRecord,
    SpotMarketVaultDepositRecord,
    DeleteUserRecord,
    FuelSweepRecord,
    FuelSeasonRecord,
    RevenueShareSettleRecord,
    LPSettleRecord,
    LPSwapRecord,
    LPMintRedeemRecord,
    LPBorrowLendDepositRecord {

  static DriftEvent read(final byte[] _data, final int _offset) {
    if (NewUserRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return NewUserRecord.read(_data, _offset);
    } else if (DepositRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return DepositRecord.read(_data, _offset);
    } else if (SpotInterestRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return SpotInterestRecord.read(_data, _offset);
    } else if (FundingPaymentRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return FundingPaymentRecord.read(_data, _offset);
    } else if (FundingRateRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return FundingRateRecord.read(_data, _offset);
    } else if (CurveRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return CurveRecord.read(_data, _offset);
    } else if (SignedMsgOrderRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return SignedMsgOrderRecord.read(_data, _offset);
    } else if (OrderRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return OrderRecord.read(_data, _offset);
    } else if (OrderActionRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return OrderActionRecord.read(_data, _offset);
    } else if (LPRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return LPRecord.read(_data, _offset);
    } else if (LiquidationRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return LiquidationRecord.read(_data, _offset);
    } else if (SettlePnlRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return SettlePnlRecord.read(_data, _offset);
    } else if (InsuranceFundRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return InsuranceFundRecord.read(_data, _offset);
    } else if (InsuranceFundStakeRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return InsuranceFundStakeRecord.read(_data, _offset);
    } else if (InsuranceFundSwapRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return InsuranceFundSwapRecord.read(_data, _offset);
    } else if (TransferProtocolIfSharesToRevenuePoolRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return TransferProtocolIfSharesToRevenuePoolRecord.read(_data, _offset);
    } else if (SwapRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return SwapRecord.read(_data, _offset);
    } else if (SpotMarketVaultDepositRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return SpotMarketVaultDepositRecord.read(_data, _offset);
    } else if (DeleteUserRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return DeleteUserRecord.read(_data, _offset);
    } else if (FuelSweepRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return FuelSweepRecord.read(_data, _offset);
    } else if (FuelSeasonRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return FuelSeasonRecord.read(_data, _offset);
    } else if (RevenueShareSettleRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return RevenueShareSettleRecord.read(_data, _offset);
    } else if (LPSettleRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return LPSettleRecord.read(_data, _offset);
    } else if (LPSwapRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return LPSwapRecord.read(_data, _offset);
    } else if (LPMintRedeemRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return LPMintRedeemRecord.read(_data, _offset);
    } else if (LPBorrowLendDepositRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return LPBorrowLendDepositRecord.read(_data, _offset);
    } else {
      return null;
    }
  }

  static DriftEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static DriftEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static DriftEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}