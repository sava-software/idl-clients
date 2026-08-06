package software.sava.idl.clients.kamino.vaults;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

/// `refresh_allocation_reserve_accounts` takes the first `get_reserves_count()` remaining
/// accounts as the vault's reserves, in the vault's own allocation order, and refreshes
/// them through a klend CPI that pairs each reserve with the lending market stored in it.
/// So the contract is: two slot-ordered blocks — every reserve writable, then every
/// market read-only — with the reserve block in allocation order.
final class KaminoVaultsRemainingAccountsTests {

  /// A real mainnet klend reserve, so `lendingMarket()` is the key the program itself
  /// would read out of this account during the refresh.
  private static final PublicKey RESERVE =
      PublicKey.fromBase58Encoded("7gHxajRCcU5sb9rQrpYDvkugSqgGsi9EG947pW7xJueT");
  private static final PublicKey LENDING_MARKET =
      PublicKey.fromBase58Encoded("4iRHKGsTq3e4uut6e4PfyV9AEbNuXaMu9JaSP378p9qy");

  /// `67dqmR…`, a live vault with a hole in its allocation strategy and eight reserves
  /// spread across eight lending markets. Its `VaultState` account and the eight
  /// reserves — concatenated in allocation order — are stored gzipped; at 62 KiB and
  /// 67 KiB raw they are too big to keep as plain base64.
  private static final PublicKey GAP_VAULT =
      PublicKey.fromBase58Encoded("67dqmR76uAbjX6e81A1ganKv3ou31WUMEdeWJkwVfeXy");
  private static final String GAP_VAULT_RESOURCE =
      "/kamino/vault-67dqmR76uAbjX6e81A1ganKv3ou31WUMEdeWJkwVfeXy.gz.base64";
  private static final String GAP_VAULT_RESERVES_RESOURCE =
      "/kamino/vault-67dqmR76uAbjX6e81A1ganKv3ou31WUMEdeWJkwVfeXy.reserves.gz.base64";

  private static final String[] GAP_VAULT_RESERVES = {
      "9GJ9GBRwCp4pHmWrQ43L5xpc9Vykg7jnfwcFGN8FoHYu",
      "AYL4LMc4ZCVyq3Z7XPJGWDM4H9PiWjqXAAuuHBEGVR2Z",
      "Ga4rZytCpq1unD4DbEJ5bkHeUz9g3oh9AAFEi6vSauXp",
      "BnYNV7TdhwASUab7mQCRhzHvasjp8o8xmmvVtKnPe3Zi",
      "Atj6UREVWa7WxbF2EMKNyfmYUY1U1txughe2gjhcPDCo",
      "D6q6wuQSrifJKZYpR1M8R4YawnLDtDsMmWM1NbBmgJ59",
      "9FRZvAsjDJ6WM8BJ2S45h9PoDCLAq8DNY9zZDX7MyGzT",
      "4QKFoFDzNFnvfkzVazABbCEfMwd3y1pZqUVzmpnkCphj"
  };

  /// The market each of those reserves stores, read off the reserve accounts on chain.
  private static final String[] GAP_VAULT_MARKETS = {
      "CqAoLuqWtavaVE8deBjMKe8ZfSt9ghR6Vb8nfsyabyHA",
      "47tfyEG9SsdEnUm9cw5kY9BXngQGqu3LBoop9j5uTAv8",
      "DxXdAyU3kCjnyggvHmY5nAwg5cRbbmdyX3npfDMjjMek",
      "CF32kn7AY8X1bW7ZkGcHc4X9ZWTxqKGCJk6QwrQkDcdw",
      "6WEGfej9B9wjxRs6t4BYpb9iCXd8CpTpJ8fVSNzHCC5y",
      "7u3HeHxYDLhnCoErrtycNokbQYbWGzLs6JSDqGAv5PfF",
      "GMqmFygF5iSm5nkckYU6tieggFcR42SyjkkhK5rswFRs",
      "52FSGeeokLpgvgAMdqxyt5Hoc2TbUYj5b8yxrEdZ37Vf"
  };

  private static byte[] gunzip(final String resource) throws Exception {
    try (var in = KaminoVaultsRemainingAccountsTests.class.getResourceAsStream(resource);
         var gzip = new GZIPInputStream(new ByteArrayInputStream(
             Base64.getMimeDecoder().decode(new String(in.readAllBytes()))))) {
      return gzip.readAllBytes();
    }
  }

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static byte[] reserveData() throws Exception {
    try (var in = KaminoVaultsRemainingAccountsTests.class.getResourceAsStream(
        "/kamino/reserve-7gHxajRCcU5sb9rQrpYDvkugSqgGsi9EG947pW7xJueT.base64")) {
      return Base64.getDecoder().decode(new String(in.readAllBytes()).trim());
    }
  }

  private static Instruction base() {
    return Instruction.createInstruction(
        AccountMeta.createInvoked(key(0x10)),
        List.of(AccountMeta.createWrite(key(0x11))),
        new byte[]{1});
  }

