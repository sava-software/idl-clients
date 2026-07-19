package software.sava.idl.clients.meteora.dlmm;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.meteora.MeteoraAccounts;
import software.sava.idl.clients.meteora.MeteoraPDAs;
import software.sava.idl.clients.meteora.dlmm.gen.LbClmmConstants;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Meteora DLMM client.
///
/// The distinctive piece is `deriveBinAccounts`: liquidity operations span a
/// *range* of bins, and the bin arrays covering that range have to be appended
/// to the instruction as extra accounts. Derive too few and the program cannot
/// reach the bins it needs; derive the wrong indices and it touches the wrong
/// liquidity. The range→array mapping is integer division that floors toward
/// negative infinity, so the interesting cases are ranges straddling zero and
/// ranges landing exactly on an array boundary.
final class MeteoraDlmmClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final MeteoraAccounts METEORA_ACCOUNTS = MeteoraAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final PublicKey LB_PAIR = key(0x13);
  private static final PublicKey POSITION = key(0x14);

  private static final MeteoraDlmmClient CLIENT =
      MeteoraDlmmClient.createClient(SOLANA_ACCOUNTS, METEORA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final List<AccountMeta> metas) {
    return metas.stream().map(AccountMeta::publicKey).toList();
  }

  private static PublicKey binArray(final int index) {
    return MeteoraPDAs.binArrayPdA(LB_PAIR, index, METEORA_ACCOUNTS.dlmmProgram()).publicKey();
  }

  @Test
  void clientBindsItsIdentity() {
    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(METEORA_ACCOUNTS, CLIENT.meteoraAccounts());

    // the short factory defaults both account sets to main net
    final var defaulted = MeteoraDlmmClient.createClient(OWNER, AccountMeta.createFeePayer(FEE_PAYER));
    assertNotNull(defaulted);
    assertEquals(SolanaAccounts.MAIN_NET, defaulted.solanaAccounts());
    assertEquals(MeteoraAccounts.MAIN_NET, defaulted.meteoraAccounts());
  }

  // ---------------------------------------------------------------------------
  // deriveBinAccounts
  // ---------------------------------------------------------------------------

  /// A range inside a single array yields exactly one bin array — not a
  /// degenerate empty list, and not a duplicate pair.
  @Test
  void singleArrayRangeYieldsOneAccount() {
    final var program = METEORA_ACCOUNTS.dlmmProgram();
    final int arraySize = (int) LbClmmConstants.MAX_BIN_PER_ARRAY;

    // bins 1..2 sit well inside array 0
    final var metas = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, 1, 2);
    assertEquals(1, metas.size());
    assertEquals(binArray(0), metas.getFirst().publicKey());
    assertTrue(metas.getFirst().write(), "bin arrays are mutated by liquidity ops");

    // the whole of array 0, inclusive of both ends, is still one array
    final var whole = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, 0, arraySize - 1);
    assertEquals(1, whole.size());
    assertEquals(binArray(0), whole.getFirst().publicKey());
  }

  /// Crossing an array boundary must include *both* arrays, and the range is
  /// inclusive at both ends.
  @Test
  void rangeSpanningArraysIncludesEveryArrayInclusive() {
    final var program = METEORA_ACCOUNTS.dlmmProgram();
    final int arraySize = (int) LbClmmConstants.MAX_BIN_PER_ARRAY;

    // one bin either side of the 0/1 boundary
    final var pair = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, arraySize - 1, arraySize);
    assertEquals(List.of(binArray(0), binArray(1)), keys(pair));

    // three consecutive arrays, in ascending order with no gaps or repeats
    final var three = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, 0, (2 * arraySize) + 1);
    assertEquals(List.of(binArray(0), binArray(1), binArray(2)), keys(three));
    assertEquals(3, java.util.Set.copyOf(keys(three)).size(), "no duplicates");
    assertTrue(three.stream().allMatch(AccountMeta::write));
  }

  /// Negative bin ids floor toward negative infinity, so a range straddling
  /// zero spans arrays -1 and 0 — truncating toward zero would collapse them.
  @Test
  void rangeStraddlingZeroSpansTheNegativeArray() {
    final var program = METEORA_ACCOUNTS.dlmmProgram();

    final var straddling = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, -1, 0);
    assertEquals(List.of(binArray(-1), binArray(0)), keys(straddling));

    // wholly negative, inside one array
    final int arraySize = (int) LbClmmConstants.MAX_BIN_PER_ARRAY;
    final var negative = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, -arraySize, -1);
    assertEquals(List.of(binArray(-1)), keys(negative));

    // one past that array's lower edge picks up array -2
    final var wider = MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, -arraySize - 1, -1);
    assertEquals(List.of(binArray(-2), binArray(-1)), keys(wider));

    // and the two arrays either side of zero are genuinely different accounts
    assertNotEquals(binArray(-1), binArray(0));
  }

  /// The derivation is bound to its pair and program — the same bin range on a
  /// different pair must not resolve to the same arrays.
  @Test
  void binAccountsDependOnThePairAndProgram() {
    final var program = METEORA_ACCOUNTS.dlmmProgram();
    final var otherPair = key(0x21);
    final var otherProgram = key(0x22);

    final var mine = keys(MeteoraDlmmClient.deriveBinAccounts(program, LB_PAIR, 0, 1));
    assertNotEquals(mine, keys(MeteoraDlmmClient.deriveBinAccounts(program, otherPair, 0, 1)));
    assertNotEquals(mine, keys(MeteoraDlmmClient.deriveBinAccounts(otherProgram, LB_PAIR, 0, 1)));
  }

  /// `appendBinAccounts` attaches the derived arrays to an instruction without
  /// disturbing the accounts already on it.
  @Test
  void appendBinAccountsExtendsRatherThanReplaces() {
    final var program = METEORA_ACCOUNTS.dlmmProgram();
    final var base = Instruction.createInstruction(
        AccountMeta.createInvoked(program),
        List.of(AccountMeta.createWrite(POSITION)),
        new byte[]{7});

    final var extended = MeteoraDlmmClient.appendBinAccounts(program, LB_PAIR, 0, 1, base);
    final var accounts = keys(extended.accounts());

    assertEquals(POSITION, accounts.getFirst(), "the original account still leads");
    assertEquals(List.of(binArray(0)), accounts.subList(1, accounts.size()));
    assertArrayEquals(base.data(), extended.data(), "appending accounts must not touch the data");

    // the instance overload binds the client's own program
    assertEquals(
        keys(extended.accounts()),
        keys(CLIENT.appendBinAccounts(LB_PAIR, 0, 1, base).accounts()));
  }

  // ---------------------------------------------------------------------------
  // swap: the optional host fee
  // ---------------------------------------------------------------------------

  /// An absent host-fee account is replaced by the DLMM program id, keeping the
  /// positional account list intact — the same Anchor optional-account
  /// convention Kamino uses.
  @Test
  void absentHostFeeSubstitutesTheDlmmProgram() {
    final var reserveX = key(0x31);
    final var reserveY = key(0x32);
    final var userIn = key(0x33);
    final var userOut = key(0x34);
    final var mintX = key(0x35);
    final var mintY = key(0x36);
    final var oracle = key(0x37);
    final var hostFee = key(0x38);

    final var withHostFee = CLIENT.swap(
        LB_PAIR, null, reserveX, reserveY, userIn, userOut, mintX, mintY,
        oracle, hostFee, SOLANA_ACCOUNTS.tokenProgram(), SOLANA_ACCOUNTS.tokenProgram(),
        1_000L, 900L, null);
    final var withoutHostFee = CLIENT.swap(
        LB_PAIR, null, reserveX, reserveY, userIn, userOut, mintX, mintY,
        oracle, null, SOLANA_ACCOUNTS.tokenProgram(), SOLANA_ACCOUNTS.tokenProgram(),
        1_000L, 900L, null);

    final var withKeys = keys(withHostFee.accounts());
    final var withoutKeys = keys(withoutHostFee.accounts());

    assertEquals(withKeys.size(), withoutKeys.size(), "the account list must not shrink");
    final int slot = withKeys.indexOf(hostFee);
    assertTrue(slot >= 0, "the host fee account is present when supplied");
    assertEquals(METEORA_ACCOUNTS.dlmmProgram(), withoutKeys.get(slot),
        "an absent host fee is replaced by the program id");

    // every other slot is unchanged
    for (int i = 0; i < withKeys.size(); i++) {
      if (i != slot) {
        assertEquals(withKeys.get(i), withoutKeys.get(i), "slot " + i);
      }
    }
  }

  /// The swap amounts are data: changing them must not move any account.
  @Test
  void swapAmountsAreDataNotAccounts() {
    final var reserveX = key(0x31);
    final var reserveY = key(0x32);
    final var userIn = key(0x33);
    final var userOut = key(0x34);
    final var mintX = key(0x35);
    final var mintY = key(0x36);
    final var oracle = key(0x37);

    final var small = CLIENT.swap(
        LB_PAIR, null, reserveX, reserveY, userIn, userOut, mintX, mintY,
        oracle, null, SOLANA_ACCOUNTS.tokenProgram(), SOLANA_ACCOUNTS.tokenProgram(),
        1_000L, 900L, null);
    final var large = CLIENT.swap(
        LB_PAIR, null, reserveX, reserveY, userIn, userOut, mintX, mintY,
        oracle, null, SOLANA_ACCOUNTS.tokenProgram(), SOLANA_ACCOUNTS.tokenProgram(),
        2_000L, 1_800L, null);

    assertEquals(keys(small.accounts()), keys(large.accounts()));
    assertFalse(Arrays.equals(small.data(), large.data()));

    // the owner signs the swap
    assertTrue(small.accounts().stream().anyMatch(m -> m.publicKey().equals(OWNER) && m.signer()));
    assertEquals(METEORA_ACCOUNTS.invokedDlmmProgram(), small.programId());
  }
}
