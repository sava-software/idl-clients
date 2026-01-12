package software.sava.idl.clients.kamino.scope.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

import java.util.OptionalInt;

import static java.nio.charset.StandardCharsets.UTF_8;
import static software.sava.core.encoding.ByteUtil.*;

public sealed interface UpdateOracleMappingAndMetadataEntry extends RustEnum permits
    UpdateOracleMappingAndMetadataEntry.RemoveEntry,
    UpdateOracleMappingAndMetadataEntry.MappingConfig,
    UpdateOracleMappingAndMetadataEntry.MappingTwapEntry,
    UpdateOracleMappingAndMetadataEntry.MappingTwapEnabledBitmask,
    UpdateOracleMappingAndMetadataEntry.MappingRefPrice,
    UpdateOracleMappingAndMetadataEntry.MetadataName,
    UpdateOracleMappingAndMetadataEntry.MetadataMaxPriceAgeSlots,
    UpdateOracleMappingAndMetadataEntry.MetadataGroupIdsBitset {

  static UpdateOracleMappingAndMetadataEntry read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> RemoveEntry.INSTANCE;
      case 1 -> MappingConfig.read(_data, i);
      case 2 -> MappingTwapEntry.read(_data, i);
      case 3 -> MappingTwapEnabledBitmask.read(_data, i);
      case 4 -> MappingRefPrice.read(_data, i);
      case 5 -> MetadataName.read(_data, i);
      case 6 -> MetadataMaxPriceAgeSlots.read(_data, i);
      case 7 -> MetadataGroupIdsBitset.read(_data, i);
      default -> null;
    };
  }

  record RemoveEntry() implements EnumNone, UpdateOracleMappingAndMetadataEntry {

    public static final RemoveEntry INSTANCE = new RemoveEntry();

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record MappingConfig(OracleType priceType, byte[] genericData) implements UpdateOracleMappingAndMetadataEntry {

    public static final int BYTES = 21;
    public static final int GENERIC_DATA_LEN = 20;

    public static MappingConfig read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var priceType = OracleType.read(_data, i);
      i += priceType.l();
      final var genericData = new byte[20];
      SerDeUtil.readArray(genericData, _data, i);
      return new MappingConfig(priceType, genericData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += priceType.write(_data, i);
      i += SerDeUtil.writeArrayChecked(genericData, 20, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record MappingTwapEntry(OracleType priceType, int twapSource) implements UpdateOracleMappingAndMetadataEntry {

    public static final int BYTES = 3;

    public static MappingTwapEntry read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      int i = _offset;
      final var priceType = OracleType.read(_data, i);
      i += priceType.l();
      final var twapSource = getInt16LE(_data, i);
      return new MappingTwapEntry(priceType, twapSource);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = writeOrdinal(_data, _offset);
      i += priceType.write(_data, i);
      putInt16LE(_data, i, twapSource);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record MappingTwapEnabledBitmask(int val) implements EnumInt8, UpdateOracleMappingAndMetadataEntry {

    public static MappingTwapEnabledBitmask read(final byte[] _data, int i) {
      return new MappingTwapEnabledBitmask(_data[i] & 0xFF);
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }

  record MappingRefPrice(OptionalInt val) implements OptionalEnumInt16, UpdateOracleMappingAndMetadataEntry {

    public static MappingRefPrice read(final byte[] _data, int i) {
      final boolean absent = SerDeUtil.isAbsent(1, _data, i);
      i += 1;
      return new MappingRefPrice(absent ? OptionalInt.empty() : OptionalInt.of(getInt16LE(_data, i)));
    }

    @Override
    public int ordinal() {
      return 4;
    }
  }

  record MetadataName(byte[] val, java.lang.String _val) implements EnumString, UpdateOracleMappingAndMetadataEntry {

    public static MetadataName createRecord(final java.lang.String val) {
      return new MetadataName(val.getBytes(UTF_8), val);
    }

    public static MetadataName read(final byte[] data, final int _offset) {
      return createRecord(SerDeUtil.readString(4, data, _offset));
    }

    @Override
    public int ordinal() {
      return 5;
    }
  }

  record MetadataMaxPriceAgeSlots(long val) implements EnumInt64, UpdateOracleMappingAndMetadataEntry {

    public static MetadataMaxPriceAgeSlots read(final byte[] _data, int i) {
      return new MetadataMaxPriceAgeSlots(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 6;
    }
  }

  record MetadataGroupIdsBitset(long val) implements EnumInt64, UpdateOracleMappingAndMetadataEntry {

    public static MetadataGroupIdsBitset read(final byte[] _data, int i) {
      return new MetadataGroupIdsBitset(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 7;
    }
  }
}
