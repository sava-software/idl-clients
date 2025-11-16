package software.sava.idl.clients.cctp.message_transmitter.v2.gen;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.AcceptOwnershipParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.DisableAttesterParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.EnableAttesterParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.InitializeParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.PauseParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.ReceiveMessageParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.ReclaimEventAccountParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.SendMessageParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.SetMaxMessageBodySizeParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.SetSignatureThresholdParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.TransferOwnershipParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.UnpauseParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.UpdateAttesterManagerParams;
import software.sava.idl.clients.cctp.message_transmitter.v2.gen.types.UpdatePauserParams;

import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class MessageTransmitterV2Program {

  public static final Discriminator ACCEPT_OWNERSHIP_DISCRIMINATOR = toDiscriminator(172, 23, 43, 13, 238, 213, 85, 150);

  public static List<AccountMeta> acceptOwnershipKeys(final PublicKey pendingOwnerKey,
                                                      final PublicKey messageTransmitterKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(pendingOwnerKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction acceptOwnership(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                            final PublicKey pendingOwnerKey,
                                            final PublicKey messageTransmitterKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final AcceptOwnershipParams params) {
    final var keys = acceptOwnershipKeys(
      pendingOwnerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return acceptOwnership(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction acceptOwnership(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                            final List<AccountMeta> keys,
                                            final AcceptOwnershipParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = ACCEPT_OWNERSHIP_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record AcceptOwnershipIxData(Discriminator discriminator, AcceptOwnershipParams params) implements Borsh {  

    public static AcceptOwnershipIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 8;

    public static AcceptOwnershipIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = AcceptOwnershipParams.read(_data, i);
      return new AcceptOwnershipIxData(discriminator, params);
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

  public static final Discriminator DISABLE_ATTESTER_DISCRIMINATOR = toDiscriminator(61, 171, 131, 95, 172, 15, 227, 229);

  public static List<AccountMeta> disableAttesterKeys(final SolanaAccounts solanaAccounts,
                                                      final PublicKey payerKey,
                                                      final PublicKey attesterManagerKey,
                                                      final PublicKey messageTransmitterKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(attesterManagerKey),
      createWrite(messageTransmitterKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction disableAttester(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                            final SolanaAccounts solanaAccounts,
                                            final PublicKey payerKey,
                                            final PublicKey attesterManagerKey,
                                            final PublicKey messageTransmitterKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey,
                                            final DisableAttesterParams params) {
    final var keys = disableAttesterKeys(
      solanaAccounts,
      payerKey,
      attesterManagerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return disableAttester(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction disableAttester(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                            final List<AccountMeta> keys,
                                            final DisableAttesterParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = DISABLE_ATTESTER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record DisableAttesterIxData(Discriminator discriminator, DisableAttesterParams params) implements Borsh {  

    public static DisableAttesterIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static DisableAttesterIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = DisableAttesterParams.read(_data, i);
      return new DisableAttesterIxData(discriminator, params);
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

  public static final Discriminator ENABLE_ATTESTER_DISCRIMINATOR = toDiscriminator(2, 11, 193, 115, 5, 148, 4, 198);

  public static List<AccountMeta> enableAttesterKeys(final SolanaAccounts solanaAccounts,
                                                     final PublicKey payerKey,
                                                     final PublicKey attesterManagerKey,
                                                     final PublicKey messageTransmitterKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(attesterManagerKey),
      createWrite(messageTransmitterKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction enableAttester(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey payerKey,
                                           final PublicKey attesterManagerKey,
                                           final PublicKey messageTransmitterKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final EnableAttesterParams params) {
    final var keys = enableAttesterKeys(
      solanaAccounts,
      payerKey,
      attesterManagerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return enableAttester(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction enableAttester(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                           final List<AccountMeta> keys,
                                           final EnableAttesterParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = ENABLE_ATTESTER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record EnableAttesterIxData(Discriminator discriminator, EnableAttesterParams params) implements Borsh {  

    public static EnableAttesterIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static EnableAttesterIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = EnableAttesterParams.read(_data, i);
      return new EnableAttesterIxData(discriminator, params);
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

  public static final Discriminator INITIALIZE_DISCRIMINATOR = toDiscriminator(175, 175, 109, 31, 13, 152, 155, 237);

  public static List<AccountMeta> initializeKeys(final SolanaAccounts solanaAccounts,
                                                 final PublicKey payerKey,
                                                 final PublicKey upgradeAuthorityKey,
                                                 final PublicKey messageTransmitterKey,
                                                 final PublicKey messageTransmitterProgramDataKey,
                                                 final PublicKey messageTransmitterProgramKey,
                                                 final PublicKey eventAuthorityKey,
                                                 final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(upgradeAuthorityKey),
      createWrite(messageTransmitterKey),
      createRead(messageTransmitterProgramDataKey),
      createRead(messageTransmitterProgramKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction initialize(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                       final SolanaAccounts solanaAccounts,
                                       final PublicKey payerKey,
                                       final PublicKey upgradeAuthorityKey,
                                       final PublicKey messageTransmitterKey,
                                       final PublicKey messageTransmitterProgramDataKey,
                                       final PublicKey messageTransmitterProgramKey,
                                       final PublicKey eventAuthorityKey,
                                       final PublicKey programKey,
                                       final InitializeParams params) {
    final var keys = initializeKeys(
      solanaAccounts,
      payerKey,
      upgradeAuthorityKey,
      messageTransmitterKey,
      messageTransmitterProgramDataKey,
      messageTransmitterProgramKey,
      eventAuthorityKey,
      programKey
    );
    return initialize(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction initialize(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                       final List<AccountMeta> keys,
                                       final InitializeParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = INITIALIZE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record InitializeIxData(Discriminator discriminator, InitializeParams params) implements Borsh {  

    public static InitializeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 56;

    public static InitializeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = InitializeParams.read(_data, i);
      return new InitializeIxData(discriminator, params);
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

  public static final Discriminator IS_NONCE_USED_DISCRIMINATOR = toDiscriminator(144, 72, 107, 148, 35, 218, 31, 187);

  /// @param usedNonceKey Account will be explicitly loaded to avoid error when it's not initialized
  public static List<AccountMeta> isNonceUsedKeys(final PublicKey usedNonceKey) {
    return List.of(
      createRead(usedNonceKey)
    );
  }

  /// @param usedNonceKey Account will be explicitly loaded to avoid error when it's not initialized
  public static Instruction isNonceUsed(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                        final PublicKey usedNonceKey) {
    final var keys = isNonceUsedKeys(
      usedNonceKey
    );
    return isNonceUsed(invokedMessageTransmitterV2ProgramMeta, keys);
  }

  public static Instruction isNonceUsed(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, IS_NONCE_USED_DISCRIMINATOR);
  }

  public static final Discriminator PAUSE_DISCRIMINATOR = toDiscriminator(211, 22, 221, 251, 74, 121, 193, 47);

  public static List<AccountMeta> pauseKeys(final PublicKey pauserKey,
                                            final PublicKey messageTransmitterKey,
                                            final PublicKey eventAuthorityKey,
                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(pauserKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction pause(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                  final PublicKey pauserKey,
                                  final PublicKey messageTransmitterKey,
                                  final PublicKey eventAuthorityKey,
                                  final PublicKey programKey,
                                  final PauseParams params) {
    final var keys = pauseKeys(
      pauserKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return pause(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction pause(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                  final List<AccountMeta> keys,
                                  final PauseParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = PAUSE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record PauseIxData(Discriminator discriminator, PauseParams params) implements Borsh {  

    public static PauseIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 8;

    public static PauseIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PauseParams.read(_data, i);
      return new PauseIxData(discriminator, params);
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

  public static final Discriminator RECEIVE_MESSAGE_DISCRIMINATOR = toDiscriminator(38, 144, 127, 225, 31, 225, 238, 25);

  /// @param usedNonceKey Each nonce is stored in a separate PDA
  public static List<AccountMeta> receiveMessageKeys(final SolanaAccounts solanaAccounts,
                                                     final PublicKey payerKey,
                                                     final PublicKey callerKey,
                                                     final PublicKey authorityPdaKey,
                                                     final PublicKey messageTransmitterKey,
                                                     final PublicKey usedNonceKey,
                                                     final PublicKey receiverKey,
                                                     final PublicKey eventAuthorityKey,
                                                     final PublicKey programKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(callerKey),
      createRead(authorityPdaKey),
      createRead(messageTransmitterKey),
      createWrite(usedNonceKey),
      createRead(receiverKey),
      createRead(solanaAccounts.systemProgram()),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  /// @param usedNonceKey Each nonce is stored in a separate PDA
  public static Instruction receiveMessage(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                           final SolanaAccounts solanaAccounts,
                                           final PublicKey payerKey,
                                           final PublicKey callerKey,
                                           final PublicKey authorityPdaKey,
                                           final PublicKey messageTransmitterKey,
                                           final PublicKey usedNonceKey,
                                           final PublicKey receiverKey,
                                           final PublicKey eventAuthorityKey,
                                           final PublicKey programKey,
                                           final ReceiveMessageParams params) {
    final var keys = receiveMessageKeys(
      solanaAccounts,
      payerKey,
      callerKey,
      authorityPdaKey,
      messageTransmitterKey,
      usedNonceKey,
      receiverKey,
      eventAuthorityKey,
      programKey
    );
    return receiveMessage(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction receiveMessage(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                           final List<AccountMeta> keys,
                                           final ReceiveMessageParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = RECEIVE_MESSAGE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record ReceiveMessageIxData(Discriminator discriminator, ReceiveMessageParams params) implements Borsh {  

    public static ReceiveMessageIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ReceiveMessageIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ReceiveMessageParams.read(_data, i);
      return new ReceiveMessageIxData(discriminator, params);
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

  public static final Discriminator RECLAIM_EVENT_ACCOUNT_DISCRIMINATOR = toDiscriminator(94, 198, 180, 159, 131, 236, 15, 174);

  /// @param payeeKey rent SOL receiver, should match original rent payer
  public static List<AccountMeta> reclaimEventAccountKeys(final PublicKey payeeKey,
                                                          final PublicKey messageTransmitterKey,
                                                          final PublicKey messageSentEventDataKey) {
    return List.of(
      createWritableSigner(payeeKey),
      createWrite(messageTransmitterKey),
      createWrite(messageSentEventDataKey)
    );
  }

  /// @param payeeKey rent SOL receiver, should match original rent payer
  public static Instruction reclaimEventAccount(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                final PublicKey payeeKey,
                                                final PublicKey messageTransmitterKey,
                                                final PublicKey messageSentEventDataKey,
                                                final ReclaimEventAccountParams params) {
    final var keys = reclaimEventAccountKeys(
      payeeKey,
      messageTransmitterKey,
      messageSentEventDataKey
    );
    return reclaimEventAccount(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction reclaimEventAccount(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                final List<AccountMeta> keys,
                                                final ReclaimEventAccountParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = RECLAIM_EVENT_ACCOUNT_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record ReclaimEventAccountIxData(Discriminator discriminator, ReclaimEventAccountParams params) implements Borsh {  

    public static ReclaimEventAccountIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ReclaimEventAccountIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = ReclaimEventAccountParams.read(_data, i);
      return new ReclaimEventAccountIxData(discriminator, params);
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

  public static final Discriminator SEND_MESSAGE_DISCRIMINATOR = toDiscriminator(57, 40, 34, 178, 189, 10, 65, 26);

  public static List<AccountMeta> sendMessageKeys(final SolanaAccounts solanaAccounts,
                                                  final PublicKey eventRentPayerKey,
                                                  final PublicKey senderAuthorityPdaKey,
                                                  final PublicKey messageTransmitterKey,
                                                  final PublicKey messageSentEventDataKey,
                                                  final PublicKey senderProgramKey) {
    return List.of(
      createWritableSigner(eventRentPayerKey),
      createReadOnlySigner(senderAuthorityPdaKey),
      createWrite(messageTransmitterKey),
      createWritableSigner(messageSentEventDataKey),
      createRead(senderProgramKey),
      createRead(solanaAccounts.systemProgram())
    );
  }

  public static Instruction sendMessage(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                        final SolanaAccounts solanaAccounts,
                                        final PublicKey eventRentPayerKey,
                                        final PublicKey senderAuthorityPdaKey,
                                        final PublicKey messageTransmitterKey,
                                        final PublicKey messageSentEventDataKey,
                                        final PublicKey senderProgramKey,
                                        final SendMessageParams params) {
    final var keys = sendMessageKeys(
      solanaAccounts,
      eventRentPayerKey,
      senderAuthorityPdaKey,
      messageTransmitterKey,
      messageSentEventDataKey,
      senderProgramKey
    );
    return sendMessage(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction sendMessage(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                        final List<AccountMeta> keys,
                                        final SendMessageParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SEND_MESSAGE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record SendMessageIxData(Discriminator discriminator, SendMessageParams params) implements Borsh {  

    public static SendMessageIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SendMessageIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = SendMessageParams.read(_data, i);
      return new SendMessageIxData(discriminator, params);
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

  public static final Discriminator SET_MAX_MESSAGE_BODY_SIZE_DISCRIMINATOR = toDiscriminator(168, 178, 8, 117, 217, 167, 219, 31);

  public static List<AccountMeta> setMaxMessageBodySizeKeys(final PublicKey ownerKey,
                                                            final PublicKey messageTransmitterKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction setMaxMessageBodySize(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                  final PublicKey ownerKey,
                                                  final PublicKey messageTransmitterKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final SetMaxMessageBodySizeParams params) {
    final var keys = setMaxMessageBodySizeKeys(
      ownerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return setMaxMessageBodySize(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction setMaxMessageBodySize(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final SetMaxMessageBodySizeParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SET_MAX_MESSAGE_BODY_SIZE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record SetMaxMessageBodySizeIxData(Discriminator discriminator, SetMaxMessageBodySizeParams params) implements Borsh {  

    public static SetMaxMessageBodySizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static SetMaxMessageBodySizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = SetMaxMessageBodySizeParams.read(_data, i);
      return new SetMaxMessageBodySizeIxData(discriminator, params);
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

  public static final Discriminator SET_SIGNATURE_THRESHOLD_DISCRIMINATOR = toDiscriminator(163, 19, 154, 168, 82, 209, 214, 219);

  public static List<AccountMeta> setSignatureThresholdKeys(final PublicKey attesterManagerKey,
                                                            final PublicKey messageTransmitterKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(attesterManagerKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction setSignatureThreshold(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                  final PublicKey attesterManagerKey,
                                                  final PublicKey messageTransmitterKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final SetSignatureThresholdParams params) {
    final var keys = setSignatureThresholdKeys(
      attesterManagerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return setSignatureThreshold(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction setSignatureThreshold(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final SetSignatureThresholdParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = SET_SIGNATURE_THRESHOLD_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record SetSignatureThresholdIxData(Discriminator discriminator, SetSignatureThresholdParams params) implements Borsh {  

    public static SetSignatureThresholdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static SetSignatureThresholdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = SetSignatureThresholdParams.read(_data, i);
      return new SetSignatureThresholdIxData(discriminator, params);
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

  public static final Discriminator TRANSFER_OWNERSHIP_DISCRIMINATOR = toDiscriminator(65, 177, 215, 73, 53, 45, 99, 47);

  public static List<AccountMeta> transferOwnershipKeys(final PublicKey ownerKey,
                                                        final PublicKey messageTransmitterKey,
                                                        final PublicKey eventAuthorityKey,
                                                        final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction transferOwnership(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                              final PublicKey ownerKey,
                                              final PublicKey messageTransmitterKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey,
                                              final TransferOwnershipParams params) {
    final var keys = transferOwnershipKeys(
      ownerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return transferOwnership(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction transferOwnership(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                              final List<AccountMeta> keys,
                                              final TransferOwnershipParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = TRANSFER_OWNERSHIP_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record TransferOwnershipIxData(Discriminator discriminator, TransferOwnershipParams params) implements Borsh {  

    public static TransferOwnershipIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static TransferOwnershipIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = TransferOwnershipParams.read(_data, i);
      return new TransferOwnershipIxData(discriminator, params);
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

  public static final Discriminator UNPAUSE_DISCRIMINATOR = toDiscriminator(169, 144, 4, 38, 10, 141, 188, 255);

  public static List<AccountMeta> unpauseKeys(final PublicKey pauserKey,
                                              final PublicKey messageTransmitterKey,
                                              final PublicKey eventAuthorityKey,
                                              final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(pauserKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction unpause(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                    final PublicKey pauserKey,
                                    final PublicKey messageTransmitterKey,
                                    final PublicKey eventAuthorityKey,
                                    final PublicKey programKey,
                                    final UnpauseParams params) {
    final var keys = unpauseKeys(
      pauserKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return unpause(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction unpause(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                    final List<AccountMeta> keys,
                                    final UnpauseParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UNPAUSE_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record UnpauseIxData(Discriminator discriminator, UnpauseParams params) implements Borsh {  

    public static UnpauseIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 8;

    public static UnpauseIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UnpauseParams.read(_data, i);
      return new UnpauseIxData(discriminator, params);
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

  public static final Discriminator UPDATE_ATTESTER_MANAGER_DISCRIMINATOR = toDiscriminator(175, 245, 178, 104, 85, 179, 71, 16);

  public static List<AccountMeta> updateAttesterManagerKeys(final PublicKey ownerKey,
                                                            final PublicKey messageTransmitterKey,
                                                            final PublicKey eventAuthorityKey,
                                                            final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updateAttesterManager(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                  final PublicKey ownerKey,
                                                  final PublicKey messageTransmitterKey,
                                                  final PublicKey eventAuthorityKey,
                                                  final PublicKey programKey,
                                                  final UpdateAttesterManagerParams params) {
    final var keys = updateAttesterManagerKeys(
      ownerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return updateAttesterManager(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction updateAttesterManager(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                                  final List<AccountMeta> keys,
                                                  final UpdateAttesterManagerParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_ATTESTER_MANAGER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record UpdateAttesterManagerIxData(Discriminator discriminator, UpdateAttesterManagerParams params) implements Borsh {  

    public static UpdateAttesterManagerIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateAttesterManagerIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdateAttesterManagerParams.read(_data, i);
      return new UpdateAttesterManagerIxData(discriminator, params);
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

  public static final Discriminator UPDATE_PAUSER_DISCRIMINATOR = toDiscriminator(140, 171, 211, 132, 57, 201, 16, 254);

  public static List<AccountMeta> updatePauserKeys(final PublicKey ownerKey,
                                                   final PublicKey messageTransmitterKey,
                                                   final PublicKey eventAuthorityKey,
                                                   final PublicKey programKey) {
    return List.of(
      createReadOnlySigner(ownerKey),
      createWrite(messageTransmitterKey),
      createRead(eventAuthorityKey),
      createRead(programKey)
    );
  }

  public static Instruction updatePauser(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                         final PublicKey ownerKey,
                                         final PublicKey messageTransmitterKey,
                                         final PublicKey eventAuthorityKey,
                                         final PublicKey programKey,
                                         final UpdatePauserParams params) {
    final var keys = updatePauserKeys(
      ownerKey,
      messageTransmitterKey,
      eventAuthorityKey,
      programKey
    );
    return updatePauser(invokedMessageTransmitterV2ProgramMeta, keys, params);
  }

  public static Instruction updatePauser(final AccountMeta invokedMessageTransmitterV2ProgramMeta,
                                         final List<AccountMeta> keys,
                                         final UpdatePauserParams params) {
    final byte[] _data = new byte[8 + params.l()];
    int i = UPDATE_PAUSER_DISCRIMINATOR.write(_data, 0);
    params.write(_data, i);

    return Instruction.createInstruction(invokedMessageTransmitterV2ProgramMeta, keys, _data);
  }

  public record UpdatePauserIxData(Discriminator discriminator, UpdatePauserParams params) implements Borsh {  

    public static UpdatePauserIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdatePauserIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdatePauserParams.read(_data, i);
      return new UpdatePauserIxData(discriminator, params);
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

  private MessageTransmitterV2Program() {
  }
}
