import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/// Diff a generated client's account order against the program's Rust source.
///
/// Run it straight from source — no build step, no module, nothing in Gradle:
///
///     java tools/GroundTruth.java anchor <rust-dir>        <Program.java>
///     java tools/GroundTruth.java shank  <instructions.rs> <Program.java>
///
/// Exit status is 0 when nothing differs, 1 otherwise. The prose that matters is in
/// [#DOC], which is what an argument-less run prints; keep the two in step.
final class GroundTruth {

  /// Printed on a bad invocation. This is the tool's actual documentation — the traps
  /// below have each produced a false alarm that cost real time, so a reader who runs it
  /// wrong gets the warnings rather than a usage line.
  private static final String DOC = """
      Diff a generated client's account order against the program's Rust source.

          java tools/GroundTruth.java anchor <rust-dir> <Program.java>
          java tools/GroundTruth.java shank  <instructions.rs> <Program.java>

      Exit status is 0 when nothing differs, 1 otherwise.

      WHAT THIS IS FOR
      The IDL is a lossy artifact; the program's Rust is ground truth. Comparing the
      two positionally is what surfaced most of the account-ordering defects this
      repo has fixed — a transposed pair of same-typed `PublicKey` accounts compiles
      cleanly and fails only on chain.

      WHAT THIS IS NOT
      An oracle. It is an *assistive* diff: every reported difference needs triage,
      and in practice most are artifacts. Read docs/PROGRAM_VERIFICATION.md before
      acting on output. The recurring traps, all of which have produced false alarms:

        * Auto-wired accounts. The client resolves well-known programs and sysvars
          internally instead of taking them as parameters, so `rent` reads as
          `solanaAccounts.rentSysVar()`. Normalised here, but the list below is not
          exhaustive — extend AUTOWIRED rather than "fixing" the client.
        * Wrong-struct matches. A monorepo holds several programs and structs are
          matched by name alone, so `PostMessage` from an example program can pair
          with a real one and report every slot as different. Check the file a struct
          came from before believing a wholesale mismatch.
        * Per-program struct naming. CCTP suffixes its account structs `Context`
          (`AcceptOwnershipContext` -> `acceptOwnership`); `--strip-suffix` handles
          that shape. A run reporting "compared 0" is a failure to compare, not a
          pass, so the compared count is always printed.
        * Published IDLs that do not match the repo. Orca's IDL declares a trailing
          `whirlpool_program` on all 66 instructions and its Rust declares it on none.
          Verify against the on-chain IDL before treating that as a defect.
      """;

