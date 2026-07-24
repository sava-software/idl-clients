package software.sava.idl.clients.loopscale;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// PDA derivations pinned against real mainnet Loopscale accounts (program
/// `1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78`). A real account is the program's own
/// PDA output, so a wrong seed encoding produces an address that does not exist on-chain
/// (see AGENTS.md on PDA helpers). `vault`/`strategy` are self-anchoring — the account
/// stores the `nonce` seed and its own address is the PDA of that nonce; the vault-child
/// and admin PDAs are pinned to addresses confirmed to exist under the program.
final class LoopscalePDATests {

  private static final PublicKey PROGRAM = PublicKey
      .fromBase58Encoded("1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78");

  // Real Vault TuD5gZJUV5WLZCYnD6oDh6Nmq1NdjWcv7HycwJeSjCU: its stored nonce (offset 40)
  // derives its own address.
  private static final PublicKey VAULT = PublicKey
      .fromBase58Encoded("TuD5gZJUV5WLZCYnD6oDh6Nmq1NdjWcv7HycwJeSjCU");
  private static final PublicKey VAULT_NONCE = PublicKey
      .fromBase58Encoded("3N6SWcbtnAMp4Wz7caCNQqrmVbzsphjXyhMYqhvyeqQK");

  // Real Strategy 1317qJiw8843BnnJVu323diXpJ4xJphEC8w3GgenPe7o: its stored nonce (offset 9)
  // derives its own address.
  private static final PublicKey STRATEGY = PublicKey
      .fromBase58Encoded("1317qJiw8843BnnJVu323diXpJ4xJphEC8w3GgenPe7o");
  private static final PublicKey STRATEGY_NONCE = PublicKey
      .fromBase58Encoded("881TH8QV7Xp2M2w1yuLXU4iX4nw2rEq6jGQpdZ1NAvJ4");

  // Children of VAULT, confirmed to exist on-chain owned by the program.
  private static final PublicKey VAULT_STRATEGY = PublicKey
      .fromBase58Encoded("3d4YWDdUgYVBHwDS2EH3ibocLosEWeCpVHDPDhurvyD2");
  private static final PublicKey VAULT_REWARDS_INFO = PublicKey
      .fromBase58Encoded("J9svCJMWJEtadqZaS6yXEpVXQN3d2qaKoXoYLr5e9ASz");

  // Singleton admin state, confirmed to exist on-chain.
  private static final PublicKey PROTOCOL_ADMIN_STATE = PublicKey
      .fromBase58Encoded("HcgXEnEsgvGowVnSjMmrzSewdx9yGvfXixiuMJPhyW2z");

  @Test
  void vaultDerivedFromStoredNonce() {
    assertEquals(VAULT, LoopscalePDAs.vault(VAULT_NONCE, PROGRAM).publicKey());
  }

  @Test
  void strategyDerivedFromStoredNonce() {
    assertEquals(STRATEGY, LoopscalePDAs.strategy(STRATEGY_NONCE, PROGRAM).publicKey());
  }

  @Test
  void vaultStrategyDerivedFromVault() {
    assertEquals(VAULT_STRATEGY, LoopscalePDAs.vaultStrategy(VAULT, PROGRAM).publicKey());
  }

  @Test
  void vaultRewardsInfoDerivedFromVault() {
    assertEquals(VAULT_REWARDS_INFO, LoopscalePDAs.vaultRewardsInfo(VAULT, PROGRAM).publicKey());
  }

  @Test
  void protocolAdminStateSingleton() {
    assertEquals(PROTOCOL_ADMIN_STATE, LoopscalePDAs.protocolAdminState(PROGRAM).publicKey());
  }

  // The derivations below have no independent on-chain anchor fetched yet, so
  // they are tested by property rather than pinned value (see AGENTS.md): the
  // derivation is deterministic, every input participates, and same-shaped
  // neighbours separate. `loan`'s nonce is LE-encoded, so dropping the encode
  // collapses every nonce onto zero — the distinctness assertions see that.

  @Test
  void loanDependsOnBorrowerAndNonce() {
    final var loan = LoopscalePDAs.loan(VAULT_NONCE, 1L, PROGRAM);
    assertEquals(loan.publicKey(), LoopscalePDAs.loan(VAULT_NONCE, 1L, PROGRAM).publicKey());
    assertNotEquals(loan.publicKey(), LoopscalePDAs.loan(STRATEGY_NONCE, 1L, PROGRAM).publicKey());
    assertNotEquals(loan.publicKey(), LoopscalePDAs.loan(VAULT_NONCE, 2L, PROGRAM).publicKey());
    assertNotEquals(loan.publicKey(), LoopscalePDAs.loan(VAULT_NONCE, 0L, PROGRAM).publicKey(),
        "a dropped nonce encoding collapses every loan onto nonce 0");
    // nonces differing only in their high bytes must still separate
    assertNotEquals(
        LoopscalePDAs.loan(VAULT_NONCE, 1L, PROGRAM).publicKey(),
        LoopscalePDAs.loan(VAULT_NONCE, 1L | (1L << 56), PROGRAM).publicKey()
    );
  }

  @Test
  void vaultStakeDependsOnBothItsSeeds() {
    final var stake = LoopscalePDAs.vaultStake(STRATEGY_NONCE, VAULT, PROGRAM);
    assertEquals(stake.publicKey(), LoopscalePDAs.vaultStake(STRATEGY_NONCE, VAULT, PROGRAM).publicKey());
    assertNotEquals(stake.publicKey(), LoopscalePDAs.vaultStake(VAULT_NONCE, VAULT, PROGRAM).publicKey());
    assertNotEquals(stake.publicKey(), LoopscalePDAs.vaultStake(STRATEGY_NONCE, STRATEGY, PROGRAM).publicKey());
    // the two 32-byte seeds are ordered: swapping them derives elsewhere
    assertNotEquals(stake.publicKey(), LoopscalePDAs.vaultStake(VAULT, STRATEGY_NONCE, PROGRAM).publicKey());
  }

  @Test
  void userRewardsInfoDependsOnTheVaultStake() {
    final var rewards = LoopscalePDAs.userRewardsInfo(VAULT, PROGRAM);
    assertEquals(rewards.publicKey(), LoopscalePDAs.userRewardsInfo(VAULT, PROGRAM).publicKey());
    assertNotEquals(rewards.publicKey(), LoopscalePDAs.userRewardsInfo(STRATEGY, PROGRAM).publicKey());
    // same input as the vault-scoped rewards info, different seed literal
    assertNotEquals(rewards.publicKey(), LoopscalePDAs.vaultRewardsInfo(VAULT, PROGRAM).publicKey());
  }

  @Test
  void eventAuthorityIsProgramScoped() {
    final var authority = LoopscalePDAs.eventAuthority(PROGRAM);
    assertEquals(authority.publicKey(), LoopscalePDAs.eventAuthority(PROGRAM).publicKey());
    assertNotEquals(authority.publicKey(), LoopscalePDAs.eventAuthority(VAULT).publicKey());
    assertNotEquals(authority.publicKey(), LoopscalePDAs.protocolAdminState(PROGRAM).publicKey());
  }
}
