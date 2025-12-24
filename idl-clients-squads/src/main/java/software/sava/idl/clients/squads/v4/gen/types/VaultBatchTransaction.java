package software.sava.idl.clients.squads.v4.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// Stores data required for execution of one transaction from a batch.
///
/// @param bump PDA bump.
/// @param ephemeralSignerBumps Derivation bumps for additional signers.
///                             Some transactions require multiple signers. Often these additional signers are "ephemeral" keypairs
///                             that are generated on the client with a sole purpose of signing the transaction and be discarded immediately after.
///                             When wrapping such transactions into multisig ones, we replace these "ephemeral" signing keypairs
///                             with PDAs derived from the transaction's `transaction_index` and controlled by the Multisig Program;
///                             during execution the program includes the seeds of these PDAs into the `invoke_signed` calls,
///                             thus "signing" on behalf of these PDAs.
/// @param message data required for executing the transaction.
public record VaultBatchTransaction(PublicKey _address,
                                    Discriminator discriminator,
                                    int bump,
                                    byte[] ephemeralSignerBumps,
                                    VaultTransactionMessage message) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(196, 121, 46, 36, 12, 19, 252, 7);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int BUMP_OFFSET = 8;
  public static final int EPHEMERAL_SIGNER_BUMPS_OFFSET = 9;

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static VaultBatchTransaction read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static VaultBatchTransaction read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static VaultBatchTransaction read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], VaultBatchTransaction> FACTORY = VaultBatchTransaction::read;

  public static VaultBatchTransaction read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var bump = _data[i] & 0xFF;
    ++i;
    final var ephemeralSignerBumps = SerDeUtil.readbyteVector(4, _data, i);
    i += SerDeUtil.lenVector(4, ephemeralSignerBumps);
    final var message = VaultTransactionMessage.read(_data, i);
    return new VaultBatchTransaction(_address, discriminator, bump, ephemeralSignerBumps, message);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    _data[i] = (byte) bump;
    ++i;
    i += SerDeUtil.writeVector(4, ephemeralSignerBumps, _data, i);
    i += message.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 1 + SerDeUtil.lenVector(4, ephemeralSignerBumps) + message.l();
  }
}
