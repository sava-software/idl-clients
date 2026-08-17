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

/// Ember's published IDL is stale, and this pins how far we go about it.
///
/// The generator is faithful to that IDL — upstream is wrong, in two ways. It declares a
/// discriminator no live account carries, and it models three trailing pubkeys where the program
/// writes four.
///
/// Only the first is fixed here, by an `accountDiscriminators` override in main_net_programs.json,
/// because a wrong discriminator makes the account undecodable and the correct value is
/// independently derivable from Phoenix's own SDK. The **short layout is deliberately left
/// alone**: a local field-list override would mean maintaining a private fork of someone else's
/// account definition indefinitely, with no upstream IDL in their repo to diff against. That fix
/// belongs upstream. A caller who needs the fourth key should say so to Phoenix.
///
/// So the assertions below are split. The discriminator ones are the contract. The layout ones
/// record the gap and act as a tripwire: if `BYTES` ever becomes 136, upstream shipped a corrected
/// IDL, and this class should be rewritten to assert the full layout rather than the shortfall.
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

  /// The fixture is the ground truth, so this holds regardless of what the client declares. It is
  /// here so a failure downstream is unambiguously the client's and not a stale capture.
  @Test
  void theCapturedAccountCarriesTheDeployedDiscriminator() {
    final byte[] data = accountData();
    assertEquals(136, data.length, "8 + four pubkeys, not the IDL's 8 + three");
    for (int i = 0; i < DEPLOYED_DISCRIMINATOR.length; i++) {
      assertEquals(DEPLOYED_DISCRIMINATOR[i], data[i] & 0xFF, "discriminator byte " + i);
    }
  }

  /// The override earns its keep here: this is the constant the stale IDL got wrong, and the one
  /// value in the file that main_net_programs.json now supplies.
  @Test
  void theDeclaredDiscriminatorMatchesTheDeployedProgram() {
    assertEquals(8, EmberState.DISCRIMINATOR.length());
    for (int i = 0; i < DEPLOYED_DISCRIMINATOR.length; i++) {
      assertEquals(DEPLOYED_DISCRIMINATOR[i], EmberState.DISCRIMINATOR.data()[i] & 0xFF,
          "declared discriminator byte " + i);
    }
  }

  /// What the override bought: the checked path decodes a live account again. Between the
  /// `readChecked` regeneration and the override this threw for 100% of live accounts.
  @Test
  void aRealAccountDecodesThroughTheCheckedPath() {
    final byte[] data = accountData();
    final var state = EmberState.readChecked(PublicKey.fromBase58Encoded(ADDRESS), data, 0);
    assertNotNull(state);

    assertEquals(8, EmberState.AUTHORITY_OFFSET);
    assertEquals(40, EmberState.INPUT_MINT_OFFSET);
    assertEquals(72, EmberState.OUTPUT_MINT_OFFSET);

    assertEquals("E11ipYYCnbbRnhsEaYetRLjkS5KM7Ld7BPbdMGVacmD3", state.authority().toBase58());
    assertEquals("EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", state.inputMint().toBase58(), "USDC");
    assertEquals("PhUsd11YkbjSaWjFncfAAmatntsjx3MgDR9B6g1ks3A", state.outputMint().toBase58());
  }

  /// The gap we are choosing not to close, stated exactly so it cannot be mistaken for an
  /// oversight. The record stops one pubkey short of the account, and that key is really there.
  ///
  /// Asserted on the raw bytes rather than through an accessor because the record has no field
  /// for it — that is the point.
  @Test
  void theRecordStopsOnePubkeyShortOfTheAccount() {
    final byte[] data = accountData();

    assertEquals(104, EmberState.BYTES, "what the stale IDL declares; see the class javadoc");
    assertEquals(136, data.length, "what the program writes");
    assertEquals(EmberState.BYTES + PublicKey.PUBLIC_KEY_LENGTH, data.length,
        "the shortfall is exactly one undeclared trailing pubkey");

    final var trailing = PublicKey.readPubKey(data, EmberState.BYTES);
    assertEquals("EtrnLzgbS7nMMy5fbD42kXiUzGg8XQzJ972Xtk1cjWih", trailing.toBase58());
  }

  /// The practical consequence, so nobody reaches for `SIZE_FILTER` and wonders why a scan comes
  /// back empty. The filter really is built from the short `BYTES`, and the size it asks the RPC
  /// for is not a size any Ember account has.
  @Test
  void theSizeFilterAsksForASizeNoLiveAccountHas() {
    assertEquals(Filter.createDataSizeFilter(EmberState.BYTES), EmberState.SIZE_FILTER,
        "SIZE_FILTER is derived from the stale BYTES");
    assertEquals("{\"dataSize\":104}", EmberState.SIZE_FILTER.toJson(), "what actually goes on the wire");

    assertNotEquals(Filter.createDataSizeFilter(accountData().length), EmberState.SIZE_FILTER,
        "so a size-filtered scan returns empty — filter on DISCRIMINATOR_FILTER instead");
  }
}
