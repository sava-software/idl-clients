package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.lang.Boolean;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public sealed interface UpdateArgs extends RustEnum permits
  UpdateArgs.V1,
  UpdateArgs.AsUpdateAuthorityV2,
  UpdateArgs.AsAuthorityItemDelegateV2,
  UpdateArgs.AsCollectionDelegateV2,
  UpdateArgs.AsDataDelegateV2,
  UpdateArgs.AsProgrammableConfigDelegateV2,
  UpdateArgs.AsDataItemDelegateV2,
  UpdateArgs.AsCollectionItemDelegateV2,
  UpdateArgs.AsProgrammableConfigItemDelegateV2 {

  static UpdateArgs read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> V1.read(_data, i);
      case 1 -> AsUpdateAuthorityV2.read(_data, i);
      case 2 -> AsAuthorityItemDelegateV2.read(_data, i);
      case 3 -> AsCollectionDelegateV2.read(_data, i);
      case 4 -> AsDataDelegateV2.read(_data, i);
      case 5 -> AsProgrammableConfigDelegateV2.read(_data, i);
      case 6 -> AsDataItemDelegateV2.read(_data, i);
      case 7 -> AsCollectionItemDelegateV2.read(_data, i);
      case 8 -> AsProgrammableConfigItemDelegateV2.read(_data, i);
      default -> null;
    };
  }

  record V1(PublicKey newUpdateAuthority,
            Data data,
            Boolean primarySaleHappened,
            Boolean isMutable,
            CollectionToggle collection,
            CollectionDetailsToggle collectionDetails,
            UsesToggle uses,
            RuleSetToggle ruleSet,
            AuthorizationData authorizationData) implements UpdateArgs {

    public static final int NEW_UPDATE_AUTHORITY_OFFSET = 1;

    public static V1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final PublicKey newUpdateAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        newUpdateAuthority = null;
        ++i;
      } else {
        ++i;
        newUpdateAuthority = readPubKey(_data, i);
        i += 32;
      }
      final Data data;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        data = null;
        ++i;
      } else {
        ++i;
        data = Data.read(_data, i);
        i += data.l();
      }
      final Boolean primarySaleHappened;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        primarySaleHappened = null;
        ++i;
      } else {
        ++i;
        primarySaleHappened = _data[i] == 1;
        ++i;
      }
      final Boolean isMutable;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        isMutable = null;
        ++i;
      } else {
        ++i;
        isMutable = _data[i] == 1;
        ++i;
      }
      final var collection = CollectionToggle.read(_data, i);
      i += collection.l();
      final var collectionDetails = CollectionDetailsToggle.read(_data, i);
      i += collectionDetails.l();
      final var uses = UsesToggle.read(_data, i);
      i += uses.l();
      final var ruleSet = RuleSetToggle.read(_data, i);
      i += ruleSet.l();
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new V1(newUpdateAuthority,
                    data,
                    primarySaleHappened,
                    isMutable,
                    collection,
                    collectionDetails,
                    uses,
                    ruleSet,
                    authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += SerDeUtil.writeOptional(1, newUpdateAuthority, _data, i);
      i += SerDeUtil.writeOptional(1, data, _data, i);
      i += SerDeUtil.writeOptional(1, primarySaleHappened, _data, i);
      i += SerDeUtil.writeOptional(1, isMutable, _data, i);
      i += collection.write(_data, i);
      i += collectionDetails.write(_data, i);
      i += uses.write(_data, i);
      i += ruleSet.write(_data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (newUpdateAuthority == null ? 1 : (1 + 32))
           + (data == null ? 1 : (1 + data.l()))
           + (primarySaleHappened == null ? 1 : (1 + 1))
           + (isMutable == null ? 1 : (1 + 1))
           + collection.l()
           + collectionDetails.l()
           + uses.l()
           + ruleSet.l()
           + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record AsUpdateAuthorityV2(PublicKey newUpdateAuthority,
                             Data data,
                             Boolean primarySaleHappened,
                             Boolean isMutable,
                             CollectionToggle collection,
                             CollectionDetailsToggle collectionDetails,
                             UsesToggle uses,
                             RuleSetToggle ruleSet,
                             TokenStandard tokenStandard,
                             AuthorizationData authorizationData) implements UpdateArgs {

    public static final int NEW_UPDATE_AUTHORITY_OFFSET = 1;

    public static AsUpdateAuthorityV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final PublicKey newUpdateAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        newUpdateAuthority = null;
        ++i;
      } else {
        ++i;
        newUpdateAuthority = readPubKey(_data, i);
        i += 32;
      }
      final Data data;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        data = null;
        ++i;
      } else {
        ++i;
        data = Data.read(_data, i);
        i += data.l();
      }
      final Boolean primarySaleHappened;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        primarySaleHappened = null;
        ++i;
      } else {
        ++i;
        primarySaleHappened = _data[i] == 1;
        ++i;
      }
      final Boolean isMutable;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        isMutable = null;
        ++i;
      } else {
        ++i;
        isMutable = _data[i] == 1;
        ++i;
      }
      final var collection = CollectionToggle.read(_data, i);
      i += collection.l();
      final var collectionDetails = CollectionDetailsToggle.read(_data, i);
      i += collectionDetails.l();
      final var uses = UsesToggle.read(_data, i);
      i += uses.l();
      final var ruleSet = RuleSetToggle.read(_data, i);
      i += ruleSet.l();
      final TokenStandard tokenStandard;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        tokenStandard = null;
        ++i;
      } else {
        ++i;
        tokenStandard = TokenStandard.read(_data, i);
        i += tokenStandard.l();
      }
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsUpdateAuthorityV2(newUpdateAuthority,
                                     data,
                                     primarySaleHappened,
                                     isMutable,
                                     collection,
                                     collectionDetails,
                                     uses,
                                     ruleSet,
                                     tokenStandard,
                                     authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += SerDeUtil.writeOptional(1, newUpdateAuthority, _data, i);
      i += SerDeUtil.writeOptional(1, data, _data, i);
      i += SerDeUtil.writeOptional(1, primarySaleHappened, _data, i);
      i += SerDeUtil.writeOptional(1, isMutable, _data, i);
      i += collection.write(_data, i);
      i += collectionDetails.write(_data, i);
      i += uses.write(_data, i);
      i += ruleSet.write(_data, i);
      i += SerDeUtil.writeOptional(1, tokenStandard, _data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (newUpdateAuthority == null ? 1 : (1 + 32))
           + (data == null ? 1 : (1 + data.l()))
           + (primarySaleHappened == null ? 1 : (1 + 1))
           + (isMutable == null ? 1 : (1 + 1))
           + collection.l()
           + collectionDetails.l()
           + uses.l()
           + ruleSet.l()
           + (tokenStandard == null ? 1 : (1 + tokenStandard.l()))
           + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record AsAuthorityItemDelegateV2(PublicKey newUpdateAuthority,
                                   Boolean primarySaleHappened,
                                   Boolean isMutable,
                                   TokenStandard tokenStandard,
                                   AuthorizationData authorizationData) implements UpdateArgs {

    public static final int NEW_UPDATE_AUTHORITY_OFFSET = 1;

    public static AsAuthorityItemDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final PublicKey newUpdateAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        newUpdateAuthority = null;
        ++i;
      } else {
        ++i;
        newUpdateAuthority = readPubKey(_data, i);
        i += 32;
      }
      final Boolean primarySaleHappened;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        primarySaleHappened = null;
        ++i;
      } else {
        ++i;
        primarySaleHappened = _data[i] == 1;
        ++i;
      }
      final Boolean isMutable;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        isMutable = null;
        ++i;
      } else {
        ++i;
        isMutable = _data[i] == 1;
        ++i;
      }
      final TokenStandard tokenStandard;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        tokenStandard = null;
        ++i;
      } else {
        ++i;
        tokenStandard = TokenStandard.read(_data, i);
        i += tokenStandard.l();
      }
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsAuthorityItemDelegateV2(newUpdateAuthority,
                                           primarySaleHappened,
                                           isMutable,
                                           tokenStandard,
                                           authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += SerDeUtil.writeOptional(1, newUpdateAuthority, _data, i);
      i += SerDeUtil.writeOptional(1, primarySaleHappened, _data, i);
      i += SerDeUtil.writeOptional(1, isMutable, _data, i);
      i += SerDeUtil.writeOptional(1, tokenStandard, _data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (newUpdateAuthority == null ? 1 : (1 + 32))
           + (primarySaleHappened == null ? 1 : (1 + 1))
           + (isMutable == null ? 1 : (1 + 1))
           + (tokenStandard == null ? 1 : (1 + tokenStandard.l()))
           + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record AsCollectionDelegateV2(CollectionToggle collection, AuthorizationData authorizationData) implements UpdateArgs {

    public static final int COLLECTION_OFFSET = 0;

    public static AsCollectionDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var collection = CollectionToggle.read(_data, i);
      i += collection.l();
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsCollectionDelegateV2(collection, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += collection.write(_data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + collection.l() + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record AsDataDelegateV2(Data data, AuthorizationData authorizationData) implements UpdateArgs {

    public static final int DATA_OFFSET = 1;

    public static AsDataDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final Data data;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        data = null;
        ++i;
      } else {
        ++i;
        data = Data.read(_data, i);
        i += data.l();
      }
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsDataDelegateV2(data, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += SerDeUtil.writeOptional(1, data, _data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (data == null ? 1 : (1 + data.l())) + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record AsProgrammableConfigDelegateV2(RuleSetToggle ruleSet, AuthorizationData authorizationData) implements UpdateArgs {

    public static final int RULE_SET_OFFSET = 0;

    public static AsProgrammableConfigDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var ruleSet = RuleSetToggle.read(_data, i);
      i += ruleSet.l();
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsProgrammableConfigDelegateV2(ruleSet, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += ruleSet.write(_data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + ruleSet.l() + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record AsDataItemDelegateV2(Data data, AuthorizationData authorizationData) implements UpdateArgs {

    public static final int DATA_OFFSET = 1;

    public static AsDataItemDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final Data data;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        data = null;
        ++i;
      } else {
        ++i;
        data = Data.read(_data, i);
        i += data.l();
      }
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsDataItemDelegateV2(data, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += SerDeUtil.writeOptional(1, data, _data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (data == null ? 1 : (1 + data.l())) + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }

  record AsCollectionItemDelegateV2(CollectionToggle collection, AuthorizationData authorizationData) implements UpdateArgs {

    public static final int COLLECTION_OFFSET = 0;

    public static AsCollectionItemDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var collection = CollectionToggle.read(_data, i);
      i += collection.l();
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsCollectionItemDelegateV2(collection, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += collection.write(_data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + collection.l() + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 7;
    }
  }

  record AsProgrammableConfigItemDelegateV2(RuleSetToggle ruleSet, AuthorizationData authorizationData) implements UpdateArgs {

    public static final int RULE_SET_OFFSET = 0;

    public static AsProgrammableConfigItemDelegateV2 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var ruleSet = RuleSetToggle.read(_data, i);
      i += ruleSet.l();
      final AuthorizationData authorizationData;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new AsProgrammableConfigItemDelegateV2(ruleSet, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += ruleSet.write(_data, i);
      i += SerDeUtil.writeOptional(1, authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + ruleSet.l() + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 8;
    }
  }
}
