package software.sava.idl.clients.jupiter.lend_borrow.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.lend_borrow.gen.types.AddressBool;

import static java.nio.charset.StandardCharsets.UTF_8;

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

public final class LendingProgram {

  public static final Discriminator DEPOSIT_DISCRIMINATOR = toDiscriminator(242, 35, 198, 137, 82, 225, 242, 182);

  public static List<AccountMeta> depositKeys(final SolanaAccounts solanaAccounts,
                                              final PublicKey signerKey,
                                              final PublicKey depositorTokenAccountKey,
                                              final PublicKey recipientTokenAccountKey,
                                              final PublicKey mintKey,
                                              final PublicKey lendingAdminKey,
                                              final PublicKey lendingKey,
                                              final PublicKey fTokenMintKey,
                                              final PublicKey supplyTokenReservesLiquidityKey,
                                              final PublicKey lendingSupplyPositionOnLiquidityKey,
                                              final PublicKey rateModelKey,
                                              final PublicKey vaultKey,
                                              final PublicKey liquidityKey,
                                              final PublicKey liquidityProgramKey,
                                              final PublicKey rewardsRateModelKey,
                                              final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(depositorTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(mintKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction deposit(final AccountMeta invokedLendingProgramMeta,
                                    final SolanaAccounts solanaAccounts,
                                    final PublicKey signerKey,
                                    final PublicKey depositorTokenAccountKey,
                                    final PublicKey recipientTokenAccountKey,
                                    final PublicKey mintKey,
                                    final PublicKey lendingAdminKey,
                                    final PublicKey lendingKey,
                                    final PublicKey fTokenMintKey,
                                    final PublicKey supplyTokenReservesLiquidityKey,
                                    final PublicKey lendingSupplyPositionOnLiquidityKey,
                                    final PublicKey rateModelKey,
                                    final PublicKey vaultKey,
                                    final PublicKey liquidityKey,
                                    final PublicKey liquidityProgramKey,
                                    final PublicKey rewardsRateModelKey,
                                    final PublicKey tokenProgramKey,
                                    final long assets) {
    final var keys = depositKeys(
      solanaAccounts,
      signerKey,
      depositorTokenAccountKey,
      recipientTokenAccountKey,
      mintKey,
      lendingAdminKey,
      lendingKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return deposit(invokedLendingProgramMeta, keys, assets);
  }

  public static Instruction deposit(final AccountMeta invokedLendingProgramMeta,
                                    final List<AccountMeta> keys,
                                    final long assets) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, assets);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record DepositIxData(Discriminator discriminator, long assets) implements Borsh {  

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
      final var assets = getInt64LE(_data, i);
      return new DepositIxData(discriminator, assets);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, assets);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_WITH_MIN_AMOUNT_OUT_DISCRIMINATOR = toDiscriminator(116, 144, 16, 97, 118, 109, 40, 119);

  public static List<AccountMeta> depositWithMinAmountOutKeys(final SolanaAccounts solanaAccounts,
                                                              final PublicKey signerKey,
                                                              final PublicKey depositorTokenAccountKey,
                                                              final PublicKey recipientTokenAccountKey,
                                                              final PublicKey mintKey,
                                                              final PublicKey lendingAdminKey,
                                                              final PublicKey lendingKey,
                                                              final PublicKey fTokenMintKey,
                                                              final PublicKey supplyTokenReservesLiquidityKey,
                                                              final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                              final PublicKey rateModelKey,
                                                              final PublicKey vaultKey,
                                                              final PublicKey liquidityKey,
                                                              final PublicKey liquidityProgramKey,
                                                              final PublicKey rewardsRateModelKey,
                                                              final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(depositorTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(mintKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction depositWithMinAmountOut(final AccountMeta invokedLendingProgramMeta,
                                                    final SolanaAccounts solanaAccounts,
                                                    final PublicKey signerKey,
                                                    final PublicKey depositorTokenAccountKey,
                                                    final PublicKey recipientTokenAccountKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey lendingAdminKey,
                                                    final PublicKey lendingKey,
                                                    final PublicKey fTokenMintKey,
                                                    final PublicKey supplyTokenReservesLiquidityKey,
                                                    final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                    final PublicKey rateModelKey,
                                                    final PublicKey vaultKey,
                                                    final PublicKey liquidityKey,
                                                    final PublicKey liquidityProgramKey,
                                                    final PublicKey rewardsRateModelKey,
                                                    final PublicKey tokenProgramKey,
                                                    final long assets,
                                                    final long minAmountOut) {
    final var keys = depositWithMinAmountOutKeys(
      solanaAccounts,
      signerKey,
      depositorTokenAccountKey,
      recipientTokenAccountKey,
      mintKey,
      lendingAdminKey,
      lendingKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return depositWithMinAmountOut(invokedLendingProgramMeta, keys, assets, minAmountOut);
  }

  public static Instruction depositWithMinAmountOut(final AccountMeta invokedLendingProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final long assets,
                                                    final long minAmountOut) {
    final byte[] _data = new byte[24];
    int i = DEPOSIT_WITH_MIN_AMOUNT_OUT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, assets);
    i += 8;
    putInt64LE(_data, i, minAmountOut);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record DepositWithMinAmountOutIxData(Discriminator discriminator, long assets, long minAmountOut) implements Borsh {  

    public static DepositWithMinAmountOutIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static DepositWithMinAmountOutIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var assets = getInt64LE(_data, i);
      i += 8;
      final var minAmountOut = getInt64LE(_data, i);
      return new DepositWithMinAmountOutIxData(discriminator, assets, minAmountOut);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, assets);
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

  public static final Discriminator INIT_LENDING_DISCRIMINATOR = toDiscriminator(156, 224, 67, 46, 89, 189, 157, 209);

  public static List<AccountMeta> initLendingKeys(final SolanaAccounts solanaAccounts,
                                                  final PublicKey signerKey,
                                                  final PublicKey lendingAdminKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey fTokenMintKey,
                                                  final PublicKey metadataAccountKey,
                                                  final PublicKey lendingKey,
                                                  final PublicKey tokenReservesLiquidityKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey metadataProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(lendingAdminKey),
      createRead(mintKey),
      createWrite(fTokenMintKey),
      createWrite(metadataAccountKey),
      createWrite(lendingKey),
      createRead(tokenReservesLiquidityKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.instructionsSysVar()),
      createRead(metadataProgramKey),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  public static Instruction initLending(final AccountMeta invokedLendingProgramMeta,
                                        final SolanaAccounts solanaAccounts,
                                        final PublicKey signerKey,
                                        final PublicKey lendingAdminKey,
                                        final PublicKey mintKey,
                                        final PublicKey fTokenMintKey,
                                        final PublicKey metadataAccountKey,
                                        final PublicKey lendingKey,
                                        final PublicKey tokenReservesLiquidityKey,
                                        final PublicKey tokenProgramKey,
                                        final PublicKey metadataProgramKey,
                                        final String symbol,
                                        final PublicKey liquidityProgram) {
    final var keys = initLendingKeys(
      solanaAccounts,
      signerKey,
      lendingAdminKey,
      mintKey,
      fTokenMintKey,
      metadataAccountKey,
      lendingKey,
      tokenReservesLiquidityKey,
      tokenProgramKey,
      metadataProgramKey
    );
    return initLending(invokedLendingProgramMeta, keys, symbol, liquidityProgram);
  }

  public static Instruction initLending(final AccountMeta invokedLendingProgramMeta,
                                        final List<AccountMeta> keys,
                                        final String symbol,
                                        final PublicKey liquidityProgram) {
    final byte[] _symbol = symbol.getBytes(UTF_8);
    final byte[] _data = new byte[44 + _symbol.length];
    int i = INIT_LENDING_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(_symbol, _data, i);
    liquidityProgram.write(_data, i);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record InitLendingIxData(Discriminator discriminator, String symbol, byte[] _symbol, PublicKey liquidityProgram) implements Borsh {  

    public static InitLendingIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitLendingIxData createRecord(final Discriminator discriminator, final String symbol, final PublicKey liquidityProgram) {
      return new InitLendingIxData(discriminator, symbol, symbol.getBytes(UTF_8), liquidityProgram);
    }

    public static InitLendingIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _symbolLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _symbol = Arrays.copyOfRange(_data, i, i + _symbolLength);
      final var symbol = new String(_symbol, UTF_8);
      i += _symbol.length;
      final var liquidityProgram = readPubKey(_data, i);
      return new InitLendingIxData(discriminator, symbol, _symbol, liquidityProgram);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_symbol, _data, i);
      liquidityProgram.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _symbol.length + 32;
    }
  }

  public static final Discriminator INIT_LENDING_ADMIN_DISCRIMINATOR = toDiscriminator(203, 185, 241, 165, 56, 254, 33, 9);

  public static List<AccountMeta> initLendingAdminKeys(final SolanaAccounts solanaAccounts,
                                                       final PublicKey authorityKey,
                                                       final PublicKey lendingAdminKey) {
    return List.of(
      createWritableSigner(authorityKey),
      createWrite(lendingAdminKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction initLendingAdmin(final AccountMeta invokedLendingProgramMeta,
                                             final SolanaAccounts solanaAccounts,
                                             final PublicKey authorityKey,
                                             final PublicKey lendingAdminKey,
                                             final PublicKey liquidityProgram,
                                             final PublicKey rebalancer,
                                             final PublicKey authority) {
    final var keys = initLendingAdminKeys(
      solanaAccounts,
      authorityKey,
      lendingAdminKey
    );
    return initLendingAdmin(
      invokedLendingProgramMeta,
      keys,
      liquidityProgram,
      rebalancer,
      authority
    );
  }

  public static Instruction initLendingAdmin(final AccountMeta invokedLendingProgramMeta,
                                             final List<AccountMeta> keys,
                                             final PublicKey liquidityProgram,
                                             final PublicKey rebalancer,
                                             final PublicKey authority) {
    final byte[] _data = new byte[104];
    int i = INIT_LENDING_ADMIN_DISCRIMINATOR.write(_data, 0);
    liquidityProgram.write(_data, i);
    i += 32;
    rebalancer.write(_data, i);
    i += 32;
    authority.write(_data, i);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record InitLendingAdminIxData(Discriminator discriminator,
                                       PublicKey liquidityProgram,
                                       PublicKey rebalancer,
                                       PublicKey authority) implements Borsh {  

    public static InitLendingAdminIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 104;

    public static InitLendingAdminIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidityProgram = readPubKey(_data, i);
      i += 32;
      final var rebalancer = readPubKey(_data, i);
      i += 32;
      final var authority = readPubKey(_data, i);
      return new InitLendingAdminIxData(discriminator, liquidityProgram, rebalancer, authority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      liquidityProgram.write(_data, i);
      i += 32;
      rebalancer.write(_data, i);
      i += 32;
      authority.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MINT_DISCRIMINATOR = toDiscriminator(51, 57, 225, 47, 182, 146, 137, 166);

  public static List<AccountMeta> mintKeys(final SolanaAccounts solanaAccounts,
                                           final PublicKey signerKey,
                                           final PublicKey depositorTokenAccountKey,
                                           final PublicKey recipientTokenAccountKey,
                                           final PublicKey mintKey,
                                           final PublicKey lendingAdminKey,
                                           final PublicKey lendingKey,
                                           final PublicKey fTokenMintKey,
                                           final PublicKey supplyTokenReservesLiquidityKey,
                                           final PublicKey lendingSupplyPositionOnLiquidityKey,
                                           final PublicKey rateModelKey,
                                           final PublicKey vaultKey,
                                           final PublicKey liquidityKey,
                                           final PublicKey liquidityProgramKey,
                                           final PublicKey rewardsRateModelKey,
                                           final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(depositorTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(mintKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction mint(final AccountMeta invokedLendingProgramMeta,
                                 final SolanaAccounts solanaAccounts,
                                 final PublicKey signerKey,
                                 final PublicKey depositorTokenAccountKey,
                                 final PublicKey recipientTokenAccountKey,
                                 final PublicKey mintKey,
                                 final PublicKey lendingAdminKey,
                                 final PublicKey lendingKey,
                                 final PublicKey fTokenMintKey,
                                 final PublicKey supplyTokenReservesLiquidityKey,
                                 final PublicKey lendingSupplyPositionOnLiquidityKey,
                                 final PublicKey rateModelKey,
                                 final PublicKey vaultKey,
                                 final PublicKey liquidityKey,
                                 final PublicKey liquidityProgramKey,
                                 final PublicKey rewardsRateModelKey,
                                 final PublicKey tokenProgramKey,
                                 final long shares) {
    final var keys = mintKeys(
      solanaAccounts,
      signerKey,
      depositorTokenAccountKey,
      recipientTokenAccountKey,
      mintKey,
      lendingAdminKey,
      lendingKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return mint(invokedLendingProgramMeta, keys, shares);
  }

  public static Instruction mint(final AccountMeta invokedLendingProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long shares) {
    final byte[] _data = new byte[16];
    int i = MINT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, shares);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record MintIxData(Discriminator discriminator, long shares) implements Borsh {  

    public static MintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static MintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var shares = getInt64LE(_data, i);
      return new MintIxData(discriminator, shares);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, shares);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MINT_WITH_MAX_ASSETS_DISCRIMINATOR = toDiscriminator(6, 94, 69, 122, 30, 179, 146, 171);

  public static List<AccountMeta> mintWithMaxAssetsKeys(final SolanaAccounts solanaAccounts,
                                                        final PublicKey signerKey,
                                                        final PublicKey depositorTokenAccountKey,
                                                        final PublicKey recipientTokenAccountKey,
                                                        final PublicKey mintKey,
                                                        final PublicKey lendingAdminKey,
                                                        final PublicKey lendingKey,
                                                        final PublicKey fTokenMintKey,
                                                        final PublicKey supplyTokenReservesLiquidityKey,
                                                        final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                        final PublicKey rateModelKey,
                                                        final PublicKey vaultKey,
                                                        final PublicKey liquidityKey,
                                                        final PublicKey liquidityProgramKey,
                                                        final PublicKey rewardsRateModelKey,
                                                        final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(depositorTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(mintKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction mintWithMaxAssets(final AccountMeta invokedLendingProgramMeta,
                                              final SolanaAccounts solanaAccounts,
                                              final PublicKey signerKey,
                                              final PublicKey depositorTokenAccountKey,
                                              final PublicKey recipientTokenAccountKey,
                                              final PublicKey mintKey,
                                              final PublicKey lendingAdminKey,
                                              final PublicKey lendingKey,
                                              final PublicKey fTokenMintKey,
                                              final PublicKey supplyTokenReservesLiquidityKey,
                                              final PublicKey lendingSupplyPositionOnLiquidityKey,
                                              final PublicKey rateModelKey,
                                              final PublicKey vaultKey,
                                              final PublicKey liquidityKey,
                                              final PublicKey liquidityProgramKey,
                                              final PublicKey rewardsRateModelKey,
                                              final PublicKey tokenProgramKey,
                                              final long shares,
                                              final long maxAssets) {
    final var keys = mintWithMaxAssetsKeys(
      solanaAccounts,
      signerKey,
      depositorTokenAccountKey,
      recipientTokenAccountKey,
      mintKey,
      lendingAdminKey,
      lendingKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return mintWithMaxAssets(invokedLendingProgramMeta, keys, shares, maxAssets);
  }

  public static Instruction mintWithMaxAssets(final AccountMeta invokedLendingProgramMeta,
                                              final List<AccountMeta> keys,
                                              final long shares,
                                              final long maxAssets) {
    final byte[] _data = new byte[24];
    int i = MINT_WITH_MAX_ASSETS_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, shares);
    i += 8;
    putInt64LE(_data, i, maxAssets);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record MintWithMaxAssetsIxData(Discriminator discriminator, long shares, long maxAssets) implements Borsh {  

    public static MintWithMaxAssetsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static MintWithMaxAssetsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var shares = getInt64LE(_data, i);
      i += 8;
      final var maxAssets = getInt64LE(_data, i);
      return new MintWithMaxAssetsIxData(discriminator, shares, maxAssets);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, shares);
      i += 8;
      putInt64LE(_data, i, maxAssets);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REBALANCE_DISCRIMINATOR = toDiscriminator(108, 158, 77, 9, 210, 52, 88, 62);

  public static List<AccountMeta> rebalanceKeys(final SolanaAccounts solanaAccounts,
                                                final PublicKey signerKey,
                                                final PublicKey depositorTokenAccountKey,
                                                final PublicKey lendingAdminKey,
                                                final PublicKey lendingKey,
                                                final PublicKey mintKey,
                                                final PublicKey fTokenMintKey,
                                                final PublicKey supplyTokenReservesLiquidityKey,
                                                final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                final PublicKey rateModelKey,
                                                final PublicKey vaultKey,
                                                final PublicKey liquidityKey,
                                                final PublicKey liquidityProgramKey,
                                                final PublicKey rewardsRateModelKey,
                                                final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(depositorTokenAccountKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createRead(mintKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createWrite(rateModelKey),
      createWrite(vaultKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction rebalance(final AccountMeta invokedLendingProgramMeta,
                                      final SolanaAccounts solanaAccounts,
                                      final PublicKey signerKey,
                                      final PublicKey depositorTokenAccountKey,
                                      final PublicKey lendingAdminKey,
                                      final PublicKey lendingKey,
                                      final PublicKey mintKey,
                                      final PublicKey fTokenMintKey,
                                      final PublicKey supplyTokenReservesLiquidityKey,
                                      final PublicKey lendingSupplyPositionOnLiquidityKey,
                                      final PublicKey rateModelKey,
                                      final PublicKey vaultKey,
                                      final PublicKey liquidityKey,
                                      final PublicKey liquidityProgramKey,
                                      final PublicKey rewardsRateModelKey,
                                      final PublicKey tokenProgramKey) {
    final var keys = rebalanceKeys(
      solanaAccounts,
      signerKey,
      depositorTokenAccountKey,
      lendingAdminKey,
      lendingKey,
      mintKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return rebalance(invokedLendingProgramMeta, keys);
  }

  public static Instruction rebalance(final AccountMeta invokedLendingProgramMeta,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLendingProgramMeta, keys, REBALANCE_DISCRIMINATOR);
  }

  public static final Discriminator REDEEM_DISCRIMINATOR = toDiscriminator(184, 12, 86, 149, 70, 196, 97, 225);

  public static List<AccountMeta> redeemKeys(final SolanaAccounts solanaAccounts,
                                             final PublicKey signerKey,
                                             final PublicKey ownerTokenAccountKey,
                                             final PublicKey recipientTokenAccountKey,
                                             final PublicKey lendingAdminKey,
                                             final PublicKey lendingKey,
                                             final PublicKey mintKey,
                                             final PublicKey fTokenMintKey,
                                             final PublicKey supplyTokenReservesLiquidityKey,
                                             final PublicKey lendingSupplyPositionOnLiquidityKey,
                                             final PublicKey rateModelKey,
                                             final PublicKey vaultKey,
                                             final PublicKey claimAccountKey,
                                             final PublicKey liquidityKey,
                                             final PublicKey liquidityProgramKey,
                                             final PublicKey rewardsRateModelKey,
                                             final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(ownerTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createRead(mintKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(claimAccountKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction redeem(final AccountMeta invokedLendingProgramMeta,
                                   final SolanaAccounts solanaAccounts,
                                   final PublicKey signerKey,
                                   final PublicKey ownerTokenAccountKey,
                                   final PublicKey recipientTokenAccountKey,
                                   final PublicKey lendingAdminKey,
                                   final PublicKey lendingKey,
                                   final PublicKey mintKey,
                                   final PublicKey fTokenMintKey,
                                   final PublicKey supplyTokenReservesLiquidityKey,
                                   final PublicKey lendingSupplyPositionOnLiquidityKey,
                                   final PublicKey rateModelKey,
                                   final PublicKey vaultKey,
                                   final PublicKey claimAccountKey,
                                   final PublicKey liquidityKey,
                                   final PublicKey liquidityProgramKey,
                                   final PublicKey rewardsRateModelKey,
                                   final PublicKey tokenProgramKey,
                                   final long shares) {
    final var keys = redeemKeys(
      solanaAccounts,
      signerKey,
      ownerTokenAccountKey,
      recipientTokenAccountKey,
      lendingAdminKey,
      lendingKey,
      mintKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      claimAccountKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return redeem(invokedLendingProgramMeta, keys, shares);
  }

  public static Instruction redeem(final AccountMeta invokedLendingProgramMeta,
                                   final List<AccountMeta> keys,
                                   final long shares) {
    final byte[] _data = new byte[16];
    int i = REDEEM_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, shares);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record RedeemIxData(Discriminator discriminator, long shares) implements Borsh {  

    public static RedeemIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static RedeemIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var shares = getInt64LE(_data, i);
      return new RedeemIxData(discriminator, shares);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, shares);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REDEEM_WITH_MIN_AMOUNT_OUT_DISCRIMINATOR = toDiscriminator(235, 189, 237, 56, 166, 180, 184, 149);

  public static List<AccountMeta> redeemWithMinAmountOutKeys(final SolanaAccounts solanaAccounts,
                                                             final PublicKey signerKey,
                                                             final PublicKey ownerTokenAccountKey,
                                                             final PublicKey recipientTokenAccountKey,
                                                             final PublicKey lendingAdminKey,
                                                             final PublicKey lendingKey,
                                                             final PublicKey mintKey,
                                                             final PublicKey fTokenMintKey,
                                                             final PublicKey supplyTokenReservesLiquidityKey,
                                                             final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                             final PublicKey rateModelKey,
                                                             final PublicKey vaultKey,
                                                             final PublicKey claimAccountKey,
                                                             final PublicKey liquidityKey,
                                                             final PublicKey liquidityProgramKey,
                                                             final PublicKey rewardsRateModelKey,
                                                             final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(ownerTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createRead(mintKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(claimAccountKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction redeemWithMinAmountOut(final AccountMeta invokedLendingProgramMeta,
                                                   final SolanaAccounts solanaAccounts,
                                                   final PublicKey signerKey,
                                                   final PublicKey ownerTokenAccountKey,
                                                   final PublicKey recipientTokenAccountKey,
                                                   final PublicKey lendingAdminKey,
                                                   final PublicKey lendingKey,
                                                   final PublicKey mintKey,
                                                   final PublicKey fTokenMintKey,
                                                   final PublicKey supplyTokenReservesLiquidityKey,
                                                   final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                   final PublicKey rateModelKey,
                                                   final PublicKey vaultKey,
                                                   final PublicKey claimAccountKey,
                                                   final PublicKey liquidityKey,
                                                   final PublicKey liquidityProgramKey,
                                                   final PublicKey rewardsRateModelKey,
                                                   final PublicKey tokenProgramKey,
                                                   final long shares,
                                                   final long minAmountOut) {
    final var keys = redeemWithMinAmountOutKeys(
      solanaAccounts,
      signerKey,
      ownerTokenAccountKey,
      recipientTokenAccountKey,
      lendingAdminKey,
      lendingKey,
      mintKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      claimAccountKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return redeemWithMinAmountOut(invokedLendingProgramMeta, keys, shares, minAmountOut);
  }

  public static Instruction redeemWithMinAmountOut(final AccountMeta invokedLendingProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final long shares,
                                                   final long minAmountOut) {
    final byte[] _data = new byte[24];
    int i = REDEEM_WITH_MIN_AMOUNT_OUT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, shares);
    i += 8;
    putInt64LE(_data, i, minAmountOut);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record RedeemWithMinAmountOutIxData(Discriminator discriminator, long shares, long minAmountOut) implements Borsh {  

    public static RedeemWithMinAmountOutIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static RedeemWithMinAmountOutIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var shares = getInt64LE(_data, i);
      i += 8;
      final var minAmountOut = getInt64LE(_data, i);
      return new RedeemWithMinAmountOutIxData(discriminator, shares, minAmountOut);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, shares);
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

  public static final Discriminator SET_REWARDS_RATE_MODEL_DISCRIMINATOR = toDiscriminator(174, 231, 116, 203, 8, 58, 143, 203);

  public static List<AccountMeta> setRewardsRateModelKeys(final PublicKey signerKey,
                                                          final PublicKey lendingAdminKey,
                                                          final PublicKey lendingKey,
                                                          final PublicKey fTokenMintKey,
                                                          final PublicKey newRewardsRateModelKey,
                                                          final PublicKey supplyTokenReservesLiquidityKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createRead(fTokenMintKey),
      createRead(newRewardsRateModelKey),
      createRead(supplyTokenReservesLiquidityKey)
    );
  }

  public static Instruction setRewardsRateModel(final AccountMeta invokedLendingProgramMeta,
                                                final PublicKey signerKey,
                                                final PublicKey lendingAdminKey,
                                                final PublicKey lendingKey,
                                                final PublicKey fTokenMintKey,
                                                final PublicKey newRewardsRateModelKey,
                                                final PublicKey supplyTokenReservesLiquidityKey,
                                                final PublicKey mint) {
    final var keys = setRewardsRateModelKeys(
      signerKey,
      lendingAdminKey,
      lendingKey,
      fTokenMintKey,
      newRewardsRateModelKey,
      supplyTokenReservesLiquidityKey
    );
    return setRewardsRateModel(invokedLendingProgramMeta, keys, mint);
  }

  public static Instruction setRewardsRateModel(final AccountMeta invokedLendingProgramMeta,
                                                final List<AccountMeta> keys,
                                                final PublicKey mint) {
    final byte[] _data = new byte[40];
    int i = SET_REWARDS_RATE_MODEL_DISCRIMINATOR.write(_data, 0);
    mint.write(_data, i);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record SetRewardsRateModelIxData(Discriminator discriminator, PublicKey mint) implements Borsh {  

    public static SetRewardsRateModelIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetRewardsRateModelIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mint = readPubKey(_data, i);
      return new SetRewardsRateModelIxData(discriminator, mint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      mint.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_AUTHORITY_DISCRIMINATOR = toDiscriminator(32, 46, 64, 28, 149, 75, 243, 88);

  public static List<AccountMeta> updateAuthorityKeys(final PublicKey signerKey,
                                                      final PublicKey lendingAdminKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createWrite(lendingAdminKey)
    );
  }

  public static Instruction updateAuthority(final AccountMeta invokedLendingProgramMeta,
                                            final PublicKey signerKey,
                                            final PublicKey lendingAdminKey,
                                            final PublicKey newAuthority) {
    final var keys = updateAuthorityKeys(
      signerKey,
      lendingAdminKey
    );
    return updateAuthority(invokedLendingProgramMeta, keys, newAuthority);
  }

  public static Instruction updateAuthority(final AccountMeta invokedLendingProgramMeta,
                                            final List<AccountMeta> keys,
                                            final PublicKey newAuthority) {
    final byte[] _data = new byte[40];
    int i = UPDATE_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    newAuthority.write(_data, i);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record UpdateAuthorityIxData(Discriminator discriminator, PublicKey newAuthority) implements Borsh {  

    public static UpdateAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newAuthority = readPubKey(_data, i);
      return new UpdateAuthorityIxData(discriminator, newAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newAuthority.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_AUTHS_DISCRIMINATOR = toDiscriminator(93, 96, 178, 156, 57, 117, 253, 209);

  public static List<AccountMeta> updateAuthsKeys(final PublicKey signerKey,
                                                  final PublicKey lendingAdminKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createWrite(lendingAdminKey)
    );
  }

  public static Instruction updateAuths(final AccountMeta invokedLendingProgramMeta,
                                        final PublicKey signerKey,
                                        final PublicKey lendingAdminKey,
                                        final AddressBool[] authStatus) {
    final var keys = updateAuthsKeys(
      signerKey,
      lendingAdminKey
    );
    return updateAuths(invokedLendingProgramMeta, keys, authStatus);
  }

  public static Instruction updateAuths(final AccountMeta invokedLendingProgramMeta,
                                        final List<AccountMeta> keys,
                                        final AddressBool[] authStatus) {
    final byte[] _data = new byte[8 + Borsh.lenVector(authStatus)];
    int i = UPDATE_AUTHS_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(authStatus, _data, i);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record UpdateAuthsIxData(Discriminator discriminator, AddressBool[] authStatus) implements Borsh {  

    public static UpdateAuthsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateAuthsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var authStatus = Borsh.readVector(AddressBool.class, AddressBool::read, _data, i);
      return new UpdateAuthsIxData(discriminator, authStatus);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(authStatus, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(authStatus);
    }
  }

  public static final Discriminator UPDATE_RATE_DISCRIMINATOR = toDiscriminator(24, 225, 53, 189, 72, 212, 225, 178);

  public static List<AccountMeta> updateRateKeys(final PublicKey lendingKey,
                                                 final PublicKey mintKey,
                                                 final PublicKey fTokenMintKey,
                                                 final PublicKey supplyTokenReservesLiquidityKey,
                                                 final PublicKey rewardsRateModelKey) {
    return List.of(
      createWrite(lendingKey),
      createRead(mintKey),
      createRead(fTokenMintKey),
      createRead(supplyTokenReservesLiquidityKey),
      createRead(rewardsRateModelKey)
    );
  }

  public static Instruction updateRate(final AccountMeta invokedLendingProgramMeta,
                                       final PublicKey lendingKey,
                                       final PublicKey mintKey,
                                       final PublicKey fTokenMintKey,
                                       final PublicKey supplyTokenReservesLiquidityKey,
                                       final PublicKey rewardsRateModelKey) {
    final var keys = updateRateKeys(
      lendingKey,
      mintKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      rewardsRateModelKey
    );
    return updateRate(invokedLendingProgramMeta, keys);
  }

  public static Instruction updateRate(final AccountMeta invokedLendingProgramMeta,
                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedLendingProgramMeta, keys, UPDATE_RATE_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_REBALANCER_DISCRIMINATOR = toDiscriminator(206, 187, 54, 228, 145, 8, 203, 111);

  public static List<AccountMeta> updateRebalancerKeys(final PublicKey signerKey,
                                                       final PublicKey lendingAdminKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createWrite(lendingAdminKey)
    );
  }

  public static Instruction updateRebalancer(final AccountMeta invokedLendingProgramMeta,
                                             final PublicKey signerKey,
                                             final PublicKey lendingAdminKey,
                                             final PublicKey newRebalancer) {
    final var keys = updateRebalancerKeys(
      signerKey,
      lendingAdminKey
    );
    return updateRebalancer(invokedLendingProgramMeta, keys, newRebalancer);
  }

  public static Instruction updateRebalancer(final AccountMeta invokedLendingProgramMeta,
                                             final List<AccountMeta> keys,
                                             final PublicKey newRebalancer) {
    final byte[] _data = new byte[40];
    int i = UPDATE_REBALANCER_DISCRIMINATOR.write(_data, 0);
    newRebalancer.write(_data, i);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record UpdateRebalancerIxData(Discriminator discriminator, PublicKey newRebalancer) implements Borsh {  

    public static UpdateRebalancerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateRebalancerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newRebalancer = readPubKey(_data, i);
      return new UpdateRebalancerIxData(discriminator, newRebalancer);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newRebalancer.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 18, 70, 156, 148, 109, 161, 34);

  public static List<AccountMeta> withdrawKeys(final SolanaAccounts solanaAccounts,
                                               final PublicKey signerKey,
                                               final PublicKey ownerTokenAccountKey,
                                               final PublicKey recipientTokenAccountKey,
                                               final PublicKey lendingAdminKey,
                                               final PublicKey lendingKey,
                                               final PublicKey mintKey,
                                               final PublicKey fTokenMintKey,
                                               final PublicKey supplyTokenReservesLiquidityKey,
                                               final PublicKey lendingSupplyPositionOnLiquidityKey,
                                               final PublicKey rateModelKey,
                                               final PublicKey vaultKey,
                                               final PublicKey claimAccountKey,
                                               final PublicKey liquidityKey,
                                               final PublicKey liquidityProgramKey,
                                               final PublicKey rewardsRateModelKey,
                                               final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(ownerTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createRead(mintKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(claimAccountKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction withdraw(final AccountMeta invokedLendingProgramMeta,
                                     final SolanaAccounts solanaAccounts,
                                     final PublicKey signerKey,
                                     final PublicKey ownerTokenAccountKey,
                                     final PublicKey recipientTokenAccountKey,
                                     final PublicKey lendingAdminKey,
                                     final PublicKey lendingKey,
                                     final PublicKey mintKey,
                                     final PublicKey fTokenMintKey,
                                     final PublicKey supplyTokenReservesLiquidityKey,
                                     final PublicKey lendingSupplyPositionOnLiquidityKey,
                                     final PublicKey rateModelKey,
                                     final PublicKey vaultKey,
                                     final PublicKey claimAccountKey,
                                     final PublicKey liquidityKey,
                                     final PublicKey liquidityProgramKey,
                                     final PublicKey rewardsRateModelKey,
                                     final PublicKey tokenProgramKey,
                                     final long amount) {
    final var keys = withdrawKeys(
      solanaAccounts,
      signerKey,
      ownerTokenAccountKey,
      recipientTokenAccountKey,
      lendingAdminKey,
      lendingKey,
      mintKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      claimAccountKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return withdraw(invokedLendingProgramMeta, keys, amount);
  }

  public static Instruction withdraw(final AccountMeta invokedLendingProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long amount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record WithdrawIxData(Discriminator discriminator, long amount) implements Borsh {  

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
      final var amount = getInt64LE(_data, i);
      return new WithdrawIxData(discriminator, amount);
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

  public static final Discriminator WITHDRAW_WITH_MAX_SHARES_BURN_DISCRIMINATOR = toDiscriminator(47, 197, 183, 171, 239, 18, 245, 171);

  public static List<AccountMeta> withdrawWithMaxSharesBurnKeys(final SolanaAccounts solanaAccounts,
                                                                final PublicKey signerKey,
                                                                final PublicKey ownerTokenAccountKey,
                                                                final PublicKey recipientTokenAccountKey,
                                                                final PublicKey lendingAdminKey,
                                                                final PublicKey lendingKey,
                                                                final PublicKey mintKey,
                                                                final PublicKey fTokenMintKey,
                                                                final PublicKey supplyTokenReservesLiquidityKey,
                                                                final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                                final PublicKey rateModelKey,
                                                                final PublicKey vaultKey,
                                                                final PublicKey claimAccountKey,
                                                                final PublicKey liquidityKey,
                                                                final PublicKey liquidityProgramKey,
                                                                final PublicKey rewardsRateModelKey,
                                                                final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(signerKey),
      createWrite(ownerTokenAccountKey),
      createWrite(recipientTokenAccountKey),
      createRead(lendingAdminKey),
      createWrite(lendingKey),
      createRead(mintKey),
      createWrite(fTokenMintKey),
      createWrite(supplyTokenReservesLiquidityKey),
      createWrite(lendingSupplyPositionOnLiquidityKey),
      createRead(rateModelKey),
      createWrite(vaultKey),
      createWrite(claimAccountKey),
      createWrite(liquidityKey),
      createWrite(liquidityProgramKey),
      createRead(rewardsRateModelKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction withdrawWithMaxSharesBurn(final AccountMeta invokedLendingProgramMeta,
                                                      final SolanaAccounts solanaAccounts,
                                                      final PublicKey signerKey,
                                                      final PublicKey ownerTokenAccountKey,
                                                      final PublicKey recipientTokenAccountKey,
                                                      final PublicKey lendingAdminKey,
                                                      final PublicKey lendingKey,
                                                      final PublicKey mintKey,
                                                      final PublicKey fTokenMintKey,
                                                      final PublicKey supplyTokenReservesLiquidityKey,
                                                      final PublicKey lendingSupplyPositionOnLiquidityKey,
                                                      final PublicKey rateModelKey,
                                                      final PublicKey vaultKey,
                                                      final PublicKey claimAccountKey,
                                                      final PublicKey liquidityKey,
                                                      final PublicKey liquidityProgramKey,
                                                      final PublicKey rewardsRateModelKey,
                                                      final PublicKey tokenProgramKey,
                                                      final long amount,
                                                      final long maxSharesBurn) {
    final var keys = withdrawWithMaxSharesBurnKeys(
      solanaAccounts,
      signerKey,
      ownerTokenAccountKey,
      recipientTokenAccountKey,
      lendingAdminKey,
      lendingKey,
      mintKey,
      fTokenMintKey,
      supplyTokenReservesLiquidityKey,
      lendingSupplyPositionOnLiquidityKey,
      rateModelKey,
      vaultKey,
      claimAccountKey,
      liquidityKey,
      liquidityProgramKey,
      rewardsRateModelKey,
      tokenProgramKey
    );
    return withdrawWithMaxSharesBurn(invokedLendingProgramMeta, keys, amount, maxSharesBurn);
  }

  public static Instruction withdrawWithMaxSharesBurn(final AccountMeta invokedLendingProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final long amount,
                                                      final long maxSharesBurn) {
    final byte[] _data = new byte[24];
    int i = WITHDRAW_WITH_MAX_SHARES_BURN_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, maxSharesBurn);

    return Instruction.createInstruction(invokedLendingProgramMeta, keys, _data);
  }

  public record WithdrawWithMaxSharesBurnIxData(Discriminator discriminator, long amount, long maxSharesBurn) implements Borsh {  

    public static WithdrawWithMaxSharesBurnIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static WithdrawWithMaxSharesBurnIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var maxSharesBurn = getInt64LE(_data, i);
      return new WithdrawWithMaxSharesBurnIxData(discriminator, amount, maxSharesBurn);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      putInt64LE(_data, i, maxSharesBurn);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  private LendingProgram() {
  }
}
