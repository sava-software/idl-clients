package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram.CreateNftCollateralOfferIxData;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram.RepayTokenLoanIxData;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftCollateral;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftCollateralAsset;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftCollateralOfferParams;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftPrincipalOfferParams;
import software.sava.idl.clients.jupiter.offerbook.gen.types.TokenCollateralOfferParams;
import software.sava.idl.clients.jupiter.offerbook.gen.types.TokenPrincipalOfferParams;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/// Jupiter Offerbook's upgrade at slot 456774686 made six instructions read one Borsh `Option`
/// more than the program-metadata IDL then declared: the four offer creators a `whitelisted_taker`
/// (`Option<Pubkey>`) at the end of their offer parameters, where `None` is one zero byte and
/// `Some` carries 32 more, and the two repays an `amount` (`Option<u64>`) right after the
/// discriminator, `Some` carrying 8 more. The IDL caught up when a Squads vault transaction
/// rewrote that account at slot 464256975. Every builder generated from the earlier document for
/// these six sent no Option tag at all, one byte short of a live `None` call, and the program
/// rejected it with `InstructionDidNotDeserialize` (102), save the NFT creators' ClassicNft whose
/// mint starts with a one-pubkey variant tag (accepted, mis-parsed).
///
/// These pin the regenerated builders to what the program reads, as
/// [EscrowProgrammableNftWithdrawTests] does for the withdraw changed at slot 456376677: three
/// against calls the program accepted, byte for byte, and the other three against the widths the
/// program was simulated to read. Each builder is fed values decoded by hand from the live bytes,
/// so the builders and the generated readers are each checked against an independent oracle. The
/// `Some` cases, which no live call in hand carries, are pinned to Borsh written out by hand.
final class OfferbookOptionArgumentTests {

  private static final HexFormat HEX = HexFormat.of();
  private static final String OFFERBOOK = "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie";

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static List<String> keysOf(final Instruction ix) {
    return ix.accounts().stream().map(meta -> meta.publicKey().toBase58()).toList();
  }

  /// Each account's flags, `W` or `R` and then `S` for a signer, in order. The live strings
  /// below are the flags the runtime handed the program, which come from the transaction's
  /// message header, since an instruction carries account indices only. In each of these
  /// transactions, another instruction shares the signer, and in the repay
  /// `borrower_collateral_escrow` too, so those flags are not the call's alone: a later IDL that
  /// drops a write there wants review, not acceptance.
  private static String flagsOf(final Instruction ix) {
    return ix.accounts().stream()
        .map(meta -> (meta.write() ? "W" : "R") + (meta.signer() ? "S" : ""))
        .collect(Collectors.joining(" "));
  }

  /// A post-upgrade `create_nft_collateral_offer` the program accepted, top-level in
  /// `21Taw4JDNjsZLw7mYUbaTkYSQv6WL2cXdomkxueNXrJY59pcmdoUSDWKhhUTDZLNr9ajQ7WyZbnLR8ZU5eSGd7yN`
  /// (slot 456926828): 63 bytes, the discriminator, the 22-byte offer parameters ending in the
  /// `None` tag for the taker at offset 29, then the collateral, a `CoreNft` tag and its asset.
  private static final byte[] NFT_COLLATERAL_LIVE = HEX.parseHex(
      "302234bd510ba908c0f35e0100000000ac0d0000803a090080f40300" + "00" + "00"
          + "028f0051207939b5e03c9019d1982af92cdf514a4762e2ae46aa9a1caef1880c95");
  private static final List<String> NFT_COLLATERAL_ACCOUNTS = List.of(
      "3PeX21jccWxn1qovPCABgAxNVc8pB6sMgw3Ub3L81oJf",
      "9Gmrnn6kNkdMBHxyrAbtfwoXR6s8yk6o7KA5htFEWddf",
      "5bjsMDacvtuRjtSD4j4ARsfP6Ras3dywyyuvsbbnXNQk",
      "5NmQn6P85HtsHJuYKhtyiRsixtDXouzvru4PJ58U5vZi",
      "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie",
      "11111111111111111111111111111111",
      "4sZptGwLvk2ywxa2E8UosgogwrjTxjPfFAK7o2rcKHb9",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie"
  );
  private static final String NFT_COLLATERAL_FLAGS = "WS W R W R R R R R";
  /// That call's parameters and collateral asset, decoded by hand from its bytes.
  private static final NftCollateralOfferParams NFT_COLLATERAL_PARAMS =
      new NftCollateralOfferParams(23_000_000L, 3_500L, 604_800L, 259_200L, false, null);
  private static final PublicKey CORE_NFT_ASSET = key("AdDahehyHWbgfLQ9xGNfhrPBR4Yf8uiuCobTVTm92GX6");

