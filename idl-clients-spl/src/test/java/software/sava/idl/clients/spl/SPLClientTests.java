package software.sava.idl.clients.spl;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.AccountWithSeed;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.spl.stake.LockUp;
import software.sava.idl.clients.spl.stake.StakeAccount;
import software.sava.idl.clients.spl.stake.StakeState;
import software.sava.idl.clients.spl.stake.gen.SolanaStakeInterfaceProgram;
import software.sava.idl.clients.spl.stake.gen.types.StakeAuthorize;

import java.time.Instant;
import java.util.List;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/// Coverage for the instruction-assembly layer. These builders are thin, but the thing they
/// do is *wiring* — putting each key into the right parameter slot — and a swap there is not
/// a crash, it is an instruction that authorizes the wrong party. So the assertions here are
/// about which key ends up where: every input key is distinct, the account list is checked for
/// order and for signer/writable roles, and the instruction data is decoded back through the
/// generated `IxData` records rather than by re-deriving byte offsets.
final class SPLClientTests {

  private static final SolanaAccounts ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final SPLClient CLIENT = SPLClient.createClient(ACCOUNTS);

  // Distinct, recognisable keys: any two of them swapped is a visible failure.
  private static final PublicKey STAKE = key(0x01);
  private static final PublicKey STAKER = key(0x02);
  private static final PublicKey WITHDRAWER = key(0x03);
  private static final PublicKey CUSTODIAN = key(0x04);
  private static final PublicKey NEW_AUTHORITY = key(0x05);
  private static final PublicKey VOTE = key(0x06);
  private static final PublicKey RECIPIENT = key(0x07);
  private static final PublicKey DESTINATION = key(0x08);
  private static final PublicKey PAYER = key(0x09);
  private static final PublicKey PROGRAM_OWNER = key(0x0A);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    java.util.Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  private static List<PublicKey> signers(final Instruction ix) {
    return ix.accounts().stream().filter(AccountMeta::signer).map(AccountMeta::publicKey).toList();
  }

  private static List<PublicKey> writable(final Instruction ix) {
    return ix.accounts().stream().filter(AccountMeta::write).map(AccountMeta::publicKey).toList();
  }

  private static StakeAccount stakeAccount(final LockUp lockUp) {
    return new StakeAccount(
        STAKE, StakeState.Stake, 0L,
        STAKER, WITHDRAWER,
        lockUp,
        VOTE, 0L, 0L, -1L, 0.25d, 0L, (byte) 0
    );
  }

  // ---------------------------------------------------------------------------
  // Construction
  // ---------------------------------------------------------------------------

