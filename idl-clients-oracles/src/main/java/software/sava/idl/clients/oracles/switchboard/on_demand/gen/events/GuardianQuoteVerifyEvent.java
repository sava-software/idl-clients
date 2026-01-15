package software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record GuardianQuoteVerifyEvent(Discriminator discriminator,
                                       PublicKey quote,
                                       PublicKey queue,
                                       PublicKey oracle) implements SbOnDemandEvent {

  public static final int BYTES = 104;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(31, 37, 39, 6, 214, 186, 33, 115);

  public static final int QUOTE_OFFSET = 8;
  public static final int QUEUE_OFFSET = 40;
  public static final int ORACLE_OFFSET = 72;

  public static GuardianQuoteVerifyEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var quote = readPubKey(_data, i);
    i += 32;
    final var queue = readPubKey(_data, i);
    i += 32;
    final var oracle = readPubKey(_data, i);
    return new GuardianQuoteVerifyEvent(discriminator, quote, queue, oracle);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    quote.write(_data, i);
    i += 32;
    queue.write(_data, i);
    i += 32;
    oracle.write(_data, i);
    i += 32;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
