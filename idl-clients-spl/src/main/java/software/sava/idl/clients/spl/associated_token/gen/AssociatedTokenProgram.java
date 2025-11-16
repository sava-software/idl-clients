package software.sava.idl.clients.spl.associated_token.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.serial.Serializable;
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
  /// @param payerKey Funding account (must be a system account).
  /// @param ataKey Associated token account address to be created.
  /// @param ownerKey Wallet address for the new associated token account.
  /// @param mintKey The token mint for the new associated token account.
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> createAssociatedTokenKeys(final SolanaAccounts solanaAccounts,
                                                            final PublicKey payerKey,
                                                            final PublicKey ataKey,
                                                            final PublicKey ownerKey,
                                                            final PublicKey mintKey,
                                                            final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(ataKey),
      createRead(ownerKey),
      createRead(mintKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(tokenProgramKey)
    );
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  /// @param payerKey Funding account (must be a system account).
  /// @param ataKey Associated token account address to be created.
  /// @param ownerKey Wallet address for the new associated token account.
  /// @param mintKey The token mint for the new associated token account.
  /// @param tokenProgramKey SPL Token program.
  public static Instruction createAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                  final SolanaAccounts solanaAccounts,
                                                  final PublicKey payerKey,
                                                  final PublicKey ataKey,
                                                  final PublicKey ownerKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey tokenProgramKey) {
    final var keys = createAssociatedTokenKeys(
      solanaAccounts,
      payerKey,
      ataKey,
      ownerKey,
      mintKey,
      tokenProgramKey
    );
    return createAssociatedToken(invokedAssociatedTokenProgramMeta, keys);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  public static Instruction createAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                  final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CREATE_ASSOCIATED_TOKEN_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedAssociatedTokenProgramMeta, keys, _data);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint Returns an error if the account exists.
  ///
  public record CreateAssociatedTokenIxData(int discriminator) implements Serializable {  

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
  /// @param payerKey Funding account (must be a system account).
  /// @param ataKey Associated token account address to be created.
  /// @param ownerKey Wallet address for the new associated token account.
  /// @param mintKey The token mint for the new associated token account.
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> createAssociatedTokenIdempotentKeys(final SolanaAccounts solanaAccounts,
                                                                      final PublicKey payerKey,
                                                                      final PublicKey ataKey,
                                                                      final PublicKey ownerKey,
                                                                      final PublicKey mintKey,
                                                                      final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(ataKey),
      createRead(ownerKey),
      createRead(mintKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(tokenProgramKey)
    );
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  /// @param payerKey Funding account (must be a system account).
  /// @param ataKey Associated token account address to be created.
  /// @param ownerKey Wallet address for the new associated token account.
  /// @param mintKey The token mint for the new associated token account.
  /// @param tokenProgramKey SPL Token program.
  public static Instruction createAssociatedTokenIdempotent(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                            final SolanaAccounts solanaAccounts,
                                                            final PublicKey payerKey,
                                                            final PublicKey ataKey,
                                                            final PublicKey ownerKey,
                                                            final PublicKey mintKey,
                                                            final PublicKey tokenProgramKey) {
    final var keys = createAssociatedTokenIdempotentKeys(
      solanaAccounts,
      payerKey,
      ataKey,
      ownerKey,
      mintKey,
      tokenProgramKey
    );
    return createAssociatedTokenIdempotent(invokedAssociatedTokenProgramMeta, keys);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  public static Instruction createAssociatedTokenIdempotent(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                            final List<AccountMeta> keys) {
    final byte[] _data = new byte[1];
    CREATE_ASSOCIATED_TOKEN_IDEMPOTENT_DISCRIMINATOR.write(_data, 0);

    return Instruction.createInstruction(invokedAssociatedTokenProgramMeta, keys, _data);
  }

  /// Creates an associated token account for the given wallet address and
  /// token mint, if it doesn't already exist. Returns an error if the
  /// account exists, but with a different owner.
  ///
  public record CreateAssociatedTokenIdempotentIxData(int discriminator) implements Serializable {  

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
  /// @param nestedAssociatedAccountAddressKey Nested associated token account, must be owned by `ownerAssociatedAccountAddress`.
  /// @param nestedTokenMintAddressKey Token mint for the nested associated token account.
  /// @param destinationAssociatedAccountAddressKey Wallet's associated token account.
  /// @param ownerAssociatedAccountAddressKey Owner associated token account address, must be owned by `walletAddress`.
  /// @param ownerTokenMintAddressKey Token mint for the owner associated token account.
  /// @param walletAddressKey Wallet address for the owner associated token account.
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> recoverNestedAssociatedTokenKeys(final PublicKey nestedAssociatedAccountAddressKey,
                                                                   final PublicKey nestedTokenMintAddressKey,
                                                                   final PublicKey destinationAssociatedAccountAddressKey,
                                                                   final PublicKey ownerAssociatedAccountAddressKey,
                                                                   final PublicKey ownerTokenMintAddressKey,
                                                                   final PublicKey walletAddressKey,
                                                                   final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(nestedAssociatedAccountAddressKey),
      createRead(nestedTokenMintAddressKey),
      createWrite(destinationAssociatedAccountAddressKey),
      createRead(ownerAssociatedAccountAddressKey),
      createRead(ownerTokenMintAddressKey),
      createWritableSigner(walletAddressKey),
      createRead(tokenProgramKey)
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
  /// @param nestedAssociatedAccountAddressKey Nested associated token account, must be owned by `ownerAssociatedAccountAddress`.
  /// @param nestedTokenMintAddressKey Token mint for the nested associated token account.
  /// @param destinationAssociatedAccountAddressKey Wallet's associated token account.
  /// @param ownerAssociatedAccountAddressKey Owner associated token account address, must be owned by `walletAddress`.
  /// @param ownerTokenMintAddressKey Token mint for the owner associated token account.
  /// @param walletAddressKey Wallet address for the owner associated token account.
  /// @param tokenProgramKey SPL Token program.
  public static Instruction recoverNestedAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta,
                                                         final PublicKey nestedAssociatedAccountAddressKey,
                                                         final PublicKey nestedTokenMintAddressKey,
                                                         final PublicKey destinationAssociatedAccountAddressKey,
                                                         final PublicKey ownerAssociatedAccountAddressKey,
                                                         final PublicKey ownerTokenMintAddressKey,
                                                         final PublicKey walletAddressKey,
                                                         final PublicKey tokenProgramKey) {
    final var keys = recoverNestedAssociatedTokenKeys(
      nestedAssociatedAccountAddressKey,
      nestedTokenMintAddressKey,
      destinationAssociatedAccountAddressKey,
      ownerAssociatedAccountAddressKey,
      ownerTokenMintAddressKey,
      walletAddressKey,
      tokenProgramKey
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
  public static Instruction recoverNestedAssociatedToken(final AccountMeta invokedAssociatedTokenProgramMeta,
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
  public record RecoverNestedAssociatedTokenIxData(int discriminator) implements Serializable {  

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
