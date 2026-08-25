package software.sava.idl.clients.spl.stake;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.stake.gen.StakeProgram;
import software.sava.idl.clients.spl.stake.gen.types.Authorized;
import software.sava.idl.clients.spl.stake.gen.types.Epoch;
import software.sava.idl.clients.spl.stake.gen.types.Lockup;
import software.sava.idl.clients.spl.stake.gen.types.StakeAuthorize;
import software.sava.idl.clients.spl.stake.gen.types.UnixTimestamp;

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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Differential coverage for the Stake program's instruction data, against an encoder this
/// repository did not write.
///
/// Every other Stake test is a round trip: an instruction is built with the generated builder and
/// read back with the generated `IxData`. Both are emitted from the same IDL by the same
/// generator, so they agree by construction — a systematic change to the wire format (the
/// discriminator's width, a size prefix's width, the order of two fields) moves the writer and
/// the reader together and leaves the round trip passing. It is not a weak test, it is a test
/// that structurally cannot see this class of change.
///
/// The bytes in `/stake/reference-vectors.txt` come from `solana-program/stake`'s own generated
/// JavaScript client, which `@codama/renderers-js` renders from the same `idl.json` this
/// repository generates from — an independent implementation of the same specification.
/// `tools/stake-vectors.mjs` regenerates them.
///
/// What this does **not** establish is that upstream's encoder matches the deployed program.
/// Nothing here could: both sides descend from the same IDL. That link is
/// [StakeOnChainInstructionTests], which holds one real mainnet `Initialize` — and it is the
/// reason this file is a supplement to that fixture rather than a replacement for it.
final class StakeReferenceEncodingTests {

  private static final AccountMeta PROGRAM = SolanaAccounts.MAIN_NET.invokedStakeProgram();

  /// The data-only builder overloads. Accounts are covered by `SPLClientTests`; an empty key list
  /// keeps this file about the bytes.
  private static final List<AccountMeta> NO_KEYS = List.of();

  private static final PublicKey STAKER = key(0x11);
  private static final PublicKey WITHDRAWER = key(0x22);
  private static final PublicKey CUSTODIAN = key(0x33);
  private static final PublicKey NEW_AUTHORITY = key(0x44);
  private static final PublicKey SEEDED_NEW_AUTHORITY = key(0x55);
  private static final PublicKey AUTHORITY_OWNER = key(0x66);
  private static final PublicKey CHECKED_AUTHORITY_OWNER = key(0x77);

  private static final long LOCKUP_TIMESTAMP = 1_700_000_000L;
  private static final long LOCKUP_EPOCH = 512L;

  /// `"seed-"` followed by U+00E9, U+20AC and U+1D11E: a 2-, a 3- and a 4-byte UTF-8 sequence, so
  /// a seed measured in `String.length()` (8) rather than in encoded bytes (14) is a mismatch.
  /// Written as escapes, and matched by escapes in `tools/stake-vectors.mjs`, so that neither the
  /// vector nor the assertion depends on how a build reads this file.
  private static final String MULTIBYTE_SEED = "seed-\u00e9\u20ac\ud834\udd1e";

  /// The longest seed `PublicKey::create_with_seed` accepts.
  private static final String MAX_LENGTH_SEED = "x".repeat(32);

  private static final Map<String, byte[]> VECTORS = load();

  // ---------------------------------------------------------------------------
  // The comparison
  // ---------------------------------------------------------------------------

  /// Every vector, built through the generated builder and compared byte for byte.
  ///
  /// Mismatches are collected rather than thrown one at a time: a wire-format change breaks many
  /// vectors at once, and the shape of *which* ones is the diagnosis. One instruction failing is a
  /// field; every seeded instruction failing is the size prefix; every vector failing is the
  /// discriminator.
  @Test
  void everyVectorMatchesTheReferenceEncoder() {
    final var failures = new ArrayList<String>();
    VECTORS.forEach((name, expected) -> {
      final byte[] actual = data(build(name));
      if (!Arrays.equals(expected, actual)) {
        failures.add(name
            + "\n  upstream " + HexFormat.of().formatHex(expected)
            + "\n  ours     " + HexFormat.of().formatHex(actual));
      }
    });
    assertTrue(failures.isEmpty(),
        () -> failures.size() + " of " + VECTORS.size()
            + " vectors disagree with solana-program/stake's client:\n" + String.join("\n", failures));
  }

