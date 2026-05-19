package software.sava.idl.clients.phoenix.perpetuals.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// MarketEvent::SetPermission Borsh variant 45.
/// Payload type: SetPermissionEvent.
///
public record SetPermissionEvent(Discriminator discriminator,
                                 PublicKey authority,
                                 PublicKey user,
                                 long previousPermission,
                                 long newPermission,
                                 long previousExpiresAtTimestamp,
                                 long newExpiresAtTimestamp,
                                 long previousNumSignerActionsRemaining,
                                 long newNumSignerActionsRemaining,
                                 boolean created) implements EternalEvent {

  public static final int BYTES = 121;
  public static final Discriminator DISCRIMINATOR = toDiscriminator(45, 0, 0, 0, 0, 0, 0, 0);

  public static final int AUTHORITY_OFFSET = 8;
  public static final int USER_OFFSET = 40;
  public static final int PREVIOUS_PERMISSION_OFFSET = 72;
  public static final int NEW_PERMISSION_OFFSET = 80;
  public static final int PREVIOUS_EXPIRES_AT_TIMESTAMP_OFFSET = 88;
  public static final int NEW_EXPIRES_AT_TIMESTAMP_OFFSET = 96;
  public static final int PREVIOUS_NUM_SIGNER_ACTIONS_REMAINING_OFFSET = 104;
  public static final int NEW_NUM_SIGNER_ACTIONS_REMAINING_OFFSET = 112;
  public static final int CREATED_OFFSET = 120;

  public static SetPermissionEvent read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var user = readPubKey(_data, i);
    i += 32;
    final var previousPermission = getInt64LE(_data, i);
    i += 8;
    final var newPermission = getInt64LE(_data, i);
    i += 8;
    final var previousExpiresAtTimestamp = getInt64LE(_data, i);
    i += 8;
    final var newExpiresAtTimestamp = getInt64LE(_data, i);
    i += 8;
    final var previousNumSignerActionsRemaining = getInt64LE(_data, i);
    i += 8;
    final var newNumSignerActionsRemaining = getInt64LE(_data, i);
    i += 8;
    final var created = _data[i] == 1;
    return new SetPermissionEvent(discriminator,
                                  authority,
                                  user,
                                  previousPermission,
                                  newPermission,
                                  previousExpiresAtTimestamp,
                                  newExpiresAtTimestamp,
                                  previousNumSignerActionsRemaining,
                                  newNumSignerActionsRemaining,
                                  created);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    user.write(_data, i);
    i += 32;
    putInt64LE(_data, i, previousPermission);
    i += 8;
    putInt64LE(_data, i, newPermission);
    i += 8;
    putInt64LE(_data, i, previousExpiresAtTimestamp);
    i += 8;
    putInt64LE(_data, i, newExpiresAtTimestamp);
    i += 8;
    putInt64LE(_data, i, previousNumSignerActionsRemaining);
    i += 8;
    putInt64LE(_data, i, newNumSignerActionsRemaining);
    i += 8;
    _data[i] = (byte) (created ? 1 : 0);
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
