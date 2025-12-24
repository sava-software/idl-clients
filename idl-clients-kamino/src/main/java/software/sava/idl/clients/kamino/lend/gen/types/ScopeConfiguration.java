package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

/// @param priceFeed Pubkey of the scope price feed (disabled if `null` or `default`)
/// @param priceChain This is the scope_id price chain that results in a price for the token
/// @param twapChain This is the scope_id price chain for the twap
public record ScopeConfiguration(PublicKey priceFeed,
                                 short[] priceChain,
                                 short[] twapChain) implements SerDe {

  public static final int BYTES = 48;
  public static final int PRICE_CHAIN_LEN = 4;
  public static final int TWAP_CHAIN_LEN = 4;

  public static ScopeConfiguration read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var priceFeed = readPubKey(_data, i);
    i += 32;
    final var priceChain = new short[4];
    i += SerDeUtil.readArray(priceChain, _data, i);
    final var twapChain = new short[4];
    SerDeUtil.readArray(twapChain, _data, i);
    return new ScopeConfiguration(priceFeed, priceChain, twapChain);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    priceFeed.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(priceChain, 4, _data, i);
    i += SerDeUtil.writeArrayChecked(twapChain, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
