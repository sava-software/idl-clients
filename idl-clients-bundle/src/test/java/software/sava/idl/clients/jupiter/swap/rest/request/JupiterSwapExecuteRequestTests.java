package software.sava.idl.clients.jupiter.swap.rest.request;

import org.junit.jupiter.api.Test;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

/// Covers the `POST /swap/v2/execute` body: its exact bytes, its JSON escaping, and its value checks.
///
/// The body is built by hand rather than by a JSON library, so the expected bodies below are the bytes on the
/// wire, written out with every backslash doubled.
final class JupiterSwapExecuteRequestTests {

  private static final String REQUEST_ID = "019974a8-5fbb-7395-9355-9ebf8f844884";

  @Test
  void bodyCarriesTheTwoRequiredFields() {
    final var request = JupiterSwapExecuteRequest.create("AQID", "019974a8-5fbb-7395-9355-9ebf8f844884");
    assertEquals("AQID", request.signedTransaction());
    assertEquals("019974a8-5fbb-7395-9355-9ebf8f844884", request.requestId());
    assertEquals(0L, request.lastValidBlockHeight());
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\"}",
        request.toJson()
    );
  }

  @Test
  void aPositiveLastValidBlockHeightIsSentAsAJsonString() {
    final var request = new JupiterSwapExecuteRequest("AQID", "019974a8-5fbb-7395-9355-9ebf8f844884", 279000150);
    assertEquals(279000150L, request.lastValidBlockHeight());
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\",\"lastValidBlockHeight\":\"279000150\"}",
        request.toJson()
    );

    // 0 omits it, and 1 is sent
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\"}",
        new JupiterSwapExecuteRequest("AQID", REQUEST_ID, 0).toJson()
    );
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\",\"lastValidBlockHeight\":\"1\"}",
        new JupiterSwapExecuteRequest("AQID", REQUEST_ID, 1).toJson()
    );
  }

  @Test
  void stringsAreJsonEscaped() {
    // E3: a quote, a backslash, two control characters, a space and a non-ASCII letter
    final var requestId = "a\"b\\c" + (char) 0x01 + (char) 0x1f + " é";
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"a\\\"b\\\\c\\u0001\\u001f é\"}",
        JupiterSwapExecuteRequest.create("AQID", requestId).toJson()
    );

    // the boundary: 0x1f is escaped and 0x20 is not
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"x\\u001f\"}",
        JupiterSwapExecuteRequest.create("AQID", "x" + (char) 0x1f).toJson()
    );
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"x \"}",
        JupiterSwapExecuteRequest.create("AQID", "x" + (char) 0x20).toJson()
    );

    // the lowest code, a line feed, and DEL, which JSON leaves alone
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"x\\u0000\\u000ay" + (char) 0x7f + "\"}",
        JupiterSwapExecuteRequest.create("AQID", "x" + (char) 0x00 + (char) 0x0a + "y" + (char) 0x7f).toJson()
    );

    // the signed transaction is escaped the same way
    assertEquals(
        "{\"signedTransaction\":\"A\\\"Q\\\\I\\u0009D\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\"}",
        JupiterSwapExecuteRequest.create("A\"Q\\I" + (char) 0x09 + "D", REQUEST_ID).toJson()
    );
  }

  @Test
  void theEscapedBodyParsesBackToTheOriginalStrings() {
    final var hostile = new StringBuilder("id:");
    for (char c = 0; c <= 0x20; ++c) {
      hostile.append(c);
    }
    hostile.append("\"\\/é").append((char) 0x7f).append(Character.toChars(0x1F600));
    final var requestId = hostile.toString();
    final var signedTransaction = "AQ\"ID\\" + (char) 0x0a + "==";

    final var body = new JupiterSwapExecuteRequest(signedTransaction, requestId, 279000150).toJson();
    final var fields = JsonIterator.parse(body.getBytes(UTF_8))
        .readMap(new LinkedHashMap<String, String>(), String::new, (name, ji) -> ji.readString());

    assertEquals(List.of("signedTransaction", "requestId", "lastValidBlockHeight"), List.copyOf(fields.keySet()));
    assertEquals(signedTransaction, fields.get("signedTransaction"));
    assertEquals(requestId, fields.get("requestId"));
    assertEquals("279000150", fields.get("lastValidBlockHeight"));
  }

  @Test
  void bytesAreBase64Encoded() {
    final var request = JupiterSwapExecuteRequest.create(new byte[]{1, 2, 3}, REQUEST_ID);
    assertEquals("AQID", request.signedTransaction());
    assertEquals(0L, request.lastValidBlockHeight());
    assertEquals(
        "{\"signedTransaction\":\"AQID\",\"requestId\":\"019974a8-5fbb-7395-9355-9ebf8f844884\"}",
        request.toJson()
    );

    // the standard alphabet, not the URL-safe one, and padded
    assertEquals("+/8=", JupiterSwapExecuteRequest.create(new byte[]{(byte) 0xfb, (byte) 0xff}, REQUEST_ID).signedTransaction());
    assertEquals("AQ==", JupiterSwapExecuteRequest.create(new byte[]{1}, REQUEST_ID).signedTransaction());

    // no bytes encode to an empty string, which is refused
    assertEquals(
        "signedTransaction must not be null or blank",
        assertThrows(IllegalArgumentException.class, () -> JupiterSwapExecuteRequest.create(new byte[0], REQUEST_ID)).getMessage()
    );
  }

  @Test
  void invalidValuesAreRejected() {
    for (final var invalid : Arrays.asList(null, "", " ", "\t")) {
      assertEquals(
          "signedTransaction must not be null or blank",
          assertThrows(IllegalArgumentException.class, () -> new JupiterSwapExecuteRequest(invalid, REQUEST_ID, 0)).getMessage()
      );
      assertEquals(
          "requestId must not be null or blank",
          assertThrows(IllegalArgumentException.class, () -> new JupiterSwapExecuteRequest("AQID", invalid, 0)).getMessage()
      );
      assertEquals(
          "signedTransaction must not be null or blank",
          assertThrows(IllegalArgumentException.class, () -> JupiterSwapExecuteRequest.create(invalid, REQUEST_ID)).getMessage()
      );
      assertEquals(
          "requestId must not be null or blank",
          assertThrows(IllegalArgumentException.class, () -> JupiterSwapExecuteRequest.create("AQID", invalid)).getMessage()
      );
      assertEquals(
          "requestId must not be null or blank",
          assertThrows(IllegalArgumentException.class, () -> JupiterSwapExecuteRequest.create(new byte[]{1, 2, 3}, invalid)).getMessage()
      );
    }

    // a null array is refused like a null string, not with the encoder's NullPointerException
    assertEquals(
        "signedTransaction must not be null or blank",
        assertThrows(IllegalArgumentException.class, () -> JupiterSwapExecuteRequest.create((byte[]) null, REQUEST_ID)).getMessage()
    );

    assertEquals(
        "lastValidBlockHeight must not be negative: -1",
        assertThrows(IllegalArgumentException.class, () -> new JupiterSwapExecuteRequest("AQID", REQUEST_ID, -1)).getMessage()
    );
    assertEquals(0L, new JupiterSwapExecuteRequest("AQID", REQUEST_ID, 0).lastValidBlockHeight());
  }
}