  /// A new instruction upstream has to arrive with a vector, or this fails until it does.
  ///
  /// Coverage is asserted against what [StakeProgram] declares rather than against a list written
  /// here, because a list written here would be updated by the same edit that forgot the vector.
  @Test
  void everyInstructionHasAVector() {
    final var covered = VECTORS.keySet().stream()
        .map(name -> name.substring(0, name.indexOf('.')))
        .collect(Collectors.toCollection(TreeSet::new));
    final var declared = declaredInstructions();

    assertEquals(17, declared.size(), "solana-program/stake declares 17 instructions");
    assertEquals(declared, covered,
        "every instruction StakeProgram declares needs at least one vector, and every vector "
            + "needs to name an instruction that exists");
  }

  // ---------------------------------------------------------------------------
  // What the priority vectors pin, asserted against the reference bytes themselves
  // ---------------------------------------------------------------------------

  /// `authoritySeed` is a `sizePrefixTypeNode` whose prefix is a **u64**, not the u32 a length
  /// prefix usually is. On little-endian hardware a u32 prefix encodes identically for any seed
  /// shorter than 4 GiB and then places `authorityOwner` four bytes early, so the width only shows
  /// in the total length — which is what these assertions read off upstream's own bytes.
  ///
  /// This is the width behind the regression recorded on
  /// `SPLClientTests.authorizeStakeAccountWithSeed`: `l()` omitted the prefix its `write()`
  /// emitted, so the caller under-allocated by exactly eight bytes.
  @Test
  void theSeedLengthPrefixIsEightBytes() {
    final byte[] empty = VECTORS.get("authorizeWithSeed.empty-seed");
    assertEquals(4 + 32 + 4 + 8 + 32, empty.length,
        "u32 discriminator, newAuthorizedPubkey, u32 StakeAuthorize, u64 seed length, authorityOwner");
    for (int i = 40; i < 48; ++i) {
      assertEquals(0, empty[i], "byte " + i + " of an empty seed's length prefix");
    }

    // Each seed lengthens the instruction by its encoded byte count and nothing else.
    assertEquals(empty.length + 14, VECTORS.get("authorizeWithSeed.staker").length, "\"authority-seed\"");
    assertEquals(empty.length + 7, VECTORS.get("authorizeWithSeed.withdrawer").length, "\"stake:9\"");
    assertEquals(empty.length + 32, VECTORS.get("authorizeWithSeed.max-length-seed").length, MAX_LENGTH_SEED);
    assertEquals(empty.length + 14, VECTORS.get("authorizeWithSeed.multibyte-seed").length,
        "the seed is 14 UTF-8 bytes, not its 8 chars");

    final byte[] checkedEmpty = VECTORS.get("authorizeCheckedWithSeed.empty-seed");
    assertEquals(4 + 4 + 8 + 32, checkedEmpty.length,
        "the checked variant drops newAuthorizedPubkey, so the seed starts at offset 8");
    assertEquals(checkedEmpty.length + 12, VECTORS.get("authorizeCheckedWithSeed.withdrawer").length,
        "\"checked-seed\"");
    assertEquals(checkedEmpty.length + 14, VECTORS.get("authorizeCheckedWithSeed.multibyte-seed").length);
    assertEquals(checkedEmpty.length + 32, VECTORS.get("authorizeCheckedWithSeed.max-length-seed").length);
  }

