package software.sava.idl.clients.orca.quote;

/// Per-reward quote within a `CollectRewardsQuote`. `rewardsOwed` is a u64
/// token amount returned as an unsigned `long`, already adjusted for any
/// Token-2022 transfer fee passed to the quote. Mirrors Rust
/// `CollectRewardQuote`.
public record CollectRewardQuote(long rewardsOwed) {
}
