package software.sava.idl.clients.jupiter.perpetuals;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.perpetuals.gen.PerpetualsProgram;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.AddLiquidity2Params;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.BorrowFromCustodyParams;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.Custody;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.Pool;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.RefreshAssetsUnderManagementParams;
import software.sava.idl.clients.jupiter.perpetuals.gen.types.RemoveLiquidity2Params;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/// The tail every Jupiter Perpetuals instruction pricing the JLP pool reads: the Pool's
/// custodies in stored order, then each one's `dovesAgOracle`, all read-only.
///
/// The fixtures are the live JLP Pool `5BUwFW4n…`, read at slot 465257594, and its six
/// custodies, read at slot 465257856: each account's data verbatim, owned by `PERPHjGB…`,
/// checked here for size and discriminator before the generated readers decode it. The
/// expected account lists and instruction data are four live calls, copied from their
/// transactions. All four end in the same twelve accounts, so the helper's output is held to
/// the chain directly and again inside each call, rebuilt from the generated builder.
final class JupiterPerpetualsRemainingAccountsTests {

  private static final PublicKey PROGRAM =
      PublicKey.fromBase58Encoded("PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu");
  private static final AccountMeta INVOKED = AccountMeta.createInvoked(PROGRAM);

  private static final PublicKey PERPETUALS =
      PublicKey.fromBase58Encoded("H4ND9aYttUVLFmNypZqLjZ52FYiGvdEB45GmwNoKEjTj");
  private static final PublicKey POOL =
      PublicKey.fromBase58Encoded("5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq");
  private static final PublicKey JLP_MINT =
      PublicKey.fromBase58Encoded("27G8MtK7VtTcCHkpASjSDdkWWYfoqT6ggEuKidVJidD4");
  private static final PublicKey TRANSFER_AUTHORITY =
      PublicKey.fromBase58Encoded("AVzP2GeRmqGphJsMxWoqjpUifPpCret7LqWhD8NWQK49");
  private static final PublicKey EVENT_AUTHORITY =
      PublicKey.fromBase58Encoded("37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN");
  private static final PublicKey TOKEN_PROGRAM =
      PublicKey.fromBase58Encoded("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");

  /// The Pool account is allocated 2,000 bytes, more than its variable-length layout uses.
  private static final int POOL_SPACE = 2000;

  /// `Pool.custodies` as stored: SOL, ETH, WBTC, USDC, USDT and JupUSD.
  private static final List<PublicKey> CUSTODIES = keys("""
      7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz
      AQCGyheWPLeo6Qp9WpYS9m3Qj479t7R636N9ey1rEjEn
      5Pv3gM9JrFFH883SWAhvJC9RPYmo8UNxuFtv5bMMALkm
      G18jKKXQwBbrHeiK3C9MRXhkHsLHf7XgCSisykV46EZa
      4vkNeXiYEUizLdrpdPS1eC2mccyM4NUPRtERrk6ZETkk
      DdwY1ELc9rRK7xNL3hTXabSFBmVrTPpfsUZSv2Y3LL1U
      """);

  /// Each custody's mint, in the same order.
  private static final List<PublicKey> MINTS = keys("""
      So11111111111111111111111111111111111111112
      7vfCXTUXx5WJV5JADk17DUJ4ksgau7utNKj4b963voxs
      3NZ9JMVBmGAqocybic2c7LQCJScmgsAZ6vQqTDzcqmJh
      EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v
      Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB
      JuprjznTrTSp2UFa3ZBUFgwdAmtZCq4MQCwysN55USD
      """);

  /// Each custody account's size on chain: the first five are allocated 2,000 bytes, the JupUSD
  /// one 1,128; all of them hold at least the `Custody.BYTES` the generated layout reads.
  private static final int[] CUSTODY_SPACE = {2000, 2000, 2000, 2000, 2000, 1128};

