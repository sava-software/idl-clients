package software.sava.idl.clients.oracles.pyth.receiver.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.oracles.pyth.push.gen.types.PostUpdateParams;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.Config;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.DataSource;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.PostTwapUpdateParams;
import software.sava.idl.clients.oracles.pyth.receiver.gen.types.PostUpdateAtomicParams;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class PythSolanaReceiverProgram {

  public static final Discriminator INITIALIZE_DISCRIMINATOR = toDiscriminator(175, 175, 109, 31, 13, 152, 155, 237);

  public static List<AccountMeta> initializeKeys(final PublicKey payerKey,
                                                 final PublicKey configKey,
                                                 final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(configKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initialize(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                       final PublicKey payerKey,
                                       final PublicKey configKey,
                                       final PublicKey systemProgramKey,
                                       final Config initialConfig) {
    final var keys = initializeKeys(
      payerKey,
      configKey,
      systemProgramKey
    );
    return initialize(invokedPythSolanaReceiverProgramMeta, keys, initialConfig);
  }

  public static Instruction initialize(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                       final List<AccountMeta> keys,
                                       final Config initialConfig) {
    final byte[] _data = new byte[8 + initialConfig.l()];
    int i = INITIALIZE_DISCRIMINATOR.write(_data, 0);
    initialConfig.write(_data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record InitializeIxData(Discriminator discriminator, Config initialConfig) implements Borsh {  

    public static InitializeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var initialConfig = Config.read(_data, i);
      return new InitializeIxData(discriminator, initialConfig);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += initialConfig.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + initialConfig.l();
    }
  }

  public static final Discriminator REQUEST_GOVERNANCE_AUTHORITY_TRANSFER_DISCRIMINATOR = toDiscriminator(92, 18, 67, 156, 27, 151, 183, 224);

  public static List<AccountMeta> requestGovernanceAuthorityTransferKeys(final PublicKey payerKey,
                                                                         final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction requestGovernanceAuthorityTransfer(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                               final PublicKey payerKey,
                                                               final PublicKey configKey,
                                                               final PublicKey targetGovernanceAuthority) {
    final var keys = requestGovernanceAuthorityTransferKeys(
      payerKey,
      configKey
    );
    return requestGovernanceAuthorityTransfer(invokedPythSolanaReceiverProgramMeta, keys, targetGovernanceAuthority);
  }

  public static Instruction requestGovernanceAuthorityTransfer(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                               final List<AccountMeta> keys,
                                                               final PublicKey targetGovernanceAuthority) {
    final byte[] _data = new byte[40];
    int i = REQUEST_GOVERNANCE_AUTHORITY_TRANSFER_DISCRIMINATOR.write(_data, 0);
    targetGovernanceAuthority.write(_data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record RequestGovernanceAuthorityTransferIxData(Discriminator discriminator, PublicKey targetGovernanceAuthority) implements Borsh {  

    public static RequestGovernanceAuthorityTransferIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static RequestGovernanceAuthorityTransferIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var targetGovernanceAuthority = readPubKey(_data, i);
      return new RequestGovernanceAuthorityTransferIxData(discriminator, targetGovernanceAuthority);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      targetGovernanceAuthority.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CANCEL_GOVERNANCE_AUTHORITY_TRANSFER_DISCRIMINATOR = toDiscriminator(39, 93, 70, 137, 137, 90, 248, 154);

  public static List<AccountMeta> cancelGovernanceAuthorityTransferKeys(final PublicKey payerKey,
                                                                        final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction cancelGovernanceAuthorityTransfer(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                              final PublicKey payerKey,
                                                              final PublicKey configKey) {
    final var keys = cancelGovernanceAuthorityTransferKeys(
      payerKey,
      configKey
    );
    return cancelGovernanceAuthorityTransfer(invokedPythSolanaReceiverProgramMeta, keys);
  }

  public static Instruction cancelGovernanceAuthorityTransfer(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, CANCEL_GOVERNANCE_AUTHORITY_TRANSFER_DISCRIMINATOR);
  }

  public static final Discriminator ACCEPT_GOVERNANCE_AUTHORITY_TRANSFER_DISCRIMINATOR = toDiscriminator(254, 39, 222, 79, 64, 217, 205, 127);

  public static List<AccountMeta> acceptGovernanceAuthorityTransferKeys(final PublicKey payerKey,
                                                                        final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction acceptGovernanceAuthorityTransfer(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                              final PublicKey payerKey,
                                                              final PublicKey configKey) {
    final var keys = acceptGovernanceAuthorityTransferKeys(
      payerKey,
      configKey
    );
    return acceptGovernanceAuthorityTransfer(invokedPythSolanaReceiverProgramMeta, keys);
  }

  public static Instruction acceptGovernanceAuthorityTransfer(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, ACCEPT_GOVERNANCE_AUTHORITY_TRANSFER_DISCRIMINATOR);
  }

  public static final Discriminator SET_DATA_SOURCES_DISCRIMINATOR = toDiscriminator(107, 73, 15, 119, 195, 116, 91, 210);

  public static List<AccountMeta> setDataSourcesKeys(final PublicKey payerKey,
                                                     final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction setDataSources(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                           final PublicKey payerKey,
                                           final PublicKey configKey,
                                           final DataSource[] validDataSources) {
    final var keys = setDataSourcesKeys(
      payerKey,
      configKey
    );
    return setDataSources(invokedPythSolanaReceiverProgramMeta, keys, validDataSources);
  }

  public static Instruction setDataSources(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                           final List<AccountMeta> keys,
                                           final DataSource[] validDataSources) {
    final byte[] _data = new byte[8 + Borsh.lenVector(validDataSources)];
    int i = SET_DATA_SOURCES_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(validDataSources, _data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record SetDataSourcesIxData(Discriminator discriminator, DataSource[] validDataSources) implements Borsh {  

    public static SetDataSourcesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SetDataSourcesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var validDataSources = Borsh.readVector(DataSource.class, DataSource::read, _data, i);
      return new SetDataSourcesIxData(discriminator, validDataSources);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(validDataSources, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(validDataSources);
    }
  }

  public static final Discriminator SET_FEE_DISCRIMINATOR = toDiscriminator(18, 154, 24, 18, 237, 214, 19, 80);

  public static List<AccountMeta> setFeeKeys(final PublicKey payerKey,
                                             final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction setFee(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                   final PublicKey payerKey,
                                   final PublicKey configKey,
                                   final long singleUpdateFeeInLamports) {
    final var keys = setFeeKeys(
      payerKey,
      configKey
    );
    return setFee(invokedPythSolanaReceiverProgramMeta, keys, singleUpdateFeeInLamports);
  }

  public static Instruction setFee(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                   final List<AccountMeta> keys,
                                   final long singleUpdateFeeInLamports) {
    final byte[] _data = new byte[16];
    int i = SET_FEE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, singleUpdateFeeInLamports);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record SetFeeIxData(Discriminator discriminator, long singleUpdateFeeInLamports) implements Borsh {  

    public static SetFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var singleUpdateFeeInLamports = getInt64LE(_data, i);
      return new SetFeeIxData(discriminator, singleUpdateFeeInLamports);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, singleUpdateFeeInLamports);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_WORMHOLE_ADDRESS_DISCRIMINATOR = toDiscriminator(154, 174, 252, 157, 91, 215, 179, 156);

  public static List<AccountMeta> setWormholeAddressKeys(final PublicKey payerKey,
                                                         final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction setWormholeAddress(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                               final PublicKey payerKey,
                                               final PublicKey configKey,
                                               final PublicKey wormhole) {
    final var keys = setWormholeAddressKeys(
      payerKey,
      configKey
    );
    return setWormholeAddress(invokedPythSolanaReceiverProgramMeta, keys, wormhole);
  }

  public static Instruction setWormholeAddress(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                               final List<AccountMeta> keys,
                                               final PublicKey wormhole) {
    final byte[] _data = new byte[40];
    int i = SET_WORMHOLE_ADDRESS_DISCRIMINATOR.write(_data, 0);
    wormhole.write(_data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record SetWormholeAddressIxData(Discriminator discriminator, PublicKey wormhole) implements Borsh {  

    public static SetWormholeAddressIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static SetWormholeAddressIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var wormhole = readPubKey(_data, i);
      return new SetWormholeAddressIxData(discriminator, wormhole);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      wormhole.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SET_MINIMUM_SIGNATURES_DISCRIMINATOR = toDiscriminator(5, 210, 206, 124, 43, 68, 104, 149);

  public static List<AccountMeta> setMinimumSignaturesKeys(final PublicKey payerKey,
                                                           final PublicKey configKey) {
    return List.of(
      createReadOnlySigner(payerKey),
      createWrite(configKey)
    );
  }

  public static Instruction setMinimumSignatures(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                 final PublicKey payerKey,
                                                 final PublicKey configKey,
                                                 final int minimumSignatures) {
    final var keys = setMinimumSignaturesKeys(
      payerKey,
      configKey
    );
    return setMinimumSignatures(invokedPythSolanaReceiverProgramMeta, keys, minimumSignatures);
  }

  public static Instruction setMinimumSignatures(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                                 final List<AccountMeta> keys,
                                                 final int minimumSignatures) {
    final byte[] _data = new byte[9];
    int i = SET_MINIMUM_SIGNATURES_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) minimumSignatures;

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record SetMinimumSignaturesIxData(Discriminator discriminator, int minimumSignatures) implements Borsh {  

    public static SetMinimumSignaturesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static SetMinimumSignaturesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var minimumSignatures = _data[i] & 0xFF;
      return new SetMinimumSignaturesIxData(discriminator, minimumSignatures);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) minimumSignatures;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator POST_UPDATE_ATOMIC_DISCRIMINATOR = toDiscriminator(49, 172, 84, 192, 175, 180, 52, 234);

  /// Post a price update using a VAA and a MerklePriceUpdate.
  /// This function allows you to post a price update in a single transaction.
  /// Compared to `post_update`, it only checks whatever signatures are present in the provided VAA and doesn't fail if the number of signatures is lower than the Wormhole quorum of two thirds of the guardians.
  /// The number of signatures that were in the VAA is stored in the `VerificationLevel` of the `PriceUpdateV2` account.
  /// 
  /// We recommend using `post_update_atomic` with 5 signatures. This is close to the maximum signatures you can verify in one transaction without exceeding the transaction size limit.
  /// 
  /// # Warning
  /// 
  /// Using partially verified price updates is dangerous, as it lowers the threshold of guardians that need to collude to produce a malicious price update.
  ///
  /// @param guardianSetKey Instead we do the same steps in deserialize_guardian_set_checked.
  /// @param priceUpdateAccountKey The constraint is such that either the price_update_account is uninitialized or the write_authority is the write_authority.
  ///                              Pubkey::default() is the SystemProgram on Solana and it can't sign so it's impossible that price_update_account.write_authority == Pubkey::default() once the account is initialized
  public static List<AccountMeta> postUpdateAtomicKeys(final PublicKey payerKey,
                                                       final PublicKey guardianSetKey,
                                                       final PublicKey configKey,
                                                       final PublicKey treasuryKey,
                                                       final PublicKey priceUpdateAccountKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey writeAuthorityKey) {
    return List.of(
      createWritableSigner(payerKey),
      createRead(guardianSetKey),
      createRead(configKey),
      createWrite(treasuryKey),
      createWritableSigner(priceUpdateAccountKey),
      createRead(systemProgramKey),
      createReadOnlySigner(writeAuthorityKey)
    );
  }

  /// Post a price update using a VAA and a MerklePriceUpdate.
  /// This function allows you to post a price update in a single transaction.
  /// Compared to `post_update`, it only checks whatever signatures are present in the provided VAA and doesn't fail if the number of signatures is lower than the Wormhole quorum of two thirds of the guardians.
  /// The number of signatures that were in the VAA is stored in the `VerificationLevel` of the `PriceUpdateV2` account.
  /// 
  /// We recommend using `post_update_atomic` with 5 signatures. This is close to the maximum signatures you can verify in one transaction without exceeding the transaction size limit.
  /// 
  /// # Warning
  /// 
  /// Using partially verified price updates is dangerous, as it lowers the threshold of guardians that need to collude to produce a malicious price update.
  ///
  /// @param guardianSetKey Instead we do the same steps in deserialize_guardian_set_checked.
  /// @param priceUpdateAccountKey The constraint is such that either the price_update_account is uninitialized or the write_authority is the write_authority.
  ///                              Pubkey::default() is the SystemProgram on Solana and it can't sign so it's impossible that price_update_account.write_authority == Pubkey::default() once the account is initialized
  public static Instruction postUpdateAtomic(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                             final PublicKey payerKey,
                                             final PublicKey guardianSetKey,
                                             final PublicKey configKey,
                                             final PublicKey treasuryKey,
                                             final PublicKey priceUpdateAccountKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey writeAuthorityKey,
                                             final PostUpdateAtomicParams params) {
    final var keys = postUpdateAtomicKeys(
      payerKey,
      guardianSetKey,
      configKey,
      treasuryKey,
      priceUpdateAccountKey,
      systemProgramKey,
      writeAuthorityKey
    );
    return postUpdateAtomic(invokedPythSolanaReceiverProgramMeta, keys, params);
  }

  /// Post a price update using a VAA and a MerklePriceUpdate.
  /// This function allows you to post a price update in a single transaction.
  /// Compared to `post_update`, it only checks whatever signatures are present in the provided VAA and doesn't fail if the number of signatures is lower than the Wormhole quorum of two thirds of the guardians.
  /// The number of signatures that were in the VAA is stored in the `VerificationLevel` of the `PriceUpdateV2` account.
  /// 
  /// We recommend using `post_update_atomic` with 5 signatures. This is close to the maximum signatures you can verify in one transaction without exceeding the transaction size limit.
  /// 
  /// # Warning
  /// 
  /// Using partially verified price updates is dangerous, as it lowers the threshold of guardians that need to collude to produce a malicious price update.
  ///
  public static Instruction postUpdateAtomic(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                             final List<AccountMeta> keys,
                                             final PostUpdateAtomicParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = POST_UPDATE_ATOMIC_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record PostUpdateAtomicIxData(Discriminator discriminator, PostUpdateAtomicParams params) implements Borsh {  

    public static PostUpdateAtomicIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PostUpdateAtomicIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PostUpdateAtomicParams.read(_data, i);
      return new PostUpdateAtomicIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator POST_UPDATE_DISCRIMINATOR = toDiscriminator(133, 95, 207, 175, 11, 79, 118, 44);

  /// Post a price update using an encoded_vaa account and a MerklePriceUpdate calldata.
  /// This should be called after the client has already verified the Vaa via the Wormhole contract.
  /// Check out target_chains/solana/cli/src/main.rs for an example of how to do this.
  ///
  /// @param priceUpdateAccountKey The constraint is such that either the price_update_account is uninitialized or the write_authority is the write_authority.
  ///                              Pubkey::default() is the SystemProgram on Solana and it can't sign so it's impossible that price_update_account.write_authority == Pubkey::default() once the account is initialized
  public static List<AccountMeta> postUpdateKeys(final PublicKey payerKey,
                                                 final PublicKey encodedVaaKey,
                                                 final PublicKey configKey,
                                                 final PublicKey treasuryKey,
                                                 final PublicKey priceUpdateAccountKey,
                                                 final PublicKey systemProgramKey,
                                                 final PublicKey writeAuthorityKey) {
    return List.of(
      createWritableSigner(payerKey),
      createRead(encodedVaaKey),
      createRead(configKey),
      createWrite(treasuryKey),
      createWritableSigner(priceUpdateAccountKey),
      createRead(systemProgramKey),
      createReadOnlySigner(writeAuthorityKey)
    );
  }

  /// Post a price update using an encoded_vaa account and a MerklePriceUpdate calldata.
  /// This should be called after the client has already verified the Vaa via the Wormhole contract.
  /// Check out target_chains/solana/cli/src/main.rs for an example of how to do this.
  ///
  /// @param priceUpdateAccountKey The constraint is such that either the price_update_account is uninitialized or the write_authority is the write_authority.
  ///                              Pubkey::default() is the SystemProgram on Solana and it can't sign so it's impossible that price_update_account.write_authority == Pubkey::default() once the account is initialized
  public static Instruction postUpdate(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                       final PublicKey payerKey,
                                       final PublicKey encodedVaaKey,
                                       final PublicKey configKey,
                                       final PublicKey treasuryKey,
                                       final PublicKey priceUpdateAccountKey,
                                       final PublicKey systemProgramKey,
                                       final PublicKey writeAuthorityKey,
                                       final PostUpdateParams params) {
    final var keys = postUpdateKeys(
      payerKey,
      encodedVaaKey,
      configKey,
      treasuryKey,
      priceUpdateAccountKey,
      systemProgramKey,
      writeAuthorityKey
    );
    return postUpdate(invokedPythSolanaReceiverProgramMeta, keys, params);
  }

  /// Post a price update using an encoded_vaa account and a MerklePriceUpdate calldata.
  /// This should be called after the client has already verified the Vaa via the Wormhole contract.
  /// Check out target_chains/solana/cli/src/main.rs for an example of how to do this.
  ///
  public static Instruction postUpdate(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                       final List<AccountMeta> keys,
                                       final PostUpdateParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = POST_UPDATE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record PostUpdateIxData(Discriminator discriminator, PostUpdateParams params) implements Borsh {  

    public static PostUpdateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PostUpdateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PostUpdateParams.read(_data, i);
      return new PostUpdateIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator POST_TWAP_UPDATE_DISCRIMINATOR = toDiscriminator(232, 176, 212, 105, 69, 121, 18, 30);

  /// Post a TWAP (time weighted average price) update for a given time window.
  /// This should be called after the client has already verified the VAAs via the Wormhole contract.
  /// Check out target_chains/solana/cli/src/main.rs for an example of how to do this.
  ///
  /// @param twapUpdateAccountKey The constraint is such that either the price_update_account is uninitialized or the write_authority is the write_authority.
  ///                             Pubkey::default() is the SystemProgram on Solana and it can't sign so it's impossible that price_update_account.write_authority == Pubkey::default() once the account is initialized
  public static List<AccountMeta> postTwapUpdateKeys(final PublicKey payerKey,
                                                     final PublicKey startEncodedVaaKey,
                                                     final PublicKey endEncodedVaaKey,
                                                     final PublicKey configKey,
                                                     final PublicKey treasuryKey,
                                                     final PublicKey twapUpdateAccountKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey writeAuthorityKey) {
    return List.of(
      createWritableSigner(payerKey),
      createRead(startEncodedVaaKey),
      createRead(endEncodedVaaKey),
      createRead(configKey),
      createWrite(treasuryKey),
      createWritableSigner(twapUpdateAccountKey),
      createRead(systemProgramKey),
      createReadOnlySigner(writeAuthorityKey)
    );
  }

  /// Post a TWAP (time weighted average price) update for a given time window.
  /// This should be called after the client has already verified the VAAs via the Wormhole contract.
  /// Check out target_chains/solana/cli/src/main.rs for an example of how to do this.
  ///
  /// @param twapUpdateAccountKey The constraint is such that either the price_update_account is uninitialized or the write_authority is the write_authority.
  ///                             Pubkey::default() is the SystemProgram on Solana and it can't sign so it's impossible that price_update_account.write_authority == Pubkey::default() once the account is initialized
  public static Instruction postTwapUpdate(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                           final PublicKey payerKey,
                                           final PublicKey startEncodedVaaKey,
                                           final PublicKey endEncodedVaaKey,
                                           final PublicKey configKey,
                                           final PublicKey treasuryKey,
                                           final PublicKey twapUpdateAccountKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey writeAuthorityKey,
                                           final PostTwapUpdateParams params) {
    final var keys = postTwapUpdateKeys(
      payerKey,
      startEncodedVaaKey,
      endEncodedVaaKey,
      configKey,
      treasuryKey,
      twapUpdateAccountKey,
      systemProgramKey,
      writeAuthorityKey
    );
    return postTwapUpdate(invokedPythSolanaReceiverProgramMeta, keys, params);
  }

  /// Post a TWAP (time weighted average price) update for a given time window.
  /// This should be called after the client has already verified the VAAs via the Wormhole contract.
  /// Check out target_chains/solana/cli/src/main.rs for an example of how to do this.
  ///
  public static Instruction postTwapUpdate(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                           final List<AccountMeta> keys,
                                           final PostTwapUpdateParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = POST_TWAP_UPDATE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, _data);
  }

  public record PostTwapUpdateIxData(Discriminator discriminator, PostTwapUpdateParams params) implements Borsh {  

    public static PostTwapUpdateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PostTwapUpdateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PostTwapUpdateParams.read(_data, i);
      return new PostTwapUpdateIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += params.write(_data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + params.l();
    }
  }

  public static final Discriminator RECLAIM_RENT_DISCRIMINATOR = toDiscriminator(218, 200, 19, 197, 227, 89, 192, 22);

  public static List<AccountMeta> reclaimRentKeys(final PublicKey payerKey,
                                                  final PublicKey priceUpdateAccountKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(priceUpdateAccountKey)
    );
  }

  public static Instruction reclaimRent(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                        final PublicKey payerKey,
                                        final PublicKey priceUpdateAccountKey) {
    final var keys = reclaimRentKeys(
      payerKey,
      priceUpdateAccountKey
    );
    return reclaimRent(invokedPythSolanaReceiverProgramMeta, keys);
  }

  public static Instruction reclaimRent(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, RECLAIM_RENT_DISCRIMINATOR);
  }

  public static final Discriminator RECLAIM_TWAP_RENT_DISCRIMINATOR = toDiscriminator(84, 3, 32, 238, 108, 217, 135, 39);

  public static List<AccountMeta> reclaimTwapRentKeys(final PublicKey payerKey,
                                                      final PublicKey twapUpdateAccountKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(twapUpdateAccountKey)
    );
  }

  public static Instruction reclaimTwapRent(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                            final PublicKey payerKey,
                                            final PublicKey twapUpdateAccountKey) {
    final var keys = reclaimTwapRentKeys(
      payerKey,
      twapUpdateAccountKey
    );
    return reclaimTwapRent(invokedPythSolanaReceiverProgramMeta, keys);
  }

  public static Instruction reclaimTwapRent(final AccountMeta invokedPythSolanaReceiverProgramMeta,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedPythSolanaReceiverProgramMeta, keys, RECLAIM_TWAP_RENT_DISCRIMINATOR);
  }

  private PythSolanaReceiverProgram() {
  }
}
