package software.sava.idl.clients.jupiter.order_engine.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;

import static java.util.Objects.requireNonNullElse;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class OrderEngineProgram {

  public static final Discriminator FILL_DISCRIMINATOR = toDiscriminator(168, 96, 183, 163, 92, 10, 40, 160);

  public static List<AccountMeta> fillKeys(final AccountMeta invokedOrderEngineProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey takerKey,
                                           final PublicKey makerKey,
                                           final PublicKey takerInputMintTokenAccountKey,
                                           final PublicKey makerInputMintTokenAccountKey,
                                           final PublicKey takerOutputMintTokenAccountKey,
                                           final PublicKey makerOutputMintTokenAccountKey,
                                           final PublicKey inputMintKey,
                                           final PublicKey inputTokenProgramKey,
                                           final PublicKey outputMintKey,
                                           final PublicKey outputTokenProgramKey) {
    return List.of(
      createWritableSigner(takerKey),
      createWritableSigner(makerKey),
      createWrite(requireNonNullElse(takerInputMintTokenAccountKey, invokedOrderEngineProgramMeta.publicKey())),
      createWrite(requireNonNullElse(makerInputMintTokenAccountKey, invokedOrderEngineProgramMeta.publicKey())),
      createWrite(requireNonNullElse(takerOutputMintTokenAccountKey, invokedOrderEngineProgramMeta.publicKey())),
      createWrite(requireNonNullElse(makerOutputMintTokenAccountKey, invokedOrderEngineProgramMeta.publicKey())),
      createRead(inputMintKey),
      createRead(inputTokenProgramKey),
      createRead(outputMintKey),
      createRead(outputTokenProgramKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction fill(final AccountMeta invokedOrderEngineProgramMeta,
                                 final SolanaAccounts solanaAccounts,
                                 final PublicKey takerKey,
                                 final PublicKey makerKey,
                                 final PublicKey takerInputMintTokenAccountKey,
                                 final PublicKey makerInputMintTokenAccountKey,
                                 final PublicKey takerOutputMintTokenAccountKey,
                                 final PublicKey makerOutputMintTokenAccountKey,
                                 final PublicKey inputMintKey,
                                 final PublicKey inputTokenProgramKey,
                                 final PublicKey outputMintKey,
                                 final PublicKey outputTokenProgramKey,
                                 final long inputAmount,
                                 final long outputAmount,
                                 final long expireAt) {
    final var keys = fillKeys(
      invokedOrderEngineProgramMeta,
      solanaAccounts,
      takerKey,
      makerKey,
      takerInputMintTokenAccountKey,
      makerInputMintTokenAccountKey,
      takerOutputMintTokenAccountKey,
      makerOutputMintTokenAccountKey,
      inputMintKey,
      inputTokenProgramKey,
      outputMintKey,
      outputTokenProgramKey
    );
    return fill(
      invokedOrderEngineProgramMeta,
      keys,
      inputAmount,
      outputAmount,
      expireAt
    );
  }

  public static Instruction fill(final AccountMeta invokedOrderEngineProgramMeta,
                                 final List<AccountMeta> keys,
                                 final long inputAmount,
                                 final long outputAmount,
                                 final long expireAt) {
    final byte[] _data = new byte[32];
    int i = FILL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, inputAmount);
    i += 8;
    putInt64LE(_data, i, outputAmount);
    i += 8;
    putInt64LE(_data, i, expireAt);

    return Instruction.createInstruction(invokedOrderEngineProgramMeta, keys, _data);
  }

  public record FillIxData(Discriminator discriminator,
                           long inputAmount,
                           long outputAmount,
                           long expireAt) implements Borsh {  

    public static FillIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 32;

    public static FillIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inputAmount = getInt64LE(_data, i);
      i += 8;
      final var outputAmount = getInt64LE(_data, i);
      i += 8;
      final var expireAt = getInt64LE(_data, i);
      return new FillIxData(discriminator, inputAmount, outputAmount, expireAt);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, inputAmount);
      i += 8;
      putInt64LE(_data, i, outputAmount);
      i += 8;
      putInt64LE(_data, i, expireAt);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  private OrderEngineProgram() {
  }
}
