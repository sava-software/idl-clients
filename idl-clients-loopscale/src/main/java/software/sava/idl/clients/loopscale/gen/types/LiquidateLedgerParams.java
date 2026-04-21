package software.sava.idl.clients.loopscale.gen.types;

import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

public record LiquidateLedgerParams(int ledgerIndex,
                                    boolean unwrapSol,
                                    byte[] assetIndexGuidance) implements SerDe {

  public static final int LEDGER_INDEX_OFFSET = 0;
  public static final int UNWRAP_SOL_OFFSET = 1;
  public static final int ASSET_INDEX_GUIDANCE_OFFSET = 2;

  public static LiquidateLedgerParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var ledgerIndex = _data[i] & 0xFF;
    ++i;
    final var unwrapSol = _data[i] == 1;
    ++i;
    final var assetIndexGuidance = SerDeUtil.readbyteVector(4, _data, i);
    return new LiquidateLedgerParams(ledgerIndex, unwrapSol, assetIndexGuidance);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) ledgerIndex;
    ++i;
    _data[i] = (byte) (unwrapSol ? 1 : 0);
    ++i;
    i += SerDeUtil.writeVector(4, assetIndexGuidance, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 1 + 1 + SerDeUtil.lenVector(4, assetIndexGuidance);
  }
}
