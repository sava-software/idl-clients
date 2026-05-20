package software.sava.idl.clients.marinade.stake_pool;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static software.sava.core.accounts.PublicKey.fromBase58Encoded;
import static software.sava.core.accounts.meta.AccountMeta.createInvoked;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;

public interface MarinadeAccounts {

  MarinadeAccounts MAIN_NET = createAddressConstants(
      "mSoLzYCxHdYgdzU16g5QSh3i5K3z3KZK7ytfqcJm7So",
      "3JLPCS1qM2zRw3Dp6V4hZnYHd4toMNPkNesXdX9tg6KM",
      "MarBmsSgKXdrN1egZf5sqe1TMai9K1rChYNDJgjq7aD",
      "8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC",
      "Du3Ysj1wKbxPKkuPPnvzQLQh8oMSVifs3jGZjJWXFmHN",
      "B1aLzaNMeFVAyQ6f3XbbUyKcH2YPHu2fqiEagmiF23VR",
      "LPmSozJJ8Jh69ut2WP3XmVohTjL4ipR18yiCzxrUmVj",
      "HZsepB79dnpvH6qfVgvMpS738EndHw3qSHo4Gv5WX1KA",
      "7GgPYjS5Dza89wV6FpZ23kUJRG5vbQ1GM25ezspYFSoE",
      "EyaSjUtSgo9aRD1f8LWXwdvkpDTmXAW54yoSHZRF14WL",
      "UefNb6z6yvArqe4cJHTXCqStRsKmWhGxnZzuHbikP5Q",
      "DwFYJNnhLmw19FBTrVaLWZ8SZJpxdPoSYVSJaio9tjbY",
      null
  );

  static MarinadeAccounts createAddressConstants(final PublicKey mSolTokenMint,
                                                 final PublicKey mSolTokenMintAuthorityPDA,
                                                 final PublicKey marinadeProgram,
                                                 final PublicKey stateAccount,
                                                 final PublicKey treasuryReserveSolPDA,
                                                 final PublicKey treasuryMSolAccount,
                                                 final PublicKey mSolSolLPMint,
                                                 final PublicKey lpAuthPDA,
                                                 final PublicKey mSolLegAccount,
                                                 final PublicKey mSolLegAuthority,
                                                 final PublicKey solLegAccount,
                                                 final PublicKey validatorListAccount,
                                                 final PublicKey stakeListAccount) {
    final var stakeWithdrawAuthority = deriveStakeWithdrawAuthority(marinadeProgram, stateAccount).publicKey();
    final var stakeDepositAuthority = deriveStakeDepositAuthority(marinadeProgram, stateAccount).publicKey();
    return new MarinadeAccountsRecord(
        mSolTokenMint,
        mSolTokenMintAuthorityPDA,
        createWrite(mSolTokenMint),
        marinadeProgram,
        createInvoked(marinadeProgram),
        stateAccount,
        createWrite(stateAccount),
        treasuryReserveSolPDA,
        createWrite(treasuryReserveSolPDA),
        treasuryMSolAccount,
        mSolSolLPMint,
        lpAuthPDA,
        mSolLegAccount,
        mSolLegAuthority,
        solLegAccount,
        validatorListAccount,
        stakeListAccount,
        stakeWithdrawAuthority,
        stakeDepositAuthority
    );
  }

  static MarinadeAccounts createAddressConstants(final String mSolTokenMint,
                                                 final String mSolTokenMintAuthorityPDA,
                                                 final String marinadeProgram,
                                                 final String stateAccount,
                                                 final String treasuryReserveSolPDA,
                                                 final String treasuryMSolAccount,
                                                 final String mSolSolLPMint,
                                                 final String lpAuthPDA,
                                                 final String mSolLegAccount,
                                                 final String mSolLegAuthority,
                                                 final String solLegAccount,
                                                 final String validatorListAccount,
                                                 final String stakeListAccount) {
    return createAddressConstants(
        fromBase58Encoded(mSolTokenMint),
        fromBase58Encoded(mSolTokenMintAuthorityPDA),
        fromBase58Encoded(marinadeProgram),
        fromBase58Encoded(stateAccount),
        fromBase58Encoded(treasuryReserveSolPDA),
        fromBase58Encoded(treasuryMSolAccount),
        fromBase58Encoded(mSolSolLPMint),
        fromBase58Encoded(lpAuthPDA),
        fromBase58Encoded(mSolLegAccount),
        fromBase58Encoded(mSolLegAuthority),
        fromBase58Encoded(solLegAccount),
        fromBase58Encoded(validatorListAccount),
        stakeListAccount == null ? null : fromBase58Encoded(stakeListAccount)
    );
  }

