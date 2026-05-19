package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Fixed EscrowHeader prefix for a Phoenix Eternal escrow account.
/// The dynamic escrow request list follows this header and is left as trailing account data by generic decoders.
///
public record EscrowHeader(PublicKey _address,
                           Discriminator discriminator,
                           long discriminant,
                           PublicKey authority,
                           PublicKey funderKey,
                           SequenceNumber sequenceNumber,
                           long len,
                           long capacity) implements SerDe {

  public static final int BYTES = 112;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(50, 4, 48, 59, 209, 138, 251, 204);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int DISCRIMINANT_OFFSET = 8;
  public static final int AUTHORITY_OFFSET = 16;
  public static final int FUNDER_KEY_OFFSET = 48;
  public static final int SEQUENCE_NUMBER_OFFSET = 80;
  public static final int LEN_OFFSET = 96;
  public static final int CAPACITY_OFFSET = 104;

  public static Filter createDiscriminantFilter(final long discriminant) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, discriminant);
    return Filter.createMemCompFilter(DISCRIMINANT_OFFSET, _data);
  }

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createFunderKeyFilter(final PublicKey funderKey) {
    return Filter.createMemCompFilter(FUNDER_KEY_OFFSET, funderKey);
  }

  public static Filter createSequenceNumberFilter(final SequenceNumber sequenceNumber) {
    return Filter.createMemCompFilter(SEQUENCE_NUMBER_OFFSET, sequenceNumber.write());
  }

  public static Filter createLenFilter(final long len) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, len);
    return Filter.createMemCompFilter(LEN_OFFSET, _data);
  }

  public static Filter createCapacityFilter(final long capacity) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, capacity);
    return Filter.createMemCompFilter(CAPACITY_OFFSET, _data);
  }

  public static EscrowHeader read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static EscrowHeader read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static EscrowHeader read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], EscrowHeader> FACTORY = EscrowHeader::read;

  public static EscrowHeader read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var discriminant = getInt64LE(_data, i);
    i += 8;
    final var authority = readPubKey(_data, i);
    i += 32;
    final var funderKey = readPubKey(_data, i);
    i += 32;
    final var sequenceNumber = SequenceNumber.read(_data, i);
    i += sequenceNumber.l();
    final var len = getInt64LE(_data, i);
    i += 8;
    final var capacity = getInt64LE(_data, i);
    return new EscrowHeader(_address,
                            discriminator,
                            discriminant,
                            authority,
                            funderKey,
                            sequenceNumber,
                            len,
                            capacity);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, discriminant);
    i += 8;
    authority.write(_data, i);
    i += 32;
    funderKey.write(_data, i);
    i += 32;
    i += sequenceNumber.write(_data, i);
    putInt64LE(_data, i, len);
    i += 8;
    putInt64LE(_data, i, capacity);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
