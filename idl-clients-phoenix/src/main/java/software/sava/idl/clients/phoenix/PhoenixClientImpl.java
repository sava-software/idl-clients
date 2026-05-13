package software.sava.idl.clients.phoenix;

import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalProgram;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.DepositFundsInstruction;

final class PhoenixClientImpl implements PhoenixClient {

  private final PhoenixAccounts accounts;

  PhoenixClientImpl(final PhoenixAccounts accounts) {
    this.accounts = accounts;
  }

  @Override
  public PhoenixAccounts phoenixAccounts() {
    return accounts;
  }

  @Override
  public Instruction deposit(final PublicKey owner,
                             final PublicKey mint,
                             final PublicKey emberMint,
                             final PublicKey tokenAccount,
                             final PublicKey emberTokenAccount,
                             final PublicKey tokenProgram,
                             final DepositParams depositParams) {
    return EmberProgram.deposit(
        accounts.invokedEmberProgram(),
        owner,
        accounts.emberStateProgram(),
        mint,
        emberMint,
        tokenAccount,
        emberTokenAccount,
        accounts.emberVaultProgram(),
        tokenProgram,
        depositParams
    );
  }

  @Override
  public Instruction depositFunds(final PublicKey traderWalletKey,
                                  final PublicKey traderTokenAccountKey,
                                  final PublicKey traderAccountKey,
                                  final PublicKey tokenProgram,
                                  final DepositFundsInstruction depositFundsInstruction) {
    final var invokedEternalProgram = accounts.invokedEternalProgram();
    return EternalProgram.depositFunds(
        invokedEternalProgram,
        invokedEternalProgram.publicKey(),
        accounts.eternalLogAuthority(),
        accounts.eternalGlobalConfig(),
        traderWalletKey,
        traderTokenAccountKey,
        traderAccountKey,
        accounts.eternalGlobalConfig(),
        tokenProgram,
        accounts.globalTraderIndex(),
        accounts.activeTraderBuffer(),
        depositFundsInstruction
    );
  }
}
