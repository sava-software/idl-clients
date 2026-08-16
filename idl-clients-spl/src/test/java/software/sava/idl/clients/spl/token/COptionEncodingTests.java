package software.sava.idl.clients.spl.token;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.Base58;
import software.sava.idl.clients.spl.lut.gen.types.AddressLookupTable;
import software.sava.idl.clients.spl.token.gen.types.AccountState;
import software.sava.idl.clients.spl.token.gen.types.Mint;
import software.sava.idl.clients.spl.token.gen.types.Token;

import java.util.Arrays;
import java.util.OptionalLong;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/// A fixed-slot `COption<T>` is a 4-byte presence tag followed by the value's slot, and the tag is
/// the only part that says whether the value is there.
///
/// The reason it needs its own tests is `pack_coption_key` in spl-token
/// (`token-2022/interface/src/state.rs`): clearing an option writes `*tag = [0; 4]` and **leaves
/// the body alone**. A revoked delegate's public key is therefore still sitting in the account,
/// and any code that reads or matches the body without consulting the tag sees a delegate that is
/// not there. That is a wrong answer, not an error, so nothing reports it.
final class COptionEncodingTests {

  private static final int DELEGATE_TAG = Token.DELEGATE_OPTION_OFFSET;   // 72
  private static final int DELEGATE_BODY = Token.DELEGATE_OFFSET;         // 76

  private static PublicKey key(final int fill) {
    final byte[] k = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(k, (byte) fill);
    return PublicKey.createPubKey(k);
  }

  private static final PublicKey MINT = key(0x11);
  private static final PublicKey OWNER = key(0x22);
  private static final PublicKey DELEGATE = key(0x33);

  /// A 165-byte token account: `tag` at the delegate slot, `body` in it.
  private static byte[] account(final int tag, final PublicKey body) {
    final byte[] data = new byte[Token.BYTES];
    MINT.write(data, Token.MINT_OFFSET);
    OWNER.write(data, Token.OWNER_OFFSET);
    data[DELEGATE_TAG] = (byte) tag;
    if (body != null) {
      body.write(data, DELEGATE_BODY);
    }
    return data;
  }

  private record MemCmp(int offset, byte[] pattern) {

    /// What the RPC node does with the filter, so the assertions below are about matching rather
    /// than about how the filter is spelled.
    boolean matches(final byte[] accountData) {
      return Arrays.equals(accountData, offset, offset + pattern.length, pattern, 0, pattern.length);
    }
  }

  private static MemCmp memCmp(final software.sava.core.rpc.Filter filter) {
    final var m = Pattern.compile("\"offset\":(\\d+),\"bytes\":\"([^\"]+)\"").matcher(filter.toJson());
    assertTrue(m.find(), filter.toJson());
    return new MemCmp(Integer.parseInt(m.group(1)), Base58.decode(m.group(2)));
  }

  /// The defect this pins: a delegate filter that compares only the 32-byte body matches an
  /// account whose delegate was revoked, because the revoke left those bytes behind.
  @Test
  void aRevokedDelegateDoesNotMatchTheDelegateFilter() {
    final byte[] active = account(1, DELEGATE);
    final byte[] revoked = account(0, DELEGATE);   // tag cleared, body left as the chain leaves it

    // Both accounts carry DELEGATE's bytes in the body; only one has it as its delegate.
    assertTrue(Arrays.equals(active, DELEGATE_BODY, DELEGATE_BODY + 32,
        revoked, DELEGATE_BODY, DELEGATE_BODY + 32), "the premise: the body survives a revoke");

    // What the filter used to be: the 32-byte key alone, anchored at the body. Spelled out here
    // rather than left to the commit history, because it is the whole reason the tag is in the
    // pattern now — and because a filter that quietly went back to this shape would still pass
    // every assertion that only looks at the active account.
    final var bodyOnly = new MemCmp(DELEGATE_BODY, DELEGATE.toByteArray());
    assertTrue(bodyOnly.matches(active));
    assertTrue(bodyOnly.matches(revoked), "the defect: it matched the revoked account too");

    final var filter = memCmp(Token.createDelegateFilter(DELEGATE));
    assertEquals(DELEGATE_TAG, filter.offset(), "the filter must start at the tag, not the body");
    assertEquals(36, filter.pattern().length, "4-byte tag plus the 32-byte key");

    assertTrue(filter.matches(active), "an account that does delegate to it must still match");
    assertFalse(filter.matches(revoked), "an account that revoked it must not");
  }

