package software.sava.idl.clients.jupiter.borrow;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.borrow.gen.VaultsProgram;
import software.sava.idl.clients.jupiter.borrow.gen.types.TransferType;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins every borrow-client builder to the generated `VaultsProgram` call it
/// must delegate to, with the client-supplied values — the owner as signer and
/// the vaults admin — spelled out on the mirror side per the generated
/// builder's parameter names. `operate` carries 33 accounts, most of them
/// same-typed `PublicKey`s, so every one is a distinct fill-byte key and the
/// whole list is compared positionally.
final class JupiterBorrowClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final JupiterAccounts JUPITER_ACCOUNTS = JupiterAccounts.MAIN_NET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey OWNER = key(0x11);

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
  private static JupiterBorrowClient client() {
    return JupiterBorrowClient.createClient(SOLANA_ACCOUNTS, JUPITER_ACCOUNTS, OWNER);
  }

  @Test
  void clientBindsItsIdentity() {
    final var client = client();
    assertEquals(SOLANA_ACCOUNTS, client.solanaAccounts());
    assertEquals(JUPITER_ACCOUNTS, client.jupiterAccounts());
    assertEquals(OWNER, client.owner());

    final var defaulted = JupiterBorrowClient.createClient(JUPITER_ACCOUNTS, OWNER);
    assertEquals(SolanaAccounts.MAIN_NET, defaulted.solanaAccounts(), "two-arg factory defaults to main net");
    assertEquals(OWNER, defaulted.owner());
  }

  @Test
  void initPositionBindsTheGeneratedBuilder() {
    assertIx(
        VaultsProgram.initPosition(
            JUPITER_ACCOUNTS.invokedVaultsProgram(), SOLANA_ACCOUNTS, OWNER,
            JUPITER_ACCOUNTS.vaultsAdminKey(), key(0x21), key(0x22), key(0x23), key(0x24), key(0x25),
            key(0x26), key(0x27), 3, 5
        ),
        client().initPosition(key(0x21), key(0x22), key(0x23), key(0x24), key(0x25), key(0x26), key(0x27), 3, 5)
    );
  }

  @Test
  void operateBindsTheGeneratedBuilder() {
    final var keys = new PublicKey[32];
    for (int i = 0; i < keys.length; i++) {
      keys[i] = key(0x20 + i);
    }
    final var newCol = BigInteger.valueOf(1_000L);
    final var newDebt = BigInteger.valueOf(-500L);
    final var indices = new byte[]{1, 2};
    assertIx(
        VaultsProgram.operate(
            JUPITER_ACCOUNTS.invokedVaultsProgram(), SOLANA_ACCOUNTS, OWNER,
            keys[0], keys[1], keys[2], keys[3], keys[4], keys[5], keys[6], keys[7], keys[8], keys[9],
            keys[10], keys[11], keys[12], keys[13], keys[14], keys[15], keys[16], keys[17], keys[18],
            keys[19], keys[20], keys[21], keys[22], keys[23], keys[24], keys[25], keys[26], keys[27],
            keys[28], keys[29], keys[30], keys[31],
            newCol, newDebt, TransferType.DIRECT, indices
        ),
        client().operate(
            keys[0], keys[1], keys[2], keys[3], keys[4], keys[5], keys[6], keys[7], keys[8], keys[9],
            keys[10], keys[11], keys[12], keys[13], keys[14], keys[15], keys[16], keys[17], keys[18],
            keys[19], keys[20], keys[21], keys[22], keys[23], keys[24], keys[25], keys[26], keys[27],
            keys[28], keys[29], keys[30], keys[31],
            newCol, newDebt, TransferType.DIRECT, indices
        )
    );
  }

  @Test
  void exchangePriceBuildersBindTheGeneratedCalls() {
    assertIx(
        VaultsProgram.getExchangePrices(
            JUPITER_ACCOUNTS.invokedVaultsProgram(), key(0x21), key(0x22), key(0x23), key(0x24)
        ),
        client().getExchangePrices(key(0x21), key(0x22), key(0x23), key(0x24))
    );
    assertIx(
        VaultsProgram.updateExchangePrices(
            JUPITER_ACCOUNTS.invokedVaultsProgram(), key(0x21), key(0x22), key(0x23), key(0x24), 7
        ),
        client().updateExchangePrices(key(0x21), key(0x22), key(0x23), key(0x24), 7)
    );
  }
}
