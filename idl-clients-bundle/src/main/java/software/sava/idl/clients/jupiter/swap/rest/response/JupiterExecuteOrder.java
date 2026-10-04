package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNullElse;

/// The result of an `/execute` call: Swap API V2's `POST /swap/v2/execute`, and the Ultra
/// `executeOrder(...)`, which shares this parser.
///
/// Jupiter may already have landed the transaction this reports on, so the amount, slot and swap
/// event mint reads are lenient. An amount (`totalInputAmount`, `totalOutputAmount` or a swap
/// event's `inputAmount` or `outputAmount`) may be any u64 in decimal digits, above
/// `Long.MAX_VALUE` included; the slot any integer in decimal digits, of any size; a mint any
/// 32-byte base58 key; and each reads JSON null or `""` as if it were absent. A failed parse loses
/// `status` and `signature`, and these still fail it: a minus sign, a decimal point or an exponent
/// in an amount, a decimal point or an exponent in the slot, even in a whole value such as `1000.0`
/// or `1e3`, and any other mint. A failed parse does not prove the transaction failed to land.
///
/// @param slot              null when absent, null or `""`
/// @param code              0 on success. An integral spelling such as `-1000.0` or `-1e3` reads as -1000, and a JSON
///                          null or `""` fails the parse with an `IllegalStateException` naming it, because 0 would
///                          read as success. A body with no `code` key reads as 0, so check `status` too. Only a 2xx
///                          body reaches this parser through `JupiterSwapV2Client.execute` or the Ultra `executeOrder`:
///                          every non-2xx answer, the documented 400 and 500 bodies of both APIs included, fails the
///                          returned future before this parser sees it, and any `code` such a body carries is in the
///                          failure's message only. Both documented 200 bodies carry `code`.
/// @param totalInputAmount  a u64 read unsigned (`Long.toUnsignedString` / `Long.compareUnsigned` above `Long.MAX_VALUE`); 0 when absent, null or `""`
/// @param totalOutputAmount a u64 read unsigned (`Long.toUnsignedString` / `Long.compareUnsigned` above `Long.MAX_VALUE`); 0 when absent, null or `""`
/// @param swapEvents        the swaps Jupiter reports, unmodifiable; never null: empty when absent, as in the
///                          aggregator and RFQ `Failed` bodies Jupiter documents, or JSON null
public record JupiterExecuteOrder(String status,
                                  String signature,
                                  BigInteger slot,
                                  String error,
                                  long code,
                                  long totalInputAmount,
                                  long totalOutputAmount,
                                  String inputAmountResult,
                                  String outputAmountResult,
                                  List<SwapEvent> swapEvents,
                                  byte[] responseJson) {

  /// Turns an absent `swapEvents` into an empty list and passes every list through `List.copyOf`,
  /// so the record's list is unmodifiable and changing the list passed in never changes it.
  ///
  /// @throws NullPointerException if `swapEvents` holds a null event
  public JupiterExecuteOrder {
    swapEvents = List.copyOf(requireNonNullElse(swapEvents, List.of()));
  }

  /// Parses an `/execute` body and keeps the raw bytes as `responseJson`; unknown fields are skipped.
  ///
  /// @throws IllegalStateException    if `code` is JSON null or `""`
  /// @throws ArithmeticException      if `code` is fractional or outside the range of a long, unless its exponent is one `BigDecimal` cannot represent
  /// @throws NumberFormatException    if an amount has a minus sign, a decimal point or an exponent, exceeds 2^64-1 or is a non-empty string that is not a number; if the slot has a decimal point or an exponent or is a non-empty string that is not a number; or if `code` is a non-empty string that is not a number or has an exponent `BigDecimal` cannot represent
  /// @throws IllegalArgumentException if a swap event mint is a string that is neither `""` nor a 32-byte base58 key
  /// @throws systems.comodal.jsoniter.JsonException if a value has the wrong JSON type, such as a numeric `status`, a
  ///                                                boolean `code`, slot or amount, or a string `swapEvents`
  public static JupiterExecuteOrder parse(final byte[] responseJson, final JsonIterator ji) {
    return ji.parseObject(Parser.FIELDS, new JupiterExecuteOrder.Parser(responseJson));
  }

  public enum Status {
    Success, Failed
  }

  private static final class Parser implements FieldIndexPredicate, Supplier<JupiterExecuteOrder> {

    private final byte[] responseJson;
    private String status;
    private String signature;
    private BigInteger slot;
    private String error;
    private long code;
    private long totalInputAmount;
    private long totalOutputAmount;
    private String inputAmountResult;
    private String outputAmountResult;
    private List<SwapEvent> swapEvents;

    private Parser(final byte[] responseJson) {
      this.responseJson = responseJson;
    }

    @Override
    public JupiterExecuteOrder get() {
      return new JupiterExecuteOrder(
          status, signature, slot, error, code,
          totalInputAmount, totalOutputAmount, inputAmountResult, outputAmountResult,
          swapEvents, responseJson
      );
    }

    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "status",
        "signature",
        "slot",
        "error",
        "code",
        "totalInputAmount",
        "totalOutputAmount",
        "inputAmountResult",
        "outputAmountResult",
        "swapEvents"
    );

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> status = ji.readString();
        case 1 -> signature = ji.readString();
        case 2 -> {
          final var s = ji.readNumberOrNumberString();
          slot = s == null || s.isEmpty() ? null : new BigInteger(s);
        }
        case 3 -> error = ji.readString();
        case 4 -> code = SwapV2Json.readRequiredIntegral(ji, "code");
        case 5 -> totalInputAmount = SwapV2Json.readU64(ji);
        case 6 -> totalOutputAmount = SwapV2Json.readU64(ji);
        case 7 -> inputAmountResult = ji.readString();
        case 8 -> outputAmountResult = ji.readString();
        case 9 -> swapEvents = ji.readList(SwapEvent::parse);
        default -> ji.skip();
      }
      return true;
    }
  }
}
