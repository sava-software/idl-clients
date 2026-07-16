package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Verified against the SPL stake-pool program Rust source:
// https://github.com/solana-program/stake-pool/blob/main/program/src/instruction.rs
// Borsh serialization: u8 discriminant (enum ordinal), then fields little-endian.
final class StakePoolProgramTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;

  private static final PublicKey STAKE_POOL_PROGRAM = PublicKey
      .fromBase58Encoded("SPoo1Ku8WFXoNDMHPsrGSTSG1Y47rzgn41SLUNakuHy");
  private static final AccountMeta INVOKED_PROGRAM = AccountMeta.createInvoked(STAKE_POOL_PROGRAM);

  // 32 bytes of 0x01, base58: 4vJ9JU1bJJE96FWSJKvHsmmFADCg4gpZQff4P3bkLKi
  private static final PublicKey STAKE_POOL = key(1);
  // findProgramAddress([STAKE_POOL, "withdraw"], STAKE_POOL_PROGRAM), pre-computed
  // independently so a mutated findStakePoolWithdrawAuthority fails this suite.
  private static final PublicKey WITHDRAW_AUTHORITY = PublicKey
      .fromBase58Encoded("DgW3oUGgemw4Xv9jU2ZRGNsSQdvDbaz487F1a5BGR9rb");

  private static final PublicKey SYSTEM_PROGRAM = PublicKey
      .fromBase58Encoded("11111111111111111111111111111111");
  private static final PublicKey STAKE_PROGRAM = PublicKey
      .fromBase58Encoded("Stake11111111111111111111111111111111111111");
  private static final PublicKey STAKE_CONFIG = PublicKey
      .fromBase58Encoded("StakeConfig11111111111111111111111111111111");
  private static final PublicKey CLOCK_SYSVAR = PublicKey
      .fromBase58Encoded("SysvarC1ock11111111111111111111111111111111");
  private static final PublicKey RENT_SYSVAR = PublicKey
      .fromBase58Encoded("SysvarRent111111111111111111111111111111111");
  private static final PublicKey STAKE_HISTORY_SYSVAR = PublicKey
      .fromBase58Encoded("SysvarStakeHistory1111111111111111111111111");

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static void assertAccount(final AccountMeta accountMeta,
                                    final PublicKey publicKey,
                                    final boolean writable,
                                    final boolean signer) {
    assertEquals(publicKey, accountMeta.publicKey());
    assertEquals(writable, accountMeta.write(), "writable flag for " + publicKey.toBase58());
    assertEquals(signer, accountMeta.signer(), "signer flag for " + publicKey.toBase58());
  }

  @Test
  void depositSol() {
    final var reserveStakeAccount = key(2);
    final var lamportsFrom = key(3);
    final var poolTokenATA = key(4);
    final var poolTokenFeeATA = key(5);
    final var poolTokenReferralFeeATA = key(6);
    final var poolTokenMint = key(7);
    final var tokenProgram = key(8);

    final var ix = StakePoolProgram.depositSol(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        reserveStakeAccount,
        lamportsFrom,
        poolTokenATA,
        poolTokenFeeATA,
        poolTokenReferralFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DepositSol = 14, then u64 lamports_in LE.
    final byte[] expectedData = {
        14,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(10, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(2), reserveStakeAccount, true, false);
    // lamports are debited from the funding account: writable + signer, matching
    // the Rust builder (AccountMeta::new(*lamports_from, true))
    assertAccount(accounts.get(3), lamportsFrom, true, true);
    assertAccount(accounts.get(4), poolTokenATA, true, false);
    assertAccount(accounts.get(5), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(6), poolTokenReferralFeeATA, true, false);
    assertAccount(accounts.get(7), poolTokenMint, true, false);
    assertAccount(accounts.get(8), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(9), tokenProgram, false, false);
  }

  @Test
  void depositSolWithSlippage() {
    final var reserveStakeAccount = key(2);
    final var lamportsFrom = key(3);
    final var poolTokenATA = key(4);
    final var poolTokenFeeATA = key(5);
    final var poolTokenReferralFeeATA = key(6);
    final var poolTokenMint = key(7);
    final var tokenProgram = key(8);

    final var ix = StakePoolProgram.depositSolWithSlippage(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        reserveStakeAccount,
        lamportsFrom,
        poolTokenATA,
        poolTokenFeeATA,
        poolTokenReferralFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L,
        0x0102030405060708L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DepositSolWithSlippage = 25, then u64 lamports_in, u64 minimum_pool_tokens_out LE.
    final byte[] expectedData = {
        25,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(10, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(2), reserveStakeAccount, true, false);
    assertAccount(accounts.get(3), lamportsFrom, true, true);
    assertAccount(accounts.get(4), poolTokenATA, true, false);
    assertAccount(accounts.get(5), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(6), poolTokenReferralFeeATA, true, false);
    assertAccount(accounts.get(7), poolTokenMint, true, false);
    assertAccount(accounts.get(8), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(9), tokenProgram, false, false);
  }

  @Test
  void withdrawSol() {
    final var transferAuthority = key(2);
    final var poolTokenATA = key(3);
    final var reserveStakeAccount = key(4);
    final var receivingAccount = key(5);
    final var poolTokenFeeATA = key(6);
    final var poolTokenMint = key(7);
    final var tokenProgram = key(8);

    final var ix = StakePoolProgram.withdrawSol(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        transferAuthority,
        poolTokenATA,
        reserveStakeAccount,
        receivingAccount,
        poolTokenFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // WithdrawSol = 16, then u64 pool_tokens_in LE.
    final byte[] expectedData = {
        16,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(12, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(2), transferAuthority, false, true);
    assertAccount(accounts.get(3), poolTokenATA, true, false);
    assertAccount(accounts.get(4), reserveStakeAccount, true, false);
    assertAccount(accounts.get(5), receivingAccount, true, false);
    assertAccount(accounts.get(6), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(7), poolTokenMint, true, false);
    assertAccount(accounts.get(8), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(9), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(10), STAKE_PROGRAM, false, false);
    assertAccount(accounts.get(11), tokenProgram, false, false);
  }

  @Test
  void withdrawSolWithSlippage() {
    final var transferAuthority = key(2);
    final var poolTokenATA = key(3);
    final var reserveStakeAccount = key(4);
    final var receivingAccount = key(5);
    final var poolTokenFeeATA = key(6);
    final var poolTokenMint = key(7);
    final var tokenProgram = key(8);

    final var ix = StakePoolProgram.withdrawSolWithSlippage(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        transferAuthority,
        poolTokenATA,
        reserveStakeAccount,
        receivingAccount,
        poolTokenFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L,
        0x0102030405060708L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // WithdrawSolWithSlippage = 26, then u64 pool_tokens_in, u64 minimum_lamports_out LE.
    final byte[] expectedData = {
        26,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(12, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(2), transferAuthority, false, true);
    assertAccount(accounts.get(3), poolTokenATA, true, false);
    assertAccount(accounts.get(4), reserveStakeAccount, true, false);
    assertAccount(accounts.get(5), receivingAccount, true, false);
    assertAccount(accounts.get(6), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(7), poolTokenMint, true, false);
    assertAccount(accounts.get(8), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(9), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(10), STAKE_PROGRAM, false, false);
    assertAccount(accounts.get(11), tokenProgram, false, false);
  }

  @Test
  void depositStake() {
    final var validatorStakeList = key(2);
    final var depositAuthority = key(3);
    final var depositStakeAccount = key(4);
    final var validatorStakeAccount = key(5);
    final var reserveStakeAccount = key(6);
    final var poolTokenATA = key(7);
    final var poolTokenFeeATA = key(8);
    final var poolTokenReferralFeeATA = key(9);
    final var poolTokenMint = key(10);
    final var tokenProgram = key(11);

    final var ix = StakePoolProgram.depositStake(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorStakeList,
        depositAuthority,
        depositStakeAccount,
        validatorStakeAccount,
        reserveStakeAccount,
        poolTokenATA,
        poolTokenFeeATA,
        poolTokenReferralFeeATA,
        poolTokenMint,
        tokenProgram
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DepositStake = 9, no fields.
    assertArrayEquals(new byte[]{9}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(15, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), validatorStakeList, true, false);
    assertAccount(accounts.get(2), depositAuthority, false, true);
    assertAccount(accounts.get(3), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(4), depositStakeAccount, true, false);
    assertAccount(accounts.get(5), validatorStakeAccount, true, false);
    assertAccount(accounts.get(6), reserveStakeAccount, true, false);
    assertAccount(accounts.get(7), poolTokenATA, true, false);
    assertAccount(accounts.get(8), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(9), poolTokenReferralFeeATA, true, false);
    assertAccount(accounts.get(10), poolTokenMint, true, false);
    assertAccount(accounts.get(11), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(12), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(13), tokenProgram, false, false);
    assertAccount(accounts.get(14), STAKE_PROGRAM, false, false);
  }

  @Test
  void depositStakeWithSlippage() {
    final var validatorStakeList = key(2);
    final var depositAuthority = key(3);
    final var depositStakeAccount = key(4);
    final var validatorStakeAccount = key(5);
    final var reserveStakeAccount = key(6);
    final var poolTokenATA = key(7);
    final var poolTokenFeeATA = key(8);
    final var poolTokenReferralFeeATA = key(9);
    final var poolTokenMint = key(10);
    final var tokenProgram = key(11);

    final var ix = StakePoolProgram.depositStakeWithSlippage(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorStakeList,
        depositAuthority,
        depositStakeAccount,
        validatorStakeAccount,
        reserveStakeAccount,
        poolTokenATA,
        poolTokenFeeATA,
        poolTokenReferralFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DepositStakeWithSlippage = 23, then u64 minimum_pool_tokens_out LE.
    final byte[] expectedData = {
        23,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(15, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), validatorStakeList, true, false);
    assertAccount(accounts.get(2), depositAuthority, false, true);
    assertAccount(accounts.get(3), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(4), depositStakeAccount, true, false);
    assertAccount(accounts.get(5), validatorStakeAccount, true, false);
    assertAccount(accounts.get(6), reserveStakeAccount, true, false);
    assertAccount(accounts.get(7), poolTokenATA, true, false);
    assertAccount(accounts.get(8), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(9), poolTokenReferralFeeATA, true, false);
    assertAccount(accounts.get(10), poolTokenMint, true, false);
    assertAccount(accounts.get(11), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(12), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(13), tokenProgram, false, false);
    assertAccount(accounts.get(14), STAKE_PROGRAM, false, false);
  }

  @Test
  void withdrawStake() {
    final var validatorStakeList = key(2);
    final var validatorOrReserveStakeAccount = key(3);
    final var uninitializedStakeAccount = key(4);
    final var stakeAccountWithdrawalAuthority = key(5);
    final var transferAuthority = key(6);
    final var poolTokenATA = key(7);
    final var poolTokenFeeATA = key(8);
    final var poolTokenMint = key(9);
    final var tokenProgram = key(10);

    final var ix = StakePoolProgram.withdrawStake(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorStakeList,
        validatorOrReserveStakeAccount,
        uninitializedStakeAccount,
        stakeAccountWithdrawalAuthority,
        transferAuthority,
        poolTokenATA,
        poolTokenFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // WithdrawStake = 10, then u64 pool tokens LE.
    final byte[] expectedData = {
        10,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(13, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), validatorStakeList, true, false);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorOrReserveStakeAccount, true, false);
    assertAccount(accounts.get(4), uninitializedStakeAccount, true, false);
    assertAccount(accounts.get(5), stakeAccountWithdrawalAuthority, false, false);
    assertAccount(accounts.get(6), transferAuthority, false, true);
    assertAccount(accounts.get(7), poolTokenATA, true, false);
    assertAccount(accounts.get(8), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(9), poolTokenMint, true, false);
    assertAccount(accounts.get(10), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(11), tokenProgram, false, false);
    assertAccount(accounts.get(12), STAKE_PROGRAM, false, false);
  }

  @Test
  void withdrawStakeWithSlippage() {
    final var validatorStakeList = key(2);
    final var validatorOrReserveStakeAccount = key(3);
    final var uninitializedStakeAccount = key(4);
    final var stakeAccountWithdrawalAuthority = key(5);
    final var transferAuthority = key(6);
    final var poolTokenATA = key(7);
    final var poolTokenFeeATA = key(8);
    final var poolTokenMint = key(9);
    final var tokenProgram = key(10);

    final var ix = StakePoolProgram.withdrawStakeWithSlippage(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorStakeList,
        validatorOrReserveStakeAccount,
        uninitializedStakeAccount,
        stakeAccountWithdrawalAuthority,
        transferAuthority,
        poolTokenATA,
        poolTokenFeeATA,
        poolTokenMint,
        tokenProgram,
        0x1122334455667788L,
        0x0102030405060708L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // WithdrawStakeWithSlippage = 24, then u64 pool_tokens_in, u64 minimum_lamports_out LE.
    final byte[] expectedData = {
        24,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(13, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), validatorStakeList, true, false);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorOrReserveStakeAccount, true, false);
    assertAccount(accounts.get(4), uninitializedStakeAccount, true, false);
    assertAccount(accounts.get(5), stakeAccountWithdrawalAuthority, false, false);
    assertAccount(accounts.get(6), transferAuthority, false, true);
    assertAccount(accounts.get(7), poolTokenATA, true, false);
    assertAccount(accounts.get(8), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(9), poolTokenMint, true, false);
    assertAccount(accounts.get(10), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(11), tokenProgram, false, false);
    assertAccount(accounts.get(12), STAKE_PROGRAM, false, false);
  }

  @Test
  void increaseValidatorStake() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var stakePoolReserveAccount = key(4);
    final var transientStakeAccount = key(5);
    final var validatorStakeAccount = key(6);
    final var validatorVoteAccount = key(7);

    final var ix = StakePoolProgram.increaseValidatorStake(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        stakePoolReserveAccount,
        transientStakeAccount,
        validatorStakeAccount,
        validatorVoteAccount,
        0x1122334455667788L,
        0x0102030405060708L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // IncreaseValidatorStake = 4, then u64 lamports, u64 transient_stake_seed LE.
    final byte[] expectedData = {
        4,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    assertArrayEquals(expectedData, ix.data());

    // 14 accounts with the writable transient stake account at index 5, matching
    // the Rust builder increase_validator_stake.
    final var accounts = ix.accounts();
    assertEquals(14, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), stakePoolReserveAccount, true, false);
    assertAccount(accounts.get(5), transientStakeAccount, true, false);
    assertAccount(accounts.get(6), validatorStakeAccount, false, false);
    assertAccount(accounts.get(7), validatorVoteAccount, false, false);
    assertAccount(accounts.get(8), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(9), RENT_SYSVAR, false, false);
    assertAccount(accounts.get(10), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(11), STAKE_CONFIG, false, false);
    assertAccount(accounts.get(12), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(13), STAKE_PROGRAM, false, false);
  }

  @Test
  void decreaseValidatorStakeWithReserve() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var stakePoolReserveStake = key(4);
    final var splitFromStakeAccount = key(5);
    final var transientStakeAccount = key(6);

    final var ix = StakePoolProgram.decreaseValidatorStakeWithReserve(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        stakePoolReserveStake,
        splitFromStakeAccount,
        transientStakeAccount,
        0x1122334455667788L,
        0x0102030405060708L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DecreaseValidatorStakeWithReserve = 21, then u64 lamports, u64 transient_stake_seed LE.
    final byte[] expectedData = {
        21,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(11, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), stakePoolReserveStake, true, false);
    assertAccount(accounts.get(5), splitFromStakeAccount, true, false);
    assertAccount(accounts.get(6), transientStakeAccount, true, false);
    assertAccount(accounts.get(7), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(8), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(9), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(10), STAKE_PROGRAM, false, false);
  }

  @Test
  void addValidatorToPoolWithSeed() {
    final var staker = key(2);
    final var reserveStakeAccount = key(3);
    final var validatorList = key(4);
    final var stakeAccount = key(5);
    final var validator = key(6);

    final var ix = StakePoolProgram.addValidatorToPool(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        reserveStakeAccount,
        validatorList,
        stakeAccount,
        validator,
        0x21222324
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // AddValidatorToPool = 1, then u32 seed LE.
    final byte[] expectedData = {
        1,
        0x24, 0x23, 0x22, 0x21
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(13, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), reserveStakeAccount, true, false);
    assertAccount(accounts.get(3), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(4), validatorList, true, false);
    assertAccount(accounts.get(5), stakeAccount, true, false);
    assertAccount(accounts.get(6), validator, false, false);
    assertAccount(accounts.get(7), RENT_SYSVAR, false, false);
    assertAccount(accounts.get(8), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(9), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(10), STAKE_CONFIG, false, false);
    assertAccount(accounts.get(11), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(12), STAKE_PROGRAM, false, false);
  }

  @Test
  void addValidatorToPoolWithoutSeed() {
    final var staker = key(2);
    final var reserveStakeAccount = key(3);
    final var validatorList = key(4);
    final var stakeAccount = key(5);
    final var validator = key(6);

    final var ix = StakePoolProgram.addValidatorToPool(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        reserveStakeAccount,
        validatorList,
        stakeAccount,
        validator
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // AddValidatorToPool(u32) with strict borsh parsing: the no-seed overload
    // serializes a zero seed, matching the Rust builder's seed.unwrap_or(0).
    assertArrayEquals(new byte[]{1, 0, 0, 0, 0}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(13, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), reserveStakeAccount, true, false);
    assertAccount(accounts.get(3), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(4), validatorList, true, false);
    assertAccount(accounts.get(5), stakeAccount, true, false);
    assertAccount(accounts.get(6), validator, false, false);
    assertAccount(accounts.get(7), RENT_SYSVAR, false, false);
    assertAccount(accounts.get(8), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(9), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(10), STAKE_CONFIG, false, false);
    assertAccount(accounts.get(11), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(12), STAKE_PROGRAM, false, false);
  }

  @Test
  void removeValidatorFromPool() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var stakeAccount = key(4);
    final var transientStakeAccount = key(5);

    final var ix = StakePoolProgram.removeValidatorFromPool(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        stakeAccount,
        transientStakeAccount
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // RemoveValidatorFromPool = 2, no fields.
    assertArrayEquals(new byte[]{2}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(8, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), stakeAccount, true, false);
    assertAccount(accounts.get(5), transientStakeAccount, true, false);
    assertAccount(accounts.get(6), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(7), STAKE_PROGRAM, false, false);
  }

  @Test
  void updateStakePoolBalance() {
    final var validatorList = key(2);
    final var reserveStakeAccount = key(3);
    final var poolTokenFeeATA = key(4);
    final var poolTokenMint = key(5);
    final var tokenProgram = key(6);

    final var ix = StakePoolProgram.updateStakePoolBalance(
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorList,
        reserveStakeAccount,
        poolTokenFeeATA,
        poolTokenMint,
        tokenProgram
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // UpdateStakePoolBalance = 7, no fields.
    assertArrayEquals(new byte[]{7}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(7, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(2), validatorList, true, false);
    assertAccount(accounts.get(3), reserveStakeAccount, false, false);
    assertAccount(accounts.get(4), poolTokenFeeATA, true, false);
    assertAccount(accounts.get(5), poolTokenMint, true, false);
    assertAccount(accounts.get(6), tokenProgram, false, false);
  }
}
