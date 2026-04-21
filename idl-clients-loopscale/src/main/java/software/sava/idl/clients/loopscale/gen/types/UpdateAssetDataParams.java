package software.sava.idl.clients.loopscale.gen.types;

import java.lang.Boolean;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record UpdateAssetDataParams(PublicKey assetIdentifier,
                                    PublicKey quoteMint,
                                    PublicKey oracleAccount,
                                    OptionalInt oracleType,
                                    OptionalInt maxUncertainty,
                                    OptionalInt maxAge,
                                    OptionalInt ltv,
                                    OptionalInt liquidationThreshold,
                                    OptionalLong maxCollateralAllocationPct,
                                    Boolean remove) implements SerDe {

  public static final int ASSET_IDENTIFIER_OFFSET = 0;
  public static final int QUOTE_MINT_OFFSET = 33;

  public static UpdateAssetDataParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var assetIdentifier = readPubKey(_data, i);
    i += 32;
    final PublicKey quoteMint;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      quoteMint = null;
      ++i;
    } else {
      ++i;
      quoteMint = readPubKey(_data, i);
      i += 32;
    }
    final PublicKey oracleAccount;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      oracleAccount = null;
      ++i;
    } else {
      ++i;
      oracleAccount = readPubKey(_data, i);
      i += 32;
    }
    final OptionalInt oracleType;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      oracleType = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      oracleType = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt maxUncertainty;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxUncertainty = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      maxUncertainty = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt maxAge;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxAge = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      maxAge = OptionalInt.of(getInt16LE(_data, i));
      i += 2;
    }
    final OptionalInt ltv;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      ltv = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      ltv = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalInt liquidationThreshold;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      liquidationThreshold = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      liquidationThreshold = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final OptionalLong maxCollateralAllocationPct;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxCollateralAllocationPct = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxCollateralAllocationPct = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final Boolean remove;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      remove = null;
    } else {
      ++i;
      remove = _data[i] == 1;
    }
    return new UpdateAssetDataParams(assetIdentifier,
                                     quoteMint,
                                     oracleAccount,
                                     oracleType,
                                     maxUncertainty,
                                     maxAge,
                                     ltv,
                                     liquidationThreshold,
                                     maxCollateralAllocationPct,
                                     remove);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    assetIdentifier.write(_data, i);
    i += 32;
    i += SerDeUtil.writeOptional(1, quoteMint, _data, i);
    i += SerDeUtil.writeOptional(1, oracleAccount, _data, i);
    i += SerDeUtil.writeOptionalbyte(1, oracleType, _data, i);
    i += SerDeUtil.writeOptional(1, maxUncertainty, _data, i);
    i += SerDeUtil.writeOptionalshort(1, maxAge, _data, i);
    i += SerDeUtil.writeOptional(1, ltv, _data, i);
    i += SerDeUtil.writeOptional(1, liquidationThreshold, _data, i);
    i += SerDeUtil.writeOptional(1, maxCollateralAllocationPct, _data, i);
    i += SerDeUtil.writeOptional(1, remove, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 32
         + (quoteMint == null ? 1 : (1 + 32))
         + (oracleAccount == null ? 1 : (1 + 32))
         + (oracleType == null || oracleType.isEmpty() ? 1 : (1 + 1))
         + (maxUncertainty == null || maxUncertainty.isEmpty() ? 1 : (1 + 4))
         + (maxAge == null || maxAge.isEmpty() ? 1 : (1 + 2))
         + (ltv == null || ltv.isEmpty() ? 1 : (1 + 4))
         + (liquidationThreshold == null || liquidationThreshold.isEmpty() ? 1 : (1 + 4))
         + (maxCollateralAllocationPct == null || maxCollateralAllocationPct.isEmpty() ? 1 : (1 + 8))
         + (remove == null ? 1 : (1 + 1));
  }
}
