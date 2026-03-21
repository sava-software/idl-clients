package software.sava.idl.clients.drift;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static software.sava.core.accounts.PublicKey.findProgramAddress;

// https://github.com/drift-labs/protocol-v2/blob/9d997937ff641e95a08cbf4c20b2ea326a5ba6be/sdk/src/addresses/pda.ts#L7
public final class DriftPDAs {

  public static byte[] lowerTwoLE(final int id) {
    return new byte[]{(byte) (id & 0xFF), (byte) (id & 0xFF00)};
  }

  public static ProgramDerivedAddress deriveStateAccount(final PublicKey programId) {
    return findProgramAddress(List.of("drift_state".getBytes(UTF_8)), programId);
  }

  public static ProgramDerivedAddress deriveSignerAccount(final PublicKey programId) {
    return findProgramAddress(List.of("drift_signer".getBytes(UTF_8)), programId);
  }

  public static ProgramDerivedAddress deriveMainUserAccount(final DriftAccounts driftAccounts,
                                                            final PublicKey authority) {
    return deriveUserAccount(driftAccounts, authority, 0);
  }

  public static ProgramDerivedAddress deriveMainUserAccount(final PublicKey driftProgram,
                                                            final PublicKey authority) {
    return deriveUserAccount(driftProgram, authority, 0);
  }

  public static ProgramDerivedAddress deriveUserAccount(final DriftAccounts driftAccounts,
                                                        final PublicKey authority,
                                                        final int subAccountId) {
    return deriveUserAccount(driftAccounts.driftProgram(), authority, subAccountId);
  }

  public static ProgramDerivedAddress deriveUserAccount(final PublicKey driftProgram,
                                                        final PublicKey authority,
                                                        final int subAccountId) {
    return findProgramAddress(
        List.of("user".getBytes(UTF_8), authority.toByteArray(), lowerTwoLE(subAccountId)),
        driftProgram
    );
  }

  public static ProgramDerivedAddress deriveUserStatsAccount(final DriftAccounts driftAccounts,
                                                             final PublicKey authority) {
    return deriveUserStatsAccount(driftAccounts.driftProgram(), authority);
  }

  public static ProgramDerivedAddress deriveUserStatsAccount(final PublicKey driftProgram,
                                                             final PublicKey authority) {
    return findProgramAddress(
        List.of("user_stats".getBytes(UTF_8), authority.toByteArray()),
        driftProgram
    );
  }

  private static ProgramDerivedAddress getIndexedAccount(final DriftAccounts driftAccounts,
                                                         final String namespace,
                                                         final int index) {
    return getIndexedAccount(driftAccounts.driftProgram(), namespace, index);
  }

  private static ProgramDerivedAddress getIndexedAccount(final PublicKey driftProgram,
                                                         final String namespace,
                                                         final int index) {
    return findProgramAddress(
        List.of(namespace.getBytes(UTF_8), lowerTwoLE(index)),
        driftProgram
    );
  }

  public static ProgramDerivedAddress derivePerpMarketAccount(final DriftAccounts driftAccounts,
                                                              final int marketIndex) {
    return getIndexedAccount(driftAccounts, "perp_market", marketIndex);
  }

  public static ProgramDerivedAddress derivePerpMarketAccount(final PublicKey driftProgram,
                                                              final int marketIndex) {
    return getIndexedAccount(driftProgram, "perp_market", marketIndex);
  }

  public static ProgramDerivedAddress deriveSpotMarketAccount(final DriftAccounts driftAccounts,
                                                              final int marketIndex) {
    return getIndexedAccount(driftAccounts, "spot_market", marketIndex);
  }

  public static ProgramDerivedAddress deriveSpotMarketAccount(final PublicKey driftProgram,
                                                              final int marketIndex) {
    return getIndexedAccount(driftProgram, "spot_market", marketIndex);
  }

  public static ProgramDerivedAddress deriveSpotMarketVaultAccount(final DriftAccounts driftAccounts,
                                                                   final int marketIndex) {
    return getIndexedAccount(driftAccounts, "spot_market_vault", marketIndex);
  }

  public static ProgramDerivedAddress deriveSpotMarketVaultAccount(final PublicKey driftProgram,
                                                                   final int marketIndex) {
    return getIndexedAccount(driftProgram, "spot_market_vault", marketIndex);
  }

  public static ProgramDerivedAddress deriveInsuranceFundStakeAccount(final DriftAccounts driftAccounts,
                                                                      final int marketIndex) {
    return getIndexedAccount(driftAccounts, "insurance_fund_stake", marketIndex);
  }

  public static ProgramDerivedAddress deriveInsuranceFundStakeAccount(final PublicKey driftProgram,
                                                                      final int marketIndex) {
    return getIndexedAccount(driftProgram, "insurance_fund_stake", marketIndex);
  }

  public static ProgramDerivedAddress derivePreLaunchOracleAccount(final DriftAccounts driftAccounts,
                                                                   final int marketIndex) {
    return getIndexedAccount(driftAccounts, "prelaunch_oracle", marketIndex);
  }

  public static ProgramDerivedAddress derivePreLaunchOracleAccount(final PublicKey driftProgram,
                                                                   final int marketIndex) {
    return getIndexedAccount(driftProgram, "prelaunch_oracle", marketIndex);
  }

  public static ProgramDerivedAddress derivePythPullOracleAccount(final PublicKey programId, final byte[] feedId) {
    return findProgramAddress(List.of("pyth_pull".getBytes(UTF_8), feedId), programId);
  }

  private DriftPDAs() {
  }
}
