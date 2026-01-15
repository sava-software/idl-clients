package software.sava.idl.clients.spl.system.gen;

import java.lang.String;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class SystemProgram {

  public static final Discriminator CREATE_ACCOUNT_DISCRIMINATOR = toDiscriminator(0, 0, 0, 0);

  public static List<AccountMeta> createAccountKeys(final PublicKey payerKey,
                                                    final PublicKey newAccountKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWritableSigner(newAccountKey)
    );
  }

  public static Instruction createAccount(final AccountMeta invokedSystemProgramMeta,
                                          final PublicKey payerKey,
                                          final PublicKey newAccountKey,
                                          final long lamports,
                                          final long space,
                                          final PublicKey programAddress) {
    final var keys = createAccountKeys(
      payerKey,
      newAccountKey
    );
    return createAccount(
      invokedSystemProgramMeta,
      keys,
      lamports,
      space,
      programAddress
    );
  }

  public static Instruction createAccount(final AccountMeta invokedSystemProgramMeta,
                                          final List<AccountMeta> keys,
                                          final long lamports,
                                          final long space,
                                          final PublicKey programAddress) {
    final byte[] _data = new byte[52];
    int i = CREATE_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, lamports);
    i += 8;
    putInt64LE(_data, i, space);
    i += 8;
    programAddress.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record CreateAccountIxData(int discriminator,
                                    long lamports,
                                    long space,
                                    PublicKey programAddress) implements SerDe {  

    public static CreateAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 52;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int LAMPORTS_OFFSET = 4;
    public static final int SPACE_OFFSET = 12;
    public static final int PROGRAM_ADDRESS_OFFSET = 20;

    public static CreateAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var lamports = getInt64LE(_data, i);
      i += 8;
      final var space = getInt64LE(_data, i);
      i += 8;
      final var programAddress = readPubKey(_data, i);
      return new CreateAccountIxData(discriminator,
                                     lamports,
                                     space,
                                     programAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      putInt64LE(_data, i, lamports);
      i += 8;
      putInt64LE(_data, i, space);
      i += 8;
      programAddress.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ASSIGN_DISCRIMINATOR = toDiscriminator(1, 0, 0, 0);

  public static List<AccountMeta> assignKeys(final PublicKey accountKey) {
    return List.of(
      createWritableSigner(accountKey)
    );
  }

  public static Instruction assign(final AccountMeta invokedSystemProgramMeta,
                                   final PublicKey accountKey,
                                   final PublicKey programAddress) {
    final var keys = assignKeys(
      accountKey
    );
    return assign(invokedSystemProgramMeta, keys, programAddress);
  }

  public static Instruction assign(final AccountMeta invokedSystemProgramMeta,
                                   final List<AccountMeta> keys,
                                   final PublicKey programAddress) {
    final byte[] _data = new byte[36];
    int i = ASSIGN_DISCRIMINATOR.write(_data, 0);
    programAddress.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record AssignIxData(int discriminator, PublicKey programAddress) implements SerDe {  

    public static AssignIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 36;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PROGRAM_ADDRESS_OFFSET = 4;

    public static AssignIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var programAddress = readPubKey(_data, i);
      return new AssignIxData(discriminator, programAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      programAddress.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_SOL_DISCRIMINATOR = toDiscriminator(2, 0, 0, 0);

  public static List<AccountMeta> transferSolKeys(final PublicKey sourceKey,
                                                  final PublicKey destinationKey) {
    return List.of(
      createWritableSigner(sourceKey),
      createWrite(destinationKey)
    );
  }

  public static Instruction transferSol(final AccountMeta invokedSystemProgramMeta,
                                        final PublicKey sourceKey,
                                        final PublicKey destinationKey,
                                        final long amount) {
    final var keys = transferSolKeys(
      sourceKey,
      destinationKey
    );
    return transferSol(invokedSystemProgramMeta, keys, amount);
  }

  public static Instruction transferSol(final AccountMeta invokedSystemProgramMeta,
                                        final List<AccountMeta> keys,
                                        final long amount) {
    final byte[] _data = new byte[12];
    int i = TRANSFER_SOL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record TransferSolIxData(int discriminator, long amount) implements SerDe {  

    public static TransferSolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 4;

    public static TransferSolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var amount = getInt64LE(_data, i);
      return new TransferSolIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_ACCOUNT_WITH_SEED_DISCRIMINATOR = toDiscriminator(3, 0, 0, 0);

  public static List<AccountMeta> createAccountWithSeedKeys(final PublicKey payerKey,
                                                            final PublicKey newAccountKey,
                                                            final PublicKey baseAccountKey) {
    final var keys = new ArrayList<AccountMeta>(3);
    keys.add(createWritableSigner(payerKey));
    keys.add(createWrite(newAccountKey));
    if (baseAccountKey != null) {
      keys.add(createReadOnlySigner(baseAccountKey));
    }
    return keys;
  }

  public static Instruction createAccountWithSeed(final AccountMeta invokedSystemProgramMeta,
                                                  final PublicKey payerKey,
                                                  final PublicKey newAccountKey,
                                                  final PublicKey baseAccountKey,
                                                  final PublicKey base,
                                                  final String seed,
                                                  final long amount,
                                                  final long space,
                                                  final PublicKey programAddress) {
    final var keys = createAccountWithSeedKeys(
      payerKey,
      newAccountKey,
      baseAccountKey
    );
    return createAccountWithSeed(
      invokedSystemProgramMeta,
      keys,
      base,
      seed,
      amount,
      space,
      programAddress
    );
  }

  public static Instruction createAccountWithSeed(final AccountMeta invokedSystemProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final PublicKey base,
                                                  final String seed,
                                                  final long amount,
                                                  final long space,
                                                  final PublicKey programAddress) {
    final byte[] _seed = seed.getBytes(UTF_8);
    final byte[] _data = new byte[92 + _seed.length];
    int i = CREATE_ACCOUNT_WITH_SEED_DISCRIMINATOR.write(_data, 0);
    base.write(_data, i);
    i += 32;
    putInt64LE(_data, i, _seed.length);
    i += 8;
    System.arraycopy(_seed, 0, _data, i, _seed.length);
    i += _seed.length;
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, space);
    i += 8;
    programAddress.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record CreateAccountWithSeedIxData(int discriminator,
                                            PublicKey base,
                                            String seed, byte[] _seed,
                                            long amount,
                                            long space,
                                            PublicKey programAddress) implements SerDe {  

    public static CreateAccountWithSeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int BASE_OFFSET = 4;
    public static final int SEED_OFFSET = 36;

    public static CreateAccountWithSeedIxData createRecord(final int discriminator,
                                                           final PublicKey base,
                                                           final String seed,
                                                           final long amount,
                                                           final long space,
                                                           final PublicKey programAddress) {
      return new CreateAccountWithSeedIxData(discriminator,
                                             base,
                                             seed, seed.getBytes(UTF_8),
                                             amount,
                                             space,
                                             programAddress);
    }

    public static CreateAccountWithSeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var base = readPubKey(_data, i);
      i += 32;
      final int _seedLength = Math.toIntExact(getInt64LE(_data, i));
      i += 8;
      final byte[] _seed = Arrays.copyOfRange(_data, i, i + _seedLength);
      final var seed = new String(_seed, UTF_8);
      i += _seedLength;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var space = getInt64LE(_data, i);
      i += 8;
      final var programAddress = readPubKey(_data, i);
      return new CreateAccountWithSeedIxData(discriminator,
                                             base,
                                             seed, _seed,
                                             amount,
                                             space,
                                             programAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      base.write(_data, i);
      i += 32;
      putInt64LE(_data, i, _seed.length);
      i += 8;
      System.arraycopy(_seed, 0, _data, i, _seed.length);
      i += _seed.length;
      putInt64LE(_data, i, amount);
      i += 8;
      putInt64LE(_data, i, space);
      i += 8;
      programAddress.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 4
           + 32
           + _seed.length
           + 8
           + 8
           + 32;
    }
  }

  public static final Discriminator ADVANCE_NONCE_ACCOUNT_DISCRIMINATOR = toDiscriminator(4, 0, 0, 0);

  public static List<AccountMeta> advanceNonceAccountKeys(final SolanaAccounts solanaAccounts,
                                                          final PublicKey nonceAccountKey,
                                                          final PublicKey nonceAuthorityKey) {
    return List.of(
      createWrite(nonceAccountKey),
      createRead(solanaAccounts.recentBlockhashesSysVar()),
      createReadOnlySigner(nonceAuthorityKey)
    );
  }

  public static Instruction advanceNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                final SolanaAccounts solanaAccounts,
                                                final PublicKey nonceAccountKey,
                                                final PublicKey nonceAuthorityKey) {
    final var keys = advanceNonceAccountKeys(
      solanaAccounts,
      nonceAccountKey,
      nonceAuthorityKey
    );
    return advanceNonceAccount(invokedSystemProgramMeta, keys);
  }

  public static Instruction advanceNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                final List<AccountMeta> keys) {
    final byte[] _data = new byte[4];
    ADVANCE_NONCE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record AdvanceNonceAccountIxData(int discriminator) implements SerDe {  

    public static AdvanceNonceAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 4;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static AdvanceNonceAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = getInt32LE(_data, _offset);
      return new AdvanceNonceAccountIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_NONCE_ACCOUNT_DISCRIMINATOR = toDiscriminator(5, 0, 0, 0);

  public static List<AccountMeta> withdrawNonceAccountKeys(final SolanaAccounts solanaAccounts,
                                                           final PublicKey nonceAccountKey,
                                                           final PublicKey recipientAccountKey,
                                                           final PublicKey nonceAuthorityKey) {
    return List.of(
      createWrite(nonceAccountKey),
      createWrite(recipientAccountKey),
      createRead(solanaAccounts.recentBlockhashesSysVar()),
      createRead(solanaAccounts.rentSysVar()),
      createReadOnlySigner(nonceAuthorityKey)
    );
  }

  public static Instruction withdrawNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                 final SolanaAccounts solanaAccounts,
                                                 final PublicKey nonceAccountKey,
                                                 final PublicKey recipientAccountKey,
                                                 final PublicKey nonceAuthorityKey,
                                                 final long withdrawAmount) {
    final var keys = withdrawNonceAccountKeys(
      solanaAccounts,
      nonceAccountKey,
      recipientAccountKey,
      nonceAuthorityKey
    );
    return withdrawNonceAccount(invokedSystemProgramMeta, keys, withdrawAmount);
  }

  public static Instruction withdrawNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long withdrawAmount) {
    final byte[] _data = new byte[12];
    int i = WITHDRAW_NONCE_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, withdrawAmount);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record WithdrawNonceAccountIxData(int discriminator, long withdrawAmount) implements SerDe {  

    public static WithdrawNonceAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int WITHDRAW_AMOUNT_OFFSET = 4;

    public static WithdrawNonceAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var withdrawAmount = getInt64LE(_data, i);
      return new WithdrawNonceAccountIxData(discriminator, withdrawAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      putInt64LE(_data, i, withdrawAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_NONCE_ACCOUNT_DISCRIMINATOR = toDiscriminator(6, 0, 0, 0);

  public static List<AccountMeta> initializeNonceAccountKeys(final SolanaAccounts solanaAccounts,
                                                             final PublicKey nonceAccountKey) {
    return List.of(
      createWrite(nonceAccountKey),
      createRead(solanaAccounts.recentBlockhashesSysVar()),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  public static Instruction initializeNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                   final SolanaAccounts solanaAccounts,
                                                   final PublicKey nonceAccountKey,
                                                   final PublicKey nonceAuthority) {
    final var keys = initializeNonceAccountKeys(
      solanaAccounts,
      nonceAccountKey
    );
    return initializeNonceAccount(invokedSystemProgramMeta, keys, nonceAuthority);
  }

  public static Instruction initializeNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final PublicKey nonceAuthority) {
    final byte[] _data = new byte[36];
    int i = INITIALIZE_NONCE_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    nonceAuthority.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record InitializeNonceAccountIxData(int discriminator, PublicKey nonceAuthority) implements SerDe {  

    public static InitializeNonceAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 36;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int NONCE_AUTHORITY_OFFSET = 4;

    public static InitializeNonceAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var nonceAuthority = readPubKey(_data, i);
      return new InitializeNonceAccountIxData(discriminator, nonceAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      nonceAuthority.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator AUTHORIZE_NONCE_ACCOUNT_DISCRIMINATOR = toDiscriminator(7, 0, 0, 0);

  public static List<AccountMeta> authorizeNonceAccountKeys(final PublicKey nonceAccountKey,
                                                            final PublicKey nonceAuthorityKey) {
    return List.of(
      createWrite(nonceAccountKey),
      createReadOnlySigner(nonceAuthorityKey)
    );
  }

  public static Instruction authorizeNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                  final PublicKey nonceAccountKey,
                                                  final PublicKey nonceAuthorityKey,
                                                  final PublicKey newNonceAuthority) {
    final var keys = authorizeNonceAccountKeys(
      nonceAccountKey,
      nonceAuthorityKey
    );
    return authorizeNonceAccount(invokedSystemProgramMeta, keys, newNonceAuthority);
  }

  public static Instruction authorizeNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final PublicKey newNonceAuthority) {
    final byte[] _data = new byte[36];
    int i = AUTHORIZE_NONCE_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    newNonceAuthority.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record AuthorizeNonceAccountIxData(int discriminator, PublicKey newNonceAuthority) implements SerDe {  

    public static AuthorizeNonceAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 36;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int NEW_NONCE_AUTHORITY_OFFSET = 4;

    public static AuthorizeNonceAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var newNonceAuthority = readPubKey(_data, i);
      return new AuthorizeNonceAccountIxData(discriminator, newNonceAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      newNonceAuthority.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ALLOCATE_DISCRIMINATOR = toDiscriminator(8, 0, 0, 0);

  public static List<AccountMeta> allocateKeys(final PublicKey newAccountKey) {
    return List.of(
      createWritableSigner(newAccountKey)
    );
  }

  public static Instruction allocate(final AccountMeta invokedSystemProgramMeta,
                                     final PublicKey newAccountKey,
                                     final long space) {
    final var keys = allocateKeys(
      newAccountKey
    );
    return allocate(invokedSystemProgramMeta, keys, space);
  }

  public static Instruction allocate(final AccountMeta invokedSystemProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long space) {
    final byte[] _data = new byte[12];
    int i = ALLOCATE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, space);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record AllocateIxData(int discriminator, long space) implements SerDe {  

    public static AllocateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int SPACE_OFFSET = 4;

    public static AllocateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var space = getInt64LE(_data, i);
      return new AllocateIxData(discriminator, space);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      putInt64LE(_data, i, space);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ALLOCATE_WITH_SEED_DISCRIMINATOR = toDiscriminator(9, 0, 0, 0);

  public static List<AccountMeta> allocateWithSeedKeys(final PublicKey newAccountKey,
                                                       final PublicKey baseAccountKey) {
    return List.of(
      createWrite(newAccountKey),
      createReadOnlySigner(baseAccountKey)
    );
  }

  public static Instruction allocateWithSeed(final AccountMeta invokedSystemProgramMeta,
                                             final PublicKey newAccountKey,
                                             final PublicKey baseAccountKey,
                                             final PublicKey base,
                                             final String seed,
                                             final long space,
                                             final PublicKey programAddress) {
    final var keys = allocateWithSeedKeys(
      newAccountKey,
      baseAccountKey
    );
    return allocateWithSeed(
      invokedSystemProgramMeta,
      keys,
      base,
      seed,
      space,
      programAddress
    );
  }

  public static Instruction allocateWithSeed(final AccountMeta invokedSystemProgramMeta,
                                             final List<AccountMeta> keys,
                                             final PublicKey base,
                                             final String seed,
                                             final long space,
                                             final PublicKey programAddress) {
    final byte[] _seed = seed.getBytes(UTF_8);
    final byte[] _data = new byte[84 + _seed.length];
    int i = ALLOCATE_WITH_SEED_DISCRIMINATOR.write(_data, 0);
    base.write(_data, i);
    i += 32;
    putInt64LE(_data, i, _seed.length);
    i += 8;
    System.arraycopy(_seed, 0, _data, i, _seed.length);
    i += _seed.length;
    putInt64LE(_data, i, space);
    i += 8;
    programAddress.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record AllocateWithSeedIxData(int discriminator,
                                       PublicKey base,
                                       String seed, byte[] _seed,
                                       long space,
                                       PublicKey programAddress) implements SerDe {  

    public static AllocateWithSeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int BASE_OFFSET = 4;
    public static final int SEED_OFFSET = 36;

    public static AllocateWithSeedIxData createRecord(final int discriminator,
                                                      final PublicKey base,
                                                      final String seed,
                                                      final long space,
                                                      final PublicKey programAddress) {
      return new AllocateWithSeedIxData(discriminator,
                                        base,
                                        seed, seed.getBytes(UTF_8),
                                        space,
                                        programAddress);
    }

    public static AllocateWithSeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var base = readPubKey(_data, i);
      i += 32;
      final int _seedLength = Math.toIntExact(getInt64LE(_data, i));
      i += 8;
      final byte[] _seed = Arrays.copyOfRange(_data, i, i + _seedLength);
      final var seed = new String(_seed, UTF_8);
      i += _seedLength;
      final var space = getInt64LE(_data, i);
      i += 8;
      final var programAddress = readPubKey(_data, i);
      return new AllocateWithSeedIxData(discriminator,
                                        base,
                                        seed, _seed,
                                        space,
                                        programAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      base.write(_data, i);
      i += 32;
      putInt64LE(_data, i, _seed.length);
      i += 8;
      System.arraycopy(_seed, 0, _data, i, _seed.length);
      i += _seed.length;
      putInt64LE(_data, i, space);
      i += 8;
      programAddress.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 4
           + 32
           + _seed.length
           + 8
           + 32;
    }
  }

  public static final Discriminator ASSIGN_WITH_SEED_DISCRIMINATOR = toDiscriminator(10, 0, 0, 0);

  public static List<AccountMeta> assignWithSeedKeys(final PublicKey accountKey,
                                                     final PublicKey baseAccountKey) {
    return List.of(
      createWrite(accountKey),
      createReadOnlySigner(baseAccountKey)
    );
  }

  public static Instruction assignWithSeed(final AccountMeta invokedSystemProgramMeta,
                                           final PublicKey accountKey,
                                           final PublicKey baseAccountKey,
                                           final PublicKey base,
                                           final String seed,
                                           final PublicKey programAddress) {
    final var keys = assignWithSeedKeys(
      accountKey,
      baseAccountKey
    );
    return assignWithSeed(
      invokedSystemProgramMeta,
      keys,
      base,
      seed,
      programAddress
    );
  }

  public static Instruction assignWithSeed(final AccountMeta invokedSystemProgramMeta,
                                           final List<AccountMeta> keys,
                                           final PublicKey base,
                                           final String seed,
                                           final PublicKey programAddress) {
    final byte[] _seed = seed.getBytes(UTF_8);
    final byte[] _data = new byte[76 + _seed.length];
    int i = ASSIGN_WITH_SEED_DISCRIMINATOR.write(_data, 0);
    base.write(_data, i);
    i += 32;
    putInt64LE(_data, i, _seed.length);
    i += 8;
    System.arraycopy(_seed, 0, _data, i, _seed.length);
    i += _seed.length;
    programAddress.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record AssignWithSeedIxData(int discriminator,
                                     PublicKey base,
                                     String seed, byte[] _seed,
                                     PublicKey programAddress) implements SerDe {  

    public static AssignWithSeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int BASE_OFFSET = 4;
    public static final int SEED_OFFSET = 36;

    public static AssignWithSeedIxData createRecord(final int discriminator,
                                                    final PublicKey base,
                                                    final String seed,
                                                    final PublicKey programAddress) {
      return new AssignWithSeedIxData(discriminator,
                                      base,
                                      seed, seed.getBytes(UTF_8),
                                      programAddress);
    }

    public static AssignWithSeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var base = readPubKey(_data, i);
      i += 32;
      final int _seedLength = Math.toIntExact(getInt64LE(_data, i));
      i += 8;
      final byte[] _seed = Arrays.copyOfRange(_data, i, i + _seedLength);
      final var seed = new String(_seed, UTF_8);
      i += _seedLength;
      final var programAddress = readPubKey(_data, i);
      return new AssignWithSeedIxData(discriminator,
                                      base,
                                      seed, _seed,
                                      programAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      base.write(_data, i);
      i += 32;
      putInt64LE(_data, i, _seed.length);
      i += 8;
      System.arraycopy(_seed, 0, _data, i, _seed.length);
      i += _seed.length;
      programAddress.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 4 + 32 + _seed.length + 32;
    }
  }

  public static final Discriminator TRANSFER_SOL_WITH_SEED_DISCRIMINATOR = toDiscriminator(11, 0, 0, 0);

  public static List<AccountMeta> transferSolWithSeedKeys(final PublicKey sourceKey,
                                                          final PublicKey baseAccountKey,
                                                          final PublicKey destinationKey) {
    return List.of(
      createWrite(sourceKey),
      createReadOnlySigner(baseAccountKey),
      createWrite(destinationKey)
    );
  }

  public static Instruction transferSolWithSeed(final AccountMeta invokedSystemProgramMeta,
                                                final PublicKey sourceKey,
                                                final PublicKey baseAccountKey,
                                                final PublicKey destinationKey,
                                                final long amount,
                                                final String fromSeed,
                                                final PublicKey fromOwner) {
    final var keys = transferSolWithSeedKeys(
      sourceKey,
      baseAccountKey,
      destinationKey
    );
    return transferSolWithSeed(
      invokedSystemProgramMeta,
      keys,
      amount,
      fromSeed,
      fromOwner
    );
  }

  public static Instruction transferSolWithSeed(final AccountMeta invokedSystemProgramMeta,
                                                final List<AccountMeta> keys,
                                                final long amount,
                                                final String fromSeed,
                                                final PublicKey fromOwner) {
    final byte[] _fromSeed = fromSeed.getBytes(UTF_8);
    final byte[] _data = new byte[52 + _fromSeed.length];
    int i = TRANSFER_SOL_WITH_SEED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    putInt64LE(_data, i, _fromSeed.length);
    i += 8;
    System.arraycopy(_fromSeed, 0, _data, i, _fromSeed.length);
    i += _fromSeed.length;
    fromOwner.write(_data, i);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record TransferSolWithSeedIxData(int discriminator,
                                          long amount,
                                          String fromSeed, byte[] _fromSeed,
                                          PublicKey fromOwner) implements SerDe {  

    public static TransferSolWithSeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 4;
    public static final int FROM_SEED_OFFSET = 12;

    public static TransferSolWithSeedIxData createRecord(final int discriminator,
                                                         final long amount,
                                                         final String fromSeed,
                                                         final PublicKey fromOwner) {
      return new TransferSolWithSeedIxData(discriminator,
                                           amount,
                                           fromSeed, fromSeed.getBytes(UTF_8),
                                           fromOwner);
    }

    public static TransferSolWithSeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = getInt32LE(_data, i);
      i += 4;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final int _fromSeedLength = Math.toIntExact(getInt64LE(_data, i));
      i += 8;
      final byte[] _fromSeed = Arrays.copyOfRange(_data, i, i + _fromSeedLength);
      final var fromSeed = new String(_fromSeed, UTF_8);
      i += _fromSeedLength;
      final var fromOwner = readPubKey(_data, i);
      return new TransferSolWithSeedIxData(discriminator,
                                           amount,
                                           fromSeed, _fromSeed,
                                           fromOwner);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      putInt64LE(_data, i, amount);
      i += 8;
      putInt64LE(_data, i, _fromSeed.length);
      i += 8;
      System.arraycopy(_fromSeed, 0, _data, i, _fromSeed.length);
      i += _fromSeed.length;
      fromOwner.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return 4 + 8 + _fromSeed.length + 32;
    }
  }

  public static final Discriminator UPGRADE_NONCE_ACCOUNT_DISCRIMINATOR = toDiscriminator(12, 0, 0, 0);

  public static List<AccountMeta> upgradeNonceAccountKeys(final PublicKey nonceAccountKey) {
    return List.of(
      createWrite(nonceAccountKey)
    );
  }

  public static Instruction upgradeNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                final PublicKey nonceAccountKey) {
    final var keys = upgradeNonceAccountKeys(
      nonceAccountKey
    );
    return upgradeNonceAccount(invokedSystemProgramMeta, keys);
  }

  public static Instruction upgradeNonceAccount(final AccountMeta invokedSystemProgramMeta,
                                                final List<AccountMeta> keys) {
    final byte[] _data = new byte[4];
    UPGRADE_NONCE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedSystemProgramMeta, keys, _data);
  }

  public record UpgradeNonceAccountIxData(int discriminator) implements SerDe {  

    public static UpgradeNonceAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 4;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static UpgradeNonceAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = getInt32LE(_data, _offset);
      return new UpgradeNonceAccountIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      putInt32LE(_data, i, discriminator);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  private SystemProgram() {
  }
}