  @Test
  void createNftCollateralOfferReproducesTheLiveCall() {
    final var collateral = new NftCollateralAsset.CoreNft(CORE_NFT_ASSET);
    final var ix = OfferbookProgram.createNftCollateralOffer(
        AccountMeta.createInvoked(key(NFT_COLLATERAL_ACCOUNTS.get(8))),
        SolanaAccounts.MAIN_NET,
        key(NFT_COLLATERAL_ACCOUNTS.get(0)), key(NFT_COLLATERAL_ACCOUNTS.get(1)), key(NFT_COLLATERAL_ACCOUNTS.get(2)),
        key(NFT_COLLATERAL_ACCOUNTS.get(3)), key(NFT_COLLATERAL_ACCOUNTS.get(4)), key(NFT_COLLATERAL_ACCOUNTS.get(5)),
        key(NFT_COLLATERAL_ACCOUNTS.get(7)), key(NFT_COLLATERAL_ACCOUNTS.get(8)),
        NFT_COLLATERAL_PARAMS, collateral
    );
    assertEquals(NFT_COLLATERAL_ACCOUNTS, keysOf(ix), "the accounts are the ones the program accepted");
    assertEquals(NFT_COLLATERAL_FLAGS, flagsOf(ix), "with the flags the program received");
    assertArrayEquals(NFT_COLLATERAL_LIVE, ix.data(), "the parameters, the None tag for the taker, then the collateral");

    assertEquals(NFT_COLLATERAL_PARAMS, NftCollateralOfferParams.read(NFT_COLLATERAL_LIVE, 8),
        "the generated reader decodes the live parameters, None taker included");
    assertEquals(collateral, NftCollateralAsset.read(NFT_COLLATERAL_LIVE, 8 + NFT_COLLATERAL_PARAMS.l()),
        "and finds the collateral after them");
  }

  /// A post-upgrade `create_token_principal_offer` the program accepted, top-level in
  /// `1ARLgPBaqXUcu6MaWRERxB6orCNbjgmtszi16vpymhaF8xwwUN49NPVkK2BQnyhcxU2gBRAgRhtVsT5fZRgYfo2`
  /// (slot 456958956): 47 bytes, the discriminator and the 39-byte offer parameters, which end in
  /// the `None` tag for the taker.
  private static final byte[] TOKEN_PRINCIPAL_LIVE = HEX.parseHex(
      "4e09458ebd40ab0d6c6c3209000000008e68fb5b000000001c2500008051010000a30200018096980000000000" + "00" + "00");
  private static final List<String> TOKEN_PRINCIPAL_ACCOUNTS = List.of(
      "9HjUbxvo5HetgfiJnc2cmPHT9aqZdBhXsgo9xAC65UvT",
      "FKDtmYSbyQzT2aQxEEcvyp1YeGhjkWGHjpcGdCM32fpx",
      "5bjsMDacvtuRjtSD4j4ARsfP6Ras3dywyyuvsbbnXNQk",
      "9Zw49zY5siyAVhWUFoBK1ioiW928ByPvxdYuMtsW1UwB",
      "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
      "Dz9mQ9NzkBcCsuGPFJ3r1bS4wgqKMHBPiVuniW8Mbonk",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie",
      "11111111111111111111111111111111",
      "4sZptGwLvk2ywxa2E8UosgogwrjTxjPfFAK7o2rcKHb9",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie"
  );
  private static final String TOKEN_PRINCIPAL_FLAGS = "WS W R W R R R R R R";
  /// That call's parameters, decoded by hand from its bytes.
  private static final TokenPrincipalOfferParams TOKEN_PRINCIPAL_PARAMS = new TokenPrincipalOfferParams(
      154_299_500L, 1_543_202_958L, 9_500L, 86_400L, 172_800L, true, 10_000_000L, false, null);

