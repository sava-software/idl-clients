package software.sava.idl.clients.jupiter.perpetuals;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.jupiter.perpetuals.gen.PerpetualsProgram;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/// Jupiter Perpetuals' published IDL gives `close_position_request3` one account fewer than the
/// program reads, and this records the shortfall rather than working around it.
///
/// As deployed at slot 465131236, the program reads 15 accounts for it: the IDL's first 12,
/// `keeper` through `associated_token_program`, then an account it names `dispensing_custody`,
/// then `event_authority` and `program`. It checks `dispensing_custody` the way an Anchor
/// `Account<Custody>` marked `mut` would: in simulation an empty account failed 3012, the Pool
/// 3002, a token account 3007, and a custody the transaction did not make writable 2000.
/// `PerpetualsProgram.closePositionRequest3` sends the IDL's 14 accounts, so the program reads the
/// event-authority PDA at index 12 as `dispensing_custody`, and every call built with it fails:
/// 3012 `AccountNotInitialized`, for as long as that PDA holds no lamports.
///
/// **Use `PerpetualsProgram.closePositionRequest2` instead.** It is what live clients send: each of
/// the 93 closes found by sweeping live position requests was a `close_position_request2`, a
/// strided scan of post-deploy traffic found 112 more calls to it, and none of the 1,205 successful
/// post-deploy transactions examined carried a `close_position_request3`. The owner behind the one
/// rejected call pinned below closed the same request with it 169 slots later, in
/// `3qZagJXkGADKxBVNAZzKwGZq9Jv26BvvdXznqM8BZthKrj6E38BPX212df7XRagkkMvN2GhfTL68hbHSfnvcf64d`
/// (slot 465213710). Simulated against six live trigger requests, it had the same effects as a
/// `close_position_request3` given a custody: the same token-program calls, the same
/// `ClosePositionRequestEvent`, the same lamport movements, and no change to any custody or to the
/// pool. `close_position_request3` costs about 16,000 CU more.
///
/// A caller who builds `close_position_request3` anyway has to insert a writable Custody at index
/// 12 of a copy of the list `closePositionRequest3Keys` returns, which is immutable. The program
/// has a single pool, and each of its six custodies passed there in simulation, with the same
/// effects. The request's own custody, `PositionRequest.custody()`, is the one to pick: it is
/// already writable at index 7, so it adds no key to the transaction, and it costs 181 CU less than
/// another custody. Which custody the program intends is not established, and neither is whether
/// the choice matters for a kind of request the simulations did not cover, such as a market
/// request.
///
/// The account list is upstream's to fix, so there is no hand-written builder and no override in
/// main_net_programs.json. The client is generated from the anchor IDL account
/// `38GK1i4cQPAxqrbfKX4RRMNfXpKRn5PgLFHVJeXm1C8Y`, last written at slot 391647676, before the
/// program's last two deploys.
///
/// Two of the tests pin the generated builders to live calls, the rejected
/// `close_position_request3` and an accepted `close_position_request2`. The third is the tripwire.
/// A corrected IDL, once regenerated, either gives `closePositionRequest3Keys` a parameter for
/// `dispensing_custody`, which stops this class compiling, or a 15th account, which fails the
/// tripwire. Either way, rewrite this class then to pin the corrected order against a live call,
/// and drop this warning.
final class ClosePositionRequest3Tests {

  private static final HexFormat HEX = HexFormat.of();

