package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;
import software.sava.rpc.json.PublicKeyEncoding;
import systems.comodal.jsoniter.FieldIndexPredicate;
import systems.comodal.jsoniter.FieldMatcher;
import systems.comodal.jsoniter.JsonIterator;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

// https://dev.jup.ag/api-reference/ultra/order
public record JupiterUltraOrderRequestRecord(PublicKey inputMint,
                                             PublicKey outputMint,
                                             BigInteger amount,
                                             PublicKey taker,
                                             PublicKey receiver,
                                             PublicKey payer,
                                             PublicKey closeAuthority,
                                             PublicKey referralAccount,
                                             int referralFeeBps,
                                             Set<String> excludeRouters,
                                             Set<String> excludeDexes) implements JupiterUltraOrderRequest {

  static final class Parser implements FieldIndexPredicate, Supplier<JupiterUltraOrderRequest> {

    private final Builder builder;

    Parser(final JupiterUltraOrderRequest prototype) {
      this.builder = JupiterUltraOrderRequest.build(prototype);
    }

    @Override
    public JupiterUltraOrderRequest get() {
      return builder.createRequest();
    }

    static final FieldMatcher FIELDS = FieldMatcher.of(
        "amount",
        "inputMint",
        "outputMint",
        "taker",
        "receiver",
        "payer",
        "closeAuthority",
        "referralAccount",
        "referralFeeBps"
    );

    @Override
    public boolean test(final int fieldIndex, final JsonIterator ji) {
      switch (fieldIndex) {
        case 0 -> builder.amount(ji.readBigInteger());
        case 1 -> builder.inputMint(PublicKeyEncoding.parseBase58Encoded(ji));
        case 2 -> builder.outputMint(PublicKeyEncoding.parseBase58Encoded(ji));
        case 3 -> builder.taker(PublicKeyEncoding.parseBase58Encoded(ji));
        case 4 -> builder.receiver(PublicKeyEncoding.parseBase58Encoded(ji));
        case 5 -> builder.payer(PublicKeyEncoding.parseBase58Encoded(ji));
        case 6 -> builder.closeAuthority(PublicKeyEncoding.parseBase58Encoded(ji));
        case 7 -> builder.referralAccount(PublicKeyEncoding.parseBase58Encoded(ji));
        case 8 -> builder.referralFeeBps(ji.readInt());
        default -> ji.skip();
      }
      return true;
    }
  }

  static final class BuilderImpl implements Builder {

    private PublicKey inputMint;
    private PublicKey outputMint;
    private BigInteger amount;
    private PublicKey taker;
    private PublicKey receiver;
    private PublicKey payer;
    private PublicKey closeAuthority;
    private PublicKey referralAccount;
    private int referralFeeBps;
    private Set<String> excludeRouters;
    private Set<String> excludeDexes;

    BuilderImpl() {
    }

    BuilderImpl(final JupiterUltraOrderRequest prototype) {
      this.amount = prototype.amount();
      this.inputMint = prototype.inputMint();
      this.outputMint = prototype.outputMint();
      this.taker = prototype.taker();
      this.receiver = prototype.receiver();
      this.payer = prototype.payer();
      this.closeAuthority = prototype.closeAuthority();
      this.referralAccount = prototype.referralAccount();
      this.referralFeeBps = prototype.referralFeeBps();
      this.excludeRouters = prototype.excludeRouters();
      this.excludeDexes = prototype.excludeDexes();
    }

    @Override
    public JupiterUltraOrderRequest createRequest() {
      return new JupiterUltraOrderRequestRecord(
          inputMint,
          outputMint,
          amount,
          taker,
          receiver,
          payer,
          closeAuthority,
          referralAccount,
          referralFeeBps,
          excludeRouters,
          excludeDexes
      );
    }

    @Override
    public Builder inputMint(final PublicKey inputMint) {
      this.inputMint = inputMint;
      return this;
    }

    @Override
    public Builder referralAccount(final PublicKey referralAccount) {
      this.referralAccount = referralAccount;
      return this;
    }

    @Override
    public Builder taker(final PublicKey taker) {
      this.taker = taker;
      return this;
    }

    @Override
    public Builder receiver(final PublicKey receiver) {
      this.receiver = receiver;
      return this;
    }

    @Override
    public Builder payer(final PublicKey payer) {
      this.payer = payer;
      return this;
    }

    @Override
    public Builder closeAuthority(final PublicKey closeAuthority) {
      this.closeAuthority = closeAuthority;
      return this;
    }

    @Override
    public Builder outputMint(final PublicKey outputMint) {
      this.outputMint = outputMint;
      return this;
    }

    @Override
    public Builder amount(final BigInteger amount) {
      this.amount = amount;
      return this;
    }

    @Override
    public Builder referralFeeBps(final int referralFeeBps) {
      this.referralFeeBps = referralFeeBps;
      return this;
    }

    @Override
    public Builder excludeRouters(final Set<String> excludeRouters) {
      this.excludeRouters = excludeRouters;
      return this;
    }

    @Override
    public Builder excludeRouter(final String excludeRouter) {
      if (this.excludeRouters == null) {
        this.excludeRouters = new HashSet<>();
      }
      this.excludeRouters.add(excludeRouter);
      return this;
    }

    @Override
    public Builder excludeDexes(final Set<String> excludeDexes) {
      this.excludeDexes = excludeDexes;
      return this;
    }

    @Override
    public Builder excludeDex(final String excludeDex) {
      if (this.excludeDexes == null) {
        this.excludeDexes = new HashSet<>();
      }
      this.excludeDexes.add(excludeDex);
      return this;
    }

    @Override
    public PublicKey inputMint() {
      return inputMint;
    }

    @Override
    public PublicKey outputMint() {
      return outputMint;
    }

    @Override
    public BigInteger amount() {
      return amount;
    }

    @Override
    public PublicKey taker() {
      return taker;
    }

    @Override
    public PublicKey referralAccount() {
      return referralAccount;
    }

    @Override
    public int referralFeeBps() {
      return referralFeeBps;
    }

    @Override
    public Set<String> excludeRouters() {
      return excludeRouters;
    }

    @Override
    public Set<String> excludeDexes() {
      return excludeDexes;
    }

    @Override
    public PublicKey receiver() {
      return receiver;
    }

    @Override
    public PublicKey payer() {
      return payer;
    }

    @Override
    public PublicKey closeAuthority() {
      return closeAuthority;
    }
  }
}
