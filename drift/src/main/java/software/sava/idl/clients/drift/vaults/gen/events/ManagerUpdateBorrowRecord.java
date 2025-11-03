package software.sava.idl.clients.drift.vaults.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record ManagerUpdateBorrowRecord(Discriminator discriminator,
                                        long ts,
                                        PublicKey vault,
                                        PublicKey manager,
                                        long previousBorrowValue,
                                        long newBorrowValue,
                                        long vaultEquityBefore,
                                        long vaultEquityAfter) implements DriftVaultsEvent {

  public static final int BYTES = 112;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(138, 224, 172, 3, 244, 19, 253, 232);

  public static ManagerUpdateBorrowRecord read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createDiscriminator(_data, _offset, 8);
    int i = _offset + discriminator.length();
    final var ts = getInt64LE(_data, i);
    i += 8;
    final var vault = readPubKey(_data, i);
    i += 32;
    final var manager = readPubKey(_data, i);
    i += 32;
    final var previousBorrowValue = getInt64LE(_data, i);
    i += 8;
    final var newBorrowValue = getInt64LE(_data, i);
    i += 8;
    final var vaultEquityBefore = getInt64LE(_data, i);
    i += 8;
    final var vaultEquityAfter = getInt64LE(_data, i);
    return new ManagerUpdateBorrowRecord(discriminator,
                                         ts,
                                         vault,
                                         manager,
                                         previousBorrowValue,
                                         newBorrowValue,
                                         vaultEquityBefore,
                                         vaultEquityAfter);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    putInt64LE(_data, i, ts);
    i += 8;
    vault.write(_data, i);
    i += 32;
    manager.write(_data, i);
    i += 32;
    putInt64LE(_data, i, previousBorrowValue);
    i += 8;
    putInt64LE(_data, i, newBorrowValue);
    i += 8;
    putInt64LE(_data, i, vaultEquityBefore);
    i += 8;
    putInt64LE(_data, i, vaultEquityAfter);
    i += 8;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
