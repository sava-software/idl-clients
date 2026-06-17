package software.sava.idl.clients.jupiter.swap.rest.request;

import software.sava.core.accounts.PublicKey;

public record JitoTip(long lamports, PublicKey payer) {

  String toJson() {
    if (payer == null) {
      return String.format("""
          "jitoTipLamports": {"lamports": %d}
          """, lamports
      );
    } else {
      return String.format("""
          "jitoTipLamportsWithPayer": {"lamports": %d, "payer": "%s"}
          """, lamports, payer
      );
    }
  }
}
