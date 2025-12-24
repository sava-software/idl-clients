package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface SbOnDemandEvent extends SerDe permits
    CostWhitelistEvent,
    GarbageCollectionEvent,
    GuardianQuoteVerifyEvent,
    OracleHeartbeatEvent,
    OracleInitEvent,
    OracleQuoteOverrideEvent,
    OracleQuoteRotateEvent,
    OracleQuoteVerifyRequestEvent,
    PermissionSetEvent,
    PullFeedErrorValueEvent,
    PullFeedValueEvents,
    QueueAddMrEnclaveEvent,
    QueueInitEvent,
    QueueRemoveMrEnclaveEvent,
    RandomnessCommitEvent {

  static SbOnDemandEvent read(final byte[] _data, final int _offset) {
    if (CostWhitelistEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return CostWhitelistEvent.read(_data, _offset);
    } else if (GarbageCollectionEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return GarbageCollectionEvent.read(_data, _offset);
    } else if (GuardianQuoteVerifyEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return GuardianQuoteVerifyEvent.read(_data, _offset);
    } else if (OracleHeartbeatEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleHeartbeatEvent.read(_data, _offset);
    } else if (OracleInitEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleInitEvent.read(_data, _offset);
    } else if (OracleQuoteOverrideEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleQuoteOverrideEvent.read(_data, _offset);
    } else if (OracleQuoteRotateEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleQuoteRotateEvent.read(_data, _offset);
    } else if (OracleQuoteVerifyRequestEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return OracleQuoteVerifyRequestEvent.read(_data, _offset);
    } else if (PermissionSetEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return PermissionSetEvent.read(_data, _offset);
    } else if (PullFeedErrorValueEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return PullFeedErrorValueEvent.read(_data, _offset);
    } else if (PullFeedValueEvents.DISCRIMINATOR.equals(_data, _offset)) {
      return PullFeedValueEvents.read(_data, _offset);
    } else if (QueueAddMrEnclaveEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return QueueAddMrEnclaveEvent.read(_data, _offset);
    } else if (QueueInitEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return QueueInitEvent.read(_data, _offset);
    } else if (QueueRemoveMrEnclaveEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return QueueRemoveMrEnclaveEvent.read(_data, _offset);
    } else if (RandomnessCommitEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return RandomnessCommitEvent.read(_data, _offset);
    } else {
      return null;
    }
  }

  static SbOnDemandEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static SbOnDemandEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static SbOnDemandEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}