package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import java.util.OptionalInt;

import software.sava.core.borsh.Borsh;
import software.sava.core.borsh.RustEnum;

public sealed interface CreateArgs extends RustEnum permits
  CreateArgs.V1 {

  static CreateArgs read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> V1.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [CreateArgs]", ordinal
      ));
    };
  }

  record V1(AssetData assetData,
            OptionalInt decimals,
            PrintSupply printSupply) implements CreateArgs {

    public static V1 read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var assetData = AssetData.read(_data, i);
      i += assetData.l();
      final OptionalInt decimals;
      if (_data[i] == 0) {
        decimals = OptionalInt.empty();
        ++i;
      } else {
        ++i;
        decimals = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final PrintSupply printSupply;
      if (_data[i] == 0) {
        printSupply = null;
      } else {
        ++i;
        printSupply = PrintSupply.read(_data, i);
      }
      return new V1(assetData, decimals, printSupply);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += assetData.write(_data, i);
      i += Borsh.writeOptionalbyte(decimals, _data, i);
      i += Borsh.writeOptional(printSupply, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 1 + assetData.l() + (decimals == null || decimals.isEmpty() ? 1 : (1 + 1)) + (printSupply == null ? 1 : (1 + printSupply.l()));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }
}
