package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.util.DecimalInteger;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;
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
    final var parser = new JupiterTokenV2.Parser();
    ji.testObject(parser);
    return parser.create();
  }

  public static Map<PublicKey, JupiterTokenV2> parseTokens(final JsonIterator ji) {
    final var tokens = HashMap.<PublicKey, JupiterTokenV2>newHashMap(1_024);
    while (ji.readArray()) {
      final var token = parseToken(ji);
      tokens.put(token.address(), token);
    }
    return tokens;
  }

  private static final class Parser implements FieldBufferPredicate {

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

    private JupiterTokenV2 create() {
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

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("id", buf, offset, len)) {
        this.address = parseBase58Encoded(ji);
      } else if (fieldEquals("name", buf, offset, len)) {
        this.name = ji.readString();
      } else if (fieldEquals("symbol", buf, offset, len)) {
        this.symbol = ji.readString();
      } else if (fieldEquals("icon", buf, offset, len)) {
        this.icon = ji.readString();
      } else if (fieldEquals("decimals", buf, offset, len)) {
        this.decimals = ji.readInt();
      } else if (fieldEquals("twitter", buf, offset, len)) {
        this.twitter = ji.readString();
      } else if (fieldEquals("telegram", buf, offset, len)) {
        this.telegram = ji.readString();
      } else if (fieldEquals("website", buf, offset, len)) {
        this.website = ji.readString();
      } else if (fieldEquals("dev", buf, offset, len)) {
        this.dev = ji.readString();
      } else if (fieldEquals("circSupply", buf, offset, len)) {
        this.circSupply = ji.readBigDecimal();
      } else if (fieldEquals("totalSupply", buf, offset, len)) {
        this.totalSupply = ji.readBigDecimal();
      } else if (fieldEquals("tokenProgram", buf, offset, len)) {
        this.tokenProgram = parseBase58Encoded(ji);
      } else if (fieldEquals("launchpad", buf, offset, len)) {
        this.launchpad = ji.readString();
      } else if (fieldEquals("partnerConfig", buf, offset, len)) {
        this.partnerConfig = ji.readString();
      } else if (fieldEquals("graduatePool", buf, offset, len)) {
        this.graduatePool = ji.readString();
      } else if (fieldEquals("graduatedAt", buf, offset, len)) {
        this.graduatedAt = ji.readDateTime();
      } else if (fieldEquals("holderCount", buf, offset, len)) {
        this.holderCount = ji.readLong();
      } else if (fieldEquals("organicScore", buf, offset, len)) {
        this.organicScore = ji.readDouble();
      } else if (fieldEquals("organicScoreLabel", buf, offset, len)) {
        this.organicScoreLabel = ji.readString();
      } else if (fieldEquals("isVerified", buf, offset, len)) {
        this.verified = ji.readBoolean();
      } else if (fieldEquals("cexes", buf, offset, len)) {
        final var cexes = new ArrayList<String>();
        while (ji.readArray()) {
          cexes.add(ji.readString());
        }
        this.cexes = cexes;
      } else if (fieldEquals("tags", buf, offset, len)) {
        final var tags = new ArrayList<String>();
        while (ji.readArray()) {
          tags.add(ji.readString());
        }
        this.tags = tags;
      } else if (fieldEquals("fdv", buf, offset, len)) {
        this.fdv = ji.readBigDecimal();
      } else if (fieldEquals("mcap", buf, offset, len)) {
        this.mcap = ji.readBigDecimal();
      } else if (fieldEquals("usdPrice", buf, offset, len)) {
        this.usdPrice = ji.readDouble();
      } else if (fieldEquals("priceBlockId", buf, offset, len)) {
        this.priceBlockId = ji.readBigInteger();
      } else if (fieldEquals("liquidity", buf, offset, len)) {
        this.liquidity = ji.readBigDecimal();
      } else if (fieldEquals("firstPool", buf, offset, len)) {
        this.firstPool = TokenPool.parse(ji);
      } else if (fieldEquals("audit", buf, offset, len)) {
        this.audit = TokenAudit.parse(ji);
      } else if (fieldStartsWith("stats", buf, offset, len)) {
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
      } else if (fieldEquals("updatedAt", buf, offset, len)) {
        this.updatedAt = ji.readDateTime();
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
