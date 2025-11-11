package software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;

import java.lang.Boolean;

import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public record PullFeedSetConfigsParams(byte[] feedHash,
                                       PublicKey authority,
                                       OptionalLong maxVariance,
                                       OptionalInt minResponses,
                                       byte[] name,
                                       byte[] ipfsHash,
                                       OptionalInt minSampleSize,
                                       OptionalInt maxStaleness,
                                       Boolean permitWriteByAuthority) implements Borsh {

  public static PullFeedSetConfigsParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final byte[] feedHash;
    if (_data[i] == 0) {
      feedHash = null;
      ++i;
    } else {
      ++i;
      feedHash = new byte[32];
      i += Borsh.readArray(feedHash, _data, i);
    }
    final PublicKey authority;
    if (_data[i] == 0) {
      authority = null;
      ++i;
    } else {
      ++i;
      authority = readPubKey(_data, i);
      i += 32;
    }
    final OptionalLong maxVariance;
    if (_data[i] == 0) {
      maxVariance = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxVariance = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final OptionalInt minResponses;
    if (_data[i] == 0) {
      minResponses = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      minResponses = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final byte[] name;
    if (_data[i] == 0) {
      name = null;
      ++i;
    } else {
      ++i;
      name = new byte[32];
      i += Borsh.readArray(name, _data, i);
    }
    final byte[] ipfsHash;
    if (_data[i] == 0) {
      ipfsHash = null;
      ++i;
    } else {
      ++i;
      ipfsHash = new byte[32];
      i += Borsh.readArray(ipfsHash, _data, i);
    }
    final OptionalInt minSampleSize;
    if (_data[i] == 0) {
      minSampleSize = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      minSampleSize = OptionalInt.of(_data[i] & 0xFF);
      ++i;
    }
    final OptionalInt maxStaleness;
    if (_data[i] == 0) {
      maxStaleness = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      maxStaleness = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final Boolean permitWriteByAuthority;
    if (_data[i] == 0) {
      permitWriteByAuthority = null;
    } else {
      ++i;
      permitWriteByAuthority = _data[i] == 1;
    }
    return new PullFeedSetConfigsParams(feedHash,
                                        authority,
                                        maxVariance,
                                        minResponses,
                                        name,
                                        ipfsHash,
                                        minSampleSize,
                                        maxStaleness,
                                        permitWriteByAuthority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    if (feedHash == null || feedHash.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += Borsh.writeArrayChecked(feedHash, 32, _data, i);
    }
    i += Borsh.writeOptional(authority, _data, i);
    i += Borsh.writeOptional(maxVariance, _data, i);
    i += Borsh.writeOptional(minResponses, _data, i);
    if (name == null || name.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += Borsh.writeArrayChecked(name, 32, _data, i);
    }
    if (ipfsHash == null || ipfsHash.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += Borsh.writeArrayChecked(ipfsHash, 32, _data, i);
    }
    i += Borsh.writeOptionalbyte(minSampleSize, _data, i);
    i += Borsh.writeOptional(maxStaleness, _data, i);
    i += Borsh.writeOptional(permitWriteByAuthority, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return (feedHash == null || feedHash.length == 0 ? 1 : (1 + Borsh.lenArray(feedHash)))
         + (authority == null ? 1 : (1 + 32))
         + (maxVariance == null || maxVariance.isEmpty() ? 1 : (1 + 8))
         + (minResponses == null || minResponses.isEmpty() ? 1 : (1 + 4))
         + (name == null || name.length == 0 ? 1 : (1 + Borsh.lenArray(name)))
         + (ipfsHash == null || ipfsHash.length == 0 ? 1 : (1 + Borsh.lenArray(ipfsHash)))
         + (minSampleSize == null || minSampleSize.isEmpty() ? 1 : (1 + 1))
         + (maxStaleness == null || maxStaleness.isEmpty() ? 1 : (1 + 4))
         + (permitWriteByAuthority == null ? 1 : (1 + 1));
  }
}
