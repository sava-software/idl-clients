package software.sava.idl.clients.squads.v4.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// @param createKey Key that is used to seed the multisig PDA.
/// @param configAuthority The authority that can change the multisig config.
///                        This is a very important parameter as this authority can change the members and threshold.
///                        
///                        The convention is to set this to `Pubkey::default()`.
///                        In this case, the multisig becomes autonomous, so every config change goes through
///                        the normal process of voting by the members.
///                        
///                        However, if this parameter is set to any other key, all the config changes for this multisig
///                        will need to be signed by the `config_authority`. We call such a multisig a "controlled multisig".
/// @param threshold Threshold for signatures.
/// @param timeLock How many seconds must pass between transaction voting settlement and execution.
/// @param transactionIndex Last transaction index. 0 means no transactions have been created.
/// @param staleTransactionIndex Last stale transaction index. All transactions up until this index are stale.
///                              This index is updated when multisig config (members/threshold/time_lock) changes.
/// @param rentCollector The address where the rent for the accounts related to executed, rejected, or cancelled
///                      transactions can be reclaimed. If set to `None`, the rent reclamation feature is turned off.
/// @param bump Bump for the multisig PDA seed.
/// @param members Members of the multisig.
public record Multisig(PublicKey _address,
                       Discriminator discriminator,
                       PublicKey createKey,
                       PublicKey configAuthority,
                       int threshold,
                       int timeLock,
                       long transactionIndex,
                       long staleTransactionIndex,
                       PublicKey rentCollector,
                       int bump,
                       Member[] members) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(224, 116, 121, 186, 68, 161, 79, 236);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int CREATE_KEY_OFFSET = 8;
  public static final int CONFIG_AUTHORITY_OFFSET = 40;
  public static final int THRESHOLD_OFFSET = 72;
  public static final int TIME_LOCK_OFFSET = 74;
  public static final int TRANSACTION_INDEX_OFFSET = 78;
  public static final int STALE_TRANSACTION_INDEX_OFFSET = 86;
  public static final int RENT_COLLECTOR_OFFSET = 95;

  public static Filter createCreateKeyFilter(final PublicKey createKey) {
    return Filter.createMemCompFilter(CREATE_KEY_OFFSET, createKey);
  }

  public static Filter createConfigAuthorityFilter(final PublicKey configAuthority) {
    return Filter.createMemCompFilter(CONFIG_AUTHORITY_OFFSET, configAuthority);
  }

  public static Filter createThresholdFilter(final int threshold) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, threshold);
    return Filter.createMemCompFilter(THRESHOLD_OFFSET, _data);
  }

  public static Filter createTimeLockFilter(final int timeLock) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, timeLock);
    return Filter.createMemCompFilter(TIME_LOCK_OFFSET, _data);
  }

  public static Filter createTransactionIndexFilter(final long transactionIndex) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, transactionIndex);
    return Filter.createMemCompFilter(TRANSACTION_INDEX_OFFSET, _data);
  }

  public static Filter createStaleTransactionIndexFilter(final long staleTransactionIndex) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, staleTransactionIndex);
    return Filter.createMemCompFilter(STALE_TRANSACTION_INDEX_OFFSET, _data);
  }

  public static Filter createRentCollectorFilter(final PublicKey rentCollector) {
    final byte[] _data = new byte[33];
    _data[0] = 1;
    rentCollector.write(_data, 1);
    return Filter.createMemCompFilter(RENT_COLLECTOR_OFFSET, _data);
  }

  public static Multisig read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Multisig read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Multisig read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Multisig> FACTORY = Multisig::read;

  public static Multisig read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var createKey = readPubKey(_data, i);
    i += 32;
    final var configAuthority = readPubKey(_data, i);
    i += 32;
    final var threshold = getInt16LE(_data, i);
    i += 2;
    final var timeLock = getInt32LE(_data, i);
    i += 4;
    final var transactionIndex = getInt64LE(_data, i);
    i += 8;
    final var staleTransactionIndex = getInt64LE(_data, i);
    i += 8;
    final PublicKey rentCollector;
    if (_data[i] == 0) {
      rentCollector = null;
      ++i;
    } else {
      ++i;
      rentCollector = readPubKey(_data, i);
      i += 32;
    }
    final var bump = _data[i] & 0xFF;
    ++i;
    final var members = SerDeUtil.readVector(4, Member.class, Member::read, _data, i);
    return new Multisig(_address,
                        discriminator,
                        createKey,
                        configAuthority,
                        threshold,
                        timeLock,
                        transactionIndex,
                        staleTransactionIndex,
                        rentCollector,
                        bump,
                        members);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    createKey.write(_data, i);
    i += 32;
    configAuthority.write(_data, i);
    i += 32;
    putInt16LE(_data, i, threshold);
    i += 2;
    putInt32LE(_data, i, timeLock);
    i += 4;
    putInt64LE(_data, i, transactionIndex);
    i += 8;
    putInt64LE(_data, i, staleTransactionIndex);
    i += 8;
    i += SerDeUtil.writeOptional(1, rentCollector, _data, i);
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeVector(4, members, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 2
         + 4
         + 8
         + 8
         + (rentCollector == null ? 1 : (1 + 32))
         + 1
         + SerDeUtil.lenVector(4, members);
  }
}
