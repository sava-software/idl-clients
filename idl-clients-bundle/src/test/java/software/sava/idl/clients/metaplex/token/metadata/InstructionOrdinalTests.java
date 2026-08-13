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

  /// Not one instruction, all of them. The defect was uniform — the program declares no
  /// `discriminator` anywhere — so a fix that corrected only the instruction someone happened to
  /// test would leave the other 57 sending a name hash.
  @Test
  void everyInstructionDispatchesOnASingleByte() {
    final var fields = discriminatorFields();
    assertTrue(fields.size() >= 50, "expected the program's full instruction set, found " + fields.size());

    final var wrongWidth = new ArrayList<String>();
    for (final var field : fields) {
      final var discriminator = value(field);
      if (discriminator.length() != 1) {
        wrongWidth.add(field.getName() + " is " + discriminator.length() + " bytes");
      }
    }
    assertEquals(List.of(), wrongWidth,
        "every Token Metadata instruction is a one-byte Borsh enum tag");
  }

  /// The ordinals are distinct. A dispatch key that collides would route two instructions to one
  /// handler, and the eight-byte hashes this replaced could never collide by construction — so
  /// nothing previously checked it.
  @Test
  void theOrdinalsAreDistinct() {
    final var seen = new java.util.HashMap<Integer, String>();
    final var collisions = new ArrayList<String>();
    for (final var field : discriminatorFields()) {
      final int ordinal = value(field).data()[0] & 0xFF;
      final var previous = seen.putIfAbsent(ordinal, field.getName());
      if (previous != null) {
        collisions.add(ordinal + ": " + previous + " and " + field.getName());
      }
    }
    assertEquals(List.of(), collisions, "two instructions cannot share a dispatch byte");
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
