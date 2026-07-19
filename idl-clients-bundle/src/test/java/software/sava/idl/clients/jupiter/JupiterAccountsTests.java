package software.sava.idl.clients.jupiter;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Jupiter address constants and governance/vote PDA helpers.
///
/// These derivations span four programs (swap, vote, gov, merkle distributor)
/// and several of them differ *only* by their seed prefix over the same key —
/// `deriveProposalMeta` and `deriveOptionProposalMeta` both take just a
/// proposal, and `deriveGovernor`/`deriveLocker` both take just the base key
/// under different programs. So the assertions are: every input participates,
/// each helper is separated from its same-shaped neighbours, and each
/// convenience overload binds the right program and the right stored key.
final class JupiterAccountsTests {

  private static final JupiterAccounts ACCOUNTS = JupiterAccounts.MAIN_NET;

  private static final PublicKey BASE = key(0x11);
  private static final PublicKey LOCKER = key(0x12);
  private static final PublicKey OWNER = key(0x13);
  private static final PublicKey GOVERNOR = key(0x14);
  private static final PublicKey PROPOSAL = key(0x15);
  private static final PublicKey VOTER = key(0x16);
  private static final PublicKey DISTRIBUTOR = key(0x17);
  private static final PublicKey OTHER = key(0x21);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  @SafeVarargs
  private static void assertDerivation(final String name,
                                       final Supplier<PublicKey> derive,
                                       final Supplier<PublicKey>... variants) {
    final var derived = derive.get();
    assertNotNull(derived, name);
    assertEquals(derived, derive.get(), name + " must be deterministic");
    for (int i = 0; i < variants.length; i++) {
      assertNotEquals(derived, variants[i].get(), name + " must depend on input " + i);
    }
  }

  @Test
  void programConstantsAreDistinctAndWired() {
    // the invoked metas wrap their own program
    assertEquals(ACCOUNTS.swapProgram(), ACCOUNTS.invokedSwapProgram().publicKey());
    assertEquals(ACCOUNTS.limitOrderProgram(), ACCOUNTS.invokedLimitOrderProgram().publicKey());
    assertEquals(ACCOUNTS.dcaProgram(), ACCOUNTS.invokedDCAProgram().publicKey());
    assertEquals(ACCOUNTS.voteProgram(), ACCOUNTS.invokedVoteProgram().publicKey());
    assertEquals(ACCOUNTS.govProgram(), ACCOUNTS.invokedGovProgram().publicKey());
    assertEquals(ACCOUNTS.lendingProgram(), ACCOUNTS.invokedLendingProgram().publicKey());

    // and the programs really are different from one another, so binding the
    // wrong one in a derivation is observable
    assertEquals(6, Set.of(
        ACCOUNTS.swapProgram(),
        ACCOUNTS.limitOrderProgram(),
        ACCOUNTS.dcaProgram(),
        ACCOUNTS.voteProgram(),
        ACCOUNTS.govProgram(),
        ACCOUNTS.lendingProgram()).size());

    assertNotNull(ACCOUNTS.jupTokenMint());
    assertNotNull(ACCOUNTS.jupBaseKey());
    assertNotNull(ACCOUNTS.aggregatorEventAuthority());
    assertNotNull(ACCOUNTS.invokedMerkleDistributorProgram());
  }

  /// The stored locker/governor keys are the derivations of the stored base
  /// key under their respective programs — not hardcoded independently.
  @Test
  void storedKeysMatchTheirDerivations() {
    assertEquals(
        JupiterAccounts.deriveLocker(ACCOUNTS.voteProgram(), ACCOUNTS.jupBaseKey()).publicKey(),
        ACCOUNTS.lockerKey());
    assertEquals(ACCOUNTS.lockerKey(), ACCOUNTS.deriveJupLocker().publicKey());

    assertEquals(
        JupiterAccounts.deriveGovernor(ACCOUNTS.govProgram(), ACCOUNTS.jupBaseKey()).publicKey(),
        ACCOUNTS.governorKey());
    assertEquals(ACCOUNTS.governorKey(), ACCOUNTS.deriveGovernor().publicKey());

    assertEquals(
        JupiterAccounts.deriveEventAuthority(ACCOUNTS.swapProgram()).publicKey(),
        ACCOUNTS.aggregatorEventAuthority());

    // locker and governor share a base key but live under different programs
    assertNotEquals(ACCOUNTS.lockerKey(), ACCOUNTS.governorKey());
  }

