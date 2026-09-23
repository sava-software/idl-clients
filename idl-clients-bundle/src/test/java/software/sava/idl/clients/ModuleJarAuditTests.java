package software.sava.idl.clients;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.util.TreeSet;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/// Run by the `moduleJarAudit` task against the built jars of both modules. The ordinary
/// test task has no jar to read and skips it.
///
/// `module-info.java` and the jar exclusions are edited in different files, and nothing
/// else in the build loads the bundle's own jar. A package exported but left out of the
/// jar fails every module-path consumer at resolution; a package shipped but not exported
/// is invisible to them, which is how `TokenMetadataRemainingAccounts` shipped from
/// 25.18.0 to 25.19.7. So the two sets have to be equal, not merely consistent.
final class ModuleJarAuditTests {

  @Test
  void moduleInfoExportsExactlyThePackagesTheJarCarries() throws IOException {
    final var jars = System.getProperty("sava.moduleJars");
    assumeTrue(jars != null, "set by the moduleJarAudit task");
    for (final var module : new String[]{"idl-clients-spl", "idl-clients-bundle"}) {
      assertTrue(jars.contains(module), module + " jar is missing from the audited set " + jars);
    }
    for (final var path : jars.split(",")) {
      try (final var jar = new JarFile(path)) {
        final var packages = new TreeSet<String>();
        final var scratch = new TreeSet<String>();
        for (final var entries = jar.entries(); entries.hasMoreElements(); ) {
          final var name = entries.nextElement().getName();
          if (!name.endsWith(".class") || name.equals("module-info.class")) {
            continue;
          }
          final int slash = name.lastIndexOf('/');
          packages.add(name.substring(0, slash).replace('/', '.'));
          final var simpleName = name.substring(slash + 1);
          if (simpleName.equals("Integ.class") || simpleName.startsWith("Integ$")) {
            scratch.add(name);
          }
        }
        final ModuleDescriptor descriptor;
        try (final var in = jar.getInputStream(jar.getJarEntry("module-info.class"))) {
          descriptor = ModuleDescriptor.read(in);
        }
        final var exports = new TreeSet<String>();
        for (final var export : descriptor.exports()) {
          if (!export.isQualified()) {
            exports.add(export.source());
          }
        }
        assertEquals(packages, exports, path);
        assertTrue(scratch.isEmpty(), path + " carries git-ignored scratch classes " + scratch);
      }
    }
  }
}
