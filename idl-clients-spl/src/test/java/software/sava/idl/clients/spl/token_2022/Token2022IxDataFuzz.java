package software.sava.idl.clients.spl.token_2022;

import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDe;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static software.sava.idl.clients.spl.token_2022.gen.Token2022Program.*;

/// Jazzer entry point for every generated Token-2022 instruction-data reader — all 99 of
/// them, in the order `Token2022Program` declares them. The first fuzzer byte picks one
/// (its value modulo 99) and everything after it is the payload, so one campaign walks the
/// whole dispatch table rather than a single instruction. This is where hostile bytes
/// actually arrive: a wallet or indexer decodes instruction data written by whoever built
/// the transaction.
///
/// Malformed-input contract: garbage in -> [RuntimeException] out. Truncation, an option
/// tag that is neither 0 nor 1, and a length prefix the buffer cannot back all surface as
/// runtime exceptions and are rejections; any other throwable is a finding.
///
/// The one rejection a reader does not raise is an unknown enum ordinal. Since the
/// 2026-08-15 change to `SerDeUtil.read`, an ordinal naming no variant decodes as null
/// instead of throwing — Token-2022 grows its `ExtensionType` list, so a client generated
/// before the program is the ordinary case, and it is the caller who decides whether a
/// variant it does not recognise matters. A record holding that sentinel still cannot be
/// serialized, because `write` would dereference it, so the harness walks the parsed
/// record for null enum-typed components and treats one as a rejection — the same move
/// `RouteV2DataFuzz` makes in the bundle. A null that is an absent `Option` is not one.
///
/// On a successful parse the harness asserts:
///
/// - `l()` equals the number of bytes `write` reports having written;
/// - `l()` never exceeds the payload the record was read from — a record claiming more
///   bytes than it was given has decoded fields nobody sent;
/// - re-reading the bytes `write` produced yields an equal record. Equality is
///   component-wise and array-aware, because the generated records take the default record
///   `equals`, which compares their `byte[]` components by reference: a plain `equals`
///   would report every metadata or confidential-balance record unequal to itself;
/// - `read(Instruction)` yields the same record as `read(payload, 0)` over those bytes.
///
/// Seeded with the real devnet payloads the Token-2022 tests carry; the corpus README
/// beside `src/test/resources/fuzz` says which instruction each seed is.
///
/// Deliberately free of Jazzer imports so it compiles with the regular test sources.
///
/// Run with `./gradlew :idl-clients-spl:fuzzToken2022IxData [-PmaxFuzzTime=<seconds>]`.
public final class Token2022IxDataFuzz {

  @FunctionalInterface
  private interface FromBytes {

    SerDe read(byte[] data, int offset);
  }

  @FunctionalInterface
  private interface FromInstruction {

    SerDe read(Instruction instruction);
  }

  private record Reader(FromBytes fromBytes, FromInstruction fromInstruction) {
  }

  private static Reader reader(final FromBytes fromBytes, final FromInstruction fromInstruction) {
    return new Reader(fromBytes, fromInstruction);
  }

