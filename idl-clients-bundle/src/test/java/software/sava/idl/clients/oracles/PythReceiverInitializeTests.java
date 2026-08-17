package software.sava.idl.clients.oracles;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.oracles.pyth.receiver.gen.PythSolanaReceiverProgram;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.Config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// `Config` is an account used as an instruction argument, and the two roles encode differently.
///
/// Upstream's `initialize(ctx, initial_config: Config)` takes a type carrying `#[account]`, which
/// serializes the **fields only** as an argument and prefixes the eight-byte discriminator only
/// when written as an account. So the instruction is `[8 ix discriminator][fields]` while the
/// stored account is `[8 account discriminator][fields]`.
///
/// The assertions below hold the built instruction to the first shape and the stored account to
/// the second, against a real mainnet Config. Both are measured from the fixture rather than from
/// the client, so neither can drift into agreeing with a wrong encoder.
final class PythReceiverInitializeTests {

  private static final String CONFIG = "DaWUKXCyXsnzcvLUyeJRWou8KTn7XtadgTsdhJ6RHS7b";
  private static final PublicKey PROGRAM =
      PublicKey.fromBase58Encoded("rec5EKMGg6MxZYaMdyBfgwp4d5rB9T1VQH5pJv5LtFJ");

  /// The receiver's only Config account, captured with `getAccountInfo` from mainnet.
  private static byte[] accountData() {
    try (var in = PythReceiverInitializeTests.class.getResourceAsStream(
        "/oracles/pythConfig-" + CONFIG + ".base64")) {
      assertNotNull(in, "fixture for " + CONFIG + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Config config() {
    return Config.readChecked(PublicKey.fromBase58Encoded(CONFIG), accountData(), 0);
  }

  /// The account encoding is right, which is the half that has to keep working. Stated first so a
  /// failure below cannot be mistaken for the layout being wrong generally.
  @Test
  void theAccountItselfDecodesAndIsDiscriminatorPrefixed() {
    final byte[] data = accountData();
    assertEquals(370, data.length);
    assertArrayEquals(
        Config.DISCRIMINATOR.data(), Arrays.copyOf(data, 8),
        "the stored account really does carry the account discriminator"
    );

    final var config = config();
    assertNotNull(config);
    // Not equality: the account is allocated at a fixed size and carries slack past its serialized
    // content — 370 bytes holding 120 — because `validDataSources` is a Vec that can grow.
    assertTrue(
        config.l() <= data.length,
        () -> "Config read past its account: " + config.l() + " > " + data.length
    );
    assertTrue(config.minimumSignatures() > 0, "upstream requires this, so a garbage decode shows here");
  }

  /// The number the fix moved: the built instruction is the ix discriminator plus the fields, with
  /// no account prefix in between. 120 bytes, not 128.
  ///
  /// The expected length is anchored to the fixture rather than recomputed from `lFields()` —
  /// asserting `8 + config.lFields()` would compare the generated sizing expression against itself
  /// and pass even if the fields-only width were wrong.
  @Test
  void theBuiltInstructionIsTheBytesTheProgramReads() {
    final var config = config();
    final var ix = PythSolanaReceiverProgram.initialize(
        AccountMeta.createInvoked(PROGRAM), List.<AccountMeta>of(), config
    );
    final byte[] data = ix.data();

    // the account's 120 serialized bytes are its 8-byte prefix plus 112 fields; upstream's
    // AnchorSerialize writes those 112 after the 8-byte instruction discriminator
    assertEquals(120, data.length, "8 ix discriminator + 112 fields");

    assertArrayEquals(
        PythSolanaReceiverProgram.INITIALIZE_DISCRIMINATOR.data(), Arrays.copyOfRange(data, 0, 8),
        "bytes 0-8 are the instruction discriminator"
    );
    assertEquals(
        config.governanceAuthority(), PublicKey.readPubKey(data, 8),
        "bytes 8-40 are the first field, not an account discriminator"
    );
  }

  /// The offset the client publishes for the argument now lands on the first field, and the whole
  /// instruction equals the one the program would have sent.
  ///
  /// `INITIAL_CONFIG_OFFSET` is unchanged at 8 and was never wrong — the instruction discriminator
  /// really is eight bytes. Only what sits at that offset moved. Note the same number means
  /// something different on the account side: `Config.GOVERNANCE_AUTHORITY_OFFSET` is also 8, and
  /// the two coincide only because both discriminators happen to be that wide.
  @Test
  void theArgumentOffsetPointsAtTheFirstField() {
    assertEquals(8, PythSolanaReceiverProgram.InitializeIxData.INITIAL_CONFIG_OFFSET);

    final var config = config();
    final var ix = PythSolanaReceiverProgram.initialize(
        AccountMeta.createInvoked(PROGRAM), List.<AccountMeta>of(), config
    );

    // what the program actually sends: ix discriminator then the serialized fields, no account
    // prefix and no allocation slack — built from the fixture, independent of the builder
    final byte[] fields = Arrays.copyOfRange(accountData(), 8, config.l());
    final byte[] upstream = new byte[8 + fields.length];
    PythSolanaReceiverProgram.INITIALIZE_DISCRIMINATOR.write(upstream, 0);
    System.arraycopy(fields, 0, upstream, 8, fields.length);

    assertArrayEquals(upstream, ix.data(), "the client builds exactly what the program would send");
    assertEquals(
        config.governanceAuthority(),
        PublicKey.readPubKey(ix.data(), PythSolanaReceiverProgram.InitializeIxData.INITIAL_CONFIG_OFFSET),
        "INITIAL_CONFIG_OFFSET lands on governanceAuthority"
    );
  }

  /// The decode side, which had no assertion while the encoding was wrong. A real upstream
  /// instruction round-trips back to the values the account holds.
  @Test
  void aProgramShapedInstructionDecodesToItsFieldValues() {
    final var config = config();
    final byte[] fields = Arrays.copyOfRange(accountData(), 8, config.l());
    final byte[] upstream = new byte[8 + fields.length];
    PythSolanaReceiverProgram.INITIALIZE_DISCRIMINATOR.write(upstream, 0);
    System.arraycopy(fields, 0, upstream, 8, fields.length);

    final var ixData = PythSolanaReceiverProgram.InitializeIxData.read(upstream, 0);
    assertNotNull(ixData);
    assertEquals(upstream.length, ixData.l(), "the decoder accounts for every byte");

    final var decoded = ixData.initialConfig();
    assertEquals(config.governanceAuthority(), decoded.governanceAuthority());
    assertEquals(config.wormhole(), decoded.wormhole());
    assertEquals(config.singleUpdateFeeInLamports(), decoded.singleUpdateFeeInLamports());
    assertEquals(config.minimumSignatures(), decoded.minimumSignatures());
    assertEquals(config.validDataSources().length, decoded.validDataSources().length);
  }
}
