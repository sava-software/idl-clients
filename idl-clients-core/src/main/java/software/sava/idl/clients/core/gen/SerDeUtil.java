package software.sava.idl.clients.core.gen;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public final class SerDeUtil {

  public static byte[] checkMaxLength(final byte[] bytes, final int maxLength) {
    if (bytes.length > maxLength) {
      throw new IllegalStateException(String.format(
          "[length=%d] cannot be greater than %d.",
          bytes.length, maxLength
      ));
    }
    return bytes;
  }

  public static int val(final int prefixBytes, final byte[] data, final int offset) {
    return switch (prefixBytes) {
      case 1 -> data[offset] & 0xFF;
      case 2 -> ByteUtil.getInt16LE(data, offset);
      case 4 -> ByteUtil.getInt32LE(data, offset);
      case 8 -> Math.toIntExact(ByteUtil.getInt64LE(data, offset));
      default -> throw new IllegalArgumentException("Invalid prefix bytes: " + prefixBytes);
    };
  }

  private static void writeVal(final int prefixBytes, final int val, final byte[] data, final int offset) {
    switch (prefixBytes) {
      case 1 -> data[offset] = (byte) val;
      case 2 -> ByteUtil.putInt16LE(data, offset, val);
      case 4 -> ByteUtil.putInt32LE(data, offset, val);
      case 8 -> ByteUtil.putInt64LE(data, offset, val);
      default -> throw new IllegalArgumentException("Invalid prefix bytes: " + prefixBytes);
    }
  }

  public static boolean isAbsent(final int optionalBytes, final byte[] data, final int offset) {
    return val(optionalBytes, data, offset) == 0;
  }

  public static boolean isPresent(final int optionalBytes, final byte[] data, final int offset) {
    return val(optionalBytes, data, offset) != 0;
  }

  // Bytes

  public static int readArray(final byte[] result, final byte[] data, final int offset) {
    System.arraycopy(data, offset, result, 0, result.length);
    return result.length;
  }

  public static int readArray(final byte[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static byte[] readbyteVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new byte[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static byte[][] readMultiDimensionbyteVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new byte[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readbyteVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static byte[][] readMultiDimensionbyteVectorArray(final int prefixBytes,
                                                           final int fixedLength,
                                                           final byte[] data,
                                                           final int offset) {
    final int len = val(prefixBytes, data, offset);
    final byte[][] result = new byte[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int writeOptionalbyte(final int optionalBytes,
                                      final OptionalInt val,
                                      final byte[] data,
                                      final int offset) {
    if (val == null || val.isEmpty()) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      data[offset + optionalBytes] = (byte) val.getAsInt();
      return optionalBytes + 1;
    }
  }

  public static int writeOptional(final int optionalBytes, final Byte val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      data[offset + optionalBytes] = val;
      return optionalBytes + 1;
    }
  }

  public static int writeArray(final byte[] array, final byte[] data, final int offset) {
    System.arraycopy(array, 0, data, offset, array.length);
    return array.length;
  }

  public static int writeArrayChecked(final byte[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final byte[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeOptionalArray(final int optionalBytes,
                                       final byte[] bytes,
                                       final byte[] data,
                                       final int offset) {
    if (bytes == null || bytes.length == 0) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      return optionalBytes + writeArray(bytes, data, offset + optionalBytes);
    }
  }

  public static int writeOptionalVector(final int optionalBytes,
                                        final int prefixBytes,
                                        final byte[] bytes,
                                        final byte[] data,
                                        final int offset) {
    if (bytes == null || bytes.length == 0) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      return optionalBytes + writeVector(prefixBytes, bytes, data, offset + optionalBytes);
    }
  }

  public static int writeArray(final byte[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final byte[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final byte[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final byte[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final byte[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes, final byte[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Byte val) {
    return val == null ? optionalBytes : optionalBytes + 1;
  }

  public static int lenOptionalbyte(final int optionalBytes, final OptionalInt val) {
    return val == null || val.isEmpty() ? optionalBytes : optionalBytes + 1;
  }

  public static int lenArray(final byte[] array) {
    return array.length;
  }

  public static int lenVector(final int prefixBytes, final byte[] array) {
    return prefixBytes + array.length;
  }

  public static int lenOptionalVector(final int optionalBytes, final int prefixBytes, final byte[] array) {
    if (array == null || array.length == 0) {
      return optionalBytes;
    } else {
      return optionalBytes + lenVector(prefixBytes, array);
    }
  }

  public static int lenArray(final byte[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final byte[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final byte[][] array) {
    return prefixBytes + lenArray(array);
  }

  // Strings

  public static String readString(final int prefixBytes,
                                  final byte[] data,
                                  final int offset,
                                  final Charset charset) {
    final int len = val(prefixBytes, data, offset);
    return new String(data, offset + prefixBytes, len, charset);
  }

  public static String readString(final int prefixBytes,
                                  final byte[] data,
                                  final int offset) {
    final int len = val(prefixBytes, data, offset);
    return new String(data, offset + prefixBytes, len, StandardCharsets.UTF_8);
  }

  public static byte[] getBytes(final String str, final Charset charset) {
    return str == null || str.isBlank() ? null : str.getBytes(charset);
  }

  public static byte[][] getBytes(final String[] strings, final Charset charset) {
    final int len = strings.length;
    final byte[][] bytes = new byte[len][];
    for (int i = 0; i < len; ++i) {
      bytes[i] = getBytes(strings[i], charset);
    }
    return bytes;
  }

  public static int len(final int prefix, final String val, final Charset charset) {
    final int len = val.getBytes(charset).length;
    return prefix + len;
  }

  public static int lenOptional(final int optionalPrefix,
                                final int stringPrefix,
                                final String val,
                                final Charset charset) {
    return val == null ? optionalPrefix : optionalPrefix + len(stringPrefix, val, charset);
  }

  public static int lenVector(final int vectorPrefix,
                              final int stringPrefix,
                              final String[] array,
                              final Charset charset) {
    int len = vectorPrefix;
    for (final var s : array) {
      len += len(stringPrefix, s, charset);
    }
    return len;
  }

  public static int lenVector(final int vectorPrefix,
                              final int stringPrefix,
                              final String[][] array,
                              final Charset charset) {
    int len = vectorPrefix;
    for (final var a : array) {
      len += lenVector(vectorPrefix, stringPrefix, a, charset);
    }
    return len;
  }

  public static int readArray(final int stringPrefix,
                              final String[] result,
                              final byte[] data,
                              final int offset,
                              final Charset charset) {
    int o = offset;
    String s;
    for (int i = 0, len; i < result.length; ++i) {
      len = val(stringPrefix, data, o);
      o += stringPrefix;
      s = new String(data, o, len, charset);
      result[i] = s;
      o += len;
    }
    return o - offset;
  }

  public static String[] readStringVector(final int vectorPrefix,
                                          final int stringPrefix,
                                          final byte[] data,
                                          final int offset,
                                          final Charset charset) {
    final int len = val(vectorPrefix, data, offset);
    final var result = new String[len];
    readArray(stringPrefix, result, data, offset + vectorPrefix, charset);
    return result;
  }

  public static String[][] readMultiDimensionStringVector(final int vectorPrefix,
                                                          final int stringPrefix,
                                                          final byte[] data,
                                                          int offset,
                                                          final Charset charset) {
    int len = val(vectorPrefix, data, offset);
    offset += vectorPrefix;
    final var result = new String[len][];
    for (int i = 0; i < result.length; ++i) {
      len = val(vectorPrefix, data, offset);
      offset += vectorPrefix;
      final var stringArray = new String[len];
      offset += readArray(stringPrefix, stringArray, data, offset, charset);
      result[i] = stringArray;
    }
    return result;
  }


  public static int readArray(final int stringPrefix,
                              final String[][] result,
                              final byte[] data,
                              final int offset,
                              final Charset charset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(stringPrefix, out, data, i, charset);
    }
    return i - offset;
  }

  public static String[][] readMultiDimensionStringVectorArray(final int vectorPrefix,
                                                               final int fixedLength,
                                                               final int stringPrefix,
                                                               final byte[] data,
                                                               final int offset,
                                                               final Charset charset) {
    final int len = val(vectorPrefix, data, offset);
    final var result = new String[len][fixedLength];
    readArray(stringPrefix, result, data, offset + vectorPrefix, charset);
    return result;
  }


  public static int write(final int stringPrefix,
                          final String str,
                          final byte[] data,
                          final int offset,
                          final Charset charset) {
    return writeVector(stringPrefix, str.getBytes(charset), data, offset);
  }

  public static int writeArray(final int stringPrefix,
                               final String[] array,
                               final byte[] data,
                               final int offset,
                               final Charset charset) {
    int i = offset;
    for (final var a : array) {
      i += write(stringPrefix, a, data, i, charset);
    }
    return i - offset;
  }

  private static IllegalArgumentException invalidArrayLength(final Object type,
                                                             final int expectedLength,
                                                             final int actualLength) {
    return new IllegalArgumentException(String.format(
        "%s.length must be %d, not %d.",
        type.getClass().getSimpleName(), expectedLength, actualLength
    ));
  }

  public static int writeArrayChecked(final int stringPrefix,
                                      final String[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset,
                                      final Charset charset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(stringPrefix, array, data, offset, charset);
  }

  public static int writeVector(final int vectorPrefix,
                                final int stringPrefix,
                                final String[] array,
                                final byte[] data,
                                final int offset,
                                final Charset charset) {
    writeVal(vectorPrefix, array.length, data, offset);
    return stringPrefix + writeArray(stringPrefix, array, data, offset + stringPrefix, charset);
  }

  public static int writeArray(final int stringPrefix,
                               final String[][] array,
                               final byte[] data,
                               final int offset,
                               final Charset charset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(stringPrefix, a, data, i, charset);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final int stringPrefix,
                                      final String[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset,
                                      final Charset charset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(stringPrefix, a, fixedLength, data, i, charset);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final int stringPrefix,
                                      final String[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset,
                                      final Charset charset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(stringPrefix, a, secondDimensionLength, data, i, charset);
    }
    return i - offset;
  }

  public static int writeVector(final int vectorPrefix,
                                final int stringPrefix,
                                final String[][] array,
                                final byte[] data,
                                final int offset,
                                final Charset charset) {
    writeVal(vectorPrefix, array.length, data, offset);
    int i = vectorPrefix + offset;
    for (final var a : array) {
      i += writeVector(vectorPrefix, stringPrefix, a, data, i, charset);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int vectorPrefix,
                                            final int stringPrefix,
                                            final String[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset,
                                            final Charset charset) {
    writeVal(vectorPrefix, array.length, data, offset);
    return vectorPrefix + writeArrayChecked(
        stringPrefix, array, fixedLength, data, offset + vectorPrefix, charset
    );
  }

  public static int writeVectorArray(final int vectorPrefix,
                                     final int stringPrefix,
                                     final String[][] array,
                                     final byte[] data,
                                     final int offset,
                                     final Charset charset) {
    writeVal(vectorPrefix, array.length, data, offset);
    return vectorPrefix + writeArray(stringPrefix, array, data, offset + vectorPrefix, charset);
  }

  // String UTF-8 convenience methods

  public static byte[] getBytes(final String str) {
    return getBytes(str, StandardCharsets.UTF_8);
  }

  public static byte[][] getBytes(final String[] strings) {
    return getBytes(strings, StandardCharsets.UTF_8);
  }

  public static int len(final int prefix, final String val) {
    return len(prefix, val, StandardCharsets.UTF_8);
  }

  public static int lenOptional(final int optionalPrefix,
                                final int stringPrefix,
                                final String val) {
    return lenOptional(optionalPrefix, stringPrefix, val, StandardCharsets.UTF_8);
  }

  public static int lenVector(final int vectorPrefix,
                              final int stringPrefix,
                              final String[] array) {
    return lenVector(vectorPrefix, stringPrefix, array, StandardCharsets.UTF_8);
  }

  public static int lenVector(final int vectorPrefix,
                              final int stringPrefix,
                              final String[][] array) {
    return lenVector(vectorPrefix, stringPrefix, array, StandardCharsets.UTF_8);
  }

  public static int readArray(final int stringPrefix,
                              final String[] result,
                              final byte[] data,
                              final int offset) {
    return readArray(stringPrefix, result, data, offset, StandardCharsets.UTF_8);
  }

  public static String[] readStringVector(final int vectorPrefix,
                                          final int stringPrefix,
                                          final byte[] data,
                                          final int offset) {
    return readStringVector(vectorPrefix, stringPrefix, data, offset, StandardCharsets.UTF_8);
  }

  public static String[][] readMultiDimensionStringVector(final int vectorPrefix,
                                                          final int stringPrefix,
                                                          final byte[] data,
                                                          final int offset) {
    return readMultiDimensionStringVector(vectorPrefix, stringPrefix, data, offset, StandardCharsets.UTF_8);
  }

  public static int readArray(final int stringPrefix,
                              final String[][] result,
                              final byte[] data,
                              final int offset) {
    return readArray(stringPrefix, result, data, offset, StandardCharsets.UTF_8);
  }

  public static String[][] readMultiDimensionStringVectorArray(final int vectorPrefix,
                                                               final int fixedLength,
                                                               final int stringPrefix,
                                                               final byte[] data,
                                                               final int offset) {
    return readMultiDimensionStringVectorArray(vectorPrefix, fixedLength, stringPrefix, data, offset, StandardCharsets.UTF_8);
  }

  public static int write(final int stringPrefix,
                          final String str,
                          final byte[] data,
                          final int offset) {
    return write(stringPrefix, str, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeArray(final int stringPrefix,
                               final String[] array,
                               final byte[] data,
                               final int offset) {
    return writeArray(stringPrefix, array, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeArrayChecked(final int stringPrefix,
                                      final String[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    return writeArrayChecked(stringPrefix, array, fixedLength, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeVector(final int vectorPrefix,
                                final int stringPrefix,
                                final String[] array,
                                final byte[] data,
                                final int offset) {
    return writeVector(vectorPrefix, stringPrefix, array, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeArray(final int stringPrefix,
                               final String[][] array,
                               final byte[] data,
                               final int offset) {
    return writeArray(stringPrefix, array, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeArrayChecked(final int stringPrefix,
                                      final String[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    return writeArrayChecked(stringPrefix, array, fixedLength, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeArrayChecked(final int stringPrefix,
                                      final String[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    return writeArrayChecked(stringPrefix, array, firstDimensionLength, secondDimensionLength, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeVector(final int vectorPrefix,
                                final int stringPrefix,
                                final String[][] array,
                                final byte[] data,
                                final int offset) {
    return writeVector(vectorPrefix, stringPrefix, array, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeVectorArrayChecked(final int vectorPrefix,
                                            final int stringPrefix,
                                            final String[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    return writeVectorArrayChecked(vectorPrefix, stringPrefix, array, fixedLength, data, offset, StandardCharsets.UTF_8);
  }

  public static int writeVectorArray(final int vectorPrefix,
                                     final int stringPrefix,
                                     final String[][] array,
                                     final byte[] data,
                                     final int offset) {
    return writeVectorArray(vectorPrefix, stringPrefix, array, data, offset, StandardCharsets.UTF_8);
  }

  // boolean

  public static int readArray(final boolean[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = data[o++] == 1;
    }
    return o - offset;
  }

  public static int readArray(final boolean[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static boolean[] readbooleanVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new boolean[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static boolean[][] readMultiDimensionbooleanVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new boolean[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readbooleanVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static boolean[][] readMultiDimensionbooleanVectorArray(final int prefixBytes,
                                                                 final int fixedLength,
                                                                 final byte[] data,
                                                                 final int offset) {
    final int len = val(prefixBytes, data, offset);
    final boolean[][] result = new boolean[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final boolean val, final byte[] data, final int offset) {
    data[offset] = (byte) (val ? 1 : 0);
    return 1;
  }

  public static int writeOptional(final int optionalBytes, final Boolean val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write(val, data, offset + optionalBytes);
      return optionalBytes + 1;
    }
  }

  public static int writeArray(final boolean[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write(a, data, i);
    }
    return array.length;
  }

  public static int writeArrayChecked(final boolean[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final boolean[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final boolean[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final boolean[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final boolean[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final boolean[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final boolean[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes,
                                     final boolean[][] array,
                                     final byte[] data,
                                     final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Boolean val) {
    return val == null ? optionalBytes : optionalBytes + 1;
  }

  public static int lenArray(final boolean[] array) {
    return array.length;
  }

  public static int lenVector(final int prefixBytes, final boolean[] array) {
    return prefixBytes + array.length;
  }

  public static int lenArray(final boolean[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final boolean[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final boolean[][] array) {
    return prefixBytes + lenArray(array);
  }

  // short

  public static int readArray(final short[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getInt16LE(data, o);
      o += Short.BYTES;
    }
    return o - offset;
  }

  public static int readArray(final short[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static short[] readshortVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new short[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static short[][] readMultiDimensionshortVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new short[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readshortVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static short[][] readMultiDimensionshortVectorArray(final int prefixBytes,
                                                             final int fixedLength,
                                                             final byte[] data,
                                                             final int offset) {
    final int len = val(prefixBytes, data, offset);
    final short[][] result = new short[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final short val, final byte[] data, final int offset) {
    ByteUtil.putInt16LE(data, offset, val);
    return Short.BYTES;
  }

  public static int writeOptional(final int optionalBytes, final Short val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write(val, data, offset + optionalBytes);
      return optionalBytes + Short.BYTES;
    }
  }

  public static int writeOptionalshort(final int optionalBytes,
                                       final OptionalInt val,
                                       final byte[] data,
                                       final int offset) {
    if (val == null || val.isEmpty()) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      ByteUtil.putInt16LE(data, offset + optionalBytes, (short) val.getAsInt());
      return optionalBytes + Short.BYTES;
    }
  }

  public static int writeArray(final short[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final short[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final short[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final short[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final short[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final short[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final short[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final short[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes,
                                     final short[][] array,
                                     final byte[] data,
                                     final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Short val) {
    return val == null ? optionalBytes : optionalBytes + 2;
  }

  public static int lenOptionalshort(final int optionalBytes, final OptionalInt val) {
    return val == null || val.isEmpty() ? optionalBytes : optionalBytes + Short.BYTES;
  }

  public static int lenArray(final short[] array) {
    return array.length * Short.BYTES;
  }

  public static int lenVector(final int prefixBytes, final short[] array) {
    return prefixBytes + array.length * Short.BYTES;
  }

  public static int lenArray(final short[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final short[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final short[][] array) {
    return prefixBytes + lenArray(array);
  }

  // int

  public static int readArray(final int[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getInt32LE(data, o);
      o += Integer.BYTES;
    }
    return o - offset;
  }

  public static int readArray(final int[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static int[] readintVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new int[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int[][] readMultiDimensionintVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new int[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readintVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static int[][] readMultiDimensionintVectorArray(final int prefixBytes,
                                                         final int fixedLength,
                                                         final byte[] data,
                                                         final int offset) {
    final int len = val(prefixBytes, data, offset);
    final int[][] result = new int[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final int val, final byte[] data, final int offset) {
    ByteUtil.putInt32LE(data, offset, val);
    return Integer.BYTES;
  }

  public static int writeOptional(final int optionalBytes, final Integer val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write(val, data, offset + optionalBytes);
      return optionalBytes + Integer.BYTES;
    }
  }

  public static int writeOptional(final int optionalBytes,
                                  final OptionalInt val,
                                  final byte[] data,
                                  final int offset) {
    if (val == null || val.isEmpty()) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      ByteUtil.putInt32LE(data, offset + optionalBytes, val.getAsInt());
      return optionalBytes + Integer.BYTES;
    }
  }

  public static int writeArray(final int[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final int[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final int[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final int[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final int[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final int[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final int[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final int[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes, final int[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Integer val) {
    return val == null ? optionalBytes : optionalBytes + Integer.BYTES;
  }

  public static int lenOptional(final int optionalBytes, final OptionalInt val) {
    return val == null || val.isEmpty() ? optionalBytes : optionalBytes + Integer.BYTES;
  }

  public static int lenArray(final int[] array) {
    return array.length * Integer.BYTES;
  }

  public static int lenVector(final int prefixBytes, final int[] array) {
    return prefixBytes + array.length * Integer.BYTES;
  }

  public static int lenArray(final int[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final int[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final int[][] array) {
    return prefixBytes + lenArray(array);
  }

  // long

  public static int readArray(final long[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getInt64LE(data, o);
      o += Long.BYTES;
    }
    return o - offset;
  }

  public static int readArray(final long[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static long[] readlongVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new long[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static long[][] readMultiDimensionlongVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new long[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readlongVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static long[][] readMultiDimensionlongVectorArray(final int prefixBytes,
                                                           final int fixedLength,
                                                           final byte[] data,
                                                           final int offset) {
    final int len = val(prefixBytes, data, offset);
    final long[][] result = new long[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final long val, final byte[] data, final int offset) {
    ByteUtil.putInt64LE(data, offset, val);
    return Long.BYTES;
  }

  public static int writeOptional(final int optionalBytes, final Long val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write(val, data, offset + optionalBytes);
      return optionalBytes + Long.BYTES;
    }
  }

  public static int writeOptional(final int optionalBytes,
                                  final OptionalLong val,
                                  final byte[] data,
                                  final int offset) {
    if (val == null || val.isEmpty()) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      ByteUtil.putInt64LE(data, offset + optionalBytes, val.getAsLong());
      return optionalBytes + Long.BYTES;
    }
  }

  public static int writeArray(final long[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final long[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final long[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final long[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final long[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final long[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final long[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final long[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes, final long[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Long val) {
    return val == null ? optionalBytes : optionalBytes + Long.BYTES;
  }

  public static int lenOptional(final int optionalBytes, final OptionalLong val) {
    return val == null || val.isEmpty() ? optionalBytes : optionalBytes + Long.BYTES;
  }

  public static int lenArray(final long[] array) {
    return array.length * Long.BYTES;
  }

  public static int lenVector(final int prefixBytes, final long[] array) {
    return prefixBytes + array.length * Long.BYTES;
  }

  public static int lenArray(final long[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final long[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final long[][] array) {
    return prefixBytes + lenArray(array);
  }

  // float

  public static int readArray(final float[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getFloat32LE(data, o);
      o += Float.BYTES;
    }
    return o - offset;
  }

  public static int readArray(final float[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static float[] readfloatVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new float[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static float[][] readMultiDimensionfloatVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new float[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readfloatVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static float[][] readMultiDimensionfloatVectorArray(final int prefixBytes,
                                                             final int fixedLength,
                                                             final byte[] data,
                                                             final int offset) {
    final int len = val(prefixBytes, data, offset);
    final float[][] result = new float[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final float val, final byte[] data, final int offset) {
    ByteUtil.putFloat32LE(data, offset, val);
    return Float.BYTES;
  }

  public static int writeOptional(final int optionalBytes, final Float val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write(val, data, offset + optionalBytes);
      return optionalBytes + Float.BYTES;
    }
  }

  public static int writeOptionalfloat(final int optionalBytes,
                                       final OptionalDouble val,
                                       final byte[] data,
                                       final int offset) {
    if (val == null || val.isEmpty()) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      ByteUtil.putFloat32LE(data, offset + optionalBytes, (float) val.getAsDouble());
      return optionalBytes + Float.BYTES;
    }
  }

  public static int writeArray(final float[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final float[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final float[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final float[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final float[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final float[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final float[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final float[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes,
                                     final float[][] array,
                                     final byte[] data,
                                     final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Float val) {
    return val == null ? optionalBytes : optionalBytes + Float.BYTES;
  }

  public static int lenOptionalfloat(final int optionalBytes, final OptionalDouble val) {
    return val == null || val.isEmpty() ? optionalBytes : optionalBytes + Float.BYTES;
  }

  public static int lenArray(final float[] array) {
    return array.length * Float.BYTES;
  }

  public static int lenVector(final int prefixBytes, final float[] array) {
    return prefixBytes + array.length * Float.BYTES;
  }

  public static int lenArray(final float[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final float[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final float[][] array) {
    return prefixBytes + lenArray(array);
  }

  // double

  public static int readArray(final double[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getFloat64LE(data, o);
      o += Double.BYTES;
    }
    return o - offset;
  }

  public static int readArray(final double[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static double[] readdoubleVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new double[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static double[][] readMultiDimensiondoubleVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new double[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readdoubleVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static double[][] readMultiDimensiondoubleVectorArray(final int prefixBytes,
                                                               final int fixedLength,
                                                               final byte[] data,
                                                               final int offset) {
    final int len = val(prefixBytes, data, offset);
    final double[][] result = new double[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final double val, final byte[] data, final int offset) {
    ByteUtil.putFloat64LE(data, offset, val);
    return Double.BYTES;
  }

  public static int writeOptional(final int optionalBytes, final Double val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write(val, data, offset + optionalBytes);
      return optionalBytes + Double.BYTES;
    }
  }

  public static int writeOptional(final int optionalBytes,
                                  final OptionalDouble val,
                                  final byte[] data,
                                  final int offset) {
    if (val == null || val.isEmpty()) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      ByteUtil.putFloat64LE(data, offset + optionalBytes, val.getAsDouble());
      return optionalBytes + Double.BYTES;
    }
  }

  public static int writeArray(final double[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final double[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final double[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final double[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final double[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final double[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final double[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final double[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes,
                                     final double[][] array,
                                     final byte[] data,
                                     final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final Double val) {
    return val == null ? optionalBytes : optionalBytes + Double.BYTES;
  }

  public static int lenOptional(final int optionalBytes, final OptionalDouble val) {
    return val == null || val.isEmpty() ? optionalBytes : optionalBytes + Double.BYTES;
  }

  public static int lenArray(final double[] array) {
    return array.length * Double.BYTES;
  }

  public static int lenVector(final int prefixBytes, final double[] array) {
    return prefixBytes + array.length * Double.BYTES;
  }

  public static int lenArray(final double[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final double[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final double[][] array) {
    return prefixBytes + lenArray(array);
  }

  // 128-bit integers

  public static final int INT128_BYTES = 16;

  public static int read128Array(final BigInteger[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getInt128LE(data, o);
      o += INT128_BYTES;
    }
    return o - offset;
  }

  public static int read128Array(final BigInteger[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += read128Array(out, data, i);
    }
    return i - offset;
  }

  public static BigInteger[] read128Vector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new BigInteger[len];
    read128Array(result, data, offset + prefixBytes);
    return result;
  }

  public static BigInteger[][] readMultiDimension128Vector(final int prefixBytes,
                                                           final byte[] data,
                                                           int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new BigInteger[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = read128Vector(prefixBytes, data, offset);
      result[i] = instance;
      offset += len128Vector(prefixBytes, instance);
    }
    return result;
  }

  public static BigInteger[][] readMultiDimension128VectorArray(final int prefixBytes,
                                                                final int fixedLength,
                                                                final byte[] data,
                                                                final int offset) {
    final int len = val(prefixBytes, data, offset);
    final BigInteger[][] result = new BigInteger[len][fixedLength];
    read128Array(result, data, offset + prefixBytes);
    return result;
  }

  public static int write128(final BigInteger val, final byte[] data, final int offset) {
    ByteUtil.putInt128LE(data, offset, val);
    return INT128_BYTES;
  }

  public static int write128Optional(final int optionalBytes,
                                     final BigInteger val,
                                     final byte[] data,
                                     final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      return optionalBytes + write128(val, data, offset + optionalBytes);
    }
  }

  public static int write128Array(final BigInteger[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write128(a, data, i);
    }
    return i - offset;
  }

  public static int write128ArrayChecked(final BigInteger[] array,
                                         final int fixedLength,
                                         final byte[] data,
                                         final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return write128Array(array, data, offset);
  }

  public static int write128Vector(final int prefixBytes,
                                   final BigInteger[] array,
                                   final byte[] data,
                                   final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + write128Array(array, data, offset + prefixBytes);
  }

  public static int write128Array(final BigInteger[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write128Array(a, data, i);
    }
    return i - offset;
  }

  public static int write128ArrayChecked(final BigInteger[][] array,
                                         final int fixedLength,
                                         final byte[] data,
                                         final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write128ArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final BigInteger[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += write128ArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int write128Vector(final int prefixBytes,
                                   final BigInteger[][] array,
                                   final byte[] data,
                                   final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += write128Vector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int write128VectorArrayChecked(final int prefixBytes,
                                               final BigInteger[][] array,
                                               final int fixedLength,
                                               final byte[] data,
                                               final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + write128ArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int write128VectorArray(final int prefixBytes,
                                        final BigInteger[][] array,
                                        final byte[] data,
                                        final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + write128Array(array, data, offset + prefixBytes);
  }

  public static int len128Optional(final int optionalBytes, final BigInteger val) {
    return val == null ? optionalBytes : optionalBytes + INT256_BYTES;
  }

  public static int len128Array(final BigInteger[] array) {
    return array.length * INT128_BYTES;
  }

  public static int len128Vector(final int prefixBytes, final BigInteger[] array) {
    return prefixBytes + array.length * INT128_BYTES;
  }

  public static int len128Array(final BigInteger[][] array) {
    int len = 0;
    for (final var a : array) {
      len += len128Array(a);
    }
    return len;
  }

  public static int len128Vector(final int prefixBytes, final BigInteger[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += len128Vector(prefixBytes, a);
    }
    return len;
  }

  public static int len128VectorArray(final int prefixBytes, final BigInteger[][] array) {
    return prefixBytes + len128Array(array);
  }

  // 256-bit integers

  public static final int INT256_BYTES = 32;

  public static int read256Array(final BigInteger[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = ByteUtil.getInt256LE(data, o);
      o += INT256_BYTES;
    }
    return o - offset;
  }

  public static int read256Array(final BigInteger[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += read256Array(out, data, i);
    }
    return i - offset;
  }

  public static BigInteger[] read256Vector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new BigInteger[len];
    read256Array(result, data, offset + prefixBytes);
    return result;
  }

  public static BigInteger[][] readMultiDimension256Vector(final int prefixBytes,
                                                           final byte[] data,
                                                           int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new BigInteger[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = read256Vector(prefixBytes, data, offset);
      result[i] = instance;
      offset += len256Vector(prefixBytes, instance);
    }
    return result;
  }

  public static BigInteger[][] readMultiDimension256VectorArray(final int prefixBytes,
                                                                final int fixedLength,
                                                                final byte[] data,
                                                                final int offset) {
    final int len = val(prefixBytes, data, offset);
    final BigInteger[][] result = new BigInteger[len][fixedLength];
    read256Array(result, data, offset + prefixBytes);
    return result;
  }

  public static int write256(final BigInteger val, final byte[] data, final int offset) {
    ByteUtil.putInt256LE(data, offset, val);
    return INT256_BYTES;
  }

  public static int write256Optional(final int optionalBytes,
                                     final BigInteger val,
                                     final byte[] data,
                                     final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      write256(val, data, offset + optionalBytes);
      return optionalBytes + INT256_BYTES;
    }
  }

  public static int write256Array(final BigInteger[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write256(a, data, i);
    }
    return i - offset;
  }

  public static int write256ArrayChecked(final BigInteger[] array,
                                         final int fixedLength,
                                         final byte[] data,
                                         final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return write256Array(array, data, offset);
  }

  public static int write256Vector(final int prefixBytes,
                                   final BigInteger[] array,
                                   final byte[] data,
                                   final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + write256Array(array, data, offset + prefixBytes);
  }

  public static int write256Array(final BigInteger[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write256Array(a, data, i);
    }
    return i - offset;
  }

  public static int write256ArrayChecked(final BigInteger[][] array,
                                         final int fixedLength,
                                         final byte[] data,
                                         final int offset) {
    int i = offset;
    for (final var a : array) {
      i += write256ArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int write256ArrayChecked(final BigInteger[][] array,
                                         final int firstDimensionLength,
                                         final int secondDimensionLength,
                                         final byte[] data,
                                         final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += write256ArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int write256Vector(final int prefixBytes,
                                   final BigInteger[][] array,
                                   final byte[] data,
                                   final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += write256Vector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int write256VectorArrayChecked(final int prefixBytes,
                                               final BigInteger[][] array,
                                               final int fixedLength,
                                               final byte[] data,
                                               final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + write256ArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int write256VectorArray(final int prefixBytes,
                                        final BigInteger[][] array,
                                        final byte[] data,
                                        final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + write256Array(array, data, offset + prefixBytes);
  }

  public static int len256Optional(final int optionalBytes, final BigInteger val) {
    return val == null ? optionalBytes : optionalBytes + INT256_BYTES;
  }

  public static int len256Array(final BigInteger[] array) {
    return array.length * INT256_BYTES;
  }

  public static int len256Vector(final int prefixBytes, final BigInteger[] array) {
    return prefixBytes + array.length * INT256_BYTES;
  }

  public static int len256Array(final BigInteger[][] array) {
    int len = 0;
    for (final var a : array) {
      len += len256Array(a);
    }
    return len;
  }

  public static int len256Vector(final int prefixBytes, final BigInteger[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += len256Vector(prefixBytes, a);
    }
    return len;
  }

  public static int len256VectorArray(final int prefixBytes, final BigInteger[][] array) {
    return prefixBytes + len256Array(array);
  }

  // PublicKey

  public static int readArray(final PublicKey[] result, final byte[] data, final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      result[i] = PublicKey.readPubKey(data, o);
      o += PublicKey.PUBLIC_KEY_LENGTH;
    }
    return o - offset;
  }

  public static int readArray(final PublicKey[][] result, final byte[] data, final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, data, i);
    }
    return i - offset;
  }

  public static PublicKey[] readPublicKeyVector(final int prefixBytes, final byte[] data, final int offset) {
    final int len = val(prefixBytes, data, offset);
    final var result = new PublicKey[len];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static PublicKey[][] readMultiDimensionPublicKeyVector(final int prefixBytes, final byte[] data, int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    final var result = new PublicKey[len][];
    for (int i = 0; i < result.length; ++i) {
      final var instance = readPublicKeyVector(prefixBytes, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static PublicKey[][] readMultiDimensionPublicKeyVectorArray(final int prefixBytes,
                                                                     final int fixedLength,
                                                                     final byte[] data,
                                                                     final int offset) {
    final int len = val(prefixBytes, data, offset);
    final PublicKey[][] result = new PublicKey[len][fixedLength];
    readArray(result, data, offset + prefixBytes);
    return result;
  }

  public static int write(final PublicKey val, final byte[] data, final int offset) {
    return val.write(data, offset);
  }

  public static int writeOptional(final int optionalBytes, final PublicKey val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      val.write(data, offset + optionalBytes);
      return optionalBytes + PublicKey.PUBLIC_KEY_LENGTH;
    }
  }

  public static int writeArray(final PublicKey[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += a.write(data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final PublicKey[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes, final PublicKey[] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final PublicKey[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final PublicKey[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final PublicKey[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes, final PublicKey[][] array, final byte[] data, final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final PublicKey[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes,
                                     final PublicKey[][] array,
                                     final byte[] data,
                                     final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int lenOptional(final int optionalBytes, final PublicKey val) {
    return val == null ? optionalBytes : optionalBytes + PublicKey.PUBLIC_KEY_LENGTH;
  }

  public static int lenArray(final PublicKey[] array) {
    return array.length * PublicKey.PUBLIC_KEY_LENGTH;
  }

  public static int lenVector(final int prefixBytes, final PublicKey[] array) {
    return prefixBytes + array.length * PublicKey.PUBLIC_KEY_LENGTH;
  }

  public static int lenArray(final PublicKey[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final PublicKey[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final PublicKey[][] array) {
    return prefixBytes + lenArray(array);
  }

  // SerDe

  public static <E extends java.lang.Enum<?>> E read(final int bytes,
                                                     final E[] values,
                                                     final byte[] data,
                                                     final int offset) {
    return values[val(bytes, data, offset)];
  }

  public static <T extends SerDe> int readArray(final T[] result,
                                                final Factory<T> factory,
                                                final byte[] data,
                                                final int offset) {
    int o = offset;
    for (int i = 0; i < result.length; ++i) {
      final var instance = factory.read(data, o);
      result[i] = instance;
      o += instance.l();
    }
    return o - offset;
  }

  public static <T extends SerDe> int readArray(final T[][] result,
                                                final Factory<T> factory,
                                                final byte[] data,
                                                final int offset) {
    int i = offset;
    for (final var out : result) {
      i += readArray(out, factory, data, i);
    }
    return i - offset;
  }

  public static <T extends SerDe> T[] readVector(final int prefixBytes,
                                                 final Class<T> clazz,
                                                 final Factory<T> factory,
                                                 final byte[] data,
                                                 final int offset) {
    final int len = val(prefixBytes, data, offset);
    @SuppressWarnings("unchecked") final var result = (T[]) Array.newInstance(clazz, len);
    readArray(result, factory, data, offset + prefixBytes);
    return result;
  }

  public static <T extends SerDe> T[][] readMultiDimensionVector(final int prefixBytes,
                                                                 final Class<T> clazz,
                                                                 final Factory<T> factory,
                                                                 final byte[] data,
                                                                 int offset) {
    final int len = val(prefixBytes, data, offset);
    offset += prefixBytes;
    @SuppressWarnings("unchecked") final T[][] result = (T[][]) Array.newInstance(clazz, len, 0);
    for (int i = 0; i < result.length; ++i) {
      final var instance = readVector(prefixBytes, clazz, factory, data, offset);
      result[i] = instance;
      offset += lenVector(prefixBytes, instance);
    }
    return result;
  }

  public static <T extends SerDe> T[][] readMultiDimensionVectorArray(final int prefixBytes,
                                                                      final Class<T> clazz,
                                                                      final Factory<T> factory,
                                                                      final int fixedLength,
                                                                      final byte[] data,
                                                                      final int offset) {
    final int len = val(prefixBytes, data, offset);
    @SuppressWarnings("unchecked") final T[][] result = (T[][]) Array.newInstance(clazz, len, fixedLength);
    readArray(result, factory, data, offset + prefixBytes);
    return result;
  }

  public static int write(final SerDe val, final byte[] data, final int offset) {
    return val.write(data, offset);
  }

  public static int writeOptional(final int optionalBytes, final SerDe val, final byte[] data, final int offset) {
    if (val == null) {
      writeVal(optionalBytes, 0, data, offset);
      return optionalBytes;
    } else {
      writeVal(optionalBytes, 1, data, offset);
      return optionalBytes + write(val, data, offset + optionalBytes);
    }
  }

  public static int writeArray(final SerDe[] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += a.write(data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final SerDe[] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != fixedLength) {
      throw invalidArrayLength(array, fixedLength, array.length);
    }
    return writeArray(array, data, offset);
  }

  public static int writeVector(final int prefixBytes,
                                final SerDe[] array,
                                final byte[] data,
                                final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int writeArray(final SerDe[][] array, final byte[] data, final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArray(a, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final SerDe[][] array,
                                      final int fixedLength,
                                      final byte[] data,
                                      final int offset) {
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, fixedLength, data, i);
    }
    return i - offset;
  }

  public static int writeArrayChecked(final SerDe[][] array,
                                      final int firstDimensionLength,
                                      final int secondDimensionLength,
                                      final byte[] data,
                                      final int offset) {
    if (array.length != firstDimensionLength) {
      throw invalidArrayLength(array, firstDimensionLength, array.length);
    }
    int i = offset;
    for (final var a : array) {
      i += writeArrayChecked(a, secondDimensionLength, data, i);
    }
    return i - offset;
  }

  public static int writeVector(final int prefixBytes,
                                final SerDe[][] array,
                                final byte[] data,
                                final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    int i = prefixBytes + offset;
    for (final var a : array) {
      i += writeVector(prefixBytes, a, data, i);
    }
    return i - offset;
  }

  public static int writeVectorArrayChecked(final int prefixBytes,
                                            final SerDe[][] array,
                                            final int fixedLength,
                                            final byte[] data,
                                            final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArrayChecked(array, fixedLength, data, offset + prefixBytes);
  }

  public static int writeVectorArray(final int prefixBytes,
                                     final SerDe[][] array,
                                     final byte[] data,
                                     final int offset) {
    writeVal(prefixBytes, array.length, data, offset);
    return prefixBytes + writeArray(array, data, offset + prefixBytes);
  }

  public static int len(final SerDe val) {
    return val.l();
  }

  public static int lenOptional(final int optionalBytes, final SerDe val) {
    return val == null ? optionalBytes : optionalBytes + val.l();
  }

  public static int lenArray(final SerDe[] array) {
    int len = 0;
    for (final var a : array) {
      len += a.l();
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final SerDe[] array) {
    return prefixBytes + lenArray(array);
  }

  public static int lenArray(final SerDe[][] array) {
    int len = 0;
    for (final var a : array) {
      len += lenArray(a);
    }
    return len;
  }

  public static int lenVector(final int prefixBytes, final SerDe[][] array) {
    int len = prefixBytes;
    for (final var a : array) {
      len += lenVector(prefixBytes, a);
    }
    return len;
  }

  public static int lenVectorArray(final int prefixBytes, final SerDe[][] array) {
    return prefixBytes + lenArray(array);
  }
}
