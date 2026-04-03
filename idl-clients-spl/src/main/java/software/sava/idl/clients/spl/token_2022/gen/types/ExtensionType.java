package software.sava.idl.clients.spl.token_2022.gen.types;

import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;

/// Extensions that can be applied to mints or accounts.  Mint extensions must
/// only be applied to mint accounts, and account extensions must only be
/// applied to token holding accounts.
public enum ExtensionType implements SerDe {

  /// Used as padding if the account size would otherwise be 355, same as a multisig
  uninitialized,
  /// Includes transfer fee rate info and accompanying authorities to withdraw
  /// and set the fee
  transferFeeConfig,
  /// Includes withheld transfer fees
  transferFeeAmount,
  /// Includes an optional mint close authority
  mintCloseAuthority,
  /// Auditor configuration for confidential transfers
  confidentialTransferMint,
  /// State for confidential transfers
  confidentialTransferAccount,
  /// Specifies the default Account::state for new Accounts
  defaultAccountState,
  /// Indicates that the Account owner authority cannot be changed
  immutableOwner,
  /// Require inbound transfers to have memo
  memoTransfer,
  /// Indicates that the tokens from this mint can't be transferred
  nonTransferable,
  /// Tokens accrue interest over time,
  interestBearingConfig,
  /// Locks privileged token operations from happening via CPI
  cpiGuard,
  /// Includes an optional permanent delegate
  permanentDelegate,
  /// Indicates that the tokens in this account belong to a non-transferable
  /// mint
  nonTransferableAccount,
  /// Mint requires a CPI to a program implementing the "transfer hook"
  /// interface
  transferHook,
  /// Indicates that the tokens in this account belong to a mint with a
  /// transfer hook
  transferHookAccount,
  /// Includes encrypted withheld fees and the encryption public that they are
  /// encrypted under
  confidentialTransferFee,
  /// Includes confidential withheld transfer fees
  confidentialTransferFeeAmount,
  /// Tokens have a scaled UI amount
  scaledUiAmountConfig,
  /// Mint contains pausable configuration
  pausableConfig,
  /// Account contains pausable configuration
  pausableAccount,
  /// Mint contains a pointer to another account (or the same account) that
  /// holds metadata
  metadataPointer,
  /// Mint contains token-metadata
  tokenMetadata,
  /// Mint contains a pointer to another account (or the same account) that
  /// holds group configurations
  groupPointer,
  /// Mint contains token group configurations
  tokenGroup,
  /// Mint contains a pointer to another account (or the same account) that
  /// holds group member configurations
  groupMemberPointer,
  /// Mint contains token group member configurations
  tokenGroupMember;

  public static ExtensionType read(final byte[] _data, final int _offset) {
    return ExtensionType.values()[ByteUtil.getInt16LE(_data, _offset)];
  }
  
  @Override
  public int write(final byte[] _data, final int _offset) {
    ByteUtil.putInt16LE(_data, _offset, ordinal());
    return 2;
  }
  
  @Override
  public int l() {
    return 2;
  }
}