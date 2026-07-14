package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record JupiterSwapTx(byte[] swapTransaction, long lastValidBlockHeight, int prioritizationFeeLamports) {

  public static JupiterSwapTx parse(final JsonIterator ji) {
    return ji.parseObject(new JupiterSwapTx.Builder());
  }

  private static final class Builder implements FieldBufferPredicate, Supplier<JupiterSwapTx> {

    private byte[] swapTransaction;
    private long lastValidBlockHeight;
    private int prioritizationFeeLamports;

    private Builder() {
    }

    @Override
    public JupiterSwapTx get() {
      return new JupiterSwapTx(swapTransaction, lastValidBlockHeight, prioritizationFeeLamports);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("swapTransaction", buf, offset, len)) {
        swapTransaction = ji.decodeBase64String();
      } else if (fieldEquals("lastValidBlockHeight", buf, offset, len)) {
        lastValidBlockHeight = ji.readLong();
      } else if (fieldEquals("prioritizationFeeLamports", buf, offset, len)) {
        prioritizationFeeLamports = ji.readInt();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
