package software.sava.idl.clients.spl.stake;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;

import static java.util.Objects.requireNonNullElse;
import static software.sava.core.accounts.PublicKey.PUBLIC_KEY_LENGTH;
import static software.sava.core.accounts.PublicKey.readPubKey;

public record LockUp(long unixTimestamp, long epoch, PublicKey custodian) implements SerDe {

  public static final int BYTES = Long.BYTES + Long.BYTES + PUBLIC_KEY_LENGTH;

  public static final LockUp NO_LOCKUP = new LockUp(0, 0, PublicKey.NONE);

  public static LockUp read(final byte[] data, final int offset) {
    final long unixTimestamp = ByteUtil.getInt64LE(data, offset);
    final long epoch = ByteUtil.getInt64LE(data, offset + Long.BYTES);
    final var custodian = readPubKey(data, offset + Long.BYTES + Long.BYTES);
    return new LockUp(unixTimestamp, epoch, custodian);
  }

  @Override
  public int l() {
    return BYTES;
  }

  @Override
  public int write(final byte[] data, final int offset) {
    ByteUtil.putInt64LE(data, offset, unixTimestamp);
    ByteUtil.putInt64LE(data, offset + Long.BYTES, epoch);
    requireNonNullElse(custodian, PublicKey.NONE).write(data, offset + Long.BYTES + Long.BYTES);
    return BYTES;
  }
}
