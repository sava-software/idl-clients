package software.sava.idl.clients.phoenix;

import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDeUtil;

import java.util.Arrays;
import java.util.OptionalLong;

import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.idl.clients.phoenix.perpetuals.gen.EternalProgram.UPDATE_SPLINE_PRICE_DISCRIMINATOR;

/// The arguments of an Eternal `UpdateSplinePrice` instruction, in either form the program takes
/// under that one discriminator.
///
/// The IDL declares one form, the price with its ordering fields, which the generated
/// `EternalProgram.updateSplinePrice` builds and `EternalProgram.UpdateSplinePriceIxData` reads.
/// The program also takes the first three fields alone, the `UpdateSplinePriceParams` that
/// Phoenix's SDK still builds (`rise/rust/ix/src/spline.rs::create_update_spline_price_ix`), and
/// both forms have executed on mainnet. The generated reader expects the ordering fields and
/// throws `ArrayIndexOutOfBoundsException` on the short form; this reads either.
///
/// An update without ordering is applied without the program checking or advancing the spline's
/// stored price sequence number. Either form is refused when its update slot is older than the
/// one the program stored.
///
/// The program tells the forms apart by length, and so does this. Up to 26 bytes with the
/// discriminator is the short form: its fields are read from the front and whatever follows
/// them is ignored, which without an update slot can be as many as eight bytes. Anything longer
/// is the ordered form, at exactly 43 bytes without an update slot and 51 with one. Simulated on
/// mainnet on 2026-11-14, the program took those lengths and no other of the ones tried.
///
/// @param userUpdateSlot the caller's update slot; when absent the program uses the current slot
/// @param ordering       the ordering fields, or null when the instruction carries the short form
public record SplinePriceUpdate(long newMidPrice,
                                OptionalLong userUpdateSlot,
                                boolean refreshRegions,
                                Ordering ordering) {

  /// The ordering fields, the last three of the generated `UpdateSplinePriceParamsWithOrdering`.
  public record Ordering(long userSequenceNumber,
                         byte[] clientOrderId,
                         boolean overrideSequenceNumber) {

    public static final int CLIENT_ORDER_ID_LEN = 16;
  }

  private static final int DISCRIMINATOR_LENGTH = 8;
  /// The price, the update slot's presence tag and the refresh flag.
  private static final int MIN_ARGS_LENGTH = 8 + 1 + 1;
  /// The short form at its longest, with an update slot.
  private static final int MAX_SHORT_ARGS_LENGTH = MIN_ARGS_LENGTH + 8;
  private static final int ORDERING_LENGTH = 8 + Ordering.CLIENT_ORDER_ID_LEN + 1;

  /// @throws IllegalArgumentException if the instruction is not an `UpdateSplinePrice` of one of
  ///                                  the lengths the program takes
  public static SplinePriceUpdate read(final Instruction instruction) {
    return read(instruction.data(), instruction.offset(), instruction.len());
  }

  /// @param data   holds the instruction's data, discriminator first
  /// @param offset where the instruction's data begins
  /// @param length how many bytes of it there are
  /// @throws IllegalArgumentException if the bytes are not an `UpdateSplinePrice` of one of the
  ///                                  lengths the program takes
  public static SplinePriceUpdate read(final byte[] data, final int offset, final int length) {
    if (!UPDATE_SPLINE_PRICE_DISCRIMINATOR.equals(data, offset, length)) {
      throw new IllegalArgumentException("Not an UpdateSplinePrice instruction.");
    }
    final int argsLength = length - DISCRIMINATOR_LENGTH;
    if (argsLength < MIN_ARGS_LENGTH) {
      throw new IllegalArgumentException(String.format(
          "UpdateSplinePrice is at least 18 bytes, not %d.", length
      ));
    }
    int i = offset + DISCRIMINATOR_LENGTH;
    final long newMidPrice = getInt64LE(data, i);
    i += 8;
    final boolean noUpdateSlot = SerDeUtil.isAbsent(1, data, i);
    ++i;
    final int sharedLength = noUpdateSlot ? MIN_ARGS_LENGTH : MAX_SHORT_ARGS_LENGTH;
    final boolean ordered = argsLength > MAX_SHORT_ARGS_LENGTH;
    if (ordered ? argsLength != sharedLength + ORDERING_LENGTH : argsLength < sharedLength) {
      throw new IllegalArgumentException(String.format(
          noUpdateSlot
              ? "UpdateSplinePrice without an update slot is 18 to 26 bytes, or 43 with ordering, not %d."
              : "UpdateSplinePrice with an update slot is 26 bytes, or 51 with ordering, not %d.",
          length
      ));
    }
    final OptionalLong userUpdateSlot;
    if (noUpdateSlot) {
      userUpdateSlot = OptionalLong.empty();
    } else {
      userUpdateSlot = OptionalLong.of(getInt64LE(data, i));
      i += 8;
    }
    final boolean refreshRegions = data[i] == 1;
    ++i;
    if (!ordered) {
      return new SplinePriceUpdate(newMidPrice, userUpdateSlot, refreshRegions, null);
    }
    final long userSequenceNumber = getInt64LE(data, i);
    i += 8;
    final byte[] clientOrderId = Arrays.copyOfRange(data, i, i + Ordering.CLIENT_ORDER_ID_LEN);
    i += Ordering.CLIENT_ORDER_ID_LEN;
    final boolean overrideSequenceNumber = data[i] == 1;
    return new SplinePriceUpdate(
        newMidPrice,
        userUpdateSlot,
        refreshRegions,
        new Ordering(userSequenceNumber, clientOrderId, overrideSequenceNumber)
    );
  }
}
