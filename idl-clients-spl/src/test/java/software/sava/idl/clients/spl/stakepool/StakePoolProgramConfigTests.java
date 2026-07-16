package software.sava.idl.clients.spl.stakepool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Verified against the SPL stake-pool program Rust source:
// https://github.com/solana-program/stake-pool/blob/main/program/src/instruction.rs
// Borsh serialization: u8 discriminant (enum ordinal), then fields little-endian.
//
// Covers the pool configuration / management builders not exercised by
// StakePoolProgramTests.
final class StakePoolProgramConfigTests {

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
  // findProgramAddress([STAKE_POOL, "deposit"], STAKE_POOL_PROGRAM), pre-computed
  // independently so a mutated findStakePoolDepositAuthority fails this suite.
  private static final PublicKey DEPOSIT_AUTHORITY = PublicKey
      .fromBase58Encoded("FdPS8MAVVXg3DTHyjTzd2jJTBF8yQaTCiHzAn2Ym8Euf");

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
  void initialize() {
    final var manager = key(2);
    final var staker = key(3);
    final var validatorList = key(4);
    final var reserveStakeAccount = key(5);
    final var poolTokenMint = key(6);
    final var feeAccount = key(7);
    final var tokenProgram = key(8);
    final var depositAuthority = key(9);

    final var ix = StakePoolProgram.initialize(
        INVOKED_PROGRAM,
        STAKE_POOL,
        manager,
        staker,
        validatorList,
        reserveStakeAccount,
        poolTokenMint,
        feeAccount,
        tokenProgram,
        depositAuthority,
        new StakePoolState.Fee(0x1112131415161718L, 0x2122232425262728L),
        new StakePoolState.Fee(0x3132333435363738L, 0x4142434445464748L),
        new StakePoolState.Fee(0x5152535455565758L, 0x6162636465666768L),
        0x7F,
        0x0A0B0C0D
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // Initialize = 0, then Fee fee (u64 denominator, u64 numerator LE),
    // Fee withdrawal_fee, Fee deposit_fee, u8 referral_fee, u32 max_validators LE.
    final byte[] expectedData = {
        0,
        0x18, 0x17, 0x16, 0x15, 0x14, 0x13, 0x12, 0x11,
        0x28, 0x27, 0x26, 0x25, 0x24, 0x23, 0x22, 0x21,
        0x38, 0x37, 0x36, 0x35, 0x34, 0x33, 0x32, 0x31,
        0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
        0x58, 0x57, 0x56, 0x55, 0x54, 0x53, 0x52, 0x51,
        0x68, 0x67, 0x66, 0x65, 0x64, 0x63, 0x62, 0x61,
        0x7F,
        0x0D, 0x0C, 0x0B, 0x0A
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(10, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), manager, false, true);
    assertAccount(accounts.get(2), staker, false, false);
    assertAccount(accounts.get(3), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(4), validatorList, true, false);
    assertAccount(accounts.get(5), reserveStakeAccount, false, false);
    // NOTE: the Rust builder fn initialize marks the pool mint and the manager fee
    // account writable (AccountMeta::new) and the optional deposit authority as a
    // signer, while the StakePoolInstruction::Initialize doc comment lists all
    // three as read-only non-signers. The Java builder follows the doc comment;
    // these assertions pin that behavior.
    assertAccount(accounts.get(6), poolTokenMint, false, false);
    assertAccount(accounts.get(7), feeAccount, false, false);
    assertAccount(accounts.get(8), tokenProgram, false, false);
    assertAccount(accounts.get(9), depositAuthority, false, false);
  }

  @Test
  void initializeWithDerivedDepositAuthority() {
    final var manager = key(2);
    final var staker = key(3);
    final var validatorList = key(4);
    final var reserveStakeAccount = key(5);
    final var poolTokenMint = key(6);
    final var feeAccount = key(7);
    final var tokenProgram = key(8);

    final var ix = StakePoolProgram.initialize(
        INVOKED_PROGRAM,
        STAKE_POOL,
        manager,
        staker,
        validatorList,
        reserveStakeAccount,
        poolTokenMint,
        feeAccount,
        tokenProgram,
        new StakePoolState.Fee(0x1112131415161718L, 0x2122232425262728L),
        new StakePoolState.Fee(0x3132333435363738L, 0x4142434445464748L),
        new StakePoolState.Fee(0x5152535455565758L, 0x6162636465666768L),
        0x7F,
        0x0A0B0C0D
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    final byte[] expectedData = {
        0,
        0x18, 0x17, 0x16, 0x15, 0x14, 0x13, 0x12, 0x11,
        0x28, 0x27, 0x26, 0x25, 0x24, 0x23, 0x22, 0x21,
        0x38, 0x37, 0x36, 0x35, 0x34, 0x33, 0x32, 0x31,
        0x48, 0x47, 0x46, 0x45, 0x44, 0x43, 0x42, 0x41,
        0x58, 0x57, 0x56, 0x55, 0x54, 0x53, 0x52, 0x51,
        0x68, 0x67, 0x66, 0x65, 0x64, 0x63, 0x62, 0x61,
        0x7F,
        0x0D, 0x0C, 0x0B, 0x0A
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(10, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), manager, false, true);
    assertAccount(accounts.get(2), staker, false, false);
    assertAccount(accounts.get(3), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(4), validatorList, true, false);
    assertAccount(accounts.get(5), reserveStakeAccount, false, false);
    assertAccount(accounts.get(6), poolTokenMint, false, false);
    assertAccount(accounts.get(7), feeAccount, false, false);
    assertAccount(accounts.get(8), tokenProgram, false, false);
    // find_deposit_authority_program_address([STAKE_POOL, "deposit"]).
    assertAccount(accounts.get(9), DEPOSIT_AUTHORITY, false, false);
  }

  @Test
  void decreaseValidatorStake() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var splitFromStakeAccount = key(4);
    final var transientStakeAccount = key(5);

    final var ix = StakePoolProgram.decreaseValidatorStake(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        splitFromStakeAccount,
        transientStakeAccount,
        0x1122334455667788L,
        0x0102030405060708L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DecreaseValidatorStake = 3, then u64 lamports, u64 transient_stake_seed LE.
    final byte[] expectedData = {
        3,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(10, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), splitFromStakeAccount, true, false);
    assertAccount(accounts.get(5), transientStakeAccount, true, false);
    assertAccount(accounts.get(6), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(7), RENT_SYSVAR, false, false);
    assertAccount(accounts.get(8), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(9), STAKE_PROGRAM, false, false);
  }

  @Test
  void setPreferredValidator() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var validatorVoteAddress = key(9);

    final var ix = StakePoolProgram.setPreferredValidator(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        StakePoolProgram.PreferredValidatorType.Deposit,
        validatorVoteAddress
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // SetPreferredValidator = 5, then PreferredValidatorType::Deposit = 0,
    // then Option<Pubkey>: Some = 1 followed by the 32 key bytes.
    final byte[] expectedData = new byte[1 + 1 + 1 + PublicKey.PUBLIC_KEY_LENGTH];
    expectedData[0] = 5;
    expectedData[1] = 0;
    expectedData[2] = 1;
    Arrays.fill(expectedData, 3, expectedData.length, (byte) 9);
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(3, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), validatorList, false, false);
  }

  @Test
  void setPreferredValidatorUnset() {
    final var staker = key(2);
    final var validatorList = key(3);

    final var ix = StakePoolProgram.setPreferredValidator(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        StakePoolProgram.PreferredValidatorType.Withdraw,
        null
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // SetPreferredValidator = 5, then PreferredValidatorType::Withdraw = 1,
    // then Option<Pubkey>: None = 0 — exactly 3 bytes, since the on-chain strict
    // try_from_slice rejects trailing bytes.
    final byte[] expectedData = new byte[3];
    expectedData[0] = 5;
    expectedData[1] = 1;
    expectedData[2] = 0;
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(3, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), validatorList, false, false);
  }

  @Test
  void updateValidatorListBalance() {
    final var validatorList = key(2);
    final var reserveStakeAccount = key(3);
    final var validatorStakeAccount = key(4);
    final var transientStakeAccount = key(5);
    final var validatorStakeAccount2 = key(6);
    final var transientStakeAccount2 = key(7);

    final var ix = StakePoolProgram.updateValidatorListBalance(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorList,
        reserveStakeAccount,
        List.of(
            validatorStakeAccount,
            transientStakeAccount,
            validatorStakeAccount2,
            transientStakeAccount2
        ),
        0x0A0B0C0D,
        true
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // UpdateValidatorListBalance = 6, then u32 start_index LE, bool no_merge.
    final byte[] expectedData = {
        6,
        0x0D, 0x0C, 0x0B, 0x0A,
        1
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(11, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(2), validatorList, true, false);
    assertAccount(accounts.get(3), reserveStakeAccount, true, false);
    assertAccount(accounts.get(4), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(5), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(6), STAKE_PROGRAM, false, false);
    // each validator/transient stake pair is writable: the processor merges
    // transient lamports into them (Rust builder update_validator_list_balance).
    assertAccount(accounts.get(7), validatorStakeAccount, true, false);
    assertAccount(accounts.get(8), transientStakeAccount, true, false);
    assertAccount(accounts.get(9), validatorStakeAccount2, true, false);
    assertAccount(accounts.get(10), transientStakeAccount2, true, false);

    final var noMergeFalse = StakePoolProgram.updateValidatorListBalance(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorList,
        reserveStakeAccount,
        List.of(),
        0,
        false
    );
    assertArrayEquals(new byte[]{6, 0, 0, 0, 0, 0}, noMergeFalse.data());
    assertEquals(7, noMergeFalse.accounts().size());
  }

  @Test
  void cleanupRemovedValidatorEntries() {
    final var validatorList = key(2);

    final var ix = StakePoolProgram.cleanupRemovedValidatorEntries(
        INVOKED_PROGRAM,
        STAKE_POOL,
        validatorList
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // CleanupRemovedValidatorEntries = 8, no fields.
    assertArrayEquals(new byte[]{8}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(2, accounts.size());
    // NOTE: the Rust builder fn cleanup_removed_validator_entries marks the stake
    // pool writable (AccountMeta::new), while the enum doc comment lists it
    // read-only and the processor only reads it. The Java builder follows the doc
    // comment; pinned as-is.
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), validatorList, true, false);
  }

  @Test
  void setManager() {
    final var manager = key(2);
    final var newManager = key(3);
    final var newManagerFeeAccount = key(4);

    final var ix = StakePoolProgram.setManager(
        INVOKED_PROGRAM,
        STAKE_POOL,
        manager,
        newManager,
        newManagerFeeAccount
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // SetManager = 11, no fields.
    assertArrayEquals(new byte[]{11}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(4, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), manager, false, true);
    assertAccount(accounts.get(2), newManager, false, true);
    assertAccount(accounts.get(3), newManagerFeeAccount, false, false);
  }

  private void assertSetFee(final FeeType feeType, final byte[] expectedData) {
    final var manager = key(2);

    final var ix = StakePoolProgram.setFee(
        INVOKED_PROGRAM,
        STAKE_POOL,
        manager,
        feeType
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(2, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), manager, false, true);
  }

  @Test
  void setFeeReferralVariants() {
    // SetFee = 12, then FeeType: u8 variant ordinal followed by u8 percentage.
    assertSetFee(new FeeType.SolReferral(0x7B), new byte[]{12, 0, 0x7B});
    assertSetFee(new FeeType.StakeReferral(0x2A), new byte[]{12, 1, 0x2A});
  }

  @Test
  void setFeeFeeVariants() {
    final var fee = new StakePoolState.Fee(0x1122334455667788L, 0x0102030405060708L);
    // SetFee = 12, then FeeType: u8 variant ordinal followed by
    // Fee (u64 denominator, u64 numerator LE).
    final byte[] feeBytes = {
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01
    };
    for (int ordinal = 2; ordinal <= 6; ++ordinal) {
      final var feeType = switch (ordinal) {
        case 2 -> new FeeType.Epoch(fee);
        case 3 -> new FeeType.StakeWithdrawal(fee);
        case 4 -> new FeeType.SolDeposit(fee);
        case 5 -> new FeeType.StakeDeposit(fee);
        default -> new FeeType.SolWithdrawal(fee);
      };
      final byte[] expectedData = new byte[2 + feeBytes.length];
      expectedData[0] = 12;
      expectedData[1] = (byte) ordinal;
      System.arraycopy(feeBytes, 0, expectedData, 2, feeBytes.length);
      assertSetFee(feeType, expectedData);
    }
  }

  @Test
  void setStaker() {
    final var managerOrCurrentStaker = key(2);
    final var newStaker = key(3);

    final var ix = StakePoolProgram.setStaker(
        INVOKED_PROGRAM,
        STAKE_POOL,
        managerOrCurrentStaker,
        newStaker
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // SetStaker = 13, no fields.
    assertArrayEquals(new byte[]{13}, ix.data());

    final var accounts = ix.accounts();
    assertEquals(3, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, true, false);
    assertAccount(accounts.get(1), managerOrCurrentStaker, false, true);
    assertAccount(accounts.get(2), newStaker, false, false);
  }

  @Test
  void setFundingAuthority() {
    final var manager = key(2);
    final var newAuthority = key(3);

    // SetFundingAuthority = 15, then FundingType as u8:
    // StakeDeposit = 0, SolDeposit = 1, SolWithdraw = 2.
    final var fundingTypes = StakePoolProgram.FundingType.values();
    assertEquals(3, fundingTypes.length);
    for (final var fundingType : fundingTypes) {
      final var ix = StakePoolProgram.setFundingAuthority(
          INVOKED_PROGRAM,
          STAKE_POOL,
          manager,
          newAuthority,
          fundingType
      );

      assertEquals(INVOKED_PROGRAM, ix.programId());
      assertArrayEquals(new byte[]{15, (byte) fundingType.ordinal()}, ix.data());

      final var accounts = ix.accounts();
      assertEquals(3, accounts.size());
      assertAccount(accounts.get(0), STAKE_POOL, true, false);
      assertAccount(accounts.get(1), manager, false, true);
      assertAccount(accounts.get(2), newAuthority, false, false);
    }
    assertEquals(0, StakePoolProgram.FundingType.StakeDeposit.ordinal());
    assertEquals(1, StakePoolProgram.FundingType.SolDeposit.ordinal());
    assertEquals(2, StakePoolProgram.FundingType.SolWithdraw.ordinal());
  }

  @Test
  void createTokenMetadata() {
    final var manager = key(2);
    final var poolTokenMint = key(3);
    final var payer = key(4);
    final var tokenMetadataAccount = key(5);
    final var metadataProgram = key(6);

    final var ix = StakePoolProgram.createTokenMetadata(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        manager,
        poolTokenMint,
        payer,
        tokenMetadataAccount,
        metadataProgram,
        "Sava",
        "SP",
        "u.ri"
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // CreateTokenMetadata = 17, then borsh String name, symbol, uri:
    // each a u32 LE byte length followed by the UTF-8 bytes.
    final byte[] expectedData = {
        17,
        4, 0, 0, 0, 'S', 'a', 'v', 'a',
        2, 0, 0, 0, 'S', 'P',
        4, 0, 0, 0, 'u', '.', 'r', 'i'
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(8, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), manager, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), poolTokenMint, false, false);
    assertAccount(accounts.get(4), payer, true, true);
    assertAccount(accounts.get(5), tokenMetadataAccount, true, false);
    assertAccount(accounts.get(6), metadataProgram, false, false);
    assertAccount(accounts.get(7), SYSTEM_PROGRAM, false, false);
  }

  @Test
  void updateTokenMetadata() {
    final var manager = key(2);
    final var tokenMetadataAccount = key(3);
    final var metadataProgram = key(4);

    final var ix = StakePoolProgram.updateTokenMetadata(
        INVOKED_PROGRAM,
        STAKE_POOL,
        manager,
        tokenMetadataAccount,
        metadataProgram,
        "Sava",
        "SP",
        "u.ri"
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // UpdateTokenMetadata = 18, then borsh String name, symbol, uri.
    final byte[] expectedData = {
        18,
        4, 0, 0, 0, 'S', 'a', 'v', 'a',
        2, 0, 0, 0, 'S', 'P',
        4, 0, 0, 0, 'u', '.', 'r', 'i'
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(5, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), manager, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), tokenMetadataAccount, true, false);
    assertAccount(accounts.get(4), metadataProgram, false, false);
  }

  @Test
  void increaseAdditionalValidatorStake() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var stakePoolReserveStake = key(4);
    final var ephemeralStakeAccount = key(5);
    final var transientStakeAccount = key(6);
    final var validatorStakeAccount = key(7);
    final var validatorVoteAccount = key(8);

    final var ix = StakePoolProgram.increaseAdditionalValidatorStake(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        stakePoolReserveStake,
        ephemeralStakeAccount,
        transientStakeAccount,
        validatorStakeAccount,
        validatorVoteAccount,
        0x1122334455667788L,
        0x0102030405060708L,
        0x2132435465768798L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // IncreaseAdditionalValidatorStake = 19, then u64 lamports,
    // u64 transient_stake_seed, u64 ephemeral_stake_seed LE.
    final byte[] expectedData = {
        19,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01,
        (byte) 0x98, (byte) 0x87, 0x76, 0x65, 0x54, 0x43, 0x32, 0x21
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(14, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), stakePoolReserveStake, true, false);
    assertAccount(accounts.get(5), ephemeralStakeAccount, true, false);
    assertAccount(accounts.get(6), transientStakeAccount, true, false);
    assertAccount(accounts.get(7), validatorStakeAccount, false, false);
    assertAccount(accounts.get(8), validatorVoteAccount, false, false);
    assertAccount(accounts.get(9), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(10), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(11), STAKE_CONFIG, false, false);
    assertAccount(accounts.get(12), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(13), STAKE_PROGRAM, false, false);
  }

  @Test
  void decreaseAdditionalValidatorStake() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var stakePoolReserveStake = key(4);
    final var splitFromStakeAccount = key(5);
    final var ephemeralStakeAccount = key(6);
    final var transientStakeAccount = key(7);

    final var ix = StakePoolProgram.decreaseAdditionalValidatorStake(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        stakePoolReserveStake,
        splitFromStakeAccount,
        ephemeralStakeAccount,
        transientStakeAccount,
        0x1122334455667788L,
        0x0102030405060708L,
        0x2132435465768798L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // DecreaseAdditionalValidatorStake = 20, then u64 lamports,
    // u64 transient_stake_seed, u64 ephemeral_stake_seed LE.
    final byte[] expectedData = {
        20,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01,
        (byte) 0x98, (byte) 0x87, 0x76, 0x65, 0x54, 0x43, 0x32, 0x21
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(12, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), stakePoolReserveStake, true, false);
    assertAccount(accounts.get(5), splitFromStakeAccount, true, false);
    assertAccount(accounts.get(6), ephemeralStakeAccount, true, false);
    assertAccount(accounts.get(7), transientStakeAccount, true, false);
    assertAccount(accounts.get(8), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(9), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(10), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(11), STAKE_PROGRAM, false, false);
  }

  @Test
  void redelegate() {
    final var staker = key(2);
    final var validatorList = key(3);
    final var stakePoolReserveStake = key(4);
    final var sourceValidatorStakeAccount = key(5);
    final var sourceTransientStakeAccount = key(6);
    final var ephemeralStakeAccount = key(7);
    final var destinationTransientStakeAccount = key(8);
    final var destinationValidatorStakeAccount = key(9);
    final var validatorVoteAccount = key(10);

    // Parameter names follow the Java builder; positionally they map to the Rust
    // builder fn redelegate as: splitFromStakeAccount = source validator stake,
    // transientStakeAccount = source transient stake, uninitializedStakeAccount =
    // ephemeral stake, ephemeralDestinationStakeAccount = destination transient
    // stake, transientDestinationStakeAccount = destination validator stake.
    final var ix = StakePoolProgram.redelegate(
        SOLANA_ACCOUNTS,
        INVOKED_PROGRAM,
        STAKE_POOL,
        staker,
        validatorList,
        stakePoolReserveStake,
        sourceValidatorStakeAccount,
        sourceTransientStakeAccount,
        ephemeralStakeAccount,
        destinationTransientStakeAccount,
        destinationValidatorStakeAccount,
        validatorVoteAccount,
        0x1122334455667788L,
        0x0102030405060708L,
        0x2132435465768798L,
        0x1020304050607080L
    );

    assertEquals(INVOKED_PROGRAM, ix.programId());

    // Redelegate = 22, then u64 lamports, u64 source_transient_stake_seed,
    // u64 ephemeral_stake_seed, u64 destination_transient_stake_seed LE.
    final byte[] expectedData = {
        22,
        (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11,
        0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01,
        (byte) 0x98, (byte) 0x87, 0x76, 0x65, 0x54, 0x43, 0x32, 0x21,
        (byte) 0x80, 0x70, 0x60, 0x50, 0x40, 0x30, 0x20, 0x10
    };
    assertArrayEquals(expectedData, ix.data());

    final var accounts = ix.accounts();
    assertEquals(16, accounts.size());
    assertAccount(accounts.get(0), STAKE_POOL, false, false);
    assertAccount(accounts.get(1), staker, false, true);
    assertAccount(accounts.get(2), WITHDRAW_AUTHORITY, false, false);
    assertAccount(accounts.get(3), validatorList, true, false);
    assertAccount(accounts.get(4), stakePoolReserveStake, true, false);
    assertAccount(accounts.get(5), sourceValidatorStakeAccount, true, false);
    assertAccount(accounts.get(6), sourceTransientStakeAccount, true, false);
    assertAccount(accounts.get(7), ephemeralStakeAccount, true, false);
    assertAccount(accounts.get(8), destinationTransientStakeAccount, true, false);
    assertAccount(accounts.get(9), destinationValidatorStakeAccount, false, false);
    assertAccount(accounts.get(10), validatorVoteAccount, false, false);
    assertAccount(accounts.get(11), CLOCK_SYSVAR, false, false);
    assertAccount(accounts.get(12), STAKE_HISTORY_SYSVAR, false, false);
    assertAccount(accounts.get(13), STAKE_CONFIG, false, false);
    assertAccount(accounts.get(14), SYSTEM_PROGRAM, false, false);
    assertAccount(accounts.get(15), STAKE_PROGRAM, false, false);
  }
}
