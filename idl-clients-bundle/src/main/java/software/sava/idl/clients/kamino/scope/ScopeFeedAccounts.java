package software.sava.idl.clients.kamino.scope;

import software.sava.core.accounts.PublicKey;

import java.util.List;

public record ScopeFeedAccounts(PublicKey oraclePrices,
                                PublicKey oracleMappings,
                                PublicKey configuration) {

  // https://github.com/Kamino-Finance/scope-sdk/blob/36b105b997bab9da6af5596507ff617d3932bee7/src/constants/index.ts#L27
  public static final ScopeFeedAccounts SCOPE_MAINNET_HUBBLE_FEED = createAccounts(
      "3NJYftD5sjVfxSnUdZ1wVML8f3aC6mp1CXCL6L7TnU8C",
      "Chpu5ZgfWX5ZzVpUx9Xvv4WPM75Xd7zPJNDPsFnCpLpk",
      "AdTiP7QyjUyv6crF4H8z7fxJKU7Z5eCAGvJN1Y55cXxb"
  );

  public static final ScopeFeedAccounts SCOPE_MAINNET_KLEND_FEED = createAccounts(
      "3t4JZcueEzTbVP6kLxXrL3VpWx45jDer4eqysweBchNH",
      "4zh6bmb77qX2CL7t5AJYCqa6YqFafbz3QJNeFvZjLowg",
      "6cMwdbrJ95D7v5655Zsoe7oXmjQJMnagWK8EcdG6qmGM"
  );

  /// A third live feed, which the SDK's constants do not list. Five klend reserves name it
  /// as their scope price feed, so a lookup keyed only on the `hubble` and `klend` feeds
  /// misses them.
  public static final ScopeFeedAccounts SCOPE_MAINNET_THIRD_FEED = createAccounts(
      "82tcZDwUSGnmekst8cU4TCgQ4KLFGdtgbVkekHeibN7V",
      "D66o6ybaEuMpBrHRRyQw7Xf3qmmHm6niJpjKaztQZBDF",
      "575gnsnEyHBitEqjnWcXPFsJFiJ5dH5i9twxqpgCfeJF"
  );

  /// A fourth live feed. No klend reserve refers to it today; it is here so
  /// [#MAINNET_FEEDS] is the complete set rather than the subset klend happens to use.
  public static final ScopeFeedAccounts SCOPE_MAINNET_FOURTH_FEED = createAccounts(
      "AMjqm5S4QaAHWLv52jJiRpFNW1qo23F6ZM5ChCF5tYgc",
      "CX3u7L7HbBknwDdkHmzkKiMex6oF35s5NjGNZFUFCxpH",
      "6YnHNqmhsSJATKyX3aFMcXHeVmWasUkpYmEpF3Pyv3WM"
  );

  /// Every scope feed that exists on mainnet: the program owns exactly four
  /// `Configuration` accounts, and these are them.
  ///
  /// The feed accounts cannot be derived from the price feed a reserve stores — the
  /// `Configuration` PDA seeds on the feed's *name* (`["conf", name]`, see
  /// `KaminoAccounts.scopeFeedConfiguration`), which the reserve does not carry — so the
  /// mapping is enumerated instead. The first two are the SDK's `hubble` and `klend`
  /// feeds; the other two were read off chain, and their names are not published.
  public static final List<ScopeFeedAccounts> MAINNET_FEEDS = List.of(
      SCOPE_MAINNET_HUBBLE_FEED,
      SCOPE_MAINNET_KLEND_FEED,
      SCOPE_MAINNET_THIRD_FEED,
      SCOPE_MAINNET_FOURTH_FEED
  );

  public static ScopeFeedAccounts createAccounts(final String oraclePrices,
                                                 final String oracleMappings,
                                                 final String configuration) {
    return new ScopeFeedAccounts(
        PublicKey.fromBase58Encoded(oraclePrices),
        PublicKey.fromBase58Encoded(oracleMappings),
        PublicKey.fromBase58Encoded(configuration)
    );
  }
}
