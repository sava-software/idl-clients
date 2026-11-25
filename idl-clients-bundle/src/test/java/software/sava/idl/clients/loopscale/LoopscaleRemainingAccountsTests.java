package software.sava.idl.clients.loopscale;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.loopscale.gen.types.BorrowPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.loopscale.gen.types.ExpectedLoanValues;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Pins one live `deposit_collateral` made outside a `lock_loan`/`unlock_loan` pair, on a loan
/// with an active ledger, to the hand-written [LoopscaleClient] builder plus the remaining
/// accounts the call carried, appended with `Instruction.extraAccounts`. The call is transaction
/// `3PYHhdygXsvP1T4VExgrPefyoVj9jSPSwojGk9ZUyqeMqcKzjmzqL5oFWCN9j7Kik2vYc2RyrePthXpJDQ3pPhzX`
/// at slot 473424793, after the program's redeploy at slot 473418297. It holds no lookup table.
/// Borrower `bs1PuRvB…`, which also pays, wraps 0.2 SOL into its wSOL account `3SXpzout…`,
/// deposits it into loan `5ZoEAKVy…` and closes the wSOL account, all in that transaction. Read
/// at slot 473878369, that loan's one active ledger borrows USDC in market `Go6kfCbT…` and its
/// one collateral slot holds wSOL. Loopscale's protocol admin `CyNKPfqs…` co-signs.
///
/// The builder emits twelve accounts: the IDL's list without the event authority and the
/// program that end it. The redeployed program takes neither, and reads whatever follows
/// `protocol_admin_state` as its remaining accounts. With the two left in ahead of this call's
/// tail it fails `6010 InvalidRemainingAccount`, expecting the market information where it finds
/// the event authority (slots 473878459 and 473884232).
///
/// Outside a lock pair the program runs the loan's health check and reads it from the remaining
/// accounts and the params' `asset_index_guidance`:
///
/// - the ledger's market information, here read-only;
/// - the principal's oracle group, then one group per collateral slot. USDC and wSOL are both
///   priced through the beam price account `E22Z2nKB…` (oracle type 19), whose group is that
///   account followed by the zero key, so the two groups are the same two keys here and only
///   the guidance tells them apart;
/// - guidance `[1, 0]`: USDC's index, then wSOL's, in the market's asset data. `[0, 0]`,
///   `[2, 0]`, `[1, 1, 0]` and `[0, 1, 0]` each fail `6066 InvalidAssetIndex`.
///
/// The live call also sent a third group and a third guidance byte, which it does not need: it
/// succeeds without the group (slots 473878470 and 473884239), and with the third byte set to
/// 1, 2 or 255. This test pins what the live call sent, extras included.
///
/// Without the tail the program refuses the call with `6006 NotEnoughRemainingAccounts` (slot
/// 473878466). As sent it succeeds (slot 473878454). These are simulations against mainnet
/// state with signature verification off: nothing was signed or sent.
final class LoopscaleRemainingAccountsTests {

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static final PublicKey BORROWER = key("bs1PuRvB9rBBZkryBjADYvxc2qYh51EVW2fsb1uTiBN");
  private static final PublicKey LOAN = key("5ZoEAKVyabv2YWuc4qB11x1BiHWuLDpordDaTMA9sQFV");
  private static final PublicKey COLLATERAL_MINT = key("So11111111111111111111111111111111111111112");
  private static final PublicKey MARKET_INFORMATION = key("Go6kfCbT9f1R1MFSfbh9uYviWE4TAG9MFrTWot8s7rCB");
  private static final PublicKey BEAM_PRICES = key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH");
  private static final PublicKey ZERO = key("11111111111111111111111111111111");

  /// The lamports the transaction wrapped and the deposit moved: its System transfer into the
  /// borrower's wSOL account, and the rise in the loan's wSOL balance from 83,643,375 to
  /// 283,643,375.
  private static final long AMOUNT = 200_000_000L;

  /// What the live call sent as `asset_index_guidance`: USDC's index, wSOL's index, and a
  /// trailing byte the call does not need.
  private static final byte[] LIVE_GUIDANCE = {1, 0, 0};

  /// The number of accounts the client's builder emits for `deposit_collateral`: the IDL's
  /// fourteen without the event authority and the program.
  private static final int BUILT_ACCOUNTS = 12;

  /// The program the live call invoked.
  private static final PublicKey LIVE_PROGRAM = key("1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78");

