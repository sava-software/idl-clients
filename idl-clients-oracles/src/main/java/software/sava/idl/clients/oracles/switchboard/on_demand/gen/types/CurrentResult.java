package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.math.BigInteger;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param value The median value of the submissions needed for quorom size
/// @param stdDev The standard deviation of the submissions needed for quorom size
/// @param mean The mean of the submissions needed for quorom size
/// @param range The range of the submissions needed for quorom size
/// @param minValue The minimum value of the submissions needed for quorom size
/// @param maxValue The maximum value of the submissions needed for quorom size
/// @param numSamples The number of samples used to calculate this result
/// @param submissionIdx The index of the submission that was used to calculate this result
/// @param slot The slot at which this value was signed.
/// @param minSlot The slot at which the first considered submission was made
/// @param maxSlot The slot at which the last considered submission was made
public record CurrentResult(BigInteger value,
                            BigInteger stdDev,
                            BigInteger mean,
                            BigInteger range,
                            BigInteger minValue,
                            BigInteger maxValue,
                            int numSamples,
                            int submissionIdx,
                            byte[] padding1,
                            long slot,
                            long minSlot,
                            long maxSlot) implements SerDe {

  public static final int BYTES = 128;
  public static final int PADDING_1_LEN = 6;

  public static final int VALUE_OFFSET = 0;
  public static final int STD_DEV_OFFSET = 16;
  public static final int MEAN_OFFSET = 32;
  public static final int RANGE_OFFSET = 48;
  public static final int MIN_VALUE_OFFSET = 64;
  public static final int MAX_VALUE_OFFSET = 80;
  public static final int NUM_SAMPLES_OFFSET = 96;
  public static final int SUBMISSION_IDX_OFFSET = 97;
  public static final int PADDING_1_OFFSET = 98;
  public static final int SLOT_OFFSET = 104;
  public static final int MIN_SLOT_OFFSET = 112;
  public static final int MAX_SLOT_OFFSET = 120;

  public static CurrentResult read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var value = getInt128LE(_data, i);
    i += 16;
    final var stdDev = getInt128LE(_data, i);
    i += 16;
    final var mean = getInt128LE(_data, i);
    i += 16;
    final var range = getInt128LE(_data, i);
    i += 16;
    final var minValue = getInt128LE(_data, i);
    i += 16;
    final var maxValue = getInt128LE(_data, i);
    i += 16;
    final var numSamples = _data[i] & 0xFF;
    ++i;
    final var submissionIdx = _data[i] & 0xFF;
    ++i;
    final var padding1 = new byte[6];
    i += SerDeUtil.readArray(padding1, _data, i);
    final var slot = getInt64LE(_data, i);
    i += 8;
    final var minSlot = getInt64LE(_data, i);
    i += 8;
    final var maxSlot = getInt64LE(_data, i);
    return new CurrentResult(value,
                             stdDev,
                             mean,
                             range,
                             minValue,
                             maxValue,
                             numSamples,
                             submissionIdx,
                             padding1,
                             slot,
                             minSlot,
                             maxSlot);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    putInt128LE(_data, i, value);
    i += 16;
    putInt128LE(_data, i, stdDev);
    i += 16;
    putInt128LE(_data, i, mean);
    i += 16;
    putInt128LE(_data, i, range);
    i += 16;
    putInt128LE(_data, i, minValue);
    i += 16;
    putInt128LE(_data, i, maxValue);
    i += 16;
    _data[i] = (byte) numSamples;
    ++i;
    _data[i] = (byte) submissionIdx;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding1, 6, _data, i);
    putInt64LE(_data, i, slot);
    i += 8;
    putInt64LE(_data, i, minSlot);
    i += 8;
    putInt64LE(_data, i, maxSlot);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
