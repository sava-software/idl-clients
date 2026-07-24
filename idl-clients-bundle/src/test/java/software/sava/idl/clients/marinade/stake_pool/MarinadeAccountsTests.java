package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Marinade address constants and the four state-scoped authority
/// PDAs. Those four take *identical* inputs — `(program, state)` — and are
/// separated only by their seed suffix (`reserve` / `st_mint` / `withdraw` /
/// `deposit`), so a copy-paste slip between them derives a real-looking
/// authority for the wrong role.
final class MarinadeAccountsTests {

  private static final MarinadeAccounts ACCOUNTS = MarinadeAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  @Test
  void constantsAreWiredAndDistinct() {
    assertNotNull(ACCOUNTS.marinadeProgram());
    assertNotNull(ACCOUNTS.stateAccount());
    assertNotNull(ACCOUNTS.mSolTokenMint());

    // the write/invoked metas wrap their own key
    assertEquals(ACCOUNTS.marinadeProgram(), ACCOUNTS.invokedMarinadeProgram().publicKey());
    assertEquals(ACCOUNTS.stateAccount(), ACCOUNTS.writeStateAccount().publicKey());
    assertTrue(ACCOUNTS.writeStateAccount().write(), "the state account is mutated");
    assertEquals(ACCOUNTS.mSolTokenMint(), ACCOUNTS.writeMSolTokenMint().publicKey());
    assertTrue(ACCOUNTS.writeMSolTokenMint().write(), "the mint supply changes");
    assertEquals(ACCOUNTS.treasuryReserveSolPDA(), ACCOUNTS.writeReserveSolPDA().publicKey());
    assertTrue(ACCOUNTS.writeReserveSolPDA().write());

    // the liquidity-pool accounts are five distinct addresses
    assertEquals(5, Set.of(
        ACCOUNTS.liquidityPoolMSolSolMint(),
        ACCOUNTS.liquidityPoolAuthPDA(),
        ACCOUNTS.liquidityPoolMSolLegAccount(),
        ACCOUNTS.liquidityPoolMSolLegAuthority(),
        ACCOUNTS.liquidityPoolSolLegAccount()).size());
  }

  /// The stored authority constants are the derivations of the stored state
  /// account, not independently hardcoded addresses.
  @Test
  void storedAuthoritiesMatchTheirDerivations() {
    assertEquals(ACCOUNTS.deriveStakeWithdrawAuthority().publicKey(), ACCOUNTS.stakeWithdrawAuthority());
    assertEquals(ACCOUNTS.deriveStakeDepositAuthority().publicKey(), ACCOUNTS.stakeDepositAuthority());
    assertEquals(ACCOUNTS.deriveStakeMintAuthority().publicKey(), ACCOUNTS.mSolTokenMintAuthorityPDA());
    assertEquals(ACCOUNTS.deriveReserveAuthority().publicKey(), ACCOUNTS.treasuryReserveSolPDA());
  }