  /// `refresh_assets_under_management` in `1qmUtLU2sxWZvqfifx4G7P4w1vzvLKxBX4C6PfRioBmxRkKMRSNtwLtfTqRoj6fcXA3bjWmfe6EzzA15NBaDdRN`,
  /// slot 465132375, top-level instruction 8 of a transaction from keeper `Ag28fGtw…`: the four
  /// IDL accounts, then the tail.
  private static final List<PublicKey> LIVE_REFRESH = keys("""
      Ag28fGtwtpnqassHURUBsQ1WfiyaWWzDDNs4Q28qHRjv
      H4ND9aYttUVLFmNypZqLjZ52FYiGvdEB45GmwNoKEjTj
      5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq
      27G8MtK7VtTcCHkpASjSDdkWWYfoqT6ggEuKidVJidD4
      7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz
      AQCGyheWPLeo6Qp9WpYS9m3Qj479t7R636N9ey1rEjEn
      5Pv3gM9JrFFH883SWAhvJC9RPYmo8UNxuFtv5bMMALkm
      G18jKKXQwBbrHeiK3C9MRXhkHsLHf7XgCSisykV46EZa
      4vkNeXiYEUizLdrpdPS1eC2mccyM4NUPRtERrk6ZETkk
      DdwY1ELc9rRK7xNL3hTXabSFBmVrTPpfsUZSv2Y3LL1U
      FYq2BWQ1V5P1WFBqr3qB2Kb5yHVvSv7upzKodgQE5zXh
      AFZnHPzy4mvVCffrVwhewHbFc93uTHvDSFrVH7GtfXF1
      hUqAT1KQ7eW1i6Csp9CXYtpPfSAvi835V7wKi5fRfmC
      6Jp2xZUTWdDD2ZyUPRzeMdc6AFQ5K3pFgZxk2EijfjnM
      Fgc93D641F8N2d1xLjQ4jmShuD3GE3BsCXA56KBQbF5u
      9DRrwc4hSMQouw3ptpHXtQXVNHoqwyqLaK6NWztWTDin
      """);
  private static final String LIVE_REFRESH_DATA = "a200d737e10fb900";
  private static final PublicKey REFRESH_KEEPER =
      PublicKey.fromBase58Encoded("Ag28fGtwtpnqassHURUBsQ1WfiyaWWzDDNs4Q28qHRjv");

  /// The twelve accounts after the refresh's four IDL accounts.
  private static final List<PublicKey> LIVE_TAIL = LIVE_REFRESH.subList(4, LIVE_REFRESH.size());

  /// `add_liquidity2` in `38qsrcKNpo8KKFxDHyKDXWBKNCqQeBGqDa4r3RX3LE6nm6fPQM7nSxmBefEsur4orXvJvR67W16whPJHMMNnybj9`,
  /// slot 465131910, inner instruction 0.1, a CPI from `JUP6LkbZ…` adding 340 units of WBTC: the
  /// fourteen IDL accounts, then the tail. The WBTC custody's `dovesAgOracle`, `hUqAT1KQ…`, fills
  /// both declared price slots and appears a third time in the tail.
  private static final List<PublicKey> LIVE_ADD = keys("""
      69yhtoJR4JYPPABZcSNkzuqbaFbwHsCkja1sP1Q2aVT5
      4TQcUWhtzjt4GPYunhD55jrBz4h1WkqB8DNtCQAVqLVt
      3YTsbrAAvbi4at77D3yCzRqKerqvdDRFX8nvqY1oe2kE
      AVzP2GeRmqGphJsMxWoqjpUifPpCret7LqWhD8NWQK49
      H4ND9aYttUVLFmNypZqLjZ52FYiGvdEB45GmwNoKEjTj
      5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq
      5Pv3gM9JrFFH883SWAhvJC9RPYmo8UNxuFtv5bMMALkm
      hUqAT1KQ7eW1i6Csp9CXYtpPfSAvi835V7wKi5fRfmC
      hUqAT1KQ7eW1i6Csp9CXYtpPfSAvi835V7wKi5fRfmC
      FgpXg2J3TzSs7w3WGYYE7aWePdrxBVLCXSxmAKnCZNtZ
      27G8MtK7VtTcCHkpASjSDdkWWYfoqT6ggEuKidVJidD4
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA
      37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN
      PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu
      7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz
      AQCGyheWPLeo6Qp9WpYS9m3Qj479t7R636N9ey1rEjEn
      5Pv3gM9JrFFH883SWAhvJC9RPYmo8UNxuFtv5bMMALkm
      G18jKKXQwBbrHeiK3C9MRXhkHsLHf7XgCSisykV46EZa
      4vkNeXiYEUizLdrpdPS1eC2mccyM4NUPRtERrk6ZETkk
      DdwY1ELc9rRK7xNL3hTXabSFBmVrTPpfsUZSv2Y3LL1U
      FYq2BWQ1V5P1WFBqr3qB2Kb5yHVvSv7upzKodgQE5zXh
      AFZnHPzy4mvVCffrVwhewHbFc93uTHvDSFrVH7GtfXF1
      hUqAT1KQ7eW1i6Csp9CXYtpPfSAvi835V7wKi5fRfmC
      6Jp2xZUTWdDD2ZyUPRzeMdc6AFQ5K3pFgZxk2EijfjnM
      Fgc93D641F8N2d1xLjQ4jmShuD3GE3BsCXA56KBQbF5u
      9DRrwc4hSMQouw3ptpHXtQXVNHoqwyqLaK6NWztWTDin
      """);
  private static final String LIVE_ADD_DATA = "e4a24e1c46db74735401000000000000000000000000000000";

