package software.sava.idl.clients.jupiter.swap;

import software.sava.idl.clients.jupiter.swap.gen.JupiterProgram;
import software.sava.idl.clients.jupiter.swap.gen.types.RoutePlanStepV2;

import java.util.Arrays;

import static software.sava.idl.clients.jupiter.swap.gen.JupiterProgram.ROUTE_V_2_DISCRIMINATOR;
import static software.sava.idl.clients.jupiter.swap.gen.JupiterProgram.SHARED_ACCOUNTS_ROUTE_V_2_DISCRIMINATOR;

/// Jazzer entry point exercising the Jupiter v2 route instruction parsers. The first
/// fuzzer byte selects one of the two supported discriminators, which the harness
/// writes itself so coverage is not gated behind an 8-byte magic value; the rest of the
/// input is the instruction payload.
///
/// Any payload either parses — in which case RouteV2Data must agree with the generated
/// ix record, and write/read must reach a byte-identical fixed point — or is rejected
/// with a runtime exception: readLen for length prefixes the buffer cannot back,
/// IndexOutOfBounds for truncation, and NPE for unknown Swap ordinals (read returns
/// null).
///
/// Deliberately has no Jazzer imports so it compiles with the regular test sources;
/// the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-bundle:fuzzRouteV2 [-PmaxFuzzTime=<seconds>]`.
public final class RouteV2DataFuzz {

  public static void fuzzerTestOneInput(final byte[] data) {
    if (data.length == 0) {
      return;
    }
    final boolean shared = (data[0] & 1) == 1;
    final var discriminator = shared ? SHARED_ACCOUNTS_ROUTE_V_2_DISCRIMINATOR : ROUTE_V_2_DISCRIMINATOR;
    final byte[] ix = new byte[discriminator.length() + data.length - 1];
    discriminator.write(ix, 0);
    System.arraycopy(data, 1, ix, discriminator.length(), data.length - 1);

    final RouteV2Data route;
    try {
      route = RouteV2Data.readData(ix, 0);
    } catch (final IndexOutOfBoundsException | NullPointerException rejected) {
      // truncated payload, a length prefix the buffer cannot back (readLen), or an
      // unknown Swap ordinal (read returns null) — rejection is the documented behavior
      return;
    }

    // the convenience record must agree with the generated ix record it wraps
    final long inAmount, quotedOutAmount;
    final int slippageBps, platformFeeBps, positiveSlippageBps, routePlanLength;
    final byte[] canonical;
    if (shared) {
      final var ixData = JupiterProgram.SharedAccountsRouteV2IxData.read(ix, 0);
      inAmount = ixData.inAmount();
      quotedOutAmount = ixData.quotedOutAmount();
      slippageBps = ixData.slippageBps();
      platformFeeBps = ixData.platformFeeBps();
      positiveSlippageBps = ixData.positiveSlippageBps();
      routePlanLength = ixData.routePlan().length;
      canonical = writeChecked(ixData.l(), ixData::write);
      final var reRead = JupiterProgram.SharedAccountsRouteV2IxData.read(canonical, 0);
      expectSameRoutePlan(ixData.routePlan(), reRead.routePlan());
      expectFixedPoint(canonical, writeChecked(reRead.l(), reRead::write));
    } else {
      final var ixData = JupiterProgram.RouteV2IxData.read(ix, 0);
      inAmount = ixData.inAmount();
      quotedOutAmount = ixData.quotedOutAmount();
      slippageBps = ixData.slippageBps();
      platformFeeBps = ixData.platformFeeBps();
      positiveSlippageBps = ixData.positiveSlippageBps();
      routePlanLength = ixData.routePlan().length;
      canonical = writeChecked(ixData.l(), ixData::write);
      final var reRead = JupiterProgram.RouteV2IxData.read(canonical, 0);
      expectSameRoutePlan(ixData.routePlan(), reRead.routePlan());
      expectFixedPoint(canonical, writeChecked(reRead.l(), reRead::write));
    }
    if (route.inAmount() != inAmount
        || route.quotedOutAmount() != quotedOutAmount
        || route.slippageBps() != slippageBps
        || route.platformFeeBps() != platformFeeBps
        || route.positiveSlippageBps() != positiveSlippageBps
        || route.routePlan().length != routePlanLength) {
      throw new IllegalStateException("RouteV2Data disagrees with the generated ix record for: " + route);
    }
  }

  private interface Writer {
    int write(byte[] data, int offset);
  }

  private static byte[] writeChecked(final int length, final Writer writer) {
    final byte[] out = new byte[length];
    final int wrote = writer.write(out, 0);
    if (wrote != length) {
      throw new IllegalStateException("write returned " + wrote + " but l() is " + length);
    }
    return out;
  }

  /// Catches value corruption that stays size-aligned — e.g. a variant whose write
  /// drops the ordinal deserializes as variant 0 without shifting any offsets.
  private static void expectSameRoutePlan(final RoutePlanStepV2[] original, final RoutePlanStepV2[] reRead) {
    if (original.length != reRead.length) {
      throw new IllegalStateException("route plan length did not round trip: "
          + original.length + " -> " + reRead.length);
    }
    for (int i = 0; i < original.length; ++i) {
      final var o = original[i].swap();
      final var r = reRead[i].swap();
      if (o.ordinal() != r.ordinal() || o.l() != r.l()) {
        throw new IllegalStateException(String.format(
            "route plan step %d did not round trip: %s -> %s", i, o, r
        ));
      }
    }
  }

  private static void expectFixedPoint(final byte[] first, final byte[] second) {
    if (!Arrays.equals(first, second)) {
      throw new IllegalStateException("write/read did not reach a byte-identical fixed point");
    }
  }

  private RouteV2DataFuzz() {
  }
}
