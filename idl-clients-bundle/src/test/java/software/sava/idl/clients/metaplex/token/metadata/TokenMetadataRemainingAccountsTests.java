package software.sava.idl.clients.metaplex.token.metadata;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the `print` extras that the Token Metadata IDL omits.
final class TokenMetadataRemainingAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  /// The pair goes on in program order, and the delegate signs — that signature
  /// is the whole point of the path, so a non-signing delegate would be rejected
  /// on chain.
  @Test
  void printHolderDelegatePairIsOrderedAndTheDelegateSigns() {
    final var record = key(0x11);
    final var delegate = key(0x12);

    final var extras = TokenMetadataRemainingAccounts.printHolderDelegate(record, delegate);

    assertEquals(2, extras.size(), "index 19 cannot be reached without occupying 18");
    assertEquals(List.of(record, delegate), extras.stream().map(AccountMeta::publicKey).toList());

    assertFalse(extras.getFirst().signer(), "the delegate record is data, not an authority");
    assertFalse(extras.getFirst().write());
    assertTrue(extras.get(1).signer(), "the delegate authorizes the print and must sign");
    assertFalse(extras.get(1).write());
  }
}
