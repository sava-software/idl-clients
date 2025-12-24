package software.sava.idl.clients.kamino.scope.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Map of mints to scope chain only valid for a given price feed
///
public record MintsToScopeChains(PublicKey _address,
                                 Discriminator discriminator,
                                 PublicKey oraclePrices,
                                 PublicKey seedPk,
                                 long seedId,
                                 int bump,
                                 MintToScopeChain[] mapping) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(156, 236, 56, 20, 39, 141, 42, 183);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int ORACLE_PRICES_OFFSET = 8;
  public static final int SEED_PK_OFFSET = 40;
  public static final int SEED_ID_OFFSET = 72;
  public static final int BUMP_OFFSET = 80;
  public static final int MAPPING_OFFSET = 81;

  public static Filter createOraclePricesFilter(final PublicKey oraclePrices) {
    return Filter.createMemCompFilter(ORACLE_PRICES_OFFSET, oraclePrices);
  }

  public static Filter createSeedPkFilter(final PublicKey seedPk) {
    return Filter.createMemCompFilter(SEED_PK_OFFSET, seedPk);
  }

  public static Filter createSeedIdFilter(final long seedId) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, seedId);
    return Filter.createMemCompFilter(SEED_ID_OFFSET, _data);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static MintsToScopeChains read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static MintsToScopeChains read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static MintsToScopeChains read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], MintsToScopeChains> FACTORY = MintsToScopeChains::read;

  public static MintsToScopeChains read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var oraclePrices = readPubKey(_data, i);
    i += 32;
    final var seedPk = readPubKey(_data, i);
    i += 32;
    final var seedId = getInt64LE(_data, i);
    i += 8;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var mapping = SerDeUtil.readVector(4, MintToScopeChain.class, MintToScopeChain::read, _data, i);
    return new MintsToScopeChains(_address,
                                  discriminator,
                                  oraclePrices,
                                  seedPk,
                                  seedId,
                                  bump,
                                  mapping);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    oraclePrices.write(_data, i);
    i += 32;
    seedPk.write(_data, i);
    i += 32;
    putInt64LE(_data, i, seedId);
    i += 8;
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeVector(4, mapping, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 8
         + 1
         + SerDeUtil.lenVector(4, mapping);
  }
}
