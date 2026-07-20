package software.sava.idl.clients.jupiter.voter;

import org.junit.jupiter.api.Test;
import software.sava.idl.clients.jupiter.voter.gen.types.Escrow;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.jupiter.JupiterAccounts;
import software.sava.idl.clients.spl.SPLAccountClient;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Covers the Jupiter vote client's instruction assembly.
///
/// The client spans three programs — locked-voter, governance and merkle
/// distributor — and every builder has to invoke the right one. That is the
/// property with teeth here: an instruction submitted to the wrong program id
/// cannot execute, and because the builders all take the same shape, a
/// mis-bound program is invisible without asserting it.
final class JupiterVoteClientTests {

  private static final SolanaAccounts SOLANA_ACCOUNTS = SolanaAccounts.MAIN_NET;
  private static final JupiterAccounts JUPITER_ACCOUNTS = JupiterAccounts.MAIN_NET;

  private static final PublicKey OWNER = key(0x11);
  private static final PublicKey FEE_PAYER = key(0x12);
  private static final PublicKey ESCROW_TOKENS = key(0x13);
  private static final PublicKey SOURCE_TOKENS = key(0x14);
  private static final PublicKey DESTINATION_TOKENS = key(0x15);
  private static final PublicKey PROPOSAL = key(0x16);
  private static final PublicKey VOTE = key(0x17);
  private static final PublicKey DELEGATE = key(0x18);
  private static final PublicKey PARTIAL_UNSTAKE = key(0x19);
  private static final PublicKey SMART_WALLET = key(0x1A);

  private static final SPLAccountClient ACCOUNT_CLIENT =
      SPLAccountClient.createClient(SOLANA_ACCOUNTS, OWNER, AccountMeta.createFeePayer(FEE_PAYER));
  private static final JupiterVoteClient CLIENT =
      JupiterVoteClient.createClient(ACCOUNT_CLIENT, JUPITER_ACCOUNTS);

  private static PublicKey key(final int fill) {
    final byte[] publicKey = new byte[PublicKey.PUBLIC_KEY_LENGTH];
    Arrays.fill(publicKey, (byte) fill);
    return PublicKey.createPubKey(publicKey);
  }

  private static List<PublicKey> keys(final Instruction ix) {
    return ix.accounts().stream().map(AccountMeta::publicKey).toList();
  }

  @Test
  void clientDerivesItsEscrowIdentity() {
    assertSame(ACCOUNT_CLIENT, CLIENT.splAccountClient());
    assertEquals(SOLANA_ACCOUNTS, CLIENT.solanaAccounts());
    assertEquals(JUPITER_ACCOUNTS, CLIENT.jupiterAccounts());
    assertEquals(OWNER, CLIENT.escrowOwnerKey());
    assertEquals(FEE_PAYER, CLIENT.feePayer());

    // the escrow is the owner's escrow under the vote program
    assertEquals(JUPITER_ACCOUNTS.deriveEscrow(OWNER).publicKey(), CLIENT.escrowKey());
    // the two token accounts are the JUP ATAs of the owner and of the escrow —
    // different accounts, and neither is the other's
    assertEquals(
        ACCOUNT_CLIENT.splClient().findATA(OWNER, JUPITER_ACCOUNTS.jupTokenMint()).publicKey(),
        CLIENT.escrowOwnerKeyATA());
    assertEquals(
        ACCOUNT_CLIENT.splClient().findATA(CLIENT.escrowKey(), JUPITER_ACCOUNTS.jupTokenMint()).publicKey(),
        CLIENT.escrowATA());
    assertNotEquals(CLIENT.escrowOwnerKeyATA(), CLIENT.escrowATA());

    assertEquals(JUPITER_ACCOUNTS.deriveVote(PROPOSAL, OWNER).publicKey(), CLIENT.deriveVoteKey(PROPOSAL));

    // the single-arg factory defaults to main-net accounts
    assertNotNull(JupiterVoteClient.createClient(ACCOUNT_CLIENT));
    assertEquals(JupiterAccounts.MAIN_NET, JupiterVoteClient.createClient(ACCOUNT_CLIENT).jupiterAccounts());
  }

