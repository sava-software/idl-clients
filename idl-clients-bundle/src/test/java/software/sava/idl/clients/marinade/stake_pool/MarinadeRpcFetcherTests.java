package software.sava.idl.clients.marinade.stake_pool;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.SolanaRpcCaptureTests;
import software.sava.idl.clients.marinade.stake_pool.gen.types.FeeCents;
import software.sava.idl.clients.marinade.stake_pool.gen.types.List;
import software.sava.idl.clients.marinade.stake_pool.gen.types.StakeSystem;
import software.sava.idl.clients.marinade.stake_pool.gen.types.State;
import software.sava.idl.clients.marinade.stake_pool.gen.types.TicketAccountData;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorRecord;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorSystem;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Marinade RPC fetchers against the capture harness. Each fetcher
/// encodes which program is scanned, which filters select the accounts, and
/// which parser (with which count) reads them back — a wrong filter offset or
/// a stale list key returns a plausible empty result rather than an error, so
/// the request fragments are the assertions with teeth. Responses carry
/// distinguishable non-default values that are asserted through the parse.
final class MarinadeRpcFetcherTests extends SolanaRpcCaptureTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final MarinadeAccounts MARINADE_ACCOUNTS = MarinadeAccounts.MAIN_NET;
  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);

  /// A live delayed-unstake ticket 165 bytes long, 77 past [TicketAccountData#BYTES] and zero
  /// there, the length a size filter dropped: fetched from mainnet at slot 465282733, owned by
  /// the Marinade program and naming its state account.
  private static final String LONG_TICKET = "DPuvkM4ogczj9brLUam1RkMZb6wRvtg8F5exonx43j2p";
  private static final String LONG_TICKET_DATA = "hU0SYtMB5wN1EZsxdYB1huP0p+XND4kOlqdTsQ/Mx2gelHOgCDJw8bqJ2OZzd+OSf03kXAJcXT0WTkF3MME5qIwarR/BpvqPfpaYAAAAAAB6AgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  private static final PublicKey LONG_TICKET_BENEFICIARY = PublicKey.fromBase58Encoded("DZAkBrLEwkQCDNdpdf2gYUEbACDca878ob1XhzuNg7oc");

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// The request's whole `filters` array as sava-rpc writes it. Matching it pins that no other
  /// filter is sent, which one fragment per filter cannot: a `dataSize` filter would pass that.
  private static String filterArray(final Filter... filters) {
    return Arrays.stream(filters).map(Filter::toJson).collect(Collectors.joining(",", "\"filters\":[", "]"));
  }

  private static MarinadeProgramClient client() {
    return MarinadeProgramClient.createClient(
        SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER)));
  }

  @Test
  void minimumTicketBalanceRequestsTheTicketAccountSize() {
    respondWith(numberResult(2_282_880L));
    final long minimum = MarinadeProgramClient.getMinimumBalanceForTicketAccount(rpcClient()).join();
    assertEquals(2_282_880L, minimum);
    assertLastRequestContains("getMinimumBalanceForRentExemption", Long.toString(TicketAccountData.BYTES));
  }

  /// The ticket scan pins the account type by discriminator and filters to the
  /// beneficiary, sending no size filter: live tickets are 88 or 165 bytes. The
  /// default overload binds the client's own program and owner. The response is
  /// the live 165-byte ticket, decoded through the fetcher's factory; the
  /// expected fields were read from its bytes at the IDL's offsets.
  @Test
  void ticketAccountsFilterByDiscriminatorAndBeneficiary() {
    final byte[] ticketData = Base64.getDecoder().decode(LONG_TICKET_DATA);
    assertEquals(TicketAccountData.BYTES + 77, ticketData.length);
    assertArrayEquals(new byte[77], Arrays.copyOfRange(ticketData, TicketAccountData.BYTES, ticketData.length));
    final var response = programAccountsResult(
        LONG_TICKET, ticketData, MARINADE_ACCOUNTS.marinadeProgram().toBase58());

    respondWith(response);
    final var explicit = MarinadeProgramClient
        .fetchTicketAccounts(rpcClient(), MARINADE_ACCOUNTS.marinadeProgram(), LONG_TICKET_BENEFICIARY)
        .join();
    assertLastRequestContains(
        "getProgramAccounts",
        MARINADE_ACCOUNTS.marinadeProgram().toBase58(),
        filterArray(TicketAccountData.DISCRIMINATOR_FILTER, TicketAccountData.createBeneficiaryFilter(LONG_TICKET_BENEFICIARY)));
    assertEquals(1, explicit.size());
    final var accountInfo = explicit.getFirst();
    assertEquals(PublicKey.fromBase58Encoded(LONG_TICKET), accountInfo.pubKey());
    assertEquals(165, accountInfo.space());
    final var parsed = accountInfo.data();
    assertEquals(MARINADE_ACCOUNTS.stateAccount(), parsed.stateAddress());
    assertEquals(LONG_TICKET_BENEFICIARY, parsed.beneficiary());
    assertEquals(9_999_998L, parsed.lamportsAmount());
    assertEquals(634L, parsed.createdEpoch());

    // the default overload scans for the client's own owner
    respondWith(response);
    assertEquals(1, client().fetchTicketAccounts(rpcClient()).join().size());
    assertLastRequestContains(
        MARINADE_ACCOUNTS.marinadeProgram().toBase58(),
        filterArray(TicketAccountData.DISCRIMINATOR_FILTER, TicketAccountData.createBeneficiaryFilter(OWNER)));
  }

  @Test
  void programStateIsFetchedFromTheStateAccount() {
    final byte[] stateData = stateData(3, key(0x31));
    respondWith(accountInfoResult(stateData, MARINADE_ACCOUNTS.marinadeProgram().toBase58()));

    final var accountInfo = client().fetchProgramState(rpcClient()).join();
    assertEquals(7_777L, accountInfo.data().msolSupply(), "the parsed state carries the planted supply");
    assertLastRequestContains("getAccountInfo", MARINADE_ACCOUNTS.stateAccount().toBase58());
  }

  /// `fetchValidatorList` threads two things out of the fetched `State`: the
  /// list *account* to read, and the authoritative `count` the parser stops
  /// at — the list account itself has no trustworthy length (see
  /// `MarinadeValidatorList`).
  @Test
  void validatorListIsReadFromTheStatesAccountAtTheStatesCount() {
    final var listKey = key(0x41);
    final var first = key(0x42);
    final var second = key(0x43);
    final var state = State.read(stateData(2, listKey), 0);

    // three non-zero records on the wire, but the State's count is 2
    final byte[] listData = new byte[MarinadeValidatorList.MAGIC_LEN + 3 * MarinadeValidatorList.ITEM_SIZE];
    int offset = MarinadeValidatorList.MAGIC_LEN;
    for (final var validator : new PublicKey[]{first, second, key(0x44)}) {
      new ValidatorRecord(validator, 100L, 1, 596L, 1, 0L).write(listData, offset);
      offset += MarinadeValidatorList.ITEM_SIZE;
    }
    respondWith(accountInfoResult(listData, MARINADE_ACCOUNTS.marinadeProgram().toBase58()));

    final var accountInfo = MarinadeProgramClient.fetchValidatorList(rpcClient(), state).join();
    final var validatorList = accountInfo.data();
    assertEquals(2, validatorList.validators().size(), "the State's count bounds the parse");
    assertEquals(first, validatorList.validators().get(0).validatorAccount());
    assertEquals(second, validatorList.validators().get(1).validatorAccount());
    assertLastRequestContains("getAccountInfo", listKey.toBase58());
  }

  @Test
  void mSolTokenAccountsScanTheOwnersMintHoldings() {
    respondWith(EMPTY_TOKEN_ACCOUNTS);
    assertTrue(client().fetchMSolTokenAccounts(rpcClient()).join().isEmpty());
    assertLastRequestContains(
        "getTokenAccountsByOwner",
        OWNER.toBase58(),
        MARINADE_ACCOUNTS.mSolTokenMint().toBase58());
  }

  /// A `State` account carrying only what the fetchers read: the mSOL supply,
  /// and the validator list's account key and count.
  private static byte[] stateData(final long validatorCount, final PublicKey validatorListKey) {
    final byte[] data = new byte[State.DELINQUENT_UPGRADER_OFFSET + 1 + (2 * FeeCents.BYTES)];
    State.DISCRIMINATOR.write(data, 0);
    data[State.DELINQUENT_UPGRADER_OFFSET] = 2; // Done — a unit variant
    ByteUtil.putInt64LE(data, State.MSOL_SUPPLY_OFFSET, 7_777L);
    final int validatorList = State.VALIDATOR_SYSTEM_OFFSET + ValidatorSystem.VALIDATOR_LIST_OFFSET;
    validatorListKey.write(data, validatorList + List.ACCOUNT_OFFSET);
    ByteUtil.putInt32LE(data, validatorList + List.COUNT_OFFSET, (int) validatorCount);
    ByteUtil.putInt32LE(data, validatorList + List.ITEM_SIZE_OFFSET, MarinadeValidatorList.ITEM_SIZE);
    final int stakeList = State.STAKE_SYSTEM_OFFSET + StakeSystem.STAKE_LIST_OFFSET;
    key(0x51).write(data, stakeList + List.ACCOUNT_OFFSET);
    return data;
  }
}
