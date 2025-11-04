package software.sava.idl.clients.drift;

import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.drift.gen.types.OracleSource;

public sealed interface MarketConfig permits SpotMarketConfig, PerpMarketConfig {

  String symbol();

  int marketIndex();

  OracleSource oracleSource();

  AccountMeta readOracle();

  AccountMeta writeOracle();

  AccountMeta oracle(final boolean write);

  AccountMeta readMarketPDA();

  AccountMeta writeMarketPDA();
}