  @Test
  void base58FactoryMatchesTheKeyFactory() {
    final var fromKeys = JupiterAccounts.createAccounts(
        ACCOUNTS.swapProgram(),
        ACCOUNTS.limitOrderProgram(),
        ACCOUNTS.dcaProgram(),
        ACCOUNTS.voteProgram(),
        ACCOUNTS.govProgram(),
        ACCOUNTS.jupTokenMint(),
        ACCOUNTS.jupBaseKey(),
        ACCOUNTS.invokedMerkleDistributorProgram().publicKey(),
        ACCOUNTS.lendingProgram(),
        ACCOUNTS.vaultsProgram());

    assertEquals(ACCOUNTS.swapProgram(), fromKeys.swapProgram());
    assertEquals(ACCOUNTS.voteProgram(), fromKeys.voteProgram());
    assertEquals(ACCOUNTS.govProgram(), fromKeys.govProgram());
    assertEquals(ACCOUNTS.jupTokenMint(), fromKeys.jupTokenMint());
    assertEquals(ACCOUNTS.jupBaseKey(), fromKeys.jupBaseKey());
    assertEquals(ACCOUNTS.lendingProgram(), fromKeys.lendingProgram());
    // the derived members come out identical too
    assertEquals(ACCOUNTS.lockerKey(), fromKeys.lockerKey());
    assertEquals(ACCOUNTS.governorKey(), fromKeys.governorKey());
    assertEquals(ACCOUNTS.aggregatorEventAuthority(), fromKeys.aggregatorEventAuthority());
  }

  @Test
  void lockerAndEscrowDerivations() {
    assertDerivation("deriveLocker",
        () -> JupiterAccounts.deriveLocker(ACCOUNTS.voteProgram(), BASE).publicKey(),
        () -> JupiterAccounts.deriveLocker(ACCOUNTS.voteProgram(), OTHER).publicKey(),
        () -> JupiterAccounts.deriveLocker(ACCOUNTS.govProgram(), BASE).publicKey());

    // the accounts-taking overload binds the vote program
    assertEquals(
        JupiterAccounts.deriveLocker(ACCOUNTS.voteProgram(), BASE).publicKey(),
        JupiterAccounts.deriveLocker(ACCOUNTS, BASE).publicKey());

    assertDerivation("deriveEscrow",
        () -> JupiterAccounts.deriveEscrow(ACCOUNTS, LOCKER, OWNER).publicKey(),
        () -> JupiterAccounts.deriveEscrow(ACCOUNTS, OTHER, OWNER).publicKey(),
        () -> JupiterAccounts.deriveEscrow(ACCOUNTS, LOCKER, OTHER).publicKey());

    // the locker and owner are distinct seed positions, so swapping them moves the address
    assertNotEquals(
        JupiterAccounts.deriveEscrow(ACCOUNTS, LOCKER, OWNER).publicKey(),
        JupiterAccounts.deriveEscrow(ACCOUNTS, OWNER, LOCKER).publicKey());

    // the one-arg overload defaults the locker to the stored key
    assertEquals(
        ACCOUNTS.deriveEscrow(ACCOUNTS.lockerKey(), OWNER).publicKey(),
        ACCOUNTS.deriveEscrow(OWNER).publicKey());
    assertNotEquals(
        ACCOUNTS.deriveEscrow(LOCKER, OWNER).publicKey(),
        ACCOUNTS.deriveEscrow(OWNER).publicKey());
  }

