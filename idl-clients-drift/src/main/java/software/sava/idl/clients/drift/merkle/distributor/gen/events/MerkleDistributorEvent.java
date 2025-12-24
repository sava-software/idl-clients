package software.sava.idl.clients.drift.merkle.distributor.gen.events;

import software.sava.idl.clients.core.gen.SerDe;

public sealed interface MerkleDistributorEvent extends SerDe permits
    NewClaimEvent,
    ClaimedEvent {

  static MerkleDistributorEvent read(final byte[] _data, final int _offset) {
    if (NewClaimEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return NewClaimEvent.read(_data, _offset);
    } else if (ClaimedEvent.DISCRIMINATOR.equals(_data, _offset)) {
      return ClaimedEvent.read(_data, _offset);
    } else {
      return null;
    }
  }

  static MerkleDistributorEvent read(final byte[] _data) {
    return read(_data, 0);
  }

  static MerkleDistributorEvent readCPI(final byte[] _data, final int _offset) {
    return read(_data, _offset + 8);
  }

  static MerkleDistributorEvent readCPI(final byte[] _data) {
    return read(_data, 8);
  }
}