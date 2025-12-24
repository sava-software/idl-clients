package software.sava.idl.clients.jupiter.lend_borrow.gen.types;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public record LendingAdmin(PublicKey _address,
                           Discriminator discriminator,
                           PublicKey authority,
                           PublicKey liquidityProgram,
                           PublicKey rebalancer,
                           int nextLendingId,
                           PublicKey[] auths,
                           int bump) implements SerDe {

  public static final Discriminator DISCRIMINATOR = toDiscriminator(42, 8, 33, 220, 163, 40, 210, 5);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int LIQUIDITY_PROGRAM_OFFSET = 40;
  public static final int REBALANCER_OFFSET = 72;
  public static final int NEXT_LENDING_ID_OFFSET = 104;
  public static final int AUTHS_OFFSET = 106;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createLiquidityProgramFilter(final PublicKey liquidityProgram) {
    return Filter.createMemCompFilter(LIQUIDITY_PROGRAM_OFFSET, liquidityProgram);
  }

  public static Filter createRebalancerFilter(final PublicKey rebalancer) {
    return Filter.createMemCompFilter(REBALANCER_OFFSET, rebalancer);
  }

  public static Filter createNextLendingIdFilter(final int nextLendingId) {
    final byte[] _data = new byte[2];
    putInt16LE(_data, 0, nextLendingId);
    return Filter.createMemCompFilter(NEXT_LENDING_ID_OFFSET, _data);
  }

  public static LendingAdmin read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static LendingAdmin read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static LendingAdmin read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], LendingAdmin> FACTORY = LendingAdmin::read;

  public static LendingAdmin read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var liquidityProgram = readPubKey(_data, i);
    i += 32;
    final var rebalancer = readPubKey(_data, i);
    i += 32;
    final var nextLendingId = getInt16LE(_data, i);
    i += 2;
    final var auths = SerDeUtil.readPublicKeyVector(4, _data, i);
    i += SerDeUtil.lenVector(4, auths);
    final var bump = _data[i] & 0xFF;
    return new LendingAdmin(_address,
                            discriminator,
                            authority,
                            liquidityProgram,
                            rebalancer,
                            nextLendingId,
                            auths,
                            bump);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    liquidityProgram.write(_data, i);
    i += 32;
    rebalancer.write(_data, i);
    i += 32;
    putInt16LE(_data, i, nextLendingId);
    i += 2;
    i += SerDeUtil.writeVector(4, auths, _data, i);
    _data[i] = (byte) bump;
    ++i;
    return i - _offset;
  }

  @Override
  public int l() {
    return 8 + 32
         + 32
         + 32
         + 2
         + SerDeUtil.lenVector(4, auths)
         + 1;
  }
}
