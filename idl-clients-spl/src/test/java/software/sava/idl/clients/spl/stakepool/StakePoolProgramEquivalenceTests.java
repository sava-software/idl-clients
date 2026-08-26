package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.stakepool.gen.types.Fee;
import software.sava.idl.clients.spl.stakepool.gen.types.FundingType;
import software.sava.idl.clients.spl.stakepool.gen.types.PreferredValidatorType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Every instruction built by the generated client (from `idls/spl_stake_pool.json`) must be
/// byte-identical — program id, account metas in order, and data — to the hand-written
/// {@link StakePoolProgram}, which predates it and encodes the interface independently from
/// the SPL instruction.rs builders. A failure here means either the IDL or the hand-written
/// client has drifted from the on-chain interface; neither may be "fixed" to match the other
/// without consulting the SPL program source.
final class StakePoolProgramEquivalenceTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final PublicKey PROGRAM = PublicKey.fromBase58Encoded("SPoo1Ku8WFXoNDMHPsrGSTSG1Y47rzgn41SLUNakuHy");
  private static final AccountMeta INVOKED = AccountMeta.createInvoked(PROGRAM);

  private static final PublicKey STAKE_POOL = key(1);
  private static final PublicKey VALIDATOR_LIST = key(2);
  private static final PublicKey RESERVE_STAKE = key(3);
  private static final PublicKey POOL_MINT = key(4);
  private static final PublicKey MANAGER = key(5);
  private static final PublicKey STAKER = key(6);
  private static final PublicKey MANAGER_FEE_ACCOUNT = key(7);
  private static final PublicKey TOKEN_PROGRAM = SOLANA_ACCOUNTS.tokenProgram();
  private static final PublicKey VALIDATOR_STAKE = key(8);
  private static final PublicKey TRANSIENT_STAKE = key(9);
  private static final PublicKey VALIDATOR_VOTE = key(10);
  private static final PublicKey EPHEMERAL_STAKE = key(11);
  private static final PublicKey USER_POOL_ACCOUNT = key(12);
  private static final PublicKey REFERRAL_POOL_ACCOUNT = key(13);
  private static final PublicKey TRANSFER_AUTHORITY = key(14);
  private static final PublicKey CUSTOM_DEPOSIT_AUTHORITY = key(15);
  private static final PublicKey WITHDRAW_AUTHORITY = StakePoolProgram
      .findStakePoolWithdrawAuthority(STAKE_POOL, PROGRAM).publicKey();

  private static PublicKey key(final int seed) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    java.util.Arrays.fill(publicKey, (byte) seed);
    return PublicKey.createPubKey(publicKey);
  }

  private static void assertEquivalent(final Instruction handWritten, final Instruction generated) {
    assertEquals(handWritten.programId(), generated.programId());
    final var handWrittenAccounts = handWritten.accounts();
    final var generatedAccounts = generated.accounts();
    assertEquals(handWrittenAccounts.size(), generatedAccounts.size());
    for (int i = 0; i < handWrittenAccounts.size(); ++i) {
      assertEquals(handWrittenAccounts.get(i), generatedAccounts.get(i), "account " + i);
    }
    assertArrayEquals(handWritten.data(), generated.data());
  }

  @Test
  void initialize() {
    final var fee = new StakePoolState.Fee(1_000, 25);
    final var withdrawalFee = new StakePoolState.Fee(1_000, 5);
    final var depositFee = new StakePoolState.Fee(1_000, 3);
    assertEquivalent(
        StakePoolProgram.initialize(
            INVOKED, STAKE_POOL, MANAGER, STAKER, VALIDATOR_LIST, RESERVE_STAKE, POOL_MINT,
            MANAGER_FEE_ACCOUNT, TOKEN_PROGRAM, CUSTOM_DEPOSIT_AUTHORITY,
            fee, withdrawalFee, depositFee, 50, 2_950
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.initialize(
            INVOKED, STAKE_POOL, MANAGER, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST, RESERVE_STAKE,
            POOL_MINT, MANAGER_FEE_ACCOUNT, TOKEN_PROGRAM, CUSTOM_DEPOSIT_AUTHORITY,
            new Fee(1_000, 25), new Fee(1_000, 5), new Fee(1_000, 3), 50, 2_950
        )
    );
  }

  @Test
  void addValidatorToPool() {
    assertEquivalent(
        StakePoolProgram.addValidatorToPool(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, RESERVE_STAKE, VALIDATOR_LIST,
            VALIDATOR_STAKE, VALIDATOR_VOTE, 7
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.addValidatorToPool(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, RESERVE_STAKE, WITHDRAW_AUTHORITY,
            VALIDATOR_LIST, VALIDATOR_STAKE, VALIDATOR_VOTE, 7
        )
    );
  }

  @Test
  void removeValidatorFromPool() {
    assertEquivalent(
        StakePoolProgram.removeValidatorFromPool(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, VALIDATOR_STAKE, TRANSIENT_STAKE
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.removeValidatorFromPool(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            VALIDATOR_STAKE, TRANSIENT_STAKE
        )
    );
  }

  @Test
  void decreaseValidatorStake() {
    assertEquivalent(
        StakePoolProgram.decreaseValidatorStake(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, VALIDATOR_STAKE,
            TRANSIENT_STAKE, 1_234_567, 42
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.decreaseValidatorStake(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            VALIDATOR_STAKE, TRANSIENT_STAKE, 1_234_567, 42
        )
    );
  }

  @Test
  void increaseValidatorStake() {
    assertEquivalent(
        StakePoolProgram.increaseValidatorStake(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, RESERVE_STAKE,
            TRANSIENT_STAKE, VALIDATOR_STAKE, VALIDATOR_VOTE, 1_234_567, 42
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.increaseValidatorStake(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            RESERVE_STAKE, TRANSIENT_STAKE, VALIDATOR_STAKE, VALIDATOR_VOTE, 1_234_567, 42
        )
    );
  }

  @Test
  void setPreferredValidator() {
    assertEquivalent(
        StakePoolProgram.setPreferredValidator(
            INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST,
            StakePoolProgram.PreferredValidatorType.Withdraw, VALIDATOR_VOTE
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setPreferredValidator(
            INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, PreferredValidatorType.withdraw, VALIDATOR_VOTE
        )
    );
    assertEquivalent(
        StakePoolProgram.setPreferredValidator(
            INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST,
            StakePoolProgram.PreferredValidatorType.Deposit, null
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setPreferredValidator(
            INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, PreferredValidatorType.deposit, null
        )
    );
  }

  @Test
  void updateValidatorListBalance() {
    assertEquivalent(
        StakePoolProgram.updateValidatorListBalance(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, VALIDATOR_LIST, RESERVE_STAKE, List.of(), 3, true
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.updateValidatorListBalance(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, WITHDRAW_AUTHORITY, VALIDATOR_LIST, RESERVE_STAKE, 3, true
        )
    );
  }

  @Test
  void updateStakePoolBalance() {
    assertEquivalent(
        StakePoolProgram.updateStakePoolBalance(
            INVOKED, STAKE_POOL, VALIDATOR_LIST, RESERVE_STAKE, MANAGER_FEE_ACCOUNT, POOL_MINT, TOKEN_PROGRAM
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.updateStakePoolBalance(
            INVOKED, STAKE_POOL, WITHDRAW_AUTHORITY, VALIDATOR_LIST, RESERVE_STAKE,
            MANAGER_FEE_ACCOUNT, POOL_MINT, TOKEN_PROGRAM
        )
    );
  }

  @Test
  void cleanupRemovedValidatorEntries() {
    assertEquivalent(
        StakePoolProgram.cleanupRemovedValidatorEntries(INVOKED, STAKE_POOL, VALIDATOR_LIST),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.cleanupRemovedValidatorEntries(
            INVOKED, STAKE_POOL, VALIDATOR_LIST
        )
    );
  }

  /// The custom deposit authority case: the hand-written client marks the authority a signer
  /// exactly when it is not the derived default; the generated client, whose IDL declares the
  /// account `isSigner: "either"`, always requires the signature. For a pool with the default
  /// (permissionless) deposit authority the hand-written form is the correct one, reachable
  /// through the generated client's `List<AccountMeta>` overload.
  @Test
  void depositStake() {
    assertEquivalent(
        StakePoolProgram.depositStake(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, VALIDATOR_LIST, CUSTOM_DEPOSIT_AUTHORITY,
            EPHEMERAL_STAKE, VALIDATOR_STAKE, RESERVE_STAKE, USER_POOL_ACCOUNT,
            MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT, POOL_MINT, TOKEN_PROGRAM
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.depositStake(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, VALIDATOR_LIST, CUSTOM_DEPOSIT_AUTHORITY,
            WITHDRAW_AUTHORITY, EPHEMERAL_STAKE, VALIDATOR_STAKE, RESERVE_STAKE, USER_POOL_ACCOUNT,
            MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT, POOL_MINT, TOKEN_PROGRAM
        )
    );
  }

  @Test
  void depositStakeWithSlippage() {
    assertEquivalent(
        StakePoolProgram.depositStakeWithSlippage(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, VALIDATOR_LIST, CUSTOM_DEPOSIT_AUTHORITY,
            EPHEMERAL_STAKE, VALIDATOR_STAKE, RESERVE_STAKE, USER_POOL_ACCOUNT,
            MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT, POOL_MINT, TOKEN_PROGRAM, 9_876
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.depositStakeWithSlippage(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, VALIDATOR_LIST, CUSTOM_DEPOSIT_AUTHORITY,
            WITHDRAW_AUTHORITY, EPHEMERAL_STAKE, VALIDATOR_STAKE, RESERVE_STAKE, USER_POOL_ACCOUNT,
            MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT, POOL_MINT, TOKEN_PROGRAM, 9_876
        )
    );
  }

  @Test
  void withdrawStake() {
    assertEquivalent(
        StakePoolProgram.withdrawStake(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, VALIDATOR_LIST, VALIDATOR_STAKE, EPHEMERAL_STAKE,
            VALIDATOR_VOTE, TRANSFER_AUTHORITY, USER_POOL_ACCOUNT, MANAGER_FEE_ACCOUNT, POOL_MINT,
            TOKEN_PROGRAM, 4_321
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.withdrawStake(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, VALIDATOR_LIST, WITHDRAW_AUTHORITY, VALIDATOR_STAKE,
            EPHEMERAL_STAKE, VALIDATOR_VOTE, TRANSFER_AUTHORITY, USER_POOL_ACCOUNT,
            MANAGER_FEE_ACCOUNT, POOL_MINT, TOKEN_PROGRAM, 4_321
        )
    );
  }

  @Test
  void withdrawStakeWithSlippage() {
    assertEquivalent(
        StakePoolProgram.withdrawStakeWithSlippage(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, VALIDATOR_LIST, VALIDATOR_STAKE, EPHEMERAL_STAKE,
            VALIDATOR_VOTE, TRANSFER_AUTHORITY, USER_POOL_ACCOUNT, MANAGER_FEE_ACCOUNT, POOL_MINT,
            TOKEN_PROGRAM, 4_321, 4_000
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.withdrawStakeWithSlippage(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, VALIDATOR_LIST, WITHDRAW_AUTHORITY, VALIDATOR_STAKE,
            EPHEMERAL_STAKE, VALIDATOR_VOTE, TRANSFER_AUTHORITY, USER_POOL_ACCOUNT,
            MANAGER_FEE_ACCOUNT, POOL_MINT, TOKEN_PROGRAM, 4_321, 4_000
        )
    );
  }

  @Test
  void setManager() {
    assertEquivalent(
        StakePoolProgram.setManager(INVOKED, STAKE_POOL, MANAGER, STAKER, MANAGER_FEE_ACCOUNT),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setManager(
            INVOKED, STAKE_POOL, MANAGER, STAKER, MANAGER_FEE_ACCOUNT
        )
    );
  }

  @Test
  void setFee() {
    assertEquivalent(
        StakePoolProgram.setFee(INVOKED, STAKE_POOL, MANAGER,
            new FeeType.Epoch(new StakePoolState.Fee(100, 3))),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setFee(
            INVOKED, STAKE_POOL, MANAGER,
            new software.sava.idl.clients.spl.stakepool.gen.types.FeeType.epoch(new Fee(100, 3))
        )
    );
    assertEquivalent(
        StakePoolProgram.setFee(INVOKED, STAKE_POOL, MANAGER, new FeeType.SolReferral(12)),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setFee(
            INVOKED, STAKE_POOL, MANAGER,
            new software.sava.idl.clients.spl.stakepool.gen.types.FeeType.solReferral(12)
        )
    );
  }

  @Test
  void setStaker() {
    assertEquivalent(
        StakePoolProgram.setStaker(INVOKED, STAKE_POOL, MANAGER, STAKER),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setStaker(
            INVOKED, STAKE_POOL, MANAGER, STAKER
        )
    );
  }

  @Test
  void depositSol() {
    assertEquivalent(
        StakePoolProgram.depositSol(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, RESERVE_STAKE, TRANSFER_AUTHORITY,
            USER_POOL_ACCOUNT, MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT, POOL_MINT,
            TOKEN_PROGRAM, 777_777
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.depositSol(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, WITHDRAW_AUTHORITY, RESERVE_STAKE,
            TRANSFER_AUTHORITY, USER_POOL_ACCOUNT, MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT,
            POOL_MINT, TOKEN_PROGRAM, null, 777_777
        )
    );
  }

  @Test
  void depositSolWithSlippage() {
    assertEquivalent(
        StakePoolProgram.depositSolWithSlippage(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, RESERVE_STAKE, TRANSFER_AUTHORITY,
            USER_POOL_ACCOUNT, MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT, POOL_MINT,
            TOKEN_PROGRAM, 777_777, 700_000
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.depositSolWithSlippage(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, WITHDRAW_AUTHORITY, RESERVE_STAKE,
            TRANSFER_AUTHORITY, USER_POOL_ACCOUNT, MANAGER_FEE_ACCOUNT, REFERRAL_POOL_ACCOUNT,
            POOL_MINT, TOKEN_PROGRAM, null, 777_777, 700_000
        )
    );
  }

  @Test
  void setFundingAuthority() {
    assertEquivalent(
        StakePoolProgram.setFundingAuthority(
            INVOKED, STAKE_POOL, MANAGER, TRANSFER_AUTHORITY, StakePoolProgram.FundingType.SolWithdraw
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.setFundingAuthority(
            INVOKED, STAKE_POOL, MANAGER, TRANSFER_AUTHORITY, FundingType.solWithdraw
        )
    );
  }

  @Test
  void withdrawSol() {
    assertEquivalent(
        StakePoolProgram.withdrawSol(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, TRANSFER_AUTHORITY, USER_POOL_ACCOUNT,
            RESERVE_STAKE, EPHEMERAL_STAKE, MANAGER_FEE_ACCOUNT, POOL_MINT, TOKEN_PROGRAM, 55_555
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.withdrawSol(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, WITHDRAW_AUTHORITY, TRANSFER_AUTHORITY,
            USER_POOL_ACCOUNT, RESERVE_STAKE, EPHEMERAL_STAKE, MANAGER_FEE_ACCOUNT, POOL_MINT,
            TOKEN_PROGRAM, null, 55_555
        )
    );
  }

  @Test
  void withdrawSolWithSlippage() {
    assertEquivalent(
        StakePoolProgram.withdrawSolWithSlippage(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, TRANSFER_AUTHORITY, USER_POOL_ACCOUNT,
            RESERVE_STAKE, EPHEMERAL_STAKE, MANAGER_FEE_ACCOUNT, POOL_MINT, TOKEN_PROGRAM,
            55_555, 54_000
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.withdrawSolWithSlippage(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, WITHDRAW_AUTHORITY, TRANSFER_AUTHORITY,
            USER_POOL_ACCOUNT, RESERVE_STAKE, EPHEMERAL_STAKE, MANAGER_FEE_ACCOUNT, POOL_MINT,
            TOKEN_PROGRAM, null, 55_555, 54_000
        )
    );
  }

  @Test
  void createTokenMetadata() {
    final var metadataProgram = key(16);
    final var tokenMetadata = key(17);
    assertEquivalent(
        StakePoolProgram.createTokenMetadata(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, MANAGER, POOL_MINT, TRANSFER_AUTHORITY,
            tokenMetadata, metadataProgram, "Test Pool", "tstSOL", "https://example.com/meta.json"
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.createTokenMetadata(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, MANAGER, WITHDRAW_AUTHORITY, POOL_MINT,
            TRANSFER_AUTHORITY, tokenMetadata, metadataProgram,
            "Test Pool", "tstSOL", "https://example.com/meta.json"
        )
    );
  }

  @Test
  void updateTokenMetadata() {
    final var metadataProgram = key(16);
    final var tokenMetadata = key(17);
    assertEquivalent(
        StakePoolProgram.updateTokenMetadata(
            INVOKED, STAKE_POOL, MANAGER, tokenMetadata, metadataProgram,
            "Test Pool", "tstSOL", "https://example.com/meta.json"
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.updateTokenMetadata(
            INVOKED, STAKE_POOL, MANAGER, WITHDRAW_AUTHORITY, tokenMetadata, metadataProgram,
            "Test Pool", "tstSOL", "https://example.com/meta.json"
        )
    );
  }

  @Test
  void increaseAdditionalValidatorStake() {
    assertEquivalent(
        StakePoolProgram.increaseAdditionalValidatorStake(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, RESERVE_STAKE,
            EPHEMERAL_STAKE, TRANSIENT_STAKE, VALIDATOR_STAKE, VALIDATOR_VOTE, 1_234_567, 42, 43
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.increaseAdditionalValidatorStake(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            RESERVE_STAKE, EPHEMERAL_STAKE, TRANSIENT_STAKE, VALIDATOR_STAKE, VALIDATOR_VOTE,
            1_234_567, 42, 43
        )
    );
  }

  @Test
  void decreaseAdditionalValidatorStake() {
    assertEquivalent(
        StakePoolProgram.decreaseAdditionalValidatorStake(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, RESERVE_STAKE,
            VALIDATOR_STAKE, EPHEMERAL_STAKE, TRANSIENT_STAKE, 1_234_567, 42, 43
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.decreaseAdditionalValidatorStake(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            RESERVE_STAKE, VALIDATOR_STAKE, EPHEMERAL_STAKE, TRANSIENT_STAKE, 1_234_567, 42, 43
        )
    );
  }

  @Test
  void decreaseValidatorStakeWithReserve() {
    assertEquivalent(
        StakePoolProgram.decreaseValidatorStakeWithReserve(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, RESERVE_STAKE,
            VALIDATOR_STAKE, TRANSIENT_STAKE, 1_234_567, 42
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.decreaseValidatorStakeWithReserve(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            RESERVE_STAKE, VALIDATOR_STAKE, TRANSIENT_STAKE, 1_234_567, 42
        )
    );
  }

  @Test
  void redelegate() {
    final var destinationTransientStake = key(18);
    final var destinationValidatorStake = key(19);
    assertEquivalent(
        StakePoolProgram.redelegate(
            SOLANA_ACCOUNTS, INVOKED, STAKE_POOL, STAKER, VALIDATOR_LIST, RESERVE_STAKE,
            VALIDATOR_STAKE, TRANSIENT_STAKE, EPHEMERAL_STAKE, destinationTransientStake,
            destinationValidatorStake, VALIDATOR_VOTE, 1_234_567, 42, 43, 44
        ),
        software.sava.idl.clients.spl.stakepool.gen.StakePoolProgram.redelegate(
            INVOKED, SOLANA_ACCOUNTS, STAKE_POOL, STAKER, WITHDRAW_AUTHORITY, VALIDATOR_LIST,
            RESERVE_STAKE, VALIDATOR_STAKE, TRANSIENT_STAKE, EPHEMERAL_STAKE,
            destinationTransientStake, destinationValidatorStake, VALIDATOR_VOTE,
            1_234_567, 42, 43, 44
        )
    );
  }
}
