package software.sava.idl.clients.marinade.stake_pool;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.marinade.stake_pool.gen.MarinadeFinanceProgram;
import software.sava.idl.clients.spl.SPLAccountClient;

final class MarinadeProgramClientImpl implements MarinadeProgramClient {

  private final SPLAccountClient splAccountClient;
  private final SolanaAccounts solanaAccounts;
  private final MarinadeAccounts marinadeAccounts;
  private final PublicKey owner;
  private final PublicKey feePayer;

  MarinadeProgramClientImpl(final SPLAccountClient splAccountClient,
                            final MarinadeAccounts marinadeAccounts) {
    this.splAccountClient = splAccountClient;
    this.solanaAccounts = splAccountClient.solanaAccounts();
    this.marinadeAccounts = marinadeAccounts;
    this.owner = splAccountClient.owner();
    this.feePayer = splAccountClient.feePayer().publicKey();
  }

  @Override
  public SolanaAccounts solanaAccounts() {
    return solanaAccounts;
  }

  @Override
  public MarinadeAccounts marinadeAccounts() {
    return marinadeAccounts;
  }

  @Override
  public SPLAccountClient splAccountClient() {
    return splAccountClient;
  }

  @Override
  public PublicKey owner() {
    return owner;
  }

  @Override
  public PublicKey feePayer() {
    return feePayer;
  }

  @Override
  public Instruction marinadeDeposit(final PublicKey mSolTokenAccount, final long lamports) {
    return MarinadeFinanceProgram.deposit(
        marinadeAccounts.invokedMarinadeProgram(),
        marinadeAccounts.stateProgram(),
        marinadeAccounts.mSolTokenMint(),
        marinadeAccounts.liquidityPoolSolLegAccount(),
        marinadeAccounts.liquidityPoolMSolLegAccount(),
        marinadeAccounts.liquidityPoolMSolLegAuthority(),
        marinadeAccounts.treasuryReserveSolPDA(),
        owner,
        mSolTokenAccount,
        marinadeAccounts.mSolTokenMintAuthorityPDA(),
        solanaAccounts.systemProgram(),
        solanaAccounts.tokenProgram(),
        lamports
    );
  }

  @Override
  public Instruction depositStakeAccount(final PublicKey validatorListKey,
                                         final PublicKey stakeListKey,
                                         final PublicKey stakeAccount,
                                         final PublicKey duplicationFlagKey,
                                         final PublicKey mSolTokenAccount,
                                         final int validatorIndex) {
    return MarinadeFinanceProgram.depositStakeAccount(
        marinadeAccounts.invokedMarinadeProgram(),
        marinadeAccounts.stateProgram(),
        validatorListKey,
        stakeListKey,
        stakeAccount,
        owner,
        duplicationFlagKey,
        feePayer,
        marinadeAccounts.mSolTokenMint(),
        mSolTokenAccount,
        marinadeAccounts.mSolTokenMintAuthorityPDA(),
        solanaAccounts.clockSysVar(),
        solanaAccounts.rentSysVar(),
        solanaAccounts.systemProgram(),
        solanaAccounts.tokenProgram(),
        solanaAccounts.stakeProgram(),
        validatorIndex
    );
  }

  @Override
  public Instruction claimTicket(final PublicKey ticketAccount) {
    return MarinadeFinanceProgram.claim(
        marinadeAccounts.invokedMarinadeProgram(),
        marinadeAccounts.stateProgram(),
        marinadeAccounts.treasuryReserveSolPDA(),
        ticketAccount,
        owner,
        solanaAccounts.clockSysVar(),
        solanaAccounts.systemProgram()
    );
  }

  @Override
  public Instruction withdrawStakeAccount(final PublicKey mSolTokenAccount,
                                          final PublicKey validatorListKey,
                                          final PublicKey stakeListKey,
                                          final PublicKey stakeWithdrawalAuthorityKey,
                                          final PublicKey stakeDepositAuthorityKey,
                                          final PublicKey stakeAccount,
                                          final PublicKey splitStakeAccountKey,
                                          final int stakeIndex,
                                          final int validatorIndex,
                                          final long msolAmount) {
    return MarinadeFinanceProgram.withdrawStakeAccount(
        marinadeAccounts.invokedMarinadeProgram(),
        marinadeAccounts.stateProgram(),
        marinadeAccounts.mSolTokenMint(),
        mSolTokenAccount,
        owner,
        marinadeAccounts.treasuryMSolAccount(),
        validatorListKey,
        stakeListKey,

        stakeWithdrawalAuthorityKey,
        stakeDepositAuthorityKey,

        stakeAccount,
        splitStakeAccountKey,
        feePayer,

        solanaAccounts.clockSysVar(),
        solanaAccounts.systemProgram(),
        solanaAccounts.tokenProgram(),
        solanaAccounts.stakeProgram(),

        stakeIndex,
        validatorIndex,
        msolAmount,
        owner
    );
  }
}
