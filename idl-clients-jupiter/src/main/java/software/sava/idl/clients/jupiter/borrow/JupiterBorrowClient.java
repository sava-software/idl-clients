package software.sava.idl.clients.jupiter.borrow;

import java.math.BigInteger;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.borrow.gen.types.TransferType;

public interface JupiterBorrowClient {

  static JupiterBorrowClient createClient(final SolanaAccounts solanaAccounts,
                                          final JupiterAccounts jupiterAccounts,
                                          final PublicKey owner) {
    return new JupiterBorrowClientImpl(solanaAccounts, jupiterAccounts, owner);
  }

  static JupiterBorrowClient createClient(final JupiterAccounts jupiterAccounts, final PublicKey owner) {
    return createClient(SolanaAccounts.MAIN_NET, jupiterAccounts, owner);
  }

  SolanaAccounts solanaAccounts();

  JupiterAccounts jupiterAccounts();

  PublicKey owner();

  Instruction initPosition(final PublicKey vaultState,
                           final PublicKey position,
                           final PublicKey positionMint,
                           final PublicKey positionTokenAccount,
                           final PublicKey metadataAccount,
                           final PublicKey tokenProgram,
                           final PublicKey metadataProgram,
                           final int vaultId,
                           final int nextPositionId);

  Instruction operate(final PublicKey signerSupplyTokenAccount,
                      final PublicKey signerBorrowTokenAccount,
                      final PublicKey recipient,
                      final PublicKey recipientBorrowTokenAccount,
                      final PublicKey recipientSupplyTokenAccount,
                      final PublicKey vaultConfig,
                      final PublicKey vaultState,
                      final PublicKey supplyToken,
                      final PublicKey borrowToken,
                      final PublicKey oracle,
                      final PublicKey position,
                      final PublicKey positionTokenAccount,
                      final PublicKey currentPositionTick,
                      final PublicKey finalPositionTick,
                      final PublicKey currentPositionTickId,
                      final PublicKey finalPositionTickId,
                      final PublicKey newBranch,
                      final PublicKey supplyTokenReservesLiquidity,
                      final PublicKey borrowTokenReservesLiquidity,
                      final PublicKey vaultSupplyPositionOnLiquidity,
                      final PublicKey vaultBorrowPositionOnLiquidity,
                      final PublicKey supplyRateModel,
                      final PublicKey borrowRateModel,
                      final PublicKey vaultSupplyTokenAccount,
                      final PublicKey vaultBorrowTokenAccount,
                      final PublicKey supplyTokenClaimAccount,
                      final PublicKey borrowTokenClaimAccount,
                      final PublicKey liquidity,
                      final PublicKey liquidityProgram,
                      final PublicKey oracleProgram,
                      final PublicKey supplyTokenProgram,
                      final PublicKey borrowTokenProgram,
                      final BigInteger newCol,
                      final BigInteger newDebt,
                      final TransferType transferType,
                      final byte[] remainingAccountsIndices);

  Instruction getExchangePrices(final PublicKey vaultState,
                                final PublicKey vaultConfig,
                                final PublicKey supplyTokenReserves,
                                final PublicKey borrowTokenReserves);

  Instruction updateExchangePrices(final PublicKey vaultState,
                                   final PublicKey vaultConfig,
                                   final PublicKey supplyTokenReservesLiquidity,
                                   final PublicKey borrowTokenReservesLiquidity,
                                   final int vaultId);
}
