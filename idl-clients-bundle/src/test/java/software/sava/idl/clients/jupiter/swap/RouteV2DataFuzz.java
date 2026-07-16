package software.sava.idl.clients.jupiter.swap;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.jupiter.swap.gen.JupiterProgram;

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
/// with a runtime exception. The generated vector reads trust in-band u32 lengths and
/// unknown Swap ordinals surface as null, so allocation failures and NPEs are the
/// rejection paths today.
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

    // the generated readVector trusts the in-band u32 count and allocates before any
    // bounds check, so a wild route-plan count burns hundreds of MB per execution just
    // to fail on the first element reads. Counts modestly beyond what the data can back
    // still exercise the truncation path; anything larger only throttles the fuzzer.
    final int routePlanOffset = shared
        ? JupiterProgram.SharedAccountsRouteV2IxData.ROUTE_PLAN_OFFSET
        : JupiterProgram.RouteV2IxData.ROUTE_PLAN_OFFSET;
    if (ix.length >= routePlanOffset + Integer.BYTES) {
      final int routePlanCount = ByteUtil.getInt32LE(ix, routePlanOffset);
      // the smallest RoutePlanStepV2 is a unit Swap variant: 1 + 2 + 1 + 1 bytes
      if (routePlanCount < 0 || routePlanCount > (ix.length / 5) + 2) {
        return;
      }
    }

    final RouteV2Data route;
    try {
      route = RouteV2Data.readData(ix, 0);
    } catch (final IndexOutOfBoundsException | NegativeArraySizeException | NullPointerException rejected) {
      // truncated payload, an in-band count the buffer cannot back, or an unknown Swap
      // ordinal (read returns null) — rejection is the documented behavior today
      return;
    } catch (final OutOfMemoryError oversizedVector) {
      // generated readVector allocates from an unvalidated u32 length; an impossible
      // count dies in the array allocation without consuming memory
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

  private static void expectFixedPoint(final byte[] first, final byte[] second) {
    if (!Arrays.equals(first, second)) {
      throw new IllegalStateException("write/read did not reach a byte-identical fixed point");
    }
  }

  private RouteV2DataFuzz() {
  }
}
