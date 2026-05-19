package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Fixed SplineCollectionHeader prefix for a Phoenix Eternal spline collection account.
/// Dynamic spline map bytes follow this header and are left as trailing account data by generic decoders.
///
public record SplineCollectionHeader(PublicKey _address,
                                     Discriminator discriminator,
                                     long discriminant,
                                     PublicKey market,
                                     Symbol assetSymbol,
                                     SequenceNumber sequenceNumber,
                                     int numSplines,
                                     int numActive,
                                     byte[] padding) implements SerDe {

  public static final int BYTES = 120;
  public static final int PADDING_LEN = 32;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(192, 245, 182, 191, 88, 37, 79, 48);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int DISCRIMINANT_OFFSET = 8;
  public static final int MARKET_OFFSET = 16;
  public static final int ASSET_SYMBOL_OFFSET = 48;
  public static final int SEQUENCE_NUMBER_OFFSET = 64;
  public static final int NUM_SPLINES_OFFSET = 80;
  public static final int NUM_ACTIVE_OFFSET = 84;
  public static final int PADDING_OFFSET = 88;

  public static Filter createDiscriminantFilter(final long discriminant) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, discriminant);
    return Filter.createMemCompFilter(DISCRIMINANT_OFFSET, _data);
  }

  public static Filter createMarketFilter(final PublicKey market) {
    return Filter.createMemCompFilter(MARKET_OFFSET, market);
  }

  public static Filter createAssetSymbolFilter(final Symbol assetSymbol) {
    return Filter.createMemCompFilter(ASSET_SYMBOL_OFFSET, assetSymbol.write());
  }

  public static Filter createSequenceNumberFilter(final SequenceNumber sequenceNumber) {
    return Filter.createMemCompFilter(SEQUENCE_NUMBER_OFFSET, sequenceNumber.write());
  }

  public static Filter createNumSplinesFilter(final int numSplines) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, numSplines);
    return Filter.createMemCompFilter(NUM_SPLINES_OFFSET, _data);
  }

  public static Filter createNumActiveFilter(final int numActive) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, numActive);
    return Filter.createMemCompFilter(NUM_ACTIVE_OFFSET, _data);
  }

  public static SplineCollectionHeader read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static SplineCollectionHeader read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static SplineCollectionHeader read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], SplineCollectionHeader> FACTORY = SplineCollectionHeader::read;

  public static SplineCollectionHeader read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var discriminant = getInt64LE(_data, i);
    i += 8;
    final var market = readPubKey(_data, i);
    i += 32;
    final var assetSymbol = Symbol.read(_data, i);
    i += assetSymbol.l();
    final var sequenceNumber = SequenceNumber.read(_data, i);
    i += sequenceNumber.l();
    final var numSplines = getInt32LE(_data, i);
    i += 4;
    final var numActive = getInt32LE(_data, i);
    i += 4;
    final var padding = new byte[32];
    SerDeUtil.readArray(padding, _data, i);
    return new SplineCollectionHeader(_address,
                                      discriminator,
                                      discriminant,
                                      market,
                                      assetSymbol,
                                      sequenceNumber,
                                      numSplines,
                                      numActive,
                                      padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, discriminant);
    i += 8;
    market.write(_data, i);
    i += 32;
    i += assetSymbol.write(_data, i);
    i += sequenceNumber.write(_data, i);
    putInt32LE(_data, i, numSplines);
    i += 4;
    putInt32LE(_data, i, numActive);
    i += 4;
    i += SerDeUtil.writeArrayChecked(padding, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
