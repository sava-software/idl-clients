package software.sava.idl.clients.jupiter.merkle_distributor.gen.types;

import java.math.BigInteger;

import java.util.function.BiFunction;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.rpc.Filter;
import software.sava.rpc.json.http.response.AccountInfo;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

/// State for the account which distributes tokens.
///
/// @param root The 256-bit merkle root.
/// @param mint Mint of the token to be distributed.
/// @param base base key of distributor.
/// @param tokenVault Token Address of the vault
/// @param clawbackReceiver Clawback receiver
/// @param admin Admin wallet
/// @param locker locker, for claim type claim and stake
/// @param operator operator for signing in permissioned merkle tree
/// @param version Version of the airdrop
/// @param maxTotalClaim Maximum number of tokens that can ever be claimed from this MerkleDistributor.
/// @param maxNumNodes Maximum number of nodes in MerkleDistributor.
/// @param totalAmountClaimed Total amount of tokens that have been claimed.
/// @param numNodesClaimed Number of nodes that have been claimed.
/// @param startTs Lockup time start (Unix Timestamp)
/// @param endTs Lockup time end (Unix Timestamp)
/// @param clawbackStartTs Clawback start (Unix Timestamp)
/// @param activationPoint this merkle tree is activated from this slot or timestamp
/// @param activationType activation type, 0 means slot, 1 means timestamp
/// @param claimType claim type
/// @param bump Bump seed.
/// @param clawedBack Whether or not the distributor has been clawed back
/// @param closable indicate that whether admin can close this pool, for testing purpose
/// @param padding0 Padding 0
public record MerkleDistributor(PublicKey _address,
                                Discriminator discriminator,
                                byte[] root,
                                PublicKey mint,
                                PublicKey base,
                                PublicKey tokenVault,
                                PublicKey clawbackReceiver,
                                PublicKey admin,
                                PublicKey locker,
                                PublicKey operator,
                                long version,
                                long maxTotalClaim,
                                long maxNumNodes,
                                long totalAmountClaimed,
                                long numNodesClaimed,
                                long startTs,
                                long endTs,
                                long clawbackStartTs,
                                long activationPoint,
                                int activationType,
                                int claimType,
                                int bump,
                                int clawedBack,
                                int closable,
                                byte[] padding0,
                                AirdropBonus airdropBonus,
                                BigInteger[] padding2) implements Borsh {

  public static final int BYTES = 448;
  public static final int ROOT_LEN = 32;
  public static final int PADDING_0_LEN = 3;
  public static final int PADDING_2_LEN = 5;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(77, 119, 139, 70, 84, 247, 12, 26);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int ROOT_OFFSET = 8;
  public static final int MINT_OFFSET = 40;
  public static final int BASE_OFFSET = 72;
  public static final int TOKEN_VAULT_OFFSET = 104;
  public static final int CLAWBACK_RECEIVER_OFFSET = 136;
  public static final int ADMIN_OFFSET = 168;
  public static final int LOCKER_OFFSET = 200;
  public static final int OPERATOR_OFFSET = 232;
  public static final int VERSION_OFFSET = 264;
  public static final int MAX_TOTAL_CLAIM_OFFSET = 272;
  public static final int MAX_NUM_NODES_OFFSET = 280;
  public static final int TOTAL_AMOUNT_CLAIMED_OFFSET = 288;
  public static final int NUM_NODES_CLAIMED_OFFSET = 296;
  public static final int START_TS_OFFSET = 304;
  public static final int END_TS_OFFSET = 312;
  public static final int CLAWBACK_START_TS_OFFSET = 320;
  public static final int ACTIVATION_POINT_OFFSET = 328;
  public static final int ACTIVATION_TYPE_OFFSET = 336;
  public static final int CLAIM_TYPE_OFFSET = 337;
  public static final int BUMP_OFFSET = 338;
  public static final int CLAWED_BACK_OFFSET = 339;
  public static final int CLOSABLE_OFFSET = 340;
  public static final int PADDING_0_OFFSET = 341;
  public static final int AIRDROP_BONUS_OFFSET = 344;
  public static final int PADDING_2_OFFSET = 368;

  public static Filter createMintFilter(final PublicKey mint) {
    return Filter.createMemCompFilter(MINT_OFFSET, mint);
  }

  public static Filter createBaseFilter(final PublicKey base) {
    return Filter.createMemCompFilter(BASE_OFFSET, base);
  }

  public static Filter createTokenVaultFilter(final PublicKey tokenVault) {
    return Filter.createMemCompFilter(TOKEN_VAULT_OFFSET, tokenVault);
  }

  public static Filter createClawbackReceiverFilter(final PublicKey clawbackReceiver) {
    return Filter.createMemCompFilter(CLAWBACK_RECEIVER_OFFSET, clawbackReceiver);
  }

  public static Filter createAdminFilter(final PublicKey admin) {
    return Filter.createMemCompFilter(ADMIN_OFFSET, admin);
  }

  public static Filter createLockerFilter(final PublicKey locker) {
    return Filter.createMemCompFilter(LOCKER_OFFSET, locker);
  }

  public static Filter createOperatorFilter(final PublicKey operator) {
    return Filter.createMemCompFilter(OPERATOR_OFFSET, operator);
  }

  public static Filter createVersionFilter(final long version) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, version);
    return Filter.createMemCompFilter(VERSION_OFFSET, _data);
  }

  public static Filter createMaxTotalClaimFilter(final long maxTotalClaim) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, maxTotalClaim);
    return Filter.createMemCompFilter(MAX_TOTAL_CLAIM_OFFSET, _data);
  }

  public static Filter createMaxNumNodesFilter(final long maxNumNodes) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, maxNumNodes);
    return Filter.createMemCompFilter(MAX_NUM_NODES_OFFSET, _data);
  }

  public static Filter createTotalAmountClaimedFilter(final long totalAmountClaimed) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, totalAmountClaimed);
    return Filter.createMemCompFilter(TOTAL_AMOUNT_CLAIMED_OFFSET, _data);
  }

  public static Filter createNumNodesClaimedFilter(final long numNodesClaimed) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, numNodesClaimed);
    return Filter.createMemCompFilter(NUM_NODES_CLAIMED_OFFSET, _data);
  }

  public static Filter createStartTsFilter(final long startTs) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, startTs);
    return Filter.createMemCompFilter(START_TS_OFFSET, _data);
  }

  public static Filter createEndTsFilter(final long endTs) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, endTs);
    return Filter.createMemCompFilter(END_TS_OFFSET, _data);
  }

  public static Filter createClawbackStartTsFilter(final long clawbackStartTs) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, clawbackStartTs);
    return Filter.createMemCompFilter(CLAWBACK_START_TS_OFFSET, _data);
  }

  public static Filter createActivationPointFilter(final long activationPoint) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, activationPoint);
    return Filter.createMemCompFilter(ACTIVATION_POINT_OFFSET, _data);
  }

  public static Filter createActivationTypeFilter(final int activationType) {
    return Filter.createMemCompFilter(ACTIVATION_TYPE_OFFSET, new byte[]{(byte) activationType});
  }

  public static Filter createClaimTypeFilter(final int claimType) {
    return Filter.createMemCompFilter(CLAIM_TYPE_OFFSET, new byte[]{(byte) claimType});
  }

  public static Filter createBumpFilter(final int bump) {
    return Filter.createMemCompFilter(BUMP_OFFSET, new byte[]{(byte) bump});
  }

  public static Filter createClawedBackFilter(final int clawedBack) {
    return Filter.createMemCompFilter(CLAWED_BACK_OFFSET, new byte[]{(byte) clawedBack});
  }

  public static Filter createClosableFilter(final int closable) {
    return Filter.createMemCompFilter(CLOSABLE_OFFSET, new byte[]{(byte) closable});
  }

  public static Filter createAirdropBonusFilter(final AirdropBonus airdropBonus) {
    return Filter.createMemCompFilter(AIRDROP_BONUS_OFFSET, airdropBonus.write());
  }

  public static MerkleDistributor read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static MerkleDistributor read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static MerkleDistributor read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], MerkleDistributor> FACTORY = MerkleDistributor::read;

  public static MerkleDistributor read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var root = new byte[32];
    i += Borsh.readArray(root, _data, i);
    final var mint = readPubKey(_data, i);
    i += 32;
    final var base = readPubKey(_data, i);
    i += 32;
    final var tokenVault = readPubKey(_data, i);
    i += 32;
    final var clawbackReceiver = readPubKey(_data, i);
    i += 32;
    final var admin = readPubKey(_data, i);
    i += 32;
    final var locker = readPubKey(_data, i);
    i += 32;
    final var operator = readPubKey(_data, i);
    i += 32;
    final var version = getInt64LE(_data, i);
    i += 8;
    final var maxTotalClaim = getInt64LE(_data, i);
    i += 8;
    final var maxNumNodes = getInt64LE(_data, i);
    i += 8;
    final var totalAmountClaimed = getInt64LE(_data, i);
    i += 8;
    final var numNodesClaimed = getInt64LE(_data, i);
    i += 8;
    final var startTs = getInt64LE(_data, i);
    i += 8;
    final var endTs = getInt64LE(_data, i);
    i += 8;
    final var clawbackStartTs = getInt64LE(_data, i);
    i += 8;
    final var activationPoint = getInt64LE(_data, i);
    i += 8;
    final var activationType = _data[i] & 0xFF;
    ++i;
    final var claimType = _data[i] & 0xFF;
    ++i;
    final var bump = _data[i] & 0xFF;
    ++i;
    final var clawedBack = _data[i] & 0xFF;
    ++i;
    final var closable = _data[i] & 0xFF;
    ++i;
    final var padding0 = new byte[3];
    i += Borsh.readArray(padding0, _data, i);
    final var airdropBonus = AirdropBonus.read(_data, i);
    i += Borsh.len(airdropBonus);
    final var padding2 = new BigInteger[5];
    Borsh.read128Array(padding2, _data, i);
    return new MerkleDistributor(_address,
                                 discriminator,
                                 root,
                                 mint,
                                 base,
                                 tokenVault,
                                 clawbackReceiver,
                                 admin,
                                 locker,
                                 operator,
                                 version,
                                 maxTotalClaim,
                                 maxNumNodes,
                                 totalAmountClaimed,
                                 numNodesClaimed,
                                 startTs,
                                 endTs,
                                 clawbackStartTs,
                                 activationPoint,
                                 activationType,
                                 claimType,
                                 bump,
                                 clawedBack,
                                 closable,
                                 padding0,
                                 airdropBonus,
                                 padding2);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    i += Borsh.writeArrayChecked(root, 32, _data, i);
    mint.write(_data, i);
    i += 32;
    base.write(_data, i);
    i += 32;
    tokenVault.write(_data, i);
    i += 32;
    clawbackReceiver.write(_data, i);
    i += 32;
    admin.write(_data, i);
    i += 32;
    locker.write(_data, i);
    i += 32;
    operator.write(_data, i);
    i += 32;
    putInt64LE(_data, i, version);
    i += 8;
    putInt64LE(_data, i, maxTotalClaim);
    i += 8;
    putInt64LE(_data, i, maxNumNodes);
    i += 8;
    putInt64LE(_data, i, totalAmountClaimed);
    i += 8;
    putInt64LE(_data, i, numNodesClaimed);
    i += 8;
    putInt64LE(_data, i, startTs);
    i += 8;
    putInt64LE(_data, i, endTs);
    i += 8;
    putInt64LE(_data, i, clawbackStartTs);
    i += 8;
    putInt64LE(_data, i, activationPoint);
    i += 8;
    _data[i] = (byte) activationType;
    ++i;
    _data[i] = (byte) claimType;
    ++i;
    _data[i] = (byte) bump;
    ++i;
    _data[i] = (byte) clawedBack;
    ++i;
    _data[i] = (byte) closable;
    ++i;
    i += Borsh.writeArrayChecked(padding0, 3, _data, i);
    i += Borsh.write(airdropBonus, _data, i);
    i += Borsh.write128ArrayChecked(padding2, 5, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
