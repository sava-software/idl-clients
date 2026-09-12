package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.token.Token2022;
import software.sava.core.accounts.token.Token2022Account;
import software.sava.core.accounts.token.extensions.AccountType;
import software.sava.core.accounts.token.extensions.TokenExtension;
import software.sava.core.accounts.token.extensions.UnknownTokenExtension;
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
import java.lang.reflect.RecordComponent;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/// Conformance for the **generated** Token-2022 account decoders — `gen.types.Mint`,
/// `gen.types.Token` and the sealed `gen.types.Extension` — against real mainnet accounts
/// committed under `/token_2022/accounts/`.
///
/// The oracle is the validator's own `jsonParsed` decode of the same bytes, which is independent
/// of both this repository's generator and of sava-core. Every fixture ships as three things:
/// the raw account bytes named by the account's address, the node's parse of those same bytes as
/// `<address>.parsed.json`, and a `manifest.json` row recording the slot, the length, the account
/// kind and the TLV extension type ids in wire order. A disagreement here is therefore a
/// disagreement between two independent decoders of one byte string.
///
/// sava-core 25.11.0 — the release the Solana BOM in `gradle/sava.properties` resolves to — is a
/// **second comparator**, not the oracle. It decodes the same accounts with hand-written records
/// written from the program's Rust rather than from the IDL, so where the two agree the agreement
/// is between two independent implementations of one wire format, and where they differ in
/// *shape* the correspondence is asserted rather than skipped.
///
/// The corpus and its design are sava-core's own
/// `software.sava.core.accounts.token.Token2022MainnetConformanceTests`; this is the same
/// comparison made of the generated records.
///
/// Three representation differences run through every assertion here, and each has one
/// normaliser:
///
/// 1. **A zeroable option is thirty-two zero bytes on the wire.** An extension's authority or
///    address field carries no presence tag at all: absence *is* an all-zero value. jsonParsed
///    prints `null`, sava-core decodes [PublicKey#NONE], and the generated reader decodes
///    `null` — but only where the IDL declares the field a `zeroableOptionTypeNode`. Where it
///    declares a plain `publicKeyTypeNode` the generated record holds `PublicKey.NONE` too.
///    [#ZEROABLE_OPTIONS] and [#PLAIN_PUBLIC_KEYS] pin the split field by field and
///    [#zeroableOptionsAreExactlyTheFieldsTheIdlDeclares()] checks it against the readers.
/// 2. **A `COption` on a base state carries a four-byte tag**, so absence there really is
///    absence: the generated record holds `null`, or an empty [java.util.OptionalLong]. The node
///    *omits* an absent token-account option and prints an absent mint authority as `null`, and
///    both directions are asserted.
/// 3. **`u64` crosses JSON as a number or as a decimal string**, and either can exceed
///    [Long#MAX_VALUE] — this corpus carries a `u64::MAX` group size printed as a bare number and
///    a delegated amount above `Long.MAX_VALUE` printed as a string. Everything is compared as
///    unsigned decimal text against [Long#toUnsignedString(long)].
/// 4. **A zero type word ends the chain for everyone but the generated reader.** The program's
///    own TLV walk, the upstream JavaScript client's and sava-core's all stop at the first zero
///    word; the generated reader keeps it as an `Extension.uninitialized` element and continues,
///    which is what lets `write` reproduce an allocated-but-unwritten tail byte for byte. Every
///    comparison here therefore truncates the generated array at its first such element — see
///    [#carried] — and no account in this corpus has one.
final class Token2022AccountConformanceTests {

  private static final String CORPUS = "/token_2022/accounts/";

  private static final String PROGRAM = "TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb";

  /// The base-state lengths. A mint that never had extension space allocated is exactly
  /// `Mint::LEN` and a token account exactly `Account::LEN`, with nothing after the base state —
  /// no padding, no account-type byte, no TLV chain. `extensions()` is `null` for exactly those,
  /// and for nothing else.
  private static final int MINT_LEN = 82;
  private static final int ACCOUNT_LEN = 165;

  /// `check_min_len_and_not_multisig` — interface/src/extension/mod.rs. A multisig is never
  /// extensible, so a buffer of exactly `Multisig::LEN` cannot be told apart from an extended
  /// mint or token account, and the program refuses it rather than guess.
  private static final int MULTISIG_LEN = 355;

  /// The highest extension type the generated enum names.
  private static final int HIGHEST_KNOWN_EXTENSION = 28;

  /// Extension types with no mainnet instance in this corpus, and what was searched. Recorded
  /// rather than silently absent, so [#everyExtensionVariantIsRepresentedOrRecordedAsAbsent()]
  /// fails the moment a fixture turns one up — or stops carrying one.
  private static final Map<Integer, String> NOT_FOUND_ON_MAINNET = Map.of(
      17, "confidentialTransferFeeAmount: the account-side half of confidential transfer fees."
          + " The capture behind this corpus walked six million extended token accounts TLV by"
          + " TLV, plus every token account of every mint carrying the mint-side pairing it"
          + " requires, without finding one."
  );

  /// The node's extension names, from `account_decoder/src/parse_token_extension.rs`, mapped to
  /// the on-chain type id — which is what [Extension#ordinal()] returns. Pairing them is what
  /// lets a field comparison be written against the right generated variant.
  private static final Map<String, Integer> EXTENSION_IDS = Map.ofEntries(
      Map.entry("transferFeeConfig", 1),
      Map.entry("transferFeeAmount", 2),
      Map.entry("mintCloseAuthority", 3),
      Map.entry("confidentialTransferMint", 4),
      Map.entry("confidentialTransferAccount", 5),
      Map.entry("defaultAccountState", 6),
      Map.entry("immutableOwner", 7),
      Map.entry("memoTransfer", 8),
      Map.entry("nonTransferable", 9),
      Map.entry("interestBearingConfig", 10),
      Map.entry("cpiGuard", 11),
      Map.entry("permanentDelegate", 12),
      Map.entry("nonTransferableAccount", 13),
      Map.entry("transferHook", 14),
      Map.entry("transferHookAccount", 15),
      Map.entry("confidentialTransferFeeConfig", 16),
      Map.entry("confidentialTransferFeeAmount", 17),
      Map.entry("metadataPointer", 18),
      Map.entry("tokenMetadata", 19),
      Map.entry("groupPointer", 20),
      Map.entry("tokenGroup", 21),
      Map.entry("groupMemberPointer", 22),
      Map.entry("tokenGroupMember", 23),
      Map.entry("confidentialMintBurn", 24),
      Map.entry("scaledUiAmountConfig", 25),
      Map.entry("pausableConfig", 26),
      Map.entry("pausableAccount", 27),
      Map.entry("permissionedBurnConfig", 28)
  );

  /// The two extensions the node names differently from the IDL. The node follows the program's
  /// Rust — `ConfidentialTransferFeeConfig`, `PermissionedBurnConfig` — and the IDL drops the
  /// `Config` suffix, so the generated variant does too.
  private static final Map<String, String> VARIANT_ALIASES = Map.of(
      "confidentialTransferFeeConfig", "confidentialTransferFee",
      "permissionedBurnConfig", "permissionedBurn"
  );

  /// Generated variant simple name to its on-chain type id, which is [EXTENSION_IDS] read
  /// through [VARIANT_ALIASES], plus the zero type word the node never prints.
  private static final Map<String, Integer> VARIANT_IDS = variantIds();

  private static Map<String, Integer> variantIds() {
    final var ids = new TreeMap<String, Integer>();
    ids.put("uninitialized", 0);
    for (final var entry : EXTENSION_IDS.entrySet()) {
      ids.put(VARIANT_ALIASES.getOrDefault(entry.getKey(), entry.getKey()), entry.getValue());
    }
    return Map.copyOf(ids);
  }

