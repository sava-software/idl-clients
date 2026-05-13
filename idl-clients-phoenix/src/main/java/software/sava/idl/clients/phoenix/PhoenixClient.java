package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.DepositFundsInstruction;

public interface PhoenixClient {

  static PhoenixClient createClient(final PhoenixAccounts accounts) {
    return new PhoenixClientImpl(accounts);
  }

  PhoenixAccounts phoenixAccounts();

  Instruction deposit(final PublicKey owner,
                      final PublicKey mint,
                      final PublicKey emberMint,
                      final PublicKey tokenAccount,
                      final PublicKey emberTokenAccount,
                      final PublicKey tokenProgram,
                      final DepositParams depositParams);

  Instruction depositFunds(final PublicKey traderWalletKey,
                           final PublicKey traderTokenAccountKey,
                           final PublicKey traderAccountKey,
                           final PublicKey tokenProgram,
                           final DepositFundsInstruction depositFundsInstruction);
}
