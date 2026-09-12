package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.spl.token_2022.gen.types.AccountState;
import software.sava.idl.clients.spl.token_2022.gen.types.DecryptableBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.EncryptedBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.Extension;
import software.sava.idl.clients.spl.token_2022.gen.types.Mint;
import software.sava.idl.clients.spl.token_2022.gen.types.Token;
import systems.comodal.jsoniter.JsonIterator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Differential coverage for the Token 2022 *account* decoders — [Mint] and [Token] — against a
/// reader this repository did not write.
///
/// **The oracle** is the program's own JavaScript client: `clients/js/src/generated/accounts/
/// {mint,token}.ts` in `solana-program/token-2022`, rendered by `@codama/renderers-js` from the
/// same codama IDL these Java records are generated from, plus `clients/js/src/hooked/
/// extensions.ts`, which is **hand-written** and is what actually walks the TLV region. So over
/// the base struct this compares two renderings of one document — a peer implementation, not
/// ground truth, and neither side testifies that the document matches the deployed program — while
/// over the extension list it compares the generated walk against a human's reading of the
/// program. What ties the *buffers* to chain is that 25 of them were captured off mainnet.
/// `tools/token2022-account-vectors.mjs` regenerates the fixture; its header documents the JSON.
///
/// Every other account test in this module is a round trip through one reader, so a systematic
/// misreading — a u16 TLV length read as u32, a `COption` prefix width, a padding rule, an
/// extension ordinal that has drifted — is invisible there by construction. Four claims are made
/// of every vector and every reader the fixture recorded:
///
/// 1. **accept/reject agreement** — where the JavaScript client failed, the generated reader
///    throws a [RuntimeException]; where it succeeded, the generated reader succeeds. The
///    exceptions are [#DIVERGENCES], a table asserted in its divergent direction by
///    [#referenceDecodes()] and asserted to be *exactly* the set of disagreements by
///    [#theDivergenceTableIsExactlyTheSetOfDisagreements()] — so it is a checker, not a skip list;
/// 2. **value agreement** — the decoded JSON is compared with the generated record component by
///    component, recursively, and the comparison fails on any JavaScript key with no matching
///    component and on any component the JavaScript value does not carry. The two exemptions are
///    named in [#exempt(RecordComponent, Map)] and are both structural: `_address`, which is the
///    account's own address rather than account data, and the `byte[] _name`-style shadow
///    component the generator emits beside each `String` field;
/// 3. **extension-list agreement** — the program stops its TLV walk at the first zero type word,
///    which is what the JavaScript client's hand-written walk implements. The generated reader
///    keeps going and retains an [Extension.uninitialized] element for each zero word, so the
///    comparison is against the generated array truncated at its first `uninitialized`
///    ([#visible(Extension[])]). A non-`uninitialized` element *behind* an `uninitialized` one is
///    unreachable for the program and is recorded as a divergence;
/// 4. **round trip** — for every buffer the generated reader accepts, `write` into a fresh
///    `byte[l()]` re-reads to an equal record, and the written bytes equal the input where `l()`
///    accounts for every byte. Where `l()` is shorter the difference is a zero tail too short to
///    hold a type word, pinned per vector in [#SLACK_TAILS].
final class Token2022ReferenceDecodeTests {

  private static final String DIR = "/token_2022/accounts/";
  private static final String FIXTURE = "reference-decodes.json";

  /// The fixture records one result per reader it attempted, under these keys.
  private enum Reader {

    MINT("mint"),
    TOKEN("token");

    private final String field;

    Reader(final String field) {
      this.field = field;
    }

    SerDe read(final PublicKey address, final byte[] data) {
      return this == MINT ? Mint.read(address, data) : Token.read(address, data);
    }
  }

  // ---------------------------------------------------------------------------
  // The divergence table
  // ---------------------------------------------------------------------------

  /// How a `<vector>/<reader>` pair can disagree with the oracle. Every direction is *detected* by
  /// [#classify()], so an unlisted disagreement of any of these shapes fails the build rather
  /// than being absorbed.
  private enum Direction {

    /// The generated reader decodes a buffer the JavaScript client refuses.
    JAVA_ACCEPTS_JS_REJECTS,
    /// The generated reader refuses a buffer the JavaScript client decodes.
    JAVA_REJECTS_JS_ACCEPTS,
    /// Both decode the buffer, but the generated `Extension[]` carries a non-`uninitialized`
    /// element behind an `uninitialized` one — bytes the program's walk stops short of. A
    /// *trailing* run of `uninitialized` elements is not this: that is the program's own TLV
    /// padding, which the truncation drops and which the oracle never reported either.
    EXTENSION_BEHIND_PADDING
  }

  private record Divergence(Direction direction, String reason) {
  }

