package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookProgram;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftCollateral;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftCollateralAsset;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftCollateralOfferParams;
import software.sava.idl.clients.jupiter.offerbook.gen.types.NftPrincipalOfferParams;
import software.sava.idl.clients.jupiter.offerbook.gen.types.TokenCollateralOfferParams;
import software.sava.idl.clients.jupiter.offerbook.gen.types.TokenPrincipalOfferParams;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Jupiter Offerbook's upgrade at slot 456774686 made six instructions read one Borsh `Option`
/// more than the program-metadata IDL this client is generated from declares (that IDL account was
/// last written at slot 438175753, two deploys earlier). The four offer creators read it at the end
/// of their offer parameters, where `None` is one zero byte and `Some` carries 32 more; the two
/// repays read it right after the discriminator, `Some` carrying 8 more. A call at the IDL's width
/// fails `InstructionDidNotDeserialize` (102) before any account is checked, save the NFT creators'
/// ClassicNft whose mint starts with a one-pubkey variant tag (accepted, mis-parsed); the IDL's width
/// is what every generated builder for these six sends. Live callers send the extra `None` byte;
/// one payer lost 170 transactions at the old width before moving at slot 456873350 (simulated at
/// slots 456975086..456979733; every transaction since the deploy up to slot 456973818 decoded).
///
/// A published IDL's field list is upstream's to correct, not a local patch's, so these pin the
/// shortfall rather than close it, as [EscrowProgrammableNftWithdrawTests] does for the withdraw
/// since slot 456376677. When Jupiter republishes the IDL the regenerated builders change and these
/// tests fail: rewrite them then to assert the full encoding.
final class OfferbookOptionArgumentTests {

  private static final HexFormat HEX = HexFormat.of();

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static List<String> keysOf(final Instruction ix) {
    return ix.accounts().stream().map(meta -> meta.publicKey().toBase58()).toList();
  }

  /// A post-upgrade `create_nft_collateral_offer` the program accepted, top-level in
  /// `21Taw4JDNjsZLw7mYUbaTkYSQv6WL2cXdomkxueNXrJY59pcmdoUSDWKhhUTDZLNr9ajQ7WyZbnLR8ZU5eSGd7yN`
  /// (slot 456926828): 63 bytes, the 21-byte offer parameters, then the `None` byte the IDL lacks at
  /// offset 29, then the collateral, a `CoreNft` tag and its asset.
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

  @Test
  void createNftCollateralOfferStopsOneByteShortAfterItsParameters() {
    final var live = NFT_COLLATERAL_LIVE;
    assertEquals(63, live.length);
    final var params = NftCollateralOfferParams.read(live, 8);
    assertEquals(23_000_000L, params.principalAmount(), "the live parameters decode as the IDL declares them");
    assertFalse(params.allowExtend());
    final var collateral = NftCollateralAsset.read(live, 30);
    assertInstanceOf(NftCollateralAsset.CoreNft.class, collateral);

    final var ix = OfferbookProgram.createNftCollateralOffer(
        AccountMeta.createInvoked(key(NFT_COLLATERAL_ACCOUNTS.get(8))),
        SolanaAccounts.MAIN_NET,
        key(NFT_COLLATERAL_ACCOUNTS.get(0)), key(NFT_COLLATERAL_ACCOUNTS.get(1)), key(NFT_COLLATERAL_ACCOUNTS.get(2)),
        key(NFT_COLLATERAL_ACCOUNTS.get(3)), key(NFT_COLLATERAL_ACCOUNTS.get(4)), key(NFT_COLLATERAL_ACCOUNTS.get(5)),
        key(NFT_COLLATERAL_ACCOUNTS.get(7)), key(NFT_COLLATERAL_ACCOUNTS.get(8)),
        params, collateral
    );
    assertEquals(NFT_COLLATERAL_ACCOUNTS, keysOf(ix), "the accounts are the ones the program accepted");

    final byte[] data = ix.data();
    assertEquals(live.length - 1, data.length, "the shortfall is exactly one byte");
    assertArrayEquals(Arrays.copyOfRange(live, 0, 29), Arrays.copyOfRange(data, 0, 29), "discriminator and parameters agree");
    assertEquals(0, live[29], "the byte the builder lacks is the Option tag for None, after the parameters");
    assertArrayEquals(Arrays.copyOfRange(live, 30, 63), Arrays.copyOfRange(data, 29, 62), "and the collateral follows it");
  }

  /// A post-upgrade `create_token_principal_offer` the program accepted, top-level in
  /// `1ARLgPBaqXUcu6MaWRERxB6orCNbjgmtszi16vpymhaF8xwwUN49NPVkK2BQnyhcxU2gBRAgRhtVsT5fZRgYfo2`
  /// (slot 456958956): 47 bytes, the 38-byte offer parameters and then the `None` byte.
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

