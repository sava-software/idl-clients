package software.sava.idl.clients.meteora;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.meteora.dlmm.gen.LbClmmPDAs;

import static org.junit.jupiter.api.Assertions.*;

/// Exercises the Meteora accounts factories from inside a test body —
/// `MAIN_NET`'s own construction runs in the interface's `<clinit>`, whose
/// coverage attribution is unstable under PIT. The key property is that the
/// event authority is *derived* from the DLMM program passed in, not copied
/// from a constant.
final class MeteoraAccountsTests {

  private static final MeteoraAccounts ACCOUNTS = MeteoraAccounts.MAIN_NET;

  @Test
  void factoriesBuildFromInsideATestBody() {
    final var fromKeys = MeteoraAccounts.createAccounts(
        ACCOUNTS.dlmmProgram(),
        ACCOUNTS.lbPairDiscriminator(),
        ACCOUNTS.positionV2Discriminator(),
        ACCOUNTS.invokedDynamicAmmPoolsProgram().publicKey(),
        ACCOUNTS.invokedM3m3StakeForFeeProgram().publicKey(),
        ACCOUNTS.invokedVaultProgram().publicKey(),
        ACCOUNTS.invokedFarmProgram().publicKey(),
        ACCOUNTS.invokedDlmmVaultProgram().publicKey(),
        ACCOUNTS.invokedAffiliateProgram().publicKey(),
        ACCOUNTS.invokedMercurialStableSwapProgram().publicKey()
    );
    assertEquals(ACCOUNTS.dlmmProgram(), fromKeys.dlmmProgram());
    assertEquals(ACCOUNTS.dlmmProgram(), fromKeys.invokedDlmmProgram().publicKey());
    assertEquals(
        LbClmmPDAs.eventAuthorityPDA(ACCOUNTS.dlmmProgram()).publicKey(),
        fromKeys.eventAuthority().publicKey(),
        "the event authority is derived from the DLMM program"
    );
    assertEquals(ACCOUNTS.lbPairDiscriminator(), fromKeys.lbPairDiscriminator());
    assertEquals(ACCOUNTS.positionV2Discriminator(), fromKeys.positionV2Discriminator());
    assertNotEquals(fromKeys.lbPairDiscriminator(), fromKeys.positionV2Discriminator());

    final var fromStrings = MeteoraAccounts.createAccounts(
        "LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo",
        new int[]{33, 11, 49, 98, 181, 101, 177, 13},
        new int[]{117, 176, 212, 199, 245, 180, 133, 182},
        "Eo7WjKq67rjJQSZxS6z3YkapzY3eMj6Xy8X5EQVn5UaB",
        "FEESngU3neckdwib9X3KWqdL7Mjmqk9XNp3uh5JbP4KP",
        "24Uqj9JCLxUeoC3hGfh5W3s9FM9uCHDS2SG3LYwBpyTi",
        "FarmuwXPWXvefWUeqFAa5w6rifLkq5X6E8bimYvrhCB1",
        "vaU6kP7iNEGkbmPkLmZfGwiGxd4Mob24QQCie5R9kd2",
        "GacY9YuN16HNRTy7ZWwULPccwvfFSBeNLuAQP7y38Du3",
        "MERLuDFBMmsHnsBPZw2sDQZHvXFMwp8EdjudcU2HKky"
    );
    assertEquals(ACCOUNTS.dlmmProgram(), fromStrings.dlmmProgram());
    assertEquals(ACCOUNTS.eventAuthority().publicKey(), fromStrings.eventAuthority().publicKey());
    assertEquals(ACCOUNTS.lbPairDiscriminator(), fromStrings.lbPairDiscriminator());
    assertEquals(
        ACCOUNTS.invokedMercurialStableSwapProgram().publicKey(),
        fromStrings.invokedMercurialStableSwapProgram().publicKey());
  }
}
