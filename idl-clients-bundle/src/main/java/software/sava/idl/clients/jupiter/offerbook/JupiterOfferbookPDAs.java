package software.sava.idl.clients.jupiter.offerbook;

import software.sava.core.accounts.ProgramDerivedAddress;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.idl.clients.jupiter.offerbook.gen.OfferbookPDAs;
import software.sava.idl.clients.spl.associated_token.gen.AssociatedTokenPDAs;

/// Jupiter Offerbook addresses the generated [OfferbookPDAs] cannot derive by itself: two whose
/// published seeds leave an encoding open, and one whose published seeds are wrong.
///
/// Offerbook is closed source. Its repository, `jup-ag/offerbook-program`, is private (the
/// Guardian and Cantina audits that docs.jup.ag links name it), and no Jupiter SDK derives these
/// accounts. So each derivation here was established against mainnet, on the program deployed
/// at slot 456774686. It reproduces every `Offer` (3355) and `Loan` (5983) account read at slots
/// 464659048 to 464659826, address and stored bump, and the protocol fee token account in all
/// 187 decoded instructions that name one. `JupiterOfferbookPDAsTests` pins each derivation to
/// such accounts.
///
/// The IDL has named an offer's and a loan's last seed, `signer_user.offer_count` and
/// `offer.fill_counter`, since Jupiter republished it at slot 473709366, and a published seed
/// does not say how a number is hashed. Both are little-endian `u64`s, which is what
/// [#offerPDA] and [#loanPDA] add to the generated helpers of the same names.
///
/// `protocol_fee_token_account` is still declared as the associated token account of `config`,
/// where the program uses that of `config.fee_authority`, so it has no generated helper.
public final class JupiterOfferbookPDAs {

  /// The offer a creator's `create_*_offer` initializes: `["offer", creator, offerIndex]`, with
  /// the index as a little-endian `u64`. The index is the creator's `User.offerCount` before the
  /// instruction increments it, so a wallet's first offer is index 0. An offer does not store
  /// its index, so this serves building a create, not finding an existing offer; read those
  /// with the `Offer` filters instead.
  public static ProgramDerivedAddress offerPDA(final PublicKey program,
                                               final PublicKey creator,
                                               final long offerIndex) {
    return OfferbookPDAs.offerPDA(program, creator, SerDeUtil.int64LESeed(offerIndex));
  }

  /// The loan a `fill_*` creates: `["loan", offer, fillIndex]`, with the index as a
  /// little-endian `u64`. The index is the offer's `Offer.fillCounter` before the fill
  /// increments it, and the loan records it as `Loan.fillIndex`.
  public static ProgramDerivedAddress loanPDA(final PublicKey program,
                                              final PublicKey offer,
                                              final long fillIndex) {
    return OfferbookPDAs.loanPDA(program, offer, SerDeUtil.int64LESeed(fillIndex));
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

  private JupiterOfferbookPDAs() {
  }
}
