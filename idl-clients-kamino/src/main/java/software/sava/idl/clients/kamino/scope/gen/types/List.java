package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;

public record List(PublicKey account,
                   int itemSize,
                   int count,
                   PublicKey newAccount,
                   int copiedCount) implements SerDe {

  public static final int BYTES = 76;

  public static final int ACCOUNT_OFFSET = 0;
  public static final int ITEM_SIZE_OFFSET = 32;
  public static final int COUNT_OFFSET = 36;
  public static final int NEW_ACCOUNT_OFFSET = 40;
  public static final int COPIED_COUNT_OFFSET = 72;

  public static List read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var account = readPubKey(_data, i);
    i += 32;
    final var itemSize = getInt32LE(_data, i);
    i += 4;
    final var count = getInt32LE(_data, i);
    i += 4;
    final var newAccount = readPubKey(_data, i);
    i += 32;
    final var copiedCount = getInt32LE(_data, i);
    return new List(account,
                    itemSize,
                    count,
                    newAccount,
                    copiedCount);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    account.write(_data, i);
    i += 32;
    putInt32LE(_data, i, itemSize);
    i += 4;
    putInt32LE(_data, i, count);
    i += 4;
    newAccount.write(_data, i);
    i += 32;
    putInt32LE(_data, i, copiedCount);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
