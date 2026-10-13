package software.sava.idl.clients;

import org.junit.jupiter.api.Test;
import systems.comodal.jsoniter.JsonIterator;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/// Run by the `zeroCopyLayoutAudit` task over the IDL every client in both modules was generated
/// from. The ordinary test task has no source root to read and skips it.
///
/// An account whose IDL type declares `serialization` `bytemuck` or `bytemuckunsafe` is read in
/// place, at offsets its layout fixes, so its generated record must have a fixed length too: a
/// field given a variable width moves every field after it as soon as it holds a short value. That
/// is how Jupiter Offerbook's `Offer` misread everything after an `Asset.None` collateral until
/// idl-src-gen gave an enum with data its whole width in such an account.
///
/// That width rests on a tag size the IDL cannot state. Anchor writes `{"kind": "c"}` for both
/// `#[repr(C)]`, whose tag is a four-byte C int, and `#[repr(C, u8)]`, and idl-src-gen takes the
/// tag to be one byte. A live account's length is the only oracle for that, so every zero-copy
/// account whose layout reaches an enum with data is pinned to one here.
final class ZeroCopyLayoutAuditTests {

  /// Every zero-copy account whose layout reaches an enum with data, and a live account of it.
  private static final Map<String, String> PINNED = Map.of(
      "software.sava.idl.clients.jupiter.offerbook.gen.types.Loan",
      "/jupiter/loan-59G7EsK1vQVvt7xCGpBn82bouxqCMzFnmdeE4GHr1os.base64",
      "software.sava.idl.clients.jupiter.offerbook.gen.types.Offer",
      "/jupiter/offer-16ftoxLGwKYwfdMhYbkwxRNzqwHaAhSFDsWdknyTraJ.base64",
      "software.sava.idl.clients.jupiter.stable.gen.types.Vault",
      "/jupiter/stableVault-7JnALHo2bFuKsgkNymiqJwt228jy3my99CnfF75QD9oj.base64"
  );

  private static final Set<String> ZERO_COPY = Set.of("bytemuck", "bytemuckunsafe");

