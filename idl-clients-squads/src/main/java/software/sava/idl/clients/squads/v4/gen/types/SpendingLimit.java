package software.sava.idl.clients.squads.v4.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param multisig The multisig this belongs to.
/// @param createKey Key that is used to seed the SpendingLimit PDA.
/// @param vaultIndex The index of the vault that the spending limit is for.
/// @param mint The token mint the spending limit is for.
///             Pubkey::default() means SOL.
///             use NATIVE_MINT for Wrapped SOL.
/// @param amount The amount of tokens that can be spent in a period.
///               This amount is in decimals of the mint,
///               so 1 SOL would be `1_000_000_000` and 1 USDC would be `1_000_000`.
/// @param period The reset period of the spending limit.
///               When it passes, the remaining amount is reset, unless it's `Period::OneTime`.
/// @param remainingAmount The remaining amount of tokens that can be spent in the current period.
///                        When reaches 0, the spending limit cannot be used anymore until the period reset.
/// @param lastReset Unix timestamp marking the last time the spending limit was reset (or created).
/// @param bump PDA bump.
/// @param members Members of the multisig that can use the spending limit.
///                In case a member is removed from the multisig, the spending limit will remain existent
///                (until explicitly deleted), but the removed member will not be able to use it anymore.
/// @param destinations The destination addresses the spending limit is allowed to sent funds to.
///                     If empty, funds can be sent to any address.
public record SpendingLimit(PublicKey _address,
                            Discriminator discriminator,
                            PublicKey multisig,
                            PublicKey createKey,
                            int vaultIndex,
                            PublicKey mint,
                            long amount,
                            Period period,
                            long remainingAmount,
                            long lastReset,
                            int bump,
                            PublicKey[] members,
                            PublicKey[] destinations) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(10, 201, 27, 160, 218, 195, 222, 152);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MULTISIG_OFFSET = 8;
  public static final int CREATE_KEY_OFFSET = 40;
  public static final int VAULT_INDEX_OFFSET = 72;
  public static final int MINT_OFFSET = 73;
  public static final int AMOUNT_OFFSET = 105;
  public static final int PERIOD_OFFSET = 113;
  public static final int REMAINING_AMOUNT_OFFSET = 114;
  public static final int LAST_RESET_OFFSET = 122;
  public static final int BUMP_OFFSET = 130;
  public static final int MEMBERS_OFFSET = 131;

  public static Filter createMultisigFilter(final PublicKey multisig) {
    return Filter.createMemCompFilter(MULTISIG_OFFSET, multisig);
  }

  public static Filter createCreateKeyFilter(final PublicKey createKey) {
    return Filter.createMemCompFilter(CREATE_KEY_OFFSET, createKey);
  }

  public static Filter createVaultIndexFilter(final int vaultIndex) {
    return Filter.createMemCompFilter(VAULT_INDEX_OFFSET, new byte[]{(byte) vaultIndex});
  }

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createAmountFilter(final long amount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, amount);
    return Filter.createMemCompFilter(AMOUNT_OFFSET, _data);
  }

  public static Filter createPeriodFilter(final Period period) {
    return Filter.createMemCompFilter(PERIOD_OFFSET, period.write());
  }

  public static Filter createRemainingAmountFilter(final long remainingAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, remainingAmount);
    return Filter.createMemCompFilter(REMAINING_AMOUNT_OFFSET, _data);
  }

  public static Filter createLastResetFilter(final long lastReset) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastReset);
    return Filter.createMemCompFilter(LAST_RESET_OFFSET, _data);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static SpendingLimit read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static SpendingLimit read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static SpendingLimit read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], SpendingLimit> FACTORY = SpendingLimit::read;

  public static SpendingLimit read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var multisig = readPubKey(_data, i);
    i += 32;
    final var createKey = readPubKey(_data, i);
    i += 32;
    final var vaultIndex = _data[i] & 0xFF;
    ++i;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var amount = getInt64LE(_data, i);
    i += 8;
    final var period = Period.read(_data, i);
    i += period.l();
    final var remainingAmount = getInt64LE(_data, i);
    i += 8;
    final var lastReset = getInt64LE(_data, i);
    i += 8;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var members = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, members);
    final var destinations = SerDeUtil.readPublicKeyVector(4, _data, i);
    return new SpendingLimit(_address,
                             discriminator,
                             multisig,
                             createKey,
                             vaultIndex,
                             mint,
                             amount,
                             period,
                             remainingAmount,
                             lastReset,
                             bump,
                             members,
                             destinations);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    multisig.write(_data, i);
    i += 32;
    createKey.write(_data, i);
    i += 32;
    _data[i] = (byte) vaultIndex;
    ++i;
    mint.write(_data, i);
    i += 32;
    putInt64LE(_data, i, amount);
    i += 8;
    i += period.write(_data, i);
    putInt64LE(_data, i, remainingAmount);
    i += 8;
    putInt64LE(_data, i, lastReset);
    i += 8;
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeVector(4, members, _data, i);
    i += SerDeUtil.writeVector(4, destinations, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 1
         + 32
         + 8
         + period.l()
         + 8
         + 8
         + 1
         + SerDeUtil.lenVector(4, members)
         + SerDeUtil.lenVector(4, destinations);
  }
}
