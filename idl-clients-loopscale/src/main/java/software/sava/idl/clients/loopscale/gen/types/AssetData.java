package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record AssetData(PublicKey assetIdentifier,
                        PublicKey quoteMint,
                        PublicKey oracleAccount,
                        int oracleType,
                        PodU32CBPS maxUncertainty,
                        PodU16 maxAge,
                        int decimals,
                        PodU32CBPS ltv,
                        PodU32CBPS liquidationThreshold,
                        CollateralCaps collateralCaps) implements SerDe {

  public static final int BYTES = 128;

  public static final int ASSET_IDENTIFIER_OFFSET = 0;
  public static final int QUOTE_MINT_OFFSET = 32;
  public static final int ORACLE_ACCOUNT_OFFSET = 64;
  public static final int ORACLE_TYPE_OFFSET = 96;
  public static final int MAX_UNCERTAINTY_OFFSET = 97;
  public static final int MAX_AGE_OFFSET = 101;
  public static final int DECIMALS_OFFSET = 103;
  public static final int LTV_OFFSET = 104;
  public static final int LIQUIDATION_THRESHOLD_OFFSET = 108;
  public static final int COLLATERAL_CAPS_OFFSET = 112;

  public static AssetData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetIdentifier = readPubKey(_data, i);
    i += 32;
    final var quoteMint = readPubKey(_data, i);
    i += 32;
    final var oracleAccount = readPubKey(_data, i);
    i += 32;
    final var oracleType = _data[i] & 0xFF;
    ++i;
    final var maxUncertainty = PodU32CBPS.read(_data, i);
    i += maxUncertainty.l();
    final var maxAge = PodU16.read(_data, i);
    i += maxAge.l();
    final var decimals = _data[i] & 0xFF;
    ++i;
    final var ltv = PodU32CBPS.read(_data, i);
    i += ltv.l();
    final var liquidationThreshold = PodU32CBPS.read(_data, i);
    i += liquidationThreshold.l();
    final var collateralCaps = CollateralCaps.read(_data, i);
    return new AssetData(assetIdentifier,
                         quoteMint,
                         oracleAccount,
                         oracleType,
                         maxUncertainty,
                         maxAge,
                         decimals,
                         ltv,
                         liquidationThreshold,
                         collateralCaps);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    assetIdentifier.write(_data, i);
    i += 32;
    quoteMint.write(_data, i);
    i += 32;
    oracleAccount.write(_data, i);
    i += 32;
    _data[i] = (byte) oracleType;
    ++i;
    i += maxUncertainty.write(_data, i);
    i += maxAge.write(_data, i);
    _data[i] = (byte) decimals;
    ++i;
    i += ltv.write(_data, i);
    i += liquidationThreshold.write(_data, i);
    i += collateralCaps.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
