package software.sava.idl.clients.meteora.dlmm.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param filterPeriod Filter period determine high frequency trading time window.
/// @param decayPeriod Decay period determine when the volatile fee start decay / decrease.
/// @param reductionFactor Reduction factor controls the volatile fee rate decrement rate.
/// @param variableFeeControl Used to scale the variable fee component depending on the dynamic of the market
/// @param maxVolatilityAccumulator Maximum number of bin crossed can be accumulated. Used to cap volatile fee rate.
public record DynamicFeeParameterUpdate(Discriminator discriminator,
                                        PublicKey lbPair,
                                        int filterPeriod,
                                        int decayPeriod,
                                        int reductionFactor,
                                        int variableFeeControl,
                                        int maxVolatilityAccumulator) implements LbClmmEvent {

  public static final int BYTES = 54;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(88, 88, 178, 135, 194, 146, 91, 243);

  public static DynamicFeeParameterUpdate read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var lbPair = readPubKey(_data, i);
    i += 32;
    final var filterPeriod = getInt16LE(_data, i);
    i += 2;
    final var decayPeriod = getInt16LE(_data, i);
    i += 2;
    final var reductionFactor = getInt16LE(_data, i);
    i += 2;
    final var variableFeeControl = getInt32LE(_data, i);
    i += 4;
    final var maxVolatilityAccumulator = getInt32LE(_data, i);
    return new DynamicFeeParameterUpdate(discriminator,
                                         lbPair,
                                         filterPeriod,
                                         decayPeriod,
                                         reductionFactor,
                                         variableFeeControl,
                                         maxVolatilityAccumulator);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    lbPair.write(_data, i);
    i += 32;
    putInt16LE(_data, i, filterPeriod);
    i += 2;
    putInt16LE(_data, i, decayPeriod);
    i += 2;
    putInt16LE(_data, i, reductionFactor);
    i += 2;
    putInt32LE(_data, i, variableFeeControl);
    i += 4;
    putInt32LE(_data, i, maxVolatilityAccumulator);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
