package software.sava.idl.clients.jupiter.voter;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.SolanaRpcCaptureTests;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.governance.gen.types.Proposal;
import software.sava.idl.clients.jupiter.voter.gen.types.Escrow;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the vote-client scans against the capture harness. The client spans
/// three programs, so the property with teeth is which one each scan runs
/// under: escrows live in the locked-voter program (selected by discriminator
/// and the vote-delegate memcmp), proposals in the governance program (selected
/// by the derived governor's memcmp).
final class JupiterVoteRpcFetcherTests extends SolanaRpcCaptureTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final JupiterAccounts JUPITER_ACCOUNTS = JupiterAccounts.MAIN_NET;

  /// A live escrow 328 bytes long, six past [Escrow#BYTES] and zero there, the length a size
  /// filter dropped: fetched from mainnet at slot 465280677, owned by the vote program. Its owner
  /// is also its vote delegate.
  private static final String LONG_ESCROW = "5mDahJCsaTSgDRo7GWJzkYKRrsqUUNmXxV1niBBKfoTc";
  private static final String LONG_ESCROW_DATA = "H9V7u7oW2psYbbAAkQH/znetxUAqBQt7HrHI0eH+0RseG4wDDWfcrhKWEFuMX2sR4CSMjV+VJ6ArsqApspPcoRImZXJFNV06/wNhxZnFs3Y6egDBocONEkHSWD1qSduMGEMW2z4593inlNYGAAAAAAAAAAAAAAAAAAAAAAAAAAAAEpYQW4xfaxHgJIyNX5UnoCuyoCmyk9yhEiZlckU1XToBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==";
  private static final PublicKey LONG_ESCROW_OWNER = PublicKey.fromBase58Encoded("2FZ4EpGn5AXfSmjjFLZgEtwr3DHdFK1y5zmKDR4g9rbK");

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static JupiterVoteClient client() {
    return JupiterVoteClient.createClient(
        SPLAccountClient.createClient(SOLANA_ACCOUNTS, key(0x11), AccountMeta.createFeePayer(key(0x12))),
        JUPITER_ACCOUNTS);
  }

  /// The request's whole `filters` array as sava-rpc writes it. Matching it pins that no other
  /// filter is sent, which one fragment per filter cannot: a `dataSize` filter would pass that.
  private static String filterArray(final Filter... filters) {
    return Arrays.stream(filters).map(Filter::toJson).collect(Collectors.joining(",", "\"filters\":[", "]"));
  }

  /// Live escrows are 322 or 328 bytes, so the scan pins the account type by discriminator and
  /// sends no size filter. The response is the live 328-byte escrow, decoded through the
  /// fetcher's factory; the expected fields were read from its bytes at the IDL's offsets.
  @Test
  void escrowScanRunsUnderTheVoteProgramForTheDelegateAtAnyLength() {
    final byte[] data = Base64.getDecoder().decode(LONG_ESCROW_DATA);
    assertEquals(Escrow.BYTES + 6, data.length);
    assertArrayEquals(new byte[6], Arrays.copyOfRange(data, Escrow.BYTES, data.length));
    respondWith(programAccountsResult(LONG_ESCROW, data, JUPITER_ACCOUNTS.voteProgram().toBase58()));

    final var escrows = client().fetchEscrowAccountsForDelegate(rpcClient(), LONG_ESCROW_OWNER).join();
    assertLastRequestContains(
        "getProgramAccounts",
        JUPITER_ACCOUNTS.voteProgram().toBase58(),
        filterArray(Escrow.DISCRIMINATOR_FILTER, Escrow.createVoteDelegateFilter(LONG_ESCROW_OWNER)));

    assertEquals(1, escrows.size());
    final var accountInfo = escrows.getFirst();
    assertEquals(PublicKey.fromBase58Encoded(LONG_ESCROW), accountInfo.pubKey());
    assertEquals(328, accountInfo.space());
    final var escrow = accountInfo.data();
    assertEquals(LONG_ESCROW_OWNER, escrow.owner());
    assertEquals(LONG_ESCROW_OWNER, escrow.voteDelegate());
    assertEquals(448_148L, escrow.amount());
    assertTrue(escrow.isMaxLock());
  }

  @Test
  void proposalScanRunsUnderTheGovProgramForTheGovernor() {
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(client().fetchProposals(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        JUPITER_ACCOUNTS.govProgram().toBase58(),
        Proposal.createGovernorFilter(JUPITER_ACCOUNTS.deriveGovernor().publicKey()).toJson());
  }
}
