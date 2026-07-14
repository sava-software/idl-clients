package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.rpc.json.PublicKeyEncoding;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record JupiterPrice(PublicKey mint,
                           Instant createdAt,
                           double liquidity,
                           double usdPrice,
                           BigInteger blockId,
                           int decimals,
                           double priceChange24h) {

  public static JupiterPrice parsePrice(final PublicKey mint, final JsonIterator ji) {
    return ji.parseObject(new Parser(mint));
  }

  public static Map<PublicKey, JupiterPrice> parsePrices(final JsonIterator ji) {
    return ji.readMap(
        HashMap.newHashMap(64),
        PublicKeyEncoding.PARSE_BASE58_PUBLIC_KEY,
        JupiterPrice::parsePrice
    );
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<JupiterPrice> {

    private final PublicKey mint;
    private Instant createdAt;
    private double liquidity;
    private double usdPrice;
    private BigInteger blockId;
    private int decimals;
    private double priceChange24h;

    private Parser(final PublicKey mint) {
      this.mint = mint;
    }

    @Override
    public JupiterPrice get() {
      return new JupiterPrice(mint, createdAt, liquidity, usdPrice, blockId, decimals, priceChange24h);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("usdPrice", buf, offset, len)) {
        this.usdPrice = ji.readDouble();
      } else if (fieldEquals("createdAt", buf, offset, len)) {
        this.createdAt = ji.readDateTime();
      } else if (fieldEquals("liquidity", buf, offset, len)) {
        this.liquidity = ji.readDouble();
      } else if (fieldEquals("blockId", buf, offset, len)) {
        this.blockId = ji.readBigInteger();
      } else if (fieldEquals("decimals", buf, offset, len)) {
        this.decimals = ji.readInt();
      } else if (fieldEquals("priceChange24h", buf, offset, len)) {
        this.priceChange24h = ji.readDouble();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
