package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.BaseLots;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.ConditionalOrderPingSnapshot;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::PingActivated Borsh variant 62.
/// Payload type: PingActivatedEvent.
///
public record PingActivatedEvent(Discriminator discriminator,
                                 PublicKey trader,
                                 long sequenceNumber,
                                 long prevSequenceNumberSlot,
                                 long assetId,
                                 int conditionalOrderIndex,
                                 ConditionalOrderPingSnapshot pre,
                                 ConditionalOrderPingSnapshot post,
                                 BaseLots bookOrderRemainingBaseLots) implements EternalEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(62, 0, 0, 0, 0, 0, 0, 0);

  public static final int TRADER_OFFSET = 8;
  public static final int SEQUENCE_NUMBER_OFFSET = 40;
  public static final int PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 48;
  public static final int ASSET_ID_OFFSET = 56;
  public static final int CONDITIONAL_ORDER_INDEX_OFFSET = 64;
  public static final int PRE_OFFSET = 65;

  public static PingActivatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var trader = readPubKey(_data, i);
    i += 32;
    final var sequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var prevSequenceNumberSlot = getInt64LE(_data, i);
    i += 8;
    final var assetId = getInt64LE(_data, i);
    i += 8;
    final var conditionalOrderIndex = _data[i] & 0xFF;
    ++i;
    final var pre = ConditionalOrderPingSnapshot.read(_data, i);
    i += pre.l();
    final var post = ConditionalOrderPingSnapshot.read(_data, i);
    i += post.l();
    final BaseLots bookOrderRemainingBaseLots;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      bookOrderRemainingBaseLots = null;
    } else {
      ++i;
      bookOrderRemainingBaseLots = BaseLots.read(_data, i);
    }
    return new PingActivatedEvent(discriminator,
                                  trader,
                                  sequenceNumber,
                                  prevSequenceNumberSlot,
                                  assetId,
                                  conditionalOrderIndex,
                                  pre,
                                  post,
                                  bookOrderRemainingBaseLots);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    trader.write(_data, i);
    i += 32;
    putInt64LE(_data, i, sequenceNumber);
    i += 8;
    putInt64LE(_data, i, prevSequenceNumberSlot);
    i += 8;
    putInt64LE(_data, i, assetId);
    i += 8;
    _data[i] = (byte) conditionalOrderIndex;
    ++i;
    i += pre.write(_data, i);
    i += post.write(_data, i);
    i += SerDeUtil.writeOptional(1, bookOrderRemainingBaseLots, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 8
         + 8
         + 8
         + 1
         + pre.l()
         + post.l()
         + (bookOrderRemainingBaseLots == null ? 1 : (1 + bookOrderRemainingBaseLots.l()));
  }
}
