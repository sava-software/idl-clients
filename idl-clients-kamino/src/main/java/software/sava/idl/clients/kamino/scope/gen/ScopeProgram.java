package software.sava.idl.clients.kamino.scope.gen;

import java.lang.String;

import java.util.Arrays;
import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.kamino.scope.gen.types.UpdateOracleMappingAndMetadataEntriesWithId;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class ScopeProgram {

  public static final Discriminator INITIALIZE_DISCRIMINATOR = toDiscriminator(175, 175, 109, 31, 13, 152, 155, 237);

  public static List<AccountMeta> initializeKeys(final PublicKey adminKey,
                                                 final PublicKey systemProgramKey,
                                                 final PublicKey configurationKey,
                                                 final PublicKey tokenMetadatasKey,
                                                 final PublicKey oracleTwapsKey,
                                                 final PublicKey oraclePricesKey,
                                                 final PublicKey oracleMappingsKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(systemProgramKey),
      createWrite(configurationKey),
      createWrite(tokenMetadatasKey),
      createWrite(oracleTwapsKey),
      createWrite(oraclePricesKey),
      createWrite(oracleMappingsKey)
    );
  }

  public static Instruction initialize(final AccountMeta invokedScopeProgramMeta,
                                       final PublicKey adminKey,
                                       final PublicKey systemProgramKey,
                                       final PublicKey configurationKey,
                                       final PublicKey tokenMetadatasKey,
                                       final PublicKey oracleTwapsKey,
                                       final PublicKey oraclePricesKey,
                                       final PublicKey oracleMappingsKey,
                                       final String feedName) {
    final var keys = initializeKeys(
      adminKey,
      systemProgramKey,
      configurationKey,
      tokenMetadatasKey,
      oracleTwapsKey,
      oraclePricesKey,
      oracleMappingsKey
    );
    return initialize(invokedScopeProgramMeta, keys, feedName);
  }

  public static Instruction initialize(final AccountMeta invokedScopeProgramMeta,
                                       final List<AccountMeta> keys,
                                       final String feedName) {
    final byte[] _feedName = feedName.getBytes(UTF_8);
    final byte[] _data = new byte[12 + _feedName.length];
    int i = INITIALIZE_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(_feedName, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record InitializeIxData(Discriminator discriminator, String feedName, byte[] _feedName) implements Borsh {  

    public static InitializeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeIxData createRecord(final Discriminator discriminator, final String feedName) {
      return new InitializeIxData(discriminator, feedName, feedName.getBytes(UTF_8));
    }

    public static InitializeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _feedNameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _feedName = Arrays.copyOfRange(_data, i, i + _feedNameLength);
      final var feedName = new String(_feedName, UTF_8);
      return new InitializeIxData(discriminator, feedName, _feedName);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_feedName, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _feedName.length;
    }
  }

  public static final Discriminator REFRESH_PRICE_LIST_DISCRIMINATOR = toDiscriminator(83, 186, 207, 131, 203, 254, 198, 130);

  public static List<AccountMeta> refreshPriceListKeys(final PublicKey oraclePricesKey,
                                                       final PublicKey oracleMappingsKey,
                                                       final PublicKey oracleTwapsKey,
                                                       final PublicKey instructionSysvarAccountInfoKey) {
    return List.of(
      createWrite(oraclePricesKey),
      createRead(oracleMappingsKey),
      createWrite(oracleTwapsKey),
      createRead(instructionSysvarAccountInfoKey)
    );
  }

  public static Instruction refreshPriceList(final AccountMeta invokedScopeProgramMeta,
                                             final PublicKey oraclePricesKey,
                                             final PublicKey oracleMappingsKey,
                                             final PublicKey oracleTwapsKey,
                                             final PublicKey instructionSysvarAccountInfoKey,
                                             final short[] tokens) {
    final var keys = refreshPriceListKeys(
      oraclePricesKey,
      oracleMappingsKey,
      oracleTwapsKey,
      instructionSysvarAccountInfoKey
    );
    return refreshPriceList(invokedScopeProgramMeta, keys, tokens);
  }

  public static Instruction refreshPriceList(final AccountMeta invokedScopeProgramMeta,
                                             final List<AccountMeta> keys,
                                             final short[] tokens) {
    final byte[] _data = new byte[8 + Borsh.lenVector(tokens)];
    int i = REFRESH_PRICE_LIST_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(tokens, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record RefreshPriceListIxData(Discriminator discriminator, short[] tokens) implements Borsh {  

    public static RefreshPriceListIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RefreshPriceListIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var tokens = Borsh.readshortVector(_data, i);
      return new RefreshPriceListIxData(discriminator, tokens);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(tokens, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(tokens);
    }
  }

  public static final Discriminator REFRESH_CHAINLINK_PRICE_DISCRIMINATOR = toDiscriminator(97, 9, 20, 115, 72, 255, 4, 140);

  /// @param userKey The account that signs the transaction.
  /// @param verifierAccountKey The Verifier Account stores the DON's public keys and other verification parameters.
  ///                           This account must match the PDA derived from the verifier program.
  /// @param accessControllerKey The Access Controller Account
  /// @param configAccountKey The Config Account is a PDA derived from a signed report
  /// @param verifierProgramIdKey The Verifier Program ID specifies the target Chainlink Data Streams Verifier Program.
  public static List<AccountMeta> refreshChainlinkPriceKeys(final PublicKey userKey,
                                                            final PublicKey oraclePricesKey,
                                                            final PublicKey oracleMappingsKey,
                                                            final PublicKey oracleTwapsKey,
                                                            final PublicKey verifierAccountKey,
                                                            final PublicKey accessControllerKey,
                                                            final PublicKey configAccountKey,
                                                            final PublicKey verifierProgramIdKey) {
    return List.of(
      createReadOnlySigner(userKey),
      createWrite(oraclePricesKey),
      createRead(oracleMappingsKey),
      createWrite(oracleTwapsKey),
      createRead(verifierAccountKey),
      createRead(accessControllerKey),
      createRead(configAccountKey),
      createRead(verifierProgramIdKey)
    );
  }

  /// @param userKey The account that signs the transaction.
  /// @param verifierAccountKey The Verifier Account stores the DON's public keys and other verification parameters.
  ///                           This account must match the PDA derived from the verifier program.
  /// @param accessControllerKey The Access Controller Account
  /// @param configAccountKey The Config Account is a PDA derived from a signed report
  /// @param verifierProgramIdKey The Verifier Program ID specifies the target Chainlink Data Streams Verifier Program.
  public static Instruction refreshChainlinkPrice(final AccountMeta invokedScopeProgramMeta,
                                                  final PublicKey userKey,
                                                  final PublicKey oraclePricesKey,
                                                  final PublicKey oracleMappingsKey,
                                                  final PublicKey oracleTwapsKey,
                                                  final PublicKey verifierAccountKey,
                                                  final PublicKey accessControllerKey,
                                                  final PublicKey configAccountKey,
                                                  final PublicKey verifierProgramIdKey,
                                                  final int token,
                                                  final byte[] serializedChainlinkReport) {
    final var keys = refreshChainlinkPriceKeys(
      userKey,
      oraclePricesKey,
      oracleMappingsKey,
      oracleTwapsKey,
      verifierAccountKey,
      accessControllerKey,
      configAccountKey,
      verifierProgramIdKey
    );
    return refreshChainlinkPrice(invokedScopeProgramMeta, keys, token, serializedChainlinkReport);
  }

  public static Instruction refreshChainlinkPrice(final AccountMeta invokedScopeProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final int token,
                                                  final byte[] serializedChainlinkReport) {
    final byte[] _data = new byte[10 + Borsh.lenVector(serializedChainlinkReport)];
    int i = REFRESH_CHAINLINK_PRICE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, token);
    i += 2;
    Borsh.writeVector(serializedChainlinkReport, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record RefreshChainlinkPriceIxData(Discriminator discriminator, int token, byte[] serializedChainlinkReport) implements Borsh {  

    public static RefreshChainlinkPriceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RefreshChainlinkPriceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var token = getInt16LE(_data, i);
      i += 2;
      final var serializedChainlinkReport = Borsh.readbyteVector(_data, i);
      return new RefreshChainlinkPriceIxData(discriminator, token, serializedChainlinkReport);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, token);
      i += 2;
      i += Borsh.writeVector(serializedChainlinkReport, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + Borsh.lenVector(serializedChainlinkReport);
    }
  }

  public static final Discriminator REFRESH_PYTH_LAZER_PRICE_DISCRIMINATOR = toDiscriminator(122, 47, 177, 133, 177, 35, 93, 118);

  /// IMPORTANT: we assume the tokens passed in to this ix are in the same order in which
  /// they are found in the message payload. Thus, we rely on the client to do this work
  ///
  /// @param userKey The account that signs the transaction.
  public static List<AccountMeta> refreshPythLazerPriceKeys(final PublicKey userKey,
                                                            final PublicKey oraclePricesKey,
                                                            final PublicKey oracleMappingsKey,
                                                            final PublicKey oracleTwapsKey,
                                                            final PublicKey pythProgramKey,
                                                            final PublicKey pythStorageKey,
                                                            final PublicKey pythTreasuryKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey instructionsSysvarKey) {
    return List.of(
      createWritableSigner(userKey),
      createWrite(oraclePricesKey),
      createRead(oracleMappingsKey),
      createWrite(oracleTwapsKey),
      createRead(pythProgramKey),
      createRead(pythStorageKey),
      createWrite(pythTreasuryKey),
      createRead(systemProgramKey),
      createRead(instructionsSysvarKey)
    );
  }

  /// IMPORTANT: we assume the tokens passed in to this ix are in the same order in which
  /// they are found in the message payload. Thus, we rely on the client to do this work
  ///
  /// @param userKey The account that signs the transaction.
  public static Instruction refreshPythLazerPrice(final AccountMeta invokedScopeProgramMeta,
                                                  final PublicKey userKey,
                                                  final PublicKey oraclePricesKey,
                                                  final PublicKey oracleMappingsKey,
                                                  final PublicKey oracleTwapsKey,
                                                  final PublicKey pythProgramKey,
                                                  final PublicKey pythStorageKey,
                                                  final PublicKey pythTreasuryKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey instructionsSysvarKey,
                                                  final short[] tokens,
                                                  final byte[] serializedPythMessage,
                                                  final int ed25519InstructionIndex) {
    final var keys = refreshPythLazerPriceKeys(
      userKey,
      oraclePricesKey,
      oracleMappingsKey,
      oracleTwapsKey,
      pythProgramKey,
      pythStorageKey,
      pythTreasuryKey,
      systemProgramKey,
      instructionsSysvarKey
    );
    return refreshPythLazerPrice(
      invokedScopeProgramMeta,
      keys,
      tokens,
      serializedPythMessage,
      ed25519InstructionIndex
    );
  }

  /// IMPORTANT: we assume the tokens passed in to this ix are in the same order in which
  /// they are found in the message payload. Thus, we rely on the client to do this work
  ///
  public static Instruction refreshPythLazerPrice(final AccountMeta invokedScopeProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final short[] tokens,
                                                  final byte[] serializedPythMessage,
                                                  final int ed25519InstructionIndex) {
    final byte[] _data = new byte[10 + Borsh.lenVector(tokens) + Borsh.lenVector(serializedPythMessage)];
    int i = REFRESH_PYTH_LAZER_PRICE_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(tokens, _data, i);
    i += Borsh.writeVector(serializedPythMessage, _data, i);
    putInt16LE(_data, i, ed25519InstructionIndex);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record RefreshPythLazerPriceIxData(Discriminator discriminator,
                                            short[] tokens,
                                            byte[] serializedPythMessage,
                                            int ed25519InstructionIndex) implements Borsh {  

    public static RefreshPythLazerPriceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RefreshPythLazerPriceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var tokens = Borsh.readshortVector(_data, i);
      i += Borsh.lenVector(tokens);
      final var serializedPythMessage = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(serializedPythMessage);
      final var ed25519InstructionIndex = getInt16LE(_data, i);
      return new RefreshPythLazerPriceIxData(discriminator, tokens, serializedPythMessage, ed25519InstructionIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(tokens, _data, i);
      i += Borsh.writeVector(serializedPythMessage, _data, i);
      putInt16LE(_data, i, ed25519InstructionIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(tokens) + Borsh.lenVector(serializedPythMessage) + 2;
    }
  }

  public static final Discriminator UPDATE_MAPPING_AND_METADATA_DISCRIMINATOR = toDiscriminator(158, 81, 149, 146, 206, 154, 91, 56);

  /// @param oraclePricesKey Price entry will be reset if the corresponding mapping changes
  /// @param oracleTwapsKey Twap entry will be reset if the corresponding mapping changes
  public static List<AccountMeta> updateMappingAndMetadataKeys(final PublicKey adminKey,
                                                               final PublicKey configurationKey,
                                                               final PublicKey oracleMappingsKey,
                                                               final PublicKey tokensMetadataKey,
                                                               final PublicKey oraclePricesKey,
                                                               final PublicKey oracleTwapsKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(configurationKey),
      createWrite(oracleMappingsKey),
      createWrite(tokensMetadataKey),
      createWrite(oraclePricesKey),
      createWrite(oracleTwapsKey)
    );
  }

  /// @param oraclePricesKey Price entry will be reset if the corresponding mapping changes
  /// @param oracleTwapsKey Twap entry will be reset if the corresponding mapping changes
  public static Instruction updateMappingAndMetadata(final AccountMeta invokedScopeProgramMeta,
                                                     final PublicKey adminKey,
                                                     final PublicKey configurationKey,
                                                     final PublicKey oracleMappingsKey,
                                                     final PublicKey tokensMetadataKey,
                                                     final PublicKey oraclePricesKey,
                                                     final PublicKey oracleTwapsKey,
                                                     final String feedName,
                                                     final UpdateOracleMappingAndMetadataEntriesWithId[] updates) {
    final var keys = updateMappingAndMetadataKeys(
      adminKey,
      configurationKey,
      oracleMappingsKey,
      tokensMetadataKey,
      oraclePricesKey,
      oracleTwapsKey
    );
    return updateMappingAndMetadata(invokedScopeProgramMeta, keys, feedName, updates);
  }

  public static Instruction updateMappingAndMetadata(final AccountMeta invokedScopeProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final String feedName,
                                                     final UpdateOracleMappingAndMetadataEntriesWithId[] updates) {
    final byte[] _feedName = feedName.getBytes(UTF_8);
    final byte[] _data = new byte[12 + _feedName.length + Borsh.lenVector(updates)];
    int i = UPDATE_MAPPING_AND_METADATA_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(_feedName, _data, i);
    Borsh.writeVector(updates, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record UpdateMappingAndMetadataIxData(Discriminator discriminator, String feedName, byte[] _feedName, UpdateOracleMappingAndMetadataEntriesWithId[] updates) implements Borsh {  

    public static UpdateMappingAndMetadataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateMappingAndMetadataIxData createRecord(final Discriminator discriminator, final String feedName, final UpdateOracleMappingAndMetadataEntriesWithId[] updates) {
      return new UpdateMappingAndMetadataIxData(discriminator, feedName, feedName.getBytes(UTF_8), updates);
    }

    public static UpdateMappingAndMetadataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _feedNameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _feedName = Arrays.copyOfRange(_data, i, i + _feedNameLength);
      final var feedName = new String(_feedName, UTF_8);
      i += _feedName.length;
      final var updates = Borsh.readVector(UpdateOracleMappingAndMetadataEntriesWithId.class, UpdateOracleMappingAndMetadataEntriesWithId::read, _data, i);
      return new UpdateMappingAndMetadataIxData(discriminator, feedName, _feedName, updates);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_feedName, _data, i);
      i += Borsh.writeVector(updates, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _feedName.length + Borsh.lenVector(updates);
    }
  }

  public static final Discriminator RESET_TWAP_DISCRIMINATOR = toDiscriminator(101, 216, 28, 92, 154, 79, 49, 187);

  public static List<AccountMeta> resetTwapKeys(final PublicKey adminKey,
                                                final PublicKey configurationKey,
                                                final PublicKey oracleTwapsKey,
                                                final PublicKey instructionSysvarAccountInfoKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(configurationKey),
      createWrite(oracleTwapsKey),
      createRead(instructionSysvarAccountInfoKey)
    );
  }

  public static Instruction resetTwap(final AccountMeta invokedScopeProgramMeta,
                                      final PublicKey adminKey,
                                      final PublicKey configurationKey,
                                      final PublicKey oracleTwapsKey,
                                      final PublicKey instructionSysvarAccountInfoKey,
                                      final long token,
                                      final String feedName) {
    final var keys = resetTwapKeys(
      adminKey,
      configurationKey,
      oracleTwapsKey,
      instructionSysvarAccountInfoKey
    );
    return resetTwap(invokedScopeProgramMeta, keys, token, feedName);
  }

  public static Instruction resetTwap(final AccountMeta invokedScopeProgramMeta,
                                      final List<AccountMeta> keys,
                                      final long token,
                                      final String feedName) {
    final byte[] _feedName = feedName.getBytes(UTF_8);
    final byte[] _data = new byte[20 + _feedName.length];
    int i = RESET_TWAP_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, token);
    i += 8;
    Borsh.writeVector(_feedName, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record ResetTwapIxData(Discriminator discriminator, long token, String feedName, byte[] _feedName) implements Borsh {  

    public static ResetTwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ResetTwapIxData createRecord(final Discriminator discriminator, final long token, final String feedName) {
      return new ResetTwapIxData(discriminator, token, feedName, feedName.getBytes(UTF_8));
    }

    public static ResetTwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var token = getInt64LE(_data, i);
      i += 8;
      final int _feedNameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _feedName = Arrays.copyOfRange(_data, i, i + _feedNameLength);
      final var feedName = new String(_feedName, UTF_8);
      return new ResetTwapIxData(discriminator, token, feedName, _feedName);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, token);
      i += 8;
      i += Borsh.writeVector(_feedName, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 8 + _feedName.length;
    }
  }

  public static final Discriminator SET_ADMIN_CACHED_DISCRIMINATOR = toDiscriminator(114, 14, 105, 205, 216, 148, 30, 75);

  public static List<AccountMeta> setAdminCachedKeys(final PublicKey adminKey,
                                                     final PublicKey configurationKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(configurationKey)
    );
  }

  public static Instruction setAdminCached(final AccountMeta invokedScopeProgramMeta,
                                           final PublicKey adminKey,
                                           final PublicKey configurationKey,
                                           final PublicKey newAdmin,
                                           final String feedName) {
    final var keys = setAdminCachedKeys(
      adminKey,
      configurationKey
    );
    return setAdminCached(invokedScopeProgramMeta, keys, newAdmin, feedName);
  }

  public static Instruction setAdminCached(final AccountMeta invokedScopeProgramMeta,
                                           final List<AccountMeta> keys,
                                           final PublicKey newAdmin,
                                           final String feedName) {
    final byte[] _feedName = feedName.getBytes(UTF_8);
    final byte[] _data = new byte[44 + _feedName.length];
    int i = SET_ADMIN_CACHED_DISCRIMINATOR.write(_data, 0);
    newAdmin.write(_data, i);
    i += 32;
    Borsh.writeVector(_feedName, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record SetAdminCachedIxData(Discriminator discriminator, PublicKey newAdmin, String feedName, byte[] _feedName) implements Borsh {  

    public static SetAdminCachedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SetAdminCachedIxData createRecord(final Discriminator discriminator, final PublicKey newAdmin, final String feedName) {
      return new SetAdminCachedIxData(discriminator, newAdmin, feedName, feedName.getBytes(UTF_8));
    }

    public static SetAdminCachedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newAdmin = readPubKey(_data, i);
      i += 32;
      final int _feedNameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _feedName = Arrays.copyOfRange(_data, i, i + _feedNameLength);
      final var feedName = new String(_feedName, UTF_8);
      return new SetAdminCachedIxData(discriminator, newAdmin, feedName, _feedName);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      newAdmin.write(_data, i);
      i += 32;
      i += Borsh.writeVector(_feedName, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 32 + _feedName.length;
    }
  }

  public static final Discriminator APPROVE_ADMIN_CACHED_DISCRIMINATOR = toDiscriminator(101, 149, 97, 58, 48, 79, 16, 105);

  public static List<AccountMeta> approveAdminCachedKeys(final PublicKey adminCachedKey,
                                                         final PublicKey configurationKey) {
    return List.of(
      createReadOnlySigner(adminCachedKey),
      createWrite(configurationKey)
    );
  }

  public static Instruction approveAdminCached(final AccountMeta invokedScopeProgramMeta,
                                               final PublicKey adminCachedKey,
                                               final PublicKey configurationKey,
                                               final String feedName) {
    final var keys = approveAdminCachedKeys(
      adminCachedKey,
      configurationKey
    );
    return approveAdminCached(invokedScopeProgramMeta, keys, feedName);
  }

  public static Instruction approveAdminCached(final AccountMeta invokedScopeProgramMeta,
                                               final List<AccountMeta> keys,
                                               final String feedName) {
    final byte[] _feedName = feedName.getBytes(UTF_8);
    final byte[] _data = new byte[12 + _feedName.length];
    int i = APPROVE_ADMIN_CACHED_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(_feedName, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record ApproveAdminCachedIxData(Discriminator discriminator, String feedName, byte[] _feedName) implements Borsh {  

    public static ApproveAdminCachedIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ApproveAdminCachedIxData createRecord(final Discriminator discriminator, final String feedName) {
      return new ApproveAdminCachedIxData(discriminator, feedName, feedName.getBytes(UTF_8));
    }

    public static ApproveAdminCachedIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final int _feedNameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _feedName = Arrays.copyOfRange(_data, i, i + _feedNameLength);
      final var feedName = new String(_feedName, UTF_8);
      return new ApproveAdminCachedIxData(discriminator, feedName, _feedName);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_feedName, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + _feedName.length;
    }
  }

  public static final Discriminator CREATE_MINT_MAP_DISCRIMINATOR = toDiscriminator(216, 218, 224, 60, 23, 31, 193, 243);

  public static List<AccountMeta> createMintMapKeys(final PublicKey adminKey,
                                                    final PublicKey configurationKey,
                                                    final PublicKey mappingsKey,
                                                    final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(configurationKey),
      createWrite(mappingsKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction createMintMap(final AccountMeta invokedScopeProgramMeta,
                                          final PublicKey adminKey,
                                          final PublicKey configurationKey,
                                          final PublicKey mappingsKey,
                                          final PublicKey systemProgramKey,
                                          final PublicKey seedPk,
                                          final long seedId,
                                          final int bump,
                                          final short[][] scopeChains) {
    final var keys = createMintMapKeys(
      adminKey,
      configurationKey,
      mappingsKey,
      systemProgramKey
    );
    return createMintMap(
      invokedScopeProgramMeta,
      keys,
      seedPk,
      seedId,
      bump,
      scopeChains
    );
  }

  public static Instruction createMintMap(final AccountMeta invokedScopeProgramMeta,
                                          final List<AccountMeta> keys,
                                          final PublicKey seedPk,
                                          final long seedId,
                                          final int bump,
                                          final short[][] scopeChains) {
    final byte[] _data = new byte[49 + Borsh.lenVectorArray(scopeChains)];
    int i = CREATE_MINT_MAP_DISCRIMINATOR.write(_data, 0);
    seedPk.write(_data, i);
    i += 32;
    putInt64LE(_data, i, seedId);
    i += 8;
    _data[i] = (byte) bump;
    ++i;
    Borsh.writeVectorArrayChecked(scopeChains, 4, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record CreateMintMapIxData(Discriminator discriminator,
                                    PublicKey seedPk,
                                    long seedId,
                                    int bump,
                                    short[][] scopeChains) implements Borsh {  

    public static CreateMintMapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateMintMapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var seedPk = readPubKey(_data, i);
      i += 32;
      final var seedId = getInt64LE(_data, i);
      i += 8;
      final var bump = _data[i] & 0xFF;
      ++i;
      final var scopeChains = Borsh.readMultiDimensionshortVectorArray(4, _data, i);
      return new CreateMintMapIxData(discriminator,
                                     seedPk,
                                     seedId,
                                     bump,
                                     scopeChains);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      seedPk.write(_data, i);
      i += 32;
      putInt64LE(_data, i, seedId);
      i += 8;
      _data[i] = (byte) bump;
      ++i;
      i += Borsh.writeVectorArrayChecked(scopeChains, 4, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 32 + 8 + 1 + Borsh.lenVectorArray(scopeChains);
    }
  }

  public static final Discriminator CLOSE_MINT_MAP_DISCRIMINATOR = toDiscriminator(146, 212, 203, 239, 191, 104, 38, 102);

  public static List<AccountMeta> closeMintMapKeys(final PublicKey adminKey,
                                                   final PublicKey configurationKey,
                                                   final PublicKey mappingsKey,
                                                   final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(configurationKey),
      createWrite(mappingsKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction closeMintMap(final AccountMeta invokedScopeProgramMeta,
                                         final PublicKey adminKey,
                                         final PublicKey configurationKey,
                                         final PublicKey mappingsKey,
                                         final PublicKey systemProgramKey) {
    final var keys = closeMintMapKeys(
      adminKey,
      configurationKey,
      mappingsKey,
      systemProgramKey
    );
    return closeMintMap(invokedScopeProgramMeta, keys);
  }

  public static Instruction closeMintMap(final AccountMeta invokedScopeProgramMeta,
                                         final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedScopeProgramMeta, keys, CLOSE_MINT_MAP_DISCRIMINATOR);
  }

  public static final Discriminator RESUME_CHAINLINKX_PRICE_DISCRIMINATOR = toDiscriminator(136, 48, 103, 146, 227, 97, 87, 108);

  public static List<AccountMeta> resumeChainlinkxPriceKeys(final PublicKey adminKey,
                                                            final PublicKey configurationKey,
                                                            final PublicKey oraclePricesKey,
                                                            final PublicKey oracleMappingsKey,
                                                            final PublicKey tokensMetadataKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(configurationKey),
      createWrite(oraclePricesKey),
      createRead(oracleMappingsKey),
      createRead(tokensMetadataKey)
    );
  }

  public static Instruction resumeChainlinkxPrice(final AccountMeta invokedScopeProgramMeta,
                                                  final PublicKey adminKey,
                                                  final PublicKey configurationKey,
                                                  final PublicKey oraclePricesKey,
                                                  final PublicKey oracleMappingsKey,
                                                  final PublicKey tokensMetadataKey,
                                                  final int token,
                                                  final String feedName) {
    final var keys = resumeChainlinkxPriceKeys(
      adminKey,
      configurationKey,
      oraclePricesKey,
      oracleMappingsKey,
      tokensMetadataKey
    );
    return resumeChainlinkxPrice(invokedScopeProgramMeta, keys, token, feedName);
  }

  public static Instruction resumeChainlinkxPrice(final AccountMeta invokedScopeProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final int token,
                                                  final String feedName) {
    final byte[] _feedName = feedName.getBytes(UTF_8);
    final byte[] _data = new byte[14 + _feedName.length];
    int i = RESUME_CHAINLINKX_PRICE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, token);
    i += 2;
    Borsh.writeVector(_feedName, _data, i);

    return Instruction.createInstruction(invokedScopeProgramMeta, keys, _data);
  }

  public record ResumeChainlinkxPriceIxData(Discriminator discriminator, int token, String feedName, byte[] _feedName) implements Borsh {  

    public static ResumeChainlinkxPriceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ResumeChainlinkxPriceIxData createRecord(final Discriminator discriminator, final int token, final String feedName) {
      return new ResumeChainlinkxPriceIxData(discriminator, token, feedName, feedName.getBytes(UTF_8));
    }

    public static ResumeChainlinkxPriceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var token = getInt16LE(_data, i);
      i += 2;
      final int _feedNameLength = getInt32LE(_data, i);
      i += 4;
      final byte[] _feedName = Arrays.copyOfRange(_data, i, i + _feedNameLength);
      final var feedName = new String(_feedName, UTF_8);
      return new ResumeChainlinkxPriceIxData(discriminator, token, feedName, _feedName);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, token);
      i += 2;
      i += Borsh.writeVector(_feedName, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + _feedName.length;
    }
  }

  private ScopeProgram() {
  }
}