  /// Every known disagreement between the generated readers and the oracle, keyed
  /// `<vector>/<reader>`.
  ///
  /// Two things are *not* here, and are worth saying so:
  ///
  /// - **A cross-reader `INVALID_CONSTANT` is an agreement.** A mint buffer handed to the token
  ///   reader, and the reverse, fails on the account-type byte at offset 165 on both sides. The
  ///   generated readers check that byte too and throw, so those pairs are ordinary agreements;
  ///   [#everyInvalidConstantRejectionIsAlsoRejectedHere()] holds them to it.
  /// - **`mint-wrong-type-byte-2` decoding as a token account is an agreement.** A mint's 82
  ///   bytes, zero-padded to 165 with the type byte set to 2, *is* a valid uninitialized token
  ///   account — nothing but that byte distinguishes the two kinds once both are extended — and
  ///   `Token.read` agrees with the JavaScript client on every field.
  ///   [#aMintUnderATokensTypeByteDecodesAsAnUninitializedTokenAccount()] pins what both produce.
  private static final Map<String, Divergence> DIVERGENCES = Map.of(
      "token-uninitialized-then-nonzero/TOKEN", new Divergence(
          Direction.EXTENSION_BEHIND_PADDING,
          """
          The TLV region is an Uninitialized type word followed by a complete ImmutableOwner \
          entry. The program's walk — and the client's hand-written one — stops at the first zero \
          type word, so it reports no extensions; the generated reader keeps walking and reports \
          [uninitialized, immutableOwner]. Truncating at the first uninitialized element \
          reconciles the two, which is the rule every other vector is compared under, but the \
          element behind the padding is bytes no reader should reach and is recorded here rather \
          than absorbed by the truncation."""));

  /// Vectors whose generated `l()` is shorter than the buffer it was read from, with the length of
  /// the tail it leaves behind. Both are an ImmutableOwner entry followed by an odd number of
  /// spare zero bytes: a type word is two bytes, so a one-byte tail cannot hold one and the walk
  /// has to stop rather than read past the end. The tail is asserted to be zero, which is what
  /// makes it slack rather than a dropped extension.
  private static final Map<String, Integer> SLACK_TAILS = Map.of(
      "token-immutable-owner-plus-one-zero-byte/TOKEN", 1,
      "token-immutable-owner-plus-three-zero-bytes/TOKEN", 1);

  /// The exception each rejection actually surfaces. The *contract* asserted everywhere is
  /// `RuntimeException` — garbage in, `RuntimeException` out, the same bar the fuzz harnesses hold
  /// these readers to — and this table is the subclass, pinned so that a rejection changing shape
  /// (a bounds read becoming a validation, or the reverse) is a visible diff rather than silence.
  ///
  /// Three classes appear, and which one a buffer earns says what stopped the reader:
  ///
  /// - `IllegalArgumentException` — a validation refused the bytes: the generated hidden-prefix
  ///   check on the account-type byte at offset 165, a `COption` presence tag that is neither 0
  ///   nor 1 (`SerDeUtil.isAbsent`, which refuses a third value because the fields after it would
  ///   be decoded at the wrong offsets), an extension whose declared length is not the one its
  ///   fields occupy, or a TLV type word naming no variant this client knows;
  /// - `IndexOutOfBoundsException` — `SerDeUtil.readLen` refused a length prefix larger than the
  ///   bytes remaining after it, before any allocation; or the buffer ended inside a `COption`
  ///   slot the reader was about to skip, which `Objects.checkFromIndexSize` reports rather than
  ///   letting a 50-byte mint decode as the 82-byte record. A buffer that ends inside the mint's
  ///   83 padding bytes is an `IllegalArgumentException` from the hidden-prefix bound instead.
  ///   These are the three truncated buffers, and the reason the contract is asserted on
  ///   `RuntimeException` rather than on a validation type.
  private static final Map<String, String> REJECTIONS = Map.ofEntries(
      Map.entry("et1Arzfg3zufiKMyNtudiM7QVWzG4F9e3ukWxiHAZMs/MINT",
          "IllegalArgumentException"),
      Map.entry("et1Arzfg3zufiKMyNtudiM7QVWzG4F9e3ukWxiHAZMs/TOKEN",
          "IllegalArgumentException"),
      Map.entry("multisig-355/MINT",
          "IllegalArgumentException"),
      Map.entry("multisig-355/TOKEN",
          "IllegalArgumentException"),
      Map.entry("mint-base-only-82/TOKEN",
          "IndexOutOfBoundsException"),
      Map.entry("mint-type-byte-no-tlv-166/TOKEN",
          "IllegalArgumentException"),
      Map.entry("token-type-byte-no-tlv-166/MINT",
          "IllegalArgumentException"),
      Map.entry("token-immutable-owner-170/MINT",
          "IllegalArgumentException"),
      Map.entry("token-immutable-owner-plus-one-zero-byte/MINT",
          "IllegalArgumentException"),
      Map.entry("token-immutable-owner-plus-two-zero-bytes/MINT",
          "IllegalArgumentException"),
      Map.entry("token-immutable-owner-plus-three-zero-bytes/MINT",
          "IllegalArgumentException"),
      Map.entry("token-immutable-owner-plus-six-zero-bytes/MINT",
          "IllegalArgumentException"),
      Map.entry("token-uninitialized-then-nonzero/MINT",
          "IllegalArgumentException"),
      Map.entry("token-unknown-extension-99-then-immutable-owner/MINT",
          "IllegalArgumentException"),
      Map.entry("token-unknown-extension-99-then-immutable-owner/TOKEN",
          "IllegalArgumentException"),
      Map.entry("mint-wrong-type-byte-2/MINT",
          "IllegalArgumentException"),
      Map.entry("token-wrong-type-byte-1/MINT",
          "IllegalArgumentException"),
      Map.entry("token-wrong-type-byte-1/TOKEN",
          "IllegalArgumentException"),
      Map.entry("mint-pre-init-metadata-pointer-234/MINT",
          "IllegalArgumentException"),
      Map.entry("mint-pre-init-metadata-pointer-234/TOKEN",
          "IllegalArgumentException"),
      Map.entry("mint-metadata-pointer-short-length-63/MINT",
          "IllegalArgumentException"),
      Map.entry("mint-metadata-pointer-short-length-63/TOKEN",
          "IllegalArgumentException"),
      Map.entry("mint-metadata-pointer-length-exceeds-remaining/MINT",
          "IndexOutOfBoundsException"),
      Map.entry("mint-metadata-pointer-length-exceeds-remaining/TOKEN",
          "IllegalArgumentException"),
      Map.entry("mint-truncated-in-padding-100/MINT",
          "IllegalArgumentException"),
      Map.entry("mint-truncated-in-padding-100/TOKEN",
          "IndexOutOfBoundsException"),
      Map.entry("mint-token-metadata-length-slack/MINT",
          "IndexOutOfBoundsException"),
      Map.entry("mint-token-metadata-length-slack/TOKEN",
          "IllegalArgumentException"));

