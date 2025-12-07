package software.sava.idl.clients.spl;

import software.sava.core.accounts.AccountWithSeed;
import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.idl.clients.spl.token.gen.TokenProgram;
import software.sava.solana.programs.address_lookup_table.AddressLookupTableProgram;

import java.util.SequencedCollection;

import static java.nio.charset.StandardCharsets.US_ASCII;

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
  public Instruction createAccount(final PublicKey payerKey,
                                   final PublicKey newAccountPublicKey,
                                   final long lamports,
                                   final long space,
                                   final PublicKey programOwner) {
    return SystemProgram.createAccount(
        solanaAccounts.invokedSystemProgram(),
        payerKey,
        newAccountPublicKey,
        lamports,
        space,
        programOwner
    );
  }

  @Override
  public Instruction createAccountWithSeed(final PublicKey payerKey,
                                           final AccountWithSeed accountWithSeed,
                                           final long lamports,
                                           final long space,
                                           final PublicKey programOwner) {
    return SystemProgram.createAccountWithSeed(
        solanaAccounts.invokedSystemProgram(),
        payerKey,
        accountWithSeed.publicKey(),
        accountWithSeed.baseKey(),
        accountWithSeed.baseKey(),
        new String(accountWithSeed.asciiSeed(), US_ASCII),
        lamports,
        space,
        programOwner
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
