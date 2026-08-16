package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.token_2022.gen.Token2022Program;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.spl.token_2022.Token2022Instructions.*;

final class Token2022InstructionsTests {

  private static final AccountMeta INVOKED_PROGRAM = SolanaAccounts.MAIN_NET.invokedToken2022Program();
  private static final PublicKey METADATA_KEY = PublicKey.fromBase58Encoded("88WLQK58mbqNjaUBxYjEvhvdsWGQde4s1EqyagvEng2f");
  private static final PublicKey UPDATE_AUTHORITY = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");

  @Test
  void roundTripUpdateName() {
    final var value = "MySuperCoolToken";
    final var ix = updateTokenMetadataName(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = Token2022Instructions.UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertArrayEquals(UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.data(), parsed.discriminator());
    assertEquals(Token2022Instructions.TokenMetadataField.Name, parsed.field());
    assertNull(parsed.key());
    assertNull(parsed._key());
    assertEquals(value, parsed.value());

    final var accounts = ix.accounts();
    assertEquals(2, accounts.size());
    assertEquals(AccountMeta.createWrite(METADATA_KEY), accounts.getFirst());
    assertEquals(AccountMeta.createReadOnlySigner(UPDATE_AUTHORITY), accounts.getLast());
  }

  @Test
  void roundTripUpdateSymbol() {
    final var value = "MINE";
    final var ix = updateTokenMetadataSymbol(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertEquals(TokenMetadataField.Symbol, parsed.field());
    assertNull(parsed.key());
    assertEquals(value, parsed.value());
  }

  @Test
  void roundTripUpdateUri() {
    final var value = "https://example.com/metadata.json";
    final var ix = updateTokenMetadataUri(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertEquals(TokenMetadataField.Uri, parsed.field());
    assertNull(parsed.key());
    assertEquals(value, parsed.value());
  }

  @Test
  void roundTripUpdateCustomKey() {
    final var key = "my new field";
    final var value = "Some data for the new field!";
    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, key, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    assertEquals(TokenMetadataField.Key, parsed.field());
    assertEquals(key, parsed.key());
    assertNotNull(parsed._key());
    assertEquals(value, parsed.value());
  }

  @Test
  void roundTripWriteAndRead() {
    final var key = "custom_field";
    final var value = "custom_value";
    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, key, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    // Re-serialize via the record's write method and verify byte-for-byte equality
    final byte[] reserialized = new byte[parsed.l()];
    final int written = parsed.write(reserialized, 0);
    assertEquals(reserialized.length, written);
    assertArrayEquals(ix.data(), reserialized);
  }

  @Test
  void roundTripWriteAndReadName() {
    final var value = "This is my larger name";
    final var ix = updateTokenMetadataName(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);

    final byte[] reserialized = new byte[parsed.l()];
    final int written = parsed.write(reserialized, 0);
    assertEquals(reserialized.length, written);
    assertArrayEquals(ix.data(), reserialized);
  }

  @Test
  void onChainUpdateSymbol() {
    // devnet 5DYworNVzp7EZ8rrWqfZVtygrUMLKAh8d5E4vQGeCNRz1kbaM4GAgK5JoXKYjZic9i6wD5mokzqtwH6Yn3FyJyrY

    final var metadataKey = PublicKey.fromBase58Encoded("4iuiwEyRAmTeZkR9ttkaJtFMdSP1CMbYyUdSYmentesq");
    final var updateAuthorityKey = PublicKey.fromBase58Encoded("AF1cFeC2i6fzkvXRFGcin8SdqaM5bkjsWvezqpLGUs2D");
    final var expectedData = Base64.getDecoder().decode("3ekxLbXK3MgBAwAAAFQzMw==");

    final var ix = updateTokenMetadataSymbol(INVOKED_PROGRAM, metadataKey, updateAuthorityKey, "T33");

    assertArrayEquals(expectedData, ix.data());

    final var parsed = UpdateTokenMetadataFieldIxData.read(expectedData, 0);
    assertEquals(TokenMetadataField.Symbol, parsed.field());
    assertNull(parsed.key());
    assertEquals("T33", parsed.value());
  }

  /// The generated client as an oracle.
  ///
  /// `updateTokenMetadataField` and its `TokenMetadataField` type were excluded in
  /// `main_net_programs.json` until 2026-08-15, which is why this hand-written encoder exists at
  /// all. Now that the generator emits them, the two are independent encoders of one wire format —
  /// this one written by hand, that one derived from the IDL — so they can check each other. That
  /// is worth more than either checking itself: a builder and the reader beside it agree by
  /// construction, and this repository has already had a round trip stay green through a wrong
  /// layout for exactly that reason.
  ///
  /// They model the payload differently on purpose, so the differential is on the bytes rather
  /// than the shape. This class keeps `field` and `key` as separate components; the generated
  /// `TokenMetadataField` is a sealed interface whose `key` variant carries the string inline. A
  /// disagreement about the layout — a field ordinal, a length prefix's width or position, the
  /// order of the key and value blocks — shows up as different bytes either way.
  @Test
  void theGeneratedClientEncodesTheSameBytes() {
    final var metadataKey = PublicKey.fromBase58Encoded("4iuiwEyRAmTeZkR9ttkaJtFMdSP1CMbYyUdSYmentesq");
    final var updateAuthorityKey = PublicKey.fromBase58Encoded("AF1cFeC2i6fzkvXRFGcin8SdqaM5bkjsWvezqpLGUs2D");
    final var generatedKeys = Token2022Program.updateTokenMetadataFieldKeys(metadataKey, updateAuthorityKey);

    record Case(TokenMetadataField field, String key, String value) {
    }

    for (final var c : List.of(
        new Case(TokenMetadataField.Name, null, "Test Token"),
        new Case(TokenMetadataField.Symbol, null, "T33"),
        new Case(TokenMetadataField.Uri, null, "https://example.invalid/t.json"),
        new Case(TokenMetadataField.Symbol, null, ""),
        new Case(TokenMetadataField.Key, "stage2", "visual-check"),
        new Case(TokenMetadataField.Key, "", ""),
        new Case(TokenMetadataField.Key, "k", "\u00e9\u20ac\ud834\udd1e"))) {

      final var mine = updateTokenMetadataField(
          INVOKED_PROGRAM, metadataKey, updateAuthorityKey, c.field(), c.key(), c.value());
      final var theirs = Token2022Program.updateTokenMetadataField(
          INVOKED_PROGRAM, generatedKeys, generated(c.field(), c.key()), c.value());

      final var label = c.field() + "/" + c.key() + "/" + c.value();
      assertArrayEquals(theirs.data(), mine.data(), label);

      // And each decoder must read what the other encoded.
      final var parsedMine = UpdateTokenMetadataFieldIxData.read(theirs);
      assertEquals(c.field(), parsedMine.field(), label);
      assertEquals(c.key(), parsedMine.key(), label);
      assertEquals(c.value(), parsedMine.value(), label);

      final var parsedTheirs = Token2022Program.UpdateTokenMetadataFieldIxData.read(mine);
      assertEquals(c.field().ordinal(), parsedTheirs.field().ordinal(), label);
      assertEquals(c.value(), parsedTheirs.value(), label);
    }
  }

  /// The same agreement against bytes neither encoder produced: two real devnet instructions.
  @Test
  void theGeneratedClientDecodesTheRealPayloadsIdentically() {
    for (final var encoded : List.of(
        "3ekxLbXK3MgBAwAAAFQzMw==",
        "3ekxLbXK3MgDBgAAAHN0YWdlMgwAAAB2aXN1YWwtY2hlY2s=")) {
      final byte[] payload = Base64.getDecoder().decode(encoded);
      final var mine = UpdateTokenMetadataFieldIxData.read(payload, 0);
      final var theirs = Token2022Program.UpdateTokenMetadataFieldIxData.read(payload, 0);

      assertEquals(mine.field().ordinal(), theirs.field().ordinal(), encoded);
      assertEquals(mine.value(), theirs.value(), encoded);
      assertEquals(payload.length, mine.l(), encoded);
      assertEquals(payload.length, theirs.l(), encoded);
      assertArrayEquals(mine._value(), theirs._value(), encoded);
    }
  }

  /// The generated `TokenMetadataField` for a hand-written one. Named in a single place because
  /// the two types share a simple name and Java has no import alias.
  private static software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField generated(
      final TokenMetadataField field, final String key) {
    return switch (field) {
      case Name -> software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField.name.INSTANCE;
      case Symbol -> software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField.symbol.INSTANCE;
      case Uri -> software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField.uri.INSTANCE;
      case Key -> software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField.key.createRecord(key);
    };
  }

  /// The two real devnet payloads, decoded the way a caller actually receives them: as one
  /// instruction sliced out of a whole transaction buffer, not as a byte array that begins and
  /// ends at the payload.
  ///
  /// `TransactionSkeleton` hands back instructions that share the transaction's buffer, so
  /// `data()` is every byte of the transaction and `offset()`/`len()` bound this instruction.
  /// Until 2026-08-15 `read(Instruction)` passed `offset()` and ignored `len()`, so the two
  /// length prefixes — both attacker-written signed int32s — were checked against the end of the
  /// *transaction* rather than the end of the instruction. A hostile `UpdateField` packed ahead
  /// of another instruction returned its neighbour's bytes as a parsed metadata value and threw
  /// nothing, so an indexer or wallet would render attacker-chosen text as the token's metadata.
  @Test
  void realPayloadsDecodeTheSameEmbeddedAsStandalone() {
    for (final var encoded : List.of(
        "3ekxLbXK3MgBAwAAAFQzMw==",
        "3ekxLbXK3MgDBgAAAHN0YWdlMgwAAAB2aXN1YWwtY2hlY2s=")) {
      final byte[] payload = Base64.getDecoder().decode(encoded);
      final var standalone = UpdateTokenMetadataFieldIxData.read(payload, 0);

      // The same payload at a non-zero offset, followed by bytes belonging to whatever comes
      // next in the transaction. `createInstruction(meta, keys, data, offset, len)` is the shape
      // a parsed instruction has.
      final byte[] neighbour = "NEIGHBOURING-INSTRUCTION-DATA".getBytes(StandardCharsets.UTF_8);
      final byte[] shared = new byte[7 + payload.length + neighbour.length];
      System.arraycopy(payload, 0, shared, 7, payload.length);
      System.arraycopy(neighbour, 0, shared, 7 + payload.length, neighbour.length);
      final var embedded = UpdateTokenMetadataFieldIxData.read(
          Instruction.createInstruction(INVOKED_PROGRAM, List.of(), shared, 7, payload.length));

      assertEquals(standalone.field(), embedded.field(), encoded);
      assertEquals(standalone.key(), embedded.key(), encoded);
      assertEquals(standalone.value(), embedded.value(), encoded);
      assertArrayEquals(standalone._value(), embedded._value(), encoded);
      assertEquals(payload.length, embedded.l(), "l() must account for every payload byte");
    }
  }

  /// The same bound, on the generated decoder rather than this hand-written one.
  ///
  /// The two tests above pin `Token2022Instructions`, which is one file. This pins the shape the
  /// generator emits for every instruction it produces — since 2026-08-15 `read(Instruction)`
  /// delegates to `read(instruction.copyData(), 0)`, where it used to pass `data(), offset()` and
  /// discard `len()`. `copyData()` returns exactly this instruction's bytes, so a length prefix
  /// claiming more than the instruction holds runs off the end of the copy and is rejected by
  /// `SerDeUtil.readLen`, instead of being served out of the shared transaction buffer.
  ///
  /// `Token2022Program` stands in for the other generated clients because it is the one whose
  /// wire format this class already encodes independently: the assertion that the generated
  /// decoder returns the *same* answer embedded as standalone is only meaningful because the
  /// standalone answer is separately known to be right.
  @Test
  void theGeneratedDecoderIsBoundedByTheInstructionNotTheTransaction() {
    final byte[] payload = Base64.getDecoder().decode("3ekxLbXK3MgBAwAAAFQzMw==");
    final byte[] secret = "SECRET-NEIGHBOUR-INSTRUCTION-DATA".getBytes(StandardCharsets.UTF_8);

    // An honest instruction sliced out of a larger buffer still decodes, and decodes identically.
    final byte[] shared = new byte[7 + payload.length + secret.length];
    System.arraycopy(payload, 0, shared, 7, payload.length);
    System.arraycopy(secret, 0, shared, 7 + payload.length, secret.length);
    final var embedded = Token2022Program.UpdateTokenMetadataFieldIxData.read(
        Instruction.createInstruction(INVOKED_PROGRAM, List.of(), shared, 7, payload.length));
    final var standalone = Token2022Program.UpdateTokenMetadataFieldIxData.read(payload, 0);
    assertEquals(standalone.value(), embedded.value());
    assertEquals(standalone.field().ordinal(), embedded.field().ordinal());
    assertEquals("T33", embedded.value(), "the bound must not truncate an honest payload");

    // The same layout with the value prefix raised from 3 to 32, so it reaches into the neighbour.
    final byte[] hostile = shared.clone();
    hostile[7 + 9] = 32;
    final var ix = Instruction.createInstruction(INVOKED_PROGRAM, List.of(), hostile, 7, payload.length);
    // RuntimeException, not Throwable: it must be catchable. An oversized prefix that sized an
    // array before checking would raise OutOfMemoryError, which is an Error and would fail here.
    assertThrows(RuntimeException.class,
        () -> Token2022Program.UpdateTokenMetadataFieldIxData.read(ix));

    // Naming what must not happen: before the bound, this returned "T33SECRET-NEIGHBOUR-INSTRUCTI"
    // as a successfully parsed metadata value, with nothing thrown.
    final var leaked = Token2022Program.UpdateTokenMetadataFieldIxData.read(hostile, 7);
    assertNotNull(leaked, "the unbounded overload still reads the whole array it was handed");
    assertTrue(leaked.value().startsWith("T33SECRET"),
        "sanity: the neighbour really is reachable without the instruction bound, which is the "
            + "thing read(Instruction) now prevents");
  }

  /// Every truncation of a valid payload is rejected, and only the exact length parses.
  ///
  /// `RuntimeException` rather than a specific type on purpose. Only two shapes are worth an
  /// explicit check — a key or value length longer than the payload, which would otherwise be
  /// served silently out of the next instruction. Every other truncation already fails on the
  /// array access itself, and restating those as nicer messages would be noise. What the caller
  /// is owed is that malformed input raises something catchable, which is what this asserts.
  /// The full length parsing is the other half: a check that rejected an exact fit would be just
  /// as wrong as one that admitted an overrun.
  @Test
  void everyTruncationIsRejectedAndOnlyTheExactLengthParses() {
    for (final var encoded : List.of(
        "3ekxLbXK3MgBAwAAAFQzMw==",
        "3ekxLbXK3MgDBgAAAHN0YWdlMgwAAAB2aXN1YWwtY2hlY2s=")) {
      final byte[] payload = Base64.getDecoder().decode(encoded);
      for (int len = 1; len < payload.length; ++len) {
        final byte[] truncated = Arrays.copyOf(payload, len);
        final int at = len;
        assertThrows(RuntimeException.class,
            () -> UpdateTokenMetadataFieldIxData.read(truncated, 0),
            () -> encoded + " truncated to " + at + " of " + payload.length + " bytes parsed anyway");
      }
      assertNotNull(UpdateTokenMetadataFieldIxData.read(payload, 0), encoded);
    }
  }

  /// A zero-length value is a real instruction — it is how a field is cleared — and it is the one
  /// input that separates "this length is negative" from "this length is empty". The bounds check
  /// has to admit `need == 0` while still rejecting `need < 0`; a check that refused both would
  /// pass every other test in this class and break clearing a field on chain.
  @Test
  void anEmptyValueAndAnEmptyKeyAreAdmitted() {
    final var metadataKey = PublicKey.fromBase58Encoded("4iuiwEyRAmTeZkR9ttkaJtFMdSP1CMbYyUdSYmentesq");
    final var updateAuthorityKey = PublicKey.fromBase58Encoded("AF1cFeC2i6fzkvXRFGcin8SdqaM5bkjsWvezqpLGUs2D");

    final var cleared = updateTokenMetadataSymbol(INVOKED_PROGRAM, metadataKey, updateAuthorityKey, "");
    final var parsedCleared = UpdateTokenMetadataFieldIxData.read(cleared);
    assertEquals(TokenMetadataField.Symbol, parsedCleared.field());
    assertEquals("", parsedCleared.value());
    assertEquals(0, parsedCleared._value().length);
    assertEquals(cleared.len(), parsedCleared.l());

    final var emptyKey = updateTokenMetadataCustomField(
        INVOKED_PROGRAM, metadataKey, updateAuthorityKey, "", "");
    final var parsedEmptyKey = UpdateTokenMetadataFieldIxData.read(emptyKey);
    assertEquals(TokenMetadataField.Key, parsedEmptyKey.field());
    assertEquals("", parsedEmptyKey.key());
    assertEquals("", parsedEmptyKey.value());
    assertEquals(emptyKey.len(), parsedEmptyKey.l());
  }

  /// The key block has its own two bounds, and only the custom-key field reaches them.
  @Test
  void anOversizedKeyLengthIsRejected() {
    final byte[] payload = Base64.getDecoder().decode("3ekxLbXK3MgDBgAAAHN0YWdlMgwAAAB2aXN1YWwtY2hlY2s=");
    final byte[] oversized = Arrays.copyOf(payload, payload.length);
    oversized[9] = 99; // keyLen = 99 against a 35-byte payload
    final var thrown = assertThrows(IllegalArgumentException.class,
        () -> UpdateTokenMetadataFieldIxData.read(oversized, 0));
    assertTrue(thrown.getMessage().contains("key"), thrown.getMessage());

    final byte[] negativeKey = Arrays.copyOf(payload, payload.length);
    negativeKey[9] = (byte) 0xFF;
    negativeKey[10] = (byte) 0xFF;
    negativeKey[11] = (byte) 0xFF;
    negativeKey[12] = (byte) 0xFF;
    assertThrows(RuntimeException.class, () -> UpdateTokenMetadataFieldIxData.read(negativeKey, 0));
  }

  /// A length prefix larger than the instruction is rejected rather than served from whatever
  /// follows it in the transaction.
  @Test
  void anOversizedLengthPrefixCannotReachTheNextInstruction() {
    final byte[] payload = Base64.getDecoder().decode("3ekxLbXK3MgBAwAAAFQzMw==");
    final byte[] secret = "SECRET-NEIGHBOUR-INSTRUCTION-DATA".getBytes(StandardCharsets.UTF_8);
    final byte[] shared = new byte[payload.length + secret.length];
    System.arraycopy(payload, 0, shared, 0, payload.length);
    System.arraycopy(secret, 0, shared, payload.length, secret.length);
    shared[9] = 32; // valueLen = 32, where this instruction only carries 3 value bytes

    final var ix = Instruction.createInstruction(INVOKED_PROGRAM, List.of(), shared, 0, payload.length);
    final var thrown = assertThrows(IllegalArgumentException.class,
        () -> UpdateTokenMetadataFieldIxData.read(ix));
    assertTrue(thrown.getMessage().contains("value"), thrown.getMessage());

    // The two-argument overload has no instruction length to work from, so it bounds against the
    // array it was given: the same oversized prefix in a buffer that stops at the payload is
    // rejected rather than read past the end.
    assertThrows(IllegalArgumentException.class,
        () -> UpdateTokenMetadataFieldIxData.read(Arrays.copyOf(shared, payload.length), 0));
  }

  /// `0x7FFFFFFF` in four bytes used to allocate two gigabytes and raise `OutOfMemoryError` — an
  /// `Error`, outside anything a caller would catch. That one is worth a bounds check. A negative
  /// length is not: `new byte[-1]` already raises `NegativeArraySizeException`, which is
  /// catchable, so it is asserted as a `RuntimeException` rather than given a check of its own.
  @Test
  void ahostileLengthPrefixFailsWithoutAllocating() {
    final byte[] payload = Base64.getDecoder().decode("3ekxLbXK3MgBAwAAAFQzMw==");

    final byte[] huge = Arrays.copyOf(payload, payload.length);
    huge[9] = (byte) 0xFF;
    huge[10] = (byte) 0xFF;
    huge[11] = (byte) 0xFF;
    huge[12] = 0x7F;
    assertThrows(IllegalArgumentException.class, () -> UpdateTokenMetadataFieldIxData.read(huge, 0));

    final byte[] negative = Arrays.copyOf(payload, payload.length);
    negative[9] = (byte) 0xFF;
    negative[10] = (byte) 0xFF;
    negative[11] = (byte) 0xFF;
    negative[12] = (byte) 0xFF;
    assertThrows(RuntimeException.class, () -> UpdateTokenMetadataFieldIxData.read(negative, 0));
  }

  /// An ordinal the enum does not cover used to parse as `field == null`, which `write` then
  /// dereferenced — `read` admitted a record the type could not re-serialize.
  @Test
  void anUnknownFieldOrdinalIsRejectedRatherThanStoredAsNull() {
    final byte[] payload = Base64.getDecoder().decode("3ekxLbXK3MgBAwAAAFQzMw==");
    final byte[] unknown = Arrays.copyOf(payload, payload.length);
    unknown[8] = (byte) 0xFF;
    assertThrows(IllegalArgumentException.class, () -> UpdateTokenMetadataFieldIxData.read(unknown, 0));
  }

  @Test
  void onChainUpdateCustomKey() {
    // devnet 3GehERwU3DihQWk3FTvH5hMG7t6czg8vkBpV2BkFWdPZE3yjg3VVr26B1kLSKZd32DiCvckuugatp3dqA3G2TeUz

    final var metadataKey = PublicKey.fromBase58Encoded("4iuiwEyRAmTeZkR9ttkaJtFMdSP1CMbYyUdSYmentesq");
    final var updateAuthorityKey = PublicKey.fromBase58Encoded("AF1cFeC2i6fzkvXRFGcin8SdqaM5bkjsWvezqpLGUs2D");
    final var expectedData = Base64.getDecoder().decode("3ekxLbXK3MgDBgAAAHN0YWdlMgwAAAB2aXN1YWwtY2hlY2s=");

    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, metadataKey, updateAuthorityKey, "stage2", "visual-check");

    assertArrayEquals(expectedData, ix.data());

    final var parsed = UpdateTokenMetadataFieldIxData.read(expectedData, 0);
    assertEquals(TokenMetadataField.Key, parsed.field());
    assertEquals("stage2", parsed.key());
    assertEquals("visual-check", parsed.value());
  }

  @Test
  void readNullOrEmptyData() {
    assertNull(UpdateTokenMetadataFieldIxData.read(null, 0));
    assertNull(UpdateTokenMetadataFieldIxData.read(new byte[0], 0));
  }

  /// The [Instruction]-taking overload must honour the instruction's own data offset rather
  /// than assuming the payload starts at zero.
  @Test
  void readFromInstruction() {
    final var value = "from-instruction";
    final var ix = updateTokenMetadataName(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, value);

    final var parsed = UpdateTokenMetadataFieldIxData.read(ix);

    assertNotNull(parsed);
    assertEquals(TokenMetadataField.Name, parsed.field());
    assertNull(parsed.key());
    assertEquals(value, parsed.value());
    assertArrayEquals(UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.data(), parsed.discriminator());
  }

  /// The field ordinal is bounds checked against the enum: the last valid ordinal must still
  /// resolve, and the first invalid one must yield a null field rather than walking off the
  /// end of `VALUES`.
  @Test
  void fieldOrdinalBounds() {
    // Key is the highest ordinal and must still resolve
    final var highest = UpdateTokenMetadataFieldIxData.read(
        blobWithFieldOrdinal(TokenMetadataField.Key.ordinal(), true), 0);
    assertEquals(TokenMetadataField.Key, highest.field());

    // One past the end is rejected, not stored as a null field. It used to parse: `read` set
    // field to null and returned a record `write` could not re-serialize, dereferencing it at
    // `field.ordinal()`. Admitting a value the type cannot round-trip is worse than refusing it,
    // so since 2026-08-15 an unknown ordinal throws — still by range check, never by indexing.
    final int pastEnd = TokenMetadataField.values().length;
    assertThrows(IllegalArgumentException.class,
        () -> UpdateTokenMetadataFieldIxData.read(blobWithFieldOrdinal(pastEnd, false), 0));

    assertThrows(IllegalArgumentException.class,
        () -> UpdateTokenMetadataFieldIxData.read(blobWithFieldOrdinal(0xFF, false), 0));
  }

  /// `write` reports the number of bytes written, which must be independent of where in the
  /// buffer it started.
  @Test
  void writeReturnsLengthNotEndPosition() {
    final var ix = updateTokenMetadataCustomField(INVOKED_PROGRAM, METADATA_KEY, UPDATE_AUTHORITY, "k", "v");
    final var parsed = UpdateTokenMetadataFieldIxData.read(ix.data(), 0);
    final int len = parsed.l();

    final int offset = 9;
    final byte[] buffer = new byte[offset + len];
    assertEquals(len, parsed.write(buffer, offset));

    // the bytes land at the offset, and re-reading from there recovers the same record
    final var reparsed = UpdateTokenMetadataFieldIxData.read(buffer, offset);
    assertEquals("k", reparsed.key());
    assertEquals("v", reparsed.value());
    assertEquals(TokenMetadataField.Key, reparsed.field());
  }

  /// 8-byte discriminator, 1-byte field ordinal, optional u32-prefixed key, u32-prefixed value.
  private static byte[] blobWithFieldOrdinal(final int fieldOrdinal, final boolean withKey) {
    final byte[] key = withKey ? "k".getBytes(java.nio.charset.StandardCharsets.UTF_8) : new byte[0];
    final byte[] value = "v".getBytes(java.nio.charset.StandardCharsets.UTF_8);

    final int keyBlock = withKey ? Integer.BYTES + key.length : 0;
    final byte[] data = new byte[8 + 1 + keyBlock + Integer.BYTES + value.length];

    System.arraycopy(UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.data(), 0, data, 0, 8);
    int i = 8;
    data[i++] = (byte) fieldOrdinal;
    if (withKey) {
      software.sava.core.encoding.ByteUtil.putInt32LE(data, i, key.length);
      i += Integer.BYTES;
      System.arraycopy(key, 0, data, i, key.length);
      i += key.length;
    }
    software.sava.core.encoding.ByteUtil.putInt32LE(data, i, value.length);
    i += Integer.BYTES;
    System.arraycopy(value, 0, data, i, value.length);
    return data;
  }
}
