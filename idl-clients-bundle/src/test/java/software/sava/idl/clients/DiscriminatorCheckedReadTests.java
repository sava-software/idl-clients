package software.sava.idl.clients;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.exponent.gen.types.MarketTwo;
import software.sava.idl.clients.exponent.gen.types.Vault;
import software.sava.idl.clients.jupiter.offerbook.gen.types.BaseAssetV1;
import software.sava.rpc.json.http.response.AccountInfo;
import software.sava.rpc.json.http.response.Context;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/// The discriminator check the generator emits, exercised against the bytes it exists to reject.
///
/// `read` applies a record's layout to whatever it is handed. It parses the leading discriminator
/// into a field rather than comparing it, so a sibling account of the same program decodes into a
/// fully populated record carrying the *other* account's discriminator — and `write` puts that back,
/// so the wrong answer round trips cleanly. `readChecked` compares first, and the two entry points
/// that fetch and then decode without having been told what they are holding — `read(AccountInfo)`
/// and the `FACTORY` a `getProgramAccounts` scan is handed — now go through it.
///
/// What is under test is the shape the generator emits for every account carrying a `DISCRIMINATOR`
/// constant, not one program's layout, so the cases below take one representative per declared
/// width: Exponent's accounts for the customary eight bytes, and Metaplex Core's `BaseAssetV1` for a
/// declared width of one, where comparing more than the first byte would reject valid accounts.
/// Both fixtures are real mainnet accounts, already committed for the layout tests beside them.
final class DiscriminatorCheckedReadTests {

  private static final String VAULT = "14fXk2YSt9KbJgTttGRYwJ3uXB7ZRfjUbHfPYEdWJyKb";
  private static final String MARKET = "12Hva9LLLmGXn6PvtareyEmDMP83ZuLaHpXHrcux2LUF";
  private static final String ASSET = "1Lz3bkYMDk5b17LZ3T5oJLEzFfSBjBYMp7hqfWmN1cQ";

  private static final PublicKey VAULT_KEY = PublicKey.fromBase58Encoded(VAULT);
  private static final PublicKey MARKET_KEY = PublicKey.fromBase58Encoded(MARKET);
  private static final PublicKey ASSET_KEY = PublicKey.fromBase58Encoded(ASSET);

