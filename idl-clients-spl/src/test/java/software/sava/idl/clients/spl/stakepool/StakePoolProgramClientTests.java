package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.stake.LockUp;
import software.sava.idl.clients.spl.stakepool.StakePoolState.Fee;
import software.sava.rpc.json.http.response.AccountInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Coverage for the stake-pool client. Every builder reads a handful of addresses *off the
/// pool state* — reserve stake, manager fee account, pool mint, token program — and drops them
/// into positional slots. Those fields are all `PublicKey`, so nothing but a distinct value per
/// field can catch one being read from the wrong accessor; the fixture below gives each its own
/// byte pattern.
///
/// The `AccountInfo` overloads are checked by equivalence against the explicit-argument form,
/// which is the whole of their contract: unpack `owner()` as the pool program and `data()` as
/// the state, in that order.
final class StakePoolProgramClientTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;

  private static final PublicKey POOL_PROGRAM = key(0x51);
  private static final PublicKey POOL = key(0x52);
  private static final PublicKey VALIDATOR_LIST = key(0x53);
  private static final PublicKey RESERVE_STAKE = key(0x54);
  private static final PublicKey POOL_MINT = key(0x55);
  private static final PublicKey MANAGER_FEE = key(0x56);
  private static final PublicKey TOKEN_PROGRAM = key(0x57);
  private static final PublicKey OWNER = key(0x58);
  private static final PublicKey FEE_PAYER = key(0x59);
  private static final PublicKey POOL_TOKEN_ATA = key(0x5A);
  private static final PublicKey DEPOSIT_STAKE = key(0x5B);
  private static final PublicKey VALIDATOR_STAKE = key(0x5C);
  private static final PublicKey UNINITIALIZED_STAKE = key(0x5D);
  private static final PublicKey STAKE_WITHDRAW_AUTHORITY = key(0x5E);

  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));
  private static final StakePoolProgramClient CLIENT =
      StakePoolProgramClient.createClient(ACCOUNT_CLIENT, StakePoolAccounts.MAIN_NET);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    java.util.Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  private static final Fee FEE = new Fee(1000L, 25L);

  private static final StakePoolState STATE = new StakePoolState(
      POOL,
      AccountType.StakePool,
      key(0x61), // manager
      key(0x62), // staker
      key(0x63), // stakeDepositAuthority
      255,       // stakeWithdrawBumpSeed
      VALIDATOR_LIST,
      RESERVE_STAKE,
      POOL_MINT,
      MANAGER_FEE,
      TOKEN_PROGRAM,
      BigDecimal.valueOf(10L), // totalLamports
      BigDecimal.valueOf(4L),  // poolTokenSupply
      7L,                      // lastUpdateEpoch
      LockUp.NO_LOCKUP,
      FEE, null,
      null, null,
      FEE, FEE, null,
      0,
      null,
      FEE,
      0,
      null,
      FEE, null,
      0L, 0L
  );

  /// An `AccountInfo` wrapping the state, owned by the pool program — the owner *is* how the
  /// convenience overloads learn which program to invoke.
  private static AccountInfo<StakePoolState> accountInfo() {
    return new AccountInfo<>(POOL, null, false, 0L, POOL_PROGRAM, BigInteger.ZERO, 0, STATE);
  }

  @Test
  void clientExposesItsDependencies() {
    assertSame(ACCOUNT_CLIENT, CLIENT.splAccountClient());
    assertEquals(ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(StakePoolAccounts.MAIN_NET, CLIENT.stakePoolAccounts());
    // the owner comes from the account client, not the fee payer
    assertEquals(OWNER, CLIENT.ownerPublicKey());
    assertNotEquals(FEE_PAYER, CLIENT.ownerPublicKey());

    // the single-argument factory defaults to the main-net pool addresses
    final var defaulted = StakePoolProgramClient.createClient(ACCOUNT_CLIENT);
    assertEquals(StakePoolAccounts.MAIN_NET, defaulted.stakePoolAccounts());
    assertEquals(OWNER, defaulted.ownerPublicKey());
  }

  // ---------------------------------------------------------------------------
  // Deposits
  // ---------------------------------------------------------------------------

  @Test
  void depositSolReadsTheReserveAndFeeAccountsOffTheState() {
    final var ix = CLIENT.depositSol(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 5_000L);

    assertEquals(AccountMeta.createInvoked(POOL_PROGRAM), ix.programId());
    final var accounts = keys(ix);
    assertTrue(accounts.contains(POOL), "the pool account");
    assertTrue(accounts.contains(RESERVE_STAKE), "SOL is deposited into the reserve");
    assertTrue(accounts.contains(MANAGER_FEE), "the manager takes a fee");
    assertTrue(accounts.contains(POOL_MINT), "pool tokens are minted");
    assertTrue(accounts.contains(TOKEN_PROGRAM));
    assertTrue(accounts.contains(POOL_TOKEN_ATA));
    // the depositing owner signs
    assertTrue(ix.accounts().stream().anyMatch(m -> m.publicKey().equals(OWNER) && m.signer()));
  }

  @Test
  void depositSolWithSlippageCarriesTheMinimumOut() {
    final var plain = CLIENT.depositSol(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 5_000L);
    final var slippage = CLIENT.depositSolWithSlippage(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 5_000L, 4_900L);

    // same accounts, but a distinct instruction carrying the extra bound
    assertEquals(keys(plain), keys(slippage));
    assertNotEquals(plain.data()[0], slippage.data()[0], "a different discriminator");
    assertEquals(plain.data().length + Long.BYTES, slippage.data().length);
    assertEquals(4_900L, software.sava.core.encoding.ByteUtil.getInt64LE(slippage.data(), 9));
  }

  @Test
  void depositStakeReadsTheValidatorListOffTheState() {
    final var ix = CLIENT.depositStake(POOL_PROGRAM, STATE, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA);

    final var accounts = keys(ix);
    assertTrue(accounts.contains(POOL));
    assertTrue(accounts.contains(VALIDATOR_LIST), "a stake deposit touches the validator list");
    assertTrue(accounts.contains(DEPOSIT_STAKE));
    assertTrue(accounts.contains(VALIDATOR_STAKE));
    assertTrue(accounts.contains(RESERVE_STAKE));
    assertTrue(accounts.contains(MANAGER_FEE));
    assertTrue(accounts.contains(POOL_MINT));
    // the deposit and validator stake accounts are distinct slots, not the same key twice
    assertNotEquals(accounts.indexOf(DEPOSIT_STAKE), accounts.indexOf(VALIDATOR_STAKE));
  }

  @Test
  void depositStakeWithSlippageCarriesTheMinimumOut() {
    final var plain = CLIENT.depositStake(POOL_PROGRAM, STATE, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA);
    final var slippage = CLIENT.depositStakeWithSlippage(
        POOL_PROGRAM, STATE, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA, 4_900L);

    assertEquals(keys(plain), keys(slippage));
    assertNotEquals(plain.data()[0], slippage.data()[0]);
    assertEquals(4_900L, software.sava.core.encoding.ByteUtil.getInt64LE(slippage.data(), 1));
  }

  // ---------------------------------------------------------------------------
  // Withdrawals
  // ---------------------------------------------------------------------------

  @Test
  void withdrawSolReturnsLamportsToTheOwner() {
    final var ix = CLIENT.withdrawSol(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 1_000L);

    final var accounts = keys(ix);
    assertTrue(accounts.contains(POOL));
    assertTrue(accounts.contains(RESERVE_STAKE), "SOL comes out of the reserve");
    assertTrue(accounts.contains(MANAGER_FEE));
    assertTrue(accounts.contains(POOL_MINT));
    assertTrue(accounts.contains(POOL_TOKEN_ATA));
    // the owner is both the burn authority and the lamport recipient
    assertTrue(accounts.contains(OWNER));
    assertTrue(ix.accounts().stream().anyMatch(m -> m.publicKey().equals(OWNER) && m.signer()));
  }

  @Test
  void withdrawSolWithSlippageCarriesTheMinimumOut() {
    final var plain = CLIENT.withdrawSol(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 1_000L);
    final var slippage = CLIENT.withdrawSolWithSlippage(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 1_000L, 900L);

    assertEquals(keys(plain), keys(slippage));
    assertNotEquals(plain.data()[0], slippage.data()[0]);
    assertEquals(900L, software.sava.core.encoding.ByteUtil.getInt64LE(slippage.data(), 9));
  }

  @Test
  void withdrawStakeSplitsIntoAnUninitializedAccount() {
    final var ix = CLIENT.withdrawStake(
        POOL_PROGRAM, STATE, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L);

    final var accounts = keys(ix);
    assertTrue(accounts.contains(POOL));
    assertTrue(accounts.contains(VALIDATOR_LIST));
    assertTrue(accounts.contains(VALIDATOR_STAKE), "the source stake account");
    assertTrue(accounts.contains(UNINITIALIZED_STAKE), "the destination stake account");
    assertTrue(accounts.contains(STAKE_WITHDRAW_AUTHORITY));
    assertTrue(accounts.contains(MANAGER_FEE));
    assertTrue(accounts.contains(POOL_MINT));
    // source and destination occupy different slots
    assertNotEquals(accounts.indexOf(VALIDATOR_STAKE), accounts.indexOf(UNINITIALIZED_STAKE));
  }

  @Test
  void withdrawStakeWithSlippageCarriesTheMinimumOut() {
    final var plain = CLIENT.withdrawStake(
        POOL_PROGRAM, STATE, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L);
    final var slippage = CLIENT.withdrawStakeWithSlippage(
        POOL_PROGRAM, STATE, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L, 900L);

    assertEquals(keys(plain), keys(slippage));
    assertNotEquals(plain.data()[0], slippage.data()[0]);
    assertEquals(900L, software.sava.core.encoding.ByteUtil.getInt64LE(slippage.data(), 9));
  }

  @Test
  void updateStakePoolBalanceTouchesTheReserveAndFeeAccounts() {
    final var ix = CLIENT.updateStakePoolBalance(POOL_PROGRAM, STATE);

    assertEquals(AccountMeta.createInvoked(POOL_PROGRAM), ix.programId());
    final var accounts = keys(ix);
    assertTrue(accounts.contains(POOL));
    assertTrue(accounts.contains(VALIDATOR_LIST));
    assertTrue(accounts.contains(RESERVE_STAKE));
    assertTrue(accounts.contains(MANAGER_FEE));
    assertTrue(accounts.contains(POOL_MINT));
    assertTrue(accounts.contains(TOKEN_PROGRAM));
  }

  // ---------------------------------------------------------------------------
  // AccountInfo overloads: unpack owner() as the program and data() as the state
  // ---------------------------------------------------------------------------

  /// Each convenience overload must reach for `owner()` as the invoked program and `data()` as
  /// the state. Swapping or dropping either yields a different instruction, so equivalence with
  /// the explicit form is the whole contract.
  @Test
  void accountInfoOverloadsMatchTheExplicitForm() {
    final var info = accountInfo();

    assertEquals(
        CLIENT.depositSol(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 5_000L),
        CLIENT.depositSol(info, POOL_TOKEN_ATA, 5_000L));

    assertEquals(
        CLIENT.depositSolWithSlippage(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 5_000L, 4_900L),
        CLIENT.depositSolWithSlippage(info, POOL_TOKEN_ATA, 5_000L, 4_900L));

    assertEquals(
        CLIENT.depositStake(POOL_PROGRAM, STATE, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA),
        CLIENT.depositStake(info, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA));

    assertEquals(
        CLIENT.depositStakeWithSlippage(POOL_PROGRAM, STATE, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA, 4_900L),
        CLIENT.depositStakeWithSlippage(info, DEPOSIT_STAKE, VALIDATOR_STAKE, POOL_TOKEN_ATA, 4_900L));

    assertEquals(
        CLIENT.withdrawSol(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 1_000L),
        CLIENT.withdrawSol(info, POOL_TOKEN_ATA, 1_000L));

    assertEquals(
        CLIENT.withdrawSolWithSlippage(POOL_PROGRAM, STATE, POOL_TOKEN_ATA, 1_000L, 900L),
        CLIENT.withdrawSolWithSlippage(info, POOL_TOKEN_ATA, 1_000L, 900L));

    assertEquals(
        CLIENT.withdrawStake(POOL_PROGRAM, STATE, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L),
        CLIENT.withdrawStake(info, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L));

    assertEquals(
        CLIENT.withdrawStakeWithSlippage(POOL_PROGRAM, STATE, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L, 900L),
        CLIENT.withdrawStakeWithSlippage(info, VALIDATOR_STAKE, UNINITIALIZED_STAKE, STAKE_WITHDRAW_AUTHORITY, POOL_TOKEN_ATA, 1_000L, 900L));

    assertEquals(
        CLIENT.updateStakePoolBalance(POOL_PROGRAM, STATE),
        CLIENT.updateStakePoolBalance(info));
  }

  /// The short `withdrawStakeWithSlippage` overload omits the withdrawal authority, defaulting
  /// it to the client's owner — the account that is burning the pool tokens.
  @Test
  void withdrawStakeWithSlippageDefaultsTheAuthorityToTheOwner() {
    final var info = accountInfo();

    final var explicit = CLIENT.withdrawStakeWithSlippage(
        info, VALIDATOR_STAKE, UNINITIALIZED_STAKE, OWNER, POOL_TOKEN_ATA, 1_000L, 900L);
    final var defaulted = CLIENT.withdrawStakeWithSlippage(
        info, VALIDATOR_STAKE, UNINITIALIZED_STAKE, POOL_TOKEN_ATA, 1_000L, 900L);

    assertEquals(explicit, defaulted);

    // and it really is the owner, not the fee payer or some other account
    assertNotEquals(
        defaulted,
        CLIENT.withdrawStakeWithSlippage(
            info, VALIDATOR_STAKE, UNINITIALIZED_STAKE, FEE_PAYER, POOL_TOKEN_ATA, 1_000L, 900L));
  }

  /// The pool program comes from the account *owner*, so an identical state owned by a
  /// different program must produce a differently-invoked instruction.
  @Test
  void accountInfoOwnerSelectsTheInvokedProgram() {
    final var other = key(0x71);
    final var otherInfo = new AccountInfo<>(POOL, null, false, 0L, other, BigInteger.ZERO, 0, STATE);

    assertEquals(
        AccountMeta.createInvoked(other),
        CLIENT.depositSol(otherInfo, POOL_TOKEN_ATA, 5_000L).programId());
    assertNotEquals(
        CLIENT.depositSol(accountInfo(), POOL_TOKEN_ATA, 5_000L).programId(),
        CLIENT.depositSol(otherInfo, POOL_TOKEN_ATA, 5_000L).programId());
  }

  @Test
  void findStakePoolWithdrawAuthorityDerivesFromThePoolAndProgram() {
    final var authority = StakePoolProgramClient.findStakePoolWithdrawAuthority(accountInfo());

    assertNotNull(authority.publicKey());
    assertEquals(
        StakePoolProgram.findStakePoolWithdrawAuthority(POOL, POOL_PROGRAM).publicKey(),
        authority.publicKey());

    // it is derived from both inputs, so changing either moves it
    final var otherPool = new AccountInfo<>(key(0x72), null, false, 0L, POOL_PROGRAM, BigInteger.ZERO, 0, STATE);
    assertNotEquals(
        authority.publicKey(),
        StakePoolProgramClient.findStakePoolWithdrawAuthority(otherPool).publicKey());
  }
}