  /// `remove_liquidity2` in `3nRNuJcrKLTQuVCAw7ypCkW2LxqG7JnG12AKxB5EsbSkME7rpfnckJPfVHqaCko8ixxB87TByHyXNh47my1ksaNU`,
  /// slot 465142781, inner instruction 0.3, a CPI from `NinafKYv…` in a successful transaction,
  /// burning 54,231,678 JLP units for JupUSD: the fourteen IDL accounts, then the tail.
  private static final List<PublicKey> LIVE_REMOVE = keys("""
      GxDC9e7SP9mzhDo4re5HbpLa2RW7gB9DtmThx4i4pXSq
      2jdL6KRa2sw7g9Zc5YJNeYSAQW16nSh6gRHLTAsovCz2
      APcBVekb2BUkNZ5WscezWoqUak6J9pQUaRV29hig1fcf
      AVzP2GeRmqGphJsMxWoqjpUifPpCret7LqWhD8NWQK49
      H4ND9aYttUVLFmNypZqLjZ52FYiGvdEB45GmwNoKEjTj
      5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq
      DdwY1ELc9rRK7xNL3hTXabSFBmVrTPpfsUZSv2Y3LL1U
      9DRrwc4hSMQouw3ptpHXtQXVNHoqwyqLaK6NWztWTDin
      9DRrwc4hSMQouw3ptpHXtQXVNHoqwyqLaK6NWztWTDin
      6p5mjdPVSYheaFV2bSxb3LvVBnuA6fNYKEXGXD7iTdsd
      27G8MtK7VtTcCHkpASjSDdkWWYfoqT6ggEuKidVJidD4
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA
      37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN
      PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu
      7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz
      AQCGyheWPLeo6Qp9WpYS9m3Qj479t7R636N9ey1rEjEn
      5Pv3gM9JrFFH883SWAhvJC9RPYmo8UNxuFtv5bMMALkm
      G18jKKXQwBbrHeiK3C9MRXhkHsLHf7XgCSisykV46EZa
      4vkNeXiYEUizLdrpdPS1eC2mccyM4NUPRtERrk6ZETkk
      DdwY1ELc9rRK7xNL3hTXabSFBmVrTPpfsUZSv2Y3LL1U
      FYq2BWQ1V5P1WFBqr3qB2Kb5yHVvSv7upzKodgQE5zXh
      AFZnHPzy4mvVCffrVwhewHbFc93uTHvDSFrVH7GtfXF1
      hUqAT1KQ7eW1i6Csp9CXYtpPfSAvi835V7wKi5fRfmC
      6Jp2xZUTWdDD2ZyUPRzeMdc6AFQ5K3pFgZxk2EijfjnM
      Fgc93D641F8N2d1xLjQ4jmShuD3GE3BsCXA56KBQbF5u
      9DRrwc4hSMQouw3ptpHXtQXVNHoqwyqLaK6NWztWTDin
      """);
  private static final String LIVE_REMOVE_DATA = "e6d7527ff165e3927e823b03000000000000000000000000";

