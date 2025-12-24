package software.sava.idl.clients.spl.token.gen;

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
import software.sava.idl.clients.spl.token.gen.types.AuthorityType;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class TokenProgram {

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
  public static Instruction initializeMint(final AccountMeta invokedTokenProgramMeta,
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
      invokedTokenProgramMeta,
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
  public static Instruction initializeMint(final AccountMeta invokedTokenProgramMeta,
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

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
      if (_data[i] == 0) {
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
  public static Instruction initializeAccount(final AccountMeta invokedTokenProgramMeta,
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
    return initializeAccount(invokedTokenProgramMeta, keys);
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
  public static Instruction initializeAccount(final AccountMeta invokedTokenProgramMeta,
                                              final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    INITIALIZE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction initializeMultisig(final AccountMeta invokedTokenProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey multisigKey,
                                               final int m) {
    final var keys = initializeMultisigKeys(
      solanaAccounts,
      multisigKey
    );
    return initializeMultisig(invokedTokenProgramMeta, keys, m);
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
  public static Instruction initializeMultisig(final AccountMeta invokedTokenProgramMeta,
                                               final List<AccountMeta> keys,
                                               final int m) {
    final byte[] _data = new byte[2];
    int i = INITIALIZE_MULTISIG_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) m;

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction transfer(final AccountMeta invokedTokenProgramMeta,
                                     final PublicKey sourceKey,
                                     final PublicKey destinationKey,
                                     final PublicKey authorityKey,
                                     final long amount) {
    final var keys = transferKeys(
      sourceKey,
      destinationKey,
      authorityKey
    );
    return transfer(invokedTokenProgramMeta, keys, amount);
  }

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param amount The amount of tokens to transfer.
  public static Instruction transfer(final AccountMeta invokedTokenProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long amount) {
    final byte[] _data = new byte[9];
    int i = TRANSFER_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction approve(final AccountMeta invokedTokenProgramMeta,
                                    final PublicKey sourceKey,
                                    final PublicKey delegateKey,
                                    final PublicKey ownerKey,
                                    final long amount) {
    final var keys = approveKeys(
      sourceKey,
      delegateKey,
      ownerKey
    );
    return approve(invokedTokenProgramMeta, keys, amount);
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param amount The amount of tokens the delegate is approved for.
  public static Instruction approve(final AccountMeta invokedTokenProgramMeta,
                                    final List<AccountMeta> keys,
                                    final long amount) {
    final byte[] _data = new byte[9];
    int i = APPROVE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction revoke(final AccountMeta invokedTokenProgramMeta,
                                   final PublicKey sourceKey,
                                   final PublicKey ownerKey) {
    final var keys = revokeKeys(
      sourceKey,
      ownerKey
    );
    return revoke(invokedTokenProgramMeta, keys);
  }

  /// Revokes the delegate's authority.
  ///
  public static Instruction revoke(final AccountMeta invokedTokenProgramMeta,
                                   final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    REVOKE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Revokes the delegate's authority.
  ///
  public record RevokeIxData(int discriminator) implements SerDe {  

    public static RevokeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

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
  public static Instruction setAuthority(final AccountMeta invokedTokenProgramMeta,
                                         final PublicKey ownedKey,
                                         final PublicKey ownerKey,
                                         final AuthorityType authorityType,
                                         final PublicKey newAuthority) {
    final var keys = setAuthorityKeys(
      ownedKey,
      ownerKey
    );
    return setAuthority(
      invokedTokenProgramMeta,
      keys,
      authorityType,
      newAuthority
    );
  }

  /// Sets a new authority of a mint or account.
  ///
  /// @param authorityType The type of authority to update.
  /// @param newAuthority The new authority
  public static Instruction setAuthority(final AccountMeta invokedTokenProgramMeta,
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

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
      if (_data[i] == 0) {
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
  public static Instruction mintTo(final AccountMeta invokedTokenProgramMeta,
                                   final PublicKey mintKey,
                                   final PublicKey tokenKey,
                                   final PublicKey mintAuthorityKey,
                                   final long amount) {
    final var keys = mintToKeys(
      mintKey,
      tokenKey,
      mintAuthorityKey
    );
    return mintTo(invokedTokenProgramMeta, keys, amount);
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param amount The amount of new tokens to mint.
  public static Instruction mintTo(final AccountMeta invokedTokenProgramMeta,
                                   final List<AccountMeta> keys,
                                   final long amount) {
    final byte[] _data = new byte[9];
    int i = MINT_TO_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param amount The amount of new tokens to mint.
  public record MintToIxData(int discriminator, long amount) implements SerDe {  

    public static MintToIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

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
  public static Instruction burn(final AccountMeta invokedTokenProgramMeta,
                                 final PublicKey accountKey,
                                 final PublicKey mintKey,
                                 final PublicKey authorityKey,
                                 final long amount) {
    final var keys = burnKeys(
      accountKey,
      mintKey,
      authorityKey
    );
    return burn(invokedTokenProgramMeta, keys, amount);
  }

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  public static Instruction burn(final AccountMeta invokedTokenProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long amount) {
    final byte[] _data = new byte[9];
    int i = BURN_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction closeAccount(final AccountMeta invokedTokenProgramMeta,
                                         final PublicKey accountKey,
                                         final PublicKey destinationKey,
                                         final PublicKey ownerKey) {
    final var keys = closeAccountKeys(
      accountKey,
      destinationKey,
      ownerKey
    );
    return closeAccount(invokedTokenProgramMeta, keys);
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  public static Instruction closeAccount(final AccountMeta invokedTokenProgramMeta,
                                         final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CLOSE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  public record CloseAccountIxData(int discriminator) implements SerDe {  

    public static CloseAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

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
  public static Instruction freezeAccount(final AccountMeta invokedTokenProgramMeta,
                                          final PublicKey accountKey,
                                          final PublicKey mintKey,
                                          final PublicKey ownerKey) {
    final var keys = freezeAccountKeys(
      accountKey,
      mintKey,
      ownerKey
    );
    return freezeAccount(invokedTokenProgramMeta, keys);
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  public static Instruction freezeAccount(final AccountMeta invokedTokenProgramMeta,
                                          final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    FREEZE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  public record FreezeAccountIxData(int discriminator) implements SerDe {  

    public static FreezeAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

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
  public static Instruction thawAccount(final AccountMeta invokedTokenProgramMeta,
                                        final PublicKey accountKey,
                                        final PublicKey mintKey,
                                        final PublicKey ownerKey) {
    final var keys = thawAccountKeys(
      accountKey,
      mintKey,
      ownerKey
    );
    return thawAccount(invokedTokenProgramMeta, keys);
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  public static Instruction thawAccount(final AccountMeta invokedTokenProgramMeta,
                                        final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    THAW_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  public record ThawAccountIxData(int discriminator) implements SerDe {  

    public static ThawAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

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
  public static Instruction transferChecked(final AccountMeta invokedTokenProgramMeta,
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
      invokedTokenProgramMeta,
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
  public static Instruction transferChecked(final AccountMeta invokedTokenProgramMeta,
                                            final List<AccountMeta> keys,
                                            final long amount,
                                            final int decimals) {
    final byte[] _data = new byte[10];
    int i = TRANSFER_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction approveChecked(final AccountMeta invokedTokenProgramMeta,
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
      invokedTokenProgramMeta,
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
  public static Instruction approveChecked(final AccountMeta invokedTokenProgramMeta,
                                           final List<AccountMeta> keys,
                                           final long amount,
                                           final int decimals) {
    final byte[] _data = new byte[10];
    int i = APPROVE_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction mintToChecked(final AccountMeta invokedTokenProgramMeta,
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
      invokedTokenProgramMeta,
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
  public static Instruction mintToChecked(final AccountMeta invokedTokenProgramMeta,
                                          final List<AccountMeta> keys,
                                          final long amount,
                                          final int decimals) {
    final byte[] _data = new byte[10];
    int i = MINT_TO_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction burnChecked(final AccountMeta invokedTokenProgramMeta,
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
      invokedTokenProgramMeta,
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
  public static Instruction burnChecked(final AccountMeta invokedTokenProgramMeta,
                                        final List<AccountMeta> keys,
                                        final long amount,
                                        final int decimals) {
    final byte[] _data = new byte[10];
    int i = BURN_CHECKED_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) decimals;

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction initializeAccount2(final AccountMeta invokedTokenProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey accountKey,
                                               final PublicKey mintKey,
                                               final PublicKey owner) {
    final var keys = initializeAccount2Keys(
      solanaAccounts,
      accountKey,
      mintKey
    );
    return initializeAccount2(invokedTokenProgramMeta, keys, owner);
  }

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount2(final AccountMeta invokedTokenProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey owner) {
    final byte[] _data = new byte[33];
    int i = INITIALIZE_ACCOUNT_2_DISCRIMINATOR.write(_data, 0);
    owner.write(_data, i);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction syncNative(final AccountMeta invokedTokenProgramMeta,
                                       final PublicKey accountKey) {
    final var keys = syncNativeKeys(
      accountKey
    );
    return syncNative(invokedTokenProgramMeta, keys);
  }

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  public static Instruction syncNative(final AccountMeta invokedTokenProgramMeta,
                                       final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    SYNC_NATIVE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction initializeAccount3(final AccountMeta invokedTokenProgramMeta,
                                               final PublicKey accountKey,
                                               final PublicKey mintKey,
                                               final PublicKey owner) {
    final var keys = initializeAccount3Keys(
      accountKey,
      mintKey
    );
    return initializeAccount3(invokedTokenProgramMeta, keys, owner);
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount3(final AccountMeta invokedTokenProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey owner) {
    final byte[] _data = new byte[33];
    int i = INITIALIZE_ACCOUNT_3_DISCRIMINATOR.write(_data, 0);
    owner.write(_data, i);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param owner The new account's owner/multisignature.
  public record InitializeAccount3IxData(int discriminator, PublicKey owner) implements SerDe {  

    public static InitializeAccount3IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 33;

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
  public static Instruction initializeMultisig2(final AccountMeta invokedTokenProgramMeta,
                                                final PublicKey multisigKey,
                                                final int m) {
    final var keys = initializeMultisig2Keys(
      multisigKey
    );
    return initializeMultisig2(invokedTokenProgramMeta, keys, m);
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig2(final AccountMeta invokedTokenProgramMeta,
                                                final List<AccountMeta> keys,
                                                final int m) {
    final byte[] _data = new byte[2];
    int i = INITIALIZE_MULTISIG_2_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) m;

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public record InitializeMultisig2IxData(int discriminator, int m) implements SerDe {  

    public static InitializeMultisig2IxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 2;

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
  public static Instruction initializeMint2(final AccountMeta invokedTokenProgramMeta,
                                            final PublicKey mintKey,
                                            final int decimals,
                                            final PublicKey mintAuthority,
                                            final PublicKey freezeAuthority) {
    final var keys = initializeMint2Keys(
      mintKey
    );
    return initializeMint2(
      invokedTokenProgramMeta,
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
  public static Instruction initializeMint2(final AccountMeta invokedTokenProgramMeta,
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

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
      if (_data[i] == 0) {
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
  public static Instruction getAccountDataSize(final AccountMeta invokedTokenProgramMeta,
                                               final PublicKey mintKey) {
    final var keys = getAccountDataSizeKeys(
      mintKey
    );
    return getAccountDataSize(invokedTokenProgramMeta, keys);
  }

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  public static Instruction getAccountDataSize(final AccountMeta invokedTokenProgramMeta,
                                               final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    GET_ACCOUNT_DATA_SIZE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction initializeImmutableOwner(final AccountMeta invokedTokenProgramMeta,
                                                     final PublicKey accountKey) {
    final var keys = initializeImmutableOwnerKeys(
      accountKey
    );
    return initializeImmutableOwner(invokedTokenProgramMeta, keys);
  }

  /// Initialize the Immutable Owner extension for the given token account
  /// 
  /// Fails if the account has already been initialized, so must be called
  /// before `InitializeAccount`.
  /// 
  /// No-ops in this version of the program, but is included for compatibility
  /// with the Associated Token Account program.
  ///
  public static Instruction initializeImmutableOwner(final AccountMeta invokedTokenProgramMeta,
                                                     final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    INITIALIZE_IMMUTABLE_OWNER_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction amountToUiAmount(final AccountMeta invokedTokenProgramMeta,
                                             final PublicKey mintKey,
                                             final long amount) {
    final var keys = amountToUiAmountKeys(
      mintKey
    );
    return amountToUiAmount(invokedTokenProgramMeta, keys, amount);
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
  public static Instruction amountToUiAmount(final AccountMeta invokedTokenProgramMeta,
                                             final List<AccountMeta> keys,
                                             final long amount) {
    final byte[] _data = new byte[9];
    int i = AMOUNT_TO_UI_AMOUNT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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
  public static Instruction uiAmountToAmount(final AccountMeta invokedTokenProgramMeta,
                                             final PublicKey mintKey,
                                             final String uiAmount) {
    final var keys = uiAmountToAmountKeys(
      mintKey
    );
    return uiAmountToAmount(invokedTokenProgramMeta, keys, uiAmount);
  }

  /// Convert a UiAmount of tokens to a little-endian `u64` raw Amount, using
  /// the given mint. In this version of the program, the mint can only
  /// specify the number of decimals.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param uiAmount The ui_amount of tokens to reformat.
  public static Instruction uiAmountToAmount(final AccountMeta invokedTokenProgramMeta,
                                             final List<AccountMeta> keys,
                                             final String uiAmount) {
    final byte[] _uiAmount = uiAmount.getBytes(UTF_8);
    final byte[] _data = new byte[5 + _uiAmount.length];
    int i = UI_AMOUNT_TO_AMOUNT_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, _uiAmount.length);
    i += 4;
    System.arraycopy(_uiAmount, 0, _data, i, _uiAmount.length);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
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

  private TokenProgram() {
  }
}
