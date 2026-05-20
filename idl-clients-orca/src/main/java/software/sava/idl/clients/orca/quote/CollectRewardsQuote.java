package software.sava.idl.clients.orca.quote;

/// Quote for `collectReward`/`collectRewardV2`. The `rewards` array always has
/// length `3`, matching Whirlpool's `NUM_REWARDS`. Mirrors Rust
/// `CollectRewardsQuote`.
public record CollectRewardsQuote(CollectRewardQuote[] rewards) {

  public static final int NUM_REWARDS = 3;
}
