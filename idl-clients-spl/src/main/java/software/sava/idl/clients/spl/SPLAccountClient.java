package software.sava.idl.clients.spl;

import software.sava.core.accounts.AccountWithSeed;
import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.List;
import java.util.SequencedCollection;

public interface SPLAccountClient {

  static SPLAccountClient createClient(final SolanaAccounts accounts,
                                       final PublicKey owner,
                                       final AccountMeta feePayer) {
    final var splClient = SPLClient.createClient(accounts);
    return new SPLAccountClientImpl(splClient, owner, feePayer);
  }

  SolanaAccounts solanaAccounts();

  SPLClient splClient();

  PublicKey owner();

  AccountMeta feePayer();

  default PublicKey feePayerKey() {
    return feePayer().publicKey();
  }

  ProgramDerivedAddress wrappedSolPDA();

  Instruction syncNative();

  List<Instruction> wrapSOL(final long lamports);

  Instruction unwrapSOL();

  default ProgramDerivedAddress findATA(final PublicKey tokenProgram, final PublicKey mint) {
    return splClient().findATA(owner(), tokenProgram, mint);
  }

  default ProgramDerivedAddress findATA(final PublicKey mint) {
    return splClient().findATA(owner(), mint);
  }

  default ProgramDerivedAddress find2022ATA(final PublicKey mint) {
    return splClient().find2022ATA(owner(), mint);
  }

  Instruction transferSolLamports(final PublicKey toPublicKey, final long lamports);

  Instruction transferTokenChecked(final AccountMeta invokedTokenProgram,
                                   final PublicKey fromTokenAccount,
                                   final PublicKey toTokenAccount,
                                   final long scaledAmount,
                                   final int decimals,
                                   final PublicKey tokenMint);

  Instruction closeTokenAccount(final AccountMeta invokedTokenProgram, final PublicKey tokenAccount);

  Instruction createATAForOwnerFundedByFeePayer(final boolean idempotent,
                                                final PublicKey ata,
                                                final PublicKey mint,
                                                final PublicKey tokenProgram);

  Instruction createAccount(final PublicKey newAccountPublicKey,
                            final long lamports,
                            final long space,
                            final PublicKey programOwner);

  Instruction createAccountWithSeed(final AccountWithSeed accountWithSeed,
                                    final long lamports,
                                    final long space,
                                    final PublicKey programOwner);

  default ProgramDerivedAddress findLookupTableAddress(final PublicKey authority, final long recentSlot) {
    return splClient().findLookupTableAddress(feePayerKey(), recentSlot);
  }

  default Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount, final long recentSlot) {
    final var feePayer = feePayerKey();
    return splClient().createLookupTable(uninitializedTableAccount, feePayer, recentSlot);
  }

  default Instruction extendLookupTable(final PublicKey tableAccount,
                                        final SequencedCollection<PublicKey> newAddresses) {
    final var feePayer = feePayerKey();
    return splClient().extendLookupTable(tableAccount, feePayer, feePayer, newAddresses);
  }

  default Instruction deactivateLookupTable(final PublicKey tableAccount) {
    return splClient().deactivateLookupTable(tableAccount, feePayerKey());
  }

  default Instruction closeLookupTable(final PublicKey tableAccount, final PublicKey lamportRecipient) {
    return splClient().closeLookupTable(tableAccount, feePayerKey(), lamportRecipient);
  }

  default Instruction freezeLookupTable(final PublicKey tableAccount) {
    return splClient().freezeLookupTable(tableAccount, feePayerKey());
  }
}
