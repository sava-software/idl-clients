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
/// `WhirlpoolPDAs` helpers with an explicit `SerDeUtil.asciiSeed`. Expected
/// addresses are mainnet accounts owned by the whirlpool program.
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
}
