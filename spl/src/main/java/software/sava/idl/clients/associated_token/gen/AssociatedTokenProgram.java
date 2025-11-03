package software.sava.idl.clients.associated_token.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class AssociatedTokenProgram {

  public static final Discriminator CREATE_ASSOCIATED_TOKEN_DISCRIMINATOR = toDiscriminator(0);

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  /// @param PayerKey Funding account (must be a system account).
  /// @param AtaKey Associated token account address to be created.
  /// @param OwnerKey Wallet address for the new associated token account.
  /// @param MintKey The token mint for the new associated token account.
  public static List<AccountMeta> createAssociatedTokenKeys(final AccountMeta invokedAssociatedTokenProgramMeta                                                            ,
                                                            final SolanaAccounts solanaAccounts,
                                                            final PublicKey PayerKey,
                                                            final PublicKey AtaKey,
                                                            final PublicKey OwnerKey,
                                                            final PublicKey MintKey) {
    return List.of(
      createWritableSigner(PayerKey),
      createWrite(AtaKey),
      createRead(OwnerKey),
      createRead(MintKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.tokenProgram())
    );
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  /// @param PayerKey Funding account (must be a system account).
  /// @param AtaKey Associated token account address to be created.
  /// @param OwnerKey Wallet address for the new associated token account.
  /// @param MintKey The token mint for the new associated token account.
  public static Instruction createAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                  final SolanaAccounts solanaAccounts,
                                                  final PublicKey PayerKey,
                                                  final PublicKey AtaKey,
                                                  final PublicKey OwnerKey,
                                                  final PublicKey MintKey) {
    final var keys = createAssociatedTokenKeys(
      invokedAssociatedTokenProgramMeta,
      solanaAccounts,
      PayerKey,
      AtaKey,
      OwnerKey,
      MintKey
    );
    return createAssociatedToken(invokedAssociatedTokenProgramMeta, keys);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  public static Instruction createAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta                                                  ,
                                                  final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CREATE_ASSOCIATED_TOKEN_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedAssociatedTokenProgramMeta, keys, _data);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  public record CreateAssociatedTokenIxData(int discriminator) implements Borsh {  

    public static CreateAssociatedTokenIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static CreateAssociatedTokenIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      final var discriminator = _data[_offset] & 0xFF;
      return new CreateAssociatedTokenIxData(discriminator);
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

  public static final Discriminator CREATE_ASSOCIATED_TOKEN_IDEMPOTENT_DISCRIMINATOR = toDiscriminator(1);

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  /// @param PayerKey Funding account (must be a system account).
  /// @param AtaKey Associated token account address to be created.
  /// @param OwnerKey Wallet address for the new associated token account.
  /// @param MintKey The token mint for the new associated token account.
  public static List<AccountMeta> createAssociatedTokenIdempotentKeys(final AccountMeta invokedAssociatedTokenProgramMeta                                                                      ,
                                                                      final SolanaAccounts solanaAccounts,
                                                                      final PublicKey PayerKey,
                                                                      final PublicKey AtaKey,
                                                                      final PublicKey OwnerKey,
                                                                      final PublicKey MintKey) {
    return List.of(
      createWritableSigner(PayerKey),
      createWrite(AtaKey),
      createRead(OwnerKey),
      createRead(MintKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(solanaAccounts.tokenProgram())
    );
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  /// @param PayerKey Funding account (must be a system account).
  /// @param AtaKey Associated token account address to be created.
  /// @param OwnerKey Wallet address for the new associated token account.
  /// @param MintKey The token mint for the new associated token account.
  public static Instruction createAssociatedTokenIdempotent(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                            final SolanaAccounts solanaAccounts,
                                                            final PublicKey PayerKey,
                                                            final PublicKey AtaKey,
                                                            final PublicKey OwnerKey,
                                                            final PublicKey MintKey) {
    final var keys = createAssociatedTokenIdempotentKeys(
      invokedAssociatedTokenProgramMeta,
      solanaAccounts,
      PayerKey,
      AtaKey,
      OwnerKey,
      MintKey
    );
    return createAssociatedTokenIdempotent(invokedAssociatedTokenProgramMeta, keys);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  public static Instruction createAssociatedTokenIdempotent(final AccountMeta invokedAssociatedTokenProgramMeta                                                            ,
                                                            final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CREATE_ASSOCIATED_TOKEN_IDEMPOTENT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedAssociatedTokenProgramMeta, keys, _data);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  public record CreateAssociatedTokenIdempotentIxData(int discriminator) implements Borsh {  

    public static CreateAssociatedTokenIdempotentIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static CreateAssociatedTokenIdempotentIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      final var discriminator = _data[_offset] & 0xFF;
      return new CreateAssociatedTokenIdempotentIxData(discriminator);
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

  public static final Discriminator RECOVER_NESTED_ASSOCIATED_TOKEN_DISCRIMINATOR = toDiscriminator(2);

  /// Transfers from and closes a nested associated token account: an
  /// associated token account owned by an associated token account.
  /// 
  /// The tokens are moved from the nested associated token account to the
  /// wallet's associated token account, and the nested account lamports are
  /// moved to the wallet.
  /// 
  /// Note: Nested token accounts are an anti-pattern, and almost always
  /// created unintentionally, so this instruction should only be used to
  /// recover from errors.
  ///
  /// @param NestedAssociatedAccountAddressKey Nested associated token account, must be owned by `ownerAssociatedAccountAddress`.
  /// @param NestedTokenMintAddressKey Token mint for the nested associated token account.
  /// @param DestinationAssociatedAccountAddressKey Wallet's associated token account.
  /// @param OwnerAssociatedAccountAddressKey Owner associated token account address, must be owned by `walletAddress`.
  /// @param OwnerTokenMintAddressKey Token mint for the owner associated token account.
  /// @param WalletAddressKey Wallet address for the owner associated token account.
  public static List<AccountMeta> recoverNestedAssociatedTokenKeys(final AccountMeta invokedAssociatedTokenProgramMeta                                                                   ,
                                                                   final SolanaAccounts solanaAccounts,
                                                                   final PublicKey NestedAssociatedAccountAddressKey,
                                                                   final PublicKey NestedTokenMintAddressKey,
                                                                   final PublicKey DestinationAssociatedAccountAddressKey,
                                                                   final PublicKey OwnerAssociatedAccountAddressKey,
                                                                   final PublicKey OwnerTokenMintAddressKey,
                                                                   final PublicKey WalletAddressKey) {
    return List.of(
      createWrite(NestedAssociatedAccountAddressKey),
      createRead(NestedTokenMintAddressKey),
      createWrite(DestinationAssociatedAccountAddressKey),
      createRead(OwnerAssociatedAccountAddressKey),
      createRead(OwnerTokenMintAddressKey),
      createWritableSigner(WalletAddressKey),
      createRead(solanaAccounts.tokenProgram())
    );
  }

  /// Transfers from and closes a nested associated token account: an
  /// associated token account owned by an associated token account.
  /// 
  /// The tokens are moved from the nested associated token account to the
  /// wallet's associated token account, and the nested account lamports are
  /// moved to the wallet.
  /// 
  /// Note: Nested token accounts are an anti-pattern, and almost always
  /// created unintentionally, so this instruction should only be used to
  /// recover from errors.
  ///
  /// @param NestedAssociatedAccountAddressKey Nested associated token account, must be owned by `ownerAssociatedAccountAddress`.
  /// @param NestedTokenMintAddressKey Token mint for the nested associated token account.
  /// @param DestinationAssociatedAccountAddressKey Wallet's associated token account.
  /// @param OwnerAssociatedAccountAddressKey Owner associated token account address, must be owned by `walletAddress`.
  /// @param OwnerTokenMintAddressKey Token mint for the owner associated token account.
  /// @param WalletAddressKey Wallet address for the owner associated token account.
  public static Instruction recoverNestedAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                         final SolanaAccounts solanaAccounts,
                                                         final PublicKey NestedAssociatedAccountAddressKey,
                                                         final PublicKey NestedTokenMintAddressKey,
                                                         final PublicKey DestinationAssociatedAccountAddressKey,
                                                         final PublicKey OwnerAssociatedAccountAddressKey,
                                                         final PublicKey OwnerTokenMintAddressKey,
                                                         final PublicKey WalletAddressKey) {
    final var keys = recoverNestedAssociatedTokenKeys(
      invokedAssociatedTokenProgramMeta,
      solanaAccounts,
      NestedAssociatedAccountAddressKey,
      NestedTokenMintAddressKey,
      DestinationAssociatedAccountAddressKey,
      OwnerAssociatedAccountAddressKey,
      OwnerTokenMintAddressKey,
      WalletAddressKey
    );
    return recoverNestedAssociatedToken(invokedAssociatedTokenProgramMeta, keys);
  }

  /// Transfers from and closes a nested associated token account: an
  /// associated token account owned by an associated token account.
  /// 
  /// The tokens are moved from the nested associated token account to the
  /// wallet's associated token account, and the nested account lamports are
  /// moved to the wallet.
  /// 
  /// Note: Nested token accounts are an anti-pattern, and almost always
  /// created unintentionally, so this instruction should only be used to
  /// recover from errors.
  ///
  public static Instruction recoverNestedAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta                                                         ,
                                                         final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    RECOVER_NESTED_ASSOCIATED_TOKEN_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedAssociatedTokenProgramMeta, keys, _data);
  }

  /// Transfers from and closes a nested associated token account: an
  /// associated token account owned by an associated token account.
  /// 
  /// The tokens are moved from the nested associated token account to the
  /// wallet's associated token account, and the nested account lamports are
  /// moved to the wallet.
  /// 
  /// Note: Nested token accounts are an anti-pattern, and almost always
  /// created unintentionally, so this instruction should only be used to
  /// recover from errors.
  ///
  public record RecoverNestedAssociatedTokenIxData(int discriminator) implements Borsh {  

    public static RecoverNestedAssociatedTokenIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 1;

    public static RecoverNestedAssociatedTokenIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }

      final var discriminator = _data[_offset] & 0xFF;
      return new RecoverNestedAssociatedTokenIxData(discriminator);
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

  private AssociatedTokenProgram() {
  }
}
