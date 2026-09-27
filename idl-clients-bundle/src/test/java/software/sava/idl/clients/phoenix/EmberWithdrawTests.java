package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram;
import software.sava.idl.clients.phoenix.ember.gen.EmberProgram.WithdrawIxData;
import software.sava.idl.clients.phoenix.ember.gen.types.DepositParams;
import software.sava.idl.clients.phoenix.ember.gen.types.WithdrawParams;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Ember's instruction data, held to bytes this repository's generator did not produce.
///
/// Ember's published IDL declares `withdraw`'s `WithdrawParams.amount` as `u64`; the deployed
/// program reads `Option<u64>`, and a `u64` encoding makes it panic on every call. The client is
/// generated with the type corrected by `"fieldTypes"` in `main_net_programs.json`, so a round trip
/// through the generated reader would agree with the generated writer whichever type it used — it
/// cannot see the defect. The oracles here are independent of the IDL:
///
/// - Phoenix's own SDK, `Ellipsis-Labs/rise-public` at `e688686`: `instructions.json` carries
///   `"EmberWithdraw": "b712469c946da122010100000000000000"` and
///   `"EmberDeposit": "f223c68952e1f2b60100000000000000"`, and `rust/ix/src/cpi.rs`
///   `write_ember_withdraw_data` writes the discriminator then `Option<u64>`, 9 bytes for `None`.
/// - Mainnet: withdraw `24tXGERMtU6BtWgmU13k7swWk7UyQigFjJRVKX8CFKCcV2RX6M4j2KgRxkpJnG5BAqc8duUowQ6nCm7Dy2xbDzLM`
///   carried `b712469c946da1220140f6440700000000`, `Some(121_960_000)`, as every sampled withdraw
///   carried 17 bytes. Simulated against live accounts on 2026-09-27, `Some(1)` and `None` both
///   succeeded and every 16-byte `u64` encoding panicked.
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
}