  /// Same inputs, four different roles — only the seed suffix separates them.
  @Test
  void theFourAuthorityPDAsAreDistinct() {
    final var program = ACCOUNTS.marinadeProgram();
    final var state = ACCOUNTS.stateAccount();

    final var reserve = MarinadeAccounts.deriveReserveAuthority(program, state).publicKey();
    final var mint = MarinadeAccounts.deriveStakeMintAuthority(program, state).publicKey();
    final var withdraw = MarinadeAccounts.deriveStakeWithdrawAuthority(program, state).publicKey();
    final var deposit = MarinadeAccounts.deriveStakeDepositAuthority(program, state).publicKey();

    assertEquals(4, Set.of(reserve, mint, withdraw, deposit).size(),
        "each role must derive its own authority");

    // and every one depends on both of its inputs
    final var otherState = key(0x31);
    final var otherProgram = key(0x32);
    assertNotEquals(reserve, MarinadeAccounts.deriveReserveAuthority(program, otherState).publicKey());
    assertNotEquals(reserve, MarinadeAccounts.deriveReserveAuthority(otherProgram, state).publicKey());
    assertNotEquals(mint, MarinadeAccounts.deriveStakeMintAuthority(program, otherState).publicKey());
    assertNotEquals(mint, MarinadeAccounts.deriveStakeMintAuthority(otherProgram, state).publicKey());
    assertNotEquals(withdraw, MarinadeAccounts.deriveStakeWithdrawAuthority(program, otherState).publicKey());
    assertNotEquals(withdraw, MarinadeAccounts.deriveStakeWithdrawAuthority(otherProgram, state).publicKey());
    assertNotEquals(deposit, MarinadeAccounts.deriveStakeDepositAuthority(program, otherState).publicKey());
    assertNotEquals(deposit, MarinadeAccounts.deriveStakeDepositAuthority(otherProgram, state).publicKey());

    // the default overloads bind the instance's own program and state
    assertEquals(reserve, ACCOUNTS.deriveReserveAuthority().publicKey());
    assertEquals(mint, ACCOUNTS.deriveStakeMintAuthority().publicKey());
    assertEquals(withdraw, ACCOUNTS.deriveStakeWithdrawAuthority().publicKey());
    assertEquals(deposit, ACCOUNTS.deriveStakeDepositAuthority().publicKey());
  }

  @Test
  void duplicationKeyDependsOnTheValidator() {
    final var validator = key(0x41);
    final var duplicate = ACCOUNTS.findDuplicationKey(validator);

    assertNotNull(duplicate.publicKey());
    assertEquals(duplicate.publicKey(), ACCOUNTS.findDuplicationKey(validator).publicKey());
    assertNotEquals(duplicate.publicKey(), ACCOUNTS.findDuplicationKey(key(0x42)).publicKey());
  }

  /// The base58 String factories are exercised from inside the test body —
  /// `MAIN_NET`'s own construction runs in the interface's `<clinit>`, whose
  /// coverage attribution is unstable under PIT. The stake list account is the
  /// one nullable slot: supplied, it must decode; omitted (or via the 12-arg
  /// overload), it must stay null rather than NPE inside the ternary.
  @Test
  void base58StringFactoriesBindEveryField() {
    final var withStakeList = MarinadeAccounts.createAddressConstants(
        "mSoLzYCxHdYgdzU16g5QSh3i5K3z3KZK7ytfqcJm7So",
        "3JLPCS1qM2zRw3Dp6V4hZnYHd4toMNPkNesXdX9tg6KM",
        "MarBmsSgKXdrN1egZf5sqe1TMai9K1rChYNDJgjq7aD",
        "8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC",
        "Du3Ysj1wKbxPKkuPPnvzQLQh8oMSVifs3jGZjJWXFmHN",
        "B1aLzaNMeFVAyQ6f3XbbUyKcH2YPHu2fqiEagmiF23VR",
        "LPmSozJJ8Jh69ut2WP3XmVohTjL4ipR18yiCzxrUmVj",
        "HZsepB79dnpvH6qfVgvMpS738EndHw3qSHo4Gv5WX1KA",
        "7GgPYjS5Dza89wV6FpZ23kUJRG5vbQ1GM25ezspYFSoE",
        "EyaSjUtSgo9aRD1f8LWXwdvkpDTmXAW54yoSHZRF14WL",
        "UefNb6z6yvArqe4cJHTXCqStRsKmWhGxnZzuHbikP5Q",
        "DwFYJNnhLmw19FBTrVaLWZ8SZJpxdPoSYVSJaio9tjbY",
        "8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC"
    );
    assertEquals(ACCOUNTS.mSolTokenMint(), withStakeList.mSolTokenMint());
    assertEquals(ACCOUNTS.marinadeProgram(), withStakeList.marinadeProgram());
    assertEquals(ACCOUNTS.stakeWithdrawAuthority(), withStakeList.stakeWithdrawAuthority());
    assertEquals(
        PublicKey.fromBase58Encoded("8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC"),
        withStakeList.stakeListAccount(),
        "a supplied stake list account decodes rather than nulling out"
    );

    final var withoutStakeList = MarinadeAccounts.createAddressConstants(
        "mSoLzYCxHdYgdzU16g5QSh3i5K3z3KZK7ytfqcJm7So",
        "3JLPCS1qM2zRw3Dp6V4hZnYHd4toMNPkNesXdX9tg6KM",
        "MarBmsSgKXdrN1egZf5sqe1TMai9K1rChYNDJgjq7aD",
        "8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC",
        "Du3Ysj1wKbxPKkuPPnvzQLQh8oMSVifs3jGZjJWXFmHN",
        "B1aLzaNMeFVAyQ6f3XbbUyKcH2YPHu2fqiEagmiF23VR",
        "LPmSozJJ8Jh69ut2WP3XmVohTjL4ipR18yiCzxrUmVj",
        "HZsepB79dnpvH6qfVgvMpS738EndHw3qSHo4Gv5WX1KA",
        "7GgPYjS5Dza89wV6FpZ23kUJRG5vbQ1GM25ezspYFSoE",
        "EyaSjUtSgo9aRD1f8LWXwdvkpDTmXAW54yoSHZRF14WL",
        "UefNb6z6yvArqe4cJHTXCqStRsKmWhGxnZzuHbikP5Q",
        "DwFYJNnhLmw19FBTrVaLWZ8SZJpxdPoSYVSJaio9tjbY"
    );
    assertNull(withoutStakeList.stakeListAccount(), "the 12-arg overload leaves the stake list unset");
    assertEquals(ACCOUNTS.mSolTokenMint(), withoutStakeList.mSolTokenMint());
    assertEquals(ACCOUNTS.stakeDepositAuthority(), withoutStakeList.stakeDepositAuthority());
    assertEquals(ACCOUNTS.validatorListAccount(), withoutStakeList.validatorListAccount());
  }

