package software.sava.idl.clients.kamino.lend;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.kamino.KaminoAccounts;
import software.sava.idl.clients.kamino.lend.gen.types.Reserve;

// https://github.com/Kamino-Finance/klend-sdk/blob/master/src/utils/seeds.ts
public interface KaminoReservePDAs {

  /// Reads the four vault addresses off a reserve, which is the only way to get them
  /// right for a reserve that already exists.
  ///
  /// klend changed how it derives them — from `[seed, market, mint]` to
  /// `[seed, reserve]` — and a reserve keeps whatever addresses it was created with, so
  /// the two forms coexist on chain. Deriving therefore cannot work for both; the
  /// program does not try, and pins each account against the value stored here instead
  /// (`address = reserve.load()?.liquidity.supply_vault`, and so on). The mint, its
  /// token program and the owning market come from the same account, so nothing about
  /// this can drift from the reserve it describes.
  static KaminoReservePDAs createPDAs(final PublicKey programId, final Reserve reserve) {
    final var liquidity = reserve.liquidity();
    final var collateral = reserve.collateral();
    return createPDAs(
        KaminoMarketPDAs.createPDAs(programId, reserve.lendingMarket()),
        liquidity.mintPubkey(),
        liquidity.tokenProgram(),
        liquidity.supplyVault(),
        collateral.mintPubkey(),
        collateral.supplyVault(),
        liquidity.feeVault()
    );
  }

  /// The four vault addresses given directly, for a caller that already holds them.
  static KaminoReservePDAs createPDAs(final KaminoMarketPDAs marketPDAs,
                                      final PublicKey mint,
                                      final PublicKey tokenProgram,
                                      final PublicKey liquiditySupplyVault,
                                      final PublicKey collateralMint,
                                      final PublicKey collateralSupplyVault,
                                      final PublicKey feeVault) {
    return new KaminoReservePDAsRecord(
        marketPDAs,
        mint,
        tokenProgram,
        liquiditySupplyVault,
        collateralMint,
        collateralSupplyVault,
        feeVault
    );
  }

  /// Derives the addresses a reserve *would* be created with, for building `initReserve`
  /// and nothing else.
  ///
  /// These are the seeds the deployed program uses today, so they are right for a
  /// reserve that does not exist yet and wrong for most that do — anything created
  /// before klend switched to seeding on the reserve address keeps its original
  /// addresses. Use [#createPDAs(PublicKey, Reserve)] for an existing reserve.
  static KaminoReservePDAs createPDAsForNewReserve(final PublicKey programId,
                                                   final KaminoMarketPDAs marketPDAs,
                                                   final PublicKey reserve,
                                                   final PublicKey mint,
                                                   final PublicKey tokenProgram) {
    return new KaminoReservePDAsRecord(
        marketPDAs,
        mint,
        tokenProgram,
        KaminoAccounts.reserveLiqSupplyPda(reserve, programId).publicKey(),
        KaminoAccounts.reserveCollateralMintPda(reserve, programId).publicKey(),
        KaminoAccounts.reserveCollateralSupplyPda(reserve, programId).publicKey(),
        KaminoAccounts.reserveFeeVaultPda(reserve, programId).publicKey()
    );
  }

  KaminoMarketPDAs marketPDAs();

  default PublicKey market() {
    return marketPDAs().market();
  }

  default PublicKey marketAuthority() {
    return marketPDAs().authority();
  }

  PublicKey mint();

  PublicKey tokenProgram();

  PublicKey liquiditySupplyVault();

  PublicKey collateralMint();

  PublicKey collateralSupplyVault();

  PublicKey feeVault();
}
