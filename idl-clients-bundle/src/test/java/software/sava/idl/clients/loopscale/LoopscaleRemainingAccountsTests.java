package software.sava.idl.clients.loopscale;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Pins one live `deposit_collateral` made outside a `lock_loan`/`unlock_loan` pair, on a loan
/// with an active ledger, to the hand-written [LoopscaleClient] builder plus the remaining
/// accounts the call carried, appended with `Instruction.extraAccounts`. The call is transaction
/// `2yhdNR2cNeA5tLQCd6ueBNZuFr3xifwoEbfNFwCAiLK2ApVuA92wzBc9Cg2ctgMmvbiKMMAXDHKCvZEE98cJm551`
/// at slot 465207273. It holds no lookup table and no other instruction with accounts (the rest
/// are two compute-budget instructions). Borrower `9Aamt4WZ…`, which also pays, deposits the whole
/// balance of its `5Y8NV33V…` token account into loan `4zQbzjBk…`. Read at slot 465258072, that
/// loan's one active ledger borrows USDC in market `FPxvMwUb…` and its one collateral slot holds
/// `5Y8NV33V…`. Loopscale's protocol admin `CyNKPfqs…` co-signs.
///
/// The builder emits the fourteen accounts the IDL declares, and the program does not accept
/// those alone. Outside a lock pair it runs the loan's health check and reads it from the
/// remaining accounts and the params' `asset_index_guidance`:
///
/// - the ledger's market information, here read-only;
/// - the principal's oracle group, then one group per collateral slot. USDC and `5Y8NV33V…` are
///   both priced through the beam price account `E22Z2nKB…` (oracle type 19), whose group is that
///   account followed by the zero key;
/// - guidance `[0, 1]`: the two assets' indices in the market's asset data.
///
/// The live call also sent a third group and a third guidance byte, which the program does not
/// read. Loan `14UUVmKi…` has the same shape: one active ledger borrowing USDC in market
/// `QHAK6eni…`, where USDC and `5Y8NV33V…` are assets 1 and 17, and `5Y8NV33V…` as its one
/// collateral. A deposit there succeeded with only the first five accounts and two guidance bytes
/// (slot 465278730) and failed `6006 NotEnoughRemainingAccounts` with four accounts (slot
/// 465278728). This test pins what the live call sent, extras included.
///
/// Without the tail the program refuses the call. This builder's output for loan `14UUVmKi…`,
/// with the guidance set but no remaining accounts, failed `6006 NotEnoughRemainingAccounts` at
/// slot 465286769. With the five-account tail appended it succeeded at slot 465286758, using
/// 220,099 compute units. These are simulations against mainnet state with signature verification
/// off: nothing was signed or sent.
final class LoopscaleRemainingAccountsTests {

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static final PublicKey BORROWER = key("9Aamt4WZ6Ngn3STCqBnjhzenGd7k2Mv1bwYCFksCpCok");
  private static final PublicKey LOAN = key("4zQbzjBkbAPcEqzeqwZubrPCK3424dYyBs7ZvkoThLv5");
  private static final PublicKey COLLATERAL_MINT = key("5Y8NV33Vv7WbnLfq3zBcKSdYPrk7g2KoiQoe7M2tcxp5");
  private static final PublicKey MARKET_INFORMATION = key("FPxvMwUbfJHQhC6F2vLNitQdkGfxc44XqefFevpwStNE");
  private static final PublicKey BEAM_PRICES = key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH");
  private static final PublicKey ZERO = key("11111111111111111111111111111111");

  /// The borrower's whole balance in its `5Y8NV33V…` token account before the call (the
  /// transaction's pre-token balances), which the deposit moved into the loan's.
  private static final long AMOUNT = 267_162_266_358L;

  /// What the live call sent as `asset_index_guidance`: USDC's index, the collateral's index, and a
  /// trailing byte the program does not read.
  private static final byte[] LIVE_GUIDANCE = {0, 1, 1};

  /// The number of accounts the generated builder declares for `deposit_collateral`.
  private static final int DECLARED_ACCOUNTS = 14;

  /// The program the live call invoked.
  private static final PublicKey LIVE_PROGRAM = key("1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78");