  /// `l()` has to agree with an independently produced length, not just with our own `write()`.
  ///
  /// This is the assertion the recorded regression would have failed: the seeded `…Args.l()` was
  /// short by the eight bytes of the size prefix, and every round trip in this repository still
  /// passed, because the reader was short by the same eight.
  @Test
  void decodedLengthsAccountForEveryReferenceByte() {
    for (final var name : List.of(
        "authorizeWithSeed.staker",
        "authorizeWithSeed.withdrawer",
        "authorizeWithSeed.empty-seed",
        "authorizeWithSeed.multibyte-seed",
        "authorizeWithSeed.max-length-seed")) {
      final byte[] data = VECTORS.get(name);
      assertEquals(data.length, StakeProgram.AuthorizeWithSeedIxData.read(data, 0).l(), name);
    }
    for (final var name : List.of(
        "authorizeCheckedWithSeed.withdrawer",
        "authorizeCheckedWithSeed.empty-seed",
        "authorizeCheckedWithSeed.multibyte-seed",
        "authorizeCheckedWithSeed.max-length-seed")) {
      final byte[] data = VECTORS.get(name);
      assertEquals(data.length, StakeProgram.AuthorizeCheckedWithSeedIxData.read(data, 0).l(), name);
    }
  }

  /// `SetLockup`'s three fields were one wrapped `LockupArgs` before upstream's pipeline flattened
  /// them into separate parameters; each is now an independent `Option` whose absent form is a
  /// single zero byte, and `null` means "leave this one alone" rather than "set it to zero".
  ///
  /// The lengths below are the whole difference between those two readings. A `null` written as a
  /// present zero would make every one of them longer, and would clear a lockup that the caller
  /// asked to preserve.
  @Test
  void anAbsentLockupFieldIsOneZeroByte() {
    // All eight presence combinations, so no field's offset is left to inference.
    assertEquals(4 + 1 + 1 + 1, VECTORS.get("setLockup.none").length, "three absent fields");
    assertEquals(4 + 9 + 1 + 1, VECTORS.get("setLockup.timestamp-only").length);
    assertEquals(4 + 1 + 9 + 1, VECTORS.get("setLockup.epoch-only").length);
    assertEquals(4 + 1 + 1 + 33, VECTORS.get("setLockup.custodian-only").length);
    assertEquals(4 + 9 + 1 + 33, VECTORS.get("setLockup.timestamp-and-custodian").length);
    assertEquals(4 + 1 + 9 + 33, VECTORS.get("setLockup.epoch-and-custodian").length);
    assertEquals(4 + 9 + 9 + 1, VECTORS.get("setLockup.negative-timestamp").length);
    assertEquals(4 + 9 + 9 + 33, VECTORS.get("setLockup.all").length);

    // The custodian's presence byte after exactly one earlier optional — offset 14 either way, and
    // the only offset the other six combinations never place it at. An off-by-one in the preceding
    // field lands here and nowhere else.
    assertEquals(1, VECTORS.get("setLockup.timestamp-and-custodian")[14], "custodian present at 14");
    assertEquals(1, VECTORS.get("setLockup.epoch-and-custodian")[14], "custodian present at 14");
    assertEquals(0, VECTORS.get("setLockup.timestamp-and-custodian")[13], "epoch absent");
    assertEquals(0, VECTORS.get("setLockup.epoch-and-custodian")[4], "unixTimestamp absent");

    assertEquals(4 + 1 + 1, VECTORS.get("setLockupChecked.none").length,
        "the checked variant takes the custodian as an account, leaving two options");
    assertEquals(4 + 9 + 1, VECTORS.get("setLockupChecked.timestamp-only").length);
    assertEquals(4 + 1 + 9, VECTORS.get("setLockupChecked.epoch-only").length);
    assertEquals(4 + 9 + 9, VECTORS.get("setLockupChecked.both").length);

    // The presence byte of a later field moves by the size of an earlier one, which is where an
    // off-by-one lands: absent-then-present and present-then-absent are different layouts.
    assertEquals(0, VECTORS.get("setLockup.epoch-only")[4], "unixTimestamp absent");
    assertEquals(1, VECTORS.get("setLockup.epoch-only")[5], "epoch present, one byte later");
    assertEquals(1, VECTORS.get("setLockup.timestamp-only")[4], "unixTimestamp present");
    assertEquals(0, VECTORS.get("setLockup.timestamp-only")[13], "epoch absent, nine bytes later");
  }

