package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param name UTF-8 encoded name of the token (null-terminated)
/// @param heuristic Heuristics limits of acceptable price
/// @param maxTwapDivergenceBps Max divergence between twap and price in bps
/// @param scopeConfiguration Scope price configuration
/// @param switchboardConfiguration Switchboard configuration
/// @param pythConfiguration Pyth configuration
public record TokenInfo(byte[] name,
                        PriceHeuristic heuristic,
                        long maxTwapDivergenceBps,
                        long maxAgePriceSeconds,
                        long maxAgeTwapSeconds,
                        ScopeConfiguration scopeConfiguration,
                        SwitchboardConfiguration switchboardConfiguration,
                        PythConfiguration pythConfiguration,
                        int blockPriceUsage,
                        byte[] reserved,
                        long[] padding) implements SerDe {

  public static final int BYTES = 384;
  public static final int NAME_LEN = 32;
  public static final int RESERVED_LEN = 7;
  public static final int PADDING_LEN = 19;

  public static final int NAME_OFFSET = 0;
  public static final int HEURISTIC_OFFSET = 32;
  public static final int MAX_TWAP_DIVERGENCE_BPS_OFFSET = 56;
  public static final int MAX_AGE_PRICE_SECONDS_OFFSET = 64;
  public static final int MAX_AGE_TWAP_SECONDS_OFFSET = 72;
  public static final int SCOPE_CONFIGURATION_OFFSET = 80;
  public static final int SWITCHBOARD_CONFIGURATION_OFFSET = 128;
  public static final int PYTH_CONFIGURATION_OFFSET = 192;
  public static final int BLOCK_PRICE_USAGE_OFFSET = 224;
  public static final int RESERVED_OFFSET = 225;
  public static final int PADDING_OFFSET = 232;

  public static TokenInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var name = new byte[32];
    i += SerDeUtil.readArray(name, _data, i);
    final var heuristic = PriceHeuristic.read(_data, i);
    i += heuristic.l();
    final var maxTwapDivergenceBps = getInt64LE(_data, i);
    i += 8;
    final var maxAgePriceSeconds = getInt64LE(_data, i);
    i += 8;
    final var maxAgeTwapSeconds = getInt64LE(_data, i);
    i += 8;
    final var scopeConfiguration = ScopeConfiguration.read(_data, i);
    i += scopeConfiguration.l();
    final var switchboardConfiguration = SwitchboardConfiguration.read(_data, i);
    i += switchboardConfiguration.l();
    final var pythConfiguration = PythConfiguration.read(_data, i);
    i += pythConfiguration.l();
    final var blockPriceUsage = _data[i] & 0xFF;
    ++i;
    final var reserved = new byte[7];
    i += SerDeUtil.readArray(reserved, _data, i);
    final var padding = new long[19];
    SerDeUtil.readArray(padding, _data, i);
    return new TokenInfo(name,
                         heuristic,
                         maxTwapDivergenceBps,
                         maxAgePriceSeconds,
                         maxAgeTwapSeconds,
                         scopeConfiguration,
                         switchboardConfiguration,
                         pythConfiguration,
                         blockPriceUsage,
                         reserved,
                         padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += SerDeUtil.writeArrayChecked(name, 32, _data, i);
    i += heuristic.write(_data, i);
    putInt64LE(_data, i, maxTwapDivergenceBps);
    i += 8;
    putInt64LE(_data, i, maxAgePriceSeconds);
    i += 8;
    putInt64LE(_data, i, maxAgeTwapSeconds);
    i += 8;
    i += scopeConfiguration.write(_data, i);
    i += switchboardConfiguration.write(_data, i);
    i += pythConfiguration.write(_data, i);
    _data[i] = (byte) blockPriceUsage;
    ++i;
    i += SerDeUtil.writeArrayChecked(reserved, 7, _data, i);
    i += SerDeUtil.writeArrayChecked(padding, 19, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
