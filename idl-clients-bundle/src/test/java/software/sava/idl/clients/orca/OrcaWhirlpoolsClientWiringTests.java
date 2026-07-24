package software.sava.idl.clients.orca;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.orca.whirlpools.gen.WhirlpoolPDAs;
import software.sava.idl.clients.orca.whirlpools.gen.WhirlpoolProgram;
import software.sava.idl.clients.orca.whirlpools.gen.types.AccountsType;
import software.sava.idl.clients.orca.whirlpools.gen.types.IncreaseLiquidityMethod;
import software.sava.idl.clients.orca.whirlpools.gen.types.LockType;
import software.sava.idl.clients.orca.whirlpools.gen.types.OpenPositionWithMetadataBumps;
import software.sava.idl.clients.orca.whirlpools.gen.types.RemainingAccountsInfo;
import software.sava.idl.clients.orca.whirlpools.gen.types.RemainingAccountsSlice;
import software.sava.idl.clients.orca.whirlpools.gen.types.RepositionLiquidityMethod;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/// Pins every remaining instruction builder on the Whirlpools client to the
/// generated `WhirlpoolProgram` call it must delegate to — the generated
/// builder's parameter names are the reference for each slot, and the
/// client-supplied values (the invoked program, the auto-wired Solana
/// accounts, and the trailing `whirlpool_program` account the published IDL
/// declares on every instruction) are spelled out on the mirror side. Every
/// caller-supplied account is a distinct fill-byte key, so a transposed pair
/// of same-typed keys changes the compared list. The `default` overloads are
/// then checked against the explicit calls they delegate to: the property
/// that matters is which *derived* key — position PDA, ATA, lock config,
/// bundled position, oracle — lands in which slot, and under which token
/// program the ATAs derive (classic vs 2022 differs per family).
final class OrcaWhirlpoolsClientWiringTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final OrcaAccounts ORCA_ACCOUNTS = OrcaAccounts.MAIN_NET;
  private static final PublicKey WHIRLPOOL_PROGRAM = ORCA_ACCOUNTS.invokedWhirlpoolProgram().publicKey();

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static final PublicKey FUNDER = key(0x11);
  private static final PublicKey OWNER = key(0x12);
  private static final PublicKey AUTHORITY = key(0x13);
  private static final PublicKey RECEIVER = key(0x14);
  private static final PublicKey POSITION = key(0x15);
  private static final PublicKey POSITION_MINT = key(0x16);
  private static final PublicKey POSITION_TA = key(0x17);
  private static final PublicKey WHIRLPOOL = key(0x18);
  private static final PublicKey METADATA = key(0x19);
  private static final PublicKey LOCK_CONFIG = key(0x1A);
  private static final PublicKey DESTINATION_TA = key(0x1B);
  private static final PublicKey BUNDLE = key(0x1C);
  private static final PublicKey BUNDLE_MINT = key(0x1D);
  private static final PublicKey BUNDLE_TA = key(0x1E);
  private static final PublicKey BUNDLED_POSITION = key(0x1F);
  private static final PublicKey TICK_ARRAY = key(0x20);
  private static final PublicKey MINT_A = key(0x21);
  private static final PublicKey MINT_B = key(0x22);
  private static final PublicKey OWNER_A = key(0x23);
  private static final PublicKey OWNER_B = key(0x24);
  private static final PublicKey VAULT_A = key(0x25);
  private static final PublicKey VAULT_B = key(0x26);
  private static final PublicKey TICK_LOWER = key(0x27);
  private static final PublicKey TICK_UPPER = key(0x28);
  private static final PublicKey NEW_TICK_LOWER = key(0x29);
  private static final PublicKey NEW_TICK_UPPER = key(0x2A);
  private static final PublicKey REWARD_OWNER = key(0x2B);
  private static final PublicKey REWARD_MINT = key(0x2C);
  private static final PublicKey REWARD_VAULT = key(0x2D);
  private static final PublicKey TOKEN_PROGRAM_A = key(0x2E);
  private static final PublicKey TOKEN_PROGRAM_B = key(0x2F);
  private static final PublicKey ORACLE = key(0x30);

  private static final RemainingAccountsInfo REMAINING = new RemainingAccountsInfo(
      new RemainingAccountsSlice[]{new RemainingAccountsSlice(AccountsType.SupplementalTickArrays, 2)});

  /// Built inside the test bodies — factory coverage attributed to a field
  /// initializer is unstable under PIT.
  private static OrcaWhirlpoolsClient client() {
    return OrcaWhirlpoolsClient.createClient(SOLANA_ACCOUNTS, ORCA_ACCOUNTS);
  }

  private static void assertIx(final Instruction expected, final Instruction actual) {
    assertEquals(expected.programId().publicKey(), actual.programId().publicKey(), "invoked program");
    assertEquals(
        expected.accounts().stream().map(AccountMeta::publicKey).toList(),
        actual.accounts().stream().map(AccountMeta::publicKey).toList(),
        "account order"
    );
    for (int i = 0; i < expected.accounts().size(); i++) {
      final var e = expected.accounts().get(i);
      final var a = actual.accounts().get(i);
      assertEquals(e.write(), a.write(), "writable at slot " + i);
      assertEquals(e.signer(), a.signer(), "signer at slot " + i);
    }
    assertArrayEquals(expected.data(), actual.data(), "instruction data");
  }

  // ---------------------------------------------------------------------------
  // derivations
  // ---------------------------------------------------------------------------

  @Test
  void remainingDerivationsBindTheProgramAndEveryInput() {
    final var client = client();

    assertEquals(
        WhirlpoolPDAs.lockConfigPDA(WHIRLPOOL_PROGRAM, POSITION).publicKey(),
        client.deriveLockConfigKey(POSITION));
    assertNotEquals(client.deriveLockConfigKey(POSITION), client.deriveLockConfigKey(key(0x41)));

    assertEquals(
        WhirlpoolPDAs.positionBundlePDA(WHIRLPOOL_PROGRAM, BUNDLE_MINT).publicKey(),
        client.derivePositionBundleKey(BUNDLE_MINT));
    assertNotEquals(client.derivePositionBundleKey(BUNDLE_MINT), client.derivePositionBundleKey(key(0x41)));

    assertEquals(
        OrcaUtil.bundledPositionPDA(WHIRLPOOL_PROGRAM, BUNDLE_MINT, 7).publicKey(),
        client.deriveBundledPositionKey(BUNDLE_MINT, 7));
    assertNotEquals(
        client.deriveBundledPositionKey(BUNDLE_MINT, 7),
        client.deriveBundledPositionKey(BUNDLE_MINT, 8),
        "the bundle index participates");

    assertEquals(
        OrcaUtil.tickArrayPDA(WHIRLPOOL_PROGRAM, WHIRLPOOL, -443_584).publicKey(),
        client.deriveTickArrayKey(WHIRLPOOL, -443_584));
    assertNotEquals(
        client.deriveTickArrayKey(WHIRLPOOL, -443_584),
        client.deriveTickArrayKey(WHIRLPOOL, 0),
        "the start tick participates");

    // same-shaped neighbours stay separated
    assertNotEquals(client.deriveLockConfigKey(POSITION), client.derivePositionKey(POSITION));
    assertNotEquals(client.derivePositionBundleKey(BUNDLE_MINT), client.derivePositionKey(BUNDLE_MINT));
  }

  // ---------------------------------------------------------------------------
  // position lifecycle
  // ---------------------------------------------------------------------------

  @Test
  void openPositionWithMetadataBindsAndDerives() {
    final var client = client();
    final var bumps = new OpenPositionWithMetadataBumps(254, 253);
    assertIx(
        WhirlpoolProgram.openPositionWithMetadata(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            FUNDER, OWNER, POSITION, POSITION_MINT, METADATA, POSITION_TA, WHIRLPOOL,
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, WHIRLPOOL_PROGRAM,
            bumps, -128, 128
        ),
        client.openPositionWithMetadata(
            FUNDER, OWNER, POSITION, POSITION_MINT, METADATA, POSITION_TA, WHIRLPOOL,
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, bumps, -128, 128
        )
    );
    // the short form derives the position and its classic-token ATA, and wires
    // the metadata program and NFT update authority from the Orca accounts
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();
    assertIx(
        client.openPositionWithMetadata(
            FUNDER, OWNER,
            client.derivePositionKey(POSITION_MINT),
            POSITION_MINT,
            METADATA,
            client.deriveATA(OWNER, tokenProgram, POSITION_MINT),
            WHIRLPOOL,
            tokenProgram,
            ORCA_ACCOUNTS.tokenMetadataProgram(),
            ORCA_ACCOUNTS.whirlpoolNftUpdateAuthority(),
            bumps, -128, 128
        ),
        client.openPositionWithMetadata(FUNDER, OWNER, POSITION_MINT, METADATA, WHIRLPOOL, bumps, -128, 128)
    );
  }

  @Test
  void openPositionWithTokenExtensionsBindsAndDerives() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.openPositionWithTokenExtensions(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            FUNDER, OWNER, POSITION, POSITION_MINT, POSITION_TA, WHIRLPOOL,
            TOKEN_PROGRAM_A, AUTHORITY, WHIRLPOOL_PROGRAM,
            -128, 128, true
        ),
        client.openPositionWithTokenExtensions(
            FUNDER, OWNER, POSITION, POSITION_MINT, POSITION_TA, WHIRLPOOL,
            TOKEN_PROGRAM_A, AUTHORITY, -128, 128, true
        )
    );
    // the short form derives under the *2022* token program
    final var token2022 = SOLANA_ACCOUNTS.token2022Program();
    assertIx(
        client.openPositionWithTokenExtensions(
            FUNDER, OWNER,
            client.derivePositionKey(POSITION_MINT),
            POSITION_MINT,
            client.deriveATA(OWNER, token2022, POSITION_MINT),
            WHIRLPOOL,
            token2022,
            ORCA_ACCOUNTS.whirlpoolNftUpdateAuthority(),
            -128, 128, true
        ),
        client.openPositionWithTokenExtensions(FUNDER, OWNER, POSITION_MINT, WHIRLPOOL, -128, 128, true)
    );
  }

  @Test
  void closePositionVariantsBindAndDerive() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.closePosition(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            AUTHORITY, RECEIVER, POSITION, POSITION_MINT, POSITION_TA, TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM
        ),
        client.closePosition(AUTHORITY, RECEIVER, POSITION, POSITION_MINT, POSITION_TA, TOKEN_PROGRAM_A)
    );
    assertIx(
        WhirlpoolProgram.closePositionWithTokenExtensions(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            AUTHORITY, RECEIVER, POSITION, POSITION_MINT, POSITION_TA, TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM
        ),
        client.closePositionWithTokenExtensions(AUTHORITY, RECEIVER, POSITION, POSITION_MINT, POSITION_TA, TOKEN_PROGRAM_A)
    );

    // the short forms derive the position and the *authority's* ATA — classic
    // token for closePosition, 2022 for the extensions variant
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();
    assertIx(
        client.closePosition(
            AUTHORITY, RECEIVER,
            client.derivePositionKey(POSITION_MINT),
            POSITION_MINT,
            client.deriveATA(AUTHORITY, tokenProgram, POSITION_MINT),
            tokenProgram
        ),
        client.closePosition(AUTHORITY, RECEIVER, POSITION_MINT)
    );
    final var token2022 = SOLANA_ACCOUNTS.token2022Program();
    assertIx(
        client.closePositionWithTokenExtensions(
            AUTHORITY, RECEIVER,
            client.derivePositionKey(POSITION_MINT),
            POSITION_MINT,
            client.deriveATA(AUTHORITY, token2022, POSITION_MINT),
            token2022
        ),
        client.closePositionWithTokenExtensions(AUTHORITY, RECEIVER, POSITION_MINT)
    );
  }

  @Test
  void lockAndTransferLockedPositionBindAndDerive() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.lockPosition(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            FUNDER, AUTHORITY, POSITION, POSITION_MINT, POSITION_TA, LOCK_CONFIG, WHIRLPOOL,
            TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM, LockType.Permanent
        ),
        client.lockPosition(
            FUNDER, AUTHORITY, POSITION, POSITION_MINT, POSITION_TA, LOCK_CONFIG, WHIRLPOOL,
            TOKEN_PROGRAM_A, LockType.Permanent
        )
    );
    assertIx(
        WhirlpoolProgram.transferLockedPosition(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            AUTHORITY, RECEIVER, POSITION, POSITION_MINT, POSITION_TA, DESTINATION_TA,
            LOCK_CONFIG, TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM
        ),
        client.transferLockedPosition(
            AUTHORITY, RECEIVER, POSITION, POSITION_MINT, POSITION_TA, DESTINATION_TA,
            LOCK_CONFIG, TOKEN_PROGRAM_A
        )
    );

    // short forms: the lock config derives off the *derived position*, the
    // ATAs off the 2022 program, and the transfer destination off its owner
    final var token2022 = SOLANA_ACCOUNTS.token2022Program();
    final var positionKey = client.derivePositionKey(POSITION_MINT);
    assertIx(
        client.lockPosition(
            FUNDER, AUTHORITY,
            positionKey,
            POSITION_MINT,
            client.deriveATA(AUTHORITY, token2022, POSITION_MINT),
            client.deriveLockConfigKey(positionKey),
            WHIRLPOOL,
            token2022,
            LockType.Permanent
        ),
        client.lockPosition(FUNDER, AUTHORITY, POSITION_MINT, WHIRLPOOL, LockType.Permanent)
    );
    final var destinationOwner = key(0x42);
    assertIx(
        client.transferLockedPosition(
            AUTHORITY, RECEIVER,
            positionKey,
            POSITION_MINT,
            client.deriveATA(AUTHORITY, token2022, POSITION_MINT),
            client.deriveATA(destinationOwner, token2022, POSITION_MINT),
            client.deriveLockConfigKey(positionKey),
            token2022
        ),
        client.transferLockedPosition(AUTHORITY, RECEIVER, POSITION_MINT, destinationOwner)
    );
  }

  @Test
  void resetPositionRangeBindsTheGeneratedBuilder() {
    assertIx(
        WhirlpoolProgram.resetPositionRange(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            FUNDER, AUTHORITY, WHIRLPOOL, POSITION, POSITION_TA, WHIRLPOOL_PROGRAM, -64, 64
        ),
        client().resetPositionRange(FUNDER, AUTHORITY, WHIRLPOOL, POSITION, POSITION_TA, -64, 64)
    );
  }

  // ---------------------------------------------------------------------------
  // position bundles
  // ---------------------------------------------------------------------------

  @Test
  void positionBundleLifecycleBindsAndDerives() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.initializePositionBundle(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            BUNDLE, BUNDLE_MINT, BUNDLE_TA, OWNER, FUNDER, TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM
        ),
        client.initializePositionBundle(BUNDLE, BUNDLE_MINT, BUNDLE_TA, OWNER, FUNDER, TOKEN_PROGRAM_A)
    );
    assertIx(
        WhirlpoolProgram.initializePositionBundleWithMetadata(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            BUNDLE, BUNDLE_MINT, METADATA, BUNDLE_TA, OWNER, FUNDER, AUTHORITY,
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, WHIRLPOOL_PROGRAM
        ),
        client.initializePositionBundleWithMetadata(
            BUNDLE, BUNDLE_MINT, METADATA, BUNDLE_TA, OWNER, FUNDER, AUTHORITY,
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B
        )
    );
    assertIx(
        WhirlpoolProgram.deletePositionBundle(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            BUNDLE, BUNDLE_MINT, BUNDLE_TA, OWNER, RECEIVER, TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM
        ),
        client.deletePositionBundle(BUNDLE, BUNDLE_MINT, BUNDLE_TA, OWNER, RECEIVER, TOKEN_PROGRAM_A)
    );
  }

  @Test
  void bundledPositionsBindAndDerive() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.openBundledPosition(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            BUNDLED_POSITION, BUNDLE, BUNDLE_TA, AUTHORITY, WHIRLPOOL, FUNDER, WHIRLPOOL_PROGRAM,
            7, -128, 128
        ),
        client.openBundledPosition(BUNDLED_POSITION, BUNDLE, BUNDLE_TA, AUTHORITY, WHIRLPOOL, FUNDER, 7, -128, 128)
    );
    assertIx(
        WhirlpoolProgram.closeBundledPosition(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            BUNDLED_POSITION, BUNDLE, BUNDLE_TA, AUTHORITY, RECEIVER, WHIRLPOOL_PROGRAM, 7
        ),
        client.closeBundledPosition(BUNDLED_POSITION, BUNDLE, BUNDLE_TA, AUTHORITY, RECEIVER, 7)
    );

    // the short forms derive the bundled position (mint + index), the bundle
    // PDA, and the *owner's* classic-token ATA for the bundle mint
    final var tokenProgram = SOLANA_ACCOUNTS.tokenProgram();
    assertIx(
        client.openBundledPosition(
            client.deriveBundledPositionKey(BUNDLE_MINT, 7),
            client.derivePositionBundleKey(BUNDLE_MINT),
            client.deriveATA(OWNER, tokenProgram, BUNDLE_MINT),
            AUTHORITY, WHIRLPOOL, FUNDER, 7, -128, 128
        ),
        client.openBundledPosition(BUNDLE_MINT, OWNER, AUTHORITY, WHIRLPOOL, FUNDER, 7, -128, 128)
    );
    assertIx(
        client.closeBundledPosition(
            client.deriveBundledPositionKey(BUNDLE_MINT, 7),
            client.derivePositionBundleKey(BUNDLE_MINT),
            client.deriveATA(OWNER, tokenProgram, BUNDLE_MINT),
            AUTHORITY, RECEIVER, 7
        ),
        client.closeBundledPosition(BUNDLE_MINT, OWNER, AUTHORITY, RECEIVER, 7)
    );
  }

  // ---------------------------------------------------------------------------
  // tick arrays
  // ---------------------------------------------------------------------------

  @Test
  void tickArrayInitializersBindTheGeneratedBuilders() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.initializeTickArray(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, FUNDER, TICK_ARRAY, WHIRLPOOL_PROGRAM, -443_584
        ),
        client.initializeTickArray(WHIRLPOOL, FUNDER, TICK_ARRAY, -443_584)
    );
    assertIx(
        WhirlpoolProgram.initializeDynamicTickArray(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, FUNDER, TICK_ARRAY, WHIRLPOOL_PROGRAM, -443_584, true
        ),
        client.initializeDynamicTickArray(WHIRLPOOL, FUNDER, TICK_ARRAY, -443_584, true)
    );
  }

  // ---------------------------------------------------------------------------
  // liquidity
  // ---------------------------------------------------------------------------

  @Test
  void liquidityBuildersBindTheGeneratedCalls() {
    final var client = client();
    final var liquidity = BigInteger.valueOf(1_000_000L);
    assertIx(
        WhirlpoolProgram.increaseLiquidity(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            WHIRLPOOL, TOKEN_PROGRAM_A, AUTHORITY, POSITION, POSITION_TA,
            OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER, WHIRLPOOL_PROGRAM,
            liquidity, 10L, 20L
        ),
        client.increaseLiquidity(
            WHIRLPOOL, TOKEN_PROGRAM_A, AUTHORITY, POSITION, POSITION_TA,
            OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER, liquidity, 10L, 20L
        )
    );
    assertIx(
        WhirlpoolProgram.increaseLiquidityV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER,
            WHIRLPOOL_PROGRAM, liquidity, 10L, 20L, REMAINING
        ),
        client.increaseLiquidityV2(
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER,
            liquidity, 10L, 20L, REMAINING
        )
    );
    final var byAmounts = new IncreaseLiquidityMethod.ByTokenAmounts(
        10L, 20L, BigInteger.ONE, BigInteger.TWO);
    assertIx(
        WhirlpoolProgram.increaseLiquidityByTokenAmountsV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER,
            WHIRLPOOL_PROGRAM, byAmounts, REMAINING
        ),
        client.increaseLiquidityByTokenAmountsV2(
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER,
            byAmounts, REMAINING
        )
    );
    assertIx(
        WhirlpoolProgram.decreaseLiquidity(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            WHIRLPOOL, TOKEN_PROGRAM_A, AUTHORITY, POSITION, POSITION_TA,
            OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER, WHIRLPOOL_PROGRAM,
            liquidity, 1L, 2L
        ),
        client.decreaseLiquidity(
            WHIRLPOOL, TOKEN_PROGRAM_A, AUTHORITY, POSITION, POSITION_TA,
            OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER, liquidity, 1L, 2L
        )
    );
    assertIx(
        WhirlpoolProgram.decreaseLiquidityV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER,
            WHIRLPOOL_PROGRAM, liquidity, 1L, 2L, REMAINING
        ),
        client.decreaseLiquidityV2(
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B, TICK_LOWER, TICK_UPPER,
            liquidity, 1L, 2L, REMAINING
        )
    );
    final var byLiquidity = new RepositionLiquidityMethod.ByLiquidity(liquidity, 1L, 2L, 10L, 20L);
    assertIx(
        WhirlpoolProgram.repositionLiquidityV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, FUNDER, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B,
            TICK_LOWER, TICK_UPPER, NEW_TICK_LOWER, NEW_TICK_UPPER,
            WHIRLPOOL_PROGRAM, -64, 64, byLiquidity, REMAINING
        ),
        client.repositionLiquidityV2(
            WHIRLPOOL, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, FUNDER, POSITION, POSITION_TA,
            MINT_A, MINT_B, OWNER_A, OWNER_B, VAULT_A, VAULT_B,
            TICK_LOWER, TICK_UPPER, NEW_TICK_LOWER, NEW_TICK_UPPER,
            -64, 64, byLiquidity, REMAINING
        )
    );
  }

  // ---------------------------------------------------------------------------
  // fees & rewards
  // ---------------------------------------------------------------------------

  @Test
  void feeAndRewardBuildersBindTheGeneratedCalls() {
    final var client = client();
    assertIx(
        WhirlpoolProgram.updateFeesAndRewards(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            WHIRLPOOL, POSITION, TICK_LOWER, TICK_UPPER, WHIRLPOOL_PROGRAM
        ),
        client.updateFeesAndRewards(WHIRLPOOL, POSITION, TICK_LOWER, TICK_UPPER)
    );
    assertIx(
        WhirlpoolProgram.collectFees(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM
        ),
        client.collectFees(
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, OWNER_A, VAULT_A, OWNER_B, VAULT_B, TOKEN_PROGRAM_A
        )
    );
    assertIx(
        WhirlpoolProgram.collectFeesV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, MINT_A, MINT_B,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B,
            WHIRLPOOL_PROGRAM, REMAINING
        ),
        client.collectFeesV2(
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, MINT_A, MINT_B,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, REMAINING
        )
    );
    assertIx(
        WhirlpoolProgram.collectReward(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, REWARD_OWNER, REWARD_VAULT,
            TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM, 2
        ),
        client.collectReward(WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, REWARD_OWNER, REWARD_VAULT, TOKEN_PROGRAM_A, 2)
    );
    assertIx(
        WhirlpoolProgram.collectRewardV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, REWARD_OWNER, REWARD_MINT, REWARD_VAULT,
            TOKEN_PROGRAM_A, WHIRLPOOL_PROGRAM, 2, REMAINING
        ),
        client.collectRewardV2(
            WHIRLPOOL, AUTHORITY, POSITION, POSITION_TA, REWARD_OWNER, REWARD_MINT, REWARD_VAULT,
            TOKEN_PROGRAM_A, 2, REMAINING
        )
    );
  }

  // ---------------------------------------------------------------------------
  // swaps
  // ---------------------------------------------------------------------------

  @Test
  void swapV2BindsAndDerivesTheOracle() {
    final var client = client();
    final var limit = BigInteger.ONE.shiftLeft(64);
    assertIx(
        WhirlpoolProgram.swapV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, WHIRLPOOL, MINT_A, MINT_B,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TICK_LOWER, TICK_UPPER, TICK_ARRAY, ORACLE,
            WHIRLPOOL_PROGRAM, 1_000L, 900L, limit, true, false, REMAINING
        ),
        client.swapV2(
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, WHIRLPOOL, MINT_A, MINT_B,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TICK_LOWER, TICK_UPPER, TICK_ARRAY, ORACLE,
            1_000L, 900L, limit, true, false, REMAINING
        )
    );
    // the oracle-less overload derives it from the whirlpool
    assertIx(
        client.swapV2(
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, WHIRLPOOL, MINT_A, MINT_B,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TICK_LOWER, TICK_UPPER, TICK_ARRAY,
            client.deriveOracleKey(WHIRLPOOL),
            1_000L, 900L, limit, true, false, REMAINING
        ),
        client.swapV2(
            TOKEN_PROGRAM_A, TOKEN_PROGRAM_B, AUTHORITY, WHIRLPOOL, MINT_A, MINT_B,
            OWNER_A, VAULT_A, OWNER_B, VAULT_B, TICK_LOWER, TICK_UPPER, TICK_ARRAY,
            1_000L, 900L, limit, true, false, REMAINING
        )
    );
  }

  @Test
  void twoHopSwapsBindTheGeneratedCalls() {
    final var client = client();
    final var pool2 = key(0x43);
    final var oracle2 = key(0x44);
    final var limit1 = BigInteger.ONE.shiftLeft(64);
    final var limit2 = BigInteger.ONE.shiftLeft(65);
    assertIx(
        WhirlpoolProgram.twoHopSwap(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(),
            TOKEN_PROGRAM_A, AUTHORITY, WHIRLPOOL, pool2,
            key(0x50), key(0x51), key(0x52), key(0x53),
            key(0x54), key(0x55), key(0x56), key(0x57),
            key(0x58), key(0x59), key(0x5A), key(0x5B), key(0x5C), key(0x5D),
            ORACLE, oracle2, WHIRLPOOL_PROGRAM,
            1_000L, 900L, true, true, false, limit1, limit2
        ),
        client.twoHopSwap(
            TOKEN_PROGRAM_A, AUTHORITY, WHIRLPOOL, pool2,
            key(0x50), key(0x51), key(0x52), key(0x53),
            key(0x54), key(0x55), key(0x56), key(0x57),
            key(0x58), key(0x59), key(0x5A), key(0x5B), key(0x5C), key(0x5D),
            ORACLE, oracle2,
            1_000L, 900L, true, true, false, limit1, limit2
        )
    );
    assertIx(
        WhirlpoolProgram.twoHopSwapV2(
            ORCA_ACCOUNTS.invokedWhirlpoolProgram(), SOLANA_ACCOUNTS,
            WHIRLPOOL, pool2, key(0x50), key(0x51), key(0x52),
            key(0x53), key(0x54), key(0x55),
            key(0x56), key(0x57), key(0x58), key(0x59), key(0x5A), key(0x5B),
            AUTHORITY,
            key(0x5C), key(0x5D), key(0x5E), key(0x5F), key(0x60), key(0x61),
            ORACLE, oracle2, WHIRLPOOL_PROGRAM,
            1_000L, 900L, true, true, false, limit1, limit2, REMAINING
        ),
        client.twoHopSwapV2(
            WHIRLPOOL, pool2, key(0x50), key(0x51), key(0x52),
            key(0x53), key(0x54), key(0x55),
            key(0x56), key(0x57), key(0x58), key(0x59), key(0x5A), key(0x5B),
            AUTHORITY,
            key(0x5C), key(0x5D), key(0x5E), key(0x5F), key(0x60), key(0x61),
            ORACLE, oracle2,
            1_000L, 900L, true, true, false, limit1, limit2, REMAINING
        )
    );
  }
}
