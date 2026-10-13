package software.sava.idl.clients.jupiter.stable;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.stable.gen.types.OracleType;
import software.sava.idl.clients.jupiter.stable.gen.types.Vault;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

/// `Vault` is a zero-copy account holding five `OracleType` slots, each a one-byte tag and a
/// 120-byte payload, 121 bytes whichever oracle is set. Every field after them now has an offset
/// and the account a size.
///
/// The fixture is the live USD vault, captured at slot 456589344: a Pyth oracle in the first slot
/// and the other four empty.
final class StableVaultTests {

  private static final PublicKey VAULT = PublicKey.fromBase58Encoded("7JnALHo2bFuKsgkNymiqJwt228jy3my99CnfF75QD9oj");

  private static byte[] fixture() {
    try (var in = StableVaultTests.class.getResourceAsStream("/jupiter/stableVault-" + VAULT.toBase58() + ".base64")) {
      assertNotNull(in, "the vault fixture is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void theFieldsAfterTheOraclesSitWhereTheProgramPutsThem() {
    assertEquals(176, Vault.ORACLES_OFFSET);
    // five 121-byte slots, then three bytes of padding
    assertEquals(176 + 5 * 121, Vault.PADDING_3_OFFSET);
    assertEquals(784, Vault.PERIOD_LIMITS_OFFSET);
    // four 48-byte period limits, then 32 reserved bytes
    assertEquals(784 + 4 * 48 + 32, Vault.TOTAL_MINTED_OFFSET);
    assertEquals(1296, Vault.BYTES);

    final byte[] data = fixture();
    assertEquals(Vault.BYTES, data.length);
    final var vault = Vault.read(VAULT, data);
    assertEquals(Vault.BYTES, vault.l());

    final var oracles = vault.oracles();
    final var pyth = assertInstanceOf(OracleType.Pyth.class, oracles[0]);
    assertEquals(readPubKey(data, 176 + 1 + 32), pyth.val().account());
    for (int i = 1; i < oracles.length; ++i) {
      assertInstanceOf(OracleType.Empty.class, oracles[i], "oracle " + i);
    }

    final var periodLimits = vault.periodLimits();
    assertEquals(86400, periodLimits[0].durationSeconds(), "a daily window");
    assertEquals(getInt64LE(data, 784), periodLimits[0].durationSeconds());
    assertEquals(getInt64LE(data, 784 + 48 + 40), periodLimits[1].windowStart());
  }

  @Test
  void aVaultWritesBackToItsOwnBytes() {
    final byte[] data = fixture();
    final var vault = Vault.read(VAULT, data);
    final byte[] written = new byte[Vault.BYTES];
    assertEquals(Vault.BYTES, vault.write(written, 0));
    assertArrayEquals(data, written);
  }
}
