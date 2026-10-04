package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;

/// Shared Swap API V2 fixtures: the keys the request examples and response bodies use, each as a
/// base58 `String` and as a matching `PublicKey` (`<NAME>_KEY`), and inline response bodies shaped
/// from Jupiter's `swap.yaml` with real base58 keys.
///
/// The bodies are hand-written, never captured from the network, and every expectation a test
/// asserts against them is written out as a literal. `BUILD`, `BUILD_MINIMAL`,
/// `BUILD_STRAY_COMPUTE_BUDGET`, `ORDER_METIS`, `ORDER_RFQ_BUILD_FAILED`, `ORDER_QUOTE_ONLY` and
/// `EXECUTE_SUCCESS` are also the bodies of the `jupiterResponse` fuzz seeds.
///
/// The class name carries `Test` so that the mutation suites treat it as test code.
public final class JupiterSwapV2TestFixtures {

  // Keys of the request examples.

  public static final String WSOL = "So11111111111111111111111111111111111111112";
  public static final String USDC = "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v";
  public static final String TAKER = "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ";
  public static final String PAYER = "gasTzr94Pmp4Gf8vknQnqxeYxdgwFjbgdJa4msYRpnB";
  public static final String FEE_ACCOUNT = "CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48";
  public static final String DEST = "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR";
  public static final String OTHER = "9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn";
  public static final String REFERRAL = "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT";

  // Keys of the response bodies.

  public static final String ATA_PROGRAM = "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL";
  public static final String TOKEN_PROGRAM = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA";
  public static final String SYSTEM = "11111111111111111111111111111111";
  public static final String JUP = "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4";
  public static final String EVENT_AUTHORITY = "D8cy77BBepLMngZx6ZukaTff5hCt1HrWyKk3Hnd9oitf";
  // One of Jupiter's 16 tip receivers (transaction/submit.mdx); /build's tipAmount pays one of them,
  // never a Jito tip account.
  public static final String TIP = "GGztQqQ6pCPaJQnNpXBgELr5cs3WwDakRbh1iEMzjgSJ";
  public static final String WSOL_ATA = "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR";
  public static final String USDC_ATA = "CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48";
  public static final String AMM_A = "3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL";
  public static final String AMM_B = "675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8";
  public static final String VAULT_A = "3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3";
  public static final String VAULT_B = "3NAM1YJMhSPvtAkmGTRABe1hYZN3aE2hZHKy3JZy9fHk";
  public static final String ALT = "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT";
  public static final String MEMO = "MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr";
  public static final String CB = "ComputeBudget111111111111111111111111111111";

  public static final PublicKey WSOL_KEY = PublicKey.fromBase58Encoded(WSOL);
  public static final PublicKey USDC_KEY = PublicKey.fromBase58Encoded(USDC);
  public static final PublicKey TAKER_KEY = PublicKey.fromBase58Encoded(TAKER);
  public static final PublicKey PAYER_KEY = PublicKey.fromBase58Encoded(PAYER);
  public static final PublicKey FEE_ACCOUNT_KEY = PublicKey.fromBase58Encoded(FEE_ACCOUNT);
  public static final PublicKey DEST_KEY = PublicKey.fromBase58Encoded(DEST);
  public static final PublicKey OTHER_KEY = PublicKey.fromBase58Encoded(OTHER);
  public static final PublicKey REFERRAL_KEY = PublicKey.fromBase58Encoded(REFERRAL);

  public static final PublicKey ATA_PROGRAM_KEY = PublicKey.fromBase58Encoded(ATA_PROGRAM);
  public static final PublicKey TOKEN_PROGRAM_KEY = PublicKey.fromBase58Encoded(TOKEN_PROGRAM);
  public static final PublicKey SYSTEM_KEY = PublicKey.fromBase58Encoded(SYSTEM);
  public static final PublicKey JUP_KEY = PublicKey.fromBase58Encoded(JUP);
  public static final PublicKey EVENT_AUTHORITY_KEY = PublicKey.fromBase58Encoded(EVENT_AUTHORITY);
  public static final PublicKey TIP_KEY = PublicKey.fromBase58Encoded(TIP);
  public static final PublicKey WSOL_ATA_KEY = PublicKey.fromBase58Encoded(WSOL_ATA);
  public static final PublicKey USDC_ATA_KEY = PublicKey.fromBase58Encoded(USDC_ATA);
  public static final PublicKey AMM_A_KEY = PublicKey.fromBase58Encoded(AMM_A);
  public static final PublicKey AMM_B_KEY = PublicKey.fromBase58Encoded(AMM_B);
  public static final PublicKey VAULT_A_KEY = PublicKey.fromBase58Encoded(VAULT_A);
  public static final PublicKey VAULT_B_KEY = PublicKey.fromBase58Encoded(VAULT_B);
  public static final PublicKey ALT_KEY = PublicKey.fromBase58Encoded(ALT);
  public static final PublicKey MEMO_KEY = PublicKey.fromBase58Encoded(MEMO);
  public static final PublicKey CB_KEY = PublicKey.fromBase58Encoded(CB);

