package software.sava.idl.clients.phoenix;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.idl.clients.phoenix.perpetuals.gen.EternalProgram;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.TickRegionParams;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateSplineParametersParamsWithOrdering;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateSplinePriceParamsWithOrdering;
import software.sava.idl.clients.phoenix.perpetuals.gen.types.UpdateSplinePriceWithMetadataParams;

import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// The IDL gives the three spline updates an optional sixth account, the market's orderbook,
/// and the generated builders always pass six: left null, the program itself stands in its
/// place. Every update found on chain passes five, so the six-account shape rests on
/// simulation. On mainnet on 2026-11-14, slots 469337059 to 469343285, each of the three, built
/// by these builders for a live spline, succeeded with five accounts, with the program in the
/// sixth place and with the market's orderbook there, and `UpdateSplinePrice` with an
/// unrelated sysvar there as well. With either in the sixth place the two price updates left
/// the splines account holding the price sent, and the parameters update logged its success.
/// What was simulated is this shape, so a generator that builds another has not been tried.
final class SplineUpdateAccountsTests {

  private static final PublicKey ETERNAL_PROGRAM = PhoenixAccounts.MAIN_NET.invokedEternalProgram().publicKey();
  private static final PublicKey LOG_AUTHORITY = PhoenixAccounts.MAIN_NET.eternalLogAuthority();

  @Test
  void splineUpdatesPassTheProgramWhereTheOrderbookIsLeftOut() {
    final var invoked = PhoenixAccounts.MAIN_NET.invokedEternalProgram();
    final var signer = PublicKey.fromBase58Encoded("sp1nLqqjVZ1kTusFjJmHp1xHDpH6M4F7KXCr1iJ3xpX");
    final var trader = PublicKey.fromBase58Encoded("3t7LRKnubdcJDvPrpXMqVrrpsx6AYGNHGcCR3TPYDhCU");
    final var splines = PublicKey.fromBase58Encoded("Cw1SXqYHP2At3EE1xgSTi4xa6Xu3WrVrCqZAYeHedppS");
    final var orderbook = PublicKey.fromBase58Encoded("HCPybc8PFzS3gL2AHWXtT7jT1WDYSva7A6qf9aVGiLMf");

    final var price = new UpdateSplinePriceParamsWithOrdering(
        8_643L, OptionalLong.empty(), true, 1L << 62, new byte[16], false);
    final var metadata = new UpdateSplinePriceWithMetadataParams(8_643, 24, 1L << 62, 0L, 2_048, 6_343, new byte[16]);
    final var parameters = new UpdateSplineParametersParamsWithOrdering(
        new TickRegionParams[0], new TickRegionParams[0], true, 1L << 62, new byte[16], false);

    for (final var sixth : new PublicKey[]{null, orderbook}) {
      final var updates = List.of(
          EternalProgram.updateSplinePrice(
              invoked, ETERNAL_PROGRAM, LOG_AUTHORITY, signer, trader, splines, sixth, price),
          EternalProgram.updateSplinePriceWithMetadata(
              invoked, ETERNAL_PROGRAM, LOG_AUTHORITY, signer, trader, splines, sixth, metadata),
          EternalProgram.updateSplineParameters(
              invoked, ETERNAL_PROGRAM, LOG_AUTHORITY, signer, trader, splines, sixth, parameters));
      for (final var update : updates) {
        assertEquals(
            List.of(
                AccountMeta.createRead(ETERNAL_PROGRAM),
                AccountMeta.createRead(PublicKey.fromBase58Encoded("GdxfTLSsdSY37G6fZoYtdGDSfgFnbT2EmRpuePZxWShS")),
                AccountMeta.createReadOnlySigner(signer),
                AccountMeta.createRead(trader),
                AccountMeta.createWrite(splines),
                AccountMeta.createRead(sixth == null ? ETERNAL_PROGRAM : orderbook)
            ),
            update.accounts());
      }
    }
  }
}
