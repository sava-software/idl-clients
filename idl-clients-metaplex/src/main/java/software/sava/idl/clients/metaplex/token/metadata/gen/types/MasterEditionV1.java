package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.OptionalLong;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record MasterEditionV1(PublicKey _address,
                              Key key,
                              long supply,
                              OptionalLong maxSupply,
                              PublicKey printingMint,
                              PublicKey oneTimePrintingAuthorizationMint) implements SerDe {

  public static final int KEY_OFFSET = 0;
  public static final int SUPPLY_OFFSET = 1;
  public static final int MAX_SUPPLY_OFFSET = 10;

  public static Filter createKeyFilter(final Key key) {
    return Filter.createMemCompFilter(KEY_OFFSET, key.write());
  }

  public static Filter createSupplyFilter(final long supply) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, supply);
    return Filter.createMemCompFilter(SUPPLY_OFFSET, _data);
  }

  public static Filter createMaxSupplyFilter(final long maxSupply) {
    final byte[] _data = new byte[9];
    _data[0] = 1;
    putInt64LE(_data, 1, maxSupply);
    return Filter.createMemCompFilter(MAX_SUPPLY_OFFSET, _data);
  }

  public static MasterEditionV1 read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static MasterEditionV1 read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static MasterEditionV1 read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], MasterEditionV1> FACTORY = MasterEditionV1::read;

  public static MasterEditionV1 read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = Key.read(_data, i);
    i += key.l();
    final var supply = getInt64LE(_data, i);
    i += 8;
    final OptionalLong maxSupply;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      maxSupply = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      maxSupply = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var printingMint = readPubKey(_data, i);
    i += 32;
    final var oneTimePrintingAuthorizationMint = readPubKey(_data, i);
    return new MasterEditionV1(_address,
                               key,
                               supply,
                               maxSupply,
                               printingMint,
                               oneTimePrintingAuthorizationMint);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += key.write(_data, i);
    putInt64LE(_data, i, supply);
    i += 8;
    i += SerDeUtil.writeOptional(1, maxSupply, _data, i);
    printingMint.write(_data, i);
    i += 32;
    oneTimePrintingAuthorizationMint.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return key.l()
         + 8
         + (maxSupply == null || maxSupply.isEmpty() ? 1 : (1 + 8))
         + 32
         + 32;
  }
}