  /// The live call's instruction data: the `deposit_collateral` discriminator, `amount` (u64 LE),
  /// `asset_type` 0, `asset_identifier` (`5Y8NV33V…`), then `asset_index_guidance` as a u32 length
  /// and its bytes.
  private static final byte[] LIVE_DATA = HexFormat.of().parseHex(
      "9c838e7492f7a278"
          + "f6aa1c343e000000"
          + "00"
          + "43681626afead43e86e92c2d3e0fa635d568a1bde81b89f098da7ac6e5d9f1ae"
          + "03000000" + "000101"
  );

  /// The live call's accounts in order, transcribed from the transaction, each with the privilege
  /// its message grants that key. A message records one privilege per key, the union over the
  /// transaction. Here that is exact for every slot: this is the message's only instruction with
  /// accounts, and each repeated key (payer and borrower, the mint twice, the zero key, the price
  /// account) carries the same privilege everywhere it appears.
  private static final List<AccountMeta> LIVE_ACCOUNTS = List.of(
      AccountMeta.createReadOnlySigner(key("CyNKPfqsSLAejjZtEeNG3pR4SkPhSPHXdGhuNTyudrNs")), // protocol_admin
      AccountMeta.createWritableSigner(key("9Aamt4WZ6Ngn3STCqBnjhzenGd7k2Mv1bwYCFksCpCok")), // payer
      AccountMeta.createWritableSigner(key("9Aamt4WZ6Ngn3STCqBnjhzenGd7k2Mv1bwYCFksCpCok")), // borrower
      AccountMeta.createWrite(key("4zQbzjBkbAPcEqzeqwZubrPCK3424dYyBs7ZvkoThLv5")), // loan
      AccountMeta.createWrite(key("GypeSScADzkNUWUVxwjaA69UBujtQ2hUFNEJia3xYztZ")), // borrower_collateral_ta
      AccountMeta.createWrite(key("Goj4FVbfrXNXBsDeXJ6zuCCGXbHknoasai3numJZ3k3x")), // loan_collateral_ta
      AccountMeta.createRead(key("5Y8NV33Vv7WbnLfq3zBcKSdYPrk7g2KoiQoe7M2tcxp5")), // deposit_mint
      AccountMeta.createRead(key("5Y8NV33Vv7WbnLfq3zBcKSdYPrk7g2KoiQoe7M2tcxp5")), // asset_identifier
      AccountMeta.createRead(key("11111111111111111111111111111111")), // system_program
      AccountMeta.createRead(key("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")), // token_program
      AccountMeta.createRead(key("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL")), // associated_token_program
      AccountMeta.createRead(key("HcgXEnEsgvGowVnSjMmrzSewdx9yGvfXixiuMJPhyW2z")), // protocol_admin_state
      AccountMeta.createRead(key("6sbyEiDvcYELrqoxurz3XTTLLg5wpe8dHCXi4uTvSSTV")), // event_authority
      AccountMeta.createRead(key("1oopBoJG58DgkUVKkEzKgyG9dvRmpgeEm1AVjoHkF78")), // program
      AccountMeta.createRead(key("FPxvMwUbfJHQhC6F2vLNitQdkGfxc44XqefFevpwStNE")), // remaining accounts from here
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111")),
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111")),
      AccountMeta.createRead(key("E22Z2nKBdA3RpJhM8G2mB35zFA95Qdbs3WGbMNxFhmuH")),
      AccountMeta.createRead(key("11111111111111111111111111111111"))
  );

  /// The remaining accounts the live call appended, in the health check's shape: the ledger's
  /// market information, the principal's oracle group, the collateral's oracle group, then the
  /// trailing group the program does not read. Every group here is the beam price account and the
  /// zero key, all read-only.
  private static List<AccountMeta> liveTail() {
    final var marketInformation = AccountMeta.createRead(MARKET_INFORMATION);
    final var beamPrices = AccountMeta.createRead(BEAM_PRICES);
    final var zero = AccountMeta.createRead(ZERO);
    return List.of(
        marketInformation,
        beamPrices, zero, // USDC, asset index 0
        beamPrices, zero, // 5Y8NV33V…, asset index 1
        beamPrices, zero // not read by the program
    );
  }

  /// The short overload is the one under test: it derives the borrower's and the loan's token
  /// accounts and takes the protocol admin, its state, the event authority and the program from
  /// [LoopscaleAccounts#MAIN_NET], so every one of them is checked against a live key. The client
  /// is built inside the test body: coverage attributed to a field initializer is unstable under
  /// PIT.
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
    assertEquals(DECLARED_ACCOUNTS, asBuilt.accounts().size(), "the builder emits the declared accounts only");

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
}
