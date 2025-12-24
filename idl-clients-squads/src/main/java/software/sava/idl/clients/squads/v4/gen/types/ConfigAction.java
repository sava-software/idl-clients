package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface ConfigAction extends RustEnum permits
  ConfigAction.AddMember,
  ConfigAction.RemoveMember,
  ConfigAction.ChangeThreshold,
  ConfigAction.SetTimeLock,
  ConfigAction.AddSpendingLimit,
  ConfigAction.RemoveSpendingLimit,
  ConfigAction.SetRentCollector {

  static ConfigAction read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> AddMember.read(_data, i);
      case 1 -> RemoveMember.read(_data, i);
      case 2 -> ChangeThreshold.read(_data, i);
      case 3 -> SetTimeLock.read(_data, i);
      case 4 -> AddSpendingLimit.read(_data, i);
      case 5 -> RemoveSpendingLimit.read(_data, i);
      case 6 -> SetRentCollector.read(_data, i);
      default -> null;
    };
  }

  record AddMember(Member val) implements SerDeEnum, ConfigAction {

    public static AddMember read(final byte[] _data, final int _offset) {
      return new AddMember(Member.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record RemoveMember(PublicKey val) implements EnumPublicKey, ConfigAction {

    public static RemoveMember read(final byte[] _data, int i) {
      return new RemoveMember(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record ChangeThreshold(int val) implements EnumInt16, ConfigAction {

    public static ChangeThreshold read(final byte[] _data, int i) {
      return new ChangeThreshold(getInt16LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record SetTimeLock(int val) implements EnumInt32, ConfigAction {

    public static SetTimeLock read(final byte[] _data, int i) {
      return new SetTimeLock(getInt32LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  /// @param createKey Key that is used to seed the SpendingLimit PDA.
  /// @param vaultIndex The index of the vault that the spending limit is for.
  /// @param mint The token mint the spending limit is for.
  /// @param amount The amount of tokens that can be spent in a period.
  ///               This amount is in decimals of the mint,
  ///               so 1 SOL would be `1_000_000_000` and 1 USDC would be `1_000_000`.
  /// @param period The reset period of the spending limit.
  ///               When it passes, the remaining amount is reset, unless it's `Period::OneTime`.
  /// @param members Members of the multisig that can use the spending limit.
  ///                In case a member is removed from the multisig, the spending limit will remain existent
  ///                (until explicitly deleted), but the removed member will not be able to use it anymore.
  /// @param destinations The destination addresses the spending limit is allowed to sent funds to.
  ///                     If empty, funds can be sent to any address.
  record AddSpendingLimit(PublicKey createKey,
                          int vaultIndex,
                          PublicKey mint,
                          long amount,
                          Period period,
                          PublicKey[] members,
                          PublicKey[] destinations) implements ConfigAction {

    public static AddSpendingLimit read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var createKey = readPubKey(_data, i);
      i += 32;
      final var vaultIndex = _data[i] & 0xFF;
      ++i;
      final var mint = readPubKey(_data, i);
      i += 32;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var period = Period.read(_data, i);
      i += period.l();
      final var members = SerDeUtil.readPublicKeyVector(4, _data, i);
      i += SerDeUtil.lenVector(4, members);
      final var destinations = SerDeUtil.readPublicKeyVector(4, _data, i);
      return new AddSpendingLimit(createKey,
                                  vaultIndex,
                                  mint,
                                  amount,
                                  period,
                                  members,
                                  destinations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      createKey.write(_data, i);
      i += 32;
      _data[i] = (byte) vaultIndex;
      ++i;
      mint.write(_data, i);
      i += 32;
      putInt64LE(_data, i, amount);
      i += 8;
      i += period.write(_data, i);
      i += SerDeUtil.writeVector(4, members, _data, i);
      i += SerDeUtil.writeVector(4, destinations, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 32
           + 1
           + 32
           + 8
           + period.l()
           + SerDeUtil.lenVector(4, members)
           + SerDeUtil.lenVector(4, destinations);
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record RemoveSpendingLimit(PublicKey val) implements EnumPublicKey, ConfigAction {

    public static RemoveSpendingLimit read(final byte[] _data, int i) {
      return new RemoveSpendingLimit(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record SetRentCollector(PublicKey val) implements OptionalEnumPublicKey, ConfigAction {

    public static SetRentCollector read(final byte[] _data, int i) {
      return new SetRentCollector(_data[i++] == 0 ? null : readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }
}