  /// Declaration order in `Token2022Program`, which is what a seed's first byte indexes.
  private static final Reader[] READERS = {
      reader(InitializeMintIxData::read, InitializeMintIxData::read),
      reader(InitializeAccountIxData::read, InitializeAccountIxData::read),
      reader(InitializeMultisigIxData::read, InitializeMultisigIxData::read),
      reader(TransferIxData::read, TransferIxData::read),
      reader(ApproveIxData::read, ApproveIxData::read),
      reader(RevokeIxData::read, RevokeIxData::read),
      reader(SetAuthorityIxData::read, SetAuthorityIxData::read),
      reader(MintToIxData::read, MintToIxData::read),
      reader(BurnIxData::read, BurnIxData::read),
      reader(CloseAccountIxData::read, CloseAccountIxData::read),
      reader(FreezeAccountIxData::read, FreezeAccountIxData::read),
      reader(ThawAccountIxData::read, ThawAccountIxData::read),
      reader(TransferCheckedIxData::read, TransferCheckedIxData::read),
      reader(ApproveCheckedIxData::read, ApproveCheckedIxData::read),
      reader(MintToCheckedIxData::read, MintToCheckedIxData::read),
      reader(BurnCheckedIxData::read, BurnCheckedIxData::read),
      reader(InitializeAccount2IxData::read, InitializeAccount2IxData::read),
      reader(SyncNativeIxData::read, SyncNativeIxData::read),
      reader(InitializeAccount3IxData::read, InitializeAccount3IxData::read),
      reader(InitializeMultisig2IxData::read, InitializeMultisig2IxData::read),
      reader(InitializeMint2IxData::read, InitializeMint2IxData::read),
      reader(GetAccountDataSizeIxData::read, GetAccountDataSizeIxData::read),
      reader(InitializeImmutableOwnerIxData::read, InitializeImmutableOwnerIxData::read),
      reader(AmountToUiAmountIxData::read, AmountToUiAmountIxData::read),
      reader(UiAmountToAmountIxData::read, UiAmountToAmountIxData::read),
      reader(InitializeMintCloseAuthorityIxData::read, InitializeMintCloseAuthorityIxData::read),
      reader(InitializeTransferFeeConfigIxData::read, InitializeTransferFeeConfigIxData::read),
      reader(TransferCheckedWithFeeIxData::read, TransferCheckedWithFeeIxData::read),
      reader(WithdrawWithheldTokensFromMintIxData::read, WithdrawWithheldTokensFromMintIxData::read),
      reader(WithdrawWithheldTokensFromAccountsIxData::read, WithdrawWithheldTokensFromAccountsIxData::read),
      reader(HarvestWithheldTokensToMintIxData::read, HarvestWithheldTokensToMintIxData::read),
      reader(SetTransferFeeIxData::read, SetTransferFeeIxData::read),
      reader(InitializeConfidentialTransferMintIxData::read, InitializeConfidentialTransferMintIxData::read),
      reader(UpdateConfidentialTransferMintIxData::read, UpdateConfidentialTransferMintIxData::read),
      reader(ConfigureConfidentialTransferAccountIxData::read, ConfigureConfidentialTransferAccountIxData::read),
      reader(ApproveConfidentialTransferAccountIxData::read, ApproveConfidentialTransferAccountIxData::read),
      reader(EmptyConfidentialTransferAccountIxData::read, EmptyConfidentialTransferAccountIxData::read),
      reader(ConfidentialDepositIxData::read, ConfidentialDepositIxData::read),
      reader(ConfidentialWithdrawIxData::read, ConfidentialWithdrawIxData::read),
      reader(ConfidentialTransferIxData::read, ConfidentialTransferIxData::read),
      reader(ApplyConfidentialPendingBalanceIxData::read, ApplyConfidentialPendingBalanceIxData::read),
      reader(EnableConfidentialCreditsIxData::read, EnableConfidentialCreditsIxData::read),
      reader(DisableConfidentialCreditsIxData::read, DisableConfidentialCreditsIxData::read),
      reader(EnableNonConfidentialCreditsIxData::read, EnableNonConfidentialCreditsIxData::read),
      reader(DisableNonConfidentialCreditsIxData::read, DisableNonConfidentialCreditsIxData::read),
      reader(ConfidentialTransferWithFeeIxData::read, ConfidentialTransferWithFeeIxData::read),
      reader(ConfigureConfidentialTransferAccountWithRegistryIxData::read,
             ConfigureConfidentialTransferAccountWithRegistryIxData::read),
      reader(InitializeDefaultAccountStateIxData::read, InitializeDefaultAccountStateIxData::read),
      reader(UpdateDefaultAccountStateIxData::read, UpdateDefaultAccountStateIxData::read),
      reader(ReallocateIxData::read, ReallocateIxData::read),
      reader(EnableMemoTransfersIxData::read, EnableMemoTransfersIxData::read),
      reader(DisableMemoTransfersIxData::read, DisableMemoTransfersIxData::read),
      reader(CreateNativeMintIxData::read, CreateNativeMintIxData::read),
      reader(InitializeNonTransferableMintIxData::read, InitializeNonTransferableMintIxData::read),
      reader(InitializeInterestBearingMintIxData::read, InitializeInterestBearingMintIxData::read),
      reader(UpdateRateInterestBearingMintIxData::read, UpdateRateInterestBearingMintIxData::read),
      reader(EnableCpiGuardIxData::read, EnableCpiGuardIxData::read),
      reader(DisableCpiGuardIxData::read, DisableCpiGuardIxData::read),
      reader(InitializePermanentDelegateIxData::read, InitializePermanentDelegateIxData::read),
      reader(InitializeTransferHookIxData::read, InitializeTransferHookIxData::read),
      reader(UpdateTransferHookIxData::read, UpdateTransferHookIxData::read),
      reader(InitializeConfidentialTransferFeeIxData::read, InitializeConfidentialTransferFeeIxData::read),
      reader(WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData::read,
             WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData::read),
      reader(WithdrawWithheldTokensFromAccountsForConfidentialTransferFeeIxData::read,
             WithdrawWithheldTokensFromAccountsForConfidentialTransferFeeIxData::read),
      reader(HarvestWithheldTokensToMintForConfidentialTransferFeeIxData::read,
             HarvestWithheldTokensToMintForConfidentialTransferFeeIxData::read),
      reader(EnableHarvestToMintIxData::read, EnableHarvestToMintIxData::read),
      reader(DisableHarvestToMintIxData::read, DisableHarvestToMintIxData::read),
      reader(WithdrawExcessLamportsIxData::read, WithdrawExcessLamportsIxData::read),
      reader(InitializeMetadataPointerIxData::read, InitializeMetadataPointerIxData::read),
      reader(UpdateMetadataPointerIxData::read, UpdateMetadataPointerIxData::read),
      reader(InitializeGroupPointerIxData::read, InitializeGroupPointerIxData::read),
      reader(UpdateGroupPointerIxData::read, UpdateGroupPointerIxData::read),
      reader(InitializeGroupMemberPointerIxData::read, InitializeGroupMemberPointerIxData::read),
      reader(UpdateGroupMemberPointerIxData::read, UpdateGroupMemberPointerIxData::read),
      reader(InitializeConfidentialMintBurnIxData::read, InitializeConfidentialMintBurnIxData::read),
      reader(RotateSupplyElgamalPubkeyIxData::read, RotateSupplyElgamalPubkeyIxData::read),
      reader(UpdateConfidentialMintBurnDecryptableSupplyIxData::read,
             UpdateConfidentialMintBurnDecryptableSupplyIxData::read),
      reader(ConfidentialMintIxData::read, ConfidentialMintIxData::read),
      reader(ConfidentialBurnIxData::read, ConfidentialBurnIxData::read),
      reader(ApplyConfidentialPendingBurnIxData::read, ApplyConfidentialPendingBurnIxData::read),
      reader(InitializeScaledUiAmountMintIxData::read, InitializeScaledUiAmountMintIxData::read),
      reader(UpdateMultiplierScaledUiMintIxData::read, UpdateMultiplierScaledUiMintIxData::read),
      reader(InitializePausableConfigIxData::read, InitializePausableConfigIxData::read),
      reader(PauseIxData::read, PauseIxData::read),
      reader(ResumeIxData::read, ResumeIxData::read),
      reader(InitializeTokenMetadataIxData::read, InitializeTokenMetadataIxData::read),
      reader(UpdateTokenMetadataFieldIxData::read, UpdateTokenMetadataFieldIxData::read),
      reader(RemoveTokenMetadataKeyIxData::read, RemoveTokenMetadataKeyIxData::read),
      reader(UpdateTokenMetadataUpdateAuthorityIxData::read, UpdateTokenMetadataUpdateAuthorityIxData::read),
      reader(EmitTokenMetadataIxData::read, EmitTokenMetadataIxData::read),
      reader(InitializeTokenGroupIxData::read, InitializeTokenGroupIxData::read),
      reader(UpdateTokenGroupMaxSizeIxData::read, UpdateTokenGroupMaxSizeIxData::read),
      reader(UpdateTokenGroupUpdateAuthorityIxData::read, UpdateTokenGroupUpdateAuthorityIxData::read),
      reader(InitializeTokenGroupMemberIxData::read, InitializeTokenGroupMemberIxData::read),
      reader(UnwrapLamportsIxData::read, UnwrapLamportsIxData::read),
      reader(InitializePermissionedBurnIxData::read, InitializePermissionedBurnIxData::read),
      reader(PermissionedBurnIxData::read, PermissionedBurnIxData::read),
      reader(PermissionedBurnCheckedIxData::read, PermissionedBurnCheckedIxData::read),
      reader(PermissionedConfidentialBurnIxData::read, PermissionedConfidentialBurnIxData::read),
  };

