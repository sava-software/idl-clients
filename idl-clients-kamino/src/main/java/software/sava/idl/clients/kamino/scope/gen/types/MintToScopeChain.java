package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record MintToScopeChain(PublicKey mint, short[] scopeChain) implements SerDe {

  public static final int BYTES = 40;
  public static final int SCOPE_CHAIN_LEN = 4;

  public static final int MINT_OFFSET = 0;
  public static final int SCOPE_CHAIN_OFFSET = 32;

  public static MintToScopeChain read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var mint = readPubKey(_data, i);
    i += 32;
    final var scopeChain = new short[4];
    SerDeUtil.readArray(scopeChain, _data, i);
    return new MintToScopeChain(mint, scopeChain);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    mint.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(scopeChain, 4, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
