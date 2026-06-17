package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import systems.comodal.jsoniter.ContextFieldBufferPredicate;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;

import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record JupiterRoute(PublicKey ammKey,
                           String label,
                           PublicKey inputMint,
                           PublicKey outputMint,
                           long inAmount,
                           long outAmount,
                           long feeAmount,
                           PublicKey feeMint,
                           int percent,
                           int bps,
                           BigDecimal usdValue) {

  public static JupiterRoute parse(final JsonIterator ji) {
    final var parser = new Parser();
    ji.testObject(parser);
    return parser.create();
  }

  private static final ContextFieldBufferPredicate<Parser> SWAP_INFO_PARSER = (parser, buf, offset, len, ji) -> {
    if (fieldEquals("ammKey", buf, offset, len)) {
      parser.ammKey = parseBase58Encoded(ji);
    } else if (fieldEquals("label", buf, offset, len)) {
      parser.label = ji.readString();
    } else if (fieldEquals("inputMint", buf, offset, len)) {
      parser.inputMint = parseBase58Encoded(ji);
    } else if (fieldEquals("outputMint", buf, offset, len)) {
      parser.outputMint = parseBase58Encoded(ji);
    } else if (fieldEquals("inAmount", buf, offset, len)) {
      parser.inAmount = ji.readLong();
    } else if (fieldEquals("outAmount", buf, offset, len)) {
      parser.outAmount = ji.readLong();
    } else if (fieldEquals("feeAmount", buf, offset, len)) {
      parser.feeAmount = ji.readLong();
    } else if (fieldEquals("feeMint", buf, offset, len)) {
      parser.feeMint = parseBase58Encoded(ji);
    } else {
      ji.skip();
    }
    return true;
  };

  private static final class Parser implements FieldBufferPredicate {

    private PublicKey ammKey;
    private String label;
    private PublicKey inputMint;
    private PublicKey outputMint;
    private long inAmount;
    private long outAmount;
    private long feeAmount;
    private PublicKey feeMint;
    private int percent;
    private int bps;
    private BigDecimal usdValue;

    private Parser() {
    }

    private JupiterRoute create() {
      return new JupiterRoute(
          ammKey, label, inputMint, outputMint, inAmount, outAmount, feeAmount, feeMint,
          percent, bps, usdValue
      );
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("swapInfo", buf, offset, len)) {
        ji.testObject(this, SWAP_INFO_PARSER);
      } else if (fieldEquals("percent", buf, offset, len)) {
        percent = ji.readNull() ? -1 : ji.readInt();
      } else if (fieldEquals("bps", buf, offset, len)) {
        bps = ji.readNull() ? -1 : ji.readInt();
      } else if (fieldEquals("usdValue", buf, offset, len)) {
        usdValue = ji.readBigDecimal();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
