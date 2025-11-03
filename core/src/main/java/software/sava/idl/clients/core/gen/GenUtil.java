package software.sava.idl.clients.core.gen;

public final class GenUtil {

  public static byte[] checkMaxLength(final byte[] bytes, final int maxLength) {
    if (bytes.length > maxLength) {
      throw new IllegalStateException(String.format(
          "[length=%d] cannot be greater than %d.",
          bytes.length, maxLength
      ));
    }
    return bytes;
  }

  private GenUtil() {}
}
