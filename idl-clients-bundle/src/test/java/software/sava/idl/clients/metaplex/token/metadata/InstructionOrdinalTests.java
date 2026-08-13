package software.sava.idl.clients.metaplex.token.metadata;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.metaplex.token.metadata.gen.TokenMetadataProgram;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import software.sava.core.programs.Discriminator;

import static org.junit.jupiter.api.Assertions.*;

/// Token Metadata dispatches on a **one-byte Borsh enum tag**, not an eight-byte Anchor
/// discriminator, and every one of its 58 instructions declares that tag in the IDL as
/// `"discriminant": {"type": "u8", "value": N}`.
///
/// The generator skipped the field. `AnchorInstructionParser`'s field matcher named five fields
/// and its default arm was `ji.skip()`, so `discriminant` was discarded and the instruction then
/// looked like an Anchor instruction with no discriminator — which falls back to synthesizing
/// `sha256("global:<name>")` and taking eight bytes. Every builder in this program sent that hash
/// to a program that reads one byte. Nothing reported it, because the skip was silent; the
/// sibling *type* parser has always thrown on an unknown field.
///
/// Measured on mainnet: a real Token Metadata instruction carries eleven bytes beginning `49`,
/// and the IDL declares `discriminant 49 -> Transfer`. `MetadataInstruction` in
/// `mpl-token-metadata` is a plain Rust enum, and Borsh writes an enum tag as a single byte.
///
/// These assertions are on the generated constants rather than on a captured transaction, so the
/// regression is caught without a network call. The chain byte is what they were checked against.
final class InstructionOrdinalTests {

