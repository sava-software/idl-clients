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
/// @param pubkey The vault depositor account's pubkey. It is a pda of vault
/// @param mint The token mint for tokenized shares owned by this VaultDepositor
/// @param vaultShares share of vault owned by this depositor. vault_shares / vault.total_shares is depositor's ownership of vault_equity
/// @param lastVaultShares stores the vault_shares from the most recent liquidity event (redeem or issuance) before a spl token
///                        CPI is done, used to track invariants
/// @param lastValidTs creation ts of vault depositor
/// @param netDeposits lifetime net deposits of vault depositor for the vault
/// @param totalDeposits lifetime total deposits
/// @param totalWithdraws lifetime total withdraws
/// @param cumulativeProfitShareAmount the token amount of gains the vault depositor has paid performance fees on
/// @param vaultSharesBase The exponent for vault_shares decimal places at the time the tokenized vault depositor was initialized.
///                        If the vault undergoes a rebase, this TokenizedVaultDepositor can no longer issue new tokens, only redeem
///                        is possible.
/// @param bump The bump for the vault pda
public record TokenizedVaultDepositor(PublicKey _address,
                                      Discriminator discriminator,
                                      PublicKey vault,
                                      PublicKey pubkey,
                                      PublicKey mint,
                                      BigInteger vaultShares,
                                      BigInteger lastVaultShares,
                                      long lastValidTs,
                                      long netDeposits,
                                      long totalDeposits,
                                      long totalWithdraws,
                                      long cumulativeProfitShareAmount,
                                      long profitShareFeePaid,
                                      int vaultSharesBase,
                                      int bump,
                                      byte[] padding1,
                                      long[] padding) implements SerDe {

  public static final int BYTES = 272;
  public static final int PADDING_1_LEN = 3;
  public static final int PADDING_LEN = 10;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(75, 23, 253, 123, 121, 93, 29, 130);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int VAULT_OFFSET = 8;
  public static final int PUBKEY_OFFSET = 40;
  public static final int MINT_OFFSET = 72;
  public static final int VAULT_SHARES_OFFSET = 104;
  public static final int LAST_VAULT_SHARES_OFFSET = 120;
  public static final int LAST_VALID_TS_OFFSET = 136;
  public static final int NET_DEPOSITS_OFFSET = 144;
  public static final int TOTAL_DEPOSITS_OFFSET = 152;
  public static final int TOTAL_WITHDRAWS_OFFSET = 160;
  public static final int CUMULATIVE_PROFIT_SHARE_AMOUNT_OFFSET = 168;
  public static final int PROFIT_SHARE_FEE_PAID_OFFSET = 176;
  public static final int VAULT_SHARES_BASE_OFFSET = 184;
  public static final int BUMP_OFFSET = 188;
  public static final int PADDING_1_OFFSET = 189;
  public static final int PADDING_OFFSET = 192;

  public static Filter createVaultFilter(final PublicKey vault) {
    return Filter.createMemCompFilter(VAULT_OFFSET, vault);
  }

  public static Filter createPubkeyFilter(final PublicKey pubkey) {
    return Filter.createMemCompFilter(PUBKEY_OFFSET, pubkey);
  }

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createVaultSharesFilter(final BigInteger vaultShares) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, vaultShares);
    return Filter.createMemCompFilter(VAULT_SHARES_OFFSET, _data);
  }

  public static Filter createLastVaultSharesFilter(final BigInteger lastVaultShares) {
    final byte[] _data = new byte[16];
    putInt128LE(_data, 0, lastVaultShares);
    return Filter.createMemCompFilter(LAST_VAULT_SHARES_OFFSET, _data);
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

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static TokenizedVaultDepositor read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TokenizedVaultDepositor read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TokenizedVaultDepositor read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TokenizedVaultDepositor> FACTORY = TokenizedVaultDepositor::read;

  public static TokenizedVaultDepositor read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var vault = readPubKey(_data, i);
    i += 32;
    final var pubkey = readPubKey(_data, i);
    i += 32;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var vaultShares = getInt128LE(_data, i);
    i += 16;
    final var lastVaultShares = getInt128LE(_data, i);
    i += 16;
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
    final var bump = _data[i] & 0xFF;
    ++i;
    final var padding1 = new byte[3];
    i += SerDeUtil.readArray(padding1, _data, i);
    final var padding = new long[10];
    SerDeUtil.readArray(padding, _data, i);
    return new TokenizedVaultDepositor(_address,
                                       discriminator,
                                       vault,
                                       pubkey,
                                       mint,
                                       vaultShares,
                                       lastVaultShares,
                                       lastValidTs,
                                       netDeposits,
                                       totalDeposits,
                                       totalWithdraws,
                                       cumulativeProfitShareAmount,
                                       profitShareFeePaid,
                                       vaultSharesBase,
                                       bump,
                                       padding1,
                                       padding);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    vault.write(_data, i);
    i += 32;
    pubkey.write(_data, i);
    i += 32;
    mint.write(_data, i);
    i += 32;
    putInt128LE(_data, i, vaultShares);
    i += 16;
    putInt128LE(_data, i, lastVaultShares);
    i += 16;
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
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeArrayChecked(padding1, 3, _data, i);
    i += SerDeUtil.writeArrayChecked(padding, 10, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
