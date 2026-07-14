package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.util.DecimalInteger;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldStartsWith;

public record JupiterTokenV2(PublicKey address,
                             String name,
                             String symbol,
                             String icon,
                             int decimals,
                             String twitter,
                             String telegram,
                             String website,
                             String dev,
                             BigDecimal circSupply,
                             BigDecimal totalSupply,
                             PublicKey tokenProgram,
                             String launchpad,
                             String partnerConfig,
                             String graduatePool,
                             Instant graduatedAt,
                             long holderCount,
                             BigDecimal fdv,
                             BigDecimal mcap,
                             double usdPrice,
                             BigInteger priceBlockId,
                             BigDecimal liquidity,
                             SequencedCollection<JupiterTokenStats> tokenStats,
                             TokenPool firstPool,
                             TokenAudit audit,
                             double organicScore,
                             String organicScoreLabel,
                             boolean verified,
                             Collection<String> cexes,
                             Collection<String> tags,
                             Instant updatedAt) implements DecimalInteger {

  public static JupiterTokenV2 parseToken(final JsonIterator ji) {
    return ji.parseObject(new JupiterTokenV2.Parser());
  }

  public static Map<PublicKey, JupiterTokenV2> parseTokens(final JsonIterator ji) {
    return ji.readMap(
        HashMap.newHashMap(2_048),
        JupiterTokenV2::parseToken,
        JupiterTokenV2::address
    );
  }

  private static final class Parser implements FieldBufferPredicate, Supplier<JupiterTokenV2> {

    private static final List<JupiterTokenStats> NO_STATS = List.of();

    private PublicKey address;
    private String name;
    private String symbol;
    private String icon;
    private int decimals;
    private String twitter;
    private String telegram;
    private String website;
    private String dev;
    private BigDecimal circSupply;
    private BigDecimal totalSupply;
    private PublicKey tokenProgram;
    private String launchpad;
    private String partnerConfig;
    private String graduatePool;
    private Instant graduatedAt;
    private long holderCount;
    private double organicScore;
    private String organicScoreLabel;
    private boolean verified;
    private Collection<String> cexes;
    private Collection<String> tags;
    private BigDecimal fdv;
    private BigDecimal mcap;
    private double usdPrice;
    private BigInteger priceBlockId;
    private BigDecimal liquidity;
    private List<JupiterTokenStats> tokenStats;
    private TokenPool firstPool;
    private TokenAudit audit;
    private Instant updatedAt;

    private Parser() {
    }

    @Override
    public JupiterTokenV2 get() {
      return new JupiterTokenV2(
          address,
          name,
          symbol,
          icon,
          decimals,
          twitter,
          telegram,
          website,
          dev,
          Objects.requireNonNullElse(circSupply, BigDecimal.ZERO),
          Objects.requireNonNullElse(totalSupply, BigDecimal.ZERO),
          tokenProgram,
          launchpad,
          partnerConfig,
          graduatePool,
          graduatedAt,
          holderCount,
          Objects.requireNonNullElse(fdv, BigDecimal.ZERO),
          Objects.requireNonNullElse(mcap, BigDecimal.ZERO),
          usdPrice,
          priceBlockId,
          Objects.requireNonNullElse(liquidity, BigDecimal.ZERO),
          Objects.requireNonNullElse(tokenStats, NO_STATS),
          firstPool,
          audit,
          organicScore,
          organicScoreLabel,
          verified,
          cexes,
          tags,
          updatedAt
      );
    }

    // Buffer predicate retained for span access: the stats* field names carry
    // the window duration (e.g. stats5m, stats24h) and are matched by prefix.
    private static final FieldMatcher FIELDS = FieldMatcher.of(
        "id",
        "name",
        "symbol",
        "icon",
        "decimals",
        "twitter",
        "telegram",
        "website",
        "dev",
        "circSupply",
        "totalSupply",
        "tokenProgram",
        "launchpad",
        "partnerConfig",
        "graduatePool",
        "graduatedAt",
        "holderCount",
        "organicScore",
        "organicScoreLabel",
        "isVerified",
        "cexes",
        "tags",
        "fdv",
        "mcap",
        "usdPrice",
        "priceBlockId",
        "liquidity",
        "firstPool",
        "audit",
        "updatedAt"
    );

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      switch (FIELDS.match(buf, offset, len)) {
        case 0 -> this.address = parseBase58Encoded(ji);
        case 1 -> this.name = ji.readString();
        case 2 -> this.symbol = ji.readString();
        case 3 -> this.icon = ji.readString();
        case 4 -> this.decimals = ji.readInt();
        case 5 -> this.twitter = ji.readString();
        case 6 -> this.telegram = ji.readString();
        case 7 -> this.website = ji.readString();
        case 8 -> this.dev = ji.readString();
        case 9 -> this.circSupply = ji.readBigDecimal();
        case 10 -> this.totalSupply = ji.readBigDecimal();
        case 11 -> this.tokenProgram = parseBase58Encoded(ji);
        case 12 -> this.launchpad = ji.readString();
        case 13 -> this.partnerConfig = ji.readString();
        case 14 -> this.graduatePool = ji.readString();
        case 15 -> this.graduatedAt = ji.readDateTime();
        case 16 -> this.holderCount = ji.readLong();
        case 17 -> this.organicScore = ji.readDouble();
        case 18 -> this.organicScoreLabel = ji.readString();
        case 19 -> this.verified = ji.readBoolean();
        case 20 -> this.cexes = ji.readList(JsonIterator::readString);
        case 21 -> this.tags = ji.readList(JsonIterator::readString);
        case 22 -> this.fdv = ji.readBigDecimal();
        case 23 -> this.mcap = ji.readBigDecimal();
        case 24 -> this.usdPrice = ji.readDouble();
        case 25 -> this.priceBlockId = ji.readBigInteger();
        case 26 -> this.liquidity = ji.readBigDecimal();
        case 27 -> this.firstPool = TokenPool.parse(ji);
        case 28 -> this.audit = TokenAudit.parse(ji);
        case 29 -> this.updatedAt = ji.readDateTime();
        default -> {
          if (fieldStartsWith("stats", buf, offset, len)) {
            final int unitOffset = offset + len - 1;
            final var unit = switch (buf[unitOffset]) {
              case 'm' -> MINUTES;
              case 'h' -> HOURS;
              default -> null;
            };
            if (unit == null) {
              ji.skip();
            } else {
              if (tokenStats == null) {
                this.tokenStats = new ArrayList<>();
              }
              final int from = offset + 5;
              final int duration = Integer.parseInt(new String(buf, from, unitOffset - from));
              final var tokenStats = JupiterTokenStats.parse(ji, Duration.of(duration, unit));
              this.tokenStats.add(tokenStats);
            }
          } else {
            ji.skip();
          }
        }
      }
      return true;
    }
  }
}
