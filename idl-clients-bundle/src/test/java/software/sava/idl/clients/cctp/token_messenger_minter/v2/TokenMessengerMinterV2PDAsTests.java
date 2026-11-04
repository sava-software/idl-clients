package software.sava.idl.clients.cctp.token_messenger_minter.v2;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.cctp.token_messenger_minter.v2.gen.TokenMessengerMinterV2PDAs;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/// CCTP v2's Token Messenger Minter keys a remote token messenger and a token pair by the remote
/// domain as decimal text, `domain.to_string().as_bytes()` (`remove_remote_token_messenger.rs:49`
/// and `unlink_token_pair.rs:49` in circlefin/solana-cctp-contracts). An Anchor IDL cannot say how
/// a number is hashed, so the generated helpers take the seed's bytes, here
/// `SerDeUtil.asciiSeed(domain)`. Expected values are mainnet accounts the program owns, read at
/// slots 465247750 and 465247999, with the domain, remote token and bump each one stores; a remote
/// token messenger stores no bump.
final class TokenMessengerMinterV2PDAsTests {

  private static final PublicKey PROGRAM = key("CCTPV2vPZJS2u2BBsUoscuikbYjnpFmbFsvVuJdgUMQe");

  private static PublicKey key(final String base58) {
    return PublicKey.fromBase58Encoded(base58);
  }

  @Test
  void aRemoteTokenMessengerIsKeyedByItsDomainAsDecimalText() {
    assertRemoteTokenMessenger("3EzN2mcmdfSNGXRCAixSpTteK6ywdmFDZZWvkMnznFt9", 0);
    assertRemoteTokenMessenger("53NSDvEXmUixWSsCQF5rnTxcKdihq19WXNaZFXrX3ojf", 3);
    assertRemoteTokenMessenger("EkF2RA5nSk2uiXBh2VYWJ8tHszNRpbcWdS9LxZgPb5at", 10);
    assertRemoteTokenMessenger("5JxZokf4v8zLuDbDij9sdQyfg4cdjMQtynUsW77uVN1L", 12);
  }

  private static void assertRemoteTokenMessenger(final String address, final int domain) {
    final var seed = SerDeUtil.asciiSeed(domain);
    assertEquals(key(address), TokenMessengerMinterV2PDAs.remoteTokenMessenger1PDA(PROGRAM, seed).publicKey());
    assertEquals(key(address), TokenMessengerMinterV2PDAs.remoteTokenMessengerPDA(PROGRAM, seed).publicKey(),
        "add_remote_token_messenger's derivation hashes the same bytes from its argument");
    assertNotEquals(key(address),
        TokenMessengerMinterV2PDAs.remoteTokenMessenger1PDA(PROGRAM, SerDeUtil.int32LESeed(domain)).publicKey(),
        "the domain's little-endian bytes derive an address the program never created");
  }

  @Test
  void aTokenPairIsKeyedByItsRemoteDomainAsDecimalTextAndItsRemoteToken() {
    // Remote tokens that are EVM addresses, left-padded to 32 bytes.
    assertTokenPair("E4ZYkwNfR73MqQB6Kfmf515aQVBk2Wxjp57LbkaGWQ5q", 255, 0, "1111111111113EsMD5n1VA94D2fALdb1SAKLam8j");
    assertTokenPair("3udrkuozTYGBVMyMdxmXWVTUrnpmSh7kEZiq67A8jTws", 251, 6, "1111111111112q2Gg8TH19xwTZeyUCme313nZsTQ");
    assertTokenPair("5TAQsL1BponbaJMcS2koGkeQ6t3CzvhNxdFvG2CLHM88", 251, 14, "1111111111112hH8Dwy9DZMaw1kWjxALstC1QF3n");
    // A remote token that fills all 32 bytes.
    assertTokenPair("8mqv2Yk7bE9bV4QLnxKNEkPtnzMLvU43hx7r5Ni36WFE", 253, 25, "DT39yUfguiQqcDtKVQJdsseBkATnN3tLBRg9KU7wjbp");
  }

  private static void assertTokenPair(final String address,
                                      final int bump,
                                      final int remoteDomain,
                                      final String remoteToken) {
    final var seed = SerDeUtil.asciiSeed(remoteDomain);
    final var pda = TokenMessengerMinterV2PDAs.tokenPair1PDA(PROGRAM, seed, key(remoteToken));
    assertEquals(key(address), pda.publicKey());
    assertEquals(bump, pda.nonce());
    assertEquals(key(address),
        TokenMessengerMinterV2PDAs.tokenPairPDA(PROGRAM, seed, key(remoteToken).toByteArray()).publicKey(),
        "link_token_pair's derivation hashes the same bytes from its arguments");
  }
}
