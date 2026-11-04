package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookPDAs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static software.sava.idl.clients.jupiter.offerbook.JupiterOfferbookPDAs.loanPDA;
import static software.sava.idl.clients.jupiter.offerbook.JupiterOfferbookPDAs.offerPDA;
import static software.sava.idl.clients.jupiter.offerbook.JupiterOfferbookPDAs.protocolFeeTokenAccountPDA;

/// Jupiter Offerbook's PDAs pinned to real mainnet accounts, read at slots 464659048 to
/// 464659826. A live account is the program's own PDA output, so a wrong seed derives an
/// address that does not exist on chain; see AGENTS.md on PDA helpers. Offers and loans store
/// the bump they were created with, so both are asserted.
///
/// An offer stores its creator but not its index: the index used here is the one value below
/// the creator's `User.offerCount` that reproduces the address. A loan stores both of its
/// inputs, `Loan.offer` and `Loan.fillIndex`.
final class JupiterOfferbookPDAsTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final PublicKey PROGRAM = key("offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie");
  private static final PublicKey USDC = key("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static void assertPda(final String address, final int bump, final ProgramDerivedAddress pda) {
    assertEquals(key(address), pda.publicKey());
    assertEquals(bump, pda.nonce());
  }

  @Test
  void anOfferIsKeyedByItsCreatorAndTheCreatorsOfferCount() {
    // A wallet's first offer; its User.offerCount was 6.
    assertPda("8G3nmVEPANTuCDNoEiVW3c4eQJAacssf3pWaUzZD7id9", 253,
        offerPDA(PROGRAM, key("3TMQepMaMVeRkUToM5oGs2pp4sLokV2DrL6z1s6bPJwp"), 0));
    // Index 429 of a count of 432.
    assertPda("2FGXLNCro5KUhPyNXyymrERYLJxWZH2Uq3vonQkSpZwJ", 255,
        offerPDA(PROGRAM, key("7mQUz7d7wqBAu9Sm2putzX3zFVi1AAsGd4QmgnS6T1JP"), 429));
    // The latest of a count of 327708, an index wider than two bytes.
    assertPda("HWaVszstKj2J3Qw3rphfg1kgbX8x1hmUPCFf9xNcUCYz", 253,
        offerPDA(PROGRAM, key("13UHfjKr6rpKwRXQ1KGAMcwjtY7eYBA2B5m9RBgxrFBz"), 327_707));
  }

  @Test
  void aLoanIsKeyedByItsOfferAndFillIndex() {
    assertPda("45tsqWuXKkcjfTwE52iae3XfhHNumkbECAhY4K3VmLJT", 254,
        loanPDA(PROGRAM, key("C1ozsrTNCEgGqF1wVtbEUixF816BqCvDx3ym6co9JtW7"), 74));
    assertPda("BQVsYv8TuYJ5Ubq2RXcGgR8Tv1QwmYGQ5MeVxTeB2mgQ", 250,
        loanPDA(PROGRAM, key("inJjZeC2pmzphonx7V1oBUkSvE747sCMBSFDeSk557U"), 15));
    // The first fill of the offer at index 429 above.
    assertPda("SwkqSwZGHEy2wdCCoopEwbZyzo7fN28mfiAXmbvpssJ", 255,
        loanPDA(PROGRAM, key("2FGXLNCro5KUhPyNXyymrERYLJxWZH2Uq3vonQkSpZwJ"), 0));
    assertPda("7ioRwgkomFAqqFWfrHbD5ywo4ArgXTa5RHBRTWfV4SLD", 254,
        loanPDA(PROGRAM, key("9Tqd6iiKqbggju3co57iCVk6siJ7dXiVdtU3vHbHZs26"), 0));
  }

  /// The fee authority is the address and bump `Config.feeAuthority` and `feeAuthorityBump`
  /// store, and each fee account is one a live instruction named as its
  /// `protocol_fee_token_account`: four collateral-side ones from `claim_token_loan`, one of
  /// them under Token-2022, and the USDC one every principal-side instruction names.
  @Test
  void protocolFeesGoToTheFeeAuthoritysTokenAccounts() {
    assertPda("Edt83Aw3RAELsorwJkt4DDkczrTpHsNeFGzty83TQzuy", 253, OfferbookPDAs.feeAuthorityPDA(PROGRAM));

    final var token = ACCOUNTS.tokenProgram();
    assertFeeAccount("FPPqHLNb7ekihwyUcsorxQ6YH3QxY6o4Wd6i4b9ExQpW", token, "So11111111111111111111111111111111111111112");
    assertFeeAccount("4uAshcDo2A9cBVN7SHPJMrKfExnWKJBYxyhYY9NhLYRG", ACCOUNTS.token2022Program(), "7KEPApdbBMByrmqihz3bht2uMhFQcatjfSFQCKq66kH3");
    assertFeeAccount("Bs6Z8Q6nnxSzKi1AovTzTk3swEYrnXXUSwiKMkzE9PH4", token, "AvZZF1YaZDziPY2RCK4oJrRVrbN3mTD9NL24hPeaZeUj");
    assertFeeAccount("3iAudUU6ThaVYadS9sget4vCB6DCwAMZWw8zrh4u9fyK", token, "CARDSccUMFKoPRZxt5vt3ksUbxEFEcnZ3H2pd3dKxYjp");
    assertFeeAccount("9dQQ6bsqVRur6GgRZ38kQqs8CrLCwLuACdWCtvGyq3cd", token, USDC.toBase58());
  }

  private static void assertFeeAccount(final String address, final PublicKey tokenProgram, final String mint) {
    assertEquals(key(address), protocolFeeTokenAccountPDA(ACCOUNTS, PROGRAM, tokenProgram, key(mint)).publicKey());
  }

  /// Every derivable account of the live `repay_token_loan` that [OfferbookOptionArgumentTests]
  /// reproduces, from its parties' wallets, its loan and its mints: the fee account from this
  /// class, the rest from the generated helpers, whose escrows derive under the associated token
  /// program.
  @Test
  void everyDerivableAccountOfTheLiveRepayReproduces() {
    final var borrower = key("CJ1gd8RAMfbrCqEGjU4yJ7K64N3ecAho1PCtuCviCc8W");
    final var lender = key("EqSAduwC1KFvC3eY3QuaFQTiLufUuaxxaSMWGEFQzLXs");
    final var loan = key("CW2EWehum9HSqWwWbFFYs7KgLkLeSXJyR5okv7Sn3KY5");
    final var collateralMint = key("Ce2gx9KGXJ6C9Mp5b5x1sn9Mg87JwEbrQby4Zqo3pump");
    final var token = ACCOUNTS.tokenProgram();
    final var ata = ACCOUNTS.associatedTokenAccountProgram();

    final var borrowerUser = OfferbookPDAs.signerUserPDA(PROGRAM, borrower).publicKey();
    final var lenderUser = OfferbookPDAs.signerUserPDA(PROGRAM, lender).publicKey();
    assertEquals(key("BHZBVGeYf7EcVeniMS2QVCdoBPnsPXVTTUFKLtbeheN"), borrowerUser, "signer_user");
    assertEquals(key("BCUu4b1gPjJqQ5bNnKdGxBKVEJUuakJf2yVrr8zH3M2k"), lenderUser, "lender_user");
    assertEquals(key("6uinkmtbsTKJxHDtJ1svZJLWWVUQ3B9tEVt4o3sDn7ad"), OfferbookPDAs.loanVaultPDA(PROGRAM, loan).publicKey(), "loan_vault");
    assertEquals(key("5bjsMDacvtuRjtSD4j4ARsfP6Ras3dywyyuvsbbnXNQk"), OfferbookPDAs.configPDA(PROGRAM).publicKey(), "config");
    assertEquals(key("FQw7viMe1y4GoHrVf2yAfQFw6cdw3Yw2sJSt4eCg28KD"),
        OfferbookPDAs.lenderPrincipalEscrowPDA(ata, lenderUser, token, USDC).publicKey(), "lender_principal_escrow");
    assertEquals(key("8ZmH6iv7h9G1yuqF7ppMktyoLf38DWz6AiSWi65Yy6Gd"),
        OfferbookPDAs.borrowerCollateralEscrowPDA(ata, borrowerUser, token, collateralMint).publicKey(), "borrower_collateral_escrow");
    assertEquals(key("9dQQ6bsqVRur6GgRZ38kQqs8CrLCwLuACdWCtvGyq3cd"),
        protocolFeeTokenAccountPDA(ACCOUNTS, PROGRAM, token, USDC).publicKey(), "protocol_fee_token_account");
    assertEquals(key("4sZptGwLvk2ywxa2E8UosgogwrjTxjPfFAK7o2rcKHb9"), OfferbookPDAs.eventAuthorityPDA(PROGRAM).publicKey(), "event_authority");
  }

  /// `escrow_yield_deposit` publishes `user` and `update_yield_market` publishes `yield_market` as
  /// derived from their own addresses, so neither declaration gets a helper. These are the
  /// derivations the program uses for both accounts: a user from its owner, and a yield market from
  /// its mint.
  @Test
  void aUserIsKeyedByItsOwnerAndAYieldMarketByItsMint() {
    assertPda("GH68QbfaZqdda6JpGmpZGdf7JhiKTa9qWNFAw1jioPsV", 255,
        OfferbookPDAs.signerUserPDA(PROGRAM, key("7mQUz7d7wqBAu9Sm2putzX3zFVi1AAsGd4QmgnS6T1JP")));
    assertPda("GSYR6JQ9YmwP35SaDbzxQnWJJ6heCCkteaUAQtSKasYV", 254, OfferbookPDAs.yieldMarketPDA(PROGRAM, USDC));
  }
}
