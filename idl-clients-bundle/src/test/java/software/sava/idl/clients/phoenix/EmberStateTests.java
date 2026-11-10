package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.phoenix.ember.gen.types.EmberState;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// Ember's corrected published IDL now describes the deployed discriminator and all four pubkeys.
/// The captured account remains the independent byte-level oracle for the generated layout.
final class EmberStateTests {

  private static final String ADDRESS = "6ur7v6AXNpnHeEb6xuk7PyezvZ1i5GrgYyWZkNCpzbRz";

  /// The value the deployed program writes: `sha256("account:state_account")[0..8]`.
  private static final int[] DEPLOYED_DISCRIMINATOR = {142, 206, 11, 177, 63, 157, 55, 98};

  /// Captured with `getAccountInfo` from mainnet.
  private static byte[] accountData() {
    try (var in = EmberStateTests.class.getResourceAsStream("/phoenix/emberState-" + ADDRESS + ".base64")) {
      assertNotNull(in, "fixture for " + ADDRESS + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// The fixture is ground truth, independently of what the generated IDL declares.
  @Test
  void theCapturedAccountCarriesTheDeployedDiscriminator() {
    final byte[] data = accountData();
    assertEquals(136, data.length, "8-byte discriminator plus four pubkeys");
    for (int i = 0; i < DEPLOYED_DISCRIMINATOR.length; i++) {
      assertEquals(DEPLOYED_DISCRIMINATOR[i], data[i] & 0xFF, "discriminator byte " + i);
    }
  }

  /// The generated discriminator now agrees with the captured deployed bytes.
  @Test
  void theDeclaredDiscriminatorMatchesTheDeployedProgram() {
    assertEquals(8, EmberState.DISCRIMINATOR.length());
    for (int i = 0; i < DEPLOYED_DISCRIMINATOR.length; i++) {
      assertEquals(DEPLOYED_DISCRIMINATOR[i], EmberState.DISCRIMINATOR.data()[i] & 0xFF,
          "discriminator byte " + i);
    }
  }

  /// The checked path accepts the captured account and reads every declared key.
  @Test
  void aRealAccountDecodesThroughTheCheckedPath() {
    final byte[] data = accountData();
    final var state = EmberState.readChecked(PublicKey.fromBase58Encoded(ADDRESS), data, 0);
    assertNotNull(state);

    assertEquals("E11ipYYCnbbRnhsEaYetRLjkS5KM7Ld7BPbdMGVacmD3", state.authority().toBase58());
    assertEquals("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", state.inputMint().toBase58(), "USDC");
    assertEquals("PhUsd11YkbjSaWjFncfAAmatntsjx3MgDR9B6g1ks3A", state.outputMint().toBase58());
    assertEquals("EtrnLzgbS7nMMy5fbD42kXiUzGg8XQzJ972Xtk1cjWih", state.phoenixProgram().toBase58());
  }

  /// The generated field offsets and values match the captured account byte for byte.
  @Test
  void theRecordCoversAllFourPubkeys() {
    final byte[] data = accountData();

    assertEquals(136, EmberState.BYTES);
    assertEquals(136, data.length, "what the program writes");
    assertEquals(8, EmberState.AUTHORITY_OFFSET);
    assertEquals(40, EmberState.INPUT_MINT_OFFSET);
    assertEquals(72, EmberState.OUTPUT_MINT_OFFSET);
    assertEquals(104, EmberState.PHOENIX_PROGRAM_OFFSET);
    assertEquals("EtrnLzgbS7nMMy5fbD42kXiUzGg8XQzJ972Xtk1cjWih",
        PublicKey.readPubKey(data, EmberState.PHOENIX_PROGRAM_OFFSET).toBase58());
    final var state = EmberState.read(data, 0);
    final byte[] encoded = new byte[EmberState.BYTES];
    assertEquals(EmberState.BYTES, state.write(encoded, 0));
    assertArrayEquals(data, encoded, "generated reader and writer preserve the captured account");
  }

  /// The generated size filter now matches live state accounts.
  @Test
  void theRecordCarriesTheLiveSizeFilter() {
    assertEquals(accountData().length, EmberState.BYTES);
    assertEquals(Filter.createDataSizeFilter(136), EmberState.SIZE_FILTER);
    assertEquals(Filter.createMemCompFilter(0, EmberState.DISCRIMINATOR.data()),
        EmberState.DISCRIMINATOR_FILTER);
  }
}
