package software.sava.idl.clients.kamino.scope.entries;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.kamino.scope.gen.types.OracleMappings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

final class ScopeReaderTests {

  private static List<String> readFiles(final String resourcePath,
                                        final String suffix) throws URISyntaxException, IOException {
    final var resource = ScopeReaderTests.class.getResource(resourcePath);
    assertNotNull(resource);
    final var dir = Paths.get(resource.toURI());
    try (final var stream = Files.list(dir)) {
      final var files = stream.<String>mapMulti((path, downstream) -> {
        if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(suffix)) {
          try {
            if (".zip".equals(suffix)) {
              try (final var in = Files.newInputStream(path)) {
                try (final var zin = new ZipInputStream(in)) {
                  final var entry = zin.getNextEntry();
                  if (entry == null) {
                    fail("Zip resource has no entries: " + path);
                  }
                  final var contents = new String(zin.readAllBytes()).strip();
                  downstream.accept(contents);
                }
              }
            } else {
              final var fileContents = Files.readString(path).strip();
              downstream.accept(fileContents);
            }
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          }
        }
      }).toList();
      assertFalse(files.isEmpty());
      return files;
    }
  }

  @Test
  void parseMappings() throws URISyntaxException, IOException {
    final var fileContents = readFiles("/oracle_mappings", ".zip");
    for (final var encodedData : fileContents) {
      final byte[] data = Base64.getDecoder().decode(encodedData);
      assertEquals(OracleMappings.BYTES, data.length);

      final var mappings = OracleMappings.read(data, 0);
      final var reader = ScopeReader.createReader(mappings);
      final var entries = reader.readEntries();
      assertEquals(OracleMappings.PRICE_INFO_ACCOUNTS_LEN, entries.length);
    }
  }
}