  /// Regression: `newEscrow` builds a `LockedVoterProgram` instruction but used
  /// to invoke the *governance* program. The escrow PDA is derived under the
  /// vote program, so that instruction could never execute — and every sibling
  /// locked-voter builder binds the vote program, which is what made the odd
  /// one out invisible.
  @Test
  void lockedVoterInstructionsInvokeTheVoteProgram() {
    final var voteProgram = JUPITER_ACCOUNTS.invokedVoteProgram();

    assertEquals(voteProgram, CLIENT.newEscrow(OWNER, CLIENT.escrowKey(), FEE_PAYER).programId(),
        "newEscrow is a locked-voter instruction");

    assertEquals(voteProgram, CLIENT.castVote(CLIENT.escrowKey(), DELEGATE, PROPOSAL, VOTE, 1).programId());
    assertEquals(voteProgram, CLIENT.setVoteDelegate(OWNER, CLIENT.escrowKey(), DELEGATE).programId());
    assertEquals(voteProgram, CLIENT.increaseLockedAmount(
        CLIENT.escrowKey(), ESCROW_TOKENS, FEE_PAYER, SOURCE_TOKENS, 1_000L).programId());
    assertEquals(voteProgram, CLIENT.extendLockDuration(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, 86_400L).programId());
    assertEquals(voteProgram, CLIENT.toggleMaxLock(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, true).programId());
    assertEquals(voteProgram, CLIENT.withdraw(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, ESCROW_TOKENS, FEE_PAYER, DESTINATION_TOKENS).programId());
    assertEquals(voteProgram, CLIENT.openPartialUnstaking(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, PARTIAL_UNSTAKE, 500L, "memo").programId());
    assertEquals(voteProgram, CLIENT.mergePartialUnstaking(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, PARTIAL_UNSTAKE).programId());
    assertEquals(voteProgram, CLIENT.withdrawPartialUnstaking(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, ESCROW_TOKENS, PARTIAL_UNSTAKE, FEE_PAYER, DESTINATION_TOKENS).programId());
    assertEquals(voteProgram, CLIENT.activateProposal(PROPOSAL, SMART_WALLET).programId());

    // the governance program is a different program, so the assertions above
    // are not trivially satisfied
    assertNotEquals(voteProgram, JUPITER_ACCOUNTS.invokedGovProgram());
  }

  /// The governance builders invoke the governance program — the counterpart
  /// to the check above.
  @Test
  void governanceInstructionsInvokeTheGovProgram() {
    final var govProgram = JUPITER_ACCOUNTS.invokedGovProgram();

    assertEquals(govProgram, CLIENT.newVote(PROPOSAL, VOTE, FEE_PAYER, OWNER).programId());
    assertEquals(govProgram, CLIENT.createGovernor(
        key(0x21), key(0x22), SMART_WALLET, FEE_PAYER, JUPITER_ACCOUNTS.lockerKey(),
        new software.sava.idl.clients.jupiter.governance.gen.types.GovernanceParameters(1L, 2L, 3L, 4L)).programId());
  }

  @Test
  void newEscrowWiresTheLockerOwnerAndPayer() {
    final var ix = CLIENT.newEscrow(OWNER, CLIENT.escrowKey(), FEE_PAYER);
    final var accounts = keys(ix);

    assertTrue(accounts.contains(JUPITER_ACCOUNTS.lockerKey()), "the locker");
    assertTrue(accounts.contains(CLIENT.escrowKey()), "the escrow being created");
    assertTrue(accounts.contains(OWNER), "the escrow owner");
    assertTrue(accounts.contains(FEE_PAYER), "the funder");
    assertTrue(accounts.contains(SOLANA_ACCOUNTS.systemProgram()), "account creation needs the system program");

    // the payer signs and is debited
    assertTrue(ix.accounts().stream().anyMatch(m -> m.publicKey().equals(FEE_PAYER) && m.signer()));

    // the no-arg overload defaults the owner and escrow to the client's own
    assertEquals(ix, CLIENT.newEscrow(FEE_PAYER));
  }

