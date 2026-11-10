package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram.WithdrawIxData;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/// Ember's instruction encodings, held to Phoenix SDK and mainnet byte vectors.
///
/// - Phoenix's own SDK, `Ellipsis-Labs/rise-public` at `e688686`: `instructions.json` carries
///   `"EmberWithdraw": "b712469c946da122010100000000000000"` and
///   `"EmberDeposit": "f223c68952e1f2b60100000000000000"`, and `rust/ix/src/cpi.rs`
///   `write_ember_withdraw_data` writes the discriminator then `Option<u64>`, 9 bytes for `None`.
/// - Mainnet: withdraw `24tXGERMtU6BtWgmU13k7swWk7UyQigFjJRVKX8CFKCcV2RX6M4j2KgRxkpJnG5BAqc8duUowQ6nCm7Dy2xbDzLM`
///   carried `b712469c946da1220140f6440700000000`, `Some(121_960_000)`, as every sampled withdraw
///   carried 17 bytes. The current IDL now declares the same `Option<u64>` wire shape.
final class EmberWithdrawTests {

  private static final HexFormat HEX = HexFormat.of();

  private static byte[] data(final Instruction ix) {
    return Arrays.copyOfRange(ix.data(), ix.offset(), ix.offset() + ix.len());
  }

  private static byte[] withdraw(final OptionalLong amount) {
    return data(EmberProgram.withdraw(
        PhoenixAccounts.MAIN_NET.invokedEmberProgram(), List.of(), new WithdrawParams(amount)
    ));
  }

  @Test
  void aWithdrawOfOneEncodesAsPhoenixsSdkDoes() {
    assertEquals("b712469c946da122010100000000000000", HEX.formatHex(withdraw(OptionalLong.of(1))));
  }

  @Test
  void aWithdrawEncodesAsTheOneObservedOnMainnet() {
    assertEquals("b712469c946da1220140f6440700000000", HEX.formatHex(withdraw(OptionalLong.of(121_960_000L))));
  }

  /// `None` is the full withdrawal, and is the discriminator plus one tag byte.
  @Test
  void aFullWithdrawalIsTheTagAlone() {
    assertEquals("b712469c946da12200", HEX.formatHex(withdraw(OptionalLong.empty())));
  }

  @Test
  void theIxDataReaderReadsBackWhatTheProgramReads() {
    for (final var amount : List.of(
        OptionalLong.empty(), OptionalLong.of(1), OptionalLong.of(121_960_000L), OptionalLong.of(-1L)
    )) {
      final byte[] bytes = withdraw(amount);
      final var read = WithdrawIxData.read(bytes, 0);
      assertEquals(amount, read.withdrawParams().amount());
      assertEquals(EmberProgram.WITHDRAW_DISCRIMINATOR, read.discriminator());
      assertEquals(bytes.length, read.l());
    }
  }

  /// Deposit's argument was never wrong; it is here so the SDK's two Ember vectors are held
  /// together, and a regression in the untouched instruction would not pass unnoticed.
  @Test
  void aDepositOfOneEncodesAsPhoenixsSdkDoes() {
    final var ix = EmberProgram.deposit(
        PhoenixAccounts.MAIN_NET.invokedEmberProgram(), List.of(), new DepositParams(1)
    );
    assertEquals("f223c68952e1f2b60100000000000000", HEX.formatHex(data(ix)));
  }

  /// Both Phoenix SDKs request the owner's signature. In unsigned mainnet simulations at
  /// slots 467909182 and 467909234, a funded, undelegated source withdrew one unit only with
  /// that signature flag; the nonsigner form returned success without moving tokens.
  @Test
  void convenienceWithdrawalRequestsOwnerSignatureForAutomaticApproval() {
    final var accounts = PhoenixAccounts.MAIN_NET;
    final var owner = PublicKey.fromBase58Encoded("E11ipYYCnbbRnhsEaYetRLjkS5KM7Ld7BPbdMGVacmD3");
    final var tokenAccount = PublicKey.createPubKey(new byte[PublicKey.PUBLIC_KEY_LENGTH]);
    final byte[] otherKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(otherKey, (byte) 1);
    final var emberTokenAccount = PublicKey.createPubKey(otherKey);
    final var client = PhoenixClient.createClient(accounts);
    final var ix = client.withdraw(owner, accounts.usdcMint(), accounts.emberUSDCMint(),
        tokenAccount, emberTokenAccount, client.solanaAccounts().tokenProgram(),
        new WithdrawParams(OptionalLong.of(1)));

    assertEquals(owner, ix.accounts().getFirst().publicKey());
    assertTrue(ix.accounts().getFirst().signer(), "the owner must sign even with a separate fee payer");
    assertFalse(ix.accounts().getFirst().write());
    assertEquals("b712469c946da122010100000000000000", HEX.formatHex(data(ix)));
  }

  /// The SDK builds a readonly signer for the owner and a readonly state account.
  @Test
  void depositAccountRightsMatchPhoenixsSdk() {
    final var owner = PublicKey.fromBase58Encoded("E11ipYYCnbbRnhsEaYetRLjkS5KM7Ld7BPbdMGVacmD3");
    final var state = PublicKey.fromBase58Encoded("6ur7v6AXNpnHeEb6xuk7PyezvZ1i5GrgYyWZkNCpzbRz");
    final var ix = EmberProgram.deposit(
        PhoenixAccounts.MAIN_NET.invokedEmberProgram(), owner, state,
        PhoenixAccounts.MAIN_NET.usdcMint(), PhoenixAccounts.MAIN_NET.emberUSDCMint(),
        PublicKey.fromBase58Encoded("11111111111111111111111111111111"),
        PublicKey.fromBase58Encoded("So11111111111111111111111111111111111111112"),
        PhoenixAccounts.MAIN_NET.emberVaultProgram(),
        PublicKey.fromBase58Encoded("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"),
        new DepositParams(1)
    );
    assertEquals(8, ix.accounts().size());
    assertEquals(owner, ix.accounts().get(0).publicKey());
    assertTrue(ix.accounts().get(0).signer());
    assertFalse(ix.accounts().get(0).write());
    assertEquals(state, ix.accounts().get(1).publicKey());
    assertFalse(ix.accounts().get(1).signer());
    assertFalse(ix.accounts().get(1).write());
  }
}