  // ---------------------------------------------------------------------------
  // The fixture
  // ---------------------------------------------------------------------------

  private record Expected(boolean ok, Json value, String error) {
  }

  private record Vector(String name, String source, byte[] data, Map<Reader, Expected> expected) {

    /// The generated readers take the account's address alongside its data; mainnet rows carry
    /// theirs in `source`, and it is passed through so the `_address` component is exercised and
    /// so a re-read of the written bytes compares equal to the record it came from.
    PublicKey address() {
      return source.startsWith("mainnet:")
          ? PublicKey.fromBase58Encoded(source.substring("mainnet:".length()))
          : null;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private static final List<Vector> VECTORS;
  private static final int DECLARED_VECTORS;

  static {
    final var root = fields(parse(resource(FIXTURE)));
    DECLARED_VECTORS = Integer.parseInt(number(fields(root.get("provenance")).get("vectors")));
    final var vectors = items(root.get("vectors"));
    final var loaded = new ArrayList<Vector>(vectors.size());
    for (final var item : vectors) {
      final var vector = fields(item);
      final var expected = new EnumMap<Reader, Expected>(Reader.class);
      for (final var reader : Reader.values()) {
        final var result = vector.get(reader.field);
        if (result != null) {
          final var fields = fields(result);
          final boolean ok = ((JsonBool) fields.get("ok")).value();
          expected.put(reader, new Expected(ok,
              ok ? fields.get("value") : null,
              ok ? null : text(fields.get("error"))));
        }
      }
      loaded.add(new Vector(text(vector.get("name")),
          text(vector.get("source")),
          Base64.getDecoder().decode(text(vector.get("data"))),
          Map.copyOf(expected)));
    }
    VECTORS = List.copyOf(loaded);
  }

  private static byte[] resource(final String name) {
    try (var in = Token2022ReferenceDecodeTests.class.getResourceAsStream(DIR + name)) {
      assertNotNull(in, "fixture " + DIR + name + " is missing");
      return in.readAllBytes();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  // ---------------------------------------------------------------------------
  // The tests
  // ---------------------------------------------------------------------------

  @Test
  void theFixtureIsWholeAndSaysWhatItHolds() {
    assertFalse(VECTORS.isEmpty(), "the fixture lists no vectors");
    assertEquals(VECTORS.size(), DECLARED_VECTORS,
        "the fixture's provenance claims a different vector count than it carries");
    assertEquals(43, VECTORS.size(),
        "the corpus changed size: re-read the new rows and update the divergence and slack tables"
            + " before trusting this test — regenerate with tools/token2022-account-vectors.mjs");
    for (final var vector : VECTORS) {
      assertFalse(vector.expected().isEmpty(), vector.name() + " records no decoder result");
      assertTrue(vector.data().length > 0, vector.name() + " carries no data");
    }
  }

  /// One test per `<vector>/<reader>` pair, making all four claims.
  @TestFactory
  Stream<DynamicTest> referenceDecodes() {
    return VECTORS.stream().flatMap(vector -> Arrays.stream(Reader.values())
        .filter(reader -> vector.expected().containsKey(reader))
        .map(reader -> DynamicTest.dynamicTest(key(vector, reader), () -> check(vector, reader))));
  }

  private void check(final Vector vector, final Reader reader) {
    final var key = key(vector, reader);
    final var expected = vector.expected().get(reader);
    final var divergence = DIVERGENCES.get(key);
    final var outcome = run(vector, reader);

    if (!expected.ok()) {
      if (divergence == null || divergence.direction() != Direction.JAVA_ACCEPTS_JS_REJECTS) {
        assertNotNull(outcome.error(), () -> "the JavaScript client refused these bytes — "
            + expected.error() + " — and the generated reader decoded them: "
            + renderJava(outcome.decoded()));
        assertEquals(REJECTIONS.get(key), outcome.error().getClass().getSimpleName(),
            () -> key + " threw " + outcome.error());
        return;
      }
      assertAccepted(outcome, key, divergence);
      return;
    }

    if (divergence != null && divergence.direction() == Direction.JAVA_REJECTS_JS_ACCEPTS) {
      assertNotNull(outcome.error(),
          () -> key + " is listed as a rejection the oracle accepts, but the generated reader"
              + " decoded it: " + divergence.reason());
      return;
    }

    if (outcome.error() != null) {
      throw new AssertionError("the JavaScript client decoded these bytes and the generated reader"
          + " refused them", outcome.error());
    }

    final var problems = new Comparison().account(expected.value(), outcome.decoded());
    assertTrue(problems.isEmpty(), () -> key + " disagrees with the oracle:\n  "
        + String.join("\n  ", problems));

    if (divergence != null && divergence.direction() == Direction.EXTENSION_BEHIND_PADDING) {
      final var generated = extensions(outcome.decoded());
      assertNotNull(generated, () -> key + " decoded no extension array at all");
      assertTrue(shadowed(generated),
          () -> key + " is listed as carrying an extension behind the padding, and its array"
              + " carries nothing but padding behind its first uninitialized element: "
              + names(generated));
    }

    roundTrip(key, vector, reader, outcome.decoded());
  }

  private static void assertAccepted(final Outcome outcome, final String key, final Divergence why) {
    assertTrue(outcome.error() == null,
        () -> key + " is listed as accepted where the oracle refuses, but it threw "
            + outcome.error() + ": " + why.reason());
  }

  /// The table has to be a checker, so the whole set of disagreements is recomputed here and
  /// compared with it. A vector that starts disagreeing, or stops, fails this — including a
  /// renamed one, whose stale key is observed nowhere.
  @Test
  void theDivergenceTableIsExactlyTheSetOfDisagreements() {
    final var expected = new LinkedHashMap<String, Direction>();
    DIVERGENCES.forEach((key, divergence) -> expected.put(key, divergence.direction()));
    assertEquals(sorted(expected), sorted(classify()),
        "the divergence table no longer describes the generated readers");
  }

  private Map<String, Direction> classify() {
    final var observed = new LinkedHashMap<String, Direction>();
    for (final var vector : VECTORS) {
      for (final var reader : Reader.values()) {
        final var expected = vector.expected().get(reader);
        if (expected == null) {
          continue;
        }
        final var outcome = run(vector, reader);
        if (expected.ok() && outcome.error() != null) {
          observed.put(key(vector, reader), Direction.JAVA_REJECTS_JS_ACCEPTS);
        } else if (!expected.ok() && outcome.error() == null) {
          observed.put(key(vector, reader), Direction.JAVA_ACCEPTS_JS_REJECTS);
        } else if (expected.ok()) {
          final var generated = extensions(outcome.decoded());
          if (generated != null && shadowed(generated)) {
            observed.put(key(vector, reader), Direction.EXTENSION_BEHIND_PADDING);
          }
        }
      }
    }
    return observed;
  }

  /// `SOLANA_ERROR__CODECS__INVALID_CONSTANT` is what the JavaScript client raises when the
  /// account-type byte (or, for a mint, the 84-byte constant that ends in it) does not match the
  /// decoder's. The generated readers check the same byte, so every one of these is an agreement
  /// and none of them is in [#DIVERGENCES] — which is only true as long as it is measured.
  @Test
  void everyInvalidConstantRejectionIsAlsoRejectedHere() {
    final var checked = new ArrayList<String>();
    for (final var vector : VECTORS) {
      for (final var reader : Reader.values()) {
        final var expected = vector.expected().get(reader);
        if (expected == null || expected.ok() || !expected.error().contains("INVALID_CONSTANT")) {
          continue;
        }
        final var key = key(vector, reader);
        checked.add(key);
        assertThrows(RuntimeException.class, () -> reader.read(vector.address(), vector.data()),
            () -> key + ": the oracle refused the account-type constant and the generated reader"
                + " did not");
      }
    }
    assertFalse(checked.isEmpty(), "no vector exercises the account-type constant");
  }

  /// A mint's bytes, zero-padded to 165 with the account-type byte set to 2, are a well-formed
  /// *uninitialized* token account: the mint authority lands in `mint`, the padding reads as an
  /// all-zero delegate/native/close-authority tail, and byte 108 is the zero that makes `state`
  /// [AccountState#uninitialized]. Both readers say so, which is the point of the vector — it is
  /// an agreement, not a divergence, and the only thing separating the two account kinds once both
  /// are extended is that one byte.
  @Test
  void aMintUnderATokensTypeByteDecodesAsAnUninitializedTokenAccount() {
    final var vector = vector("mint-wrong-type-byte-2");
    assertTrue(vector.expected().get(Reader.TOKEN).ok(), "the oracle refused this buffer");
    assertFalse(vector.expected().get(Reader.MINT).ok(), "the oracle accepted this buffer as a mint");

    final var token = (Token) Reader.TOKEN.read(vector.address(), vector.data());
    assertEquals(AccountState.uninitialized, token.state());
    assertEquals(0L, token.amount());
    assertEquals(0L, token.delegatedAmount());
    assertEquals(OptionalLong.empty(), token.isNative());
    assertEquals(List.of("immutableOwner"), names(token.extensions()));
    // IllegalArgumentException, from the generated hidden-prefix check: a mint decoder over the
    // same bytes finds 2 at offset 165 where it requires 1.
    assertThrows(RuntimeException.class, () -> Reader.MINT.read(vector.address(), vector.data()));
  }

  /// The one divergence, in both its shapes: the oracle's walk stops at the zero type word and
  /// reports nothing; the generated reader walks past it and reports the entry behind it.
  @Test
  void theExtensionBehindThePaddingIsReachedOnlyByTheGeneratedWalk() {
    final var vector = vector("token-uninitialized-then-nonzero");
    final var expected = vector.expected().get(Reader.TOKEN);
    assertTrue(expected.ok(), "the oracle refused this buffer");
    final var reported = fields(fields(expected.value()).get("extensions"));
    assertEquals("Some", text(reported.get("__option")), "the oracle decoded no extension region");
    assertEquals(List.of(), items(reported.get("value")), "the oracle reported an extension");

    final var token = (Token) Reader.TOKEN.read(vector.address(), vector.data());
    assertEquals(List.of("uninitialized", "immutableOwner"), names(token.extensions()));
    assertEquals(0, visible(token.extensions()),
        "truncating at the first uninitialized element no longer reconciles the two");
  }

  /// The vectors whose `l()` does not account for every byte of the buffer they were read from,
  /// with the tail each leaves. Pinned as a set: a new one is a reader that stopped early, which
  /// is the same shape as a dropped extension.
  @Test
  void onlyTheOddTailVectorsLeaveSlack() {
    final var observed = new LinkedHashMap<String, Integer>();
    for (final var vector : VECTORS) {
      for (final var reader : Reader.values()) {
        final var expected = vector.expected().get(reader);
        if (expected == null || !expected.ok()) {
          continue;
        }
        final var outcome = run(vector, reader);
        if (outcome.error() != null) {
          continue;
        }
        final int length = outcome.decoded().l();
        if (length != vector.data().length) {
          observed.put(key(vector, reader), vector.data().length - length);
        }
      }
    }
    assertEquals(sorted(SLACK_TAILS), sorted(observed),
        "the set of buffers the generated readers do not account for in full has changed");
  }

  // ---------------------------------------------------------------------------
  // Round trip
  // ---------------------------------------------------------------------------

  private static void roundTrip(final String key,
                                final Vector vector,
                                final Reader reader,
                                final SerDe decoded) {
    final int length = decoded.l();
    final byte[] written = new byte[length];
    assertEquals(length, decoded.write(written, 0),
        () -> key + ": write did not fill the byte[" + length + "] l() asked for");

    final var reread = reader.read(vector.address(), written);
    assertTrue(equal(decoded, reread),
        () -> key + ": re-reading the written bytes produced a different record\n  wrote "
            + renderJava(decoded) + "\n   read " + renderJava(reread));

    final byte[] input = vector.data();
    if (length == input.length) {
      assertArrayEquals(input, written, () -> key + ": l() accounts for every byte, so the written"
          + " bytes must be the input's");
      return;
    }
    assertTrue(length < input.length,
        () -> key + ": l() claims " + length + " bytes from a " + input.length + " byte buffer");
    final int tail = input.length - length;
    assertEquals(SLACK_TAILS.get(key), tail,
        () -> key + " leaves a " + tail + " byte tail; " + SLACK_TAILS.get(key) + " was recorded");
    assertArrayEquals(Arrays.copyOf(input, length), written,
        () -> key + ": the bytes l() does account for must be the input's");
    for (int i = length; i < input.length; ++i) {
      assertEquals(0, input[i], () -> key + ": the tail past l() is not slack");
    }
  }

  /// Component-wise and array-aware, because a generated record holding a `byte[]` — every
  /// [EncryptedBalance], every `_name` shadow — has reference equality for it.
  private static boolean equal(final Object left, final Object right) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null || left.getClass() != right.getClass()) {
      return false;
    }
    if (left.getClass().isArray()) {
      final int length = Array.getLength(left);
      if (length != Array.getLength(right)) {
        return false;
      }
      for (int i = 0; i < length; ++i) {
        if (!equal(Array.get(left, i), Array.get(right, i))) {
          return false;
        }
      }
      return true;
    }
    if (left.getClass().isRecord()) {
      for (final var component : left.getClass().getRecordComponents()) {
        if (!equal(read(left, component), read(right, component))) {
          return false;
        }
      }
      return true;
    }
    if (left instanceof Map<?, ?> leftMap && right instanceof Map<?, ?> rightMap) {
      final var leftEntries = List.copyOf(leftMap.entrySet());
      final var rightEntries = List.copyOf(rightMap.entrySet());
      if (leftEntries.size() != rightEntries.size()) {
        return false;
      }
      for (int i = 0; i < leftEntries.size(); ++i) {
        if (!equal(leftEntries.get(i).getKey(), rightEntries.get(i).getKey())
            || !equal(leftEntries.get(i).getValue(), rightEntries.get(i).getValue())) {
          return false;
        }
      }
      return true;
    }
    return left.equals(right);
  }

  // ---------------------------------------------------------------------------
  // The comparator: one walk over the JSON, one normaliser per representation
  // ---------------------------------------------------------------------------

  /// Collects every disagreement with a path rather than failing at the first, so one run names
  /// the whole difference.
  private static final class Comparison {

    private final List<String> problems = new ArrayList<>();

    private void at(final String path, final String message) {
      problems.add(path + " — " + message);
    }

    List<String> account(final Json json, final SerDe decoded) {
      compareRecord(decoded.getClass().getSimpleName(), json, decoded);
      return problems;
    }

    /// Walks a record against a JSON object, insisting the two describe the same field set.
    private void compareRecord(final String path, final Json json, final Object decoded) {
      if (!(json instanceof JsonObj object)) {
        at(path, "expected a JSON object for " + decoded.getClass().getSimpleName()
            + ", found " + render(json));
        return;
      }
      final var components = new LinkedHashMap<String, RecordComponent>();
      for (final var component : decoded.getClass().getRecordComponents()) {
        components.put(component.getName(), component);
      }
      final var compared = new LinkedHashSet<String>();
      for (final var component : components.values()) {
        if (!exempt(component, components)) {
          compared.add(component.getName());
        }
      }
      for (final var field : object.fields().keySet()) {
        if (!field.equals("__kind") && !compared.contains(field)) {
          at(path + '.' + field, "the oracle decoded a field "
              + decoded.getClass().getSimpleName() + " has no component for");
        }
      }
      for (final var name : compared) {
        final var value = object.fields().get(name);
        if (value == null) {
          at(path + '.' + name, "the oracle's value does not carry this component");
          continue;
        }
        final var component = components.get(name);
        value(path + '.' + name, value, read(decoded, component), component.getType());
      }
    }

    private void value(final String path, final Json json, final Object decoded, final Class<?> type) {
      if (json instanceof JsonObj object && object.fields().containsKey("__option")) {
        option(path, object, decoded, type);
        return;
      }
      if (type == OptionalLong.class) {
        at(path, "expected an option, found " + render(json));
        return;
      }
      if (decoded == null) {
        at(path, "the generated reader decoded null where the oracle has " + render(json));
        return;
      }
      if (type == Extension[].class) {
        extensionList(path, json, (Extension[]) decoded);
      } else if (type == boolean.class) {
        flag(path, json, (Boolean) decoded);
      } else if (type == int.class) {
        // u8/u16/i16 fields: the oracle keeps them JSON numbers.
        integer(path, json, Long.toString((Integer) decoded));
      } else if (type == long.class) {
        // u64 fields: the oracle serializes every bigint as a decimal string.
        bigint(path, json, Long.toUnsignedString((Long) decoded));
      } else if (type == double.class) {
        f64(path, json, (Double) decoded);
      } else if (type == PublicKey.class) {
        string(path, json, ((PublicKey) decoded).toBase58(), "an address");
      } else if (type == String.class) {
        string(path, json, (String) decoded, "a string");
      } else if (type == byte[].class) {
        bytes(path, json, (byte[]) decoded);
      } else if (decoded instanceof EncryptedBalance balance) {
        bytes(path, json, balance.val());
      } else if (decoded instanceof DecryptableBalance balance) {
        bytes(path, json, balance.val());
      } else if (decoded instanceof AccountState state) {
        // The oracle writes an enum-valued field as its numeric ordinal.
        integer(path, json, Integer.toString(state.ordinal()));
      } else if (decoded instanceof Map<?, ?> map) {
        map(path, json, map);
      } else if (type.isRecord()) {
        compareRecord(path, json, decoded);
      } else {
        at(path, "no comparison rule for " + type.getName());
      }
    }

    /// kit's option shape. A zeroable option — `noneValue: 'zeroes'`, which is how every
    /// Token 2022 extension authority is declared — is `{"__option":"None"}` on the oracle's side
    /// and a `null` `PublicKey` on the generated one; `isNative` is a `COption<u64>` and an
    /// [OptionalLong].
    private void option(final String path, final JsonObj object, final Object decoded, final Class<?> type) {
      final var tag = object.fields().get("__option");
      if (!(tag instanceof JsonStr kind)) {
        at(path, "__option is not a string: " + render(tag));
        return;
      }
      switch (kind.value()) {
        case "None" -> {
          if (!object.fields().keySet().equals(Set.of("__option"))) {
            at(path, "a None option carries " + object.fields().keySet());
          }
          if (type == OptionalLong.class) {
            if (((OptionalLong) decoded).isPresent()) {
              at(path, "expected an empty OptionalLong, found " + decoded);
            }
          } else if (decoded != null) {
            at(path, "expected null, found " + renderJava(decoded));
          }
        }
        case "Some" -> {
          if (!object.fields().keySet().equals(Set.of("__option", "value"))) {
            at(path, "a Some option carries " + object.fields().keySet());
            return;
          }
          final var inner = object.fields().get("value");
          if (type == OptionalLong.class) {
            final var present = (OptionalLong) decoded;
            if (present.isEmpty()) {
              at(path, "expected " + render(inner) + ", found an empty OptionalLong");
            } else {
              bigint(path, inner, Long.toUnsignedString(present.getAsLong()));
            }
          } else if (decoded == null) {
            at(path, "expected " + render(inner) + ", found null");
          } else {
            value(path, inner, decoded, type);
          }
        }
        default -> at(path, "unknown option tag " + kind.value());
      }
    }

    /// The program stops at the first zero type word, so the oracle's list is compared with the
    /// generated array up to its first [Extension.uninitialized].
    private void extensionList(final String path, final Json json, final Extension[] decoded) {
      if (!(json instanceof JsonArr array)) {
        at(path, "expected an array of extensions, found " + render(json));
        return;
      }
      final int visible = visible(decoded);
      if (visible != array.items().size()) {
        at(path, "the oracle walked " + array.items().size() + " extension(s) and the generated"
            + " reader " + visible + " before its first uninitialized entry: " + names(decoded));
        return;
      }
      for (int i = 0; i < visible; ++i) {
        variant(path + '[' + i + ']', array.items().get(i), decoded[i]);
      }
    }

    /// `__kind` is UpperCamel; the generated nested record is the same name with its first letter
    /// lowered, exactly as the IDL spells the variant.
    private void variant(final String path, final Json json, final Extension decoded) {
      if (!(json instanceof JsonObj object)) {
        at(path, "expected an extension object, found " + render(json));
        return;
      }
      if (!(object.fields().get("__kind") instanceof JsonStr kind)) {
        at(path, "no __kind on " + render(json));
        return;
      }
      final var expected = Character.toLowerCase(kind.value().charAt(0)) + kind.value().substring(1);
      final var actual = decoded.getClass().getSimpleName();
      if (!expected.equals(actual)) {
        at(path, "the oracle decoded " + kind.value() + ", which names Extension." + expected
            + "; the generated reader decoded Extension." + actual);
        return;
      }
      compareRecord(path + '(' + actual + ')', object, decoded);
    }

    private void flag(final String path, final Json json, final boolean decoded) {
      if (!(json instanceof JsonBool value)) {
        at(path, "expected a boolean, found " + render(json));
      } else if (value.value() != decoded) {
        at(path, "the oracle has " + value.value() + ", the generated reader " + decoded);
      }
    }

    private void integer(final String path, final Json json, final String decoded) {
      if (!(json instanceof JsonNum value)) {
        at(path, "expected a JSON number, found " + render(json));
      } else if (!value.raw().equals(decoded)) {
        at(path, "the oracle has " + value.raw() + ", the generated reader " + decoded);
      }
    }

    private void bigint(final String path, final Json json, final String decoded) {
      if (!(json instanceof JsonStr value)) {
        at(path, "expected a bigint's decimal string, found " + render(json));
      } else if (!value.value().equals(decoded)) {
        at(path, "the oracle has " + value.value() + ", the generated reader " + decoded);
      }
    }

    /// An f64 multiplier. The generator writes a non-finite number as its JavaScript `String()`
    /// form, JSON having no literal for one.
    private void f64(final String path, final Json json, final double decoded) {
      final double expected;
      if (json instanceof JsonNum value) {
        expected = Double.parseDouble(value.raw());
      } else if (json instanceof JsonStr value) {
        expected = switch (value.value()) {
          case "Infinity" -> Double.POSITIVE_INFINITY;
          case "-Infinity" -> Double.NEGATIVE_INFINITY;
          case "NaN" -> Double.NaN;
          default -> {
            at(path, "expected an f64, found " + render(json));
            yield decoded;
          }
        };
      } else {
        at(path, "expected an f64, found " + render(json));
        return;
      }
      if (Double.compare(expected, decoded) != 0) {
        at(path, "the oracle has " + expected + ", the generated reader " + decoded);
      }
    }

    private void string(final String path, final Json json, final String decoded, final String what) {
      if (!(json instanceof JsonStr value)) {
        at(path, "expected " + what + ", found " + render(json));
      } else if (!value.value().equals(decoded)) {
        at(path, "the oracle has \"" + value.value() + "\", the generated reader \"" + decoded + '"');
      }
    }

    private void bytes(final String path, final Json json, final byte[] decoded) {
      if (!(json instanceof JsonStr value)) {
        at(path, "expected base64 bytes, found " + render(json));
        return;
      }
      final var expected = Base64.getDecoder().decode(value.value());
      if (!Arrays.equals(expected, decoded)) {
        at(path, "the oracle has " + value.value() + ", the generated reader "
            + Base64.getEncoder().encodeToString(decoded));
      }
    }

    /// A codama map is `[key, value]` pairs in wire order, and the generated reader returns a
    /// [LinkedHashMap] in that same order — `additional_metadata` is a Rust `Vec<(String, String)>`
    /// whose on-chain order is insertion order, so order is part of the value.
    private void map(final String path, final Json json, final Map<?, ?> decoded) {
      if (!(json instanceof JsonArr array)) {
        at(path, "expected an array of [key, value] pairs, found " + render(json));
        return;
      }
      final var entries = List.copyOf(decoded.entrySet());
      if (entries.size() != array.items().size()) {
        at(path, "the oracle has " + array.items().size() + " entries, the generated reader "
            + entries.size() + ' ' + decoded);
        return;
      }
      for (int i = 0; i < entries.size(); ++i) {
        if (!(array.items().get(i) instanceof JsonArr pair) || pair.items().size() != 2) {
          at(path + '[' + i + ']', "expected a [key, value] pair, found "
              + render(array.items().get(i)));
          continue;
        }
        string(path + '[' + i + "].key", pair.items().get(0), (String) entries.get(i).getKey(), "a string");
        string(path + '[' + i + "].value", pair.items().get(1), (String) entries.get(i).getValue(), "a string");
      }
    }
  }

  /// The only two components a JSON key is not required for, both structural rather than a skip:
  ///
  /// - `_address` is the account's own address, which the reader is handed rather than decoding;
  /// - a `byte[] _name` beside a `String name` is the shadow the generator emits for a string
  ///   field, holding the bytes the `String` was decoded from so `l()` and `write` can reproduce
  ///   them exactly. `tokenMetadata` carries three (`_name`, `_symbol`, `_uri`).
  ///
  /// Anything else starting with `_` is reported, so a new shadow shape is a failure rather than
  /// a silent exemption.
  private static boolean exempt(final RecordComponent component,
                                final Map<String, RecordComponent> siblings) {
    final var name = component.getName();
    if (!name.startsWith("_")) {
      return false;
    }
    if (name.equals("_address") && component.getType() == PublicKey.class) {
      return true;
    }
    final var shadowed = siblings.get(name.substring(1));
    return component.getType() == byte[].class
        && shadowed != null && shadowed.getType() == String.class;
  }

  // ---------------------------------------------------------------------------
  // Plumbing
  // ---------------------------------------------------------------------------

  private record Outcome(SerDe decoded, RuntimeException error) {
  }

  private static Outcome run(final Vector vector, final Reader reader) {
    try {
      return new Outcome(reader.read(vector.address(), vector.data()), null);
    } catch (final RuntimeException e) {
      return new Outcome(null, e);
    }
  }

  private static String key(final Vector vector, final Reader reader) {
    return vector.name() + '/' + reader;
  }

  private static Vector vector(final String name) {
    return VECTORS.stream().filter(v -> v.name().equals(name)).findFirst()
        .orElseThrow(() -> new AssertionError("the fixture has no vector named " + name));
  }

  private static Extension[] extensions(final SerDe decoded) {
    return decoded instanceof Mint mint ? mint.extensions() : ((Token) decoded).extensions();
  }

  /// How much of a generated extension array the program's walk can reach: everything before its
  /// first [Extension.uninitialized].
  private static int visible(final Extension[] extensions) {
    for (int i = 0; i < extensions.length; ++i) {
      if (extensions[i] instanceof Extension.uninitialized) {
        return i;
      }
    }
    return extensions.length;
  }

  /// Whether any non-`uninitialized` element sits behind the array's first `uninitialized` one.
  /// A trailing run of `uninitialized` elements is the program's own TLV padding and is not a
  /// disagreement — the oracle stops at the same zero word and reports nothing more either.
  private static boolean shadowed(final Extension[] extensions) {
    for (int i = visible(extensions); i < extensions.length; ++i) {
      if (!(extensions[i] instanceof Extension.uninitialized)) {
        return true;
      }
    }
    return false;
  }

  private static List<String> names(final Extension[] extensions) {
    return Arrays.stream(extensions).map(e -> e.getClass().getSimpleName()).toList();
  }

  private static Object read(final Object record, final RecordComponent component) {
    try {
      return component.getAccessor().invoke(record);
    } catch (final ReflectiveOperationException e) {
      throw new AssertionError("could not read " + component, e);
    }
  }

  private static <V> Map<String, V> sorted(final Map<String, V> map) {
    return new TreeMap<>(map);
  }

  private static String renderJava(final Object value) {
    if (value == null) {
      return "null";
    }
    if (value instanceof byte[] bytes) {
      return Base64.getEncoder().encodeToString(bytes);
    }
    if (value.getClass().isArray()) {
      final var rendered = new ArrayList<String>();
      for (int i = 0, n = Array.getLength(value); i < n; ++i) {
        rendered.add(renderJava(Array.get(value, i)));
      }
      return rendered.toString();
    }
    if (value.getClass().isRecord()) {
      return value.getClass().getSimpleName() + Arrays.stream(value.getClass().getRecordComponents())
          .map(c -> c.getName() + '=' + renderJava(read(value, c)))
          .collect(Collectors.joining(", ", "[", "]"));
    }
    return String.valueOf(value);
  }

  // ---------------------------------------------------------------------------
  // A JSON tree, because the comparison is driven by the oracle's keys
  // ---------------------------------------------------------------------------

  private sealed interface Json {
  }

  private record JsonObj(Map<String, Json> fields) implements Json {
  }

  private record JsonArr(List<Json> items) implements Json {
  }

  private record JsonStr(String value) implements Json {
  }

  /// Numbers keep their source text: whether the oracle wrote a number or a bigint's decimal
  /// string is part of what is being compared.
  private record JsonNum(String raw) implements Json {
  }

  private record JsonBool(boolean value) implements Json {
  }

  private record JsonNil() implements Json {
  }

  private static Json parse(final byte[] json) {
    return readJson(JsonIterator.parse(json));
  }

  private static Json readJson(final JsonIterator ji) {
    return switch (ji.whatIsNext()) {
      case OBJECT -> {
        final var fields = new LinkedHashMap<String, Json>();
        ji.testObject((buf, offset, len, i) -> {
          fields.put(new String(buf, offset, len), readJson(i));
          return true;
        });
        yield new JsonObj(fields);
      }
      case ARRAY -> {
        final var items = new ArrayList<Json>();
        while (ji.readArray()) {
          items.add(readJson(ji));
        }
        yield new JsonArr(items);
      }
      case STRING -> new JsonStr(ji.readString());
      case NUMBER -> new JsonNum(ji.readNumberAsString());
      case BOOLEAN -> new JsonBool(ji.readBoolean());
      case NULL -> {
        ji.skip();
        yield new JsonNil();
      }
      case INVALID -> throw new IllegalStateException("not a JSON value at " + ji.currentBuffer());
    };
  }

  private static Map<String, Json> fields(final Json json) {
    return ((JsonObj) json).fields();
  }

  private static List<Json> items(final Json json) {
    return ((JsonArr) json).items();
  }

  private static String text(final Json json) {
    return ((JsonStr) json).value();
  }

  private static String number(final Json json) {
    return ((JsonNum) json).raw();
  }

  private static String render(final Json json) {
    return switch (json) {
      case JsonObj object -> object.fields().entrySet().stream()
          .map(e -> '"' + e.getKey() + "\":" + render(e.getValue()))
          .collect(Collectors.joining(",", "{", "}"));
      case JsonArr array -> array.items().stream().map(Token2022ReferenceDecodeTests::render)
          .collect(Collectors.joining(",", "[", "]"));
      case JsonStr value -> '"' + value.value() + '"';
      case JsonNum value -> value.raw();
      case JsonBool value -> Boolean.toString(value.value());
      case JsonNil ignored -> "null";
    };
  }
}
