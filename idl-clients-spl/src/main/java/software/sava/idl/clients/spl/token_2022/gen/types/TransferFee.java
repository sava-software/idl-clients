package software.sava.idl.clients.spl.token_2022.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param epoch First epoch where the transfer fee takes effect.
/// @param maximumFee Maximum fee assessed on transfers, expressed as an amount of tokens.
/// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the
///                               transfer amount, ie. increments of 0.01%.
public record TransferFee(long epoch,
                          long maximumFee,
                          int transferFeeBasisPoints) implements SerDe {

  public static final int BYTES = 18;

  public static final int EPOCH_OFFSET = 0;
  public static final int MAXIMUM_FEE_OFFSET = 8;
  public static final int TRANSFER_FEE_BASIS_POINTS_OFFSET = 16;

  public static TransferFee read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var epoch = getInt64LE(_data, i);
    i += 8;
    final var maximumFee = getInt64LE(_data, i);
    i += 8;
    final var transferFeeBasisPoints = getInt16LE(_data, i);
    return new TransferFee(epoch, maximumFee, transferFeeBasisPoints);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt64LE(_data, i, epoch);
    i += 8;
    putInt64LE(_data, i, maximumFee);
    i += 8;
    putInt16LE(_data, i, transferFeeBasisPoints);
    i += 2;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
