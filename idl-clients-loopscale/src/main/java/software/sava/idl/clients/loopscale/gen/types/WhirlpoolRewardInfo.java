package software.sava.idl.clients.loopscale.gen.types;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;

/// Stores the state relevant for tracking liquidity mining rewards at the `Whirlpool` level.
/// These values are used in conjunction with `PositionRewardInfo`, `Tick.reward_growths_outside`,
/// and `Whirlpool.reward_last_updated_timestamp` to determine how many rewards are earned by open
/// positions.
///
/// @param mint Reward token mint.
/// @param vault Reward vault token account.
/// @param authority Authority account that has permission to initialize the reward and set emissions.
/// @param emissionsPerSecondX64 Q64.64 number that indicates how many tokens per second are earned per unit of liquidity.
/// @param growthGlobalX64 Q64.64 number that tracks the total tokens earned per unit of liquidity since the reward
///                        emissions were turned on.
public record WhirlpoolRewardInfo(PublicKey mint,
                                  PublicKey vault,
                                  PublicKey authority,
                                  BigInteger emissionsPerSecondX64,
                                  BigInteger growthGlobalX64) implements SerDe {

  public static final int BYTES = 128;

  public static final int MINT_OFFSET = 0;
  public static final int VAULT_OFFSET = 32;
  public static final int AUTHORITY_OFFSET = 64;
  public static final int EMISSIONS_PER_SECOND_X_66_OFFSET = 96;
  public static final int GROWTH_GLOBAL_X_66_OFFSET = 112;

  public static WhirlpoolRewardInfo read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var vault = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var emissionsPerSecondX64 = getInt128LE(_data, i);
    i += 16;
    final var growthGlobalX64 = getInt128LE(_data, i);
    return new WhirlpoolRewardInfo(mint,
                                   vault,
                                   authority,
                                   emissionsPerSecondX64,
                                   growthGlobalX64);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mint.write(_data, i);
    i += 32;
    vault.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    putInt128LE(_data, i, emissionsPerSecondX64);
    i += 16;
    putInt128LE(_data, i, growthGlobalX64);
    i += 16;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
