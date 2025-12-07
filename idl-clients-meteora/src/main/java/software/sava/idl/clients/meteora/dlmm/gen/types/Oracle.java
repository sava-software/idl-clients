package software.sava.idl.clients.meteora.dlmm.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param idx Index of latest observation
/// @param activeSize Size of active sample. Active sample is initialized observation.
/// @param length Number of observations
public record Oracle(PublicKey _address,
                     Discriminator discriminator,
                     long idx,
                     long activeSize,
                     long length) implements Borsh {

  public static final int BYTES = 32;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(139, 194, 131, 179, 140, 179, 229, 244);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int IDX_OFFSET = 8;
  public static final int ACTIVE_SIZE_OFFSET = 16;
  public static final int LENGTH_OFFSET = 24;

  public static Filter createIdxFilter(final long idx) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, idx);
    return Filter.createMemCompFilter(IDX_OFFSET, _data);
  }

  public static Filter createActiveSizeFilter(final long activeSize) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, activeSize);
    return Filter.createMemCompFilter(ACTIVE_SIZE_OFFSET, _data);
  }

  public static Filter createLengthFilter(final long length) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, length);
    return Filter.createMemCompFilter(LENGTH_OFFSET, _data);
  }

  public static Oracle read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Oracle read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Oracle read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Oracle> FACTORY = Oracle::read;

  public static Oracle read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var idx = getInt64LE(_data, i);
    i += 8;
    final var activeSize = getInt64LE(_data, i);
    i += 8;
    final var length = getInt64LE(_data, i);
    return new Oracle(_address, discriminator, idx, activeSize, length);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, idx);
    i += 8;
    putInt64LE(_data, i, activeSize);
    i += 8;
    putInt64LE(_data, i, length);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
