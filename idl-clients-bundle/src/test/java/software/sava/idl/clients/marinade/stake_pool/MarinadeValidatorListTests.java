package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorRecord;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// The Marinade validator list account is an 8-byte "validatr" magic followed by fixed
/// 61-byte slots (an 8-byte frame + a 53-byte {@link ValidatorRecord}). The list is
/// compacted with swap-removes and vacated tail slots are NOT zeroed, so the authoritative
/// length is the State's `count` — a zero-terminator scan over-reads stale records.
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

  /// Boundary of the factory's scan: a buffer holding exactly one slot
  /// (`ITEM_SIZE` bytes) starts its only iteration with `offset == to`, so the
  /// inclusive bound is what admits the record.
  @Test
  void factoryReadsASingleExactSlot() {
    final byte[] data = new byte[ITEM_SIZE];
    record(1, 100).write(data, MAGIC_LEN);

    final var list = MarinadeValidatorList.FACTORY.apply(PublicKey.NONE, data);
    assertEquals(1, list.validators().size());
    assertEquals(record(1, 100), list.validators().getFirst());
  }
}
