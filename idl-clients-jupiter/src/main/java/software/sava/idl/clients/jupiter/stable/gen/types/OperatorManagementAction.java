package software.sava.idl.clients.jupiter.stable.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

public sealed interface OperatorManagementAction extends RustEnum permits
  OperatorManagementAction.SetStatus,
  OperatorManagementAction.SetRole,
  OperatorManagementAction.ClearRole {

  static OperatorManagementAction read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> SetStatus.read(_data, i);
      case 1 -> SetRole.read(_data, i);
      case 2 -> ClearRole.read(_data, i);
      default -> null;
    };
  }

  record SetStatus(OperatorStatus val) implements SerDeEnum, OperatorManagementAction {

    public static SetStatus read(final byte[] _data, final int _offset) {
      return new SetStatus(OperatorStatus.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record SetRole(OperatorRole val) implements SerDeEnum, OperatorManagementAction {

    public static SetRole read(final byte[] _data, final int _offset) {
      return new SetRole(OperatorRole.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record ClearRole(OperatorRole val) implements SerDeEnum, OperatorManagementAction {

    public static ClearRole read(final byte[] _data, final int _offset) {
      return new ClearRole(OperatorRole.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }
}
