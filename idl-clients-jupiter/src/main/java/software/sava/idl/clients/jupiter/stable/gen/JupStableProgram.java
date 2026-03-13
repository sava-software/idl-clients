package software.sava.idl.clients.jupiter.stable.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.jupiter.stable.gen.types.BenefactorManagementAction;
import software.sava.idl.clients.jupiter.stable.gen.types.ConfigManagementAction;
import software.sava.idl.clients.jupiter.stable.gen.types.OperatorManagementAction;
import software.sava.idl.clients.jupiter.stable.gen.types.OperatorRole;
import software.sava.idl.clients.jupiter.stable.gen.types.VaultManagementAction;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class JupStableProgram {

  public static final Discriminator CREATE_BENEFACTOR_DISCRIMINATOR = toDiscriminator(184, 241, 45, 0, 53, 40, 201, 54);

  public static List<AccountMeta> createBenefactorKeys(final SolanaAccounts solanaAccounts,
                                                       final PublicKey operatorAuthorityKey,
                                                       final PublicKey operatorKey,
                                                       final PublicKey payerKey,
                                                       final PublicKey benefactorAuthorityKey,
                                                       final PublicKey benefactorKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWritableSigner(payerKey),
      createRead(benefactorAuthorityKey),
      createWrite(benefactorKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction createBenefactor(final AccountMeta invokedJupStableProgramMeta,
                                             final SolanaAccounts solanaAccounts,
                                             final PublicKey operatorAuthorityKey,
                                             final PublicKey operatorKey,
                                             final PublicKey payerKey,
                                             final PublicKey benefactorAuthorityKey,
                                             final PublicKey benefactorKey,
                                             final int mintFeeRate,
                                             final int redeemFeeRate) {
    final var keys = createBenefactorKeys(
      solanaAccounts,
      operatorAuthorityKey,
      operatorKey,
      payerKey,
      benefactorAuthorityKey,
      benefactorKey
    );
    return createBenefactor(invokedJupStableProgramMeta, keys, mintFeeRate, redeemFeeRate);
  }

  public static Instruction createBenefactor(final AccountMeta invokedJupStableProgramMeta,
                                             final List<AccountMeta> keys,
                                             final int mintFeeRate,
                                             final int redeemFeeRate) {
    final byte[] _data = new byte[12];
    int i = CREATE_BENEFACTOR_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, mintFeeRate);
    i += 2;
    putInt16LE(_data, i, redeemFeeRate);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record CreateBenefactorIxData(Discriminator discriminator, int mintFeeRate, int redeemFeeRate) implements SerDe {  

    public static CreateBenefactorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static final int MINT_FEE_RATE_OFFSET = 8;
    public static final int REDEEM_FEE_RATE_OFFSET = 10;

    public static CreateBenefactorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var mintFeeRate = getInt16LE(_data, i);
      i += 2;
      final var redeemFeeRate = getInt16LE(_data, i);
      return new CreateBenefactorIxData(discriminator, mintFeeRate, redeemFeeRate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, mintFeeRate);
      i += 2;
      putInt16LE(_data, i, redeemFeeRate);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_OPERATOR_DISCRIMINATOR = toDiscriminator(145, 40, 238, 75, 181, 252, 59, 11);

  public static List<AccountMeta> createOperatorKeys(final SolanaAccounts solanaAccounts,
                                                     final PublicKey operatorAuthorityKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey operatorKey,
                                                     final PublicKey newOperatorAuthorityKey,
                                                     final PublicKey newOperatorKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createWritableSigner(payerKey),
      createRead(operatorKey),
      createRead(newOperatorAuthorityKey),
      createWrite(newOperatorKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction createOperator(final AccountMeta invokedJupStableProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey operatorAuthorityKey,
                                           final PublicKey payerKey,
                                           final PublicKey operatorKey,
                                           final PublicKey newOperatorAuthorityKey,
                                           final PublicKey newOperatorKey,
                                           final OperatorRole role) {
    final var keys = createOperatorKeys(
      solanaAccounts,
      operatorAuthorityKey,
      payerKey,
      operatorKey,
      newOperatorAuthorityKey,
      newOperatorKey
    );
    return createOperator(invokedJupStableProgramMeta, keys, role);
  }

  public static Instruction createOperator(final AccountMeta invokedJupStableProgramMeta,
                                           final List<AccountMeta> keys,
                                           final OperatorRole role) {
    final byte[] _data = new byte[8 + role.l()];
    int i = CREATE_OPERATOR_DISCRIMINATOR.write(_data, 0);
    role.write(_data, i);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record CreateOperatorIxData(Discriminator discriminator, OperatorRole role) implements SerDe {  

    public static CreateOperatorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int ROLE_OFFSET = 8;

    public static CreateOperatorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var role = OperatorRole.read(_data, i);
      return new CreateOperatorIxData(discriminator, role);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += role.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_VAULT_DISCRIMINATOR = toDiscriminator(29, 237, 247, 208, 193, 82, 54, 135);

  public static List<AccountMeta> createVaultKeys(final SolanaAccounts solanaAccounts,
                                                  final PublicKey operatorAuthorityKey,
                                                  final PublicKey operatorKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey configKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey vaultKey,
                                                  final PublicKey tokenAccountKey,
                                                  final PublicKey tokenProgramKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWritableSigner(payerKey),
      createRead(mintKey),
      createRead(configKey),
      createRead(authorityKey),
      createWrite(vaultKey),
      createWrite(tokenAccountKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.associatedTokenAccountProgram()),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction createVault(final AccountMeta invokedJupStableProgramMeta,
                                        final SolanaAccounts solanaAccounts,
                                        final PublicKey operatorAuthorityKey,
                                        final PublicKey operatorKey,
                                        final PublicKey payerKey,
                                        final PublicKey mintKey,
                                        final PublicKey configKey,
                                        final PublicKey authorityKey,
                                        final PublicKey vaultKey,
                                        final PublicKey tokenAccountKey,
                                        final PublicKey tokenProgramKey) {
    final var keys = createVaultKeys(
      solanaAccounts,
      operatorAuthorityKey,
      operatorKey,
      payerKey,
      mintKey,
      configKey,
      authorityKey,
      vaultKey,
      tokenAccountKey,
      tokenProgramKey
    );
    return createVault(invokedJupStableProgramMeta, keys);
  }

  public static Instruction createVault(final AccountMeta invokedJupStableProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, CREATE_VAULT_DISCRIMINATOR);
  }

  public static final Discriminator DELETE_BENEFACTOR_DISCRIMINATOR = toDiscriminator(216, 227, 84, 147, 79, 177, 152, 147);

  public static List<AccountMeta> deleteBenefactorKeys(final PublicKey operatorAuthorityKey,
                                                       final PublicKey operatorKey,
                                                       final PublicKey receiverKey,
                                                       final PublicKey benefactorKey) {
    return List.of(
      createWritableSigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWrite(receiverKey),
      createWrite(benefactorKey)
    );
  }

  public static Instruction deleteBenefactor(final AccountMeta invokedJupStableProgramMeta,
                                             final PublicKey operatorAuthorityKey,
                                             final PublicKey operatorKey,
                                             final PublicKey receiverKey,
                                             final PublicKey benefactorKey) {
    final var keys = deleteBenefactorKeys(
      operatorAuthorityKey,
      operatorKey,
      receiverKey,
      benefactorKey
    );
    return deleteBenefactor(invokedJupStableProgramMeta, keys);
  }

  public static Instruction deleteBenefactor(final AccountMeta invokedJupStableProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, DELETE_BENEFACTOR_DISCRIMINATOR);
  }

  public static final Discriminator DELETE_OPERATOR_DISCRIMINATOR = toDiscriminator(208, 84, 168, 116, 138, 201, 98, 16);

  public static List<AccountMeta> deleteOperatorKeys(final PublicKey operatorAuthorityKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey operatorKey,
                                                     final PublicKey deletedOperatorKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createWritableSigner(payerKey),
      createRead(operatorKey),
      createWrite(deletedOperatorKey)
    );
  }

  public static Instruction deleteOperator(final AccountMeta invokedJupStableProgramMeta,
                                           final PublicKey operatorAuthorityKey,
                                           final PublicKey payerKey,
                                           final PublicKey operatorKey,
                                           final PublicKey deletedOperatorKey) {
    final var keys = deleteOperatorKeys(
      operatorAuthorityKey,
      payerKey,
      operatorKey,
      deletedOperatorKey
    );
    return deleteOperator(invokedJupStableProgramMeta, keys);
  }

  public static Instruction deleteOperator(final AccountMeta invokedJupStableProgramMeta,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, DELETE_OPERATOR_DISCRIMINATOR);
  }

  public static final Discriminator INIT_DISCRIMINATOR = toDiscriminator(220, 59, 207, 236, 108, 250, 47, 100);

  public static List<AccountMeta> initKeys(final SolanaAccounts solanaAccounts,
                                           final PublicKey payerKey,
                                           final PublicKey upgradeAuthorityKey,
                                           final PublicKey operatorKey,
                                           final PublicKey configKey,
                                           final PublicKey authorityKey,
                                           final PublicKey mintKey,
                                           final PublicKey metadataKey,
                                           final PublicKey programDataKey,
                                           final PublicKey programKey,
                                           final PublicKey metadataProgramKey,
                                           final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(upgradeAuthorityKey),
      createWrite(operatorKey),
      createWrite(configKey),
      createWrite(authorityKey),
      createWritableSigner(mintKey),
      createWrite(metadataKey),
      createRead(programDataKey),
      createRead(programKey),
      createRead(metadataProgramKey),
      createRead(tokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  public static Instruction init(final AccountMeta invokedJupStableProgramMeta,
                                 final SolanaAccounts solanaAccounts,
                                 final PublicKey payerKey,
                                 final PublicKey upgradeAuthorityKey,
                                 final PublicKey operatorKey,
                                 final PublicKey configKey,
                                 final PublicKey authorityKey,
                                 final PublicKey mintKey,
                                 final PublicKey metadataKey,
                                 final PublicKey programDataKey,
                                 final PublicKey programKey,
                                 final PublicKey metadataProgramKey,
                                 final PublicKey tokenProgramKey,
                                 final int decimals,
                                 final String name,
                                 final String symbol,
                                 final String uri) {
    final var keys = initKeys(
      solanaAccounts,
      payerKey,
      upgradeAuthorityKey,
      operatorKey,
      configKey,
      authorityKey,
      mintKey,
      metadataKey,
      programDataKey,
      programKey,
      metadataProgramKey,
      tokenProgramKey
    );
    return init(
      invokedJupStableProgramMeta,
      keys,
      decimals,
      name,
      symbol,
      uri
    );
  }

  public static Instruction init(final AccountMeta invokedJupStableProgramMeta,
                                 final List<AccountMeta> keys,
                                 final int decimals,
                                 final String name,
                                 final String symbol,
                                 final String uri) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _symbol = symbol.getBytes(UTF_8);
    final byte[] _uri = uri.getBytes(UTF_8);
    final byte[] _data = new byte[21 + _name.length + _symbol.length + _uri.length];
    int i = INIT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) decimals;
    ++i;
    i += SerDeUtil.writeVector(4, _name, _data, i);
    i += SerDeUtil.writeVector(4, _symbol, _data, i);
    SerDeUtil.writeVector(4, _uri, _data, i);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record InitIxData(Discriminator discriminator,
                           int decimals,
                           String name, byte[] _name,
                           String symbol, byte[] _symbol,
                           String uri, byte[] _uri) implements SerDe {  

    public static InitIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DECIMALS_OFFSET = 8;
    public static final int NAME_OFFSET = 9;

    public static InitIxData createRecord(final Discriminator discriminator,
                                          final int decimals,
                                          final String name,
                                          final String symbol,
                                          final String uri) {
      return new InitIxData(discriminator,
                            decimals,
                            name, name == null ? null : name.getBytes(UTF_8),
                            symbol, symbol == null ? null : symbol.getBytes(UTF_8),
                            uri, uri == null ? null : uri.getBytes(UTF_8));
    }

    public static InitIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var decimals = _data[i] & 0xFF;
      ++i;
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
      return new InitIxData(discriminator,
                            decimals,
                            name, name == null ? null : name.getBytes(UTF_8),
                            symbol, symbol == null ? null : symbol.getBytes(UTF_8),
                            uri, uri == null ? null : uri.getBytes(UTF_8));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) decimals;
      ++i;
      i += SerDeUtil.writeVector(4, _name, _data, i);
      i += SerDeUtil.writeVector(4, _symbol, _data, i);
      i += SerDeUtil.writeVector(4, _uri, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 1 + _name.length + _symbol.length + _uri.length;
    }
  }

  public static final Discriminator MANAGE_BENEFACTOR_DISCRIMINATOR = toDiscriminator(22, 231, 128, 62, 115, 219, 149, 14);

  public static List<AccountMeta> manageBenefactorKeys(final PublicKey operatorAuthorityKey,
                                                       final PublicKey operatorKey,
                                                       final PublicKey benefactorKey) {
    return List.of(
      createWritableSigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWrite(benefactorKey)
    );
  }

  public static Instruction manageBenefactor(final AccountMeta invokedJupStableProgramMeta,
                                             final PublicKey operatorAuthorityKey,
                                             final PublicKey operatorKey,
                                             final PublicKey benefactorKey,
                                             final BenefactorManagementAction action) {
    final var keys = manageBenefactorKeys(
      operatorAuthorityKey,
      operatorKey,
      benefactorKey
    );
    return manageBenefactor(invokedJupStableProgramMeta, keys, action);
  }

  public static Instruction manageBenefactor(final AccountMeta invokedJupStableProgramMeta,
                                             final List<AccountMeta> keys,
                                             final BenefactorManagementAction action) {
    final byte[] _data = new byte[8 + action.l()];
    int i = MANAGE_BENEFACTOR_DISCRIMINATOR.write(_data, 0);
    action.write(_data, i);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record ManageBenefactorIxData(Discriminator discriminator, BenefactorManagementAction action) implements SerDe {  

    public static ManageBenefactorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ACTION_OFFSET = 8;

    public static ManageBenefactorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var action = BenefactorManagementAction.read(_data, i);
      return new ManageBenefactorIxData(discriminator, action);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += action.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + action.l();
    }
  }

  public static final Discriminator MANAGE_CONFIG_DISCRIMINATOR = toDiscriminator(119, 51, 144, 55, 24, 242, 232, 231);

  public static List<AccountMeta> manageConfigKeys(final PublicKey operatorAuthorityKey,
                                                   final PublicKey operatorKey,
                                                   final PublicKey configKey) {
    return List.of(
      createWritableSigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWrite(configKey)
    );
  }

  public static Instruction manageConfig(final AccountMeta invokedJupStableProgramMeta,
                                         final PublicKey operatorAuthorityKey,
                                         final PublicKey operatorKey,
                                         final PublicKey configKey,
                                         final ConfigManagementAction action) {
    final var keys = manageConfigKeys(
      operatorAuthorityKey,
      operatorKey,
      configKey
    );
    return manageConfig(invokedJupStableProgramMeta, keys, action);
  }

  public static Instruction manageConfig(final AccountMeta invokedJupStableProgramMeta,
                                         final List<AccountMeta> keys,
                                         final ConfigManagementAction action) {
    final byte[] _data = new byte[8 + action.l()];
    int i = MANAGE_CONFIG_DISCRIMINATOR.write(_data, 0);
    action.write(_data, i);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record ManageConfigIxData(Discriminator discriminator, ConfigManagementAction action) implements SerDe {  

    public static ManageConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ACTION_OFFSET = 8;

    public static ManageConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var action = ConfigManagementAction.read(_data, i);
      return new ManageConfigIxData(discriminator, action);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += action.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + action.l();
    }
  }

  public static final Discriminator MANAGE_OPERATOR_DISCRIMINATOR = toDiscriminator(82, 172, 106, 235, 147, 246, 96, 85);

  public static List<AccountMeta> manageOperatorKeys(final SolanaAccounts solanaAccounts,
                                                     final PublicKey operatorAuthorityKey,
                                                     final PublicKey operatorKey,
                                                     final PublicKey managedOperatorKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWrite(managedOperatorKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction manageOperator(final AccountMeta invokedJupStableProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey operatorAuthorityKey,
                                           final PublicKey operatorKey,
                                           final PublicKey managedOperatorKey,
                                           final OperatorManagementAction action) {
    final var keys = manageOperatorKeys(
      solanaAccounts,
      operatorAuthorityKey,
      operatorKey,
      managedOperatorKey
    );
    return manageOperator(invokedJupStableProgramMeta, keys, action);
  }

  public static Instruction manageOperator(final AccountMeta invokedJupStableProgramMeta,
                                           final List<AccountMeta> keys,
                                           final OperatorManagementAction action) {
    final byte[] _data = new byte[8 + action.l()];
    int i = MANAGE_OPERATOR_DISCRIMINATOR.write(_data, 0);
    action.write(_data, i);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record ManageOperatorIxData(Discriminator discriminator, OperatorManagementAction action) implements SerDe {  

    public static ManageOperatorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ACTION_OFFSET = 8;

    public static ManageOperatorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var action = OperatorManagementAction.read(_data, i);
      return new ManageOperatorIxData(discriminator, action);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += action.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + action.l();
    }
  }

  public static final Discriminator MANAGE_VAULT_DISCRIMINATOR = toDiscriminator(165, 7, 106, 242, 73, 193, 195, 128);

  public static List<AccountMeta> manageVaultKeys(final PublicKey operatorAuthorityKey,
                                                  final PublicKey operatorKey,
                                                  final PublicKey vaultKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createRead(operatorKey),
      createWrite(vaultKey)
    );
  }

  public static Instruction manageVault(final AccountMeta invokedJupStableProgramMeta,
                                        final PublicKey operatorAuthorityKey,
                                        final PublicKey operatorKey,
                                        final PublicKey vaultKey,
                                        final VaultManagementAction action) {
    final var keys = manageVaultKeys(
      operatorAuthorityKey,
      operatorKey,
      vaultKey
    );
    return manageVault(invokedJupStableProgramMeta, keys, action);
  }

  public static Instruction manageVault(final AccountMeta invokedJupStableProgramMeta,
                                        final List<AccountMeta> keys,
                                        final VaultManagementAction action) {
    final byte[] _data = new byte[8 + action.l()];
    int i = MANAGE_VAULT_DISCRIMINATOR.write(_data, 0);
    action.write(_data, i);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record ManageVaultIxData(Discriminator discriminator, VaultManagementAction action) implements SerDe {  

    public static ManageVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ACTION_OFFSET = 8;

    public static ManageVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var action = VaultManagementAction.read(_data, i);
      return new ManageVaultIxData(discriminator, action);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += action.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + action.l();
    }
  }

  public static final Discriminator MINT_DISCRIMINATOR = toDiscriminator(51, 57, 225, 47, 182, 146, 137, 166);

  public static List<AccountMeta> mintKeys(final SolanaAccounts solanaAccounts,
                                           final PublicKey userKey,
                                           final PublicKey userCollateralTokenAccountKey,
                                           final PublicKey userLpTokenAccountKey,
                                           final PublicKey configKey,
                                           final PublicKey authorityKey,
                                           final PublicKey lpMintKey,
                                           final PublicKey vaultKey,
                                           final PublicKey vaultMintKey,
                                           final PublicKey custodianKey,
                                           final PublicKey custodianTokenAccountKey,
                                           final PublicKey benefactorKey,
                                           final PublicKey lpTokenProgramKey,
                                           final PublicKey vaultTokenProgramKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey) {
    return List.of(
      createWritableSigner(userKey),
      createWrite(userCollateralTokenAccountKey),
      createWrite(userLpTokenAccountKey),
      createWrite(configKey),
      createRead(authorityKey),
      createWrite(lpMintKey),
      createWrite(vaultKey),
      createRead(vaultMintKey),
      createRead(custodianKey),
      createWrite(custodianTokenAccountKey),
      createWrite(benefactorKey),
      createRead(lpTokenProgramKey),
      createRead(vaultTokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction mint(final AccountMeta invokedJupStableProgramMeta,
                                 final SolanaAccounts solanaAccounts,
                                 final PublicKey userKey,
                                 final PublicKey userCollateralTokenAccountKey,
                                 final PublicKey userLpTokenAccountKey,
                                 final PublicKey configKey,
                                 final PublicKey authorityKey,
                                 final PublicKey lpMintKey,
                                 final PublicKey vaultKey,
                                 final PublicKey vaultMintKey,
                                 final PublicKey custodianKey,
                                 final PublicKey custodianTokenAccountKey,
                                 final PublicKey benefactorKey,
                                 final PublicKey lpTokenProgramKey,
                                 final PublicKey vaultTokenProgramKey,
                                 final PublicKey eventAuthorityKey,
                                 final PublicKey programKey,
                                 final long amount,
                                 final long minAmountOut) {
    final var keys = mintKeys(
      solanaAccounts,
      userKey,
      userCollateralTokenAccountKey,
      userLpTokenAccountKey,
      configKey,
      authorityKey,
      lpMintKey,
      vaultKey,
      vaultMintKey,
      custodianKey,
      custodianTokenAccountKey,
      benefactorKey,
      lpTokenProgramKey,
      vaultTokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return mint(invokedJupStableProgramMeta, keys, amount, minAmountOut);
  }

  public static Instruction mint(final AccountMeta invokedJupStableProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long amount,
                                 final long minAmountOut) {
    final byte[] _data = new byte[24];
    int i = MINT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, minAmountOut);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record MintIxData(Discriminator discriminator, long amount, long minAmountOut) implements SerDe {  

    public static MintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static final int AMOUNT_OFFSET = 8;
    public static final int MIN_AMOUNT_OUT_OFFSET = 16;

    public static MintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var minAmountOut = getInt64LE(_data, i);
      return new MintIxData(discriminator, amount, minAmountOut);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
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

  public static final Discriminator REDEEM_DISCRIMINATOR = toDiscriminator(184, 12, 86, 149, 70, 196, 97, 225);

  public static List<AccountMeta> redeemKeys(final SolanaAccounts solanaAccounts,
                                             final PublicKey userKey,
                                             final PublicKey userLpTokenAccountKey,
                                             final PublicKey userCollateralTokenAccountKey,
                                             final PublicKey configKey,
                                             final PublicKey authorityKey,
                                             final PublicKey lpMintKey,
                                             final PublicKey vaultKey,
                                             final PublicKey vaultTokenAccountKey,
                                             final PublicKey vaultMintKey,
                                             final PublicKey benefactorKey,
                                             final PublicKey lpTokenProgramKey,
                                             final PublicKey vaultTokenProgramKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey programKey) {
    return List.of(
      createWritableSigner(userKey),
      createWrite(userLpTokenAccountKey),
      createWrite(userCollateralTokenAccountKey),
      createWrite(configKey),
      createRead(authorityKey),
      createWrite(lpMintKey),
      createWrite(vaultKey),
      createWrite(vaultTokenAccountKey),
      createRead(vaultMintKey),
      createWrite(benefactorKey),
      createRead(lpTokenProgramKey),
      createRead(vaultTokenProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction redeem(final AccountMeta invokedJupStableProgramMeta,
                                   final SolanaAccounts solanaAccounts,
                                   final PublicKey userKey,
                                   final PublicKey userLpTokenAccountKey,
                                   final PublicKey userCollateralTokenAccountKey,
                                   final PublicKey configKey,
                                   final PublicKey authorityKey,
                                   final PublicKey lpMintKey,
                                   final PublicKey vaultKey,
                                   final PublicKey vaultTokenAccountKey,
                                   final PublicKey vaultMintKey,
                                   final PublicKey benefactorKey,
                                   final PublicKey lpTokenProgramKey,
                                   final PublicKey vaultTokenProgramKey,
                                   final PublicKey eventAuthorityKey,
                                   final PublicKey programKey,
                                   final long amount,
                                   final long minAmountOut) {
    final var keys = redeemKeys(
      solanaAccounts,
      userKey,
      userLpTokenAccountKey,
      userCollateralTokenAccountKey,
      configKey,
      authorityKey,
      lpMintKey,
      vaultKey,
      vaultTokenAccountKey,
      vaultMintKey,
      benefactorKey,
      lpTokenProgramKey,
      vaultTokenProgramKey,
      eventAuthorityKey,
      programKey
    );
    return redeem(invokedJupStableProgramMeta, keys, amount, minAmountOut);
  }

  public static Instruction redeem(final AccountMeta invokedJupStableProgramMeta,
                                   final List<AccountMeta> keys,
                                   final long amount,
                                   final long minAmountOut) {
    final byte[] _data = new byte[24];
    int i = REDEEM_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, minAmountOut);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record RedeemIxData(Discriminator discriminator, long amount, long minAmountOut) implements SerDe {  

    public static RedeemIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static final int AMOUNT_OFFSET = 8;
    public static final int MIN_AMOUNT_OUT_OFFSET = 16;

    public static RedeemIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var minAmountOut = getInt64LE(_data, i);
      return new RedeemIxData(discriminator, amount, minAmountOut);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
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

  public static final Discriminator WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 18, 70, 156, 148, 109, 161, 34);

  public static List<AccountMeta> withdrawKeys(final PublicKey operatorAuthorityKey,
                                               final PublicKey operatorKey,
                                               final PublicKey custodianKey,
                                               final PublicKey custodianTokenAccountKey,
                                               final PublicKey configKey,
                                               final PublicKey authorityKey,
                                               final PublicKey vaultKey,
                                               final PublicKey vaultTokenAccountKey,
                                               final PublicKey vaultMintKey,
                                               final PublicKey tokenProgramKey) {
    return List.of(
      createReadOnlySigner(operatorAuthorityKey),
      createRead(operatorKey),
      createRead(custodianKey),
      createWrite(custodianTokenAccountKey),
      createRead(configKey),
      createRead(authorityKey),
      createWrite(vaultKey),
      createWrite(vaultTokenAccountKey),
      createRead(vaultMintKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction withdraw(final AccountMeta invokedJupStableProgramMeta,
                                     final PublicKey operatorAuthorityKey,
                                     final PublicKey operatorKey,
                                     final PublicKey custodianKey,
                                     final PublicKey custodianTokenAccountKey,
                                     final PublicKey configKey,
                                     final PublicKey authorityKey,
                                     final PublicKey vaultKey,
                                     final PublicKey vaultTokenAccountKey,
                                     final PublicKey vaultMintKey,
                                     final PublicKey tokenProgramKey,
                                     final long amount) {
    final var keys = withdrawKeys(
      operatorAuthorityKey,
      operatorKey,
      custodianKey,
      custodianTokenAccountKey,
      configKey,
      authorityKey,
      vaultKey,
      vaultTokenAccountKey,
      vaultMintKey,
      tokenProgramKey
    );
    return withdraw(invokedJupStableProgramMeta, keys, amount);
  }

  public static Instruction withdraw(final AccountMeta invokedJupStableProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long amount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedJupStableProgramMeta, keys, _data);
  }

  public record WithdrawIxData(Discriminator discriminator, long amount) implements SerDe {  

    public static WithdrawIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int AMOUNT_OFFSET = 8;

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

  private JupStableProgram() {
  }
}