  private static final AccountMeta INVOKED_PROGRAM = SolanaAccounts.MAIN_NET.invokedToken2022Program();
  private static final List<AccountMeta> NO_KEYS = List.of();

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length < 2) {
      // one selector byte and at least one payload byte; an empty payload reads as null
      return;
    }
    final int selector = (data[0] & 0xFF) % READERS.length;
    final var reader = READERS[selector];
    final byte[] payload = Arrays.copyOfRange(data, 1, data.length);

    final SerDe parsed;
    try {
      parsed = reader.fromBytes().read(payload, 0);
    } catch (final RuntimeException rejected) {
      // garbage in -> RuntimeException out is the documented contract
      return;
    }
    if (parsed == null) {
      // read() answers null for null or empty data, which the length guard above excludes
      throw new IllegalStateException("reader " + selector + " returned null for " + payload.length + " bytes");
    }
    if (hasUnknownVariant(parsed, parsed.getClass())) {
      // an ordinal naming no variant reads as null; the record cannot be written, so this
      // input is a rejection rather than a parse
      return;
    }

    final String name = parsed.getClass().getSimpleName();
    final int length = parsed.l();
    if (length > payload.length) {
      throw new IllegalStateException(String.format(
          "%s claims %d bytes after reading a %d byte payload", name, length, payload.length
      ));
    }
    final byte[] canonical = new byte[length];
    final int wrote = parsed.write(canonical, 0);
    if (wrote != length) {
      throw new IllegalStateException(String.format(
          "%s wrote %d bytes but l() is %d", name, wrote, length
      ));
    }

    final var reRead = reader.fromBytes().read(canonical, 0);
    if (!sameValue(parsed, reRead)) {
      throw new IllegalStateException(name + " does not round trip through write then read");
    }

    // the Instruction overload is the same parse bounded by the instruction rather than by
    // the buffer it sits in, so it must land on the same record for a standalone payload
    final var instruction = Instruction.createInstruction(INVOKED_PROGRAM, NO_KEYS, payload, 0, payload.length);
    final SerDe fromInstruction;
    try {
      fromInstruction = reader.fromInstruction().read(instruction);
    } catch (final RuntimeException rejectedOnlyHere) {
      throw new IllegalStateException(name + " read(Instruction) rejected what read(data, 0) accepted",
                                      rejectedOnlyHere);
    }
    if (!sameValue(parsed, fromInstruction)) {
      throw new IllegalStateException(name + " read(Instruction) disagrees with read(data, 0)");
    }
  }

  private static final RecordComponent[] NO_COMPONENTS = new RecordComponent[0];

  private static final ClassValue<RecordComponent[]> RECORD_COMPONENTS = new ClassValue<>() {
    @Override
    protected RecordComponent[] computeValue(final Class<?> type) {
      return type.isRecord() ? type.getRecordComponents() : NO_COMPONENTS;
    }
  };

  /// Walks the acyclic generated record graph for a null in an enum-typed slot, which is
  /// how `SerDeUtil` reports an ordinal that names no variant. Only enum-typed slots
  /// count: an absent `Option` is null too and is a perfectly serializable value.
  /// Reflection failures are harness failures, not malformed input, so they escape.
  private static boolean hasUnknownVariant(final Object value, final Class<?> declaredType) {
    if (declaredType.isArray()) {
      if (value == null) {
        return false;
      }
      final var elementType = declaredType.getComponentType();
      if (elementType.isPrimitive()) {
        return false;
      }
      for (final var element : (Object[]) value) {
        if (hasUnknownVariant(element, elementType)) {
          return true;
        }
      }
      return false;
    }
    if (value == null) {
      return Enum.class.isAssignableFrom(declaredType) || RustEnum.class.isAssignableFrom(declaredType);
    }
    for (final var component : RECORD_COMPONENTS.get(value.getClass())) {
      if (hasUnknownVariant(componentValue(component, value), component.getType())) {
        return true;
      }
    }
    return false;
  }

  /// Record equality the generated records cannot express themselves: the default record
  /// `equals` compares a `byte[]` component by reference, so two records read from the
  /// same bytes are unequal whenever one carries an array. Compares component-wise
  /// instead, with array-aware equality at every level.
  private static boolean sameValue(final Object left, final Object right) {
    if (left == right) {
      return true;
    }
    if (left == null || right == null || left.getClass() != right.getClass()) {
      return false;
    }
    final var type = left.getClass();
    if (type.isArray()) {
      if (type.getComponentType().isPrimitive()) {
        return Objects.deepEquals(left, right);
      }
      final var leftElements = (Object[]) left;
      final var rightElements = (Object[]) right;
      if (leftElements.length != rightElements.length) {
        return false;
      }
      for (int i = 0; i < leftElements.length; ++i) {
        if (!sameValue(leftElements[i], rightElements[i])) {
          return false;
        }
      }
      return true;
    }
    final var components = RECORD_COMPONENTS.get(type);
    if (components.length == 0) {
      // enums, strings, public keys and no-component enum variants compare themselves
      return left.equals(right);
    }
    for (final var component : components) {
      if (!sameValue(componentValue(component, left), componentValue(component, right))) {
        return false;
      }
    }
    return true;
  }

  private static Object componentValue(final RecordComponent component, final Object record) {
    try {
      return component.getAccessor().invoke(record);
    } catch (final ReflectiveOperationException reflectionFailure) {
      throw new AssertionError("Could not inspect " + component, reflectionFailure);
    }
  }

  private Token2022IxDataFuzz() {
  }
}
