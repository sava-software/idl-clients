package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record AssetData(String name, byte[] _name,
                        String symbol, byte[] _symbol,
                        String uri, byte[] _uri,
                        int sellerFeeBasisPoints,
                        Creator[] creators,
                        boolean primarySaleHappened,
                        boolean isMutable,
                        TokenStandard tokenStandard,
                        Collection collection,
                        Uses uses,
                        CollectionDetails collectionDetails,
                        PublicKey ruleSet) implements SerDe {

  public static AssetData createRecord(final String name,
                                       final String symbol,
                                       final String uri,
                                       final int sellerFeeBasisPoints,
                                       final Creator[] creators,
                                       final boolean primarySaleHappened,
                                       final boolean isMutable,
                                       final TokenStandard tokenStandard,
                                       final Collection collection,
                                       final Uses uses,
                                       final CollectionDetails collectionDetails,
                                       final PublicKey ruleSet) {
    return new AssetData(name, name == null ? null : name.getBytes(UTF_8),
                         symbol, symbol == null ? null : symbol.getBytes(UTF_8),
                         uri, uri == null ? null : uri.getBytes(UTF_8),
                         sellerFeeBasisPoints,
                         creators,
                         primarySaleHappened,
                         isMutable,
                         tokenStandard,
                         collection,
                         uses,
                         collectionDetails,
                         ruleSet);
  }

  public static AssetData read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final int _nameLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _name = Arrays.copyOfRange(_data, i, i + _nameLength);
    final var name = new String(_name, UTF_8);
    i += _name.length;
    final int _symbolLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _symbol = Arrays.copyOfRange(_data, i, i + _symbolLength);
    final var symbol = new String(_symbol, UTF_8);
    i += _symbol.length;
    final int _uriLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _uri = Arrays.copyOfRange(_data, i, i + _uriLength);
    final var uri = new String(_uri, UTF_8);
    i += _uri.length;
    final var sellerFeeBasisPoints = getInt16LE(_data, i);
    i += 2;
    final Creator[] creators;
    if (_data[i] == 0) {
      creators = null;
      ++i;
    } else {
      ++i;
      creators = SerDeUtil.readVector(4, Creator.class, Creator::read, _data, i);
      i += SerDeUtil.lenVector(4, creators);
    }
    final var primarySaleHappened = _data[i] == 1;
    ++i;
    final var isMutable = _data[i] == 1;
    ++i;
    final var tokenStandard = TokenStandard.read(_data, i);
    i += tokenStandard.l();
    final Collection collection;
    if (_data[i] == 0) {
      collection = null;
      ++i;
    } else {
      ++i;
      collection = Collection.read(_data, i);
      i += collection.l();
    }
    final Uses uses;
    if (_data[i] == 0) {
      uses = null;
      ++i;
    } else {
      ++i;
      uses = Uses.read(_data, i);
      i += uses.l();
    }
    final CollectionDetails collectionDetails;
    if (_data[i] == 0) {
      collectionDetails = null;
      ++i;
    } else {
      ++i;
      collectionDetails = CollectionDetails.read(_data, i);
      i += collectionDetails.l();
    }
    final PublicKey ruleSet;
    if (_data[i] == 0) {
      ruleSet = null;
    } else {
      ++i;
      ruleSet = readPubKey(_data, i);
    }
    return new AssetData(name, name == null ? null : name.getBytes(UTF_8),
                         symbol, symbol == null ? null : symbol.getBytes(UTF_8),
                         uri, uri == null ? null : uri.getBytes(UTF_8),
                         sellerFeeBasisPoints,
                         creators,
                         primarySaleHappened,
                         isMutable,
                         tokenStandard,
                         collection,
                         uses,
                         collectionDetails,
                         ruleSet);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeVector(4, _name, _data, i);
    i += SerDeUtil.writeVector(4, _symbol, _data, i);
    i += SerDeUtil.writeVector(4, _uri, _data, i);
    putInt16LE(_data, i, sellerFeeBasisPoints);
    i += 2;
    if (creators == null || creators.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += SerDeUtil.writeVector(4, creators, _data, i);
    }
    _data[i] = (byte) (primarySaleHappened ? 1 : 0);
    ++i;
    _data[i] = (byte) (isMutable ? 1 : 0);
    ++i;
    i += tokenStandard.write(_data, i);
    i += SerDeUtil.writeOptional(1, collection, _data, i);
    i += SerDeUtil.writeOptional(1, uses, _data, i);
    i += SerDeUtil.writeOptional(1, collectionDetails, _data, i);
    i += SerDeUtil.writeOptional(1, ruleSet, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return _name.length
         + _symbol.length
         + _uri.length
         + 2
         + (creators == null || creators.length == 0 ? 1 : (1 + SerDeUtil.lenVector(4, creators)))
         + 1
         + 1
         + tokenStandard.l()
         + (collection == null ? 1 : (1 + collection.l()))
         + (uses == null ? 1 : (1 + uses.l()))
         + (collectionDetails == null ? 1 : (1 + collectionDetails.l()))
         + (ruleSet == null ? 1 : (1 + 32));
  }
}
