package software.sava.idl.clients.core.gen;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;

import java.math.BigInteger;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public interface RustEnum extends SerDe {

  int ordinal();

  default String name() {
    return getClass().getSimpleName();
  }

  default int ordinalBytes() {
    return 1;
  }

  default int l() {
    return 1;
  }

  default int writeOrdinal(final byte[] data, final int offset) {
    final int ordinalBytes = ordinalBytes();
    switch (ordinalBytes) {
      case 1 -> data[offset] = (byte) ordinal();
      case 2 -> ByteUtil.putInt16LE(data, offset, (short) ordinal());
      case 4 -> ByteUtil.putInt32LE(data, offset, ordinal());
      case 8 -> ByteUtil.putInt64LE(data, offset, ordinal());
      default -> throw new UnsupportedOperationException("Unsupported enum length: " + l());
    }
    return ordinalBytes;
  }

  default int write(final byte[] data, final int offset) {
    return writeOrdinal(data, offset);
  }

  interface EnumNone extends RustEnum {

    default int write(final byte[] data, final int offset) {
      return l();
    }
  }

  interface EnumBool extends RustEnum {

    boolean val();

    default int l() {
      return ordinalBytes() + 1;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      data[i++] = (byte) (val() ? 1 : 0);
      return i - offset;
    }
  }

  interface EnumFloat32 extends RustEnum {

    float val();

    default int l() {
      return ordinalBytes() + Float.BYTES;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putFloat32LE(data, i, val());
      return (i + Float.BYTES) - offset;
    }
  }

  interface EnumFloat64 extends RustEnum {

    double val();

    default int l() {
      return ordinalBytes() + Double.BYTES;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putFloat64LE(data, i, val());
      return (i + Double.BYTES) - offset;
    }
  }

  interface EnumInt8 extends RustEnum {

    int val();

    default int l() {
      return 2;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      data[i++] = (byte) val();
      return i - offset;
    }
  }

  interface EnumInt16 extends RustEnum {

    int val();

    default int l() {
      return ordinalBytes() + Short.BYTES;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putInt16LE(data, i, (short) val());
      return (i + Short.BYTES) - offset;
    }
  }

  interface EnumInt32 extends RustEnum {

    int val();

    default int l() {
      return ordinalBytes() + Integer.BYTES;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putInt32LE(data, i, val());
      return (i + Integer.BYTES) - offset;
    }
  }

  interface EnumInt64 extends RustEnum {

    long val();

    default int l() {
      return ordinalBytes() + Long.BYTES;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putInt64LE(data, i, val());
      return (i + Long.BYTES) - offset;
    }
  }

  interface EnumInt128 extends RustEnum {

    BigInteger val();

    default int l() {
      return 129;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putInt128LE(data, i, val());
      return (i + SerDeUtil.INT128_BYTES) - offset;
    }
  }

  interface EnumInt256 extends RustEnum {

    BigInteger val();

    default int l() {
      return 257;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      ByteUtil.putInt256LE(data, i, val());
      return (i + SerDeUtil.INT256_BYTES) - offset;
    }
  }

  interface EnumBytes extends RustEnum {

    byte[] val();

    default int prefixBytes() {
      return 4;
    }

    default int l() {
      return ordinalBytes() + SerDeUtil.lenVector(prefixBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeVector(prefixBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface EnumString extends RustEnum.EnumBytes {

    String _val();
  }

  interface EnumPublicKey extends RustEnum {

    PublicKey val();

    default int l() {
      return ordinalBytes() + PublicKey.PUBLIC_KEY_LENGTH;
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += val().write(data, i);
      return i - offset;
    }
  }

  interface SerDeEnum extends RustEnum {

    SerDe val();

    default int l() {
      return ordinalBytes() + val().l();
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += val().write(data, i);
      return i - offset;
    }
  }

  interface SerDeVectorEnum extends RustEnum {

    SerDe[] val();

    default int prefixBytes() {
      return 4;
    }

    default int l() {
      return ordinalBytes() + SerDeUtil.lenVector(prefixBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeVector(prefixBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface SerDeArrayEnum extends RustEnum {

    SerDe[] val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenArray(val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeArray(val(), data, i);
      return i - offset;
    }
  }

  interface PublicKeyVectorEnum extends RustEnum {

    PublicKey[] val();

    default int prefixBytes() {
      return 4;
    }

    default int l() {
      return ordinalBytes() + SerDeUtil.lenVector(prefixBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeVector(prefixBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface PublicKeyArrayEnum extends RustEnum {

    PublicKey[] val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenArray(val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeArray(val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnum extends RustEnum {

    default int optionalBytes() {
      return 1;
    }
  }

  interface OptionalEnumBool extends OptionalEnum {

    Boolean val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumFloat32 extends OptionalEnum {

    OptionalDouble val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptionalfloat(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptionalfloat(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumFloat64 extends OptionalEnum {

    OptionalDouble val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumInt8 extends OptionalEnum {

    OptionalInt val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptionalbyte(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptionalbyte(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumInt16 extends OptionalEnum {

    OptionalInt val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptionalshort(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptionalshort(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumInt32 extends OptionalEnum {

    OptionalInt val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumInt64 extends OptionalEnum {

    OptionalLong val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumInt128 extends OptionalEnum {

    BigInteger val();

    default int l() {
      return ordinalBytes() + SerDeUtil.len128Optional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.write128Optional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumInt256 extends OptionalEnum {

    BigInteger val();

    default int l() {
      return ordinalBytes() + SerDeUtil.len256Optional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.write256Optional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumBytes extends OptionalEnum {

    byte[] val();

    default int prefixBytes() {
      return 4;
    }

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptionalVector(optionalBytes(), prefixBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptionalVector(optionalBytes(), prefixBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalEnumString extends OptionalEnum.OptionalEnumBytes {

    String _val();
  }

  interface OptionalEnumPublicKey extends OptionalEnum {

    PublicKey val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      i += SerDeUtil.writeOptional(optionalBytes(), val(), data, i);
      return i - offset;
    }
  }

  interface OptionalSerdeEnum extends OptionalEnum {

    SerDe val();

    default int l() {
      return ordinalBytes() + SerDeUtil.lenOptional(optionalBytes(), val());
    }

    default int write(final byte[] data, final int offset) {
      int i = offset + writeOrdinal(data, offset);
      return ordinalBytes() + SerDeUtil.writeOptional(optionalBytes(), val(), data, i);
    }
  }
}
