package software.sava.idl.clients.marinade.stake_pool;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.lookup.AccountIndexLookupTableEntry;
import software.sava.idl.clients.marinade.stake_pool.gen.types.ValidatorRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public record MarinadeValidatorList(PublicKey address,
                                    List<ValidatorRecord> validators,
                                    AccountIndexLookupTableEntry[] reverseLookupTable) {

  public int validatorIndex(final PublicKey validator) {
    return AccountIndexLookupTableEntry.lookupAccountIndex(reverseLookupTable, validator);
  }

  public ValidatorRecord validatorRecord(final int index) {
    return validators.get(index);
  }

  public ValidatorRecord validatorRecord(final PublicKey validator) {
    final int index = validatorIndex(validator);
    return index < 0 ? null : validators.get(index);
  }

  /// The list account is an 8-byte "validatr" magic followed by fixed-size slots; each slot
  /// carries an 8-byte frame ahead of a {@link ValidatorRecord}.
  static final int MAGIC_LEN = 8;
  static final int ITEM_SIZE = MAGIC_LEN + ValidatorRecord.BYTES;

  /// Parses exactly {@code count} validators. The list is compacted with swap-removes, so
  /// slots past {@code count} hold stale but non-zero records — the authoritative length
  /// lives in the State's `validator_system.validator_list.count`, never in this account.
  /// Prefer this over {@link #FACTORY} whenever the State is available (it always is when
  /// resolving the list account's address).
  public static MarinadeValidatorList read(final PublicKey address, final byte[] data, final int count) {
    final var validators = new ArrayList<ValidatorRecord>(count);
    for (int s = 0, offset = MAGIC_LEN; s < count; ++s, offset += ITEM_SIZE) {
      validators.add(ValidatorRecord.read(data, offset));
    }
    return new MarinadeValidatorList(address, validators, reverseLookup(validators));
  }

  private static AccountIndexLookupTableEntry[] reverseLookup(final List<ValidatorRecord> validators) {
    final var reverseLookupTable = new AccountIndexLookupTableEntry[validators.size()];
    int i = 0;
    for (final var validatorRecord : validators) {
      reverseLookupTable[i] = new AccountIndexLookupTableEntry(validatorRecord.validatorAccount().toByteArray(), i);
      ++i;
    }
    Arrays.sort(reverseLookupTable);
    return reverseLookupTable;
  }

  /// Count-blind fallback that stops at the first zeroed slot. Unreliable on a compacted
  /// list — Marinade does not zero vacated tail slots, so this over-reads stale records.
  /// Use {@link #read(PublicKey, byte[], int)} with the State's count instead.
  @Deprecated
  public static final BiFunction<PublicKey, byte[], MarinadeValidatorList> FACTORY = (publicKey, data) -> {
    final var validators = new ArrayList<ValidatorRecord>(4_096);
    final int to = data.length - ITEM_SIZE;
    for (int offset = 0; offset <= to; ) {
      offset += MAGIC_LEN;
      final var validatorRecord = ValidatorRecord.read(data, offset);
      if (validatorRecord.validatorAccount().equals(PublicKey.NONE)) {
        break;
      } else {
        offset += ValidatorRecord.BYTES;
        validators.add(validatorRecord);
      }
    }
    return new MarinadeValidatorList(publicKey, validators, reverseLookup(validators));
  };
}
