package software.sava.idl.clients.kamino.lend;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Kamino lending client's instruction assembly.
///
/// The distinctive shape here is *sentinel substitution*: several builders take
/// optional accounts and, when one is absent, pass a program id in its place to
/// keep the positional account list the right length. Two different sentinels
/// are in play — the kLend program for oracles and referrers, the farms program
/// for scope prices — and each substitution is independent. Getting one wrong
/// swaps in a real account at an oracle position, which is exactly the failure
/// that produces a plausible wrong price rather than an error.
final class KaminoLendClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final KaminoAccounts KAMINO_ACCOUNTS = KaminoAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final PublicKey MARKET = key(0x13);
  private static final PublicKey RESERVE = key(0x14);
  private static final PublicKey PYTH = key(0x15);
  private static final PublicKey SWITCHBOARD_PRICE = key(0x16);
  private static final PublicKey SWITCHBOARD_TWAP = key(0x17);
  private static final PublicKey SCOPE_PRICES = key(0x18);

  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));
  private static final KaminoLendClient CLIENT =
      KaminoLendClient.createClient(ACCOUNT_CLIENT, KAMINO_ACCOUNTS);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  /// Regression: `withdrawReferrerFees` passed the reserve's *collateral* mint
  /// where the program requires its *liquidity* mint. klend enforces
  /// `address = reserve.liquidity.mint_pubkey` on that account
  /// (`handler_withdraw_referrer_fees.rs`), so the instruction could never
  /// execute. `KaminoReservePDAs.mint()` is the liquidity mint — the six other
  /// builders in this client all pass it into the same slot; only this one
  /// reached for `collateralMint()`, which is a different (derived) account.
  @Test
  void withdrawReferrerFeesUsesTheLiquidityMint() {
    // createPDAs takes (programId, market) — in that order
    final var reservePDAs = KAMINO_ACCOUNTS.createReservePDAs(
        software.sava.idl.clients.kamino.lend.KaminoMarketPDAs.createPDAs(KAMINO_ACCOUNTS.kLendProgram(), MARKET),
        key(0x41),
        SOLANA_ACCOUNTS.tokenProgram());

    final var ix = CLIENT.withdrawReferrerFees(RESERVE, reservePDAs, key(0x42), key(0x43), key(0x44));
    final var accounts = keys(ix);

    assertTrue(accounts.contains(reservePDAs.mint()), "the liquidity mint must be present");
    assertFalse(accounts.contains(reservePDAs.collateralMint()),
        "the collateral mint is a different account and has no slot here");
    // and the two really are distinct, so the assertion above has teeth
    assertNotEquals(reservePDAs.mint(), reservePDAs.collateralMint());
    assertTrue(accounts.contains(reservePDAs.liquiditySupplyVault()));
  }

  @Test
  void clientBindsItsIdentity() {
    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(KAMINO_ACCOUNTS, CLIENT.kaminoAccounts());

    // the single-arg factory defaults to main net
    final var defaulted = KaminoLendClient.createClient(ACCOUNT_CLIENT);
    assertNotNull(defaulted);
    assertEquals(KaminoAccounts.MAIN_NET, defaulted.kaminoAccounts());
  }

  // ---------------------------------------------------------------------------
  // refreshReserve: four independent oracle sentinels
  // ---------------------------------------------------------------------------

  /// With all four oracles supplied, each lands in the account list under its
  /// own distinct key.
  @Test
  void refreshReserveCarriesEveryOracle() {
    final var ix = CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, SCOPE_PRICES);
    final var accounts = keys(ix);

    assertEquals(KAMINO_ACCOUNTS.invokedKLendProgram(), ix.programId());
    assertTrue(accounts.contains(RESERVE));
    assertTrue(accounts.contains(MARKET));
    assertTrue(accounts.contains(PYTH));
    assertTrue(accounts.contains(SWITCHBOARD_PRICE));
    assertTrue(accounts.contains(SWITCHBOARD_TWAP));
    assertTrue(accounts.contains(SCOPE_PRICES));

    // the four oracles occupy four distinct positions
    assertEquals(4, java.util.Set.of(
        accounts.indexOf(PYTH),
        accounts.indexOf(SWITCHBOARD_PRICE),
        accounts.indexOf(SWITCHBOARD_TWAP),
        accounts.indexOf(SCOPE_PRICES)).size());
  }

  /// Each oracle is substituted *independently*: dropping one must replace only
  /// that slot with the kLend sentinel and leave the other three intact.
  @Test
  void refreshReserveSubstitutesEachOracleIndependently() {
    final var sentinel = KAMINO_ACCOUNTS.kLendProgram();
    final var all = keys(CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, SCOPE_PRICES));

    final int pythSlot = all.indexOf(PYTH);
    final int priceSlot = all.indexOf(SWITCHBOARD_PRICE);
    final int twapSlot = all.indexOf(SWITCHBOARD_TWAP);
    final int scopeSlot = all.indexOf(SCOPE_PRICES);

    final var noPyth = keys(CLIENT.refreshReserve(MARKET, RESERVE, null, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, SCOPE_PRICES));
    assertEquals(sentinel, noPyth.get(pythSlot));
    assertEquals(SWITCHBOARD_PRICE, noPyth.get(priceSlot));
    assertEquals(SWITCHBOARD_TWAP, noPyth.get(twapSlot));
    assertEquals(SCOPE_PRICES, noPyth.get(scopeSlot));

    final var noPrice = keys(CLIENT.refreshReserve(MARKET, RESERVE, PYTH, null, SWITCHBOARD_TWAP, SCOPE_PRICES));
    assertEquals(PYTH, noPrice.get(pythSlot));
    assertEquals(sentinel, noPrice.get(priceSlot));
    assertEquals(SWITCHBOARD_TWAP, noPrice.get(twapSlot));

    final var noTwap = keys(CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, null, SCOPE_PRICES));
    assertEquals(SWITCHBOARD_PRICE, noTwap.get(priceSlot));
    assertEquals(sentinel, noTwap.get(twapSlot));
    assertEquals(SCOPE_PRICES, noTwap.get(scopeSlot));

    final var noScope = keys(CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, null));
    assertEquals(SWITCHBOARD_TWAP, noScope.get(twapSlot));
    assertEquals(sentinel, noScope.get(scopeSlot));

    // all absent: every oracle slot is the sentinel, and the list keeps its length
    final var none = keys(CLIENT.refreshReserve(MARKET, RESERVE, null, null, null, null));
    assertEquals(all.size(), none.size(), "the account list must not shrink");
    assertEquals(sentinel, none.get(pythSlot));
    assertEquals(sentinel, none.get(priceSlot));
    assertEquals(sentinel, none.get(twapSlot));
    assertEquals(sentinel, none.get(scopeSlot));
  }

  /// Both null-key sentinels are honoured, not just `null`: Kamino uses
  /// `PublicKey.NONE` and its own `nu111...` key as absent markers too.
  @Test
  void refreshReserveTreatsBothNullSentinelsAsAbsent() {
    final var sentinel = KAMINO_ACCOUNTS.kLendProgram();
    final var slot = keys(CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, SCOPE_PRICES))
        .indexOf(PYTH);

    for (final var absent : new PublicKey[]{null, PublicKey.NONE, KaminoAccounts.NULL_KEY}) {
      final var accounts = keys(CLIENT.refreshReserve(MARKET, RESERVE, absent, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, SCOPE_PRICES));
      assertEquals(sentinel, accounts.get(slot), String.valueOf(absent));
    }
  }

  // ---------------------------------------------------------------------------
  // farms: a different sentinel
  // ---------------------------------------------------------------------------

  /// The farms builders substitute the *farms* program for an absent scope
  /// prices account — not the kLend program the reserve refresh uses.
  @Test
  void refreshFarmSubstitutesTheFarmsProgram() {
    final var farmState = key(0x21);

    final var supplied = keys(CLIENT.refreshFarm(farmState, SCOPE_PRICES));
    assertTrue(supplied.contains(SCOPE_PRICES));

    final var absent = keys(CLIENT.refreshFarm(farmState, null));
    assertTrue(absent.contains(KAMINO_ACCOUNTS.farmProgram()), "the farms program is the sentinel here");
    assertFalse(absent.contains(SCOPE_PRICES));

    // and it is a different sentinel from the reserve refresh's
    assertNotEquals(KAMINO_ACCOUNTS.farmProgram(), KAMINO_ACCOUNTS.kLendProgram());
    assertFalse(absent.contains(KAMINO_ACCOUNTS.kLendProgram()));

    assertEquals(KAMINO_ACCOUNTS.invokedFarmsProgram(), CLIENT.refreshFarm(farmState, null).programId());
  }

  /// An absent delegatee defaults to the owner, so the user state is delegated
  /// to itself rather than to the zero key.
  @Test
  void initializeFarmUserDefaultsTheDelegateeToTheOwner() {
    final var farmState = key(0x22);
    final var userState = key(0x23);
    final var delegatee = key(0x24);

    final var withDelegatee = keys(CLIENT.initializeFarmUser(farmState, userState, delegatee));
    assertTrue(withDelegatee.contains(delegatee));

    final var withoutDelegatee = CLIENT.initializeFarmUser(farmState, userState, null);
    assertFalse(keys(withoutDelegatee).contains(delegatee));
    assertTrue(keys(withoutDelegatee).contains(OWNER));
    // passing the owner explicitly is the same instruction
    assertEquals(withoutDelegatee, CLIENT.initializeFarmUser(farmState, userState, OWNER));
    // as is either null sentinel
    assertEquals(withoutDelegatee, CLIENT.initializeFarmUser(farmState, userState, PublicKey.NONE));
    assertEquals(withoutDelegatee, CLIENT.initializeFarmUser(farmState, userState, KaminoAccounts.NULL_KEY));
  }

  // ---------------------------------------------------------------------------
  // flash loans: referrer sentinels, and the borrow/repay pair
  // ---------------------------------------------------------------------------

  @Test
  void flashBorrowSubstitutesAbsentReferrers() {
    final var mint = key(0x31);
    final var source = key(0x32);
    final var destination = key(0x33);
    final var feeReceiver = key(0x34);
    final var referrerTokenState = key(0x35);
    final var referrerAccount = key(0x36);
    final var sentinel = KAMINO_ACCOUNTS.kLendProgram();

    final var withReferrer = keys(CLIENT.flashBorrowReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        referrerTokenState, referrerAccount, SOLANA_ACCOUNTS.tokenProgram(), 1_000L));
    assertTrue(withReferrer.contains(referrerTokenState));
    assertTrue(withReferrer.contains(referrerAccount));

    final int stateSlot = withReferrer.indexOf(referrerTokenState);
    final int accountSlot = withReferrer.indexOf(referrerAccount);

    // each referrer slot is substituted independently
    final var noState = keys(CLIENT.flashBorrowReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        null, referrerAccount, SOLANA_ACCOUNTS.tokenProgram(), 1_000L));
    assertEquals(sentinel, noState.get(stateSlot));
    assertEquals(referrerAccount, noState.get(accountSlot));

    final var noAccount = keys(CLIENT.flashBorrowReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        referrerTokenState, null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L));
    assertEquals(referrerTokenState, noAccount.get(stateSlot));
    assertEquals(sentinel, noAccount.get(accountSlot));

    // the market authority is derived, not passed
    assertTrue(withReferrer.contains(KAMINO_ACCOUNTS.lendingMarketAuthPda(MARKET).publicKey()));
    // the instructions sysvar is present: the program checks the repay pairing
    assertTrue(withReferrer.contains(SOLANA_ACCOUNTS.instructionsSysVar()));
  }

  /// The repay side takes a `borrowInstructionIndex` that the on-chain program
  /// uses to locate its matching borrow via the instructions sysvar. It is
  /// data, not an account, so it must not disturb the account list.
  @Test
  void flashRepayCarriesTheBorrowInstructionIndex() {
    final var mint = key(0x31);
    final var source = key(0x32);
    final var destination = key(0x33);
    final var feeReceiver = key(0x34);

    final var atZero = CLIENT.flashRepayReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        null, null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L, 0);
    final var atThree = CLIENT.flashRepayReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        null, null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L, 3);

    assertEquals(keys(atZero), keys(atThree), "the index is data, not an account");
    assertFalse(Arrays.equals(atZero.data(), atThree.data()));
    // the sysvar it is resolved against must be present
    assertTrue(keys(atZero).contains(SOLANA_ACCOUNTS.instructionsSysVar()));
    // and the market authority is derived rather than passed
    assertTrue(keys(atZero).contains(KAMINO_ACCOUNTS.lendingMarketAuthPda(MARKET).publicKey()));
  }

  // ---------------------------------------------------------------------------
  // borrow / repay
  // ---------------------------------------------------------------------------

  /// Borrowing routes liquidity out of the reserve's supply into the user's
  /// account, taking a fee to a third. Those three token accounts are distinct
  /// keys in distinct slots — a transposition would pay the fee to the borrower
  /// or draw from the wrong vault.
  @Test
  void borrowObligationLiquidityWiresItsThreeTokenAccounts() {
    final var obligation = key(0x51);
    final var mint = key(0x52);
    final var source = key(0x53);
    final var feeReceiver = key(0x54);
    final var destination = key(0x55);

    final var ix = CLIENT.borrowObligationLiquidity(
        obligation, MARKET, RESERVE, mint, source, feeReceiver, destination,
        null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L);

    assertEquals(KAMINO_ACCOUNTS.invokedKLendProgram(), ix.programId());
    final var accounts = keys(ix);
    assertTrue(accounts.contains(obligation));
    assertTrue(accounts.contains(mint));
    assertTrue(accounts.contains(source), "the reserve's supply");
    assertTrue(accounts.contains(feeReceiver), "the fee receiver");
    assertTrue(accounts.contains(destination), "the borrower's account");
    assertEquals(3, java.util.Set.of(
        accounts.indexOf(source), accounts.indexOf(feeReceiver), accounts.indexOf(destination)).size());

    // the owner signs, and the market authority is derived
    assertTrue(ix.accounts().stream().anyMatch(m -> m.publicKey().equals(OWNER) && m.signer()));
    assertTrue(accounts.contains(KAMINO_ACCOUNTS.lendingMarketAuthPda(MARKET).publicKey()));

    // an absent referrer token state falls back to the kLend sentinel
    final int referrerSlot = keys(CLIENT.borrowObligationLiquidity(
        obligation, MARKET, RESERVE, mint, source, feeReceiver, destination,
        key(0x56), SOLANA_ACCOUNTS.tokenProgram(), 1_000L)).indexOf(key(0x56));
    assertEquals(KAMINO_ACCOUNTS.kLendProgram(), accounts.get(referrerSlot));

    // the amount is data
    final var larger = CLIENT.borrowObligationLiquidity(
        obligation, MARKET, RESERVE, mint, source, feeReceiver, destination,
        null, SOLANA_ACCOUNTS.tokenProgram(), 2_000L);
    assertEquals(accounts, keys(larger));
    assertFalse(Arrays.equals(ix.data(), larger.data()));
  }

  /// The V2 variant appends the two farm state accounts plus the farms program,
  /// keeping the base account list intact ahead of them.
  @Test
  void borrowObligationLiquidityV2AppendsTheFarmAccounts() {
    final var obligation = key(0x51);
    final var mint = key(0x52);
    final var source = key(0x53);
    final var feeReceiver = key(0x54);
    final var destination = key(0x55);
    final var obligationFarm = key(0x57);
    final var reserveFarm = key(0x58);

    final var v1 = CLIENT.borrowObligationLiquidity(
        obligation, MARKET, RESERVE, mint, source, feeReceiver, destination,
        null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L);
    final var v2 = CLIENT.borrowObligationLiquidityV2(
        obligation, MARKET, RESERVE, mint, source, feeReceiver, destination,
        null, SOLANA_ACCOUNTS.tokenProgram(), obligationFarm, reserveFarm, 1_000L);

    final var v1Keys = keys(v1);
    final var v2Keys = keys(v2);
    // the base accounts lead, unchanged
    assertEquals(v1Keys, v2Keys.subList(0, v1Keys.size()));
    // then the farm pair and the farms program
    assertEquals(
        java.util.List.of(obligationFarm, reserveFarm, KAMINO_ACCOUNTS.farmProgram()),
        v2Keys.subList(v1Keys.size(), v2Keys.size()));
    // and it is a distinct instruction, not the same one with extra accounts
    assertNotEquals(v1.data()[0], v2.data()[0]);
  }

  @Test
  void repayObligationLiquidityDrawsFromTheUser() {
    final var obligation = key(0x61);
    final var mint = key(0x62);
    final var reserveDestination = key(0x63);
    final var userSource = key(0x64);

    final var ix = CLIENT.repayObligationLiquidity(
        obligation, MARKET, RESERVE, mint, reserveDestination, userSource,
        SOLANA_ACCOUNTS.tokenProgram(), 1_000L);

    assertEquals(KAMINO_ACCOUNTS.invokedKLendProgram(), ix.programId());
    final var accounts = keys(ix);
    assertTrue(accounts.contains(reserveDestination), "repayment lands in the reserve");
    assertTrue(accounts.contains(userSource), "and is drawn from the user");
    assertNotEquals(accounts.indexOf(reserveDestination), accounts.indexOf(userSource));

    // repay is the inverse of borrow: same reserve, opposite token flow
    final var borrow = CLIENT.borrowObligationLiquidity(
        obligation, MARKET, RESERVE, mint, reserveDestination, key(0x65), userSource,
        null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L);
    assertNotEquals(borrow.data()[0], ix.data()[0]);
  }

  /// Borrow and repay share their account shape but are distinct instructions —
  /// a flash loan is only valid as a matched pair.
  @Test
  void flashBorrowAndRepayAreDistinctInstructions() {
    final var mint = key(0x31);
    final var source = key(0x32);
    final var destination = key(0x33);
    final var feeReceiver = key(0x34);

    final var borrow = CLIENT.flashBorrowReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        null, null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L);
    final var repay = CLIENT.flashRepayReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        null, null, SOLANA_ACCOUNTS.tokenProgram(), 1_000L, 0);

    assertEquals(KAMINO_ACCOUNTS.invokedKLendProgram(), borrow.programId());
    assertEquals(KAMINO_ACCOUNTS.invokedKLendProgram(), repay.programId());
    assertNotEquals(borrow.data()[0], repay.data()[0]);

    // the amount is data, so a different amount keeps the same accounts
    final var larger = CLIENT.flashBorrowReserveLiquidity(
        MARKET, RESERVE, mint, source, destination, feeReceiver,
        null, null, SOLANA_ACCOUNTS.tokenProgram(), 2_000L);
    assertEquals(keys(borrow), keys(larger));
    assertFalse(Arrays.equals(borrow.data(), larger.data()));
  }

  // ---------------------------------------------------------------------------
  // sentinel substitution, exhaustively
  // ---------------------------------------------------------------------------

  /// Every builder that takes an optional account substitutes a program id when
  /// it is absent, so the positional list keeps its length. The tests above pin
  /// the interesting cases in detail; this one sweeps the *remaining* sites so
  /// no substitution goes unexercised in both directions.
  ///
  /// Both directions matter: asserting only the absent case would pass even if
  /// the builder ignored its argument and always emitted the sentinel, and
  /// asserting only the present case would miss a missing substitution.
  ///
  /// Kamino treats three values as "absent" — `null`, `PublicKey.NONE`, and its
  /// own `nu111...` key — and all three must map to the same sentinel.
  private void assertSubstitutes(final String label,
                                 final java.util.function.Function<PublicKey, Instruction> build,
                                 final PublicKey sentinel) {
    final var present = key(0x7E);
    final var withKey = keys(build.apply(present));
    final int slot = withKey.indexOf(present);
    assertTrue(slot >= 0, label + ": the supplied key must appear");

    for (final var absent : new PublicKey[]{null, PublicKey.NONE, KaminoAccounts.NULL_KEY}) {
      final var without = keys(build.apply(absent));
      assertEquals(withKey.size(), without.size(), label + ": the list must not shrink");
      assertEquals(sentinel, without.get(slot), label + ": absent -> sentinel");
      for (int i = 0; i < withKey.size(); i++) {
        if (i != slot) {
          assertEquals(withKey.get(i), without.get(i), label + ": slot " + i + " must not move");
        }
      }
    }
  }

  @Test
  void everyOptionalAccountSubstitutesItsSentinel() {
    final var kLend = KAMINO_ACCOUNTS.kLendProgram();
    final var farms = KAMINO_ACCOUNTS.farmProgram();
    final var mint = key(0x21);
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();

    // referrers substitute the kLend program
    assertSubstitutes("flashRepay.referrerTokenState",
        k -> CLIENT.flashRepayReserveLiquidity(MARKET, RESERVE, mint, key(0x22), key(0x23),
            key(0x24), k, key(0x25), tokenProgram, 1_000L, 3), kLend);
    assertSubstitutes("flashRepay.referrerAccount",
        k -> CLIENT.flashRepayReserveLiquidity(MARKET, RESERVE, mint, key(0x22), key(0x23),
            key(0x24), key(0x26), k, tokenProgram, 1_000L, 3), kLend);
    assertSubstitutes("borrowObligationLiquidity.referrerTokenState",
        k -> CLIENT.borrowObligationLiquidity(key(0x27), MARKET, RESERVE, mint, key(0x28),
            key(0x29), key(0x2A), k, tokenProgram, 1_000L), kLend);
    assertSubstitutes("borrowObligationLiquidityV2.referrerTokenState",
        k -> CLIENT.borrowObligationLiquidityV2(key(0x27), MARKET, RESERVE, mint, key(0x28),
            key(0x29), key(0x2A), k, tokenProgram, key(0x2B), key(0x2C), 1_000L), kLend);

    // scope prices substitute the farms program
    assertSubstitutes("refreshFarmUserState.scopePrices",
        k -> CLIENT.refreshFarmUserState(key(0x31), key(0x32), k), farms);
    assertSubstitutes("stakeFarm.scopePrices",
        k -> CLIENT.stakeFarm(key(0x31), key(0x32), key(0x33), key(0x34), mint, k,
            tokenProgram, 1_000L), farms);
    assertSubstitutes("unstakeFarm.scopePrices",
        k -> CLIENT.unstakeFarm(key(0x31), key(0x32), k, java.math.BigInteger.TEN), farms);
    assertSubstitutes("harvestFarmReward.scopePrices",
        k -> CLIENT.harvestFarmReward(key(0x31), key(0x32), mint, key(0x35), key(0x36),
            key(0x37), key(0x38), k, tokenProgram, 0L), farms);

    assertSubstitutes("flashBorrow.referrerTokenState",
        k -> CLIENT.flashBorrowReserveLiquidity(MARKET, RESERVE, mint, key(0x28), key(0x29),
            key(0x2A), k, key(0x2B), tokenProgram, 1_000L), kLend);
    assertSubstitutes("flashBorrow.referrerAccount",
        k -> CLIENT.flashBorrowReserveLiquidity(MARKET, RESERVE, mint, key(0x28), key(0x29),
            key(0x2A), key(0x2B), k, tokenProgram, 1_000L), kLend);

    // refreshReserve's four oracles all fall back to the kLend program
    assertSubstitutes("refreshReserve.pyth",
        k -> CLIENT.refreshReserve(MARKET, RESERVE, k, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, SCOPE_PRICES), kLend);
    assertSubstitutes("refreshReserve.switchboardPrice",
        k -> CLIENT.refreshReserve(MARKET, RESERVE, PYTH, k, SWITCHBOARD_TWAP, SCOPE_PRICES), kLend);
    assertSubstitutes("refreshReserve.switchboardTwap",
        k -> CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, k, SCOPE_PRICES), kLend);
    assertSubstitutes("refreshReserve.scopePrices",
        k -> CLIENT.refreshReserve(MARKET, RESERVE, PYTH, SWITCHBOARD_PRICE, SWITCHBOARD_TWAP, k), kLend);

    // the two sentinels are genuinely different programs
    assertNotEquals(kLend, farms);
  }
}
