package software.sava.idl.clients.drift.merkle.distributor.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;

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

  /// READ THE FOLLOWING:
  /// 
  /// This instruction is susceptible to frontrunning that could result in loss of funds if not handled properly.
  /// 
  /// An attack could look like:
  /// - A legitimate user opens a new distributor.
  /// - Someone observes the call to this instruction.
  /// - They replace the clawback_receiver, admin, or time parameters with their own.
  /// 
  /// One situation that could happen here is the attacker replaces the admin and clawback_receiver with their own
  /// and sets the clawback_start_ts with the minimal time allowed. After clawback_start_ts has elapsed,
  /// the attacker can steal all funds from the distributor to their own clawback_receiver account.
  /// 
  /// HOW TO AVOID:
  /// - When you call into this instruction, ensure your transaction succeeds.
  /// - To be extra safe, after your transaction succeeds, read back the state of the created MerkleDistributor account and
  /// assert the parameters are what you expect, most importantly the clawback_receiver and admin.
  /// - If your transaction fails, double check the value on-chain matches what you expect.
  ///
  /// @param distributorKey MerkleDistributor.
  /// @param clawbackReceiverKey Clawback receiver token account
  /// @param mintKey The mint to distribute.
  /// @param tokenVaultKey Token vault
  ///                      Should create previously
  /// @param adminKey Admin wallet, responsible for creating the distributor and paying for the transaction.
  ///                 Also has the authority to set the clawback receiver and change itself.
  /// @param systemProgramKey The System program.
  /// @param associatedTokenProgramKey The Associated Token program.
  /// @param tokenProgramKey The Token program.
  public static List<AccountMeta> newDistributorKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                                     ,
                                                     final PublicKey distributorKey,
                                                     final PublicKey clawbackReceiverKey,
                                                     final PublicKey mintKey,
                                                     final PublicKey tokenVaultKey,
                                                     final PublicKey adminKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey associatedTokenProgramKey,
                                                     final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(clawbackReceiverKey),
      createRead(mintKey),
      createRead(tokenVaultKey),
      createWritableSigner(adminKey),
      createRead(systemProgramKey),
      createRead(associatedTokenProgramKey),
      createRead(tokenProgramKey)
    );
  }

  /// READ THE FOLLOWING:
  /// 
  /// This instruction is susceptible to frontrunning that could result in loss of funds if not handled properly.
  /// 
  /// An attack could look like:
  /// - A legitimate user opens a new distributor.
  /// - Someone observes the call to this instruction.
  /// - They replace the clawback_receiver, admin, or time parameters with their own.
  /// 
  /// One situation that could happen here is the attacker replaces the admin and clawback_receiver with their own
  /// and sets the clawback_start_ts with the minimal time allowed. After clawback_start_ts has elapsed,
  /// the attacker can steal all funds from the distributor to their own clawback_receiver account.
  /// 
  /// HOW TO AVOID:
  /// - When you call into this instruction, ensure your transaction succeeds.
  /// - To be extra safe, after your transaction succeeds, read back the state of the created MerkleDistributor account and
  /// assert the parameters are what you expect, most importantly the clawback_receiver and admin.
  /// - If your transaction fails, double check the value on-chain matches what you expect.
  ///
  /// @param distributorKey MerkleDistributor.
  /// @param clawbackReceiverKey Clawback receiver token account
  /// @param mintKey The mint to distribute.
  /// @param tokenVaultKey Token vault
  ///                      Should create previously
  /// @param adminKey Admin wallet, responsible for creating the distributor and paying for the transaction.
  ///                 Also has the authority to set the clawback receiver and change itself.
  /// @param systemProgramKey The System program.
  /// @param associatedTokenProgramKey The Associated Token program.
  /// @param tokenProgramKey The Token program.
  public static Instruction newDistributor(final AccountMeta invokedMerkleDistributorProgramMeta,
                                           final PublicKey distributorKey,
                                           final PublicKey clawbackReceiverKey,
                                           final PublicKey mintKey,
                                           final PublicKey tokenVaultKey,
                                           final PublicKey adminKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey associatedTokenProgramKey,
                                           final PublicKey tokenProgramKey,
                                           final long version,
                                           final byte[] root,
                                           final long maxTotalClaim,
                                           final long maxNumNodes,
                                           final long startVestingTs,
                                           final long endVestingTs,
                                           final long clawbackStartTs,
                                           final long enableSlot,
                                           final boolean closable) {
    final var keys = newDistributorKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      clawbackReceiverKey,
      mintKey,
      tokenVaultKey,
      adminKey,
      systemProgramKey,
      associatedTokenProgramKey,
      tokenProgramKey
    );
    return newDistributor(
      invokedMerkleDistributorProgramMeta,
      keys,
      version,
      root,
      maxTotalClaim,
      maxNumNodes,
      startVestingTs,
      endVestingTs,
      clawbackStartTs,
      enableSlot,
      closable
    );
  }

  /// READ THE FOLLOWING:
  /// 
  /// This instruction is susceptible to frontrunning that could result in loss of funds if not handled properly.
  /// 
  /// An attack could look like:
  /// - A legitimate user opens a new distributor.
  /// - Someone observes the call to this instruction.
  /// - They replace the clawback_receiver, admin, or time parameters with their own.
  /// 
  /// One situation that could happen here is the attacker replaces the admin and clawback_receiver with their own
  /// and sets the clawback_start_ts with the minimal time allowed. After clawback_start_ts has elapsed,
  /// the attacker can steal all funds from the distributor to their own clawback_receiver account.
  /// 
  /// HOW TO AVOID:
  /// - When you call into this instruction, ensure your transaction succeeds.
  /// - To be extra safe, after your transaction succeeds, read back the state of the created MerkleDistributor account and
  /// assert the parameters are what you expect, most importantly the clawback_receiver and admin.
  /// - If your transaction fails, double check the value on-chain matches what you expect.
  ///
  public static Instruction newDistributor(final AccountMeta invokedMerkleDistributorProgramMeta                                           ,
                                           final List<AccountMeta> keys,
                                           final long version,
                                           final byte[] root,
                                           final long maxTotalClaim,
                                           final long maxNumNodes,
                                           final long startVestingTs,
                                           final long endVestingTs,
                                           final long clawbackStartTs,
                                           final long enableSlot,
                                           final boolean closable) {
    final byte[] _data = new byte[65 + Borsh.lenArray(root)];
    int i = NEW_DISTRIBUTOR_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, version);
    i += 8;
    i += Borsh.writeArrayChecked(root, 32, _data, i);
    putInt64LE(_data, i, maxTotalClaim);
    i += 8;
    putInt64LE(_data, i, maxNumNodes);
    i += 8;
    putInt64LE(_data, i, startVestingTs);
    i += 8;
    putInt64LE(_data, i, endVestingTs);
    i += 8;
    putInt64LE(_data, i, clawbackStartTs);
    i += 8;
    putInt64LE(_data, i, enableSlot);
    i += 8;
    _data[i] = (byte) (closable ? 1 : 0);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record NewDistributorIxData(Discriminator discriminator,
                                     long version,
                                     byte[] root,
                                     long maxTotalClaim,
                                     long maxNumNodes,
                                     long startVestingTs,
                                     long endVestingTs,
                                     long clawbackStartTs,
                                     long enableSlot,
                                     boolean closable) implements Borsh {  

    public static NewDistributorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 97;
    public static final int ROOT_LEN = 32;

    public static NewDistributorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var version = getInt64LE(_data, i);
      i += 8;
      final var root = new byte[32];
      i += Borsh.readArray(root, _data, i);
      final var maxTotalClaim = getInt64LE(_data, i);
      i += 8;
      final var maxNumNodes = getInt64LE(_data, i);
      i += 8;
      final var startVestingTs = getInt64LE(_data, i);
      i += 8;
      final var endVestingTs = getInt64LE(_data, i);
      i += 8;
      final var clawbackStartTs = getInt64LE(_data, i);
      i += 8;
      final var enableSlot = getInt64LE(_data, i);
      i += 8;
      final var closable = _data[i] == 1;
      return new NewDistributorIxData(discriminator,
                                      version,
                                      root,
                                      maxTotalClaim,
                                      maxNumNodes,
                                      startVestingTs,
                                      endVestingTs,
                                      clawbackStartTs,
                                      enableSlot,
                                      closable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, version);
      i += 8;
      i += Borsh.writeArrayChecked(root, 32, _data, i);
      putInt64LE(_data, i, maxTotalClaim);
      i += 8;
      putInt64LE(_data, i, maxNumNodes);
      i += 8;
      putInt64LE(_data, i, startVestingTs);
      i += 8;
      putInt64LE(_data, i, endVestingTs);
      i += 8;
      putInt64LE(_data, i, clawbackStartTs);
      i += 8;
      putInt64LE(_data, i, enableSlot);
      i += 8;
      _data[i] = (byte) (closable ? 1 : 0);
      ++i;
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
  public static List<AccountMeta> closeDistributorKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                                       ,
                                                       final PublicKey distributorKey,
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
      invokedMerkleDistributorProgramMeta,
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
  public static Instruction closeDistributor(final AccountMeta invokedMerkleDistributorProgramMeta                                             ,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLOSE_DISTRIBUTOR_DISCRIMINATOR);
  }

  public static final Discriminator CLOSE_CLAIM_STATUS_DISCRIMINATOR = toDiscriminator(163, 214, 191, 165, 245, 188, 17, 185);

  /// only available in test phase
  ///
  public static List<AccountMeta> closeClaimStatusKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                                       ,
                                                       final PublicKey claimStatusKey,
                                                       final PublicKey claimantKey,
                                                       final PublicKey adminKey,
                                                       final PublicKey distributorKey) {
    return List.of(
      createWrite(claimStatusKey),
      createWrite(claimantKey),
      createReadOnlySigner(adminKey),
      createRead(distributorKey)
    );
  }

  /// only available in test phase
  ///
  public static Instruction closeClaimStatus(final AccountMeta invokedMerkleDistributorProgramMeta,
                                             final PublicKey claimStatusKey,
                                             final PublicKey claimantKey,
                                             final PublicKey adminKey,
                                             final PublicKey distributorKey) {
    final var keys = closeClaimStatusKeys(
      invokedMerkleDistributorProgramMeta,
      claimStatusKey,
      claimantKey,
      adminKey,
      distributorKey
    );
    return closeClaimStatus(invokedMerkleDistributorProgramMeta, keys);
  }

  /// only available in test phase
  ///
  public static Instruction closeClaimStatus(final AccountMeta invokedMerkleDistributorProgramMeta                                             ,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLOSE_CLAIM_STATUS_DISCRIMINATOR);
  }

  public static final Discriminator SET_ENABLE_SLOT_DISCRIMINATOR = toDiscriminator(5, 52, 73, 33, 150, 115, 97, 206);

  /// @param distributorKey MerkleDistributor.
  /// @param adminKey Payer to create the distributor.
  public static List<AccountMeta> setEnableSlotKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                                    ,
                                                    final PublicKey distributorKey,
                                                    final PublicKey adminKey) {
    return List.of(
      createWrite(distributorKey),
      createWritableSigner(adminKey)
    );
  }

  /// @param distributorKey MerkleDistributor.
  /// @param adminKey Payer to create the distributor.
  public static Instruction setEnableSlot(final AccountMeta invokedMerkleDistributorProgramMeta,
                                          final PublicKey distributorKey,
                                          final PublicKey adminKey,
                                          final long enableSlot) {
    final var keys = setEnableSlotKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      adminKey
    );
    return setEnableSlot(invokedMerkleDistributorProgramMeta, keys, enableSlot);
  }

  public static Instruction setEnableSlot(final AccountMeta invokedMerkleDistributorProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final long enableSlot) {
    final byte[] _data = new byte[16];
    int i = SET_ENABLE_SLOT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, enableSlot);

    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, _data);
  }

  public record SetEnableSlotIxData(Discriminator discriminator, long enableSlot) implements Borsh {  

    public static SetEnableSlotIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetEnableSlotIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enableSlot = getInt64LE(_data, i);
      return new SetEnableSlotIxData(discriminator, enableSlot);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, enableSlot);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator NEW_CLAIM_DISCRIMINATOR = toDiscriminator(78, 177, 98, 123, 210, 21, 187, 83);

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  /// @param claimantKey Who is claiming the tokens.
  /// @param tokenProgramKey SPL Token program.
  /// @param systemProgramKey The System program.
  public static List<AccountMeta> newClaimKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                               ,
                                               final PublicKey distributorKey,
                                               final PublicKey claimStatusKey,
                                               final PublicKey fromKey,
                                               final PublicKey toKey,
                                               final PublicKey claimantKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey systemProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(claimStatusKey),
      createWrite(fromKey),
      createWrite(toKey),
      createWritableSigner(claimantKey),
      createRead(tokenProgramKey),
      createRead(systemProgramKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  /// @param claimantKey Who is claiming the tokens.
  /// @param tokenProgramKey SPL Token program.
  /// @param systemProgramKey The System program.
  public static Instruction newClaim(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final PublicKey distributorKey,
                                     final PublicKey claimStatusKey,
                                     final PublicKey fromKey,
                                     final PublicKey toKey,
                                     final PublicKey claimantKey,
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

  public static Instruction newClaim(final AccountMeta invokedMerkleDistributorProgramMeta                                     ,
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
  ///              Claimant must sign the transaction and can only claim on behalf of themself
  /// @param claimantKey Who is claiming the tokens.
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> claimLockedKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                                  ,
                                                  final PublicKey distributorKey,
                                                  final PublicKey claimStatusKey,
                                                  final PublicKey fromKey,
                                                  final PublicKey toKey,
                                                  final PublicKey claimantKey,
                                                  final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(claimStatusKey),
      createWrite(fromKey),
      createWrite(toKey),
      createWritableSigner(claimantKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param claimStatusKey Claim Status PDA
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey Account to send the claimed tokens to.
  ///              Claimant must sign the transaction and can only claim on behalf of themself
  /// @param claimantKey Who is claiming the tokens.
  /// @param tokenProgramKey SPL Token program.
  public static Instruction claimLocked(final AccountMeta invokedMerkleDistributorProgramMeta,
                                        final PublicKey distributorKey,
                                        final PublicKey claimStatusKey,
                                        final PublicKey fromKey,
                                        final PublicKey toKey,
                                        final PublicKey claimantKey,
                                        final PublicKey tokenProgramKey) {
    final var keys = claimLockedKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      claimStatusKey,
      fromKey,
      toKey,
      claimantKey,
      tokenProgramKey
    );
    return claimLocked(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction claimLocked(final AccountMeta invokedMerkleDistributorProgramMeta                                        ,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLAIM_LOCKED_DISCRIMINATOR);
  }

  public static final Discriminator CLAWBACK_DISCRIMINATOR = toDiscriminator(111, 92, 142, 79, 33, 234, 82, 27);

  /// @param distributorKey The MerkleDistributor.
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey The Clawback token account.
  /// @param claimantKey Claimant account
  ///                    Anyone can claw back the funds
  /// @param systemProgramKey The System program.
  /// @param tokenProgramKey SPL Token program.
  public static List<AccountMeta> clawbackKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                               ,
                                               final PublicKey distributorKey,
                                               final PublicKey fromKey,
                                               final PublicKey toKey,
                                               final PublicKey claimantKey,
                                               final PublicKey systemProgramKey,
                                               final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(distributorKey),
      createWrite(fromKey),
      createWrite(toKey),
      createReadOnlySigner(claimantKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param distributorKey The MerkleDistributor.
  /// @param fromKey Distributor ATA containing the tokens to distribute.
  /// @param toKey The Clawback token account.
  /// @param claimantKey Claimant account
  ///                    Anyone can claw back the funds
  /// @param systemProgramKey The System program.
  /// @param tokenProgramKey SPL Token program.
  public static Instruction clawback(final AccountMeta invokedMerkleDistributorProgramMeta,
                                     final PublicKey distributorKey,
                                     final PublicKey fromKey,
                                     final PublicKey toKey,
                                     final PublicKey claimantKey,
                                     final PublicKey systemProgramKey,
                                     final PublicKey tokenProgramKey) {
    final var keys = clawbackKeys(
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      fromKey,
      toKey,
      claimantKey,
      systemProgramKey,
      tokenProgramKey
    );
    return clawback(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction clawback(final AccountMeta invokedMerkleDistributorProgramMeta                                     ,
                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, CLAWBACK_DISCRIMINATOR);
  }

  public static final Discriminator SET_CLAWBACK_RECEIVER_DISCRIMINATOR = toDiscriminator(153, 217, 34, 20, 19, 29, 229, 75);

  /// @param distributorKey The MerkleDistributor.
  /// @param newClawbackAccountKey New clawback account
  /// @param adminKey Admin signer
  public static List<AccountMeta> setClawbackReceiverKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                                          ,
                                                          final PublicKey distributorKey,
                                                          final PublicKey newClawbackAccountKey,
                                                          final PublicKey adminKey) {
    return List.of(
      createWrite(distributorKey),
      createRead(newClawbackAccountKey),
      createWritableSigner(adminKey)
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
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      newClawbackAccountKey,
      adminKey
    );
    return setClawbackReceiver(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction setClawbackReceiver(final AccountMeta invokedMerkleDistributorProgramMeta                                                ,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, SET_CLAWBACK_RECEIVER_DISCRIMINATOR);
  }

  public static final Discriminator SET_ADMIN_DISCRIMINATOR = toDiscriminator(251, 163, 0, 52, 91, 194, 187, 92);

  /// @param distributorKey The MerkleDistributor.
  /// @param adminKey Admin signer
  /// @param newAdminKey New admin account
  public static List<AccountMeta> setAdminKeys(final AccountMeta invokedMerkleDistributorProgramMeta                                               ,
                                               final PublicKey distributorKey,
                                               final PublicKey adminKey,
                                               final PublicKey newAdminKey) {
    return List.of(
      createWrite(distributorKey),
      createWritableSigner(adminKey),
      createWrite(newAdminKey)
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
      invokedMerkleDistributorProgramMeta,
      distributorKey,
      adminKey,
      newAdminKey
    );
    return setAdmin(invokedMerkleDistributorProgramMeta, keys);
  }

  public static Instruction setAdmin(final AccountMeta invokedMerkleDistributorProgramMeta                                     ,
                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMerkleDistributorProgramMeta, keys, SET_ADMIN_DISCRIMINATOR);
  }

  private MerkleDistributorProgram() {
  }
}
