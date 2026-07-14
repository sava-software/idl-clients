package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;

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
        case 2 -> slot = ji.readBigInteger();
        case 3 -> error = ji.readString();
        case 4 -> code = ji.readLong();
        case 5 -> totalInputAmount = ji.readLong();
        case 6 -> totalOutputAmount = ji.readLong();
        case 7 -> inputAmountResult = ji.readString();
        case 8 -> outputAmountResult = ji.readString();
        case 9 -> swapEvents = ji.readList(SwapEvent::parse);
        default -> ji.skip();
      }
      return true;
    }
  }
}
