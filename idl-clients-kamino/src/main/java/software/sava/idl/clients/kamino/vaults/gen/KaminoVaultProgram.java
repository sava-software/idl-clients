package software.sava.idl.clients.kamino.vaults.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.lend.gen.types.UpdateGlobalConfigMode;
import software.sava.idl.clients.kamino.vaults.gen.types.UpdateReserveWhitelistMode;
import software.sava.idl.clients.kamino.vaults.gen.types.VaultConfigField;

import static java.nio.charset.StandardCharsets.UTF_8;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class KaminoVaultProgram {

  public static final Discriminator INIT_VAULT_DISCRIMINATOR = toDiscriminator(77, 79, 85, 150, 33, 217, 52, 106);

  public static List<AccountMeta> initVaultKeys(final PublicKey adminAuthorityKey,
                                                final PublicKey vaultStateKey,
                                                final PublicKey baseVaultAuthorityKey,
                                                final PublicKey tokenVaultKey,
                                                final PublicKey baseTokenMintKey,
                                                final PublicKey sharesMintKey,
                                                final PublicKey adminTokenAccountKey,
                                                final PublicKey systemProgramKey,
                                                final PublicKey rentKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey sharesTokenProgramKey) {
    return List.of(
      createWritableSigner(adminAuthorityKey),
      createWrite(vaultStateKey),
      createRead(baseVaultAuthorityKey),
      createWrite(tokenVaultKey),
      createRead(baseTokenMintKey),
      createWrite(sharesMintKey),
      createWrite(adminTokenAccountKey),
      createRead(systemProgramKey),
      createRead(rentKey),
      createRead(tokenProgramKey),
      createRead(sharesTokenProgramKey)
    );
  }

  public static Instruction initVault(final AccountMeta invokedKaminoVaultProgramMeta,
                                      final PublicKey adminAuthorityKey,
                                      final PublicKey vaultStateKey,
                                      final PublicKey baseVaultAuthorityKey,
                                      final PublicKey tokenVaultKey,
                                      final PublicKey baseTokenMintKey,
                                      final PublicKey sharesMintKey,
                                      final PublicKey adminTokenAccountKey,
                                      final PublicKey systemProgramKey,
                                      final PublicKey rentKey,
                                      final PublicKey tokenProgramKey,
                                      final PublicKey sharesTokenProgramKey) {
    final var keys = initVaultKeys(
      adminAuthorityKey,
      vaultStateKey,
      baseVaultAuthorityKey,
      tokenVaultKey,
      baseTokenMintKey,
      sharesMintKey,
      adminTokenAccountKey,
      systemProgramKey,
      rentKey,
      tokenProgramKey,
      sharesTokenProgramKey
    );
    return initVault(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction initVault(final AccountMeta invokedKaminoVaultProgramMeta,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, INIT_VAULT_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_RESERVE_ALLOCATION_DISCRIMINATOR = toDiscriminator(5, 54, 213, 112, 75, 232, 117, 37);

  public static List<AccountMeta> updateReserveAllocationKeys(final AccountMeta invokedKaminoVaultProgramMeta,
                                                              final PublicKey signerKey,
                                                              final PublicKey vaultStateKey,
                                                              final PublicKey baseVaultAuthorityKey,
                                                              final PublicKey reserveCollateralMintKey,
                                                              final PublicKey reserveKey,
                                                              final PublicKey ctokenVaultKey,
                                                              final PublicKey reserveWhitelistEntryKey,
                                                              final PublicKey reserveCollateralTokenProgramKey,
                                                              final PublicKey systemProgramKey,
                                                              final PublicKey rentKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(vaultStateKey),
      createRead(baseVaultAuthorityKey),
      createWrite(reserveCollateralMintKey),
      createRead(reserveKey),
      createWrite(ctokenVaultKey),
      createRead(requireNonNullElse(reserveWhitelistEntryKey, invokedKaminoVaultProgramMeta.publicKey())),
      createRead(reserveCollateralTokenProgramKey),
      createRead(systemProgramKey),
      createRead(rentKey)
    );
  }

  public static Instruction updateReserveAllocation(final AccountMeta invokedKaminoVaultProgramMeta,
                                                    final PublicKey signerKey,
                                                    final PublicKey vaultStateKey,
                                                    final PublicKey baseVaultAuthorityKey,
                                                    final PublicKey reserveCollateralMintKey,
                                                    final PublicKey reserveKey,
                                                    final PublicKey ctokenVaultKey,
                                                    final PublicKey reserveWhitelistEntryKey,
                                                    final PublicKey reserveCollateralTokenProgramKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey rentKey,
                                                    final long weight,
                                                    final long cap) {
    final var keys = updateReserveAllocationKeys(
      invokedKaminoVaultProgramMeta,
      signerKey,
      vaultStateKey,
      baseVaultAuthorityKey,
      reserveCollateralMintKey,
      reserveKey,
      ctokenVaultKey,
      reserveWhitelistEntryKey,
      reserveCollateralTokenProgramKey,
      systemProgramKey,
      rentKey
    );
    return updateReserveAllocation(invokedKaminoVaultProgramMeta, keys, weight, cap);
  }

  public static Instruction updateReserveAllocation(final AccountMeta invokedKaminoVaultProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final long weight,
                                                    final long cap) {
    final byte[] _data = new byte[24];
    int i = UPDATE_RESERVE_ALLOCATION_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, weight);
    i += 8;
    putInt64LE(_data, i, cap);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record UpdateReserveAllocationIxData(Discriminator discriminator, long weight, long cap) implements Borsh {  

    public static UpdateReserveAllocationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdateReserveAllocationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var weight = getInt64LE(_data, i);
      i += 8;
      final var cap = getInt64LE(_data, i);
      return new UpdateReserveAllocationIxData(discriminator, weight, cap);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, weight);
      i += 8;
      putInt64LE(_data, i, cap);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_DISCRIMINATOR = toDiscriminator(242, 35, 198, 137, 82, 225, 242, 182);

  public static List<AccountMeta> depositKeys(final PublicKey userKey,
                                              final PublicKey vaultStateKey,
                                              final PublicKey tokenVaultKey,
                                              final PublicKey tokenMintKey,
                                              final PublicKey baseVaultAuthorityKey,
                                              final PublicKey sharesMintKey,
                                              final PublicKey userTokenAtaKey,
                                              final PublicKey userSharesAtaKey,
                                              final PublicKey klendProgramKey,
                                              final PublicKey tokenProgramKey,
                                              final PublicKey sharesTokenProgramKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey) {
    return List.of(
      createWritableSigner(userKey),
      createWrite(vaultStateKey),
      createWrite(tokenVaultKey),
      createRead(tokenMintKey),
      createRead(baseVaultAuthorityKey),
      createWrite(sharesMintKey),
      createWrite(userTokenAtaKey),
      createWrite(userSharesAtaKey),
      createRead(klendProgramKey),
      createRead(tokenProgramKey),
      createRead(sharesTokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction deposit(final AccountMeta invokedKaminoVaultProgramMeta,
                                    final PublicKey userKey,
                                    final PublicKey vaultStateKey,
                                    final PublicKey tokenVaultKey,
                                    final PublicKey tokenMintKey,
                                    final PublicKey baseVaultAuthorityKey,
                                    final PublicKey sharesMintKey,
                                    final PublicKey userTokenAtaKey,
                                    final PublicKey userSharesAtaKey,
                                    final PublicKey klendProgramKey,
                                    final PublicKey tokenProgramKey,
                                    final PublicKey sharesTokenProgramKey,
                                    final PublicKey eventAuthorityKey,
                                    final PublicKey programKey,
                                    final long maxAmount) {
    final var keys = depositKeys(
      userKey,
      vaultStateKey,
      tokenVaultKey,
      tokenMintKey,
      baseVaultAuthorityKey,
      sharesMintKey,
      userTokenAtaKey,
      userSharesAtaKey,
      klendProgramKey,
      tokenProgramKey,
      sharesTokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return deposit(invokedKaminoVaultProgramMeta, keys, maxAmount);
  }

  public static Instruction deposit(final AccountMeta invokedKaminoVaultProgramMeta,
                                    final List<AccountMeta> keys,
                                    final long maxAmount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxAmount);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record DepositIxData(Discriminator discriminator, long maxAmount) implements Borsh {  

    public static DepositIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxAmount = getInt64LE(_data, i);
      return new DepositIxData(discriminator, maxAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BUY_DISCRIMINATOR = toDiscriminator(102, 6, 61, 18, 1, 218, 235, 234);

  public static List<AccountMeta> buyKeys(final PublicKey userKey,
                                          final PublicKey vaultStateKey,
                                          final PublicKey tokenVaultKey,
                                          final PublicKey tokenMintKey,
                                          final PublicKey baseVaultAuthorityKey,
                                          final PublicKey sharesMintKey,
                                          final PublicKey userTokenAtaKey,
                                          final PublicKey userSharesAtaKey,
                                          final PublicKey klendProgramKey,
                                          final PublicKey tokenProgramKey,
                                          final PublicKey sharesTokenProgramKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey) {
    return List.of(
      createWritableSigner(userKey),
      createWrite(vaultStateKey),
      createWrite(tokenVaultKey),
      createRead(tokenMintKey),
      createRead(baseVaultAuthorityKey),
      createWrite(sharesMintKey),
      createWrite(userTokenAtaKey),
      createWrite(userSharesAtaKey),
      createRead(klendProgramKey),
      createRead(tokenProgramKey),
      createRead(sharesTokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction buy(final AccountMeta invokedKaminoVaultProgramMeta,
                                final PublicKey userKey,
                                final PublicKey vaultStateKey,
                                final PublicKey tokenVaultKey,
                                final PublicKey tokenMintKey,
                                final PublicKey baseVaultAuthorityKey,
                                final PublicKey sharesMintKey,
                                final PublicKey userTokenAtaKey,
                                final PublicKey userSharesAtaKey,
                                final PublicKey klendProgramKey,
                                final PublicKey tokenProgramKey,
                                final PublicKey sharesTokenProgramKey,
                                final PublicKey eventAuthorityKey,
                                final PublicKey programKey,
                                final long maxAmount) {
    final var keys = buyKeys(
      userKey,
      vaultStateKey,
      tokenVaultKey,
      tokenMintKey,
      baseVaultAuthorityKey,
      sharesMintKey,
      userTokenAtaKey,
      userSharesAtaKey,
      klendProgramKey,
      tokenProgramKey,
      sharesTokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return buy(invokedKaminoVaultProgramMeta, keys, maxAmount);
  }

  public static Instruction buy(final AccountMeta invokedKaminoVaultProgramMeta,
                                final List<AccountMeta> keys,
                                final long maxAmount) {
    final byte[] _data = new byte[16];
    int i = BUY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxAmount);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record BuyIxData(Discriminator discriminator, long maxAmount) implements Borsh {  

    public static BuyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static BuyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxAmount = getInt64LE(_data, i);
      return new BuyIxData(discriminator, maxAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 18, 70, 156, 148, 109, 161, 34);

  public static List<AccountMeta> withdrawKeys(final PublicKey withdrawFromAvailableUserKey,
                                               final PublicKey withdrawFromAvailableVaultStateKey,
                                               final PublicKey withdrawFromAvailableGlobalConfigKey,
                                               final PublicKey withdrawFromAvailableTokenVaultKey,
                                               final PublicKey withdrawFromAvailableBaseVaultAuthorityKey,
                                               final PublicKey withdrawFromAvailableUserTokenAtaKey,
                                               final PublicKey withdrawFromAvailableTokenMintKey,
                                               final PublicKey withdrawFromAvailableUserSharesAtaKey,
                                               final PublicKey withdrawFromAvailableSharesMintKey,
                                               final PublicKey withdrawFromAvailableTokenProgramKey,
                                               final PublicKey withdrawFromAvailableSharesTokenProgramKey,
                                               final PublicKey withdrawFromAvailableKlendProgramKey,
                                               final PublicKey withdrawFromAvailableEventAuthorityKey,
                                               final PublicKey withdrawFromAvailableProgramKey,
                                               final PublicKey withdrawFromReserveAccountsVaultStateKey,
                                               final PublicKey withdrawFromReserveAccountsReserveKey,
                                               final PublicKey withdrawFromReserveAccountsCtokenVaultKey,
                                               final PublicKey withdrawFromReserveAccountsLendingMarketKey,
                                               final PublicKey withdrawFromReserveAccountsLendingMarketAuthorityKey,
                                               final PublicKey withdrawFromReserveAccountsReserveLiquiditySupplyKey,
                                               final PublicKey withdrawFromReserveAccountsReserveCollateralMintKey,
                                               final PublicKey withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
                                               final PublicKey withdrawFromReserveAccountsInstructionSysvarAccountKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey) {
    return List.of(
      createWritableSigner(withdrawFromAvailableUserKey),
      createWrite(withdrawFromAvailableVaultStateKey),
      createRead(withdrawFromAvailableGlobalConfigKey),
      createWrite(withdrawFromAvailableTokenVaultKey),
      createRead(withdrawFromAvailableBaseVaultAuthorityKey),
      createWrite(withdrawFromAvailableUserTokenAtaKey),
      createWrite(withdrawFromAvailableTokenMintKey),
      createWrite(withdrawFromAvailableUserSharesAtaKey),
      createWrite(withdrawFromAvailableSharesMintKey),
      createRead(withdrawFromAvailableTokenProgramKey),
      createRead(withdrawFromAvailableSharesTokenProgramKey),
      createRead(withdrawFromAvailableKlendProgramKey),
      createRead(withdrawFromAvailableEventAuthorityKey),
      createRead(withdrawFromAvailableProgramKey),
      createWrite(withdrawFromReserveAccountsVaultStateKey),
      createWrite(withdrawFromReserveAccountsReserveKey),
      createWrite(withdrawFromReserveAccountsCtokenVaultKey),
      createRead(withdrawFromReserveAccountsLendingMarketKey),
      createRead(withdrawFromReserveAccountsLendingMarketAuthorityKey),
      createWrite(withdrawFromReserveAccountsReserveLiquiditySupplyKey),
      createWrite(withdrawFromReserveAccountsReserveCollateralMintKey),
      createRead(withdrawFromReserveAccountsReserveCollateralTokenProgramKey),
      createRead(withdrawFromReserveAccountsInstructionSysvarAccountKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction withdraw(final AccountMeta invokedKaminoVaultProgramMeta,
                                     final PublicKey withdrawFromAvailableUserKey,
                                     final PublicKey withdrawFromAvailableVaultStateKey,
                                     final PublicKey withdrawFromAvailableGlobalConfigKey,
                                     final PublicKey withdrawFromAvailableTokenVaultKey,
                                     final PublicKey withdrawFromAvailableBaseVaultAuthorityKey,
                                     final PublicKey withdrawFromAvailableUserTokenAtaKey,
                                     final PublicKey withdrawFromAvailableTokenMintKey,
                                     final PublicKey withdrawFromAvailableUserSharesAtaKey,
                                     final PublicKey withdrawFromAvailableSharesMintKey,
                                     final PublicKey withdrawFromAvailableTokenProgramKey,
                                     final PublicKey withdrawFromAvailableSharesTokenProgramKey,
                                     final PublicKey withdrawFromAvailableKlendProgramKey,
                                     final PublicKey withdrawFromAvailableEventAuthorityKey,
                                     final PublicKey withdrawFromAvailableProgramKey,
                                     final PublicKey withdrawFromReserveAccountsVaultStateKey,
                                     final PublicKey withdrawFromReserveAccountsReserveKey,
                                     final PublicKey withdrawFromReserveAccountsCtokenVaultKey,
                                     final PublicKey withdrawFromReserveAccountsLendingMarketKey,
                                     final PublicKey withdrawFromReserveAccountsLendingMarketAuthorityKey,
                                     final PublicKey withdrawFromReserveAccountsReserveLiquiditySupplyKey,
                                     final PublicKey withdrawFromReserveAccountsReserveCollateralMintKey,
                                     final PublicKey withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
                                     final PublicKey withdrawFromReserveAccountsInstructionSysvarAccountKey,
                                     final PublicKey eventAuthorityKey,
                                     final PublicKey programKey,
                                     final long sharesAmount) {
    final var keys = withdrawKeys(
      withdrawFromAvailableUserKey,
      withdrawFromAvailableVaultStateKey,
      withdrawFromAvailableGlobalConfigKey,
      withdrawFromAvailableTokenVaultKey,
      withdrawFromAvailableBaseVaultAuthorityKey,
      withdrawFromAvailableUserTokenAtaKey,
      withdrawFromAvailableTokenMintKey,
      withdrawFromAvailableUserSharesAtaKey,
      withdrawFromAvailableSharesMintKey,
      withdrawFromAvailableTokenProgramKey,
      withdrawFromAvailableSharesTokenProgramKey,
      withdrawFromAvailableKlendProgramKey,
      withdrawFromAvailableEventAuthorityKey,
      withdrawFromAvailableProgramKey,
      withdrawFromReserveAccountsVaultStateKey,
      withdrawFromReserveAccountsReserveKey,
      withdrawFromReserveAccountsCtokenVaultKey,
      withdrawFromReserveAccountsLendingMarketKey,
      withdrawFromReserveAccountsLendingMarketAuthorityKey,
      withdrawFromReserveAccountsReserveLiquiditySupplyKey,
      withdrawFromReserveAccountsReserveCollateralMintKey,
      withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
      withdrawFromReserveAccountsInstructionSysvarAccountKey,
      eventAuthorityKey,
      programKey
    );
    return withdraw(invokedKaminoVaultProgramMeta, keys, sharesAmount);
  }

  public static Instruction withdraw(final AccountMeta invokedKaminoVaultProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long sharesAmount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, sharesAmount);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record WithdrawIxData(Discriminator discriminator, long sharesAmount) implements Borsh {  

    public static WithdrawIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var sharesAmount = getInt64LE(_data, i);
      return new WithdrawIxData(discriminator, sharesAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, sharesAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SELL_DISCRIMINATOR = toDiscriminator(51, 230, 133, 164, 1, 127, 131, 173);

  public static List<AccountMeta> sellKeys(final PublicKey withdrawFromAvailableUserKey,
                                           final PublicKey withdrawFromAvailableVaultStateKey,
                                           final PublicKey withdrawFromAvailableGlobalConfigKey,
                                           final PublicKey withdrawFromAvailableTokenVaultKey,
                                           final PublicKey withdrawFromAvailableBaseVaultAuthorityKey,
                                           final PublicKey withdrawFromAvailableUserTokenAtaKey,
                                           final PublicKey withdrawFromAvailableTokenMintKey,
                                           final PublicKey withdrawFromAvailableUserSharesAtaKey,
                                           final PublicKey withdrawFromAvailableSharesMintKey,
                                           final PublicKey withdrawFromAvailableTokenProgramKey,
                                           final PublicKey withdrawFromAvailableSharesTokenProgramKey,
                                           final PublicKey withdrawFromAvailableKlendProgramKey,
                                           final PublicKey withdrawFromAvailableEventAuthorityKey,
                                           final PublicKey withdrawFromAvailableProgramKey,
                                           final PublicKey withdrawFromReserveAccountsVaultStateKey,
                                           final PublicKey withdrawFromReserveAccountsReserveKey,
                                           final PublicKey withdrawFromReserveAccountsCtokenVaultKey,
                                           final PublicKey withdrawFromReserveAccountsLendingMarketKey,
                                           final PublicKey withdrawFromReserveAccountsLendingMarketAuthorityKey,
                                           final PublicKey withdrawFromReserveAccountsReserveLiquiditySupplyKey,
                                           final PublicKey withdrawFromReserveAccountsReserveCollateralMintKey,
                                           final PublicKey withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
                                           final PublicKey withdrawFromReserveAccountsInstructionSysvarAccountKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey) {
    return List.of(
      createWritableSigner(withdrawFromAvailableUserKey),
      createWrite(withdrawFromAvailableVaultStateKey),
      createRead(withdrawFromAvailableGlobalConfigKey),
      createWrite(withdrawFromAvailableTokenVaultKey),
      createRead(withdrawFromAvailableBaseVaultAuthorityKey),
      createWrite(withdrawFromAvailableUserTokenAtaKey),
      createWrite(withdrawFromAvailableTokenMintKey),
      createWrite(withdrawFromAvailableUserSharesAtaKey),
      createWrite(withdrawFromAvailableSharesMintKey),
      createRead(withdrawFromAvailableTokenProgramKey),
      createRead(withdrawFromAvailableSharesTokenProgramKey),
      createRead(withdrawFromAvailableKlendProgramKey),
      createRead(withdrawFromAvailableEventAuthorityKey),
      createRead(withdrawFromAvailableProgramKey),
      createWrite(withdrawFromReserveAccountsVaultStateKey),
      createWrite(withdrawFromReserveAccountsReserveKey),
      createWrite(withdrawFromReserveAccountsCtokenVaultKey),
      createRead(withdrawFromReserveAccountsLendingMarketKey),
      createRead(withdrawFromReserveAccountsLendingMarketAuthorityKey),
      createWrite(withdrawFromReserveAccountsReserveLiquiditySupplyKey),
      createWrite(withdrawFromReserveAccountsReserveCollateralMintKey),
      createRead(withdrawFromReserveAccountsReserveCollateralTokenProgramKey),
      createRead(withdrawFromReserveAccountsInstructionSysvarAccountKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction sell(final AccountMeta invokedKaminoVaultProgramMeta,
                                 final PublicKey withdrawFromAvailableUserKey,
                                 final PublicKey withdrawFromAvailableVaultStateKey,
                                 final PublicKey withdrawFromAvailableGlobalConfigKey,
                                 final PublicKey withdrawFromAvailableTokenVaultKey,
                                 final PublicKey withdrawFromAvailableBaseVaultAuthorityKey,
                                 final PublicKey withdrawFromAvailableUserTokenAtaKey,
                                 final PublicKey withdrawFromAvailableTokenMintKey,
                                 final PublicKey withdrawFromAvailableUserSharesAtaKey,
                                 final PublicKey withdrawFromAvailableSharesMintKey,
                                 final PublicKey withdrawFromAvailableTokenProgramKey,
                                 final PublicKey withdrawFromAvailableSharesTokenProgramKey,
                                 final PublicKey withdrawFromAvailableKlendProgramKey,
                                 final PublicKey withdrawFromAvailableEventAuthorityKey,
                                 final PublicKey withdrawFromAvailableProgramKey,
                                 final PublicKey withdrawFromReserveAccountsVaultStateKey,
                                 final PublicKey withdrawFromReserveAccountsReserveKey,
                                 final PublicKey withdrawFromReserveAccountsCtokenVaultKey,
                                 final PublicKey withdrawFromReserveAccountsLendingMarketKey,
                                 final PublicKey withdrawFromReserveAccountsLendingMarketAuthorityKey,
                                 final PublicKey withdrawFromReserveAccountsReserveLiquiditySupplyKey,
                                 final PublicKey withdrawFromReserveAccountsReserveCollateralMintKey,
                                 final PublicKey withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
                                 final PublicKey withdrawFromReserveAccountsInstructionSysvarAccountKey,
                                 final PublicKey eventAuthorityKey,
                                 final PublicKey programKey,
                                 final long sharesAmount) {
    final var keys = sellKeys(
      withdrawFromAvailableUserKey,
      withdrawFromAvailableVaultStateKey,
      withdrawFromAvailableGlobalConfigKey,
      withdrawFromAvailableTokenVaultKey,
      withdrawFromAvailableBaseVaultAuthorityKey,
      withdrawFromAvailableUserTokenAtaKey,
      withdrawFromAvailableTokenMintKey,
      withdrawFromAvailableUserSharesAtaKey,
      withdrawFromAvailableSharesMintKey,
      withdrawFromAvailableTokenProgramKey,
      withdrawFromAvailableSharesTokenProgramKey,
      withdrawFromAvailableKlendProgramKey,
      withdrawFromAvailableEventAuthorityKey,
      withdrawFromAvailableProgramKey,
      withdrawFromReserveAccountsVaultStateKey,
      withdrawFromReserveAccountsReserveKey,
      withdrawFromReserveAccountsCtokenVaultKey,
      withdrawFromReserveAccountsLendingMarketKey,
      withdrawFromReserveAccountsLendingMarketAuthorityKey,
      withdrawFromReserveAccountsReserveLiquiditySupplyKey,
      withdrawFromReserveAccountsReserveCollateralMintKey,
      withdrawFromReserveAccountsReserveCollateralTokenProgramKey,
      withdrawFromReserveAccountsInstructionSysvarAccountKey,
      eventAuthorityKey,
      programKey
    );
    return sell(invokedKaminoVaultProgramMeta, keys, sharesAmount);
  }

  public static Instruction sell(final AccountMeta invokedKaminoVaultProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long sharesAmount) {
    final byte[] _data = new byte[16];
    int i = SELL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, sharesAmount);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record SellIxData(Discriminator discriminator, long sharesAmount) implements Borsh {  

    public static SellIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SellIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var sharesAmount = getInt64LE(_data, i);
      return new SellIxData(discriminator, sharesAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, sharesAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INVEST_DISCRIMINATOR = toDiscriminator(13, 245, 180, 103, 254, 182, 121, 4);

  /// @param reserveKey CPI accounts
  public static List<AccountMeta> investKeys(final AccountMeta invokedKaminoVaultProgramMeta,
                                             final PublicKey payerKey,
                                             final PublicKey payerTokenAccountKey,
                                             final PublicKey vaultStateKey,
                                             final PublicKey tokenVaultKey,
                                             final PublicKey tokenMintKey,
                                             final PublicKey baseVaultAuthorityKey,
                                             final PublicKey ctokenVaultKey,
                                             final PublicKey reserveKey,
                                             final PublicKey lendingMarketKey,
                                             final PublicKey lendingMarketAuthorityKey,
                                             final PublicKey reserveLiquiditySupplyKey,
                                             final PublicKey reserveCollateralMintKey,
                                             final PublicKey reserveWhitelistEntryKey,
                                             final PublicKey klendProgramKey,
                                             final PublicKey reserveCollateralTokenProgramKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(payerTokenAccountKey),
      createWrite(vaultStateKey),
      createWrite(tokenVaultKey),
      createWrite(tokenMintKey),
      createWrite(baseVaultAuthorityKey),
      createWrite(ctokenVaultKey),
      createWrite(reserveKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(reserveCollateralMintKey),
      createRead(requireNonNullElse(reserveWhitelistEntryKey, invokedKaminoVaultProgramMeta.publicKey())),
      createRead(klendProgramKey),
      createRead(reserveCollateralTokenProgramKey),
      createRead(tokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  /// @param reserveKey CPI accounts
  public static Instruction invest(final AccountMeta invokedKaminoVaultProgramMeta,
                                   final PublicKey payerKey,
                                   final PublicKey payerTokenAccountKey,
                                   final PublicKey vaultStateKey,
                                   final PublicKey tokenVaultKey,
                                   final PublicKey tokenMintKey,
                                   final PublicKey baseVaultAuthorityKey,
                                   final PublicKey ctokenVaultKey,
                                   final PublicKey reserveKey,
                                   final PublicKey lendingMarketKey,
                                   final PublicKey lendingMarketAuthorityKey,
                                   final PublicKey reserveLiquiditySupplyKey,
                                   final PublicKey reserveCollateralMintKey,
                                   final PublicKey reserveWhitelistEntryKey,
                                   final PublicKey klendProgramKey,
                                   final PublicKey reserveCollateralTokenProgramKey,
                                   final PublicKey tokenProgramKey,
                                   final PublicKey instructionSysvarAccountKey) {
    final var keys = investKeys(
      invokedKaminoVaultProgramMeta,
      payerKey,
      payerTokenAccountKey,
      vaultStateKey,
      tokenVaultKey,
      tokenMintKey,
      baseVaultAuthorityKey,
      ctokenVaultKey,
      reserveKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      reserveLiquiditySupplyKey,
      reserveCollateralMintKey,
      reserveWhitelistEntryKey,
      klendProgramKey,
      reserveCollateralTokenProgramKey,
      tokenProgramKey,
      instructionSysvarAccountKey
    );
    return invest(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction invest(final AccountMeta invokedKaminoVaultProgramMeta,
                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, INVEST_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_VAULT_CONFIG_DISCRIMINATOR = toDiscriminator(122, 3, 21, 222, 158, 255, 238, 157);

  public static List<AccountMeta> updateVaultConfigKeys(final PublicKey signerKey,
                                                        final PublicKey globalConfigKey,
                                                        final PublicKey vaultStateKey,
                                                        final PublicKey klendProgramKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createRead(globalConfigKey),
      createWrite(vaultStateKey),
      createRead(klendProgramKey)
    );
  }

  public static Instruction updateVaultConfig(final AccountMeta invokedKaminoVaultProgramMeta,
                                              final PublicKey signerKey,
                                              final PublicKey globalConfigKey,
                                              final PublicKey vaultStateKey,
                                              final PublicKey klendProgramKey,
                                              final VaultConfigField entry,
                                              final byte[] data) {
    final var keys = updateVaultConfigKeys(
      signerKey,
      globalConfigKey,
      vaultStateKey,
      klendProgramKey
    );
    return updateVaultConfig(invokedKaminoVaultProgramMeta, keys, entry, data);
  }

  public static Instruction updateVaultConfig(final AccountMeta invokedKaminoVaultProgramMeta,
                                              final List<AccountMeta> keys,
                                              final VaultConfigField entry,
                                              final byte[] data) {
    final byte[] _data = new byte[8 + entry.l() + Borsh.lenVector(data)];
    int i = UPDATE_VAULT_CONFIG_DISCRIMINATOR.write(_data, 0);
    i += entry.write(_data, i);
    Borsh.writeVector(data, _data, i);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record UpdateVaultConfigIxData(Discriminator discriminator, VaultConfigField entry, byte[] data) implements Borsh {  

    public static UpdateVaultConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateVaultConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var entry = VaultConfigField.read(_data, i);
      i += entry.l();
      final var data = Borsh.readbyteVector(_data, i);
      return new UpdateVaultConfigIxData(discriminator, entry, data);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += entry.write(_data, i);
      i += Borsh.writeVector(data, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + entry.l() + Borsh.lenVector(data);
    }
  }

  public static final Discriminator WITHDRAW_PENDING_FEES_DISCRIMINATOR = toDiscriminator(131, 194, 200, 140, 175, 244, 217, 183);

  /// @param lendingMarketKey CPI accounts
  public static List<AccountMeta> withdrawPendingFeesKeys(final PublicKey vaultAdminAuthorityKey,
                                                          final PublicKey vaultStateKey,
                                                          final PublicKey reserveKey,
                                                          final PublicKey tokenVaultKey,
                                                          final PublicKey ctokenVaultKey,
                                                          final PublicKey baseVaultAuthorityKey,
                                                          final PublicKey tokenAtaKey,
                                                          final PublicKey tokenMintKey,
                                                          final PublicKey lendingMarketKey,
                                                          final PublicKey lendingMarketAuthorityKey,
                                                          final PublicKey reserveLiquiditySupplyKey,
                                                          final PublicKey reserveCollateralMintKey,
                                                          final PublicKey klendProgramKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey reserveCollateralTokenProgramKey,
                                                          final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createWritableSigner(vaultAdminAuthorityKey),
      createWrite(vaultStateKey),
      createWrite(reserveKey),
      createWrite(tokenVaultKey),
      createWrite(ctokenVaultKey),
      createWrite(baseVaultAuthorityKey),
      createWrite(tokenAtaKey),
      createWrite(tokenMintKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(reserveCollateralMintKey),
      createRead(klendProgramKey),
      createRead(tokenProgramKey),
      createRead(reserveCollateralTokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  /// @param lendingMarketKey CPI accounts
  public static Instruction withdrawPendingFees(final AccountMeta invokedKaminoVaultProgramMeta,
                                                final PublicKey vaultAdminAuthorityKey,
                                                final PublicKey vaultStateKey,
                                                final PublicKey reserveKey,
                                                final PublicKey tokenVaultKey,
                                                final PublicKey ctokenVaultKey,
                                                final PublicKey baseVaultAuthorityKey,
                                                final PublicKey tokenAtaKey,
                                                final PublicKey tokenMintKey,
                                                final PublicKey lendingMarketKey,
                                                final PublicKey lendingMarketAuthorityKey,
                                                final PublicKey reserveLiquiditySupplyKey,
                                                final PublicKey reserveCollateralMintKey,
                                                final PublicKey klendProgramKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey reserveCollateralTokenProgramKey,
                                                final PublicKey instructionSysvarAccountKey) {
    final var keys = withdrawPendingFeesKeys(
      vaultAdminAuthorityKey,
      vaultStateKey,
      reserveKey,
      tokenVaultKey,
      ctokenVaultKey,
      baseVaultAuthorityKey,
      tokenAtaKey,
      tokenMintKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      reserveLiquiditySupplyKey,
      reserveCollateralMintKey,
      klendProgramKey,
      tokenProgramKey,
      reserveCollateralTokenProgramKey,
      instructionSysvarAccountKey
    );
    return withdrawPendingFees(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction withdrawPendingFees(final AccountMeta invokedKaminoVaultProgramMeta,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, WITHDRAW_PENDING_FEES_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_ADMIN_DISCRIMINATOR = toDiscriminator(161, 176, 40, 213, 60, 184, 179, 228);

  public static List<AccountMeta> updateAdminKeys(final PublicKey pendingAdminKey,
                                                  final PublicKey vaultStateKey) {
    return List.of(
      createWritableSigner(pendingAdminKey),
      createWrite(vaultStateKey)
    );
  }

  public static Instruction updateAdmin(final AccountMeta invokedKaminoVaultProgramMeta,
                                        final PublicKey pendingAdminKey,
                                        final PublicKey vaultStateKey) {
    final var keys = updateAdminKeys(
      pendingAdminKey,
      vaultStateKey
    );
    return updateAdmin(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction updateAdmin(final AccountMeta invokedKaminoVaultProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, UPDATE_ADMIN_DISCRIMINATOR);
  }

  public static final Discriminator GIVE_UP_PENDING_FEES_DISCRIMINATOR = toDiscriminator(177, 200, 120, 134, 110, 217, 147, 81);

  public static List<AccountMeta> giveUpPendingFeesKeys(final PublicKey vaultAdminAuthorityKey,
                                                        final PublicKey vaultStateKey,
                                                        final PublicKey klendProgramKey) {
    return List.of(
      createWritableSigner(vaultAdminAuthorityKey),
      createWrite(vaultStateKey),
      createRead(klendProgramKey)
    );
  }

  public static Instruction giveUpPendingFees(final AccountMeta invokedKaminoVaultProgramMeta,
                                              final PublicKey vaultAdminAuthorityKey,
                                              final PublicKey vaultStateKey,
                                              final PublicKey klendProgramKey,
                                              final long maxAmountToGiveUp) {
    final var keys = giveUpPendingFeesKeys(
      vaultAdminAuthorityKey,
      vaultStateKey,
      klendProgramKey
    );
    return giveUpPendingFees(invokedKaminoVaultProgramMeta, keys, maxAmountToGiveUp);
  }

  public static Instruction giveUpPendingFees(final AccountMeta invokedKaminoVaultProgramMeta,
                                              final List<AccountMeta> keys,
                                              final long maxAmountToGiveUp) {
    final byte[] _data = new byte[16];
    int i = GIVE_UP_PENDING_FEES_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxAmountToGiveUp);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record GiveUpPendingFeesIxData(Discriminator discriminator, long maxAmountToGiveUp) implements Borsh {  

    public static GiveUpPendingFeesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static GiveUpPendingFeesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxAmountToGiveUp = getInt64LE(_data, i);
      return new GiveUpPendingFeesIxData(discriminator, maxAmountToGiveUp);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxAmountToGiveUp);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_SHARES_METADATA_DISCRIMINATOR = toDiscriminator(3, 15, 172, 114, 200, 0, 131, 32);

  public static List<AccountMeta> initializeSharesMetadataKeys(final PublicKey vaultAdminAuthorityKey,
                                                               final PublicKey vaultStateKey,
                                                               final PublicKey sharesMintKey,
                                                               final PublicKey baseVaultAuthorityKey,
                                                               final PublicKey sharesMetadataKey,
                                                               final PublicKey systemProgramKey,
                                                               final PublicKey rentKey,
                                                               final PublicKey metadataProgramKey) {
    return List.of(
      createWritableSigner(vaultAdminAuthorityKey),
      createRead(vaultStateKey),
      createRead(sharesMintKey),
      createRead(baseVaultAuthorityKey),
      createWrite(sharesMetadataKey),
      createRead(systemProgramKey),
      createRead(rentKey),
      createRead(metadataProgramKey)
    );
  }

  public static Instruction initializeSharesMetadata(final AccountMeta invokedKaminoVaultProgramMeta,
                                                     final PublicKey vaultAdminAuthorityKey,
                                                     final PublicKey vaultStateKey,
                                                     final PublicKey sharesMintKey,
                                                     final PublicKey baseVaultAuthorityKey,
                                                     final PublicKey sharesMetadataKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey rentKey,
                                                     final PublicKey metadataProgramKey,
                                                     final String name,
                                                     final String symbol,
                                                     final String uri) {
    final var keys = initializeSharesMetadataKeys(
      vaultAdminAuthorityKey,
      vaultStateKey,
      sharesMintKey,
      baseVaultAuthorityKey,
      sharesMetadataKey,
      systemProgramKey,
      rentKey,
      metadataProgramKey
    );
    return initializeSharesMetadata(
      invokedKaminoVaultProgramMeta,
      keys,
      name,
      symbol,
      uri
    );
  }

  public static Instruction initializeSharesMetadata(final AccountMeta invokedKaminoVaultProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final String name,
                                                     final String symbol,
                                                     final String uri) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _symbol = symbol.getBytes(UTF_8);
    final byte[] _uri = uri.getBytes(UTF_8);
    final byte[] _data = new byte[20 + _name.length + _symbol.length + _uri.length];
    int i = INITIALIZE_SHARES_METADATA_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(_name, _data, i);
    i += Borsh.writeVector(_symbol, _data, i);
    Borsh.writeVector(_uri, _data, i);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record InitializeSharesMetadataIxData(Discriminator discriminator,
                                               String name, byte[] _name,
                                               String symbol, byte[] _symbol,
                                               String uri, byte[] _uri) implements Borsh {  

    public static InitializeSharesMetadataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeSharesMetadataIxData createRecord(final Discriminator discriminator,
                                                              final String name,
                                                              final String symbol,
                                                              final String uri) {
      return new InitializeSharesMetadataIxData(discriminator, name, name.getBytes(UTF_8), symbol, symbol.getBytes(UTF_8), uri, uri.getBytes(UTF_8));
    }

    public static InitializeSharesMetadataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _nameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _name = Arrays.copyOfRange(_data, i, i + _nameLength);
      final var name = new String(_name, UTF_8);
      i += _name.length;
      final int _symbolLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _symbol = Arrays.copyOfRange(_data, i, i + _symbolLength);
      final var symbol = new String(_symbol, UTF_8);
      i += _symbol.length;
      final int _uriLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _uri = Arrays.copyOfRange(_data, i, i + _uriLength);
      final var uri = new String(_uri, UTF_8);
      return new InitializeSharesMetadataIxData(discriminator, name, _name, symbol, _symbol, uri, _uri);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_name, _data, i);
      i += Borsh.writeVector(_symbol, _data, i);
      i += Borsh.writeVector(_uri, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _name.length + _symbol.length + _uri.length;
    }
  }

  public static final Discriminator UPDATE_SHARES_METADATA_DISCRIMINATOR = toDiscriminator(155, 34, 122, 165, 245, 137, 147, 107);

  public static List<AccountMeta> updateSharesMetadataKeys(final PublicKey vaultAdminAuthorityKey,
                                                           final PublicKey vaultStateKey,
                                                           final PublicKey baseVaultAuthorityKey,
                                                           final PublicKey sharesMetadataKey,
                                                           final PublicKey metadataProgramKey) {
    return List.of(
      createWritableSigner(vaultAdminAuthorityKey),
      createRead(vaultStateKey),
      createRead(baseVaultAuthorityKey),
      createWrite(sharesMetadataKey),
      createRead(metadataProgramKey)
    );
  }

  public static Instruction updateSharesMetadata(final AccountMeta invokedKaminoVaultProgramMeta,
                                                 final PublicKey vaultAdminAuthorityKey,
                                                 final PublicKey vaultStateKey,
                                                 final PublicKey baseVaultAuthorityKey,
                                                 final PublicKey sharesMetadataKey,
                                                 final PublicKey metadataProgramKey,
                                                 final String name,
                                                 final String symbol,
                                                 final String uri) {
    final var keys = updateSharesMetadataKeys(
      vaultAdminAuthorityKey,
      vaultStateKey,
      baseVaultAuthorityKey,
      sharesMetadataKey,
      metadataProgramKey
    );
    return updateSharesMetadata(
      invokedKaminoVaultProgramMeta,
      keys,
      name,
      symbol,
      uri
    );
  }

  public static Instruction updateSharesMetadata(final AccountMeta invokedKaminoVaultProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final String name,
                                                 final String symbol,
                                                 final String uri) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _symbol = symbol.getBytes(UTF_8);
    final byte[] _uri = uri.getBytes(UTF_8);
    final byte[] _data = new byte[20 + _name.length + _symbol.length + _uri.length];
    int i = UPDATE_SHARES_METADATA_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(_name, _data, i);
    i += Borsh.writeVector(_symbol, _data, i);
    Borsh.writeVector(_uri, _data, i);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record UpdateSharesMetadataIxData(Discriminator discriminator,
                                           String name, byte[] _name,
                                           String symbol, byte[] _symbol,
                                           String uri, byte[] _uri) implements Borsh {  

    public static UpdateSharesMetadataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateSharesMetadataIxData createRecord(final Discriminator discriminator,
                                                          final String name,
                                                          final String symbol,
                                                          final String uri) {
      return new UpdateSharesMetadataIxData(discriminator, name, name.getBytes(UTF_8), symbol, symbol.getBytes(UTF_8), uri, uri.getBytes(UTF_8));
    }

    public static UpdateSharesMetadataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _nameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _name = Arrays.copyOfRange(_data, i, i + _nameLength);
      final var name = new String(_name, UTF_8);
      i += _name.length;
      final int _symbolLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _symbol = Arrays.copyOfRange(_data, i, i + _symbolLength);
      final var symbol = new String(_symbol, UTF_8);
      i += _symbol.length;
      final int _uriLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _uri = Arrays.copyOfRange(_data, i, i + _uriLength);
      final var uri = new String(_uri, UTF_8);
      return new UpdateSharesMetadataIxData(discriminator, name, _name, symbol, _symbol, uri, _uri);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_name, _data, i);
      i += Borsh.writeVector(_symbol, _data, i);
      i += Borsh.writeVector(_uri, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _name.length + _symbol.length + _uri.length;
    }
  }

  public static final Discriminator WITHDRAW_FROM_AVAILABLE_DISCRIMINATOR = toDiscriminator(19, 131, 112, 155, 170, 220, 34, 57);

  public static List<AccountMeta> withdrawFromAvailableKeys(final PublicKey userKey,
                                                            final PublicKey vaultStateKey,
                                                            final PublicKey globalConfigKey,
                                                            final PublicKey tokenVaultKey,
                                                            final PublicKey baseVaultAuthorityKey,
                                                            final PublicKey userTokenAtaKey,
                                                            final PublicKey tokenMintKey,
                                                            final PublicKey userSharesAtaKey,
                                                            final PublicKey sharesMintKey,
                                                            final PublicKey tokenProgramKey,
                                                            final PublicKey sharesTokenProgramKey,
                                                            final PublicKey klendProgramKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createWritableSigner(userKey),
      createWrite(vaultStateKey),
      createRead(globalConfigKey),
      createWrite(tokenVaultKey),
      createRead(baseVaultAuthorityKey),
      createWrite(userTokenAtaKey),
      createWrite(tokenMintKey),
      createWrite(userSharesAtaKey),
      createWrite(sharesMintKey),
      createRead(tokenProgramKey),
      createRead(sharesTokenProgramKey),
      createRead(klendProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction withdrawFromAvailable(final AccountMeta invokedKaminoVaultProgramMeta,
                                                  final PublicKey userKey,
                                                  final PublicKey vaultStateKey,
                                                  final PublicKey globalConfigKey,
                                                  final PublicKey tokenVaultKey,
                                                  final PublicKey baseVaultAuthorityKey,
                                                  final PublicKey userTokenAtaKey,
                                                  final PublicKey tokenMintKey,
                                                  final PublicKey userSharesAtaKey,
                                                  final PublicKey sharesMintKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey sharesTokenProgramKey,
                                                  final PublicKey klendProgramKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final long sharesAmount) {
    final var keys = withdrawFromAvailableKeys(
      userKey,
      vaultStateKey,
      globalConfigKey,
      tokenVaultKey,
      baseVaultAuthorityKey,
      userTokenAtaKey,
      tokenMintKey,
      userSharesAtaKey,
      sharesMintKey,
      tokenProgramKey,
      sharesTokenProgramKey,
      klendProgramKey,
      eventAuthorityKey,
      programKey
    );
    return withdrawFromAvailable(invokedKaminoVaultProgramMeta, keys, sharesAmount);
  }

  public static Instruction withdrawFromAvailable(final AccountMeta invokedKaminoVaultProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final long sharesAmount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_FROM_AVAILABLE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, sharesAmount);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record WithdrawFromAvailableIxData(Discriminator discriminator, long sharesAmount) implements Borsh {  

    public static WithdrawFromAvailableIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawFromAvailableIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var sharesAmount = getInt64LE(_data, i);
      return new WithdrawFromAvailableIxData(discriminator, sharesAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, sharesAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REMOVE_ALLOCATION_DISCRIMINATOR = toDiscriminator(32, 220, 211, 141, 209, 231, 73, 76);

  public static List<AccountMeta> removeAllocationKeys(final PublicKey vaultAdminAuthorityKey,
                                                       final PublicKey vaultStateKey,
                                                       final PublicKey reserveKey) {
    return List.of(
      createWritableSigner(vaultAdminAuthorityKey),
      createWrite(vaultStateKey),
      createRead(reserveKey)
    );
  }

  public static Instruction removeAllocation(final AccountMeta invokedKaminoVaultProgramMeta,
                                             final PublicKey vaultAdminAuthorityKey,
                                             final PublicKey vaultStateKey,
                                             final PublicKey reserveKey) {
    final var keys = removeAllocationKeys(
      vaultAdminAuthorityKey,
      vaultStateKey,
      reserveKey
    );
    return removeAllocation(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction removeAllocation(final AccountMeta invokedKaminoVaultProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, REMOVE_ALLOCATION_DISCRIMINATOR);
  }

  public static final Discriminator INIT_GLOBAL_CONFIG_DISCRIMINATOR = toDiscriminator(140, 136, 214, 48, 87, 0, 120, 255);

  public static List<AccountMeta> initGlobalConfigKeys(final PublicKey payerKey,
                                                       final PublicKey globalConfigKey,
                                                       final PublicKey programDataKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey rentKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(globalConfigKey),
      createRead(programDataKey),
      createRead(systemProgramKey),
      createRead(rentKey)
    );
  }

  public static Instruction initGlobalConfig(final AccountMeta invokedKaminoVaultProgramMeta,
                                             final PublicKey payerKey,
                                             final PublicKey globalConfigKey,
                                             final PublicKey programDataKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey rentKey) {
    final var keys = initGlobalConfigKeys(
      payerKey,
      globalConfigKey,
      programDataKey,
      systemProgramKey,
      rentKey
    );
    return initGlobalConfig(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction initGlobalConfig(final AccountMeta invokedKaminoVaultProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, INIT_GLOBAL_CONFIG_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_GLOBAL_CONFIG_DISCRIMINATOR = toDiscriminator(164, 84, 130, 189, 111, 58, 250, 200);

  public static List<AccountMeta> updateGlobalConfigKeys(final PublicKey globalAdminKey,
                                                         final PublicKey globalConfigKey) {
    return List.of(
      createReadOnlySigner(globalAdminKey),
      createWrite(globalConfigKey)
    );
  }

  public static Instruction updateGlobalConfig(final AccountMeta invokedKaminoVaultProgramMeta,
                                               final PublicKey globalAdminKey,
                                               final PublicKey globalConfigKey,
                                               final UpdateGlobalConfigMode update) {
    final var keys = updateGlobalConfigKeys(
      globalAdminKey,
      globalConfigKey
    );
    return updateGlobalConfig(invokedKaminoVaultProgramMeta, keys, update);
  }

  public static Instruction updateGlobalConfig(final AccountMeta invokedKaminoVaultProgramMeta,
                                               final List<AccountMeta> keys,
                                               final UpdateGlobalConfigMode update) {
    final byte[] _data = new byte[8 + update.l()];
    int i = UPDATE_GLOBAL_CONFIG_DISCRIMINATOR.write(_data, 0);
    update.write(_data, i);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record UpdateGlobalConfigIxData(Discriminator discriminator, UpdateGlobalConfigMode update) implements Borsh {  

    public static UpdateGlobalConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateGlobalConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var update = UpdateGlobalConfigMode.read(_data, i);
      return new UpdateGlobalConfigIxData(discriminator, update);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += update.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + update.l();
    }
  }

  public static final Discriminator UPDATE_GLOBAL_CONFIG_ADMIN_DISCRIMINATOR = toDiscriminator(184, 87, 23, 193, 156, 238, 175, 119);

  public static List<AccountMeta> updateGlobalConfigAdminKeys(final PublicKey pendingAdminKey,
                                                              final PublicKey globalConfigKey) {
    return List.of(
      createReadOnlySigner(pendingAdminKey),
      createWrite(globalConfigKey)
    );
  }

  public static Instruction updateGlobalConfigAdmin(final AccountMeta invokedKaminoVaultProgramMeta,
                                                    final PublicKey pendingAdminKey,
                                                    final PublicKey globalConfigKey) {
    final var keys = updateGlobalConfigAdminKeys(
      pendingAdminKey,
      globalConfigKey
    );
    return updateGlobalConfigAdmin(invokedKaminoVaultProgramMeta, keys);
  }

  public static Instruction updateGlobalConfigAdmin(final AccountMeta invokedKaminoVaultProgramMeta,
                                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, UPDATE_GLOBAL_CONFIG_ADMIN_DISCRIMINATOR);
  }

  public static final Discriminator ADD_UPDATE_WHITELISTED_RESERVE_DISCRIMINATOR = toDiscriminator(219, 139, 95, 204, 7, 183, 118, 45);

  public static List<AccountMeta> addUpdateWhitelistedReserveKeys(final PublicKey globalAdminKey,
                                                                  final PublicKey globalConfigKey,
                                                                  final PublicKey reserveKey,
                                                                  final PublicKey reserveWhitelistEntryKey,
                                                                  final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(globalAdminKey),
      createRead(globalConfigKey),
      createRead(reserveKey),
      createWrite(reserveWhitelistEntryKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction addUpdateWhitelistedReserve(final AccountMeta invokedKaminoVaultProgramMeta,
                                                        final PublicKey globalAdminKey,
                                                        final PublicKey globalConfigKey,
                                                        final PublicKey reserveKey,
                                                        final PublicKey reserveWhitelistEntryKey,
                                                        final PublicKey systemProgramKey,
                                                        final UpdateReserveWhitelistMode update) {
    final var keys = addUpdateWhitelistedReserveKeys(
      globalAdminKey,
      globalConfigKey,
      reserveKey,
      reserveWhitelistEntryKey,
      systemProgramKey
    );
    return addUpdateWhitelistedReserve(invokedKaminoVaultProgramMeta, keys, update);
  }

  public static Instruction addUpdateWhitelistedReserve(final AccountMeta invokedKaminoVaultProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final UpdateReserveWhitelistMode update) {
    final byte[] _data = new byte[8 + update.l()];
    int i = ADD_UPDATE_WHITELISTED_RESERVE_DISCRIMINATOR.write(_data, 0);
    update.write(_data, i);

    return Instruction.createInstruction(invokedKaminoVaultProgramMeta, keys, _data);
  }

  public record AddUpdateWhitelistedReserveIxData(Discriminator discriminator, UpdateReserveWhitelistMode update) implements Borsh {  

    public static AddUpdateWhitelistedReserveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddUpdateWhitelistedReserveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var update = UpdateReserveWhitelistMode.read(_data, i);
      return new AddUpdateWhitelistedReserveIxData(discriminator, update);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += update.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + update.l();
    }
  }

  private KaminoVaultProgram() {
  }
}
