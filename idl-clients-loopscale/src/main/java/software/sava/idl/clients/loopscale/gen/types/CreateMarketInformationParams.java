package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record CreateMarketInformationParams(PublicKey principalMint, PublicKey authority) implements SerDe {

  public static final int BYTES = 64;

  public static final int PRINCIPAL_MINT_OFFSET = 0;
  public static final int AUTHORITY_OFFSET = 32;

  public static CreateMarketInformationParams read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var principalMint = readPubKey(_data, i);
    i += 32;
    final var authority = readPubKey(_data, i);
    return new CreateMarketInformationParams(principalMint, authority);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    principalMint.write(_data, i);
    i += 32;
    authority.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