  /// `UnixTimestamp` is `i64` upstream and a plain `long` here, `Epoch` is `u64`. Both are eight
  /// little-endian bytes, so the only thing that could differ is a sign or a width — hence a
  /// negative timestamp and an all-ones u64 among the vectors.
  @Test
  void sixtyFourBitFieldsAreNeitherSignedNorWidened() {
    final byte[] negative = VECTORS.get("setLockup.negative-timestamp");
    for (int i = 5; i < 13; ++i) {
      assertEquals((byte) 0xFF, negative[i], "byte " + i + " of a -1 unixTimestamp");
    }

    final byte[] maxU64 = VECTORS.get("split.max-u64");
    assertEquals(12, maxU64.length);
    for (int i = 4; i < 12; ++i) {
      assertEquals((byte) 0xFF, maxU64[i], "byte " + i + " of an all-ones u64");
    }
  }

  // ---------------------------------------------------------------------------
  // Vector name to builder call
  // ---------------------------------------------------------------------------

  /// Exhaustive by construction: a vector with no case here fails the switch, and a case naming a
  /// vector that is not in the file fails the lookup in [#everyVectorMatchesTheReferenceEncoder].
  private static Instruction build(final String vector) {
    return switch (vector) {
      case "initialize.basic" -> StakeProgram.initialize(PROGRAM, NO_KEYS,
          new Authorized(STAKER, WITHDRAWER),
          new Lockup(new UnixTimestamp(LOCKUP_TIMESTAMP), new Epoch(LOCKUP_EPOCH), CUSTODIAN));
      case "initialize.negative-timestamp" -> StakeProgram.initialize(PROGRAM, NO_KEYS,
          new Authorized(STAKER, WITHDRAWER),
          new Lockup(new UnixTimestamp(-1L), new Epoch(0L), CUSTODIAN));

      case "authorize.staker" -> StakeProgram.authorize(PROGRAM, NO_KEYS, NEW_AUTHORITY, StakeAuthorize.staker);
      case "authorize.withdrawer" -> StakeProgram.authorize(PROGRAM, NO_KEYS, NEW_AUTHORITY, StakeAuthorize.withdrawer);

      case "delegateStake.bare" -> StakeProgram.delegateStake(PROGRAM, NO_KEYS);

      case "split.lamports" -> StakeProgram.split(PROGRAM, NO_KEYS, 5_000L);
      // u64 max. Java has no unsigned long, so the caller passes the same 64 bits as -1.
      case "split.max-u64" -> StakeProgram.split(PROGRAM, NO_KEYS, -1L);

      case "withdraw.lamports" -> StakeProgram.withdraw(PROGRAM, NO_KEYS, 4_230_000_000_000L);

      case "deactivate.bare" -> StakeProgram.deactivate(PROGRAM, NO_KEYS);

      case "setLockup.all" -> StakeProgram.setLockup(PROGRAM, NO_KEYS,
          new UnixTimestamp(LOCKUP_TIMESTAMP), new Epoch(LOCKUP_EPOCH), CUSTODIAN);
      case "setLockup.none" -> StakeProgram.setLockup(PROGRAM, NO_KEYS, null, null, null);
      case "setLockup.timestamp-only" -> StakeProgram.setLockup(PROGRAM, NO_KEYS,
          new UnixTimestamp(LOCKUP_TIMESTAMP), null, null);
      case "setLockup.epoch-only" -> StakeProgram.setLockup(PROGRAM, NO_KEYS,
          null, new Epoch(LOCKUP_EPOCH), null);
      case "setLockup.custodian-only" -> StakeProgram.setLockup(PROGRAM, NO_KEYS, null, null, CUSTODIAN);
      case "setLockup.timestamp-and-custodian" -> StakeProgram.setLockup(PROGRAM, NO_KEYS,
          new UnixTimestamp(LOCKUP_TIMESTAMP), null, CUSTODIAN);
      case "setLockup.epoch-and-custodian" -> StakeProgram.setLockup(PROGRAM, NO_KEYS,
          null, new Epoch(LOCKUP_EPOCH), CUSTODIAN);
      case "setLockup.negative-timestamp" -> StakeProgram.setLockup(PROGRAM, NO_KEYS,
          new UnixTimestamp(-1L), new Epoch(0L), null);

      case "merge.bare" -> StakeProgram.merge(PROGRAM, NO_KEYS);

      case "authorizeWithSeed.staker" -> StakeProgram.authorizeWithSeed(PROGRAM, NO_KEYS,
          SEEDED_NEW_AUTHORITY, StakeAuthorize.staker, "authority-seed", AUTHORITY_OWNER);
      case "authorizeWithSeed.withdrawer" -> StakeProgram.authorizeWithSeed(PROGRAM, NO_KEYS,
          SEEDED_NEW_AUTHORITY, StakeAuthorize.withdrawer, "stake:9", AUTHORITY_OWNER);
      case "authorizeWithSeed.empty-seed" -> StakeProgram.authorizeWithSeed(PROGRAM, NO_KEYS,
          SEEDED_NEW_AUTHORITY, StakeAuthorize.staker, "", AUTHORITY_OWNER);
      case "authorizeWithSeed.multibyte-seed" -> StakeProgram.authorizeWithSeed(PROGRAM, NO_KEYS,
          SEEDED_NEW_AUTHORITY, StakeAuthorize.staker, MULTIBYTE_SEED, AUTHORITY_OWNER);
      case "authorizeWithSeed.max-length-seed" -> StakeProgram.authorizeWithSeed(PROGRAM, NO_KEYS,
          SEEDED_NEW_AUTHORITY, StakeAuthorize.staker, MAX_LENGTH_SEED, AUTHORITY_OWNER);

      case "initializeChecked.bare" -> StakeProgram.initializeChecked(PROGRAM, NO_KEYS);

      case "authorizeChecked.staker" -> StakeProgram.authorizeChecked(PROGRAM, NO_KEYS, StakeAuthorize.staker);
      case "authorizeChecked.withdrawer" -> StakeProgram.authorizeChecked(PROGRAM, NO_KEYS, StakeAuthorize.withdrawer);

      case "authorizeCheckedWithSeed.withdrawer" -> StakeProgram.authorizeCheckedWithSeed(PROGRAM, NO_KEYS,
          StakeAuthorize.withdrawer, "checked-seed", CHECKED_AUTHORITY_OWNER);
      case "authorizeCheckedWithSeed.empty-seed" -> StakeProgram.authorizeCheckedWithSeed(PROGRAM, NO_KEYS,
          StakeAuthorize.staker, "", CHECKED_AUTHORITY_OWNER);
      case "authorizeCheckedWithSeed.multibyte-seed" -> StakeProgram.authorizeCheckedWithSeed(PROGRAM, NO_KEYS,
          StakeAuthorize.staker, MULTIBYTE_SEED, CHECKED_AUTHORITY_OWNER);
      case "authorizeCheckedWithSeed.max-length-seed" -> StakeProgram.authorizeCheckedWithSeed(PROGRAM, NO_KEYS,
          StakeAuthorize.withdrawer, MAX_LENGTH_SEED, CHECKED_AUTHORITY_OWNER);

      case "setLockupChecked.both" -> StakeProgram.setLockupChecked(PROGRAM, NO_KEYS,
          new UnixTimestamp(LOCKUP_TIMESTAMP), new Epoch(LOCKUP_EPOCH));
      case "setLockupChecked.none" -> StakeProgram.setLockupChecked(PROGRAM, NO_KEYS, null, null);
      case "setLockupChecked.timestamp-only" -> StakeProgram.setLockupChecked(PROGRAM, NO_KEYS,
          new UnixTimestamp(LOCKUP_TIMESTAMP), null);
      case "setLockupChecked.epoch-only" -> StakeProgram.setLockupChecked(PROGRAM, NO_KEYS,
          null, new Epoch(LOCKUP_EPOCH));

      case "getMinimumDelegation.bare" -> StakeProgram.getMinimumDelegation(PROGRAM);

      case "deactivateDelinquent.bare" -> StakeProgram.deactivateDelinquent(PROGRAM, NO_KEYS);

      case "moveStake.lamports" -> StakeProgram.moveStake(PROGRAM, NO_KEYS, 1_000_000_000L);

      case "moveLamports.lamports" -> StakeProgram.moveLamports(PROGRAM, NO_KEYS, 2_500_000_000L);

      default -> throw new IllegalStateException(
          vector + " has no builder call — add one, or drop the vector from tools/stake-vectors.mjs");
    };
  }

