package software.sava.idl.clients.spl.token_2022;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.token.Token2022;
import software.sava.core.accounts.token.Token2022Account;
import software.sava.core.accounts.token.extensions.AccountType;
import software.sava.core.accounts.token.extensions.TokenExtension;
import software.sava.core.accounts.token.extensions.UnknownTokenExtension;
import software.sava.core.serial.Serializable;
import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.spl.token_2022.gen.types.Extension;
import software.sava.idl.clients.spl.token_2022.gen.types.Mint;
import software.sava.idl.clients.spl.token_2022.gen.types.Token;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/// Jazzer entry point for the two generated Token-2022 account decoders. The first fuzzer
/// byte picks one — even selects [Mint#read(PublicKey, byte[])], odd selects
/// [Token#read(PublicKey, byte[])] — and everything after it is the account data, so one
/// campaign drives both the mint and the token-account layout plus the whole TLV extension
/// table hanging off each. This is where hostile bytes arrive for a reader: an indexer or
/// wallet decodes whatever an RPC node hands back for an address it was told is a mint.
///
/// Malformed-input contract: garbage in -> [RuntimeException] out. Truncation, an option
/// tag that is neither 0 nor 1, a TLV length the buffer cannot back, an extension whose
/// declared length disagrees with its layout, and an account-type byte that is not the
/// decoder's own are all rejections; any other throwable is a finding.
///
/// The one rejection the readers do not raise is an unknown enum ordinal. Since the
/// 2026-08-15 change to `SerDeUtil.read`, an ordinal naming no variant decodes as null
/// rather than throwing, and [Token]'s `state` plus the `defaultAccountState` extension
/// both hold such a slot. A record carrying that sentinel cannot be serialized, because
/// `l()` and `write` dereference it, so the harness walks the parsed record for null
/// enum-typed components and treats one as a rejection — the same move
/// [Token2022IxDataFuzz] makes. A null that is an absent `Option` is not one. Such an
/// input is also outside the differential below; divergence 4 says why.
///
/// On a successful read the harness asserts:
///
/// - `l()` equals the number of bytes `write` reports having written;
/// - `l()` never exceeds the input the record was read from — a record claiming more
///   bytes than it was given has decoded fields nobody sent;
/// - re-reading the bytes `write` produced yields an equal record. Equality is
///   component-wise and array-aware, because the generated records take the default
///   record `equals`, which compares `byte[]` components by reference: `extensions()` is
///   an [Extension] array of nested records, and `tokenMetadata` alone carries three
///   `byte[]` string bodies and a `Map<String, String>`, while `confidentialTransferFee`
///   and friends hold `EncryptedBalance`/`DecryptableBalance` records wrapping a
///   `byte[]`. A plain `equals` would report those unequal to themselves.
///
/// ## The differential against sava-core
///
/// sava-core ships hand-written readers for the same two accounts — `Token2022.read` and
/// `Token2022Account.read`, at the 25.11.1 release the Solana BOM pins — written from the SPL sources
/// rather than generated from the IDL. Two independent decoders of one wire format are a
/// differential oracle, so whenever sava-core reads an input the generated decoder must
/// read it too, byte-identically and with the same extension list.
///
/// The oracle fires only when sava-core's own answer is unambiguous, which means all of:
///
/// - sava-core decoded the bytes without throwing;
/// - its `write` into `new byte[l()]` reproduces the input exactly, so it consumed and
///   accounted for every byte rather than stopping early or normalising something;
/// - its account type is the one the selected reader is for — `Mint` for [Mint#read],
///   `Account` for [Token#read] — or null for an account of exactly `Mint::LEN` or
///   `Account::LEN`, which carries no type byte at all;
/// - none of its `tokenExtensions()` is an [UnknownTokenExtension], i.e. every TLV was
///   one it has a layout for.
///
/// Two more exclusions used to stand here for what sava-core 25.11.0 did: it threw on an
/// extension-free account (`Mint::LEN` or `Account::LEN` exactly) and decoded a `COption`
/// presence tag other than 0 or 1. 25.11.1 decodes the former like the generated readers, so
/// those inputs are inside the differential, and refuses the latter like them, so those are
/// outside it by its own rejection. Each exclusion that remains is a documented divergence
/// between the two decoders rather than a hole in the oracle:
///
/// 1. **Any account-type byte but the selected reader's own.** The generated readers model
///    the byte at [#ACCOUNT_TYPE_OFFSET] as a fixed constant — 1 for a mint, 2 for a token
///    account — and refuse anything else, which is what the upstream JS client does when it
///    declines to unpack such an account. sava-core instead reads the byte as an
///    `AccountType` and carries whatever it finds: 0 (re-allocated but not yet initialised)
///    becomes `Uninitialized`, a byte naming no variant becomes null, and — the case that
///    matters — a byte naming the *other* kind still decodes, because neither hand-written
///    reader checks that the byte agrees with the layout it has just read. `Token2022Account`
///    will read a real mint, and write it back verbatim, type byte included. So past the base
///    length the guard requires the type to be the selected reader's own rather than merely
///    known and non-zero: otherwise the reference is decoding a different account than the
///    subject.
///    Found by this harness on its first campaign (the `Token` selector over a 435-byte
///    mint, where `Token.read` rightly refuses the 1 at offset 165).
/// 2. **Unknown extension types.** Token-2022 keeps adding `ExtensionType` variants, so a
///    client older than the program is the ordinary case. sava-core keeps the bytes as an
///    [UnknownTokenExtension] and leaves the caller to decide; the generated reader throws
///    `Unknown Extension at offset N`, because it has no length for the TLV and so cannot
///    find where the next one starts.
/// 3. **`Uninitialized` padding.** A zero type word ends the TLV list as far as the
///    program is concerned: it, sava-core, and the upstream JS client's hand-written walk
///    all stop there and ignore whatever follows. The generated decoder instead decodes
///    what the IDL declares, reading each zero word as an `Extension.uninitialized`
///    element and carrying on — which is what lets it write the padding back. So the
///    extension sequences are compared by truncating the generated list at its first
///    ordinal 0 and dropping sava-core's lone `Uninitialized` entry, after which the two
///    orders must match exactly. They can: sava-core holds its extensions in a
///    `LinkedHashSet` in wire order and refuses duplicates, so whenever the oracle fires
///    there is one canonical order.
///
///    The byte comparison is dropped for the one shape the truncation hides — padding
///    with a real TLV behind it. There the two readers legitimately re-encode to
///    different bytes, because the reference drops everything past the zero word while
///    the generated writer emits it again, and the program never writes such an account
///    in the first place: extension space is appended, so the zeroed tail is always last.
///    A tail that is *only* zero words stays inside the comparison — both writers put it
///    back verbatim.
/// 4. **Enum bytes the reference never validates.** `DefaultAccountState` holds its state as
///    a raw `int` and writes it back, so sava-core reproduces a `defaultAccountState` extension whose state byte is
///    49 while the generated `AccountState.read` answers null for it — and the program's own
///    `#[repr(u8)] AccountState` has three variants and rejects the rest. Because the
///    reference does not model these bytes as an enum at all, "sava-core succeeded" carries
///    no information about them, so an input the generated reader turns into a null
///    enum-typed slot leaves the differential rather than failing it. The `Token.state`
///    byte is the one enum sava-core does model (`TokenAccount.parseState`, which refuses a
///    byte past the last variant), so that slot is excluded by the reference's own rejection.
///    Found by this harness (`Mint`, 657 bytes, `defaultAccountState` state byte 0x31).
///
/// Seeded with the 25-account mainnet corpus under `src/test/resources/token_2022/accounts`,
/// one seed per account per applicable selector; the corpus README beside
/// `src/test/resources/fuzz` says what each seed is, and the accounts' provenance lives
/// beside the accounts themselves in `token_2022/accounts/manifest.json`.
///
/// Deliberately free of Jazzer imports so it compiles with the regular test sources.
///
/// Run with `./gradlew :idl-clients-spl:fuzzToken2022Account [-PmaxFuzzTime=<seconds>]`.
public final class Token2022AccountFuzz {

