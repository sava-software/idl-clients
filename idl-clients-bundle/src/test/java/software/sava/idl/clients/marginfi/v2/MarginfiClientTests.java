package software.sava.idl.clients.marginfi.v2;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.marginfi.v2.gen.MarginfiPDAs;
import software.sava.idl.clients.marginfi.v2.gen.types.OrderTrigger;
import software.sava.idl.clients.marginfi.v2.gen.types.WrappedI80F48;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the marginfi v2 client.
///
/// The convenience overloads derive three different keys off the same `bank`:
/// its liquidity vault, that vault's *authority*, and the caller's ATA. Vault
/// and vault-authority are adjacent parameters on `withdraw` and `borrow`, so a
/// transposition compiles cleanly and produces an instruction the program
/// rejects — the same defect class already found in Scope and Kamino Lend.
/// These tests pin each derived key to its slot.
///
/// Account orders are ground-truthed against the live on-chain IDL (marginfi
/// `0.1.8`, program `MFv2hWf31Z9kbCa1snEPYctwafyhdvnV7FZnsebVacA`).
final class MarginfiClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final MarginfiAccounts ACCOUNTS = MarginfiAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final PublicKey MARGINFI_ACCOUNT = key(0x13);
  private static final PublicKey BANK = key(0x14);
  private static final PublicKey MINT = key(0x15);
  private static final PublicKey ORDER = key(0x16);
  private static final PublicKey GLOBAL_FEE_WALLET = key(0x17);

  private static final SPLAccountClient SPL_ACCOUNT_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));

  private static final MarginfiClient CLIENT = MarginfiClient.createClient(SPL_ACCOUNT_CLIENT, ACCOUNTS);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  private static PublicKey ata() {
    return CLIENT.splClient().findATA(OWNER, SOLANA_ACCOUNTS.tokenProgram(), MINT).publicKey();
  }

  private static PublicKey vault() {
    return ACCOUNTS.bankLiquidityVaultPDA(BANK).publicKey();
  }

  private static PublicKey vaultAuthority() {
    return ACCOUNTS.bankLiquidityVaultAuthorityPDA(BANK).publicKey();
  }

  // ---------------------------------------------------------------------------
  // accounts
  // ---------------------------------------------------------------------------

  @Test
  void accountConstantsAreWiredAndDistinct() {
    assertEquals("MFv2hWf31Z9kbCa1snEPYctwafyhdvnV7FZnsebVacA", ACCOUNTS.marginfiProgram().toBase58());
    assertEquals("4qp6Fx6tnZkY5Wropq9wUYgtFxXKwE6viZxFHg3rdAG8", ACCOUNTS.marginfiGroup().toBase58());
    assertEquals(ACCOUNTS.marginfiProgram(), ACCOUNTS.invokedMarginfiProgram().publicKey());
    assertEquals(MarginfiPDAs.feeStatePDA(ACCOUNTS.marginfiProgram()).publicKey(), ACCOUNTS.feeState());

    // the base58 factory agrees with the key factory
    final var fromKeys = MarginfiAccounts.createAccounts(ACCOUNTS.marginfiProgram(), ACCOUNTS.marginfiGroup());
    assertEquals(ACCOUNTS.marginfiGroup(), fromKeys.marginfiGroup());
    assertEquals(ACCOUNTS.feeState(), fromKeys.feeState());

    final var fromBase58 = MarginfiAccounts.createAccounts(
        "MFv2hWf31Z9kbCa1snEPYctwafyhdvnV7FZnsebVacA",
        "4qp6Fx6tnZkY5Wropq9wUYgtFxXKwE6viZxFHg3rdAG8");
    assertNotNull(fromBase58);
    assertEquals(ACCOUNTS.marginfiProgram(), fromBase58.marginfiProgram());
    assertEquals(ACCOUNTS.marginfiGroup(), fromBase58.marginfiGroup());
    assertEquals(ACCOUNTS.feeState(), fromBase58.feeState());
  }

  /// A bank's liquidity vault and that vault's authority are two different
  /// PDAs, each bound to the bank and the program.
  @Test
  void bankVaultAndVaultAuthorityAreDistinctPDAs() {
    assertNotEquals(vault(), vaultAuthority());
    assertEquals(vault(), ACCOUNTS.bankLiquidityVaultPDA(BANK).publicKey(), "deterministic");
    assertEquals(vaultAuthority(), ACCOUNTS.bankLiquidityVaultAuthorityPDA(BANK).publicKey(), "deterministic");

    // per bank
    final var otherBank = key(0x21);
    assertNotEquals(vault(), ACCOUNTS.bankLiquidityVaultPDA(otherBank).publicKey());
    assertNotEquals(vaultAuthority(), ACCOUNTS.bankLiquidityVaultAuthorityPDA(otherBank).publicKey());

    // and bound to the program
    final var otherProgram = MarginfiAccounts.createAccounts(key(0x22), ACCOUNTS.marginfiGroup());
    assertNotEquals(vault(), otherProgram.bankLiquidityVaultPDA(BANK).publicKey());
    assertNotEquals(vaultAuthority(), otherProgram.bankLiquidityVaultAuthorityPDA(BANK).publicKey());
  }

  @Test
  void clientBindsItsIdentity() {
    final var fresh = MarginfiClient.createClient(SPL_ACCOUNT_CLIENT, ACCOUNTS);
    assertNotNull(fresh);
    assertEquals(OWNER, fresh.authority());
    assertEquals(ACCOUNTS, fresh.marginfiAccounts());

    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(ACCOUNTS, CLIENT.marginfiAccounts());
    assertNotNull(CLIENT.splClient());
    assertEquals(OWNER, CLIENT.authority(), "the authority is the SPL client's owner");
    assertEquals(FEE_PAYER, CLIENT.feePayer(), "the fee payer is the SPL client's fee payer");
    assertNotEquals(CLIENT.authority(), CLIENT.feePayer());
  }

  // ---------------------------------------------------------------------------
  // account lifecycle
  // ---------------------------------------------------------------------------

  /// The no-authority overloads must substitute the client's own authority and
  /// fee payer — not the same key for both.
  @Test
  void lifecycleOverloadsSubstituteTheClientIdentity() {
    final var destination = key(0x23);

    assertEquals(
        keys(CLIENT.initializeAccount(MARGINFI_ACCOUNT, OWNER, FEE_PAYER)),
        keys(CLIENT.initializeAccount(MARGINFI_ACCOUNT)));
    assertEquals(
        keys(CLIENT.closeAccount(MARGINFI_ACCOUNT, OWNER, FEE_PAYER)),
        keys(CLIENT.closeAccount(MARGINFI_ACCOUNT)));
    assertEquals(
        keys(CLIENT.updateEmissionsDestinationAccount(MARGINFI_ACCOUNT, OWNER, destination)),
        keys(CLIENT.updateEmissionsDestinationAccount(MARGINFI_ACCOUNT, destination)));

    final var init = CLIENT.initializeAccount(MARGINFI_ACCOUNT);
    final var initKeys = keys(init);
    assertEquals(ACCOUNTS.invokedMarginfiProgram(), init.programId());
    assertEquals(ACCOUNTS.marginfiGroup(), initKeys.getFirst(), "[0] group");
    assertEquals(MARGINFI_ACCOUNT, initKeys.get(1), "[1] marginfi account");
    assertEquals(OWNER, initKeys.get(2), "[2] authority");
    assertEquals(FEE_PAYER, initKeys.get(3), "[3] fee payer");
    assertTrue(init.accounts().stream().anyMatch(m -> m.publicKey().equals(OWNER) && m.signer()));
    assertTrue(init.accounts().stream().anyMatch(m -> m.publicKey().equals(FEE_PAYER) && m.signer()));

    // the emissions destination is a distinct account from the authority
    final var emissions = keys(CLIENT.updateEmissionsDestinationAccount(MARGINFI_ACCOUNT, destination));
    assertTrue(emissions.contains(destination));
    assertNotEquals(emissions.indexOf(OWNER), emissions.indexOf(destination));
  }

  /// The PDA variants carry the account index and third-party id as *data*, so
  /// changing them must move no account.
  @Test
  void pdaVariantsCarryTheIndexAsData() {
    final var one = CLIENT.initializeAccountPda(MARGINFI_ACCOUNT, 1, OptionalInt.empty());
    final var two = CLIENT.initializeAccountPda(MARGINFI_ACCOUNT, 2, OptionalInt.empty());
    final var withId = CLIENT.initializeAccountPda(MARGINFI_ACCOUNT, 1, OptionalInt.of(7));

    assertEquals(keys(one), keys(two));
    assertFalse(Arrays.equals(one.data(), two.data()), "the account index is in the data");
    assertFalse(Arrays.equals(one.data(), withId.data()), "the third party id is in the data");

    assertEquals(
        keys(CLIENT.initializeAccountPda(MARGINFI_ACCOUNT, OWNER, FEE_PAYER, 1, OptionalInt.empty())),
        keys(one));

    // and the PDA form is a different instruction from the plain form
    assertNotEquals(CLIENT.initializeAccount(MARGINFI_ACCOUNT).data()[0], one.data()[0]);
  }

  /// `transferToNewAccount` moves an account to a *new* authority — the old and
  /// new accounts, and the old and new authorities, must not collapse.
  @Test
  void transferToNewAccountKeepsOldAndNewDistinct() {
    final var newAccount = key(0x24);
    final var newAuthority = key(0x25);

    final var ix = CLIENT.transferToNewAccount(MARGINFI_ACCOUNT, newAccount, newAuthority, GLOBAL_FEE_WALLET);
    final var accounts = keys(ix);

    assertEquals(ACCOUNTS.marginfiGroup(), accounts.getFirst(), "[0] group");
    assertEquals(MARGINFI_ACCOUNT, accounts.get(1), "[1] old account");
    assertEquals(newAccount, accounts.get(2), "[2] new account");
    assertEquals(OWNER, accounts.get(3), "[3] authority");
    assertEquals(FEE_PAYER, accounts.get(4), "[4] fee payer");
    assertEquals(newAuthority, accounts.get(5), "[5] new authority");
    assertEquals(GLOBAL_FEE_WALLET, accounts.get(6), "[6] global fee wallet");

    assertEquals(
        keys(CLIENT.transferToNewAccount(MARGINFI_ACCOUNT, newAccount, OWNER, FEE_PAYER, newAuthority, GLOBAL_FEE_WALLET)),
        accounts);

    // a keypair account must sign for itself; a derived one cannot, and the PDA
    // variant instead carries the instructions sysvar
    assertTrue(ix.accounts().get(2).signer(), "a fresh keypair account signs");
    assertFalse(keys(ix).contains(SOLANA_ACCOUNTS.instructionsSysVar()));

    final var pda = CLIENT.transferToNewAccountPda(
        MARGINFI_ACCOUNT, newAccount, newAuthority, GLOBAL_FEE_WALLET, 3, OptionalInt.of(9));
    assertEquals(accounts, keys(pda).stream().filter(k -> !k.equals(SOLANA_ACCOUNTS.instructionsSysVar())).toList(),
        "same accounts, plus the instructions sysvar");
    assertFalse(pda.accounts().get(2).signer(), "a derived account cannot sign");
    assertTrue(keys(pda).contains(SOLANA_ACCOUNTS.instructionsSysVar()));
    assertFalse(Arrays.equals(ix.data(), pda.data()));
    assertEquals(
        keys(CLIENT.transferToNewAccountPda(MARGINFI_ACCOUNT, newAccount, OWNER, FEE_PAYER,
            newAuthority, GLOBAL_FEE_WALLET, 3, OptionalInt.of(9))),
        keys(pda));
  }

  // ---------------------------------------------------------------------------
  // lending
  // ---------------------------------------------------------------------------

  /// `deposit` and `repay` take the caller's token account and the bank's
  /// liquidity vault — the vault *authority* is not among their accounts, so a
  /// mixed-up derivation shows up here as a wrong key.
  @Test
  void depositAndRepayDeriveTheAtaAndLiquidityVault() {
    final var deposit = CLIENT.deposit(MARGINFI_ACCOUNT, BANK, MINT, 1_000L);
    final var accounts = keys(deposit);

    assertEquals(ACCOUNTS.marginfiGroup(), accounts.getFirst(), "[0] group");
    assertEquals(MARGINFI_ACCOUNT, accounts.get(1), "[1] marginfi account");
    assertEquals(OWNER, accounts.get(2), "[2] authority");
    assertEquals(BANK, accounts.get(3), "[3] bank");
    assertEquals(ata(), accounts.get(4), "[4] signer token account");
    assertEquals(vault(), accounts.get(5), "[5] liquidity vault");
    assertEquals(SOLANA_ACCOUNTS.tokenProgram(), accounts.get(6), "[6] token program");

    assertNotEquals(vaultAuthority(), accounts.get(5), "the vault, not its authority");
    assertFalse(accounts.contains(vaultAuthority()), "deposit does not take the vault authority");

    assertEquals(
        keys(CLIENT.deposit(MARGINFI_ACCOUNT, OWNER, BANK, ata(), vault(), SOLANA_ACCOUNTS.tokenProgram(), 1_000L, null)),
        accounts);

    // repay shares the layout but is a different instruction
    final var repay = CLIENT.repay(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, Boolean.TRUE);
    assertEquals(accounts, keys(repay));
    assertNotEquals(deposit.data()[0], repay.data()[0]);
    // repayAll is data
    assertFalse(Arrays.equals(repay.data(), CLIENT.repay(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, null).data()));
  }

  /// Regression guard: `withdraw` takes the vault authority *before* the vault.
  /// Swapping them still compiles and still produces two real PDAs of the same
  /// bank, so only a positional check catches it.
  @Test
  void withdrawPlacesTheVaultAuthorityBeforeTheVault() {
    final var withdraw = CLIENT.withdraw(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, Boolean.FALSE);
    final var accounts = keys(withdraw);

    assertEquals(ACCOUNTS.marginfiGroup(), accounts.getFirst(), "[0] group");
    assertEquals(MARGINFI_ACCOUNT, accounts.get(1), "[1] marginfi account");
    assertEquals(OWNER, accounts.get(2), "[2] authority");
    assertEquals(BANK, accounts.get(3), "[3] bank");
    assertEquals(ata(), accounts.get(4), "[4] destination token account");
    assertEquals(vaultAuthority(), accounts.get(5), "[5] bank liquidity vault AUTHORITY");
    assertEquals(vault(), accounts.get(6), "[6] bank liquidity vault");
    assertEquals(SOLANA_ACCOUNTS.tokenProgram(), accounts.get(7), "[7] token program");

    assertNotEquals(accounts.get(5), accounts.get(6), "authority and vault must not collapse");

    assertEquals(
        keys(CLIENT.withdraw(MARGINFI_ACCOUNT, OWNER, BANK, ata(), vaultAuthority(), vault(),
            SOLANA_ACCOUNTS.tokenProgram(), 1_000L, Boolean.FALSE)),
        accounts);

    // withdrawAll is data, not an account
    assertEquals(accounts, keys(CLIENT.withdraw(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, Boolean.TRUE)));
    assertFalse(Arrays.equals(
        withdraw.data(),
        CLIENT.withdraw(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, Boolean.TRUE).data()));
  }

  /// `borrow` has withdraw's account layout and a different discriminator.
  @Test
  void borrowMatchesWithdrawsLayoutButIsADistinctInstruction() {
    final var borrow = CLIENT.borrow(MARGINFI_ACCOUNT, BANK, MINT, 1_000L);
    final var accounts = keys(borrow);

    assertEquals(ata(), accounts.get(4), "[4] destination token account");
    assertEquals(vaultAuthority(), accounts.get(5), "[5] bank liquidity vault AUTHORITY");
    assertEquals(vault(), accounts.get(6), "[6] bank liquidity vault");
    assertEquals(SOLANA_ACCOUNTS.tokenProgram(), accounts.get(7), "[7] token program");

    assertEquals(keys(CLIENT.withdraw(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, null)), accounts);
    assertNotEquals(
        CLIENT.withdraw(MARGINFI_ACCOUNT, BANK, MINT, 1_000L, null).data()[0],
        borrow.data()[0]);

    assertEquals(
        keys(CLIENT.borrow(MARGINFI_ACCOUNT, OWNER, BANK, ata(), vaultAuthority(), vault(),
            SOLANA_ACCOUNTS.tokenProgram(), 1_000L)),
        accounts);

    // the amount is data
    assertFalse(Arrays.equals(borrow.data(), CLIENT.borrow(MARGINFI_ACCOUNT, BANK, MINT, 2_000L).data()));
  }

  /// `pulseHealth` is permissionless — it must not smuggle the client's owner
  /// into the account list.
  ///
  /// `clearEmissions` used to live here. Program 0.1.9 removed
  /// `lending_account_clear_emissions` outright, so the wrapper was deleted
  /// rather than repaired — see the README.
  @Test
  void permissionlessInstructionsTakeNoAuthority() {
    final var pulse = CLIENT.pulseHealth(MARGINFI_ACCOUNT);

    assertEquals(List.of(MARGINFI_ACCOUNT), keys(pulse));
    assertFalse(keys(pulse).contains(OWNER));
    assertTrue(pulse.accounts().stream().noneMatch(AccountMeta::signer), "no signer");

    final var closeBalance = CLIENT.closeBalance(MARGINFI_ACCOUNT, BANK);
    assertEquals(List.of(ACCOUNTS.marginfiGroup(), MARGINFI_ACCOUNT, OWNER, BANK), keys(closeBalance));
    assertEquals(keys(CLIENT.closeBalance(MARGINFI_ACCOUNT, OWNER, BANK)), keys(closeBalance));
  }

  // ---------------------------------------------------------------------------
  // flash loans
  // ---------------------------------------------------------------------------

  /// The end index tells the program which instruction closes the loan, so it
  /// is data — and start/end are distinct instructions.
  @Test
  void flashloanBoundsAreDataAndTheEndsAreDistinct() {
    final var start = CLIENT.startFlashloan(MARGINFI_ACCOUNT, 3L);
    final var end = CLIENT.endFlashloan(MARGINFI_ACCOUNT);

    assertEquals(keys(start), keys(CLIENT.startFlashloan(MARGINFI_ACCOUNT, 9L)));
    assertFalse(Arrays.equals(start.data(), CLIENT.startFlashloan(MARGINFI_ACCOUNT, 9L).data()));
    assertNotEquals(start.data()[0], end.data()[0]);

    assertEquals(keys(CLIENT.startFlashloan(MARGINFI_ACCOUNT, OWNER, 3L)), keys(start));
    assertEquals(keys(CLIENT.endFlashloan(MARGINFI_ACCOUNT, OWNER)), keys(end));

    // start carries the instructions sysvar so the program can inspect the tail
    assertTrue(keys(start).contains(SOLANA_ACCOUNTS.instructionsSysVar()));
    assertFalse(keys(end).contains(SOLANA_ACCOUNTS.instructionsSysVar()));
    assertEquals(List.of(MARGINFI_ACCOUNT, OWNER), keys(end));
  }

  // ---------------------------------------------------------------------------
  // conditional orders
  // ---------------------------------------------------------------------------

  /// `placeOrder` threads the program's fee state and appends the position
  /// banks; the banks must land after the fixed accounts, in the given order.
  @Test
  void placeOrderThreadsTheFeeStateAndAppendsBanks() {
    final var otherBank = key(0x26);
    final var trigger = new OrderTrigger.StopLoss(new WrappedI80F48(new byte[16]), 100L);

    final var ix = CLIENT.placeOrder(MARGINFI_ACCOUNT, ORDER, GLOBAL_FEE_WALLET,
        new PublicKey[]{BANK, otherBank}, trigger);
    final var accounts = keys(ix);

    assertEquals(ACCOUNTS.marginfiGroup(), accounts.getFirst(), "[0] group");
    assertEquals(MARGINFI_ACCOUNT, accounts.get(1), "[1] marginfi account");
    assertEquals(FEE_PAYER, accounts.get(2), "[2] fee payer");
    assertEquals(OWNER, accounts.get(3), "[3] authority");
    assertEquals(ORDER, accounts.get(4), "[4] order");
    assertEquals(ACCOUNTS.feeState(), accounts.get(5), "[5] fee state");
    assertEquals(GLOBAL_FEE_WALLET, accounts.get(6), "[6] global fee wallet");

    // the position banks are an instruction *argument*, not accounts — they are
    // serialized into the data as a length-prefixed vector
    assertFalse(accounts.contains(BANK), "banks are data, not accounts");
    assertFalse(accounts.contains(otherBank));

    final var oneBank = CLIENT.placeOrder(MARGINFI_ACCOUNT, ORDER, GLOBAL_FEE_WALLET,
        new PublicKey[]{BANK}, trigger);
    assertEquals(accounts, keys(oneBank), "dropping a bank moves no account");
    assertEquals(ix.data().length - PublicKey.PUBLIC_KEY_LENGTH, oneBank.data().length,
        "one fewer bank is one fewer key in the data");

    // and the banks round-trip in the order given
    final var decoded = software.sava.idl.clients.marginfi.v2.gen.MarginfiProgram
        .MarginfiAccountPlaceOrderIxData.read(ix);
    assertArrayEquals(new PublicKey[]{BANK, otherBank}, decoded.bankKeys());
    // WrappedI80F48 wraps a byte[], so compare the fields rather than the record
    final var decodedTrigger = assertInstanceOf(OrderTrigger.StopLoss.class, decoded.trigger());
    assertEquals(trigger.maxSlippage(), decodedTrigger.maxSlippage());
    assertArrayEquals(trigger.threshold().value(), decodedTrigger.threshold().value());

    assertEquals(
        keys(CLIENT.placeOrder(MARGINFI_ACCOUNT, FEE_PAYER, OWNER, ORDER, GLOBAL_FEE_WALLET,
            new PublicKey[]{BANK, otherBank}, trigger)),
        accounts);
  }

  /// Closing an order refunds rent to a recipient that is not necessarily the
  /// authority — the two must occupy their own slots.
  ///
  /// Regression: program 0.1.9 prepended `group` to this instruction, shifting
  /// every account down one slot. The client kept the 0.1.8 order, so all five
  /// accounts were misaligned against the deployed program — invisible until
  /// the account list is checked by slot.
  @Test
  void closeOrderLeadsWithTheGroupAndKeepsTheFeeRecipientSeparate() {
    final var feeRecipient = key(0x27);

    final var ix = CLIENT.closeOrder(MARGINFI_ACCOUNT, ORDER, feeRecipient);
    final var accounts = keys(ix);

    assertEquals(ACCOUNTS.marginfiGroup(), accounts.getFirst(), "[0] group");
    assertEquals(MARGINFI_ACCOUNT, accounts.get(1), "[1] marginfi account");
    assertEquals(OWNER, accounts.get(2), "[2] authority");
    assertEquals(ORDER, accounts.get(3), "[3] order");
    assertEquals(feeRecipient, accounts.get(4), "[4] fee recipient");
    assertEquals(SOLANA_ACCOUNTS.systemProgram(), accounts.get(5), "[5] system program");
    assertNotEquals(accounts.get(2), accounts.get(4));

    assertEquals(keys(CLIENT.closeOrder(MARGINFI_ACCOUNT, OWNER, ORDER, feeRecipient)), accounts);
    assertNotEquals(
        CLIENT.placeOrder(MARGINFI_ACCOUNT, ORDER, GLOBAL_FEE_WALLET, new PublicKey[]{BANK},
            new OrderTrigger.StopLoss(new WrappedI80F48(new byte[16]), 100L)).data()[0],
        ix.data()[0]);
  }
}
