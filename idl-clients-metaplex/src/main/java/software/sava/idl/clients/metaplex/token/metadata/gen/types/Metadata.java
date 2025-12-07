package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.OptionalInt;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record Metadata(PublicKey _address,
                       Key key,
                       PublicKey updateAuthority,
                       PublicKey mint,
                       Data data,
                       boolean primarySaleHappened,
                       boolean isMutable,
                       OptionalInt editionNonce,
                       TokenStandard tokenStandard,
                       Collection collection,
                       Uses uses,
                       CollectionDetails collectionDetails,
                       ProgrammableConfig programmableConfig) implements Borsh {

  public static final int KEY_OFFSET = 0;
  public static final int UPDATE_AUTHORITY_OFFSET = 1;
  public static final int MINT_OFFSET = 33;
  public static final int DATA_OFFSET = 65;

  public static Filter createKeyFilter(final Key key) {
    return Filter.createMemCompFilter(KEY_OFFSET, key.write());
  }

  public static Filter createUpdateAuthorityFilter(final PublicKey updateAuthority) {
    return Filter.createMemCompFilter(UPDATE_AUTHORITY_OFFSET, updateAuthority);
  }

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Metadata read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Metadata read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Metadata read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Metadata> FACTORY = Metadata::read;

  public static Metadata read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = Key.read(_data, i);
    i += key.l();
    final var updateAuthority = readPubKey(_data, i);
    i += 32;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var data = Data.read(_data, i);
    i += data.l();
    final var primarySaleHappened = _data[i] == 1;
    ++i;
    final var isMutable = _data[i] == 1;
    ++i;
    final OptionalInt editionNonce;
    if (_data[i] == 0) {
      editionNonce = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      editionNonce = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final TokenStandard tokenStandard;
    if (_data[i] == 0) {
      tokenStandard = null;
      ++i;
    } else {
      ++i;
      tokenStandard = TokenStandard.read(_data, i);
      i += tokenStandard.l();
    }
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
    final ProgrammableConfig programmableConfig;
    if (_data[i] == 0) {
      programmableConfig = null;
    } else {
      ++i;
      programmableConfig = ProgrammableConfig.read(_data, i);
    }
    return new Metadata(_address,
                        key,
                        updateAuthority,
                        mint,
                        data,
                        primarySaleHappened,
                        isMutable,
                        editionNonce,
                        tokenStandard,
                        collection,
                        uses,
                        collectionDetails,
                        programmableConfig);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += key.write(_data, i);
    updateAuthority.write(_data, i);
    i += 32;
    mint.write(_data, i);
    i += 32;
    i += data.write(_data, i);
    _data[i] = (byte) (primarySaleHappened ? 1 : 0);
    ++i;
    _data[i] = (byte) (isMutable ? 1 : 0);
    ++i;
    i += Borsh.writeOptionalbyte(editionNonce, _data, i);
    i += Borsh.writeOptional(tokenStandard, _data, i);
    i += Borsh.writeOptional(collection, _data, i);
    i += Borsh.writeOptional(uses, _data, i);
    i += Borsh.writeOptional(collectionDetails, _data, i);
    i += Borsh.writeOptional(programmableConfig, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return key.l()
         + 32
         + 32
         + data.l()
         + 1
         + 1
         + (editionNonce == null || editionNonce.isEmpty() ? 1 : (1 + 1))
         + (tokenStandard == null ? 1 : (1 + tokenStandard.l()))
         + (collection == null ? 1 : (1 + collection.l()))
         + (uses == null ? 1 : (1 + uses.l()))
         + (collectionDetails == null ? 1 : (1 + collectionDetails.l()))
         + (programmableConfig == null ? 1 : (1 + programmableConfig.l()));
  }
}
