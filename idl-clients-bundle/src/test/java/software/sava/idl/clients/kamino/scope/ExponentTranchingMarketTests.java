package software.sava.idl.clients.kamino.scope;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.lookup.AddressLookupTable;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;
import software.sava.rpc.json.http.response.AccountInfo;
import software.sava.rpc.json.http.response.Context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.kamino.scope.ExponentTranchingMarket.CpiInterfaceContext;

/// Pins [ExponentTranchingMarket] to two live markets and their lookup tables, captured
/// from mainnet at slot 452336032 (2026-10-01). The expected keys and flags were decoded
/// independently of this class, by following Scope's `exponent-itf` `MarketCpiConfig`
/// reader, and the decode was checked against chain: the lookup table is owned by the
/// lookup table program, the SY program is executable, and the return model storage is
/// owned by the tranching program. `AsX2JX…` lists four `get_sy_state` accounts, the last
/// of them the klend feed's own `OraclePrices`; `DzL8Sjf…` lists six, four writable, and
/// has no LP supply on either side, so a refresh of it is accepted but never prices.
final class ExponentTranchingMarketTests {

  private static final PublicKey MARKET = PublicKey.fromBase58Encoded("AsX2JXshsKwfekXBsGjAUTiJevBL6GWezxhNqe1gDwTc");
  private static final PublicKey MARKET_LOOKUP_TABLE = PublicKey.fromBase58Encoded("B1Z3EZyA6udpHdEBDcsnT1YwzuuPg6zMk1oNKNT5odFc");
  private static final PublicKey MARKET_RETURN_MODEL_STORAGE = PublicKey.fromBase58Encoded("7ABHuGbUhLun6qoYYhtPCpNdQsjcdLjzUdjQsaxueCXo");

  private static final PublicKey WIDE_MARKET = PublicKey.fromBase58Encoded("DzL8SjfkrPnybYfSh82QDhhFq2Nby2XVDnKRoTehkMsz");
  private static final PublicKey WIDE_LOOKUP_TABLE = PublicKey.fromBase58Encoded("9tY5hZYEjr9dm38u1C4SUNXd2mD6wTXYFSkwidNgYwof");
  private static final PublicKey WIDE_RETURN_MODEL_STORAGE = PublicKey.fromBase58Encoded("GchAqnMbBxGid5sgY1Ld8ePGTL23px1E89WfARpP3f8z");

  private static final PublicKey SY_PROGRAM = PublicKey.fromBase58Encoded("XP1BRLn8eCYSygrd8er5P4GKdzqKbC3DLoSsS5UYVZy");

  /// Where `AsX2JX…`'s `get_sy_state` length sits: the discriminator, the 1273 fixed bytes,
  /// then two role lists of one key each.
  private static final int GET_SY_STATE_LENGTH_OFFSET = 8 + 1273 + (4 + 32) * 2;
  /// The first byte past the four `get_sy_state` contexts, which is all the reader needs.
  private static final int MARKET_PARSED_END = GET_SY_STATE_LENGTH_OFFSET + 4 + 4 * 3;

