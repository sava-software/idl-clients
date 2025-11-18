package software.sava.idl.clients.spl.token;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.spl.token.gen.TokenProgram;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class TokenProgramTests {

  @Test
  void initializeMint() {
    final var mint = PublicKey.fromBase58Encoded("3ntfH5pyhTGePb2cv2gqhyBmZHVW3EggCnbq1ND7YmgX");
    final var authority = PublicKey.fromBase58Encoded("2NYZ8sqfCnH5gWwvb3E8eYv9DeMkaHQE9EZVjNBZAVYJ");
    final int decimals = 6;

    final byte[] expectedData = Base64.getDecoder().decode("""
        AAYUYI/tKjCaCLC2naec1R2jSt1cnmJq02rXYP/HQvM47wEUYI/tKjCaCLC2naec1R2jSt1cnmJq02rXYP/HQvM47w==""".stripTrailing());

    final var solAccounts = SolanaAccounts.MAIN_NET;
    var initMintIx = TokenProgram.initializeMint(
        solAccounts.invokedTokenProgram(),
        solAccounts,
        mint,
        decimals,
        authority,
        authority
    );

    assertEquals(solAccounts.invokedTokenProgram(), initMintIx.programId());

    var accounts = initMintIx.accounts();
    assertEquals(2, accounts.size());
    assertEquals(AccountMeta.createWrite(mint), accounts.getFirst());
    assertEquals(solAccounts.readRentSysVar(), accounts.getLast());

    assertArrayEquals(expectedData, initMintIx.data());

    TokenProgram.INITIALIZE_MINT_2_DISCRIMINATOR.write(expectedData);
    initMintIx = TokenProgram.initializeMint2(
        solAccounts.invokedTokenProgram(),
        mint,
        decimals,
        authority,
        authority
    );

    assertEquals(solAccounts.invokedTokenProgram(), initMintIx.programId());

    accounts = initMintIx.accounts();
    assertEquals(1, accounts.size());
    assertEquals(AccountMeta.createWrite(mint), accounts.getFirst());

    assertArrayEquals(expectedData, initMintIx.data());


    TokenProgram.INITIALIZE_MINT_DISCRIMINATOR.write(expectedData);
    initMintIx = TokenProgram.initializeMint(
        solAccounts.invokedToken2022Program(),
        solAccounts,
        mint,
        decimals,
        authority,
        authority
    );

    assertEquals(solAccounts.invokedToken2022Program(), initMintIx.programId());

    accounts = initMintIx.accounts();
    assertEquals(2, accounts.size());
    assertEquals(AccountMeta.createWrite(mint), accounts.getFirst());
    assertEquals(solAccounts.readRentSysVar(), accounts.getLast());

    assertArrayEquals(expectedData, initMintIx.data());

    TokenProgram.INITIALIZE_MINT_2_DISCRIMINATOR.write(expectedData);
    initMintIx = TokenProgram.initializeMint2(
        solAccounts.invokedToken2022Program(),
        mint,
        decimals,
        authority,
        authority
    );

    assertEquals(solAccounts.invokedToken2022Program(), initMintIx.programId());

    accounts = initMintIx.accounts();
    assertEquals(1, accounts.size());
    assertEquals(AccountMeta.createWrite(mint), accounts.getFirst());

    assertArrayEquals(expectedData, initMintIx.data());
  }
}
