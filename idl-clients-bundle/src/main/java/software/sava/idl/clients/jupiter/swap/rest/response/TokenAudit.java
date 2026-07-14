package software.sava.idl.clients.jupiter.swap.rest.response;

import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.util.function.Supplier;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record TokenAudit(boolean isSus,
                         boolean mintAuthorityDisabled,
                         boolean freezeAuthorityDisabled,
                         double topHoldersPercentage,
                         double devBalancePercentage,
                         int devMigrations) {

  public static TokenAudit parse(final JsonIterator ji) {
    return ji.parseObject(new Parser());
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<TokenAudit> {

    private boolean isSus;
    private boolean mintAuthorityDisabled;
    private boolean freezeAuthorityDisabled;
    private double topHoldersPercentage;
    private double devBalancePercentage;
    private int devMigrations;

    @Override
    public TokenAudit get() {
      return new TokenAudit(
          isSus,
          mintAuthorityDisabled,
          freezeAuthorityDisabled,
          topHoldersPercentage,
          devBalancePercentage,
          devMigrations
      );
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("isSus", buf, offset, len)) {
        this.isSus = ji.readBoolean();
      } else if (fieldEquals("mintAuthorityDisabled", buf, offset, len)) {
        this.mintAuthorityDisabled = ji.readBoolean();
      } else if (fieldEquals("freezeAuthorityDisabled", buf, offset, len)) {
        this.freezeAuthorityDisabled = ji.readBoolean();
      } else if (fieldEquals("topHoldersPercentage", buf, offset, len)) {
        this.topHoldersPercentage = ji.readDouble();
      } else if (fieldEquals("devBalancePercentage", buf, offset, len)) {
        this.devBalancePercentage = ji.readDouble();
      } else if (fieldEquals("devMigrations", buf, offset, len)) {
        this.devMigrations = ji.readInt();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
