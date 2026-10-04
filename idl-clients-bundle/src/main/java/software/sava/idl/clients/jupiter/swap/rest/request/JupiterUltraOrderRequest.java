package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.US_ASCII;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.Set;

/// @deprecated Builds Ultra /ultra/v1/order queries. Use [JupiterSwapOrderRequest]: build() becomes buildRequest(),
/// amount(BigInteger) becomes amount(long), read as an unsigned u64, referralFeeBps(n) becomes referralFee(n), the
/// singular excludeRouter/excludeDex setters give way to excludeRouters/excludeDexes, and closeAuthority has no V2
/// equivalent. Unlike this builder, V2 throws IllegalStateException before sending a receiver equal to the taker
/// (Ultra's default, which this builder sends), a referralAccount without a referralFee or the reverse, or no amount.
/// Its setters also throw IllegalArgumentException for a router name or DEX label that is null, blank, padded with
/// whitespace or contains a comma, which this builder sends as given, and for a negative referralFee, which this
/// builder drops.
@Deprecated
public interface JupiterUltraOrderRequest {

  static Builder build() {
    return new JupiterUltraOrderRequestRecord.BuilderImpl();
  }

  static Builder build(final JupiterUltraOrderRequest prototype) {
    return prototype == null ? build() : new JupiterUltraOrderRequestRecord.BuilderImpl(prototype);
  }

  /// Reads `amount`, `inputMint`, `outputMint`, `taker`, `receiver`, `payer`, `closeAuthority`, `referralAccount` and
  /// the referral fee, on top of `prototype` when it is not null. Every other field is skipped, `excludeRouters` and
  /// `excludeDexes` included; set those through `build(parsed)`.
  ///
  /// The referral fee is read from `referralFee`, the name the Ultra API documents and [#serialize()] sends, or from
  /// `referralFeeBps`, the name of the Java accessor [#referralFeeBps()]. Either name sets the same value; when a
  /// document carries both, the later one wins.
  static JupiterUltraOrderRequest parseRequest(final JupiterUltraOrderRequest prototype, final JsonIterator ji) {
    return ji.parseObject(JupiterUltraOrderRequestRecord.Parser.FIELDS, new JupiterUltraOrderRequestRecord.Parser(prototype));
  }

  /// Reads a request from a JSON object without a prototype. See
  /// [#parseRequest(JupiterUltraOrderRequest, JsonIterator)].
  static JupiterUltraOrderRequest parseRequest(final JsonIterator ji) {
    return parseRequest(null, ji);
  }

  PublicKey inputMint();

  PublicKey outputMint();

  BigInteger amount();

  PublicKey taker();

  PublicKey referralAccount();

  /// The referral fee in basis points. [#serialize()] sends it as `referralFee`, the name the Ultra `/order` API
  /// documents, and only when it is above 0. Jupiter documents 50 to 255 and takes the fee together with
  /// [#referralAccount()].
  int referralFeeBps();

  Set<String> excludeRouters();

  Set<String> excludeDexes();

  /// Serializes this request as the query string of an Ultra `/order` call, without a leading `?`.
  ///
  /// The query starts with `inputMint` and `outputMint`. Every other parameter is omitted when it is unset, and an
  /// `amount` or a referral fee that is not above 0 counts as unset. The referral fee is sent as `referralFee=<n>`,
  /// after `referralAccount` and before `excludeRouters`. The Java name `referralFeeBps` is never a query parameter.
  default String serialize() {
    final var builder = new StringBuilder(256);
    builder.append("inputMint=").append(inputMint().toBase58());
    builder.append("&outputMint=").append(outputMint().toBase58());
    final var amount = amount();
    if (amount != null && amount.signum() > 0) {
      builder.append("&amount=").append(amount);
    }
    if (taker() != null) {
      builder.append("&taker=").append(taker().toBase58());
    }
    if (receiver() != null) {
      builder.append("&receiver=").append(receiver().toBase58());
    }
    if (payer() != null) {
      builder.append("&payer=").append(payer().toBase58());
    }
    if (closeAuthority() != null) {
      builder.append("&closeAuthority=").append(closeAuthority().toBase58());
    }
    if (referralAccount() != null) {
      builder.append("&referralAccount=").append(referralAccount().toBase58());
    }
    if (referralFeeBps() > 0) {
      builder.append("&referralFee=").append(referralFeeBps());
    }
    final var excludeRouters = excludeRouters();
    if (excludeRouters != null && !excludeRouters.isEmpty()) {
      builder.append("&excludeRouters=").append(URLEncoder.encode(String.join(",", excludeRouters), US_ASCII));
    }
    final var excludeDexes = excludeDexes();
    if (excludeDexes != null && !excludeDexes.isEmpty()) {
      builder.append("&excludeDexes=").append(URLEncoder.encode(String.join(",", excludeDexes), US_ASCII));
    }
    return builder.toString();
  }

  PublicKey receiver();

  PublicKey payer();

  PublicKey closeAuthority();

  interface Builder extends JupiterUltraOrderRequest {

    JupiterUltraOrderRequest createRequest();

    Builder inputMint(final PublicKey inputMint);

    Builder outputMint(final PublicKey outputMint);

    Builder amount(final BigInteger amount);

    Builder taker(final PublicKey taker);

    Builder receiver(final PublicKey receiver);

    Builder payer(final PublicKey payer);

    Builder closeAuthority(final PublicKey closeAuthority);

    Builder referralAccount(final PublicKey referralAccount);

    /// Sets the referral fee in basis points, which [#serialize()] sends as `referralFee`. See [#referralFeeBps()].
    Builder referralFeeBps(final int referralFeeBps);

    Builder excludeRouters(final Set<String> excludeRouters);

    Builder excludeRouter(final String excludeRouter);

    Builder excludeDexes(final Set<String> excludeDexes);

    Builder excludeDex(final String excludeDex);
  }
}
