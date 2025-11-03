package software.sava.idl.clients.drift.gen;

import java.math.BigInteger;

import java.util.List;
import java.util.OptionalInt;
import java.util.OptionalLong;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.drift.gen.types.AddAmmConstituentMappingDatum;
import software.sava.idl.clients.drift.gen.types.AssetTier;
import software.sava.idl.clients.drift.gen.types.ConstituentParams;
import software.sava.idl.clients.drift.gen.types.ContractTier;
import software.sava.idl.clients.drift.gen.types.FeeStructure;
import software.sava.idl.clients.drift.gen.types.IfRebalanceConfigParams;
import software.sava.idl.clients.drift.gen.types.LpPoolParams;
import software.sava.idl.clients.drift.gen.types.MarketStatus;
import software.sava.idl.clients.drift.gen.types.MarketType;
import software.sava.idl.clients.drift.gen.types.ModifyOrderParams;
import software.sava.idl.clients.drift.gen.types.OracleGuardRails;
import software.sava.idl.clients.drift.gen.types.OracleSource;
import software.sava.idl.clients.drift.gen.types.OrderParams;
import software.sava.idl.clients.drift.gen.types.OverrideAmmCacheParams;
import software.sava.idl.clients.drift.gen.types.PositionDirection;
import software.sava.idl.clients.drift.gen.types.PrelaunchOracleParams;
import software.sava.idl.clients.drift.gen.types.SettlePnlMode;
import software.sava.idl.clients.drift.gen.types.SpotFulfillmentConfigStatus;
import software.sava.idl.clients.drift.gen.types.SpotFulfillmentType;
import software.sava.idl.clients.drift.gen.types.SwapReduceOnly;
import software.sava.idl.clients.drift.gen.types.UpdatePerpMarketSummaryStatsParams;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt128LE;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt128LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class DriftProgram {

  public static final Discriminator INITIALIZE_USER_DISCRIMINATOR = toDiscriminator(111, 17, 185, 250, 60, 122, 38, 254);

  public static List<AccountMeta> initializeUserKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey userKey,
                                                     final PublicKey userStatsKey,
                                                     final PublicKey stateKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey payerKey,
                                                     final PublicKey rentKey,
                                                     final PublicKey systemProgramKey) {
    return List.of(
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(stateKey),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeUser(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey userKey,
                                           final PublicKey userStatsKey,
                                           final PublicKey stateKey,
                                           final PublicKey authorityKey,
                                           final PublicKey payerKey,
                                           final PublicKey rentKey,
                                           final PublicKey systemProgramKey,
                                           final int subAccountId,
                                           final byte[] name) {
    final var keys = initializeUserKeys(
      invokedDriftProgramMeta,
      userKey,
      userStatsKey,
      stateKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeUser(invokedDriftProgramMeta, keys, subAccountId, name);
  }

  public static Instruction initializeUser(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys,
                                           final int subAccountId,
                                           final byte[] name) {
    final byte[] _data = new byte[10 + Borsh.lenArray(name)];
    int i = INITIALIZE_USER_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    Borsh.writeArrayChecked(name, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeUserIxData(Discriminator discriminator, int subAccountId, byte[] name) implements Borsh {  

    public static InitializeUserIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 42;
    public static final int NAME_LEN = 32;

    public static InitializeUserIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var name = new byte[32];
      Borsh.readArray(name, _data, i);
      return new InitializeUserIxData(discriminator, subAccountId, name);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_USER_STATS_DISCRIMINATOR = toDiscriminator(254, 243, 72, 98, 251, 130, 168, 213);

  public static List<AccountMeta> initializeUserStatsKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey userStatsKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey rentKey,
                                                          final PublicKey systemProgramKey) {
    return List.of(
      createWrite(userStatsKey),
      createWrite(stateKey),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeUserStats(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey userStatsKey,
                                                final PublicKey stateKey,
                                                final PublicKey authorityKey,
                                                final PublicKey payerKey,
                                                final PublicKey rentKey,
                                                final PublicKey systemProgramKey) {
    final var keys = initializeUserStatsKeys(
      invokedDriftProgramMeta,
      userStatsKey,
      stateKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeUserStats(invokedDriftProgramMeta, keys);
  }

  public static Instruction initializeUserStats(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, INITIALIZE_USER_STATS_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_SIGNED_MSG_USER_ORDERS_DISCRIMINATOR = toDiscriminator(164, 99, 156, 126, 156, 57, 99, 180);

  public static List<AccountMeta> initializeSignedMsgUserOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey signedMsgUserOrdersKey,
                                                                    final PublicKey authorityKey,
                                                                    final PublicKey payerKey,
                                                                    final PublicKey rentKey,
                                                                    final PublicKey systemProgramKey) {
    return List.of(
      createWrite(signedMsgUserOrdersKey),
      createRead(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeSignedMsgUserOrders(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey signedMsgUserOrdersKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey payerKey,
                                                          final PublicKey rentKey,
                                                          final PublicKey systemProgramKey,
                                                          final int numOrders) {
    final var keys = initializeSignedMsgUserOrdersKeys(
      invokedDriftProgramMeta,
      signedMsgUserOrdersKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeSignedMsgUserOrders(invokedDriftProgramMeta, keys, numOrders);
  }

  public static Instruction initializeSignedMsgUserOrders(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int numOrders) {
    final byte[] _data = new byte[10];
    int i = INITIALIZE_SIGNED_MSG_USER_ORDERS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, numOrders);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeSignedMsgUserOrdersIxData(Discriminator discriminator, int numOrders) implements Borsh {  

    public static InitializeSignedMsgUserOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitializeSignedMsgUserOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var numOrders = getInt16LE(_data, i);
      return new InitializeSignedMsgUserOrdersIxData(discriminator, numOrders);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, numOrders);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RESIZE_SIGNED_MSG_USER_ORDERS_DISCRIMINATOR = toDiscriminator(137, 10, 87, 150, 18, 115, 79, 168);

  public static List<AccountMeta> resizeSignedMsgUserOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey signedMsgUserOrdersKey,
                                                                final PublicKey authorityKey,
                                                                final PublicKey userKey,
                                                                final PublicKey payerKey,
                                                                final PublicKey systemProgramKey) {
    return List.of(
      createWrite(signedMsgUserOrdersKey),
      createRead(authorityKey),
      createRead(userKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction resizeSignedMsgUserOrders(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey signedMsgUserOrdersKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey userKey,
                                                      final PublicKey payerKey,
                                                      final PublicKey systemProgramKey,
                                                      final int numOrders) {
    final var keys = resizeSignedMsgUserOrdersKeys(
      invokedDriftProgramMeta,
      signedMsgUserOrdersKey,
      authorityKey,
      userKey,
      payerKey,
      systemProgramKey
    );
    return resizeSignedMsgUserOrders(invokedDriftProgramMeta, keys, numOrders);
  }

  public static Instruction resizeSignedMsgUserOrders(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int numOrders) {
    final byte[] _data = new byte[10];
    int i = RESIZE_SIGNED_MSG_USER_ORDERS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, numOrders);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ResizeSignedMsgUserOrdersIxData(Discriminator discriminator, int numOrders) implements Borsh {  

    public static ResizeSignedMsgUserOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static ResizeSignedMsgUserOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var numOrders = getInt16LE(_data, i);
      return new ResizeSignedMsgUserOrdersIxData(discriminator, numOrders);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, numOrders);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_SIGNED_MSG_WS_DELEGATES_DISCRIMINATOR = toDiscriminator(40, 132, 96, 219, 184, 193, 80, 8);

  public static List<AccountMeta> initializeSignedMsgWsDelegatesKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey signedMsgWsDelegatesKey,
                                                                     final PublicKey authorityKey,
                                                                     final PublicKey rentKey,
                                                                     final PublicKey systemProgramKey) {
    return List.of(
      createWrite(signedMsgWsDelegatesKey),
      createWritableSigner(authorityKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeSignedMsgWsDelegates(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey signedMsgWsDelegatesKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey rentKey,
                                                           final PublicKey systemProgramKey,
                                                           final PublicKey[] delegates) {
    final var keys = initializeSignedMsgWsDelegatesKeys(
      invokedDriftProgramMeta,
      signedMsgWsDelegatesKey,
      authorityKey,
      rentKey,
      systemProgramKey
    );
    return initializeSignedMsgWsDelegates(invokedDriftProgramMeta, keys, delegates);
  }

  public static Instruction initializeSignedMsgWsDelegates(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final PublicKey[] delegates) {
    final byte[] _data = new byte[8 + Borsh.lenVector(delegates)];
    int i = INITIALIZE_SIGNED_MSG_WS_DELEGATES_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(delegates, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeSignedMsgWsDelegatesIxData(Discriminator discriminator, PublicKey[] delegates) implements Borsh {  

    public static InitializeSignedMsgWsDelegatesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeSignedMsgWsDelegatesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var delegates = Borsh.readPublicKeyVector(_data, i);
      return new InitializeSignedMsgWsDelegatesIxData(discriminator, delegates);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(delegates, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(delegates);
    }
  }

  public static final Discriminator CHANGE_SIGNED_MSG_WS_DELEGATE_STATUS_DISCRIMINATOR = toDiscriminator(252, 202, 252, 219, 179, 27, 84, 138);

  public static List<AccountMeta> changeSignedMsgWsDelegateStatusKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey signedMsgWsDelegatesKey,
                                                                      final PublicKey authorityKey,
                                                                      final PublicKey systemProgramKey) {
    return List.of(
      createWrite(signedMsgWsDelegatesKey),
      createWritableSigner(authorityKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction changeSignedMsgWsDelegateStatus(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey signedMsgWsDelegatesKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey delegate,
                                                            final boolean add) {
    final var keys = changeSignedMsgWsDelegateStatusKeys(
      invokedDriftProgramMeta,
      signedMsgWsDelegatesKey,
      authorityKey,
      systemProgramKey
    );
    return changeSignedMsgWsDelegateStatus(invokedDriftProgramMeta, keys, delegate, add);
  }

  public static Instruction changeSignedMsgWsDelegateStatus(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final PublicKey delegate,
                                                            final boolean add) {
    final byte[] _data = new byte[41];
    int i = CHANGE_SIGNED_MSG_WS_DELEGATE_STATUS_DISCRIMINATOR.write(_data, 0);
    delegate.write(_data, i);
    i += 32;
    _data[i] = (byte) (add ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ChangeSignedMsgWsDelegateStatusIxData(Discriminator discriminator, PublicKey delegate, boolean add) implements Borsh {  

    public static ChangeSignedMsgWsDelegateStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 41;

    public static ChangeSignedMsgWsDelegateStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var delegate = readPubKey(_data, i);
      i += 32;
      final var add = _data[i] == 1;
      return new ChangeSignedMsgWsDelegateStatusIxData(discriminator, delegate, add);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      delegate.write(_data, i);
      i += 32;
      _data[i] = (byte) (add ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_FUEL_OVERFLOW_DISCRIMINATOR = toDiscriminator(88, 223, 132, 161, 208, 88, 142, 42);

  public static List<AccountMeta> initializeFuelOverflowKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey fuelOverflowKey,
                                                             final PublicKey userStatsKey,
                                                             final PublicKey authorityKey,
                                                             final PublicKey payerKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey) {
    return List.of(
      createWrite(fuelOverflowKey),
      createWrite(userStatsKey),
      createRead(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeFuelOverflow(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey fuelOverflowKey,
                                                   final PublicKey userStatsKey,
                                                   final PublicKey authorityKey,
                                                   final PublicKey payerKey,
                                                   final PublicKey rentKey,
                                                   final PublicKey systemProgramKey) {
    final var keys = initializeFuelOverflowKeys(
      invokedDriftProgramMeta,
      fuelOverflowKey,
      userStatsKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeFuelOverflow(invokedDriftProgramMeta, keys);
  }

  public static Instruction initializeFuelOverflow(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, INITIALIZE_FUEL_OVERFLOW_DISCRIMINATOR);
  }

  public static final Discriminator SWEEP_FUEL_DISCRIMINATOR = toDiscriminator(175, 107, 19, 56, 165, 241, 43, 69);

  public static List<AccountMeta> sweepFuelKeys(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final PublicKey fuelOverflowKey,
                                                final PublicKey userStatsKey,
                                                final PublicKey authorityKey,
                                                final PublicKey signerKey) {
    return List.of(
      createWrite(fuelOverflowKey),
      createWrite(userStatsKey),
      createRead(authorityKey),
      createReadOnlySigner(signerKey)
    );
  }

  public static Instruction sweepFuel(final AccountMeta invokedDriftProgramMeta,
                                      final PublicKey fuelOverflowKey,
                                      final PublicKey userStatsKey,
                                      final PublicKey authorityKey,
                                      final PublicKey signerKey) {
    final var keys = sweepFuelKeys(
      invokedDriftProgramMeta,
      fuelOverflowKey,
      userStatsKey,
      authorityKey,
      signerKey
    );
    return sweepFuel(invokedDriftProgramMeta, keys);
  }

  public static Instruction sweepFuel(final AccountMeta invokedDriftProgramMeta                                      ,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, SWEEP_FUEL_DISCRIMINATOR);
  }

  public static final Discriminator RESET_FUEL_SEASON_DISCRIMINATOR = toDiscriminator(199, 122, 192, 255, 32, 99, 63, 200);

  public static List<AccountMeta> resetFuelSeasonKeys(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final PublicKey userStatsKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey adminKey) {
    return List.of(
      createWrite(userStatsKey),
      createRead(authorityKey),
      createRead(stateKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction resetFuelSeason(final AccountMeta invokedDriftProgramMeta,
                                            final PublicKey userStatsKey,
                                            final PublicKey authorityKey,
                                            final PublicKey stateKey,
                                            final PublicKey adminKey) {
    final var keys = resetFuelSeasonKeys(
      invokedDriftProgramMeta,
      userStatsKey,
      authorityKey,
      stateKey,
      adminKey
    );
    return resetFuelSeason(invokedDriftProgramMeta, keys);
  }

  public static Instruction resetFuelSeason(final AccountMeta invokedDriftProgramMeta                                            ,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, RESET_FUEL_SEASON_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_REFERRER_NAME_DISCRIMINATOR = toDiscriminator(235, 126, 231, 10, 42, 164, 26, 61);

  public static List<AccountMeta> initializeReferrerNameKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey referrerNameKey,
                                                             final PublicKey userKey,
                                                             final PublicKey userStatsKey,
                                                             final PublicKey authorityKey,
                                                             final PublicKey payerKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey) {
    return List.of(
      createWrite(referrerNameKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeReferrerName(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey referrerNameKey,
                                                   final PublicKey userKey,
                                                   final PublicKey userStatsKey,
                                                   final PublicKey authorityKey,
                                                   final PublicKey payerKey,
                                                   final PublicKey rentKey,
                                                   final PublicKey systemProgramKey,
                                                   final byte[] name) {
    final var keys = initializeReferrerNameKeys(
      invokedDriftProgramMeta,
      referrerNameKey,
      userKey,
      userStatsKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeReferrerName(invokedDriftProgramMeta, keys, name);
  }

  public static Instruction initializeReferrerName(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final byte[] name) {
    final byte[] _data = new byte[8 + Borsh.lenArray(name)];
    int i = INITIALIZE_REFERRER_NAME_DISCRIMINATOR.write(_data, 0);
    Borsh.writeArrayChecked(name, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeReferrerNameIxData(Discriminator discriminator, byte[] name) implements Borsh {  

    public static InitializeReferrerNameIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int NAME_LEN = 32;

    public static InitializeReferrerNameIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var name = new byte[32];
      Borsh.readArray(name, _data, i);
      return new InitializeReferrerNameIxData(discriminator, name);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_DISCRIMINATOR = toDiscriminator(242, 35, 198, 137, 82, 225, 242, 182);

  public static List<AccountMeta> depositKeys(final AccountMeta invokedDriftProgramMeta                                              ,
                                              final PublicKey stateKey,
                                              final PublicKey userKey,
                                              final PublicKey userStatsKey,
                                              final PublicKey authorityKey,
                                              final PublicKey spotMarketVaultKey,
                                              final PublicKey userTokenAccountKey,
                                              final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(spotMarketVaultKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction deposit(final AccountMeta invokedDriftProgramMeta,
                                    final PublicKey stateKey,
                                    final PublicKey userKey,
                                    final PublicKey userStatsKey,
                                    final PublicKey authorityKey,
                                    final PublicKey spotMarketVaultKey,
                                    final PublicKey userTokenAccountKey,
                                    final PublicKey tokenProgramKey,
                                    final int marketIndex,
                                    final long amount,
                                    final boolean reduceOnly) {
    final var keys = depositKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      authorityKey,
      spotMarketVaultKey,
      userTokenAccountKey,
      tokenProgramKey
    );
    return deposit(
      invokedDriftProgramMeta,
      keys,
      marketIndex,
      amount,
      reduceOnly
    );
  }

  public static Instruction deposit(final AccountMeta invokedDriftProgramMeta                                    ,
                                    final List<AccountMeta> keys,
                                    final int marketIndex,
                                    final long amount,
                                    final boolean reduceOnly) {
    final byte[] _data = new byte[19];
    int i = DEPOSIT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) (reduceOnly ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DepositIxData(Discriminator discriminator,
                              int marketIndex,
                              long amount,
                              boolean reduceOnly) implements Borsh {  

    public static DepositIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 19;

    public static DepositIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var reduceOnly = _data[i] == 1;
      return new DepositIxData(discriminator, marketIndex, amount, reduceOnly);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) (reduceOnly ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 18, 70, 156, 148, 109, 161, 34);

  public static List<AccountMeta> withdrawKeys(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final PublicKey stateKey,
                                               final PublicKey userKey,
                                               final PublicKey userStatsKey,
                                               final PublicKey authorityKey,
                                               final PublicKey spotMarketVaultKey,
                                               final PublicKey driftSignerKey,
                                               final PublicKey userTokenAccountKey,
                                               final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(spotMarketVaultKey),
      createRead(driftSignerKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction withdraw(final AccountMeta invokedDriftProgramMeta,
                                     final PublicKey stateKey,
                                     final PublicKey userKey,
                                     final PublicKey userStatsKey,
                                     final PublicKey authorityKey,
                                     final PublicKey spotMarketVaultKey,
                                     final PublicKey driftSignerKey,
                                     final PublicKey userTokenAccountKey,
                                     final PublicKey tokenProgramKey,
                                     final int marketIndex,
                                     final long amount,
                                     final boolean reduceOnly) {
    final var keys = withdrawKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      authorityKey,
      spotMarketVaultKey,
      driftSignerKey,
      userTokenAccountKey,
      tokenProgramKey
    );
    return withdraw(
      invokedDriftProgramMeta,
      keys,
      marketIndex,
      amount,
      reduceOnly
    );
  }

  public static Instruction withdraw(final AccountMeta invokedDriftProgramMeta                                     ,
                                     final List<AccountMeta> keys,
                                     final int marketIndex,
                                     final long amount,
                                     final boolean reduceOnly) {
    final byte[] _data = new byte[19];
    int i = WITHDRAW_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);
    i += 8;
    _data[i] = (byte) (reduceOnly ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record WithdrawIxData(Discriminator discriminator,
                               int marketIndex,
                               long amount,
                               boolean reduceOnly) implements Borsh {  

    public static WithdrawIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 19;

    public static WithdrawIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      i += 8;
      final var reduceOnly = _data[i] == 1;
      return new WithdrawIxData(discriminator, marketIndex, amount, reduceOnly);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      _data[i] = (byte) (reduceOnly ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_DEPOSIT_DISCRIMINATOR = toDiscriminator(20, 20, 147, 223, 41, 63, 204, 111);

  public static List<AccountMeta> transferDepositKeys(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final PublicKey fromUserKey,
                                                      final PublicKey toUserKey,
                                                      final PublicKey userStatsKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey spotMarketVaultKey) {
    return List.of(
      createWrite(fromUserKey),
      createWrite(toUserKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createRead(stateKey),
      createRead(spotMarketVaultKey)
    );
  }

  public static Instruction transferDeposit(final AccountMeta invokedDriftProgramMeta,
                                            final PublicKey fromUserKey,
                                            final PublicKey toUserKey,
                                            final PublicKey userStatsKey,
                                            final PublicKey authorityKey,
                                            final PublicKey stateKey,
                                            final PublicKey spotMarketVaultKey,
                                            final int marketIndex,
                                            final long amount) {
    final var keys = transferDepositKeys(
      invokedDriftProgramMeta,
      fromUserKey,
      toUserKey,
      userStatsKey,
      authorityKey,
      stateKey,
      spotMarketVaultKey
    );
    return transferDeposit(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction transferDeposit(final AccountMeta invokedDriftProgramMeta                                            ,
                                            final List<AccountMeta> keys,
                                            final int marketIndex,
                                            final long amount) {
    final byte[] _data = new byte[18];
    int i = TRANSFER_DEPOSIT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record TransferDepositIxData(Discriminator discriminator, int marketIndex, long amount) implements Borsh {  

    public static TransferDepositIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static TransferDepositIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      return new TransferDepositIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_POOLS_DISCRIMINATOR = toDiscriminator(197, 103, 154, 25, 107, 90, 60, 94);

  public static List<AccountMeta> transferPoolsKeys(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final PublicKey fromUserKey,
                                                    final PublicKey toUserKey,
                                                    final PublicKey userStatsKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey stateKey,
                                                    final PublicKey depositFromSpotMarketVaultKey,
                                                    final PublicKey depositToSpotMarketVaultKey,
                                                    final PublicKey borrowFromSpotMarketVaultKey,
                                                    final PublicKey borrowToSpotMarketVaultKey,
                                                    final PublicKey driftSignerKey) {
    return List.of(
      createWrite(fromUserKey),
      createWrite(toUserKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createRead(stateKey),
      createWrite(depositFromSpotMarketVaultKey),
      createWrite(depositToSpotMarketVaultKey),
      createWrite(borrowFromSpotMarketVaultKey),
      createWrite(borrowToSpotMarketVaultKey),
      createRead(driftSignerKey)
    );
  }

  public static Instruction transferPools(final AccountMeta invokedDriftProgramMeta,
                                          final PublicKey fromUserKey,
                                          final PublicKey toUserKey,
                                          final PublicKey userStatsKey,
                                          final PublicKey authorityKey,
                                          final PublicKey stateKey,
                                          final PublicKey depositFromSpotMarketVaultKey,
                                          final PublicKey depositToSpotMarketVaultKey,
                                          final PublicKey borrowFromSpotMarketVaultKey,
                                          final PublicKey borrowToSpotMarketVaultKey,
                                          final PublicKey driftSignerKey,
                                          final int depositFromMarketIndex,
                                          final int depositToMarketIndex,
                                          final int borrowFromMarketIndex,
                                          final int borrowToMarketIndex,
                                          final OptionalLong depositAmount,
                                          final OptionalLong borrowAmount) {
    final var keys = transferPoolsKeys(
      invokedDriftProgramMeta,
      fromUserKey,
      toUserKey,
      userStatsKey,
      authorityKey,
      stateKey,
      depositFromSpotMarketVaultKey,
      depositToSpotMarketVaultKey,
      borrowFromSpotMarketVaultKey,
      borrowToSpotMarketVaultKey,
      driftSignerKey
    );
    return transferPools(
      invokedDriftProgramMeta,
      keys,
      depositFromMarketIndex,
      depositToMarketIndex,
      borrowFromMarketIndex,
      borrowToMarketIndex,
      depositAmount,
      borrowAmount
    );
  }

  public static Instruction transferPools(final AccountMeta invokedDriftProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final int depositFromMarketIndex,
                                          final int depositToMarketIndex,
                                          final int borrowFromMarketIndex,
                                          final int borrowToMarketIndex,
                                          final OptionalLong depositAmount,
                                          final OptionalLong borrowAmount) {
    final byte[] _data = new byte[
    16
    + (depositAmount == null || depositAmount.isEmpty() ? 1 : 9)
    + (borrowAmount == null || borrowAmount.isEmpty() ? 1 : 9)
    ];
    int i = TRANSFER_POOLS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, depositFromMarketIndex);
    i += 2;
    putInt16LE(_data, i, depositToMarketIndex);
    i += 2;
    putInt16LE(_data, i, borrowFromMarketIndex);
    i += 2;
    putInt16LE(_data, i, borrowToMarketIndex);
    i += 2;
    i += Borsh.writeOptional(depositAmount, _data, i);
    Borsh.writeOptional(borrowAmount, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record TransferPoolsIxData(Discriminator discriminator,
                                    int depositFromMarketIndex,
                                    int depositToMarketIndex,
                                    int borrowFromMarketIndex,
                                    int borrowToMarketIndex,
                                    OptionalLong depositAmount,
                                    OptionalLong borrowAmount) implements Borsh {  

    public static TransferPoolsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static TransferPoolsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var depositFromMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var depositToMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var borrowFromMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var borrowToMarketIndex = getInt16LE(_data, i);
      i += 2;
      final OptionalLong depositAmount;
      if (_data[i] == 0) {
        depositAmount = OptionalLong.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        depositAmount = OptionalLong.of(getInt64LE(_data, i));
        i += 8;
      }
      final OptionalLong borrowAmount;
      if (_data[i] == 0) {
        borrowAmount = OptionalLong.empty();
      } else {
        ++i;
      ;
        borrowAmount = OptionalLong.of(getInt64LE(_data, i));
      }
      return new TransferPoolsIxData(discriminator,
                                     depositFromMarketIndex,
                                     depositToMarketIndex,
                                     borrowFromMarketIndex,
                                     borrowToMarketIndex,
                                     depositAmount,
                                     borrowAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, depositFromMarketIndex);
      i += 2;
      putInt16LE(_data, i, depositToMarketIndex);
      i += 2;
      putInt16LE(_data, i, borrowFromMarketIndex);
      i += 2;
      putInt16LE(_data, i, borrowToMarketIndex);
      i += 2;
      i += Borsh.writeOptional(depositAmount, _data, i);
      i += Borsh.writeOptional(borrowAmount, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2
           + 2
           + 2
           + 2
           + (depositAmount == null || depositAmount.isEmpty() ? 1 : (1 + 8))
           + (borrowAmount == null || borrowAmount.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator TRANSFER_PERP_POSITION_DISCRIMINATOR = toDiscriminator(23, 172, 188, 168, 134, 210, 3, 108);

  public static List<AccountMeta> transferPerpPositionKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey fromUserKey,
                                                           final PublicKey toUserKey,
                                                           final PublicKey userStatsKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey stateKey) {
    return List.of(
      createWrite(fromUserKey),
      createWrite(toUserKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createRead(stateKey)
    );
  }

  public static Instruction transferPerpPosition(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey fromUserKey,
                                                 final PublicKey toUserKey,
                                                 final PublicKey userStatsKey,
                                                 final PublicKey authorityKey,
                                                 final PublicKey stateKey,
                                                 final int marketIndex,
                                                 final OptionalLong amount) {
    final var keys = transferPerpPositionKeys(
      invokedDriftProgramMeta,
      fromUserKey,
      toUserKey,
      userStatsKey,
      authorityKey,
      stateKey
    );
    return transferPerpPosition(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction transferPerpPosition(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int marketIndex,
                                                 final OptionalLong amount) {
    final byte[] _data = new byte[
    10
    + (amount == null || amount.isEmpty() ? 1 : 9)
    ];
    int i = TRANSFER_PERP_POSITION_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    Borsh.writeOptional(amount, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record TransferPerpPositionIxData(Discriminator discriminator, int marketIndex, OptionalLong amount) implements Borsh {  

    public static TransferPerpPositionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static TransferPerpPositionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final OptionalLong amount;
      if (_data[i] == 0) {
        amount = OptionalLong.empty();
      } else {
        ++i;
      ;
        amount = OptionalLong.of(getInt64LE(_data, i));
      }
      return new TransferPerpPositionIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      i += Borsh.writeOptional(amount, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + (amount == null || amount.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator PLACE_PERP_ORDER_DISCRIMINATOR = toDiscriminator(69, 161, 93, 202, 120, 126, 76, 185);

  public static List<AccountMeta> placePerpOrderKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey stateKey,
                                                     final PublicKey userKey,
                                                     final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placePerpOrder(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey stateKey,
                                           final PublicKey userKey,
                                           final PublicKey authorityKey,
                                           final OrderParams params) {
    final var keys = placePerpOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return placePerpOrder(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction placePerpOrder(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys,
                                           final OrderParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = PLACE_PERP_ORDER_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlacePerpOrderIxData(Discriminator discriminator, OrderParams params) implements Borsh {  

    public static PlacePerpOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlacePerpOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      return new PlacePerpOrderIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params);
    }
  }

  public static final Discriminator CANCEL_ORDER_DISCRIMINATOR = toDiscriminator(95, 129, 237, 240, 8, 49, 223, 132);

  public static List<AccountMeta> cancelOrderKeys(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction cancelOrder(final AccountMeta invokedDriftProgramMeta,
                                        final PublicKey stateKey,
                                        final PublicKey userKey,
                                        final PublicKey authorityKey,
                                        final OptionalInt orderId) {
    final var keys = cancelOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return cancelOrder(invokedDriftProgramMeta, keys, orderId);
  }

  public static Instruction cancelOrder(final AccountMeta invokedDriftProgramMeta                                        ,
                                        final List<AccountMeta> keys,
                                        final OptionalInt orderId) {
    final byte[] _data = new byte[
    8
    + (orderId == null || orderId.isEmpty() ? 1 : 5)
    ];
    int i = CANCEL_ORDER_DISCRIMINATOR.write(_data, 0);
    Borsh.writeOptional(orderId, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record CancelOrderIxData(Discriminator discriminator, OptionalInt orderId) implements Borsh {  

    public static CancelOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CancelOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt orderId;
      if (_data[i] == 0) {
        orderId = OptionalInt.empty();
      } else {
        ++i;
      ;
        orderId = OptionalInt.of(getInt32LE(_data, i));
      }
      return new CancelOrderIxData(discriminator, orderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(orderId, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (orderId == null || orderId.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator CANCEL_ORDER_BY_USER_ID_DISCRIMINATOR = toDiscriminator(107, 211, 250, 133, 18, 37, 57, 100);

  public static List<AccountMeta> cancelOrderByUserIdKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey stateKey,
                                                          final PublicKey userKey,
                                                          final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction cancelOrderByUserId(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey stateKey,
                                                final PublicKey userKey,
                                                final PublicKey authorityKey,
                                                final int userOrderId) {
    final var keys = cancelOrderByUserIdKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return cancelOrderByUserId(invokedDriftProgramMeta, keys, userOrderId);
  }

  public static Instruction cancelOrderByUserId(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys,
                                                final int userOrderId) {
    final byte[] _data = new byte[9];
    int i = CANCEL_ORDER_BY_USER_ID_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) userOrderId;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record CancelOrderByUserIdIxData(Discriminator discriminator, int userOrderId) implements Borsh {  

    public static CancelOrderByUserIdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static CancelOrderByUserIdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var userOrderId = _data[i] & 0xFF;
      return new CancelOrderByUserIdIxData(discriminator, userOrderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) userOrderId;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CANCEL_ORDERS_DISCRIMINATOR = toDiscriminator(238, 225, 95, 158, 227, 103, 8, 194);

  public static List<AccountMeta> cancelOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final PublicKey stateKey,
                                                   final PublicKey userKey,
                                                   final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction cancelOrders(final AccountMeta invokedDriftProgramMeta,
                                         final PublicKey stateKey,
                                         final PublicKey userKey,
                                         final PublicKey authorityKey,
                                         final MarketType marketType,
                                         final OptionalInt marketIndex,
                                         final PositionDirection direction) {
    final var keys = cancelOrdersKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return cancelOrders(
      invokedDriftProgramMeta,
      keys,
      marketType,
      marketIndex,
      direction
    );
  }

  public static Instruction cancelOrders(final AccountMeta invokedDriftProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final MarketType marketType,
                                         final OptionalInt marketIndex,
                                         final PositionDirection direction) {
    final byte[] _data = new byte[
    8
    + (marketType == null ? 1 : (1 + Borsh.len(marketType)))
    + (marketIndex == null || marketIndex.isEmpty() ? 1 : 3)
    + (direction == null ? 1 : (1 + Borsh.len(direction)))
    ];
    int i = CANCEL_ORDERS_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(marketType, _data, i);
    i += Borsh.writeOptionalshort(marketIndex, _data, i);
    Borsh.writeOptional(direction, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record CancelOrdersIxData(Discriminator discriminator,
                                   MarketType marketType,
                                   OptionalInt marketIndex,
                                   PositionDirection direction) implements Borsh {  

    public static CancelOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CancelOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final MarketType marketType;
      if (_data[i] == 0) {
        marketType = null;
        ++i;
      ;
      } else {
        ++i;
      ;
        marketType = MarketType.read(_data, i);
        i += Borsh.len(marketType);
      }
      final OptionalInt marketIndex;
      if (_data[i] == 0) {
        marketIndex = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        marketIndex = OptionalInt.of(getInt16LE(_data, i));
        i += 2;
      }
      final PositionDirection direction;
      if (_data[i] == 0) {
        direction = null;
      } else {
        ++i;
      ;
        direction = PositionDirection.read(_data, i);
      }
      return new CancelOrdersIxData(discriminator, marketType, marketIndex, direction);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(marketType, _data, i);
      i += Borsh.writeOptionalshort(marketIndex, _data, i);
      i += Borsh.writeOptional(direction, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (marketType == null ? 1 : (1 + Borsh.len(marketType))) + (marketIndex == null || marketIndex.isEmpty() ? 1 : (1 + 2)) + (direction == null ? 1 : (1 + Borsh.len(direction)));
    }
  }

  public static final Discriminator CANCEL_ORDERS_BY_IDS_DISCRIMINATOR = toDiscriminator(134, 19, 144, 165, 94, 240, 210, 94);

  public static List<AccountMeta> cancelOrdersByIdsKeys(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final PublicKey stateKey,
                                                        final PublicKey userKey,
                                                        final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction cancelOrdersByIds(final AccountMeta invokedDriftProgramMeta,
                                              final PublicKey stateKey,
                                              final PublicKey userKey,
                                              final PublicKey authorityKey,
                                              final int[] orderIds) {
    final var keys = cancelOrdersByIdsKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return cancelOrdersByIds(invokedDriftProgramMeta, keys, orderIds);
  }

  public static Instruction cancelOrdersByIds(final AccountMeta invokedDriftProgramMeta                                              ,
                                              final List<AccountMeta> keys,
                                              final int[] orderIds) {
    final byte[] _data = new byte[8 + Borsh.lenVector(orderIds)];
    int i = CANCEL_ORDERS_BY_IDS_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(orderIds, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record CancelOrdersByIdsIxData(Discriminator discriminator, int[] orderIds) implements Borsh {  

    public static CancelOrdersByIdsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CancelOrdersByIdsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderIds = Borsh.readintVector(_data, i);
      return new CancelOrdersByIdsIxData(discriminator, orderIds);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(orderIds, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(orderIds);
    }
  }

  public static final Discriminator MODIFY_ORDER_DISCRIMINATOR = toDiscriminator(47, 124, 117, 255, 201, 197, 130, 94);

  public static List<AccountMeta> modifyOrderKeys(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction modifyOrder(final AccountMeta invokedDriftProgramMeta,
                                        final PublicKey stateKey,
                                        final PublicKey userKey,
                                        final PublicKey authorityKey,
                                        final OptionalInt orderId,
                                        final ModifyOrderParams modifyOrderParams) {
    final var keys = modifyOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return modifyOrder(invokedDriftProgramMeta, keys, orderId, modifyOrderParams);
  }

  public static Instruction modifyOrder(final AccountMeta invokedDriftProgramMeta                                        ,
                                        final List<AccountMeta> keys,
                                        final OptionalInt orderId,
                                        final ModifyOrderParams modifyOrderParams) {
    final byte[] _data = new byte[
    8
    + (orderId == null || orderId.isEmpty() ? 1 : 5) + Borsh.len(modifyOrderParams)
    ];
    int i = MODIFY_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(orderId, _data, i);
    Borsh.write(modifyOrderParams, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ModifyOrderIxData(Discriminator discriminator, OptionalInt orderId, ModifyOrderParams modifyOrderParams) implements Borsh {  

    public static ModifyOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ModifyOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt orderId;
      if (_data[i] == 0) {
        orderId = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        orderId = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final var modifyOrderParams = ModifyOrderParams.read(_data, i);
      return new ModifyOrderIxData(discriminator, orderId, modifyOrderParams);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(orderId, _data, i);
      i += Borsh.write(modifyOrderParams, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (orderId == null || orderId.isEmpty() ? 1 : (1 + 4)) + Borsh.len(modifyOrderParams);
    }
  }

  public static final Discriminator MODIFY_ORDER_BY_USER_ID_DISCRIMINATOR = toDiscriminator(158, 77, 4, 253, 252, 194, 161, 179);

  public static List<AccountMeta> modifyOrderByUserIdKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey stateKey,
                                                          final PublicKey userKey,
                                                          final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction modifyOrderByUserId(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey stateKey,
                                                final PublicKey userKey,
                                                final PublicKey authorityKey,
                                                final int userOrderId,
                                                final ModifyOrderParams modifyOrderParams) {
    final var keys = modifyOrderByUserIdKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return modifyOrderByUserId(invokedDriftProgramMeta, keys, userOrderId, modifyOrderParams);
  }

  public static Instruction modifyOrderByUserId(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys,
                                                final int userOrderId,
                                                final ModifyOrderParams modifyOrderParams) {
    final byte[] _data = new byte[9 + Borsh.len(modifyOrderParams)];
    int i = MODIFY_ORDER_BY_USER_ID_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) userOrderId;
    ++i;
    Borsh.write(modifyOrderParams, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ModifyOrderByUserIdIxData(Discriminator discriminator, int userOrderId, ModifyOrderParams modifyOrderParams) implements Borsh {  

    public static ModifyOrderByUserIdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ModifyOrderByUserIdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var userOrderId = _data[i] & 0xFF;
      ++i;
      final var modifyOrderParams = ModifyOrderParams.read(_data, i);
      return new ModifyOrderByUserIdIxData(discriminator, userOrderId, modifyOrderParams);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) userOrderId;
      ++i;
      i += Borsh.write(modifyOrderParams, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 1 + Borsh.len(modifyOrderParams);
    }
  }

  public static final Discriminator PLACE_AND_TAKE_PERP_ORDER_DISCRIMINATOR = toDiscriminator(213, 51, 1, 187, 108, 220, 230, 224);

  public static List<AccountMeta> placeAndTakePerpOrderKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeAndTakePerpOrder(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey authorityKey,
                                                  final OrderParams params,
                                                  final OptionalInt successCondition) {
    final var keys = placeAndTakePerpOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      authorityKey
    );
    return placeAndTakePerpOrder(invokedDriftProgramMeta, keys, params, successCondition);
  }

  public static Instruction placeAndTakePerpOrder(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final OrderParams params,
                                                  final OptionalInt successCondition) {
    final byte[] _data = new byte[
    8 + Borsh.len(params)
    + (successCondition == null || successCondition.isEmpty() ? 1 : 5)
    ];
    int i = PLACE_AND_TAKE_PERP_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(params, _data, i);
    Borsh.writeOptional(successCondition, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceAndTakePerpOrderIxData(Discriminator discriminator, OrderParams params, OptionalInt successCondition) implements Borsh {  

    public static PlaceAndTakePerpOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceAndTakePerpOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      i += Borsh.len(params);
      final OptionalInt successCondition;
      if (_data[i] == 0) {
        successCondition = OptionalInt.empty();
      } else {
        ++i;
      ;
        successCondition = OptionalInt.of(getInt32LE(_data, i));
      }
      return new PlaceAndTakePerpOrderIxData(discriminator, params, successCondition);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      i += Borsh.writeOptional(successCondition, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params) + (successCondition == null || successCondition.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator PLACE_AND_MAKE_PERP_ORDER_DISCRIMINATOR = toDiscriminator(149, 117, 11, 237, 47, 95, 89, 237);

  public static List<AccountMeta> placeAndMakePerpOrderKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey takerKey,
                                                            final PublicKey takerStatsKey,
                                                            final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(takerKey),
      createWrite(takerStatsKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeAndMakePerpOrder(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey takerKey,
                                                  final PublicKey takerStatsKey,
                                                  final PublicKey authorityKey,
                                                  final OrderParams params,
                                                  final int takerOrderId) {
    final var keys = placeAndMakePerpOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      takerKey,
      takerStatsKey,
      authorityKey
    );
    return placeAndMakePerpOrder(invokedDriftProgramMeta, keys, params, takerOrderId);
  }

  public static Instruction placeAndMakePerpOrder(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final OrderParams params,
                                                  final int takerOrderId) {
    final byte[] _data = new byte[12 + Borsh.len(params)];
    int i = PLACE_AND_MAKE_PERP_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(params, _data, i);
    putInt32LE(_data, i, takerOrderId);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceAndMakePerpOrderIxData(Discriminator discriminator, OrderParams params, int takerOrderId) implements Borsh {  

    public static PlaceAndMakePerpOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceAndMakePerpOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      i += Borsh.len(params);
      final var takerOrderId = getInt32LE(_data, i);
      return new PlaceAndMakePerpOrderIxData(discriminator, params, takerOrderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      putInt32LE(_data, i, takerOrderId);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params) + 4;
    }
  }

  public static final Discriminator PLACE_AND_MAKE_SIGNED_MSG_PERP_ORDER_DISCRIMINATOR = toDiscriminator(16, 26, 123, 131, 94, 29, 175, 98);

  public static List<AccountMeta> placeAndMakeSignedMsgPerpOrderKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey userKey,
                                                                     final PublicKey userStatsKey,
                                                                     final PublicKey takerKey,
                                                                     final PublicKey takerStatsKey,
                                                                     final PublicKey takerSignedMsgUserOrdersKey,
                                                                     final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(takerKey),
      createWrite(takerStatsKey),
      createRead(takerSignedMsgUserOrdersKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeAndMakeSignedMsgPerpOrder(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey stateKey,
                                                           final PublicKey userKey,
                                                           final PublicKey userStatsKey,
                                                           final PublicKey takerKey,
                                                           final PublicKey takerStatsKey,
                                                           final PublicKey takerSignedMsgUserOrdersKey,
                                                           final PublicKey authorityKey,
                                                           final OrderParams params,
                                                           final byte[] signedMsgOrderUuid) {
    final var keys = placeAndMakeSignedMsgPerpOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      takerKey,
      takerStatsKey,
      takerSignedMsgUserOrdersKey,
      authorityKey
    );
    return placeAndMakeSignedMsgPerpOrder(invokedDriftProgramMeta, keys, params, signedMsgOrderUuid);
  }

  public static Instruction placeAndMakeSignedMsgPerpOrder(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final OrderParams params,
                                                           final byte[] signedMsgOrderUuid) {
    final byte[] _data = new byte[8 + Borsh.len(params) + Borsh.lenArray(signedMsgOrderUuid)];
    int i = PLACE_AND_MAKE_SIGNED_MSG_PERP_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(params, _data, i);
    Borsh.writeArrayChecked(signedMsgOrderUuid, 8, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceAndMakeSignedMsgPerpOrderIxData(Discriminator discriminator, OrderParams params, byte[] signedMsgOrderUuid) implements Borsh {  

    public static PlaceAndMakeSignedMsgPerpOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int SIGNED_MSG_ORDER_UUID_LEN = 8;
    public static PlaceAndMakeSignedMsgPerpOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      i += Borsh.len(params);
      final var signedMsgOrderUuid = new byte[8];
      Borsh.readArray(signedMsgOrderUuid, _data, i);
      return new PlaceAndMakeSignedMsgPerpOrderIxData(discriminator, params, signedMsgOrderUuid);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      i += Borsh.writeArrayChecked(signedMsgOrderUuid, 8, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params) + Borsh.lenArray(signedMsgOrderUuid);
    }
  }

  public static final Discriminator PLACE_SIGNED_MSG_TAKER_ORDER_DISCRIMINATOR = toDiscriminator(32, 79, 101, 139, 25, 6, 98, 15);

  /// @param ixSysvarKey the supplied Sysvar could be anything else.
  ///                    The Instruction Sysvar has not been implemented
  ///                    in the Anchor framework yet, so this is the safe approach.
  public static List<AccountMeta> placeSignedMsgTakerOrderKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey stateKey,
                                                               final PublicKey userKey,
                                                               final PublicKey userStatsKey,
                                                               final PublicKey signedMsgUserOrdersKey,
                                                               final PublicKey authorityKey,
                                                               final PublicKey ixSysvarKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(signedMsgUserOrdersKey),
      createReadOnlySigner(authorityKey),
      createRead(ixSysvarKey)
    );
  }

  /// @param ixSysvarKey the supplied Sysvar could be anything else.
  ///                    The Instruction Sysvar has not been implemented
  ///                    in the Anchor framework yet, so this is the safe approach.
  public static Instruction placeSignedMsgTakerOrder(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey stateKey,
                                                     final PublicKey userKey,
                                                     final PublicKey userStatsKey,
                                                     final PublicKey signedMsgUserOrdersKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey ixSysvarKey,
                                                     final byte[] signedMsgOrderParamsMessageBytes,
                                                     final boolean isDelegateSigner) {
    final var keys = placeSignedMsgTakerOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      signedMsgUserOrdersKey,
      authorityKey,
      ixSysvarKey
    );
    return placeSignedMsgTakerOrder(invokedDriftProgramMeta, keys, signedMsgOrderParamsMessageBytes, isDelegateSigner);
  }

  public static Instruction placeSignedMsgTakerOrder(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final byte[] signedMsgOrderParamsMessageBytes,
                                                     final boolean isDelegateSigner) {
    final byte[] _data = new byte[9 + Borsh.lenVector(signedMsgOrderParamsMessageBytes)];
    int i = PLACE_SIGNED_MSG_TAKER_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(signedMsgOrderParamsMessageBytes, _data, i);
    _data[i] = (byte) (isDelegateSigner ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceSignedMsgTakerOrderIxData(Discriminator discriminator, byte[] signedMsgOrderParamsMessageBytes, boolean isDelegateSigner) implements Borsh {  

    public static PlaceSignedMsgTakerOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceSignedMsgTakerOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var signedMsgOrderParamsMessageBytes = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(signedMsgOrderParamsMessageBytes);
      final var isDelegateSigner = _data[i] == 1;
      return new PlaceSignedMsgTakerOrderIxData(discriminator, signedMsgOrderParamsMessageBytes, isDelegateSigner);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(signedMsgOrderParamsMessageBytes, _data, i);
      _data[i] = (byte) (isDelegateSigner ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(signedMsgOrderParamsMessageBytes) + 1;
    }
  }

  public static final Discriminator PLACE_SPOT_ORDER_DISCRIMINATOR = toDiscriminator(45, 79, 81, 160, 248, 90, 91, 220);

  public static List<AccountMeta> placeSpotOrderKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey stateKey,
                                                     final PublicKey userKey,
                                                     final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeSpotOrder(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey stateKey,
                                           final PublicKey userKey,
                                           final PublicKey authorityKey,
                                           final OrderParams params) {
    final var keys = placeSpotOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return placeSpotOrder(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction placeSpotOrder(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys,
                                           final OrderParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = PLACE_SPOT_ORDER_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceSpotOrderIxData(Discriminator discriminator, OrderParams params) implements Borsh {  

    public static PlaceSpotOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceSpotOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      return new PlaceSpotOrderIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params);
    }
  }

  public static final Discriminator PLACE_AND_TAKE_SPOT_ORDER_DISCRIMINATOR = toDiscriminator(191, 3, 138, 71, 114, 198, 202, 100);

  public static List<AccountMeta> placeAndTakeSpotOrderKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeAndTakeSpotOrder(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey authorityKey,
                                                  final OrderParams params,
                                                  final SpotFulfillmentType fulfillmentType,
                                                  final OptionalInt makerOrderId) {
    final var keys = placeAndTakeSpotOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      authorityKey
    );
    return placeAndTakeSpotOrder(
      invokedDriftProgramMeta,
      keys,
      params,
      fulfillmentType,
      makerOrderId
    );
  }

  public static Instruction placeAndTakeSpotOrder(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final OrderParams params,
                                                  final SpotFulfillmentType fulfillmentType,
                                                  final OptionalInt makerOrderId) {
    final byte[] _data = new byte[
    8 + Borsh.len(params)
    + (fulfillmentType == null ? 1 : (1 + Borsh.len(fulfillmentType)))
    + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : 5)
    ];
    int i = PLACE_AND_TAKE_SPOT_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(params, _data, i);
    i += Borsh.writeOptional(fulfillmentType, _data, i);
    Borsh.writeOptional(makerOrderId, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceAndTakeSpotOrderIxData(Discriminator discriminator,
                                            OrderParams params,
                                            SpotFulfillmentType fulfillmentType,
                                            OptionalInt makerOrderId) implements Borsh {  

    public static PlaceAndTakeSpotOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceAndTakeSpotOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      i += Borsh.len(params);
      final SpotFulfillmentType fulfillmentType;
      if (_data[i] == 0) {
        fulfillmentType = null;
        ++i;
      ;
      } else {
        ++i;
      ;
        fulfillmentType = SpotFulfillmentType.read(_data, i);
        i += Borsh.len(fulfillmentType);
      }
      final OptionalInt makerOrderId;
      if (_data[i] == 0) {
        makerOrderId = OptionalInt.empty();
      } else {
        ++i;
      ;
        makerOrderId = OptionalInt.of(getInt32LE(_data, i));
      }
      return new PlaceAndTakeSpotOrderIxData(discriminator, params, fulfillmentType, makerOrderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      i += Borsh.writeOptional(fulfillmentType, _data, i);
      i += Borsh.writeOptional(makerOrderId, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params) + (fulfillmentType == null ? 1 : (1 + Borsh.len(fulfillmentType))) + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator PLACE_AND_MAKE_SPOT_ORDER_DISCRIMINATOR = toDiscriminator(149, 158, 85, 66, 239, 9, 243, 98);

  public static List<AccountMeta> placeAndMakeSpotOrderKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey takerKey,
                                                            final PublicKey takerStatsKey,
                                                            final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(takerKey),
      createWrite(takerStatsKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeAndMakeSpotOrder(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey takerKey,
                                                  final PublicKey takerStatsKey,
                                                  final PublicKey authorityKey,
                                                  final OrderParams params,
                                                  final int takerOrderId,
                                                  final SpotFulfillmentType fulfillmentType) {
    final var keys = placeAndMakeSpotOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      takerKey,
      takerStatsKey,
      authorityKey
    );
    return placeAndMakeSpotOrder(
      invokedDriftProgramMeta,
      keys,
      params,
      takerOrderId,
      fulfillmentType
    );
  }

  public static Instruction placeAndMakeSpotOrder(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final OrderParams params,
                                                  final int takerOrderId,
                                                  final SpotFulfillmentType fulfillmentType) {
    final byte[] _data = new byte[
    12 + Borsh.len(params)
    + (fulfillmentType == null ? 1 : (1 + Borsh.len(fulfillmentType)))
    ];
    int i = PLACE_AND_MAKE_SPOT_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.write(params, _data, i);
    putInt32LE(_data, i, takerOrderId);
    i += 4;
    Borsh.writeOptional(fulfillmentType, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceAndMakeSpotOrderIxData(Discriminator discriminator,
                                            OrderParams params,
                                            int takerOrderId,
                                            SpotFulfillmentType fulfillmentType) implements Borsh {  

    public static PlaceAndMakeSpotOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceAndMakeSpotOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = OrderParams.read(_data, i);
      i += Borsh.len(params);
      final var takerOrderId = getInt32LE(_data, i);
      i += 4;
      final SpotFulfillmentType fulfillmentType;
      if (_data[i] == 0) {
        fulfillmentType = null;
      } else {
        ++i;
      ;
        fulfillmentType = SpotFulfillmentType.read(_data, i);
      }
      return new PlaceAndMakeSpotOrderIxData(discriminator, params, takerOrderId, fulfillmentType);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      putInt32LE(_data, i, takerOrderId);
      i += 4;
      i += Borsh.writeOptional(fulfillmentType, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params) + 4 + (fulfillmentType == null ? 1 : (1 + Borsh.len(fulfillmentType)));
    }
  }

  public static final Discriminator PLACE_ORDERS_DISCRIMINATOR = toDiscriminator(60, 63, 50, 123, 12, 197, 60, 190);

  public static List<AccountMeta> placeOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final PublicKey stateKey,
                                                  final PublicKey userKey,
                                                  final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction placeOrders(final AccountMeta invokedDriftProgramMeta,
                                        final PublicKey stateKey,
                                        final PublicKey userKey,
                                        final PublicKey authorityKey,
                                        final OrderParams[] params) {
    final var keys = placeOrdersKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return placeOrders(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction placeOrders(final AccountMeta invokedDriftProgramMeta                                        ,
                                        final List<AccountMeta> keys,
                                        final OrderParams[] params) {
    final byte[] _data = new byte[8 + Borsh.lenVector(params)];
    int i = PLACE_ORDERS_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PlaceOrdersIxData(Discriminator discriminator, OrderParams[] params) implements Borsh {  

    public static PlaceOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PlaceOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = Borsh.readVector(OrderParams.class, OrderParams::read, _data, i);
      return new PlaceOrdersIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(params);
    }
  }

  public static final Discriminator BEGIN_SWAP_DISCRIMINATOR = toDiscriminator(174, 109, 228, 1, 242, 105, 232, 105);

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> beginSwapKeys(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final PublicKey stateKey,
                                                final PublicKey userKey,
                                                final PublicKey userStatsKey,
                                                final PublicKey authorityKey,
                                                final PublicKey outSpotMarketVaultKey,
                                                final PublicKey inSpotMarketVaultKey,
                                                final PublicKey outTokenAccountKey,
                                                final PublicKey inTokenAccountKey,
                                                final PublicKey tokenProgramKey,
                                                final PublicKey driftSignerKey,
                                                final PublicKey instructionsKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(outSpotMarketVaultKey),
      createWrite(inSpotMarketVaultKey),
      createWrite(outTokenAccountKey),
      createWrite(inTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey),
      createRead(instructionsKey)
    );
  }

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction beginSwap(final AccountMeta invokedDriftProgramMeta,
                                      final PublicKey stateKey,
                                      final PublicKey userKey,
                                      final PublicKey userStatsKey,
                                      final PublicKey authorityKey,
                                      final PublicKey outSpotMarketVaultKey,
                                      final PublicKey inSpotMarketVaultKey,
                                      final PublicKey outTokenAccountKey,
                                      final PublicKey inTokenAccountKey,
                                      final PublicKey tokenProgramKey,
                                      final PublicKey driftSignerKey,
                                      final PublicKey instructionsKey,
                                      final int inMarketIndex,
                                      final int outMarketIndex,
                                      final long amountIn) {
    final var keys = beginSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      authorityKey,
      outSpotMarketVaultKey,
      inSpotMarketVaultKey,
      outTokenAccountKey,
      inTokenAccountKey,
      tokenProgramKey,
      driftSignerKey,
      instructionsKey
    );
    return beginSwap(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      outMarketIndex,
      amountIn
    );
  }

  public static Instruction beginSwap(final AccountMeta invokedDriftProgramMeta                                      ,
                                      final List<AccountMeta> keys,
                                      final int inMarketIndex,
                                      final int outMarketIndex,
                                      final long amountIn) {
    final byte[] _data = new byte[20];
    int i = BEGIN_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    putInt64LE(_data, i, amountIn);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record BeginSwapIxData(Discriminator discriminator,
                                int inMarketIndex,
                                int outMarketIndex,
                                long amountIn) implements Borsh {  

    public static BeginSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 20;

    public static BeginSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var amountIn = getInt64LE(_data, i);
      return new BeginSwapIxData(discriminator, inMarketIndex, outMarketIndex, amountIn);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      putInt64LE(_data, i, amountIn);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator END_SWAP_DISCRIMINATOR = toDiscriminator(177, 184, 27, 193, 34, 13, 210, 145);

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> endSwapKeys(final AccountMeta invokedDriftProgramMeta                                              ,
                                              final PublicKey stateKey,
                                              final PublicKey userKey,
                                              final PublicKey userStatsKey,
                                              final PublicKey authorityKey,
                                              final PublicKey outSpotMarketVaultKey,
                                              final PublicKey inSpotMarketVaultKey,
                                              final PublicKey outTokenAccountKey,
                                              final PublicKey inTokenAccountKey,
                                              final PublicKey tokenProgramKey,
                                              final PublicKey driftSignerKey,
                                              final PublicKey instructionsKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(outSpotMarketVaultKey),
      createWrite(inSpotMarketVaultKey),
      createWrite(outTokenAccountKey),
      createWrite(inTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey),
      createRead(instructionsKey)
    );
  }

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction endSwap(final AccountMeta invokedDriftProgramMeta,
                                    final PublicKey stateKey,
                                    final PublicKey userKey,
                                    final PublicKey userStatsKey,
                                    final PublicKey authorityKey,
                                    final PublicKey outSpotMarketVaultKey,
                                    final PublicKey inSpotMarketVaultKey,
                                    final PublicKey outTokenAccountKey,
                                    final PublicKey inTokenAccountKey,
                                    final PublicKey tokenProgramKey,
                                    final PublicKey driftSignerKey,
                                    final PublicKey instructionsKey,
                                    final int inMarketIndex,
                                    final int outMarketIndex,
                                    final OptionalLong limitPrice,
                                    final SwapReduceOnly reduceOnly) {
    final var keys = endSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      userStatsKey,
      authorityKey,
      outSpotMarketVaultKey,
      inSpotMarketVaultKey,
      outTokenAccountKey,
      inTokenAccountKey,
      tokenProgramKey,
      driftSignerKey,
      instructionsKey
    );
    return endSwap(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      outMarketIndex,
      limitPrice,
      reduceOnly
    );
  }

  public static Instruction endSwap(final AccountMeta invokedDriftProgramMeta                                    ,
                                    final List<AccountMeta> keys,
                                    final int inMarketIndex,
                                    final int outMarketIndex,
                                    final OptionalLong limitPrice,
                                    final SwapReduceOnly reduceOnly) {
    final byte[] _data = new byte[
    12
    + (limitPrice == null || limitPrice.isEmpty() ? 1 : 9)
    + (reduceOnly == null ? 1 : (1 + Borsh.len(reduceOnly)))
    ];
    int i = END_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    i += Borsh.writeOptional(limitPrice, _data, i);
    Borsh.writeOptional(reduceOnly, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record EndSwapIxData(Discriminator discriminator,
                              int inMarketIndex,
                              int outMarketIndex,
                              OptionalLong limitPrice,
                              SwapReduceOnly reduceOnly) implements Borsh {  

    public static EndSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static EndSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      i += 2;
      final OptionalLong limitPrice;
      if (_data[i] == 0) {
        limitPrice = OptionalLong.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        limitPrice = OptionalLong.of(getInt64LE(_data, i));
        i += 8;
      }
      final SwapReduceOnly reduceOnly;
      if (_data[i] == 0) {
        reduceOnly = null;
      } else {
        ++i;
      ;
        reduceOnly = SwapReduceOnly.read(_data, i);
      }
      return new EndSwapIxData(discriminator,
                               inMarketIndex,
                               outMarketIndex,
                               limitPrice,
                               reduceOnly);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      i += Borsh.writeOptional(limitPrice, _data, i);
      i += Borsh.writeOptional(reduceOnly, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + 2 + (limitPrice == null || limitPrice.isEmpty() ? 1 : (1 + 8)) + (reduceOnly == null ? 1 : (1 + Borsh.len(reduceOnly)));
    }
  }

  public static final Discriminator UPDATE_USER_NAME_DISCRIMINATOR = toDiscriminator(135, 25, 185, 56, 165, 53, 34, 136);

  public static List<AccountMeta> updateUserNameKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey userKey,
                                                     final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserName(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey userKey,
                                           final PublicKey authorityKey,
                                           final int subAccountId,
                                           final byte[] name) {
    final var keys = updateUserNameKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserName(invokedDriftProgramMeta, keys, subAccountId, name);
  }

  public static Instruction updateUserName(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys,
                                           final int subAccountId,
                                           final byte[] name) {
    final byte[] _data = new byte[10 + Borsh.lenArray(name)];
    int i = UPDATE_USER_NAME_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    Borsh.writeArrayChecked(name, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserNameIxData(Discriminator discriminator, int subAccountId, byte[] name) implements Borsh {  

    public static UpdateUserNameIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 42;
    public static final int NAME_LEN = 32;

    public static UpdateUserNameIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var name = new byte[32];
      Borsh.readArray(name, _data, i);
      return new UpdateUserNameIxData(discriminator, subAccountId, name);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_CUSTOM_MARGIN_RATIO_DISCRIMINATOR = toDiscriminator(21, 221, 140, 187, 32, 129, 11, 123);

  public static List<AccountMeta> updateUserCustomMarginRatioKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey userKey,
                                                                  final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserCustomMarginRatio(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey userKey,
                                                        final PublicKey authorityKey,
                                                        final int subAccountId,
                                                        final int marginRatio) {
    final var keys = updateUserCustomMarginRatioKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserCustomMarginRatio(invokedDriftProgramMeta, keys, subAccountId, marginRatio);
  }

  public static Instruction updateUserCustomMarginRatio(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final int subAccountId,
                                                        final int marginRatio) {
    final byte[] _data = new byte[14];
    int i = UPDATE_USER_CUSTOM_MARGIN_RATIO_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    putInt32LE(_data, i, marginRatio);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserCustomMarginRatioIxData(Discriminator discriminator, int subAccountId, int marginRatio) implements Borsh {  

    public static UpdateUserCustomMarginRatioIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 14;

    public static UpdateUserCustomMarginRatioIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var marginRatio = getInt32LE(_data, i);
      return new UpdateUserCustomMarginRatioIxData(discriminator, subAccountId, marginRatio);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      putInt32LE(_data, i, marginRatio);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_PERP_POSITION_CUSTOM_MARGIN_RATIO_DISCRIMINATOR = toDiscriminator(121, 137, 157, 155, 89, 186, 145, 113);

  public static List<AccountMeta> updateUserPerpPositionCustomMarginRatioKeys(final AccountMeta invokedDriftProgramMeta                                                                              ,
                                                                              final PublicKey userKey,
                                                                              final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserPerpPositionCustomMarginRatio(final AccountMeta invokedDriftProgramMeta,
                                                                    final PublicKey userKey,
                                                                    final PublicKey authorityKey,
                                                                    final int subAccountId,
                                                                    final int perpMarketIndex,
                                                                    final int marginRatio) {
    final var keys = updateUserPerpPositionCustomMarginRatioKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserPerpPositionCustomMarginRatio(
      invokedDriftProgramMeta,
      keys,
      subAccountId,
      perpMarketIndex,
      marginRatio
    );
  }

  public static Instruction updateUserPerpPositionCustomMarginRatio(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final List<AccountMeta> keys,
                                                                    final int subAccountId,
                                                                    final int perpMarketIndex,
                                                                    final int marginRatio) {
    final byte[] _data = new byte[14];
    int i = UPDATE_USER_PERP_POSITION_CUSTOM_MARGIN_RATIO_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt16LE(_data, i, marginRatio);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserPerpPositionCustomMarginRatioIxData(Discriminator discriminator,
                                                              int subAccountId,
                                                              int perpMarketIndex,
                                                              int marginRatio) implements Borsh {  

    public static UpdateUserPerpPositionCustomMarginRatioIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 14;

    public static UpdateUserPerpPositionCustomMarginRatioIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var perpMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var marginRatio = getInt16LE(_data, i);
      return new UpdateUserPerpPositionCustomMarginRatioIxData(discriminator, subAccountId, perpMarketIndex, marginRatio);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      putInt16LE(_data, i, perpMarketIndex);
      i += 2;
      putInt16LE(_data, i, marginRatio);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_MARGIN_TRADING_ENABLED_DISCRIMINATOR = toDiscriminator(194, 92, 204, 223, 246, 188, 31, 203);

  public static List<AccountMeta> updateUserMarginTradingEnabledKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey userKey,
                                                                     final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserMarginTradingEnabled(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey userKey,
                                                           final PublicKey authorityKey,
                                                           final int subAccountId,
                                                           final boolean marginTradingEnabled) {
    final var keys = updateUserMarginTradingEnabledKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserMarginTradingEnabled(invokedDriftProgramMeta, keys, subAccountId, marginTradingEnabled);
  }

  public static Instruction updateUserMarginTradingEnabled(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final int subAccountId,
                                                           final boolean marginTradingEnabled) {
    final byte[] _data = new byte[11];
    int i = UPDATE_USER_MARGIN_TRADING_ENABLED_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    _data[i] = (byte) (marginTradingEnabled ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserMarginTradingEnabledIxData(Discriminator discriminator, int subAccountId, boolean marginTradingEnabled) implements Borsh {  

    public static UpdateUserMarginTradingEnabledIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static UpdateUserMarginTradingEnabledIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var marginTradingEnabled = _data[i] == 1;
      return new UpdateUserMarginTradingEnabledIxData(discriminator, subAccountId, marginTradingEnabled);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      _data[i] = (byte) (marginTradingEnabled ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_POOL_ID_DISCRIMINATOR = toDiscriminator(219, 86, 73, 106, 56, 218, 128, 109);

  public static List<AccountMeta> updateUserPoolIdKeys(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final PublicKey userKey,
                                                       final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserPoolId(final AccountMeta invokedDriftProgramMeta,
                                             final PublicKey userKey,
                                             final PublicKey authorityKey,
                                             final int subAccountId,
                                             final int poolId) {
    final var keys = updateUserPoolIdKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserPoolId(invokedDriftProgramMeta, keys, subAccountId, poolId);
  }

  public static Instruction updateUserPoolId(final AccountMeta invokedDriftProgramMeta                                             ,
                                             final List<AccountMeta> keys,
                                             final int subAccountId,
                                             final int poolId) {
    final byte[] _data = new byte[11];
    int i = UPDATE_USER_POOL_ID_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    _data[i] = (byte) poolId;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserPoolIdIxData(Discriminator discriminator, int subAccountId, int poolId) implements Borsh {  

    public static UpdateUserPoolIdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static UpdateUserPoolIdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var poolId = _data[i] & 0xFF;
      return new UpdateUserPoolIdIxData(discriminator, subAccountId, poolId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      _data[i] = (byte) poolId;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_DELEGATE_DISCRIMINATOR = toDiscriminator(139, 205, 141, 141, 113, 36, 94, 187);

  public static List<AccountMeta> updateUserDelegateKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey userKey,
                                                         final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserDelegate(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey userKey,
                                               final PublicKey authorityKey,
                                               final int subAccountId,
                                               final PublicKey delegate) {
    final var keys = updateUserDelegateKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserDelegate(invokedDriftProgramMeta, keys, subAccountId, delegate);
  }

  public static Instruction updateUserDelegate(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final int subAccountId,
                                               final PublicKey delegate) {
    final byte[] _data = new byte[42];
    int i = UPDATE_USER_DELEGATE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    delegate.write(_data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserDelegateIxData(Discriminator discriminator, int subAccountId, PublicKey delegate) implements Borsh {  

    public static UpdateUserDelegateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 42;

    public static UpdateUserDelegateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var delegate = readPubKey(_data, i);
      return new UpdateUserDelegateIxData(discriminator, subAccountId, delegate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      delegate.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_REDUCE_ONLY_DISCRIMINATOR = toDiscriminator(199, 71, 42, 67, 144, 19, 86, 109);

  public static List<AccountMeta> updateUserReduceOnlyKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey userKey,
                                                           final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateUserReduceOnly(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey userKey,
                                                 final PublicKey authorityKey,
                                                 final int subAccountId,
                                                 final boolean reduceOnly) {
    final var keys = updateUserReduceOnlyKeys(
      invokedDriftProgramMeta,
      userKey,
      authorityKey
    );
    return updateUserReduceOnly(invokedDriftProgramMeta, keys, subAccountId, reduceOnly);
  }

  public static Instruction updateUserReduceOnly(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int subAccountId,
                                                 final boolean reduceOnly) {
    final byte[] _data = new byte[11];
    int i = UPDATE_USER_REDUCE_ONLY_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    _data[i] = (byte) (reduceOnly ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserReduceOnlyIxData(Discriminator discriminator, int subAccountId, boolean reduceOnly) implements Borsh {  

    public static UpdateUserReduceOnlyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static UpdateUserReduceOnlyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var reduceOnly = _data[i] == 1;
      return new UpdateUserReduceOnlyIxData(discriminator, subAccountId, reduceOnly);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      _data[i] = (byte) (reduceOnly ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_PROTECTED_MAKER_ORDERS_DISCRIMINATOR = toDiscriminator(114, 39, 123, 198, 187, 25, 90, 219);

  public static List<AccountMeta> updateUserProtectedMakerOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey userKey,
                                                                     final PublicKey authorityKey,
                                                                     final PublicKey protectedMakerModeConfigKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey),
      createWrite(protectedMakerModeConfigKey)
    );
  }

  public static Instruction updateUserProtectedMakerOrders(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey stateKey,
                                                           final PublicKey userKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey protectedMakerModeConfigKey,
                                                           final int subAccountId,
                                                           final boolean protectedMakerOrders) {
    final var keys = updateUserProtectedMakerOrdersKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey,
      protectedMakerModeConfigKey
    );
    return updateUserProtectedMakerOrders(invokedDriftProgramMeta, keys, subAccountId, protectedMakerOrders);
  }

  public static Instruction updateUserProtectedMakerOrders(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final int subAccountId,
                                                           final boolean protectedMakerOrders) {
    final byte[] _data = new byte[11];
    int i = UPDATE_USER_PROTECTED_MAKER_ORDERS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);
    i += 2;
    _data[i] = (byte) (protectedMakerOrders ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateUserProtectedMakerOrdersIxData(Discriminator discriminator, int subAccountId, boolean protectedMakerOrders) implements Borsh {  

    public static UpdateUserProtectedMakerOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 11;

    public static UpdateUserProtectedMakerOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      i += 2;
      final var protectedMakerOrders = _data[i] == 1;
      return new UpdateUserProtectedMakerOrdersIxData(discriminator, subAccountId, protectedMakerOrders);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      _data[i] = (byte) (protectedMakerOrders ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DELETE_USER_DISCRIMINATOR = toDiscriminator(186, 85, 17, 249, 219, 231, 98, 251);

  public static List<AccountMeta> deleteUserKeys(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final PublicKey userKey,
                                                 final PublicKey userStatsKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey authorityKey) {
    return List.of(
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(stateKey),
      createWritableSigner(authorityKey)
    );
  }

  public static Instruction deleteUser(final AccountMeta invokedDriftProgramMeta,
                                       final PublicKey userKey,
                                       final PublicKey userStatsKey,
                                       final PublicKey stateKey,
                                       final PublicKey authorityKey) {
    final var keys = deleteUserKeys(
      invokedDriftProgramMeta,
      userKey,
      userStatsKey,
      stateKey,
      authorityKey
    );
    return deleteUser(invokedDriftProgramMeta, keys);
  }

  public static Instruction deleteUser(final AccountMeta invokedDriftProgramMeta                                       ,
                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, DELETE_USER_DISCRIMINATOR);
  }

  public static final Discriminator FORCE_DELETE_USER_DISCRIMINATOR = toDiscriminator(2, 241, 195, 172, 227, 24, 254, 158);

  public static List<AccountMeta> forceDeleteUserKeys(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final PublicKey userKey,
                                                      final PublicKey userStatsKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey keeperKey,
                                                      final PublicKey driftSignerKey) {
    return List.of(
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(stateKey),
      createWrite(authorityKey),
      createWritableSigner(keeperKey),
      createRead(driftSignerKey)
    );
  }

  public static Instruction forceDeleteUser(final AccountMeta invokedDriftProgramMeta,
                                            final PublicKey userKey,
                                            final PublicKey userStatsKey,
                                            final PublicKey stateKey,
                                            final PublicKey authorityKey,
                                            final PublicKey keeperKey,
                                            final PublicKey driftSignerKey) {
    final var keys = forceDeleteUserKeys(
      invokedDriftProgramMeta,
      userKey,
      userStatsKey,
      stateKey,
      authorityKey,
      keeperKey,
      driftSignerKey
    );
    return forceDeleteUser(invokedDriftProgramMeta, keys);
  }

  public static Instruction forceDeleteUser(final AccountMeta invokedDriftProgramMeta                                            ,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, FORCE_DELETE_USER_DISCRIMINATOR);
  }

  public static final Discriminator DELETE_SIGNED_MSG_USER_ORDERS_DISCRIMINATOR = toDiscriminator(221, 247, 128, 253, 212, 254, 46, 153);

  public static List<AccountMeta> deleteSignedMsgUserOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey signedMsgUserOrdersKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey authorityKey) {
    return List.of(
      createWrite(signedMsgUserOrdersKey),
      createWrite(stateKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction deleteSignedMsgUserOrders(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey signedMsgUserOrdersKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey authorityKey) {
    final var keys = deleteSignedMsgUserOrdersKeys(
      invokedDriftProgramMeta,
      signedMsgUserOrdersKey,
      stateKey,
      authorityKey
    );
    return deleteSignedMsgUserOrders(invokedDriftProgramMeta, keys);
  }

  public static Instruction deleteSignedMsgUserOrders(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, DELETE_SIGNED_MSG_USER_ORDERS_DISCRIMINATOR);
  }

  public static final Discriminator RECLAIM_RENT_DISCRIMINATOR = toDiscriminator(218, 200, 19, 197, 227, 89, 192, 22);

  public static List<AccountMeta> reclaimRentKeys(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey stateKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey rentKey) {
    return List.of(
      createWrite(userKey),
      createWrite(userStatsKey),
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createRead(rentKey)
    );
  }

  public static Instruction reclaimRent(final AccountMeta invokedDriftProgramMeta,
                                        final PublicKey userKey,
                                        final PublicKey userStatsKey,
                                        final PublicKey stateKey,
                                        final PublicKey authorityKey,
                                        final PublicKey rentKey) {
    final var keys = reclaimRentKeys(
      invokedDriftProgramMeta,
      userKey,
      userStatsKey,
      stateKey,
      authorityKey,
      rentKey
    );
    return reclaimRent(invokedDriftProgramMeta, keys);
  }

  public static Instruction reclaimRent(final AccountMeta invokedDriftProgramMeta                                        ,
                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, RECLAIM_RENT_DISCRIMINATOR);
  }

  public static final Discriminator ENABLE_USER_HIGH_LEVERAGE_MODE_DISCRIMINATOR = toDiscriminator(231, 24, 230, 112, 201, 173, 73, 184);

  public static List<AccountMeta> enableUserHighLeverageModeKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey userKey,
                                                                 final PublicKey authorityKey,
                                                                 final PublicKey highLeverageModeConfigKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey),
      createWrite(highLeverageModeConfigKey)
    );
  }

  public static Instruction enableUserHighLeverageMode(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey stateKey,
                                                       final PublicKey userKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey highLeverageModeConfigKey,
                                                       final int subAccountId) {
    final var keys = enableUserHighLeverageModeKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey,
      highLeverageModeConfigKey
    );
    return enableUserHighLeverageMode(invokedDriftProgramMeta, keys, subAccountId);
  }

  public static Instruction enableUserHighLeverageMode(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final int subAccountId) {
    final byte[] _data = new byte[10];
    int i = ENABLE_USER_HIGH_LEVERAGE_MODE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, subAccountId);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record EnableUserHighLeverageModeIxData(Discriminator discriminator, int subAccountId) implements Borsh {  

    public static EnableUserHighLeverageModeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static EnableUserHighLeverageModeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var subAccountId = getInt16LE(_data, i);
      return new EnableUserHighLeverageModeIxData(discriminator, subAccountId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, subAccountId);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FILL_PERP_ORDER_DISCRIMINATOR = toDiscriminator(13, 188, 248, 103, 134, 217, 106, 240);

  public static List<AccountMeta> fillPerpOrderKeys(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final PublicKey stateKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey fillerKey,
                                                    final PublicKey fillerStatsKey,
                                                    final PublicKey userKey,
                                                    final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(fillerKey),
      createWrite(fillerStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction fillPerpOrder(final AccountMeta invokedDriftProgramMeta,
                                          final PublicKey stateKey,
                                          final PublicKey authorityKey,
                                          final PublicKey fillerKey,
                                          final PublicKey fillerStatsKey,
                                          final PublicKey userKey,
                                          final PublicKey userStatsKey,
                                          final OptionalInt orderId,
                                          final OptionalInt makerOrderId) {
    final var keys = fillPerpOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      fillerKey,
      fillerStatsKey,
      userKey,
      userStatsKey
    );
    return fillPerpOrder(invokedDriftProgramMeta, keys, orderId, makerOrderId);
  }

  public static Instruction fillPerpOrder(final AccountMeta invokedDriftProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final OptionalInt orderId,
                                          final OptionalInt makerOrderId) {
    final byte[] _data = new byte[
    8
    + (orderId == null || orderId.isEmpty() ? 1 : 5)
    + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : 5)
    ];
    int i = FILL_PERP_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(orderId, _data, i);
    Borsh.writeOptional(makerOrderId, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record FillPerpOrderIxData(Discriminator discriminator, OptionalInt orderId, OptionalInt makerOrderId) implements Borsh {  

    public static FillPerpOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static FillPerpOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt orderId;
      if (_data[i] == 0) {
        orderId = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        orderId = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final OptionalInt makerOrderId;
      if (_data[i] == 0) {
        makerOrderId = OptionalInt.empty();
      } else {
        ++i;
      ;
        makerOrderId = OptionalInt.of(getInt32LE(_data, i));
      }
      return new FillPerpOrderIxData(discriminator, orderId, makerOrderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(orderId, _data, i);
      i += Borsh.writeOptional(makerOrderId, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (orderId == null || orderId.isEmpty() ? 1 : (1 + 4)) + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator REVERT_FILL_DISCRIMINATOR = toDiscriminator(236, 238, 176, 69, 239, 10, 181, 193);

  public static List<AccountMeta> revertFillKeys(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final PublicKey stateKey,
                                                 final PublicKey authorityKey,
                                                 final PublicKey fillerKey,
                                                 final PublicKey fillerStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(fillerKey),
      createWrite(fillerStatsKey)
    );
  }

  public static Instruction revertFill(final AccountMeta invokedDriftProgramMeta,
                                       final PublicKey stateKey,
                                       final PublicKey authorityKey,
                                       final PublicKey fillerKey,
                                       final PublicKey fillerStatsKey) {
    final var keys = revertFillKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      fillerKey,
      fillerStatsKey
    );
    return revertFill(invokedDriftProgramMeta, keys);
  }

  public static Instruction revertFill(final AccountMeta invokedDriftProgramMeta                                       ,
                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, REVERT_FILL_DISCRIMINATOR);
  }

  public static final Discriminator FILL_SPOT_ORDER_DISCRIMINATOR = toDiscriminator(212, 206, 130, 173, 21, 34, 199, 40);

  public static List<AccountMeta> fillSpotOrderKeys(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final PublicKey stateKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey fillerKey,
                                                    final PublicKey fillerStatsKey,
                                                    final PublicKey userKey,
                                                    final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(fillerKey),
      createWrite(fillerStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction fillSpotOrder(final AccountMeta invokedDriftProgramMeta,
                                          final PublicKey stateKey,
                                          final PublicKey authorityKey,
                                          final PublicKey fillerKey,
                                          final PublicKey fillerStatsKey,
                                          final PublicKey userKey,
                                          final PublicKey userStatsKey,
                                          final OptionalInt orderId,
                                          final SpotFulfillmentType fulfillmentType,
                                          final OptionalInt makerOrderId) {
    final var keys = fillSpotOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      fillerKey,
      fillerStatsKey,
      userKey,
      userStatsKey
    );
    return fillSpotOrder(
      invokedDriftProgramMeta,
      keys,
      orderId,
      fulfillmentType,
      makerOrderId
    );
  }

  public static Instruction fillSpotOrder(final AccountMeta invokedDriftProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final OptionalInt orderId,
                                          final SpotFulfillmentType fulfillmentType,
                                          final OptionalInt makerOrderId) {
    final byte[] _data = new byte[
    8
    + (orderId == null || orderId.isEmpty() ? 1 : 5)
    + (fulfillmentType == null ? 1 : (1 + Borsh.len(fulfillmentType)))
    + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : 5)
    ];
    int i = FILL_SPOT_ORDER_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(orderId, _data, i);
    i += Borsh.writeOptional(fulfillmentType, _data, i);
    Borsh.writeOptional(makerOrderId, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record FillSpotOrderIxData(Discriminator discriminator,
                                    OptionalInt orderId,
                                    SpotFulfillmentType fulfillmentType,
                                    OptionalInt makerOrderId) implements Borsh {  

    public static FillSpotOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static FillSpotOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt orderId;
      if (_data[i] == 0) {
        orderId = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        orderId = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final SpotFulfillmentType fulfillmentType;
      if (_data[i] == 0) {
        fulfillmentType = null;
        ++i;
      ;
      } else {
        ++i;
      ;
        fulfillmentType = SpotFulfillmentType.read(_data, i);
        i += Borsh.len(fulfillmentType);
      }
      final OptionalInt makerOrderId;
      if (_data[i] == 0) {
        makerOrderId = OptionalInt.empty();
      } else {
        ++i;
      ;
        makerOrderId = OptionalInt.of(getInt32LE(_data, i));
      }
      return new FillSpotOrderIxData(discriminator, orderId, fulfillmentType, makerOrderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(orderId, _data, i);
      i += Borsh.writeOptional(fulfillmentType, _data, i);
      i += Borsh.writeOptional(makerOrderId, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (orderId == null || orderId.isEmpty() ? 1 : (1 + 4)) + (fulfillmentType == null ? 1 : (1 + Borsh.len(fulfillmentType))) + (makerOrderId == null || makerOrderId.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator TRIGGER_ORDER_DISCRIMINATOR = toDiscriminator(63, 112, 51, 233, 232, 47, 240, 199);

  public static List<AccountMeta> triggerOrderKeys(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final PublicKey stateKey,
                                                   final PublicKey authorityKey,
                                                   final PublicKey fillerKey,
                                                   final PublicKey userKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(fillerKey),
      createWrite(userKey)
    );
  }

  public static Instruction triggerOrder(final AccountMeta invokedDriftProgramMeta,
                                         final PublicKey stateKey,
                                         final PublicKey authorityKey,
                                         final PublicKey fillerKey,
                                         final PublicKey userKey,
                                         final int orderId) {
    final var keys = triggerOrderKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      fillerKey,
      userKey
    );
    return triggerOrder(invokedDriftProgramMeta, keys, orderId);
  }

  public static Instruction triggerOrder(final AccountMeta invokedDriftProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final int orderId) {
    final byte[] _data = new byte[12];
    int i = TRIGGER_ORDER_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, orderId);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record TriggerOrderIxData(Discriminator discriminator, int orderId) implements Borsh {  

    public static TriggerOrderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static TriggerOrderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderId = getInt32LE(_data, i);
      return new TriggerOrderIxData(discriminator, orderId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, orderId);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator FORCE_CANCEL_ORDERS_DISCRIMINATOR = toDiscriminator(64, 181, 196, 63, 222, 72, 64, 232);

  public static List<AccountMeta> forceCancelOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final PublicKey stateKey,
                                                        final PublicKey authorityKey,
                                                        final PublicKey fillerKey,
                                                        final PublicKey userKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(fillerKey),
      createWrite(userKey)
    );
  }

  public static Instruction forceCancelOrders(final AccountMeta invokedDriftProgramMeta,
                                              final PublicKey stateKey,
                                              final PublicKey authorityKey,
                                              final PublicKey fillerKey,
                                              final PublicKey userKey) {
    final var keys = forceCancelOrdersKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      fillerKey,
      userKey
    );
    return forceCancelOrders(invokedDriftProgramMeta, keys);
  }

  public static Instruction forceCancelOrders(final AccountMeta invokedDriftProgramMeta                                              ,
                                              final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, FORCE_CANCEL_ORDERS_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_USER_IDLE_DISCRIMINATOR = toDiscriminator(253, 133, 67, 22, 103, 161, 20, 100);

  public static List<AccountMeta> updateUserIdleKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey stateKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey fillerKey,
                                                     final PublicKey userKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(fillerKey),
      createWrite(userKey)
    );
  }

  public static Instruction updateUserIdle(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey stateKey,
                                           final PublicKey authorityKey,
                                           final PublicKey fillerKey,
                                           final PublicKey userKey) {
    final var keys = updateUserIdleKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      fillerKey,
      userKey
    );
    return updateUserIdle(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateUserIdle(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_USER_IDLE_DISCRIMINATOR);
  }

  public static final Discriminator LOG_USER_BALANCES_DISCRIMINATOR = toDiscriminator(162, 21, 35, 251, 32, 57, 161, 210);

  public static List<AccountMeta> logUserBalancesKeys(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final PublicKey stateKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey userKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(userKey)
    );
  }

  public static Instruction logUserBalances(final AccountMeta invokedDriftProgramMeta,
                                            final PublicKey stateKey,
                                            final PublicKey authorityKey,
                                            final PublicKey userKey) {
    final var keys = logUserBalancesKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      userKey
    );
    return logUserBalances(invokedDriftProgramMeta, keys);
  }

  public static Instruction logUserBalances(final AccountMeta invokedDriftProgramMeta                                            ,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, LOG_USER_BALANCES_DISCRIMINATOR);
  }

  public static final Discriminator DISABLE_USER_HIGH_LEVERAGE_MODE_DISCRIMINATOR = toDiscriminator(183, 155, 45, 0, 226, 85, 213, 69);

  public static List<AccountMeta> disableUserHighLeverageModeKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey authorityKey,
                                                                  final PublicKey userKey,
                                                                  final PublicKey highLeverageModeConfigKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(userKey),
      createWrite(highLeverageModeConfigKey)
    );
  }

  public static Instruction disableUserHighLeverageMode(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey stateKey,
                                                        final PublicKey authorityKey,
                                                        final PublicKey userKey,
                                                        final PublicKey highLeverageModeConfigKey,
                                                        final boolean disableMaintenance) {
    final var keys = disableUserHighLeverageModeKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      userKey,
      highLeverageModeConfigKey
    );
    return disableUserHighLeverageMode(invokedDriftProgramMeta, keys, disableMaintenance);
  }

  public static Instruction disableUserHighLeverageMode(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final boolean disableMaintenance) {
    final byte[] _data = new byte[9];
    int i = DISABLE_USER_HIGH_LEVERAGE_MODE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (disableMaintenance ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DisableUserHighLeverageModeIxData(Discriminator discriminator, boolean disableMaintenance) implements Borsh {  

    public static DisableUserHighLeverageModeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static DisableUserHighLeverageModeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var disableMaintenance = _data[i] == 1;
      return new DisableUserHighLeverageModeIxData(discriminator, disableMaintenance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (disableMaintenance ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_FUEL_BONUS_DISCRIMINATOR = toDiscriminator(88, 175, 201, 190, 222, 100, 143, 57);

  public static List<AccountMeta> updateUserFuelBonusKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey stateKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey userKey,
                                                          final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction updateUserFuelBonus(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey stateKey,
                                                final PublicKey authorityKey,
                                                final PublicKey userKey,
                                                final PublicKey userStatsKey) {
    final var keys = updateUserFuelBonusKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      userKey,
      userStatsKey
    );
    return updateUserFuelBonus(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateUserFuelBonus(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_USER_FUEL_BONUS_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_USER_STATS_REFERRER_STATUS_DISCRIMINATOR = toDiscriminator(174, 154, 72, 42, 191, 148, 145, 205);

  public static List<AccountMeta> updateUserStatsReferrerStatusKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey authorityKey,
                                                                    final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction updateUserStatsReferrerStatus(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey stateKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey userStatsKey) {
    final var keys = updateUserStatsReferrerStatusKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      userStatsKey
    );
    return updateUserStatsReferrerStatus(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateUserStatsReferrerStatus(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_USER_STATS_REFERRER_STATUS_DISCRIMINATOR);
  }

  public static final Discriminator ADMIN_DISABLE_UPDATE_PERP_BID_ASK_TWAP_DISCRIMINATOR = toDiscriminator(17, 164, 82, 45, 183, 86, 191, 199);

  public static List<AccountMeta> adminDisableUpdatePerpBidAskTwapKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey userStatsKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction adminDisableUpdatePerpBidAskTwap(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey userStatsKey,
                                                             final boolean disable) {
    final var keys = adminDisableUpdatePerpBidAskTwapKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      userStatsKey
    );
    return adminDisableUpdatePerpBidAskTwap(invokedDriftProgramMeta, keys, disable);
  }

  public static Instruction adminDisableUpdatePerpBidAskTwap(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final boolean disable) {
    final byte[] _data = new byte[9];
    int i = ADMIN_DISABLE_UPDATE_PERP_BID_ASK_TWAP_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (disable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record AdminDisableUpdatePerpBidAskTwapIxData(Discriminator discriminator, boolean disable) implements Borsh {  

    public static AdminDisableUpdatePerpBidAskTwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static AdminDisableUpdatePerpBidAskTwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var disable = _data[i] == 1;
      return new AdminDisableUpdatePerpBidAskTwapIxData(discriminator, disable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (disable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SETTLE_PNL_DISCRIMINATOR = toDiscriminator(43, 61, 234, 45, 15, 95, 152, 153);

  public static List<AccountMeta> settlePnlKeys(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final PublicKey stateKey,
                                                final PublicKey userKey,
                                                final PublicKey authorityKey,
                                                final PublicKey spotMarketVaultKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey),
      createRead(spotMarketVaultKey)
    );
  }

  public static Instruction settlePnl(final AccountMeta invokedDriftProgramMeta,
                                      final PublicKey stateKey,
                                      final PublicKey userKey,
                                      final PublicKey authorityKey,
                                      final PublicKey spotMarketVaultKey,
                                      final int marketIndex) {
    final var keys = settlePnlKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey,
      spotMarketVaultKey
    );
    return settlePnl(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction settlePnl(final AccountMeta invokedDriftProgramMeta                                      ,
                                      final List<AccountMeta> keys,
                                      final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = SETTLE_PNL_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record SettlePnlIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static SettlePnlIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static SettlePnlIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new SettlePnlIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SETTLE_MULTIPLE_PNLS_DISCRIMINATOR = toDiscriminator(127, 66, 117, 57, 40, 50, 152, 127);

  public static List<AccountMeta> settleMultiplePnlsKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey stateKey,
                                                         final PublicKey userKey,
                                                         final PublicKey authorityKey,
                                                         final PublicKey spotMarketVaultKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey),
      createRead(spotMarketVaultKey)
    );
  }

  public static Instruction settleMultiplePnls(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey stateKey,
                                               final PublicKey userKey,
                                               final PublicKey authorityKey,
                                               final PublicKey spotMarketVaultKey,
                                               final short[] marketIndexes,
                                               final SettlePnlMode mode) {
    final var keys = settleMultiplePnlsKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey,
      spotMarketVaultKey
    );
    return settleMultiplePnls(invokedDriftProgramMeta, keys, marketIndexes, mode);
  }

  public static Instruction settleMultiplePnls(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final short[] marketIndexes,
                                               final SettlePnlMode mode) {
    final byte[] _data = new byte[8 + Borsh.lenVector(marketIndexes) + Borsh.len(mode)];
    int i = SETTLE_MULTIPLE_PNLS_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(marketIndexes, _data, i);
    Borsh.write(mode, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record SettleMultiplePnlsIxData(Discriminator discriminator, short[] marketIndexes, SettlePnlMode mode) implements Borsh {  

    public static SettleMultiplePnlsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static SettleMultiplePnlsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndexes = Borsh.readshortVector(_data, i);
      i += Borsh.lenVector(marketIndexes);
      final var mode = SettlePnlMode.read(_data, i);
      return new SettleMultiplePnlsIxData(discriminator, marketIndexes, mode);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(marketIndexes, _data, i);
      i += Borsh.write(mode, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(marketIndexes) + Borsh.len(mode);
    }
  }

  public static final Discriminator SETTLE_FUNDING_PAYMENT_DISCRIMINATOR = toDiscriminator(222, 90, 202, 94, 28, 45, 115, 183);

  public static List<AccountMeta> settleFundingPaymentKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey stateKey,
                                                           final PublicKey userKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey)
    );
  }

  public static Instruction settleFundingPayment(final AccountMeta invokedDriftProgramMeta, final PublicKey stateKey, final PublicKey userKey) {     final var keys = settleFundingPaymentKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey
    );
    return settleFundingPayment(invokedDriftProgramMeta, keys);
  }

  public static Instruction settleFundingPayment(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, SETTLE_FUNDING_PAYMENT_DISCRIMINATOR);
  }

  public static final Discriminator SETTLE_EXPIRED_MARKET_DISCRIMINATOR = toDiscriminator(120, 89, 11, 25, 122, 77, 72, 193);

  public static List<AccountMeta> settleExpiredMarketKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction settleExpiredMarket(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey adminKey,
                                                final PublicKey stateKey,
                                                final PublicKey perpMarketKey,
                                                final int marketIndex) {
    final var keys = settleExpiredMarketKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return settleExpiredMarket(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction settleExpiredMarket(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys,
                                                final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = SETTLE_EXPIRED_MARKET_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record SettleExpiredMarketIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static SettleExpiredMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static SettleExpiredMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new SettleExpiredMarketIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_PERP_DISCRIMINATOR = toDiscriminator(75, 35, 119, 247, 191, 18, 139, 2);

  public static List<AccountMeta> liquidatePerpKeys(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final PublicKey stateKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey liquidatorKey,
                                                    final PublicKey liquidatorStatsKey,
                                                    final PublicKey userKey,
                                                    final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction liquidatePerp(final AccountMeta invokedDriftProgramMeta,
                                          final PublicKey stateKey,
                                          final PublicKey authorityKey,
                                          final PublicKey liquidatorKey,
                                          final PublicKey liquidatorStatsKey,
                                          final PublicKey userKey,
                                          final PublicKey userStatsKey,
                                          final int marketIndex,
                                          final long liquidatorMaxBaseAssetAmount,
                                          final OptionalLong limitPrice) {
    final var keys = liquidatePerpKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey
    );
    return liquidatePerp(
      invokedDriftProgramMeta,
      keys,
      marketIndex,
      liquidatorMaxBaseAssetAmount,
      limitPrice
    );
  }

  public static Instruction liquidatePerp(final AccountMeta invokedDriftProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final int marketIndex,
                                          final long liquidatorMaxBaseAssetAmount,
                                          final OptionalLong limitPrice) {
    final byte[] _data = new byte[
    18
    + (limitPrice == null || limitPrice.isEmpty() ? 1 : 9)
    ];
    int i = LIQUIDATE_PERP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, liquidatorMaxBaseAssetAmount);
    i += 8;
    Borsh.writeOptional(limitPrice, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidatePerpIxData(Discriminator discriminator,
                                    int marketIndex,
                                    long liquidatorMaxBaseAssetAmount,
                                    OptionalLong limitPrice) implements Borsh {  

    public static LiquidatePerpIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static LiquidatePerpIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var liquidatorMaxBaseAssetAmount = getInt64LE(_data, i);
      i += 8;
      final OptionalLong limitPrice;
      if (_data[i] == 0) {
        limitPrice = OptionalLong.empty();
      } else {
        ++i;
      ;
        limitPrice = OptionalLong.of(getInt64LE(_data, i));
      }
      return new LiquidatePerpIxData(discriminator, marketIndex, liquidatorMaxBaseAssetAmount, limitPrice);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, liquidatorMaxBaseAssetAmount);
      i += 8;
      i += Borsh.writeOptional(limitPrice, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + 8 + (limitPrice == null || limitPrice.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator LIQUIDATE_PERP_WITH_FILL_DISCRIMINATOR = toDiscriminator(95, 111, 124, 105, 86, 169, 187, 34);

  public static List<AccountMeta> liquidatePerpWithFillKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey liquidatorKey,
                                                            final PublicKey liquidatorStatsKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction liquidatePerpWithFill(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey liquidatorKey,
                                                  final PublicKey liquidatorStatsKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final int marketIndex) {
    final var keys = liquidatePerpWithFillKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey
    );
    return liquidatePerpWithFill(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction liquidatePerpWithFill(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = LIQUIDATE_PERP_WITH_FILL_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidatePerpWithFillIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static LiquidatePerpWithFillIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static LiquidatePerpWithFillIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new LiquidatePerpWithFillIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_SPOT_DISCRIMINATOR = toDiscriminator(107, 0, 128, 41, 35, 229, 251, 18);

  public static List<AccountMeta> liquidateSpotKeys(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final PublicKey stateKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey liquidatorKey,
                                                    final PublicKey liquidatorStatsKey,
                                                    final PublicKey userKey,
                                                    final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction liquidateSpot(final AccountMeta invokedDriftProgramMeta,
                                          final PublicKey stateKey,
                                          final PublicKey authorityKey,
                                          final PublicKey liquidatorKey,
                                          final PublicKey liquidatorStatsKey,
                                          final PublicKey userKey,
                                          final PublicKey userStatsKey,
                                          final int assetMarketIndex,
                                          final int liabilityMarketIndex,
                                          final BigInteger liquidatorMaxLiabilityTransfer,
                                          final OptionalLong limitPrice) {
    final var keys = liquidateSpotKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey
    );
    return liquidateSpot(
      invokedDriftProgramMeta,
      keys,
      assetMarketIndex,
      liabilityMarketIndex,
      liquidatorMaxLiabilityTransfer,
      limitPrice
    );
  }

  public static Instruction liquidateSpot(final AccountMeta invokedDriftProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final int assetMarketIndex,
                                          final int liabilityMarketIndex,
                                          final BigInteger liquidatorMaxLiabilityTransfer,
                                          final OptionalLong limitPrice) {
    final byte[] _data = new byte[
    28
    + (limitPrice == null || limitPrice.isEmpty() ? 1 : 9)
    ];
    int i = LIQUIDATE_SPOT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, assetMarketIndex);
    i += 2;
    putInt16LE(_data, i, liabilityMarketIndex);
    i += 2;
    putInt128LE(_data, i, liquidatorMaxLiabilityTransfer);
    i += 16;
    Borsh.writeOptional(limitPrice, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidateSpotIxData(Discriminator discriminator,
                                    int assetMarketIndex,
                                    int liabilityMarketIndex,
                                    BigInteger liquidatorMaxLiabilityTransfer,
                                    OptionalLong limitPrice) implements Borsh {  

    public static LiquidateSpotIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static LiquidateSpotIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var assetMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var liabilityMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var liquidatorMaxLiabilityTransfer = getInt128LE(_data, i);
      i += 16;
      final OptionalLong limitPrice;
      if (_data[i] == 0) {
        limitPrice = OptionalLong.empty();
      } else {
        ++i;
      ;
        limitPrice = OptionalLong.of(getInt64LE(_data, i));
      }
      return new LiquidateSpotIxData(discriminator,
                                     assetMarketIndex,
                                     liabilityMarketIndex,
                                     liquidatorMaxLiabilityTransfer,
                                     limitPrice);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, assetMarketIndex);
      i += 2;
      putInt16LE(_data, i, liabilityMarketIndex);
      i += 2;
      putInt128LE(_data, i, liquidatorMaxLiabilityTransfer);
      i += 16;
      i += Borsh.writeOptional(limitPrice, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + 2 + 16 + (limitPrice == null || limitPrice.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator LIQUIDATE_SPOT_WITH_SWAP_BEGIN_DISCRIMINATOR = toDiscriminator(12, 43, 176, 83, 156, 251, 117, 13);

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> liquidateSpotWithSwapBeginKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey authorityKey,
                                                                 final PublicKey liquidatorKey,
                                                                 final PublicKey liquidatorStatsKey,
                                                                 final PublicKey userKey,
                                                                 final PublicKey userStatsKey,
                                                                 final PublicKey liabilitySpotMarketVaultKey,
                                                                 final PublicKey assetSpotMarketVaultKey,
                                                                 final PublicKey liabilityTokenAccountKey,
                                                                 final PublicKey assetTokenAccountKey,
                                                                 final PublicKey tokenProgramKey,
                                                                 final PublicKey driftSignerKey,
                                                                 final PublicKey instructionsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(liabilitySpotMarketVaultKey),
      createWrite(assetSpotMarketVaultKey),
      createWrite(liabilityTokenAccountKey),
      createWrite(assetTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey),
      createRead(instructionsKey)
    );
  }

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction liquidateSpotWithSwapBegin(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey stateKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey liquidatorKey,
                                                       final PublicKey liquidatorStatsKey,
                                                       final PublicKey userKey,
                                                       final PublicKey userStatsKey,
                                                       final PublicKey liabilitySpotMarketVaultKey,
                                                       final PublicKey assetSpotMarketVaultKey,
                                                       final PublicKey liabilityTokenAccountKey,
                                                       final PublicKey assetTokenAccountKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey driftSignerKey,
                                                       final PublicKey instructionsKey,
                                                       final int assetMarketIndex,
                                                       final int liabilityMarketIndex,
                                                       final long swapAmount) {
    final var keys = liquidateSpotWithSwapBeginKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey,
      liabilitySpotMarketVaultKey,
      assetSpotMarketVaultKey,
      liabilityTokenAccountKey,
      assetTokenAccountKey,
      tokenProgramKey,
      driftSignerKey,
      instructionsKey
    );
    return liquidateSpotWithSwapBegin(
      invokedDriftProgramMeta,
      keys,
      assetMarketIndex,
      liabilityMarketIndex,
      swapAmount
    );
  }

  public static Instruction liquidateSpotWithSwapBegin(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final int assetMarketIndex,
                                                       final int liabilityMarketIndex,
                                                       final long swapAmount) {
    final byte[] _data = new byte[20];
    int i = LIQUIDATE_SPOT_WITH_SWAP_BEGIN_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, assetMarketIndex);
    i += 2;
    putInt16LE(_data, i, liabilityMarketIndex);
    i += 2;
    putInt64LE(_data, i, swapAmount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidateSpotWithSwapBeginIxData(Discriminator discriminator,
                                                 int assetMarketIndex,
                                                 int liabilityMarketIndex,
                                                 long swapAmount) implements Borsh {  

    public static LiquidateSpotWithSwapBeginIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 20;

    public static LiquidateSpotWithSwapBeginIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var assetMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var liabilityMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var swapAmount = getInt64LE(_data, i);
      return new LiquidateSpotWithSwapBeginIxData(discriminator, assetMarketIndex, liabilityMarketIndex, swapAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, assetMarketIndex);
      i += 2;
      putInt16LE(_data, i, liabilityMarketIndex);
      i += 2;
      putInt64LE(_data, i, swapAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_SPOT_WITH_SWAP_END_DISCRIMINATOR = toDiscriminator(142, 88, 163, 160, 223, 75, 55, 225);

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> liquidateSpotWithSwapEndKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey stateKey,
                                                               final PublicKey authorityKey,
                                                               final PublicKey liquidatorKey,
                                                               final PublicKey liquidatorStatsKey,
                                                               final PublicKey userKey,
                                                               final PublicKey userStatsKey,
                                                               final PublicKey liabilitySpotMarketVaultKey,
                                                               final PublicKey assetSpotMarketVaultKey,
                                                               final PublicKey liabilityTokenAccountKey,
                                                               final PublicKey assetTokenAccountKey,
                                                               final PublicKey tokenProgramKey,
                                                               final PublicKey driftSignerKey,
                                                               final PublicKey instructionsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(liabilitySpotMarketVaultKey),
      createWrite(assetSpotMarketVaultKey),
      createWrite(liabilityTokenAccountKey),
      createWrite(assetTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey),
      createRead(instructionsKey)
    );
  }

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction liquidateSpotWithSwapEnd(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey stateKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey liquidatorKey,
                                                     final PublicKey liquidatorStatsKey,
                                                     final PublicKey userKey,
                                                     final PublicKey userStatsKey,
                                                     final PublicKey liabilitySpotMarketVaultKey,
                                                     final PublicKey assetSpotMarketVaultKey,
                                                     final PublicKey liabilityTokenAccountKey,
                                                     final PublicKey assetTokenAccountKey,
                                                     final PublicKey tokenProgramKey,
                                                     final PublicKey driftSignerKey,
                                                     final PublicKey instructionsKey,
                                                     final int assetMarketIndex,
                                                     final int liabilityMarketIndex) {
    final var keys = liquidateSpotWithSwapEndKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey,
      liabilitySpotMarketVaultKey,
      assetSpotMarketVaultKey,
      liabilityTokenAccountKey,
      assetTokenAccountKey,
      tokenProgramKey,
      driftSignerKey,
      instructionsKey
    );
    return liquidateSpotWithSwapEnd(invokedDriftProgramMeta, keys, assetMarketIndex, liabilityMarketIndex);
  }

  public static Instruction liquidateSpotWithSwapEnd(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final int assetMarketIndex,
                                                     final int liabilityMarketIndex) {
    final byte[] _data = new byte[12];
    int i = LIQUIDATE_SPOT_WITH_SWAP_END_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, assetMarketIndex);
    i += 2;
    putInt16LE(_data, i, liabilityMarketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidateSpotWithSwapEndIxData(Discriminator discriminator, int assetMarketIndex, int liabilityMarketIndex) implements Borsh {  

    public static LiquidateSpotWithSwapEndIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static LiquidateSpotWithSwapEndIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var assetMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var liabilityMarketIndex = getInt16LE(_data, i);
      return new LiquidateSpotWithSwapEndIxData(discriminator, assetMarketIndex, liabilityMarketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, assetMarketIndex);
      i += 2;
      putInt16LE(_data, i, liabilityMarketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LIQUIDATE_BORROW_FOR_PERP_PNL_DISCRIMINATOR = toDiscriminator(169, 17, 32, 90, 207, 148, 209, 27);

  public static List<AccountMeta> liquidateBorrowForPerpPnlKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey stateKey,
                                                                final PublicKey authorityKey,
                                                                final PublicKey liquidatorKey,
                                                                final PublicKey liquidatorStatsKey,
                                                                final PublicKey userKey,
                                                                final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction liquidateBorrowForPerpPnl(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey stateKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey liquidatorKey,
                                                      final PublicKey liquidatorStatsKey,
                                                      final PublicKey userKey,
                                                      final PublicKey userStatsKey,
                                                      final int perpMarketIndex,
                                                      final int spotMarketIndex,
                                                      final BigInteger liquidatorMaxLiabilityTransfer,
                                                      final OptionalLong limitPrice) {
    final var keys = liquidateBorrowForPerpPnlKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey
    );
    return liquidateBorrowForPerpPnl(
      invokedDriftProgramMeta,
      keys,
      perpMarketIndex,
      spotMarketIndex,
      liquidatorMaxLiabilityTransfer,
      limitPrice
    );
  }

  public static Instruction liquidateBorrowForPerpPnl(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int perpMarketIndex,
                                                      final int spotMarketIndex,
                                                      final BigInteger liquidatorMaxLiabilityTransfer,
                                                      final OptionalLong limitPrice) {
    final byte[] _data = new byte[
    28
    + (limitPrice == null || limitPrice.isEmpty() ? 1 : 9)
    ];
    int i = LIQUIDATE_BORROW_FOR_PERP_PNL_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt128LE(_data, i, liquidatorMaxLiabilityTransfer);
    i += 16;
    Borsh.writeOptional(limitPrice, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidateBorrowForPerpPnlIxData(Discriminator discriminator,
                                                int perpMarketIndex,
                                                int spotMarketIndex,
                                                BigInteger liquidatorMaxLiabilityTransfer,
                                                OptionalLong limitPrice) implements Borsh {  

    public static LiquidateBorrowForPerpPnlIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static LiquidateBorrowForPerpPnlIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var perpMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var spotMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var liquidatorMaxLiabilityTransfer = getInt128LE(_data, i);
      i += 16;
      final OptionalLong limitPrice;
      if (_data[i] == 0) {
        limitPrice = OptionalLong.empty();
      } else {
        ++i;
      ;
        limitPrice = OptionalLong.of(getInt64LE(_data, i));
      }
      return new LiquidateBorrowForPerpPnlIxData(discriminator,
                                                 perpMarketIndex,
                                                 spotMarketIndex,
                                                 liquidatorMaxLiabilityTransfer,
                                                 limitPrice);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, perpMarketIndex);
      i += 2;
      putInt16LE(_data, i, spotMarketIndex);
      i += 2;
      putInt128LE(_data, i, liquidatorMaxLiabilityTransfer);
      i += 16;
      i += Borsh.writeOptional(limitPrice, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + 2 + 16 + (limitPrice == null || limitPrice.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator LIQUIDATE_PERP_PNL_FOR_DEPOSIT_DISCRIMINATOR = toDiscriminator(237, 75, 198, 235, 233, 186, 75, 35);

  public static List<AccountMeta> liquidatePerpPnlForDepositKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey authorityKey,
                                                                 final PublicKey liquidatorKey,
                                                                 final PublicKey liquidatorStatsKey,
                                                                 final PublicKey userKey,
                                                                 final PublicKey userStatsKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction liquidatePerpPnlForDeposit(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey stateKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey liquidatorKey,
                                                       final PublicKey liquidatorStatsKey,
                                                       final PublicKey userKey,
                                                       final PublicKey userStatsKey,
                                                       final int perpMarketIndex,
                                                       final int spotMarketIndex,
                                                       final BigInteger liquidatorMaxPnlTransfer,
                                                       final OptionalLong limitPrice) {
    final var keys = liquidatePerpPnlForDepositKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey
    );
    return liquidatePerpPnlForDeposit(
      invokedDriftProgramMeta,
      keys,
      perpMarketIndex,
      spotMarketIndex,
      liquidatorMaxPnlTransfer,
      limitPrice
    );
  }

  public static Instruction liquidatePerpPnlForDeposit(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final int perpMarketIndex,
                                                       final int spotMarketIndex,
                                                       final BigInteger liquidatorMaxPnlTransfer,
                                                       final OptionalLong limitPrice) {
    final byte[] _data = new byte[
    28
    + (limitPrice == null || limitPrice.isEmpty() ? 1 : 9)
    ];
    int i = LIQUIDATE_PERP_PNL_FOR_DEPOSIT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt128LE(_data, i, liquidatorMaxPnlTransfer);
    i += 16;
    Borsh.writeOptional(limitPrice, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LiquidatePerpPnlForDepositIxData(Discriminator discriminator,
                                                 int perpMarketIndex,
                                                 int spotMarketIndex,
                                                 BigInteger liquidatorMaxPnlTransfer,
                                                 OptionalLong limitPrice) implements Borsh {  

    public static LiquidatePerpPnlForDepositIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static LiquidatePerpPnlForDepositIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var perpMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var spotMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var liquidatorMaxPnlTransfer = getInt128LE(_data, i);
      i += 16;
      final OptionalLong limitPrice;
      if (_data[i] == 0) {
        limitPrice = OptionalLong.empty();
      } else {
        ++i;
      ;
        limitPrice = OptionalLong.of(getInt64LE(_data, i));
      }
      return new LiquidatePerpPnlForDepositIxData(discriminator,
                                                  perpMarketIndex,
                                                  spotMarketIndex,
                                                  liquidatorMaxPnlTransfer,
                                                  limitPrice);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, perpMarketIndex);
      i += 2;
      putInt16LE(_data, i, spotMarketIndex);
      i += 2;
      putInt128LE(_data, i, liquidatorMaxPnlTransfer);
      i += 16;
      i += Borsh.writeOptional(limitPrice, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + 2 + 16 + (limitPrice == null || limitPrice.isEmpty() ? 1 : (1 + 8));
    }
  }

  public static final Discriminator SET_USER_STATUS_TO_BEING_LIQUIDATED_DISCRIMINATOR = toDiscriminator(106, 133, 160, 206, 193, 171, 192, 194);

  public static List<AccountMeta> setUserStatusToBeingLiquidatedKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey userKey,
                                                                     final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction setUserStatusToBeingLiquidated(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey stateKey,
                                                           final PublicKey userKey,
                                                           final PublicKey authorityKey) {
    final var keys = setUserStatusToBeingLiquidatedKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      authorityKey
    );
    return setUserStatusToBeingLiquidated(invokedDriftProgramMeta, keys);
  }

  public static Instruction setUserStatusToBeingLiquidated(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, SET_USER_STATUS_TO_BEING_LIQUIDATED_DISCRIMINATOR);
  }

  public static final Discriminator RESOLVE_PERP_PNL_DEFICIT_DISCRIMINATOR = toDiscriminator(168, 204, 68, 150, 159, 126, 95, 148);

  public static List<AccountMeta> resolvePerpPnlDeficitKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey spotMarketVaultKey,
                                                            final PublicKey insuranceFundVaultKey,
                                                            final PublicKey driftSignerKey,
                                                            final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction resolvePerpPnlDeficit(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey spotMarketVaultKey,
                                                  final PublicKey insuranceFundVaultKey,
                                                  final PublicKey driftSignerKey,
                                                  final PublicKey tokenProgramKey,
                                                  final int spotMarketIndex,
                                                  final int perpMarketIndex) {
    final var keys = resolvePerpPnlDeficitKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      driftSignerKey,
      tokenProgramKey
    );
    return resolvePerpPnlDeficit(invokedDriftProgramMeta, keys, spotMarketIndex, perpMarketIndex);
  }

  public static Instruction resolvePerpPnlDeficit(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int spotMarketIndex,
                                                  final int perpMarketIndex) {
    final byte[] _data = new byte[12];
    int i = RESOLVE_PERP_PNL_DEFICIT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt16LE(_data, i, perpMarketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ResolvePerpPnlDeficitIxData(Discriminator discriminator, int spotMarketIndex, int perpMarketIndex) implements Borsh {  

    public static ResolvePerpPnlDeficitIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static ResolvePerpPnlDeficitIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var spotMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var perpMarketIndex = getInt16LE(_data, i);
      return new ResolvePerpPnlDeficitIxData(discriminator, spotMarketIndex, perpMarketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, spotMarketIndex);
      i += 2;
      putInt16LE(_data, i, perpMarketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RESOLVE_PERP_BANKRUPTCY_DISCRIMINATOR = toDiscriminator(224, 16, 176, 214, 162, 213, 183, 222);

  public static List<AccountMeta> resolvePerpBankruptcyKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey liquidatorKey,
                                                            final PublicKey liquidatorStatsKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey spotMarketVaultKey,
                                                            final PublicKey insuranceFundVaultKey,
                                                            final PublicKey driftSignerKey,
                                                            final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction resolvePerpBankruptcy(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey liquidatorKey,
                                                  final PublicKey liquidatorStatsKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey spotMarketVaultKey,
                                                  final PublicKey insuranceFundVaultKey,
                                                  final PublicKey driftSignerKey,
                                                  final PublicKey tokenProgramKey,
                                                  final int quoteSpotMarketIndex,
                                                  final int marketIndex) {
    final var keys = resolvePerpBankruptcyKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      driftSignerKey,
      tokenProgramKey
    );
    return resolvePerpBankruptcy(invokedDriftProgramMeta, keys, quoteSpotMarketIndex, marketIndex);
  }

  public static Instruction resolvePerpBankruptcy(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int quoteSpotMarketIndex,
                                                  final int marketIndex) {
    final byte[] _data = new byte[12];
    int i = RESOLVE_PERP_BANKRUPTCY_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, quoteSpotMarketIndex);
    i += 2;
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ResolvePerpBankruptcyIxData(Discriminator discriminator, int quoteSpotMarketIndex, int marketIndex) implements Borsh {  

    public static ResolvePerpBankruptcyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static ResolvePerpBankruptcyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var quoteSpotMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var marketIndex = getInt16LE(_data, i);
      return new ResolvePerpBankruptcyIxData(discriminator, quoteSpotMarketIndex, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, quoteSpotMarketIndex);
      i += 2;
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RESOLVE_SPOT_BANKRUPTCY_DISCRIMINATOR = toDiscriminator(124, 194, 240, 254, 198, 213, 52, 122);

  public static List<AccountMeta> resolveSpotBankruptcyKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey liquidatorKey,
                                                            final PublicKey liquidatorStatsKey,
                                                            final PublicKey userKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey spotMarketVaultKey,
                                                            final PublicKey insuranceFundVaultKey,
                                                            final PublicKey driftSignerKey,
                                                            final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWrite(liquidatorKey),
      createWrite(liquidatorStatsKey),
      createWrite(userKey),
      createWrite(userStatsKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction resolveSpotBankruptcy(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey liquidatorKey,
                                                  final PublicKey liquidatorStatsKey,
                                                  final PublicKey userKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey spotMarketVaultKey,
                                                  final PublicKey insuranceFundVaultKey,
                                                  final PublicKey driftSignerKey,
                                                  final PublicKey tokenProgramKey,
                                                  final int marketIndex) {
    final var keys = resolveSpotBankruptcyKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      liquidatorKey,
      liquidatorStatsKey,
      userKey,
      userStatsKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      driftSignerKey,
      tokenProgramKey
    );
    return resolveSpotBankruptcy(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction resolveSpotBankruptcy(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = RESOLVE_SPOT_BANKRUPTCY_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ResolveSpotBankruptcyIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static ResolveSpotBankruptcyIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static ResolveSpotBankruptcyIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new ResolveSpotBankruptcyIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SETTLE_REVENUE_TO_INSURANCE_FUND_DISCRIMINATOR = toDiscriminator(200, 120, 93, 136, 69, 38, 199, 159);

  public static List<AccountMeta> settleRevenueToInsuranceFundKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey spotMarketKey,
                                                                   final PublicKey spotMarketVaultKey,
                                                                   final PublicKey driftSignerKey,
                                                                   final PublicKey insuranceFundVaultKey,
                                                                   final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createWrite(spotMarketVaultKey),
      createRead(driftSignerKey),
      createWrite(insuranceFundVaultKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction settleRevenueToInsuranceFund(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey stateKey,
                                                         final PublicKey spotMarketKey,
                                                         final PublicKey spotMarketVaultKey,
                                                         final PublicKey driftSignerKey,
                                                         final PublicKey insuranceFundVaultKey,
                                                         final PublicKey tokenProgramKey,
                                                         final int spotMarketIndex) {
    final var keys = settleRevenueToInsuranceFundKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      spotMarketVaultKey,
      driftSignerKey,
      insuranceFundVaultKey,
      tokenProgramKey
    );
    return settleRevenueToInsuranceFund(invokedDriftProgramMeta, keys, spotMarketIndex);
  }

  public static Instruction settleRevenueToInsuranceFund(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final int spotMarketIndex) {
    final byte[] _data = new byte[10];
    int i = SETTLE_REVENUE_TO_INSURANCE_FUND_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, spotMarketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record SettleRevenueToInsuranceFundIxData(Discriminator discriminator, int spotMarketIndex) implements Borsh {  

    public static SettleRevenueToInsuranceFundIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static SettleRevenueToInsuranceFundIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var spotMarketIndex = getInt16LE(_data, i);
      return new SettleRevenueToInsuranceFundIxData(discriminator, spotMarketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, spotMarketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FUNDING_RATE_DISCRIMINATOR = toDiscriminator(201, 178, 116, 212, 166, 144, 72, 238);

  public static List<AccountMeta> updateFundingRateKeys(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final PublicKey stateKey,
                                                        final PublicKey perpMarketKey,
                                                        final PublicKey oracleKey) {
    return List.of(
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey)
    );
  }

  public static Instruction updateFundingRate(final AccountMeta invokedDriftProgramMeta,
                                              final PublicKey stateKey,
                                              final PublicKey perpMarketKey,
                                              final PublicKey oracleKey,
                                              final int marketIndex) {
    final var keys = updateFundingRateKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      oracleKey
    );
    return updateFundingRate(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction updateFundingRate(final AccountMeta invokedDriftProgramMeta                                              ,
                                              final List<AccountMeta> keys,
                                              final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = UPDATE_FUNDING_RATE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFundingRateIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static UpdateFundingRateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateFundingRateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new UpdateFundingRateIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PRELAUNCH_ORACLE_DISCRIMINATOR = toDiscriminator(220, 132, 27, 27, 233, 220, 61, 219);

  public static List<AccountMeta> updatePrelaunchOracleKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey perpMarketKey,
                                                            final PublicKey oracleKey) {
    return List.of(
      createRead(stateKey),
      createRead(perpMarketKey),
      createWrite(oracleKey)
    );
  }

  public static Instruction updatePrelaunchOracle(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey perpMarketKey,
                                                  final PublicKey oracleKey) {
    final var keys = updatePrelaunchOracleKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      oracleKey
    );
    return updatePrelaunchOracle(invokedDriftProgramMeta, keys);
  }

  public static Instruction updatePrelaunchOracle(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_PRELAUNCH_ORACLE_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_PERP_BID_ASK_TWAP_DISCRIMINATOR = toDiscriminator(247, 23, 255, 65, 212, 90, 221, 194);

  public static List<AccountMeta> updatePerpBidAskTwapKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey stateKey,
                                                           final PublicKey perpMarketKey,
                                                           final PublicKey oracleKey,
                                                           final PublicKey keeperStatsKey,
                                                           final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey),
      createRead(keeperStatsKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updatePerpBidAskTwap(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey stateKey,
                                                 final PublicKey perpMarketKey,
                                                 final PublicKey oracleKey,
                                                 final PublicKey keeperStatsKey,
                                                 final PublicKey authorityKey) {
    final var keys = updatePerpBidAskTwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      oracleKey,
      keeperStatsKey,
      authorityKey
    );
    return updatePerpBidAskTwap(invokedDriftProgramMeta, keys);
  }

  public static Instruction updatePerpBidAskTwap(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_PERP_BID_ASK_TWAP_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_SPOT_MARKET_CUMULATIVE_INTEREST_DISCRIMINATOR = toDiscriminator(39, 166, 139, 243, 158, 165, 155, 225);

  public static List<AccountMeta> updateSpotMarketCumulativeInterestKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey spotMarketKey,
                                                                         final PublicKey oracleKey,
                                                                         final PublicKey spotMarketVaultKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createRead(oracleKey),
      createRead(spotMarketVaultKey)
    );
  }

  public static Instruction updateSpotMarketCumulativeInterest(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey stateKey,
                                                               final PublicKey spotMarketKey,
                                                               final PublicKey oracleKey,
                                                               final PublicKey spotMarketVaultKey) {
    final var keys = updateSpotMarketCumulativeInterestKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      oracleKey,
      spotMarketVaultKey
    );
    return updateSpotMarketCumulativeInterest(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateSpotMarketCumulativeInterest(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_SPOT_MARKET_CUMULATIVE_INTEREST_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_AMMS_DISCRIMINATOR = toDiscriminator(201, 106, 217, 253, 4, 175, 228, 97);

  public static List<AccountMeta> updateAmmsKeys(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final PublicKey stateKey,
                                                 final PublicKey authorityKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(authorityKey)
    );
  }

  public static Instruction updateAmms(final AccountMeta invokedDriftProgramMeta,
                                       final PublicKey stateKey,
                                       final PublicKey authorityKey,
                                       final short[] marketIndexes) {
    final var keys = updateAmmsKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey
    );
    return updateAmms(invokedDriftProgramMeta, keys, marketIndexes);
  }

  public static Instruction updateAmms(final AccountMeta invokedDriftProgramMeta                                       ,
                                       final List<AccountMeta> keys,
                                       final short[] marketIndexes) {
    final byte[] _data = new byte[8 + Borsh.lenVector(marketIndexes)];
    int i = UPDATE_AMMS_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(marketIndexes, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateAmmsIxData(Discriminator discriminator, short[] marketIndexes) implements Borsh {  

    public static UpdateAmmsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateAmmsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndexes = Borsh.readshortVector(_data, i);
      return new UpdateAmmsIxData(discriminator, marketIndexes);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(marketIndexes, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(marketIndexes);
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_EXPIRY_DISCRIMINATOR = toDiscriminator(208, 11, 211, 159, 226, 24, 11, 247);

  public static List<AccountMeta> updateSpotMarketExpiryKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketExpiry(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey spotMarketKey,
                                                   final long expiryTs) {
    final var keys = updateSpotMarketExpiryKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketExpiry(invokedDriftProgramMeta, keys, expiryTs);
  }

  public static Instruction updateSpotMarketExpiry(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final long expiryTs) {
    final byte[] _data = new byte[16];
    int i = UPDATE_SPOT_MARKET_EXPIRY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, expiryTs);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketExpiryIxData(Discriminator discriminator, long expiryTs) implements Borsh {  

    public static UpdateSpotMarketExpiryIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateSpotMarketExpiryIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var expiryTs = getInt64LE(_data, i);
      return new UpdateSpotMarketExpiryIxData(discriminator, expiryTs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, expiryTs);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_USER_QUOTE_ASSET_INSURANCE_STAKE_DISCRIMINATOR = toDiscriminator(251, 101, 156, 7, 2, 63, 30, 23);

  public static List<AccountMeta> updateUserQuoteAssetInsuranceStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey spotMarketKey,
                                                                         final PublicKey insuranceFundStakeKey,
                                                                         final PublicKey userStatsKey,
                                                                         final PublicKey signerKey,
                                                                         final PublicKey insuranceFundVaultKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(signerKey),
      createWrite(insuranceFundVaultKey)
    );
  }

  public static Instruction updateUserQuoteAssetInsuranceStake(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey stateKey,
                                                               final PublicKey spotMarketKey,
                                                               final PublicKey insuranceFundStakeKey,
                                                               final PublicKey userStatsKey,
                                                               final PublicKey signerKey,
                                                               final PublicKey insuranceFundVaultKey) {
    final var keys = updateUserQuoteAssetInsuranceStakeKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      signerKey,
      insuranceFundVaultKey
    );
    return updateUserQuoteAssetInsuranceStake(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateUserQuoteAssetInsuranceStake(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_USER_QUOTE_ASSET_INSURANCE_STAKE_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_USER_GOV_TOKEN_INSURANCE_STAKE_DISCRIMINATOR = toDiscriminator(143, 99, 235, 187, 20, 159, 184, 84);

  public static List<AccountMeta> updateUserGovTokenInsuranceStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey spotMarketKey,
                                                                       final PublicKey insuranceFundStakeKey,
                                                                       final PublicKey userStatsKey,
                                                                       final PublicKey signerKey,
                                                                       final PublicKey insuranceFundVaultKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(signerKey),
      createWrite(insuranceFundVaultKey)
    );
  }

  public static Instruction updateUserGovTokenInsuranceStake(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey,
                                                             final PublicKey insuranceFundStakeKey,
                                                             final PublicKey userStatsKey,
                                                             final PublicKey signerKey,
                                                             final PublicKey insuranceFundVaultKey) {
    final var keys = updateUserGovTokenInsuranceStakeKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      signerKey,
      insuranceFundVaultKey
    );
    return updateUserGovTokenInsuranceStake(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateUserGovTokenInsuranceStake(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_USER_GOV_TOKEN_INSURANCE_STAKE_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_DELEGATE_USER_GOV_TOKEN_INSURANCE_STAKE_DISCRIMINATOR = toDiscriminator(241, 29, 215, 228, 142, 116, 22, 160);

  public static List<AccountMeta> updateDelegateUserGovTokenInsuranceStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                               ,
                                                                               final PublicKey spotMarketKey,
                                                                               final PublicKey insuranceFundStakeKey,
                                                                               final PublicKey userStatsKey,
                                                                               final PublicKey adminKey,
                                                                               final PublicKey insuranceFundVaultKey,
                                                                               final PublicKey stateKey) {
    return List.of(
      createWrite(spotMarketKey),
      createRead(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(adminKey),
      createWrite(insuranceFundVaultKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateDelegateUserGovTokenInsuranceStake(final AccountMeta invokedDriftProgramMeta,
                                                                     final PublicKey spotMarketKey,
                                                                     final PublicKey insuranceFundStakeKey,
                                                                     final PublicKey userStatsKey,
                                                                     final PublicKey adminKey,
                                                                     final PublicKey insuranceFundVaultKey,
                                                                     final PublicKey stateKey) {
    final var keys = updateDelegateUserGovTokenInsuranceStakeKeys(
      invokedDriftProgramMeta,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      adminKey,
      insuranceFundVaultKey,
      stateKey
    );
    return updateDelegateUserGovTokenInsuranceStake(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateDelegateUserGovTokenInsuranceStake(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_DELEGATE_USER_GOV_TOKEN_INSURANCE_STAKE_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_INSURANCE_FUND_STAKE_DISCRIMINATOR = toDiscriminator(187, 179, 243, 70, 248, 90, 92, 147);

  public static List<AccountMeta> initializeInsuranceFundStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey spotMarketKey,
                                                                   final PublicKey insuranceFundStakeKey,
                                                                   final PublicKey userStatsKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey authorityKey,
                                                                   final PublicKey payerKey,
                                                                   final PublicKey rentKey,
                                                                   final PublicKey systemProgramKey) {
    return List.of(
      createRead(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createRead(stateKey),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeInsuranceFundStake(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey spotMarketKey,
                                                         final PublicKey insuranceFundStakeKey,
                                                         final PublicKey userStatsKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey authorityKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey rentKey,
                                                         final PublicKey systemProgramKey,
                                                         final int marketIndex) {
    final var keys = initializeInsuranceFundStakeKeys(
      invokedDriftProgramMeta,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      stateKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeInsuranceFundStake(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction initializeInsuranceFundStake(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = INITIALIZE_INSURANCE_FUND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeInsuranceFundStakeIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static InitializeInsuranceFundStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitializeInsuranceFundStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new InitializeInsuranceFundStakeIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ADD_INSURANCE_FUND_STAKE_DISCRIMINATOR = toDiscriminator(251, 144, 115, 11, 222, 47, 62, 236);

  public static List<AccountMeta> addInsuranceFundStakeKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey spotMarketKey,
                                                            final PublicKey insuranceFundStakeKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey spotMarketVaultKey,
                                                            final PublicKey insuranceFundVaultKey,
                                                            final PublicKey driftSignerKey,
                                                            final PublicKey userTokenAccountKey,
                                                            final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction addInsuranceFundStake(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey spotMarketKey,
                                                  final PublicKey insuranceFundStakeKey,
                                                  final PublicKey userStatsKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey spotMarketVaultKey,
                                                  final PublicKey insuranceFundVaultKey,
                                                  final PublicKey driftSignerKey,
                                                  final PublicKey userTokenAccountKey,
                                                  final PublicKey tokenProgramKey,
                                                  final int marketIndex,
                                                  final long amount) {
    final var keys = addInsuranceFundStakeKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      authorityKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      driftSignerKey,
      userTokenAccountKey,
      tokenProgramKey
    );
    return addInsuranceFundStake(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction addInsuranceFundStake(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int marketIndex,
                                                  final long amount) {
    final byte[] _data = new byte[18];
    int i = ADD_INSURANCE_FUND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record AddInsuranceFundStakeIxData(Discriminator discriminator, int marketIndex, long amount) implements Borsh {  

    public static AddInsuranceFundStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static AddInsuranceFundStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      return new AddInsuranceFundStakeIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REQUEST_REMOVE_INSURANCE_FUND_STAKE_DISCRIMINATOR = toDiscriminator(142, 70, 204, 92, 73, 106, 180, 52);

  public static List<AccountMeta> requestRemoveInsuranceFundStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey spotMarketKey,
                                                                      final PublicKey insuranceFundStakeKey,
                                                                      final PublicKey userStatsKey,
                                                                      final PublicKey authorityKey,
                                                                      final PublicKey insuranceFundVaultKey) {
    return List.of(
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(insuranceFundVaultKey)
    );
  }

  public static Instruction requestRemoveInsuranceFundStake(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey spotMarketKey,
                                                            final PublicKey insuranceFundStakeKey,
                                                            final PublicKey userStatsKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey insuranceFundVaultKey,
                                                            final int marketIndex,
                                                            final long amount) {
    final var keys = requestRemoveInsuranceFundStakeKeys(
      invokedDriftProgramMeta,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      authorityKey,
      insuranceFundVaultKey
    );
    return requestRemoveInsuranceFundStake(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction requestRemoveInsuranceFundStake(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final int marketIndex,
                                                            final long amount) {
    final byte[] _data = new byte[18];
    int i = REQUEST_REMOVE_INSURANCE_FUND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record RequestRemoveInsuranceFundStakeIxData(Discriminator discriminator, int marketIndex, long amount) implements Borsh {  

    public static RequestRemoveInsuranceFundStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static RequestRemoveInsuranceFundStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      return new RequestRemoveInsuranceFundStakeIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CANCEL_REQUEST_REMOVE_INSURANCE_FUND_STAKE_DISCRIMINATOR = toDiscriminator(97, 235, 78, 62, 212, 42, 241, 127);

  public static List<AccountMeta> cancelRequestRemoveInsuranceFundStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final PublicKey spotMarketKey,
                                                                            final PublicKey insuranceFundStakeKey,
                                                                            final PublicKey userStatsKey,
                                                                            final PublicKey authorityKey,
                                                                            final PublicKey insuranceFundVaultKey) {
    return List.of(
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(insuranceFundVaultKey)
    );
  }

  public static Instruction cancelRequestRemoveInsuranceFundStake(final AccountMeta invokedDriftProgramMeta,
                                                                  final PublicKey spotMarketKey,
                                                                  final PublicKey insuranceFundStakeKey,
                                                                  final PublicKey userStatsKey,
                                                                  final PublicKey authorityKey,
                                                                  final PublicKey insuranceFundVaultKey,
                                                                  final int marketIndex) {
    final var keys = cancelRequestRemoveInsuranceFundStakeKeys(
      invokedDriftProgramMeta,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      authorityKey,
      insuranceFundVaultKey
    );
    return cancelRequestRemoveInsuranceFundStake(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction cancelRequestRemoveInsuranceFundStake(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final List<AccountMeta> keys,
                                                                  final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = CANCEL_REQUEST_REMOVE_INSURANCE_FUND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record CancelRequestRemoveInsuranceFundStakeIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static CancelRequestRemoveInsuranceFundStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static CancelRequestRemoveInsuranceFundStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new CancelRequestRemoveInsuranceFundStakeIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REMOVE_INSURANCE_FUND_STAKE_DISCRIMINATOR = toDiscriminator(128, 166, 142, 9, 254, 187, 143, 174);

  public static List<AccountMeta> removeInsuranceFundStakeKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey stateKey,
                                                               final PublicKey spotMarketKey,
                                                               final PublicKey insuranceFundStakeKey,
                                                               final PublicKey userStatsKey,
                                                               final PublicKey authorityKey,
                                                               final PublicKey insuranceFundVaultKey,
                                                               final PublicKey driftSignerKey,
                                                               final PublicKey userTokenAccountKey,
                                                               final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createReadOnlySigner(authorityKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction removeInsuranceFundStake(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey stateKey,
                                                     final PublicKey spotMarketKey,
                                                     final PublicKey insuranceFundStakeKey,
                                                     final PublicKey userStatsKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey insuranceFundVaultKey,
                                                     final PublicKey driftSignerKey,
                                                     final PublicKey userTokenAccountKey,
                                                     final PublicKey tokenProgramKey,
                                                     final int marketIndex) {
    final var keys = removeInsuranceFundStakeKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      authorityKey,
      insuranceFundVaultKey,
      driftSignerKey,
      userTokenAccountKey,
      tokenProgramKey
    );
    return removeInsuranceFundStake(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction removeInsuranceFundStake(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = REMOVE_INSURANCE_FUND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record RemoveInsuranceFundStakeIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static RemoveInsuranceFundStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static RemoveInsuranceFundStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new RemoveInsuranceFundStakeIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BEGIN_INSURANCE_FUND_SWAP_DISCRIMINATOR = toDiscriminator(176, 69, 143, 205, 32, 132, 163, 0);

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> beginInsuranceFundSwapKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey stateKey,
                                                             final PublicKey authorityKey,
                                                             final PublicKey outInsuranceFundVaultKey,
                                                             final PublicKey inInsuranceFundVaultKey,
                                                             final PublicKey outTokenAccountKey,
                                                             final PublicKey inTokenAccountKey,
                                                             final PublicKey ifRebalanceConfigKey,
                                                             final PublicKey tokenProgramKey,
                                                             final PublicKey driftSignerKey,
                                                             final PublicKey instructionsKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(authorityKey),
      createWrite(outInsuranceFundVaultKey),
      createWrite(inInsuranceFundVaultKey),
      createWrite(outTokenAccountKey),
      createWrite(inTokenAccountKey),
      createWrite(ifRebalanceConfigKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey),
      createRead(instructionsKey)
    );
  }

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction beginInsuranceFundSwap(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey stateKey,
                                                   final PublicKey authorityKey,
                                                   final PublicKey outInsuranceFundVaultKey,
                                                   final PublicKey inInsuranceFundVaultKey,
                                                   final PublicKey outTokenAccountKey,
                                                   final PublicKey inTokenAccountKey,
                                                   final PublicKey ifRebalanceConfigKey,
                                                   final PublicKey tokenProgramKey,
                                                   final PublicKey driftSignerKey,
                                                   final PublicKey instructionsKey,
                                                   final int inMarketIndex,
                                                   final int outMarketIndex,
                                                   final long amountIn) {
    final var keys = beginInsuranceFundSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      outInsuranceFundVaultKey,
      inInsuranceFundVaultKey,
      outTokenAccountKey,
      inTokenAccountKey,
      ifRebalanceConfigKey,
      tokenProgramKey,
      driftSignerKey,
      instructionsKey
    );
    return beginInsuranceFundSwap(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      outMarketIndex,
      amountIn
    );
  }

  public static Instruction beginInsuranceFundSwap(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final int inMarketIndex,
                                                   final int outMarketIndex,
                                                   final long amountIn) {
    final byte[] _data = new byte[20];
    int i = BEGIN_INSURANCE_FUND_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    putInt64LE(_data, i, amountIn);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record BeginInsuranceFundSwapIxData(Discriminator discriminator,
                                             int inMarketIndex,
                                             int outMarketIndex,
                                             long amountIn) implements Borsh {  

    public static BeginInsuranceFundSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 20;

    public static BeginInsuranceFundSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var amountIn = getInt64LE(_data, i);
      return new BeginInsuranceFundSwapIxData(discriminator, inMarketIndex, outMarketIndex, amountIn);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      putInt64LE(_data, i, amountIn);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator END_INSURANCE_FUND_SWAP_DISCRIMINATOR = toDiscriminator(206, 230, 98, 8, 249, 158, 169, 167);

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> endInsuranceFundSwapKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey stateKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey outInsuranceFundVaultKey,
                                                           final PublicKey inInsuranceFundVaultKey,
                                                           final PublicKey outTokenAccountKey,
                                                           final PublicKey inTokenAccountKey,
                                                           final PublicKey ifRebalanceConfigKey,
                                                           final PublicKey tokenProgramKey,
                                                           final PublicKey driftSignerKey,
                                                           final PublicKey instructionsKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(authorityKey),
      createWrite(outInsuranceFundVaultKey),
      createWrite(inInsuranceFundVaultKey),
      createWrite(outTokenAccountKey),
      createWrite(inTokenAccountKey),
      createWrite(ifRebalanceConfigKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey),
      createRead(instructionsKey)
    );
  }

  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction endInsuranceFundSwap(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey stateKey,
                                                 final PublicKey authorityKey,
                                                 final PublicKey outInsuranceFundVaultKey,
                                                 final PublicKey inInsuranceFundVaultKey,
                                                 final PublicKey outTokenAccountKey,
                                                 final PublicKey inTokenAccountKey,
                                                 final PublicKey ifRebalanceConfigKey,
                                                 final PublicKey tokenProgramKey,
                                                 final PublicKey driftSignerKey,
                                                 final PublicKey instructionsKey,
                                                 final int inMarketIndex,
                                                 final int outMarketIndex) {
    final var keys = endInsuranceFundSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      outInsuranceFundVaultKey,
      inInsuranceFundVaultKey,
      outTokenAccountKey,
      inTokenAccountKey,
      ifRebalanceConfigKey,
      tokenProgramKey,
      driftSignerKey,
      instructionsKey
    );
    return endInsuranceFundSwap(invokedDriftProgramMeta, keys, inMarketIndex, outMarketIndex);
  }

  public static Instruction endInsuranceFundSwap(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int inMarketIndex,
                                                 final int outMarketIndex) {
    final byte[] _data = new byte[12];
    int i = END_INSURANCE_FUND_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record EndInsuranceFundSwapIxData(Discriminator discriminator, int inMarketIndex, int outMarketIndex) implements Borsh {  

    public static EndInsuranceFundSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static EndInsuranceFundSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      return new EndInsuranceFundSwapIxData(discriminator, inMarketIndex, outMarketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator TRANSFER_PROTOCOL_IF_SHARES_TO_REVENUE_POOL_DISCRIMINATOR = toDiscriminator(236, 136, 147, 153, 146, 205, 104, 29);

  public static List<AccountMeta> transferProtocolIfSharesToRevenuePoolKeys(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final PublicKey stateKey,
                                                                            final PublicKey authorityKey,
                                                                            final PublicKey insuranceFundVaultKey,
                                                                            final PublicKey spotMarketVaultKey,
                                                                            final PublicKey ifRebalanceConfigKey,
                                                                            final PublicKey tokenProgramKey,
                                                                            final PublicKey driftSignerKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(authorityKey),
      createWrite(insuranceFundVaultKey),
      createWrite(spotMarketVaultKey),
      createWrite(ifRebalanceConfigKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey)
    );
  }

  public static Instruction transferProtocolIfSharesToRevenuePool(final AccountMeta invokedDriftProgramMeta,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey authorityKey,
                                                                  final PublicKey insuranceFundVaultKey,
                                                                  final PublicKey spotMarketVaultKey,
                                                                  final PublicKey ifRebalanceConfigKey,
                                                                  final PublicKey tokenProgramKey,
                                                                  final PublicKey driftSignerKey,
                                                                  final int marketIndex,
                                                                  final long amount) {
    final var keys = transferProtocolIfSharesToRevenuePoolKeys(
      invokedDriftProgramMeta,
      stateKey,
      authorityKey,
      insuranceFundVaultKey,
      spotMarketVaultKey,
      ifRebalanceConfigKey,
      tokenProgramKey,
      driftSignerKey
    );
    return transferProtocolIfSharesToRevenuePool(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction transferProtocolIfSharesToRevenuePool(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final List<AccountMeta> keys,
                                                                  final int marketIndex,
                                                                  final long amount) {
    final byte[] _data = new byte[18];
    int i = TRANSFER_PROTOCOL_IF_SHARES_TO_REVENUE_POOL_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record TransferProtocolIfSharesToRevenuePoolIxData(Discriminator discriminator, int marketIndex, long amount) implements Borsh {  

    public static TransferProtocolIfSharesToRevenuePoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static TransferProtocolIfSharesToRevenuePoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      return new TransferProtocolIfSharesToRevenuePoolIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_INTO_INSURANCE_FUND_STAKE_DISCRIMINATOR = toDiscriminator(4, 22, 226, 201, 124, 44, 82, 230);

  public static List<AccountMeta> depositIntoInsuranceFundStakeKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey signerKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey spotMarketKey,
                                                                    final PublicKey insuranceFundStakeKey,
                                                                    final PublicKey userStatsKey,
                                                                    final PublicKey spotMarketVaultKey,
                                                                    final PublicKey insuranceFundVaultKey,
                                                                    final PublicKey userTokenAccountKey,
                                                                    final PublicKey tokenProgramKey,
                                                                    final PublicKey driftSignerKey) {
    return List.of(
      createReadOnlySigner(signerKey),
      createWrite(stateKey),
      createWrite(spotMarketKey),
      createWrite(insuranceFundStakeKey),
      createWrite(userStatsKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey)
    );
  }

  public static Instruction depositIntoInsuranceFundStake(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey signerKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey spotMarketKey,
                                                          final PublicKey insuranceFundStakeKey,
                                                          final PublicKey userStatsKey,
                                                          final PublicKey spotMarketVaultKey,
                                                          final PublicKey insuranceFundVaultKey,
                                                          final PublicKey userTokenAccountKey,
                                                          final PublicKey tokenProgramKey,
                                                          final PublicKey driftSignerKey,
                                                          final int marketIndex,
                                                          final long amount) {
    final var keys = depositIntoInsuranceFundStakeKeys(
      invokedDriftProgramMeta,
      signerKey,
      stateKey,
      spotMarketKey,
      insuranceFundStakeKey,
      userStatsKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      userTokenAccountKey,
      tokenProgramKey,
      driftSignerKey
    );
    return depositIntoInsuranceFundStake(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction depositIntoInsuranceFundStake(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int marketIndex,
                                                          final long amount) {
    final byte[] _data = new byte[18];
    int i = DEPOSIT_INTO_INSURANCE_FUND_STAKE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DepositIntoInsuranceFundStakeIxData(Discriminator discriminator, int marketIndex, long amount) implements Borsh {  

    public static DepositIntoInsuranceFundStakeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static DepositIntoInsuranceFundStakeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      return new DepositIntoInsuranceFundStakeIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PYTH_PULL_ORACLE_DISCRIMINATOR = toDiscriminator(230, 191, 189, 94, 108, 59, 74, 197);

  public static List<AccountMeta> updatePythPullOracleKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey keeperKey,
                                                           final PublicKey pythSolanaReceiverKey,
                                                           final PublicKey encodedVaaKey,
                                                           final PublicKey priceFeedKey) {
    return List.of(
      createWritableSigner(keeperKey),
      createRead(pythSolanaReceiverKey),
      createRead(encodedVaaKey),
      createWrite(priceFeedKey)
    );
  }

  public static Instruction updatePythPullOracle(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey keeperKey,
                                                 final PublicKey pythSolanaReceiverKey,
                                                 final PublicKey encodedVaaKey,
                                                 final PublicKey priceFeedKey,
                                                 final byte[] feedId,
                                                 final byte[] params) {
    final var keys = updatePythPullOracleKeys(
      invokedDriftProgramMeta,
      keeperKey,
      pythSolanaReceiverKey,
      encodedVaaKey,
      priceFeedKey
    );
    return updatePythPullOracle(invokedDriftProgramMeta, keys, feedId, params);
  }

  public static Instruction updatePythPullOracle(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final byte[] feedId,
                                                 final byte[] params) {
    final byte[] _data = new byte[8 + Borsh.lenArray(feedId) + Borsh.lenVector(params)];
    int i = UPDATE_PYTH_PULL_ORACLE_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeArrayChecked(feedId, 32, _data, i);
    Borsh.writeVector(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePythPullOracleIxData(Discriminator discriminator, byte[] feedId, byte[] params) implements Borsh {  

    public static UpdatePythPullOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int FEED_ID_LEN = 32;
    public static UpdatePythPullOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feedId = new byte[32];
      i += Borsh.readArray(feedId, _data, i);
      final var params = Borsh.readbyteVector(_data, i);
      return new UpdatePythPullOracleIxData(discriminator, feedId, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(feedId, 32, _data, i);
      i += Borsh.writeVector(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenArray(feedId) + Borsh.lenVector(params);
    }
  }

  public static final Discriminator POST_PYTH_PULL_ORACLE_UPDATE_ATOMIC_DISCRIMINATOR = toDiscriminator(116, 122, 137, 158, 224, 195, 173, 119);

  public static List<AccountMeta> postPythPullOracleUpdateAtomicKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey keeperKey,
                                                                     final PublicKey pythSolanaReceiverKey,
                                                                     final PublicKey guardianSetKey,
                                                                     final PublicKey priceFeedKey) {
    return List.of(
      createWritableSigner(keeperKey),
      createRead(pythSolanaReceiverKey),
      createRead(guardianSetKey),
      createWrite(priceFeedKey)
    );
  }

  public static Instruction postPythPullOracleUpdateAtomic(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey keeperKey,
                                                           final PublicKey pythSolanaReceiverKey,
                                                           final PublicKey guardianSetKey,
                                                           final PublicKey priceFeedKey,
                                                           final byte[] feedId,
                                                           final byte[] params) {
    final var keys = postPythPullOracleUpdateAtomicKeys(
      invokedDriftProgramMeta,
      keeperKey,
      pythSolanaReceiverKey,
      guardianSetKey,
      priceFeedKey
    );
    return postPythPullOracleUpdateAtomic(invokedDriftProgramMeta, keys, feedId, params);
  }

  public static Instruction postPythPullOracleUpdateAtomic(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final byte[] feedId,
                                                           final byte[] params) {
    final byte[] _data = new byte[8 + Borsh.lenArray(feedId) + Borsh.lenVector(params)];
    int i = POST_PYTH_PULL_ORACLE_UPDATE_ATOMIC_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeArrayChecked(feedId, 32, _data, i);
    Borsh.writeVector(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PostPythPullOracleUpdateAtomicIxData(Discriminator discriminator, byte[] feedId, byte[] params) implements Borsh {  

    public static PostPythPullOracleUpdateAtomicIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int FEED_ID_LEN = 32;
    public static PostPythPullOracleUpdateAtomicIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feedId = new byte[32];
      i += Borsh.readArray(feedId, _data, i);
      final var params = Borsh.readbyteVector(_data, i);
      return new PostPythPullOracleUpdateAtomicIxData(discriminator, feedId, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(feedId, 32, _data, i);
      i += Borsh.writeVector(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenArray(feedId) + Borsh.lenVector(params);
    }
  }

  public static final Discriminator POST_MULTI_PYTH_PULL_ORACLE_UPDATES_ATOMIC_DISCRIMINATOR = toDiscriminator(243, 79, 204, 228, 227, 208, 100, 244);

  public static List<AccountMeta> postMultiPythPullOracleUpdatesAtomicKeys(final AccountMeta invokedDriftProgramMeta                                                                           ,
                                                                           final PublicKey keeperKey,
                                                                           final PublicKey pythSolanaReceiverKey,
                                                                           final PublicKey guardianSetKey) {
    return List.of(
      createWritableSigner(keeperKey),
      createRead(pythSolanaReceiverKey),
      createRead(guardianSetKey)
    );
  }

  public static Instruction postMultiPythPullOracleUpdatesAtomic(final AccountMeta invokedDriftProgramMeta,
                                                                 final PublicKey keeperKey,
                                                                 final PublicKey pythSolanaReceiverKey,
                                                                 final PublicKey guardianSetKey,
                                                                 final byte[] params) {
    final var keys = postMultiPythPullOracleUpdatesAtomicKeys(
      invokedDriftProgramMeta,
      keeperKey,
      pythSolanaReceiverKey,
      guardianSetKey
    );
    return postMultiPythPullOracleUpdatesAtomic(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction postMultiPythPullOracleUpdatesAtomic(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final List<AccountMeta> keys,
                                                                 final byte[] params) {
    final byte[] _data = new byte[8 + Borsh.lenVector(params)];
    int i = POST_MULTI_PYTH_PULL_ORACLE_UPDATES_ATOMIC_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PostMultiPythPullOracleUpdatesAtomicIxData(Discriminator discriminator, byte[] params) implements Borsh {  

    public static PostMultiPythPullOracleUpdatesAtomicIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PostMultiPythPullOracleUpdatesAtomicIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = Borsh.readbyteVector(_data, i);
      return new PostMultiPythPullOracleUpdatesAtomicIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(params);
    }
  }

  public static final Discriminator PAUSE_SPOT_MARKET_DEPOSIT_WITHDRAW_DISCRIMINATOR = toDiscriminator(183, 119, 59, 170, 137, 35, 242, 86);

  public static List<AccountMeta> pauseSpotMarketDepositWithdrawKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey keeperKey,
                                                                     final PublicKey spotMarketKey,
                                                                     final PublicKey spotMarketVaultKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(keeperKey),
      createWrite(spotMarketKey),
      createRead(spotMarketVaultKey)
    );
  }

  public static Instruction pauseSpotMarketDepositWithdraw(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey stateKey,
                                                           final PublicKey keeperKey,
                                                           final PublicKey spotMarketKey,
                                                           final PublicKey spotMarketVaultKey) {
    final var keys = pauseSpotMarketDepositWithdrawKeys(
      invokedDriftProgramMeta,
      stateKey,
      keeperKey,
      spotMarketKey,
      spotMarketVaultKey
    );
    return pauseSpotMarketDepositWithdraw(invokedDriftProgramMeta, keys);
  }

  public static Instruction pauseSpotMarketDepositWithdraw(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, PAUSE_SPOT_MARKET_DEPOSIT_WITHDRAW_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_DISCRIMINATOR = toDiscriminator(175, 175, 109, 31, 13, 152, 155, 237);

  public static List<AccountMeta> initializeKeys(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey quoteAssetMintKey,
                                                 final PublicKey driftSignerKey,
                                                 final PublicKey rentKey,
                                                 final PublicKey systemProgramKey,
                                                 final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(stateKey),
      createRead(quoteAssetMintKey),
      createRead(driftSignerKey),
      createRead(rentKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction initialize(final AccountMeta invokedDriftProgramMeta,
                                       final PublicKey adminKey,
                                       final PublicKey stateKey,
                                       final PublicKey quoteAssetMintKey,
                                       final PublicKey driftSignerKey,
                                       final PublicKey rentKey,
                                       final PublicKey systemProgramKey,
                                       final PublicKey tokenProgramKey) {
    final var keys = initializeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      quoteAssetMintKey,
      driftSignerKey,
      rentKey,
      systemProgramKey,
      tokenProgramKey
    );
    return initialize(invokedDriftProgramMeta, keys);
  }

  public static Instruction initialize(final AccountMeta invokedDriftProgramMeta                                       ,
                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, INITIALIZE_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_SPOT_MARKET_DISCRIMINATOR = toDiscriminator(234, 196, 128, 44, 94, 15, 48, 201);

  public static List<AccountMeta> initializeSpotMarketKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey spotMarketKey,
                                                           final PublicKey spotMarketMintKey,
                                                           final PublicKey spotMarketVaultKey,
                                                           final PublicKey insuranceFundVaultKey,
                                                           final PublicKey driftSignerKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey oracleKey,
                                                           final PublicKey adminKey,
                                                           final PublicKey rentKey,
                                                           final PublicKey systemProgramKey,
                                                           final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(spotMarketKey),
      createRead(spotMarketMintKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createWrite(stateKey),
      createRead(oracleKey),
      createWritableSigner(adminKey),
      createRead(rentKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction initializeSpotMarket(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey spotMarketKey,
                                                 final PublicKey spotMarketMintKey,
                                                 final PublicKey spotMarketVaultKey,
                                                 final PublicKey insuranceFundVaultKey,
                                                 final PublicKey driftSignerKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey oracleKey,
                                                 final PublicKey adminKey,
                                                 final PublicKey rentKey,
                                                 final PublicKey systemProgramKey,
                                                 final PublicKey tokenProgramKey,
                                                 final int optimalUtilization,
                                                 final int optimalBorrowRate,
                                                 final int maxBorrowRate,
                                                 final OracleSource oracleSource,
                                                 final int initialAssetWeight,
                                                 final int maintenanceAssetWeight,
                                                 final int initialLiabilityWeight,
                                                 final int maintenanceLiabilityWeight,
                                                 final int imfFactor,
                                                 final int liquidatorFee,
                                                 final int ifLiquidationFee,
                                                 final boolean activeStatus,
                                                 final AssetTier assetTier,
                                                 final long scaleInitialAssetWeightStart,
                                                 final long withdrawGuardThreshold,
                                                 final long orderTickSize,
                                                 final long orderStepSize,
                                                 final int ifTotalFactor,
                                                 final byte[] name) {
    final var keys = initializeSpotMarketKeys(
      invokedDriftProgramMeta,
      spotMarketKey,
      spotMarketMintKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      driftSignerKey,
      stateKey,
      oracleKey,
      adminKey,
      rentKey,
      systemProgramKey,
      tokenProgramKey
    );
    return initializeSpotMarket(
      invokedDriftProgramMeta,
      keys,
      optimalUtilization,
      optimalBorrowRate,
      maxBorrowRate,
      oracleSource,
      initialAssetWeight,
      maintenanceAssetWeight,
      initialLiabilityWeight,
      maintenanceLiabilityWeight,
      imfFactor,
      liquidatorFee,
      ifLiquidationFee,
      activeStatus,
      assetTier,
      scaleInitialAssetWeightStart,
      withdrawGuardThreshold,
      orderTickSize,
      orderStepSize,
      ifTotalFactor,
      name
    );
  }

  public static Instruction initializeSpotMarket(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int optimalUtilization,
                                                 final int optimalBorrowRate,
                                                 final int maxBorrowRate,
                                                 final OracleSource oracleSource,
                                                 final int initialAssetWeight,
                                                 final int maintenanceAssetWeight,
                                                 final int initialLiabilityWeight,
                                                 final int maintenanceLiabilityWeight,
                                                 final int imfFactor,
                                                 final int liquidatorFee,
                                                 final int ifLiquidationFee,
                                                 final boolean activeStatus,
                                                 final AssetTier assetTier,
                                                 final long scaleInitialAssetWeightStart,
                                                 final long withdrawGuardThreshold,
                                                 final long orderTickSize,
                                                 final long orderStepSize,
                                                 final int ifTotalFactor,
                                                 final byte[] name) {
    final byte[] _data = new byte[85 + Borsh.len(oracleSource) + Borsh.len(assetTier) + Borsh.lenArray(name)];
    int i = INITIALIZE_SPOT_MARKET_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, optimalUtilization);
    i += 4;
    putInt32LE(_data, i, optimalBorrowRate);
    i += 4;
    putInt32LE(_data, i, maxBorrowRate);
    i += 4;
    i += Borsh.write(oracleSource, _data, i);
    putInt32LE(_data, i, initialAssetWeight);
    i += 4;
    putInt32LE(_data, i, maintenanceAssetWeight);
    i += 4;
    putInt32LE(_data, i, initialLiabilityWeight);
    i += 4;
    putInt32LE(_data, i, maintenanceLiabilityWeight);
    i += 4;
    putInt32LE(_data, i, imfFactor);
    i += 4;
    putInt32LE(_data, i, liquidatorFee);
    i += 4;
    putInt32LE(_data, i, ifLiquidationFee);
    i += 4;
    _data[i] = (byte) (activeStatus ? 1 : 0);
    ++i;
    i += Borsh.write(assetTier, _data, i);
    putInt64LE(_data, i, scaleInitialAssetWeightStart);
    i += 8;
    putInt64LE(_data, i, withdrawGuardThreshold);
    i += 8;
    putInt64LE(_data, i, orderTickSize);
    i += 8;
    putInt64LE(_data, i, orderStepSize);
    i += 8;
    putInt32LE(_data, i, ifTotalFactor);
    i += 4;
    Borsh.writeArrayChecked(name, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeSpotMarketIxData(Discriminator discriminator,
                                           int optimalUtilization,
                                           int optimalBorrowRate,
                                           int maxBorrowRate,
                                           OracleSource oracleSource,
                                           int initialAssetWeight,
                                           int maintenanceAssetWeight,
                                           int initialLiabilityWeight,
                                           int maintenanceLiabilityWeight,
                                           int imfFactor,
                                           int liquidatorFee,
                                           int ifLiquidationFee,
                                           boolean activeStatus,
                                           AssetTier assetTier,
                                           long scaleInitialAssetWeightStart,
                                           long withdrawGuardThreshold,
                                           long orderTickSize,
                                           long orderStepSize,
                                           int ifTotalFactor,
                                           byte[] name) implements Borsh {  

    public static InitializeSpotMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 119;
    public static final int NAME_LEN = 32;

    public static InitializeSpotMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var optimalUtilization = getInt32LE(_data, i);
      i += 4;
      final var optimalBorrowRate = getInt32LE(_data, i);
      i += 4;
      final var maxBorrowRate = getInt32LE(_data, i);
      i += 4;
      final var oracleSource = OracleSource.read(_data, i);
      i += Borsh.len(oracleSource);
      final var initialAssetWeight = getInt32LE(_data, i);
      i += 4;
      final var maintenanceAssetWeight = getInt32LE(_data, i);
      i += 4;
      final var initialLiabilityWeight = getInt32LE(_data, i);
      i += 4;
      final var maintenanceLiabilityWeight = getInt32LE(_data, i);
      i += 4;
      final var imfFactor = getInt32LE(_data, i);
      i += 4;
      final var liquidatorFee = getInt32LE(_data, i);
      i += 4;
      final var ifLiquidationFee = getInt32LE(_data, i);
      i += 4;
      final var activeStatus = _data[i] == 1;
      ++i;
      final var assetTier = AssetTier.read(_data, i);
      i += Borsh.len(assetTier);
      final var scaleInitialAssetWeightStart = getInt64LE(_data, i);
      i += 8;
      final var withdrawGuardThreshold = getInt64LE(_data, i);
      i += 8;
      final var orderTickSize = getInt64LE(_data, i);
      i += 8;
      final var orderStepSize = getInt64LE(_data, i);
      i += 8;
      final var ifTotalFactor = getInt32LE(_data, i);
      i += 4;
      final var name = new byte[32];
      Borsh.readArray(name, _data, i);
      return new InitializeSpotMarketIxData(discriminator,
                                            optimalUtilization,
                                            optimalBorrowRate,
                                            maxBorrowRate,
                                            oracleSource,
                                            initialAssetWeight,
                                            maintenanceAssetWeight,
                                            initialLiabilityWeight,
                                            maintenanceLiabilityWeight,
                                            imfFactor,
                                            liquidatorFee,
                                            ifLiquidationFee,
                                            activeStatus,
                                            assetTier,
                                            scaleInitialAssetWeightStart,
                                            withdrawGuardThreshold,
                                            orderTickSize,
                                            orderStepSize,
                                            ifTotalFactor,
                                            name);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, optimalUtilization);
      i += 4;
      putInt32LE(_data, i, optimalBorrowRate);
      i += 4;
      putInt32LE(_data, i, maxBorrowRate);
      i += 4;
      i += Borsh.write(oracleSource, _data, i);
      putInt32LE(_data, i, initialAssetWeight);
      i += 4;
      putInt32LE(_data, i, maintenanceAssetWeight);
      i += 4;
      putInt32LE(_data, i, initialLiabilityWeight);
      i += 4;
      putInt32LE(_data, i, maintenanceLiabilityWeight);
      i += 4;
      putInt32LE(_data, i, imfFactor);
      i += 4;
      putInt32LE(_data, i, liquidatorFee);
      i += 4;
      putInt32LE(_data, i, ifLiquidationFee);
      i += 4;
      _data[i] = (byte) (activeStatus ? 1 : 0);
      ++i;
      i += Borsh.write(assetTier, _data, i);
      putInt64LE(_data, i, scaleInitialAssetWeightStart);
      i += 8;
      putInt64LE(_data, i, withdrawGuardThreshold);
      i += 8;
      putInt64LE(_data, i, orderTickSize);
      i += 8;
      putInt64LE(_data, i, orderStepSize);
      i += 8;
      putInt32LE(_data, i, ifTotalFactor);
      i += 4;
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DELETE_INITIALIZED_SPOT_MARKET_DISCRIMINATOR = toDiscriminator(31, 140, 67, 191, 189, 20, 101, 221);

  public static List<AccountMeta> deleteInitializedSpotMarketKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey spotMarketKey,
                                                                  final PublicKey spotMarketVaultKey,
                                                                  final PublicKey insuranceFundVaultKey,
                                                                  final PublicKey driftSignerKey,
                                                                  final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(stateKey),
      createWrite(spotMarketKey),
      createWrite(spotMarketVaultKey),
      createWrite(insuranceFundVaultKey),
      createRead(driftSignerKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction deleteInitializedSpotMarket(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey adminKey,
                                                        final PublicKey stateKey,
                                                        final PublicKey spotMarketKey,
                                                        final PublicKey spotMarketVaultKey,
                                                        final PublicKey insuranceFundVaultKey,
                                                        final PublicKey driftSignerKey,
                                                        final PublicKey tokenProgramKey,
                                                        final int marketIndex) {
    final var keys = deleteInitializedSpotMarketKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey,
      spotMarketVaultKey,
      insuranceFundVaultKey,
      driftSignerKey,
      tokenProgramKey
    );
    return deleteInitializedSpotMarket(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction deleteInitializedSpotMarket(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = DELETE_INITIALIZED_SPOT_MARKET_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DeleteInitializedSpotMarketIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static DeleteInitializedSpotMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static DeleteInitializedSpotMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new DeleteInitializedSpotMarketIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_SERUM_FULFILLMENT_CONFIG_DISCRIMINATOR = toDiscriminator(193, 211, 132, 172, 70, 171, 7, 94);

  public static List<AccountMeta> initializeSerumFulfillmentConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey baseSpotMarketKey,
                                                                       final PublicKey quoteSpotMarketKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey serumProgramKey,
                                                                       final PublicKey serumMarketKey,
                                                                       final PublicKey serumOpenOrdersKey,
                                                                       final PublicKey driftSignerKey,
                                                                       final PublicKey serumFulfillmentConfigKey,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey rentKey,
                                                                       final PublicKey systemProgramKey) {
    return List.of(
      createRead(baseSpotMarketKey),
      createRead(quoteSpotMarketKey),
      createWrite(stateKey),
      createRead(serumProgramKey),
      createRead(serumMarketKey),
      createWrite(serumOpenOrdersKey),
      createRead(driftSignerKey),
      createWrite(serumFulfillmentConfigKey),
      createWritableSigner(adminKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeSerumFulfillmentConfig(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey baseSpotMarketKey,
                                                             final PublicKey quoteSpotMarketKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey serumProgramKey,
                                                             final PublicKey serumMarketKey,
                                                             final PublicKey serumOpenOrdersKey,
                                                             final PublicKey driftSignerKey,
                                                             final PublicKey serumFulfillmentConfigKey,
                                                             final PublicKey adminKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey,
                                                             final int marketIndex) {
    final var keys = initializeSerumFulfillmentConfigKeys(
      invokedDriftProgramMeta,
      baseSpotMarketKey,
      quoteSpotMarketKey,
      stateKey,
      serumProgramKey,
      serumMarketKey,
      serumOpenOrdersKey,
      driftSignerKey,
      serumFulfillmentConfigKey,
      adminKey,
      rentKey,
      systemProgramKey
    );
    return initializeSerumFulfillmentConfig(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction initializeSerumFulfillmentConfig(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = INITIALIZE_SERUM_FULFILLMENT_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeSerumFulfillmentConfigIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static InitializeSerumFulfillmentConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitializeSerumFulfillmentConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new InitializeSerumFulfillmentConfigIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SERUM_FULFILLMENT_CONFIG_STATUS_DISCRIMINATOR = toDiscriminator(171, 109, 240, 251, 95, 1, 149, 89);

  public static List<AccountMeta> updateSerumFulfillmentConfigStatusKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey serumFulfillmentConfigKey,
                                                                         final PublicKey adminKey) {
    return List.of(
      createRead(stateKey),
      createWrite(serumFulfillmentConfigKey),
      createWritableSigner(adminKey)
    );
  }

  public static Instruction updateSerumFulfillmentConfigStatus(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey stateKey,
                                                               final PublicKey serumFulfillmentConfigKey,
                                                               final PublicKey adminKey,
                                                               final SpotFulfillmentConfigStatus status) {
    final var keys = updateSerumFulfillmentConfigStatusKeys(
      invokedDriftProgramMeta,
      stateKey,
      serumFulfillmentConfigKey,
      adminKey
    );
    return updateSerumFulfillmentConfigStatus(invokedDriftProgramMeta, keys, status);
  }

  public static Instruction updateSerumFulfillmentConfigStatus(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys,
                                                               final SpotFulfillmentConfigStatus status) {
    final byte[] _data = new byte[8 + Borsh.len(status)];
    int i = UPDATE_SERUM_FULFILLMENT_CONFIG_STATUS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(status, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSerumFulfillmentConfigStatusIxData(Discriminator discriminator, SpotFulfillmentConfigStatus status) implements Borsh {  

    public static UpdateSerumFulfillmentConfigStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSerumFulfillmentConfigStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = SpotFulfillmentConfigStatus.read(_data, i);
      return new UpdateSerumFulfillmentConfigStatusIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(status, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_OPENBOOK_V_2_FULFILLMENT_CONFIG_DISCRIMINATOR = toDiscriminator(7, 221, 103, 153, 107, 57, 27, 197);

  public static List<AccountMeta> initializeOpenbookV2FulfillmentConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final PublicKey baseSpotMarketKey,
                                                                            final PublicKey quoteSpotMarketKey,
                                                                            final PublicKey stateKey,
                                                                            final PublicKey openbookV2ProgramKey,
                                                                            final PublicKey openbookV2MarketKey,
                                                                            final PublicKey driftSignerKey,
                                                                            final PublicKey openbookV2FulfillmentConfigKey,
                                                                            final PublicKey adminKey,
                                                                            final PublicKey rentKey,
                                                                            final PublicKey systemProgramKey) {
    return List.of(
      createRead(baseSpotMarketKey),
      createRead(quoteSpotMarketKey),
      createWrite(stateKey),
      createRead(openbookV2ProgramKey),
      createRead(openbookV2MarketKey),
      createRead(driftSignerKey),
      createWrite(openbookV2FulfillmentConfigKey),
      createWritableSigner(adminKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeOpenbookV2FulfillmentConfig(final AccountMeta invokedDriftProgramMeta,
                                                                  final PublicKey baseSpotMarketKey,
                                                                  final PublicKey quoteSpotMarketKey,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey openbookV2ProgramKey,
                                                                  final PublicKey openbookV2MarketKey,
                                                                  final PublicKey driftSignerKey,
                                                                  final PublicKey openbookV2FulfillmentConfigKey,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey rentKey,
                                                                  final PublicKey systemProgramKey,
                                                                  final int marketIndex) {
    final var keys = initializeOpenbookV2FulfillmentConfigKeys(
      invokedDriftProgramMeta,
      baseSpotMarketKey,
      quoteSpotMarketKey,
      stateKey,
      openbookV2ProgramKey,
      openbookV2MarketKey,
      driftSignerKey,
      openbookV2FulfillmentConfigKey,
      adminKey,
      rentKey,
      systemProgramKey
    );
    return initializeOpenbookV2FulfillmentConfig(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction initializeOpenbookV2FulfillmentConfig(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final List<AccountMeta> keys,
                                                                  final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = INITIALIZE_OPENBOOK_V_2_FULFILLMENT_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeOpenbookV2FulfillmentConfigIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static InitializeOpenbookV2FulfillmentConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitializeOpenbookV2FulfillmentConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new InitializeOpenbookV2FulfillmentConfigIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator OPENBOOK_V_2_FULFILLMENT_CONFIG_STATUS_DISCRIMINATOR = toDiscriminator(25, 173, 19, 189, 4, 211, 64, 238);

  public static List<AccountMeta> openbookV2FulfillmentConfigStatusKeys(final AccountMeta invokedDriftProgramMeta                                                                        ,
                                                                        final PublicKey stateKey,
                                                                        final PublicKey openbookV2FulfillmentConfigKey,
                                                                        final PublicKey adminKey) {
    return List.of(
      createRead(stateKey),
      createWrite(openbookV2FulfillmentConfigKey),
      createWritableSigner(adminKey)
    );
  }

  public static Instruction openbookV2FulfillmentConfigStatus(final AccountMeta invokedDriftProgramMeta,
                                                              final PublicKey stateKey,
                                                              final PublicKey openbookV2FulfillmentConfigKey,
                                                              final PublicKey adminKey,
                                                              final SpotFulfillmentConfigStatus status) {
    final var keys = openbookV2FulfillmentConfigStatusKeys(
      invokedDriftProgramMeta,
      stateKey,
      openbookV2FulfillmentConfigKey,
      adminKey
    );
    return openbookV2FulfillmentConfigStatus(invokedDriftProgramMeta, keys, status);
  }

  public static Instruction openbookV2FulfillmentConfigStatus(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final List<AccountMeta> keys,
                                                              final SpotFulfillmentConfigStatus status) {
    final byte[] _data = new byte[8 + Borsh.len(status)];
    int i = OPENBOOK_V_2_FULFILLMENT_CONFIG_STATUS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(status, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record OpenbookV2FulfillmentConfigStatusIxData(Discriminator discriminator, SpotFulfillmentConfigStatus status) implements Borsh {  

    public static OpenbookV2FulfillmentConfigStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static OpenbookV2FulfillmentConfigStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = SpotFulfillmentConfigStatus.read(_data, i);
      return new OpenbookV2FulfillmentConfigStatusIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(status, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PHOENIX_FULFILLMENT_CONFIG_DISCRIMINATOR = toDiscriminator(135, 132, 110, 107, 185, 160, 169, 154);

  public static List<AccountMeta> initializePhoenixFulfillmentConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey baseSpotMarketKey,
                                                                         final PublicKey quoteSpotMarketKey,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey phoenixProgramKey,
                                                                         final PublicKey phoenixMarketKey,
                                                                         final PublicKey driftSignerKey,
                                                                         final PublicKey phoenixFulfillmentConfigKey,
                                                                         final PublicKey adminKey,
                                                                         final PublicKey rentKey,
                                                                         final PublicKey systemProgramKey) {
    return List.of(
      createRead(baseSpotMarketKey),
      createRead(quoteSpotMarketKey),
      createWrite(stateKey),
      createRead(phoenixProgramKey),
      createRead(phoenixMarketKey),
      createRead(driftSignerKey),
      createWrite(phoenixFulfillmentConfigKey),
      createWritableSigner(adminKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializePhoenixFulfillmentConfig(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey baseSpotMarketKey,
                                                               final PublicKey quoteSpotMarketKey,
                                                               final PublicKey stateKey,
                                                               final PublicKey phoenixProgramKey,
                                                               final PublicKey phoenixMarketKey,
                                                               final PublicKey driftSignerKey,
                                                               final PublicKey phoenixFulfillmentConfigKey,
                                                               final PublicKey adminKey,
                                                               final PublicKey rentKey,
                                                               final PublicKey systemProgramKey,
                                                               final int marketIndex) {
    final var keys = initializePhoenixFulfillmentConfigKeys(
      invokedDriftProgramMeta,
      baseSpotMarketKey,
      quoteSpotMarketKey,
      stateKey,
      phoenixProgramKey,
      phoenixMarketKey,
      driftSignerKey,
      phoenixFulfillmentConfigKey,
      adminKey,
      rentKey,
      systemProgramKey
    );
    return initializePhoenixFulfillmentConfig(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction initializePhoenixFulfillmentConfig(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys,
                                                               final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = INITIALIZE_PHOENIX_FULFILLMENT_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializePhoenixFulfillmentConfigIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static InitializePhoenixFulfillmentConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitializePhoenixFulfillmentConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new InitializePhoenixFulfillmentConfigIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator PHOENIX_FULFILLMENT_CONFIG_STATUS_DISCRIMINATOR = toDiscriminator(96, 31, 113, 32, 12, 203, 7, 154);

  public static List<AccountMeta> phoenixFulfillmentConfigStatusKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey phoenixFulfillmentConfigKey,
                                                                     final PublicKey adminKey) {
    return List.of(
      createRead(stateKey),
      createWrite(phoenixFulfillmentConfigKey),
      createWritableSigner(adminKey)
    );
  }

  public static Instruction phoenixFulfillmentConfigStatus(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey stateKey,
                                                           final PublicKey phoenixFulfillmentConfigKey,
                                                           final PublicKey adminKey,
                                                           final SpotFulfillmentConfigStatus status) {
    final var keys = phoenixFulfillmentConfigStatusKeys(
      invokedDriftProgramMeta,
      stateKey,
      phoenixFulfillmentConfigKey,
      adminKey
    );
    return phoenixFulfillmentConfigStatus(invokedDriftProgramMeta, keys, status);
  }

  public static Instruction phoenixFulfillmentConfigStatus(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final SpotFulfillmentConfigStatus status) {
    final byte[] _data = new byte[8 + Borsh.len(status)];
    int i = PHOENIX_FULFILLMENT_CONFIG_STATUS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(status, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PhoenixFulfillmentConfigStatusIxData(Discriminator discriminator, SpotFulfillmentConfigStatus status) implements Borsh {  

    public static PhoenixFulfillmentConfigStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static PhoenixFulfillmentConfigStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = SpotFulfillmentConfigStatus.read(_data, i);
      return new PhoenixFulfillmentConfigStatusIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(status, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PERP_MARKET_DISCRIMINATOR = toDiscriminator(132, 9, 229, 118, 117, 118, 117, 62);

  public static List<AccountMeta> initializePerpMarketKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey perpMarketKey,
                                                           final PublicKey oracleKey,
                                                           final PublicKey rentKey,
                                                           final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializePerpMarket(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey perpMarketKey,
                                                 final PublicKey oracleKey,
                                                 final PublicKey rentKey,
                                                 final PublicKey systemProgramKey,
                                                 final int marketIndex,
                                                 final BigInteger ammBaseAssetReserve,
                                                 final BigInteger ammQuoteAssetReserve,
                                                 final long ammPeriodicity,
                                                 final BigInteger ammPegMultiplier,
                                                 final OracleSource oracleSource,
                                                 final ContractTier contractTier,
                                                 final int marginRatioInitial,
                                                 final int marginRatioMaintenance,
                                                 final int liquidatorFee,
                                                 final int ifLiquidationFee,
                                                 final int imfFactor,
                                                 final boolean activeStatus,
                                                 final int baseSpread,
                                                 final int maxSpread,
                                                 final BigInteger maxOpenInterest,
                                                 final long maxRevenueWithdrawPerPeriod,
                                                 final long quoteMaxInsurance,
                                                 final long orderStepSize,
                                                 final long orderTickSize,
                                                 final long minOrderSize,
                                                 final BigInteger concentrationCoefScale,
                                                 final int curveUpdateIntensity,
                                                 final int ammJitIntensity,
                                                 final byte[] name,
                                                 final int lpPoolId) {
    final var keys = initializePerpMarketKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey,
      oracleKey,
      rentKey,
      systemProgramKey
    );
    return initializePerpMarket(
      invokedDriftProgramMeta,
      keys,
      marketIndex,
      ammBaseAssetReserve,
      ammQuoteAssetReserve,
      ammPeriodicity,
      ammPegMultiplier,
      oracleSource,
      contractTier,
      marginRatioInitial,
      marginRatioMaintenance,
      liquidatorFee,
      ifLiquidationFee,
      imfFactor,
      activeStatus,
      baseSpread,
      maxSpread,
      maxOpenInterest,
      maxRevenueWithdrawPerPeriod,
      quoteMaxInsurance,
      orderStepSize,
      orderTickSize,
      minOrderSize,
      concentrationCoefScale,
      curveUpdateIntensity,
      ammJitIntensity,
      name,
      lpPoolId
    );
  }

  public static Instruction initializePerpMarket(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int marketIndex,
                                                 final BigInteger ammBaseAssetReserve,
                                                 final BigInteger ammQuoteAssetReserve,
                                                 final long ammPeriodicity,
                                                 final BigInteger ammPegMultiplier,
                                                 final OracleSource oracleSource,
                                                 final ContractTier contractTier,
                                                 final int marginRatioInitial,
                                                 final int marginRatioMaintenance,
                                                 final int liquidatorFee,
                                                 final int ifLiquidationFee,
                                                 final int imfFactor,
                                                 final boolean activeStatus,
                                                 final int baseSpread,
                                                 final int maxSpread,
                                                 final BigInteger maxOpenInterest,
                                                 final long maxRevenueWithdrawPerPeriod,
                                                 final long quoteMaxInsurance,
                                                 final long orderStepSize,
                                                 final long orderTickSize,
                                                 final long minOrderSize,
                                                 final BigInteger concentrationCoefScale,
                                                 final int curveUpdateIntensity,
                                                 final int ammJitIntensity,
                                                 final byte[] name,
                                                 final int lpPoolId) {
    final byte[] _data = new byte[170 + Borsh.len(oracleSource) + Borsh.len(contractTier) + Borsh.lenArray(name)];
    int i = INITIALIZE_PERP_MARKET_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt128LE(_data, i, ammBaseAssetReserve);
    i += 16;
    putInt128LE(_data, i, ammQuoteAssetReserve);
    i += 16;
    putInt64LE(_data, i, ammPeriodicity);
    i += 8;
    putInt128LE(_data, i, ammPegMultiplier);
    i += 16;
    i += Borsh.write(oracleSource, _data, i);
    i += Borsh.write(contractTier, _data, i);
    putInt32LE(_data, i, marginRatioInitial);
    i += 4;
    putInt32LE(_data, i, marginRatioMaintenance);
    i += 4;
    putInt32LE(_data, i, liquidatorFee);
    i += 4;
    putInt32LE(_data, i, ifLiquidationFee);
    i += 4;
    putInt32LE(_data, i, imfFactor);
    i += 4;
    _data[i] = (byte) (activeStatus ? 1 : 0);
    ++i;
    putInt32LE(_data, i, baseSpread);
    i += 4;
    putInt32LE(_data, i, maxSpread);
    i += 4;
    putInt128LE(_data, i, maxOpenInterest);
    i += 16;
    putInt64LE(_data, i, maxRevenueWithdrawPerPeriod);
    i += 8;
    putInt64LE(_data, i, quoteMaxInsurance);
    i += 8;
    putInt64LE(_data, i, orderStepSize);
    i += 8;
    putInt64LE(_data, i, orderTickSize);
    i += 8;
    putInt64LE(_data, i, minOrderSize);
    i += 8;
    putInt128LE(_data, i, concentrationCoefScale);
    i += 16;
    _data[i] = (byte) curveUpdateIntensity;
    ++i;
    _data[i] = (byte) ammJitIntensity;
    ++i;
    i += Borsh.writeArrayChecked(name, 32, _data, i);
    _data[i] = (byte) lpPoolId;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializePerpMarketIxData(Discriminator discriminator,
                                           int marketIndex,
                                           BigInteger ammBaseAssetReserve,
                                           BigInteger ammQuoteAssetReserve,
                                           long ammPeriodicity,
                                           BigInteger ammPegMultiplier,
                                           OracleSource oracleSource,
                                           ContractTier contractTier,
                                           int marginRatioInitial,
                                           int marginRatioMaintenance,
                                           int liquidatorFee,
                                           int ifLiquidationFee,
                                           int imfFactor,
                                           boolean activeStatus,
                                           int baseSpread,
                                           int maxSpread,
                                           BigInteger maxOpenInterest,
                                           long maxRevenueWithdrawPerPeriod,
                                           long quoteMaxInsurance,
                                           long orderStepSize,
                                           long orderTickSize,
                                           long minOrderSize,
                                           BigInteger concentrationCoefScale,
                                           int curveUpdateIntensity,
                                           int ammJitIntensity,
                                           byte[] name,
                                           int lpPoolId) implements Borsh {  

    public static InitializePerpMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 204;
    public static final int NAME_LEN = 32;

    public static InitializePerpMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var ammBaseAssetReserve = getInt128LE(_data, i);
      i += 16;
      final var ammQuoteAssetReserve = getInt128LE(_data, i);
      i += 16;
      final var ammPeriodicity = getInt64LE(_data, i);
      i += 8;
      final var ammPegMultiplier = getInt128LE(_data, i);
      i += 16;
      final var oracleSource = OracleSource.read(_data, i);
      i += Borsh.len(oracleSource);
      final var contractTier = ContractTier.read(_data, i);
      i += Borsh.len(contractTier);
      final var marginRatioInitial = getInt32LE(_data, i);
      i += 4;
      final var marginRatioMaintenance = getInt32LE(_data, i);
      i += 4;
      final var liquidatorFee = getInt32LE(_data, i);
      i += 4;
      final var ifLiquidationFee = getInt32LE(_data, i);
      i += 4;
      final var imfFactor = getInt32LE(_data, i);
      i += 4;
      final var activeStatus = _data[i] == 1;
      ++i;
      final var baseSpread = getInt32LE(_data, i);
      i += 4;
      final var maxSpread = getInt32LE(_data, i);
      i += 4;
      final var maxOpenInterest = getInt128LE(_data, i);
      i += 16;
      final var maxRevenueWithdrawPerPeriod = getInt64LE(_data, i);
      i += 8;
      final var quoteMaxInsurance = getInt64LE(_data, i);
      i += 8;
      final var orderStepSize = getInt64LE(_data, i);
      i += 8;
      final var orderTickSize = getInt64LE(_data, i);
      i += 8;
      final var minOrderSize = getInt64LE(_data, i);
      i += 8;
      final var concentrationCoefScale = getInt128LE(_data, i);
      i += 16;
      final var curveUpdateIntensity = _data[i] & 0xFF;
      ++i;
      final var ammJitIntensity = _data[i] & 0xFF;
      ++i;
      final var name = new byte[32];
      i += Borsh.readArray(name, _data, i);
      final var lpPoolId = _data[i] & 0xFF;
      return new InitializePerpMarketIxData(discriminator,
                                            marketIndex,
                                            ammBaseAssetReserve,
                                            ammQuoteAssetReserve,
                                            ammPeriodicity,
                                            ammPegMultiplier,
                                            oracleSource,
                                            contractTier,
                                            marginRatioInitial,
                                            marginRatioMaintenance,
                                            liquidatorFee,
                                            ifLiquidationFee,
                                            imfFactor,
                                            activeStatus,
                                            baseSpread,
                                            maxSpread,
                                            maxOpenInterest,
                                            maxRevenueWithdrawPerPeriod,
                                            quoteMaxInsurance,
                                            orderStepSize,
                                            orderTickSize,
                                            minOrderSize,
                                            concentrationCoefScale,
                                            curveUpdateIntensity,
                                            ammJitIntensity,
                                            name,
                                            lpPoolId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt128LE(_data, i, ammBaseAssetReserve);
      i += 16;
      putInt128LE(_data, i, ammQuoteAssetReserve);
      i += 16;
      putInt64LE(_data, i, ammPeriodicity);
      i += 8;
      putInt128LE(_data, i, ammPegMultiplier);
      i += 16;
      i += Borsh.write(oracleSource, _data, i);
      i += Borsh.write(contractTier, _data, i);
      putInt32LE(_data, i, marginRatioInitial);
      i += 4;
      putInt32LE(_data, i, marginRatioMaintenance);
      i += 4;
      putInt32LE(_data, i, liquidatorFee);
      i += 4;
      putInt32LE(_data, i, ifLiquidationFee);
      i += 4;
      putInt32LE(_data, i, imfFactor);
      i += 4;
      _data[i] = (byte) (activeStatus ? 1 : 0);
      ++i;
      putInt32LE(_data, i, baseSpread);
      i += 4;
      putInt32LE(_data, i, maxSpread);
      i += 4;
      putInt128LE(_data, i, maxOpenInterest);
      i += 16;
      putInt64LE(_data, i, maxRevenueWithdrawPerPeriod);
      i += 8;
      putInt64LE(_data, i, quoteMaxInsurance);
      i += 8;
      putInt64LE(_data, i, orderStepSize);
      i += 8;
      putInt64LE(_data, i, orderTickSize);
      i += 8;
      putInt64LE(_data, i, minOrderSize);
      i += 8;
      putInt128LE(_data, i, concentrationCoefScale);
      i += 16;
      _data[i] = (byte) curveUpdateIntensity;
      ++i;
      _data[i] = (byte) ammJitIntensity;
      ++i;
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      _data[i] = (byte) lpPoolId;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_AMM_CACHE_DISCRIMINATOR = toDiscriminator(38, 60, 171, 158, 203, 58, 137, 8);

  public static List<AccountMeta> initializeAmmCacheKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey ammCacheKey,
                                                         final PublicKey rentKey,
                                                         final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(stateKey),
      createWrite(ammCacheKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeAmmCache(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey adminKey,
                                               final PublicKey stateKey,
                                               final PublicKey ammCacheKey,
                                               final PublicKey rentKey,
                                               final PublicKey systemProgramKey) {
    final var keys = initializeAmmCacheKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      ammCacheKey,
      rentKey,
      systemProgramKey
    );
    return initializeAmmCache(invokedDriftProgramMeta, keys);
  }

  public static Instruction initializeAmmCache(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, INITIALIZE_AMM_CACHE_DISCRIMINATOR);
  }

  public static final Discriminator ADD_MARKET_TO_AMM_CACHE_DISCRIMINATOR = toDiscriminator(112, 149, 195, 222, 124, 7, 87, 237);

  public static List<AccountMeta> addMarketToAmmCacheKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey ammCacheKey,
                                                          final PublicKey perpMarketKey,
                                                          final PublicKey rentKey,
                                                          final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(stateKey),
      createWrite(ammCacheKey),
      createRead(perpMarketKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction addMarketToAmmCache(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey adminKey,
                                                final PublicKey stateKey,
                                                final PublicKey ammCacheKey,
                                                final PublicKey perpMarketKey,
                                                final PublicKey rentKey,
                                                final PublicKey systemProgramKey) {
    final var keys = addMarketToAmmCacheKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      ammCacheKey,
      perpMarketKey,
      rentKey,
      systemProgramKey
    );
    return addMarketToAmmCache(invokedDriftProgramMeta, keys);
  }

  public static Instruction addMarketToAmmCache(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, ADD_MARKET_TO_AMM_CACHE_DISCRIMINATOR);
  }

  public static final Discriminator DELETE_AMM_CACHE_DISCRIMINATOR = toDiscriminator(216, 130, 215, 206, 233, 232, 191, 88);

  public static List<AccountMeta> deleteAmmCacheKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey adminKey,
                                                     final PublicKey stateKey,
                                                     final PublicKey ammCacheKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(stateKey),
      createWrite(ammCacheKey)
    );
  }

  public static Instruction deleteAmmCache(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey adminKey,
                                           final PublicKey stateKey,
                                           final PublicKey ammCacheKey) {
    final var keys = deleteAmmCacheKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      ammCacheKey
    );
    return deleteAmmCache(invokedDriftProgramMeta, keys);
  }

  public static Instruction deleteAmmCache(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, DELETE_AMM_CACHE_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_INITIAL_AMM_CACHE_INFO_DISCRIMINATOR = toDiscriminator(157, 210, 109, 67, 212, 170, 12, 107);

  public static List<AccountMeta> updateInitialAmmCacheInfoKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey stateKey,
                                                                final PublicKey adminKey,
                                                                final PublicKey ammCacheKey) {
    return List.of(
      createWrite(stateKey),
      createReadOnlySigner(adminKey),
      createWrite(ammCacheKey)
    );
  }

  public static Instruction updateInitialAmmCacheInfo(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey stateKey,
                                                      final PublicKey adminKey,
                                                      final PublicKey ammCacheKey) {
    final var keys = updateInitialAmmCacheInfoKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      ammCacheKey
    );
    return updateInitialAmmCacheInfo(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateInitialAmmCacheInfo(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_INITIAL_AMM_CACHE_INFO_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_PREDICTION_MARKET_DISCRIMINATOR = toDiscriminator(248, 70, 198, 224, 224, 105, 125, 195);

  public static List<AccountMeta> initializePredictionMarketKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction initializePredictionMarket(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey adminKey,
                                                       final PublicKey stateKey,
                                                       final PublicKey perpMarketKey) {
    final var keys = initializePredictionMarketKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return initializePredictionMarket(invokedDriftProgramMeta, keys);
  }

  public static Instruction initializePredictionMarket(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, INITIALIZE_PREDICTION_MARKET_DISCRIMINATOR);
  }

  public static final Discriminator DELETE_INITIALIZED_PERP_MARKET_DISCRIMINATOR = toDiscriminator(91, 154, 24, 87, 106, 59, 190, 66);

  public static List<AccountMeta> deleteInitializedPerpMarketKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey perpMarketKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction deleteInitializedPerpMarket(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey adminKey,
                                                        final PublicKey stateKey,
                                                        final PublicKey perpMarketKey,
                                                        final int marketIndex) {
    final var keys = deleteInitializedPerpMarketKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return deleteInitializedPerpMarket(invokedDriftProgramMeta, keys, marketIndex);
  }

  public static Instruction deleteInitializedPerpMarket(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final int marketIndex) {
    final byte[] _data = new byte[10];
    int i = DELETE_INITIALIZED_PERP_MARKET_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DeleteInitializedPerpMarketIxData(Discriminator discriminator, int marketIndex) implements Borsh {  

    public static DeleteInitializedPerpMarketIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static DeleteInitializedPerpMarketIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      return new DeleteInitializedPerpMarketIxData(discriminator, marketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator MOVE_AMM_PRICE_DISCRIMINATOR = toDiscriminator(235, 109, 2, 82, 219, 118, 6, 159);

  public static List<AccountMeta> moveAmmPriceKeys(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction moveAmmPrice(final AccountMeta invokedDriftProgramMeta,
                                         final PublicKey adminKey,
                                         final PublicKey stateKey,
                                         final PublicKey perpMarketKey,
                                         final BigInteger baseAssetReserve,
                                         final BigInteger quoteAssetReserve,
                                         final BigInteger sqrtK) {
    final var keys = moveAmmPriceKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return moveAmmPrice(
      invokedDriftProgramMeta,
      keys,
      baseAssetReserve,
      quoteAssetReserve,
      sqrtK
    );
  }

  public static Instruction moveAmmPrice(final AccountMeta invokedDriftProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final BigInteger baseAssetReserve,
                                         final BigInteger quoteAssetReserve,
                                         final BigInteger sqrtK) {
    final byte[] _data = new byte[56];
    int i = MOVE_AMM_PRICE_DISCRIMINATOR.write(_data, 0);
    putInt128LE(_data, i, baseAssetReserve);
    i += 16;
    putInt128LE(_data, i, quoteAssetReserve);
    i += 16;
    putInt128LE(_data, i, sqrtK);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record MoveAmmPriceIxData(Discriminator discriminator,
                                   BigInteger baseAssetReserve,
                                   BigInteger quoteAssetReserve,
                                   BigInteger sqrtK) implements Borsh {  

    public static MoveAmmPriceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 56;

    public static MoveAmmPriceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var baseAssetReserve = getInt128LE(_data, i);
      i += 16;
      final var quoteAssetReserve = getInt128LE(_data, i);
      i += 16;
      final var sqrtK = getInt128LE(_data, i);
      return new MoveAmmPriceIxData(discriminator, baseAssetReserve, quoteAssetReserve, sqrtK);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt128LE(_data, i, baseAssetReserve);
      i += 16;
      putInt128LE(_data, i, quoteAssetReserve);
      i += 16;
      putInt128LE(_data, i, sqrtK);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RECENTER_PERP_MARKET_AMM_DISCRIMINATOR = toDiscriminator(24, 87, 10, 115, 165, 190, 80, 139);

  public static List<AccountMeta> recenterPerpMarketAmmKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction recenterPerpMarketAmm(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey adminKey,
                                                  final PublicKey stateKey,
                                                  final PublicKey perpMarketKey,
                                                  final BigInteger pegMultiplier,
                                                  final BigInteger sqrtK) {
    final var keys = recenterPerpMarketAmmKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return recenterPerpMarketAmm(invokedDriftProgramMeta, keys, pegMultiplier, sqrtK);
  }

  public static Instruction recenterPerpMarketAmm(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final BigInteger pegMultiplier,
                                                  final BigInteger sqrtK) {
    final byte[] _data = new byte[40];
    int i = RECENTER_PERP_MARKET_AMM_DISCRIMINATOR.write(_data, 0);
    putInt128LE(_data, i, pegMultiplier);
    i += 16;
    putInt128LE(_data, i, sqrtK);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record RecenterPerpMarketAmmIxData(Discriminator discriminator, BigInteger pegMultiplier, BigInteger sqrtK) implements Borsh {  

    public static RecenterPerpMarketAmmIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static RecenterPerpMarketAmmIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var pegMultiplier = getInt128LE(_data, i);
      i += 16;
      final var sqrtK = getInt128LE(_data, i);
      return new RecenterPerpMarketAmmIxData(discriminator, pegMultiplier, sqrtK);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt128LE(_data, i, pegMultiplier);
      i += 16;
      putInt128LE(_data, i, sqrtK);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RECENTER_PERP_MARKET_AMM_CRANK_DISCRIMINATOR = toDiscriminator(166, 19, 64, 10, 14, 51, 101, 122);

  public static List<AccountMeta> recenterPerpMarketAmmCrankKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey perpMarketKey,
                                                                 final PublicKey spotMarketKey,
                                                                 final PublicKey oracleKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(spotMarketKey),
      createRead(oracleKey)
    );
  }

  public static Instruction recenterPerpMarketAmmCrank(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey adminKey,
                                                       final PublicKey stateKey,
                                                       final PublicKey perpMarketKey,
                                                       final PublicKey spotMarketKey,
                                                       final PublicKey oracleKey,
                                                       final BigInteger depth) {
    final var keys = recenterPerpMarketAmmCrankKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey,
      spotMarketKey,
      oracleKey
    );
    return recenterPerpMarketAmmCrank(invokedDriftProgramMeta, keys, depth);
  }

  public static Instruction recenterPerpMarketAmmCrank(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final BigInteger depth) {
    final byte[] _data = new byte[
    8
    + (depth == null ? 1 : 17)
    ];
    int i = RECENTER_PERP_MARKET_AMM_CRANK_DISCRIMINATOR.write(_data, 0);
    Borsh.write128Optional(depth, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record RecenterPerpMarketAmmCrankIxData(Discriminator discriminator, BigInteger depth) implements Borsh {  

    public static RecenterPerpMarketAmmCrankIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static RecenterPerpMarketAmmCrankIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final BigInteger depth;
      if (_data[i] == 0) {
        depth = null;
      } else {
        ++i;
      ;
        depth = getInt128LE(_data, i);
      }
      return new RecenterPerpMarketAmmCrankIxData(discriminator, depth);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write128Optional(depth, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (depth == null ? 1 : (1 + 16));
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_AMM_SUMMARY_STATS_DISCRIMINATOR = toDiscriminator(122, 101, 249, 238, 209, 9, 241, 245);

  public static List<AccountMeta> updatePerpMarketAmmSummaryStatsKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey stateKey,
                                                                      final PublicKey perpMarketKey,
                                                                      final PublicKey spotMarketKey,
                                                                      final PublicKey oracleKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(spotMarketKey),
      createRead(oracleKey)
    );
  }

  public static Instruction updatePerpMarketAmmSummaryStats(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final PublicKey perpMarketKey,
                                                            final PublicKey spotMarketKey,
                                                            final PublicKey oracleKey,
                                                            final UpdatePerpMarketSummaryStatsParams params) {
    final var keys = updatePerpMarketAmmSummaryStatsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey,
      spotMarketKey,
      oracleKey
    );
    return updatePerpMarketAmmSummaryStats(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction updatePerpMarketAmmSummaryStats(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final UpdatePerpMarketSummaryStatsParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = UPDATE_PERP_MARKET_AMM_SUMMARY_STATS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketAmmSummaryStatsIxData(Discriminator discriminator, UpdatePerpMarketSummaryStatsParams params) implements Borsh {  

    public static UpdatePerpMarketAmmSummaryStatsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdatePerpMarketAmmSummaryStatsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = UpdatePerpMarketSummaryStatsParams.read(_data, i);
      return new UpdatePerpMarketAmmSummaryStatsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params);
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_EXPIRY_DISCRIMINATOR = toDiscriminator(44, 221, 227, 151, 131, 140, 22, 110);

  public static List<AccountMeta> updatePerpMarketExpiryKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketExpiry(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey perpMarketKey,
                                                   final long expiryTs) {
    final var keys = updatePerpMarketExpiryKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketExpiry(invokedDriftProgramMeta, keys, expiryTs);
  }

  public static Instruction updatePerpMarketExpiry(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final long expiryTs) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_EXPIRY_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, expiryTs);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketExpiryIxData(Discriminator discriminator, long expiryTs) implements Borsh {  

    public static UpdatePerpMarketExpiryIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketExpiryIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var expiryTs = getInt64LE(_data, i);
      return new UpdatePerpMarketExpiryIxData(discriminator, expiryTs);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, expiryTs);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_LP_POOL_PAUSED_OPERATIONS_DISCRIMINATOR = toDiscriminator(181, 94, 93, 146, 51, 89, 32, 135);

  public static List<AccountMeta> updatePerpMarketLpPoolPausedOperationsKeys(final AccountMeta invokedDriftProgramMeta                                                                             ,
                                                                             final PublicKey adminKey,
                                                                             final PublicKey stateKey,
                                                                             final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketLpPoolPausedOperations(final AccountMeta invokedDriftProgramMeta,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey,
                                                                   final int lpPausedOperations) {
    final var keys = updatePerpMarketLpPoolPausedOperationsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketLpPoolPausedOperations(invokedDriftProgramMeta, keys, lpPausedOperations);
  }

  public static Instruction updatePerpMarketLpPoolPausedOperations(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final List<AccountMeta> keys,
                                                                   final int lpPausedOperations) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_LP_POOL_PAUSED_OPERATIONS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) lpPausedOperations;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketLpPoolPausedOperationsIxData(Discriminator discriminator, int lpPausedOperations) implements Borsh {  

    public static UpdatePerpMarketLpPoolPausedOperationsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketLpPoolPausedOperationsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lpPausedOperations = _data[i] & 0xFF;
      return new UpdatePerpMarketLpPoolPausedOperationsIxData(discriminator, lpPausedOperations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) lpPausedOperations;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_LP_POOL_STATUS_DISCRIMINATOR = toDiscriminator(67, 6, 252, 61, 54, 88, 89, 233);

  public static List<AccountMeta> updatePerpMarketLpPoolStatusKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey,
                                                                   final PublicKey ammCacheKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey),
      createWrite(ammCacheKey)
    );
  }

  public static Instruction updatePerpMarketLpPoolStatus(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey perpMarketKey,
                                                         final PublicKey ammCacheKey,
                                                         final int lpStatus) {
    final var keys = updatePerpMarketLpPoolStatusKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey,
      ammCacheKey
    );
    return updatePerpMarketLpPoolStatus(invokedDriftProgramMeta, keys, lpStatus);
  }

  public static Instruction updatePerpMarketLpPoolStatus(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final int lpStatus) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_LP_POOL_STATUS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) lpStatus;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketLpPoolStatusIxData(Discriminator discriminator, int lpStatus) implements Borsh {  

    public static UpdatePerpMarketLpPoolStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketLpPoolStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lpStatus = _data[i] & 0xFF;
      return new UpdatePerpMarketLpPoolStatusIxData(discriminator, lpStatus);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) lpStatus;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_LP_POOL_FEE_TRANSFER_SCALAR_DISCRIMINATOR = toDiscriminator(94, 228, 237, 109, 100, 185, 4, 81);

  public static List<AccountMeta> updatePerpMarketLpPoolFeeTransferScalarKeys(final AccountMeta invokedDriftProgramMeta                                                                              ,
                                                                              final PublicKey adminKey,
                                                                              final PublicKey stateKey,
                                                                              final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketLpPoolFeeTransferScalar(final AccountMeta invokedDriftProgramMeta,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey,
                                                                    final OptionalInt optionalLpFeeTransferScalar,
                                                                    final OptionalInt optionalLpNetPnlTransferScalar) {
    final var keys = updatePerpMarketLpPoolFeeTransferScalarKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketLpPoolFeeTransferScalar(invokedDriftProgramMeta, keys, optionalLpFeeTransferScalar, optionalLpNetPnlTransferScalar);
  }

  public static Instruction updatePerpMarketLpPoolFeeTransferScalar(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final List<AccountMeta> keys,
                                                                    final OptionalInt optionalLpFeeTransferScalar,
                                                                    final OptionalInt optionalLpNetPnlTransferScalar) {
    final byte[] _data = new byte[
    8
    + (optionalLpFeeTransferScalar == null || optionalLpFeeTransferScalar.isEmpty() ? 1 : 2)
    + (optionalLpNetPnlTransferScalar == null || optionalLpNetPnlTransferScalar.isEmpty() ? 1 : 2)
    ];
    int i = UPDATE_PERP_MARKET_LP_POOL_FEE_TRANSFER_SCALAR_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptionalbyte(optionalLpFeeTransferScalar, _data, i);
    Borsh.writeOptionalbyte(optionalLpNetPnlTransferScalar, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketLpPoolFeeTransferScalarIxData(Discriminator discriminator, OptionalInt optionalLpFeeTransferScalar, OptionalInt optionalLpNetPnlTransferScalar) implements Borsh {  

    public static UpdatePerpMarketLpPoolFeeTransferScalarIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdatePerpMarketLpPoolFeeTransferScalarIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt optionalLpFeeTransferScalar;
      if (_data[i] == 0) {
        optionalLpFeeTransferScalar = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        optionalLpFeeTransferScalar = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt optionalLpNetPnlTransferScalar;
      if (_data[i] == 0) {
        optionalLpNetPnlTransferScalar = OptionalInt.empty();
      } else {
        ++i;
      ;
        optionalLpNetPnlTransferScalar = OptionalInt.of(_data[i] & 0xFF);
      }
      return new UpdatePerpMarketLpPoolFeeTransferScalarIxData(discriminator, optionalLpFeeTransferScalar, optionalLpNetPnlTransferScalar);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptionalbyte(optionalLpFeeTransferScalar, _data, i);
      i += Borsh.writeOptionalbyte(optionalLpNetPnlTransferScalar, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (optionalLpFeeTransferScalar == null || optionalLpFeeTransferScalar.isEmpty() ? 1 : (1 + 1)) + (optionalLpNetPnlTransferScalar == null || optionalLpNetPnlTransferScalar.isEmpty() ? 1 : (1 + 1));
    }
  }

  public static final Discriminator SETTLE_EXPIRED_MARKET_POOLS_TO_REVENUE_POOL_DISCRIMINATOR = toDiscriminator(55, 19, 238, 169, 227, 90, 200, 184);

  public static List<AccountMeta> settleExpiredMarketPoolsToRevenuePoolKeys(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final PublicKey stateKey,
                                                                            final PublicKey adminKey,
                                                                            final PublicKey spotMarketKey,
                                                                            final PublicKey perpMarketKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(adminKey),
      createWrite(spotMarketKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction settleExpiredMarketPoolsToRevenuePool(final AccountMeta invokedDriftProgramMeta,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey spotMarketKey,
                                                                  final PublicKey perpMarketKey) {
    final var keys = settleExpiredMarketPoolsToRevenuePoolKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      spotMarketKey,
      perpMarketKey
    );
    return settleExpiredMarketPoolsToRevenuePool(invokedDriftProgramMeta, keys);
  }

  public static Instruction settleExpiredMarketPoolsToRevenuePool(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, SETTLE_EXPIRED_MARKET_POOLS_TO_REVENUE_POOL_DISCRIMINATOR);
  }

  public static final Discriminator DEPOSIT_INTO_PERP_MARKET_FEE_POOL_DISCRIMINATOR = toDiscriminator(34, 58, 57, 68, 97, 80, 244, 6);

  public static List<AccountMeta> depositIntoPerpMarketFeePoolKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey sourceVaultKey,
                                                                   final PublicKey driftSignerKey,
                                                                   final PublicKey quoteSpotMarketKey,
                                                                   final PublicKey spotMarketVaultKey,
                                                                   final PublicKey tokenProgramKey) {
    return List.of(
      createWrite(stateKey),
      createWrite(perpMarketKey),
      createReadOnlySigner(adminKey),
      createWrite(sourceVaultKey),
      createRead(driftSignerKey),
      createWrite(quoteSpotMarketKey),
      createWrite(spotMarketVaultKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction depositIntoPerpMarketFeePool(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey stateKey,
                                                         final PublicKey perpMarketKey,
                                                         final PublicKey adminKey,
                                                         final PublicKey sourceVaultKey,
                                                         final PublicKey driftSignerKey,
                                                         final PublicKey quoteSpotMarketKey,
                                                         final PublicKey spotMarketVaultKey,
                                                         final PublicKey tokenProgramKey,
                                                         final long amount) {
    final var keys = depositIntoPerpMarketFeePoolKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      adminKey,
      sourceVaultKey,
      driftSignerKey,
      quoteSpotMarketKey,
      spotMarketVaultKey,
      tokenProgramKey
    );
    return depositIntoPerpMarketFeePool(invokedDriftProgramMeta, keys, amount);
  }

  public static Instruction depositIntoPerpMarketFeePool(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final long amount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_INTO_PERP_MARKET_FEE_POOL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DepositIntoPerpMarketFeePoolIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static DepositIntoPerpMarketFeePoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositIntoPerpMarketFeePoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new DepositIntoPerpMarketFeePoolIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_PNL_POOL_DISCRIMINATOR = toDiscriminator(50, 202, 249, 224, 166, 184, 13, 143);

  public static List<AccountMeta> updatePerpMarketPnlPoolKeys(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final PublicKey stateKey,
                                                              final PublicKey adminKey,
                                                              final PublicKey spotMarketKey,
                                                              final PublicKey spotMarketVaultKey,
                                                              final PublicKey perpMarketKey) {
    return List.of(
      createRead(stateKey),
      createReadOnlySigner(adminKey),
      createWrite(spotMarketKey),
      createWrite(spotMarketVaultKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketPnlPool(final AccountMeta invokedDriftProgramMeta,
                                                    final PublicKey stateKey,
                                                    final PublicKey adminKey,
                                                    final PublicKey spotMarketKey,
                                                    final PublicKey spotMarketVaultKey,
                                                    final PublicKey perpMarketKey,
                                                    final long amount) {
    final var keys = updatePerpMarketPnlPoolKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      spotMarketKey,
      spotMarketVaultKey,
      perpMarketKey
    );
    return updatePerpMarketPnlPool(invokedDriftProgramMeta, keys, amount);
  }

  public static Instruction updatePerpMarketPnlPool(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final List<AccountMeta> keys,
                                                    final long amount) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_PNL_POOL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketPnlPoolIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static UpdatePerpMarketPnlPoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketPnlPoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new UpdatePerpMarketPnlPoolIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_INTO_SPOT_MARKET_VAULT_DISCRIMINATOR = toDiscriminator(48, 252, 119, 73, 255, 205, 174, 247);

  public static List<AccountMeta> depositIntoSpotMarketVaultKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey spotMarketKey,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey sourceVaultKey,
                                                                 final PublicKey spotMarketVaultKey,
                                                                 final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createReadOnlySigner(adminKey),
      createWrite(sourceVaultKey),
      createWrite(spotMarketVaultKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction depositIntoSpotMarketVault(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey stateKey,
                                                       final PublicKey spotMarketKey,
                                                       final PublicKey adminKey,
                                                       final PublicKey sourceVaultKey,
                                                       final PublicKey spotMarketVaultKey,
                                                       final PublicKey tokenProgramKey,
                                                       final long amount) {
    final var keys = depositIntoSpotMarketVaultKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      adminKey,
      sourceVaultKey,
      spotMarketVaultKey,
      tokenProgramKey
    );
    return depositIntoSpotMarketVault(invokedDriftProgramMeta, keys, amount);
  }

  public static Instruction depositIntoSpotMarketVault(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final long amount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_INTO_SPOT_MARKET_VAULT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DepositIntoSpotMarketVaultIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static DepositIntoSpotMarketVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositIntoSpotMarketVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new DepositIntoSpotMarketVaultIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator DEPOSIT_INTO_SPOT_MARKET_REVENUE_POOL_DISCRIMINATOR = toDiscriminator(92, 40, 151, 42, 122, 254, 139, 246);

  public static List<AccountMeta> depositIntoSpotMarketRevenuePoolKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey spotMarketKey,
                                                                       final PublicKey authorityKey,
                                                                       final PublicKey spotMarketVaultKey,
                                                                       final PublicKey userTokenAccountKey,
                                                                       final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(spotMarketKey),
      createWritableSigner(authorityKey),
      createWrite(spotMarketVaultKey),
      createWrite(userTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction depositIntoSpotMarketRevenuePool(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey,
                                                             final PublicKey authorityKey,
                                                             final PublicKey spotMarketVaultKey,
                                                             final PublicKey userTokenAccountKey,
                                                             final PublicKey tokenProgramKey,
                                                             final long amount) {
    final var keys = depositIntoSpotMarketRevenuePoolKeys(
      invokedDriftProgramMeta,
      stateKey,
      spotMarketKey,
      authorityKey,
      spotMarketVaultKey,
      userTokenAccountKey,
      tokenProgramKey
    );
    return depositIntoSpotMarketRevenuePool(invokedDriftProgramMeta, keys, amount);
  }

  public static Instruction depositIntoSpotMarketRevenuePool(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final long amount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_INTO_SPOT_MARKET_REVENUE_POOL_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DepositIntoSpotMarketRevenuePoolIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static DepositIntoSpotMarketRevenuePoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositIntoSpotMarketRevenuePoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new DepositIntoSpotMarketRevenuePoolIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator REPEG_AMM_CURVE_DISCRIMINATOR = toDiscriminator(3, 36, 102, 89, 180, 128, 120, 213);

  public static List<AccountMeta> repegAmmCurveKeys(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final PublicKey stateKey,
                                                    final PublicKey perpMarketKey,
                                                    final PublicKey oracleKey,
                                                    final PublicKey adminKey) {
    return List.of(
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction repegAmmCurve(final AccountMeta invokedDriftProgramMeta,
                                          final PublicKey stateKey,
                                          final PublicKey perpMarketKey,
                                          final PublicKey oracleKey,
                                          final PublicKey adminKey,
                                          final BigInteger newPegCandidate) {
    final var keys = repegAmmCurveKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      oracleKey,
      adminKey
    );
    return repegAmmCurve(invokedDriftProgramMeta, keys, newPegCandidate);
  }

  public static Instruction repegAmmCurve(final AccountMeta invokedDriftProgramMeta                                          ,
                                          final List<AccountMeta> keys,
                                          final BigInteger newPegCandidate) {
    final byte[] _data = new byte[24];
    int i = REPEG_AMM_CURVE_DISCRIMINATOR.write(_data, 0);
    putInt128LE(_data, i, newPegCandidate);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record RepegAmmCurveIxData(Discriminator discriminator, BigInteger newPegCandidate) implements Borsh {  

    public static RepegAmmCurveIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static RepegAmmCurveIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newPegCandidate = getInt128LE(_data, i);
      return new RepegAmmCurveIxData(discriminator, newPegCandidate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt128LE(_data, i, newPegCandidate);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_AMM_ORACLE_TWAP_DISCRIMINATOR = toDiscriminator(241, 74, 114, 123, 206, 153, 24, 202);

  public static List<AccountMeta> updatePerpMarketAmmOracleTwapKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey,
                                                                    final PublicKey oracleKey,
                                                                    final PublicKey adminKey) {
    return List.of(
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction updatePerpMarketAmmOracleTwap(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey stateKey,
                                                          final PublicKey perpMarketKey,
                                                          final PublicKey oracleKey,
                                                          final PublicKey adminKey) {
    final var keys = updatePerpMarketAmmOracleTwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      oracleKey,
      adminKey
    );
    return updatePerpMarketAmmOracleTwap(invokedDriftProgramMeta, keys);
  }

  public static Instruction updatePerpMarketAmmOracleTwap(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_PERP_MARKET_AMM_ORACLE_TWAP_DISCRIMINATOR);
  }

  public static final Discriminator RESET_PERP_MARKET_AMM_ORACLE_TWAP_DISCRIMINATOR = toDiscriminator(127, 10, 55, 164, 123, 226, 47, 24);

  public static List<AccountMeta> resetPerpMarketAmmOracleTwapKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey,
                                                                   final PublicKey oracleKey,
                                                                   final PublicKey adminKey) {
    return List.of(
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey),
      createReadOnlySigner(adminKey)
    );
  }

  public static Instruction resetPerpMarketAmmOracleTwap(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey stateKey,
                                                         final PublicKey perpMarketKey,
                                                         final PublicKey oracleKey,
                                                         final PublicKey adminKey) {
    final var keys = resetPerpMarketAmmOracleTwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      perpMarketKey,
      oracleKey,
      adminKey
    );
    return resetPerpMarketAmmOracleTwap(invokedDriftProgramMeta, keys);
  }

  public static Instruction resetPerpMarketAmmOracleTwap(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, RESET_PERP_MARKET_AMM_ORACLE_TWAP_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_K_DISCRIMINATOR = toDiscriminator(72, 98, 9, 139, 129, 229, 172, 56);

  public static List<AccountMeta> updateKKeys(final AccountMeta invokedDriftProgramMeta                                              ,
                                              final PublicKey adminKey,
                                              final PublicKey stateKey,
                                              final PublicKey perpMarketKey,
                                              final PublicKey oracleKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey)
    );
  }

  public static Instruction updateK(final AccountMeta invokedDriftProgramMeta,
                                    final PublicKey adminKey,
                                    final PublicKey stateKey,
                                    final PublicKey perpMarketKey,
                                    final PublicKey oracleKey,
                                    final BigInteger sqrtK) {
    final var keys = updateKKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey,
      oracleKey
    );
    return updateK(invokedDriftProgramMeta, keys, sqrtK);
  }

  public static Instruction updateK(final AccountMeta invokedDriftProgramMeta                                    ,
                                    final List<AccountMeta> keys,
                                    final BigInteger sqrtK) {
    final byte[] _data = new byte[24];
    int i = UPDATE_K_DISCRIMINATOR.write(_data, 0);
    putInt128LE(_data, i, sqrtK);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateKIxData(Discriminator discriminator, BigInteger sqrtK) implements Borsh {  

    public static UpdateKIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdateKIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var sqrtK = getInt128LE(_data, i);
      return new UpdateKIxData(discriminator, sqrtK);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt128LE(_data, i, sqrtK);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MARGIN_RATIO_DISCRIMINATOR = toDiscriminator(130, 173, 107, 45, 119, 105, 26, 113);

  public static List<AccountMeta> updatePerpMarketMarginRatioKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMarginRatio(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey adminKey,
                                                        final PublicKey stateKey,
                                                        final PublicKey perpMarketKey,
                                                        final int marginRatioInitial,
                                                        final int marginRatioMaintenance) {
    final var keys = updatePerpMarketMarginRatioKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMarginRatio(invokedDriftProgramMeta, keys, marginRatioInitial, marginRatioMaintenance);
  }

  public static Instruction updatePerpMarketMarginRatio(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final int marginRatioInitial,
                                                        final int marginRatioMaintenance) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_MARGIN_RATIO_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, marginRatioInitial);
    i += 4;
    putInt32LE(_data, i, marginRatioMaintenance);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMarginRatioIxData(Discriminator discriminator, int marginRatioInitial, int marginRatioMaintenance) implements Borsh {  

    public static UpdatePerpMarketMarginRatioIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketMarginRatioIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marginRatioInitial = getInt32LE(_data, i);
      i += 4;
      final var marginRatioMaintenance = getInt32LE(_data, i);
      return new UpdatePerpMarketMarginRatioIxData(discriminator, marginRatioInitial, marginRatioMaintenance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, marginRatioInitial);
      i += 4;
      putInt32LE(_data, i, marginRatioMaintenance);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_HIGH_LEVERAGE_MARGIN_RATIO_DISCRIMINATOR = toDiscriminator(88, 112, 86, 49, 24, 116, 74, 157);

  public static List<AccountMeta> updatePerpMarketHighLeverageMarginRatioKeys(final AccountMeta invokedDriftProgramMeta                                                                              ,
                                                                              final PublicKey adminKey,
                                                                              final PublicKey stateKey,
                                                                              final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketHighLeverageMarginRatio(final AccountMeta invokedDriftProgramMeta,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey,
                                                                    final int marginRatioInitial,
                                                                    final int marginRatioMaintenance) {
    final var keys = updatePerpMarketHighLeverageMarginRatioKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketHighLeverageMarginRatio(invokedDriftProgramMeta, keys, marginRatioInitial, marginRatioMaintenance);
  }

  public static Instruction updatePerpMarketHighLeverageMarginRatio(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final List<AccountMeta> keys,
                                                                    final int marginRatioInitial,
                                                                    final int marginRatioMaintenance) {
    final byte[] _data = new byte[12];
    int i = UPDATE_PERP_MARKET_HIGH_LEVERAGE_MARGIN_RATIO_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marginRatioInitial);
    i += 2;
    putInt16LE(_data, i, marginRatioMaintenance);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketHighLeverageMarginRatioIxData(Discriminator discriminator, int marginRatioInitial, int marginRatioMaintenance) implements Borsh {  

    public static UpdatePerpMarketHighLeverageMarginRatioIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static UpdatePerpMarketHighLeverageMarginRatioIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marginRatioInitial = getInt16LE(_data, i);
      i += 2;
      final var marginRatioMaintenance = getInt16LE(_data, i);
      return new UpdatePerpMarketHighLeverageMarginRatioIxData(discriminator, marginRatioInitial, marginRatioMaintenance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marginRatioInitial);
      i += 2;
      putInt16LE(_data, i, marginRatioMaintenance);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_FUNDING_PERIOD_DISCRIMINATOR = toDiscriminator(171, 161, 69, 91, 129, 139, 161, 28);

  public static List<AccountMeta> updatePerpMarketFundingPeriodKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketFundingPeriod(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey perpMarketKey,
                                                          final long fundingPeriod) {
    final var keys = updatePerpMarketFundingPeriodKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketFundingPeriod(invokedDriftProgramMeta, keys, fundingPeriod);
  }

  public static Instruction updatePerpMarketFundingPeriod(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final long fundingPeriod) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_FUNDING_PERIOD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, fundingPeriod);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketFundingPeriodIxData(Discriminator discriminator, long fundingPeriod) implements Borsh {  

    public static UpdatePerpMarketFundingPeriodIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketFundingPeriodIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var fundingPeriod = getInt64LE(_data, i);
      return new UpdatePerpMarketFundingPeriodIxData(discriminator, fundingPeriod);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, fundingPeriod);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MAX_IMBALANCES_DISCRIMINATOR = toDiscriminator(15, 206, 73, 133, 60, 8, 86, 89);

  public static List<AccountMeta> updatePerpMarketMaxImbalancesKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMaxImbalances(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey perpMarketKey,
                                                          final long unrealizedMaxImbalance,
                                                          final long maxRevenueWithdrawPerPeriod,
                                                          final long quoteMaxInsurance) {
    final var keys = updatePerpMarketMaxImbalancesKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMaxImbalances(
      invokedDriftProgramMeta,
      keys,
      unrealizedMaxImbalance,
      maxRevenueWithdrawPerPeriod,
      quoteMaxInsurance
    );
  }

  public static Instruction updatePerpMarketMaxImbalances(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final long unrealizedMaxImbalance,
                                                          final long maxRevenueWithdrawPerPeriod,
                                                          final long quoteMaxInsurance) {
    final byte[] _data = new byte[32];
    int i = UPDATE_PERP_MARKET_MAX_IMBALANCES_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, unrealizedMaxImbalance);
    i += 8;
    putInt64LE(_data, i, maxRevenueWithdrawPerPeriod);
    i += 8;
    putInt64LE(_data, i, quoteMaxInsurance);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMaxImbalancesIxData(Discriminator discriminator,
                                                    long unrealizedMaxImbalance,
                                                    long maxRevenueWithdrawPerPeriod,
                                                    long quoteMaxInsurance) implements Borsh {  

    public static UpdatePerpMarketMaxImbalancesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 32;

    public static UpdatePerpMarketMaxImbalancesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var unrealizedMaxImbalance = getInt64LE(_data, i);
      i += 8;
      final var maxRevenueWithdrawPerPeriod = getInt64LE(_data, i);
      i += 8;
      final var quoteMaxInsurance = getInt64LE(_data, i);
      return new UpdatePerpMarketMaxImbalancesIxData(discriminator, unrealizedMaxImbalance, maxRevenueWithdrawPerPeriod, quoteMaxInsurance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, unrealizedMaxImbalance);
      i += 8;
      putInt64LE(_data, i, maxRevenueWithdrawPerPeriod);
      i += 8;
      putInt64LE(_data, i, quoteMaxInsurance);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_LIQUIDATION_FEE_DISCRIMINATOR = toDiscriminator(90, 137, 9, 145, 41, 8, 148, 117);

  public static List<AccountMeta> updatePerpMarketLiquidationFeeKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey adminKey,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketLiquidationFee(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey perpMarketKey,
                                                           final int liquidatorFee,
                                                           final int ifLiquidationFee) {
    final var keys = updatePerpMarketLiquidationFeeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketLiquidationFee(invokedDriftProgramMeta, keys, liquidatorFee, ifLiquidationFee);
  }

  public static Instruction updatePerpMarketLiquidationFee(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final int liquidatorFee,
                                                           final int ifLiquidationFee) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_LIQUIDATION_FEE_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, liquidatorFee);
    i += 4;
    putInt32LE(_data, i, ifLiquidationFee);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketLiquidationFeeIxData(Discriminator discriminator, int liquidatorFee, int ifLiquidationFee) implements Borsh {  

    public static UpdatePerpMarketLiquidationFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketLiquidationFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidatorFee = getInt32LE(_data, i);
      i += 4;
      final var ifLiquidationFee = getInt32LE(_data, i);
      return new UpdatePerpMarketLiquidationFeeIxData(discriminator, liquidatorFee, ifLiquidationFee);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, liquidatorFee);
      i += 4;
      putInt32LE(_data, i, ifLiquidationFee);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_LP_POOL_ID_DISCRIMINATOR = toDiscriminator(119, 208, 154, 88, 165, 92, 21, 188);

  public static List<AccountMeta> updatePerpMarketLpPoolIdKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey adminKey,
                                                               final PublicKey stateKey,
                                                               final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketLpPoolId(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey adminKey,
                                                     final PublicKey stateKey,
                                                     final PublicKey perpMarketKey,
                                                     final int lpPoolId) {
    final var keys = updatePerpMarketLpPoolIdKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketLpPoolId(invokedDriftProgramMeta, keys, lpPoolId);
  }

  public static Instruction updatePerpMarketLpPoolId(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final int lpPoolId) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_LP_POOL_ID_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) lpPoolId;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketLpPoolIdIxData(Discriminator discriminator, int lpPoolId) implements Borsh {  

    public static UpdatePerpMarketLpPoolIdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketLpPoolIdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lpPoolId = _data[i] & 0xFF;
      return new UpdatePerpMarketLpPoolIdIxData(discriminator, lpPoolId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) lpPoolId;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_INSURANCE_FUND_UNSTAKING_PERIOD_DISCRIMINATOR = toDiscriminator(44, 69, 43, 226, 204, 223, 202, 52);

  public static List<AccountMeta> updateInsuranceFundUnstakingPeriodKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey adminKey,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateInsuranceFundUnstakingPeriod(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey adminKey,
                                                               final PublicKey stateKey,
                                                               final PublicKey spotMarketKey,
                                                               final long insuranceFundUnstakingPeriod) {
    final var keys = updateInsuranceFundUnstakingPeriodKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateInsuranceFundUnstakingPeriod(invokedDriftProgramMeta, keys, insuranceFundUnstakingPeriod);
  }

  public static Instruction updateInsuranceFundUnstakingPeriod(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys,
                                                               final long insuranceFundUnstakingPeriod) {
    final byte[] _data = new byte[16];
    int i = UPDATE_INSURANCE_FUND_UNSTAKING_PERIOD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, insuranceFundUnstakingPeriod);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateInsuranceFundUnstakingPeriodIxData(Discriminator discriminator, long insuranceFundUnstakingPeriod) implements Borsh {  

    public static UpdateInsuranceFundUnstakingPeriodIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateInsuranceFundUnstakingPeriodIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var insuranceFundUnstakingPeriod = getInt64LE(_data, i);
      return new UpdateInsuranceFundUnstakingPeriodIxData(discriminator, insuranceFundUnstakingPeriod);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, insuranceFundUnstakingPeriod);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_POOL_ID_DISCRIMINATOR = toDiscriminator(22, 213, 197, 160, 139, 193, 81, 149);

  public static List<AccountMeta> updateSpotMarketPoolIdKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketPoolId(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey spotMarketKey,
                                                   final int poolId) {
    final var keys = updateSpotMarketPoolIdKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketPoolId(invokedDriftProgramMeta, keys, poolId);
  }

  public static Instruction updateSpotMarketPoolId(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final int poolId) {
    final byte[] _data = new byte[9];
    int i = UPDATE_SPOT_MARKET_POOL_ID_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) poolId;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketPoolIdIxData(Discriminator discriminator, int poolId) implements Borsh {  

    public static UpdateSpotMarketPoolIdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotMarketPoolIdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var poolId = _data[i] & 0xFF;
      return new UpdateSpotMarketPoolIdIxData(discriminator, poolId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) poolId;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_LIQUIDATION_FEE_DISCRIMINATOR = toDiscriminator(11, 13, 255, 53, 56, 136, 104, 177);

  public static List<AccountMeta> updateSpotMarketLiquidationFeeKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey adminKey,
                                                                     final PublicKey stateKey,
                                                                     final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketLiquidationFee(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey spotMarketKey,
                                                           final int liquidatorFee,
                                                           final int ifLiquidationFee) {
    final var keys = updateSpotMarketLiquidationFeeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketLiquidationFee(invokedDriftProgramMeta, keys, liquidatorFee, ifLiquidationFee);
  }

  public static Instruction updateSpotMarketLiquidationFee(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final int liquidatorFee,
                                                           final int ifLiquidationFee) {
    final byte[] _data = new byte[16];
    int i = UPDATE_SPOT_MARKET_LIQUIDATION_FEE_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, liquidatorFee);
    i += 4;
    putInt32LE(_data, i, ifLiquidationFee);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketLiquidationFeeIxData(Discriminator discriminator, int liquidatorFee, int ifLiquidationFee) implements Borsh {  

    public static UpdateSpotMarketLiquidationFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateSpotMarketLiquidationFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidatorFee = getInt32LE(_data, i);
      i += 4;
      final var ifLiquidationFee = getInt32LE(_data, i);
      return new UpdateSpotMarketLiquidationFeeIxData(discriminator, liquidatorFee, ifLiquidationFee);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, liquidatorFee);
      i += 4;
      putInt32LE(_data, i, ifLiquidationFee);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_WITHDRAW_GUARD_THRESHOLD_DISCRIMINATOR = toDiscriminator(56, 18, 39, 61, 155, 211, 44, 133);

  public static List<AccountMeta> updateWithdrawGuardThresholdKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateWithdrawGuardThreshold(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey spotMarketKey,
                                                         final long withdrawGuardThreshold) {
    final var keys = updateWithdrawGuardThresholdKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateWithdrawGuardThreshold(invokedDriftProgramMeta, keys, withdrawGuardThreshold);
  }

  public static Instruction updateWithdrawGuardThreshold(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final long withdrawGuardThreshold) {
    final byte[] _data = new byte[16];
    int i = UPDATE_WITHDRAW_GUARD_THRESHOLD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, withdrawGuardThreshold);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateWithdrawGuardThresholdIxData(Discriminator discriminator, long withdrawGuardThreshold) implements Borsh {  

    public static UpdateWithdrawGuardThresholdIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateWithdrawGuardThresholdIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var withdrawGuardThreshold = getInt64LE(_data, i);
      return new UpdateWithdrawGuardThresholdIxData(discriminator, withdrawGuardThreshold);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, withdrawGuardThreshold);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_IF_FACTOR_DISCRIMINATOR = toDiscriminator(147, 30, 224, 34, 18, 230, 105, 4);

  public static List<AccountMeta> updateSpotMarketIfFactorKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey adminKey,
                                                               final PublicKey stateKey,
                                                               final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketIfFactor(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey adminKey,
                                                     final PublicKey stateKey,
                                                     final PublicKey spotMarketKey,
                                                     final int spotMarketIndex,
                                                     final int userIfFactor,
                                                     final int totalIfFactor) {
    final var keys = updateSpotMarketIfFactorKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketIfFactor(
      invokedDriftProgramMeta,
      keys,
      spotMarketIndex,
      userIfFactor,
      totalIfFactor
    );
  }

  public static Instruction updateSpotMarketIfFactor(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final int spotMarketIndex,
                                                     final int userIfFactor,
                                                     final int totalIfFactor) {
    final byte[] _data = new byte[18];
    int i = UPDATE_SPOT_MARKET_IF_FACTOR_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    putInt32LE(_data, i, userIfFactor);
    i += 4;
    putInt32LE(_data, i, totalIfFactor);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketIfFactorIxData(Discriminator discriminator,
                                               int spotMarketIndex,
                                               int userIfFactor,
                                               int totalIfFactor) implements Borsh {  

    public static UpdateSpotMarketIfFactorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static UpdateSpotMarketIfFactorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var spotMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var userIfFactor = getInt32LE(_data, i);
      i += 4;
      final var totalIfFactor = getInt32LE(_data, i);
      return new UpdateSpotMarketIfFactorIxData(discriminator, spotMarketIndex, userIfFactor, totalIfFactor);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, spotMarketIndex);
      i += 2;
      putInt32LE(_data, i, userIfFactor);
      i += 4;
      putInt32LE(_data, i, totalIfFactor);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_REVENUE_SETTLE_PERIOD_DISCRIMINATOR = toDiscriminator(81, 92, 126, 41, 250, 225, 156, 219);

  public static List<AccountMeta> updateSpotMarketRevenueSettlePeriodKeys(final AccountMeta invokedDriftProgramMeta                                                                          ,
                                                                          final PublicKey adminKey,
                                                                          final PublicKey stateKey,
                                                                          final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketRevenueSettlePeriod(final AccountMeta invokedDriftProgramMeta,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey spotMarketKey,
                                                                final long revenueSettlePeriod) {
    final var keys = updateSpotMarketRevenueSettlePeriodKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketRevenueSettlePeriod(invokedDriftProgramMeta, keys, revenueSettlePeriod);
  }

  public static Instruction updateSpotMarketRevenueSettlePeriod(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final List<AccountMeta> keys,
                                                                final long revenueSettlePeriod) {
    final byte[] _data = new byte[16];
    int i = UPDATE_SPOT_MARKET_REVENUE_SETTLE_PERIOD_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, revenueSettlePeriod);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketRevenueSettlePeriodIxData(Discriminator discriminator, long revenueSettlePeriod) implements Borsh {  

    public static UpdateSpotMarketRevenueSettlePeriodIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateSpotMarketRevenueSettlePeriodIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var revenueSettlePeriod = getInt64LE(_data, i);
      return new UpdateSpotMarketRevenueSettlePeriodIxData(discriminator, revenueSettlePeriod);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, revenueSettlePeriod);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_STATUS_DISCRIMINATOR = toDiscriminator(78, 94, 16, 188, 193, 110, 231, 31);

  public static List<AccountMeta> updateSpotMarketStatusKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketStatus(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey spotMarketKey,
                                                   final MarketStatus status) {
    final var keys = updateSpotMarketStatusKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketStatus(invokedDriftProgramMeta, keys, status);
  }

  public static Instruction updateSpotMarketStatus(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final MarketStatus status) {
    final byte[] _data = new byte[8 + Borsh.len(status)];
    int i = UPDATE_SPOT_MARKET_STATUS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(status, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketStatusIxData(Discriminator discriminator, MarketStatus status) implements Borsh {  

    public static UpdateSpotMarketStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotMarketStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = MarketStatus.read(_data, i);
      return new UpdateSpotMarketStatusIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(status, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_PAUSED_OPERATIONS_DISCRIMINATOR = toDiscriminator(100, 61, 153, 81, 180, 12, 6, 248);

  public static List<AccountMeta> updateSpotMarketPausedOperationsKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketPausedOperations(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey,
                                                             final int pausedOperations) {
    final var keys = updateSpotMarketPausedOperationsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketPausedOperations(invokedDriftProgramMeta, keys, pausedOperations);
  }

  public static Instruction updateSpotMarketPausedOperations(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final int pausedOperations) {
    final byte[] _data = new byte[9];
    int i = UPDATE_SPOT_MARKET_PAUSED_OPERATIONS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) pausedOperations;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketPausedOperationsIxData(Discriminator discriminator, int pausedOperations) implements Borsh {  

    public static UpdateSpotMarketPausedOperationsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotMarketPausedOperationsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var pausedOperations = _data[i] & 0xFF;
      return new UpdateSpotMarketPausedOperationsIxData(discriminator, pausedOperations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) pausedOperations;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_ASSET_TIER_DISCRIMINATOR = toDiscriminator(253, 209, 231, 14, 242, 208, 243, 130);

  public static List<AccountMeta> updateSpotMarketAssetTierKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketAssetTier(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey spotMarketKey,
                                                      final AssetTier assetTier) {
    final var keys = updateSpotMarketAssetTierKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketAssetTier(invokedDriftProgramMeta, keys, assetTier);
  }

  public static Instruction updateSpotMarketAssetTier(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final AssetTier assetTier) {
    final byte[] _data = new byte[8 + Borsh.len(assetTier)];
    int i = UPDATE_SPOT_MARKET_ASSET_TIER_DISCRIMINATOR.write(_data, 0);
    Borsh.write(assetTier, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketAssetTierIxData(Discriminator discriminator, AssetTier assetTier) implements Borsh {  

    public static UpdateSpotMarketAssetTierIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotMarketAssetTierIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var assetTier = AssetTier.read(_data, i);
      return new UpdateSpotMarketAssetTierIxData(discriminator, assetTier);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(assetTier, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_MARGIN_WEIGHTS_DISCRIMINATOR = toDiscriminator(109, 33, 87, 195, 255, 36, 6, 81);

  public static List<AccountMeta> updateSpotMarketMarginWeightsKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketMarginWeights(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey spotMarketKey,
                                                          final int initialAssetWeight,
                                                          final int maintenanceAssetWeight,
                                                          final int initialLiabilityWeight,
                                                          final int maintenanceLiabilityWeight,
                                                          final int imfFactor) {
    final var keys = updateSpotMarketMarginWeightsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketMarginWeights(
      invokedDriftProgramMeta,
      keys,
      initialAssetWeight,
      maintenanceAssetWeight,
      initialLiabilityWeight,
      maintenanceLiabilityWeight,
      imfFactor
    );
  }

  public static Instruction updateSpotMarketMarginWeights(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int initialAssetWeight,
                                                          final int maintenanceAssetWeight,
                                                          final int initialLiabilityWeight,
                                                          final int maintenanceLiabilityWeight,
                                                          final int imfFactor) {
    final byte[] _data = new byte[28];
    int i = UPDATE_SPOT_MARKET_MARGIN_WEIGHTS_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, initialAssetWeight);
    i += 4;
    putInt32LE(_data, i, maintenanceAssetWeight);
    i += 4;
    putInt32LE(_data, i, initialLiabilityWeight);
    i += 4;
    putInt32LE(_data, i, maintenanceLiabilityWeight);
    i += 4;
    putInt32LE(_data, i, imfFactor);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketMarginWeightsIxData(Discriminator discriminator,
                                                    int initialAssetWeight,
                                                    int maintenanceAssetWeight,
                                                    int initialLiabilityWeight,
                                                    int maintenanceLiabilityWeight,
                                                    int imfFactor) implements Borsh {  

    public static UpdateSpotMarketMarginWeightsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 28;

    public static UpdateSpotMarketMarginWeightsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var initialAssetWeight = getInt32LE(_data, i);
      i += 4;
      final var maintenanceAssetWeight = getInt32LE(_data, i);
      i += 4;
      final var initialLiabilityWeight = getInt32LE(_data, i);
      i += 4;
      final var maintenanceLiabilityWeight = getInt32LE(_data, i);
      i += 4;
      final var imfFactor = getInt32LE(_data, i);
      return new UpdateSpotMarketMarginWeightsIxData(discriminator,
                                                     initialAssetWeight,
                                                     maintenanceAssetWeight,
                                                     initialLiabilityWeight,
                                                     maintenanceLiabilityWeight,
                                                     imfFactor);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, initialAssetWeight);
      i += 4;
      putInt32LE(_data, i, maintenanceAssetWeight);
      i += 4;
      putInt32LE(_data, i, initialLiabilityWeight);
      i += 4;
      putInt32LE(_data, i, maintenanceLiabilityWeight);
      i += 4;
      putInt32LE(_data, i, imfFactor);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_BORROW_RATE_DISCRIMINATOR = toDiscriminator(71, 239, 236, 153, 210, 62, 254, 76);

  public static List<AccountMeta> updateSpotMarketBorrowRateKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketBorrowRate(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey adminKey,
                                                       final PublicKey stateKey,
                                                       final PublicKey spotMarketKey,
                                                       final int optimalUtilization,
                                                       final int optimalBorrowRate,
                                                       final int maxBorrowRate,
                                                       final OptionalInt minBorrowRate) {
    final var keys = updateSpotMarketBorrowRateKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketBorrowRate(
      invokedDriftProgramMeta,
      keys,
      optimalUtilization,
      optimalBorrowRate,
      maxBorrowRate,
      minBorrowRate
    );
  }

  public static Instruction updateSpotMarketBorrowRate(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final int optimalUtilization,
                                                       final int optimalBorrowRate,
                                                       final int maxBorrowRate,
                                                       final OptionalInt minBorrowRate) {
    final byte[] _data = new byte[
    20
    + (minBorrowRate == null || minBorrowRate.isEmpty() ? 1 : 2)
    ];
    int i = UPDATE_SPOT_MARKET_BORROW_RATE_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, optimalUtilization);
    i += 4;
    putInt32LE(_data, i, optimalBorrowRate);
    i += 4;
    putInt32LE(_data, i, maxBorrowRate);
    i += 4;
    Borsh.writeOptionalbyte(minBorrowRate, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketBorrowRateIxData(Discriminator discriminator,
                                                 int optimalUtilization,
                                                 int optimalBorrowRate,
                                                 int maxBorrowRate,
                                                 OptionalInt minBorrowRate) implements Borsh {  

    public static UpdateSpotMarketBorrowRateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateSpotMarketBorrowRateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var optimalUtilization = getInt32LE(_data, i);
      i += 4;
      final var optimalBorrowRate = getInt32LE(_data, i);
      i += 4;
      final var maxBorrowRate = getInt32LE(_data, i);
      i += 4;
      final OptionalInt minBorrowRate;
      if (_data[i] == 0) {
        minBorrowRate = OptionalInt.empty();
      } else {
        ++i;
      ;
        minBorrowRate = OptionalInt.of(_data[i] & 0xFF);
      }
      return new UpdateSpotMarketBorrowRateIxData(discriminator,
                                                  optimalUtilization,
                                                  optimalBorrowRate,
                                                  maxBorrowRate,
                                                  minBorrowRate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, optimalUtilization);
      i += 4;
      putInt32LE(_data, i, optimalBorrowRate);
      i += 4;
      putInt32LE(_data, i, maxBorrowRate);
      i += 4;
      i += Borsh.writeOptionalbyte(minBorrowRate, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 4 + 4 + 4 + (minBorrowRate == null || minBorrowRate.isEmpty() ? 1 : (1 + 1));
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_MAX_TOKEN_DEPOSITS_DISCRIMINATOR = toDiscriminator(56, 191, 79, 18, 26, 121, 80, 208);

  public static List<AccountMeta> updateSpotMarketMaxTokenDepositsKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketMaxTokenDeposits(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey,
                                                             final long maxTokenDeposits) {
    final var keys = updateSpotMarketMaxTokenDepositsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketMaxTokenDeposits(invokedDriftProgramMeta, keys, maxTokenDeposits);
  }

  public static Instruction updateSpotMarketMaxTokenDeposits(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final long maxTokenDeposits) {
    final byte[] _data = new byte[16];
    int i = UPDATE_SPOT_MARKET_MAX_TOKEN_DEPOSITS_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxTokenDeposits);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketMaxTokenDepositsIxData(Discriminator discriminator, long maxTokenDeposits) implements Borsh {  

    public static UpdateSpotMarketMaxTokenDepositsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateSpotMarketMaxTokenDepositsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxTokenDeposits = getInt64LE(_data, i);
      return new UpdateSpotMarketMaxTokenDepositsIxData(discriminator, maxTokenDeposits);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxTokenDeposits);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_MAX_TOKEN_BORROWS_DISCRIMINATOR = toDiscriminator(57, 102, 204, 212, 253, 95, 13, 199);

  public static List<AccountMeta> updateSpotMarketMaxTokenBorrowsKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey stateKey,
                                                                      final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketMaxTokenBorrows(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final PublicKey spotMarketKey,
                                                            final int maxTokenBorrowsFraction) {
    final var keys = updateSpotMarketMaxTokenBorrowsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketMaxTokenBorrows(invokedDriftProgramMeta, keys, maxTokenBorrowsFraction);
  }

  public static Instruction updateSpotMarketMaxTokenBorrows(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final int maxTokenBorrowsFraction) {
    final byte[] _data = new byte[10];
    int i = UPDATE_SPOT_MARKET_MAX_TOKEN_BORROWS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, maxTokenBorrowsFraction);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketMaxTokenBorrowsIxData(Discriminator discriminator, int maxTokenBorrowsFraction) implements Borsh {  

    public static UpdateSpotMarketMaxTokenBorrowsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateSpotMarketMaxTokenBorrowsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxTokenBorrowsFraction = getInt16LE(_data, i);
      return new UpdateSpotMarketMaxTokenBorrowsIxData(discriminator, maxTokenBorrowsFraction);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, maxTokenBorrowsFraction);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_SCALE_INITIAL_ASSET_WEIGHT_START_DISCRIMINATOR = toDiscriminator(217, 204, 204, 118, 204, 130, 225, 147);

  public static List<AccountMeta> updateSpotMarketScaleInitialAssetWeightStartKeys(final AccountMeta invokedDriftProgramMeta                                                                                   ,
                                                                                   final PublicKey adminKey,
                                                                                   final PublicKey stateKey,
                                                                                   final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketScaleInitialAssetWeightStart(final AccountMeta invokedDriftProgramMeta,
                                                                         final PublicKey adminKey,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey spotMarketKey,
                                                                         final long scaleInitialAssetWeightStart) {
    final var keys = updateSpotMarketScaleInitialAssetWeightStartKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketScaleInitialAssetWeightStart(invokedDriftProgramMeta, keys, scaleInitialAssetWeightStart);
  }

  public static Instruction updateSpotMarketScaleInitialAssetWeightStart(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final List<AccountMeta> keys,
                                                                         final long scaleInitialAssetWeightStart) {
    final byte[] _data = new byte[16];
    int i = UPDATE_SPOT_MARKET_SCALE_INITIAL_ASSET_WEIGHT_START_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, scaleInitialAssetWeightStart);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketScaleInitialAssetWeightStartIxData(Discriminator discriminator, long scaleInitialAssetWeightStart) implements Borsh {  

    public static UpdateSpotMarketScaleInitialAssetWeightStartIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateSpotMarketScaleInitialAssetWeightStartIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var scaleInitialAssetWeightStart = getInt64LE(_data, i);
      return new UpdateSpotMarketScaleInitialAssetWeightStartIxData(discriminator, scaleInitialAssetWeightStart);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, scaleInitialAssetWeightStart);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_ORACLE_DISCRIMINATOR = toDiscriminator(114, 184, 102, 37, 246, 186, 180, 99);

  public static List<AccountMeta> updateSpotMarketOracleKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey spotMarketKey,
                                                             final PublicKey oracleKey,
                                                             final PublicKey oldOracleKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey),
      createRead(oracleKey),
      createRead(oldOracleKey)
    );
  }

  public static Instruction updateSpotMarketOracle(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey spotMarketKey,
                                                   final PublicKey oracleKey,
                                                   final PublicKey oldOracleKey,
                                                   final PublicKey oracle,
                                                   final OracleSource oracleSource,
                                                   final boolean skipInvariantCheck) {
    final var keys = updateSpotMarketOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey,
      oracleKey,
      oldOracleKey
    );
    return updateSpotMarketOracle(
      invokedDriftProgramMeta,
      keys,
      oracle,
      oracleSource,
      skipInvariantCheck
    );
  }

  public static Instruction updateSpotMarketOracle(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final PublicKey oracle,
                                                   final OracleSource oracleSource,
                                                   final boolean skipInvariantCheck) {
    final byte[] _data = new byte[41 + Borsh.len(oracleSource)];
    int i = UPDATE_SPOT_MARKET_ORACLE_DISCRIMINATOR.write(_data, 0);
    oracle.write(_data, i);
    i += 32;
    i += Borsh.write(oracleSource, _data, i);
    _data[i] = (byte) (skipInvariantCheck ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketOracleIxData(Discriminator discriminator,
                                             PublicKey oracle,
                                             OracleSource oracleSource,
                                             boolean skipInvariantCheck) implements Borsh {  

    public static UpdateSpotMarketOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 42;

    public static UpdateSpotMarketOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var oracle = readPubKey(_data, i);
      i += 32;
      final var oracleSource = OracleSource.read(_data, i);
      i += Borsh.len(oracleSource);
      final var skipInvariantCheck = _data[i] == 1;
      return new UpdateSpotMarketOracleIxData(discriminator, oracle, oracleSource, skipInvariantCheck);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      oracle.write(_data, i);
      i += 32;
      i += Borsh.write(oracleSource, _data, i);
      _data[i] = (byte) (skipInvariantCheck ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_STEP_SIZE_AND_TICK_SIZE_DISCRIMINATOR = toDiscriminator(238, 153, 137, 80, 206, 59, 250, 61);

  public static List<AccountMeta> updateSpotMarketStepSizeAndTickSizeKeys(final AccountMeta invokedDriftProgramMeta                                                                          ,
                                                                          final PublicKey adminKey,
                                                                          final PublicKey stateKey,
                                                                          final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketStepSizeAndTickSize(final AccountMeta invokedDriftProgramMeta,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey spotMarketKey,
                                                                final long stepSize,
                                                                final long tickSize) {
    final var keys = updateSpotMarketStepSizeAndTickSizeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketStepSizeAndTickSize(invokedDriftProgramMeta, keys, stepSize, tickSize);
  }

  public static Instruction updateSpotMarketStepSizeAndTickSize(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final List<AccountMeta> keys,
                                                                final long stepSize,
                                                                final long tickSize) {
    final byte[] _data = new byte[24];
    int i = UPDATE_SPOT_MARKET_STEP_SIZE_AND_TICK_SIZE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, stepSize);
    i += 8;
    putInt64LE(_data, i, tickSize);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketStepSizeAndTickSizeIxData(Discriminator discriminator, long stepSize, long tickSize) implements Borsh {  

    public static UpdateSpotMarketStepSizeAndTickSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdateSpotMarketStepSizeAndTickSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var stepSize = getInt64LE(_data, i);
      i += 8;
      final var tickSize = getInt64LE(_data, i);
      return new UpdateSpotMarketStepSizeAndTickSizeIxData(discriminator, stepSize, tickSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, stepSize);
      i += 8;
      putInt64LE(_data, i, tickSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_MIN_ORDER_SIZE_DISCRIMINATOR = toDiscriminator(93, 128, 11, 119, 26, 20, 181, 50);

  public static List<AccountMeta> updateSpotMarketMinOrderSizeKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketMinOrderSize(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey spotMarketKey,
                                                         final long orderSize) {
    final var keys = updateSpotMarketMinOrderSizeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketMinOrderSize(invokedDriftProgramMeta, keys, orderSize);
  }

  public static Instruction updateSpotMarketMinOrderSize(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final long orderSize) {
    final byte[] _data = new byte[16];
    int i = UPDATE_SPOT_MARKET_MIN_ORDER_SIZE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, orderSize);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketMinOrderSizeIxData(Discriminator discriminator, long orderSize) implements Borsh {  

    public static UpdateSpotMarketMinOrderSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateSpotMarketMinOrderSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderSize = getInt64LE(_data, i);
      return new UpdateSpotMarketMinOrderSizeIxData(discriminator, orderSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, orderSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_ORDERS_ENABLED_DISCRIMINATOR = toDiscriminator(190, 79, 206, 15, 26, 229, 229, 43);

  public static List<AccountMeta> updateSpotMarketOrdersEnabledKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketOrdersEnabled(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey spotMarketKey,
                                                          final boolean ordersEnabled) {
    final var keys = updateSpotMarketOrdersEnabledKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketOrdersEnabled(invokedDriftProgramMeta, keys, ordersEnabled);
  }

  public static Instruction updateSpotMarketOrdersEnabled(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final boolean ordersEnabled) {
    final byte[] _data = new byte[9];
    int i = UPDATE_SPOT_MARKET_ORDERS_ENABLED_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (ordersEnabled ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketOrdersEnabledIxData(Discriminator discriminator, boolean ordersEnabled) implements Borsh {  

    public static UpdateSpotMarketOrdersEnabledIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotMarketOrdersEnabledIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ordersEnabled = _data[i] == 1;
      return new UpdateSpotMarketOrdersEnabledIxData(discriminator, ordersEnabled);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (ordersEnabled ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_IF_PAUSED_OPERATIONS_DISCRIMINATOR = toDiscriminator(101, 215, 79, 74, 59, 41, 79, 12);

  public static List<AccountMeta> updateSpotMarketIfPausedOperationsKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey adminKey,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketIfPausedOperations(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey adminKey,
                                                               final PublicKey stateKey,
                                                               final PublicKey spotMarketKey,
                                                               final int pausedOperations) {
    final var keys = updateSpotMarketIfPausedOperationsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketIfPausedOperations(invokedDriftProgramMeta, keys, pausedOperations);
  }

  public static Instruction updateSpotMarketIfPausedOperations(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys,
                                                               final int pausedOperations) {
    final byte[] _data = new byte[9];
    int i = UPDATE_SPOT_MARKET_IF_PAUSED_OPERATIONS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) pausedOperations;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketIfPausedOperationsIxData(Discriminator discriminator, int pausedOperations) implements Borsh {  

    public static UpdateSpotMarketIfPausedOperationsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotMarketIfPausedOperationsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var pausedOperations = _data[i] & 0xFF;
      return new UpdateSpotMarketIfPausedOperationsIxData(discriminator, pausedOperations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) pausedOperations;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_NAME_DISCRIMINATOR = toDiscriminator(17, 208, 1, 1, 162, 211, 188, 224);

  public static List<AccountMeta> updateSpotMarketNameKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketName(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey spotMarketKey,
                                                 final byte[] name) {
    final var keys = updateSpotMarketNameKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketName(invokedDriftProgramMeta, keys, name);
  }

  public static Instruction updateSpotMarketName(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final byte[] name) {
    final byte[] _data = new byte[8 + Borsh.lenArray(name)];
    int i = UPDATE_SPOT_MARKET_NAME_DISCRIMINATOR.write(_data, 0);
    Borsh.writeArrayChecked(name, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketNameIxData(Discriminator discriminator, byte[] name) implements Borsh {  

    public static UpdateSpotMarketNameIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int NAME_LEN = 32;

    public static UpdateSpotMarketNameIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var name = new byte[32];
      Borsh.readArray(name, _data, i);
      return new UpdateSpotMarketNameIxData(discriminator, name);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_STATUS_DISCRIMINATOR = toDiscriminator(71, 201, 175, 122, 255, 207, 196, 207);

  public static List<AccountMeta> updatePerpMarketStatusKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketStatus(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey perpMarketKey,
                                                   final MarketStatus status) {
    final var keys = updatePerpMarketStatusKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketStatus(invokedDriftProgramMeta, keys, status);
  }

  public static Instruction updatePerpMarketStatus(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final MarketStatus status) {
    final byte[] _data = new byte[8 + Borsh.len(status)];
    int i = UPDATE_PERP_MARKET_STATUS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(status, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketStatusIxData(Discriminator discriminator, MarketStatus status) implements Borsh {  

    public static UpdatePerpMarketStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var status = MarketStatus.read(_data, i);
      return new UpdatePerpMarketStatusIxData(discriminator, status);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(status, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_PAUSED_OPERATIONS_DISCRIMINATOR = toDiscriminator(53, 16, 136, 132, 30, 220, 121, 85);

  public static List<AccountMeta> updatePerpMarketPausedOperationsKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketPausedOperations(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey perpMarketKey,
                                                             final int pausedOperations) {
    final var keys = updatePerpMarketPausedOperationsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketPausedOperations(invokedDriftProgramMeta, keys, pausedOperations);
  }

  public static Instruction updatePerpMarketPausedOperations(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final int pausedOperations) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_PAUSED_OPERATIONS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) pausedOperations;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketPausedOperationsIxData(Discriminator discriminator, int pausedOperations) implements Borsh {  

    public static UpdatePerpMarketPausedOperationsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketPausedOperationsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var pausedOperations = _data[i] & 0xFF;
      return new UpdatePerpMarketPausedOperationsIxData(discriminator, pausedOperations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) pausedOperations;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_CONTRACT_TIER_DISCRIMINATOR = toDiscriminator(236, 128, 15, 95, 203, 214, 68, 117);

  public static List<AccountMeta> updatePerpMarketContractTierKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketContractTier(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey perpMarketKey,
                                                         final ContractTier contractTier) {
    final var keys = updatePerpMarketContractTierKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketContractTier(invokedDriftProgramMeta, keys, contractTier);
  }

  public static Instruction updatePerpMarketContractTier(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final ContractTier contractTier) {
    final byte[] _data = new byte[8 + Borsh.len(contractTier)];
    int i = UPDATE_PERP_MARKET_CONTRACT_TIER_DISCRIMINATOR.write(_data, 0);
    Borsh.write(contractTier, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketContractTierIxData(Discriminator discriminator, ContractTier contractTier) implements Borsh {  

    public static UpdatePerpMarketContractTierIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketContractTierIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var contractTier = ContractTier.read(_data, i);
      return new UpdatePerpMarketContractTierIxData(discriminator, contractTier);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(contractTier, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_IMF_FACTOR_DISCRIMINATOR = toDiscriminator(207, 194, 56, 132, 35, 67, 71, 244);

  public static List<AccountMeta> updatePerpMarketImfFactorKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketImfFactor(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey perpMarketKey,
                                                      final int imfFactor,
                                                      final int unrealizedPnlImfFactor) {
    final var keys = updatePerpMarketImfFactorKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketImfFactor(invokedDriftProgramMeta, keys, imfFactor, unrealizedPnlImfFactor);
  }

  public static Instruction updatePerpMarketImfFactor(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int imfFactor,
                                                      final int unrealizedPnlImfFactor) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_IMF_FACTOR_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, imfFactor);
    i += 4;
    putInt32LE(_data, i, unrealizedPnlImfFactor);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketImfFactorIxData(Discriminator discriminator, int imfFactor, int unrealizedPnlImfFactor) implements Borsh {  

    public static UpdatePerpMarketImfFactorIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketImfFactorIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var imfFactor = getInt32LE(_data, i);
      i += 4;
      final var unrealizedPnlImfFactor = getInt32LE(_data, i);
      return new UpdatePerpMarketImfFactorIxData(discriminator, imfFactor, unrealizedPnlImfFactor);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, imfFactor);
      i += 4;
      putInt32LE(_data, i, unrealizedPnlImfFactor);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_UNREALIZED_ASSET_WEIGHT_DISCRIMINATOR = toDiscriminator(135, 132, 205, 165, 109, 150, 166, 106);

  public static List<AccountMeta> updatePerpMarketUnrealizedAssetWeightKeys(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final PublicKey adminKey,
                                                                            final PublicKey stateKey,
                                                                            final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketUnrealizedAssetWeight(final AccountMeta invokedDriftProgramMeta,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey perpMarketKey,
                                                                  final int unrealizedInitialAssetWeight,
                                                                  final int unrealizedMaintenanceAssetWeight) {
    final var keys = updatePerpMarketUnrealizedAssetWeightKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketUnrealizedAssetWeight(invokedDriftProgramMeta, keys, unrealizedInitialAssetWeight, unrealizedMaintenanceAssetWeight);
  }

  public static Instruction updatePerpMarketUnrealizedAssetWeight(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final List<AccountMeta> keys,
                                                                  final int unrealizedInitialAssetWeight,
                                                                  final int unrealizedMaintenanceAssetWeight) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_UNREALIZED_ASSET_WEIGHT_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, unrealizedInitialAssetWeight);
    i += 4;
    putInt32LE(_data, i, unrealizedMaintenanceAssetWeight);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketUnrealizedAssetWeightIxData(Discriminator discriminator, int unrealizedInitialAssetWeight, int unrealizedMaintenanceAssetWeight) implements Borsh {  

    public static UpdatePerpMarketUnrealizedAssetWeightIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketUnrealizedAssetWeightIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var unrealizedInitialAssetWeight = getInt32LE(_data, i);
      i += 4;
      final var unrealizedMaintenanceAssetWeight = getInt32LE(_data, i);
      return new UpdatePerpMarketUnrealizedAssetWeightIxData(discriminator, unrealizedInitialAssetWeight, unrealizedMaintenanceAssetWeight);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, unrealizedInitialAssetWeight);
      i += 4;
      putInt32LE(_data, i, unrealizedMaintenanceAssetWeight);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_CONCENTRATION_COEF_DISCRIMINATOR = toDiscriminator(24, 78, 232, 126, 169, 176, 230, 16);

  public static List<AccountMeta> updatePerpMarketConcentrationCoefKeys(final AccountMeta invokedDriftProgramMeta                                                                        ,
                                                                        final PublicKey adminKey,
                                                                        final PublicKey stateKey,
                                                                        final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketConcentrationCoef(final AccountMeta invokedDriftProgramMeta,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final PublicKey perpMarketKey,
                                                              final BigInteger concentrationScale) {
    final var keys = updatePerpMarketConcentrationCoefKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketConcentrationCoef(invokedDriftProgramMeta, keys, concentrationScale);
  }

  public static Instruction updatePerpMarketConcentrationCoef(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final List<AccountMeta> keys,
                                                              final BigInteger concentrationScale) {
    final byte[] _data = new byte[24];
    int i = UPDATE_PERP_MARKET_CONCENTRATION_COEF_DISCRIMINATOR.write(_data, 0);
    putInt128LE(_data, i, concentrationScale);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketConcentrationCoefIxData(Discriminator discriminator, BigInteger concentrationScale) implements Borsh {  

    public static UpdatePerpMarketConcentrationCoefIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdatePerpMarketConcentrationCoefIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var concentrationScale = getInt128LE(_data, i);
      return new UpdatePerpMarketConcentrationCoefIxData(discriminator, concentrationScale);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt128LE(_data, i, concentrationScale);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_CURVE_UPDATE_INTENSITY_DISCRIMINATOR = toDiscriminator(50, 131, 6, 156, 226, 231, 189, 72);

  public static List<AccountMeta> updatePerpMarketCurveUpdateIntensityKeys(final AccountMeta invokedDriftProgramMeta                                                                           ,
                                                                           final PublicKey adminKey,
                                                                           final PublicKey stateKey,
                                                                           final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketCurveUpdateIntensity(final AccountMeta invokedDriftProgramMeta,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey perpMarketKey,
                                                                 final int curveUpdateIntensity) {
    final var keys = updatePerpMarketCurveUpdateIntensityKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketCurveUpdateIntensity(invokedDriftProgramMeta, keys, curveUpdateIntensity);
  }

  public static Instruction updatePerpMarketCurveUpdateIntensity(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final List<AccountMeta> keys,
                                                                 final int curveUpdateIntensity) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_CURVE_UPDATE_INTENSITY_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) curveUpdateIntensity;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketCurveUpdateIntensityIxData(Discriminator discriminator, int curveUpdateIntensity) implements Borsh {  

    public static UpdatePerpMarketCurveUpdateIntensityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketCurveUpdateIntensityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var curveUpdateIntensity = _data[i] & 0xFF;
      return new UpdatePerpMarketCurveUpdateIntensityIxData(discriminator, curveUpdateIntensity);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) curveUpdateIntensity;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_REFERENCE_PRICE_OFFSET_DEADBAND_PCT_DISCRIMINATOR = toDiscriminator(214, 73, 166, 11, 218, 76, 110, 163);

  public static List<AccountMeta> updatePerpMarketReferencePriceOffsetDeadbandPctKeys(final AccountMeta invokedDriftProgramMeta                                                                                      ,
                                                                                      final PublicKey adminKey,
                                                                                      final PublicKey stateKey,
                                                                                      final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketReferencePriceOffsetDeadbandPct(final AccountMeta invokedDriftProgramMeta,
                                                                            final PublicKey adminKey,
                                                                            final PublicKey stateKey,
                                                                            final PublicKey perpMarketKey,
                                                                            final int referencePriceOffsetDeadbandPct) {
    final var keys = updatePerpMarketReferencePriceOffsetDeadbandPctKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketReferencePriceOffsetDeadbandPct(invokedDriftProgramMeta, keys, referencePriceOffsetDeadbandPct);
  }

  public static Instruction updatePerpMarketReferencePriceOffsetDeadbandPct(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final List<AccountMeta> keys,
                                                                            final int referencePriceOffsetDeadbandPct) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_REFERENCE_PRICE_OFFSET_DEADBAND_PCT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) referencePriceOffsetDeadbandPct;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketReferencePriceOffsetDeadbandPctIxData(Discriminator discriminator, int referencePriceOffsetDeadbandPct) implements Borsh {  

    public static UpdatePerpMarketReferencePriceOffsetDeadbandPctIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketReferencePriceOffsetDeadbandPctIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var referencePriceOffsetDeadbandPct = _data[i] & 0xFF;
      return new UpdatePerpMarketReferencePriceOffsetDeadbandPctIxData(discriminator, referencePriceOffsetDeadbandPct);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) referencePriceOffsetDeadbandPct;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_LP_COOLDOWN_TIME_DISCRIMINATOR = toDiscriminator(198, 133, 88, 41, 241, 119, 61, 14);

  public static List<AccountMeta> updateLpCooldownTimeKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateLpCooldownTime(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final long lpCooldownTime) {
    final var keys = updateLpCooldownTimeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateLpCooldownTime(invokedDriftProgramMeta, keys, lpCooldownTime);
  }

  public static Instruction updateLpCooldownTime(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final long lpCooldownTime) {
    final byte[] _data = new byte[16];
    int i = UPDATE_LP_COOLDOWN_TIME_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, lpCooldownTime);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateLpCooldownTimeIxData(Discriminator discriminator, long lpCooldownTime) implements Borsh {  

    public static UpdateLpCooldownTimeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdateLpCooldownTimeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lpCooldownTime = getInt64LE(_data, i);
      return new UpdateLpCooldownTimeIxData(discriminator, lpCooldownTime);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, lpCooldownTime);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_FEE_STRUCTURE_DISCRIMINATOR = toDiscriminator(23, 178, 111, 203, 73, 22, 140, 75);

  public static List<AccountMeta> updatePerpFeeStructureKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updatePerpFeeStructure(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final FeeStructure feeStructure) {
    final var keys = updatePerpFeeStructureKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updatePerpFeeStructure(invokedDriftProgramMeta, keys, feeStructure);
  }

  public static Instruction updatePerpFeeStructure(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final FeeStructure feeStructure) {
    final byte[] _data = new byte[8 + Borsh.len(feeStructure)];
    int i = UPDATE_PERP_FEE_STRUCTURE_DISCRIMINATOR.write(_data, 0);
    Borsh.write(feeStructure, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpFeeStructureIxData(Discriminator discriminator, FeeStructure feeStructure) implements Borsh {  

    public static UpdatePerpFeeStructureIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 368;

    public static UpdatePerpFeeStructureIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feeStructure = FeeStructure.read(_data, i);
      return new UpdatePerpFeeStructureIxData(discriminator, feeStructure);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(feeStructure, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_FEE_STRUCTURE_DISCRIMINATOR = toDiscriminator(97, 216, 105, 131, 113, 246, 142, 141);

  public static List<AccountMeta> updateSpotFeeStructureKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateSpotFeeStructure(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final FeeStructure feeStructure) {
    final var keys = updateSpotFeeStructureKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateSpotFeeStructure(invokedDriftProgramMeta, keys, feeStructure);
  }

  public static Instruction updateSpotFeeStructure(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final FeeStructure feeStructure) {
    final byte[] _data = new byte[8 + Borsh.len(feeStructure)];
    int i = UPDATE_SPOT_FEE_STRUCTURE_DISCRIMINATOR.write(_data, 0);
    Borsh.write(feeStructure, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotFeeStructureIxData(Discriminator discriminator, FeeStructure feeStructure) implements Borsh {  

    public static UpdateSpotFeeStructureIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 368;

    public static UpdateSpotFeeStructureIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feeStructure = FeeStructure.read(_data, i);
      return new UpdateSpotFeeStructureIxData(discriminator, feeStructure);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(feeStructure, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_INITIAL_PCT_TO_LIQUIDATE_DISCRIMINATOR = toDiscriminator(210, 133, 225, 128, 194, 50, 13, 109);

  public static List<AccountMeta> updateInitialPctToLiquidateKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateInitialPctToLiquidate(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey adminKey,
                                                        final PublicKey stateKey,
                                                        final int initialPctToLiquidate) {
    final var keys = updateInitialPctToLiquidateKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateInitialPctToLiquidate(invokedDriftProgramMeta, keys, initialPctToLiquidate);
  }

  public static Instruction updateInitialPctToLiquidate(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final int initialPctToLiquidate) {
    final byte[] _data = new byte[10];
    int i = UPDATE_INITIAL_PCT_TO_LIQUIDATE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, initialPctToLiquidate);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateInitialPctToLiquidateIxData(Discriminator discriminator, int initialPctToLiquidate) implements Borsh {  

    public static UpdateInitialPctToLiquidateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateInitialPctToLiquidateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var initialPctToLiquidate = getInt16LE(_data, i);
      return new UpdateInitialPctToLiquidateIxData(discriminator, initialPctToLiquidate);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, initialPctToLiquidate);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_LIQUIDATION_DURATION_DISCRIMINATOR = toDiscriminator(28, 154, 20, 249, 102, 192, 73, 71);

  public static List<AccountMeta> updateLiquidationDurationKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateLiquidationDuration(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey stateKey,
                                                      final int liquidationDuration) {
    final var keys = updateLiquidationDurationKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateLiquidationDuration(invokedDriftProgramMeta, keys, liquidationDuration);
  }

  public static Instruction updateLiquidationDuration(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int liquidationDuration) {
    final byte[] _data = new byte[9];
    int i = UPDATE_LIQUIDATION_DURATION_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) liquidationDuration;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateLiquidationDurationIxData(Discriminator discriminator, int liquidationDuration) implements Borsh {  

    public static UpdateLiquidationDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateLiquidationDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidationDuration = _data[i] & 0xFF;
      return new UpdateLiquidationDurationIxData(discriminator, liquidationDuration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) liquidationDuration;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_LIQUIDATION_MARGIN_BUFFER_RATIO_DISCRIMINATOR = toDiscriminator(132, 224, 243, 160, 154, 82, 97, 215);

  public static List<AccountMeta> updateLiquidationMarginBufferRatioKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey adminKey,
                                                                         final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateLiquidationMarginBufferRatio(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey adminKey,
                                                               final PublicKey stateKey,
                                                               final int liquidationMarginBufferRatio) {
    final var keys = updateLiquidationMarginBufferRatioKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateLiquidationMarginBufferRatio(invokedDriftProgramMeta, keys, liquidationMarginBufferRatio);
  }

  public static Instruction updateLiquidationMarginBufferRatio(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys,
                                                               final int liquidationMarginBufferRatio) {
    final byte[] _data = new byte[12];
    int i = UPDATE_LIQUIDATION_MARGIN_BUFFER_RATIO_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, liquidationMarginBufferRatio);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateLiquidationMarginBufferRatioIxData(Discriminator discriminator, int liquidationMarginBufferRatio) implements Borsh {  

    public static UpdateLiquidationMarginBufferRatioIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static UpdateLiquidationMarginBufferRatioIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var liquidationMarginBufferRatio = getInt32LE(_data, i);
      return new UpdateLiquidationMarginBufferRatioIxData(discriminator, liquidationMarginBufferRatio);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, liquidationMarginBufferRatio);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_ORACLE_GUARD_RAILS_DISCRIMINATOR = toDiscriminator(131, 112, 10, 59, 32, 54, 40, 164);

  public static List<AccountMeta> updateOracleGuardRailsKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateOracleGuardRails(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final OracleGuardRails oracleGuardRails) {
    final var keys = updateOracleGuardRailsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateOracleGuardRails(invokedDriftProgramMeta, keys, oracleGuardRails);
  }

  public static Instruction updateOracleGuardRails(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final OracleGuardRails oracleGuardRails) {
    final byte[] _data = new byte[8 + Borsh.len(oracleGuardRails)];
    int i = UPDATE_ORACLE_GUARD_RAILS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(oracleGuardRails, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateOracleGuardRailsIxData(Discriminator discriminator, OracleGuardRails oracleGuardRails) implements Borsh {  

    public static UpdateOracleGuardRailsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 56;

    public static UpdateOracleGuardRailsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var oracleGuardRails = OracleGuardRails.read(_data, i);
      return new UpdateOracleGuardRailsIxData(discriminator, oracleGuardRails);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(oracleGuardRails, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_STATE_SETTLEMENT_DURATION_DISCRIMINATOR = toDiscriminator(97, 68, 199, 235, 131, 80, 61, 173);

  public static List<AccountMeta> updateStateSettlementDurationKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateStateSettlementDuration(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final int settlementDuration) {
    final var keys = updateStateSettlementDurationKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateStateSettlementDuration(invokedDriftProgramMeta, keys, settlementDuration);
  }

  public static Instruction updateStateSettlementDuration(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int settlementDuration) {
    final byte[] _data = new byte[10];
    int i = UPDATE_STATE_SETTLEMENT_DURATION_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, settlementDuration);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateStateSettlementDurationIxData(Discriminator discriminator, int settlementDuration) implements Borsh {  

    public static UpdateStateSettlementDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateStateSettlementDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var settlementDuration = getInt16LE(_data, i);
      return new UpdateStateSettlementDurationIxData(discriminator, settlementDuration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, settlementDuration);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_STATE_MAX_NUMBER_OF_SUB_ACCOUNTS_DISCRIMINATOR = toDiscriminator(155, 123, 214, 2, 221, 166, 204, 85);

  public static List<AccountMeta> updateStateMaxNumberOfSubAccountsKeys(final AccountMeta invokedDriftProgramMeta                                                                        ,
                                                                        final PublicKey adminKey,
                                                                        final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateStateMaxNumberOfSubAccounts(final AccountMeta invokedDriftProgramMeta,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final int maxNumberOfSubAccounts) {
    final var keys = updateStateMaxNumberOfSubAccountsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateStateMaxNumberOfSubAccounts(invokedDriftProgramMeta, keys, maxNumberOfSubAccounts);
  }

  public static Instruction updateStateMaxNumberOfSubAccounts(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final List<AccountMeta> keys,
                                                              final int maxNumberOfSubAccounts) {
    final byte[] _data = new byte[10];
    int i = UPDATE_STATE_MAX_NUMBER_OF_SUB_ACCOUNTS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, maxNumberOfSubAccounts);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateStateMaxNumberOfSubAccountsIxData(Discriminator discriminator, int maxNumberOfSubAccounts) implements Borsh {  

    public static UpdateStateMaxNumberOfSubAccountsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateStateMaxNumberOfSubAccountsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxNumberOfSubAccounts = getInt16LE(_data, i);
      return new UpdateStateMaxNumberOfSubAccountsIxData(discriminator, maxNumberOfSubAccounts);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, maxNumberOfSubAccounts);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_STATE_MAX_INITIALIZE_USER_FEE_DISCRIMINATOR = toDiscriminator(237, 225, 25, 237, 193, 45, 77, 97);

  public static List<AccountMeta> updateStateMaxInitializeUserFeeKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateStateMaxInitializeUserFee(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final int maxInitializeUserFee) {
    final var keys = updateStateMaxInitializeUserFeeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateStateMaxInitializeUserFee(invokedDriftProgramMeta, keys, maxInitializeUserFee);
  }

  public static Instruction updateStateMaxInitializeUserFee(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final int maxInitializeUserFee) {
    final byte[] _data = new byte[10];
    int i = UPDATE_STATE_MAX_INITIALIZE_USER_FEE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, maxInitializeUserFee);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateStateMaxInitializeUserFeeIxData(Discriminator discriminator, int maxInitializeUserFee) implements Borsh {  

    public static UpdateStateMaxInitializeUserFeeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateStateMaxInitializeUserFeeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxInitializeUserFee = getInt16LE(_data, i);
      return new UpdateStateMaxInitializeUserFeeIxData(discriminator, maxInitializeUserFee);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, maxInitializeUserFee);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_ORACLE_DISCRIMINATOR = toDiscriminator(182, 113, 111, 160, 67, 174, 89, 191);

  public static List<AccountMeta> updatePerpMarketOracleKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey perpMarketKey,
                                                             final PublicKey oracleKey,
                                                             final PublicKey oldOracleKey,
                                                             final PublicKey ammCacheKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey),
      createRead(oracleKey),
      createRead(oldOracleKey),
      createWrite(ammCacheKey)
    );
  }

  public static Instruction updatePerpMarketOracle(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey perpMarketKey,
                                                   final PublicKey oracleKey,
                                                   final PublicKey oldOracleKey,
                                                   final PublicKey ammCacheKey,
                                                   final PublicKey oracle,
                                                   final OracleSource oracleSource,
                                                   final boolean skipInvariantCheck) {
    final var keys = updatePerpMarketOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey,
      oracleKey,
      oldOracleKey,
      ammCacheKey
    );
    return updatePerpMarketOracle(
      invokedDriftProgramMeta,
      keys,
      oracle,
      oracleSource,
      skipInvariantCheck
    );
  }

  public static Instruction updatePerpMarketOracle(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys,
                                                   final PublicKey oracle,
                                                   final OracleSource oracleSource,
                                                   final boolean skipInvariantCheck) {
    final byte[] _data = new byte[41 + Borsh.len(oracleSource)];
    int i = UPDATE_PERP_MARKET_ORACLE_DISCRIMINATOR.write(_data, 0);
    oracle.write(_data, i);
    i += 32;
    i += Borsh.write(oracleSource, _data, i);
    _data[i] = (byte) (skipInvariantCheck ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketOracleIxData(Discriminator discriminator,
                                             PublicKey oracle,
                                             OracleSource oracleSource,
                                             boolean skipInvariantCheck) implements Borsh {  

    public static UpdatePerpMarketOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 42;

    public static UpdatePerpMarketOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var oracle = readPubKey(_data, i);
      i += 32;
      final var oracleSource = OracleSource.read(_data, i);
      i += Borsh.len(oracleSource);
      final var skipInvariantCheck = _data[i] == 1;
      return new UpdatePerpMarketOracleIxData(discriminator, oracle, oracleSource, skipInvariantCheck);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      oracle.write(_data, i);
      i += 32;
      i += Borsh.write(oracleSource, _data, i);
      _data[i] = (byte) (skipInvariantCheck ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_BASE_SPREAD_DISCRIMINATOR = toDiscriminator(71, 95, 84, 168, 9, 157, 198, 65);

  public static List<AccountMeta> updatePerpMarketBaseSpreadKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketBaseSpread(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey adminKey,
                                                       final PublicKey stateKey,
                                                       final PublicKey perpMarketKey,
                                                       final int baseSpread) {
    final var keys = updatePerpMarketBaseSpreadKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketBaseSpread(invokedDriftProgramMeta, keys, baseSpread);
  }

  public static Instruction updatePerpMarketBaseSpread(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final int baseSpread) {
    final byte[] _data = new byte[12];
    int i = UPDATE_PERP_MARKET_BASE_SPREAD_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, baseSpread);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketBaseSpreadIxData(Discriminator discriminator, int baseSpread) implements Borsh {  

    public static UpdatePerpMarketBaseSpreadIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static UpdatePerpMarketBaseSpreadIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var baseSpread = getInt32LE(_data, i);
      return new UpdatePerpMarketBaseSpreadIxData(discriminator, baseSpread);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, baseSpread);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_AMM_JIT_INTENSITY_DISCRIMINATOR = toDiscriminator(181, 191, 53, 109, 166, 249, 55, 142);

  public static List<AccountMeta> updateAmmJitIntensityKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updateAmmJitIntensity(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey adminKey,
                                                  final PublicKey stateKey,
                                                  final PublicKey perpMarketKey,
                                                  final int ammJitIntensity) {
    final var keys = updateAmmJitIntensityKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updateAmmJitIntensity(invokedDriftProgramMeta, keys, ammJitIntensity);
  }

  public static Instruction updateAmmJitIntensity(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int ammJitIntensity) {
    final byte[] _data = new byte[9];
    int i = UPDATE_AMM_JIT_INTENSITY_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) ammJitIntensity;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateAmmJitIntensityIxData(Discriminator discriminator, int ammJitIntensity) implements Borsh {  

    public static UpdateAmmJitIntensityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateAmmJitIntensityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ammJitIntensity = _data[i] & 0xFF;
      return new UpdateAmmJitIntensityIxData(discriminator, ammJitIntensity);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) ammJitIntensity;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MAX_SPREAD_DISCRIMINATOR = toDiscriminator(80, 252, 122, 62, 40, 218, 91, 100);

  public static List<AccountMeta> updatePerpMarketMaxSpreadKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMaxSpread(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey perpMarketKey,
                                                      final int maxSpread) {
    final var keys = updatePerpMarketMaxSpreadKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMaxSpread(invokedDriftProgramMeta, keys, maxSpread);
  }

  public static Instruction updatePerpMarketMaxSpread(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int maxSpread) {
    final byte[] _data = new byte[12];
    int i = UPDATE_PERP_MARKET_MAX_SPREAD_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, maxSpread);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMaxSpreadIxData(Discriminator discriminator, int maxSpread) implements Borsh {  

    public static UpdatePerpMarketMaxSpreadIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static UpdatePerpMarketMaxSpreadIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxSpread = getInt32LE(_data, i);
      return new UpdatePerpMarketMaxSpreadIxData(discriminator, maxSpread);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, maxSpread);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_STEP_SIZE_AND_TICK_SIZE_DISCRIMINATOR = toDiscriminator(231, 255, 97, 25, 146, 139, 174, 4);

  public static List<AccountMeta> updatePerpMarketStepSizeAndTickSizeKeys(final AccountMeta invokedDriftProgramMeta                                                                          ,
                                                                          final PublicKey adminKey,
                                                                          final PublicKey stateKey,
                                                                          final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketStepSizeAndTickSize(final AccountMeta invokedDriftProgramMeta,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey perpMarketKey,
                                                                final long stepSize,
                                                                final long tickSize) {
    final var keys = updatePerpMarketStepSizeAndTickSizeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketStepSizeAndTickSize(invokedDriftProgramMeta, keys, stepSize, tickSize);
  }

  public static Instruction updatePerpMarketStepSizeAndTickSize(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final List<AccountMeta> keys,
                                                                final long stepSize,
                                                                final long tickSize) {
    final byte[] _data = new byte[24];
    int i = UPDATE_PERP_MARKET_STEP_SIZE_AND_TICK_SIZE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, stepSize);
    i += 8;
    putInt64LE(_data, i, tickSize);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketStepSizeAndTickSizeIxData(Discriminator discriminator, long stepSize, long tickSize) implements Borsh {  

    public static UpdatePerpMarketStepSizeAndTickSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdatePerpMarketStepSizeAndTickSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var stepSize = getInt64LE(_data, i);
      i += 8;
      final var tickSize = getInt64LE(_data, i);
      return new UpdatePerpMarketStepSizeAndTickSizeIxData(discriminator, stepSize, tickSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, stepSize);
      i += 8;
      putInt64LE(_data, i, tickSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_NAME_DISCRIMINATOR = toDiscriminator(211, 31, 21, 210, 64, 108, 66, 201);

  public static List<AccountMeta> updatePerpMarketNameKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketName(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey perpMarketKey,
                                                 final byte[] name) {
    final var keys = updatePerpMarketNameKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketName(invokedDriftProgramMeta, keys, name);
  }

  public static Instruction updatePerpMarketName(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final byte[] name) {
    final byte[] _data = new byte[8 + Borsh.lenArray(name)];
    int i = UPDATE_PERP_MARKET_NAME_DISCRIMINATOR.write(_data, 0);
    Borsh.writeArrayChecked(name, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketNameIxData(Discriminator discriminator, byte[] name) implements Borsh {  

    public static UpdatePerpMarketNameIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int NAME_LEN = 32;

    public static UpdatePerpMarketNameIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var name = new byte[32];
      Borsh.readArray(name, _data, i);
      return new UpdatePerpMarketNameIxData(discriminator, name);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(name, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MIN_ORDER_SIZE_DISCRIMINATOR = toDiscriminator(226, 74, 5, 89, 108, 223, 46, 141);

  public static List<AccountMeta> updatePerpMarketMinOrderSizeKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMinOrderSize(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey perpMarketKey,
                                                         final long orderSize) {
    final var keys = updatePerpMarketMinOrderSizeKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMinOrderSize(invokedDriftProgramMeta, keys, orderSize);
  }

  public static Instruction updatePerpMarketMinOrderSize(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final long orderSize) {
    final byte[] _data = new byte[16];
    int i = UPDATE_PERP_MARKET_MIN_ORDER_SIZE_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, orderSize);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMinOrderSizeIxData(Discriminator discriminator, long orderSize) implements Borsh {  

    public static UpdatePerpMarketMinOrderSizeIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static UpdatePerpMarketMinOrderSizeIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var orderSize = getInt64LE(_data, i);
      return new UpdatePerpMarketMinOrderSizeIxData(discriminator, orderSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, orderSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MAX_SLIPPAGE_RATIO_DISCRIMINATOR = toDiscriminator(235, 37, 40, 196, 70, 146, 54, 201);

  public static List<AccountMeta> updatePerpMarketMaxSlippageRatioKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMaxSlippageRatio(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey perpMarketKey,
                                                             final int maxSlippageRatio) {
    final var keys = updatePerpMarketMaxSlippageRatioKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMaxSlippageRatio(invokedDriftProgramMeta, keys, maxSlippageRatio);
  }

  public static Instruction updatePerpMarketMaxSlippageRatio(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final int maxSlippageRatio) {
    final byte[] _data = new byte[10];
    int i = UPDATE_PERP_MARKET_MAX_SLIPPAGE_RATIO_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, maxSlippageRatio);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMaxSlippageRatioIxData(Discriminator discriminator, int maxSlippageRatio) implements Borsh {  

    public static UpdatePerpMarketMaxSlippageRatioIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdatePerpMarketMaxSlippageRatioIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxSlippageRatio = getInt16LE(_data, i);
      return new UpdatePerpMarketMaxSlippageRatioIxData(discriminator, maxSlippageRatio);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, maxSlippageRatio);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MAX_FILL_RESERVE_FRACTION_DISCRIMINATOR = toDiscriminator(19, 172, 114, 154, 42, 135, 161, 133);

  public static List<AccountMeta> updatePerpMarketMaxFillReserveFractionKeys(final AccountMeta invokedDriftProgramMeta                                                                             ,
                                                                             final PublicKey adminKey,
                                                                             final PublicKey stateKey,
                                                                             final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMaxFillReserveFraction(final AccountMeta invokedDriftProgramMeta,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey perpMarketKey,
                                                                   final int maxFillReserveFraction) {
    final var keys = updatePerpMarketMaxFillReserveFractionKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMaxFillReserveFraction(invokedDriftProgramMeta, keys, maxFillReserveFraction);
  }

  public static Instruction updatePerpMarketMaxFillReserveFraction(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final List<AccountMeta> keys,
                                                                   final int maxFillReserveFraction) {
    final byte[] _data = new byte[10];
    int i = UPDATE_PERP_MARKET_MAX_FILL_RESERVE_FRACTION_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, maxFillReserveFraction);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMaxFillReserveFractionIxData(Discriminator discriminator, int maxFillReserveFraction) implements Borsh {  

    public static UpdatePerpMarketMaxFillReserveFractionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdatePerpMarketMaxFillReserveFractionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxFillReserveFraction = getInt16LE(_data, i);
      return new UpdatePerpMarketMaxFillReserveFractionIxData(discriminator, maxFillReserveFraction);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, maxFillReserveFraction);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_MAX_OPEN_INTEREST_DISCRIMINATOR = toDiscriminator(194, 79, 149, 224, 246, 102, 186, 140);

  public static List<AccountMeta> updatePerpMarketMaxOpenInterestKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey stateKey,
                                                                      final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketMaxOpenInterest(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final PublicKey perpMarketKey,
                                                            final BigInteger maxOpenInterest) {
    final var keys = updatePerpMarketMaxOpenInterestKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketMaxOpenInterest(invokedDriftProgramMeta, keys, maxOpenInterest);
  }

  public static Instruction updatePerpMarketMaxOpenInterest(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final BigInteger maxOpenInterest) {
    final byte[] _data = new byte[24];
    int i = UPDATE_PERP_MARKET_MAX_OPEN_INTEREST_DISCRIMINATOR.write(_data, 0);
    putInt128LE(_data, i, maxOpenInterest);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketMaxOpenInterestIxData(Discriminator discriminator, BigInteger maxOpenInterest) implements Borsh {  

    public static UpdatePerpMarketMaxOpenInterestIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 24;

    public static UpdatePerpMarketMaxOpenInterestIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxOpenInterest = getInt128LE(_data, i);
      return new UpdatePerpMarketMaxOpenInterestIxData(discriminator, maxOpenInterest);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt128LE(_data, i, maxOpenInterest);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_NUMBER_OF_USERS_DISCRIMINATOR = toDiscriminator(35, 62, 144, 177, 180, 62, 215, 196);

  public static List<AccountMeta> updatePerpMarketNumberOfUsersKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketNumberOfUsers(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey perpMarketKey,
                                                          final OptionalInt numberOfUsers,
                                                          final OptionalInt numberOfUsersWithBase) {
    final var keys = updatePerpMarketNumberOfUsersKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketNumberOfUsers(invokedDriftProgramMeta, keys, numberOfUsers, numberOfUsersWithBase);
  }

  public static Instruction updatePerpMarketNumberOfUsers(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final OptionalInt numberOfUsers,
                                                          final OptionalInt numberOfUsersWithBase) {
    final byte[] _data = new byte[
    8
    + (numberOfUsers == null || numberOfUsers.isEmpty() ? 1 : 5)
    + (numberOfUsersWithBase == null || numberOfUsersWithBase.isEmpty() ? 1 : 5)
    ];
    int i = UPDATE_PERP_MARKET_NUMBER_OF_USERS_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(numberOfUsers, _data, i);
    Borsh.writeOptional(numberOfUsersWithBase, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketNumberOfUsersIxData(Discriminator discriminator, OptionalInt numberOfUsers, OptionalInt numberOfUsersWithBase) implements Borsh {  

    public static UpdatePerpMarketNumberOfUsersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdatePerpMarketNumberOfUsersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt numberOfUsers;
      if (_data[i] == 0) {
        numberOfUsers = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        numberOfUsers = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final OptionalInt numberOfUsersWithBase;
      if (_data[i] == 0) {
        numberOfUsersWithBase = OptionalInt.empty();
      } else {
        ++i;
      ;
        numberOfUsersWithBase = OptionalInt.of(getInt32LE(_data, i));
      }
      return new UpdatePerpMarketNumberOfUsersIxData(discriminator, numberOfUsers, numberOfUsersWithBase);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(numberOfUsers, _data, i);
      i += Borsh.writeOptional(numberOfUsersWithBase, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (numberOfUsers == null || numberOfUsers.isEmpty() ? 1 : (1 + 4)) + (numberOfUsersWithBase == null || numberOfUsersWithBase.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_FEE_ADJUSTMENT_DISCRIMINATOR = toDiscriminator(194, 174, 87, 102, 43, 148, 32, 112);

  public static List<AccountMeta> updatePerpMarketFeeAdjustmentKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketFeeAdjustment(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey perpMarketKey,
                                                          final int feeAdjustment) {
    final var keys = updatePerpMarketFeeAdjustmentKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketFeeAdjustment(invokedDriftProgramMeta, keys, feeAdjustment);
  }

  public static Instruction updatePerpMarketFeeAdjustment(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int feeAdjustment) {
    final byte[] _data = new byte[10];
    int i = UPDATE_PERP_MARKET_FEE_ADJUSTMENT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, feeAdjustment);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketFeeAdjustmentIxData(Discriminator discriminator, int feeAdjustment) implements Borsh {  

    public static UpdatePerpMarketFeeAdjustmentIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdatePerpMarketFeeAdjustmentIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feeAdjustment = getInt16LE(_data, i);
      return new UpdatePerpMarketFeeAdjustmentIxData(discriminator, feeAdjustment);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, feeAdjustment);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_FEE_ADJUSTMENT_DISCRIMINATOR = toDiscriminator(148, 182, 3, 126, 157, 114, 220, 99);

  public static List<AccountMeta> updateSpotMarketFeeAdjustmentKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketFeeAdjustment(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final PublicKey spotMarketKey,
                                                          final int feeAdjustment) {
    final var keys = updateSpotMarketFeeAdjustmentKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketFeeAdjustment(invokedDriftProgramMeta, keys, feeAdjustment);
  }

  public static Instruction updateSpotMarketFeeAdjustment(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int feeAdjustment) {
    final byte[] _data = new byte[10];
    int i = UPDATE_SPOT_MARKET_FEE_ADJUSTMENT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, feeAdjustment);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketFeeAdjustmentIxData(Discriminator discriminator, int feeAdjustment) implements Borsh {  

    public static UpdateSpotMarketFeeAdjustmentIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static UpdateSpotMarketFeeAdjustmentIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feeAdjustment = getInt16LE(_data, i);
      return new UpdateSpotMarketFeeAdjustmentIxData(discriminator, feeAdjustment);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, feeAdjustment);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_FUEL_DISCRIMINATOR = toDiscriminator(252, 141, 110, 101, 27, 99, 182, 21);

  public static List<AccountMeta> updatePerpMarketFuelKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketFuel(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey perpMarketKey,
                                                 final OptionalInt fuelBoostTaker,
                                                 final OptionalInt fuelBoostMaker,
                                                 final OptionalInt fuelBoostPosition) {
    final var keys = updatePerpMarketFuelKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketFuel(
      invokedDriftProgramMeta,
      keys,
      fuelBoostTaker,
      fuelBoostMaker,
      fuelBoostPosition
    );
  }

  public static Instruction updatePerpMarketFuel(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final OptionalInt fuelBoostTaker,
                                                 final OptionalInt fuelBoostMaker,
                                                 final OptionalInt fuelBoostPosition) {
    final byte[] _data = new byte[
    8
    + (fuelBoostTaker == null || fuelBoostTaker.isEmpty() ? 1 : 2)
    + (fuelBoostMaker == null || fuelBoostMaker.isEmpty() ? 1 : 2)
    + (fuelBoostPosition == null || fuelBoostPosition.isEmpty() ? 1 : 2)
    ];
    int i = UPDATE_PERP_MARKET_FUEL_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptionalbyte(fuelBoostTaker, _data, i);
    i += Borsh.writeOptionalbyte(fuelBoostMaker, _data, i);
    Borsh.writeOptionalbyte(fuelBoostPosition, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketFuelIxData(Discriminator discriminator,
                                           OptionalInt fuelBoostTaker,
                                           OptionalInt fuelBoostMaker,
                                           OptionalInt fuelBoostPosition) implements Borsh {  

    public static UpdatePerpMarketFuelIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdatePerpMarketFuelIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt fuelBoostTaker;
      if (_data[i] == 0) {
        fuelBoostTaker = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostTaker = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt fuelBoostMaker;
      if (_data[i] == 0) {
        fuelBoostMaker = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostMaker = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt fuelBoostPosition;
      if (_data[i] == 0) {
        fuelBoostPosition = OptionalInt.empty();
      } else {
        ++i;
      ;
        fuelBoostPosition = OptionalInt.of(_data[i] & 0xFF);
      }
      return new UpdatePerpMarketFuelIxData(discriminator, fuelBoostTaker, fuelBoostMaker, fuelBoostPosition);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptionalbyte(fuelBoostTaker, _data, i);
      i += Borsh.writeOptionalbyte(fuelBoostMaker, _data, i);
      i += Borsh.writeOptionalbyte(fuelBoostPosition, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (fuelBoostTaker == null || fuelBoostTaker.isEmpty() ? 1 : (1 + 1)) + (fuelBoostMaker == null || fuelBoostMaker.isEmpty() ? 1 : (1 + 1)) + (fuelBoostPosition == null || fuelBoostPosition.isEmpty() ? 1 : (1 + 1));
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_PROTECTED_MAKER_PARAMS_DISCRIMINATOR = toDiscriminator(249, 213, 115, 34, 253, 239, 75, 173);

  public static List<AccountMeta> updatePerpMarketProtectedMakerParamsKeys(final AccountMeta invokedDriftProgramMeta                                                                           ,
                                                                           final PublicKey adminKey,
                                                                           final PublicKey stateKey,
                                                                           final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketProtectedMakerParams(final AccountMeta invokedDriftProgramMeta,
                                                                 final PublicKey adminKey,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey perpMarketKey,
                                                                 final OptionalInt protectedMakerLimitPriceDivisor,
                                                                 final OptionalInt protectedMakerDynamicDivisor) {
    final var keys = updatePerpMarketProtectedMakerParamsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketProtectedMakerParams(invokedDriftProgramMeta, keys, protectedMakerLimitPriceDivisor, protectedMakerDynamicDivisor);
  }

  public static Instruction updatePerpMarketProtectedMakerParams(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final List<AccountMeta> keys,
                                                                 final OptionalInt protectedMakerLimitPriceDivisor,
                                                                 final OptionalInt protectedMakerDynamicDivisor) {
    final byte[] _data = new byte[
    8
    + (protectedMakerLimitPriceDivisor == null || protectedMakerLimitPriceDivisor.isEmpty() ? 1 : 2)
    + (protectedMakerDynamicDivisor == null || protectedMakerDynamicDivisor.isEmpty() ? 1 : 2)
    ];
    int i = UPDATE_PERP_MARKET_PROTECTED_MAKER_PARAMS_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptionalbyte(protectedMakerLimitPriceDivisor, _data, i);
    Borsh.writeOptionalbyte(protectedMakerDynamicDivisor, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketProtectedMakerParamsIxData(Discriminator discriminator, OptionalInt protectedMakerLimitPriceDivisor, OptionalInt protectedMakerDynamicDivisor) implements Borsh {  

    public static UpdatePerpMarketProtectedMakerParamsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdatePerpMarketProtectedMakerParamsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt protectedMakerLimitPriceDivisor;
      if (_data[i] == 0) {
        protectedMakerLimitPriceDivisor = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        protectedMakerLimitPriceDivisor = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt protectedMakerDynamicDivisor;
      if (_data[i] == 0) {
        protectedMakerDynamicDivisor = OptionalInt.empty();
      } else {
        ++i;
      ;
        protectedMakerDynamicDivisor = OptionalInt.of(_data[i] & 0xFF);
      }
      return new UpdatePerpMarketProtectedMakerParamsIxData(discriminator, protectedMakerLimitPriceDivisor, protectedMakerDynamicDivisor);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptionalbyte(protectedMakerLimitPriceDivisor, _data, i);
      i += Borsh.writeOptionalbyte(protectedMakerDynamicDivisor, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (protectedMakerLimitPriceDivisor == null || protectedMakerLimitPriceDivisor.isEmpty() ? 1 : (1 + 1)) + (protectedMakerDynamicDivisor == null || protectedMakerDynamicDivisor.isEmpty() ? 1 : (1 + 1));
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_ORACLE_LOW_RISK_SLOT_DELAY_OVERRIDE_DISCRIMINATOR = toDiscriminator(124, 108, 147, 229, 109, 117, 123, 3);

  public static List<AccountMeta> updatePerpMarketOracleLowRiskSlotDelayOverrideKeys(final AccountMeta invokedDriftProgramMeta                                                                                     ,
                                                                                     final PublicKey adminKey,
                                                                                     final PublicKey stateKey,
                                                                                     final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketOracleLowRiskSlotDelayOverride(final AccountMeta invokedDriftProgramMeta,
                                                                           final PublicKey adminKey,
                                                                           final PublicKey stateKey,
                                                                           final PublicKey perpMarketKey,
                                                                           final int oracleLowRiskSlotDelayOverride) {
    final var keys = updatePerpMarketOracleLowRiskSlotDelayOverrideKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketOracleLowRiskSlotDelayOverride(invokedDriftProgramMeta, keys, oracleLowRiskSlotDelayOverride);
  }

  public static Instruction updatePerpMarketOracleLowRiskSlotDelayOverride(final AccountMeta invokedDriftProgramMeta                                                                           ,
                                                                           final List<AccountMeta> keys,
                                                                           final int oracleLowRiskSlotDelayOverride) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_ORACLE_LOW_RISK_SLOT_DELAY_OVERRIDE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) oracleLowRiskSlotDelayOverride;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketOracleLowRiskSlotDelayOverrideIxData(Discriminator discriminator, int oracleLowRiskSlotDelayOverride) implements Borsh {  

    public static UpdatePerpMarketOracleLowRiskSlotDelayOverrideIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketOracleLowRiskSlotDelayOverrideIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var oracleLowRiskSlotDelayOverride = _data[i];
      return new UpdatePerpMarketOracleLowRiskSlotDelayOverrideIxData(discriminator, oracleLowRiskSlotDelayOverride);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) oracleLowRiskSlotDelayOverride;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_AMM_SPREAD_ADJUSTMENT_DISCRIMINATOR = toDiscriminator(155, 195, 149, 43, 220, 82, 173, 205);

  public static List<AccountMeta> updatePerpMarketAmmSpreadAdjustmentKeys(final AccountMeta invokedDriftProgramMeta                                                                          ,
                                                                          final PublicKey adminKey,
                                                                          final PublicKey stateKey,
                                                                          final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketAmmSpreadAdjustment(final AccountMeta invokedDriftProgramMeta,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey perpMarketKey,
                                                                final int ammSpreadAdjustment,
                                                                final int ammInventorySpreadAdjustment,
                                                                final int referencePriceOffset) {
    final var keys = updatePerpMarketAmmSpreadAdjustmentKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketAmmSpreadAdjustment(
      invokedDriftProgramMeta,
      keys,
      ammSpreadAdjustment,
      ammInventorySpreadAdjustment,
      referencePriceOffset
    );
  }

  public static Instruction updatePerpMarketAmmSpreadAdjustment(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final List<AccountMeta> keys,
                                                                final int ammSpreadAdjustment,
                                                                final int ammInventorySpreadAdjustment,
                                                                final int referencePriceOffset) {
    final byte[] _data = new byte[14];
    int i = UPDATE_PERP_MARKET_AMM_SPREAD_ADJUSTMENT_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) ammSpreadAdjustment;
    ++i;
    _data[i] = (byte) ammInventorySpreadAdjustment;
    ++i;
    putInt32LE(_data, i, referencePriceOffset);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketAmmSpreadAdjustmentIxData(Discriminator discriminator,
                                                          int ammSpreadAdjustment,
                                                          int ammInventorySpreadAdjustment,
                                                          int referencePriceOffset) implements Borsh {  

    public static UpdatePerpMarketAmmSpreadAdjustmentIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 14;

    public static UpdatePerpMarketAmmSpreadAdjustmentIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ammSpreadAdjustment = _data[i];
      ++i;
      final var ammInventorySpreadAdjustment = _data[i];
      ++i;
      final var referencePriceOffset = getInt32LE(_data, i);
      return new UpdatePerpMarketAmmSpreadAdjustmentIxData(discriminator, ammSpreadAdjustment, ammInventorySpreadAdjustment, referencePriceOffset);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) ammSpreadAdjustment;
      ++i;
      _data[i] = (byte) ammInventorySpreadAdjustment;
      ++i;
      putInt32LE(_data, i, referencePriceOffset);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_MARKET_ORACLE_SLOT_DELAY_OVERRIDE_DISCRIMINATOR = toDiscriminator(165, 91, 239, 227, 63, 172, 227, 8);

  public static List<AccountMeta> updatePerpMarketOracleSlotDelayOverrideKeys(final AccountMeta invokedDriftProgramMeta                                                                              ,
                                                                              final PublicKey adminKey,
                                                                              final PublicKey stateKey,
                                                                              final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction updatePerpMarketOracleSlotDelayOverride(final AccountMeta invokedDriftProgramMeta,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey perpMarketKey,
                                                                    final int oracleSlotDelayOverride) {
    final var keys = updatePerpMarketOracleSlotDelayOverrideKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return updatePerpMarketOracleSlotDelayOverride(invokedDriftProgramMeta, keys, oracleSlotDelayOverride);
  }

  public static Instruction updatePerpMarketOracleSlotDelayOverride(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final List<AccountMeta> keys,
                                                                    final int oracleSlotDelayOverride) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_MARKET_ORACLE_SLOT_DELAY_OVERRIDE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) oracleSlotDelayOverride;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpMarketOracleSlotDelayOverrideIxData(Discriminator discriminator, int oracleSlotDelayOverride) implements Borsh {  

    public static UpdatePerpMarketOracleSlotDelayOverrideIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpMarketOracleSlotDelayOverrideIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var oracleSlotDelayOverride = _data[i];
      return new UpdatePerpMarketOracleSlotDelayOverrideIxData(discriminator, oracleSlotDelayOverride);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) oracleSlotDelayOverride;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_MARKET_FUEL_DISCRIMINATOR = toDiscriminator(226, 253, 76, 71, 17, 2, 171, 169);

  public static List<AccountMeta> updateSpotMarketFuelKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey,
                                                           final PublicKey spotMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(spotMarketKey)
    );
  }

  public static Instruction updateSpotMarketFuel(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final PublicKey spotMarketKey,
                                                 final OptionalInt fuelBoostDeposits,
                                                 final OptionalInt fuelBoostBorrows,
                                                 final OptionalInt fuelBoostTaker,
                                                 final OptionalInt fuelBoostMaker,
                                                 final OptionalInt fuelBoostInsurance) {
    final var keys = updateSpotMarketFuelKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      spotMarketKey
    );
    return updateSpotMarketFuel(
      invokedDriftProgramMeta,
      keys,
      fuelBoostDeposits,
      fuelBoostBorrows,
      fuelBoostTaker,
      fuelBoostMaker,
      fuelBoostInsurance
    );
  }

  public static Instruction updateSpotMarketFuel(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final OptionalInt fuelBoostDeposits,
                                                 final OptionalInt fuelBoostBorrows,
                                                 final OptionalInt fuelBoostTaker,
                                                 final OptionalInt fuelBoostMaker,
                                                 final OptionalInt fuelBoostInsurance) {
    final byte[] _data = new byte[
    8
    + (fuelBoostDeposits == null || fuelBoostDeposits.isEmpty() ? 1 : 2)
    + (fuelBoostBorrows == null || fuelBoostBorrows.isEmpty() ? 1 : 2)
    + (fuelBoostTaker == null || fuelBoostTaker.isEmpty() ? 1 : 2)
    + (fuelBoostMaker == null || fuelBoostMaker.isEmpty() ? 1 : 2)
    + (fuelBoostInsurance == null || fuelBoostInsurance.isEmpty() ? 1 : 2)
    ];
    int i = UPDATE_SPOT_MARKET_FUEL_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptionalbyte(fuelBoostDeposits, _data, i);
    i += Borsh.writeOptionalbyte(fuelBoostBorrows, _data, i);
    i += Borsh.writeOptionalbyte(fuelBoostTaker, _data, i);
    i += Borsh.writeOptionalbyte(fuelBoostMaker, _data, i);
    Borsh.writeOptionalbyte(fuelBoostInsurance, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotMarketFuelIxData(Discriminator discriminator,
                                           OptionalInt fuelBoostDeposits,
                                           OptionalInt fuelBoostBorrows,
                                           OptionalInt fuelBoostTaker,
                                           OptionalInt fuelBoostMaker,
                                           OptionalInt fuelBoostInsurance) implements Borsh {  

    public static UpdateSpotMarketFuelIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateSpotMarketFuelIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt fuelBoostDeposits;
      if (_data[i] == 0) {
        fuelBoostDeposits = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostDeposits = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt fuelBoostBorrows;
      if (_data[i] == 0) {
        fuelBoostBorrows = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostBorrows = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt fuelBoostTaker;
      if (_data[i] == 0) {
        fuelBoostTaker = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostTaker = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt fuelBoostMaker;
      if (_data[i] == 0) {
        fuelBoostMaker = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostMaker = OptionalInt.of(_data[i] & 0xFF);
        ++i;
      }
      final OptionalInt fuelBoostInsurance;
      if (_data[i] == 0) {
        fuelBoostInsurance = OptionalInt.empty();
      } else {
        ++i;
      ;
        fuelBoostInsurance = OptionalInt.of(_data[i] & 0xFF);
      }
      return new UpdateSpotMarketFuelIxData(discriminator,
                                            fuelBoostDeposits,
                                            fuelBoostBorrows,
                                            fuelBoostTaker,
                                            fuelBoostMaker,
                                            fuelBoostInsurance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptionalbyte(fuelBoostDeposits, _data, i);
      i += Borsh.writeOptionalbyte(fuelBoostBorrows, _data, i);
      i += Borsh.writeOptionalbyte(fuelBoostTaker, _data, i);
      i += Borsh.writeOptionalbyte(fuelBoostMaker, _data, i);
      i += Borsh.writeOptionalbyte(fuelBoostInsurance, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (fuelBoostDeposits == null || fuelBoostDeposits.isEmpty() ? 1 : (1 + 1))
           + (fuelBoostBorrows == null || fuelBoostBorrows.isEmpty() ? 1 : (1 + 1))
           + (fuelBoostTaker == null || fuelBoostTaker.isEmpty() ? 1 : (1 + 1))
           + (fuelBoostMaker == null || fuelBoostMaker.isEmpty() ? 1 : (1 + 1))
           + (fuelBoostInsurance == null || fuelBoostInsurance.isEmpty() ? 1 : (1 + 1));
    }
  }

  public static final Discriminator INIT_USER_FUEL_DISCRIMINATOR = toDiscriminator(132, 191, 228, 141, 201, 138, 60, 48);

  public static List<AccountMeta> initUserFuelKeys(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final PublicKey adminKey,
                                                   final PublicKey stateKey,
                                                   final PublicKey userKey,
                                                   final PublicKey userStatsKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(userKey),
      createWrite(userStatsKey)
    );
  }

  public static Instruction initUserFuel(final AccountMeta invokedDriftProgramMeta,
                                         final PublicKey adminKey,
                                         final PublicKey stateKey,
                                         final PublicKey userKey,
                                         final PublicKey userStatsKey,
                                         final OptionalInt fuelBoostDeposits,
                                         final OptionalInt fuelBoostBorrows,
                                         final OptionalInt fuelBoostTaker,
                                         final OptionalInt fuelBoostMaker,
                                         final OptionalInt fuelBoostInsurance) {
    final var keys = initUserFuelKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      userKey,
      userStatsKey
    );
    return initUserFuel(
      invokedDriftProgramMeta,
      keys,
      fuelBoostDeposits,
      fuelBoostBorrows,
      fuelBoostTaker,
      fuelBoostMaker,
      fuelBoostInsurance
    );
  }

  public static Instruction initUserFuel(final AccountMeta invokedDriftProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final OptionalInt fuelBoostDeposits,
                                         final OptionalInt fuelBoostBorrows,
                                         final OptionalInt fuelBoostTaker,
                                         final OptionalInt fuelBoostMaker,
                                         final OptionalInt fuelBoostInsurance) {
    final byte[] _data = new byte[
    8
    + (fuelBoostDeposits == null || fuelBoostDeposits.isEmpty() ? 1 : 5)
    + (fuelBoostBorrows == null || fuelBoostBorrows.isEmpty() ? 1 : 5)
    + (fuelBoostTaker == null || fuelBoostTaker.isEmpty() ? 1 : 5)
    + (fuelBoostMaker == null || fuelBoostMaker.isEmpty() ? 1 : 5)
    + (fuelBoostInsurance == null || fuelBoostInsurance.isEmpty() ? 1 : 5)
    ];
    int i = INIT_USER_FUEL_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeOptional(fuelBoostDeposits, _data, i);
    i += Borsh.writeOptional(fuelBoostBorrows, _data, i);
    i += Borsh.writeOptional(fuelBoostTaker, _data, i);
    i += Borsh.writeOptional(fuelBoostMaker, _data, i);
    Borsh.writeOptional(fuelBoostInsurance, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitUserFuelIxData(Discriminator discriminator,
                                   OptionalInt fuelBoostDeposits,
                                   OptionalInt fuelBoostBorrows,
                                   OptionalInt fuelBoostTaker,
                                   OptionalInt fuelBoostMaker,
                                   OptionalInt fuelBoostInsurance) implements Borsh {  

    public static InitUserFuelIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitUserFuelIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final OptionalInt fuelBoostDeposits;
      if (_data[i] == 0) {
        fuelBoostDeposits = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostDeposits = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final OptionalInt fuelBoostBorrows;
      if (_data[i] == 0) {
        fuelBoostBorrows = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostBorrows = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final OptionalInt fuelBoostTaker;
      if (_data[i] == 0) {
        fuelBoostTaker = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostTaker = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final OptionalInt fuelBoostMaker;
      if (_data[i] == 0) {
        fuelBoostMaker = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        fuelBoostMaker = OptionalInt.of(getInt32LE(_data, i));
        i += 4;
      }
      final OptionalInt fuelBoostInsurance;
      if (_data[i] == 0) {
        fuelBoostInsurance = OptionalInt.empty();
      } else {
        ++i;
      ;
        fuelBoostInsurance = OptionalInt.of(getInt32LE(_data, i));
      }
      return new InitUserFuelIxData(discriminator,
                                    fuelBoostDeposits,
                                    fuelBoostBorrows,
                                    fuelBoostTaker,
                                    fuelBoostMaker,
                                    fuelBoostInsurance);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeOptional(fuelBoostDeposits, _data, i);
      i += Borsh.writeOptional(fuelBoostBorrows, _data, i);
      i += Borsh.writeOptional(fuelBoostTaker, _data, i);
      i += Borsh.writeOptional(fuelBoostMaker, _data, i);
      i += Borsh.writeOptional(fuelBoostInsurance, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + (fuelBoostDeposits == null || fuelBoostDeposits.isEmpty() ? 1 : (1 + 4))
           + (fuelBoostBorrows == null || fuelBoostBorrows.isEmpty() ? 1 : (1 + 4))
           + (fuelBoostTaker == null || fuelBoostTaker.isEmpty() ? 1 : (1 + 4))
           + (fuelBoostMaker == null || fuelBoostMaker.isEmpty() ? 1 : (1 + 4))
           + (fuelBoostInsurance == null || fuelBoostInsurance.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator UPDATE_ADMIN_DISCRIMINATOR = toDiscriminator(161, 176, 40, 213, 60, 184, 179, 228);

  public static List<AccountMeta> updateAdminKeys(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final PublicKey adminKey,
                                                  final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateAdmin(final AccountMeta invokedDriftProgramMeta,
                                        final PublicKey adminKey,
                                        final PublicKey stateKey,
                                        final PublicKey admin) {
    final var keys = updateAdminKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateAdmin(invokedDriftProgramMeta, keys, admin);
  }

  public static Instruction updateAdmin(final AccountMeta invokedDriftProgramMeta                                        ,
                                        final List<AccountMeta> keys,
                                        final PublicKey admin) {
    final byte[] _data = new byte[40];
    int i = UPDATE_ADMIN_DISCRIMINATOR.write(_data, 0);
    admin.write(_data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateAdminIxData(Discriminator discriminator, PublicKey admin) implements Borsh {  

    public static UpdateAdminIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateAdminIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var admin = readPubKey(_data, i);
      return new UpdateAdminIxData(discriminator, admin);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      admin.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_WHITELIST_MINT_DISCRIMINATOR = toDiscriminator(161, 15, 162, 19, 148, 120, 144, 151);

  public static List<AccountMeta> updateWhitelistMintKeys(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateWhitelistMint(final AccountMeta invokedDriftProgramMeta,
                                                final PublicKey adminKey,
                                                final PublicKey stateKey,
                                                final PublicKey whitelistMint) {
    final var keys = updateWhitelistMintKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateWhitelistMint(invokedDriftProgramMeta, keys, whitelistMint);
  }

  public static Instruction updateWhitelistMint(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final List<AccountMeta> keys,
                                                final PublicKey whitelistMint) {
    final byte[] _data = new byte[40];
    int i = UPDATE_WHITELIST_MINT_DISCRIMINATOR.write(_data, 0);
    whitelistMint.write(_data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateWhitelistMintIxData(Discriminator discriminator, PublicKey whitelistMint) implements Borsh {  

    public static UpdateWhitelistMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateWhitelistMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var whitelistMint = readPubKey(_data, i);
      return new UpdateWhitelistMintIxData(discriminator, whitelistMint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      whitelistMint.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_DISCOUNT_MINT_DISCRIMINATOR = toDiscriminator(32, 252, 122, 211, 66, 31, 47, 241);

  public static List<AccountMeta> updateDiscountMintKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateDiscountMint(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey adminKey,
                                               final PublicKey stateKey,
                                               final PublicKey discountMint) {
    final var keys = updateDiscountMintKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateDiscountMint(invokedDriftProgramMeta, keys, discountMint);
  }

  public static Instruction updateDiscountMint(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final PublicKey discountMint) {
    final byte[] _data = new byte[40];
    int i = UPDATE_DISCOUNT_MINT_DISCRIMINATOR.write(_data, 0);
    discountMint.write(_data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateDiscountMintIxData(Discriminator discriminator, PublicKey discountMint) implements Borsh {  

    public static UpdateDiscountMintIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateDiscountMintIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var discountMint = readPubKey(_data, i);
      return new UpdateDiscountMintIxData(discriminator, discountMint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      discountMint.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_EXCHANGE_STATUS_DISCRIMINATOR = toDiscriminator(83, 160, 252, 250, 129, 116, 49, 223);

  public static List<AccountMeta> updateExchangeStatusKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey adminKey,
                                                           final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateExchangeStatus(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey adminKey,
                                                 final PublicKey stateKey,
                                                 final int exchangeStatus) {
    final var keys = updateExchangeStatusKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateExchangeStatus(invokedDriftProgramMeta, keys, exchangeStatus);
  }

  public static Instruction updateExchangeStatus(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int exchangeStatus) {
    final byte[] _data = new byte[9];
    int i = UPDATE_EXCHANGE_STATUS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) exchangeStatus;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateExchangeStatusIxData(Discriminator discriminator, int exchangeStatus) implements Borsh {  

    public static UpdateExchangeStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateExchangeStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var exchangeStatus = _data[i] & 0xFF;
      return new UpdateExchangeStatusIxData(discriminator, exchangeStatus);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) exchangeStatus;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PERP_AUCTION_DURATION_DISCRIMINATOR = toDiscriminator(126, 110, 52, 174, 30, 206, 215, 90);

  public static List<AccountMeta> updatePerpAuctionDurationKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updatePerpAuctionDuration(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey stateKey,
                                                      final int minPerpAuctionDuration) {
    final var keys = updatePerpAuctionDurationKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updatePerpAuctionDuration(invokedDriftProgramMeta, keys, minPerpAuctionDuration);
  }

  public static Instruction updatePerpAuctionDuration(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int minPerpAuctionDuration) {
    final byte[] _data = new byte[9];
    int i = UPDATE_PERP_AUCTION_DURATION_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) minPerpAuctionDuration;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePerpAuctionDurationIxData(Discriminator discriminator, int minPerpAuctionDuration) implements Borsh {  

    public static UpdatePerpAuctionDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdatePerpAuctionDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var minPerpAuctionDuration = _data[i] & 0xFF;
      return new UpdatePerpAuctionDurationIxData(discriminator, minPerpAuctionDuration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) minPerpAuctionDuration;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_SPOT_AUCTION_DURATION_DISCRIMINATOR = toDiscriminator(182, 178, 203, 72, 187, 143, 157, 107);

  public static List<AccountMeta> updateSpotAuctionDurationKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateSpotAuctionDuration(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey stateKey,
                                                      final int defaultSpotAuctionDuration) {
    final var keys = updateSpotAuctionDurationKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateSpotAuctionDuration(invokedDriftProgramMeta, keys, defaultSpotAuctionDuration);
  }

  public static Instruction updateSpotAuctionDuration(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int defaultSpotAuctionDuration) {
    final byte[] _data = new byte[9];
    int i = UPDATE_SPOT_AUCTION_DURATION_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) defaultSpotAuctionDuration;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateSpotAuctionDurationIxData(Discriminator discriminator, int defaultSpotAuctionDuration) implements Borsh {  

    public static UpdateSpotAuctionDurationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateSpotAuctionDurationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var defaultSpotAuctionDuration = _data[i] & 0xFF;
      return new UpdateSpotAuctionDurationIxData(discriminator, defaultSpotAuctionDuration);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) defaultSpotAuctionDuration;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PRELAUNCH_ORACLE_DISCRIMINATOR = toDiscriminator(169, 178, 84, 25, 175, 62, 29, 247);

  public static List<AccountMeta> initializePrelaunchOracleKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey prelaunchOracleKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey rentKey,
                                                                final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(prelaunchOracleKey),
      createRead(stateKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializePrelaunchOracle(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey prelaunchOracleKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey rentKey,
                                                      final PublicKey systemProgramKey,
                                                      final PrelaunchOracleParams params) {
    final var keys = initializePrelaunchOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      prelaunchOracleKey,
      stateKey,
      rentKey,
      systemProgramKey
    );
    return initializePrelaunchOracle(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction initializePrelaunchOracle(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final PrelaunchOracleParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = INITIALIZE_PRELAUNCH_ORACLE_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializePrelaunchOracleIxData(Discriminator discriminator, PrelaunchOracleParams params) implements Borsh {  

    public static InitializePrelaunchOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializePrelaunchOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PrelaunchOracleParams.read(_data, i);
      return new InitializePrelaunchOracleIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params);
    }
  }

  public static final Discriminator UPDATE_PRELAUNCH_ORACLE_PARAMS_DISCRIMINATOR = toDiscriminator(98, 205, 147, 243, 18, 75, 83, 207);

  public static List<AccountMeta> updatePrelaunchOracleParamsKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey prelaunchOracleKey,
                                                                  final PublicKey perpMarketKey,
                                                                  final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(prelaunchOracleKey),
      createWrite(perpMarketKey),
      createRead(stateKey)
    );
  }

  public static Instruction updatePrelaunchOracleParams(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey adminKey,
                                                        final PublicKey prelaunchOracleKey,
                                                        final PublicKey perpMarketKey,
                                                        final PublicKey stateKey,
                                                        final PrelaunchOracleParams params) {
    final var keys = updatePrelaunchOracleParamsKeys(
      invokedDriftProgramMeta,
      adminKey,
      prelaunchOracleKey,
      perpMarketKey,
      stateKey
    );
    return updatePrelaunchOracleParams(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction updatePrelaunchOracleParams(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final PrelaunchOracleParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = UPDATE_PRELAUNCH_ORACLE_PARAMS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdatePrelaunchOracleParamsIxData(Discriminator discriminator, PrelaunchOracleParams params) implements Borsh {  

    public static UpdatePrelaunchOracleParamsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdatePrelaunchOracleParamsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = PrelaunchOracleParams.read(_data, i);
      return new UpdatePrelaunchOracleParamsIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(params);
    }
  }

  public static final Discriminator DELETE_PRELAUNCH_ORACLE_DISCRIMINATOR = toDiscriminator(59, 169, 100, 49, 69, 17, 173, 253);

  public static List<AccountMeta> deletePrelaunchOracleKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey adminKey,
                                                            final PublicKey prelaunchOracleKey,
                                                            final PublicKey perpMarketKey,
                                                            final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(prelaunchOracleKey),
      createRead(perpMarketKey),
      createRead(stateKey)
    );
  }

  public static Instruction deletePrelaunchOracle(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey adminKey,
                                                  final PublicKey prelaunchOracleKey,
                                                  final PublicKey perpMarketKey,
                                                  final PublicKey stateKey,
                                                  final int perpMarketIndex) {
    final var keys = deletePrelaunchOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      prelaunchOracleKey,
      perpMarketKey,
      stateKey
    );
    return deletePrelaunchOracle(invokedDriftProgramMeta, keys, perpMarketIndex);
  }

  public static Instruction deletePrelaunchOracle(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int perpMarketIndex) {
    final byte[] _data = new byte[10];
    int i = DELETE_PRELAUNCH_ORACLE_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, perpMarketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DeletePrelaunchOracleIxData(Discriminator discriminator, int perpMarketIndex) implements Borsh {  

    public static DeletePrelaunchOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static DeletePrelaunchOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var perpMarketIndex = getInt16LE(_data, i);
      return new DeletePrelaunchOracleIxData(discriminator, perpMarketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, perpMarketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PYTH_PULL_ORACLE_DISCRIMINATOR = toDiscriminator(249, 140, 253, 243, 248, 74, 240, 238);

  public static List<AccountMeta> initializePythPullOracleKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey adminKey,
                                                               final PublicKey pythSolanaReceiverKey,
                                                               final PublicKey priceFeedKey,
                                                               final PublicKey systemProgramKey,
                                                               final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(pythSolanaReceiverKey),
      createWrite(priceFeedKey),
      createRead(systemProgramKey),
      createRead(stateKey)
    );
  }

  public static Instruction initializePythPullOracle(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey adminKey,
                                                     final PublicKey pythSolanaReceiverKey,
                                                     final PublicKey priceFeedKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey stateKey,
                                                     final byte[] feedId) {
    final var keys = initializePythPullOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      pythSolanaReceiverKey,
      priceFeedKey,
      systemProgramKey,
      stateKey
    );
    return initializePythPullOracle(invokedDriftProgramMeta, keys, feedId);
  }

  public static Instruction initializePythPullOracle(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final byte[] feedId) {
    final byte[] _data = new byte[8 + Borsh.lenArray(feedId)];
    int i = INITIALIZE_PYTH_PULL_ORACLE_DISCRIMINATOR.write(_data, 0);
    Borsh.writeArrayChecked(feedId, 32, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializePythPullOracleIxData(Discriminator discriminator, byte[] feedId) implements Borsh {  

    public static InitializePythPullOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;
    public static final int FEED_ID_LEN = 32;

    public static InitializePythPullOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feedId = new byte[32];
      Borsh.readArray(feedId, _data, i);
      return new InitializePythPullOracleIxData(discriminator, feedId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeArrayChecked(feedId, 32, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_PYTH_LAZER_ORACLE_DISCRIMINATOR = toDiscriminator(140, 107, 33, 214, 235, 219, 103, 20);

  public static List<AccountMeta> initializePythLazerOracleKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey adminKey,
                                                                final PublicKey lazerOracleKey,
                                                                final PublicKey stateKey,
                                                                final PublicKey rentKey,
                                                                final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(lazerOracleKey),
      createRead(stateKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializePythLazerOracle(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey adminKey,
                                                      final PublicKey lazerOracleKey,
                                                      final PublicKey stateKey,
                                                      final PublicKey rentKey,
                                                      final PublicKey systemProgramKey,
                                                      final int feedId) {
    final var keys = initializePythLazerOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      lazerOracleKey,
      stateKey,
      rentKey,
      systemProgramKey
    );
    return initializePythLazerOracle(invokedDriftProgramMeta, keys, feedId);
  }

  public static Instruction initializePythLazerOracle(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final int feedId) {
    final byte[] _data = new byte[12];
    int i = INITIALIZE_PYTH_LAZER_ORACLE_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, feedId);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializePythLazerOracleIxData(Discriminator discriminator, int feedId) implements Borsh {  

    public static InitializePythLazerOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static InitializePythLazerOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var feedId = getInt32LE(_data, i);
      return new InitializePythLazerOracleIxData(discriminator, feedId);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, feedId);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator POST_PYTH_LAZER_ORACLE_UPDATE_DISCRIMINATOR = toDiscriminator(218, 237, 170, 245, 39, 143, 166, 33);

  public static List<AccountMeta> postPythLazerOracleUpdateKeys(final AccountMeta invokedDriftProgramMeta                                                                ,
                                                                final PublicKey keeperKey,
                                                                final PublicKey pythLazerStorageKey,
                                                                final PublicKey ixSysvarKey) {
    return List.of(
      createWritableSigner(keeperKey),
      createRead(pythLazerStorageKey),
      createRead(ixSysvarKey)
    );
  }

  public static Instruction postPythLazerOracleUpdate(final AccountMeta invokedDriftProgramMeta,
                                                      final PublicKey keeperKey,
                                                      final PublicKey pythLazerStorageKey,
                                                      final PublicKey ixSysvarKey,
                                                      final byte[] pythMessage) {
    final var keys = postPythLazerOracleUpdateKeys(
      invokedDriftProgramMeta,
      keeperKey,
      pythLazerStorageKey,
      ixSysvarKey
    );
    return postPythLazerOracleUpdate(invokedDriftProgramMeta, keys, pythMessage);
  }

  public static Instruction postPythLazerOracleUpdate(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final List<AccountMeta> keys,
                                                      final byte[] pythMessage) {
    final byte[] _data = new byte[8 + Borsh.lenVector(pythMessage)];
    int i = POST_PYTH_LAZER_ORACLE_UPDATE_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(pythMessage, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record PostPythLazerOracleUpdateIxData(Discriminator discriminator, byte[] pythMessage) implements Borsh {  

    public static PostPythLazerOracleUpdateIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static PostPythLazerOracleUpdateIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var pythMessage = Borsh.readbyteVector(_data, i);
      return new PostPythLazerOracleUpdateIxData(discriminator, pythMessage);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(pythMessage, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(pythMessage);
    }
  }

  public static final Discriminator INITIALIZE_HIGH_LEVERAGE_MODE_CONFIG_DISCRIMINATOR = toDiscriminator(213, 167, 93, 246, 208, 130, 90, 248);

  public static List<AccountMeta> initializeHighLeverageModeConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey highLeverageModeConfigKey,
                                                                       final PublicKey stateKey,
                                                                       final PublicKey rentKey,
                                                                       final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(highLeverageModeConfigKey),
      createRead(stateKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeHighLeverageModeConfig(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey highLeverageModeConfigKey,
                                                             final PublicKey stateKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey,
                                                             final int maxUsers) {
    final var keys = initializeHighLeverageModeConfigKeys(
      invokedDriftProgramMeta,
      adminKey,
      highLeverageModeConfigKey,
      stateKey,
      rentKey,
      systemProgramKey
    );
    return initializeHighLeverageModeConfig(invokedDriftProgramMeta, keys, maxUsers);
  }

  public static Instruction initializeHighLeverageModeConfig(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final int maxUsers) {
    final byte[] _data = new byte[12];
    int i = INITIALIZE_HIGH_LEVERAGE_MODE_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, maxUsers);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeHighLeverageModeConfigIxData(Discriminator discriminator, int maxUsers) implements Borsh {  

    public static InitializeHighLeverageModeConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static InitializeHighLeverageModeConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxUsers = getInt32LE(_data, i);
      return new InitializeHighLeverageModeConfigIxData(discriminator, maxUsers);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, maxUsers);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_HIGH_LEVERAGE_MODE_CONFIG_DISCRIMINATOR = toDiscriminator(64, 122, 212, 93, 141, 217, 202, 55);

  public static List<AccountMeta> updateHighLeverageModeConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey highLeverageModeConfigKey,
                                                                   final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(highLeverageModeConfigKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateHighLeverageModeConfig(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey highLeverageModeConfigKey,
                                                         final PublicKey stateKey,
                                                         final int maxUsers,
                                                         final boolean reduceOnly,
                                                         final OptionalInt currentUsers) {
    final var keys = updateHighLeverageModeConfigKeys(
      invokedDriftProgramMeta,
      adminKey,
      highLeverageModeConfigKey,
      stateKey
    );
    return updateHighLeverageModeConfig(
      invokedDriftProgramMeta,
      keys,
      maxUsers,
      reduceOnly,
      currentUsers
    );
  }

  public static Instruction updateHighLeverageModeConfig(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final int maxUsers,
                                                         final boolean reduceOnly,
                                                         final OptionalInt currentUsers) {
    final byte[] _data = new byte[
    13
    + (currentUsers == null || currentUsers.isEmpty() ? 1 : 5)
    ];
    int i = UPDATE_HIGH_LEVERAGE_MODE_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, maxUsers);
    i += 4;
    _data[i] = (byte) (reduceOnly ? 1 : 0);
    ++i;
    Borsh.writeOptional(currentUsers, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateHighLeverageModeConfigIxData(Discriminator discriminator,
                                                   int maxUsers,
                                                   boolean reduceOnly,
                                                   OptionalInt currentUsers) implements Borsh {  

    public static UpdateHighLeverageModeConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateHighLeverageModeConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxUsers = getInt32LE(_data, i);
      i += 4;
      final var reduceOnly = _data[i] == 1;
      ++i;
      final OptionalInt currentUsers;
      if (_data[i] == 0) {
        currentUsers = OptionalInt.empty();
      } else {
        ++i;
      ;
        currentUsers = OptionalInt.of(getInt32LE(_data, i));
      }
      return new UpdateHighLeverageModeConfigIxData(discriminator, maxUsers, reduceOnly, currentUsers);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, maxUsers);
      i += 4;
      _data[i] = (byte) (reduceOnly ? 1 : 0);
      ++i;
      i += Borsh.writeOptional(currentUsers, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 4 + 1 + (currentUsers == null || currentUsers.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator INITIALIZE_PROTECTED_MAKER_MODE_CONFIG_DISCRIMINATOR = toDiscriminator(67, 103, 220, 67, 88, 32, 252, 8);

  public static List<AccountMeta> initializeProtectedMakerModeConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                         ,
                                                                         final PublicKey adminKey,
                                                                         final PublicKey protectedMakerModeConfigKey,
                                                                         final PublicKey stateKey,
                                                                         final PublicKey rentKey,
                                                                         final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(protectedMakerModeConfigKey),
      createRead(stateKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeProtectedMakerModeConfig(final AccountMeta invokedDriftProgramMeta,
                                                               final PublicKey adminKey,
                                                               final PublicKey protectedMakerModeConfigKey,
                                                               final PublicKey stateKey,
                                                               final PublicKey rentKey,
                                                               final PublicKey systemProgramKey,
                                                               final int maxUsers) {
    final var keys = initializeProtectedMakerModeConfigKeys(
      invokedDriftProgramMeta,
      adminKey,
      protectedMakerModeConfigKey,
      stateKey,
      rentKey,
      systemProgramKey
    );
    return initializeProtectedMakerModeConfig(invokedDriftProgramMeta, keys, maxUsers);
  }

  public static Instruction initializeProtectedMakerModeConfig(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final List<AccountMeta> keys,
                                                               final int maxUsers) {
    final byte[] _data = new byte[12];
    int i = INITIALIZE_PROTECTED_MAKER_MODE_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, maxUsers);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeProtectedMakerModeConfigIxData(Discriminator discriminator, int maxUsers) implements Borsh {  

    public static InitializeProtectedMakerModeConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static InitializeProtectedMakerModeConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxUsers = getInt32LE(_data, i);
      return new InitializeProtectedMakerModeConfigIxData(discriminator, maxUsers);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, maxUsers);
      i += 4;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_PROTECTED_MAKER_MODE_CONFIG_DISCRIMINATOR = toDiscriminator(86, 166, 235, 253, 67, 202, 223, 17);

  public static List<AccountMeta> updateProtectedMakerModeConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey adminKey,
                                                                     final PublicKey protectedMakerModeConfigKey,
                                                                     final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(protectedMakerModeConfigKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateProtectedMakerModeConfig(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey adminKey,
                                                           final PublicKey protectedMakerModeConfigKey,
                                                           final PublicKey stateKey,
                                                           final int maxUsers,
                                                           final boolean reduceOnly,
                                                           final OptionalInt currentUsers) {
    final var keys = updateProtectedMakerModeConfigKeys(
      invokedDriftProgramMeta,
      adminKey,
      protectedMakerModeConfigKey,
      stateKey
    );
    return updateProtectedMakerModeConfig(
      invokedDriftProgramMeta,
      keys,
      maxUsers,
      reduceOnly,
      currentUsers
    );
  }

  public static Instruction updateProtectedMakerModeConfig(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final int maxUsers,
                                                           final boolean reduceOnly,
                                                           final OptionalInt currentUsers) {
    final byte[] _data = new byte[
    13
    + (currentUsers == null || currentUsers.isEmpty() ? 1 : 5)
    ];
    int i = UPDATE_PROTECTED_MAKER_MODE_CONFIG_DISCRIMINATOR.write(_data, 0);
    putInt32LE(_data, i, maxUsers);
    i += 4;
    _data[i] = (byte) (reduceOnly ? 1 : 0);
    ++i;
    Borsh.writeOptional(currentUsers, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateProtectedMakerModeConfigIxData(Discriminator discriminator,
                                                     int maxUsers,
                                                     boolean reduceOnly,
                                                     OptionalInt currentUsers) implements Borsh {  

    public static UpdateProtectedMakerModeConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateProtectedMakerModeConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxUsers = getInt32LE(_data, i);
      i += 4;
      final var reduceOnly = _data[i] == 1;
      ++i;
      final OptionalInt currentUsers;
      if (_data[i] == 0) {
        currentUsers = OptionalInt.empty();
      } else {
        ++i;
      ;
        currentUsers = OptionalInt.of(getInt32LE(_data, i));
      }
      return new UpdateProtectedMakerModeConfigIxData(discriminator, maxUsers, reduceOnly, currentUsers);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt32LE(_data, i, maxUsers);
      i += 4;
      _data[i] = (byte) (reduceOnly ? 1 : 0);
      ++i;
      i += Borsh.writeOptional(currentUsers, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 4 + 1 + (currentUsers == null || currentUsers.isEmpty() ? 1 : (1 + 4));
    }
  }

  public static final Discriminator ADMIN_DEPOSIT_DISCRIMINATOR = toDiscriminator(210, 66, 65, 182, 102, 214, 176, 30);

  public static List<AccountMeta> adminDepositKeys(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final PublicKey stateKey,
                                                   final PublicKey userKey,
                                                   final PublicKey adminKey,
                                                   final PublicKey spotMarketVaultKey,
                                                   final PublicKey adminTokenAccountKey,
                                                   final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(userKey),
      createWritableSigner(adminKey),
      createWrite(spotMarketVaultKey),
      createWrite(adminTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction adminDeposit(final AccountMeta invokedDriftProgramMeta,
                                         final PublicKey stateKey,
                                         final PublicKey userKey,
                                         final PublicKey adminKey,
                                         final PublicKey spotMarketVaultKey,
                                         final PublicKey adminTokenAccountKey,
                                         final PublicKey tokenProgramKey,
                                         final int marketIndex,
                                         final long amount) {
    final var keys = adminDepositKeys(
      invokedDriftProgramMeta,
      stateKey,
      userKey,
      adminKey,
      spotMarketVaultKey,
      adminTokenAccountKey,
      tokenProgramKey
    );
    return adminDeposit(invokedDriftProgramMeta, keys, marketIndex, amount);
  }

  public static Instruction adminDeposit(final AccountMeta invokedDriftProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final int marketIndex,
                                         final long amount) {
    final byte[] _data = new byte[18];
    int i = ADMIN_DEPOSIT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record AdminDepositIxData(Discriminator discriminator, int marketIndex, long amount) implements Borsh {  

    public static AdminDepositIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static AdminDepositIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var amount = getInt64LE(_data, i);
      return new AdminDepositIxData(discriminator, marketIndex, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_IF_REBALANCE_CONFIG_DISCRIMINATOR = toDiscriminator(8, 85, 184, 167, 176, 61, 173, 226);

  public static List<AccountMeta> initializeIfRebalanceConfigKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey ifRebalanceConfigKey,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey rentKey,
                                                                  final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(ifRebalanceConfigKey),
      createRead(stateKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeIfRebalanceConfig(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey adminKey,
                                                        final PublicKey ifRebalanceConfigKey,
                                                        final PublicKey stateKey,
                                                        final PublicKey rentKey,
                                                        final PublicKey systemProgramKey,
                                                        final IfRebalanceConfigParams params) {
    final var keys = initializeIfRebalanceConfigKeys(
      invokedDriftProgramMeta,
      adminKey,
      ifRebalanceConfigKey,
      stateKey,
      rentKey,
      systemProgramKey
    );
    return initializeIfRebalanceConfig(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction initializeIfRebalanceConfig(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys,
                                                        final IfRebalanceConfigParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = INITIALIZE_IF_REBALANCE_CONFIG_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeIfRebalanceConfigIxData(Discriminator discriminator, IfRebalanceConfigParams params) implements Borsh {  

    public static InitializeIfRebalanceConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static InitializeIfRebalanceConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = IfRebalanceConfigParams.read(_data, i);
      return new InitializeIfRebalanceConfigIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_IF_REBALANCE_CONFIG_DISCRIMINATOR = toDiscriminator(142, 245, 249, 66, 249, 181, 22, 83);

  public static List<AccountMeta> updateIfRebalanceConfigKeys(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final PublicKey adminKey,
                                                              final PublicKey ifRebalanceConfigKey,
                                                              final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(ifRebalanceConfigKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateIfRebalanceConfig(final AccountMeta invokedDriftProgramMeta,
                                                    final PublicKey adminKey,
                                                    final PublicKey ifRebalanceConfigKey,
                                                    final PublicKey stateKey,
                                                    final IfRebalanceConfigParams params) {
    final var keys = updateIfRebalanceConfigKeys(
      invokedDriftProgramMeta,
      adminKey,
      ifRebalanceConfigKey,
      stateKey
    );
    return updateIfRebalanceConfig(invokedDriftProgramMeta, keys, params);
  }

  public static Instruction updateIfRebalanceConfig(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final List<AccountMeta> keys,
                                                    final IfRebalanceConfigParams params) {
    final byte[] _data = new byte[8 + Borsh.len(params)];
    int i = UPDATE_IF_REBALANCE_CONFIG_DISCRIMINATOR.write(_data, 0);
    Borsh.write(params, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateIfRebalanceConfigIxData(Discriminator discriminator, IfRebalanceConfigParams params) implements Borsh {  

    public static UpdateIfRebalanceConfigIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 40;

    public static UpdateIfRebalanceConfigIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var params = IfRebalanceConfigParams.read(_data, i);
      return new UpdateIfRebalanceConfigIxData(discriminator, params);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(params, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEATURE_BIT_FLAGS_MM_ORACLE_DISCRIMINATOR = toDiscriminator(218, 134, 33, 186, 231, 59, 130, 149);

  public static List<AccountMeta> updateFeatureBitFlagsMmOracleKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateFeatureBitFlagsMmOracle(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey adminKey,
                                                          final PublicKey stateKey,
                                                          final boolean enable) {
    final var keys = updateFeatureBitFlagsMmOracleKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateFeatureBitFlagsMmOracle(invokedDriftProgramMeta, keys, enable);
  }

  public static Instruction updateFeatureBitFlagsMmOracle(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final boolean enable) {
    final byte[] _data = new byte[9];
    int i = UPDATE_FEATURE_BIT_FLAGS_MM_ORACLE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (enable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFeatureBitFlagsMmOracleIxData(Discriminator discriminator, boolean enable) implements Borsh {  

    public static UpdateFeatureBitFlagsMmOracleIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateFeatureBitFlagsMmOracleIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enable = _data[i] == 1;
      return new UpdateFeatureBitFlagsMmOracleIxData(discriminator, enable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (enable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator ZERO_MM_ORACLE_FIELDS_DISCRIMINATOR = toDiscriminator(192, 226, 39, 204, 207, 120, 148, 250);

  public static List<AccountMeta> zeroMmOracleFieldsKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey perpMarketKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createRead(stateKey),
      createWrite(perpMarketKey)
    );
  }

  public static Instruction zeroMmOracleFields(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey adminKey,
                                               final PublicKey stateKey,
                                               final PublicKey perpMarketKey) {
    final var keys = zeroMmOracleFieldsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      perpMarketKey
    );
    return zeroMmOracleFields(invokedDriftProgramMeta, keys);
  }

  public static Instruction zeroMmOracleFields(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, ZERO_MM_ORACLE_FIELDS_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_FEATURE_BIT_FLAGS_MEDIAN_TRIGGER_PRICE_DISCRIMINATOR = toDiscriminator(64, 185, 221, 45, 87, 147, 12, 19);

  public static List<AccountMeta> updateFeatureBitFlagsMedianTriggerPriceKeys(final AccountMeta invokedDriftProgramMeta                                                                              ,
                                                                              final PublicKey adminKey,
                                                                              final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateFeatureBitFlagsMedianTriggerPrice(final AccountMeta invokedDriftProgramMeta,
                                                                    final PublicKey adminKey,
                                                                    final PublicKey stateKey,
                                                                    final boolean enable) {
    final var keys = updateFeatureBitFlagsMedianTriggerPriceKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateFeatureBitFlagsMedianTriggerPrice(invokedDriftProgramMeta, keys, enable);
  }

  public static Instruction updateFeatureBitFlagsMedianTriggerPrice(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final List<AccountMeta> keys,
                                                                    final boolean enable) {
    final byte[] _data = new byte[9];
    int i = UPDATE_FEATURE_BIT_FLAGS_MEDIAN_TRIGGER_PRICE_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (enable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFeatureBitFlagsMedianTriggerPriceIxData(Discriminator discriminator, boolean enable) implements Borsh {  

    public static UpdateFeatureBitFlagsMedianTriggerPriceIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateFeatureBitFlagsMedianTriggerPriceIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enable = _data[i] == 1;
      return new UpdateFeatureBitFlagsMedianTriggerPriceIxData(discriminator, enable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (enable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEATURE_BIT_FLAGS_BUILDER_CODES_DISCRIMINATOR = toDiscriminator(1, 128, 177, 51, 173, 45, 11, 102);

  public static List<AccountMeta> updateFeatureBitFlagsBuilderCodesKeys(final AccountMeta invokedDriftProgramMeta                                                                        ,
                                                                        final PublicKey adminKey,
                                                                        final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateFeatureBitFlagsBuilderCodes(final AccountMeta invokedDriftProgramMeta,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final boolean enable) {
    final var keys = updateFeatureBitFlagsBuilderCodesKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateFeatureBitFlagsBuilderCodes(invokedDriftProgramMeta, keys, enable);
  }

  public static Instruction updateFeatureBitFlagsBuilderCodes(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final List<AccountMeta> keys,
                                                              final boolean enable) {
    final byte[] _data = new byte[9];
    int i = UPDATE_FEATURE_BIT_FLAGS_BUILDER_CODES_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (enable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFeatureBitFlagsBuilderCodesIxData(Discriminator discriminator, boolean enable) implements Borsh {  

    public static UpdateFeatureBitFlagsBuilderCodesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateFeatureBitFlagsBuilderCodesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enable = _data[i] == 1;
      return new UpdateFeatureBitFlagsBuilderCodesIxData(discriminator, enable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (enable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_REVENUE_SHARE_DISCRIMINATOR = toDiscriminator(57, 9, 123, 131, 82, 52, 50, 13);

  public static List<AccountMeta> initializeRevenueShareKeys(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final PublicKey revenueShareKey,
                                                             final PublicKey authorityKey,
                                                             final PublicKey payerKey,
                                                             final PublicKey rentKey,
                                                             final PublicKey systemProgramKey) {
    return List.of(
      createWrite(revenueShareKey),
      createRead(authorityKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeRevenueShare(final AccountMeta invokedDriftProgramMeta,
                                                   final PublicKey revenueShareKey,
                                                   final PublicKey authorityKey,
                                                   final PublicKey payerKey,
                                                   final PublicKey rentKey,
                                                   final PublicKey systemProgramKey) {
    final var keys = initializeRevenueShareKeys(
      invokedDriftProgramMeta,
      revenueShareKey,
      authorityKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeRevenueShare(invokedDriftProgramMeta, keys);
  }

  public static Instruction initializeRevenueShare(final AccountMeta invokedDriftProgramMeta                                                   ,
                                                   final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, INITIALIZE_REVENUE_SHARE_DISCRIMINATOR);
  }

  public static final Discriminator INITIALIZE_REVENUE_SHARE_ESCROW_DISCRIMINATOR = toDiscriminator(187, 18, 123, 88, 238, 104, 84, 154);

  public static List<AccountMeta> initializeRevenueShareEscrowKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey escrowKey,
                                                                   final PublicKey authorityKey,
                                                                   final PublicKey userStatsKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey payerKey,
                                                                   final PublicKey rentKey,
                                                                   final PublicKey systemProgramKey) {
    return List.of(
      createWrite(escrowKey),
      createRead(authorityKey),
      createWrite(userStatsKey),
      createRead(stateKey),
      createWritableSigner(payerKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeRevenueShareEscrow(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey escrowKey,
                                                         final PublicKey authorityKey,
                                                         final PublicKey userStatsKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey payerKey,
                                                         final PublicKey rentKey,
                                                         final PublicKey systemProgramKey,
                                                         final int numOrders) {
    final var keys = initializeRevenueShareEscrowKeys(
      invokedDriftProgramMeta,
      escrowKey,
      authorityKey,
      userStatsKey,
      stateKey,
      payerKey,
      rentKey,
      systemProgramKey
    );
    return initializeRevenueShareEscrow(invokedDriftProgramMeta, keys, numOrders);
  }

  public static Instruction initializeRevenueShareEscrow(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final int numOrders) {
    final byte[] _data = new byte[10];
    int i = INITIALIZE_REVENUE_SHARE_ESCROW_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, numOrders);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeRevenueShareEscrowIxData(Discriminator discriminator, int numOrders) implements Borsh {  

    public static InitializeRevenueShareEscrowIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static InitializeRevenueShareEscrowIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var numOrders = getInt16LE(_data, i);
      return new InitializeRevenueShareEscrowIxData(discriminator, numOrders);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, numOrders);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator RESIZE_REVENUE_SHARE_ESCROW_ORDERS_DISCRIMINATOR = toDiscriminator(32, 124, 247, 225, 151, 213, 225, 38);

  public static List<AccountMeta> resizeRevenueShareEscrowOrdersKeys(final AccountMeta invokedDriftProgramMeta                                                                     ,
                                                                     final PublicKey escrowKey,
                                                                     final PublicKey authorityKey,
                                                                     final PublicKey payerKey,
                                                                     final PublicKey systemProgramKey) {
    return List.of(
      createWrite(escrowKey),
      createRead(authorityKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction resizeRevenueShareEscrowOrders(final AccountMeta invokedDriftProgramMeta,
                                                           final PublicKey escrowKey,
                                                           final PublicKey authorityKey,
                                                           final PublicKey payerKey,
                                                           final PublicKey systemProgramKey,
                                                           final int numOrders) {
    final var keys = resizeRevenueShareEscrowOrdersKeys(
      invokedDriftProgramMeta,
      escrowKey,
      authorityKey,
      payerKey,
      systemProgramKey
    );
    return resizeRevenueShareEscrowOrders(invokedDriftProgramMeta, keys, numOrders);
  }

  public static Instruction resizeRevenueShareEscrowOrders(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final List<AccountMeta> keys,
                                                           final int numOrders) {
    final byte[] _data = new byte[10];
    int i = RESIZE_REVENUE_SHARE_ESCROW_ORDERS_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, numOrders);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ResizeRevenueShareEscrowOrdersIxData(Discriminator discriminator, int numOrders) implements Borsh {  

    public static ResizeRevenueShareEscrowOrdersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 10;

    public static ResizeRevenueShareEscrowOrdersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var numOrders = getInt16LE(_data, i);
      return new ResizeRevenueShareEscrowOrdersIxData(discriminator, numOrders);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, numOrders);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CHANGE_APPROVED_BUILDER_DISCRIMINATOR = toDiscriminator(179, 134, 211, 45, 195, 5, 189, 173);

  public static List<AccountMeta> changeApprovedBuilderKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey escrowKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey payerKey,
                                                            final PublicKey systemProgramKey) {
    return List.of(
      createWrite(escrowKey),
      createReadOnlySigner(authorityKey),
      createWritableSigner(payerKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction changeApprovedBuilder(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey escrowKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey payerKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey builder,
                                                  final int maxFeeBps,
                                                  final boolean add) {
    final var keys = changeApprovedBuilderKeys(
      invokedDriftProgramMeta,
      escrowKey,
      authorityKey,
      payerKey,
      systemProgramKey
    );
    return changeApprovedBuilder(
      invokedDriftProgramMeta,
      keys,
      builder,
      maxFeeBps,
      add
    );
  }

  public static Instruction changeApprovedBuilder(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final PublicKey builder,
                                                  final int maxFeeBps,
                                                  final boolean add) {
    final byte[] _data = new byte[43];
    int i = CHANGE_APPROVED_BUILDER_DISCRIMINATOR.write(_data, 0);
    builder.write(_data, i);
    i += 32;
    putInt16LE(_data, i, maxFeeBps);
    i += 2;
    _data[i] = (byte) (add ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ChangeApprovedBuilderIxData(Discriminator discriminator,
                                            PublicKey builder,
                                            int maxFeeBps,
                                            boolean add) implements Borsh {  

    public static ChangeApprovedBuilderIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 43;

    public static ChangeApprovedBuilderIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var builder = readPubKey(_data, i);
      i += 32;
      final var maxFeeBps = getInt16LE(_data, i);
      i += 2;
      final var add = _data[i] == 1;
      return new ChangeApprovedBuilderIxData(discriminator, builder, maxFeeBps, add);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      builder.write(_data, i);
      i += 32;
      putInt16LE(_data, i, maxFeeBps);
      i += 2;
      _data[i] = (byte) (add ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_LP_POOL_DISCRIMINATOR = toDiscriminator(242, 64, 1, 222, 142, 46, 204, 227);

  public static List<AccountMeta> initializeLpPoolKeys(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final PublicKey adminKey,
                                                       final PublicKey lpPoolKey,
                                                       final PublicKey mintKey,
                                                       final PublicKey lpPoolTokenVaultKey,
                                                       final PublicKey ammConstituentMappingKey,
                                                       final PublicKey constituentTargetBaseKey,
                                                       final PublicKey constituentCorrelationsKey,
                                                       final PublicKey stateKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey rentKey,
                                                       final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createWrite(lpPoolKey),
      createRead(mintKey),
      createWrite(lpPoolTokenVaultKey),
      createWrite(ammConstituentMappingKey),
      createWrite(constituentTargetBaseKey),
      createWrite(constituentCorrelationsKey),
      createRead(stateKey),
      createRead(tokenProgramKey),
      createRead(rentKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction initializeLpPool(final AccountMeta invokedDriftProgramMeta,
                                             final PublicKey adminKey,
                                             final PublicKey lpPoolKey,
                                             final PublicKey mintKey,
                                             final PublicKey lpPoolTokenVaultKey,
                                             final PublicKey ammConstituentMappingKey,
                                             final PublicKey constituentTargetBaseKey,
                                             final PublicKey constituentCorrelationsKey,
                                             final PublicKey stateKey,
                                             final PublicKey tokenProgramKey,
                                             final PublicKey rentKey,
                                             final PublicKey systemProgramKey,
                                             final int lpPoolId,
                                             final long minMintFee,
                                             final BigInteger maxAum,
                                             final long maxSettleQuoteAmountPerMarket,
                                             final PublicKey whitelistMint) {
    final var keys = initializeLpPoolKeys(
      invokedDriftProgramMeta,
      adminKey,
      lpPoolKey,
      mintKey,
      lpPoolTokenVaultKey,
      ammConstituentMappingKey,
      constituentTargetBaseKey,
      constituentCorrelationsKey,
      stateKey,
      tokenProgramKey,
      rentKey,
      systemProgramKey
    );
    return initializeLpPool(
      invokedDriftProgramMeta,
      keys,
      lpPoolId,
      minMintFee,
      maxAum,
      maxSettleQuoteAmountPerMarket,
      whitelistMint
    );
  }

  public static Instruction initializeLpPool(final AccountMeta invokedDriftProgramMeta                                             ,
                                             final List<AccountMeta> keys,
                                             final int lpPoolId,
                                             final long minMintFee,
                                             final BigInteger maxAum,
                                             final long maxSettleQuoteAmountPerMarket,
                                             final PublicKey whitelistMint) {
    final byte[] _data = new byte[73];
    int i = INITIALIZE_LP_POOL_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) lpPoolId;
    ++i;
    putInt64LE(_data, i, minMintFee);
    i += 8;
    putInt128LE(_data, i, maxAum);
    i += 16;
    putInt64LE(_data, i, maxSettleQuoteAmountPerMarket);
    i += 8;
    whitelistMint.write(_data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeLpPoolIxData(Discriminator discriminator,
                                       int lpPoolId,
                                       long minMintFee,
                                       BigInteger maxAum,
                                       long maxSettleQuoteAmountPerMarket,
                                       PublicKey whitelistMint) implements Borsh {  

    public static InitializeLpPoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 73;

    public static InitializeLpPoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lpPoolId = _data[i] & 0xFF;
      ++i;
      final var minMintFee = getInt64LE(_data, i);
      i += 8;
      final var maxAum = getInt128LE(_data, i);
      i += 16;
      final var maxSettleQuoteAmountPerMarket = getInt64LE(_data, i);
      i += 8;
      final var whitelistMint = readPubKey(_data, i);
      return new InitializeLpPoolIxData(discriminator,
                                        lpPoolId,
                                        minMintFee,
                                        maxAum,
                                        maxSettleQuoteAmountPerMarket,
                                        whitelistMint);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) lpPoolId;
      ++i;
      putInt64LE(_data, i, minMintFee);
      i += 8;
      putInt128LE(_data, i, maxAum);
      i += 16;
      putInt64LE(_data, i, maxSettleQuoteAmountPerMarket);
      i += 8;
      whitelistMint.write(_data, i);
      i += 32;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEATURE_BIT_FLAGS_SETTLE_LP_POOL_DISCRIMINATOR = toDiscriminator(186, 28, 78, 230, 155, 83, 242, 26);

  public static List<AccountMeta> updateFeatureBitFlagsSettleLpPoolKeys(final AccountMeta invokedDriftProgramMeta                                                                        ,
                                                                        final PublicKey adminKey,
                                                                        final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateFeatureBitFlagsSettleLpPool(final AccountMeta invokedDriftProgramMeta,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final boolean enable) {
    final var keys = updateFeatureBitFlagsSettleLpPoolKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateFeatureBitFlagsSettleLpPool(invokedDriftProgramMeta, keys, enable);
  }

  public static Instruction updateFeatureBitFlagsSettleLpPool(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final List<AccountMeta> keys,
                                                              final boolean enable) {
    final byte[] _data = new byte[9];
    int i = UPDATE_FEATURE_BIT_FLAGS_SETTLE_LP_POOL_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (enable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFeatureBitFlagsSettleLpPoolIxData(Discriminator discriminator, boolean enable) implements Borsh {  

    public static UpdateFeatureBitFlagsSettleLpPoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateFeatureBitFlagsSettleLpPoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enable = _data[i] == 1;
      return new UpdateFeatureBitFlagsSettleLpPoolIxData(discriminator, enable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (enable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEATURE_BIT_FLAGS_SWAP_LP_POOL_DISCRIMINATOR = toDiscriminator(83, 16, 150, 12, 102, 3, 22, 58);

  public static List<AccountMeta> updateFeatureBitFlagsSwapLpPoolKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateFeatureBitFlagsSwapLpPool(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey stateKey,
                                                            final boolean enable) {
    final var keys = updateFeatureBitFlagsSwapLpPoolKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateFeatureBitFlagsSwapLpPool(invokedDriftProgramMeta, keys, enable);
  }

  public static Instruction updateFeatureBitFlagsSwapLpPool(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final boolean enable) {
    final byte[] _data = new byte[9];
    int i = UPDATE_FEATURE_BIT_FLAGS_SWAP_LP_POOL_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (enable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFeatureBitFlagsSwapLpPoolIxData(Discriminator discriminator, boolean enable) implements Borsh {  

    public static UpdateFeatureBitFlagsSwapLpPoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateFeatureBitFlagsSwapLpPoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enable = _data[i] == 1;
      return new UpdateFeatureBitFlagsSwapLpPoolIxData(discriminator, enable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (enable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_FEATURE_BIT_FLAGS_MINT_REDEEM_LP_POOL_DISCRIMINATOR = toDiscriminator(26, 11, 142, 122, 206, 159, 9, 45);

  public static List<AccountMeta> updateFeatureBitFlagsMintRedeemLpPoolKeys(final AccountMeta invokedDriftProgramMeta                                                                            ,
                                                                            final PublicKey adminKey,
                                                                            final PublicKey stateKey) {
    return List.of(
      createReadOnlySigner(adminKey),
      createWrite(stateKey)
    );
  }

  public static Instruction updateFeatureBitFlagsMintRedeemLpPool(final AccountMeta invokedDriftProgramMeta,
                                                                  final PublicKey adminKey,
                                                                  final PublicKey stateKey,
                                                                  final boolean enable) {
    final var keys = updateFeatureBitFlagsMintRedeemLpPoolKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey
    );
    return updateFeatureBitFlagsMintRedeemLpPool(invokedDriftProgramMeta, keys, enable);
  }

  public static Instruction updateFeatureBitFlagsMintRedeemLpPool(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final List<AccountMeta> keys,
                                                                  final boolean enable) {
    final byte[] _data = new byte[9];
    int i = UPDATE_FEATURE_BIT_FLAGS_MINT_REDEEM_LP_POOL_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (enable ? 1 : 0);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateFeatureBitFlagsMintRedeemLpPoolIxData(Discriminator discriminator, boolean enable) implements Borsh {  

    public static UpdateFeatureBitFlagsMintRedeemLpPoolIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateFeatureBitFlagsMintRedeemLpPoolIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var enable = _data[i] == 1;
      return new UpdateFeatureBitFlagsMintRedeemLpPoolIxData(discriminator, enable);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (enable ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator INITIALIZE_CONSTITUENT_DISCRIMINATOR = toDiscriminator(12, 196, 45, 218, 93, 89, 0, 33);

  public static List<AccountMeta> initializeConstituentKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey adminKey,
                                                            final PublicKey lpPoolKey,
                                                            final PublicKey constituentTargetBaseKey,
                                                            final PublicKey constituentCorrelationsKey,
                                                            final PublicKey constituentKey,
                                                            final PublicKey spotMarketKey,
                                                            final PublicKey spotMarketMintKey,
                                                            final PublicKey constituentVaultKey,
                                                            final PublicKey rentKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(adminKey),
      createWrite(lpPoolKey),
      createWrite(constituentTargetBaseKey),
      createWrite(constituentCorrelationsKey),
      createWrite(constituentKey),
      createRead(spotMarketKey),
      createRead(spotMarketMintKey),
      createWrite(constituentVaultKey),
      createRead(rentKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction initializeConstituent(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey adminKey,
                                                  final PublicKey lpPoolKey,
                                                  final PublicKey constituentTargetBaseKey,
                                                  final PublicKey constituentCorrelationsKey,
                                                  final PublicKey constituentKey,
                                                  final PublicKey spotMarketKey,
                                                  final PublicKey spotMarketMintKey,
                                                  final PublicKey constituentVaultKey,
                                                  final PublicKey rentKey,
                                                  final PublicKey systemProgramKey,
                                                  final PublicKey tokenProgramKey,
                                                  final int spotMarketIndex,
                                                  final int decimals,
                                                  final long maxWeightDeviation,
                                                  final long swapFeeMin,
                                                  final long swapFeeMax,
                                                  final long maxBorrowTokenAmount,
                                                  final long oracleStalenessThreshold,
                                                  final int costToTrade,
                                                  final OptionalInt constituentDerivativeIndex,
                                                  final long constituentDerivativeDepegThreshold,
                                                  final long derivativeWeight,
                                                  final long volatility,
                                                  final int gammaExecution,
                                                  final int gammaInventory,
                                                  final int xi,
                                                  final long[] newConstituentCorrelations) {
    final var keys = initializeConstituentKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      lpPoolKey,
      constituentTargetBaseKey,
      constituentCorrelationsKey,
      constituentKey,
      spotMarketKey,
      spotMarketMintKey,
      constituentVaultKey,
      rentKey,
      systemProgramKey,
      tokenProgramKey
    );
    return initializeConstituent(
      invokedDriftProgramMeta,
      keys,
      spotMarketIndex,
      decimals,
      maxWeightDeviation,
      swapFeeMin,
      swapFeeMax,
      maxBorrowTokenAmount,
      oracleStalenessThreshold,
      costToTrade,
      constituentDerivativeIndex,
      constituentDerivativeDepegThreshold,
      derivativeWeight,
      volatility,
      gammaExecution,
      gammaInventory,
      xi,
      newConstituentCorrelations
    );
  }

  public static Instruction initializeConstituent(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int spotMarketIndex,
                                                  final int decimals,
                                                  final long maxWeightDeviation,
                                                  final long swapFeeMin,
                                                  final long swapFeeMax,
                                                  final long maxBorrowTokenAmount,
                                                  final long oracleStalenessThreshold,
                                                  final int costToTrade,
                                                  final OptionalInt constituentDerivativeIndex,
                                                  final long constituentDerivativeDepegThreshold,
                                                  final long derivativeWeight,
                                                  final long volatility,
                                                  final int gammaExecution,
                                                  final int gammaInventory,
                                                  final int xi,
                                                  final long[] newConstituentCorrelations) {
    final byte[] _data = new byte[
    82
    + (constituentDerivativeIndex == null || constituentDerivativeIndex.isEmpty() ? 1 : 3) + Borsh.lenVector(newConstituentCorrelations)
    ];
    int i = INITIALIZE_CONSTITUENT_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, spotMarketIndex);
    i += 2;
    _data[i] = (byte) decimals;
    ++i;
    putInt64LE(_data, i, maxWeightDeviation);
    i += 8;
    putInt64LE(_data, i, swapFeeMin);
    i += 8;
    putInt64LE(_data, i, swapFeeMax);
    i += 8;
    putInt64LE(_data, i, maxBorrowTokenAmount);
    i += 8;
    putInt64LE(_data, i, oracleStalenessThreshold);
    i += 8;
    putInt32LE(_data, i, costToTrade);
    i += 4;
    i += Borsh.writeOptionalshort(constituentDerivativeIndex, _data, i);
    putInt64LE(_data, i, constituentDerivativeDepegThreshold);
    i += 8;
    putInt64LE(_data, i, derivativeWeight);
    i += 8;
    putInt64LE(_data, i, volatility);
    i += 8;
    _data[i] = (byte) gammaExecution;
    ++i;
    _data[i] = (byte) gammaInventory;
    ++i;
    _data[i] = (byte) xi;
    ++i;
    Borsh.writeVector(newConstituentCorrelations, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record InitializeConstituentIxData(Discriminator discriminator,
                                            int spotMarketIndex,
                                            int decimals,
                                            long maxWeightDeviation,
                                            long swapFeeMin,
                                            long swapFeeMax,
                                            long maxBorrowTokenAmount,
                                            long oracleStalenessThreshold,
                                            int costToTrade,
                                            OptionalInt constituentDerivativeIndex,
                                            long constituentDerivativeDepegThreshold,
                                            long derivativeWeight,
                                            long volatility,
                                            int gammaExecution,
                                            int gammaInventory,
                                            int xi,
                                            long[] newConstituentCorrelations) implements Borsh {  

    public static InitializeConstituentIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static InitializeConstituentIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var spotMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var decimals = _data[i] & 0xFF;
      ++i;
      final var maxWeightDeviation = getInt64LE(_data, i);
      i += 8;
      final var swapFeeMin = getInt64LE(_data, i);
      i += 8;
      final var swapFeeMax = getInt64LE(_data, i);
      i += 8;
      final var maxBorrowTokenAmount = getInt64LE(_data, i);
      i += 8;
      final var oracleStalenessThreshold = getInt64LE(_data, i);
      i += 8;
      final var costToTrade = getInt32LE(_data, i);
      i += 4;
      final OptionalInt constituentDerivativeIndex;
      if (_data[i] == 0) {
        constituentDerivativeIndex = OptionalInt.empty();
        ++i;
      ;
      } else {
        ++i;
      ;
        constituentDerivativeIndex = OptionalInt.of(getInt16LE(_data, i));
        i += 2;
      }
      final var constituentDerivativeDepegThreshold = getInt64LE(_data, i);
      i += 8;
      final var derivativeWeight = getInt64LE(_data, i);
      i += 8;
      final var volatility = getInt64LE(_data, i);
      i += 8;
      final var gammaExecution = _data[i] & 0xFF;
      ++i;
      final var gammaInventory = _data[i] & 0xFF;
      ++i;
      final var xi = _data[i] & 0xFF;
      ++i;
      final var newConstituentCorrelations = Borsh.readlongVector(_data, i);
      return new InitializeConstituentIxData(discriminator,
                                             spotMarketIndex,
                                             decimals,
                                             maxWeightDeviation,
                                             swapFeeMin,
                                             swapFeeMax,
                                             maxBorrowTokenAmount,
                                             oracleStalenessThreshold,
                                             costToTrade,
                                             constituentDerivativeIndex,
                                             constituentDerivativeDepegThreshold,
                                             derivativeWeight,
                                             volatility,
                                             gammaExecution,
                                             gammaInventory,
                                             xi,
                                             newConstituentCorrelations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, spotMarketIndex);
      i += 2;
      _data[i] = (byte) decimals;
      ++i;
      putInt64LE(_data, i, maxWeightDeviation);
      i += 8;
      putInt64LE(_data, i, swapFeeMin);
      i += 8;
      putInt64LE(_data, i, swapFeeMax);
      i += 8;
      putInt64LE(_data, i, maxBorrowTokenAmount);
      i += 8;
      putInt64LE(_data, i, oracleStalenessThreshold);
      i += 8;
      putInt32LE(_data, i, costToTrade);
      i += 4;
      i += Borsh.writeOptionalshort(constituentDerivativeIndex, _data, i);
      putInt64LE(_data, i, constituentDerivativeDepegThreshold);
      i += 8;
      putInt64LE(_data, i, derivativeWeight);
      i += 8;
      putInt64LE(_data, i, volatility);
      i += 8;
      _data[i] = (byte) gammaExecution;
      ++i;
      _data[i] = (byte) gammaInventory;
      ++i;
      _data[i] = (byte) xi;
      ++i;
      i += Borsh.writeVector(newConstituentCorrelations, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2
           + 1
           + 8
           + 8
           + 8
           + 8
           + 8
           + 4
           + (constituentDerivativeIndex == null || constituentDerivativeIndex.isEmpty() ? 1 : (1 + 2))
           + 8
           + 8
           + 8
           + 1
           + 1
           + 1
           + Borsh.lenVector(newConstituentCorrelations);
    }
  }

  public static final Discriminator UPDATE_CONSTITUENT_STATUS_DISCRIMINATOR = toDiscriminator(76, 159, 211, 239, 182, 214, 6, 15);

  public static List<AccountMeta> updateConstituentStatusKeys(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final PublicKey constituentKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(stateKey),
      createWrite(constituentKey)
    );
  }

  public static Instruction updateConstituentStatus(final AccountMeta invokedDriftProgramMeta,
                                                    final PublicKey adminKey,
                                                    final PublicKey stateKey,
                                                    final PublicKey constituentKey,
                                                    final int newStatus) {
    final var keys = updateConstituentStatusKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      constituentKey
    );
    return updateConstituentStatus(invokedDriftProgramMeta, keys, newStatus);
  }

  public static Instruction updateConstituentStatus(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final List<AccountMeta> keys,
                                                    final int newStatus) {
    final byte[] _data = new byte[9];
    int i = UPDATE_CONSTITUENT_STATUS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) newStatus;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateConstituentStatusIxData(Discriminator discriminator, int newStatus) implements Borsh {  

    public static UpdateConstituentStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateConstituentStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var newStatus = _data[i] & 0xFF;
      return new UpdateConstituentStatusIxData(discriminator, newStatus);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) newStatus;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_CONSTITUENT_PAUSED_OPERATIONS_DISCRIMINATOR = toDiscriminator(185, 122, 153, 191, 131, 177, 132, 208);

  public static List<AccountMeta> updateConstituentPausedOperationsKeys(final AccountMeta invokedDriftProgramMeta                                                                        ,
                                                                        final PublicKey adminKey,
                                                                        final PublicKey stateKey,
                                                                        final PublicKey constituentKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(stateKey),
      createWrite(constituentKey)
    );
  }

  public static Instruction updateConstituentPausedOperations(final AccountMeta invokedDriftProgramMeta,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final PublicKey constituentKey,
                                                              final int pausedOperations) {
    final var keys = updateConstituentPausedOperationsKeys(
      invokedDriftProgramMeta,
      adminKey,
      stateKey,
      constituentKey
    );
    return updateConstituentPausedOperations(invokedDriftProgramMeta, keys, pausedOperations);
  }

  public static Instruction updateConstituentPausedOperations(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final List<AccountMeta> keys,
                                                              final int pausedOperations) {
    final byte[] _data = new byte[9];
    int i = UPDATE_CONSTITUENT_PAUSED_OPERATIONS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) pausedOperations;

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateConstituentPausedOperationsIxData(Discriminator discriminator, int pausedOperations) implements Borsh {  

    public static UpdateConstituentPausedOperationsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static UpdateConstituentPausedOperationsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var pausedOperations = _data[i] & 0xFF;
      return new UpdateConstituentPausedOperationsIxData(discriminator, pausedOperations);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) pausedOperations;
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_CONSTITUENT_PARAMS_DISCRIMINATOR = toDiscriminator(238, 130, 122, 31, 12, 104, 192, 122);

  public static List<AccountMeta> updateConstituentParamsKeys(final AccountMeta invokedDriftProgramMeta                                                              ,
                                                              final PublicKey lpPoolKey,
                                                              final PublicKey constituentTargetBaseKey,
                                                              final PublicKey adminKey,
                                                              final PublicKey stateKey,
                                                              final PublicKey constituentKey) {
    return List.of(
      createRead(lpPoolKey),
      createWrite(constituentTargetBaseKey),
      createWritableSigner(adminKey),
      createRead(stateKey),
      createWrite(constituentKey)
    );
  }

  public static Instruction updateConstituentParams(final AccountMeta invokedDriftProgramMeta,
                                                    final PublicKey lpPoolKey,
                                                    final PublicKey constituentTargetBaseKey,
                                                    final PublicKey adminKey,
                                                    final PublicKey stateKey,
                                                    final PublicKey constituentKey,
                                                    final ConstituentParams constituentParams) {
    final var keys = updateConstituentParamsKeys(
      invokedDriftProgramMeta,
      lpPoolKey,
      constituentTargetBaseKey,
      adminKey,
      stateKey,
      constituentKey
    );
    return updateConstituentParams(invokedDriftProgramMeta, keys, constituentParams);
  }

  public static Instruction updateConstituentParams(final AccountMeta invokedDriftProgramMeta                                                    ,
                                                    final List<AccountMeta> keys,
                                                    final ConstituentParams constituentParams) {
    final byte[] _data = new byte[8 + Borsh.len(constituentParams)];
    int i = UPDATE_CONSTITUENT_PARAMS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(constituentParams, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateConstituentParamsIxData(Discriminator discriminator, ConstituentParams constituentParams) implements Borsh {  

    public static UpdateConstituentParamsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateConstituentParamsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var constituentParams = ConstituentParams.read(_data, i);
      return new UpdateConstituentParamsIxData(discriminator, constituentParams);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(constituentParams, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(constituentParams);
    }
  }

  public static final Discriminator UPDATE_LP_POOL_PARAMS_DISCRIMINATOR = toDiscriminator(217, 92, 2, 255, 27, 167, 178, 81);

  public static List<AccountMeta> updateLpPoolParamsKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey lpPoolKey,
                                                         final PublicKey adminKey,
                                                         final PublicKey stateKey) {
    return List.of(
      createWrite(lpPoolKey),
      createWritableSigner(adminKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateLpPoolParams(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey lpPoolKey,
                                               final PublicKey adminKey,
                                               final PublicKey stateKey,
                                               final LpPoolParams lpPoolParams) {
    final var keys = updateLpPoolParamsKeys(
      invokedDriftProgramMeta,
      lpPoolKey,
      adminKey,
      stateKey
    );
    return updateLpPoolParams(invokedDriftProgramMeta, keys, lpPoolParams);
  }

  public static Instruction updateLpPoolParams(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final LpPoolParams lpPoolParams) {
    final byte[] _data = new byte[8 + Borsh.len(lpPoolParams)];
    int i = UPDATE_LP_POOL_PARAMS_DISCRIMINATOR.write(_data, 0);
    Borsh.write(lpPoolParams, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateLpPoolParamsIxData(Discriminator discriminator, LpPoolParams lpPoolParams) implements Borsh {  

    public static UpdateLpPoolParamsIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateLpPoolParamsIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var lpPoolParams = LpPoolParams.read(_data, i);
      return new UpdateLpPoolParamsIxData(discriminator, lpPoolParams);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.write(lpPoolParams, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.len(lpPoolParams);
    }
  }

  public static final Discriminator ADD_AMM_CONSTITUENT_MAPPING_DATA_DISCRIMINATOR = toDiscriminator(164, 236, 130, 40, 118, 179, 46, 235);

  public static List<AccountMeta> addAmmConstituentMappingDataKeys(final AccountMeta invokedDriftProgramMeta                                                                   ,
                                                                   final PublicKey adminKey,
                                                                   final PublicKey lpPoolKey,
                                                                   final PublicKey ammConstituentMappingKey,
                                                                   final PublicKey constituentTargetBaseKey,
                                                                   final PublicKey stateKey,
                                                                   final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(lpPoolKey),
      createWrite(ammConstituentMappingKey),
      createWrite(constituentTargetBaseKey),
      createRead(stateKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction addAmmConstituentMappingData(final AccountMeta invokedDriftProgramMeta,
                                                         final PublicKey adminKey,
                                                         final PublicKey lpPoolKey,
                                                         final PublicKey ammConstituentMappingKey,
                                                         final PublicKey constituentTargetBaseKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey systemProgramKey,
                                                         final AddAmmConstituentMappingDatum[] ammConstituentMappingData) {
    final var keys = addAmmConstituentMappingDataKeys(
      invokedDriftProgramMeta,
      adminKey,
      lpPoolKey,
      ammConstituentMappingKey,
      constituentTargetBaseKey,
      stateKey,
      systemProgramKey
    );
    return addAmmConstituentMappingData(invokedDriftProgramMeta, keys, ammConstituentMappingData);
  }

  public static Instruction addAmmConstituentMappingData(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final List<AccountMeta> keys,
                                                         final AddAmmConstituentMappingDatum[] ammConstituentMappingData) {
    final byte[] _data = new byte[8 + Borsh.lenVector(ammConstituentMappingData)];
    int i = ADD_AMM_CONSTITUENT_MAPPING_DATA_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(ammConstituentMappingData, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record AddAmmConstituentMappingDataIxData(Discriminator discriminator, AddAmmConstituentMappingDatum[] ammConstituentMappingData) implements Borsh {  

    public static AddAmmConstituentMappingDataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static AddAmmConstituentMappingDataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ammConstituentMappingData = Borsh.readVector(AddAmmConstituentMappingDatum.class, AddAmmConstituentMappingDatum::read, _data, i);
      return new AddAmmConstituentMappingDataIxData(discriminator, ammConstituentMappingData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(ammConstituentMappingData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(ammConstituentMappingData);
    }
  }

  public static final Discriminator UPDATE_AMM_CONSTITUENT_MAPPING_DATA_DISCRIMINATOR = toDiscriminator(84, 70, 33, 167, 133, 107, 59, 24);

  public static List<AccountMeta> updateAmmConstituentMappingDataKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey lpPoolKey,
                                                                      final PublicKey ammConstituentMappingKey,
                                                                      final PublicKey systemProgramKey,
                                                                      final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(lpPoolKey),
      createWrite(ammConstituentMappingKey),
      createRead(systemProgramKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateAmmConstituentMappingData(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey lpPoolKey,
                                                            final PublicKey ammConstituentMappingKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey stateKey,
                                                            final AddAmmConstituentMappingDatum[] ammConstituentMappingData) {
    final var keys = updateAmmConstituentMappingDataKeys(
      invokedDriftProgramMeta,
      adminKey,
      lpPoolKey,
      ammConstituentMappingKey,
      systemProgramKey,
      stateKey
    );
    return updateAmmConstituentMappingData(invokedDriftProgramMeta, keys, ammConstituentMappingData);
  }

  public static Instruction updateAmmConstituentMappingData(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final AddAmmConstituentMappingDatum[] ammConstituentMappingData) {
    final byte[] _data = new byte[8 + Borsh.lenVector(ammConstituentMappingData)];
    int i = UPDATE_AMM_CONSTITUENT_MAPPING_DATA_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(ammConstituentMappingData, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateAmmConstituentMappingDataIxData(Discriminator discriminator, AddAmmConstituentMappingDatum[] ammConstituentMappingData) implements Borsh {  

    public static UpdateAmmConstituentMappingDataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateAmmConstituentMappingDataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var ammConstituentMappingData = Borsh.readVector(AddAmmConstituentMappingDatum.class, AddAmmConstituentMappingDatum::read, _data, i);
      return new UpdateAmmConstituentMappingDataIxData(discriminator, ammConstituentMappingData);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(ammConstituentMappingData, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(ammConstituentMappingData);
    }
  }

  public static final Discriminator REMOVE_AMM_CONSTITUENT_MAPPING_DATA_DISCRIMINATOR = toDiscriminator(20, 183, 211, 162, 16, 52, 229, 115);

  public static List<AccountMeta> removeAmmConstituentMappingDataKeys(final AccountMeta invokedDriftProgramMeta                                                                      ,
                                                                      final PublicKey adminKey,
                                                                      final PublicKey lpPoolKey,
                                                                      final PublicKey ammConstituentMappingKey,
                                                                      final PublicKey systemProgramKey,
                                                                      final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(lpPoolKey),
      createWrite(ammConstituentMappingKey),
      createRead(systemProgramKey),
      createRead(stateKey)
    );
  }

  public static Instruction removeAmmConstituentMappingData(final AccountMeta invokedDriftProgramMeta,
                                                            final PublicKey adminKey,
                                                            final PublicKey lpPoolKey,
                                                            final PublicKey ammConstituentMappingKey,
                                                            final PublicKey systemProgramKey,
                                                            final PublicKey stateKey,
                                                            final int perpMarketIndex,
                                                            final int constituentIndex) {
    final var keys = removeAmmConstituentMappingDataKeys(
      invokedDriftProgramMeta,
      adminKey,
      lpPoolKey,
      ammConstituentMappingKey,
      systemProgramKey,
      stateKey
    );
    return removeAmmConstituentMappingData(invokedDriftProgramMeta, keys, perpMarketIndex, constituentIndex);
  }

  public static Instruction removeAmmConstituentMappingData(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final List<AccountMeta> keys,
                                                            final int perpMarketIndex,
                                                            final int constituentIndex) {
    final byte[] _data = new byte[12];
    int i = REMOVE_AMM_CONSTITUENT_MAPPING_DATA_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, perpMarketIndex);
    i += 2;
    putInt16LE(_data, i, constituentIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record RemoveAmmConstituentMappingDataIxData(Discriminator discriminator, int perpMarketIndex, int constituentIndex) implements Borsh {  

    public static RemoveAmmConstituentMappingDataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static RemoveAmmConstituentMappingDataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var perpMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var constituentIndex = getInt16LE(_data, i);
      return new RemoveAmmConstituentMappingDataIxData(discriminator, perpMarketIndex, constituentIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, perpMarketIndex);
      i += 2;
      putInt16LE(_data, i, constituentIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_CONSTITUENT_CORRELATION_DATA_DISCRIMINATOR = toDiscriminator(79, 14, 19, 73, 221, 106, 62, 109);

  public static List<AccountMeta> updateConstituentCorrelationDataKeys(final AccountMeta invokedDriftProgramMeta                                                                       ,
                                                                       final PublicKey adminKey,
                                                                       final PublicKey lpPoolKey,
                                                                       final PublicKey constituentCorrelationsKey,
                                                                       final PublicKey stateKey) {
    return List.of(
      createWritableSigner(adminKey),
      createRead(lpPoolKey),
      createWrite(constituentCorrelationsKey),
      createRead(stateKey)
    );
  }

  public static Instruction updateConstituentCorrelationData(final AccountMeta invokedDriftProgramMeta,
                                                             final PublicKey adminKey,
                                                             final PublicKey lpPoolKey,
                                                             final PublicKey constituentCorrelationsKey,
                                                             final PublicKey stateKey,
                                                             final int index1,
                                                             final int index2,
                                                             final long correlation) {
    final var keys = updateConstituentCorrelationDataKeys(
      invokedDriftProgramMeta,
      adminKey,
      lpPoolKey,
      constituentCorrelationsKey,
      stateKey
    );
    return updateConstituentCorrelationData(
      invokedDriftProgramMeta,
      keys,
      index1,
      index2,
      correlation
    );
  }

  public static Instruction updateConstituentCorrelationData(final AccountMeta invokedDriftProgramMeta                                                             ,
                                                             final List<AccountMeta> keys,
                                                             final int index1,
                                                             final int index2,
                                                             final long correlation) {
    final byte[] _data = new byte[20];
    int i = UPDATE_CONSTITUENT_CORRELATION_DATA_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, index1);
    i += 2;
    putInt16LE(_data, i, index2);
    i += 2;
    putInt64LE(_data, i, correlation);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record UpdateConstituentCorrelationDataIxData(Discriminator discriminator,
                                                       int index1,
                                                       int index2,
                                                       long correlation) implements Borsh {  

    public static UpdateConstituentCorrelationDataIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 20;

    public static UpdateConstituentCorrelationDataIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var index1 = getInt16LE(_data, i);
      i += 2;
      final var index2 = getInt16LE(_data, i);
      i += 2;
      final var correlation = getInt64LE(_data, i);
      return new UpdateConstituentCorrelationDataIxData(discriminator, index1, index2, correlation);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, index1);
      i += 2;
      putInt16LE(_data, i, index2);
      i += 2;
      putInt64LE(_data, i, correlation);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_LP_CONSTITUENT_TARGET_BASE_DISCRIMINATOR = toDiscriminator(157, 65, 50, 207, 59, 236, 161, 110);

  public static List<AccountMeta> updateLpConstituentTargetBaseKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey keeperKey,
                                                                    final PublicKey ammConstituentMappingKey,
                                                                    final PublicKey constituentTargetBaseKey,
                                                                    final PublicKey ammCacheKey,
                                                                    final PublicKey lpPoolKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(keeperKey),
      createRead(ammConstituentMappingKey),
      createWrite(constituentTargetBaseKey),
      createRead(ammCacheKey),
      createRead(lpPoolKey)
    );
  }

  public static Instruction updateLpConstituentTargetBase(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey stateKey,
                                                          final PublicKey keeperKey,
                                                          final PublicKey ammConstituentMappingKey,
                                                          final PublicKey constituentTargetBaseKey,
                                                          final PublicKey ammCacheKey,
                                                          final PublicKey lpPoolKey) {
    final var keys = updateLpConstituentTargetBaseKeys(
      invokedDriftProgramMeta,
      stateKey,
      keeperKey,
      ammConstituentMappingKey,
      constituentTargetBaseKey,
      ammCacheKey,
      lpPoolKey
    );
    return updateLpConstituentTargetBase(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateLpConstituentTargetBase(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_LP_CONSTITUENT_TARGET_BASE_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_LP_POOL_AUM_DISCRIMINATOR = toDiscriminator(88, 113, 137, 206, 246, 247, 171, 142);

  public static List<AccountMeta> updateLpPoolAumKeys(final AccountMeta invokedDriftProgramMeta                                                      ,
                                                      final PublicKey stateKey,
                                                      final PublicKey keeperKey,
                                                      final PublicKey lpPoolKey,
                                                      final PublicKey constituentTargetBaseKey,
                                                      final PublicKey ammCacheKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(keeperKey),
      createWrite(lpPoolKey),
      createWrite(constituentTargetBaseKey),
      createWrite(ammCacheKey)
    );
  }

  public static Instruction updateLpPoolAum(final AccountMeta invokedDriftProgramMeta,
                                            final PublicKey stateKey,
                                            final PublicKey keeperKey,
                                            final PublicKey lpPoolKey,
                                            final PublicKey constituentTargetBaseKey,
                                            final PublicKey ammCacheKey) {
    final var keys = updateLpPoolAumKeys(
      invokedDriftProgramMeta,
      stateKey,
      keeperKey,
      lpPoolKey,
      constituentTargetBaseKey,
      ammCacheKey
    );
    return updateLpPoolAum(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateLpPoolAum(final AccountMeta invokedDriftProgramMeta                                            ,
                                            final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_LP_POOL_AUM_DISCRIMINATOR);
  }

  public static final Discriminator UPDATE_AMM_CACHE_DISCRIMINATOR = toDiscriminator(88, 4, 63, 94, 83, 224, 255, 130);

  public static List<AccountMeta> updateAmmCacheKeys(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final PublicKey keeperKey,
                                                     final PublicKey stateKey,
                                                     final PublicKey ammCacheKey,
                                                     final PublicKey quoteMarketKey) {
    return List.of(
      createWritableSigner(keeperKey),
      createRead(stateKey),
      createWrite(ammCacheKey),
      createRead(quoteMarketKey)
    );
  }

  public static Instruction updateAmmCache(final AccountMeta invokedDriftProgramMeta,
                                           final PublicKey keeperKey,
                                           final PublicKey stateKey,
                                           final PublicKey ammCacheKey,
                                           final PublicKey quoteMarketKey) {
    final var keys = updateAmmCacheKeys(
      invokedDriftProgramMeta,
      keeperKey,
      stateKey,
      ammCacheKey,
      quoteMarketKey
    );
    return updateAmmCache(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateAmmCache(final AccountMeta invokedDriftProgramMeta                                           ,
                                           final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_AMM_CACHE_DISCRIMINATOR);
  }

  public static final Discriminator OVERRIDE_AMM_CACHE_INFO_DISCRIMINATOR = toDiscriminator(189, 198, 128, 9, 49, 145, 201, 115);

  public static List<AccountMeta> overrideAmmCacheInfoKeys(final AccountMeta invokedDriftProgramMeta                                                           ,
                                                           final PublicKey stateKey,
                                                           final PublicKey adminKey,
                                                           final PublicKey ammCacheKey) {
    return List.of(
      createWrite(stateKey),
      createReadOnlySigner(adminKey),
      createWrite(ammCacheKey)
    );
  }

  public static Instruction overrideAmmCacheInfo(final AccountMeta invokedDriftProgramMeta,
                                                 final PublicKey stateKey,
                                                 final PublicKey adminKey,
                                                 final PublicKey ammCacheKey,
                                                 final int marketIndex,
                                                 final OverrideAmmCacheParams overrideParams) {
    final var keys = overrideAmmCacheInfoKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      ammCacheKey
    );
    return overrideAmmCacheInfo(invokedDriftProgramMeta, keys, marketIndex, overrideParams);
  }

  public static Instruction overrideAmmCacheInfo(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final List<AccountMeta> keys,
                                                 final int marketIndex,
                                                 final OverrideAmmCacheParams overrideParams) {
    final byte[] _data = new byte[10 + Borsh.len(overrideParams)];
    int i = OVERRIDE_AMM_CACHE_INFO_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, marketIndex);
    i += 2;
    Borsh.write(overrideParams, _data, i);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record OverrideAmmCacheInfoIxData(Discriminator discriminator, int marketIndex, OverrideAmmCacheParams overrideParams) implements Borsh {  

    public static OverrideAmmCacheInfoIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static OverrideAmmCacheInfoIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var marketIndex = getInt16LE(_data, i);
      i += 2;
      final var overrideParams = OverrideAmmCacheParams.read(_data, i);
      return new OverrideAmmCacheInfoIxData(discriminator, marketIndex, overrideParams);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, marketIndex);
      i += 2;
      i += Borsh.write(overrideParams, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 2 + Borsh.len(overrideParams);
    }
  }

  public static final Discriminator LP_POOL_SWAP_DISCRIMINATOR = toDiscriminator(36, 161, 39, 49, 227, 1, 35, 226);

  public static List<AccountMeta> lpPoolSwapKeys(final AccountMeta invokedDriftProgramMeta                                                 ,
                                                 final PublicKey stateKey,
                                                 final PublicKey lpPoolKey,
                                                 final PublicKey constituentTargetBaseKey,
                                                 final PublicKey constituentCorrelationsKey,
                                                 final PublicKey constituentInTokenAccountKey,
                                                 final PublicKey constituentOutTokenAccountKey,
                                                 final PublicKey userInTokenAccountKey,
                                                 final PublicKey userOutTokenAccountKey,
                                                 final PublicKey inConstituentKey,
                                                 final PublicKey outConstituentKey,
                                                 final PublicKey inMarketMintKey,
                                                 final PublicKey outMarketMintKey,
                                                 final PublicKey authorityKey,
                                                 final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createRead(lpPoolKey),
      createRead(constituentTargetBaseKey),
      createRead(constituentCorrelationsKey),
      createWrite(constituentInTokenAccountKey),
      createWrite(constituentOutTokenAccountKey),
      createWrite(userInTokenAccountKey),
      createWrite(userOutTokenAccountKey),
      createWrite(inConstituentKey),
      createWrite(outConstituentKey),
      createRead(inMarketMintKey),
      createRead(outMarketMintKey),
      createReadOnlySigner(authorityKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction lpPoolSwap(final AccountMeta invokedDriftProgramMeta,
                                       final PublicKey stateKey,
                                       final PublicKey lpPoolKey,
                                       final PublicKey constituentTargetBaseKey,
                                       final PublicKey constituentCorrelationsKey,
                                       final PublicKey constituentInTokenAccountKey,
                                       final PublicKey constituentOutTokenAccountKey,
                                       final PublicKey userInTokenAccountKey,
                                       final PublicKey userOutTokenAccountKey,
                                       final PublicKey inConstituentKey,
                                       final PublicKey outConstituentKey,
                                       final PublicKey inMarketMintKey,
                                       final PublicKey outMarketMintKey,
                                       final PublicKey authorityKey,
                                       final PublicKey tokenProgramKey,
                                       final int inMarketIndex,
                                       final int outMarketIndex,
                                       final long inAmount,
                                       final long minOutAmount) {
    final var keys = lpPoolSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      lpPoolKey,
      constituentTargetBaseKey,
      constituentCorrelationsKey,
      constituentInTokenAccountKey,
      constituentOutTokenAccountKey,
      userInTokenAccountKey,
      userOutTokenAccountKey,
      inConstituentKey,
      outConstituentKey,
      inMarketMintKey,
      outMarketMintKey,
      authorityKey,
      tokenProgramKey
    );
    return lpPoolSwap(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      outMarketIndex,
      inAmount,
      minOutAmount
    );
  }

  public static Instruction lpPoolSwap(final AccountMeta invokedDriftProgramMeta                                       ,
                                       final List<AccountMeta> keys,
                                       final int inMarketIndex,
                                       final int outMarketIndex,
                                       final long inAmount,
                                       final long minOutAmount) {
    final byte[] _data = new byte[28];
    int i = LP_POOL_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    putInt64LE(_data, i, inAmount);
    i += 8;
    putInt64LE(_data, i, minOutAmount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LpPoolSwapIxData(Discriminator discriminator,
                                 int inMarketIndex,
                                 int outMarketIndex,
                                 long inAmount,
                                 long minOutAmount) implements Borsh {  

    public static LpPoolSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 28;

    public static LpPoolSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var inAmount = getInt64LE(_data, i);
      i += 8;
      final var minOutAmount = getInt64LE(_data, i);
      return new LpPoolSwapIxData(discriminator,
                                  inMarketIndex,
                                  outMarketIndex,
                                  inAmount,
                                  minOutAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      putInt64LE(_data, i, inAmount);
      i += 8;
      putInt64LE(_data, i, minOutAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator VIEW_LP_POOL_SWAP_FEES_DISCRIMINATOR = toDiscriminator(126, 189, 109, 189, 170, 156, 3, 46);

  public static List<AccountMeta> viewLpPoolSwapFeesKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey driftSignerKey,
                                                         final PublicKey stateKey,
                                                         final PublicKey lpPoolKey,
                                                         final PublicKey constituentTargetBaseKey,
                                                         final PublicKey constituentCorrelationsKey,
                                                         final PublicKey constituentInTokenAccountKey,
                                                         final PublicKey constituentOutTokenAccountKey,
                                                         final PublicKey inConstituentKey,
                                                         final PublicKey outConstituentKey,
                                                         final PublicKey authorityKey,
                                                         final PublicKey tokenProgramKey) {
    return List.of(
      createRead(driftSignerKey),
      createRead(stateKey),
      createRead(lpPoolKey),
      createRead(constituentTargetBaseKey),
      createRead(constituentCorrelationsKey),
      createWrite(constituentInTokenAccountKey),
      createWrite(constituentOutTokenAccountKey),
      createWrite(inConstituentKey),
      createWrite(outConstituentKey),
      createReadOnlySigner(authorityKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction viewLpPoolSwapFees(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey driftSignerKey,
                                               final PublicKey stateKey,
                                               final PublicKey lpPoolKey,
                                               final PublicKey constituentTargetBaseKey,
                                               final PublicKey constituentCorrelationsKey,
                                               final PublicKey constituentInTokenAccountKey,
                                               final PublicKey constituentOutTokenAccountKey,
                                               final PublicKey inConstituentKey,
                                               final PublicKey outConstituentKey,
                                               final PublicKey authorityKey,
                                               final PublicKey tokenProgramKey,
                                               final int inMarketIndex,
                                               final int outMarketIndex,
                                               final long inAmount,
                                               final long inTargetWeight,
                                               final long outTargetWeight) {
    final var keys = viewLpPoolSwapFeesKeys(
      invokedDriftProgramMeta,
      driftSignerKey,
      stateKey,
      lpPoolKey,
      constituentTargetBaseKey,
      constituentCorrelationsKey,
      constituentInTokenAccountKey,
      constituentOutTokenAccountKey,
      inConstituentKey,
      outConstituentKey,
      authorityKey,
      tokenProgramKey
    );
    return viewLpPoolSwapFees(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      outMarketIndex,
      inAmount,
      inTargetWeight,
      outTargetWeight
    );
  }

  public static Instruction viewLpPoolSwapFees(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final int inMarketIndex,
                                               final int outMarketIndex,
                                               final long inAmount,
                                               final long inTargetWeight,
                                               final long outTargetWeight) {
    final byte[] _data = new byte[36];
    int i = VIEW_LP_POOL_SWAP_FEES_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    putInt64LE(_data, i, inAmount);
    i += 8;
    putInt64LE(_data, i, inTargetWeight);
    i += 8;
    putInt64LE(_data, i, outTargetWeight);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ViewLpPoolSwapFeesIxData(Discriminator discriminator,
                                         int inMarketIndex,
                                         int outMarketIndex,
                                         long inAmount,
                                         long inTargetWeight,
                                         long outTargetWeight) implements Borsh {  

    public static ViewLpPoolSwapFeesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 36;

    public static ViewLpPoolSwapFeesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var inAmount = getInt64LE(_data, i);
      i += 8;
      final var inTargetWeight = getInt64LE(_data, i);
      i += 8;
      final var outTargetWeight = getInt64LE(_data, i);
      return new ViewLpPoolSwapFeesIxData(discriminator,
                                          inMarketIndex,
                                          outMarketIndex,
                                          inAmount,
                                          inTargetWeight,
                                          outTargetWeight);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      putInt64LE(_data, i, inAmount);
      i += 8;
      putInt64LE(_data, i, inTargetWeight);
      i += 8;
      putInt64LE(_data, i, outTargetWeight);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LP_POOL_ADD_LIQUIDITY_DISCRIMINATOR = toDiscriminator(49, 135, 246, 103, 93, 146, 220, 141);

  public static List<AccountMeta> lpPoolAddLiquidityKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey stateKey,
                                                         final PublicKey lpPoolKey,
                                                         final PublicKey authorityKey,
                                                         final PublicKey inMarketMintKey,
                                                         final PublicKey inConstituentKey,
                                                         final PublicKey userInTokenAccountKey,
                                                         final PublicKey constituentInTokenAccountKey,
                                                         final PublicKey userLpTokenAccountKey,
                                                         final PublicKey lpMintKey,
                                                         final PublicKey constituentTargetBaseKey,
                                                         final PublicKey lpPoolTokenVaultKey,
                                                         final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWrite(lpPoolKey),
      createReadOnlySigner(authorityKey),
      createRead(inMarketMintKey),
      createWrite(inConstituentKey),
      createWrite(userInTokenAccountKey),
      createWrite(constituentInTokenAccountKey),
      createWrite(userLpTokenAccountKey),
      createWrite(lpMintKey),
      createRead(constituentTargetBaseKey),
      createWrite(lpPoolTokenVaultKey),
      createRead(tokenProgramKey)
    );
  }

  public static Instruction lpPoolAddLiquidity(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey stateKey,
                                               final PublicKey lpPoolKey,
                                               final PublicKey authorityKey,
                                               final PublicKey inMarketMintKey,
                                               final PublicKey inConstituentKey,
                                               final PublicKey userInTokenAccountKey,
                                               final PublicKey constituentInTokenAccountKey,
                                               final PublicKey userLpTokenAccountKey,
                                               final PublicKey lpMintKey,
                                               final PublicKey constituentTargetBaseKey,
                                               final PublicKey lpPoolTokenVaultKey,
                                               final PublicKey tokenProgramKey,
                                               final int inMarketIndex,
                                               final BigInteger inAmount,
                                               final long minMintAmount) {
    final var keys = lpPoolAddLiquidityKeys(
      invokedDriftProgramMeta,
      stateKey,
      lpPoolKey,
      authorityKey,
      inMarketMintKey,
      inConstituentKey,
      userInTokenAccountKey,
      constituentInTokenAccountKey,
      userLpTokenAccountKey,
      lpMintKey,
      constituentTargetBaseKey,
      lpPoolTokenVaultKey,
      tokenProgramKey
    );
    return lpPoolAddLiquidity(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      inAmount,
      minMintAmount
    );
  }

  public static Instruction lpPoolAddLiquidity(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final int inMarketIndex,
                                               final BigInteger inAmount,
                                               final long minMintAmount) {
    final byte[] _data = new byte[34];
    int i = LP_POOL_ADD_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt128LE(_data, i, inAmount);
    i += 16;
    putInt64LE(_data, i, minMintAmount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LpPoolAddLiquidityIxData(Discriminator discriminator,
                                         int inMarketIndex,
                                         BigInteger inAmount,
                                         long minMintAmount) implements Borsh {  

    public static LpPoolAddLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static LpPoolAddLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var inAmount = getInt128LE(_data, i);
      i += 16;
      final var minMintAmount = getInt64LE(_data, i);
      return new LpPoolAddLiquidityIxData(discriminator, inMarketIndex, inAmount, minMintAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt128LE(_data, i, inAmount);
      i += 16;
      putInt64LE(_data, i, minMintAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator LP_POOL_REMOVE_LIQUIDITY_DISCRIMINATOR = toDiscriminator(164, 36, 193, 252, 196, 157, 138, 43);

  public static List<AccountMeta> lpPoolRemoveLiquidityKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey driftSignerKey,
                                                            final PublicKey lpPoolKey,
                                                            final PublicKey authorityKey,
                                                            final PublicKey outMarketMintKey,
                                                            final PublicKey outConstituentKey,
                                                            final PublicKey userOutTokenAccountKey,
                                                            final PublicKey constituentOutTokenAccountKey,
                                                            final PublicKey userLpTokenAccountKey,
                                                            final PublicKey spotMarketTokenAccountKey,
                                                            final PublicKey lpMintKey,
                                                            final PublicKey constituentTargetBaseKey,
                                                            final PublicKey lpPoolTokenVaultKey,
                                                            final PublicKey tokenProgramKey,
                                                            final PublicKey ammCacheKey) {
    return List.of(
      createRead(stateKey),
      createRead(driftSignerKey),
      createWrite(lpPoolKey),
      createReadOnlySigner(authorityKey),
      createRead(outMarketMintKey),
      createWrite(outConstituentKey),
      createWrite(userOutTokenAccountKey),
      createWrite(constituentOutTokenAccountKey),
      createWrite(userLpTokenAccountKey),
      createWrite(spotMarketTokenAccountKey),
      createWrite(lpMintKey),
      createRead(constituentTargetBaseKey),
      createWrite(lpPoolTokenVaultKey),
      createRead(tokenProgramKey),
      createRead(ammCacheKey)
    );
  }

  public static Instruction lpPoolRemoveLiquidity(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey driftSignerKey,
                                                  final PublicKey lpPoolKey,
                                                  final PublicKey authorityKey,
                                                  final PublicKey outMarketMintKey,
                                                  final PublicKey outConstituentKey,
                                                  final PublicKey userOutTokenAccountKey,
                                                  final PublicKey constituentOutTokenAccountKey,
                                                  final PublicKey userLpTokenAccountKey,
                                                  final PublicKey spotMarketTokenAccountKey,
                                                  final PublicKey lpMintKey,
                                                  final PublicKey constituentTargetBaseKey,
                                                  final PublicKey lpPoolTokenVaultKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey ammCacheKey,
                                                  final int inMarketIndex,
                                                  final long inAmount,
                                                  final BigInteger minOutAmount) {
    final var keys = lpPoolRemoveLiquidityKeys(
      invokedDriftProgramMeta,
      stateKey,
      driftSignerKey,
      lpPoolKey,
      authorityKey,
      outMarketMintKey,
      outConstituentKey,
      userOutTokenAccountKey,
      constituentOutTokenAccountKey,
      userLpTokenAccountKey,
      spotMarketTokenAccountKey,
      lpMintKey,
      constituentTargetBaseKey,
      lpPoolTokenVaultKey,
      tokenProgramKey,
      ammCacheKey
    );
    return lpPoolRemoveLiquidity(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      inAmount,
      minOutAmount
    );
  }

  public static Instruction lpPoolRemoveLiquidity(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final int inMarketIndex,
                                                  final long inAmount,
                                                  final BigInteger minOutAmount) {
    final byte[] _data = new byte[34];
    int i = LP_POOL_REMOVE_LIQUIDITY_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt64LE(_data, i, inAmount);
    i += 8;
    putInt128LE(_data, i, minOutAmount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record LpPoolRemoveLiquidityIxData(Discriminator discriminator,
                                            int inMarketIndex,
                                            long inAmount,
                                            BigInteger minOutAmount) implements Borsh {  

    public static LpPoolRemoveLiquidityIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 34;

    public static LpPoolRemoveLiquidityIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var inAmount = getInt64LE(_data, i);
      i += 8;
      final var minOutAmount = getInt128LE(_data, i);
      return new LpPoolRemoveLiquidityIxData(discriminator, inMarketIndex, inAmount, minOutAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt64LE(_data, i, inAmount);
      i += 8;
      putInt128LE(_data, i, minOutAmount);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator VIEW_LP_POOL_ADD_LIQUIDITY_FEES_DISCRIMINATOR = toDiscriminator(80, 66, 226, 161, 70, 142, 119, 84);

  public static List<AccountMeta> viewLpPoolAddLiquidityFeesKeys(final AccountMeta invokedDriftProgramMeta                                                                 ,
                                                                 final PublicKey stateKey,
                                                                 final PublicKey lpPoolKey,
                                                                 final PublicKey authorityKey,
                                                                 final PublicKey inMarketMintKey,
                                                                 final PublicKey inConstituentKey,
                                                                 final PublicKey lpMintKey,
                                                                 final PublicKey constituentTargetBaseKey) {
    return List.of(
      createRead(stateKey),
      createRead(lpPoolKey),
      createReadOnlySigner(authorityKey),
      createRead(inMarketMintKey),
      createRead(inConstituentKey),
      createRead(lpMintKey),
      createRead(constituentTargetBaseKey)
    );
  }

  public static Instruction viewLpPoolAddLiquidityFees(final AccountMeta invokedDriftProgramMeta,
                                                       final PublicKey stateKey,
                                                       final PublicKey lpPoolKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey inMarketMintKey,
                                                       final PublicKey inConstituentKey,
                                                       final PublicKey lpMintKey,
                                                       final PublicKey constituentTargetBaseKey,
                                                       final int inMarketIndex,
                                                       final BigInteger inAmount) {
    final var keys = viewLpPoolAddLiquidityFeesKeys(
      invokedDriftProgramMeta,
      stateKey,
      lpPoolKey,
      authorityKey,
      inMarketMintKey,
      inConstituentKey,
      lpMintKey,
      constituentTargetBaseKey
    );
    return viewLpPoolAddLiquidityFees(invokedDriftProgramMeta, keys, inMarketIndex, inAmount);
  }

  public static Instruction viewLpPoolAddLiquidityFees(final AccountMeta invokedDriftProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final int inMarketIndex,
                                                       final BigInteger inAmount) {
    final byte[] _data = new byte[26];
    int i = VIEW_LP_POOL_ADD_LIQUIDITY_FEES_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt128LE(_data, i, inAmount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ViewLpPoolAddLiquidityFeesIxData(Discriminator discriminator, int inMarketIndex, BigInteger inAmount) implements Borsh {  

    public static ViewLpPoolAddLiquidityFeesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 26;

    public static ViewLpPoolAddLiquidityFeesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var inAmount = getInt128LE(_data, i);
      return new ViewLpPoolAddLiquidityFeesIxData(discriminator, inMarketIndex, inAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt128LE(_data, i, inAmount);
      i += 16;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator VIEW_LP_POOL_REMOVE_LIQUIDITY_FEES_DISCRIMINATOR = toDiscriminator(47, 12, 9, 102, 12, 226, 197, 89);

  public static List<AccountMeta> viewLpPoolRemoveLiquidityFeesKeys(final AccountMeta invokedDriftProgramMeta                                                                    ,
                                                                    final PublicKey stateKey,
                                                                    final PublicKey lpPoolKey,
                                                                    final PublicKey authorityKey,
                                                                    final PublicKey outMarketMintKey,
                                                                    final PublicKey outConstituentKey,
                                                                    final PublicKey lpMintKey,
                                                                    final PublicKey constituentTargetBaseKey) {
    return List.of(
      createRead(stateKey),
      createRead(lpPoolKey),
      createReadOnlySigner(authorityKey),
      createRead(outMarketMintKey),
      createRead(outConstituentKey),
      createRead(lpMintKey),
      createRead(constituentTargetBaseKey)
    );
  }

  public static Instruction viewLpPoolRemoveLiquidityFees(final AccountMeta invokedDriftProgramMeta,
                                                          final PublicKey stateKey,
                                                          final PublicKey lpPoolKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey outMarketMintKey,
                                                          final PublicKey outConstituentKey,
                                                          final PublicKey lpMintKey,
                                                          final PublicKey constituentTargetBaseKey,
                                                          final int inMarketIndex,
                                                          final long inAmount) {
    final var keys = viewLpPoolRemoveLiquidityFeesKeys(
      invokedDriftProgramMeta,
      stateKey,
      lpPoolKey,
      authorityKey,
      outMarketMintKey,
      outConstituentKey,
      lpMintKey,
      constituentTargetBaseKey
    );
    return viewLpPoolRemoveLiquidityFees(invokedDriftProgramMeta, keys, inMarketIndex, inAmount);
  }

  public static Instruction viewLpPoolRemoveLiquidityFees(final AccountMeta invokedDriftProgramMeta                                                          ,
                                                          final List<AccountMeta> keys,
                                                          final int inMarketIndex,
                                                          final long inAmount) {
    final byte[] _data = new byte[18];
    int i = VIEW_LP_POOL_REMOVE_LIQUIDITY_FEES_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt64LE(_data, i, inAmount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record ViewLpPoolRemoveLiquidityFeesIxData(Discriminator discriminator, int inMarketIndex, long inAmount) implements Borsh {  

    public static ViewLpPoolRemoveLiquidityFeesIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 18;

    public static ViewLpPoolRemoveLiquidityFeesIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var inAmount = getInt64LE(_data, i);
      return new ViewLpPoolRemoveLiquidityFeesIxData(discriminator, inMarketIndex, inAmount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt64LE(_data, i, inAmount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator BEGIN_LP_SWAP_DISCRIMINATOR = toDiscriminator(64, 44, 24, 199, 48, 125, 67, 91);

  /// @param signerOutTokenAccountKey Signer token accounts
  /// @param constituentOutTokenAccountKey Constituent token accounts
  /// @param outConstituentKey Constituents
  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> beginLpSwapKeys(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final PublicKey stateKey,
                                                  final PublicKey adminKey,
                                                  final PublicKey signerOutTokenAccountKey,
                                                  final PublicKey signerInTokenAccountKey,
                                                  final PublicKey constituentOutTokenAccountKey,
                                                  final PublicKey constituentInTokenAccountKey,
                                                  final PublicKey outConstituentKey,
                                                  final PublicKey inConstituentKey,
                                                  final PublicKey lpPoolKey,
                                                  final PublicKey instructionsKey,
                                                  final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(adminKey),
      createWrite(signerOutTokenAccountKey),
      createWrite(signerInTokenAccountKey),
      createWrite(constituentOutTokenAccountKey),
      createWrite(constituentInTokenAccountKey),
      createWrite(outConstituentKey),
      createWrite(inConstituentKey),
      createRead(lpPoolKey),
      createRead(instructionsKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param signerOutTokenAccountKey Signer token accounts
  /// @param constituentOutTokenAccountKey Constituent token accounts
  /// @param outConstituentKey Constituents
  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction beginLpSwap(final AccountMeta invokedDriftProgramMeta,
                                        final PublicKey stateKey,
                                        final PublicKey adminKey,
                                        final PublicKey signerOutTokenAccountKey,
                                        final PublicKey signerInTokenAccountKey,
                                        final PublicKey constituentOutTokenAccountKey,
                                        final PublicKey constituentInTokenAccountKey,
                                        final PublicKey outConstituentKey,
                                        final PublicKey inConstituentKey,
                                        final PublicKey lpPoolKey,
                                        final PublicKey instructionsKey,
                                        final PublicKey tokenProgramKey,
                                        final int inMarketIndex,
                                        final int outMarketIndex,
                                        final long amountIn) {
    final var keys = beginLpSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      signerOutTokenAccountKey,
      signerInTokenAccountKey,
      constituentOutTokenAccountKey,
      constituentInTokenAccountKey,
      outConstituentKey,
      inConstituentKey,
      lpPoolKey,
      instructionsKey,
      tokenProgramKey
    );
    return beginLpSwap(
      invokedDriftProgramMeta,
      keys,
      inMarketIndex,
      outMarketIndex,
      amountIn
    );
  }

  public static Instruction beginLpSwap(final AccountMeta invokedDriftProgramMeta                                        ,
                                        final List<AccountMeta> keys,
                                        final int inMarketIndex,
                                        final int outMarketIndex,
                                        final long amountIn) {
    final byte[] _data = new byte[20];
    int i = BEGIN_LP_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);
    i += 2;
    putInt64LE(_data, i, amountIn);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record BeginLpSwapIxData(Discriminator discriminator,
                                  int inMarketIndex,
                                  int outMarketIndex,
                                  long amountIn) implements Borsh {  

    public static BeginLpSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 20;

    public static BeginLpSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var amountIn = getInt64LE(_data, i);
      return new BeginLpSwapIxData(discriminator, inMarketIndex, outMarketIndex, amountIn);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      putInt64LE(_data, i, amountIn);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator END_LP_SWAP_DISCRIMINATOR = toDiscriminator(99, 125, 214, 165, 129, 175, 253, 135);

  /// @param signerOutTokenAccountKey Signer token accounts
  /// @param constituentOutTokenAccountKey Constituent token accounts
  /// @param outConstituentKey Constituents
  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static List<AccountMeta> endLpSwapKeys(final AccountMeta invokedDriftProgramMeta                                                ,
                                                final PublicKey stateKey,
                                                final PublicKey adminKey,
                                                final PublicKey signerOutTokenAccountKey,
                                                final PublicKey signerInTokenAccountKey,
                                                final PublicKey constituentOutTokenAccountKey,
                                                final PublicKey constituentInTokenAccountKey,
                                                final PublicKey outConstituentKey,
                                                final PublicKey inConstituentKey,
                                                final PublicKey lpPoolKey,
                                                final PublicKey instructionsKey,
                                                final PublicKey tokenProgramKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(adminKey),
      createWrite(signerOutTokenAccountKey),
      createWrite(signerInTokenAccountKey),
      createWrite(constituentOutTokenAccountKey),
      createWrite(constituentInTokenAccountKey),
      createWrite(outConstituentKey),
      createWrite(inConstituentKey),
      createRead(lpPoolKey),
      createRead(instructionsKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param signerOutTokenAccountKey Signer token accounts
  /// @param constituentOutTokenAccountKey Constituent token accounts
  /// @param outConstituentKey Constituents
  /// @param instructionsKey Instructions Sysvar for instruction introspection
  public static Instruction endLpSwap(final AccountMeta invokedDriftProgramMeta,
                                      final PublicKey stateKey,
                                      final PublicKey adminKey,
                                      final PublicKey signerOutTokenAccountKey,
                                      final PublicKey signerInTokenAccountKey,
                                      final PublicKey constituentOutTokenAccountKey,
                                      final PublicKey constituentInTokenAccountKey,
                                      final PublicKey outConstituentKey,
                                      final PublicKey inConstituentKey,
                                      final PublicKey lpPoolKey,
                                      final PublicKey instructionsKey,
                                      final PublicKey tokenProgramKey,
                                      final int inMarketIndex,
                                      final int outMarketIndex) {
    final var keys = endLpSwapKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      signerOutTokenAccountKey,
      signerInTokenAccountKey,
      constituentOutTokenAccountKey,
      constituentInTokenAccountKey,
      outConstituentKey,
      inConstituentKey,
      lpPoolKey,
      instructionsKey,
      tokenProgramKey
    );
    return endLpSwap(invokedDriftProgramMeta, keys, inMarketIndex, outMarketIndex);
  }

  public static Instruction endLpSwap(final AccountMeta invokedDriftProgramMeta                                      ,
                                      final List<AccountMeta> keys,
                                      final int inMarketIndex,
                                      final int outMarketIndex) {
    final byte[] _data = new byte[12];
    int i = END_LP_SWAP_DISCRIMINATOR.write(_data, 0);
    putInt16LE(_data, i, inMarketIndex);
    i += 2;
    putInt16LE(_data, i, outMarketIndex);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record EndLpSwapIxData(Discriminator discriminator, int inMarketIndex, int outMarketIndex) implements Borsh {  

    public static EndLpSwapIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 12;

    public static EndLpSwapIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var inMarketIndex = getInt16LE(_data, i);
      i += 2;
      final var outMarketIndex = getInt16LE(_data, i);
      return new EndLpSwapIxData(discriminator, inMarketIndex, outMarketIndex);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt16LE(_data, i, inMarketIndex);
      i += 2;
      putInt16LE(_data, i, outMarketIndex);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator UPDATE_CONSTITUENT_ORACLE_INFO_DISCRIMINATOR = toDiscriminator(198, 117, 231, 250, 147, 33, 127, 161);

  public static List<AccountMeta> updateConstituentOracleInfoKeys(final AccountMeta invokedDriftProgramMeta                                                                  ,
                                                                  final PublicKey stateKey,
                                                                  final PublicKey keeperKey,
                                                                  final PublicKey constituentKey,
                                                                  final PublicKey spotMarketKey,
                                                                  final PublicKey oracleKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(keeperKey),
      createWrite(constituentKey),
      createRead(spotMarketKey),
      createRead(oracleKey)
    );
  }

  public static Instruction updateConstituentOracleInfo(final AccountMeta invokedDriftProgramMeta,
                                                        final PublicKey stateKey,
                                                        final PublicKey keeperKey,
                                                        final PublicKey constituentKey,
                                                        final PublicKey spotMarketKey,
                                                        final PublicKey oracleKey) {
    final var keys = updateConstituentOracleInfoKeys(
      invokedDriftProgramMeta,
      stateKey,
      keeperKey,
      constituentKey,
      spotMarketKey,
      oracleKey
    );
    return updateConstituentOracleInfo(invokedDriftProgramMeta, keys);
  }

  public static Instruction updateConstituentOracleInfo(final AccountMeta invokedDriftProgramMeta                                                        ,
                                                        final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, UPDATE_CONSTITUENT_ORACLE_INFO_DISCRIMINATOR);
  }

  public static final Discriminator DEPOSIT_TO_PROGRAM_VAULT_DISCRIMINATOR = toDiscriminator(235, 171, 121, 80, 57, 239, 147, 220);

  public static List<AccountMeta> depositToProgramVaultKeys(final AccountMeta invokedDriftProgramMeta                                                            ,
                                                            final PublicKey stateKey,
                                                            final PublicKey adminKey,
                                                            final PublicKey constituentKey,
                                                            final PublicKey constituentTokenAccountKey,
                                                            final PublicKey spotMarketKey,
                                                            final PublicKey spotMarketVaultKey,
                                                            final PublicKey tokenProgramKey,
                                                            final PublicKey mintKey,
                                                            final PublicKey oracleKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(adminKey),
      createWrite(constituentKey),
      createWrite(constituentTokenAccountKey),
      createWrite(spotMarketKey),
      createWrite(spotMarketVaultKey),
      createRead(tokenProgramKey),
      createRead(mintKey),
      createRead(oracleKey)
    );
  }

  public static Instruction depositToProgramVault(final AccountMeta invokedDriftProgramMeta,
                                                  final PublicKey stateKey,
                                                  final PublicKey adminKey,
                                                  final PublicKey constituentKey,
                                                  final PublicKey constituentTokenAccountKey,
                                                  final PublicKey spotMarketKey,
                                                  final PublicKey spotMarketVaultKey,
                                                  final PublicKey tokenProgramKey,
                                                  final PublicKey mintKey,
                                                  final PublicKey oracleKey,
                                                  final long amount) {
    final var keys = depositToProgramVaultKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      constituentKey,
      constituentTokenAccountKey,
      spotMarketKey,
      spotMarketVaultKey,
      tokenProgramKey,
      mintKey,
      oracleKey
    );
    return depositToProgramVault(invokedDriftProgramMeta, keys, amount);
  }

  public static Instruction depositToProgramVault(final AccountMeta invokedDriftProgramMeta                                                  ,
                                                  final List<AccountMeta> keys,
                                                  final long amount) {
    final byte[] _data = new byte[16];
    int i = DEPOSIT_TO_PROGRAM_VAULT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record DepositToProgramVaultIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static DepositToProgramVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static DepositToProgramVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new DepositToProgramVaultIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator WITHDRAW_FROM_PROGRAM_VAULT_DISCRIMINATOR = toDiscriminator(120, 40, 183, 149, 232, 18, 224, 151);

  public static List<AccountMeta> withdrawFromProgramVaultKeys(final AccountMeta invokedDriftProgramMeta                                                               ,
                                                               final PublicKey stateKey,
                                                               final PublicKey adminKey,
                                                               final PublicKey driftSignerKey,
                                                               final PublicKey constituentKey,
                                                               final PublicKey constituentTokenAccountKey,
                                                               final PublicKey spotMarketKey,
                                                               final PublicKey spotMarketVaultKey,
                                                               final PublicKey tokenProgramKey,
                                                               final PublicKey mintKey,
                                                               final PublicKey oracleKey) {
    return List.of(
      createRead(stateKey),
      createWritableSigner(adminKey),
      createRead(driftSignerKey),
      createWrite(constituentKey),
      createWrite(constituentTokenAccountKey),
      createWrite(spotMarketKey),
      createWrite(spotMarketVaultKey),
      createRead(tokenProgramKey),
      createRead(mintKey),
      createRead(oracleKey)
    );
  }

  public static Instruction withdrawFromProgramVault(final AccountMeta invokedDriftProgramMeta,
                                                     final PublicKey stateKey,
                                                     final PublicKey adminKey,
                                                     final PublicKey driftSignerKey,
                                                     final PublicKey constituentKey,
                                                     final PublicKey constituentTokenAccountKey,
                                                     final PublicKey spotMarketKey,
                                                     final PublicKey spotMarketVaultKey,
                                                     final PublicKey tokenProgramKey,
                                                     final PublicKey mintKey,
                                                     final PublicKey oracleKey,
                                                     final long amount) {
    final var keys = withdrawFromProgramVaultKeys(
      invokedDriftProgramMeta,
      stateKey,
      adminKey,
      driftSignerKey,
      constituentKey,
      constituentTokenAccountKey,
      spotMarketKey,
      spotMarketVaultKey,
      tokenProgramKey,
      mintKey,
      oracleKey
    );
    return withdrawFromProgramVault(invokedDriftProgramMeta, keys, amount);
  }

  public static Instruction withdrawFromProgramVault(final AccountMeta invokedDriftProgramMeta                                                     ,
                                                     final List<AccountMeta> keys,
                                                     final long amount) {
    final byte[] _data = new byte[16];
    int i = WITHDRAW_FROM_PROGRAM_VAULT_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, amount);

    return Instruction.createInstruction(invokedDriftProgramMeta, keys, _data);
  }

  public record WithdrawFromProgramVaultIxData(Discriminator discriminator, long amount) implements Borsh {  

    public static WithdrawFromProgramVaultIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static WithdrawFromProgramVaultIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var amount = getInt64LE(_data, i);
      return new WithdrawFromProgramVaultIxData(discriminator, amount);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, amount);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator SETTLE_PERP_TO_LP_POOL_DISCRIMINATOR = toDiscriminator(5, 98, 46, 188, 10, 59, 2, 249);

  public static List<AccountMeta> settlePerpToLpPoolKeys(final AccountMeta invokedDriftProgramMeta                                                         ,
                                                         final PublicKey stateKey,
                                                         final PublicKey lpPoolKey,
                                                         final PublicKey keeperKey,
                                                         final PublicKey ammCacheKey,
                                                         final PublicKey quoteMarketKey,
                                                         final PublicKey constituentKey,
                                                         final PublicKey constituentQuoteTokenAccountKey,
                                                         final PublicKey quoteTokenVaultKey,
                                                         final PublicKey tokenProgramKey,
                                                         final PublicKey driftSignerKey) {
    return List.of(
      createRead(stateKey),
      createWrite(lpPoolKey),
      createWritableSigner(keeperKey),
      createWrite(ammCacheKey),
      createWrite(quoteMarketKey),
      createWrite(constituentKey),
      createWrite(constituentQuoteTokenAccountKey),
      createWrite(quoteTokenVaultKey),
      createRead(tokenProgramKey),
      createRead(driftSignerKey)
    );
  }

  public static Instruction settlePerpToLpPool(final AccountMeta invokedDriftProgramMeta,
                                               final PublicKey stateKey,
                                               final PublicKey lpPoolKey,
                                               final PublicKey keeperKey,
                                               final PublicKey ammCacheKey,
                                               final PublicKey quoteMarketKey,
                                               final PublicKey constituentKey,
                                               final PublicKey constituentQuoteTokenAccountKey,
                                               final PublicKey quoteTokenVaultKey,
                                               final PublicKey tokenProgramKey,
                                               final PublicKey driftSignerKey) {
    final var keys = settlePerpToLpPoolKeys(
      invokedDriftProgramMeta,
      stateKey,
      lpPoolKey,
      keeperKey,
      ammCacheKey,
      quoteMarketKey,
      constituentKey,
      constituentQuoteTokenAccountKey,
      quoteTokenVaultKey,
      tokenProgramKey,
      driftSignerKey
    );
    return settlePerpToLpPool(invokedDriftProgramMeta, keys);
  }

  public static Instruction settlePerpToLpPool(final AccountMeta invokedDriftProgramMeta                                               ,
                                               final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedDriftProgramMeta, keys, SETTLE_PERP_TO_LP_POOL_DISCRIMINATOR);
  }

  private DriftProgram() {
  }
}
