package software.sava.idl.clients.squads.v4.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;

import static software.sava.core.encoding.ByteUtil.getInt64LE;

  /// The status of a proposal.
  /// Each variant wraps a timestamp of when the status was set.
public sealed interface ProposalStatus extends RustEnum permits
  ProposalStatus.Draft,
  ProposalStatus.Active,
  ProposalStatus.Rejected,
  ProposalStatus.Approved,
  ProposalStatus.Executing,
  ProposalStatus.Executed,
  ProposalStatus.Cancelled {

  static ProposalStatus read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Draft.read(_data, i);
      case 1 -> Active.read(_data, i);
      case 2 -> Rejected.read(_data, i);
      case 3 -> Approved.read(_data, i);
      case 4 -> Executing.INSTANCE;
      case 5 -> Executed.read(_data, i);
      case 6 -> Cancelled.read(_data, i);
      default -> null;
    };
  }

  record Draft(long val) implements EnumInt64, ProposalStatus {

    public static Draft read(final byte[] _data, int i) {
      return new Draft(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Active(long val) implements EnumInt64, ProposalStatus {

    public static Active read(final byte[] _data, int i) {
      return new Active(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record Rejected(long val) implements EnumInt64, ProposalStatus {

    public static Rejected read(final byte[] _data, int i) {
      return new Rejected(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record Approved(long val) implements EnumInt64, ProposalStatus {

    public static Approved read(final byte[] _data, int i) {
      return new Approved(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record Executing() implements EnumNone, ProposalStatus {

    public static final Executing INSTANCE = new Executing();

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record Executed(long val) implements EnumInt64, ProposalStatus {

    public static Executed read(final byte[] _data, int i) {
      return new Executed(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record Cancelled(long val) implements EnumInt64, ProposalStatus {

    public static Cancelled read(final byte[] _data, int i) {
      return new Cancelled(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }
}