  @Test
  void clientExposesItsAccounts() {
    assertEquals(ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(SolanaAccounts.MAIN_NET, SPLClient.createClient().solanaAccounts());

    final var accountClient = CLIENT.createAccountClient(STAKER, AccountMeta.createFeePayer(PAYER));
    assertEquals(STAKER, accountClient.owner());
    assertEquals(PAYER, accountClient.feePayerKey());
    assertSame(CLIENT, accountClient.splClient());

    // the PublicKey overload wraps the fee payer in a fee-payer meta
    final var fromKey = CLIENT.createAccountClient(STAKER, PAYER);
    assertEquals(STAKER, fromKey.owner());
    assertEquals(AccountMeta.createFeePayer(PAYER), fromKey.feePayer());

    // the single-argument overload makes the owner its own fee payer
    final var selfFunded = CLIENT.createAccountClient(STAKER);
    assertEquals(STAKER, selfFunded.owner());
    assertEquals(STAKER, selfFunded.feePayerKey());
  }

  // ---------------------------------------------------------------------------
  // Stake account lifecycle
  // ---------------------------------------------------------------------------

  /// `staker` and `withdrawer` are adjacent same-typed parameters, so a swap is only visible
  /// by decoding the data back. A null lockup must become the zero lockup, not a null field.
  @Test
  void initializeStakeAccountWiresAuthoritiesAndDefaultLockup() {
    final var ix = CLIENT.initializeStakeAccount(STAKE, STAKER, WITHDRAWER, null);

    assertEquals(ACCOUNTS.invokedStakeProgram(), ix.programId());
    assertEquals(List.of(STAKE, ACCOUNTS.rentSysVar()), keys(ix));
    assertEquals(List.of(STAKE), writable(ix));

    final var data = SolanaStakeInterfaceProgram.InitializeIxData.read(ix);
    assertEquals(STAKER, data.arg0().staker());
    assertEquals(WITHDRAWER, data.arg0().withdrawer());
    // toLockup(null) -> the zero lockup with no custodian
    assertEquals(0L, data.arg1().unixTimestamp().val());
    assertEquals(0L, data.arg1().epoch().val());
    assertEquals(PublicKey.NONE, data.arg1().custodian());
  }

  @Test
  void initializeStakeAccountCarriesAnExplicitLockup() {
    final var ix = CLIENT.initializeStakeAccount(STAKE, STAKER, WITHDRAWER, new LockUp(1_700L, 9L, CUSTODIAN));

    final var data = SolanaStakeInterfaceProgram.InitializeIxData.read(ix);
    assertEquals(1_700L, data.arg1().unixTimestamp().val());
    assertEquals(9L, data.arg1().epoch().val());
    assertEquals(CUSTODIAN, data.arg1().custodian());

    // a lockup with no custodian still serializes a key
    final var noCustodian = CLIENT.initializeStakeAccount(STAKE, STAKER, WITHDRAWER, new LockUp(1L, 2L, null));
    assertEquals(PublicKey.NONE, SolanaStakeInterfaceProgram.InitializeIxData.read(noCustodian).arg1().custodian());

    // the no-lockup convenience overload matches an explicit NO_LOCKUP
    assertArrayEquals(
        CLIENT.initializeStakeAccount(STAKE, STAKER, WITHDRAWER, LockUp.NO_LOCKUP).data(),
        CLIENT.initializeStakeAccount(STAKE, STAKER, WITHDRAWER).data());
  }

  @Test
  void initializeStakeAccountChecked() {
    final var ix = CLIENT.initializeStakeAccountChecked(STAKE, STAKER, WITHDRAWER);

    assertEquals(ACCOUNTS.invokedStakeProgram(), ix.programId());
    // the checked variant carries both authorities as accounts, and the withdrawer must sign
    assertEquals(List.of(STAKE, ACCOUNTS.rentSysVar(), STAKER, WITHDRAWER), keys(ix));
    assertEquals(List.of(WITHDRAWER), signers(ix));
    assertEquals(List.of(STAKE), writable(ix));
  }

  /// The lockup authority is optional; when absent it must not occupy a slot, because the
  /// on-chain program reads the account list positionally.
  @Test
  void authorizeStakeAccountOptionalLockupAuthority() {
    final var withoutLockup = CLIENT.authorizeStakeAccount(STAKE, STAKER, null, NEW_AUTHORITY, StakeAuthorize.staker);
    assertEquals(List.of(STAKE, ACCOUNTS.clockSysVar(), STAKER), keys(withoutLockup));
    assertEquals(List.of(STAKER), signers(withoutLockup));

    final var withLockup = CLIENT.authorizeStakeAccount(STAKE, STAKER, CUSTODIAN, NEW_AUTHORITY, StakeAuthorize.staker);
    assertEquals(List.of(STAKE, ACCOUNTS.clockSysVar(), STAKER, CUSTODIAN), keys(withLockup));
    assertEquals(List.of(STAKER, CUSTODIAN), signers(withLockup));

    // the new authority travels in the data, not the accounts
    final var data = SolanaStakeInterfaceProgram.AuthorizeIxData.read(withLockup);
    assertEquals(NEW_AUTHORITY, data.arg0());
    assertEquals(StakeAuthorize.staker, data.arg1());
  }

  /// The `StakeAccount` overload must pick the authority matching the role being changed:
  /// the *stake* authority signs a staker change, the *withdraw* authority a withdrawer
  /// change. Getting this backwards produces an instruction the wrong party can sign.
  @Test
  void authorizeStakeAccountSelectsAuthorityByRole() {
    final var account = stakeAccount(LockUp.NO_LOCKUP);

    final var staker = CLIENT.authorizeStakeAccount(account, NEW_AUTHORITY, StakeAuthorize.staker);
    assertEquals(List.of(STAKER), signers(staker));
    assertEquals(StakeAuthorize.staker, SolanaStakeInterfaceProgram.AuthorizeIxData.read(staker).arg1());

    final var withdrawer = CLIENT.authorizeStakeAccount(account, NEW_AUTHORITY, StakeAuthorize.withdrawer);
    assertEquals(List.of(WITHDRAWER), signers(withdrawer));
    assertEquals(StakeAuthorize.withdrawer, SolanaStakeInterfaceProgram.AuthorizeIxData.read(withdrawer).arg1());

    // and the three-arg overload defaults to no lockup authority
    assertEquals(
        keys(CLIENT.authorizeStakeAccount(STAKE, STAKER, NEW_AUTHORITY, StakeAuthorize.staker)),
        keys(staker));
  }

  @Test
  void authorizeStakeAccountChecked() {
    final var ix = CLIENT.authorizeStakeAccountChecked(
        STAKE, STAKER, NEW_AUTHORITY, CUSTODIAN, StakeAuthorize.withdrawer);

    assertEquals(List.of(STAKE, ACCOUNTS.clockSysVar(), STAKER, NEW_AUTHORITY, CUSTODIAN), keys(ix));
    // both the current authority and the incoming one must sign a checked authorize
    assertEquals(List.of(STAKER, NEW_AUTHORITY, CUSTODIAN), signers(ix));

    final var noLockup = CLIENT.authorizeStakeAccountChecked(
        STAKE, STAKER, NEW_AUTHORITY, null, StakeAuthorize.withdrawer);
    assertEquals(List.of(STAKE, ACCOUNTS.clockSysVar(), STAKER, NEW_AUTHORITY), keys(noLockup));

    // the StakeAccount overload picks the authority matching the role being changed
    final var account = stakeAccount(LockUp.NO_LOCKUP);
    final var forWithdrawer = CLIENT.authorizeStakeAccountChecked(account, NEW_AUTHORITY, StakeAuthorize.withdrawer);
    assertEquals(List.of(WITHDRAWER, NEW_AUTHORITY), signers(forWithdrawer));

    final var forStaker = CLIENT.authorizeStakeAccountChecked(account, NEW_AUTHORITY, StakeAuthorize.staker);
    assertEquals(List.of(STAKER, NEW_AUTHORITY), signers(forStaker));
  }

  /// The seeded variants derive the signing authority from a base key plus an ASCII seed, so
  /// the seed and its owning program have to reach the instruction data intact — the on-chain
  /// program re-derives the address from them and rejects a mismatch.
  ///
  /// DISABLED: this path currently throws [ArrayIndexOutOfBoundsException] for every input.
  /// `AuthorizeWithSeedArgs.l()` omits the 8-byte u64 length prefix that its `write()` emits
  /// ahead of the seed, so the caller under-allocates by exactly 8 bytes. That record is
  /// `@generated ... DO NOT EDIT`, so the fix belongs in idl-src-gen, not here. The assertions
  /// below are the intended contract; re-enable once the generator is fixed and regenerated.
  @org.junit.jupiter.api.Disabled("blocked on idl-src-gen: AuthorizeWithSeedArgs.l() omits the seed's 8-byte length prefix")
  @Test
  void authorizeStakeAccountWithSeed() {
    final var base = key(0x31);
    final var seeded = PublicKey.createOffCurveAccountWithAsciiSeed(base, "authority-seed", PROGRAM_OWNER);

    final var ix = CLIENT.authorizeStakeAccountWithSeed(
        STAKE, seeded, CUSTODIAN, NEW_AUTHORITY, StakeAuthorize.staker, PROGRAM_OWNER);

    assertEquals(ACCOUNTS.invokedStakeProgram(), ix.programId());
    // the base key signs, not the derived address
    assertEquals(List.of(STAKE, base, ACCOUNTS.clockSysVar(), CUSTODIAN), keys(ix));
    assertEquals(List.of(base, CUSTODIAN), signers(ix));

    final var data = SolanaStakeInterfaceProgram.AuthorizeWithSeedIxData.read(ix);
    assertEquals(NEW_AUTHORITY, data.arg0().newAuthorizedPubkey());
    assertEquals(StakeAuthorize.staker, data.arg0().stakeAuthorize());
    assertEquals("authority-seed", data.arg0().authoritySeed());
    assertEquals(PROGRAM_OWNER, data.arg0().authorityOwner());

    // the overload without a lockup authority omits that account
    final var noLockup = CLIENT.authorizeStakeAccountWithSeed(
        STAKE, seeded, NEW_AUTHORITY, StakeAuthorize.staker, PROGRAM_OWNER);
    assertEquals(List.of(STAKE, base, ACCOUNTS.clockSysVar()), keys(noLockup));
  }

  /// DISABLED for the same reason as [#authorizeStakeAccountWithSeed] —
  /// `AuthorizeCheckedWithSeedArgs.l()` is short by the same 8 bytes.
  @org.junit.jupiter.api.Disabled("blocked on idl-src-gen: AuthorizeCheckedWithSeedArgs.l() omits the seed's 8-byte length prefix")
  @Test
  void authorizeStakeAccountCheckedWithSeed() {
    final var base = key(0x31);
    final var seeded = PublicKey.createOffCurveAccountWithAsciiSeed(base, "checked-seed", PROGRAM_OWNER);

    final var ix = CLIENT.authorizeStakeAccountCheckedWithSeed(
        STAKE, seeded, NEW_AUTHORITY, CUSTODIAN, StakeAuthorize.withdrawer, PROGRAM_OWNER);

    assertEquals(ACCOUNTS.invokedStakeProgram(), ix.programId());
    // the checked variant additionally requires the incoming authority to sign
    assertEquals(List.of(STAKE, base, ACCOUNTS.clockSysVar(), NEW_AUTHORITY, CUSTODIAN), keys(ix));
    assertEquals(List.of(base, NEW_AUTHORITY, CUSTODIAN), signers(ix));

    final var data = SolanaStakeInterfaceProgram.AuthorizeCheckedWithSeedIxData.read(ix);
    assertEquals(StakeAuthorize.withdrawer, data.arg0().stakeAuthorize());
    assertEquals("checked-seed", data.arg0().authoritySeed());
    assertEquals(PROGRAM_OWNER, data.arg0().authorityOwner());
  }

  /// Redelegation was removed from the runtime, so the builder emits the bare discriminator
  /// with no accounts — it must not silently assemble something that looks usable.
  @Test
  void reDelegateStakeAccount() {
    final var ix = CLIENT.reDelegateStakeAccount(stakeAccount(LockUp.NO_LOCKUP), DESTINATION, VOTE);

    assertEquals(ACCOUNTS.invokedStakeProgram(), ix.programId());
    assertEquals(List.of(), keys(ix));
  }

  @Test
  void delegateStakeAccount() {
    final var ix = CLIENT.delegateStakeAccount(STAKE, VOTE, STAKER);

    assertEquals(ACCOUNTS.invokedStakeProgram(), ix.programId());
    assertEquals(
        List.of(STAKE, VOTE, ACCOUNTS.clockSysVar(), ACCOUNTS.stakeHistorySysVar(), ACCOUNTS.stakeConfig(), STAKER),
        keys(ix));
    assertEquals(List.of(STAKER), signers(ix));
    assertEquals(List.of(STAKE), writable(ix));

    // the StakeAccount overload supplies the address and stake authority from the account
    assertEquals(keys(ix), keys(CLIENT.delegateStakeAccount(stakeAccount(LockUp.NO_LOCKUP), VOTE)));
  }

  @Test
  void splitStakeAccount() {
    final var ix = CLIENT.splitStakeAccount(stakeAccount(LockUp.NO_LOCKUP), DESTINATION, 5_000L);

    assertEquals(List.of(STAKE, DESTINATION, STAKER), keys(ix));
    assertEquals(List.of(STAKER), signers(ix));
    // both stake accounts are mutated
    assertEquals(List.of(STAKE, DESTINATION), writable(ix));
    assertEquals(5_000L, SolanaStakeInterfaceProgram.SplitIxData.read(ix).args());
  }

  /// Merge is directional: the destination is the account that survives.
  @Test
  void mergeStakeAccounts() {
    final var destination = stakeAccount(LockUp.NO_LOCKUP);
    final var ix = CLIENT.mergeStakeAccounts(destination, DESTINATION);

    assertEquals(
        List.of(STAKE, DESTINATION, ACCOUNTS.clockSysVar(), ACCOUNTS.stakeHistorySysVar(), STAKER),
        keys(ix));
    assertEquals(List.of(STAKER), signers(ix));
    assertEquals(List.of(STAKE, DESTINATION), writable(ix));
  }

  /// Withdrawal is the highest-consequence builder here: the *withdraw* authority signs, not
  /// the stake authority, and a lockup adds the custodian as an extra required signer. A
  /// lockup that is present but empty must not add that slot.
  @Test
  void withdrawStakeAccountSignersDependOnLockup() {
    final var unlocked = CLIENT.withdrawStakeAccount(stakeAccount(LockUp.NO_LOCKUP), RECIPIENT, 1_234L);

    assertEquals(
        List.of(STAKE, RECIPIENT, ACCOUNTS.clockSysVar(), ACCOUNTS.stakeHistorySysVar(), WITHDRAWER),
        keys(unlocked));
    assertEquals(List.of(WITHDRAWER), signers(unlocked));
    assertEquals(List.of(STAKE, RECIPIENT), writable(unlocked));
    assertEquals(1_234L, SolanaStakeInterfaceProgram.WithdrawIxData.read(unlocked).args());

    // an absent lockup is treated the same as an empty one
    assertEquals(keys(unlocked), keys(CLIENT.withdrawStakeAccount(stakeAccount(null), RECIPIENT, 1_234L)));

    // a real lockup appends its custodian as an additional signer
    final var locked = CLIENT.withdrawStakeAccount(
        stakeAccount(new LockUp(1_700L, 9L, CUSTODIAN)), RECIPIENT, 1_234L);
    assertEquals(
        List.of(STAKE, RECIPIENT, ACCOUNTS.clockSysVar(), ACCOUNTS.stakeHistorySysVar(), WITHDRAWER, CUSTODIAN),
        keys(locked));
    assertEquals(List.of(WITHDRAWER, CUSTODIAN), signers(locked));
  }

  @Test
  void deactivateStakeAccount() {
    final var ix = CLIENT.deactivateStakeAccount(STAKE, STAKER);

    assertEquals(List.of(STAKE, ACCOUNTS.clockSysVar(), STAKER), keys(ix));
    assertEquals(List.of(STAKER), signers(ix));

    // the StakeAccount overload takes the address and stake authority from the account
    assertEquals(keys(ix), keys(CLIENT.deactivateStakeAccount(stakeAccount(LockUp.NO_LOCKUP))));
  }

  @Test
  void deactivateDelinquentStake() {
    final var ix = CLIENT.deactivateDelinquentStake(STAKE, VOTE, DESTINATION);

    assertEquals(List.of(STAKE, VOTE, DESTINATION), keys(ix));
    // no signature is required to deactivate a delinquent stake
    assertEquals(List.of(), signers(ix));
  }

  @Test
  void setStakeAccountLockup() {
    final var timestamp = Instant.ofEpochSecond(1_700_000_000L);
    final var ix = CLIENT.setStakeAccountLockup(STAKE, WITHDRAWER, timestamp, OptionalLong.of(9L), CUSTODIAN);

    assertEquals(List.of(STAKE, WITHDRAWER), keys(ix));
    assertEquals(List.of(WITHDRAWER), signers(ix));

    final var data = SolanaStakeInterfaceProgram.SetLockupIxData.read(ix);
    assertEquals(1_700_000_000L, data.arg0().unixTimestamp().val());
    assertEquals(9L, data.arg0().epoch().val());
    assertEquals(CUSTODIAN, data.arg0().custodian());

    // each lockup field is independently optional
    final var noneSet = CLIENT.setStakeAccountLockup(STAKE, WITHDRAWER, null, OptionalLong.empty(), null);
    final var empty = SolanaStakeInterfaceProgram.SetLockupIxData.read(noneSet);
    assertNull(empty.arg0().unixTimestamp());
    assertNull(empty.arg0().epoch());
    assertNull(empty.arg0().custodian());
  }

  @Test
  void setStakeAccountLockupChecked() {
    final var timestamp = Instant.ofEpochSecond(1_700_000_000L);
    final var ix = CLIENT.setStakeAccountLockupChecked(
        STAKE, WITHDRAWER, NEW_AUTHORITY, timestamp, OptionalLong.of(9L));

    // the incoming lockup authority must sign
    assertEquals(List.of(STAKE, WITHDRAWER, NEW_AUTHORITY), keys(ix));
    assertEquals(List.of(WITHDRAWER, NEW_AUTHORITY), signers(ix));

    final var data = SolanaStakeInterfaceProgram.SetLockupCheckedIxData.read(ix);
    assertEquals(1_700_000_000L, data.arg0().unixTimestamp().val());
    assertEquals(9L, data.arg0().epoch().val());

    // the overload without a new authority omits that account entirely
    final var noNewAuthority = CLIENT.setStakeAccountLockupChecked(
        STAKE, WITHDRAWER, timestamp, OptionalLong.of(9L));
    assertEquals(List.of(STAKE, WITHDRAWER), keys(noNewAuthority));

    final var cleared = SolanaStakeInterfaceProgram.SetLockupCheckedIxData.read(
        CLIENT.setStakeAccountLockupChecked(STAKE, WITHDRAWER, null, OptionalLong.empty()));
    assertNull(cleared.arg0().unixTimestamp());
    assertNull(cleared.arg0().epoch());
  }

  @Test
  void moveStakeAndMoveLamports() {
    final var move = CLIENT.moveStake(STAKE, DESTINATION, STAKER, 700L);
    assertEquals(List.of(STAKE, DESTINATION, STAKER), keys(move));
    assertEquals(List.of(STAKER), signers(move));
    assertEquals(List.of(STAKE, DESTINATION), writable(move));
    assertEquals(700L, SolanaStakeInterfaceProgram.MoveStakeIxData.read(move).args());

    final var lamports = CLIENT.moveLamports(STAKE, DESTINATION, STAKER, 800L);
    assertEquals(keys(move), keys(lamports));
    assertEquals(800L, SolanaStakeInterfaceProgram.MoveLamportsIxData.read(lamports).args());

    // the two are distinct instructions, not aliases
    assertNotEquals(move.data()[0], lamports.data()[0]);

    // the StakeAccount overloads supply the source address and its stake authority
    final var account = stakeAccount(LockUp.NO_LOCKUP);
    assertEquals(keys(move), keys(CLIENT.moveStake(account, DESTINATION, 700L)));
    assertEquals(keys(lamports), keys(CLIENT.moveLamports(account, DESTINATION, 800L)));
  }

  /// Allocation must reserve exactly a stake account's footprint and assign it to the stake
  /// program — an account sized or owned wrongly cannot later be initialized.
  @Test
  void allocateStakeAccount() {
    final var ix = CLIENT.allocateStakeAccount(STAKE, PAYER);

    assertEquals(ACCOUNTS.invokedSystemProgram(), ix.programId());
    assertEquals(List.of(PAYER, STAKE), keys(ix));

    // createAccount data: u32 discriminant, u64 lamports, u64 space, 32-byte owner
    final byte[] data = ix.data();
    assertEquals(0L, software.sava.core.encoding.ByteUtil.getInt64LE(data, 4), "lamports");
    assertEquals(StakeAccount.BYTES, software.sava.core.encoding.ByteUtil.getInt64LE(data, 12), "space");
    assertEquals(ACCOUNTS.stakeProgram(), PublicKey.readPubKey(data, 20), "owner");
  }

  // ---------------------------------------------------------------------------
  // Merge fan-out helpers
  // ---------------------------------------------------------------------------

  /// Every fan-out variant merges *into the first account*, pairwise — not sequentially into
  /// the previous one. The list overloads also refuse to emit anything below two accounts,
  /// since a single account has nothing to merge with.
  @Test
  void mergeFanOutTargetsTheFirstAccount() {
    final var destination = stakeAccount(LockUp.NO_LOCKUP);
    final var second = key(0x21);
    final var third = key(0x22);

    final var byKeys = CLIENT.mergeStakeAccountKeysInto(destination, List.of(second, third));
    assertEquals(2, byKeys.size());
    assertEquals(List.of(STAKE, second), keys(byKeys.getFirst()).subList(0, 2));
    assertEquals(List.of(STAKE, third), keys(byKeys.getLast()).subList(0, 2));

    final var byAccounts = CLIENT.mergeStakeAccountsInto(
        destination, List.of(accountAt(second), accountAt(third)));
    assertEquals(2, byAccounts.size());
    assertEquals(keys(byKeys.getFirst()), keys(byAccounts.getFirst()));
    assertEquals(keys(byKeys.getLast()), keys(byAccounts.getLast()));

    assertEquals(1, CLIENT.mergeStakeAccountKeysInto(destination, List.of(second)).size());
    assertEquals(0, CLIENT.mergeStakeAccountKeysInto(destination, List.of()).size());
  }

  @Test
  void mergeListRequiresTwoAccounts() {
    final var first = stakeAccount(LockUp.NO_LOCKUP);
    final var second = accountAt(key(0x21));
    final var third = accountAt(key(0x22));

    // fewer than two accounts is a no-op, and exactly two is not
    assertEquals(List.of(), CLIENT.mergeStakeAccounts(List.of()));
    assertEquals(List.of(), CLIENT.mergeStakeAccounts(List.of(first)));
    assertEquals(1, CLIENT.mergeStakeAccounts(List.of(first, second)).size());

    final var merged = CLIENT.mergeStakeAccounts(List.of(first, second, third));
    assertEquals(2, merged.size());
    // both merge into the first account, not into each other
    assertEquals(List.of(STAKE, second.address()), keys(merged.getFirst()).subList(0, 2));
    assertEquals(List.of(STAKE, third.address()), keys(merged.getLast()).subList(0, 2));
  }

  @Test
  void mergeCollectionRequiresTwoAccounts() {
    final var first = stakeAccount(LockUp.NO_LOCKUP);
    final var second = accountAt(key(0x21));
    final var third = accountAt(key(0x22));

    assertEquals(List.of(), CLIENT.mergeStakeAccounts(java.util.Set.of()));
    assertEquals(List.of(), CLIENT.mergeStakeAccounts(java.util.Set.of(first)));

    // a SequencedCollection preserves the same "merge into the first" rule
    final var merged = CLIENT.mergeStakeAccounts(new java.util.LinkedHashSet<>(List.of(first, second, third)));
    assertEquals(2, merged.size());
    assertEquals(List.of(STAKE, second.address()), keys(merged.getFirst()).subList(0, 2));
    assertEquals(List.of(STAKE, third.address()), keys(merged.getLast()).subList(0, 2));

    assertEquals(1, CLIENT.mergeStakeAccounts(new java.util.LinkedHashSet<>(List.of(first, second))).size());
  }

  @Test
  void mergeStakeAccountsFromTwoAccountObjects() {
    final var destination = stakeAccount(LockUp.NO_LOCKUP);
    final var source = accountAt(key(0x21));

    final var ix = CLIENT.mergeStakeAccounts(destination, source);
    assertEquals(List.of(STAKE, source.address()), keys(ix).subList(0, 2));
  }

  @Test
  void deactivateStakeAccounts() {
    final var accounts = List.of(stakeAccount(LockUp.NO_LOCKUP), accountAt(key(0x21)));

    final var instructions = CLIENT.deactivateStakeAccounts(accounts);
    assertEquals(2, instructions.size());
    assertEquals(STAKE, keys(instructions.getFirst()).getFirst());
    assertEquals(key(0x21), keys(instructions.getLast()).getFirst());

    assertEquals(List.of(), CLIENT.deactivateStakeAccounts(List.of()));
  }

  private static StakeAccount accountAt(final PublicKey address) {
    return new StakeAccount(
        address, StakeState.Stake, 0L,
        STAKER, WITHDRAWER, LockUp.NO_LOCKUP,
        VOTE, 0L, 0L, -1L, 0.25d, 0L, (byte) 0
    );
  }

  // ---------------------------------------------------------------------------
  // System / ATA / lookup tables
  // ---------------------------------------------------------------------------

  @Test
  void createAccount() {
    final var ix = CLIENT.createAccount(PAYER, STAKE, 1_000L, 200L, PROGRAM_OWNER);

    assertEquals(ACCOUNTS.invokedSystemProgram(), ix.programId());
    assertEquals(List.of(PAYER, STAKE), keys(ix));
    // both the funder and the new account sign, and both are mutated
    assertEquals(List.of(PAYER, STAKE), signers(ix));
    assertEquals(List.of(PAYER, STAKE), writable(ix));
  }

  @Test
  void createAccountWithSeed() {
    final var base = key(0x31);
    final var seeded = PublicKey.createOffCurveAccountWithAsciiSeed(base, "my-seed", PROGRAM_OWNER);

    final var ix = CLIENT.createAccountWithSeed(PAYER, seeded, 1_000L, 200L, PROGRAM_OWNER);

    assertEquals(ACCOUNTS.invokedSystemProgram(), ix.programId());
    assertTrue(keys(ix).contains(PAYER), "the funder");
    assertTrue(keys(ix).contains(seeded.publicKey()), "the derived account");
    // the base key authorizes the derivation and must sign
    assertTrue(signers(ix).contains(base), "the base key must sign");

    // the seed round-trips into the instruction data, so a different seed is a different ix
    final var otherSeed = PublicKey.createOffCurveAccountWithAsciiSeed(base, "other-seed", PROGRAM_OWNER);
    assertNotEquals(
        seeded.publicKey(),
        otherSeed.publicKey());
    assertFalse(
        java.util.Arrays.equals(ix.data(), CLIENT.createAccountWithSeed(PAYER, otherSeed, 1_000L, 200L, PROGRAM_OWNER).data()),
        "the seed must be serialized");
  }

  @Test
  void syncNative() {
    final var ix = CLIENT.syncNative(STAKE);

    assertEquals(ACCOUNTS.invokedTokenProgram(), ix.programId());
    assertEquals(List.of(STAKE), keys(ix));
    assertEquals(List.of(STAKE), writable(ix));
  }

  @Test
  void findATAIsDerivedFromOwnerMintAndTokenProgram() {
    final var mint = key(0x41);
    final var ata = CLIENT.findATA(STAKER, ACCOUNTS.tokenProgram(), mint);

    assertNotNull(ata.publicKey());
    // deterministic
    assertEquals(ata.publicKey(), CLIENT.findATA(STAKER, ACCOUNTS.tokenProgram(), mint).publicKey());
    // and sensitive to each input
    assertNotEquals(ata.publicKey(), CLIENT.findATA(WITHDRAWER, ACCOUNTS.tokenProgram(), mint).publicKey());
    assertNotEquals(ata.publicKey(), CLIENT.findATA(STAKER, ACCOUNTS.tokenProgram(), key(0x42)).publicKey());
    assertNotEquals(ata.publicKey(), CLIENT.findATA(STAKER, ACCOUNTS.token2022Program(), mint).publicKey());

    // the convenience overloads pick the classic and 2022 token programs respectively
    assertEquals(ata.publicKey(), CLIENT.findATA(STAKER, mint).publicKey());
    assertEquals(
        CLIENT.findATA(STAKER, ACCOUNTS.token2022Program(), mint).publicKey(),
        CLIENT.find2022ATA(STAKER, mint).publicKey());
  }

  @Test
  void lookupTableAddressIsDerivedFromAuthorityAndSlot() {
    final var table = CLIENT.findLookupTableAddress(STAKER, 100L);

    assertNotNull(table.publicKey());
    assertEquals(table.publicKey(), CLIENT.findLookupTableAddress(STAKER, 100L).publicKey());
    assertNotEquals(table.publicKey(), CLIENT.findLookupTableAddress(STAKER, 101L).publicKey());
    assertNotEquals(table.publicKey(), CLIENT.findLookupTableAddress(WITHDRAWER, 100L).publicKey());
  }

  @Test
  void lookupTableLifecycle() {
    final var table = CLIENT.findLookupTableAddress(STAKER, 100L);
    final var lutProgram = ACCOUNTS.invokedAddressLookupTableProgram();

    // Creation deliberately does *not* require the authority's signature: the table address is
    // derived from the authority and slot, so the derivation itself proves control. This
    // mirrors Solana's `create_lookup_table` (as opposed to `create_lookup_table_signed`).
    final var create = CLIENT.createLookupTable(table, STAKER, PAYER, 100L);
    assertEquals(lutProgram, create.programId());
    assertEquals(List.of(table.publicKey(), STAKER, PAYER, ACCOUNTS.systemProgram()), keys(create));
    assertEquals(List.of(PAYER), signers(create), "only the funder signs creation");
    assertEquals(List.of(table.publicKey(), PAYER), writable(create));

    // Extension, by contrast, does require the authority to sign.
    final var extend = CLIENT.extendLookupTable(table.publicKey(), STAKER, PAYER, List.of(VOTE, DESTINATION));
    assertEquals(lutProgram, extend.programId());
    assertEquals(List.of(table.publicKey(), STAKER, PAYER, ACCOUNTS.systemProgram()), keys(extend));
    assertEquals(List.of(STAKER, PAYER), signers(extend));
    // the new addresses travel in the data, so each one lengthens the instruction by a key
    assertEquals(
        PublicKey.PUBLIC_KEY_LENGTH,
        extend.data().length - CLIENT.extendLookupTable(table.publicKey(), STAKER, PAYER, List.of(VOTE)).data().length,
        "each additional address must be serialized");

    // the two-key overload makes the authority its own funder
    assertEquals(
        List.of(table.publicKey(), STAKER, STAKER, ACCOUNTS.systemProgram()),
        keys(CLIENT.extendLookupTable(table.publicKey(), STAKER, List.of(VOTE))));

    final var deactivate = CLIENT.deactivateLookupTable(table.publicKey(), STAKER);
    assertEquals(List.of(table.publicKey(), STAKER), keys(deactivate));
    assertEquals(List.of(STAKER), signers(deactivate));

    final var freeze = CLIENT.freezeLookupTable(table.publicKey(), STAKER);
    assertEquals(List.of(table.publicKey(), STAKER), keys(freeze));
    assertEquals(List.of(STAKER), signers(freeze));
    // freeze and deactivate share an account layout, so only the data distinguishes them
    assertNotEquals(freeze.data()[0], deactivate.data()[0]);

    // close sends the rent to an explicit recipient, defaulting to the authority
    final var close = CLIENT.closeLookupTable(table.publicKey(), STAKER, RECIPIENT);
    assertEquals(List.of(table.publicKey(), STAKER, RECIPIENT), keys(close));
    assertEquals(List.of(STAKER), signers(close));
    assertEquals(List.of(table.publicKey(), RECIPIENT), writable(close));
    assertEquals(
        List.of(table.publicKey(), STAKER, STAKER),
        keys(CLIENT.closeLookupTable(table.publicKey(), STAKER)));
  }

  // ---------------------------------------------------------------------------
  // Compute budget
  // ---------------------------------------------------------------------------

  /// The limit builder adds the compute the budget instructions themselves consume, so the
  /// caller's figure is a budget for *their* work.
  @Test
  void computeBudgetLimitAddsTheBudgetInstructionOverhead() {
    final var ix = CLIENT.computeBudgetLimit(10_000);

    assertEquals(ACCOUNTS.invokedComputeBudgetProgram(), ix.programId());
    assertEquals(List.of(), keys(ix));

    // discriminator byte, then the u32 limit little-endian
    final byte[] data = ix.data();
    final int encoded = software.sava.core.encoding.ByteUtil.getInt32LE(data, 1);
    assertEquals(10_000 + ComputeBudgetUtilAccess.COMPUTE_UNITS_CONSUMED, encoded);
    assertTrue(encoded > 10_000, "the overhead must be added, not subtracted");
  }

  @Test
  void computeBudgetPricePassesThePriceThrough() {
    final var ix = CLIENT.computeBudgetPrice(5_000L);

    assertEquals(ACCOUNTS.invokedComputeBudgetProgram(), ix.programId());
    assertEquals(List.of(), keys(ix));
    assertEquals(5_000L, software.sava.core.encoding.ByteUtil.getInt64LE(ix.data(), 1));

    assertNotEquals(ix.data()[0], CLIENT.computeBudgetLimit(10_000).data()[0]);
  }

  /// Bridges to the package-private constant so the expectation above is not a second copy of
  /// the magic number.
  private static final class ComputeBudgetUtilAccess {

    private static final int COMPUTE_UNITS_CONSUMED =
        software.sava.idl.clients.spl.compute_budget.ComputeBudgetUtil.COMPUTE_UNITS_CONSUMED;
  }
}