  /// Where both layouts keep the account-type discriminator: [Token#EXTENSIONS_OFFSET],
  /// and [Mint#EXTENSIONS_OFFSET] plus the 83 bytes a Token-2022 mint is padded with so
  /// that it cannot be confused with an `Account::LEN` account.
  private static final int ACCOUNT_TYPE_OFFSET = 165;

  /// The address is metadata the caller supplies, not part of the encoding: neither
  /// `write` nor `l()` touches it. A fixed key keeps the `_address` component equal across
  /// a read and a re-read so the round trip compares only decoded bytes.
  private static final PublicKey ADDRESS = PublicKey.NONE;

  private static final int[] NO_ORDINALS = new int[0];

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length < 2) {
      // one selector byte and at least one account byte; empty account data reads as null
      return;
    }
    final boolean asMint = (data[0] & 1) == 0;
    final byte[] account = Arrays.copyOfRange(data, 1, data.length);
    final String name = asMint ? "Mint" : "Token";

    // Ask sava-core first: its answer decides whether a rejection below is allowed.
    final int[] reference = referenceOrdinals(asMint, account);

    final SerDe parsed;
    try {
      parsed = asMint ? Mint.read(ADDRESS, account) : Token.read(ADDRESS, account);
    } catch (final RuntimeException rejected) {
      if (reference != null) {
        throw new IllegalStateException(
            name + " rejected " + account.length + " bytes that sava-core decodes and writes back verbatim",
            rejected);
      }
      // garbage in -> RuntimeException out is the documented contract
      return;
    }
    if (parsed == null) {
      // read() answers null for null or empty data, which the length guard above excludes
      throw new IllegalStateException(name + " returned null for " + account.length + " bytes");
    }
    if (hasUnknownVariant(parsed, parsed.getClass())) {
      // An ordinal naming no variant reads as null; the record cannot be written, so this
      // input is a rejection rather than a parse. It is outside the differential too, and
      // not merely unasserted: the reference keeps such a byte as a raw int and writes it
      // straight back, so its success says nothing about whether the byte named a variant
      // (divergence 4).
      return;
    }

    final int length = parsed.l();
    if (length > account.length) {
      throw new IllegalStateException(String.format(
          "%s claims %d bytes after reading %d", name, length, account.length
      ));
    }
    final byte[] canonical = new byte[length];
    final int wrote = parsed.write(canonical, 0);
    if (wrote != length) {
      throw new IllegalStateException(String.format(
          "%s wrote %d bytes but l() is %d", name, wrote, length
      ));
    }

    final var reRead = asMint ? Mint.read(ADDRESS, canonical) : Token.read(ADDRESS, canonical);
    if (!sameValue(parsed, reRead)) {
      throw new IllegalStateException(name + " does not round trip through write then read");
    }

    if (reference == null) {
      return;
    }
    final var extensions = extensionsOf(parsed);
    if (!buriedPadding(extensions) && !Arrays.equals(canonical, account)) {
      throw new IllegalStateException(String.format(
          "%s re-encodes to %d bytes that differ from the input sava-core reproduces verbatim",
          name, canonical.length
      ));
    }
    final int[] decoded = ordinalsBeforePadding(extensions);
    if (!Arrays.equals(reference, decoded)) {
      throw new IllegalStateException(String.format(
          "%s decoded extensions %s where sava-core decoded %s",
          name, Arrays.toString(decoded), Arrays.toString(reference)
      ));
    }
  }

  /// The extension ordinals sava-core decodes from these bytes, or null when the
  /// differential does not apply. See the class documentation for what each exclusion
  /// stands for. Ordinal 0 is dropped: sava-core reports it only as a lone `Uninitialized`
  /// standing for an all-padding extension region, never alongside a real extension.
  static int[] referenceOrdinals(final boolean asMint, final byte[] account) {
    try {
      // An extension-free account — Mint::LEN or Account::LEN exactly — carries no account-type
      // byte, and sava-core reads null there; anything longer must name the selected reader's
      // own kind (divergence 1).
      final Serializable core;
      final Collection<TokenExtension> extensions;
      if (asMint) {
        final var mint = Token2022.read(ADDRESS, account);
        if (mint == null
            || (account.length != Mint.EXTENSIONS_OFFSET && mint.accountType() != AccountType.Mint)) {
          return null;
        }
        core = mint;
        extensions = mint.tokenExtensions();
      } else {
        final var token = Token2022Account.read(ADDRESS, account);
        if (token == null
            || (account.length != Token.EXTENSIONS_OFFSET && token.type() != AccountType.Account)) {
          return null;
        }
        core = token;
        extensions = token.tokenExtensions();
      }
      for (final var extension : extensions) {
        if (extension instanceof UnknownTokenExtension) {
          return null;
        }
      }
      final byte[] rewritten = new byte[core.l()];
      if (rewritten.length != account.length) {
        return null;
      }
      core.write(rewritten, 0);
      if (!Arrays.equals(rewritten, account)) {
        return null;
      }
      return extensions.stream().mapToInt(TokenExtension::ordinal).filter(o -> o != 0).toArray();
    } catch (final RuntimeException referenceRejected) {
      // the reference is an oracle, not a subject: whatever it refuses is simply outside
      // the differential
      return null;
    }
  }

  /// Whether the sava-core differential applies to a whole fuzzer input, selector byte
  /// included. Only `Token2022AccountFuzzSeedTests` calls this, to hold the seed corpus to
  /// exercising the oracle rather than only the round trip.
  static boolean differentialApplies(final byte[] data) {
    return data.length >= 2
        && referenceOrdinals((data[0] & 1) == 0, Arrays.copyOfRange(data, 1, data.length)) != null;
  }

  private static Extension[] extensionsOf(final SerDe parsed) {
    return parsed instanceof Mint mint ? mint.extensions() : ((Token) parsed).extensions();
  }

  /// The ordinals the program would see: everything up to, and excluding, the first zero
  /// type word, which is where its own TLV walk stops.
  private static int[] ordinalsBeforePadding(final Extension[] extensions) {
    if (extensions == null) {
      return NO_ORDINALS;
    }
    int end = 0;
    while (end < extensions.length && extensions[end].ordinal() != 0) {
      ++end;
    }
    final int[] ordinals = new int[end];
    for (int i = 0; i < end; ++i) {
      ordinals[i] = extensions[i].ordinal();
    }
    return ordinals;
  }

  /// Whether a real extension sits behind a zero type word — the one decoded shape whose
  /// re-encoding the two readers cannot be held to agree on, and one the program never
  /// writes. A tail of nothing but zero words is not it: both writers put that back
  /// verbatim.
  private static boolean buriedPadding(final Extension[] extensions) {
    if (extensions == null) {
      return false;
    }
    boolean padded = false;
    for (final var extension : extensions) {
      if (extension.ordinal() == 0) {
        padded = true;
      } else if (padded) {
        return true;
      }
    }
    return false;
  }

  private static final RecordComponent[] NO_COMPONENTS = new RecordComponent[0];

  private static final ClassValue<RecordComponent[]> RECORD_COMPONENTS = new ClassValue<>() {
    @Override
    protected RecordComponent[] computeValue(final Class<?> type) {
      return type.isRecord() ? type.getRecordComponents() : NO_COMPONENTS;
    }
  };

  /// Walks the acyclic generated record graph for a null in an enum-typed slot, which is
  /// how `SerDeUtil` reports an ordinal that names no variant. Only enum-typed slots
  /// count: an absent `Option` is null too and is a perfectly serializable value.
  /// Reflection failures are harness failures, not malformed input, so they escape.
  private static boolean hasUnknownVariant(final Object value, final Class<?> declaredType) {
    if (declaredType.isArray()) {
      if (value == null) {
        return false;
      }
      final var elementType = declaredType.getComponentType();
      if (elementType.isPrimitive()) {
        return false;
      }
      for (final var element : (Object[]) value) {
        if (hasUnknownVariant(element, elementType)) {
          return true;
        }
      }
      return false;
    }
    if (value == null) {
      return Enum.class.isAssignableFrom(declaredType) || RustEnum.class.isAssignableFrom(declaredType);
    }
    for (final var component : RECORD_COMPONENTS.get(value.getClass())) {
      if (hasUnknownVariant(componentValue(component, value), component.getType())) {
        return true;
      }
    }
    return false;
  }

  /// Record equality the generated records cannot express themselves: the default record
  /// `equals` compares a `byte[]` component by reference, so two records read from the
  /// same bytes are unequal whenever one carries an array. Compares component-wise
  /// instead, with array-aware equality at every level.
  private static boolean sameValue(final Object left, final Object right) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null || left.getClass() != right.getClass()) {
      return false;
    }
    final var type = left.getClass();
    if (type.isArray()) {
      if (type.getComponentType().isPrimitive()) {
        return Objects.deepEquals(left, right);
      }
      final var leftElements = (Object[]) left;
      final var rightElements = (Object[]) right;
      if (leftElements.length != rightElements.length) {
        return false;
      }
      for (int i = 0; i < leftElements.length; ++i) {
        if (!sameValue(leftElements[i], rightElements[i])) {
          return false;
        }
      }
      return true;
    }
    final var components = RECORD_COMPONENTS.get(type);
    if (components.length == 0) {
      // enums, strings, optionals, maps, public keys and no-component enum variants
      // compare themselves
      return left.equals(right);
    }
    for (final var component : components) {
      if (!sameValue(componentValue(component, left), componentValue(component, right))) {
        return false;
      }
    }
    return true;
  }

  private static Object componentValue(final RecordComponent component, final Object record) {
    try {
      return component.getAccessor().invoke(record);
    } catch (final ReflectiveOperationException reflectionFailure) {
      throw new AssertionError("Could not inspect " + component, reflectionFailure);
    }
  }

  private Token2022AccountFuzz() {
  }
}
