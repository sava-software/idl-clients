package software.sava.idl.clients.kamino.scope;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.lookup.AddressLookupTable;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.encoding.ByteUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import java.util.Arrays;
import java.util.List;

import static software.sava.core.accounts.PublicKey.PUBLIC_KEY_LENGTH;

/// What Scope reads from an Exponent tranching market to build and check the
/// `update_market` CPI that refreshes an `ExponentTranching` entry: the market's address
/// lookup table, SY program and return model storage, and the lookup-table indices of the
/// accounts its `get_sy_state` CPI takes. This is Scope's own view of the market,
/// `MarketCpiConfig` in Scope's `exponent-itf` crate, which Scope parses on chain and checks
/// every refresh account against, so the accounts [#refreshAccounts(AddressLookupTable)]
/// builds are the ones the refresh accepts. The tranching program publishes neither its
/// source nor an IDL.
///
/// Scope also reads the tranches' LP supplies from the market, after the CPI, which can
/// change them; a side with no supply does not price. They are not carried here.
///
/// @param getSyState in the order the CPI takes them; `altIndex` indexes the market's
///                   lookup table
public record ExponentTranchingMarket(PublicKey address,
                                      PublicKey addressLookupTable,
                                      PublicKey syProgram,
                                      PublicKey returnModelStorage,
                                      List<CpiInterfaceContext> getSyState) {

  /// The Exponent tranching program, as Scope 0.43.0's `exponent-itf` declares it. It is
  /// compiled into the Scope program, so it does not vary by network.
  public static final PublicKey PROGRAM_ID = PublicKey.fromBase58Encoded("XPTrnchoawiUc9iYJrpfchS8vgr8Y5X2QGBdHPXukty");
  /// The tranching program's event authority, `["__event_authority"]` under [#PROGRAM_ID],
  /// which `update_market` takes as an account. Scope compares the account it is given with
  /// this constant rather than deriving it.
  public static final PublicKey EVENT_AUTHORITY = PublicKey.fromBase58Encoded("3mBi7DRWMdTdDghA1cVLrwDKAgDo7UTDWoeik4GkXCsf");

  /// `sha256("account:ExponentTranchingMarket")[0..8]`.
  static final byte[] DISCRIMINATOR = {0x77, 0x26, 0x78, 0x7a, 0x3c, 0x18, 0x3a, (byte) 0xa0};

  // Offsets from the start of the account, `exponent-itf`'s body offsets plus the
  // discriminator. Everything up to the two role lists is fixed size.
  static final int ADDRESS_LOOKUP_TABLE_OFFSET = 8;
  static final int SY_PROGRAM_OFFSET = 8 + 64;
  static final int RETURN_MODEL_STORAGE_OFFSET = 8 + 224;
  static final int ROLES_OFFSET = 8 + 1273;
  /// `alt_index: u8`, `is_signer: bool`, `is_writable: bool`.
  static final int CPI_INTERFACE_CONTEXT_BYTES = 3;
  /// The accounts a refresh takes for one entry before the `get_sy_state` list: the market
  /// and the five `update_market` accounts that follow it.
  static final int FIXED_REFRESH_ACCOUNTS = 6;

  /// One account of the `get_sy_state` CPI: its index in the market's lookup table and the
  /// flags the CPI passes it with.
  ///
  /// @param altIndex a `u8` on chain, so 0 to 255
  public record CpiInterfaceContext(int altIndex, boolean signer, boolean writable) {

    public CpiInterfaceContext {
      if (altIndex < 0 || altIndex > 0xFF) {
        throw new IllegalArgumentException("A lookup table index is a u8, not " + altIndex + ".");
      }
    }
  }

  public static ExponentTranchingMarket read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data());
  }

  /// Reads the market the way `MarketCpiConfig::from_account_data` does: the three keys in
  /// place, the two role lists skipped by their lengths, and then only the `get_sy_state`
  /// list, the first of the market's CPI account lists. The lists after it are left unread.
  ///
  /// @throws IllegalArgumentException for data Scope's own read refuses — the wrong
  ///                                  discriminator, too few bytes for a length a list
  ///                                  declares, or a flag byte that is neither 0 nor 1,
  ///                                  which borsh rejects as a `bool`
  public static ExponentTranchingMarket read(final PublicKey address, final byte[] data) {
    if (!Arrays.equals(data, 0, Math.min(data.length, DISCRIMINATOR.length), DISCRIMINATOR, 0, DISCRIMINATOR.length)) {
      throw new IllegalArgumentException(address + " is not an Exponent tranching market.");
    }
    int offset = ROLES_OFFSET;
    // admin, then sentinel: each a u32 length and 32 bytes per key
    for (int i = 0; i < 2; ++i) {
      final long numKeys = length(address, data, offset);
      offset += Integer.BYTES;
      offset += checkedSpan(address, data, offset, numKeys, PUBLIC_KEY_LENGTH);
    }
    final long numContexts = length(address, data, offset);
    offset += Integer.BYTES;
    checkedSpan(address, data, offset, numContexts, CPI_INTERFACE_CONTEXT_BYTES);
    final var contexts = new CpiInterfaceContext[(int) numContexts];
    for (int i = 0; i < contexts.length; ++i, offset += CPI_INTERFACE_CONTEXT_BYTES) {
      contexts[i] = new CpiInterfaceContext(
          data[offset] & 0xFF,
          flag(address, data, offset + 1),
          flag(address, data, offset + 2)
      );
    }
    return new ExponentTranchingMarket(
        address,
        PublicKey.readPubKey(data, ADDRESS_LOOKUP_TABLE_OFFSET),
        PublicKey.readPubKey(data, SY_PROGRAM_OFFSET),
        PublicKey.readPubKey(data, RETURN_MODEL_STORAGE_OFFSET),
        List.of(contexts)
    );
  }

  private static long length(final PublicKey address, final byte[] data, final int offset) {
    if (offset > data.length - Integer.BYTES) {
      throw new IllegalArgumentException("Exponent tranching market " + address
          + " ends at byte " + data.length + ", before the list length at byte " + offset + ".");
    }
    return Integer.toUnsignedLong(ByteUtil.getInt32LE(data, offset));
  }

  /// The bytes `count` elements of `elementBytes` take from `offset`, checked against what
  /// the data holds before anything is multiplied or allocated.
  private static int checkedSpan(final PublicKey address,
                                 final byte[] data,
                                 final int offset,
                                 final long count,
                                 final int elementBytes) {
    if (count > (data.length - offset) / elementBytes) {
      throw new IllegalArgumentException("Exponent tranching market " + address + " declares " + count
          + " elements of " + elementBytes + " bytes at byte " + offset + ", past its end at " + data.length + ".");
    }
    return (int) count * elementBytes;
  }

  private static boolean flag(final PublicKey address, final byte[] data, final int offset) {
    return switch (data[offset]) {
      case 0 -> false;
      case 1 -> true;
      default -> throw new IllegalArgumentException("Exponent tranching market " + address
          + " holds " + Byte.toUnsignedInt(data[offset]) + " at byte " + offset + ", where a bool is 0 or 1.");
    };
  }

  /// The accounts `refresh_price_list` takes for one `ExponentTranching` entry on this
  /// market, in the order Scope's `exponent_tranching::get_price` consumes them: the market,
  /// writable, as the entry's own account; its return model storage, writable; its lookup
  /// table, SY program, the [#EVENT_AUTHORITY] and the [#PROGRAM_ID], read-only; then each
  /// `get_sy_state` account resolved through `lookupTable`, writable when the market flags
  /// it. Each flag is the one Scope's CPI passes the account with, `exponent-itf`'s
  /// `UpdateMarket` marking the market and the return model storage `mut`, and the runtime
  /// refuses a CPI that passes an account writable when the transaction passed it
  /// read-only, aborting the whole transaction.
  ///
  /// Pass the result to
  /// [ScopeProgramClient#refreshPriceListExtraAccounts(software.sava.idl.clients.kamino.scope.gen.types.OracleMappings, int\[\], java.util.Map)]
  /// keyed by [#address()], or append it in place of the entry's single account. Scope's
  /// authors require an entry whose refresh CPIs into another program, as this one does, to
  /// be refreshed in its own single-entry call, because a failed CPI aborts the whole
  /// transaction instead of skipping the entry. Scope budgets 175,000 compute units for
  /// refreshing one (`OracleType::get_update_cu_budget`), most of a transaction's 200,000
  /// default.
  ///
  /// @param lookupTable the market's own table, [#addressLookupTable()]
  /// @throws IllegalArgumentException when `lookupTable` is another table, or an index is
  ///                                  past its last entry
  /// @throws IllegalStateException    when the market flags a `get_sy_state` account as a
  ///                                  signer: Scope cannot sign the CPI and refuses the
  ///                                  market, so no refresh of it can succeed
  public List<AccountMeta> refreshAccounts(final AddressLookupTable lookupTable) {
    if (!addressLookupTable.equals(lookupTable.address())) {
      throw new IllegalArgumentException("Exponent tranching market " + address + " uses lookup table "
          + addressLookupTable + ", not " + lookupTable.address() + ".");
    }
    final var metas = new AccountMeta[FIXED_REFRESH_ACCOUNTS + getSyState.size()];
    metas[0] = AccountMeta.createWrite(address);
    metas[1] = AccountMeta.createWrite(returnModelStorage);
    metas[2] = AccountMeta.createRead(addressLookupTable);
    metas[3] = AccountMeta.createRead(syProgram);
    metas[4] = AccountMeta.createRead(EVENT_AUTHORITY);
    metas[5] = AccountMeta.createRead(PROGRAM_ID);
    int i = FIXED_REFRESH_ACCOUNTS;
    for (final var context : getSyState) {
      final int altIndex = context.altIndex();
      if (context.signer()) {
        throw new IllegalStateException("Exponent tranching market " + address + " flags lookup table index "
            + altIndex + " as a signer of its get_sy_state CPI, which Scope cannot sign.");
      }
      if (altIndex >= lookupTable.numAccounts()) {
        throw new IllegalArgumentException("Lookup table " + addressLookupTable + " holds "
            + lookupTable.numAccounts() + " addresses, so it has no index " + altIndex + ".");
      }
      final var account = lookupTable.account(altIndex);
      metas[i++] = context.writable() ? AccountMeta.createWrite(account) : AccountMeta.createRead(account);
    }
    return List.of(metas);
  }
}
