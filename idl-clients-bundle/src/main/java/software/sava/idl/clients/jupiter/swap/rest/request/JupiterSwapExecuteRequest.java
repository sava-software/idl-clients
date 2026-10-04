package software.sava.idl.clients.jupiter.swap.rest.request;

import java.util.Base64;

/// The body of `POST /swap/v2/execute`.
///
/// @param signedTransaction    the base64 of the signed `/order` transaction, partially signed whenever its fee payer,
///                             the order's `signatureFeePayer`, is a key you do not hold (a JupiterZ market maker, or
///                             Jupiter's sponsor on an automatically sponsored order): Jupiter adds that signature
///                             during `/execute`
/// @param requestId            the `/order` response's requestId
/// @param lastValidBlockHeight a block height for nonce validation, sent when positive; 0 omits it
public record JupiterSwapExecuteRequest(String signedTransaction, String requestId, long lastValidBlockHeight) {

  /// Checks the three values; the strings are escaped by [#toJson()], never pattern-matched.
  ///
  /// @throws IllegalArgumentException if a string is null or blank, or the height is negative
  public JupiterSwapExecuteRequest {
    requireNonBlank("signedTransaction", signedTransaction);
    requireNonBlank("requestId", requestId);
    lastValidBlockHeight = SwapV2Query.requireNonNegative("lastValidBlockHeight", lastValidBlockHeight);
  }

  /// A request without a block height.
  public static JupiterSwapExecuteRequest create(final String base64SignedTransaction, final String requestId) {
    return new JupiterSwapExecuteRequest(base64SignedTransaction, requestId, 0);
  }

  /// A request without a block height, base64-encoding (standard alphabet, padded) `signedTransaction`.
  public static JupiterSwapExecuteRequest create(final byte[] signedTransaction, final String requestId) {
    return create(signedTransaction == null ? null : Base64.getEncoder().encodeToString(signedTransaction), requestId);
  }

  /// `{"signedTransaction":"…","requestId":"…"}` plus `"lastValidBlockHeight":"N"` when positive; both strings JSON-escaped, no whitespace.
  ///
  /// The escape works in one pass: a quote or a backslash gets a backslash in front of it, every character below
  /// U+0020 becomes a backslash, `u`, then the code as four lowercase hex digits, and every other character is
  /// copied verbatim.
  public String toJson() {
    final var json = new StringBuilder();
    json.append("{\"signedTransaction\":");
    appendJsonString(json, signedTransaction);
    json.append(",\"requestId\":");
    appendJsonString(json, requestId);
    if (lastValidBlockHeight > 0) {
      json.append(",\"lastValidBlockHeight\":\"").append(lastValidBlockHeight).append('"');
    }
    return json.append('}').toString();
  }

  private static void requireNonBlank(final String parameter, final String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(parameter + " must not be null or blank");
    }
  }

  private static void appendJsonString(final StringBuilder json, final String value) {
    json.append('"');
    for (int i = 0, length = value.length(); i < length; ++i) {
      final char c = value.charAt(i);
      if (c == '"' || c == '\\') {
        json.append('\\').append(c);
      } else if (c < 0x20) {
        json.append(String.format("\\u%04x", (int) c));
      } else {
        json.append(c);
      }
    }
    json.append('"');
  }
}
