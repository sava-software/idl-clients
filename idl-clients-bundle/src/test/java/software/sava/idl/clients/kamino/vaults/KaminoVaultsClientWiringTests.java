package software.sava.idl.clients.kamino.vaults;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;
import software.sava.idl.clients.kamino.lend.gen.types.ReserveCollateral;
import software.sava.idl.clients.kamino.lend.gen.types.ReserveLiquidity;
import software.sava.idl.clients.kamino.vaults.gen.KaminoVaultProgram;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultState;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins each vault client builder to the generated `KaminoVaultProgram` call
/// it delegates to, with the wired constants (kLend program, kVault global
/// config, event authority, program id — the latter two appearing twice in
/// the composite `withdraw`) spelled out on the mirror side. The
/// `VaultState`/`Reserve` overloads are then checked against the explicit
/// calls: an account-sourced field landing in the wrong slot shows up as a
/// different list, and the ATA overloads' derivations are recomputed here.
final class KaminoVaultsClientWiringTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final KaminoAccounts KAMINO_ACCOUNTS = KaminoAccounts.MAIN_NET;
  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final SPLAccountClient SPL_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));
  private static final KaminoVaultsClient CLIENT = KaminoVaultsClient.createClient(SPL_CLIENT, KAMINO_ACCOUNTS);

  private static final PublicKey VAULT_STATE = key(0x20);
  private static final PublicKey TOKEN_VAULT = key(0x21);
  private static final PublicKey TOKEN_MINT = key(0x22);
  private static final PublicKey BASE_AUTHORITY = key(0x23);
  private static final PublicKey SHARES_MINT = key(0x24);
  private static final PublicKey USER_TOKEN_ATA = key(0x25);
  private static final PublicKey USER_SHARES_ATA = key(0x26);
  private static final PublicKey TOKEN_PROGRAM = key(0x27);
  private static final PublicKey SHARES_TOKEN_PROGRAM = key(0x28);
  private static final PublicKey RESERVE = key(0x29);
  private static final PublicKey CTOKEN_VAULT = key(0x2A);
  private static final PublicKey LENDING_MARKET = key(0x2B);
  private static final PublicKey LENDING_MARKET_AUTHORITY = key(0x2C);
  private static final PublicKey RESERVE_LIQUIDITY_SUPPLY = key(0x2D);
  private static final PublicKey RESERVE_COLLATERAL_MINT = key(0x2E);
  private static final PublicKey RESERVE_COLLATERAL_TOKEN_PROGRAM = key(0x2F);
  private static final PublicKey USER_CTOKEN_TA = key(0x30);

  private static final AccountMeta INVOKED = KAMINO_ACCOUNTS.invokedKVaultsProgram();
  private static final PublicKey K_LEND = KAMINO_ACCOUNTS.kLendProgram();
  private static final PublicKey GLOBAL_CONFIG = KAMINO_ACCOUNTS.kVaultGlobalConfig().publicKey();
  private static final PublicKey EVENT_AUTHORITY = KAMINO_ACCOUNTS.kVaultsEventAuthority();
  private static final PublicKey K_VAULTS = KAMINO_ACCOUNTS.kVaultsProgram();

  private static void assertIx(final Instruction expected, final Instruction actual) {
    assertEquals(expected.programId().publicKey(), actual.programId().publicKey(), "invoked program");
    assertEquals(
        expected.accounts().stream().map(AccountMeta::publicKey).toList(),
        actual.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order");
    for (int i = 0; i < expected.accounts().size(); i++) {
      assertEquals(expected.accounts().get(i).write(), actual.accounts().get(i).write(), "writable at slot " + i);
      assertEquals(expected.accounts().get(i).signer(), actual.accounts().get(i).signer(), "signer at slot " + i);
    }
    assertArrayEquals(expected.data(), actual.data(), "instruction data");
  }

  @Test
  void clientBindsItsIdentityAndDefaults() {
    assertSame(SPL_CLIENT, CLIENT.splAccountClient());
    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(KAMINO_ACCOUNTS, CLIENT.kaminoAccounts());
    assertEquals(OWNER, CLIENT.user());
    assertEquals(FEE_PAYER, CLIENT.feePayer());

    // the single-arg factory defaults the accounts to main net
    assertEquals(KaminoAccounts.MAIN_NET, KaminoVaultsClient.createClient(SPL_CLIENT).kaminoAccounts());
  }

  @Test
  void depositWithdrawAndRedeemBindTheGeneratedBuilders() {
    assertIx(
        KaminoVaultProgram.deposit(INVOKED, OWNER, VAULT_STATE, TOKEN_VAULT, TOKEN_MINT,
            BASE_AUTHORITY, SHARES_MINT, USER_TOKEN_ATA, USER_SHARES_ATA, K_LEND,
            TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, EVENT_AUTHORITY, K_VAULTS, 1_000L),
        CLIENT.deposit(VAULT_STATE, TOKEN_VAULT, TOKEN_MINT, BASE_AUTHORITY, SHARES_MINT,
            USER_TOKEN_ATA, USER_SHARES_ATA, TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 1_000L));

    // the composite withdraw carries the vault state, event authority and
    // program twice — once for the available leg, once for the invested leg
    assertIx(
        KaminoVaultProgram.withdraw(INVOKED, OWNER, VAULT_STATE, GLOBAL_CONFIG, TOKEN_VAULT,
            BASE_AUTHORITY, USER_TOKEN_ATA, TOKEN_MINT, USER_SHARES_ATA, SHARES_MINT,
            TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, K_LEND, EVENT_AUTHORITY, K_VAULTS,
            VAULT_STATE, RESERVE, CTOKEN_VAULT, LENDING_MARKET, LENDING_MARKET_AUTHORITY,
            RESERVE_LIQUIDITY_SUPPLY, RESERVE_COLLATERAL_MINT, RESERVE_COLLATERAL_TOKEN_PROGRAM,
            SOLANA_ACCOUNTS.instructionsSysVar(), EVENT_AUTHORITY, K_VAULTS, 500L),
        CLIENT.withdraw(VAULT_STATE, TOKEN_VAULT, BASE_AUTHORITY, USER_TOKEN_ATA, TOKEN_MINT,
            USER_SHARES_ATA, SHARES_MINT, TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, RESERVE,
            CTOKEN_VAULT, LENDING_MARKET, LENDING_MARKET_AUTHORITY, RESERVE_LIQUIDITY_SUPPLY,
            RESERVE_COLLATERAL_MINT, RESERVE_COLLATERAL_TOKEN_PROGRAM, 500L));

    assertIx(
        KaminoVaultProgram.withdrawFromAvailable(INVOKED, OWNER, VAULT_STATE, GLOBAL_CONFIG,
            TOKEN_VAULT, BASE_AUTHORITY, USER_TOKEN_ATA, TOKEN_MINT, USER_SHARES_ATA, SHARES_MINT,
            TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, K_LEND, EVENT_AUTHORITY, K_VAULTS, 500L),
        CLIENT.withdrawFromAvailable(VAULT_STATE, TOKEN_VAULT, BASE_AUTHORITY, USER_TOKEN_ATA,
            TOKEN_MINT, USER_SHARES_ATA, SHARES_MINT, TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L));

    assertIx(
        KaminoVaultProgram.redeemInKind(INVOKED, OWNER, VAULT_STATE, GLOBAL_CONFIG,
            BASE_AUTHORITY, RESERVE, CTOKEN_VAULT, USER_CTOKEN_TA, RESERVE_COLLATERAL_MINT,
            USER_SHARES_ATA, SHARES_MINT, RESERVE_COLLATERAL_TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM,
            K_LEND, EVENT_AUTHORITY, K_VAULTS, 500L),
        CLIENT.redeemInKind(VAULT_STATE, BASE_AUTHORITY, RESERVE, CTOKEN_VAULT, USER_CTOKEN_TA,
            RESERVE_COLLATERAL_MINT, USER_SHARES_ATA, SHARES_MINT,
            RESERVE_COLLATERAL_TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L));
  }

  /// Only the fields the overloads read are populated; a generated record
  /// accepts null for everything else.
  private static VaultState vaultState() {
    return new VaultState(VAULT_STATE, null, null, BASE_AUTHORITY, 0L, TOKEN_MINT, 0L,
        TOKEN_VAULT, TOKEN_PROGRAM, SHARES_MINT, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
        null, null, null, null, 0L, 0L, 0L, 0L, 0L, null, null, null, null, null,
        null, null, 0L, 0L, null, 0L, 0L, null, 0, 0, null, 0L, null, null);
  }

  private static Reserve reserve() {
    final var liquidity = new ReserveLiquidity(null, RESERVE_LIQUIDITY_SUPPLY, null, 0L, null,
        null, 0L, 0L, 0L, 0L, null, null, null, null, null, null, 0L, null, null);
    final var collateral = new ReserveCollateral(RESERVE_COLLATERAL_MINT, 0L, null, null, null);
    return new Reserve(RESERVE, null, 0L, null, LENDING_MARKET, null, null, liquidity, null,
        collateral, null, null, null, 0L, null, null, null);
  }

  @Test
  void vaultStateAndReserveOverloadsSourceTheAccountFields() {
    final var state = vaultState();
    assertIx(
        CLIENT.deposit(VAULT_STATE, TOKEN_VAULT, TOKEN_MINT, BASE_AUTHORITY, SHARES_MINT,
            USER_TOKEN_ATA, USER_SHARES_ATA, TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 1_000L),
        CLIENT.deposit(state, USER_TOKEN_ATA, USER_SHARES_ATA, SHARES_TOKEN_PROGRAM, 1_000L));
    assertIx(
        CLIENT.withdrawFromAvailable(VAULT_STATE, TOKEN_VAULT, BASE_AUTHORITY, USER_TOKEN_ATA,
            TOKEN_MINT, USER_SHARES_ATA, SHARES_MINT, TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L),
        CLIENT.withdrawFromAvailable(state, USER_TOKEN_ATA, USER_SHARES_ATA, SHARES_TOKEN_PROGRAM, 500L));
    assertIx(
        CLIENT.withdraw(VAULT_STATE, TOKEN_VAULT, BASE_AUTHORITY, USER_TOKEN_ATA, TOKEN_MINT,
            USER_SHARES_ATA, SHARES_MINT, TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, RESERVE,
            CTOKEN_VAULT, LENDING_MARKET, LENDING_MARKET_AUTHORITY, RESERVE_LIQUIDITY_SUPPLY,
            RESERVE_COLLATERAL_MINT, RESERVE_COLLATERAL_TOKEN_PROGRAM, 500L),
        CLIENT.withdraw(state, USER_TOKEN_ATA, USER_SHARES_ATA, SHARES_TOKEN_PROGRAM, RESERVE,
            CTOKEN_VAULT, LENDING_MARKET, LENDING_MARKET_AUTHORITY, RESERVE_LIQUIDITY_SUPPLY,
            RESERVE_COLLATERAL_MINT, RESERVE_COLLATERAL_TOKEN_PROGRAM, 500L));
  }

  @Test
  void reserveOverloadsDeriveTheLendingSideAndAtas() {
    final var state = vaultState();
    final var fetched = reserve();
    final var cTokenVault = KAMINO_ACCOUNTS.cTokenVault(VAULT_STATE, RESERVE).publicKey();
    final var marketAuthority = KAMINO_ACCOUNTS.lendingMarketAuthPda(LENDING_MARKET).publicKey();

    // the Reserve overload derives the ctoken vault and market authority and
    // reads market, supply vault and collateral mint off the account
    assertIx(
        CLIENT.withdraw(state, USER_TOKEN_ATA, USER_SHARES_ATA, SHARES_TOKEN_PROGRAM, RESERVE,
            cTokenVault, LENDING_MARKET, marketAuthority, RESERVE_LIQUIDITY_SUPPLY,
            RESERVE_COLLATERAL_MINT, RESERVE_COLLATERAL_TOKEN_PROGRAM, 500L),
        CLIENT.withdraw(state, USER_TOKEN_ATA, USER_SHARES_ATA, SHARES_TOKEN_PROGRAM,
            RESERVE_COLLATERAL_TOKEN_PROGRAM, fetched, 500L));

    // the ATA overloads derive the user's token and shares accounts
    final var userTokenAta = SPL_CLIENT.findATA(TOKEN_PROGRAM, TOKEN_MINT).publicKey();
    final var userSharesAta = SPL_CLIENT.findATA(SHARES_TOKEN_PROGRAM, SHARES_MINT).publicKey();
    assertIx(
        CLIENT.deposit(state, userTokenAta, userSharesAta, SHARES_TOKEN_PROGRAM, 1_000L),
        CLIENT.deposit(state, SHARES_TOKEN_PROGRAM, 1_000L));
    assertIx(
        CLIENT.withdrawFromAvailable(state, userTokenAta, userSharesAta, SHARES_TOKEN_PROGRAM, 500L),
        CLIENT.withdrawFromAvailable(state, SHARES_TOKEN_PROGRAM, 500L));
    assertIx(
        CLIENT.withdraw(state, userTokenAta, userSharesAta, SHARES_TOKEN_PROGRAM,
            RESERVE_COLLATERAL_TOKEN_PROGRAM, fetched, 500L),
        CLIENT.withdraw(state, SHARES_TOKEN_PROGRAM, RESERVE_COLLATERAL_TOKEN_PROGRAM, fetched, 500L));

    // redeemInKind: explicit vs Reserve-derived, then the ATA short form
    assertIx(
        CLIENT.redeemInKind(VAULT_STATE, BASE_AUTHORITY, RESERVE, cTokenVault, USER_CTOKEN_TA,
            RESERVE_COLLATERAL_MINT, USER_SHARES_ATA, SHARES_MINT,
            RESERVE_COLLATERAL_TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L),
        CLIENT.redeemInKind(state, fetched, USER_CTOKEN_TA, USER_SHARES_ATA,
            RESERVE_COLLATERAL_TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L));
    final var userCtokenTa = SPL_CLIENT.findATA(RESERVE_COLLATERAL_TOKEN_PROGRAM, RESERVE_COLLATERAL_MINT).publicKey();
    assertIx(
        CLIENT.redeemInKind(state, fetched, userCtokenTa, userSharesAta,
            RESERVE_COLLATERAL_TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L),
        CLIENT.redeemInKind(state, fetched, RESERVE_COLLATERAL_TOKEN_PROGRAM, SHARES_TOKEN_PROGRAM, 500L));
  }
}
