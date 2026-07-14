package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record PlatformFee(long amount, int feeBps) {

  public static PlatformFee parse(final JsonIterator ji) {
    return ji.parseObject(new Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<PlatformFee> {
    private long amount;
    private int feeBps;

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("amount", buf, offset, len)) {
        this.amount = ji.readLong();
      } else if (fieldEquals("feeBps", buf, offset, len)) {
        this.feeBps = ji.readInt();
      } else {
        ji.skip();
      }
      return true;
    }

    @Override
    public PlatformFee get() {
      return new PlatformFee(amount, feeBps);
    }
  }
}
