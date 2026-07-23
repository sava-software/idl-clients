package software.sava.idl.clients.meteora.dlmm;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.meteora.MeteoraPDAs;
import software.sava.idl.clients.meteora.dlmm.gen.types.AccountsType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// The `remaining_accounts` payload these helpers build is invisible to the
/// IDL: the program infers bin arrays from the metas-after-info length and
/// walks transfer-hook slices by the counts in `RemainingAccountsInfo`. A
/// dropped append or a slice miscount fails on chain with nothing pointing
/// back at the caller's list, so the tests pin the exact metas, order, and
/// flags each helper emits.
final class MeteoraDlmmRemainingAccountsTests {

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey PROGRAM = key(0x10);
  private static final PublicKey LB_PAIR = key(0x11);

  private static Instruction ix() {
    return Instruction.createInstruction(
        AccountMeta.createInvoked(PROGRAM),
        List.of(AccountMeta.createWrite(key(0x12))),
        new byte[]{1});
  }

  @Test
  void binAccountsCoverTheArrayRangeInOrderAndWritable() {
    // a range inside one bin array yields exactly that array's PDA
    final int lowerIdx = DlmmUtils.binIdToArrayIndex(0);
    final var single = MeteoraDlmmRemainingAccounts.deriveBinAccounts(PROGRAM, LB_PAIR, 0, 0);
    assertEquals(1, single.size());
    assertEquals(MeteoraPDAs.binArrayPdA(LB_PAIR, lowerIdx, PROGRAM).publicKey(),
        single.getFirst().publicKey());
    assertTrue(single.getFirst().write());

    // a range straddling arrays yields one writable PDA per array index,
    // lower to upper
    final int lowerBin = -1;
    final int upperBin = (int) software.sava.idl.clients.meteora.dlmm.gen.LbClmmConstants.MAX_BIN_PER_ARRAY * 2;
    final int lo = DlmmUtils.binIdToArrayIndex(lowerBin);
    final int hi = DlmmUtils.binIdToArrayIndex(upperBin);
    final var range = MeteoraDlmmRemainingAccounts.deriveBinAccounts(PROGRAM, LB_PAIR, lowerBin, upperBin);
    assertEquals(hi - lo + 1, range.size());
    for (int i = 0; i < range.size(); i++) {
      assertEquals(MeteoraPDAs.binArrayPdA(LB_PAIR, lo + i, PROGRAM).publicKey(),
          range.get(i).publicKey(), "bin array at index " + (lo + i));
      assertTrue(range.get(i).write());
      assertFalse(range.get(i).signer());
    }

    // appendBinAccounts appends the same metas after the instruction's own
    final var appended = MeteoraDlmmRemainingAccounts.appendBinAccounts(
        ix(), PROGRAM, LB_PAIR, lowerBin, upperBin);
    final var accounts = appended.accounts();
    assertEquals(1 + range.size(), accounts.size());
    for (int i = 0; i < range.size(); i++) {
      assertEquals(range.get(i).publicKey(), accounts.get(1 + i).publicKey());
    }
  }

  @Test
  void transferHookSlicesPairWithTheirMetasInOrder() {
    final var hookX = key(0x21);
    final var pdaX = key(0x22);
    final var extraX = key(0x23);
    final var hookY = key(0x24);
    final var pdaY = key(0x25);
    final var hookR = key(0x26);
    final var pdaR = key(0x27);
    final var hookF = key(0x28);
    final var pdaF = key(0x29);
    final var hookM = key(0x2A);
    final var pdaM = key(0x2B);

    // each add returns the builder itself — the documented chaining contract
    final var extras = MeteoraDlmmRemainingAccounts.transferHooks();
    assertSame(extras, extras.addTransferHookX(hookX, pdaX, List.of(AccountMeta.createWrite(extraX))));
    assertSame(extras, extras.addTransferHookY(hookY, pdaY, null));
    assertSame(extras, extras.addTransferHookReward(hookR, pdaR, List.of()));
    assertSame(extras, extras.addTransferHookReferral(hookF, pdaF, null));
    assertSame(extras, extras.addTransferHookMultiReward(3, hookM, pdaM, null));

    final var slices = extras.info().slices();
    assertEquals(5, slices.length);
    assertEquals(AccountsType.TransferHookX.INSTANCE, slices[0].accountsType());
    assertEquals(3, slices[0].length(), "hook program + PDA + one resolved extra");
    assertEquals(AccountsType.TransferHookY.INSTANCE, slices[1].accountsType());
    assertEquals(2, slices[1].length());
    assertEquals(AccountsType.TransferHookReward.INSTANCE, slices[2].accountsType());
    assertEquals(2, slices[2].length());
    assertEquals(AccountsType.TransferHookReferral.INSTANCE, slices[3].accountsType());
    assertEquals(2, slices[3].length());
    assertEquals(new AccountsType.TransferHookMultiReward(3), slices[4].accountsType());
    assertEquals(2, slices[4].length());

    // the meta tail matches the slice walk: per slice, hook program then
    // extra-metas PDA (both read-only) then any resolved extras
    final var appended = extras.append(ix());
    final var accounts = appended.accounts();
    final var expectedTail = List.of(
        hookX, pdaX, extraX,
        hookY, pdaY,
        hookR, pdaR,
        hookF, pdaF,
        hookM, pdaM);
    assertEquals(1 + expectedTail.size(), accounts.size());
    for (int i = 0; i < expectedTail.size(); i++) {
      final var meta = accounts.get(1 + i);
      assertEquals(expectedTail.get(i), meta.publicKey(), "slot " + i);
      assertFalse(meta.signer());
    }
    // hook programs and PDAs are read-only; the resolved extra keeps its flag
    assertFalse(accounts.get(1).write());
    assertFalse(accounts.get(2).write());
    assertTrue(accounts.get(3).write(), "the resolved extra stays writable");

    // an empty builder appends nothing and returns the instruction unchanged
    final var untouched = ix();
    assertSame(untouched, MeteoraDlmmRemainingAccounts.transferHooks().append(untouched));
  }

  /// A slice length is a u8 on the wire: `2 + extras` up to 255 is accepted,
  /// 256 must be rejected up front — on chain the count would silently
  /// truncate and the `for_idx` walker would desynchronize from the metas.
  @Test
  void sliceLengthIsBoundedByU8() {
    final var filler = AccountMeta.createRead(key(0x31));

    final var atLimit = MeteoraDlmmRemainingAccounts.transferHooks();
    atLimit.addTransferHookX(key(0x32), key(0x33), java.util.Collections.nCopies(253, filler));
    assertEquals(255, atLimit.info().slices()[0].length());

    final var overLimit = MeteoraDlmmRemainingAccounts.transferHooks();
    final var rejected = assertThrows(IllegalArgumentException.class, () ->
        overLimit.addTransferHookX(key(0x32), key(0x33), java.util.Collections.nCopies(254, filler)));
    assertTrue(rejected.getMessage().contains("256"), rejected.getMessage());
  }
}
