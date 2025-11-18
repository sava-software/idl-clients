package software.sava.idl.clients.spl;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;
import software.sava.idl.clients.spl.token.gen.TokenProgram;
import software.sava.solana.programs.address_lookup_table.AddressLookupTableProgram;

import java.util.SequencedCollection;

record SPLClientImpl(SolanaAccounts solanaAccounts) implements SPLClient {

  @Override
  public SPLAccountClient createAccountClient(final SolanaAccounts accounts,
                                              final PublicKey owner,
                                              final AccountMeta feePayer) {
    return new SPLAccountClientImpl(this, owner, feePayer);
  }

  @Override
  public Instruction syncNative(final PublicKey tokenAccount) {
    return TokenProgram.syncNative(solanaAccounts.invokedTokenProgram(), tokenAccount);
  }

  @Override
  public ProgramDerivedAddress findATA(final PublicKey owner, final PublicKey tokenProgram, final PublicKey mint) {
    return AssociatedTokenPDAs.associatedTokenPDA(
        solanaAccounts.associatedTokenAccountProgram(),
        owner,
        tokenProgram,
        mint
    );
  }

  @Override
  public ProgramDerivedAddress findLookupTableAddress(final PublicKey authority, final long recentSlot) {
    return AddressLookupTableProgram.findLookupTableAddress(solanaAccounts, authority, recentSlot);
  }

  @Override
  public Instruction createLookupTable(final ProgramDerivedAddress uninitializedTableAccount,
                                       final PublicKey authority,
                                       final PublicKey feePayer,
                                       final long recentSlot) {
    return AddressLookupTableProgram.createLookupTable(
        solanaAccounts,
        uninitializedTableAccount.publicKey(),
        authority,
        feePayer,
        recentSlot,
        uninitializedTableAccount.nonce()
    );
  }

  @Override
  public Instruction extendLookupTable(final PublicKey tableAccount,
                                       final PublicKey authority,
                                       final PublicKey feePayer,
                                       final SequencedCollection<PublicKey> newAddresses) {
    return AddressLookupTableProgram.extendLookupTable(
        solanaAccounts,
        tableAccount,
        authority,
        feePayer,
        newAddresses
    );
  }

  @Override
  public Instruction deactivateLookupTable(final PublicKey tableAccount, final PublicKey authority) {
    return AddressLookupTableProgram.deactivateLookupTable(
        solanaAccounts,
        tableAccount,
        authority
    );
  }

  @Override
  public Instruction closeLookupTable(final PublicKey tableAccount,
                                      final PublicKey authority,
                                      final PublicKey lamportRecipient) {
    return AddressLookupTableProgram.closeLookupTable(
        solanaAccounts,
        tableAccount,
        authority,
        lamportRecipient
    );
  }

  @Override
  public Instruction freezeLookupTable(final PublicKey tableAccount, final PublicKey authority) {
    return AddressLookupTableProgram.freezeLookupTable(
        solanaAccounts,
        tableAccount,
        authority
    );
  }
}