  /// The body is unreliable once the tag is zero, so asking for "no delegate" can only mean the
  /// tag. This is why an absent filter exists at all rather than callers passing a zero key.
  @Test
  void theAbsentFilterMatchesTheTagAndIgnoresTheBody() {
    final var absent = memCmp(Token.createDelegateAbsentFilter());
    assertEquals(DELEGATE_TAG, absent.offset());
    assertArrayEquals(new byte[4], absent.pattern(), "four zero bytes, and nothing about the body");

    assertTrue(absent.matches(account(0, DELEGATE)), "revoked, body still dirty");
    assertTrue(absent.matches(account(0, null)), "never set");
    assertFalse(absent.matches(account(1, DELEGATE)), "actively delegated");
  }

  /// Writing an absent option has to clear the tag. Skipping the slot leaves whatever the buffer
  /// held, and since a non-zero tag reads as present, the result is a delegate assembled out of
  /// unrelated bytes — returned without error.
  @Test
  void anAbsentDelegateWrittenIntoADirtyBufferReadsBackAbsent() {
    final var token = Token.read(account(1, DELEGATE), 0);
    assertEquals(DELEGATE, token.delegate(), "premise");

    final var cleared = new Token(token._address(), token.mint(), token.owner(), token.amount(),
        null, token.state(), token.isNative(), token.delegatedAmount(), token.closeAuthority());

    final byte[] dirty = new byte[Token.BYTES];
    Arrays.fill(dirty, (byte) 0xFF);
    cleared.write(dirty, 0);

    assertEquals(0, dirty[DELEGATE_TAG], "the absent branch has to clear the tag, not step over it");
    assertNull(Token.read(dirty, 0).delegate(), "a cleared delegate must not come back from the buffer");

    // What stepping over it left behind: the buffer's own bytes as a tag and a key. The reader
    // now refuses that rather than returning a public key assembled out of unrelated memory.
    final byte[] skipped = new byte[Token.BYTES];
    Arrays.fill(skipped, (byte) 0xFF);
    MINT.write(skipped, Token.MINT_OFFSET);
    OWNER.write(skipped, Token.OWNER_OFFSET);
    assertThrows(RuntimeException.class, () -> Token.read(skipped, 0),
        "0xFFFFFFFF is not a presence tag");
  }

  /// `unpack_coption_key` accepts `[0,0,0,0]` or `[1,0,0,0]` and returns InvalidAccountData for
  /// anything else. A third value does not mean present — it means these bytes are not the option
  /// they are being read as, and every field after it is at the wrong offset.
  @Test
  void aPresenceTagThatIsNeitherZeroNorOneIsRejected() {
    assertNotNull(Token.read(account(0, null), 0));
    assertNotNull(Token.read(account(1, DELEGATE), 0));

    for (final int tag : new int[]{2, 3, 0x7F, 0xFF}) {
      final byte[] data = account(tag, DELEGATE);
      assertThrows(RuntimeException.class, () -> Token.read(data, 0), "tag " + tag);
    }
  }