  /// `borrow_from_custody` in `4sj5X9AY4PX9hUM1wSmiuvW6ceEdvJkGRNUgCpSAiCXxtHk6zaZWBFgGYMRXuXDio3Jo7v8dLtGdewxeiJVfQHGk`,
  /// slot 465175088, top-level instruction 2 of a legacy transaction, borrowing 7,797 USDC: the
  /// twelve IDL accounts, then the tail. Its client marked the tail's custodies writable, which
  /// the program tolerates; the helper's read-only tail rebuilds the same keys.
  private static final List<PublicKey> LIVE_BORROW = keys("""
      6Z6CTeC8yvrbCaCsxyHRBF8hnGygWYnwS6ynn4NNNTRz
      H4ND9aYttUVLFmNypZqLjZ52FYiGvdEB45GmwNoKEjTj
      5BUwFW4nRbftYTDMbgxykoFWqWHPzahFSNAaaaJtVKsq
      G18jKKXQwBbrHeiK3C9MRXhkHsLHf7XgCSisykV46EZa
      AVzP2GeRmqGphJsMxWoqjpUifPpCret7LqWhD8NWQK49
      9LnbJGnEYYQAAw1n7z4zHDXbCipRRGiCXbwX2FgXdtCM
      WzWUoCmtVv7eqAbU3BfKPU3fhLP6CXR8NCJH78UK9VS
      Ai3s2Sz8MyxqPYhb9uhvhM5J9ZDAgkeLWVqnK8BrsDfA
      27G8MtK7VtTcCHkpASjSDdkWWYfoqT6ggEuKidVJidD4
      TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA
      37hJBDnntwqhGbK7L6M1bLyvccj4u55CCUiLPdYkiqBN
      PERPHjGBqRHArX4DySjwM6UJHiR3sWAatqfdBS2qQJu
      7xS2gz2bTp3fwCC7knJvUWTEU9Tycczu6VhJYKgi1wdz
      AQCGyheWPLeo6Qp9WpYS9m3Qj479t7R636N9ey1rEjEn
      5Pv3gM9JrFFH883SWAhvJC9RPYmo8UNxuFtv5bMMALkm
      G18jKKXQwBbrHeiK3C9MRXhkHsLHf7XgCSisykV46EZa
      4vkNeXiYEUizLdrpdPS1eC2mccyM4NUPRtERrk6ZETkk
      DdwY1ELc9rRK7xNL3hTXabSFBmVrTPpfsUZSv2Y3LL1U
      FYq2BWQ1V5P1WFBqr3qB2Kb5yHVvSv7upzKodgQE5zXh
      AFZnHPzy4mvVCffrVwhewHbFc93uTHvDSFrVH7GtfXF1
      hUqAT1KQ7eW1i6Csp9CXYtpPfSAvi835V7wKi5fRfmC
      6Jp2xZUTWdDD2ZyUPRzeMdc6AFQ5K3pFgZxk2EijfjnM
      Fgc93D641F8N2d1xLjQ4jmShuD3GE3BsCXA56KBQbF5u
      9DRrwc4hSMQouw3ptpHXtQXVNHoqwyqLaK6NWztWTDin
      """);
  private static final String LIVE_BORROW_DATA = "99b741417121f92d40c7bcd001000000";

  private static List<PublicKey> keys(final String lines) {
    return lines.lines().map(PublicKey::fromBase58Encoded).toList();
  }

  private static List<PublicKey> keysOf(final List<AccountMeta> metas) {
    return metas.stream().map(AccountMeta::publicKey).toList();
  }

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static byte[] accountData(final String kind, final PublicKey address) {
    final var resource = "/jupiter/perpetuals/" + kind + '-' + address.toBase58() + ".base64";
    try (final var in = JupiterPerpetualsRemainingAccountsTests.class.getResourceAsStream(resource)) {
      assertNotNull(in, resource + " is missing");
      return Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.UTF_8).trim());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static byte[] poolData() {
    final byte[] data = accountData("pool", POOL);
    assertEquals(POOL_SPACE, data.length, "Pool account size");
    assertTrue(Pool.DISCRIMINATOR.equals(data, 0), "Pool discriminator");
    return data;
  }

  private static Pool pool() {
    return Pool.read(POOL, poolData());
  }