  /// jsonParsed key to generated record component, keyed `<variant>.<key>`, for the four names
  /// that differ. Every other key of every other extension matches a component name exactly.
  ///
  /// - the node abbreviates the confidential pending-balance halves to `Lo`/`Hi`, the IDL spells
  ///   them out;
  /// - the node's `defaultAccountState.accountState` is the IDL's `state`;
  /// - the IDL renames the Rust field `withdraw_withheld_authority_elgamal_pubkey`, which is the
  ///   name the node prints, to `elgamalPubkey`.
  private static final Map<String, String> FIELD_ALIASES = Map.of(
      "confidentialTransferAccount.pendingBalanceLo", "pendingBalanceLow",
      "confidentialTransferAccount.pendingBalanceHi", "pendingBalanceHigh",
      "defaultAccountState.accountState", "state",
      "confidentialTransferFee.withdrawWithheldAuthorityElgamalPubkey", "elgamalPubkey"
  );

  /// Record components no jsonParsed key covers, keyed by the coordinate they sit under. The node
  /// omits nothing of substance: the only uncovered components in the whole corpus are the three
  /// raw UTF-8 arrays the generated `tokenMetadata` carries beside its decoded strings, so that
  /// `l()` can be computed without re-encoding. They are asserted against their own `String`
  /// siblings in [#assertTokenMetadataRawBytes].
  private static final Map<String, Set<String>> NOT_PRINTED =
      Map.of("tokenMetadata", Set.of("_name", "_symbol", "_uri"));

  /// The `PublicKey`-typed components the node prints **base64**, because they are ElGamal
  /// encryption keys rather than addresses. Every other component of that type is base58.
  private static final Set<String> ELGAMAL_KEYS = Set.of(
      "confidentialTransferMint.auditorElgamalPubkey",
      "confidentialTransferAccount.elgamalPubkey",
      "confidentialTransferFee.elgamalPubkey",
      "confidentialMintBurn.supplyElgamalPubkey"
  );

  /// Extension fields the IDL declares `zeroableOptionTypeNode`, so the generated reader decodes
  /// an all-zero value as `null` and writes `null` back as zeros.
  private static final Set<String> ZEROABLE_OPTIONS = Set.of(
      "confidentialTransferMint.authority",
      "confidentialTransferMint.auditorElgamalPubkey",
      "confidentialTransferFee.authority",
      "metadataPointer.authority",
      "metadataPointer.metadataAddress",
      "tokenMetadata.updateAuthority",
      "groupPointer.authority",
      "groupPointer.groupAddress",
      "tokenGroup.updateAuthority",
      "groupMemberPointer.authority",
      "groupMemberPointer.memberAddress",
      "pausableConfig.authority",
      "permissionedBurn.authority"
  );

  /// Extension fields the IDL declares a plain `publicKeyTypeNode`, so the generated reader
  /// decodes an all-zero value as [PublicKey#NONE] and can never report absence.
  ///
  /// The first eight are `MaybeNull<Address>` in the program's own Rust and the node prints them
  /// `null` when zeroed; the transfer hook's pair is the one this corpus exercises. That is the
  /// **IDL** under-declaring the field rather than the generator mis-reading it — a zeroable
  /// option and a plain public key are the same 32 bytes on the wire, so nothing decodes wrongly,
  /// but a caller has to compare those eight against `PublicKey.NONE` and the thirteen above
  /// against `null`.
  private static final Set<String> PLAIN_PUBLIC_KEYS = Set.of(
      // MaybeNull<Address> upstream, plain publicKeyTypeNode in the IDL
      "transferFeeConfig.transferFeeConfigAuthority",
      "transferFeeConfig.withdrawWithheldAuthority",
      "mintCloseAuthority.closeAuthority",
      "interestBearingConfig.rateAuthority",
      "permanentDelegate.delegate",
      "transferHook.authority",
      "transferHook.programId",
      "scaledUiAmountConfig.authority",
      // genuinely non-optional upstream: a required key, or the mint or group an extension names
      "confidentialTransferAccount.elgamalPubkey",
      "confidentialTransferFee.elgamalPubkey",
      "confidentialMintBurn.supplyElgamalPubkey",
      "tokenMetadata.mint",
      "tokenGroup.mint",
      "tokenGroupMember.mint",
      "tokenGroupMember.group"
  );

  /// Generated component to sava-core 25.11.0 accessor, keyed `<variant>.<component>`, for the
  /// nine names that release spells differently. The rest match exactly.
  private static final Map<String, String> SAVA_CORE_ALIASES = Map.ofEntries(
      Map.entry("transferFeeAmount.withheldAmount", "withHeldAmount"),
      Map.entry("memoTransfer.requireIncomingTransferMemos", "requireIncomingTransferAmount"),
      Map.entry("interestBearingConfig.initializationTimestamp", "unixTimestamp"),
      Map.entry("cpiGuard.lockCpi", "lockCPI"),
      Map.entry("confidentialTransferMint.auditorElgamalPubkey", "auditorElGamalKey"),
      Map.entry("confidentialTransferAccount.pendingBalanceLow", "pendingBalanceLo"),
      Map.entry("confidentialTransferAccount.pendingBalanceHigh", "pendingBalanceHi"),
      Map.entry("confidentialTransferFee.elgamalPubkey", "withdrawWithheldAuthorityElgamalPubkey"),
      Map.entry("confidentialMintBurn.supplyElgamalPubkey", "supplyElGamalPubKey")
  );

  /// Generated components with no sava-core counterpart: the raw UTF-8 arrays again, which
  /// sava-core does not keep because it re-encodes the strings when it sizes itself.
  private static final Set<String> NOT_IN_SAVA_CORE =
      Set.of("tokenMetadata._name", "tokenMetadata._symbol", "tokenMetadata._uri");

  // ---------------------------------------------------------------------------------------
  // the corpus
  // ---------------------------------------------------------------------------------------

  /// One captured account. `extensions` is the TLV chain read off the committed bytes when they
  /// were captured, in wire order.
  private record Fixture(String address,
                         long slot,
                         int length,
                         String kind,
                         List<Integer> extensions,
                         String sha256,
                         String note,
                         byte[] data,
                         Map<String, Object> parsed) {

    PublicKey key() {
      return PublicKey.fromBase58Encoded(address);
    }

    Map<String, Object> info() {
      return object(parsed.get("info"));
    }

    List<Object> parsedExtensions() {
      final var extensions = info().get("extensions");
      return extensions == null ? List.of() : array(extensions);
    }

    boolean multisig() {
      return kind.equals("multisig");
    }

    boolean baseLengthOnly() {
      return length == MINT_LEN || length == ACCOUNT_LEN;
    }

    @Override
    public String toString() {
      return address + " (" + kind + ", " + length + " bytes, slot " + slot + ')';
    }
  }

  private static final List<Fixture> FIXTURES = loadCorpus();

  private static List<Fixture> loadCorpus() {
    final var manifest = object(json(resource("manifest.json")));
    assertEquals(PROGRAM, string(manifest.get("program")), "the corpus must be Token-2022 accounts");
    final var accounts = array(manifest.get("accounts"));
    assertFalse(accounts.isEmpty(), "the mainnet corpus is empty");
    final var fixtures = new ArrayList<Fixture>(accounts.size());
    for (final var entry : accounts) {
      final var row = object(entry);
      final var address = string(row.get("address"));
      final var ids = new ArrayList<Integer>();
      for (final var id : array(row.get("extensions"))) {
        ids.add(Math.toIntExact(u64(id)));
      }
      fixtures.add(new Fixture(
          address,
          u64(row.get("slot")),
          Math.toIntExact(u64(row.get("length"))),
          string(row.get("kind")),
          List.copyOf(ids),
          string(row.get("sha256")),
          string(row.get("note")),
          resource(address),
          object(json(resource(address + ".parsed.json")))
      ));
    }
    return List.copyOf(fixtures);
  }