  @Test
  void createTokenPrincipalOfferReproducesTheLiveCall() {
    final var ix = OfferbookProgram.createTokenPrincipalOffer(
        AccountMeta.createInvoked(key(TOKEN_PRINCIPAL_ACCOUNTS.get(9))),
        SolanaAccounts.MAIN_NET,
        key(TOKEN_PRINCIPAL_ACCOUNTS.get(0)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(1)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(2)),
        key(TOKEN_PRINCIPAL_ACCOUNTS.get(3)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(4)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(5)),
        key(TOKEN_PRINCIPAL_ACCOUNTS.get(6)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(8)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(9)),
        TOKEN_PRINCIPAL_PARAMS
    );
    assertEquals(TOKEN_PRINCIPAL_ACCOUNTS, keysOf(ix), "the accounts are the ones the program accepted");
    assertEquals(TOKEN_PRINCIPAL_FLAGS, flagsOf(ix), "with the flags the program received");
    assertArrayEquals(TOKEN_PRINCIPAL_LIVE, ix.data(), "the parameters, ending in the None tag for the taker");

    assertEquals(TOKEN_PRINCIPAL_PARAMS, TokenPrincipalOfferParams.read(TOKEN_PRINCIPAL_LIVE, 8),
        "and the generated reader decodes them, None taker included");
  }

  /// No live call in hand names a taker or tells `allow_extend` from `whitelisted_taker`: each sets
  /// the flag false and the taker `None`, both one zero byte. So a private offer is pinned to Borsh
  /// written out by hand in the order the republished IDL declares: the live call's bytes up to
  /// `allow_extend`, the flag set, the tag `01` and the taker's 32 bytes, then the NFT collateral.
  @Test
  void aPrivateOfferCarriesItsTakerAfterAllowExtend() {
    final var taker = key("9HjUbxvo5HetgfiJnc2cmPHT9aqZdBhXsgo9xAC65UvT");
    final var takerHex = "7b26d0e6ca74c91741e6f2f900c9656654f2501320ecd0df2a9a97b1c6702078";
    final var program = AccountMeta.createInvoked(key(OFFERBOOK));

    final var tokenExpected = HEX.parseHex(
        "4e09458ebd40ab0d6c6c3209000000008e68fb5b000000001c2500008051010000a30200018096980000000000"
            + "01" + "01" + takerHex);
    final var t = TOKEN_PRINCIPAL_PARAMS;
    final var tokenParams = new TokenPrincipalOfferParams(
        t.principalAmount(), t.collateralAmount(), t.apy(), t.duration(), t.expiry(),
        t.allowPartialFill(), t.minFillAmount(), true, taker
    );
    assertArrayEquals(tokenExpected, OfferbookProgram.createTokenPrincipalOffer(program, List.of(), tokenParams).data());
    assertEquals(tokenParams, TokenPrincipalOfferParams.read(tokenExpected, 8));

    final var nftExpected = HEX.parseHex(
        "302234bd510ba908c0f35e0100000000ac0d0000803a090080f40300"
            + "01" + "01" + takerHex
            + "028f0051207939b5e03c9019d1982af92cdf514a4762e2ae46aa9a1caef1880c95");
    final var n = NFT_COLLATERAL_PARAMS;
    final var nftParams = new NftCollateralOfferParams(n.principalAmount(), n.apy(), n.duration(), n.expiry(), true, taker);
    final var collateral = new NftCollateralAsset.CoreNft(CORE_NFT_ASSET);
    assertArrayEquals(nftExpected, OfferbookProgram.createNftCollateralOffer(program, List.of(), nftParams, collateral).data());
    final var nftDecoded = CreateNftCollateralOfferIxData.read(nftExpected, 0);
    assertEquals(nftParams, nftDecoded.offerParams());
    assertEquals(collateral, nftDecoded.collateral(), "the collateral is read after the taker's 33 bytes");
  }

