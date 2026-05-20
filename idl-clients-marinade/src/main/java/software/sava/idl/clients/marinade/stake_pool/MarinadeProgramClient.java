package software.sava.idl.clients.marinade.stake_pool;

import software.sava.core.accounts.AccountWithSeed;
import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.token.TokenAccount;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.marinade.stake_pool.gen.types.State;
import software.sava.idl.clients.marinade.stake_pool.gen.types.TicketAccountData;
import software.sava.idl.clients.spl.SPLAccountClient;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;
import software.sava.rpc.json.http.client.SolanaRpcClient;
import software.sava.rpc.json.http.response.AccountInfo;
import software.sava.solana.programs.stake.StakeAccount;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MarinadeProgramClient {

  // --- Factories ---

  static MarinadeProgramClient createClient(final SPLAccountClient splAccountClient,
                                            final MarinadeAccounts marinadeAccounts) {
    return new MarinadeProgramClientImpl(splAccountClient, marinadeAccounts);
  }

  static MarinadeProgramClient createClient(final SPLAccountClient splAccountClient) {
    return createClient(splAccountClient, MarinadeAccounts.MAIN_NET);
  }

  // --- Static helpers ---

  static CompletableFuture<Long> getMinimumBalanceForTicketAccount(final SolanaRpcClient rpcClient) {
    return rpcClient.getMinimumBalanceForRentExemption(TicketAccountData.BYTES);
  }

  static CompletableFuture<List<AccountInfo<TicketAccountData>>> fetchTicketAccounts(final SolanaRpcClient rpcClient,
                                                                                     final PublicKey marinadeProgram,
                                                                                     final PublicKey owner) {
    return rpcClient.getProgramAccounts(
        marinadeProgram,
        List.of(TicketAccountData.SIZE_FILTER, TicketAccountData.createBeneficiaryFilter(owner)),
        TicketAccountData.FACTORY
    );
  }

  static CompletableFuture<AccountInfo<MarinadeValidatorList>> fetchValidatorList(final SolanaRpcClient rpcClient,
                                                                                  final State programState) {
    final var destinationValidatorList = programState.validatorSystem().validatorList();
    return rpcClient.getAccountInfo(destinationValidatorList.account(), MarinadeValidatorList.FACTORY);
  }

  static long totalVirtualStakedLamports(final State state) {
    final long totalLamportsUnderControl = state.validatorSystem().totalActiveBalance()
        + state.stakeSystem().delayedUnstakeCoolingDown()
        + state.emergencyCoolingDown()
        + state.availableReserveBalance();
    return totalLamportsUnderControl - state.circulatingTicketBalance();
  }

  static double solPrice(final State state) {
    return totalVirtualStakedLamports(state) / (double) state.msolSupply();
  }

  static int accountIndex(final byte[] key,
                          final byte[] listData,
                          final int itemSize) {
    for (int i = 8, s = 0; i < listData.length; i += itemSize, ++s) {
      if (Arrays.equals(
          key, 0, PublicKey.PUBLIC_KEY_LENGTH,
          listData, i, i + PublicKey.PUBLIC_KEY_LENGTH
      )) {
        return s;
      }
    }
    return -1;
  }

  static int stakeAccountIndex(final byte[] stakeAccountKey,
                               final byte[] stakeListData,
                               final State state) {
    return accountIndex(stakeAccountKey, stakeListData, state.stakeSystem().stakeList().itemSize());
  }

  static int stakeAccountIndex(final StakeAccount stakeAccount,
                               final byte[] validatorListData,
                               final State state) {
    return stakeAccountIndex(stakeAccount.address().toByteArray(), validatorListData, state);
  }

  static int validatorIndex(final byte[] validatorKey,
                            final byte[] validatorListData,
                            final State state) {
    return accountIndex(validatorKey, validatorListData, state.validatorSystem().validatorList().itemSize());
  }

  static int validatorIndex(final StakeAccount stakeAccount,
                            final byte[] validatorListData,
                            final State state) {
    return validatorIndex(stakeAccount.voterPublicKey().toByteArray(), validatorListData, state);
  }

  // --- Accessors ---

  SolanaAccounts solanaAccounts();

  MarinadeAccounts marinadeAccounts();

  SPLAccountClient splAccountClient();

  PublicKey owner();

  PublicKey feePayer();

  // --- Abstract instructions ---

  Instruction marinadeDeposit(final PublicKey mSolTokenAccount, final long lamports);

  Instruction depositStakeAccount(final PublicKey validatorListKey,
                                  final PublicKey stakeListKey,
                                  final PublicKey stakeAccount,
                                  final PublicKey duplicationFlagKey,
                                  final PublicKey rentPayer,
                                  final PublicKey mSolTokenAccount,
                                  final int validatorIndex);

  Instruction claimTicket(final PublicKey ticketAccount);

  Instruction withdrawStakeAccount(final PublicKey mSolTokenAccount,
                                   final PublicKey validatorListKey,
                                   final PublicKey stakeListKey,
                                   final PublicKey stakeWithdrawalAuthorityKey,
                                   final PublicKey stakeDepositAuthorityKey,
                                   final PublicKey stakeAccount,
                                   final PublicKey splitStakeAccountKey,
                                   final PublicKey splitStakeRentPayer,
                                   final PublicKey beneficiary,
                                   final int stakeIndex,
                                   final int validatorIndex,
                                   final long msolAmount);

  Instruction liquidUnstake(final PublicKey mSolTokenAccount,
                            final PublicKey solReceiverAccount,
                            final long msolAmount);

  Instruction orderUnstake(final PublicKey mSolTokenAccount,
                           final PublicKey newTicketAccount,
                           final long msolAmount);

  Instruction addLiquidity(final PublicKey solSourceAccount,
                           final PublicKey lpTokenAccount,
                           final long lamports);

  Instruction removeLiquidity(final PublicKey lpTokenAccount,
                              final PublicKey solReceiverAccount,
                              final PublicKey mSolReceiverAccount,
                              final long lpTokens);

  // --- Default helpers / convenience overloads ---

  default CompletableFuture<List<AccountInfo<TokenAccount>>> fetchMSolTokenAccounts(final SolanaRpcClient rpcClient) {
    return rpcClient.getTokenAccountsForTokenMintByOwner(owner(), marinadeAccounts().mSolTokenMint());
  }

  default CompletableFuture<AccountInfo<State>> fetchProgramState(final SolanaRpcClient rpcClient) {
    return rpcClient.getAccountInfo(marinadeAccounts().stateAccount(), State.FACTORY);
  }

  default CompletableFuture<List<AccountInfo<TicketAccountData>>> fetchTicketAccounts(final SolanaRpcClient rpcClient) {
    return fetchTicketAccounts(rpcClient, marinadeAccounts().marinadeProgram(), owner());
  }

  default ProgramDerivedAddress findDuplicationKey(final PublicKey validatorPublicKey) {
    return marinadeAccounts().findDuplicationKey(validatorPublicKey);
  }

  default PublicKey deriveATA(final PublicKey ownerKey, final PublicKey mintKey) {
    return AssociatedTokenPDAs.associatedTokenPDA(
        solanaAccounts().associatedTokenAccountProgram(),
        ownerKey,
        solanaAccounts().tokenProgram(),
        mintKey
    ).publicKey();
  }

  @Deprecated
  default AccountWithSeed createOffCurveAccountWithSeed(final String asciiSeed) {
    return PublicKey.createOffCurveAccountWithAsciiSeed(
        feePayer(),
        asciiSeed,
        marinadeAccounts().marinadeProgram()
    );
  }

  // depositStakeAccount

  default Instruction depositStakeAccount(final PublicKey validatorListKey,
                                          final PublicKey stakeListKey,
                                          final PublicKey stakeAccount,
                                          final PublicKey duplicationFlagKey,
                                          final PublicKey mSolTokenAccount,
                                          final int validatorIndex) {
    return depositStakeAccount(
        validatorListKey,
        stakeListKey,
        stakeAccount,
        duplicationFlagKey,
        feePayer(),
        mSolTokenAccount,
        validatorIndex
    );
  }

  default Instruction depositStakeAccount(final State marinadeProgramState,
                                          final PublicKey stakeAccount,
                                          final PublicKey mSolTokenAccount,
                                          final PublicKey validatorPublicKey,
                                          final int validatorIndex) {
    return depositStakeAccount(
        marinadeProgramState.validatorSystem().validatorList().account(),
        marinadeProgramState.stakeSystem().stakeList().account(),
        stakeAccount,
        findDuplicationKey(validatorPublicKey).publicKey(),
        feePayer(),
        mSolTokenAccount,
        validatorIndex
    );
  }

  // claimTicket

  default List<Instruction> claimTickets(final Collection<PublicKey> ticketAccounts) {
    return ticketAccounts.stream().map(this::claimTicket).toList();
  }

  // withdrawStakeAccount

  default Instruction withdrawStakeAccount(final PublicKey mSolTokenAccount,
                                           final PublicKey validatorListKey,
                                           final PublicKey stakeListKey,
                                           final PublicKey stakeWithdrawalAuthorityKey,
                                           final PublicKey stakeDepositAuthorityKey,
                                           final PublicKey stakeAccount,
                                           final PublicKey splitStakeAccountKey,
                                           final int stakeIndex,
                                           final int validatorIndex,
                                           final long msolAmount) {
    return withdrawStakeAccount(
        mSolTokenAccount,
        validatorListKey,
        stakeListKey,
        stakeWithdrawalAuthorityKey,
        stakeDepositAuthorityKey,
        stakeAccount,
        splitStakeAccountKey,
        feePayer(),
        owner(),
        stakeIndex,
        validatorIndex,
        msolAmount
    );
  }

  default Instruction withdrawStakeAccount(final State marinadeProgramState,
                                           final PublicKey mSolTokenAccount,
                                           final PublicKey stakeAccount,
                                           final PublicKey splitStakeAccountKey,
                                           final int stakeIndex,
                                           final int validatorIndex,
                                           final long msolAmount) {
    final var marinadeAccounts = marinadeAccounts();
    return withdrawStakeAccount(
        mSolTokenAccount,
        marinadeProgramState.validatorSystem().validatorList().account(),
        marinadeProgramState.stakeSystem().stakeList().account(),
        marinadeAccounts.stakeWithdrawAuthority(),
        marinadeAccounts.stakeDepositAuthority(),
        stakeAccount,
        splitStakeAccountKey,
        feePayer(),
        owner(),
        stakeIndex,
        validatorIndex,
        msolAmount
    );
  }

  default Instruction withdrawStakeAccount(final State marinadeProgramState,
                                           final PublicKey mSolTokenAccount,
                                           final PublicKey stakeAccount,
                                           final PublicKey splitStakeAccountKey,
                                           final PublicKey splitStakeRentPayer,
                                           final PublicKey beneficiary,
                                           final int stakeIndex,
                                           final int validatorIndex,
                                           final long msolAmount) {
    final var marinadeAccounts = marinadeAccounts();
    return withdrawStakeAccount(
        mSolTokenAccount,
        marinadeProgramState.validatorSystem().validatorList().account(),
        marinadeProgramState.stakeSystem().stakeList().account(),
        marinadeAccounts.stakeWithdrawAuthority(),
        marinadeAccounts.stakeDepositAuthority(),
        stakeAccount,
        splitStakeAccountKey,
        splitStakeRentPayer,
        beneficiary,
        stakeIndex,
        validatorIndex,
        msolAmount
    );
  }

  // liquidUnstake / addLiquidity / removeLiquidity convenience overloads

  default Instruction liquidUnstake(final PublicKey mSolTokenAccount, final long msolAmount) {
    return liquidUnstake(mSolTokenAccount, owner(), msolAmount);
  }

  default Instruction addLiquidity(final PublicKey lpTokenAccount, final long lamports) {
    return addLiquidity(owner(), lpTokenAccount, lamports);
  }

  default Instruction addLiquidityATA(final long lamports) {
    return addLiquidity(owner(), deriveATA(owner(), marinadeAccounts().liquidityPoolMSolSolMint()), lamports);
  }

  default Instruction removeLiquidity(final PublicKey lpTokenAccount,
                                      final PublicKey mSolReceiverAccount,
                                      final long lpTokens) {
    return removeLiquidity(lpTokenAccount, owner(), mSolReceiverAccount, lpTokens);
  }

  default Instruction removeLiquidityATA(final long lpTokens) {
    final var marinadeAccounts = marinadeAccounts();
    final var ownerKey = owner();
    return removeLiquidity(
        deriveATA(ownerKey, marinadeAccounts.liquidityPoolMSolSolMint()),
        ownerKey,
        deriveATA(ownerKey, marinadeAccounts.mSolTokenMint()),
        lpTokens
    );
  }
}
