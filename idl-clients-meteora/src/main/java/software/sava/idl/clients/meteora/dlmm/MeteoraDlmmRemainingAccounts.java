package software.sava.idl.clients.meteora.dlmm;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.meteora.MeteoraPDAs;
import software.sava.idl.clients.meteora.dlmm.gen.types.AccountsType;
import software.sava.idl.clients.meteora.dlmm.gen.types.RemainingAccountsInfo;
import software.sava.idl.clients.meteora.dlmm.gen.types.RemainingAccountsSlice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/// Helpers for appending the non-IDL `remaining_accounts` payload that several
/// `LbClmmProgram` instructions require:
///
/// 1. **Bin arrays** — every add/remove/swap/limit-order V2 ixn reads a sequence of
///    bin-array accounts from `remaining_accounts`. They are **not** described by
///    `RemainingAccountsInfo`; the program infers them from the metas-after-info
///    length. For LP paths the bin arrays cover the position's `[lowerBinId, upperBinId]`
///    range; for swaps they cover the worst-case active-bin walk (caller-estimated).
///    All bin-array entries must be **writable**.
///
/// 2. **Token-2022 transfer-hook accounts** — when either token mint has the
///    `TransferHook` extension enabled, each mint contributes:
///    - the hook program id,
///    - the per-mint `extra-account-metas` PDA
///      (seeds: `["extra-account-metas", mint]`, owned by the hook program), and
///    - every account listed by that PDA (resolved against any seed-based metas).
///
///    Transfer-hook slices are described by `RemainingAccountsInfo` and must
///    appear **after** any bin-array accounts in the on-chain `remaining_accounts`
///    order. The `RemainingAccountsInfo` arg passed to the builder describes only
///    the transfer-hook tail; the bin arrays are implicit.
///
/// Writability / signer flags follow the upstream Rust handler:
/// - bin arrays — writable, no signer;
/// - transfer-hook program id — read-only;
/// - extra-account-metas PDA — read-only;
/// - hook program extras — read-only by default (flip writable only when the
///   hook program's `ExtraAccountMetaList` explicitly marks them writable).
public final class MeteoraDlmmRemainingAccounts {

  private MeteoraDlmmRemainingAccounts() {
  }

  /// Append the writable bin-array metas covering `[lowerBinId, upperBinId]` to
  /// `instruction`. Bin arrays must always come **first** in `remaining_accounts`.
  public static Instruction appendBinAccounts(final Instruction instruction,
                                              final PublicKey programId,
                                              final PublicKey lbPairKey,
                                              final int lowerBinId,
                                              final int upperBinId) {
    return instruction.extraAccounts(deriveBinAccounts(programId, lbPairKey, lowerBinId, upperBinId));
  }

  /// Bin-array meta range as `AccountMeta` list (writable). Use this when the caller
  /// wants to combine bin metas with other extras before `extraAccounts(...)`.
  public static List<AccountMeta> deriveBinAccounts(final PublicKey programId,
                                                    final PublicKey lbPairKey,
                                                    final int lowerBinId,
                                                    final int upperBinId) {
    final int lowerBinIndex = DlmmUtils.binIdToArrayIndex(lowerBinId);
    final int upperBinIndex = DlmmUtils.binIdToArrayIndex(upperBinId);
    if (lowerBinIndex == upperBinIndex) {
      return List.of(AccountMeta.createWrite(
          MeteoraPDAs.binArrayPdA(lbPairKey, lowerBinIndex, programId).publicKey()
      ));
    }
    return IntStream.rangeClosed(lowerBinIndex, upperBinIndex)
        .mapToObj(idx -> MeteoraPDAs.binArrayPdA(lbPairKey, idx, programId).publicKey())
        .map(AccountMeta.CREATE_WRITE)
        .toList();
  }

