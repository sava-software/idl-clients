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

/// Stores data required for execution of a multisig configuration transaction.
/// Config transaction can perform a predefined set of actions on the Multisig PDA, such as adding/removing members,
/// changing the threshold, etc.
///
/// @param multisig The multisig this belongs to.
/// @param creator Member of the Multisig who submitted the transaction.
/// @param index Index of this transaction within the multisig.
/// @param bump bump for the transaction seeds.
/// @param actions Action to be performed on the multisig.
public record ConfigTransaction(PublicKey _address,
                                Discriminator discriminator,
                                PublicKey multisig,
                                PublicKey creator,
                                long index,
                                int bump,
                                ConfigAction[] actions) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(94, 8, 4, 35, 113, 139, 139, 112);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MULTISIG_OFFSET = 8;
  public static final int CREATOR_OFFSET = 40;
  public static final int INDEX_OFFSET = 72;
  public static final int BUMP_OFFSET = 80;
  public static final int ACTIONS_OFFSET = 81;

  public static Filter createMultisigFilter(final PublicKey multisig) {
    return Filter.createMemCompFilter(MULTISIG_OFFSET, multisig);
  }

  public static Filter createCreatorFilter(final PublicKey creator) {
    return Filter.createMemCompFilter(CREATOR_OFFSET, creator);
  }

  public static Filter createIndexFilter(final long index) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, index);
    return Filter.createMemCompFilter(INDEX_OFFSET, _data);
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static ConfigTransaction read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ConfigTransaction read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ConfigTransaction read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ConfigTransaction> FACTORY = ConfigTransaction::read;

  public static ConfigTransaction read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var multisig = readPubKey(_data, i);
    i += 32;
    final var creator = readPubKey(_data, i);
    i += 32;
    final var index = getInt64LE(_data, i);
    i += 8;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var actions = SerDeUtil.readVector(4, ConfigAction.class, ConfigAction::read, _data, i);
    return new ConfigTransaction(_address,
                                 discriminator,
                                 multisig,
                                 creator,
                                 index,
                                 bump,
                                 actions);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    multisig.write(_data, i);
    i += 32;
    creator.write(_data, i);
    i += 32;
    putInt64LE(_data, i, index);
    i += 8;
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeVector(4, actions, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 8
         + 1
         + SerDeUtil.lenVector(4, actions);
  }
}
