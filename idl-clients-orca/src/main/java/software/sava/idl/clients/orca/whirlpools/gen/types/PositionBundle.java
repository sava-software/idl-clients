package software.sava.idl.clients.orca.whirlpools.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record PositionBundle(PublicKey _address, Discriminator discriminator, PublicKey positionBundleMint, byte[] positionBitmap) implements SerDe {

  public static final int BYTES = 72;
  public static final int POSITION_BITMAP_LEN = 32;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(129, 169, 175, 65, 185, 95, 32, 100);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int POSITION_BUNDLE_MINT_OFFSET = 8;
  public static final int POSITION_BITMAP_OFFSET = 40;

  public static Filter createPositionBundleMintFilter(final PublicKey positionBundleMint) {
    return Filter.createMemCompFilter(POSITION_BUNDLE_MINT_OFFSET, positionBundleMint);
  }

  public static PositionBundle read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static PositionBundle read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static PositionBundle read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], PositionBundle> FACTORY = PositionBundle::read;

  public static PositionBundle read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var positionBundleMint = readPubKey(_data, i);
    i += 32;
    final var positionBitmap = new byte[32];
    SerDeUtil.readArray(positionBitmap, _data, i);
    return new PositionBundle(_address, discriminator, positionBundleMint, positionBitmap);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    positionBundleMint.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(positionBitmap, 32, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