  @Test
  void createTokenPrincipalOfferStopsOneByteShortAtTheEnd() {
    final var live = TOKEN_PRINCIPAL_LIVE;
    assertEquals(47, live.length);
    final var params = TokenPrincipalOfferParams.read(live, 8);
    assertEquals(10_000_000L, params.minFillAmount(), "the live parameters decode as the IDL declares them");
    assertTrue(params.allowPartialFill());

    final var ix = OfferbookProgram.createTokenPrincipalOffer(
        AccountMeta.createInvoked(key(TOKEN_PRINCIPAL_ACCOUNTS.get(9))),
        SolanaAccounts.MAIN_NET,
        key(TOKEN_PRINCIPAL_ACCOUNTS.get(0)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(1)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(2)),
        key(TOKEN_PRINCIPAL_ACCOUNTS.get(3)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(4)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(5)),
        key(TOKEN_PRINCIPAL_ACCOUNTS.get(6)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(8)), key(TOKEN_PRINCIPAL_ACCOUNTS.get(9)),
        params
    );
    assertEquals(TOKEN_PRINCIPAL_ACCOUNTS, keysOf(ix), "the accounts are the ones the program accepted");

    final byte[] data = ix.data();
    assertEquals(live.length - 1, data.length, "the shortfall is exactly one byte");
    assertArrayEquals(Arrays.copyOf(live, data.length), data, "the live call leads with the same bytes");
    assertEquals(0, live[data.length], "and ends with the Option tag for None");
  }

  /// A post-upgrade `repay_token_loan` the program accepted, top-level in
  /// `2cFikMDBkKG9QAgFTjM5SNkzK2GaqcrdU1xraGDJmHKQQd586Xn77mfzWTKkqqNtFTEapQEwdB97U8HzyfMJo2ow`
  /// (slot 456826752): the discriminator and then the `None` byte, with its 17 accounts in order.
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

  @Test
  void repayTokenLoanStopsOneByteShortAfterTheDiscriminator() {
    final var a = REPAY_TOKEN_ACCOUNTS;
    final var ix = OfferbookProgram.repayTokenLoan(
        AccountMeta.createInvoked(key(a.get(16))),
        key(a.get(0)), key(a.get(1)), key(a.get(2)), key(a.get(3)), key(a.get(4)), key(a.get(5)), key(a.get(6)),
        key(a.get(7)), key(a.get(8)), key(a.get(9)), key(a.get(10)), key(a.get(11)), key(a.get(12)), key(a.get(13)),
        key(a.get(14)), key(a.get(15)), key(a.get(16))
    );
    assertEquals(REPAY_TOKEN_ACCOUNTS, keysOf(ix), "the accounts are the ones the program accepted");

    final byte[] data = ix.data();
    assertArrayEquals(OfferbookProgram.REPAY_TOKEN_LOAN_DISCRIMINATOR.data(), data, "what the IDL declares: the discriminator alone");
    assertEquals(REPAY_TOKEN_LIVE.length - 1, data.length, "the shortfall is exactly one byte");
    assertArrayEquals(Arrays.copyOf(REPAY_TOKEN_LIVE, data.length), data);
    assertEquals(0, REPAY_TOKEN_LIVE[data.length], "and the live call ends with the Option tag for None");
  }

  /// No sampled transaction called the other three since the deploy; the simulation did, with
  /// zero accounts, and read the same shape: `create_nft_principal_offer` deserializes at 63 bytes
  /// and not 62, `create_token_collateral_offer` at 47 and not 46, `repay_non_fungible_loan` at 9 and
  /// not 8, each with the `None` byte where its sibling above has it (slots 456975097, 456975110,
  /// 456975248 for the IDL widths, 456976457..456977782 for the accepted ones). The builders stay at
  /// the IDL's widths, so each falls one byte short in the same way.
  @Test
  void theOtherThreeBuildersStopAtTheIdlsWidth() {
    final var any = key("5bjsMDacvtuRjtSD4j4ARsfP6Ras3dywyyuvsbbnXNQk");
    final var program = AccountMeta.createInvoked(key("offerbkFMvVfpQhL8ZQ5iromnjct5rz3r52B9ewu3ie"));

    final var nftPrincipal = OfferbookProgram.createNftPrincipalOffer(
        program, SolanaAccounts.MAIN_NET, any, any, any, any, any, any, any, any,
        new NftPrincipalOfferParams(1, 2, 3, 4, false), new NftCollateral.CoreNft(any)
    );
    assertEquals(62, nftPrincipal.data().length, "8 + 21 parameters + 33 collateral; the program reads 63");

    final var tokenCollateral = OfferbookProgram.createTokenCollateralOffer(
        program, SolanaAccounts.MAIN_NET, any, any, any, any, any, any, any, any, any,
        new TokenCollateralOfferParams(1, 2, 3, 4, 5, false, 6, false)
    );
    assertEquals(46, tokenCollateral.data().length, "8 + 38 parameters; the program reads 47");

    final var repayNft = OfferbookProgram.repayNonFungibleLoan(
        program, SolanaAccounts.MAIN_NET, any, any, any, any, any, any, any, any, any, any, any, any, any, any
    );
    assertEquals(8, repayNft.data().length, "the discriminator alone; the program reads 9");
  }
}