  /// Builder for the transfer-hook tail. Each `add*` call pairs a `RemainingAccountsSlice`
  /// (kind + length) with the matching ordered list of `AccountMeta`s so the on-chain
  /// `for_idx` walker sees the same counts the arg encodes.
  ///
  /// Build flow:
  /// ```
  /// var b = MeteoraDlmmRemainingAccounts.transferHooks();
  /// b.addTransferHookX(hookProgramId, extraMetasPda, resolvedExtras);
  /// b.addTransferHookY(hookProgramId, extraMetasPda, resolvedExtras);
  /// var ix = LbClmmProgram.swap2(..., b.info());
  /// ix = MeteoraDlmmRemainingAccounts.appendBinAccounts(ix, programId, lbPair, lo, hi);
  /// ix = b.append(ix);
  /// ```
  public static TransferHookExtras transferHooks() {
    return new TransferHookExtras();
  }

  public static final class TransferHookExtras {

    private final List<RemainingAccountsSlice> slices = new ArrayList<>();
    private final List<AccountMeta> metas = new ArrayList<>();

    private TransferHookExtras() {
    }

    public TransferHookExtras addTransferHookX(final PublicKey hookProgramId,
                                               final PublicKey extraAccountMetasPda,
                                               final List<AccountMeta> resolvedExtras) {
      return add(AccountsType.TransferHookX.INSTANCE, hookProgramId, extraAccountMetasPda, resolvedExtras);
    }

    public TransferHookExtras addTransferHookY(final PublicKey hookProgramId,
                                               final PublicKey extraAccountMetasPda,
                                               final List<AccountMeta> resolvedExtras) {
      return add(AccountsType.TransferHookY.INSTANCE, hookProgramId, extraAccountMetasPda, resolvedExtras);
    }

    public TransferHookExtras addTransferHookReward(final PublicKey hookProgramId,
                                                    final PublicKey extraAccountMetasPda,
                                                    final List<AccountMeta> resolvedExtras) {
      return add(AccountsType.TransferHookReward.INSTANCE, hookProgramId, extraAccountMetasPda, resolvedExtras);
    }

    public TransferHookExtras addTransferHookReferral(final PublicKey hookProgramId,
                                                      final PublicKey extraAccountMetasPda,
                                                      final List<AccountMeta> resolvedExtras) {
      return add(AccountsType.TransferHookReferral.INSTANCE, hookProgramId, extraAccountMetasPda, resolvedExtras);
    }

    /// `rewardIndex` identifies which reward slot the transfer-hook applies to (0..n).
    public TransferHookExtras addTransferHookMultiReward(final int rewardIndex,
                                                        final PublicKey hookProgramId,
                                                        final PublicKey extraAccountMetasPda,
                                                        final List<AccountMeta> resolvedExtras) {
      return add(new AccountsType.TransferHookMultiReward(rewardIndex), hookProgramId, extraAccountMetasPda, resolvedExtras);
    }

    private TransferHookExtras add(final AccountsType kind,
                                   final PublicKey hookProgramId,
                                   final PublicKey extraAccountMetasPda,
                                   final List<AccountMeta> resolvedExtras) {
      final int len = 2 + (resolvedExtras == null ? 0 : resolvedExtras.size());
      if (len > 0xFF) {
        throw new IllegalArgumentException("transfer-hook slice length exceeds u8: " + len);
      }
      slices.add(new RemainingAccountsSlice(kind, len));
      metas.add(AccountMeta.createRead(hookProgramId));
      metas.add(AccountMeta.createRead(extraAccountMetasPda));
      if (resolvedExtras != null) {
        metas.addAll(resolvedExtras);
      }
      return this;
    }

    /// `RemainingAccountsInfo` payload to pass to the builder.
    public RemainingAccountsInfo info() {
      return new RemainingAccountsInfo(slices.toArray(RemainingAccountsSlice[]::new));
    }

    /// Append the accumulated transfer-hook metas to `instruction`. Must be called
    /// **after** any bin-array metas have been appended.
    public Instruction append(final Instruction instruction) {
      return metas.isEmpty() ? instruction : instruction.extraAccounts(metas);
    }
  }
}
