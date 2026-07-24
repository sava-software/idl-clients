package software.sava.idl.clients.jupiter.voter;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.SolanaRpcCaptureTests;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.governance.gen.types.Proposal;
import software.sava.idl.clients.jupiter.voter.gen.types.Escrow;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

/// Covers the vote-client scans against the capture harness. The client spans
/// three programs, so the property with teeth is which one each scan runs
/// under: escrows live in the locked-voter program (selected by size and the
/// vote-delegate memcmp), proposals in the governance program (selected by
/// the derived governor's memcmp).
final class JupiterVoteRpcFetcherTests extends SolanaRpcCaptureTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final JupiterAccounts JUPITER_ACCOUNTS = JupiterAccounts.MAIN_NET;

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

  @Test
  void escrowScanRunsUnderTheVoteProgramForTheDelegate() {
    final var delegate = key(0x21);
    respondWith(EMPTY_PROGRAM_ACCOUNTS);
    assertTrue(client().fetchEscrowAccountsForDelegate(rpcClient(), delegate).join().isEmpty());
    assertLastRequestContains(
        "getProgramAccounts",
        JUPITER_ACCOUNTS.voteProgram().toBase58(),
        Escrow.SIZE_FILTER.toJson(),
        Escrow.createVoteDelegateFilter(delegate).toJson());
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
