package software.sava.idl.clients.meteora.dlmm.gen.events;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Swap(Discriminator discriminator,
                   PublicKey lbPair,
                   PublicKey from,
                   int startBinId,
                   int endBinId,
                   long amountIn,
                   long amountOut,
                   boolean swapForY,
                   long fee,
                   long protocolFee,
                   BigInteger feeBps,
                   long hostFee) implements LbClmmEvent {

  public static final int BYTES = 137;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(81, 108, 227, 190, 205, 208, 10, 196);

  public static final int LB_PAIR_OFFSET = 8;
  public static final int FROM_OFFSET = 40;
  public static final int START_BIN_ID_OFFSET = 72;
  public static final int END_BIN_ID_OFFSET = 76;
  public static final int AMOUNT_IN_OFFSET = 80;
  public static final int AMOUNT_OUT_OFFSET = 88;
  public static final int SWAP_FOR_Y_OFFSET = 96;
  public static final int FEE_OFFSET = 97;
  public static final int PROTOCOL_FEE_OFFSET = 105;
  public static final int FEE_BPS_OFFSET = 113;
  public static final int HOST_FEE_OFFSET = 129;

  public static Swap read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var from = readPubKey(_data, i);
    i += 32;
    final var startBinId = getInt32LE(_data, i);
    i += 4;
    final var endBinId = getInt32LE(_data, i);
    i += 4;
    final var amountIn = getInt64LE(_data, i);
    i += 8;
    final var amountOut = getInt64LE(_data, i);
    i += 8;
    final var swapForY = _data[i] == 1;
    ++i;
    final var fee = getInt64LE(_data, i);
    i += 8;
    final var protocolFee = getInt64LE(_data, i);
    i += 8;
    final var feeBps = getInt128LE(_data, i);
    i += 16;
    final var hostFee = getInt64LE(_data, i);
    return new Swap(discriminator,
                    lbPair,
                    from,
                    startBinId,
                    endBinId,
                    amountIn,
                    amountOut,
                    swapForY,
                    fee,
                    protocolFee,
                    feeBps,
                    hostFee);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    from.write(_data, i);
    i += 32;
    putInt32LE(_data, i, startBinId);
    i += 4;
    putInt32LE(_data, i, endBinId);
    i += 4;
    putInt64LE(_data, i, amountIn);
    i += 8;
    putInt64LE(_data, i, amountOut);
    i += 8;
    _data[i] = (byte) (swapForY ? 1 : 0);
    ++i;
    putInt64LE(_data, i, fee);
    i += 8;
    putInt64LE(_data, i, protocolFee);
    i += 8;
    putInt128LE(_data, i, feeBps);
    i += 16;
    putInt64LE(_data, i, hostFee);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
