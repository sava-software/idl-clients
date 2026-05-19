package software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.phoenix.dev.perpetuals.gen.types.Symbol;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::Header Borsh variant 1.
/// Payload type: MarketEventHeader.
///
public record MarketEventHeader(Discriminator discriminator,
                                long sequenceNumber,
                                long prevSequenceNumberSlot,
                                Symbol assetSymbol,
                                int assetId,
                                int tickSize,
                                int baseLotDecimals,
                                int quoteLotDecimals,
                                PublicKey signer,
                                PublicKey traderAccount) implements EternalEvent {

  public static final int BYTES = 114;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(1, 0, 0, 0, 0, 0, 0, 0);

  public static final int SEQUENCE_NUMBER_OFFSET = 8;
  public static final int PREV_SEQUENCE_NUMBER_SLOT_OFFSET = 16;
  public static final int ASSET_SYMBOL_OFFSET = 24;
  public static final int ASSET_ID_OFFSET = 40;
  public static final int TICK_SIZE_OFFSET = 44;
  public static final int BASE_LOT_DECIMALS_OFFSET = 48;
  public static final int QUOTE_LOT_DECIMALS_OFFSET = 49;
  public static final int SIGNER_OFFSET = 50;
  public static final int TRADER_ACCOUNT_OFFSET = 82;

  public static MarketEventHeader read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var sequenceNumber = getInt64LE(_data, i);
    i += 8;
    final var prevSequenceNumberSlot = getInt64LE(_data, i);
    i += 8;
    final var assetSymbol = Symbol.read(_data, i);
    i += assetSymbol.l();
    final var assetId = getInt32LE(_data, i);
    i += 4;
    final var tickSize = getInt32LE(_data, i);
    i += 4;
    final var baseLotDecimals = _data[i];
    ++i;
    final var quoteLotDecimals = _data[i] & 0xFF;
    ++i;
    final var signer = readPubKey(_data, i);
    i += 32;
    final var traderAccount = readPubKey(_data, i);
    return new MarketEventHeader(discriminator,
                                 sequenceNumber,
                                 prevSequenceNumberSlot,
                                 assetSymbol,
                                 assetId,
                                 tickSize,
                                 baseLotDecimals,
                                 quoteLotDecimals,
                                 signer,
                                 traderAccount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, sequenceNumber);
    i += 8;
    putInt64LE(_data, i, prevSequenceNumberSlot);
    i += 8;
    i += assetSymbol.write(_data, i);
    putInt32LE(_data, i, assetId);
    i += 4;
    putInt32LE(_data, i, tickSize);
    i += 4;
    _data[i] = (byte) baseLotDecimals;
    ++i;
    _data[i] = (byte) quoteLotDecimals;
    ++i;
    signer.write(_data, i);
    i += 32;
    traderAccount.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