  /// UTF-8 explicitly: the report pads with an em dash, and a JVM started under a
  /// non-UTF-8 default would mangle it into something a diff against a recorded run
  /// would flag for the wrong reason.
  private static final PrintStream OUT =
      new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
  private static final PrintStream ERR =
      new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8);

  /// `\w` and `\s` are Unicode-aware here, matching the Python this replaced. Rust
  /// identifiers are ASCII in practice, so this only ever matters for the `\s*` runs.
  private static Pattern re(final String pattern) {
    return Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
  }


  /// An account list drawn from one side of the comparison. `idx` is Shank's explicit
  /// account index, empty for Anchor; `file` is the source it came from, null for Anchor
  /// because a struct there is located by a whole-tree scan rather than one path.
  private record Struct(List<String> fields, List<Integer> idx, String file) {
  }

  private record Finding(String ix, String kind, List<String> exp, List<String> act,
                         List<int[]> bad, List<String> badExp, List<String> badAct, String file) {
  }

  // ---------------------------------------------------------------------------
  // Rust: Anchor `#[derive(Accounts)]`
  // ---------------------------------------------------------------------------

  private static final Pattern FIELD = re("^(?:pub\\s+)?(\\w+)\\s*:\\s*(.+?),?\\s*$");
  private static final Pattern DERIVE = re("#\\[derive\\(([^)]*)\\)\\]");
  private static final Pattern PUB_STRUCT = re("pub struct (\\w+)\\s*<");

  /// Drop `#[...]` attributes (brace/bracket aware) and `//` comments, keeping a marker
  /// for cfg-gated fields so duplicates can be de-duped.
  private static String stripAttrs(final String body) {
    final var out = new StringBuilder();
    final int n = body.length();
    int i = 0;
    while (i < n) {
      if (body.charAt(i) == '#' && i + 1 < n && body.charAt(i + 1) == '[') {
        int depth = 0;
        int j = i + 1;
        while (j < n) {
          if (body.charAt(j) == '[') {
            ++depth;
          } else if (body.charAt(j) == ']') {
            if (--depth == 0) {
              break;
            }
          }
          ++j;
        }
        i = j + 1;
        continue;
      }
      if (body.startsWith("//", i)) {
        final int j = body.indexOf('\n', i);
        i = j < 0 ? n : j;
        continue;
      }
      out.append(body.charAt(i));
      ++i;
    }
    return out.toString();
  }

  private record Bodies(Map<String, String> bodies, Set<String> eventCpi) {
  }

  private static Bodies structBodies(final String root) {
    final var bodies = new LinkedHashMap<String, String>();
    final var eventCpi = new LinkedHashSet<String>();
    for (final var path : rustFiles(root)) {
      final var text = readLossy(path);
      final var derive = DERIVE.matcher(text);
      while (derive.find()) {
        if (!derive.group(1).contains("Accounts")) {
          continue;
        }
        final int window = Math.min(derive.end() + 800, text.length());
        final var sm = PUB_STRUCT.matcher(text.substring(derive.end(), window));
        if (!sm.find()) {
          continue;
        }
        final var name = sm.group(1);
        final int start = text.indexOf('{', derive.end() + sm.end() - 1);
        if (start < 0) {
          throw new IllegalStateException("no struct body for " + name + " in " + path);
        }
        int depth = 0;
        int i = start;
        while (i < text.length()) {
          if (text.charAt(i) == '{') {
            ++depth;
          } else if (text.charAt(i) == '}') {
            if (--depth == 0) {
              break;
            }
          }
          ++i;
        }
        bodies.put(name, stripAttrs(text.substring(start + 1, i)));
        // #[event_cpi] appends event_authority + program to this struct
        if (text.substring(Math.max(0, derive.start() - 200), derive.start()).contains("event_cpi")) {
          eventCpi.add(name);
        }
      }
    }
    return new Bodies(bodies, eventCpi);
  }

  /// `[(name, type)]` at depth 0, de-duplicated by name (cfg twins).
  private static List<String[]> fieldsOf(final String body) {
    final var out = new ArrayList<String[]>();
    final var seen = new LinkedHashSet<String>();
    final var cur = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < body.length(); ++i) {
      final char ch = body.charAt(i);
      if ("<({[".indexOf(ch) >= 0) {
        ++depth;
      } else if (">)}]".indexOf(ch) >= 0) {
        --depth;
      }
      if (ch == ',' && depth == 0) {
        addField(out, seen, cur.toString());
        cur.setLength(0);
      } else {
        cur.append(ch);
      }
    }
    addField(out, seen, cur.toString());
    return out;
  }

  private static void addField(final List<String[]> out, final Set<String> seen, final String cur) {
    final var m = FIELD.matcher(cur.strip());
    if (m.lookingAt() && seen.add(m.group(1))) {
      out.add(new String[]{m.group(1), m.group(2).strip()});
    }
  }

  /// Anchor flattens nested Accounts structs into the account list, and `#[event_cpi]`
  /// appends event_authority + program to whichever struct carries it — including a
  /// nested one, landing mid-list.
  private static List<String> flatten(final String name,
                                      final Map<String, String> bodies,
                                      final Set<String> eventCpi,
                                      final int depth) {
    final var out = new ArrayList<String>();
    if (depth > 6 || !bodies.containsKey(name)) {
      return out;
    }
    for (final var field : fieldsOf(bodies.get(name))) {
      final var base = field[1].replaceAll("<.*", "").strip();
      if (bodies.containsKey(base)) {                 // composite -> inline its fields
        for (final var s : flatten(base, bodies, eventCpi, depth + 1)) {
          out.add(field[0] + "." + s);
        }
      } else {
        out.add(field[0]);
      }
    }
    if (eventCpi.contains(name)) {
      out.add("event_authority");
      out.add("program");
    }
    return out;
  }

  /// Anchor inlines a nested `#[derive(Accounts)]` composite into the account list, and
  /// `#[event_cpi]` appends `event_authority` + `program` to whichever struct carries it —
  /// including a nested one, where the pair lands mid-list. A naive field scan gets both
  /// wrong and reports phantom length mismatches.
  private static Map<String, Struct> anchorStructs(final String root) {
    final var parsed = structBodies(root);
    final var out = new LinkedHashMap<String, Struct>();
    for (final var name : parsed.bodies().keySet()) {
      out.put(name, new Struct(flatten(name, parsed.bodies(), parsed.eventCpi(), 0), List.of(), null));
    }
    return out;
  }

  // ---------------------------------------------------------------------------
  // Rust: Shank indexed attributes
  // ---------------------------------------------------------------------------

  private static final Pattern ACCOUNT_ATTR = re("#\\[account\\(");
  private static final Pattern VARIANT = re("\\n\\s*([A-Z]\\w*)\\s*(?:\\{|\\(|=|,)");
  private static final Pattern LEADING_INDEX = re("\\s*(\\d+)");
  private static final Pattern ATTR_NAME = re("name\\s*=\\s*\"([^\"]+)\"");

  private record Block(int end, String body) {
  }

  /// One entry per `#[account(..)]`, quote- and nest-aware.
  private static List<Block> accountBlocks(final String text) {
    final var out = new ArrayList<Block>();
    final var m = ACCOUNT_ATTR.matcher(text);
    while (m.find()) {
      int i = m.end();
      int depth = 1;
      boolean inStr = false;
      boolean esc = false;
      while (i < text.length() && depth > 0) {
        final char ch = text.charAt(i);
        if (inStr) {
          if (esc) {
            esc = false;
          } else if (ch == '\\') {
            esc = true;
          } else if (ch == '"') {
            inStr = false;
          }
        } else if (ch == '"') {
          inStr = true;
        } else if (ch == '(') {
          ++depth;
        } else if (ch == ')') {
          --depth;
        }
        ++i;
      }
      out.add(new Block(i, text.substring(m.end(), i - 1)));
    }
    return out;
  }

  /// Shank carries an explicit index per account, which the caller should use as a parse
  /// check: indices must read 0..n-1 or the parse drifted (attributes wrap across lines
  /// and `desc` strings may contain parentheses).
  private static Map<String, Struct> shankInstructions(final String path) {
    final var text = stripComments(readLossy(Path.of(path)));
    final var variants = new ArrayList<Object[]>();
    final var vm = VARIANT.matcher(text);
    while (vm.find()) {
      variants.add(new Object[]{vm.start(1), vm.group(1)});
    }
    final var fields = new LinkedHashMap<String, List<String>>();
    final var indices = new LinkedHashMap<String, List<Integer>>();
    for (final var block : accountBlocks(text)) {
      final var idx = LEADING_INDEX.matcher(block.body());
      final var name = ATTR_NAME.matcher(block.body());
      if (!idx.lookingAt() || !name.find()) {
        continue;
      }
      String next = null;
      for (final var v : variants) {
        if ((Integer) v[0] >= block.end()) {
          next = (String) v[1];
          break;
        }
      }
      if (next != null) {
        fields.computeIfAbsent(next, k -> new ArrayList<>()).add(name.group(1));
        indices.computeIfAbsent(next, k -> new ArrayList<>()).add(Integer.parseInt(idx.group(1)));
      }
    }
    final var out = new LinkedHashMap<String, Struct>();
    fields.forEach((name, f) -> out.put(name, new Struct(f, indices.get(name), path)));
    return out;
  }

  // ---------------------------------------------------------------------------
  // Java: generated `*Keys` builders
  // ---------------------------------------------------------------------------

  private static final Pattern KEYS_DECL = re("List<AccountMeta> (\\w+)Keys\\(");
  private static final Pattern KEYS_ADD = re("keys\\.add\\(");
  private static final String LIST_OF = "return List.of(";
  private static final Pattern TERNARY = re("([A-Za-z_]\\w*)\\s*==\\s*null\\s*\\?");
  private static final Pattern REQUIRE_NON_NULL_ELSE = re("requireNonNullElse\\(\\s*([A-Za-z_][\\w.()]*)");
  private static final Pattern CREATE_CALL = re("create\\w+\\(\\s*([A-Za-z_][\\w.()]*)");

  private static List<String> splitTopLevel(final String body) {
    final var out = new ArrayList<String>();
    final var cur = new StringBuilder();
    int depth = 0;
    for (int i = 0; i < body.length(); ++i) {
      final char ch = body.charAt(i);
      if (ch == ',' && depth == 0) {
        out.add(cur.toString());
        cur.setLength(0);
        continue;
      }
      if (ch == '(' || ch == '[') {
        ++depth;
      } else if (ch == ')' || ch == ']') {
        --depth;
      }
      cur.append(ch);
    }
    if (!cur.toString().isBlank()) {
      out.add(cur.toString());
    }
    return out;
  }

  /// The caller-supplied key, seeing through Anchor's absent-optional shapes.
  private static String accountOf(final String entry) {
    final var e = entry.strip();
    final var ternary = TERNARY.matcher(e);
    if (ternary.lookingAt()) {
      return ternary.group(1);
    }
    final var elseCall = REQUIRE_NON_NULL_ELSE.matcher(e);
    if (elseCall.find()) {
      return elseCall.group(1);
    }
    final var create = CREATE_CALL.matcher(e);
    return create.find() ? create.group(1) : null;
  }

  /// Every `*Keys` builder, in both shapes the generator emits.
  ///
  /// A builder with no optional account returns `List.of(..)`; one with an optional account
  /// fills an `ArrayList` with `keys.add(..)` behind a null check. Reading only the first shape
  /// was wrong twice over: 44 builders across this repository were never compared at all, and —
  /// worse — a single regex spanning `Keys\(.*?\)\s*\{\s*return List\.of\(` would run *past* an
  /// `ArrayList` builder and pair its name with the next builder's account list, which produced
  /// 18 confidently-reported mismatches that were nothing of the kind. Locating each declaration
  /// first, then reading only its own body, is what makes a reported difference mean something.
  private static Map<String, List<String>> javaBuilders(final String path) {
    final var text = stripComments(readStrict(Path.of(path)));
    final var out = new LinkedHashMap<String, List<String>>();
    final var decl = KEYS_DECL.matcher(text);
    while (decl.find()) {
      final int sigClose = matchDelim(text, decl.end() - 1, ')');
      final int bodyOpen = text.indexOf('{', sigClose);
      if (bodyOpen < 0) {
        throw new IllegalStateException("no body for " + decl.group(1) + "Keys in " + path);
      }
      final var body = text.substring(bodyOpen + 1, matchDelim(text, bodyOpen, '}'));
      final var accounts = new ArrayList<String>();
      final int listOf = body.indexOf(LIST_OF);
      if (listOf >= 0) {
        final int open = listOf + LIST_OF.length() - 1;
        for (final var entry : splitTopLevel(body.substring(open + 1, matchDelim(body, open, ')')))) {
          addAccount(accounts, entry);
        }
      } else {
        final var add = KEYS_ADD.matcher(body);
        while (add.find()) {
          final int open = add.end() - 1;
          addAccount(accounts, body.substring(open + 1, matchDelim(body, open, ')')));
        }
      }
      out.put(decl.group(1), accounts);
    }
    return out;
  }

  private static void addAccount(final List<String> accounts, final String entry) {
    final var a = accountOf(entry);
    if (a != null) {
      accounts.add(a);
    }
  }

  /// Index of the delimiter matching the one at `open`, skipping string and character literals.
  private static int matchDelim(final String text, final int open, final char close) {
    final char openCh = text.charAt(open);
    int depth = 0;
    for (int i = open; i < text.length(); ++i) {
      final char c = text.charAt(i);
      if (c == '"' || c == '\'') {
        ++i;
        while (i < text.length() && text.charAt(i) != c) {
          i += text.charAt(i) == '\\' ? 2 : 1;
        }
        continue;
      }
      if (c == openCh) {
        ++depth;
      } else if (c == close && --depth == 0) {
        return i;
      }
    }
    throw new IllegalStateException("unbalanced '" + openCh + "' at offset " + open);
  }

  /// Drops `//` and `/* */` comments, leaving string literals intact.
  ///
  /// Both sides need this and for the same reason: commented-out code is not code. Metaplex
  /// keeps two `#[account(18, ..)]` / `#[account(19, ..)]` lines commented out above `Print`,
  /// because those accounts arrive through remaining-accounts instead — counting them made a
  /// correct 18-account client look like it was missing two. Rust `desc="..."` strings carry
  /// prose that can contain `//`, so the scan has to know where a literal starts and ends.
  private static String stripComments(final String text) {
    final var out = new StringBuilder(text.length());
    final int n = text.length();
    int i = 0;
    while (i < n) {
      final char c = text.charAt(i);
      if (c == '"') {
        out.append(c);
        ++i;
        while (i < n) {
          final char d = text.charAt(i);
          out.append(d);
          ++i;
          if (d == '\\' && i < n) {
            out.append(text.charAt(i));
            ++i;
          } else if (d == '"') {
            break;
          }
        }
      } else if (c == '/' && i + 1 < n && text.charAt(i + 1) == '/') {
        while (i < n && text.charAt(i) != '\n') {
          ++i;                                        // the newline itself is kept
        }
      } else if (c == '/' && i + 1 < n && text.charAt(i + 1) == '*') {
        i += 2;
        while (i + 1 < n && !(text.charAt(i) == '*' && text.charAt(i + 1) == '/')) {
          ++i;
        }
        i = Math.min(i + 2, n);
      } else {
        out.append(c);
        ++i;
      }
    }
    return out.toString();
  }

  // ---------------------------------------------------------------------------
  // Comparison
  // ---------------------------------------------------------------------------

  /// Names for the *same* auto-wired account, grouped. `_core` already folds case, punctuation, a
  /// `Key` suffix and a `solanaAccounts.` prefix, so what is left here is genuine aliasing: the
  /// Rust calls it `rent` and the client resolves it as `solanaAccounts.rentSysVar()`.
  ///
  /// **Grouped, not a flat set.** A flat set makes every auto-wired name equal to every other, so a
  /// client passing the system program where the Rust wants rent compares clean — the instruction
  /// still fails on chain, and this tool exists to catch exactly that. Two names match only when
  /// they name the same account.
  private static final List<Set<String>> AUTOWIRED = List.of(
      Set.of("rent", "rentsysvar", "sysvarrent"),
      Set.of("clock", "clocksysvar"),
      Set.of("instructionssysvar", "sysvarinstructions", "instructionsysvaraccount"),
      Set.of("associatedtokenprogram", "associatedtokenaccountprogram"),
      Set.of("memoprogram", "memoprogramv2"),
      Set.of("systemprogram"),
      Set.of("tokenprogram"),
      Set.of("token2022program"));

  private static boolean sameAutowiredAccount(final String x, final String y) {
    for (final var group : AUTOWIRED) {
      if (group.contains(x)) {
        return group.contains(y);
      }
    }
    return false;
  }

  private static String camel(final String sn) {
    final var parts = new ArrayList<String>();
    for (final var p : sn.split("_")) {
      if (!p.isEmpty()) {
        parts.add(p);
      }
    }
    if (parts.isEmpty()) {
      return "";
    }
    final var out = new StringBuilder(parts.getFirst());
    for (int i = 1; i < parts.size(); ++i) {
      final var w = parts.get(i);
      out.append(w.substring(0, 1).toUpperCase(Locale.ROOT)).append(w.substring(1));
    }
    return out.toString();
  }

  private static String core(final String raw) {
    var t = raw.strip();
    int end = t.length();
    while (end > 0 && t.charAt(end - 1) == ')') {
      --end;
    }
    t = t.substring(0, end);
    t = t.replaceAll("Key$", "");
    t = t.replaceAll("^solanaAccounts\\.", "");
    t = t.replaceAll("\\(\\)$", "");
    return t.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
  }

  private record Comparison(int compared, int matched, List<Finding> findings) {
  }

  private static Comparison compare(final Map<String, Struct> rustIn,
                                    final Map<String, List<String>> java,
                                    final String stripSuffix) {
    var rust = rustIn;
    if (stripSuffix != null) {
      final var stripped = new LinkedHashMap<String, Struct>();
      rustIn.forEach((k, v) -> stripped.put(k.replaceAll(stripSuffix + "$", ""), v));
      rust = stripped;
    }
    final var jl = new LinkedHashMap<String, String>();
    for (final var k : java.keySet()) {
      jl.put(k.toLowerCase(Locale.ROOT), k);
    }
    int compared = 0;
    int matched = 0;
    final var findings = new ArrayList<Finding>();
    for (final var e : new TreeMap<>(rust).entrySet()) {
      final var struct = e.getKey();
      final var ix = struct.substring(0, 1).toLowerCase(Locale.ROOT) + struct.substring(1);
      final var key = jl.get(ix.toLowerCase(Locale.ROOT));
      if (key == null) {
        continue;
      }
      ++compared;
      final var exp = e.getValue().fields().stream().map(GroundTruth::camel).toList();
      final var act = List.copyOf(java.get(key));
      if (exp.size() != act.size()) {
        findings.add(new Finding(ix, "LENGTH", exp, act, List.of(), List.of(), List.of(), e.getValue().file()));
        continue;
      }
      final var badIdx = new ArrayList<int[]>();
      final var badExp = new ArrayList<String>();
      final var badAct = new ArrayList<String>();
      for (int i = 0; i < exp.size(); ++i) {
        final var x = core(exp.get(i));
        final var y = core(act.get(i));
        if (!x.equals(y) && !sameAutowiredAccount(x, y)) {
          badIdx.add(new int[]{i});
          badExp.add(exp.get(i));
          badAct.add(act.get(i));
        }
      }
      if (!badIdx.isEmpty()) {
        findings.add(new Finding(ix, "ORDER", exp, act, badIdx, badExp, badAct, e.getValue().file()));
      } else {
        ++matched;
      }
    }
    return new Comparison(compared, matched, findings);
  }

  // ---------------------------------------------------------------------------

  public static void main(final String[] args) {
    System.exit(run(args));
  }

  private static int run(final String[] args) {
    if (args.length < 3) {
      ERR.print(DOC + "\n");
      return 1;
    }
    final var mode = args[0];
    final var src = args[1];
    final var javaPath = args[2];
    String strip = null;
    String dropTrailing = null;
    for (final var arg : Arrays.asList(args).subList(3, args.length)) {
      if (arg.startsWith("--strip-suffix=")) {
        strip = arg.substring("--strip-suffix=".length());
      } else if (arg.startsWith("--drop-trailing=")) {
        dropTrailing = arg.substring("--drop-trailing=".length());
      }
    }

    final Map<String, Struct> rust;
    if (mode.equals("anchor")) {
      rust = anchorStructs(src);
    } else if (mode.equals("shank")) {
      rust = shankInstructions(src);
      final var drifted = new ArrayList<String>();
      rust.forEach((k, v) -> {
        for (int i = 0; i < v.idx().size(); ++i) {
          if (v.idx().get(i) != i) {
            drifted.add(k);
            return;
          }
        }
      });
      if (!drifted.isEmpty()) {
        ERR.println("shank parse drifted (indices not 0..n-1) for: "
            + drifted.subList(0, Math.min(5, drifted.size()))
            + "\nThe attribute scan lost accounts — fix the parser, do not trust this diff.");
        return 1;
      }
    } else {
      ERR.println("unknown mode '" + mode + "'; expected 'anchor' or 'shank'");
      return 1;
    }

    var java = javaBuilders(javaPath);
    if (dropTrailing != null) {
      // Some published IDLs append an account the repo's Rust never declares — Orca adds
      // `whirlpool_program` to all 66 instructions. Verify against the on-chain IDL first,
      // then normalise it away so it does not swamp the diff.
      final var want = core(dropTrailing);
      final var trimmed = new LinkedHashMap<String, List<String>>();
      java.forEach((k, v) -> trimmed.put(k,
          !v.isEmpty() && core(v.getLast()).equals(want) ? v.subList(0, v.size() - 1) : v));
      java = trimmed;
    }
    final var result = compare(rust, java, strip);

    OUT.print("rust structs " + rust.size() + "  java builders " + java.size()
        + "  compared " + result.compared() + "  match " + result.matched()
        + "  differ " + result.findings().size() + "\n");
    if (result.compared() == 0) {
      OUT.print("""

            NOTHING WAS COMPARED — this is a failure to match names, not a pass.
            Check whether the program suffixes its structs (try --strip-suffix=Context).
          """);
      return 1;
    }
    for (final var f : result.findings()) {
      OUT.print("\n### " + f.ix() + " [" + f.kind() + "] rust=" + f.exp().size()
          + " java=" + f.act().size() + "\n");
      if (f.file() != null) {
        OUT.print("    from " + f.file() + "\n");
      }
      if (f.kind().equals("LENGTH")) {
        for (int i = 0; i < Math.max(f.exp().size(), f.act().size()); ++i) {
          final var a = i < f.exp().size() ? f.exp().get(i) : "—";
          final var b = i < f.act().size() ? f.act().get(i) : "—";
          OUT.print(String.format("   [%2d] rust=%-32s java=%s", i, a, b)
              + (core(a).equals(core(b)) ? "" : "  <<<") + "\n");
        }
      } else {
        for (int i = 0; i < f.bad().size(); ++i) {
          OUT.print(String.format("   [%2d] rust=%-32s java=%s",
              f.bad().get(i)[0], f.badExp().get(i), f.badAct().get(i)) + "\n");
        }
      }
    }
    if (!result.findings().isEmpty()) {
      OUT.print("\nTriage before acting — see the traps in this file's docstring and "
          + "docs/PROGRAM_VERIFICATION.md.\n");
    }
    return result.findings().isEmpty() ? 0 : 1;
  }

  // ---------------------------------------------------------------------------
  // Files
  // ---------------------------------------------------------------------------

  /// Sorted, so two machines walking the same tree agree on which of two same-named
  /// structs wins. The Python this replaced took whatever order the filesystem gave.
  private static List<Path> rustFiles(final String root) {
    try (Stream<Path> walk = Files.walk(Path.of(root))) {
      return walk.filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith(".rs"))
          .sorted()
          .toList();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// Undecodable bytes are dropped rather than replaced, matching the `errors='ignore'`
  /// the Rust scan used — a replacement character would shift every offset after it.
  private static String readLossy(final Path path) {
    try {
      return StandardCharsets.UTF_8.newDecoder()
          .onMalformedInput(CodingErrorAction.IGNORE)
          .onUnmappableCharacter(CodingErrorAction.IGNORE)
          .decode(ByteBuffer.wrap(Files.readAllBytes(path)))
          .toString();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// Generated Java is ours and is UTF-8; a decode failure here is a real problem rather
  /// than noise to skip past.
  private static String readStrict(final Path path) {
    try {
      return Files.readString(path);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private GroundTruth() {
  }
}