  private static byte[] resource(final String name) {
    try (final var in = Token2022AccountConformanceTests.class.getResourceAsStream(CORPUS + name)) {
      assertNotNull(in, "missing Token-2022 fixture " + CORPUS + name);
      return in.readAllBytes();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String sha256(final byte[] data) {
    try {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  // ---------------------------------------------------------------------------------------
  // 1. corpus integrity
  // ---------------------------------------------------------------------------------------

  /// The committed bytes are the fixture; the manifest row and the node's parse only *describe*
  /// them, so a silent edit to either would retune the oracle instead of failing a test.
  ///
  /// Oracle: the bytes themselves, and the node's own word for the account kind.
  @Test
  void committedBytesMatchTheirManifestRow() {
    for (final var fixture : FIXTURES) {
      assertEquals(fixture.length(), fixture.data().length, fixture + ": recorded length");
      assertEquals(fixture.sha256(), sha256(fixture.data()), fixture + ": recorded digest");
      assertTrue(fixture.slot() > 0, fixture + ": every capture records the slot it was read at");
      assertFalse(fixture.note().isBlank(),
          fixture + ": every capture says why it is in the corpus, or it cannot be re-picked");
      assertEquals(string(fixture.parsed().get("type")), fixture.kind(),
          fixture + ": the node and the manifest must agree on the account kind");
      assertEquals(Set.of("info", "type"), fixture.parsed().keySet(),
          fixture + ": the node's account parse is {info, type}");
    }
  }

  /// Coverage guard: the corpus answers "does the generated code decode real accounts", which it
  /// cannot do for a variant it does not contain. Compared against the recorded exceptions rather
  /// than against the empty set, so a variant that stops being covered fails here too.
  ///
  /// Oracle: the manifest's TLV type ids, and the node's own name table, against the sealed
  /// enum's permitted variants.
  @Test
  void everyExtensionVariantIsRepresentedOrRecordedAsAbsent() {
    final var covered = new TreeSet<Integer>();
    for (final var fixture : FIXTURES) {
      covered.addAll(fixture.extensions());
    }
    final var missing = new TreeSet<Integer>();
    for (int id = 1; id <= HIGHEST_KNOWN_EXTENSION; ++id) {
      if (!covered.contains(id)) {
        missing.add(id);
      }
    }
    assertEquals(NOT_FOUND_ON_MAINNET.keySet(), missing, "extension types with no mainnet fixture");

    final var variants = new TreeSet<String>();
    for (final var subclass : Extension.class.getPermittedSubclasses()) {
      variants.add(subclass.getSimpleName());
    }
    assertEquals(new TreeSet<>(VARIANT_IDS.keySet()), variants,
        "the node's extension names and the generated variants must be the same set");
  }

  /// A coordinate classified both ways would make [#assertZeroedPublicKey] answer whichever
  /// branch it reached first.
  @Test
  void theZeroableAndPlainTablesAreDisjoint() {
    final var both = new TreeSet<>(ZEROABLE_OPTIONS);
    both.retainAll(PLAIN_PUBLIC_KEYS);
    assertEquals(Set.of(), both, "classified as both a zeroable option and a plain public key");
  }

  // ---------------------------------------------------------------------------------------
  // 2. decode, TLV order, length, round trip
  // ---------------------------------------------------------------------------------------

  /// The decode half: the TLV chain the generated reader walks must be the one the bytes carry,
  /// in the order they carry it. Then the write half, which a synthetic round trip cannot make —
  /// the account has to come back out identical to what the chain holds, at exactly the length
  /// `l()` promised.
  ///
  /// Oracle: the manifest's TLV chain, read off the committed bytes at capture time, and the
  /// committed bytes themselves.
  ///
  /// No fixture decodes with an `Extension.uninitialized` element, which is asserted rather than
  /// assumed: it is what makes [#carried] a statement about the helper's contract instead of a
  /// filter the corpus happens to need, and it is the fact that lets every comparison here pair
  /// the two decoders position by position.
  @Test
  void generatedReadersDecodeInTlvOrderAndRoundTripByteForByte() {
    int baseLengthOnly = 0;
    for (final var fixture : FIXTURES) {
      if (fixture.multisig()) {
        continue;
      }
      final var decoded = decode(fixture.key(), fixture.data(), fixture.kind());
      assertNotNull(decoded, fixture + ": the generated reader must decode a real account");
      final var extensions = extensionsOf(decoded);

      if (fixture.baseLengthOnly()) {
        ++baseLengthOnly;
        assertNull(extensions,
            fixture + ": a base-length account has no remainder at all, so extensions() is null");
        assertEquals(List.of(), fixture.extensions(), fixture + ": and the manifest agrees");
      } else {
        assertNotNull(extensions, fixture + ": an extended account decodes its TLV chain");
        final var carried = carried(extensions);
        assertEquals(fixture.extensions(), ordinalsOf(carried),
            fixture + ": extension ids in TLV order");
        assertEquals(extensions.length, carried.size(),
            fixture + ": no fixture carries a zero type word, so nothing is truncated and no "
                + "Extension.uninitialized element is decoded");
      }

      assertEquals(fixture.length(), decoded.l(), fixture + ": serialized length");
      final byte[] written = new byte[decoded.l()];
      assertEquals(written.length, decoded.write(written, 0), fixture + ": bytes written");
      assertArrayEquals(fixture.data(), written, fixture + ": byte-exact round trip");
      // a record's own equals() compares a byte[] component by reference, so the generated
      // records cannot express this themselves — see sameValue
      assertTrue(sameValue(decoded, decode(fixture.key(), written, fixture.kind())),
          fixture + ": re-reading the written bytes yields the same record");
    }
    assertEquals(4, baseLengthOnly,
        "the corpus carries four extension-free fixtures: two mints at 82 and two accounts at 165");
  }

  // ---------------------------------------------------------------------------------------
  // 3. field conformance against jsonParsed
  // ---------------------------------------------------------------------------------------

  /// The field-by-field comparison against the node's decode of the same bytes: base state first,
  /// then every extension, paired by position because both decoders walk the TLV chain in wire
  /// order.
  ///
  /// Oracle: the validator's `jsonParsed` account decoder.
  ///
  /// Nothing is skipped in either direction. A jsonParsed key with no mapping fails, and so does
  /// a record component no key covered unless [#NOT_PRINTED] names it.
  @Test
  void decodedFieldsMatchTheNodesParse() {
    for (final var fixture : FIXTURES) {
      if (fixture.multisig()) {
        continue;
      }
      final var decoded = decode(fixture.key(), fixture.data(), fixture.kind());
      final var info = fixture.info();
      final var where = fixture + " ";
      if (decoded instanceof Mint mint) {
        assertMintState(where, info, fixture.address(), mint);
      } else {
        assertTokenState(where, info, fixture.address(), (Token) decoded);
      }

      final var parsed = fixture.parsedExtensions();
      final var extensions = extensionsOf(decoded);
      // the node's TLV walk stops at the first zero type word, so the generated array is
      // truncated there before the two are paired
      final var decodedExtensions = extensions == null ? List.<Extension>of() : carried(extensions);
      assertEquals(parsed.size(), decodedExtensions.size(),
          where + "the node and the generated reader must find the same number of extensions");
      for (int i = 0; i < parsed.size(); ++i) {
        assertExtension(where, object(parsed.get(i)), decodedExtensions.get(i));
      }
    }
  }

  /// The mint's two authorities are `COption`s on the base state: the four-byte tag decides, the
  /// generated record holds `null` when it is zero, and the node prints the member as `null`
  /// rather than omitting it.
  private static void assertMintState(final String where,
                                      final Map<String, Object> info,
                                      final String address,
                                      final Mint mint) {
    assertEquals(address, mint._address().toBase58(), where + "mint address");
    final var consumed = new TreeSet<String>();
    assertCOption(where, "mintAuthority", info, mint.mintAuthority(), consumed);
    assertEquals(u64Text(require(where, "supply", info, consumed)),
        Long.toUnsignedString(mint.supply()), where + "supply");
    assertEquals(numberText(require(where, "decimals", info, consumed)),
        Integer.toString(mint.decimals()), where + "decimals");
    assertEquals(require(where, "isInitialized", info, consumed), mint.isInitialized(),
        where + "isInitialized");
    assertCOption(where, "freezeAuthority", info, mint.freezeAuthority(), consumed);
    assertEveryKeyConsumed(where + "mint", info, consumed);
  }

  /// The token account's `delegate`, `closeAuthority` and `isNative` are `COption`s too, but the
  /// node **omits** an absent one rather than printing it null — and prints the amount that only
  /// has meaning alongside a set tag (`delegatedAmount`, `rentExemptReserve`) only when the tag
  /// is set, while the wire carries the field either way. So absence has to be asserted as a zero
  /// on this side rather than left uncompared.
  private static void assertTokenState(final String where,
                                       final Map<String, Object> info,
                                       final String address,
                                       final Token token) {
    assertEquals(address, token._address().toBase58(), where + "account address");
    final var consumed = new TreeSet<String>();
    assertEquals(string(require(where, "mint", info, consumed)), token.mint().toBase58(),
        where + "mint");
    assertEquals(string(require(where, "owner", info, consumed)), token.owner().toBase58(),
        where + "owner");

    // tokenAmount is the node scaling the raw amount by the *mint's* decimals, which this account
    // does not carry: only `amount` is a field of it
    final var tokenAmount = object(require(where, "tokenAmount", info, consumed));
    assertEquals(Set.of("amount", "decimals", "uiAmount", "uiAmountString"), tokenAmount.keySet(),
        where + "tokenAmount is the node's scaled view of one u64");
    assertEquals(u64Text(tokenAmount.get("amount")), Long.toUnsignedString(token.amount()),
        where + "tokenAmount.amount");

    assertEquals(AccountState.valueOf(string(require(where, "state", info, consumed))),
        token.state(), where + "state");

    assertCOption(where, "delegate", info, token.delegate(), consumed);
    if (info.containsKey("delegatedAmount")) {
      assertTrue(info.containsKey("delegate"), where + "a delegated amount without a delegate");
      final var delegated = object(require(where, "delegatedAmount", info, consumed));
      assertEquals(u64Text(delegated.get("amount")), Long.toUnsignedString(token.delegatedAmount()),
          where + "delegatedAmount.amount");
    } else {
      assertEquals(0L, token.delegatedAmount(),
          where + "the node omits delegatedAmount only when there is no delegate, and the wire "
              + "field is zero then");
    }

    final boolean isNative = assertInstanceOf(Boolean.class,
        require(where, "isNative", info, consumed), where + "isNative is a boolean");
    assertEquals(isNative, token.isNative().isPresent(), where + "isNative option tag");
    if (isNative) {
      final var reserve = object(require(where, "rentExemptReserve", info, consumed));
      assertEquals(u64Text(reserve.get("amount")),
          Long.toUnsignedString(token.isNative().orElseThrow()),
          where + "rentExemptReserve.amount is the is-native option's value");
    } else {
      assertFalse(info.containsKey("rentExemptReserve"),
          where + "a non-native account has no rent-exempt reserve");
    }

    assertCOption(where, "closeAuthority", info, token.closeAuthority(), consumed);
    assertEveryKeyConsumed(where + "account", info, consumed);
  }

  private static void assertExtension(final String where,
                                      final Map<String, Object> parsed,
                                      final Extension decoded) {
    final var name = string(parsed.get("extension"));
    final var id = EXTENSION_IDS.get(name);
    assertNotNull(id, where + "the node reported an extension this test cannot map: " + name);
    assertEquals(id.intValue(), decoded.ordinal(), where + "extension at this position is " + name);
    final var variant = VARIANT_ALIASES.getOrDefault(name, name);
    assertEquals(variant, decoded.getClass().getSimpleName(),
        where + name + ": the generated variant the ordinal resolved to");
    assertEquals(parsed.containsKey("state") ? Set.of("extension", "state") : Set.of("extension"),
        parsed.keySet(),
        where + name + ": a parsed extension is {extension} or {extension, state}");

    // a marker extension carries no value at all, so matching the variant is the whole comparison
    final var state = parsed.containsKey("state")
        ? object(parsed.get("state"))
        : Map.<String, Object>of();
    assertRecord(where, variant, state, decoded);
    if (decoded instanceof Extension.tokenMetadata metadata) {
      assertTokenMetadataRawBytes(where, metadata);
    }
  }

  /// Compares one jsonParsed object against one generated record, generically: every key resolves
  /// to the record component of the same name — or to the one [#FIELD_ALIASES] names — and every
  /// component is either covered by a key or listed in [#NOT_PRINTED]. Both halves fail loudly,
  /// so neither a new IDL field nor a new jsonParsed field can pass unnoticed.
  private static void assertRecord(final String where,
                                   final String coordinate,
                                   final Map<String, Object> state,
                                   final Object record) {
    final var components = new LinkedHashMap<String, RecordComponent>();
    for (final var component : record.getClass().getRecordComponents()) {
      components.put(component.getName(), component);
    }
    final var covered = new TreeSet<String>();
    for (final var entry : state.entrySet()) {
      final var key = entry.getKey();
      final var name = FIELD_ALIASES.getOrDefault(coordinate + '.' + key, key);
      final var component = components.get(name);
      assertNotNull(component, where + coordinate + ": the node printed '" + key
          + "', which maps to no record component — give it a FIELD_ALIASES entry, or the "
          + "generated record is missing a field the node can see");
      covered.add(name);
      assertValue(where, coordinate + '.' + name, entry.getValue(),
          value(component, record), component.getType());
    }
    final var uncovered = new TreeSet<>(components.keySet());
    uncovered.removeAll(covered);
    assertEquals(NOT_PRINTED.getOrDefault(coordinate, Set.of()), uncovered,
        where + coordinate + ": record components no jsonParsed key covered");
  }

  /// One normaliser per representation difference, chosen by the generated component's declared
  /// type. Anything unhandled fails rather than being compared with `equals` by accident.
  private static void assertValue(final String where,
                                  final String coordinate,
                                  final Object parsed,
                                  final Object decoded,
                                  final Class<?> type) {
    final var at = where + coordinate;
    if (type == PublicKey.class) {
      if (parsed == null) {
        assertZeroedPublicKey(at, coordinate, decoded);
      } else if (ELGAMAL_KEYS.contains(coordinate)) {
        // an ElGamal key is 32 bytes like an address, but it is a ciphertext key rather than one,
        // so the node prints it base64
        assertNotNull(decoded, at);
        assertArrayEquals(base64(parsed), ((PublicKey) decoded).toByteArray(), at);
      } else {
        assertNotNull(decoded, at);
        assertEquals(string(parsed), ((PublicKey) decoded).toBase58(), at);
        assertNotEquals(PublicKey.NONE, decoded, at + " is present, so it is not all zeros");
      }
    } else if (type == long.class) {
      assertEquals(u64Text(parsed), Long.toUnsignedString((Long) decoded), at);
    } else if (type == int.class) {
      // u8 and u16 arrive widened and unsigned; an i16 rate arrives signed and the node prints it
      // signed too, so decimal text compares correctly either way
      assertEquals(numberText(parsed), Integer.toString((Integer) decoded), at);
    } else if (type == boolean.class) {
      assertEquals(parsed, decoded, at);
    } else if (type == double.class) {
      // the wire field is an f64 and the node prints it as a decimal string, not a JSON number
      assertEquals(Double.parseDouble(decimalText(parsed)), (Double) decoded, at);
    } else if (type == String.class) {
      assertEquals(string(parsed), decoded, at);
    } else if (type == AccountState.class) {
      // the generated enum's constants are the node's own words
      assertEquals(AccountState.valueOf(string(parsed)), decoded, at);
    } else if (type == EncryptedBalance.class) {
      assertArrayEquals(base64(parsed), ((EncryptedBalance) decoded).val(), at);
    } else if (type == DecryptableBalance.class) {
      assertArrayEquals(base64(parsed), ((DecryptableBalance) decoded).val(), at);
    } else if (type == Map.class) {
      // a Borsh Vec of pairs: the node prints the pairs in wire order and the generated record
      // keeps that order in a LinkedHashMap
      final var expected = new LinkedHashMap<String, String>();
      for (final var pair : array(parsed)) {
        final var entry = array(pair);
        assertEquals(2, entry.size(), at + " entries are key/value pairs");
        assertNull(expected.put(string(entry.get(0)), string(entry.get(1))), at + " keys are unique");
      }
      final var map = assertInstanceOf(Map.class, decoded, at);
      assertEquals(expected, map, at);
      assertEquals(List.copyOf(expected.keySet()), List.copyOf(map.keySet()),
          at + " iteration order follows the Borsh Vec");
    } else if (type.isRecord()) {
      assertRecord(where, coordinate, object(parsed), decoded);
    } else {
      fail(at + ": no normaliser is written for a " + type.getName() + " component");
    }
  }

  /// jsonParsed prints `null` for a zeroable option. Which shape the generated reader holds is
  /// decided by the IDL and not by the wire, so it is asserted from the declared table rather
  /// than accepted either way.
  private static void assertZeroedPublicKey(final String at,
                                            final String coordinate,
                                            final Object decoded) {
    if (ZEROABLE_OPTIONS.contains(coordinate)) {
      assertNull(decoded,
          at + ": the IDL declares a zeroable option, so 32 zero bytes decode as null");
    } else if (PLAIN_PUBLIC_KEYS.contains(coordinate)) {
      assertEquals(PublicKey.NONE, decoded,
          at + ": the IDL declares a plain publicKey, so the 32 zero bytes the node prints as "
              + "null decode as PublicKey.NONE");
    } else {
      fail(at + ": the node printed null for a public key this test has not classified — add it "
          + "to ZEROABLE_OPTIONS or PLAIN_PUBLIC_KEYS");
    }
  }

  /// The generated `tokenMetadata` carries the raw UTF-8 of each string beside the decoded
  /// `String`, so `l()` and `write` need no re-encoding. The node prints only the strings, so
  /// the arrays are compared against their own siblings instead of being skipped.
  private static void assertTokenMetadataRawBytes(final String where,
                                                  final Extension.tokenMetadata metadata) {
    assertArrayEquals(metadata.name().getBytes(StandardCharsets.UTF_8), metadata._name(),
        where + "tokenMetadata._name is the UTF-8 of name");
    assertArrayEquals(metadata.symbol().getBytes(StandardCharsets.UTF_8), metadata._symbol(),
        where + "tokenMetadata._symbol is the UTF-8 of symbol");
    assertArrayEquals(metadata.uri().getBytes(StandardCharsets.UTF_8), metadata._uri(),
        where + "tokenMetadata._uri is the UTF-8 of uri");
  }

  // ---------------------------------------------------------------------------------------
  // 4. cross-check against sava-core
  // ---------------------------------------------------------------------------------------

  /// The second comparator: sava-core's hand-written decoders over the same bytes. Where a
  /// component has the same name on both sides its value must agree after normalising the two
  /// shape differences — sava-core reads a zeroable option unconditionally and so holds
  /// [PublicKey#NONE] where the generated record holds `null`, and it keeps a ciphertext as a raw
  /// `byte[]` where the generated record wraps it in `EncryptedBalance`/`DecryptableBalance`.
  ///
  /// Oracle: sava-core 25.11.0, resolved through the version catalog. Not independent of this
  /// repository's *corpus* — it is the same capture — but independent of its generator.
  @Test
  void savaCoreAndTheGeneratedReaderAgree() {
    int compared = 0;
    for (final var fixture : FIXTURES) {
      if (fixture.multisig() || fixture.baseLengthOnly()) {
        // the base-length four are in savaCore25_11_0CannotDecodeABaseLengthAccount instead
        continue;
      }
      ++compared;
      final var where = fixture + " ";
      final var generated = decode(fixture.key(), fixture.data(), fixture.kind());
      final var savaCoreExtensions = new ArrayList<TokenExtension>();
      if (generated instanceof Mint mint) {
        final var savaCore = Token2022.read(fixture.key(), fixture.data());
        assertEquals(AccountType.Mint, savaCore.accountType(),
            where + "the account-type byte the generated reader checks as its hidden prefix");
        assertSameMint(where, mint, savaCore.mint());
        savaCoreExtensions.addAll(savaCore.tokenExtensions());
      } else {
        final var savaCore = Token2022Account.read(fixture.key(), fixture.data());
        assertEquals(AccountType.Account, savaCore.type(),
            where + "the account-type byte the generated reader checks as its hidden prefix");
        assertSameToken(where, (Token) generated, savaCore.tokenAccount());
        savaCoreExtensions.addAll(savaCore.tokenExtensions());
      }

      // sava-core's parseExtensions returns at the first zero type word, so the generated array
      // is truncated there rather than filtered — see carried
      final var generatedExtensions = carried(extensionsOf(generated));
      final var savaCoreIds = new ArrayList<Integer>();
      for (final var extension : savaCoreExtensions) {
        assertFalse(extension instanceof UnknownTokenExtension,
            where + "sava-core fell through to UnknownTokenExtension at type " + extension.ordinal());
        savaCoreIds.add(extension.ordinal());
      }
      assertEquals(ordinalsOf(generatedExtensions), savaCoreIds,
          where + "extension ordinals in TLV order");

      for (int i = 0; i < generatedExtensions.size(); ++i) {
        final var extension = generatedExtensions.get(i);
        assertSameExtension(where, extension.getClass().getSimpleName(), extension,
            savaCoreExtensions.get(i));
      }
    }
    assertEquals(20, compared, "extended fixtures compared against sava-core");
  }

  private static void assertSameMint(final String where,
                                     final Mint generated,
                                     final software.sava.core.accounts.token.Mint savaCore) {
    assertEquals(generated._address(), savaCore.address(), where + "mint address");
    assertEquals(generated.mintAuthority(), savaCore.mintAuthority(), where + "mintAuthority");
    assertEquals(generated.supply(), savaCore.supply(), where + "supply");
    assertEquals(generated.decimals(), savaCore.decimals(), where + "decimals");
    assertEquals(generated.isInitialized(), savaCore.initialized(), where + "isInitialized");
    assertEquals(generated.freezeAuthority(), savaCore.freezeAuthority(), where + "freezeAuthority");
  }

  private static void assertSameToken(final String where,
                                      final Token generated,
                                      final software.sava.core.accounts.token.TokenAccount savaCore) {
    assertEquals(generated._address(), savaCore.address(), where + "account address");
    assertEquals(generated.mint(), savaCore.mint(), where + "mint");
    assertEquals(generated.owner(), savaCore.owner(), where + "owner");
    assertEquals(generated.amount(), savaCore.amount(), where + "amount");
    assertEquals(generated.delegate(), savaCore.delegate(), where + "delegate");
    // two enums over the same three states, declared in the same order, in two libraries
    assertEquals(generated.state().ordinal(), savaCore.state().ordinal(), where + "state");
    assertEquals(generated.state().name(), savaCore.state().name().toLowerCase(Locale.ROOT),
        where + "state name");
    // sava-core keeps the COption tag and its value in separate components; the generated record
    // folds them into an OptionalLong
    assertEquals(generated.isNative().isPresent(), savaCore.isNativeOption() == 1,
        where + "isNative option tag");
    assertEquals(generated.isNative().orElse(0L), savaCore.isNative(), where + "isNative");
    assertEquals(generated.delegatedAmount(), savaCore.delegatedAmount(), where + "delegatedAmount");
    assertEquals(generated.closeAuthority(), savaCore.closeAuthority(), where + "closeAuthority");
  }

  private static void assertSameExtension(final String where,
                                          final String variant,
                                          final Extension generated,
                                          final TokenExtension savaCore) {
    for (final var component : generated.getClass().getRecordComponents()) {
      final var coordinate = variant + '.' + component.getName();
      if (NOT_IN_SAVA_CORE.contains(coordinate)) {
        continue;
      }
      final var name = SAVA_CORE_ALIASES.getOrDefault(coordinate, component.getName());
      final var counterpart = component(savaCore, name);
      assertNotNull(counterpart, where + coordinate + ": sava-core 25.11.0 has no component '"
          + name + "' — give it a SAVA_CORE_ALIASES entry, or add it to NOT_IN_SAVA_CORE with "
          + "the reason");
      assertSameValue(where + coordinate, value(component, generated),
          value(counterpart, savaCore), component.getType());
    }
  }

  private static void assertSameValue(final String at,
                                      final Object generated,
                                      final Object savaCore,
                                      final Class<?> type) {
    if (type == PublicKey.class) {
      if (generated == null) {
        assertEquals(PublicKey.NONE, savaCore,
            at + ": the generated reader nulls a zeroed option, sava-core decodes PublicKey.NONE");
      } else {
        assertEquals(generated, savaCore, at);
      }
    } else if (type == EncryptedBalance.class) {
      assertArrayEquals(((EncryptedBalance) generated).val(), (byte[]) savaCore, at);
    } else if (type == DecryptableBalance.class) {
      assertArrayEquals(((DecryptableBalance) generated).val(), (byte[]) savaCore, at);
    } else if (type == AccountState.class) {
      // sava-core keeps DefaultAccountState's discriminant as the raw u8
      assertEquals(((AccountState) generated).ordinal(), savaCore, at);
    } else if (type == Map.class) {
      final var expected = assertInstanceOf(Map.class, generated, at);
      final var actual = assertInstanceOf(Map.class, savaCore, at);
      assertEquals(expected, actual, at);
      assertEquals(List.copyOf(expected.keySet()), List.copyOf(actual.keySet()),
          at + ": both keep the Borsh Vec's order");
    } else if (type.isRecord()) {
      assertNotNull(generated, at);
      assertNotNull(savaCore, at);
      for (final var component : generated.getClass().getRecordComponents()) {
        final var counterpart = component(savaCore, component.getName());
        assertNotNull(counterpart, at + '.' + component.getName() + ": no sava-core counterpart");
        assertSameValue(at + '.' + component.getName(), value(component, generated),
            value(counterpart, savaCore), component.getType());
      }
    } else {
      assertEquals(generated, savaCore, at);
    }
  }

  /// The reader bug that keeps the four extension-free fixtures out of the cross-check above:
  /// sava-core 25.11.0 indexes the account-type byte at offset 165 unconditionally, so a mint of
  /// exactly `Mint::LEN` or a token account of exactly `Account::LEN` — perfectly legal accounts
  /// with no remainder at all — throw instead of decoding. The generated readers decode them.
  ///
  /// **This assertion is the checker that says when to widen the comparison.** The fix is on
  /// sava `main` (commit 28e30ad, "fix: decode extension-free Token-2022 mints and token
  /// accounts") and unreleased: when the version catalog moves to a sava-core carrying it, this
  /// test stops passing, and the four fixtures should then come out of the exclusion in
  /// [#savaCoreAndTheGeneratedReaderAgree()] and be compared like every other one.
  @Test
  void savaCore25_11_0CannotDecodeABaseLengthAccount() {
    int refused = 0;
    for (final var fixture : FIXTURES) {
      if (!fixture.baseLengthOnly()) {
        continue;
      }
      ++refused;
      final var data = fixture.data();
      final var key = fixture.key();
      assertNotNull(decode(key, data, fixture.kind()),
          fixture + ": the generated reader decodes a base-length account");
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> Token2022.read(key, data),
          fixture + ": sava-core 25.11.0's mint reader indexes past the end of the account");
      assertThrows(ArrayIndexOutOfBoundsException.class, () -> Token2022Account.read(key, data),
          fixture + ": sava-core 25.11.0's token-account reader does the same");
    }
    assertEquals(4, refused, "two mints at 82 bytes and two token accounts at 165");
  }

  // ---------------------------------------------------------------------------------------
  // 5. the multisig fixture
  // ---------------------------------------------------------------------------------------

  /// A multisig is never extensible, so a buffer of exactly `Multisig::LEN` cannot be told apart
  /// from an extended mint or token account and the program refuses it outright
  /// (`check_min_len_and_not_multisig`). The fixture is a real on-chain 2-of-3 multisig, and the
  /// node's own parse agrees it is one.
  ///
  /// Both generated readers refuse it, though by their own rules rather than by length, and it is
  /// worth knowing which rule fires. Neither reader knows `Multisig::LEN`; what stops them is
  /// that a multisig's signer keys land in the base state's four-byte `COption` tag slots, where
  /// a tag has to be 0 or 1 — the mint reader rejects the mint-authority tag at offset 0 and the
  /// token reader the delegate tag at offset 72. The account-type discriminant is the backstop
  /// behind that, and only the backstop is a rule about the *shape* rather than about the bytes
  /// this particular multisig happens to carry, so
  /// [#theAccountTypeDiscriminantIsTheBackstopBehindTheOptionTags()] exercises it separately.
  ///
  /// sava-core 25.11.0 does **not** refuse it. It reads that same zero as
  /// `AccountType.Uninitialized`, finds a zero type word where a TLV chain would start, and hands
  /// back a record whose `l()` is 170 against the 355 bytes that went in — so what it would write
  /// is neither the account it read nor anything the program would unpack. The length guard is in
  /// the same unreleased sava commit as the base-length fix (28e30ad), so what that release does
  /// is asserted here and this test moves with it.
  @Test
  void theMultisigFixtureIsRefusedByBothGeneratedReaders() {
    int multisigs = 0;
    for (final var fixture : FIXTURES) {
      if (!fixture.multisig()) {
        continue;
      }
      ++multisigs;
      assertEquals(MULTISIG_LEN, fixture.length(), fixture + ": Multisig::LEN");
      final var data = fixture.data();
      final var key = fixture.key();
      final var info = fixture.info();
      assertEquals(2L, u64(info.get("numRequiredSigners")), fixture + ": a real 2-of-3");
      assertEquals(3L, u64(info.get("numValidSigners")), fixture + ": a real 2-of-3");

      // signers[0] occupies the mint-authority option tag, signers[2] the delegate one; the
      // reader reports the tag as the signed int it read, so the second reads negative
      assertEquals("Invalid presence tag 1593901826; a 4 byte option tag is 0 or 1.",
          assertThrows(RuntimeException.class, () -> Mint.read(key, data),
              fixture + ": the generated mint reader must refuse a multisig").getMessage());
      assertEquals("Invalid presence tag -1914089608; a 4 byte option tag is 0 or 1.",
          assertThrows(RuntimeException.class, () -> Token.read(key, data),
              fixture + ": the generated token reader must refuse a multisig").getMessage());

      // what the pinned sava-core release does instead, asserted so that the release which starts
      // refusing it fails here and this comparison can be tightened
      final var asMint = Token2022.read(key, data);
      assertEquals(AccountType.Uninitialized, asMint.accountType(),
          fixture + ": sava-core 25.11.0 reads the zeroed signer slot as an account type");
      assertNotEquals(fixture.length(), asMint.l(),
          fixture + ": and sizes the record it built from something that is not a mint");
      final var asAccount = Token2022Account.read(key, data);
      assertEquals(AccountType.Uninitialized, asAccount.type(), fixture + ": the same both ways");
      assertNotEquals(fixture.length(), asAccount.l(), fixture + ": the same both ways");
    }
    assertEquals(1, multisigs, "the corpus must carry exactly one real multisig");
  }

  /// What refuses a `Multisig::LEN` buffer once its `COption` tags happen to be legal: the byte
  /// the program writes at offset 165 to say which of the two extensible account types this is.
  /// Each generated reader requires the constant its own type declares — 1 for a mint, 2 for a
  /// token account — and a multisig has an eleventh signer slot there, all zeros for a 2-of-3.
  ///
  /// The input is the real fixture with its three (respectively five) option tags zeroed, so it
  /// is derived from a captured account rather than invented, and it is the only synthetic input
  /// in this class. Without it the multisig assertions above would only say that *these* signer
  /// keys are not valid option tags.
  ///
  /// A mint has one more backstop ahead of the discriminant: the 83 bytes between its base state
  /// and the type byte must be zero, as the program requires on unpack, and a multisig's signer
  /// slots are not. That rule is asserted first, then the padding is cleared to reach the byte.
  @Test
  void theAccountTypeDiscriminantIsTheBackstopBehindTheOptionTags() {
    for (final var fixture : FIXTURES) {
      if (!fixture.multisig()) {
        continue;
      }
      final var key = fixture.key();

      final byte[] asMint = fixture.data().clone();
      clearTag(asMint, Mint.MINT_AUTHORITY_OPTION_OFFSET);
      clearTag(asMint, Mint.FREEZE_AUTHORITY_OPTION_OFFSET);
      assertEquals("Expected 83 zero bytes of padding at offset 82.",
          assertThrows(RuntimeException.class, () -> Mint.read(key, asMint)).getMessage());
      java.util.Arrays.fill(asMint, 82, 165, (byte) 0);
      assertEquals("Expected the hidden prefix constant 1 at offset 165, found 0.",
          assertThrows(RuntimeException.class, () -> Mint.read(key, asMint)).getMessage());

      final byte[] asAccount = fixture.data().clone();
      clearTag(asAccount, Token.DELEGATE_OPTION_OFFSET);
      clearTag(asAccount, Token.IS_NATIVE_OPTION_OFFSET);
      clearTag(asAccount, Token.CLOSE_AUTHORITY_OPTION_OFFSET);
      assertEquals("Expected the hidden prefix constant 2 at offset 165, found 0.",
          assertThrows(RuntimeException.class, () -> Token.read(key, asAccount)).getMessage());
    }
  }

  private static void clearTag(final byte[] data, final int offset) {
    for (int i = offset; i < offset + Integer.BYTES; ++i) {
      data[i] = 0;
    }
  }

  // ---------------------------------------------------------------------------------------
  // the zeroable-option split, checked against the readers rather than written down
  // ---------------------------------------------------------------------------------------

  /// [#ZEROABLE_OPTIONS] and [#PLAIN_PUBLIC_KEYS] are read off the IDL, and every `PublicKey`
  /// component of every variant has to be in exactly one of them — which makes the two sets a
  /// complete, checked inventory rather than a note about the fields this corpus happens to
  /// exercise. Each variant is decoded from an all-zero value of its own declared size, and a
  /// component comes back `null` if and only if its coordinate is a zeroable option.
  ///
  /// Oracle: the generated readers themselves, against the IDL the two sets were read from.
  @Test
  void zeroableOptionsAreExactlyTheFieldsTheIdlDeclares() {
    final var nulled = new TreeSet<String>();
    final var present = new TreeSet<String>();
    int variants = 0;
    for (final var subclass : Extension.class.getPermittedSubclasses()) {
      final var variant = subclass.getSimpleName();
      final int ordinal = VARIANT_IDS.get(variant);
      if (ordinal == 0) {
        // a zero type word is trailing uninitialized space, not a size-prefixed value
        continue;
      }
      ++variants;
      final var decoded = Extension.read(zeroedTlv(subclass, ordinal), 0);
      assertNotNull(decoded, variant + ": an all-zero value of the declared size must decode");
      assertEquals(variant, decoded.getClass().getSimpleName(),
          "the type word " + ordinal + " must dispatch to " + variant);
      for (final var component : decoded.getClass().getRecordComponents()) {
        if (component.getType() != PublicKey.class) {
          continue;
        }
        final var coordinate = variant + '.' + component.getName();
        if (value(component, decoded) == null) {
          nulled.add(coordinate);
        } else {
          assertEquals(PublicKey.NONE, value(component, decoded),
              coordinate + ": a plain public key over 32 zero bytes is PublicKey.NONE");
          present.add(coordinate);
        }
      }
    }
    assertEquals(HIGHEST_KNOWN_EXTENSION, variants, "every extension type but the zero word");
    assertEquals(new TreeSet<>(ZEROABLE_OPTIONS), nulled,
        "the fields the generated readers decode as null over 32 zero bytes");
    assertEquals(new TreeSet<>(PLAIN_PUBLIC_KEYS), present,
        "the fields the generated readers decode as PublicKey.NONE over 32 zero bytes");
  }

  /// A TLV entry for one variant whose value is entirely zeros: the two-byte type word, the
  /// two-byte size prefix, then that many zero bytes.
  private static byte[] zeroedTlv(final Class<?> variant, final int ordinal) {
    final int size = variant.getSimpleName().equals("tokenMetadata")
        // the one variable-length variant: two public keys, three empty size-prefixed strings and
        // an empty map, none of which has a BYTES constant to read
        ? 32 + 32 + 4 + 4 + 4 + 4
        : declaredBytes(variant);
    final byte[] data = new byte[4 + size];
    data[0] = (byte) ordinal;
    data[1] = (byte) (ordinal >>> 8);
    data[2] = (byte) size;
    data[3] = (byte) (size >>> 8);
    return data;
  }

  private static int declaredBytes(final Class<?> variant) {
    try {
      return ((Integer) variant.getField("BYTES").get(null)).intValue();
    } catch (final NoSuchFieldException noValue) {
      // a marker extension declares no BYTES because its value is empty
      assertEquals(0, variant.getRecordComponents().length,
          variant.getSimpleName() + " declares no BYTES but has components");
      return 0;
    } catch (final ReflectiveOperationException e) {
      throw new AssertionError("Could not read " + variant.getSimpleName() + ".BYTES", e);
    }
  }

  // ---------------------------------------------------------------------------------------
  // decoding helpers
  // ---------------------------------------------------------------------------------------

  private static SerDe decode(final PublicKey address, final byte[] data, final String kind) {
    return kind.equals("mint") ? Mint.read(address, data) : Token.read(address, data);
  }

  private static Extension[] extensionsOf(final SerDe decoded) {
    return decoded instanceof Mint mint ? mint.extensions() : ((Token) decoded).extensions();
  }

  /// The extensions an account really carries, which is the decoded array **up to its first
  /// `uninitialized` element**.
  ///
  /// A zero type word is not an extension: it is space the program reallocated and has not
  /// written yet, and the program's own TLV walk — like the upstream JavaScript client's and
  /// sava-core's — stops at it, so everything past one is unread tail. The generated reader
  /// instead keeps each zero word as an `Extension.uninitialized` element and keeps going, which
  /// is what lets `write` reproduce the tail byte for byte, so a comparison against any of those
  /// three has to truncate here rather than filter zeros out of the middle.
  ///
  /// No account in this corpus has a zero tail at all —
  /// [#generatedReadersDecodeInTlvOrderAndRoundTripByteForByte()] asserts that, which is what
  /// makes this a statement about the helper's contract rather than about the fixtures. It is
  /// also where `1DMqxdD2LQF8dR8qh5ULVK7pVx616DggBT3pKEKDPJ5`'s manifest note is wrong: that
  /// mint's note says its extensions land on `Multisig::LEN` and the program allocated the
  /// two-byte pad, but its metadata pointer and token metadata end exactly at byte 357 with no
  /// pad and no zero word, which is why `l()` reports 357 and the round trip is exact.
  private static List<Extension> carried(final Extension[] extensions) {
    final var carried = new ArrayList<Extension>(extensions.length);
    for (final var extension : extensions) {
      if (extension.ordinal() == 0) {
        break;
      }
      carried.add(extension);
    }
    return carried;
  }

  private static List<Integer> ordinalsOf(final List<Extension> extensions) {
    final var ordinals = new ArrayList<Integer>(extensions.size());
    for (final var extension : extensions) {
      ordinals.add(extension.ordinal());
    }
    return ordinals;
  }

  private static RecordComponent component(final Object record, final String name) {
    for (final var component : record.getClass().getRecordComponents()) {
      if (component.getName().equals(name)) {
        return component;
      }
    }
    return null;
  }

  private static Object value(final RecordComponent component, final Object record) {
    try {
      return component.getAccessor().invoke(record);
    } catch (final ReflectiveOperationException e) {
      throw new AssertionError("Could not read " + component, e);
    }
  }

  /// Record equality the generated records cannot express themselves: a record's default
  /// `equals` compares a `byte[]` component by reference, so two records read from the same bytes
  /// are unequal whenever one carries an array. Compares component-wise instead, with
  /// array-aware equality at every level — the same walk `Token2022IxDataFuzz` uses.
  private static boolean sameValue(final Object left, final Object right) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null || !left.getClass().equals(right.getClass())) {
      return false;
    }
    final var type = left.getClass();
    if (type.isArray()) {
      if (type.getComponentType().isPrimitive()) {
        return Objects.deepEquals(left, right);
      }
      final var leftElements = (Object[]) left;
      final var rightElements = (Object[]) right;
      if (leftElements.length != rightElements.length) {
        return false;
      }
      for (int i = 0; i < leftElements.length; ++i) {
        if (!sameValue(leftElements[i], rightElements[i])) {
          return false;
        }
      }
      return true;
    }
    if (!type.isRecord()) {
      // enums, strings, public keys, optionals and maps compare themselves
      return left.equals(right);
    }
    for (final var component : type.getRecordComponents()) {
      if (!sameValue(value(component, left), value(component, right))) {
        return false;
      }
    }
    return true;
  }

  // ---------------------------------------------------------------------------------------
  // jsonParsed helpers
  // ---------------------------------------------------------------------------------------

  /// A JSON number keeps its source text: a `u64` maximum group size has more significant digits
  /// than a `double` holds, and this corpus carries one printed as a bare JSON number.
  private record Num(String text) {

    @Override
    public String toString() {
      return text;
    }
  }

  private static Object json(final byte[] utf8) {
    return readValue(JsonIterator.parse(utf8));
  }

  private static Object readValue(final JsonIterator ji) {
    return switch (ji.whatIsNext()) {
      case OBJECT -> {
        final var map = new LinkedHashMap<String, Object>();
        ji.testObject((buf, offset, len, next) -> {
          final var field = new String(buf, offset, len);
          assertFalse(map.containsKey(field), "duplicate JSON field " + field);
          map.put(field, readValue(next));
          return true;
        });
        yield map;
      }
      case ARRAY -> {
        final var list = new ArrayList<>();
        while (ji.readArray()) {
          list.add(readValue(ji));
        }
        yield list;
      }
      case STRING -> ji.readString();
      case NUMBER -> new Num(ji.readNumberAsString());
      case BOOLEAN -> ji.readBoolean();
      case NULL -> {
        assertTrue(ji.readNull(), "expected a JSON null");
        yield null;
      }
      case INVALID -> throw new IllegalStateException("malformed JSON fixture");
    };
  }

  private static String string(final Object json) {
    return assertInstanceOf(String.class, json, "expected a JSON string");
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> object(final Object json) {
    return assertInstanceOf(Map.class, json, "expected a JSON object");
  }

  @SuppressWarnings("unchecked")
  private static List<Object> array(final Object json) {
    return assertInstanceOf(List.class, json, "expected a JSON array");
  }

  private static byte[] base64(final Object json) {
    return Base64.getDecoder().decode(string(json));
  }

  /// The digits the node printed, whether it printed them as a number or as a string: a `u64`
  /// crosses the JSON boundary both ways, depending on whether it fits the node's own serializer.
  private static String decimalText(final Object json) {
    return json instanceof Num number ? number.text() : string(json);
  }

  /// A `u64` as unsigned decimal, normalised through [BigInteger] so that a value printed with a
  /// leading `+` or with insignificant zeros still compares.
  private static String u64Text(final Object json) {
    return new BigInteger(decimalText(json)).toString();
  }

  /// Text from a value the node must have printed as a JSON *number*, so that a field silently
  /// changing representation is a failure rather than a pass.
  private static String numberText(final Object json) {
    return assertInstanceOf(Num.class, json, "expected a JSON number").text();
  }

  private static long u64(final Object json) {
    return new BigInteger(decimalText(json)).longValue();
  }

  /// A `COption` on a base state: the node prints an absent mint authority as `null` and omits an
  /// absent token-account option entirely, and the generated record holds `null` for both.
  private static void assertCOption(final String where,
                                    final String key,
                                    final Map<String, Object> info,
                                    final PublicKey decoded,
                                    final Set<String> consumed) {
    consumed.add(key);
    final var parsed = info.get(key);
    if (parsed == null) {
      assertNull(decoded, where + key + " is absent, so the COption tag is zero");
    } else {
      assertEquals(string(parsed), decoded.toBase58(), where + key);
    }
  }

  private static Object require(final String where,
                                final String key,
                                final Map<String, Object> info,
                                final Set<String> consumed) {
    assertTrue(info.containsKey(key), where + "the node's parse is missing " + key);
    consumed.add(key);
    return info.get(key);
  }

  /// Nothing the node printed may go uncompared, so the keys the base-state comparison consumed
  /// are checked against the keys the node actually wrote.
  private static void assertEveryKeyConsumed(final String where,
                                             final Map<String, Object> info,
                                             final Set<String> consumed) {
    final var printed = new TreeSet<>(info.keySet());
    printed.remove("extensions");
    final var compared = new TreeSet<>(consumed);
    compared.retainAll(printed);
    assertEquals(printed, compared,
        where + ": every jsonParsed base-state key must be compared against a record component");
  }
}
