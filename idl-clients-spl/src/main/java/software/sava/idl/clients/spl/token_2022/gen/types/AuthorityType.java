package software.sava.idl.clients.spl.token_2022.gen.types;

import software.sava.idl.clients.core.gen.SerDe;

public enum AuthorityType implements SerDe {

  mintTokens,
  freezeAccount,
  accountOwner,
  closeAccount,
  transferFeeConfig,
  withheldWithdraw,
  closeMint,
  interestRate,
  permanentDelegate,
  confidentialTransferMint,
  transferHookProgramId,
  confidentialTransferFeeConfig,
  metadataPointer,
  groupPointer,
  groupMemberPointer,
  scaledUiAmount,
  pause,
  permissionedBurn;

  public static AuthorityType read(final byte[] _data, final int _offset) {
    return AuthorityType.values()[_data[_offset] & 0xFF];
  }
  
  @Override
  public int write(final byte[] _data, final int _offset) {
    _data[_offset] = (byte) ordinal();
    return 1;
  }
  
  @Override
  public int l() {
    return 1;
  }
}