package software.sava.idl.clients.kamino.lend.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.lend.gen.types.AssetTier;
import software.sava.idl.clients.kamino.lend.gen.types.FeeCalculation;
import software.sava.idl.clients.kamino.lend.gen.types.InitObligationArgs;
import software.sava.idl.clients.kamino.lend.gen.types.ObligationOrder;
import software.sava.idl.clients.kamino.lend.gen.types.ReserveFarmKind;
import software.sava.idl.clients.kamino.lend.gen.types.ReserveStatus;
import software.sava.idl.clients.kamino.lend.gen.types.UpdateConfigMode;
import software.sava.idl.clients.kamino.lend.gen.types.UpdateGlobalConfigMode;
import software.sava.idl.clients.kamino.lend.gen.types.UpdateLendingMarketConfigValue;
import software.sava.idl.clients.kamino.lend.gen.types.UpdateLendingMarketMode;

import static java.nio.charset.StandardCharsets.UTF_8;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class KaminoLendingProgram {

  public static final Discriminator INIT_LENDING_MARKET_DISCRIMINATOR = toDiscriminator(34, 162, 116, 14, 101, 137, 94, 239);

  public static List<AccountMeta> initLendingMarketKeys(final PublicKey lendingMarketOwnerKey,
                                                        final PublicKey lendingMarketKey,
                                                        final PublicKey lendingMarketAuthorityKey,
                                                        final PublicKey systemProgramKey,
                                                        final PublicKey rentKey) {
    return List.of(
      createWritableSigner(lendingMarketOwnerKey),
      createWrite(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createRead(systemProgramKey),
      createRead(rentKey)
    );
  }

  public static Instruction initLendingMarket(final AccountMeta invokedKaminoLendingProgramMeta,
                                              final PublicKey lendingMarketOwnerKey,
                                              final PublicKey lendingMarketKey,
                                              final PublicKey lendingMarketAuthorityKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey rentKey,
                                              final byte[] quoteCurrency) {
    final var keys = initLendingMarketKeys(
      lendingMarketOwnerKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      systemProgramKey,
      rentKey
    );
    return initLendingMarket(invokedKaminoLendingProgramMeta, keys, quoteCurrency);
  }

  public static Instruction initLendingMarket(final AccountMeta invokedKaminoLendingProgramMeta,
                                              final List<AccountMeta> keys,
                                              final byte[] quoteCurrency) {
    final byte[] _data = new byte[8 + Borsh.lenArray(quoteCurrency)];
    int i = INIT_LENDING_MARKET_DISCRIMINATOR.write(_data, 0);
    Borsh.writeArrayChecked(quoteCurrency, 32, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record InitLendingMarketIxData(Discriminator discriminator, byte[] quoteCurrency) implements Borsh {  

    public static InitLendingMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int QUOTE_CURRENCY_LEN = 32;

    public static InitLendingMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var quoteCurrency = new byte[32];
      Borsh.readArray(quoteCurrency, _data, i);
      return new InitLendingMarketIxData(discriminator, quoteCurrency);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(quoteCurrency, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_LENDING_MARKET_DISCRIMINATOR = toDiscriminator(209, 157, 53, 210, 97, 180, 31, 45);

  public static List<AccountMeta> updateLendingMarketKeys(final PublicKey lendingMarketOwnerKey,
                                                          final PublicKey lendingMarketKey) {
    return List.of(
      createReadOnlySigner(lendingMarketOwnerKey),
      createWrite(lendingMarketKey)
    );
  }

  public static Instruction updateLendingMarket(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final PublicKey lendingMarketOwnerKey,
                                                final PublicKey lendingMarketKey,
                                                final long mode,
                                                final byte[] value) {
    final var keys = updateLendingMarketKeys(
      lendingMarketOwnerKey,
      lendingMarketKey
    );
    return updateLendingMarket(invokedKaminoLendingProgramMeta, keys, mode, value);
  }

  public static Instruction updateLendingMarket(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final List<AccountMeta> keys,
                                                final long mode,
                                                final byte[] value) {
    final byte[] _data = new byte[16 + Borsh.lenArray(value)];
    int i = UPDATE_LENDING_MARKET_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, mode);
    i += 8;
    Borsh.writeArrayChecked(value, 72, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record UpdateLendingMarketIxData(Discriminator discriminator, long mode, byte[] value) implements Borsh {  

    public static UpdateLendingMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 88;
    public static final int VALUE_LEN = 72;

    public static UpdateLendingMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mode = getInt64LE(_data, i);
      i += 8;
      final var value = new byte[72];
      Borsh.readArray(value, _data, i);
      return new UpdateLendingMarketIxData(discriminator, mode, value);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, mode);
      i += 8;
      i += Borsh.writeArrayChecked(value, 72, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_LENDING_MARKET_OWNER_DISCRIMINATOR = toDiscriminator(118, 224, 10, 62, 196, 230, 184, 89);

  public static List<AccountMeta> updateLendingMarketOwnerKeys(final PublicKey lendingMarketOwnerCachedKey,
                                                               final PublicKey lendingMarketKey) {
    return List.of(
      createReadOnlySigner(lendingMarketOwnerCachedKey),
      createWrite(lendingMarketKey)
    );
  }

  public static Instruction updateLendingMarketOwner(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final PublicKey lendingMarketOwnerCachedKey,
                                                     final PublicKey lendingMarketKey) {
    final var keys = updateLendingMarketOwnerKeys(
      lendingMarketOwnerCachedKey,
      lendingMarketKey
    );
    return updateLendingMarketOwner(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction updateLendingMarketOwner(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, UPDATE_LENDING_MARKET_OWNER_DISCRIMINATOR);
  }

  public static final Discriminator INIT_RESERVE_DISCRIMINATOR = toDiscriminator(138, 245, 71, 225, 153, 4, 3, 43);

  public static List<AccountMeta> initReserveKeys(final PublicKey signerKey,
                                                  final PublicKey lendingMarketKey,
                                                  final PublicKey lendingMarketAuthorityKey,
                                                  final PublicKey reserveKey,
                                                  final PublicKey reserveLiquidityMintKey,
                                                  final PublicKey reserveLiquiditySupplyKey,
                                                  final PublicKey feeReceiverKey,
                                                  final PublicKey reserveCollateralMintKey,
                                                  final PublicKey reserveCollateralSupplyKey,
                                                  final PublicKey initialLiquiditySourceKey,
                                                  final PublicKey rentKey,
                                                  final PublicKey liquidityTokenProgramKey,
                                                  final PublicKey collateralTokenProgramKey,
                                                  final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(feeReceiverKey),
      createWrite(reserveCollateralMintKey),
      createWrite(reserveCollateralSupplyKey),
      createWrite(initialLiquiditySourceKey),
      createRead(rentKey),
      createRead(liquidityTokenProgramKey),
      createRead(collateralTokenProgramKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                        final PublicKey signerKey,
                                        final PublicKey lendingMarketKey,
                                        final PublicKey lendingMarketAuthorityKey,
                                        final PublicKey reserveKey,
                                        final PublicKey reserveLiquidityMintKey,
                                        final PublicKey reserveLiquiditySupplyKey,
                                        final PublicKey feeReceiverKey,
                                        final PublicKey reserveCollateralMintKey,
                                        final PublicKey reserveCollateralSupplyKey,
                                        final PublicKey initialLiquiditySourceKey,
                                        final PublicKey rentKey,
                                        final PublicKey liquidityTokenProgramKey,
                                        final PublicKey collateralTokenProgramKey,
                                        final PublicKey systemProgramKey) {
    final var keys = initReserveKeys(
      signerKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      reserveKey,
      reserveLiquidityMintKey,
      reserveLiquiditySupplyKey,
      feeReceiverKey,
      reserveCollateralMintKey,
      reserveCollateralSupplyKey,
      initialLiquiditySourceKey,
      rentKey,
      liquidityTokenProgramKey,
      collateralTokenProgramKey,
      systemProgramKey
    );
    return initReserve(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction initReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, INIT_RESERVE_DISCRIMINATOR);
  }

  public static final Discriminator INIT_FARMS_FOR_RESERVE_DISCRIMINATOR = toDiscriminator(218, 6, 62, 233, 1, 33, 232, 82);

  public static List<AccountMeta> initFarmsForReserveKeys(final PublicKey lendingMarketOwnerKey,
                                                          final PublicKey lendingMarketKey,
                                                          final PublicKey lendingMarketAuthorityKey,
                                                          final PublicKey reserveKey,
                                                          final PublicKey farmsProgramKey,
                                                          final PublicKey farmsGlobalConfigKey,
                                                          final PublicKey farmStateKey,
                                                          final PublicKey farmsVaultAuthorityKey,
                                                          final PublicKey rentKey,
                                                          final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(lendingMarketOwnerKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(reserveKey),
      createRead(farmsProgramKey),
      createRead(farmsGlobalConfigKey),
      createWrite(farmStateKey),
      createRead(farmsVaultAuthorityKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initFarmsForReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final PublicKey lendingMarketOwnerKey,
                                                final PublicKey lendingMarketKey,
                                                final PublicKey lendingMarketAuthorityKey,
                                                final PublicKey reserveKey,
                                                final PublicKey farmsProgramKey,
                                                final PublicKey farmsGlobalConfigKey,
                                                final PublicKey farmStateKey,
                                                final PublicKey farmsVaultAuthorityKey,
                                                final PublicKey rentKey,
                                                final PublicKey systemProgramKey,
                                                final int mode) {
    final var keys = initFarmsForReserveKeys(
      lendingMarketOwnerKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      reserveKey,
      farmsProgramKey,
      farmsGlobalConfigKey,
      farmStateKey,
      farmsVaultAuthorityKey,
      rentKey,
      systemProgramKey
    );
    return initFarmsForReserve(invokedKaminoLendingProgramMeta, keys, mode);
  }

  public static Instruction initFarmsForReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final List<AccountMeta> keys,
                                                final int mode) {
    final byte[] _data = new byte[9];
    int i = INIT_FARMS_FOR_RESERVE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) mode;

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record InitFarmsForReserveIxData(Discriminator discriminator, int mode) implements Borsh {  

    public static InitFarmsForReserveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static InitFarmsForReserveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mode = _data[i] & 0xFF;
      return new InitFarmsForReserveIxData(discriminator, mode);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) mode;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_RESERVE_CONFIG_DISCRIMINATOR = toDiscriminator(61, 148, 100, 70, 143, 107, 17, 13);

  public static List<AccountMeta> updateReserveConfigKeys(final PublicKey signerKey,
                                                          final PublicKey globalConfigKey,
                                                          final PublicKey lendingMarketKey,
                                                          final PublicKey reserveKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createRead(globalConfigKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey)
    );
  }

  public static Instruction updateReserveConfig(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final PublicKey signerKey,
                                                final PublicKey globalConfigKey,
                                                final PublicKey lendingMarketKey,
                                                final PublicKey reserveKey,
                                                final UpdateConfigMode mode,
                                                final byte[] value,
                                                final boolean skipConfigIntegrityValidation) {
    final var keys = updateReserveConfigKeys(
      signerKey,
      globalConfigKey,
      lendingMarketKey,
      reserveKey
    );
    return updateReserveConfig(
      invokedKaminoLendingProgramMeta,
      keys,
      mode,
      value,
      skipConfigIntegrityValidation
    );
  }

  public static Instruction updateReserveConfig(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final List<AccountMeta> keys,
                                                final UpdateConfigMode mode,
                                                final byte[] value,
                                                final boolean skipConfigIntegrityValidation) {
    final byte[] _data = new byte[9 + Borsh.len(mode) + Borsh.lenVector(value)];
    int i = UPDATE_RESERVE_CONFIG_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(mode, _data, i);
    i += Borsh.writeVector(value, _data, i);
    _data[i] = (byte) (skipConfigIntegrityValidation ? 1 : 0);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record UpdateReserveConfigIxData(Discriminator discriminator,
                                          UpdateConfigMode mode,
                                          byte[] value,
                                          boolean skipConfigIntegrityValidation) implements Borsh {  

    public static UpdateReserveConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateReserveConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mode = UpdateConfigMode.read(_data, i);
      i += Borsh.len(mode);
      final var value = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(value);
      final var skipConfigIntegrityValidation = _data[i] == 1;
      return new UpdateReserveConfigIxData(discriminator, mode, value, skipConfigIntegrityValidation);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(mode, _data, i);
      i += Borsh.writeVector(value, _data, i);
      _data[i] = (byte) (skipConfigIntegrityValidation ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(mode) + Borsh.lenVector(value) + 1;
    }
  }

  public static final Discriminator REDEEM_FEES_DISCRIMINATOR = toDiscriminator(215, 39, 180, 41, 173, 46, 248, 220);

  public static List<AccountMeta> redeemFeesKeys(final PublicKey reserveKey,
                                                 final PublicKey reserveLiquidityMintKey,
                                                 final PublicKey reserveLiquidityFeeReceiverKey,
                                                 final PublicKey reserveSupplyLiquidityKey,
                                                 final PublicKey lendingMarketKey,
                                                 final PublicKey lendingMarketAuthorityKey,
                                                 final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveLiquidityFeeReceiverKey),
      createWrite(reserveSupplyLiquidityKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction redeemFees(final AccountMeta invokedKaminoLendingProgramMeta,
                                       final PublicKey reserveKey,
                                       final PublicKey reserveLiquidityMintKey,
                                       final PublicKey reserveLiquidityFeeReceiverKey,
                                       final PublicKey reserveSupplyLiquidityKey,
                                       final PublicKey lendingMarketKey,
                                       final PublicKey lendingMarketAuthorityKey,
                                       final PublicKey tokenProgramKey) {
    final var keys = redeemFeesKeys(
      reserveKey,
      reserveLiquidityMintKey,
      reserveLiquidityFeeReceiverKey,
      reserveSupplyLiquidityKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      tokenProgramKey
    );
    return redeemFees(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction redeemFees(final AccountMeta invokedKaminoLendingProgramMeta,
                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, REDEEM_FEES_DISCRIMINATOR);
  }

  public static final Discriminator WITHDRAW_PROTOCOL_FEE_DISCRIMINATOR = toDiscriminator(158, 201, 158, 189, 33, 93, 162, 103);

  public static List<AccountMeta> withdrawProtocolFeeKeys(final PublicKey globalConfigKey,
                                                          final PublicKey lendingMarketKey,
                                                          final PublicKey reserveKey,
                                                          final PublicKey reserveLiquidityMintKey,
                                                          final PublicKey lendingMarketAuthorityKey,
                                                          final PublicKey feeVaultKey,
                                                          final PublicKey feeCollectorAtaKey,
                                                          final PublicKey tokenProgramKey) {
    return List.of(
      createRead(globalConfigKey),
      createRead(lendingMarketKey),
      createRead(reserveKey),
      createRead(reserveLiquidityMintKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(feeVaultKey),
      createWrite(feeCollectorAtaKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction withdrawProtocolFee(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final PublicKey globalConfigKey,
                                                final PublicKey lendingMarketKey,
                                                final PublicKey reserveKey,
                                                final PublicKey reserveLiquidityMintKey,
                                                final PublicKey lendingMarketAuthorityKey,
                                                final PublicKey feeVaultKey,
                                                final PublicKey feeCollectorAtaKey,
                                                final PublicKey tokenProgramKey,
                                                final long amount) {
    final var keys = withdrawProtocolFeeKeys(
      globalConfigKey,
      lendingMarketKey,
      reserveKey,
      reserveLiquidityMintKey,
      lendingMarketAuthorityKey,
      feeVaultKey,
      feeCollectorAtaKey,
      tokenProgramKey
    );
    return withdrawProtocolFee(invokedKaminoLendingProgramMeta, keys, amount);
  }

  public static Instruction withdrawProtocolFee(final AccountMeta invokedKaminoLendingProgramMeta,
                                                final List<AccountMeta> keys,
                                                final long amount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_PROTOCOL_FEE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record WithdrawProtocolFeeIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static WithdrawProtocolFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawProtocolFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new WithdrawProtocolFeeIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SEED_DEPOSIT_ON_INIT_RESERVE_DISCRIMINATOR = toDiscriminator(254, 197, 228, 118, 183, 206, 62, 226);

  public static List<AccountMeta> seedDepositOnInitReserveKeys(final PublicKey signerKey,
                                                               final PublicKey lendingMarketKey,
                                                               final PublicKey reserveKey,
                                                               final PublicKey reserveLiquidityMintKey,
                                                               final PublicKey reserveLiquiditySupplyKey,
                                                               final PublicKey initialLiquiditySourceKey,
                                                               final PublicKey liquidityTokenProgramKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(initialLiquiditySourceKey),
      createRead(liquidityTokenProgramKey)
    );
  }

  public static Instruction seedDepositOnInitReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final PublicKey signerKey,
                                                     final PublicKey lendingMarketKey,
                                                     final PublicKey reserveKey,
                                                     final PublicKey reserveLiquidityMintKey,
                                                     final PublicKey reserveLiquiditySupplyKey,
                                                     final PublicKey initialLiquiditySourceKey,
                                                     final PublicKey liquidityTokenProgramKey) {
    final var keys = seedDepositOnInitReserveKeys(
      signerKey,
      lendingMarketKey,
      reserveKey,
      reserveLiquidityMintKey,
      reserveLiquiditySupplyKey,
      initialLiquiditySourceKey,
      liquidityTokenProgramKey
    );
    return seedDepositOnInitReserve(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction seedDepositOnInitReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, SEED_DEPOSIT_ON_INIT_RESERVE_DISCRIMINATOR);
  }

  public static final Discriminator SOCIALIZE_LOSS_DISCRIMINATOR = toDiscriminator(245, 75, 91, 0, 236, 97, 19, 3);

  public static List<AccountMeta> socializeLossKeys(final PublicKey riskCouncilKey,
                                                    final PublicKey obligationKey,
                                                    final PublicKey lendingMarketKey,
                                                    final PublicKey reserveKey,
                                                    final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(riskCouncilKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction socializeLoss(final AccountMeta invokedKaminoLendingProgramMeta,
                                          final PublicKey riskCouncilKey,
                                          final PublicKey obligationKey,
                                          final PublicKey lendingMarketKey,
                                          final PublicKey reserveKey,
                                          final PublicKey instructionSysvarAccountKey,
                                          final long liquidityAmount) {
    final var keys = socializeLossKeys(
      riskCouncilKey,
      obligationKey,
      lendingMarketKey,
      reserveKey,
      instructionSysvarAccountKey
    );
    return socializeLoss(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction socializeLoss(final AccountMeta invokedKaminoLendingProgramMeta,
                                          final List<AccountMeta> keys,
                                          final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = SOCIALIZE_LOSS_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record SocializeLossIxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static SocializeLossIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SocializeLossIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new SocializeLossIxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SOCIALIZE_LOSS_V_2_DISCRIMINATOR = toDiscriminator(238, 95, 98, 220, 187, 40, 204, 154);

  public static List<AccountMeta> socializeLossV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                      final PublicKey socializeLossAccountsRiskCouncilKey,
                                                      final PublicKey socializeLossAccountsObligationKey,
                                                      final PublicKey socializeLossAccountsLendingMarketKey,
                                                      final PublicKey socializeLossAccountsReserveKey,
                                                      final PublicKey socializeLossAccountsInstructionSysvarAccountKey,
                                                      final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                      final PublicKey farmsAccountsReserveFarmStateKey,
                                                      final PublicKey lendingMarketAuthorityKey,
                                                      final PublicKey farmsProgramKey) {
    return List.of(
      createReadOnlySigner(socializeLossAccountsRiskCouncilKey),
      createWrite(socializeLossAccountsObligationKey),
      createRead(socializeLossAccountsLendingMarketKey),
      createWrite(socializeLossAccountsReserveKey),
      createRead(socializeLossAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(lendingMarketAuthorityKey),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction socializeLossV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                            final PublicKey socializeLossAccountsRiskCouncilKey,
                                            final PublicKey socializeLossAccountsObligationKey,
                                            final PublicKey socializeLossAccountsLendingMarketKey,
                                            final PublicKey socializeLossAccountsReserveKey,
                                            final PublicKey socializeLossAccountsInstructionSysvarAccountKey,
                                            final PublicKey farmsAccountsObligationFarmUserStateKey,
                                            final PublicKey farmsAccountsReserveFarmStateKey,
                                            final PublicKey lendingMarketAuthorityKey,
                                            final PublicKey farmsProgramKey,
                                            final long liquidityAmount) {
    final var keys = socializeLossV2Keys(
      invokedKaminoLendingProgramMeta,
      socializeLossAccountsRiskCouncilKey,
      socializeLossAccountsObligationKey,
      socializeLossAccountsLendingMarketKey,
      socializeLossAccountsReserveKey,
      socializeLossAccountsInstructionSysvarAccountKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      lendingMarketAuthorityKey,
      farmsProgramKey
    );
    return socializeLossV2(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction socializeLossV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                            final List<AccountMeta> keys,
                                            final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = SOCIALIZE_LOSS_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record SocializeLossV2IxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static SocializeLossV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SocializeLossV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new SocializeLossV2IxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MARK_OBLIGATION_FOR_DELEVERAGING_DISCRIMINATOR = toDiscriminator(164, 35, 182, 19, 0, 116, 243, 127);

  public static List<AccountMeta> markObligationForDeleveragingKeys(final PublicKey riskCouncilKey,
                                                                    final PublicKey obligationKey,
                                                                    final PublicKey lendingMarketKey) {
    return List.of(
      createReadOnlySigner(riskCouncilKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey)
    );
  }

  public static Instruction markObligationForDeleveraging(final AccountMeta invokedKaminoLendingProgramMeta,
                                                          final PublicKey riskCouncilKey,
                                                          final PublicKey obligationKey,
                                                          final PublicKey lendingMarketKey,
                                                          final int autodeleverageTargetLtvPct) {
    final var keys = markObligationForDeleveragingKeys(
      riskCouncilKey,
      obligationKey,
      lendingMarketKey
    );
    return markObligationForDeleveraging(invokedKaminoLendingProgramMeta, keys, autodeleverageTargetLtvPct);
  }

  public static Instruction markObligationForDeleveraging(final AccountMeta invokedKaminoLendingProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final int autodeleverageTargetLtvPct) {
    final byte[] _data = new byte[9];
    int i = MARK_OBLIGATION_FOR_DELEVERAGING_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) autodeleverageTargetLtvPct;

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record MarkObligationForDeleveragingIxData(Discriminator discriminator, int autodeleverageTargetLtvPct) implements Borsh {  

    public static MarkObligationForDeleveragingIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static MarkObligationForDeleveragingIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var autodeleverageTargetLtvPct = _data[i] & 0xFF;
      return new MarkObligationForDeleveragingIxData(discriminator, autodeleverageTargetLtvPct);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) autodeleverageTargetLtvPct;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REFRESH_RESERVE_DISCRIMINATOR = toDiscriminator(2, 218, 138, 235, 79, 201, 25, 102);

  public static List<AccountMeta> refreshReserveKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final PublicKey reserveKey,
                                                     final PublicKey lendingMarketKey,
                                                     final PublicKey pythOracleKey,
                                                     final PublicKey switchboardPriceOracleKey,
                                                     final PublicKey switchboardTwapOracleKey,
                                                     final PublicKey scopePricesKey) {
    return List.of(
      createWrite(reserveKey),
      createRead(lendingMarketKey),
      createRead(requireNonNullElse(pythOracleKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(requireNonNullElse(switchboardPriceOracleKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(requireNonNullElse(switchboardTwapOracleKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(requireNonNullElse(scopePricesKey, invokedKaminoLendingProgramMeta.publicKey()))
    );
  }

  public static Instruction refreshReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                           final PublicKey reserveKey,
                                           final PublicKey lendingMarketKey,
                                           final PublicKey pythOracleKey,
                                           final PublicKey switchboardPriceOracleKey,
                                           final PublicKey switchboardTwapOracleKey,
                                           final PublicKey scopePricesKey) {
    final var keys = refreshReserveKeys(
      invokedKaminoLendingProgramMeta,
      reserveKey,
      lendingMarketKey,
      pythOracleKey,
      switchboardPriceOracleKey,
      switchboardTwapOracleKey,
      scopePricesKey
    );
    return refreshReserve(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction refreshReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, REFRESH_RESERVE_DISCRIMINATOR);
  }

  public static final Discriminator REFRESH_RESERVES_BATCH_DISCRIMINATOR = toDiscriminator(144, 110, 26, 103, 162, 204, 252, 147);

  public static Instruction refreshReservesBatch(final AccountMeta invokedKaminoLendingProgramMeta,
                                                 final boolean skipPriceUpdates) {
    final byte[] _data = new byte[9];
    int i = REFRESH_RESERVES_BATCH_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (skipPriceUpdates ? 1 : 0);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, List.of(), _data);
  }

  public record RefreshReservesBatchIxData(Discriminator discriminator, boolean skipPriceUpdates) implements Borsh {  

    public static RefreshReservesBatchIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static RefreshReservesBatchIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var skipPriceUpdates = _data[i] == 1;
      return new RefreshReservesBatchIxData(discriminator, skipPriceUpdates);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (skipPriceUpdates ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_RESERVE_LIQUIDITY_DISCRIMINATOR = toDiscriminator(169, 201, 30, 126, 6, 205, 102, 68);

  public static List<AccountMeta> depositReserveLiquidityKeys(final PublicKey ownerKey,
                                                              final PublicKey reserveKey,
                                                              final PublicKey lendingMarketKey,
                                                              final PublicKey lendingMarketAuthorityKey,
                                                              final PublicKey reserveLiquidityMintKey,
                                                              final PublicKey reserveLiquiditySupplyKey,
                                                              final PublicKey reserveCollateralMintKey,
                                                              final PublicKey userSourceLiquidityKey,
                                                              final PublicKey userDestinationCollateralKey,
                                                              final PublicKey collateralTokenProgramKey,
                                                              final PublicKey liquidityTokenProgramKey,
                                                              final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(reserveKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(reserveCollateralMintKey),
      createWrite(userSourceLiquidityKey),
      createWrite(userDestinationCollateralKey),
      createRead(collateralTokenProgramKey),
      createRead(liquidityTokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction depositReserveLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                    final PublicKey ownerKey,
                                                    final PublicKey reserveKey,
                                                    final PublicKey lendingMarketKey,
                                                    final PublicKey lendingMarketAuthorityKey,
                                                    final PublicKey reserveLiquidityMintKey,
                                                    final PublicKey reserveLiquiditySupplyKey,
                                                    final PublicKey reserveCollateralMintKey,
                                                    final PublicKey userSourceLiquidityKey,
                                                    final PublicKey userDestinationCollateralKey,
                                                    final PublicKey collateralTokenProgramKey,
                                                    final PublicKey liquidityTokenProgramKey,
                                                    final PublicKey instructionSysvarAccountKey,
                                                    final long liquidityAmount) {
    final var keys = depositReserveLiquidityKeys(
      ownerKey,
      reserveKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      reserveLiquidityMintKey,
      reserveLiquiditySupplyKey,
      reserveCollateralMintKey,
      userSourceLiquidityKey,
      userDestinationCollateralKey,
      collateralTokenProgramKey,
      liquidityTokenProgramKey,
      instructionSysvarAccountKey
    );
    return depositReserveLiquidity(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction depositReserveLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_RESERVE_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record DepositReserveLiquidityIxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static DepositReserveLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositReserveLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new DepositReserveLiquidityIxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REDEEM_RESERVE_COLLATERAL_DISCRIMINATOR = toDiscriminator(234, 117, 181, 125, 185, 142, 220, 29);

  public static List<AccountMeta> redeemReserveCollateralKeys(final PublicKey ownerKey,
                                                              final PublicKey lendingMarketKey,
                                                              final PublicKey reserveKey,
                                                              final PublicKey lendingMarketAuthorityKey,
                                                              final PublicKey reserveLiquidityMintKey,
                                                              final PublicKey reserveCollateralMintKey,
                                                              final PublicKey reserveLiquiditySupplyKey,
                                                              final PublicKey userSourceCollateralKey,
                                                              final PublicKey userDestinationLiquidityKey,
                                                              final PublicKey collateralTokenProgramKey,
                                                              final PublicKey liquidityTokenProgramKey,
                                                              final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey),
      createRead(lendingMarketAuthorityKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveCollateralMintKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(userSourceCollateralKey),
      createWrite(userDestinationLiquidityKey),
      createRead(collateralTokenProgramKey),
      createRead(liquidityTokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction redeemReserveCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                    final PublicKey ownerKey,
                                                    final PublicKey lendingMarketKey,
                                                    final PublicKey reserveKey,
                                                    final PublicKey lendingMarketAuthorityKey,
                                                    final PublicKey reserveLiquidityMintKey,
                                                    final PublicKey reserveCollateralMintKey,
                                                    final PublicKey reserveLiquiditySupplyKey,
                                                    final PublicKey userSourceCollateralKey,
                                                    final PublicKey userDestinationLiquidityKey,
                                                    final PublicKey collateralTokenProgramKey,
                                                    final PublicKey liquidityTokenProgramKey,
                                                    final PublicKey instructionSysvarAccountKey,
                                                    final long collateralAmount) {
    final var keys = redeemReserveCollateralKeys(
      ownerKey,
      lendingMarketKey,
      reserveKey,
      lendingMarketAuthorityKey,
      reserveLiquidityMintKey,
      reserveCollateralMintKey,
      reserveLiquiditySupplyKey,
      userSourceCollateralKey,
      userDestinationLiquidityKey,
      collateralTokenProgramKey,
      liquidityTokenProgramKey,
      instructionSysvarAccountKey
    );
    return redeemReserveCollateral(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction redeemReserveCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = REDEEM_RESERVE_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record RedeemReserveCollateralIxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static RedeemReserveCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static RedeemReserveCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new RedeemReserveCollateralIxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INIT_OBLIGATION_DISCRIMINATOR = toDiscriminator(251, 10, 231, 76, 27, 11, 159, 96);

  public static List<AccountMeta> initObligationKeys(final PublicKey obligationOwnerKey,
                                                     final PublicKey feePayerKey,
                                                     final PublicKey obligationKey,
                                                     final PublicKey lendingMarketKey,
                                                     final PublicKey seed1AccountKey,
                                                     final PublicKey seed2AccountKey,
                                                     final PublicKey ownerUserMetadataKey,
                                                     final PublicKey rentKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(obligationOwnerKey),
      createWritableSigner(feePayerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createRead(seed1AccountKey),
      createRead(seed2AccountKey),
      createRead(ownerUserMetadataKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initObligation(final AccountMeta invokedKaminoLendingProgramMeta,
                                           final PublicKey obligationOwnerKey,
                                           final PublicKey feePayerKey,
                                           final PublicKey obligationKey,
                                           final PublicKey lendingMarketKey,
                                           final PublicKey seed1AccountKey,
                                           final PublicKey seed2AccountKey,
                                           final PublicKey ownerUserMetadataKey,
                                           final PublicKey rentKey,
                                           final PublicKey systemProgramKey,
                                           final InitObligationArgs args) {
    final var keys = initObligationKeys(
      obligationOwnerKey,
      feePayerKey,
      obligationKey,
      lendingMarketKey,
      seed1AccountKey,
      seed2AccountKey,
      ownerUserMetadataKey,
      rentKey,
      systemProgramKey
    );
    return initObligation(invokedKaminoLendingProgramMeta, keys, args);
  }

  public static Instruction initObligation(final AccountMeta invokedKaminoLendingProgramMeta,
                                           final List<AccountMeta> keys,
                                           final InitObligationArgs args) {
    final byte[] _data = new byte[8 + Borsh.len(args)];
    int i = INIT_OBLIGATION_DISCRIMINATOR.write(_data, 0);
    Borsh.write(args, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record InitObligationIxData(Discriminator discriminator, InitObligationArgs args) implements Borsh {  

    public static InitObligationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitObligationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = InitObligationArgs.read(_data, i);
      return new InitObligationIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(args, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INIT_OBLIGATION_FARMS_FOR_RESERVE_DISCRIMINATOR = toDiscriminator(136, 63, 15, 186, 211, 152, 168, 164);

  public static List<AccountMeta> initObligationFarmsForReserveKeys(final PublicKey payerKey,
                                                                    final PublicKey ownerKey,
                                                                    final PublicKey obligationKey,
                                                                    final PublicKey lendingMarketAuthorityKey,
                                                                    final PublicKey reserveKey,
                                                                    final PublicKey reserveFarmStateKey,
                                                                    final PublicKey obligationFarmKey,
                                                                    final PublicKey lendingMarketKey,
                                                                    final PublicKey farmsProgramKey,
                                                                    final PublicKey rentKey,
                                                                    final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createRead(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(reserveKey),
      createWrite(reserveFarmStateKey),
      createWrite(obligationFarmKey),
      createRead(lendingMarketKey),
      createRead(farmsProgramKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initObligationFarmsForReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                          final PublicKey payerKey,
                                                          final PublicKey ownerKey,
                                                          final PublicKey obligationKey,
                                                          final PublicKey lendingMarketAuthorityKey,
                                                          final PublicKey reserveKey,
                                                          final PublicKey reserveFarmStateKey,
                                                          final PublicKey obligationFarmKey,
                                                          final PublicKey lendingMarketKey,
                                                          final PublicKey farmsProgramKey,
                                                          final PublicKey rentKey,
                                                          final PublicKey systemProgramKey,
                                                          final int mode) {
    final var keys = initObligationFarmsForReserveKeys(
      payerKey,
      ownerKey,
      obligationKey,
      lendingMarketAuthorityKey,
      reserveKey,
      reserveFarmStateKey,
      obligationFarmKey,
      lendingMarketKey,
      farmsProgramKey,
      rentKey,
      systemProgramKey
    );
    return initObligationFarmsForReserve(invokedKaminoLendingProgramMeta, keys, mode);
  }

  public static Instruction initObligationFarmsForReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final int mode) {
    final byte[] _data = new byte[9];
    int i = INIT_OBLIGATION_FARMS_FOR_RESERVE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) mode;

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record InitObligationFarmsForReserveIxData(Discriminator discriminator, int mode) implements Borsh {  

    public static InitObligationFarmsForReserveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static InitObligationFarmsForReserveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mode = _data[i] & 0xFF;
      return new InitObligationFarmsForReserveIxData(discriminator, mode);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) mode;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REFRESH_OBLIGATION_FARMS_FOR_RESERVE_DISCRIMINATOR = toDiscriminator(140, 144, 253, 21, 10, 74, 248, 3);

  public static List<AccountMeta> refreshObligationFarmsForReserveKeys(final PublicKey crankKey,
                                                                       final PublicKey baseAccountsObligationKey,
                                                                       final PublicKey baseAccountsLendingMarketAuthorityKey,
                                                                       final PublicKey baseAccountsReserveKey,
                                                                       final PublicKey baseAccountsReserveFarmStateKey,
                                                                       final PublicKey baseAccountsObligationFarmUserStateKey,
                                                                       final PublicKey baseAccountsLendingMarketKey,
                                                                       final PublicKey farmsProgramKey,
                                                                       final PublicKey rentKey,
                                                                       final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(crankKey),
      createRead(baseAccountsObligationKey),
      createRead(baseAccountsLendingMarketAuthorityKey),
      createRead(baseAccountsReserveKey),
      createWrite(baseAccountsReserveFarmStateKey),
      createWrite(baseAccountsObligationFarmUserStateKey),
      createRead(baseAccountsLendingMarketKey),
      createRead(farmsProgramKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction refreshObligationFarmsForReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                             final PublicKey crankKey,
                                                             final PublicKey baseAccountsObligationKey,
                                                             final PublicKey baseAccountsLendingMarketAuthorityKey,
                                                             final PublicKey baseAccountsReserveKey,
                                                             final PublicKey baseAccountsReserveFarmStateKey,
                                                             final PublicKey baseAccountsObligationFarmUserStateKey,
                                                             final PublicKey baseAccountsLendingMarketKey,
                                                             final PublicKey farmsProgramKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey,
                                                             final int mode) {
    final var keys = refreshObligationFarmsForReserveKeys(
      crankKey,
      baseAccountsObligationKey,
      baseAccountsLendingMarketAuthorityKey,
      baseAccountsReserveKey,
      baseAccountsReserveFarmStateKey,
      baseAccountsObligationFarmUserStateKey,
      baseAccountsLendingMarketKey,
      farmsProgramKey,
      rentKey,
      systemProgramKey
    );
    return refreshObligationFarmsForReserve(invokedKaminoLendingProgramMeta, keys, mode);
  }

  public static Instruction refreshObligationFarmsForReserve(final AccountMeta invokedKaminoLendingProgramMeta,
                                                             final List<AccountMeta> keys,
                                                             final int mode) {
    final byte[] _data = new byte[9];
    int i = REFRESH_OBLIGATION_FARMS_FOR_RESERVE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) mode;

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record RefreshObligationFarmsForReserveIxData(Discriminator discriminator, int mode) implements Borsh {  

    public static RefreshObligationFarmsForReserveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static RefreshObligationFarmsForReserveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mode = _data[i] & 0xFF;
      return new RefreshObligationFarmsForReserveIxData(discriminator, mode);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) mode;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REFRESH_OBLIGATION_DISCRIMINATOR = toDiscriminator(33, 132, 147, 228, 151, 192, 72, 89);

  public static List<AccountMeta> refreshObligationKeys(final PublicKey lendingMarketKey,
                                                        final PublicKey obligationKey) {
    return List.of(
      createRead(lendingMarketKey),
      createWrite(obligationKey)
    );
  }

  public static Instruction refreshObligation(final AccountMeta invokedKaminoLendingProgramMeta,
                                              final PublicKey lendingMarketKey,
                                              final PublicKey obligationKey) {
    final var keys = refreshObligationKeys(
      lendingMarketKey,
      obligationKey
    );
    return refreshObligation(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction refreshObligation(final AccountMeta invokedKaminoLendingProgramMeta,
                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, REFRESH_OBLIGATION_DISCRIMINATOR);
  }

  public static final Discriminator DEPOSIT_OBLIGATION_COLLATERAL_DISCRIMINATOR = toDiscriminator(108, 209, 4, 72, 21, 22, 118, 133);

  public static List<AccountMeta> depositObligationCollateralKeys(final PublicKey ownerKey,
                                                                  final PublicKey obligationKey,
                                                                  final PublicKey lendingMarketKey,
                                                                  final PublicKey depositReserveKey,
                                                                  final PublicKey reserveDestinationCollateralKey,
                                                                  final PublicKey userSourceCollateralKey,
                                                                  final PublicKey tokenProgramKey,
                                                                  final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createWrite(depositReserveKey),
      createWrite(reserveDestinationCollateralKey),
      createWrite(userSourceCollateralKey),
      createRead(tokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction depositObligationCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                        final PublicKey ownerKey,
                                                        final PublicKey obligationKey,
                                                        final PublicKey lendingMarketKey,
                                                        final PublicKey depositReserveKey,
                                                        final PublicKey reserveDestinationCollateralKey,
                                                        final PublicKey userSourceCollateralKey,
                                                        final PublicKey tokenProgramKey,
                                                        final PublicKey instructionSysvarAccountKey,
                                                        final long collateralAmount) {
    final var keys = depositObligationCollateralKeys(
      ownerKey,
      obligationKey,
      lendingMarketKey,
      depositReserveKey,
      reserveDestinationCollateralKey,
      userSourceCollateralKey,
      tokenProgramKey,
      instructionSysvarAccountKey
    );
    return depositObligationCollateral(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction depositObligationCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_OBLIGATION_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record DepositObligationCollateralIxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static DepositObligationCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositObligationCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new DepositObligationCollateralIxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_OBLIGATION_COLLATERAL_V_2_DISCRIMINATOR = toDiscriminator(137, 145, 151, 94, 167, 113, 4, 145);

  public static List<AccountMeta> depositObligationCollateralV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                    final PublicKey depositAccountsOwnerKey,
                                                                    final PublicKey depositAccountsObligationKey,
                                                                    final PublicKey depositAccountsLendingMarketKey,
                                                                    final PublicKey depositAccountsDepositReserveKey,
                                                                    final PublicKey depositAccountsReserveDestinationCollateralKey,
                                                                    final PublicKey depositAccountsUserSourceCollateralKey,
                                                                    final PublicKey depositAccountsTokenProgramKey,
                                                                    final PublicKey depositAccountsInstructionSysvarAccountKey,
                                                                    final PublicKey lendingMarketAuthorityKey,
                                                                    final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                    final PublicKey farmsAccountsReserveFarmStateKey,
                                                                    final PublicKey farmsProgramKey) {
    return List.of(
      createReadOnlySigner(depositAccountsOwnerKey),
      createWrite(depositAccountsObligationKey),
      createRead(depositAccountsLendingMarketKey),
      createWrite(depositAccountsDepositReserveKey),
      createWrite(depositAccountsReserveDestinationCollateralKey),
      createWrite(depositAccountsUserSourceCollateralKey),
      createRead(depositAccountsTokenProgramKey),
      createRead(depositAccountsInstructionSysvarAccountKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction depositObligationCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                          final PublicKey depositAccountsOwnerKey,
                                                          final PublicKey depositAccountsObligationKey,
                                                          final PublicKey depositAccountsLendingMarketKey,
                                                          final PublicKey depositAccountsDepositReserveKey,
                                                          final PublicKey depositAccountsReserveDestinationCollateralKey,
                                                          final PublicKey depositAccountsUserSourceCollateralKey,
                                                          final PublicKey depositAccountsTokenProgramKey,
                                                          final PublicKey depositAccountsInstructionSysvarAccountKey,
                                                          final PublicKey lendingMarketAuthorityKey,
                                                          final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                          final PublicKey farmsAccountsReserveFarmStateKey,
                                                          final PublicKey farmsProgramKey,
                                                          final long collateralAmount) {
    final var keys = depositObligationCollateralV2Keys(
      invokedKaminoLendingProgramMeta,
      depositAccountsOwnerKey,
      depositAccountsObligationKey,
      depositAccountsLendingMarketKey,
      depositAccountsDepositReserveKey,
      depositAccountsReserveDestinationCollateralKey,
      depositAccountsUserSourceCollateralKey,
      depositAccountsTokenProgramKey,
      depositAccountsInstructionSysvarAccountKey,
      lendingMarketAuthorityKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return depositObligationCollateralV2(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction depositObligationCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_OBLIGATION_COLLATERAL_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record DepositObligationCollateralV2IxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static DepositObligationCollateralV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositObligationCollateralV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new DepositObligationCollateralV2IxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_OBLIGATION_COLLATERAL_DISCRIMINATOR = toDiscriminator(37, 116, 205, 103, 243, 192, 92, 198);

  public static List<AccountMeta> withdrawObligationCollateralKeys(final PublicKey ownerKey,
                                                                   final PublicKey obligationKey,
                                                                   final PublicKey lendingMarketKey,
                                                                   final PublicKey lendingMarketAuthorityKey,
                                                                   final PublicKey withdrawReserveKey,
                                                                   final PublicKey reserveSourceCollateralKey,
                                                                   final PublicKey userDestinationCollateralKey,
                                                                   final PublicKey tokenProgramKey,
                                                                   final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createWritableSigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(withdrawReserveKey),
      createWrite(reserveSourceCollateralKey),
      createWrite(userDestinationCollateralKey),
      createRead(tokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction withdrawObligationCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                         final PublicKey ownerKey,
                                                         final PublicKey obligationKey,
                                                         final PublicKey lendingMarketKey,
                                                         final PublicKey lendingMarketAuthorityKey,
                                                         final PublicKey withdrawReserveKey,
                                                         final PublicKey reserveSourceCollateralKey,
                                                         final PublicKey userDestinationCollateralKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey instructionSysvarAccountKey,
                                                         final long collateralAmount) {
    final var keys = withdrawObligationCollateralKeys(
      ownerKey,
      obligationKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      withdrawReserveKey,
      reserveSourceCollateralKey,
      userDestinationCollateralKey,
      tokenProgramKey,
      instructionSysvarAccountKey
    );
    return withdrawObligationCollateral(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction withdrawObligationCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_OBLIGATION_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record WithdrawObligationCollateralIxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static WithdrawObligationCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawObligationCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new WithdrawObligationCollateralIxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_OBLIGATION_COLLATERAL_V_2_DISCRIMINATOR = toDiscriminator(202, 249, 117, 114, 231, 192, 47, 138);

  public static List<AccountMeta> withdrawObligationCollateralV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                     final PublicKey withdrawAccountsOwnerKey,
                                                                     final PublicKey withdrawAccountsObligationKey,
                                                                     final PublicKey withdrawAccountsLendingMarketKey,
                                                                     final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                                     final PublicKey withdrawAccountsWithdrawReserveKey,
                                                                     final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                                     final PublicKey withdrawAccountsUserDestinationCollateralKey,
                                                                     final PublicKey withdrawAccountsTokenProgramKey,
                                                                     final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                                     final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                     final PublicKey farmsAccountsReserveFarmStateKey,
                                                                     final PublicKey farmsProgramKey) {
    return List.of(
      createWritableSigner(withdrawAccountsOwnerKey),
      createWrite(withdrawAccountsObligationKey),
      createRead(withdrawAccountsLendingMarketKey),
      createRead(withdrawAccountsLendingMarketAuthorityKey),
      createWrite(withdrawAccountsWithdrawReserveKey),
      createWrite(withdrawAccountsReserveSourceCollateralKey),
      createWrite(withdrawAccountsUserDestinationCollateralKey),
      createRead(withdrawAccountsTokenProgramKey),
      createRead(withdrawAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction withdrawObligationCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                           final PublicKey withdrawAccountsOwnerKey,
                                                           final PublicKey withdrawAccountsObligationKey,
                                                           final PublicKey withdrawAccountsLendingMarketKey,
                                                           final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                           final PublicKey withdrawAccountsWithdrawReserveKey,
                                                           final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                           final PublicKey withdrawAccountsUserDestinationCollateralKey,
                                                           final PublicKey withdrawAccountsTokenProgramKey,
                                                           final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                           final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                           final PublicKey farmsAccountsReserveFarmStateKey,
                                                           final PublicKey farmsProgramKey,
                                                           final long collateralAmount) {
    final var keys = withdrawObligationCollateralV2Keys(
      invokedKaminoLendingProgramMeta,
      withdrawAccountsOwnerKey,
      withdrawAccountsObligationKey,
      withdrawAccountsLendingMarketKey,
      withdrawAccountsLendingMarketAuthorityKey,
      withdrawAccountsWithdrawReserveKey,
      withdrawAccountsReserveSourceCollateralKey,
      withdrawAccountsUserDestinationCollateralKey,
      withdrawAccountsTokenProgramKey,
      withdrawAccountsInstructionSysvarAccountKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return withdrawObligationCollateralV2(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction withdrawObligationCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                           final List<AccountMeta> keys,
                                                           final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_OBLIGATION_COLLATERAL_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record WithdrawObligationCollateralV2IxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static WithdrawObligationCollateralV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawObligationCollateralV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new WithdrawObligationCollateralV2IxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BORROW_OBLIGATION_LIQUIDITY_DISCRIMINATOR = toDiscriminator(121, 127, 18, 204, 73, 245, 225, 65);

  public static List<AccountMeta> borrowObligationLiquidityKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                final PublicKey ownerKey,
                                                                final PublicKey obligationKey,
                                                                final PublicKey lendingMarketKey,
                                                                final PublicKey lendingMarketAuthorityKey,
                                                                final PublicKey borrowReserveKey,
                                                                final PublicKey borrowReserveLiquidityMintKey,
                                                                final PublicKey reserveSourceLiquidityKey,
                                                                final PublicKey borrowReserveLiquidityFeeReceiverKey,
                                                                final PublicKey userDestinationLiquidityKey,
                                                                final PublicKey referrerTokenStateKey,
                                                                final PublicKey tokenProgramKey,
                                                                final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(borrowReserveKey),
      createRead(borrowReserveLiquidityMintKey),
      createWrite(reserveSourceLiquidityKey),
      createWrite(borrowReserveLiquidityFeeReceiverKey),
      createWrite(userDestinationLiquidityKey),
      createWrite(requireNonNullElse(referrerTokenStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(tokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction borrowObligationLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                      final PublicKey ownerKey,
                                                      final PublicKey obligationKey,
                                                      final PublicKey lendingMarketKey,
                                                      final PublicKey lendingMarketAuthorityKey,
                                                      final PublicKey borrowReserveKey,
                                                      final PublicKey borrowReserveLiquidityMintKey,
                                                      final PublicKey reserveSourceLiquidityKey,
                                                      final PublicKey borrowReserveLiquidityFeeReceiverKey,
                                                      final PublicKey userDestinationLiquidityKey,
                                                      final PublicKey referrerTokenStateKey,
                                                      final PublicKey tokenProgramKey,
                                                      final PublicKey instructionSysvarAccountKey,
                                                      final long liquidityAmount) {
    final var keys = borrowObligationLiquidityKeys(
      invokedKaminoLendingProgramMeta,
      ownerKey,
      obligationKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      borrowReserveKey,
      borrowReserveLiquidityMintKey,
      reserveSourceLiquidityKey,
      borrowReserveLiquidityFeeReceiverKey,
      userDestinationLiquidityKey,
      referrerTokenStateKey,
      tokenProgramKey,
      instructionSysvarAccountKey
    );
    return borrowObligationLiquidity(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction borrowObligationLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = BORROW_OBLIGATION_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record BorrowObligationLiquidityIxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static BorrowObligationLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static BorrowObligationLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new BorrowObligationLiquidityIxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BORROW_OBLIGATION_LIQUIDITY_V_2_DISCRIMINATOR = toDiscriminator(161, 128, 143, 245, 171, 199, 194, 6);

  public static List<AccountMeta> borrowObligationLiquidityV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                  final PublicKey borrowAccountsOwnerKey,
                                                                  final PublicKey borrowAccountsObligationKey,
                                                                  final PublicKey borrowAccountsLendingMarketKey,
                                                                  final PublicKey borrowAccountsLendingMarketAuthorityKey,
                                                                  final PublicKey borrowAccountsBorrowReserveKey,
                                                                  final PublicKey borrowAccountsBorrowReserveLiquidityMintKey,
                                                                  final PublicKey borrowAccountsReserveSourceLiquidityKey,
                                                                  final PublicKey borrowAccountsBorrowReserveLiquidityFeeReceiverKey,
                                                                  final PublicKey borrowAccountsUserDestinationLiquidityKey,
                                                                  final PublicKey borrowAccountsReferrerTokenStateKey,
                                                                  final PublicKey borrowAccountsTokenProgramKey,
                                                                  final PublicKey borrowAccountsInstructionSysvarAccountKey,
                                                                  final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                  final PublicKey farmsAccountsReserveFarmStateKey,
                                                                  final PublicKey farmsProgramKey) {
    return List.of(
      createReadOnlySigner(borrowAccountsOwnerKey),
      createWrite(borrowAccountsObligationKey),
      createRead(borrowAccountsLendingMarketKey),
      createRead(borrowAccountsLendingMarketAuthorityKey),
      createWrite(borrowAccountsBorrowReserveKey),
      createRead(borrowAccountsBorrowReserveLiquidityMintKey),
      createWrite(borrowAccountsReserveSourceLiquidityKey),
      createWrite(borrowAccountsBorrowReserveLiquidityFeeReceiverKey),
      createWrite(borrowAccountsUserDestinationLiquidityKey),
      createWrite(requireNonNullElse(borrowAccountsReferrerTokenStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(borrowAccountsTokenProgramKey),
      createRead(borrowAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction borrowObligationLiquidityV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                        final PublicKey borrowAccountsOwnerKey,
                                                        final PublicKey borrowAccountsObligationKey,
                                                        final PublicKey borrowAccountsLendingMarketKey,
                                                        final PublicKey borrowAccountsLendingMarketAuthorityKey,
                                                        final PublicKey borrowAccountsBorrowReserveKey,
                                                        final PublicKey borrowAccountsBorrowReserveLiquidityMintKey,
                                                        final PublicKey borrowAccountsReserveSourceLiquidityKey,
                                                        final PublicKey borrowAccountsBorrowReserveLiquidityFeeReceiverKey,
                                                        final PublicKey borrowAccountsUserDestinationLiquidityKey,
                                                        final PublicKey borrowAccountsReferrerTokenStateKey,
                                                        final PublicKey borrowAccountsTokenProgramKey,
                                                        final PublicKey borrowAccountsInstructionSysvarAccountKey,
                                                        final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                        final PublicKey farmsAccountsReserveFarmStateKey,
                                                        final PublicKey farmsProgramKey,
                                                        final long liquidityAmount) {
    final var keys = borrowObligationLiquidityV2Keys(
      invokedKaminoLendingProgramMeta,
      borrowAccountsOwnerKey,
      borrowAccountsObligationKey,
      borrowAccountsLendingMarketKey,
      borrowAccountsLendingMarketAuthorityKey,
      borrowAccountsBorrowReserveKey,
      borrowAccountsBorrowReserveLiquidityMintKey,
      borrowAccountsReserveSourceLiquidityKey,
      borrowAccountsBorrowReserveLiquidityFeeReceiverKey,
      borrowAccountsUserDestinationLiquidityKey,
      borrowAccountsReferrerTokenStateKey,
      borrowAccountsTokenProgramKey,
      borrowAccountsInstructionSysvarAccountKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return borrowObligationLiquidityV2(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction borrowObligationLiquidityV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = BORROW_OBLIGATION_LIQUIDITY_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record BorrowObligationLiquidityV2IxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static BorrowObligationLiquidityV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static BorrowObligationLiquidityV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new BorrowObligationLiquidityV2IxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REPAY_OBLIGATION_LIQUIDITY_DISCRIMINATOR = toDiscriminator(145, 178, 13, 225, 76, 240, 147, 72);

  public static List<AccountMeta> repayObligationLiquidityKeys(final PublicKey ownerKey,
                                                               final PublicKey obligationKey,
                                                               final PublicKey lendingMarketKey,
                                                               final PublicKey repayReserveKey,
                                                               final PublicKey reserveLiquidityMintKey,
                                                               final PublicKey reserveDestinationLiquidityKey,
                                                               final PublicKey userSourceLiquidityKey,
                                                               final PublicKey tokenProgramKey,
                                                               final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createWrite(repayReserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveDestinationLiquidityKey),
      createWrite(userSourceLiquidityKey),
      createRead(tokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction repayObligationLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final PublicKey ownerKey,
                                                     final PublicKey obligationKey,
                                                     final PublicKey lendingMarketKey,
                                                     final PublicKey repayReserveKey,
                                                     final PublicKey reserveLiquidityMintKey,
                                                     final PublicKey reserveDestinationLiquidityKey,
                                                     final PublicKey userSourceLiquidityKey,
                                                     final PublicKey tokenProgramKey,
                                                     final PublicKey instructionSysvarAccountKey,
                                                     final long liquidityAmount) {
    final var keys = repayObligationLiquidityKeys(
      ownerKey,
      obligationKey,
      lendingMarketKey,
      repayReserveKey,
      reserveLiquidityMintKey,
      reserveDestinationLiquidityKey,
      userSourceLiquidityKey,
      tokenProgramKey,
      instructionSysvarAccountKey
    );
    return repayObligationLiquidity(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction repayObligationLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = REPAY_OBLIGATION_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record RepayObligationLiquidityIxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static RepayObligationLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static RepayObligationLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new RepayObligationLiquidityIxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REPAY_OBLIGATION_LIQUIDITY_V_2_DISCRIMINATOR = toDiscriminator(116, 174, 213, 76, 180, 53, 210, 144);

  public static List<AccountMeta> repayObligationLiquidityV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                 final PublicKey repayAccountsOwnerKey,
                                                                 final PublicKey repayAccountsObligationKey,
                                                                 final PublicKey repayAccountsLendingMarketKey,
                                                                 final PublicKey repayAccountsRepayReserveKey,
                                                                 final PublicKey repayAccountsReserveLiquidityMintKey,
                                                                 final PublicKey repayAccountsReserveDestinationLiquidityKey,
                                                                 final PublicKey repayAccountsUserSourceLiquidityKey,
                                                                 final PublicKey repayAccountsTokenProgramKey,
                                                                 final PublicKey repayAccountsInstructionSysvarAccountKey,
                                                                 final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                 final PublicKey farmsAccountsReserveFarmStateKey,
                                                                 final PublicKey lendingMarketAuthorityKey,
                                                                 final PublicKey farmsProgramKey) {
    return List.of(
      createReadOnlySigner(repayAccountsOwnerKey),
      createWrite(repayAccountsObligationKey),
      createRead(repayAccountsLendingMarketKey),
      createWrite(repayAccountsRepayReserveKey),
      createRead(repayAccountsReserveLiquidityMintKey),
      createWrite(repayAccountsReserveDestinationLiquidityKey),
      createWrite(repayAccountsUserSourceLiquidityKey),
      createRead(repayAccountsTokenProgramKey),
      createRead(repayAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(lendingMarketAuthorityKey),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction repayObligationLiquidityV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                       final PublicKey repayAccountsOwnerKey,
                                                       final PublicKey repayAccountsObligationKey,
                                                       final PublicKey repayAccountsLendingMarketKey,
                                                       final PublicKey repayAccountsRepayReserveKey,
                                                       final PublicKey repayAccountsReserveLiquidityMintKey,
                                                       final PublicKey repayAccountsReserveDestinationLiquidityKey,
                                                       final PublicKey repayAccountsUserSourceLiquidityKey,
                                                       final PublicKey repayAccountsTokenProgramKey,
                                                       final PublicKey repayAccountsInstructionSysvarAccountKey,
                                                       final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                       final PublicKey farmsAccountsReserveFarmStateKey,
                                                       final PublicKey lendingMarketAuthorityKey,
                                                       final PublicKey farmsProgramKey,
                                                       final long liquidityAmount) {
    final var keys = repayObligationLiquidityV2Keys(
      invokedKaminoLendingProgramMeta,
      repayAccountsOwnerKey,
      repayAccountsObligationKey,
      repayAccountsLendingMarketKey,
      repayAccountsRepayReserveKey,
      repayAccountsReserveLiquidityMintKey,
      repayAccountsReserveDestinationLiquidityKey,
      repayAccountsUserSourceLiquidityKey,
      repayAccountsTokenProgramKey,
      repayAccountsInstructionSysvarAccountKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      lendingMarketAuthorityKey,
      farmsProgramKey
    );
    return repayObligationLiquidityV2(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction repayObligationLiquidityV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = REPAY_OBLIGATION_LIQUIDITY_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record RepayObligationLiquidityV2IxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static RepayObligationLiquidityV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static RepayObligationLiquidityV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new RepayObligationLiquidityV2IxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REPAY_AND_WITHDRAW_AND_REDEEM_DISCRIMINATOR = toDiscriminator(2, 54, 152, 3, 148, 96, 109, 218);

  public static List<AccountMeta> repayAndWithdrawAndRedeemKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                final PublicKey repayAccountsOwnerKey,
                                                                final PublicKey repayAccountsObligationKey,
                                                                final PublicKey repayAccountsLendingMarketKey,
                                                                final PublicKey repayAccountsRepayReserveKey,
                                                                final PublicKey repayAccountsReserveLiquidityMintKey,
                                                                final PublicKey repayAccountsReserveDestinationLiquidityKey,
                                                                final PublicKey repayAccountsUserSourceLiquidityKey,
                                                                final PublicKey repayAccountsTokenProgramKey,
                                                                final PublicKey repayAccountsInstructionSysvarAccountKey,
                                                                final PublicKey withdrawAccountsOwnerKey,
                                                                final PublicKey withdrawAccountsObligationKey,
                                                                final PublicKey withdrawAccountsLendingMarketKey,
                                                                final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                                final PublicKey withdrawAccountsWithdrawReserveKey,
                                                                final PublicKey withdrawAccountsReserveLiquidityMintKey,
                                                                final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                                final PublicKey withdrawAccountsReserveCollateralMintKey,
                                                                final PublicKey withdrawAccountsReserveLiquiditySupplyKey,
                                                                final PublicKey withdrawAccountsUserDestinationLiquidityKey,
                                                                final PublicKey withdrawAccountsPlaceholderUserDestinationCollateralKey,
                                                                final PublicKey withdrawAccountsCollateralTokenProgramKey,
                                                                final PublicKey withdrawAccountsLiquidityTokenProgramKey,
                                                                final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                                final PublicKey collateralFarmsAccountsObligationFarmUserStateKey,
                                                                final PublicKey collateralFarmsAccountsReserveFarmStateKey,
                                                                final PublicKey debtFarmsAccountsObligationFarmUserStateKey,
                                                                final PublicKey debtFarmsAccountsReserveFarmStateKey,
                                                                final PublicKey farmsProgramKey) {
    return List.of(
      createReadOnlySigner(repayAccountsOwnerKey),
      createWrite(repayAccountsObligationKey),
      createRead(repayAccountsLendingMarketKey),
      createWrite(repayAccountsRepayReserveKey),
      createRead(repayAccountsReserveLiquidityMintKey),
      createWrite(repayAccountsReserveDestinationLiquidityKey),
      createWrite(repayAccountsUserSourceLiquidityKey),
      createRead(repayAccountsTokenProgramKey),
      createRead(repayAccountsInstructionSysvarAccountKey),
      createWritableSigner(withdrawAccountsOwnerKey),
      createWrite(withdrawAccountsObligationKey),
      createRead(withdrawAccountsLendingMarketKey),
      createRead(withdrawAccountsLendingMarketAuthorityKey),
      createWrite(withdrawAccountsWithdrawReserveKey),
      createRead(withdrawAccountsReserveLiquidityMintKey),
      createWrite(withdrawAccountsReserveSourceCollateralKey),
      createWrite(withdrawAccountsReserveCollateralMintKey),
      createWrite(withdrawAccountsReserveLiquiditySupplyKey),
      createWrite(withdrawAccountsUserDestinationLiquidityKey),
      createRead(requireNonNullElse(withdrawAccountsPlaceholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(withdrawAccountsCollateralTokenProgramKey),
      createRead(withdrawAccountsLiquidityTokenProgramKey),
      createRead(withdrawAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(collateralFarmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(collateralFarmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(debtFarmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(debtFarmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction repayAndWithdrawAndRedeem(final AccountMeta invokedKaminoLendingProgramMeta,
                                                      final PublicKey repayAccountsOwnerKey,
                                                      final PublicKey repayAccountsObligationKey,
                                                      final PublicKey repayAccountsLendingMarketKey,
                                                      final PublicKey repayAccountsRepayReserveKey,
                                                      final PublicKey repayAccountsReserveLiquidityMintKey,
                                                      final PublicKey repayAccountsReserveDestinationLiquidityKey,
                                                      final PublicKey repayAccountsUserSourceLiquidityKey,
                                                      final PublicKey repayAccountsTokenProgramKey,
                                                      final PublicKey repayAccountsInstructionSysvarAccountKey,
                                                      final PublicKey withdrawAccountsOwnerKey,
                                                      final PublicKey withdrawAccountsObligationKey,
                                                      final PublicKey withdrawAccountsLendingMarketKey,
                                                      final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                      final PublicKey withdrawAccountsWithdrawReserveKey,
                                                      final PublicKey withdrawAccountsReserveLiquidityMintKey,
                                                      final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                      final PublicKey withdrawAccountsReserveCollateralMintKey,
                                                      final PublicKey withdrawAccountsReserveLiquiditySupplyKey,
                                                      final PublicKey withdrawAccountsUserDestinationLiquidityKey,
                                                      final PublicKey withdrawAccountsPlaceholderUserDestinationCollateralKey,
                                                      final PublicKey withdrawAccountsCollateralTokenProgramKey,
                                                      final PublicKey withdrawAccountsLiquidityTokenProgramKey,
                                                      final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                      final PublicKey collateralFarmsAccountsObligationFarmUserStateKey,
                                                      final PublicKey collateralFarmsAccountsReserveFarmStateKey,
                                                      final PublicKey debtFarmsAccountsObligationFarmUserStateKey,
                                                      final PublicKey debtFarmsAccountsReserveFarmStateKey,
                                                      final PublicKey farmsProgramKey,
                                                      final long repayAmount,
                                                      final long withdrawCollateralAmount) {
    final var keys = repayAndWithdrawAndRedeemKeys(
      invokedKaminoLendingProgramMeta,
      repayAccountsOwnerKey,
      repayAccountsObligationKey,
      repayAccountsLendingMarketKey,
      repayAccountsRepayReserveKey,
      repayAccountsReserveLiquidityMintKey,
      repayAccountsReserveDestinationLiquidityKey,
      repayAccountsUserSourceLiquidityKey,
      repayAccountsTokenProgramKey,
      repayAccountsInstructionSysvarAccountKey,
      withdrawAccountsOwnerKey,
      withdrawAccountsObligationKey,
      withdrawAccountsLendingMarketKey,
      withdrawAccountsLendingMarketAuthorityKey,
      withdrawAccountsWithdrawReserveKey,
      withdrawAccountsReserveLiquidityMintKey,
      withdrawAccountsReserveSourceCollateralKey,
      withdrawAccountsReserveCollateralMintKey,
      withdrawAccountsReserveLiquiditySupplyKey,
      withdrawAccountsUserDestinationLiquidityKey,
      withdrawAccountsPlaceholderUserDestinationCollateralKey,
      withdrawAccountsCollateralTokenProgramKey,
      withdrawAccountsLiquidityTokenProgramKey,
      withdrawAccountsInstructionSysvarAccountKey,
      collateralFarmsAccountsObligationFarmUserStateKey,
      collateralFarmsAccountsReserveFarmStateKey,
      debtFarmsAccountsObligationFarmUserStateKey,
      debtFarmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return repayAndWithdrawAndRedeem(invokedKaminoLendingProgramMeta, keys, repayAmount, withdrawCollateralAmount);
  }

  public static Instruction repayAndWithdrawAndRedeem(final AccountMeta invokedKaminoLendingProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final long repayAmount,
                                                      final long withdrawCollateralAmount) {
    final byte[] _data = new byte[24];
    int i = REPAY_AND_WITHDRAW_AND_REDEEM_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, repayAmount);
    i += 8;
    putInt64LE(_data, i, withdrawCollateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record RepayAndWithdrawAndRedeemIxData(Discriminator discriminator, long repayAmount, long withdrawCollateralAmount) implements Borsh {  

    public static RepayAndWithdrawAndRedeemIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static RepayAndWithdrawAndRedeemIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var repayAmount = getInt64LE(_data, i);
      i += 8;
      final var withdrawCollateralAmount = getInt64LE(_data, i);
      return new RepayAndWithdrawAndRedeemIxData(discriminator, repayAmount, withdrawCollateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, repayAmount);
      i += 8;
      putInt64LE(_data, i, withdrawCollateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_AND_WITHDRAW_DISCRIMINATOR = toDiscriminator(141, 153, 39, 15, 64, 61, 88, 84);

  public static List<AccountMeta> depositAndWithdrawKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                         final PublicKey depositAccountsOwnerKey,
                                                         final PublicKey depositAccountsObligationKey,
                                                         final PublicKey depositAccountsLendingMarketKey,
                                                         final PublicKey depositAccountsLendingMarketAuthorityKey,
                                                         final PublicKey depositAccountsReserveKey,
                                                         final PublicKey depositAccountsReserveLiquidityMintKey,
                                                         final PublicKey depositAccountsReserveLiquiditySupplyKey,
                                                         final PublicKey depositAccountsReserveCollateralMintKey,
                                                         final PublicKey depositAccountsReserveDestinationDepositCollateralKey,
                                                         final PublicKey depositAccountsUserSourceLiquidityKey,
                                                         final PublicKey depositAccountsPlaceholderUserDestinationCollateralKey,
                                                         final PublicKey depositAccountsCollateralTokenProgramKey,
                                                         final PublicKey depositAccountsLiquidityTokenProgramKey,
                                                         final PublicKey depositAccountsInstructionSysvarAccountKey,
                                                         final PublicKey withdrawAccountsOwnerKey,
                                                         final PublicKey withdrawAccountsObligationKey,
                                                         final PublicKey withdrawAccountsLendingMarketKey,
                                                         final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                         final PublicKey withdrawAccountsWithdrawReserveKey,
                                                         final PublicKey withdrawAccountsReserveLiquidityMintKey,
                                                         final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                         final PublicKey withdrawAccountsReserveCollateralMintKey,
                                                         final PublicKey withdrawAccountsReserveLiquiditySupplyKey,
                                                         final PublicKey withdrawAccountsUserDestinationLiquidityKey,
                                                         final PublicKey withdrawAccountsPlaceholderUserDestinationCollateralKey,
                                                         final PublicKey withdrawAccountsCollateralTokenProgramKey,
                                                         final PublicKey withdrawAccountsLiquidityTokenProgramKey,
                                                         final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                         final PublicKey depositFarmsAccountsObligationFarmUserStateKey,
                                                         final PublicKey depositFarmsAccountsReserveFarmStateKey,
                                                         final PublicKey withdrawFarmsAccountsObligationFarmUserStateKey,
                                                         final PublicKey withdrawFarmsAccountsReserveFarmStateKey,
                                                         final PublicKey farmsProgramKey) {
    return List.of(
      createWritableSigner(depositAccountsOwnerKey),
      createWrite(depositAccountsObligationKey),
      createRead(depositAccountsLendingMarketKey),
      createRead(depositAccountsLendingMarketAuthorityKey),
      createWrite(depositAccountsReserveKey),
      createRead(depositAccountsReserveLiquidityMintKey),
      createWrite(depositAccountsReserveLiquiditySupplyKey),
      createWrite(depositAccountsReserveCollateralMintKey),
      createWrite(depositAccountsReserveDestinationDepositCollateralKey),
      createWrite(depositAccountsUserSourceLiquidityKey),
      createRead(requireNonNullElse(depositAccountsPlaceholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(depositAccountsCollateralTokenProgramKey),
      createRead(depositAccountsLiquidityTokenProgramKey),
      createRead(depositAccountsInstructionSysvarAccountKey),
      createWritableSigner(withdrawAccountsOwnerKey),
      createWrite(withdrawAccountsObligationKey),
      createRead(withdrawAccountsLendingMarketKey),
      createRead(withdrawAccountsLendingMarketAuthorityKey),
      createWrite(withdrawAccountsWithdrawReserveKey),
      createRead(withdrawAccountsReserveLiquidityMintKey),
      createWrite(withdrawAccountsReserveSourceCollateralKey),
      createWrite(withdrawAccountsReserveCollateralMintKey),
      createWrite(withdrawAccountsReserveLiquiditySupplyKey),
      createWrite(withdrawAccountsUserDestinationLiquidityKey),
      createRead(requireNonNullElse(withdrawAccountsPlaceholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(withdrawAccountsCollateralTokenProgramKey),
      createRead(withdrawAccountsLiquidityTokenProgramKey),
      createRead(withdrawAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(depositFarmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(depositFarmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(withdrawFarmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(withdrawFarmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction depositAndWithdraw(final AccountMeta invokedKaminoLendingProgramMeta,
                                               final PublicKey depositAccountsOwnerKey,
                                               final PublicKey depositAccountsObligationKey,
                                               final PublicKey depositAccountsLendingMarketKey,
                                               final PublicKey depositAccountsLendingMarketAuthorityKey,
                                               final PublicKey depositAccountsReserveKey,
                                               final PublicKey depositAccountsReserveLiquidityMintKey,
                                               final PublicKey depositAccountsReserveLiquiditySupplyKey,
                                               final PublicKey depositAccountsReserveCollateralMintKey,
                                               final PublicKey depositAccountsReserveDestinationDepositCollateralKey,
                                               final PublicKey depositAccountsUserSourceLiquidityKey,
                                               final PublicKey depositAccountsPlaceholderUserDestinationCollateralKey,
                                               final PublicKey depositAccountsCollateralTokenProgramKey,
                                               final PublicKey depositAccountsLiquidityTokenProgramKey,
                                               final PublicKey depositAccountsInstructionSysvarAccountKey,
                                               final PublicKey withdrawAccountsOwnerKey,
                                               final PublicKey withdrawAccountsObligationKey,
                                               final PublicKey withdrawAccountsLendingMarketKey,
                                               final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                               final PublicKey withdrawAccountsWithdrawReserveKey,
                                               final PublicKey withdrawAccountsReserveLiquidityMintKey,
                                               final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                               final PublicKey withdrawAccountsReserveCollateralMintKey,
                                               final PublicKey withdrawAccountsReserveLiquiditySupplyKey,
                                               final PublicKey withdrawAccountsUserDestinationLiquidityKey,
                                               final PublicKey withdrawAccountsPlaceholderUserDestinationCollateralKey,
                                               final PublicKey withdrawAccountsCollateralTokenProgramKey,
                                               final PublicKey withdrawAccountsLiquidityTokenProgramKey,
                                               final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                               final PublicKey depositFarmsAccountsObligationFarmUserStateKey,
                                               final PublicKey depositFarmsAccountsReserveFarmStateKey,
                                               final PublicKey withdrawFarmsAccountsObligationFarmUserStateKey,
                                               final PublicKey withdrawFarmsAccountsReserveFarmStateKey,
                                               final PublicKey farmsProgramKey,
                                               final long liquidityAmount,
                                               final long withdrawCollateralAmount) {
    final var keys = depositAndWithdrawKeys(
      invokedKaminoLendingProgramMeta,
      depositAccountsOwnerKey,
      depositAccountsObligationKey,
      depositAccountsLendingMarketKey,
      depositAccountsLendingMarketAuthorityKey,
      depositAccountsReserveKey,
      depositAccountsReserveLiquidityMintKey,
      depositAccountsReserveLiquiditySupplyKey,
      depositAccountsReserveCollateralMintKey,
      depositAccountsReserveDestinationDepositCollateralKey,
      depositAccountsUserSourceLiquidityKey,
      depositAccountsPlaceholderUserDestinationCollateralKey,
      depositAccountsCollateralTokenProgramKey,
      depositAccountsLiquidityTokenProgramKey,
      depositAccountsInstructionSysvarAccountKey,
      withdrawAccountsOwnerKey,
      withdrawAccountsObligationKey,
      withdrawAccountsLendingMarketKey,
      withdrawAccountsLendingMarketAuthorityKey,
      withdrawAccountsWithdrawReserveKey,
      withdrawAccountsReserveLiquidityMintKey,
      withdrawAccountsReserveSourceCollateralKey,
      withdrawAccountsReserveCollateralMintKey,
      withdrawAccountsReserveLiquiditySupplyKey,
      withdrawAccountsUserDestinationLiquidityKey,
      withdrawAccountsPlaceholderUserDestinationCollateralKey,
      withdrawAccountsCollateralTokenProgramKey,
      withdrawAccountsLiquidityTokenProgramKey,
      withdrawAccountsInstructionSysvarAccountKey,
      depositFarmsAccountsObligationFarmUserStateKey,
      depositFarmsAccountsReserveFarmStateKey,
      withdrawFarmsAccountsObligationFarmUserStateKey,
      withdrawFarmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return depositAndWithdraw(invokedKaminoLendingProgramMeta, keys, liquidityAmount, withdrawCollateralAmount);
  }

  public static Instruction depositAndWithdraw(final AccountMeta invokedKaminoLendingProgramMeta,
                                               final List<AccountMeta> keys,
                                               final long liquidityAmount,
                                               final long withdrawCollateralAmount) {
    final byte[] _data = new byte[24];
    int i = DEPOSIT_AND_WITHDRAW_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);
    i += 8;
    putInt64LE(_data, i, withdrawCollateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record DepositAndWithdrawIxData(Discriminator discriminator, long liquidityAmount, long withdrawCollateralAmount) implements Borsh {  

    public static DepositAndWithdrawIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static DepositAndWithdrawIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      i += 8;
      final var withdrawCollateralAmount = getInt64LE(_data, i);
      return new DepositAndWithdrawIxData(discriminator, liquidityAmount, withdrawCollateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      putInt64LE(_data, i, withdrawCollateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_RESERVE_LIQUIDITY_AND_OBLIGATION_COLLATERAL_DISCRIMINATOR = toDiscriminator(129, 199, 4, 2, 222, 39, 26, 46);

  public static List<AccountMeta> depositReserveLiquidityAndObligationCollateralKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                     final PublicKey ownerKey,
                                                                                     final PublicKey obligationKey,
                                                                                     final PublicKey lendingMarketKey,
                                                                                     final PublicKey lendingMarketAuthorityKey,
                                                                                     final PublicKey reserveKey,
                                                                                     final PublicKey reserveLiquidityMintKey,
                                                                                     final PublicKey reserveLiquiditySupplyKey,
                                                                                     final PublicKey reserveCollateralMintKey,
                                                                                     final PublicKey reserveDestinationDepositCollateralKey,
                                                                                     final PublicKey userSourceLiquidityKey,
                                                                                     final PublicKey placeholderUserDestinationCollateralKey,
                                                                                     final PublicKey collateralTokenProgramKey,
                                                                                     final PublicKey liquidityTokenProgramKey,
                                                                                     final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createWritableSigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(reserveCollateralMintKey),
      createWrite(reserveDestinationDepositCollateralKey),
      createWrite(userSourceLiquidityKey),
      createRead(requireNonNullElse(placeholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(collateralTokenProgramKey),
      createRead(liquidityTokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction depositReserveLiquidityAndObligationCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                           final PublicKey ownerKey,
                                                                           final PublicKey obligationKey,
                                                                           final PublicKey lendingMarketKey,
                                                                           final PublicKey lendingMarketAuthorityKey,
                                                                           final PublicKey reserveKey,
                                                                           final PublicKey reserveLiquidityMintKey,
                                                                           final PublicKey reserveLiquiditySupplyKey,
                                                                           final PublicKey reserveCollateralMintKey,
                                                                           final PublicKey reserveDestinationDepositCollateralKey,
                                                                           final PublicKey userSourceLiquidityKey,
                                                                           final PublicKey placeholderUserDestinationCollateralKey,
                                                                           final PublicKey collateralTokenProgramKey,
                                                                           final PublicKey liquidityTokenProgramKey,
                                                                           final PublicKey instructionSysvarAccountKey,
                                                                           final long liquidityAmount) {
    final var keys = depositReserveLiquidityAndObligationCollateralKeys(
      invokedKaminoLendingProgramMeta,
      ownerKey,
      obligationKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      reserveKey,
      reserveLiquidityMintKey,
      reserveLiquiditySupplyKey,
      reserveCollateralMintKey,
      reserveDestinationDepositCollateralKey,
      userSourceLiquidityKey,
      placeholderUserDestinationCollateralKey,
      collateralTokenProgramKey,
      liquidityTokenProgramKey,
      instructionSysvarAccountKey
    );
    return depositReserveLiquidityAndObligationCollateral(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction depositReserveLiquidityAndObligationCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                           final List<AccountMeta> keys,
                                                                           final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_RESERVE_LIQUIDITY_AND_OBLIGATION_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record DepositReserveLiquidityAndObligationCollateralIxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static DepositReserveLiquidityAndObligationCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositReserveLiquidityAndObligationCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new DepositReserveLiquidityAndObligationCollateralIxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_RESERVE_LIQUIDITY_AND_OBLIGATION_COLLATERAL_V_2_DISCRIMINATOR = toDiscriminator(216, 224, 191, 27, 204, 151, 102, 175);

  public static List<AccountMeta> depositReserveLiquidityAndObligationCollateralV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                       final PublicKey depositAccountsOwnerKey,
                                                                                       final PublicKey depositAccountsObligationKey,
                                                                                       final PublicKey depositAccountsLendingMarketKey,
                                                                                       final PublicKey depositAccountsLendingMarketAuthorityKey,
                                                                                       final PublicKey depositAccountsReserveKey,
                                                                                       final PublicKey depositAccountsReserveLiquidityMintKey,
                                                                                       final PublicKey depositAccountsReserveLiquiditySupplyKey,
                                                                                       final PublicKey depositAccountsReserveCollateralMintKey,
                                                                                       final PublicKey depositAccountsReserveDestinationDepositCollateralKey,
                                                                                       final PublicKey depositAccountsUserSourceLiquidityKey,
                                                                                       final PublicKey depositAccountsPlaceholderUserDestinationCollateralKey,
                                                                                       final PublicKey depositAccountsCollateralTokenProgramKey,
                                                                                       final PublicKey depositAccountsLiquidityTokenProgramKey,
                                                                                       final PublicKey depositAccountsInstructionSysvarAccountKey,
                                                                                       final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                                       final PublicKey farmsAccountsReserveFarmStateKey,
                                                                                       final PublicKey farmsProgramKey) {
    return List.of(
      createWritableSigner(depositAccountsOwnerKey),
      createWrite(depositAccountsObligationKey),
      createRead(depositAccountsLendingMarketKey),
      createRead(depositAccountsLendingMarketAuthorityKey),
      createWrite(depositAccountsReserveKey),
      createRead(depositAccountsReserveLiquidityMintKey),
      createWrite(depositAccountsReserveLiquiditySupplyKey),
      createWrite(depositAccountsReserveCollateralMintKey),
      createWrite(depositAccountsReserveDestinationDepositCollateralKey),
      createWrite(depositAccountsUserSourceLiquidityKey),
      createRead(requireNonNullElse(depositAccountsPlaceholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(depositAccountsCollateralTokenProgramKey),
      createRead(depositAccountsLiquidityTokenProgramKey),
      createRead(depositAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction depositReserveLiquidityAndObligationCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                             final PublicKey depositAccountsOwnerKey,
                                                                             final PublicKey depositAccountsObligationKey,
                                                                             final PublicKey depositAccountsLendingMarketKey,
                                                                             final PublicKey depositAccountsLendingMarketAuthorityKey,
                                                                             final PublicKey depositAccountsReserveKey,
                                                                             final PublicKey depositAccountsReserveLiquidityMintKey,
                                                                             final PublicKey depositAccountsReserveLiquiditySupplyKey,
                                                                             final PublicKey depositAccountsReserveCollateralMintKey,
                                                                             final PublicKey depositAccountsReserveDestinationDepositCollateralKey,
                                                                             final PublicKey depositAccountsUserSourceLiquidityKey,
                                                                             final PublicKey depositAccountsPlaceholderUserDestinationCollateralKey,
                                                                             final PublicKey depositAccountsCollateralTokenProgramKey,
                                                                             final PublicKey depositAccountsLiquidityTokenProgramKey,
                                                                             final PublicKey depositAccountsInstructionSysvarAccountKey,
                                                                             final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                             final PublicKey farmsAccountsReserveFarmStateKey,
                                                                             final PublicKey farmsProgramKey,
                                                                             final long liquidityAmount) {
    final var keys = depositReserveLiquidityAndObligationCollateralV2Keys(
      invokedKaminoLendingProgramMeta,
      depositAccountsOwnerKey,
      depositAccountsObligationKey,
      depositAccountsLendingMarketKey,
      depositAccountsLendingMarketAuthorityKey,
      depositAccountsReserveKey,
      depositAccountsReserveLiquidityMintKey,
      depositAccountsReserveLiquiditySupplyKey,
      depositAccountsReserveCollateralMintKey,
      depositAccountsReserveDestinationDepositCollateralKey,
      depositAccountsUserSourceLiquidityKey,
      depositAccountsPlaceholderUserDestinationCollateralKey,
      depositAccountsCollateralTokenProgramKey,
      depositAccountsLiquidityTokenProgramKey,
      depositAccountsInstructionSysvarAccountKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return depositReserveLiquidityAndObligationCollateralV2(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction depositReserveLiquidityAndObligationCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                             final List<AccountMeta> keys,
                                                                             final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_RESERVE_LIQUIDITY_AND_OBLIGATION_COLLATERAL_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record DepositReserveLiquidityAndObligationCollateralV2IxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static DepositReserveLiquidityAndObligationCollateralV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositReserveLiquidityAndObligationCollateralV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new DepositReserveLiquidityAndObligationCollateralV2IxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_OBLIGATION_COLLATERAL_AND_REDEEM_RESERVE_COLLATERAL_DISCRIMINATOR = toDiscriminator(75, 93, 93, 220, 34, 150, 218, 196);

  public static List<AccountMeta> withdrawObligationCollateralAndRedeemReserveCollateralKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                             final PublicKey ownerKey,
                                                                                             final PublicKey obligationKey,
                                                                                             final PublicKey lendingMarketKey,
                                                                                             final PublicKey lendingMarketAuthorityKey,
                                                                                             final PublicKey withdrawReserveKey,
                                                                                             final PublicKey reserveLiquidityMintKey,
                                                                                             final PublicKey reserveSourceCollateralKey,
                                                                                             final PublicKey reserveCollateralMintKey,
                                                                                             final PublicKey reserveLiquiditySupplyKey,
                                                                                             final PublicKey userDestinationLiquidityKey,
                                                                                             final PublicKey placeholderUserDestinationCollateralKey,
                                                                                             final PublicKey collateralTokenProgramKey,
                                                                                             final PublicKey liquidityTokenProgramKey,
                                                                                             final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createWritableSigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(withdrawReserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveSourceCollateralKey),
      createWrite(reserveCollateralMintKey),
      createWrite(reserveLiquiditySupplyKey),
      createWrite(userDestinationLiquidityKey),
      createRead(requireNonNullElse(placeholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(collateralTokenProgramKey),
      createRead(liquidityTokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction withdrawObligationCollateralAndRedeemReserveCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                   final PublicKey ownerKey,
                                                                                   final PublicKey obligationKey,
                                                                                   final PublicKey lendingMarketKey,
                                                                                   final PublicKey lendingMarketAuthorityKey,
                                                                                   final PublicKey withdrawReserveKey,
                                                                                   final PublicKey reserveLiquidityMintKey,
                                                                                   final PublicKey reserveSourceCollateralKey,
                                                                                   final PublicKey reserveCollateralMintKey,
                                                                                   final PublicKey reserveLiquiditySupplyKey,
                                                                                   final PublicKey userDestinationLiquidityKey,
                                                                                   final PublicKey placeholderUserDestinationCollateralKey,
                                                                                   final PublicKey collateralTokenProgramKey,
                                                                                   final PublicKey liquidityTokenProgramKey,
                                                                                   final PublicKey instructionSysvarAccountKey,
                                                                                   final long collateralAmount) {
    final var keys = withdrawObligationCollateralAndRedeemReserveCollateralKeys(
      invokedKaminoLendingProgramMeta,
      ownerKey,
      obligationKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      withdrawReserveKey,
      reserveLiquidityMintKey,
      reserveSourceCollateralKey,
      reserveCollateralMintKey,
      reserveLiquiditySupplyKey,
      userDestinationLiquidityKey,
      placeholderUserDestinationCollateralKey,
      collateralTokenProgramKey,
      liquidityTokenProgramKey,
      instructionSysvarAccountKey
    );
    return withdrawObligationCollateralAndRedeemReserveCollateral(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction withdrawObligationCollateralAndRedeemReserveCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                   final List<AccountMeta> keys,
                                                                                   final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_OBLIGATION_COLLATERAL_AND_REDEEM_RESERVE_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record WithdrawObligationCollateralAndRedeemReserveCollateralIxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static WithdrawObligationCollateralAndRedeemReserveCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawObligationCollateralAndRedeemReserveCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new WithdrawObligationCollateralAndRedeemReserveCollateralIxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_OBLIGATION_COLLATERAL_AND_REDEEM_RESERVE_COLLATERAL_V_2_DISCRIMINATOR = toDiscriminator(235, 52, 119, 152, 149, 197, 20, 7);

  public static List<AccountMeta> withdrawObligationCollateralAndRedeemReserveCollateralV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                               final PublicKey withdrawAccountsOwnerKey,
                                                                                               final PublicKey withdrawAccountsObligationKey,
                                                                                               final PublicKey withdrawAccountsLendingMarketKey,
                                                                                               final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                                                               final PublicKey withdrawAccountsWithdrawReserveKey,
                                                                                               final PublicKey withdrawAccountsReserveLiquidityMintKey,
                                                                                               final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                                                               final PublicKey withdrawAccountsReserveCollateralMintKey,
                                                                                               final PublicKey withdrawAccountsReserveLiquiditySupplyKey,
                                                                                               final PublicKey withdrawAccountsUserDestinationLiquidityKey,
                                                                                               final PublicKey withdrawAccountsPlaceholderUserDestinationCollateralKey,
                                                                                               final PublicKey withdrawAccountsCollateralTokenProgramKey,
                                                                                               final PublicKey withdrawAccountsLiquidityTokenProgramKey,
                                                                                               final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                                                               final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                                               final PublicKey farmsAccountsReserveFarmStateKey,
                                                                                               final PublicKey farmsProgramKey) {
    return List.of(
      createWritableSigner(withdrawAccountsOwnerKey),
      createWrite(withdrawAccountsObligationKey),
      createRead(withdrawAccountsLendingMarketKey),
      createRead(withdrawAccountsLendingMarketAuthorityKey),
      createWrite(withdrawAccountsWithdrawReserveKey),
      createRead(withdrawAccountsReserveLiquidityMintKey),
      createWrite(withdrawAccountsReserveSourceCollateralKey),
      createWrite(withdrawAccountsReserveCollateralMintKey),
      createWrite(withdrawAccountsReserveLiquiditySupplyKey),
      createWrite(withdrawAccountsUserDestinationLiquidityKey),
      createRead(requireNonNullElse(withdrawAccountsPlaceholderUserDestinationCollateralKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(withdrawAccountsCollateralTokenProgramKey),
      createRead(withdrawAccountsLiquidityTokenProgramKey),
      createRead(withdrawAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(farmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(farmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction withdrawObligationCollateralAndRedeemReserveCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                     final PublicKey withdrawAccountsOwnerKey,
                                                                                     final PublicKey withdrawAccountsObligationKey,
                                                                                     final PublicKey withdrawAccountsLendingMarketKey,
                                                                                     final PublicKey withdrawAccountsLendingMarketAuthorityKey,
                                                                                     final PublicKey withdrawAccountsWithdrawReserveKey,
                                                                                     final PublicKey withdrawAccountsReserveLiquidityMintKey,
                                                                                     final PublicKey withdrawAccountsReserveSourceCollateralKey,
                                                                                     final PublicKey withdrawAccountsReserveCollateralMintKey,
                                                                                     final PublicKey withdrawAccountsReserveLiquiditySupplyKey,
                                                                                     final PublicKey withdrawAccountsUserDestinationLiquidityKey,
                                                                                     final PublicKey withdrawAccountsPlaceholderUserDestinationCollateralKey,
                                                                                     final PublicKey withdrawAccountsCollateralTokenProgramKey,
                                                                                     final PublicKey withdrawAccountsLiquidityTokenProgramKey,
                                                                                     final PublicKey withdrawAccountsInstructionSysvarAccountKey,
                                                                                     final PublicKey farmsAccountsObligationFarmUserStateKey,
                                                                                     final PublicKey farmsAccountsReserveFarmStateKey,
                                                                                     final PublicKey farmsProgramKey,
                                                                                     final long collateralAmount) {
    final var keys = withdrawObligationCollateralAndRedeemReserveCollateralV2Keys(
      invokedKaminoLendingProgramMeta,
      withdrawAccountsOwnerKey,
      withdrawAccountsObligationKey,
      withdrawAccountsLendingMarketKey,
      withdrawAccountsLendingMarketAuthorityKey,
      withdrawAccountsWithdrawReserveKey,
      withdrawAccountsReserveLiquidityMintKey,
      withdrawAccountsReserveSourceCollateralKey,
      withdrawAccountsReserveCollateralMintKey,
      withdrawAccountsReserveLiquiditySupplyKey,
      withdrawAccountsUserDestinationLiquidityKey,
      withdrawAccountsPlaceholderUserDestinationCollateralKey,
      withdrawAccountsCollateralTokenProgramKey,
      withdrawAccountsLiquidityTokenProgramKey,
      withdrawAccountsInstructionSysvarAccountKey,
      farmsAccountsObligationFarmUserStateKey,
      farmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return withdrawObligationCollateralAndRedeemReserveCollateralV2(invokedKaminoLendingProgramMeta, keys, collateralAmount);
  }

  public static Instruction withdrawObligationCollateralAndRedeemReserveCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                     final List<AccountMeta> keys,
                                                                                     final long collateralAmount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_OBLIGATION_COLLATERAL_AND_REDEEM_RESERVE_COLLATERAL_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, collateralAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record WithdrawObligationCollateralAndRedeemReserveCollateralV2IxData(Discriminator discriminator, long collateralAmount) implements Borsh {  

    public static WithdrawObligationCollateralAndRedeemReserveCollateralV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawObligationCollateralAndRedeemReserveCollateralV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var collateralAmount = getInt64LE(_data, i);
      return new WithdrawObligationCollateralAndRedeemReserveCollateralV2IxData(discriminator, collateralAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, collateralAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_OBLIGATION_AND_REDEEM_RESERVE_COLLATERAL_DISCRIMINATOR = toDiscriminator(177, 71, 154, 188, 226, 133, 74, 55);

  public static List<AccountMeta> liquidateObligationAndRedeemReserveCollateralKeys(final PublicKey liquidatorKey,
                                                                                    final PublicKey obligationKey,
                                                                                    final PublicKey lendingMarketKey,
                                                                                    final PublicKey lendingMarketAuthorityKey,
                                                                                    final PublicKey repayReserveKey,
                                                                                    final PublicKey repayReserveLiquidityMintKey,
                                                                                    final PublicKey repayReserveLiquiditySupplyKey,
                                                                                    final PublicKey withdrawReserveKey,
                                                                                    final PublicKey withdrawReserveLiquidityMintKey,
                                                                                    final PublicKey withdrawReserveCollateralMintKey,
                                                                                    final PublicKey withdrawReserveCollateralSupplyKey,
                                                                                    final PublicKey withdrawReserveLiquiditySupplyKey,
                                                                                    final PublicKey withdrawReserveLiquidityFeeReceiverKey,
                                                                                    final PublicKey userSourceLiquidityKey,
                                                                                    final PublicKey userDestinationCollateralKey,
                                                                                    final PublicKey userDestinationLiquidityKey,
                                                                                    final PublicKey collateralTokenProgramKey,
                                                                                    final PublicKey repayLiquidityTokenProgramKey,
                                                                                    final PublicKey withdrawLiquidityTokenProgramKey,
                                                                                    final PublicKey instructionSysvarAccountKey) {
    return List.of(
      createReadOnlySigner(liquidatorKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createWrite(repayReserveKey),
      createRead(repayReserveLiquidityMintKey),
      createWrite(repayReserveLiquiditySupplyKey),
      createWrite(withdrawReserveKey),
      createRead(withdrawReserveLiquidityMintKey),
      createWrite(withdrawReserveCollateralMintKey),
      createWrite(withdrawReserveCollateralSupplyKey),
      createWrite(withdrawReserveLiquiditySupplyKey),
      createWrite(withdrawReserveLiquidityFeeReceiverKey),
      createWrite(userSourceLiquidityKey),
      createWrite(userDestinationCollateralKey),
      createWrite(userDestinationLiquidityKey),
      createRead(collateralTokenProgramKey),
      createRead(repayLiquidityTokenProgramKey),
      createRead(withdrawLiquidityTokenProgramKey),
      createRead(instructionSysvarAccountKey)
    );
  }

  public static Instruction liquidateObligationAndRedeemReserveCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                          final PublicKey liquidatorKey,
                                                                          final PublicKey obligationKey,
                                                                          final PublicKey lendingMarketKey,
                                                                          final PublicKey lendingMarketAuthorityKey,
                                                                          final PublicKey repayReserveKey,
                                                                          final PublicKey repayReserveLiquidityMintKey,
                                                                          final PublicKey repayReserveLiquiditySupplyKey,
                                                                          final PublicKey withdrawReserveKey,
                                                                          final PublicKey withdrawReserveLiquidityMintKey,
                                                                          final PublicKey withdrawReserveCollateralMintKey,
                                                                          final PublicKey withdrawReserveCollateralSupplyKey,
                                                                          final PublicKey withdrawReserveLiquiditySupplyKey,
                                                                          final PublicKey withdrawReserveLiquidityFeeReceiverKey,
                                                                          final PublicKey userSourceLiquidityKey,
                                                                          final PublicKey userDestinationCollateralKey,
                                                                          final PublicKey userDestinationLiquidityKey,
                                                                          final PublicKey collateralTokenProgramKey,
                                                                          final PublicKey repayLiquidityTokenProgramKey,
                                                                          final PublicKey withdrawLiquidityTokenProgramKey,
                                                                          final PublicKey instructionSysvarAccountKey,
                                                                          final long liquidityAmount,
                                                                          final long minAcceptableReceivedLiquidityAmount,
                                                                          final long maxAllowedLtvOverridePercent) {
    final var keys = liquidateObligationAndRedeemReserveCollateralKeys(
      liquidatorKey,
      obligationKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      repayReserveKey,
      repayReserveLiquidityMintKey,
      repayReserveLiquiditySupplyKey,
      withdrawReserveKey,
      withdrawReserveLiquidityMintKey,
      withdrawReserveCollateralMintKey,
      withdrawReserveCollateralSupplyKey,
      withdrawReserveLiquiditySupplyKey,
      withdrawReserveLiquidityFeeReceiverKey,
      userSourceLiquidityKey,
      userDestinationCollateralKey,
      userDestinationLiquidityKey,
      collateralTokenProgramKey,
      repayLiquidityTokenProgramKey,
      withdrawLiquidityTokenProgramKey,
      instructionSysvarAccountKey
    );
    return liquidateObligationAndRedeemReserveCollateral(
      invokedKaminoLendingProgramMeta,
      keys,
      liquidityAmount,
      minAcceptableReceivedLiquidityAmount,
      maxAllowedLtvOverridePercent
    );
  }

  public static Instruction liquidateObligationAndRedeemReserveCollateral(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                          final List<AccountMeta> keys,
                                                                          final long liquidityAmount,
                                                                          final long minAcceptableReceivedLiquidityAmount,
                                                                          final long maxAllowedLtvOverridePercent) {
    final byte[] _data = new byte[32];
    int i = LIQUIDATE_OBLIGATION_AND_REDEEM_RESERVE_COLLATERAL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);
    i += 8;
    putInt64LE(_data, i, minAcceptableReceivedLiquidityAmount);
    i += 8;
    putInt64LE(_data, i, maxAllowedLtvOverridePercent);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record LiquidateObligationAndRedeemReserveCollateralIxData(Discriminator discriminator,
                                                                    long liquidityAmount,
                                                                    long minAcceptableReceivedLiquidityAmount,
                                                                    long maxAllowedLtvOverridePercent) implements Borsh {  

    public static LiquidateObligationAndRedeemReserveCollateralIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 32;

    public static LiquidateObligationAndRedeemReserveCollateralIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      i += 8;
      final var minAcceptableReceivedLiquidityAmount = getInt64LE(_data, i);
      i += 8;
      final var maxAllowedLtvOverridePercent = getInt64LE(_data, i);
      return new LiquidateObligationAndRedeemReserveCollateralIxData(discriminator, liquidityAmount, minAcceptableReceivedLiquidityAmount, maxAllowedLtvOverridePercent);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      putInt64LE(_data, i, minAcceptableReceivedLiquidityAmount);
      i += 8;
      putInt64LE(_data, i, maxAllowedLtvOverridePercent);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_OBLIGATION_AND_REDEEM_RESERVE_COLLATERAL_V_2_DISCRIMINATOR = toDiscriminator(162, 161, 35, 143, 30, 187, 185, 103);

  public static List<AccountMeta> liquidateObligationAndRedeemReserveCollateralV2Keys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                                      final PublicKey liquidationAccountsLiquidatorKey,
                                                                                      final PublicKey liquidationAccountsObligationKey,
                                                                                      final PublicKey liquidationAccountsLendingMarketKey,
                                                                                      final PublicKey liquidationAccountsLendingMarketAuthorityKey,
                                                                                      final PublicKey liquidationAccountsRepayReserveKey,
                                                                                      final PublicKey liquidationAccountsRepayReserveLiquidityMintKey,
                                                                                      final PublicKey liquidationAccountsRepayReserveLiquiditySupplyKey,
                                                                                      final PublicKey liquidationAccountsWithdrawReserveKey,
                                                                                      final PublicKey liquidationAccountsWithdrawReserveLiquidityMintKey,
                                                                                      final PublicKey liquidationAccountsWithdrawReserveCollateralMintKey,
                                                                                      final PublicKey liquidationAccountsWithdrawReserveCollateralSupplyKey,
                                                                                      final PublicKey liquidationAccountsWithdrawReserveLiquiditySupplyKey,
                                                                                      final PublicKey liquidationAccountsWithdrawReserveLiquidityFeeReceiverKey,
                                                                                      final PublicKey liquidationAccountsUserSourceLiquidityKey,
                                                                                      final PublicKey liquidationAccountsUserDestinationCollateralKey,
                                                                                      final PublicKey liquidationAccountsUserDestinationLiquidityKey,
                                                                                      final PublicKey liquidationAccountsCollateralTokenProgramKey,
                                                                                      final PublicKey liquidationAccountsRepayLiquidityTokenProgramKey,
                                                                                      final PublicKey liquidationAccountsWithdrawLiquidityTokenProgramKey,
                                                                                      final PublicKey liquidationAccountsInstructionSysvarAccountKey,
                                                                                      final PublicKey collateralFarmsAccountsObligationFarmUserStateKey,
                                                                                      final PublicKey collateralFarmsAccountsReserveFarmStateKey,
                                                                                      final PublicKey debtFarmsAccountsObligationFarmUserStateKey,
                                                                                      final PublicKey debtFarmsAccountsReserveFarmStateKey,
                                                                                      final PublicKey farmsProgramKey) {
    return List.of(
      createReadOnlySigner(liquidationAccountsLiquidatorKey),
      createWrite(liquidationAccountsObligationKey),
      createRead(liquidationAccountsLendingMarketKey),
      createRead(liquidationAccountsLendingMarketAuthorityKey),
      createWrite(liquidationAccountsRepayReserveKey),
      createRead(liquidationAccountsRepayReserveLiquidityMintKey),
      createWrite(liquidationAccountsRepayReserveLiquiditySupplyKey),
      createWrite(liquidationAccountsWithdrawReserveKey),
      createRead(liquidationAccountsWithdrawReserveLiquidityMintKey),
      createWrite(liquidationAccountsWithdrawReserveCollateralMintKey),
      createWrite(liquidationAccountsWithdrawReserveCollateralSupplyKey),
      createWrite(liquidationAccountsWithdrawReserveLiquiditySupplyKey),
      createWrite(liquidationAccountsWithdrawReserveLiquidityFeeReceiverKey),
      createWrite(liquidationAccountsUserSourceLiquidityKey),
      createWrite(liquidationAccountsUserDestinationCollateralKey),
      createWrite(liquidationAccountsUserDestinationLiquidityKey),
      createRead(liquidationAccountsCollateralTokenProgramKey),
      createRead(liquidationAccountsRepayLiquidityTokenProgramKey),
      createRead(liquidationAccountsWithdrawLiquidityTokenProgramKey),
      createRead(liquidationAccountsInstructionSysvarAccountKey),
      createWrite(requireNonNullElse(collateralFarmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(collateralFarmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(debtFarmsAccountsObligationFarmUserStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(debtFarmsAccountsReserveFarmStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(farmsProgramKey)
    );
  }

  public static Instruction liquidateObligationAndRedeemReserveCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                            final PublicKey liquidationAccountsLiquidatorKey,
                                                                            final PublicKey liquidationAccountsObligationKey,
                                                                            final PublicKey liquidationAccountsLendingMarketKey,
                                                                            final PublicKey liquidationAccountsLendingMarketAuthorityKey,
                                                                            final PublicKey liquidationAccountsRepayReserveKey,
                                                                            final PublicKey liquidationAccountsRepayReserveLiquidityMintKey,
                                                                            final PublicKey liquidationAccountsRepayReserveLiquiditySupplyKey,
                                                                            final PublicKey liquidationAccountsWithdrawReserveKey,
                                                                            final PublicKey liquidationAccountsWithdrawReserveLiquidityMintKey,
                                                                            final PublicKey liquidationAccountsWithdrawReserveCollateralMintKey,
                                                                            final PublicKey liquidationAccountsWithdrawReserveCollateralSupplyKey,
                                                                            final PublicKey liquidationAccountsWithdrawReserveLiquiditySupplyKey,
                                                                            final PublicKey liquidationAccountsWithdrawReserveLiquidityFeeReceiverKey,
                                                                            final PublicKey liquidationAccountsUserSourceLiquidityKey,
                                                                            final PublicKey liquidationAccountsUserDestinationCollateralKey,
                                                                            final PublicKey liquidationAccountsUserDestinationLiquidityKey,
                                                                            final PublicKey liquidationAccountsCollateralTokenProgramKey,
                                                                            final PublicKey liquidationAccountsRepayLiquidityTokenProgramKey,
                                                                            final PublicKey liquidationAccountsWithdrawLiquidityTokenProgramKey,
                                                                            final PublicKey liquidationAccountsInstructionSysvarAccountKey,
                                                                            final PublicKey collateralFarmsAccountsObligationFarmUserStateKey,
                                                                            final PublicKey collateralFarmsAccountsReserveFarmStateKey,
                                                                            final PublicKey debtFarmsAccountsObligationFarmUserStateKey,
                                                                            final PublicKey debtFarmsAccountsReserveFarmStateKey,
                                                                            final PublicKey farmsProgramKey,
                                                                            final long liquidityAmount,
                                                                            final long minAcceptableReceivedLiquidityAmount,
                                                                            final long maxAllowedLtvOverridePercent) {
    final var keys = liquidateObligationAndRedeemReserveCollateralV2Keys(
      invokedKaminoLendingProgramMeta,
      liquidationAccountsLiquidatorKey,
      liquidationAccountsObligationKey,
      liquidationAccountsLendingMarketKey,
      liquidationAccountsLendingMarketAuthorityKey,
      liquidationAccountsRepayReserveKey,
      liquidationAccountsRepayReserveLiquidityMintKey,
      liquidationAccountsRepayReserveLiquiditySupplyKey,
      liquidationAccountsWithdrawReserveKey,
      liquidationAccountsWithdrawReserveLiquidityMintKey,
      liquidationAccountsWithdrawReserveCollateralMintKey,
      liquidationAccountsWithdrawReserveCollateralSupplyKey,
      liquidationAccountsWithdrawReserveLiquiditySupplyKey,
      liquidationAccountsWithdrawReserveLiquidityFeeReceiverKey,
      liquidationAccountsUserSourceLiquidityKey,
      liquidationAccountsUserDestinationCollateralKey,
      liquidationAccountsUserDestinationLiquidityKey,
      liquidationAccountsCollateralTokenProgramKey,
      liquidationAccountsRepayLiquidityTokenProgramKey,
      liquidationAccountsWithdrawLiquidityTokenProgramKey,
      liquidationAccountsInstructionSysvarAccountKey,
      collateralFarmsAccountsObligationFarmUserStateKey,
      collateralFarmsAccountsReserveFarmStateKey,
      debtFarmsAccountsObligationFarmUserStateKey,
      debtFarmsAccountsReserveFarmStateKey,
      farmsProgramKey
    );
    return liquidateObligationAndRedeemReserveCollateralV2(
      invokedKaminoLendingProgramMeta,
      keys,
      liquidityAmount,
      minAcceptableReceivedLiquidityAmount,
      maxAllowedLtvOverridePercent
    );
  }

  public static Instruction liquidateObligationAndRedeemReserveCollateralV2(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                            final List<AccountMeta> keys,
                                                                            final long liquidityAmount,
                                                                            final long minAcceptableReceivedLiquidityAmount,
                                                                            final long maxAllowedLtvOverridePercent) {
    final byte[] _data = new byte[32];
    int i = LIQUIDATE_OBLIGATION_AND_REDEEM_RESERVE_COLLATERAL_V_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);
    i += 8;
    putInt64LE(_data, i, minAcceptableReceivedLiquidityAmount);
    i += 8;
    putInt64LE(_data, i, maxAllowedLtvOverridePercent);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record LiquidateObligationAndRedeemReserveCollateralV2IxData(Discriminator discriminator,
                                                                      long liquidityAmount,
                                                                      long minAcceptableReceivedLiquidityAmount,
                                                                      long maxAllowedLtvOverridePercent) implements Borsh {  

    public static LiquidateObligationAndRedeemReserveCollateralV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 32;

    public static LiquidateObligationAndRedeemReserveCollateralV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      i += 8;
      final var minAcceptableReceivedLiquidityAmount = getInt64LE(_data, i);
      i += 8;
      final var maxAllowedLtvOverridePercent = getInt64LE(_data, i);
      return new LiquidateObligationAndRedeemReserveCollateralV2IxData(discriminator, liquidityAmount, minAcceptableReceivedLiquidityAmount, maxAllowedLtvOverridePercent);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      putInt64LE(_data, i, minAcceptableReceivedLiquidityAmount);
      i += 8;
      putInt64LE(_data, i, maxAllowedLtvOverridePercent);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FLASH_REPAY_RESERVE_LIQUIDITY_DISCRIMINATOR = toDiscriminator(185, 117, 0, 203, 96, 245, 180, 186);

  public static List<AccountMeta> flashRepayReserveLiquidityKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                 final PublicKey userTransferAuthorityKey,
                                                                 final PublicKey lendingMarketAuthorityKey,
                                                                 final PublicKey lendingMarketKey,
                                                                 final PublicKey reserveKey,
                                                                 final PublicKey reserveLiquidityMintKey,
                                                                 final PublicKey reserveDestinationLiquidityKey,
                                                                 final PublicKey userSourceLiquidityKey,
                                                                 final PublicKey reserveLiquidityFeeReceiverKey,
                                                                 final PublicKey referrerTokenStateKey,
                                                                 final PublicKey referrerAccountKey,
                                                                 final PublicKey sysvarInfoKey,
                                                                 final PublicKey tokenProgramKey) {
    return List.of(
      createReadOnlySigner(userTransferAuthorityKey),
      createRead(lendingMarketAuthorityKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveDestinationLiquidityKey),
      createWrite(userSourceLiquidityKey),
      createWrite(reserveLiquidityFeeReceiverKey),
      createWrite(requireNonNullElse(referrerTokenStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(referrerAccountKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(sysvarInfoKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction flashRepayReserveLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                       final PublicKey userTransferAuthorityKey,
                                                       final PublicKey lendingMarketAuthorityKey,
                                                       final PublicKey lendingMarketKey,
                                                       final PublicKey reserveKey,
                                                       final PublicKey reserveLiquidityMintKey,
                                                       final PublicKey reserveDestinationLiquidityKey,
                                                       final PublicKey userSourceLiquidityKey,
                                                       final PublicKey reserveLiquidityFeeReceiverKey,
                                                       final PublicKey referrerTokenStateKey,
                                                       final PublicKey referrerAccountKey,
                                                       final PublicKey sysvarInfoKey,
                                                       final PublicKey tokenProgramKey,
                                                       final long liquidityAmount,
                                                       final int borrowInstructionIndex) {
    final var keys = flashRepayReserveLiquidityKeys(
      invokedKaminoLendingProgramMeta,
      userTransferAuthorityKey,
      lendingMarketAuthorityKey,
      lendingMarketKey,
      reserveKey,
      reserveLiquidityMintKey,
      reserveDestinationLiquidityKey,
      userSourceLiquidityKey,
      reserveLiquidityFeeReceiverKey,
      referrerTokenStateKey,
      referrerAccountKey,
      sysvarInfoKey,
      tokenProgramKey
    );
    return flashRepayReserveLiquidity(invokedKaminoLendingProgramMeta, keys, liquidityAmount, borrowInstructionIndex);
  }

  public static Instruction flashRepayReserveLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final long liquidityAmount,
                                                       final int borrowInstructionIndex) {
    final byte[] _data = new byte[17];
    int i = FLASH_REPAY_RESERVE_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);
    i += 8;
    _data[i] = (byte) borrowInstructionIndex;

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record FlashRepayReserveLiquidityIxData(Discriminator discriminator, long liquidityAmount, int borrowInstructionIndex) implements Borsh {  

    public static FlashRepayReserveLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 17;

    public static FlashRepayReserveLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      i += 8;
      final var borrowInstructionIndex = _data[i] & 0xFF;
      return new FlashRepayReserveLiquidityIxData(discriminator, liquidityAmount, borrowInstructionIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      _data[i] = (byte) borrowInstructionIndex;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FLASH_BORROW_RESERVE_LIQUIDITY_DISCRIMINATOR = toDiscriminator(135, 231, 52, 167, 7, 52, 212, 193);

  public static List<AccountMeta> flashBorrowReserveLiquidityKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                                  final PublicKey userTransferAuthorityKey,
                                                                  final PublicKey lendingMarketAuthorityKey,
                                                                  final PublicKey lendingMarketKey,
                                                                  final PublicKey reserveKey,
                                                                  final PublicKey reserveLiquidityMintKey,
                                                                  final PublicKey reserveSourceLiquidityKey,
                                                                  final PublicKey userDestinationLiquidityKey,
                                                                  final PublicKey reserveLiquidityFeeReceiverKey,
                                                                  final PublicKey referrerTokenStateKey,
                                                                  final PublicKey referrerAccountKey,
                                                                  final PublicKey sysvarInfoKey,
                                                                  final PublicKey tokenProgramKey) {
    return List.of(
      createReadOnlySigner(userTransferAuthorityKey),
      createRead(lendingMarketAuthorityKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveSourceLiquidityKey),
      createWrite(userDestinationLiquidityKey),
      createWrite(reserveLiquidityFeeReceiverKey),
      createWrite(requireNonNullElse(referrerTokenStateKey, invokedKaminoLendingProgramMeta.publicKey())),
      createWrite(requireNonNullElse(referrerAccountKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(sysvarInfoKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction flashBorrowReserveLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                        final PublicKey userTransferAuthorityKey,
                                                        final PublicKey lendingMarketAuthorityKey,
                                                        final PublicKey lendingMarketKey,
                                                        final PublicKey reserveKey,
                                                        final PublicKey reserveLiquidityMintKey,
                                                        final PublicKey reserveSourceLiquidityKey,
                                                        final PublicKey userDestinationLiquidityKey,
                                                        final PublicKey reserveLiquidityFeeReceiverKey,
                                                        final PublicKey referrerTokenStateKey,
                                                        final PublicKey referrerAccountKey,
                                                        final PublicKey sysvarInfoKey,
                                                        final PublicKey tokenProgramKey,
                                                        final long liquidityAmount) {
    final var keys = flashBorrowReserveLiquidityKeys(
      invokedKaminoLendingProgramMeta,
      userTransferAuthorityKey,
      lendingMarketAuthorityKey,
      lendingMarketKey,
      reserveKey,
      reserveLiquidityMintKey,
      reserveSourceLiquidityKey,
      userDestinationLiquidityKey,
      reserveLiquidityFeeReceiverKey,
      referrerTokenStateKey,
      referrerAccountKey,
      sysvarInfoKey,
      tokenProgramKey
    );
    return flashBorrowReserveLiquidity(invokedKaminoLendingProgramMeta, keys, liquidityAmount);
  }

  public static Instruction flashBorrowReserveLiquidity(final AccountMeta invokedKaminoLendingProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final long liquidityAmount) {
    final byte[] _data = new byte[16];
    int i = FLASH_BORROW_RESERVE_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, liquidityAmount);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record FlashBorrowReserveLiquidityIxData(Discriminator discriminator, long liquidityAmount) implements Borsh {  

    public static FlashBorrowReserveLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static FlashBorrowReserveLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityAmount = getInt64LE(_data, i);
      return new FlashBorrowReserveLiquidityIxData(discriminator, liquidityAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, liquidityAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REQUEST_ELEVATION_GROUP_DISCRIMINATOR = toDiscriminator(36, 119, 251, 129, 34, 240, 7, 147);

  public static List<AccountMeta> requestElevationGroupKeys(final PublicKey ownerKey,
                                                            final PublicKey obligationKey,
                                                            final PublicKey lendingMarketKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey)
    );
  }

  public static Instruction requestElevationGroup(final AccountMeta invokedKaminoLendingProgramMeta,
                                                  final PublicKey ownerKey,
                                                  final PublicKey obligationKey,
                                                  final PublicKey lendingMarketKey,
                                                  final int elevationGroup) {
    final var keys = requestElevationGroupKeys(
      ownerKey,
      obligationKey,
      lendingMarketKey
    );
    return requestElevationGroup(invokedKaminoLendingProgramMeta, keys, elevationGroup);
  }

  public static Instruction requestElevationGroup(final AccountMeta invokedKaminoLendingProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final int elevationGroup) {
    final byte[] _data = new byte[9];
    int i = REQUEST_ELEVATION_GROUP_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) elevationGroup;

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record RequestElevationGroupIxData(Discriminator discriminator, int elevationGroup) implements Borsh {  

    public static RequestElevationGroupIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static RequestElevationGroupIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var elevationGroup = _data[i] & 0xFF;
      return new RequestElevationGroupIxData(discriminator, elevationGroup);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) elevationGroup;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INIT_REFERRER_TOKEN_STATE_DISCRIMINATOR = toDiscriminator(116, 45, 66, 148, 58, 13, 218, 115);

  public static List<AccountMeta> initReferrerTokenStateKeys(final PublicKey payerKey,
                                                             final PublicKey lendingMarketKey,
                                                             final PublicKey reserveKey,
                                                             final PublicKey referrerKey,
                                                             final PublicKey referrerTokenStateKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createRead(lendingMarketKey),
      createRead(reserveKey),
      createRead(referrerKey),
      createWrite(referrerTokenStateKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initReferrerTokenState(final AccountMeta invokedKaminoLendingProgramMeta,
                                                   final PublicKey payerKey,
                                                   final PublicKey lendingMarketKey,
                                                   final PublicKey reserveKey,
                                                   final PublicKey referrerKey,
                                                   final PublicKey referrerTokenStateKey,
                                                   final PublicKey rentKey,
                                                   final PublicKey systemProgramKey) {
    final var keys = initReferrerTokenStateKeys(
      payerKey,
      lendingMarketKey,
      reserveKey,
      referrerKey,
      referrerTokenStateKey,
      rentKey,
      systemProgramKey
    );
    return initReferrerTokenState(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction initReferrerTokenState(final AccountMeta invokedKaminoLendingProgramMeta,
                                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, INIT_REFERRER_TOKEN_STATE_DISCRIMINATOR);
  }

  public static final Discriminator INIT_USER_METADATA_DISCRIMINATOR = toDiscriminator(117, 169, 176, 69, 197, 23, 15, 162);

  public static List<AccountMeta> initUserMetadataKeys(final AccountMeta invokedKaminoLendingProgramMeta,
                                                       final PublicKey ownerKey,
                                                       final PublicKey feePayerKey,
                                                       final PublicKey userMetadataKey,
                                                       final PublicKey referrerUserMetadataKey,
                                                       final PublicKey rentKey,
                                                       final PublicKey systemProgramKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWritableSigner(feePayerKey),
      createWrite(userMetadataKey),
      createRead(requireNonNullElse(referrerUserMetadataKey, invokedKaminoLendingProgramMeta.publicKey())),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initUserMetadata(final AccountMeta invokedKaminoLendingProgramMeta,
                                             final PublicKey ownerKey,
                                             final PublicKey feePayerKey,
                                             final PublicKey userMetadataKey,
                                             final PublicKey referrerUserMetadataKey,
                                             final PublicKey rentKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey userLookupTable) {
    final var keys = initUserMetadataKeys(
      invokedKaminoLendingProgramMeta,
      ownerKey,
      feePayerKey,
      userMetadataKey,
      referrerUserMetadataKey,
      rentKey,
      systemProgramKey
    );
    return initUserMetadata(invokedKaminoLendingProgramMeta, keys, userLookupTable);
  }

  public static Instruction initUserMetadata(final AccountMeta invokedKaminoLendingProgramMeta,
                                             final List<AccountMeta> keys,
                                             final PublicKey userLookupTable) {
    final byte[] _data = new byte[40];
    int i = INIT_USER_METADATA_DISCRIMINATOR.write(_data, 0);
    userLookupTable.write(_data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record InitUserMetadataIxData(Discriminator discriminator, PublicKey userLookupTable) implements Borsh {  

    public static InitUserMetadataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static InitUserMetadataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var userLookupTable = readPubKey(_data, i);
      return new InitUserMetadataIxData(discriminator, userLookupTable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      userLookupTable.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_REFERRER_FEES_DISCRIMINATOR = toDiscriminator(171, 118, 121, 201, 233, 140, 23, 228);

  public static List<AccountMeta> withdrawReferrerFeesKeys(final PublicKey referrerKey,
                                                           final PublicKey referrerTokenStateKey,
                                                           final PublicKey reserveKey,
                                                           final PublicKey reserveLiquidityMintKey,
                                                           final PublicKey reserveSupplyLiquidityKey,
                                                           final PublicKey referrerTokenAccountKey,
                                                           final PublicKey lendingMarketKey,
                                                           final PublicKey lendingMarketAuthorityKey,
                                                           final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(referrerKey),
      createWrite(referrerTokenStateKey),
      createWrite(reserveKey),
      createRead(reserveLiquidityMintKey),
      createWrite(reserveSupplyLiquidityKey),
      createWrite(referrerTokenAccountKey),
      createRead(lendingMarketKey),
      createRead(lendingMarketAuthorityKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction withdrawReferrerFees(final AccountMeta invokedKaminoLendingProgramMeta,
                                                 final PublicKey referrerKey,
                                                 final PublicKey referrerTokenStateKey,
                                                 final PublicKey reserveKey,
                                                 final PublicKey reserveLiquidityMintKey,
                                                 final PublicKey reserveSupplyLiquidityKey,
                                                 final PublicKey referrerTokenAccountKey,
                                                 final PublicKey lendingMarketKey,
                                                 final PublicKey lendingMarketAuthorityKey,
                                                 final PublicKey tokenProgramKey) {
    final var keys = withdrawReferrerFeesKeys(
      referrerKey,
      referrerTokenStateKey,
      reserveKey,
      reserveLiquidityMintKey,
      reserveSupplyLiquidityKey,
      referrerTokenAccountKey,
      lendingMarketKey,
      lendingMarketAuthorityKey,
      tokenProgramKey
    );
    return withdrawReferrerFees(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction withdrawReferrerFees(final AccountMeta invokedKaminoLendingProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, WITHDRAW_REFERRER_FEES_DISCRIMINATOR);
  }

  public static final Discriminator INIT_REFERRER_STATE_AND_SHORT_URL_DISCRIMINATOR = toDiscriminator(165, 19, 25, 127, 100, 55, 31, 90);

  public static List<AccountMeta> initReferrerStateAndShortUrlKeys(final PublicKey referrerKey,
                                                                   final PublicKey referrerStateKey,
                                                                   final PublicKey referrerShortUrlKey,
                                                                   final PublicKey referrerUserMetadataKey,
                                                                   final PublicKey rentKey,
                                                                   final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(referrerKey),
      createWrite(referrerStateKey),
      createWrite(referrerShortUrlKey),
      createRead(referrerUserMetadataKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initReferrerStateAndShortUrl(final AccountMeta invokedKaminoLendingProgramMeta,
                                                         final PublicKey referrerKey,
                                                         final PublicKey referrerStateKey,
                                                         final PublicKey referrerShortUrlKey,
                                                         final PublicKey referrerUserMetadataKey,
                                                         final PublicKey rentKey,
                                                         final PublicKey systemProgramKey,
                                                         final String shortUrl) {
    final var keys = initReferrerStateAndShortUrlKeys(
      referrerKey,
      referrerStateKey,
      referrerShortUrlKey,
      referrerUserMetadataKey,
      rentKey,
      systemProgramKey
    );
    return initReferrerStateAndShortUrl(invokedKaminoLendingProgramMeta, keys, shortUrl);
  }

  public static Instruction initReferrerStateAndShortUrl(final AccountMeta invokedKaminoLendingProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final String shortUrl) {
    final byte[] _shortUrl = shortUrl.getBytes(UTF_8);
    final byte[] _data = new byte[12 + _shortUrl.length];
    int i = INIT_REFERRER_STATE_AND_SHORT_URL_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(_shortUrl, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record InitReferrerStateAndShortUrlIxData(Discriminator discriminator, String shortUrl, byte[] _shortUrl) implements Borsh {  

    public static InitReferrerStateAndShortUrlIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitReferrerStateAndShortUrlIxData createRecord(final Discriminator discriminator, final String shortUrl) {
      return new InitReferrerStateAndShortUrlIxData(discriminator, shortUrl, shortUrl.getBytes(UTF_8));
    }

    public static InitReferrerStateAndShortUrlIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _shortUrlLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _shortUrl = Arrays.copyOfRange(_data, i, i + _shortUrlLength);
      final var shortUrl = new String(_shortUrl, UTF_8);
      return new InitReferrerStateAndShortUrlIxData(discriminator, shortUrl, _shortUrl);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_shortUrl, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _shortUrl.length;
    }
  }

  public static final Discriminator DELETE_REFERRER_STATE_AND_SHORT_URL_DISCRIMINATOR = toDiscriminator(153, 185, 99, 28, 228, 179, 187, 150);

  public static List<AccountMeta> deleteReferrerStateAndShortUrlKeys(final PublicKey referrerKey,
                                                                     final PublicKey referrerStateKey,
                                                                     final PublicKey shortUrlKey,
                                                                     final PublicKey rentKey,
                                                                     final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(referrerKey),
      createWrite(referrerStateKey),
      createWrite(shortUrlKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction deleteReferrerStateAndShortUrl(final AccountMeta invokedKaminoLendingProgramMeta,
                                                           final PublicKey referrerKey,
                                                           final PublicKey referrerStateKey,
                                                           final PublicKey shortUrlKey,
                                                           final PublicKey rentKey,
                                                           final PublicKey systemProgramKey) {
    final var keys = deleteReferrerStateAndShortUrlKeys(
      referrerKey,
      referrerStateKey,
      shortUrlKey,
      rentKey,
      systemProgramKey
    );
    return deleteReferrerStateAndShortUrl(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction deleteReferrerStateAndShortUrl(final AccountMeta invokedKaminoLendingProgramMeta,
                                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, DELETE_REFERRER_STATE_AND_SHORT_URL_DISCRIMINATOR);
  }

  public static final Discriminator SET_OBLIGATION_ORDER_DISCRIMINATOR = toDiscriminator(81, 1, 99, 156, 211, 83, 78, 46);

  public static List<AccountMeta> setObligationOrderKeys(final PublicKey ownerKey,
                                                         final PublicKey obligationKey,
                                                         final PublicKey lendingMarketKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(obligationKey),
      createRead(lendingMarketKey)
    );
  }

  public static Instruction setObligationOrder(final AccountMeta invokedKaminoLendingProgramMeta,
                                               final PublicKey ownerKey,
                                               final PublicKey obligationKey,
                                               final PublicKey lendingMarketKey,
                                               final int index,
                                               final ObligationOrder order) {
    final var keys = setObligationOrderKeys(
      ownerKey,
      obligationKey,
      lendingMarketKey
    );
    return setObligationOrder(invokedKaminoLendingProgramMeta, keys, index, order);
  }

  public static Instruction setObligationOrder(final AccountMeta invokedKaminoLendingProgramMeta,
                                               final List<AccountMeta> keys,
                                               final int index,
                                               final ObligationOrder order) {
    final byte[] _data = new byte[9 + Borsh.len(order)];
    int i = SET_OBLIGATION_ORDER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) index;
    ++i;
    Borsh.write(order, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record SetObligationOrderIxData(Discriminator discriminator, int index, ObligationOrder order) implements Borsh {  

    public static SetObligationOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 137;

    public static SetObligationOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var index = _data[i] & 0xFF;
      ++i;
      final var order = ObligationOrder.read(_data, i);
      return new SetObligationOrderIxData(discriminator, index, order);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) index;
      ++i;
      i += Borsh.write(order, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
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

  public static Instruction initGlobalConfig(final AccountMeta invokedKaminoLendingProgramMeta,
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
    return initGlobalConfig(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction initGlobalConfig(final AccountMeta invokedKaminoLendingProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, INIT_GLOBAL_CONFIG_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_GLOBAL_CONFIG_DISCRIMINATOR = toDiscriminator(164, 84, 130, 189, 111, 58, 250, 200);

  public static List<AccountMeta> updateGlobalConfigKeys(final PublicKey globalAdminKey,
                                                         final PublicKey globalConfigKey) {
    return List.of(
      createReadOnlySigner(globalAdminKey),
      createWrite(globalConfigKey)
    );
  }

  public static Instruction updateGlobalConfig(final AccountMeta invokedKaminoLendingProgramMeta,
                                               final PublicKey globalAdminKey,
                                               final PublicKey globalConfigKey,
                                               final UpdateGlobalConfigMode mode,
                                               final byte[] value) {
    final var keys = updateGlobalConfigKeys(
      globalAdminKey,
      globalConfigKey
    );
    return updateGlobalConfig(invokedKaminoLendingProgramMeta, keys, mode, value);
  }

  public static Instruction updateGlobalConfig(final AccountMeta invokedKaminoLendingProgramMeta,
                                               final List<AccountMeta> keys,
                                               final UpdateGlobalConfigMode mode,
                                               final byte[] value) {
    final byte[] _data = new byte[8 + Borsh.len(mode) + Borsh.lenVector(value)];
    int i = UPDATE_GLOBAL_CONFIG_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(mode, _data, i);
    Borsh.writeVector(value, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record UpdateGlobalConfigIxData(Discriminator discriminator, UpdateGlobalConfigMode mode, byte[] value) implements Borsh {  

    public static UpdateGlobalConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateGlobalConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mode = UpdateGlobalConfigMode.read(_data, i);
      i += Borsh.len(mode);
      final var value = Borsh.readbyteVector(_data, i);
      return new UpdateGlobalConfigIxData(discriminator, mode, value);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(mode, _data, i);
      i += Borsh.writeVector(value, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(mode) + Borsh.lenVector(value);
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

  public static Instruction updateGlobalConfigAdmin(final AccountMeta invokedKaminoLendingProgramMeta,
                                                    final PublicKey pendingAdminKey,
                                                    final PublicKey globalConfigKey) {
    final var keys = updateGlobalConfigAdminKeys(
      pendingAdminKey,
      globalConfigKey
    );
    return updateGlobalConfigAdmin(invokedKaminoLendingProgramMeta, keys);
  }

  public static Instruction updateGlobalConfigAdmin(final AccountMeta invokedKaminoLendingProgramMeta,
                                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, UPDATE_GLOBAL_CONFIG_ADMIN_DISCRIMINATOR);
  }

  public static final Discriminator IDL_MISSING_TYPES_DISCRIMINATOR = toDiscriminator(130, 80, 38, 153, 80, 212, 182, 253);

  public static List<AccountMeta> idlMissingTypesKeys(final PublicKey signerKey,
                                                      final PublicKey globalConfigKey,
                                                      final PublicKey lendingMarketKey,
                                                      final PublicKey reserveKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createRead(globalConfigKey),
      createRead(lendingMarketKey),
      createWrite(reserveKey)
    );
  }

  public static Instruction idlMissingTypes(final AccountMeta invokedKaminoLendingProgramMeta,
                                            final PublicKey signerKey,
                                            final PublicKey globalConfigKey,
                                            final PublicKey lendingMarketKey,
                                            final PublicKey reserveKey,
                                            final ReserveFarmKind reserveFarmKind,
                                            final AssetTier assetTier,
                                            final FeeCalculation feeCalculation,
                                            final ReserveStatus reserveStatus,
                                            final UpdateConfigMode updateConfigMode,
                                            final UpdateLendingMarketConfigValue updateLendingMarketConfigValue,
                                            final UpdateLendingMarketMode updateLendingMarketConfigMode) {
    final var keys = idlMissingTypesKeys(
      signerKey,
      globalConfigKey,
      lendingMarketKey,
      reserveKey
    );
    return idlMissingTypes(
      invokedKaminoLendingProgramMeta,
      keys,
      reserveFarmKind,
      assetTier,
      feeCalculation,
      reserveStatus,
      updateConfigMode,
      updateLendingMarketConfigValue,
      updateLendingMarketConfigMode
    );
  }

  public static Instruction idlMissingTypes(final AccountMeta invokedKaminoLendingProgramMeta,
                                            final List<AccountMeta> keys,
                                            final ReserveFarmKind reserveFarmKind,
                                            final AssetTier assetTier,
                                            final FeeCalculation feeCalculation,
                                            final ReserveStatus reserveStatus,
                                            final UpdateConfigMode updateConfigMode,
                                            final UpdateLendingMarketConfigValue updateLendingMarketConfigValue,
                                            final UpdateLendingMarketMode updateLendingMarketConfigMode) {
    final byte[] _data = new byte[8 + Borsh.len(reserveFarmKind) + Borsh.len(assetTier) + Borsh.len(feeCalculation) + Borsh.len(reserveStatus) + Borsh.len(updateConfigMode) + Borsh.len(updateLendingMarketConfigValue) + Borsh.len(updateLendingMarketConfigMode)];
    int i = IDL_MISSING_TYPES_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(reserveFarmKind, _data, i);
    i += Borsh.write(assetTier, _data, i);
    i += Borsh.write(feeCalculation, _data, i);
    i += Borsh.write(reserveStatus, _data, i);
    i += Borsh.write(updateConfigMode, _data, i);
    i += Borsh.write(updateLendingMarketConfigValue, _data, i);
    Borsh.write(updateLendingMarketConfigMode, _data, i);

    return Instruction.createInstruction(invokedKaminoLendingProgramMeta, keys, _data);
  }

  public record IdlMissingTypesIxData(Discriminator discriminator,
                                      ReserveFarmKind reserveFarmKind,
                                      AssetTier assetTier,
                                      FeeCalculation feeCalculation,
                                      ReserveStatus reserveStatus,
                                      UpdateConfigMode updateConfigMode,
                                      UpdateLendingMarketConfigValue updateLendingMarketConfigValue,
                                      UpdateLendingMarketMode updateLendingMarketConfigMode) implements Borsh {  

    public static IdlMissingTypesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static IdlMissingTypesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var reserveFarmKind = ReserveFarmKind.read(_data, i);
      i += Borsh.len(reserveFarmKind);
      final var assetTier = AssetTier.read(_data, i);
      i += Borsh.len(assetTier);
      final var feeCalculation = FeeCalculation.read(_data, i);
      i += Borsh.len(feeCalculation);
      final var reserveStatus = ReserveStatus.read(_data, i);
      i += Borsh.len(reserveStatus);
      final var updateConfigMode = UpdateConfigMode.read(_data, i);
      i += Borsh.len(updateConfigMode);
      final var updateLendingMarketConfigValue = UpdateLendingMarketConfigValue.read(_data, i);
      i += Borsh.len(updateLendingMarketConfigValue);
      final var updateLendingMarketConfigMode = UpdateLendingMarketMode.read(_data, i);
      return new IdlMissingTypesIxData(discriminator,
                                       reserveFarmKind,
                                       assetTier,
                                       feeCalculation,
                                       reserveStatus,
                                       updateConfigMode,
                                       updateLendingMarketConfigValue,
                                       updateLendingMarketConfigMode);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(reserveFarmKind, _data, i);
      i += Borsh.write(assetTier, _data, i);
      i += Borsh.write(feeCalculation, _data, i);
      i += Borsh.write(reserveStatus, _data, i);
      i += Borsh.write(updateConfigMode, _data, i);
      i += Borsh.write(updateLendingMarketConfigValue, _data, i);
      i += Borsh.write(updateLendingMarketConfigMode, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(reserveFarmKind)
           + Borsh.len(assetTier)
           + Borsh.len(feeCalculation)
           + Borsh.len(reserveStatus)
           + Borsh.len(updateConfigMode)
           + Borsh.len(updateLendingMarketConfigValue)
           + Borsh.len(updateLendingMarketConfigMode);
    }
  }

  private KaminoLendingProgram() {
  }
}
