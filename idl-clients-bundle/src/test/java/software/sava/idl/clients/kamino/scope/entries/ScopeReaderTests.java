package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;
import software.sava.idl.clients.kamino.scope.gen.types.OracleType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

final class ScopeReaderTests {

  @Test
  void parseMappings() throws URISyntaxException, IOException {
    final var fileContents = ResourceUtil.readFiles("/scope/oracle_mappings", ".zip");
    for (final var encodedData : fileContents) {
      final byte[] data = Base64.getDecoder().decode(encodedData);
      assertEquals(OracleMappings.BYTES, data.length);

      final var mappings = OracleMappings.read(data, 0);
      final var entries = ScopeReader.parseEntries(-1, mappings);
      assertEquals(OracleMappings.PRICE_INFO_ACCOUNTS_LEN, entries.numEntries());
    }
  }

  /// The deployed Scope program can be ahead of the generated [OracleType] enum
  /// (e.g. `PythLazerEMA`, ordinal 48, appeared on-chain while the enum still ended at
  /// 47 and every mappings parse threw [ArrayIndexOutOfBoundsException]). An
  /// out-of-range ordinal must degrade to a [NotYetSupported] entry with a null
  /// oracle type, not fail the whole parse.
  @Test
  void unknownOracleTypeDegradesToNotYetSupported() throws URISyntaxException, IOException {
    final int numOracleTypes = OracleType.values().length;
    final var fileContents = ResourceUtil.readFiles("/scope/oracle_mappings", ".zip");
    for (final var encodedData : fileContents) {
      final byte[] data = Base64.getDecoder().decode(encodedData);
      // one past the enum, the max maskable ordinal, and out-of-range with the frozen bit set
      data[OracleMappings.PRICE_TYPES_OFFSET] = (byte) numOracleTypes;
      data[OracleMappings.PRICE_TYPES_OFFSET + 1] = (byte) 0x7F;
      data[OracleMappings.PRICE_TYPES_OFFSET + 2] = (byte) (0x80 | 0x7F);

      final var mappings = OracleMappings.read(data, 0);
      final var entries = ScopeReader.parseEntries(-1, mappings);
      assertEquals(OracleMappings.PRICE_INFO_ACCOUNTS_LEN, entries.numEntries());
      for (int i = 0; i < 3; ++i) {
        final var notYetSupported = assertInstanceOf(NotYetSupported.class, entries.scopeEntry(i));
        assertEquals(i, notYetSupported.index());
        assertNull(notYetSupported.oracleType());
      }
    }
  }
}