  /// The other four emitted sites, and the one distinction between them.
  ///
  /// The tests above pin `Token.delegate`. Every optional key field goes through the same emission
  /// path, so a regression would hit all of them at once — but they do not all have the same tag.
  /// SPL Token and Mint are `COption`, a **4-byte** tag; the address lookup table's authority is a
  /// Borsh `Option`, a **1-byte** tag. A generator that assumed one width for the whole family
  /// would place the LUT's body three bytes off and produce a filter that silently matches nothing,
  /// which no round trip would catch because the writer would be wrong the same way.
  ///
  /// The invariant asserted is the one that ties the two emitted constants together:
  /// `OPTION_OFFSET + tagWidth == FIELD_OFFSET`, with the filter anchored at the tag and its
  /// pattern exactly the tag plus the value.
  @Test
  void everyOptionalKeyFilterIsAnchoredAtItsOwnTagWidth() {
    record Site(String name, int optionOffset, int fieldOffset, int tagWidth,
                software.sava.core.rpc.Filter present, software.sava.core.rpc.Filter absent) {
    }

    final var lutAuthority = key(0x44);
    for (final var site : java.util.List.of(
        new Site("Token.delegate", Token.DELEGATE_OPTION_OFFSET, Token.DELEGATE_OFFSET, 4,
            Token.createDelegateFilter(DELEGATE), Token.createDelegateAbsentFilter()),
        new Site("Token.closeAuthority", Token.CLOSE_AUTHORITY_OPTION_OFFSET, Token.CLOSE_AUTHORITY_OFFSET, 4,
            Token.createCloseAuthorityFilter(DELEGATE), Token.createCloseAuthorityAbsentFilter()),
        new Site("Mint.mintAuthority", Mint.MINT_AUTHORITY_OPTION_OFFSET, Mint.MINT_AUTHORITY_OFFSET, 4,
            Mint.createMintAuthorityFilter(DELEGATE), Mint.createMintAuthorityAbsentFilter()),
        new Site("Mint.freezeAuthority", Mint.FREEZE_AUTHORITY_OPTION_OFFSET, Mint.FREEZE_AUTHORITY_OFFSET, 4,
            Mint.createFreezeAuthorityFilter(DELEGATE), Mint.createFreezeAuthorityAbsentFilter()),
        // The odd one out, and the reason this sweep exists rather than four copies of the above.
        new Site("AddressLookupTable.authority", AddressLookupTable.AUTHORITY_OPTION_OFFSET,
            AddressLookupTable.AUTHORITY_OFFSET, 1,
            AddressLookupTable.createAuthorityFilter(lutAuthority),
            AddressLookupTable.createAuthorityAbsentFilter()))) {

      assertEquals(site.fieldOffset(), site.optionOffset() + site.tagWidth(),
          site.name() + ": the value must begin exactly one tag past the option offset");

      final var present = memCmp(site.present());
      assertEquals(site.optionOffset(), present.offset(), site.name() + ": anchored at the tag");
      assertEquals(site.tagWidth() + PublicKey.PUBLIC_KEY_LENGTH, present.pattern().length,
          site.name() + ": the pattern is the tag plus the key");
      assertEquals(1, present.pattern()[0], site.name() + ": present is tag 1");
      for (int i = 1; i < site.tagWidth(); ++i) {
        assertEquals(0, present.pattern()[i], site.name() + ": the tag's high bytes are zero");
      }

      final var absent = memCmp(site.absent());
      assertEquals(site.optionOffset(), absent.offset(), site.name() + ": anchored at the tag");
      assertArrayEquals(new byte[site.tagWidth()], absent.pattern(),
          site.name() + ": absent is a zero tag and nothing about the body");
    }
  }

  /// The other direction of the same layout: an option that is present round-trips through the
  /// tag, so a writer that emits the value without the tag would fail here too.
  @Test
  void aPresentOptionRoundTrips() {
    final var token = Token.read(account(1, DELEGATE), 0);
    final byte[] out = new byte[Token.BYTES];
    assertEquals(Token.BYTES, token.write(out, 0));
    assertEquals(1, out[DELEGATE_TAG]);
    assertEquals(DELEGATE, Token.read(out, 0).delegate());
    assertEquals(OptionalLong.empty(), Token.read(out, 0).isNative());
    assertEquals(AccountState.uninitialized, Token.read(out, 0).state());
  }
}