  private static byte[] fixture(final String name) {
    try (final var in = ExponentTranchingMarketTests.class.getResourceAsStream("/scope/exponent_tranching/" + name + ".base64")) {
      assertNotNull(in, name);
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static byte[] marketData() {
    return fixture("market-" + MARKET);
  }

  private static AddressLookupTable lookupTable() {
    return AddressLookupTable.read(MARKET_LOOKUP_TABLE, fixture("lookupTable-" + MARKET_LOOKUP_TABLE));
  }

  private static AccountMeta write(final String key) {
    return AccountMeta.createWrite(PublicKey.fromBase58Encoded(key));
  }

  private static AccountMeta read(final String key) {
    return AccountMeta.createRead(PublicKey.fromBase58Encoded(key));
  }

  @Test
  void readsTheCpiConfigScopeReads() {
    final var market = ExponentTranchingMarket.read(MARKET, marketData());
    assertEquals(MARKET, market.address());
    assertEquals(MARKET_LOOKUP_TABLE, market.addressLookupTable());
    assertEquals(SY_PROGRAM, market.syProgram());
    assertEquals(MARKET_RETURN_MODEL_STORAGE, market.returnModelStorage());
    assertEquals(List.of(
        new CpiInterfaceContext(0, false, true),
        new CpiInterfaceContext(1, false, false),
        new CpiInterfaceContext(2, false, false),
        new CpiInterfaceContext(3, false, true)
    ), market.getSyState());

    final var wide = ExponentTranchingMarket.read(WIDE_MARKET, fixture("market-" + WIDE_MARKET));
    assertEquals(WIDE_LOOKUP_TABLE, wide.addressLookupTable());
    assertEquals(SY_PROGRAM, wide.syProgram());
    assertEquals(WIDE_RETURN_MODEL_STORAGE, wide.returnModelStorage());
    assertEquals(List.of(
        new CpiInterfaceContext(0, false, true),
        new CpiInterfaceContext(1, false, false),
        new CpiInterfaceContext(2, false, false),
        new CpiInterfaceContext(3, false, true),
        new CpiInterfaceContext(4, false, true),
        new CpiInterfaceContext(5, false, true)
    ), wide.getSyState());
  }

  @Test
  void readsAnAccountInfoAsItsKeyAndData() {
    final var data = marketData();
    final var accountInfo = new AccountInfo<>(MARKET, new Context(0L, null), false, 0L,
        ExponentTranchingMarket.PROGRAM_ID, BigInteger.ZERO, 0, data);
    assertEquals(ExponentTranchingMarket.read(MARKET, data), ExponentTranchingMarket.read(accountInfo));
  }

  /// The order `exponent_tranching::get_price` takes them in: the market as the entry's own
  /// account, then return model storage, lookup table, SY program, event authority and
  /// program, then the `get_sy_state` accounts resolved through the market's table.
  @Test
  void refreshAccountsFollowTheOrderScopeConsumesThem() {
    final var accounts = ExponentTranchingMarket.read(MARKET, marketData()).refreshAccounts(lookupTable());
    assertEquals(List.of(
        write("AsX2JXshsKwfekXBsGjAUTiJevBL6GWezxhNqe1gDwTc"),
        write("7ABHuGbUhLun6qoYYhtPCpNdQsjcdLjzUdjQsaxueCXo"),
        read("B1Z3EZyA6udpHdEBDcsnT1YwzuuPg6zMk1oNKNT5odFc"),
        read("XP1BRLn8eCYSygrd8er5P4GKdzqKbC3DLoSsS5UYVZy"),
        read("3mBi7DRWMdTdDghA1cVLrwDKAgDo7UTDWoeik4GkXCsf"),
        read("XPTrnchoawiUc9iYJrpfchS8vgr8Y5X2QGBdHPXukty"),
        write("2JqgD3RkGVUk62rrkULcNLS8y9qKgpd5VQfVF3PiMCdZ"),
        read("5NF7dsQ1w3bP7BZ3twpMSFL5sJ3CgtuV9iCfLSpc3aSL"),
        read("DDUMt9bExDCNXLatJGmmqqKbUhf8yWvfLihkLkMG944P"),
        write("3t4JZcueEzTbVP6kLxXrL3VpWx45jDer4eqysweBchNH")
    ), accounts);
    assertThrows(UnsupportedOperationException.class, () -> accounts.add(read("11111111111111111111111111111111")));

    final var wide = ExponentTranchingMarket.read(WIDE_MARKET, fixture("market-" + WIDE_MARKET))
        .refreshAccounts(AddressLookupTable.read(WIDE_LOOKUP_TABLE, fixture("lookupTable-" + WIDE_LOOKUP_TABLE)));
    assertEquals(List.of(
        write("DzL8SjfkrPnybYfSh82QDhhFq2Nby2XVDnKRoTehkMsz"),
        write("GchAqnMbBxGid5sgY1Ld8ePGTL23px1E89WfARpP3f8z"),
        read("9tY5hZYEjr9dm38u1C4SUNXd2mD6wTXYFSkwidNgYwof"),
        read("XP1BRLn8eCYSygrd8er5P4GKdzqKbC3DLoSsS5UYVZy"),
        read("3mBi7DRWMdTdDghA1cVLrwDKAgDo7UTDWoeik4GkXCsf"),
        read("XPTrnchoawiUc9iYJrpfchS8vgr8Y5X2QGBdHPXukty"),
        write("EbYQEZqyenEkCSepMBdmeseDzMg4qijA7XDJqmkWuEpQ"),
        read("2qJMSFpR8UgRcPoBnQ6MUmyvuAqcrfDgsmrp7v6KDrNT"),
        read("FU296aNM9AzBvX8WQYPrsyT1frUfygv6qizcjQf8GukL"),
        write("EZ8sq2FNnmqQo254irAMGNp7c6B7DPuKh22SyXAwuXSn"),
        write("6kkHmkaStWwVvicq36Zmjz6CaCZKUwxLzQmbfgBtwU7W"),
        write("D6v98uJEmGJsi3kMa8fEL4aJ8VByQd9oZxwAcCH1Xycx")
    ), wide);
  }

  /// Both live markets happen to list their table's first entries in order, which a reader
  /// using the list position instead of each context's index would also reproduce.
  @Test
  void eachContextResolvesThroughItsOwnIndex() {
    final var data = marketData();
    final int contexts = GET_SY_STATE_LENGTH_OFFSET + 4;
    data[contexts] = 12;
    data[contexts + 3] = 0;
    data[contexts + 6] = 7;
    final var table = lookupTable();
    final var accounts = ExponentTranchingMarket.read(MARKET, data).refreshAccounts(table);
    assertEquals(AccountMeta.createWrite(table.account(12)), accounts.get(6));
    assertEquals(AccountMeta.createRead(table.account(0)), accounts.get(7));
    assertEquals(AccountMeta.createRead(table.account(7)), accounts.get(8));
    assertEquals(AccountMeta.createWrite(table.account(3)), accounts.get(9));
  }

  @Test
  void theConstantsAreWhatTheProgramDerives() throws NoSuchAlgorithmException {
    final var eventAuthority = PublicKey.findProgramAddress(
        List.of("__event_authority".getBytes(StandardCharsets.US_ASCII)), ExponentTranchingMarket.PROGRAM_ID);
    assertEquals(ExponentTranchingMarket.EVENT_AUTHORITY, eventAuthority.publicKey());

    final byte[] hash = MessageDigest.getInstance("SHA-256")
        .digest("account:ExponentTranchingMarket".getBytes(StandardCharsets.US_ASCII));
    assertArrayEquals(Arrays.copyOf(hash, 8), ExponentTranchingMarket.DISCRIMINATOR);
  }

  @Test
  void anotherAccountIsRefusedByItsDiscriminator() {
    for (int i = 0; i < 8; ++i) {
      final var data = marketData();
      data[i] ^= 1;
      assertThrows(IllegalArgumentException.class, () -> ExponentTranchingMarket.read(MARKET, data), "byte " + i);
    }
  }

  /// Every prefix of a live market up to the end of its `get_sy_state` list is refused,
  /// and every longer one reads the same: the reader takes exactly what Scope's does and
  /// leaves the market's later lists unread.
  @Test
  void aTruncatedMarketIsRefusedUpToTheLastByteTheReaderNeeds() {
    final var data = marketData();
    final var whole = ExponentTranchingMarket.read(MARKET, data);
    for (int length = 0; length < data.length; ++length) {
      final var prefix = Arrays.copyOf(data, length);
      if (length < MARKET_PARSED_END) {
        assertThrows(IllegalArgumentException.class, () -> ExponentTranchingMarket.read(MARKET, prefix), "length " + length);
      } else {
        assertEquals(whole, ExponentTranchingMarket.read(MARKET, prefix), "length " + length);
      }
    }
  }

  /// An empty `get_sy_state` list whose length is the last thing in the account.
  @Test
  void anEmptyListMayEndTheAccount() {
    final var data = Arrays.copyOf(marketData(), GET_SY_STATE_LENGTH_OFFSET + 4);
    Arrays.fill(data, GET_SY_STATE_LENGTH_OFFSET, data.length, (byte) 0);
    final var market = ExponentTranchingMarket.read(MARKET, data);
    assertEquals(List.of(), market.getSyState());
    assertEquals(6, market.refreshAccounts(lookupTable()).size());
  }

  /// A hostile length is refused before anything is allocated for it, whichever list it
  /// is on, including one past `Integer.MAX_VALUE` once read unsigned.
  @Test
  void aListLengthPastTheDataIsRefused() {
    for (final int lengthOffset : new int[]{8 + 1273, 8 + 1273 + 36, GET_SY_STATE_LENGTH_OFFSET}) {
      for (final int length : new int[]{-1, Integer.MAX_VALUE, 1 << 20}) {
        final var data = marketData();
        data[lengthOffset] = (byte) length;
        data[lengthOffset + 1] = (byte) (length >> 8);
        data[lengthOffset + 2] = (byte) (length >> 16);
        data[lengthOffset + 3] = (byte) (length >> 24);
        assertThrows(IllegalArgumentException.class, () -> ExponentTranchingMarket.read(MARKET, data),
            "length " + Integer.toUnsignedString(length) + " at " + lengthOffset);
      }
    }
  }

  /// borsh reads a `bool` from 0 or 1 and refuses anything else, and so does Scope's read of
  /// the market, so a refresh of such a market cannot succeed.
  @Test
  void aFlagThatIsNotABoolIsRefused() {
    for (final int flagOffset : new int[]{GET_SY_STATE_LENGTH_OFFSET + 4 + 1, GET_SY_STATE_LENGTH_OFFSET + 4 + 2}) {
      for (final int value : new int[]{2, 0x7F, 0x80, 0xFF}) {
        final var data = marketData();
        data[flagOffset] = (byte) value;
        assertThrows(IllegalArgumentException.class, () -> ExponentTranchingMarket.read(MARKET, data),
            value + " at byte " + flagOffset);
      }
    }
  }

  /// Scope's read accepts a signer flag and its refresh then refuses the market, as it
  /// cannot sign the CPI.
  @Test
  void aSignerContextReadsButCannotBeRefreshed() {
    final var data = marketData();
    data[GET_SY_STATE_LENGTH_OFFSET + 4 + 3 + 1] = 1;
    final var market = ExponentTranchingMarket.read(MARKET, data);
    assertEquals(new CpiInterfaceContext(1, true, false), market.getSyState().get(1));
    final var table = lookupTable();
    assertThrows(IllegalStateException.class, () -> market.refreshAccounts(table));
  }

  @Test
  void anotherLookupTableIsRefused() {
    final var market = ExponentTranchingMarket.read(MARKET, marketData());
    final var other = AddressLookupTable.read(WIDE_LOOKUP_TABLE, fixture("lookupTable-" + MARKET_LOOKUP_TABLE));
    assertThrows(IllegalArgumentException.class, () -> market.refreshAccounts(other));
  }

  /// The highest index the market uses is 3, so a table of four entries is enough and a
  /// table of three is not.
  @Test
  void anIndexPastTheTableIsRefused() {
    final var market = ExponentTranchingMarket.read(MARKET, marketData());
    final var tableData = fixture("lookupTable-" + MARKET_LOOKUP_TABLE);
    final var four = AddressLookupTable.read(MARKET_LOOKUP_TABLE, Arrays.copyOf(tableData, AddressLookupTable.LOOKUP_TABLE_META_SIZE + 4 * 32));
    assertEquals(10, market.refreshAccounts(four).size());
    final var three = AddressLookupTable.read(MARKET_LOOKUP_TABLE, Arrays.copyOf(tableData, AddressLookupTable.LOOKUP_TABLE_META_SIZE + 3 * 32));
    assertThrows(IllegalArgumentException.class, () -> market.refreshAccounts(three));
  }

  /// `alt_index` is a `u8`, so an index from 128 up is an entry of a table of up to 256,
  /// not a negative one. Both live markets stay below 6, so the table is extended to its
  /// limit with keys that differ from each other.
  @Test
  void anIndexFrom128UpIsATableEntry() {
    final var tableData = fixture("lookupTable-" + MARKET_LOOKUP_TABLE);
    final int meta = AddressLookupTable.LOOKUP_TABLE_META_SIZE;
    final var full = Arrays.copyOf(tableData, meta + 256 * 32);
    for (int i = (tableData.length - meta) / 32; i < 256; ++i) {
      Arrays.fill(full, meta + i * 32, meta + (i + 1) * 32, (byte) i);
    }
    final var table = AddressLookupTable.read(MARKET_LOOKUP_TABLE, full);

    final var data = marketData();
    final int contexts = GET_SY_STATE_LENGTH_OFFSET + 4;
    data[contexts] = (byte) 200;
    data[contexts + 3] = (byte) 255;
    final var market = ExponentTranchingMarket.read(MARKET, data);
    assertEquals(200, market.getSyState().get(0).altIndex());
    assertEquals(255, market.getSyState().get(1).altIndex());
    final var accounts = market.refreshAccounts(table);
    assertEquals(AccountMeta.createWrite(table.account(200)), accounts.get(6));
    assertEquals(AccountMeta.createRead(table.account(255)), accounts.get(7));
    // and the live table of 13 entries has neither
    assertThrows(IllegalArgumentException.class, () -> market.refreshAccounts(lookupTable()));
  }

  @Test
  void aContextIndexIsAU8() {
    assertEquals(0, new CpiInterfaceContext(0, false, false).altIndex());
    assertEquals(255, new CpiInterfaceContext(255, false, false).altIndex());
    assertThrows(IllegalArgumentException.class, () -> new CpiInterfaceContext(-1, false, false));
    assertThrows(IllegalArgumentException.class, () -> new CpiInterfaceContext(256, false, false));
  }

  /// Both live markets hold one key in each role list. The lists are skipped by their own
  /// lengths, so any other lengths, including two that add up to the same, read the same.
  @Test
  void roleListsAreSkippedByTheirOwnLengths() {
    final var expected = ExponentTranchingMarket.read(MARKET, marketData());
    for (final int[] roles : new int[][]{{0, 0}, {0, 2}, {2, 0}, {2, 1}, {3, 0}}) {
      assertEquals(expected, ExponentTranchingMarket.read(MARKET, withRoles(roles[0], roles[1])), Arrays.toString(roles));
    }
  }

  /// The live market with its admin and sentinel lists replaced by lists of the given
  /// lengths, everything after them moved along.
  private static byte[] withRoles(final int admins, final int sentinels) {
    final var data = marketData();
    final int rolesOffset = 8 + 1273;
    final var tail = Arrays.copyOfRange(data, GET_SY_STATE_LENGTH_OFFSET, data.length);
    final var market = new byte[rolesOffset + 2 * 4 + 32 * (admins + sentinels) + tail.length];
    System.arraycopy(data, 0, market, 0, rolesOffset);
    int offset = rolesOffset;
    for (final int keys : new int[]{admins, sentinels}) {
      ByteUtil.putInt32LE(market, offset, keys);
      offset += 4;
      Arrays.fill(market, offset, offset + 32 * keys, (byte) 0x5A);
      offset += 32 * keys;
    }
    System.arraycopy(tail, 0, market, offset, tail.length);
    return market;
  }
}
