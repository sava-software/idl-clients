package software.sava.idl.clients.jupiter.offerbook;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.jupiter.offerbook.gen.types.Asset;
import software.sava.idl.clients.jupiter.offerbook.gen.types.AssetFilter;
import software.sava.idl.clients.jupiter.offerbook.gen.types.Loan;
import software.sava.idl.clients.jupiter.offerbook.gen.types.Offer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

/// `Offer` and `Loan` are zero-copy accounts, so each `Asset` and `AssetFilter` field occupies its
/// whole 265 bytes, a one-byte tag and the widest variant's 264-byte payload, whichever variant is
/// set. The reader used to advance by the variant present: an offer whose collateral is
/// `Asset::None` had its filter's tag read from the collateral's unused payload, and `Offer.read`
/// threw for every live offer of that shape.
///
/// The fixtures are live accounts, captured at slots 456589341 to 456589343. The sizes they are
/// held to are the program's: at slot 456514252 every one of its 2,861 offers was 1224 bytes and
/// every one of its 5,230 loans 808, which only full-width slots add up to.
final class ZeroCopyAccountTests {

  /// Collateral `None`, its unused payload still holding a previous variant's bytes; created
  /// 2026-05-27.
  private static final String NONE_COLLATERAL = "46YTVrpVsmosXoWe8tia5bz6XVqrrAmDYuRrTGdEc6rN";
  /// Token principal, token collateral, no filter: every slot's payload is the full 264 bytes.
  private static final String FULL_SLOTS = "16ftoxLGwKYwfdMhYbkwxRNzqwHaAhSFDsWdknyTraJ";
  /// Token principal, Core NFT collateral.
  private static final String LOAN = "59G7EsK1vQVvt7xCGpBn82bouxqCMzFnmdeE4GHr1os";

  private static byte[] fixture(final String name) {
    try (var in = ZeroCopyAccountTests.class.getResourceAsStream("/jupiter/" + name + ".base64")) {
      assertNotNull(in, "fixture " + name + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void theAccountsAreTheSizeTheProgramWrites() {
    assertEquals(1224, Offer.BYTES);
    assertEquals(808, Loan.BYTES);
    assertEquals(Offer.BYTES, fixture("offer-" + NONE_COLLATERAL).length);
    assertEquals(Offer.BYTES, fixture("offer-" + FULL_SLOTS).length);
    assertEquals(Loan.BYTES, fixture("loan-" + LOAN).length);
    assertEquals(Offer.BYTES, Offer.read(PublicKey.fromBase58Encoded(FULL_SLOTS), fixture("offer-" + FULL_SLOTS)).l());
  }

  @Test
  void aNoneCollateralLeavesEveryLaterFieldWhereTheProgramPutIt() {
    final byte[] data = fixture("offer-" + NONE_COLLATERAL);
    assertEquals(0, data[320], "the collateral's tag: None");
    assertNotEquals(0, data[328], "its unused payload is not blank");

    final var offer = Offer.read(PublicKey.fromBase58Encoded(NONE_COLLATERAL), data);
    assertNotNull(offer);
    assertInstanceOf(Asset.Token.class, offer.principal());
    assertInstanceOf(Asset.None.class, offer.collateral());
    final var filter = assertInstanceOf(AssetFilter.Collection.class, offer.filter());
    assertEquals(readPubKey(data, 593), filter.val().collection());
    assertEquals(Integer.toUnsignedLong(getInt32LE(data, 904)), offer.apy());
    assertEquals(getInt64LE(data, 912), offer.createdAt());
    assertEquals(1779910229L, offer.createdAt(), "2026-05-27");
    assertEquals(readPubKey(data, 960), offer.counteredOffer());
  }

  /// Every slot carries a full payload, so the account writes back to its own bytes.
  @Test
  void anOfferWithFullSlotsWritesBackToItsOwnBytes() {
    final byte[] data = fixture("offer-" + FULL_SLOTS);
    final var offer = Offer.read(PublicKey.fromBase58Encoded(FULL_SLOTS), data);
    assertInstanceOf(Asset.Token.class, offer.collateral());
    assertInstanceOf(AssetFilter.NoFilter.class, offer.filter());
    final byte[] written = new byte[Offer.BYTES];
    assertEquals(Offer.BYTES, offer.write(written, 0));
    assertArrayEquals(data, written);
  }

  /// A `None` slot keeps no payload, so its unused bytes are written as zeros, as a freshly
  /// initialized account holds them, and every other byte is the account's own.
  @Test
  void aNoneSlotIsWrittenWithItsUnusedPayloadZeroed() {
    final byte[] data = fixture("offer-" + NONE_COLLATERAL);
    final var offer = Offer.read(PublicKey.fromBase58Encoded(NONE_COLLATERAL), data);
    final byte[] written = new byte[Offer.BYTES];
    Arrays.fill(written, (byte) 0xFF);
    assertEquals(Offer.BYTES, offer.write(written, 0));

    final byte[] expected = data.clone();
    Arrays.fill(expected, 321, 585, (byte) 0);
    assertArrayEquals(expected, written);
  }

  @Test
  void aLoanDecodesAndWritesBackToItsOwnBytes() {
    final byte[] data = fixture("loan-" + LOAN);
    final var loan = Loan.read(PublicKey.fromBase58Encoded(LOAN), data);
    assertInstanceOf(Asset.Token.class, loan.principal());
    final var collateral = assertInstanceOf(Asset.CoreNft.class, loan.collateral());
    assertEquals(readPubKey(data, 425), collateral.val().asset());
    assertEquals(Integer.toUnsignedLong(getInt32LE(data, 696)), loan.apy());
    final byte[] written = new byte[Loan.BYTES];
    assertEquals(Loan.BYTES, loan.write(written, 0));
    assertArrayEquals(data, written);
  }
}
