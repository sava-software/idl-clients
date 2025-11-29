package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record SwapEvent(PublicKey inputMint,
                        long inputAmount,
                        PublicKey outputMint,
                        long outputAmount) {

  public static SwapEvent parse(final JsonIterator ji) {
    final var parser = new SwapEvent.Parser();
    ji.testObject(parser);
    return parser.create();
  }

  private static final class Parser implements FieldBufferPredicate {

    private PublicKey inputMint;
    private long inputAmount;
    private PublicKey outputMint;
    private long outputAmount;

    private SwapEvent create() {
      return new SwapEvent(inputMint, inputAmount, outputMint, outputAmount);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("inputMint", buf, offset, len)) {
        inputMint = parseBase58Encoded(ji);
      } else if (fieldEquals("inputAmount", buf, offset, len)) {
        inputAmount = ji.readLong();
      } else if (fieldEquals("outputMint", buf, offset, len)) {
        outputMint = parseBase58Encoded(ji);
      } else if (fieldEquals("outputAmount", buf, offset, len)) {
        outputAmount = ji.readLong();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
