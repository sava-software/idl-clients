package software.sava.idl.clients.kamino.scope.entries;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public final class ResourceUtil {

  private ResourceUtil() {
  }

  public static List<String> readFiles(final String resourcePath,
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
}
