package software.sava.idl.clients.drift.vaults.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface DriftVaultsEvent extends SerDe permits
    VaultRecord,
    VaultDepositorRecord,
    VaultDepositorV1Record,
    ShareTransferRecord,
    FuelSeasonRecord,
    FeeUpdateRecord,
    ManagerBorrowRecord,
    ManagerRepayRecord,
    ManagerUpdateBorrowRecord {

  static DriftVaultsEvent read(final byte[] _data, final int _offset) {
    if (VaultRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return VaultRecord.read(_data, _offset);
    } else if (VaultDepositorRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return VaultDepositorRecord.read(_data, _offset);
    } else if (VaultDepositorV1Record.DISCRIMINATOR.equals(_data, _offset)) {
      return VaultDepositorV1Record.read(_data, _offset);
    } else if (ShareTransferRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return ShareTransferRecord.read(_data, _offset);
    } else if (FuelSeasonRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return FuelSeasonRecord.read(_data, _offset);
    } else if (FeeUpdateRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return FeeUpdateRecord.read(_data, _offset);
    } else if (ManagerBorrowRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return ManagerBorrowRecord.read(_data, _offset);
    } else if (ManagerRepayRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return ManagerRepayRecord.read(_data, _offset);
    } else if (ManagerUpdateBorrowRecord.DISCRIMINATOR.equals(_data, _offset)) {
      return ManagerUpdateBorrowRecord.read(_data, _offset);
    } else {
      return null;
    }
  }

  static DriftVaultsEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static DriftVaultsEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static DriftVaultsEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}