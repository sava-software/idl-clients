package software.sava.idl.clients.exponent;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.exponent.gen.ExponentCoreProgram;
import software.sava.idl.clients.exponent.gen.types.LpPosition;
import software.sava.idl.clients.exponent.gen.types.MarketTwo;
import software.sava.idl.clients.exponent.gen.types.Vault;
import software.sava.idl.clients.exponent.gen.types.YieldTokenPosition;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Decodes real Exponent data from mainnet.
///
/// Exponent declares its 42 instruction discriminators as **single bytes** — `buy_yt` is `[0]`,
/// `withdraw_yt` is `[8]` — via `#[instruction(discriminator = [N])]`. Anchor permits any width,
/// and dispatch is `data.starts_with(&DISCRIMINATOR)` slicing only `DISCRIMINATOR.len()`.
///
/// That prefix dispatch is exactly why the old eight-byte assumption was dangerous rather than
/// loud: a builder emitting `[8, 0,0,0,0,0,0,0]` still *matched* `withdraw_yt` on chain, and the
/// seven surplus zeros were then handed to Borsh as the leading bytes of the arguments. The
/// instruction below is the refutation — a real, successful mainnet call whose data is **nine**
/// bytes, not sixteen.
///
/// Its accounts, by contrast, carry ordinary eight-byte discriminators, so the account tests here
/// are a regression anchor showing the width work moved nothing on that path — they are *not*
/// evidence for the fix.
final class ExponentOnChainTests {

  private static byte[] account(final String name) {
    try (var in = ExponentOnChainTests.class.getResourceAsStream("/exponent/" + name + ".base64")) {
      assertNotNull(in, "fixture /exponent/" + name + ".base64 is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /// Instruction data from mainnet transaction
  /// `2s8FZz71ekR7ojLiPLm12SaFvsasGWDbPruuXf3hb6PtodgR6Bx5AHsU9TAQLYv5QhMgBgS6aRcwqU6oUK5ZSee4`:
  /// one discriminator byte then a u64. Sixteen bytes is what the old generator would have built.
  private static final byte[] WITHDRAW_YT = HexFormat.of().parseHex("084804203c3b060100");
  private static final byte[] DEPOSIT_YT = HexFormat.of().parseHex("074804203c3b060100");
  private static final long AMOUNT = 288_326_458_278_984L;

  @Test
  void aRealInstructionIsOneDiscriminatorByteThenItsArguments() {
    assertEquals(9, WITHDRAW_YT.length, "1 discriminator byte + one u64");
    assertEquals(8, WITHDRAW_YT[0]);

    assertEquals(1, ExponentCoreProgram.WITHDRAW_YT_DISCRIMINATOR.length());
    assertEquals(8, ExponentCoreProgram.WITHDRAW_YT_DISCRIMINATOR.data()[0]);
    assertEquals(1, ExponentCoreProgram.WithdrawYtIxData.AMOUNT_OFFSET, "the argument starts at offset 1");
  }

  @Test
  void aRealInstructionDecodesToTheAmountItCarried() {
    final var withdraw = ExponentCoreProgram.WithdrawYtIxData.read(WITHDRAW_YT, 0);
    assertNotNull(withdraw);
    assertEquals(AMOUNT, withdraw.amount());
    assertEquals(WITHDRAW_YT.length, withdraw.l());

    // the same transaction's inner deposit differs only in the leading byte, which pins ordering
    final var deposit = ExponentCoreProgram.DepositYtIxData.read(DEPOSIT_YT, 0);
    assertNotNull(deposit);
    assertEquals(AMOUNT, deposit.amount());
    assertEquals(7, ExponentCoreProgram.DEPOSIT_YT_DISCRIMINATOR.data()[0]);
  }

  /// The builder a caller actually uses, against the bytes the chain actually carried. This is the
  /// number the width fix moved — the array is sized from the discriminator's declared width, so
  /// before it this produced sixteen bytes for a nine-byte instruction and Anchor accepted it,
  /// handing the seven surplus zeros to Borsh as the leading bytes of `amount`.
  @Test
  void theBuilderProducesTheBytesTheChainCarried() {
    final var invoked = AccountMeta.createInvoked(
        PublicKey.fromBase58Encoded("ExponentnaRg3CQbW6dqQNZKXp7gtZ9DGMp1cwC4HAS7")
    );

    final var withdraw = ExponentCoreProgram.withdrawYt(invoked, List.of(), AMOUNT);
    assertArrayEquals(WITHDRAW_YT, withdraw.data(), "the built instruction must equal the captured one");

    final var deposit = ExponentCoreProgram.depositYt(invoked, List.of(), AMOUNT);
    assertArrayEquals(DEPOSIT_YT, deposit.data());
  }

  @Test
  void aRealInstructionReserializesToTheCapturedBytes() {
    final var withdraw = ExponentCoreProgram.WithdrawYtIxData.read(WITHDRAW_YT, 0);
    final byte[] out = new byte[WITHDRAW_YT.length];
    assertEquals(WITHDRAW_YT.length, withdraw.write(out, 0));
    assertArrayEquals(WITHDRAW_YT, out, "a rebuilt instruction must be byte-identical to the chain's");
  }

  /// Each decoder consumes a prefix of the account and never runs past it. Not equality: a Solana
  /// account is allocated at a fixed size and may carry slack past its serialized content —
  /// MarketTwo holds 685 bytes and decodes 662 — so demanding an exact match would assert
  /// something the layout does not promise. Overrunning is the failure that matters, and it is what
  /// a wrong discriminator width would have caused.
  @Test
  void realAccountsDecodeWithoutOverrunningTheirData() {
    final var vaultAddress = "14fXk2YSt9KbJgTttGRYwJ3uXB7ZRfjUbHfPYEdWJyKb";
    final byte[] vaultData = account("vault-" + vaultAddress);
    final var vault = Vault.read(PublicKey.fromBase58Encoded(vaultAddress), vaultData, 0);
    assertNotNull(vault);
    assertEquals(8, Vault.DISCRIMINATOR.length(), "accounts keep the customary eight bytes");
    assertTrue(vault.l() <= vaultData.length, () -> "Vault read past its account: " + vault.l() + " > " + vaultData.length);

    final var marketAddress = "12Hva9LLLmGXn6PvtareyEmDMP83ZuLaHpXHrcux2LUF";
    final byte[] marketData = account("marketTwo-" + marketAddress);
    final var market = MarketTwo.read(PublicKey.fromBase58Encoded(marketAddress), marketData, 0);
    assertNotNull(market);
    assertTrue(market.l() <= marketData.length, () -> "MarketTwo read past its account: " + market.l());
    // the account stores its own address, so this is a self-check needing no second lookup
    assertEquals(marketAddress, market.selfAddress().toBase58());

    final var ytAddress = "11dURnX19gtQtmYTFJeiBzh3513bwTpXPoA4g8Wa8Xk";
    final var yt = YieldTokenPosition.read(
        PublicKey.fromBase58Encoded(ytAddress), account("yieldTokenPosition-" + ytAddress), 0
    );
    assertNotNull(yt);
    assertTrue(yt.l() <= account("yieldTokenPosition-" + ytAddress).length);

    final var lpAddress = "1xZjGk8LTt3ZGXXWWvYagfkUcLQsNj4czwctCQZQxG";
    final var lp = LpPosition.read(
        PublicKey.fromBase58Encoded(lpAddress), account("lpPosition-" + lpAddress), 0
    );
    assertNotNull(lp);
    assertTrue(lp.l() <= account("lpPosition-" + lpAddress).length);
  }
}