  /// A post-upgrade `repay_token_loan` the program accepted, top-level in
  /// `2cFikMDBkKG9QAgFTjM5SNkzK2GaqcrdU1xraGDJmHKQQd586Xn77mfzWTKkqqNtFTEapQEwdB97U8HzyfMJo2ow`
  /// (slot 456826752): the discriminator and then the `None` tag, with its 17 accounts in order.
  private static final byte[] REPAY_TOKEN_LIVE = HEX.parseHex("d032d027bd9919fb" + "00");
  private static final List<String> REPAY_TOKEN_ACCOUNTS = List.of(
      "CJ1gd8RAMfbrCqEGjU4yJ7K64N3ecAho1PCtuCviCc8W",
      "BHZBVGeYf7EcVeniMS2QVCdoBPnsPXVTTUFKLtbeheN",
      "EqSAduwC1KFvC3eY3QuaFQTiLufUuaxxaSMWGEFQzLXs",
      "BCUu4b1gPjJqQ5bNnKdGxBKVEJUuakJf2yVrr8zH3M2k",
      "CW2EWehum9HSqWwWbFFYs7KgLkLeSXJyR5okv7Sn3KY5",
      "6uinkmtbsTKJxHDtJ1svZJLWWVUQ3B9tEVt4o3sDn7ad",
      "5bjsMDacvtuRjtSD4j4ARsfP6Ras3dywyyuvsbbnXNQk",
      "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
      "Ce2gx9KGXJ6C9Mp5b5x1sn9Mg87JwEbrQby4Zqo3pump",
      "66PEx6yzENEdhMnXL2x7F1pcAoCaSPekQDMydkZaivHv",
      "FQw7viMe1y4GoHrVf2yAfQFw6cdw3Yw2sJSt4eCg28KD",
      "8ZmH6iv7h9G1yuqF7ppMktyoLf38DWz6AiSWi65Yy6Gd",
      "9dQQ6bsqVRur6GgRZ38kQqs8CrLCwLuACdWCtvGyq3cd",
      "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
      "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
      "4sZptGwLvk2ywxa2E8UosgogwrjTxjPfFAK7o2rcKHb9",
      "offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie"
  );
  private static final String REPAY_TOKEN_FLAGS = "WS R W R W W R R R W W W W R R R R";

  @Test
  void repayTokenLoanReproducesTheLiveCall() {
    final var a = REPAY_TOKEN_ACCOUNTS;
    final var ix = OfferbookProgram.repayTokenLoan(
        AccountMeta.createInvoked(key(a.get(16))),
        key(a.get(0)), key(a.get(1)), key(a.get(2)), key(a.get(3)), key(a.get(4)), key(a.get(5)), key(a.get(6)),
        key(a.get(7)), key(a.get(8)), key(a.get(9)), key(a.get(10)), key(a.get(11)), key(a.get(12)), key(a.get(13)),
        key(a.get(14)), key(a.get(15)), key(a.get(16)),
        OptionalLong.empty()
    );
    assertEquals(REPAY_TOKEN_ACCOUNTS, keysOf(ix), "the accounts are the ones the program accepted");
    assertEquals(REPAY_TOKEN_FLAGS, flagsOf(ix), "with the flags the program received");
    assertArrayEquals(REPAY_TOKEN_LIVE, ix.data(), "the discriminator, then the Option tag for None, which settles the loan");

    assertEquals(OptionalLong.empty(), RepayTokenLoanIxData.read(REPAY_TOKEN_LIVE, 0).amount(),
        "and the generated reader decodes the live call's amount as None");
  }

