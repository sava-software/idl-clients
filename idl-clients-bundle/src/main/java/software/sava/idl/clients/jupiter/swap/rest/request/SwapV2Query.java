package software.sava.idl.clients.jupiter.swap.rest.request;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/// Query helpers shared by the two V2 request types.
final class SwapV2Query {

  private static final String[] NO_LABELS = new String[0];

  /// An unmodifiable copy of `labels` (`List.of()` for null) after checking each element; `parameter` names the query parameter in the message.
  ///
  /// @throws IllegalArgumentException for a null or blank element, one with leading or trailing whitespace or
  ///                                  no-break space, or one containing a comma
  static List<String> labels(final String parameter, final Collection<String> labels) {
    final String[] copy = labels == null ? NO_LABELS : labels.toArray(String[]::new);
    for (final String label : copy) {
      if (label == null) {
        throw new IllegalArgumentException(parameter + " must not contain null");
      }
      if (label.isBlank()) {
        throw new IllegalArgumentException(parameter + " must not contain a blank value: \"" + label + '"');
      }
      // strip() follows Character.isWhitespace, which leaves out the no-break spaces U+00A0, U+2007 and U+202F
      if (!label.equals(label.strip())
          || Character.isSpaceChar(label.codePointAt(0))
          || Character.isSpaceChar(label.codePointBefore(label.length()))) {
        throw new IllegalArgumentException(parameter + " must not have leading or trailing whitespace: \"" + label + '"');
      }
      if (label.contains(",")) {
        throw new IllegalArgumentException(parameter + " must not contain a value with a comma: \"" + label + '"');
      }
    }
    return List.of(copy);
  }

  /// Appends `&name=` and `URLEncoder.encode(String.join(",", labels), UTF_8)` when `labels` is non-empty.
  static void appendLabels(final StringBuilder query, final String name, final List<String> labels) {
    if (!labels.isEmpty()) {
      query.append('&').append(name).append('=').append(URLEncoder.encode(String.join(",", labels), UTF_8));
    }
  }

  /// Returns `value`.
  ///
  /// @throws IllegalArgumentException naming `parameter` when negative
  static int requireNonNegative(final String parameter, final int value) {
    if (value < 0) {
      throw new IllegalArgumentException(parameter + " must not be negative: " + value);
    }
    return value;
  }

  /// Returns `value`.
  ///
  /// @throws IllegalArgumentException naming `parameter` when negative
  static long requireNonNegative(final String parameter, final long value) {
    if (value < 0) {
      throw new IllegalArgumentException(parameter + " must not be negative: " + value);
    }
    return value;
  }

  private SwapV2Query() {
  }
}
