package software.sava.idl.clients.squads.v4.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.squads.v4.gen.types.BatchAddTransactionArgs;
import software.sava.idl.clients.squads.v4.gen.types.BatchCreateArgs;
import software.sava.idl.clients.squads.v4.gen.types.ConfigTransactionCreateArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigAddMemberArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigAddSpendingLimitArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigChangeThresholdArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigCreateArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigCreateArgsV2;
import software.sava.idl.clients.squads.v4.gen.types.MultisigRemoveMemberArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigRemoveSpendingLimitArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigSetConfigAuthorityArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigSetRentCollectorArgs;
import software.sava.idl.clients.squads.v4.gen.types.MultisigSetTimeLockArgs;
import software.sava.idl.clients.squads.v4.gen.types.ProgramConfigInitArgs;
import software.sava.idl.clients.squads.v4.gen.types.ProgramConfigSetAuthorityArgs;
import software.sava.idl.clients.squads.v4.gen.types.ProgramConfigSetMultisigCreationFeeArgs;
import software.sava.idl.clients.squads.v4.gen.types.ProgramConfigSetTreasuryArgs;
import software.sava.idl.clients.squads.v4.gen.types.ProposalCreateArgs;
import software.sava.idl.clients.squads.v4.gen.types.ProposalVoteArgs;
import software.sava.idl.clients.squads.v4.gen.types.SpendingLimitUseArgs;
import software.sava.idl.clients.squads.v4.gen.types.VaultTransactionCreateArgs;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class SquadsMultisigProgram {

  public static final Discriminator PROGRAM_CONFIG_INIT_DISCRIMINATOR = toDiscriminator(184, 188, 198, 195, 205, 124, 117, 216);

  /// Initialize the program config.
  ///
  /// @param initializerKey The hard-coded account that is used to initialize the program config once.
  public static List<AccountMeta> programConfigInitKeys(final PublicKey programConfigKey,
                                                        final PublicKey initializerKey,
                                                        final PublicKey systemProgramKey) {
    return List.of(
      createWrite(programConfigKey),
      createWritableSigner(initializerKey),
      createRead(systemProgramKey)
    );
  }

  /// Initialize the program config.
  ///
  /// @param initializerKey The hard-coded account that is used to initialize the program config once.
  public static Instruction programConfigInit(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                              final PublicKey programConfigKey,
                                              final PublicKey initializerKey,
                                              final PublicKey systemProgramKey,
                                              final ProgramConfigInitArgs args) {
    final var keys = programConfigInitKeys(
      programConfigKey,
      initializerKey,
      systemProgramKey
    );
    return programConfigInit(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Initialize the program config.
  ///
  public static Instruction programConfigInit(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                              final List<AccountMeta> keys,
                                              final ProgramConfigInitArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROGRAM_CONFIG_INIT_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProgramConfigInitIxData(Discriminator discriminator, ProgramConfigInitArgs args) implements SerDe {  

    public static ProgramConfigInitIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 80;

    public static final int ARGS_OFFSET = 8;

    public static ProgramConfigInitIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProgramConfigInitArgs.read(_data, i);
      return new ProgramConfigInitIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PROGRAM_CONFIG_SET_AUTHORITY_DISCRIMINATOR = toDiscriminator(238, 242, 36, 181, 32, 143, 216, 75);

  /// Set the `authority` parameter of the program config.
  ///
  public static List<AccountMeta> programConfigSetAuthorityKeys(final PublicKey programConfigKey,
                                                                final PublicKey authorityKey) {
    return List.of(
      createWrite(programConfigKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Set the `authority` parameter of the program config.
  ///
  public static Instruction programConfigSetAuthority(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                      final PublicKey programConfigKey,
                                                      final PublicKey authorityKey,
                                                      final ProgramConfigSetAuthorityArgs args) {
    final var keys = programConfigSetAuthorityKeys(
      programConfigKey,
      authorityKey
    );
    return programConfigSetAuthority(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the `authority` parameter of the program config.
  ///
  public static Instruction programConfigSetAuthority(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final ProgramConfigSetAuthorityArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROGRAM_CONFIG_SET_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProgramConfigSetAuthorityIxData(Discriminator discriminator, ProgramConfigSetAuthorityArgs args) implements SerDe {  

    public static ProgramConfigSetAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static final int ARGS_OFFSET = 8;

    public static ProgramConfigSetAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProgramConfigSetAuthorityArgs.read(_data, i);
      return new ProgramConfigSetAuthorityIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PROGRAM_CONFIG_SET_MULTISIG_CREATION_FEE_DISCRIMINATOR = toDiscriminator(101, 160, 249, 63, 154, 215, 153, 13);

  /// Set the `multisig_creation_fee` parameter of the program config.
  ///
  public static List<AccountMeta> programConfigSetMultisigCreationFeeKeys(final PublicKey programConfigKey,
                                                                          final PublicKey authorityKey) {
    return List.of(
      createWrite(programConfigKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Set the `multisig_creation_fee` parameter of the program config.
  ///
  public static Instruction programConfigSetMultisigCreationFee(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                                final PublicKey programConfigKey,
                                                                final PublicKey authorityKey,
                                                                final ProgramConfigSetMultisigCreationFeeArgs args) {
    final var keys = programConfigSetMultisigCreationFeeKeys(
      programConfigKey,
      authorityKey
    );
    return programConfigSetMultisigCreationFee(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the `multisig_creation_fee` parameter of the program config.
  ///
  public static Instruction programConfigSetMultisigCreationFee(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                                final List<AccountMeta> keys,
                                                                final ProgramConfigSetMultisigCreationFeeArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROGRAM_CONFIG_SET_MULTISIG_CREATION_FEE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProgramConfigSetMultisigCreationFeeIxData(Discriminator discriminator, ProgramConfigSetMultisigCreationFeeArgs args) implements SerDe {  

    public static ProgramConfigSetMultisigCreationFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static final int ARGS_OFFSET = 8;

    public static ProgramConfigSetMultisigCreationFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProgramConfigSetMultisigCreationFeeArgs.read(_data, i);
      return new ProgramConfigSetMultisigCreationFeeIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PROGRAM_CONFIG_SET_TREASURY_DISCRIMINATOR = toDiscriminator(111, 46, 243, 117, 144, 188, 162, 107);

  /// Set the `treasury` parameter of the program config.
  ///
  public static List<AccountMeta> programConfigSetTreasuryKeys(final PublicKey programConfigKey,
                                                               final PublicKey authorityKey) {
    return List.of(
      createWrite(programConfigKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Set the `treasury` parameter of the program config.
  ///
  public static Instruction programConfigSetTreasury(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final PublicKey programConfigKey,
                                                     final PublicKey authorityKey,
                                                     final ProgramConfigSetTreasuryArgs args) {
    final var keys = programConfigSetTreasuryKeys(
      programConfigKey,
      authorityKey
    );
    return programConfigSetTreasury(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the `treasury` parameter of the program config.
  ///
  public static Instruction programConfigSetTreasury(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final ProgramConfigSetTreasuryArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROGRAM_CONFIG_SET_TREASURY_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProgramConfigSetTreasuryIxData(Discriminator discriminator, ProgramConfigSetTreasuryArgs args) implements SerDe {  

    public static ProgramConfigSetTreasuryIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static final int ARGS_OFFSET = 8;

    public static ProgramConfigSetTreasuryIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProgramConfigSetTreasuryArgs.read(_data, i);
      return new ProgramConfigSetTreasuryIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MULTISIG_CREATE_DISCRIMINATOR = toDiscriminator(122, 77, 80, 159, 84, 88, 90, 197);

  /// Create a multisig.
  ///
  /// @param createKey An ephemeral signer that is used as a seed for the Multisig PDA.
  ///                  Must be a signer to prevent front-running attack by someone else but the original creator.
  /// @param creatorKey The creator of the multisig.
  public static List<AccountMeta> multisigCreateKeys(final PublicKey multisigKey,
                                                     final PublicKey createKey,
                                                     final PublicKey creatorKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(createKey),
      createWritableSigner(creatorKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a multisig.
  ///
  /// @param createKey An ephemeral signer that is used as a seed for the Multisig PDA.
  ///                  Must be a signer to prevent front-running attack by someone else but the original creator.
  /// @param creatorKey The creator of the multisig.
  public static Instruction multisigCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final PublicKey multisigKey,
                                           final PublicKey createKey,
                                           final PublicKey creatorKey,
                                           final PublicKey systemProgramKey,
                                           final MultisigCreateArgs args) {
    final var keys = multisigCreateKeys(
      multisigKey,
      createKey,
      creatorKey,
      systemProgramKey
    );
    return multisigCreate(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a multisig.
  ///
  public static Instruction multisigCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final List<AccountMeta> keys,
                                           final MultisigCreateArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_CREATE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigCreateIxData(Discriminator discriminator, MultisigCreateArgs args) implements SerDe {  

    public static MultisigCreateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigCreateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigCreateArgs.read(_data, i);
      return new MultisigCreateIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_CREATE_V_2_DISCRIMINATOR = toDiscriminator(50, 221, 199, 93, 40, 245, 139, 233);

  /// Create a multisig.
  ///
  /// @param programConfigKey Global program config account.
  /// @param treasuryKey The treasury where the creation fee is transferred to.
  /// @param createKey An ephemeral signer that is used as a seed for the Multisig PDA.
  ///                  Must be a signer to prevent front-running attack by someone else but the original creator.
  /// @param creatorKey The creator of the multisig.
  public static List<AccountMeta> multisigCreateV2Keys(final PublicKey programConfigKey,
                                                       final PublicKey treasuryKey,
                                                       final PublicKey multisigKey,
                                                       final PublicKey createKey,
                                                       final PublicKey creatorKey,
                                                       final PublicKey systemProgramKey) {
    return List.of(
      createRead(programConfigKey),
      createWrite(treasuryKey),
      createWrite(multisigKey),
      createReadOnlySigner(createKey),
      createWritableSigner(creatorKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a multisig.
  ///
  /// @param programConfigKey Global program config account.
  /// @param treasuryKey The treasury where the creation fee is transferred to.
  /// @param createKey An ephemeral signer that is used as a seed for the Multisig PDA.
  ///                  Must be a signer to prevent front-running attack by someone else but the original creator.
  /// @param creatorKey The creator of the multisig.
  public static Instruction multisigCreateV2(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                             final PublicKey programConfigKey,
                                             final PublicKey treasuryKey,
                                             final PublicKey multisigKey,
                                             final PublicKey createKey,
                                             final PublicKey creatorKey,
                                             final PublicKey systemProgramKey,
                                             final MultisigCreateArgsV2 args) {
    final var keys = multisigCreateV2Keys(
      programConfigKey,
      treasuryKey,
      multisigKey,
      createKey,
      creatorKey,
      systemProgramKey
    );
    return multisigCreateV2(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a multisig.
  ///
  public static Instruction multisigCreateV2(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                             final List<AccountMeta> keys,
                                             final MultisigCreateArgsV2 args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_CREATE_V_2_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigCreateV2IxData(Discriminator discriminator, MultisigCreateArgsV2 args) implements SerDe {  

    public static MultisigCreateV2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigCreateV2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigCreateArgsV2.read(_data, i);
      return new MultisigCreateV2IxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_ADD_MEMBER_DISCRIMINATOR = toDiscriminator(1, 219, 215, 108, 184, 229, 214, 8);

  /// Add a new member to the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> multisigAddMemberKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                        final PublicKey multisigKey,
                                                        final PublicKey configAuthorityKey,
                                                        final PublicKey rentPayerKey,
                                                        final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Add a new member to the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction multisigAddMember(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                              final PublicKey multisigKey,
                                              final PublicKey configAuthorityKey,
                                              final PublicKey rentPayerKey,
                                              final PublicKey systemProgramKey,
                                              final MultisigAddMemberArgs args) {
    final var keys = multisigAddMemberKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      configAuthorityKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigAddMember(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Add a new member to the controlled multisig.
  ///
  public static Instruction multisigAddMember(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                              final List<AccountMeta> keys,
                                              final MultisigAddMemberArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_ADD_MEMBER_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigAddMemberIxData(Discriminator discriminator, MultisigAddMemberArgs args) implements SerDe {  

    public static MultisigAddMemberIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigAddMemberIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigAddMemberArgs.read(_data, i);
      return new MultisigAddMemberIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_REMOVE_MEMBER_DISCRIMINATOR = toDiscriminator(217, 117, 177, 210, 182, 145, 218, 72);

  /// Remove a member/key from the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> multisigRemoveMemberKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                           final PublicKey multisigKey,
                                                           final PublicKey configAuthorityKey,
                                                           final PublicKey rentPayerKey,
                                                           final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Remove a member/key from the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction multisigRemoveMember(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                 final PublicKey multisigKey,
                                                 final PublicKey configAuthorityKey,
                                                 final PublicKey rentPayerKey,
                                                 final PublicKey systemProgramKey,
                                                 final MultisigRemoveMemberArgs args) {
    final var keys = multisigRemoveMemberKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      configAuthorityKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigRemoveMember(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Remove a member/key from the controlled multisig.
  ///
  public static Instruction multisigRemoveMember(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final MultisigRemoveMemberArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_REMOVE_MEMBER_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigRemoveMemberIxData(Discriminator discriminator, MultisigRemoveMemberArgs args) implements SerDe {  

    public static MultisigRemoveMemberIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigRemoveMemberIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigRemoveMemberArgs.read(_data, i);
      return new MultisigRemoveMemberIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_SET_TIME_LOCK_DISCRIMINATOR = toDiscriminator(148, 154, 121, 77, 212, 254, 155, 72);

  /// Set the `time_lock` config parameter for the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> multisigSetTimeLockKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                          final PublicKey multisigKey,
                                                          final PublicKey configAuthorityKey,
                                                          final PublicKey rentPayerKey,
                                                          final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Set the `time_lock` config parameter for the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction multisigSetTimeLock(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                final PublicKey multisigKey,
                                                final PublicKey configAuthorityKey,
                                                final PublicKey rentPayerKey,
                                                final PublicKey systemProgramKey,
                                                final MultisigSetTimeLockArgs args) {
    final var keys = multisigSetTimeLockKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      configAuthorityKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigSetTimeLock(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the `time_lock` config parameter for the controlled multisig.
  ///
  public static Instruction multisigSetTimeLock(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                final List<AccountMeta> keys,
                                                final MultisigSetTimeLockArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_SET_TIME_LOCK_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigSetTimeLockIxData(Discriminator discriminator, MultisigSetTimeLockArgs args) implements SerDe {  

    public static MultisigSetTimeLockIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigSetTimeLockIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigSetTimeLockArgs.read(_data, i);
      return new MultisigSetTimeLockIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_CHANGE_THRESHOLD_DISCRIMINATOR = toDiscriminator(141, 42, 15, 126, 169, 92, 62, 181);

  /// Set the `threshold` config parameter for the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> multisigChangeThresholdKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                              final PublicKey multisigKey,
                                                              final PublicKey configAuthorityKey,
                                                              final PublicKey rentPayerKey,
                                                              final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Set the `threshold` config parameter for the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction multisigChangeThreshold(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final PublicKey multisigKey,
                                                    final PublicKey configAuthorityKey,
                                                    final PublicKey rentPayerKey,
                                                    final PublicKey systemProgramKey,
                                                    final MultisigChangeThresholdArgs args) {
    final var keys = multisigChangeThresholdKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      configAuthorityKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigChangeThreshold(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the `threshold` config parameter for the controlled multisig.
  ///
  public static Instruction multisigChangeThreshold(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final MultisigChangeThresholdArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_CHANGE_THRESHOLD_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigChangeThresholdIxData(Discriminator discriminator, MultisigChangeThresholdArgs args) implements SerDe {  

    public static MultisigChangeThresholdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigChangeThresholdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigChangeThresholdArgs.read(_data, i);
      return new MultisigChangeThresholdIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_SET_CONFIG_AUTHORITY_DISCRIMINATOR = toDiscriminator(143, 93, 199, 143, 92, 169, 193, 232);

  /// Set the multisig `config_authority`.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> multisigSetConfigAuthorityKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                                 final PublicKey multisigKey,
                                                                 final PublicKey configAuthorityKey,
                                                                 final PublicKey rentPayerKey,
                                                                 final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Set the multisig `config_authority`.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction multisigSetConfigAuthority(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                       final PublicKey multisigKey,
                                                       final PublicKey configAuthorityKey,
                                                       final PublicKey rentPayerKey,
                                                       final PublicKey systemProgramKey,
                                                       final MultisigSetConfigAuthorityArgs args) {
    final var keys = multisigSetConfigAuthorityKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      configAuthorityKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigSetConfigAuthority(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the multisig `config_authority`.
  ///
  public static Instruction multisigSetConfigAuthority(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final MultisigSetConfigAuthorityArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_SET_CONFIG_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigSetConfigAuthorityIxData(Discriminator discriminator, MultisigSetConfigAuthorityArgs args) implements SerDe {  

    public static MultisigSetConfigAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigSetConfigAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigSetConfigAuthorityArgs.read(_data, i);
      return new MultisigSetConfigAuthorityIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_SET_RENT_COLLECTOR_DISCRIMINATOR = toDiscriminator(48, 204, 65, 57, 210, 70, 156, 74);

  /// Set the multisig `rent_collector`.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> multisigSetRentCollectorKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                               final PublicKey multisigKey,
                                                               final PublicKey configAuthorityKey,
                                                               final PublicKey rentPayerKey,
                                                               final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Set the multisig `rent_collector`.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey The account that will be charged or credited in case the multisig account needs to reallocate space,
  ///                     for example when adding a new member or a spending limit.
  ///                     This is usually the same as `config_authority`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction multisigSetRentCollector(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final PublicKey multisigKey,
                                                     final PublicKey configAuthorityKey,
                                                     final PublicKey rentPayerKey,
                                                     final PublicKey systemProgramKey,
                                                     final MultisigSetRentCollectorArgs args) {
    final var keys = multisigSetRentCollectorKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      configAuthorityKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigSetRentCollector(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Set the multisig `rent_collector`.
  ///
  public static Instruction multisigSetRentCollector(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final MultisigSetRentCollectorArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_SET_RENT_COLLECTOR_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigSetRentCollectorIxData(Discriminator discriminator, MultisigSetRentCollectorArgs args) implements SerDe {  

    public static MultisigSetRentCollectorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigSetRentCollectorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigSetRentCollectorArgs.read(_data, i);
      return new MultisigSetRentCollectorIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_ADD_SPENDING_LIMIT_DISCRIMINATOR = toDiscriminator(11, 242, 159, 42, 86, 197, 89, 115);

  /// Create a new spending limit for the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey This is usually the same as `config_authority`, but can be a different account if needed.
  public static List<AccountMeta> multisigAddSpendingLimitKeys(final PublicKey multisigKey,
                                                               final PublicKey configAuthorityKey,
                                                               final PublicKey spendingLimitKey,
                                                               final PublicKey rentPayerKey,
                                                               final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      createWrite(spendingLimitKey),
      createWritableSigner(rentPayerKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a new spending limit for the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentPayerKey This is usually the same as `config_authority`, but can be a different account if needed.
  public static Instruction multisigAddSpendingLimit(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final PublicKey multisigKey,
                                                     final PublicKey configAuthorityKey,
                                                     final PublicKey spendingLimitKey,
                                                     final PublicKey rentPayerKey,
                                                     final PublicKey systemProgramKey,
                                                     final MultisigAddSpendingLimitArgs args) {
    final var keys = multisigAddSpendingLimitKeys(
      multisigKey,
      configAuthorityKey,
      spendingLimitKey,
      rentPayerKey,
      systemProgramKey
    );
    return multisigAddSpendingLimit(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a new spending limit for the controlled multisig.
  ///
  public static Instruction multisigAddSpendingLimit(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final MultisigAddSpendingLimitArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_ADD_SPENDING_LIMIT_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigAddSpendingLimitIxData(Discriminator discriminator, MultisigAddSpendingLimitArgs args) implements SerDe {  

    public static MultisigAddSpendingLimitIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigAddSpendingLimitIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigAddSpendingLimitArgs.read(_data, i);
      return new MultisigAddSpendingLimitIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator MULTISIG_REMOVE_SPENDING_LIMIT_DISCRIMINATOR = toDiscriminator(228, 198, 136, 111, 123, 4, 178, 113);

  /// Remove the spending limit from the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentCollectorKey This is usually the same as `config_authority`, but can be a different account if needed.
  public static List<AccountMeta> multisigRemoveSpendingLimitKeys(final PublicKey multisigKey,
                                                                  final PublicKey configAuthorityKey,
                                                                  final PublicKey spendingLimitKey,
                                                                  final PublicKey rentCollectorKey) {
    return List.of(
      createRead(multisigKey),
      createReadOnlySigner(configAuthorityKey),
      createWrite(spendingLimitKey),
      createWrite(rentCollectorKey)
    );
  }

  /// Remove the spending limit from the controlled multisig.
  ///
  /// @param configAuthorityKey Multisig `config_authority` that must authorize the configuration change.
  /// @param rentCollectorKey This is usually the same as `config_authority`, but can be a different account if needed.
  public static Instruction multisigRemoveSpendingLimit(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                        final PublicKey multisigKey,
                                                        final PublicKey configAuthorityKey,
                                                        final PublicKey spendingLimitKey,
                                                        final PublicKey rentCollectorKey,
                                                        final MultisigRemoveSpendingLimitArgs args) {
    final var keys = multisigRemoveSpendingLimitKeys(
      multisigKey,
      configAuthorityKey,
      spendingLimitKey,
      rentCollectorKey
    );
    return multisigRemoveSpendingLimit(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Remove the spending limit from the controlled multisig.
  ///
  public static Instruction multisigRemoveSpendingLimit(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final MultisigRemoveSpendingLimitArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = MULTISIG_REMOVE_SPENDING_LIMIT_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record MultisigRemoveSpendingLimitIxData(Discriminator discriminator, MultisigRemoveSpendingLimitArgs args) implements SerDe {  

    public static MultisigRemoveSpendingLimitIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static MultisigRemoveSpendingLimitIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = MultisigRemoveSpendingLimitArgs.read(_data, i);
      return new MultisigRemoveSpendingLimitIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator CONFIG_TRANSACTION_CREATE_DISCRIMINATOR = toDiscriminator(155, 236, 87, 228, 137, 75, 81, 39);

  /// Create a new config transaction.
  ///
  /// @param creatorKey The member of the multisig that is creating the transaction.
  /// @param rentPayerKey The payer for the transaction account rent.
  public static List<AccountMeta> configTransactionCreateKeys(final PublicKey multisigKey,
                                                              final PublicKey transactionKey,
                                                              final PublicKey creatorKey,
                                                              final PublicKey rentPayerKey,
                                                              final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createWrite(transactionKey),
      createReadOnlySigner(creatorKey),
      createWritableSigner(rentPayerKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a new config transaction.
  ///
  /// @param creatorKey The member of the multisig that is creating the transaction.
  /// @param rentPayerKey The payer for the transaction account rent.
  public static Instruction configTransactionCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final PublicKey multisigKey,
                                                    final PublicKey transactionKey,
                                                    final PublicKey creatorKey,
                                                    final PublicKey rentPayerKey,
                                                    final PublicKey systemProgramKey,
                                                    final ConfigTransactionCreateArgs args) {
    final var keys = configTransactionCreateKeys(
      multisigKey,
      transactionKey,
      creatorKey,
      rentPayerKey,
      systemProgramKey
    );
    return configTransactionCreate(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a new config transaction.
  ///
  public static Instruction configTransactionCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final ConfigTransactionCreateArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = CONFIG_TRANSACTION_CREATE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ConfigTransactionCreateIxData(Discriminator discriminator, ConfigTransactionCreateArgs args) implements SerDe {  

    public static ConfigTransactionCreateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static ConfigTransactionCreateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ConfigTransactionCreateArgs.read(_data, i);
      return new ConfigTransactionCreateIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator CONFIG_TRANSACTION_EXECUTE_DISCRIMINATOR = toDiscriminator(114, 146, 244, 189, 252, 140, 36, 40);

  /// Execute a config transaction.
  /// The transaction must be `Approved`.
  ///
  /// @param multisigKey The multisig account that owns the transaction.
  /// @param memberKey One of the multisig members with `Execute` permission.
  /// @param proposalKey The proposal account associated with the transaction.
  /// @param transactionKey The transaction to execute.
  /// @param rentPayerKey The account that will be charged/credited in case the config transaction causes space reallocation,
  ///                     for example when adding a new member, adding or removing a spending limit.
  ///                     This is usually the same as `member`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static List<AccountMeta> configTransactionExecuteKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                               final PublicKey multisigKey,
                                                               final PublicKey memberKey,
                                                               final PublicKey proposalKey,
                                                               final PublicKey transactionKey,
                                                               final PublicKey rentPayerKey,
                                                               final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createReadOnlySigner(memberKey),
      createWrite(proposalKey),
      createRead(transactionKey),
      rentPayerKey == null ? createWrite(invokedSquadsMultisigProgramProgramMeta.publicKey()) : createWritableSigner(rentPayerKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Execute a config transaction.
  /// The transaction must be `Approved`.
  ///
  /// @param multisigKey The multisig account that owns the transaction.
  /// @param memberKey One of the multisig members with `Execute` permission.
  /// @param proposalKey The proposal account associated with the transaction.
  /// @param transactionKey The transaction to execute.
  /// @param rentPayerKey The account that will be charged/credited in case the config transaction causes space reallocation,
  ///                     for example when adding a new member, adding or removing a spending limit.
  ///                     This is usually the same as `member`, but can be a different account if needed.
  /// @param systemProgramKey We might need it in case reallocation is needed.
  public static Instruction configTransactionExecute(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final PublicKey multisigKey,
                                                     final PublicKey memberKey,
                                                     final PublicKey proposalKey,
                                                     final PublicKey transactionKey,
                                                     final PublicKey rentPayerKey,
                                                     final PublicKey systemProgramKey) {
    final var keys = configTransactionExecuteKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      memberKey,
      proposalKey,
      transactionKey,
      rentPayerKey,
      systemProgramKey
    );
    return configTransactionExecute(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Execute a config transaction.
  /// The transaction must be `Approved`.
  ///
  public static Instruction configTransactionExecute(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, CONFIG_TRANSACTION_EXECUTE_DISCRIMINATOR);
  }

  public static final Discriminator VAULT_TRANSACTION_CREATE_DISCRIMINATOR = toDiscriminator(48, 250, 78, 168, 208, 226, 218, 211);

  /// Create a new vault transaction.
  ///
  /// @param creatorKey The member of the multisig that is creating the transaction.
  /// @param rentPayerKey The payer for the transaction account rent.
  public static List<AccountMeta> vaultTransactionCreateKeys(final PublicKey multisigKey,
                                                             final PublicKey transactionKey,
                                                             final PublicKey creatorKey,
                                                             final PublicKey rentPayerKey,
                                                             final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createWrite(transactionKey),
      createReadOnlySigner(creatorKey),
      createWritableSigner(rentPayerKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a new vault transaction.
  ///
  /// @param creatorKey The member of the multisig that is creating the transaction.
  /// @param rentPayerKey The payer for the transaction account rent.
  public static Instruction vaultTransactionCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                   final PublicKey multisigKey,
                                                   final PublicKey transactionKey,
                                                   final PublicKey creatorKey,
                                                   final PublicKey rentPayerKey,
                                                   final PublicKey systemProgramKey,
                                                   final VaultTransactionCreateArgs args) {
    final var keys = vaultTransactionCreateKeys(
      multisigKey,
      transactionKey,
      creatorKey,
      rentPayerKey,
      systemProgramKey
    );
    return vaultTransactionCreate(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a new vault transaction.
  ///
  public static Instruction vaultTransactionCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final VaultTransactionCreateArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = VAULT_TRANSACTION_CREATE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record VaultTransactionCreateIxData(Discriminator discriminator, VaultTransactionCreateArgs args) implements SerDe {  

    public static VaultTransactionCreateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static VaultTransactionCreateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = VaultTransactionCreateArgs.read(_data, i);
      return new VaultTransactionCreateIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator VAULT_TRANSACTION_EXECUTE_DISCRIMINATOR = toDiscriminator(194, 8, 161, 87, 153, 164, 25, 171);

  /// Execute a vault transaction.
  /// The transaction must be `Approved`.
  ///
  /// @param proposalKey The proposal account associated with the transaction.
  /// @param transactionKey The transaction to execute.
  public static List<AccountMeta> vaultTransactionExecuteKeys(final PublicKey multisigKey,
                                                              final PublicKey proposalKey,
                                                              final PublicKey transactionKey,
                                                              final PublicKey memberKey) {
    return List.of(
      createRead(multisigKey),
      createWrite(proposalKey),
      createRead(transactionKey),
      createReadOnlySigner(memberKey)
    );
  }

  /// Execute a vault transaction.
  /// The transaction must be `Approved`.
  ///
  /// @param proposalKey The proposal account associated with the transaction.
  /// @param transactionKey The transaction to execute.
  public static Instruction vaultTransactionExecute(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final PublicKey multisigKey,
                                                    final PublicKey proposalKey,
                                                    final PublicKey transactionKey,
                                                    final PublicKey memberKey) {
    final var keys = vaultTransactionExecuteKeys(
      multisigKey,
      proposalKey,
      transactionKey,
      memberKey
    );
    return vaultTransactionExecute(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Execute a vault transaction.
  /// The transaction must be `Approved`.
  ///
  public static Instruction vaultTransactionExecute(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, VAULT_TRANSACTION_EXECUTE_DISCRIMINATOR);
  }

  public static final Discriminator BATCH_CREATE_DISCRIMINATOR = toDiscriminator(194, 142, 141, 17, 55, 185, 20, 248);

  /// Create a new batch.
  ///
  /// @param creatorKey The member of the multisig that is creating the batch.
  /// @param rentPayerKey The payer for the batch account rent.
  public static List<AccountMeta> batchCreateKeys(final PublicKey multisigKey,
                                                  final PublicKey batchKey,
                                                  final PublicKey creatorKey,
                                                  final PublicKey rentPayerKey,
                                                  final PublicKey systemProgramKey) {
    return List.of(
      createWrite(multisigKey),
      createWrite(batchKey),
      createReadOnlySigner(creatorKey),
      createWritableSigner(rentPayerKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a new batch.
  ///
  /// @param creatorKey The member of the multisig that is creating the batch.
  /// @param rentPayerKey The payer for the batch account rent.
  public static Instruction batchCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                        final PublicKey multisigKey,
                                        final PublicKey batchKey,
                                        final PublicKey creatorKey,
                                        final PublicKey rentPayerKey,
                                        final PublicKey systemProgramKey,
                                        final BatchCreateArgs args) {
    final var keys = batchCreateKeys(
      multisigKey,
      batchKey,
      creatorKey,
      rentPayerKey,
      systemProgramKey
    );
    return batchCreate(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a new batch.
  ///
  public static Instruction batchCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                        final List<AccountMeta> keys,
                                        final BatchCreateArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = BATCH_CREATE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record BatchCreateIxData(Discriminator discriminator, BatchCreateArgs args) implements SerDe {  

    public static BatchCreateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static BatchCreateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = BatchCreateArgs.read(_data, i);
      return new BatchCreateIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator BATCH_ADD_TRANSACTION_DISCRIMINATOR = toDiscriminator(89, 100, 224, 18, 69, 70, 54, 76);

  /// Add a transaction to the batch.
  ///
  /// @param multisigKey Multisig account this batch belongs to.
  /// @param proposalKey The proposal account associated with the batch.
  /// @param transactionKey `VaultBatchTransaction` account to initialize and add to the `batch`.
  /// @param memberKey Member of the multisig.
  /// @param rentPayerKey The payer for the batch transaction account rent.
  public static List<AccountMeta> batchAddTransactionKeys(final PublicKey multisigKey,
                                                          final PublicKey proposalKey,
                                                          final PublicKey batchKey,
                                                          final PublicKey transactionKey,
                                                          final PublicKey memberKey,
                                                          final PublicKey rentPayerKey,
                                                          final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createRead(proposalKey),
      createWrite(batchKey),
      createWrite(transactionKey),
      createReadOnlySigner(memberKey),
      createWritableSigner(rentPayerKey),
      createRead(systemProgramKey)
    );
  }

  /// Add a transaction to the batch.
  ///
  /// @param multisigKey Multisig account this batch belongs to.
  /// @param proposalKey The proposal account associated with the batch.
  /// @param transactionKey `VaultBatchTransaction` account to initialize and add to the `batch`.
  /// @param memberKey Member of the multisig.
  /// @param rentPayerKey The payer for the batch transaction account rent.
  public static Instruction batchAddTransaction(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                final PublicKey multisigKey,
                                                final PublicKey proposalKey,
                                                final PublicKey batchKey,
                                                final PublicKey transactionKey,
                                                final PublicKey memberKey,
                                                final PublicKey rentPayerKey,
                                                final PublicKey systemProgramKey,
                                                final BatchAddTransactionArgs args) {
    final var keys = batchAddTransactionKeys(
      multisigKey,
      proposalKey,
      batchKey,
      transactionKey,
      memberKey,
      rentPayerKey,
      systemProgramKey
    );
    return batchAddTransaction(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Add a transaction to the batch.
  ///
  public static Instruction batchAddTransaction(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                final List<AccountMeta> keys,
                                                final BatchAddTransactionArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = BATCH_ADD_TRANSACTION_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record BatchAddTransactionIxData(Discriminator discriminator, BatchAddTransactionArgs args) implements SerDe {  

    public static BatchAddTransactionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static BatchAddTransactionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = BatchAddTransactionArgs.read(_data, i);
      return new BatchAddTransactionIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator BATCH_EXECUTE_TRANSACTION_DISCRIMINATOR = toDiscriminator(172, 44, 179, 152, 21, 127, 234, 180);

  /// Execute a transaction from the batch.
  ///
  /// @param multisigKey Multisig account this batch belongs to.
  /// @param memberKey Member of the multisig.
  /// @param proposalKey The proposal account associated with the batch.
  ///                    If `transaction` is the last in the batch, the `proposal` status will be set to `Executed`.
  /// @param transactionKey Batch transaction to execute.
  public static List<AccountMeta> batchExecuteTransactionKeys(final PublicKey multisigKey,
                                                              final PublicKey memberKey,
                                                              final PublicKey proposalKey,
                                                              final PublicKey batchKey,
                                                              final PublicKey transactionKey) {
    return List.of(
      createRead(multisigKey),
      createReadOnlySigner(memberKey),
      createWrite(proposalKey),
      createWrite(batchKey),
      createRead(transactionKey)
    );
  }

  /// Execute a transaction from the batch.
  ///
  /// @param multisigKey Multisig account this batch belongs to.
  /// @param memberKey Member of the multisig.
  /// @param proposalKey The proposal account associated with the batch.
  ///                    If `transaction` is the last in the batch, the `proposal` status will be set to `Executed`.
  /// @param transactionKey Batch transaction to execute.
  public static Instruction batchExecuteTransaction(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final PublicKey multisigKey,
                                                    final PublicKey memberKey,
                                                    final PublicKey proposalKey,
                                                    final PublicKey batchKey,
                                                    final PublicKey transactionKey) {
    final var keys = batchExecuteTransactionKeys(
      multisigKey,
      memberKey,
      proposalKey,
      batchKey,
      transactionKey
    );
    return batchExecuteTransaction(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Execute a transaction from the batch.
  ///
  public static Instruction batchExecuteTransaction(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                    final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, BATCH_EXECUTE_TRANSACTION_DISCRIMINATOR);
  }

  public static final Discriminator PROPOSAL_CREATE_DISCRIMINATOR = toDiscriminator(220, 60, 73, 224, 30, 108, 79, 159);

  /// Create a new multisig proposal.
  ///
  /// @param creatorKey The member of the multisig that is creating the proposal.
  /// @param rentPayerKey The payer for the proposal account rent.
  public static List<AccountMeta> proposalCreateKeys(final PublicKey multisigKey,
                                                     final PublicKey proposalKey,
                                                     final PublicKey creatorKey,
                                                     final PublicKey rentPayerKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createWrite(proposalKey),
      createReadOnlySigner(creatorKey),
      createWritableSigner(rentPayerKey),
      createRead(systemProgramKey)
    );
  }

  /// Create a new multisig proposal.
  ///
  /// @param creatorKey The member of the multisig that is creating the proposal.
  /// @param rentPayerKey The payer for the proposal account rent.
  public static Instruction proposalCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final PublicKey multisigKey,
                                           final PublicKey proposalKey,
                                           final PublicKey creatorKey,
                                           final PublicKey rentPayerKey,
                                           final PublicKey systemProgramKey,
                                           final ProposalCreateArgs args) {
    final var keys = proposalCreateKeys(
      multisigKey,
      proposalKey,
      creatorKey,
      rentPayerKey,
      systemProgramKey
    );
    return proposalCreate(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Create a new multisig proposal.
  ///
  public static Instruction proposalCreate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final List<AccountMeta> keys,
                                           final ProposalCreateArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROPOSAL_CREATE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProposalCreateIxData(Discriminator discriminator, ProposalCreateArgs args) implements SerDe {  

    public static ProposalCreateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 17;

    public static final int ARGS_OFFSET = 8;

    public static ProposalCreateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProposalCreateArgs.read(_data, i);
      return new ProposalCreateIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PROPOSAL_ACTIVATE_DISCRIMINATOR = toDiscriminator(11, 34, 92, 248, 154, 27, 51, 106);

  /// Update status of a multisig proposal from `Draft` to `Active`.
  ///
  public static List<AccountMeta> proposalActivateKeys(final PublicKey multisigKey,
                                                       final PublicKey memberKey,
                                                       final PublicKey proposalKey) {
    return List.of(
      createRead(multisigKey),
      createWritableSigner(memberKey),
      createWrite(proposalKey)
    );
  }

  /// Update status of a multisig proposal from `Draft` to `Active`.
  ///
  public static Instruction proposalActivate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                             final PublicKey multisigKey,
                                             final PublicKey memberKey,
                                             final PublicKey proposalKey) {
    final var keys = proposalActivateKeys(
      multisigKey,
      memberKey,
      proposalKey
    );
    return proposalActivate(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Update status of a multisig proposal from `Draft` to `Active`.
  ///
  public static Instruction proposalActivate(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, PROPOSAL_ACTIVATE_DISCRIMINATOR);
  }

  public static final Discriminator PROPOSAL_APPROVE_DISCRIMINATOR = toDiscriminator(144, 37, 164, 136, 188, 216, 42, 248);

  /// Approve a multisig proposal on behalf of the `member`.
  /// The proposal must be `Active`.
  ///
  public static List<AccountMeta> proposalApproveKeys(final PublicKey multisigKey,
                                                      final PublicKey memberKey,
                                                      final PublicKey proposalKey) {
    return List.of(
      createRead(multisigKey),
      createWritableSigner(memberKey),
      createWrite(proposalKey)
    );
  }

  /// Approve a multisig proposal on behalf of the `member`.
  /// The proposal must be `Active`.
  ///
  public static Instruction proposalApprove(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                            final PublicKey multisigKey,
                                            final PublicKey memberKey,
                                            final PublicKey proposalKey,
                                            final ProposalVoteArgs args) {
    final var keys = proposalApproveKeys(
      multisigKey,
      memberKey,
      proposalKey
    );
    return proposalApprove(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Approve a multisig proposal on behalf of the `member`.
  /// The proposal must be `Active`.
  ///
  public static Instruction proposalApprove(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                            final List<AccountMeta> keys,
                                            final ProposalVoteArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROPOSAL_APPROVE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProposalApproveIxData(Discriminator discriminator, ProposalVoteArgs args) implements SerDe {  

    public static ProposalApproveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static ProposalApproveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProposalVoteArgs.read(_data, i);
      return new ProposalApproveIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator PROPOSAL_REJECT_DISCRIMINATOR = toDiscriminator(243, 62, 134, 156, 230, 106, 246, 135);

  /// Reject a multisig proposal on behalf of the `member`.
  /// The proposal must be `Active`.
  ///
  public static List<AccountMeta> proposalRejectKeys(final PublicKey multisigKey,
                                                     final PublicKey memberKey,
                                                     final PublicKey proposalKey) {
    return List.of(
      createRead(multisigKey),
      createWritableSigner(memberKey),
      createWrite(proposalKey)
    );
  }

  /// Reject a multisig proposal on behalf of the `member`.
  /// The proposal must be `Active`.
  ///
  public static Instruction proposalReject(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final PublicKey multisigKey,
                                           final PublicKey memberKey,
                                           final PublicKey proposalKey,
                                           final ProposalVoteArgs args) {
    final var keys = proposalRejectKeys(
      multisigKey,
      memberKey,
      proposalKey
    );
    return proposalReject(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Reject a multisig proposal on behalf of the `member`.
  /// The proposal must be `Active`.
  ///
  public static Instruction proposalReject(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final List<AccountMeta> keys,
                                           final ProposalVoteArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROPOSAL_REJECT_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProposalRejectIxData(Discriminator discriminator, ProposalVoteArgs args) implements SerDe {  

    public static ProposalRejectIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static ProposalRejectIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProposalVoteArgs.read(_data, i);
      return new ProposalRejectIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator PROPOSAL_CANCEL_DISCRIMINATOR = toDiscriminator(27, 42, 127, 237, 38, 163, 84, 203);

  /// Cancel a multisig proposal on behalf of the `member`.
  /// The proposal must be `Approved`.
  ///
  public static List<AccountMeta> proposalCancelKeys(final PublicKey multisigKey,
                                                     final PublicKey memberKey,
                                                     final PublicKey proposalKey) {
    return List.of(
      createRead(multisigKey),
      createWritableSigner(memberKey),
      createWrite(proposalKey)
    );
  }

  /// Cancel a multisig proposal on behalf of the `member`.
  /// The proposal must be `Approved`.
  ///
  public static Instruction proposalCancel(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final PublicKey multisigKey,
                                           final PublicKey memberKey,
                                           final PublicKey proposalKey,
                                           final ProposalVoteArgs args) {
    final var keys = proposalCancelKeys(
      multisigKey,
      memberKey,
      proposalKey
    );
    return proposalCancel(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Cancel a multisig proposal on behalf of the `member`.
  /// The proposal must be `Approved`.
  ///
  public static Instruction proposalCancel(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                           final List<AccountMeta> keys,
                                           final ProposalVoteArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = PROPOSAL_CANCEL_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record ProposalCancelIxData(Discriminator discriminator, ProposalVoteArgs args) implements SerDe {  

    public static ProposalCancelIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static ProposalCancelIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = ProposalVoteArgs.read(_data, i);
      return new ProposalCancelIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator SPENDING_LIMIT_USE_DISCRIMINATOR = toDiscriminator(16, 57, 130, 127, 193, 20, 155, 134);

  /// Use a spending limit to transfer tokens from a multisig vault to a destination account.
  ///
  /// @param multisigKey The multisig account the `spending_limit` is for.
  /// @param spendingLimitKey The SpendingLimit account to use.
  /// @param vaultKey Multisig vault account to transfer tokens from.
  /// @param destinationKey Destination account to transfer tokens to.
  /// @param systemProgramKey In case `spending_limit.mint` is SOL.
  /// @param mintKey The mint of the tokens to transfer in case `spending_limit.mint` is an SPL token.
  /// @param vaultTokenAccountKey Multisig vault token account to transfer tokens from in case `spending_limit.mint` is an SPL token.
  /// @param destinationTokenAccountKey Destination token account in case `spending_limit.mint` is an SPL token.
  /// @param tokenProgramKey In case `spending_limit.mint` is an SPL token.
  public static List<AccountMeta> spendingLimitUseKeys(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                       final PublicKey multisigKey,
                                                       final PublicKey memberKey,
                                                       final PublicKey spendingLimitKey,
                                                       final PublicKey vaultKey,
                                                       final PublicKey destinationKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey mintKey,
                                                       final PublicKey vaultTokenAccountKey,
                                                       final PublicKey destinationTokenAccountKey,
                                                       final PublicKey tokenProgramKey) {
    return List.of(
      createRead(multisigKey),
      createReadOnlySigner(memberKey),
      createWrite(spendingLimitKey),
      createWrite(vaultKey),
      createWrite(destinationKey),
      createRead(requireNonNullElse(systemProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey())),
      createRead(requireNonNullElse(mintKey, invokedSquadsMultisigProgramProgramMeta.publicKey())),
      createWrite(requireNonNullElse(vaultTokenAccountKey, invokedSquadsMultisigProgramProgramMeta.publicKey())),
      createWrite(requireNonNullElse(destinationTokenAccountKey, invokedSquadsMultisigProgramProgramMeta.publicKey())),
      createRead(requireNonNullElse(tokenProgramKey, invokedSquadsMultisigProgramProgramMeta.publicKey()))
    );
  }

  /// Use a spending limit to transfer tokens from a multisig vault to a destination account.
  ///
  /// @param multisigKey The multisig account the `spending_limit` is for.
  /// @param spendingLimitKey The SpendingLimit account to use.
  /// @param vaultKey Multisig vault account to transfer tokens from.
  /// @param destinationKey Destination account to transfer tokens to.
  /// @param systemProgramKey In case `spending_limit.mint` is SOL.
  /// @param mintKey The mint of the tokens to transfer in case `spending_limit.mint` is an SPL token.
  /// @param vaultTokenAccountKey Multisig vault token account to transfer tokens from in case `spending_limit.mint` is an SPL token.
  /// @param destinationTokenAccountKey Destination token account in case `spending_limit.mint` is an SPL token.
  /// @param tokenProgramKey In case `spending_limit.mint` is an SPL token.
  public static Instruction spendingLimitUse(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                             final PublicKey multisigKey,
                                             final PublicKey memberKey,
                                             final PublicKey spendingLimitKey,
                                             final PublicKey vaultKey,
                                             final PublicKey destinationKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey mintKey,
                                             final PublicKey vaultTokenAccountKey,
                                             final PublicKey destinationTokenAccountKey,
                                             final PublicKey tokenProgramKey,
                                             final SpendingLimitUseArgs args) {
    final var keys = spendingLimitUseKeys(
      invokedSquadsMultisigProgramProgramMeta,
      multisigKey,
      memberKey,
      spendingLimitKey,
      vaultKey,
      destinationKey,
      systemProgramKey,
      mintKey,
      vaultTokenAccountKey,
      destinationTokenAccountKey,
      tokenProgramKey
    );
    return spendingLimitUse(invokedSquadsMultisigProgramProgramMeta, keys, args);
  }

  /// Use a spending limit to transfer tokens from a multisig vault to a destination account.
  ///
  public static Instruction spendingLimitUse(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                             final List<AccountMeta> keys,
                                             final SpendingLimitUseArgs args) {
    final byte[] _data = new byte[8 + args.l()];
    int i = SPENDING_LIMIT_USE_DISCRIMINATOR.write(_data, 0);
    args.write(_data, i);

    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, _data);
  }

  public record SpendingLimitUseIxData(Discriminator discriminator, SpendingLimitUseArgs args) implements SerDe {  

    public static SpendingLimitUseIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int ARGS_OFFSET = 8;

    public static SpendingLimitUseIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var args = SpendingLimitUseArgs.read(_data, i);
      return new SpendingLimitUseIxData(discriminator, args);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += args.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + args.l();
    }
  }

  public static final Discriminator CONFIG_TRANSACTION_ACCOUNTS_CLOSE_DISCRIMINATOR = toDiscriminator(80, 203, 84, 53, 151, 112, 187, 186);

  /// Closes a `ConfigTransaction` and the corresponding `Proposal`.
  /// `transaction` can be closed if either:
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale.
  ///
  /// @param transactionKey ConfigTransaction corresponding to the `proposal`.
  /// @param rentCollectorKey The rent collector.
  public static List<AccountMeta> configTransactionAccountsCloseKeys(final PublicKey multisigKey,
                                                                     final PublicKey proposalKey,
                                                                     final PublicKey transactionKey,
                                                                     final PublicKey rentCollectorKey,
                                                                     final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createWrite(proposalKey),
      createWrite(transactionKey),
      createWrite(rentCollectorKey),
      createRead(systemProgramKey)
    );
  }

  /// Closes a `ConfigTransaction` and the corresponding `Proposal`.
  /// `transaction` can be closed if either:
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale.
  ///
  /// @param transactionKey ConfigTransaction corresponding to the `proposal`.
  /// @param rentCollectorKey The rent collector.
  public static Instruction configTransactionAccountsClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                           final PublicKey multisigKey,
                                                           final PublicKey proposalKey,
                                                           final PublicKey transactionKey,
                                                           final PublicKey rentCollectorKey,
                                                           final PublicKey systemProgramKey) {
    final var keys = configTransactionAccountsCloseKeys(
      multisigKey,
      proposalKey,
      transactionKey,
      rentCollectorKey,
      systemProgramKey
    );
    return configTransactionAccountsClose(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Closes a `ConfigTransaction` and the corresponding `Proposal`.
  /// `transaction` can be closed if either:
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale.
  ///
  public static Instruction configTransactionAccountsClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, CONFIG_TRANSACTION_ACCOUNTS_CLOSE_DISCRIMINATOR);
  }

  public static final Discriminator VAULT_TRANSACTION_ACCOUNTS_CLOSE_DISCRIMINATOR = toDiscriminator(196, 71, 187, 176, 2, 35, 170, 165);

  /// Closes a `VaultTransaction` and the corresponding `Proposal`.
  /// `transaction` can be closed if either:
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale and not `Approved`.
  ///
  /// @param transactionKey VaultTransaction corresponding to the `proposal`.
  /// @param rentCollectorKey The rent collector.
  public static List<AccountMeta> vaultTransactionAccountsCloseKeys(final PublicKey multisigKey,
                                                                    final PublicKey proposalKey,
                                                                    final PublicKey transactionKey,
                                                                    final PublicKey rentCollectorKey,
                                                                    final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createWrite(proposalKey),
      createWrite(transactionKey),
      createWrite(rentCollectorKey),
      createRead(systemProgramKey)
    );
  }

  /// Closes a `VaultTransaction` and the corresponding `Proposal`.
  /// `transaction` can be closed if either:
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale and not `Approved`.
  ///
  /// @param transactionKey VaultTransaction corresponding to the `proposal`.
  /// @param rentCollectorKey The rent collector.
  public static Instruction vaultTransactionAccountsClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                          final PublicKey multisigKey,
                                                          final PublicKey proposalKey,
                                                          final PublicKey transactionKey,
                                                          final PublicKey rentCollectorKey,
                                                          final PublicKey systemProgramKey) {
    final var keys = vaultTransactionAccountsCloseKeys(
      multisigKey,
      proposalKey,
      transactionKey,
      rentCollectorKey,
      systemProgramKey
    );
    return vaultTransactionAccountsClose(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Closes a `VaultTransaction` and the corresponding `Proposal`.
  /// `transaction` can be closed if either:
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale and not `Approved`.
  ///
  public static Instruction vaultTransactionAccountsClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, VAULT_TRANSACTION_ACCOUNTS_CLOSE_DISCRIMINATOR);
  }

  public static final Discriminator VAULT_BATCH_TRANSACTION_ACCOUNT_CLOSE_DISCRIMINATOR = toDiscriminator(134, 18, 19, 106, 129, 68, 97, 247);

  /// Closes a `VaultBatchTransaction` belonging to the `batch` and `proposal`.
  /// `transaction` can be closed if either:
  /// - it's marked as executed within the `batch`;
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale and not `Approved`.
  ///
  /// @param batchKey `Batch` corresponding to the `proposal`.
  /// @param transactionKey `VaultBatchTransaction` account to close.
  ///                       The transaction must be the current last one in the batch.
  /// @param rentCollectorKey The rent collector.
  public static List<AccountMeta> vaultBatchTransactionAccountCloseKeys(final PublicKey multisigKey,
                                                                        final PublicKey proposalKey,
                                                                        final PublicKey batchKey,
                                                                        final PublicKey transactionKey,
                                                                        final PublicKey rentCollectorKey,
                                                                        final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createRead(proposalKey),
      createWrite(batchKey),
      createWrite(transactionKey),
      createWrite(rentCollectorKey),
      createRead(systemProgramKey)
    );
  }

  /// Closes a `VaultBatchTransaction` belonging to the `batch` and `proposal`.
  /// `transaction` can be closed if either:
  /// - it's marked as executed within the `batch`;
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale and not `Approved`.
  ///
  /// @param batchKey `Batch` corresponding to the `proposal`.
  /// @param transactionKey `VaultBatchTransaction` account to close.
  ///                       The transaction must be the current last one in the batch.
  /// @param rentCollectorKey The rent collector.
  public static Instruction vaultBatchTransactionAccountClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                              final PublicKey multisigKey,
                                                              final PublicKey proposalKey,
                                                              final PublicKey batchKey,
                                                              final PublicKey transactionKey,
                                                              final PublicKey rentCollectorKey,
                                                              final PublicKey systemProgramKey) {
    final var keys = vaultBatchTransactionAccountCloseKeys(
      multisigKey,
      proposalKey,
      batchKey,
      transactionKey,
      rentCollectorKey,
      systemProgramKey
    );
    return vaultBatchTransactionAccountClose(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Closes a `VaultBatchTransaction` belonging to the `batch` and `proposal`.
  /// `transaction` can be closed if either:
  /// - it's marked as executed within the `batch`;
  /// - the `proposal` is in a terminal state: `Executed`, `Rejected`, or `Cancelled`.
  /// - the `proposal` is stale and not `Approved`.
  ///
  public static Instruction vaultBatchTransactionAccountClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, VAULT_BATCH_TRANSACTION_ACCOUNT_CLOSE_DISCRIMINATOR);
  }

  public static final Discriminator BATCH_ACCOUNTS_CLOSE_DISCRIMINATOR = toDiscriminator(218, 196, 7, 175, 130, 102, 11, 255);

  /// Closes Batch and the corresponding Proposal accounts for proposals in terminal states:
  /// `Executed`, `Rejected`, or `Cancelled` or stale proposals that aren't `Approved`.
  /// 
  /// This instruction is only allowed to be executed when all `VaultBatchTransaction` accounts
  /// in the `batch` are already closed: `batch.size == 0`.
  ///
  /// @param batchKey `Batch` corresponding to the `proposal`.
  /// @param rentCollectorKey The rent collector.
  public static List<AccountMeta> batchAccountsCloseKeys(final PublicKey multisigKey,
                                                         final PublicKey proposalKey,
                                                         final PublicKey batchKey,
                                                         final PublicKey rentCollectorKey,
                                                         final PublicKey systemProgramKey) {
    return List.of(
      createRead(multisigKey),
      createWrite(proposalKey),
      createWrite(batchKey),
      createWrite(rentCollectorKey),
      createRead(systemProgramKey)
    );
  }

  /// Closes Batch and the corresponding Proposal accounts for proposals in terminal states:
  /// `Executed`, `Rejected`, or `Cancelled` or stale proposals that aren't `Approved`.
  /// 
  /// This instruction is only allowed to be executed when all `VaultBatchTransaction` accounts
  /// in the `batch` are already closed: `batch.size == 0`.
  ///
  /// @param batchKey `Batch` corresponding to the `proposal`.
  /// @param rentCollectorKey The rent collector.
  public static Instruction batchAccountsClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                               final PublicKey multisigKey,
                                               final PublicKey proposalKey,
                                               final PublicKey batchKey,
                                               final PublicKey rentCollectorKey,
                                               final PublicKey systemProgramKey) {
    final var keys = batchAccountsCloseKeys(
      multisigKey,
      proposalKey,
      batchKey,
      rentCollectorKey,
      systemProgramKey
    );
    return batchAccountsClose(invokedSquadsMultisigProgramProgramMeta, keys);
  }

  /// Closes Batch and the corresponding Proposal accounts for proposals in terminal states:
  /// `Executed`, `Rejected`, or `Cancelled` or stale proposals that aren't `Approved`.
  /// 
  /// This instruction is only allowed to be executed when all `VaultBatchTransaction` accounts
  /// in the `batch` are already closed: `batch.size == 0`.
  ///
  public static Instruction batchAccountsClose(final AccountMeta invokedSquadsMultisigProgramProgramMeta,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSquadsMultisigProgramProgramMeta, keys, BATCH_ACCOUNTS_CLOSE_DISCRIMINATOR);
  }

  private SquadsMultisigProgram() {
  }
}
