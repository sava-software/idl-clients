package software.sava.idl.clients.loopscale.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.loopscale.gen.types.BorrowPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.ClaimVaultFeeParams;
import software.sava.idl.clients.loopscale.gen.types.CollateralAllocationParam;
import software.sava.idl.clients.loopscale.gen.types.CreateLoanParams;
import software.sava.idl.clients.loopscale.gen.types.CreateMarketInformationParams;
import software.sava.idl.clients.loopscale.gen.types.CreateRewardsScheduleParams;
import software.sava.idl.clients.loopscale.gen.types.CreateStrategyParams;
import software.sava.idl.clients.loopscale.gen.types.CreateVaultParams;
import software.sava.idl.clients.loopscale.gen.types.DepositCollateralParams;
import software.sava.idl.clients.loopscale.gen.types.LiquidateLedgerParams;
import software.sava.idl.clients.loopscale.gen.types.LoanUnlockParams;
import software.sava.idl.clients.loopscale.gen.types.LockLoanParams;
import software.sava.idl.clients.loopscale.gen.types.LpParams;
import software.sava.idl.clients.loopscale.gen.types.ManageLiquidityParams;
import software.sava.idl.clients.loopscale.gen.types.ManageRaydiumLiquidityParams;
import software.sava.idl.clients.loopscale.gen.types.MultiCollateralTermsUpdateParams;
import software.sava.idl.clients.loopscale.gen.types.RefinanceLedgerParams;
import software.sava.idl.clients.loopscale.gen.types.RepayPrincipalParams;
import software.sava.idl.clients.loopscale.gen.types.SellLedgerParams;
import software.sava.idl.clients.loopscale.gen.types.TimelockUpdateParams;
import software.sava.idl.clients.loopscale.gen.types.TransferPositionParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateAssetDataParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateCapsParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateRewardsScheduleParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateStrategyParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateVaultParams;
import software.sava.idl.clients.loopscale.gen.types.UpdateWeightMatrixParams;
import software.sava.idl.clients.loopscale.gen.types.VaultStakeParams;
import software.sava.idl.clients.loopscale.gen.types.VaultUnstakeParams;
import software.sava.idl.clients.loopscale.gen.types.WithdrawCollateralParams;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class LoopscaleProgram {

  public static final Discriminator BORROW_PRINCIPAL_DISCRIMINATOR = toDiscriminator(106, 10, 38, 204, 139, 188, 124, 50);

  /// principal instructions
  /// 6.
  /// 6.1. borrow principal
  ///
  public static List<AccountMeta> borrowPrincipalKeys(final PublicKey bsAuthKey,
                                                      final PublicKey payerKey,
                                                      final PublicKey borrowerKey,
                                                      final PublicKey loanKey,
                                                      final PublicKey strategyKey,
                                                      final PublicKey marketInformationKey,
                                                      final PublicKey principalMintKey,
                                                      final PublicKey borrowerTaKey,
                                                      final PublicKey strategyTaKey,
                                                      final PublicKey associatedTokenProgramKey,
                                                      final PublicKey tokenProgramKey,
                                                      final PublicKey systemProgramKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createWrite(strategyKey),
      createWrite(marketInformationKey),
      createRead(principalMintKey),
      createWrite(borrowerTaKey),
      createWrite(strategyTaKey),
      createRead(associatedTokenProgramKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// principal instructions
  /// 6.
  /// 6.1. borrow principal
  ///
  public static Instruction borrowPrincipal(final AccountMeta invokedLoopscaleProgramMeta,
                                            final PublicKey bsAuthKey,
                                            final PublicKey payerKey,
                                            final PublicKey borrowerKey,
                                            final PublicKey loanKey,
                                            final PublicKey strategyKey,
                                            final PublicKey marketInformationKey,
                                            final PublicKey principalMintKey,
                                            final PublicKey borrowerTaKey,
                                            final PublicKey strategyTaKey,
                                            final PublicKey associatedTokenProgramKey,
                                            final PublicKey tokenProgramKey,
                                            final PublicKey systemProgramKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final BorrowPrincipalParams params) {
    final var keys = borrowPrincipalKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      strategyKey,
      marketInformationKey,
      principalMintKey,
      borrowerTaKey,
      strategyTaKey,
      associatedTokenProgramKey,
      tokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return borrowPrincipal(invokedLoopscaleProgramMeta, keys, params);
  }

  /// principal instructions
  /// 6.
  /// 6.1. borrow principal
  ///
  public static Instruction borrowPrincipal(final AccountMeta invokedLoopscaleProgramMeta,
                                            final List<AccountMeta> keys,
                                            final BorrowPrincipalParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = BORROW_PRINCIPAL_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record BorrowPrincipalIxData(Discriminator discriminator, BorrowPrincipalParams params) implements SerDe {  

    public static BorrowPrincipalIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static BorrowPrincipalIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = BorrowPrincipalParams.read(_data, i);
      return new BorrowPrincipalIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator CANCEL_TIMELOCK_DISCRIMINATOR = toDiscriminator(158, 180, 47, 81, 133, 231, 168, 238);

  /// 9.2.3 timelock cancel
  ///
  public static List<AccountMeta> cancelTimelockKeys(final PublicKey bsAuthKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey managerKey,
                                                     final PublicKey vaultKey,
                                                     final PublicKey timelockKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(managerKey),
      createWrite(vaultKey),
      createWrite(timelockKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.2.3 timelock cancel
  ///
  public static Instruction cancelTimelock(final AccountMeta invokedLoopscaleProgramMeta,
                                           final PublicKey bsAuthKey,
                                           final PublicKey payerKey,
                                           final PublicKey managerKey,
                                           final PublicKey vaultKey,
                                           final PublicKey timelockKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey) {
    final var keys = cancelTimelockKeys(
      bsAuthKey,
      payerKey,
      managerKey,
      vaultKey,
      timelockKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return cancelTimelock(invokedLoopscaleProgramMeta, keys);
  }

  /// 9.2.3 timelock cancel
  ///
  public static Instruction cancelTimelock(final AccountMeta invokedLoopscaleProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, CANCEL_TIMELOCK_DISCRIMINATOR);
  }

  public static final Discriminator CLAIM_VAULT_FEE_DISCRIMINATOR = toDiscriminator(38, 40, 51, 195, 130, 248, 134, 247);

  /// 9.1.2 vault manager actions
  ///
  public static List<AccountMeta> claimVaultFeeKeys(final PublicKey bsAuthKey,
                                                    final PublicKey payerKey,
                                                    final PublicKey managerKey,
                                                    final PublicKey vaultKey,
                                                    final PublicKey strategyKey,
                                                    final PublicKey principalMintKey,
                                                    final PublicKey marketInformationKey,
                                                    final PublicKey managerPrincipalTaKey,
                                                    final PublicKey strategyPrincipalTaKey,
                                                    final PublicKey tokenProgramKey,
                                                    final PublicKey associatedTokenProgramKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(managerKey),
      createWrite(vaultKey),
      createWrite(strategyKey),
      createRead(principalMintKey),
      createRead(marketInformationKey),
      createWrite(managerPrincipalTaKey),
      createWrite(strategyPrincipalTaKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.1.2 vault manager actions
  ///
  public static Instruction claimVaultFee(final AccountMeta invokedLoopscaleProgramMeta,
                                          final PublicKey bsAuthKey,
                                          final PublicKey payerKey,
                                          final PublicKey managerKey,
                                          final PublicKey vaultKey,
                                          final PublicKey strategyKey,
                                          final PublicKey principalMintKey,
                                          final PublicKey marketInformationKey,
                                          final PublicKey managerPrincipalTaKey,
                                          final PublicKey strategyPrincipalTaKey,
                                          final PublicKey tokenProgramKey,
                                          final PublicKey associatedTokenProgramKey,
                                          final PublicKey systemProgramKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey,
                                          final ClaimVaultFeeParams params) {
    final var keys = claimVaultFeeKeys(
      bsAuthKey,
      payerKey,
      managerKey,
      vaultKey,
      strategyKey,
      principalMintKey,
      marketInformationKey,
      managerPrincipalTaKey,
      strategyPrincipalTaKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimVaultFee(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.1.2 vault manager actions
  ///
  public static Instruction claimVaultFee(final AccountMeta invokedLoopscaleProgramMeta,
                                          final List<AccountMeta> keys,
                                          final ClaimVaultFeeParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CLAIM_VAULT_FEE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ClaimVaultFeeIxData(Discriminator discriminator, ClaimVaultFeeParams params) implements SerDe {  

    public static ClaimVaultFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static ClaimVaultFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ClaimVaultFeeParams.read(_data, i);
      return new ClaimVaultFeeIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLAIM_VAULT_REWARDS_DISCRIMINATOR = toDiscriminator(0, 152, 75, 29, 195, 223, 12, 101);

  /// 9.1.1.5 vault user claim rewards
  ///
  public static List<AccountMeta> claimVaultRewardsKeys(final PublicKey bsAuthKey,
                                                        final PublicKey payerKey,
                                                        final PublicKey userKey,
                                                        final PublicKey vaultKey,
                                                        final PublicKey vaultRewardsInfoKey,
                                                        final PublicKey userRewardsInfoKey,
                                                        final PublicKey stakeAccountKey,
                                                        final PublicKey associatedTokenProgramKey,
                                                        final PublicKey systemProgramKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createRead(userKey),
      createRead(vaultKey),
      createWrite(vaultRewardsInfoKey),
      createWrite(userRewardsInfoKey),
      createWrite(stakeAccountKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.1.1.5 vault user claim rewards
  ///
  public static Instruction claimVaultRewards(final AccountMeta invokedLoopscaleProgramMeta,
                                              final PublicKey bsAuthKey,
                                              final PublicKey payerKey,
                                              final PublicKey userKey,
                                              final PublicKey vaultKey,
                                              final PublicKey vaultRewardsInfoKey,
                                              final PublicKey userRewardsInfoKey,
                                              final PublicKey stakeAccountKey,
                                              final PublicKey associatedTokenProgramKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey,
                                              final PublicKey[] mints) {
    final var keys = claimVaultRewardsKeys(
      bsAuthKey,
      payerKey,
      userKey,
      vaultKey,
      vaultRewardsInfoKey,
      userRewardsInfoKey,
      stakeAccountKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimVaultRewards(invokedLoopscaleProgramMeta, keys, mints);
  }

  /// 9.1.1.5 vault user claim rewards
  ///
  public static Instruction claimVaultRewards(final AccountMeta invokedLoopscaleProgramMeta,
                                              final List<AccountMeta> keys,
                                              final PublicKey[] mints) {
    final byte[] _data = new byte[8 + SerDeUtil.lenVector(4, mints)];
    int i = CLAIM_VAULT_REWARDS_DISCRIMINATOR.write(_data, 0);
    SerDeUtil.writeVector(4, mints, _data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ClaimVaultRewardsIxData(Discriminator discriminator, PublicKey[] mints) implements SerDe {  

    public static ClaimVaultRewardsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int MINTS_OFFSET = 8;

    public static ClaimVaultRewardsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mints = SerDeUtil.readPublicKeyVector(4, _data, i);
      return new ClaimVaultRewardsIxData(discriminator, mints);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += SerDeUtil.writeVector(4, mints, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + SerDeUtil.lenVector(4, mints);
    }
  }

  public static final Discriminator CLOSE_LOAN_DISCRIMINATOR = toDiscriminator(96, 114, 111, 204, 149, 228, 235, 124);

  /// 1.4 close loan
  ///
  public static List<AccountMeta> closeLoanKeys(final PublicKey bsAuthKey,
                                                final PublicKey payerKey,
                                                final PublicKey borrowerKey,
                                                final PublicKey loanKey,
                                                final PublicKey systemProgramKey,
                                                final PublicKey eventAuthorityKey,
                                                final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 1.4 close loan
  ///
  public static Instruction closeLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                      final PublicKey bsAuthKey,
                                      final PublicKey payerKey,
                                      final PublicKey borrowerKey,
                                      final PublicKey loanKey,
                                      final PublicKey systemProgramKey,
                                      final PublicKey eventAuthorityKey,
                                      final PublicKey programKey) {
    final var keys = closeLoanKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return closeLoan(invokedLoopscaleProgramMeta, keys);
  }

  /// 1.4 close loan
  ///
  public static Instruction closeLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, CLOSE_LOAN_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_STRATEGY_DISCRIMINATOR = toDiscriminator(56, 247, 170, 246, 89, 221, 134, 200);

  /// 8.5 close strategy
  ///
  public static List<AccountMeta> closeStrategyKeys(final PublicKey bsAuthKey,
                                                    final PublicKey payerKey,
                                                    final PublicKey lenderKey,
                                                    final PublicKey strategyKey,
                                                    final PublicKey principalMintKey,
                                                    final PublicKey tokenProgramKey,
                                                    final PublicKey associatedTokenProgramKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(lenderKey),
      createWrite(strategyKey),
      createRead(principalMintKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 8.5 close strategy
  ///
  public static Instruction closeStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                          final PublicKey bsAuthKey,
                                          final PublicKey payerKey,
                                          final PublicKey lenderKey,
                                          final PublicKey strategyKey,
                                          final PublicKey principalMintKey,
                                          final PublicKey tokenProgramKey,
                                          final PublicKey associatedTokenProgramKey,
                                          final PublicKey systemProgramKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey) {
    final var keys = closeStrategyKeys(
      bsAuthKey,
      payerKey,
      lenderKey,
      strategyKey,
      principalMintKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return closeStrategy(invokedLoopscaleProgramMeta, keys);
  }

  /// 8.5 close strategy
  ///
  public static Instruction closeStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, CLOSE_STRATEGY_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_LOAN_DISCRIMINATOR = toDiscriminator(166, 131, 118, 219, 138, 218, 206, 140);

  /// creditbook instructionss
  /// 
  /// 1. loan instructions
  /// 1.1 create loan
  ///
  public static List<AccountMeta> createLoanKeys(final PublicKey bsAuthKey,
                                                 final PublicKey payerKey,
                                                 final PublicKey borrowerKey,
                                                 final PublicKey loanKey,
                                                 final PublicKey systemProgramKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// creditbook instructionss
  /// 
  /// 1. loan instructions
  /// 1.1 create loan
  ///
  public static Instruction createLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                       final PublicKey bsAuthKey,
                                       final PublicKey payerKey,
                                       final PublicKey borrowerKey,
                                       final PublicKey loanKey,
                                       final PublicKey systemProgramKey,
                                       final PublicKey eventAuthorityKey,
                                       final PublicKey programKey,
                                       final CreateLoanParams params) {
    final var keys = createLoanKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createLoan(invokedLoopscaleProgramMeta, keys, params);
  }

  /// creditbook instructionss
  /// 
  /// 1. loan instructions
  /// 1.1 create loan
  ///
  public static Instruction createLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                       final List<AccountMeta> keys,
                                       final CreateLoanParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CREATE_LOAN_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record CreateLoanIxData(Discriminator discriminator, CreateLoanParams params) implements SerDe {  

    public static CreateLoanIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int PARAMS_OFFSET = 8;

    public static CreateLoanIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CreateLoanParams.read(_data, i);
      return new CreateLoanIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_MARKET_INFORMATION_DISCRIMINATOR = toDiscriminator(246, 45, 227, 173, 15, 51, 85, 1);

  /// 7. oracle instructions
  /// 7.1 create market information account
  ///
  public static List<AccountMeta> createMarketInformationKeys(final PublicKey bsAuthKey,
                                                              final PublicKey marketInformationKey,
                                                              final PublicKey principalMintKey,
                                                              final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWrite(marketInformationKey),
      createRead(principalMintKey),
      createRead(systemProgramKey)
    );
  }

  /// 7. oracle instructions
  /// 7.1 create market information account
  ///
  public static Instruction createMarketInformation(final AccountMeta invokedLoopscaleProgramMeta,
                                                    final PublicKey bsAuthKey,
                                                    final PublicKey marketInformationKey,
                                                    final PublicKey principalMintKey,
                                                    final PublicKey systemProgramKey,
                                                    final CreateMarketInformationParams params) {
    final var keys = createMarketInformationKeys(
      bsAuthKey,
      marketInformationKey,
      principalMintKey,
      systemProgramKey
    );
    return createMarketInformation(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 7. oracle instructions
  /// 7.1 create market information account
  ///
  public static Instruction createMarketInformation(final AccountMeta invokedLoopscaleProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final CreateMarketInformationParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CREATE_MARKET_INFORMATION_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record CreateMarketInformationIxData(Discriminator discriminator, CreateMarketInformationParams params) implements SerDe {  

    public static CreateMarketInformationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 72;

    public static final int PARAMS_OFFSET = 8;

    public static CreateMarketInformationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CreateMarketInformationParams.read(_data, i);
      return new CreateMarketInformationIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_REWARDS_SCHEDULE_DISCRIMINATOR = toDiscriminator(201, 90, 205, 53, 85, 60, 229, 176);

  /// 9.5 reward management instructions
  /// 9.5.1 create rewards schedule
  ///
  public static List<AccountMeta> createRewardsScheduleKeys(final PublicKey bsAuthKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey managerKey,
                                                            final PublicKey rewardsSourceKey,
                                                            final PublicKey vaultKey,
                                                            final PublicKey vaultRewardsInfoKey,
                                                            final PublicKey vaultRewardsMintKey,
                                                            final PublicKey vaultRewardsTaKey,
                                                            final PublicKey rewardsSourceTaKey,
                                                            final PublicKey tokenProgramKey,
                                                            final PublicKey associatedTokenProgramKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(managerKey),
      createReadOnlySigner(rewardsSourceKey),
      createWrite(vaultKey),
      createWrite(vaultRewardsInfoKey),
      createRead(vaultRewardsMintKey),
      createWrite(vaultRewardsTaKey),
      createWrite(rewardsSourceTaKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.5 reward management instructions
  /// 9.5.1 create rewards schedule
  ///
  public static Instruction createRewardsSchedule(final AccountMeta invokedLoopscaleProgramMeta,
                                                  final PublicKey bsAuthKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey managerKey,
                                                  final PublicKey rewardsSourceKey,
                                                  final PublicKey vaultKey,
                                                  final PublicKey vaultRewardsInfoKey,
                                                  final PublicKey vaultRewardsMintKey,
                                                  final PublicKey vaultRewardsTaKey,
                                                  final PublicKey rewardsSourceTaKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey associatedTokenProgramKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final CreateRewardsScheduleParams params,
                                                  final long amountToTransfer) {
    final var keys = createRewardsScheduleKeys(
      bsAuthKey,
      payerKey,
      managerKey,
      rewardsSourceKey,
      vaultKey,
      vaultRewardsInfoKey,
      vaultRewardsMintKey,
      vaultRewardsTaKey,
      rewardsSourceTaKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createRewardsSchedule(invokedLoopscaleProgramMeta, keys, params, amountToTransfer);
  }

  /// 9.5 reward management instructions
  /// 9.5.1 create rewards schedule
  ///
  public static Instruction createRewardsSchedule(final AccountMeta invokedLoopscaleProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final CreateRewardsScheduleParams params,
                                                  final long amountToTransfer) {
    final byte[] _data = new byte[16 + params.l()];
    int i = CREATE_REWARDS_SCHEDULE_DISCRIMINATOR.write(_data, 0);
    i += params.write(_data, i);
    putInt64LE(_data, i, amountToTransfer);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record CreateRewardsScheduleIxData(Discriminator discriminator, CreateRewardsScheduleParams params, long amountToTransfer) implements SerDe {  

    public static CreateRewardsScheduleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 76;

    public static final int PARAMS_OFFSET = 8;
    public static final int AMOUNT_TO_TRANSFER_OFFSET = 68;

    public static CreateRewardsScheduleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CreateRewardsScheduleParams.read(_data, i);
      i += params.l();
      final var amountToTransfer = getInt64LE(_data, i);
      return new CreateRewardsScheduleIxData(discriminator, params, amountToTransfer);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      putInt64LE(_data, i, amountToTransfer);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_STRATEGY_DISCRIMINATOR = toDiscriminator(152, 160, 107, 148, 245, 190, 127, 224);

  /// 8. strategy instructions
  /// 8.1 create strategy
  ///
  public static List<AccountMeta> createStrategyKeys(final PublicKey bsAuthKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey nonceKey,
                                                     final PublicKey strategyKey,
                                                     final PublicKey marketInformationKey,
                                                     final PublicKey principalMintKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(nonceKey),
      createWrite(strategyKey),
      createRead(marketInformationKey),
      createRead(principalMintKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 8. strategy instructions
  /// 8.1 create strategy
  ///
  public static Instruction createStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                           final PublicKey bsAuthKey,
                                           final PublicKey payerKey,
                                           final PublicKey nonceKey,
                                           final PublicKey strategyKey,
                                           final PublicKey marketInformationKey,
                                           final PublicKey principalMintKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final CreateStrategyParams params) {
    final var keys = createStrategyKeys(
      bsAuthKey,
      payerKey,
      nonceKey,
      strategyKey,
      marketInformationKey,
      principalMintKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createStrategy(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 8. strategy instructions
  /// 8.1 create strategy
  ///
  public static Instruction createStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                           final List<AccountMeta> keys,
                                           final CreateStrategyParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CREATE_STRATEGY_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record CreateStrategyIxData(Discriminator discriminator, CreateStrategyParams params) implements SerDe {  

    public static CreateStrategyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static CreateStrategyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CreateStrategyParams.read(_data, i);
      return new CreateStrategyIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator CREATE_TIMELOCK_DISCRIMINATOR = toDiscriminator(243, 10, 110, 170, 71, 251, 210, 87);

  /// 9.2 timelock instructions
  /// 9.2.1 timelock create
  ///
  public static List<AccountMeta> createTimelockKeys(final PublicKey bsAuthKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey managerKey,
                                                     final PublicKey vaultKey,
                                                     final PublicKey timelockKey,
                                                     final PublicKey vaultMarketInformationKey,
                                                     final PublicKey externalMarketInformationKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(managerKey),
      createWrite(vaultKey),
      createWritableSigner(timelockKey),
      createWrite(vaultMarketInformationKey),
      createRead(externalMarketInformationKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.2 timelock instructions
  /// 9.2.1 timelock create
  ///
  public static Instruction createTimelock(final AccountMeta invokedLoopscaleProgramMeta,
                                           final PublicKey bsAuthKey,
                                           final PublicKey payerKey,
                                           final PublicKey managerKey,
                                           final PublicKey vaultKey,
                                           final PublicKey timelockKey,
                                           final PublicKey vaultMarketInformationKey,
                                           final PublicKey externalMarketInformationKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final TimelockUpdateParams params) {
    final var keys = createTimelockKeys(
      bsAuthKey,
      payerKey,
      managerKey,
      vaultKey,
      timelockKey,
      vaultMarketInformationKey,
      externalMarketInformationKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createTimelock(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.2 timelock instructions
  /// 9.2.1 timelock create
  ///
  public static Instruction createTimelock(final AccountMeta invokedLoopscaleProgramMeta,
                                           final List<AccountMeta> keys,
                                           final TimelockUpdateParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CREATE_TIMELOCK_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record CreateTimelockIxData(Discriminator discriminator, TimelockUpdateParams params) implements SerDe {  

    public static CreateTimelockIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static CreateTimelockIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TimelockUpdateParams.read(_data, i);
      return new CreateTimelockIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator CREATE_VAULT_DISCRIMINATOR = toDiscriminator(29, 237, 247, 208, 193, 82, 54, 135);

  /// 9.3 create vault
  ///
  public static List<AccountMeta> createVaultKeys(final PublicKey bsAuthKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey nonceKey,
                                                  final PublicKey principalMintKey,
                                                  final PublicKey lpMintKey,
                                                  final PublicKey vaultKey,
                                                  final PublicKey strategyKey,
                                                  final PublicKey marketInformationKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(nonceKey),
      createRead(principalMintKey),
      createWritableSigner(lpMintKey),
      createWrite(vaultKey),
      createWrite(strategyKey),
      createWrite(marketInformationKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.3 create vault
  ///
  public static Instruction createVault(final AccountMeta invokedLoopscaleProgramMeta,
                                        final PublicKey bsAuthKey,
                                        final PublicKey payerKey,
                                        final PublicKey nonceKey,
                                        final PublicKey principalMintKey,
                                        final PublicKey lpMintKey,
                                        final PublicKey vaultKey,
                                        final PublicKey strategyKey,
                                        final PublicKey marketInformationKey,
                                        final PublicKey tokenProgramKey,
                                        final PublicKey systemProgramKey,
                                        final PublicKey eventAuthorityKey,
                                        final PublicKey programKey,
                                        final CreateVaultParams params) {
    final var keys = createVaultKeys(
      bsAuthKey,
      payerKey,
      nonceKey,
      principalMintKey,
      lpMintKey,
      vaultKey,
      strategyKey,
      marketInformationKey,
      tokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return createVault(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.3 create vault
  ///
  public static Instruction createVault(final AccountMeta invokedLoopscaleProgramMeta,
                                        final List<AccountMeta> keys,
                                        final CreateVaultParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = CREATE_VAULT_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record CreateVaultIxData(Discriminator discriminator, CreateVaultParams params) implements SerDe {  

    public static CreateVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static CreateVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CreateVaultParams.read(_data, i);
      return new CreateVaultIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator DEPOSIT_COLLATERAL_DISCRIMINATOR = toDiscriminator(156, 131, 142, 116, 146, 247, 162, 120);

  /// collateral instructions
  /// 5.
  /// 5.1. deposit collateral
  ///
  /// @param assetIdentifierKey CHECKs: checks in constraint
  public static List<AccountMeta> depositCollateralKeys(final PublicKey bsAuthKey,
                                                        final PublicKey payerKey,
                                                        final PublicKey borrowerKey,
                                                        final PublicKey loanKey,
                                                        final PublicKey borrowerCollateralTaKey,
                                                        final PublicKey loanCollateralTaKey,
                                                        final PublicKey depositMintKey,
                                                        final PublicKey assetIdentifierKey,
                                                        final PublicKey systemProgramKey,
                                                        final PublicKey tokenProgramKey,
                                                        final PublicKey associatedTokenProgramKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWritableSigner(borrowerKey),
      createWrite(loanKey),
      createWrite(borrowerCollateralTaKey),
      createWrite(loanCollateralTaKey),
      createRead(depositMintKey),
      createRead(assetIdentifierKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// collateral instructions
  /// 5.
  /// 5.1. deposit collateral
  ///
  /// @param assetIdentifierKey CHECKs: checks in constraint
  public static Instruction depositCollateral(final AccountMeta invokedLoopscaleProgramMeta,
                                              final PublicKey bsAuthKey,
                                              final PublicKey payerKey,
                                              final PublicKey borrowerKey,
                                              final PublicKey loanKey,
                                              final PublicKey borrowerCollateralTaKey,
                                              final PublicKey loanCollateralTaKey,
                                              final PublicKey depositMintKey,
                                              final PublicKey assetIdentifierKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey tokenProgramKey,
                                              final PublicKey associatedTokenProgramKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey,
                                              final DepositCollateralParams params) {
    final var keys = depositCollateralKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      borrowerCollateralTaKey,
      loanCollateralTaKey,
      depositMintKey,
      assetIdentifierKey,
      systemProgramKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return depositCollateral(invokedLoopscaleProgramMeta, keys, params);
  }

  /// collateral instructions
  /// 5.
  /// 5.1. deposit collateral
  ///
  public static Instruction depositCollateral(final AccountMeta invokedLoopscaleProgramMeta,
                                              final List<AccountMeta> keys,
                                              final DepositCollateralParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = DEPOSIT_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record DepositCollateralIxData(Discriminator discriminator, DepositCollateralParams params) implements SerDe {  

    public static DepositCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static DepositCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = DepositCollateralParams.read(_data, i);
      return new DepositCollateralIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator DEPOSIT_STRATEGY_DISCRIMINATOR = toDiscriminator(246, 82, 57, 226, 131, 222, 253, 249);

  /// 8.2 deposit strategy
  ///
  public static List<AccountMeta> depositStrategyKeys(final PublicKey bsAuthKey,
                                                      final PublicKey payerKey,
                                                      final PublicKey lenderKey,
                                                      final PublicKey strategyKey,
                                                      final PublicKey principalMintKey,
                                                      final PublicKey marketInformationKey,
                                                      final PublicKey lenderTaKey,
                                                      final PublicKey strategyTaKey,
                                                      final PublicKey tokenProgramKey,
                                                      final PublicKey associatedTokenProgramKey,
                                                      final PublicKey systemProgramKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWritableSigner(lenderKey),
      createWrite(strategyKey),
      createRead(principalMintKey),
      createRead(marketInformationKey),
      createWrite(lenderTaKey),
      createWrite(strategyTaKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 8.2 deposit strategy
  ///
  public static Instruction depositStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                            final PublicKey bsAuthKey,
                                            final PublicKey payerKey,
                                            final PublicKey lenderKey,
                                            final PublicKey strategyKey,
                                            final PublicKey principalMintKey,
                                            final PublicKey marketInformationKey,
                                            final PublicKey lenderTaKey,
                                            final PublicKey strategyTaKey,
                                            final PublicKey tokenProgramKey,
                                            final PublicKey associatedTokenProgramKey,
                                            final PublicKey systemProgramKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final long amount) {
    final var keys = depositStrategyKeys(
      bsAuthKey,
      payerKey,
      lenderKey,
      strategyKey,
      principalMintKey,
      marketInformationKey,
      lenderTaKey,
      strategyTaKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return depositStrategy(invokedLoopscaleProgramMeta, keys, amount);
  }

  /// 8.2 deposit strategy
  ///
  public static Instruction depositStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                            final List<AccountMeta> keys,
                                            final long amount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_STRATEGY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record DepositStrategyIxData(Discriminator discriminator, long amount) implements SerDe {  

    public static DepositStrategyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int AMOUNT_OFFSET = 8;

    public static DepositStrategyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new DepositStrategyIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_USER_VAULT_DISCRIMINATOR = toDiscriminator(204, 190, 182, 224, 15, 219, 247, 121);

  /// 9. vault instructions
  /// 9.1 vault actions
  /// 9.1.1 vault user actions
  /// 9.1.1.1 vault user deposit
  ///
  public static List<AccountMeta> depositUserVaultKeys(final PublicKey bsAuthKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey userKey,
                                                       final PublicKey vaultKey,
                                                       final PublicKey strategyKey,
                                                       final PublicKey marketInformationKey,
                                                       final PublicKey lpMintKey,
                                                       final PublicKey userLpTaKey,
                                                       final PublicKey userPrincipalTaKey,
                                                       final PublicKey strategyPrincipalTaKey,
                                                       final PublicKey principalMintKey,
                                                       final PublicKey principalTokenProgramKey,
                                                       final PublicKey token2022ProgramKey,
                                                       final PublicKey associatedTokenProgramKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWritableSigner(userKey),
      createWrite(vaultKey),
      createWrite(strategyKey),
      createRead(marketInformationKey),
      createWrite(lpMintKey),
      createWrite(userLpTaKey),
      createWrite(userPrincipalTaKey),
      createWrite(strategyPrincipalTaKey),
      createRead(principalMintKey),
      createRead(principalTokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9. vault instructions
  /// 9.1 vault actions
  /// 9.1.1 vault user actions
  /// 9.1.1.1 vault user deposit
  ///
  public static Instruction depositUserVault(final AccountMeta invokedLoopscaleProgramMeta,
                                             final PublicKey bsAuthKey,
                                             final PublicKey payerKey,
                                             final PublicKey userKey,
                                             final PublicKey vaultKey,
                                             final PublicKey strategyKey,
                                             final PublicKey marketInformationKey,
                                             final PublicKey lpMintKey,
                                             final PublicKey userLpTaKey,
                                             final PublicKey userPrincipalTaKey,
                                             final PublicKey strategyPrincipalTaKey,
                                             final PublicKey principalMintKey,
                                             final PublicKey principalTokenProgramKey,
                                             final PublicKey token2022ProgramKey,
                                             final PublicKey associatedTokenProgramKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey,
                                             final LpParams params) {
    final var keys = depositUserVaultKeys(
      bsAuthKey,
      payerKey,
      userKey,
      vaultKey,
      strategyKey,
      marketInformationKey,
      lpMintKey,
      userLpTaKey,
      userPrincipalTaKey,
      strategyPrincipalTaKey,
      principalMintKey,
      principalTokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return depositUserVault(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9. vault instructions
  /// 9.1 vault actions
  /// 9.1.1 vault user actions
  /// 9.1.1.1 vault user deposit
  ///
  public static Instruction depositUserVault(final AccountMeta invokedLoopscaleProgramMeta,
                                             final List<AccountMeta> keys,
                                             final LpParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = DEPOSIT_USER_VAULT_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record DepositUserVaultIxData(Discriminator discriminator, LpParams params) implements SerDe {  

    public static DepositUserVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static DepositUserVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LpParams.read(_data, i);
      return new DepositUserVaultIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator EXECUTE_TIMELOCK_DISCRIMINATOR = toDiscriminator(160, 194, 240, 8, 212, 93, 157, 221);

  /// 9.2.2 timelock execute
  ///
  public static List<AccountMeta> executeTimelockKeys(final PublicKey bsAuthKey,
                                                      final PublicKey payerKey,
                                                      final PublicKey vaultKey,
                                                      final PublicKey timelockKey,
                                                      final PublicKey strategyKey,
                                                      final PublicKey marketInformationKey,
                                                      final PublicKey strategyTaKey,
                                                      final PublicKey systemProgramKey,
                                                      final PublicKey tokenProgramKey,
                                                      final PublicKey associatedTokenProgramKey,
                                                      final PublicKey principalMintKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWrite(vaultKey),
      createWrite(timelockKey),
      createWrite(strategyKey),
      createWrite(marketInformationKey),
      createWrite(strategyTaKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(principalMintKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.2.2 timelock execute
  ///
  public static Instruction executeTimelock(final AccountMeta invokedLoopscaleProgramMeta,
                                            final PublicKey bsAuthKey,
                                            final PublicKey payerKey,
                                            final PublicKey vaultKey,
                                            final PublicKey timelockKey,
                                            final PublicKey strategyKey,
                                            final PublicKey marketInformationKey,
                                            final PublicKey strategyTaKey,
                                            final PublicKey systemProgramKey,
                                            final PublicKey tokenProgramKey,
                                            final PublicKey associatedTokenProgramKey,
                                            final PublicKey principalMintKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey) {
    final var keys = executeTimelockKeys(
      bsAuthKey,
      payerKey,
      vaultKey,
      timelockKey,
      strategyKey,
      marketInformationKey,
      strategyTaKey,
      systemProgramKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      principalMintKey,
      eventAuthorityKey,
      programKey
    );
    return executeTimelock(invokedLoopscaleProgramMeta, keys);
  }

  /// 9.2.2 timelock execute
  ///
  public static Instruction executeTimelock(final AccountMeta invokedLoopscaleProgramMeta,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, EXECUTE_TIMELOCK_DISCRIMINATOR);
  }

  public static final Discriminator LIQUIDATE_LEDGER_DISCRIMINATOR = toDiscriminator(5, 124, 101, 85, 254, 175, 184, 249);

  /// 2. liquidate ledger
  ///
  public static List<AccountMeta> liquidateLedgerKeys(final PublicKey payerKey,
                                                      final PublicKey liquidatorKey,
                                                      final PublicKey borrowerKey,
                                                      final PublicKey loanKey,
                                                      final PublicKey strategyKey,
                                                      final PublicKey marketInformationKey,
                                                      final PublicKey liquidatorTaKey,
                                                      final PublicKey strategyTaKey,
                                                      final PublicKey principalMintKey,
                                                      final PublicKey associatedTokenProgramKey,
                                                      final PublicKey tokenProgramKey,
                                                      final PublicKey token2022ProgramKey,
                                                      final PublicKey systemProgramKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWritableSigner(liquidatorKey),
      createWrite(borrowerKey),
      createWrite(loanKey),
      createWrite(strategyKey),
      createWrite(marketInformationKey),
      createWrite(liquidatorTaKey),
      createWrite(strategyTaKey),
      createRead(principalMintKey),
      createRead(associatedTokenProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 2. liquidate ledger
  ///
  public static Instruction liquidateLedger(final AccountMeta invokedLoopscaleProgramMeta,
                                            final PublicKey payerKey,
                                            final PublicKey liquidatorKey,
                                            final PublicKey borrowerKey,
                                            final PublicKey loanKey,
                                            final PublicKey strategyKey,
                                            final PublicKey marketInformationKey,
                                            final PublicKey liquidatorTaKey,
                                            final PublicKey strategyTaKey,
                                            final PublicKey principalMintKey,
                                            final PublicKey associatedTokenProgramKey,
                                            final PublicKey tokenProgramKey,
                                            final PublicKey token2022ProgramKey,
                                            final PublicKey systemProgramKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final LiquidateLedgerParams params) {
    final var keys = liquidateLedgerKeys(
      payerKey,
      liquidatorKey,
      borrowerKey,
      loanKey,
      strategyKey,
      marketInformationKey,
      liquidatorTaKey,
      strategyTaKey,
      principalMintKey,
      associatedTokenProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return liquidateLedger(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 2. liquidate ledger
  ///
  public static Instruction liquidateLedger(final AccountMeta invokedLoopscaleProgramMeta,
                                            final List<AccountMeta> keys,
                                            final LiquidateLedgerParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = LIQUIDATE_LEDGER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record LiquidateLedgerIxData(Discriminator discriminator, LiquidateLedgerParams params) implements SerDe {  

    public static LiquidateLedgerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static LiquidateLedgerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LiquidateLedgerParams.read(_data, i);
      return new LiquidateLedgerIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator LOCK_LOAN_DISCRIMINATOR = toDiscriminator(28, 101, 52, 240, 146, 230, 95, 22);

  /// 1.2 lock loan
  ///
  public static List<AccountMeta> lockLoanKeys(final PublicKey bsAuthKey,
                                               final PublicKey payerKey,
                                               final PublicKey borrowerKey,
                                               final PublicKey loanKey,
                                               final PublicKey instructionsSysvarKey,
                                               final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createRead(instructionsSysvarKey),
      createRead(systemProgramKey)
    );
  }

  /// 1.2 lock loan
  ///
  public static Instruction lockLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                     final PublicKey bsAuthKey,
                                     final PublicKey payerKey,
                                     final PublicKey borrowerKey,
                                     final PublicKey loanKey,
                                     final PublicKey instructionsSysvarKey,
                                     final PublicKey systemProgramKey,
                                     final LockLoanParams params) {
    final var keys = lockLoanKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      instructionsSysvarKey,
      systemProgramKey
    );
    return lockLoan(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 1.2 lock loan
  ///
  public static Instruction lockLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                     final List<AccountMeta> keys,
                                     final LockLoanParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = LOCK_LOAN_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record LockLoanIxData(Discriminator discriminator, LockLoanParams params) implements SerDe {  

    public static LockLoanIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int PARAMS_OFFSET = 8;

    public static LockLoanIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LockLoanParams.read(_data, i);
      return new LockLoanIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_CLAIM_ORCA_FEE_DISCRIMINATOR = toDiscriminator(242, 48, 127, 91, 24, 187, 211, 234);

  /// 5.3. orca deposit collateral
  /// manage collateral instructions
  /// 5.3
  /// 5.3.1 orca claim fee
  ///
  public static List<AccountMeta> manageCollateralClaimOrcaFeeKeys(final PublicKey bsAuthKey,
                                                                   final PublicKey payerKey,
                                                                   final PublicKey borrowerKey,
                                                                   final PublicKey loanKey,
                                                                   final PublicKey whirlpoolKey,
                                                                   final PublicKey tokenVaultAKey,
                                                                   final PublicKey tokenVaultBKey,
                                                                   final PublicKey tickArrayLowerKey,
                                                                   final PublicKey tickArrayUpperKey,
                                                                   final PublicKey positionKey,
                                                                   final PublicKey positionTokenAccountKey,
                                                                   final PublicKey borrowerTaAKey,
                                                                   final PublicKey borrowerTaBKey,
                                                                   final PublicKey loanTaAKey,
                                                                   final PublicKey loanTaBKey,
                                                                   final PublicKey mintAKey,
                                                                   final PublicKey mintBKey,
                                                                   final PublicKey whirlpoolProgramKey,
                                                                   final PublicKey tokenProgramKey,
                                                                   final PublicKey token2022ProgramKey,
                                                                   final PublicKey associatedTokenProgramKey,
                                                                   final PublicKey systemProgramKey,
                                                                   final PublicKey memoProgramKey,
                                                                   final PublicKey eventAuthorityKey,
                                                                   final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createRead(loanKey),
      createWrite(whirlpoolKey),
      createWrite(tokenVaultAKey),
      createWrite(tokenVaultBKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(positionKey),
      createWrite(positionTokenAccountKey),
      createWrite(borrowerTaAKey),
      createWrite(borrowerTaBKey),
      createWrite(loanTaAKey),
      createWrite(loanTaBKey),
      createRead(mintAKey),
      createRead(mintBKey),
      createRead(whirlpoolProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.3. orca deposit collateral
  /// manage collateral instructions
  /// 5.3
  /// 5.3.1 orca claim fee
  ///
  public static Instruction manageCollateralClaimOrcaFee(final AccountMeta invokedLoopscaleProgramMeta,
                                                         final PublicKey bsAuthKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey borrowerKey,
                                                         final PublicKey loanKey,
                                                         final PublicKey whirlpoolKey,
                                                         final PublicKey tokenVaultAKey,
                                                         final PublicKey tokenVaultBKey,
                                                         final PublicKey tickArrayLowerKey,
                                                         final PublicKey tickArrayUpperKey,
                                                         final PublicKey positionKey,
                                                         final PublicKey positionTokenAccountKey,
                                                         final PublicKey borrowerTaAKey,
                                                         final PublicKey borrowerTaBKey,
                                                         final PublicKey loanTaAKey,
                                                         final PublicKey loanTaBKey,
                                                         final PublicKey mintAKey,
                                                         final PublicKey mintBKey,
                                                         final PublicKey whirlpoolProgramKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey token2022ProgramKey,
                                                         final PublicKey associatedTokenProgramKey,
                                                         final PublicKey systemProgramKey,
                                                         final PublicKey memoProgramKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey,
                                                         final boolean closeTa) {
    final var keys = manageCollateralClaimOrcaFeeKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      whirlpoolKey,
      tokenVaultAKey,
      tokenVaultBKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      positionKey,
      positionTokenAccountKey,
      borrowerTaAKey,
      borrowerTaBKey,
      loanTaAKey,
      loanTaBKey,
      mintAKey,
      mintBKey,
      whirlpoolProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return manageCollateralClaimOrcaFee(invokedLoopscaleProgramMeta, keys, closeTa);
  }

  /// 5.3. orca deposit collateral
  /// manage collateral instructions
  /// 5.3
  /// 5.3.1 orca claim fee
  ///
  public static Instruction manageCollateralClaimOrcaFee(final AccountMeta invokedLoopscaleProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final boolean closeTa) {
    final byte[] _data = new byte[9];
    int i = MANAGE_COLLATERAL_CLAIM_ORCA_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (closeTa ? 1 : 0);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralClaimOrcaFeeIxData(Discriminator discriminator, boolean closeTa) implements SerDe {  

    public static ManageCollateralClaimOrcaFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int CLOSE_TA_OFFSET = 8;

    public static ManageCollateralClaimOrcaFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var closeTa = _data[i] == 1;
      return new ManageCollateralClaimOrcaFeeIxData(discriminator, closeTa);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (closeTa ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_DECREASE_RAYDIUM_LIQUIDITY_DISCRIMINATOR = toDiscriminator(203, 192, 202, 215, 108, 54, 90, 202);

  /// 5.3.5 raydium decrease liquidity
  ///
  public static List<AccountMeta> manageCollateralDecreaseRaydiumLiquidityKeys(final PublicKey bsAuthKey,
                                                                               final PublicKey payerKey,
                                                                               final PublicKey borrowerKey,
                                                                               final PublicKey loanKey,
                                                                               final PublicKey poolKey,
                                                                               final PublicKey protocolPositionKey,
                                                                               final PublicKey positionKey,
                                                                               final PublicKey positionTokenAccountKey,
                                                                               final PublicKey tickArrayLowerKey,
                                                                               final PublicKey tickArrayUpperKey,
                                                                               final PublicKey tokenVault0Key,
                                                                               final PublicKey tokenVault1Key,
                                                                               final PublicKey borrowerTa0Key,
                                                                               final PublicKey borrowerTa1Key,
                                                                               final PublicKey loanTa0Key,
                                                                               final PublicKey loanTa1Key,
                                                                               final PublicKey mint0Key,
                                                                               final PublicKey mint1Key,
                                                                               final PublicKey raydiumProgramKey,
                                                                               final PublicKey tokenProgramKey,
                                                                               final PublicKey token2022ProgramKey,
                                                                               final PublicKey associatedTokenProgramKey,
                                                                               final PublicKey systemProgramKey,
                                                                               final PublicKey memoProgramKey,
                                                                               final PublicKey eventAuthorityKey,
                                                                               final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createRead(loanKey),
      createWrite(poolKey),
      createWrite(protocolPositionKey),
      createWrite(positionKey),
      createRead(positionTokenAccountKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(tokenVault0Key),
      createWrite(tokenVault1Key),
      createWrite(borrowerTa0Key),
      createWrite(borrowerTa1Key),
      createWrite(loanTa0Key),
      createWrite(loanTa1Key),
      createRead(mint0Key),
      createRead(mint1Key),
      createRead(raydiumProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.3.5 raydium decrease liquidity
  ///
  public static Instruction manageCollateralDecreaseRaydiumLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                     final PublicKey bsAuthKey,
                                                                     final PublicKey payerKey,
                                                                     final PublicKey borrowerKey,
                                                                     final PublicKey loanKey,
                                                                     final PublicKey poolKey,
                                                                     final PublicKey protocolPositionKey,
                                                                     final PublicKey positionKey,
                                                                     final PublicKey positionTokenAccountKey,
                                                                     final PublicKey tickArrayLowerKey,
                                                                     final PublicKey tickArrayUpperKey,
                                                                     final PublicKey tokenVault0Key,
                                                                     final PublicKey tokenVault1Key,
                                                                     final PublicKey borrowerTa0Key,
                                                                     final PublicKey borrowerTa1Key,
                                                                     final PublicKey loanTa0Key,
                                                                     final PublicKey loanTa1Key,
                                                                     final PublicKey mint0Key,
                                                                     final PublicKey mint1Key,
                                                                     final PublicKey raydiumProgramKey,
                                                                     final PublicKey tokenProgramKey,
                                                                     final PublicKey token2022ProgramKey,
                                                                     final PublicKey associatedTokenProgramKey,
                                                                     final PublicKey systemProgramKey,
                                                                     final PublicKey memoProgramKey,
                                                                     final PublicKey eventAuthorityKey,
                                                                     final PublicKey programKey,
                                                                     final ManageRaydiumLiquidityParams params) {
    final var keys = manageCollateralDecreaseRaydiumLiquidityKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      poolKey,
      protocolPositionKey,
      positionKey,
      positionTokenAccountKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      tokenVault0Key,
      tokenVault1Key,
      borrowerTa0Key,
      borrowerTa1Key,
      loanTa0Key,
      loanTa1Key,
      mint0Key,
      mint1Key,
      raydiumProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return manageCollateralDecreaseRaydiumLiquidity(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.3.5 raydium decrease liquidity
  ///
  public static Instruction manageCollateralDecreaseRaydiumLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                     final List<AccountMeta> keys,
                                                                     final ManageRaydiumLiquidityParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = MANAGE_COLLATERAL_DECREASE_RAYDIUM_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralDecreaseRaydiumLiquidityIxData(Discriminator discriminator, ManageRaydiumLiquidityParams params) implements SerDe {  

    public static ManageCollateralDecreaseRaydiumLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ManageCollateralDecreaseRaydiumLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ManageRaydiumLiquidityParams.read(_data, i);
      return new ManageCollateralDecreaseRaydiumLiquidityIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_INCREASE_ORCA_LIQUIDITY_DISCRIMINATOR = toDiscriminator(173, 62, 204, 113, 206, 27, 147, 128);

  /// 5.3.2 orca increase liquidity
  ///
  public static List<AccountMeta> manageCollateralIncreaseOrcaLiquidityKeys(final PublicKey bsAuthKey,
                                                                            final PublicKey payerKey,
                                                                            final PublicKey borrowerKey,
                                                                            final PublicKey loanKey,
                                                                            final PublicKey whirlpoolKey,
                                                                            final PublicKey positionKey,
                                                                            final PublicKey positionTokenAccountKey,
                                                                            final PublicKey tickArrayLowerKey,
                                                                            final PublicKey tickArrayUpperKey,
                                                                            final PublicKey tokenVaultAKey,
                                                                            final PublicKey tokenVaultBKey,
                                                                            final PublicKey borrowerTaAKey,
                                                                            final PublicKey borrowerTaBKey,
                                                                            final PublicKey loanTaAKey,
                                                                            final PublicKey loanTaBKey,
                                                                            final PublicKey mintAKey,
                                                                            final PublicKey mintBKey,
                                                                            final PublicKey whirlpoolProgramKey,
                                                                            final PublicKey tokenProgramKey,
                                                                            final PublicKey token2022ProgramKey,
                                                                            final PublicKey associatedTokenProgramKey,
                                                                            final PublicKey systemProgramKey,
                                                                            final PublicKey memoProgramKey,
                                                                            final PublicKey eventAuthorityKey,
                                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createRead(loanKey),
      createWrite(whirlpoolKey),
      createWrite(positionKey),
      createRead(positionTokenAccountKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(tokenVaultAKey),
      createWrite(tokenVaultBKey),
      createWrite(borrowerTaAKey),
      createWrite(borrowerTaBKey),
      createWrite(loanTaAKey),
      createWrite(loanTaBKey),
      createRead(mintAKey),
      createRead(mintBKey),
      createRead(whirlpoolProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.3.2 orca increase liquidity
  ///
  public static Instruction manageCollateralIncreaseOrcaLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                  final PublicKey bsAuthKey,
                                                                  final PublicKey payerKey,
                                                                  final PublicKey borrowerKey,
                                                                  final PublicKey loanKey,
                                                                  final PublicKey whirlpoolKey,
                                                                  final PublicKey positionKey,
                                                                  final PublicKey positionTokenAccountKey,
                                                                  final PublicKey tickArrayLowerKey,
                                                                  final PublicKey tickArrayUpperKey,
                                                                  final PublicKey tokenVaultAKey,
                                                                  final PublicKey tokenVaultBKey,
                                                                  final PublicKey borrowerTaAKey,
                                                                  final PublicKey borrowerTaBKey,
                                                                  final PublicKey loanTaAKey,
                                                                  final PublicKey loanTaBKey,
                                                                  final PublicKey mintAKey,
                                                                  final PublicKey mintBKey,
                                                                  final PublicKey whirlpoolProgramKey,
                                                                  final PublicKey tokenProgramKey,
                                                                  final PublicKey token2022ProgramKey,
                                                                  final PublicKey associatedTokenProgramKey,
                                                                  final PublicKey systemProgramKey,
                                                                  final PublicKey memoProgramKey,
                                                                  final PublicKey eventAuthorityKey,
                                                                  final PublicKey programKey,
                                                                  final ManageLiquidityParams params) {
    final var keys = manageCollateralIncreaseOrcaLiquidityKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      whirlpoolKey,
      positionKey,
      positionTokenAccountKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      tokenVaultAKey,
      tokenVaultBKey,
      borrowerTaAKey,
      borrowerTaBKey,
      loanTaAKey,
      loanTaBKey,
      mintAKey,
      mintBKey,
      whirlpoolProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return manageCollateralIncreaseOrcaLiquidity(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.3.2 orca increase liquidity
  ///
  public static Instruction manageCollateralIncreaseOrcaLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                  final List<AccountMeta> keys,
                                                                  final ManageLiquidityParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = MANAGE_COLLATERAL_INCREASE_ORCA_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralIncreaseOrcaLiquidityIxData(Discriminator discriminator, ManageLiquidityParams params) implements SerDe {  

    public static ManageCollateralIncreaseOrcaLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ManageCollateralIncreaseOrcaLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ManageLiquidityParams.read(_data, i);
      return new ManageCollateralIncreaseOrcaLiquidityIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_INCREASE_RAYDIUM_LIQUIDITY_DISCRIMINATOR = toDiscriminator(54, 205, 137, 146, 40, 175, 141, 20);

  /// 5.3.6 raydium increase liquidity
  ///
  public static List<AccountMeta> manageCollateralIncreaseRaydiumLiquidityKeys(final PublicKey bsAuthKey,
                                                                               final PublicKey payerKey,
                                                                               final PublicKey borrowerKey,
                                                                               final PublicKey loanKey,
                                                                               final PublicKey poolKey,
                                                                               final PublicKey protocolPositionKey,
                                                                               final PublicKey positionKey,
                                                                               final PublicKey positionTokenAccountKey,
                                                                               final PublicKey tickArrayLowerKey,
                                                                               final PublicKey tickArrayUpperKey,
                                                                               final PublicKey tokenVault0Key,
                                                                               final PublicKey tokenVault1Key,
                                                                               final PublicKey borrowerTa0Key,
                                                                               final PublicKey borrowerTa1Key,
                                                                               final PublicKey loanTa0Key,
                                                                               final PublicKey loanTa1Key,
                                                                               final PublicKey mint0Key,
                                                                               final PublicKey mint1Key,
                                                                               final PublicKey raydiumProgramKey,
                                                                               final PublicKey tokenProgramKey,
                                                                               final PublicKey token2022ProgramKey,
                                                                               final PublicKey associatedTokenProgramKey,
                                                                               final PublicKey systemProgramKey,
                                                                               final PublicKey memoProgramKey,
                                                                               final PublicKey eventAuthorityKey,
                                                                               final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createRead(loanKey),
      createWrite(poolKey),
      createWrite(protocolPositionKey),
      createWrite(positionKey),
      createRead(positionTokenAccountKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(tokenVault0Key),
      createWrite(tokenVault1Key),
      createWrite(borrowerTa0Key),
      createWrite(borrowerTa1Key),
      createWrite(loanTa0Key),
      createWrite(loanTa1Key),
      createRead(mint0Key),
      createRead(mint1Key),
      createRead(raydiumProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.3.6 raydium increase liquidity
  ///
  public static Instruction manageCollateralIncreaseRaydiumLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                     final PublicKey bsAuthKey,
                                                                     final PublicKey payerKey,
                                                                     final PublicKey borrowerKey,
                                                                     final PublicKey loanKey,
                                                                     final PublicKey poolKey,
                                                                     final PublicKey protocolPositionKey,
                                                                     final PublicKey positionKey,
                                                                     final PublicKey positionTokenAccountKey,
                                                                     final PublicKey tickArrayLowerKey,
                                                                     final PublicKey tickArrayUpperKey,
                                                                     final PublicKey tokenVault0Key,
                                                                     final PublicKey tokenVault1Key,
                                                                     final PublicKey borrowerTa0Key,
                                                                     final PublicKey borrowerTa1Key,
                                                                     final PublicKey loanTa0Key,
                                                                     final PublicKey loanTa1Key,
                                                                     final PublicKey mint0Key,
                                                                     final PublicKey mint1Key,
                                                                     final PublicKey raydiumProgramKey,
                                                                     final PublicKey tokenProgramKey,
                                                                     final PublicKey token2022ProgramKey,
                                                                     final PublicKey associatedTokenProgramKey,
                                                                     final PublicKey systemProgramKey,
                                                                     final PublicKey memoProgramKey,
                                                                     final PublicKey eventAuthorityKey,
                                                                     final PublicKey programKey,
                                                                     final ManageRaydiumLiquidityParams params) {
    final var keys = manageCollateralIncreaseRaydiumLiquidityKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      poolKey,
      protocolPositionKey,
      positionKey,
      positionTokenAccountKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      tokenVault0Key,
      tokenVault1Key,
      borrowerTa0Key,
      borrowerTa1Key,
      loanTa0Key,
      loanTa1Key,
      mint0Key,
      mint1Key,
      raydiumProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return manageCollateralIncreaseRaydiumLiquidity(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.3.6 raydium increase liquidity
  ///
  public static Instruction manageCollateralIncreaseRaydiumLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                     final List<AccountMeta> keys,
                                                                     final ManageRaydiumLiquidityParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = MANAGE_COLLATERAL_INCREASE_RAYDIUM_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralIncreaseRaydiumLiquidityIxData(Discriminator discriminator, ManageRaydiumLiquidityParams params) implements SerDe {  

    public static ManageCollateralIncreaseRaydiumLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ManageCollateralIncreaseRaydiumLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ManageRaydiumLiquidityParams.read(_data, i);
      return new ManageCollateralIncreaseRaydiumLiquidityIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_TRANSFER_ORCA_POSITION_DISCRIMINATOR = toDiscriminator(85, 151, 110, 243, 164, 41, 62, 238);

  /// 5.3.4 orca transfer position
  ///
  public static List<AccountMeta> manageCollateralTransferOrcaPositionKeys(final PublicKey bsAuthKey,
                                                                           final PublicKey payerKey,
                                                                           final PublicKey borrowerKey,
                                                                           final PublicKey loanKey,
                                                                           final PublicKey whirlpoolKey,
                                                                           final PublicKey positionKey,
                                                                           final PublicKey positionTokenAccountKey,
                                                                           final PublicKey positionMintKey,
                                                                           final PublicKey tickArrayLowerKey,
                                                                           final PublicKey tickArrayUpperKey,
                                                                           final PublicKey newTickArrayLowerKey,
                                                                           final PublicKey newTickArrayUpperKey,
                                                                           final PublicKey tokenVaultAKey,
                                                                           final PublicKey tokenVaultBKey,
                                                                           final PublicKey borrowerTaAKey,
                                                                           final PublicKey borrowerTaBKey,
                                                                           final PublicKey loanTaAKey,
                                                                           final PublicKey loanTaBKey,
                                                                           final PublicKey newPositionKey,
                                                                           final PublicKey newPositionMintKey,
                                                                           final PublicKey newPositionTokenAccountKey,
                                                                           final PublicKey mintAKey,
                                                                           final PublicKey mintBKey,
                                                                           final PublicKey metadataUpdateAuthKey,
                                                                           final PublicKey whirlpoolProgramKey,
                                                                           final PublicKey tokenProgramKey,
                                                                           final PublicKey token2022ProgramKey,
                                                                           final PublicKey associatedTokenProgramKey,
                                                                           final PublicKey systemProgramKey,
                                                                           final PublicKey memoProgramKey,
                                                                           final PublicKey eventAuthorityKey,
                                                                           final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createWrite(whirlpoolKey),
      createWrite(positionKey),
      createWrite(positionTokenAccountKey),
      createWrite(positionMintKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(newTickArrayLowerKey),
      createWrite(newTickArrayUpperKey),
      createWrite(tokenVaultAKey),
      createWrite(tokenVaultBKey),
      createWrite(borrowerTaAKey),
      createWrite(borrowerTaBKey),
      createWrite(loanTaAKey),
      createWrite(loanTaBKey),
      createWrite(newPositionKey),
      createWritableSigner(newPositionMintKey),
      createWrite(newPositionTokenAccountKey),
      createRead(mintAKey),
      createRead(mintBKey),
      createRead(metadataUpdateAuthKey),
      createRead(whirlpoolProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.3.4 orca transfer position
  ///
  public static Instruction manageCollateralTransferOrcaPosition(final AccountMeta invokedLoopscaleProgramMeta,
                                                                 final PublicKey bsAuthKey,
                                                                 final PublicKey payerKey,
                                                                 final PublicKey borrowerKey,
                                                                 final PublicKey loanKey,
                                                                 final PublicKey whirlpoolKey,
                                                                 final PublicKey positionKey,
                                                                 final PublicKey positionTokenAccountKey,
                                                                 final PublicKey positionMintKey,
                                                                 final PublicKey tickArrayLowerKey,
                                                                 final PublicKey tickArrayUpperKey,
                                                                 final PublicKey newTickArrayLowerKey,
                                                                 final PublicKey newTickArrayUpperKey,
                                                                 final PublicKey tokenVaultAKey,
                                                                 final PublicKey tokenVaultBKey,
                                                                 final PublicKey borrowerTaAKey,
                                                                 final PublicKey borrowerTaBKey,
                                                                 final PublicKey loanTaAKey,
                                                                 final PublicKey loanTaBKey,
                                                                 final PublicKey newPositionKey,
                                                                 final PublicKey newPositionMintKey,
                                                                 final PublicKey newPositionTokenAccountKey,
                                                                 final PublicKey mintAKey,
                                                                 final PublicKey mintBKey,
                                                                 final PublicKey metadataUpdateAuthKey,
                                                                 final PublicKey whirlpoolProgramKey,
                                                                 final PublicKey tokenProgramKey,
                                                                 final PublicKey token2022ProgramKey,
                                                                 final PublicKey associatedTokenProgramKey,
                                                                 final PublicKey systemProgramKey,
                                                                 final PublicKey memoProgramKey,
                                                                 final PublicKey eventAuthorityKey,
                                                                 final PublicKey programKey,
                                                                 final TransferPositionParams params) {
    final var keys = manageCollateralTransferOrcaPositionKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      whirlpoolKey,
      positionKey,
      positionTokenAccountKey,
      positionMintKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      newTickArrayLowerKey,
      newTickArrayUpperKey,
      tokenVaultAKey,
      tokenVaultBKey,
      borrowerTaAKey,
      borrowerTaBKey,
      loanTaAKey,
      loanTaBKey,
      newPositionKey,
      newPositionMintKey,
      newPositionTokenAccountKey,
      mintAKey,
      mintBKey,
      metadataUpdateAuthKey,
      whirlpoolProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return manageCollateralTransferOrcaPosition(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.3.4 orca transfer position
  ///
  public static Instruction manageCollateralTransferOrcaPosition(final AccountMeta invokedLoopscaleProgramMeta,
                                                                 final List<AccountMeta> keys,
                                                                 final TransferPositionParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = MANAGE_COLLATERAL_TRANSFER_ORCA_POSITION_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralTransferOrcaPositionIxData(Discriminator discriminator, TransferPositionParams params) implements SerDe {  

    public static ManageCollateralTransferOrcaPositionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ManageCollateralTransferOrcaPositionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TransferPositionParams.read(_data, i);
      return new ManageCollateralTransferOrcaPositionIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_TRANSFER_RAYDIUM_POSITION_DISCRIMINATOR = toDiscriminator(183, 215, 111, 172, 104, 250, 55, 219);

  /// 5.3.7 raydium transfer position
  ///
  public static List<AccountMeta> manageCollateralTransferRaydiumPositionKeys(final PublicKey bsAuthKey,
                                                                              final PublicKey payerKey,
                                                                              final PublicKey borrowerKey,
                                                                              final PublicKey loanKey,
                                                                              final PublicKey poolKey,
                                                                              final PublicKey protocolPositionOldKey,
                                                                              final PublicKey protocolPositionNewKey,
                                                                              final PublicKey positionKey,
                                                                              final PublicKey positionTokenAccountKey,
                                                                              final PublicKey positionMintKey,
                                                                              final PublicKey tickArrayLowerKey,
                                                                              final PublicKey tickArrayUpperKey,
                                                                              final PublicKey newTickArrayLowerKey,
                                                                              final PublicKey newTickArrayUpperKey,
                                                                              final PublicKey tokenVault0Key,
                                                                              final PublicKey tokenVault1Key,
                                                                              final PublicKey borrowerTa0Key,
                                                                              final PublicKey borrowerTa1Key,
                                                                              final PublicKey loanTa0Key,
                                                                              final PublicKey loanTa1Key,
                                                                              final PublicKey newPositionKey,
                                                                              final PublicKey newPositionMintKey,
                                                                              final PublicKey newPositionTokenAccountKey,
                                                                              final PublicKey mint0Key,
                                                                              final PublicKey mint1Key,
                                                                              final PublicKey raydiumProgramKey,
                                                                              final PublicKey tokenProgramKey,
                                                                              final PublicKey token2022ProgramKey,
                                                                              final PublicKey associatedTokenProgramKey,
                                                                              final PublicKey systemProgramKey,
                                                                              final PublicKey rentKey,
                                                                              final PublicKey memoProgramKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createWrite(poolKey),
      createWrite(protocolPositionOldKey),
      createWrite(protocolPositionNewKey),
      createWrite(positionKey),
      createWrite(positionTokenAccountKey),
      createWrite(positionMintKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(newTickArrayLowerKey),
      createWrite(newTickArrayUpperKey),
      createWrite(tokenVault0Key),
      createWrite(tokenVault1Key),
      createWrite(borrowerTa0Key),
      createWrite(borrowerTa1Key),
      createWrite(loanTa0Key),
      createWrite(loanTa1Key),
      createWrite(newPositionKey),
      createWritableSigner(newPositionMintKey),
      createWrite(newPositionTokenAccountKey),
      createRead(mint0Key),
      createRead(mint1Key),
      createRead(raydiumProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(rentKey),
      createRead(memoProgramKey)
    );
  }

  /// 5.3.7 raydium transfer position
  ///
  public static Instruction manageCollateralTransferRaydiumPosition(final AccountMeta invokedLoopscaleProgramMeta,
                                                                    final PublicKey bsAuthKey,
                                                                    final PublicKey payerKey,
                                                                    final PublicKey borrowerKey,
                                                                    final PublicKey loanKey,
                                                                    final PublicKey poolKey,
                                                                    final PublicKey protocolPositionOldKey,
                                                                    final PublicKey protocolPositionNewKey,
                                                                    final PublicKey positionKey,
                                                                    final PublicKey positionTokenAccountKey,
                                                                    final PublicKey positionMintKey,
                                                                    final PublicKey tickArrayLowerKey,
                                                                    final PublicKey tickArrayUpperKey,
                                                                    final PublicKey newTickArrayLowerKey,
                                                                    final PublicKey newTickArrayUpperKey,
                                                                    final PublicKey tokenVault0Key,
                                                                    final PublicKey tokenVault1Key,
                                                                    final PublicKey borrowerTa0Key,
                                                                    final PublicKey borrowerTa1Key,
                                                                    final PublicKey loanTa0Key,
                                                                    final PublicKey loanTa1Key,
                                                                    final PublicKey newPositionKey,
                                                                    final PublicKey newPositionMintKey,
                                                                    final PublicKey newPositionTokenAccountKey,
                                                                    final PublicKey mint0Key,
                                                                    final PublicKey mint1Key,
                                                                    final PublicKey raydiumProgramKey,
                                                                    final PublicKey tokenProgramKey,
                                                                    final PublicKey token2022ProgramKey,
                                                                    final PublicKey associatedTokenProgramKey,
                                                                    final PublicKey systemProgramKey,
                                                                    final PublicKey rentKey,
                                                                    final PublicKey memoProgramKey,
                                                                    final TransferPositionParams params) {
    final var keys = manageCollateralTransferRaydiumPositionKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      poolKey,
      protocolPositionOldKey,
      protocolPositionNewKey,
      positionKey,
      positionTokenAccountKey,
      positionMintKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      newTickArrayLowerKey,
      newTickArrayUpperKey,
      tokenVault0Key,
      tokenVault1Key,
      borrowerTa0Key,
      borrowerTa1Key,
      loanTa0Key,
      loanTa1Key,
      newPositionKey,
      newPositionMintKey,
      newPositionTokenAccountKey,
      mint0Key,
      mint1Key,
      raydiumProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      rentKey,
      memoProgramKey
    );
    return manageCollateralTransferRaydiumPosition(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.3.7 raydium transfer position
  ///
  public static Instruction manageCollateralTransferRaydiumPosition(final AccountMeta invokedLoopscaleProgramMeta,
                                                                    final List<AccountMeta> keys,
                                                                    final TransferPositionParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = MANAGE_COLLATERAL_TRANSFER_RAYDIUM_POSITION_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralTransferRaydiumPositionIxData(Discriminator discriminator, TransferPositionParams params) implements SerDe {  

    public static ManageCollateralTransferRaydiumPositionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ManageCollateralTransferRaydiumPositionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TransferPositionParams.read(_data, i);
      return new ManageCollateralTransferRaydiumPositionIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator MANAGE_COLLATERAL_WITHDRAW_ORCA_LIQUIDITY_DISCRIMINATOR = toDiscriminator(219, 89, 35, 158, 224, 12, 200, 90);

  /// 5.3.3 orca withdraw liquidity
  ///
  public static List<AccountMeta> manageCollateralWithdrawOrcaLiquidityKeys(final PublicKey bsAuthKey,
                                                                            final PublicKey payerKey,
                                                                            final PublicKey borrowerKey,
                                                                            final PublicKey loanKey,
                                                                            final PublicKey whirlpoolKey,
                                                                            final PublicKey positionKey,
                                                                            final PublicKey positionTokenAccountKey,
                                                                            final PublicKey tickArrayLowerKey,
                                                                            final PublicKey tickArrayUpperKey,
                                                                            final PublicKey tokenVaultAKey,
                                                                            final PublicKey tokenVaultBKey,
                                                                            final PublicKey borrowerTaAKey,
                                                                            final PublicKey borrowerTaBKey,
                                                                            final PublicKey loanTaAKey,
                                                                            final PublicKey loanTaBKey,
                                                                            final PublicKey mintAKey,
                                                                            final PublicKey mintBKey,
                                                                            final PublicKey whirlpoolProgramKey,
                                                                            final PublicKey tokenProgramKey,
                                                                            final PublicKey token2022ProgramKey,
                                                                            final PublicKey associatedTokenProgramKey,
                                                                            final PublicKey systemProgramKey,
                                                                            final PublicKey memoProgramKey,
                                                                            final PublicKey eventAuthorityKey,
                                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createRead(loanKey),
      createWrite(whirlpoolKey),
      createWrite(positionKey),
      createRead(positionTokenAccountKey),
      createWrite(tickArrayLowerKey),
      createWrite(tickArrayUpperKey),
      createWrite(tokenVaultAKey),
      createWrite(tokenVaultBKey),
      createWrite(borrowerTaAKey),
      createWrite(borrowerTaBKey),
      createWrite(loanTaAKey),
      createWrite(loanTaBKey),
      createRead(mintAKey),
      createRead(mintBKey),
      createRead(whirlpoolProgramKey),
      createRead(tokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.3.3 orca withdraw liquidity
  ///
  public static Instruction manageCollateralWithdrawOrcaLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                  final PublicKey bsAuthKey,
                                                                  final PublicKey payerKey,
                                                                  final PublicKey borrowerKey,
                                                                  final PublicKey loanKey,
                                                                  final PublicKey whirlpoolKey,
                                                                  final PublicKey positionKey,
                                                                  final PublicKey positionTokenAccountKey,
                                                                  final PublicKey tickArrayLowerKey,
                                                                  final PublicKey tickArrayUpperKey,
                                                                  final PublicKey tokenVaultAKey,
                                                                  final PublicKey tokenVaultBKey,
                                                                  final PublicKey borrowerTaAKey,
                                                                  final PublicKey borrowerTaBKey,
                                                                  final PublicKey loanTaAKey,
                                                                  final PublicKey loanTaBKey,
                                                                  final PublicKey mintAKey,
                                                                  final PublicKey mintBKey,
                                                                  final PublicKey whirlpoolProgramKey,
                                                                  final PublicKey tokenProgramKey,
                                                                  final PublicKey token2022ProgramKey,
                                                                  final PublicKey associatedTokenProgramKey,
                                                                  final PublicKey systemProgramKey,
                                                                  final PublicKey memoProgramKey,
                                                                  final PublicKey eventAuthorityKey,
                                                                  final PublicKey programKey,
                                                                  final ManageLiquidityParams params) {
    final var keys = manageCollateralWithdrawOrcaLiquidityKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      whirlpoolKey,
      positionKey,
      positionTokenAccountKey,
      tickArrayLowerKey,
      tickArrayUpperKey,
      tokenVaultAKey,
      tokenVaultBKey,
      borrowerTaAKey,
      borrowerTaBKey,
      loanTaAKey,
      loanTaBKey,
      mintAKey,
      mintBKey,
      whirlpoolProgramKey,
      tokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return manageCollateralWithdrawOrcaLiquidity(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.3.3 orca withdraw liquidity
  ///
  public static Instruction manageCollateralWithdrawOrcaLiquidity(final AccountMeta invokedLoopscaleProgramMeta,
                                                                  final List<AccountMeta> keys,
                                                                  final ManageLiquidityParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = MANAGE_COLLATERAL_WITHDRAW_ORCA_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record ManageCollateralWithdrawOrcaLiquidityIxData(Discriminator discriminator, ManageLiquidityParams params) implements SerDe {  

    public static ManageCollateralWithdrawOrcaLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static ManageCollateralWithdrawOrcaLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ManageLiquidityParams.read(_data, i);
      return new ManageCollateralWithdrawOrcaLiquidityIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator MIGRATE_MARKET_INFO_ALLOCATION_DISCRIMINATOR = toDiscriminator(112, 172, 64, 213, 227, 1, 86, 2);

  public static List<AccountMeta> migrateMarketInfoAllocationKeys(final PublicKey bsAuthKey,
                                                                  final PublicKey marketInfoKey,
                                                                  final PublicKey eventAuthorityKey,
                                                                  final PublicKey programKey) {
    return List.of(
      createWritableSigner(bsAuthKey),
      createWrite(marketInfoKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction migrateMarketInfoAllocation(final AccountMeta invokedLoopscaleProgramMeta,
                                                        final PublicKey bsAuthKey,
                                                        final PublicKey marketInfoKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey,
                                                        final CollateralAllocationParam[] allocations) {
    final var keys = migrateMarketInfoAllocationKeys(
      bsAuthKey,
      marketInfoKey,
      eventAuthorityKey,
      programKey
    );
    return migrateMarketInfoAllocation(invokedLoopscaleProgramMeta, keys, allocations);
  }

  public static Instruction migrateMarketInfoAllocation(final AccountMeta invokedLoopscaleProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final CollateralAllocationParam[] allocations) {
    final byte[] _data = new byte[8 + SerDeUtil.lenVector(4, allocations)];
    int i = MIGRATE_MARKET_INFO_ALLOCATION_DISCRIMINATOR.write(_data, 0);
    SerDeUtil.writeVector(4, allocations, _data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record MigrateMarketInfoAllocationIxData(Discriminator discriminator, CollateralAllocationParam[] allocations) implements SerDe {  

    public static MigrateMarketInfoAllocationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ALLOCATIONS_OFFSET = 8;

    public static MigrateMarketInfoAllocationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var allocations = SerDeUtil.readVector(4, CollateralAllocationParam.class, CollateralAllocationParam::read, _data, i);
      return new MigrateMarketInfoAllocationIxData(discriminator, allocations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += SerDeUtil.writeVector(4, allocations, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + SerDeUtil.lenVector(4, allocations);
    }
  }

  public static final Discriminator REFINANCE_LEDGER_DISCRIMINATOR = toDiscriminator(103, 41, 134, 43, 140, 152, 253, 74);

  /// 3. refinance ledger
  ///
  public static List<AccountMeta> refinanceLedgerKeys(final PublicKey bsAuthKey,
                                                      final PublicKey payerKey,
                                                      final PublicKey loanKey,
                                                      final PublicKey oldStrategyKey,
                                                      final PublicKey newStrategyKey,
                                                      final PublicKey oldStrategyTaKey,
                                                      final PublicKey newStrategyTaKey,
                                                      final PublicKey oldStrategyMarketInformationKey,
                                                      final PublicKey newStrategyMarketInformationKey,
                                                      final PublicKey principalMintKey,
                                                      final PublicKey tokenProgramKey,
                                                      final PublicKey associatedTokenProgramKey,
                                                      final PublicKey systemProgramKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWrite(loanKey),
      createWrite(oldStrategyKey),
      createWrite(newStrategyKey),
      createWrite(oldStrategyTaKey),
      createWrite(newStrategyTaKey),
      createWrite(oldStrategyMarketInformationKey),
      createWrite(newStrategyMarketInformationKey),
      createRead(principalMintKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 3. refinance ledger
  ///
  public static Instruction refinanceLedger(final AccountMeta invokedLoopscaleProgramMeta,
                                            final PublicKey bsAuthKey,
                                            final PublicKey payerKey,
                                            final PublicKey loanKey,
                                            final PublicKey oldStrategyKey,
                                            final PublicKey newStrategyKey,
                                            final PublicKey oldStrategyTaKey,
                                            final PublicKey newStrategyTaKey,
                                            final PublicKey oldStrategyMarketInformationKey,
                                            final PublicKey newStrategyMarketInformationKey,
                                            final PublicKey principalMintKey,
                                            final PublicKey tokenProgramKey,
                                            final PublicKey associatedTokenProgramKey,
                                            final PublicKey systemProgramKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final RefinanceLedgerParams params) {
    final var keys = refinanceLedgerKeys(
      bsAuthKey,
      payerKey,
      loanKey,
      oldStrategyKey,
      newStrategyKey,
      oldStrategyTaKey,
      newStrategyTaKey,
      oldStrategyMarketInformationKey,
      newStrategyMarketInformationKey,
      principalMintKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return refinanceLedger(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 3. refinance ledger
  ///
  public static Instruction refinanceLedger(final AccountMeta invokedLoopscaleProgramMeta,
                                            final List<AccountMeta> keys,
                                            final RefinanceLedgerParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = REFINANCE_LEDGER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record RefinanceLedgerIxData(Discriminator discriminator, RefinanceLedgerParams params) implements SerDe {  

    public static RefinanceLedgerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static RefinanceLedgerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = RefinanceLedgerParams.read(_data, i);
      return new RefinanceLedgerIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator REPAY_PRINCIPAL_DISCRIMINATOR = toDiscriminator(229, 67, 83, 65, 77, 84, 80, 141);

  /// 6.2. repay principal
  ///
  public static List<AccountMeta> repayPrincipalKeys(final PublicKey bsAuthKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey borrowerKey,
                                                     final PublicKey loanKey,
                                                     final PublicKey strategyKey,
                                                     final PublicKey marketInformationKey,
                                                     final PublicKey principalMintKey,
                                                     final PublicKey borrowerTaKey,
                                                     final PublicKey strategyTaKey,
                                                     final PublicKey associatedTokenProgramKey,
                                                     final PublicKey tokenProgramKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createWrite(strategyKey),
      createWrite(marketInformationKey),
      createRead(principalMintKey),
      createWrite(borrowerTaKey),
      createWrite(strategyTaKey),
      createRead(associatedTokenProgramKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 6.2. repay principal
  ///
  public static Instruction repayPrincipal(final AccountMeta invokedLoopscaleProgramMeta,
                                           final PublicKey bsAuthKey,
                                           final PublicKey payerKey,
                                           final PublicKey borrowerKey,
                                           final PublicKey loanKey,
                                           final PublicKey strategyKey,
                                           final PublicKey marketInformationKey,
                                           final PublicKey principalMintKey,
                                           final PublicKey borrowerTaKey,
                                           final PublicKey strategyTaKey,
                                           final PublicKey associatedTokenProgramKey,
                                           final PublicKey tokenProgramKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final RepayPrincipalParams params) {
    final var keys = repayPrincipalKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      strategyKey,
      marketInformationKey,
      principalMintKey,
      borrowerTaKey,
      strategyTaKey,
      associatedTokenProgramKey,
      tokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return repayPrincipal(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 6.2. repay principal
  ///
  public static Instruction repayPrincipal(final AccountMeta invokedLoopscaleProgramMeta,
                                           final List<AccountMeta> keys,
                                           final RepayPrincipalParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = REPAY_PRINCIPAL_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record RepayPrincipalIxData(Discriminator discriminator, RepayPrincipalParams params) implements SerDe {  

    public static RepayPrincipalIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static final int PARAMS_OFFSET = 8;

    public static RepayPrincipalIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = RepayPrincipalParams.read(_data, i);
      return new RepayPrincipalIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SELL_LEDGER_DISCRIMINATOR = toDiscriminator(55, 17, 153, 148, 120, 242, 80, 5);

  /// 4. sell ledger
  ///
  public static List<AccountMeta> sellLedgerKeys(final AccountMeta invokedLoopscaleProgramMeta,
                                                 final PublicKey bsAuthKey,
                                                 final PublicKey payerKey,
                                                 final PublicKey lenderAuthKey,
                                                 final PublicKey loanKey,
                                                 final PublicKey newStrategyTaKey,
                                                 final PublicKey lenderAuthTaKey,
                                                 final PublicKey oldStrategyKey,
                                                 final PublicKey newStrategyKey,
                                                 final PublicKey oldStrategyMarketInformationKey,
                                                 final PublicKey newStrategyMarketInformationKey,
                                                 final PublicKey principalMintKey,
                                                 final PublicKey tokenProgramKey,
                                                 final PublicKey associatedTokenProgramKey,
                                                 final PublicKey systemProgramKey,
                                                 final PublicKey vaultKey,
                                                 final PublicKey oldStrategyTaKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(lenderAuthKey),
      createWrite(loanKey),
      createWrite(newStrategyTaKey),
      createWrite(lenderAuthTaKey),
      createWrite(oldStrategyKey),
      createWrite(newStrategyKey),
      createWrite(oldStrategyMarketInformationKey),
      createWrite(newStrategyMarketInformationKey),
      createRead(principalMintKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createWrite(requireNonNullElse(vaultKey, invokedLoopscaleProgramMeta.publicKey())),
      createWrite(requireNonNullElse(oldStrategyTaKey, invokedLoopscaleProgramMeta.publicKey())),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 4. sell ledger
  ///
  public static Instruction sellLedger(final AccountMeta invokedLoopscaleProgramMeta,
                                       final PublicKey bsAuthKey,
                                       final PublicKey payerKey,
                                       final PublicKey lenderAuthKey,
                                       final PublicKey loanKey,
                                       final PublicKey newStrategyTaKey,
                                       final PublicKey lenderAuthTaKey,
                                       final PublicKey oldStrategyKey,
                                       final PublicKey newStrategyKey,
                                       final PublicKey oldStrategyMarketInformationKey,
                                       final PublicKey newStrategyMarketInformationKey,
                                       final PublicKey principalMintKey,
                                       final PublicKey tokenProgramKey,
                                       final PublicKey associatedTokenProgramKey,
                                       final PublicKey systemProgramKey,
                                       final PublicKey vaultKey,
                                       final PublicKey oldStrategyTaKey,
                                       final PublicKey eventAuthorityKey,
                                       final PublicKey programKey,
                                       final SellLedgerParams params) {
    final var keys = sellLedgerKeys(
      invokedLoopscaleProgramMeta,
      bsAuthKey,
      payerKey,
      lenderAuthKey,
      loanKey,
      newStrategyTaKey,
      lenderAuthTaKey,
      oldStrategyKey,
      newStrategyKey,
      oldStrategyMarketInformationKey,
      newStrategyMarketInformationKey,
      principalMintKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      vaultKey,
      oldStrategyTaKey,
      eventAuthorityKey,
      programKey
    );
    return sellLedger(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 4. sell ledger
  ///
  public static Instruction sellLedger(final AccountMeta invokedLoopscaleProgramMeta,
                                       final List<AccountMeta> keys,
                                       final SellLedgerParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SELL_LEDGER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record SellLedgerIxData(Discriminator discriminator, SellLedgerParams params) implements SerDe {  

    public static SellLedgerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static SellLedgerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = SellLedgerParams.read(_data, i);
      return new SellLedgerIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator STAKE_USER_VAULT_LP_DISCRIMINATOR = toDiscriminator(114, 132, 194, 209, 208, 149, 43, 136);

  /// 9.1.1.3 vault user stake
  ///
  public static List<AccountMeta> stakeUserVaultLpKeys(final PublicKey bsAuthKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey userKey,
                                                       final PublicKey nonceKey,
                                                       final PublicKey vaultKey,
                                                       final PublicKey vaultStakeKey,
                                                       final PublicKey lpMintKey,
                                                       final PublicKey userLpTaKey,
                                                       final PublicKey vaultStakeLpTaKey,
                                                       final PublicKey vaultRewardsInfoKey,
                                                       final PublicKey userRewardsInfoKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey associatedTokenProgramKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(userKey),
      createReadOnlySigner(nonceKey),
      createWrite(vaultKey),
      createWrite(vaultStakeKey),
      createRead(lpMintKey),
      createWrite(userLpTaKey),
      createWrite(vaultStakeLpTaKey),
      createWrite(vaultRewardsInfoKey),
      createWrite(userRewardsInfoKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.1.1.3 vault user stake
  ///
  public static Instruction stakeUserVaultLp(final AccountMeta invokedLoopscaleProgramMeta,
                                             final PublicKey bsAuthKey,
                                             final PublicKey payerKey,
                                             final PublicKey userKey,
                                             final PublicKey nonceKey,
                                             final PublicKey vaultKey,
                                             final PublicKey vaultStakeKey,
                                             final PublicKey lpMintKey,
                                             final PublicKey userLpTaKey,
                                             final PublicKey vaultStakeLpTaKey,
                                             final PublicKey vaultRewardsInfoKey,
                                             final PublicKey userRewardsInfoKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey associatedTokenProgramKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey,
                                             final VaultStakeParams params) {
    final var keys = stakeUserVaultLpKeys(
      bsAuthKey,
      payerKey,
      userKey,
      nonceKey,
      vaultKey,
      vaultStakeKey,
      lpMintKey,
      userLpTaKey,
      vaultStakeLpTaKey,
      vaultRewardsInfoKey,
      userRewardsInfoKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return stakeUserVaultLp(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.1.1.3 vault user stake
  ///
  public static Instruction stakeUserVaultLp(final AccountMeta invokedLoopscaleProgramMeta,
                                             final List<AccountMeta> keys,
                                             final VaultStakeParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = STAKE_USER_VAULT_LP_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record StakeUserVaultLpIxData(Discriminator discriminator, VaultStakeParams params) implements SerDe {  

    public static StakeUserVaultLpIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static StakeUserVaultLpIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = VaultStakeParams.read(_data, i);
      return new StakeUserVaultLpIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator UNLOCK_LOAN_DISCRIMINATOR = toDiscriminator(121, 226, 178, 98, 215, 209, 240, 38);

  /// 1.3 unlock loan
  ///
  public static List<AccountMeta> unlockLoanKeys(final PublicKey borrowerKey,
                                                 final PublicKey loanKey,
                                                 final PublicKey payerKey,
                                                 final PublicKey bsAuthKey,
                                                 final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(bsAuthKey),
      createRead(systemProgramKey)
    );
  }

  /// 1.3 unlock loan
  ///
  public static Instruction unlockLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                       final PublicKey borrowerKey,
                                       final PublicKey loanKey,
                                       final PublicKey payerKey,
                                       final PublicKey bsAuthKey,
                                       final PublicKey systemProgramKey,
                                       final LoanUnlockParams params) {
    final var keys = unlockLoanKeys(
      borrowerKey,
      loanKey,
      payerKey,
      bsAuthKey,
      systemProgramKey
    );
    return unlockLoan(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 1.3 unlock loan
  ///
  public static Instruction unlockLoan(final AccountMeta invokedLoopscaleProgramMeta,
                                       final List<AccountMeta> keys,
                                       final LoanUnlockParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UNLOCK_LOAN_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UnlockLoanIxData(Discriminator discriminator, LoanUnlockParams params) implements SerDe {  

    public static UnlockLoanIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UnlockLoanIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LoanUnlockParams.read(_data, i);
      return new UnlockLoanIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator UNSTAKE_USER_VAULT_LP_DISCRIMINATOR = toDiscriminator(83, 78, 230, 123, 226, 40, 158, 97);

  /// 9.1.1.4 vault user unstake
  ///
  public static List<AccountMeta> unstakeUserVaultLpKeys(final PublicKey bsAuthKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey userKey,
                                                         final PublicKey vaultKey,
                                                         final PublicKey lpMintKey,
                                                         final PublicKey vaultStakeKey,
                                                         final PublicKey userLpTaKey,
                                                         final PublicKey vaultStakeLpTaKey,
                                                         final PublicKey vaultRewardsInfoKey,
                                                         final PublicKey userRewardsInfoKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey associatedTokenProgramKey,
                                                         final PublicKey systemProgramKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(userKey),
      createWrite(vaultKey),
      createWrite(lpMintKey),
      createWrite(vaultStakeKey),
      createWrite(userLpTaKey),
      createWrite(vaultStakeLpTaKey),
      createWrite(vaultRewardsInfoKey),
      createWrite(userRewardsInfoKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.1.1.4 vault user unstake
  ///
  public static Instruction unstakeUserVaultLp(final AccountMeta invokedLoopscaleProgramMeta,
                                               final PublicKey bsAuthKey,
                                               final PublicKey payerKey,
                                               final PublicKey userKey,
                                               final PublicKey vaultKey,
                                               final PublicKey lpMintKey,
                                               final PublicKey vaultStakeKey,
                                               final PublicKey userLpTaKey,
                                               final PublicKey vaultStakeLpTaKey,
                                               final PublicKey vaultRewardsInfoKey,
                                               final PublicKey userRewardsInfoKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey associatedTokenProgramKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey,
                                               final VaultUnstakeParams params) {
    final var keys = unstakeUserVaultLpKeys(
      bsAuthKey,
      payerKey,
      userKey,
      vaultKey,
      lpMintKey,
      vaultStakeKey,
      userLpTaKey,
      vaultStakeLpTaKey,
      vaultRewardsInfoKey,
      userRewardsInfoKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return unstakeUserVaultLp(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.1.1.4 vault user unstake
  ///
  public static Instruction unstakeUserVaultLp(final AccountMeta invokedLoopscaleProgramMeta,
                                               final List<AccountMeta> keys,
                                               final VaultUnstakeParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UNSTAKE_USER_VAULT_LP_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UnstakeUserVaultLpIxData(Discriminator discriminator, VaultUnstakeParams params) implements SerDe {  

    public static UnstakeUserVaultLpIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 17;

    public static final int PARAMS_OFFSET = 8;

    public static UnstakeUserVaultLpIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = VaultUnstakeParams.read(_data, i);
      return new UnstakeUserVaultLpIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_MARKET_INFORMATION_DISCRIMINATOR = toDiscriminator(186, 195, 82, 187, 196, 199, 135, 158);

  /// 7.2 update market information account
  ///
  public static List<AccountMeta> updateMarketInformationKeys(final PublicKey authorityKey,
                                                              final PublicKey marketInformationKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createWrite(marketInformationKey)
    );
  }

  /// 7.2 update market information account
  ///
  public static Instruction updateMarketInformation(final AccountMeta invokedLoopscaleProgramMeta,
                                                    final PublicKey authorityKey,
                                                    final PublicKey marketInformationKey,
                                                    final UpdateAssetDataParams[] assetUpdateParams,
                                                    final UpdateCapsParams updateCapParams) {
    final var keys = updateMarketInformationKeys(
      authorityKey,
      marketInformationKey
    );
    return updateMarketInformation(invokedLoopscaleProgramMeta, keys, assetUpdateParams, updateCapParams);
  }

  /// 7.2 update market information account
  ///
  public static Instruction updateMarketInformation(final AccountMeta invokedLoopscaleProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final UpdateAssetDataParams[] assetUpdateParams,
                                                    final UpdateCapsParams updateCapParams) {
    final byte[] _data = new byte[
    8
    + (assetUpdateParams == null || assetUpdateParams.length == 0 ? 1 : (1 + SerDeUtil.lenVector(4, assetUpdateParams)))
    + (updateCapParams == null ? 1 : (1 + updateCapParams.l()))
    ];
    int i = UPDATE_MARKET_INFORMATION_DISCRIMINATOR.write(_data, 0);
    if (assetUpdateParams == null || assetUpdateParams.length == 0) {
      _data[i++] = 0;
    } else {
      _data[i++] = 1;
      i += SerDeUtil.writeVector(4, assetUpdateParams, _data, i);
    }
    SerDeUtil.writeOptional(1, updateCapParams, _data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UpdateMarketInformationIxData(Discriminator discriminator, UpdateAssetDataParams[] assetUpdateParams, UpdateCapsParams updateCapParams) implements SerDe {  

    public static UpdateMarketInformationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ASSET_UPDATE_PARAMS_OFFSET = 9;

    public static UpdateMarketInformationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final UpdateAssetDataParams[] assetUpdateParams;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        assetUpdateParams = null;
        ++i;
      } else {
        ++i;
        assetUpdateParams = SerDeUtil.readVector(4, UpdateAssetDataParams.class, UpdateAssetDataParams::read, _data, i);
        i += SerDeUtil.lenVector(4, assetUpdateParams);
      }
      final UpdateCapsParams updateCapParams;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        updateCapParams = null;
      } else {
        ++i;
        updateCapParams = UpdateCapsParams.read(_data, i);
      }
      return new UpdateMarketInformationIxData(discriminator, assetUpdateParams, updateCapParams);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      if (assetUpdateParams == null || assetUpdateParams.length == 0) {
        _data[i++] = 0;
      } else {
        _data[i++] = 1;
        i += SerDeUtil.writeVector(4, assetUpdateParams, _data, i);
      }
      i += SerDeUtil.writeOptional(1, updateCapParams, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (assetUpdateParams == null || assetUpdateParams.length == 0 ? 1 : (1 + SerDeUtil.lenVector(4, assetUpdateParams))) + (updateCapParams == null ? 1 : (1 + updateCapParams.l()));
    }
  }

  public static final Discriminator UPDATE_REWARDS_SCHEDULE_DISCRIMINATOR = toDiscriminator(226, 238, 15, 86, 66, 219, 13, 232);

  public static List<AccountMeta> updateRewardsScheduleKeys(final PublicKey bsAuthKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey managerKey,
                                                            final PublicKey rewardsSourceKey,
                                                            final PublicKey vaultKey,
                                                            final PublicKey vaultRewardsInfoKey,
                                                            final PublicKey vaultRewardsMintKey,
                                                            final PublicKey vaultRewardsTaKey,
                                                            final PublicKey rewardsSourceTaKey,
                                                            final PublicKey tokenProgramKey,
                                                            final PublicKey associatedTokenProgramKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(managerKey),
      createReadOnlySigner(rewardsSourceKey),
      createWrite(vaultKey),
      createWrite(vaultRewardsInfoKey),
      createRead(vaultRewardsMintKey),
      createWrite(vaultRewardsTaKey),
      createWrite(rewardsSourceTaKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updateRewardsSchedule(final AccountMeta invokedLoopscaleProgramMeta,
                                                  final PublicKey bsAuthKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey managerKey,
                                                  final PublicKey rewardsSourceKey,
                                                  final PublicKey vaultKey,
                                                  final PublicKey vaultRewardsInfoKey,
                                                  final PublicKey vaultRewardsMintKey,
                                                  final PublicKey vaultRewardsTaKey,
                                                  final PublicKey rewardsSourceTaKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey associatedTokenProgramKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final UpdateRewardsScheduleParams params) {
    final var keys = updateRewardsScheduleKeys(
      bsAuthKey,
      payerKey,
      managerKey,
      rewardsSourceKey,
      vaultKey,
      vaultRewardsInfoKey,
      vaultRewardsMintKey,
      vaultRewardsTaKey,
      rewardsSourceTaKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return updateRewardsSchedule(invokedLoopscaleProgramMeta, keys, params);
  }

  public static Instruction updateRewardsSchedule(final AccountMeta invokedLoopscaleProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final UpdateRewardsScheduleParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_REWARDS_SCHEDULE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UpdateRewardsScheduleIxData(Discriminator discriminator, UpdateRewardsScheduleParams params) implements SerDe {  

    public static UpdateRewardsScheduleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UpdateRewardsScheduleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateRewardsScheduleParams.read(_data, i);
      return new UpdateRewardsScheduleIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator UPDATE_STRATEGY_DISCRIMINATOR = toDiscriminator(16, 76, 138, 179, 171, 112, 196, 21);

  /// 8.3 update strategy
  ///
  public static List<AccountMeta> updateStrategyKeys(final PublicKey bsAuthKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey lenderKey,
                                                     final PublicKey strategyKey,
                                                     final PublicKey principalMintKey,
                                                     final PublicKey strategyTaKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey associatedTokenProgramKey,
                                                     final PublicKey tokenProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(lenderKey),
      createWrite(strategyKey),
      createRead(principalMintKey),
      createWrite(strategyTaKey),
      createRead(systemProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 8.3 update strategy
  ///
  public static Instruction updateStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                           final PublicKey bsAuthKey,
                                           final PublicKey payerKey,
                                           final PublicKey lenderKey,
                                           final PublicKey strategyKey,
                                           final PublicKey principalMintKey,
                                           final PublicKey strategyTaKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey associatedTokenProgramKey,
                                           final PublicKey tokenProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final MultiCollateralTermsUpdateParams[] collateralTerms,
                                           final UpdateStrategyParams params) {
    final var keys = updateStrategyKeys(
      bsAuthKey,
      payerKey,
      lenderKey,
      strategyKey,
      principalMintKey,
      strategyTaKey,
      systemProgramKey,
      associatedTokenProgramKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return updateStrategy(invokedLoopscaleProgramMeta, keys, collateralTerms, params);
  }

  /// 8.3 update strategy
  ///
  public static Instruction updateStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                           final List<AccountMeta> keys,
                                           final MultiCollateralTermsUpdateParams[] collateralTerms,
                                           final UpdateStrategyParams params) {
    final byte[] _data = new byte[
    8 + SerDeUtil.lenVector(4, collateralTerms)
    + (params == null ? 1 : (1 + params.l()))
    ];
    int i = UPDATE_STRATEGY_DISCRIMINATOR.write(_data, 0);
    i += SerDeUtil.writeVector(4, collateralTerms, _data, i);
    SerDeUtil.writeOptional(1, params, _data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UpdateStrategyIxData(Discriminator discriminator, MultiCollateralTermsUpdateParams[] collateralTerms, UpdateStrategyParams params) implements SerDe {  

    public static UpdateStrategyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int COLLATERAL_TERMS_OFFSET = 8;

    public static UpdateStrategyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralTerms = SerDeUtil.readVector(4, MultiCollateralTermsUpdateParams.class, MultiCollateralTermsUpdateParams::read, _data, i);
      i += SerDeUtil.lenVector(4, collateralTerms);
      final UpdateStrategyParams params;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        params = null;
      } else {
        ++i;
        params = UpdateStrategyParams.read(_data, i);
      }
      return new UpdateStrategyIxData(discriminator, collateralTerms, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += SerDeUtil.writeVector(4, collateralTerms, _data, i);
      i += SerDeUtil.writeOptional(1, params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + SerDeUtil.lenVector(4, collateralTerms) + (params == null ? 1 : (1 + params.l()));
    }
  }

  public static final Discriminator UPDATE_VAULT_DISCRIMINATOR = toDiscriminator(67, 229, 185, 188, 226, 11, 210, 60);

  /// 9.4 toggle vault deposits
  ///
  public static List<AccountMeta> updateVaultKeys(final PublicKey bsAuthKey,
                                                  final PublicKey managerKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey vaultKey,
                                                  final PublicKey marketInformationKey,
                                                  final PublicKey vaultRewardsInfoKey,
                                                  final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createReadOnlySigner(managerKey),
      createWritableSigner(payerKey),
      createWrite(vaultKey),
      createRead(marketInformationKey),
      createWrite(vaultRewardsInfoKey),
      createRead(systemProgramKey)
    );
  }

  /// 9.4 toggle vault deposits
  ///
  public static Instruction updateVault(final AccountMeta invokedLoopscaleProgramMeta,
                                        final PublicKey bsAuthKey,
                                        final PublicKey managerKey,
                                        final PublicKey payerKey,
                                        final PublicKey vaultKey,
                                        final PublicKey marketInformationKey,
                                        final PublicKey vaultRewardsInfoKey,
                                        final PublicKey systemProgramKey,
                                        final UpdateVaultParams params) {
    final var keys = updateVaultKeys(
      bsAuthKey,
      managerKey,
      payerKey,
      vaultKey,
      marketInformationKey,
      vaultRewardsInfoKey,
      systemProgramKey
    );
    return updateVault(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.4 toggle vault deposits
  ///
  public static Instruction updateVault(final AccountMeta invokedLoopscaleProgramMeta,
                                        final List<AccountMeta> keys,
                                        final UpdateVaultParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_VAULT_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UpdateVaultIxData(Discriminator discriminator, UpdateVaultParams params) implements SerDe {  

    public static UpdateVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static final int PARAMS_OFFSET = 8;

    public static UpdateVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateVaultParams.read(_data, i);
      return new UpdateVaultIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_WEIGHT_MATRIX_DISCRIMINATOR = toDiscriminator(252, 166, 37, 207, 154, 83, 187, 128);

  /// 10. update weight matrix
  ///
  public static List<AccountMeta> updateWeightMatrixKeys(final PublicKey bsAuthKey,
                                                         final PublicKey borrowerKey,
                                                         final PublicKey loanKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey)
    );
  }

  /// 10. update weight matrix
  ///
  public static Instruction updateWeightMatrix(final AccountMeta invokedLoopscaleProgramMeta,
                                               final PublicKey bsAuthKey,
                                               final PublicKey borrowerKey,
                                               final PublicKey loanKey,
                                               final UpdateWeightMatrixParams params) {
    final var keys = updateWeightMatrixKeys(
      bsAuthKey,
      borrowerKey,
      loanKey
    );
    return updateWeightMatrix(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 10. update weight matrix
  ///
  public static Instruction updateWeightMatrix(final AccountMeta invokedLoopscaleProgramMeta,
                                               final List<AccountMeta> keys,
                                               final UpdateWeightMatrixParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_WEIGHT_MATRIX_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record UpdateWeightMatrixIxData(Discriminator discriminator, UpdateWeightMatrixParams params) implements SerDe {  

    public static UpdateWeightMatrixIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static UpdateWeightMatrixIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateWeightMatrixParams.read(_data, i);
      return new UpdateWeightMatrixIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator WITHDRAW_COLLATERAL_DISCRIMINATOR = toDiscriminator(115, 135, 168, 106, 139, 214, 138, 150);

  /// 5.2. withdraw collateral
  ///
  public static List<AccountMeta> withdrawCollateralKeys(final PublicKey bsAuthKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey borrowerKey,
                                                         final PublicKey loanKey,
                                                         final PublicKey borrowerTaKey,
                                                         final PublicKey loanTaKey,
                                                         final PublicKey systemProgramKey,
                                                         final PublicKey assetMintKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey associatedTokenProgramKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createReadOnlySigner(borrowerKey),
      createWrite(loanKey),
      createWrite(borrowerTaKey),
      createWrite(loanTaKey),
      createRead(systemProgramKey),
      createRead(assetMintKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 5.2. withdraw collateral
  ///
  public static Instruction withdrawCollateral(final AccountMeta invokedLoopscaleProgramMeta,
                                               final PublicKey bsAuthKey,
                                               final PublicKey payerKey,
                                               final PublicKey borrowerKey,
                                               final PublicKey loanKey,
                                               final PublicKey borrowerTaKey,
                                               final PublicKey loanTaKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey assetMintKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey associatedTokenProgramKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey,
                                               final WithdrawCollateralParams params) {
    final var keys = withdrawCollateralKeys(
      bsAuthKey,
      payerKey,
      borrowerKey,
      loanKey,
      borrowerTaKey,
      loanTaKey,
      systemProgramKey,
      assetMintKey,
      tokenProgramKey,
      associatedTokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return withdrawCollateral(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 5.2. withdraw collateral
  ///
  public static Instruction withdrawCollateral(final AccountMeta invokedLoopscaleProgramMeta,
                                               final List<AccountMeta> keys,
                                               final WithdrawCollateralParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = WITHDRAW_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record WithdrawCollateralIxData(Discriminator discriminator, WithdrawCollateralParams params) implements SerDe {  

    public static WithdrawCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static WithdrawCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = WithdrawCollateralParams.read(_data, i);
      return new WithdrawCollateralIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator WITHDRAW_STRATEGY_DISCRIMINATOR = toDiscriminator(31, 45, 162, 5, 193, 217, 134, 188);

  /// 8.4 withdraw strategy
  ///
  public static List<AccountMeta> withdrawStrategyKeys(final PublicKey bsAuthKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey lenderKey,
                                                       final PublicKey strategyKey,
                                                       final PublicKey principalMintKey,
                                                       final PublicKey marketInformationKey,
                                                       final PublicKey lenderTaKey,
                                                       final PublicKey strategyTaKey,
                                                       final PublicKey associatedTokenProgramKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWritableSigner(lenderKey),
      createWrite(strategyKey),
      createRead(principalMintKey),
      createRead(marketInformationKey),
      createWrite(lenderTaKey),
      createWrite(strategyTaKey),
      createRead(associatedTokenProgramKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 8.4 withdraw strategy
  ///
  public static Instruction withdrawStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                             final PublicKey bsAuthKey,
                                             final PublicKey payerKey,
                                             final PublicKey lenderKey,
                                             final PublicKey strategyKey,
                                             final PublicKey principalMintKey,
                                             final PublicKey marketInformationKey,
                                             final PublicKey lenderTaKey,
                                             final PublicKey strategyTaKey,
                                             final PublicKey associatedTokenProgramKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey,
                                             final long amount,
                                             final boolean withdrawAll) {
    final var keys = withdrawStrategyKeys(
      bsAuthKey,
      payerKey,
      lenderKey,
      strategyKey,
      principalMintKey,
      marketInformationKey,
      lenderTaKey,
      strategyTaKey,
      associatedTokenProgramKey,
      tokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return withdrawStrategy(invokedLoopscaleProgramMeta, keys, amount, withdrawAll);
  }

  /// 8.4 withdraw strategy
  ///
  public static Instruction withdrawStrategy(final AccountMeta invokedLoopscaleProgramMeta,
                                             final List<AccountMeta> keys,
                                             final long amount,
                                             final boolean withdrawAll) {
    final byte[] _data = new byte[17];
    int i = WITHDRAW_STRATEGY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) (withdrawAll ? 1 : 0);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record WithdrawStrategyIxData(Discriminator discriminator, long amount, boolean withdrawAll) implements SerDe {  

    public static WithdrawStrategyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 17;

    public static final int AMOUNT_OFFSET = 8;
    public static final int WITHDRAW_ALL_OFFSET = 16;

    public static WithdrawStrategyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var withdrawAll = _data[i] == 1;
      return new WithdrawStrategyIxData(discriminator, amount, withdrawAll);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) (withdrawAll ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_USER_VAULT_DISCRIMINATOR = toDiscriminator(9, 80, 134, 138, 212, 20, 61, 42);

  /// 9.1.1.2 vault user withdraw
  ///
  public static List<AccountMeta> withdrawUserVaultKeys(final PublicKey bsAuthKey,
                                                        final PublicKey payerKey,
                                                        final PublicKey userKey,
                                                        final PublicKey vaultKey,
                                                        final PublicKey strategyKey,
                                                        final PublicKey marketInformationKey,
                                                        final PublicKey lpMintKey,
                                                        final PublicKey userLpTaKey,
                                                        final PublicKey userPrincipalTaKey,
                                                        final PublicKey strategyPrincipalTaKey,
                                                        final PublicKey principalMintKey,
                                                        final PublicKey principalTokenProgramKey,
                                                        final PublicKey token2022ProgramKey,
                                                        final PublicKey associatedTokenProgramKey,
                                                        final PublicKey systemProgramKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(bsAuthKey),
      createWritableSigner(payerKey),
      createWritableSigner(userKey),
      createWrite(vaultKey),
      createWrite(strategyKey),
      createRead(marketInformationKey),
      createWrite(lpMintKey),
      createWrite(userLpTaKey),
      createWrite(userPrincipalTaKey),
      createWrite(strategyPrincipalTaKey),
      createRead(principalMintKey),
      createRead(principalTokenProgramKey),
      createRead(token2022ProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(systemProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// 9.1.1.2 vault user withdraw
  ///
  public static Instruction withdrawUserVault(final AccountMeta invokedLoopscaleProgramMeta,
                                              final PublicKey bsAuthKey,
                                              final PublicKey payerKey,
                                              final PublicKey userKey,
                                              final PublicKey vaultKey,
                                              final PublicKey strategyKey,
                                              final PublicKey marketInformationKey,
                                              final PublicKey lpMintKey,
                                              final PublicKey userLpTaKey,
                                              final PublicKey userPrincipalTaKey,
                                              final PublicKey strategyPrincipalTaKey,
                                              final PublicKey principalMintKey,
                                              final PublicKey principalTokenProgramKey,
                                              final PublicKey token2022ProgramKey,
                                              final PublicKey associatedTokenProgramKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey,
                                              final LpParams params) {
    final var keys = withdrawUserVaultKeys(
      bsAuthKey,
      payerKey,
      userKey,
      vaultKey,
      strategyKey,
      marketInformationKey,
      lpMintKey,
      userLpTaKey,
      userPrincipalTaKey,
      strategyPrincipalTaKey,
      principalMintKey,
      principalTokenProgramKey,
      token2022ProgramKey,
      associatedTokenProgramKey,
      systemProgramKey,
      eventAuthorityKey,
      programKey
    );
    return withdrawUserVault(invokedLoopscaleProgramMeta, keys, params);
  }

  /// 9.1.1.2 vault user withdraw
  ///
  public static Instruction withdrawUserVault(final AccountMeta invokedLoopscaleProgramMeta,
                                              final List<AccountMeta> keys,
                                              final LpParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = WITHDRAW_USER_VAULT_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLoopscaleProgramMeta, keys, _data);
  }

  public record WithdrawUserVaultIxData(Discriminator discriminator, LpParams params) implements SerDe {  

    public static WithdrawUserVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int PARAMS_OFFSET = 8;

    public static WithdrawUserVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = LpParams.read(_data, i);
      return new WithdrawUserVaultIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  private LoopscaleProgram() {
  }
}
