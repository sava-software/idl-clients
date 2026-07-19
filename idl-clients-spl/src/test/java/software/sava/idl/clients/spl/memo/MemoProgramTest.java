package software.sava.idl.clients.spl.memo;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.Base58;
import software.sava.core.tx.Transaction;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MemoProgramTest {

  @Test
  void testMemoV2() {
    final var recentBlockHash = "6FE9y44TsLodyVbN243QXATAWhgcj6xVfSLzEEzDbaPS";
    final var feePayer = PublicKey.fromBase58Encoded("savaKKJmmwDsHHhxV6G293hrRM4f1p6jv6qUF441QD3");

    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    final var memoIx = MemoProgram.createMemo(solanaAccounts, List.of(feePayer), "Sava".getBytes());
    assertEquals(solanaAccounts.invokedMemoProgramV2(), memoIx.programId());
    final var accounts = memoIx.accounts();
    assertEquals(1, accounts.size());
    assertEquals(feePayer, accounts.getFirst().publicKey());
    assertEquals("Sava", new String(memoIx.data()));

    final var transaction = Transaction.createTx(feePayer, memoIx);

    transaction.setRecentBlockHash(recentBlockHash);

    assertEquals(feePayer, transaction.feePayer().publicKey());
    assertArrayEquals(Base58.decode(recentBlockHash), transaction.recentBlockHash());

    final var instructions = transaction.instructions();
    assertEquals(1, instructions.size());
    assertEquals(memoIx, instructions.getFirst());

    final var serialized = transaction.serialized();

    final var expectedTransaction = Base64.getDecoder().decode("""
        AYQi1wFqaZuNJZYZYTeQ5AqEuCsPYhQMfvNrndpMQl1Yl74pzOEGXKizxeNNlWgKiP8AWdYK/V+IwSudfGi1oQ4BAAECDPVl6eB0qtYSlYif4b0tHW4ZfMrzSctd89y3PLhgsgYFSlNamSkhBk0k6HFg2jh8fDW13bySu4HkH6hAQQVEjU3vcJ2gJrwt3TS+VKevTdETKgPzW2DnS1NaKWR0INIpAQEBAARTYXZh
        """.stripTrailing());
    System.arraycopy(expectedTransaction, 1, serialized, 1, Transaction.SIGNATURE_LENGTH);

    assertArrayEquals(expectedTransaction, serialized);
  }

  /// The memo program identifies signers purely by the account metas attached to the
  /// instruction — a signer passed as read-only is not counted, so the builder that takes raw
  /// keys must mark every one of them as a signer.
  @Test
  void createMemoMarksEveryKeyAsSigner() {
    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    final var first = PublicKey.fromBase58Encoded("savaKKJmmwDsHHhxV6G293hrRM4f1p6jv6qUF441QD3");
    final var second = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] memo = "two signers".getBytes();

    final var ix = MemoProgram.createMemo(solanaAccounts.invokedMemoProgramV2(), List.of(first, second), memo);

    assertEquals(2, ix.accounts().size());
    assertEquals(first, ix.accounts().getFirst().publicKey());
    assertEquals(second, ix.accounts().getLast().publicKey());
    assertTrue(ix.accounts().stream().allMatch(AccountMeta::signer), "every memo key must sign");
    assertFalse(ix.accounts().stream().anyMatch(AccountMeta::write), "a memo mutates nothing");
    assertArrayEquals(memo, ix.data());

    // the SolanaAccounts overload resolves to the same v2 program
    assertEquals(
        ix.programId(),
        MemoProgram.createMemo(solanaAccounts, List.of(first, second), memo).programId());
  }

  /// The account-taking builder is the escape hatch: it passes metas through verbatim, so a
  /// caller can attach a non-signing account deliberately.
  @Test
  void createMemoFromAccountsPassesMetasThrough() {
    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    final var signer = PublicKey.fromBase58Encoded("savaKKJmmwDsHHhxV6G293hrRM4f1p6jv6qUF441QD3");
    final var readOnly = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final byte[] memo = "verbatim".getBytes();

    final var metas = List.of(AccountMeta.createReadOnlySigner(signer), AccountMeta.createRead(readOnly));
    final var ix = MemoProgram.createMemoFromAccounts(solanaAccounts.invokedMemoProgramV2(), metas, memo);

    assertEquals(solanaAccounts.invokedMemoProgramV2(), ix.programId());
    assertEquals(metas, ix.accounts());
    assertArrayEquals(memo, ix.data());

    // the SolanaAccounts overload selects the v2 program rather than defaulting elsewhere
    final var viaAccounts = MemoProgram.createMemoFromAccounts(solanaAccounts, metas, memo);
    assertEquals(solanaAccounts.invokedMemoProgramV2(), viaAccounts.programId());
    assertEquals(metas, viaAccounts.accounts());

    // an empty memo with no signers is still a well-formed instruction
    final var empty = MemoProgram.createMemoFromAccounts(solanaAccounts, List.of(), new byte[0]);
    assertEquals(0, empty.accounts().size());
    assertEquals(0, empty.data().length);
  }
}
