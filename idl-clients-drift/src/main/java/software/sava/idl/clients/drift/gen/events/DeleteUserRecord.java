package software.sava.idl.clients.drift.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record DeleteUserRecord(Discriminator discriminator,
                               long ts,
                               PublicKey userAuthority,
                               PublicKey user,
                               int subAccountId,
                               PublicKey keeper) implements DriftEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(218, 84, 214, 163, 196, 206, 189, 250);

  public static final int TS_OFFSET = 8;
  public static final int USER_AUTHORITY_OFFSET = 16;
  public static final int USER_OFFSET = 48;
  public static final int SUB_ACCOUNT_ID_OFFSET = 80;
  public static final int KEEPER_OFFSET = 83;

  public static DeleteUserRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var userAuthority = readPubKey(_data, i);
    i += 32;
    final var user = readPubKey(_data, i);
    i += 32;
    final var subAccountId = getInt16LE(_data, i);
    i += 2;
    final PublicKey keeper;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      keeper = null;
    } else {
      ++i;
      keeper = readPubKey(_data, i);
    }
    return new DeleteUserRecord(discriminator,
                                ts,
                                userAuthority,
                                user,
                                subAccountId,
                                keeper);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    userAuthority.write(_data, i);
    i += 32;
    user.write(_data, i);
    i += 32;
    putInt16LE(_data, i, subAccountId);
    i += 2;
    i += SerDeUtil.writeOptional(1, keeper, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return discriminator.length() + 8
         + 32
         + 32
         + 2
         + (keeper == null ? 1 : (1 + 32));
  }
}
