package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalPDAs;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.DepositFundsInstruction;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.WithdrawFundsInstruction;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Phoenix perpetuals client.
///
/// Account orders here are pinned against the protocol's own Rust SDK
/// (`rise/rust/ix/src/*.rs`, whose `build_accounts` blocks push metas in a
/// numbered, commented order). Every fund-moving instruction leads with the
/// same four accounts — program, log authority, global *configuration*, trader
/// — and the accounts that follow differ per instruction, which makes a
/// mis-slotted key easy to introduce and invisible without a positional check.
final class PhoenixClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final PhoenixAccounts ACCOUNTS = PhoenixAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey TRADER_ACCOUNT = key(0x13);
  private static final PublicKey TRADER_TOKEN_ACCOUNT = key(0x14);
  private static final PublicKey PERP_ASSET_MAP = key(0x15);
  private static final PublicKey WITHDRAW_QUEUE = key(0x16);
  private static final PublicKey DESTINATION = key(0x17);

  private static final PhoenixClient CLIENT =
      PhoenixClient.createClient(SOLANA_ACCOUNTS, ACCOUNTS);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  /// The global vault is `["vault", mint]` under the Eternal program — per
  /// mint, and a different account from the global configuration.
  @Test
  void globalVaultIsPerMintAndNotTheGlobalConfig() {
    final var usdcVault = ACCOUNTS.globalVaultPDA(ACCOUNTS.usdcMint()).publicKey();

    assertNotNull(usdcVault);
    assertEquals(usdcVault, ACCOUNTS.globalVaultPDA(ACCOUNTS.usdcMint()).publicKey(), "deterministic");
    assertNotEquals(ACCOUNTS.eternalGlobalConfig(), usdcVault,
        "the vault and the global configuration are different accounts");

    // per-mint: a different mint derives a different vault
    assertNotEquals(usdcVault, ACCOUNTS.globalVaultPDA(key(0x21)).publicKey());
    // and it is bound to the Eternal program
    assertEquals(
        PhoenixAccounts.globalVaultPDA(ACCOUNTS.usdcMint(), ACCOUNTS.invokedEternalProgram().publicKey()).publicKey(),
        usdcVault);
    assertNotEquals(
        PhoenixAccounts.globalVaultPDA(ACCOUNTS.usdcMint(), ACCOUNTS.invokedEmberProgram().publicKey()).publicKey(),
        usdcVault);
  }

  /// Regression: `depositFunds` passed the global *configuration* into the
  /// global *vault* slot — the same value it already supplies at index 2. The
  /// protocol's own SDK
  /// (`rise/rust/ix/src/deposit_funds.rs::build_accounts`) orders these as
  /// program, log authority, global configuration, trader, trader token
  /// account, trader account, global vault, token program.
  @Test
  void depositFundsFollowsTheSdkAccountOrder() {
    final var vault = ACCOUNTS.globalVaultPDA(ACCOUNTS.usdcMint()).publicKey();

    final var ix = CLIENT.depositFunds(
        OWNER, TRADER_TOKEN_ACCOUNT, TRADER_ACCOUNT, vault, SOLANA_ACCOUNTS.tokenProgram(), new DepositFundsInstruction(1_000L));
    final var accounts = keys(ix);

    assertEquals(ACCOUNTS.invokedEternalProgram(), ix.programId());
    assertEquals(ACCOUNTS.invokedEternalProgram().publicKey(), accounts.get(0), "[0] phoenix program");
    assertEquals(ACCOUNTS.eternalLogAuthority(), accounts.get(1), "[1] log authority");
    assertEquals(ACCOUNTS.eternalGlobalConfig(), accounts.get(2), "[2] global configuration");
    assertEquals(OWNER, accounts.get(3), "[3] trader wallet");
    assertEquals(TRADER_TOKEN_ACCOUNT, accounts.get(4), "[4] trader token account");
    assertEquals(TRADER_ACCOUNT, accounts.get(5), "[5] trader account");
    assertEquals(vault, accounts.get(6), "[6] global vault");
    assertEquals(SOLANA_ACCOUNTS.tokenProgram(), accounts.get(7), "[7] token program");

    // the vault slot must not repeat the configuration
    assertNotEquals(accounts.get(2), accounts.get(6),
        "the global config must not stand in for the vault");
    // the trader signs
    assertTrue(ix.accounts().stream().anyMatch(m -> m.publicKey().equals(OWNER) && m.signer()));
    // the trader index accounts follow
    assertTrue(accounts.contains(ACCOUNTS.globalTraderIndex()));
    assertTrue(accounts.contains(ACCOUNTS.activeTraderBuffer()));
  }

  /// Same fix on the withdraw side, where the vault sits between the perp asset
  /// map and the destination token account.
  @Test
  void withdrawFundsFollowsTheSdkAccountOrder() {
    final var vault = ACCOUNTS.globalVaultPDA(ACCOUNTS.usdcMint()).publicKey();

    final var ix = CLIENT.withdrawFunds(
        OWNER, TRADER_ACCOUNT, PERP_ASSET_MAP, vault, DESTINATION,
        SOLANA_ACCOUNTS.tokenProgram(), WITHDRAW_QUEUE, new WithdrawFundsInstruction(1_000L));
    final var accounts = keys(ix);

    assertEquals(ACCOUNTS.invokedEternalProgram().publicKey(), accounts.get(0), "[0] phoenix program");
    assertEquals(ACCOUNTS.eternalLogAuthority(), accounts.get(1), "[1] log authority");
    assertEquals(ACCOUNTS.eternalGlobalConfig(), accounts.get(2), "[2] global configuration");
    assertEquals(OWNER, accounts.get(3), "[3] trader wallet");
    assertEquals(TRADER_ACCOUNT, accounts.get(4), "[4] trader account");
    assertEquals(PERP_ASSET_MAP, accounts.get(5), "[5] perp asset map");
    assertEquals(vault, accounts.get(6), "[6] global vault");
    assertEquals(DESTINATION, accounts.get(7), "[7] destination token account");
    assertEquals(SOLANA_ACCOUNTS.tokenProgram(), accounts.get(8), "[8] token program");

    assertNotEquals(accounts.get(2), accounts.get(6));
    assertTrue(accounts.contains(WITHDRAW_QUEUE));
    // deposit and withdraw are distinct instructions
    assertNotEquals(
        CLIENT.depositFunds(OWNER, TRADER_TOKEN_ACCOUNT, TRADER_ACCOUNT, vault,
            SOLANA_ACCOUNTS.tokenProgram(), new DepositFundsInstruction(1_000L)).data()[0],
        ix.data()[0]);
  }

  /// Regression: the Eternal IDL declares `SyncParentToChild`'s `traderWallet`
  /// as a non-signer, but the program's own SDK pushes it as
  /// `AccountMeta::readonly_signer` — the wallet authorizes moving collateral
  /// between its own trader accounts. The generated builder follows the IDL, so
  /// the client rebuilds the meta.
  ///
  /// This is easy to miss in practice because the trader wallet is usually also
  /// the fee payer, and message compilation would then mark it a signer anyway —
  /// masking the bug for exactly the callers least likely to hit it.
  @Test
  void syncParentToChildRequiresTheTraderWalletToSign() {
    final var parent = key(0x41);
    final var child = key(0x42);

    final var ix = CLIENT.syncParentToChild(OWNER, parent, child);
    final var accounts = keys(ix);

    assertEquals(ACCOUNTS.invokedEternalProgram().publicKey(), accounts.getFirst(), "[0] phoenix program");
    assertEquals(ACCOUNTS.eternalLogAuthority(), accounts.get(1), "[1] log authority");
    assertEquals(ACCOUNTS.eternalGlobalConfig(), accounts.get(2), "[2] global configuration");
    assertEquals(OWNER, accounts.get(3), "[3] trader wallet");
    assertEquals(parent, accounts.get(4), "[4] parent trader account");
    assertEquals(child, accounts.get(5), "[5] child trader account");
    assertEquals(ACCOUNTS.globalTraderIndex(), accounts.get(6), "[6] global trader index");

    final var wallet = ix.accounts().get(3);
    assertTrue(wallet.signer(), "the trader wallet must sign");
    assertFalse(wallet.write(), "and it is read-only");

    // the child is mutated, the parent only read
    assertTrue(ix.accounts().get(5).write(), "child trader account is writable");
    assertFalse(ix.accounts().get(4).write(), "parent trader account is read-only");
    assertTrue(ix.accounts().get(6).write(), "global trader index is writable");
    // exactly one signer
    assertEquals(1, ix.accounts().stream().filter(AccountMeta::signer).count());
  }

  @Test
  void accountConstantsAreWiredAndDistinct() {
    assertEquals("EtrnLzgbS7nMMy5fbD42kXiUzGg8XQzJ972Xtk1cjWih",
        ACCOUNTS.invokedEternalProgram().publicKey().toBase58());
    assertEquals("EMBERpYNE6ehWmXymZZS2skiFmCa9V5dp14e1iduM5qy",
        ACCOUNTS.invokedEmberProgram().publicKey().toBase58());
    assertEquals("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", ACCOUNTS.usdcMint().toBase58());

    // the derived Eternal accounts come from the Eternal program
    final var eternal = ACCOUNTS.invokedEternalProgram().publicKey();
    assertEquals(EternalPDAs.globalConfigurationPDA(eternal).publicKey(), ACCOUNTS.eternalGlobalConfig());
    assertEquals(EternalPDAs.phoenixLogAuthorityPDA(eternal).publicKey(), ACCOUNTS.eternalLogAuthority());
    assertEquals(EternalPDAs.globalTraderIndexHeaderPDA(eternal).publicKey(), ACCOUNTS.globalTraderIndex());
    assertEquals(EternalPDAs.activeTraderBufferHeaderPDA(eternal).publicKey(), ACCOUNTS.activeTraderBuffer());

    // the four Eternal-derived accounts are four distinct addresses
    assertEquals(4, java.util.Set.of(
        ACCOUNTS.eternalGlobalConfig(),
        ACCOUNTS.eternalLogAuthority(),
        ACCOUNTS.globalTraderIndex(),
        ACCOUNTS.activeTraderBuffer()).size());

    // the base58 factory agrees with the key factory
    final var fromKeys = PhoenixAccounts.createAccounts(
        ACCOUNTS.invokedEmberProgram().publicKey(),
        ACCOUNTS.emberUSDCMint(),
        eternal,
        ACCOUNTS.usdcMint());
    assertEquals(ACCOUNTS.eternalGlobalConfig(), fromKeys.eternalGlobalConfig());
    assertEquals(ACCOUNTS.emberStateProgram(), fromKeys.emberStateProgram());
    assertEquals(ACCOUNTS.emberVaultProgram(), fromKeys.emberVaultProgram());
  }
}
