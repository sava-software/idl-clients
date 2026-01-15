package software.sava.idl.clients.drift.vaults.gen.events;

import software.sava.core.programs.Discriminator;

import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record VaultRecord(Discriminator discriminator,
                          long ts,
                          int spotMarketIndex,
                          long vaultEquityBefore) implements DriftVaultsEvent {

  public static final int BYTES = 26;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(138, 224, 172, 3, 244, 19, 253, 232);

  public static final int TS_OFFSET = 8;
  public static final int SPOT_MARKET_INDEX_OFFSET = 16;
  public static final int VAULT_EQUITY_BEFORE_OFFSET = 18;

  public static VaultRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var spotMarketIndex = getInt16LE(_data, i);
    i += 2;
    final var vaultEquityBefore = getInt64LE(_data, i);
    return new VaultRecord(discriminator, ts, spotMarketIndex, vaultEquityBefore);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt64LE(_data, i, vaultEquityBefore);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