  @Test
  void base58FactoryMatchesTheKeyFactory() {
    final var fromKeys = MarinadeAccounts.createAddressConstants(
        ACCOUNTS.mSolTokenMint(),
        ACCOUNTS.mSolTokenMintAuthorityPDA(),
        ACCOUNTS.marinadeProgram(),
        ACCOUNTS.stateAccount(),
        ACCOUNTS.treasuryReserveSolPDA(),
        ACCOUNTS.treasuryMSolAccount(),
        ACCOUNTS.liquidityPoolMSolSolMint(),
        ACCOUNTS.liquidityPoolAuthPDA(),
        ACCOUNTS.liquidityPoolMSolLegAccount(),
        ACCOUNTS.liquidityPoolMSolLegAuthority(),
        ACCOUNTS.liquidityPoolSolLegAccount(),
        ACCOUNTS.validatorListAccount(),
        ACCOUNTS.stakeListAccount());

    assertEquals(ACCOUNTS.mSolTokenMint(), fromKeys.mSolTokenMint());
    assertEquals(ACCOUNTS.marinadeProgram(), fromKeys.marinadeProgram());
    assertEquals(ACCOUNTS.stateAccount(), fromKeys.stateAccount());
    assertEquals(ACCOUNTS.liquidityPoolMSolSolMint(), fromKeys.liquidityPoolMSolSolMint());
    assertEquals(ACCOUNTS.liquidityPoolSolLegAccount(), fromKeys.liquidityPoolSolLegAccount());
    // the derived authorities come out identical
    assertEquals(ACCOUNTS.stakeWithdrawAuthority(), fromKeys.stakeWithdrawAuthority());
    assertEquals(ACCOUNTS.stakeDepositAuthority(), fromKeys.stakeDepositAuthority());
    assertEquals(ACCOUNTS.mSolTokenMintAuthorityPDA(), fromKeys.mSolTokenMintAuthorityPDA());
    assertEquals(ACCOUNTS.treasuryReserveSolPDA(), fromKeys.treasuryReserveSolPDA());
  }
}
