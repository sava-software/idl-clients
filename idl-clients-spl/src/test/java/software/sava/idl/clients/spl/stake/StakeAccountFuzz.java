package software.sava.idl.clients.spl.stake;

/// Jazzer entry point for the hand-written stake-account parser. Stake
/// accounts arrive as RPC account data, and the parse decides which keys hold
/// stake and withdraw authority — the money fields.
///
/// Any input either parses or is rejected with a `RuntimeException` (an
/// unknown state discriminant or a truncated buffer both surface as
/// index-out-of-bounds); any other throwable is a finding. On a successful
/// parse the harness asserts:
///
/// - determinism: re-reading the same bytes yields an equal record (every
///   component is a value type, so record equality is exact);
/// - the offset overload agrees with the zero-offset read when the same bytes
///   are shifted;
/// - the derived state machine is total: `state(epoch)` returns for boundary
///   epochs rather than throwing on any parsed account.
///
/// Deliberately has no Jazzer imports so it compiles with the regular test
/// sources; the raw `byte[]` signature is all the driver needs.
///
/// Run with `./gradlew :idl-clients-spl:fuzzStakeAccount [-PmaxFuzzTime=<seconds>]`.
public final class StakeAccountFuzz {

  public static void fuzzerTestOneInput(final byte[] data) {
    final StakeAccount account;
    try {
      account = StakeAccount.read(null, data);
    } catch (final RuntimeException rejected) {
      // garbage in -> RuntimeException out is the documented contract
      return;
    }
    if (account == null) {
      return;
    }

    final var reRead = StakeAccount.read(null, data);
    if (!account.equals(reRead)) {
      throw new IllegalStateException("StakeAccount.read is not deterministic");
    }

    // the offset overload must land on the same record when the buffer is shifted
    final byte[] shifted = new byte[data.length + 7];
    System.arraycopy(data, 0, shifted, 7, data.length);
    final var offsetRead = StakeAccount.read(null, shifted, 7);
    if (!account.equals(offsetRead)) {
      throw new IllegalStateException("offset read disagrees with the zero-offset read");
    }

    // the derived state machine is total over epochs
    for (final long epoch : new long[]{
        0L, 1L,
        account.activationEpoch(), account.activationEpoch() + 1,
        account.deActivationEpoch(), account.deActivationEpoch() + 1,
        Long.MAX_VALUE}) {
      if (account.state(epoch) == null) {
        throw new IllegalStateException("state(" + epoch + ") returned null");
      }
    }
  }

  private StakeAccountFuzz() {
  }
}
