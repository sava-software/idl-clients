package software.sava.idl.clients.marginfi.v2;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.marginfi.v2.gen.types.Bank;
import software.sava.idl.clients.marginfi.v2.gen.types.BankConfig;
import software.sava.idl.clients.marginfi.v2.gen.types.OracleSetup;

import java.util.Arrays;
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

    // the fixed setups win over the asset tag
    assertEquals(1, accountsPerBank(OracleSetup.Fixed, ASSET_TAG_STAKED));
    assertEquals(2, accountsPerBank(OracleSetup.FixedKamino, ASSET_TAG_STAKED));
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
        null, null, null, null, 0L, null, null, oracleSetup, null, null,
        0L, null, assetTag, 0, null, 0L, 0, null, 0L, null, null);
    return new Bank(
        address, null, null, 0, null, null, null, null, null, 0,
        0, null, 0, 0, null, null, null, 0, 0, null,
        null, null, null, 0L, config, 0L, 0L, null, null, null,
        null, null, null, 0, 0, null, null, null, null, null,
        null, 0L, null);
  }
}
