package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.borsh.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

public sealed interface DelegateArgs extends RustEnum permits
  DelegateArgs.CollectionV1,
  DelegateArgs.SaleV1,
  DelegateArgs.TransferV1,
  DelegateArgs.DataV1,
  DelegateArgs.UtilityV1,
  DelegateArgs.StakingV1,
  DelegateArgs.StandardV1,
  DelegateArgs.LockedTransferV1,
  DelegateArgs.ProgrammableConfigV1,
  DelegateArgs.AuthorityItemV1,
  DelegateArgs.DataItemV1,
  DelegateArgs.CollectionItemV1,
  DelegateArgs.ProgrammableConfigItemV1,
  DelegateArgs.PrintDelegateV1 {

  static DelegateArgs read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> CollectionV1.read(_data, i);
      case 1 -> SaleV1.read(_data, i);
      case 2 -> TransferV1.read(_data, i);
      case 3 -> DataV1.read(_data, i);
      case 4 -> UtilityV1.read(_data, i);
      case 5 -> StakingV1.read(_data, i);
      case 6 -> StandardV1.read(_data, i);
      case 7 -> LockedTransferV1.read(_data, i);
      case 8 -> ProgrammableConfigV1.read(_data, i);
      case 9 -> AuthorityItemV1.read(_data, i);
      case 10 -> DataItemV1.read(_data, i);
      case 11 -> CollectionItemV1.read(_data, i);
      case 12 -> ProgrammableConfigItemV1.read(_data, i);
      case 13 -> PrintDelegateV1.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [DelegateArgs]", ordinal
      ));
    };
  }

  record CollectionV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static CollectionV1 read(final byte[] _data, final int _offset) {
      return new CollectionV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record SaleV1(long amount, AuthorizationData authorizationData) implements DelegateArgs {

    public static SaleV1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final AuthorizationData authorizationData;
      if (_data[i] == 0) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new SaleV1(amount, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += Borsh.writeOptional(authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 8 + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record TransferV1(long amount, AuthorizationData authorizationData) implements DelegateArgs {

    public static TransferV1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final AuthorizationData authorizationData;
      if (_data[i] == 0) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new TransferV1(amount, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += Borsh.writeOptional(authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 8 + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record DataV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static DataV1 read(final byte[] _data, final int _offset) {
      return new DataV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record UtilityV1(long amount, AuthorizationData authorizationData) implements DelegateArgs {

    public static UtilityV1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final AuthorizationData authorizationData;
      if (_data[i] == 0) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new UtilityV1(amount, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += Borsh.writeOptional(authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 8 + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record StakingV1(long amount, AuthorizationData authorizationData) implements DelegateArgs {

    public static StakingV1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final AuthorizationData authorizationData;
      if (_data[i] == 0) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new StakingV1(amount, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      i += Borsh.writeOptional(authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 8 + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record StandardV1(long val) implements EnumInt64, DelegateArgs {

    public static StandardV1 read(final byte[] _data, int i) {
      return new StandardV1(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }

  record LockedTransferV1(long amount,
                          PublicKey lockedAddress,
                          AuthorizationData authorizationData) implements DelegateArgs {

    public static LockedTransferV1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var lockedAddress = readPubKey(_data, i);
      i += 32;
      final AuthorizationData authorizationData;
      if (_data[i] == 0) {
        authorizationData = null;
      } else {
        ++i;
        authorizationData = AuthorizationData.read(_data, i);
      }
      return new LockedTransferV1(amount, lockedAddress, authorizationData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      lockedAddress.write(_data, i);
      i += 32;
      i += Borsh.writeOptional(authorizationData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + 8 + 32 + (authorizationData == null ? 1 : (1 + authorizationData.l()));
    }

    @Override
    public int ordinal() {
      return 7;
    }
  }

  record ProgrammableConfigV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static ProgrammableConfigV1 read(final byte[] _data, final int _offset) {
      return new ProgrammableConfigV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 8;
    }
  }

  record AuthorityItemV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static AuthorityItemV1 read(final byte[] _data, final int _offset) {
      return new AuthorityItemV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 9;
    }
  }

  record DataItemV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static DataItemV1 read(final byte[] _data, final int _offset) {
      return new DataItemV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 10;
    }
  }

  record CollectionItemV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static CollectionItemV1 read(final byte[] _data, final int _offset) {
      return new CollectionItemV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 11;
    }
  }

  record ProgrammableConfigItemV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static ProgrammableConfigItemV1 read(final byte[] _data, final int _offset) {
      return new ProgrammableConfigItemV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 12;
    }
  }

  record PrintDelegateV1(AuthorizationData val) implements BorshEnum, DelegateArgs {

    public static PrintDelegateV1 read(final byte[] _data, final int _offset) {
      return new PrintDelegateV1(AuthorizationData.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 13;
    }
  }
}
