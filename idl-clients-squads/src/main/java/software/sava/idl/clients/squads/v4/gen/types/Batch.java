package software.sava.idl.clients.squads.v4.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Stores data required for serial execution of a batch of multisig vault transactions.
/// Vault transaction is a transaction that's executed on behalf of the multisig vault PDA
/// and wraps arbitrary Solana instructions, typically calling into other Solana programs.
/// The transactions themselves are stored in separate PDAs associated with the this account.
///
/// @param multisig The multisig this belongs to.
/// @param creator Member of the Multisig who submitted the batch.
/// @param index Index of this batch within the multisig transactions.
/// @param bump PDA bump.
/// @param vaultIndex Index of the vault this batch belongs to.
/// @param vaultBump Derivation bump of the vault PDA this batch belongs to.
/// @param size Number of transactions in the batch.
/// @param executedTransactionIndex Index of the last executed transaction within the batch.
///                                 0 means that no transactions have been executed yet.
public record Batch(PublicKey _address,
                    Discriminator discriminator,
                    PublicKey multisig,
                    PublicKey creator,
                    long index,
                    int bump,
                    int vaultIndex,
                    int vaultBump,
                    int size,
                    int executedTransactionIndex) implements SerDe {

  public static final int BYTES = 91;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(156, 194, 70, 44, 22, 88, 137, 44);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MULTISIG_OFFSET = 8;
  public static final int CREATOR_OFFSET = 40;
  public static final int INDEX_OFFSET = 72;
  public static final int BUMP_OFFSET = 80;
  public static final int VAULT_INDEX_OFFSET = 81;
  public static final int VAULT_BUMP_OFFSET = 82;
  public static final int SIZE_OFFSET = 83;
  public static final int EXECUTED_TRANSACTION_INDEX_OFFSET = 87;

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

  public static Filter createVaultIndexFilter(final int vaultIndex) {
    return Filter.createMemCompFilter(VAULT_INDEX_OFFSET, new byte[]{(byte) vaultIndex});
  }

  public static Filter createVaultBumpFilter(final int vaultBump) {
    return Filter.createMemCompFilter(VAULT_BUMP_OFFSET, new byte[]{(byte) vaultBump});
  }

  public static Filter createSizeFilter(final int size) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, size);
    return Filter.createMemCompFilter(SIZE_OFFSET, _data);
  }

  public static Filter createExecutedTransactionIndexFilter(final int executedTransactionIndex) {
    final byte[] _data = new byte[4];
    putInt32LE(_data, 0, executedTransactionIndex);
    return Filter.createMemCompFilter(EXECUTED_TRANSACTION_INDEX_OFFSET, _data);
  }

  public static Batch read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static Batch read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static Batch read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], Batch> FACTORY = Batch::read;

  public static Batch read(final PublicKey _address, final byte[] _data, final int _offset) {
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
    final var vaultIndex = _data[i] & 0xFF;
    ++i;
    final var vaultBump = _data[i] & 0xFF;
    ++i;
    final var size = getInt32LE(_data, i);
    i += 4;
    final var executedTransactionIndex = getInt32LE(_data, i);
    return new Batch(_address,
                     discriminator,
                     multisig,
                     creator,
                     index,
                     bump,
                     vaultIndex,
                     vaultBump,
                     size,
                     executedTransactionIndex);
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
    _data[i] = (byte) vaultIndex;
    ++i;
    _data[i] = (byte) vaultBump;
    ++i;
    putInt32LE(_data, i, size);
    i += 4;
    putInt32LE(_data, i, executedTransactionIndex);
    i += 4;
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
