package software.sava.idl.clients.jupiter.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.jupiter.lend.gen.LendingProgram;

record JupiterLendClientImpl(SolanaAccounts solanaAccounts,
                             JupiterAccounts jupiterAccounts,
                             PublicKey owner) implements JupiterLendClient {

  @Override
  public Instruction deposit(final PublicKey depositorTokenAccount,
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
                             final long assets) {
    return LendingProgram.deposit(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        depositorTokenAccount,
        recipientTokenAccount,
        mint,
        jupiterAccounts.lendingAdminKey(),
        lending,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        assets
    );
  }

  @Override
  public Instruction depositWithMinAmountOut(final PublicKey depositorTokenAccount,
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
                                             final long minAmountOut) {
    return LendingProgram.depositWithMinAmountOut(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        depositorTokenAccount,
        recipientTokenAccount,
        mint,
        jupiterAccounts.lendingAdminKey(),
        lending,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        assets,
        minAmountOut
    );
  }

  @Override
  public Instruction mint(final PublicKey depositorTokenAccount,
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
                          final long shares) {
    return LendingProgram.mint(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        depositorTokenAccount,
        recipientTokenAccount,
        mint,
        jupiterAccounts.lendingAdminKey(),
        lending,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        shares
    );
  }

  @Override
  public Instruction mintWithMaxAssets(final PublicKey depositorTokenAccount,
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
                                       final long maxAssets) {
    return LendingProgram.mintWithMaxAssets(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        depositorTokenAccount,
        recipientTokenAccount,
        mint,
        jupiterAccounts.lendingAdminKey(),
        lending,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        shares,
        maxAssets
    );
  }

  @Override
  public Instruction redeem(final PublicKey ownerTokenAccount,
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
                            final long shares) {
    return LendingProgram.redeem(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        ownerTokenAccount,
        recipientTokenAccount,
        jupiterAccounts.lendingAdminKey(),
        lending,
        mint,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        claimAccount,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        shares
    );
  }

  @Override
  public Instruction redeemWithMinAmountOut(final PublicKey ownerTokenAccount,
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
                                            final long minAmountOut) {
    return LendingProgram.redeemWithMinAmountOut(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        ownerTokenAccount,
        recipientTokenAccount,
        jupiterAccounts.lendingAdminKey(),
        lending,
        mint,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        claimAccount,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        shares,
        minAmountOut
    );
  }

  @Override
  public Instruction withdraw(final PublicKey ownerTokenAccount,
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
                              final long amount) {
    return LendingProgram.withdraw(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        ownerTokenAccount,
        recipientTokenAccount,
        jupiterAccounts.lendingAdminKey(),
        lending,
        mint,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        claimAccount,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        amount
    );
  }

  @Override
  public Instruction withdrawWithMaxSharesBurn(final PublicKey ownerTokenAccount,
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
                                               final long maxSharesBurn) {
    return LendingProgram.withdrawWithMaxSharesBurn(
        jupiterAccounts.invokedLendingProgram(),
        solanaAccounts,
        owner,
        ownerTokenAccount,
        recipientTokenAccount,
        jupiterAccounts.lendingAdminKey(),
        lending,
        mint,
        fTokenMint,
        supplyTokenReservesLiquidity,
        lendingSupplyPositionOnLiquidity,
        rateModel,
        vault,
        claimAccount,
        liquidity,
        liquidityProgram,
        rewardsRateModel,
        tokenProgram,
        amount,
        maxSharesBurn
    );
  }

  @Override
  public Instruction updateRate(final PublicKey lending,
                                final PublicKey mint,
                                final PublicKey fTokenMint,
                                final PublicKey supplyTokenReservesLiquidity,
                                final PublicKey rewardsRateModel) {
    return LendingProgram.updateRate(
        jupiterAccounts.invokedLendingProgram(),
        lending,
        mint,
        fTokenMint,
        supplyTokenReservesLiquidity,
        rewardsRateModel
    );
  }
}
