package software.sava.idl.clients.drift.vaults.gen.types;

import java.math.BigInteger;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param vault The vault deposited into
/// @param pubkey The vault depositor account's pubkey. It is a pda of vault and authority
/// @param authority The authority is the address w permission to deposit/withdraw
/// @param vaultShares share of vault owned by this depositor. vault_shares / vault.total_shares is depositor's ownership of vault_equity
/// @param lastWithdrawRequest last withdraw request
/// @param lastValidTs creation ts of vault depositor
/// @param netDeposits lifetime net deposits of vault depositor for the vault
/// @param totalDeposits lifetime total deposits
/// @param totalWithdraws lifetime total withdraws
/// @param cumulativeProfitShareAmount the token amount of gains the vault depositor has paid performance fees on
/// @param vaultSharesBase the exponent for vault_shares decimal places
/// @param cumulativeFuelPerShareAmount precision: FUEL_SHARE_PRECISION
/// @param fuelAmount precision: none
public record VaultDepositor(PublicKey _address,
                             Discriminator discriminator,
                             PublicKey vault,
                             PublicKey pubkey,
                             PublicKey authority,
                             BigInteger vaultShares,
                             WithdrawRequest lastWithdrawRequest,
                             long lastValidTs,
                             long netDeposits,
                             long totalDeposits,
                             long totalWithdraws,
                             long cumulativeProfitShareAmount,
                             long profitShareFeePaid,
                             int vaultSharesBase,
                             int lastFuelUpdateTs,
                             BigInteger cumulativeFuelPerShareAmount,
                             BigInteger fuelAmount,
                             long[] padding) implements SerDe {

  public static final int BYTES = 272;
  public static final int PADDING_LEN = 4;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(87, 109, 182, 106, 87, 96, 63, 211);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int VAULT_OFFSET = 8;
  public static final int PUBKEY_OFFSET = 40;
  public static final int AUTHORITY_OFFSET = 72;
  public static final int VAULT_SHARES_OFFSET = 104;
  public static final int LAST_WITHDRAW_REQUEST_OFFSET = 120;
  public static final int LAST_VALID_TS_OFFSET = 152;
  public static final int NET_DEPOSITS_OFFSET = 160;
  public static final int TOTAL_DEPOSITS_OFFSET = 168;
  public static final int TOTAL_WITHDRAWS_OFFSET = 176;
  public static final int CUMULATIVE_PROFIT_SHARE_AMOUNT_OFFSET = 184;
  public static final int PROFIT_SHARE_FEE_PAID_OFFSET = 192;
  public static final int VAULT_SHARES_BASE_OFFSET = 200;
  public static final int LAST_FUEL_UPDATE_TS_OFFSET = 204;
  public static final int CUMULATIVE_FUEL_PER_SHARE_AMOUNT_OFFSET = 208;
  public static final int FUEL_AMOUNT_OFFSET = 224;
  public static final int PADDING_OFFSET = 240;

  public static Filter createVaultFilter(final PublicKey vault) {
    return Filter.createMemCompFilter(VAULT_OFFSET, vault);
  }

  public static Filter createPubkeyFilter(final PublicKey pubkey) {
    return Filter.createMemCompFilter(PUBKEY_OFFSET, pubkey);
  }

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createVaultSharesFilter(final BigInteger vaultShares) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, vaultShares);
    return Filter.createMemCompFilter(VAULT_SHARES_OFFSET, _data);
  }

  public static Filter createLastWithdrawRequestFilter(final WithdrawRequest lastWithdrawRequest) {
    return Filter.createMemCompFilter(LAST_WITHDRAW_REQUEST_OFFSET, lastWithdrawRequest.write());
  }

  public static Filter createLastValidTsFilter(final long lastValidTs) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, lastValidTs);
    return Filter.createMemCompFilter(LAST_VALID_TS_OFFSET, _data);
  }

  public static Filter createNetDepositsFilter(final long netDeposits) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, netDeposits);
    return Filter.createMemCompFilter(NET_DEPOSITS_OFFSET, _data);
  }

  public static Filter createTotalDepositsFilter(final long totalDeposits) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalDeposits);
    return Filter.createMemCompFilter(TOTAL_DEPOSITS_OFFSET, _data);
  }

  public static Filter createTotalWithdrawsFilter(final long totalWithdraws) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalWithdraws);
    return Filter.createMemCompFilter(TOTAL_WITHDRAWS_OFFSET, _data);
  }

  public static Filter createCumulativeProfitShareAmountFilter(final long cumulativeProfitShareAmount) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, cumulativeProfitShareAmount);
    return Filter.createMemCompFilter(CUMULATIVE_PROFIT_SHARE_AMOUNT_OFFSET, _data);
  }

  public static Filter createProfitShareFeePaidFilter(final long profitShareFeePaid) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, profitShareFeePaid);
    return Filter.createMemCompFilter(PROFIT_SHARE_FEE_PAID_OFFSET, _data);
  }

  public static Filter createVaultSharesBaseFilter(final int vaultSharesBase) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, vaultSharesBase);
    return Filter.createMemCompFilter(VAULT_SHARES_BASE_OFFSET, _data);
  }

  public static Filter createLastFuelUpdateTsFilter(final int lastFuelUpdateTs) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, lastFuelUpdateTs);
    return Filter.createMemCompFilter(LAST_FUEL_UPDATE_TS_OFFSET, _data);
  }

  public static Filter createCumulativeFuelPerShareAmountFilter(final BigInteger cumulativeFuelPerShareAmount) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, cumulativeFuelPerShareAmount);
    return Filter.createMemCompFilter(CUMULATIVE_FUEL_PER_SHARE_AMOUNT_OFFSET, _data);
  }

  public static Filter createFuelAmountFilter(final BigInteger fuelAmount) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, fuelAmount);
    return Filter.createMemCompFilter(FUEL_AMOUNT_OFFSET, _data);
  }

  public static VaultDepositor read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static VaultDepositor read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static VaultDepositor read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], VaultDepositor> FACTORY = VaultDepositor::read;

  public static VaultDepositor read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vault = readPubKey(_data, i);
    i += 32;
    final var pubkey = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var vaultShares = getInt128LE(_data, i);
    i += 16;
    final var lastWithdrawRequest = WithdrawRequest.read(_data, i);
    i += lastWithdrawRequest.l();
    final var lastValidTs = getInt64LE(_data, i);
    i += 8;
    final var netDeposits = getInt64LE(_data, i);
    i += 8;
    final var totalDeposits = getInt64LE(_data, i);
    i += 8;
    final var totalWithdraws = getInt64LE(_data, i);
    i += 8;
    final var cumulativeProfitShareAmount = getInt64LE(_data, i);
    i += 8;
    final var profitShareFeePaid = getInt64LE(_data, i);
    i += 8;
    final var vaultSharesBase = getInt32LE(_data, i);
    i += 4;
    final var lastFuelUpdateTs = getInt32LE(_data, i);
    i += 4;
    final var cumulativeFuelPerShareAmount = getInt128LE(_data, i);
    i += 16;
    final var fuelAmount = getInt128LE(_data, i);
    i += 16;
    final var padding = new long[4];
    SerDeUtil.readArray(padding, _data, i);
    return new VaultDepositor(_address,
                              discriminator,
                              vault,
                              pubkey,
                              authority,
                              vaultShares,
                              lastWithdrawRequest,
                              lastValidTs,
                              netDeposits,
                              totalDeposits,
                              totalWithdraws,
                              cumulativeProfitShareAmount,
                              profitShareFeePaid,
                              vaultSharesBase,
                              lastFuelUpdateTs,
                              cumulativeFuelPerShareAmount,
                              fuelAmount,
                              padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    vault.write(_data, i);
    i += 32;
    pubkey.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    putInt128LE(_data, i, vaultShares);
    i += 16;
    i += lastWithdrawRequest.write(_data, i);
    putInt64LE(_data, i, lastValidTs);
    i += 8;
    putInt64LE(_data, i, netDeposits);
    i += 8;
    putInt64LE(_data, i, totalDeposits);
    i += 8;
    putInt64LE(_data, i, totalWithdraws);
    i += 8;
    putInt64LE(_data, i, cumulativeProfitShareAmount);
    i += 8;
    putInt64LE(_data, i, profitShareFeePaid);
    i += 8;
    putInt32LE(_data, i, vaultSharesBase);
    i += 4;
    putInt32LE(_data, i, lastFuelUpdateTs);
    i += 4;
    putInt128LE(_data, i, cumulativeFuelPerShareAmount);
    i += 16;
    putInt128LE(_data, i, fuelAmount);
    i += 16;
    i += SerDeUtil.writeArrayChecked(padding, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
