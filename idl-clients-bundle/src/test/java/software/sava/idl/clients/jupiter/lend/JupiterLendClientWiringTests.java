package software.sava.idl.clients.jupiter.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.lend.gen.LendingProgram;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins every lend-client builder to the generated `LendingProgram` call it
/// must delegate to, with the client-supplied values — the owner as signer and
/// the lending admin — spelled out on the mirror side per the generated
/// builder's parameter names. Every caller-supplied account is a distinct
/// fill-byte key, so a transposed pair of same-typed keys changes the compared
/// list; the deposit/mint and withdraw/redeem families differ only in where
/// `mint` and `lending` sit, which is exactly the transposition this guards.
final class JupiterLendClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final JupiterAccounts JUPITER_ACCOUNTS = JupiterAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey DEPOSITOR_TA = key(0x21);
  private static final PublicKey RECIPIENT_TA = key(0x22);
  private static final PublicKey MINT = key(0x23);
  private static final PublicKey LENDING = key(0x24);
  private static final PublicKey F_TOKEN_MINT = key(0x25);
  private static final PublicKey SUPPLY_RESERVES = key(0x26);
  private static final PublicKey SUPPLY_POSITION = key(0x27);
  private static final PublicKey RATE_MODEL = key(0x28);
  private static final PublicKey VAULT = key(0x29);
  private static final PublicKey LIQUIDITY = key(0x2A);
  private static final PublicKey LIQUIDITY_PROGRAM = key(0x2B);
  private static final PublicKey REWARDS_RATE_MODEL = key(0x2C);
  private static final PublicKey TOKEN_PROGRAM = key(0x2D);
  private static final PublicKey CLAIM_ACCOUNT = key(0x2E);

  private static void assertIx(final Instruction expected, final Instruction actual) {
    assertEquals(expected.programId().publicKey(), actual.programId().publicKey(), "invoked program");
    assertEquals(
        expected.accounts().stream().map(AccountMeta::publicKey).toList(),
        actual.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order"
    );
    for (int i = 0; i < expected.accounts().size(); i++) {
      assertEquals(expected.accounts().get(i).write(), actual.accounts().get(i).write(), "writable at slot " + i);
      assertEquals(expected.accounts().get(i).signer(), actual.accounts().get(i).signer(), "signer at slot " + i);
    }
    assertArrayEquals(expected.data(), actual.data(), "instruction data");
  }

  /// Built inside the test body — factory coverage attributed to a field
  /// initializer is unstable under PIT.
  private static JupiterLendClient client() {
    return JupiterLendClient.createClient(SOLANA_ACCOUNTS, JUPITER_ACCOUNTS, OWNER);
  }

  @Test
  void clientBindsItsIdentity() {
    final var client = client();
    assertEquals(SOLANA_ACCOUNTS, client.solanaAccounts());
    assertEquals(JUPITER_ACCOUNTS, client.jupiterAccounts());
    assertEquals(OWNER, client.owner());

    final var defaulted = JupiterLendClient.createClient(JUPITER_ACCOUNTS, OWNER);
    assertEquals(SolanaAccounts.MAIN_NET, defaulted.solanaAccounts(), "two-arg factory defaults to main net");
    assertEquals(OWNER, defaulted.owner());
  }

  @Test
  void depositFamilyBindsTheGeneratedBuilders() {
    final var client = client();
    assertIx(
        LendingProgram.deposit(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, MINT, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM,
            REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L
        ),
        client.deposit(
            DEPOSITOR_TA, RECIPIENT_TA, MINT, LENDING, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L
        )
    );
    assertIx(
        LendingProgram.depositWithMinAmountOut(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, MINT, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM,
            REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L, 40L
        ),
        client.depositWithMinAmountOut(
            DEPOSITOR_TA, RECIPIENT_TA, MINT, LENDING, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L, 40L
        )
    );
    assertIx(
        LendingProgram.mint(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, MINT, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM,
            REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L
        ),
        client.mint(
            DEPOSITOR_TA, RECIPIENT_TA, MINT, LENDING, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L
        )
    );
    assertIx(
        LendingProgram.mintWithMaxAssets(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, MINT, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM,
            REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L, 9L
        ),
        client.mintWithMaxAssets(
            DEPOSITOR_TA, RECIPIENT_TA, MINT, LENDING, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L, 9L
        )
    );
  }

  /// The withdraw family flips the client's `lending`/`mint` parameter order
  /// relative to deposit and adds the claim account — the mirror pins where
  /// each lands in the generated call.
  @Test
  void withdrawFamilyBindsTheGeneratedBuilders() {
    final var client = client();
    assertIx(
        LendingProgram.redeem(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, MINT, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY,
            LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L
        ),
        client.redeem(
            DEPOSITOR_TA, RECIPIENT_TA, LENDING, MINT, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L
        )
    );
    assertIx(
        LendingProgram.redeemWithMinAmountOut(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, MINT, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY,
            LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L, 5L
        ),
        client.redeemWithMinAmountOut(
            DEPOSITOR_TA, RECIPIENT_TA, LENDING, MINT, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 7L, 5L
        )
    );
    assertIx(
        LendingProgram.withdraw(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, MINT, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY,
            LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L
        ),
        client.withdraw(
            DEPOSITOR_TA, RECIPIENT_TA, LENDING, MINT, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L
        )
    );
    assertIx(
        LendingProgram.withdrawWithMaxSharesBurn(
            JUPITER_ACCOUNTS.invokedLendingProgram(), SOLANA_ACCOUNTS, OWNER,
            DEPOSITOR_TA, RECIPIENT_TA, JUPITER_ACCOUNTS.lendingAdminKey(), LENDING, MINT, F_TOKEN_MINT,
            SUPPLY_RESERVES, SUPPLY_POSITION, RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY,
            LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L, 50L
        ),
        client.withdrawWithMaxSharesBurn(
            DEPOSITOR_TA, RECIPIENT_TA, LENDING, MINT, F_TOKEN_MINT, SUPPLY_RESERVES, SUPPLY_POSITION,
            RATE_MODEL, VAULT, CLAIM_ACCOUNT, LIQUIDITY, LIQUIDITY_PROGRAM, REWARDS_RATE_MODEL, TOKEN_PROGRAM, 42L, 50L
        )
    );
  }

  @Test
  void updateRateBindsTheGeneratedBuilder() {
    assertIx(
        LendingProgram.updateRate(
            JUPITER_ACCOUNTS.invokedLendingProgram(), LENDING, MINT, F_TOKEN_MINT, SUPPLY_RESERVES, REWARDS_RATE_MODEL
        ),
        client().updateRate(LENDING, MINT, F_TOKEN_MINT, SUPPLY_RESERVES, REWARDS_RATE_MODEL)
    );
  }
}
