package software.sava.idl.clients.orca.whirlpools.gen.types;

import java.math.BigInteger;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DynamicTickArray(PublicKey _address,
                               Discriminator discriminator,
                               int startTickIndex,
                               PublicKey whirlpool,
                               BigInteger tickBitmap,
                               DynamicTick[] ticks) implements SerDe {

  public static final int TICKS_LEN = 88;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(17, 216, 246, 142, 225, 199, 218, 56);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int START_TICK_INDEX_OFFSET = 8;
  public static final int WHIRLPOOL_OFFSET = 12;
  public static final int TICK_BITMAP_OFFSET = 44;
  public static final int TICKS_OFFSET = 60;

  public static Filter createStartTickIndexFilter(final int startTickIndex) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, startTickIndex);
    return Filter.createMemCompFilter(START_TICK_INDEX_OFFSET, _data);
  }

  public static Filter createWhirlpoolFilter(final PublicKey whirlpool) {
    return Filter.createMemCompFilter(WHIRLPOOL_OFFSET, whirlpool);
  }

  public static Filter createTickBitmapFilter(final BigInteger tickBitmap) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, tickBitmap);
    return Filter.createMemCompFilter(TICK_BITMAP_OFFSET, _data);
  }

  public static DynamicTickArray read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static DynamicTickArray read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static DynamicTickArray read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], DynamicTickArray> FACTORY = DynamicTickArray::read;

  public static DynamicTickArray read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var startTickIndex = getInt32LE(_data, i);
    i += 4;
    final var whirlpool = readPubKey(_data, i);
    i += 32;
    final var tickBitmap = getInt128LE(_data, i);
    i += 16;
    final var ticks = new DynamicTick[88];
    SerDeUtil.readArray(ticks, DynamicTick::read, _data, i);
    return new DynamicTickArray(_address,
                                discriminator,
                                startTickIndex,
                                whirlpool,
                                tickBitmap,
                                ticks);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt32LE(_data, i, startTickIndex);
    i += 4;
    whirlpool.write(_data, i);
    i += 32;
    putInt128LE(_data, i, tickBitmap);
    i += 16;
    i += SerDeUtil.writeArrayChecked(ticks, 88, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 4 + 32 + 16 + SerDeUtil.lenArray(ticks);
  }
}
