package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.OptionalLong;
import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public record ReservationListV1(PublicKey _address,
                                Key key,
                                PublicKey masterEdition,
                                OptionalLong supplySnapshot,
                                ReservationV1[] reservations) implements SerDe {

  public static final int KEY_OFFSET = 0;
  public static final int MASTER_EDITION_OFFSET = 1;
  public static final int SUPPLY_SNAPSHOT_OFFSET = 34;

  public static Filter createKeyFilter(final Key key) {
    return Filter.createMemCompFilter(KEY_OFFSET, key.write());
  }

  public static Filter createMasterEditionFilter(final PublicKey masterEdition) {
    return Filter.createMemCompFilter(MASTER_EDITION_OFFSET, masterEdition);
  }

  public static Filter createSupplySnapshotFilter(final long supplySnapshot) {
    final byte[] _data = new byte[9];
    _data[0] = 1;
    putInt64LE(_data, 1, supplySnapshot);
    return Filter.createMemCompFilter(SUPPLY_SNAPSHOT_OFFSET, _data);
  }

  public static ReservationListV1 read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ReservationListV1 read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ReservationListV1 read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ReservationListV1> FACTORY = ReservationListV1::read;

  public static ReservationListV1 read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = Key.read(_data, i);
    i += key.l();
    final var masterEdition = readPubKey(_data, i);
    i += 32;
    final OptionalLong supplySnapshot;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      supplySnapshot = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      supplySnapshot = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final var reservations = SerDeUtil.readVector(4, ReservationV1.class, ReservationV1::read, _data, i);
    return new ReservationListV1(_address,
                                 key,
                                 masterEdition,
                                 supplySnapshot,
                                 reservations);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += key.write(_data, i);
    masterEdition.write(_data, i);
    i += 32;
    i += SerDeUtil.writeOptional(1, supplySnapshot, _data, i);
    i += SerDeUtil.writeVector(4, reservations, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return key.l() + 32 + (supplySnapshot == null || supplySnapshot.isEmpty() ? 1 : (1 + 8)) + SerDeUtil.lenVector(4, reservations);
  }
}