  /// Both blocks, from real reserve accounts: the markets come out of the reserves, so a
  /// caller cannot supply a market that disagrees with the reserve it belongs to.
  @Test
  void reservesThenTheirMarkets() throws Exception {
    final byte[] data = reserveData();
    final var second = key(0x33);
    final var reserves = List.of(Reserve.read(RESERVE, data), Reserve.read(second, data));

    final var accounts = KaminoVaultsRemainingAccounts
        .appendVaultReserves(base(), reserves)
        .accounts();

    assertEquals(5, accounts.size());

    assertEquals(RESERVE, accounts.get(1).publicKey());
    assertEquals(second, accounts.get(2).publicKey());
    assertTrue(accounts.get(1).write());
    assertTrue(accounts.get(2).write());

    assertEquals(LENDING_MARKET, accounts.get(3).publicKey());
    assertEquals(LENDING_MARKET, accounts.get(4).publicKey());
    assertFalse(accounts.get(3).write());
    assertFalse(accounts.get(4).write());

    accounts.subList(1, accounts.size()).forEach(meta -> assertFalse(meta.signer()));
  }

  /// The markets are a block of their own, not interleaved with the reserves: index `i`
  /// of the second block belongs to index `i` of the first.
  @Test
  void marketsAreASecondBlockNotInterleaved() {
    final var reserveA = key(0x21);
    final var reserveB = key(0x22);
    final var marketA = key(0x31);
    final var marketB = key(0x32);

    final var accounts = KaminoVaultsRemainingAccounts
        .appendVaultReserves(base(), List.of(reserveA, reserveB), List.of(marketA, marketB))
        .accounts();

    assertEquals(
        List.of(reserveA, reserveB, marketA, marketB),
        accounts.subList(1, accounts.size()).stream().map(AccountMeta::publicKey).toList());
    assertTrue(accounts.get(1).write());
    assertTrue(accounts.get(2).write());
    assertFalse(accounts.get(3).write());
    assertFalse(accounts.get(4).write());
  }

  /// A short market list would silently shift the whole second block, so it is rejected
  /// rather than truncated.
  @Test
  void mismatchedBlockLengthsRejected() {
    assertThrows(IllegalArgumentException.class, () -> KaminoVaultsRemainingAccounts
        .appendVaultReserves(base(), List.of(key(0x21), key(0x22)), List.of(key(0x31))));
  }

  @Test
  void noReservesAppendsNothing() {
    assertEquals(1, KaminoVaultsRemainingAccounts
        .appendVaultReserves(base(), List.<Reserve>of()).accounts().size());
  }

  /// A live mainnet vault, `67dqmR…`, whose eight allocations sit in slots 0-4 and 6-8:
  /// slot 5 is empty, so the reserves the program walks are not the first eight slots.
  /// 13 of the 172 vaults on chain have a hole like this.
  @Test
  void allocationsCompactAroundEmptySlots() throws Exception {
    final var vaultState = VaultState.read(GAP_VAULT, gunzip(GAP_VAULT_RESOURCE));

    final var strategy = vaultState.vaultAllocationStrategy();
    assertEquals(25, strategy.length);
    assertEquals(PublicKey.NONE, strategy[5].reserve());
    assertNotEquals(PublicKey.NONE, strategy[6].reserve());

    assertEquals(
        Arrays.stream(GAP_VAULT_RESERVES).map(PublicKey::fromBase58Encoded).toList(),
        KaminoVaultsRemainingAccounts.allocatedReserves(vaultState));
  }

  /// End to end on that vault: its eight reserves live in eight *distinct* lending
  /// markets, so a market block built from anything but the individual reserves — the
  /// vault's own market, the first reserve's market, a deduplicated set — is wrong in
  /// seven slots.
  @Test
  void bothBlocksForARealMultiMarketVault() throws Exception {
    final var vaultState = VaultState.read(GAP_VAULT, gunzip(GAP_VAULT_RESOURCE));
    final var reserveKeys = KaminoVaultsRemainingAccounts.allocatedReserves(vaultState);

    final byte[] reserveBytes = gunzip(GAP_VAULT_RESERVES_RESOURCE);
    assertEquals(reserveKeys.size() * Reserve.BYTES, reserveBytes.length);
    final var reserves = new ArrayList<Reserve>(reserveKeys.size());
    for (int i = 0; i < reserveKeys.size(); ++i) {
      reserves.add(Reserve.read(reserveKeys.get(i), reserveBytes, i * Reserve.BYTES));
    }

    final var markets = reserves.stream().map(Reserve::lendingMarket).toList();
    assertEquals(reserves.size(), Set.copyOf(markets).size(), "eight distinct markets");
    assertEquals(
        Arrays.stream(GAP_VAULT_MARKETS).map(PublicKey::fromBase58Encoded).toList(),
        markets);

    final var accounts = KaminoVaultsRemainingAccounts
        .appendVaultReserves(base(), reserves)
        .accounts();
    final var extras = accounts.subList(accounts.size() - (reserves.size() * 2), accounts.size());

    for (int i = 0, n = reserves.size(); i < n; ++i) {
      final var reserveMeta = extras.get(i);
      assertEquals(reserveKeys.get(i), reserveMeta.publicKey(), "reserve block, slot " + i);
      assertTrue(reserveMeta.write(), "reserve block, slot " + i);
      assertFalse(reserveMeta.signer(), "reserve block, slot " + i);

      final var marketMeta = extras.get(n + i);
      assertEquals(markets.get(i), marketMeta.publicKey(), "market block, slot " + i);
      assertFalse(marketMeta.write(), "market block, slot " + i);
      assertFalse(marketMeta.signer(), "market block, slot " + i);
    }
  }
}
