package software.sava.idl.clients.jupiter.perpetuals;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.Custody;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.Pool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;

/// Helper for appending the "extra" (non-IDL) accounts that every Jupiter Perpetuals
/// instruction pricing the JLP pool reads from `ctx.remaining_accounts`. The generated
/// `PerpetualsProgram` builders only carry IDL accounts, so callers must append these via
/// [#appendPoolPricingAccounts(Instruction, Pool, Map)] after building the instruction.
///
/// The tail is `2n` read-only accounts, where `n` is the length of `Pool.custodies`:
///
/// 1. the Pool's custodies, in the order the Pool stores them;
/// 2. then each of those custodies' `dovesAgOracle`, in the same order.
///
/// The program is closed source, so this is measured rather than read: about 200 simulations
/// on mainnet between slots 465260126 and 465278995 that varied the accounts of otherwise
/// fixed calls, against the Pool `5BUwFW4n…` as read at slot 465257594 and its six custodies
/// as read at slot 465257856. Each of the 397 live tails sampled in slots 465045661 to
/// 465217306 and 465246776 to 465258954 begins with exactly these accounts. The simulations,
/// and the live calls the tests reproduce, postdate the program's upgrade at slot 465131236;
/// a later upgrade can change any of what follows.
///
/// The program checks the tail position by position, custody `i` and then its oracle for `i`
/// ascending (inferred from the errors and from compute units, which grow with the failing
/// position):
///
/// - Fewer than `2n` accounts fail `NotEnoughAccountKeys`. `n` comes from the Pool, not from
///   the tail: five pairs fail 6002 rather than `NotEnoughAccountKeys`, because the program
///   takes position 6 as custody 0's oracle.
/// - Custody slot `i` must be `Pool.custodies[i]`, or the call fails Anchor's 2502
///   `RequireKeysEqViolated`, logged at `programs/perpetuals/src/state/pool.rs:658`. A
///   permutation that keeps each oracle with its custody fails the same way, so the order is
///   the Pool's.
/// - Oracle slot `n + i` must be custody `i`'s `dovesAgOracle`, or the call fails 6002
///   `InvalidOracleAccount`, logged at `programs/perpetuals/src/state/oracle.rs:102`. The
///   custody's `dovesOracle` and its Pyth `oracle.oracleAccount` are rejected too.
/// - Read-only is enough for all `2n` accounts, and writable is tolerated. Accounts past `2n`
///   are ignored.
/// - One slot goes unchecked today: the oracle of the USDT custody `4vkNeXiY…`, position
///   `n + 4`, accepted another custody's oracle and even the System Program. Why is not
///   established; every live caller passes that custody's `dovesAgOracle`, and so does this
///   helper.
///
/// Do not deduplicate the tail against the declared accounts. The program indexes it by
/// position, so the declared custody and its oracle legitimately appear in it again; sava
/// merges flags per key when compiling a transaction, so a custody the IDL marks writable
/// stays writable, which the program accepts.
///
/// Which instructions read the tail:
///
/// | Instruction | Evidence |
/// |---|---|
/// | `refreshAssetsUnderManagement` (keeper) | 301 of 301 live calls carry it; simulated |
/// | `addLiquidity2` | 46 of 46 live calls; simulated |
/// | `removeLiquidity2` | 44 of 44 live calls; simulated |
/// | `getAssetsUnderManagement2`, `getAddLiquidityAmountAndFee2`, `getRemoveLiquidityAmountAndFee2` | simulated |
/// | `borrowFromCustody` | 1 of 1 live call; simulated |
/// | `withdrawCollateralForBorrows` | probed: `NotEnoughAccountKeys` without the tail, success with it |
/// | `liquidateBorrowPosition`, `partialLiquidateBorrowPosition` (keeper) | probed: `NotEnoughAccountKeys` without it, 6063 `CannotLiquidate` on a healthy position with it |
/// | `withdrawFees2` (keeper) | 5 of 5 live calls; not simulated |
///
/// These succeed without it: `depositCollateralForBorrows`, `repayToCustody`,
/// `createIncreasePositionMarketRequest`, and every position, swap and token-ledger
/// instruction sampled. Instructions neither sampled nor probed, such as
/// `closeBorrowPosition`, are not established either way.
///
/// The declared price accounts of `addLiquidity2`, `removeLiquidity2` and their two views take
/// a custody's oracle too. Pass its `dovesAgOracle` as `custodyDovesPriceAccountKey`: a
/// simulated `getAddLiquidityAmountAndFee2` rejected the other two with 6002. Live callers
/// pass it as `custodyPythnetPriceAccountKey` as well (all 34 adds and 40 of the 44 removes
/// sampled; the other four pass the Pyth account), a slot that simulation found accepting any
/// of the three.
public final class JupiterPerpetualsRemainingAccounts {