  /// A full `/build` response: every instruction slot filled except `otherInstructions`, which is
  /// empty, a two-step route with fractional percentages, one lookup table, and the blockhash with
  /// its `fetchedAt`.
  public static final String BUILD = """
      {
        "inputMint": "So11111111111111111111111111111111111111112",
        "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
        "inAmount": "100000000",
        "outAmount": "461208958",
        "otherAmountThreshold": "460024271",
        "swapMode": "ExactIn",
        "slippageBps": 50,
        "priceImpactPct": "0.0001",
        "routePlan": [
          {"swapInfo": {"ammKey": "3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", "label": "Meteora DLMM",
            "inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
            "inAmount": "66670000", "outAmount": "307498012"}, "percent": 66.67, "bps": 6667, "usdValue": 133.36},
          {"swapInfo": {"ammKey": "675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8", "label": "Raydium",
            "inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
            "inAmount": "33330000", "outAmount": "153710946"}, "percent": 33.33, "bps": 3333}
        ],
        "computeBudgetInstructions": [
          {"programId": "ComputeBudget111111111111111111111111111111", "accounts": [], "data": "A4fWEgAAAAAA"}
        ],
        "setupInstructions": [
          {"programId": "ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL", "accounts": [
            {"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": true},
            {"pubkey": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR", "isSigner": false, "isWritable": true},
            {"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": false, "isWritable": false},
            {"pubkey": "So11111111111111111111111111111111111111112", "isSigner": false, "isWritable": false},
            {"pubkey": "11111111111111111111111111111111", "isSigner": false, "isWritable": false},
            {"pubkey": "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", "isSigner": false, "isWritable": false}
          ], "data": "AQ=="},
          {"programId": "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", "accounts": [
            {"pubkey": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR", "isSigner": false, "isWritable": true}
          ], "data": "EQ=="}
        ],
        "swapInstruction": {"programId": "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4", "accounts": [
          {"pubkey": "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", "isSigner": false, "isWritable": false},
          {"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": false},
          {"pubkey": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR", "isSigner": false, "isWritable": true},
          {"pubkey": "CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48", "isSigner": false, "isWritable": true},
          {"pubkey": "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4", "isSigner": false, "isWritable": false},
          {"pubkey": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "isSigner": false, "isWritable": false},
          {"pubkey": "D8cy77BBepLMngZx6ZukaTff5hCt1HrWyKk3Hnd9oitf", "isSigner": false, "isWritable": false},
          {"pubkey": "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4", "isSigner": false, "isWritable": false},
          {"pubkey": "3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", "isSigner": false, "isWritable": true},
          {"pubkey": "3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3", "isSigner": false, "isWritable": true},
          {"pubkey": "3NAM1YJMhSPvtAkmGTRABe1hYZN3aE2hZHKy3JZy9fHk", "isSigner": false, "isWritable": true}
        ], "data": "u2T6zDHErxQA4fUFAAAAAH59fRsAAAAAMgAAAAAAAAAAAA=="},
        "cleanupInstruction": {"programId": "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA", "accounts": [
          {"pubkey": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR", "isSigner": false, "isWritable": true},
          {"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": false, "isWritable": true},
          {"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": false}
        ], "data": "CQ=="},
        "otherInstructions": [],
        "tipInstruction": {"programId": "11111111111111111111111111111111", "accounts": [
          {"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": true},
          {"pubkey": "GGztQqQ6pCPaJQnNpXBgELr5cs3WwDakRbh1iEMzjgSJ", "isSigner": false, "isWritable": true}
        ], "data": "AgAAAEBCDwAAAAAA"},
        "addressesByLookupTableAddress": {"3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT": [
          "3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3", "3NAM1YJMhSPvtAkmGTRABe1hYZN3aE2hZHKy3JZy9fHk",
          "D8cy77BBepLMngZx6ZukaTff5hCt1HrWyKk3Hnd9oitf"]},
        "blockhashWithMetadata": {"blockhash": [1,8,15,22,29,36,43,50,57,64,71,78,85,92,99,106,113,120,127,134,141,148,155,162,169,176,183,190,197,204,211,218],
          "lastValidBlockHeight": 279000150,
          "fetchedAt": {"secs_since_epoch": 1759480000, "nanos_since_epoch": 123456789}}
      }
      """;

