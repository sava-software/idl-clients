package software.sava.idl.clients.meteora.dlmm;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.SolanaRpcCaptureTests;
import software.sava.idl.clients.meteora.MeteoraAccounts;
import software.sava.idl.clients.meteora.dlmm.gen.types.LbPair;
import software.sava.idl.clients.meteora.dlmm.gen.types.PositionV2;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/// Covers the DLMM account scans against the capture harness. Every scan
/// selects by the account discriminator carried by `MeteoraAccounts`. The pair
/// scans pair it with the size filter, and the mint-scoped one adds both mint
/// memcmps, each pinned via the exact `Filter.toJson()` the client must emit.
/// The position scan narrows to the client's owner and sends no size filter,
/// since a position wider than 70 bins is longer than `PositionV2.BYTES`; its
/// whole filter array is pinned, so a size filter cannot return unnoticed.
final class MeteoraDlmmRpcFetcherTests extends SolanaRpcCaptureTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final MeteoraAccounts METEORA_ACCOUNTS = MeteoraAccounts.MAIN_NET;
  private static final PublicKey OWNER = key(0x11);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static MeteoraDlmmClient client() {
    return MeteoraDlmmClient.createClient(
        SOLANA_ACCOUNTS, METEORA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(key(0x12)));
  }

  /// The request's whole `filters` array as sava-rpc writes it. Matching it pins that no other
  /// filter is sent, which one fragment per filter cannot: a `dataSize` filter would pass that.
  private static String filterArray(final Filter... filters) {
    return Arrays.stream(filters).map(Filter::toJson).collect(Collectors.joining(",", "\"filters\":[", "]"));
  }

  @Test
  void positionScanSelectsTheOwnersPositionsOfAnyWidth() {
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(client().fetchPositions(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        METEORA_ACCOUNTS.dlmmProgram().toBase58(),
        filterArray(
            Filter.createMemCompFilter(0, METEORA_ACCOUNTS.positionV2Discriminator().data()),
            PositionV2.createOwnerFilter(OWNER)));
  }

  @Test
  void pairScansSelectByDiscriminatorAndOptionallyMints() {
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(client().fetchPairs(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        METEORA_ACCOUNTS.dlmmProgram().toBase58(),
        LbPair.SIZE_FILTER.toJson(),
        Filter.createMemCompFilter(0, METEORA_ACCOUNTS.lbPairDiscriminator().data()).toJson());

    final var xMint = key(0x21);
    final var yMint = key(0x22);
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(client().fetchPairs(rpcClient(), xMint, yMint).join().isEmpty());
    assertLastRequestContains(
        METEORA_ACCOUNTS.dlmmProgram().toBase58(),
        LbPair.SIZE_FILTER.toJson(),
        LbPair.createTokenXMintFilter(xMint).toJson(),
        LbPair.createTokenYMintFilter(yMint).toJson());
  }
}