  private static byte[] custodyData(final int index) {
    final var address = CUSTODIES.get(index);
    final byte[] data = accountData("custody", address);
    assertEquals(CUSTODY_SPACE[index], data.length, "custody " + index + " account size");
    assertTrue(data.length >= Custody.BYTES, "custody " + index + " holds the generated layout");
    assertTrue(Custody.DISCRIMINATOR.equals(data, 0), "custody " + index + " discriminator");
    return data;
  }

  private static Custody custody(final int index) {
    return Custody.read(CUSTODIES.get(index), custodyData(index));
  }

  private static Map<PublicKey, Custody> custodies() {
    final var custodies = new HashMap<PublicKey, Custody>();
    for (int i = 0; i < CUSTODIES.size(); ++i) {
      custodies.put(CUSTODIES.get(i), custody(i));
    }
    return custodies;
  }

  private static Instruction refreshWithoutTail() {
    return PerpetualsProgram.refreshAssetsUnderManagement(
        INVOKED, REFRESH_KEEPER, PERPETUALS, POOL, JLP_MINT, new RefreshAssetsUnderManagementParams());
  }

  /// Rebuilt instructions must carry the live call's accounts in order and its data byte for
  /// byte, with the helper's twelve accounts read-only and unsigned after the declared ones.
  private static void assertLiveCall(final List<PublicKey> live,
                                     final String liveData,
                                     final int declared,
                                     final Instruction rebuilt) {
    assertEquals(PROGRAM, rebuilt.programId().publicKey());
    final var accounts = rebuilt.accounts();
    assertEquals(live, keysOf(accounts));
    assertEquals(liveData, HexFormat.of().formatHex(rebuilt.copyData()));
    assertEquals(declared + LIVE_TAIL.size(), accounts.size());
    for (int i = declared; i < accounts.size(); ++i) {
      assertFalse(accounts.get(i).write(), "tail account " + i + " is read-only");
      assertFalse(accounts.get(i).signer(), "tail account " + i + " does not sign");
    }
  }

  /// The fixtures are what the chain held: the Pool lists the six custodies in the order every
  /// live tail carries them, and each custody names this Pool and its own mint.
  @Test
  void theFixturesAreTheLivePoolAndItsCustodies() {
    final var pool = pool();
    assertEquals("Pool", pool.name());
    assertEquals(CUSTODIES, List.of(pool.custodies()));
    assertEquals(CUSTODIES, LIVE_TAIL.subList(0, CUSTODIES.size()));

    for (int i = 0; i < CUSTODIES.size(); ++i) {
      final var custody = custody(i);
      assertEquals(CUSTODIES.get(i), custody._address());
      assertEquals(POOL, custody.pool(), "custody " + i + " pool");
      assertEquals(MINTS.get(i), custody.mint(), "custody " + i + " mint");
    }
  }

  /// The helper's twelve metas are the live tail: the custodies in Pool order, then their
  /// `dovesAgOracle` keys, every one read-only and unsigned.
  ///
  /// Custody 0 (SOL) stores three distinct oracle keys, so it shows which field the tail takes:
  /// `dovesAgOracle`, which the program requires, not `dovesOracle` or the Pyth account, both of
  /// which it rejects with 6002. The JupUSD custody stores one key in all three fields and cannot
  /// tell them apart.
  @Test
  void theTailIsTheCustodiesThenTheirDovesAgOracles() {
    final var custodies = custodies();
    final var metas = JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool(), custodies);

    assertEquals(LIVE_TAIL, keysOf(metas));
    for (final var meta : metas) {
      assertFalse(meta.write(), meta.publicKey() + " is read-only");
      assertFalse(meta.signer(), meta.publicKey() + " does not sign");
    }

    final int n = CUSTODIES.size();
    assertEquals(2 * n, metas.size());
    for (int i = 0; i < n; ++i) {
      final var custody = custodies.get(CUSTODIES.get(i));
      assertEquals(CUSTODIES.get(i), metas.get(i).publicKey(), "custody slot " + i);
      assertEquals(custody.dovesAgOracle(), metas.get(n + i).publicKey(), "oracle slot " + (n + i));
    }

