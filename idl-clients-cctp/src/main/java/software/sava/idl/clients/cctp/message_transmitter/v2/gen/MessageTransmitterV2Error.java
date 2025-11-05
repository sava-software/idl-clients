package software.sava.idl.clients.cctp.message_transmitter.v2.gen;

import software.sava.idl.clients.core.gen.ProgramError;

public sealed interface MessageTransmitterV2Error extends ProgramError permits
    MessageTransmitterV2Error.InvalidAuthority,
    MessageTransmitterV2Error.ProgramPaused,
    MessageTransmitterV2Error.InvalidMessageTransmitterState,
    MessageTransmitterV2Error.InvalidSignatureThreshold,
    MessageTransmitterV2Error.SignatureThresholdAlreadySet,
    MessageTransmitterV2Error.InvalidOwner,
    MessageTransmitterV2Error.InvalidPauser,
    MessageTransmitterV2Error.InvalidAttesterManager,
    MessageTransmitterV2Error.InvalidAttester,
    MessageTransmitterV2Error.AttesterAlreadyEnabled,
    MessageTransmitterV2Error.TooFewEnabledAttesters,
    MessageTransmitterV2Error.SignatureThresholdTooLow,
    MessageTransmitterV2Error.AttesterAlreadyDisabled,
    MessageTransmitterV2Error.MessageBodyLimitExceeded,
    MessageTransmitterV2Error.InvalidDestinationCaller,
    MessageTransmitterV2Error.InvalidRecipient,
    MessageTransmitterV2Error.SenderNotPermitted,
    MessageTransmitterV2Error.InvalidSourceDomain,
    MessageTransmitterV2Error.InvalidDestinationDomain,
    MessageTransmitterV2Error.InvalidMessageVersion,
    MessageTransmitterV2Error.InvalidUsedNoncesAccount,
    MessageTransmitterV2Error.InvalidRecipientProgram,
    MessageTransmitterV2Error.InvalidNonce,
    MessageTransmitterV2Error.NonceAlreadyUsed,
    MessageTransmitterV2Error.MessageTooShort,
    MessageTransmitterV2Error.MalformedMessage,
    MessageTransmitterV2Error.InvalidSignatureOrderOrDupe,
    MessageTransmitterV2Error.InvalidAttesterSignature,
    MessageTransmitterV2Error.InvalidAttestationLength,
    MessageTransmitterV2Error.InvalidSignatureRecoveryId,
    MessageTransmitterV2Error.InvalidSignatureSValue,
    MessageTransmitterV2Error.InvalidMessageHash,
    MessageTransmitterV2Error.InvalidDestinationMessage,
    MessageTransmitterV2Error.EventAccountWindowNotExpired,
    MessageTransmitterV2Error.DestinationDomainIsLocalDomain {

  static MessageTransmitterV2Error getInstance(final int errorCode) {
    return switch (errorCode) {
      case 6000 -> InvalidAuthority.INSTANCE;
      case 6001 -> ProgramPaused.INSTANCE;
      case 6002 -> InvalidMessageTransmitterState.INSTANCE;
      case 6003 -> InvalidSignatureThreshold.INSTANCE;
      case 6004 -> SignatureThresholdAlreadySet.INSTANCE;
      case 6005 -> InvalidOwner.INSTANCE;
      case 6006 -> InvalidPauser.INSTANCE;
      case 6007 -> InvalidAttesterManager.INSTANCE;
      case 6008 -> InvalidAttester.INSTANCE;
      case 6009 -> AttesterAlreadyEnabled.INSTANCE;
      case 6010 -> TooFewEnabledAttesters.INSTANCE;
      case 6011 -> SignatureThresholdTooLow.INSTANCE;
      case 6012 -> AttesterAlreadyDisabled.INSTANCE;
      case 6013 -> MessageBodyLimitExceeded.INSTANCE;
      case 6014 -> InvalidDestinationCaller.INSTANCE;
      case 6015 -> InvalidRecipient.INSTANCE;
      case 6016 -> SenderNotPermitted.INSTANCE;
      case 6017 -> InvalidSourceDomain.INSTANCE;
      case 6018 -> InvalidDestinationDomain.INSTANCE;
      case 6019 -> InvalidMessageVersion.INSTANCE;
      case 6020 -> InvalidUsedNoncesAccount.INSTANCE;
      case 6021 -> InvalidRecipientProgram.INSTANCE;
      case 6022 -> InvalidNonce.INSTANCE;
      case 6023 -> NonceAlreadyUsed.INSTANCE;
      case 6024 -> MessageTooShort.INSTANCE;
      case 6025 -> MalformedMessage.INSTANCE;
      case 6026 -> InvalidSignatureOrderOrDupe.INSTANCE;
      case 6027 -> InvalidAttesterSignature.INSTANCE;
      case 6028 -> InvalidAttestationLength.INSTANCE;
      case 6029 -> InvalidSignatureRecoveryId.INSTANCE;
      case 6030 -> InvalidSignatureSValue.INSTANCE;
      case 6031 -> InvalidMessageHash.INSTANCE;
      case 6032 -> InvalidDestinationMessage.INSTANCE;
      case 6033 -> EventAccountWindowNotExpired.INSTANCE;
      case 6034 -> DestinationDomainIsLocalDomain.INSTANCE;
      default -> null;
    };
  }

  record InvalidAuthority(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidAuthority INSTANCE = new InvalidAuthority(
        6000, "Invalid authority"
    );
  }

  record ProgramPaused(int code, String msg) implements MessageTransmitterV2Error {

    public static final ProgramPaused INSTANCE = new ProgramPaused(
        6001, "Instruction is not allowed at this time"
    );
  }

  record InvalidMessageTransmitterState(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidMessageTransmitterState INSTANCE = new InvalidMessageTransmitterState(
        6002, "Invalid message transmitter state"
    );
  }

  record InvalidSignatureThreshold(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidSignatureThreshold INSTANCE = new InvalidSignatureThreshold(
        6003, "Invalid signature threshold"
    );
  }

  record SignatureThresholdAlreadySet(int code, String msg) implements MessageTransmitterV2Error {

    public static final SignatureThresholdAlreadySet INSTANCE = new SignatureThresholdAlreadySet(
        6004, "Signature threshold already set"
    );
  }

  record InvalidOwner(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidOwner INSTANCE = new InvalidOwner(
        6005, "Invalid owner"
    );
  }

  record InvalidPauser(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidPauser INSTANCE = new InvalidPauser(
        6006, "Invalid pauser"
    );
  }

  record InvalidAttesterManager(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidAttesterManager INSTANCE = new InvalidAttesterManager(
        6007, "Invalid attester manager"
    );
  }

  record InvalidAttester(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidAttester INSTANCE = new InvalidAttester(
        6008, "Invalid attester"
    );
  }

  record AttesterAlreadyEnabled(int code, String msg) implements MessageTransmitterV2Error {

    public static final AttesterAlreadyEnabled INSTANCE = new AttesterAlreadyEnabled(
        6009, "Attester already enabled"
    );
  }

  record TooFewEnabledAttesters(int code, String msg) implements MessageTransmitterV2Error {

    public static final TooFewEnabledAttesters INSTANCE = new TooFewEnabledAttesters(
        6010, "Too few enabled attesters"
    );
  }

  record SignatureThresholdTooLow(int code, String msg) implements MessageTransmitterV2Error {

    public static final SignatureThresholdTooLow INSTANCE = new SignatureThresholdTooLow(
        6011, "Signature threshold is too low"
    );
  }

  record AttesterAlreadyDisabled(int code, String msg) implements MessageTransmitterV2Error {

    public static final AttesterAlreadyDisabled INSTANCE = new AttesterAlreadyDisabled(
        6012, "Attester already disabled"
    );
  }

  record MessageBodyLimitExceeded(int code, String msg) implements MessageTransmitterV2Error {

    public static final MessageBodyLimitExceeded INSTANCE = new MessageBodyLimitExceeded(
        6013, "Message body exceeds max size"
    );
  }

  record InvalidDestinationCaller(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidDestinationCaller INSTANCE = new InvalidDestinationCaller(
        6014, "Invalid destination caller"
    );
  }

  record InvalidRecipient(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidRecipient INSTANCE = new InvalidRecipient(
        6015, "Invalid message recipient"
    );
  }

  record SenderNotPermitted(int code, String msg) implements MessageTransmitterV2Error {

    public static final SenderNotPermitted INSTANCE = new SenderNotPermitted(
        6016, "Sender is not permitted"
    );
  }

  record InvalidSourceDomain(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidSourceDomain INSTANCE = new InvalidSourceDomain(
        6017, "Invalid source domain"
    );
  }

  record InvalidDestinationDomain(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidDestinationDomain INSTANCE = new InvalidDestinationDomain(
        6018, "Invalid destination domain"
    );
  }

  record InvalidMessageVersion(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidMessageVersion INSTANCE = new InvalidMessageVersion(
        6019, "Invalid message version"
    );
  }

  record InvalidUsedNoncesAccount(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidUsedNoncesAccount INSTANCE = new InvalidUsedNoncesAccount(
        6020, "Invalid used nonces account"
    );
  }

  record InvalidRecipientProgram(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidRecipientProgram INSTANCE = new InvalidRecipientProgram(
        6021, "Invalid recipient program"
    );
  }

  record InvalidNonce(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidNonce INSTANCE = new InvalidNonce(
        6022, "Invalid nonce"
    );
  }

  record NonceAlreadyUsed(int code, String msg) implements MessageTransmitterV2Error {

    public static final NonceAlreadyUsed INSTANCE = new NonceAlreadyUsed(
        6023, "Nonce already used"
    );
  }

  record MessageTooShort(int code, String msg) implements MessageTransmitterV2Error {

    public static final MessageTooShort INSTANCE = new MessageTooShort(
        6024, "Message is too short"
    );
  }

  record MalformedMessage(int code, String msg) implements MessageTransmitterV2Error {

    public static final MalformedMessage INSTANCE = new MalformedMessage(
        6025, "Malformed message"
    );
  }

  record InvalidSignatureOrderOrDupe(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidSignatureOrderOrDupe INSTANCE = new InvalidSignatureOrderOrDupe(
        6026, "Invalid signature order or dupe"
    );
  }

  record InvalidAttesterSignature(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidAttesterSignature INSTANCE = new InvalidAttesterSignature(
        6027, "Invalid attester signature"
    );
  }

  record InvalidAttestationLength(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidAttestationLength INSTANCE = new InvalidAttestationLength(
        6028, "Invalid attestation length"
    );
  }

  record InvalidSignatureRecoveryId(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidSignatureRecoveryId INSTANCE = new InvalidSignatureRecoveryId(
        6029, "Invalid signature recovery ID"
    );
  }

  record InvalidSignatureSValue(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidSignatureSValue INSTANCE = new InvalidSignatureSValue(
        6030, "Invalid signature S value"
    );
  }

  record InvalidMessageHash(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidMessageHash INSTANCE = new InvalidMessageHash(
        6031, "Invalid message hash"
    );
  }

  record InvalidDestinationMessage(int code, String msg) implements MessageTransmitterV2Error {

    public static final InvalidDestinationMessage INSTANCE = new InvalidDestinationMessage(
        6032, "Invalid destination message"
    );
  }

  record EventAccountWindowNotExpired(int code, String msg) implements MessageTransmitterV2Error {

    public static final EventAccountWindowNotExpired INSTANCE = new EventAccountWindowNotExpired(
        6033, "Event account window not expired"
    );
  }

  record DestinationDomainIsLocalDomain(int code, String msg) implements MessageTransmitterV2Error {

    public static final DestinationDomainIsLocalDomain INSTANCE = new DestinationDomainIsLocalDomain(
        6034, "Destination domain is local domain"
    );
  }
}
