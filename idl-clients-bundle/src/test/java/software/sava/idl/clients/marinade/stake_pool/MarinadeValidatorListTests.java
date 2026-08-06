package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorRecord;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// The Marinade validator list account is an 8-byte "validatr" magic followed by
/// back-to-back {@link ValidatorRecord}s of 61 bytes each — the program's own
/// `List::bytes_for` is `8 + count * item_size`, so the magic is counted once for the
/// account and there is no per-slot frame. The list is compacted with swap-removes and
/// vacated tail slots are NOT zeroed, so the authoritative length is the State's
/// `count` — a zero-terminator scan over-reads stale records.
final class MarinadeValidatorListTests {

  private static final int MAGIC_LEN = MarinadeValidatorList.MAGIC_LEN;
  private static final int ITEM_SIZE = MarinadeValidatorList.ITEM_SIZE;

  private static PublicKey key(final int fill) {
    final byte[] k = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(k, (byte) fill);
    return PublicKey.createPubKey(k);
  }

  private static ValidatorRecord record(final int fill, final long balance) {
    return new ValidatorRecord(key(fill), balance, fill, 596L, fill & 0xFF, 0L);
  }

  /// magic + `live` active records + `stale` non-zero records (swap-remove leftovers) +
  /// `zeroed` fully-zeroed trailing capacity.
  private static byte[] buildList(final ValidatorRecord[] live, final ValidatorRecord[] stale, final int zeroed) {
    final int slots = live.length + stale.length + zeroed;
    final byte[] data = new byte[MAGIC_LEN + (slots * ITEM_SIZE)];
    System.arraycopy(new byte[]{'v', 'a', 'l', 'i', 'd', 'a', 't', 'r'}, 0, data, 0, MAGIC_LEN);
    int slot = 0;
    for (final var r : live) {
      r.write(data, MAGIC_LEN + (slot++ * ITEM_SIZE));
    }
    for (final var r : stale) {
      r.write(data, MAGIC_LEN + (slot++ * ITEM_SIZE));
    }
    // remaining slots stay zeroed
    return data;
  }

  @Test
  void countBasedReadIgnoresStaleTail() {
    final var live = new ValidatorRecord[]{record(1, 100), record(2, 200), record(3, 300)};
    final var stale = new ValidatorRecord[]{record(4, 400), record(5, 500)};
    final byte[] data = buildList(live, stale, 4);

    final var list = MarinadeValidatorList.read(PublicKey.NONE, data, live.length);
    assertEquals(3, list.validators().size());
    for (int i = 0; i < live.length; ++i) {
      assertEquals(live[i], list.validators().get(i));
    }
    // the reverse lookup only knows the live validators
    assertEquals(0, list.validatorIndex(key(1)));
    assertEquals(2, list.validatorIndex(key(3)));
    assertFalse(list.validatorIndex(key(4)) >= 0, "a stale-tail validator must not resolve");
  }

  @Test
  void factoryOverReadsStaleTail() {
    final var live = new ValidatorRecord[]{record(1, 100), record(2, 200), record(3, 300)};
    final var stale = new ValidatorRecord[]{record(4, 400), record(5, 500)};
    final byte[] data = buildList(live, stale, 4);

    // The deprecated terminator-based factory cannot tell live from stale: it reads until
    // the first zeroed slot, so it returns all 5 non-zero records, not the 3 live ones.
    final var overRead = MarinadeValidatorList.FACTORY.apply(PublicKey.NONE, data);
    assertEquals(5, overRead.validators().size());

    // count-based read on the same bytes yields only the live prefix.
    assertEquals(3, MarinadeValidatorList.read(PublicKey.NONE, data, 3).validators().size());
  }

  @Test
  void emptyList() {
    final byte[] data = buildList(new ValidatorRecord[0], new ValidatorRecord[0], 8);
    assertEquals(0, MarinadeValidatorList.read(PublicKey.NONE, data, 0).validators().size());
  }

  /// `validatorRecord(key)` resolves through the reverse lookup: a present key —
  /// including the one at index 0, where an off-by-one on the `< 0` guard flips
  /// the answer — returns its record, an absent one returns `null`.
  @Test
  void validatorRecordByIndexAndByKey() {
    final var live = new ValidatorRecord[]{record(1, 100), record(2, 200), record(3, 300)};
    final var list = MarinadeValidatorList.read(PublicKey.NONE, buildList(live, new ValidatorRecord[0], 2), live.length);

    assertEquals(live[1], list.validatorRecord(1));
    assertEquals(live[0], list.validatorRecord(key(1)), "index 0 is a hit, not a miss");
    assertEquals(live[2], list.validatorRecord(key(3)));
    assertNull(list.validatorRecord(key(4)), "an absent validator resolves to null, not an exception");
  }

