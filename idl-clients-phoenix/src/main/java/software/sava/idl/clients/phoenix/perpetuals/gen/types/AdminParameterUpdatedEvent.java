package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import java.util.OptionalInt;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.AdminParameterUpdateKind;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.Symbol;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::AdminParameterUpdated Borsh variant 48.
/// Payload type: AdminParameterUpdatedEvent.
///
public record AdminParameterUpdatedEvent(Discriminator discriminator,
                                         PublicKey authority,
                                         Symbol assetSymbol,
                                         OptionalInt assetId,
                                         AdminParameterUpdateKind updateKind) implements EternalEvent {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(48, 0, 0, 0, 0, 0, 0, 0);

  public static final int AUTHORITY_OFFSET = 8;
  public static final int ASSET_SYMBOL_OFFSET = 41;

  public static AdminParameterUpdatedEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final Symbol assetSymbol;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      assetSymbol = null;
      ++i;
    } else {
      ++i;
      assetSymbol = Symbol.read(_data, i);
      i += assetSymbol.l();
    }
    final OptionalInt assetId;
    if (SerDeUtil.isAbsent(1, _data, i)) {
      assetId = OptionalInt.empty();
      ++i;
    } else {
      ++i;
      assetId = OptionalInt.of(getInt32LE(_data, i));
      i += 4;
    }
    final var updateKind = AdminParameterUpdateKind.read(_data, i);
    return new AdminParameterUpdatedEvent(discriminator,
                                          authority,
                                          assetSymbol,
                                          assetId,
                                          updateKind);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    i += SerDeUtil.writeOptional(1, assetSymbol, _data, i);
    i += SerDeUtil.writeOptional(1, assetId, _data, i);
    i += updateKind.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32 + (assetSymbol == null ? 1 : (1 + assetSymbol.l())) + (assetId == null || assetId.isEmpty() ? 1 : (1 + 4)) + updateKind.l();
  }
}
