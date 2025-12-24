package software.sava.idl.clients.oracles.pyth.push.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.oracles.pyth.push.gen.types.PostUpdateParams;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class PythPushOracleProgram {

  public static final Discriminator UPDATE_PRICE_FEED_DISCRIMINATOR = toDiscriminator(28, 9, 93, 150, 86, 153, 188, 115);

  public static List<AccountMeta> updatePriceFeedKeys(final PublicKey payerKey,
                                                      final PublicKey pythSolanaReceiverKey,
                                                      final PublicKey encodedVaaKey,
                                                      final PublicKey configKey,
                                                      final PublicKey treasuryKey,
                                                      final PublicKey priceFeedAccountKey,
                                                      final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createRead(pythSolanaReceiverKey),
      createRead(encodedVaaKey),
      createRead(configKey),
      createWrite(treasuryKey),
      createWrite(priceFeedAccountKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction updatePriceFeed(final AccountMeta invokedPythPushOracleProgramMeta,
                                            final PublicKey payerKey,
                                            final PublicKey pythSolanaReceiverKey,
                                            final PublicKey encodedVaaKey,
                                            final PublicKey configKey,
                                            final PublicKey treasuryKey,
                                            final PublicKey priceFeedAccountKey,
                                            final PublicKey systemProgramKey,
                                            final PostUpdateParams params,
                                            final int shardId,
                                            final byte[] feedId) {
    final var keys = updatePriceFeedKeys(
      payerKey,
      pythSolanaReceiverKey,
      encodedVaaKey,
      configKey,
      treasuryKey,
      priceFeedAccountKey,
      systemProgramKey
    );
    return updatePriceFeed(
      invokedPythPushOracleProgramMeta,
      keys,
      params,
      shardId,
      feedId
    );
  }

  public static Instruction updatePriceFeed(final AccountMeta invokedPythPushOracleProgramMeta,
                                            final List<AccountMeta> keys,
                                            final PostUpdateParams params,
                                            final int shardId,
                                            final byte[] feedId) {
    final byte[] _data = new byte[10 + params.l() + SerDeUtil.lenArray(feedId)];
    int i = UPDATE_PRICE_FEED_DISCRIMINATOR.write(_data, 0);
    i += params.write(_data, i);
    putInt16LE(_data, i, shardId);
    i += 2;
    SerDeUtil.writeArrayChecked(feedId, 32, _data, i);

    return Instruction.createInstruction(invokedPythPushOracleProgramMeta, keys, _data);
  }

  public record UpdatePriceFeedIxData(Discriminator discriminator,
                                      PostUpdateParams params,
                                      int shardId,
                                      byte[] feedId) implements SerDe {  

    public static UpdatePriceFeedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int FEED_ID_LEN = 32;
    public static UpdatePriceFeedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PostUpdateParams.read(_data, i);
      i += params.l();
      final var shardId = getInt16LE(_data, i);
      i += 2;
      final var feedId = new byte[32];
      SerDeUtil.readArray(feedId, _data, i);
      return new UpdatePriceFeedIxData(discriminator, params, shardId, feedId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      putInt16LE(_data, i, shardId);
      i += 2;
      i += SerDeUtil.writeArrayChecked(feedId, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l() + 2 + SerDeUtil.lenArray(feedId);
    }
  }

  private PythPushOracleProgram() {
  }
}
