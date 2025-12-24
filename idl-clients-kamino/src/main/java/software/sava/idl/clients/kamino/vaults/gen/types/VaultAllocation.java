package software.sava.idl.clients.kamino.vaults.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// @param tokenAllocationCap Maximum token invested in this reserve
public record VaultAllocation(PublicKey reserve,
                              PublicKey ctokenVault,
                              long targetAllocationWeight,
                              long tokenAllocationCap,
                              long ctokenVaultBump,
                              long[] configPadding,
                              long ctokenAllocation,
                              long lastInvestSlot,
                              BigInteger tokenTargetAllocationSf,
                              long[] statePadding) implements SerDe {

  public static final int BYTES = 2160;
  public static final int CONFIG_PADDING_LEN = 127;
  public static final int STATE_PADDING_LEN = 128;

  public static VaultAllocation read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var reserve = readPubKey(_data, i);
    i += 32;
    final var ctokenVault = readPubKey(_data, i);
    i += 32;
    final var targetAllocationWeight = getInt64LE(_data, i);
    i += 8;
    final var tokenAllocationCap = getInt64LE(_data, i);
    i += 8;
    final var ctokenVaultBump = getInt64LE(_data, i);
    i += 8;
    final var configPadding = new long[127];
    i += SerDeUtil.readArray(configPadding, _data, i);
    final var ctokenAllocation = getInt64LE(_data, i);
    i += 8;
    final var lastInvestSlot = getInt64LE(_data, i);
    i += 8;
    final var tokenTargetAllocationSf = getInt128LE(_data, i);
    i += 16;
    final var statePadding = new long[128];
    SerDeUtil.readArray(statePadding, _data, i);
    return new VaultAllocation(reserve,
                               ctokenVault,
                               targetAllocationWeight,
                               tokenAllocationCap,
                               ctokenVaultBump,
                               configPadding,
                               ctokenAllocation,
                               lastInvestSlot,
                               tokenTargetAllocationSf,
                               statePadding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    reserve.write(_data, i);
    i += 32;
    ctokenVault.write(_data, i);
    i += 32;
    putInt64LE(_data, i, targetAllocationWeight);
    i += 8;
    putInt64LE(_data, i, tokenAllocationCap);
    i += 8;
    putInt64LE(_data, i, ctokenVaultBump);
    i += 8;
    i += SerDeUtil.writeArrayChecked(configPadding, 127, _data, i);
    putInt64LE(_data, i, ctokenAllocation);
    i += 8;
    putInt64LE(_data, i, lastInvestSlot);
    i += 8;
    putInt128LE(_data, i, tokenTargetAllocationSf);
    i += 16;
    i += SerDeUtil.writeArrayChecked(statePadding, 128, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