  /// The reverse lookup is binary-searched, so `read` must sort it. The keys
  /// are written in descending byte order — already-sorted input would let a
  /// dropped sort pass unnoticed.
  @Test
  void reverseLookupIsSortedBeforeSearching() {
    final var live = new ValidatorRecord[]{record(5, 500), record(4, 400), record(3, 300), record(2, 200), record(1, 100)};
    final var list = MarinadeValidatorList.read(PublicKey.NONE, buildList(live, new ValidatorRecord[0], 0), live.length);
    for (int i = 0; i < live.length; ++i) {
      assertEquals(i, list.validatorIndex(key(5 - i)), "validator " + (5 - i) + " resolves to its list position");
    }
  }

  /// The deprecated factory on a list whose capacity is exactly its live
  /// records — no zeroed tail to break on, so the length bound alone must stop
  /// the scan. An over-shot bound reads past the end and throws instead.
  @Test
  void factoryReadsAnExactFitList() {
    final var live = new ValidatorRecord[]{record(1, 100), record(2, 200), record(3, 300)};
    final byte[] data = buildList(live, new ValidatorRecord[0], 0);

    final var list = MarinadeValidatorList.FACTORY.apply(PublicKey.NONE, data);
    assertEquals(3, list.validators().size());
    for (int i = 0; i < live.length; ++i) {
      assertEquals(live[i], list.validators().get(i));
    }
  }

  /// Boundary of the factory's scan: an account holding exactly one record is
  /// `MAGIC_LEN + ITEM_SIZE` bytes, so its only iteration starts with `offset == to`
  /// and the inclusive bound is what admits the record.
  @Test
  void factoryReadsASingleExactSlot() {
    final byte[] data = new byte[MAGIC_LEN + ITEM_SIZE];
    record(1, 100).write(data, MAGIC_LEN);

    final var list = MarinadeValidatorList.FACTORY.apply(PublicKey.NONE, data);
    assertEquals(1, list.validators().size());
    assertEquals(record(1, 100), list.validators().getFirst());
  }

  /// A 313-byte head of the live mainnet validator list
  /// (DwFYJNnhLmw19FBTrVaLWZ8SZJpxdPoSYVSJaio9tjbY), which is the only oracle that can
  /// settle the record stride: this test, the reader and the deprecated FACTORY all used
  /// to share one constant, so they agreed with each other and none of them with the
  /// chain.
  ///
  /// Every expected key below is a real vote account — each is owned by
  /// Vote111111111111111111111111111111111111111 on mainnet. Decoding at any other
  /// stride yields addresses that do not exist on chain at all, which is what makes this
  /// checkable rather than merely self-consistent: a misaligned 32-byte window still
  /// base58-encodes to something that looks like a pubkey.
  @Test
  void realMainnetListDecodesAtTheOnChainStride() {
    final byte[] data = java.util.Base64.getDecoder().decode(
        "dmFsaWRhdHKZfVG8bcevdTyOpztfpNnQfTXx1AfvtwAzELSxLAgOVQAAAAAAAAAAAAAAAFQCAAAA" +
        "AAAA/wAAAAAAAAAARHpnDJ7Gl942VsW6BfYX8IFfnCBWLoDEo5jk/68SopsAAAAAAAAAAAAAAAD/" +
        "//////////8AAAAAAAAAAFLxsikSm+ETd4Won3Ja38tJ5BNHkR4d6fHjnx4PFEtOAAAAAAAAAAAA" +
        "AAAAfQIAAAAAAAD/AAAAAAAAAADRMGcFYdRe/jja02rkuxaFrdQ8RwCnQIw+wB2yA/rUugAAAAAA" +
        "AAAAAAAAAIgBAAAAAAAA/QAAAAAAAAAAZSR65YCDCaAGop6mi9bvD2RBeEiE6G1Nbkrs1p4eUwwA" +
        "AAAAAAAAAAAAAAByAQAAAAAAAP8AAAAAAAAAAA==");

    final var expected = new String[]{
        "BLADE1qNA1uNjRgER6DtUFf7FU3c1TWLLdpPeEcKatZ2",
        "5cJyfCLBfghRtoCuVJNreJgNCStqXLrhHmRhSRYtbgtr",
        "6anBvYWGwkkZPAaPF6BmzF6LUPfP2HFVhQUAWckKH9LZ",
        "F5b1wSUtpaYDnpjLQonCZC7iyFvizLcNqTactZbwSEXK",
        "7opSZGmevWhRDyLt5Wu38FZFjUyredGmMki4DNmxDnjd"
    };

    final var list = MarinadeValidatorList.read(PublicKey.NONE, data, expected.length);
    assertEquals(expected.length, list.validators().size());
    for (int i = 0; i < expected.length; ++i) {
      assertEquals(expected[i], list.validators().get(i).validatorAccount().toBase58(),
          "record " + i + " — a wrong stride still decodes to a well-formed but nonexistent key");
    }

    // and the reverse lookup indexes those same keys
    assertEquals(1, list.validatorIndex(PublicKey.fromBase58Encoded(expected[1])));
  }

}
