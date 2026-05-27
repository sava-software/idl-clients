package software.sava.idl.clients.spl.compute_budget;

import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.compute_budget.gen.ComputeBudgetProgram;

public final class ComputeBudgetUtil {

  public static int CU_LIMIT_CONSUMPTION = 150;
  public static int CU_PRICE_CONSUMPTION = 150;
  // CU's consumed for typical inclusion of set CU limit and price.
  public static int COMPUTE_UNITS_CONSUMED = CU_LIMIT_CONSUMPTION + CU_PRICE_CONSUMPTION;
  public static int SET_LOADED_ACCOUNT_SIZE_LIMIT_CONSUMPTION = 150;

  public static int MAX_COMPUTE_BUDGET = 1_400_000;

  public static final Instruction MAX_COMPUTE_BUDGET_IX = ComputeBudgetProgram.setComputeUnitLimit(
      SolanaAccounts.MAIN_NET.invokedComputeBudgetProgram(),
      MAX_COMPUTE_BUDGET
  );

  private ComputeBudgetUtil() {
  }
}