  /// The live call's instruction data: the `deposit_collateral` discriminator, `amount` (u64 LE),
  /// `asset_type` 0, `asset_identifier` (wSOL), then `asset_index_guidance` as a u32 length and
  /// its bytes.
  private static final byte[] LIVE_DATA = HexFormat.of().parseHex(
      "9c838e7492f7a278"
          + "00c2eb0b00000000"
          + "00"
          + "069b8857feab8184fb687f634618c035dac439dc1aeb3b5598a0f00000000001"
          + "03000000" + "010000"
  );

  /// The live call's accounts in order, transcribed from the transaction, each with the privilege
  /// its message grants that key. A message records one privilege per key, the union over the
  /// transaction, and four other instructions here carry accounts (the wSOL account's creation,
  /// funding, sync and close). For each of the twelve declared slots that union is still what
  /// the IDL declares, and every account of the tail is read-only.
  private static final List<AccountMeta> LIVE_ACCOUNTS = List.of(
      AccountMeta.createReadOnlySigner(key("CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs")), // protocol_admin
      AccountMeta.createWritableSigner(key("bs1PuRvB9rBBZkryBjADYvxc2qYh51EVW2fsb1uTiBN")), // payer
      AccountMeta.createWritableSigner(key("bs1PuRvB9rBBZkryBjADYvxc2qYh51EVW2fsb1uTiBN")), // borrower
      AccountMeta.createWrite(key("5ZoEAKVyabv2YWuc4qB11x1BiHWuLDpordDaTMA9sQFV")), // loan
      AccountMeta.createWrite(key("3SXpzoutKSCghLDk4vmtiDdCPDFZTuajVPLxAsT4QZhD")), // borrower_collateral_ta
      AccountMeta.createWrite(key("BFTDkhxVLKhbAERRsEmRiNhYGunu53J342zdfxRqohdH")), // loan_collateral_ta
      AccountMeta.createRead(key("So11111111111111111111111111111111111111112")), // deposit_mint
      AccountMeta.createRead(key("So11111111111111111111111111111111111111112")), // asset_identifier
      AccountMeta.createRead(key("11111111111111111111111111111111")), // system_program
      AccountMeta.createRead(key("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")), // token_program
      AccountMeta.createRead(key("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL")), // associated_token_program
      AccountMeta.createRead(key("HcgXEnEsgvGowVnSjMmrzSewdx9yGvfXixiuMJPhyW2z")), // protocol_admin_state
      AccountMeta.createRead(key("Go6kfCbT9f1R1MFSfbh9uYviWE4TAG9MFrTWot8s7rCB")), // remaining accounts from here
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111")),
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111")),
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111"))
  );

  /// The remaining accounts the live call appended, in the health check's shape: the ledger's
  /// market information, an oracle group for the principal and one for the collateral, then a
  /// trailing group the call does not need. Every group here is the beam price account and the
  /// zero key, all read-only.
  private static List<AccountMeta> liveTail() {
    final var marketInformation = AccountMeta.createRead(MARKET_INFORMATION);
    final var beamPrices = AccountMeta.createRead(BEAM_PRICES);
    final var zero = AccountMeta.createRead(ZERO);
    return List.of(
        marketInformation,
        beamPrices, zero,
        beamPrices, zero,
        beamPrices, zero // not needed
    );
  }

  /// The short overload is the one under test: it derives the borrower's and the loan's token
  /// accounts and takes the protocol admin and its state from [LoopscaleAccounts#MAIN_NET], so
  /// every one of them is checked against a live key. The client is built inside the test body:
  /// coverage attributed to a field initializer is unstable under PIT.
  @Test
  void builderPlusTheLiveTailReproducesTheLiveCall() {
    final var accountClient = SPLAccountClient.createClient(
        SolanaAccounts.MAIN_NET,
        BORROWER,
        AccountMeta.createFeePayer(BORROWER)
    );
    final var client = LoopscaleClient.create(accountClient, LoopscaleAccounts.MAIN_NET);
    final var params = new DepositCollateralParams(AMOUNT, 0, COLLATERAL_MINT, LIVE_GUIDANCE);

    final var asBuilt = client.depositCollateral(LOAN, COLLATERAL_MINT, params);
    assertEquals(BUILT_ACCOUNTS, asBuilt.accounts().size(), "the builder stops at protocol_admin_state");

    final var ix = asBuilt.extraAccounts(liveTail());
    assertEquals(LIVE_PROGRAM, ix.programId().publicKey(), "invoked program");
    assertEquals(
        LIVE_ACCOUNTS.stream().map(AccountMeta::publicKey).toList(),
        ix.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order"
    );
    for (int i = 0; i < LIVE_ACCOUNTS.size(); i++) {
      final var live = LIVE_ACCOUNTS.get(i);
      final var built = ix.accounts().get(i);
      assertEquals(live.write(), built.write(), "writable at slot " + i);
      assertEquals(live.signer(), built.signer(), "signer at slot " + i);
    }
    assertArrayEquals(LIVE_DATA, ix.copyData(), "instruction data");
  }

