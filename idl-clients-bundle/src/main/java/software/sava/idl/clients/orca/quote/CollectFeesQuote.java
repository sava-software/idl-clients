package software.sava.idl.clients.orca.quote;

/// Quote for `collectFees`/`collectFeesV2`. `feeOwedA`/`feeOwedB` are u64 token
/// amounts returned as unsigned `long`s, already adjusted for any Token-2022
/// transfer fees passed to the quote. Mirrors Rust `CollectFeesQuote`.
public record CollectFeesQuote(long feeOwedA, long feeOwedB) {
}
