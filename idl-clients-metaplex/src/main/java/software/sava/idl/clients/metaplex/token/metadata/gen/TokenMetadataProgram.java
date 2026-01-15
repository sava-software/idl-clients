package software.sava.idl.clients.metaplex.token.metadata.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.ApproveUseAuthorityArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.BurnArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.CreateArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.CreateMasterEditionArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.CreateMetadataAccountArgsV3;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.DelegateArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.LockArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.MintArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.MintNewEditionFromMasterEditionViaTokenArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.PrintArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.RevokeArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.SetCollectionSizeArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.TransferArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.TransferOutOfEscrowArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.UnlockArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.UpdateArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.UpdateMetadataAccountArgsV2;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.UseArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.UtilizeArgs;
import software.sava.idl.clients.metaplex.token.metadata.gen.types.VerificationArgs;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class TokenMetadataProgram {

  public static final Discriminator CREATE_METADATA_ACCOUNT_DISCRIMINATOR = toDiscriminator(75, 73, 45, 178, 212, 194, 127, 113);

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param mintKey Mint of token asset
  /// @param mintAuthorityKey Mint authority
  /// @param payerKey payer
  /// @param updateAuthorityKey update authority info
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> createMetadataAccountKeys(final PublicKey metadataKey,
                                                            final PublicKey mintKey,
                                                            final PublicKey mintAuthorityKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey updateAuthorityKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey rentKey) {
    return List.of(
      createWrite(metadataKey),
      createRead(mintKey),
      createReadOnlySigner(mintAuthorityKey),
      createWritableSigner(payerKey),
      createRead(updateAuthorityKey),
      createRead(systemProgramKey),
      createRead(rentKey)
    );
  }

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param mintKey Mint of token asset
  /// @param mintAuthorityKey Mint authority
  /// @param payerKey payer
  /// @param updateAuthorityKey update authority info
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction createMetadataAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                  final PublicKey metadataKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey mintAuthorityKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey updateAuthorityKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey rentKey) {
    final var keys = createMetadataAccountKeys(
      metadataKey,
      mintKey,
      mintAuthorityKey,
      payerKey,
      updateAuthorityKey,
      systemProgramKey,
      rentKey
    );
    return createMetadataAccount(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction createMetadataAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CREATE_METADATA_ACCOUNT_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_METADATA_ACCOUNT_DISCRIMINATOR = toDiscriminator(141, 14, 23, 104, 247, 192, 53, 173);

  /// @param metadataKey Metadata account
  /// @param updateAuthorityKey Update authority key
  public static List<AccountMeta> updateMetadataAccountKeys(final PublicKey metadataKey,
                                                            final PublicKey updateAuthorityKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// @param metadataKey Metadata account
  /// @param updateAuthorityKey Update authority key
  public static Instruction updateMetadataAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                  final PublicKey metadataKey,
                                                  final PublicKey updateAuthorityKey) {
    final var keys = updateMetadataAccountKeys(
      metadataKey,
      updateAuthorityKey
    );
    return updateMetadataAccount(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction updateMetadataAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, UPDATE_METADATA_ACCOUNT_DISCRIMINATOR);
  }

  public static final Discriminator DEPRECATED_CREATE_MASTER_EDITION_DISCRIMINATOR = toDiscriminator(155, 127, 165, 159, 236, 92, 79, 21);

  /// @param editionKey Unallocated edition V1 account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Metadata mint
  /// @param printingMintKey Printing mint - A mint you control that can mint tokens that can be exchanged for limited editions of your master edition via the MintNewEditionFromMasterEditionViaToken endpoint
  /// @param oneTimePrintingAuthorizationMintKey One time authorization printing mint - A mint you control that prints tokens that gives the bearer permission to mint any number of tokens from the printing mint one time via an endpoint with the token-metadata program for your metadata. Also burns the token.
  /// @param updateAuthorityKey Current Update authority key
  /// @param printingMintAuthorityKey Printing mint authority - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY.
  /// @param mintAuthorityKey Mint authority on the metadata's mint - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param metadataKey Metadata account
  /// @param payerKey payer
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  /// @param oneTimePrintingAuthorizationMintAuthorityKey One time authorization printing mint authority - must be provided if using max supply. THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY.
  public static List<AccountMeta> deprecatedCreateMasterEditionKeys(final PublicKey editionKey,
                                                                    final PublicKey mintKey,
                                                                    final PublicKey printingMintKey,
                                                                    final PublicKey oneTimePrintingAuthorizationMintKey,
                                                                    final PublicKey updateAuthorityKey,
                                                                    final PublicKey printingMintAuthorityKey,
                                                                    final PublicKey mintAuthorityKey,
                                                                    final PublicKey metadataKey,
                                                                    final PublicKey payerKey,
                                                                    final PublicKey tokenProgramKey,
                                                                    final PublicKey systemProgramKey,
                                                                    final PublicKey rentKey,
                                                                    final PublicKey oneTimePrintingAuthorizationMintAuthorityKey) {
    return List.of(
      createWrite(editionKey),
      createWrite(mintKey),
      createWrite(printingMintKey),
      createWrite(oneTimePrintingAuthorizationMintKey),
      createReadOnlySigner(updateAuthorityKey),
      createReadOnlySigner(printingMintAuthorityKey),
      createReadOnlySigner(mintAuthorityKey),
      createRead(metadataKey),
      createReadOnlySigner(payerKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(rentKey),
      createReadOnlySigner(oneTimePrintingAuthorizationMintAuthorityKey)
    );
  }

  /// @param editionKey Unallocated edition V1 account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Metadata mint
  /// @param printingMintKey Printing mint - A mint you control that can mint tokens that can be exchanged for limited editions of your master edition via the MintNewEditionFromMasterEditionViaToken endpoint
  /// @param oneTimePrintingAuthorizationMintKey One time authorization printing mint - A mint you control that prints tokens that gives the bearer permission to mint any number of tokens from the printing mint one time via an endpoint with the token-metadata program for your metadata. Also burns the token.
  /// @param updateAuthorityKey Current Update authority key
  /// @param printingMintAuthorityKey Printing mint authority - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY.
  /// @param mintAuthorityKey Mint authority on the metadata's mint - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param metadataKey Metadata account
  /// @param payerKey payer
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  /// @param oneTimePrintingAuthorizationMintAuthorityKey One time authorization printing mint authority - must be provided if using max supply. THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY.
  public static Instruction deprecatedCreateMasterEdition(final AccountMeta invokedTokenMetadataProgramMeta,
                                                          final PublicKey editionKey,
                                                          final PublicKey mintKey,
                                                          final PublicKey printingMintKey,
                                                          final PublicKey oneTimePrintingAuthorizationMintKey,
                                                          final PublicKey updateAuthorityKey,
                                                          final PublicKey printingMintAuthorityKey,
                                                          final PublicKey mintAuthorityKey,
                                                          final PublicKey metadataKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey systemProgramKey,
                                                          final PublicKey rentKey,
                                                          final PublicKey oneTimePrintingAuthorizationMintAuthorityKey) {
    final var keys = deprecatedCreateMasterEditionKeys(
      editionKey,
      mintKey,
      printingMintKey,
      oneTimePrintingAuthorizationMintKey,
      updateAuthorityKey,
      printingMintAuthorityKey,
      mintAuthorityKey,
      metadataKey,
      payerKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey,
      oneTimePrintingAuthorizationMintAuthorityKey
    );
    return deprecatedCreateMasterEdition(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction deprecatedCreateMasterEdition(final AccountMeta invokedTokenMetadataProgramMeta,
                                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, DEPRECATED_CREATE_MASTER_EDITION_DISCRIMINATOR);
  }

  public static final Discriminator DEPRECATED_MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_PRINTING_TOKEN_DISCRIMINATOR = toDiscriminator(154, 36, 174, 111, 190, 80, 155, 228);

  /// @param metadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param editionKey New Edition V1 (pda of 'metadata', program id, mint id, 'edition')
  /// @param masterEditionKey Master Record Edition V1 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param mintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param mintAuthorityKey Mint authority of new mint
  /// @param printingMintKey Printing Mint of master record edition
  /// @param masterTokenAccountKey Token account containing Printing mint token to be transferred
  /// @param editionMarkerKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master mint id, edition_number)
  /// @param burnAuthorityKey Burn authority for this token
  /// @param payerKey payer
  /// @param masterUpdateAuthorityKey update authority info for new metadata account
  /// @param masterMetadataKey Master record metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  /// @param reservationListKey Reservation List - If present, and you are on this list, you can get an edition number given by your position on the list.
  public static List<AccountMeta> deprecatedMintNewEditionFromMasterEditionViaPrintingTokenKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                                                final PublicKey metadataKey,
                                                                                                final PublicKey editionKey,
                                                                                                final PublicKey masterEditionKey,
                                                                                                final PublicKey mintKey,
                                                                                                final PublicKey mintAuthorityKey,
                                                                                                final PublicKey printingMintKey,
                                                                                                final PublicKey masterTokenAccountKey,
                                                                                                final PublicKey editionMarkerKey,
                                                                                                final PublicKey burnAuthorityKey,
                                                                                                final PublicKey payerKey,
                                                                                                final PublicKey masterUpdateAuthorityKey,
                                                                                                final PublicKey masterMetadataKey,
                                                                                                final PublicKey tokenProgramKey,
                                                                                                final PublicKey systemProgramKey,
                                                                                                final PublicKey rentKey,
                                                                                                final PublicKey reservationListKey) {
    return List.of(
      createWrite(metadataKey),
      createWrite(editionKey),
      createWrite(masterEditionKey),
      createWrite(mintKey),
      createReadOnlySigner(mintAuthorityKey),
      createWrite(printingMintKey),
      createWrite(masterTokenAccountKey),
      createWrite(editionMarkerKey),
      createReadOnlySigner(burnAuthorityKey),
      createReadOnlySigner(payerKey),
      createRead(masterUpdateAuthorityKey),
      createRead(masterMetadataKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(rentKey),
      createWrite(requireNonNullElse(reservationListKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param editionKey New Edition V1 (pda of 'metadata', program id, mint id, 'edition')
  /// @param masterEditionKey Master Record Edition V1 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param mintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param mintAuthorityKey Mint authority of new mint
  /// @param printingMintKey Printing Mint of master record edition
  /// @param masterTokenAccountKey Token account containing Printing mint token to be transferred
  /// @param editionMarkerKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master mint id, edition_number)
  /// @param burnAuthorityKey Burn authority for this token
  /// @param payerKey payer
  /// @param masterUpdateAuthorityKey update authority info for new metadata account
  /// @param masterMetadataKey Master record metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  /// @param reservationListKey Reservation List - If present, and you are on this list, you can get an edition number given by your position on the list.
  public static Instruction deprecatedMintNewEditionFromMasterEditionViaPrintingToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                                      final PublicKey metadataKey,
                                                                                      final PublicKey editionKey,
                                                                                      final PublicKey masterEditionKey,
                                                                                      final PublicKey mintKey,
                                                                                      final PublicKey mintAuthorityKey,
                                                                                      final PublicKey printingMintKey,
                                                                                      final PublicKey masterTokenAccountKey,
                                                                                      final PublicKey editionMarkerKey,
                                                                                      final PublicKey burnAuthorityKey,
                                                                                      final PublicKey payerKey,
                                                                                      final PublicKey masterUpdateAuthorityKey,
                                                                                      final PublicKey masterMetadataKey,
                                                                                      final PublicKey tokenProgramKey,
                                                                                      final PublicKey systemProgramKey,
                                                                                      final PublicKey rentKey,
                                                                                      final PublicKey reservationListKey) {
    final var keys = deprecatedMintNewEditionFromMasterEditionViaPrintingTokenKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      editionKey,
      masterEditionKey,
      mintKey,
      mintAuthorityKey,
      printingMintKey,
      masterTokenAccountKey,
      editionMarkerKey,
      burnAuthorityKey,
      payerKey,
      masterUpdateAuthorityKey,
      masterMetadataKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey,
      reservationListKey
    );
    return deprecatedMintNewEditionFromMasterEditionViaPrintingToken(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction deprecatedMintNewEditionFromMasterEditionViaPrintingToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, DEPRECATED_MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_PRINTING_TOKEN_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_PRIMARY_SALE_HAPPENED_VIA_TOKEN_DISCRIMINATOR = toDiscriminator(172, 129, 173, 210, 222, 129, 243, 98);

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param ownerKey Owner on the token account
  /// @param tokenKey Account containing tokens from the metadata's mint
  public static List<AccountMeta> updatePrimarySaleHappenedViaTokenKeys(final PublicKey metadataKey,
                                                                        final PublicKey ownerKey,
                                                                        final PublicKey tokenKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(ownerKey),
      createRead(tokenKey)
    );
  }

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param ownerKey Owner on the token account
  /// @param tokenKey Account containing tokens from the metadata's mint
  public static Instruction updatePrimarySaleHappenedViaToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                              final PublicKey metadataKey,
                                                              final PublicKey ownerKey,
                                                              final PublicKey tokenKey) {
    final var keys = updatePrimarySaleHappenedViaTokenKeys(
      metadataKey,
      ownerKey,
      tokenKey
    );
    return updatePrimarySaleHappenedViaToken(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction updatePrimarySaleHappenedViaToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, UPDATE_PRIMARY_SALE_HAPPENED_VIA_TOKEN_DISCRIMINATOR);
  }

  public static final Discriminator DEPRECATED_SET_RESERVATION_LIST_DISCRIMINATOR = toDiscriminator(68, 28, 66, 19, 59, 203, 190, 142);

  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param reservationListKey PDA for ReservationList of 'metadata', program id, master edition key, 'reservation', resource-key
  /// @param resourceKey The resource you tied the reservation list too
  public static List<AccountMeta> deprecatedSetReservationListKeys(final PublicKey masterEditionKey,
                                                                   final PublicKey reservationListKey,
                                                                   final PublicKey resourceKey) {
    return List.of(
      createWrite(masterEditionKey),
      createWrite(reservationListKey),
      createReadOnlySigner(resourceKey)
    );
  }

  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param reservationListKey PDA for ReservationList of 'metadata', program id, master edition key, 'reservation', resource-key
  /// @param resourceKey The resource you tied the reservation list too
  public static Instruction deprecatedSetReservationList(final AccountMeta invokedTokenMetadataProgramMeta,
                                                         final PublicKey masterEditionKey,
                                                         final PublicKey reservationListKey,
                                                         final PublicKey resourceKey) {
    final var keys = deprecatedSetReservationListKeys(
      masterEditionKey,
      reservationListKey,
      resourceKey
    );
    return deprecatedSetReservationList(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction deprecatedSetReservationList(final AccountMeta invokedTokenMetadataProgramMeta,
                                                         final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, DEPRECATED_SET_RESERVATION_LIST_DISCRIMINATOR);
  }

  public static final Discriminator DEPRECATED_CREATE_RESERVATION_LIST_DISCRIMINATOR = toDiscriminator(171, 227, 161, 158, 1, 176, 105, 72);

  /// @param reservationListKey PDA for ReservationList of 'metadata', program id, master edition key, 'reservation', resource-key
  /// @param payerKey Payer
  /// @param updateAuthorityKey Update authority
  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param resourceKey A resource you wish to tie the reservation list to. This is so your later visitors who come to redeem can derive your reservation list PDA with something they can easily get at. You choose what this should be.
  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> deprecatedCreateReservationListKeys(final PublicKey reservationListKey,
                                                                      final PublicKey payerKey,
                                                                      final PublicKey updateAuthorityKey,
                                                                      final PublicKey masterEditionKey,
                                                                      final PublicKey resourceKey,
                                                                      final PublicKey metadataKey,
                                                                      final PublicKey systemProgramKey,
                                                                      final PublicKey rentKey) {
    return List.of(
      createWrite(reservationListKey),
      createReadOnlySigner(payerKey),
      createReadOnlySigner(updateAuthorityKey),
      createRead(masterEditionKey),
      createRead(resourceKey),
      createRead(metadataKey),
      createRead(systemProgramKey),
      createRead(rentKey)
    );
  }

  /// @param reservationListKey PDA for ReservationList of 'metadata', program id, master edition key, 'reservation', resource-key
  /// @param payerKey Payer
  /// @param updateAuthorityKey Update authority
  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param resourceKey A resource you wish to tie the reservation list to. This is so your later visitors who come to redeem can derive your reservation list PDA with something they can easily get at. You choose what this should be.
  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction deprecatedCreateReservationList(final AccountMeta invokedTokenMetadataProgramMeta,
                                                            final PublicKey reservationListKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey updateAuthorityKey,
                                                            final PublicKey masterEditionKey,
                                                            final PublicKey resourceKey,
                                                            final PublicKey metadataKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey rentKey) {
    final var keys = deprecatedCreateReservationListKeys(
      reservationListKey,
      payerKey,
      updateAuthorityKey,
      masterEditionKey,
      resourceKey,
      metadataKey,
      systemProgramKey,
      rentKey
    );
    return deprecatedCreateReservationList(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction deprecatedCreateReservationList(final AccountMeta invokedTokenMetadataProgramMeta,
                                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, DEPRECATED_CREATE_RESERVATION_LIST_DISCRIMINATOR);
  }

  public static final Discriminator SIGN_METADATA_DISCRIMINATOR = toDiscriminator(178, 245, 253, 205, 236, 250, 233, 209);

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param creatorKey Creator
  public static List<AccountMeta> signMetadataKeys(final PublicKey metadataKey,
                                                   final PublicKey creatorKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(creatorKey)
    );
  }

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param creatorKey Creator
  public static Instruction signMetadata(final AccountMeta invokedTokenMetadataProgramMeta,
                                         final PublicKey metadataKey,
                                         final PublicKey creatorKey) {
    final var keys = signMetadataKeys(
      metadataKey,
      creatorKey
    );
    return signMetadata(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction signMetadata(final AccountMeta invokedTokenMetadataProgramMeta,
                                         final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, SIGN_METADATA_DISCRIMINATOR);
  }

  public static final Discriminator DEPRECATED_MINT_PRINTING_TOKENS_VIA_TOKEN_DISCRIMINATOR = toDiscriminator(84, 34, 152, 133, 145, 48, 4, 223);

  /// @param destinationKey Destination account
  /// @param tokenKey Token account containing one time authorization token
  /// @param oneTimePrintingAuthorizationMintKey One time authorization mint
  /// @param printingMintKey Printing mint
  /// @param burnAuthorityKey Burn authority
  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param tokenProgramKey Token program
  /// @param rentKey Rent
  public static List<AccountMeta> deprecatedMintPrintingTokensViaTokenKeys(final PublicKey destinationKey,
                                                                           final PublicKey tokenKey,
                                                                           final PublicKey oneTimePrintingAuthorizationMintKey,
                                                                           final PublicKey printingMintKey,
                                                                           final PublicKey burnAuthorityKey,
                                                                           final PublicKey metadataKey,
                                                                           final PublicKey masterEditionKey,
                                                                           final PublicKey tokenProgramKey,
                                                                           final PublicKey rentKey) {
    return List.of(
      createWrite(destinationKey),
      createWrite(tokenKey),
      createWrite(oneTimePrintingAuthorizationMintKey),
      createWrite(printingMintKey),
      createReadOnlySigner(burnAuthorityKey),
      createRead(metadataKey),
      createRead(masterEditionKey),
      createRead(tokenProgramKey),
      createRead(rentKey)
    );
  }

  /// @param destinationKey Destination account
  /// @param tokenKey Token account containing one time authorization token
  /// @param oneTimePrintingAuthorizationMintKey One time authorization mint
  /// @param printingMintKey Printing mint
  /// @param burnAuthorityKey Burn authority
  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param tokenProgramKey Token program
  /// @param rentKey Rent
  public static Instruction deprecatedMintPrintingTokensViaToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                 final PublicKey destinationKey,
                                                                 final PublicKey tokenKey,
                                                                 final PublicKey oneTimePrintingAuthorizationMintKey,
                                                                 final PublicKey printingMintKey,
                                                                 final PublicKey burnAuthorityKey,
                                                                 final PublicKey metadataKey,
                                                                 final PublicKey masterEditionKey,
                                                                 final PublicKey tokenProgramKey,
                                                                 final PublicKey rentKey) {
    final var keys = deprecatedMintPrintingTokensViaTokenKeys(
      destinationKey,
      tokenKey,
      oneTimePrintingAuthorizationMintKey,
      printingMintKey,
      burnAuthorityKey,
      metadataKey,
      masterEditionKey,
      tokenProgramKey,
      rentKey
    );
    return deprecatedMintPrintingTokensViaToken(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction deprecatedMintPrintingTokensViaToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, DEPRECATED_MINT_PRINTING_TOKENS_VIA_TOKEN_DISCRIMINATOR);
  }

  public static final Discriminator DEPRECATED_MINT_PRINTING_TOKENS_DISCRIMINATOR = toDiscriminator(194, 107, 144, 9, 126, 143, 53, 121);

  /// @param destinationKey Destination account
  /// @param printingMintKey Printing mint
  /// @param updateAuthorityKey Update authority
  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param tokenProgramKey Token program
  /// @param rentKey Rent
  public static List<AccountMeta> deprecatedMintPrintingTokensKeys(final PublicKey destinationKey,
                                                                   final PublicKey printingMintKey,
                                                                   final PublicKey updateAuthorityKey,
                                                                   final PublicKey metadataKey,
                                                                   final PublicKey masterEditionKey,
                                                                   final PublicKey tokenProgramKey,
                                                                   final PublicKey rentKey) {
    return List.of(
      createWrite(destinationKey),
      createWrite(printingMintKey),
      createReadOnlySigner(updateAuthorityKey),
      createRead(metadataKey),
      createRead(masterEditionKey),
      createRead(tokenProgramKey),
      createRead(rentKey)
    );
  }

  /// @param destinationKey Destination account
  /// @param printingMintKey Printing mint
  /// @param updateAuthorityKey Update authority
  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param masterEditionKey Master Edition V1 key (pda of 'metadata', program id, mint id, 'edition')
  /// @param tokenProgramKey Token program
  /// @param rentKey Rent
  public static Instruction deprecatedMintPrintingTokens(final AccountMeta invokedTokenMetadataProgramMeta,
                                                         final PublicKey destinationKey,
                                                         final PublicKey printingMintKey,
                                                         final PublicKey updateAuthorityKey,
                                                         final PublicKey metadataKey,
                                                         final PublicKey masterEditionKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey rentKey) {
    final var keys = deprecatedMintPrintingTokensKeys(
      destinationKey,
      printingMintKey,
      updateAuthorityKey,
      metadataKey,
      masterEditionKey,
      tokenProgramKey,
      rentKey
    );
    return deprecatedMintPrintingTokens(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction deprecatedMintPrintingTokens(final AccountMeta invokedTokenMetadataProgramMeta,
                                                         final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, DEPRECATED_MINT_PRINTING_TOKENS_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_MASTER_EDITION_DISCRIMINATOR = toDiscriminator(179, 210, 96, 96, 57, 25, 79, 69);

  /// @param editionKey Unallocated edition V2 account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Metadata mint
  /// @param updateAuthorityKey Update authority
  /// @param mintAuthorityKey Mint authority on the metadata's mint - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param payerKey payer
  /// @param metadataKey Metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> createMasterEditionKeys(final PublicKey editionKey,
                                                          final PublicKey mintKey,
                                                          final PublicKey updateAuthorityKey,
                                                          final PublicKey mintAuthorityKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey metadataKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey systemProgramKey,
                                                          final PublicKey rentKey) {
    return List.of(
      createWrite(editionKey),
      createWrite(mintKey),
      createReadOnlySigner(updateAuthorityKey),
      createReadOnlySigner(mintAuthorityKey),
      createWritableSigner(payerKey),
      createRead(metadataKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(rentKey)
    );
  }

  /// @param editionKey Unallocated edition V2 account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Metadata mint
  /// @param updateAuthorityKey Update authority
  /// @param mintAuthorityKey Mint authority on the metadata's mint - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param payerKey payer
  /// @param metadataKey Metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction createMasterEdition(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final PublicKey editionKey,
                                                final PublicKey mintKey,
                                                final PublicKey updateAuthorityKey,
                                                final PublicKey mintAuthorityKey,
                                                final PublicKey payerKey,
                                                final PublicKey metadataKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey systemProgramKey,
                                                final PublicKey rentKey) {
    final var keys = createMasterEditionKeys(
      editionKey,
      mintKey,
      updateAuthorityKey,
      mintAuthorityKey,
      payerKey,
      metadataKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey
    );
    return createMasterEdition(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction createMasterEdition(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CREATE_MASTER_EDITION_DISCRIMINATOR);
  }

  public static final Discriminator MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_TOKEN_DISCRIMINATOR = toDiscriminator(252, 218, 191, 168, 126, 69, 125, 118);

  /// @param newMetadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param newEditionKey New Edition (pda of 'metadata', program id, mint id, 'edition')
  /// @param masterEditionKey Master Record Edition V2 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param newMintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param editionMarkPdaKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master metadata mint id, 'edition', edition_number) where edition_number is NOT the edition number you pass in args but actually edition_number = floor(edition/EDITION_MARKER_BIT_SIZE).
  /// @param newMintAuthorityKey Mint authority of new mint
  /// @param payerKey payer
  /// @param tokenAccountOwnerKey owner of token account containing master token (#8)
  /// @param tokenAccountKey token account containing token from master metadata mint
  /// @param newMetadataUpdateAuthorityKey Update authority info for new metadata
  /// @param metadataKey Master record metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> mintNewEditionFromMasterEditionViaTokenKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                              final PublicKey newMetadataKey,
                                                                              final PublicKey newEditionKey,
                                                                              final PublicKey masterEditionKey,
                                                                              final PublicKey newMintKey,
                                                                              final PublicKey editionMarkPdaKey,
                                                                              final PublicKey newMintAuthorityKey,
                                                                              final PublicKey payerKey,
                                                                              final PublicKey tokenAccountOwnerKey,
                                                                              final PublicKey tokenAccountKey,
                                                                              final PublicKey newMetadataUpdateAuthorityKey,
                                                                              final PublicKey metadataKey,
                                                                              final PublicKey tokenProgramKey,
                                                                              final PublicKey systemProgramKey,
                                                                              final PublicKey rentKey) {
    return List.of(
      createWrite(newMetadataKey),
      createWrite(newEditionKey),
      createWrite(masterEditionKey),
      createWrite(newMintKey),
      createWrite(editionMarkPdaKey),
      createReadOnlySigner(newMintAuthorityKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(tokenAccountOwnerKey),
      createRead(tokenAccountKey),
      createRead(newMetadataUpdateAuthorityKey),
      createRead(metadataKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param newMetadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param newEditionKey New Edition (pda of 'metadata', program id, mint id, 'edition')
  /// @param masterEditionKey Master Record Edition V2 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param newMintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param editionMarkPdaKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master metadata mint id, 'edition', edition_number) where edition_number is NOT the edition number you pass in args but actually edition_number = floor(edition/EDITION_MARKER_BIT_SIZE).
  /// @param newMintAuthorityKey Mint authority of new mint
  /// @param payerKey payer
  /// @param tokenAccountOwnerKey owner of token account containing master token (#8)
  /// @param tokenAccountKey token account containing token from master metadata mint
  /// @param newMetadataUpdateAuthorityKey Update authority info for new metadata
  /// @param metadataKey Master record metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction mintNewEditionFromMasterEditionViaToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                    final PublicKey newMetadataKey,
                                                                    final PublicKey newEditionKey,
                                                                    final PublicKey masterEditionKey,
                                                                    final PublicKey newMintKey,
                                                                    final PublicKey editionMarkPdaKey,
                                                                    final PublicKey newMintAuthorityKey,
                                                                    final PublicKey payerKey,
                                                                    final PublicKey tokenAccountOwnerKey,
                                                                    final PublicKey tokenAccountKey,
                                                                    final PublicKey newMetadataUpdateAuthorityKey,
                                                                    final PublicKey metadataKey,
                                                                    final PublicKey tokenProgramKey,
                                                                    final PublicKey systemProgramKey,
                                                                    final PublicKey rentKey,
                                                                    final MintNewEditionFromMasterEditionViaTokenArgs mintNewEditionFromMasterEditionViaTokenArgs) {
    final var keys = mintNewEditionFromMasterEditionViaTokenKeys(
      invokedTokenMetadataProgramMeta,
      newMetadataKey,
      newEditionKey,
      masterEditionKey,
      newMintKey,
      editionMarkPdaKey,
      newMintAuthorityKey,
      payerKey,
      tokenAccountOwnerKey,
      tokenAccountKey,
      newMetadataUpdateAuthorityKey,
      metadataKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey
    );
    return mintNewEditionFromMasterEditionViaToken(invokedTokenMetadataProgramMeta, keys, mintNewEditionFromMasterEditionViaTokenArgs);
  }

  public static Instruction mintNewEditionFromMasterEditionViaToken(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                    final List<AccountMeta> keys,
                                                                    final MintNewEditionFromMasterEditionViaTokenArgs mintNewEditionFromMasterEditionViaTokenArgs) {
    final byte[] _data = new byte[8 + mintNewEditionFromMasterEditionViaTokenArgs.l()];
    int i = MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_TOKEN_DISCRIMINATOR.write(_data, 0);
    mintNewEditionFromMasterEditionViaTokenArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record MintNewEditionFromMasterEditionViaTokenIxData(Discriminator discriminator, MintNewEditionFromMasterEditionViaTokenArgs mintNewEditionFromMasterEditionViaTokenArgs) implements SerDe {  

    public static MintNewEditionFromMasterEditionViaTokenIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_TOKEN_ARGS_OFFSET = 8;

    public static MintNewEditionFromMasterEditionViaTokenIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mintNewEditionFromMasterEditionViaTokenArgs = MintNewEditionFromMasterEditionViaTokenArgs.read(_data, i);
      return new MintNewEditionFromMasterEditionViaTokenIxData(discriminator, mintNewEditionFromMasterEditionViaTokenArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += mintNewEditionFromMasterEditionViaTokenArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CONVERT_MASTER_EDITION_V_1_TO_V_2_DISCRIMINATOR = toDiscriminator(217, 26, 108, 0, 55, 126, 167, 238);

  /// @param masterEditionKey Master Record Edition V1 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param oneTimeAuthKey One time authorization mint
  /// @param printingMintKey Printing mint
  public static List<AccountMeta> convertMasterEditionV1ToV2Keys(final PublicKey masterEditionKey,
                                                                 final PublicKey oneTimeAuthKey,
                                                                 final PublicKey printingMintKey) {
    return List.of(
      createWrite(masterEditionKey),
      createWrite(oneTimeAuthKey),
      createWrite(printingMintKey)
    );
  }

  /// @param masterEditionKey Master Record Edition V1 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param oneTimeAuthKey One time authorization mint
  /// @param printingMintKey Printing mint
  public static Instruction convertMasterEditionV1ToV2(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final PublicKey masterEditionKey,
                                                       final PublicKey oneTimeAuthKey,
                                                       final PublicKey printingMintKey) {
    final var keys = convertMasterEditionV1ToV2Keys(
      masterEditionKey,
      oneTimeAuthKey,
      printingMintKey
    );
    return convertMasterEditionV1ToV2(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction convertMasterEditionV1ToV2(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CONVERT_MASTER_EDITION_V_1_TO_V_2_DISCRIMINATOR);
  }

  public static final Discriminator MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_VAULT_PROXY_DISCRIMINATOR = toDiscriminator(66, 246, 206, 73, 249, 35, 194, 47);

  /// @param newMetadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param newEditionKey New Edition (pda of 'metadata', program id, mint id, 'edition')
  /// @param masterEditionKey Master Record Edition V2 (pda of 'metadata', program id, master metadata mint id, 'edition'
  /// @param newMintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param editionMarkPdaKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master metadata mint id, 'edition', edition_number) where edition_number is NOT the edition number you pass in args but actually edition_number = floor(edition/EDITION_MARKER_BIT_SIZE).
  /// @param newMintAuthorityKey Mint authority of new mint
  /// @param payerKey payer
  /// @param vaultAuthorityKey Vault authority
  /// @param safetyDepositStoreKey Safety deposit token store account
  /// @param safetyDepositBoxKey Safety deposit box
  /// @param vaultKey Vault
  /// @param newMetadataUpdateAuthorityKey Update authority info for new metadata
  /// @param metadataKey Master record metadata account
  /// @param tokenProgramKey Token program
  /// @param tokenVaultProgramKey Token vault program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> mintNewEditionFromMasterEditionViaVaultProxyKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                                   final PublicKey newMetadataKey,
                                                                                   final PublicKey newEditionKey,
                                                                                   final PublicKey masterEditionKey,
                                                                                   final PublicKey newMintKey,
                                                                                   final PublicKey editionMarkPdaKey,
                                                                                   final PublicKey newMintAuthorityKey,
                                                                                   final PublicKey payerKey,
                                                                                   final PublicKey vaultAuthorityKey,
                                                                                   final PublicKey safetyDepositStoreKey,
                                                                                   final PublicKey safetyDepositBoxKey,
                                                                                   final PublicKey vaultKey,
                                                                                   final PublicKey newMetadataUpdateAuthorityKey,
                                                                                   final PublicKey metadataKey,
                                                                                   final PublicKey tokenProgramKey,
                                                                                   final PublicKey tokenVaultProgramKey,
                                                                                   final PublicKey systemProgramKey,
                                                                                   final PublicKey rentKey) {
    return List.of(
      createWrite(newMetadataKey),
      createWrite(newEditionKey),
      createWrite(masterEditionKey),
      createWrite(newMintKey),
      createWrite(editionMarkPdaKey),
      createReadOnlySigner(newMintAuthorityKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(vaultAuthorityKey),
      createRead(safetyDepositStoreKey),
      createRead(safetyDepositBoxKey),
      createRead(vaultKey),
      createRead(newMetadataUpdateAuthorityKey),
      createRead(metadataKey),
      createRead(tokenProgramKey),
      createRead(tokenVaultProgramKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param newMetadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param newEditionKey New Edition (pda of 'metadata', program id, mint id, 'edition')
  /// @param masterEditionKey Master Record Edition V2 (pda of 'metadata', program id, master metadata mint id, 'edition'
  /// @param newMintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param editionMarkPdaKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master metadata mint id, 'edition', edition_number) where edition_number is NOT the edition number you pass in args but actually edition_number = floor(edition/EDITION_MARKER_BIT_SIZE).
  /// @param newMintAuthorityKey Mint authority of new mint
  /// @param payerKey payer
  /// @param vaultAuthorityKey Vault authority
  /// @param safetyDepositStoreKey Safety deposit token store account
  /// @param safetyDepositBoxKey Safety deposit box
  /// @param vaultKey Vault
  /// @param newMetadataUpdateAuthorityKey Update authority info for new metadata
  /// @param metadataKey Master record metadata account
  /// @param tokenProgramKey Token program
  /// @param tokenVaultProgramKey Token vault program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction mintNewEditionFromMasterEditionViaVaultProxy(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                         final PublicKey newMetadataKey,
                                                                         final PublicKey newEditionKey,
                                                                         final PublicKey masterEditionKey,
                                                                         final PublicKey newMintKey,
                                                                         final PublicKey editionMarkPdaKey,
                                                                         final PublicKey newMintAuthorityKey,
                                                                         final PublicKey payerKey,
                                                                         final PublicKey vaultAuthorityKey,
                                                                         final PublicKey safetyDepositStoreKey,
                                                                         final PublicKey safetyDepositBoxKey,
                                                                         final PublicKey vaultKey,
                                                                         final PublicKey newMetadataUpdateAuthorityKey,
                                                                         final PublicKey metadataKey,
                                                                         final PublicKey tokenProgramKey,
                                                                         final PublicKey tokenVaultProgramKey,
                                                                         final PublicKey systemProgramKey,
                                                                         final PublicKey rentKey,
                                                                         final MintNewEditionFromMasterEditionViaTokenArgs mintNewEditionFromMasterEditionViaTokenArgs) {
    final var keys = mintNewEditionFromMasterEditionViaVaultProxyKeys(
      invokedTokenMetadataProgramMeta,
      newMetadataKey,
      newEditionKey,
      masterEditionKey,
      newMintKey,
      editionMarkPdaKey,
      newMintAuthorityKey,
      payerKey,
      vaultAuthorityKey,
      safetyDepositStoreKey,
      safetyDepositBoxKey,
      vaultKey,
      newMetadataUpdateAuthorityKey,
      metadataKey,
      tokenProgramKey,
      tokenVaultProgramKey,
      systemProgramKey,
      rentKey
    );
    return mintNewEditionFromMasterEditionViaVaultProxy(invokedTokenMetadataProgramMeta, keys, mintNewEditionFromMasterEditionViaTokenArgs);
  }

  public static Instruction mintNewEditionFromMasterEditionViaVaultProxy(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                         final List<AccountMeta> keys,
                                                                         final MintNewEditionFromMasterEditionViaTokenArgs mintNewEditionFromMasterEditionViaTokenArgs) {
    final byte[] _data = new byte[8 + mintNewEditionFromMasterEditionViaTokenArgs.l()];
    int i = MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_VAULT_PROXY_DISCRIMINATOR.write(_data, 0);
    mintNewEditionFromMasterEditionViaTokenArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record MintNewEditionFromMasterEditionViaVaultProxyIxData(Discriminator discriminator, MintNewEditionFromMasterEditionViaTokenArgs mintNewEditionFromMasterEditionViaTokenArgs) implements SerDe {  

    public static MintNewEditionFromMasterEditionViaVaultProxyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_TOKEN_ARGS_OFFSET = 8;

    public static MintNewEditionFromMasterEditionViaVaultProxyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mintNewEditionFromMasterEditionViaTokenArgs = MintNewEditionFromMasterEditionViaTokenArgs.read(_data, i);
      return new MintNewEditionFromMasterEditionViaVaultProxyIxData(discriminator, mintNewEditionFromMasterEditionViaTokenArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += mintNewEditionFromMasterEditionViaTokenArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PUFF_METADATA_DISCRIMINATOR = toDiscriminator(87, 217, 21, 132, 105, 238, 71, 114);

  /// @param metadataKey Metadata account
  public static List<AccountMeta> puffMetadataKeys(final PublicKey metadataKey) {
    return List.of(
      createWrite(metadataKey)
    );
  }

  /// @param metadataKey Metadata account
  public static Instruction puffMetadata(final AccountMeta invokedTokenMetadataProgramMeta,
                                         final PublicKey metadataKey) {
    final var keys = puffMetadataKeys(
      metadataKey
    );
    return puffMetadata(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction puffMetadata(final AccountMeta invokedTokenMetadataProgramMeta,
                                         final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, PUFF_METADATA_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR = toDiscriminator(202, 132, 152, 229, 216, 217, 137, 212);

  /// @param metadataKey Metadata account
  /// @param updateAuthorityKey Update authority key
  public static List<AccountMeta> updateMetadataAccountV2Keys(final PublicKey metadataKey,
                                                              final PublicKey updateAuthorityKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// @param metadataKey Metadata account
  /// @param updateAuthorityKey Update authority key
  public static Instruction updateMetadataAccountV2(final AccountMeta invokedTokenMetadataProgramMeta,
                                                    final PublicKey metadataKey,
                                                    final PublicKey updateAuthorityKey,
                                                    final UpdateMetadataAccountArgsV2 updateMetadataAccountArgsV2) {
    final var keys = updateMetadataAccountV2Keys(
      metadataKey,
      updateAuthorityKey
    );
    return updateMetadataAccountV2(invokedTokenMetadataProgramMeta, keys, updateMetadataAccountArgsV2);
  }

  public static Instruction updateMetadataAccountV2(final AccountMeta invokedTokenMetadataProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final UpdateMetadataAccountArgsV2 updateMetadataAccountArgsV2) {
    final byte[] _data = new byte[8 + updateMetadataAccountArgsV2.l()];
    int i = UPDATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR.write(_data, 0);
    updateMetadataAccountArgsV2.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record UpdateMetadataAccountV2IxData(Discriminator discriminator, UpdateMetadataAccountArgsV2 updateMetadataAccountArgsV2) implements SerDe {  

    public static UpdateMetadataAccountV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int UPDATE_METADATA_ACCOUNT_ARGS_V_2_OFFSET = 8;

    public static UpdateMetadataAccountV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var updateMetadataAccountArgsV2 = UpdateMetadataAccountArgsV2.read(_data, i);
      return new UpdateMetadataAccountV2IxData(discriminator, updateMetadataAccountArgsV2);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += updateMetadataAccountArgsV2.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + updateMetadataAccountArgsV2.l();
    }
  }

  public static final Discriminator CREATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR = toDiscriminator(24, 73, 41, 237, 44, 142, 194, 254);

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param mintKey Mint of token asset
  /// @param mintAuthorityKey Mint authority
  /// @param payerKey payer
  /// @param updateAuthorityKey update authority info
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> createMetadataAccountV2Keys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                              final PublicKey metadataKey,
                                                              final PublicKey mintKey,
                                                              final PublicKey mintAuthorityKey,
                                                              final PublicKey payerKey,
                                                              final PublicKey updateAuthorityKey,
                                                              final PublicKey systemProgramKey,
                                                              final PublicKey rentKey) {
    return List.of(
      createWrite(metadataKey),
      createRead(mintKey),
      createReadOnlySigner(mintAuthorityKey),
      createWritableSigner(payerKey),
      createRead(updateAuthorityKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param mintKey Mint of token asset
  /// @param mintAuthorityKey Mint authority
  /// @param payerKey payer
  /// @param updateAuthorityKey update authority info
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction createMetadataAccountV2(final AccountMeta invokedTokenMetadataProgramMeta,
                                                    final PublicKey metadataKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey mintAuthorityKey,
                                                    final PublicKey payerKey,
                                                    final PublicKey updateAuthorityKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey rentKey) {
    final var keys = createMetadataAccountV2Keys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      mintKey,
      mintAuthorityKey,
      payerKey,
      updateAuthorityKey,
      systemProgramKey,
      rentKey
    );
    return createMetadataAccountV2(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction createMetadataAccountV2(final AccountMeta invokedTokenMetadataProgramMeta,
                                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CREATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_MASTER_EDITION_V_3_DISCRIMINATOR = toDiscriminator(147, 149, 17, 159, 74, 134, 114, 237);

  /// @param editionKey Unallocated edition V2 account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Metadata mint
  /// @param updateAuthorityKey Update authority
  /// @param mintAuthorityKey Mint authority on the metadata's mint - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param payerKey payer
  /// @param metadataKey Metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> createMasterEditionV3Keys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                            final PublicKey editionKey,
                                                            final PublicKey mintKey,
                                                            final PublicKey updateAuthorityKey,
                                                            final PublicKey mintAuthorityKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey metadataKey,
                                                            final PublicKey tokenProgramKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey rentKey) {
    return List.of(
      createWrite(editionKey),
      createWrite(mintKey),
      createReadOnlySigner(updateAuthorityKey),
      createReadOnlySigner(mintAuthorityKey),
      createWritableSigner(payerKey),
      createWrite(metadataKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param editionKey Unallocated edition V2 account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Metadata mint
  /// @param updateAuthorityKey Update authority
  /// @param mintAuthorityKey Mint authority on the metadata's mint - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param payerKey payer
  /// @param metadataKey Metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction createMasterEditionV3(final AccountMeta invokedTokenMetadataProgramMeta,
                                                  final PublicKey editionKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey updateAuthorityKey,
                                                  final PublicKey mintAuthorityKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey metadataKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey rentKey,
                                                  final CreateMasterEditionArgs createMasterEditionArgs) {
    final var keys = createMasterEditionV3Keys(
      invokedTokenMetadataProgramMeta,
      editionKey,
      mintKey,
      updateAuthorityKey,
      mintAuthorityKey,
      payerKey,
      metadataKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey
    );
    return createMasterEditionV3(invokedTokenMetadataProgramMeta, keys, createMasterEditionArgs);
  }

  public static Instruction createMasterEditionV3(final AccountMeta invokedTokenMetadataProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final CreateMasterEditionArgs createMasterEditionArgs) {
    final byte[] _data = new byte[8 + createMasterEditionArgs.l()];
    int i = CREATE_MASTER_EDITION_V_3_DISCRIMINATOR.write(_data, 0);
    createMasterEditionArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record CreateMasterEditionV3IxData(Discriminator discriminator, CreateMasterEditionArgs createMasterEditionArgs) implements SerDe {  

    public static CreateMasterEditionV3IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int CREATE_MASTER_EDITION_ARGS_OFFSET = 8;

    public static CreateMasterEditionV3IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var createMasterEditionArgs = CreateMasterEditionArgs.read(_data, i);
      return new CreateMasterEditionV3IxData(discriminator, createMasterEditionArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += createMasterEditionArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + createMasterEditionArgs.l();
    }
  }

  public static final Discriminator VERIFY_COLLECTION_DISCRIMINATOR = toDiscriminator(56, 113, 101, 253, 79, 55, 122, 169);

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey payer
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> verifyCollectionKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final PublicKey metadataKey,
                                                       final PublicKey collectionAuthorityKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey collectionMintKey,
                                                       final PublicKey collectionKey,
                                                       final PublicKey collectionMasterEditionAccountKey,
                                                       final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(metadataKey),
      createWritableSigner(collectionAuthorityKey),
      createWritableSigner(payerKey),
      createRead(collectionMintKey),
      createRead(collectionKey),
      createRead(collectionMasterEditionAccountKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey payer
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction verifyCollection(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey metadataKey,
                                             final PublicKey collectionAuthorityKey,
                                             final PublicKey payerKey,
                                             final PublicKey collectionMintKey,
                                             final PublicKey collectionKey,
                                             final PublicKey collectionMasterEditionAccountKey,
                                             final PublicKey collectionAuthorityRecordKey) {
    final var keys = verifyCollectionKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      collectionAuthorityKey,
      payerKey,
      collectionMintKey,
      collectionKey,
      collectionMasterEditionAccountKey,
      collectionAuthorityRecordKey
    );
    return verifyCollection(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction verifyCollection(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, VERIFY_COLLECTION_DISCRIMINATOR);
  }

  public static final Discriminator UTILIZE_DISCRIMINATOR = toDiscriminator(104, 146, 242, 209, 176, 174, 185, 163);

  /// @param metadataKey Metadata account
  /// @param tokenAccountKey Token Account Of NFT
  /// @param mintKey Mint of the Metadata
  /// @param useAuthorityKey A Use Authority / Can be the current Owner of the NFT
  /// @param ownerKey Owner
  /// @param tokenProgramKey Token program
  /// @param ataProgramKey Associated Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  /// @param useAuthorityRecordKey Use Authority Record PDA If present the program Assumes a delegated use authority
  /// @param burnerKey Program As Signer (Burner)
  public static List<AccountMeta> utilizeKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                              final PublicKey metadataKey,
                                              final PublicKey tokenAccountKey,
                                              final PublicKey mintKey,
                                              final PublicKey useAuthorityKey,
                                              final PublicKey ownerKey,
                                              final PublicKey tokenProgramKey,
                                              final PublicKey ataProgramKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey rentKey,
                                              final PublicKey useAuthorityRecordKey,
                                              final PublicKey burnerKey) {
    return List.of(
      createWrite(metadataKey),
      createWrite(tokenAccountKey),
      createWrite(mintKey),
      createWritableSigner(useAuthorityKey),
      createRead(ownerKey),
      createRead(tokenProgramKey),
      createRead(ataProgramKey),
      createRead(systemProgramKey),
      createRead(rentKey),
      createWrite(requireNonNullElse(useAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(burnerKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param tokenAccountKey Token Account Of NFT
  /// @param mintKey Mint of the Metadata
  /// @param useAuthorityKey A Use Authority / Can be the current Owner of the NFT
  /// @param ownerKey Owner
  /// @param tokenProgramKey Token program
  /// @param ataProgramKey Associated Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  /// @param useAuthorityRecordKey Use Authority Record PDA If present the program Assumes a delegated use authority
  /// @param burnerKey Program As Signer (Burner)
  public static Instruction utilize(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final PublicKey metadataKey,
                                    final PublicKey tokenAccountKey,
                                    final PublicKey mintKey,
                                    final PublicKey useAuthorityKey,
                                    final PublicKey ownerKey,
                                    final PublicKey tokenProgramKey,
                                    final PublicKey ataProgramKey,
                                    final PublicKey systemProgramKey,
                                    final PublicKey rentKey,
                                    final PublicKey useAuthorityRecordKey,
                                    final PublicKey burnerKey,
                                    final UtilizeArgs utilizeArgs) {
    final var keys = utilizeKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      tokenAccountKey,
      mintKey,
      useAuthorityKey,
      ownerKey,
      tokenProgramKey,
      ataProgramKey,
      systemProgramKey,
      rentKey,
      useAuthorityRecordKey,
      burnerKey
    );
    return utilize(invokedTokenMetadataProgramMeta, keys, utilizeArgs);
  }

  public static Instruction utilize(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final List<AccountMeta> keys,
                                    final UtilizeArgs utilizeArgs) {
    final byte[] _data = new byte[8 + utilizeArgs.l()];
    int i = UTILIZE_DISCRIMINATOR.write(_data, 0);
    utilizeArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record UtilizeIxData(Discriminator discriminator, UtilizeArgs utilizeArgs) implements SerDe {  

    public static UtilizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int UTILIZE_ARGS_OFFSET = 8;

    public static UtilizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var utilizeArgs = UtilizeArgs.read(_data, i);
      return new UtilizeIxData(discriminator, utilizeArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += utilizeArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator APPROVE_USE_AUTHORITY_DISCRIMINATOR = toDiscriminator(14, 4, 77, 134, 86, 23, 37, 236);

  /// @param useAuthorityRecordKey Use Authority Record PDA
  /// @param ownerKey Owner
  /// @param payerKey Payer
  /// @param userKey A Use Authority
  /// @param ownerTokenAccountKey Owned Token Account Of Mint
  /// @param metadataKey Metadata account
  /// @param mintKey Mint of Metadata
  /// @param burnerKey Program As Signer (Burner)
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> approveUseAuthorityKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                          final PublicKey useAuthorityRecordKey,
                                                          final PublicKey ownerKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey userKey,
                                                          final PublicKey ownerTokenAccountKey,
                                                          final PublicKey metadataKey,
                                                          final PublicKey mintKey,
                                                          final PublicKey burnerKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey systemProgramKey,
                                                          final PublicKey rentKey) {
    return List.of(
      createWrite(useAuthorityRecordKey),
      createWritableSigner(ownerKey),
      createWritableSigner(payerKey),
      createRead(userKey),
      createWrite(ownerTokenAccountKey),
      createRead(metadataKey),
      createRead(mintKey),
      createRead(burnerKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param useAuthorityRecordKey Use Authority Record PDA
  /// @param ownerKey Owner
  /// @param payerKey Payer
  /// @param userKey A Use Authority
  /// @param ownerTokenAccountKey Owned Token Account Of Mint
  /// @param metadataKey Metadata account
  /// @param mintKey Mint of Metadata
  /// @param burnerKey Program As Signer (Burner)
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction approveUseAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final PublicKey useAuthorityRecordKey,
                                                final PublicKey ownerKey,
                                                final PublicKey payerKey,
                                                final PublicKey userKey,
                                                final PublicKey ownerTokenAccountKey,
                                                final PublicKey metadataKey,
                                                final PublicKey mintKey,
                                                final PublicKey burnerKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey systemProgramKey,
                                                final PublicKey rentKey,
                                                final ApproveUseAuthorityArgs approveUseAuthorityArgs) {
    final var keys = approveUseAuthorityKeys(
      invokedTokenMetadataProgramMeta,
      useAuthorityRecordKey,
      ownerKey,
      payerKey,
      userKey,
      ownerTokenAccountKey,
      metadataKey,
      mintKey,
      burnerKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey
    );
    return approveUseAuthority(invokedTokenMetadataProgramMeta, keys, approveUseAuthorityArgs);
  }

  public static Instruction approveUseAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final List<AccountMeta> keys,
                                                final ApproveUseAuthorityArgs approveUseAuthorityArgs) {
    final byte[] _data = new byte[8 + approveUseAuthorityArgs.l()];
    int i = APPROVE_USE_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    approveUseAuthorityArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record ApproveUseAuthorityIxData(Discriminator discriminator, ApproveUseAuthorityArgs approveUseAuthorityArgs) implements SerDe {  

    public static ApproveUseAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int APPROVE_USE_AUTHORITY_ARGS_OFFSET = 8;

    public static ApproveUseAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var approveUseAuthorityArgs = ApproveUseAuthorityArgs.read(_data, i);
      return new ApproveUseAuthorityIxData(discriminator, approveUseAuthorityArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += approveUseAuthorityArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REVOKE_USE_AUTHORITY_DISCRIMINATOR = toDiscriminator(204, 194, 208, 141, 142, 221, 109, 84);

  /// @param useAuthorityRecordKey Use Authority Record PDA
  /// @param ownerKey Owner
  /// @param userKey A Use Authority
  /// @param ownerTokenAccountKey Owned Token Account Of Mint
  /// @param mintKey Mint of Metadata
  /// @param metadataKey Metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> revokeUseAuthorityKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                         final PublicKey useAuthorityRecordKey,
                                                         final PublicKey ownerKey,
                                                         final PublicKey userKey,
                                                         final PublicKey ownerTokenAccountKey,
                                                         final PublicKey mintKey,
                                                         final PublicKey metadataKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey systemProgramKey,
                                                         final PublicKey rentKey) {
    return List.of(
      createWrite(useAuthorityRecordKey),
      createWritableSigner(ownerKey),
      createRead(userKey),
      createWrite(ownerTokenAccountKey),
      createRead(mintKey),
      createRead(metadataKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param useAuthorityRecordKey Use Authority Record PDA
  /// @param ownerKey Owner
  /// @param userKey A Use Authority
  /// @param ownerTokenAccountKey Owned Token Account Of Mint
  /// @param mintKey Mint of Metadata
  /// @param metadataKey Metadata account
  /// @param tokenProgramKey Token program
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction revokeUseAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final PublicKey useAuthorityRecordKey,
                                               final PublicKey ownerKey,
                                               final PublicKey userKey,
                                               final PublicKey ownerTokenAccountKey,
                                               final PublicKey mintKey,
                                               final PublicKey metadataKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey rentKey) {
    final var keys = revokeUseAuthorityKeys(
      invokedTokenMetadataProgramMeta,
      useAuthorityRecordKey,
      ownerKey,
      userKey,
      ownerTokenAccountKey,
      mintKey,
      metadataKey,
      tokenProgramKey,
      systemProgramKey,
      rentKey
    );
    return revokeUseAuthority(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction revokeUseAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, REVOKE_USE_AUTHORITY_DISCRIMINATOR);
  }

  public static final Discriminator UNVERIFY_COLLECTION_DISCRIMINATOR = toDiscriminator(250, 251, 42, 106, 41, 137, 186, 168);

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Authority
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> unverifyCollectionKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                         final PublicKey metadataKey,
                                                         final PublicKey collectionAuthorityKey,
                                                         final PublicKey collectionMintKey,
                                                         final PublicKey collectionKey,
                                                         final PublicKey collectionMasterEditionAccountKey,
                                                         final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(metadataKey),
      createWritableSigner(collectionAuthorityKey),
      createRead(collectionMintKey),
      createRead(collectionKey),
      createRead(collectionMasterEditionAccountKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Authority
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction unverifyCollection(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final PublicKey metadataKey,
                                               final PublicKey collectionAuthorityKey,
                                               final PublicKey collectionMintKey,
                                               final PublicKey collectionKey,
                                               final PublicKey collectionMasterEditionAccountKey,
                                               final PublicKey collectionAuthorityRecordKey) {
    final var keys = unverifyCollectionKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      collectionAuthorityKey,
      collectionMintKey,
      collectionKey,
      collectionMasterEditionAccountKey,
      collectionAuthorityRecordKey
    );
    return unverifyCollection(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction unverifyCollection(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, UNVERIFY_COLLECTION_DISCRIMINATOR);
  }

  public static final Discriminator APPROVE_COLLECTION_AUTHORITY_DISCRIMINATOR = toDiscriminator(254, 136, 208, 39, 65, 66, 27, 111);

  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  /// @param newCollectionAuthorityKey A Collection Authority
  /// @param updateAuthorityKey Update Authority of Collection NFT
  /// @param payerKey Payer
  /// @param metadataKey Collection Metadata account
  /// @param mintKey Mint of Collection Metadata
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> approveCollectionAuthorityKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                 final PublicKey collectionAuthorityRecordKey,
                                                                 final PublicKey newCollectionAuthorityKey,
                                                                 final PublicKey updateAuthorityKey,
                                                                 final PublicKey payerKey,
                                                                 final PublicKey metadataKey,
                                                                 final PublicKey mintKey,
                                                                 final PublicKey systemProgramKey,
                                                                 final PublicKey rentKey) {
    return List.of(
      createWrite(collectionAuthorityRecordKey),
      createRead(newCollectionAuthorityKey),
      createWritableSigner(updateAuthorityKey),
      createWritableSigner(payerKey),
      createRead(metadataKey),
      createRead(mintKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  /// @param newCollectionAuthorityKey A Collection Authority
  /// @param updateAuthorityKey Update Authority of Collection NFT
  /// @param payerKey Payer
  /// @param metadataKey Collection Metadata account
  /// @param mintKey Mint of Collection Metadata
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction approveCollectionAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final PublicKey collectionAuthorityRecordKey,
                                                       final PublicKey newCollectionAuthorityKey,
                                                       final PublicKey updateAuthorityKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey metadataKey,
                                                       final PublicKey mintKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey rentKey) {
    final var keys = approveCollectionAuthorityKeys(
      invokedTokenMetadataProgramMeta,
      collectionAuthorityRecordKey,
      newCollectionAuthorityKey,
      updateAuthorityKey,
      payerKey,
      metadataKey,
      mintKey,
      systemProgramKey,
      rentKey
    );
    return approveCollectionAuthority(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction approveCollectionAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, APPROVE_COLLECTION_AUTHORITY_DISCRIMINATOR);
  }

  public static final Discriminator REVOKE_COLLECTION_AUTHORITY_DISCRIMINATOR = toDiscriminator(31, 139, 135, 198, 29, 48, 160, 154);

  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  /// @param delegateAuthorityKey Delegated Collection Authority
  /// @param revokeAuthorityKey Update Authority, or Delegated Authority, of Collection NFT
  /// @param metadataKey Metadata account
  /// @param mintKey Mint of Metadata
  public static List<AccountMeta> revokeCollectionAuthorityKeys(final PublicKey collectionAuthorityRecordKey,
                                                                final PublicKey delegateAuthorityKey,
                                                                final PublicKey revokeAuthorityKey,
                                                                final PublicKey metadataKey,
                                                                final PublicKey mintKey) {
    return List.of(
      createWrite(collectionAuthorityRecordKey),
      createWrite(delegateAuthorityKey),
      createWritableSigner(revokeAuthorityKey),
      createRead(metadataKey),
      createRead(mintKey)
    );
  }

  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  /// @param delegateAuthorityKey Delegated Collection Authority
  /// @param revokeAuthorityKey Update Authority, or Delegated Authority, of Collection NFT
  /// @param metadataKey Metadata account
  /// @param mintKey Mint of Metadata
  public static Instruction revokeCollectionAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                                      final PublicKey collectionAuthorityRecordKey,
                                                      final PublicKey delegateAuthorityKey,
                                                      final PublicKey revokeAuthorityKey,
                                                      final PublicKey metadataKey,
                                                      final PublicKey mintKey) {
    final var keys = revokeCollectionAuthorityKeys(
      collectionAuthorityRecordKey,
      delegateAuthorityKey,
      revokeAuthorityKey,
      metadataKey,
      mintKey
    );
    return revokeCollectionAuthority(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction revokeCollectionAuthority(final AccountMeta invokedTokenMetadataProgramMeta,
                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, REVOKE_COLLECTION_AUTHORITY_DISCRIMINATOR);
  }

  public static final Discriminator SET_AND_VERIFY_COLLECTION_DISCRIMINATOR = toDiscriminator(235, 242, 121, 216, 158, 234, 180, 234);

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey Payer
  /// @param updateAuthorityKey Update Authority of Collection NFT and NFT
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> setAndVerifyCollectionKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                             final PublicKey metadataKey,
                                                             final PublicKey collectionAuthorityKey,
                                                             final PublicKey payerKey,
                                                             final PublicKey updateAuthorityKey,
                                                             final PublicKey collectionMintKey,
                                                             final PublicKey collectionKey,
                                                             final PublicKey collectionMasterEditionAccountKey,
                                                             final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(metadataKey),
      createWritableSigner(collectionAuthorityKey),
      createWritableSigner(payerKey),
      createRead(updateAuthorityKey),
      createRead(collectionMintKey),
      createRead(collectionKey),
      createRead(collectionMasterEditionAccountKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey Payer
  /// @param updateAuthorityKey Update Authority of Collection NFT and NFT
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction setAndVerifyCollection(final AccountMeta invokedTokenMetadataProgramMeta,
                                                   final PublicKey metadataKey,
                                                   final PublicKey collectionAuthorityKey,
                                                   final PublicKey payerKey,
                                                   final PublicKey updateAuthorityKey,
                                                   final PublicKey collectionMintKey,
                                                   final PublicKey collectionKey,
                                                   final PublicKey collectionMasterEditionAccountKey,
                                                   final PublicKey collectionAuthorityRecordKey) {
    final var keys = setAndVerifyCollectionKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      collectionAuthorityKey,
      payerKey,
      updateAuthorityKey,
      collectionMintKey,
      collectionKey,
      collectionMasterEditionAccountKey,
      collectionAuthorityRecordKey
    );
    return setAndVerifyCollection(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction setAndVerifyCollection(final AccountMeta invokedTokenMetadataProgramMeta,
                                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, SET_AND_VERIFY_COLLECTION_DISCRIMINATOR);
  }

  public static final Discriminator FREEZE_DELEGATED_ACCOUNT_DISCRIMINATOR = toDiscriminator(14, 16, 189, 180, 116, 19, 96, 127);

  /// @param delegateKey Delegate
  /// @param tokenAccountKey Token account to freeze
  /// @param editionKey Edition
  /// @param mintKey Token mint
  /// @param tokenProgramKey Token Program
  public static List<AccountMeta> freezeDelegatedAccountKeys(final PublicKey delegateKey,
                                                             final PublicKey tokenAccountKey,
                                                             final PublicKey editionKey,
                                                             final PublicKey mintKey,
                                                             final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(delegateKey),
      createWrite(tokenAccountKey),
      createRead(editionKey),
      createRead(mintKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param delegateKey Delegate
  /// @param tokenAccountKey Token account to freeze
  /// @param editionKey Edition
  /// @param mintKey Token mint
  /// @param tokenProgramKey Token Program
  public static Instruction freezeDelegatedAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                   final PublicKey delegateKey,
                                                   final PublicKey tokenAccountKey,
                                                   final PublicKey editionKey,
                                                   final PublicKey mintKey,
                                                   final PublicKey tokenProgramKey) {
    final var keys = freezeDelegatedAccountKeys(
      delegateKey,
      tokenAccountKey,
      editionKey,
      mintKey,
      tokenProgramKey
    );
    return freezeDelegatedAccount(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction freezeDelegatedAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, FREEZE_DELEGATED_ACCOUNT_DISCRIMINATOR);
  }

  public static final Discriminator THAW_DELEGATED_ACCOUNT_DISCRIMINATOR = toDiscriminator(239, 152, 227, 34, 225, 200, 206, 170);

  /// @param delegateKey Delegate
  /// @param tokenAccountKey Token account to thaw
  /// @param editionKey Edition
  /// @param mintKey Token mint
  /// @param tokenProgramKey Token Program
  public static List<AccountMeta> thawDelegatedAccountKeys(final PublicKey delegateKey,
                                                           final PublicKey tokenAccountKey,
                                                           final PublicKey editionKey,
                                                           final PublicKey mintKey,
                                                           final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(delegateKey),
      createWrite(tokenAccountKey),
      createRead(editionKey),
      createRead(mintKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param delegateKey Delegate
  /// @param tokenAccountKey Token account to thaw
  /// @param editionKey Edition
  /// @param mintKey Token mint
  /// @param tokenProgramKey Token Program
  public static Instruction thawDelegatedAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                 final PublicKey delegateKey,
                                                 final PublicKey tokenAccountKey,
                                                 final PublicKey editionKey,
                                                 final PublicKey mintKey,
                                                 final PublicKey tokenProgramKey) {
    final var keys = thawDelegatedAccountKeys(
      delegateKey,
      tokenAccountKey,
      editionKey,
      mintKey,
      tokenProgramKey
    );
    return thawDelegatedAccount(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction thawDelegatedAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, THAW_DELEGATED_ACCOUNT_DISCRIMINATOR);
  }

  public static final Discriminator REMOVE_CREATOR_VERIFICATION_DISCRIMINATOR = toDiscriminator(41, 194, 140, 217, 90, 160, 139, 6);

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param creatorKey Creator
  public static List<AccountMeta> removeCreatorVerificationKeys(final PublicKey metadataKey,
                                                                final PublicKey creatorKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(creatorKey)
    );
  }

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param creatorKey Creator
  public static Instruction removeCreatorVerification(final AccountMeta invokedTokenMetadataProgramMeta,
                                                      final PublicKey metadataKey,
                                                      final PublicKey creatorKey) {
    final var keys = removeCreatorVerificationKeys(
      metadataKey,
      creatorKey
    );
    return removeCreatorVerification(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction removeCreatorVerification(final AccountMeta invokedTokenMetadataProgramMeta,
                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, REMOVE_CREATOR_VERIFICATION_DISCRIMINATOR);
  }

  public static final Discriminator BURN_NFT_DISCRIMINATOR = toDiscriminator(119, 13, 183, 17, 194, 243, 38, 31);

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param ownerKey NFT owner
  /// @param mintKey Mint of the NFT
  /// @param tokenAccountKey Token account to close
  /// @param masterEditionAccountKey MasterEdition2 of the NFT
  /// @param splTokenProgramKey SPL Token Program
  /// @param collectionMetadataKey Metadata of the Collection
  public static List<AccountMeta> burnNftKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                              final PublicKey metadataKey,
                                              final PublicKey ownerKey,
                                              final PublicKey mintKey,
                                              final PublicKey tokenAccountKey,
                                              final PublicKey masterEditionAccountKey,
                                              final PublicKey splTokenProgramKey,
                                              final PublicKey collectionMetadataKey) {
    return List.of(
      createWrite(metadataKey),
      createWritableSigner(ownerKey),
      createWrite(mintKey),
      createWrite(tokenAccountKey),
      createWrite(masterEditionAccountKey),
      createRead(splTokenProgramKey),
      createWrite(requireNonNullElse(collectionMetadataKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param ownerKey NFT owner
  /// @param mintKey Mint of the NFT
  /// @param tokenAccountKey Token account to close
  /// @param masterEditionAccountKey MasterEdition2 of the NFT
  /// @param splTokenProgramKey SPL Token Program
  /// @param collectionMetadataKey Metadata of the Collection
  public static Instruction burnNft(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final PublicKey metadataKey,
                                    final PublicKey ownerKey,
                                    final PublicKey mintKey,
                                    final PublicKey tokenAccountKey,
                                    final PublicKey masterEditionAccountKey,
                                    final PublicKey splTokenProgramKey,
                                    final PublicKey collectionMetadataKey) {
    final var keys = burnNftKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      ownerKey,
      mintKey,
      tokenAccountKey,
      masterEditionAccountKey,
      splTokenProgramKey,
      collectionMetadataKey
    );
    return burnNft(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction burnNft(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, BURN_NFT_DISCRIMINATOR);
  }

  public static final Discriminator VERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR = toDiscriminator(86, 111, 223, 68, 17, 99, 180, 147);

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey payer
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> verifySizedCollectionItemKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                final PublicKey metadataKey,
                                                                final PublicKey collectionAuthorityKey,
                                                                final PublicKey payerKey,
                                                                final PublicKey collectionMintKey,
                                                                final PublicKey collectionKey,
                                                                final PublicKey collectionMasterEditionAccountKey,
                                                                final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(collectionAuthorityKey),
      createWritableSigner(payerKey),
      createRead(collectionMintKey),
      createWrite(collectionKey),
      createRead(collectionMasterEditionAccountKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey payer
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction verifySizedCollectionItem(final AccountMeta invokedTokenMetadataProgramMeta,
                                                      final PublicKey metadataKey,
                                                      final PublicKey collectionAuthorityKey,
                                                      final PublicKey payerKey,
                                                      final PublicKey collectionMintKey,
                                                      final PublicKey collectionKey,
                                                      final PublicKey collectionMasterEditionAccountKey,
                                                      final PublicKey collectionAuthorityRecordKey) {
    final var keys = verifySizedCollectionItemKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      collectionAuthorityKey,
      payerKey,
      collectionMintKey,
      collectionKey,
      collectionMasterEditionAccountKey,
      collectionAuthorityRecordKey
    );
    return verifySizedCollectionItem(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction verifySizedCollectionItem(final AccountMeta invokedTokenMetadataProgramMeta,
                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, VERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR);
  }

  public static final Discriminator UNVERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR = toDiscriminator(161, 187, 194, 156, 158, 154, 144, 221);

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Authority
  /// @param payerKey payer
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> unverifySizedCollectionItemKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                  final PublicKey metadataKey,
                                                                  final PublicKey collectionAuthorityKey,
                                                                  final PublicKey payerKey,
                                                                  final PublicKey collectionMintKey,
                                                                  final PublicKey collectionKey,
                                                                  final PublicKey collectionMasterEditionAccountKey,
                                                                  final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(collectionAuthorityKey),
      createWritableSigner(payerKey),
      createRead(collectionMintKey),
      createWrite(collectionKey),
      createRead(collectionMasterEditionAccountKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Authority
  /// @param payerKey payer
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction unverifySizedCollectionItem(final AccountMeta invokedTokenMetadataProgramMeta,
                                                        final PublicKey metadataKey,
                                                        final PublicKey collectionAuthorityKey,
                                                        final PublicKey payerKey,
                                                        final PublicKey collectionMintKey,
                                                        final PublicKey collectionKey,
                                                        final PublicKey collectionMasterEditionAccountKey,
                                                        final PublicKey collectionAuthorityRecordKey) {
    final var keys = unverifySizedCollectionItemKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      collectionAuthorityKey,
      payerKey,
      collectionMintKey,
      collectionKey,
      collectionMasterEditionAccountKey,
      collectionAuthorityRecordKey
    );
    return unverifySizedCollectionItem(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction unverifySizedCollectionItem(final AccountMeta invokedTokenMetadataProgramMeta,
                                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, UNVERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR);
  }

  public static final Discriminator SET_AND_VERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR = toDiscriminator(184, 105, 169, 35, 3, 88, 238, 67);

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey payer
  /// @param updateAuthorityKey Update Authority of Collection NFT and NFT
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> setAndVerifySizedCollectionItemKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                      final PublicKey metadataKey,
                                                                      final PublicKey collectionAuthorityKey,
                                                                      final PublicKey payerKey,
                                                                      final PublicKey updateAuthorityKey,
                                                                      final PublicKey collectionMintKey,
                                                                      final PublicKey collectionKey,
                                                                      final PublicKey collectionMasterEditionAccountKey,
                                                                      final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(collectionAuthorityKey),
      createWritableSigner(payerKey),
      createRead(updateAuthorityKey),
      createRead(collectionMintKey),
      createWrite(collectionKey),
      createRead(collectionMasterEditionAccountKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param payerKey payer
  /// @param updateAuthorityKey Update Authority of Collection NFT and NFT
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionKey Metadata Account of the Collection
  /// @param collectionMasterEditionAccountKey MasterEdition2 Account of the Collection Token
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction setAndVerifySizedCollectionItem(final AccountMeta invokedTokenMetadataProgramMeta,
                                                            final PublicKey metadataKey,
                                                            final PublicKey collectionAuthorityKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey updateAuthorityKey,
                                                            final PublicKey collectionMintKey,
                                                            final PublicKey collectionKey,
                                                            final PublicKey collectionMasterEditionAccountKey,
                                                            final PublicKey collectionAuthorityRecordKey) {
    final var keys = setAndVerifySizedCollectionItemKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      collectionAuthorityKey,
      payerKey,
      updateAuthorityKey,
      collectionMintKey,
      collectionKey,
      collectionMasterEditionAccountKey,
      collectionAuthorityRecordKey
    );
    return setAndVerifySizedCollectionItem(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction setAndVerifySizedCollectionItem(final AccountMeta invokedTokenMetadataProgramMeta,
                                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, SET_AND_VERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_METADATA_ACCOUNT_V_3_DISCRIMINATOR = toDiscriminator(43, 12, 175, 14, 252, 45, 188, 155);

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param mintKey Mint of token asset
  /// @param mintAuthorityKey Mint authority
  /// @param payerKey payer
  /// @param updateAuthorityKey update authority info
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static List<AccountMeta> createMetadataAccountV3Keys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                              final PublicKey metadataKey,
                                                              final PublicKey mintKey,
                                                              final PublicKey mintAuthorityKey,
                                                              final PublicKey payerKey,
                                                              final PublicKey updateAuthorityKey,
                                                              final PublicKey systemProgramKey,
                                                              final PublicKey rentKey) {
    return List.of(
      createWrite(metadataKey),
      createRead(mintKey),
      createReadOnlySigner(mintAuthorityKey),
      createWritableSigner(payerKey),
      updateAuthorityKey == null ? createRead(invokedTokenMetadataProgramMeta.publicKey()) : createReadOnlySigner(updateAuthorityKey),
      createRead(systemProgramKey),
      createRead(requireNonNullElse(rentKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata key (pda of 'metadata', program id, mint id)
  /// @param mintKey Mint of token asset
  /// @param mintAuthorityKey Mint authority
  /// @param payerKey payer
  /// @param updateAuthorityKey update authority info
  /// @param systemProgramKey System program
  /// @param rentKey Rent info
  public static Instruction createMetadataAccountV3(final AccountMeta invokedTokenMetadataProgramMeta,
                                                    final PublicKey metadataKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey mintAuthorityKey,
                                                    final PublicKey payerKey,
                                                    final PublicKey updateAuthorityKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey rentKey,
                                                    final CreateMetadataAccountArgsV3 createMetadataAccountArgsV3) {
    final var keys = createMetadataAccountV3Keys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      mintKey,
      mintAuthorityKey,
      payerKey,
      updateAuthorityKey,
      systemProgramKey,
      rentKey
    );
    return createMetadataAccountV3(invokedTokenMetadataProgramMeta, keys, createMetadataAccountArgsV3);
  }

  public static Instruction createMetadataAccountV3(final AccountMeta invokedTokenMetadataProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final CreateMetadataAccountArgsV3 createMetadataAccountArgsV3) {
    final byte[] _data = new byte[8 + createMetadataAccountArgsV3.l()];
    int i = CREATE_METADATA_ACCOUNT_V_3_DISCRIMINATOR.write(_data, 0);
    createMetadataAccountArgsV3.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record CreateMetadataAccountV3IxData(Discriminator discriminator, CreateMetadataAccountArgsV3 createMetadataAccountArgsV3) implements SerDe {  

    public static CreateMetadataAccountV3IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int CREATE_METADATA_ACCOUNT_ARGS_V_3_OFFSET = 8;

    public static CreateMetadataAccountV3IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var createMetadataAccountArgsV3 = CreateMetadataAccountArgsV3.read(_data, i);
      return new CreateMetadataAccountV3IxData(discriminator, createMetadataAccountArgsV3);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += createMetadataAccountArgsV3.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + createMetadataAccountArgsV3.l();
    }
  }

  public static final Discriminator SET_COLLECTION_SIZE_DISCRIMINATOR = toDiscriminator(157, 254, 166, 144, 43, 223, 199, 39);

  /// @param collectionMetadataKey Collection Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> setCollectionSizeKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                        final PublicKey collectionMetadataKey,
                                                        final PublicKey collectionAuthorityKey,
                                                        final PublicKey collectionMintKey,
                                                        final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(collectionMetadataKey),
      createWritableSigner(collectionAuthorityKey),
      createRead(collectionMintKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param collectionMetadataKey Collection Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction setCollectionSize(final AccountMeta invokedTokenMetadataProgramMeta,
                                              final PublicKey collectionMetadataKey,
                                              final PublicKey collectionAuthorityKey,
                                              final PublicKey collectionMintKey,
                                              final PublicKey collectionAuthorityRecordKey,
                                              final SetCollectionSizeArgs setCollectionSizeArgs) {
    final var keys = setCollectionSizeKeys(
      invokedTokenMetadataProgramMeta,
      collectionMetadataKey,
      collectionAuthorityKey,
      collectionMintKey,
      collectionAuthorityRecordKey
    );
    return setCollectionSize(invokedTokenMetadataProgramMeta, keys, setCollectionSizeArgs);
  }

  public static Instruction setCollectionSize(final AccountMeta invokedTokenMetadataProgramMeta,
                                              final List<AccountMeta> keys,
                                              final SetCollectionSizeArgs setCollectionSizeArgs) {
    final byte[] _data = new byte[8 + setCollectionSizeArgs.l()];
    int i = SET_COLLECTION_SIZE_DISCRIMINATOR.write(_data, 0);
    setCollectionSizeArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record SetCollectionSizeIxData(Discriminator discriminator, SetCollectionSizeArgs setCollectionSizeArgs) implements SerDe {  

    public static SetCollectionSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int SET_COLLECTION_SIZE_ARGS_OFFSET = 8;

    public static SetCollectionSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var setCollectionSizeArgs = SetCollectionSizeArgs.read(_data, i);
      return new SetCollectionSizeIxData(discriminator, setCollectionSizeArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += setCollectionSizeArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_TOKEN_STANDARD_DISCRIMINATOR = toDiscriminator(147, 212, 106, 195, 30, 170, 209, 128);

  /// @param metadataKey Metadata account
  /// @param updateAuthorityKey Metadata update authority
  /// @param mintKey Mint account
  /// @param editionKey Edition account
  public static List<AccountMeta> setTokenStandardKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final PublicKey metadataKey,
                                                       final PublicKey updateAuthorityKey,
                                                       final PublicKey mintKey,
                                                       final PublicKey editionKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(updateAuthorityKey),
      createRead(mintKey),
      createRead(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param updateAuthorityKey Metadata update authority
  /// @param mintKey Mint account
  /// @param editionKey Edition account
  public static Instruction setTokenStandard(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey metadataKey,
                                             final PublicKey updateAuthorityKey,
                                             final PublicKey mintKey,
                                             final PublicKey editionKey) {
    final var keys = setTokenStandardKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      updateAuthorityKey,
      mintKey,
      editionKey
    );
    return setTokenStandard(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction setTokenStandard(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, SET_TOKEN_STANDARD_DISCRIMINATOR);
  }

  public static final Discriminator BUBBLEGUM_SET_COLLECTION_SIZE_DISCRIMINATOR = toDiscriminator(230, 215, 231, 226, 156, 188, 56, 6);

  /// @param collectionMetadataKey Collection Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param collectionMintKey Mint of the Collection
  /// @param bubblegumSignerKey Signing PDA of Bubblegum program
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static List<AccountMeta> bubblegumSetCollectionSizeKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                                 final PublicKey collectionMetadataKey,
                                                                 final PublicKey collectionAuthorityKey,
                                                                 final PublicKey collectionMintKey,
                                                                 final PublicKey bubblegumSignerKey,
                                                                 final PublicKey collectionAuthorityRecordKey) {
    return List.of(
      createWrite(collectionMetadataKey),
      createReadOnlySigner(collectionAuthorityKey),
      createRead(collectionMintKey),
      createReadOnlySigner(bubblegumSignerKey),
      createRead(requireNonNullElse(collectionAuthorityRecordKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param collectionMetadataKey Collection Metadata account
  /// @param collectionAuthorityKey Collection Update authority
  /// @param collectionMintKey Mint of the Collection
  /// @param bubblegumSignerKey Signing PDA of Bubblegum program
  /// @param collectionAuthorityRecordKey Collection Authority Record PDA
  public static Instruction bubblegumSetCollectionSize(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final PublicKey collectionMetadataKey,
                                                       final PublicKey collectionAuthorityKey,
                                                       final PublicKey collectionMintKey,
                                                       final PublicKey bubblegumSignerKey,
                                                       final PublicKey collectionAuthorityRecordKey,
                                                       final SetCollectionSizeArgs setCollectionSizeArgs) {
    final var keys = bubblegumSetCollectionSizeKeys(
      invokedTokenMetadataProgramMeta,
      collectionMetadataKey,
      collectionAuthorityKey,
      collectionMintKey,
      bubblegumSignerKey,
      collectionAuthorityRecordKey
    );
    return bubblegumSetCollectionSize(invokedTokenMetadataProgramMeta, keys, setCollectionSizeArgs);
  }

  public static Instruction bubblegumSetCollectionSize(final AccountMeta invokedTokenMetadataProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final SetCollectionSizeArgs setCollectionSizeArgs) {
    final byte[] _data = new byte[8 + setCollectionSizeArgs.l()];
    int i = BUBBLEGUM_SET_COLLECTION_SIZE_DISCRIMINATOR.write(_data, 0);
    setCollectionSizeArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record BubblegumSetCollectionSizeIxData(Discriminator discriminator, SetCollectionSizeArgs setCollectionSizeArgs) implements SerDe {  

    public static BubblegumSetCollectionSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int SET_COLLECTION_SIZE_ARGS_OFFSET = 8;

    public static BubblegumSetCollectionSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var setCollectionSizeArgs = SetCollectionSizeArgs.read(_data, i);
      return new BubblegumSetCollectionSizeIxData(discriminator, setCollectionSizeArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += setCollectionSizeArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BURN_EDITION_NFT_DISCRIMINATOR = toDiscriminator(221, 105, 196, 64, 164, 27, 93, 197);

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param ownerKey NFT owner
  /// @param printEditionMintKey Mint of the print edition NFT
  /// @param masterEditionMintKey Mint of the original/master NFT
  /// @param printEditionTokenAccountKey Token account the print edition NFT is in
  /// @param masterEditionTokenAccountKey Token account the Master Edition NFT is in
  /// @param masterEditionAccountKey MasterEdition2 of the original NFT
  /// @param printEditionAccountKey Print Edition account of the NFT
  /// @param editionMarkerAccountKey Edition Marker PDA of the NFT
  /// @param splTokenProgramKey SPL Token Program
  public static List<AccountMeta> burnEditionNftKeys(final PublicKey metadataKey,
                                                     final PublicKey ownerKey,
                                                     final PublicKey printEditionMintKey,
                                                     final PublicKey masterEditionMintKey,
                                                     final PublicKey printEditionTokenAccountKey,
                                                     final PublicKey masterEditionTokenAccountKey,
                                                     final PublicKey masterEditionAccountKey,
                                                     final PublicKey printEditionAccountKey,
                                                     final PublicKey editionMarkerAccountKey,
                                                     final PublicKey splTokenProgramKey) {
    return List.of(
      createWrite(metadataKey),
      createWritableSigner(ownerKey),
      createWrite(printEditionMintKey),
      createRead(masterEditionMintKey),
      createWrite(printEditionTokenAccountKey),
      createRead(masterEditionTokenAccountKey),
      createWrite(masterEditionAccountKey),
      createWrite(printEditionAccountKey),
      createWrite(editionMarkerAccountKey),
      createRead(splTokenProgramKey)
    );
  }

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param ownerKey NFT owner
  /// @param printEditionMintKey Mint of the print edition NFT
  /// @param masterEditionMintKey Mint of the original/master NFT
  /// @param printEditionTokenAccountKey Token account the print edition NFT is in
  /// @param masterEditionTokenAccountKey Token account the Master Edition NFT is in
  /// @param masterEditionAccountKey MasterEdition2 of the original NFT
  /// @param printEditionAccountKey Print Edition account of the NFT
  /// @param editionMarkerAccountKey Edition Marker PDA of the NFT
  /// @param splTokenProgramKey SPL Token Program
  public static Instruction burnEditionNft(final AccountMeta invokedTokenMetadataProgramMeta,
                                           final PublicKey metadataKey,
                                           final PublicKey ownerKey,
                                           final PublicKey printEditionMintKey,
                                           final PublicKey masterEditionMintKey,
                                           final PublicKey printEditionTokenAccountKey,
                                           final PublicKey masterEditionTokenAccountKey,
                                           final PublicKey masterEditionAccountKey,
                                           final PublicKey printEditionAccountKey,
                                           final PublicKey editionMarkerAccountKey,
                                           final PublicKey splTokenProgramKey) {
    final var keys = burnEditionNftKeys(
      metadataKey,
      ownerKey,
      printEditionMintKey,
      masterEditionMintKey,
      printEditionTokenAccountKey,
      masterEditionTokenAccountKey,
      masterEditionAccountKey,
      printEditionAccountKey,
      editionMarkerAccountKey,
      splTokenProgramKey
    );
    return burnEditionNft(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction burnEditionNft(final AccountMeta invokedTokenMetadataProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, BURN_EDITION_NFT_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_ESCROW_ACCOUNT_DISCRIMINATOR = toDiscriminator(146, 147, 225, 47, 51, 64, 112, 1);

  /// @param escrowKey Escrow account
  /// @param metadataKey Metadata account
  /// @param mintKey Mint account
  /// @param tokenAccountKey Token account of the token
  /// @param editionKey Edition account
  /// @param payerKey Wallet paying for the transaction and new account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param authorityKey Authority/creator of the escrow account
  public static List<AccountMeta> createEscrowAccountKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                          final PublicKey escrowKey,
                                                          final PublicKey metadataKey,
                                                          final PublicKey mintKey,
                                                          final PublicKey tokenAccountKey,
                                                          final PublicKey editionKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey systemProgramKey,
                                                          final PublicKey sysvarInstructionsKey,
                                                          final PublicKey authorityKey) {
    return List.of(
      createWrite(escrowKey),
      createWrite(metadataKey),
      createRead(mintKey),
      createRead(tokenAccountKey),
      createRead(editionKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      authorityKey == null ? createRead(invokedTokenMetadataProgramMeta.publicKey()) : createReadOnlySigner(authorityKey)
    );
  }

  /// @param escrowKey Escrow account
  /// @param metadataKey Metadata account
  /// @param mintKey Mint account
  /// @param tokenAccountKey Token account of the token
  /// @param editionKey Edition account
  /// @param payerKey Wallet paying for the transaction and new account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param authorityKey Authority/creator of the escrow account
  public static Instruction createEscrowAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final PublicKey escrowKey,
                                                final PublicKey metadataKey,
                                                final PublicKey mintKey,
                                                final PublicKey tokenAccountKey,
                                                final PublicKey editionKey,
                                                final PublicKey payerKey,
                                                final PublicKey systemProgramKey,
                                                final PublicKey sysvarInstructionsKey,
                                                final PublicKey authorityKey) {
    final var keys = createEscrowAccountKeys(
      invokedTokenMetadataProgramMeta,
      escrowKey,
      metadataKey,
      mintKey,
      tokenAccountKey,
      editionKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      authorityKey
    );
    return createEscrowAccount(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction createEscrowAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CREATE_ESCROW_ACCOUNT_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_ESCROW_ACCOUNT_DISCRIMINATOR = toDiscriminator(209, 42, 208, 179, 140, 78, 18, 43);

  /// @param escrowKey Escrow account
  /// @param metadataKey Metadata account
  /// @param mintKey Mint account
  /// @param tokenAccountKey Token account
  /// @param editionKey Edition account
  /// @param payerKey Wallet paying for the transaction and new account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  public static List<AccountMeta> closeEscrowAccountKeys(final PublicKey escrowKey,
                                                         final PublicKey metadataKey,
                                                         final PublicKey mintKey,
                                                         final PublicKey tokenAccountKey,
                                                         final PublicKey editionKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey systemProgramKey,
                                                         final PublicKey sysvarInstructionsKey) {
    return List.of(
      createWrite(escrowKey),
      createWrite(metadataKey),
      createRead(mintKey),
      createRead(tokenAccountKey),
      createRead(editionKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey)
    );
  }

  /// @param escrowKey Escrow account
  /// @param metadataKey Metadata account
  /// @param mintKey Mint account
  /// @param tokenAccountKey Token account
  /// @param editionKey Edition account
  /// @param payerKey Wallet paying for the transaction and new account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  public static Instruction closeEscrowAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final PublicKey escrowKey,
                                               final PublicKey metadataKey,
                                               final PublicKey mintKey,
                                               final PublicKey tokenAccountKey,
                                               final PublicKey editionKey,
                                               final PublicKey payerKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey sysvarInstructionsKey) {
    final var keys = closeEscrowAccountKeys(
      escrowKey,
      metadataKey,
      mintKey,
      tokenAccountKey,
      editionKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey
    );
    return closeEscrowAccount(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction closeEscrowAccount(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CLOSE_ESCROW_ACCOUNT_DISCRIMINATOR);
  }

  public static final Discriminator TRANSFER_OUT_OF_ESCROW_DISCRIMINATOR = toDiscriminator(55, 186, 186, 216, 115, 158, 58, 153);

  /// @param escrowKey Escrow account
  /// @param metadataKey Metadata account
  /// @param payerKey Wallet paying for the transaction and new account
  /// @param attributeMintKey Mint account for the new attribute
  /// @param attributeSrcKey Token account source for the new attribute
  /// @param attributeDstKey Token account, owned by TM, destination for the new attribute
  /// @param escrowMintKey Mint account that the escrow is attached
  /// @param escrowAccountKey Token account that holds the token the escrow is attached to
  /// @param systemProgramKey System program
  /// @param ataProgramKey Associated Token program
  /// @param tokenProgramKey Token program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param authorityKey Authority/creator of the escrow account
  public static List<AccountMeta> transferOutOfEscrowKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                                          final PublicKey escrowKey,
                                                          final PublicKey metadataKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey attributeMintKey,
                                                          final PublicKey attributeSrcKey,
                                                          final PublicKey attributeDstKey,
                                                          final PublicKey escrowMintKey,
                                                          final PublicKey escrowAccountKey,
                                                          final PublicKey systemProgramKey,
                                                          final PublicKey ataProgramKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey sysvarInstructionsKey,
                                                          final PublicKey authorityKey) {
    return List.of(
      createRead(escrowKey),
      createWrite(metadataKey),
      createWritableSigner(payerKey),
      createRead(attributeMintKey),
      createWrite(attributeSrcKey),
      createWrite(attributeDstKey),
      createRead(escrowMintKey),
      createRead(escrowAccountKey),
      createRead(systemProgramKey),
      createRead(ataProgramKey),
      createRead(tokenProgramKey),
      createRead(sysvarInstructionsKey),
      authorityKey == null ? createRead(invokedTokenMetadataProgramMeta.publicKey()) : createReadOnlySigner(authorityKey)
    );
  }

  /// @param escrowKey Escrow account
  /// @param metadataKey Metadata account
  /// @param payerKey Wallet paying for the transaction and new account
  /// @param attributeMintKey Mint account for the new attribute
  /// @param attributeSrcKey Token account source for the new attribute
  /// @param attributeDstKey Token account, owned by TM, destination for the new attribute
  /// @param escrowMintKey Mint account that the escrow is attached
  /// @param escrowAccountKey Token account that holds the token the escrow is attached to
  /// @param systemProgramKey System program
  /// @param ataProgramKey Associated Token program
  /// @param tokenProgramKey Token program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param authorityKey Authority/creator of the escrow account
  public static Instruction transferOutOfEscrow(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final PublicKey escrowKey,
                                                final PublicKey metadataKey,
                                                final PublicKey payerKey,
                                                final PublicKey attributeMintKey,
                                                final PublicKey attributeSrcKey,
                                                final PublicKey attributeDstKey,
                                                final PublicKey escrowMintKey,
                                                final PublicKey escrowAccountKey,
                                                final PublicKey systemProgramKey,
                                                final PublicKey ataProgramKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey sysvarInstructionsKey,
                                                final PublicKey authorityKey,
                                                final TransferOutOfEscrowArgs transferOutOfEscrowArgs) {
    final var keys = transferOutOfEscrowKeys(
      invokedTokenMetadataProgramMeta,
      escrowKey,
      metadataKey,
      payerKey,
      attributeMintKey,
      attributeSrcKey,
      attributeDstKey,
      escrowMintKey,
      escrowAccountKey,
      systemProgramKey,
      ataProgramKey,
      tokenProgramKey,
      sysvarInstructionsKey,
      authorityKey
    );
    return transferOutOfEscrow(invokedTokenMetadataProgramMeta, keys, transferOutOfEscrowArgs);
  }

  public static Instruction transferOutOfEscrow(final AccountMeta invokedTokenMetadataProgramMeta,
                                                final List<AccountMeta> keys,
                                                final TransferOutOfEscrowArgs transferOutOfEscrowArgs) {
    final byte[] _data = new byte[8 + transferOutOfEscrowArgs.l()];
    int i = TRANSFER_OUT_OF_ESCROW_DISCRIMINATOR.write(_data, 0);
    transferOutOfEscrowArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record TransferOutOfEscrowIxData(Discriminator discriminator, TransferOutOfEscrowArgs transferOutOfEscrowArgs) implements SerDe {  

    public static TransferOutOfEscrowIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int TRANSFER_OUT_OF_ESCROW_ARGS_OFFSET = 8;

    public static TransferOutOfEscrowIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var transferOutOfEscrowArgs = TransferOutOfEscrowArgs.read(_data, i);
      return new TransferOutOfEscrowIxData(discriminator, transferOutOfEscrowArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += transferOutOfEscrowArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BURN_DISCRIMINATOR = toDiscriminator(116, 110, 29, 56, 107, 219, 42, 93);

  /// @param authorityKey Asset owner or Utility delegate
  /// @param collectionMetadataKey Metadata of the Collection
  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param editionKey Edition of the asset
  /// @param mintKey Mint of token asset
  /// @param tokenKey Token account to close
  /// @param masterEditionKey Master edition account
  /// @param masterEditionMintKey Master edition mint of the asset
  /// @param masterEditionTokenKey Master edition token account
  /// @param editionMarkerKey Edition marker account
  /// @param tokenRecordKey Token record account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  public static List<AccountMeta> burnKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                           final PublicKey authorityKey,
                                           final PublicKey collectionMetadataKey,
                                           final PublicKey metadataKey,
                                           final PublicKey editionKey,
                                           final PublicKey mintKey,
                                           final PublicKey tokenKey,
                                           final PublicKey masterEditionKey,
                                           final PublicKey masterEditionMintKey,
                                           final PublicKey masterEditionTokenKey,
                                           final PublicKey editionMarkerKey,
                                           final PublicKey tokenRecordKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey sysvarInstructionsKey,
                                           final PublicKey splTokenProgramKey) {
    return List.of(
      createWritableSigner(authorityKey),
      createWrite(requireNonNullElse(collectionMetadataKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(metadataKey),
      createWrite(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(mintKey),
      createWrite(tokenKey),
      createWrite(requireNonNullElse(masterEditionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(masterEditionMintKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(masterEditionTokenKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(editionMarkerKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(splTokenProgramKey)
    );
  }

  /// @param authorityKey Asset owner or Utility delegate
  /// @param collectionMetadataKey Metadata of the Collection
  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param editionKey Edition of the asset
  /// @param mintKey Mint of token asset
  /// @param tokenKey Token account to close
  /// @param masterEditionKey Master edition account
  /// @param masterEditionMintKey Master edition mint of the asset
  /// @param masterEditionTokenKey Master edition token account
  /// @param editionMarkerKey Edition marker account
  /// @param tokenRecordKey Token record account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  public static Instruction burn(final AccountMeta invokedTokenMetadataProgramMeta,
                                 final PublicKey authorityKey,
                                 final PublicKey collectionMetadataKey,
                                 final PublicKey metadataKey,
                                 final PublicKey editionKey,
                                 final PublicKey mintKey,
                                 final PublicKey tokenKey,
                                 final PublicKey masterEditionKey,
                                 final PublicKey masterEditionMintKey,
                                 final PublicKey masterEditionTokenKey,
                                 final PublicKey editionMarkerKey,
                                 final PublicKey tokenRecordKey,
                                 final PublicKey systemProgramKey,
                                 final PublicKey sysvarInstructionsKey,
                                 final PublicKey splTokenProgramKey,
                                 final BurnArgs burnArgs) {
    final var keys = burnKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      collectionMetadataKey,
      metadataKey,
      editionKey,
      mintKey,
      tokenKey,
      masterEditionKey,
      masterEditionMintKey,
      masterEditionTokenKey,
      editionMarkerKey,
      tokenRecordKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey
    );
    return burn(invokedTokenMetadataProgramMeta, keys, burnArgs);
  }

  public static Instruction burn(final AccountMeta invokedTokenMetadataProgramMeta,
                                 final List<AccountMeta> keys,
                                 final BurnArgs burnArgs) {
    final byte[] _data = new byte[8 + burnArgs.l()];
    int i = BURN_DISCRIMINATOR.write(_data, 0);
    burnArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record BurnIxData(Discriminator discriminator, BurnArgs burnArgs) implements SerDe {  

    public static BurnIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BURN_ARGS_OFFSET = 8;

    public static BurnIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var burnArgs = BurnArgs.read(_data, i);
      return new BurnIxData(discriminator, burnArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += burnArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + burnArgs.l();
    }
  }

  public static final Discriminator CREATE_DISCRIMINATOR = toDiscriminator(24, 30, 200, 40, 5, 28, 7, 119);

  /// @param metadataKey Unallocated metadata account with address as pda of 'metadata', program id, mint id
  /// @param masterEditionKey Unallocated edition account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Mint of token asset
  /// @param authorityKey Mint authority
  /// @param payerKey Payer
  /// @param updateAuthorityKey Update authority for the metadata account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token program
  public static List<AccountMeta> createKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey metadataKey,
                                             final PublicKey masterEditionKey,
                                             final PublicKey mintKey,
                                             final PublicKey authorityKey,
                                             final PublicKey payerKey,
                                             final PublicKey updateAuthorityKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey sysvarInstructionsKey,
                                             final PublicKey splTokenProgramKey) {
    return List.of(
      createWrite(metadataKey),
      createWrite(requireNonNullElse(masterEditionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(mintKey),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(updateAuthorityKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(splTokenProgramKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Unallocated metadata account with address as pda of 'metadata', program id, mint id
  /// @param masterEditionKey Unallocated edition account with address as pda of 'metadata', program id, mint, 'edition'
  /// @param mintKey Mint of token asset
  /// @param authorityKey Mint authority
  /// @param payerKey Payer
  /// @param updateAuthorityKey Update authority for the metadata account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token program
  public static Instruction create(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final PublicKey metadataKey,
                                   final PublicKey masterEditionKey,
                                   final PublicKey mintKey,
                                   final PublicKey authorityKey,
                                   final PublicKey payerKey,
                                   final PublicKey updateAuthorityKey,
                                   final PublicKey systemProgramKey,
                                   final PublicKey sysvarInstructionsKey,
                                   final PublicKey splTokenProgramKey,
                                   final CreateArgs createArgs) {
    final var keys = createKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      masterEditionKey,
      mintKey,
      authorityKey,
      payerKey,
      updateAuthorityKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey
    );
    return create(invokedTokenMetadataProgramMeta, keys, createArgs);
  }

  public static Instruction create(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final List<AccountMeta> keys,
                                   final CreateArgs createArgs) {
    final byte[] _data = new byte[8 + createArgs.l()];
    int i = CREATE_DISCRIMINATOR.write(_data, 0);
    createArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record CreateIxData(Discriminator discriminator, CreateArgs createArgs) implements SerDe {  

    public static CreateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int CREATE_ARGS_OFFSET = 8;

    public static CreateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var createArgs = CreateArgs.read(_data, i);
      return new CreateIxData(discriminator, createArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += createArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + createArgs.l();
    }
  }

  public static final Discriminator MINT_DISCRIMINATOR = toDiscriminator(51, 57, 225, 47, 182, 146, 137, 166);

  /// @param tokenKey Token or Associated Token account
  /// @param tokenOwnerKey Owner of the token account
  /// @param metadataKey Metadata account (pda of 'metadata', program id, mint id)
  /// @param masterEditionKey Master Edition account
  /// @param tokenRecordKey Token record account
  /// @param mintKey Mint of token asset
  /// @param authorityKey (Mint or Update) authority
  /// @param delegateRecordKey Metadata delegate record
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token program
  /// @param splAtaProgramKey SPL Associated Token Account program
  /// @param authorizationRulesProgramKey Token Authorization Rules program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> mintKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                           final PublicKey tokenKey,
                                           final PublicKey tokenOwnerKey,
                                           final PublicKey metadataKey,
                                           final PublicKey masterEditionKey,
                                           final PublicKey tokenRecordKey,
                                           final PublicKey mintKey,
                                           final PublicKey authorityKey,
                                           final PublicKey delegateRecordKey,
                                           final PublicKey payerKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey sysvarInstructionsKey,
                                           final PublicKey splTokenProgramKey,
                                           final PublicKey splAtaProgramKey,
                                           final PublicKey authorizationRulesProgramKey,
                                           final PublicKey authorizationRulesKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(requireNonNullElse(tokenOwnerKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(metadataKey),
      createWrite(requireNonNullElse(masterEditionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(mintKey),
      createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(splTokenProgramKey),
      createRead(splAtaProgramKey),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param tokenKey Token or Associated Token account
  /// @param tokenOwnerKey Owner of the token account
  /// @param metadataKey Metadata account (pda of 'metadata', program id, mint id)
  /// @param masterEditionKey Master Edition account
  /// @param tokenRecordKey Token record account
  /// @param mintKey Mint of token asset
  /// @param authorityKey (Mint or Update) authority
  /// @param delegateRecordKey Metadata delegate record
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token program
  /// @param splAtaProgramKey SPL Associated Token Account program
  /// @param authorizationRulesProgramKey Token Authorization Rules program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction mint(final AccountMeta invokedTokenMetadataProgramMeta,
                                 final PublicKey tokenKey,
                                 final PublicKey tokenOwnerKey,
                                 final PublicKey metadataKey,
                                 final PublicKey masterEditionKey,
                                 final PublicKey tokenRecordKey,
                                 final PublicKey mintKey,
                                 final PublicKey authorityKey,
                                 final PublicKey delegateRecordKey,
                                 final PublicKey payerKey,
                                 final PublicKey systemProgramKey,
                                 final PublicKey sysvarInstructionsKey,
                                 final PublicKey splTokenProgramKey,
                                 final PublicKey splAtaProgramKey,
                                 final PublicKey authorizationRulesProgramKey,
                                 final PublicKey authorizationRulesKey,
                                 final MintArgs mintArgs) {
    final var keys = mintKeys(
      invokedTokenMetadataProgramMeta,
      tokenKey,
      tokenOwnerKey,
      metadataKey,
      masterEditionKey,
      tokenRecordKey,
      mintKey,
      authorityKey,
      delegateRecordKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      splAtaProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return mint(invokedTokenMetadataProgramMeta, keys, mintArgs);
  }

  public static Instruction mint(final AccountMeta invokedTokenMetadataProgramMeta,
                                 final List<AccountMeta> keys,
                                 final MintArgs mintArgs) {
    final byte[] _data = new byte[8 + mintArgs.l()];
    int i = MINT_DISCRIMINATOR.write(_data, 0);
    mintArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record MintIxData(Discriminator discriminator, MintArgs mintArgs) implements SerDe {  

    public static MintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int MINT_ARGS_OFFSET = 8;

    public static MintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mintArgs = MintArgs.read(_data, i);
      return new MintIxData(discriminator, mintArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += mintArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + mintArgs.l();
    }
  }

  public static final Discriminator DELEGATE_DISCRIMINATOR = toDiscriminator(90, 147, 75, 178, 85, 88, 4, 137);

  /// @param delegateRecordKey Delegate record account
  /// @param delegateKey Owner of the delegated account
  /// @param metadataKey Metadata account
  /// @param masterEditionKey Master Edition account
  /// @param tokenRecordKey Token record account
  /// @param mintKey Mint of metadata
  /// @param tokenKey Token account of mint
  /// @param authorityKey Update authority or token owner
  /// @param payerKey Payer
  /// @param systemProgramKey System Program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> delegateKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final PublicKey delegateRecordKey,
                                               final PublicKey delegateKey,
                                               final PublicKey metadataKey,
                                               final PublicKey masterEditionKey,
                                               final PublicKey tokenRecordKey,
                                               final PublicKey mintKey,
                                               final PublicKey tokenKey,
                                               final PublicKey authorityKey,
                                               final PublicKey payerKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey sysvarInstructionsKey,
                                               final PublicKey splTokenProgramKey,
                                               final PublicKey authorizationRulesProgramKey,
                                               final PublicKey authorizationRulesKey) {
    return List.of(
      createWrite(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(delegateKey),
      createWrite(metadataKey),
      createRead(requireNonNullElse(masterEditionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(mintKey),
      createWrite(requireNonNullElse(tokenKey, invokedTokenMetadataProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(splTokenProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param delegateRecordKey Delegate record account
  /// @param delegateKey Owner of the delegated account
  /// @param metadataKey Metadata account
  /// @param masterEditionKey Master Edition account
  /// @param tokenRecordKey Token record account
  /// @param mintKey Mint of metadata
  /// @param tokenKey Token account of mint
  /// @param authorityKey Update authority or token owner
  /// @param payerKey Payer
  /// @param systemProgramKey System Program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction delegate(final AccountMeta invokedTokenMetadataProgramMeta,
                                     final PublicKey delegateRecordKey,
                                     final PublicKey delegateKey,
                                     final PublicKey metadataKey,
                                     final PublicKey masterEditionKey,
                                     final PublicKey tokenRecordKey,
                                     final PublicKey mintKey,
                                     final PublicKey tokenKey,
                                     final PublicKey authorityKey,
                                     final PublicKey payerKey,
                                     final PublicKey systemProgramKey,
                                     final PublicKey sysvarInstructionsKey,
                                     final PublicKey splTokenProgramKey,
                                     final PublicKey authorizationRulesProgramKey,
                                     final PublicKey authorizationRulesKey,
                                     final DelegateArgs delegateArgs) {
    final var keys = delegateKeys(
      invokedTokenMetadataProgramMeta,
      delegateRecordKey,
      delegateKey,
      metadataKey,
      masterEditionKey,
      tokenRecordKey,
      mintKey,
      tokenKey,
      authorityKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return delegate(invokedTokenMetadataProgramMeta, keys, delegateArgs);
  }

  public static Instruction delegate(final AccountMeta invokedTokenMetadataProgramMeta,
                                     final List<AccountMeta> keys,
                                     final DelegateArgs delegateArgs) {
    final byte[] _data = new byte[8 + delegateArgs.l()];
    int i = DELEGATE_DISCRIMINATOR.write(_data, 0);
    delegateArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record DelegateIxData(Discriminator discriminator, DelegateArgs delegateArgs) implements SerDe {  

    public static DelegateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DELEGATE_ARGS_OFFSET = 8;

    public static DelegateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var delegateArgs = DelegateArgs.read(_data, i);
      return new DelegateIxData(discriminator, delegateArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += delegateArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + delegateArgs.l();
    }
  }

  public static final Discriminator REVOKE_DISCRIMINATOR = toDiscriminator(170, 23, 31, 34, 133, 173, 93, 242);

  /// @param delegateRecordKey Delegate record account
  /// @param delegateKey Owner of the delegated account
  /// @param metadataKey Metadata account
  /// @param masterEditionKey Master Edition account
  /// @param tokenRecordKey Token record account
  /// @param mintKey Mint of metadata
  /// @param tokenKey Token account of mint
  /// @param authorityKey Update authority or token owner
  /// @param payerKey Payer
  /// @param systemProgramKey System Program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> revokeKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey delegateRecordKey,
                                             final PublicKey delegateKey,
                                             final PublicKey metadataKey,
                                             final PublicKey masterEditionKey,
                                             final PublicKey tokenRecordKey,
                                             final PublicKey mintKey,
                                             final PublicKey tokenKey,
                                             final PublicKey authorityKey,
                                             final PublicKey payerKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey sysvarInstructionsKey,
                                             final PublicKey splTokenProgramKey,
                                             final PublicKey authorizationRulesProgramKey,
                                             final PublicKey authorizationRulesKey) {
    return List.of(
      createWrite(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(delegateKey),
      createWrite(metadataKey),
      createRead(requireNonNullElse(masterEditionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(mintKey),
      createWrite(requireNonNullElse(tokenKey, invokedTokenMetadataProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(splTokenProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param delegateRecordKey Delegate record account
  /// @param delegateKey Owner of the delegated account
  /// @param metadataKey Metadata account
  /// @param masterEditionKey Master Edition account
  /// @param tokenRecordKey Token record account
  /// @param mintKey Mint of metadata
  /// @param tokenKey Token account of mint
  /// @param authorityKey Update authority or token owner
  /// @param payerKey Payer
  /// @param systemProgramKey System Program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction revoke(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final PublicKey delegateRecordKey,
                                   final PublicKey delegateKey,
                                   final PublicKey metadataKey,
                                   final PublicKey masterEditionKey,
                                   final PublicKey tokenRecordKey,
                                   final PublicKey mintKey,
                                   final PublicKey tokenKey,
                                   final PublicKey authorityKey,
                                   final PublicKey payerKey,
                                   final PublicKey systemProgramKey,
                                   final PublicKey sysvarInstructionsKey,
                                   final PublicKey splTokenProgramKey,
                                   final PublicKey authorizationRulesProgramKey,
                                   final PublicKey authorizationRulesKey,
                                   final RevokeArgs revokeArgs) {
    final var keys = revokeKeys(
      invokedTokenMetadataProgramMeta,
      delegateRecordKey,
      delegateKey,
      metadataKey,
      masterEditionKey,
      tokenRecordKey,
      mintKey,
      tokenKey,
      authorityKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return revoke(invokedTokenMetadataProgramMeta, keys, revokeArgs);
  }

  public static Instruction revoke(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final List<AccountMeta> keys,
                                   final RevokeArgs revokeArgs) {
    final byte[] _data = new byte[8 + revokeArgs.l()];
    int i = REVOKE_DISCRIMINATOR.write(_data, 0);
    revokeArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record RevokeIxData(Discriminator discriminator, RevokeArgs revokeArgs) implements SerDe {  

    public static RevokeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int REVOKE_ARGS_OFFSET = 8;

    public static RevokeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var revokeArgs = RevokeArgs.read(_data, i);
      return new RevokeIxData(discriminator, revokeArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += revokeArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LOCK_DISCRIMINATOR = toDiscriminator(21, 19, 208, 43, 237, 62, 255, 87);

  /// @param authorityKey Delegate or freeze authority
  /// @param tokenOwnerKey Token owner account
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param tokenRecordKey Token record account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey System program
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> lockKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                           final PublicKey authorityKey,
                                           final PublicKey tokenOwnerKey,
                                           final PublicKey tokenKey,
                                           final PublicKey mintKey,
                                           final PublicKey metadataKey,
                                           final PublicKey editionKey,
                                           final PublicKey tokenRecordKey,
                                           final PublicKey payerKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey sysvarInstructionsKey,
                                           final PublicKey splTokenProgramKey,
                                           final PublicKey authorizationRulesProgramKey,
                                           final PublicKey authorizationRulesKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(tokenOwnerKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(tokenKey),
      createRead(mintKey),
      createWrite(metadataKey),
      createRead(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(splTokenProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param authorityKey Delegate or freeze authority
  /// @param tokenOwnerKey Token owner account
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param tokenRecordKey Token record account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey System program
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction lock(final AccountMeta invokedTokenMetadataProgramMeta,
                                 final PublicKey authorityKey,
                                 final PublicKey tokenOwnerKey,
                                 final PublicKey tokenKey,
                                 final PublicKey mintKey,
                                 final PublicKey metadataKey,
                                 final PublicKey editionKey,
                                 final PublicKey tokenRecordKey,
                                 final PublicKey payerKey,
                                 final PublicKey systemProgramKey,
                                 final PublicKey sysvarInstructionsKey,
                                 final PublicKey splTokenProgramKey,
                                 final PublicKey authorizationRulesProgramKey,
                                 final PublicKey authorizationRulesKey,
                                 final LockArgs lockArgs) {
    final var keys = lockKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      tokenOwnerKey,
      tokenKey,
      mintKey,
      metadataKey,
      editionKey,
      tokenRecordKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return lock(invokedTokenMetadataProgramMeta, keys, lockArgs);
  }

  public static Instruction lock(final AccountMeta invokedTokenMetadataProgramMeta,
                                 final List<AccountMeta> keys,
                                 final LockArgs lockArgs) {
    final byte[] _data = new byte[8 + lockArgs.l()];
    int i = LOCK_DISCRIMINATOR.write(_data, 0);
    lockArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record LockIxData(Discriminator discriminator, LockArgs lockArgs) implements SerDe {  

    public static LockIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int LOCK_ARGS_OFFSET = 8;

    public static LockIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lockArgs = LockArgs.read(_data, i);
      return new LockIxData(discriminator, lockArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += lockArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + lockArgs.l();
    }
  }

  public static final Discriminator UNLOCK_DISCRIMINATOR = toDiscriminator(101, 155, 40, 21, 158, 189, 56, 203);

  /// @param authorityKey Delegate or freeze authority
  /// @param tokenOwnerKey Token owner account
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param tokenRecordKey Token record account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey System program
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> unlockKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey authorityKey,
                                             final PublicKey tokenOwnerKey,
                                             final PublicKey tokenKey,
                                             final PublicKey mintKey,
                                             final PublicKey metadataKey,
                                             final PublicKey editionKey,
                                             final PublicKey tokenRecordKey,
                                             final PublicKey payerKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey sysvarInstructionsKey,
                                             final PublicKey splTokenProgramKey,
                                             final PublicKey authorizationRulesProgramKey,
                                             final PublicKey authorizationRulesKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(tokenOwnerKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(tokenKey),
      createRead(mintKey),
      createWrite(metadataKey),
      createRead(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(splTokenProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param authorityKey Delegate or freeze authority
  /// @param tokenOwnerKey Token owner account
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param tokenRecordKey Token record account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey System program
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction unlock(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final PublicKey authorityKey,
                                   final PublicKey tokenOwnerKey,
                                   final PublicKey tokenKey,
                                   final PublicKey mintKey,
                                   final PublicKey metadataKey,
                                   final PublicKey editionKey,
                                   final PublicKey tokenRecordKey,
                                   final PublicKey payerKey,
                                   final PublicKey systemProgramKey,
                                   final PublicKey sysvarInstructionsKey,
                                   final PublicKey splTokenProgramKey,
                                   final PublicKey authorizationRulesProgramKey,
                                   final PublicKey authorizationRulesKey,
                                   final UnlockArgs unlockArgs) {
    final var keys = unlockKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      tokenOwnerKey,
      tokenKey,
      mintKey,
      metadataKey,
      editionKey,
      tokenRecordKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return unlock(invokedTokenMetadataProgramMeta, keys, unlockArgs);
  }

  public static Instruction unlock(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final List<AccountMeta> keys,
                                   final UnlockArgs unlockArgs) {
    final byte[] _data = new byte[8 + unlockArgs.l()];
    int i = UNLOCK_DISCRIMINATOR.write(_data, 0);
    unlockArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record UnlockIxData(Discriminator discriminator, UnlockArgs unlockArgs) implements SerDe {  

    public static UnlockIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int UNLOCK_ARGS_OFFSET = 8;

    public static UnlockIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var unlockArgs = UnlockArgs.read(_data, i);
      return new UnlockIxData(discriminator, unlockArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += unlockArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + unlockArgs.l();
    }
  }

  public static final Discriminator MIGRATE_DISCRIMINATOR = toDiscriminator(155, 234, 231, 146, 236, 158, 162, 30);

  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param tokenKey Token account
  /// @param tokenOwnerKey Token account owner
  /// @param mintKey Mint account
  /// @param payerKey Payer
  /// @param authorityKey Update authority
  /// @param collectionMetadataKey Collection metadata account
  /// @param delegateRecordKey Delegate record account
  /// @param tokenRecordKey Token record account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instruction sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> migrateKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                              final PublicKey metadataKey,
                                              final PublicKey editionKey,
                                              final PublicKey tokenKey,
                                              final PublicKey tokenOwnerKey,
                                              final PublicKey mintKey,
                                              final PublicKey payerKey,
                                              final PublicKey authorityKey,
                                              final PublicKey collectionMetadataKey,
                                              final PublicKey delegateRecordKey,
                                              final PublicKey tokenRecordKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey sysvarInstructionsKey,
                                              final PublicKey splTokenProgramKey,
                                              final PublicKey authorizationRulesProgramKey,
                                              final PublicKey authorizationRulesKey) {
    return List.of(
      createWrite(metadataKey),
      createWrite(editionKey),
      createWrite(tokenKey),
      createRead(tokenOwnerKey),
      createRead(mintKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(collectionMetadataKey),
      createRead(delegateRecordKey),
      createWrite(tokenRecordKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(splTokenProgramKey),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param tokenKey Token account
  /// @param tokenOwnerKey Token account owner
  /// @param mintKey Mint account
  /// @param payerKey Payer
  /// @param authorityKey Update authority
  /// @param collectionMetadataKey Collection metadata account
  /// @param delegateRecordKey Delegate record account
  /// @param tokenRecordKey Token record account
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instruction sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction migrate(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final PublicKey metadataKey,
                                    final PublicKey editionKey,
                                    final PublicKey tokenKey,
                                    final PublicKey tokenOwnerKey,
                                    final PublicKey mintKey,
                                    final PublicKey payerKey,
                                    final PublicKey authorityKey,
                                    final PublicKey collectionMetadataKey,
                                    final PublicKey delegateRecordKey,
                                    final PublicKey tokenRecordKey,
                                    final PublicKey systemProgramKey,
                                    final PublicKey sysvarInstructionsKey,
                                    final PublicKey splTokenProgramKey,
                                    final PublicKey authorizationRulesProgramKey,
                                    final PublicKey authorizationRulesKey) {
    final var keys = migrateKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      editionKey,
      tokenKey,
      tokenOwnerKey,
      mintKey,
      payerKey,
      authorityKey,
      collectionMetadataKey,
      delegateRecordKey,
      tokenRecordKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return migrate(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction migrate(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, MIGRATE_DISCRIMINATOR);
  }

  public static final Discriminator TRANSFER_DISCRIMINATOR = toDiscriminator(163, 52, 200, 231, 140, 3, 69, 186);

  /// @param tokenKey Token account
  /// @param tokenOwnerKey Token account owner
  /// @param destinationKey Destination token account
  /// @param destinationOwnerKey Destination token account owner
  /// @param mintKey Mint of token asset
  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param editionKey Edition of token asset
  /// @param ownerTokenRecordKey Owner token record account
  /// @param destinationTokenRecordKey Destination token record account
  /// @param authorityKey Transfer authority (token owner or delegate)
  /// @param payerKey Payer
  /// @param systemProgramKey System Program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param splAtaProgramKey SPL Associated Token Account program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> transferKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final PublicKey tokenKey,
                                               final PublicKey tokenOwnerKey,
                                               final PublicKey destinationKey,
                                               final PublicKey destinationOwnerKey,
                                               final PublicKey mintKey,
                                               final PublicKey metadataKey,
                                               final PublicKey editionKey,
                                               final PublicKey ownerTokenRecordKey,
                                               final PublicKey destinationTokenRecordKey,
                                               final PublicKey authorityKey,
                                               final PublicKey payerKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey sysvarInstructionsKey,
                                               final PublicKey splTokenProgramKey,
                                               final PublicKey splAtaProgramKey,
                                               final PublicKey authorizationRulesProgramKey,
                                               final PublicKey authorizationRulesKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(tokenOwnerKey),
      createWrite(destinationKey),
      createRead(destinationOwnerKey),
      createRead(mintKey),
      createWrite(metadataKey),
      createRead(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(ownerTokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(destinationTokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(splTokenProgramKey),
      createRead(splAtaProgramKey),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param tokenKey Token account
  /// @param tokenOwnerKey Token account owner
  /// @param destinationKey Destination token account
  /// @param destinationOwnerKey Destination token account owner
  /// @param mintKey Mint of token asset
  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param editionKey Edition of token asset
  /// @param ownerTokenRecordKey Owner token record account
  /// @param destinationTokenRecordKey Destination token record account
  /// @param authorityKey Transfer authority (token owner or delegate)
  /// @param payerKey Payer
  /// @param systemProgramKey System Program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param splTokenProgramKey SPL Token Program
  /// @param splAtaProgramKey SPL Associated Token Account program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction transfer(final AccountMeta invokedTokenMetadataProgramMeta,
                                     final PublicKey tokenKey,
                                     final PublicKey tokenOwnerKey,
                                     final PublicKey destinationKey,
                                     final PublicKey destinationOwnerKey,
                                     final PublicKey mintKey,
                                     final PublicKey metadataKey,
                                     final PublicKey editionKey,
                                     final PublicKey ownerTokenRecordKey,
                                     final PublicKey destinationTokenRecordKey,
                                     final PublicKey authorityKey,
                                     final PublicKey payerKey,
                                     final PublicKey systemProgramKey,
                                     final PublicKey sysvarInstructionsKey,
                                     final PublicKey splTokenProgramKey,
                                     final PublicKey splAtaProgramKey,
                                     final PublicKey authorizationRulesProgramKey,
                                     final PublicKey authorizationRulesKey,
                                     final TransferArgs transferArgs) {
    final var keys = transferKeys(
      invokedTokenMetadataProgramMeta,
      tokenKey,
      tokenOwnerKey,
      destinationKey,
      destinationOwnerKey,
      mintKey,
      metadataKey,
      editionKey,
      ownerTokenRecordKey,
      destinationTokenRecordKey,
      authorityKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      splAtaProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return transfer(invokedTokenMetadataProgramMeta, keys, transferArgs);
  }

  public static Instruction transfer(final AccountMeta invokedTokenMetadataProgramMeta,
                                     final List<AccountMeta> keys,
                                     final TransferArgs transferArgs) {
    final byte[] _data = new byte[8 + transferArgs.l()];
    int i = TRANSFER_DISCRIMINATOR.write(_data, 0);
    transferArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record TransferIxData(Discriminator discriminator, TransferArgs transferArgs) implements SerDe {  

    public static TransferIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int TRANSFER_ARGS_OFFSET = 8;

    public static TransferIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var transferArgs = TransferArgs.read(_data, i);
      return new TransferIxData(discriminator, transferArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += transferArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + transferArgs.l();
    }
  }

  public static final Discriminator UPDATE_DISCRIMINATOR = toDiscriminator(219, 200, 88, 176, 158, 63, 253, 127);

  /// @param authorityKey Update authority or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> updateKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey authorityKey,
                                             final PublicKey delegateRecordKey,
                                             final PublicKey tokenKey,
                                             final PublicKey mintKey,
                                             final PublicKey metadataKey,
                                             final PublicKey editionKey,
                                             final PublicKey payerKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey sysvarInstructionsKey,
                                             final PublicKey authorizationRulesProgramKey,
                                             final PublicKey authorizationRulesKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(tokenKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(mintKey),
      createWrite(metadataKey),
      createRead(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWritableSigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param authorityKey Update authority or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction update(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final PublicKey authorityKey,
                                   final PublicKey delegateRecordKey,
                                   final PublicKey tokenKey,
                                   final PublicKey mintKey,
                                   final PublicKey metadataKey,
                                   final PublicKey editionKey,
                                   final PublicKey payerKey,
                                   final PublicKey systemProgramKey,
                                   final PublicKey sysvarInstructionsKey,
                                   final PublicKey authorizationRulesProgramKey,
                                   final PublicKey authorizationRulesKey,
                                   final UpdateArgs updateArgs) {
    final var keys = updateKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      delegateRecordKey,
      tokenKey,
      mintKey,
      metadataKey,
      editionKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return update(invokedTokenMetadataProgramMeta, keys, updateArgs);
  }

  public static Instruction update(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final List<AccountMeta> keys,
                                   final UpdateArgs updateArgs) {
    final byte[] _data = new byte[8 + updateArgs.l()];
    int i = UPDATE_DISCRIMINATOR.write(_data, 0);
    updateArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record UpdateIxData(Discriminator discriminator, UpdateArgs updateArgs) implements SerDe {  

    public static UpdateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int UPDATE_ARGS_OFFSET = 8;

    public static UpdateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var updateArgs = UpdateArgs.read(_data, i);
      return new UpdateIxData(discriminator, updateArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += updateArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + updateArgs.l();
    }
  }

  public static final Discriminator USE_DISCRIMINATOR = toDiscriminator(86, 205, 116, 166, 12, 177, 252, 83);

  /// @param authorityKey Token owner or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey System program
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static List<AccountMeta> useKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                          final PublicKey authorityKey,
                                          final PublicKey delegateRecordKey,
                                          final PublicKey tokenKey,
                                          final PublicKey mintKey,
                                          final PublicKey metadataKey,
                                          final PublicKey editionKey,
                                          final PublicKey payerKey,
                                          final PublicKey systemProgramKey,
                                          final PublicKey sysvarInstructionsKey,
                                          final PublicKey splTokenProgramKey,
                                          final PublicKey authorizationRulesProgramKey,
                                          final PublicKey authorizationRulesKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createWrite(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(tokenKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(mintKey),
      createWrite(metadataKey),
      createWrite(requireNonNullElse(editionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createReadOnlySigner(payerKey),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(requireNonNullElse(splTokenProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesProgramKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(authorizationRulesKey, invokedTokenMetadataProgramMeta.publicKey()))
    );
  }

  /// @param authorityKey Token owner or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param tokenKey Token account
  /// @param mintKey Mint account
  /// @param metadataKey Metadata account
  /// @param editionKey Edition account
  /// @param payerKey Payer
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey System program
  /// @param splTokenProgramKey SPL Token Program
  /// @param authorizationRulesProgramKey Token Authorization Rules Program
  /// @param authorizationRulesKey Token Authorization Rules account
  public static Instruction use(final AccountMeta invokedTokenMetadataProgramMeta,
                                final PublicKey authorityKey,
                                final PublicKey delegateRecordKey,
                                final PublicKey tokenKey,
                                final PublicKey mintKey,
                                final PublicKey metadataKey,
                                final PublicKey editionKey,
                                final PublicKey payerKey,
                                final PublicKey systemProgramKey,
                                final PublicKey sysvarInstructionsKey,
                                final PublicKey splTokenProgramKey,
                                final PublicKey authorizationRulesProgramKey,
                                final PublicKey authorizationRulesKey,
                                final UseArgs useArgs) {
    final var keys = useKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      delegateRecordKey,
      tokenKey,
      mintKey,
      metadataKey,
      editionKey,
      payerKey,
      systemProgramKey,
      sysvarInstructionsKey,
      splTokenProgramKey,
      authorizationRulesProgramKey,
      authorizationRulesKey
    );
    return use(invokedTokenMetadataProgramMeta, keys, useArgs);
  }

  public static Instruction use(final AccountMeta invokedTokenMetadataProgramMeta,
                                final List<AccountMeta> keys,
                                final UseArgs useArgs) {
    final byte[] _data = new byte[8 + useArgs.l()];
    int i = USE_DISCRIMINATOR.write(_data, 0);
    useArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record UseIxData(Discriminator discriminator, UseArgs useArgs) implements SerDe {  

    public static UseIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int USE_ARGS_OFFSET = 8;

    public static UseIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var useArgs = UseArgs.read(_data, i);
      return new UseIxData(discriminator, useArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += useArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + useArgs.l();
    }
  }

  public static final Discriminator VERIFY_DISCRIMINATOR = toDiscriminator(133, 161, 141, 48, 120, 198, 88, 150);

  /// @param authorityKey Creator to verify, collection update authority or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param metadataKey Metadata account
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionMetadataKey Metadata Account of the Collection
  /// @param collectionMasterEditionKey Master Edition Account of the Collection Token
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  public static List<AccountMeta> verifyKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey authorityKey,
                                             final PublicKey delegateRecordKey,
                                             final PublicKey metadataKey,
                                             final PublicKey collectionMintKey,
                                             final PublicKey collectionMetadataKey,
                                             final PublicKey collectionMasterEditionKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey sysvarInstructionsKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(metadataKey),
      createRead(requireNonNullElse(collectionMintKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(collectionMetadataKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(requireNonNullElse(collectionMasterEditionKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey)
    );
  }

  /// @param authorityKey Creator to verify, collection update authority or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param metadataKey Metadata account
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionMetadataKey Metadata Account of the Collection
  /// @param collectionMasterEditionKey Master Edition Account of the Collection Token
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  public static Instruction verify(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final PublicKey authorityKey,
                                   final PublicKey delegateRecordKey,
                                   final PublicKey metadataKey,
                                   final PublicKey collectionMintKey,
                                   final PublicKey collectionMetadataKey,
                                   final PublicKey collectionMasterEditionKey,
                                   final PublicKey systemProgramKey,
                                   final PublicKey sysvarInstructionsKey,
                                   final VerificationArgs verificationArgs) {
    final var keys = verifyKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      delegateRecordKey,
      metadataKey,
      collectionMintKey,
      collectionMetadataKey,
      collectionMasterEditionKey,
      systemProgramKey,
      sysvarInstructionsKey
    );
    return verify(invokedTokenMetadataProgramMeta, keys, verificationArgs);
  }

  public static Instruction verify(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final List<AccountMeta> keys,
                                   final VerificationArgs verificationArgs) {
    final byte[] _data = new byte[8 + verificationArgs.l()];
    int i = VERIFY_DISCRIMINATOR.write(_data, 0);
    verificationArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record VerifyIxData(Discriminator discriminator, VerificationArgs verificationArgs) implements SerDe {  

    public static VerifyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int VERIFICATION_ARGS_OFFSET = 8;

    public static VerifyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var verificationArgs = VerificationArgs.read(_data, i);
      return new VerifyIxData(discriminator, verificationArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += verificationArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UNVERIFY_DISCRIMINATOR = toDiscriminator(55, 1, 25, 88, 115, 67, 20, 24);

  /// @param authorityKey Creator to verify, collection (or metadata if parent burned) update authority or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param metadataKey Metadata account
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionMetadataKey Metadata Account of the Collection
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  public static List<AccountMeta> unverifyKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                               final PublicKey authorityKey,
                                               final PublicKey delegateRecordKey,
                                               final PublicKey metadataKey,
                                               final PublicKey collectionMintKey,
                                               final PublicKey collectionMetadataKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey sysvarInstructionsKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(delegateRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(metadataKey),
      createRead(requireNonNullElse(collectionMintKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(requireNonNullElse(collectionMetadataKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(systemProgramKey),
      createRead(sysvarInstructionsKey)
    );
  }

  /// @param authorityKey Creator to verify, collection (or metadata if parent burned) update authority or delegate
  /// @param delegateRecordKey Delegate record PDA
  /// @param metadataKey Metadata account
  /// @param collectionMintKey Mint of the Collection
  /// @param collectionMetadataKey Metadata Account of the Collection
  /// @param systemProgramKey System program
  /// @param sysvarInstructionsKey Instructions sysvar account
  public static Instruction unverify(final AccountMeta invokedTokenMetadataProgramMeta,
                                     final PublicKey authorityKey,
                                     final PublicKey delegateRecordKey,
                                     final PublicKey metadataKey,
                                     final PublicKey collectionMintKey,
                                     final PublicKey collectionMetadataKey,
                                     final PublicKey systemProgramKey,
                                     final PublicKey sysvarInstructionsKey,
                                     final VerificationArgs verificationArgs) {
    final var keys = unverifyKeys(
      invokedTokenMetadataProgramMeta,
      authorityKey,
      delegateRecordKey,
      metadataKey,
      collectionMintKey,
      collectionMetadataKey,
      systemProgramKey,
      sysvarInstructionsKey
    );
    return unverify(invokedTokenMetadataProgramMeta, keys, verificationArgs);
  }

  public static Instruction unverify(final AccountMeta invokedTokenMetadataProgramMeta,
                                     final List<AccountMeta> keys,
                                     final VerificationArgs verificationArgs) {
    final byte[] _data = new byte[8 + verificationArgs.l()];
    int i = UNVERIFY_DISCRIMINATOR.write(_data, 0);
    verificationArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record UnverifyIxData(Discriminator discriminator, VerificationArgs verificationArgs) implements SerDe {  

    public static UnverifyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int VERIFICATION_ARGS_OFFSET = 8;

    public static UnverifyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var verificationArgs = VerificationArgs.read(_data, i);
      return new UnverifyIxData(discriminator, verificationArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += verificationArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator COLLECT_DISCRIMINATOR = toDiscriminator(208, 47, 194, 155, 17, 98, 82, 236);

  /// @param authorityKey Authority to collect fees
  /// @param recipientKey The account to transfer collected fees to
  public static List<AccountMeta> collectKeys(final PublicKey authorityKey,
                                              final PublicKey recipientKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(recipientKey)
    );
  }

  /// @param authorityKey Authority to collect fees
  /// @param recipientKey The account to transfer collected fees to
  public static Instruction collect(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final PublicKey authorityKey,
                                    final PublicKey recipientKey) {
    final var keys = collectKeys(
      authorityKey,
      recipientKey
    );
    return collect(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction collect(final AccountMeta invokedTokenMetadataProgramMeta,
                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, COLLECT_DISCRIMINATOR);
  }

  public static final Discriminator PRINT_DISCRIMINATOR = toDiscriminator(195, 207, 47, 76, 90, 172, 115, 105);

  /// @param editionMetadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param editionKey New Edition (pda of 'metadata', program id, mint id, 'edition')
  /// @param editionMintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param editionTokenAccountOwnerKey Owner of the token account of new token
  /// @param editionTokenAccountKey Token account of new token
  /// @param editionMintAuthorityKey Mint authority of new mint
  /// @param editionTokenRecordKey Token record account
  /// @param masterEditionKey Master Record Edition V2 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param editionMarkerPdaKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master metadata mint id, 'edition', edition_number) where edition_number is NOT the edition number you pass in args but actually edition_number = floor(edition/EDITION_MARKER_BIT_SIZE).
  /// @param payerKey payer
  /// @param masterTokenAccountOwnerKey owner of token account containing master token
  /// @param masterTokenAccountKey token account containing token from master metadata mint
  /// @param masterMetadataKey Master record metadata account
  /// @param updateAuthorityKey The update authority of the master edition.
  /// @param splTokenProgramKey Token program
  /// @param splAtaProgramKey SPL Associated Token Account program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param systemProgramKey System program
  public static List<AccountMeta> printKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                            final PublicKey editionMetadataKey,
                                            final PublicKey editionKey,
                                            final PublicKey editionMintKey,
                                            final PublicKey editionTokenAccountOwnerKey,
                                            final PublicKey editionTokenAccountKey,
                                            final PublicKey editionMintAuthorityKey,
                                            final PublicKey editionTokenRecordKey,
                                            final PublicKey masterEditionKey,
                                            final PublicKey editionMarkerPdaKey,
                                            final PublicKey payerKey,
                                            final PublicKey masterTokenAccountOwnerKey,
                                            final PublicKey masterTokenAccountKey,
                                            final PublicKey masterMetadataKey,
                                            final PublicKey updateAuthorityKey,
                                            final PublicKey splTokenProgramKey,
                                            final PublicKey splAtaProgramKey,
                                            final PublicKey sysvarInstructionsKey,
                                            final PublicKey systemProgramKey) {
    return List.of(
      createWrite(editionMetadataKey),
      createWrite(editionKey),
      createWrite(editionMintKey),
      createRead(editionTokenAccountOwnerKey),
      createWrite(editionTokenAccountKey),
      createReadOnlySigner(editionMintAuthorityKey),
      createWrite(requireNonNullElse(editionTokenRecordKey, invokedTokenMetadataProgramMeta.publicKey())),
      createWrite(masterEditionKey),
      createWrite(editionMarkerPdaKey),
      createWritableSigner(payerKey),
      masterTokenAccountOwnerKey == null ? createRead(invokedTokenMetadataProgramMeta.publicKey()) : createReadOnlySigner(masterTokenAccountOwnerKey),
      createRead(masterTokenAccountKey),
      createRead(masterMetadataKey),
      createRead(updateAuthorityKey),
      createRead(splTokenProgramKey),
      createRead(splAtaProgramKey),
      createRead(sysvarInstructionsKey),
      createRead(systemProgramKey)
    );
  }

  /// @param editionMetadataKey New Metadata key (pda of 'metadata', program id, mint id)
  /// @param editionKey New Edition (pda of 'metadata', program id, mint id, 'edition')
  /// @param editionMintKey Mint of new token - THIS WILL TRANSFER AUTHORITY AWAY FROM THIS KEY
  /// @param editionTokenAccountOwnerKey Owner of the token account of new token
  /// @param editionTokenAccountKey Token account of new token
  /// @param editionMintAuthorityKey Mint authority of new mint
  /// @param editionTokenRecordKey Token record account
  /// @param masterEditionKey Master Record Edition V2 (pda of 'metadata', program id, master metadata mint id, 'edition')
  /// @param editionMarkerPdaKey Edition pda to mark creation - will be checked for pre-existence. (pda of 'metadata', program id, master metadata mint id, 'edition', edition_number) where edition_number is NOT the edition number you pass in args but actually edition_number = floor(edition/EDITION_MARKER_BIT_SIZE).
  /// @param payerKey payer
  /// @param masterTokenAccountOwnerKey owner of token account containing master token
  /// @param masterTokenAccountKey token account containing token from master metadata mint
  /// @param masterMetadataKey Master record metadata account
  /// @param updateAuthorityKey The update authority of the master edition.
  /// @param splTokenProgramKey Token program
  /// @param splAtaProgramKey SPL Associated Token Account program
  /// @param sysvarInstructionsKey Instructions sysvar account
  /// @param systemProgramKey System program
  public static Instruction print(final AccountMeta invokedTokenMetadataProgramMeta,
                                  final PublicKey editionMetadataKey,
                                  final PublicKey editionKey,
                                  final PublicKey editionMintKey,
                                  final PublicKey editionTokenAccountOwnerKey,
                                  final PublicKey editionTokenAccountKey,
                                  final PublicKey editionMintAuthorityKey,
                                  final PublicKey editionTokenRecordKey,
                                  final PublicKey masterEditionKey,
                                  final PublicKey editionMarkerPdaKey,
                                  final PublicKey payerKey,
                                  final PublicKey masterTokenAccountOwnerKey,
                                  final PublicKey masterTokenAccountKey,
                                  final PublicKey masterMetadataKey,
                                  final PublicKey updateAuthorityKey,
                                  final PublicKey splTokenProgramKey,
                                  final PublicKey splAtaProgramKey,
                                  final PublicKey sysvarInstructionsKey,
                                  final PublicKey systemProgramKey,
                                  final PrintArgs printArgs) {
    final var keys = printKeys(
      invokedTokenMetadataProgramMeta,
      editionMetadataKey,
      editionKey,
      editionMintKey,
      editionTokenAccountOwnerKey,
      editionTokenAccountKey,
      editionMintAuthorityKey,
      editionTokenRecordKey,
      masterEditionKey,
      editionMarkerPdaKey,
      payerKey,
      masterTokenAccountOwnerKey,
      masterTokenAccountKey,
      masterMetadataKey,
      updateAuthorityKey,
      splTokenProgramKey,
      splAtaProgramKey,
      sysvarInstructionsKey,
      systemProgramKey
    );
    return print(invokedTokenMetadataProgramMeta, keys, printArgs);
  }

  public static Instruction print(final AccountMeta invokedTokenMetadataProgramMeta,
                                  final List<AccountMeta> keys,
                                  final PrintArgs printArgs) {
    final byte[] _data = new byte[8 + printArgs.l()];
    int i = PRINT_DISCRIMINATOR.write(_data, 0);
    printArgs.write(_data, i);

    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, _data);
  }

  public record PrintIxData(Discriminator discriminator, PrintArgs printArgs) implements SerDe {  

    public static PrintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PRINT_ARGS_OFFSET = 8;

    public static PrintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var printArgs = PrintArgs.read(_data, i);
      return new PrintIxData(discriminator, printArgs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += printArgs.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + printArgs.l();
    }
  }

  public static final Discriminator RESIZE_DISCRIMINATOR = toDiscriminator(74, 27, 74, 155, 56, 134, 175, 125);

  /// @param metadataKey The metadata account of the digital asset
  /// @param editionKey The master edition or edition account of the digital asset, an uninitialized account for fungible assets
  /// @param mintKey Mint of token asset
  /// @param payerKey The recipient of the excess rent and authority if the authority account is not present
  /// @param authorityKey Owner of the asset for (p)NFTs, or mint authority for fungible assets, if different from the payer
  /// @param tokenKey Token or Associated Token account
  /// @param systemProgramKey System program
  public static List<AccountMeta> resizeKeys(final AccountMeta invokedTokenMetadataProgramMeta,
                                             final PublicKey metadataKey,
                                             final PublicKey editionKey,
                                             final PublicKey mintKey,
                                             final PublicKey payerKey,
                                             final PublicKey authorityKey,
                                             final PublicKey tokenKey,
                                             final PublicKey systemProgramKey) {
    return List.of(
      createWrite(metadataKey),
      createWrite(editionKey),
      createRead(mintKey),
      payerKey == null ? createWrite(invokedTokenMetadataProgramMeta.publicKey()) : createWritableSigner(payerKey),
      authorityKey == null ? createRead(invokedTokenMetadataProgramMeta.publicKey()) : createReadOnlySigner(authorityKey),
      createRead(requireNonNullElse(tokenKey, invokedTokenMetadataProgramMeta.publicKey())),
      createRead(systemProgramKey)
    );
  }

  /// @param metadataKey The metadata account of the digital asset
  /// @param editionKey The master edition or edition account of the digital asset, an uninitialized account for fungible assets
  /// @param mintKey Mint of token asset
  /// @param payerKey The recipient of the excess rent and authority if the authority account is not present
  /// @param authorityKey Owner of the asset for (p)NFTs, or mint authority for fungible assets, if different from the payer
  /// @param tokenKey Token or Associated Token account
  /// @param systemProgramKey System program
  public static Instruction resize(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final PublicKey metadataKey,
                                   final PublicKey editionKey,
                                   final PublicKey mintKey,
                                   final PublicKey payerKey,
                                   final PublicKey authorityKey,
                                   final PublicKey tokenKey,
                                   final PublicKey systemProgramKey) {
    final var keys = resizeKeys(
      invokedTokenMetadataProgramMeta,
      metadataKey,
      editionKey,
      mintKey,
      payerKey,
      authorityKey,
      tokenKey,
      systemProgramKey
    );
    return resize(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction resize(final AccountMeta invokedTokenMetadataProgramMeta,
                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, RESIZE_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_ACCOUNTS_DISCRIMINATOR = toDiscriminator(171, 222, 94, 233, 34, 250, 202, 1);

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param editionKey Edition of the asset
  /// @param mintKey Mint of token asset
  /// @param authorityKey Authority to close ownerless accounts
  /// @param destinationKey The destination account that will receive the rent.
  public static List<AccountMeta> closeAccountsKeys(final PublicKey metadataKey,
                                                    final PublicKey editionKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey destinationKey) {
    return List.of(
      createWrite(metadataKey),
      createWrite(editionKey),
      createWrite(mintKey),
      createReadOnlySigner(authorityKey),
      createWrite(destinationKey)
    );
  }

  /// @param metadataKey Metadata (pda of 'metadata', program id, mint id)
  /// @param editionKey Edition of the asset
  /// @param mintKey Mint of token asset
  /// @param authorityKey Authority to close ownerless accounts
  /// @param destinationKey The destination account that will receive the rent.
  public static Instruction closeAccounts(final AccountMeta invokedTokenMetadataProgramMeta,
                                          final PublicKey metadataKey,
                                          final PublicKey editionKey,
                                          final PublicKey mintKey,
                                          final PublicKey authorityKey,
                                          final PublicKey destinationKey) {
    final var keys = closeAccountsKeys(
      metadataKey,
      editionKey,
      mintKey,
      authorityKey,
      destinationKey
    );
    return closeAccounts(invokedTokenMetadataProgramMeta, keys);
  }

  public static Instruction closeAccounts(final AccountMeta invokedTokenMetadataProgramMeta,
                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedTokenMetadataProgramMeta, keys, CLOSE_ACCOUNTS_DISCRIMINATOR);
  }

  private TokenMetadataProgram() {
  }
}
