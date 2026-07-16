package software.sava.idl.clients.meteora;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

/// Pins the hand-written Meteora DLMM PDA-derivation helpers against a real
/// mainnet LbPair. A wrong seed encoding (mint ordering, u16 little-endian bin
/// step / base factor) would derive an address that does not exist on-chain.
///
/// Anchor account: LbPair `112JU91seUnMLBmXxwqiyXbaZhbFRxGJprvh7pYWT98`
/// (owner `LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo`, 904 bytes). The LbPair's
/// OWN address equals `lbPairPDA(tokenXMint, tokenYMint, binStep, baseFactor)`,
/// and its stored `reserveX` / `reserveY` are `reservePDA(lbPair, mint)` outputs.
/// All expected values are read directly from that on-chain account.
final class MeteoraPDATests {

  private static final PublicKey DLMM_PROGRAM =
      PublicKey.fromBase58Encoded("LBUZKhRxPF3XUpBCjp4YzTKgLccjZhTSDM9YuVaPwxo");

  private static final PublicKey LB_PAIR =
      PublicKey.fromBase58Encoded("112JU91seUnMLBmXxwqiyXbaZhbFRxGJprvh7pYWT98");

  // Fields read out of the LbPair account (the program's own PDA inputs / outputs).
  private static final PublicKey TOKEN_X_MINT =
      PublicKey.fromBase58Encoded("2CZNBcjjxE6wYfHKdnRAemynnoU9v24vW83W3PUZnPrj");
  private static final PublicKey TOKEN_Y_MINT =
      PublicKey.fromBase58Encoded("74SBV4zDXxTRgv1pEMoECskKBkZHc2yGPnc7GYVepump");
  private static final int BIN_STEP = 80;
  private static final int BASE_FACTOR = 62500;

  private static final PublicKey RESERVE_X =
      PublicKey.fromBase58Encoded("4NJqVStmuPc7ZhEn6ELPNmUry95Nikegu9ejniMGccyJ");
  private static final PublicKey RESERVE_Y =
      PublicKey.fromBase58Encoded("7KFGP56m9DuHnceK2ajaCrFYdrM4z533yYCoAuG4TYg1");

  @Test
  void lbPairPDA() {
    assertEquals(
        LB_PAIR.toBase58(),
        MeteoraPDAs.lbPairPDA(TOKEN_X_MINT, TOKEN_Y_MINT, BIN_STEP, BASE_FACTOR, DLMM_PROGRAM).publicKey().toBase58()
    );
    // Seed ordering is min/max by key, so passing the mints in the opposite
    // order must derive the same address.
    assertEquals(
        LB_PAIR.toBase58(),
        MeteoraPDAs.lbPairPDA(TOKEN_Y_MINT, TOKEN_X_MINT, BIN_STEP, BASE_FACTOR, DLMM_PROGRAM).publicKey().toBase58()
    );
  }

  @Test
  void reservePDA() {
    assertEquals(
        RESERVE_X.toBase58(),
        MeteoraPDAs.reservePDA(LB_PAIR, TOKEN_X_MINT, DLMM_PROGRAM).publicKey().toBase58()
    );
    assertEquals(
        RESERVE_Y.toBase58(),
        MeteoraPDAs.reservePDA(LB_PAIR, TOKEN_Y_MINT, DLMM_PROGRAM).publicKey().toBase58()
    );
  }
}
