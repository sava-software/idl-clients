package software.sava.idl.clients.spl.system;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.Signer;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.encoding.Base58;
import software.sava.core.tx.Transaction;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.idl.clients.spl.system.gen.types.Nonce;
import software.sava.idl.clients.spl.system.gen.types.NonceState;
import software.sava.idl.clients.spl.system.gen.types.NonceVersion;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.accounts.SolanaAccounts.MAIN_NET;

final class SystemProgramTest {

  @Test
  public void transferInstruction() {
    final var fromPublicKey = PublicKey.fromBase58Encoded("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
    final var toPublicKey = PublicKey.fromBase58Encoded("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
    final int lamports = 3000;

    final var instruction = SystemProgram.transferSol(
        SolanaAccounts.MAIN_NET.invokedSystemProgram(),
        fromPublicKey,
        toPublicKey,
        lamports
    );

    assertEquals(MAIN_NET.invokedSystemProgram(), instruction.programId());
    assertEquals(2, instruction.accounts().size());
    assertEquals(fromPublicKey, instruction.accounts().getFirst().publicKey());
    assertEquals(toPublicKey, instruction.accounts().getLast().publicKey());

    assertArrayEquals(new byte[]{2, 0, 0, 0, -72, 11, 0, 0, 0, 0, 0, 0}, instruction.data());
  }

  @Test
  public void createAccountInstruction() {
    final var instruction = SystemProgram.createAccount(
        MAIN_NET.invokedSystemProgram(),
        MAIN_NET.systemProgram(),
        MAIN_NET.systemProgram(), 2039280, 165,
        MAIN_NET.systemProgram()
    );

    assertEquals("11119os1e9qSs2u7TsThXqkBSRUo9x7kpbdqtNNbTeaxHGPdWbvoHsks9hpp6mb2ed1NeB",
        Base58.encode(instruction.data())
    );
  }

  @Test
  public void parseNonceAccount() {
    final var base64Data = "AQAAAAEAAAAM9WXp4HSq1hKViJ/hvS0dbhl8yvNJy13z3Lc8uGCyBirl7d+e05ILHtmpCyrZqMRG/x5AzISLYbViohfeG07tiBMAAAAAAAA=";
    final byte[] data = Base64.getDecoder().decode(base64Data);
    final var nonceAccount = Nonce.read(data, 0);

    assertEquals(NonceVersion.current, nonceAccount.version());
    assertEquals(NonceState.initialized, nonceAccount.state());
    assertEquals(PublicKey.fromBase58Encoded("savaKKJmmwDsHHhxV6G293hrRM4f1p6jv6qUF441QD3"), nonceAccount.authority());

    final var blockHash = PublicKey.fromBase58Encoded("3tTUV2sKPJ6zkS77Yo5D4vZnaqy3BX4WaTtmJsMwC2rQ");
    assertEquals(blockHash, nonceAccount.blockhash());

    assertEquals(5_000, nonceAccount.lamportsPerSignature());
  }

  @Test
  public void serializeMessage() {
    final var fromPublicKey = PublicKey.fromBase58Encoded("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
    final var toPublicKey = PublicKey.fromBase58Encoded("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
    final int lamports = 3_000;

    final var transaction = Transaction.createTx(fromPublicKey, SystemProgram.transferSol(
            SolanaAccounts.MAIN_NET.invokedSystemProgram(),
            fromPublicKey,
            toPublicKey,
            lamports
        )
    );

    final byte[] blockHash = Base58.decode("Eit7RCyhUixAe2hGBS8oqnw59QK3kgMMjfLME5bm9wRn");
    transaction.setRecentBlockHash(blockHash);
    final byte[] expectedMsg = toUnsignedByteArray(new int[]{1, 0, 1, 3, 6, 26, 217, 208, 83, 135, 21, 72, 83, 126, 222, 62, 38, 24, 73, 163,
        223, 183, 253, 2, 250, 188, 117, 178, 35, 200, 228, 106, 219, 133, 61, 12, 235, 122, 188, 208, 216, 117,
        235, 194, 109, 161, 177, 129, 163, 51, 155, 62, 242, 163, 22, 149, 187, 122, 189, 188, 103, 130, 115,
        188, 173, 205, 229, 170, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 203, 226, 136, 193, 153, 148, 240, 50, 230, 98, 9, 79, 221, 179, 243, 174, 90, 67,
        104, 169, 6, 187, 165, 72, 36, 156, 19, 57, 132, 38, 69, 245, 1, 2, 2, 0, 1, 12, 2, 0, 0, 0, 184, 11, 0,
        0, 0, 0, 0, 0});
    final int sigLen = 1 + Transaction.SIGNATURE_LENGTH;
    final byte[] expected = new byte[sigLen + expectedMsg.length];
    System.arraycopy(expectedMsg, 0, expected, sigLen, expectedMsg.length);
    expected[0] = 1;

    assertEquals(Base64.getEncoder().encodeToString(expected), transaction.base64EncodeToString());
  }

  private static byte[] toUnsignedByteArray(final int[] in) {
    final byte[] out = new byte[in.length];
    for (int i = 0; i < in.length; i++) {
      out[i] = (byte) (in[i] & 0xff);
    }
    return out;
  }

  @Test
  public void signAndSerialize() {
    final var signer = Signer.createFromKeyPair(Base58.decode("4Z7cXSyeFR8wNGMVXUE1TwtKn5D5Vu7FzEv69dokLv7KrQk7h6pu4LF8ZRR9yQBhc7uSM6RTTZtU1fmaxiNrxXrs"));
    final var fromPublicKey = PublicKey.fromBase58Encoded("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
    final var toPublicKey = PublicKey.fromBase58Encoded("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
    final int lamports = 3_000;

    final var instruction = SystemProgram.transferSol(
        SolanaAccounts.MAIN_NET.invokedSystemProgram(),
        fromPublicKey,
        toPublicKey,
        lamports
    );
    final var program = instruction.programId();
    assertEquals(MAIN_NET.invokedSystemProgram(), program);
    assertFalse(program.feePayer());
    assertFalse(program.signer());
    assertFalse(program.write());
    final var accounts = instruction.accounts();
    assertEquals(2, accounts.size());

    var account = accounts.getFirst();
    assertEquals(fromPublicKey, account.publicKey());
    assertFalse(account.feePayer());
    assertTrue(account.signer());
    assertTrue(account.write());

    account = accounts.getLast();
    assertEquals(toPublicKey, account.publicKey());
    assertFalse(account.feePayer());
    assertFalse(account.signer());
    assertTrue(account.write());

    final var transaction = Transaction.createTx(fromPublicKey, instruction);
    assertTrue(transaction.feePayer().feePayer());
    assertEquals(fromPublicKey, transaction.feePayer().publicKey());

    final var encodedTx = transaction.signAndBase64Encode(Base58.decode("Eit7RCyhUixAe2hGBS8oqnw59QK3kgMMjfLME5bm9wRn"), signer);
    assertEquals(
        "ASdDdWBaKXVRA+6flVFiZokic9gK0+r1JWgwGg/GJAkLSreYrGF4rbTCXNJvyut6K6hupJtm72GztLbWNmRF1Q4BAAEDBhrZ0FOHFUhTft4+JhhJo9+3/QL6vHWyI8jkatuFPQzrerzQ2HXrwm2hsYGjM5s+8qMWlbt6vbxngnO8rc3lqgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAy+KIwZmU8DLmYglP3bPzrlpDaKkGu6VIJJwTOYQmRfUBAgIAAQwCAAAAuAsAAAAAAAA=",
        encodedTx
    );
  }
}
