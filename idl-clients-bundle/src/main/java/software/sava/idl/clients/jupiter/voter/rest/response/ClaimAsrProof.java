package software.sava.idl.clients.jupiter.voter.rest.response;

import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.List;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record ClaimAsrProof(List<ClaimProof> claimProof, int voteCount, int voteCountExtra) {

  public static ClaimAsrProof parseProof(final JsonIterator ji) {
    return ji.parseObject(new ClaimAsrProof.Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<ClaimAsrProof> {

    private List<ClaimProof> claims;
    private int voteCount;
    private int voteCountExtra;

    private Parser() {
    }

    @Override
    public ClaimAsrProof get() {
      return new ClaimAsrProof(claims, voteCount, voteCountExtra);
    }


    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("claim", buf, offset, len)) {
        claims = ji.readList(ClaimProof::parseProof);
      } else if (fieldEquals("voteCount", buf, offset, len)) {
        voteCount = ji.readInt();
      } else {
        voteCountExtra = ji.readInt();
      }
      return true;
    }
  }
}
