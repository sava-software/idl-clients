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

/// Stores data required for tracking the voting and execution status of a vault transaction.
/// Vault transaction is a transaction that's executed on behalf of the multisig vault PDA
/// and wraps arbitrary Solana instructions, typically calling into other Solana programs.
///
/// @param multisig The multisig this belongs to.
/// @param creator Member of the Multisig who submitted the transaction.
/// @param index Index of this transaction within the multisig.
/// @param bump bump for the transaction seeds.
/// @param vaultIndex Index of the vault this transaction belongs to.
/// @param vaultBump Derivation bump of the vault PDA this transaction belongs to.
/// @param ephemeralSignerBumps Derivation bumps for additional signers.
///                             Some transactions require multiple signers. Often these additional signers are "ephemeral" keypairs
///                             that are generated on the client with a sole purpose of signing the transaction and be discarded immediately after.
///                             When wrapping such transactions into multisig ones, we replace these "ephemeral" signing keypairs
///                             with PDAs derived from the MultisigTransaction's `transaction_index` and controlled by the Multisig Program;
///                             during execution the program includes the seeds of these PDAs into the `invoke_signed` calls,
///                             thus "signing" on behalf of these PDAs.
/// @param message data required for executing the transaction.
public record VaultTransaction(PublicKey _address,
                               Discriminator discriminator,
                               PublicKey multisig,
                               PublicKey creator,
                               long index,
                               int bump,
                               int vaultIndex,
                               int vaultBump,
                               byte[] ephemeralSignerBumps,
                               VaultTransactionMessage message) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(168, 250, 162, 100, 81, 14, 162, 207);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int MULTISIG_OFFSET = 8;
  public static final int CREATOR_OFFSET = 40;
  public static final int INDEX_OFFSET = 72;
  public static final int BUMP_OFFSET = 80;
  public static final int VAULT_INDEX_OFFSET = 81;
  public static final int VAULT_BUMP_OFFSET = 82;
  public static final int EPHEMERAL_SIGNER_BUMPS_OFFSET = 83;

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

  public static VaultTransaction read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static VaultTransaction read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static VaultTransaction read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], VaultTransaction> FACTORY = VaultTransaction::read;

  public static VaultTransaction read(final PublicKey _address, final byte[] _data, final int _offset) {
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
    final var ephemeralSignerBumps = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, ephemeralSignerBumps);
    final var message = VaultTransactionMessage.read(_data, i);
    return new VaultTransaction(_address,
                                discriminator,
                                multisig,
                                creator,
                                index,
                                bump,
                                vaultIndex,
                                vaultBump,
                                ephemeralSignerBumps,
                                message);
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
    i += SerDeUtil.writeVector(4, ephemeralSignerBumps, _data, i);
    i += message.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 8
         + 1
         + 1
         + 1
         + SerDeUtil.lenVector(4, ephemeralSignerBumps)
         + message.l();
  }
}
