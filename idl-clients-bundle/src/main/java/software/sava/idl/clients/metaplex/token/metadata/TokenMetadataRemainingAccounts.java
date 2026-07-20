package software.sava.idl.clients.metaplex.token.metadata;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;

import java.util.List;

/// Helper for the "extra" accounts that Token Metadata instructions accept but
/// which the published IDL does not declare, so the generated
/// `TokenMetadataProgram` builders cannot emit them.
///
/// Token Metadata is a **Shank** program: its account order is declared as
/// indexed attributes on the instruction enum
/// (`token-metadata/program/src/instruction/mod.rs`) rather than as an Anchor
/// `#[derive(Accounts)]` struct. Diffing those attributes against the IDL shows
/// exactly one instruction where the two disagree.
///
/// ### `print` — the holder-delegate authority pair
///
/// The Rust declares 20 accounts; the IDL declares 18, omitting both trailing
/// **optional** accounts:
///
/// | Index | Account | Flags |
/// |---|---|---|
/// | 18 | `holder_delegate_record` | optional |
/// | 19 | `delegate` | optional, **signer** |
///
/// They are what lets a *holder delegate* — rather than the token holder
/// themselves — authorize printing an edition. Because the IDL omits them, the
/// generated positional builder has no parameters for them and a caller
/// restricted to it simply cannot exercise that authority path.
///
/// Append them with [#printHolderDelegate] to the instruction's account list.
/// Both are optional and must be supplied **together**: index 19 cannot be
/// reached without occupying index 18 first.
public final class TokenMetadataRemainingAccounts {

  /// The two optional trailing accounts of `print`, in program order.
  ///
  /// @param holderDelegateRecord the delegate record proving the delegation
  /// @param delegate             the delegate authorizing the print; signs
  public static List<AccountMeta> printHolderDelegate(final PublicKey holderDelegateRecord,
                                                      final PublicKey delegate) {
    return List.of(
        AccountMeta.createRead(holderDelegateRecord),
        AccountMeta.createReadOnlySigner(delegate)
    );
  }

  private TokenMetadataRemainingAccounts() {
  }
}
