package software.sava.idl.clients.jupiter.merkle_distributor.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.merkle_distributor.gen.types.NewDistributorParams;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class MerkleDistributorProgram {

  public static final Discriminator NEW_DISTRIBUTOR_DISCRIMINATOR = toDiscriminator(32, 139, 112, 171, 0, 2, 225, 155);

  /// ADMIN FUNCTIONS ////
  ///
  /// @param distributorKey MerkleDistributor.
  /// @param baseKey Base key of the distributor.
  /// @param clawbackReceiverKey Clawback receiver token account
  /// @param mintKey The mint to distribute.
  /// @param tokenVaultKey Token vault
  ///                      Should create previously
  /// @param adminKey Admin wallet, responsible for creating the distributor and paying for the transaction.
  ///                 Also has the authority to set the clawback receiver and change itself.
  /// @param systemProgramKey The System program.
  /// @param tokenProgramKey The Token program.
  public static List<AccountMeta> newDistributorKeys(final PublicKey distributorKey,
                                                     final PublicKey baseKey,
                                                     final PublicKey clawbackReceiverKey,
                                                     final PublicKey mintKey,
                                                     final PublicKey tokenVaultKey,
                                                     final PublicKey adminKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createReadOnlySigner(baseKey),
      createWrite(clawbackReceiverKey),
      createRead(mintKey),
      createRead(tokenVaultKey),
      createWritableSigner(adminKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey)
    );
  }

  /// ADMIN FUNCTIONS ////
  ///
  /// @param distributorKey MerkleDistributor.
  /// @param baseKey Base key of the distributor.
  /// @param clawbackReceiverKey Clawback receiver token account
  /// @param mintKey The mint to distribute.
  /// @param tokenVaultKey Token vault
  ///                      Should create previously
  /// @param adminKey Admin wallet, responsible for creating the distributor and paying for the transaction.
  ///                 Also has the authority to set the clawback receiver and change itself.
  /// @param systemProgramKey The System program.
  /// @param tokenProgramKey The Token program.
  public static Instruction newDistributor(final AccountMeta invokedMerkleDistributorProgramMeta,
                                           final PublicKey distributorKey,
                                           final PublicKey baseKey,
                                           final PublicKey clawbackReceiverKey,
                                           final PublicKey mintKey,
                                           final PublicKey tokenVaultKey,
                                           final PublicKey adminKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey tokenProgramKey,
                                           final NewDistributorParams params) {
    final var keys = newDistributorKeys(
      distributorKey,
      baseKey,
      clawbackReceiverKey,
      mintKey,
      tokenVaultKey,
      adminKey,
      systemProgramKey,
      tokenProgramKey
    );
    return newDistributor(invokedMerkleDistributorProgramMeta, keys, params);
  }

  /// ADMIN FUNCTIONS ////
  ///
  public static Instruction newDistributor(final AccountMeta invokedMerkleDistributorProgramMeta,
                                           final List<AccountMeta> keys,
                                           final NewDistributorParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = NEW_DISTRIBUTOR_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record NewDistributorIxData(Discriminator discriminator, NewDistributorParams params) implements Borsh {  

    public static NewDistributorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 179;

    public static NewDistributorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = NewDistributorParams.read(_data, i);
      return new NewDistributorIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLOSE_DISTRIBUTOR_DISCRIMINATOR = toDiscriminator(202, 56, 180, 143, 46, 104, 106, 112);

  /// only available in test phase
  ///
  /// @param distributorKey MerkleDistributor.
  /// @param tokenVaultKey Clawback receiver token account
  /// @param adminKey Admin wallet, responsible for creating the distributor and paying for the transaction.
  ///                 Also has the authority to set the clawback receiver and change itself.
  /// @param destinationTokenAccountKey account receive token back
  /// @param tokenProgramKey The Token program.
  public static List<AccountMeta> closeDistributorKeys(final PublicKey distributorKey,
                                                       final PublicKey tokenVaultKey,
                                                       final PublicKey adminKey,
                                                       final PublicKey destinationTokenAccountKey,
                                                       final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(tokenVaultKey),
      createWritableSigner(adminKey),
      createWrite(destinationTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  /// only available in test phase
  ///
  /// @param distributorKey MerkleDistributor.
  /// @param tokenVaultKey Clawback receiver token account
  /// @param adminKey Admin wallet, responsible for creating the distributor and paying for the transaction.
  ///                 Also has the authority to set the clawback receiver and change itself.
  /// @param destinationTokenAccountKey account receive token back
  /// @param tokenProgramKey The Token program.
  public static Instruction closeDistributor(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final PublicKey distributorKey,
                                             final PublicKey tokenVaultKey,
                                             final PublicKey adminKey,
                                             final PublicKey destinationTokenAccountKey,
                                             final PublicKey tokenProgramKey) {
    final var keys = closeDistributorKeys(
      distributorKey,
      tokenVaultKey,
      adminKey,
      destinationTokenAccountKey,
      tokenProgramKey
    );
    return closeDistributor(invokedMerkleDistributorProgramMeta, keys);
  }

  /// only available in test phase
  ///
  public static Instruction closeDistributor(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLOSE_DISTRIBUTOR_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_CLAIM_STATUS_DISCRIMINATOR = toDiscriminator(163, 214, 191, 165, 245, 188, 17, 185);

  /// only available in test phase
  ///
  public static List<AccountMeta> closeClaimStatusKeys(final PublicKey claimStatusKey,
                                                       final PublicKey claimantKey,
                                                       final PublicKey adminKey) {
    return List.of(
      createWrite(claimStatusKey),
      createWrite(claimantKey),
      createReadOnlySigner(adminKey)
    );
  }

  /// only available in test phase
  ///
  public static Instruction closeClaimStatus(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final PublicKey claimStatusKey,
                                             final PublicKey claimantKey,
                                             final PublicKey adminKey) {
    final var keys = closeClaimStatusKeys(
      claimStatusKey,
      claimantKey,
      adminKey
    );
    return closeClaimStatus(invokedMerkleDistributorProgramMeta, keys);
  }

  /// only available in test phase
  ///
  public static Instruction closeClaimStatus(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLOSE_CLAIM_STATUS_DISCRIMINATOR);
  }

  public static final Discriminator SET_ACTIVATION_POINT_DISCRIMINATOR = toDiscriminator(91, 249, 15, 165, 26, 129, 254, 125);

  /// @param distributorKey MerkleDistributor.
  /// @param adminKey Payer to create the distributor.
  public static List<AccountMeta> setActivationPointKeys(final PublicKey distributorKey,
                                                         final PublicKey adminKey) {
    return List.of(
      createWrite(distributorKey),
      createWritableSigner(adminKey)
    );
  }

  /// @param distributorKey MerkleDistributor.
  /// @param adminKey Payer to create the distributor.
  public static Instruction setActivationPoint(final AccountMeta invokedMerkleDistributorProgramMeta,
                                               final PublicKey distributorKey,
                                               final PublicKey adminKey,
                                               final long activationPoint) {
    final var keys = setActivationPointKeys(
      distributorKey,
      adminKey
    );
    return setActivationPoint(invokedMerkleDistributorProgramMeta, keys, activationPoint);
  }

  public static Instruction setActivationPoint(final AccountMeta invokedMerkleDistributorProgramMeta,
                                               final List<AccountMeta> keys,
                                               final long activationPoint) {
    final byte[] _data = new byte[16];
    int i = SET_ACTIVATION_POINT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, activationPoint);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record SetActivationPointIxData(Discriminator discriminator, long activationPoint) implements Borsh {  

    public static SetActivationPointIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetActivationPointIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var activationPoint = getInt64LE(_data, i);
      return new SetActivationPointIxData(discriminator, activationPoint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, activationPoint);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CLAWBACK_DISCRIMINATOR = toDiscriminator(111, 92, 142, 79, 33, 234, 82, 27);

  /// @param distributorKey The MerkleDistributor.
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param clawbackReceiverKey The Clawback token account.
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> clawbackKeys(final PublicKey distributorKey,
                                               final PublicKey fromKey,
                                               final PublicKey clawbackReceiverKey,
                                               final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(fromKey),
      createWrite(clawbackReceiverKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param clawbackReceiverKey The Clawback token account.
  /// @param tokenProgramKey SPL Token program.
  public static Instruction clawback(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final PublicKey distributorKey,
                                     final PublicKey fromKey,
                                     final PublicKey clawbackReceiverKey,
                                     final PublicKey tokenProgramKey) {
    final var keys = clawbackKeys(
      distributorKey,
      fromKey,
      clawbackReceiverKey,
      tokenProgramKey
    );
    return clawback(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction clawback(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLAWBACK_DISCRIMINATOR);
  }

  public static final Discriminator SET_CLAWBACK_RECEIVER_DISCRIMINATOR = toDiscriminator(153, 217, 34, 20, 19, 29, 229, 75);

  /// @param distributorKey The MerkleDistributor.
  /// @param newClawbackAccountKey New clawback account
  /// @param adminKey Admin signer
  public static List<AccountMeta> setClawbackReceiverKeys(final PublicKey distributorKey,
                                                          final PublicKey newClawbackAccountKey,
                                                          final PublicKey adminKey) {
    return List.of(
      createWrite(distributorKey),
      createRead(newClawbackAccountKey),
      createReadOnlySigner(adminKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param newClawbackAccountKey New clawback account
  /// @param adminKey Admin signer
  public static Instruction setClawbackReceiver(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                final PublicKey distributorKey,
                                                final PublicKey newClawbackAccountKey,
                                                final PublicKey adminKey) {
    final var keys = setClawbackReceiverKeys(
      distributorKey,
      newClawbackAccountKey,
      adminKey
    );
    return setClawbackReceiver(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction setClawbackReceiver(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, SET_CLAWBACK_RECEIVER_DISCRIMINATOR);
  }

  public static final Discriminator SET_ADMIN_DISCRIMINATOR = toDiscriminator(251, 163, 0, 52, 91, 194, 187, 92);

  /// @param distributorKey The MerkleDistributor.
  /// @param adminKey Admin signer
  /// @param newAdminKey New admin account
  public static List<AccountMeta> setAdminKeys(final PublicKey distributorKey,
                                               final PublicKey adminKey,
                                               final PublicKey newAdminKey) {
    return List.of(
      createWrite(distributorKey),
      createReadOnlySigner(adminKey),
      createRead(newAdminKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param adminKey Admin signer
  /// @param newAdminKey New admin account
  public static Instruction setAdmin(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final PublicKey distributorKey,
                                     final PublicKey adminKey,
                                     final PublicKey newAdminKey) {
    final var keys = setAdminKeys(
      distributorKey,
      adminKey,
      newAdminKey
    );
    return setAdmin(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction setAdmin(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, SET_ADMIN_DISCRIMINATOR);
  }

  public static final Discriminator SET_OPERATOR_DISCRIMINATOR = toDiscriminator(238, 153, 101, 169, 243, 131, 36, 1);

  /// @param distributorKey The MerkleDistributor.
  /// @param adminKey Admin signer
  public static List<AccountMeta> setOperatorKeys(final PublicKey distributorKey,
                                                  final PublicKey adminKey) {
    return List.of(
      createWrite(distributorKey),
      createReadOnlySigner(adminKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param adminKey Admin signer
  public static Instruction setOperator(final AccountMeta invokedMerkleDistributorProgramMeta,
                                        final PublicKey distributorKey,
                                        final PublicKey adminKey,
                                        final PublicKey newOperator) {
    final var keys = setOperatorKeys(
      distributorKey,
      adminKey
    );
    return setOperator(invokedMerkleDistributorProgramMeta, keys, newOperator);
  }

  public static Instruction setOperator(final AccountMeta invokedMerkleDistributorProgramMeta,
                                        final List<AccountMeta> keys,
                                        final PublicKey newOperator) {
    final byte[] _data = new byte[40];
    int i = SET_OPERATOR_DISCRIMINATOR.write(_data, 0);
    newOperator.write(_data, i);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record SetOperatorIxData(Discriminator discriminator, PublicKey newOperator) implements Borsh {  

    public static SetOperatorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetOperatorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newOperator = readPubKey(_data, i);
      return new SetOperatorIxData(discriminator, newOperator);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newOperator.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator NEW_CLAIM_DISCRIMINATOR = toDiscriminator(78, 177, 98, 123, 210, 21, 187, 83);

  /// USER FUNCTIONS /////
  ///
  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  /// @param systemProgramKey The System program.
  public static List<AccountMeta> newClaimKeys(final AccountMeta invokedMerkleDistributorProgramMeta,
                                               final PublicKey distributorKey,
                                               final PublicKey claimStatusKey,
                                               final PublicKey fromKey,
                                               final PublicKey toKey,
                                               final PublicKey claimantKey,
                                               final PublicKey operatorKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey systemProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(claimStatusKey),
      createWrite(fromKey),
      createWrite(toKey),
      createWritableSigner(claimantKey),
      operatorKey == null ? createRead(invokedMerkleDistributorProgramMeta.publicKey()) : createReadOnlySigner(operatorKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey)
    );
  }

  /// USER FUNCTIONS /////
  ///
  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  /// @param systemProgramKey The System program.
  public static Instruction newClaim(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final PublicKey distributorKey,
                                     final PublicKey claimStatusKey,
                                     final PublicKey fromKey,
                                     final PublicKey toKey,
                                     final PublicKey claimantKey,
                                     final PublicKey operatorKey,
                                     final PublicKey tokenProgramKey,
                                     final PublicKey systemProgramKey,
                                     final long amountUnlocked,
                                     final long amountLocked,
                                     final byte[][] proof) {
    final var keys = newClaimKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      claimStatusKey,
      fromKey,
      toKey,
      claimantKey,
      operatorKey,
      tokenProgramKey,
      systemProgramKey
    );
    return newClaim(
      invokedMerkleDistributorProgramMeta,
      keys,
      amountUnlocked,
      amountLocked,
      proof
    );
  }

  /// USER FUNCTIONS /////
  ///
  public static Instruction newClaim(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final List<AccountMeta> keys,
                                     final long amountUnlocked,
                                     final long amountLocked,
                                     final byte[][] proof) {
    final byte[] _data = new byte[24 + Borsh.lenVectorArray(proof)];
    int i = NEW_CLAIM_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amountUnlocked);
    i += 8;
    putInt64LE(_data, i, amountLocked);
    i += 8;
    Borsh.writeVectorArrayChecked(proof, 32, _data, i);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record NewClaimIxData(Discriminator discriminator,
                               long amountUnlocked,
                               long amountLocked,
                               byte[][] proof) implements Borsh {  

    public static NewClaimIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static NewClaimIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amountUnlocked = getInt64LE(_data, i);
      i += 8;
      final var amountLocked = getInt64LE(_data, i);
      i += 8;
      final var proof = Borsh.readMultiDimensionbyteVectorArray(32, _data, i);
      return new NewClaimIxData(discriminator, amountUnlocked, amountLocked, proof);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amountUnlocked);
      i += 8;
      putInt64LE(_data, i, amountLocked);
      i += 8;
      i += Borsh.writeVectorArrayChecked(proof, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 8 + Borsh.lenVectorArray(proof);
    }
  }

  public static final Discriminator CLAIM_LOCKED_DISCRIMINATOR = toDiscriminator(34, 206, 181, 23, 11, 207, 147, 90);

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim Status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> claimLockedKeys(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                  final PublicKey distributorKey,
                                                  final PublicKey claimStatusKey,
                                                  final PublicKey fromKey,
                                                  final PublicKey toKey,
                                                  final PublicKey claimantKey,
                                                  final PublicKey operatorKey,
                                                  final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(claimStatusKey),
      createWrite(fromKey),
      createWrite(toKey),
      createReadOnlySigner(claimantKey),
      operatorKey == null ? createRead(invokedMerkleDistributorProgramMeta.publicKey()) : createReadOnlySigner(operatorKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim Status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  public static Instruction claimLocked(final AccountMeta invokedMerkleDistributorProgramMeta,
                                        final PublicKey distributorKey,
                                        final PublicKey claimStatusKey,
                                        final PublicKey fromKey,
                                        final PublicKey toKey,
                                        final PublicKey claimantKey,
                                        final PublicKey operatorKey,
                                        final PublicKey tokenProgramKey) {
    final var keys = claimLockedKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      claimStatusKey,
      fromKey,
      toKey,
      claimantKey,
      operatorKey,
      tokenProgramKey
    );
    return claimLocked(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction claimLocked(final AccountMeta invokedMerkleDistributorProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLAIM_LOCKED_DISCRIMINATOR);
  }

  public static final Discriminator NEW_CLAIM_AND_STAKE_DISCRIMINATOR = toDiscriminator(50, 111, 242, 118, 51, 250, 141, 187);

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  /// @param systemProgramKey The System program.
  /// @param voterProgramKey Voter program
  public static List<AccountMeta> newClaimAndStakeKeys(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                       final PublicKey distributorKey,
                                                       final PublicKey claimStatusKey,
                                                       final PublicKey fromKey,
                                                       final PublicKey claimantKey,
                                                       final PublicKey operatorKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey voterProgramKey,
                                                       final PublicKey lockerKey,
                                                       final PublicKey escrowKey,
                                                       final PublicKey escrowTokensKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(claimStatusKey),
      createWrite(fromKey),
      createWritableSigner(claimantKey),
      operatorKey == null ? createRead(invokedMerkleDistributorProgramMeta.publicKey()) : createReadOnlySigner(operatorKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey),
      createRead(voterProgramKey),
      createWrite(lockerKey),
      createWrite(escrowKey),
      createWrite(escrowTokensKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  /// @param systemProgramKey The System program.
  /// @param voterProgramKey Voter program
  public static Instruction newClaimAndStake(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final PublicKey distributorKey,
                                             final PublicKey claimStatusKey,
                                             final PublicKey fromKey,
                                             final PublicKey claimantKey,
                                             final PublicKey operatorKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey voterProgramKey,
                                             final PublicKey lockerKey,
                                             final PublicKey escrowKey,
                                             final PublicKey escrowTokensKey,
                                             final long amountUnlocked,
                                             final long amountLocked,
                                             final byte[][] proof) {
    final var keys = newClaimAndStakeKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      claimStatusKey,
      fromKey,
      claimantKey,
      operatorKey,
      tokenProgramKey,
      systemProgramKey,
      voterProgramKey,
      lockerKey,
      escrowKey,
      escrowTokensKey
    );
    return newClaimAndStake(
      invokedMerkleDistributorProgramMeta,
      keys,
      amountUnlocked,
      amountLocked,
      proof
    );
  }

  public static Instruction newClaimAndStake(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final List<AccountMeta> keys,
                                             final long amountUnlocked,
                                             final long amountLocked,
                                             final byte[][] proof) {
    final byte[] _data = new byte[24 + Borsh.lenVectorArray(proof)];
    int i = NEW_CLAIM_AND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amountUnlocked);
    i += 8;
    putInt64LE(_data, i, amountLocked);
    i += 8;
    Borsh.writeVectorArrayChecked(proof, 32, _data, i);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record NewClaimAndStakeIxData(Discriminator discriminator,
                                       long amountUnlocked,
                                       long amountLocked,
                                       byte[][] proof) implements Borsh {  

    public static NewClaimAndStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static NewClaimAndStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amountUnlocked = getInt64LE(_data, i);
      i += 8;
      final var amountLocked = getInt64LE(_data, i);
      i += 8;
      final var proof = Borsh.readMultiDimensionbyteVectorArray(32, _data, i);
      return new NewClaimAndStakeIxData(discriminator, amountUnlocked, amountLocked, proof);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amountUnlocked);
      i += 8;
      putInt64LE(_data, i, amountLocked);
      i += 8;
      i += Borsh.writeVectorArrayChecked(proof, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + 8 + Borsh.lenVectorArray(proof);
    }
  }

  public static final Discriminator CLAIM_LOCKED_AND_STAKE_DISCRIMINATOR = toDiscriminator(173, 208, 81, 8, 13, 19, 202, 150);

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim Status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  /// @param voterProgramKey Voter program
  public static List<AccountMeta> claimLockedAndStakeKeys(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                          final PublicKey distributorKey,
                                                          final PublicKey claimStatusKey,
                                                          final PublicKey fromKey,
                                                          final PublicKey claimantKey,
                                                          final PublicKey operatorKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey voterProgramKey,
                                                          final PublicKey lockerKey,
                                                          final PublicKey escrowKey,
                                                          final PublicKey escrowTokensKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(claimStatusKey),
      createWrite(fromKey),
      createReadOnlySigner(claimantKey),
      operatorKey == null ? createRead(invokedMerkleDistributorProgramMeta.publicKey()) : createReadOnlySigner(operatorKey),
      createRead(tokenProgramKey),
      createRead(voterProgramKey),
      createWrite(lockerKey),
      createWrite(escrowKey),
      createWrite(escrowTokensKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim Status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param claimantKey Who is claiming the tokens.
  /// @param operatorKey operator
  /// @param tokenProgramKey SPL Token program.
  /// @param voterProgramKey Voter program
  public static Instruction claimLockedAndStake(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                final PublicKey distributorKey,
                                                final PublicKey claimStatusKey,
                                                final PublicKey fromKey,
                                                final PublicKey claimantKey,
                                                final PublicKey operatorKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey voterProgramKey,
                                                final PublicKey lockerKey,
                                                final PublicKey escrowKey,
                                                final PublicKey escrowTokensKey) {
    final var keys = claimLockedAndStakeKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      claimStatusKey,
      fromKey,
      claimantKey,
      operatorKey,
      tokenProgramKey,
      voterProgramKey,
      lockerKey,
      escrowKey,
      escrowTokensKey
    );
    return claimLockedAndStake(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction claimLockedAndStake(final AccountMeta invokedMerkleDistributorProgramMeta,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLAIM_LOCKED_AND_STAKE_DISCRIMINATOR);
  }

  private MerkleDistributorProgram() {
  }
}