  /// The docs' TypeScript shape of a `/build` response: no `priceImpactPct`, `usdValue` or
  /// `fetchedAt`, and explicit nulls for the optional instructions and the lookup tables.
  public static final String BUILD_MINIMAL = """
      {"inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
       "inAmount": "1000000", "outAmount": "4612089", "otherAmountThreshold": "4589028", "swapMode": "ExactIn", "slippageBps": 50,
       "routePlan": [{"percent": 100, "bps": 10000, "swapInfo": {"ammKey": "3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL",
         "label": "Meteora DLMM", "inputMint": "So11111111111111111111111111111111111111112",
         "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "inAmount": "1000000", "outAmount": "4612089"}}],
       "computeBudgetInstructions": [], "setupInstructions": [],
       "swapInstruction": {"programId": "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4",
         "accounts": [{"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": false}], "data": "u2T6zDHErxQ="},
       "cleanupInstruction": null, "otherInstructions": [], "tipInstruction": null, "addressesByLookupTableAddress": null,
       "blockhashWithMetadata": {"blockhash": [1,8,15,22,29,36,43,50,57,64,71,78,85,92,99,106,113,120,127,134,141,148,155,162,169,176,183,190,197,204,211,218],
         "lastValidBlockHeight": 279000150}}
      """;

  /// A ComputeBudget instruction outside its list (a SetComputeUnitLimit in `setupInstructions`)
  /// and a memo instruction inside `computeBudgetInstructions`.
  public static final String BUILD_STRAY_COMPUTE_BUDGET = """
      {"inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
       "inAmount": "1000000", "outAmount": "4612089", "otherAmountThreshold": "4589028", "swapMode": "ExactIn", "slippageBps": 50,
       "routePlan": [],
       "computeBudgetInstructions": [
         {"programId": "MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr", "accounts": [], "data": "aGk="},
         {"programId": "ComputeBudget111111111111111111111111111111", "accounts": [], "data": "A0BCDwAAAAAA"}],
       "setupInstructions": [{"programId": "ComputeBudget111111111111111111111111111111", "accounts": [], "data": "AkANAwA="}],
       "swapInstruction": {"programId": "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4",
         "accounts": [{"pubkey": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ", "isSigner": true, "isWritable": false}], "data": "u2T6zDHErxQ="},
       "cleanupInstruction": null, "otherInstructions": [], "tipInstruction": null}
      """;

  /// An `/order` won by Metis with a taker, so it carries a transaction; shaped from Jupiter's Ultra
  /// order example with real keys and the V2 fields, including the deprecated `swapType` and
  /// `priceImpactPct`.
  public static final String ORDER_METIS = """
      {"mode": "ultra", "inputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputMint": "So11111111111111111111111111111111111111112",
       "inAmount": "100000000", "outAmount": "461208958", "inUsdValue": 99.96761068334662, "outUsdValue": 99.95449893632635,
       "priceImpact": -0.013115995201493341, "swapUsdValue": 99.96761068334662, "otherAmountThreshold": "460024271",
       "swapMode": "ExactIn", "slippageBps": 26, "priceImpactPct": "-0.0001311599520149334",
       "routePlan": [
         {"swapInfo": {"ammKey": "HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR", "label": "MeteoraDLMM",
           "inputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputMint": "So11111111111111111111111111111111111111112",
           "inAmount": "52000000", "outAmount": "239879552", "feeAmount": "0", "feeMint": "11111111111111111111111111111111"},
          "percent": 52, "bps": 5200},
         {"swapInfo": {"ammKey": "3EKkiwNLWqoUbzFkPrmKbtUB4EweE6f4STzevYUmezeL", "label": "SolFi",
           "inputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputMint": "So11111111111111111111111111111111111111112",
           "inAmount": "48000000", "outAmount": "221329406"}, "percent": 48, "bps": 4800, "usdValue": 47.98}],
       "referralAccount": "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT",
       "feeMint": "So11111111111111111111111111111111111111112", "feeBps": 2,
       "platformFee": {"amount": "92241", "feeBps": 2, "feeMint": "So11111111111111111111111111111111111111112"},
       "signatureFeeLamports": 5000, "signatureFeePayer": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
       "prioritizationFeeLamports": 696237, "prioritizationFeePayer": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
       "rentFeeLamports": 0, "rentFeePayer": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
       "swapType": "aggregator", "router": "metis", "transaction": "AQID", "lastValidBlockHeight": "279000150",
       "gasless": false, "requestId": "019974a8-5fbb-7395-9355-9ebf8f844884", "totalTime": 359,
       "taker": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ"}
      """;

