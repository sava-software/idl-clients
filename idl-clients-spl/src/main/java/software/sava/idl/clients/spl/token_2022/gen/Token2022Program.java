package software.sava.idl.clients.spl.token_2022.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.spl.token_2022.gen.types.AccountState;
import software.sava.idl.clients.spl.token_2022.gen.types.AuthorityType;
import software.sava.idl.clients.spl.token_2022.gen.types.DecryptableBalance;
import software.sava.idl.clients.spl.token_2022.gen.types.ExtensionType;

import static java.nio.charset.StandardCharsets.UTF_8;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getFloat64LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putFloat64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.toDiscriminator;
import static software.sava.idl.clients.core.gen.SerDeUtil.checkMaxLength;

public final class Token2022Program {

  public static final Discriminator INITIALIZE_MINT_DISCRIMINATOR = toDiscriminator(0);

  /// Initializes a new mint and optionally deposits all the newly minted
  /// tokens in an account.
  /// 
  /// The `InitializeMint` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param mintKey Token mint account.
  public static List<AccountMeta> initializeMintKeys(final SolanaAccounts solanaAccounts,
                                                     final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  /// Initializes a new mint and optionally deposits all the newly minted
  /// tokens in an account.
  /// 
  /// The `InitializeMint` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param mintKey Token mint account.
  /// @param decimals Number of decimals in token account amounts.
  /// @param mintAuthority Minting authority.
  /// @param freezeAuthority Optional authority that can freeze token accounts.
  public static Instruction initializeMint(final AccountMeta invokedToken2022ProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey mintKey,
                                           final int decimals,
                                           final PublicKey mintAuthority,
                                           final PublicKey freezeAuthority) {
    final var keys = initializeMintKeys(
      solanaAccounts,
      mintKey
    );
    return initializeMint(
      invokedToken2022ProgramMeta,
      keys,
      decimals,
      mintAuthority,
      freezeAuthority
    );
  }

  /// Initializes a new mint and optionally deposits all the newly minted
  /// tokens in an account.
  /// 
  /// The `InitializeMint` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param decimals Number of decimals in token account amounts.
  /// @param mintAuthority Minting authority.
  /// @param freezeAuthority Optional authority that can freeze token accounts.
  public static Instruction initializeMint(final AccountMeta invokedToken2022ProgramMeta,
                                           final List<AccountMeta> keys,
                                           final int decimals,
                                           final PublicKey mintAuthority,
                                           final PublicKey freezeAuthority) {
    final byte[] _data = new byte[
    34
    + (freezeAuthority == null ? 1 : 33)
    ];
    int i = INITIALIZE_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) decimals;
    ++i;
    mintAuthority.write(_data, i);
    i += 32;
    SerDeUtil.writeOptional(1, freezeAuthority, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initializes a new mint and optionally deposits all the newly minted
  /// tokens in an account.
  /// 
  /// The `InitializeMint` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param decimals Number of decimals in token account amounts.
  /// @param mintAuthority Minting authority.
  /// @param freezeAuthority Optional authority that can freeze token accounts.
  public record InitializeMintIxData(int discriminator,
                                     int decimals,
                                     PublicKey mintAuthority,
                                     PublicKey freezeAuthority) implements SerDe {  

    public static InitializeMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int DECIMALS_OFFSET = 1;
    public static final int MINT_AUTHORITY_OFFSET = 2;
    public static final int FREEZE_AUTHORITY_OFFSET = 35;

    public static InitializeMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var decimals = _data[i] & 0xFF;
      ++i;
      final var mintAuthority = readPubKey(_data, i);
      i += 32;
      final PublicKey freezeAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        freezeAuthority = null;
      } else {
        ++i;
        freezeAuthority = readPubKey(_data, i);
      }
      return new InitializeMintIxData(discriminator,
                                      decimals,
                                      mintAuthority,
                                      freezeAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) decimals;
      ++i;
      mintAuthority.write(_data, i);
      i += 32;
      i += SerDeUtil.writeOptional(1, freezeAuthority, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 1 + 32 + (freezeAuthority == null ? 1 : (1 + 32));
    }
  }

  public static final Discriminator INITIALIZE_ACCOUNT_DISCRIMINATOR = toDiscriminator(1);

  /// Initializes a new account to hold tokens. If this account is associated
  /// with the native mint then the token balance of the initialized account
  /// will be equal to the amount of SOL in the account. If this account is
  /// associated with another mint, that mint must be initialized before this
  /// command can succeed.
  /// 
  /// The `InitializeAccount` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param accountKey The account to initialize.
  /// @param mintKey The mint this account will be associated with.
  /// @param ownerKey The new account's owner/multisignature.
  public static List<AccountMeta> initializeAccountKeys(final SolanaAccounts solanaAccounts,
                                                        final PublicKey accountKey,
                                                        final PublicKey mintKey,
                                                        final PublicKey ownerKey) {
    return List.of(
      createWrite(accountKey),
      createRead(mintKey),
      createRead(ownerKey),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  /// Initializes a new account to hold tokens. If this account is associated
  /// with the native mint then the token balance of the initialized account
  /// will be equal to the amount of SOL in the account. If this account is
  /// associated with another mint, that mint must be initialized before this
  /// command can succeed.
  /// 
  /// The `InitializeAccount` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param accountKey The account to initialize.
  /// @param mintKey The mint this account will be associated with.
  /// @param ownerKey The new account's owner/multisignature.
  public static Instruction initializeAccount(final AccountMeta invokedToken2022ProgramMeta,
                                              final SolanaAccounts solanaAccounts,
                                              final PublicKey accountKey,
                                              final PublicKey mintKey,
                                              final PublicKey ownerKey) {
    final var keys = initializeAccountKeys(
      solanaAccounts,
      accountKey,
      mintKey,
      ownerKey
    );
    return initializeAccount(invokedToken2022ProgramMeta, keys);
  }

  /// Initializes a new account to hold tokens. If this account is associated
  /// with the native mint then the token balance of the initialized account
  /// will be equal to the amount of SOL in the account. If this account is
  /// associated with another mint, that mint must be initialized before this
  /// command can succeed.
  /// 
  /// The `InitializeAccount` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  public static Instruction initializeAccount(final AccountMeta invokedToken2022ProgramMeta,
                                              final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    INITIALIZE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initializes a new account to hold tokens. If this account is associated
  /// with the native mint then the token balance of the initialized account
  /// will be equal to the amount of SOL in the account. If this account is
  /// associated with another mint, that mint must be initialized before this
  /// command can succeed.
  /// 
  /// The `InitializeAccount` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  public record InitializeAccountIxData(int discriminator) implements SerDe {  

    public static InitializeAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static InitializeAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new InitializeAccountIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_MULTISIG_DISCRIMINATOR = toDiscriminator(2);

  /// Initializes a multisignature account with N provided signers.
  /// 
  /// Multisignature accounts can used in place of any single owner/delegate
  /// accounts in any token instruction that require an owner/delegate to be
  /// present. The variant field represents the number of signers (M)
  /// required to validate this multisignature account.
  /// 
  /// The `InitializeMultisig` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param multisigKey The multisignature account to initialize.
  public static List<AccountMeta> initializeMultisigKeys(final SolanaAccounts solanaAccounts,
                                                         final PublicKey multisigKey) {
    return List.of(
      createWrite(multisigKey),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  /// Initializes a multisignature account with N provided signers.
  /// 
  /// Multisignature accounts can used in place of any single owner/delegate
  /// accounts in any token instruction that require an owner/delegate to be
  /// present. The variant field represents the number of signers (M)
  /// required to validate this multisignature account.
  /// 
  /// The `InitializeMultisig` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param multisigKey The multisignature account to initialize.
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig(final AccountMeta invokedToken2022ProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey multisigKey,
                                               final int m) {
    final var keys = initializeMultisigKeys(
      solanaAccounts,
      multisigKey
    );
    return initializeMultisig(invokedToken2022ProgramMeta, keys, m);
  }

  /// Initializes a multisignature account with N provided signers.
  /// 
  /// Multisignature accounts can used in place of any single owner/delegate
  /// accounts in any token instruction that require an owner/delegate to be
  /// present. The variant field represents the number of signers (M)
  /// required to validate this multisignature account.
  /// 
  /// The `InitializeMultisig` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig(final AccountMeta invokedToken2022ProgramMeta,
                                               final List<AccountMeta> keys,
                                               final int m) {
    final byte[] _data = new byte[2];
    int i = INITIALIZE_MULTISIG_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) m;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initializes a multisignature account with N provided signers.
  /// 
  /// Multisignature accounts can used in place of any single owner/delegate
  /// accounts in any token instruction that require an owner/delegate to be
  /// present. The variant field represents the number of signers (M)
  /// required to validate this multisignature account.
  /// 
  /// The `InitializeMultisig` instruction requires no signers and MUST be
  /// included within the same Transaction as the system program's
  /// `CreateAccount` instruction that creates the account being initialized.
  /// Otherwise another party can acquire ownership of the uninitialized account.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public record InitializeMultisigIxData(int discriminator, int m) implements SerDe {  

    public static InitializeMultisigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int M_OFFSET = 1;

    public static InitializeMultisigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var m = _data[i] & 0xFF;
      return new InitializeMultisigIxData(discriminator, m);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) m;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_DISCRIMINATOR = toDiscriminator(3);

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param sourceKey The source account.
  /// @param destinationKey The destination account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> transferKeys(final PublicKey sourceKey,
                                               final PublicKey destinationKey,
                                               final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceKey),
      createWrite(destinationKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param sourceKey The source account.
  /// @param destinationKey The destination account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to transfer.
  public static Instruction transfer(final AccountMeta invokedToken2022ProgramMeta,
                                     final PublicKey sourceKey,
                                     final PublicKey destinationKey,
                                     final PublicKey authorityKey,
                                     final long amount) {
    final var keys = transferKeys(
      sourceKey,
      destinationKey,
      authorityKey
    );
    return transfer(invokedToken2022ProgramMeta, keys, amount);
  }

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param amount The amount of tokens to transfer.
  public static Instruction transfer(final AccountMeta invokedToken2022ProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long amount) {
    final byte[] _data = new byte[9];
    int i = TRANSFER_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param amount The amount of tokens to transfer.
  public record TransferIxData(int discriminator, long amount) implements SerDe {  

    public static TransferIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;

    public static TransferIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      return new TransferIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator APPROVE_DISCRIMINATOR = toDiscriminator(4);

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param sourceKey The source account.
  /// @param delegateKey The delegate.
  /// @param ownerKey The source account owner or its multisignature account.
  public static List<AccountMeta> approveKeys(final PublicKey sourceKey,
                                              final PublicKey delegateKey,
                                              final PublicKey ownerKey) {
    return List.of(
      createWrite(sourceKey),
      createRead(delegateKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param sourceKey The source account.
  /// @param delegateKey The delegate.
  /// @param ownerKey The source account owner or its multisignature account.
  /// @param amount The amount of tokens the delegate is approved for.
  public static Instruction approve(final AccountMeta invokedToken2022ProgramMeta,
                                    final PublicKey sourceKey,
                                    final PublicKey delegateKey,
                                    final PublicKey ownerKey,
                                    final long amount) {
    final var keys = approveKeys(
      sourceKey,
      delegateKey,
      ownerKey
    );
    return approve(invokedToken2022ProgramMeta, keys, amount);
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param amount The amount of tokens the delegate is approved for.
  public static Instruction approve(final AccountMeta invokedToken2022ProgramMeta,
                                    final List<AccountMeta> keys,
                                    final long amount) {
    final byte[] _data = new byte[9];
    int i = APPROVE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param amount The amount of tokens the delegate is approved for.
  public record ApproveIxData(int discriminator, long amount) implements SerDe {  

    public static ApproveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;

    public static ApproveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      return new ApproveIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REVOKE_DISCRIMINATOR = toDiscriminator(5);

  /// Revokes the delegate's authority.
  ///
  /// @param sourceKey The source account.
  /// @param ownerKey The source account owner or its multisignature.
  public static List<AccountMeta> revokeKeys(final PublicKey sourceKey,
                                             final PublicKey ownerKey) {
    return List.of(
      createWrite(sourceKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Revokes the delegate's authority.
  ///
  /// @param sourceKey The source account.
  /// @param ownerKey The source account owner or its multisignature.
  public static Instruction revoke(final AccountMeta invokedToken2022ProgramMeta,
                                   final PublicKey sourceKey,
                                   final PublicKey ownerKey) {
    final var keys = revokeKeys(
      sourceKey,
      ownerKey
    );
    return revoke(invokedToken2022ProgramMeta, keys);
  }

  /// Revokes the delegate's authority.
  ///
  public static Instruction revoke(final AccountMeta invokedToken2022ProgramMeta,
                                   final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    REVOKE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Revokes the delegate's authority.
  ///
  public record RevokeIxData(int discriminator) implements SerDe {  

    public static RevokeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static RevokeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new RevokeIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_AUTHORITY_DISCRIMINATOR = toDiscriminator(6);

  /// Sets a new authority of a mint or account.
  ///
  /// @param ownedKey The mint or account to change the authority of.
  /// @param ownerKey The current authority or the multisignature account of the mint or account to update.
  public static List<AccountMeta> setAuthorityKeys(final PublicKey ownedKey,
                                                   final PublicKey ownerKey) {
    return List.of(
      createWrite(ownedKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Sets a new authority of a mint or account.
  ///
  /// @param ownedKey The mint or account to change the authority of.
  /// @param ownerKey The current authority or the multisignature account of the mint or account to update.
  /// @param authorityType The type of authority to update.
  /// @param newAuthority The new authority
  public static Instruction setAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                         final PublicKey ownedKey,
                                         final PublicKey ownerKey,
                                         final AuthorityType authorityType,
                                         final PublicKey newAuthority) {
    final var keys = setAuthorityKeys(
      ownedKey,
      ownerKey
    );
    return setAuthority(
      invokedToken2022ProgramMeta,
      keys,
      authorityType,
      newAuthority
    );
  }

  /// Sets a new authority of a mint or account.
  ///
  /// @param authorityType The type of authority to update.
  /// @param newAuthority The new authority
  public static Instruction setAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                         final List<AccountMeta> keys,
                                         final AuthorityType authorityType,
                                         final PublicKey newAuthority) {
    final byte[] _data = new byte[
    1 + authorityType.l()
    + (newAuthority == null ? 1 : 33)
    ];
    int i = SET_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    i += authorityType.write(_data, i);
    SerDeUtil.writeOptional(1, newAuthority, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Sets a new authority of a mint or account.
  ///
  /// @param authorityType The type of authority to update.
  /// @param newAuthority The new authority
  public record SetAuthorityIxData(int discriminator,
                                   AuthorityType authorityType,
                                   PublicKey newAuthority) implements SerDe {  

    public static SetAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AUTHORITY_TYPE_OFFSET = 1;
    public static final int NEW_AUTHORITY_OFFSET = 3;

    public static SetAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var authorityType = AuthorityType.read(_data, i);
      i += authorityType.l();
      final PublicKey newAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        newAuthority = null;
      } else {
        ++i;
        newAuthority = readPubKey(_data, i);
      }
      return new SetAuthorityIxData(discriminator, authorityType, newAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      i += authorityType.write(_data, i);
      i += SerDeUtil.writeOptional(1, newAuthority, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + authorityType.l() + (newAuthority == null ? 1 : (1 + 32));
    }
  }

  public static final Discriminator MINT_TO_DISCRIMINATOR = toDiscriminator(7);

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param mintKey The mint account.
  /// @param tokenKey The account to mint tokens to.
  /// @param mintAuthorityKey The mint's minting authority or its multisignature account.
  public static List<AccountMeta> mintToKeys(final PublicKey mintKey,
                                             final PublicKey tokenKey,
                                             final PublicKey mintAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createWrite(tokenKey),
      createReadOnlySigner(mintAuthorityKey)
    );
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param mintKey The mint account.
  /// @param tokenKey The account to mint tokens to.
  /// @param mintAuthorityKey The mint's minting authority or its multisignature account.
  /// @param amount The amount of new tokens to mint.
  public static Instruction mintTo(final AccountMeta invokedToken2022ProgramMeta,
                                   final PublicKey mintKey,
                                   final PublicKey tokenKey,
                                   final PublicKey mintAuthorityKey,
                                   final long amount) {
    final var keys = mintToKeys(
      mintKey,
      tokenKey,
      mintAuthorityKey
    );
    return mintTo(invokedToken2022ProgramMeta, keys, amount);
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param amount The amount of new tokens to mint.
  public static Instruction mintTo(final AccountMeta invokedToken2022ProgramMeta,
                                   final List<AccountMeta> keys,
                                   final long amount) {
    final byte[] _data = new byte[9];
    int i = MINT_TO_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param amount The amount of new tokens to mint.
  public record MintToIxData(int discriminator, long amount) implements SerDe {  

    public static MintToIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;

    public static MintToIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      return new MintToIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BURN_DISCRIMINATOR = toDiscriminator(8);

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  /// @param accountKey The account to burn from.
  /// @param mintKey The token mint.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> burnKeys(final PublicKey accountKey,
                                           final PublicKey mintKey,
                                           final PublicKey authorityKey) {
    return List.of(
      createWrite(accountKey),
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  /// @param accountKey The account to burn from.
  /// @param mintKey The token mint.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  public static Instruction burn(final AccountMeta invokedToken2022ProgramMeta,
                                 final PublicKey accountKey,
                                 final PublicKey mintKey,
                                 final PublicKey authorityKey,
                                 final long amount) {
    final var keys = burnKeys(
      accountKey,
      mintKey,
      authorityKey
    );
    return burn(invokedToken2022ProgramMeta, keys, amount);
  }

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  public static Instruction burn(final AccountMeta invokedToken2022ProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long amount) {
    final byte[] _data = new byte[9];
    int i = BURN_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  /// @param discriminator The amount of tokens to burn.
  public record BurnIxData(int discriminator, long amount) implements SerDe {  

    public static BurnIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;

    public static BurnIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      return new BurnIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLOSE_ACCOUNT_DISCRIMINATOR = toDiscriminator(9);

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  /// @param accountKey The account to close.
  /// @param destinationKey The destination account.
  /// @param ownerKey The account's owner or its multisignature account.
  public static List<AccountMeta> closeAccountKeys(final PublicKey accountKey,
                                                   final PublicKey destinationKey,
                                                   final PublicKey ownerKey) {
    return List.of(
      createWrite(accountKey),
      createWrite(destinationKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  /// @param accountKey The account to close.
  /// @param destinationKey The destination account.
  /// @param ownerKey The account's owner or its multisignature account.
  public static Instruction closeAccount(final AccountMeta invokedToken2022ProgramMeta,
                                         final PublicKey accountKey,
                                         final PublicKey destinationKey,
                                         final PublicKey ownerKey) {
    final var keys = closeAccountKeys(
      accountKey,
      destinationKey,
      ownerKey
    );
    return closeAccount(invokedToken2022ProgramMeta, keys);
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  public static Instruction closeAccount(final AccountMeta invokedToken2022ProgramMeta,
                                         final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CLOSE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  public record CloseAccountIxData(int discriminator) implements SerDe {  

    public static CloseAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static CloseAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new CloseAccountIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FREEZE_ACCOUNT_DISCRIMINATOR = toDiscriminator(10);

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  /// @param accountKey The account to freeze.
  /// @param mintKey The token mint.
  /// @param ownerKey The mint freeze authority or its multisignature account.
  public static List<AccountMeta> freezeAccountKeys(final PublicKey accountKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey ownerKey) {
    return List.of(
      createWrite(accountKey),
      createRead(mintKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  /// @param accountKey The account to freeze.
  /// @param mintKey The token mint.
  /// @param ownerKey The mint freeze authority or its multisignature account.
  public static Instruction freezeAccount(final AccountMeta invokedToken2022ProgramMeta,
                                          final PublicKey accountKey,
                                          final PublicKey mintKey,
                                          final PublicKey ownerKey) {
    final var keys = freezeAccountKeys(
      accountKey,
      mintKey,
      ownerKey
    );
    return freezeAccount(invokedToken2022ProgramMeta, keys);
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  public static Instruction freezeAccount(final AccountMeta invokedToken2022ProgramMeta,
                                          final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    FREEZE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  public record FreezeAccountIxData(int discriminator) implements SerDe {  

    public static FreezeAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static FreezeAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new FreezeAccountIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator THAW_ACCOUNT_DISCRIMINATOR = toDiscriminator(11);

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  /// @param accountKey The account to thaw.
  /// @param mintKey The token mint.
  /// @param ownerKey The mint freeze authority or its multisignature account.
  public static List<AccountMeta> thawAccountKeys(final PublicKey accountKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey ownerKey) {
    return List.of(
      createWrite(accountKey),
      createRead(mintKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  /// @param accountKey The account to thaw.
  /// @param mintKey The token mint.
  /// @param ownerKey The mint freeze authority or its multisignature account.
  public static Instruction thawAccount(final AccountMeta invokedToken2022ProgramMeta,
                                        final PublicKey accountKey,
                                        final PublicKey mintKey,
                                        final PublicKey ownerKey) {
    final var keys = thawAccountKeys(
      accountKey,
      mintKey,
      ownerKey
    );
    return thawAccount(invokedToken2022ProgramMeta, keys);
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  public static Instruction thawAccount(final AccountMeta invokedToken2022ProgramMeta,
                                        final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    THAW_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  public record ThawAccountIxData(int discriminator) implements SerDe {  

    public static ThawAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static ThawAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new ThawAccountIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_CHECKED_DISCRIMINATOR = toDiscriminator(12);

  /// Transfers tokens from one account to another either directly or via a
  /// delegate. If this account is associated with the native mint then equal
  /// amounts of SOL and Tokens will be transferred to the destination account.
  /// 
  /// This instruction differs from Transfer in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param sourceKey The source account.
  /// @param mintKey The token mint.
  /// @param destinationKey The destination account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> transferCheckedKeys(final PublicKey sourceKey,
                                                      final PublicKey mintKey,
                                                      final PublicKey destinationKey,
                                                      final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceKey),
      createRead(mintKey),
      createWrite(destinationKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfers tokens from one account to another either directly or via a
  /// delegate. If this account is associated with the native mint then equal
  /// amounts of SOL and Tokens will be transferred to the destination account.
  /// 
  /// This instruction differs from Transfer in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param sourceKey The source account.
  /// @param mintKey The token mint.
  /// @param destinationKey The destination account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction transferChecked(final AccountMeta invokedToken2022ProgramMeta,
                                            final PublicKey sourceKey,
                                            final PublicKey mintKey,
                                            final PublicKey destinationKey,
                                            final PublicKey authorityKey,
                                            final long amount,
                                            final int decimals) {
    final var keys = transferCheckedKeys(
      sourceKey,
      mintKey,
      destinationKey,
      authorityKey
    );
    return transferChecked(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals
    );
  }

  /// Transfers tokens from one account to another either directly or via a
  /// delegate. If this account is associated with the native mint then equal
  /// amounts of SOL and Tokens will be transferred to the destination account.
  /// 
  /// This instruction differs from Transfer in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction transferChecked(final AccountMeta invokedToken2022ProgramMeta,
                                            final List<AccountMeta> keys,
                                            final long amount,
                                            final int decimals) {
    final byte[] _data = new byte[10];
    int i = TRANSFER_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfers tokens from one account to another either directly or via a
  /// delegate. If this account is associated with the native mint then equal
  /// amounts of SOL and Tokens will be transferred to the destination account.
  /// 
  /// This instruction differs from Transfer in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public record TransferCheckedIxData(int discriminator,
                                      long amount,
                                      int decimals) implements SerDe {  

    public static TransferCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;
    public static final int DECIMALS_OFFSET = 9;

    public static TransferCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      return new TransferCheckedIxData(discriminator, amount, decimals);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator APPROVE_CHECKED_DISCRIMINATOR = toDiscriminator(13);

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  /// 
  /// This instruction differs from Approve in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param sourceKey The source account.
  /// @param mintKey The token mint.
  /// @param delegateKey The delegate.
  /// @param ownerKey The source account owner or its multisignature account.
  public static List<AccountMeta> approveCheckedKeys(final PublicKey sourceKey,
                                                     final PublicKey mintKey,
                                                     final PublicKey delegateKey,
                                                     final PublicKey ownerKey) {
    return List.of(
      createWrite(sourceKey),
      createRead(mintKey),
      createRead(delegateKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  /// 
  /// This instruction differs from Approve in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param sourceKey The source account.
  /// @param mintKey The token mint.
  /// @param delegateKey The delegate.
  /// @param ownerKey The source account owner or its multisignature account.
  /// @param amount The amount of tokens the delegate is approved for.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction approveChecked(final AccountMeta invokedToken2022ProgramMeta,
                                           final PublicKey sourceKey,
                                           final PublicKey mintKey,
                                           final PublicKey delegateKey,
                                           final PublicKey ownerKey,
                                           final long amount,
                                           final int decimals) {
    final var keys = approveCheckedKeys(
      sourceKey,
      mintKey,
      delegateKey,
      ownerKey
    );
    return approveChecked(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals
    );
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  /// 
  /// This instruction differs from Approve in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param amount The amount of tokens the delegate is approved for.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction approveChecked(final AccountMeta invokedToken2022ProgramMeta,
                                           final List<AccountMeta> keys,
                                           final long amount,
                                           final int decimals) {
    final byte[] _data = new byte[10];
    int i = APPROVE_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  /// 
  /// This instruction differs from Approve in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param amount The amount of tokens the delegate is approved for.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public record ApproveCheckedIxData(int discriminator,
                                     long amount,
                                     int decimals) implements SerDe {  

    public static ApproveCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;
    public static final int DECIMALS_OFFSET = 9;

    public static ApproveCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      return new ApproveCheckedIxData(discriminator, amount, decimals);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MINT_TO_CHECKED_DISCRIMINATOR = toDiscriminator(14);

  /// Mints new tokens to an account. The native mint does not support minting.
  /// 
  /// This instruction differs from MintTo in that the decimals value is
  /// checked by the caller. This may be useful when creating transactions
  /// offline or within a hardware wallet.
  ///
  /// @param mintKey The mint.
  /// @param tokenKey The account to mint tokens to.
  /// @param mintAuthorityKey The mint's minting authority or its multisignature account.
  public static List<AccountMeta> mintToCheckedKeys(final PublicKey mintKey,
                                                    final PublicKey tokenKey,
                                                    final PublicKey mintAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createWrite(tokenKey),
      createReadOnlySigner(mintAuthorityKey)
    );
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  /// 
  /// This instruction differs from MintTo in that the decimals value is
  /// checked by the caller. This may be useful when creating transactions
  /// offline or within a hardware wallet.
  ///
  /// @param mintKey The mint.
  /// @param tokenKey The account to mint tokens to.
  /// @param mintAuthorityKey The mint's minting authority or its multisignature account.
  /// @param amount The amount of new tokens to mint.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction mintToChecked(final AccountMeta invokedToken2022ProgramMeta,
                                          final PublicKey mintKey,
                                          final PublicKey tokenKey,
                                          final PublicKey mintAuthorityKey,
                                          final long amount,
                                          final int decimals) {
    final var keys = mintToCheckedKeys(
      mintKey,
      tokenKey,
      mintAuthorityKey
    );
    return mintToChecked(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals
    );
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  /// 
  /// This instruction differs from MintTo in that the decimals value is
  /// checked by the caller. This may be useful when creating transactions
  /// offline or within a hardware wallet.
  ///
  /// @param amount The amount of new tokens to mint.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction mintToChecked(final AccountMeta invokedToken2022ProgramMeta,
                                          final List<AccountMeta> keys,
                                          final long amount,
                                          final int decimals) {
    final byte[] _data = new byte[10];
    int i = MINT_TO_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  /// 
  /// This instruction differs from MintTo in that the decimals value is
  /// checked by the caller. This may be useful when creating transactions
  /// offline or within a hardware wallet.
  ///
  /// @param amount The amount of new tokens to mint.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public record MintToCheckedIxData(int discriminator,
                                    long amount,
                                    int decimals) implements SerDe {  

    public static MintToCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;
    public static final int DECIMALS_OFFSET = 9;

    public static MintToCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      return new MintToCheckedIxData(discriminator, amount, decimals);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BURN_CHECKED_DISCRIMINATOR = toDiscriminator(15);

  /// Burns tokens by removing them from an account. `BurnChecked` does not
  /// support accounts associated with the native mint, use `CloseAccount` instead.
  /// 
  /// This instruction differs from Burn in that the decimals value is checked
  /// by the caller. This may be useful when creating transactions offline or
  /// within a hardware wallet.
  ///
  /// @param accountKey The account to burn from.
  /// @param mintKey The token mint.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> burnCheckedKeys(final PublicKey accountKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey authorityKey) {
    return List.of(
      createWrite(accountKey),
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Burns tokens by removing them from an account. `BurnChecked` does not
  /// support accounts associated with the native mint, use `CloseAccount` instead.
  /// 
  /// This instruction differs from Burn in that the decimals value is checked
  /// by the caller. This may be useful when creating transactions offline or
  /// within a hardware wallet.
  ///
  /// @param accountKey The account to burn from.
  /// @param mintKey The token mint.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction burnChecked(final AccountMeta invokedToken2022ProgramMeta,
                                        final PublicKey accountKey,
                                        final PublicKey mintKey,
                                        final PublicKey authorityKey,
                                        final long amount,
                                        final int decimals) {
    final var keys = burnCheckedKeys(
      accountKey,
      mintKey,
      authorityKey
    );
    return burnChecked(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals
    );
  }

  /// Burns tokens by removing them from an account. `BurnChecked` does not
  /// support accounts associated with the native mint, use `CloseAccount` instead.
  /// 
  /// This instruction differs from Burn in that the decimals value is checked
  /// by the caller. This may be useful when creating transactions offline or
  /// within a hardware wallet.
  ///
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction burnChecked(final AccountMeta invokedToken2022ProgramMeta,
                                        final List<AccountMeta> keys,
                                        final long amount,
                                        final int decimals) {
    final byte[] _data = new byte[10];
    int i = BURN_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Burns tokens by removing them from an account. `BurnChecked` does not
  /// support accounts associated with the native mint, use `CloseAccount` instead.
  /// 
  /// This instruction differs from Burn in that the decimals value is checked
  /// by the caller. This may be useful when creating transactions offline or
  /// within a hardware wallet.
  ///
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public record BurnCheckedIxData(int discriminator,
                                  long amount,
                                  int decimals) implements SerDe {  

    public static BurnCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;
    public static final int DECIMALS_OFFSET = 9;

    public static BurnCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      return new BurnCheckedIxData(discriminator, amount, decimals);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_ACCOUNT_2_DISCRIMINATOR = toDiscriminator(16);

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param accountKey The account to initialize.
  /// @param mintKey The mint this account will be associated with.
  public static List<AccountMeta> initializeAccount2Keys(final SolanaAccounts solanaAccounts,
                                                         final PublicKey accountKey,
                                                         final PublicKey mintKey) {
    return List.of(
      createWrite(accountKey),
      createRead(mintKey),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param accountKey The account to initialize.
  /// @param mintKey The mint this account will be associated with.
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount2(final AccountMeta invokedToken2022ProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey accountKey,
                                               final PublicKey mintKey,
                                               final PublicKey owner) {
    final var keys = initializeAccount2Keys(
      solanaAccounts,
      accountKey,
      mintKey
    );
    return initializeAccount2(invokedToken2022ProgramMeta, keys, owner);
  }

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount2(final AccountMeta invokedToken2022ProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey owner) {
    final byte[] _data = new byte[33];
    int i = INITIALIZE_ACCOUNT_2_DISCRIMINATOR.write(_data, 0);
    owner.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param owner The new account's owner/multisignature.
  public record InitializeAccount2IxData(int discriminator, PublicKey owner) implements SerDe {  

    public static InitializeAccount2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 33;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int OWNER_OFFSET = 1;

    public static InitializeAccount2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var owner = readPubKey(_data, i);
      return new InitializeAccount2IxData(discriminator, owner);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      owner.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SYNC_NATIVE_DISCRIMINATOR = toDiscriminator(17);

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  /// @param accountKey The native token account to sync with its underlying lamports.
  public static List<AccountMeta> syncNativeKeys(final PublicKey accountKey) {
    return List.of(
      createWrite(accountKey)
    );
  }

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  /// @param accountKey The native token account to sync with its underlying lamports.
  public static Instruction syncNative(final AccountMeta invokedToken2022ProgramMeta,
                                       final PublicKey accountKey) {
    final var keys = syncNativeKeys(
      accountKey
    );
    return syncNative(invokedToken2022ProgramMeta, keys);
  }

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  public static Instruction syncNative(final AccountMeta invokedToken2022ProgramMeta,
                                       final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    SYNC_NATIVE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  public record SyncNativeIxData(int discriminator) implements SerDe {  

    public static SyncNativeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static SyncNativeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new SyncNativeIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_ACCOUNT_3_DISCRIMINATOR = toDiscriminator(18);

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param accountKey The account to initialize.
  /// @param mintKey The mint this account will be associated with.
  public static List<AccountMeta> initializeAccount3Keys(final PublicKey accountKey,
                                                         final PublicKey mintKey) {
    return List.of(
      createWrite(accountKey),
      createRead(mintKey)
    );
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param accountKey The account to initialize.
  /// @param mintKey The mint this account will be associated with.
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount3(final AccountMeta invokedToken2022ProgramMeta,
                                               final PublicKey accountKey,
                                               final PublicKey mintKey,
                                               final PublicKey owner) {
    final var keys = initializeAccount3Keys(
      accountKey,
      mintKey
    );
    return initializeAccount3(invokedToken2022ProgramMeta, keys, owner);
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount3(final AccountMeta invokedToken2022ProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey owner) {
    final byte[] _data = new byte[33];
    int i = INITIALIZE_ACCOUNT_3_DISCRIMINATOR.write(_data, 0);
    owner.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param owner The new account's owner/multisignature.
  public record InitializeAccount3IxData(int discriminator, PublicKey owner) implements SerDe {  

    public static InitializeAccount3IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 33;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int OWNER_OFFSET = 1;

    public static InitializeAccount3IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var owner = readPubKey(_data, i);
      return new InitializeAccount3IxData(discriminator, owner);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      owner.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_MULTISIG_2_DISCRIMINATOR = toDiscriminator(19);

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param multisigKey The multisignature account to initialize.
  public static List<AccountMeta> initializeMultisig2Keys(final PublicKey multisigKey) {
    return List.of(
      createWrite(multisigKey)
    );
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param multisigKey The multisignature account to initialize.
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig2(final AccountMeta invokedToken2022ProgramMeta,
                                                final PublicKey multisigKey,
                                                final int m) {
    final var keys = initializeMultisig2Keys(
      multisigKey
    );
    return initializeMultisig2(invokedToken2022ProgramMeta, keys, m);
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig2(final AccountMeta invokedToken2022ProgramMeta,
                                                final List<AccountMeta> keys,
                                                final int m) {
    final byte[] _data = new byte[2];
    int i = INITIALIZE_MULTISIG_2_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) m;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public record InitializeMultisig2IxData(int discriminator, int m) implements SerDe {  

    public static InitializeMultisig2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int M_OFFSET = 1;

    public static InitializeMultisig2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var m = _data[i] & 0xFF;
      return new InitializeMultisig2IxData(discriminator, m);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) m;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_MINT_2_DISCRIMINATOR = toDiscriminator(20);

  /// Like `InitializeMint`, but does not require the Rent sysvar to be provided.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeMint2Keys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Like `InitializeMint`, but does not require the Rent sysvar to be provided.
  ///
  /// @param mintKey The mint to initialize.
  /// @param decimals Number of base 10 digits to the right of the decimal place.
  /// @param mintAuthority The authority/multisignature to mint tokens.
  /// @param freezeAuthority The optional freeze authority/multisignature of the mint.
  public static Instruction initializeMint2(final AccountMeta invokedToken2022ProgramMeta,
                                            final PublicKey mintKey,
                                            final int decimals,
                                            final PublicKey mintAuthority,
                                            final PublicKey freezeAuthority) {
    final var keys = initializeMint2Keys(
      mintKey
    );
    return initializeMint2(
      invokedToken2022ProgramMeta,
      keys,
      decimals,
      mintAuthority,
      freezeAuthority
    );
  }

  /// Like `InitializeMint`, but does not require the Rent sysvar to be provided.
  ///
  /// @param decimals Number of base 10 digits to the right of the decimal place.
  /// @param mintAuthority The authority/multisignature to mint tokens.
  /// @param freezeAuthority The optional freeze authority/multisignature of the mint.
  public static Instruction initializeMint2(final AccountMeta invokedToken2022ProgramMeta,
                                            final List<AccountMeta> keys,
                                            final int decimals,
                                            final PublicKey mintAuthority,
                                            final PublicKey freezeAuthority) {
    final byte[] _data = new byte[
    34
    + (freezeAuthority == null ? 1 : 33)
    ];
    int i = INITIALIZE_MINT_2_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) decimals;
    ++i;
    mintAuthority.write(_data, i);
    i += 32;
    SerDeUtil.writeOptional(1, freezeAuthority, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Like `InitializeMint`, but does not require the Rent sysvar to be provided.
  ///
  /// @param decimals Number of base 10 digits to the right of the decimal place.
  /// @param mintAuthority The authority/multisignature to mint tokens.
  /// @param freezeAuthority The optional freeze authority/multisignature of the mint.
  public record InitializeMint2IxData(int discriminator,
                                      int decimals,
                                      PublicKey mintAuthority,
                                      PublicKey freezeAuthority) implements SerDe {  

    public static InitializeMint2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int DECIMALS_OFFSET = 1;
    public static final int MINT_AUTHORITY_OFFSET = 2;
    public static final int FREEZE_AUTHORITY_OFFSET = 35;

    public static InitializeMint2IxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var decimals = _data[i] & 0xFF;
      ++i;
      final var mintAuthority = readPubKey(_data, i);
      i += 32;
      final PublicKey freezeAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        freezeAuthority = null;
      } else {
        ++i;
        freezeAuthority = readPubKey(_data, i);
      }
      return new InitializeMint2IxData(discriminator,
                                       decimals,
                                       mintAuthority,
                                       freezeAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) decimals;
      ++i;
      mintAuthority.write(_data, i);
      i += 32;
      i += SerDeUtil.writeOptional(1, freezeAuthority, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 1 + 32 + (freezeAuthority == null ? 1 : (1 + 32));
    }
  }

  public static final Discriminator GET_ACCOUNT_DATA_SIZE_DISCRIMINATOR = toDiscriminator(21);

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param mintKey The mint to calculate for.
  public static List<AccountMeta> getAccountDataSizeKeys(final PublicKey mintKey) {
    return List.of(
      createRead(mintKey)
    );
  }

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param mintKey The mint to calculate for.
  public static Instruction getAccountDataSize(final AccountMeta invokedToken2022ProgramMeta,
                                               final PublicKey mintKey) {
    final var keys = getAccountDataSizeKeys(
      mintKey
    );
    return getAccountDataSize(invokedToken2022ProgramMeta, keys);
  }

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  public static Instruction getAccountDataSize(final AccountMeta invokedToken2022ProgramMeta,
                                               final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    GET_ACCOUNT_DATA_SIZE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  public record GetAccountDataSizeIxData(int discriminator) implements SerDe {  

    public static GetAccountDataSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static GetAccountDataSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new GetAccountDataSizeIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_IMMUTABLE_OWNER_DISCRIMINATOR = toDiscriminator(22);

  /// Initialize the Immutable Owner extension for the given token account
  /// 
  /// Fails if the account has already been initialized, so must be called
  /// before `InitializeAccount`.
  /// 
  /// No-ops in this version of the program, but is included for compatibility
  /// with the Associated Token Account program.
  ///
  /// @param accountKey The account to initialize.
  public static List<AccountMeta> initializeImmutableOwnerKeys(final PublicKey accountKey) {
    return List.of(
      createWrite(accountKey)
    );
  }

  /// Initialize the Immutable Owner extension for the given token account
  /// 
  /// Fails if the account has already been initialized, so must be called
  /// before `InitializeAccount`.
  /// 
  /// No-ops in this version of the program, but is included for compatibility
  /// with the Associated Token Account program.
  ///
  /// @param accountKey The account to initialize.
  public static Instruction initializeImmutableOwner(final AccountMeta invokedToken2022ProgramMeta,
                                                     final PublicKey accountKey) {
    final var keys = initializeImmutableOwnerKeys(
      accountKey
    );
    return initializeImmutableOwner(invokedToken2022ProgramMeta, keys);
  }

  /// Initialize the Immutable Owner extension for the given token account
  /// 
  /// Fails if the account has already been initialized, so must be called
  /// before `InitializeAccount`.
  /// 
  /// No-ops in this version of the program, but is included for compatibility
  /// with the Associated Token Account program.
  ///
  public static Instruction initializeImmutableOwner(final AccountMeta invokedToken2022ProgramMeta,
                                                     final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    INITIALIZE_IMMUTABLE_OWNER_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize the Immutable Owner extension for the given token account
  /// 
  /// Fails if the account has already been initialized, so must be called
  /// before `InitializeAccount`.
  /// 
  /// No-ops in this version of the program, but is included for compatibility
  /// with the Associated Token Account program.
  ///
  public record InitializeImmutableOwnerIxData(int discriminator) implements SerDe {  

    public static InitializeImmutableOwnerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static InitializeImmutableOwnerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new InitializeImmutableOwnerIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator AMOUNT_TO_UI_AMOUNT_DISCRIMINATOR = toDiscriminator(23);

  /// Convert an Amount of tokens to a UiAmount `string`, using the given
  /// mint. In this version of the program, the mint can only specify the
  /// number of decimals.
  /// 
  /// Fails on an invalid mint.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserialized
  /// with `String::from_utf8`.
  ///
  /// @param mintKey The mint to calculate for.
  public static List<AccountMeta> amountToUiAmountKeys(final PublicKey mintKey) {
    return List.of(
      createRead(mintKey)
    );
  }

  /// Convert an Amount of tokens to a UiAmount `string`, using the given
  /// mint. In this version of the program, the mint can only specify the
  /// number of decimals.
  /// 
  /// Fails on an invalid mint.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserialized
  /// with `String::from_utf8`.
  ///
  /// @param mintKey The mint to calculate for.
  /// @param amount The amount of tokens to reformat.
  public static Instruction amountToUiAmount(final AccountMeta invokedToken2022ProgramMeta,
                                             final PublicKey mintKey,
                                             final long amount) {
    final var keys = amountToUiAmountKeys(
      mintKey
    );
    return amountToUiAmount(invokedToken2022ProgramMeta, keys, amount);
  }

  /// Convert an Amount of tokens to a UiAmount `string`, using the given
  /// mint. In this version of the program, the mint can only specify the
  /// number of decimals.
  /// 
  /// Fails on an invalid mint.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserialized
  /// with `String::from_utf8`.
  ///
  /// @param amount The amount of tokens to reformat.
  public static Instruction amountToUiAmount(final AccountMeta invokedToken2022ProgramMeta,
                                             final List<AccountMeta> keys,
                                             final long amount) {
    final byte[] _data = new byte[9];
    int i = AMOUNT_TO_UI_AMOUNT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Convert an Amount of tokens to a UiAmount `string`, using the given
  /// mint. In this version of the program, the mint can only specify the
  /// number of decimals.
  /// 
  /// Fails on an invalid mint.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserialized
  /// with `String::from_utf8`.
  ///
  /// @param amount The amount of tokens to reformat.
  public record AmountToUiAmountIxData(int discriminator, long amount) implements SerDe {  

    public static AmountToUiAmountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 1;

    public static AmountToUiAmountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      return new AmountToUiAmountIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UI_AMOUNT_TO_AMOUNT_DISCRIMINATOR = toDiscriminator(24);

  /// Convert a UiAmount of tokens to a little-endian `u64` raw Amount, using
  /// the given mint. In this version of the program, the mint can only
  /// specify the number of decimals.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param mintKey The mint to calculate for.
  public static List<AccountMeta> uiAmountToAmountKeys(final PublicKey mintKey) {
    return List.of(
      createRead(mintKey)
    );
  }

  /// Convert a UiAmount of tokens to a little-endian `u64` raw Amount, using
  /// the given mint. In this version of the program, the mint can only
  /// specify the number of decimals.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param mintKey The mint to calculate for.
  /// @param uiAmount The ui_amount of tokens to reformat.
  public static Instruction uiAmountToAmount(final AccountMeta invokedToken2022ProgramMeta,
                                             final PublicKey mintKey,
                                             final String uiAmount) {
    final var keys = uiAmountToAmountKeys(
      mintKey
    );
    return uiAmountToAmount(invokedToken2022ProgramMeta, keys, uiAmount);
  }

  /// Convert a UiAmount of tokens to a little-endian `u64` raw Amount, using
  /// the given mint. In this version of the program, the mint can only
  /// specify the number of decimals.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param uiAmount The ui_amount of tokens to reformat.
  public static Instruction uiAmountToAmount(final AccountMeta invokedToken2022ProgramMeta,
                                             final List<AccountMeta> keys,
                                             final String uiAmount) {
    final byte[] _uiAmount = uiAmount.getBytes(UTF_8);
    final byte[] _data = new byte[5 + _uiAmount.length];
    int i = UI_AMOUNT_TO_AMOUNT_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, _uiAmount.length);
    i += 4;
    System.arraycopy(_uiAmount, 0, _data, i, _uiAmount.length);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Convert a UiAmount of tokens to a little-endian `u64` raw Amount, using
  /// the given mint. In this version of the program, the mint can only
  /// specify the number of decimals.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param uiAmount The ui_amount of tokens to reformat.
  public record UiAmountToAmountIxData(int discriminator, String uiAmount, byte[] _uiAmount) implements SerDe {  

    public static UiAmountToAmountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int UI_AMOUNT_OFFSET = 1;

    public static UiAmountToAmountIxData createRecord(final int discriminator, final String uiAmount) {
      return new UiAmountToAmountIxData(discriminator, uiAmount, uiAmount.getBytes(UTF_8));
    }

    public static UiAmountToAmountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final int _uiAmountLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _uiAmount = Arrays.copyOfRange(_data, i, i + _uiAmountLength);
      final var uiAmount = new String(_uiAmount, UTF_8);
      return new UiAmountToAmountIxData(discriminator, uiAmount, _uiAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      putInt32LE(_data, i, _uiAmount.length);
      i += 4;
      System.arraycopy(_uiAmount, 0, _data, i, _uiAmount.length);
      i += _uiAmount.length;
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + _uiAmount.length;
    }
  }

  public static final Discriminator INITIALIZE_MINT_CLOSE_AUTHORITY_DISCRIMINATOR = toDiscriminator(25);

  /// Initialize the close account authority on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeMintCloseAuthorityKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize the close account authority on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param closeAuthority Authority that must sign the `CloseAccount` instruction on a mint.
  public static Instruction initializeMintCloseAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                                         final PublicKey mintKey,
                                                         final PublicKey closeAuthority) {
    final var keys = initializeMintCloseAuthorityKeys(
      mintKey
    );
    return initializeMintCloseAuthority(invokedToken2022ProgramMeta, keys, closeAuthority);
  }

  /// Initialize the close account authority on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param closeAuthority Authority that must sign the `CloseAccount` instruction on a mint.
  public static Instruction initializeMintCloseAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final PublicKey closeAuthority) {
    final byte[] _data = new byte[
    1
    + (closeAuthority == null ? 1 : 33)
    ];
    int i = INITIALIZE_MINT_CLOSE_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    SerDeUtil.writeOptional(1, closeAuthority, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize the close account authority on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param closeAuthority Authority that must sign the `CloseAccount` instruction on a mint.
  public record InitializeMintCloseAuthorityIxData(int discriminator, PublicKey closeAuthority) implements SerDe {  

    public static InitializeMintCloseAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CLOSE_AUTHORITY_OFFSET = 2;

    public static InitializeMintCloseAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final PublicKey closeAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        closeAuthority = null;
      } else {
        ++i;
        closeAuthority = readPubKey(_data, i);
      }
      return new InitializeMintCloseAuthorityIxData(discriminator, closeAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      i += SerDeUtil.writeOptional(1, closeAuthority, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (closeAuthority == null ? 1 : (1 + 32));
    }
  }

  public static final Discriminator INITIALIZE_TRANSFER_FEE_CONFIG_DISCRIMINATOR = toDiscriminator(26);

  /// Initialize the transfer fee on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeTransferFeeConfigKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize the transfer fee on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param transferFeeConfigAuthority Pubkey that may update the fees.
  /// @param withdrawWithheldAuthority Withdraw instructions must be signed by this key.
  /// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the transfer amount.
  /// @param maximumFee Maximum fee assessed on transfers.
  public static Instruction initializeTransferFeeConfig(final AccountMeta invokedToken2022ProgramMeta,
                                                        final PublicKey mintKey,
                                                        final PublicKey transferFeeConfigAuthority,
                                                        final PublicKey withdrawWithheldAuthority,
                                                        final int transferFeeBasisPoints,
                                                        final long maximumFee) {
    final var keys = initializeTransferFeeConfigKeys(
      mintKey
    );
    return initializeTransferFeeConfig(
      invokedToken2022ProgramMeta,
      keys,
      transferFeeConfigAuthority,
      withdrawWithheldAuthority,
      transferFeeBasisPoints,
      maximumFee
    );
  }

  /// Initialize the transfer fee on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param transferFeeConfigAuthority Pubkey that may update the fees.
  /// @param withdrawWithheldAuthority Withdraw instructions must be signed by this key.
  /// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the transfer amount.
  /// @param maximumFee Maximum fee assessed on transfers.
  public static Instruction initializeTransferFeeConfig(final AccountMeta invokedToken2022ProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final PublicKey transferFeeConfigAuthority,
                                                        final PublicKey withdrawWithheldAuthority,
                                                        final int transferFeeBasisPoints,
                                                        final long maximumFee) {
    final byte[] _data = new byte[
    12
    + (transferFeeConfigAuthority == null ? 1 : 33)
    + (withdrawWithheldAuthority == null ? 1 : 33)
    ];
    int i = INITIALIZE_TRANSFER_FEE_CONFIG_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    i += SerDeUtil.writeOptional(1, transferFeeConfigAuthority, _data, i);
    i += SerDeUtil.writeOptional(1, withdrawWithheldAuthority, _data, i);
    putInt16LE(_data, i, transferFeeBasisPoints);
    i += 2;
    putInt64LE(_data, i, maximumFee);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize the transfer fee on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param transferFeeConfigAuthority Pubkey that may update the fees.
  /// @param withdrawWithheldAuthority Withdraw instructions must be signed by this key.
  /// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the transfer amount.
  /// @param maximumFee Maximum fee assessed on transfers.
  public record InitializeTransferFeeConfigIxData(int discriminator,
                                                  int transferFeeDiscriminator,
                                                  PublicKey transferFeeConfigAuthority,
                                                  PublicKey withdrawWithheldAuthority,
                                                  int transferFeeBasisPoints,
                                                  long maximumFee) implements SerDe {  

    public static InitializeTransferFeeConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int TRANSFER_FEE_CONFIG_AUTHORITY_OFFSET = 3;

    public static InitializeTransferFeeConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final PublicKey transferFeeConfigAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        transferFeeConfigAuthority = null;
        ++i;

      } else {
        ++i;
        transferFeeConfigAuthority = readPubKey(_data, i);
        i += 32;
      }
      final PublicKey withdrawWithheldAuthority;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        withdrawWithheldAuthority = null;
        ++i;

      } else {
        ++i;
        withdrawWithheldAuthority = readPubKey(_data, i);
        i += 32;
      }
      final var transferFeeBasisPoints = getInt16LE(_data, i);
      i += 2;
      final var maximumFee = getInt64LE(_data, i);
      return new InitializeTransferFeeConfigIxData(discriminator,
                                                   transferFeeDiscriminator,
                                                   transferFeeConfigAuthority,
                                                   withdrawWithheldAuthority,
                                                   transferFeeBasisPoints,
                                                   maximumFee);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferFeeDiscriminator;
      ++i;
      i += SerDeUtil.writeOptional(1, transferFeeConfigAuthority, _data, i);
      i += SerDeUtil.writeOptional(1, withdrawWithheldAuthority, _data, i);
      putInt16LE(_data, i, transferFeeBasisPoints);
      i += 2;
      putInt64LE(_data, i, maximumFee);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return 1
           + 1
           + (transferFeeConfigAuthority == null ? 1 : (1 + 32))
           + (withdrawWithheldAuthority == null ? 1 : (1 + 32))
           + 2
           + 8;
    }
  }

  public static final Discriminator TRANSFER_CHECKED_WITH_FEE_DISCRIMINATOR = toDiscriminator(26);

  /// Transfer, providing expected mint information and fees.
  /// 
  /// This instruction succeeds if the mint has no configured transfer fee
  /// and the provided fee is 0. This allows applications to use
  /// `TransferCheckedWithFee` with any mint.
  ///
  /// @param sourceKey The source account. May include the `TransferFeeAmount` extension.
  /// @param mintKey The token mint. May include the `TransferFeeConfig` extension.
  /// @param destinationKey The destination account. May include the `TransferFeeAmount` extension.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> transferCheckedWithFeeKeys(final PublicKey sourceKey,
                                                             final PublicKey mintKey,
                                                             final PublicKey destinationKey,
                                                             final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceKey),
      createRead(mintKey),
      createWrite(destinationKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfer, providing expected mint information and fees.
  /// 
  /// This instruction succeeds if the mint has no configured transfer fee
  /// and the provided fee is 0. This allows applications to use
  /// `TransferCheckedWithFee` with any mint.
  ///
  /// @param sourceKey The source account. May include the `TransferFeeAmount` extension.
  /// @param mintKey The token mint. May include the `TransferFeeConfig` extension.
  /// @param destinationKey The destination account. May include the `TransferFeeAmount` extension.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  /// @param fee Expected fee assessed on this transfer, calculated off-chain based
  ///            on the transfer_fee_basis_points and maximum_fee of the mint. May
  ///            be 0 for a mint without a configured transfer fee.
  public static Instruction transferCheckedWithFee(final AccountMeta invokedToken2022ProgramMeta,
                                                   final PublicKey sourceKey,
                                                   final PublicKey mintKey,
                                                   final PublicKey destinationKey,
                                                   final PublicKey authorityKey,
                                                   final long amount,
                                                   final int decimals,
                                                   final long fee) {
    final var keys = transferCheckedWithFeeKeys(
      sourceKey,
      mintKey,
      destinationKey,
      authorityKey
    );
    return transferCheckedWithFee(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals,
      fee
    );
  }

  /// Transfer, providing expected mint information and fees.
  /// 
  /// This instruction succeeds if the mint has no configured transfer fee
  /// and the provided fee is 0. This allows applications to use
  /// `TransferCheckedWithFee` with any mint.
  ///
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  /// @param fee Expected fee assessed on this transfer, calculated off-chain based
  ///            on the transfer_fee_basis_points and maximum_fee of the mint. May
  ///            be 0 for a mint without a configured transfer fee.
  public static Instruction transferCheckedWithFee(final AccountMeta invokedToken2022ProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final long amount,
                                                   final int decimals,
                                                   final long fee) {
    final byte[] _data = new byte[19];
    int i = TRANSFER_CHECKED_WITH_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;
    ++i;
    putInt64LE(_data, i, fee);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer, providing expected mint information and fees.
  /// 
  /// This instruction succeeds if the mint has no configured transfer fee
  /// and the provided fee is 0. This allows applications to use
  /// `TransferCheckedWithFee` with any mint.
  ///
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  /// @param fee Expected fee assessed on this transfer, calculated off-chain based
  ///            on the transfer_fee_basis_points and maximum_fee of the mint. May
  ///            be 0 for a mint without a configured transfer fee.
  public record TransferCheckedWithFeeIxData(int discriminator,
                                             int transferFeeDiscriminator,
                                             long amount,
                                             int decimals,
                                             long fee) implements SerDe {  

    public static TransferCheckedWithFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 19;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int AMOUNT_OFFSET = 2;
    public static final int DECIMALS_OFFSET = 10;
    public static final int FEE_OFFSET = 11;

    public static TransferCheckedWithFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      ++i;
      final var fee = getInt64LE(_data, i);
      return new TransferCheckedWithFeeIxData(discriminator,
                                              transferFeeDiscriminator,
                                              amount,
                                              decimals,
                                              fee);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferFeeDiscriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      putInt64LE(_data, i, fee);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_WITHHELD_TOKENS_FROM_MINT_DISCRIMINATOR = toDiscriminator(26);

  /// Transfer all withheld tokens in the mint to an account. Signed by the
  /// mint's withdraw withheld tokens authority.
  ///
  /// @param mintKey The token mint. Must include the `TransferFeeConfig` extension.
  /// @param feeReceiverKey The fee receiver account. Must include the `TransferFeeAmount`
  ///                       extension associated with the provided mint.
  /// @param withdrawWithheldAuthorityKey The mint's `withdraw_withheld_authority` or its multisignature account.
  public static List<AccountMeta> withdrawWithheldTokensFromMintKeys(final PublicKey mintKey,
                                                                     final PublicKey feeReceiverKey,
                                                                     final PublicKey withdrawWithheldAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createWrite(feeReceiverKey),
      createReadOnlySigner(withdrawWithheldAuthorityKey)
    );
  }

  /// Transfer all withheld tokens in the mint to an account. Signed by the
  /// mint's withdraw withheld tokens authority.
  ///
  /// @param mintKey The token mint. Must include the `TransferFeeConfig` extension.
  /// @param feeReceiverKey The fee receiver account. Must include the `TransferFeeAmount`
  ///                       extension associated with the provided mint.
  /// @param withdrawWithheldAuthorityKey The mint's `withdraw_withheld_authority` or its multisignature account.
  public static Instruction withdrawWithheldTokensFromMint(final AccountMeta invokedToken2022ProgramMeta,
                                                           final PublicKey mintKey,
                                                           final PublicKey feeReceiverKey,
                                                           final PublicKey withdrawWithheldAuthorityKey) {
    final var keys = withdrawWithheldTokensFromMintKeys(
      mintKey,
      feeReceiverKey,
      withdrawWithheldAuthorityKey
    );
    return withdrawWithheldTokensFromMint(invokedToken2022ProgramMeta, keys);
  }

  /// Transfer all withheld tokens in the mint to an account. Signed by the
  /// mint's withdraw withheld tokens authority.
  ///
  public static Instruction withdrawWithheldTokensFromMint(final AccountMeta invokedToken2022ProgramMeta,
                                                           final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = WITHDRAW_WITHHELD_TOKENS_FROM_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 2;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer all withheld tokens in the mint to an account. Signed by the
  /// mint's withdraw withheld tokens authority.
  ///
  public record WithdrawWithheldTokensFromMintIxData(int discriminator, int transferFeeDiscriminator) implements SerDe {  

    public static WithdrawWithheldTokensFromMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;

    public static WithdrawWithheldTokensFromMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeDiscriminator = _data[i] & 0xFF;
      return new WithdrawWithheldTokensFromMintIxData(discriminator, transferFeeDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferFeeDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_WITHHELD_TOKENS_FROM_ACCOUNTS_DISCRIMINATOR = toDiscriminator(26);

  /// Transfer all withheld tokens to an account. Signed by the mint's
  /// withdraw withheld tokens authority.
  ///
  /// @param mintKey The token mint. Must include the `TransferFeeConfig` extension.
  /// @param feeReceiverKey The fee receiver account. Must include the `TransferFeeAmount`
  ///                       extension associated with the provided mint.
  /// @param withdrawWithheldAuthorityKey The mint's `withdraw_withheld_authority` or its multisignature account.
  public static List<AccountMeta> withdrawWithheldTokensFromAccountsKeys(final PublicKey mintKey,
                                                                         final PublicKey feeReceiverKey,
                                                                         final PublicKey withdrawWithheldAuthorityKey) {
    return List.of(
      createRead(mintKey),
      createWrite(feeReceiverKey),
      createReadOnlySigner(withdrawWithheldAuthorityKey)
    );
  }

  /// Transfer all withheld tokens to an account. Signed by the mint's
  /// withdraw withheld tokens authority.
  ///
  /// @param mintKey The token mint. Must include the `TransferFeeConfig` extension.
  /// @param feeReceiverKey The fee receiver account. Must include the `TransferFeeAmount`
  ///                       extension associated with the provided mint.
  /// @param withdrawWithheldAuthorityKey The mint's `withdraw_withheld_authority` or its multisignature account.
  /// @param numTokenAccounts Number of token accounts harvested.
  public static Instruction withdrawWithheldTokensFromAccounts(final AccountMeta invokedToken2022ProgramMeta,
                                                               final PublicKey mintKey,
                                                               final PublicKey feeReceiverKey,
                                                               final PublicKey withdrawWithheldAuthorityKey,
                                                               final int numTokenAccounts) {
    final var keys = withdrawWithheldTokensFromAccountsKeys(
      mintKey,
      feeReceiverKey,
      withdrawWithheldAuthorityKey
    );
    return withdrawWithheldTokensFromAccounts(
      invokedToken2022ProgramMeta,
      keys,
      numTokenAccounts
    );
  }

  /// Transfer all withheld tokens to an account. Signed by the mint's
  /// withdraw withheld tokens authority.
  ///
  /// @param numTokenAccounts Number of token accounts harvested.
  public static Instruction withdrawWithheldTokensFromAccounts(final AccountMeta invokedToken2022ProgramMeta,
                                                               final List<AccountMeta> keys,
                                                               final int numTokenAccounts) {
    final byte[] _data = new byte[3];
    int i = WITHDRAW_WITHHELD_TOKENS_FROM_ACCOUNTS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 3;
    ++i;
    _data[i] = (byte) numTokenAccounts;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer all withheld tokens to an account. Signed by the mint's
  /// withdraw withheld tokens authority.
  ///
  /// @param numTokenAccounts Number of token accounts harvested.
  public record WithdrawWithheldTokensFromAccountsIxData(int discriminator,
                                                         int transferFeeDiscriminator,
                                                         int numTokenAccounts) implements SerDe {  

    public static WithdrawWithheldTokensFromAccountsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 3;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int NUM_TOKEN_ACCOUNTS_OFFSET = 2;

    public static WithdrawWithheldTokensFromAccountsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final var numTokenAccounts = _data[i] & 0xFF;
      return new WithdrawWithheldTokensFromAccountsIxData(discriminator, transferFeeDiscriminator, numTokenAccounts);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferFeeDiscriminator;
      ++i;
      _data[i] = (byte) numTokenAccounts;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator HARVEST_WITHHELD_TOKENS_TO_MINT_DISCRIMINATOR = toDiscriminator(26);

  /// Permissionless instruction to transfer all withheld tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include the `TransferFeeAmount` extension.
  /// If not, the account is skipped.
  ///
  /// @param mintKey The token mint.
  public static List<AccountMeta> harvestWithheldTokensToMintKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Permissionless instruction to transfer all withheld tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include the `TransferFeeAmount` extension.
  /// If not, the account is skipped.
  ///
  /// @param mintKey The token mint.
  public static Instruction harvestWithheldTokensToMint(final AccountMeta invokedToken2022ProgramMeta,
                                                        final PublicKey mintKey) {
    final var keys = harvestWithheldTokensToMintKeys(
      mintKey
    );
    return harvestWithheldTokensToMint(invokedToken2022ProgramMeta, keys);
  }

  /// Permissionless instruction to transfer all withheld tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include the `TransferFeeAmount` extension.
  /// If not, the account is skipped.
  ///
  public static Instruction harvestWithheldTokensToMint(final AccountMeta invokedToken2022ProgramMeta,
                                                        final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = HARVEST_WITHHELD_TOKENS_TO_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 4;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Permissionless instruction to transfer all withheld tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include the `TransferFeeAmount` extension.
  /// If not, the account is skipped.
  ///
  public record HarvestWithheldTokensToMintIxData(int discriminator, int transferFeeDiscriminator) implements SerDe {  

    public static HarvestWithheldTokensToMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;

    public static HarvestWithheldTokensToMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeDiscriminator = _data[i] & 0xFF;
      return new HarvestWithheldTokensToMintIxData(discriminator, transferFeeDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferFeeDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_TRANSFER_FEE_DISCRIMINATOR = toDiscriminator(26);

  /// Set transfer fee. Only supported for mints that include the
  /// `TransferFeeConfig` extension.
  ///
  /// @param mintKey The mint.
  /// @param transferFeeConfigAuthorityKey The mint's fee account owner or its multisignature account.
  public static List<AccountMeta> setTransferFeeKeys(final PublicKey mintKey,
                                                     final PublicKey transferFeeConfigAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(transferFeeConfigAuthorityKey)
    );
  }

  /// Set transfer fee. Only supported for mints that include the
  /// `TransferFeeConfig` extension.
  ///
  /// @param mintKey The mint.
  /// @param transferFeeConfigAuthorityKey The mint's fee account owner or its multisignature account.
  /// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the transfer amount.
  /// @param maximumFee Maximum fee assessed on transfers.
  public static Instruction setTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                           final PublicKey mintKey,
                                           final PublicKey transferFeeConfigAuthorityKey,
                                           final int transferFeeBasisPoints,
                                           final long maximumFee) {
    final var keys = setTransferFeeKeys(
      mintKey,
      transferFeeConfigAuthorityKey
    );
    return setTransferFee(
      invokedToken2022ProgramMeta,
      keys,
      transferFeeBasisPoints,
      maximumFee
    );
  }

  /// Set transfer fee. Only supported for mints that include the
  /// `TransferFeeConfig` extension.
  ///
  /// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the transfer amount.
  /// @param maximumFee Maximum fee assessed on transfers.
  public static Instruction setTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                           final List<AccountMeta> keys,
                                           final int transferFeeBasisPoints,
                                           final long maximumFee) {
    final byte[] _data = new byte[12];
    int i = SET_TRANSFER_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 5;
    ++i;
    putInt16LE(_data, i, transferFeeBasisPoints);
    i += 2;
    putInt64LE(_data, i, maximumFee);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Set transfer fee. Only supported for mints that include the
  /// `TransferFeeConfig` extension.
  ///
  /// @param transferFeeBasisPoints Amount of transfer collected as fees, expressed as basis points of the transfer amount.
  /// @param maximumFee Maximum fee assessed on transfers.
  public record SetTransferFeeIxData(int discriminator,
                                     int transferFeeDiscriminator,
                                     int transferFeeBasisPoints,
                                     long maximumFee) implements SerDe {  

    public static SetTransferFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int TRANSFER_FEE_BASIS_POINTS_OFFSET = 2;
    public static final int MAXIMUM_FEE_OFFSET = 4;

    public static SetTransferFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final var transferFeeBasisPoints = getInt16LE(_data, i);
      i += 2;
      final var maximumFee = getInt64LE(_data, i);
      return new SetTransferFeeIxData(discriminator,
                                      transferFeeDiscriminator,
                                      transferFeeBasisPoints,
                                      maximumFee);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferFeeDiscriminator;
      ++i;
      putInt16LE(_data, i, transferFeeBasisPoints);
      i += 2;
      putInt64LE(_data, i, maximumFee);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_CONFIDENTIAL_TRANSFER_MINT_DISCRIMINATOR = toDiscriminator(27);

  /// Initializes confidential transfers for a mint.
  /// 
  /// The `ConfidentialTransferInstruction::InitializeMint` instruction
  /// requires no signers and MUST be included within the same Transaction
  /// as `TokenInstruction::InitializeMint`. Otherwise another party can
  /// initialize the configuration.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeMint`
  /// instruction has already executed for the mint.
  ///
  /// @param mintKey The SPL Token mint.
  public static List<AccountMeta> initializeConfidentialTransferMintKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initializes confidential transfers for a mint.
  /// 
  /// The `ConfidentialTransferInstruction::InitializeMint` instruction
  /// requires no signers and MUST be included within the same Transaction
  /// as `TokenInstruction::InitializeMint`. Otherwise another party can
  /// initialize the configuration.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeMint`
  /// instruction has already executed for the mint.
  ///
  /// @param mintKey The SPL Token mint.
  /// @param authority Authority to modify the `ConfidentialTransferMint` configuration and to
  ///                  approve new accounts.
  /// @param autoApproveNewAccounts Determines if newly configured accounts must be approved by the
  ///                               `authority` before they may be used by the user.
  /// @param auditorElgamalPubkey New authority to decode any transfer amount in a confidential transfer.
  public static Instruction initializeConfidentialTransferMint(final AccountMeta invokedToken2022ProgramMeta,
                                                               final PublicKey mintKey,
                                                               final PublicKey authority,
                                                               final boolean autoApproveNewAccounts,
                                                               final PublicKey auditorElgamalPubkey) {
    final var keys = initializeConfidentialTransferMintKeys(
      mintKey
    );
    return initializeConfidentialTransferMint(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      autoApproveNewAccounts,
      auditorElgamalPubkey
    );
  }

  /// Initializes confidential transfers for a mint.
  /// 
  /// The `ConfidentialTransferInstruction::InitializeMint` instruction
  /// requires no signers and MUST be included within the same Transaction
  /// as `TokenInstruction::InitializeMint`. Otherwise another party can
  /// initialize the configuration.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeMint`
  /// instruction has already executed for the mint.
  ///
  /// @param authority Authority to modify the `ConfidentialTransferMint` configuration and to
  ///                  approve new accounts.
  /// @param autoApproveNewAccounts Determines if newly configured accounts must be approved by the
  ///                               `authority` before they may be used by the user.
  /// @param auditorElgamalPubkey New authority to decode any transfer amount in a confidential transfer.
  public static Instruction initializeConfidentialTransferMint(final AccountMeta invokedToken2022ProgramMeta,
                                                               final List<AccountMeta> keys,
                                                               final PublicKey authority,
                                                               final boolean autoApproveNewAccounts,
                                                               final PublicKey auditorElgamalPubkey) {
    final byte[] _data = new byte[67];
    int i = INITIALIZE_CONFIDENTIAL_TRANSFER_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    _data[i] = (byte) (autoApproveNewAccounts ? 1 : 0);
    ++i;
    if (auditorElgamalPubkey != null) {
      auditorElgamalPubkey.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initializes confidential transfers for a mint.
  /// 
  /// The `ConfidentialTransferInstruction::InitializeMint` instruction
  /// requires no signers and MUST be included within the same Transaction
  /// as `TokenInstruction::InitializeMint`. Otherwise another party can
  /// initialize the configuration.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeMint`
  /// instruction has already executed for the mint.
  ///
  /// @param authority Authority to modify the `ConfidentialTransferMint` configuration and to
  ///                  approve new accounts.
  /// @param autoApproveNewAccounts Determines if newly configured accounts must be approved by the
  ///                               `authority` before they may be used by the user.
  /// @param auditorElgamalPubkey New authority to decode any transfer amount in a confidential transfer.
  public record InitializeConfidentialTransferMintIxData(int discriminator,
                                                         int confidentialTransferDiscriminator,
                                                         PublicKey authority,
                                                         boolean autoApproveNewAccounts,
                                                         PublicKey auditorElgamalPubkey) implements SerDe {  

    public static InitializeConfidentialTransferMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 67;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int AUTO_APPROVE_NEW_ACCOUNTS_OFFSET = 34;
    public static final int AUDITOR_ELGAMAL_PUBKEY_OFFSET = 35;

    public static InitializeConfidentialTransferMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var autoApproveNewAccounts = _data[i] == 1;
      ++i;
      final var auditorElgamalPubkey = readPubKey(_data, i);
      return new InitializeConfidentialTransferMintIxData(discriminator,
                                                          confidentialTransferDiscriminator,
                                                          authority,
                                                          autoApproveNewAccounts,
                                                          auditorElgamalPubkey);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      _data[i] = (byte) (autoApproveNewAccounts ? 1 : 0);
      ++i;
      if (auditorElgamalPubkey != null) {
        auditorElgamalPubkey.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_CONFIDENTIAL_TRANSFER_MINT_DISCRIMINATOR = toDiscriminator(27);

  /// Updates the confidential transfer mint configuration for a mint.
  /// 
  /// Use `TokenInstruction::SetAuthority` to update the confidential transfer
  /// mint authority.
  ///
  /// @param mintKey The SPL Token mint.
  /// @param authorityKey Confidential transfer mint authority.
  public static List<AccountMeta> updateConfidentialTransferMintKeys(final PublicKey mintKey,
                                                                     final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Updates the confidential transfer mint configuration for a mint.
  /// 
  /// Use `TokenInstruction::SetAuthority` to update the confidential transfer
  /// mint authority.
  ///
  /// @param mintKey The SPL Token mint.
  /// @param authorityKey Confidential transfer mint authority.
  /// @param autoApproveNewAccounts Determines if newly configured accounts must be approved by the
  ///                               `authority` before they may be used by the user.
  /// @param auditorElgamalPubkey New authority to decode any transfer amount in a confidential transfer.
  public static Instruction updateConfidentialTransferMint(final AccountMeta invokedToken2022ProgramMeta,
                                                           final PublicKey mintKey,
                                                           final PublicKey authorityKey,
                                                           final boolean autoApproveNewAccounts,
                                                           final PublicKey auditorElgamalPubkey) {
    final var keys = updateConfidentialTransferMintKeys(
      mintKey,
      authorityKey
    );
    return updateConfidentialTransferMint(
      invokedToken2022ProgramMeta,
      keys,
      autoApproveNewAccounts,
      auditorElgamalPubkey
    );
  }

  /// Updates the confidential transfer mint configuration for a mint.
  /// 
  /// Use `TokenInstruction::SetAuthority` to update the confidential transfer
  /// mint authority.
  ///
  /// @param autoApproveNewAccounts Determines if newly configured accounts must be approved by the
  ///                               `authority` before they may be used by the user.
  /// @param auditorElgamalPubkey New authority to decode any transfer amount in a confidential transfer.
  public static Instruction updateConfidentialTransferMint(final AccountMeta invokedToken2022ProgramMeta,
                                                           final List<AccountMeta> keys,
                                                           final boolean autoApproveNewAccounts,
                                                           final PublicKey auditorElgamalPubkey) {
    final byte[] _data = new byte[35];
    int i = UPDATE_CONFIDENTIAL_TRANSFER_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    _data[i] = (byte) (autoApproveNewAccounts ? 1 : 0);
    ++i;
    if (auditorElgamalPubkey != null) {
      auditorElgamalPubkey.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Updates the confidential transfer mint configuration for a mint.
  /// 
  /// Use `TokenInstruction::SetAuthority` to update the confidential transfer
  /// mint authority.
  ///
  /// @param autoApproveNewAccounts Determines if newly configured accounts must be approved by the
  ///                               `authority` before they may be used by the user.
  /// @param auditorElgamalPubkey New authority to decode any transfer amount in a confidential transfer.
  public record UpdateConfidentialTransferMintIxData(int discriminator,
                                                     int confidentialTransferDiscriminator,
                                                     boolean autoApproveNewAccounts,
                                                     PublicKey auditorElgamalPubkey) implements SerDe {  

    public static UpdateConfidentialTransferMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 35;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTO_APPROVE_NEW_ACCOUNTS_OFFSET = 2;
    public static final int AUDITOR_ELGAMAL_PUBKEY_OFFSET = 3;

    public static UpdateConfidentialTransferMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var autoApproveNewAccounts = _data[i] == 1;
      ++i;
      final var auditorElgamalPubkey = readPubKey(_data, i);
      return new UpdateConfidentialTransferMintIxData(discriminator,
                                                      confidentialTransferDiscriminator,
                                                      autoApproveNewAccounts,
                                                      auditorElgamalPubkey);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      _data[i] = (byte) (autoApproveNewAccounts ? 1 : 0);
      ++i;
      if (auditorElgamalPubkey != null) {
        auditorElgamalPubkey.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CONFIGURE_CONFIDENTIAL_TRANSFER_ACCOUNT_DISCRIMINATOR = toDiscriminator(27);

  /// Configures confidential transfers for a token account.
  /// 
  /// The instruction fails if the confidential transfers are already
  /// configured, or if the mint was not initialized with confidential
  /// transfer support.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeAccount`
  /// instruction has not yet successfully executed for the token account.
  /// 
  /// Upon success, confidential and non-confidential deposits and transfers
  /// are enabled. Use the `DisableConfidentialCredits` and
  /// `DisableNonConfidentialCredits` instructions to disable.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyPubkeyValidity` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param recordKey (Optional) Record account if the accompanying proof is to be read from a record account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> configureConfidentialTransferAccountKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                                           final SolanaAccounts solanaAccounts,
                                                                           final PublicKey tokenKey,
                                                                           final PublicKey mintKey,
                                                                           final PublicKey recordKey,
                                                                           final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(mintKey),
      createRead(solanaAccounts.instructionsSysVar()),
      createRead(requireNonNullElse(recordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configures confidential transfers for a token account.
  /// 
  /// The instruction fails if the confidential transfers are already
  /// configured, or if the mint was not initialized with confidential
  /// transfer support.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeAccount`
  /// instruction has not yet successfully executed for the token account.
  /// 
  /// Upon success, confidential and non-confidential deposits and transfers
  /// are enabled. Use the `DisableConfidentialCredits` and
  /// `DisableNonConfidentialCredits` instructions to disable.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyPubkeyValidity` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param recordKey (Optional) Record account if the accompanying proof is to be read from a record account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param decryptableZeroBalance The decryptable balance (always 0) once the configure account succeeds.
  /// @param maximumPendingBalanceCreditCounter The maximum number of despots and transfers that an account can receiver
  ///                                           before the `ApplyPendingBalance` is executed
  /// @param proofInstructionOffset Relative location of the `ProofInstruction::ZeroCiphertextProof`
  ///                               instruction to the `ConfigureAccount` instruction in the
  ///                               transaction. If the offset is `0`, then use a context state account
  ///                               for the proof.
  public static Instruction configureConfidentialTransferAccount(final AccountMeta invokedToken2022ProgramMeta,
                                                                 final SolanaAccounts solanaAccounts,
                                                                 final PublicKey tokenKey,
                                                                 final PublicKey mintKey,
                                                                 final PublicKey recordKey,
                                                                 final PublicKey authorityKey,
                                                                 final DecryptableBalance decryptableZeroBalance,
                                                                 final long maximumPendingBalanceCreditCounter,
                                                                 final int proofInstructionOffset) {
    final var keys = configureConfidentialTransferAccountKeys(
      invokedToken2022ProgramMeta,
      solanaAccounts,
      tokenKey,
      mintKey,
      recordKey,
      authorityKey
    );
    return configureConfidentialTransferAccount(
      invokedToken2022ProgramMeta,
      keys,
      decryptableZeroBalance,
      maximumPendingBalanceCreditCounter,
      proofInstructionOffset
    );
  }

  /// Configures confidential transfers for a token account.
  /// 
  /// The instruction fails if the confidential transfers are already
  /// configured, or if the mint was not initialized with confidential
  /// transfer support.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeAccount`
  /// instruction has not yet successfully executed for the token account.
  /// 
  /// Upon success, confidential and non-confidential deposits and transfers
  /// are enabled. Use the `DisableConfidentialCredits` and
  /// `DisableNonConfidentialCredits` instructions to disable.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyPubkeyValidity` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param decryptableZeroBalance The decryptable balance (always 0) once the configure account succeeds.
  /// @param maximumPendingBalanceCreditCounter The maximum number of despots and transfers that an account can receiver
  ///                                           before the `ApplyPendingBalance` is executed
  /// @param proofInstructionOffset Relative location of the `ProofInstruction::ZeroCiphertextProof`
  ///                               instruction to the `ConfigureAccount` instruction in the
  ///                               transaction. If the offset is `0`, then use a context state account
  ///                               for the proof.
  public static Instruction configureConfidentialTransferAccount(final AccountMeta invokedToken2022ProgramMeta,
                                                                 final List<AccountMeta> keys,
                                                                 final DecryptableBalance decryptableZeroBalance,
                                                                 final long maximumPendingBalanceCreditCounter,
                                                                 final int proofInstructionOffset) {
    final byte[] _data = new byte[11 + decryptableZeroBalance.l()];
    int i = CONFIGURE_CONFIDENTIAL_TRANSFER_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 2;
    ++i;
    i += decryptableZeroBalance.write(_data, i);
    putInt64LE(_data, i, maximumPendingBalanceCreditCounter);
    i += 8;
    _data[i] = (byte) proofInstructionOffset;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configures confidential transfers for a token account.
  /// 
  /// The instruction fails if the confidential transfers are already
  /// configured, or if the mint was not initialized with confidential
  /// transfer support.
  /// 
  /// The instruction fails if the `TokenInstruction::InitializeAccount`
  /// instruction has not yet successfully executed for the token account.
  /// 
  /// Upon success, confidential and non-confidential deposits and transfers
  /// are enabled. Use the `DisableConfidentialCredits` and
  /// `DisableNonConfidentialCredits` instructions to disable.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyPubkeyValidity` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param decryptableZeroBalance The decryptable balance (always 0) once the configure account succeeds.
  /// @param maximumPendingBalanceCreditCounter The maximum number of despots and transfers that an account can receiver
  ///                                           before the `ApplyPendingBalance` is executed
  /// @param proofInstructionOffset Relative location of the `ProofInstruction::ZeroCiphertextProof`
  ///                               instruction to the `ConfigureAccount` instruction in the
  ///                               transaction. If the offset is `0`, then use a context state account
  ///                               for the proof.
  public record ConfigureConfidentialTransferAccountIxData(int discriminator,
                                                           int confidentialTransferDiscriminator,
                                                           DecryptableBalance decryptableZeroBalance,
                                                           long maximumPendingBalanceCreditCounter,
                                                           int proofInstructionOffset) implements SerDe {  

    public static ConfigureConfidentialTransferAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 47;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int DECRYPTABLE_ZERO_BALANCE_OFFSET = 2;
    public static final int MAXIMUM_PENDING_BALANCE_CREDIT_COUNTER_OFFSET = 38;
    public static final int PROOF_INSTRUCTION_OFFSET_OFFSET = 46;

    public static ConfigureConfidentialTransferAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var decryptableZeroBalance = DecryptableBalance.read(_data, i);
      i += decryptableZeroBalance.l();
      final var maximumPendingBalanceCreditCounter = getInt64LE(_data, i);
      i += 8;
      final var proofInstructionOffset = _data[i];
      return new ConfigureConfidentialTransferAccountIxData(discriminator,
                                                            confidentialTransferDiscriminator,
                                                            decryptableZeroBalance,
                                                            maximumPendingBalanceCreditCounter,
                                                            proofInstructionOffset);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      i += decryptableZeroBalance.write(_data, i);
      putInt64LE(_data, i, maximumPendingBalanceCreditCounter);
      i += 8;
      _data[i] = (byte) proofInstructionOffset;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator APPROVE_CONFIDENTIAL_TRANSFER_ACCOUNT_DISCRIMINATOR = toDiscriminator(27);

  /// Approves a token account for confidential transfers.
  /// 
  /// Approval is only required when the
  /// `ConfidentialTransferMint::approve_new_accounts` field is set in the
  /// SPL Token mint.  This instruction must be executed after the account
  /// owner configures their account for confidential transfers with
  /// `ConfidentialTransferInstruction::ConfigureAccount`.
  ///
  /// @param tokenKey The SPL Token account to approve.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param authorityKey Confidential transfer mint authority.
  public static List<AccountMeta> approveConfidentialTransferAccountKeys(final PublicKey tokenKey,
                                                                         final PublicKey mintKey,
                                                                         final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Approves a token account for confidential transfers.
  /// 
  /// Approval is only required when the
  /// `ConfidentialTransferMint::approve_new_accounts` field is set in the
  /// SPL Token mint.  This instruction must be executed after the account
  /// owner configures their account for confidential transfers with
  /// `ConfidentialTransferInstruction::ConfigureAccount`.
  ///
  /// @param tokenKey The SPL Token account to approve.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param authorityKey Confidential transfer mint authority.
  public static Instruction approveConfidentialTransferAccount(final AccountMeta invokedToken2022ProgramMeta,
                                                               final PublicKey tokenKey,
                                                               final PublicKey mintKey,
                                                               final PublicKey authorityKey) {
    final var keys = approveConfidentialTransferAccountKeys(
      tokenKey,
      mintKey,
      authorityKey
    );
    return approveConfidentialTransferAccount(invokedToken2022ProgramMeta, keys);
  }

  /// Approves a token account for confidential transfers.
  /// 
  /// Approval is only required when the
  /// `ConfidentialTransferMint::approve_new_accounts` field is set in the
  /// SPL Token mint.  This instruction must be executed after the account
  /// owner configures their account for confidential transfers with
  /// `ConfidentialTransferInstruction::ConfigureAccount`.
  ///
  public static Instruction approveConfidentialTransferAccount(final AccountMeta invokedToken2022ProgramMeta,
                                                               final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = APPROVE_CONFIDENTIAL_TRANSFER_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 3;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Approves a token account for confidential transfers.
  /// 
  /// Approval is only required when the
  /// `ConfidentialTransferMint::approve_new_accounts` field is set in the
  /// SPL Token mint.  This instruction must be executed after the account
  /// owner configures their account for confidential transfers with
  /// `ConfidentialTransferInstruction::ConfigureAccount`.
  ///
  public record ApproveConfidentialTransferAccountIxData(int discriminator, int confidentialTransferDiscriminator) implements SerDe {  

    public static ApproveConfidentialTransferAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;

    public static ApproveConfidentialTransferAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      return new ApproveConfidentialTransferAccountIxData(discriminator, confidentialTransferDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator EMPTY_CONFIDENTIAL_TRANSFER_ACCOUNT_DISCRIMINATOR = toDiscriminator(27);

  /// Empty the available balance in a confidential token account.
  /// 
  /// A token account that is extended for confidential transfers can only be
  /// closed if the pending and available balance ciphertexts are emptied.
  /// The pending balance can be emptied
  /// via the `ConfidentialTransferInstruction::ApplyPendingBalance`
  /// instruction. Use the `ConfidentialTransferInstruction::EmptyAccount`
  /// instruction to empty the available balance ciphertext.
  /// 
  /// Note that a newly configured account is always empty, so this
  /// instruction is not required prior to account closing if no
  /// instructions beyond
  /// `ConfidentialTransferInstruction::ConfigureAccount` have affected the
  /// token account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyZeroCiphertext` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param recordKey (Optional) Record account if the accompanying proof is to be read from a record account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> emptyConfidentialTransferAccountKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                                       final SolanaAccounts solanaAccounts,
                                                                       final PublicKey tokenKey,
                                                                       final PublicKey recordKey,
                                                                       final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(solanaAccounts.instructionsSysVar()),
      createRead(requireNonNullElse(recordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Empty the available balance in a confidential token account.
  /// 
  /// A token account that is extended for confidential transfers can only be
  /// closed if the pending and available balance ciphertexts are emptied.
  /// The pending balance can be emptied
  /// via the `ConfidentialTransferInstruction::ApplyPendingBalance`
  /// instruction. Use the `ConfidentialTransferInstruction::EmptyAccount`
  /// instruction to empty the available balance ciphertext.
  /// 
  /// Note that a newly configured account is always empty, so this
  /// instruction is not required prior to account closing if no
  /// instructions beyond
  /// `ConfidentialTransferInstruction::ConfigureAccount` have affected the
  /// token account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyZeroCiphertext` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param recordKey (Optional) Record account if the accompanying proof is to be read from a record account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param proofInstructionOffset Relative location of the `ProofInstruction::VerifyCloseAccount`
  ///                               instruction to the `EmptyAccount` instruction in the transaction. If
  ///                               the offset is `0`, then use a context state account for the proof.
  public static Instruction emptyConfidentialTransferAccount(final AccountMeta invokedToken2022ProgramMeta,
                                                             final SolanaAccounts solanaAccounts,
                                                             final PublicKey tokenKey,
                                                             final PublicKey recordKey,
                                                             final PublicKey authorityKey,
                                                             final int proofInstructionOffset) {
    final var keys = emptyConfidentialTransferAccountKeys(
      invokedToken2022ProgramMeta,
      solanaAccounts,
      tokenKey,
      recordKey,
      authorityKey
    );
    return emptyConfidentialTransferAccount(
      invokedToken2022ProgramMeta,
      keys,
      proofInstructionOffset
    );
  }

  /// Empty the available balance in a confidential token account.
  /// 
  /// A token account that is extended for confidential transfers can only be
  /// closed if the pending and available balance ciphertexts are emptied.
  /// The pending balance can be emptied
  /// via the `ConfidentialTransferInstruction::ApplyPendingBalance`
  /// instruction. Use the `ConfidentialTransferInstruction::EmptyAccount`
  /// instruction to empty the available balance ciphertext.
  /// 
  /// Note that a newly configured account is always empty, so this
  /// instruction is not required prior to account closing if no
  /// instructions beyond
  /// `ConfidentialTransferInstruction::ConfigureAccount` have affected the
  /// token account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyZeroCiphertext` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param proofInstructionOffset Relative location of the `ProofInstruction::VerifyCloseAccount`
  ///                               instruction to the `EmptyAccount` instruction in the transaction. If
  ///                               the offset is `0`, then use a context state account for the proof.
  public static Instruction emptyConfidentialTransferAccount(final AccountMeta invokedToken2022ProgramMeta,
                                                             final List<AccountMeta> keys,
                                                             final int proofInstructionOffset) {
    final byte[] _data = new byte[3];
    int i = EMPTY_CONFIDENTIAL_TRANSFER_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 4;
    ++i;
    _data[i] = (byte) proofInstructionOffset;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Empty the available balance in a confidential token account.
  /// 
  /// A token account that is extended for confidential transfers can only be
  /// closed if the pending and available balance ciphertexts are emptied.
  /// The pending balance can be emptied
  /// via the `ConfidentialTransferInstruction::ApplyPendingBalance`
  /// instruction. Use the `ConfidentialTransferInstruction::EmptyAccount`
  /// instruction to empty the available balance ciphertext.
  /// 
  /// Note that a newly configured account is always empty, so this
  /// instruction is not required prior to account closing if no
  /// instructions beyond
  /// `ConfidentialTransferInstruction::ConfigureAccount` have affected the
  /// token account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the `VerifyZeroCiphertext` instruction of the
  /// `zk_elgamal_proof` program in the same transaction or the address of a
  /// context state account for the proof must be provided.
  ///
  /// @param proofInstructionOffset Relative location of the `ProofInstruction::VerifyCloseAccount`
  ///                               instruction to the `EmptyAccount` instruction in the transaction. If
  ///                               the offset is `0`, then use a context state account for the proof.
  public record EmptyConfidentialTransferAccountIxData(int discriminator,
                                                       int confidentialTransferDiscriminator,
                                                       int proofInstructionOffset) implements SerDe {  

    public static EmptyConfidentialTransferAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 3;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int PROOF_INSTRUCTION_OFFSET_OFFSET = 2;

    public static EmptyConfidentialTransferAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var proofInstructionOffset = _data[i];
      return new EmptyConfidentialTransferAccountIxData(discriminator, confidentialTransferDiscriminator, proofInstructionOffset);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      _data[i] = (byte) proofInstructionOffset;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CONFIDENTIAL_DEPOSIT_DISCRIMINATOR = toDiscriminator(27);

  /// Deposit SPL Tokens into the pending balance of a confidential token
  /// account.
  /// 
  /// The account owner can then invoke the `ApplyPendingBalance` instruction
  /// to roll the deposit into their available balance at a time of their
  /// choosing.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> confidentialDepositKeys(final PublicKey tokenKey,
                                                          final PublicKey mintKey,
                                                          final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Deposit SPL Tokens into the pending balance of a confidential token
  /// account.
  /// 
  /// The account owner can then invoke the `ApplyPendingBalance` instruction
  /// to roll the deposit into their available balance at a time of their
  /// choosing.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to deposit.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction confidentialDeposit(final AccountMeta invokedToken2022ProgramMeta,
                                                final PublicKey tokenKey,
                                                final PublicKey mintKey,
                                                final PublicKey authorityKey,
                                                final long amount,
                                                final int decimals) {
    final var keys = confidentialDepositKeys(
      tokenKey,
      mintKey,
      authorityKey
    );
    return confidentialDeposit(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals
    );
  }

  /// Deposit SPL Tokens into the pending balance of a confidential token
  /// account.
  /// 
  /// The account owner can then invoke the `ApplyPendingBalance` instruction
  /// to roll the deposit into their available balance at a time of their
  /// choosing.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param amount The amount of tokens to deposit.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction confidentialDeposit(final AccountMeta invokedToken2022ProgramMeta,
                                                final List<AccountMeta> keys,
                                                final long amount,
                                                final int decimals) {
    final byte[] _data = new byte[11];
    int i = CONFIDENTIAL_DEPOSIT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 5;
    ++i;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Deposit SPL Tokens into the pending balance of a confidential token
  /// account.
  /// 
  /// The account owner can then invoke the `ApplyPendingBalance` instruction
  /// to roll the deposit into their available balance at a time of their
  /// choosing.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param amount The amount of tokens to deposit.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public record ConfidentialDepositIxData(int discriminator,
                                          int confidentialTransferDiscriminator,
                                          long amount,
                                          int decimals) implements SerDe {  

    public static ConfidentialDepositIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int AMOUNT_OFFSET = 2;
    public static final int DECIMALS_OFFSET = 10;

    public static ConfidentialDepositIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      return new ConfidentialDepositIxData(discriminator,
                                           confidentialTransferDiscriminator,
                                           amount,
                                           decimals);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CONFIDENTIAL_WITHDRAW_DISCRIMINATOR = toDiscriminator(27);

  /// Withdraw SPL Tokens from the available balance of a confidential token
  /// account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedRangeProofU64`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account address need to be provided.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param instructionsSysvarKey Instructions sysvar if at least one of the
  ///                              `zk_elgamal_proof` instructions are included in the same
  ///                              transaction.
  /// @param equalityRecordKey (Optional) Equality proof record account or context state account.
  /// @param rangeRecordKey (Optional) Range proof record account or context state account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> confidentialWithdrawKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                           final PublicKey tokenKey,
                                                           final PublicKey mintKey,
                                                           final PublicKey instructionsSysvarKey,
                                                           final PublicKey equalityRecordKey,
                                                           final PublicKey rangeRecordKey,
                                                           final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createRead(mintKey),
      createRead(requireNonNullElse(instructionsSysvarKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(equalityRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(rangeRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Withdraw SPL Tokens from the available balance of a confidential token
  /// account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedRangeProofU64`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account address need to be provided.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param instructionsSysvarKey Instructions sysvar if at least one of the
  ///                              `zk_elgamal_proof` instructions are included in the same
  ///                              transaction.
  /// @param equalityRecordKey (Optional) Equality proof record account or context state account.
  /// @param rangeRecordKey (Optional) Range proof record account or context state account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to withdraw.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  /// @param newDecryptableAvailableBalance The new decryptable balance if the withdrawal succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `Withdraw` instruction in the transaction. If the offset is
  ///                                       `0`, then use a context state account for the proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU64`
  ///                                    instruction to the `Withdraw` instruction in the transaction. If the
  ///                                    offset is `0`, then use a context state account for the proof.
  public static Instruction confidentialWithdraw(final AccountMeta invokedToken2022ProgramMeta,
                                                 final PublicKey tokenKey,
                                                 final PublicKey mintKey,
                                                 final PublicKey instructionsSysvarKey,
                                                 final PublicKey equalityRecordKey,
                                                 final PublicKey rangeRecordKey,
                                                 final PublicKey authorityKey,
                                                 final long amount,
                                                 final int decimals,
                                                 final DecryptableBalance newDecryptableAvailableBalance,
                                                 final int equalityProofInstructionOffset,
                                                 final int rangeProofInstructionOffset) {
    final var keys = confidentialWithdrawKeys(
      invokedToken2022ProgramMeta,
      tokenKey,
      mintKey,
      instructionsSysvarKey,
      equalityRecordKey,
      rangeRecordKey,
      authorityKey
    );
    return confidentialWithdraw(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals,
      newDecryptableAvailableBalance,
      equalityProofInstructionOffset,
      rangeProofInstructionOffset
    );
  }

  /// Withdraw SPL Tokens from the available balance of a confidential token
  /// account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedRangeProofU64`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account address need to be provided.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param amount The amount of tokens to withdraw.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  /// @param newDecryptableAvailableBalance The new decryptable balance if the withdrawal succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `Withdraw` instruction in the transaction. If the offset is
  ///                                       `0`, then use a context state account for the proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU64`
  ///                                    instruction to the `Withdraw` instruction in the transaction. If the
  ///                                    offset is `0`, then use a context state account for the proof.
  public static Instruction confidentialWithdraw(final AccountMeta invokedToken2022ProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final long amount,
                                                 final int decimals,
                                                 final DecryptableBalance newDecryptableAvailableBalance,
                                                 final int equalityProofInstructionOffset,
                                                 final int rangeProofInstructionOffset) {
    final byte[] _data = new byte[13 + newDecryptableAvailableBalance.l()];
    int i = CONFIDENTIAL_WITHDRAW_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 6;
    ++i;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;
    ++i;
    i += newDecryptableAvailableBalance.write(_data, i);
    _data[i] = (byte) equalityProofInstructionOffset;
    ++i;
    _data[i] = (byte) rangeProofInstructionOffset;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Withdraw SPL Tokens from the available balance of a confidential token
  /// account.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedRangeProofU64`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account address need to be provided.
  /// 
  /// Fails if the source or destination accounts are frozen.
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param amount The amount of tokens to withdraw.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  /// @param newDecryptableAvailableBalance The new decryptable balance if the withdrawal succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `Withdraw` instruction in the transaction. If the offset is
  ///                                       `0`, then use a context state account for the proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU64`
  ///                                    instruction to the `Withdraw` instruction in the transaction. If the
  ///                                    offset is `0`, then use a context state account for the proof.
  public record ConfidentialWithdrawIxData(int discriminator,
                                           int confidentialTransferDiscriminator,
                                           long amount,
                                           int decimals,
                                           DecryptableBalance newDecryptableAvailableBalance,
                                           int equalityProofInstructionOffset,
                                           int rangeProofInstructionOffset) implements SerDe {  

    public static ConfidentialWithdrawIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 49;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int AMOUNT_OFFSET = 2;
    public static final int DECIMALS_OFFSET = 10;
    public static final int NEW_DECRYPTABLE_AVAILABLE_BALANCE_OFFSET = 11;
    public static final int EQUALITY_PROOF_INSTRUCTION_OFFSET_OFFSET = 47;
    public static final int RANGE_PROOF_INSTRUCTION_OFFSET_OFFSET = 48;

    public static ConfidentialWithdrawIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      ++i;
      final var newDecryptableAvailableBalance = DecryptableBalance.read(_data, i);
      i += newDecryptableAvailableBalance.l();
      final var equalityProofInstructionOffset = _data[i];
      ++i;
      final var rangeProofInstructionOffset = _data[i];
      return new ConfidentialWithdrawIxData(discriminator,
                                            confidentialTransferDiscriminator,
                                            amount,
                                            decimals,
                                            newDecryptableAvailableBalance,
                                            equalityProofInstructionOffset,
                                            rangeProofInstructionOffset);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      i += newDecryptableAvailableBalance.write(_data, i);
      _data[i] = (byte) equalityProofInstructionOffset;
      ++i;
      _data[i] = (byte) rangeProofInstructionOffset;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CONFIDENTIAL_TRANSFER_DISCRIMINATOR = toDiscriminator(27);

  /// Transfer tokens confidentially.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity`
  /// - `VerifyBatchedRangeProofU128`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param sourceTokenKey The source SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param destinationTokenKey The destination SPL Token account.
  /// @param instructionsSysvarKey (Optional) Instructions sysvar if at least one of the
  ///                              `zk_elgamal_proof` instructions are included in the same
  ///                              transaction.
  /// @param equalityRecordKey (Optional) Equality proof record account or context state account.
  /// @param ciphertextValidityRecordKey (Optional) Ciphertext validity proof record account or context state account.
  /// @param rangeRecordKey (Optional) Range proof record account or context state account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> confidentialTransferKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                           final PublicKey sourceTokenKey,
                                                           final PublicKey mintKey,
                                                           final PublicKey destinationTokenKey,
                                                           final PublicKey instructionsSysvarKey,
                                                           final PublicKey equalityRecordKey,
                                                           final PublicKey ciphertextValidityRecordKey,
                                                           final PublicKey rangeRecordKey,
                                                           final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceTokenKey),
      createRead(mintKey),
      createWrite(destinationTokenKey),
      createRead(requireNonNullElse(instructionsSysvarKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(equalityRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(ciphertextValidityRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(rangeRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfer tokens confidentially.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity`
  /// - `VerifyBatchedRangeProofU128`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param sourceTokenKey The source SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param destinationTokenKey The destination SPL Token account.
  /// @param instructionsSysvarKey (Optional) Instructions sysvar if at least one of the
  ///                              `zk_elgamal_proof` instructions are included in the same
  ///                              transaction.
  /// @param equalityRecordKey (Optional) Equality proof record account or context state account.
  /// @param ciphertextValidityRecordKey (Optional) Ciphertext validity proof record account or context state account.
  /// @param rangeRecordKey (Optional) Range proof record account or context state account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param newSourceDecryptableAvailableBalance The new source decryptable balance if the transfer succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `Transfer` instruction in the transaction. If the offset is
  ///                                       `0`, then use a context state account for the proof.
  /// @param ciphertextValidityProofInstructionOffset Relative location of the
  ///                                                 `ProofInstruction::VerifyBatchedGroupedCiphertext3HandlesValidity`
  ///                                                 instruction to the `Transfer` instruction in the transaction. If the
  ///                                                 offset is `0`, then use a context state account for the proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU128Data`
  ///                                    instruction to the `Transfer` instruction in the transaction. If the
  ///                                    offset is `0`, then use a context state account for the proof.
  public static Instruction confidentialTransfer(final AccountMeta invokedToken2022ProgramMeta,
                                                 final PublicKey sourceTokenKey,
                                                 final PublicKey mintKey,
                                                 final PublicKey destinationTokenKey,
                                                 final PublicKey instructionsSysvarKey,
                                                 final PublicKey equalityRecordKey,
                                                 final PublicKey ciphertextValidityRecordKey,
                                                 final PublicKey rangeRecordKey,
                                                 final PublicKey authorityKey,
                                                 final DecryptableBalance newSourceDecryptableAvailableBalance,
                                                 final int equalityProofInstructionOffset,
                                                 final int ciphertextValidityProofInstructionOffset,
                                                 final int rangeProofInstructionOffset) {
    final var keys = confidentialTransferKeys(
      invokedToken2022ProgramMeta,
      sourceTokenKey,
      mintKey,
      destinationTokenKey,
      instructionsSysvarKey,
      equalityRecordKey,
      ciphertextValidityRecordKey,
      rangeRecordKey,
      authorityKey
    );
    return confidentialTransfer(
      invokedToken2022ProgramMeta,
      keys,
      newSourceDecryptableAvailableBalance,
      equalityProofInstructionOffset,
      ciphertextValidityProofInstructionOffset,
      rangeProofInstructionOffset
    );
  }

  /// Transfer tokens confidentially.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity`
  /// - `VerifyBatchedRangeProofU128`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param newSourceDecryptableAvailableBalance The new source decryptable balance if the transfer succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `Transfer` instruction in the transaction. If the offset is
  ///                                       `0`, then use a context state account for the proof.
  /// @param ciphertextValidityProofInstructionOffset Relative location of the
  ///                                                 `ProofInstruction::VerifyBatchedGroupedCiphertext3HandlesValidity`
  ///                                                 instruction to the `Transfer` instruction in the transaction. If the
  ///                                                 offset is `0`, then use a context state account for the proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU128Data`
  ///                                    instruction to the `Transfer` instruction in the transaction. If the
  ///                                    offset is `0`, then use a context state account for the proof.
  public static Instruction confidentialTransfer(final AccountMeta invokedToken2022ProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final DecryptableBalance newSourceDecryptableAvailableBalance,
                                                 final int equalityProofInstructionOffset,
                                                 final int ciphertextValidityProofInstructionOffset,
                                                 final int rangeProofInstructionOffset) {
    final byte[] _data = new byte[5 + newSourceDecryptableAvailableBalance.l()];
    int i = CONFIDENTIAL_TRANSFER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 7;
    ++i;
    i += newSourceDecryptableAvailableBalance.write(_data, i);
    _data[i] = (byte) equalityProofInstructionOffset;
    ++i;
    _data[i] = (byte) ciphertextValidityProofInstructionOffset;
    ++i;
    _data[i] = (byte) rangeProofInstructionOffset;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer tokens confidentially.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity`
  /// - `VerifyBatchedRangeProofU128`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// Fails if the associated mint is extended as `NonTransferable`.
  ///
  /// @param newSourceDecryptableAvailableBalance The new source decryptable balance if the transfer succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `Transfer` instruction in the transaction. If the offset is
  ///                                       `0`, then use a context state account for the proof.
  /// @param ciphertextValidityProofInstructionOffset Relative location of the
  ///                                                 `ProofInstruction::VerifyBatchedGroupedCiphertext3HandlesValidity`
  ///                                                 instruction to the `Transfer` instruction in the transaction. If the
  ///                                                 offset is `0`, then use a context state account for the proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU128Data`
  ///                                    instruction to the `Transfer` instruction in the transaction. If the
  ///                                    offset is `0`, then use a context state account for the proof.
  public record ConfidentialTransferIxData(int discriminator,
                                           int confidentialTransferDiscriminator,
                                           DecryptableBalance newSourceDecryptableAvailableBalance,
                                           int equalityProofInstructionOffset,
                                           int ciphertextValidityProofInstructionOffset,
                                           int rangeProofInstructionOffset) implements SerDe {  

    public static ConfidentialTransferIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 41;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int NEW_SOURCE_DECRYPTABLE_AVAILABLE_BALANCE_OFFSET = 2;
    public static final int EQUALITY_PROOF_INSTRUCTION_OFFSET_OFFSET = 38;
    public static final int CIPHERTEXT_VALIDITY_PROOF_INSTRUCTION_OFFSET_OFFSET = 39;
    public static final int RANGE_PROOF_INSTRUCTION_OFFSET_OFFSET = 40;

    public static ConfidentialTransferIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var newSourceDecryptableAvailableBalance = DecryptableBalance.read(_data, i);
      i += newSourceDecryptableAvailableBalance.l();
      final var equalityProofInstructionOffset = _data[i];
      ++i;
      final var ciphertextValidityProofInstructionOffset = _data[i];
      ++i;
      final var rangeProofInstructionOffset = _data[i];
      return new ConfidentialTransferIxData(discriminator,
                                            confidentialTransferDiscriminator,
                                            newSourceDecryptableAvailableBalance,
                                            equalityProofInstructionOffset,
                                            ciphertextValidityProofInstructionOffset,
                                            rangeProofInstructionOffset);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      i += newSourceDecryptableAvailableBalance.write(_data, i);
      _data[i] = (byte) equalityProofInstructionOffset;
      ++i;
      _data[i] = (byte) ciphertextValidityProofInstructionOffset;
      ++i;
      _data[i] = (byte) rangeProofInstructionOffset;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator APPLY_CONFIDENTIAL_PENDING_BALANCE_DISCRIMINATOR = toDiscriminator(27);

  /// Applies the pending balance to the available balance, based on the
  /// history of `Deposit` and/or `Transfer` instructions.
  /// 
  /// After submitting `ApplyPendingBalance`, the client should compare
  /// `ConfidentialTransferAccount::expected_pending_balance_credit_counter`
  /// with
  /// `ConfidentialTransferAccount::actual_applied_pending_balance_instructions`.  If they are
  /// equal then the
  /// `ConfidentialTransferAccount::decryptable_available_balance` is
  /// consistent with `ConfidentialTransferAccount::available_balance`. If
  /// they differ then there is more pending balance to be applied.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> applyConfidentialPendingBalanceKeys(final PublicKey tokenKey,
                                                                      final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Applies the pending balance to the available balance, based on the
  /// history of `Deposit` and/or `Transfer` instructions.
  /// 
  /// After submitting `ApplyPendingBalance`, the client should compare
  /// `ConfidentialTransferAccount::expected_pending_balance_credit_counter`
  /// with
  /// `ConfidentialTransferAccount::actual_applied_pending_balance_instructions`.  If they are
  /// equal then the
  /// `ConfidentialTransferAccount::decryptable_available_balance` is
  /// consistent with `ConfidentialTransferAccount::available_balance`. If
  /// they differ then there is more pending balance to be applied.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param expectedPendingBalanceCreditCounter The expected number of pending balance credits since the last successful
  ///                                            `ApplyPendingBalance` instruction
  /// @param newDecryptableAvailableBalance The new decryptable balance if the pending balance is applied
  ///                                       successfully
  public static Instruction applyConfidentialPendingBalance(final AccountMeta invokedToken2022ProgramMeta,
                                                            final PublicKey tokenKey,
                                                            final PublicKey authorityKey,
                                                            final long expectedPendingBalanceCreditCounter,
                                                            final DecryptableBalance newDecryptableAvailableBalance) {
    final var keys = applyConfidentialPendingBalanceKeys(
      tokenKey,
      authorityKey
    );
    return applyConfidentialPendingBalance(
      invokedToken2022ProgramMeta,
      keys,
      expectedPendingBalanceCreditCounter,
      newDecryptableAvailableBalance
    );
  }

  /// Applies the pending balance to the available balance, based on the
  /// history of `Deposit` and/or `Transfer` instructions.
  /// 
  /// After submitting `ApplyPendingBalance`, the client should compare
  /// `ConfidentialTransferAccount::expected_pending_balance_credit_counter`
  /// with
  /// `ConfidentialTransferAccount::actual_applied_pending_balance_instructions`.  If they are
  /// equal then the
  /// `ConfidentialTransferAccount::decryptable_available_balance` is
  /// consistent with `ConfidentialTransferAccount::available_balance`. If
  /// they differ then there is more pending balance to be applied.
  ///
  /// @param expectedPendingBalanceCreditCounter The expected number of pending balance credits since the last successful
  ///                                            `ApplyPendingBalance` instruction
  /// @param newDecryptableAvailableBalance The new decryptable balance if the pending balance is applied
  ///                                       successfully
  public static Instruction applyConfidentialPendingBalance(final AccountMeta invokedToken2022ProgramMeta,
                                                            final List<AccountMeta> keys,
                                                            final long expectedPendingBalanceCreditCounter,
                                                            final DecryptableBalance newDecryptableAvailableBalance) {
    final byte[] _data = new byte[10 + newDecryptableAvailableBalance.l()];
    int i = APPLY_CONFIDENTIAL_PENDING_BALANCE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 8;
    ++i;
    putInt64LE(_data, i, expectedPendingBalanceCreditCounter);
    i += 8;
    newDecryptableAvailableBalance.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Applies the pending balance to the available balance, based on the
  /// history of `Deposit` and/or `Transfer` instructions.
  /// 
  /// After submitting `ApplyPendingBalance`, the client should compare
  /// `ConfidentialTransferAccount::expected_pending_balance_credit_counter`
  /// with
  /// `ConfidentialTransferAccount::actual_applied_pending_balance_instructions`.  If they are
  /// equal then the
  /// `ConfidentialTransferAccount::decryptable_available_balance` is
  /// consistent with `ConfidentialTransferAccount::available_balance`. If
  /// they differ then there is more pending balance to be applied.
  ///
  /// @param expectedPendingBalanceCreditCounter The expected number of pending balance credits since the last successful
  ///                                            `ApplyPendingBalance` instruction
  /// @param newDecryptableAvailableBalance The new decryptable balance if the pending balance is applied
  ///                                       successfully
  public record ApplyConfidentialPendingBalanceIxData(int discriminator,
                                                      int confidentialTransferDiscriminator,
                                                      long expectedPendingBalanceCreditCounter,
                                                      DecryptableBalance newDecryptableAvailableBalance) implements SerDe {  

    public static ApplyConfidentialPendingBalanceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 46;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int EXPECTED_PENDING_BALANCE_CREDIT_COUNTER_OFFSET = 2;
    public static final int NEW_DECRYPTABLE_AVAILABLE_BALANCE_OFFSET = 10;

    public static ApplyConfidentialPendingBalanceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var expectedPendingBalanceCreditCounter = getInt64LE(_data, i);
      i += 8;
      final var newDecryptableAvailableBalance = DecryptableBalance.read(_data, i);
      return new ApplyConfidentialPendingBalanceIxData(discriminator,
                                                       confidentialTransferDiscriminator,
                                                       expectedPendingBalanceCreditCounter,
                                                       newDecryptableAvailableBalance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      putInt64LE(_data, i, expectedPendingBalanceCreditCounter);
      i += 8;
      i += newDecryptableAvailableBalance.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ENABLE_CONFIDENTIAL_CREDITS_DISCRIMINATOR = toDiscriminator(27);

  /// Configure a confidential extension account to accept incoming
  /// confidential transfers.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> enableConfidentialCreditsKeys(final PublicKey tokenKey,
                                                                final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configure a confidential extension account to accept incoming
  /// confidential transfers.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static Instruction enableConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                      final PublicKey tokenKey,
                                                      final PublicKey authorityKey) {
    final var keys = enableConfidentialCreditsKeys(
      tokenKey,
      authorityKey
    );
    return enableConfidentialCredits(invokedToken2022ProgramMeta, keys);
  }

  /// Configure a confidential extension account to accept incoming
  /// confidential transfers.
  ///
  public static Instruction enableConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                      final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = ENABLE_CONFIDENTIAL_CREDITS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 9;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configure a confidential extension account to accept incoming
  /// confidential transfers.
  ///
  public record EnableConfidentialCreditsIxData(int discriminator, int confidentialTransferDiscriminator) implements SerDe {  

    public static EnableConfidentialCreditsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;

    public static EnableConfidentialCreditsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      return new EnableConfidentialCreditsIxData(discriminator, confidentialTransferDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DISABLE_CONFIDENTIAL_CREDITS_DISCRIMINATOR = toDiscriminator(27);

  /// Configure a confidential extension account to reject any incoming
  /// confidential transfers.
  /// 
  /// If the `allow_non_confidential_credits` field is `true`, then the base
  /// account can still receive non-confidential transfers.
  /// 
  /// This instruction can be used to disable confidential payments after a
  /// token account has already been extended for confidential transfers.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> disableConfidentialCreditsKeys(final PublicKey tokenKey,
                                                                 final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configure a confidential extension account to reject any incoming
  /// confidential transfers.
  /// 
  /// If the `allow_non_confidential_credits` field is `true`, then the base
  /// account can still receive non-confidential transfers.
  /// 
  /// This instruction can be used to disable confidential payments after a
  /// token account has already been extended for confidential transfers.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static Instruction disableConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                       final PublicKey tokenKey,
                                                       final PublicKey authorityKey) {
    final var keys = disableConfidentialCreditsKeys(
      tokenKey,
      authorityKey
    );
    return disableConfidentialCredits(invokedToken2022ProgramMeta, keys);
  }

  /// Configure a confidential extension account to reject any incoming
  /// confidential transfers.
  /// 
  /// If the `allow_non_confidential_credits` field is `true`, then the base
  /// account can still receive non-confidential transfers.
  /// 
  /// This instruction can be used to disable confidential payments after a
  /// token account has already been extended for confidential transfers.
  ///
  public static Instruction disableConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                       final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = DISABLE_CONFIDENTIAL_CREDITS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 10;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configure a confidential extension account to reject any incoming
  /// confidential transfers.
  /// 
  /// If the `allow_non_confidential_credits` field is `true`, then the base
  /// account can still receive non-confidential transfers.
  /// 
  /// This instruction can be used to disable confidential payments after a
  /// token account has already been extended for confidential transfers.
  ///
  public record DisableConfidentialCreditsIxData(int discriminator, int confidentialTransferDiscriminator) implements SerDe {  

    public static DisableConfidentialCreditsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;

    public static DisableConfidentialCreditsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      return new DisableConfidentialCreditsIxData(discriminator, confidentialTransferDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ENABLE_NON_CONFIDENTIAL_CREDITS_DISCRIMINATOR = toDiscriminator(27);

  /// Configure an account with the confidential extension to accept incoming
  /// non-confidential transfers.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> enableNonConfidentialCreditsKeys(final PublicKey tokenKey,
                                                                   final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configure an account with the confidential extension to accept incoming
  /// non-confidential transfers.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static Instruction enableNonConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                         final PublicKey tokenKey,
                                                         final PublicKey authorityKey) {
    final var keys = enableNonConfidentialCreditsKeys(
      tokenKey,
      authorityKey
    );
    return enableNonConfidentialCredits(invokedToken2022ProgramMeta, keys);
  }

  /// Configure an account with the confidential extension to accept incoming
  /// non-confidential transfers.
  ///
  public static Instruction enableNonConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                         final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = ENABLE_NON_CONFIDENTIAL_CREDITS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 11;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configure an account with the confidential extension to accept incoming
  /// non-confidential transfers.
  ///
  public record EnableNonConfidentialCreditsIxData(int discriminator, int confidentialTransferDiscriminator) implements SerDe {  

    public static EnableNonConfidentialCreditsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;

    public static EnableNonConfidentialCreditsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      return new EnableNonConfidentialCreditsIxData(discriminator, confidentialTransferDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DISABLE_NON_CONFIDENTIAL_CREDITS_DISCRIMINATOR = toDiscriminator(27);

  /// Configure an account with the confidential extension to reject any
  /// incoming non-confidential transfers.
  /// 
  /// This instruction can be used to configure a confidential extension
  /// account to exclusively receive confidential payments.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> disableNonConfidentialCreditsKeys(final PublicKey tokenKey,
                                                                    final PublicKey authorityKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configure an account with the confidential extension to reject any
  /// incoming non-confidential transfers.
  /// 
  /// This instruction can be used to configure a confidential extension
  /// account to exclusively receive confidential payments.
  ///
  /// @param tokenKey The SPL Token account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static Instruction disableNonConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                          final PublicKey tokenKey,
                                                          final PublicKey authorityKey) {
    final var keys = disableNonConfidentialCreditsKeys(
      tokenKey,
      authorityKey
    );
    return disableNonConfidentialCredits(invokedToken2022ProgramMeta, keys);
  }

  /// Configure an account with the confidential extension to reject any
  /// incoming non-confidential transfers.
  /// 
  /// This instruction can be used to configure a confidential extension
  /// account to exclusively receive confidential payments.
  ///
  public static Instruction disableNonConfidentialCredits(final AccountMeta invokedToken2022ProgramMeta,
                                                          final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = DISABLE_NON_CONFIDENTIAL_CREDITS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 12;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configure an account with the confidential extension to reject any
  /// incoming non-confidential transfers.
  /// 
  /// This instruction can be used to configure a confidential extension
  /// account to exclusively receive confidential payments.
  ///
  public record DisableNonConfidentialCreditsIxData(int discriminator, int confidentialTransferDiscriminator) implements SerDe {  

    public static DisableNonConfidentialCreditsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;

    public static DisableNonConfidentialCreditsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      return new DisableNonConfidentialCreditsIxData(discriminator, confidentialTransferDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CONFIDENTIAL_TRANSFER_WITH_FEE_DISCRIMINATOR = toDiscriminator(27);

  /// Transfer tokens confidentially with fee.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity` (transfer amount
  /// ciphertext)
  /// - `VerifyPercentageWithFee`
  /// - `VerifyBatchedGroupedCiphertext2HandlesValidity` (fee ciphertext)
  /// - `VerifyBatchedRangeProofU256`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// The same restrictions for the `Transfer` applies to
  /// `TransferWithFee`. Namely, the instruction fails if the
  /// associated mint is extended as `NonTransferable`.
  ///
  /// @param sourceTokenKey The source SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param destinationTokenKey The destination SPL Token account.
  /// @param instructionsSysvarKey (Optional) Instructions sysvar if at least one of the
  ///                              `zk_elgamal_proof` instructions are included in the same
  ///                              transaction.
  /// @param equalityRecordKey (Optional) Equality proof record account or context state account.
  /// @param transferAmountCiphertextValidityRecordKey (Optional) Transfer amount ciphertext validity proof record
  ///                                                  account or context state account.
  /// @param feeSigmaRecordKey (Optional) Fee sigma proof record account or context state account.
  /// @param feeCiphertextValidityRecordKey (Optional) Fee ciphertext validity proof record account or context state account.
  /// @param rangeRecordKey (Optional) Range proof record account or context state account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> confidentialTransferWithFeeKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                                  final PublicKey sourceTokenKey,
                                                                  final PublicKey mintKey,
                                                                  final PublicKey destinationTokenKey,
                                                                  final PublicKey instructionsSysvarKey,
                                                                  final PublicKey equalityRecordKey,
                                                                  final PublicKey transferAmountCiphertextValidityRecordKey,
                                                                  final PublicKey feeSigmaRecordKey,
                                                                  final PublicKey feeCiphertextValidityRecordKey,
                                                                  final PublicKey rangeRecordKey,
                                                                  final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceTokenKey),
      createRead(mintKey),
      createWrite(destinationTokenKey),
      createRead(requireNonNullElse(instructionsSysvarKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(equalityRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(transferAmountCiphertextValidityRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(feeSigmaRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(feeCiphertextValidityRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createRead(requireNonNullElse(rangeRecordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfer tokens confidentially with fee.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity` (transfer amount
  /// ciphertext)
  /// - `VerifyPercentageWithFee`
  /// - `VerifyBatchedGroupedCiphertext2HandlesValidity` (fee ciphertext)
  /// - `VerifyBatchedRangeProofU256`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// The same restrictions for the `Transfer` applies to
  /// `TransferWithFee`. Namely, the instruction fails if the
  /// associated mint is extended as `NonTransferable`.
  ///
  /// @param sourceTokenKey The source SPL Token account.
  /// @param mintKey The corresponding SPL Token mint.
  /// @param destinationTokenKey The destination SPL Token account.
  /// @param instructionsSysvarKey (Optional) Instructions sysvar if at least one of the
  ///                              `zk_elgamal_proof` instructions are included in the same
  ///                              transaction.
  /// @param equalityRecordKey (Optional) Equality proof record account or context state account.
  /// @param transferAmountCiphertextValidityRecordKey (Optional) Transfer amount ciphertext validity proof record
  ///                                                  account or context state account.
  /// @param feeSigmaRecordKey (Optional) Fee sigma proof record account or context state account.
  /// @param feeCiphertextValidityRecordKey (Optional) Fee ciphertext validity proof record account or context state account.
  /// @param rangeRecordKey (Optional) Range proof record account or context state account.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  /// @param newSourceDecryptableAvailableBalance The new source decryptable balance if the transfer succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `TransferWithFee` instruction in the transaction. If the offset
  ///                                       is `0`, then use a context state account for the proof.
  /// @param transferAmountCiphertextValidityProofInstructionOffset Relative location of the
  ///                                                               `ProofInstruction::VerifyBatchedGroupedCiphertext3HandlesValidity`
  ///                                                               instruction to the `TransferWithFee` instruction in the transaction.
  ///                                                               If the offset is `0`, then use a context state account for the
  ///                                                               proof.
  /// @param feeSigmaProofInstructionOffset Relative location of the `ProofInstruction::VerifyPercentageWithFee`
  ///                                       instruction to the `TransferWithFee` instruction in the transaction.
  ///                                       If the offset is `0`, then use a context state account for the
  ///                                       proof.
  /// @param feeCiphertextValidityProofInstructionOffset Relative location of the
  ///                                                    `ProofInstruction::VerifyBatchedGroupedCiphertext2HandlesValidity`
  ///                                                    instruction to the `TransferWithFee` instruction in the transaction.
  ///                                                    If the offset is `0`, then use a context state account for the
  ///                                                    proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU256Data`
  ///                                    instruction to the `TransferWithFee` instruction in the transaction.
  ///                                    If the offset is `0`, then use a context state account for the
  ///                                    proof.
  public static Instruction confidentialTransferWithFee(final AccountMeta invokedToken2022ProgramMeta,
                                                        final PublicKey sourceTokenKey,
                                                        final PublicKey mintKey,
                                                        final PublicKey destinationTokenKey,
                                                        final PublicKey instructionsSysvarKey,
                                                        final PublicKey equalityRecordKey,
                                                        final PublicKey transferAmountCiphertextValidityRecordKey,
                                                        final PublicKey feeSigmaRecordKey,
                                                        final PublicKey feeCiphertextValidityRecordKey,
                                                        final PublicKey rangeRecordKey,
                                                        final PublicKey authorityKey,
                                                        final DecryptableBalance newSourceDecryptableAvailableBalance,
                                                        final int equalityProofInstructionOffset,
                                                        final int transferAmountCiphertextValidityProofInstructionOffset,
                                                        final int feeSigmaProofInstructionOffset,
                                                        final int feeCiphertextValidityProofInstructionOffset,
                                                        final int rangeProofInstructionOffset) {
    final var keys = confidentialTransferWithFeeKeys(
      invokedToken2022ProgramMeta,
      sourceTokenKey,
      mintKey,
      destinationTokenKey,
      instructionsSysvarKey,
      equalityRecordKey,
      transferAmountCiphertextValidityRecordKey,
      feeSigmaRecordKey,
      feeCiphertextValidityRecordKey,
      rangeRecordKey,
      authorityKey
    );
    return confidentialTransferWithFee(
      invokedToken2022ProgramMeta,
      keys,
      newSourceDecryptableAvailableBalance,
      equalityProofInstructionOffset,
      transferAmountCiphertextValidityProofInstructionOffset,
      feeSigmaProofInstructionOffset,
      feeCiphertextValidityProofInstructionOffset,
      rangeProofInstructionOffset
    );
  }

  /// Transfer tokens confidentially with fee.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity` (transfer amount
  /// ciphertext)
  /// - `VerifyPercentageWithFee`
  /// - `VerifyBatchedGroupedCiphertext2HandlesValidity` (fee ciphertext)
  /// - `VerifyBatchedRangeProofU256`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// The same restrictions for the `Transfer` applies to
  /// `TransferWithFee`. Namely, the instruction fails if the
  /// associated mint is extended as `NonTransferable`.
  ///
  /// @param newSourceDecryptableAvailableBalance The new source decryptable balance if the transfer succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `TransferWithFee` instruction in the transaction. If the offset
  ///                                       is `0`, then use a context state account for the proof.
  /// @param transferAmountCiphertextValidityProofInstructionOffset Relative location of the
  ///                                                               `ProofInstruction::VerifyBatchedGroupedCiphertext3HandlesValidity`
  ///                                                               instruction to the `TransferWithFee` instruction in the transaction.
  ///                                                               If the offset is `0`, then use a context state account for the
  ///                                                               proof.
  /// @param feeSigmaProofInstructionOffset Relative location of the `ProofInstruction::VerifyPercentageWithFee`
  ///                                       instruction to the `TransferWithFee` instruction in the transaction.
  ///                                       If the offset is `0`, then use a context state account for the
  ///                                       proof.
  /// @param feeCiphertextValidityProofInstructionOffset Relative location of the
  ///                                                    `ProofInstruction::VerifyBatchedGroupedCiphertext2HandlesValidity`
  ///                                                    instruction to the `TransferWithFee` instruction in the transaction.
  ///                                                    If the offset is `0`, then use a context state account for the
  ///                                                    proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU256Data`
  ///                                    instruction to the `TransferWithFee` instruction in the transaction.
  ///                                    If the offset is `0`, then use a context state account for the
  ///                                    proof.
  public static Instruction confidentialTransferWithFee(final AccountMeta invokedToken2022ProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final DecryptableBalance newSourceDecryptableAvailableBalance,
                                                        final int equalityProofInstructionOffset,
                                                        final int transferAmountCiphertextValidityProofInstructionOffset,
                                                        final int feeSigmaProofInstructionOffset,
                                                        final int feeCiphertextValidityProofInstructionOffset,
                                                        final int rangeProofInstructionOffset) {
    final byte[] _data = new byte[7 + newSourceDecryptableAvailableBalance.l()];
    int i = CONFIDENTIAL_TRANSFER_WITH_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 13;
    ++i;
    i += newSourceDecryptableAvailableBalance.write(_data, i);
    _data[i] = (byte) equalityProofInstructionOffset;
    ++i;
    _data[i] = (byte) transferAmountCiphertextValidityProofInstructionOffset;
    ++i;
    _data[i] = (byte) feeSigmaProofInstructionOffset;
    ++i;
    _data[i] = (byte) feeCiphertextValidityProofInstructionOffset;
    ++i;
    _data[i] = (byte) rangeProofInstructionOffset;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer tokens confidentially with fee.
  /// 
  /// In order for this instruction to be successfully processed, it must be
  /// accompanied by the following list of `zk_elgamal_proof` program
  /// instructions:
  /// - `VerifyCiphertextCommitmentEquality`
  /// - `VerifyBatchedGroupedCiphertext3HandlesValidity` (transfer amount
  /// ciphertext)
  /// - `VerifyPercentageWithFee`
  /// - `VerifyBatchedGroupedCiphertext2HandlesValidity` (fee ciphertext)
  /// - `VerifyBatchedRangeProofU256`
  /// These instructions can be accompanied in the same transaction or can be
  /// pre-verified into a context state account, in which case, only their
  /// context state account addresses need to be provided.
  /// 
  /// The same restrictions for the `Transfer` applies to
  /// `TransferWithFee`. Namely, the instruction fails if the
  /// associated mint is extended as `NonTransferable`.
  ///
  /// @param newSourceDecryptableAvailableBalance The new source decryptable balance if the transfer succeeds.
  /// @param equalityProofInstructionOffset Relative location of the
  ///                                       `ProofInstruction::VerifyCiphertextCommitmentEquality` instruction
  ///                                       to the `TransferWithFee` instruction in the transaction. If the offset
  ///                                       is `0`, then use a context state account for the proof.
  /// @param transferAmountCiphertextValidityProofInstructionOffset Relative location of the
  ///                                                               `ProofInstruction::VerifyBatchedGroupedCiphertext3HandlesValidity`
  ///                                                               instruction to the `TransferWithFee` instruction in the transaction.
  ///                                                               If the offset is `0`, then use a context state account for the
  ///                                                               proof.
  /// @param feeSigmaProofInstructionOffset Relative location of the `ProofInstruction::VerifyPercentageWithFee`
  ///                                       instruction to the `TransferWithFee` instruction in the transaction.
  ///                                       If the offset is `0`, then use a context state account for the
  ///                                       proof.
  /// @param feeCiphertextValidityProofInstructionOffset Relative location of the
  ///                                                    `ProofInstruction::VerifyBatchedGroupedCiphertext2HandlesValidity`
  ///                                                    instruction to the `TransferWithFee` instruction in the transaction.
  ///                                                    If the offset is `0`, then use a context state account for the
  ///                                                    proof.
  /// @param rangeProofInstructionOffset Relative location of the `ProofInstruction::BatchedRangeProofU256Data`
  ///                                    instruction to the `TransferWithFee` instruction in the transaction.
  ///                                    If the offset is `0`, then use a context state account for the
  ///                                    proof.
  public record ConfidentialTransferWithFeeIxData(int discriminator,
                                                  int confidentialTransferDiscriminator,
                                                  DecryptableBalance newSourceDecryptableAvailableBalance,
                                                  int equalityProofInstructionOffset,
                                                  int transferAmountCiphertextValidityProofInstructionOffset,
                                                  int feeSigmaProofInstructionOffset,
                                                  int feeCiphertextValidityProofInstructionOffset,
                                                  int rangeProofInstructionOffset) implements SerDe {  

    public static ConfidentialTransferWithFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 43;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_DISCRIMINATOR_OFFSET = 1;
    public static final int NEW_SOURCE_DECRYPTABLE_AVAILABLE_BALANCE_OFFSET = 2;
    public static final int EQUALITY_PROOF_INSTRUCTION_OFFSET_OFFSET = 38;
    public static final int TRANSFER_AMOUNT_CIPHERTEXT_VALIDITY_PROOF_INSTRUCTION_OFFSET_OFFSET = 39;
    public static final int FEE_SIGMA_PROOF_INSTRUCTION_OFFSET_OFFSET = 40;
    public static final int FEE_CIPHERTEXT_VALIDITY_PROOF_INSTRUCTION_OFFSET_OFFSET = 41;
    public static final int RANGE_PROOF_INSTRUCTION_OFFSET_OFFSET = 42;

    public static ConfidentialTransferWithFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferDiscriminator = _data[i] & 0xFF;
      ++i;
      final var newSourceDecryptableAvailableBalance = DecryptableBalance.read(_data, i);
      i += newSourceDecryptableAvailableBalance.l();
      final var equalityProofInstructionOffset = _data[i];
      ++i;
      final var transferAmountCiphertextValidityProofInstructionOffset = _data[i];
      ++i;
      final var feeSigmaProofInstructionOffset = _data[i];
      ++i;
      final var feeCiphertextValidityProofInstructionOffset = _data[i];
      ++i;
      final var rangeProofInstructionOffset = _data[i];
      return new ConfidentialTransferWithFeeIxData(discriminator,
                                                   confidentialTransferDiscriminator,
                                                   newSourceDecryptableAvailableBalance,
                                                   equalityProofInstructionOffset,
                                                   transferAmountCiphertextValidityProofInstructionOffset,
                                                   feeSigmaProofInstructionOffset,
                                                   feeCiphertextValidityProofInstructionOffset,
                                                   rangeProofInstructionOffset);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferDiscriminator;
      ++i;
      i += newSourceDecryptableAvailableBalance.write(_data, i);
      _data[i] = (byte) equalityProofInstructionOffset;
      ++i;
      _data[i] = (byte) transferAmountCiphertextValidityProofInstructionOffset;
      ++i;
      _data[i] = (byte) feeSigmaProofInstructionOffset;
      ++i;
      _data[i] = (byte) feeCiphertextValidityProofInstructionOffset;
      ++i;
      _data[i] = (byte) rangeProofInstructionOffset;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_DEFAULT_ACCOUNT_STATE_DISCRIMINATOR = toDiscriminator(28);

  /// Initialize a new mint with the default state for new Accounts.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint.
  public static List<AccountMeta> initializeDefaultAccountStateKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with the default state for new Accounts.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint.
  /// @param state The state each new token account should start with.
  public static Instruction initializeDefaultAccountState(final AccountMeta invokedToken2022ProgramMeta,
                                                          final PublicKey mintKey,
                                                          final AccountState state) {
    final var keys = initializeDefaultAccountStateKeys(
      mintKey
    );
    return initializeDefaultAccountState(
      invokedToken2022ProgramMeta,
      keys,
      state
    );
  }

  /// Initialize a new mint with the default state for new Accounts.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param state The state each new token account should start with.
  public static Instruction initializeDefaultAccountState(final AccountMeta invokedToken2022ProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final AccountState state) {
    final byte[] _data = new byte[2 + state.l()];
    int i = INITIALIZE_DEFAULT_ACCOUNT_STATE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    state.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with the default state for new Accounts.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param state The state each new token account should start with.
  public record InitializeDefaultAccountStateIxData(int discriminator,
                                                    int defaultAccountStateDiscriminator,
                                                    AccountState state) implements SerDe {  

    public static InitializeDefaultAccountStateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 3;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int DEFAULT_ACCOUNT_STATE_DISCRIMINATOR_OFFSET = 1;
    public static final int STATE_OFFSET = 2;

    public static InitializeDefaultAccountStateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var defaultAccountStateDiscriminator = _data[i] & 0xFF;
      ++i;
      final var state = AccountState.read(_data, i);
      return new InitializeDefaultAccountStateIxData(discriminator, defaultAccountStateDiscriminator, state);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) defaultAccountStateDiscriminator;
      ++i;
      i += state.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_DEFAULT_ACCOUNT_STATE_DISCRIMINATOR = toDiscriminator(28);

  /// Update the default state for new Accounts. Only supported for mints that
  /// include the `DefaultAccountState` extension.
  ///
  /// @param mintKey The mint.
  /// @param freezeAuthorityKey The mint freeze authority or its multisignature account.
  public static List<AccountMeta> updateDefaultAccountStateKeys(final PublicKey mintKey,
                                                                final PublicKey freezeAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(freezeAuthorityKey)
    );
  }

  /// Update the default state for new Accounts. Only supported for mints that
  /// include the `DefaultAccountState` extension.
  ///
  /// @param mintKey The mint.
  /// @param freezeAuthorityKey The mint freeze authority or its multisignature account.
  /// @param state The state each new token account should start with.
  public static Instruction updateDefaultAccountState(final AccountMeta invokedToken2022ProgramMeta,
                                                      final PublicKey mintKey,
                                                      final PublicKey freezeAuthorityKey,
                                                      final AccountState state) {
    final var keys = updateDefaultAccountStateKeys(
      mintKey,
      freezeAuthorityKey
    );
    return updateDefaultAccountState(
      invokedToken2022ProgramMeta,
      keys,
      state
    );
  }

  /// Update the default state for new Accounts. Only supported for mints that
  /// include the `DefaultAccountState` extension.
  ///
  /// @param state The state each new token account should start with.
  public static Instruction updateDefaultAccountState(final AccountMeta invokedToken2022ProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final AccountState state) {
    final byte[] _data = new byte[2 + state.l()];
    int i = UPDATE_DEFAULT_ACCOUNT_STATE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    state.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the default state for new Accounts. Only supported for mints that
  /// include the `DefaultAccountState` extension.
  ///
  /// @param state The state each new token account should start with.
  public record UpdateDefaultAccountStateIxData(int discriminator,
                                                int defaultAccountStateDiscriminator,
                                                AccountState state) implements SerDe {  

    public static UpdateDefaultAccountStateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 3;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int DEFAULT_ACCOUNT_STATE_DISCRIMINATOR_OFFSET = 1;
    public static final int STATE_OFFSET = 2;

    public static UpdateDefaultAccountStateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var defaultAccountStateDiscriminator = _data[i] & 0xFF;
      ++i;
      final var state = AccountState.read(_data, i);
      return new UpdateDefaultAccountStateIxData(discriminator, defaultAccountStateDiscriminator, state);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) defaultAccountStateDiscriminator;
      ++i;
      i += state.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REALLOCATE_DISCRIMINATOR = toDiscriminator(29);

  /// Check to see if a token account is large enough for a list of
  /// ExtensionTypes, and if not, use reallocation to increase the data
  /// size.
  ///
  /// @param tokenKey The token account to reallocate.
  /// @param payerKey The payer account to fund reallocation.
  /// @param ownerKey The account's owner or its multisignature account.
  public static List<AccountMeta> reallocateKeys(final SolanaAccounts solanaAccounts,
                                                 final PublicKey tokenKey,
                                                 final PublicKey payerKey,
                                                 final PublicKey ownerKey) {
    return List.of(
      createWrite(tokenKey),
      createWritableSigner(payerKey),
      createRead(solanaAccounts.systemProgram()),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Check to see if a token account is large enough for a list of
  /// ExtensionTypes, and if not, use reallocation to increase the data
  /// size.
  ///
  /// @param tokenKey The token account to reallocate.
  /// @param payerKey The payer account to fund reallocation.
  /// @param ownerKey The account's owner or its multisignature account.
  /// @param newExtensionTypes New extension types to include in the reallocated account.
  public static Instruction reallocate(final AccountMeta invokedToken2022ProgramMeta,
                                       final SolanaAccounts solanaAccounts,
                                       final PublicKey tokenKey,
                                       final PublicKey payerKey,
                                       final PublicKey ownerKey,
                                       final ExtensionType[] newExtensionTypes) {
    final var keys = reallocateKeys(
      solanaAccounts,
      tokenKey,
      payerKey,
      ownerKey
    );
    return reallocate(invokedToken2022ProgramMeta, keys, newExtensionTypes);
  }

  /// Check to see if a token account is large enough for a list of
  /// ExtensionTypes, and if not, use reallocation to increase the data
  /// size.
  ///
  /// @param newExtensionTypes New extension types to include in the reallocated account.
  public static Instruction reallocate(final AccountMeta invokedToken2022ProgramMeta,
                                       final List<AccountMeta> keys,
                                       final ExtensionType[] newExtensionTypes) {
    final byte[] _data = new byte[1 + SerDeUtil.lenArray(newExtensionTypes)];
    int i = REALLOCATE_DISCRIMINATOR.write(_data, 0);
    SerDeUtil.writeArray(newExtensionTypes, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Check to see if a token account is large enough for a list of
  /// ExtensionTypes, and if not, use reallocation to increase the data
  /// size.
  ///
  /// @param newExtensionTypes New extension types to include in the reallocated account.
  public record ReallocateIxData(int discriminator, ExtensionType[] newExtensionTypes) implements SerDe {  

    public static ReallocateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int NEW_EXTENSION_TYPES_OFFSET = 1;

    public static ReallocateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final int _newExtensionTypesCount = (_data.length - i) / 2;
      final var newExtensionTypes = new ExtensionType[_newExtensionTypesCount];
      for (int _i = 0; i < _data.length; ++_i) {
        final var _newExtensionTypesItem = ExtensionType.read(_data, i);
        i += _newExtensionTypesItem.l();
        newExtensionTypes[_i] = _newExtensionTypesItem;
      }
      return new ReallocateIxData(discriminator, newExtensionTypes);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      i += SerDeUtil.writeArray(newExtensionTypes, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + SerDeUtil.lenArray(newExtensionTypes);
    }
  }

  public static final Discriminator ENABLE_MEMO_TRANSFERS_DISCRIMINATOR = toDiscriminator(30);

  /// Require memos for transfers into this Account. Adds the MemoTransfer
  /// extension to the Account, if it doesn't already exist.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner or its multisignature account.
  public static List<AccountMeta> enableMemoTransfersKeys(final PublicKey tokenKey,
                                                          final PublicKey ownerKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Require memos for transfers into this Account. Adds the MemoTransfer
  /// extension to the Account, if it doesn't already exist.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner or its multisignature account.
  public static Instruction enableMemoTransfers(final AccountMeta invokedToken2022ProgramMeta,
                                                final PublicKey tokenKey,
                                                final PublicKey ownerKey) {
    final var keys = enableMemoTransfersKeys(
      tokenKey,
      ownerKey
    );
    return enableMemoTransfers(invokedToken2022ProgramMeta, keys);
  }

  /// Require memos for transfers into this Account. Adds the MemoTransfer
  /// extension to the Account, if it doesn't already exist.
  ///
  public static Instruction enableMemoTransfers(final AccountMeta invokedToken2022ProgramMeta,
                                                final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = ENABLE_MEMO_TRANSFERS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Require memos for transfers into this Account. Adds the MemoTransfer
  /// extension to the Account, if it doesn't already exist.
  ///
  public record EnableMemoTransfersIxData(int discriminator, int memoTransfersDiscriminator) implements SerDe {  

    public static EnableMemoTransfersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int MEMO_TRANSFERS_DISCRIMINATOR_OFFSET = 1;

    public static EnableMemoTransfersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var memoTransfersDiscriminator = _data[i] & 0xFF;
      return new EnableMemoTransfersIxData(discriminator, memoTransfersDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) memoTransfersDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DISABLE_MEMO_TRANSFERS_DISCRIMINATOR = toDiscriminator(30);

  /// Stop requiring memos for transfers into this Account.
  /// 
  /// Implicitly initializes the extension in the case where it is not
  /// present.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner or its multisignature account.
  public static List<AccountMeta> disableMemoTransfersKeys(final PublicKey tokenKey,
                                                           final PublicKey ownerKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Stop requiring memos for transfers into this Account.
  /// 
  /// Implicitly initializes the extension in the case where it is not
  /// present.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner or its multisignature account.
  public static Instruction disableMemoTransfers(final AccountMeta invokedToken2022ProgramMeta,
                                                 final PublicKey tokenKey,
                                                 final PublicKey ownerKey) {
    final var keys = disableMemoTransfersKeys(
      tokenKey,
      ownerKey
    );
    return disableMemoTransfers(invokedToken2022ProgramMeta, keys);
  }

  /// Stop requiring memos for transfers into this Account.
  /// 
  /// Implicitly initializes the extension in the case where it is not
  /// present.
  ///
  public static Instruction disableMemoTransfers(final AccountMeta invokedToken2022ProgramMeta,
                                                 final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = DISABLE_MEMO_TRANSFERS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Stop requiring memos for transfers into this Account.
  /// 
  /// Implicitly initializes the extension in the case where it is not
  /// present.
  ///
  public record DisableMemoTransfersIxData(int discriminator, int memoTransfersDiscriminator) implements SerDe {  

    public static DisableMemoTransfersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int MEMO_TRANSFERS_DISCRIMINATOR_OFFSET = 1;

    public static DisableMemoTransfersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var memoTransfersDiscriminator = _data[i] & 0xFF;
      return new DisableMemoTransfersIxData(discriminator, memoTransfersDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) memoTransfersDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_NATIVE_MINT_DISCRIMINATOR = toDiscriminator(31);

  /// Creates the native mint.
  /// 
  /// This instruction only needs to be invoked once after deployment and is
  /// permissionless. Wrapped SOL (`native_mint::id()`) will not be
  /// available until this instruction is successfully executed.
  ///
  /// @param payerKey Funding account (must be a system account)
  /// @param nativeMintKey The native mint address
  public static List<AccountMeta> createNativeMintKeys(final SolanaAccounts solanaAccounts,
                                                       final PublicKey payerKey,
                                                       final PublicKey nativeMintKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(nativeMintKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  /// Creates the native mint.
  /// 
  /// This instruction only needs to be invoked once after deployment and is
  /// permissionless. Wrapped SOL (`native_mint::id()`) will not be
  /// available until this instruction is successfully executed.
  ///
  /// @param payerKey Funding account (must be a system account)
  /// @param nativeMintKey The native mint address
  public static Instruction createNativeMint(final AccountMeta invokedToken2022ProgramMeta,
                                             final SolanaAccounts solanaAccounts,
                                             final PublicKey payerKey,
                                             final PublicKey nativeMintKey) {
    final var keys = createNativeMintKeys(
      solanaAccounts,
      payerKey,
      nativeMintKey
    );
    return createNativeMint(invokedToken2022ProgramMeta, keys);
  }

  /// Creates the native mint.
  /// 
  /// This instruction only needs to be invoked once after deployment and is
  /// permissionless. Wrapped SOL (`native_mint::id()`) will not be
  /// available until this instruction is successfully executed.
  ///
  public static Instruction createNativeMint(final AccountMeta invokedToken2022ProgramMeta,
                                             final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CREATE_NATIVE_MINT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Creates the native mint.
  /// 
  /// This instruction only needs to be invoked once after deployment and is
  /// permissionless. Wrapped SOL (`native_mint::id()`) will not be
  /// available until this instruction is successfully executed.
  ///
  public record CreateNativeMintIxData(int discriminator) implements SerDe {  

    public static CreateNativeMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static CreateNativeMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new CreateNativeMintIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_NON_TRANSFERABLE_MINT_DISCRIMINATOR = toDiscriminator(32);

  /// Initialize the non transferable extension for the given mint account
  /// 
  /// Fails if the account has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param mintKey The mint account to initialize.
  public static List<AccountMeta> initializeNonTransferableMintKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize the non transferable extension for the given mint account
  /// 
  /// Fails if the account has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param mintKey The mint account to initialize.
  public static Instruction initializeNonTransferableMint(final AccountMeta invokedToken2022ProgramMeta,
                                                          final PublicKey mintKey) {
    final var keys = initializeNonTransferableMintKeys(
      mintKey
    );
    return initializeNonTransferableMint(invokedToken2022ProgramMeta, keys);
  }

  /// Initialize the non transferable extension for the given mint account
  /// 
  /// Fails if the account has already been initialized, so must be called before `InitializeMint`.
  ///
  public static Instruction initializeNonTransferableMint(final AccountMeta invokedToken2022ProgramMeta,
                                                          final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    INITIALIZE_NON_TRANSFERABLE_MINT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize the non transferable extension for the given mint account
  /// 
  /// Fails if the account has already been initialized, so must be called before `InitializeMint`.
  ///
  public record InitializeNonTransferableMintIxData(int discriminator) implements SerDe {  

    public static InitializeNonTransferableMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static InitializeNonTransferableMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new InitializeNonTransferableMintIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_INTEREST_BEARING_MINT_DISCRIMINATOR = toDiscriminator(33);

  /// Initialize a new mint with the `InterestBearing` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeInterestBearingMintKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with the `InterestBearing` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param rateAuthority The public key for the account that can update the rate
  /// @param rate The initial interest rate
  public static Instruction initializeInterestBearingMint(final AccountMeta invokedToken2022ProgramMeta,
                                                          final PublicKey mintKey,
                                                          final PublicKey rateAuthority,
                                                          final int rate) {
    final var keys = initializeInterestBearingMintKeys(
      mintKey
    );
    return initializeInterestBearingMint(
      invokedToken2022ProgramMeta,
      keys,
      rateAuthority,
      rate
    );
  }

  /// Initialize a new mint with the `InterestBearing` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param rateAuthority The public key for the account that can update the rate
  /// @param rate The initial interest rate
  public static Instruction initializeInterestBearingMint(final AccountMeta invokedToken2022ProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final PublicKey rateAuthority,
                                                          final int rate) {
    final byte[] _data = new byte[36];
    int i = INITIALIZE_INTEREST_BEARING_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (rateAuthority != null) {
      rateAuthority.write(_data, i);
    }
    i += 32;
    putInt16LE(_data, i, rate);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with the `InterestBearing` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param rateAuthority The public key for the account that can update the rate
  /// @param rate The initial interest rate
  public record InitializeInterestBearingMintIxData(int discriminator,
                                                    int interestBearingMintDiscriminator,
                                                    PublicKey rateAuthority,
                                                    int rate) implements SerDe {  

    public static InitializeInterestBearingMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 36;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int INTEREST_BEARING_MINT_DISCRIMINATOR_OFFSET = 1;
    public static final int RATE_AUTHORITY_OFFSET = 2;
    public static final int RATE_OFFSET = 34;

    public static InitializeInterestBearingMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var interestBearingMintDiscriminator = _data[i] & 0xFF;
      ++i;
      final var rateAuthority = readPubKey(_data, i);
      i += 32;
      final var rate = getInt16LE(_data, i);
      return new InitializeInterestBearingMintIxData(discriminator,
                                                     interestBearingMintDiscriminator,
                                                     rateAuthority,
                                                     rate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) interestBearingMintDiscriminator;
      ++i;
      if (rateAuthority != null) {
        rateAuthority.write(_data, i);
      }
      i += 32;
      putInt16LE(_data, i, rate);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_RATE_INTEREST_BEARING_MINT_DISCRIMINATOR = toDiscriminator(33);

  /// Update the interest rate. Only supported for mints that include the
  /// `InterestBearingConfig` extension.
  ///
  /// @param mintKey The mint.
  /// @param rateAuthorityKey The mint rate authority.
  public static List<AccountMeta> updateRateInterestBearingMintKeys(final PublicKey mintKey,
                                                                    final PublicKey rateAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createWritableSigner(rateAuthorityKey)
    );
  }

  /// Update the interest rate. Only supported for mints that include the
  /// `InterestBearingConfig` extension.
  ///
  /// @param mintKey The mint.
  /// @param rateAuthorityKey The mint rate authority.
  /// @param rate The interest rate to update.
  public static Instruction updateRateInterestBearingMint(final AccountMeta invokedToken2022ProgramMeta,
                                                          final PublicKey mintKey,
                                                          final PublicKey rateAuthorityKey,
                                                          final int rate) {
    final var keys = updateRateInterestBearingMintKeys(
      mintKey,
      rateAuthorityKey
    );
    return updateRateInterestBearingMint(
      invokedToken2022ProgramMeta,
      keys,
      rate
    );
  }

  /// Update the interest rate. Only supported for mints that include the
  /// `InterestBearingConfig` extension.
  ///
  /// @param rate The interest rate to update.
  public static Instruction updateRateInterestBearingMint(final AccountMeta invokedToken2022ProgramMeta,
                                                          final List<AccountMeta> keys,
                                                          final int rate) {
    final byte[] _data = new byte[4];
    int i = UPDATE_RATE_INTEREST_BEARING_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    putInt16LE(_data, i, rate);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the interest rate. Only supported for mints that include the
  /// `InterestBearingConfig` extension.
  ///
  /// @param rate The interest rate to update.
  public record UpdateRateInterestBearingMintIxData(int discriminator,
                                                    int interestBearingMintDiscriminator,
                                                    int rate) implements SerDe {  

    public static UpdateRateInterestBearingMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 4;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int INTEREST_BEARING_MINT_DISCRIMINATOR_OFFSET = 1;
    public static final int RATE_OFFSET = 2;

    public static UpdateRateInterestBearingMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var interestBearingMintDiscriminator = _data[i] & 0xFF;
      ++i;
      final var rate = getInt16LE(_data, i);
      return new UpdateRateInterestBearingMintIxData(discriminator, interestBearingMintDiscriminator, rate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) interestBearingMintDiscriminator;
      ++i;
      putInt16LE(_data, i, rate);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ENABLE_CPI_GUARD_DISCRIMINATOR = toDiscriminator(34);

  /// Lock certain token operations from taking place within CPI for this Account, namely:
  /// * Transfer and Burn must go through a delegate.
  /// * CloseAccount can only return lamports to owner.
  /// * SetAuthority can only be used to remove an existing close authority.
  /// * Approve is disallowed entirely.
  /// 
  /// In addition, CPI Guard cannot be enabled or disabled via CPI.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> enableCpiGuardKeys(final PublicKey tokenKey,
                                                     final PublicKey ownerKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Lock certain token operations from taking place within CPI for this Account, namely:
  /// * Transfer and Burn must go through a delegate.
  /// * CloseAccount can only return lamports to owner.
  /// * SetAuthority can only be used to remove an existing close authority.
  /// * Approve is disallowed entirely.
  /// 
  /// In addition, CPI Guard cannot be enabled or disabled via CPI.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner/delegate or its multisignature account.
  public static Instruction enableCpiGuard(final AccountMeta invokedToken2022ProgramMeta,
                                           final PublicKey tokenKey,
                                           final PublicKey ownerKey) {
    final var keys = enableCpiGuardKeys(
      tokenKey,
      ownerKey
    );
    return enableCpiGuard(invokedToken2022ProgramMeta, keys);
  }

  /// Lock certain token operations from taking place within CPI for this Account, namely:
  /// * Transfer and Burn must go through a delegate.
  /// * CloseAccount can only return lamports to owner.
  /// * SetAuthority can only be used to remove an existing close authority.
  /// * Approve is disallowed entirely.
  /// 
  /// In addition, CPI Guard cannot be enabled or disabled via CPI.
  ///
  public static Instruction enableCpiGuard(final AccountMeta invokedToken2022ProgramMeta,
                                           final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = ENABLE_CPI_GUARD_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Lock certain token operations from taking place within CPI for this Account, namely:
  /// * Transfer and Burn must go through a delegate.
  /// * CloseAccount can only return lamports to owner.
  /// * SetAuthority can only be used to remove an existing close authority.
  /// * Approve is disallowed entirely.
  /// 
  /// In addition, CPI Guard cannot be enabled or disabled via CPI.
  ///
  public record EnableCpiGuardIxData(int discriminator, int cpiGuardDiscriminator) implements SerDe {  

    public static EnableCpiGuardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CPI_GUARD_DISCRIMINATOR_OFFSET = 1;

    public static EnableCpiGuardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var cpiGuardDiscriminator = _data[i] & 0xFF;
      return new EnableCpiGuardIxData(discriminator, cpiGuardDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) cpiGuardDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DISABLE_CPI_GUARD_DISCRIMINATOR = toDiscriminator(34);

  /// Allow all token operations to happen via CPI as normal.
  /// 
  /// Implicitly initializes the extension in the case where it is not present.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> disableCpiGuardKeys(final PublicKey tokenKey,
                                                      final PublicKey ownerKey) {
    return List.of(
      createWrite(tokenKey),
      createReadOnlySigner(ownerKey)
    );
  }

  /// Allow all token operations to happen via CPI as normal.
  /// 
  /// Implicitly initializes the extension in the case where it is not present.
  ///
  /// @param tokenKey The token account to update.
  /// @param ownerKey The account's owner/delegate or its multisignature account.
  public static Instruction disableCpiGuard(final AccountMeta invokedToken2022ProgramMeta,
                                            final PublicKey tokenKey,
                                            final PublicKey ownerKey) {
    final var keys = disableCpiGuardKeys(
      tokenKey,
      ownerKey
    );
    return disableCpiGuard(invokedToken2022ProgramMeta, keys);
  }

  /// Allow all token operations to happen via CPI as normal.
  /// 
  /// Implicitly initializes the extension in the case where it is not present.
  ///
  public static Instruction disableCpiGuard(final AccountMeta invokedToken2022ProgramMeta,
                                            final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = DISABLE_CPI_GUARD_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Allow all token operations to happen via CPI as normal.
  /// 
  /// Implicitly initializes the extension in the case where it is not present.
  ///
  public record DisableCpiGuardIxData(int discriminator, int cpiGuardDiscriminator) implements SerDe {  

    public static DisableCpiGuardIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CPI_GUARD_DISCRIMINATOR_OFFSET = 1;

    public static DisableCpiGuardIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var cpiGuardDiscriminator = _data[i] & 0xFF;
      return new DisableCpiGuardIxData(discriminator, cpiGuardDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) cpiGuardDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PERMANENT_DELEGATE_DISCRIMINATOR = toDiscriminator(35);

  /// Initialize the permanent delegate on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializePermanentDelegateKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize the permanent delegate on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param delegate Authority that may sign for `Transfer`s and `Burn`s on any account
  public static Instruction initializePermanentDelegate(final AccountMeta invokedToken2022ProgramMeta,
                                                        final PublicKey mintKey,
                                                        final PublicKey delegate) {
    final var keys = initializePermanentDelegateKeys(
      mintKey
    );
    return initializePermanentDelegate(invokedToken2022ProgramMeta, keys, delegate);
  }

  /// Initialize the permanent delegate on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param delegate Authority that may sign for `Transfer`s and `Burn`s on any account
  public static Instruction initializePermanentDelegate(final AccountMeta invokedToken2022ProgramMeta,
                                                        final List<AccountMeta> keys,
                                                        final PublicKey delegate) {
    final byte[] _data = new byte[33];
    int i = INITIALIZE_PERMANENT_DELEGATE_DISCRIMINATOR.write(_data, 0);
    delegate.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize the permanent delegate on a new mint.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param delegate Authority that may sign for `Transfer`s and `Burn`s on any account
  public record InitializePermanentDelegateIxData(int discriminator, PublicKey delegate) implements SerDe {  

    public static InitializePermanentDelegateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 33;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int DELEGATE_OFFSET = 1;

    public static InitializePermanentDelegateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var delegate = readPubKey(_data, i);
      return new InitializePermanentDelegateIxData(discriminator, delegate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      delegate.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_TRANSFER_HOOK_DISCRIMINATOR = toDiscriminator(36);

  /// Initialize a new mint with a transfer hook program.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeTransferHookKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with a transfer hook program.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param authority The public key for the account that can update the program id
  /// @param programId The program id that performs logic during transfers
  public static Instruction initializeTransferHook(final AccountMeta invokedToken2022ProgramMeta,
                                                   final PublicKey mintKey,
                                                   final PublicKey authority,
                                                   final PublicKey programId) {
    final var keys = initializeTransferHookKeys(
      mintKey
    );
    return initializeTransferHook(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      programId
    );
  }

  /// Initialize a new mint with a transfer hook program.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the program id
  /// @param programId The program id that performs logic during transfers
  public static Instruction initializeTransferHook(final AccountMeta invokedToken2022ProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final PublicKey authority,
                                                   final PublicKey programId) {
    final byte[] _data = new byte[66];
    int i = INITIALIZE_TRANSFER_HOOK_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    if (programId != null) {
      programId.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with a transfer hook program.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the program id
  /// @param programId The program id that performs logic during transfers
  public record InitializeTransferHookIxData(int discriminator,
                                             int transferHookDiscriminator,
                                             PublicKey authority,
                                             PublicKey programId) implements SerDe {  

    public static InitializeTransferHookIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 66;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_HOOK_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int PROGRAM_ID_OFFSET = 34;

    public static InitializeTransferHookIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferHookDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var programId = readPubKey(_data, i);
      return new InitializeTransferHookIxData(discriminator,
                                              transferHookDiscriminator,
                                              authority,
                                              programId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferHookDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      if (programId != null) {
        programId.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_TRANSFER_HOOK_DISCRIMINATOR = toDiscriminator(36);

  /// Update the transfer hook program id. Only supported for mints that
  /// include the `TransferHook` extension.
  /// 
  /// Accounts expected by this instruction:
  /// 
  /// 0. `writable` The mint.
  /// 1. `signer` The transfer hook authority.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The transfer hook authority.
  public static List<AccountMeta> updateTransferHookKeys(final PublicKey mintKey,
                                                         final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Update the transfer hook program id. Only supported for mints that
  /// include the `TransferHook` extension.
  /// 
  /// Accounts expected by this instruction:
  /// 
  /// 0. `writable` The mint.
  /// 1. `signer` The transfer hook authority.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The transfer hook authority.
  /// @param programId The program id that performs logic during transfers
  public static Instruction updateTransferHook(final AccountMeta invokedToken2022ProgramMeta,
                                               final PublicKey mintKey,
                                               final PublicKey authorityKey,
                                               final PublicKey programId) {
    final var keys = updateTransferHookKeys(
      mintKey,
      authorityKey
    );
    return updateTransferHook(
      invokedToken2022ProgramMeta,
      keys,
      programId
    );
  }

  /// Update the transfer hook program id. Only supported for mints that
  /// include the `TransferHook` extension.
  /// 
  /// Accounts expected by this instruction:
  /// 
  /// 0. `writable` The mint.
  /// 1. `signer` The transfer hook authority.
  ///
  /// @param programId The program id that performs logic during transfers
  public static Instruction updateTransferHook(final AccountMeta invokedToken2022ProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey programId) {
    final byte[] _data = new byte[34];
    int i = UPDATE_TRANSFER_HOOK_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    if (programId != null) {
      programId.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the transfer hook program id. Only supported for mints that
  /// include the `TransferHook` extension.
  /// 
  /// Accounts expected by this instruction:
  /// 
  /// 0. `writable` The mint.
  /// 1. `signer` The transfer hook authority.
  ///
  /// @param programId The program id that performs logic during transfers
  public record UpdateTransferHookIxData(int discriminator,
                                         int transferHookDiscriminator,
                                         PublicKey programId) implements SerDe {  

    public static UpdateTransferHookIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int TRANSFER_HOOK_DISCRIMINATOR_OFFSET = 1;
    public static final int PROGRAM_ID_OFFSET = 2;

    public static UpdateTransferHookIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var transferHookDiscriminator = _data[i] & 0xFF;
      ++i;
      final var programId = readPubKey(_data, i);
      return new UpdateTransferHookIxData(discriminator, transferHookDiscriminator, programId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) transferHookDiscriminator;
      ++i;
      if (programId != null) {
        programId.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR = toDiscriminator(37);

  /// Initializes confidential transfer fees for a mint.
  /// 
  /// The instruction must be included within the same Transaction as TokenInstruction::InitializeMint.
  /// Otherwise another party can initialize the configuration.
  /// 
  /// The instruction fails if TokenInstruction::InitializeMint has already executed for the mint.
  ///
  /// @param mintKey The SPL Token mint.
  public static List<AccountMeta> initializeConfidentialTransferFeeKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initializes confidential transfer fees for a mint.
  /// 
  /// The instruction must be included within the same Transaction as TokenInstruction::InitializeMint.
  /// Otherwise another party can initialize the configuration.
  /// 
  /// The instruction fails if TokenInstruction::InitializeMint has already executed for the mint.
  ///
  /// @param mintKey The SPL Token mint.
  /// @param authority Optional authority to set the withdraw withheld authority ElGamal key
  /// @param withdrawWithheldAuthorityElGamalPubkey Withheld fees from accounts must be encrypted with this ElGamal key
  public static Instruction initializeConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                              final PublicKey mintKey,
                                                              final PublicKey authority,
                                                              final PublicKey withdrawWithheldAuthorityElGamalPubkey) {
    final var keys = initializeConfidentialTransferFeeKeys(
      mintKey
    );
    return initializeConfidentialTransferFee(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      withdrawWithheldAuthorityElGamalPubkey
    );
  }

  /// Initializes confidential transfer fees for a mint.
  /// 
  /// The instruction must be included within the same Transaction as TokenInstruction::InitializeMint.
  /// Otherwise another party can initialize the configuration.
  /// 
  /// The instruction fails if TokenInstruction::InitializeMint has already executed for the mint.
  ///
  /// @param authority Optional authority to set the withdraw withheld authority ElGamal key
  /// @param withdrawWithheldAuthorityElGamalPubkey Withheld fees from accounts must be encrypted with this ElGamal key
  public static Instruction initializeConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                              final List<AccountMeta> keys,
                                                              final PublicKey authority,
                                                              final PublicKey withdrawWithheldAuthorityElGamalPubkey) {
    final byte[] _data = new byte[66];
    int i = INITIALIZE_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    if (withdrawWithheldAuthorityElGamalPubkey != null) {
      withdrawWithheldAuthorityElGamalPubkey.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initializes confidential transfer fees for a mint.
  /// 
  /// The instruction must be included within the same Transaction as TokenInstruction::InitializeMint.
  /// Otherwise another party can initialize the configuration.
  /// 
  /// The instruction fails if TokenInstruction::InitializeMint has already executed for the mint.
  ///
  /// @param authority Optional authority to set the withdraw withheld authority ElGamal key
  /// @param withdrawWithheldAuthorityElGamalPubkey Withheld fees from accounts must be encrypted with this ElGamal key
  public record InitializeConfidentialTransferFeeIxData(int discriminator,
                                                        int confidentialTransferFeeDiscriminator,
                                                        PublicKey authority,
                                                        PublicKey withdrawWithheldAuthorityElGamalPubkey) implements SerDe {  

    public static InitializeConfidentialTransferFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 66;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int WITHDRAW_WITHHELD_AUTHORITY_EL_GAMAL_PUBKEY_OFFSET = 34;

    public static InitializeConfidentialTransferFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var withdrawWithheldAuthorityElGamalPubkey = readPubKey(_data, i);
      return new InitializeConfidentialTransferFeeIxData(discriminator,
                                                         confidentialTransferFeeDiscriminator,
                                                         authority,
                                                         withdrawWithheldAuthorityElGamalPubkey);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferFeeDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      if (withdrawWithheldAuthorityElGamalPubkey != null) {
        withdrawWithheldAuthorityElGamalPubkey.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_WITHHELD_TOKENS_FROM_MINT_FOR_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR = toDiscriminator(37);

  /// Transfer all withheld confidential tokens in the mint to an account.
  /// Signed by the mint's withdraw withheld tokens authority.
  /// 
  /// The withheld confidential tokens are aggregated directly into the destination available balance.
  /// 
  /// Must be accompanied by the VerifyCiphertextCiphertextEquality instruction
  /// of the zk_elgamal_proof program in the same transaction or the address of
  /// a context state account for the proof must be provided.
  ///
  /// @param mintKey The token mint.
  /// @param destinationKey The fee receiver account.
  /// @param instructionsSysvarOrContextStateKey Instructions sysvar or context state account
  /// @param recordKey Optional record account if proof is read from record
  /// @param authorityKey The mint's withdraw_withheld_authority
  public static List<AccountMeta> withdrawWithheldTokensFromMintForConfidentialTransferFeeKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                                                               final PublicKey mintKey,
                                                                                               final PublicKey destinationKey,
                                                                                               final PublicKey instructionsSysvarOrContextStateKey,
                                                                                               final PublicKey recordKey,
                                                                                               final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createWrite(destinationKey),
      createRead(instructionsSysvarOrContextStateKey),
      createRead(requireNonNullElse(recordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfer all withheld confidential tokens in the mint to an account.
  /// Signed by the mint's withdraw withheld tokens authority.
  /// 
  /// The withheld confidential tokens are aggregated directly into the destination available balance.
  /// 
  /// Must be accompanied by the VerifyCiphertextCiphertextEquality instruction
  /// of the zk_elgamal_proof program in the same transaction or the address of
  /// a context state account for the proof must be provided.
  ///
  /// @param mintKey The token mint.
  /// @param destinationKey The fee receiver account.
  /// @param instructionsSysvarOrContextStateKey Instructions sysvar or context state account
  /// @param recordKey Optional record account if proof is read from record
  /// @param authorityKey The mint's withdraw_withheld_authority
  /// @param proofInstructionOffset Proof instruction offset
  /// @param newDecryptableAvailableBalance The new decryptable balance in the destination token account
  public static Instruction withdrawWithheldTokensFromMintForConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                                                     final PublicKey mintKey,
                                                                                     final PublicKey destinationKey,
                                                                                     final PublicKey instructionsSysvarOrContextStateKey,
                                                                                     final PublicKey recordKey,
                                                                                     final PublicKey authorityKey,
                                                                                     final int proofInstructionOffset,
                                                                                     final DecryptableBalance newDecryptableAvailableBalance) {
    final var keys = withdrawWithheldTokensFromMintForConfidentialTransferFeeKeys(
      invokedToken2022ProgramMeta,
      mintKey,
      destinationKey,
      instructionsSysvarOrContextStateKey,
      recordKey,
      authorityKey
    );
    return withdrawWithheldTokensFromMintForConfidentialTransferFee(
      invokedToken2022ProgramMeta,
      keys,
      proofInstructionOffset,
      newDecryptableAvailableBalance
    );
  }

  /// Transfer all withheld confidential tokens in the mint to an account.
  /// Signed by the mint's withdraw withheld tokens authority.
  /// 
  /// The withheld confidential tokens are aggregated directly into the destination available balance.
  /// 
  /// Must be accompanied by the VerifyCiphertextCiphertextEquality instruction
  /// of the zk_elgamal_proof program in the same transaction or the address of
  /// a context state account for the proof must be provided.
  ///
  /// @param proofInstructionOffset Proof instruction offset
  /// @param newDecryptableAvailableBalance The new decryptable balance in the destination token account
  public static Instruction withdrawWithheldTokensFromMintForConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                                                     final List<AccountMeta> keys,
                                                                                     final int proofInstructionOffset,
                                                                                     final DecryptableBalance newDecryptableAvailableBalance) {
    final byte[] _data = new byte[3 + newDecryptableAvailableBalance.l()];
    int i = WITHDRAW_WITHHELD_TOKENS_FROM_MINT_FOR_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    _data[i] = (byte) proofInstructionOffset;
    ++i;
    newDecryptableAvailableBalance.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer all withheld confidential tokens in the mint to an account.
  /// Signed by the mint's withdraw withheld tokens authority.
  /// 
  /// The withheld confidential tokens are aggregated directly into the destination available balance.
  /// 
  /// Must be accompanied by the VerifyCiphertextCiphertextEquality instruction
  /// of the zk_elgamal_proof program in the same transaction or the address of
  /// a context state account for the proof must be provided.
  ///
  /// @param proofInstructionOffset Proof instruction offset
  /// @param newDecryptableAvailableBalance The new decryptable balance in the destination token account
  public record WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData(int discriminator,
                                                                               int confidentialTransferFeeDiscriminator,
                                                                               int proofInstructionOffset,
                                                                               DecryptableBalance newDecryptableAvailableBalance) implements SerDe {  

    public static WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 39;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int PROOF_INSTRUCTION_OFFSET_OFFSET = 2;
    public static final int NEW_DECRYPTABLE_AVAILABLE_BALANCE_OFFSET = 3;

    public static WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final var proofInstructionOffset = _data[i];
      ++i;
      final var newDecryptableAvailableBalance = DecryptableBalance.read(_data, i);
      return new WithdrawWithheldTokensFromMintForConfidentialTransferFeeIxData(discriminator,
                                                                                confidentialTransferFeeDiscriminator,
                                                                                proofInstructionOffset,
                                                                                newDecryptableAvailableBalance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferFeeDiscriminator;
      ++i;
      _data[i] = (byte) proofInstructionOffset;
      ++i;
      i += newDecryptableAvailableBalance.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_WITHHELD_TOKENS_FROM_ACCOUNTS_FOR_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR = toDiscriminator(37);

  /// Transfer all withheld tokens to an account. Signed by the mint's withdraw withheld
  /// tokens authority. This instruction is susceptible to front-running.
  /// Use `HarvestWithheldTokensToMint` and `WithdrawWithheldTokensFromMint` as alternative.
  /// 
  /// Must be accompanied by the VerifyWithdrawWithheldTokens instruction.
  ///
  /// @param mintKey The token mint.
  /// @param destinationKey The fee receiver account.
  /// @param instructionsSysvarOrContextStateKey Instructions sysvar or context state account
  /// @param recordKey Optional record account
  /// @param authorityKey The mint's withdraw_withheld_authority
  public static List<AccountMeta> withdrawWithheldTokensFromAccountsForConfidentialTransferFeeKeys(final AccountMeta invokedToken2022ProgramMeta,
                                                                                                   final PublicKey mintKey,
                                                                                                   final PublicKey destinationKey,
                                                                                                   final PublicKey instructionsSysvarOrContextStateKey,
                                                                                                   final PublicKey recordKey,
                                                                                                   final PublicKey authorityKey) {
    return List.of(
      createRead(mintKey),
      createWrite(destinationKey),
      createRead(instructionsSysvarOrContextStateKey),
      createRead(requireNonNullElse(recordKey, invokedToken2022ProgramMeta.publicKey())),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfer all withheld tokens to an account. Signed by the mint's withdraw withheld
  /// tokens authority. This instruction is susceptible to front-running.
  /// Use `HarvestWithheldTokensToMint` and `WithdrawWithheldTokensFromMint` as alternative.
  /// 
  /// Must be accompanied by the VerifyWithdrawWithheldTokens instruction.
  ///
  /// @param mintKey The token mint.
  /// @param destinationKey The fee receiver account.
  /// @param instructionsSysvarOrContextStateKey Instructions sysvar or context state account
  /// @param recordKey Optional record account
  /// @param authorityKey The mint's withdraw_withheld_authority
  /// @param numTokenAccounts Number of token accounts harvested
  /// @param proofInstructionOffset Proof instruction offset
  /// @param newDecryptableAvailableBalance The new decryptable balance in the destination token account
  public static Instruction withdrawWithheldTokensFromAccountsForConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                                                         final PublicKey mintKey,
                                                                                         final PublicKey destinationKey,
                                                                                         final PublicKey instructionsSysvarOrContextStateKey,
                                                                                         final PublicKey recordKey,
                                                                                         final PublicKey authorityKey,
                                                                                         final int numTokenAccounts,
                                                                                         final int proofInstructionOffset,
                                                                                         final DecryptableBalance newDecryptableAvailableBalance) {
    final var keys = withdrawWithheldTokensFromAccountsForConfidentialTransferFeeKeys(
      invokedToken2022ProgramMeta,
      mintKey,
      destinationKey,
      instructionsSysvarOrContextStateKey,
      recordKey,
      authorityKey
    );
    return withdrawWithheldTokensFromAccountsForConfidentialTransferFee(
      invokedToken2022ProgramMeta,
      keys,
      numTokenAccounts,
      proofInstructionOffset,
      newDecryptableAvailableBalance
    );
  }

  /// Transfer all withheld tokens to an account. Signed by the mint's withdraw withheld
  /// tokens authority. This instruction is susceptible to front-running.
  /// Use `HarvestWithheldTokensToMint` and `WithdrawWithheldTokensFromMint` as alternative.
  /// 
  /// Must be accompanied by the VerifyWithdrawWithheldTokens instruction.
  ///
  /// @param numTokenAccounts Number of token accounts harvested
  /// @param proofInstructionOffset Proof instruction offset
  /// @param newDecryptableAvailableBalance The new decryptable balance in the destination token account
  public static Instruction withdrawWithheldTokensFromAccountsForConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                                                         final List<AccountMeta> keys,
                                                                                         final int numTokenAccounts,
                                                                                         final int proofInstructionOffset,
                                                                                         final DecryptableBalance newDecryptableAvailableBalance) {
    final byte[] _data = new byte[4 + newDecryptableAvailableBalance.l()];
    int i = WITHDRAW_WITHHELD_TOKENS_FROM_ACCOUNTS_FOR_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 2;
    ++i;
    _data[i] = (byte) numTokenAccounts;
    ++i;
    _data[i] = (byte) proofInstructionOffset;
    ++i;
    newDecryptableAvailableBalance.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer all withheld tokens to an account. Signed by the mint's withdraw withheld
  /// tokens authority. This instruction is susceptible to front-running.
  /// Use `HarvestWithheldTokensToMint` and `WithdrawWithheldTokensFromMint` as alternative.
  /// 
  /// Must be accompanied by the VerifyWithdrawWithheldTokens instruction.
  ///
  /// @param numTokenAccounts Number of token accounts harvested
  /// @param proofInstructionOffset Proof instruction offset
  /// @param newDecryptableAvailableBalance The new decryptable balance in the destination token account
  public record WithdrawWithheldTokensFromAccountsForConfidentialTransferFeeIxData(int discriminator,
                                                                                   int confidentialTransferFeeDiscriminator,
                                                                                   int numTokenAccounts,
                                                                                   int proofInstructionOffset,
                                                                                   DecryptableBalance newDecryptableAvailableBalance) implements SerDe {  

    public static WithdrawWithheldTokensFromAccountsForConfidentialTransferFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;
    public static final int NUM_TOKEN_ACCOUNTS_OFFSET = 2;
    public static final int PROOF_INSTRUCTION_OFFSET_OFFSET = 3;
    public static final int NEW_DECRYPTABLE_AVAILABLE_BALANCE_OFFSET = 4;

    public static WithdrawWithheldTokensFromAccountsForConfidentialTransferFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferFeeDiscriminator = _data[i] & 0xFF;
      ++i;
      final var numTokenAccounts = _data[i] & 0xFF;
      ++i;
      final var proofInstructionOffset = _data[i];
      ++i;
      final var newDecryptableAvailableBalance = DecryptableBalance.read(_data, i);
      return new WithdrawWithheldTokensFromAccountsForConfidentialTransferFeeIxData(discriminator,
                                                                                    confidentialTransferFeeDiscriminator,
                                                                                    numTokenAccounts,
                                                                                    proofInstructionOffset,
                                                                                    newDecryptableAvailableBalance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferFeeDiscriminator;
      ++i;
      _data[i] = (byte) numTokenAccounts;
      ++i;
      _data[i] = (byte) proofInstructionOffset;
      ++i;
      i += newDecryptableAvailableBalance.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator HARVEST_WITHHELD_TOKENS_TO_MINT_FOR_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR = toDiscriminator(37);

  /// Permissionless instruction to transfer all withheld confidential tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include both the `TransferFeeAmount` and
  /// `ConfidentialTransferAccount` extension. If not, the account is skipped.
  ///
  /// @param mintKey The mint.
  public static List<AccountMeta> harvestWithheldTokensToMintForConfidentialTransferFeeKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Permissionless instruction to transfer all withheld confidential tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include both the `TransferFeeAmount` and
  /// `ConfidentialTransferAccount` extension. If not, the account is skipped.
  ///
  /// @param mintKey The mint.
  public static Instruction harvestWithheldTokensToMintForConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                                                  final PublicKey mintKey) {
    final var keys = harvestWithheldTokensToMintForConfidentialTransferFeeKeys(
      mintKey
    );
    return harvestWithheldTokensToMintForConfidentialTransferFee(invokedToken2022ProgramMeta, keys);
  }

  /// Permissionless instruction to transfer all withheld confidential tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include both the `TransferFeeAmount` and
  /// `ConfidentialTransferAccount` extension. If not, the account is skipped.
  ///
  public static Instruction harvestWithheldTokensToMintForConfidentialTransferFee(final AccountMeta invokedToken2022ProgramMeta,
                                                                                  final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = HARVEST_WITHHELD_TOKENS_TO_MINT_FOR_CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 3;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Permissionless instruction to transfer all withheld confidential tokens to the mint.
  /// 
  /// Succeeds for frozen accounts.
  /// 
  /// Accounts provided should include both the `TransferFeeAmount` and
  /// `ConfidentialTransferAccount` extension. If not, the account is skipped.
  ///
  public record HarvestWithheldTokensToMintForConfidentialTransferFeeIxData(int discriminator, int confidentialTransferFeeDiscriminator) implements SerDe {  

    public static HarvestWithheldTokensToMintForConfidentialTransferFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;

    public static HarvestWithheldTokensToMintForConfidentialTransferFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferFeeDiscriminator = _data[i] & 0xFF;
      return new HarvestWithheldTokensToMintForConfidentialTransferFeeIxData(discriminator, confidentialTransferFeeDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferFeeDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ENABLE_HARVEST_TO_MINT_DISCRIMINATOR = toDiscriminator(37);

  /// Configure a confidential transfer fee mint to accept harvested confidential fees.
  ///
  /// @param mintKey The token mint.
  /// @param authorityKey The confidential transfer fee authority
  public static List<AccountMeta> enableHarvestToMintKeys(final PublicKey mintKey,
                                                          final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configure a confidential transfer fee mint to accept harvested confidential fees.
  ///
  /// @param mintKey The token mint.
  /// @param authorityKey The confidential transfer fee authority
  public static Instruction enableHarvestToMint(final AccountMeta invokedToken2022ProgramMeta,
                                                final PublicKey mintKey,
                                                final PublicKey authorityKey) {
    final var keys = enableHarvestToMintKeys(
      mintKey,
      authorityKey
    );
    return enableHarvestToMint(invokedToken2022ProgramMeta, keys);
  }

  /// Configure a confidential transfer fee mint to accept harvested confidential fees.
  ///
  public static Instruction enableHarvestToMint(final AccountMeta invokedToken2022ProgramMeta,
                                                final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = ENABLE_HARVEST_TO_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 4;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configure a confidential transfer fee mint to accept harvested confidential fees.
  ///
  public record EnableHarvestToMintIxData(int discriminator, int confidentialTransferFeeDiscriminator) implements SerDe {  

    public static EnableHarvestToMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;

    public static EnableHarvestToMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferFeeDiscriminator = _data[i] & 0xFF;
      return new EnableHarvestToMintIxData(discriminator, confidentialTransferFeeDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferFeeDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DISABLE_HARVEST_TO_MINT_DISCRIMINATOR = toDiscriminator(37);

  /// Configure a confidential transfer fee mint to reject any harvested confidential fees.
  ///
  /// @param mintKey The token mint.
  /// @param authorityKey The confidential transfer fee authority
  public static List<AccountMeta> disableHarvestToMintKeys(final PublicKey mintKey,
                                                           final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Configure a confidential transfer fee mint to reject any harvested confidential fees.
  ///
  /// @param mintKey The token mint.
  /// @param authorityKey The confidential transfer fee authority
  public static Instruction disableHarvestToMint(final AccountMeta invokedToken2022ProgramMeta,
                                                 final PublicKey mintKey,
                                                 final PublicKey authorityKey) {
    final var keys = disableHarvestToMintKeys(
      mintKey,
      authorityKey
    );
    return disableHarvestToMint(invokedToken2022ProgramMeta, keys);
  }

  /// Configure a confidential transfer fee mint to reject any harvested confidential fees.
  ///
  public static Instruction disableHarvestToMint(final AccountMeta invokedToken2022ProgramMeta,
                                                 final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = DISABLE_HARVEST_TO_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 5;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Configure a confidential transfer fee mint to reject any harvested confidential fees.
  ///
  public record DisableHarvestToMintIxData(int discriminator, int confidentialTransferFeeDiscriminator) implements SerDe {  

    public static DisableHarvestToMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int CONFIDENTIAL_TRANSFER_FEE_DISCRIMINATOR_OFFSET = 1;

    public static DisableHarvestToMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var confidentialTransferFeeDiscriminator = _data[i] & 0xFF;
      return new DisableHarvestToMintIxData(discriminator, confidentialTransferFeeDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) confidentialTransferFeeDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_EXCESS_LAMPORTS_DISCRIMINATOR = toDiscriminator(38);

  /// This instruction is to be used to rescue SOLs sent to any TokenProgram
  /// owned account by sending them to any other account, leaving behind only
  /// lamports for rent exemption.
  ///
  /// @param sourceAccountKey Account holding excess lamports.
  /// @param destinationAccountKey Destination account for withdrawn lamports.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> withdrawExcessLamportsKeys(final PublicKey sourceAccountKey,
                                                             final PublicKey destinationAccountKey,
                                                             final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceAccountKey),
      createWrite(destinationAccountKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// This instruction is to be used to rescue SOLs sent to any TokenProgram
  /// owned account by sending them to any other account, leaving behind only
  /// lamports for rent exemption.
  ///
  /// @param sourceAccountKey Account holding excess lamports.
  /// @param destinationAccountKey Destination account for withdrawn lamports.
  /// @param authorityKey The source account's owner/delegate or its multisignature account.
  public static Instruction withdrawExcessLamports(final AccountMeta invokedToken2022ProgramMeta,
                                                   final PublicKey sourceAccountKey,
                                                   final PublicKey destinationAccountKey,
                                                   final PublicKey authorityKey) {
    final var keys = withdrawExcessLamportsKeys(
      sourceAccountKey,
      destinationAccountKey,
      authorityKey
    );
    return withdrawExcessLamports(invokedToken2022ProgramMeta, keys);
  }

  /// This instruction is to be used to rescue SOLs sent to any TokenProgram
  /// owned account by sending them to any other account, leaving behind only
  /// lamports for rent exemption.
  ///
  public static Instruction withdrawExcessLamports(final AccountMeta invokedToken2022ProgramMeta,
                                                   final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    WITHDRAW_EXCESS_LAMPORTS_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// This instruction is to be used to rescue SOLs sent to any TokenProgram
  /// owned account by sending them to any other account, leaving behind only
  /// lamports for rent exemption.
  ///
  public record WithdrawExcessLamportsIxData(int discriminator) implements SerDe {  

    public static WithdrawExcessLamportsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static WithdrawExcessLamportsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = _data[_offset] & 0xFF;
      return new WithdrawExcessLamportsIxData(discriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_METADATA_POINTER_DISCRIMINATOR = toDiscriminator(39);

  /// Initialize a new mint with a metadata pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeMetadataPointerKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with a metadata pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param authority The public key for the account that can update the metadata address.
  /// @param metadataAddress The account address that holds the metadata.
  public static Instruction initializeMetadataPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                      final PublicKey mintKey,
                                                      final PublicKey authority,
                                                      final PublicKey metadataAddress) {
    final var keys = initializeMetadataPointerKeys(
      mintKey
    );
    return initializeMetadataPointer(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      metadataAddress
    );
  }

  /// Initialize a new mint with a metadata pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the metadata address.
  /// @param metadataAddress The account address that holds the metadata.
  public static Instruction initializeMetadataPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                      final List<AccountMeta> keys,
                                                      final PublicKey authority,
                                                      final PublicKey metadataAddress) {
    final byte[] _data = new byte[66];
    int i = INITIALIZE_METADATA_POINTER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    if (metadataAddress != null) {
      metadataAddress.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with a metadata pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the metadata address.
  /// @param metadataAddress The account address that holds the metadata.
  public record InitializeMetadataPointerIxData(int discriminator,
                                                int metadataPointerDiscriminator,
                                                PublicKey authority,
                                                PublicKey metadataAddress) implements SerDe {  

    public static InitializeMetadataPointerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 66;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int METADATA_POINTER_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int METADATA_ADDRESS_OFFSET = 34;

    public static InitializeMetadataPointerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var metadataPointerDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var metadataAddress = readPubKey(_data, i);
      return new InitializeMetadataPointerIxData(discriminator,
                                                 metadataPointerDiscriminator,
                                                 authority,
                                                 metadataAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) metadataPointerDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      if (metadataAddress != null) {
        metadataAddress.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_METADATA_POINTER_DISCRIMINATOR = toDiscriminator(39);

  /// Update the metadata pointer address. Only supported for mints that
  /// include the `MetadataPointer` extension.
  ///
  /// @param mintKey The mint to initialize.
  /// @param metadataPointerAuthorityKey The metadata pointer authority or its multisignature account.
  public static List<AccountMeta> updateMetadataPointerKeys(final PublicKey mintKey,
                                                            final PublicKey metadataPointerAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(metadataPointerAuthorityKey)
    );
  }

  /// Update the metadata pointer address. Only supported for mints that
  /// include the `MetadataPointer` extension.
  ///
  /// @param mintKey The mint to initialize.
  /// @param metadataPointerAuthorityKey The metadata pointer authority or its multisignature account.
  /// @param metadataAddress The new account address that holds the metadata.
  public static Instruction updateMetadataPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                  final PublicKey mintKey,
                                                  final PublicKey metadataPointerAuthorityKey,
                                                  final PublicKey metadataAddress) {
    final var keys = updateMetadataPointerKeys(
      mintKey,
      metadataPointerAuthorityKey
    );
    return updateMetadataPointer(
      invokedToken2022ProgramMeta,
      keys,
      metadataAddress
    );
  }

  /// Update the metadata pointer address. Only supported for mints that
  /// include the `MetadataPointer` extension.
  ///
  /// @param metadataAddress The new account address that holds the metadata.
  public static Instruction updateMetadataPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final PublicKey metadataAddress) {
    final byte[] _data = new byte[34];
    int i = UPDATE_METADATA_POINTER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    if (metadataAddress != null) {
      metadataAddress.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the metadata pointer address. Only supported for mints that
  /// include the `MetadataPointer` extension.
  ///
  /// @param metadataAddress The new account address that holds the metadata.
  public record UpdateMetadataPointerIxData(int discriminator,
                                            int metadataPointerDiscriminator,
                                            PublicKey metadataAddress) implements SerDe {  

    public static UpdateMetadataPointerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int METADATA_POINTER_DISCRIMINATOR_OFFSET = 1;
    public static final int METADATA_ADDRESS_OFFSET = 2;

    public static UpdateMetadataPointerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var metadataPointerDiscriminator = _data[i] & 0xFF;
      ++i;
      final var metadataAddress = readPubKey(_data, i);
      return new UpdateMetadataPointerIxData(discriminator, metadataPointerDiscriminator, metadataAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) metadataPointerDiscriminator;
      ++i;
      if (metadataAddress != null) {
        metadataAddress.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_GROUP_POINTER_DISCRIMINATOR = toDiscriminator(40);

  /// Initialize a new mint with a group pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeGroupPointerKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with a group pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param authority The public key for the account that can update the group address.
  /// @param groupAddress The account address that holds the group.
  public static Instruction initializeGroupPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                   final PublicKey mintKey,
                                                   final PublicKey authority,
                                                   final PublicKey groupAddress) {
    final var keys = initializeGroupPointerKeys(
      mintKey
    );
    return initializeGroupPointer(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      groupAddress
    );
  }

  /// Initialize a new mint with a group pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the group address.
  /// @param groupAddress The account address that holds the group.
  public static Instruction initializeGroupPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final PublicKey authority,
                                                   final PublicKey groupAddress) {
    final byte[] _data = new byte[66];
    int i = INITIALIZE_GROUP_POINTER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    if (groupAddress != null) {
      groupAddress.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with a group pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the group address.
  /// @param groupAddress The account address that holds the group.
  public record InitializeGroupPointerIxData(int discriminator,
                                             int groupPointerDiscriminator,
                                             PublicKey authority,
                                             PublicKey groupAddress) implements SerDe {  

    public static InitializeGroupPointerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 66;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int GROUP_POINTER_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int GROUP_ADDRESS_OFFSET = 34;

    public static InitializeGroupPointerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var groupPointerDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var groupAddress = readPubKey(_data, i);
      return new InitializeGroupPointerIxData(discriminator,
                                              groupPointerDiscriminator,
                                              authority,
                                              groupAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) groupPointerDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      if (groupAddress != null) {
        groupAddress.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_GROUP_POINTER_DISCRIMINATOR = toDiscriminator(40);

  /// Update the group pointer address. Only supported for mints that
  /// include the `GroupPointer` extension.
  ///
  /// @param mintKey The mint to initialize.
  /// @param groupPointerAuthorityKey The group pointer authority or its multisignature account.
  public static List<AccountMeta> updateGroupPointerKeys(final PublicKey mintKey,
                                                         final PublicKey groupPointerAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(groupPointerAuthorityKey)
    );
  }

  /// Update the group pointer address. Only supported for mints that
  /// include the `GroupPointer` extension.
  ///
  /// @param mintKey The mint to initialize.
  /// @param groupPointerAuthorityKey The group pointer authority or its multisignature account.
  /// @param groupAddress The new account address that holds the group configurations.
  public static Instruction updateGroupPointer(final AccountMeta invokedToken2022ProgramMeta,
                                               final PublicKey mintKey,
                                               final PublicKey groupPointerAuthorityKey,
                                               final PublicKey groupAddress) {
    final var keys = updateGroupPointerKeys(
      mintKey,
      groupPointerAuthorityKey
    );
    return updateGroupPointer(
      invokedToken2022ProgramMeta,
      keys,
      groupAddress
    );
  }

  /// Update the group pointer address. Only supported for mints that
  /// include the `GroupPointer` extension.
  ///
  /// @param groupAddress The new account address that holds the group configurations.
  public static Instruction updateGroupPointer(final AccountMeta invokedToken2022ProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey groupAddress) {
    final byte[] _data = new byte[34];
    int i = UPDATE_GROUP_POINTER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    if (groupAddress != null) {
      groupAddress.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the group pointer address. Only supported for mints that
  /// include the `GroupPointer` extension.
  ///
  /// @param groupAddress The new account address that holds the group configurations.
  public record UpdateGroupPointerIxData(int discriminator,
                                         int groupPointerDiscriminator,
                                         PublicKey groupAddress) implements SerDe {  

    public static UpdateGroupPointerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int GROUP_POINTER_DISCRIMINATOR_OFFSET = 1;
    public static final int GROUP_ADDRESS_OFFSET = 2;

    public static UpdateGroupPointerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var groupPointerDiscriminator = _data[i] & 0xFF;
      ++i;
      final var groupAddress = readPubKey(_data, i);
      return new UpdateGroupPointerIxData(discriminator, groupPointerDiscriminator, groupAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) groupPointerDiscriminator;
      ++i;
      if (groupAddress != null) {
        groupAddress.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_GROUP_MEMBER_POINTER_DISCRIMINATOR = toDiscriminator(41);

  /// Initialize a new mint with a group member pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeGroupMemberPointerKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with a group member pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param authority The public key for the account that can update the group member address.
  /// @param memberAddress The account address that holds the member.
  public static Instruction initializeGroupMemberPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                         final PublicKey mintKey,
                                                         final PublicKey authority,
                                                         final PublicKey memberAddress) {
    final var keys = initializeGroupMemberPointerKeys(
      mintKey
    );
    return initializeGroupMemberPointer(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      memberAddress
    );
  }

  /// Initialize a new mint with a group member pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the group member address.
  /// @param memberAddress The account address that holds the member.
  public static Instruction initializeGroupMemberPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final PublicKey authority,
                                                         final PublicKey memberAddress) {
    final byte[] _data = new byte[66];
    int i = INITIALIZE_GROUP_MEMBER_POINTER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    if (memberAddress != null) {
      memberAddress.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with a group member pointer
  /// 
  /// Fails if the mint has already been initialized, so must be called before
  /// `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82
  /// bytes), plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The public key for the account that can update the group member address.
  /// @param memberAddress The account address that holds the member.
  public record InitializeGroupMemberPointerIxData(int discriminator,
                                                   int groupMemberPointerDiscriminator,
                                                   PublicKey authority,
                                                   PublicKey memberAddress) implements SerDe {  

    public static InitializeGroupMemberPointerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 66;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int GROUP_MEMBER_POINTER_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int MEMBER_ADDRESS_OFFSET = 34;

    public static InitializeGroupMemberPointerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var groupMemberPointerDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var memberAddress = readPubKey(_data, i);
      return new InitializeGroupMemberPointerIxData(discriminator,
                                                    groupMemberPointerDiscriminator,
                                                    authority,
                                                    memberAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) groupMemberPointerDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      if (memberAddress != null) {
        memberAddress.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_GROUP_MEMBER_POINTER_DISCRIMINATOR = toDiscriminator(41);

  /// Update the group member pointer address. Only supported for mints that
  /// include the `GroupMemberPointer` extension.
  ///
  /// @param mintKey The mint to initialize.
  /// @param groupMemberPointerAuthorityKey The group member pointer authority or its multisignature account.
  public static List<AccountMeta> updateGroupMemberPointerKeys(final PublicKey mintKey,
                                                               final PublicKey groupMemberPointerAuthorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(groupMemberPointerAuthorityKey)
    );
  }

  /// Update the group member pointer address. Only supported for mints that
  /// include the `GroupMemberPointer` extension.
  ///
  /// @param mintKey The mint to initialize.
  /// @param groupMemberPointerAuthorityKey The group member pointer authority or its multisignature account.
  /// @param memberAddress The new account address that holds the member.
  public static Instruction updateGroupMemberPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                     final PublicKey mintKey,
                                                     final PublicKey groupMemberPointerAuthorityKey,
                                                     final PublicKey memberAddress) {
    final var keys = updateGroupMemberPointerKeys(
      mintKey,
      groupMemberPointerAuthorityKey
    );
    return updateGroupMemberPointer(
      invokedToken2022ProgramMeta,
      keys,
      memberAddress
    );
  }

  /// Update the group member pointer address. Only supported for mints that
  /// include the `GroupMemberPointer` extension.
  ///
  /// @param memberAddress The new account address that holds the member.
  public static Instruction updateGroupMemberPointer(final AccountMeta invokedToken2022ProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final PublicKey memberAddress) {
    final byte[] _data = new byte[34];
    int i = UPDATE_GROUP_MEMBER_POINTER_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    if (memberAddress != null) {
      memberAddress.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the group member pointer address. Only supported for mints that
  /// include the `GroupMemberPointer` extension.
  ///
  /// @param memberAddress The new account address that holds the member.
  public record UpdateGroupMemberPointerIxData(int discriminator,
                                               int groupMemberPointerDiscriminator,
                                               PublicKey memberAddress) implements SerDe {  

    public static UpdateGroupMemberPointerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int GROUP_MEMBER_POINTER_DISCRIMINATOR_OFFSET = 1;
    public static final int MEMBER_ADDRESS_OFFSET = 2;

    public static UpdateGroupMemberPointerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var groupMemberPointerDiscriminator = _data[i] & 0xFF;
      ++i;
      final var memberAddress = readPubKey(_data, i);
      return new UpdateGroupMemberPointerIxData(discriminator, groupMemberPointerDiscriminator, memberAddress);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) groupMemberPointerDiscriminator;
      ++i;
      if (memberAddress != null) {
        memberAddress.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_SCALED_UI_AMOUNT_MINT_DISCRIMINATOR = toDiscriminator(43);

  /// Initialize a new mint with the `ScaledUiAmount` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  public static List<AccountMeta> initializeScaledUiAmountMintKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with the `ScaledUiAmount` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param mintKey The mint to initialize.
  /// @param authority The authority that can update the multiplier
  /// @param multiplier The initial multiplier for the scaled UI extension
  public static Instruction initializeScaledUiAmountMint(final AccountMeta invokedToken2022ProgramMeta,
                                                         final PublicKey mintKey,
                                                         final PublicKey authority,
                                                         final double multiplier) {
    final var keys = initializeScaledUiAmountMintKeys(
      mintKey
    );
    return initializeScaledUiAmountMint(
      invokedToken2022ProgramMeta,
      keys,
      authority,
      multiplier
    );
  }

  /// Initialize a new mint with the `ScaledUiAmount` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The authority that can update the multiplier
  /// @param multiplier The initial multiplier for the scaled UI extension
  public static Instruction initializeScaledUiAmountMint(final AccountMeta invokedToken2022ProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final PublicKey authority,
                                                         final double multiplier) {
    final byte[] _data = new byte[42];
    int i = INITIALIZE_SCALED_UI_AMOUNT_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;
    putFloat64LE(_data, i, multiplier);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with the `ScaledUiAmount` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  /// 
  /// The mint must have exactly enough space allocated for the base mint (82 bytes),
  /// plus 83 bytes of padding, 1 byte reserved for the account type,
  /// then space required for this extension, plus any others.
  ///
  /// @param authority The authority that can update the multiplier
  /// @param multiplier The initial multiplier for the scaled UI extension
  public record InitializeScaledUiAmountMintIxData(int discriminator,
                                                   int scaledUiAmountMintDiscriminator,
                                                   PublicKey authority,
                                                   double multiplier) implements SerDe {  

    public static InitializeScaledUiAmountMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 42;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int SCALED_UI_AMOUNT_MINT_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;
    public static final int MULTIPLIER_OFFSET = 34;

    public static InitializeScaledUiAmountMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var scaledUiAmountMintDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      i += 32;
      final var multiplier = getFloat64LE(_data, i);
      return new InitializeScaledUiAmountMintIxData(discriminator,
                                                    scaledUiAmountMintDiscriminator,
                                                    authority,
                                                    multiplier);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) scaledUiAmountMintDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      putFloat64LE(_data, i, multiplier);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_MULTIPLIER_SCALED_UI_MINT_DISCRIMINATOR = toDiscriminator(43);

  /// Update the multiplier. Only supported for mints that include the
  /// `ScaledUiAmountConfig` extension.
  /// You can set a specific timestamp for the multiplier to take effect.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The multiplier authority.
  public static List<AccountMeta> updateMultiplierScaledUiMintKeys(final PublicKey mintKey,
                                                                   final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createWritableSigner(authorityKey)
    );
  }

  /// Update the multiplier. Only supported for mints that include the
  /// `ScaledUiAmountConfig` extension.
  /// You can set a specific timestamp for the multiplier to take effect.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The multiplier authority.
  /// @param multiplier The new multiplier for the scaled UI extension
  /// @param effectiveTimestamp The timestamp at which the new multiplier will take effect
  public static Instruction updateMultiplierScaledUiMint(final AccountMeta invokedToken2022ProgramMeta,
                                                         final PublicKey mintKey,
                                                         final PublicKey authorityKey,
                                                         final double multiplier,
                                                         final long effectiveTimestamp) {
    final var keys = updateMultiplierScaledUiMintKeys(
      mintKey,
      authorityKey
    );
    return updateMultiplierScaledUiMint(
      invokedToken2022ProgramMeta,
      keys,
      multiplier,
      effectiveTimestamp
    );
  }

  /// Update the multiplier. Only supported for mints that include the
  /// `ScaledUiAmountConfig` extension.
  /// You can set a specific timestamp for the multiplier to take effect.
  ///
  /// @param multiplier The new multiplier for the scaled UI extension
  /// @param effectiveTimestamp The timestamp at which the new multiplier will take effect
  public static Instruction updateMultiplierScaledUiMint(final AccountMeta invokedToken2022ProgramMeta,
                                                         final List<AccountMeta> keys,
                                                         final double multiplier,
                                                         final long effectiveTimestamp) {
    final byte[] _data = new byte[18];
    int i = UPDATE_MULTIPLIER_SCALED_UI_MINT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    putFloat64LE(_data, i, multiplier);
    i += 8;
    putInt64LE(_data, i, effectiveTimestamp);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the multiplier. Only supported for mints that include the
  /// `ScaledUiAmountConfig` extension.
  /// You can set a specific timestamp for the multiplier to take effect.
  ///
  /// @param multiplier The new multiplier for the scaled UI extension
  /// @param effectiveTimestamp The timestamp at which the new multiplier will take effect
  public record UpdateMultiplierScaledUiMintIxData(int discriminator,
                                                   int scaledUiAmountMintDiscriminator,
                                                   double multiplier,
                                                   long effectiveTimestamp) implements SerDe {  

    public static UpdateMultiplierScaledUiMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int SCALED_UI_AMOUNT_MINT_DISCRIMINATOR_OFFSET = 1;
    public static final int MULTIPLIER_OFFSET = 2;
    public static final int EFFECTIVE_TIMESTAMP_OFFSET = 10;

    public static UpdateMultiplierScaledUiMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var scaledUiAmountMintDiscriminator = _data[i] & 0xFF;
      ++i;
      final var multiplier = getFloat64LE(_data, i);
      i += 8;
      final var effectiveTimestamp = getInt64LE(_data, i);
      return new UpdateMultiplierScaledUiMintIxData(discriminator,
                                                    scaledUiAmountMintDiscriminator,
                                                    multiplier,
                                                    effectiveTimestamp);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) scaledUiAmountMintDiscriminator;
      ++i;
      putFloat64LE(_data, i, multiplier);
      i += 8;
      putInt64LE(_data, i, effectiveTimestamp);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PAUSABLE_CONFIG_DISCRIMINATOR = toDiscriminator(44);

  /// Initialize a new mint with the `Pausable` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param mintKey The mint.
  public static List<AccountMeta> initializePausableConfigKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Initialize a new mint with the `Pausable` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param mintKey The mint.
  /// @param authority The authority that can pause and resume the mint.
  public static Instruction initializePausableConfig(final AccountMeta invokedToken2022ProgramMeta,
                                                     final PublicKey mintKey,
                                                     final PublicKey authority) {
    final var keys = initializePausableConfigKeys(
      mintKey
    );
    return initializePausableConfig(
      invokedToken2022ProgramMeta,
      keys,
      authority
    );
  }

  /// Initialize a new mint with the `Pausable` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param authority The authority that can pause and resume the mint.
  public static Instruction initializePausableConfig(final AccountMeta invokedToken2022ProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final PublicKey authority) {
    final byte[] _data = new byte[34];
    int i = INITIALIZE_PAUSABLE_CONFIG_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    if (authority != null) {
      authority.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new mint with the `Pausable` extension.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param authority The authority that can pause and resume the mint.
  public record InitializePausableConfigIxData(int discriminator,
                                               int pausableDiscriminator,
                                               PublicKey authority) implements SerDe {  

    public static InitializePausableConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PAUSABLE_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;

    public static InitializePausableConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var pausableDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      return new InitializePausableConfigIxData(discriminator, pausableDiscriminator, authority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) pausableDiscriminator;
      ++i;
      if (authority != null) {
        authority.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PAUSE_DISCRIMINATOR = toDiscriminator(44);

  /// Pause the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The pausable authority that can pause the mint.
  public static List<AccountMeta> pauseKeys(final PublicKey mintKey,
                                            final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Pause the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The pausable authority that can pause the mint.
  public static Instruction pause(final AccountMeta invokedToken2022ProgramMeta,
                                  final PublicKey mintKey,
                                  final PublicKey authorityKey) {
    final var keys = pauseKeys(
      mintKey,
      authorityKey
    );
    return pause(invokedToken2022ProgramMeta, keys);
  }

  /// Pause the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  public static Instruction pause(final AccountMeta invokedToken2022ProgramMeta,
                                  final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = PAUSE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Pause the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  public record PauseIxData(int discriminator, int pausableDiscriminator) implements SerDe {  

    public static PauseIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PAUSABLE_DISCRIMINATOR_OFFSET = 1;

    public static PauseIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var pausableDiscriminator = _data[i] & 0xFF;
      return new PauseIxData(discriminator, pausableDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) pausableDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RESUME_DISCRIMINATOR = toDiscriminator(44);

  /// Resume the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The pausable authority that can resume the mint.
  public static List<AccountMeta> resumeKeys(final PublicKey mintKey,
                                             final PublicKey authorityKey) {
    return List.of(
      createWrite(mintKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Resume the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  /// @param mintKey The mint.
  /// @param authorityKey The pausable authority that can resume the mint.
  public static Instruction resume(final AccountMeta invokedToken2022ProgramMeta,
                                   final PublicKey mintKey,
                                   final PublicKey authorityKey) {
    final var keys = resumeKeys(
      mintKey,
      authorityKey
    );
    return resume(invokedToken2022ProgramMeta, keys);
  }

  /// Resume the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  public static Instruction resume(final AccountMeta invokedToken2022ProgramMeta,
                                   final List<AccountMeta> keys) {
    final byte[] _data = new byte[2];
    int i = RESUME_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 2;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Resume the mint.
  /// 
  /// Fails if the mint is not pausable.
  ///
  public record ResumeIxData(int discriminator, int pausableDiscriminator) implements SerDe {  

    public static ResumeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PAUSABLE_DISCRIMINATOR_OFFSET = 1;

    public static ResumeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var pausableDiscriminator = _data[i] & 0xFF;
      return new ResumeIxData(discriminator, pausableDiscriminator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) pausableDiscriminator;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_TOKEN_METADATA_DISCRIMINATOR = toDiscriminator(210, 225, 30, 162, 88, 184, 77, 141);

  /// Initializes a TLV entry with the basic token-metadata fields.
  /// 
  /// Assumes that the provided mint is an SPL token mint, that the metadata
  /// account is allocated and assigned to the program, and that the metadata
  /// account has enough lamports to cover the rent-exempt reserve.
  ///
  public static List<AccountMeta> initializeTokenMetadataKeys(final PublicKey metadataKey,
                                                              final PublicKey updateAuthorityKey,
                                                              final PublicKey mintKey,
                                                              final PublicKey mintAuthorityKey) {
    return List.of(
      createWrite(metadataKey),
      createRead(updateAuthorityKey),
      createRead(mintKey),
      createReadOnlySigner(mintAuthorityKey)
    );
  }

  /// Initializes a TLV entry with the basic token-metadata fields.
  /// 
  /// Assumes that the provided mint is an SPL token mint, that the metadata
  /// account is allocated and assigned to the program, and that the metadata
  /// account has enough lamports to cover the rent-exempt reserve.
  ///
  /// @param name Longer name of the token.
  /// @param symbol Shortened symbol of the token.
  /// @param uri URI pointing to more metadata (image, video, etc.).
  public static Instruction initializeTokenMetadata(final AccountMeta invokedToken2022ProgramMeta,
                                                    final PublicKey metadataKey,
                                                    final PublicKey updateAuthorityKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey mintAuthorityKey,
                                                    final String name,
                                                    final String symbol,
                                                    final String uri) {
    final var keys = initializeTokenMetadataKeys(
      metadataKey,
      updateAuthorityKey,
      mintKey,
      mintAuthorityKey
    );
    return initializeTokenMetadata(
      invokedToken2022ProgramMeta,
      keys,
      name,
      symbol,
      uri
    );
  }

  /// Initializes a TLV entry with the basic token-metadata fields.
  /// 
  /// Assumes that the provided mint is an SPL token mint, that the metadata
  /// account is allocated and assigned to the program, and that the metadata
  /// account has enough lamports to cover the rent-exempt reserve.
  ///
  /// @param name Longer name of the token.
  /// @param symbol Shortened symbol of the token.
  /// @param uri URI pointing to more metadata (image, video, etc.).
  public static Instruction initializeTokenMetadata(final AccountMeta invokedToken2022ProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final String name,
                                                    final String symbol,
                                                    final String uri) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _symbol = symbol.getBytes(UTF_8);
    final byte[] _uri = uri.getBytes(UTF_8);
    final byte[] _data = new byte[20 + _name.length + _symbol.length + _uri.length];
    int i = INITIALIZE_TOKEN_METADATA_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, _name.length);
    i += 4;
    System.arraycopy(_name, 0, _data, i, _name.length);
    i += _name.length;
    putInt32LE(_data, i, _symbol.length);
    i += 4;
    System.arraycopy(_symbol, 0, _data, i, _symbol.length);
    i += _symbol.length;
    putInt32LE(_data, i, _uri.length);
    i += 4;
    System.arraycopy(_uri, 0, _data, i, _uri.length);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initializes a TLV entry with the basic token-metadata fields.
  /// 
  /// Assumes that the provided mint is an SPL token mint, that the metadata
  /// account is allocated and assigned to the program, and that the metadata
  /// account has enough lamports to cover the rent-exempt reserve.
  ///
  /// @param name Longer name of the token.
  /// @param symbol Shortened symbol of the token.
  /// @param uri URI pointing to more metadata (image, video, etc.).
  public record InitializeTokenMetadataIxData(byte[] discriminator,
                                              String name, byte[] _name,
                                              String symbol, byte[] _symbol,
                                              String uri, byte[] _uri) implements SerDe {  

    public static InitializeTokenMetadataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_SIZE = 8;
    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int NAME_OFFSET = 8;

    public static InitializeTokenMetadataIxData createRecord(final byte[] discriminator,
                                                             final String name,
                                                             final String symbol,
                                                             final String uri) {
      return new InitializeTokenMetadataIxData(checkMaxLength(discriminator, 8),
                                               name, name.getBytes(UTF_8),
                                               symbol, symbol.getBytes(UTF_8),
                                               uri, uri.getBytes(UTF_8));
    }

    public static InitializeTokenMetadataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final int _nameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _name = Arrays.copyOfRange(_data, i, i + _nameLength);
      final var name = new String(_name, UTF_8);
      i += _nameLength;
      final int _symbolLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _symbol = Arrays.copyOfRange(_data, i, i + _symbolLength);
      final var symbol = new String(_symbol, UTF_8);
      i += _symbolLength;
      final int _uriLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _uri = Arrays.copyOfRange(_data, i, i + _uriLength);
      final var uri = new String(_uri, UTF_8);
      return new InitializeTokenMetadataIxData(checkMaxLength(discriminator, 8),
                                               name, _name,
                                               symbol, _symbol,
                                               uri, _uri);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      putInt32LE(_data, i, _name.length);
      i += 4;
      System.arraycopy(_name, 0, _data, i, _name.length);
      i += _name.length;
      putInt32LE(_data, i, _symbol.length);
      i += 4;
      System.arraycopy(_symbol, 0, _data, i, _symbol.length);
      i += _symbol.length;
      putInt32LE(_data, i, _uri.length);
      i += 4;
      System.arraycopy(_uri, 0, _data, i, _uri.length);
      i += _uri.length;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _name.length + _symbol.length + _uri.length;
    }
  }

  public static final Discriminator REMOVE_TOKEN_METADATA_KEY_DISCRIMINATOR = toDiscriminator(234, 18, 32, 56, 89, 141, 37, 181);

  /// Removes a key-value pair in a token-metadata account.
  /// 
  /// This only applies to additional fields, and not the base name / symbol /
  /// URI fields.
  /// 
  /// By the end of the instruction, the metadata account must be properly
  /// resized at the end based on the new size of the TLV entry.
  ///
  public static List<AccountMeta> removeTokenMetadataKeyKeys(final PublicKey metadataKey,
                                                             final PublicKey updateAuthorityKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// Removes a key-value pair in a token-metadata account.
  /// 
  /// This only applies to additional fields, and not the base name / symbol /
  /// URI fields.
  /// 
  /// By the end of the instruction, the metadata account must be properly
  /// resized at the end based on the new size of the TLV entry.
  ///
  /// @param idempotent If the idempotent flag is set to true, then the instruction will not
  ///                   error if the key does not exist
  /// @param key Key to remove in the additional metadata portion.
  public static Instruction removeTokenMetadataKey(final AccountMeta invokedToken2022ProgramMeta,
                                                   final PublicKey metadataKey,
                                                   final PublicKey updateAuthorityKey,
                                                   final boolean idempotent,
                                                   final String key) {
    final var keys = removeTokenMetadataKeyKeys(
      metadataKey,
      updateAuthorityKey
    );
    return removeTokenMetadataKey(
      invokedToken2022ProgramMeta,
      keys,
      idempotent,
      key
    );
  }

  /// Removes a key-value pair in a token-metadata account.
  /// 
  /// This only applies to additional fields, and not the base name / symbol /
  /// URI fields.
  /// 
  /// By the end of the instruction, the metadata account must be properly
  /// resized at the end based on the new size of the TLV entry.
  ///
  /// @param idempotent If the idempotent flag is set to true, then the instruction will not
  ///                   error if the key does not exist
  /// @param key Key to remove in the additional metadata portion.
  public static Instruction removeTokenMetadataKey(final AccountMeta invokedToken2022ProgramMeta,
                                                   final List<AccountMeta> keys,
                                                   final boolean idempotent,
                                                   final String key) {
    final byte[] _key = key.getBytes(UTF_8);
    final byte[] _data = new byte[13 + _key.length];
    int i = REMOVE_TOKEN_METADATA_KEY_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (idempotent ? 1 : 0);
    ++i;
    putInt32LE(_data, i, _key.length);
    i += 4;
    System.arraycopy(_key, 0, _data, i, _key.length);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Removes a key-value pair in a token-metadata account.
  /// 
  /// This only applies to additional fields, and not the base name / symbol /
  /// URI fields.
  /// 
  /// By the end of the instruction, the metadata account must be properly
  /// resized at the end based on the new size of the TLV entry.
  ///
  /// @param idempotent If the idempotent flag is set to true, then the instruction will not
  ///                   error if the key does not exist
  /// @param key Key to remove in the additional metadata portion.
  public record RemoveTokenMetadataKeyIxData(byte[] discriminator,
                                             boolean idempotent,
                                             String key, byte[] _key) implements SerDe {  

    public static RemoveTokenMetadataKeyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_SIZE = 8;
    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int IDEMPOTENT_OFFSET = 8;
    public static final int KEY_OFFSET = 9;

    public static RemoveTokenMetadataKeyIxData createRecord(final byte[] discriminator,
                                                            final boolean idempotent,
                                                            final String key) {
      return new RemoveTokenMetadataKeyIxData(checkMaxLength(discriminator, 8), idempotent, key, key.getBytes(UTF_8));
    }

    public static RemoveTokenMetadataKeyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final var idempotent = _data[i] == 1;
      ++i;
      final int _keyLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _key = Arrays.copyOfRange(_data, i, i + _keyLength);
      final var key = new String(_key, UTF_8);
      return new RemoveTokenMetadataKeyIxData(checkMaxLength(discriminator, 8), idempotent, key, _key);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      _data[i] = (byte) (idempotent ? 1 : 0);
      ++i;
      putInt32LE(_data, i, _key.length);
      i += 4;
      System.arraycopy(_key, 0, _data, i, _key.length);
      i += _key.length;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 1 + _key.length;
    }
  }

  public static final Discriminator UPDATE_TOKEN_METADATA_UPDATE_AUTHORITY_DISCRIMINATOR = toDiscriminator(215, 228, 166, 228, 84, 100, 86, 123);

  /// Updates the token-metadata authority.
  ///
  public static List<AccountMeta> updateTokenMetadataUpdateAuthorityKeys(final PublicKey metadataKey,
                                                                         final PublicKey updateAuthorityKey) {
    return List.of(
      createWrite(metadataKey),
      createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// Updates the token-metadata authority.
  ///
  /// @param newUpdateAuthority New authority for the token metadata, or unset if `None`
  public static Instruction updateTokenMetadataUpdateAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                                               final PublicKey metadataKey,
                                                               final PublicKey updateAuthorityKey,
                                                               final PublicKey newUpdateAuthority) {
    final var keys = updateTokenMetadataUpdateAuthorityKeys(
      metadataKey,
      updateAuthorityKey
    );
    return updateTokenMetadataUpdateAuthority(invokedToken2022ProgramMeta, keys, newUpdateAuthority);
  }

  /// Updates the token-metadata authority.
  ///
  /// @param newUpdateAuthority New authority for the token metadata, or unset if `None`
  public static Instruction updateTokenMetadataUpdateAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                                               final List<AccountMeta> keys,
                                                               final PublicKey newUpdateAuthority) {
    final byte[] _data = new byte[40];
    int i = UPDATE_TOKEN_METADATA_UPDATE_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    if (newUpdateAuthority != null) {
      newUpdateAuthority.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Updates the token-metadata authority.
  ///
  /// @param newUpdateAuthority New authority for the token metadata, or unset if `None`
  public record UpdateTokenMetadataUpdateAuthorityIxData(byte[] discriminator, PublicKey newUpdateAuthority) implements SerDe {  

    public static UpdateTokenMetadataUpdateAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int DISCRIMINATOR_SIZE = 8;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int NEW_UPDATE_AUTHORITY_OFFSET = 8;

    public static UpdateTokenMetadataUpdateAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final var newUpdateAuthority = readPubKey(_data, i);
      return new UpdateTokenMetadataUpdateAuthorityIxData(checkMaxLength(discriminator, 8), newUpdateAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      if (newUpdateAuthority != null) {
        newUpdateAuthority.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator EMIT_TOKEN_METADATA_DISCRIMINATOR = toDiscriminator(250, 166, 180, 250, 13, 12, 184, 70);

  /// Emits the token-metadata as return data
  /// 
  /// The format of the data emitted follows exactly the `TokenMetadata`
  /// struct, but it's possible that the account data is stored in another
  /// format by the program.
  /// 
  /// With this instruction, a program that implements the token-metadata
  /// interface can return `TokenMetadata` without adhering to the specific
  /// byte layout of the `TokenMetadata` struct in any accounts.
  ///
  public static List<AccountMeta> emitTokenMetadataKeys(final PublicKey metadataKey) {
    return List.of(
      createRead(metadataKey)
    );
  }

  /// Emits the token-metadata as return data
  /// 
  /// The format of the data emitted follows exactly the `TokenMetadata`
  /// struct, but it's possible that the account data is stored in another
  /// format by the program.
  /// 
  /// With this instruction, a program that implements the token-metadata
  /// interface can return `TokenMetadata` without adhering to the specific
  /// byte layout of the `TokenMetadata` struct in any accounts.
  ///
  /// @param start Start of range of data to emit
  /// @param end End of range of data to emit
  public static Instruction emitTokenMetadata(final AccountMeta invokedToken2022ProgramMeta,
                                              final PublicKey metadataKey,
                                              final OptionalLong start,
                                              final OptionalLong end) {
    final var keys = emitTokenMetadataKeys(
      metadataKey
    );
    return emitTokenMetadata(
      invokedToken2022ProgramMeta,
      keys,
      start,
      end
    );
  }

  /// Emits the token-metadata as return data
  /// 
  /// The format of the data emitted follows exactly the `TokenMetadata`
  /// struct, but it's possible that the account data is stored in another
  /// format by the program.
  /// 
  /// With this instruction, a program that implements the token-metadata
  /// interface can return `TokenMetadata` without adhering to the specific
  /// byte layout of the `TokenMetadata` struct in any accounts.
  ///
  /// @param start Start of range of data to emit
  /// @param end End of range of data to emit
  public static Instruction emitTokenMetadata(final AccountMeta invokedToken2022ProgramMeta,
                                              final List<AccountMeta> keys,
                                              final OptionalLong start,
                                              final OptionalLong end) {
    final byte[] _data = new byte[
    8
    + (start == null || start.isEmpty() ? 1 : 9)
    + (end == null || end.isEmpty() ? 1 : 9)
    ];
    int i = EMIT_TOKEN_METADATA_DISCRIMINATOR.write(_data, 0);
    i += SerDeUtil.writeOptional(1, start, _data, i);
    SerDeUtil.writeOptional(1, end, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Emits the token-metadata as return data
  /// 
  /// The format of the data emitted follows exactly the `TokenMetadata`
  /// struct, but it's possible that the account data is stored in another
  /// format by the program.
  /// 
  /// With this instruction, a program that implements the token-metadata
  /// interface can return `TokenMetadata` without adhering to the specific
  /// byte layout of the `TokenMetadata` struct in any accounts.
  ///
  /// @param start Start of range of data to emit
  /// @param end End of range of data to emit
  public record EmitTokenMetadataIxData(byte[] discriminator,
                                        OptionalLong start,
                                        OptionalLong end) implements SerDe {  

    public static EmitTokenMetadataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_SIZE = 8;
    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int START_OFFSET = 9;

    public static EmitTokenMetadataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final OptionalLong start;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        start = OptionalLong.empty();
        ++i;

      } else {
        ++i;
        start = OptionalLong.of(getInt64LE(_data, i));
        i += 8;
      }
      final OptionalLong end;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        end = OptionalLong.empty();
      } else {
        ++i;
        end = OptionalLong.of(getInt64LE(_data, i));
      }
      return new EmitTokenMetadataIxData(checkMaxLength(discriminator, 8), start, end);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      i += SerDeUtil.writeOptional(1, start, _data, i);
      i += SerDeUtil.writeOptional(1, end, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (start == null || start.isEmpty() ? 1 : (1 + 8)) + (end == null || end.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator INITIALIZE_TOKEN_GROUP_DISCRIMINATOR = toDiscriminator(121, 113, 108, 39, 54, 51, 0, 4);

  /// Initialize a new `Group`
  /// 
  /// Assumes one has already initialized a mint for the group.
  ///
  public static List<AccountMeta> initializeTokenGroupKeys(final PublicKey groupKey,
                                                           final PublicKey mintKey,
                                                           final PublicKey mintAuthorityKey) {
    return List.of(
      createWrite(groupKey),
      createRead(mintKey),
      createReadOnlySigner(mintAuthorityKey)
    );
  }

  /// Initialize a new `Group`
  /// 
  /// Assumes one has already initialized a mint for the group.
  ///
  /// @param updateAuthority Update authority for the group
  /// @param maxSize The maximum number of group members
  public static Instruction initializeTokenGroup(final AccountMeta invokedToken2022ProgramMeta,
                                                 final PublicKey groupKey,
                                                 final PublicKey mintKey,
                                                 final PublicKey mintAuthorityKey,
                                                 final PublicKey updateAuthority,
                                                 final long maxSize) {
    final var keys = initializeTokenGroupKeys(
      groupKey,
      mintKey,
      mintAuthorityKey
    );
    return initializeTokenGroup(
      invokedToken2022ProgramMeta,
      keys,
      updateAuthority,
      maxSize
    );
  }

  /// Initialize a new `Group`
  /// 
  /// Assumes one has already initialized a mint for the group.
  ///
  /// @param updateAuthority Update authority for the group
  /// @param maxSize The maximum number of group members
  public static Instruction initializeTokenGroup(final AccountMeta invokedToken2022ProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final PublicKey updateAuthority,
                                                 final long maxSize) {
    final byte[] _data = new byte[48];
    int i = INITIALIZE_TOKEN_GROUP_DISCRIMINATOR.write(_data, 0);
    if (updateAuthority != null) {
      updateAuthority.write(_data, i);
    }
    i += 32;
    putInt64LE(_data, i, maxSize);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new `Group`
  /// 
  /// Assumes one has already initialized a mint for the group.
  ///
  /// @param updateAuthority Update authority for the group
  /// @param maxSize The maximum number of group members
  public record InitializeTokenGroupIxData(byte[] discriminator,
                                           PublicKey updateAuthority,
                                           long maxSize) implements SerDe {  

    public static InitializeTokenGroupIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 48;
    public static final int DISCRIMINATOR_SIZE = 8;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int UPDATE_AUTHORITY_OFFSET = 8;
    public static final int MAX_SIZE_OFFSET = 40;

    public static InitializeTokenGroupIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final var updateAuthority = readPubKey(_data, i);
      i += 32;
      final var maxSize = getInt64LE(_data, i);
      return new InitializeTokenGroupIxData(checkMaxLength(discriminator, 8), updateAuthority, maxSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      if (updateAuthority != null) {
        updateAuthority.write(_data, i);
      }
      i += 32;
      putInt64LE(_data, i, maxSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_TOKEN_GROUP_MAX_SIZE_DISCRIMINATOR = toDiscriminator(108, 37, 171, 143, 248, 30, 18, 110);

  /// Update the max size of a `Group`.
  ///
  public static List<AccountMeta> updateTokenGroupMaxSizeKeys(final PublicKey groupKey,
                                                              final PublicKey updateAuthorityKey) {
    return List.of(
      createWrite(groupKey),
      createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// Update the max size of a `Group`.
  ///
  /// @param maxSize New max size for the group
  public static Instruction updateTokenGroupMaxSize(final AccountMeta invokedToken2022ProgramMeta,
                                                    final PublicKey groupKey,
                                                    final PublicKey updateAuthorityKey,
                                                    final long maxSize) {
    final var keys = updateTokenGroupMaxSizeKeys(
      groupKey,
      updateAuthorityKey
    );
    return updateTokenGroupMaxSize(invokedToken2022ProgramMeta, keys, maxSize);
  }

  /// Update the max size of a `Group`.
  ///
  /// @param maxSize New max size for the group
  public static Instruction updateTokenGroupMaxSize(final AccountMeta invokedToken2022ProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final long maxSize) {
    final byte[] _data = new byte[16];
    int i = UPDATE_TOKEN_GROUP_MAX_SIZE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxSize);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the max size of a `Group`.
  ///
  /// @param maxSize New max size for the group
  public record UpdateTokenGroupMaxSizeIxData(byte[] discriminator, long maxSize) implements SerDe {  

    public static UpdateTokenGroupMaxSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;
    public static final int DISCRIMINATOR_SIZE = 8;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int MAX_SIZE_OFFSET = 8;

    public static UpdateTokenGroupMaxSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final var maxSize = getInt64LE(_data, i);
      return new UpdateTokenGroupMaxSizeIxData(checkMaxLength(discriminator, 8), maxSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      putInt64LE(_data, i, maxSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_TOKEN_GROUP_UPDATE_AUTHORITY_DISCRIMINATOR = toDiscriminator(161, 105, 88, 1, 237, 221, 216, 203);

  /// Update the authority of a `Group`.
  ///
  /// @param updateAuthorityKey Current update authority
  public static List<AccountMeta> updateTokenGroupUpdateAuthorityKeys(final PublicKey groupKey,
                                                                      final PublicKey updateAuthorityKey) {
    return List.of(
      createWrite(groupKey),
      createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// Update the authority of a `Group`.
  ///
  /// @param updateAuthorityKey Current update authority
  /// @param newUpdateAuthority New authority for the group, or unset if `None`
  public static Instruction updateTokenGroupUpdateAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                                            final PublicKey groupKey,
                                                            final PublicKey updateAuthorityKey,
                                                            final PublicKey newUpdateAuthority) {
    final var keys = updateTokenGroupUpdateAuthorityKeys(
      groupKey,
      updateAuthorityKey
    );
    return updateTokenGroupUpdateAuthority(invokedToken2022ProgramMeta, keys, newUpdateAuthority);
  }

  /// Update the authority of a `Group`.
  ///
  /// @param newUpdateAuthority New authority for the group, or unset if `None`
  public static Instruction updateTokenGroupUpdateAuthority(final AccountMeta invokedToken2022ProgramMeta,
                                                            final List<AccountMeta> keys,
                                                            final PublicKey newUpdateAuthority) {
    final byte[] _data = new byte[40];
    int i = UPDATE_TOKEN_GROUP_UPDATE_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    if (newUpdateAuthority != null) {
      newUpdateAuthority.write(_data, i);
    }
    i += 32;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Update the authority of a `Group`.
  ///
  /// @param newUpdateAuthority New authority for the group, or unset if `None`
  public record UpdateTokenGroupUpdateAuthorityIxData(byte[] discriminator, PublicKey newUpdateAuthority) implements SerDe {  

    public static UpdateTokenGroupUpdateAuthorityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int DISCRIMINATOR_SIZE = 8;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int NEW_UPDATE_AUTHORITY_OFFSET = 8;

    public static UpdateTokenGroupUpdateAuthorityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, i, discriminator, 0, discriminator.length);
      i += 8;

      final var newUpdateAuthority = readPubKey(_data, i);
      return new UpdateTokenGroupUpdateAuthorityIxData(checkMaxLength(discriminator, 8), newUpdateAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      if (newUpdateAuthority != null) {
        newUpdateAuthority.write(_data, i);
      }
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_TOKEN_GROUP_MEMBER_DISCRIMINATOR = toDiscriminator(152, 32, 222, 176, 223, 237, 116, 134);

  /// Initialize a new `Member` of a `Group`
  /// 
  /// Assumes the `Group` has already been initialized,
  /// as well as the mint for the member.
  ///
  public static List<AccountMeta> initializeTokenGroupMemberKeys(final PublicKey memberKey,
                                                                 final PublicKey memberMintKey,
                                                                 final PublicKey memberMintAuthorityKey,
                                                                 final PublicKey groupKey,
                                                                 final PublicKey groupUpdateAuthorityKey) {
    return List.of(
      createWrite(memberKey),
      createRead(memberMintKey),
      createReadOnlySigner(memberMintAuthorityKey),
      createWrite(groupKey),
      createReadOnlySigner(groupUpdateAuthorityKey)
    );
  }

  /// Initialize a new `Member` of a `Group`
  /// 
  /// Assumes the `Group` has already been initialized,
  /// as well as the mint for the member.
  ///
  public static Instruction initializeTokenGroupMember(final AccountMeta invokedToken2022ProgramMeta,
                                                       final PublicKey memberKey,
                                                       final PublicKey memberMintKey,
                                                       final PublicKey memberMintAuthorityKey,
                                                       final PublicKey groupKey,
                                                       final PublicKey groupUpdateAuthorityKey) {
    final var keys = initializeTokenGroupMemberKeys(
      memberKey,
      memberMintKey,
      memberMintAuthorityKey,
      groupKey,
      groupUpdateAuthorityKey
    );
    return initializeTokenGroupMember(invokedToken2022ProgramMeta, keys);
  }

  /// Initialize a new `Member` of a `Group`
  /// 
  /// Assumes the `Group` has already been initialized,
  /// as well as the mint for the member.
  ///
  public static Instruction initializeTokenGroupMember(final AccountMeta invokedToken2022ProgramMeta,
                                                       final List<AccountMeta> keys) {
    final byte[] _data = new byte[8];
    INITIALIZE_TOKEN_GROUP_MEMBER_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Initialize a new `Member` of a `Group`
  /// 
  /// Assumes the `Group` has already been initialized,
  /// as well as the mint for the member.
  ///
  public record InitializeTokenGroupMemberIxData(byte[] discriminator) implements SerDe {  

    public static InitializeTokenGroupMemberIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 8;
    public static final int DISCRIMINATOR_SIZE = 8;

    public static final int DISCRIMINATOR_OFFSET = 0;

    public static InitializeTokenGroupMemberIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final byte[] discriminator = new byte[8];
      System.arraycopy(_data, _offset, discriminator, 0, discriminator.length);

      return new InitializeTokenGroupMemberIxData(checkMaxLength(discriminator, 8));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      if (discriminator.length > 8) {
        throw new IllegalStateException(String.format(
            "Encoded [length=%d] of [discriminator=%s] cannot be greater than 8.",
            discriminator.length, Arrays.toString(discriminator)
        ));
      }
      System.arraycopy(discriminator, 0, _data, i, discriminator.length);
      i += 8;

      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UNWRAP_LAMPORTS_DISCRIMINATOR = toDiscriminator(45);

  /// Transfer lamports from a native SOL account to a destination account.
  ///
  /// @param sourceKey The source account.
  /// @param destinationKey The destination account.
  /// @param authorityKey The source account's owner or its multisignature account.
  public static List<AccountMeta> unwrapLamportsKeys(final PublicKey sourceKey,
                                                     final PublicKey destinationKey,
                                                     final PublicKey authorityKey) {
    return List.of(
      createWrite(sourceKey),
      createWrite(destinationKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Transfer lamports from a native SOL account to a destination account.
  ///
  /// @param sourceKey The source account.
  /// @param destinationKey The destination account.
  /// @param authorityKey The source account's owner or its multisignature account.
  /// @param amount The amount of lamports to transfer.
  public static Instruction unwrapLamports(final AccountMeta invokedToken2022ProgramMeta,
                                           final PublicKey sourceKey,
                                           final PublicKey destinationKey,
                                           final PublicKey authorityKey,
                                           final OptionalLong amount) {
    final var keys = unwrapLamportsKeys(
      sourceKey,
      destinationKey,
      authorityKey
    );
    return unwrapLamports(invokedToken2022ProgramMeta, keys, amount);
  }

  /// Transfer lamports from a native SOL account to a destination account.
  ///
  /// @param amount The amount of lamports to transfer.
  public static Instruction unwrapLamports(final AccountMeta invokedToken2022ProgramMeta,
                                           final List<AccountMeta> keys,
                                           final OptionalLong amount) {
    final byte[] _data = new byte[
    1
    + (amount == null || amount.isEmpty() ? 1 : 9)
    ];
    int i = UNWRAP_LAMPORTS_DISCRIMINATOR.write(_data, 0);
    SerDeUtil.writeOptional(1, amount, _data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Transfer lamports from a native SOL account to a destination account.
  ///
  /// @param amount The amount of lamports to transfer.
  public record UnwrapLamportsIxData(int discriminator, OptionalLong amount) implements SerDe {  

    public static UnwrapLamportsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int AMOUNT_OFFSET = 2;

    public static UnwrapLamportsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final OptionalLong amount;
      if (SerDeUtil.isAbsent(1, _data, i)) {
        amount = OptionalLong.empty();
      } else {
        ++i;
        amount = OptionalLong.of(getInt64LE(_data, i));
      }
      return new UnwrapLamportsIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      i += SerDeUtil.writeOptional(1, amount, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + (amount == null || amount.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator INITIALIZE_PERMISSIONED_BURN_DISCRIMINATOR = toDiscriminator(46);

  /// Require permissioned burn for the given mint account.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param mintKey The mint account to initialize.
  public static List<AccountMeta> initializePermissionedBurnKeys(final PublicKey mintKey) {
    return List.of(
      createWrite(mintKey)
    );
  }

  /// Require permissioned burn for the given mint account.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param mintKey The mint account to initialize.
  /// @param authority The public key for the account that is required for token burning.
  public static Instruction initializePermissionedBurn(final AccountMeta invokedToken2022ProgramMeta,
                                                       final PublicKey mintKey,
                                                       final PublicKey authority) {
    final var keys = initializePermissionedBurnKeys(
      mintKey
    );
    return initializePermissionedBurn(
      invokedToken2022ProgramMeta,
      keys,
      authority
    );
  }

  /// Require permissioned burn for the given mint account.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param authority The public key for the account that is required for token burning.
  public static Instruction initializePermissionedBurn(final AccountMeta invokedToken2022ProgramMeta,
                                                       final List<AccountMeta> keys,
                                                       final PublicKey authority) {
    final byte[] _data = new byte[34];
    int i = INITIALIZE_PERMISSIONED_BURN_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 0;
    ++i;
    authority.write(_data, i);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Require permissioned burn for the given mint account.
  /// 
  /// Fails if the mint has already been initialized, so must be called before `InitializeMint`.
  ///
  /// @param authority The public key for the account that is required for token burning.
  public record InitializePermissionedBurnIxData(int discriminator,
                                                 int permissionedBurnDiscriminator,
                                                 PublicKey authority) implements SerDe {  

    public static InitializePermissionedBurnIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PERMISSIONED_BURN_DISCRIMINATOR_OFFSET = 1;
    public static final int AUTHORITY_OFFSET = 2;

    public static InitializePermissionedBurnIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var permissionedBurnDiscriminator = _data[i] & 0xFF;
      ++i;
      final var authority = readPubKey(_data, i);
      return new InitializePermissionedBurnIxData(discriminator, permissionedBurnDiscriminator, authority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) permissionedBurnDiscriminator;
      ++i;
      authority.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PERMISSIONED_BURN_DISCRIMINATOR = toDiscriminator(46);

  /// Burn tokens when the mint has the permissioned burn extension enabled.
  ///
  /// @param accountKey The source account to burn from.
  /// @param mintKey The token mint.
  /// @param permissionedBurnAuthorityKey Authority configured on the mint that must sign any permissioned burn instruction.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> permissionedBurnKeys(final PublicKey accountKey,
                                                       final PublicKey mintKey,
                                                       final PublicKey permissionedBurnAuthorityKey,
                                                       final PublicKey authorityKey) {
    return List.of(
      createWrite(accountKey),
      createWrite(mintKey),
      createReadOnlySigner(permissionedBurnAuthorityKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Burn tokens when the mint has the permissioned burn extension enabled.
  ///
  /// @param accountKey The source account to burn from.
  /// @param mintKey The token mint.
  /// @param permissionedBurnAuthorityKey Authority configured on the mint that must sign any permissioned burn instruction.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to burn.
  public static Instruction permissionedBurn(final AccountMeta invokedToken2022ProgramMeta,
                                             final PublicKey accountKey,
                                             final PublicKey mintKey,
                                             final PublicKey permissionedBurnAuthorityKey,
                                             final PublicKey authorityKey,
                                             final long amount) {
    final var keys = permissionedBurnKeys(
      accountKey,
      mintKey,
      permissionedBurnAuthorityKey,
      authorityKey
    );
    return permissionedBurn(
      invokedToken2022ProgramMeta,
      keys,
      amount
    );
  }

  /// Burn tokens when the mint has the permissioned burn extension enabled.
  ///
  /// @param amount The amount of tokens to burn.
  public static Instruction permissionedBurn(final AccountMeta invokedToken2022ProgramMeta,
                                             final List<AccountMeta> keys,
                                             final long amount) {
    final byte[] _data = new byte[10];
    int i = PERMISSIONED_BURN_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 1;
    ++i;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Burn tokens when the mint has the permissioned burn extension enabled.
  ///
  /// @param amount The amount of tokens to burn.
  public record PermissionedBurnIxData(int discriminator,
                                       int permissionedBurnDiscriminator,
                                       long amount) implements SerDe {  

    public static PermissionedBurnIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PERMISSIONED_BURN_DISCRIMINATOR_OFFSET = 1;
    public static final int AMOUNT_OFFSET = 2;

    public static PermissionedBurnIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var permissionedBurnDiscriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      return new PermissionedBurnIxData(discriminator, permissionedBurnDiscriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) permissionedBurnDiscriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PERMISSIONED_BURN_CHECKED_DISCRIMINATOR = toDiscriminator(46);

  /// Burn tokens with expected decimals when the mint has the permissioned burn extension enabled.
  ///
  /// @param accountKey The source account to burn from.
  /// @param mintKey The token mint.
  /// @param permissionedBurnAuthorityKey Authority configured on the mint that must sign any permissioned burn instruction.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> permissionedBurnCheckedKeys(final PublicKey accountKey,
                                                              final PublicKey mintKey,
                                                              final PublicKey permissionedBurnAuthorityKey,
                                                              final PublicKey authorityKey) {
    return List.of(
      createWrite(accountKey),
      createWrite(mintKey),
      createReadOnlySigner(permissionedBurnAuthorityKey),
      createReadOnlySigner(authorityKey)
    );
  }

  /// Burn tokens with expected decimals when the mint has the permissioned burn extension enabled.
  ///
  /// @param accountKey The source account to burn from.
  /// @param mintKey The token mint.
  /// @param permissionedBurnAuthorityKey Authority configured on the mint that must sign any permissioned burn instruction.
  /// @param authorityKey The account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction permissionedBurnChecked(final AccountMeta invokedToken2022ProgramMeta,
                                                    final PublicKey accountKey,
                                                    final PublicKey mintKey,
                                                    final PublicKey permissionedBurnAuthorityKey,
                                                    final PublicKey authorityKey,
                                                    final long amount,
                                                    final int decimals) {
    final var keys = permissionedBurnCheckedKeys(
      accountKey,
      mintKey,
      permissionedBurnAuthorityKey,
      authorityKey
    );
    return permissionedBurnChecked(
      invokedToken2022ProgramMeta,
      keys,
      amount,
      decimals
    );
  }

  /// Burn tokens with expected decimals when the mint has the permissioned burn extension enabled.
  ///
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction permissionedBurnChecked(final AccountMeta invokedToken2022ProgramMeta,
                                                    final List<AccountMeta> keys,
                                                    final long amount,
                                                    final int decimals) {
    final byte[] _data = new byte[11];
    int i = PERMISSIONED_BURN_CHECKED_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) 2;
    ++i;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  /// Burn tokens with expected decimals when the mint has the permissioned burn extension enabled.
  ///
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public record PermissionedBurnCheckedIxData(int discriminator,
                                              int permissionedBurnDiscriminator,
                                              long amount,
                                              int decimals) implements SerDe {  

    public static PermissionedBurnCheckedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static final int DISCRIMINATOR_OFFSET = 0;
    public static final int PERMISSIONED_BURN_DISCRIMINATOR_OFFSET = 1;
    public static final int AMOUNT_OFFSET = 2;
    public static final int DECIMALS_OFFSET = 10;

    public static PermissionedBurnCheckedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      int i = _offset;
      final var discriminator = _data[i] & 0xFF;
      ++i;
      final var permissionedBurnDiscriminator = _data[i] & 0xFF;
      ++i;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var decimals = _data[i] & 0xFF;
      return new PermissionedBurnCheckedIxData(discriminator,
                                               permissionedBurnDiscriminator,
                                               amount,
                                               decimals);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      _data[i] = (byte) permissionedBurnDiscriminator;
      ++i;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) decimals;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  private Token2022Program() {
  }
}