  static MarinadeAccounts createAddressConstants(final String mSolTokenMint,
                                                 final String mSolTokenMintAuthorityPDA,
                                                 final String marinadeProgram,
                                                 final String stateAccount,
                                                 final String treasuryReserveSolPDA,
                                                 final String treasuryMSolAccount,
                                                 final String mSolSolLPMint,
                                                 final String lpAuthPDA,
                                                 final String mSolLegAccount,
                                                 final String mSolLegAuthority,
                                                 final String solLegAccount,
                                                 final String validatorListAccount) {
    return createAddressConstants(
        mSolTokenMint,
        mSolTokenMintAuthorityPDA,
        marinadeProgram,
        stateAccount,
        treasuryReserveSolPDA,
        treasuryMSolAccount,
        mSolSolLPMint,
        lpAuthPDA,
        mSolLegAccount,
        mSolLegAuthority,
        solLegAccount,
        validatorListAccount,
        null
    );
  }

  PublicKey mSolTokenMint();

  PublicKey mSolTokenMintAuthorityPDA();

  AccountMeta writeMSolTokenMint();

  PublicKey marinadeProgram();

  AccountMeta invokedMarinadeProgram();

  PublicKey stateAccount();

  AccountMeta writeStateAccount();

  // Treasury

  PublicKey treasuryReserveSolPDA();

  AccountMeta writeReserveSolPDA();

  PublicKey treasuryMSolAccount();

  // Liquidity Pool

  PublicKey liquidityPoolMSolSolMint();

  PublicKey liquidityPoolAuthPDA();

  PublicKey liquidityPoolMSolLegAccount();

  PublicKey liquidityPoolMSolLegAuthority();

  PublicKey liquidityPoolSolLegAccount();

  /// May be {@code null}; the authoritative list account is the one stored in the on-chain
  /// {@code State}. Treat any value here as a (possibly stale) hint and prefer
  /// {@code state.validatorSystem().validatorList().account()} when {@code State} is fetched.
  PublicKey validatorListAccount();

  /// May be {@code null}; the authoritative list account is the one stored in the on-chain
  /// {@code State}. Treat any value here as a (possibly stale) hint and prefer
  /// {@code state.stakeSystem().stakeList().account()} when {@code State} is fetched.
  PublicKey stakeListAccount();

  PublicKey stakeWithdrawAuthority();

  PublicKey stakeDepositAuthority();

  default ProgramDerivedAddress findDuplicationKey(final PublicKey validatorPublicKey) {
    return PublicKey.findProgramAddress(List.of(
            stateAccount().toByteArray(),
            "unique_validator".getBytes(StandardCharsets.UTF_8),
            validatorPublicKey.toByteArray()
        ), marinadeProgram()
    );
  }

  static ProgramDerivedAddress deriveReserveAuthority(final PublicKey program, final PublicKey state) {
    return PublicKey.findProgramAddress(List.of(
            state.toByteArray(),
            "reserve".getBytes(US_ASCII)
        ), program
    );
  }

  default ProgramDerivedAddress deriveReserveAuthority() {
    return deriveReserveAuthority(marinadeProgram(), stateAccount());
  }

  static ProgramDerivedAddress deriveStakeMintAuthority(final PublicKey program, final PublicKey state) {
    return PublicKey.findProgramAddress(List.of(
            state.toByteArray(),
            "st_mint".getBytes(US_ASCII)
        ), program
    );
  }

  default ProgramDerivedAddress deriveStakeMintAuthority() {
    return deriveStakeMintAuthority(marinadeProgram(), stateAccount());
  }

  static ProgramDerivedAddress deriveStakeWithdrawAuthority(final PublicKey program, final PublicKey state) {
    return PublicKey.findProgramAddress(List.of(
            state.toByteArray(),
            "withdraw".getBytes(US_ASCII)
        ), program
    );
  }

  default ProgramDerivedAddress deriveStakeWithdrawAuthority() {
    return deriveStakeWithdrawAuthority(marinadeProgram(), stateAccount());
  }

  static ProgramDerivedAddress deriveStakeDepositAuthority(final PublicKey program, final PublicKey state) {
    return PublicKey.findProgramAddress(List.of(
            state.toByteArray(),
            "deposit".getBytes(US_ASCII)
        ), program
    );
  }

  default ProgramDerivedAddress deriveStakeDepositAuthority() {
    return deriveStakeDepositAuthority(marinadeProgram(), stateAccount());
  }
}