  private static byte[] fixture(final String path) {
    try (var in = DiscriminatorCheckedReadTests.class.getResourceAsStream(path)) {
      assertNotNull(in, "fixture " + path + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static byte[] vaultData() {
    return fixture("/exponent/vault-" + VAULT + ".base64");
  }

  private static byte[] marketData() {
    return fixture("/exponent/marketTwo-" + MARKET + ".base64");
  }

  private static byte[] assetData() {
    return fixture("/jupiter/baseAssetV1-" + ASSET + ".base64");
  }

  private static byte[] withFlippedByte(final byte[] data, final int index) {
    final byte[] copy = data.clone();
    copy[index] ^= (byte) 0xff;
    return copy;
  }

  private static AccountInfo<byte[]> accountInfo(final PublicKey address, final byte[] data) {
    return new AccountInfo<>(address, new Context(0L, null), false, 0L, PublicKey.NONE, BigInteger.ZERO, 0, data);
  }

  /// The hole, stated as an assertion. Handing `read` a real Vault whose leading eight bytes claim
  /// to be a MarketTwo isolates the discriminator from every other cause of failure: the rest of the
  /// account is genuine, so the decode cannot fail for a layout reason, and what comes back is a
  /// fully populated `Vault` carrying MarketTwo's discriminator. `write` then puts that back, so the
  /// wrong answer round trips and nothing downstream can tell.
  ///
  /// Stating it this way rather than by decoding one account as another is deliberate: whether a
  /// foreign account decodes *quietly* depends on its layout. Every Exponent account here reaches a
  /// `Vec` length prefix eventually and dies loudly on garbage. A fixed-layout account has no such
  /// backstop, and the discriminator is the only thing that ever separated the two cases.
  @Test
  void readStoresWhateverDiscriminatorItFindsAndReadCheckedRefusesIt() {
    final byte[] data = vaultData();
    final byte[] mislabelled = data.clone();
    MarketTwo.DISCRIMINATOR.write(mislabelled, 0);

    final var decoded = Vault.read(VAULT_KEY, mislabelled, 0);
    assertNotNull(decoded, "read never compares the discriminator");
    assertEquals(MarketTwo.DISCRIMINATOR, decoded.discriminator(), "it stores whatever it found");
    assertNotEquals(Vault.DISCRIMINATOR, decoded.discriminator());

    final byte[] out = new byte[decoded.l()];
    decoded.write(out, 0);
    assertArrayEquals(
        MarketTwo.DISCRIMINATOR.data(), Arrays.copyOf(out, MarketTwo.DISCRIMINATOR.length()),
        "write puts the foreign discriminator back, so the wrong answer round trips"
    );

    final var refused = assertThrows(
        IllegalArgumentException.class,
        () -> Vault.readChecked(VAULT_KEY, mislabelled, 0)
    );
    assertTrue(refused.getMessage().contains("Vault"), refused::getMessage);

    // and the converse, so this is a check and not a blanket refusal
    assertNotNull(Vault.readChecked(VAULT_KEY, data, 0));
  }

  /// Every fixture passes its own check and fails its neighbour's, across both declared widths.
  @Test
  void realAccountsPassTheirOwnCheckAndOnlyTheirOwn() {
    assertNotNull(Vault.readChecked(VAULT_KEY, vaultData()));
    assertNotNull(MarketTwo.readChecked(MARKET_KEY, marketData()));
    assertNotNull(BaseAssetV1.readChecked(ASSET_KEY, assetData()));

    assertThrows(IllegalArgumentException.class, () -> Vault.readChecked(VAULT_KEY, marketData()));
    assertThrows(IllegalArgumentException.class, () -> BaseAssetV1.readChecked(ASSET_KEY, vaultData()));
  }

  /// `read(AccountInfo)` and `FACTORY` are the two entry points the change rerouted — they receive
  /// whatever an RPC returned, so they are the ones that were never told what they are holding.
  @Test
  void theFetchThenDecodeEntryPointsAreChecked() {
    final byte[] data = vaultData();
    final byte[] mislabelled = data.clone();
    MarketTwo.DISCRIMINATOR.write(mislabelled, 0);

    assertNotNull(Vault.read(accountInfo(VAULT_KEY, data)));
    assertNotNull(Vault.FACTORY.apply(VAULT_KEY, data));

    assertThrows(
        IllegalArgumentException.class,
        () -> Vault.read(accountInfo(VAULT_KEY, mislabelled)),
        "read(AccountInfo) must not decode bytes that are not this account"
    );
    assertThrows(
        IllegalArgumentException.class,
        () -> Vault.FACTORY.apply(VAULT_KEY, mislabelled),
        "the FACTORY a scan is handed must not decode bytes that are not this account"
    );

    // the unchecked overloads are deliberately untouched, for a caller that has already dispatched
    assertNotNull(Vault.read(VAULT_KEY, mislabelled, 0));
    assertNotNull(Vault.read(VAULT_KEY, mislabelled));
  }

  /// The asymmetry is deliberate and worth pinning: absent data reads as null, wrong data throws.
  /// A closed account is the case that separates them — zero-length once reaped, but full-length
  /// zeroes until then, and full-length zeroes are bytes that are not this account.
  @Test
  void absentDataReadsAsNullButAZeroedAccountThrows() {
    assertNull(Vault.readChecked(VAULT_KEY, null));
    assertNull(Vault.readChecked(VAULT_KEY, new byte[0]));
    assertNull(Vault.read(accountInfo(VAULT_KEY, new byte[0])));

    final int size = vaultData().length;
    final var zeroed = assertThrows(
        IllegalArgumentException.class,
        () -> Vault.readChecked(VAULT_KEY, new byte[size]),
        "a closed-but-unreaped account is not a Vault"
    );
    assertTrue(zeroed.getMessage().contains("Vault"), zeroed::getMessage);
  }

  /// Data too short to hold the discriminator is a mismatch, not an overrun. `read` indexes past
  /// the end of such a buffer; the check reaches its own refusal first.
  @Test
  void dataShorterThanTheDiscriminatorIsRefusedNotIndexedPast() {
    final byte[] truncated = new byte[Vault.DISCRIMINATOR.length() - 1];
    System.arraycopy(vaultData(), 0, truncated, 0, truncated.length);

    assertThrows(IllegalArgumentException.class, () -> Vault.readChecked(VAULT_KEY, truncated));
    assertThrows(IndexOutOfBoundsException.class, () -> Vault.read(VAULT_KEY, truncated, 0));
  }

  /// Only the declared width is compared. For `BaseAssetV1` that is one byte — the `key` field and
  /// the discriminator are the same byte, and comparing a second would reject every account whose
  /// owner happens to differ. For an eight-byte account the boundary sits between index 7 and 8.
  @Test
  void onlyTheDeclaredWidthIsCompared() {
    assertEquals(1, BaseAssetV1.DISCRIMINATOR.length());
    final byte[] assetData = assetData();
    assertThrows(
        IllegalArgumentException.class,
        () -> BaseAssetV1.readChecked(ASSET_KEY, withFlippedByte(assetData, 0))
    );
    assertNotNull(
        BaseAssetV1.readChecked(ASSET_KEY, withFlippedByte(assetData, 1)),
        "byte 1 is the owner, past the one byte this account declares"
    );

    assertEquals(8, Vault.DISCRIMINATOR.length());
    final byte[] vaultData = vaultData();
    assertThrows(
        IllegalArgumentException.class,
        () -> Vault.readChecked(VAULT_KEY, withFlippedByte(vaultData, 7)),
        "index 7 is the last discriminator byte"
    );
    assertNotNull(
        Vault.readChecked(VAULT_KEY, withFlippedByte(vaultData, 8)),
        "index 8 is the first field"
    );
  }

  /// The check reads from the offset it was given, not from zero.
  @Test
  void theCheckHonoursTheOffset() {
    final byte[] vaultData = vaultData();
    final int offset = 9;
    final byte[] embedded = new byte[offset + vaultData.length];
    System.arraycopy(vaultData, 0, embedded, offset, vaultData.length);

    assertNotNull(Vault.readChecked(VAULT_KEY, embedded, offset));
    assertThrows(IllegalArgumentException.class, () -> Vault.readChecked(VAULT_KEY, embedded, 0));
  }
}
