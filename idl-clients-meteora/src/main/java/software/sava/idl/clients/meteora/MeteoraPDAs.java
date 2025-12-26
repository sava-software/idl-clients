package software.sava.idl.clients.meteora;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.meteora.dlmm.gen.LbClmmPDAs;

import java.util.List;

public final class MeteoraPDAs {

  private static final byte[] ILM_BASE_KEY = PublicKey.fromBase58Encoded("MFGQxwAmB91SwuYX36okv2Qmdc9aMuHTwWGUrp4AtB1").toByteArray();

  public static ProgramDerivedAddress lbPairPDA(final PublicKey xMint,
                                                final PublicKey yMint,
                                                final int binStep,
                                                final int baseFactor,
                                                final PublicKey programId) {
    final PublicKey minKey;
    final PublicKey maxKey;
    if (xMint.compareTo(yMint) < 0) {
      minKey = xMint;
      maxKey = yMint;
    } else {
      minKey = yMint;
      maxKey = xMint;
    }

    final byte[] binStepBytes = new byte[Short.BYTES];
    ByteUtil.putInt16LE(binStepBytes, 0, binStep);
    final byte[] baseFactorBytes = new byte[Short.BYTES];
    ByteUtil.putInt16LE(baseFactorBytes, 0, baseFactor);

    return PublicKey.findProgramAddress(List.of(
            minKey.toByteArray(),
            maxKey.toByteArray(),
            binStepBytes,
            baseFactorBytes
        ), programId
    );
  }

  public static ProgramDerivedAddress customizablePermissionlessLbPairPDA(final PublicKey xMint,
                                                                          final PublicKey yMint,
                                                                          final PublicKey programId) {
    final PublicKey minKey;
    final PublicKey maxKey;
    if (xMint.compareTo(yMint) < 0) {
      minKey = xMint;
      maxKey = yMint;
    } else {
      minKey = yMint;
      maxKey = xMint;
    }

    return PublicKey.findProgramAddress(List.of(
            ILM_BASE_KEY,
            minKey.toByteArray(),
            maxKey.toByteArray()
        ), programId
    );
  }

  public static ProgramDerivedAddress permissionLbPairPDA(final PublicKey baseKey,
                                                          final PublicKey xMint,
                                                          final PublicKey yMint,
                                                          final int binStep,
                                                          final PublicKey programId) {
    final PublicKey minKey;
    final PublicKey maxKey;
    if (xMint.compareTo(yMint) < 0) {
      minKey = xMint;
      maxKey = yMint;
    } else {
      minKey = yMint;
      maxKey = xMint;
    }

    final byte[] binStepBytes = new byte[Short.BYTES];
    ByteUtil.putInt16LE(binStepBytes, 0, binStep);

    return PublicKey.findProgramAddress(List.of(
            baseKey.toByteArray(),
            minKey.toByteArray(),
            maxKey.toByteArray(),
            binStepBytes
        ), programId
    );
  }

  public static ProgramDerivedAddress positionPDA(final PublicKey lbPair,
                                                  final PublicKey baseKey,
                                                  final int lowerBinId,
                                                  final int width,
                                                  final PublicKey programId) {
    final byte[] lowerBinIdBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(lowerBinIdBytes, 0, lowerBinId);

    final byte[] widthBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(widthBytes, 0, width);

    return LbClmmPDAs.positionPDA(
        programId,
        lbPair,
        baseKey,
        lowerBinIdBytes,
        widthBytes
    );
  }

  public static ProgramDerivedAddress binArrayPdA(final PublicKey lbPair,
                                                  final int binArrayIndex,
                                                  final PublicKey programId) {
    final byte[] binArrayIndexBytes = new byte[Long.BYTES];
    ByteUtil.putInt64LE(binArrayIndexBytes, 0, binArrayIndex);
    return LbClmmPDAs.binArrayPDA(programId, lbPair, binArrayIndexBytes);
  }

  public static ProgramDerivedAddress reservePDA(final PublicKey lbPair,
                                                 final PublicKey tokenMint,
                                                 final PublicKey programId) {
    return PublicKey.findProgramAddress(List.of(
            lbPair.toByteArray(),
            tokenMint.toByteArray()
        ), programId
    );
  }

  public static ProgramDerivedAddress rewardVaultPDA(final PublicKey lbPair,
                                                     final long rewardIndex,
                                                     final PublicKey programId) {
    final byte[] rewardIndexBytes = new byte[Long.BYTES];
    ByteUtil.putInt64LE(rewardIndexBytes, 0, rewardIndex);
    return LbClmmPDAs.rewardVaultPDA(programId, lbPair, rewardIndexBytes);
  }

  public static ProgramDerivedAddress presetParameterPDA(final int ixIndex,
                                                         final PublicKey programId) {
    final byte[] ixIndexBytes = new byte[Short.BYTES];
    ByteUtil.putInt16LE(ixIndexBytes, 0, ixIndex);
    return LbClmmPDAs.presetParameterPDA(programId, ixIndexBytes);
  }

  private MeteoraPDAs() {
  }
}
