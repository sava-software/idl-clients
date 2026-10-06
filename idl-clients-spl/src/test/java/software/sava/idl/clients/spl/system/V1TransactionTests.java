package software.sava.idl.clients.spl.system;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.Signer;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Transaction;
import software.sava.core.tx.TransactionSkeleton;
import software.sava.core.tx.TxBuilder;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.idl.clients.spl.system.nonce.NonceAccount;

import static org.junit.jupiter.api.Assertions.*;

/// A SIMD-0385 v1 transaction carries its compute budget in the message header rather than in
/// ComputeBudgetProgram instructions, so the two things this module adds around a transaction,
/// a generated System program instruction and a durable nonce, have to survive that format:
/// the limits a builder sets, and later tightens in place, must read back from the serialized
/// bytes, and a nonce prepended to a v1 transaction must keep them.
final class V1TransactionTests {

  // Runtime maximums, the TxBuilder defaults.
  private static final int MAX_COMPUTE_UNIT_LIMIT = 1_400_000;
  private static final int MAX_ACCOUNT_DATA_SIZE_LIMIT = 64 * 1_024 * 1_024;

  @Test
  void transferV1Transaction() {
    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    final var feePayer = Signer.createFromKeyPair(Signer.generatePrivateKeyPairBytes()).publicKey();
    final var recipient = Signer.createFromKeyPair(Signer.generatePrivateKeyPairBytes()).publicKey();

    final var transferIx = SystemProgram.transferSol(
        solanaAccounts.invokedSystemProgram(),
        feePayer,
        recipient,
        3_000
    );

    final var tx = TxBuilder.createBuilder()
        .feePayer(feePayer)
        .addInstruction(transferIx)
        .priorityFeeLamports(5_000L)
        .createTransaction();

    assertEquals(1, tx.version());
    assertFalse(tx.exceedsSizeLimit());

    // The compute unit and accounts data size limits default to the runtime maximums.
    var skeleton = TransactionSkeleton.deserializeSkeleton(tx.serialized());
    assertEquals(5_000L, skeleton.priorityFeeLamports());
    assertEquals(MAX_COMPUTE_UNIT_LIMIT, skeleton.computeUnitLimit());
    assertEquals(MAX_ACCOUNT_DATA_SIZE_LIMIT, skeleton.accountDataSizeLimit());

    // Simulate against the maximums, then tighten the limits in place.
    assertSame(tx, tx.setComputeUnitLimit(450));
    assertSame(tx, tx.setAccountDataSizeLimit(0));
    skeleton = TransactionSkeleton.deserializeSkeleton(tx.serialized());
    assertEquals(450, skeleton.computeUnitLimit());
    assertEquals(0, skeleton.accountDataSizeLimit());
  }

  @Test
  void durableNonceV1Transaction() {
    final var solanaAccounts = SolanaAccounts.MAIN_NET;
    final var feePayer = Signer.createFromKeyPair(Signer.generatePrivateKeyPairBytes()).publicKey();
    final var recipient = Signer.createFromKeyPair(Signer.generatePrivateKeyPairBytes()).publicKey();
    final var nonceAccountKey = Signer.createFromKeyPair(Signer.generatePrivateKeyPairBytes()).publicKey();

    final var transferIx = SystemProgram.transferSol(
        solanaAccounts.invokedSystemProgram(),
        feePayer,
        recipient,
        3_000
    );

    final var tx = TxBuilder.createBuilder()
        .feePayer(feePayer)
        .addInstruction(transferIx)
        .priorityFeeLamports(5_000L)
        .computeUnitLimit(450)
        .createTransaction();

    final byte[] nonce = new byte[Transaction.BLOCK_HASH_LENGTH];
    for (int b = 0; b < nonce.length; ++b) {
      nonce[b] = (byte) (b + 1);
    }
    final var nonceAccount = new NonceAccount(
        nonceAccountKey,
        1,
        NonceAccount.State.Initialized,
        feePayer,
        nonce,
        5_000L
    );

    // The v1 lifetime specifier means the same as the legacy recent block hash field, so the
    // nonce goes where the block hash would, and the advance instruction goes first.
    final var withNonce = nonceAccount.setNonce(solanaAccounts, tx);
    assertNotSame(tx, withNonce);
    assertEquals(1, withNonce.version());
    assertEquals(2, withNonce.numInstructions());
    assertEquals(
        solanaAccounts.systemProgram(),
        withNonce.instructions().getFirst().programId().publicKey()
    );
    assertArrayEquals(nonce, withNonce.recentBlockHash());

    // The configured limits carry over to the derived transaction.
    final var skeleton = TransactionSkeleton.deserializeSkeleton(withNonce.serialized());
    assertEquals(5_000L, skeleton.priorityFeeLamports());
    assertEquals(450, skeleton.computeUnitLimit());
    assertEquals(MAX_ACCOUNT_DATA_SIZE_LIMIT, skeleton.accountDataSizeLimit());
    assertEquals(0, skeleton.heapSize());
  }
}
