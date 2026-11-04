package software.sava.idl.clients.jupiter.offerbook;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookPDAs;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;

import java.util.List;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static software.sava.core.encoding.ByteUtil.putInt64LE;

/// Jupiter Offerbook addresses whose published seeds do not derive them, so the generated
/// [OfferbookPDAs] has no helper for them.
///
/// Offerbook is closed source. Its repository, `jup-ag/offerbook-program`, is private (the
/// Guardian and Cantina audits that docs.jup.ag links name it), and no Jupiter SDK derives these
/// accounts. So each derivation here was established against mainnet, on the program deployed
/// at slot 456774686. It reproduces every `Offer` (3355) and `Loan` (5983) account read at slots
/// 464659048 to 464659826, address and stored bump, and the protocol fee token account in all
/// 187 decoded instructions that name one. `JupiterOfferbookPDAsTests` pins each derivation to
/// such accounts.
///
/// Where a seed reads a field of a zero-copy account, the IDL keeps only the account's name.
/// `offer` declares `["offer", signer, signer_user]` where the program reads the signer's
/// `offer_count`. `loan` declares `["loan", offer, offer]` where it reads the offer's
/// `fill_counter`. `protocol_fee_token_account` declares the associated token account of
/// `config` where the program uses that of `config.fee_authority`.
///
/// The same loss leaves `user` and `yield_market` declared, in one instruction each, as derived
/// from their own addresses, so those declarations get no helper either. Derive a user with
/// `signerUserPDA` from its owner, and a yield market with `yieldMarketPDA` from its mint. The
/// other generated helpers reproduce live accounts. Those deriving an associated token account
/// (the escrows and the JL token accounts) take the associated token program as
/// `associatedTokenAccountProgram`, not Offerbook's id.
public final class JupiterOfferbookPDAs {

  /// The offer a creator's `create_*_offer` initializes: `["offer", creator, offerIndex]`, with
  /// the index as a little-endian `u64`. The index is the creator's `User.offerCount` before the
  /// instruction increments it, so a wallet's first offer is index 0. An offer does not store
  /// its index, so this serves building a create, not finding an existing offer; read those
  /// with the `Offer` filters instead.
  public static ProgramDerivedAddress offerPDA(final PublicKey program,
                                               final PublicKey creator,
                                               final long offerIndex) {
    return PublicKey.findProgramAddress(
        List.of("offer".getBytes(US_ASCII), creator.toByteArray(), u64(offerIndex)),
        program
    );
  }

  /// The loan a `fill_*` creates: `["loan", offer, fillIndex]`, with the index as a
  /// little-endian `u64`. The index is the offer's `Offer.fillCounter` before the fill
  /// increments it, and the loan records it as `Loan.fillIndex`.
  public static ProgramDerivedAddress loanPDA(final PublicKey program,
                                              final PublicKey offer,
                                              final long fillIndex) {
    return PublicKey.findProgramAddress(
        List.of("loan".getBytes(US_ASCII), offer.toByteArray(), u64(fillIndex)),
        program
    );
  }

  /// The protocol fee token account for a mint: the associated token account of the fee
  /// authority, `["fee"]`, which `Config.feeAuthority` records. The fills, the repays and
  /// `extend_loan` take the one for their principal mint, and `claim_token_loan` the one for its
  /// collateral mint.
  public static ProgramDerivedAddress protocolFeeTokenAccountPDA(final SolanaAccounts solanaAccounts,
                                                                 final PublicKey program,
                                                                 final PublicKey tokenProgram,
                                                                 final PublicKey mint) {
    return AssociatedTokenPDAs.associatedTokenPDA(
        solanaAccounts.associatedTokenAccountProgram(),
        OfferbookPDAs.feeAuthorityPDA(program).publicKey(),
        tokenProgram,
        mint
    );
  }

  private static byte[] u64(final long value) {
    final byte[] bytes = new byte[Long.BYTES];
    putInt64LE(bytes, 0, value);
    return bytes;
  }

  private JupiterOfferbookPDAs() {
  }
}
