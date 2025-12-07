package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.borsh.Borsh;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;

public record DataV2(String name, byte[] _name,
                     String symbol, byte[] _symbol,
                     String uri, byte[] _uri,
                     int sellerFeeBasisPoints,
                     Creator[] creators,
                     Collection collection,
                     Uses uses) implements Borsh {

  public static DataV2 createRecord(final String name,
                                    final String symbol,
                                    final String uri,
                                    final int sellerFeeBasisPoints,
                                    final Creator[] creators,
                                    final Collection collection,
                                    final Uses uses) {
    return new DataV2(name, name.getBytes(UTF_8),
                      symbol, symbol.getBytes(UTF_8),
                      uri, uri.getBytes(UTF_8),
                      sellerFeeBasisPoints,
                      creators,
                      collection,
                      uses);
  }

  public static DataV2 read(final byte[] _data, final int _offset) {
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
      creators = Borsh.readVector(Creator.class, Creator::read, _data, i);
      i += Borsh.lenVector(creators);
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
    } else {
      ++i;
      uses = Uses.read(_data, i);
    }
    return new DataV2(name, _name,
                      symbol, _symbol,
                      uri, _uri,
                      sellerFeeBasisPoints,
                      creators,
                      collection,
                      uses);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += Borsh.writeVector(_name, _data, i);
    i += Borsh.writeVector(_symbol, _data, i);
    i += Borsh.writeVector(_uri, _data, i);
    putInt16LE(_data, i, sellerFeeBasisPoints);
    i += 2;
    if (creators == null || creators.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += Borsh.writeVector(creators, _data, i);
    }
    i += Borsh.writeOptional(collection, _data, i);
    i += Borsh.writeOptional(uses, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return _name.length
         + _symbol.length
         + _uri.length
         + 2
         + (creators == null || creators.length == 0 ? 1 : (1 + Borsh.lenVector(creators)))
         + (collection == null ? 1 : (1 + collection.l()))
         + (uses == null ? 1 : (1 + uses.l()));
  }
}
