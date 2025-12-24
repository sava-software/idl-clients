package software.sava.idl.clients.meteora.dlmm.gen;

import java.util.List;
import java.util.OptionalInt;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.meteora.dlmm.gen.types.AddLiquiditySingleSidePreciseParameter;
import software.sava.idl.clients.meteora.dlmm.gen.types.AddLiquiditySingleSidePreciseParameter2;
import software.sava.idl.clients.meteora.dlmm.gen.types.BaseFeeParameter;
import software.sava.idl.clients.meteora.dlmm.gen.types.BinLiquidityReduction;
import software.sava.idl.clients.meteora.dlmm.gen.types.CustomizableParams;
import software.sava.idl.clients.meteora.dlmm.gen.types.DummyIx;
import software.sava.idl.clients.meteora.dlmm.gen.types.DynamicFeeParameter;
import software.sava.idl.clients.meteora.dlmm.gen.types.InitPermissionPairIx;
import software.sava.idl.clients.meteora.dlmm.gen.types.InitPresetParameters2Ix;
import software.sava.idl.clients.meteora.dlmm.gen.types.InitPresetParametersIx;
import software.sava.idl.clients.meteora.dlmm.gen.types.InitializeLbPair2Params;
import software.sava.idl.clients.meteora.dlmm.gen.types.LiquidityOneSideParameter;
import software.sava.idl.clients.meteora.dlmm.gen.types.LiquidityParameter;
import software.sava.idl.clients.meteora.dlmm.gen.types.LiquidityParameterByStrategy;
import software.sava.idl.clients.meteora.dlmm.gen.types.LiquidityParameterByStrategyOneSide;
import software.sava.idl.clients.meteora.dlmm.gen.types.LiquidityParameterByWeight;
import software.sava.idl.clients.meteora.dlmm.gen.types.RebalanceLiquidityParams;
import software.sava.idl.clients.meteora.dlmm.gen.types.RemainingAccountsInfo;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class LbClmmProgram {

  public static final Discriminator ADD_LIQUIDITY_DISCRIMINATOR = toDiscriminator(181, 157, 89, 67, 143, 182, 52, 72);

  public static List<AccountMeta> addLiquidityKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                   final PublicKey positionKey,
                                                   final PublicKey lbPairKey,
                                                   final PublicKey binArrayBitmapExtensionKey,
                                                   final PublicKey userTokenXKey,
                                                   final PublicKey userTokenYKey,
                                                   final PublicKey reserveXKey,
                                                   final PublicKey reserveYKey,
                                                   final PublicKey tokenXMintKey,
                                                   final PublicKey tokenYMintKey,
                                                   final PublicKey binArrayLowerKey,
                                                   final PublicKey binArrayUpperKey,
                                                   final PublicKey senderKey,
                                                   final PublicKey tokenXProgramKey,
                                                   final PublicKey tokenYProgramKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                         final PublicKey positionKey,
                                         final PublicKey lbPairKey,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey userTokenXKey,
                                         final PublicKey userTokenYKey,
                                         final PublicKey reserveXKey,
                                         final PublicKey reserveYKey,
                                         final PublicKey tokenXMintKey,
                                         final PublicKey tokenYMintKey,
                                         final PublicKey binArrayLowerKey,
                                         final PublicKey binArrayUpperKey,
                                         final PublicKey senderKey,
                                         final PublicKey tokenXProgramKey,
                                         final PublicKey tokenYProgramKey,
                                         final PublicKey eventAuthorityKey,
                                         final PublicKey programKey,
                                         final LiquidityParameter liquidityParameter) {
    final var keys = addLiquidityKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidity(invokedLbClmmProgramMeta, keys, liquidityParameter);
  }

  public static Instruction addLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                         final List<AccountMeta> keys,
                                         final LiquidityParameter liquidityParameter) {
    final byte[] _data = new byte[8 + liquidityParameter.l()];
    int i = ADD_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    liquidityParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityIxData(Discriminator discriminator, LiquidityParameter liquidityParameter) implements SerDe {  

    public static AddLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityParameter.read(_data, i);
      return new AddLiquidityIxData(discriminator, liquidityParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + liquidityParameter.l();
    }
  }

  public static final Discriminator ADD_LIQUIDITY_2_DISCRIMINATOR = toDiscriminator(228, 162, 78, 28, 70, 219, 116, 115);

  public static List<AccountMeta> addLiquidity2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                    final PublicKey positionKey,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey binArrayBitmapExtensionKey,
                                                    final PublicKey userTokenXKey,
                                                    final PublicKey userTokenYKey,
                                                    final PublicKey reserveXKey,
                                                    final PublicKey reserveYKey,
                                                    final PublicKey tokenXMintKey,
                                                    final PublicKey tokenYMintKey,
                                                    final PublicKey senderKey,
                                                    final PublicKey tokenXProgramKey,
                                                    final PublicKey tokenYProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidity2(final AccountMeta invokedLbClmmProgramMeta,
                                          final PublicKey positionKey,
                                          final PublicKey lbPairKey,
                                          final PublicKey binArrayBitmapExtensionKey,
                                          final PublicKey userTokenXKey,
                                          final PublicKey userTokenYKey,
                                          final PublicKey reserveXKey,
                                          final PublicKey reserveYKey,
                                          final PublicKey tokenXMintKey,
                                          final PublicKey tokenYMintKey,
                                          final PublicKey senderKey,
                                          final PublicKey tokenXProgramKey,
                                          final PublicKey tokenYProgramKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey,
                                          final LiquidityParameter liquidityParameter,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = addLiquidity2Keys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidity2(invokedLbClmmProgramMeta, keys, liquidityParameter, remainingAccountsInfo);
  }

  public static Instruction addLiquidity2(final AccountMeta invokedLbClmmProgramMeta,
                                          final List<AccountMeta> keys,
                                          final LiquidityParameter liquidityParameter,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[8 + liquidityParameter.l() + remainingAccountsInfo.l()];
    int i = ADD_LIQUIDITY_2_DISCRIMINATOR.write(_data, 0);
    i += liquidityParameter.write(_data, i);
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidity2IxData(Discriminator discriminator, LiquidityParameter liquidityParameter, RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static AddLiquidity2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidity2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityParameter.read(_data, i);
      i += liquidityParameter.l();
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new AddLiquidity2IxData(discriminator, liquidityParameter, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + liquidityParameter.l() + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator ADD_LIQUIDITY_BY_STRATEGY_DISCRIMINATOR = toDiscriminator(7, 3, 150, 127, 148, 40, 61, 200);

  public static List<AccountMeta> addLiquidityByStrategyKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                             final PublicKey positionKey,
                                                             final PublicKey lbPairKey,
                                                             final PublicKey binArrayBitmapExtensionKey,
                                                             final PublicKey userTokenXKey,
                                                             final PublicKey userTokenYKey,
                                                             final PublicKey reserveXKey,
                                                             final PublicKey reserveYKey,
                                                             final PublicKey tokenXMintKey,
                                                             final PublicKey tokenYMintKey,
                                                             final PublicKey binArrayLowerKey,
                                                             final PublicKey binArrayUpperKey,
                                                             final PublicKey senderKey,
                                                             final PublicKey tokenXProgramKey,
                                                             final PublicKey tokenYProgramKey,
                                                             final PublicKey eventAuthorityKey,
                                                             final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityByStrategy(final AccountMeta invokedLbClmmProgramMeta,
                                                   final PublicKey positionKey,
                                                   final PublicKey lbPairKey,
                                                   final PublicKey binArrayBitmapExtensionKey,
                                                   final PublicKey userTokenXKey,
                                                   final PublicKey userTokenYKey,
                                                   final PublicKey reserveXKey,
                                                   final PublicKey reserveYKey,
                                                   final PublicKey tokenXMintKey,
                                                   final PublicKey tokenYMintKey,
                                                   final PublicKey binArrayLowerKey,
                                                   final PublicKey binArrayUpperKey,
                                                   final PublicKey senderKey,
                                                   final PublicKey tokenXProgramKey,
                                                   final PublicKey tokenYProgramKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey,
                                                   final LiquidityParameterByStrategy liquidityParameter) {
    final var keys = addLiquidityByStrategyKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityByStrategy(invokedLbClmmProgramMeta, keys, liquidityParameter);
  }

  public static Instruction addLiquidityByStrategy(final AccountMeta invokedLbClmmProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final LiquidityParameterByStrategy liquidityParameter) {
    final byte[] _data = new byte[8 + liquidityParameter.l()];
    int i = ADD_LIQUIDITY_BY_STRATEGY_DISCRIMINATOR.write(_data, 0);
    liquidityParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityByStrategyIxData(Discriminator discriminator, LiquidityParameterByStrategy liquidityParameter) implements SerDe {  

    public static AddLiquidityByStrategyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 105;

    public static AddLiquidityByStrategyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityParameterByStrategy.read(_data, i);
      return new AddLiquidityByStrategyIxData(discriminator, liquidityParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ADD_LIQUIDITY_BY_STRATEGY_2_DISCRIMINATOR = toDiscriminator(3, 221, 149, 218, 111, 141, 118, 213);

  public static List<AccountMeta> addLiquidityByStrategy2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                              final PublicKey positionKey,
                                                              final PublicKey lbPairKey,
                                                              final PublicKey binArrayBitmapExtensionKey,
                                                              final PublicKey userTokenXKey,
                                                              final PublicKey userTokenYKey,
                                                              final PublicKey reserveXKey,
                                                              final PublicKey reserveYKey,
                                                              final PublicKey tokenXMintKey,
                                                              final PublicKey tokenYMintKey,
                                                              final PublicKey senderKey,
                                                              final PublicKey tokenXProgramKey,
                                                              final PublicKey tokenYProgramKey,
                                                              final PublicKey eventAuthorityKey,
                                                              final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityByStrategy2(final AccountMeta invokedLbClmmProgramMeta,
                                                    final PublicKey positionKey,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey binArrayBitmapExtensionKey,
                                                    final PublicKey userTokenXKey,
                                                    final PublicKey userTokenYKey,
                                                    final PublicKey reserveXKey,
                                                    final PublicKey reserveYKey,
                                                    final PublicKey tokenXMintKey,
                                                    final PublicKey tokenYMintKey,
                                                    final PublicKey senderKey,
                                                    final PublicKey tokenXProgramKey,
                                                    final PublicKey tokenYProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey,
                                                    final LiquidityParameterByStrategy liquidityParameter,
                                                    final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = addLiquidityByStrategy2Keys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityByStrategy2(invokedLbClmmProgramMeta, keys, liquidityParameter, remainingAccountsInfo);
  }

  public static Instruction addLiquidityByStrategy2(final AccountMeta invokedLbClmmProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final LiquidityParameterByStrategy liquidityParameter,
                                                    final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[8 + liquidityParameter.l() + remainingAccountsInfo.l()];
    int i = ADD_LIQUIDITY_BY_STRATEGY_2_DISCRIMINATOR.write(_data, 0);
    i += liquidityParameter.write(_data, i);
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityByStrategy2IxData(Discriminator discriminator, LiquidityParameterByStrategy liquidityParameter, RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static AddLiquidityByStrategy2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidityByStrategy2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityParameterByStrategy.read(_data, i);
      i += liquidityParameter.l();
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new AddLiquidityByStrategy2IxData(discriminator, liquidityParameter, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + liquidityParameter.l() + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator ADD_LIQUIDITY_BY_STRATEGY_ONE_SIDE_DISCRIMINATOR = toDiscriminator(41, 5, 238, 175, 100, 225, 6, 205);

  public static List<AccountMeta> addLiquidityByStrategyOneSideKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                                    final PublicKey positionKey,
                                                                    final PublicKey lbPairKey,
                                                                    final PublicKey binArrayBitmapExtensionKey,
                                                                    final PublicKey userTokenKey,
                                                                    final PublicKey reserveKey,
                                                                    final PublicKey tokenMintKey,
                                                                    final PublicKey binArrayLowerKey,
                                                                    final PublicKey binArrayUpperKey,
                                                                    final PublicKey senderKey,
                                                                    final PublicKey tokenProgramKey,
                                                                    final PublicKey eventAuthorityKey,
                                                                    final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenKey),
      createWrite(reserveKey),
      createRead(tokenMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityByStrategyOneSide(final AccountMeta invokedLbClmmProgramMeta,
                                                          final PublicKey positionKey,
                                                          final PublicKey lbPairKey,
                                                          final PublicKey binArrayBitmapExtensionKey,
                                                          final PublicKey userTokenKey,
                                                          final PublicKey reserveKey,
                                                          final PublicKey tokenMintKey,
                                                          final PublicKey binArrayLowerKey,
                                                          final PublicKey binArrayUpperKey,
                                                          final PublicKey senderKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey eventAuthorityKey,
                                                          final PublicKey programKey,
                                                          final LiquidityParameterByStrategyOneSide liquidityParameter) {
    final var keys = addLiquidityByStrategyOneSideKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenKey,
      reserveKey,
      tokenMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityByStrategyOneSide(invokedLbClmmProgramMeta, keys, liquidityParameter);
  }

  public static Instruction addLiquidityByStrategyOneSide(final AccountMeta invokedLbClmmProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final LiquidityParameterByStrategyOneSide liquidityParameter) {
    final byte[] _data = new byte[8 + liquidityParameter.l()];
    int i = ADD_LIQUIDITY_BY_STRATEGY_ONE_SIDE_DISCRIMINATOR.write(_data, 0);
    liquidityParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityByStrategyOneSideIxData(Discriminator discriminator, LiquidityParameterByStrategyOneSide liquidityParameter) implements SerDe {  

    public static AddLiquidityByStrategyOneSideIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 97;

    public static AddLiquidityByStrategyOneSideIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityParameterByStrategyOneSide.read(_data, i);
      return new AddLiquidityByStrategyOneSideIxData(discriminator, liquidityParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ADD_LIQUIDITY_BY_WEIGHT_DISCRIMINATOR = toDiscriminator(28, 140, 238, 99, 231, 162, 21, 149);

  public static List<AccountMeta> addLiquidityByWeightKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                           final PublicKey positionKey,
                                                           final PublicKey lbPairKey,
                                                           final PublicKey binArrayBitmapExtensionKey,
                                                           final PublicKey userTokenXKey,
                                                           final PublicKey userTokenYKey,
                                                           final PublicKey reserveXKey,
                                                           final PublicKey reserveYKey,
                                                           final PublicKey tokenXMintKey,
                                                           final PublicKey tokenYMintKey,
                                                           final PublicKey binArrayLowerKey,
                                                           final PublicKey binArrayUpperKey,
                                                           final PublicKey senderKey,
                                                           final PublicKey tokenXProgramKey,
                                                           final PublicKey tokenYProgramKey,
                                                           final PublicKey eventAuthorityKey,
                                                           final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityByWeight(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey positionKey,
                                                 final PublicKey lbPairKey,
                                                 final PublicKey binArrayBitmapExtensionKey,
                                                 final PublicKey userTokenXKey,
                                                 final PublicKey userTokenYKey,
                                                 final PublicKey reserveXKey,
                                                 final PublicKey reserveYKey,
                                                 final PublicKey tokenXMintKey,
                                                 final PublicKey tokenYMintKey,
                                                 final PublicKey binArrayLowerKey,
                                                 final PublicKey binArrayUpperKey,
                                                 final PublicKey senderKey,
                                                 final PublicKey tokenXProgramKey,
                                                 final PublicKey tokenYProgramKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey,
                                                 final LiquidityParameterByWeight liquidityParameter) {
    final var keys = addLiquidityByWeightKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityByWeight(invokedLbClmmProgramMeta, keys, liquidityParameter);
  }

  public static Instruction addLiquidityByWeight(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final LiquidityParameterByWeight liquidityParameter) {
    final byte[] _data = new byte[8 + liquidityParameter.l()];
    int i = ADD_LIQUIDITY_BY_WEIGHT_DISCRIMINATOR.write(_data, 0);
    liquidityParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityByWeightIxData(Discriminator discriminator, LiquidityParameterByWeight liquidityParameter) implements SerDe {  

    public static AddLiquidityByWeightIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidityByWeightIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityParameterByWeight.read(_data, i);
      return new AddLiquidityByWeightIxData(discriminator, liquidityParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + liquidityParameter.l();
    }
  }

  public static final Discriminator ADD_LIQUIDITY_ONE_SIDE_DISCRIMINATOR = toDiscriminator(94, 155, 103, 151, 70, 95, 220, 165);

  public static List<AccountMeta> addLiquidityOneSideKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                          final PublicKey positionKey,
                                                          final PublicKey lbPairKey,
                                                          final PublicKey binArrayBitmapExtensionKey,
                                                          final PublicKey userTokenKey,
                                                          final PublicKey reserveKey,
                                                          final PublicKey tokenMintKey,
                                                          final PublicKey binArrayLowerKey,
                                                          final PublicKey binArrayUpperKey,
                                                          final PublicKey senderKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey eventAuthorityKey,
                                                          final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenKey),
      createWrite(reserveKey),
      createRead(tokenMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityOneSide(final AccountMeta invokedLbClmmProgramMeta,
                                                final PublicKey positionKey,
                                                final PublicKey lbPairKey,
                                                final PublicKey binArrayBitmapExtensionKey,
                                                final PublicKey userTokenKey,
                                                final PublicKey reserveKey,
                                                final PublicKey tokenMintKey,
                                                final PublicKey binArrayLowerKey,
                                                final PublicKey binArrayUpperKey,
                                                final PublicKey senderKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey eventAuthorityKey,
                                                final PublicKey programKey,
                                                final LiquidityOneSideParameter liquidityParameter) {
    final var keys = addLiquidityOneSideKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenKey,
      reserveKey,
      tokenMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityOneSide(invokedLbClmmProgramMeta, keys, liquidityParameter);
  }

  public static Instruction addLiquidityOneSide(final AccountMeta invokedLbClmmProgramMeta,
                                                final List<AccountMeta> keys,
                                                final LiquidityOneSideParameter liquidityParameter) {
    final byte[] _data = new byte[8 + liquidityParameter.l()];
    int i = ADD_LIQUIDITY_ONE_SIDE_DISCRIMINATOR.write(_data, 0);
    liquidityParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityOneSideIxData(Discriminator discriminator, LiquidityOneSideParameter liquidityParameter) implements SerDe {  

    public static AddLiquidityOneSideIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidityOneSideIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = LiquidityOneSideParameter.read(_data, i);
      return new AddLiquidityOneSideIxData(discriminator, liquidityParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + liquidityParameter.l();
    }
  }

  public static final Discriminator ADD_LIQUIDITY_ONE_SIDE_PRECISE_DISCRIMINATOR = toDiscriminator(161, 194, 103, 84, 171, 71, 250, 154);

  public static List<AccountMeta> addLiquidityOneSidePreciseKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                                 final PublicKey positionKey,
                                                                 final PublicKey lbPairKey,
                                                                 final PublicKey binArrayBitmapExtensionKey,
                                                                 final PublicKey userTokenKey,
                                                                 final PublicKey reserveKey,
                                                                 final PublicKey tokenMintKey,
                                                                 final PublicKey binArrayLowerKey,
                                                                 final PublicKey binArrayUpperKey,
                                                                 final PublicKey senderKey,
                                                                 final PublicKey tokenProgramKey,
                                                                 final PublicKey eventAuthorityKey,
                                                                 final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenKey),
      createWrite(reserveKey),
      createRead(tokenMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityOneSidePrecise(final AccountMeta invokedLbClmmProgramMeta,
                                                       final PublicKey positionKey,
                                                       final PublicKey lbPairKey,
                                                       final PublicKey binArrayBitmapExtensionKey,
                                                       final PublicKey userTokenKey,
                                                       final PublicKey reserveKey,
                                                       final PublicKey tokenMintKey,
                                                       final PublicKey binArrayLowerKey,
                                                       final PublicKey binArrayUpperKey,
                                                       final PublicKey senderKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey,
                                                       final AddLiquiditySingleSidePreciseParameter parameter) {
    final var keys = addLiquidityOneSidePreciseKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenKey,
      reserveKey,
      tokenMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityOneSidePrecise(invokedLbClmmProgramMeta, keys, parameter);
  }

  public static Instruction addLiquidityOneSidePrecise(final AccountMeta invokedLbClmmProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final AddLiquiditySingleSidePreciseParameter parameter) {
    final byte[] _data = new byte[8 + parameter.l()];
    int i = ADD_LIQUIDITY_ONE_SIDE_PRECISE_DISCRIMINATOR.write(_data, 0);
    parameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityOneSidePreciseIxData(Discriminator discriminator, AddLiquiditySingleSidePreciseParameter parameter) implements SerDe {  

    public static AddLiquidityOneSidePreciseIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidityOneSidePreciseIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var parameter = AddLiquiditySingleSidePreciseParameter.read(_data, i);
      return new AddLiquidityOneSidePreciseIxData(discriminator, parameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += parameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + parameter.l();
    }
  }

  public static final Discriminator ADD_LIQUIDITY_ONE_SIDE_PRECISE_2_DISCRIMINATOR = toDiscriminator(33, 51, 163, 201, 117, 98, 125, 231);

  public static List<AccountMeta> addLiquidityOneSidePrecise2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                                  final PublicKey positionKey,
                                                                  final PublicKey lbPairKey,
                                                                  final PublicKey binArrayBitmapExtensionKey,
                                                                  final PublicKey userTokenKey,
                                                                  final PublicKey reserveKey,
                                                                  final PublicKey tokenMintKey,
                                                                  final PublicKey senderKey,
                                                                  final PublicKey tokenProgramKey,
                                                                  final PublicKey eventAuthorityKey,
                                                                  final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenKey),
      createWrite(reserveKey),
      createRead(tokenMintKey),
      createReadOnlySigner(senderKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction addLiquidityOneSidePrecise2(final AccountMeta invokedLbClmmProgramMeta,
                                                        final PublicKey positionKey,
                                                        final PublicKey lbPairKey,
                                                        final PublicKey binArrayBitmapExtensionKey,
                                                        final PublicKey userTokenKey,
                                                        final PublicKey reserveKey,
                                                        final PublicKey tokenMintKey,
                                                        final PublicKey senderKey,
                                                        final PublicKey tokenProgramKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey,
                                                        final AddLiquiditySingleSidePreciseParameter2 liquidityParameter,
                                                        final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = addLiquidityOneSidePrecise2Keys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenKey,
      reserveKey,
      tokenMintKey,
      senderKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return addLiquidityOneSidePrecise2(invokedLbClmmProgramMeta, keys, liquidityParameter, remainingAccountsInfo);
  }

  public static Instruction addLiquidityOneSidePrecise2(final AccountMeta invokedLbClmmProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final AddLiquiditySingleSidePreciseParameter2 liquidityParameter,
                                                        final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[8 + liquidityParameter.l() + remainingAccountsInfo.l()];
    int i = ADD_LIQUIDITY_ONE_SIDE_PRECISE_2_DISCRIMINATOR.write(_data, 0);
    i += liquidityParameter.write(_data, i);
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record AddLiquidityOneSidePrecise2IxData(Discriminator discriminator, AddLiquiditySingleSidePreciseParameter2 liquidityParameter, RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static AddLiquidityOneSidePrecise2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddLiquidityOneSidePrecise2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityParameter = AddLiquiditySingleSidePreciseParameter2.read(_data, i);
      i += liquidityParameter.l();
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new AddLiquidityOneSidePrecise2IxData(discriminator, liquidityParameter, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += liquidityParameter.write(_data, i);
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + liquidityParameter.l() + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator CLAIM_FEE_DISCRIMINATOR = toDiscriminator(169, 32, 79, 137, 136, 232, 70, 137);

  public static List<AccountMeta> claimFeeKeys(final PublicKey lbPairKey,
                                               final PublicKey positionKey,
                                               final PublicKey binArrayLowerKey,
                                               final PublicKey binArrayUpperKey,
                                               final PublicKey senderKey,
                                               final PublicKey reserveXKey,
                                               final PublicKey reserveYKey,
                                               final PublicKey userTokenXKey,
                                               final PublicKey userTokenYKey,
                                               final PublicKey tokenXMintKey,
                                               final PublicKey tokenYMintKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(positionKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction claimFee(final AccountMeta invokedLbClmmProgramMeta,
                                     final PublicKey lbPairKey,
                                     final PublicKey positionKey,
                                     final PublicKey binArrayLowerKey,
                                     final PublicKey binArrayUpperKey,
                                     final PublicKey senderKey,
                                     final PublicKey reserveXKey,
                                     final PublicKey reserveYKey,
                                     final PublicKey userTokenXKey,
                                     final PublicKey userTokenYKey,
                                     final PublicKey tokenXMintKey,
                                     final PublicKey tokenYMintKey,
                                     final PublicKey tokenProgramKey,
                                     final PublicKey eventAuthorityKey,
                                     final PublicKey programKey) {
    final var keys = claimFeeKeys(
      lbPairKey,
      positionKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      reserveXKey,
      reserveYKey,
      userTokenXKey,
      userTokenYKey,
      tokenXMintKey,
      tokenYMintKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimFee(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction claimFee(final AccountMeta invokedLbClmmProgramMeta,
                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLAIM_FEE_DISCRIMINATOR);
  }

  public static final Discriminator CLAIM_FEE_2_DISCRIMINATOR = toDiscriminator(112, 191, 101, 171, 28, 144, 127, 187);

  public static List<AccountMeta> claimFee2Keys(final PublicKey lbPairKey,
                                                final PublicKey positionKey,
                                                final PublicKey senderKey,
                                                final PublicKey reserveXKey,
                                                final PublicKey reserveYKey,
                                                final PublicKey userTokenXKey,
                                                final PublicKey userTokenYKey,
                                                final PublicKey tokenXMintKey,
                                                final PublicKey tokenYMintKey,
                                                final PublicKey tokenProgramXKey,
                                                final PublicKey tokenProgramYKey,
                                                final PublicKey memoProgramKey,
                                                final PublicKey eventAuthorityKey,
                                                final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(positionKey),
      createReadOnlySigner(senderKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createRead(tokenProgramXKey),
      createRead(tokenProgramYKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction claimFee2(final AccountMeta invokedLbClmmProgramMeta,
                                      final PublicKey lbPairKey,
                                      final PublicKey positionKey,
                                      final PublicKey senderKey,
                                      final PublicKey reserveXKey,
                                      final PublicKey reserveYKey,
                                      final PublicKey userTokenXKey,
                                      final PublicKey userTokenYKey,
                                      final PublicKey tokenXMintKey,
                                      final PublicKey tokenYMintKey,
                                      final PublicKey tokenProgramXKey,
                                      final PublicKey tokenProgramYKey,
                                      final PublicKey memoProgramKey,
                                      final PublicKey eventAuthorityKey,
                                      final PublicKey programKey,
                                      final int minBinId,
                                      final int maxBinId,
                                      final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = claimFee2Keys(
      lbPairKey,
      positionKey,
      senderKey,
      reserveXKey,
      reserveYKey,
      userTokenXKey,
      userTokenYKey,
      tokenXMintKey,
      tokenYMintKey,
      tokenProgramXKey,
      tokenProgramYKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimFee2(
      invokedLbClmmProgramMeta,
      keys,
      minBinId,
      maxBinId,
      remainingAccountsInfo
    );
  }

  public static Instruction claimFee2(final AccountMeta invokedLbClmmProgramMeta,
                                      final List<AccountMeta> keys,
                                      final int minBinId,
                                      final int maxBinId,
                                      final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[16 + remainingAccountsInfo.l()];
    int i = CLAIM_FEE_2_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, minBinId);
    i += 4;
    putInt32LE(_data, i, maxBinId);
    i += 4;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record ClaimFee2IxData(Discriminator discriminator,
                                int minBinId,
                                int maxBinId,
                                RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static ClaimFee2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ClaimFee2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var minBinId = getInt32LE(_data, i);
      i += 4;
      final var maxBinId = getInt32LE(_data, i);
      i += 4;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new ClaimFee2IxData(discriminator, minBinId, maxBinId, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, minBinId);
      i += 4;
      putInt32LE(_data, i, maxBinId);
      i += 4;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 4 + 4 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator CLAIM_REWARD_DISCRIMINATOR = toDiscriminator(149, 95, 181, 242, 94, 90, 158, 162);

  public static List<AccountMeta> claimRewardKeys(final PublicKey lbPairKey,
                                                  final PublicKey positionKey,
                                                  final PublicKey binArrayLowerKey,
                                                  final PublicKey binArrayUpperKey,
                                                  final PublicKey senderKey,
                                                  final PublicKey rewardVaultKey,
                                                  final PublicKey rewardMintKey,
                                                  final PublicKey userTokenAccountKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(positionKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createWrite(rewardVaultKey),
      createRead(rewardMintKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction claimReward(final AccountMeta invokedLbClmmProgramMeta,
                                        final PublicKey lbPairKey,
                                        final PublicKey positionKey,
                                        final PublicKey binArrayLowerKey,
                                        final PublicKey binArrayUpperKey,
                                        final PublicKey senderKey,
                                        final PublicKey rewardVaultKey,
                                        final PublicKey rewardMintKey,
                                        final PublicKey userTokenAccountKey,
                                        final PublicKey tokenProgramKey,
                                        final PublicKey eventAuthorityKey,
                                        final PublicKey programKey,
                                        final long rewardIndex) {
    final var keys = claimRewardKeys(
      lbPairKey,
      positionKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      rewardVaultKey,
      rewardMintKey,
      userTokenAccountKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimReward(invokedLbClmmProgramMeta, keys, rewardIndex);
  }

  public static Instruction claimReward(final AccountMeta invokedLbClmmProgramMeta,
                                        final List<AccountMeta> keys,
                                        final long rewardIndex) {
    final byte[] _data = new byte[16];
    int i = CLAIM_REWARD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record ClaimRewardIxData(Discriminator discriminator, long rewardIndex) implements SerDe {  

    public static ClaimRewardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static ClaimRewardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      return new ClaimRewardIxData(discriminator, rewardIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLAIM_REWARD_2_DISCRIMINATOR = toDiscriminator(190, 3, 127, 119, 178, 87, 157, 183);

  public static List<AccountMeta> claimReward2Keys(final PublicKey lbPairKey,
                                                   final PublicKey positionKey,
                                                   final PublicKey senderKey,
                                                   final PublicKey rewardVaultKey,
                                                   final PublicKey rewardMintKey,
                                                   final PublicKey userTokenAccountKey,
                                                   final PublicKey tokenProgramKey,
                                                   final PublicKey memoProgramKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(positionKey),
      createReadOnlySigner(senderKey),
      createWrite(rewardVaultKey),
      createRead(rewardMintKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction claimReward2(final AccountMeta invokedLbClmmProgramMeta,
                                         final PublicKey lbPairKey,
                                         final PublicKey positionKey,
                                         final PublicKey senderKey,
                                         final PublicKey rewardVaultKey,
                                         final PublicKey rewardMintKey,
                                         final PublicKey userTokenAccountKey,
                                         final PublicKey tokenProgramKey,
                                         final PublicKey memoProgramKey,
                                         final PublicKey eventAuthorityKey,
                                         final PublicKey programKey,
                                         final long rewardIndex,
                                         final int minBinId,
                                         final int maxBinId,
                                         final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = claimReward2Keys(
      lbPairKey,
      positionKey,
      senderKey,
      rewardVaultKey,
      rewardMintKey,
      userTokenAccountKey,
      tokenProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return claimReward2(
      invokedLbClmmProgramMeta,
      keys,
      rewardIndex,
      minBinId,
      maxBinId,
      remainingAccountsInfo
    );
  }

  public static Instruction claimReward2(final AccountMeta invokedLbClmmProgramMeta,
                                         final List<AccountMeta> keys,
                                         final long rewardIndex,
                                         final int minBinId,
                                         final int maxBinId,
                                         final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[24 + remainingAccountsInfo.l()];
    int i = CLAIM_REWARD_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt32LE(_data, i, minBinId);
    i += 4;
    putInt32LE(_data, i, maxBinId);
    i += 4;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record ClaimReward2IxData(Discriminator discriminator,
                                   long rewardIndex,
                                   int minBinId,
                                   int maxBinId,
                                   RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static ClaimReward2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ClaimReward2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      i += 8;
      final var minBinId = getInt32LE(_data, i);
      i += 4;
      final var maxBinId = getInt32LE(_data, i);
      i += 4;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new ClaimReward2IxData(discriminator,
                                    rewardIndex,
                                    minBinId,
                                    maxBinId,
                                    remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      putInt32LE(_data, i, minBinId);
      i += 4;
      putInt32LE(_data, i, maxBinId);
      i += 4;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 4 + 4 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator CLOSE_CLAIM_PROTOCOL_FEE_OPERATOR_DISCRIMINATOR = toDiscriminator(8, 41, 87, 35, 80, 48, 121, 26);

  public static List<AccountMeta> closeClaimProtocolFeeOperatorKeys(final PublicKey claimFeeOperatorKey,
                                                                    final PublicKey rentReceiverKey,
                                                                    final PublicKey adminKey) {
    return List.of(
      createWrite(claimFeeOperatorKey),
      createWrite(rentReceiverKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction closeClaimProtocolFeeOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                          final PublicKey claimFeeOperatorKey,
                                                          final PublicKey rentReceiverKey,
                                                          final PublicKey adminKey) {
    final var keys = closeClaimProtocolFeeOperatorKeys(
      claimFeeOperatorKey,
      rentReceiverKey,
      adminKey
    );
    return closeClaimProtocolFeeOperator(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closeClaimProtocolFeeOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_CLAIM_PROTOCOL_FEE_OPERATOR_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_POSITION_DISCRIMINATOR = toDiscriminator(123, 134, 81, 0, 49, 68, 98, 98);

  public static List<AccountMeta> closePositionKeys(final PublicKey positionKey,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey binArrayLowerKey,
                                                    final PublicKey binArrayUpperKey,
                                                    final PublicKey senderKey,
                                                    final PublicKey rentReceiverKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createWrite(rentReceiverKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction closePosition(final AccountMeta invokedLbClmmProgramMeta,
                                          final PublicKey positionKey,
                                          final PublicKey lbPairKey,
                                          final PublicKey binArrayLowerKey,
                                          final PublicKey binArrayUpperKey,
                                          final PublicKey senderKey,
                                          final PublicKey rentReceiverKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey) {
    final var keys = closePositionKeys(
      positionKey,
      lbPairKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      rentReceiverKey,
      eventAuthorityKey,
      programKey
    );
    return closePosition(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closePosition(final AccountMeta invokedLbClmmProgramMeta,
                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_POSITION_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_POSITION_2_DISCRIMINATOR = toDiscriminator(174, 90, 35, 115, 186, 40, 147, 226);

  public static List<AccountMeta> closePosition2Keys(final PublicKey positionKey,
                                                     final PublicKey senderKey,
                                                     final PublicKey rentReceiverKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createReadOnlySigner(senderKey),
      createWrite(rentReceiverKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction closePosition2(final AccountMeta invokedLbClmmProgramMeta,
                                           final PublicKey positionKey,
                                           final PublicKey senderKey,
                                           final PublicKey rentReceiverKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey) {
    final var keys = closePosition2Keys(
      positionKey,
      senderKey,
      rentReceiverKey,
      eventAuthorityKey,
      programKey
    );
    return closePosition2(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closePosition2(final AccountMeta invokedLbClmmProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_POSITION_2_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_POSITION_IF_EMPTY_DISCRIMINATOR = toDiscriminator(59, 124, 212, 118, 91, 152, 110, 157);

  public static List<AccountMeta> closePositionIfEmptyKeys(final PublicKey positionKey,
                                                           final PublicKey senderKey,
                                                           final PublicKey rentReceiverKey,
                                                           final PublicKey eventAuthorityKey,
                                                           final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createReadOnlySigner(senderKey),
      createWrite(rentReceiverKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction closePositionIfEmpty(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey positionKey,
                                                 final PublicKey senderKey,
                                                 final PublicKey rentReceiverKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey) {
    final var keys = closePositionIfEmptyKeys(
      positionKey,
      senderKey,
      rentReceiverKey,
      eventAuthorityKey,
      programKey
    );
    return closePositionIfEmpty(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closePositionIfEmpty(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_POSITION_IF_EMPTY_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_PRESET_PARAMETER_DISCRIMINATOR = toDiscriminator(4, 148, 145, 100, 134, 26, 181, 61);

  public static List<AccountMeta> closePresetParameterKeys(final PublicKey presetParameterKey,
                                                           final PublicKey adminKey,
                                                           final PublicKey rentReceiverKey) {
    return List.of(
      createWrite(presetParameterKey),
      createWritableSigner(adminKey),
      createWrite(rentReceiverKey)
    );
  }

  public static Instruction closePresetParameter(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey presetParameterKey,
                                                 final PublicKey adminKey,
                                                 final PublicKey rentReceiverKey) {
    final var keys = closePresetParameterKeys(
      presetParameterKey,
      adminKey,
      rentReceiverKey
    );
    return closePresetParameter(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closePresetParameter(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_PRESET_PARAMETER_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_PRESET_PARAMETER_2_DISCRIMINATOR = toDiscriminator(39, 25, 95, 107, 116, 17, 115, 28);

  public static List<AccountMeta> closePresetParameter2Keys(final PublicKey presetParameterKey,
                                                            final PublicKey adminKey,
                                                            final PublicKey rentReceiverKey) {
    return List.of(
      createWrite(presetParameterKey),
      createWritableSigner(adminKey),
      createWrite(rentReceiverKey)
    );
  }

  public static Instruction closePresetParameter2(final AccountMeta invokedLbClmmProgramMeta,
                                                  final PublicKey presetParameterKey,
                                                  final PublicKey adminKey,
                                                  final PublicKey rentReceiverKey) {
    final var keys = closePresetParameter2Keys(
      presetParameterKey,
      adminKey,
      rentReceiverKey
    );
    return closePresetParameter2(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closePresetParameter2(final AccountMeta invokedLbClmmProgramMeta,
                                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_PRESET_PARAMETER_2_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_TOKEN_BADGE_DISCRIMINATOR = toDiscriminator(108, 146, 86, 110, 179, 254, 10, 104);

  public static List<AccountMeta> closeTokenBadgeKeys(final PublicKey tokenBadgeKey,
                                                      final PublicKey rentReceiverKey,
                                                      final PublicKey adminKey) {
    return List.of(
      createWrite(tokenBadgeKey),
      createWrite(rentReceiverKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction closeTokenBadge(final AccountMeta invokedLbClmmProgramMeta,
                                            final PublicKey tokenBadgeKey,
                                            final PublicKey rentReceiverKey,
                                            final PublicKey adminKey) {
    final var keys = closeTokenBadgeKeys(
      tokenBadgeKey,
      rentReceiverKey,
      adminKey
    );
    return closeTokenBadge(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction closeTokenBadge(final AccountMeta invokedLbClmmProgramMeta,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CLOSE_TOKEN_BADGE_DISCRIMINATOR);
  }

  public static final Discriminator CREATE_CLAIM_PROTOCOL_FEE_OPERATOR_DISCRIMINATOR = toDiscriminator(51, 19, 150, 252, 105, 157, 48, 91);

  public static List<AccountMeta> createClaimProtocolFeeOperatorKeys(final SolanaAccounts solanaAccounts,
                                                                     final PublicKey claimFeeOperatorKey,
                                                                     final PublicKey operatorKey,
                                                                     final PublicKey adminKey) {
    return List.of(
      createWrite(claimFeeOperatorKey),
      createRead(operatorKey),
      createWritableSigner(adminKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction createClaimProtocolFeeOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                           final SolanaAccounts solanaAccounts,
                                                           final PublicKey claimFeeOperatorKey,
                                                           final PublicKey operatorKey,
                                                           final PublicKey adminKey) {
    final var keys = createClaimProtocolFeeOperatorKeys(
      solanaAccounts,
      claimFeeOperatorKey,
      operatorKey,
      adminKey
    );
    return createClaimProtocolFeeOperator(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction createClaimProtocolFeeOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, CREATE_CLAIM_PROTOCOL_FEE_OPERATOR_DISCRIMINATOR);
  }

  public static final Discriminator DECREASE_POSITION_LENGTH_DISCRIMINATOR = toDiscriminator(194, 219, 136, 32, 25, 96, 105, 37);

  public static List<AccountMeta> decreasePositionLengthKeys(final SolanaAccounts solanaAccounts,
                                                             final PublicKey rentReceiverKey,
                                                             final PublicKey positionKey,
                                                             final PublicKey ownerKey,
                                                             final PublicKey eventAuthorityKey,
                                                             final PublicKey programKey) {
    return List.of(
      createWrite(rentReceiverKey),
      createWrite(positionKey),
      createReadOnlySigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction decreasePositionLength(final AccountMeta invokedLbClmmProgramMeta,
                                                   final SolanaAccounts solanaAccounts,
                                                   final PublicKey rentReceiverKey,
                                                   final PublicKey positionKey,
                                                   final PublicKey ownerKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey,
                                                   final int lengthToRemove,
                                                   final int side) {
    final var keys = decreasePositionLengthKeys(
      solanaAccounts,
      rentReceiverKey,
      positionKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return decreasePositionLength(invokedLbClmmProgramMeta, keys, lengthToRemove, side);
  }

  public static Instruction decreasePositionLength(final AccountMeta invokedLbClmmProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final int lengthToRemove,
                                                   final int side) {
    final byte[] _data = new byte[11];
    int i = DECREASE_POSITION_LENGTH_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, lengthToRemove);
    i += 2;
    _data[i] = (byte) side;

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record DecreasePositionLengthIxData(Discriminator discriminator, int lengthToRemove, int side) implements SerDe {  

    public static DecreasePositionLengthIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static DecreasePositionLengthIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lengthToRemove = getInt16LE(_data, i);
      i += 2;
      final var side = _data[i] & 0xFF;
      return new DecreasePositionLengthIxData(discriminator, lengthToRemove, side);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, lengthToRemove);
      i += 2;
      _data[i] = (byte) side;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FOR_IDL_TYPE_GENERATION_DO_NOT_CALL_DISCRIMINATOR = toDiscriminator(180, 105, 69, 80, 95, 50, 73, 108);

  public static List<AccountMeta> forIdlTypeGenerationDoNotCallKeys(final PublicKey dummyZcAccountKey) {
    return List.of(
      createRead(dummyZcAccountKey)
    );
  }

  public static Instruction forIdlTypeGenerationDoNotCall(final AccountMeta invokedLbClmmProgramMeta,
                                                          final PublicKey dummyZcAccountKey,
                                                          final DummyIx ix) {
    final var keys = forIdlTypeGenerationDoNotCallKeys(
      dummyZcAccountKey
    );
    return forIdlTypeGenerationDoNotCall(invokedLbClmmProgramMeta, keys, ix);
  }

  public static Instruction forIdlTypeGenerationDoNotCall(final AccountMeta invokedLbClmmProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final DummyIx ix) {
    final byte[] _data = new byte[8 + ix.l()];
    int i = FOR_IDL_TYPE_GENERATION_DO_NOT_CALL_DISCRIMINATOR.write(_data, 0);
    ix.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record ForIdlTypeGenerationDoNotCallIxData(Discriminator discriminator, DummyIx ix) implements SerDe {  

    public static ForIdlTypeGenerationDoNotCallIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 14;

    public static ForIdlTypeGenerationDoNotCallIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ix = DummyIx.read(_data, i);
      return new ForIdlTypeGenerationDoNotCallIxData(discriminator, ix);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += ix.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FUND_REWARD_DISCRIMINATOR = toDiscriminator(188, 50, 249, 165, 93, 151, 38, 63);

  public static List<AccountMeta> fundRewardKeys(final PublicKey lbPairKey,
                                                 final PublicKey rewardVaultKey,
                                                 final PublicKey rewardMintKey,
                                                 final PublicKey funderTokenAccountKey,
                                                 final PublicKey funderKey,
                                                 final PublicKey binArrayKey,
                                                 final PublicKey tokenProgramKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(rewardVaultKey),
      createRead(rewardMintKey),
      createWrite(funderTokenAccountKey),
      createReadOnlySigner(funderKey),
      createWrite(binArrayKey),
      createRead(tokenProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction fundReward(final AccountMeta invokedLbClmmProgramMeta,
                                       final PublicKey lbPairKey,
                                       final PublicKey rewardVaultKey,
                                       final PublicKey rewardMintKey,
                                       final PublicKey funderTokenAccountKey,
                                       final PublicKey funderKey,
                                       final PublicKey binArrayKey,
                                       final PublicKey tokenProgramKey,
                                       final PublicKey eventAuthorityKey,
                                       final PublicKey programKey,
                                       final long rewardIndex,
                                       final long amount,
                                       final boolean carryForward,
                                       final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = fundRewardKeys(
      lbPairKey,
      rewardVaultKey,
      rewardMintKey,
      funderTokenAccountKey,
      funderKey,
      binArrayKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return fundReward(
      invokedLbClmmProgramMeta,
      keys,
      rewardIndex,
      amount,
      carryForward,
      remainingAccountsInfo
    );
  }

  public static Instruction fundReward(final AccountMeta invokedLbClmmProgramMeta,
                                       final List<AccountMeta> keys,
                                       final long rewardIndex,
                                       final long amount,
                                       final boolean carryForward,
                                       final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[25 + remainingAccountsInfo.l()];
    int i = FUND_REWARD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) (carryForward ? 1 : 0);
    ++i;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record FundRewardIxData(Discriminator discriminator,
                                 long rewardIndex,
                                 long amount,
                                 boolean carryForward,
                                 RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static FundRewardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static FundRewardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      i += 8;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var carryForward = _data[i] == 1;
      ++i;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new FundRewardIxData(discriminator,
                                  rewardIndex,
                                  amount,
                                  carryForward,
                                  remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) (carryForward ? 1 : 0);
      ++i;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 8 + 1 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator GO_TO_A_BIN_DISCRIMINATOR = toDiscriminator(146, 72, 174, 224, 40, 253, 84, 174);

  public static List<AccountMeta> goToABinKeys(final AccountMeta invokedLbClmmProgramMeta,
                                               final PublicKey lbPairKey,
                                               final PublicKey binArrayBitmapExtensionKey,
                                               final PublicKey fromBinArrayKey,
                                               final PublicKey toBinArrayKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(requireNonNullElse(fromBinArrayKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(requireNonNullElse(toBinArrayKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction goToABin(final AccountMeta invokedLbClmmProgramMeta,
                                     final PublicKey lbPairKey,
                                     final PublicKey binArrayBitmapExtensionKey,
                                     final PublicKey fromBinArrayKey,
                                     final PublicKey toBinArrayKey,
                                     final PublicKey eventAuthorityKey,
                                     final PublicKey programKey,
                                     final int binId) {
    final var keys = goToABinKeys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      fromBinArrayKey,
      toBinArrayKey,
      eventAuthorityKey,
      programKey
    );
    return goToABin(invokedLbClmmProgramMeta, keys, binId);
  }

  public static Instruction goToABin(final AccountMeta invokedLbClmmProgramMeta,
                                     final List<AccountMeta> keys,
                                     final int binId) {
    final byte[] _data = new byte[12];
    int i = GO_TO_A_BIN_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, binId);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record GoToABinIxData(Discriminator discriminator, int binId) implements SerDe {  

    public static GoToABinIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static GoToABinIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var binId = getInt32LE(_data, i);
      return new GoToABinIxData(discriminator, binId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, binId);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INCREASE_ORACLE_LENGTH_DISCRIMINATOR = toDiscriminator(190, 61, 125, 87, 103, 79, 158, 173);

  public static List<AccountMeta> increaseOracleLengthKeys(final SolanaAccounts solanaAccounts,
                                                           final PublicKey oracleKey,
                                                           final PublicKey funderKey,
                                                           final PublicKey eventAuthorityKey,
                                                           final PublicKey programKey) {
    return List.of(
      createWrite(oracleKey),
      createWritableSigner(funderKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction increaseOracleLength(final AccountMeta invokedLbClmmProgramMeta,
                                                 final SolanaAccounts solanaAccounts,
                                                 final PublicKey oracleKey,
                                                 final PublicKey funderKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey,
                                                 final long lengthToAdd) {
    final var keys = increaseOracleLengthKeys(
      solanaAccounts,
      oracleKey,
      funderKey,
      eventAuthorityKey,
      programKey
    );
    return increaseOracleLength(invokedLbClmmProgramMeta, keys, lengthToAdd);
  }

  public static Instruction increaseOracleLength(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long lengthToAdd) {
    final byte[] _data = new byte[16];
    int i = INCREASE_ORACLE_LENGTH_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, lengthToAdd);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record IncreaseOracleLengthIxData(Discriminator discriminator, long lengthToAdd) implements SerDe {  

    public static IncreaseOracleLengthIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static IncreaseOracleLengthIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lengthToAdd = getInt64LE(_data, i);
      return new IncreaseOracleLengthIxData(discriminator, lengthToAdd);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, lengthToAdd);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INCREASE_POSITION_LENGTH_DISCRIMINATOR = toDiscriminator(80, 83, 117, 211, 66, 13, 33, 149);

  public static List<AccountMeta> increasePositionLengthKeys(final SolanaAccounts solanaAccounts,
                                                             final PublicKey funderKey,
                                                             final PublicKey lbPairKey,
                                                             final PublicKey positionKey,
                                                             final PublicKey ownerKey,
                                                             final PublicKey eventAuthorityKey,
                                                             final PublicKey programKey) {
    return List.of(
      createWritableSigner(funderKey),
      createRead(lbPairKey),
      createWrite(positionKey),
      createReadOnlySigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction increasePositionLength(final AccountMeta invokedLbClmmProgramMeta,
                                                   final SolanaAccounts solanaAccounts,
                                                   final PublicKey funderKey,
                                                   final PublicKey lbPairKey,
                                                   final PublicKey positionKey,
                                                   final PublicKey ownerKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey,
                                                   final int lengthToAdd,
                                                   final int side) {
    final var keys = increasePositionLengthKeys(
      solanaAccounts,
      funderKey,
      lbPairKey,
      positionKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return increasePositionLength(invokedLbClmmProgramMeta, keys, lengthToAdd, side);
  }

  public static Instruction increasePositionLength(final AccountMeta invokedLbClmmProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final int lengthToAdd,
                                                   final int side) {
    final byte[] _data = new byte[11];
    int i = INCREASE_POSITION_LENGTH_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, lengthToAdd);
    i += 2;
    _data[i] = (byte) side;

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record IncreasePositionLengthIxData(Discriminator discriminator, int lengthToAdd, int side) implements SerDe {  

    public static IncreasePositionLengthIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static IncreasePositionLengthIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lengthToAdd = getInt16LE(_data, i);
      i += 2;
      final var side = _data[i] & 0xFF;
      return new IncreasePositionLengthIxData(discriminator, lengthToAdd, side);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, lengthToAdd);
      i += 2;
      _data[i] = (byte) side;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INCREASE_POSITION_LENGTH_2_DISCRIMINATOR = toDiscriminator(255, 210, 204, 71, 115, 137, 225, 113);

  public static List<AccountMeta> increasePositionLength2Keys(final SolanaAccounts solanaAccounts,
                                                              final PublicKey funderKey,
                                                              final PublicKey lbPairKey,
                                                              final PublicKey positionKey,
                                                              final PublicKey ownerKey,
                                                              final PublicKey eventAuthorityKey,
                                                              final PublicKey programKey) {
    return List.of(
      createWritableSigner(funderKey),
      createRead(lbPairKey),
      createWrite(positionKey),
      createReadOnlySigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction increasePositionLength2(final AccountMeta invokedLbClmmProgramMeta,
                                                    final SolanaAccounts solanaAccounts,
                                                    final PublicKey funderKey,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey positionKey,
                                                    final PublicKey ownerKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey,
                                                    final int minimumUpperBinId) {
    final var keys = increasePositionLength2Keys(
      solanaAccounts,
      funderKey,
      lbPairKey,
      positionKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return increasePositionLength2(invokedLbClmmProgramMeta, keys, minimumUpperBinId);
  }

  public static Instruction increasePositionLength2(final AccountMeta invokedLbClmmProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final int minimumUpperBinId) {
    final byte[] _data = new byte[12];
    int i = INCREASE_POSITION_LENGTH_2_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, minimumUpperBinId);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record IncreasePositionLength2IxData(Discriminator discriminator, int minimumUpperBinId) implements SerDe {  

    public static IncreasePositionLength2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static IncreasePositionLength2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var minimumUpperBinId = getInt32LE(_data, i);
      return new IncreasePositionLength2IxData(discriminator, minimumUpperBinId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, minimumUpperBinId);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_BIN_ARRAY_DISCRIMINATOR = toDiscriminator(35, 86, 19, 185, 78, 212, 75, 211);

  public static List<AccountMeta> initializeBinArrayKeys(final SolanaAccounts solanaAccounts,
                                                         final PublicKey lbPairKey,
                                                         final PublicKey binArrayKey,
                                                         final PublicKey funderKey) {
    return List.of(
      createRead(lbPairKey),
      createWrite(binArrayKey),
      createWritableSigner(funderKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction initializeBinArray(final AccountMeta invokedLbClmmProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey lbPairKey,
                                               final PublicKey binArrayKey,
                                               final PublicKey funderKey,
                                               final long index) {
    final var keys = initializeBinArrayKeys(
      solanaAccounts,
      lbPairKey,
      binArrayKey,
      funderKey
    );
    return initializeBinArray(invokedLbClmmProgramMeta, keys, index);
  }

  public static Instruction initializeBinArray(final AccountMeta invokedLbClmmProgramMeta,
                                               final List<AccountMeta> keys,
                                               final long index) {
    final byte[] _data = new byte[16];
    int i = INITIALIZE_BIN_ARRAY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, index);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializeBinArrayIxData(Discriminator discriminator, long index) implements SerDe {  

    public static InitializeBinArrayIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static InitializeBinArrayIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var index = getInt64LE(_data, i);
      return new InitializeBinArrayIxData(discriminator, index);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, index);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_BIN_ARRAY_BITMAP_EXTENSION_DISCRIMINATOR = toDiscriminator(47, 157, 226, 180, 12, 240, 33, 71);

  /// @param binArrayBitmapExtensionKey Initialize an account to store if a bin array is initialized.
  public static List<AccountMeta> initializeBinArrayBitmapExtensionKeys(final SolanaAccounts solanaAccounts,
                                                                        final PublicKey lbPairKey,
                                                                        final PublicKey binArrayBitmapExtensionKey,
                                                                        final PublicKey funderKey) {
    return List.of(
      createRead(lbPairKey),
      createWrite(binArrayBitmapExtensionKey),
      createWritableSigner(funderKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  /// @param binArrayBitmapExtensionKey Initialize an account to store if a bin array is initialized.
  public static Instruction initializeBinArrayBitmapExtension(final AccountMeta invokedLbClmmProgramMeta,
                                                              final SolanaAccounts solanaAccounts,
                                                              final PublicKey lbPairKey,
                                                              final PublicKey binArrayBitmapExtensionKey,
                                                              final PublicKey funderKey) {
    final var keys = initializeBinArrayBitmapExtensionKeys(
      solanaAccounts,
      lbPairKey,
      binArrayBitmapExtensionKey,
      funderKey
    );
    return initializeBinArrayBitmapExtension(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction initializeBinArrayBitmapExtension(final AccountMeta invokedLbClmmProgramMeta,
                                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, INITIALIZE_BIN_ARRAY_BITMAP_EXTENSION_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_CUSTOMIZABLE_PERMISSIONLESS_LB_PAIR_DISCRIMINATOR = toDiscriminator(46, 39, 41, 135, 111, 183, 200, 64);

  public static List<AccountMeta> initializeCustomizablePermissionlessLbPairKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                                                 final SolanaAccounts solanaAccounts,
                                                                                 final PublicKey lbPairKey,
                                                                                 final PublicKey binArrayBitmapExtensionKey,
                                                                                 final PublicKey tokenMintXKey,
                                                                                 final PublicKey tokenMintYKey,
                                                                                 final PublicKey reserveXKey,
                                                                                 final PublicKey reserveYKey,
                                                                                 final PublicKey oracleKey,
                                                                                 final PublicKey userTokenXKey,
                                                                                 final PublicKey funderKey,
                                                                                 final PublicKey tokenProgramKey,
                                                                                 final PublicKey userTokenYKey,
                                                                                 final PublicKey eventAuthorityKey,
                                                                                 final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenMintXKey),
      createRead(tokenMintYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(oracleKey),
      createRead(userTokenXKey),
      createWritableSigner(funderKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(userTokenYKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializeCustomizablePermissionlessLbPair(final AccountMeta invokedLbClmmProgramMeta,
                                                                       final SolanaAccounts solanaAccounts,
                                                                       final PublicKey lbPairKey,
                                                                       final PublicKey binArrayBitmapExtensionKey,
                                                                       final PublicKey tokenMintXKey,
                                                                       final PublicKey tokenMintYKey,
                                                                       final PublicKey reserveXKey,
                                                                       final PublicKey reserveYKey,
                                                                       final PublicKey oracleKey,
                                                                       final PublicKey userTokenXKey,
                                                                       final PublicKey funderKey,
                                                                       final PublicKey tokenProgramKey,
                                                                       final PublicKey userTokenYKey,
                                                                       final PublicKey eventAuthorityKey,
                                                                       final PublicKey programKey,
                                                                       final CustomizableParams params) {
    final var keys = initializeCustomizablePermissionlessLbPairKeys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      lbPairKey,
      binArrayBitmapExtensionKey,
      tokenMintXKey,
      tokenMintYKey,
      reserveXKey,
      reserveYKey,
      oracleKey,
      userTokenXKey,
      funderKey,
      tokenProgramKey,
      userTokenYKey,
      eventAuthorityKey,
      programKey
    );
    return initializeCustomizablePermissionlessLbPair(invokedLbClmmProgramMeta, keys, params);
  }

  public static Instruction initializeCustomizablePermissionlessLbPair(final AccountMeta invokedLbClmmProgramMeta,
                                                                       final List<AccountMeta> keys,
                                                                       final CustomizableParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = INITIALIZE_CUSTOMIZABLE_PERMISSIONLESS_LB_PAIR_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializeCustomizablePermissionlessLbPairIxData(Discriminator discriminator, CustomizableParams params) implements SerDe {  

    public static InitializeCustomizablePermissionlessLbPairIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeCustomizablePermissionlessLbPairIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CustomizableParams.read(_data, i);
      return new InitializeCustomizablePermissionlessLbPairIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator INITIALIZE_CUSTOMIZABLE_PERMISSIONLESS_LB_PAIR_2_DISCRIMINATOR = toDiscriminator(243, 73, 129, 126, 51, 19, 241, 107);

  public static List<AccountMeta> initializeCustomizablePermissionlessLbPair2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                                                  final SolanaAccounts solanaAccounts,
                                                                                  final PublicKey lbPairKey,
                                                                                  final PublicKey binArrayBitmapExtensionKey,
                                                                                  final PublicKey tokenMintXKey,
                                                                                  final PublicKey tokenMintYKey,
                                                                                  final PublicKey reserveXKey,
                                                                                  final PublicKey reserveYKey,
                                                                                  final PublicKey oracleKey,
                                                                                  final PublicKey userTokenXKey,
                                                                                  final PublicKey funderKey,
                                                                                  final PublicKey tokenBadgeXKey,
                                                                                  final PublicKey tokenBadgeYKey,
                                                                                  final PublicKey tokenProgramXKey,
                                                                                  final PublicKey tokenProgramYKey,
                                                                                  final PublicKey userTokenYKey,
                                                                                  final PublicKey eventAuthorityKey,
                                                                                  final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenMintXKey),
      createRead(tokenMintYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(oracleKey),
      createRead(userTokenXKey),
      createWritableSigner(funderKey),
      createRead(requireNonNullElse(tokenBadgeXKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(requireNonNullElse(tokenBadgeYKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenProgramXKey),
      createRead(tokenProgramYKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(userTokenYKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializeCustomizablePermissionlessLbPair2(final AccountMeta invokedLbClmmProgramMeta,
                                                                        final SolanaAccounts solanaAccounts,
                                                                        final PublicKey lbPairKey,
                                                                        final PublicKey binArrayBitmapExtensionKey,
                                                                        final PublicKey tokenMintXKey,
                                                                        final PublicKey tokenMintYKey,
                                                                        final PublicKey reserveXKey,
                                                                        final PublicKey reserveYKey,
                                                                        final PublicKey oracleKey,
                                                                        final PublicKey userTokenXKey,
                                                                        final PublicKey funderKey,
                                                                        final PublicKey tokenBadgeXKey,
                                                                        final PublicKey tokenBadgeYKey,
                                                                        final PublicKey tokenProgramXKey,
                                                                        final PublicKey tokenProgramYKey,
                                                                        final PublicKey userTokenYKey,
                                                                        final PublicKey eventAuthorityKey,
                                                                        final PublicKey programKey,
                                                                        final CustomizableParams params) {
    final var keys = initializeCustomizablePermissionlessLbPair2Keys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      lbPairKey,
      binArrayBitmapExtensionKey,
      tokenMintXKey,
      tokenMintYKey,
      reserveXKey,
      reserveYKey,
      oracleKey,
      userTokenXKey,
      funderKey,
      tokenBadgeXKey,
      tokenBadgeYKey,
      tokenProgramXKey,
      tokenProgramYKey,
      userTokenYKey,
      eventAuthorityKey,
      programKey
    );
    return initializeCustomizablePermissionlessLbPair2(invokedLbClmmProgramMeta, keys, params);
  }

  public static Instruction initializeCustomizablePermissionlessLbPair2(final AccountMeta invokedLbClmmProgramMeta,
                                                                        final List<AccountMeta> keys,
                                                                        final CustomizableParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = INITIALIZE_CUSTOMIZABLE_PERMISSIONLESS_LB_PAIR_2_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializeCustomizablePermissionlessLbPair2IxData(Discriminator discriminator, CustomizableParams params) implements SerDe {  

    public static InitializeCustomizablePermissionlessLbPair2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeCustomizablePermissionlessLbPair2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = CustomizableParams.read(_data, i);
      return new InitializeCustomizablePermissionlessLbPair2IxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator INITIALIZE_LB_PAIR_DISCRIMINATOR = toDiscriminator(45, 154, 237, 210, 221, 15, 166, 92);

  public static List<AccountMeta> initializeLbPairKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                       final SolanaAccounts solanaAccounts,
                                                       final PublicKey lbPairKey,
                                                       final PublicKey binArrayBitmapExtensionKey,
                                                       final PublicKey tokenMintXKey,
                                                       final PublicKey tokenMintYKey,
                                                       final PublicKey reserveXKey,
                                                       final PublicKey reserveYKey,
                                                       final PublicKey oracleKey,
                                                       final PublicKey presetParameterKey,
                                                       final PublicKey funderKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenMintXKey),
      createRead(tokenMintYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(oracleKey),
      createRead(presetParameterKey),
      createWritableSigner(funderKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializeLbPair(final AccountMeta invokedLbClmmProgramMeta,
                                             final SolanaAccounts solanaAccounts,
                                             final PublicKey lbPairKey,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey tokenMintXKey,
                                             final PublicKey tokenMintYKey,
                                             final PublicKey reserveXKey,
                                             final PublicKey reserveYKey,
                                             final PublicKey oracleKey,
                                             final PublicKey presetParameterKey,
                                             final PublicKey funderKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey,
                                             final int activeId,
                                             final int binStep) {
    final var keys = initializeLbPairKeys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      lbPairKey,
      binArrayBitmapExtensionKey,
      tokenMintXKey,
      tokenMintYKey,
      reserveXKey,
      reserveYKey,
      oracleKey,
      presetParameterKey,
      funderKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return initializeLbPair(invokedLbClmmProgramMeta, keys, activeId, binStep);
  }

  public static Instruction initializeLbPair(final AccountMeta invokedLbClmmProgramMeta,
                                             final List<AccountMeta> keys,
                                             final int activeId,
                                             final int binStep) {
    final byte[] _data = new byte[14];
    int i = INITIALIZE_LB_PAIR_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, activeId);
    i += 4;
    putInt16LE(_data, i, binStep);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializeLbPairIxData(Discriminator discriminator, int activeId, int binStep) implements SerDe {  

    public static InitializeLbPairIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 14;

    public static InitializeLbPairIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var activeId = getInt32LE(_data, i);
      i += 4;
      final var binStep = getInt16LE(_data, i);
      return new InitializeLbPairIxData(discriminator, activeId, binStep);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, activeId);
      i += 4;
      putInt16LE(_data, i, binStep);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_LB_PAIR_2_DISCRIMINATOR = toDiscriminator(73, 59, 36, 120, 237, 83, 108, 198);

  public static List<AccountMeta> initializeLbPair2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                        final SolanaAccounts solanaAccounts,
                                                        final PublicKey lbPairKey,
                                                        final PublicKey binArrayBitmapExtensionKey,
                                                        final PublicKey tokenMintXKey,
                                                        final PublicKey tokenMintYKey,
                                                        final PublicKey reserveXKey,
                                                        final PublicKey reserveYKey,
                                                        final PublicKey oracleKey,
                                                        final PublicKey presetParameterKey,
                                                        final PublicKey funderKey,
                                                        final PublicKey tokenBadgeXKey,
                                                        final PublicKey tokenBadgeYKey,
                                                        final PublicKey tokenProgramXKey,
                                                        final PublicKey tokenProgramYKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenMintXKey),
      createRead(tokenMintYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(oracleKey),
      createRead(presetParameterKey),
      createWritableSigner(funderKey),
      createRead(requireNonNullElse(tokenBadgeXKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(requireNonNullElse(tokenBadgeYKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenProgramXKey),
      createRead(tokenProgramYKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializeLbPair2(final AccountMeta invokedLbClmmProgramMeta,
                                              final SolanaAccounts solanaAccounts,
                                              final PublicKey lbPairKey,
                                              final PublicKey binArrayBitmapExtensionKey,
                                              final PublicKey tokenMintXKey,
                                              final PublicKey tokenMintYKey,
                                              final PublicKey reserveXKey,
                                              final PublicKey reserveYKey,
                                              final PublicKey oracleKey,
                                              final PublicKey presetParameterKey,
                                              final PublicKey funderKey,
                                              final PublicKey tokenBadgeXKey,
                                              final PublicKey tokenBadgeYKey,
                                              final PublicKey tokenProgramXKey,
                                              final PublicKey tokenProgramYKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey,
                                              final InitializeLbPair2Params params) {
    final var keys = initializeLbPair2Keys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      lbPairKey,
      binArrayBitmapExtensionKey,
      tokenMintXKey,
      tokenMintYKey,
      reserveXKey,
      reserveYKey,
      oracleKey,
      presetParameterKey,
      funderKey,
      tokenBadgeXKey,
      tokenBadgeYKey,
      tokenProgramXKey,
      tokenProgramYKey,
      eventAuthorityKey,
      programKey
    );
    return initializeLbPair2(invokedLbClmmProgramMeta, keys, params);
  }

  public static Instruction initializeLbPair2(final AccountMeta invokedLbClmmProgramMeta,
                                              final List<AccountMeta> keys,
                                              final InitializeLbPair2Params params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = INITIALIZE_LB_PAIR_2_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializeLbPair2IxData(Discriminator discriminator, InitializeLbPair2Params params) implements SerDe {  

    public static InitializeLbPair2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 108;

    public static InitializeLbPair2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = InitializeLbPair2Params.read(_data, i);
      return new InitializeLbPair2IxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PERMISSION_LB_PAIR_DISCRIMINATOR = toDiscriminator(108, 102, 213, 85, 251, 3, 53, 21);

  public static List<AccountMeta> initializePermissionLbPairKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                                 final SolanaAccounts solanaAccounts,
                                                                 final PublicKey baseKey,
                                                                 final PublicKey lbPairKey,
                                                                 final PublicKey binArrayBitmapExtensionKey,
                                                                 final PublicKey tokenMintXKey,
                                                                 final PublicKey tokenMintYKey,
                                                                 final PublicKey reserveXKey,
                                                                 final PublicKey reserveYKey,
                                                                 final PublicKey oracleKey,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey tokenBadgeXKey,
                                                                 final PublicKey tokenBadgeYKey,
                                                                 final PublicKey tokenProgramXKey,
                                                                 final PublicKey tokenProgramYKey,
                                                                 final PublicKey eventAuthorityKey,
                                                                 final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(baseKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenMintXKey),
      createRead(tokenMintYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(oracleKey),
      createWritableSigner(adminKey),
      createRead(requireNonNullElse(tokenBadgeXKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(requireNonNullElse(tokenBadgeYKey, invokedLbClmmProgramMeta.publicKey())),
      createRead(tokenProgramXKey),
      createRead(tokenProgramYKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializePermissionLbPair(final AccountMeta invokedLbClmmProgramMeta,
                                                       final SolanaAccounts solanaAccounts,
                                                       final PublicKey baseKey,
                                                       final PublicKey lbPairKey,
                                                       final PublicKey binArrayBitmapExtensionKey,
                                                       final PublicKey tokenMintXKey,
                                                       final PublicKey tokenMintYKey,
                                                       final PublicKey reserveXKey,
                                                       final PublicKey reserveYKey,
                                                       final PublicKey oracleKey,
                                                       final PublicKey adminKey,
                                                       final PublicKey tokenBadgeXKey,
                                                       final PublicKey tokenBadgeYKey,
                                                       final PublicKey tokenProgramXKey,
                                                       final PublicKey tokenProgramYKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey,
                                                       final InitPermissionPairIx ixData) {
    final var keys = initializePermissionLbPairKeys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      baseKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      tokenMintXKey,
      tokenMintYKey,
      reserveXKey,
      reserveYKey,
      oracleKey,
      adminKey,
      tokenBadgeXKey,
      tokenBadgeYKey,
      tokenProgramXKey,
      tokenProgramYKey,
      eventAuthorityKey,
      programKey
    );
    return initializePermissionLbPair(invokedLbClmmProgramMeta, keys, ixData);
  }

  public static Instruction initializePermissionLbPair(final AccountMeta invokedLbClmmProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final InitPermissionPairIx ixData) {
    final byte[] _data = new byte[8 + ixData.l()];
    int i = INITIALIZE_PERMISSION_LB_PAIR_DISCRIMINATOR.write(_data, 0);
    ixData.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePermissionLbPairIxData(Discriminator discriminator, InitPermissionPairIx ixData) implements SerDe {  

    public static InitializePermissionLbPairIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 20;

    public static InitializePermissionLbPairIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ixData = InitPermissionPairIx.read(_data, i);
      return new InitializePermissionLbPairIxData(discriminator, ixData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += ixData.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_POSITION_DISCRIMINATOR = toDiscriminator(219, 192, 234, 71, 190, 191, 102, 80);

  public static List<AccountMeta> initializePositionKeys(final SolanaAccounts solanaAccounts,
                                                         final PublicKey payerKey,
                                                         final PublicKey positionKey,
                                                         final PublicKey lbPairKey,
                                                         final PublicKey ownerKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWritableSigner(positionKey),
      createRead(lbPairKey),
      createReadOnlySigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializePosition(final AccountMeta invokedLbClmmProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey payerKey,
                                               final PublicKey positionKey,
                                               final PublicKey lbPairKey,
                                               final PublicKey ownerKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey,
                                               final int lowerBinId,
                                               final int width) {
    final var keys = initializePositionKeys(
      solanaAccounts,
      payerKey,
      positionKey,
      lbPairKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return initializePosition(invokedLbClmmProgramMeta, keys, lowerBinId, width);
  }

  public static Instruction initializePosition(final AccountMeta invokedLbClmmProgramMeta,
                                               final List<AccountMeta> keys,
                                               final int lowerBinId,
                                               final int width) {
    final byte[] _data = new byte[16];
    int i = INITIALIZE_POSITION_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, lowerBinId);
    i += 4;
    putInt32LE(_data, i, width);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePositionIxData(Discriminator discriminator, int lowerBinId, int width) implements SerDe {  

    public static InitializePositionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static InitializePositionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lowerBinId = getInt32LE(_data, i);
      i += 4;
      final var width = getInt32LE(_data, i);
      return new InitializePositionIxData(discriminator, lowerBinId, width);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, lowerBinId);
      i += 4;
      putInt32LE(_data, i, width);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_POSITION_2_DISCRIMINATOR = toDiscriminator(143, 19, 242, 145, 213, 15, 104, 115);

  public static List<AccountMeta> initializePosition2Keys(final SolanaAccounts solanaAccounts,
                                                          final PublicKey payerKey,
                                                          final PublicKey positionKey,
                                                          final PublicKey lbPairKey,
                                                          final PublicKey ownerKey,
                                                          final PublicKey eventAuthorityKey,
                                                          final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWritableSigner(positionKey),
      createRead(lbPairKey),
      createReadOnlySigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializePosition2(final AccountMeta invokedLbClmmProgramMeta,
                                                final SolanaAccounts solanaAccounts,
                                                final PublicKey payerKey,
                                                final PublicKey positionKey,
                                                final PublicKey lbPairKey,
                                                final PublicKey ownerKey,
                                                final PublicKey eventAuthorityKey,
                                                final PublicKey programKey,
                                                final int lowerBinId,
                                                final int width) {
    final var keys = initializePosition2Keys(
      solanaAccounts,
      payerKey,
      positionKey,
      lbPairKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return initializePosition2(invokedLbClmmProgramMeta, keys, lowerBinId, width);
  }

  public static Instruction initializePosition2(final AccountMeta invokedLbClmmProgramMeta,
                                                final List<AccountMeta> keys,
                                                final int lowerBinId,
                                                final int width) {
    final byte[] _data = new byte[16];
    int i = INITIALIZE_POSITION_2_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, lowerBinId);
    i += 4;
    putInt32LE(_data, i, width);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePosition2IxData(Discriminator discriminator, int lowerBinId, int width) implements SerDe {  

    public static InitializePosition2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static InitializePosition2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lowerBinId = getInt32LE(_data, i);
      i += 4;
      final var width = getInt32LE(_data, i);
      return new InitializePosition2IxData(discriminator, lowerBinId, width);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, lowerBinId);
      i += 4;
      putInt32LE(_data, i, width);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_POSITION_BY_OPERATOR_DISCRIMINATOR = toDiscriminator(251, 189, 190, 244, 117, 254, 35, 148);

  /// @param operatorKey operator
  public static List<AccountMeta> initializePositionByOperatorKeys(final SolanaAccounts solanaAccounts,
                                                                   final PublicKey payerKey,
                                                                   final PublicKey baseKey,
                                                                   final PublicKey positionKey,
                                                                   final PublicKey lbPairKey,
                                                                   final PublicKey ownerKey,
                                                                   final PublicKey operatorKey,
                                                                   final PublicKey operatorTokenXKey,
                                                                   final PublicKey ownerTokenXKey,
                                                                   final PublicKey eventAuthorityKey,
                                                                   final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(baseKey),
      createWrite(positionKey),
      createRead(lbPairKey),
      createRead(ownerKey),
      createReadOnlySigner(operatorKey),
      createRead(operatorTokenXKey),
      createRead(ownerTokenXKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// @param operatorKey operator
  public static Instruction initializePositionByOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                         final SolanaAccounts solanaAccounts,
                                                         final PublicKey payerKey,
                                                         final PublicKey baseKey,
                                                         final PublicKey positionKey,
                                                         final PublicKey lbPairKey,
                                                         final PublicKey ownerKey,
                                                         final PublicKey operatorKey,
                                                         final PublicKey operatorTokenXKey,
                                                         final PublicKey ownerTokenXKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey,
                                                         final int lowerBinId,
                                                         final int width,
                                                         final PublicKey feeOwner,
                                                         final long lockReleasePoint) {
    final var keys = initializePositionByOperatorKeys(
      solanaAccounts,
      payerKey,
      baseKey,
      positionKey,
      lbPairKey,
      ownerKey,
      operatorKey,
      operatorTokenXKey,
      ownerTokenXKey,
      eventAuthorityKey,
      programKey
    );
    return initializePositionByOperator(
      invokedLbClmmProgramMeta,
      keys,
      lowerBinId,
      width,
      feeOwner,
      lockReleasePoint
    );
  }

  public static Instruction initializePositionByOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final int lowerBinId,
                                                         final int width,
                                                         final PublicKey feeOwner,
                                                         final long lockReleasePoint) {
    final byte[] _data = new byte[56];
    int i = INITIALIZE_POSITION_BY_OPERATOR_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, lowerBinId);
    i += 4;
    putInt32LE(_data, i, width);
    i += 4;
    feeOwner.write(_data, i);
    i += 32;
    putInt64LE(_data, i, lockReleasePoint);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePositionByOperatorIxData(Discriminator discriminator,
                                                   int lowerBinId,
                                                   int width,
                                                   PublicKey feeOwner,
                                                   long lockReleasePoint) implements SerDe {  

    public static InitializePositionByOperatorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 56;

    public static InitializePositionByOperatorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lowerBinId = getInt32LE(_data, i);
      i += 4;
      final var width = getInt32LE(_data, i);
      i += 4;
      final var feeOwner = readPubKey(_data, i);
      i += 32;
      final var lockReleasePoint = getInt64LE(_data, i);
      return new InitializePositionByOperatorIxData(discriminator,
                                                    lowerBinId,
                                                    width,
                                                    feeOwner,
                                                    lockReleasePoint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, lowerBinId);
      i += 4;
      putInt32LE(_data, i, width);
      i += 4;
      feeOwner.write(_data, i);
      i += 32;
      putInt64LE(_data, i, lockReleasePoint);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_POSITION_PDA_DISCRIMINATOR = toDiscriminator(46, 82, 125, 146, 85, 141, 228, 153);

  /// @param ownerKey owner
  public static List<AccountMeta> initializePositionPdaKeys(final SolanaAccounts solanaAccounts,
                                                            final PublicKey payerKey,
                                                            final PublicKey baseKey,
                                                            final PublicKey positionKey,
                                                            final PublicKey lbPairKey,
                                                            final PublicKey ownerKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(baseKey),
      createWrite(positionKey),
      createRead(lbPairKey),
      createReadOnlySigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// @param ownerKey owner
  public static Instruction initializePositionPda(final AccountMeta invokedLbClmmProgramMeta,
                                                  final SolanaAccounts solanaAccounts,
                                                  final PublicKey payerKey,
                                                  final PublicKey baseKey,
                                                  final PublicKey positionKey,
                                                  final PublicKey lbPairKey,
                                                  final PublicKey ownerKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final int lowerBinId,
                                                  final int width) {
    final var keys = initializePositionPdaKeys(
      solanaAccounts,
      payerKey,
      baseKey,
      positionKey,
      lbPairKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return initializePositionPda(invokedLbClmmProgramMeta, keys, lowerBinId, width);
  }

  public static Instruction initializePositionPda(final AccountMeta invokedLbClmmProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final int lowerBinId,
                                                  final int width) {
    final byte[] _data = new byte[16];
    int i = INITIALIZE_POSITION_PDA_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, lowerBinId);
    i += 4;
    putInt32LE(_data, i, width);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePositionPdaIxData(Discriminator discriminator, int lowerBinId, int width) implements SerDe {  

    public static InitializePositionPdaIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static InitializePositionPdaIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lowerBinId = getInt32LE(_data, i);
      i += 4;
      final var width = getInt32LE(_data, i);
      return new InitializePositionPdaIxData(discriminator, lowerBinId, width);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, lowerBinId);
      i += 4;
      putInt32LE(_data, i, width);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PRESET_PARAMETER_DISCRIMINATOR = toDiscriminator(66, 188, 71, 211, 98, 109, 14, 186);

  public static List<AccountMeta> initializePresetParameterKeys(final SolanaAccounts solanaAccounts,
                                                                final PublicKey presetParameterKey,
                                                                final PublicKey adminKey) {
    return List.of(
      createWrite(presetParameterKey),
      createWritableSigner(adminKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  public static Instruction initializePresetParameter(final AccountMeta invokedLbClmmProgramMeta,
                                                      final SolanaAccounts solanaAccounts,
                                                      final PublicKey presetParameterKey,
                                                      final PublicKey adminKey,
                                                      final InitPresetParametersIx ix) {
    final var keys = initializePresetParameterKeys(
      solanaAccounts,
      presetParameterKey,
      adminKey
    );
    return initializePresetParameter(invokedLbClmmProgramMeta, keys, ix);
  }

  public static Instruction initializePresetParameter(final AccountMeta invokedLbClmmProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final InitPresetParametersIx ix) {
    final byte[] _data = new byte[8 + ix.l()];
    int i = INITIALIZE_PRESET_PARAMETER_DISCRIMINATOR.write(_data, 0);
    ix.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePresetParameterIxData(Discriminator discriminator, InitPresetParametersIx ix) implements SerDe {  

    public static InitializePresetParameterIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 28;

    public static InitializePresetParameterIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ix = InitPresetParametersIx.read(_data, i);
      return new InitializePresetParameterIxData(discriminator, ix);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += ix.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PRESET_PARAMETER_2_DISCRIMINATOR = toDiscriminator(184, 7, 240, 171, 103, 47, 183, 121);

  public static List<AccountMeta> initializePresetParameter2Keys(final SolanaAccounts solanaAccounts,
                                                                 final PublicKey presetParameterKey,
                                                                 final PublicKey adminKey) {
    return List.of(
      createWrite(presetParameterKey),
      createWritableSigner(adminKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction initializePresetParameter2(final AccountMeta invokedLbClmmProgramMeta,
                                                       final SolanaAccounts solanaAccounts,
                                                       final PublicKey presetParameterKey,
                                                       final PublicKey adminKey,
                                                       final InitPresetParameters2Ix ix) {
    final var keys = initializePresetParameter2Keys(
      solanaAccounts,
      presetParameterKey,
      adminKey
    );
    return initializePresetParameter2(invokedLbClmmProgramMeta, keys, ix);
  }

  public static Instruction initializePresetParameter2(final AccountMeta invokedLbClmmProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final InitPresetParameters2Ix ix) {
    final byte[] _data = new byte[8 + ix.l()];
    int i = INITIALIZE_PRESET_PARAMETER_2_DISCRIMINATOR.write(_data, 0);
    ix.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializePresetParameter2IxData(Discriminator discriminator, InitPresetParameters2Ix ix) implements SerDe {  

    public static InitializePresetParameter2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 31;

    public static InitializePresetParameter2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ix = InitPresetParameters2Ix.read(_data, i);
      return new InitializePresetParameter2IxData(discriminator, ix);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += ix.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_REWARD_DISCRIMINATOR = toDiscriminator(95, 135, 192, 196, 242, 129, 230, 68);

  public static List<AccountMeta> initializeRewardKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                       final SolanaAccounts solanaAccounts,
                                                       final PublicKey lbPairKey,
                                                       final PublicKey rewardVaultKey,
                                                       final PublicKey rewardMintKey,
                                                       final PublicKey tokenBadgeKey,
                                                       final PublicKey adminKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(rewardVaultKey),
      createRead(rewardMintKey),
      createRead(requireNonNullElse(tokenBadgeKey, invokedLbClmmProgramMeta.publicKey())),
      createWritableSigner(adminKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initializeReward(final AccountMeta invokedLbClmmProgramMeta,
                                             final SolanaAccounts solanaAccounts,
                                             final PublicKey lbPairKey,
                                             final PublicKey rewardVaultKey,
                                             final PublicKey rewardMintKey,
                                             final PublicKey tokenBadgeKey,
                                             final PublicKey adminKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey,
                                             final long rewardIndex,
                                             final long rewardDuration,
                                             final PublicKey funder) {
    final var keys = initializeRewardKeys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      lbPairKey,
      rewardVaultKey,
      rewardMintKey,
      tokenBadgeKey,
      adminKey,
      tokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return initializeReward(
      invokedLbClmmProgramMeta,
      keys,
      rewardIndex,
      rewardDuration,
      funder
    );
  }

  public static Instruction initializeReward(final AccountMeta invokedLbClmmProgramMeta,
                                             final List<AccountMeta> keys,
                                             final long rewardIndex,
                                             final long rewardDuration,
                                             final PublicKey funder) {
    final byte[] _data = new byte[56];
    int i = INITIALIZE_REWARD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, rewardDuration);
    i += 8;
    funder.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record InitializeRewardIxData(Discriminator discriminator,
                                       long rewardIndex,
                                       long rewardDuration,
                                       PublicKey funder) implements SerDe {  

    public static InitializeRewardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 56;

    public static InitializeRewardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      i += 8;
      final var rewardDuration = getInt64LE(_data, i);
      i += 8;
      final var funder = readPubKey(_data, i);
      return new InitializeRewardIxData(discriminator, rewardIndex, rewardDuration, funder);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      putInt64LE(_data, i, rewardDuration);
      i += 8;
      funder.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_TOKEN_BADGE_DISCRIMINATOR = toDiscriminator(253, 77, 205, 95, 27, 224, 89, 223);

  public static List<AccountMeta> initializeTokenBadgeKeys(final SolanaAccounts solanaAccounts,
                                                           final PublicKey tokenMintKey,
                                                           final PublicKey tokenBadgeKey,
                                                           final PublicKey adminKey) {
    return List.of(
      createRead(tokenMintKey),
      createWrite(tokenBadgeKey),
      createWritableSigner(adminKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction initializeTokenBadge(final AccountMeta invokedLbClmmProgramMeta,
                                                 final SolanaAccounts solanaAccounts,
                                                 final PublicKey tokenMintKey,
                                                 final PublicKey tokenBadgeKey,
                                                 final PublicKey adminKey) {
    final var keys = initializeTokenBadgeKeys(
      solanaAccounts,
      tokenMintKey,
      tokenBadgeKey,
      adminKey
    );
    return initializeTokenBadge(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction initializeTokenBadge(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, INITIALIZE_TOKEN_BADGE_DISCRIMINATOR);
  }

  public static final Discriminator MIGRATE_BIN_ARRAY_DISCRIMINATOR = toDiscriminator(17, 23, 159, 211, 101, 184, 41, 241);

  public static List<AccountMeta> migrateBinArrayKeys(final PublicKey lbPairKey) {
    return List.of(
      createRead(lbPairKey)
    );
  }

  public static Instruction migrateBinArray(final AccountMeta invokedLbClmmProgramMeta,
                                            final PublicKey lbPairKey) {
    final var keys = migrateBinArrayKeys(
      lbPairKey
    );
    return migrateBinArray(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction migrateBinArray(final AccountMeta invokedLbClmmProgramMeta,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, MIGRATE_BIN_ARRAY_DISCRIMINATOR);
  }

  public static final Discriminator MIGRATE_POSITION_DISCRIMINATOR = toDiscriminator(15, 132, 59, 50, 199, 6, 251, 46);

  public static List<AccountMeta> migratePositionKeys(final SolanaAccounts solanaAccounts,
                                                      final PublicKey positionV2Key,
                                                      final PublicKey positionV1Key,
                                                      final PublicKey lbPairKey,
                                                      final PublicKey binArrayLowerKey,
                                                      final PublicKey binArrayUpperKey,
                                                      final PublicKey ownerKey,
                                                      final PublicKey rentReceiverKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createWritableSigner(positionV2Key),
      createWrite(positionV1Key),
      createRead(lbPairKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createWritableSigner(ownerKey),
      createRead(solanaAccounts.systemProgram()),
      createWrite(rentReceiverKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction migratePosition(final AccountMeta invokedLbClmmProgramMeta,
                                            final SolanaAccounts solanaAccounts,
                                            final PublicKey positionV2Key,
                                            final PublicKey positionV1Key,
                                            final PublicKey lbPairKey,
                                            final PublicKey binArrayLowerKey,
                                            final PublicKey binArrayUpperKey,
                                            final PublicKey ownerKey,
                                            final PublicKey rentReceiverKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey) {
    final var keys = migratePositionKeys(
      solanaAccounts,
      positionV2Key,
      positionV1Key,
      lbPairKey,
      binArrayLowerKey,
      binArrayUpperKey,
      ownerKey,
      rentReceiverKey,
      eventAuthorityKey,
      programKey
    );
    return migratePosition(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction migratePosition(final AccountMeta invokedLbClmmProgramMeta,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, MIGRATE_POSITION_DISCRIMINATOR);
  }

  public static final Discriminator REBALANCE_LIQUIDITY_DISCRIMINATOR = toDiscriminator(92, 4, 176, 193, 119, 185, 83, 9);

  public static List<AccountMeta> rebalanceLiquidityKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                         final SolanaAccounts solanaAccounts,
                                                         final PublicKey positionKey,
                                                         final PublicKey lbPairKey,
                                                         final PublicKey binArrayBitmapExtensionKey,
                                                         final PublicKey userTokenXKey,
                                                         final PublicKey userTokenYKey,
                                                         final PublicKey reserveXKey,
                                                         final PublicKey reserveYKey,
                                                         final PublicKey tokenXMintKey,
                                                         final PublicKey tokenYMintKey,
                                                         final PublicKey ownerKey,
                                                         final PublicKey rentPayerKey,
                                                         final PublicKey tokenXProgramKey,
                                                         final PublicKey tokenYProgramKey,
                                                         final PublicKey memoProgramKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createReadOnlySigner(ownerKey),
      createWritableSigner(rentPayerKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction rebalanceLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey positionKey,
                                               final PublicKey lbPairKey,
                                               final PublicKey binArrayBitmapExtensionKey,
                                               final PublicKey userTokenXKey,
                                               final PublicKey userTokenYKey,
                                               final PublicKey reserveXKey,
                                               final PublicKey reserveYKey,
                                               final PublicKey tokenXMintKey,
                                               final PublicKey tokenYMintKey,
                                               final PublicKey ownerKey,
                                               final PublicKey rentPayerKey,
                                               final PublicKey tokenXProgramKey,
                                               final PublicKey tokenYProgramKey,
                                               final PublicKey memoProgramKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey,
                                               final RebalanceLiquidityParams params,
                                               final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = rebalanceLiquidityKeys(
      invokedLbClmmProgramMeta,
      solanaAccounts,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      ownerKey,
      rentPayerKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return rebalanceLiquidity(invokedLbClmmProgramMeta, keys, params, remainingAccountsInfo);
  }

  public static Instruction rebalanceLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                               final List<AccountMeta> keys,
                                               final RebalanceLiquidityParams params,
                                               final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[8 + params.l() + remainingAccountsInfo.l()];
    int i = REBALANCE_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    i += params.write(_data, i);
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record RebalanceLiquidityIxData(Discriminator discriminator, RebalanceLiquidityParams params, RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static RebalanceLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RebalanceLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = RebalanceLiquidityParams.read(_data, i);
      i += params.l();
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new RebalanceLiquidityIxData(discriminator, params, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l() + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator REMOVE_ALL_LIQUIDITY_DISCRIMINATOR = toDiscriminator(10, 51, 61, 35, 112, 105, 24, 85);

  public static List<AccountMeta> removeAllLiquidityKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                         final PublicKey positionKey,
                                                         final PublicKey lbPairKey,
                                                         final PublicKey binArrayBitmapExtensionKey,
                                                         final PublicKey userTokenXKey,
                                                         final PublicKey userTokenYKey,
                                                         final PublicKey reserveXKey,
                                                         final PublicKey reserveYKey,
                                                         final PublicKey tokenXMintKey,
                                                         final PublicKey tokenYMintKey,
                                                         final PublicKey binArrayLowerKey,
                                                         final PublicKey binArrayUpperKey,
                                                         final PublicKey senderKey,
                                                         final PublicKey tokenXProgramKey,
                                                         final PublicKey tokenYProgramKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction removeAllLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                               final PublicKey positionKey,
                                               final PublicKey lbPairKey,
                                               final PublicKey binArrayBitmapExtensionKey,
                                               final PublicKey userTokenXKey,
                                               final PublicKey userTokenYKey,
                                               final PublicKey reserveXKey,
                                               final PublicKey reserveYKey,
                                               final PublicKey tokenXMintKey,
                                               final PublicKey tokenYMintKey,
                                               final PublicKey binArrayLowerKey,
                                               final PublicKey binArrayUpperKey,
                                               final PublicKey senderKey,
                                               final PublicKey tokenXProgramKey,
                                               final PublicKey tokenYProgramKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey) {
    final var keys = removeAllLiquidityKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return removeAllLiquidity(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction removeAllLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, REMOVE_ALL_LIQUIDITY_DISCRIMINATOR);
  }

  public static final Discriminator REMOVE_LIQUIDITY_DISCRIMINATOR = toDiscriminator(80, 85, 209, 72, 24, 206, 177, 108);

  public static List<AccountMeta> removeLiquidityKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                      final PublicKey positionKey,
                                                      final PublicKey lbPairKey,
                                                      final PublicKey binArrayBitmapExtensionKey,
                                                      final PublicKey userTokenXKey,
                                                      final PublicKey userTokenYKey,
                                                      final PublicKey reserveXKey,
                                                      final PublicKey reserveYKey,
                                                      final PublicKey tokenXMintKey,
                                                      final PublicKey tokenYMintKey,
                                                      final PublicKey binArrayLowerKey,
                                                      final PublicKey binArrayUpperKey,
                                                      final PublicKey senderKey,
                                                      final PublicKey tokenXProgramKey,
                                                      final PublicKey tokenYProgramKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction removeLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                            final PublicKey positionKey,
                                            final PublicKey lbPairKey,
                                            final PublicKey binArrayBitmapExtensionKey,
                                            final PublicKey userTokenXKey,
                                            final PublicKey userTokenYKey,
                                            final PublicKey reserveXKey,
                                            final PublicKey reserveYKey,
                                            final PublicKey tokenXMintKey,
                                            final PublicKey tokenYMintKey,
                                            final PublicKey binArrayLowerKey,
                                            final PublicKey binArrayUpperKey,
                                            final PublicKey senderKey,
                                            final PublicKey tokenXProgramKey,
                                            final PublicKey tokenYProgramKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final BinLiquidityReduction[] binLiquidityRemoval) {
    final var keys = removeLiquidityKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return removeLiquidity(invokedLbClmmProgramMeta, keys, binLiquidityRemoval);
  }

  public static Instruction removeLiquidity(final AccountMeta invokedLbClmmProgramMeta,
                                            final List<AccountMeta> keys,
                                            final BinLiquidityReduction[] binLiquidityRemoval) {
    final byte[] _data = new byte[8 + SerDeUtil.lenVector(4, binLiquidityRemoval)];
    int i = REMOVE_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    SerDeUtil.writeVector(4, binLiquidityRemoval, _data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record RemoveLiquidityIxData(Discriminator discriminator, BinLiquidityReduction[] binLiquidityRemoval) implements SerDe {  

    public static RemoveLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RemoveLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var binLiquidityRemoval = SerDeUtil.readVector(4, BinLiquidityReduction.class, BinLiquidityReduction::read, _data, i);
      return new RemoveLiquidityIxData(discriminator, binLiquidityRemoval);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += SerDeUtil.writeVector(4, binLiquidityRemoval, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + SerDeUtil.lenVector(4, binLiquidityRemoval);
    }
  }

  public static final Discriminator REMOVE_LIQUIDITY_2_DISCRIMINATOR = toDiscriminator(230, 215, 82, 127, 241, 101, 227, 146);

  public static List<AccountMeta> removeLiquidity2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                       final PublicKey positionKey,
                                                       final PublicKey lbPairKey,
                                                       final PublicKey binArrayBitmapExtensionKey,
                                                       final PublicKey userTokenXKey,
                                                       final PublicKey userTokenYKey,
                                                       final PublicKey reserveXKey,
                                                       final PublicKey reserveYKey,
                                                       final PublicKey tokenXMintKey,
                                                       final PublicKey tokenYMintKey,
                                                       final PublicKey senderKey,
                                                       final PublicKey tokenXProgramKey,
                                                       final PublicKey tokenYProgramKey,
                                                       final PublicKey memoProgramKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction removeLiquidity2(final AccountMeta invokedLbClmmProgramMeta,
                                             final PublicKey positionKey,
                                             final PublicKey lbPairKey,
                                             final PublicKey binArrayBitmapExtensionKey,
                                             final PublicKey userTokenXKey,
                                             final PublicKey userTokenYKey,
                                             final PublicKey reserveXKey,
                                             final PublicKey reserveYKey,
                                             final PublicKey tokenXMintKey,
                                             final PublicKey tokenYMintKey,
                                             final PublicKey senderKey,
                                             final PublicKey tokenXProgramKey,
                                             final PublicKey tokenYProgramKey,
                                             final PublicKey memoProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey,
                                             final BinLiquidityReduction[] binLiquidityRemoval,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = removeLiquidity2Keys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return removeLiquidity2(invokedLbClmmProgramMeta, keys, binLiquidityRemoval, remainingAccountsInfo);
  }

  public static Instruction removeLiquidity2(final AccountMeta invokedLbClmmProgramMeta,
                                             final List<AccountMeta> keys,
                                             final BinLiquidityReduction[] binLiquidityRemoval,
                                             final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[8 + SerDeUtil.lenVector(4, binLiquidityRemoval) + remainingAccountsInfo.l()];
    int i = REMOVE_LIQUIDITY_2_DISCRIMINATOR.write(_data, 0);
    i += SerDeUtil.writeVector(4, binLiquidityRemoval, _data, i);
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record RemoveLiquidity2IxData(Discriminator discriminator, BinLiquidityReduction[] binLiquidityRemoval, RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static RemoveLiquidity2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RemoveLiquidity2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var binLiquidityRemoval = SerDeUtil.readVector(4, BinLiquidityReduction.class, BinLiquidityReduction::read, _data, i);
      i += SerDeUtil.lenVector(4, binLiquidityRemoval);
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new RemoveLiquidity2IxData(discriminator, binLiquidityRemoval, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += SerDeUtil.writeVector(4, binLiquidityRemoval, _data, i);
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + SerDeUtil.lenVector(4, binLiquidityRemoval) + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator REMOVE_LIQUIDITY_BY_RANGE_DISCRIMINATOR = toDiscriminator(26, 82, 102, 152, 240, 74, 105, 26);

  public static List<AccountMeta> removeLiquidityByRangeKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                             final PublicKey positionKey,
                                                             final PublicKey lbPairKey,
                                                             final PublicKey binArrayBitmapExtensionKey,
                                                             final PublicKey userTokenXKey,
                                                             final PublicKey userTokenYKey,
                                                             final PublicKey reserveXKey,
                                                             final PublicKey reserveYKey,
                                                             final PublicKey tokenXMintKey,
                                                             final PublicKey tokenYMintKey,
                                                             final PublicKey binArrayLowerKey,
                                                             final PublicKey binArrayUpperKey,
                                                             final PublicKey senderKey,
                                                             final PublicKey tokenXProgramKey,
                                                             final PublicKey tokenYProgramKey,
                                                             final PublicKey eventAuthorityKey,
                                                             final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction removeLiquidityByRange(final AccountMeta invokedLbClmmProgramMeta,
                                                   final PublicKey positionKey,
                                                   final PublicKey lbPairKey,
                                                   final PublicKey binArrayBitmapExtensionKey,
                                                   final PublicKey userTokenXKey,
                                                   final PublicKey userTokenYKey,
                                                   final PublicKey reserveXKey,
                                                   final PublicKey reserveYKey,
                                                   final PublicKey tokenXMintKey,
                                                   final PublicKey tokenYMintKey,
                                                   final PublicKey binArrayLowerKey,
                                                   final PublicKey binArrayUpperKey,
                                                   final PublicKey senderKey,
                                                   final PublicKey tokenXProgramKey,
                                                   final PublicKey tokenYProgramKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey,
                                                   final int fromBinId,
                                                   final int toBinId,
                                                   final int bpsToRemove) {
    final var keys = removeLiquidityByRangeKeys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      binArrayLowerKey,
      binArrayUpperKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return removeLiquidityByRange(
      invokedLbClmmProgramMeta,
      keys,
      fromBinId,
      toBinId,
      bpsToRemove
    );
  }

  public static Instruction removeLiquidityByRange(final AccountMeta invokedLbClmmProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final int fromBinId,
                                                   final int toBinId,
                                                   final int bpsToRemove) {
    final byte[] _data = new byte[18];
    int i = REMOVE_LIQUIDITY_BY_RANGE_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, fromBinId);
    i += 4;
    putInt32LE(_data, i, toBinId);
    i += 4;
    putInt16LE(_data, i, bpsToRemove);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record RemoveLiquidityByRangeIxData(Discriminator discriminator,
                                             int fromBinId,
                                             int toBinId,
                                             int bpsToRemove) implements SerDe {  

    public static RemoveLiquidityByRangeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static RemoveLiquidityByRangeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var fromBinId = getInt32LE(_data, i);
      i += 4;
      final var toBinId = getInt32LE(_data, i);
      i += 4;
      final var bpsToRemove = getInt16LE(_data, i);
      return new RemoveLiquidityByRangeIxData(discriminator, fromBinId, toBinId, bpsToRemove);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, fromBinId);
      i += 4;
      putInt32LE(_data, i, toBinId);
      i += 4;
      putInt16LE(_data, i, bpsToRemove);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REMOVE_LIQUIDITY_BY_RANGE_2_DISCRIMINATOR = toDiscriminator(204, 2, 195, 145, 53, 145, 145, 205);

  public static List<AccountMeta> removeLiquidityByRange2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                              final PublicKey positionKey,
                                                              final PublicKey lbPairKey,
                                                              final PublicKey binArrayBitmapExtensionKey,
                                                              final PublicKey userTokenXKey,
                                                              final PublicKey userTokenYKey,
                                                              final PublicKey reserveXKey,
                                                              final PublicKey reserveYKey,
                                                              final PublicKey tokenXMintKey,
                                                              final PublicKey tokenYMintKey,
                                                              final PublicKey senderKey,
                                                              final PublicKey tokenXProgramKey,
                                                              final PublicKey tokenYProgramKey,
                                                              final PublicKey memoProgramKey,
                                                              final PublicKey eventAuthorityKey,
                                                              final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(userTokenXKey),
      createWrite(userTokenYKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createReadOnlySigner(senderKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction removeLiquidityByRange2(final AccountMeta invokedLbClmmProgramMeta,
                                                    final PublicKey positionKey,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey binArrayBitmapExtensionKey,
                                                    final PublicKey userTokenXKey,
                                                    final PublicKey userTokenYKey,
                                                    final PublicKey reserveXKey,
                                                    final PublicKey reserveYKey,
                                                    final PublicKey tokenXMintKey,
                                                    final PublicKey tokenYMintKey,
                                                    final PublicKey senderKey,
                                                    final PublicKey tokenXProgramKey,
                                                    final PublicKey tokenYProgramKey,
                                                    final PublicKey memoProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey,
                                                    final int fromBinId,
                                                    final int toBinId,
                                                    final int bpsToRemove,
                                                    final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = removeLiquidityByRange2Keys(
      invokedLbClmmProgramMeta,
      positionKey,
      lbPairKey,
      binArrayBitmapExtensionKey,
      userTokenXKey,
      userTokenYKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      senderKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return removeLiquidityByRange2(
      invokedLbClmmProgramMeta,
      keys,
      fromBinId,
      toBinId,
      bpsToRemove,
      remainingAccountsInfo
    );
  }

  public static Instruction removeLiquidityByRange2(final AccountMeta invokedLbClmmProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final int fromBinId,
                                                    final int toBinId,
                                                    final int bpsToRemove,
                                                    final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[18 + remainingAccountsInfo.l()];
    int i = REMOVE_LIQUIDITY_BY_RANGE_2_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, fromBinId);
    i += 4;
    putInt32LE(_data, i, toBinId);
    i += 4;
    putInt16LE(_data, i, bpsToRemove);
    i += 2;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record RemoveLiquidityByRange2IxData(Discriminator discriminator,
                                              int fromBinId,
                                              int toBinId,
                                              int bpsToRemove,
                                              RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static RemoveLiquidityByRange2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RemoveLiquidityByRange2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var fromBinId = getInt32LE(_data, i);
      i += 4;
      final var toBinId = getInt32LE(_data, i);
      i += 4;
      final var bpsToRemove = getInt16LE(_data, i);
      i += 2;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new RemoveLiquidityByRange2IxData(discriminator,
                                               fromBinId,
                                               toBinId,
                                               bpsToRemove,
                                               remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, fromBinId);
      i += 4;
      putInt32LE(_data, i, toBinId);
      i += 4;
      putInt16LE(_data, i, bpsToRemove);
      i += 2;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 4 + 4 + 2 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator SET_ACTIVATION_POINT_DISCRIMINATOR = toDiscriminator(91, 249, 15, 165, 26, 129, 254, 125);

  public static List<AccountMeta> setActivationPointKeys(final PublicKey lbPairKey,
                                                         final PublicKey adminKey) {
    return List.of(
      createWrite(lbPairKey),
      createWritableSigner(adminKey)
    );
  }

  public static Instruction setActivationPoint(final AccountMeta invokedLbClmmProgramMeta,
                                               final PublicKey lbPairKey,
                                               final PublicKey adminKey,
                                               final long activationPoint) {
    final var keys = setActivationPointKeys(
      lbPairKey,
      adminKey
    );
    return setActivationPoint(invokedLbClmmProgramMeta, keys, activationPoint);
  }

  public static Instruction setActivationPoint(final AccountMeta invokedLbClmmProgramMeta,
                                               final List<AccountMeta> keys,
                                               final long activationPoint) {
    final byte[] _data = new byte[16];
    int i = SET_ACTIVATION_POINT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, activationPoint);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SetActivationPointIxData(Discriminator discriminator, long activationPoint) implements SerDe {  

    public static SetActivationPointIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetActivationPointIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var activationPoint = getInt64LE(_data, i);
      return new SetActivationPointIxData(discriminator, activationPoint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, activationPoint);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_PAIR_STATUS_DISCRIMINATOR = toDiscriminator(67, 248, 231, 137, 154, 149, 217, 174);

  public static List<AccountMeta> setPairStatusKeys(final PublicKey lbPairKey,
                                                    final PublicKey adminKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction setPairStatus(final AccountMeta invokedLbClmmProgramMeta,
                                          final PublicKey lbPairKey,
                                          final PublicKey adminKey,
                                          final int status) {
    final var keys = setPairStatusKeys(
      lbPairKey,
      adminKey
    );
    return setPairStatus(invokedLbClmmProgramMeta, keys, status);
  }

  public static Instruction setPairStatus(final AccountMeta invokedLbClmmProgramMeta,
                                          final List<AccountMeta> keys,
                                          final int status) {
    final byte[] _data = new byte[9];
    int i = SET_PAIR_STATUS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) status;

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SetPairStatusIxData(Discriminator discriminator, int status) implements SerDe {  

    public static SetPairStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static SetPairStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = _data[i] & 0xFF;
      return new SetPairStatusIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) status;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_PAIR_STATUS_PERMISSIONLESS_DISCRIMINATOR = toDiscriminator(78, 59, 152, 211, 70, 183, 46, 208);

  public static List<AccountMeta> setPairStatusPermissionlessKeys(final PublicKey lbPairKey,
                                                                  final PublicKey creatorKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(creatorKey)
    );
  }

  public static Instruction setPairStatusPermissionless(final AccountMeta invokedLbClmmProgramMeta,
                                                        final PublicKey lbPairKey,
                                                        final PublicKey creatorKey,
                                                        final int status) {
    final var keys = setPairStatusPermissionlessKeys(
      lbPairKey,
      creatorKey
    );
    return setPairStatusPermissionless(invokedLbClmmProgramMeta, keys, status);
  }

  public static Instruction setPairStatusPermissionless(final AccountMeta invokedLbClmmProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final int status) {
    final byte[] _data = new byte[9];
    int i = SET_PAIR_STATUS_PERMISSIONLESS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) status;

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SetPairStatusPermissionlessIxData(Discriminator discriminator, int status) implements SerDe {  

    public static SetPairStatusPermissionlessIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static SetPairStatusPermissionlessIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = _data[i] & 0xFF;
      return new SetPairStatusPermissionlessIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) status;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_PRE_ACTIVATION_DURATION_DISCRIMINATOR = toDiscriminator(165, 61, 201, 244, 130, 159, 22, 100);

  public static List<AccountMeta> setPreActivationDurationKeys(final PublicKey lbPairKey,
                                                               final PublicKey creatorKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(creatorKey)
    );
  }

  public static Instruction setPreActivationDuration(final AccountMeta invokedLbClmmProgramMeta,
                                                     final PublicKey lbPairKey,
                                                     final PublicKey creatorKey,
                                                     final long preActivationDuration) {
    final var keys = setPreActivationDurationKeys(
      lbPairKey,
      creatorKey
    );
    return setPreActivationDuration(invokedLbClmmProgramMeta, keys, preActivationDuration);
  }

  public static Instruction setPreActivationDuration(final AccountMeta invokedLbClmmProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final long preActivationDuration) {
    final byte[] _data = new byte[16];
    int i = SET_PRE_ACTIVATION_DURATION_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, preActivationDuration);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SetPreActivationDurationIxData(Discriminator discriminator, long preActivationDuration) implements SerDe {  

    public static SetPreActivationDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetPreActivationDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var preActivationDuration = getInt64LE(_data, i);
      return new SetPreActivationDurationIxData(discriminator, preActivationDuration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, preActivationDuration);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_PRE_ACTIVATION_SWAP_ADDRESS_DISCRIMINATOR = toDiscriminator(57, 139, 47, 123, 216, 80, 223, 10);

  public static List<AccountMeta> setPreActivationSwapAddressKeys(final PublicKey lbPairKey,
                                                                  final PublicKey creatorKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(creatorKey)
    );
  }

  public static Instruction setPreActivationSwapAddress(final AccountMeta invokedLbClmmProgramMeta,
                                                        final PublicKey lbPairKey,
                                                        final PublicKey creatorKey,
                                                        final PublicKey preActivationSwapAddress) {
    final var keys = setPreActivationSwapAddressKeys(
      lbPairKey,
      creatorKey
    );
    return setPreActivationSwapAddress(invokedLbClmmProgramMeta, keys, preActivationSwapAddress);
  }

  public static Instruction setPreActivationSwapAddress(final AccountMeta invokedLbClmmProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final PublicKey preActivationSwapAddress) {
    final byte[] _data = new byte[40];
    int i = SET_PRE_ACTIVATION_SWAP_ADDRESS_DISCRIMINATOR.write(_data, 0);
    preActivationSwapAddress.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SetPreActivationSwapAddressIxData(Discriminator discriminator, PublicKey preActivationSwapAddress) implements SerDe {  

    public static SetPreActivationSwapAddressIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetPreActivationSwapAddressIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var preActivationSwapAddress = readPubKey(_data, i);
      return new SetPreActivationSwapAddressIxData(discriminator, preActivationSwapAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      preActivationSwapAddress.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SWAP_DISCRIMINATOR = toDiscriminator(248, 198, 158, 145, 225, 117, 135, 200);

  public static List<AccountMeta> swapKeys(final AccountMeta invokedLbClmmProgramMeta,
                                           final PublicKey lbPairKey,
                                           final PublicKey binArrayBitmapExtensionKey,
                                           final PublicKey reserveXKey,
                                           final PublicKey reserveYKey,
                                           final PublicKey userTokenInKey,
                                           final PublicKey userTokenOutKey,
                                           final PublicKey tokenXMintKey,
                                           final PublicKey tokenYMintKey,
                                           final PublicKey oracleKey,
                                           final PublicKey hostFeeInKey,
                                           final PublicKey userKey,
                                           final PublicKey tokenXProgramKey,
                                           final PublicKey tokenYProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenInKey),
      createWrite(userTokenOutKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(oracleKey),
      createWrite(requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())),
      createReadOnlySigner(userKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction swap(final AccountMeta invokedLbClmmProgramMeta,
                                 final PublicKey lbPairKey,
                                 final PublicKey binArrayBitmapExtensionKey,
                                 final PublicKey reserveXKey,
                                 final PublicKey reserveYKey,
                                 final PublicKey userTokenInKey,
                                 final PublicKey userTokenOutKey,
                                 final PublicKey tokenXMintKey,
                                 final PublicKey tokenYMintKey,
                                 final PublicKey oracleKey,
                                 final PublicKey hostFeeInKey,
                                 final PublicKey userKey,
                                 final PublicKey tokenXProgramKey,
                                 final PublicKey tokenYProgramKey,
                                 final PublicKey eventAuthorityKey,
                                 final PublicKey programKey,
                                 final long amountIn,
                                 final long minAmountOut) {
    final var keys = swapKeys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      reserveXKey,
      reserveYKey,
      userTokenInKey,
      userTokenOutKey,
      tokenXMintKey,
      tokenYMintKey,
      oracleKey,
      hostFeeInKey,
      userKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return swap(invokedLbClmmProgramMeta, keys, amountIn, minAmountOut);
  }

  public static Instruction swap(final AccountMeta invokedLbClmmProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long amountIn,
                                 final long minAmountOut) {
    final byte[] _data = new byte[24];
    int i = SWAP_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amountIn);
    i += 8;
    putInt64LE(_data, i, minAmountOut);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SwapIxData(Discriminator discriminator, long amountIn, long minAmountOut) implements SerDe {  

    public static SwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static SwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amountIn = getInt64LE(_data, i);
      i += 8;
      final var minAmountOut = getInt64LE(_data, i);
      return new SwapIxData(discriminator, amountIn, minAmountOut);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amountIn);
      i += 8;
      putInt64LE(_data, i, minAmountOut);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SWAP_2_DISCRIMINATOR = toDiscriminator(65, 75, 63, 76, 235, 91, 91, 136);

  public static List<AccountMeta> swap2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                            final PublicKey lbPairKey,
                                            final PublicKey binArrayBitmapExtensionKey,
                                            final PublicKey reserveXKey,
                                            final PublicKey reserveYKey,
                                            final PublicKey userTokenInKey,
                                            final PublicKey userTokenOutKey,
                                            final PublicKey tokenXMintKey,
                                            final PublicKey tokenYMintKey,
                                            final PublicKey oracleKey,
                                            final PublicKey hostFeeInKey,
                                            final PublicKey userKey,
                                            final PublicKey tokenXProgramKey,
                                            final PublicKey tokenYProgramKey,
                                            final PublicKey memoProgramKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenInKey),
      createWrite(userTokenOutKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(oracleKey),
      createWrite(requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())),
      createReadOnlySigner(userKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction swap2(final AccountMeta invokedLbClmmProgramMeta,
                                  final PublicKey lbPairKey,
                                  final PublicKey binArrayBitmapExtensionKey,
                                  final PublicKey reserveXKey,
                                  final PublicKey reserveYKey,
                                  final PublicKey userTokenInKey,
                                  final PublicKey userTokenOutKey,
                                  final PublicKey tokenXMintKey,
                                  final PublicKey tokenYMintKey,
                                  final PublicKey oracleKey,
                                  final PublicKey hostFeeInKey,
                                  final PublicKey userKey,
                                  final PublicKey tokenXProgramKey,
                                  final PublicKey tokenYProgramKey,
                                  final PublicKey memoProgramKey,
                                  final PublicKey eventAuthorityKey,
                                  final PublicKey programKey,
                                  final long amountIn,
                                  final long minAmountOut,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = swap2Keys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      reserveXKey,
      reserveYKey,
      userTokenInKey,
      userTokenOutKey,
      tokenXMintKey,
      tokenYMintKey,
      oracleKey,
      hostFeeInKey,
      userKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return swap2(
      invokedLbClmmProgramMeta,
      keys,
      amountIn,
      minAmountOut,
      remainingAccountsInfo
    );
  }

  public static Instruction swap2(final AccountMeta invokedLbClmmProgramMeta,
                                  final List<AccountMeta> keys,
                                  final long amountIn,
                                  final long minAmountOut,
                                  final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[24 + remainingAccountsInfo.l()];
    int i = SWAP_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amountIn);
    i += 8;
    putInt64LE(_data, i, minAmountOut);
    i += 8;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record Swap2IxData(Discriminator discriminator,
                            long amountIn,
                            long minAmountOut,
                            RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static Swap2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static Swap2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amountIn = getInt64LE(_data, i);
      i += 8;
      final var minAmountOut = getInt64LE(_data, i);
      i += 8;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new Swap2IxData(discriminator, amountIn, minAmountOut, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amountIn);
      i += 8;
      putInt64LE(_data, i, minAmountOut);
      i += 8;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 8 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator SWAP_EXACT_OUT_DISCRIMINATOR = toDiscriminator(250, 73, 101, 33, 38, 207, 75, 184);

  public static List<AccountMeta> swapExactOutKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                   final PublicKey lbPairKey,
                                                   final PublicKey binArrayBitmapExtensionKey,
                                                   final PublicKey reserveXKey,
                                                   final PublicKey reserveYKey,
                                                   final PublicKey userTokenInKey,
                                                   final PublicKey userTokenOutKey,
                                                   final PublicKey tokenXMintKey,
                                                   final PublicKey tokenYMintKey,
                                                   final PublicKey oracleKey,
                                                   final PublicKey hostFeeInKey,
                                                   final PublicKey userKey,
                                                   final PublicKey tokenXProgramKey,
                                                   final PublicKey tokenYProgramKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenInKey),
      createWrite(userTokenOutKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(oracleKey),
      createWrite(requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())),
      createReadOnlySigner(userKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction swapExactOut(final AccountMeta invokedLbClmmProgramMeta,
                                         final PublicKey lbPairKey,
                                         final PublicKey binArrayBitmapExtensionKey,
                                         final PublicKey reserveXKey,
                                         final PublicKey reserveYKey,
                                         final PublicKey userTokenInKey,
                                         final PublicKey userTokenOutKey,
                                         final PublicKey tokenXMintKey,
                                         final PublicKey tokenYMintKey,
                                         final PublicKey oracleKey,
                                         final PublicKey hostFeeInKey,
                                         final PublicKey userKey,
                                         final PublicKey tokenXProgramKey,
                                         final PublicKey tokenYProgramKey,
                                         final PublicKey eventAuthorityKey,
                                         final PublicKey programKey,
                                         final long maxInAmount,
                                         final long outAmount) {
    final var keys = swapExactOutKeys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      reserveXKey,
      reserveYKey,
      userTokenInKey,
      userTokenOutKey,
      tokenXMintKey,
      tokenYMintKey,
      oracleKey,
      hostFeeInKey,
      userKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return swapExactOut(invokedLbClmmProgramMeta, keys, maxInAmount, outAmount);
  }

  public static Instruction swapExactOut(final AccountMeta invokedLbClmmProgramMeta,
                                         final List<AccountMeta> keys,
                                         final long maxInAmount,
                                         final long outAmount) {
    final byte[] _data = new byte[24];
    int i = SWAP_EXACT_OUT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxInAmount);
    i += 8;
    putInt64LE(_data, i, outAmount);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SwapExactOutIxData(Discriminator discriminator, long maxInAmount, long outAmount) implements SerDe {  

    public static SwapExactOutIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static SwapExactOutIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxInAmount = getInt64LE(_data, i);
      i += 8;
      final var outAmount = getInt64LE(_data, i);
      return new SwapExactOutIxData(discriminator, maxInAmount, outAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxInAmount);
      i += 8;
      putInt64LE(_data, i, outAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SWAP_EXACT_OUT_2_DISCRIMINATOR = toDiscriminator(43, 215, 247, 132, 137, 60, 243, 81);

  public static List<AccountMeta> swapExactOut2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey binArrayBitmapExtensionKey,
                                                    final PublicKey reserveXKey,
                                                    final PublicKey reserveYKey,
                                                    final PublicKey userTokenInKey,
                                                    final PublicKey userTokenOutKey,
                                                    final PublicKey tokenXMintKey,
                                                    final PublicKey tokenYMintKey,
                                                    final PublicKey oracleKey,
                                                    final PublicKey hostFeeInKey,
                                                    final PublicKey userKey,
                                                    final PublicKey tokenXProgramKey,
                                                    final PublicKey tokenYProgramKey,
                                                    final PublicKey memoProgramKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenInKey),
      createWrite(userTokenOutKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(oracleKey),
      createWrite(requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())),
      createReadOnlySigner(userKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction swapExactOut2(final AccountMeta invokedLbClmmProgramMeta,
                                          final PublicKey lbPairKey,
                                          final PublicKey binArrayBitmapExtensionKey,
                                          final PublicKey reserveXKey,
                                          final PublicKey reserveYKey,
                                          final PublicKey userTokenInKey,
                                          final PublicKey userTokenOutKey,
                                          final PublicKey tokenXMintKey,
                                          final PublicKey tokenYMintKey,
                                          final PublicKey oracleKey,
                                          final PublicKey hostFeeInKey,
                                          final PublicKey userKey,
                                          final PublicKey tokenXProgramKey,
                                          final PublicKey tokenYProgramKey,
                                          final PublicKey memoProgramKey,
                                          final PublicKey eventAuthorityKey,
                                          final PublicKey programKey,
                                          final long maxInAmount,
                                          final long outAmount,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = swapExactOut2Keys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      reserveXKey,
      reserveYKey,
      userTokenInKey,
      userTokenOutKey,
      tokenXMintKey,
      tokenYMintKey,
      oracleKey,
      hostFeeInKey,
      userKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return swapExactOut2(
      invokedLbClmmProgramMeta,
      keys,
      maxInAmount,
      outAmount,
      remainingAccountsInfo
    );
  }

  public static Instruction swapExactOut2(final AccountMeta invokedLbClmmProgramMeta,
                                          final List<AccountMeta> keys,
                                          final long maxInAmount,
                                          final long outAmount,
                                          final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[24 + remainingAccountsInfo.l()];
    int i = SWAP_EXACT_OUT_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxInAmount);
    i += 8;
    putInt64LE(_data, i, outAmount);
    i += 8;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SwapExactOut2IxData(Discriminator discriminator,
                                    long maxInAmount,
                                    long outAmount,
                                    RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static SwapExactOut2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SwapExactOut2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxInAmount = getInt64LE(_data, i);
      i += 8;
      final var outAmount = getInt64LE(_data, i);
      i += 8;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new SwapExactOut2IxData(discriminator, maxInAmount, outAmount, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxInAmount);
      i += 8;
      putInt64LE(_data, i, outAmount);
      i += 8;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 8 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator SWAP_WITH_PRICE_IMPACT_DISCRIMINATOR = toDiscriminator(56, 173, 230, 208, 173, 228, 156, 205);

  public static List<AccountMeta> swapWithPriceImpactKeys(final AccountMeta invokedLbClmmProgramMeta,
                                                          final PublicKey lbPairKey,
                                                          final PublicKey binArrayBitmapExtensionKey,
                                                          final PublicKey reserveXKey,
                                                          final PublicKey reserveYKey,
                                                          final PublicKey userTokenInKey,
                                                          final PublicKey userTokenOutKey,
                                                          final PublicKey tokenXMintKey,
                                                          final PublicKey tokenYMintKey,
                                                          final PublicKey oracleKey,
                                                          final PublicKey hostFeeInKey,
                                                          final PublicKey userKey,
                                                          final PublicKey tokenXProgramKey,
                                                          final PublicKey tokenYProgramKey,
                                                          final PublicKey eventAuthorityKey,
                                                          final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenInKey),
      createWrite(userTokenOutKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(oracleKey),
      createWrite(requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())),
      createReadOnlySigner(userKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction swapWithPriceImpact(final AccountMeta invokedLbClmmProgramMeta,
                                                final PublicKey lbPairKey,
                                                final PublicKey binArrayBitmapExtensionKey,
                                                final PublicKey reserveXKey,
                                                final PublicKey reserveYKey,
                                                final PublicKey userTokenInKey,
                                                final PublicKey userTokenOutKey,
                                                final PublicKey tokenXMintKey,
                                                final PublicKey tokenYMintKey,
                                                final PublicKey oracleKey,
                                                final PublicKey hostFeeInKey,
                                                final PublicKey userKey,
                                                final PublicKey tokenXProgramKey,
                                                final PublicKey tokenYProgramKey,
                                                final PublicKey eventAuthorityKey,
                                                final PublicKey programKey,
                                                final long amountIn,
                                                final OptionalInt activeId,
                                                final int maxPriceImpactBps) {
    final var keys = swapWithPriceImpactKeys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      reserveXKey,
      reserveYKey,
      userTokenInKey,
      userTokenOutKey,
      tokenXMintKey,
      tokenYMintKey,
      oracleKey,
      hostFeeInKey,
      userKey,
      tokenXProgramKey,
      tokenYProgramKey,
      eventAuthorityKey,
      programKey
    );
    return swapWithPriceImpact(
      invokedLbClmmProgramMeta,
      keys,
      amountIn,
      activeId,
      maxPriceImpactBps
    );
  }

  public static Instruction swapWithPriceImpact(final AccountMeta invokedLbClmmProgramMeta,
                                                final List<AccountMeta> keys,
                                                final long amountIn,
                                                final OptionalInt activeId,
                                                final int maxPriceImpactBps) {
    final byte[] _data = new byte[
    18
    + (activeId == null || activeId.isEmpty() ? 1 : 5)
    ];
    int i = SWAP_WITH_PRICE_IMPACT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amountIn);
    i += 8;
    i += SerDeUtil.writeOptional(1, activeId, _data, i);
    putInt16LE(_data, i, maxPriceImpactBps);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SwapWithPriceImpactIxData(Discriminator discriminator,
                                          long amountIn,
                                          OptionalInt activeId,
                                          int maxPriceImpactBps) implements SerDe {  

    public static SwapWithPriceImpactIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SwapWithPriceImpactIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amountIn = getInt64LE(_data, i);
      i += 8;
      final OptionalInt activeId;
      if (_data[i] == 0) {
        activeId = OptionalInt.empty();
        ++i;
      } else {
        ++i;
        activeId = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final var maxPriceImpactBps = getInt16LE(_data, i);
      return new SwapWithPriceImpactIxData(discriminator, amountIn, activeId, maxPriceImpactBps);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amountIn);
      i += 8;
      i += SerDeUtil.writeOptional(1, activeId, _data, i);
      putInt16LE(_data, i, maxPriceImpactBps);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + (activeId == null || activeId.isEmpty() ? 1 : (1 + 4)) + 2;
    }
  }

  public static final Discriminator SWAP_WITH_PRICE_IMPACT_2_DISCRIMINATOR = toDiscriminator(74, 98, 192, 214, 177, 51, 75, 51);

  public static List<AccountMeta> swapWithPriceImpact2Keys(final AccountMeta invokedLbClmmProgramMeta,
                                                           final PublicKey lbPairKey,
                                                           final PublicKey binArrayBitmapExtensionKey,
                                                           final PublicKey reserveXKey,
                                                           final PublicKey reserveYKey,
                                                           final PublicKey userTokenInKey,
                                                           final PublicKey userTokenOutKey,
                                                           final PublicKey tokenXMintKey,
                                                           final PublicKey tokenYMintKey,
                                                           final PublicKey oracleKey,
                                                           final PublicKey hostFeeInKey,
                                                           final PublicKey userKey,
                                                           final PublicKey tokenXProgramKey,
                                                           final PublicKey tokenYProgramKey,
                                                           final PublicKey memoProgramKey,
                                                           final PublicKey eventAuthorityKey,
                                                           final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createRead(requireNonNullElse(binArrayBitmapExtensionKey, invokedLbClmmProgramMeta.publicKey())),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createWrite(userTokenInKey),
      createWrite(userTokenOutKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(oracleKey),
      createWrite(requireNonNullElse(hostFeeInKey, invokedLbClmmProgramMeta.publicKey())),
      createReadOnlySigner(userKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction swapWithPriceImpact2(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey lbPairKey,
                                                 final PublicKey binArrayBitmapExtensionKey,
                                                 final PublicKey reserveXKey,
                                                 final PublicKey reserveYKey,
                                                 final PublicKey userTokenInKey,
                                                 final PublicKey userTokenOutKey,
                                                 final PublicKey tokenXMintKey,
                                                 final PublicKey tokenYMintKey,
                                                 final PublicKey oracleKey,
                                                 final PublicKey hostFeeInKey,
                                                 final PublicKey userKey,
                                                 final PublicKey tokenXProgramKey,
                                                 final PublicKey tokenYProgramKey,
                                                 final PublicKey memoProgramKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey,
                                                 final long amountIn,
                                                 final OptionalInt activeId,
                                                 final int maxPriceImpactBps,
                                                 final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = swapWithPriceImpact2Keys(
      invokedLbClmmProgramMeta,
      lbPairKey,
      binArrayBitmapExtensionKey,
      reserveXKey,
      reserveYKey,
      userTokenInKey,
      userTokenOutKey,
      tokenXMintKey,
      tokenYMintKey,
      oracleKey,
      hostFeeInKey,
      userKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return swapWithPriceImpact2(
      invokedLbClmmProgramMeta,
      keys,
      amountIn,
      activeId,
      maxPriceImpactBps,
      remainingAccountsInfo
    );
  }

  public static Instruction swapWithPriceImpact2(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long amountIn,
                                                 final OptionalInt activeId,
                                                 final int maxPriceImpactBps,
                                                 final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[
    18
    + (activeId == null || activeId.isEmpty() ? 1 : 5) + remainingAccountsInfo.l()
    ];
    int i = SWAP_WITH_PRICE_IMPACT_2_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amountIn);
    i += 8;
    i += SerDeUtil.writeOptional(1, activeId, _data, i);
    putInt16LE(_data, i, maxPriceImpactBps);
    i += 2;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record SwapWithPriceImpact2IxData(Discriminator discriminator,
                                           long amountIn,
                                           OptionalInt activeId,
                                           int maxPriceImpactBps,
                                           RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static SwapWithPriceImpact2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SwapWithPriceImpact2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amountIn = getInt64LE(_data, i);
      i += 8;
      final OptionalInt activeId;
      if (_data[i] == 0) {
        activeId = OptionalInt.empty();
        ++i;
      } else {
        ++i;
        activeId = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final var maxPriceImpactBps = getInt16LE(_data, i);
      i += 2;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new SwapWithPriceImpact2IxData(discriminator,
                                            amountIn,
                                            activeId,
                                            maxPriceImpactBps,
                                            remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amountIn);
      i += 8;
      i += SerDeUtil.writeOptional(1, activeId, _data, i);
      putInt16LE(_data, i, maxPriceImpactBps);
      i += 2;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + (activeId == null || activeId.isEmpty() ? 1 : (1 + 4)) + 2 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator UPDATE_BASE_FEE_PARAMETERS_DISCRIMINATOR = toDiscriminator(75, 168, 223, 161, 16, 195, 3, 47);

  public static List<AccountMeta> updateBaseFeeParametersKeys(final PublicKey lbPairKey,
                                                              final PublicKey adminKey,
                                                              final PublicKey eventAuthorityKey,
                                                              final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(adminKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updateBaseFeeParameters(final AccountMeta invokedLbClmmProgramMeta,
                                                    final PublicKey lbPairKey,
                                                    final PublicKey adminKey,
                                                    final PublicKey eventAuthorityKey,
                                                    final PublicKey programKey,
                                                    final BaseFeeParameter feeParameter) {
    final var keys = updateBaseFeeParametersKeys(
      lbPairKey,
      adminKey,
      eventAuthorityKey,
      programKey
    );
    return updateBaseFeeParameters(invokedLbClmmProgramMeta, keys, feeParameter);
  }

  public static Instruction updateBaseFeeParameters(final AccountMeta invokedLbClmmProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final BaseFeeParameter feeParameter) {
    final byte[] _data = new byte[8 + feeParameter.l()];
    int i = UPDATE_BASE_FEE_PARAMETERS_DISCRIMINATOR.write(_data, 0);
    feeParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record UpdateBaseFeeParametersIxData(Discriminator discriminator, BaseFeeParameter feeParameter) implements SerDe {  

    public static UpdateBaseFeeParametersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 13;

    public static UpdateBaseFeeParametersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feeParameter = BaseFeeParameter.read(_data, i);
      return new UpdateBaseFeeParametersIxData(discriminator, feeParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += feeParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_DYNAMIC_FEE_PARAMETERS_DISCRIMINATOR = toDiscriminator(92, 161, 46, 246, 255, 189, 22, 22);

  public static List<AccountMeta> updateDynamicFeeParametersKeys(final PublicKey lbPairKey,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey eventAuthorityKey,
                                                                 final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(adminKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updateDynamicFeeParameters(final AccountMeta invokedLbClmmProgramMeta,
                                                       final PublicKey lbPairKey,
                                                       final PublicKey adminKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey programKey,
                                                       final DynamicFeeParameter feeParameter) {
    final var keys = updateDynamicFeeParametersKeys(
      lbPairKey,
      adminKey,
      eventAuthorityKey,
      programKey
    );
    return updateDynamicFeeParameters(invokedLbClmmProgramMeta, keys, feeParameter);
  }

  public static Instruction updateDynamicFeeParameters(final AccountMeta invokedLbClmmProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final DynamicFeeParameter feeParameter) {
    final byte[] _data = new byte[8 + feeParameter.l()];
    int i = UPDATE_DYNAMIC_FEE_PARAMETERS_DISCRIMINATOR.write(_data, 0);
    feeParameter.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record UpdateDynamicFeeParametersIxData(Discriminator discriminator, DynamicFeeParameter feeParameter) implements SerDe {  

    public static UpdateDynamicFeeParametersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 22;

    public static UpdateDynamicFeeParametersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feeParameter = DynamicFeeParameter.read(_data, i);
      return new UpdateDynamicFeeParametersIxData(discriminator, feeParameter);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += feeParameter.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEES_AND_REWARD_2_DISCRIMINATOR = toDiscriminator(32, 142, 184, 154, 103, 65, 184, 88);

  public static List<AccountMeta> updateFeesAndReward2Keys(final PublicKey positionKey,
                                                           final PublicKey lbPairKey,
                                                           final PublicKey ownerKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createReadOnlySigner(ownerKey)
    );
  }

  public static Instruction updateFeesAndReward2(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey positionKey,
                                                 final PublicKey lbPairKey,
                                                 final PublicKey ownerKey,
                                                 final int minBinId,
                                                 final int maxBinId) {
    final var keys = updateFeesAndReward2Keys(
      positionKey,
      lbPairKey,
      ownerKey
    );
    return updateFeesAndReward2(invokedLbClmmProgramMeta, keys, minBinId, maxBinId);
  }

  public static Instruction updateFeesAndReward2(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final int minBinId,
                                                 final int maxBinId) {
    final byte[] _data = new byte[16];
    int i = UPDATE_FEES_AND_REWARD_2_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, minBinId);
    i += 4;
    putInt32LE(_data, i, maxBinId);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record UpdateFeesAndReward2IxData(Discriminator discriminator, int minBinId, int maxBinId) implements SerDe {  

    public static UpdateFeesAndReward2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateFeesAndReward2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var minBinId = getInt32LE(_data, i);
      i += 4;
      final var maxBinId = getInt32LE(_data, i);
      return new UpdateFeesAndReward2IxData(discriminator, minBinId, maxBinId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, minBinId);
      i += 4;
      putInt32LE(_data, i, maxBinId);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEES_AND_REWARDS_DISCRIMINATOR = toDiscriminator(154, 230, 250, 13, 236, 209, 75, 223);

  public static List<AccountMeta> updateFeesAndRewardsKeys(final PublicKey positionKey,
                                                           final PublicKey lbPairKey,
                                                           final PublicKey binArrayLowerKey,
                                                           final PublicKey binArrayUpperKey,
                                                           final PublicKey ownerKey) {
    return List.of(
      createWrite(positionKey),
      createWrite(lbPairKey),
      createWrite(binArrayLowerKey),
      createWrite(binArrayUpperKey),
      createReadOnlySigner(ownerKey)
    );
  }

  public static Instruction updateFeesAndRewards(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey positionKey,
                                                 final PublicKey lbPairKey,
                                                 final PublicKey binArrayLowerKey,
                                                 final PublicKey binArrayUpperKey,
                                                 final PublicKey ownerKey) {
    final var keys = updateFeesAndRewardsKeys(
      positionKey,
      lbPairKey,
      binArrayLowerKey,
      binArrayUpperKey,
      ownerKey
    );
    return updateFeesAndRewards(invokedLbClmmProgramMeta, keys);
  }

  public static Instruction updateFeesAndRewards(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, UPDATE_FEES_AND_REWARDS_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_POSITION_OPERATOR_DISCRIMINATOR = toDiscriminator(202, 184, 103, 143, 180, 191, 116, 217);

  public static List<AccountMeta> updatePositionOperatorKeys(final PublicKey positionKey,
                                                             final PublicKey ownerKey,
                                                             final PublicKey eventAuthorityKey,
                                                             final PublicKey programKey) {
    return List.of(
      createWrite(positionKey),
      createReadOnlySigner(ownerKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updatePositionOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                   final PublicKey positionKey,
                                                   final PublicKey ownerKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey,
                                                   final PublicKey operator) {
    final var keys = updatePositionOperatorKeys(
      positionKey,
      ownerKey,
      eventAuthorityKey,
      programKey
    );
    return updatePositionOperator(invokedLbClmmProgramMeta, keys, operator);
  }

  public static Instruction updatePositionOperator(final AccountMeta invokedLbClmmProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final PublicKey operator) {
    final byte[] _data = new byte[40];
    int i = UPDATE_POSITION_OPERATOR_DISCRIMINATOR.write(_data, 0);
    operator.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record UpdatePositionOperatorIxData(Discriminator discriminator, PublicKey operator) implements SerDe {  

    public static UpdatePositionOperatorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdatePositionOperatorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var operator = readPubKey(_data, i);
      return new UpdatePositionOperatorIxData(discriminator, operator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      operator.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_REWARD_DURATION_DISCRIMINATOR = toDiscriminator(138, 174, 196, 169, 213, 235, 254, 107);

  public static List<AccountMeta> updateRewardDurationKeys(final PublicKey lbPairKey,
                                                           final PublicKey adminKey,
                                                           final PublicKey binArrayKey,
                                                           final PublicKey eventAuthorityKey,
                                                           final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(adminKey),
      createWrite(binArrayKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updateRewardDuration(final AccountMeta invokedLbClmmProgramMeta,
                                                 final PublicKey lbPairKey,
                                                 final PublicKey adminKey,
                                                 final PublicKey binArrayKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey,
                                                 final long rewardIndex,
                                                 final long newDuration) {
    final var keys = updateRewardDurationKeys(
      lbPairKey,
      adminKey,
      binArrayKey,
      eventAuthorityKey,
      programKey
    );
    return updateRewardDuration(invokedLbClmmProgramMeta, keys, rewardIndex, newDuration);
  }

  public static Instruction updateRewardDuration(final AccountMeta invokedLbClmmProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long rewardIndex,
                                                 final long newDuration) {
    final byte[] _data = new byte[24];
    int i = UPDATE_REWARD_DURATION_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    putInt64LE(_data, i, newDuration);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record UpdateRewardDurationIxData(Discriminator discriminator, long rewardIndex, long newDuration) implements SerDe {  

    public static UpdateRewardDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdateRewardDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      i += 8;
      final var newDuration = getInt64LE(_data, i);
      return new UpdateRewardDurationIxData(discriminator, rewardIndex, newDuration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      putInt64LE(_data, i, newDuration);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_REWARD_FUNDER_DISCRIMINATOR = toDiscriminator(211, 28, 48, 32, 215, 160, 35, 23);

  public static List<AccountMeta> updateRewardFunderKeys(final PublicKey lbPairKey,
                                                         final PublicKey adminKey,
                                                         final PublicKey eventAuthorityKey,
                                                         final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createReadOnlySigner(adminKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updateRewardFunder(final AccountMeta invokedLbClmmProgramMeta,
                                               final PublicKey lbPairKey,
                                               final PublicKey adminKey,
                                               final PublicKey eventAuthorityKey,
                                               final PublicKey programKey,
                                               final long rewardIndex,
                                               final PublicKey newFunder) {
    final var keys = updateRewardFunderKeys(
      lbPairKey,
      adminKey,
      eventAuthorityKey,
      programKey
    );
    return updateRewardFunder(invokedLbClmmProgramMeta, keys, rewardIndex, newFunder);
  }

  public static Instruction updateRewardFunder(final AccountMeta invokedLbClmmProgramMeta,
                                               final List<AccountMeta> keys,
                                               final long rewardIndex,
                                               final PublicKey newFunder) {
    final byte[] _data = new byte[48];
    int i = UPDATE_REWARD_FUNDER_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    newFunder.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record UpdateRewardFunderIxData(Discriminator discriminator, long rewardIndex, PublicKey newFunder) implements SerDe {  

    public static UpdateRewardFunderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 48;

    public static UpdateRewardFunderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      i += 8;
      final var newFunder = readPubKey(_data, i);
      return new UpdateRewardFunderIxData(discriminator, rewardIndex, newFunder);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      newFunder.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_INELIGIBLE_REWARD_DISCRIMINATOR = toDiscriminator(148, 206, 42, 195, 247, 49, 103, 8);

  public static List<AccountMeta> withdrawIneligibleRewardKeys(final PublicKey lbPairKey,
                                                               final PublicKey rewardVaultKey,
                                                               final PublicKey rewardMintKey,
                                                               final PublicKey funderTokenAccountKey,
                                                               final PublicKey funderKey,
                                                               final PublicKey binArrayKey,
                                                               final PublicKey tokenProgramKey,
                                                               final PublicKey memoProgramKey,
                                                               final PublicKey eventAuthorityKey,
                                                               final PublicKey programKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(rewardVaultKey),
      createRead(rewardMintKey),
      createWrite(funderTokenAccountKey),
      createReadOnlySigner(funderKey),
      createWrite(binArrayKey),
      createRead(tokenProgramKey),
      createRead(memoProgramKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction withdrawIneligibleReward(final AccountMeta invokedLbClmmProgramMeta,
                                                     final PublicKey lbPairKey,
                                                     final PublicKey rewardVaultKey,
                                                     final PublicKey rewardMintKey,
                                                     final PublicKey funderTokenAccountKey,
                                                     final PublicKey funderKey,
                                                     final PublicKey binArrayKey,
                                                     final PublicKey tokenProgramKey,
                                                     final PublicKey memoProgramKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey,
                                                     final long rewardIndex,
                                                     final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = withdrawIneligibleRewardKeys(
      lbPairKey,
      rewardVaultKey,
      rewardMintKey,
      funderTokenAccountKey,
      funderKey,
      binArrayKey,
      tokenProgramKey,
      memoProgramKey,
      eventAuthorityKey,
      programKey
    );
    return withdrawIneligibleReward(invokedLbClmmProgramMeta, keys, rewardIndex, remainingAccountsInfo);
  }

  public static Instruction withdrawIneligibleReward(final AccountMeta invokedLbClmmProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final long rewardIndex,
                                                     final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[16 + remainingAccountsInfo.l()];
    int i = WITHDRAW_INELIGIBLE_REWARD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, rewardIndex);
    i += 8;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record WithdrawIneligibleRewardIxData(Discriminator discriminator, long rewardIndex, RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static WithdrawIneligibleRewardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static WithdrawIneligibleRewardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var rewardIndex = getInt64LE(_data, i);
      i += 8;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new WithdrawIneligibleRewardIxData(discriminator, rewardIndex, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, rewardIndex);
      i += 8;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + remainingAccountsInfo.l();
    }
  }

  public static final Discriminator WITHDRAW_PROTOCOL_FEE_DISCRIMINATOR = toDiscriminator(158, 201, 158, 189, 33, 93, 162, 103);

  /// @param operatorKey operator
  public static List<AccountMeta> withdrawProtocolFeeKeys(final PublicKey lbPairKey,
                                                          final PublicKey reserveXKey,
                                                          final PublicKey reserveYKey,
                                                          final PublicKey tokenXMintKey,
                                                          final PublicKey tokenYMintKey,
                                                          final PublicKey receiverTokenXKey,
                                                          final PublicKey receiverTokenYKey,
                                                          final PublicKey claimFeeOperatorKey,
                                                          final PublicKey operatorKey,
                                                          final PublicKey tokenXProgramKey,
                                                          final PublicKey tokenYProgramKey,
                                                          final PublicKey memoProgramKey) {
    return List.of(
      createWrite(lbPairKey),
      createWrite(reserveXKey),
      createWrite(reserveYKey),
      createRead(tokenXMintKey),
      createRead(tokenYMintKey),
      createWrite(receiverTokenXKey),
      createWrite(receiverTokenYKey),
      createRead(claimFeeOperatorKey),
      createReadOnlySigner(operatorKey),
      createRead(tokenXProgramKey),
      createRead(tokenYProgramKey),
      createRead(memoProgramKey)
    );
  }

  /// @param operatorKey operator
  public static Instruction withdrawProtocolFee(final AccountMeta invokedLbClmmProgramMeta,
                                                final PublicKey lbPairKey,
                                                final PublicKey reserveXKey,
                                                final PublicKey reserveYKey,
                                                final PublicKey tokenXMintKey,
                                                final PublicKey tokenYMintKey,
                                                final PublicKey receiverTokenXKey,
                                                final PublicKey receiverTokenYKey,
                                                final PublicKey claimFeeOperatorKey,
                                                final PublicKey operatorKey,
                                                final PublicKey tokenXProgramKey,
                                                final PublicKey tokenYProgramKey,
                                                final PublicKey memoProgramKey,
                                                final long maxAmountX,
                                                final long maxAmountY,
                                                final RemainingAccountsInfo remainingAccountsInfo) {
    final var keys = withdrawProtocolFeeKeys(
      lbPairKey,
      reserveXKey,
      reserveYKey,
      tokenXMintKey,
      tokenYMintKey,
      receiverTokenXKey,
      receiverTokenYKey,
      claimFeeOperatorKey,
      operatorKey,
      tokenXProgramKey,
      tokenYProgramKey,
      memoProgramKey
    );
    return withdrawProtocolFee(
      invokedLbClmmProgramMeta,
      keys,
      maxAmountX,
      maxAmountY,
      remainingAccountsInfo
    );
  }

  public static Instruction withdrawProtocolFee(final AccountMeta invokedLbClmmProgramMeta,
                                                final List<AccountMeta> keys,
                                                final long maxAmountX,
                                                final long maxAmountY,
                                                final RemainingAccountsInfo remainingAccountsInfo) {
    final byte[] _data = new byte[24 + remainingAccountsInfo.l()];
    int i = WITHDRAW_PROTOCOL_FEE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxAmountX);
    i += 8;
    putInt64LE(_data, i, maxAmountY);
    i += 8;
    remainingAccountsInfo.write(_data, i);

    return Instruction.createInstruction(invokedLbClmmProgramMeta, keys, _data);
  }

  public record WithdrawProtocolFeeIxData(Discriminator discriminator,
                                          long maxAmountX,
                                          long maxAmountY,
                                          RemainingAccountsInfo remainingAccountsInfo) implements SerDe {  

    public static WithdrawProtocolFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static WithdrawProtocolFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxAmountX = getInt64LE(_data, i);
      i += 8;
      final var maxAmountY = getInt64LE(_data, i);
      i += 8;
      final var remainingAccountsInfo = RemainingAccountsInfo.read(_data, i);
      return new WithdrawProtocolFeeIxData(discriminator, maxAmountX, maxAmountY, remainingAccountsInfo);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxAmountX);
      i += 8;
      putInt64LE(_data, i, maxAmountY);
      i += 8;
      i += remainingAccountsInfo.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 8 + remainingAccountsInfo.l();
    }
  }

  private LbClmmProgram() {
  }
}