  /// The review of the 456774686 deploy saw one accepted `repay_token_loan` carrying
  /// `Some(2000000)`, a partial repayment. Its bytes are Borsh's: the tag `01`, then the amount as
  /// a little-endian `u64`, written out here by hand.
  @Test
  void repayTokenLoanEncodesAPartialRepaymentAsSomeAmount() {
    final var expected = HEX.parseHex("d032d027bd9919fb" + "01" + "80841e0000000000");
    final var ix = OfferbookProgram.repayTokenLoan(
        AccountMeta.createInvoked(key(OFFERBOOK)), List.of(), OptionalLong.of(2_000_000L)
    );
    assertArrayEquals(expected, ix.data());
    assertEquals(OptionalLong.of(2_000_000L), RepayTokenLoanIxData.read(expected, 0).amount());
  }

  /// The sample held no accepted call to the other three. The census of the 456774686 deploy found
  /// two each of `create_token_collateral_offer` (47 bytes) and `repay_non_fungible_loan` (9
  /// bytes) and none of `create_nft_principal_offer`, and simulation read the same shape for all
  /// three: 63 bytes and not 62, 47 and not 46, 9 and not 8, each with the `None` byte where its
  /// sibling above has it (slots 456975097, 456975110 and 456975248 for the IDL's widths,
  /// 456976457..456977782 for the ones accepted). Each shares its sibling's argument layout, so
  /// given the sibling's live values it writes the sibling's live bytes after its own
  /// discriminator, which is written out as Anchor derives it, `sha256("global:<name>")[..8]`.
  @Test
  void theOtherThreeBuildersLayOutTheirArgumentsAsTheirSiblingsDo() {
    final var any = key("5bjsMDacvtuRjtSD4j4ARsfP6Ras3dywyyuvsbbnXNQk");
    final var program = AccountMeta.createInvoked(key(OFFERBOOK));

    final var n = NFT_COLLATERAL_PARAMS;
    final var nftPrincipal = OfferbookProgram.createNftPrincipalOffer(
        program, SolanaAccounts.MAIN_NET, any, any, any, any, any, any, any, any,
        new NftPrincipalOfferParams(n.principalAmount(), n.apy(), n.duration(), n.expiry(), n.allowExtend(), n.whitelistedTaker()),
        new NftCollateral.CoreNft(CORE_NFT_ASSET)
    );
    assertSiblingLayout("02245327b1627e4b", 63, NFT_COLLATERAL_LIVE, nftPrincipal);

    final var t = TOKEN_PRINCIPAL_PARAMS;
    final var tokenCollateral = OfferbookProgram.createTokenCollateralOffer(
        program, SolanaAccounts.MAIN_NET, any, any, any, any, any, any, any, any, any,
        new TokenCollateralOfferParams(
            t.principalAmount(), t.collateralAmount(), t.apy(), t.duration(), t.expiry(),
            t.allowPartialFill(), t.minFillAmount(), t.allowExtend(), t.whitelistedTaker()
        )
    );
    assertSiblingLayout("264bb6fead0df029", 47, TOKEN_PRINCIPAL_LIVE, tokenCollateral);

    final var repayNft = OfferbookProgram.repayNonFungibleLoan(
        program, SolanaAccounts.MAIN_NET, any, any, any, any, any, any, any, any, any, any, any, any, any, any,
        OptionalLong.empty()
    );
    assertSiblingLayout("94d30f39ad1b1a31", 9, REPAY_TOKEN_LIVE, repayNft);
  }

  private static void assertSiblingLayout(final String discriminator,
                                          final int simulatedWidth,
                                          final byte[] siblingLive,
                                          final Instruction ix) {
    final byte[] data = ix.data();
    assertEquals(simulatedWidth, data.length, "the width the program was simulated to read");
    assertArrayEquals(HEX.parseHex(discriminator), Arrays.copyOf(data, 8), "Anchor's discriminator for the instruction");
    assertArrayEquals(
        Arrays.copyOfRange(siblingLive, 8, siblingLive.length), Arrays.copyOfRange(data, 8, data.length),
        "the arguments laid out as the sibling's live call has them"
    );
  }
}
