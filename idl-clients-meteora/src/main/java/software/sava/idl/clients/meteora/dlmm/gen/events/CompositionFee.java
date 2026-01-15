package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record CompositionFee(Discriminator discriminator,
                             PublicKey from,
                             int binId,
                             long tokenXFeeAmount,
                             long tokenYFeeAmount,
                             long protocolTokenXFeeAmount,
                             long protocolTokenYFeeAmount) implements LbClmmEvent {

  public static final int BYTES = 74;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(128, 151, 123, 106, 17, 102, 113, 142);

  public static final int FROM_OFFSET = 8;
  public static final int BIN_ID_OFFSET = 40;
  public static final int TOKEN_X_FEE_AMOUNT_OFFSET = 42;
  public static final int TOKEN_Y_FEE_AMOUNT_OFFSET = 50;
  public static final int PROTOCOL_TOKEN_X_FEE_AMOUNT_OFFSET = 58;
  public static final int PROTOCOL_TOKEN_Y_FEE_AMOUNT_OFFSET = 66;

  public static CompositionFee read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var from = readPubKey(_data, i);
    i += 32;
    final var binId = getInt16LE(_data, i);
    i += 2;
    final var tokenXFeeAmount = getInt64LE(_data, i);
    i += 8;
    final var tokenYFeeAmount = getInt64LE(_data, i);
    i += 8;
    final var protocolTokenXFeeAmount = getInt64LE(_data, i);
    i += 8;
    final var protocolTokenYFeeAmount = getInt64LE(_data, i);
    return new CompositionFee(discriminator,
                              from,
                              binId,
                              tokenXFeeAmount,
                              tokenYFeeAmount,
                              protocolTokenXFeeAmount,
                              protocolTokenYFeeAmount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    from.write(_data, i);
    i += 32;
    putInt16LE(_data, i, binId);
    i += 2;
    putInt64LE(_data, i, tokenXFeeAmount);
    i += 8;
    putInt64LE(_data, i, tokenYFeeAmount);
    i += 8;
    putInt64LE(_data, i, protocolTokenXFeeAmount);
    i += 8;
    putInt64LE(_data, i, protocolTokenYFeeAmount);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
