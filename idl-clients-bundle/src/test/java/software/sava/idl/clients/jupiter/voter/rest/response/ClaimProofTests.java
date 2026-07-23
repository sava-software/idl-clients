package software.sava.idl.clients.jupiter.voter.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.JsonIterator;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// The proof parser routes unrecognized fields through `ji.skip()`, which
/// returns its iterator — a dropped skip parks the iterator inside the unknown
/// value and misreads every field after it. The API adds fields without
/// notice, so the parse must land the same values past unknown neighbors.
final class ClaimProofTests {

  private static final PublicKey MINT =
      PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112");
  private static final PublicKey MERKLE_TREE =
      PublicKey.fromBase58Encoded("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");

  @Test
  void proofParsesPastUnknownFields() {
    final var json = """
        {"unknownLeading":{"a":[1,{"b":true}]},
         "mint":"%s",
         "unknownMid":["s",2.5],
         "merkle_tree":"%s",
         "amount":1000,"locked_amount":250,
         "proof":[%s],
         "unknownTrailing":false}"""
        .formatted(MINT.toBase58(), MERKLE_TREE.toBase58(), hashJson((byte) 0x11));

    final var proof = ClaimProof.parseProof(JsonIterator.parse(json.getBytes(UTF_8)));

    assertEquals(MINT, proof.mint());
    assertEquals(MERKLE_TREE, proof.merkleTree());
    assertEquals(1_000L, proof.amount());
    assertEquals(250L, proof.lockedAmount());
    assertEquals(1, proof.proof().length);
    final byte[] expected = new byte[32];
    java.util.Arrays.fill(expected, (byte) 0x11);
    assertArrayEquals(expected, proof.proof()[0]);
  }

  private static String hashJson(final byte fill) {
    final var sb = new StringBuilder("[");
    for (int i = 0; i < 32; i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(fill & 0xFF);
    }
    return sb.append(']').toString();
  }
}
