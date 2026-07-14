package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.time.Instant;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record TokenPool(String id, Instant createdAt) {

  public static TokenPool parse(final JsonIterator ji) {
    return ji.parseObject(new Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<TokenPool> {

    private String id;
    private Instant createdAt;

    @Override
    public TokenPool get() {
      return new TokenPool(id, createdAt);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("id", buf, offset, len)) {
        this.id = ji.readString();
      } else if (fieldEquals("createdAt", buf, offset, len)) {
        this.createdAt = ji.readDateTime();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