  private static final PublicKey PERP = key("PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu");
  private static final PublicKey TOKEN_PROGRAM = key("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
  private static final PublicKey SYSTEM_PROGRAM = key("11111111111111111111111111111111");
  private static final PublicKey ATA_PROGRAM = key("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL");
  private static final PublicKey USDC = key("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
  /// The program's only Pool.
  private static final PublicKey POOL = key("5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq");
  private static final PublicKey SOL_CUSTODY = key("7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz");
  /// The program's Anchor `__event_authority` PDA, as both live calls carry it.
  private static final PublicKey EVENT_AUTHORITY = key("37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN");

  /// The rejected call, the last top-level instruction of
  /// `43o1RU3b7uvPRQKhQGXhJ7mefJFEdAHQ27Lga5Mujb2ijJMVAfGXQb218YKZD7rtsqBJMVSwSFWBZcsyTtLerfA8`
  /// (slot 465213541), which failed with `AnchorError caused by account: dispensing_custody. Error
  /// Code: AccountNotInitialized`: its data, the discriminator alone, its 14 accounts in order, and
  /// the indexes its message made writable.
  private static final byte[] REJECTED_DATA = HEX.parseHex("7a822112d32ca13a");
  private static final List<String> REJECTED_ACCOUNTS = List.of(
      "PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu", // keeper: none, so the program id
      "3RMzZL2oEcUDtv5igCXUQhCpYU2dEJtQZSWMpor3ptLD", // owner
      "81A85hgTNgHKnPyo7E5JKgNgX88JkzDVNqDcoN99tSqT", // owner_ata
      "5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq", // pool
      "3qDtmTLacMK6zdn8phdC2tsDZbw3CdVzUestYRQ6CLun", // position_request
      "FNGPxt9Zv7D9qoBszdzoPojd2xfNdLbSkd6qTJygWNus", // position_request_ata
      "J4Sd5oi45v2RWkmUQsLU9hRoAHDWgADmpSjs1HLsYq4D", // position
      "7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz", // custody, SOL's
      "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", // mint, USDC
      "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", // token_program
      "11111111111111111111111111111111", // system_program
      "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL", // associated_token_program
      "37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN", // event_authority, read as dispensing_custody
      "PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu" // program
  );
  private static final Set<Integer> REJECTED_WRITABLE = Set.of(1, 2, 3, 4, 5, 7);
  private static final PublicKey REJECTED_OWNER = key("3RMzZL2oEcUDtv5igCXUQhCpYU2dEJtQZSWMpor3ptLD");
  private static final PublicKey REJECTED_REQUEST = key("3qDtmTLacMK6zdn8phdC2tsDZbw3CdVzUestYRQ6CLun");
  private static final PublicKey REJECTED_POSITION = key("J4Sd5oi45v2RWkmUQsLU9hRoAHDWgADmpSjs1HLsYq4D");

  /// An accepted call, the last top-level instruction of
  /// `4tLUSowpbkp64YkVrA5v1btgznM6pzodtKVrKwjsihvXgXgyoJa7zjJsZfZ45qfWp8XCecg7u5rBXMgNEfNbh1C5`
  /// (slot 465221792), an owner closing their own request: its data, the discriminator alone, its
  /// 13 accounts in order, which are `close_position_request3`'s without `custody`, and the indexes
  /// its message made writable.
  private static final byte[] ACCEPTED_DATA = HEX.parseHex("7944a21cd82fc842");
  private static final List<String> ACCEPTED_ACCOUNTS = List.of(
      "PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu", // keeper: none, so the program id
      "6dikvkdrX9ApHn2VowSxL2hKXzoVhukdz8Yefrdhzgrh", // owner
      "GgKYK176vgzQ3xiJAuZigSs2eGu1tRrQHVQzrCiopn5J", // owner_ata
      "5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq", // pool
      "DSjBrhoGhj3h26YHhMkB68GfFizB9qT7CUPqyny59zSE", // position_request
      "EviDHvCaVonjVTHvikePWQH1UQMdQ9t1Cpo5rxLkXwg7", // position_request_ata
      "2ZWvQouz9frxMYnesFEzEgnwsNjytcUd32KS5diwxBxh", // position
      "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", // mint, USDC
      "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", // token_program
      "11111111111111111111111111111111", // system_program
      "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL", // associated_token_program
      "37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN", // event_authority
      "PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu" // program
  );
  private static final Set<Integer> ACCEPTED_WRITABLE = Set.of(1, 2, 3, 4, 5);
  private static final PublicKey ACCEPTED_OWNER = key("6dikvkdrX9ApHn2VowSxL2hKXzoVhukdz8Yefrdhzgrh");
  private static final PublicKey ACCEPTED_REQUEST = key("DSjBrhoGhj3h26YHhMkB68GfFizB9qT7CUPqyny59zSE");
  private static final PublicKey ACCEPTED_POSITION = key("2ZWvQouz9frxMYnesFEzEgnwsNjytcUd32KS5diwxBxh");

  /// What the tripwire says when it breaks.
  private static final String STALE_IDL = "the deployed program reads index 12 of close_position_request3 as "
      + "dispensing_custody, and the generated builder put the event authority there, so every call failed 3012 "
      + "AccountNotInitialized (live at slot 465213541, simulated at 465223978 and 465277435). The builder has "
      + "changed: if a corrected IDL was regenerated, rewrite ClosePositionRequest3Tests to pin the corrected "
      + "15-account order against a live call, and drop the stale-IDL warning from its javadoc";

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static List<String> base58(final List<AccountMeta> keys) {
    return keys.stream().map(meta -> meta.publicKey().toBase58()).toList();
  }

  private static PublicKey usdcAta(final PublicKey owner) {
    return PublicKey.findProgramAddress(
        List.of(owner.toByteArray(), TOKEN_PROGRAM.toByteArray(), USDC.toByteArray()), ATA_PROGRAM
    ).publicKey();
  }

  private static PublicKey eventAuthority() {
    return PublicKey.findProgramAddress(List.of("__event_authority".getBytes(StandardCharsets.US_ASCII)), PERP)
        .publicKey();
  }

  /// The rejected call's list as a caller builds it, from the request, its owner, position, custody
  /// and mint, with the ATAs and the event authority derived.
  private static List<AccountMeta> rejectedCallKeys() {
    return PerpetualsProgram.closePositionRequest3Keys(
        AccountMeta.createInvoked(PERP),
        null,
        REJECTED_OWNER,
        usdcAta(REJECTED_OWNER),
        POOL,
        REJECTED_REQUEST,
        usdcAta(REJECTED_REQUEST),
        REJECTED_POSITION,
        SOL_CUSTODY,
        USDC,
        TOKEN_PROGRAM,
        SYSTEM_PROGRAM,
        ATA_PROGRAM,
        eventAuthority(),
        PERP
    );
  }

  /// Compares the flags from index 1 on, because index 0 is `keeper`, which neither live call
  /// passes: the generated builders then put the program id there writable, where both live
  /// messages carry it read-only. The builders mark no account a signer, as the IDL makes none but
  /// `keeper` one; the owner at index 1 signs each live message as its fee payer.
  private static void assertFlagsFromIndexOne(final Set<Integer> liveWritable, final List<AccountMeta> keys) {
    for (int i = 1; i < keys.size(); ++i) {
      final var meta = keys.get(i);
      assertEquals(liveWritable.contains(i), meta.write(), "writable at index " + i);
      assertFalse(meta.signer(), "signer at index " + i);
    }
  }

  /// The derived accounts are checked against the live call before the builder is: then a failure
  /// below is the builder's.
  @Test
  void theGeneratedBuilderReproducesTheRejectedLiveCall() {
    assertEquals(key(REJECTED_ACCOUNTS.get(2)), usdcAta(REJECTED_OWNER), "the owner's USDC ATA");
    assertEquals(key(REJECTED_ACCOUNTS.get(5)), usdcAta(REJECTED_REQUEST), "the request's USDC ATA");
    assertEquals(EVENT_AUTHORITY, eventAuthority(), "the program's event authority");

    final var keys = rejectedCallKeys();
    assertEquals(REJECTED_ACCOUNTS, base58(keys), "the accounts are the ones the program rejected");
    assertFlagsFromIndexOne(REJECTED_WRITABLE, keys);

    final var ix = PerpetualsProgram.closePositionRequest3(AccountMeta.createInvoked(PERP), keys);
    assertArrayEquals(REJECTED_DATA, ix.data(), "the discriminator, and no arguments");
  }

  /// The tripwire: the shortfall the class javadoc describes, stated as the builder's output.
  @Test
  void theBuilderIsStillOneAccountShort() {
    final var keys = rejectedCallKeys();
    assertEquals(14, keys.size(), STALE_IDL);
    assertEquals(EVENT_AUTHORITY, keys.get(12).publicKey(), STALE_IDL);
  }

  @Test
  void closePositionRequest2ReproducesAnAcceptedLiveCall() {
    assertEquals(key(ACCEPTED_ACCOUNTS.get(2)), usdcAta(ACCEPTED_OWNER), "the owner's USDC ATA");
    assertEquals(key(ACCEPTED_ACCOUNTS.get(5)), usdcAta(ACCEPTED_REQUEST), "the request's USDC ATA");

    final var keys = PerpetualsProgram.closePositionRequest2Keys(
        AccountMeta.createInvoked(PERP),
        null,
        ACCEPTED_OWNER,
        usdcAta(ACCEPTED_OWNER),
        POOL,
        ACCEPTED_REQUEST,
        usdcAta(ACCEPTED_REQUEST),
        ACCEPTED_POSITION,
        USDC,
        TOKEN_PROGRAM,
        SYSTEM_PROGRAM,
        ATA_PROGRAM,
        eventAuthority(),
        PERP
    );
    assertEquals(ACCEPTED_ACCOUNTS, base58(keys), "the accounts are the ones the program accepted");
    assertFlagsFromIndexOne(ACCEPTED_WRITABLE, keys);

    final var ix = PerpetualsProgram.closePositionRequest2(AccountMeta.createInvoked(PERP), keys);
    assertArrayEquals(ACCEPTED_DATA, ix.data(), "the discriminator, and no arguments");
  }
}
