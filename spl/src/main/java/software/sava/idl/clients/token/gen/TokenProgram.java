package software.sava.idl.clients.token.gen;

import java.lang.String;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.token.gen.types.AuthorityType;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
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
  /// @param MintKey Token mint account.
  public static List<AccountMeta> initializeMintKeys(final AccountMeta invokedTokenProgramMeta                                                     ,
                                                     final SolanaAccounts solanaAccounts,
                                                     final PublicKey MintKey) {
    return List.of(
      createWrite(MintKey),
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
  /// @param MintKey Token mint account.
  /// @param decimals Number of decimals in token account amounts.
  /// @param mintAuthority Minting authority.
  /// @param freezeAuthority Optional authority that can freeze token accounts.
  public static Instruction initializeMint(final AccountMeta invokedTokenProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey MintKey,
                                           final int decimals,
                                           final PublicKey mintAuthority,
                                           final PublicKey freezeAuthority) {
    final var keys = initializeMintKeys(
      invokedTokenProgramMeta,
      solanaAccounts,
      MintKey
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
  public static Instruction initializeMint(final AccountMeta invokedTokenProgramMeta                                           ,
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
    Borsh.writeOptional(freezeAuthority, _data, i);

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
                                     PublicKey freezeAuthority) implements Borsh {  

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
      i += Borsh.writeOptional(freezeAuthority, _data, i);
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
  /// @param AccountKey The account to initialize.
  /// @param MintKey The mint this account will be associated with.
  /// @param OwnerKey The new account's owner/multisignature.
  public static List<AccountMeta> initializeAccountKeys(final AccountMeta invokedTokenProgramMeta                                                        ,
                                                        final SolanaAccounts solanaAccounts,
                                                        final PublicKey AccountKey,
                                                        final PublicKey MintKey,
                                                        final PublicKey OwnerKey) {
    return List.of(
      createWrite(AccountKey),
      createRead(MintKey),
      createRead(OwnerKey),
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
  /// @param AccountKey The account to initialize.
  /// @param MintKey The mint this account will be associated with.
  /// @param OwnerKey The new account's owner/multisignature.
  public static Instruction initializeAccount(final AccountMeta invokedTokenProgramMeta,
                                              final SolanaAccounts solanaAccounts,
                                              final PublicKey AccountKey,
                                              final PublicKey MintKey,
                                              final PublicKey OwnerKey) {
    final var keys = initializeAccountKeys(
      invokedTokenProgramMeta,
      solanaAccounts,
      AccountKey,
      MintKey,
      OwnerKey
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
  public static Instruction initializeAccount(final AccountMeta invokedTokenProgramMeta                                              ,
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
  public record InitializeAccountIxData(int discriminator) implements Borsh {  

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
  /// @param MultisigKey The multisignature account to initialize.
  public static List<AccountMeta> initializeMultisigKeys(final AccountMeta invokedTokenProgramMeta                                                         ,
                                                         final SolanaAccounts solanaAccounts,
                                                         final PublicKey MultisigKey) {
    return List.of(
      createWrite(MultisigKey),
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
  /// @param MultisigKey The multisignature account to initialize.
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig(final AccountMeta invokedTokenProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey MultisigKey,
                                               final int m) {
    final var keys = initializeMultisigKeys(
      invokedTokenProgramMeta,
      solanaAccounts,
      MultisigKey
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
  public static Instruction initializeMultisig(final AccountMeta invokedTokenProgramMeta                                               ,
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
  public record InitializeMultisigIxData(int discriminator, int m) implements Borsh {  

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
  /// @param SourceKey The source account.
  /// @param DestinationKey The destination account.
  /// @param AuthorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> transferKeys(final AccountMeta invokedTokenProgramMeta                                               ,
                                               final PublicKey SourceKey,
                                               final PublicKey DestinationKey,
                                               final PublicKey AuthorityKey) {
    return List.of(
      createWrite(SourceKey),
      createWrite(DestinationKey),
      createReadOnlySigner(AuthorityKey)
    );
  }

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param SourceKey The source account.
  /// @param DestinationKey The destination account.
  /// @param AuthorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to transfer.
  public static Instruction transfer(final AccountMeta invokedTokenProgramMeta,
                                     final PublicKey SourceKey,
                                     final PublicKey DestinationKey,
                                     final PublicKey AuthorityKey,
                                     final long amount) {
    final var keys = transferKeys(
      invokedTokenProgramMeta,
      SourceKey,
      DestinationKey,
      AuthorityKey
    );
    return transfer(invokedTokenProgramMeta, keys, amount);
  }

  /// Transfers tokens from one account to another either directly or via a delegate.
  /// If this account is associated with the native mint then equal amounts
  /// of SOL and Tokens will be transferred to the destination account.
  ///
  /// @param amount The amount of tokens to transfer.
  public static Instruction transfer(final AccountMeta invokedTokenProgramMeta                                     ,
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
  public record TransferIxData(int discriminator, long amount) implements Borsh {  

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
  /// @param SourceKey The source account.
  /// @param DelegateKey The delegate.
  /// @param OwnerKey The source account owner or its multisignature account.
  public static List<AccountMeta> approveKeys(final AccountMeta invokedTokenProgramMeta                                              ,
                                              final PublicKey SourceKey,
                                              final PublicKey DelegateKey,
                                              final PublicKey OwnerKey) {
    return List.of(
      createWrite(SourceKey),
      createRead(DelegateKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param SourceKey The source account.
  /// @param DelegateKey The delegate.
  /// @param OwnerKey The source account owner or its multisignature account.
  /// @param amount The amount of tokens the delegate is approved for.
  public static Instruction approve(final AccountMeta invokedTokenProgramMeta,
                                    final PublicKey SourceKey,
                                    final PublicKey DelegateKey,
                                    final PublicKey OwnerKey,
                                    final long amount) {
    final var keys = approveKeys(
      invokedTokenProgramMeta,
      SourceKey,
      DelegateKey,
      OwnerKey
    );
    return approve(invokedTokenProgramMeta, keys, amount);
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  ///
  /// @param amount The amount of tokens the delegate is approved for.
  public static Instruction approve(final AccountMeta invokedTokenProgramMeta                                    ,
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
  public record ApproveIxData(int discriminator, long amount) implements Borsh {  

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
  /// @param SourceKey The source account.
  /// @param OwnerKey The source account owner or its multisignature.
  public static List<AccountMeta> revokeKeys(final AccountMeta invokedTokenProgramMeta                                             ,
                                             final PublicKey SourceKey,
                                             final PublicKey OwnerKey) {
    return List.of(
      createWrite(SourceKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Revokes the delegate's authority.
  ///
  /// @param SourceKey The source account.
  /// @param OwnerKey The source account owner or its multisignature.
  public static Instruction revoke(final AccountMeta invokedTokenProgramMeta,
                                   final PublicKey SourceKey,
                                   final PublicKey OwnerKey) {
    final var keys = revokeKeys(
      invokedTokenProgramMeta,
      SourceKey,
      OwnerKey
    );
    return revoke(invokedTokenProgramMeta, keys);
  }

  /// Revokes the delegate's authority.
  ///
  public static Instruction revoke(final AccountMeta invokedTokenProgramMeta                                   ,
                                   final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    REVOKE_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Revokes the delegate's authority.
  ///
  public record RevokeIxData(int discriminator) implements Borsh {  

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
  /// @param OwnedKey The mint or account to change the authority of.
  /// @param OwnerKey The current authority or the multisignature account of the mint or account to update.
  public static List<AccountMeta> setAuthorityKeys(final AccountMeta invokedTokenProgramMeta                                                   ,
                                                   final PublicKey OwnedKey,
                                                   final PublicKey OwnerKey) {
    return List.of(
      createWrite(OwnedKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Sets a new authority of a mint or account.
  ///
  /// @param OwnedKey The mint or account to change the authority of.
  /// @param OwnerKey The current authority or the multisignature account of the mint or account to update.
  /// @param authorityType The type of authority to update.
  /// @param newAuthority The new authority
  public static Instruction setAuthority(final AccountMeta invokedTokenProgramMeta,
                                         final PublicKey OwnedKey,
                                         final PublicKey OwnerKey,
                                         final AuthorityType authorityType,
                                         final PublicKey newAuthority) {
    final var keys = setAuthorityKeys(
      invokedTokenProgramMeta,
      OwnedKey,
      OwnerKey
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
  public static Instruction setAuthority(final AccountMeta invokedTokenProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final AuthorityType authorityType,
                                         final PublicKey newAuthority) {
    final byte[] _data = new byte[
    1 + Borsh.len(authorityType)
    + (newAuthority == null ? 1 : 33)
    ];
    int i = SET_AUTHORITY_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(authorityType, _data, i);
    Borsh.writeOptional(newAuthority, _data, i);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Sets a new authority of a mint or account.
  ///
  /// @param authorityType The type of authority to update.
  /// @param newAuthority The new authority
  public record SetAuthorityIxData(int discriminator,
                                   AuthorityType authorityType,
                                   PublicKey newAuthority) implements Borsh {  

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
      i += Borsh.len(authorityType);
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
      i += Borsh.write(authorityType, _data, i);
      i += Borsh.writeOptional(newAuthority, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + Borsh.len(authorityType) + (newAuthority == null ? 1 : (1 + 32));
    }
  }

  public static final Discriminator MINT_TO_DISCRIMINATOR = toDiscriminator(7);

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param MintKey The mint account.
  /// @param TokenKey The account to mint tokens to.
  /// @param MintAuthorityKey The mint's minting authority or its multisignature account.
  public static List<AccountMeta> mintToKeys(final AccountMeta invokedTokenProgramMeta                                             ,
                                             final PublicKey MintKey,
                                             final PublicKey TokenKey,
                                             final PublicKey MintAuthorityKey) {
    return List.of(
      createWrite(MintKey),
      createWrite(TokenKey),
      createReadOnlySigner(MintAuthorityKey)
    );
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param MintKey The mint account.
  /// @param TokenKey The account to mint tokens to.
  /// @param MintAuthorityKey The mint's minting authority or its multisignature account.
  /// @param amount The amount of new tokens to mint.
  public static Instruction mintTo(final AccountMeta invokedTokenProgramMeta,
                                   final PublicKey MintKey,
                                   final PublicKey TokenKey,
                                   final PublicKey MintAuthorityKey,
                                   final long amount) {
    final var keys = mintToKeys(
      invokedTokenProgramMeta,
      MintKey,
      TokenKey,
      MintAuthorityKey
    );
    return mintTo(invokedTokenProgramMeta, keys, amount);
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  ///
  /// @param amount The amount of new tokens to mint.
  public static Instruction mintTo(final AccountMeta invokedTokenProgramMeta                                   ,
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
  public record MintToIxData(int discriminator, long amount) implements Borsh {  

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
  /// @param AccountKey The account to burn from.
  /// @param MintKey The token mint.
  /// @param AuthorityKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> burnKeys(final AccountMeta invokedTokenProgramMeta                                           ,
                                           final PublicKey AccountKey,
                                           final PublicKey MintKey,
                                           final PublicKey AuthorityKey) {
    return List.of(
      createWrite(AccountKey),
      createWrite(MintKey),
      createReadOnlySigner(AuthorityKey)
    );
  }

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  /// @param AccountKey The account to burn from.
  /// @param MintKey The token mint.
  /// @param AuthorityKey The account's owner/delegate or its multisignature account.
  public static Instruction burn(final AccountMeta invokedTokenProgramMeta,
                                 final PublicKey AccountKey,
                                 final PublicKey MintKey,
                                 final PublicKey AuthorityKey,
                                 final long amount) {
    final var keys = burnKeys(
      invokedTokenProgramMeta,
      AccountKey,
      MintKey,
      AuthorityKey
    );
    return burn(invokedTokenProgramMeta, keys, amount);
  }

  /// Burns tokens by removing them from an account. `Burn` does not support
  /// accounts associated with the native mint, use `CloseAccount` instead.
  ///
  public static Instruction burn(final AccountMeta invokedTokenProgramMeta                                 ,
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
  public record BurnIxData(int discriminator, long amount) implements Borsh {  

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
  /// @param AccountKey The account to close.
  /// @param DestinationKey The destination account.
  /// @param OwnerKey The account's owner or its multisignature account.
  public static List<AccountMeta> closeAccountKeys(final AccountMeta invokedTokenProgramMeta                                                   ,
                                                   final PublicKey AccountKey,
                                                   final PublicKey DestinationKey,
                                                   final PublicKey OwnerKey) {
    return List.of(
      createWrite(AccountKey),
      createWrite(DestinationKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  /// @param AccountKey The account to close.
  /// @param DestinationKey The destination account.
  /// @param OwnerKey The account's owner or its multisignature account.
  public static Instruction closeAccount(final AccountMeta invokedTokenProgramMeta,
                                         final PublicKey AccountKey,
                                         final PublicKey DestinationKey,
                                         final PublicKey OwnerKey) {
    final var keys = closeAccountKeys(
      invokedTokenProgramMeta,
      AccountKey,
      DestinationKey,
      OwnerKey
    );
    return closeAccount(invokedTokenProgramMeta, keys);
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  public static Instruction closeAccount(final AccountMeta invokedTokenProgramMeta                                         ,
                                         final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CLOSE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Close an account by transferring all its SOL to the destination account.
  /// Non-native accounts may only be closed if its token amount is zero.
  ///
  public record CloseAccountIxData(int discriminator) implements Borsh {  

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
  /// @param AccountKey The account to freeze.
  /// @param MintKey The token mint.
  /// @param OwnerKey The mint freeze authority or its multisignature account.
  public static List<AccountMeta> freezeAccountKeys(final AccountMeta invokedTokenProgramMeta                                                    ,
                                                    final PublicKey AccountKey,
                                                    final PublicKey MintKey,
                                                    final PublicKey OwnerKey) {
    return List.of(
      createWrite(AccountKey),
      createRead(MintKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  /// @param AccountKey The account to freeze.
  /// @param MintKey The token mint.
  /// @param OwnerKey The mint freeze authority or its multisignature account.
  public static Instruction freezeAccount(final AccountMeta invokedTokenProgramMeta,
                                          final PublicKey AccountKey,
                                          final PublicKey MintKey,
                                          final PublicKey OwnerKey) {
    final var keys = freezeAccountKeys(
      invokedTokenProgramMeta,
      AccountKey,
      MintKey,
      OwnerKey
    );
    return freezeAccount(invokedTokenProgramMeta, keys);
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  public static Instruction freezeAccount(final AccountMeta invokedTokenProgramMeta                                          ,
                                          final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    FREEZE_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Freeze an Initialized account using the Mint's freeze_authority (if set).
  ///
  public record FreezeAccountIxData(int discriminator) implements Borsh {  

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
  /// @param AccountKey The account to thaw.
  /// @param MintKey The token mint.
  /// @param OwnerKey The mint freeze authority or its multisignature account.
  public static List<AccountMeta> thawAccountKeys(final AccountMeta invokedTokenProgramMeta                                                  ,
                                                  final PublicKey AccountKey,
                                                  final PublicKey MintKey,
                                                  final PublicKey OwnerKey) {
    return List.of(
      createWrite(AccountKey),
      createRead(MintKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  /// @param AccountKey The account to thaw.
  /// @param MintKey The token mint.
  /// @param OwnerKey The mint freeze authority or its multisignature account.
  public static Instruction thawAccount(final AccountMeta invokedTokenProgramMeta,
                                        final PublicKey AccountKey,
                                        final PublicKey MintKey,
                                        final PublicKey OwnerKey) {
    final var keys = thawAccountKeys(
      invokedTokenProgramMeta,
      AccountKey,
      MintKey,
      OwnerKey
    );
    return thawAccount(invokedTokenProgramMeta, keys);
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  public static Instruction thawAccount(final AccountMeta invokedTokenProgramMeta                                        ,
                                        final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    THAW_ACCOUNT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedTokenProgramMeta, keys, _data);
  }

  /// Thaw a Frozen account using the Mint's freeze_authority (if set).
  ///
  public record ThawAccountIxData(int discriminator) implements Borsh {  

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
  /// @param SourceKey The source account.
  /// @param MintKey The token mint.
  /// @param DestinationKey The destination account.
  /// @param AuthorityKey The source account's owner/delegate or its multisignature account.
  public static List<AccountMeta> transferCheckedKeys(final AccountMeta invokedTokenProgramMeta                                                      ,
                                                      final PublicKey SourceKey,
                                                      final PublicKey MintKey,
                                                      final PublicKey DestinationKey,
                                                      final PublicKey AuthorityKey) {
    return List.of(
      createWrite(SourceKey),
      createRead(MintKey),
      createWrite(DestinationKey),
      createReadOnlySigner(AuthorityKey)
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
  /// @param SourceKey The source account.
  /// @param MintKey The token mint.
  /// @param DestinationKey The destination account.
  /// @param AuthorityKey The source account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to transfer.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction transferChecked(final AccountMeta invokedTokenProgramMeta,
                                            final PublicKey SourceKey,
                                            final PublicKey MintKey,
                                            final PublicKey DestinationKey,
                                            final PublicKey AuthorityKey,
                                            final long amount,
                                            final int decimals) {
    final var keys = transferCheckedKeys(
      invokedTokenProgramMeta,
      SourceKey,
      MintKey,
      DestinationKey,
      AuthorityKey
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
  public static Instruction transferChecked(final AccountMeta invokedTokenProgramMeta                                            ,
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
                                      int decimals) implements Borsh {  

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
  /// @param SourceKey The source account.
  /// @param MintKey The token mint.
  /// @param DelegateKey The delegate.
  /// @param OwnerKey The source account owner or its multisignature account.
  public static List<AccountMeta> approveCheckedKeys(final AccountMeta invokedTokenProgramMeta                                                     ,
                                                     final PublicKey SourceKey,
                                                     final PublicKey MintKey,
                                                     final PublicKey DelegateKey,
                                                     final PublicKey OwnerKey) {
    return List.of(
      createWrite(SourceKey),
      createRead(MintKey),
      createRead(DelegateKey),
      createReadOnlySigner(OwnerKey)
    );
  }

  /// Approves a delegate. A delegate is given the authority over tokens on
  /// behalf of the source account's owner.
  /// 
  /// This instruction differs from Approve in that the token mint and
  /// decimals value is checked by the caller. This may be useful when
  /// creating transactions offline or within a hardware wallet.
  ///
  /// @param SourceKey The source account.
  /// @param MintKey The token mint.
  /// @param DelegateKey The delegate.
  /// @param OwnerKey The source account owner or its multisignature account.
  /// @param amount The amount of tokens the delegate is approved for.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction approveChecked(final AccountMeta invokedTokenProgramMeta,
                                           final PublicKey SourceKey,
                                           final PublicKey MintKey,
                                           final PublicKey DelegateKey,
                                           final PublicKey OwnerKey,
                                           final long amount,
                                           final int decimals) {
    final var keys = approveCheckedKeys(
      invokedTokenProgramMeta,
      SourceKey,
      MintKey,
      DelegateKey,
      OwnerKey
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
  public static Instruction approveChecked(final AccountMeta invokedTokenProgramMeta                                           ,
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
                                     int decimals) implements Borsh {  

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
  /// @param MintKey The mint.
  /// @param TokenKey The account to mint tokens to.
  /// @param MintAuthorityKey The mint's minting authority or its multisignature account.
  public static List<AccountMeta> mintToCheckedKeys(final AccountMeta invokedTokenProgramMeta                                                    ,
                                                    final PublicKey MintKey,
                                                    final PublicKey TokenKey,
                                                    final PublicKey MintAuthorityKey) {
    return List.of(
      createWrite(MintKey),
      createWrite(TokenKey),
      createReadOnlySigner(MintAuthorityKey)
    );
  }

  /// Mints new tokens to an account. The native mint does not support minting.
  /// 
  /// This instruction differs from MintTo in that the decimals value is
  /// checked by the caller. This may be useful when creating transactions
  /// offline or within a hardware wallet.
  ///
  /// @param MintKey The mint.
  /// @param TokenKey The account to mint tokens to.
  /// @param MintAuthorityKey The mint's minting authority or its multisignature account.
  /// @param amount The amount of new tokens to mint.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction mintToChecked(final AccountMeta invokedTokenProgramMeta,
                                          final PublicKey MintKey,
                                          final PublicKey TokenKey,
                                          final PublicKey MintAuthorityKey,
                                          final long amount,
                                          final int decimals) {
    final var keys = mintToCheckedKeys(
      invokedTokenProgramMeta,
      MintKey,
      TokenKey,
      MintAuthorityKey
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
  public static Instruction mintToChecked(final AccountMeta invokedTokenProgramMeta                                          ,
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
                                    int decimals) implements Borsh {  

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
  /// @param AccountKey The account to burn from.
  /// @param MintKey The token mint.
  /// @param AuthorityKey The account's owner/delegate or its multisignature account.
  public static List<AccountMeta> burnCheckedKeys(final AccountMeta invokedTokenProgramMeta                                                  ,
                                                  final PublicKey AccountKey,
                                                  final PublicKey MintKey,
                                                  final PublicKey AuthorityKey) {
    return List.of(
      createWrite(AccountKey),
      createWrite(MintKey),
      createReadOnlySigner(AuthorityKey)
    );
  }

  /// Burns tokens by removing them from an account. `BurnChecked` does not
  /// support accounts associated with the native mint, use `CloseAccount` instead.
  /// 
  /// This instruction differs from Burn in that the decimals value is checked
  /// by the caller. This may be useful when creating transactions offline or
  /// within a hardware wallet.
  ///
  /// @param AccountKey The account to burn from.
  /// @param MintKey The token mint.
  /// @param AuthorityKey The account's owner/delegate or its multisignature account.
  /// @param amount The amount of tokens to burn.
  /// @param decimals Expected number of base 10 digits to the right of the decimal place.
  public static Instruction burnChecked(final AccountMeta invokedTokenProgramMeta,
                                        final PublicKey AccountKey,
                                        final PublicKey MintKey,
                                        final PublicKey AuthorityKey,
                                        final long amount,
                                        final int decimals) {
    final var keys = burnCheckedKeys(
      invokedTokenProgramMeta,
      AccountKey,
      MintKey,
      AuthorityKey
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
  public static Instruction burnChecked(final AccountMeta invokedTokenProgramMeta                                        ,
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
                                  int decimals) implements Borsh {  

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
  /// @param AccountKey The account to initialize.
  /// @param MintKey The mint this account will be associated with.
  public static List<AccountMeta> initializeAccount2Keys(final AccountMeta invokedTokenProgramMeta                                                         ,
                                                         final SolanaAccounts solanaAccounts,
                                                         final PublicKey AccountKey,
                                                         final PublicKey MintKey) {
    return List.of(
      createWrite(AccountKey),
      createRead(MintKey),
      createRead(solanaAccounts.rentSysVar())
    );
  }

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param AccountKey The account to initialize.
  /// @param MintKey The mint this account will be associated with.
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount2(final AccountMeta invokedTokenProgramMeta,
                                               final SolanaAccounts solanaAccounts,
                                               final PublicKey AccountKey,
                                               final PublicKey MintKey,
                                               final PublicKey owner) {
    final var keys = initializeAccount2Keys(
      invokedTokenProgramMeta,
      solanaAccounts,
      AccountKey,
      MintKey
    );
    return initializeAccount2(invokedTokenProgramMeta, keys, owner);
  }

  /// Like InitializeAccount, but the owner pubkey is passed via instruction
  /// data rather than the accounts list. This variant may be preferable
  /// when using Cross Program Invocation from an instruction that does
  /// not need the owner's `AccountInfo` otherwise.
  ///
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount2(final AccountMeta invokedTokenProgramMeta                                               ,
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
  public record InitializeAccount2IxData(int discriminator, PublicKey owner) implements Borsh {  

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
  /// @param AccountKey The native token account to sync with its underlying lamports.
  public static List<AccountMeta> syncNativeKeys(final AccountMeta invokedTokenProgramMeta                                                 ,
                                                 final PublicKey AccountKey) {
    return List.of(
      createWrite(AccountKey)
    );
  }

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  /// @param AccountKey The native token account to sync with its underlying lamports.
  public static Instruction syncNative(final AccountMeta invokedTokenProgramMeta, final PublicKey AccountKey) {     final var keys = syncNativeKeys(
      invokedTokenProgramMeta,
      AccountKey
    );
    return syncNative(invokedTokenProgramMeta, keys);
  }

  /// Given a wrapped / native token account (a token account containing SOL)
  /// updates its amount field based on the account's underlying `lamports`.
  /// This is useful if a non-wrapped SOL account uses
  /// `system_instruction::transfer` to move lamports to a wrapped token
  /// account, and needs to have its token `amount` field updated.
  ///
  public static Instruction syncNative(final AccountMeta invokedTokenProgramMeta                                       ,
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
  public record SyncNativeIxData(int discriminator) implements Borsh {  

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
  /// @param AccountKey The account to initialize.
  /// @param MintKey The mint this account will be associated with.
  public static List<AccountMeta> initializeAccount3Keys(final AccountMeta invokedTokenProgramMeta                                                         ,
                                                         final PublicKey AccountKey,
                                                         final PublicKey MintKey) {
    return List.of(
      createWrite(AccountKey),
      createRead(MintKey)
    );
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param AccountKey The account to initialize.
  /// @param MintKey The mint this account will be associated with.
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount3(final AccountMeta invokedTokenProgramMeta,
                                               final PublicKey AccountKey,
                                               final PublicKey MintKey,
                                               final PublicKey owner) {
    final var keys = initializeAccount3Keys(
      invokedTokenProgramMeta,
      AccountKey,
      MintKey
    );
    return initializeAccount3(invokedTokenProgramMeta, keys, owner);
  }

  /// Like InitializeAccount2, but does not require the Rent sysvar to be provided.
  ///
  /// @param owner The new account's owner/multisignature.
  public static Instruction initializeAccount3(final AccountMeta invokedTokenProgramMeta                                               ,
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
  public record InitializeAccount3IxData(int discriminator, PublicKey owner) implements Borsh {  

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
  /// @param MultisigKey The multisignature account to initialize.
  public static List<AccountMeta> initializeMultisig2Keys(final AccountMeta invokedTokenProgramMeta                                                          ,
                                                          final PublicKey MultisigKey) {
    return List.of(
      createWrite(MultisigKey)
    );
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param MultisigKey The multisignature account to initialize.
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig2(final AccountMeta invokedTokenProgramMeta,
                                                final PublicKey MultisigKey,
                                                final int m) {
    final var keys = initializeMultisig2Keys(
      invokedTokenProgramMeta,
      MultisigKey
    );
    return initializeMultisig2(invokedTokenProgramMeta, keys, m);
  }

  /// Like InitializeMultisig, but does not require the Rent sysvar to be provided.
  ///
  /// @param m The number of signers (M) required to validate this multisignature account.
  public static Instruction initializeMultisig2(final AccountMeta invokedTokenProgramMeta                                                ,
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
  public record InitializeMultisig2IxData(int discriminator, int m) implements Borsh {  

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
  /// @param MintKey The mint to initialize.
  public static List<AccountMeta> initializeMint2Keys(final AccountMeta invokedTokenProgramMeta                                                      ,
                                                      final PublicKey MintKey) {
    return List.of(
      createWrite(MintKey)
    );
  }

  /// Like `InitializeMint`, but does not require the Rent sysvar to be provided.
  ///
  /// @param MintKey The mint to initialize.
  /// @param decimals Number of base 10 digits to the right of the decimal place.
  /// @param mintAuthority The authority/multisignature to mint tokens.
  /// @param freezeAuthority The optional freeze authority/multisignature of the mint.
  public static Instruction initializeMint2(final AccountMeta invokedTokenProgramMeta,
                                            final PublicKey MintKey,
                                            final int decimals,
                                            final PublicKey mintAuthority,
                                            final PublicKey freezeAuthority) {
    final var keys = initializeMint2Keys(
      invokedTokenProgramMeta,
      MintKey
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
  public static Instruction initializeMint2(final AccountMeta invokedTokenProgramMeta                                            ,
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
    Borsh.writeOptional(freezeAuthority, _data, i);

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
                                      PublicKey freezeAuthority) implements Borsh {  

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
      i += Borsh.writeOptional(freezeAuthority, _data, i);
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
  /// @param MintKey The mint to calculate for.
  public static List<AccountMeta> getAccountDataSizeKeys(final AccountMeta invokedTokenProgramMeta                                                         ,
                                                         final PublicKey MintKey) {
    return List.of(
      createRead(MintKey)
    );
  }

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param MintKey The mint to calculate for.
  public static Instruction getAccountDataSize(final AccountMeta invokedTokenProgramMeta, final PublicKey MintKey) {     final var keys = getAccountDataSizeKeys(
      invokedTokenProgramMeta,
      MintKey
    );
    return getAccountDataSize(invokedTokenProgramMeta, keys);
  }

  /// Gets the required size of an account for the given mint as a
  /// little-endian `u64`.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  public static Instruction getAccountDataSize(final AccountMeta invokedTokenProgramMeta                                               ,
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
  public record GetAccountDataSizeIxData(int discriminator) implements Borsh {  

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
  /// @param AccountKey The account to initialize.
  public static List<AccountMeta> initializeImmutableOwnerKeys(final AccountMeta invokedTokenProgramMeta                                                               ,
                                                               final PublicKey AccountKey) {
    return List.of(
      createWrite(AccountKey)
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
  /// @param AccountKey The account to initialize.
  public static Instruction initializeImmutableOwner(final AccountMeta invokedTokenProgramMeta, final PublicKey AccountKey) {     final var keys = initializeImmutableOwnerKeys(
      invokedTokenProgramMeta,
      AccountKey
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
  public static Instruction initializeImmutableOwner(final AccountMeta invokedTokenProgramMeta                                                     ,
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
  public record InitializeImmutableOwnerIxData(int discriminator) implements Borsh {  

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
  /// @param MintKey The mint to calculate for.
  public static List<AccountMeta> amountToUiAmountKeys(final AccountMeta invokedTokenProgramMeta                                                       ,
                                                       final PublicKey MintKey) {
    return List.of(
      createRead(MintKey)
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
  /// @param MintKey The mint to calculate for.
  /// @param amount The amount of tokens to reformat.
  public static Instruction amountToUiAmount(final AccountMeta invokedTokenProgramMeta,
                                             final PublicKey MintKey,
                                             final long amount) {
    final var keys = amountToUiAmountKeys(
      invokedTokenProgramMeta,
      MintKey
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
  public static Instruction amountToUiAmount(final AccountMeta invokedTokenProgramMeta                                             ,
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
  public record AmountToUiAmountIxData(int discriminator, long amount) implements Borsh {  

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
  /// @param MintKey The mint to calculate for.
  public static List<AccountMeta> uiAmountToAmountKeys(final AccountMeta invokedTokenProgramMeta                                                       ,
                                                       final PublicKey MintKey) {
    return List.of(
      createRead(MintKey)
    );
  }

  /// Convert a UiAmount of tokens to a little-endian `u64` raw Amount, using
  /// the given mint. In this version of the program, the mint can only
  /// specify the number of decimals.
  /// 
  /// Return data can be fetched using `sol_get_return_data` and deserializing
  /// the return data as a little-endian `u64`.
  ///
  /// @param MintKey The mint to calculate for.
  /// @param uiAmount The ui_amount of tokens to reformat.
  public static Instruction uiAmountToAmount(final AccountMeta invokedTokenProgramMeta,
                                             final PublicKey MintKey,
                                             final String uiAmount) {
    final var keys = uiAmountToAmountKeys(
      invokedTokenProgramMeta,
      MintKey
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
  public static Instruction uiAmountToAmount(final AccountMeta invokedTokenProgramMeta                                             ,
                                             final List<AccountMeta> keys,
                                             final String uiAmount) {
    final byte[] _uiAmount = uiAmount.getBytes(UTF_8);
    final byte[] _data = new byte[5 + Borsh.lenVector(_uiAmount)];
    int i = UI_AMOUNT_TO_AMOUNT_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(_uiAmount, _data, i);

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
  public record UiAmountToAmountIxData(int discriminator, String uiAmount, byte[] _uiAmount) implements Borsh {  

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
      final var uiAmount = Borsh.string(_data, i);
      return new UiAmountToAmountIxData(discriminator, uiAmount, uiAmount.getBytes(UTF_8));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset;
      _data[i] = (byte) discriminator;
      ++i;
      i += Borsh.writeVector(_uiAmount, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + Borsh.lenVector(_uiAmount);
    }
  }

  private TokenProgram() {
  }
}
