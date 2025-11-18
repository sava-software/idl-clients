package software.sava.idl.clients.spl;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;

import java.util.SequencedCollection;

import static software.sava.solana.programs.compute_budget.ComputeBudgetProgram.*;

public interface SPLClient {

  static SPLClient createClient(final SolanaAccounts solanaAccounts) {
    return new SPLClientImpl(solanaAccounts);
  }

  SolanaAccounts solanaAccounts();

  SPLAccountClient createAccountClient(final SolanaAccounts accounts,
                                       final PublicKey owner,
                                       final AccountMeta feePayer);

  Instruction syncNative(final PublicKey tokenAccount);

  ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey tokenProgram, final PublicKey mint);

  default ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey mint) {
    return findATA(owner, solanaAccounts().tokenProgram(), mint);
  }

  default ProgramDerivedAddress find2022ATA(final PublicKey owner, final PublicKey mint) {
    return findATA(owner, solanaAccounts().token2022Program(), mint);
  }

  ProgramDerivedAddress findLookupTableAddress(final PublicKey authority, final long recentSlot);

  Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount,
                                final PublicKey authority,
                                final PublicKey feePayer,
                                final long recentSlot);

  default Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount,
                                        final PublicKey authority,
                                        final long recentSlot) {
    return createLookupTable(uninitializedTableAccount, authority, authority, recentSlot);
  }

  Instruction extendLookupTable(final PublicKey tableAccount,
                                final PublicKey authority,
                                final PublicKey feePayer,
                                final SequencedCollection<PublicKey> newAddresses);

  default Instruction extendLookupTable(final PublicKey tableAccount,
                                        final PublicKey authority,
                                        final SequencedCollection<PublicKey> newAddresses) {
    return extendLookupTable(tableAccount, authority, authority, newAddresses);
  }

  Instruction deactivateLookupTable(final PublicKey tableAccount, final PublicKey authority);

  Instruction closeLookupTable(final PublicKey tableAccount,
                               final PublicKey authority,
                               final PublicKey lamportRecipient);

  default Instruction closeLookupTable(final PublicKey tableAccount, final PublicKey authority) {
    return closeLookupTable(tableAccount, authority, authority);
  }

  Instruction freezeLookupTable(final PublicKey tableAccount, final PublicKey authority);

  default Instruction computeBudgetLimit(final int computeUnitLimit) {
    return setComputeUnitLimit(solanaAccounts().invokedComputeBudgetProgram(), computeUnitLimit + COMPUTE_UNITS_CONSUMED);
  }

  default Instruction computeBudgetPrice(final long microLamportComputeUnitPrice) {
    return setComputeUnitPrice(solanaAccounts().invokedComputeBudgetProgram(), microLamportComputeUnitPrice);
  }
}
