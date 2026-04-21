package software.sava.idl.clients.loopscale.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.idl.clients.core.gen.SerDe;

import static software.sava.core.accounts.PublicKey.readPubKey;

public record Ledger(int status,
                     PublicKey strategy,
                     PublicKey principalMint,
                     PublicKey marketInformation,
                     PodU64 principalDue,
                     PodU64 principalRepaid,
                     PodU64 interestOutstanding,
                     PodU64 lastInterestUpdatedTime,
                     Duration duration,
                     PodDecimal interestPerSecond,
                     PodU64 startTime,
                     PodU64 endTime,
                     PodU64CBPS apy) implements SerDe {

  public static final int BYTES = 182;

  public static final int STATUS_OFFSET = 0;
  public static final int STRATEGY_OFFSET = 1;
  public static final int PRINCIPAL_MINT_OFFSET = 33;
  public static final int MARKET_INFORMATION_OFFSET = 65;
  public static final int PRINCIPAL_DUE_OFFSET = 97;
  public static final int PRINCIPAL_REPAID_OFFSET = 105;
  public static final int INTEREST_OUTSTANDING_OFFSET = 113;
  public static final int LAST_INTEREST_UPDATED_TIME_OFFSET = 121;
  public static final int DURATION_OFFSET = 129;
  public static final int INTEREST_PER_SECOND_OFFSET = 134;
  public static final int START_TIME_OFFSET = 158;
  public static final int END_TIME_OFFSET = 166;
  public static final int APY_OFFSET = 174;

  public static Ledger read(final byte[] _data, final int _offset) {
    if (_data == null || _data.length == 0) {
      return null;
    }
    int i = _offset;
    final var status = _data[i] & 0xFF;
    ++i;
    final var strategy = readPubKey(_data, i);
    i += 32;
    final var principalMint = readPubKey(_data, i);
    i += 32;
    final var marketInformation = readPubKey(_data, i);
    i += 32;
    final var principalDue = PodU64.read(_data, i);
    i += principalDue.l();
    final var principalRepaid = PodU64.read(_data, i);
    i += principalRepaid.l();
    final var interestOutstanding = PodU64.read(_data, i);
    i += interestOutstanding.l();
    final var lastInterestUpdatedTime = PodU64.read(_data, i);
    i += lastInterestUpdatedTime.l();
    final var duration = Duration.read(_data, i);
    i += duration.l();
    final var interestPerSecond = PodDecimal.read(_data, i);
    i += interestPerSecond.l();
    final var startTime = PodU64.read(_data, i);
    i += startTime.l();
    final var endTime = PodU64.read(_data, i);
    i += endTime.l();
    final var apy = PodU64CBPS.read(_data, i);
    return new Ledger(status,
                      strategy,
                      principalMint,
                      marketInformation,
                      principalDue,
                      principalRepaid,
                      interestOutstanding,
                      lastInterestUpdatedTime,
                      duration,
                      interestPerSecond,
                      startTime,
                      endTime,
                      apy);
  }

  @Override
  public int write(final byte[] _data, final int _offset) {
    int i = _offset;
    _data[i] = (byte) status;
    ++i;
    strategy.write(_data, i);
    i += 32;
    principalMint.write(_data, i);
    i += 32;
    marketInformation.write(_data, i);
    i += 32;
    i += principalDue.write(_data, i);
    i += principalRepaid.write(_data, i);
    i += interestOutstanding.write(_data, i);
    i += lastInterestUpdatedTime.write(_data, i);
    i += duration.write(_data, i);
    i += interestPerSecond.write(_data, i);
    i += startTime.write(_data, i);
    i += endTime.write(_data, i);
    i += apy.write(_data, i);
    return i - _offset;
  }

  @Override
  public int l() {
    return BYTES;
  }
}
