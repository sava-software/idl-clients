package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.NodePointer;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.QuoteLots;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::WithdrawStateTransition Borsh variant 36.
/// Payload type: WithdrawStateTransitionEvent.
///
public record WithdrawStateTransitionEvent(Discriminator discriminator,
                                           PublicKey trader,
                                           QuoteLots amount,
                                           int fromState,
                                           int toState,
                                           int reason,
                                           int transitionCount,
                                           NodePointer nodeIndex) implements EternalEvent {

  public static final int BYTES = 57;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(36, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int AMOUNT_OFFSET = 40;
  public static final int FROM_STATE_OFFSET = 48;
  public static final int TO_STATE_OFFSET = 49;
  public static final int REASON_OFFSET = 50;
  public static final int TRANSITION_COUNT_OFFSET = 51;
  public static final int NODE_INDEX_OFFSET = 53;

  public static WithdrawStateTransitionEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var amount = QuoteLots.read(_data, i);
    i += amount.l();
    final var fromState = _data[i] & 0xFF;
    ++i;
    final var toState = _data[i] & 0xFF;
    ++i;
    final var reason = _data[i] & 0xFF;
    ++i;
    final var transitionCount = getInt16LE(_data, i);
    i += 2;
    final var nodeIndex = NodePointer.read(_data, i);
    return new WithdrawStateTransitionEvent(discriminator,
                                            trader,
                                            amount,
                                            fromState,
                                            toState,
                                            reason,
                                            transitionCount,
                                            nodeIndex);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    i += amount.write(_data, i);
    _data[i] = (byte) fromState;
    ++i;
    _data[i] = (byte) toState;
    ++i;
    _data[i] = (byte) reason;
    ++i;
    putInt16LE(_data, i, transitionCount);
    i += 2;
    i += nodeIndex.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
