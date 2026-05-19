package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

/// Borsh payload for authority-driven force cancellation by remaining side size.
///
public record AuthorizedForceCancelParams(BaseLots remainingBidBaseLots, BaseLots remainingAskBaseLots) implements SerDe {

  public static final int BYTES = 16;

  public static final int REMAINING_BID_BASE_LOTS_OFFSET = 0;
  public static final int REMAINING_ASK_BASE_LOTS_OFFSET = 8;

  public static AuthorizedForceCancelParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var remainingBidBaseLots = BaseLots.read(_data, i);
    i += remainingBidBaseLots.l();
    final var remainingAskBaseLots = BaseLots.read(_data, i);
    return new AuthorizedForceCancelParams(remainingBidBaseLots, remainingAskBaseLots);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += remainingBidBaseLots.write(_data, i);
    i += remainingAskBaseLots.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