  /// `castVote` carries the locker, escrow, delegate, proposal and vote
  /// accounts plus the governor — six distinct keys whose positions decide
  /// which proposal is voted on and by whom.
  @Test
  void castVoteWiresEveryParticipant() {
    final var ix = CLIENT.castVote(CLIENT.escrowKey(), DELEGATE, PROPOSAL, VOTE, 1);
    final var accounts = keys(ix);

    assertTrue(accounts.contains(JUPITER_ACCOUNTS.lockerKey()));
    assertTrue(accounts.contains(CLIENT.escrowKey()));
    assertTrue(accounts.contains(DELEGATE));
    assertTrue(accounts.contains(PROPOSAL));
    assertTrue(accounts.contains(VOTE));
    assertTrue(accounts.contains(JUPITER_ACCOUNTS.governorKey()));

    // the delegate is the signer, not the escrow owner
    assertTrue(ix.accounts().stream().anyMatch(m -> m.publicKey().equals(DELEGATE) && m.signer()));

    // the side is data, so a different side is a different instruction with the
    // same accounts
    final var otherSide = CLIENT.castVote(CLIENT.escrowKey(), DELEGATE, PROPOSAL, VOTE, 0);
    assertEquals(accounts, keys(otherSide));
    assertFalse(Arrays.equals(ix.data(), otherSide.data()));
  }

  @Test
  void increaseLockedAmountWiresBothTokenAccounts() {
    final var ix = CLIENT.increaseLockedAmount(CLIENT.escrowKey(), ESCROW_TOKENS, FEE_PAYER, SOURCE_TOKENS, 1_000L);
    final var accounts = keys(ix);

    assertTrue(accounts.contains(JUPITER_ACCOUNTS.lockerKey()));
    assertTrue(accounts.contains(CLIENT.escrowKey()));
    assertTrue(accounts.contains(ESCROW_TOKENS), "the destination");
    assertTrue(accounts.contains(SOURCE_TOKENS), "the source");
    assertTrue(accounts.contains(SOLANA_ACCOUNTS.tokenProgram()));
    // source and destination occupy different slots
    assertNotEquals(accounts.indexOf(ESCROW_TOKENS), accounts.indexOf(SOURCE_TOKENS));

    // the amount is data: a different amount keeps the same accounts
    final var larger = CLIENT.increaseLockedAmount(CLIENT.escrowKey(), ESCROW_TOKENS, FEE_PAYER, SOURCE_TOKENS, 2_000L);
    assertEquals(accounts, keys(larger));
    assertFalse(Arrays.equals(ix.data(), larger.data()));
  }

  @Test
  void withdrawSendsToTheDestinationNotTheEscrow() {
    final var ix = CLIENT.withdraw(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, ESCROW_TOKENS, FEE_PAYER, DESTINATION_TOKENS);
    final var accounts = keys(ix);

    assertTrue(accounts.contains(ESCROW_TOKENS), "the escrow's tokens are the source");
    assertTrue(accounts.contains(DESTINATION_TOKENS), "the caller's account is the destination");
    assertNotEquals(accounts.indexOf(ESCROW_TOKENS), accounts.indexOf(DESTINATION_TOKENS));
    assertTrue(accounts.contains(OWNER));
    assertTrue(accounts.contains(SOLANA_ACCOUNTS.tokenProgram()));
  }

  @Test
  void partialUnstakingSharesItsAccountShape() {
    final var open = CLIENT.openPartialUnstaking(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, PARTIAL_UNSTAKE, 500L, "half");
    assertTrue(keys(open).contains(PARTIAL_UNSTAKE));
    assertTrue(keys(open).contains(SOLANA_ACCOUNTS.systemProgram()), "the partial unstake account is created");

    final var merge = CLIENT.mergePartialUnstaking(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, PARTIAL_UNSTAKE);
    assertTrue(keys(merge).contains(PARTIAL_UNSTAKE));

    final var withdraw = CLIENT.withdrawPartialUnstaking(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, ESCROW_TOKENS, PARTIAL_UNSTAKE, FEE_PAYER, DESTINATION_TOKENS);
    assertTrue(keys(withdraw).contains(PARTIAL_UNSTAKE));
    assertTrue(keys(withdraw).contains(DESTINATION_TOKENS));

    // the three are distinct instructions over overlapping accounts
    assertNotEquals(open.data()[0], merge.data()[0]);
    assertNotEquals(merge.data()[0], withdraw.data()[0]);
  }