    final var sol = custodies.get(CUSTODIES.getFirst());
    assertEquals(PublicKey.fromBase58Encoded("FYq2BWQ1V5P1WFBqr3qB2Kb5yHVvSv7upzKodgQE5zXh"), sol.dovesAgOracle());
    assertEquals(PublicKey.fromBase58Encoded("39cWjvHrpHNz2SbXv6ME4NPhqBDBd4KsjUYv5JkHEAJU"), sol.dovesOracle());
    assertEquals(PublicKey.fromBase58Encoded("7UVimffxr9ow1uXYxsr4LHAcV58mLzhmwaeKvJ1pjLiE"), sol.oracle().oracleAccount());
    assertEquals(sol.dovesAgOracle(), metas.get(n).publicKey());
    assertNotEquals(sol.dovesOracle(), metas.get(n).publicKey());
    assertNotEquals(sol.oracle().oracleAccount(), metas.get(n).publicKey());

    final var jupUsd = custodies.get(CUSTODIES.getLast());
    assertEquals(jupUsd.dovesAgOracle(), jupUsd.dovesOracle());
    assertEquals(jupUsd.dovesAgOracle(), jupUsd.oracle().oracleAccount());
  }

  /// The keeper's refresh: four IDL accounts, then the tail.
  @Test
  void refreshAssetsUnderManagementMatchesTheLiveCall() {
    final var instruction = JupiterPerpetualsRemainingAccounts.appendPoolPricingAccounts(
        refreshWithoutTail(), pool(), custodies());

    assertLiveCall(LIVE_REFRESH, LIVE_REFRESH_DATA, 4, instruction);
  }

  /// A WBTC deposit. Both declared price slots take the custody's `dovesAgOracle`, read off the
  /// same record the tail is built from, and the tail repeats the declared custody and that oracle:
  /// the program indexes the tail by position, so nothing is deduplicated, and the declared custody
  /// keeps its writable flag while its tail copy stays read-only.
  @Test
  void addLiquidity2MatchesTheLiveCall() {
    final var custodies = custodies();
    final var wbtc = custodies.get(CUSTODIES.get(2));
    final var instruction = JupiterPerpetualsRemainingAccounts.appendPoolPricingAccounts(
        PerpetualsProgram.addLiquidity2(
            INVOKED,
            PublicKey.fromBase58Encoded("69yhtoJR4JYPPABZcSNkzuqbaFbwHsCkja1sP1Q2aVT5"),
            PublicKey.fromBase58Encoded("4TQcUWhtzjt4GPYunhD55jrBz4h1WkqB8DNtCQAVqLVt"),
            PublicKey.fromBase58Encoded("3YTsbrAAvbi4at77D3yCzRqKerqvdDRFX8nvqY1oe2kE"),
            TRANSFER_AUTHORITY,
            PERPETUALS,
            POOL,
            wbtc._address(),
            wbtc.dovesAgOracle(),
            wbtc.dovesAgOracle(),
            wbtc.tokenAccount(),
            JLP_MINT,
            TOKEN_PROGRAM,
            EVENT_AUTHORITY,
            PROGRAM,
            new AddLiquidity2Params(340, 0, OptionalLong.empty())),
        pool(), custodies);

    assertLiveCall(LIVE_ADD, LIVE_ADD_DATA, 14, instruction);

    final var accounts = instruction.accounts();
    assertEquals(wbtc._address(), accounts.get(6).publicKey());
    assertTrue(accounts.get(6).write(), "the declared custody stays writable");
    assertEquals(wbtc._address(), accounts.get(14 + 2).publicKey());
    assertFalse(accounts.get(14 + 2).write(), "its tail copy is read-only");
    assertEquals(wbtc.dovesAgOracle(), accounts.get(14 + 6 + 2).publicKey());
  }

  /// A JupUSD withdrawal, in a transaction that succeeded end to end.
  @Test
  void removeLiquidity2MatchesTheLiveCall() {
    final var custodies = custodies();
    final var jupUsd = custodies.get(CUSTODIES.get(5));
    final var instruction = JupiterPerpetualsRemainingAccounts.appendPoolPricingAccounts(
        PerpetualsProgram.removeLiquidity2(
            INVOKED,
            PublicKey.fromBase58Encoded("GxDC9e7SP9mzhDo4re5HbpLa2RW7gB9DtmThx4i4pXSq"),
            PublicKey.fromBase58Encoded("2jdL6KRa2sw7g9Zc5YJNeYSAQW16nSh6gRHLTAsovCz2"),
            PublicKey.fromBase58Encoded("APcBVekb2BUkNZ5WscezWoqUak6J9pQUaRV29hig1fcf"),
            TRANSFER_AUTHORITY,
            PERPETUALS,
            POOL,
            jupUsd._address(),
            jupUsd.dovesAgOracle(),
            jupUsd.dovesAgOracle(),
            jupUsd.tokenAccount(),
            JLP_MINT,
            TOKEN_PROGRAM,
            EVENT_AUTHORITY,
            PROGRAM,
            new RemoveLiquidity2Params(54_231_678L, 0)),
        pool(), custodies);

    assertLiveCall(LIVE_REMOVE, LIVE_REMOVE_DATA, 14, instruction);
  }

  /// A USDC borrow against JLP collateral.
  @Test
  void borrowFromCustodyMatchesTheLiveCall() {
    final var custodies = custodies();
    final var usdc = custodies.get(CUSTODIES.get(3));
    final var instruction = JupiterPerpetualsRemainingAccounts.appendPoolPricingAccounts(
        PerpetualsProgram.borrowFromCustody(
            INVOKED,
            PublicKey.fromBase58Encoded("6Z6CTeC8yvrbCaCsxyHRBF8hnGygWYnwS6ynn4NNNTRz"),
            PERPETUALS,
            POOL,
            usdc._address(),
            TRANSFER_AUTHORITY,
            PublicKey.fromBase58Encoded("9LnbJGnEYYQAAw1n7z4zHDXbCipRRGiCXbwX2FgXdtCM"),
            usdc.tokenAccount(),
            PublicKey.fromBase58Encoded("Ai3s2Sz8MyxqPYhb9uhvhM5J9ZDAgkeLWVqnK8BrsDfA"),
            JLP_MINT,
            TOKEN_PROGRAM,
            EVENT_AUTHORITY,
            PROGRAM,
            new BorrowFromCustodyParams(7_797_000_000L)),
        pool(), custodies);

    assertLiveCall(LIVE_BORROW, LIVE_BORROW_DATA, 12, instruction);
  }

  /// The order is the Pool's, whatever order the map iterates in: a sorted map and one filled in
  /// reverse both give the live tail.
  @Test
  void theOrderIsThePoolsNotTheMaps() {
    final var sorted = new TreeMap<>(custodies());
    assertNotEquals(CUSTODIES, new ArrayList<>(sorted.keySet()), "a sorted map would prove nothing");

    final var reversed = new LinkedHashMap<PublicKey, Custody>();
    for (final var custody : CUSTODIES.reversed()) {
      reversed.put(custody, sorted.get(custody));
    }

    final var pool = pool();
    assertEquals(LIVE_TAIL, keysOf(JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool, sorted)));
    assertEquals(LIVE_TAIL, keysOf(JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool, reversed)));
  }

  /// A map may hold more than the Pool lists; only the listed custodies are read, so another
  /// pool's custody under a key of its own neither throws nor joins the tail.
  @Test
  void custodiesThePoolDoesNotListAreIgnored() {
    final var stranger = key(0x51);
    final byte[] data = custodyData(0);
    key(0x52).write(data, Custody.POOL_OFFSET);

    final var custodies = custodies();
    custodies.put(stranger, Custody.read(stranger, data));

    assertEquals(LIVE_TAIL, keysOf(JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool(), custodies)));
  }

  @Test
  void aCustodyMissingFromTheMapIsRejected() {
    final var custodies = custodies();
    final var wbtc = CUSTODIES.get(2);
    custodies.remove(wbtc);

    final var thrown = assertThrows(IllegalArgumentException.class,
        () -> JupiterPerpetualsRemainingAccounts.appendPoolPricingAccounts(refreshWithoutTail(), pool(), custodies));
    assertTrue(thrown.getMessage().contains(wbtc.toBase58()), thrown.getMessage());
  }

  /// A custody listed by the Pool but naming another pool cannot price this one. The record is
  /// the live USDC custody with another key written over its `pool` field.
  @Test
  void aCustodyOfAnotherPoolIsRejected() {
    final var usdc = CUSTODIES.get(3);
    final var otherPool = key(0x52);
    final byte[] data = custodyData(3);
    otherPool.write(data, Custody.POOL_OFFSET);

    final var custodies = custodies();
    custodies.put(usdc, Custody.read(usdc, data));
    assertEquals(otherPool, custodies.get(usdc).pool());

    final var thrown = assertThrows(IllegalArgumentException.class,
        () -> JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool(), custodies));
    assertTrue(thrown.getMessage().contains(usdc.toBase58()), thrown.getMessage());
    assertTrue(thrown.getMessage().contains(otherPool.toBase58()), thrown.getMessage());
  }

  /// A record filed under another custody's key would put that custody's oracle in the wrong
  /// slot, which the program rejects with 6002. Here the live USDC custody sits under the SOL
  /// custody's key; both belong to the pool, so only the address check can tell.
  @Test
  void aCustodyFiledUnderAnotherCustodysKeyIsRejected() {
    final var sol = CUSTODIES.get(0);
    final var usdc = CUSTODIES.get(3);
    final var custodies = custodies();
    custodies.put(sol, custody(3));
    assertEquals(usdc, custodies.get(sol)._address());

    final var thrown = assertThrows(IllegalArgumentException.class,
        () -> JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool(), custodies));
    assertTrue(thrown.getMessage().contains(sol.toBase58()), thrown.getMessage());
    assertTrue(thrown.getMessage().contains(usdc.toBase58()), thrown.getMessage());
  }

  /// A custody decoded without its address, by `Custody.read(byte[], int)`, is taken at its key:
  /// the check guards a mismatch, not a missing address.
  @Test
  void aCustodyReadWithoutItsAddressIsTakenAtItsKey() {
    final var custodies = custodies();
    final var custody = Custody.read(custodyData(3), 0);
    assertNull(custody._address());
    custodies.put(CUSTODIES.get(3), custody);

    final var tail = JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool(), custodies);
    assertEquals(JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool(), custodies()), tail);
  }

  /// A Pool decoded without its address cannot vouch for its custodies, so it is refused rather
  /// than trusted.
  @Test
  void aPoolReadWithoutItsAddressIsRejected() {
    final var pool = Pool.read(poolData(), 0);
    assertNull(pool._address());

    assertThrows(IllegalArgumentException.class,
        () -> JupiterPerpetualsRemainingAccounts.poolPricingAccounts(pool, custodies()));
  }

  /// The positional overload builds the same tail from the keys alone.
  @Test
  void thePositionalOverloadBuildsTheSameTail() {
    final var custodies = custodies();
    final var oracles = CUSTODIES.stream().map(custody -> custodies.get(custody).dovesAgOracle()).toList();

    final var positional = JupiterPerpetualsRemainingAccounts
        .appendPoolPricingAccounts(refreshWithoutTail(), CUSTODIES, oracles)
        .accounts();
    final var fromRecords = JupiterPerpetualsRemainingAccounts
        .appendPoolPricingAccounts(refreshWithoutTail(), pool(), custodies)
        .accounts();

    assertEquals(LIVE_REFRESH, keysOf(positional));
    assertEquals(keysOf(fromRecords), keysOf(positional));
    for (final var meta : positional.subList(4, positional.size())) {
      assertFalse(meta.write(), meta.publicKey() + " is read-only");
      assertFalse(meta.signer(), meta.publicKey() + " does not sign");
    }
  }

  /// A short block would shift every oracle after it into the wrong slot, so it is rejected
  /// rather than truncated or padded.
  @Test
  void positionalBlocksOfDifferentSizesAreRejected() {
    final var oracles = LIVE_TAIL.subList(CUSTODIES.size(), LIVE_TAIL.size());

    assertThrows(IllegalArgumentException.class, () -> JupiterPerpetualsRemainingAccounts
        .appendPoolPricingAccounts(refreshWithoutTail(), CUSTODIES, oracles.subList(0, oracles.size() - 1)));
    assertThrows(IllegalArgumentException.class, () -> JupiterPerpetualsRemainingAccounts
        .appendPoolPricingAccounts(refreshWithoutTail(), CUSTODIES.subList(1, CUSTODIES.size()), oracles));
  }
}
