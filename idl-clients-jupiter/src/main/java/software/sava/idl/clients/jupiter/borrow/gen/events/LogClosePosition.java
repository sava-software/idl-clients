package software.sava.idl.clients.jupiter.borrow.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LogClosePosition(Discriminator discriminator,
                               PublicKey signer,
                               int positionId,
                               int vaultId,
                               PublicKey positionMint) implements VaultsEvent {

  public static final int BYTES = 78;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(225, 156, 13, 36, 189, 95, 170, 92);

  public static final int SIGNER_OFFSET = 8;
  public static final int POSITION_ID_OFFSET = 40;
  public static final int VAULT_ID_OFFSET = 44;
  public static final int POSITION_MINT_OFFSET = 46;

  public static LogClosePosition read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var signer = readPubKey(_data, i);
    i += 32;
    final var positionId = getInt32LE(_data, i);
    i += 4;
    final var vaultId = getInt16LE(_data, i);
    i += 2;
    final var positionMint = readPubKey(_data, i);
    return new LogClosePosition(discriminator,
                                signer,
                                positionId,
                                vaultId,
                                positionMint);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    signer.write(_data, i);
    i += 32;
    putInt32LE(_data, i, positionId);
    i += 4;
    putInt16LE(_data, i, vaultId);
    i += 2;
    positionMint.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
