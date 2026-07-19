package software.sava.idl.clients.spl;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Coverage for the owner-scoped client. Its whole job is to bind an owner and a fee payer to
/// the stateless [SPLClient], so the property under test throughout is *which* of those two
/// keys each builder reaches for — they are deliberately different keys here, because a client
/// that silently used the owner where it meant the fee payer would still produce a valid-looking
/// instruction.
final class SPLAccountClientTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final PublicKey MINT = key(0x13);
  private static final PublicKey RECIPIENT = key(0x14);
  private static final PublicKey SOURCE_TOKEN = key(0x15);
  private static final PublicKey DESTINATION_TOKEN = key(0x16);
  private static final PublicKey PROGRAM_OWNER = key(0x17);

  private static final AccountMeta FEE_PAYER_META = AccountMeta.createFeePayer(FEE_PAYER);
  private static final SPLAccountClient CLIENT = SPLAccountClient.createClient(ACCOUNTS, OWNER, FEE_PAYER_META);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    java.util.Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  private static List<PublicKey> signers(final Instruction ix) {
    return ix.accounts().stream().filter(AccountMeta::signer).map(AccountMeta::publicKey).toList();
  }

  private static List<PublicKey> writable(final Instruction ix) {
    return ix.accounts().stream().filter(AccountMeta::write).map(AccountMeta::publicKey).toList();
  }

  @Test
  void clientExposesItsIdentity() {
    assertEquals(ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(OWNER, CLIENT.owner());
    assertEquals(FEE_PAYER_META, CLIENT.feePayer());
    assertEquals(FEE_PAYER, CLIENT.feePayerKey());
    assertNotNull(CLIENT.splClient());
    assertEquals(ACCOUNTS, CLIENT.splClient().solanaAccounts());
  }

  /// The wrapped-SOL account is the owner's ATA for the native mint, resolved once at
  /// construction — it must belong to the owner, not the fee payer.
  @Test
  void wrappedSolPDAIsTheOwnersNativeMintATA() {
    final var wrappedSol = CLIENT.wrappedSolPDA();

    assertNotNull(wrappedSol.publicKey());
    assertEquals(
        CLIENT.splClient().findATA(OWNER, ACCOUNTS.tokenProgram(), ACCOUNTS.wrappedSolTokenMint()).publicKey(),
        wrappedSol.publicKey());

    // a client with a different owner resolves a different wrapped-SOL account
    final var otherOwner = SPLAccountClient.createClient(ACCOUNTS, RECIPIENT, FEE_PAYER_META);
    assertNotEquals(wrappedSol.publicKey(), otherOwner.wrappedSolPDA().publicKey());

    // but changing only the fee payer does not move it
    final var otherPayer = SPLAccountClient.createClient(ACCOUNTS, OWNER, AccountMeta.createFeePayer(RECIPIENT));
    assertEquals(wrappedSol.publicKey(), otherPayer.wrappedSolPDA().publicKey());
  }

  @Test
  void findATADelegatesWithTheOwner() {
    final var splClient = CLIENT.splClient();

    assertEquals(
        splClient.findATA(OWNER, ACCOUNTS.tokenProgram(), MINT).publicKey(),
        CLIENT.findATA(ACCOUNTS.tokenProgram(), MINT).publicKey());
    assertEquals(
        splClient.findATA(OWNER, MINT).publicKey(),
        CLIENT.findATA(MINT).publicKey());
    assertEquals(
        splClient.find2022ATA(OWNER, MINT).publicKey(),
        CLIENT.find2022ATA(MINT).publicKey());

    // the classic and 2022 token programs derive different addresses
    assertNotEquals(CLIENT.findATA(MINT).publicKey(), CLIENT.find2022ATA(MINT).publicKey());
  }

  @Test
  void syncNativeTargetsTheWrappedSolAccount() {
    final var ix = CLIENT.syncNative();

    assertEquals(ACCOUNTS.invokedTokenProgram(), ix.programId());
    assertEquals(List.of(CLIENT.wrappedSolPDA().publicKey()), keys(ix));
  }

  @Test
  void transferSolLamportsSendsFromTheOwner() {
    final var ix = CLIENT.transferSolLamports(RECIPIENT, 5_000L);

    assertEquals(ACCOUNTS.invokedSystemProgram(), ix.programId());
    // the owner funds the transfer, not the fee payer
    assertEquals(List.of(OWNER, RECIPIENT), keys(ix));
    assertEquals(List.of(OWNER), signers(ix));
    assertEquals(5_000L, software.sava.core.encoding.ByteUtil.getInt64LE(ix.data(), 4));
  }

  /// Wrapping is a transfer into the wrapped-SOL account followed by a sync; the order matters,
  /// because syncing before the lamports arrive is a no-op.
  @Test
  void wrapSOLTransfersThenSyncs() {
    final var wrappedSol = CLIENT.wrappedSolPDA().publicKey();
    final var instructions = CLIENT.wrapSOL(7_000L);

    assertEquals(2, instructions.size());

    final var transfer = instructions.getFirst();
    assertEquals(ACCOUNTS.invokedSystemProgram(), transfer.programId());
    assertEquals(List.of(OWNER, wrappedSol), keys(transfer));
    assertEquals(7_000L, software.sava.core.encoding.ByteUtil.getInt64LE(transfer.data(), 4));

    final var sync = instructions.getLast();
    assertEquals(ACCOUNTS.invokedTokenProgram(), sync.programId());
    assertEquals(List.of(wrappedSol), keys(sync));
  }

  /// Unwrapping closes the wrapped-SOL account, returning both the rent and the wrapped balance
  /// to the owner — the fee payer must not be the beneficiary.
  @Test
  void unwrapSOLClosesToTheOwner() {
    final var ix = CLIENT.unwrapSOL();

    assertEquals(ACCOUNTS.invokedTokenProgram(), ix.programId());
    assertEquals(List.of(CLIENT.wrappedSolPDA().publicKey(), OWNER, OWNER), keys(ix));
    assertEquals(List.of(OWNER), signers(ix));
  }

  @Test
  void closeTokenAccountReturnsToTheOwner() {
    final var ix = CLIENT.closeTokenAccount(ACCOUNTS.invokedTokenProgram(), SOURCE_TOKEN);

    assertEquals(List.of(SOURCE_TOKEN, OWNER, OWNER), keys(ix));
    assertEquals(List.of(OWNER), signers(ix));
    assertEquals(List.of(SOURCE_TOKEN, OWNER), writable(ix));

    // the token program is a parameter, so a 2022 account closes through the 2022 program
    assertEquals(
        ACCOUNTS.invokedToken2022Program(),
        CLIENT.closeTokenAccount(ACCOUNTS.invokedToken2022Program(), SOURCE_TOKEN).programId());
  }

  /// `transferChecked` interleaves the mint between source and destination; the owner is the
  /// signing authority, and the mint must not land in a token-account slot.
  @Test
  void transferTokenCheckedOrdersSourceMintDestination() {
    final var ix = CLIENT.transferTokenChecked(
        ACCOUNTS.invokedTokenProgram(), SOURCE_TOKEN, DESTINATION_TOKEN, 1_500L, 6, MINT);

    assertEquals(ACCOUNTS.invokedTokenProgram(), ix.programId());
    assertEquals(List.of(SOURCE_TOKEN, MINT, DESTINATION_TOKEN, OWNER), keys(ix));
    assertEquals(List.of(OWNER), signers(ix));
    assertEquals(List.of(SOURCE_TOKEN, DESTINATION_TOKEN), writable(ix));

    // amount then decimals
    assertEquals(1_500L, software.sava.core.encoding.ByteUtil.getInt64LE(ix.data(), 1));
    assertEquals(6, ix.data()[9]);
  }

  /// The ATA is created *for the owner* but *funded by the fee payer* — the whole point of the
  /// method. Only the payer signs and only the payer's balance is debited.
  @Test
  void createATAForOwnerFundedByFeePayer() {
    final var ata = CLIENT.findATA(MINT).publicKey();

    final var ix = CLIENT.createATAForOwnerFundedByFeePayer(false, ata, MINT, ACCOUNTS.tokenProgram());

    assertEquals(ACCOUNTS.invokedAssociatedTokenAccountProgram(), ix.programId());
    assertEquals(
        List.of(FEE_PAYER, ata, OWNER, MINT, ACCOUNTS.systemProgram(), ACCOUNTS.tokenProgram()),
        keys(ix));
    assertEquals(List.of(FEE_PAYER), signers(ix), "only the fee payer signs");
    assertEquals(List.of(FEE_PAYER, ata), writable(ix));
  }

  /// The idempotent variant differs only in its discriminator — same accounts, different
  /// behavior when the account already exists.
  @Test
  void createATAIdempotentSelectsADifferentInstruction() {
    final var ata = CLIENT.findATA(MINT).publicKey();

    final var plain = CLIENT.createATAForOwnerFundedByFeePayer(false, ata, MINT, ACCOUNTS.tokenProgram());
    final var idempotent = CLIENT.createATAForOwnerFundedByFeePayer(true, ata, MINT, ACCOUNTS.tokenProgram());

    assertEquals(keys(plain), keys(idempotent));
    assertFalse(java.util.Arrays.equals(plain.data(), idempotent.data()),
        "idempotent creation must be a distinct instruction");
  }

  @Test
  void createAccountIsFundedByTheFeePayer() {
    final var newAccount = key(0x21);
    final var ix = CLIENT.createAccount(newAccount, 1_000L, 200L, PROGRAM_OWNER);

    assertEquals(ACCOUNTS.invokedSystemProgram(), ix.programId());
    // the fee payer funds it, not the owner
    assertEquals(List.of(FEE_PAYER, newAccount), keys(ix));
    assertEquals(List.of(FEE_PAYER, newAccount), signers(ix));
    assertEquals(1_000L, software.sava.core.encoding.ByteUtil.getInt64LE(ix.data(), 4));
    assertEquals(200L, software.sava.core.encoding.ByteUtil.getInt64LE(ix.data(), 12));
    assertEquals(PROGRAM_OWNER, PublicKey.readPubKey(ix.data(), 20));
  }

  @Test
  void createAccountWithSeedIsFundedByTheFeePayer() {
    final var base = key(0x22);
    final var seeded = PublicKey.createOffCurveAccountWithAsciiSeed(base, "seed", PROGRAM_OWNER);

    final var ix = CLIENT.createAccountWithSeed(seeded, 1_000L, 200L, PROGRAM_OWNER);

    assertEquals(ACCOUNTS.invokedSystemProgram(), ix.programId());
    assertTrue(keys(ix).contains(FEE_PAYER), "the fee payer funds the account");
    assertTrue(keys(ix).contains(seeded.publicKey()));
    assertFalse(keys(ix).contains(OWNER), "the owner is not involved in a seeded creation");
  }

  // ---------------------------------------------------------------------------
  // Lookup tables: all of these bind the fee payer as the authority
  // ---------------------------------------------------------------------------

  @Test
  void lookupTableHelpersUseTheFeePayerAsAuthority() {
    final var splClient = CLIENT.splClient();
    final var table = CLIENT.findLookupTableAddress(100L);

    assertEquals(splClient.findLookupTableAddress(FEE_PAYER, 100L).publicKey(), table.publicKey());
    assertNotEquals(splClient.findLookupTableAddress(OWNER, 100L).publicKey(), table.publicKey());

    final var create = CLIENT.createLookupTable(table, 100L);
    assertEquals(List.of(table.publicKey(), FEE_PAYER, FEE_PAYER, ACCOUNTS.systemProgram()), keys(create));

    final var extend = CLIENT.extendLookupTable(table.publicKey(), List.of(MINT, RECIPIENT));
    assertEquals(List.of(table.publicKey(), FEE_PAYER, FEE_PAYER, ACCOUNTS.systemProgram()), keys(extend));

    final var deactivate = CLIENT.deactivateLookupTable(table.publicKey());
    assertEquals(List.of(table.publicKey(), FEE_PAYER), keys(deactivate));
    assertEquals(List.of(FEE_PAYER), signers(deactivate));

    final var freeze = CLIENT.freezeLookupTable(table.publicKey());
    assertEquals(List.of(table.publicKey(), FEE_PAYER), keys(freeze));

    // close directs the reclaimed rent wherever the caller asks
    final var close = CLIENT.closeLookupTable(table.publicKey(), RECIPIENT);
    assertEquals(List.of(table.publicKey(), FEE_PAYER, RECIPIENT), keys(close));
    assertEquals(List.of(FEE_PAYER), signers(close));
  }
}