  private JupiterPerpetualsRemainingAccounts() {
  }

  /// The `2n` pricing accounts, read-only and in program order: the Pool's custodies as the
  /// Pool stores them, then each one's `dovesAgOracle`, read off the decoded [Custody] that
  /// `custodies` holds for it.
  ///
  /// `custodies` must hold the record of every key in `pool.custodies()`, keyed by that
  /// custody's address; entries for other keys are ignored. Each listed custody must belong to
  /// `pool`, so the Pool has to carry its own address, as `Pool.read(PublicKey, byte[])` and
  /// `Pool.read(AccountInfo)` give it. A custody record that carries its own address must sit
  /// under that address, so a record filed under another custody's key is refused rather than
  /// priced with the wrong oracle; a record decoded without an address, by
  /// `Custody.read(byte[], int)`, is taken at its key. Nothing is sorted or deduplicated.
  ///
  /// @throws IllegalArgumentException when a custody the Pool lists is missing from
  ///                                  `custodies`, names a different pool, or carries an
  ///                                  address other than its key
  public static List<AccountMeta> poolPricingAccounts(final Pool pool,
                                                      final Map<PublicKey, Custody> custodies) {
    final var custodyKeys = Arrays.asList(pool.custodies());
    final var oracles = new ArrayList<PublicKey>();
    for (final var custodyKey : custodyKeys) {
      final var custody = custodies.get(custodyKey);
      if (custody == null) {
        throw new IllegalArgumentException("Pool " + pool._address() + " lists custody " + custodyKey
            + ", which is missing from the custodies supplied.");
      }
      if (!custody.pool().equals(pool._address())) {
        throw new IllegalArgumentException("Custody " + custodyKey + " belongs to pool " + custody.pool()
            + ", not to " + pool._address() + ".");
      }
      final var address = custody._address();
      if (address != null && !address.equals(custodyKey)) {
        throw new IllegalArgumentException("The record filed under custody " + custodyKey
            + " is the account " + address + ".");
      }
      oracles.add(custody.dovesAgOracle());
    }
    return pricingAccounts(custodyKeys, oracles);
  }

  /// Append the `2n` pricing accounts of [#poolPricingAccounts(Pool, Map)], taking each oracle
  /// from the custody that names it so the two blocks cannot disagree.
  ///
  /// @throws IllegalArgumentException when a custody the Pool lists is missing from
  ///                                  `custodies`, or names a different pool
  public static Instruction appendPoolPricingAccounts(final Instruction instruction,
                                                      final Pool pool,
                                                      final Map<PublicKey, Custody> custodies) {
    return instruction.extraAccounts(poolPricingAccounts(pool, custodies));
  }

  /// The same two blocks, for a caller that already holds the keys.
  ///
  /// The collections are positional: `custodiesInPoolOrder` must be `Pool.custodies` in the
  /// order the Pool stores them, which the program requires (2502 otherwise), and index `i` of
  /// `dovesAgOracles` must be custody `i`'s `dovesAgOracle` (6002 otherwise). Prefer
  /// [#appendPoolPricingAccounts(Instruction, Pool, Map)], which reads both off the decoded
  /// accounts and cannot mispair them.
  ///
  /// @throws IllegalArgumentException when the two collections differ in size
  public static Instruction appendPoolPricingAccounts(final Instruction instruction,
                                                      final SequencedCollection<PublicKey> custodiesInPoolOrder,
                                                      final SequencedCollection<PublicKey> dovesAgOracles) {
    if (custodiesInPoolOrder.size() != dovesAgOracles.size()) {
      throw new IllegalArgumentException("Each custody needs its dovesAgOracle: "
          + custodiesInPoolOrder.size() + " custodies, " + dovesAgOracles.size() + " oracles.");
    }
    return instruction.extraAccounts(pricingAccounts(custodiesInPoolOrder, dovesAgOracles));
  }

  private static List<AccountMeta> pricingAccounts(final Collection<PublicKey> custodies,
                                                   final Collection<PublicKey> oracles) {
    final var metas = new ArrayList<AccountMeta>();
    for (final var custody : custodies) {
      metas.add(AccountMeta.createRead(custody));
    }
    for (final var oracle : oracles) {
      metas.add(AccountMeta.createRead(oracle));
    }
    return metas;
  }
}
