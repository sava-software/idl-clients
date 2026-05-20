package software.sava.idl.clients.jupiter.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;

public interface JupiterLendClient {

  static JupiterLendClient createClient(final SolanaAccounts solanaAccounts,
                                        final JupiterAccounts jupiterAccounts,
                                        final PublicKey owner) {
    return new JupiterLendClientImpl(solanaAccounts, jupiterAccounts, owner);
  }

  static JupiterLendClient createClient(final JupiterAccounts jupiterAccounts, final PublicKey owner) {
    return createClient(SolanaAccounts.MAIN_NET, jupiterAccounts, owner);
  }

  SolanaAccounts solanaAccounts();

  JupiterAccounts jupiterAccounts();

  PublicKey owner();

  Instruction deposit(final PublicKey depositorTokenAccount,
                      final PublicKey recipientTokenAccount,
                      final PublicKey mint,
                      final PublicKey lending,
                      final PublicKey fTokenMint,
                      final PublicKey supplyTokenReservesLiquidity,
                      final PublicKey lendingSupplyPositionOnLiquidity,
                      final PublicKey rateModel,
                      final PublicKey vault,
                      final PublicKey liquidity,
                      final PublicKey liquidityProgram,
                      final PublicKey rewardsRateModel,
                      final PublicKey tokenProgram,
                      final long assets);

  Instruction depositWithMinAmountOut(final PublicKey depositorTokenAccount,
                                      final PublicKey recipientTokenAccount,
                                      final PublicKey mint,
                                      final PublicKey lending,
                                      final PublicKey fTokenMint,
                                      final PublicKey supplyTokenReservesLiquidity,
                                      final PublicKey lendingSupplyPositionOnLiquidity,
                                      final PublicKey rateModel,
                                      final PublicKey vault,
                                      final PublicKey liquidity,
                                      final PublicKey liquidityProgram,
                                      final PublicKey rewardsRateModel,
                                      final PublicKey tokenProgram,
                                      final long assets,
                                      final long minAmountOut);

  Instruction mint(final PublicKey depositorTokenAccount,
                   final PublicKey recipientTokenAccount,
                   final PublicKey mint,
                   final PublicKey lending,
                   final PublicKey fTokenMint,
                   final PublicKey supplyTokenReservesLiquidity,
                   final PublicKey lendingSupplyPositionOnLiquidity,
                   final PublicKey rateModel,
                   final PublicKey vault,
                   final PublicKey liquidity,
                   final PublicKey liquidityProgram,
                   final PublicKey rewardsRateModel,
                   final PublicKey tokenProgram,
                   final long shares);

  Instruction mintWithMaxAssets(final PublicKey depositorTokenAccount,
                                final PublicKey recipientTokenAccount,
                                final PublicKey mint,
                                final PublicKey lending,
                                final PublicKey fTokenMint,
                                final PublicKey supplyTokenReservesLiquidity,
                                final PublicKey lendingSupplyPositionOnLiquidity,
                                final PublicKey rateModel,
                                final PublicKey vault,
                                final PublicKey liquidity,
                                final PublicKey liquidityProgram,
                                final PublicKey rewardsRateModel,
                                final PublicKey tokenProgram,
                                final long shares,
                                final long maxAssets);

  Instruction redeem(final PublicKey ownerTokenAccount,
                     final PublicKey recipientTokenAccount,
                     final PublicKey lending,
                     final PublicKey mint,
                     final PublicKey fTokenMint,
                     final PublicKey supplyTokenReservesLiquidity,
                     final PublicKey lendingSupplyPositionOnLiquidity,
                     final PublicKey rateModel,
                     final PublicKey vault,
                     final PublicKey claimAccount,
                     final PublicKey liquidity,
                     final PublicKey liquidityProgram,
                     final PublicKey rewardsRateModel,
                     final PublicKey tokenProgram,
                     final long shares);

  Instruction redeemWithMinAmountOut(final PublicKey ownerTokenAccount,
                                     final PublicKey recipientTokenAccount,
                                     final PublicKey lending,
                                     final PublicKey mint,
                                     final PublicKey fTokenMint,
                                     final PublicKey supplyTokenReservesLiquidity,
                                     final PublicKey lendingSupplyPositionOnLiquidity,
                                     final PublicKey rateModel,
                                     final PublicKey vault,
                                     final PublicKey claimAccount,
                                     final PublicKey liquidity,
                                     final PublicKey liquidityProgram,
                                     final PublicKey rewardsRateModel,
                                     final PublicKey tokenProgram,
                                     final long shares,
                                     final long minAmountOut);

  Instruction withdraw(final PublicKey ownerTokenAccount,
                       final PublicKey recipientTokenAccount,
                       final PublicKey lending,
                       final PublicKey mint,
                       final PublicKey fTokenMint,
                       final PublicKey supplyTokenReservesLiquidity,
                       final PublicKey lendingSupplyPositionOnLiquidity,
                       final PublicKey rateModel,
                       final PublicKey vault,
                       final PublicKey claimAccount,
                       final PublicKey liquidity,
                       final PublicKey liquidityProgram,
                       final PublicKey rewardsRateModel,
                       final PublicKey tokenProgram,
                       final long amount);

  Instruction withdrawWithMaxSharesBurn(final PublicKey ownerTokenAccount,
                                        final PublicKey recipientTokenAccount,
                                        final PublicKey lending,
                                        final PublicKey mint,
                                        final PublicKey fTokenMint,
                                        final PublicKey supplyTokenReservesLiquidity,
                                        final PublicKey lendingSupplyPositionOnLiquidity,
                                        final PublicKey rateModel,
                                        final PublicKey vault,
                                        final PublicKey claimAccount,
                                        final PublicKey liquidity,
                                        final PublicKey liquidityProgram,
                                        final PublicKey rewardsRateModel,
                                        final PublicKey tokenProgram,
                                        final long amount,
                                        final long maxSharesBurn);

  Instruction updateRate(final PublicKey lending,
                         final PublicKey mint,
                         final PublicKey fTokenMint,
                         final PublicKey supplyTokenReservesLiquidity,
                         final PublicKey rewardsRateModel);
}
