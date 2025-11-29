package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;

public record JupiterSwapRequest(PublicKey userPublicKey,
                                 PublicKey payer,
                                 boolean wrapAndUnwrapSol,
                                 boolean useSharedAccounts,
                                 PublicKey feeAccount,
                                 PublicKey trackingAccount,
                                 PriorityFeeLamports prioritizationFeeLamports,
                                 JitoTip jitoTip,
                                 boolean asLegacyTransaction,
                                 PublicKey destinationTokenAccount,
                                 PublicKey nativeDestinationAccount,
                                 boolean dynamicComputeUnitLimit,
                                 int computeUnitPriceMicroLamports,
                                 boolean skipUserAccountsRpcCalls,
                                 int blockhashSlotsToExpiry) {

  public static Builder buildRequest() {
    return new Builder();
  }

  public StringBuilder preSerialize() {
    final var builder = new StringBuilder(1_024);
    builder.append("{\"userPublicKey\":\"").append(userPublicKey).append('"');
    if (payer != null) {
      builder.append(",\"payer\":\"").append(payer).append('"');
    }
    if (!wrapAndUnwrapSol) {
      builder.append(",\"wrapAndUnwrapSol\":false");
    }
    if (!useSharedAccounts) {
      builder.append(",\"useSharedAccounts\":false");
    }
    if (feeAccount != null) {
      builder.append(",\"feeAccount\":\"").append(feeAccount).append('"');
    }
    if (trackingAccount != null) {
      builder.append(",\"trackingAccount\":\"").append(trackingAccount).append('"');
    }

    if (prioritizationFeeLamports != null || jitoTip != null) {
      builder.append(",\"prioritizationFeeLamports\":{");
      if (prioritizationFeeLamports != null) {
        builder.append(prioritizationFeeLamports.toJson());
        if (jitoTip != null) {
          builder.append(",");
        }
      }
      if (jitoTip != null) {
        builder.append(jitoTip.toJson());
      }
      builder.append("}");
    }

    if (dynamicComputeUnitLimit) {
      builder.append(",\"dynamicComputeUnitLimit\": true");
    } else if (computeUnitPriceMicroLamports > 0) {
      builder.append(",\"computeUnitPriceMicroLamports\":").append(computeUnitPriceMicroLamports);
    }
    if (asLegacyTransaction) {
      builder.append(",\"asLegacyTransaction\":true");
    }
    if (destinationTokenAccount != null) {
      builder.append(",\"destinationTokenAccount\":\"").append(destinationTokenAccount).append('"');
    } else if (nativeDestinationAccount != null) {
      builder.append(",\"nativeDestinationAccount\":\"").append(nativeDestinationAccount).append('"');
    }
    if (skipUserAccountsRpcCalls) {
      builder.append(",\"skipUserAccountsRpcCalls\":true");
    }
    if (blockhashSlotsToExpiry > 0) {
      builder.append(",\"blockhashSlotsToExpiry\":").append(blockhashSlotsToExpiry);
    }
    return builder.append(",\"quoteResponse\":");
  }

  public static final class Builder {

    private PublicKey userPublicKey;
    private PublicKey payer;
    private boolean wrapAndUnwrapSol = true;
    private boolean useSharedAccounts = true;
    private PublicKey feeAccount;
    private PublicKey trackingAccount;
    private PriorityFeeLamports prioritizationFeeLamports;
    private JitoTip jitoTip;
    private boolean asLegacyTransaction;
    private PublicKey destinationTokenAccount;
    private PublicKey nativeDestinationAccount;
    private boolean dynamicComputeUnitLimit;
    private int computeUnitPriceMicroLamports = Integer.MIN_VALUE;
    private boolean skipUserAccountsRpcCalls = true;
    private int blockhashSlotsToExpiry;

    private Builder() {
    }

    public JupiterSwapRequest createRequest() {
      return new JupiterSwapRequest(
          userPublicKey,
          payer,
          wrapAndUnwrapSol,
          useSharedAccounts,
          feeAccount,
          trackingAccount,
          prioritizationFeeLamports,
          jitoTip,
          asLegacyTransaction,
          destinationTokenAccount,
          nativeDestinationAccount,
          dynamicComputeUnitLimit,
          computeUnitPriceMicroLamports,
          skipUserAccountsRpcCalls,
          blockhashSlotsToExpiry
      );
    }

    public Builder userPublicKey(final PublicKey userPublicKey) {
      this.userPublicKey = userPublicKey;
      return this;
    }

    public Builder payer(final PublicKey payer) {
      this.payer = payer;
      return this;
    }

    public Builder wrapAndUnwrapSol(final boolean wrapAndUnwrapSol) {
      this.wrapAndUnwrapSol = wrapAndUnwrapSol;
      return this;
    }

    public Builder useSharedAccounts(final boolean useSharedAccounts) {
      this.useSharedAccounts = useSharedAccounts;
      return this;
    }

    public Builder feeAccount(final PublicKey feeAccount) {
      this.feeAccount = feeAccount;
      return this;
    }

    public Builder trackingAccount(final PublicKey trackingAccount) {
      this.trackingAccount = trackingAccount;
      return this;
    }

    public Builder computeUnitPriceMicroLamports(final int computeUnitPriceMicroLamports) {
      this.computeUnitPriceMicroLamports = computeUnitPriceMicroLamports;
      return this;
    }

    public Builder prioritizationFeeLamports(final PriorityFeeLamports prioritizationFeeLamports) {
      this.prioritizationFeeLamports = prioritizationFeeLamports;
      return this;
    }

    public Builder jitoTip(final JitoTip jitoTip) {
      this.jitoTip = jitoTip;
      return this;
    }

    public Builder asLegacyTransaction(final boolean asLegacyTransaction) {
      this.asLegacyTransaction = asLegacyTransaction;
      return this;
    }

    public Builder destinationTokenAccount(final PublicKey destinationTokenAccount) {
      this.destinationTokenAccount = destinationTokenAccount;
      return this;
    }

    public Builder nativeDestinationAccount(final PublicKey nativeDestinationAccount) {
      this.nativeDestinationAccount = nativeDestinationAccount;
      return this;
    }

    public Builder dynamicComputeUnitLimit(final boolean dynamicComputeUnitLimit) {
      this.dynamicComputeUnitLimit = dynamicComputeUnitLimit;
      return this;
    }

    public Builder skipUserAccountsRpcCalls(final boolean skipUserAccountsRpcCalls) {
      this.skipUserAccountsRpcCalls = skipUserAccountsRpcCalls;
      return this;
    }

    public Builder blockhashSlotsToExpiry(final int blockhashSlotsToExpiry) {
      this.blockhashSlotsToExpiry = blockhashSlotsToExpiry;
      return this;
    }

    public PublicKey userPublicKey() {
      return userPublicKey;
    }

    public PublicKey payer() {
      return payer;
    }

    public boolean wrapAndUnwrapSol() {
      return wrapAndUnwrapSol;
    }

    public boolean useSharedAccounts() {
      return useSharedAccounts;
    }

    public PublicKey feeAccount() {
      return feeAccount;
    }

    public PublicKey trackingAccount() {
      return trackingAccount;
    }

    public int computeUnitPriceMicroLamports() {
      return computeUnitPriceMicroLamports;
    }

    public PriorityFeeLamports prioritizationFeeLamports() {
      return prioritizationFeeLamports;
    }

    public JitoTip jitoTip() {
      return jitoTip;
    }

    public boolean asLegacyTransaction() {
      return asLegacyTransaction;
    }

    public PublicKey destinationTokenAccount() {
      return destinationTokenAccount;
    }

    public PublicKey nativeDestinationAccount() {
      return nativeDestinationAccount;
    }

    public boolean dynamicComputeUnitLimit() {
      return dynamicComputeUnitLimit;
    }

    public boolean skipUserAccountsRpcCalls() {
      return skipUserAccountsRpcCalls;
    }

    public int blockhashSlotsToExpiry() {
      return blockhashSlotsToExpiry;
    }
  }
}
