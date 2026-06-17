package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.swap.RouteV2Data;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.Collection;
import java.util.List;

import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapInstructions.parseInstruction;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapInstructions.parseKeys;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record JupiterSwapIx(Instruction swapInstruction, Collection<PublicKey> addressLookupTableAddresses) {

  public RouteV2Data readData() {
    return RouteV2Data.readData(swapInstruction.data(), swapInstruction.offset());
  }

  public static JupiterSwapIx parse(final JsonIterator ji) {
    final var parser = new Parser();
    ji.testObject(parser);
    return parser.create();
  }

  private static final class Parser implements FieldBufferPredicate {

    private Instruction swapInstruction;
    private List<PublicKey> addressLookupTableAddresses;

    private Parser() {
    }

    private JupiterSwapIx create() {
      return new JupiterSwapIx(swapInstruction, addressLookupTableAddresses);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("swapInstruction", buf, offset, len)) {
        swapInstruction = parseInstruction(ji);
      } else if (fieldEquals("addressLookupTableAddresses", buf, offset, len)) {
        addressLookupTableAddresses = parseKeys(ji);
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
