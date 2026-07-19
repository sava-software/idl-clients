package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorRecord;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
}