  /// The newest borrow found among live loans, an inner call of transaction
  /// `5r55MCnJoeSQ5roSmuRw8SV6HsNKWrNnh5ey2W2gr6a9x7XUQPF4wrbyrVDE6Hwosfc3uE646ZHVMR6XVBDJiMpN`
  /// at slot 473053828, in which a Squads vault borrows 100,000 of a Token-2022 mint outside a
  /// lock pair. It was made before the redeploy, so it carried the event authority and the
  /// program after `protocol_admin_state`, ahead of its health check. No borrow appears among
  /// the program's transactions from the redeploy to slot 473884443.
  ///
  /// This is that call's account list without those two accounts, with the privileges the
  /// simulations below sent. A message keeps one privilege per key, the union of its uses, so
  /// the market information is writable in the tail through its declared slot.
  private static final List<AccountMeta> BORROW_ACCEPTED = List.of(
      AccountMeta.createReadOnlySigner(key("CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs")), // protocol_admin
      AccountMeta.createWritableSigner(key("AVvbWjgpVrFtYVaUA9RfsGy8VfqMNeum5FdQfQkd9hqH")), // payer
      AccountMeta.createWritableSigner(key("AVvbWjgpVrFtYVaUA9RfsGy8VfqMNeum5FdQfQkd9hqH")), // borrower
      AccountMeta.createWrite(key("Hp2jDpve9xSM6T65WPHUaTadLsVQn9EPZvkvEuPesCpV")), // loan
      AccountMeta.createWrite(key("836SQM4FLXELjrSu2cLLvi32aiqn22uNbZsZvBNdDHFm")), // strategy
      AccountMeta.createWrite(key("6TBbDMm1uoVbxVrcs8bWnmoyuks4wzHNyQqX3GaEFcgB")), // market_information
      AccountMeta.createRead(key("dawn7ZUF7h7anFuEsDdAU1Y3HYwikwqNMAENZsQJdNL")), // principal_mint
      AccountMeta.createWrite(key("6Us9rJVCJnCNS1erktF3rzH9Xr6p4zxdfjHy8GVSB3AQ")), // borrower_ta
      AccountMeta.createWrite(key("F488bJXts4G2e5eeFqLCuT5A4ebifSqEZmwLX7qeKurV")), // strategy_ta
      AccountMeta.createRead(key("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL")), // associated_token_program
      AccountMeta.createRead(key("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb")), // token_program
      AccountMeta.createRead(key("11111111111111111111111111111111")), // system_program
      AccountMeta.createRead(key("HcgXEnEsgvGowVnSjMmrzSewdx9yGvfXixiuMJPhyW2z")), // protocol_admin_state
      AccountMeta.createWrite(key("6TBbDMm1uoVbxVrcs8bWnmoyuks4wzHNyQqX3GaEFcgB")), // remaining accounts from here
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111")),
      AccountMeta.createRead(key("H84M3es89eLfGRDhUFxo7xgTQmhzAx4h2oBexU7WNiGN")),
      AccountMeta.createRead(key("11111111111111111111111111111111"))
  );

  /// The number of accounts the client's builder emits for `borrow_principal`: the IDL's fifteen
  /// without the event authority and the program.
  private static final int BORROW_BUILT_ACCOUNTS = 13;

  private static final PublicKey BORROW_VAULT = key("AVvbWjgpVrFtYVaUA9RfsGy8VfqMNeum5FdQfQkd9hqH");
  private static final PublicKey BORROW_LOAN = key("Hp2jDpve9xSM6T65WPHUaTadLsVQn9EPZvkvEuPesCpV");
  private static final PublicKey BORROW_STRATEGY = key("836SQM4FLXELjrSu2cLLvi32aiqn22uNbZsZvBNdDHFm");
  private static final PublicKey BORROW_MARKET_INFORMATION = key("6TBbDMm1uoVbxVrcs8bWnmoyuks4wzHNyQqX3GaEFcgB");
  private static final PublicKey BORROW_PRINCIPAL_MINT = key("dawn7ZUF7h7anFuEsDdAU1Y3HYwikwqNMAENZsQJdNL");
  private static final PublicKey BORROW_COLLATERAL_ORACLE = key("H84M3es89eLfGRDhUFxo7xgTQmhzAx4h2oBexU7WNiGN");

