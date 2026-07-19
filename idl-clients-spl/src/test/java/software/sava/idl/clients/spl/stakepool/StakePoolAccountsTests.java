package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// `createAddressConstants` fans one program id out into three shapes — the raw key, an
/// *invoked* meta, and a *read* meta — for each of two programs. Six derived values from four
/// inputs is exactly the shape where a copy-paste slip goes unnoticed, so each one is pinned to
/// the input it must come from.
final class StakePoolAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    java.util.Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  @Test
  void eachProgramFansOutToItsOwnMetas() {
    final var stakePool = key(0x01);
    final var singleValidator = key(0x02);
    final var sanctumMulti = key(0x03);
    final var sanctumSingle = key(0x04);

    final var accounts = StakePoolAccounts.createAddressConstants(
        stakePool, singleValidator, sanctumMulti, sanctumSingle);

    assertEquals(stakePool, accounts.stakePoolProgram());
    assertEquals(AccountMeta.createInvoked(stakePool), accounts.invokedStakePoolProgram());
    assertEquals(AccountMeta.createRead(stakePool), accounts.readStakePoolProgram());

    assertEquals(singleValidator, accounts.singleValidatorStakePoolProgram());
    assertEquals(AccountMeta.createInvoked(singleValidator), accounts.invokedSingleValidatorStakePoolProgram());
    assertEquals(AccountMeta.createRead(singleValidator), accounts.readSingleValidatorStakePoolProgram());

    assertEquals(sanctumMulti, accounts.sanctumMultiValidatorStakePoolProgram());
    assertEquals(sanctumSingle, accounts.sanctumSingleValidatorStakePoolProgram());

    // invoked and read are genuinely different roles, not the same meta twice
    assertNotEquals(accounts.invokedStakePoolProgram(), accounts.readStakePoolProgram());
    // and the two programs do not bleed into each other
    assertNotEquals(accounts.invokedStakePoolProgram(), accounts.invokedSingleValidatorStakePoolProgram());
  }

  /// The base58 overload must decode each string into the matching slot.
  @Test
  void base58OverloadDecodesInOrder() {
    final var accounts = StakePoolAccounts.createAddressConstants(
        "SPoo1Ku8WFXoNDMHPsrGSTSG1Y47rzgn41SLUNakuHy",
        "SVSPxpvHdN29nkVg9rPapPNDddN5DipNLRUFhyjFThE",
        "SPMBzsVUuoHA4Jm6KunbsotaahvVikZs1JyTW6iJvbn",
        "SP12tWFxD9oJsVWNavTTBZvMbA6gkAmxtVgxdqvyvhY");

    assertEquals("SPoo1Ku8WFXoNDMHPsrGSTSG1Y47rzgn41SLUNakuHy", accounts.stakePoolProgram().toBase58());
    assertEquals("SVSPxpvHdN29nkVg9rPapPNDddN5DipNLRUFhyjFThE", accounts.singleValidatorStakePoolProgram().toBase58());
    assertEquals("SPMBzsVUuoHA4Jm6KunbsotaahvVikZs1JyTW6iJvbn", accounts.sanctumMultiValidatorStakePoolProgram().toBase58());
    assertEquals("SP12tWFxD9oJsVWNavTTBZvMbA6gkAmxtVgxdqvyvhY", accounts.sanctumSingleValidatorStakePoolProgram().toBase58());

    // MAIN_NET is built from exactly these addresses
    assertEquals(StakePoolAccounts.MAIN_NET.stakePoolProgram(), accounts.stakePoolProgram());
    assertEquals(StakePoolAccounts.MAIN_NET.singleValidatorStakePoolProgram(), accounts.singleValidatorStakePoolProgram());
    assertEquals(StakePoolAccounts.MAIN_NET.sanctumMultiValidatorStakePoolProgram(), accounts.sanctumMultiValidatorStakePoolProgram());
    assertEquals(StakePoolAccounts.MAIN_NET.sanctumSingleValidatorStakePoolProgram(), accounts.sanctumSingleValidatorStakePoolProgram());
  }
}
