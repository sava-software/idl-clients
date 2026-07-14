package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.time.Duration;
import java.util.function.Supplier;

public record JupiterTokenStats(Duration duration,
                                double priceChange,
                                double holderChange,
                                double liquidityChange,
                                double volumeChange,
                                double buyVolume, double sellVolume,
                                double buyOrganicVolume, double sellOrganicVolume,
                                int numBuys, int numSells,
                                int numTraders,
                                int numOrganicBuyers,
                                int numNetBuyers) {

  public static JupiterTokenStats parse(final JsonIterator ji, final Duration duration) {
    return ji.parseObject(Parser.FIELDS, new JupiterTokenStats.Parser(duration));
  }

  private static final class Parser implements FieldIndexPredicate, Supplier<JupiterTokenStats> {

    private final Duration duration;
    private double priceChange;
    private double holderChange;
    private double liquidityChange;
    private double volumeChange;
    private double buyVolume;
    private double sellVolume;
    private double buyOrganicVolume;
    private double sellOrganicVolume;
    private int numBuys;
    private int numSells;
    private int numTraders;
    private int numOrganicBuyers;
    private int numNetBuyers;

    private Parser(final Duration duration) {
      this.duration = duration;
    }

    @Override
    public JupiterTokenStats get() {
      return new JupiterTokenStats(
          duration,
          priceChange, holderChange, liquidityChange, volumeChange,
          buyVolume, sellVolume, buyOrganicVolume, sellOrganicVolume,
          numBuys, numSells, numTraders, numOrganicBuyers, numNetBuyers
      );
    }

    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "priceChange",
        "holderChange",
        "liquidityChange",
        "volumeChange",
        "buyVolume",
        "sellVolume",
        "buyOrganicVolume",
        "sellOrganicVolume",
        "numBuys",
        "numSells",
        "numTraders",
        "numOrganicBuyers",
        "numNetBuyers"
    );

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> this.priceChange = ji.readDouble();
        case 1 -> this.holderChange = ji.readDouble();
        case 2 -> this.liquidityChange = ji.readDouble();
        case 3 -> this.volumeChange = ji.readDouble();
        case 4 -> this.buyVolume = ji.readDouble();
        case 5 -> this.sellVolume = ji.readDouble();
        case 6 -> this.buyOrganicVolume = ji.readDouble();
        case 7 -> this.sellOrganicVolume = ji.readDouble();
        case 8 -> this.numBuys = ji.readInt();
        case 9 -> this.numSells = ji.readInt();
        case 10 -> this.numTraders = ji.readInt();
        case 11 -> this.numOrganicBuyers = ji.readInt();
        case 12 -> this.numNetBuyers = ji.readInt();
        default -> ji.skip();
      }
      return true;
    }
  }
}
