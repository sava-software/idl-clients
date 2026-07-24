package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.SolanaRpcCaptureTests;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.farms.gen.types.FarmState;
import software.sava.idl.clients.kamino.lend.gen.types.LendingMarket;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ReferrerState;
import software.sava.idl.clients.kamino.lend.gen.types.ReferrerTokenState;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/// Covers the Kamino account scans against the capture harness. Every fetcher
/// selects by a size filter (several account types share the program, so the
/// size *is* the type selector), `fetchReserveAccounts`' market overload adds
/// the lending-market memcmp, and `fetchVaults` runs under the *vaults*
/// program with its discriminator — each pinned via the exact
/// `Filter.toJson()` the client must emit.
final class KaminoRpcFetcherTests extends SolanaRpcCaptureTests {

  private static final KaminoAccounts KAMINO_ACCOUNTS = KaminoAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  @Test
  void lendScansSelectByAccountSize() {
    final var kLend = KAMINO_ACCOUNTS.kLendProgram();

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchFarmStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), FarmState.SIZE_FILTER.toJson());

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchLendingMarkets(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), LendingMarket.SIZE_FILTER.toJson());

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReferrerStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), ReferrerState.SIZE_FILTER.toJson());

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReferrerTokenStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), ReferrerTokenState.SIZE_FILTER.toJson());
  }

  /// The market-scoped reserve scan narrows by a memcmp on the reserve's
  /// stored lending market — a wrong offset would silently return every
  /// market's reserves.
  @Test
  void reserveScansSelectBySizeAndOptionallyMarket() {
    final var kLend = KAMINO_ACCOUNTS.kLendProgram();
    final var market = key(0x21);

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReserveAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), Reserve.SIZE_FILTER.toJson());

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReserveAccounts(rpcClient(), kLend, market).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        kLend.toBase58(),
        Reserve.SIZE_FILTER.toJson(),
        Reserve.createLendingMarketFilter(market).toJson());
  }

  /// `fetchVaults` is the odd one out: it scans the *vaults* program, not
  /// kLend, and pairs the size filter with the `VaultState` discriminator.
  @Test
  void vaultScanRunsUnderTheVaultsProgram() {
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KAMINO_ACCOUNTS.fetchVaults(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        KAMINO_ACCOUNTS.kVaultsProgram().toBase58(),
        VaultState.SIZE_FILTER.toJson(),
        VaultState.DISCRIMINATOR_FILTER.toJson());
  }
}
