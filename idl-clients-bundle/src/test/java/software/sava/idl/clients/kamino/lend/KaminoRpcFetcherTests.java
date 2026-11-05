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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import java.util.stream.Collectors;
import software.sava.core.rpc.Filter;
import software.sava.core.programs.Discriminator;
import software.sava.rpc.json.http.response.AccountInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

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

  /// The request's whole `filters` array as sava-rpc writes it, so that no other filter, a
  /// `dataSize` among them, is sent beside the ones named.
  private static String filterArray(final Filter... filters) {
    return Arrays.stream(filters).map(Filter::toJson).collect(Collectors.joining(",", "\"filters\":[", "]"));
  }

  @Test
  void lendScansSelectByAccountSize() {
    final var kLend = KAMINO_ACCOUNTS.kLendProgram();

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchLendingMarkets(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), LendingMarket.SIZE_FILTER.toJson());

    // Referrer states come in two live lengths, so this scan selects by discriminator alone.
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReferrerStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), filterArray(ReferrerState.DISCRIMINATOR_FILTER));

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchReferrerTokenStateAccounts(rpcClient(), kLend).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", kLend.toBase58(), ReferrerTokenState.SIZE_FILTER.toJson());
  }

  /// An account of the layout's length with the discriminator; the rest zero.
  private static byte[] account(final int length, final Discriminator discriminator) {
    final byte[] data = new byte[length];
    discriminator.write(data, 0);
    return data;
  }

  /// Both scans drop the legacy accounts that are shorter than the layout instead of failing on
  /// them: on 2026-11-04 the Farms program held 9 farms of 4736 bytes and kLend 8 referrer states
  /// of 40, and one such account would otherwise fail the whole scan, since `getProgramAccounts`
  /// decodes every account before returning any.
  @Test
  void scansDropLegacyAccountsShorterThanTheLayout() {
    final var farms = KAMINO_ACCOUNTS.farmProgram();
    final var current = key(0x31);
    final var legacy = key(0x32);
    respondWith(programAccountsResult(farms.toBase58(), List.of(
        Map.entry(current.toBase58(), account(FarmState.BYTES, FarmState.DISCRIMINATOR)),
        Map.entry(legacy.toBase58(), account(4736, FarmState.DISCRIMINATOR)))));
    final var farmStates = KaminoLendClient.fetchFarmStateAccounts(rpcClient(), farms).join();
    assertEquals(List.of(current), farmStates.stream().map(AccountInfo::pubKey).toList(), "the 4736-byte farm is dropped");
    assertEquals(current, farmStates.getFirst().data()._address(), "and the current one is decoded");

    final var kLend = KAMINO_ACCOUNTS.kLendProgram();
    respondWith(programAccountsResult(kLend.toBase58(), List.of(
        Map.entry(legacy.toBase58(), account(40, ReferrerState.DISCRIMINATOR)),
        Map.entry(current.toBase58(), account(ReferrerState.BYTES, ReferrerState.DISCRIMINATOR)))));
    final var referrerStates = KaminoLendClient.fetchReferrerStateAccounts(rpcClient(), kLend).join();
    assertEquals(List.of(current), referrerStates.stream().map(AccountInfo::pubKey).toList(), "the 40-byte referrer state is dropped");
    assertEquals(current, referrerStates.getFirst().data()._address());
  }

  /// The guard is on length alone. An account of the layout's length that is not the type it
  /// claims still fails the scan, as `readChecked` makes it: the scan drops nothing it could have
  /// decoded, and hides nothing that is wrong.
  @Test
  void scansStillFailOnAFullLengthAccountOfAnotherType() {
    final var farms = KAMINO_ACCOUNTS.farmProgram();
    respondWith(programAccountsResult(key(0x33).toBase58(), account(FarmState.BYTES, ReferrerState.DISCRIMINATOR), farms.toBase58()));
    final var thrown = assertThrows(CompletionException.class,
        () -> KaminoLendClient.fetchFarmStateAccounts(rpcClient(), farms).join());
    assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
  }

  /// Farm states belong to the Farms program. Scanned under kLend, as this fetcher's parameter
  /// once named it, the scan matches nothing: 0 accounts at slot 456604915, against 616 under
  /// the Farms program at slot 456604911. It selects by discriminator alone, since live farms
  /// come in more than one length.
  @Test
  void farmStateScanRunsUnderTheFarmsProgram() {
    final var farms = KAMINO_ACCOUNTS.farmProgram();
    assertEquals(PublicKey.fromBase58Encoded("FarmsPZpWu9i7Kky8tPN37rs2TpmMrAZrC7S7vJa91Hr"), farms);

    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(KaminoLendClient.fetchFarmStateAccounts(rpcClient(), farms).join().isEmpty());
    assertLastRequestContains("getProgramAccounts", farms.toBase58(), filterArray(FarmState.DISCRIMINATOR_FILTER));
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
