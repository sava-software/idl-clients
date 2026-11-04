package software.sava.idl.clients.jupiter.doves;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.doves.gen.DovesPDAs;

import java.util.Arrays;
import java.util.HexFormat;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// Doves keys a price feed by all 32 bytes of its `pair`, `["feed", pair]`. Most feeds hold an
/// ASCII symbol padded with zeros; the feeds an `AgPriceFeed` names as its `cl_feed` hold an opaque
/// identifier, which a caller copies from the account rather than builds. Expected values are
/// mainnet `PriceFeed` accounts read at slot 465247788, with the pair and bump each one stores.
/// There is no published source, so the live accounts are the only oracle.
final class DovesPDAsTests {

  private static final PublicKey PROGRAM = key("DoVEsk76QybCEHQGzkvYPWLQu9gzNoZZZt3TPiL597e");

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  private static byte[] symbol(final String symbol) {
    return Arrays.copyOf(symbol.getBytes(US_ASCII), 32);
  }

  private static void assertFeed(final String address, final int bump, final byte[] pair) {
    final var pda = DovesPDAs.feed1PDA(PROGRAM, pair);
    assertEquals(key(address), pda.publicKey());
    assertEquals(bump, pda.nonce());
    assertEquals(key(address), DovesPDAs.feedPDA(PROGRAM, pair).publicKey(),
        "the argument-seed derivation hashes the same bytes");
  }

  @Test
  void aPriceFeedIsKeyedByItsZeroPaddedSymbol() {
    assertFeed("4HBbPx9QJdjJ7GUe6bsiJjGybvfpDhQMMPXP1UEa7VT5", 251, symbol("BTCUSD"));
    assertFeed("A28T5pKtscnhDo6C1Sz786Tup88aTjt8uyKewjVvPrGk", 255, symbol("USDCUSD"));
    assertFeed("A5d4aY4K4BYEtXdAY7WNBHTKxRMeyAA6RYHWVYEW22Fe", 254, symbol("SOLUSD-PYTH"));
  }

  @Test
  void theZeroPaddingIsPartOfTheSeed() {
    assertNotEquals(key("4HBbPx9QJdjJ7GUe6bsiJjGybvfpDhQMMPXP1UEa7VT5"),
        DovesPDAs.feed1PDA(PROGRAM, "BTCUSD".getBytes(US_ASCII)).publicKey());
  }

  @Test
  void aClFeedIsKeyedByItsStoredIdentifier() {
    final var hex = HexFormat.of();
    assertFeed("FWLXDDgW2Qm2VuX8MdV99VYpo6X1HLEykUjfAsjz2G78", 255,
        hex.parseHex("0003b778d3f6b2ac4991302b89cb313f99a42467d6c9c5f96f57c29c0d2bc24f"));
    assertFeed("5KQxzQ4xQGPUiJGYbujLjygm6Frin9zE5h996hxxfyqe", 252,
        hex.parseHex("0003a910a43485e0685ff5d6d366541f5c21150f0634c5b14254392d1a1c06db"));
  }
}
