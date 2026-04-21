package software.sava.idl.clients.loopscale.gen.events;

import java.lang.String;

import java.util.Arrays;

import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.loopscale.gen.types.TimelockUpdateParams;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record TimelockCreatedEvent(Discriminator discriminator,
                                   String timelockAddress, byte[] _timelockAddress,
                                   String vaultAddress, byte[] _vaultAddress,
                                   TimelockUpdateParams timelockParams,
                                   long timelockInitTimestamp,
                                   long timelockExecutionDelay) implements LoopscaleEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(152, 153, 7, 180, 31, 147, 228, 201);

  public static final int TIMELOCK_ADDRESS_OFFSET = 8;

  public static TimelockCreatedEvent createRecord(final Discriminator discriminator,
                                                  final String timelockAddress,
                                                  final String vaultAddress,
                                                  final TimelockUpdateParams timelockParams,
                                                  final long timelockInitTimestamp,
                                                  final long timelockExecutionDelay) {
    return new TimelockCreatedEvent(discriminator,
                                    timelockAddress, timelockAddress == null ? null : timelockAddress.getBytes(UTF_8),
                                    vaultAddress, vaultAddress == null ? null : vaultAddress.getBytes(UTF_8),
                                    timelockParams,
                                    timelockInitTimestamp,
                                    timelockExecutionDelay);
  }

  public static TimelockCreatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final int _timelockAddressLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _timelockAddress = Arrays.copyOfRange(_data, i, i + _timelockAddressLength);
    final var timelockAddress = new String(_timelockAddress, UTF_8);
    i += _timelockAddress.length;
    final int _vaultAddressLength = getInt32LE(_data, i);
    i += 4;
    final byte[] _vaultAddress = Arrays.copyOfRange(_data, i, i + _vaultAddressLength);
    final var vaultAddress = new String(_vaultAddress, UTF_8);
    i += _vaultAddress.length;
    final var timelockParams = TimelockUpdateParams.read(_data, i);
    i += timelockParams.l();
    final var timelockInitTimestamp = getInt64LE(_data, i);
    i += 8;
    final var timelockExecutionDelay = getInt64LE(_data, i);
    return new TimelockCreatedEvent(discriminator,
                                    timelockAddress, timelockAddress == null ? null : timelockAddress.getBytes(UTF_8),
                                    vaultAddress, vaultAddress == null ? null : vaultAddress.getBytes(UTF_8),
                                    timelockParams,
                                    timelockInitTimestamp,
                                    timelockExecutionDelay);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += SerDeUtil.writeVector(4, _timelockAddress, _data, i);
    i += SerDeUtil.writeVector(4, _vaultAddress, _data, i);
    i += timelockParams.write(_data, i);
    putInt64LE(_data, i, timelockInitTimestamp);
    i += 8;
    putInt64LE(_data, i, timelockExecutionDelay);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + _timelockAddress.length
         + _vaultAddress.length
         + timelockParams.l()
         + 8
         + 8;
  }
}