  /// Every discriminator this program declares, read off the generated class.
  private static List<java.lang.reflect.Field> discriminatorFields() {
    final var fields = new ArrayList<java.lang.reflect.Field>();
    for (final var field : TokenMetadataProgram.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers())
          && field.getType() == Discriminator.class
          && field.getName().endsWith("_DISCRIMINATOR")) {
        fields.add(field);
      }
    }
    return fields;
  }

  private static Discriminator value(final java.lang.reflect.Field field) {
    try {
      return (Discriminator) field.get(null);
    } catch (final IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  /// The instruction whose bytes were read off the chain.
  @Test
  void transferIsTheSingleByteTheChainCarried() {
    final var transfer = TokenMetadataProgram.TRANSFER_DISCRIMINATOR;

    assertEquals(1, transfer.length(), "Borsh writes an enum tag as one byte");
    assertEquals(49, transfer.data()[0] & 0xFF, "the ordinal a real mainnet instruction began with");
  }

  /// The first two ordinals, which pin that the value is the declared one and not an accident of
  /// ordering — `CreateMetadataAccount` is 0 and `UpdateMetadataAccount` is 1.
  @Test
  void theDeclaredOrdinalsAreTheDeclaredValues() {
    assertEquals(0, TokenMetadataProgram.CREATE_METADATA_ACCOUNT_DISCRIMINATOR.data()[0] & 0xFF);
    assertEquals(1, TokenMetadataProgram.UPDATE_METADATA_ACCOUNT_DISCRIMINATOR.data()[0] & 0xFF);
  }

  /// The whole dispatch table, by exact name and exact value, transcribed from the committed
  /// `gen/idl.json` and cross-checked against `MetadataInstruction` in `mpl-token-metadata`.
  ///
  /// The weaker forms this replaces were checking almost nothing. "At least fifty one-byte
  /// constants" passes if eight are dropped; "all widths are one" passes if two ordinals are
  /// swapped, or if 2 becomes 58. The defect was uniform across the program, so a check that
  /// cannot see a single wrong value is not evidence about the other 57.
  @Test
  void theDispatchTableMatchesTheIdlExactly() {
    final var expected = new java.util.LinkedHashMap<String, Integer>();
    expected.put("CREATE_METADATA_ACCOUNT_DISCRIMINATOR", 0);
    expected.put("UPDATE_METADATA_ACCOUNT_DISCRIMINATOR", 1);
    expected.put("DEPRECATED_CREATE_MASTER_EDITION_DISCRIMINATOR", 2);
    expected.put("DEPRECATED_MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_PRINTING_TOKEN_DISCRIMINATOR", 3);
    expected.put("UPDATE_PRIMARY_SALE_HAPPENED_VIA_TOKEN_DISCRIMINATOR", 4);
    expected.put("DEPRECATED_SET_RESERVATION_LIST_DISCRIMINATOR", 5);
    expected.put("DEPRECATED_CREATE_RESERVATION_LIST_DISCRIMINATOR", 6);
    expected.put("SIGN_METADATA_DISCRIMINATOR", 7);
    expected.put("DEPRECATED_MINT_PRINTING_TOKENS_VIA_TOKEN_DISCRIMINATOR", 8);
    expected.put("DEPRECATED_MINT_PRINTING_TOKENS_DISCRIMINATOR", 9);
    expected.put("CREATE_MASTER_EDITION_DISCRIMINATOR", 10);
    expected.put("MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_TOKEN_DISCRIMINATOR", 11);
    expected.put("CONVERT_MASTER_EDITION_V_1_TO_V_2_DISCRIMINATOR", 12);
    expected.put("MINT_NEW_EDITION_FROM_MASTER_EDITION_VIA_VAULT_PROXY_DISCRIMINATOR", 13);
    expected.put("PUFF_METADATA_DISCRIMINATOR", 14);
    expected.put("UPDATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR", 15);
    expected.put("CREATE_METADATA_ACCOUNT_V_2_DISCRIMINATOR", 16);
    expected.put("CREATE_MASTER_EDITION_V_3_DISCRIMINATOR", 17);
    expected.put("VERIFY_COLLECTION_DISCRIMINATOR", 18);
    expected.put("UTILIZE_DISCRIMINATOR", 19);
    expected.put("APPROVE_USE_AUTHORITY_DISCRIMINATOR", 20);
    expected.put("REVOKE_USE_AUTHORITY_DISCRIMINATOR", 21);
    expected.put("UNVERIFY_COLLECTION_DISCRIMINATOR", 22);
    expected.put("APPROVE_COLLECTION_AUTHORITY_DISCRIMINATOR", 23);
    expected.put("REVOKE_COLLECTION_AUTHORITY_DISCRIMINATOR", 24);
    expected.put("SET_AND_VERIFY_COLLECTION_DISCRIMINATOR", 25);
    expected.put("FREEZE_DELEGATED_ACCOUNT_DISCRIMINATOR", 26);
    expected.put("THAW_DELEGATED_ACCOUNT_DISCRIMINATOR", 27);
    expected.put("REMOVE_CREATOR_VERIFICATION_DISCRIMINATOR", 28);
    expected.put("BURN_NFT_DISCRIMINATOR", 29);
    expected.put("VERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR", 30);
    expected.put("UNVERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR", 31);
    expected.put("SET_AND_VERIFY_SIZED_COLLECTION_ITEM_DISCRIMINATOR", 32);
    expected.put("CREATE_METADATA_ACCOUNT_V_3_DISCRIMINATOR", 33);
    expected.put("SET_COLLECTION_SIZE_DISCRIMINATOR", 34);
    expected.put("SET_TOKEN_STANDARD_DISCRIMINATOR", 35);
    expected.put("BUBBLEGUM_SET_COLLECTION_SIZE_DISCRIMINATOR", 36);
    expected.put("BURN_EDITION_NFT_DISCRIMINATOR", 37);
    expected.put("CREATE_ESCROW_ACCOUNT_DISCRIMINATOR", 38);
    expected.put("CLOSE_ESCROW_ACCOUNT_DISCRIMINATOR", 39);
    expected.put("TRANSFER_OUT_OF_ESCROW_DISCRIMINATOR", 40);
    expected.put("BURN_DISCRIMINATOR", 41);
    expected.put("CREATE_DISCRIMINATOR", 42);
    expected.put("MINT_DISCRIMINATOR", 43);
    expected.put("DELEGATE_DISCRIMINATOR", 44);
    expected.put("REVOKE_DISCRIMINATOR", 45);
    expected.put("LOCK_DISCRIMINATOR", 46);
    expected.put("UNLOCK_DISCRIMINATOR", 47);
    expected.put("MIGRATE_DISCRIMINATOR", 48);
    expected.put("TRANSFER_DISCRIMINATOR", 49);
    expected.put("UPDATE_DISCRIMINATOR", 50);
    expected.put("USE_DISCRIMINATOR", 51);
    expected.put("VERIFY_DISCRIMINATOR", 52);
    expected.put("UNVERIFY_DISCRIMINATOR", 53);
    expected.put("COLLECT_DISCRIMINATOR", 54);
    expected.put("PRINT_DISCRIMINATOR", 55);
    expected.put("RESIZE_DISCRIMINATOR", 56);
    expected.put("CLOSE_ACCOUNTS_DISCRIMINATOR", 57);

    final var actual = new java.util.LinkedHashMap<String, Integer>();
    for (final var field : discriminatorFields()) {
      final var discriminator = value(field);
      assertEquals(1, discriminator.length(), field.getName() + " must be one byte");
      actual.put(field.getName(), discriminator.data()[0] & 0xFF);
    }

    assertEquals(expected, actual, "the client's dispatch table must be the program's");
    assertEquals(58, actual.size(), "every declared instruction, none dropped");
    assertEquals(
        java.util.stream.IntStream.range(0, 58).boxed().toList(),
        actual.values().stream().sorted().toList(),
        "the ordinals are exactly 0..57, contiguous and distinct"
    );
  }


  /// An optional *signer* is not an optional *account*, and conflating them moves accounts.
  ///
  /// `CreateMetadataAccountV3` declares `updateAuthority` as `isOptionalSigner` at index 4 — a
  /// required, positional account whose signer privilege the caller chooses, which
  /// mpl-token-metadata's Rust client models as `(Pubkey, bool)` and always supplies. Its only
  /// genuine optional is the trailing `rent`.
  ///
  /// The parser collapsed `isOptionalSigner` into signer + optional, so once absent optionals
  /// began being omitted rather than sentinel-substituted, a null update authority dropped the
  /// account and `systemProgram` slid into index 4 — the position the processor reads as update
  /// authority. Every account after it moved with it.
  ///
  /// This pins the resulting *layout*, not the defect: with a key supplied both the broken and the
  /// fixed generator emit the same six accounts, because the drop only happens on null — and null
  /// is not a valid argument once the account is required. The regression itself is caught in
  /// idl-src-gen by `anOptionalSignerIsPositionalAndAnOptionalAccountIsNot`.
  @Test
  void anOptionalSignerKeepsItsPosition() {
    final var key = software.sava.core.accounts.PublicKey.fromBase58Encoded(
        "11111111111111111111111111111111");

    // rent absent: the only account that may be omitted is the trailing one.
    final var keys = TokenMetadataProgram.createMetadataAccountV3Keys(
        key, key, key, key, key, key, null);

    assertEquals(6, keys.size(), "six required accounts, the trailing optional omitted");
    assertTrue(keys.get(4).signer(), "index 4 is the update authority, and it signs");
    assertFalse(keys.get(5).signer(), "index 5 is the system program");

    // ...and with rent supplied it is appended, never inserted.
    final var withRent = TokenMetadataProgram.createMetadataAccountV3Keys(
        key, key, key, key, key, key, key);
    assertEquals(7, withRent.size());
    assertTrue(withRent.get(4).signer(), "the update authority does not move when rent is present");
  }

  /// The regression itself, stated as the thing that must never come back: no instruction in this
  /// program may carry the eight-byte width an Anchor discriminator would have.
  @Test
  void noInstructionCarriesAnEightByteAnchorDiscriminator() {
    for (final var field : discriminatorFields()) {
      final var discriminator = value(field);
      assertNotEquals(8, discriminator.length(),
          field.getName() + " is eight bytes; this program dispatches on one");
    }
  }
}