  @Test
  void governanceDerivations() {
    assertDerivation("deriveGovernor",
        () -> JupiterAccounts.deriveGovernor(ACCOUNTS.govProgram(), BASE).publicKey(),
        () -> JupiterAccounts.deriveGovernor(ACCOUNTS.govProgram(), OTHER).publicKey(),
        () -> JupiterAccounts.deriveGovernor(ACCOUNTS.voteProgram(), BASE).publicKey());
    assertEquals(
        JupiterAccounts.deriveGovernor(ACCOUNTS.govProgram(), BASE).publicKey(),
        JupiterAccounts.deriveGovernor(ACCOUNTS, BASE).publicKey());

    assertDerivation("deriveProposal",
        () -> JupiterAccounts.deriveProposal(ACCOUNTS, GOVERNOR, 7L).publicKey(),
        () -> JupiterAccounts.deriveProposal(ACCOUNTS, OTHER, 7L).publicKey(),
        () -> JupiterAccounts.deriveProposal(ACCOUNTS, GOVERNOR, 8L).publicKey());
    // the proposal count is a full u64
    assertNotEquals(
        JupiterAccounts.deriveProposal(ACCOUNTS, GOVERNOR, 1L).publicKey(),
        JupiterAccounts.deriveProposal(ACCOUNTS, GOVERNOR, 1L << 32).publicKey());
    // the count-only overload defaults the governor to the stored key
    assertEquals(
        ACCOUNTS.deriveProposal(ACCOUNTS.governorKey(), 7L).publicKey(),
        ACCOUNTS.deriveProposal(7L).publicKey());
    assertNotEquals(
        ACCOUNTS.deriveProposal(GOVERNOR, 7L).publicKey(),
        ACCOUNTS.deriveProposal(7L).publicKey());

    assertDerivation("deriveProposalMeta",
        () -> JupiterAccounts.deriveProposalMeta(ACCOUNTS, PROPOSAL).publicKey(),
        () -> JupiterAccounts.deriveProposalMeta(ACCOUNTS, OTHER).publicKey());
    assertDerivation("deriveOptionProposalMeta",
        () -> JupiterAccounts.deriveOptionProposalMeta(ACCOUNTS, PROPOSAL).publicKey(),
        () -> JupiterAccounts.deriveOptionProposalMeta(ACCOUNTS, OTHER).publicKey());
    // same input, same program: only the seed prefix separates these two
    assertNotEquals(
        JupiterAccounts.deriveProposalMeta(ACCOUNTS, PROPOSAL).publicKey(),
        JupiterAccounts.deriveOptionProposalMeta(ACCOUNTS, PROPOSAL).publicKey());
    assertEquals(
        JupiterAccounts.deriveProposalMeta(ACCOUNTS, PROPOSAL).publicKey(),
        ACCOUNTS.deriveProposalMeta(PROPOSAL).publicKey());
    assertEquals(
        JupiterAccounts.deriveOptionProposalMeta(ACCOUNTS, PROPOSAL).publicKey(),
        ACCOUNTS.deriveOptionProposalMeta(PROPOSAL).publicKey());

    assertDerivation("deriveVote",
        () -> JupiterAccounts.deriveVote(ACCOUNTS, PROPOSAL, VOTER).publicKey(),
        () -> JupiterAccounts.deriveVote(ACCOUNTS, OTHER, VOTER).publicKey(),
        () -> JupiterAccounts.deriveVote(ACCOUNTS, PROPOSAL, OTHER).publicKey());
    assertNotEquals(
        JupiterAccounts.deriveVote(ACCOUNTS, PROPOSAL, VOTER).publicKey(),
        JupiterAccounts.deriveVote(ACCOUNTS, VOTER, PROPOSAL).publicKey());
    assertEquals(
        JupiterAccounts.deriveVote(ACCOUNTS, PROPOSAL, VOTER).publicKey(),
        ACCOUNTS.deriveVote(PROPOSAL, VOTER).publicKey());
  }

  @Test
  void claimStatusDerivation() {
    final var program = ACCOUNTS.invokedMerkleDistributorProgram().publicKey();

    assertDerivation("deriveClaimStatus",
        () -> JupiterAccounts.deriveClaimStatus(program, 3L, DISTRIBUTOR).publicKey(),
        () -> JupiterAccounts.deriveClaimStatus(program, 4L, DISTRIBUTOR).publicKey(),
        () -> JupiterAccounts.deriveClaimStatus(program, 3L, OTHER).publicKey(),
        () -> JupiterAccounts.deriveClaimStatus(OTHER, 3L, DISTRIBUTOR).publicKey());

    // the claim index is a full u64
    assertNotEquals(
        JupiterAccounts.deriveClaimStatus(program, 1L, DISTRIBUTOR).publicKey(),
        JupiterAccounts.deriveClaimStatus(program, 1L << 32, DISTRIBUTOR).publicKey());
  }
}
