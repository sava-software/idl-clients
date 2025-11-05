package software.sava.idl.clients.stake.gen;

import java.lang.String;

import java.util.List;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.stake.gen.types.Authorized;
import software.sava.idl.clients.stake.gen.types.Lockup;
import software.sava.idl.clients.stake.gen.types.StakeAuthorize;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class StakeProgramProgram {

  public static final Discriminator INITIALIZE_DISCRIMINATOR = toDiscriminator(175, 175, 109, 31, 13, 152, 155, 237);

  /// @param stakeKey The stake account to initialize
  /// @param rentKey Rent sysvar
  public static List<AccountMeta> initializeKeys(final AccountMeta invokedStakeProgramProgramMeta                                                 ,
                                                 final PublicKey stakeKey,
                                                 final PublicKey rentKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(rentKey)
    );
  }

  /// @param stakeKey The stake account to initialize
  /// @param rentKey Rent sysvar
  public static Instruction initialize(final AccountMeta invokedStakeProgramProgramMeta,
                                       final PublicKey stakeKey,
                                       final PublicKey rentKey,
                                       final Authorized authorized,
                                       final Lockup lockup) {
    final var keys = initializeKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      rentKey
    );
    return initialize(invokedStakeProgramProgramMeta, keys, authorized, lockup);
  }

  public static Instruction initialize(final AccountMeta invokedStakeProgramProgramMeta                                       ,
                                       final List<AccountMeta> keys,
                                       final Authorized authorized,
                                       final Lockup lockup) {
    final byte[] _data = new byte[8 + Borsh.len(authorized) + Borsh.len(lockup)];
    int i = INITIALIZE_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(authorized, _data, i);
    Borsh.write(lockup, _data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record InitializeIxData(Discriminator discriminator, Authorized authorized, Lockup lockup) implements Borsh {  

    public static InitializeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 120;

    public static InitializeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var authorized = Authorized.read(_data, i);
      i += Borsh.len(authorized);
      final var lockup = Lockup.read(_data, i);
      return new InitializeIxData(discriminator, authorized, lockup);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(authorized, _data, i);
      i += Borsh.write(lockup, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator AUTHORIZE_DISCRIMINATOR = toDiscriminator(173, 193, 102, 210, 219, 137, 113, 120);

  /// @param stakeKey The stake account to be updated
  /// @param clockKey Clock sysvar
  /// @param authorityKey stake's current stake or withdraw authority to change away from. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer
  public static List<AccountMeta> authorizeKeys(final AccountMeta invokedStakeProgramProgramMeta                                                ,
                                                final PublicKey stakeKey,
                                                final PublicKey clockKey,
                                                final PublicKey authorityKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(clockKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// @param stakeKey The stake account to be updated
  /// @param clockKey Clock sysvar
  /// @param authorityKey stake's current stake or withdraw authority to change away from. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer
  public static Instruction authorize(final AccountMeta invokedStakeProgramProgramMeta,
                                      final PublicKey stakeKey,
                                      final PublicKey clockKey,
                                      final PublicKey authorityKey,
                                      final PublicKey newAuthority,
                                      final StakeAuthorize stakeAuthorize) {
    final var keys = authorizeKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      clockKey,
      authorityKey
    );
    return authorize(invokedStakeProgramProgramMeta, keys, newAuthority, stakeAuthorize);
  }

  public static Instruction authorize(final AccountMeta invokedStakeProgramProgramMeta                                      ,
                                      final List<AccountMeta> keys,
                                      final PublicKey newAuthority,
                                      final StakeAuthorize stakeAuthorize) {
    final byte[] _data = new byte[40 + Borsh.len(stakeAuthorize)];
    int i = AUTHORIZE_DISCRIMINATOR.write(_data, 0);
    newAuthority.write(_data, i);
    i += 32;
    Borsh.write(stakeAuthorize, _data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record AuthorizeIxData(Discriminator discriminator, PublicKey newAuthority, StakeAuthorize stakeAuthorize) implements Borsh {  

    public static AuthorizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 41;

    public static AuthorizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newAuthority = readPubKey(_data, i);
      i += 32;
      final var stakeAuthorize = StakeAuthorize.read(_data, i);
      return new AuthorizeIxData(discriminator, newAuthority, stakeAuthorize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newAuthority.write(_data, i);
      i += 32;
      i += Borsh.write(stakeAuthorize, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DELEGATE_STAKE_DISCRIMINATOR = toDiscriminator(50, 110, 95, 179, 194, 75, 140, 246);

  /// @param stakeKey The stake account to be delegated
  /// @param voteKey Vote account to which stake will be delegated
  /// @param clockKey Clock sysvar
  /// @param stakeHistoryKey Stake history sysvar
  /// @param stakeConfigKey Stake config native program
  /// @param stakeAuthorityKey stake's stake authority
  public static List<AccountMeta> delegateStakeKeys(final AccountMeta invokedStakeProgramProgramMeta                                                    ,
                                                    final PublicKey stakeKey,
                                                    final PublicKey voteKey,
                                                    final PublicKey clockKey,
                                                    final PublicKey stakeHistoryKey,
                                                    final PublicKey stakeConfigKey,
                                                    final PublicKey stakeAuthorityKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(voteKey),
      createRead(clockKey),
      createRead(stakeHistoryKey),
      createRead(stakeConfigKey),
      createReadOnlySigner(stakeAuthorityKey)
    );
  }

  /// @param stakeKey The stake account to be delegated
  /// @param voteKey Vote account to which stake will be delegated
  /// @param clockKey Clock sysvar
  /// @param stakeHistoryKey Stake history sysvar
  /// @param stakeConfigKey Stake config native program
  /// @param stakeAuthorityKey stake's stake authority
  public static Instruction delegateStake(final AccountMeta invokedStakeProgramProgramMeta,
                                          final PublicKey stakeKey,
                                          final PublicKey voteKey,
                                          final PublicKey clockKey,
                                          final PublicKey stakeHistoryKey,
                                          final PublicKey stakeConfigKey,
                                          final PublicKey stakeAuthorityKey) {
    final var keys = delegateStakeKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      voteKey,
      clockKey,
      stakeHistoryKey,
      stakeConfigKey,
      stakeAuthorityKey
    );
    return delegateStake(invokedStakeProgramProgramMeta, keys);
  }

  public static Instruction delegateStake(final AccountMeta invokedStakeProgramProgramMeta                                          ,
                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, DELEGATE_STAKE_DISCRIMINATOR);
  }

  public static final Discriminator SPLIT_DISCRIMINATOR = toDiscriminator(124, 189, 27, 43, 216, 40, 147, 66);

  /// @param fromKey The stake account to split. Must be in the Initialized or Stake state
  /// @param toKey The uninitialized stake account to split to. Must be rent-exempt starting from solana 1.17.
  /// @param stakeAuthorityKey from's stake authority
  public static List<AccountMeta> splitKeys(final AccountMeta invokedStakeProgramProgramMeta                                            ,
                                            final PublicKey fromKey,
                                            final PublicKey toKey,
                                            final PublicKey stakeAuthorityKey) {
    return List.of(
      createWrite(fromKey),
      createWrite(toKey),
      createReadOnlySigner(stakeAuthorityKey)
    );
  }

  /// @param fromKey The stake account to split. Must be in the Initialized or Stake state
  /// @param toKey The uninitialized stake account to split to. Must be rent-exempt starting from solana 1.17.
  /// @param stakeAuthorityKey from's stake authority
  public static Instruction split(final AccountMeta invokedStakeProgramProgramMeta,
                                  final PublicKey fromKey,
                                  final PublicKey toKey,
                                  final PublicKey stakeAuthorityKey,
                                  final long lamports) {
    final var keys = splitKeys(
      invokedStakeProgramProgramMeta,
      fromKey,
      toKey,
      stakeAuthorityKey
    );
    return split(invokedStakeProgramProgramMeta, keys, lamports);
  }

  public static Instruction split(final AccountMeta invokedStakeProgramProgramMeta                                  ,
                                  final List<AccountMeta> keys,
                                  final long lamports) {
    final byte[] _data = new byte[16];
    int i = SPLIT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, lamports);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record SplitIxData(Discriminator discriminator, long lamports) implements Borsh {  

    public static SplitIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SplitIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lamports = getInt64LE(_data, i);
      return new SplitIxData(discriminator, lamports);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, lamports);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 18, 70, 156, 148, 109, 161, 34);

  /// @param fromKey The stake account to withdraw from
  /// @param toKey Recipient account
  /// @param clockKey Clock sysvar
  /// @param stakeHistoryKey Stake history sysvar
  /// @param withdrawAuthorityKey from's withdraw authority. If stake Lockup is active, the signing lockup authority must follow.
  public static List<AccountMeta> withdrawKeys(final AccountMeta invokedStakeProgramProgramMeta                                               ,
                                               final PublicKey fromKey,
                                               final PublicKey toKey,
                                               final PublicKey clockKey,
                                               final PublicKey stakeHistoryKey,
                                               final PublicKey withdrawAuthorityKey) {
    return List.of(
      createWrite(fromKey),
      createWrite(toKey),
      createRead(clockKey),
      createRead(stakeHistoryKey),
      createReadOnlySigner(withdrawAuthorityKey)
    );
  }

  /// @param fromKey The stake account to withdraw from
  /// @param toKey Recipient account
  /// @param clockKey Clock sysvar
  /// @param stakeHistoryKey Stake history sysvar
  /// @param withdrawAuthorityKey from's withdraw authority. If stake Lockup is active, the signing lockup authority must follow.
  public static Instruction withdraw(final AccountMeta invokedStakeProgramProgramMeta,
                                     final PublicKey fromKey,
                                     final PublicKey toKey,
                                     final PublicKey clockKey,
                                     final PublicKey stakeHistoryKey,
                                     final PublicKey withdrawAuthorityKey,
                                     final long lamports) {
    final var keys = withdrawKeys(
      invokedStakeProgramProgramMeta,
      fromKey,
      toKey,
      clockKey,
      stakeHistoryKey,
      withdrawAuthorityKey
    );
    return withdraw(invokedStakeProgramProgramMeta, keys, lamports);
  }

  public static Instruction withdraw(final AccountMeta invokedStakeProgramProgramMeta                                     ,
                                     final List<AccountMeta> keys,
                                     final long lamports) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, lamports);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record WithdrawIxData(Discriminator discriminator, long lamports) implements Borsh {  

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
      final var lamports = getInt64LE(_data, i);
      return new WithdrawIxData(discriminator, lamports);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, lamports);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEACTIVATE_DISCRIMINATOR = toDiscriminator(44, 112, 33, 172, 113, 28, 142, 13);

  /// @param stakeKey The stake account to deactivate
  /// @param clockKey Clock sysvar
  /// @param stakeAuthorityKey stake's stake authority
  public static List<AccountMeta> deactivateKeys(final AccountMeta invokedStakeProgramProgramMeta                                                 ,
                                                 final PublicKey stakeKey,
                                                 final PublicKey clockKey,
                                                 final PublicKey stakeAuthorityKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(clockKey),
      createReadOnlySigner(stakeAuthorityKey)
    );
  }

  /// @param stakeKey The stake account to deactivate
  /// @param clockKey Clock sysvar
  /// @param stakeAuthorityKey stake's stake authority
  public static Instruction deactivate(final AccountMeta invokedStakeProgramProgramMeta,
                                       final PublicKey stakeKey,
                                       final PublicKey clockKey,
                                       final PublicKey stakeAuthorityKey) {
    final var keys = deactivateKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      clockKey,
      stakeAuthorityKey
    );
    return deactivate(invokedStakeProgramProgramMeta, keys);
  }

  public static Instruction deactivate(final AccountMeta invokedStakeProgramProgramMeta                                       ,
                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, DEACTIVATE_DISCRIMINATOR);
  }

  public static final Discriminator SET_LOCKUP_DISCRIMINATOR = toDiscriminator(44, 170, 189, 40, 128, 123, 252, 201);

  /// @param stakeKey The stake account to set the lockup of
  /// @param authorityKey stake's withdraw authority or lockup authority if lockup is active
  public static List<AccountMeta> setLockupKeys(final AccountMeta invokedStakeProgramProgramMeta                                                ,
                                                final PublicKey stakeKey,
                                                final PublicKey authorityKey) {
    return List.of(
      createWrite(stakeKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// @param stakeKey The stake account to set the lockup of
  /// @param authorityKey stake's withdraw authority or lockup authority if lockup is active
  public static Instruction setLockup(final AccountMeta invokedStakeProgramProgramMeta,
                                      final PublicKey stakeKey,
                                      final PublicKey authorityKey,
                                      final OptionalLong unixTimestamp,
                                      final OptionalLong epoch,
                                      final PublicKey custodian) {
    final var keys = setLockupKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      authorityKey
    );
    return setLockup(
      invokedStakeProgramProgramMeta,
      keys,
      unixTimestamp,
      epoch,
      custodian
    );
  }

  public static Instruction setLockup(final AccountMeta invokedStakeProgramProgramMeta                                      ,
                                      final List<AccountMeta> keys,
                                      final OptionalLong unixTimestamp,
                                      final OptionalLong epoch,
                                      final PublicKey custodian) {
    final byte[] _data = new byte[
    8
    + (unixTimestamp == null || unixTimestamp.isEmpty() ? 1 : 9)
    + (epoch == null || epoch.isEmpty() ? 1 : 9)
    + (custodian == null ? 1 : 33)
    ];
    int i = SET_LOCKUP_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(unixTimestamp, _data, i);
    i += Borsh.writeOptional(epoch, _data, i);
    Borsh.writeOptional(custodian, _data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record SetLockupIxData(Discriminator discriminator,
                                OptionalLong unixTimestamp,
                                OptionalLong epoch,
                                PublicKey custodian) implements Borsh {  

    public static SetLockupIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SetLockupIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalLong unixTimestamp;
      if (_data[i] == 0) {
        unixTimestamp = OptionalLong.empty();
        ++i;
      } else {
        ++i;
        unixTimestamp = OptionalLong.of(getInt64LE(_data, i));
        i += 8;
      }
      final OptionalLong epoch;
      if (_data[i] == 0) {
        epoch = OptionalLong.empty();
        ++i;
      } else {
        ++i;
        epoch = OptionalLong.of(getInt64LE(_data, i));
        i += 8;
      }
      final PublicKey custodian;
      if (_data[i] == 0) {
        custodian = null;
      } else {
        ++i;
        custodian = readPubKey(_data, i);
      }
      return new SetLockupIxData(discriminator, unixTimestamp, epoch, custodian);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(unixTimestamp, _data, i);
      i += Borsh.writeOptional(epoch, _data, i);
      i += Borsh.writeOptional(custodian, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (unixTimestamp == null || unixTimestamp.isEmpty() ? 1 : (1 + 8)) + (epoch == null || epoch.isEmpty() ? 1 : (1 + 8)) + (custodian == null ? 1 : (1 + 32));
    }
  }

  public static final Discriminator MERGE_DISCRIMINATOR = toDiscriminator(148, 141, 236, 47, 174, 126, 69, 111);

  /// @param toKey The destination stake account to merge into
  /// @param fromKey The stake account to merge from. Must have exact same lockup and authority as to. This account will be drained.
  /// @param clockKey Clock sysvar
  /// @param stakeHistoryKey Stake history sysvar
  /// @param stakeAuthorityKey Both from and to's stake authority
  public static List<AccountMeta> mergeKeys(final AccountMeta invokedStakeProgramProgramMeta                                            ,
                                            final PublicKey toKey,
                                            final PublicKey fromKey,
                                            final PublicKey clockKey,
                                            final PublicKey stakeHistoryKey,
                                            final PublicKey stakeAuthorityKey) {
    return List.of(
      createWrite(toKey),
      createWrite(fromKey),
      createRead(clockKey),
      createRead(stakeHistoryKey),
      createReadOnlySigner(stakeAuthorityKey)
    );
  }

  /// @param toKey The destination stake account to merge into
  /// @param fromKey The stake account to merge from. Must have exact same lockup and authority as to. This account will be drained.
  /// @param clockKey Clock sysvar
  /// @param stakeHistoryKey Stake history sysvar
  /// @param stakeAuthorityKey Both from and to's stake authority
  public static Instruction merge(final AccountMeta invokedStakeProgramProgramMeta,
                                  final PublicKey toKey,
                                  final PublicKey fromKey,
                                  final PublicKey clockKey,
                                  final PublicKey stakeHistoryKey,
                                  final PublicKey stakeAuthorityKey) {
    final var keys = mergeKeys(
      invokedStakeProgramProgramMeta,
      toKey,
      fromKey,
      clockKey,
      stakeHistoryKey,
      stakeAuthorityKey
    );
    return merge(invokedStakeProgramProgramMeta, keys);
  }

  public static Instruction merge(final AccountMeta invokedStakeProgramProgramMeta                                  ,
                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, MERGE_DISCRIMINATOR);
  }

  public static final Discriminator AUTHORIZE_WITH_SEED_DISCRIMINATOR = toDiscriminator(7, 18, 211, 41, 76, 83, 115, 61);

  /// @param stakeKey The stake account to be updated, with the authority to be updated being an account created with Pubkey::create_with_seed()
  /// @param authorityBaseKey Base account of stake's authority to be updated
  /// @param clockKey Clock sysvar. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer.
  public static List<AccountMeta> authorizeWithSeedKeys(final AccountMeta invokedStakeProgramProgramMeta                                                        ,
                                                        final PublicKey stakeKey,
                                                        final PublicKey authorityBaseKey,
                                                        final PublicKey clockKey) {
    return List.of(
      createWrite(stakeKey),
      createReadOnlySigner(authorityBaseKey),
      createRead(clockKey)
    );
  }

  /// @param stakeKey The stake account to be updated, with the authority to be updated being an account created with Pubkey::create_with_seed()
  /// @param authorityBaseKey Base account of stake's authority to be updated
  /// @param clockKey Clock sysvar. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer.
  public static Instruction authorizeWithSeed(final AccountMeta invokedStakeProgramProgramMeta,
                                              final PublicKey stakeKey,
                                              final PublicKey authorityBaseKey,
                                              final PublicKey clockKey,
                                              final PublicKey newAuthority,
                                              final StakeAuthorize stakeAuthorize,
                                              final String authoritySeed,
                                              final PublicKey authorityOwner) {
    final var keys = authorizeWithSeedKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      authorityBaseKey,
      clockKey
    );
    return authorizeWithSeed(
      invokedStakeProgramProgramMeta,
      keys,
      newAuthority,
      stakeAuthorize,
      authoritySeed,
      authorityOwner
    );
  }

  public static Instruction authorizeWithSeed(final AccountMeta invokedStakeProgramProgramMeta                                              ,
                                              final List<AccountMeta> keys,
                                              final PublicKey newAuthority,
                                              final StakeAuthorize stakeAuthorize,
                                              final String authoritySeed,
                                              final PublicKey authorityOwner) {
    final byte[] _authoritySeed = authoritySeed.getBytes(UTF_8);
    final byte[] _data = new byte[76 + Borsh.len(stakeAuthorize) + Borsh.lenVector(_authoritySeed)];
    int i = AUTHORIZE_WITH_SEED_DISCRIMINATOR.write(_data, 0);
    newAuthority.write(_data, i);
    i += 32;
    i += Borsh.write(stakeAuthorize, _data, i);
    i += Borsh.writeVector(_authoritySeed, _data, i);
    authorityOwner.write(_data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record AuthorizeWithSeedIxData(Discriminator discriminator,
                                        PublicKey newAuthority,
                                        StakeAuthorize stakeAuthorize,
                                        String authoritySeed, byte[] _authoritySeed,
                                        PublicKey authorityOwner) implements Borsh {  

    public static AuthorizeWithSeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AuthorizeWithSeedIxData createRecord(final Discriminator discriminator,
                                                       final PublicKey newAuthority,
                                                       final StakeAuthorize stakeAuthorize,
                                                       final String authoritySeed,
                                                       final PublicKey authorityOwner) {
      return new AuthorizeWithSeedIxData(discriminator,
                                         newAuthority,
                                         stakeAuthorize,
                                         authoritySeed, authoritySeed.getBytes(UTF_8),
                                         authorityOwner);
    }

    public static AuthorizeWithSeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newAuthority = readPubKey(_data, i);
      i += 32;
      final var stakeAuthorize = StakeAuthorize.read(_data, i);
      i += Borsh.len(stakeAuthorize);
      final var authoritySeed = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var authorityOwner = readPubKey(_data, i);
      return new AuthorizeWithSeedIxData(discriminator,
                                         newAuthority,
                                         stakeAuthorize,
                                         authoritySeed, authoritySeed.getBytes(UTF_8),
                                         authorityOwner);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newAuthority.write(_data, i);
      i += 32;
      i += Borsh.write(stakeAuthorize, _data, i);
      i += Borsh.writeVector(_authoritySeed, _data, i);
      authorityOwner.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 32 + Borsh.len(stakeAuthorize) + Borsh.lenVector(_authoritySeed) + 32;
    }
  }

  public static final Discriminator INITIALIZE_CHECKED_DISCRIMINATOR = toDiscriminator(219, 90, 58, 161, 139, 88, 246, 28);

  /// @param stakeKey The stake account to initialize
  /// @param rentKey Rent sysvar
  /// @param stakeAuthorityKey stake's new stake authority
  /// @param withdrawAuthorityKey stake's new withdraw authority
  public static List<AccountMeta> initializeCheckedKeys(final AccountMeta invokedStakeProgramProgramMeta                                                        ,
                                                        final PublicKey stakeKey,
                                                        final PublicKey rentKey,
                                                        final PublicKey stakeAuthorityKey,
                                                        final PublicKey withdrawAuthorityKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(rentKey),
      createRead(stakeAuthorityKey),
      createReadOnlySigner(withdrawAuthorityKey)
    );
  }

  /// @param stakeKey The stake account to initialize
  /// @param rentKey Rent sysvar
  /// @param stakeAuthorityKey stake's new stake authority
  /// @param withdrawAuthorityKey stake's new withdraw authority
  public static Instruction initializeChecked(final AccountMeta invokedStakeProgramProgramMeta,
                                              final PublicKey stakeKey,
                                              final PublicKey rentKey,
                                              final PublicKey stakeAuthorityKey,
                                              final PublicKey withdrawAuthorityKey) {
    final var keys = initializeCheckedKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      rentKey,
      stakeAuthorityKey,
      withdrawAuthorityKey
    );
    return initializeChecked(invokedStakeProgramProgramMeta, keys);
  }

  public static Instruction initializeChecked(final AccountMeta invokedStakeProgramProgramMeta                                              ,
                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, INITIALIZE_CHECKED_DISCRIMINATOR);
  }

  public static final Discriminator AUTHORIZE_CHECKED_DISCRIMINATOR = toDiscriminator(147, 97, 67, 26, 230, 107, 45, 242);

  /// @param stakeKey The stake account to be updated
  /// @param clockKey Clock sysvar
  /// @param authorityKey stake's current stake or withdraw authority to change away from
  /// @param newAuthorityKey stake's new stake or withdraw authority to change to. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer.
  public static List<AccountMeta> authorizeCheckedKeys(final AccountMeta invokedStakeProgramProgramMeta                                                       ,
                                                       final PublicKey stakeKey,
                                                       final PublicKey clockKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey newAuthorityKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(clockKey),
      createReadOnlySigner(authorityKey),
      createReadOnlySigner(newAuthorityKey)
    );
  }

  /// @param stakeKey The stake account to be updated
  /// @param clockKey Clock sysvar
  /// @param authorityKey stake's current stake or withdraw authority to change away from
  /// @param newAuthorityKey stake's new stake or withdraw authority to change to. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer.
  public static Instruction authorizeChecked(final AccountMeta invokedStakeProgramProgramMeta,
                                             final PublicKey stakeKey,
                                             final PublicKey clockKey,
                                             final PublicKey authorityKey,
                                             final PublicKey newAuthorityKey,
                                             final StakeAuthorize stakeAuthorize) {
    final var keys = authorizeCheckedKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      clockKey,
      authorityKey,
      newAuthorityKey
    );
    return authorizeChecked(invokedStakeProgramProgramMeta, keys, stakeAuthorize);
  }

  public static Instruction authorizeChecked(final AccountMeta invokedStakeProgramProgramMeta                                             ,
                                             final List<AccountMeta> keys,
                                             final StakeAuthorize stakeAuthorize) {
    final byte[] _data = new byte[8 + Borsh.len(stakeAuthorize)];
    int i = AUTHORIZE_CHECKED_DISCRIMINATOR.write(_data, 0);
    Borsh.write(stakeAuthorize, _data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record AuthorizeCheckedIxData(Discriminator discriminator, StakeAuthorize stakeAuthorize) implements Borsh {  

    public static AuthorizeCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static AuthorizeCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var stakeAuthorize = StakeAuthorize.read(_data, i);
      return new AuthorizeCheckedIxData(discriminator, stakeAuthorize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(stakeAuthorize, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator AUTHORIZE_CHECKED_WITH_SEED_DISCRIMINATOR = toDiscriminator(14, 230, 154, 165, 225, 209, 194, 210);

  /// @param stakeKey The stake account to be updated
  /// @param authorityBaseKey Base account of stake's authority to be updated
  /// @param clockKey Clock sysvar
  /// @param newAuthorityKey stake's new stake or withdraw authority to change to. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer.
  public static List<AccountMeta> authorizeCheckedWithSeedKeys(final AccountMeta invokedStakeProgramProgramMeta                                                               ,
                                                               final PublicKey stakeKey,
                                                               final PublicKey authorityBaseKey,
                                                               final PublicKey clockKey,
                                                               final PublicKey newAuthorityKey) {
    return List.of(
      createWrite(stakeKey),
      createReadOnlySigner(authorityBaseKey),
      createRead(clockKey),
      createReadOnlySigner(newAuthorityKey)
    );
  }

  /// @param stakeKey The stake account to be updated
  /// @param authorityBaseKey Base account of stake's authority to be updated
  /// @param clockKey Clock sysvar
  /// @param newAuthorityKey stake's new stake or withdraw authority to change to. If stake Lockup is active, the signing lockup authority must follow if updating withdrawer.
  public static Instruction authorizeCheckedWithSeed(final AccountMeta invokedStakeProgramProgramMeta,
                                                     final PublicKey stakeKey,
                                                     final PublicKey authorityBaseKey,
                                                     final PublicKey clockKey,
                                                     final PublicKey newAuthorityKey,
                                                     final StakeAuthorize stakeAuthorize,
                                                     final String authoritySeed,
                                                     final PublicKey authorityOwner) {
    final var keys = authorizeCheckedWithSeedKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      authorityBaseKey,
      clockKey,
      newAuthorityKey
    );
    return authorizeCheckedWithSeed(
      invokedStakeProgramProgramMeta,
      keys,
      stakeAuthorize,
      authoritySeed,
      authorityOwner
    );
  }

  public static Instruction authorizeCheckedWithSeed(final AccountMeta invokedStakeProgramProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final StakeAuthorize stakeAuthorize,
                                                     final String authoritySeed,
                                                     final PublicKey authorityOwner) {
    final byte[] _authoritySeed = authoritySeed.getBytes(UTF_8);
    final byte[] _data = new byte[44 + Borsh.len(stakeAuthorize) + Borsh.lenVector(_authoritySeed)];
    int i = AUTHORIZE_CHECKED_WITH_SEED_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(stakeAuthorize, _data, i);
    i += Borsh.writeVector(_authoritySeed, _data, i);
    authorityOwner.write(_data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record AuthorizeCheckedWithSeedIxData(Discriminator discriminator,
                                               StakeAuthorize stakeAuthorize,
                                               String authoritySeed, byte[] _authoritySeed,
                                               PublicKey authorityOwner) implements Borsh {  

    public static AuthorizeCheckedWithSeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AuthorizeCheckedWithSeedIxData createRecord(final Discriminator discriminator,
                                                              final StakeAuthorize stakeAuthorize,
                                                              final String authoritySeed,
                                                              final PublicKey authorityOwner) {
      return new AuthorizeCheckedWithSeedIxData(discriminator, stakeAuthorize, authoritySeed, authoritySeed.getBytes(UTF_8), authorityOwner);
    }

    public static AuthorizeCheckedWithSeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var stakeAuthorize = StakeAuthorize.read(_data, i);
      i += Borsh.len(stakeAuthorize);
      final var authoritySeed = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var authorityOwner = readPubKey(_data, i);
      return new AuthorizeCheckedWithSeedIxData(discriminator, stakeAuthorize, authoritySeed, authoritySeed.getBytes(UTF_8), authorityOwner);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(stakeAuthorize, _data, i);
      i += Borsh.writeVector(_authoritySeed, _data, i);
      authorityOwner.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(stakeAuthorize) + Borsh.lenVector(_authoritySeed) + 32;
    }
  }

  public static final Discriminator SET_LOCKUP_CHECKED_DISCRIMINATOR = toDiscriminator(22, 158, 12, 183, 118, 94, 156, 255);

  /// @param stakeKey The stake account to set the lockup of
  /// @param authorityKey stake's withdraw authority or lockup authority if lockup is active. If setting a new lockup authority, the signing new lockup authority must follow.
  public static List<AccountMeta> setLockupCheckedKeys(final AccountMeta invokedStakeProgramProgramMeta                                                       ,
                                                       final PublicKey stakeKey,
                                                       final PublicKey authorityKey) {
    return List.of(
      createWrite(stakeKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// @param stakeKey The stake account to set the lockup of
  /// @param authorityKey stake's withdraw authority or lockup authority if lockup is active. If setting a new lockup authority, the signing new lockup authority must follow.
  public static Instruction setLockupChecked(final AccountMeta invokedStakeProgramProgramMeta,
                                             final PublicKey stakeKey,
                                             final PublicKey authorityKey,
                                             final OptionalLong unixTimestamp,
                                             final OptionalLong epoch) {
    final var keys = setLockupCheckedKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      authorityKey
    );
    return setLockupChecked(invokedStakeProgramProgramMeta, keys, unixTimestamp, epoch);
  }

  public static Instruction setLockupChecked(final AccountMeta invokedStakeProgramProgramMeta                                             ,
                                             final List<AccountMeta> keys,
                                             final OptionalLong unixTimestamp,
                                             final OptionalLong epoch) {
    final byte[] _data = new byte[
    8
    + (unixTimestamp == null || unixTimestamp.isEmpty() ? 1 : 9)
    + (epoch == null || epoch.isEmpty() ? 1 : 9)
    ];
    int i = SET_LOCKUP_CHECKED_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(unixTimestamp, _data, i);
    Borsh.writeOptional(epoch, _data, i);

    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, _data);
  }

  public record SetLockupCheckedIxData(Discriminator discriminator, OptionalLong unixTimestamp, OptionalLong epoch) implements Borsh {  

    public static SetLockupCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SetLockupCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalLong unixTimestamp;
      if (_data[i] == 0) {
        unixTimestamp = OptionalLong.empty();
        ++i;
      } else {
        ++i;
        unixTimestamp = OptionalLong.of(getInt64LE(_data, i));
        i += 8;
      }
      final OptionalLong epoch;
      if (_data[i] == 0) {
        epoch = OptionalLong.empty();
      } else {
        ++i;
        epoch = OptionalLong.of(getInt64LE(_data, i));
      }
      return new SetLockupCheckedIxData(discriminator, unixTimestamp, epoch);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(unixTimestamp, _data, i);
      i += Borsh.writeOptional(epoch, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (unixTimestamp == null || unixTimestamp.isEmpty() ? 1 : (1 + 8)) + (epoch == null || epoch.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator GET_MINIMUM_DELEGATION_DISCRIMINATOR = toDiscriminator(197, 65, 7, 73, 151, 105, 133, 105);

  public static Instruction getMinimumDelegation(final AccountMeta invokedStakeProgramProgramMeta) {
    return Instruction.createInstruction(invokedStakeProgramProgramMeta, List.of(), GET_MINIMUM_DELEGATION_DISCRIMINATOR);
  }

  public static final Discriminator DEACTIVATE_DELINQUENT_DISCRIMINATOR = toDiscriminator(6, 113, 198, 138, 228, 163, 159, 221);

  /// @param stakeKey The delinquent stake account to deactivate
  /// @param voteKey stake's delinquent vote account
  /// @param referenceVoteKey Reference vote account that has voted at least once in the last MINIMUM_DELINQUENT_EPOCHS_FOR_DEACTIVATION epochs
  public static List<AccountMeta> deactivateDelinquentKeys(final AccountMeta invokedStakeProgramProgramMeta                                                           ,
                                                           final PublicKey stakeKey,
                                                           final PublicKey voteKey,
                                                           final PublicKey referenceVoteKey) {
    return List.of(
      createWrite(stakeKey),
      createRead(voteKey),
      createRead(referenceVoteKey)
    );
  }

  /// @param stakeKey The delinquent stake account to deactivate
  /// @param voteKey stake's delinquent vote account
  /// @param referenceVoteKey Reference vote account that has voted at least once in the last MINIMUM_DELINQUENT_EPOCHS_FOR_DEACTIVATION epochs
  public static Instruction deactivateDelinquent(final AccountMeta invokedStakeProgramProgramMeta,
                                                 final PublicKey stakeKey,
                                                 final PublicKey voteKey,
                                                 final PublicKey referenceVoteKey) {
    final var keys = deactivateDelinquentKeys(
      invokedStakeProgramProgramMeta,
      stakeKey,
      voteKey,
      referenceVoteKey
    );
    return deactivateDelinquent(invokedStakeProgramProgramMeta, keys);
  }

  public static Instruction deactivateDelinquent(final AccountMeta invokedStakeProgramProgramMeta                                                 ,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedStakeProgramProgramMeta, keys, DEACTIVATE_DELINQUENT_DISCRIMINATOR);
  }

  private StakeProgramProgram() {
  }
}
