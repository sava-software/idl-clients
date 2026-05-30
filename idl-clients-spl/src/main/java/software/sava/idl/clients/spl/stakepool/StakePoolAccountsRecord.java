package software.sava.idl.clients.spl.stakepool;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public record StakePoolAccountsRecord(PublicKey stakePoolProgram,
                                      AccountMeta invokedStakePoolProgram,
                                      AccountMeta readStakePoolProgram,
                                      PublicKey singleValidatorStakePoolProgram,
                                      AccountMeta invokedSingleValidatorStakePoolProgram,
                                      AccountMeta readSingleValidatorStakePoolProgram,
                                      PublicKey sanctumMultiValidatorStakePoolProgram,
                                      PublicKey sanctumSingleValidatorStakePoolProgram) implements StakePoolAccounts {
}
