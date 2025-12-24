package software.sava.idl.clients.squads.v4.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Stores the data required for tracking the status of a multisig proposal.
/// Each `Proposal` has a 1:1 association with a transaction account, e.g. a `VaultTransaction` or a `ConfigTransaction`;
/// the latter can be executed only after the `Proposal` has been approved and its time lock is released.
///
/// @param multisig The multisig this belongs to.
/// @param transactionIndex Index of the multisig transaction this proposal is associated with.
/// @param status The status of the transaction.
/// @param bump PDA bump.
/// @param approved Keys that have approved/signed.
/// @param rejected Keys that have rejected.
/// @param cancelled Keys that have cancelled (Approved only).
public record Proposal(PublicKey _address,
                       Discriminator discriminator,
                       PublicKey multisig,
                       long transactionIndex,
                       ProposalStatus status,
                       int bump,
                       PublicKey[] approved,
                       PublicKey[] rejected,
                       PublicKey[] cancelled) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(26, 94, 189, 187, 116, 136, 53, 33);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MULTISIG_OFFSET = 8;
  public static final int TRANSACTION_INDEX_OFFSET = 40;
  public static final int STATUS_OFFSET = 48;

  public static Filter createMultisigFilter(final PublicKey multisig) {
    return Filter.createMemCompFilter(MULTISIG_OFFSET, multisig);
  }

  public static Filter createTransactionIndexFilter(final long transactionIndex) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, transactionIndex);
    return Filter.createMemCompFilter(TRANSACTION_INDEX_OFFSET, _data);
  }

  public static Filter createStatusFilter(final ProposalStatus status) {
    return Filter.createMemCompFilter(STATUS_OFFSET, status.write());
  }

  public static Proposal read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Proposal read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Proposal read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Proposal> FACTORY = Proposal::read;

  public static Proposal read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var multisig = readPubKey(_data, i);
    i += 32;
    final var transactionIndex = getInt64LE(_data, i);
    i += 8;
    final var status = ProposalStatus.read(_data, i);
    i += status.l();
    final var bump = _data[i] & 0xFF;
    ++i;
    final var approved = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, approved);
    final var rejected = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, rejected);
    final var cancelled = SerDeUtil.readPublicKeyVector(4, _data, i);
    return new Proposal(_address,
                        discriminator,
                        multisig,
                        transactionIndex,
                        status,
                        bump,
                        approved,
                        rejected,
                        cancelled);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    multisig.write(_data, i);
    i += 32;
    putInt64LE(_data, i, transactionIndex);
    i += 8;
    i += status.write(_data, i);
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeVector(4, approved, _data, i);
    i += SerDeUtil.writeVector(4, rejected, _data, i);
    i += SerDeUtil.writeVector(4, cancelled, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 8
         + status.l()
         + 1
         + SerDeUtil.lenVector(4, approved)
         + SerDeUtil.lenVector(4, rejected)
         + SerDeUtil.lenVector(4, cancelled);
  }
}
