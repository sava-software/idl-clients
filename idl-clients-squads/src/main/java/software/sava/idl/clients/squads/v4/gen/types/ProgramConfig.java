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

/// Global program configuration account.
///
/// @param authority The authority which can update the config.
/// @param multisigCreationFee The lamports amount charged for creating a new multisig account.
///                            This fee is sent to the `treasury` account.
/// @param treasury The treasury account to send charged fees to.
/// @param reserved Reserved for future use.
public record ProgramConfig(PublicKey _address,
                            Discriminator discriminator,
                            PublicKey authority,
                            long multisigCreationFee,
                            PublicKey treasury,
                            byte[] reserved) implements SerDe {

  public static final int BYTES = 144;
  public static final int RESERVED_LEN = 64;
  public static final Filter SIZE_FILTER = Filter.createDataSizeFilter(BYTES);

  public static final Discriminator DISCRIMINATOR = toDiscriminator(196, 210, 90, 231, 144, 149, 140, 63);
  public static final Filter DISCRIMINATOR_FILTER = Filter.createMemCompFilter(0, DISCRIMINATOR.data());

  public static final int AUTHORITY_OFFSET = 8;
  public static final int MULTISIG_CREATION_FEE_OFFSET = 40;
  public static final int TREASURY_OFFSET = 48;
  public static final int RESERVED_OFFSET = 80;

  public static Filter createAuthorityFilter(final PublicKey authority) {
    return Filter.createMemCompFilter(AUTHORITY_OFFSET, authority);
  }

  public static Filter createMultisigCreationFeeFilter(final long multisigCreationFee) {
    final byte[] _data = new byte[8];
    putInt64LE(_data, 0, multisigCreationFee);
    return Filter.createMemCompFilter(MULTISIG_CREATION_FEE_OFFSET, _data);
  }

  public static Filter createTreasuryFilter(final PublicKey treasury) {
    return Filter.createMemCompFilter(TREASURY_OFFSET, treasury);
  }

  public static ProgramConfig read(final byte[] _data, final int _offset) {
    return read(null, _data, _offset);
  }

  public static ProgramConfig read(final AccountInfo<byte[]> accountInfo) {
    return read(accountInfo.pubKey(), accountInfo.data(), 0);
  }

  public static ProgramConfig read(final PublicKey _address, final byte[] _data) {
    return read(_address, _data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ProgramConfig> FACTORY = ProgramConfig::read;

  public static ProgramConfig read(final PublicKey _address, final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    final var discriminator = createAnchorDiscriminator(_data, _offset);
    int i = _offset + discriminator.length();
    final var authority = readPubKey(_data, i);
    i += 32;
    final var multisigCreationFee = getInt64LE(_data, i);
    i += 8;
    final var treasury = readPubKey(_data, i);
    i += 32;
    final var reserved = new byte[64];
    SerDeUtil.readArray(reserved, _data, i);
    return new ProgramConfig(_address,
                             discriminator,
                             authority,
                             multisigCreationFee,
                             treasury,
                             reserved);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset + discriminator.write(_data, _offset);
    authority.write(_data, i);
    i += 32;
    putInt64LE(_data, i, multisigCreationFee);
    i += 8;
    treasury.write(_data, i);
    i += 32;
    i += SerDeUtil.writeArrayChecked(reserved, 64, _data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
