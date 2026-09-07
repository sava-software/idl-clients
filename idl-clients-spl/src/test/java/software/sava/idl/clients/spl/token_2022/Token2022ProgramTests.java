package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.spl.token_2022.gen.Token2022Program;
import software.sava.idl.clients.spl.token_2022.gen.types.AuthorityType;
import software.sava.idl.clients.spl.token_2022.gen.types.DecryptableBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.EncryptedBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.ExtensionType;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalLong;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class Token2022ProgramTests {

  @Test
  void createMintWithTransferHook() {
    // devnet 3b7rYDCdxymqBXR3FLFgUtUoQyoYGg2ZF2bbKviuSQToSWyZeJmfb2wpjpTsZRf8FCWVMtuNetTAz2EAvmRSZLUi

    final var mintAccount = PublicKey.fromBase58Encoded("88WLQK58mbqNjaUBxYjEvhvdsWGQde4s1EqyagvEng2f");
    final var mintAuthority = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");
    final var programAccount = PublicKey.fromBase58Encoded("2o6gvxp17hkML8Rz3cvqzbSTFStES287fYeDPeHhF7Vj");

    final byte[] expectedData = Base64.getDecoder().decode(
        "JACxI9EyhTENtAmHTkc1jeGOXz5ac2rbQKBtpzoJOzX1ZBqq1gqqi0gmr088z6opaqke8W8zo8Lcb1bxDLWCwhBK"
    );

    final var solAccounts = SolanaAccounts.MAIN_NET;
    final var initializeTransferHookIx = Token2022Program.initializeTransferHook(
        solAccounts.invokedToken2022Program(),
        mintAccount,
        mintAuthority,
        programAccount
    );

    assertEquals(solAccounts.invokedToken2022Program(), initializeTransferHookIx.programId());

    final var accounts = initializeTransferHookIx.accounts();
    assertEquals(1, accounts.size());
    assertEquals(AccountMeta.createWrite(mintAccount), accounts.getFirst());

    assertArrayEquals(expectedData, initializeTransferHookIx.data());
  }

  @Test
  void createMintWithMetadataPointer() {
    // devnet 3b7rYDCdxymqBXR3FLFgUtUoQyoYGg2ZF2bbKviuSQToSWyZeJmfb2wpjpTsZRf8FCWVMtuNetTAz2EAvmRSZLUi

    final var mint = PublicKey.fromBase58Encoded("88WLQK58mbqNjaUBxYjEvhvdsWGQde4s1EqyagvEng2f");
    final var authority = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");

    final byte[] expectedData = Base64.getDecoder().decode(
        "JwCxI9EyhTENtAmHTkc1jeGOXz5ac2rbQKBtpzoJOzX1ZGnuCvRYCzpsDUH5g837ywyG3bHaM3+sD1Ohmf1+QlgU"
    );

    final var solAccounts = SolanaAccounts.MAIN_NET;
    final var initMetadataPointerIx = Token2022Program.initializeMetadataPointer(
        solAccounts.invokedToken2022Program(),
        mint,
        authority,
        mint
    );

    assertEquals(solAccounts.invokedToken2022Program(), initMetadataPointerIx.programId());

    final var accounts = initMetadataPointerIx.accounts();
    assertEquals(1, accounts.size());
    assertEquals(AccountMeta.createWrite(mint), accounts.getFirst());

    assertArrayEquals(expectedData, initMetadataPointerIx.data());
  }

  @Test
  void createMintWithInitializingMetadata() {
    // devnet 3b7rYDCdxymqBXR3FLFgUtUoQyoYGg2ZF2bbKviuSQToSWyZeJmfb2wpjpTsZRf8FCWVMtuNetTAz2EAvmRSZLUi

    final var name = "SimpleTestCoin";
    final var symbol = "STC";
    final var uri = "https://example.com/metadata.json";
    final var mintAccount = PublicKey.fromBase58Encoded("88WLQK58mbqNjaUBxYjEvhvdsWGQde4s1EqyagvEng2f");
    final var metadataAccount = PublicKey.fromBase58Encoded("88WLQK58mbqNjaUBxYjEvhvdsWGQde4s1EqyagvEng2f");
    final var authority = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");
    final var updateAuthority = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");

    final byte[] expectedData = Base64.getDecoder().decode(
        "0uEeoli4TY0OAAAAU2ltcGxlVGVzdENvaW4DAAAAU1RDIQAAAGh0dHBzOi8vZXhhbXBsZS5jb20vbWV0YWRhdGEuanNvbg=="
    );

    final var solAccounts = SolanaAccounts.MAIN_NET;

    final var initializeTokenMetadataIx = Token2022Program.initializeTokenMetadata(
        solAccounts.invokedToken2022Program(),
        metadataAccount,
        updateAuthority,
        mintAccount,
        authority,
        name,
        symbol,
        uri
    );

    assertEquals(solAccounts.invokedToken2022Program(), initializeTokenMetadataIx.programId());

    final var accounts = initializeTokenMetadataIx.accounts();
    assertEquals(4, accounts.size());
    assertEquals(AccountMeta.createWrite(metadataAccount), accounts.getFirst());
    assertEquals(AccountMeta.createRead(updateAuthority), accounts.get(1));
    assertEquals(AccountMeta.createRead(mintAccount), accounts.get(2));
    assertEquals(AccountMeta.createReadOnlySigner(authority), accounts.getLast());

    assertArrayEquals(expectedData, initializeTokenMetadataIx.data());
  }

  @Test
  void updateTransferHookAccount() {
    // devnet THZ3HTPAQZaEj6ggHSaLSxSS5CeGYp88VDa6NyXxY7pV9khHk1xJk1yHqP4jWByHjBUz34UuWLPffWQfeCzjNyi

    final var mintAccount = PublicKey.fromBase58Encoded("HCRDkSQ6vM9QxDkJMGNUmVKjWqPYudkEsZRDwoJvyzQE");
    final var mintAuthority = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");
    final var newTransferHookProgramId = PublicKey.fromBase58Encoded("7cjXTZvHYGuFarmmsYqjXsyYZY5TMyeNmvidxPJfvQ1Q");

    final byte[] expectedData = Base64.getDecoder().decode("JAFiTYiACXf8S3S3AvEVNeOMHAEhHcY+xbSY6XcSCFeVmw==");

    final var solAccounts = SolanaAccounts.MAIN_NET;
    final var updateTransferHookIx = Token2022Program.updateTransferHook(
        solAccounts.invokedToken2022Program(),
        mintAccount,
        mintAuthority,
        newTransferHookProgramId
    );

    assertEquals(solAccounts.invokedToken2022Program(), updateTransferHookIx.programId());

    final var accounts = updateTransferHookIx.accounts();
    assertEquals(2, accounts.size());
    assertEquals(AccountMeta.createWrite(mintAccount), accounts.getFirst());
    assertEquals(AccountMeta.createReadOnlySigner(mintAuthority), accounts.getLast());

    assertArrayEquals(expectedData, updateTransferHookIx.data());
  }

  @Test
  void updateMintMetadataAccount() {
    // devnet 3iKA2XCusAq2uCxuGyWhw8oBkdYQMQMq87t5sJTXJpCcD169NDzDjBE3fcuTv6Dg8QpjC4QNmwxZXFhSB8DLZkj2

    final var mintAccount = PublicKey.fromBase58Encoded("HCRDkSQ6vM9QxDkJMGNUmVKjWqPYudkEsZRDwoJvyzQE");
    final var mintAuthority = PublicKey.fromBase58Encoded("CvUqgjP892h66aYPC9E8gKTXnTebY8qaU5ehGrgEQSwV");
    final var newMetadataAddress = PublicKey.fromBase58Encoded("AsFagyk29GvS8dtibZ6vjtbfwjnMzn9xHcEzoAnRusCB");

    final byte[] expectedData = Base64.getDecoder().decode("JwGSmLWb6FaiJaQHWUI5v71XJ6PVnzJ4PGbU7A2sL30skA==");

    final var solAccounts = SolanaAccounts.MAIN_NET;
    final var updateMetadataPointerIx = Token2022Program.updateMetadataPointer(
        solAccounts.invokedToken2022Program(),
        mintAccount,
        mintAuthority,
        newMetadataAddress
    );

    assertEquals(solAccounts.invokedToken2022Program(), updateMetadataPointerIx.programId());

    final var accounts = updateMetadataPointerIx.accounts();
    assertEquals(2, accounts.size());
    assertEquals(AccountMeta.createWrite(mintAccount), accounts.getFirst());
    assertEquals(AccountMeta.createReadOnlySigner(mintAuthority), accounts.getLast());

    assertArrayEquals(expectedData, updateMetadataPointerIx.data());
  }

  // ---------------------------------------------------------------------------------------------
  // Reference encodings.
  //
  // Every expected byte string below is transcribed from an implementation this repository did not
  // write. Two oracles are used, named per test:
  //
  //  * the SPL Token-2022 program's own Rust packing tests, which assert literal byte vectors —
  //    `interface/src/instruction.rs` (`mod test`) for the base instructions and
  //    `interface/src/extension/transfer_fee/instruction.rs` (`test_instruction_packing`) for the
  //    transfer-fee extension. Account lists come from the instruction constructors in the same
  //    files.
  //  * the upstream JavaScript client `@solana-program/token-2022` 0.16.1, for the confidential
  //    families, whose sub-instructions the Rust interface crate does not pin with literal vectors.
  //    Its generated builders under `clients/js/src/generated/instructions/` were executed with the
  //    fixed inputs each test restates, and both the data bytes and the account roles recorded.
  // ---------------------------------------------------------------------------------------------

  private static final AccountMeta INVOKED_TOKEN_2022 = SolanaAccounts.MAIN_NET.invokedToken2022Program();

  /// `sysvar::rent::id()`, spelled out rather than read back from `SolanaAccounts` so the account
  /// assertions compare against the program's own constant.
  private static final PublicKey RENT_SYSVAR = PublicKey.fromBase58Encoded("SysvarRent111111111111111111111111111111111");
  private static final PublicKey SYSTEM_PROGRAM = PublicKey.fromBase58Encoded("11111111111111111111111111111111");

  /// `crate::native_mint::id()` from token-2022's `interface/src/native_mint.rs`.
  private static final PublicKey NATIVE_MINT = PublicKey.fromBase58Encoded("9pan9bMn5HatX4EJdBwg9VgCa7Uz5HL8N1m5D3NdXejP");

  private static final PublicKey MINT = filledKey(0x21);
  private static final PublicKey TOKEN_ACCOUNT = filledKey(0x22);
  private static final PublicKey DESTINATION = filledKey(0x23);
  private static final PublicKey OWNER = filledKey(0x24);
  private static final PublicKey DELEGATE = filledKey(0x25);
  private static final PublicKey PAYER = filledKey(0x26);
  private static final PublicKey MULTISIG = filledKey(0x27);
  private static final PublicKey FEE_RECEIVER = filledKey(0x28);
  private static final List<PublicKey> MULTISIG_SIGNERS = List.of(filledKey(0x31), filledKey(0x32), filledKey(0x33));

  private static PublicKey filledKey(final int fill) {
    final byte[] key = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(key, (byte) fill);
    return PublicKey.createPubKey(key);
  }

  private static Vector vector() {
    return new Vector();
  }

  /// Mirrors the `expect.extend_from_slice(..)` style of the Rust packing tests so a vector reads
  /// the same way here as it does there.
  private static final class Vector {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    Vector u8(final int value) {
      out.write(value & 0xFF);
      return this;
    }

    Vector bool(final boolean value) {
      return u8(value ? 1 : 0);
    }

    Vector u16LE(final int value) {
      return u8(value).u8(value >> 8);
    }

    Vector u64LE(final long value) {
      for (int shift = 0; shift < Long.SIZE; shift += Byte.SIZE) {
        u8((int) (value >>> shift));
      }
      return this;
    }

    Vector repeat(final int value, final int length) {
      for (int i = 0; i < length; ++i) {
        u8(value);
      }
      return this;
    }

    Vector key(final PublicKey key) {
      final byte[] raw = new byte[PublicKey.PUBLIC_KEY_LENGTH];
      key.write(raw, 0);
      return raw(raw);
    }

    Vector ascii(final String value) {
      return raw(value.getBytes(US_ASCII));
    }

    Vector raw(final byte[] value) {
      out.writeBytes(value);
      return this;
    }

    byte[] done() {
      return out.toByteArray();
    }
  }

  /// A parsed instruction must re-serialize to the bytes it was read from, at the length it
  /// reports, so `l()` and `write` stay in step with `read`.
  private static void assertReserializes(final byte[] expected, final SerDe parsed) {
    assertNotNull(parsed);
    assertEquals(expected.length, parsed.l());
    final byte[] reserialized = new byte[parsed.l()];
    assertEquals(expected.length, parsed.write(reserialized, 0));
    assertArrayEquals(expected, reserialized);
  }

  // --- base instructions: interface/src/instruction.rs -------------------------------------------

  /// Oracle: `test_initialize_mint_packing` and `initialize_mint`.
  @Test
  void initializeMintMatchesUpstreamPacking() {
    final var solanaAccounts = SolanaAccounts.MAIN_NET;

    final var withoutFreezeAuthority = vector().u8(0).u8(2).repeat(1, 32).u8(0).done();
    final var noFreezeIx = Token2022Program.initializeMint(
        INVOKED_TOKEN_2022, solanaAccounts, MINT, 2, filledKey(1), null
    );
    assertArrayEquals(withoutFreezeAuthority, noFreezeIx.data());
    assertEquals(List.of(
        AccountMeta.createWrite(MINT),
        AccountMeta.createRead(RENT_SYSVAR)
    ), noFreezeIx.accounts());

    final var withFreezeAuthority = vector().u8(0).u8(2).repeat(2, 32).u8(1).repeat(3, 32).done();
    final var freezeIx = Token2022Program.initializeMint(
        INVOKED_TOKEN_2022, solanaAccounts, MINT, 2, filledKey(2), filledKey(3)
    );
    assertArrayEquals(withFreezeAuthority, freezeIx.data());
  }

  /// Oracle: `test_initialize_account_packing` and `initialize_account`.
  @Test
  void initializeAccountMatchesUpstreamPacking() {
    final var expected = vector().u8(1).done();

    final var ix = Token2022Program.initializeAccount(
        INVOKED_TOKEN_2022, SolanaAccounts.MAIN_NET, TOKEN_ACCOUNT, MINT, OWNER
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createRead(OWNER),
        AccountMeta.createRead(RENT_SYSVAR)
    ), ix.accounts());
  }

  /// Oracle: `test_initialize_multisig_packing` and `initialize_multisig`, which appends the M
  /// signer accounts as read-only **non**-signers. The IDL models them as remaining accounts, so
  /// the generated key helper stops at the rent sysvar and the caller appends them.
  @Test
  void initializeMultisigMatchesUpstreamPacking() {
    final var expected = vector().u8(2).u8(1).done();
    final var solanaAccounts = SolanaAccounts.MAIN_NET;

    final var declaredKeys = Token2022Program.initializeMultisigKeys(solanaAccounts, MULTISIG);
    assertEquals(List.of(
        AccountMeta.createWrite(MULTISIG),
        AccountMeta.createRead(RENT_SYSVAR)
    ), declaredKeys);

    final var keys = new ArrayList<>(declaredKeys);
    MULTISIG_SIGNERS.forEach(signer -> keys.add(AccountMeta.createRead(signer)));

    final var ix = Token2022Program.initializeMultisig(INVOKED_TOKEN_2022, keys, 1);

    assertArrayEquals(expected, ix.data());
    assertEquals(5, ix.accounts().size());
    assertEquals(AccountMeta.createRead(MULTISIG_SIGNERS.getFirst()), ix.accounts().get(2));
    assertEquals(AccountMeta.createRead(MULTISIG_SIGNERS.getLast()), ix.accounts().getLast());
  }

  /// Oracle: `test_transfer_packing` and `transfer`.
  @Test
  void transferMatchesUpstreamPacking() {
    final var expected = vector().u8(3).u64LE(1).done();

    final var ix = Token2022Program.transfer(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, DESTINATION, OWNER, 1L);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createWrite(DESTINATION),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `transfer` in `interface/src/instruction.rs`. Its authority meta is
  /// `AccountMeta::new_readonly(owner, signer_pubkeys.is_empty())`, so a multisig owner is a
  /// read-only **non**-signer followed by the M signers. The generated key helper always marks the
  /// owner a signer and appends nothing, so that list is the caller's to build.
  @Test
  void aMultisigOwnerAccountListIsTheCallersToBuild() {
    final var expected = vector().u8(3).u64LE(1).done();

    assertEquals(
        AccountMeta.createReadOnlySigner(MULTISIG),
        Token2022Program.transferKeys(TOKEN_ACCOUNT, DESTINATION, MULTISIG).getLast()
    );

    final var keys = new ArrayList<AccountMeta>();
    keys.add(AccountMeta.createWrite(TOKEN_ACCOUNT));
    keys.add(AccountMeta.createWrite(DESTINATION));
    keys.add(AccountMeta.createRead(MULTISIG));
    MULTISIG_SIGNERS.forEach(signer -> keys.add(AccountMeta.createReadOnlySigner(signer)));

    final var ix = Token2022Program.transfer(INVOKED_TOKEN_2022, keys, 1L);

    assertArrayEquals(expected, ix.data());
    assertEquals(keys, ix.accounts());
  }

  /// Oracle: `test_approve_packing` and `approve`.
  @Test
  void approveMatchesUpstreamPacking() {
    final var expected = vector().u8(4).u64LE(1).done();

    final var ix = Token2022Program.approve(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, DELEGATE, OWNER, 1L);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(DELEGATE),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_revoke_packing` and `revoke`.
  @Test
  void revokeMatchesUpstreamPacking() {
    final var expected = vector().u8(5).done();

    final var ix = Token2022Program.revoke(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, OWNER);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_set_authority_packing` (the `COption::Some` arm) and `set_authority`. The
  /// absent arm is pinned by `TokenInstruction::pack_pubkey_option`, whose `None` encoding
  /// `test_initialize_mint_packing` fixes as a single zero byte.
  @Test
  void setAuthorityMatchesUpstreamPacking() {
    final var withNewAuthority = vector().u8(6).u8(1).u8(1).repeat(4, 32).done();
    final var ix = Token2022Program.setAuthority(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, OWNER, AuthorityType.freezeAccount, filledKey(4)
    );
    assertArrayEquals(withNewAuthority, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());

    final var cleared = vector().u8(6).u8(1).u8(0).done();
    assertArrayEquals(cleared, Token2022Program.setAuthority(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, OWNER, AuthorityType.freezeAccount, null
    ).data());
  }

  /// Oracle: `test_mint_to_packing` and `mint_to`.
  @Test
  void mintToMatchesUpstreamPacking() {
    final var expected = vector().u8(7).u64LE(1).done();

    final var ix = Token2022Program.mintTo(INVOKED_TOKEN_2022, MINT, TOKEN_ACCOUNT, OWNER, 1L);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(MINT),
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_burn_packing` and `burn`.
  @Test
  void burnMatchesUpstreamPacking() {
    final var expected = vector().u8(8).u64LE(1).done();

    final var ix = Token2022Program.burn(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, OWNER, 1L);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createWrite(MINT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_close_account_packing` and `close_account`.
  @Test
  void closeAccountMatchesUpstreamPacking() {
    final var expected = vector().u8(9).done();

    final var ix = Token2022Program.closeAccount(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, DESTINATION, OWNER);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createWrite(DESTINATION),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_freeze_account_packing` and `freeze_account`.
  @Test
  void freezeAccountMatchesUpstreamPacking() {
    final var expected = vector().u8(10).done();

    final var ix = Token2022Program.freezeAccount(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, OWNER);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_thaw_account_packing` and `thaw_account`.
  @Test
  void thawAccountMatchesUpstreamPacking() {
    final var expected = vector().u8(11).done();

    final var ix = Token2022Program.thawAccount(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, OWNER);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_transfer_checked_packing` and `transfer_checked`.
  @Test
  void transferCheckedMatchesUpstreamPacking() {
    final var expected = vector().u8(12).u64LE(1).u8(2).done();

    final var ix = Token2022Program.transferChecked(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, DESTINATION, OWNER, 1L, 2
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createWrite(DESTINATION),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_approve_checked_packing` and `approve_checked`.
  @Test
  void approveCheckedMatchesUpstreamPacking() {
    final var expected = vector().u8(13).u64LE(1).u8(2).done();

    final var ix = Token2022Program.approveChecked(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, DELEGATE, OWNER, 1L, 2
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createRead(DELEGATE),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_mint_to_checked_packing` and `mint_to_checked`.
  @Test
  void mintToCheckedMatchesUpstreamPacking() {
    final var expected = vector().u8(14).u64LE(1).u8(2).done();

    final var ix = Token2022Program.mintToChecked(INVOKED_TOKEN_2022, MINT, TOKEN_ACCOUNT, OWNER, 1L, 2);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(MINT),
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_burn_checked_packing` and `burn_checked`.
  @Test
  void burnCheckedMatchesUpstreamPacking() {
    final var expected = vector().u8(15).u64LE(1).u8(2).done();

    final var ix = Token2022Program.burnChecked(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, OWNER, 1L, 2);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createWrite(MINT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_initialize_account2_packing` and `initialize_account2`.
  @Test
  void initializeAccount2MatchesUpstreamPacking() {
    final var expected = vector().u8(16).repeat(2, 32).done();

    final var ix = Token2022Program.initializeAccount2(
        INVOKED_TOKEN_2022, SolanaAccounts.MAIN_NET, TOKEN_ACCOUNT, MINT, filledKey(2)
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createRead(RENT_SYSVAR)
    ), ix.accounts());
  }

  /// Oracle: `test_sync_native_packing` and `sync_native`, whose account list is the token account
  /// alone. The IDL marks the rent sysvar optional, and the generated key helper appends it only
  /// when handed a `SolanaAccounts`; both spellings encode the same byte.
  @Test
  void syncNativeMatchesUpstreamPacking() {
    final var expected = vector().u8(17).done();

    final var ix = Token2022Program.syncNative(INVOKED_TOKEN_2022, null, TOKEN_ACCOUNT);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createWrite(TOKEN_ACCOUNT)), ix.accounts());

    final var withRent = Token2022Program.syncNative(INVOKED_TOKEN_2022, SolanaAccounts.MAIN_NET, TOKEN_ACCOUNT);
    assertArrayEquals(expected, withRent.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(RENT_SYSVAR)
    ), withRent.accounts());
  }

  /// Oracle: `test_initialize_account3_packing` and `initialize_account3`.
  @Test
  void initializeAccount3MatchesUpstreamPacking() {
    final var expected = vector().u8(18).repeat(2, 32).done();

    final var ix = Token2022Program.initializeAccount3(INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, filledKey(2));

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT)
    ), ix.accounts());
  }

  /// Oracle: `test_initialize_multisig2_packing` and `initialize_multisig2`, which drops the rent
  /// sysvar and appends the M signers as read-only non-signers.
  @Test
  void initializeMultisig2MatchesUpstreamPacking() {
    final var expected = vector().u8(19).u8(1).done();

    assertEquals(List.of(AccountMeta.createWrite(MULTISIG)), Token2022Program.initializeMultisig2Keys(MULTISIG));

    final var keys = new ArrayList<AccountMeta>();
    keys.add(AccountMeta.createWrite(MULTISIG));
    MULTISIG_SIGNERS.forEach(signer -> keys.add(AccountMeta.createRead(signer)));

    final var ix = Token2022Program.initializeMultisig2(INVOKED_TOKEN_2022, keys, 1);

    assertArrayEquals(expected, ix.data());
    assertEquals(keys, ix.accounts());
  }

  /// Oracle: `test_initialize_mint2_packing` and `initialize_mint2`.
  @Test
  void initializeMint2MatchesUpstreamPacking() {
    final var withoutFreezeAuthority = vector().u8(20).u8(2).repeat(1, 32).u8(0).done();
    final var noFreezeIx = Token2022Program.initializeMint2(INVOKED_TOKEN_2022, MINT, 2, filledKey(1), null);
    assertArrayEquals(withoutFreezeAuthority, noFreezeIx.data());
    assertEquals(List.of(AccountMeta.createWrite(MINT)), noFreezeIx.accounts());

    final var withFreezeAuthority = vector().u8(20).u8(2).repeat(2, 32).u8(1).repeat(3, 32).done();
    assertArrayEquals(withFreezeAuthority, Token2022Program.initializeMint2(
        INVOKED_TOKEN_2022, MINT, 2, filledKey(2), filledKey(3)
    ).data());
  }

  /// Oracle: `test_get_account_data_size_packing` (the empty-`extension_types` vector) and
  /// `get_account_data_size`. The IDL declares no `extensionTypes` argument, so the generated
  /// builder emits only the discriminator — the `[21, 1, 0, 2, 0]` vector, which asks for a size
  /// including named extensions, has no Java spelling.
  @Test
  void getAccountDataSizeMatchesUpstreamPacking() {
    final var expected = vector().u8(21).done();

    final var ix = Token2022Program.getAccountDataSize(INVOKED_TOKEN_2022, MINT);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createRead(MINT)), ix.accounts());
  }

  /// Oracle: `test_amount_to_ui_amount_packing` and `amount_to_ui_amount`.
  @Test
  void amountToUiAmountMatchesUpstreamPacking() {
    final var expected = vector().u8(23).u64LE(42).done();

    final var ix = Token2022Program.amountToUiAmount(INVOKED_TOKEN_2022, MINT, 42L);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createRead(MINT)), ix.accounts());
  }

  /// Oracle: `test_ui_amount_to_amount_packing` and `ui_amount_to_amount`. The string is the
  /// unprefixed remainder of the instruction data.
  @Test
  void uiAmountToAmountMatchesUpstreamPacking() {
    final var expected = vector().u8(24).ascii("0.42").done();

    final var ix = Token2022Program.uiAmountToAmount(INVOKED_TOKEN_2022, MINT, "0.42");

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createRead(MINT)), ix.accounts());
  }

  /// Oracle: `test_initialize_mint_close_authority_packing` and `initialize_mint_close_authority`.
  @Test
  void initializeMintCloseAuthorityMatchesUpstreamPacking() {
    final var expected = vector().u8(25).u8(1).repeat(10, 32).done();

    final var ix = Token2022Program.initializeMintCloseAuthority(INVOKED_TOKEN_2022, MINT, filledKey(10));

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createWrite(MINT)), ix.accounts());
  }

  /// Oracle: `TokenInstruction::pack`'s `Reallocate` arm, which writes the same little-endian `u16`
  /// extension-type array that `test_get_account_data_size_packing` pins as `1, 0, 2, 0` for
  /// `[TransferFeeConfig, TransferFeeAmount]`. Account list from `reallocate`.
  @Test
  void reallocateMatchesUpstreamExtensionTypeEncoding() {
    final var expected = vector().u8(29).u16LE(1).u16LE(2).done();

    final var ix = Token2022Program.reallocate(
        INVOKED_TOKEN_2022,
        SolanaAccounts.MAIN_NET,
        TOKEN_ACCOUNT,
        PAYER,
        OWNER,
        new ExtensionType[]{ExtensionType.transferFeeConfig, ExtensionType.transferFeeAmount}
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createWritableSigner(PAYER),
        AccountMeta.createRead(SYSTEM_PROGRAM),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_create_native_mint_packing` and `create_native_mint`, whose second account is
  /// `crate::native_mint::id()`.
  @Test
  void createNativeMintMatchesUpstreamPacking() {
    final var expected = vector().u8(31).done();

    final var ix = Token2022Program.createNativeMint(
        INVOKED_TOKEN_2022, SolanaAccounts.MAIN_NET, PAYER, NATIVE_MINT
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWritableSigner(PAYER),
        AccountMeta.createWrite(NATIVE_MINT),
        AccountMeta.createRead(SYSTEM_PROGRAM)
    ), ix.accounts());
  }

  /// Oracle: `test_initialize_permanent_delegate_packing` and `initialize_permanent_delegate`.
  @Test
  void initializePermanentDelegateMatchesUpstreamPacking() {
    final var expected = vector().u8(35).repeat(11, 32).done();

    final var ix = Token2022Program.initializePermanentDelegate(INVOKED_TOKEN_2022, MINT, filledKey(11));

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createWrite(MINT)), ix.accounts());
  }

  /// Oracle: `test_unwrap_lamports_packing` and `unwrap_lamports`, covering both `COption` arms.
  @Test
  void unwrapLamportsMatchesUpstreamPacking() {
    final var wholeBalance = vector().u8(45).u8(0).done();
    final var wholeBalanceIx = Token2022Program.unwrapLamports(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, DESTINATION, OWNER, OptionalLong.empty()
    );
    assertArrayEquals(wholeBalance, wholeBalanceIx.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createWrite(DESTINATION),
        AccountMeta.createReadOnlySigner(OWNER)
    ), wholeBalanceIx.accounts());

    final var oneLamport = vector().u8(45).u8(1).u64LE(1).done();
    assertArrayEquals(oneLamport, Token2022Program.unwrapLamports(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, DESTINATION, OWNER, OptionalLong.of(1L)
    ).data());
  }

  // --- transfer fee extension: interface/src/extension/transfer_fee/instruction.rs --------------
  //
  // `encode_instruction_data` there prefixes every vector below with `TokenInstruction::
  // TransferFeeExtension.pack()`, the single byte 26; `test_instruction_packing` pins the rest.

  /// Oracle: `test_instruction_packing`'s `InitializeTransferFeeConfig` arm and
  /// `initialize_transfer_fee_config`.
  @Test
  void initializeTransferFeeConfigMatchesUpstreamPacking() {
    final var expected = vector()
        .u8(26).u8(0)
        .u8(1).repeat(11, 32)
        .u8(0)
        .u16LE(111)
        .u64LE(-1L)
        .done();

    final var ix = Token2022Program.initializeTransferFeeConfig(
        INVOKED_TOKEN_2022, MINT, filledKey(11), null, 111, -1L
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createWrite(MINT)), ix.accounts());
  }

  /// Oracle: `test_instruction_packing`'s `TransferCheckedWithFee` arm and
  /// `transfer_checked_with_fee`.
  @Test
  void transferCheckedWithFeeMatchesUpstreamPacking() {
    final var expected = vector().u8(26).u8(1).u64LE(24).u8(24).u64LE(23).done();

    final var ix = Token2022Program.transferCheckedWithFee(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, DESTINATION, OWNER, 24L, 24, 23L
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(TOKEN_ACCOUNT),
        AccountMeta.createRead(MINT),
        AccountMeta.createWrite(DESTINATION),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_instruction_packing`'s `WithdrawWithheldTokensFromMint` arm and
  /// `withdraw_withheld_tokens_from_mint`.
  @Test
  void withdrawWithheldTokensFromMintMatchesUpstreamPacking() {
    final var expected = vector().u8(26).u8(2).done();

    final var ix = Token2022Program.withdrawWithheldTokensFromMint(
        INVOKED_TOKEN_2022, MINT, FEE_RECEIVER, OWNER
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(MINT),
        AccountMeta.createWrite(FEE_RECEIVER),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_instruction_packing`'s `WithdrawWithheldTokensFromAccounts` arm and
  /// `withdraw_withheld_tokens_from_accounts`. The N source accounts it withdraws from follow the
  /// declared list and are the caller's to append.
  @Test
  void withdrawWithheldTokensFromAccountsMatchesUpstreamPacking() {
    final var expected = vector().u8(26).u8(3).u8(255).done();

    final var ix = Token2022Program.withdrawWithheldTokensFromAccounts(
        INVOKED_TOKEN_2022, MINT, FEE_RECEIVER, OWNER, 255
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createRead(MINT),
        AccountMeta.createWrite(FEE_RECEIVER),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  /// Oracle: `test_instruction_packing`'s `HarvestWithheldTokensToMint` arm and
  /// `harvest_withheld_tokens_to_mint`.
  @Test
  void harvestWithheldTokensToMintMatchesUpstreamPacking() {
    final var expected = vector().u8(26).u8(4).done();

    final var ix = Token2022Program.harvestWithheldTokensToMint(INVOKED_TOKEN_2022, MINT);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(AccountMeta.createWrite(MINT)), ix.accounts());
  }

  /// Oracle: `test_instruction_packing`'s `SetTransferFee` arm and `set_transfer_fee`.
  @Test
  void setTransferFeeMatchesUpstreamPacking() {
    final var expected = vector().u8(26).u8(5).u16LE(0xFFFF).u64LE(-1L).done();

    final var ix = Token2022Program.setTransferFee(INVOKED_TOKEN_2022, MINT, OWNER, 0xFFFF, -1L);

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(MINT),
        AccountMeta.createReadOnlySigner(OWNER)
    ), ix.accounts());
  }

  // --- confidential families: @solana-program/token-2022 0.16.1 ---------------------------------
  //
  // The JS builders were run with addresses whose 32 bytes are a single repeated constant, and
  // fixed-length ciphertext fields filled with one repeated byte, so every value below restates
  // exactly what was passed. Roles recorded from the emitted account metas.

  private static final PublicKey JS_1 = filledKey(1);
  private static final PublicKey JS_2 = filledKey(2);
  private static final PublicKey JS_3 = filledKey(3);
  private static final PublicKey JS_4 = filledKey(4);
  private static final PublicKey JS_5 = filledKey(5);
  private static final PublicKey JS_6 = filledKey(6);
  private static final PublicKey JS_7 = filledKey(7);
  private static final PublicKey JS_8 = filledKey(8);

  private static DecryptableBalance decryptableBalance(final int fill) {
    final byte[] val = new byte[DecryptableBalance.BYTES];
    Arrays.fill(val, (byte) fill);
    return new DecryptableBalance(val);
  }

  private static EncryptedBalance encryptedBalance(final int fill) {
    final byte[] val = new byte[EncryptedBalance.BYTES];
    Arrays.fill(val, (byte) fill);
    return new EncryptedBalance(val);
  }

  /// Oracle: `getConfidentialTransferInstruction` in the JS client's
  /// `generated/instructions/confidentialTransfer.ts`. Extension 27, sub-instruction 7.
  @Test
  void confidentialTransferMatchesUpstreamJsClient() {
    final var expected = vector()
        .u8(27).u8(7)
        .repeat(0x11, 36)
        .repeat(0x22, 64)
        .repeat(0x33, 64)
        .u8(5).u8(6).u8(7)
        .done();
    assertEquals(169, expected.length);

    final var ix = Token2022Program.confidentialTransfer(
        INVOKED_TOKEN_2022,
        JS_1, JS_2, JS_3, JS_4, JS_5, JS_6, JS_7, JS_8,
        decryptableBalance(0x11), encryptedBalance(0x22), encryptedBalance(0x33),
        5, 6, 7
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createRead(JS_2),
        AccountMeta.createWrite(JS_3),
        AccountMeta.createRead(JS_4),
        AccountMeta.createRead(JS_5),
        AccountMeta.createRead(JS_6),
        AccountMeta.createRead(JS_7),
        AccountMeta.createReadOnlySigner(JS_8)
    ), ix.accounts());

    // With every optional record account omitted the JS builder drops them from the list rather
    // than substituting the program id, and emits the same data bytes.
    final var omitted = Token2022Program.confidentialTransfer(
        INVOKED_TOKEN_2022,
        JS_1, JS_2, JS_3, null, null, null, null, JS_8,
        decryptableBalance(0x11), encryptedBalance(0x22), encryptedBalance(0x33),
        5, 6, 7
    );
    assertArrayEquals(expected, omitted.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createRead(JS_2),
        AccountMeta.createWrite(JS_3),
        AccountMeta.createReadOnlySigner(JS_8)
    ), omitted.accounts());
  }

  /// Oracle: `getConfidentialWithdrawInstruction` in the JS client's
  /// `generated/instructions/confidentialWithdraw.ts`. Extension 27, sub-instruction 6.
  @Test
  void confidentialWithdrawMatchesUpstreamJsClient() {
    final var expected = vector()
        .u8(27).u8(6)
        .u64LE(0x0102030405060708L)
        .u8(9)
        .repeat(0x11, 36)
        .u8(5).u8(7)
        .done();
    assertEquals(49, expected.length);

    final var ix = Token2022Program.confidentialWithdraw(
        INVOKED_TOKEN_2022,
        JS_1, JS_2, JS_3, JS_4, JS_5, JS_6,
        0x0102030405060708L, 9, decryptableBalance(0x11), 5, 7
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createRead(JS_2),
        AccountMeta.createRead(JS_3),
        AccountMeta.createRead(JS_4),
        AccountMeta.createRead(JS_5),
        AccountMeta.createReadOnlySigner(JS_6)
    ), ix.accounts());

    final var omitted = Token2022Program.confidentialWithdraw(
        INVOKED_TOKEN_2022,
        JS_1, JS_2, null, null, null, JS_6,
        0x0102030405060708L, 9, decryptableBalance(0x11), 5, 7
    );
    assertArrayEquals(expected, omitted.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createRead(JS_2),
        AccountMeta.createReadOnlySigner(JS_6)
    ), omitted.accounts());
  }

  /// Oracle: `getInitializeConfidentialTransferMintInstruction` in the JS client's
  /// `generated/instructions/initializeConfidentialTransferMint.ts`. Extension 27,
  /// sub-instruction 0. Both authority fields are zeroable options: no tag byte, 67 bytes in
  /// every variant, and absent means thirty-two zero bytes.
  @Test
  void initializeConfidentialTransferMintMatchesUpstreamJsClient() {
    final var bothPresent = vector().u8(27).u8(0).repeat(2, 32).bool(true).repeat(3, 32).done();
    assertEquals(67, bothPresent.length);
    final var bothPresentIx = Token2022Program.initializeConfidentialTransferMint(
        INVOKED_TOKEN_2022, JS_1, JS_2, true, JS_3
    );
    assertArrayEquals(bothPresent, bothPresentIx.data());
    assertEquals(List.of(AccountMeta.createWrite(JS_1)), bothPresentIx.accounts());

    final var neither = vector().u8(27).u8(0).repeat(0, 32).bool(true).repeat(0, 32).done();
    assertArrayEquals(neither, Token2022Program.initializeConfidentialTransferMint(
        INVOKED_TOKEN_2022, JS_1, null, true, null
    ).data());

    final var authorityOnly = vector().u8(27).u8(0).repeat(2, 32).bool(true).repeat(0, 32).done();
    assertArrayEquals(authorityOnly, Token2022Program.initializeConfidentialTransferMint(
        INVOKED_TOKEN_2022, JS_1, JS_2, true, null
    ).data());

    final var auditorOnly = vector().u8(27).u8(0).repeat(0, 32).bool(true).repeat(3, 32).done();
    assertArrayEquals(auditorOnly, Token2022Program.initializeConfidentialTransferMint(
        INVOKED_TOKEN_2022, JS_1, null, true, JS_3
    ).data());

    final var manualApproval = vector().u8(27).u8(0).repeat(2, 32).bool(false).repeat(3, 32).done();
    assertArrayEquals(manualApproval, Token2022Program.initializeConfidentialTransferMint(
        INVOKED_TOKEN_2022, JS_1, JS_2, false, JS_3
    ).data());
  }

  /// Oracle: `getConfidentialMintInstruction` in the JS client's
  /// `generated/instructions/confidentialMint.ts`. This one is the confidential mint/burn
  /// extension — 42 — with sub-instruction 3, and its mint account is writable.
  @Test
  void confidentialMintMatchesUpstreamJsClient() {
    final var expected = vector()
        .u8(42).u8(3)
        .repeat(0x11, 36)
        .repeat(0x22, 64)
        .repeat(0x33, 64)
        .u8(5).u8(6).u8(7)
        .done();
    assertEquals(169, expected.length);

    final var ix = Token2022Program.confidentialMint(
        INVOKED_TOKEN_2022,
        JS_1, JS_2, JS_3, JS_4, JS_5, JS_6, JS_7,
        decryptableBalance(0x11), encryptedBalance(0x22), encryptedBalance(0x33),
        5, 6, 7
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createWrite(JS_2),
        AccountMeta.createRead(JS_3),
        AccountMeta.createRead(JS_4),
        AccountMeta.createRead(JS_5),
        AccountMeta.createRead(JS_6),
        AccountMeta.createReadOnlySigner(JS_7)
    ), ix.accounts());

    final var omitted = Token2022Program.confidentialMint(
        INVOKED_TOKEN_2022,
        JS_1, JS_2, null, null, null, null, JS_7,
        decryptableBalance(0x11), encryptedBalance(0x22), encryptedBalance(0x33),
        5, 6, 7
    );
    assertArrayEquals(expected, omitted.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createWrite(JS_2),
        AccountMeta.createReadOnlySigner(JS_7)
    ), omitted.accounts());
  }

  /// Oracle: `getWithdrawWithheldTokensFromMintForConfidentialTransferFeeInstruction` in the JS
  /// client's `generated/instructions/
  /// withdrawWithheldTokensFromMintForConfidentialTransferFee.ts`. Extension 37, sub-instruction 1,
  /// and the only one of these five whose proof-instruction offset precedes the ciphertext.
  @Test
  void withdrawWithheldTokensForConfidentialTransferFeeMatchesUpstreamJsClient() {
    final var expected = vector().u8(37).u8(1).u8(5).repeat(0x11, 36).done();
    assertEquals(39, expected.length);

    final var ix = Token2022Program.withdrawWithheldTokensFromMintForConfidentialTransferFee(
        INVOKED_TOKEN_2022, JS_1, JS_2, JS_3, JS_4, 5, decryptableBalance(0x11)
    );

    assertArrayEquals(expected, ix.data());
    assertEquals(List.of(
        AccountMeta.createWrite(JS_1),
        AccountMeta.createWrite(JS_2),
        AccountMeta.createRead(JS_3),
        AccountMeta.createReadOnlySigner(JS_4)
    ), ix.accounts());

    // The JS builder demotes the authority to a read-only non-signer and appends the multisig
    // signers when it is handed a plain address rather than a signer. The generated key helper has
    // no such spelling, so that list is hand-built.
    final var keys = new ArrayList<AccountMeta>();
    keys.add(AccountMeta.createWrite(JS_1));
    keys.add(AccountMeta.createWrite(JS_2));
    keys.add(AccountMeta.createRead(JS_3));
    keys.add(AccountMeta.createRead(JS_4));
    keys.add(AccountMeta.createReadOnlySigner(JS_5));
    keys.add(AccountMeta.createReadOnlySigner(JS_6));

    final var multisig = Token2022Program.withdrawWithheldTokensFromMintForConfidentialTransferFee(
        INVOKED_TOKEN_2022, keys, 5, decryptableBalance(0x11)
    );
    assertArrayEquals(expected, multisig.data());
    assertEquals(keys, multisig.accounts());
  }

  // --- decode / re-encode round trips -----------------------------------------------------------

  /// Every base-instruction vector above must decode and come back byte for byte.
  @Test
  void baseInstructionDataRoundTrips() {
    final var initializeMint = vector().u8(0).u8(2).repeat(2, 32).u8(1).repeat(3, 32).done();
    assertReserializes(initializeMint, Token2022Program.InitializeMintIxData.read(initializeMint, 0));

    final var initializeMintNoFreeze = vector().u8(0).u8(2).repeat(1, 32).u8(0).done();
    assertReserializes(initializeMintNoFreeze, Token2022Program.InitializeMintIxData.read(initializeMintNoFreeze, 0));

    final var initializeMultisig = vector().u8(2).u8(1).done();
    assertReserializes(initializeMultisig, Token2022Program.InitializeMultisigIxData.read(initializeMultisig, 0));

    final var transfer = vector().u8(3).u64LE(1).done();
    assertReserializes(transfer, Token2022Program.TransferIxData.read(transfer, 0));

    final var setAuthority = vector().u8(6).u8(1).u8(1).repeat(4, 32).done();
    final var parsedSetAuthority = Token2022Program.SetAuthorityIxData.read(setAuthority, 0);
    assertEquals(AuthorityType.freezeAccount, parsedSetAuthority.authorityType());
    assertReserializes(setAuthority, parsedSetAuthority);

    final var setAuthorityCleared = vector().u8(6).u8(1).u8(0).done();
    assertReserializes(setAuthorityCleared, Token2022Program.SetAuthorityIxData.read(setAuthorityCleared, 0));

    final var transferChecked = vector().u8(12).u64LE(1).u8(2).done();
    assertReserializes(transferChecked, Token2022Program.TransferCheckedIxData.read(transferChecked, 0));

    final var getAccountDataSize = vector().u8(21).done();
    assertReserializes(getAccountDataSize, Token2022Program.GetAccountDataSizeIxData.read(getAccountDataSize, 0));

    final var amountToUiAmount = vector().u8(23).u64LE(42).done();
    assertReserializes(amountToUiAmount, Token2022Program.AmountToUiAmountIxData.read(amountToUiAmount, 0));

    final var uiAmountToAmount = vector().u8(24).ascii("0.42").done();
    final var parsedUiAmount = Token2022Program.UiAmountToAmountIxData.read(uiAmountToAmount, 0);
    assertEquals("0.42", parsedUiAmount.uiAmount());
    assertReserializes(uiAmountToAmount, parsedUiAmount);

    final var reallocate = vector().u8(29).u16LE(1).u16LE(2).done();
    final var parsedReallocate = Token2022Program.ReallocateIxData.read(reallocate, 0);
    assertArrayEquals(
        new ExtensionType[]{ExtensionType.transferFeeConfig, ExtensionType.transferFeeAmount},
        parsedReallocate.newExtensionTypes()
    );
    assertReserializes(reallocate, parsedReallocate);

    final var unwrapWholeBalance = vector().u8(45).u8(0).done();
    assertReserializes(unwrapWholeBalance, Token2022Program.UnwrapLamportsIxData.read(unwrapWholeBalance, 0));

    final var unwrapOneLamport = vector().u8(45).u8(1).u64LE(1).done();
    final var parsedUnwrap = Token2022Program.UnwrapLamportsIxData.read(unwrapOneLamport, 0);
    assertEquals(OptionalLong.of(1L), parsedUnwrap.amount());
    assertReserializes(unwrapOneLamport, parsedUnwrap);
  }

  /// The six transfer-fee sub-instructions all dispatch on 26, so the sub-discriminator each
  /// parsed record carries is what tells them apart.
  @Test
  void transferFeeInstructionDataRoundTrips() {
    final var initializeConfig = vector()
        .u8(26).u8(0).u8(1).repeat(11, 32).u8(0).u16LE(111).u64LE(-1L)
        .done();
    final var parsedConfig = Token2022Program.InitializeTransferFeeConfigIxData.read(initializeConfig, 0);
    assertEquals(26, parsedConfig.discriminator());
    assertEquals(0, parsedConfig.transferFeeDiscriminator());
    assertEquals(111, parsedConfig.transferFeeBasisPoints());
    assertEquals(-1L, parsedConfig.maximumFee());
    assertReserializes(initializeConfig, parsedConfig);

    final var transferWithFee = vector().u8(26).u8(1).u64LE(24).u8(24).u64LE(23).done();
    final var parsedTransfer = Token2022Program.TransferCheckedWithFeeIxData.read(transferWithFee, 0);
    assertEquals(1, parsedTransfer.transferFeeDiscriminator());
    assertReserializes(transferWithFee, parsedTransfer);

    final var fromMint = vector().u8(26).u8(2).done();
    final var parsedFromMint = Token2022Program.WithdrawWithheldTokensFromMintIxData.read(fromMint, 0);
    assertEquals(2, parsedFromMint.transferFeeDiscriminator());
    assertReserializes(fromMint, parsedFromMint);

    final var fromAccounts = vector().u8(26).u8(3).u8(255).done();
    final var parsedFromAccounts = Token2022Program.WithdrawWithheldTokensFromAccountsIxData.read(fromAccounts, 0);
    assertEquals(3, parsedFromAccounts.transferFeeDiscriminator());
    assertEquals(255, parsedFromAccounts.numTokenAccounts());
    assertReserializes(fromAccounts, parsedFromAccounts);

    final var harvest = vector().u8(26).u8(4).done();
    final var parsedHarvest = Token2022Program.HarvestWithheldTokensToMintIxData.read(harvest, 0);
    assertEquals(4, parsedHarvest.transferFeeDiscriminator());
    assertReserializes(harvest, parsedHarvest);

    final var setFee = vector().u8(26).u8(5).u16LE(0xFFFF).u64LE(-1L).done();
    final var parsedSetFee = Token2022Program.SetTransferFeeIxData.read(setFee, 0);
    assertEquals(5, parsedSetFee.transferFeeDiscriminator());
    assertEquals(0xFFFF, parsedSetFee.transferFeeBasisPoints());
    assertReserializes(setFee, parsedSetFee);
  }

  /// The confidential vectors, read back and re-encoded. The zeroable options of
  /// `initializeConfidentialTransferMint` decode to null and re-encode to zeroes.
  @Test
  void confidentialInstructionDataRoundTrips() {
    final var confidentialTransfer = vector()
        .u8(27).u8(7).repeat(0x11, 36).repeat(0x22, 64).repeat(0x33, 64).u8(5).u8(6).u8(7)
        .done();
    final var parsedTransfer = Token2022Program.ConfidentialTransferIxData.read(confidentialTransfer, 0);
    assertEquals(7, parsedTransfer.confidentialTransferDiscriminator());
    assertReserializes(confidentialTransfer, parsedTransfer);

    final var confidentialWithdraw = vector()
        .u8(27).u8(6).u64LE(0x0102030405060708L).u8(9).repeat(0x11, 36).u8(5).u8(7)
        .done();
    final var parsedWithdraw = Token2022Program.ConfidentialWithdrawIxData.read(confidentialWithdraw, 0);
    assertEquals(6, parsedWithdraw.confidentialTransferDiscriminator());
    assertEquals(0x0102030405060708L, parsedWithdraw.amount());
    assertReserializes(confidentialWithdraw, parsedWithdraw);

    final var initializeMint = vector().u8(27).u8(0).repeat(2, 32).bool(true).repeat(3, 32).done();
    assertReserializes(initializeMint, Token2022Program.InitializeConfidentialTransferMintIxData.read(initializeMint, 0));

    final var initializeMintNoAuthorities = vector().u8(27).u8(0).repeat(0, 32).bool(true).repeat(0, 32).done();
    final var parsedNoAuthorities =
        Token2022Program.InitializeConfidentialTransferMintIxData.read(initializeMintNoAuthorities, 0);
    assertNull(parsedNoAuthorities.authority());
    assertNull(parsedNoAuthorities.auditorElgamalPubkey());
    assertReserializes(initializeMintNoAuthorities, parsedNoAuthorities);

    final var confidentialMint = vector()
        .u8(42).u8(3).repeat(0x11, 36).repeat(0x22, 64).repeat(0x33, 64).u8(5).u8(6).u8(7)
        .done();
    final var parsedMint = Token2022Program.ConfidentialMintIxData.read(confidentialMint, 0);
    assertEquals(3, parsedMint.confidentialMintBurnDiscriminator());
    assertReserializes(confidentialMint, parsedMint);

    final var withdrawWithheld = vector().u8(37).u8(1).u8(5).repeat(0x11, 36).done();
    final var parsedWithheld =
        Token2022Program.WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData.read(withdrawWithheld, 0);
    assertEquals(1, parsedWithheld.confidentialTransferFeeDiscriminator());
    assertReserializes(withdrawWithheld, parsedWithheld);
  }

  /// Every generated discriminator constant is the whole key the program dispatches on, so no two
  /// of the 99 instructions share one. Oracle: the codama document declares two
  /// fieldDiscriminatorNodes on each extension instruction, and the Rust sub-instruction enums
  /// under interface/src/extension/*/instruction.rs are what the second byte indexes. Until the
  /// client regenerated on 2026-09-06 the constants carried only the first byte, so 58 of them
  /// collided in 15 families and a constant could not tell sibling sub-instructions apart.
  @Test
  void everyInstructionHasItsOwnDiscriminator() throws IllegalAccessException {
    final var byLength = new int[9];
    final var seen = new HashSet<List<Byte>>();
    int constants = 0;
    for (final var field : Token2022Program.class.getDeclaredFields()) {
      if (field.getType() == Discriminator.class
          && Modifier.isStatic(field.getModifiers())
          && field.getName().endsWith("_DISCRIMINATOR")) {
        final byte[] data = ((Discriminator) field.get(null)).data();
        ++byLength[data.length];
        final var key = new ArrayList<Byte>(data.length);
        for (final byte b : data) {
          key.add(b);
        }
        assertTrue(seen.add(key), field.getName() + " shares its discriminator with another instruction");
        ++constants;
      }
    }
    assertEquals(99, constants);
    assertEquals(32, byLength[1], "base instructions");
    assertEquals(58, byLength[2], "extension sub-instructions");
    assertEquals(9, byLength[8], "token-metadata and token-group interface instructions");

    // the constant is exactly the prefix its builder writes, and a sibling's is not
    final byte[] data = Token2022Program.transferCheckedWithFee(
        INVOKED_TOKEN_2022, TOKEN_ACCOUNT, MINT, DESTINATION, OWNER, 24L, 24, 23L
    ).data();
    assertTrue(Token2022Program.TRANSFER_CHECKED_WITH_FEE_DISCRIMINATOR.equals(data, 0));
    assertFalse(Token2022Program.INITIALIZE_TRANSFER_FEE_CONFIG_DISCRIMINATOR.equals(data, 0));
    assertFalse(Token2022Program.SET_TRANSFER_FEE_DISCRIMINATOR.equals(data, 0));
  }
}