  /// That borrow's health check: the ledger's market information, then the oracle group of the
  /// principal (asset 0 of that market, priced through the beam price account) and of the
  /// collateral (asset 52, priced through `H84M3es8…`), each followed by the zero key.
  private static List<AccountMeta> borrowTail() {
    final var zero = AccountMeta.createRead(ZERO);
    return List.of(
        AccountMeta.createRead(BORROW_MARKET_INFORMATION),
        AccountMeta.createRead(BEAM_PRICES), zero,
        AccountMeta.createRead(BORROW_COLLATERAL_ORACLE), zero
    );
  }

  /// That borrow's instruction data: the `borrow_principal` discriminator, `amount` (u64 LE),
  /// `asset_index_guidance` `[52, 0, 52]` behind its u32 length, `duration` 2, `expected_apy`
  /// 160000, five zero `expected_lqt` (u32 each) and `skip_sol_unwrap` false.
  private static final byte[] BORROW_DATA = HexFormat.of().parseHex(
      "6a0a26cc8bbc7c32"
          + "00e8764817000000"
          + "03000000" + "340034"
          + "02"
          + "0071020000000000"
          + "0000000000000000000000000000000000000000"
          + "00"
  );

  private static Set<PublicKey> keysWith(final List<AccountMeta> accounts, final Predicate<AccountMeta> privilege) {
    return accounts.stream().filter(privilege).map(AccountMeta::publicKey).collect(Collectors.toSet());
  }

  /// Lifted to a top-level call and simulated with the amount lowered to 1,000 (the loan now
  /// carries the debt this very call created, so the full amount fails
  /// `6001 MaintanenceCollateralNotMet`), [#BORROW_ACCEPTED] succeeds (slots 473879577 and
  /// 473884470). With the two event accounts back in front of the health check it fails
  /// `6010 InvalidRemainingAccount` (slots 473879580 and 473884473). The builder is pinned to
  /// the accepted list, key by key and by the privileges a message would grant each key, and to
  /// the call's own data.
  @Test
  void borrowPlusTheLiveTailIsTheListTheProgramAccepts() {
    final var accountClient = SPLAccountClient.createClient(
        SolanaAccounts.MAIN_NET,
        BORROW_VAULT,
        AccountMeta.createFeePayer(BORROW_VAULT)
    );
    final var client = LoopscaleClient.create(accountClient, LoopscaleAccounts.MAIN_NET);
    final var token2022 = SolanaAccounts.MAIN_NET.token2022Program();
    final var borrowerTa = accountClient.splClient().findATA(BORROW_VAULT, token2022, BORROW_PRINCIPAL_MINT).publicKey();
    final var strategyTa = client.strategyTokenAccount(BORROW_STRATEGY, BORROW_PRINCIPAL_MINT, token2022);
    final var params = new BorrowPrincipalParams(
        100_000_000_000L,
        new byte[]{52, 0, 52},
        2,
        new ExpectedLoanValues(160_000L, new long[5]),
        false
    );

    final var asBuilt = client.borrowPrincipal(
        BORROW_VAULT, BORROW_LOAN, BORROW_STRATEGY, BORROW_MARKET_INFORMATION, BORROW_PRINCIPAL_MINT,
        borrowerTa, strategyTa, token2022, params
    );
    assertEquals(BORROW_BUILT_ACCOUNTS, asBuilt.accounts().size(), "the builder stops at protocol_admin_state");

    final var ix = asBuilt.extraAccounts(borrowTail());
    assertEquals(LIVE_PROGRAM, ix.programId().publicKey(), "invoked program");
    assertEquals(
        BORROW_ACCEPTED.stream().map(AccountMeta::publicKey).toList(),
        ix.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order"
    );
    assertEquals(keysWith(BORROW_ACCEPTED, AccountMeta::signer), keysWith(ix.accounts(), AccountMeta::signer), "signers");
    assertEquals(keysWith(BORROW_ACCEPTED, AccountMeta::write), keysWith(ix.accounts(), AccountMeta::write), "writable keys");
    assertArrayEquals(BORROW_DATA, ix.copyData(), "instruction data");
  }
}