  /// An RFQ `/order` with a taker whose transaction could not be built: `transaction` is `""`, and
  /// so is `lastValidBlockHeight`, which must read as 0 without costing the caller `errorCode`.
  public static final String ORDER_RFQ_BUILD_FAILED = """
      {"mode": "ultra", "inputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputMint": "So11111111111111111111111111111111111111112",
       "inAmount": "100000000", "outAmount": "460250418", "otherAmountThreshold": "460250418", "swapMode": "ExactIn", "slippageBps": 0,
       "routePlan": [{"swapInfo": {"ammKey": "CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48", "label": "JupiterZ",
         "inputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputMint": "So11111111111111111111111111111111111111112",
         "inAmount": "100000000", "outAmount": "460250418"}, "percent": 100, "bps": 10000}],
       "feeBps": 2.5, "platformFee": {"amount": "92050", "feeBps": 0.5}, "gasless": true,
       "signatureFeeLamports": 0, "prioritizationFeeLamports": 0, "rentFeeLamports": 0,
       "swapType": "rfq", "router": "jupiterz", "transaction": "", "lastValidBlockHeight": "",
       "requestId": "ff63982b-9140-9b0e-e525-44f7246a79b2", "quoteId": "5852f88e-525b-5400-ab97-abe5e409ebfd",
       "maker": "CDg3bPoM21fSXEzrXWHWyJR33JHX6xaYboq5p7s4uo48", "taker": "GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ",
       "expireAt": "1758598698", "totalTime": 489.5, "errorCode": 3,
       "errorMessage": "Quote could not be built into a transaction", "error": "Quote could not be built into a transaction"}
      """;

  /// An `/order` without a taker: `transaction` is null and the fee payers are null.
  public static final String ORDER_QUOTE_ONLY = """
      {"mode": "ultra", "inputMint": "So11111111111111111111111111111111111111112", "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v",
       "inAmount": "1000000", "outAmount": "4612089", "otherAmountThreshold": "4600559", "swapMode": "ExactIn", "slippageBps": 25,
       "routePlan": [], "feeMint": "So11111111111111111111111111111111111111112", "feeBps": 2,
       "platformFee": {"feeBps": 2, "feeMint": "So11111111111111111111111111111111111111112"},
       "signatureFeeLamports": 5000, "signatureFeePayer": null, "prioritizationFeeLamports": 0, "prioritizationFeePayer": null,
       "rentFeeLamports": 0, "rentFeePayer": null, "router": "metis", "transaction": null, "gasless": false,
       "requestId": "01997500-0000-7000-8000-000000000001", "totalTime": 120, "taker": null}
      """;

  /// A successful `/execute` result: Jupiter's Ultra example plus the two totals.
  public static final String EXECUTE_SUCCESS = """
      {"status": "Success", "signature": "transaction-signature", "slot": "323598314", "code": 0,
       "totalInputAmount": "10000000", "totalOutputAmount": "1274698", "inputAmountResult": "9995000", "outputAmountResult": "1274698",
       "swapEvents": [{"inputMint": "So11111111111111111111111111111111111111112", "inputAmount": "9995000",
         "outputMint": "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v", "outputAmount": "1274698"}]}
      """;

  /// A `/program-id-to-label` body with arbitrary but valid program keys, a label two programs
  /// share, and two labels that differ only in case.
  public static final String PROGRAM_ID_TO_LABEL = """
      {"SoLFiHG9TfgtdUXUjWAxi3LtvYuFyDLVhBWxdMZxyCe": "SolFi", "whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc": "Whirlpool",
       "LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo": "Meteora DLMM", "675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8": "Raydium",
       "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT": "Sanctum", "9ahYBF9J6simiVmTPwRgxB1t1AiZY5C8xpeLQGh5tJxn": "Sanctum",
       "D8cy77BBepLMngZx6ZukaTff5hCt1HrWyKk3Hnd9oitf": "solfi"}
      """;

  private JupiterSwapV2TestFixtures() {
  }
}
