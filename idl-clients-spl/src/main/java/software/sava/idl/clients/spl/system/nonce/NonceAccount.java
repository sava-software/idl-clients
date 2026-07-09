package software.sava.idl.clients.spl.system.nonce;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.encoding.ByteUtil;
import software.sava.core.rpc.Filter;
import software.sava.core.tx.Instruction;
import software.sava.core.tx.Transaction;
import software.sava.idl.clients.spl.system.gen.SystemProgram;
import software.sava.rpc.json.http.response.AccountInfo;

import java.util.function.BiFunction;

import static software.sava.core.accounts.PublicKey.PUBLIC_KEY_LENGTH;
import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.rpc.Filter.createDataSizeFilter;
import static software.sava.core.rpc.Filter.createMemCompFilter;

public record NonceAccount(PublicKey address,
                           int version,
                           State state,
                           PublicKey authority,
                           byte[] nonce,
                           long lamportsPerSignature) {

  public enum State {
    Uninitialized,
    Initialized,
  }

  public static final int BYTES = 80;
  public static final Filter DATA_SIZE_FILTER = createDataSizeFilter(BYTES);

  public static final int VERSION_OFFSET = 0;
  public static final int STATE_OFFSET = VERSION_OFFSET + Integer.BYTES;
  public static final int AUTHORITY_OFFSET = STATE_OFFSET + Integer.BYTES;
  public static final int NONCE_OFFSET = AUTHORITY_OFFSET + PUBLIC_KEY_LENGTH;
  public static final int LAMPORTS_PER_SIG_OFFSET = NONCE_OFFSET + Transaction.BLOCK_HASH_LENGTH;

  public static Filter createVersionFilter(final int version) {
    final byte[] versionBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(versionBytes, 0, version);
    return createMemCompFilter(VERSION_OFFSET, versionBytes);
  }

  public static Filter createStateFilter(final State state) {
    final byte[] stateBytes = new byte[Integer.BYTES];
    ByteUtil.putInt32LE(stateBytes, 0, state.ordinal());
    return createMemCompFilter(STATE_OFFSET, stateBytes);
  }

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createNonceFilter(final byte[] nonce) {
    return createMemCompFilter(NONCE_OFFSET, nonce);
  }

  public static Filter createVersionFilter(final long lamportsPerSignature) {
    final byte[] bytes = new byte[Long.BYTES];
    ByteUtil.putInt64LE(bytes, 0, lamportsPerSignature);
    return createMemCompFilter(VERSION_OFFSET, bytes);
  }

  public static NonceAccount read(final byte[] data, int offset) {
    return read(null, data, offset);
  }

  public static NonceAccount read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static NonceAccount read(final PublicKey address, final byte[] data) {
    return read(address, data, 0);
  }

  public static final BiFunction<PublicKey, byte[], NonceAccount> FACTORY = NonceAccount::read;

  public static NonceAccount read(final PublicKey address, final byte[] data, int offset) {
    final int version = ByteUtil.getInt32LE(data, offset);
    offset += Integer.BYTES;
    final var state = State.values()[ByteUtil.getInt32LE(data, offset)];
    offset += Integer.BYTES;
    final var authority = readPubKey(data, offset);
    offset += PUBLIC_KEY_LENGTH;
    final byte[] nonce = new byte[Transaction.BLOCK_HASH_LENGTH];
    System.arraycopy(data, offset, nonce, 0, Transaction.BLOCK_HASH_LENGTH);
    offset += Transaction.BLOCK_HASH_LENGTH;
    final long lamportsPerSignature = getInt64LE(data, offset);
    return new NonceAccount(
        address,
        version,
        state,
        authority,
        nonce,
        lamportsPerSignature
    );
  }

  public Instruction advanceNonceAccount() {
    return advanceNonceAccount(SolanaAccounts.MAIN_NET);
  }

  public Instruction advanceNonceAccount(final SolanaAccounts solanaAccounts) {
    return SystemProgram.advanceNonceAccount(
        solanaAccounts.invokedSystemProgram(),
        solanaAccounts,
        address, authority
    );
  }

  public Transaction setNonce(final Transaction transaction) {
    return setNonce(SolanaAccounts.MAIN_NET, transaction);
  }

  public Transaction setNonce(final SolanaAccounts solanaAccounts, final Transaction transaction) {
    transaction.setRecentBlockHash(nonce);
    final var advanceNonceIx = advanceNonceAccount(solanaAccounts);
    return transaction.prependIx(advanceNonceIx);
  }
}
