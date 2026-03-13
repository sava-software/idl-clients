package software.sava.idl.clients.jupiter.stable.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record Operator(PublicKey _address,
                       Discriminator discriminator,
                       PublicKey operatorAuthority,
                       long role,
                       OperatorStatus status,
                       byte[] padding0,
                       byte[] reserved) implements SerDe {

  public static final int BYTES = 184;
  public static final int PADDING_0_LEN = 7;
  public static final int RESERVED_LEN = 128;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(219, 31, 188, 145, 69, 139, 204, 117);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int OPERATOR_AUTHORITY_OFFSET = 8;
  public static final int ROLE_OFFSET = 40;
  public static final int STATUS_OFFSET = 48;
  public static final int PADDING_0_OFFSET = 49;
  public static final int RESERVED_OFFSET = 56;

  public static Filter createOperatorAuthorityFilter(final PublicKey operatorAuthority) {
    return Filter.createMemCompFilter(OPERATOR_AUTHORITY_OFFSET, operatorAuthority);
  }

  public static Filter createRoleFilter(final long role) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, role);
    return Filter.createMemCompFilter(ROLE_OFFSET, _data);
  }

  public static Filter createStatusFilter(final OperatorStatus status) {
    return Filter.createMemCompFilter(STATUS_OFFSET, status.write());
  }

  public static Operator read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Operator read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Operator read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Operator> FACTORY = Operator::read;

  public static Operator read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var operatorAuthority = readPubKey(_data, i);
    i += 32;
    final var role = getInt64LE(_data, i);
    i += 8;
    final var status = OperatorStatus.read(_data, i);
    i += status.l();
    final var padding0 = new byte[7];
    i += SerDeUtil.readArray(padding0, _data, i);
    final var reserved = new byte[128];
    SerDeUtil.readArray(reserved, _data, i);
    return new Operator(_address,
                        discriminator,
                        operatorAuthority,
                        role,
                        status,
                        padding0,
                        reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    operatorAuthority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, role);
    i += 8;
    i += status.write(_data, i);
    i += SerDeUtil.writeArrayChecked(padding0, 7, _data, i);
    i += SerDeUtil.writeArrayChecked(reserved, 128, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
