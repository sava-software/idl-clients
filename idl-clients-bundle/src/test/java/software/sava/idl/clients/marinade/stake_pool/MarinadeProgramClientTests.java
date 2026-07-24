package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.marinade.stake_pool.gen.types.FeeCents;
import software.sava.idl.clients.marinade.stake_pool.gen.types.List;
import software.sava.idl.clients.marinade.stake_pool.gen.types.StakeSystem;
import software.sava.idl.clients.marinade.stake_pool.gen.types.State;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorSystem;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.stake.LockUp;
import software.sava.idl.clients.spl.stake.StakeAccount;
import software.sava.idl.clients.spl.stake.StakeState;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Marinade client's pure helpers — the list index scans and the
/// virtual-stake/price arithmetic — plus client identity wiring.
///
/// `accountIndex` is the one with teeth: it linearly scans a raw list account,
/// skipping an 8-byte discriminator and striding by the item size recorded in
/// `State`. The index it returns is passed straight into instructions that act
/// on *that* validator or stake account, so an off-by-one in the start offset,
/// the stride, or the compared key length silently targets the wrong one.
final class MarinadeProgramClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);

  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));

  /// Absolute offsets of the nested fields inside a `State` account, composed
  /// from the generated constants rather than hardcoded.
  private static final int TOTAL_ACTIVE_BALANCE =
      State.VALIDATOR_SYSTEM_OFFSET + ValidatorSystem.TOTAL_ACTIVE_BALANCE_OFFSET;
  private static final int DELAYED_UNSTAKE_COOLING_DOWN =
      State.STAKE_SYSTEM_OFFSET + StakeSystem.DELAYED_UNSTAKE_COOLING_DOWN_OFFSET;
  private static final int STAKE_LIST_ITEM_SIZE =
      State.STAKE_SYSTEM_OFFSET + StakeSystem.STAKE_LIST_OFFSET + List.ITEM_SIZE_OFFSET;
  private static final int VALIDATOR_LIST_ITEM_SIZE =
      State.VALIDATOR_SYSTEM_OFFSET + ValidatorSystem.VALIDATOR_LIST_OFFSET + List.ITEM_SIZE_OFFSET;

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// A synthetic `State` account: the balance fields that feed the price math,
  /// plus the two list item sizes the index scans stride by.
  private static State state(final long totalActiveBalance,
                             final long delayedUnstakeCoolingDown,
                             final long emergencyCoolingDown,
                             final long availableReserveBalance,
                             final long circulatingTicketBalance,
                             final long msolSupply,
                             final int stakeItemSize,
                             final int validatorItemSize) {
    // State is no longer fixed-size: a variable-length DelinquentUpgraderState
    // enum sits at DELINQUENT_UPGRADER_OFFSET, followed by two FeeCents. Size
    // the buffer for the unit ("Done") variant plus that tail.
    final byte[] data = new byte[State.DELINQUENT_UPGRADER_OFFSET + 1 + (2 * FeeCents.BYTES)];
    State.DISCRIMINATOR.write(data, 0);
    data[State.DELINQUENT_UPGRADER_OFFSET] = 2; // Done — a unit variant
    ByteUtil.putInt64LE(data, TOTAL_ACTIVE_BALANCE, totalActiveBalance);
    ByteUtil.putInt64LE(data, DELAYED_UNSTAKE_COOLING_DOWN, delayedUnstakeCoolingDown);
    ByteUtil.putInt64LE(data, State.EMERGENCY_COOLING_DOWN_OFFSET, emergencyCoolingDown);
    ByteUtil.putInt64LE(data, State.AVAILABLE_RESERVE_BALANCE_OFFSET, availableReserveBalance);
    ByteUtil.putInt64LE(data, State.CIRCULATING_TICKET_BALANCE_OFFSET, circulatingTicketBalance);
    ByteUtil.putInt64LE(data, State.MSOL_SUPPLY_OFFSET, msolSupply);
    ByteUtil.putInt32LE(data, STAKE_LIST_ITEM_SIZE, stakeItemSize);
    ByteUtil.putInt32LE(data, VALIDATOR_LIST_ITEM_SIZE, validatorItemSize);
    return State.read(data, 0);
  }

  private static State state(final int stakeItemSize, final int validatorItemSize) {
    return state(0L, 0L, 0L, 0L, 0L, 1L, stakeItemSize, validatorItemSize);
  }

  /// A raw list account: 8-byte discriminator then `itemSize`-strided records,
  /// each leading with its 32-byte key.
  private static byte[] listAccount(final int itemSize, final PublicKey... entries) {
    final byte[] data = new byte[8 + (itemSize * entries.length)];
    Arrays.fill(data, 0, 8, (byte) 0xEE); // a discriminator that must be skipped
    for (int i = 0; i < entries.length; i++) {
      entries[i].write(data, 8 + (i * itemSize));
    }
    return data;
  }

  // ---------------------------------------------------------------------------
  // accountIndex
  // ---------------------------------------------------------------------------

  @Test
  void accountIndexScansPastTheDiscriminatorAtItemStride() {
    final int itemSize = 61; // not a multiple of the key length: stride must be honoured
    final var first = key(0x21);
    final var second = key(0x22);
    final var third = key(0x23);
    final byte[] list = listAccount(itemSize, first, second, third);

    assertEquals(0, MarinadeProgramClient.accountIndex(first.toByteArray(), list, itemSize));
    assertEquals(1, MarinadeProgramClient.accountIndex(second.toByteArray(), list, itemSize));
    assertEquals(2, MarinadeProgramClient.accountIndex(third.toByteArray(), list, itemSize));

    // absent -> -1, not 0 and not an exception
    assertEquals(-1, MarinadeProgramClient.accountIndex(key(0x24).toByteArray(), list, itemSize));
    // an empty list is just "not found"
    assertEquals(-1, MarinadeProgramClient.accountIndex(first.toByteArray(), new byte[8], itemSize));
  }

  /// The 8-byte discriminator is skipped, not treated as the first record: a
  /// key planted at offset 0 must not be found.
  @Test
  void accountIndexIgnoresTheDiscriminatorBytes() {
    final int itemSize = 40;
    final var planted = key(0x31);
    final var real = key(0x32);

    final byte[] list = listAccount(itemSize, real);
    // overwrite the leading bytes with the start of another key
    final byte[] plantedBytes = planted.toByteArray();
    System.arraycopy(plantedBytes, 0, list, 0, 8);

    assertEquals(-1, MarinadeProgramClient.accountIndex(planted.toByteArray(), list, itemSize));
    assertEquals(0, MarinadeProgramClient.accountIndex(real.toByteArray(), list, itemSize));
  }

  /// Only the 32-byte key participates in the comparison — the trailing record
  /// payload must not affect the match.
  @Test
  void accountIndexComparesOnlyTheKey() {
    final int itemSize = 61;
    final var target = key(0x41);
    final byte[] list = listAccount(itemSize, key(0x40), target);
    // fill the rest of every record with noise
    for (int i = 0; i < 2; i++) {
      final int recordStart = 8 + (i * itemSize);
      Arrays.fill(list, recordStart + PublicKey.PUBLIC_KEY_LENGTH, recordStart + itemSize, (byte) 0x7F);
    }
    assertEquals(1, MarinadeProgramClient.accountIndex(target.toByteArray(), list, itemSize));

    // a key that matches only in its first 31 bytes is not a match
    final byte[] almost = target.toByteArray();
    almost[PublicKey.PUBLIC_KEY_LENGTH - 1] ^= 0x01;
    assertEquals(-1, MarinadeProgramClient.accountIndex(almost, list, itemSize));
  }

  /// The stake and validator lists carry *different* item sizes in `State`, so
  /// each scan must stride by its own.
  @Test
  void stakeAndValidatorIndexUseTheirOwnItemSize() {
    final int stakeItemSize = 61;
    final int validatorItemSize = 40;
    final var state = state(stakeItemSize, validatorItemSize);

    final var stakeKey = key(0x51);
    final var voteKey = key(0x52);
    final byte[] stakeList = listAccount(stakeItemSize, key(0x50), stakeKey);
    final byte[] validatorList = listAccount(validatorItemSize, key(0x50), voteKey);

    assertEquals(1, MarinadeProgramClient.stakeAccountIndex(stakeKey.toByteArray(), stakeList, state));
    assertEquals(1, MarinadeProgramClient.validatorIndex(voteKey.toByteArray(), validatorList, state));

    // each key is found only in its own list
    assertEquals(-1, MarinadeProgramClient.accountIndex(voteKey.toByteArray(), stakeList, stakeItemSize));
    assertEquals(-1, MarinadeProgramClient.accountIndex(stakeKey.toByteArray(), validatorList, validatorItemSize));

    // the StakeAccount overloads read the address for the stake list and the
    // *voter* key for the validator list — not the same field
    final var stakeAccount = new StakeAccount(
        stakeKey, StakeState.Stake, 0L, OWNER, OWNER, LockUp.NO_LOCKUP,
        voteKey, 0L, 0L, -1L, 0d, 0L, (byte) 0);
    assertEquals(1, MarinadeProgramClient.stakeAccountIndex(stakeAccount, stakeList, state));
    assertEquals(1, MarinadeProgramClient.validatorIndex(stakeAccount, validatorList, state));
    // and they are genuinely different keys, so the two accessors cannot be swapped
    assertNotEquals(stakeAccount.address(), stakeAccount.voterPublicKey());

  }

  /// Regression: the scan used to read a full key at every stride without
  /// confirming one remained, so a list whose length is not an exact multiple
  /// of its item size — a truncated account, or an item size that disagrees
  /// with the data — threw `ArrayIndexOutOfBoundsException` instead of
  /// reporting "not found". It now stops when fewer than a whole key is left.
  @Test
  void accountIndexStopsShortOfATruncatedRecord() {
    final int itemSize = 40;
    final var present = key(0x61);
    // one whole record plus a partial second
    final byte[] truncated = Arrays.copyOf(listAccount(itemSize, present, key(0x63)), 8 + itemSize + 10);

    assertEquals(-1, MarinadeProgramClient.accountIndex(key(0x62).toByteArray(), truncated, itemSize));
    // the whole record that *is* present still resolves
    assertEquals(0, MarinadeProgramClient.accountIndex(present.toByteArray(), truncated, itemSize));

    // a list shorter than a single key, and an empty one, are both "not found"
    assertEquals(-1, MarinadeProgramClient.accountIndex(present.toByteArray(), new byte[8 + 31], itemSize));
    assertEquals(-1, MarinadeProgramClient.accountIndex(present.toByteArray(), new byte[0], itemSize));

    // a well-formed list of the same item size is unaffected
    assertEquals(-1, MarinadeProgramClient.accountIndex(key(0x62).toByteArray(), listAccount(itemSize, present), itemSize));
    assertEquals(0, MarinadeProgramClient.accountIndex(present.toByteArray(), listAccount(itemSize, present), itemSize));

    // and a mismatched stride no longer overruns — it just misses
    assertEquals(-1, MarinadeProgramClient.accountIndex(present.toByteArray(), listAccount(61, key(0x64), present), 40));

    // a key ending flush with the buffer — payload truncated, key complete —
    // is exactly the inclusive scan bound, and must still be found
    final byte[] flush = Arrays.copyOf(listAccount(itemSize, key(0x64), present), 8 + itemSize + PublicKey.PUBLIC_KEY_LENGTH);
    assertEquals(1, MarinadeProgramClient.accountIndex(present.toByteArray(), flush, itemSize));
  }

  // ---------------------------------------------------------------------------
  // virtual stake / price
  // ---------------------------------------------------------------------------

  /// Every balance component is summed and the circulating ticket balance is
  /// *subtracted* — tickets are already-claimed SOL that must not be counted.
  @Test
  void totalVirtualStakedLamportsSumsAndSubtractsTickets() {
    final var state = state(1_000L, 200L, 30L, 4L, 5L, 1L, 61, 40);
    // (1000 + 200 + 30 + 4) - 5
    assertEquals(1_229L, MarinadeProgramClient.totalVirtualStakedLamports(state));

    // each component moves the total by its own amount, in the right direction
    assertEquals(1_329L, MarinadeProgramClient.totalVirtualStakedLamports(state(1_100L, 200L, 30L, 4L, 5L, 1L, 61, 40)));
    assertEquals(1_329L, MarinadeProgramClient.totalVirtualStakedLamports(state(1_000L, 300L, 30L, 4L, 5L, 1L, 61, 40)));
    assertEquals(1_239L, MarinadeProgramClient.totalVirtualStakedLamports(state(1_000L, 200L, 40L, 4L, 5L, 1L, 61, 40)));
    assertEquals(1_230L, MarinadeProgramClient.totalVirtualStakedLamports(state(1_000L, 200L, 30L, 5L, 5L, 1L, 61, 40)));
    assertEquals(1_224L, MarinadeProgramClient.totalVirtualStakedLamports(state(1_000L, 200L, 30L, 4L, 10L, 1L, 61, 40)));
  }

  @Test
  void solPriceIsVirtualStakeOverSupply() {
    // 1_229 virtual lamports over 1_000 mSOL
    assertEquals(1.229d, MarinadeProgramClient.solPrice(state(1_000L, 200L, 30L, 4L, 5L, 1_000L, 61, 40)), 1e-12);
    // a larger supply lowers the price
    assertTrue(MarinadeProgramClient.solPrice(state(1_000L, 200L, 30L, 4L, 5L, 2_000L, 61, 40))
        < MarinadeProgramClient.solPrice(state(1_000L, 200L, 30L, 4L, 5L, 1_000L, 61, 40)));
    // the division is floating point, not integer truncation
    assertEquals(0.5d, MarinadeProgramClient.solPrice(state(1L, 0L, 0L, 0L, 0L, 2L, 61, 40)), 1e-12);
  }

  // ---------------------------------------------------------------------------
  // client wiring
  // ---------------------------------------------------------------------------

  @Test
  void clientBindsItsIdentity() {
    final var client = MarinadeProgramClient.createClient(ACCOUNT_CLIENT);

    assertEquals(SOLANA_ACCOUNTS, client.solanaAccounts());
    assertEquals(MarinadeAccounts.MAIN_NET, client.marinadeAccounts(), "single-arg factory defaults to main net");
    assertSame(ACCOUNT_CLIENT, client.splAccountClient());
    assertEquals(OWNER, client.owner());
    assertEquals(FEE_PAYER, client.feePayer());
    assertNotEquals(client.owner(), client.feePayer());

    final var explicit = MarinadeProgramClient.createClient(ACCOUNT_CLIENT, MarinadeAccounts.MAIN_NET);
    assertNotNull(explicit);
    assertEquals(MarinadeAccounts.MAIN_NET, explicit.marinadeAccounts());
  }

  /// `claimTickets` maps each ticket to its own `claimTicket` instruction. The
  /// mapping step is the mutant target: dropped, the raw keys ride the list in
  /// the instructions' place, and each element access below fails the cast.
  @Test
  void claimTicketsMapsEachTicketToItsOwnInstruction() {
    final var client = MarinadeProgramClient.createClient(ACCOUNT_CLIENT);
    final var first = key(0x31);
    final var second = key(0x32);

    final var instructions = client.claimTickets(java.util.List.of(first, second));
    assertEquals(2, instructions.size());

    final var firstIx = instructions.get(0);
    final var secondIx = instructions.get(1);
    assertTrue(firstIx.accounts().stream().anyMatch(a -> a.publicKey().equals(first)),
        "the first instruction claims the first ticket");
    assertFalse(firstIx.accounts().stream().anyMatch(a -> a.publicKey().equals(second)));
    assertTrue(secondIx.accounts().stream().anyMatch(a -> a.publicKey().equals(second)),
        "the second instruction claims the second ticket");
  }
}
