package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

final class ScopeReaderTests {

  @Test
  void parseMappings() throws URISyntaxException, IOException {
    final var fileContents = ResourceUtil.readFiles("/scope/oracle_mappings", ".zip");
    for (final var encodedData : fileContents) {
      final byte[] data = Base64.getDecoder().decode(encodedData);
      assertEquals(OracleMappings.BYTES, data.length);

      final var mappings = OracleMappings.read(data, 0);
      final var entries = ScopeReader.parseEntries(mappings);
      assertEquals(OracleMappings.PRICE_INFO_ACCOUNTS_LEN, entries.numEntries());
    }
  }
}
