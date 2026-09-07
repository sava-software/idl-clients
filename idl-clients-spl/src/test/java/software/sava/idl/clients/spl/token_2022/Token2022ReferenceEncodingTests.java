package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.token_2022.gen.Token2022Program;
import software.sava.idl.clients.spl.token_2022.gen.types.AccountState;
import software.sava.idl.clients.spl.token_2022.gen.types.AuthorityType;
import software.sava.idl.clients.spl.token_2022.gen.types.DecryptableBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.EncryptedBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.ExtensionType;
import software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Differential coverage for the Token 2022 program's instruction data and account lists, against
/// an encoder this repository did not write.
///
/// Every other Token 2022 test here is a round trip: an instruction is built with the generated
/// builder and read back with the generated `IxData`. Both are emitted from the same IDL by the
/// same generator, so they agree by construction — a systematic change to the wire format (a
/// discriminator's width, a size prefix's width, a u16 enum narrowed to u8, the order of two
/// fields) moves the writer and the reader together and leaves the round trip passing. It is not a
/// weak test, it is a test that structurally cannot see this class of change.
///
/// The bytes in `/token_2022/reference-vectors.txt` come from `solana-program/token-2022`'s own
/// generated JavaScript client, which `@codama/renderers-js` renders from the same IDL this
/// repository generates from — an independent implementation of the same specification.
/// `tools/token2022-vectors.mjs` regenerates them.
///
/// Each vector also carries the account list upstream's builder produced, and every case here goes
/// through the *key-argument* builder overload rather than the `List<AccountMeta>` one, so account
/// order, writability and signer flags are compared too. That is the half a data-only comparison
/// cannot reach, and Token 2022 is where it matters: optional accounts the caller omits, sysvars
/// the builder fills in, and signers that are only signers because a signer was passed.
///
/// What this does **not** establish is that upstream's encoder matches the deployed program.
/// Nothing here could: both sides descend from the same IDL.
final class Token2022ReferenceEncodingTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final AccountMeta PROGRAM = ACCOUNTS.invokedToken2022Program();

  // ---------------------------------------------------------------------------
  // The values every vector is built from — one constant per line, in the order
  // tools/token2022-vectors.mjs declares them, so the two files can be read side by side.
  // ---------------------------------------------------------------------------

  private static final PublicKey MINT = key(0x11);
  private static final PublicKey TOKEN = key(0x22);
  private static final PublicKey AUTHORITY = key(0x33);
  private static final PublicKey DESTINATION = key(0x44);
  private static final PublicKey DELEGATE = key(0x55);
  private static final PublicKey PAYER = key(0x66);
  private static final PublicKey METADATA = key(0x77);
  private static final PublicKey GROUP = key(0x88);
  private static final PublicKey MEMBER = key(0x99);
  private static final PublicKey EQUALITY_RECORD = key(0xaa);
  private static final PublicKey VALIDITY_RECORD = key(0xbb);
  private static final PublicKey RANGE_RECORD = key(0xcc);
  private static final PublicKey FEE_SIGMA_RECORD = key(0xdd);
  private static final PublicKey FEE_VALIDITY_RECORD = key(0xee);
  private static final PublicKey ELGAMAL_REGISTRY = key(0x0f);
  private static final PublicKey NEW_AUTHORITY = key(0x1f);
  private static final PublicKey ELGAMAL_PUBKEY = key(0x2f);
  private static final PublicKey HOOK_PROGRAM = key(0x3f);
  private static final PublicKey SOURCE = key(0x4f);
  private static final PublicKey FEE_RECEIVER = key(0x5f);
  private static final PublicKey NATIVE_MINT = key(0x6f);
  private static final PublicKey SECOND_AUTHORITY = key(0x7f);
  private static final PublicKey POINTER_ADDRESS = key(0x8f);
  private static final PublicKey MULTISIG = key(0x9f);

  private static final long AMOUNT = 4_230_000_000_000L;
  /// u64 max. Java has no unsigned long, so the caller passes the same 64 bits as -1.
  private static final long MAX_U64 = -1L;
  private static final int DECIMALS = 9;
  private static final int M = 3;
  private static final long FEE = 7_777L;
  private static final long MAXIMUM_FEE = 5_000_000L;
  private static final int FEE_BASIS_POINTS = 1_234;
  private static final int RATE = 4_321;
  private static final int NEGATIVE_RATE = -1_234;
  private static final double MULTIPLIER = 3.141592653589793;
  private static final double NEGATIVE_MULTIPLIER = -0.5;
  private static final long EFFECTIVE_TIMESTAMP = 1_700_000_000L;
  private static final int PROOF_OFFSET = 5;
  private static final int NEGATIVE_PROOF_OFFSET = -3;
  private static final long CREDIT_COUNTER = 1_024L;
  private static final long MAX_PENDING_CREDITS = 65_536L;
  private static final int NUM_TOKEN_ACCOUNTS = 7;
  private static final long MAX_SIZE = 9_000L;
  private static final long START = 16L;
  private static final long END = 48L;

  private static final DecryptableBalance AVAILABLE_BALANCE = new DecryptableBalance(cipher(0xc3, 36));
  private static final DecryptableBalance SUPPLY_BALANCE = new DecryptableBalance(cipher(0xd4, 36));
  private static final EncryptedBalance AUDITOR_LO = new EncryptedBalance(cipher(0xa1, 64));
  private static final EncryptedBalance AUDITOR_HI = new EncryptedBalance(cipher(0xb2, 64));

  /// `"name-"` followed by U+00E9, U+20AC and U+1D11E: a 2-, a 3- and a 4-byte UTF-8 sequence, so a
  /// string measured in `String.length()` (9) rather than in encoded bytes (14) is a mismatch.
  /// Written as escapes, and matched by escapes in `tools/token2022-vectors.mjs`, so that neither
  /// the vector nor the assertion depends on how a build reads this file.
  private static final String MULTIBYTE = "name-\u00e9\u20ac\ud834\udd1e";
  private static final String NAME = "Sava Token";
  private static final String SYMBOL = "SAVA";
  private static final String URI = "https://sava.software/token.json";
  private static final String FIELD_KEY = "website";
  private static final String UI_AMOUNT = "12.345";

  private static final Map<String, Vector> VECTORS = load();

  /// Where the generated builder is *known* to produce a different account list from upstream's,
  /// keyed by vector name and carrying the reason the difference is legitimate.
  ///
  /// A vector listed here must differ; a vector not listed here must not; and a name listed here
  /// must be a vector that exists. All three are asserted by
  /// [#everyVectorMatchesTheReferenceAccountList], so an entry cannot outlive the difference it
  /// describes and a new difference cannot arrive as a silent skip.
  ///
  /// **It is empty, and that is a measurement rather than an omission.** All 155 vectors' account
  /// lists agree exactly — order, writability and signer flag — including the three shapes where a
  /// divergence was expected. `syncNative`'s rent sysvar is optional in the IDL and *defaulted* by
  /// upstream's builder rather than omitted, so both sides emit it and the generated helper's
  /// `SolanaAccounts` argument lands on the same two accounts. The eight instructions with genuinely
  /// optional accounts (`confidentialWithdraw`, `confidentialTransfer`,
  /// `confidentialTransferWithFee`, `confidentialMint`, `confidentialBurn`,
  /// `permissionedConfidentialBurn`, `configureConfidentialTransferAccountWithRegistry`, and
  /// `syncNative`) are the only ones upstream renders with the `omitted` strategy, and the
  /// generated builders drop a null account the same way. Every account either IDL declares a
  /// signer is marked one unconditionally here, and upstream marks it one whenever a signer was
  /// passed — which the vectors do for all of them.
  ///
  /// What is deliberately *not* compared is anything appended past the declared account list:
  /// upstream's `multiSigners`, and the `signers` / `sources` arrays on `initializeMultisig`,
  /// `harvestWithheldTokensToMint` and `withdrawWithheldTokensFromAccounts`. No IDL expresses those
  /// and no generated builder emits them, so the vectors pass them empty; a caller assembling them
  /// is doing hand-written work that belongs beside the client, not inside it.
  private static final Map<String, String> EXPECTED_ACCOUNT_DIFFERENCES = Map.of();

  // ---------------------------------------------------------------------------
  // The comparison
  // ---------------------------------------------------------------------------

  /// Every vector's instruction data, built through the generated builder and compared byte for
  /// byte.
  ///
  /// Mismatches are collected rather than thrown one at a time: a wire-format change breaks many
  /// vectors at once, and the shape of *which* ones is the diagnosis. One instruction failing is a
  /// field; every extension instruction failing is the two-byte discriminator; every vector with a
  /// string failing is the size prefix.
  @Test
  void everyVectorMatchesTheReferenceEncoder() {
    final var failures = new ArrayList<String>();
    VECTORS.forEach((name, vector) -> {
      final byte[] actual = data(build(name));
      if (!Arrays.equals(vector.data(), actual)) {
        failures.add(name
            + "\n  upstream " + HexFormat.of().formatHex(vector.data())
            + "\n  ours     " + HexFormat.of().formatHex(actual));
      }
    });
    assertTrue(failures.isEmpty(),
        () -> failures.size() + " of " + VECTORS.size()
            + " vectors disagree with solana-program/token-2022's client:\n" + String.join("\n", failures));
  }

  /// Every vector's account list — address, writable, signer, in order — against the one upstream's
  /// builder produced for the same input.
  ///
  /// A difference is a finding, not a skip: it is either a defect here or a documented divergence,
  /// and the only way to keep the second from hiding the first is to name each one and to fail
  /// when a named one stops being true.
  @Test
  void everyVectorMatchesTheReferenceAccountList() {
    final var failures = new ArrayList<String>();
    final var staleExceptions = new ArrayList<String>();
    VECTORS.forEach((name, vector) -> {
      final var actual = accounts(build(name));
      final var reason = EXPECTED_ACCOUNT_DIFFERENCES.get(name);
      if (actual.equals(vector.accounts())) {
        if (reason != null) {
          staleExceptions.add(name + " — recorded as differing because " + reason
              + ", but the two lists now agree");
        }
      } else if (reason == null) {
        failures.add(name
            + "\n  upstream " + String.join(", ", vector.accounts())
            + "\n  ours     " + String.join(", ", actual));
      }
    });
    assertTrue(failures.isEmpty(),
        () -> failures.size() + " of " + VECTORS.size()
            + " account lists disagree with solana-program/token-2022's client, and are not in"
            + " EXPECTED_ACCOUNT_DIFFERENCES:\n" + String.join("\n", failures));
    assertTrue(staleExceptions.isEmpty(),
        () -> "EXPECTED_ACCOUNT_DIFFERENCES has entries that no longer describe a difference:\n"
            + String.join("\n", staleExceptions));

    final var unknown = new TreeSet<>(EXPECTED_ACCOUNT_DIFFERENCES.keySet());
    unknown.removeAll(VECTORS.keySet());
    assertTrue(unknown.isEmpty(),
        () -> "EXPECTED_ACCOUNT_DIFFERENCES names vectors that do not exist: " + unknown);
  }

  /// The matching `*IxData.read` decodes upstream's bytes and writes them back unchanged.
  ///
  /// This is the reader half, and it is the half the builder comparison above cannot reach: a
  /// reader that agrees with our writer and disagrees with the wire format leaves
  /// [#everyVectorMatchesTheReferenceEncoder] green. Reading foreign bytes and re-serialising them
  /// exercises `read`, `write` and `l()` against a length nothing in this repository chose — the
  /// shape of the recorded `SPLClientTests.authorizeStakeAccountWithSeed` regression, where `l()`
  /// was short by a size prefix and every round trip still passed because the reader was short by
  /// the same amount.
  ///
  /// The `IxData` record is looked up by name rather than switched on, so an instruction whose
  /// record is missing or renamed fails here instead of quietly losing coverage.
  @Test
  void everyVectorReDecodesThroughItsIxData() {
    final var failures = new ArrayList<String>();
    VECTORS.forEach((name, vector) -> {
      final var instruction = name.substring(0, name.indexOf('.'));
      final byte[] reserialized = reserialize(instruction, vector.data());
      if (!Arrays.equals(vector.data(), reserialized)) {
        failures.add(name
            + "\n  read     " + HexFormat.of().formatHex(vector.data())
            + "\n  rewrote  " + HexFormat.of().formatHex(reserialized));
      }
    });
    assertTrue(failures.isEmpty(),
        () -> failures.size() + " of " + VECTORS.size()
            + " vectors do not survive a read/write round trip through their IxData:\n"
            + String.join("\n", failures));
  }

  /// A new instruction upstream has to arrive with a vector, or this fails until it does.
  ///
  /// Coverage is asserted against what [Token2022Program] declares rather than against a list
  /// written here, because a list written here would be updated by the same edit that forgot the
  /// vector.
  @Test
  void everyInstructionHasAVector() {
    final var covered = VECTORS.keySet().stream()
        .map(name -> name.substring(0, name.indexOf('.')))
        .collect(Collectors.toCollection(TreeSet::new));
    final var declared = declaredInstructions();

    assertEquals(99, declared.size(), "Token2022Program declares 99 instructions");
    assertEquals(declared, covered,
        "every instruction Token2022Program declares needs at least one vector, and every vector "
            + "needs to name an instruction that exists");
  }

  // ---------------------------------------------------------------------------
  // What the priority vectors pin, asserted against the reference bytes themselves
  // ---------------------------------------------------------------------------

  /// Token 2022 uses two option encodings and they are not interchangeable.
  ///
  /// A `zeroableOption` pubkey is 32 bytes whether present or absent — absent is 32 zero bytes with
  /// no prefix — so the field after it never moves. A prefixed option is one byte when absent and
  /// 33 when present, so everything after it does. Writing one as the other produces a decodable
  /// instruction that means something else: a pointer extension initialised with an authority the
  /// caller did not name, or a mint whose freeze authority is silently the 32 zero bytes.
  @Test
  void theTwoOptionEncodingsAreNotInterchangeable() {
    // zeroableOption: two pubkeys either side of nothing, so both cases are the same length.
    assertEquals(2 + 32 + 32, VECTORS.get("initializeMetadataPointer.both").data().length);
    assertEquals(2 + 32 + 32, VECTORS.get("initializeMetadataPointer.none").data().length,
        "an absent zeroable option still occupies its 32 bytes");
    final byte[] none = VECTORS.get("initializeMetadataPointer.none").data();
    for (int i = 2; i < none.length; ++i) {
      assertEquals(0, none[i], "byte " + i + " of two absent zeroable-option pubkeys");
    }
    // The mixed case: an absent authority followed by a present address, which is the only layout
    // where a one-byte prefix would land the address 31 bytes early.
    final byte[] addressOnly = VECTORS.get("initializeMetadataPointer.address-only").data();
    assertEquals(2 + 32 + 32, addressOnly.length);
    for (int i = 2; i < 34; ++i) {
      assertEquals(0, addressOnly[i], "byte " + i + " of the absent authority");
    }
    assertEquals(POINTER_ADDRESS, PublicKey.readPubKey(addressOnly, 34));

    // Prefixed option: one byte absent, 33 present.
    assertEquals(1 + 1 + 32 + 1, VECTORS.get("initializeMint.no-freeze-authority").data().length,
        "discriminator, decimals, mintAuthority, one zero byte for the absent freeze authority");
    assertEquals(1 + 1 + 32 + 33, VECTORS.get("initializeMint.with-freeze-authority").data().length);

    // Two prefixed options back to back, all four presence combinations, because the second one's
    // presence byte moves by 32 when the first is present — the offset an off-by-one lands on.
    assertEquals(2 + 1 + 1 + 2 + 8, VECTORS.get("initializeTransferFeeConfig.no-authorities").data().length);
    assertEquals(2 + 33 + 1 + 2 + 8, VECTORS.get("initializeTransferFeeConfig.config-authority-only").data().length);
    assertEquals(2 + 1 + 33 + 2 + 8, VECTORS.get("initializeTransferFeeConfig.withdraw-authority-only").data().length);
    assertEquals(2 + 33 + 33 + 2 + 8, VECTORS.get("initializeTransferFeeConfig.both-authorities").data().length);
    assertEquals(1, VECTORS.get("initializeTransferFeeConfig.withdraw-authority-only").data()[3],
        "the second option is present one byte after an absent first one");
    assertEquals(1, VECTORS.get("initializeTransferFeeConfig.both-authorities").data()[35],
        "and 33 bytes after a present one");

    // An absent option over u64 is one zero byte, not eight — "all of it" rather than "zero of it".
    assertEquals(1 + 1, VECTORS.get("unwrapLamports.none").data().length);
    assertEquals(1 + 9, VECTORS.get("unwrapLamports.amount").data().length);
  }

  /// The token-metadata interface prefixes every string with a **u32** byte count, and the count is
  /// of UTF-8 bytes rather than of Java `char`s.
  ///
  /// Three of those strings run back to back in `initializeTokenMetadata`, so a length taken from
  /// `String.length()` does not merely truncate one field: it moves the two after it, and the
  /// instruction still decodes.
  @Test
  void metadataStringsAreLengthPrefixedInUtf8Bytes() {
    final int header = 8; // the eight-byte token-metadata interface discriminator
    assertEquals(header + 4 + 4 + 4, VECTORS.get("initializeTokenMetadata.empty").data().length,
        "three empty strings are three zero prefixes, not three skipped fields");
    assertEquals(header + (4 + 10) + (4 + 4) + (4 + 32), VECTORS.get("initializeTokenMetadata.basic").data().length);
    assertEquals(header + (4 + 14) + (4 + 4) + (4 + 32), VECTORS.get("initializeTokenMetadata.multibyte").data().length,
        "the name is 14 UTF-8 bytes, not its 9 chars");
    assertEquals(9, MULTIBYTE.length(), "and String.length() would say 9");

    // The data enum variant that carries its own prefixed string, so the value's offset depends on
    // both the variant ordinal and that string's length.
    assertEquals(header + 1 + (4 + 32), VECTORS.get("updateTokenMetadataField.uri").data().length,
        "a unit variant is one ordinal byte");
    assertEquals(header + 1 + (4 + 7) + (4 + 32), VECTORS.get("updateTokenMetadataField.key").data().length,
        "the Key variant carries a prefixed string of its own");
    assertEquals(header + 1 + (4 + 14) + (4 + 14), VECTORS.get("updateTokenMetadataField.multibyte-key").data().length);

    // uiAmount is the one bare string: no prefix at all, it runs to the end of the data.
    assertEquals(1 + 6, VECTORS.get("uiAmountToAmount.decimal").data().length);
    assertEquals(1 + 14, VECTORS.get("uiAmountToAmount.multibyte").data().length);
    assertEquals(1, VECTORS.get("uiAmountToAmount.empty").data().length,
        "an empty bare string is no bytes, not a zero prefix");
  }

  /// The narrow numeric fields, which are all `int` on this side and none of which is an `int` on
  /// the wire.
  ///
  /// `proofInstructionOffset` is an i8 and the offsets callers actually use are negative — the
  /// proof instruction sits *before* this one in the transaction. The interest rate is an i16 and
  /// negative rates are the point of the extension. `ExtensionType` is a u16 enum. A widened field
  /// shifts everything after it; a narrowed one truncates.
  @Test
  void narrowNumericFieldsKeepTheirWidthAndSign() {
    final byte[] negativeOffset = VECTORS.get("emptyConfidentialTransferAccount.negative-offset").data();
    assertEquals(3, negativeOffset.length, "two discriminator bytes and one i8");
    assertEquals((byte) NEGATIVE_PROOF_OFFSET, negativeOffset[2]);
    assertEquals((byte) PROOF_OFFSET, VECTORS.get("emptyConfidentialTransferAccount.offset").data()[2]);

    final byte[] negativeRate = VECTORS.get("initializeInterestBearingMint.negative-rate").data();
    assertEquals(2 + 32 + 2, negativeRate.length, "the rate is two bytes, after a zeroable-option pubkey");
    assertEquals((byte) (NEGATIVE_RATE & 0xFF), negativeRate[34]);
    assertEquals((byte) ((NEGATIVE_RATE >> 8) & 0xFF), negativeRate[35]);

    // A remainder-counted array of u16 enums: no length prefix, so the element width is the only
    // thing that says where the second element starts.
    assertEquals(1, VECTORS.get("reallocate.empty").data().length, "an empty remainder array is no bytes");
    assertEquals(1 + 4, VECTORS.get("reallocate.two-extensions").data().length);
    assertEquals(1 + 2, VECTORS.get("reallocate.high-ordinal").data().length);
    final byte[] highOrdinal = VECTORS.get("reallocate.high-ordinal").data();
    assertEquals((byte) ExtensionType.permissionedBurn.ordinal(), highOrdinal[1]);
    assertEquals(0, highOrdinal[2], "the high byte of a u16 ordinal");

    // f64, and a value no narrower float can hold.
    final byte[] multiplier = VECTORS.get("updateMultiplierScaledUiMint.multiplier").data();
    assertEquals(2 + 8 + 8, multiplier.length);
    assertEquals(MULTIPLIER, Double.longBitsToDouble(longAt(multiplier, 2)));
    // i64, negative, so neither side may treat it as unsigned.
    assertEquals(-1L, longAt(VECTORS.get("updateMultiplierScaledUiMint.negative-timestamp").data(), 10));

    // u64 max, for the same reason from the other end.
    final byte[] maxU64 = VECTORS.get("transfer.max-u64").data();
    assertEquals(1 + 8, maxU64.length);
    for (int i = 1; i < maxU64.length; ++i) {
      assertEquals((byte) 0xFF, maxU64[i], "byte " + i + " of an all-ones u64");
    }
  }

  /// The fixed-size ciphertexts, whose only wire property is their length.
  ///
  /// A `decryptableBalance` is 36 bytes and an `encryptedBalance` is 64, both unprefixed, and
  /// several instructions carry one of each in sequence. Nothing in the bytes marks where one ends,
  /// so a wrong size is invisible until the field after it is read — which is why the vectors use a
  /// pattern with no zero byte in it: a short copy leaves zero fill, and a long one runs into the
  /// next field's pattern.
  @Test
  void fixedSizeCiphertextsAreExactlyTheirDeclaredLength() {
    assertEquals(36, AVAILABLE_BALANCE.val().length);
    assertEquals(64, AUDITOR_LO.val().length);

    final byte[] transfer = VECTORS.get("confidentialTransfer.no-records").data();
    assertEquals(2 + 36 + 64 + 64 + 1 + 1 + 1, transfer.length,
        "two discriminator bytes, a decryptable balance, two ciphertexts and three i8 offsets");
    assertArrayEquals(AVAILABLE_BALANCE.val(), Arrays.copyOfRange(transfer, 2, 38));
    assertArrayEquals(AUDITOR_LO.val(), Arrays.copyOfRange(transfer, 38, 102));
    assertArrayEquals(AUDITOR_HI.val(), Arrays.copyOfRange(transfer, 102, 166));
    for (final byte b : transfer) {
      // the offsets are the only bytes that may be zero, and none of them is
      assertTrue(b != 0, "a zero byte anywhere in this instruction means a short copy");
    }
  }

  /// The account list is where the optional accounts live, and omitting one shortens the list
  /// rather than substituting a placeholder.
  ///
  /// The proof-record accounts sit *between* required accounts, so dropping them moves the
  /// authority — the signer — three places earlier. A client that substituted the program id, or
  /// that kept the slots and passed the default pubkey, would build an instruction the program
  /// rejects for the wrong reason.
  @Test
  void omittedOptionalAccountsShortenTheList() {
    assertEquals(6, VECTORS.get("confidentialWithdraw.all-records").accounts().size());
    assertEquals(3, VECTORS.get("confidentialWithdraw.no-records").accounts().size());
    assertEquals(signerFlag(AUTHORITY), VECTORS.get("confidentialWithdraw.no-records").accounts().getLast());
    assertEquals(signerFlag(AUTHORITY), VECTORS.get("confidentialWithdraw.all-records").accounts().getLast());

    assertEquals(10, VECTORS.get("confidentialTransferWithFee.all-records").accounts().size());
    assertEquals(4, VECTORS.get("confidentialTransferWithFee.no-records").accounts().size(),
        "six optional proof-record accounts drop out of the middle of a ten-account list");

    assertEquals(5, VECTORS.get("configureConfidentialTransferAccountWithRegistry.with-payer").accounts().size());
    assertEquals(3, VECTORS.get("configureConfidentialTransferAccountWithRegistry.no-payer").accounts().size());

    // Neither case changes a byte of instruction data — which is exactly why the data comparison
    // alone cannot see an account-list defect.
    assertArrayEquals(VECTORS.get("confidentialWithdraw.all-records").data(),
        VECTORS.get("confidentialWithdraw.no-records").data());
    assertArrayEquals(VECTORS.get("confidentialTransferWithFee.all-records").data(),
        VECTORS.get("confidentialTransferWithFee.no-records").data());
  }

  /// The sysvars a caller never names, and which therefore only ever appear because a builder put
  /// them there.
  @Test
  void autoWiredSysvarsAppearInTheReferenceLists() {
    assertEquals(rentFlag(), VECTORS.get("initializeMint.with-freeze-authority").accounts().getLast());
    assertEquals(rentFlag(), VECTORS.get("initializeAccount.basic").accounts().getLast());
    assertEquals(rentFlag(), VECTORS.get("initializeMultisig.m3").accounts().getLast());
    assertEquals(rentFlag(), VECTORS.get("syncNative.bare").accounts().getLast());
    assertEquals(2, VECTORS.get("syncNative.bare").accounts().size(),
        "syncNative's rent sysvar is optional upstream and defaulted, not omitted");

    assertEquals(readOnly(ACCOUNTS.instructionsSysVar()),
        VECTORS.get("configureConfidentialTransferAccount.basic").accounts().get(2));
    assertEquals(readOnly(ACCOUNTS.systemProgram()), VECTORS.get("createNativeMint.bare").accounts().getLast());
    assertEquals(readOnly(ACCOUNTS.systemProgram()), VECTORS.get("reallocate.two-extensions").accounts().get(2));
  }

  // ---------------------------------------------------------------------------
  // Vector name to builder call
  // ---------------------------------------------------------------------------

  /// Exhaustive by construction: a vector with no case here fails the switch, and a case naming a
  /// vector that is not in the file fails the lookup in [#everyVectorMatchesTheReferenceEncoder].
  ///
  /// Every call is the key-argument overload, so the account list is the builder's own rather than
  /// one transcribed here.
  private static Instruction build(final String vector) {
    return switch (vector) {
      // Base token instructions
      case "initializeMint.with-freeze-authority" -> Token2022Program.initializeMint(
          PROGRAM, ACCOUNTS, MINT, DECIMALS, AUTHORITY, SECOND_AUTHORITY);
      case "initializeMint.no-freeze-authority" -> Token2022Program.initializeMint(
          PROGRAM, ACCOUNTS, MINT, DECIMALS, AUTHORITY, null);

      case "initializeAccount.basic" -> Token2022Program.initializeAccount(PROGRAM, ACCOUNTS, TOKEN, MINT, AUTHORITY);

      case "initializeMultisig.m3" -> Token2022Program.initializeMultisig(PROGRAM, ACCOUNTS, MULTISIG, M);

      case "transfer.amount" -> Token2022Program.transfer(PROGRAM, SOURCE, DESTINATION, AUTHORITY, AMOUNT);
      case "transfer.max-u64" -> Token2022Program.transfer(PROGRAM, SOURCE, DESTINATION, AUTHORITY, MAX_U64);

      case "approve.amount" -> Token2022Program.approve(PROGRAM, SOURCE, DELEGATE, AUTHORITY, AMOUNT);

      case "revoke.bare" -> Token2022Program.revoke(PROGRAM, SOURCE, AUTHORITY);

      case "setAuthority.mint-tokens" -> Token2022Program.setAuthority(
          PROGRAM, MINT, AUTHORITY, AuthorityType.mintTokens, NEW_AUTHORITY);
      case "setAuthority.close-account-none" -> Token2022Program.setAuthority(
          PROGRAM, TOKEN, AUTHORITY, AuthorityType.closeAccount, null);
      case "setAuthority.permissioned-burn" -> Token2022Program.setAuthority(
          PROGRAM, MINT, AUTHORITY, AuthorityType.permissionedBurn, NEW_AUTHORITY);

      case "mintTo.amount" -> Token2022Program.mintTo(PROGRAM, MINT, TOKEN, AUTHORITY, AMOUNT);
      case "burn.amount" -> Token2022Program.burn(PROGRAM, TOKEN, MINT, AUTHORITY, AMOUNT);
      case "closeAccount.bare" -> Token2022Program.closeAccount(PROGRAM, TOKEN, DESTINATION, AUTHORITY);
      case "freezeAccount.bare" -> Token2022Program.freezeAccount(PROGRAM, TOKEN, MINT, AUTHORITY);
      case "thawAccount.bare" -> Token2022Program.thawAccount(PROGRAM, TOKEN, MINT, AUTHORITY);

      case "transferChecked.amount" -> Token2022Program.transferChecked(
          PROGRAM, SOURCE, MINT, DESTINATION, AUTHORITY, AMOUNT, DECIMALS);
      case "approveChecked.amount" -> Token2022Program.approveChecked(
          PROGRAM, SOURCE, MINT, DELEGATE, AUTHORITY, AMOUNT, DECIMALS);
      case "mintToChecked.amount" -> Token2022Program.mintToChecked(
          PROGRAM, MINT, TOKEN, AUTHORITY, AMOUNT, DECIMALS);
      case "burnChecked.amount" -> Token2022Program.burnChecked(
          PROGRAM, TOKEN, MINT, AUTHORITY, AMOUNT, DECIMALS);

      case "syncNative.bare" -> Token2022Program.syncNative(PROGRAM, ACCOUNTS, TOKEN);
      case "getAccountDataSize.bare" -> Token2022Program.getAccountDataSize(PROGRAM, MINT);
      case "initializeImmutableOwner.bare" -> Token2022Program.initializeImmutableOwner(PROGRAM, TOKEN);
      case "amountToUiAmount.amount" -> Token2022Program.amountToUiAmount(PROGRAM, MINT, AMOUNT);

      case "uiAmountToAmount.decimal" -> Token2022Program.uiAmountToAmount(PROGRAM, MINT, UI_AMOUNT);
      case "uiAmountToAmount.multibyte" -> Token2022Program.uiAmountToAmount(PROGRAM, MINT, MULTIBYTE);
      case "uiAmountToAmount.empty" -> Token2022Program.uiAmountToAmount(PROGRAM, MINT, "");

      case "initializeAccount2.basic" -> Token2022Program.initializeAccount2(PROGRAM, ACCOUNTS, TOKEN, MINT, AUTHORITY);
      case "initializeAccount3.basic" -> Token2022Program.initializeAccount3(PROGRAM, TOKEN, MINT, AUTHORITY);
      case "initializeMultisig2.m3" -> Token2022Program.initializeMultisig2(PROGRAM, MULTISIG, M);
      case "initializeMint2.with-freeze-authority" -> Token2022Program.initializeMint2(
          PROGRAM, MINT, DECIMALS, AUTHORITY, SECOND_AUTHORITY);
      case "initializeMint2.no-freeze-authority" -> Token2022Program.initializeMint2(
          PROGRAM, MINT, DECIMALS, AUTHORITY, null);

      // Transfer fee extension
      case "initializeMintCloseAuthority.some" -> Token2022Program.initializeMintCloseAuthority(
          PROGRAM, MINT, NEW_AUTHORITY);
      case "initializeMintCloseAuthority.none" -> Token2022Program.initializeMintCloseAuthority(PROGRAM, MINT, null);

      case "initializeTransferFeeConfig.both-authorities" -> Token2022Program.initializeTransferFeeConfig(
          PROGRAM, MINT, AUTHORITY, SECOND_AUTHORITY, FEE_BASIS_POINTS, MAXIMUM_FEE);
      case "initializeTransferFeeConfig.no-authorities" -> Token2022Program.initializeTransferFeeConfig(
          PROGRAM, MINT, null, null, FEE_BASIS_POINTS, MAXIMUM_FEE);
      case "initializeTransferFeeConfig.config-authority-only" -> Token2022Program.initializeTransferFeeConfig(
          PROGRAM, MINT, AUTHORITY, null, FEE_BASIS_POINTS, MAXIMUM_FEE);
      case "initializeTransferFeeConfig.withdraw-authority-only" -> Token2022Program.initializeTransferFeeConfig(
          PROGRAM, MINT, null, SECOND_AUTHORITY, FEE_BASIS_POINTS, MAXIMUM_FEE);

      case "transferCheckedWithFee.fee" -> Token2022Program.transferCheckedWithFee(
          PROGRAM, SOURCE, MINT, DESTINATION, AUTHORITY, AMOUNT, DECIMALS, FEE);

      case "withdrawWithheldTokensFromMint.bare" -> Token2022Program.withdrawWithheldTokensFromMint(
          PROGRAM, MINT, FEE_RECEIVER, AUTHORITY);
      case "withdrawWithheldTokensFromAccounts.count" -> Token2022Program.withdrawWithheldTokensFromAccounts(
          PROGRAM, MINT, FEE_RECEIVER, AUTHORITY, NUM_TOKEN_ACCOUNTS);
      case "harvestWithheldTokensToMint.bare" -> Token2022Program.harvestWithheldTokensToMint(PROGRAM, MINT);
      case "setTransferFee.fee" -> Token2022Program.setTransferFee(
          PROGRAM, MINT, AUTHORITY, FEE_BASIS_POINTS, MAXIMUM_FEE);

      // Confidential transfer extension
      case "initializeConfidentialTransferMint.auto-approve" -> Token2022Program.initializeConfidentialTransferMint(
          PROGRAM, MINT, AUTHORITY, true, ELGAMAL_PUBKEY);
      case "initializeConfidentialTransferMint.no-auto-approve" -> Token2022Program.initializeConfidentialTransferMint(
          PROGRAM, MINT, null, false, null);

      case "updateConfidentialTransferMint.enable" -> Token2022Program.updateConfidentialTransferMint(
          PROGRAM, MINT, AUTHORITY, true, ELGAMAL_PUBKEY);
      case "updateConfidentialTransferMint.disable" -> Token2022Program.updateConfidentialTransferMint(
          PROGRAM, MINT, AUTHORITY, false, null);

      case "configureConfidentialTransferAccount.basic" -> Token2022Program.configureConfidentialTransferAccount(
          PROGRAM, ACCOUNTS, TOKEN, MINT, AUTHORITY, AVAILABLE_BALANCE, MAX_PENDING_CREDITS, PROOF_OFFSET);
      case "approveConfidentialTransferAccount.bare" -> Token2022Program.approveConfidentialTransferAccount(
          PROGRAM, TOKEN, MINT, AUTHORITY);
      case "emptyConfidentialTransferAccount.offset" -> Token2022Program.emptyConfidentialTransferAccount(
          PROGRAM, ACCOUNTS, TOKEN, AUTHORITY, PROOF_OFFSET);
      case "emptyConfidentialTransferAccount.negative-offset" -> Token2022Program.emptyConfidentialTransferAccount(
          PROGRAM, ACCOUNTS, TOKEN, AUTHORITY, NEGATIVE_PROOF_OFFSET);

      case "confidentialDeposit.amount" -> Token2022Program.confidentialDeposit(
          PROGRAM, TOKEN, MINT, AUTHORITY, AMOUNT, DECIMALS);

      case "confidentialWithdraw.all-records" -> Token2022Program.confidentialWithdraw(
          PROGRAM, TOKEN, MINT, ACCOUNTS.instructionsSysVar(), EQUALITY_RECORD, RANGE_RECORD, AUTHORITY,
          AMOUNT, DECIMALS, AVAILABLE_BALANCE, NEGATIVE_PROOF_OFFSET, PROOF_OFFSET);
      case "confidentialWithdraw.no-records" -> Token2022Program.confidentialWithdraw(
          PROGRAM, TOKEN, MINT, null, null, null, AUTHORITY,
          AMOUNT, DECIMALS, AVAILABLE_BALANCE, NEGATIVE_PROOF_OFFSET, PROOF_OFFSET);

      case "confidentialTransfer.all-records" -> Token2022Program.confidentialTransfer(
          PROGRAM, SOURCE, MINT, DESTINATION, ACCOUNTS.instructionsSysVar(),
          EQUALITY_RECORD, VALIDITY_RECORD, RANGE_RECORD, AUTHORITY,
          AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "confidentialTransfer.no-records" -> Token2022Program.confidentialTransfer(
          PROGRAM, SOURCE, MINT, DESTINATION, null, null, null, null, AUTHORITY,
          AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);

      case "applyConfidentialPendingBalance.counter" -> Token2022Program.applyConfidentialPendingBalance(
          PROGRAM, TOKEN, AUTHORITY, CREDIT_COUNTER, AVAILABLE_BALANCE);

      case "enableConfidentialCredits.bare" -> Token2022Program.enableConfidentialCredits(PROGRAM, TOKEN, AUTHORITY);
      case "disableConfidentialCredits.bare" -> Token2022Program.disableConfidentialCredits(PROGRAM, TOKEN, AUTHORITY);
      case "enableNonConfidentialCredits.bare" -> Token2022Program.enableNonConfidentialCredits(
          PROGRAM, TOKEN, AUTHORITY);
      case "disableNonConfidentialCredits.bare" -> Token2022Program.disableNonConfidentialCredits(
          PROGRAM, TOKEN, AUTHORITY);

      case "confidentialTransferWithFee.all-records" -> Token2022Program.confidentialTransferWithFee(
          PROGRAM, SOURCE, MINT, DESTINATION, ACCOUNTS.instructionsSysVar(), EQUALITY_RECORD, VALIDITY_RECORD,
          FEE_SIGMA_RECORD, FEE_VALIDITY_RECORD, RANGE_RECORD, AUTHORITY,
          AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "confidentialTransferWithFee.no-records" -> Token2022Program.confidentialTransferWithFee(
          PROGRAM, SOURCE, MINT, DESTINATION, null, null, null, null, null, null, AUTHORITY,
          AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);

      case "configureConfidentialTransferAccountWithRegistry.with-payer" ->
          Token2022Program.configureConfidentialTransferAccountWithRegistry(
              PROGRAM, TOKEN, MINT, ELGAMAL_REGISTRY, PAYER, ACCOUNTS.systemProgram());
      case "configureConfidentialTransferAccountWithRegistry.no-payer" ->
          Token2022Program.configureConfidentialTransferAccountWithRegistry(
              PROGRAM, TOKEN, MINT, ELGAMAL_REGISTRY, null, null);

      // Default account state, memo transfer, CPI guard, reallocate
      case "initializeDefaultAccountState.frozen" -> Token2022Program.initializeDefaultAccountState(
          PROGRAM, MINT, AccountState.frozen);
      case "initializeDefaultAccountState.uninitialized" -> Token2022Program.initializeDefaultAccountState(
          PROGRAM, MINT, AccountState.uninitialized);
      case "updateDefaultAccountState.initialized" -> Token2022Program.updateDefaultAccountState(
          PROGRAM, MINT, AUTHORITY, AccountState.initialized);

      case "reallocate.two-extensions" -> Token2022Program.reallocate(PROGRAM, ACCOUNTS, TOKEN, PAYER, AUTHORITY,
          new ExtensionType[]{ExtensionType.memoTransfer, ExtensionType.cpiGuard});
      case "reallocate.empty" -> Token2022Program.reallocate(PROGRAM, ACCOUNTS, TOKEN, PAYER, AUTHORITY,
          new ExtensionType[0]);
      case "reallocate.high-ordinal" -> Token2022Program.reallocate(PROGRAM, ACCOUNTS, TOKEN, PAYER, AUTHORITY,
          new ExtensionType[]{ExtensionType.permissionedBurn});

      case "enableMemoTransfers.bare" -> Token2022Program.enableMemoTransfers(PROGRAM, TOKEN, AUTHORITY);
      case "disableMemoTransfers.bare" -> Token2022Program.disableMemoTransfers(PROGRAM, TOKEN, AUTHORITY);

      case "createNativeMint.bare" -> Token2022Program.createNativeMint(PROGRAM, ACCOUNTS, PAYER, NATIVE_MINT);
      case "initializeNonTransferableMint.bare" -> Token2022Program.initializeNonTransferableMint(PROGRAM, MINT);

      case "initializeInterestBearingMint.positive-rate" -> Token2022Program.initializeInterestBearingMint(
          PROGRAM, MINT, AUTHORITY, RATE);
      case "initializeInterestBearingMint.negative-rate" -> Token2022Program.initializeInterestBearingMint(
          PROGRAM, MINT, AUTHORITY, NEGATIVE_RATE);
      case "initializeInterestBearingMint.no-authority" -> Token2022Program.initializeInterestBearingMint(
          PROGRAM, MINT, null, RATE);
      case "updateRateInterestBearingMint.negative-rate" -> Token2022Program.updateRateInterestBearingMint(
          PROGRAM, MINT, AUTHORITY, NEGATIVE_RATE);

      case "enableCpiGuard.bare" -> Token2022Program.enableCpiGuard(PROGRAM, TOKEN, AUTHORITY);
      case "disableCpiGuard.bare" -> Token2022Program.disableCpiGuard(PROGRAM, TOKEN, AUTHORITY);
      case "initializePermanentDelegate.bare" -> Token2022Program.initializePermanentDelegate(PROGRAM, MINT, DELEGATE);

      // Transfer hook, confidential transfer fee, excess lamports
      case "initializeTransferHook.both" -> Token2022Program.initializeTransferHook(
          PROGRAM, MINT, AUTHORITY, HOOK_PROGRAM);
      case "initializeTransferHook.none" -> Token2022Program.initializeTransferHook(PROGRAM, MINT, null, null);
      case "initializeTransferHook.authority-only" -> Token2022Program.initializeTransferHook(
          PROGRAM, MINT, AUTHORITY, null);
      case "updateTransferHook.some" -> Token2022Program.updateTransferHook(PROGRAM, MINT, AUTHORITY, HOOK_PROGRAM);
      case "updateTransferHook.none" -> Token2022Program.updateTransferHook(PROGRAM, MINT, AUTHORITY, null);

      case "initializeConfidentialTransferFee.some-authority" -> Token2022Program.initializeConfidentialTransferFee(
          PROGRAM, MINT, AUTHORITY, ELGAMAL_PUBKEY);
      case "initializeConfidentialTransferFee.no-authority" -> Token2022Program.initializeConfidentialTransferFee(
          PROGRAM, MINT, null, ELGAMAL_PUBKEY);

      case "withdrawWithheldTokensFromMintForConfidentialTransferFee.offset" ->
          Token2022Program.withdrawWithheldTokensFromMintForConfidentialTransferFee(
              PROGRAM, MINT, DESTINATION, ACCOUNTS.instructionsSysVar(), AUTHORITY,
              NEGATIVE_PROOF_OFFSET, AVAILABLE_BALANCE);
      case "withdrawWithheldTokensFromAccountsForConfidentialTransferFee.offset" ->
          Token2022Program.withdrawWithheldTokensFromAccountsForConfidentialTransferFee(
              PROGRAM, MINT, DESTINATION, ACCOUNTS.instructionsSysVar(), AUTHORITY,
              NUM_TOKEN_ACCOUNTS, NEGATIVE_PROOF_OFFSET, AVAILABLE_BALANCE);
      case "harvestWithheldTokensToMintForConfidentialTransferFee.bare" ->
          Token2022Program.harvestWithheldTokensToMintForConfidentialTransferFee(PROGRAM, MINT);

      case "enableHarvestToMint.bare" -> Token2022Program.enableHarvestToMint(PROGRAM, MINT, AUTHORITY);
      case "disableHarvestToMint.bare" -> Token2022Program.disableHarvestToMint(PROGRAM, MINT, AUTHORITY);
      case "withdrawExcessLamports.bare" -> Token2022Program.withdrawExcessLamports(
          PROGRAM, SOURCE, DESTINATION, AUTHORITY);

      // Pointer extensions
      case "initializeMetadataPointer.both" -> Token2022Program.initializeMetadataPointer(
          PROGRAM, MINT, AUTHORITY, POINTER_ADDRESS);
      case "initializeMetadataPointer.none" -> Token2022Program.initializeMetadataPointer(PROGRAM, MINT, null, null);
      case "initializeMetadataPointer.address-only" -> Token2022Program.initializeMetadataPointer(
          PROGRAM, MINT, null, POINTER_ADDRESS);
      case "updateMetadataPointer.some" -> Token2022Program.updateMetadataPointer(
          PROGRAM, MINT, AUTHORITY, POINTER_ADDRESS);
      case "updateMetadataPointer.none" -> Token2022Program.updateMetadataPointer(PROGRAM, MINT, AUTHORITY, null);

      case "initializeGroupPointer.both" -> Token2022Program.initializeGroupPointer(
          PROGRAM, MINT, AUTHORITY, POINTER_ADDRESS);
      case "initializeGroupPointer.none" -> Token2022Program.initializeGroupPointer(PROGRAM, MINT, null, null);
      case "updateGroupPointer.some" -> Token2022Program.updateGroupPointer(
          PROGRAM, MINT, AUTHORITY, POINTER_ADDRESS);
      case "updateGroupPointer.none" -> Token2022Program.updateGroupPointer(PROGRAM, MINT, AUTHORITY, null);

      case "initializeGroupMemberPointer.both" -> Token2022Program.initializeGroupMemberPointer(
          PROGRAM, MINT, AUTHORITY, POINTER_ADDRESS);
      case "initializeGroupMemberPointer.none" -> Token2022Program.initializeGroupMemberPointer(
          PROGRAM, MINT, null, null);
      case "updateGroupMemberPointer.some" -> Token2022Program.updateGroupMemberPointer(
          PROGRAM, MINT, AUTHORITY, POINTER_ADDRESS);
      case "updateGroupMemberPointer.none" -> Token2022Program.updateGroupMemberPointer(PROGRAM, MINT, AUTHORITY, null);

      // Confidential mint/burn extension
      case "initializeConfidentialMintBurn.basic" -> Token2022Program.initializeConfidentialMintBurn(
          PROGRAM, MINT, ELGAMAL_PUBKEY, SUPPLY_BALANCE);
      case "rotateSupplyElgamalPubkey.offset" -> Token2022Program.rotateSupplyElgamalPubkey(
          PROGRAM, ACCOUNTS, MINT, AUTHORITY, ELGAMAL_PUBKEY, NEGATIVE_PROOF_OFFSET);
      case "updateConfidentialMintBurnDecryptableSupply.basic" ->
          Token2022Program.updateConfidentialMintBurnDecryptableSupply(PROGRAM, MINT, AUTHORITY, SUPPLY_BALANCE);

      case "confidentialMint.all-records" -> Token2022Program.confidentialMint(
          PROGRAM, TOKEN, MINT, ACCOUNTS.instructionsSysVar(), EQUALITY_RECORD, VALIDITY_RECORD, RANGE_RECORD,
          AUTHORITY, SUPPLY_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "confidentialMint.no-records" -> Token2022Program.confidentialMint(
          PROGRAM, TOKEN, MINT, null, null, null, null,
          AUTHORITY, SUPPLY_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "confidentialBurn.all-records" -> Token2022Program.confidentialBurn(
          PROGRAM, TOKEN, MINT, ACCOUNTS.instructionsSysVar(), EQUALITY_RECORD, VALIDITY_RECORD, RANGE_RECORD,
          AUTHORITY, AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "confidentialBurn.no-records" -> Token2022Program.confidentialBurn(
          PROGRAM, TOKEN, MINT, null, null, null, null,
          AUTHORITY, AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "applyConfidentialPendingBurn.bare" -> Token2022Program.applyConfidentialPendingBurn(
          PROGRAM, MINT, AUTHORITY);

      // Scaled UI amount and pausable extensions
      case "initializeScaledUiAmountMint.multiplier" -> Token2022Program.initializeScaledUiAmountMint(
          PROGRAM, MINT, AUTHORITY, MULTIPLIER);
      case "initializeScaledUiAmountMint.negative-multiplier" -> Token2022Program.initializeScaledUiAmountMint(
          PROGRAM, MINT, AUTHORITY, NEGATIVE_MULTIPLIER);
      case "initializeScaledUiAmountMint.no-authority" -> Token2022Program.initializeScaledUiAmountMint(
          PROGRAM, MINT, null, MULTIPLIER);
      case "updateMultiplierScaledUiMint.multiplier" -> Token2022Program.updateMultiplierScaledUiMint(
          PROGRAM, MINT, AUTHORITY, MULTIPLIER, EFFECTIVE_TIMESTAMP);
      case "updateMultiplierScaledUiMint.negative-timestamp" -> Token2022Program.updateMultiplierScaledUiMint(
          PROGRAM, MINT, AUTHORITY, NEGATIVE_MULTIPLIER, -1L);

      case "initializePausableConfig.some" -> Token2022Program.initializePausableConfig(PROGRAM, MINT, AUTHORITY);
      case "initializePausableConfig.none" -> Token2022Program.initializePausableConfig(PROGRAM, MINT, null);
      case "pause.bare" -> Token2022Program.pause(PROGRAM, MINT, AUTHORITY);
      case "resume.bare" -> Token2022Program.resume(PROGRAM, MINT, AUTHORITY);

      // Token metadata interface
      case "initializeTokenMetadata.basic" -> Token2022Program.initializeTokenMetadata(
          PROGRAM, METADATA, NEW_AUTHORITY, MINT, AUTHORITY, NAME, SYMBOL, URI);
      case "initializeTokenMetadata.multibyte" -> Token2022Program.initializeTokenMetadata(
          PROGRAM, METADATA, NEW_AUTHORITY, MINT, AUTHORITY, MULTIBYTE, SYMBOL, URI);
      case "initializeTokenMetadata.empty" -> Token2022Program.initializeTokenMetadata(
          PROGRAM, METADATA, NEW_AUTHORITY, MINT, AUTHORITY, "", "", "");

      case "updateTokenMetadataField.name" -> Token2022Program.updateTokenMetadataField(
          PROGRAM, METADATA, AUTHORITY, TokenMetadataField.name.INSTANCE, NAME);
      case "updateTokenMetadataField.symbol" -> Token2022Program.updateTokenMetadataField(
          PROGRAM, METADATA, AUTHORITY, TokenMetadataField.symbol.INSTANCE, SYMBOL);
      case "updateTokenMetadataField.uri" -> Token2022Program.updateTokenMetadataField(
          PROGRAM, METADATA, AUTHORITY, TokenMetadataField.uri.INSTANCE, URI);
      case "updateTokenMetadataField.key" -> Token2022Program.updateTokenMetadataField(
          PROGRAM, METADATA, AUTHORITY, TokenMetadataField.key.createRecord(FIELD_KEY), URI);
      case "updateTokenMetadataField.multibyte-key" -> Token2022Program.updateTokenMetadataField(
          PROGRAM, METADATA, AUTHORITY, TokenMetadataField.key.createRecord(MULTIBYTE), MULTIBYTE);

      case "removeTokenMetadataKey.idempotent" -> Token2022Program.removeTokenMetadataKey(
          PROGRAM, METADATA, AUTHORITY, true, FIELD_KEY);
      case "removeTokenMetadataKey.not-idempotent" -> Token2022Program.removeTokenMetadataKey(
          PROGRAM, METADATA, AUTHORITY, false, FIELD_KEY);
      case "removeTokenMetadataKey.multibyte-key" -> Token2022Program.removeTokenMetadataKey(
          PROGRAM, METADATA, AUTHORITY, true, MULTIBYTE);

      case "updateTokenMetadataUpdateAuthority.some" -> Token2022Program.updateTokenMetadataUpdateAuthority(
          PROGRAM, METADATA, AUTHORITY, NEW_AUTHORITY);
      case "updateTokenMetadataUpdateAuthority.none" -> Token2022Program.updateTokenMetadataUpdateAuthority(
          PROGRAM, METADATA, AUTHORITY, null);

      case "emitTokenMetadata.range" -> Token2022Program.emitTokenMetadata(
          PROGRAM, METADATA, OptionalLong.of(START), OptionalLong.of(END));
      case "emitTokenMetadata.none" -> Token2022Program.emitTokenMetadata(
          PROGRAM, METADATA, OptionalLong.empty(), OptionalLong.empty());
      case "emitTokenMetadata.start-only" -> Token2022Program.emitTokenMetadata(
          PROGRAM, METADATA, OptionalLong.of(START), OptionalLong.empty());
      case "emitTokenMetadata.end-only" -> Token2022Program.emitTokenMetadata(
          PROGRAM, METADATA, OptionalLong.empty(), OptionalLong.of(END));

      // Token group interface
      case "initializeTokenGroup.some-authority" -> Token2022Program.initializeTokenGroup(
          PROGRAM, GROUP, MINT, AUTHORITY, NEW_AUTHORITY, MAX_SIZE);
      case "initializeTokenGroup.no-authority" -> Token2022Program.initializeTokenGroup(
          PROGRAM, GROUP, MINT, AUTHORITY, null, MAX_SIZE);
      case "updateTokenGroupMaxSize.max-size" -> Token2022Program.updateTokenGroupMaxSize(
          PROGRAM, GROUP, AUTHORITY, MAX_SIZE);
      case "updateTokenGroupUpdateAuthority.some" -> Token2022Program.updateTokenGroupUpdateAuthority(
          PROGRAM, GROUP, AUTHORITY, NEW_AUTHORITY);
      case "updateTokenGroupUpdateAuthority.none" -> Token2022Program.updateTokenGroupUpdateAuthority(
          PROGRAM, GROUP, AUTHORITY, null);
      case "initializeTokenGroupMember.bare" -> Token2022Program.initializeTokenGroupMember(
          PROGRAM, MEMBER, MINT, AUTHORITY, GROUP, NEW_AUTHORITY);

      // Unwrap lamports and the permissioned burn extension
      case "unwrapLamports.amount" -> Token2022Program.unwrapLamports(
          PROGRAM, SOURCE, DESTINATION, AUTHORITY, OptionalLong.of(AMOUNT));
      case "unwrapLamports.none" -> Token2022Program.unwrapLamports(
          PROGRAM, SOURCE, DESTINATION, AUTHORITY, OptionalLong.empty());

      case "initializePermissionedBurn.bare" -> Token2022Program.initializePermissionedBurn(PROGRAM, MINT, AUTHORITY);
      case "permissionedBurn.amount" -> Token2022Program.permissionedBurn(
          PROGRAM, TOKEN, MINT, SECOND_AUTHORITY, AUTHORITY, AMOUNT);
      case "permissionedBurnChecked.amount" -> Token2022Program.permissionedBurnChecked(
          PROGRAM, TOKEN, MINT, SECOND_AUTHORITY, AUTHORITY, AMOUNT, DECIMALS);
      case "permissionedConfidentialBurn.all-records" -> Token2022Program.permissionedConfidentialBurn(
          PROGRAM, TOKEN, MINT, ACCOUNTS.instructionsSysVar(), EQUALITY_RECORD, VALIDITY_RECORD, RANGE_RECORD,
          SECOND_AUTHORITY, AUTHORITY, AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);
      case "permissionedConfidentialBurn.no-records" -> Token2022Program.permissionedConfidentialBurn(
          PROGRAM, TOKEN, MINT, null, null, null, null,
          SECOND_AUTHORITY, AUTHORITY, AVAILABLE_BALANCE, AUDITOR_LO, AUDITOR_HI,
          NEGATIVE_PROOF_OFFSET, PROOF_OFFSET, NEGATIVE_PROOF_OFFSET);

      default -> throw new IllegalStateException(
          vector + " has no builder call — add one, or drop the vector from tools/token2022-vectors.mjs");
    };
  }

  // ---------------------------------------------------------------------------
  // Fixture plumbing
  // ---------------------------------------------------------------------------

  /// One vector: the instruction data upstream encoded, and the account list upstream's builder
  /// produced, each account rendered as `<base58>:<flags>` exactly as the file records it.
  private record Vector(byte[] data, List<String> accounts) {
  }

  /// Byte `i` is `(seed + i * 7) & 0xff`, mirroring `k()` in `tools/token2022-vectors.mjs`. Every
  /// byte of the key differs from its neighbours, so a pubkey written backwards or shifted by one
  /// is a mismatch — which a key of repeated bytes, the convention elsewhere in these tests, would
  /// not catch.
  private static PublicKey key(final int seed) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    for (int i = 0; i < publicKey.length; ++i) {
      publicKey[i] = (byte) (seed + i * 7);
    }
    return PublicKey.createPubKey(publicKey);
  }

  /// Byte `i` is `1 + ((seed + i * 7) % 255)`, mirroring `cipher()` in
  /// `tools/token2022-vectors.mjs`. No byte is zero, so a short copy shows as zero fill and a long
  /// one runs into the next field — which is the only way a fixed-size unprefixed field can be seen
  /// to be the wrong size at all.
  private static byte[] cipher(final int seed, final int length) {
    final byte[] bytes = new byte[length];
    for (int i = 0; i < length; ++i) {
      bytes[i] = (byte) (1 + ((seed + i * 7) % 255));
    }
    return bytes;
  }

  private static byte[] data(final Instruction ix) {
    return Arrays.copyOfRange(ix.data(), ix.offset(), ix.offset() + ix.len());
  }

  private static List<String> accounts(final Instruction ix) {
    return ix.accounts().stream().map(Token2022ReferenceEncodingTests::render).toList();
  }

  private static String render(final AccountMeta account) {
    final var flags = (account.write() ? "w" : "") + (account.signer() ? "s" : "");
    return account.publicKey().toBase58() + ':' + (flags.isEmpty() ? "-" : flags);
  }

  private static String readOnly(final PublicKey account) {
    return account.toBase58() + ":-";
  }

  private static String signerFlag(final PublicKey account) {
    return account.toBase58() + ":s";
  }

  private static String rentFlag() {
    return readOnly(ACCOUNTS.rentSysVar());
  }

  private static long longAt(final byte[] data, final int offset) {
    long value = 0;
    for (int i = 7; i >= 0; --i) {
      value = (value << 8) | (data[offset + i] & 0xFFL);
    }
    return value;
  }

  /// Decode with the generated `<Instruction>IxData` record and write it straight back out.
  ///
  /// Looked up by name so that this covers every instruction without a second 155-case switch, and
  /// so that a missing or renamed record is a failure rather than a gap.
  private static byte[] reserialize(final String instruction, final byte[] data) {
    final var simpleName = Character.toUpperCase(instruction.charAt(0)) + instruction.substring(1) + "IxData";
    final var type = Arrays.stream(Token2022Program.class.getDeclaredClasses())
        .filter(declared -> declared.getSimpleName().equals(simpleName))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Token2022Program declares no " + simpleName));
    try {
      final var decoded = type.getMethod("read", byte[].class, int.class).invoke(null, new Object[]{data, 0});
      final int length = (int) type.getMethod("l").invoke(decoded);
      final byte[] rewritten = new byte[length];
      type.getMethod("write", byte[].class, int.class).invoke(decoded, new Object[]{rewritten, 0});
      return rewritten;
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException(simpleName + " could not read and rewrite the reference bytes", e);
    }
  }

  private static Set<String> declaredInstructions() {
    return Arrays.stream(Token2022Program.class.getFields())
        .filter(field -> field.getType() == Discriminator.class)
        .map(Field::getName)
        .map(Token2022ReferenceEncodingTests::instructionName)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  /// `INITIALIZE_ACCOUNT_2_DISCRIMINATOR` to `initializeAccount2`, the name the vectors use.
  private static String instructionName(final String constant) {
    final var words = constant.substring(0, constant.lastIndexOf("_DISCRIMINATOR")).split("_");
    final var name = new StringBuilder(words[0].toLowerCase(Locale.ROOT));
    for (int i = 1; i < words.length; ++i) {
      name.append(words[i].charAt(0)).append(words[i].substring(1).toLowerCase(Locale.ROOT));
    }
    return name.toString();
  }

  private static Map<String, Vector> load() {
    final var resource = "/token_2022/reference-vectors.txt";
    try (var in = Token2022ReferenceEncodingTests.class.getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalStateException("fixture " + resource + " is missing");
      }
      final var vectors = new LinkedHashMap<String, Vector>();
      try (var reader = new BufferedReader(new InputStreamReader(in, UTF_8))) {
        for (String line; (line = reader.readLine()) != null; ) {
          line = line.strip();
          if (line.isEmpty() || line.charAt(0) == '#') {
            continue;
          }
          final var fields = line.split(" ");
          if (fields.length != 3) {
            throw new IllegalStateException(
                "expected \"<name> <base64> <accounts>\" but read: " + line);
          }
          final var name = fields[0];
          if (name.indexOf('.') < 0) {
            throw new IllegalStateException("vector " + name + " is not named <instruction>.<case>");
          }
          final var vector = new Vector(Base64.getDecoder().decode(fields[1]), List.of(fields[2].split(",")));
          if (vectors.put(name, vector) != null) {
            throw new IllegalStateException("duplicate vector " + name);
          }
        }
      }
      // insertion order, so a batch of failures reads in the order the file lists them
      return Collections.unmodifiableMap(vectors);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
