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

public record TokenRecord(PublicKey _address,
                          Key key,
                          int bump,
                          TokenState state,
                          OptionalLong ruleSetRevision,
                          PublicKey delegate,
                          TokenDelegateRole delegateRole,
                          PublicKey lockedTransfer) implements SerDe {

  public static final int KEY_OFFSET = 0;
  public static final int BUMP_OFFSET = 1;
  public static final int STATE_OFFSET = 2;
  public static final int RULE_SET_REVISION_OFFSET = 4;

  public static Filter createKeyFilter(final Key key) {
    return Filter.createMemCompFilter(KEY_OFFSET, key.write());
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static Filter createStateFilter(final TokenState state) {
    return Filter.createMemCompFilter(STATE_OFFSET, state.write());
  }

  public static Filter createRuleSetRevisionFilter(final long ruleSetRevision) {
    final byte[] _data = new byte[9];
    _data[0] = 1;
    putInt64LE(_data, 1, ruleSetRevision);
    return Filter.createMemCompFilter(RULE_SET_REVISION_OFFSET, _data);
  }

  public static TokenRecord read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static TokenRecord read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static TokenRecord read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], TokenRecord> FACTORY = TokenRecord::read;

  public static TokenRecord read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var key = Key.read(_data, i);
    i += key.l();
    final var bump = _data[i] & 0xFF;
    ++i;
    final var state = TokenState.read(_data, i);
    i += state.l();
    final OptionalLong ruleSetRevision;
    if (_data[i] == 0) {
      ruleSetRevision = OptionalLong.empty();
      ++i;
    } else {
      ++i;
      ruleSetRevision = OptionalLong.of(getInt64LE(_data, i));
      i += 8;
    }
    final PublicKey delegate;
    if (_data[i] == 0) {
      delegate = null;
      ++i;
    } else {
      ++i;
      delegate = readPubKey(_data, i);
      i += 32;
    }
    final TokenDelegateRole delegateRole;
    if (_data[i] == 0) {
      delegateRole = null;
      ++i;
    } else {
      ++i;
      delegateRole = TokenDelegateRole.read(_data, i);
      i += delegateRole.l();
    }
    final PublicKey lockedTransfer;
    if (_data[i] == 0) {
      lockedTransfer = null;
    } else {
      ++i;
      lockedTransfer = readPubKey(_data, i);
    }
    return new TokenRecord(_address,
                           key,
                           bump,
                           state,
                           ruleSetRevision,
                           delegate,
                           delegateRole,
                           lockedTransfer);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    i += key.write(_data, i);
    _data[i] = (byte) bump;
    ++i;
    i += state.write(_data, i);
    i += SerDeUtil.writeOptional(1, ruleSetRevision, _data, i);
    i += SerDeUtil.writeOptional(1, delegate, _data, i);
    i += SerDeUtil.writeOptional(1, delegateRole, _data, i);
    i += SerDeUtil.writeOptional(1, lockedTransfer, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return key.l()
         + 1
         + state.l()
         + (ruleSetRevision == null || ruleSetRevision.isEmpty() ? 1 : (1 + 8))
         + (delegate == null ? 1 : (1 + 32))
         + (delegateRole == null ? 1 : (1 + delegateRole.l()))
         + (lockedTransfer == null ? 1 : (1 + 32));
  }
}
