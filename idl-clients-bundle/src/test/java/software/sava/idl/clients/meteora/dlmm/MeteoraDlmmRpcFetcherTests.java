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

import static org.junit.jupiter.api.Assertions.assertTrue;

/// Covers the DLMM account scans against the capture harness. The DLMM
/// program hosts several same-sized account families, so each scan pairs the
/// size filter with the account discriminator carried by `MeteoraAccounts`;
/// the position scan additionally narrows to the client's owner, and the
/// mint-scoped pair scan adds both mint memcmps — each pinned via the exact
/// `Filter.toJson()` the client must emit.
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

  @Test
  void positionScanSelectsTheOwnersPositions() {
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(client().fetchPositions(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        METEORA_ACCOUNTS.dlmmProgram().toBase58(),
        PositionV2.SIZE_FILTER.toJson(),
        Filter.createMemCompFilter(0, METEORA_ACCOUNTS.positionV2Discriminator().data()).toJson(),
        PositionV2.createOwnerFilter(OWNER).toJson());
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