  // ---------------------------------------------------------------------------
  // Fixture plumbing
  // ---------------------------------------------------------------------------

  /// Byte `i` is `(seed + i * 7) & 0xff`, mirroring `k()` in `tools/stake-vectors.mjs`. Every byte
  /// of the key differs from its neighbours, so a pubkey written backwards or shifted by one is a
  /// mismatch — which a key of repeated bytes, the convention elsewhere in these tests, would not
  /// catch.
  private static PublicKey key(final int seed) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    for (int i = 0; i < publicKey.length; ++i) {
      publicKey[i] = (byte) (seed + i * 7);
    }
    return PublicKey.createPubKey(publicKey);
  }

  private static byte[] data(final Instruction ix) {
    return Arrays.copyOfRange(ix.data(), ix.offset(), ix.offset() + ix.len());
  }

  private static Set<String> declaredInstructions() {
    return Arrays.stream(StakeProgram.class.getFields())
        .filter(field -> field.getType() == Discriminator.class)
        .map(Field::getName)
        .map(StakeReferenceEncodingTests::instructionName)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  /// `AUTHORIZE_WITH_SEED_DISCRIMINATOR` to `authorizeWithSeed`, the name the vectors use.
  private static String instructionName(final String constant) {
    final var words = constant.substring(0, constant.lastIndexOf("_DISCRIMINATOR")).split("_");
    final var name = new StringBuilder(words[0].toLowerCase(Locale.ROOT));
    for (int i = 1; i < words.length; ++i) {
      name.append(words[i].charAt(0)).append(words[i].substring(1).toLowerCase(Locale.ROOT));
    }
    return name.toString();
  }

  private static Map<String, byte[]> load() {
    final var resource = "/stake/reference-vectors.txt";
    try (var in = StakeReferenceEncodingTests.class.getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalStateException("fixture " + resource + " is missing");
      }
      final var vectors = new LinkedHashMap<String, byte[]>();
      try (var reader = new BufferedReader(new InputStreamReader(in, UTF_8))) {
        for (String line; (line = reader.readLine()) != null; ) {
          line = line.strip();
          if (line.isEmpty() || line.charAt(0) == '#') {
            continue;
          }
          final int split = line.indexOf(' ');
          if (split < 0) {
            throw new IllegalStateException("expected \"<name> <base64>\" but read: " + line);
          }
          final var name = line.substring(0, split);
          if (name.indexOf('.') < 0) {
            throw new IllegalStateException("vector " + name + " is not named <instruction>.<case>");
          }
          if (vectors.put(name, Base64.getDecoder().decode(line.substring(split + 1))) != null) {
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
