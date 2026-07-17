package software.sava.idl.clients.spl.stakepool;

import software.sava.core.accounts.PublicKey;
import software.sava.core.encoding.ByteUtil;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.core.gen.SerDeUtil;

import java.util.function.BiFunction;

import static software.sava.core.encoding.ByteUtil.getInt32LE;

public record ValidatorList(PublicKey address,
                            AccountType accountType,
                            int maxValidators,
                            ValidatorStakeInfo[] validators) implements SerDe {

  public static ValidatorList read(final byte[] data, final int offset) {
    return read(null, data, offset);
  }

  public static ValidatorList read(final PublicKey publicKey, final byte[] data) {
    return read(publicKey, data, 0);
  }

  public static final BiFunction<PublicKey, byte[], ValidatorList> FACTORY = ValidatorList::read;

  public static ValidatorList read(final PublicKey publicKey, final byte[] data, int offset) {
    final var accountType = AccountType.values()[data[offset] & 0xFF];
    ++offset;
    final int maxValidators = getInt32LE(data, offset);
    offset += Integer.BYTES;
    final int numValidators = ByteUtil.getInt32LE(data, offset);
    offset += Integer.BYTES;
    final var validators = new ValidatorStakeInfo[numValidators];
    for (int i = 0; i < numValidators; ++i) {
      validators[i] = ValidatorStakeInfo.read(data, offset);
      offset += ValidatorStakeInfo.BYTES;
    }
    return new ValidatorList(
        publicKey,
        accountType,
        maxValidators,
        validators
    );
  }

  @Override
  public int write(final byte[] data, final int offset) {
    data[offset] = (byte) accountType.ordinal();
    int i = offset + 1;
    ByteUtil.putInt32LE(data, i, maxValidators);
    i += Integer.BYTES;
    ByteUtil.putInt32LE(data, i, validators.length);
    i += Integer.BYTES;
    i += SerDeUtil.writeArray(validators, data, i);
    return i - offset;
  }

  @Override
  public int l() {
    return 1 + Integer.BYTES + Integer.BYTES + (ValidatorStakeInfo.BYTES * validators.length);
  }
}
