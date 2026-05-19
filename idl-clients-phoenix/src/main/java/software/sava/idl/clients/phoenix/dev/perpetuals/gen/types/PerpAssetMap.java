package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Canonical Eternal perpetual asset map account.
///
public record PerpAssetMap(PublicKey _address,
                           Discriminator discriminator,
                           long discriminant,
                           SequenceNumber sequenceNumber,
                           int numAssets,
                           byte[] padding0,
                           PerpAssetMetadataShortMapV2 metadatas) implements SerDe {

  public static final int BYTES = 1622072;
  public static final int PADDING_0_LEN = 6;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(142, 54, 115, 223, 124, 239, 108, 38);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int DISCRIMINANT_OFFSET = 8;
  public static final int SEQUENCE_NUMBER_OFFSET = 16;
  public static final int NUM_ASSETS_OFFSET = 32;
  public static final int PADDING_0_OFFSET = 34;
  public static final int METADATAS_OFFSET = 40;

  public static Filter createDiscriminantFilter(final long discriminant) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, discriminant);
    return Filter.createMemCompFilter(DISCRIMINANT_OFFSET, _data);
  }

  public static Filter createSequenceNumberFilter(final SequenceNumber sequenceNumber) {
    return Filter.createMemCompFilter(SEQUENCE_NUMBER_OFFSET, sequenceNumber.write());
  }

  public static Filter createNumAssetsFilter(final int numAssets) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, numAssets);
    return Filter.createMemCompFilter(NUM_ASSETS_OFFSET, _data);
  }

  public static PerpAssetMap read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static PerpAssetMap read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static PerpAssetMap read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], PerpAssetMap> FACTORY = PerpAssetMap::read;

  public static PerpAssetMap read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var discriminant = getInt64LE(_data, i);
    i += 8;
    final var sequenceNumber = SequenceNumber.read(_data, i);
    i += sequenceNumber.l();
    final var numAssets = getInt16LE(_data, i);
    i += 2;
    final var padding0 = new byte[6];
    i += SerDeUtil.readArray(padding0, _data, i);
    final var metadatas = PerpAssetMetadataShortMapV2.read(_data, i);
    return new PerpAssetMap(_address,
                            discriminator,
                            discriminant,
                            sequenceNumber,
                            numAssets,
                            padding0,
                            metadatas);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, discriminant);
    i += 8;
    i += sequenceNumber.write(_data, i);
    putInt16LE(_data, i, numAssets);
    i += 2;
    i += SerDeUtil.writeArrayChecked(padding0, 6, _data, i);
    i += metadatas.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
