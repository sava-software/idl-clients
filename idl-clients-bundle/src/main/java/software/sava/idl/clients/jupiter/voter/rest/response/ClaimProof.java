package software.sava.idl.clients.jupiter.voter.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.rpc.json.PublicKeyEncoding;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.ArrayList;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record ClaimProof(PublicKey mint,
                         PublicKey merkleTree,
                         long amount,
                         long lockedAmount,
                         byte[][] proof) {

  public static ClaimProof parseProof(final JsonIterator ji) {
    return ji.parseObject(new ClaimProof.Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<ClaimProof> {

    private PublicKey mint;
    private PublicKey merkleTree;
    private long amount;
    private long lockedAmount;
    private byte[][] proof;

    private Parser() {
    }

    @Override
    public ClaimProof get() {
      return new ClaimProof(mint, merkleTree, amount, lockedAmount, proof);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("mint", buf, offset, len)) {
        mint = PublicKeyEncoding.parseBase58Encoded(ji);
      } else if (fieldEquals("merkle_tree", buf, offset, len)) {
        merkleTree = PublicKeyEncoding.parseBase58Encoded(ji);
      } else if (fieldEquals("amount", buf, offset, len)) {
        amount = ji.readLong();
      } else if (fieldEquals("locked_amount", buf, offset, len)) {
        lockedAmount = ji.readLong();
      } else if (fieldEquals("proof", buf, offset, len)) {
        this.proof = ji.readCollection(new ArrayList<>(16), jsonIterator -> {
          final byte[] item = new byte[32];
          jsonIterator.readByteArray(item);
          return item;
        }).toArray(byte[][]::new);
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
