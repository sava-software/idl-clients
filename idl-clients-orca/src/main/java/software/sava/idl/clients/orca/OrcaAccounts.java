package software.sava.idl.clients.orca;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

public interface OrcaAccounts {

  OrcaAccounts MAIN_NET = createAccounts(
      "whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc",
      "3axbTs2z5GBy6usVbNVoqEgZMng3vZvMnAoX29BFfwhr",
      "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"
  );

  static OrcaAccounts createAccounts(final PublicKey whirlpoolProgram,
                                     final PublicKey whirlpoolNftUpdateAuthority,
                                     final PublicKey tokenMetadataProgram) {
    return new OrcaAccountsRecord(
        AccountMeta.createInvoked(whirlpoolProgram),
        whirlpoolNftUpdateAuthority,
        tokenMetadataProgram
    );
  }

  static OrcaAccounts createAccounts(final String whirlpoolProgram,
                                     final String whirlpoolNftUpdateAuthority,
                                     final String tokenMetadataProgram) {
    return createAccounts(
        PublicKey.fromBase58Encoded(whirlpoolProgram),
        PublicKey.fromBase58Encoded(whirlpoolNftUpdateAuthority),
        PublicKey.fromBase58Encoded(tokenMetadataProgram)
    );
  }

  AccountMeta invokedWhirlpoolProgram();

  PublicKey whirlpoolNftUpdateAuthority();

  PublicKey tokenMetadataProgram();
}
