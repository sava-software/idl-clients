package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.orca.whirlpools.gen.WhirlpoolPDAs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/// The on-chain program derives `tick_array` and `bundled_position` PDAs from
/// the *decimal string* of the integer seed, which the anchor IDL cannot
/// express — so these must go through `OrcaUtil`, or the generated
/// `WhirlpoolPDAs` helpers with an explicit `SerDeUtil.asciiSeed`. A pool
/// initialized from an adaptive fee tier is the exception: it hashes the tier's
/// index as a little-endian u16. Expected addresses are mainnet accounts owned by
/// the whirlpool program.
final class OrcaPDATests {

  private static final PublicKey WHIRLPOOL_PROGRAM = PublicKey
      .fromBase58Encoded("whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc");

  @Test
  void tickArrayPDA() {
    final var whirlpool = PublicKey.fromBase58Encoded("4fuUiYxTQ6QCrdSq9ouBYcTM7bqSwYTSyLueGZLTy4T4");
    final var pda = OrcaUtil.tickArrayPDA(WHIRLPOOL_PROGRAM, whirlpool, 0);
    assertEquals(
        PublicKey.fromBase58Encoded("FqFkv2xNNCUyx1RYV61pGZ9AMzGfgcD8uXC9zCF5JKnR"),
        pda.publicKey()
    );
    assertEquals(255, pda.nonce());
    // The generated helper now takes raw seed bytes; little-endian encoding
    // (the historical generated behavior) derives a different, non-existent
    // address, while the decimal-string seed matches OrcaUtil.
    assertNotEquals(
        pda.publicKey(),
        WhirlpoolPDAs.tickArrayPDA(WHIRLPOOL_PROGRAM, whirlpool, SerDeUtil.int32LESeed(0)).publicKey()
    );
    assertEquals(
        pda.publicKey(),
        WhirlpoolPDAs.tickArrayPDA(WHIRLPOOL_PROGRAM, whirlpool, SerDeUtil.asciiSeed(0)).publicKey()
    );
  }

  @Test
  void tickArrayPDANegativeStartTickIndex() {
    // SOL/USDC tick-spacing-64 pool; both arrays exist on mainnet.
    final var whirlpool = PublicKey.fromBase58Encoded("HJPjoWUrhoZzkNfRpHuieeFk9WcZWjwy6PBjZ81ngndJ");
    assertEquals(
        PublicKey.fromBase58Encoded("CEstjhG1v4nUgvGDyFruYEbJ18X8XeN4sX1WFCLt4D5c"),
        OrcaUtil.tickArrayPDA(WHIRLPOOL_PROGRAM, whirlpool, -22528).publicKey()
    );
    assertEquals(
        PublicKey.fromBase58Encoded("A2W6hiA2nf16iqtbZt9vX8FJbiXjv3DBUG3DgTja61HT"),
        OrcaUtil.tickArrayPDA(WHIRLPOOL_PROGRAM, whirlpool, -28160).publicKey()
    );
  }

  @Test
  void bundledPositionPDA() {
    final var positionBundleMint = PublicKey.fromBase58Encoded("4fuUiYxTQ6QCrdSq9ouBYcTM7bqSwYTSyLueGZLTy4T4");
    assertEquals(
        PublicKey.fromBase58Encoded("8gSNFDVToCM3kZypL7i1k5jSar6YDd9jTXTaBXCd1MgR"),
        OrcaUtil.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, 0).publicKey()
    );
    assertEquals(
        PublicKey.fromBase58Encoded("E2M8XAWVQ9AnA7dakSVDJb18Doa32UiERuLr47DCERHj"),
        OrcaUtil.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, 255).publicKey()
    );
    assertNotEquals(
        OrcaUtil.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, 0).publicKey(),
        WhirlpoolPDAs.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, SerDeUtil.int16LESeed(0)).publicKey()
    );
    assertEquals(
        OrcaUtil.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, 0).publicKey(),
        WhirlpoolPDAs.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, SerDeUtil.asciiSeed(0)).publicKey()
    );
    assertThrows(
        IllegalArgumentException.class,
        () -> OrcaUtil.bundledPositionPDA(WHIRLPOOL_PROGRAM, positionBundleMint, OrcaUtil.POSITION_BUNDLE_SIZE)
    );
  }

  /// `initialize_pool_with_adaptive_fee.rs:40` seeds the pool with
  /// `adaptive_fee_tier.fee_tier_index.to_le_bytes()`, the two bytes the pool keeps as
  /// `fee_tier_index_seed`. Each pool below was read at slot 465250444 with its config, mints,
  /// seed and stored bump.
  @Test
  void whirlpoolPDAOfAnAdaptiveFeeTier() {
    assertAdaptiveFeePool("DZM8vQrdz8m895ariTrJNHjsGAzY74rSoqTNMyWUZnP1", 251,
        "2LecshUwdy9xi7meFgHtFJQNSKk4KdTrcpvaB56dP2NQ",
        "So11111111111111111111111111111111111111112", "HgmSjjBLk4mzgnXgz6pMuCRvgcEFbmCNuv9bHrXMZU3t", 1031);
    assertAdaptiveFeePool("77wceqoTMEjjkLvPcqrHgrjMa4SFmKh7DToSQH78k9DX", 249,
        "2LecshUwdy9xi7meFgHtFJQNSKk4KdTrcpvaB56dP2NQ",
        "cbbtcf3aa214zXHbiAZQwf4122FBYbraNdFqgw4iMij", "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB", 1034);
    assertAdaptiveFeePool("5tCYDk4mNgDz1BGWkYES5hAcecau2k6mr3amzcqHa1F1", 249,
        "2LecshUwdy9xi7meFgHtFJQNSKk4KdTrcpvaB56dP2NQ",
        "6UpQcMAb5xMzxc7ZfPaVMgx3KqsvKZdT5U718BzD5We2", "EPH3fykJyHB2MWi7bVuHVxfLEzN5seRADxoXXJUJp5GP", 1033);
    // An index above Short.MAX_VALUE is still its two low bytes.
    assertAdaptiveFeePool("Hj9U8xkVzVBAtDXSa2u77UgP7YrpoQBMx18xRFbQvP9", 255,
        "98RDvVz5G123hArL7Ce4sC1Ft1F44rshAU8MF3AduPp",
        "5xgsnby6P9zqGK71J7H4yJLxzqPvNbC7rDZxNzjHmj7e", "EVULoNF4DeMBN4dGiZiDfpiiTfNZgoCvXWWgaV3epump", 32897);
  }

  private static void assertAdaptiveFeePool(final String whirlpool,
                                            final int bump,
                                            final String whirlpoolsConfig,
                                            final String tokenMintA,
                                            final String tokenMintB,
                                            final int feeTierIndex) {
    final var config = PublicKey.fromBase58Encoded(whirlpoolsConfig);
    final var mintA = PublicKey.fromBase58Encoded(tokenMintA);
    final var mintB = PublicKey.fromBase58Encoded(tokenMintB);
    final var pda = WhirlpoolPDAs.whirlpool1PDA(WHIRLPOOL_PROGRAM, config, mintA, mintB, SerDeUtil.int16LESeed(feeTierIndex));
    assertEquals(PublicKey.fromBase58Encoded(whirlpool), pda.publicKey());
    assertEquals(bump, pda.nonce());
    assertNotEquals(pda.publicKey(),
        WhirlpoolPDAs.whirlpool1PDA(WHIRLPOOL_PROGRAM, config, mintA, mintB, SerDeUtil.asciiSeed(feeTierIndex)).publicKey(),
        "the decimal text the tick arrays use derives a pool the program never created");
  }
}