  /// Each zero-copy account's generated class, and whether its layout reaches an enum with data.
  private static Map<String, Boolean> zeroCopyAccounts() {
    final var roots = System.getProperty("sava.genSourceRoots");
    assumeTrue(roots != null, "set by the zeroCopyLayoutAudit task");
    final var accounts = new TreeMap<String, Boolean>();
    for (final var root : roots.split(",")) {
      final var rootPath = Path.of(root);
      assertTrue(Files.isDirectory(rootPath), root + " is not a source root");
      final List<Path> idls;
      try (final var paths = Files.walk(rootPath)) {
        idls = paths.filter(ZeroCopyLayoutAuditTests::isGeneratedFrom).sorted().toList();
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
      for (final var idl : idls) {
        final var typesPackage = rootPath.relativize(idl.getParent()).toString().replace(File.separatorChar, '.') + ".types";
        collectZeroCopyAccounts(typesPackage, readJson(idl), accounts);
      }
    }
    assertFalse(accounts.isEmpty(), "no zero-copy account found under " + roots);
    return accounts;
  }

  /// The document a client was generated from; the channel copies beside it generated nothing.
  private static boolean isGeneratedFrom(final Path path) {
    final var parent = path.getParent();
    return path.getFileName().toString().equals("idl.json")
        && parent != null && parent.getFileName().toString().equals("gen");
  }

  private static void collectZeroCopyAccounts(final String typesPackage,
                                              final Object idl,
                                              final Map<String, Boolean> accounts) {
    if (!(idl instanceof Map<?, ?> document)) {
      return;
    }
    final var types = new HashMap<String, Map<?, ?>>();
    if (document.get("types") instanceof List<?> declared) {
      for (final var type : declared) {
        if (type instanceof Map<?, ?> map && map.get("name") instanceof String name) {
          types.put(name, map);
        }
      }
    }
    if (document.get("accounts") instanceof List<?> declared) {
      for (final var account : declared) {
        if (account instanceof Map<?, ?> map
            && map.get("name") instanceof String name
            && types.get(name) instanceof Map<?, ?> type
            && type.get("serialization") instanceof String serialization
            && ZERO_COPY.contains(serialization)) {
          accounts.put(typesPackage + '.' + name, reachesEnumWithData(types, type.get("type"), new HashSet<>()));
        }
      }
    }
  }

  /// Whether anything inside `node`, followed through every type it names, is an enum with a
  /// variant that carries data.
  private static boolean reachesEnumWithData(final Map<String, Map<?, ?>> types,
                                             final Object node,
                                             final Set<String> visiting) {
    if (node instanceof Map<?, ?> map) {
      if ("enum".equals(map.get("kind")) && map.get("variants") instanceof List<?> variants) {
        for (final var variant : variants) {
          if (variant instanceof Map<?, ?> v && v.get("fields") instanceof List<?> fields && !fields.isEmpty()) {
            return true;
          }
        }
      }
      final var defined = map.get("defined");
      final var name = defined instanceof Map<?, ?> d ? d.get("name") : defined;
      if (name instanceof String typeName
          && types.get(typeName) instanceof Map<?, ?> type
          && visiting.add(typeName)
          && reachesEnumWithData(types, type.get("type"), visiting)) {
        return true;
      }
      for (final var value : map.values()) {
        if (reachesEnumWithData(types, value, visiting)) {
          return true;
        }
      }
    } else if (node instanceof List<?> list) {
      for (final var value : list) {
        if (reachesEnumWithData(types, value, visiting)) {
          return true;
        }
      }
    }
    return false;
  }

  /// The record's `BYTES`, or -1 when it has none, which is to say a layout of variable length.
  private static int bytes(final String className) {
    final Class<?> type;
    try {
      type = Class.forName(className);
    } catch (final ClassNotFoundException e) {
      return fail(className + " is an account of its IDL but no such record was generated", e);
    }
    try {
      return type.getField("BYTES").getInt(null);
    } catch (final NoSuchFieldException e) {
      return -1;
    } catch (final IllegalAccessException e) {
      return fail(className + ".BYTES is not readable", e);
    }
  }

  @Test
  void everyZeroCopyAccountHasAFixedLayout() {
    final var variable = new TreeSet<String>();
    for (final var className : zeroCopyAccounts().keySet()) {
      if (bytes(className) <= 0) {
        variable.add(className);
      }
    }
    assertTrue(variable.isEmpty(), () -> variable + (variable.size() == 1 ? " is a zero-copy account" : " are zero-copy accounts")
        + ", read in place at fixed offsets, with no BYTES: the generator gave some field a variable width, which moves "
        + "every field after it as soon as that field holds a short value. Fix idl-src-gen and regenerate; never edit gen/.");
  }

  @Test
  void everyZeroCopyAccountReachingAnEnumWithDataIsPinnedToALiveOne() {
    final var reaching = new TreeSet<String>();
    zeroCopyAccounts().forEach((className, reaches) -> {
      if (reaches) {
        reaching.add(className);
      }
    });
    assertEquals(new TreeSet<>(PINNED.keySet()), reaching, () -> "The zero-copy accounts whose layout reaches an enum "
        + "with data are not the pinned ones. idl-src-gen gives such an enum its whole width, a tag and its widest variant, "
        + "and takes the tag to be one byte, which the IDL cannot confirm: Anchor writes {\"kind\": \"c\"} for #[repr(C)], "
        + "a four-byte tag, and #[repr(C, u8)] alike. Pin a new one by committing a live account of it under the test "
        + "resources and naming it in PINNED; remove an entry whose account no longer reaches such an enum.");
    for (final var pinned : PINNED.entrySet()) {
      final var className = pinned.getKey();
      final int live = fixture(pinned.getValue()).length;
      final int generated = bytes(className);
      assertEquals(live, generated, () -> generated < 0
          ? className + " has no BYTES, so its layout is not fixed; everyZeroCopyAccountHasAFixedLayout says why that matters."
          : className + " is " + generated + " bytes and the live account " + live
          + ": the enum's tag is not one byte, or its payload does not start right after it. That is idl-src-gen's "
          + "AnchorZeroCopyLayout to correct, never a hand edit of gen/.");
    }
  }

  private static byte[] fixture(final String path) {
    try (final var in = ZeroCopyLayoutAuditTests.class.getResourceAsStream(path)) {
      assertNotNull(in, "fixture " + path + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Object readJson(final Path path) {
    try {
      return readValue(JsonIterator.parse(Files.readAllBytes(path)));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Object readValue(final JsonIterator ji) {
    return switch (ji.whatIsNext()) {
      case OBJECT -> {
        final var object = new LinkedHashMap<String, Object>();
        ji.testObject((buf, offset, len, it) -> {
          object.put(new String(buf, offset, len), readValue(it));
          return true;
        });
        yield object;
      }
      case ARRAY -> {
        final var array = new ArrayList<>();
        while (ji.readArray()) {
          array.add(readValue(ji));
        }
        yield array;
      }
      case STRING -> ji.readString();
      case NUMBER -> ji.readNumberAsString();
      case BOOLEAN -> ji.readBoolean();
      case NULL -> {
        ji.readNull();
        yield null;
      }
      case INVALID -> throw new IllegalStateException("not JSON");
    };
  }
}