  /// `toggleMaxLock` and `extendLockDuration` both take the same three keys, so
  /// only the data separates them; and the convenience overloads must default
  /// the locker and escrow to the client's own.
  @Test
  void lockManagementOverloadsDefaultToTheClientsEscrow() {
    final var explicitToggle = CLIENT.toggleMaxLock(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, true);
    assertEquals(explicitToggle, CLIENT.toggleMaxLock(true));

    final var explicitExtend = CLIENT.extendLockDuration(
        JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, 86_400L);
    assertEquals(explicitExtend, CLIENT.extendLockDuration(86_400L));

    // same accounts, different instruction
    assertEquals(keys(explicitToggle), keys(explicitExtend));
    assertNotEquals(explicitToggle.data()[0], explicitExtend.data()[0]);

    // the flag and the duration are data
    assertFalse(Arrays.equals(
        explicitToggle.data(),
        CLIENT.toggleMaxLock(JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), OWNER, false).data()));

    // the owner-keyed overload derives that owner's escrow, not the client's
    final var otherOwner = key(0x31);
    final var forOther = CLIENT.toggleMaxLock(otherOwner, true);
    assertTrue(keys(forOther).contains(JUPITER_ACCOUNTS.deriveEscrow(otherOwner).publicKey()));
    assertFalse(keys(forOther).contains(CLIENT.escrowKey()));
  }

  /// The merkle-distributor claim invokes its own program and threads the
  /// escrow through, so a claim stakes into the caller's escrow.
  @Test
  void newClaimAndStakeInvokesTheDistributorAndStakesIntoTheEscrow() {
    final var distributor = key(0x41);
    final var claimStatus = key(0x42);
    final var from = key(0x43);
    final var operator = key(0x44);

    final var ix = CLIENT.newClaimAndStake(
        claimStatus, from, distributor, operator, ESCROW_TOKENS,
        SOLANA_ACCOUNTS.tokenProgram(), 100L, 900L, new byte[][]{new byte[32]});

    assertEquals(JUPITER_ACCOUNTS.invokedMerkleDistributorProgram(), ix.programId());

    final var accounts = keys(ix);
    assertTrue(accounts.contains(distributor), "the distributor");
    assertTrue(accounts.contains(claimStatus), "the claim status account");
    assertTrue(accounts.contains(from), "the distributor's token account");
    assertTrue(accounts.contains(operator));
    assertTrue(accounts.contains(ESCROW_TOKENS));
    // the claim stakes into this client's escrow, under the vote program
    assertTrue(accounts.contains(CLIENT.escrowKey()));
    assertTrue(accounts.contains(JUPITER_ACCOUNTS.lockerKey()));
    assertTrue(accounts.contains(JUPITER_ACCOUNTS.voteProgram()));
    assertTrue(accounts.contains(OWNER), "the claimant is the escrow owner");

    // the four caller-supplied keys land in four distinct slots
    assertEquals(4, java.util.Set.of(
        accounts.indexOf(distributor),
        accounts.indexOf(claimStatus),
        accounts.indexOf(from),
        accounts.indexOf(operator)).size());
  }

  // ---------------------------------------------------------------------------
  // convenience overloads
  // ---------------------------------------------------------------------------

  /// A fetched escrow, carrying its own address and the three keys the
  /// `Escrow`-taking overloads read out of it.
  private static Escrow escrow(final PublicKey address,
                               final PublicKey locker,
                               final PublicKey owner,
                               final PublicKey tokens) {
    return new Escrow(
        address,
        software.sava.core.programs.Discriminator.toDiscriminator(0, 1, 2, 3, 4, 5, 6, 7),
        locker, owner, 255, tokens,
        1_000L, 0L, 0L, owner, false, 0L, 0L, new java.math.BigInteger[0]);
  }

  /// The `Escrow`-taking overloads exist so a caller can act on an account it
  /// just fetched rather than re-deriving four keys. Each field has to reach its
  /// own slot: locker, escrow address, owner and token account are all
  /// `PublicKey`, so a transposition compiles and produces a plausible
  /// instruction.
  @Test
  void escrowOverloadsReadEveryFieldIntoItsOwnSlot() {
    final var address = key(0x51);
    final var locker = key(0x52);
    final var owner = key(0x53);
    final var tokens = key(0x54);
    final var payer = key(0x55);
    final var destination = key(0x56);
    final var fetched = escrow(address, locker, owner, tokens);

    final var fromEscrow = CLIENT.withdraw(fetched, payer, destination);
    final var explicit = CLIENT.withdraw(locker, address, owner, tokens, payer, destination);
    assertEquals(keys(explicit), keys(fromEscrow),
        "the escrow's fields must land where the explicit call puts them");

    // every field participates — moving any one moves the instruction
    assertNotEquals(keys(fromEscrow),
        keys(CLIENT.withdraw(escrow(address, key(0x57), owner, tokens), payer, destination)));
    assertNotEquals(keys(fromEscrow),
        keys(CLIENT.withdraw(escrow(key(0x57), locker, owner, tokens), payer, destination)));
    assertNotEquals(keys(fromEscrow),
        keys(CLIENT.withdraw(escrow(address, locker, key(0x57), tokens), payer, destination)));
    assertNotEquals(keys(fromEscrow),
        keys(CLIENT.withdraw(escrow(address, locker, owner, key(0x57)), payer, destination)));

    assertEquals(JUPITER_ACCOUNTS.invokedVoteProgram(), fromEscrow.programId());
  }

  /// The single-argument `withdraw(Escrow)` defaults the payer to the escrow's
  /// *owner* and the destination to the client's own ATA — not the escrow's.
  @Test
  void singleArgWithdrawDefaultsThePayerToTheEscrowOwner() {
    final var address = key(0x51);
    final var locker = key(0x52);
    final var owner = key(0x53);
    final var tokens = key(0x54);
    final var fetched = escrow(address, locker, owner, tokens);

    assertEquals(
        keys(CLIENT.withdraw(fetched, owner, CLIENT.escrowOwnerKeyATA())),
        keys(CLIENT.withdraw(fetched)));

    // the destination is the owner's ATA, and is not the escrow's token account
    final var accounts = keys(CLIENT.withdraw(fetched));
    assertTrue(accounts.contains(CLIENT.escrowOwnerKeyATA()));
    assertNotEquals(tokens, CLIENT.escrowOwnerKeyATA());
  }

  /// The no-argument and payer-only overloads fall back to the client's own
  /// escrow identity rather than requiring it to be passed back in.
  @Test
  void payerOverloadsFallBackToTheClientsIdentity() {
    final var payer = key(0x55);

    assertEquals(
        keys(CLIENT.withdraw(JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(), CLIENT.escrowOwnerKey(),
            CLIENT.escrowATA(), payer, CLIENT.escrowOwnerKeyATA())),
        keys(CLIENT.withdraw(payer)));

    // no-arg defaults the payer to the escrow owner
    assertEquals(keys(CLIENT.withdraw(CLIENT.escrowOwnerKey())), keys(CLIENT.withdraw()));

    // and a different payer really does change the instruction
    assertNotEquals(keys(CLIENT.withdraw()), keys(CLIENT.withdraw(payer)));
  }

  /// The remaining convenience overloads follow the same two shapes: read the
  /// four keys out of a fetched `Escrow`, or fall back to the client's own
  /// escrow identity. Each is checked against the explicit call it delegates to,
  /// so a field reaching the wrong slot shows up as a different account list.
  @Test
  void theRemainingOverloadsDelegateWithoutReshufflingKeys() {
    final var address = key(0x51);
    final var locker = key(0x52);
    final var owner = key(0x53);
    final var tokens = key(0x54);
    final var payer = key(0x55);
    final var fetched = escrow(address, locker, owner, tokens);

    // Escrow-taking: locker, escrow address and owner, in that order
    assertEquals(
        keys(CLIENT.extendLockDuration(locker, address, owner, 42L)),
        keys(CLIENT.extendLockDuration(fetched, 42L)));
    assertEquals(
        keys(CLIENT.toggleMaxLock(locker, address, owner, true)),
        keys(CLIENT.toggleMaxLock(fetched, true)));

    // identity fallbacks
    assertEquals(
        keys(CLIENT.extendLockDuration(JUPITER_ACCOUNTS.lockerKey(), CLIENT.escrowKey(),
            CLIENT.escrowOwnerKey(), 42L)),
        keys(CLIENT.extendLockDuration(42L)));
    assertEquals(
        keys(CLIENT.castVote(CLIENT.escrowKey(), CLIENT.escrowOwnerKey(), key(0x58), key(0x59), 1)),
        keys(CLIENT.castVote(key(0x58), key(0x59), 1)));
    assertEquals(
        keys(CLIENT.setVoteDelegate(CLIENT.escrowOwnerKey(), CLIENT.escrowKey(), key(0x5A))),
        keys(CLIENT.setVoteDelegate(key(0x5A))));

    // increaseLockedAmount's short form supplies the escrow's ATA as the
    // destination and the owner's as the source — swapping them would move
    // tokens the wrong way, and both are same-typed
    final var shortForm = keys(CLIENT.increaseLockedAmount(payer, 1_000L));
    assertEquals(
        keys(CLIENT.increaseLockedAmount(CLIENT.escrowATA(), payer, CLIENT.escrowOwnerKeyATA(), 1_000L)),
        shortForm);
    assertNotEquals(
        keys(CLIENT.increaseLockedAmount(CLIENT.escrowOwnerKeyATA(), payer, CLIENT.escrowATA(), 1_000L)),
        shortForm,
        "source and destination token accounts must not be interchangeable");

    // the duration/side/amount arguments are data: they must not move accounts
    assertEquals(keys(CLIENT.extendLockDuration(42L)), keys(CLIENT.extendLockDuration(99L)));
    assertEquals(keys(CLIENT.toggleMaxLock(fetched, true)), keys(CLIENT.toggleMaxLock(fetched, false)));
    assertNotEquals(
        CLIENT.toggleMaxLock(fetched, true).data()[8],
        CLIENT.toggleMaxLock(fetched, false).data()[8]);

    // every one of these is a locked-voter instruction
    for (final var ix : List.of(
        CLIENT.extendLockDuration(fetched, 42L),
        CLIENT.toggleMaxLock(fetched, true),
        CLIENT.castVote(key(0x58), key(0x59), 1),
        CLIENT.setVoteDelegate(key(0x5A)),
        CLIENT.increaseLockedAmount(payer, 1_000L))) {
      assertEquals(JUPITER_ACCOUNTS.invokedVoteProgram(), ix.programId());
    }
  }

  /// The partial-unstaking family shares the `Escrow` shape, and its short forms
  /// default the payer to the client's own owner.
  @Test
  void partialUnstakingOverloadsDelegateConsistently() {
    final var address = key(0x51);
    final var locker = key(0x52);
    final var owner = key(0x53);
    final var tokens = key(0x54);
    final var partialUnstake = key(0x5B);
    final var fetched = escrow(address, locker, owner, tokens);

    assertEquals(
        keys(CLIENT.openPartialUnstaking(locker, address, owner, partialUnstake, 500L, "memo")),
        keys(CLIENT.openPartialUnstaking(fetched, partialUnstake, 500L, "memo")));
    assertEquals(
        keys(CLIENT.mergePartialUnstaking(locker, address, owner, partialUnstake)),
        keys(CLIENT.mergePartialUnstaking(fetched, partialUnstake)));

    // the memo is data, not an account
    assertEquals(
        keys(CLIENT.openPartialUnstaking(fetched, partialUnstake, 500L, "memo")),
        keys(CLIENT.openPartialUnstaking(fetched, partialUnstake, 500L, "another memo")));
    assertFalse(java.util.Arrays.equals(
        CLIENT.openPartialUnstaking(fetched, partialUnstake, 500L, "memo").data(),
        CLIENT.openPartialUnstaking(fetched, partialUnstake, 500L, "another memo").data()));
  }
}
