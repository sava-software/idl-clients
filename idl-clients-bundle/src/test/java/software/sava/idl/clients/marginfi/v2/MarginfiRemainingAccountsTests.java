package software.sava.idl.clients.marginfi.v2;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.marginfi.v2.gen.types.Bank;
import software.sava.idl.clients.marginfi.v2.gen.types.BankConfig;
import software.sava.idl.clients.marginfi.v2.gen.types.OracleSetup;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.marginfi.v2.MarginfiRemainingAccounts.*;

/// Covers the marginfi `remaining_accounts` payload.
///
/// This is the shape most likely to be assembled wrongly by hand, because the
/// obvious mental model — a flat `<bank, oracle>` sequence — is right only for
/// ordinary banks. A staked bank contributes five accounts and an integration
/// bank three, and the token-moving instructions additionally want the bank mint
/// at the front for Token-2022. Both mistakes fail on chain
/// (`WrongNumberOfOracleAccounts`, `T22MintRequired`) with nothing pointing at
/// the caller's list.
final class MarginfiRemainingAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final List<AccountMeta> metas) {
    return metas.stream().map(AccountMeta::publicKey).toList();
  }

  /// The per-bank count table, mirroring `get_remaining_accounts_per_bank`.
  @Test
  void accountsPerBankFollowsTheOracleSetupAndAssetTag() {
    // a fixed-price bank carries no oracle at all
    assertEquals(1, accountsPerBank(OracleSetup.Fixed, ASSET_TAG_DEFAULT));

    // fixed + venue: the venue's own state stands in for the oracle
    assertEquals(2, accountsPerBank(OracleSetup.FixedKamino, ASSET_TAG_DEFAULT));
    assertEquals(2, accountsPerBank(OracleSetup.FixedDrift, ASSET_TAG_DEFAULT));
    assertEquals(2, accountsPerBank(OracleSetup.FixedJuplend, ASSET_TAG_DEFAULT));

    // ordinary banks are the familiar pair
    assertEquals(2, accountsPerBank(OracleSetup.PythLegacy, ASSET_TAG_DEFAULT));
    assertEquals(2, accountsPerBank(OracleSetup.SwitchboardPull, ASSET_TAG_SOL));

    // integration banks add their reserve
    for (final int tag : new int[]{ASSET_TAG_KAMINO, ASSET_TAG_DRIFT, ASSET_TAG_SOLEND, ASSET_TAG_JUPLEND}) {
      assertEquals(3, accountsPerBank(OracleSetup.PythPushOracle, tag), "asset tag " + tag);
    }

    // staked banks are the outlier
    assertEquals(5, accountsPerBank(OracleSetup.StakedWithPythPush, ASSET_TAG_STAKED));

    // 0.1.11's wrapped-asset setups each carry a rate source of their own: a
    // Marinade State for mSOL, an SPL stake pool for an LST, an Exponent vault
    // for a PT. The Kamino/JupLend flavours carry the venue reserve as well.
    assertEquals(2, accountsPerBank(OracleSetup.PTFixed, ASSET_TAG_DEFAULT));
    assertEquals(3, accountsPerBank(OracleSetup.PythMSOL, ASSET_TAG_DEFAULT));
    assertEquals(3, accountsPerBank(OracleSetup.PythLST, ASSET_TAG_DEFAULT));
    assertEquals(3, accountsPerBank(OracleSetup.PTPyth, ASSET_TAG_DEFAULT));
    assertEquals(4, accountsPerBank(OracleSetup.KaminoMSOL, ASSET_TAG_KAMINO));
    assertEquals(4, accountsPerBank(OracleSetup.JuplendMSOL, ASSET_TAG_JUPLEND));
    assertEquals(4, accountsPerBank(OracleSetup.KaminoLST, ASSET_TAG_KAMINO));
    assertEquals(4, accountsPerBank(OracleSetup.JuplendLST, ASSET_TAG_JUPLEND));

    // Scope is the one setup 0.1.11 added that does *not* override: it prices
    // through the ordinary pair, the oracle being the feed's `OraclePrices`
    // account and `BankConfig.scopeEntryIndex` picking the entry inside it.
    assertEquals(2, accountsPerBank(OracleSetup.Scope, ASSET_TAG_DEFAULT));
    assertEquals(2, accountsPerBank(OracleSetup.Scope, ASSET_TAG_SOL));

    // the setup wins over the asset tag
    assertEquals(1, accountsPerBank(OracleSetup.Fixed, ASSET_TAG_STAKED));
    assertEquals(2, accountsPerBank(OracleSetup.FixedKamino, ASSET_TAG_STAKED));
    assertEquals(3, accountsPerBank(OracleSetup.PythMSOL, ASSET_TAG_STAKED));
    assertEquals(4, accountsPerBank(OracleSetup.KaminoLST, ASSET_TAG_STAKED));

    // the fetched-Bank overload reads the setup and tag out of the config
    assertEquals(
        accountsPerBank(OracleSetup.StakedWithPythPush, ASSET_TAG_STAKED),
        MarginfiRemainingAccounts.accountsPerBank(
            syntheticBank(key(0x11), OracleSetup.StakedWithPythPush, ASSET_TAG_STAKED)));
    assertEquals(
        accountsPerBank(OracleSetup.Fixed, ASSET_TAG_DEFAULT),
        MarginfiRemainingAccounts.accountsPerBank(
            syntheticBank(key(0x11), OracleSetup.Fixed, ASSET_TAG_DEFAULT)));
  }

  /// Every setup the program can put on a bank is named here, so a regeneration
  /// that appends one fails this test instead of silently pricing the new bank
  /// as an ordinary pair. That silence is not hypothetical: 0.1.11 appended nine
  /// constants, and eight of them need three or four accounts — the fall-through
  /// answered two for all nine.
  ///
  /// The expected counts are `get_remaining_accounts_per_bank` transcribed by
  /// hand, at `ASSET_TAG_DEFAULT` so the setup is the only variable. Setups that
  /// legitimately fall through to the asset tag are listed rather than defaulted,
  /// so a new one cannot join them by omission.
  @Test
  void everyOracleSetupIsClassified() {
    final var expected = new EnumMap<OracleSetup, Integer>(OracleSetup.class);
    // no override: priced as <bank, oracle> at the default asset tag
    for (final var fallsThrough : List.of(
        OracleSetup.None,
        OracleSetup.PythLegacy,
        OracleSetup.SwitchboardV2,
        OracleSetup.PythPushOracle,
        OracleSetup.SwitchboardPull,
        OracleSetup.StakedWithPythPush,
        OracleSetup.KaminoPythPush,
        OracleSetup.KaminoSwitchboardPull,
        OracleSetup.DriftPythPull,
        OracleSetup.DriftSwitchboardPull,
        OracleSetup.SolendPythPull,
        OracleSetup.SolendSwitchboardPull,
        OracleSetup.JuplendPythPull,
        OracleSetup.JuplendSwitchboardPull,
        OracleSetup.Scope)) {
      expected.put(fallsThrough, 2);
    }
    expected.put(OracleSetup.Fixed, 1);
    expected.put(OracleSetup.FixedKamino, 2);
    expected.put(OracleSetup.FixedDrift, 2);
    expected.put(OracleSetup.FixedJuplend, 2);
    expected.put(OracleSetup.PTFixed, 2);
    expected.put(OracleSetup.PythMSOL, 3);
    expected.put(OracleSetup.PythLST, 3);
    expected.put(OracleSetup.PTPyth, 3);
    expected.put(OracleSetup.KaminoMSOL, 4);
    expected.put(OracleSetup.JuplendMSOL, 4);
    expected.put(OracleSetup.KaminoLST, 4);
    expected.put(OracleSetup.JuplendLST, 4);

    for (final var setup : OracleSetup.values()) {
      final var count = expected.get(setup);
      assertNotNull(count, "unclassified oracle setup " + setup
          + " — check get_remaining_accounts_per_bank and add it to the table");
      assertEquals(count.intValue(), accountsPerBank(setup, ASSET_TAG_DEFAULT), setup.name());
    }
    assertEquals(expected.size(), OracleSetup.values().length, "the table names a setup that no longer exists");

    // The count is looked up from a value decoded out of one wire byte, so the
    // ordinals are as much a part of the contract as the counts: Rust numbers
    // this enum `#[repr(u8)]`, Scope at 18 through PTFixed at 26.
    assertEquals(OracleSetup.Scope, OracleSetup.read(new byte[]{18}, 0));
    assertEquals(OracleSetup.PTFixed, OracleSetup.read(new byte[]{26}, 0));
  }

  /// Groups are laid out in the order they are added, bank first.
  @Test
  void groupsAreEmittedInOrderWithTheBankLeading() {
    final var bankA = key(0x11);
    final var oracleA = key(0x12);
    final var bankB = key(0x13);
    final var oracleB = key(0x14);
    final var reserveB = key(0x15);

    final var accounts = MarginfiRemainingAccounts.builder()
        .bankWithOracle(bankA, oracleA)
        .bank(bankB, OracleSetup.PythPushOracle, ASSET_TAG_KAMINO, oracleB, reserveB)
        .build();

    assertEquals(List.of(bankA, oracleA, bankB, oracleB, reserveB), keys(accounts));
    assertTrue(accounts.stream().noneMatch(AccountMeta::signer));
    assertTrue(accounts.stream().noneMatch(AccountMeta::write));
  }

  /// The Token-2022 mint goes at the *front*, ahead of every risk-engine group —
  /// the program splits it off before the risk engine ever sees the list.
  @Test
  void theTokenTwentyTwoMintLeadsTheWholePayload() {
    final var mint = key(0x21);
    final var bank = key(0x22);
    final var oracle = key(0x23);

    final var withMint = MarginfiRemainingAccounts.builder()
        .bankMint(mint)
        .bankWithOracle(bank, oracle)
        .build();
    assertEquals(List.of(mint, bank, oracle), keys(withMint));

    // an SPL Token bank omits it entirely rather than passing a placeholder
    final var withoutMint = MarginfiRemainingAccounts.builder()
        .bankWithOracle(bank, oracle)
        .build();
    assertEquals(List.of(bank, oracle), keys(withoutMint));
    assertEquals(withMint.size() - 1, withoutMint.size());
  }

  /// Transfer-hook accounts trail everything, since the program forwards the
  /// whole slice to the transfer CPI after the risk engine has read its groups.
  @Test
  void transferHookAccountsComeLast() {
    final var mint = key(0x21);
    final var bank = key(0x22);
    final var oracle = key(0x23);
    final var hookProgram = key(0x24);
    final var hookExtra = key(0x25);

    final var accounts = MarginfiRemainingAccounts.builder()
        .bankMint(mint)
        .bankWithOracle(bank, oracle)
        .transferHookAccounts(List.of(AccountMeta.createRead(hookProgram), AccountMeta.createRead(hookExtra)))
        .build();

    assertEquals(List.of(mint, bank, oracle, hookProgram, hookExtra), keys(accounts));
  }

  /// A miscounted group is rejected at build time rather than on chain, and the
  /// message names the bank and the expected count.
  @Test
  void aMiscountedGroupIsRejectedUpFront() {
    final var bank = key(0x31);
    final var oracle = key(0x32);

    // a staked bank needs five accounts, not two
    final var tooFew = assertThrows(IllegalArgumentException.class, () ->
        MarginfiRemainingAccounts.builder()
            .bank(bank, OracleSetup.StakedWithPythPush, ASSET_TAG_STAKED, oracle));
    assertTrue(tooFew.getMessage().contains("needs 5"), tooFew.getMessage());
    assertTrue(tooFew.getMessage().contains("got 2"), tooFew.getMessage());

    // and an ordinary bank does not take a reserve
    final var tooMany = assertThrows(IllegalArgumentException.class, () ->
        MarginfiRemainingAccounts.builder()
            .bank(bank, OracleSetup.PythLegacy, ASSET_TAG_DEFAULT, oracle, key(0x33)));
    assertTrue(tooMany.getMessage().contains("needs 2"), tooMany.getMessage());

    // a fixed-price bank takes none
    assertDoesNotThrow(() -> MarginfiRemainingAccounts.builder()
        .bank(bank, OracleSetup.Fixed, ASSET_TAG_DEFAULT));
    assertThrows(IllegalArgumentException.class, () ->
        MarginfiRemainingAccounts.builder()
            .bank(bank, OracleSetup.Fixed, ASSET_TAG_DEFAULT, oracle));

    // an empty payload is legitimate: an account with no active balances
    assertTrue(MarginfiRemainingAccounts.builder().build().isEmpty());
  }

  /// A `Bank` fetched from chain carries its own address, oracle setup and
  /// asset tag; the overload must feed exactly those three into the manual
  /// path — a dropped delegate silently contributes no group at all.
  @Test
  void aFetchedBankContributesItsOwnAddressSetupAndTag() {
    final var bankKey = key(0x41);
    final var oracle = key(0x42);
    final var reserve = key(0x43);
    final var bank = syntheticBank(bankKey, OracleSetup.PythPushOracle, ASSET_TAG_KAMINO);

    final var viaBank = MarginfiRemainingAccounts.builder()
        .bank(bank, oracle, reserve)
        .build();
    final var direct = MarginfiRemainingAccounts.builder()
        .bank(bankKey, OracleSetup.PythPushOracle, ASSET_TAG_KAMINO, oracle, reserve)
        .build();

    assertEquals(keys(direct), keys(viaBank));
    assertEquals(List.of(bankKey, oracle, reserve), keys(viaBank));
  }

  /// Only the three fields the overload reads are populated; a generated
  /// record accepts null for everything else.
  private static Bank syntheticBank(final PublicKey address,
                                    final OracleSetup oracleSetup,
                                    final int assetTag) {
    final var config = new BankConfig(
        null, null, null, null, 0L, null, null, oracleSetup, null, 0,
        0, null, 0L, null, assetTag, 0, null, 0L, 0L, 0, 0, 0L,
        null, null, null, 0, 0, 0);
    return new Bank(
        address, null, null, 0, null, null, null, null, null, 0,
        0, null, 0, 0, null, null, null, 0, 0, null,
        null, null, null, 0L, config, 0L, 0L, null, null, null,
        null, null, null, 0, 0, 0L, 0L, null, null, null,
        null, null, null, 0L, 0L, 0L, 0, 0, 0, null,
        0L, 0L, null, null, 0L, 0L, null);
  }
}
