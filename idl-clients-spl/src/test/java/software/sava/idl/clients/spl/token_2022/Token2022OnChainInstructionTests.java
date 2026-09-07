package software.sava.idl.clients.spl.token_2022;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.SolanaAccounts;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;
import software.sava.idl.clients.spl.token_2022.gen.Token2022Program;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// Real mainnet Token-2022 instructions, decoded and rebuilt with the generated client.
///
/// Every other test of this program compares one encoder against another.
/// [Token2022ReferenceEncodingTests] is the strongest of them — 155 vectors built through
/// solana-program/token-2022's own generated JavaScript client — but that client is rendered by
/// `@codama/renderers-js` from the same `idl.json` this repository generates from, so the two are
/// second opinions on one document rather than on the program. [Token2022ProgramTests] and
/// [Token2022InstructionsTests] are narrower still. None of them can testify that the *deployed*
/// program accepts what the client emits. The fixtures under `/token_2022/mainnet/` can: each one
/// is an instruction the program **executed** — captured with `getTransaction` from mainnet,
/// top-level and inner (CPI) alike — recorded with its data, its account list, and the
/// signer/writable flags the transaction message resolved for each account.
///
/// Four claims are made of every fixture:
///
/// 1. the instruction's `*_DISCRIMINATOR` matches the captured data and **no other instruction's
///    constant does** — every one of the ninety-nine is compared against the bytes, so the
///    two-byte constants an extension sub-instruction now carries are checked against the
///    siblings they used to collide with;
/// 2. `*IxData.read(byte[], int)` decodes it, `l()` accounts for every captured byte, and `write`
///    reproduces them exactly — the one instruction whose IDL leaves an argument out is named in
///    [#UNDER_DECLARED_ARGUMENTS] and reported rather than excused;
/// 3. the generated `List<AccountMeta>` builder overload, fed the decoded fields, emits byte-equal
///    data — so the decode is not merely self-consistent;
/// 4. the generated `*Keys` helper, fed the captured addresses, reproduces the captured account
///    list. Where it cannot, the difference is classified into [AccountDifference] and pinned
///    per fixture in [#EXPECTED_DIFFERENCES]; anything else fails.
///
/// The helpers are reached by reflection, deliberately. A hand-written table of ninety-nine
/// decode/rebuild lambdas would be a second transcription of the generator's output and would
/// drift from it silently; resolving `<name>Keys`, `<name>(AccountMeta, List<AccountMeta>, …)` and
/// `<Name>IxData` by name instead makes a renamed or dropped member a failure here.
///
/// These fixtures do not expire on their own. The program is upgradeable and the mainnet ELF can
/// be replaced by feature activation as much as by transaction, so what keeps them good is that no
/// deploy so far has moved a dispatch byte or an argument layout. A redeploy is the trigger to
/// re-capture.
final class Token2022OnChainInstructionTests {

  private static final String DIR = "/token_2022/mainnet/";
  private static final PublicKey TOKEN_2022 =
      PublicKey.fromBase58Encoded("TokenzQdBNbLqP5VEhdkAS6EPFLC1PHnBqCXEpPxuEb");

  /// Named reasons an on-chain account list can differ from what a `*Keys` helper builds.
  ///
  /// The first four are consequences of how a *transaction message* encodes accounts, not of the
  /// instruction. Signer and writable are properties of a key across the whole message, so an
  /// account any instruction needs writable is writable for all of them and the fee payer is
  /// always both; and a signature granted by `invoke_signed` at a CPI boundary appears nowhere in
  /// the message at all. A helper describes one instruction in isolation, so it can only ever be
  /// the narrower of the two. [#MULTISIG_AUTHORITY] and [#REMAINING_ACCOUNT] are what the IDL
  /// genuinely cannot express. The last four are the directions that would be defects.
  enum AccountDifference {
    /// The key is the transaction's fee payer, which the message always marks signer and
    /// writable, whatever the instruction needs of it.
    FEE_PAYER,
    /// The helper builds a non-signer; the message marks the key a signer, because it signs for
    /// another instruction in the same transaction.
    SIGNS_TRANSACTION,
    /// The helper builds a read-only account; the message marks the key writable, because
    /// another instruction in the same transaction writes to it.
    MESSAGE_WRITABLE,
    /// The helper builds a signer, the message does not mark the key one, and the instruction is
    /// an **inner** one: the invoking program signed for it with `invoke_signed`, so the
    /// signature is granted at the CPI boundary and never appears in the message header.
    CPI_SIGNER,
    /// The helper builds a signer and the key in that slot is a Token-2022 **multisignature
    /// account**, which signs nothing itself: this is `isSigner: "either"` resolved the second
    /// way, with the member signers following as remaining accounts. Listed file by file in
    /// [#multisigOwnerOccurrences()].
    MULTISIG_AUTHORITY,
    /// The helper builds a signer, the key is not a multisig, and the instruction is top-level,
    /// so nothing grants the signature. The program would have rejected it, so no fixture is
    /// expected to carry it — it exists to keep [#MULTISIG_AUTHORITY] and [#CPI_SIGNER] honest
    /// about what they claim.
    AUTHORITY_DID_NOT_SIGN,
    /// An account beyond the helper's declared list. Token-2022 appends multisig member signers,
    /// the source accounts a fee harvest or withdrawal sweeps, transfer-hook extra accounts, and
    /// — as two captures here show — a duplicated authority; none of that is in the IDL.
    REMAINING_ACCOUNT,
    /// The helper builds a writable account the message marks read-only. Nothing on chain
    /// explains this direction: a client following the helper asks for write access the program
    /// did not need. One instruction carries it, and the IDL is where it comes from — see
    /// [#theScaledUiMultiplierAuthorityIsDeclaredWritableAndIsNot()].
    DECLARED_WRITABLE_BUT_READ_ONLY,
    /// The helper filled this position with a constant of its own and the instruction used a
    /// different account, so no argument of the helper can express what was captured — see
    /// [#theEmptyAccountProofSlotCannotBeOverridden()].
    HELPER_DEFAULT_NOT_OVERRIDABLE,
    /// The helper put a different address in a position it takes as a parameter.
    ADDRESS_MISMATCH,
    /// The helper builds more accounts than the instruction carried, after every combination of
    /// omitted optional accounts was tried — see [#syncNativeAppendsARentSysvarThatIsNotUsed()].
    MISSING_ACCOUNT
  }

  /// Captured instructions the generated reader cannot fully account for, mapped to the number of
  /// bytes it does account for. Each is an argument the **IDL** does not declare, so it is
  /// reported rather than papered over: the reader is still checked, byte for byte, over the
  /// prefix it claims, and the remainder is pinned by the named test beside it.
  private static final Map<String, Integer> UNDER_DECLARED_ARGUMENTS = Map.of(
      "getAccountDataSize-1.json", 1);

  /// Per-fixture expectations, keyed by file name, valued as `index:KIND[,KIND]` entries joined by
  /// `;`. A fixture absent from this map must match its helper exactly. Regenerate deliberately:
  /// an entry disappearing means a helper improved, an entry appearing means it did not.
  private static final Map<String, String> EXPECTED_DIFFERENCES = Map.ofEntries(
      Map.entry("initializeMint-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeMint-2.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeAccount-1.json", "2:FEE_PAYER"),
      Map.entry("initializeMultisig-1.json", "0:SIGNS_TRANSACTION;2:REMAINING_ACCOUNT;3:REMAINING_ACCOUNT;4:REMAINING_ACCOUNT;5:REMAINING_ACCOUNT"),
      Map.entry("initializeMultisig-2.json", "0:SIGNS_TRANSACTION;2:REMAINING_ACCOUNT;3:REMAINING_ACCOUNT"),
      Map.entry("transfer-1.json", "2:FEE_PAYER"),
      Map.entry("approve-1.json", "1:MESSAGE_WRITABLE;2:FEE_PAYER"),
      Map.entry("revoke-1.json", "1:FEE_PAYER"),
      Map.entry("setAuthority-1.json", "1:MULTISIG_AUTHORITY;2:REMAINING_ACCOUNT"),
      Map.entry("setAuthority-2.json", "0:SIGNS_TRANSACTION;1:CPI_SIGNER,MESSAGE_WRITABLE;2:REMAINING_ACCOUNT"),
      Map.entry("setAuthority-3.json", "1:MESSAGE_WRITABLE"),
      Map.entry("mintTo-1.json", "2:MULTISIG_AUTHORITY;3:REMAINING_ACCOUNT"),
      Map.entry("mintTo-2.json", "2:CPI_SIGNER"),
      Map.entry("burn-1.json", "2:CPI_SIGNER"),
      Map.entry("closeAccount-1.json", "1:FEE_PAYER;2:FEE_PAYER"),
      Map.entry("closeAccount-2.json", "1:FEE_PAYER;2:FEE_PAYER;3:REMAINING_ACCOUNT"),
      Map.entry("closeAccount-3.json", "1:FEE_PAYER;3:REMAINING_ACCOUNT;4:REMAINING_ACCOUNT"),
      Map.entry("freezeAccount-1.json", "1:MESSAGE_WRITABLE;2:FEE_PAYER"),
      Map.entry("freezeAccount-2.json", "2:CPI_SIGNER;3:REMAINING_ACCOUNT"),
      Map.entry("thawAccount-1.json", "1:MESSAGE_WRITABLE;2:FEE_PAYER"),
      Map.entry("thawAccount-2.json", "2:CPI_SIGNER;3:REMAINING_ACCOUNT"),
      Map.entry("transferChecked-1.json", "1:MESSAGE_WRITABLE;3:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("approveChecked-1.json", "1:MESSAGE_WRITABLE;3:FEE_PAYER"),
      Map.entry("mintToChecked-1.json", "2:MULTISIG_AUTHORITY;3:REMAINING_ACCOUNT"),
      Map.entry("mintToChecked-2.json", "2:FEE_PAYER"),
      Map.entry("burnChecked-1.json", "2:FEE_PAYER"),
      Map.entry("syncNative-1.json", "1:MISSING_ACCOUNT"),
      Map.entry("initializeAccount3-1.json", "1:MESSAGE_WRITABLE"),
      Map.entry("initializeMint2-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeMint2-2.json", "0:SIGNS_TRANSACTION"),
      Map.entry("getAccountDataSize-1.json", "0:MESSAGE_WRITABLE"),
      Map.entry("initializeMintCloseAuthority-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeTransferFeeConfig-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeTransferFeeConfig-2.json", "0:SIGNS_TRANSACTION"),
      Map.entry("transferCheckedWithFee-1.json", "3:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("withdrawWithheldTokensFromMint-1.json", "2:CPI_SIGNER"),
      Map.entry("withdrawWithheldTokensFromMint-2.json", "2:FEE_PAYER;3:REMAINING_ACCOUNT"),
      Map.entry("withdrawWithheldTokensFromAccounts-1.json", "0:MESSAGE_WRITABLE;2:CPI_SIGNER;3:REMAINING_ACCOUNT;4:REMAINING_ACCOUNT"),
      Map.entry("withdrawWithheldTokensFromAccounts-2.json", "0:MESSAGE_WRITABLE;2:CPI_SIGNER;3:REMAINING_ACCOUNT"),
      Map.entry("withdrawWithheldTokensFromAccounts-3.json", "0:MESSAGE_WRITABLE;2:FEE_PAYER;3:REMAINING_ACCOUNT;4:REMAINING_ACCOUNT;5:REMAINING_ACCOUNT"),
      Map.entry("harvestWithheldTokensToMint-1.json", "1:REMAINING_ACCOUNT;2:REMAINING_ACCOUNT"),
      Map.entry("harvestWithheldTokensToMint-2.json", "1:REMAINING_ACCOUNT"),
      Map.entry("harvestWithheldTokensToMint-3.json", "1:REMAINING_ACCOUNT;2:REMAINING_ACCOUNT;3:REMAINING_ACCOUNT"),
      Map.entry("setTransferFee-1.json", "0:SIGNS_TRANSACTION;1:FEE_PAYER;2:REMAINING_ACCOUNT"),
      Map.entry("setTransferFee-2.json", "1:FEE_PAYER"),
      Map.entry("initializeConfidentialTransferMint-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("configureConfidentialTransferAccount-1.json", "3:FEE_PAYER"),
      Map.entry("emptyConfidentialTransferAccount-1.json", "1:HELPER_DEFAULT_NOT_OVERRIDABLE,MESSAGE_WRITABLE;2:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("confidentialDeposit-1.json", "2:FEE_PAYER"),
      Map.entry("confidentialWithdraw-1.json", "1:MESSAGE_WRITABLE;2:MESSAGE_WRITABLE;3:MESSAGE_WRITABLE;4:FEE_PAYER"),
      Map.entry("confidentialTransfer-1.json", "3:MESSAGE_WRITABLE;4:MESSAGE_WRITABLE;5:MESSAGE_WRITABLE;6:MESSAGE_WRITABLE"),
      Map.entry("applyConfidentialPendingBalance-1.json", "1:FEE_PAYER"),
      Map.entry("disableConfidentialCredits-1.json", "1:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("disableNonConfidentialCredits-1.json", "1:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("initializeDefaultAccountState-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("updateDefaultAccountState-1.json", "1:FEE_PAYER"),
      Map.entry("reallocate-1.json", "3:FEE_PAYER"),
      Map.entry("reallocate-2.json", "3:FEE_PAYER"),
      Map.entry("reallocate-3.json", "3:FEE_PAYER;4:REMAINING_ACCOUNT"),
      Map.entry("enableMemoTransfers-1.json", "1:FEE_PAYER"),
      Map.entry("enableMemoTransfers-2.json", "0:SIGNS_TRANSACTION;1:MULTISIG_AUTHORITY;2:REMAINING_ACCOUNT;3:REMAINING_ACCOUNT;4:REMAINING_ACCOUNT"),
      Map.entry("disableMemoTransfers-1.json", "1:FEE_PAYER"),
      Map.entry("initializeNonTransferableMint-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeInterestBearingMint-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("updateRateInterestBearingMint-1.json", "2:REMAINING_ACCOUNT"),
      Map.entry("enableCpiGuard-1.json", "0:SIGNS_TRANSACTION;1:MULTISIG_AUTHORITY;2:REMAINING_ACCOUNT;3:REMAINING_ACCOUNT;4:REMAINING_ACCOUNT"),
      Map.entry("enableCpiGuard-2.json", "1:FEE_PAYER"),
      Map.entry("initializePermanentDelegate-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeTransferHook-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("updateTransferHook-1.json", "0:SIGNS_TRANSACTION;1:MESSAGE_WRITABLE"),
      Map.entry("updateTransferHook-2.json", "1:CPI_SIGNER;2:REMAINING_ACCOUNT"),
      Map.entry("initializeConfidentialTransferFee-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("withdrawExcessLamports-1.json", "1:FEE_PAYER;2:FEE_PAYER"),
      Map.entry("initializeMetadataPointer-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeGroupPointer-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("initializeGroupMemberPointer-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("updateGroupMemberPointer-1.json", "0:SIGNS_TRANSACTION;1:MESSAGE_WRITABLE"),
      Map.entry("initializeConfidentialMintBurn-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("confidentialMint-1.json", "5:FEE_PAYER"),
      Map.entry("confidentialBurn-1.json", "5:FEE_PAYER"),
      Map.entry("initializeScaledUiAmountMint-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("updateMultiplierScaledUiMint-1.json", "1:CPI_SIGNER,DECLARED_WRITABLE_BUT_READ_ONLY"),
      Map.entry("initializePausableConfig-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("pause-1.json", "1:CPI_SIGNER"),
      Map.entry("resume-1.json", "1:CPI_SIGNER"),
      Map.entry("initializeTokenMetadata-1.json", "0:SIGNS_TRANSACTION;2:MESSAGE_WRITABLE,SIGNS_TRANSACTION;3:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("initializeTokenMetadata-2.json", "0:SIGNS_TRANSACTION;2:MESSAGE_WRITABLE,SIGNS_TRANSACTION;3:CPI_SIGNER"),
      Map.entry("initializeTokenMetadata-3.json", "0:SIGNS_TRANSACTION;2:MESSAGE_WRITABLE,SIGNS_TRANSACTION;3:CPI_SIGNER"),
      Map.entry("updateTokenMetadataField-1.json", "1:FEE_PAYER"),
      Map.entry("updateTokenMetadataField-2.json", "1:CPI_SIGNER"),
      Map.entry("updateTokenMetadataField-3.json", "1:CPI_SIGNER"),
      Map.entry("removeTokenMetadataKey-1.json", "1:CPI_SIGNER"),
      Map.entry("removeTokenMetadataKey-2.json", "1:CPI_SIGNER"),
      Map.entry("removeTokenMetadataKey-3.json", "1:CPI_SIGNER,MESSAGE_WRITABLE"),
      Map.entry("updateTokenMetadataUpdateAuthority-1.json", "0:SIGNS_TRANSACTION;1:CPI_SIGNER"),
      Map.entry("initializeTokenGroup-1.json", "0:SIGNS_TRANSACTION;1:MESSAGE_WRITABLE,SIGNS_TRANSACTION;2:FEE_PAYER"),
      Map.entry("initializeTokenGroupMember-1.json", "0:SIGNS_TRANSACTION;1:MESSAGE_WRITABLE,SIGNS_TRANSACTION;2:FEE_PAYER;4:FEE_PAYER"),
      Map.entry("unwrapLamports-1.json", "2:FEE_PAYER"),
      Map.entry("initializePermissionedBurn-1.json", "0:SIGNS_TRANSACTION"),
      Map.entry("permissionedBurnChecked-1.json", "2:FEE_PAYER")
  );

  /// Every captured multisignature-authority occurrence: fixture file to `index:address` of the
  /// `Multisig` account that stood in the authority slot.
  private static final Map<String, String> MULTISIG_OWNER_FIXTURES = Map.ofEntries(
      Map.entry("setAuthority-1.json", "1:5ugNLveLjrSgaeLP3eLwYuMjWWuUKCawZMyUwecqJTYs"),
      Map.entry("mintTo-1.json", "2:8Jornc27vtAYPkwDzsZVgLQchAYyC8nD7aCNPCDV8Qk2"),
      Map.entry("mintToChecked-1.json", "2:8Jornc27vtAYPkwDzsZVgLQchAYyC8nD7aCNPCDV8Qk2"),
      Map.entry("enableMemoTransfers-2.json", "1:9HWrvk9p1efsTFWNSymXb6rMf9RRuefFNMXyQk3nBh4u"),
      Map.entry("enableCpiGuard-1.json", "1:9HWrvk9p1efsTFWNSymXb6rMf9RRuefFNMXyQk3nBh4u")
  );

  // ---------------------------------------------------------------- fixtures

  /// `multisig` is recorded at capture time and is the test the program itself makes in
  /// `validate_owner`: an account owned by Token-2022 whose data is exactly `Multisig::LEN`
  /// (355) bytes.
  record OnChainAccount(PublicKey pubkey,
                        boolean signer,
                        boolean writable,
                        boolean feePayer,
                        boolean multisig) {
  }

  record Fixture(String file,
                 String instruction,
                 String signature,
                 long slot,
                 String position,
                 PublicKey programId,
                 List<OnChainAccount> accounts,
                 byte[] data) {

    @Override
    public String toString() {
      return file;
    }
  }

  private static byte[] resource(final String name) {
    try (var in = Token2022OnChainInstructionTests.class.getResourceAsStream(DIR + name)) {
      assertNotNull(in, "fixture " + DIR + name + " is missing");
      return in.readAllBytes();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static final class AccountParser implements FieldBufferPredicate, Supplier<OnChainAccount> {

    private PublicKey pubkey;
    private boolean signer;
    private boolean writable;
    private boolean feePayer;
    private boolean multisig;

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("pubkey", buf, offset, len)) {
        pubkey = PublicKey.fromBase58Encoded(ji.readString());
      } else if (fieldEquals("signer", buf, offset, len)) {
        signer = ji.readBoolean();
      } else if (fieldEquals("writable", buf, offset, len)) {
        writable = ji.readBoolean();
      } else if (fieldEquals("feePayer", buf, offset, len)) {
        feePayer = ji.readBoolean();
      } else if (fieldEquals("multisig", buf, offset, len)) {
        multisig = ji.readBoolean();
      } else {
        ji.skip();
      }
      return true;
    }

    @Override
    public OnChainAccount get() {
      return new OnChainAccount(pubkey, signer, writable, feePayer, multisig);
    }
  }

  private static final class FixtureParser implements FieldBufferPredicate {

    private String instruction;
    private String signature;
    private long slot;
    private String position;
    private PublicKey programId;
    private final List<OnChainAccount> accounts = new ArrayList<>();
    private byte[] data;

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("instruction", buf, offset, len)) {
        instruction = ji.readString();
      } else if (fieldEquals("signature", buf, offset, len)) {
        signature = ji.readString();
      } else if (fieldEquals("slot", buf, offset, len)) {
        slot = ji.readLong();
      } else if (fieldEquals("position", buf, offset, len)) {
        position = ji.readString();
      } else if (fieldEquals("programId", buf, offset, len)) {
        programId = PublicKey.fromBase58Encoded(ji.readString());
      } else if (fieldEquals("accounts", buf, offset, len)) {
        while (ji.readArray()) {
          accounts.add(ji.parseObject(new AccountParser()));
        }
      } else if (fieldEquals("data", buf, offset, len)) {
        data = Base64.getDecoder().decode(ji.readString());
      } else {
        ji.skip();
      }
      return true;
    }

    Fixture toFixture(final String file) {
      return new Fixture(file, instruction, signature, slot, position, programId,
          List.copyOf(accounts), data);
    }
  }

  private static final class ManifestEntryParser implements FieldBufferPredicate, Supplier<String> {

    private String file;

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("file", buf, offset, len)) {
        file = ji.readString();
      } else {
        ji.skip();
      }
      return true;
    }

    @Override
    public String get() {
      return file;
    }
  }

  private static final class ManifestParser implements FieldBufferPredicate {

    private final List<String> files = new ArrayList<>();

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("fixtures", buf, offset, len)) {
        while (ji.readArray()) {
          files.add(ji.parseObject(new ManifestEntryParser()));
        }
      } else {
        ji.skip();
      }
      return true;
    }
  }

  private static List<Fixture> loadFixtures() {
    final var manifest = new ManifestParser();
    JsonIterator.parse(resource("manifest.json")).testObject(manifest);
    assertFalse(manifest.files.isEmpty(), "the manifest lists no fixtures");
    final var fixtures = new ArrayList<Fixture>(manifest.files.size());
    for (final var file : manifest.files) {
      final var parser = new FixtureParser();
      JsonIterator.parse(resource(file)).testObject(parser);
      fixtures.add(parser.toFixture(file));
    }
    return List.copyOf(fixtures);
  }

  private static final List<Fixture> FIXTURES = loadFixtures();

  /// Instructions the IDL declares that this repository generates no client for. `batch` is the
  /// only one, excluded by `main_net_programs.json`, and it runs on mainnet — see
  /// [#theBatchInstructionIsExcludedByConfiguration()].
  private static final Set<String> NOT_GENERATED = Set.of("batch");

  private static final List<Fixture> GENERATED = FIXTURES.stream()
      .filter(f -> !NOT_GENERATED.contains(f.instruction()))
      .toList();

  // ------------------------------------------------------- generated members

  /// `initializeAccount2` -> `INITIALIZE_ACCOUNT_2_DISCRIMINATOR`: a break before every capital and
  /// before every digit run, which is what the generator emits.
  private static String constantName(final String instruction) {
    final var out = new StringBuilder(instruction.length() + 8);
    char previous = 0;
    for (int i = 0; i < instruction.length(); ++i) {
      final char c = instruction.charAt(i);
      final boolean boundary = i > 0
          && (Character.isUpperCase(c) || (Character.isDigit(c) && !Character.isDigit(previous)));
      if (boundary) {
        out.append('_');
      }
      out.append(Character.toUpperCase(c));
      previous = c;
    }
    return out.append("_DISCRIMINATOR").toString();
  }

  private static Map<String, Discriminator> discriminators() {
    final var byField = new HashMap<String, Discriminator>();
    for (final var field : Token2022Program.class.getFields()) {
      if (field.getType() == Discriminator.class && Modifier.isStatic(field.getModifiers())) {
        try {
          byField.put(field.getName(), (Discriminator) field.get(null));
        } catch (final IllegalAccessException e) {
          throw new AssertionError(e);
        }
      }
    }
    return Map.copyOf(byField);
  }

  private static final Map<String, Discriminator> DISCRIMINATORS = discriminators();

  private static Discriminator discriminator(final String instruction) {
    final var name = constantName(instruction);
    final var d = DISCRIMINATORS.get(name);
    assertNotNull(d, "Token2022Program." + name + " is missing for instruction " + instruction);
    return d;
  }

  private static Class<?> ixDataClass(final String instruction) {
    final var simple = Character.toUpperCase(instruction.charAt(0)) + instruction.substring(1) + "IxData";
    for (final var nested : Token2022Program.class.getClasses()) {
      if (nested.getSimpleName().equals(simple)) {
        return nested;
      }
    }
    throw new AssertionError("Token2022Program." + simple + " is missing");
  }

  private static Method readMethod(final Class<?> ixData) {
    try {
      return ixData.getMethod("read", byte[].class, int.class);
    } catch (final NoSuchMethodException e) {
      throw new AssertionError(ixData.getSimpleName() + ".read(byte[], int) is missing", e);
    }
  }

  private static boolean isDispatchComponent(final RecordComponent c) {
    final var n = c.getName();
    return n.equals("discriminator") || n.endsWith("Discriminator");
  }

  /// Components that feed the builder: everything after the leading dispatch fields, minus the
  /// `_`-prefixed encodings the generator keeps beside a `String` or a vector.
  private static List<RecordComponent> argComponents(final Class<?> ixData) {
    final var all = ixData.getRecordComponents();
    final var out = new ArrayList<RecordComponent>(all.length);
    int i = 0;
    while (i < all.length && isDispatchComponent(all[i])) {
      ++i;
    }
    for (; i < all.length; ++i) {
      assertFalse(isDispatchComponent(all[i]), ixData.getSimpleName()
          + " has a dispatch component after its arguments: " + all[i].getName());
      if (!all[i].getName().startsWith("_")) {
        out.add(all[i]);
      }
    }
    return out;
  }

  private static Method keysMethod(final String instruction) {
    for (final var m : Token2022Program.class.getMethods()) {
      if (m.getName().equals(instruction + "Keys") && Modifier.isStatic(m.getModifiers())) {
        return m;
      }
    }
    throw new AssertionError("Token2022Program." + instruction + "Keys(..) is missing");
  }

  private static Method builderMethod(final String instruction, final int argCount) {
    for (final var m : Token2022Program.class.getMethods()) {
      if (!m.getName().equals(instruction) || !Modifier.isStatic(m.getModifiers())) {
        continue;
      }
      final var p = m.getParameterTypes();
      if (p.length == argCount + 2 && p[0] == AccountMeta.class && p[1] == List.class) {
        return m;
      }
    }
    throw new AssertionError("Token2022Program." + instruction
        + "(AccountMeta, List<AccountMeta>, .. " + argCount + " args) is missing");
  }

  // ------------------------------------------------------------- key solving

  private static final PublicKey[] SENTINELS = sentinels();

  private static PublicKey[] sentinels() {
    final var keys = new PublicKey[32];
    for (int i = 0; i < keys.length; ++i) {
      final byte[] b = new byte[PublicKey.PUBLIC_KEY_LENGTH];
      b[0] = (byte) 0xEE;
      b[1] = (byte) i;
      keys[i] = PublicKey.createPubKey(b);
    }
    return keys;
  }

  /// Which parameters of a `*Keys` helper the generator lets go null, discovered by trying.
  private record KeysShape(Method method,
                           boolean takesSolanaAccounts,
                           int keyParams,
                           List<Integer> optional) {
  }

  @SuppressWarnings("unchecked")
  private static List<AccountMeta> invokeKeys(final KeysShape shape, final PublicKey[] keys) {
    final var args = new Object[shape.method().getParameterCount()];
    int i = 0;
    if (shape.takesSolanaAccounts()) {
      args[i++] = SolanaAccounts.MAIN_NET;
    }
    for (int k = 0; k < shape.keyParams(); ++k) {
      args[i++] = keys[k];
    }
    try {
      return (List<AccountMeta>) shape.method().invoke(null, args);
    } catch (final IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (final InvocationTargetException e) {
      throw new IllegalStateException(e.getTargetException());
    }
  }

  private static KeysShape keysShape(final String instruction) {
    final var m = keysMethod(instruction);
    final var params = m.getParameterTypes();
    final boolean solanaAccounts = params.length > 0 && params[0] == SolanaAccounts.class;
    final int keyParams = params.length - (solanaAccounts ? 1 : 0);
    for (int i = solanaAccounts ? 1 : 0; i < params.length; ++i) {
      assertEquals(PublicKey.class, params[i],
          instruction + "Keys takes an unexpected parameter type at " + i);
    }
    final var probing = new KeysShape(m, solanaAccounts, keyParams, List.of());
    final var all = Arrays.copyOf(SENTINELS, keyParams);
    final int base = invokeKeys(probing, all).size();
    final var optional = new ArrayList<Integer>();
    for (int i = 0; i < keyParams; ++i) {
      final var probe = all.clone();
      probe[i] = null;
      try {
        if (invokeKeys(probing, probe).size() == base - 1) {
          optional.add(i);
        }
      } catch (final RuntimeException ignored) {
        // List.of rejects the null: the parameter is required
      }
    }
    assertTrue(optional.size() <= 8, instruction + "Keys has too many optional accounts to solve");
    return new KeysShape(m, solanaAccounts, keyParams, List.copyOf(optional));
  }

  /// Positions of the produced list expressed as parameter indexes, with -1 where the helper
  /// supplied a constant of its own (a sysvar, a well-known program).
  private static int[] layout(final KeysShape shape, final Set<Integer> omit) {
    final var keys = new PublicKey[shape.keyParams()];
    for (int i = 0; i < shape.keyParams(); ++i) {
      keys[i] = omit.contains(i) ? null : SENTINELS[i];
    }
    final var produced = invokeKeys(shape, keys);
    final int[] out = new int[produced.size()];
    Arrays.fill(out, -1);
    for (int j = 0; j < produced.size(); ++j) {
      final var k = produced.get(j).publicKey();
      for (int i = 0; i < shape.keyParams(); ++i) {
        if (!omit.contains(i) && k.equals(SENTINELS[i])) {
          out[j] = i;
          break;
        }
      }
    }
    return out;
  }

  /// What the helper built, and which of its parameters supplied each position (-1 where it
  /// supplied a constant of its own).
  private record Rebuilt(List<AccountMeta> keys, int[] layout) {
  }

  /// Feeds the captured addresses back through the helper. Every combination of omitted optional
  /// accounts is tried, and the one that reproduces the most of the captured list wins — which is
  /// how the test learns, from the generated code alone, which optional accounts the instruction
  /// actually carried.
  private static Rebuilt solve(final String instruction, final List<OnChainAccount> onChain) {
    final var shape = keysShape(instruction);
    final var optional = shape.optional();
    Rebuilt best = null;
    int bestScore = Integer.MIN_VALUE;
    int bestOmissions = Integer.MAX_VALUE;
    for (int mask = 0; mask < (1 << optional.size()); ++mask) {
      final var omit = new LinkedHashSet<Integer>();
      for (int b = 0; b < optional.size(); ++b) {
        if ((mask & (1 << b)) != 0) {
          omit.add(optional.get(b));
        }
      }
      final int[] layout = layout(shape, omit);
      final var keys = new PublicKey[shape.keyParams()];
      for (int j = 0; j < layout.length && j < onChain.size(); ++j) {
        if (layout[j] >= 0) {
          keys[layout[j]] = onChain.get(j).pubkey();
        }
      }
      final List<AccountMeta> produced;
      try {
        produced = invokeKeys(shape, keys);
      } catch (final RuntimeException e) {
        continue; // a required key was left unset because the layout overran the captured list
      }
      int matched = 0;
      for (int j = 0; j < produced.size() && j < onChain.size(); ++j) {
        if (produced.get(j).publicKey().equals(onChain.get(j).pubkey())) {
          ++matched;
        }
      }
      final int score = (matched << 8) - Math.max(0, produced.size() - onChain.size());
      if (score > bestScore || (score == bestScore && omit.size() < bestOmissions)) {
        best = new Rebuilt(produced, layout);
        bestScore = score;
        bestOmissions = omit.size();
      }
    }
    assertNotNull(best, instruction + "Keys cannot be fed an account list of " + onChain.size());
    return best;
  }

  // ------------------------------------------------------------- differences

  private static Map<Integer, Set<AccountDifference>> differences(final Fixture fixture) {
    final var onChain = fixture.accounts();
    final var solved = solve(fixture.instruction(), onChain);
    final var expected = solved.keys();
    final boolean inner = fixture.position().indexOf('.') >= 0;
    final var out = new TreeMap<Integer, Set<AccountDifference>>();
    for (int j = 0; j < onChain.size(); ++j) {
      final var kinds = new LinkedHashSet<AccountDifference>();
      final var actual = onChain.get(j);
      if (j >= expected.size()) {
        kinds.add(AccountDifference.REMAINING_ACCOUNT);
      } else {
        final var want = expected.get(j);
        if (!want.publicKey().equals(actual.pubkey())) {
          kinds.add(j < solved.layout().length && solved.layout()[j] < 0
              ? AccountDifference.HELPER_DEFAULT_NOT_OVERRIDABLE
              : AccountDifference.ADDRESS_MISMATCH);
        }
        final boolean signerDiffers = want.signer() != actual.signer();
        final boolean writeDiffers = want.write() != actual.writable();
        if (actual.feePayer() && actual.signer() && actual.writable() && (signerDiffers || writeDiffers)) {
          kinds.add(AccountDifference.FEE_PAYER);
        } else {
          if (signerDiffers) {
            kinds.add(actual.signer() ? AccountDifference.SIGNS_TRANSACTION
                : actual.multisig() ? AccountDifference.MULTISIG_AUTHORITY
                : inner ? AccountDifference.CPI_SIGNER
                : AccountDifference.AUTHORITY_DID_NOT_SIGN);
          }
          if (writeDiffers) {
            kinds.add(actual.writable()
                ? AccountDifference.MESSAGE_WRITABLE
                : AccountDifference.DECLARED_WRITABLE_BUT_READ_ONLY);
          }
        }
      }
      if (!kinds.isEmpty()) {
        out.put(j, kinds);
      }
    }
    for (int j = onChain.size(); j < expected.size(); ++j) {
      out.put(j, Set.of(AccountDifference.MISSING_ACCOUNT));
    }
    return out;
  }

  private static String encode(final Map<Integer, Set<AccountDifference>> diffs) {
    final var sb = new StringBuilder();
    diffs.forEach((index, kinds) -> {
      if (!sb.isEmpty()) {
        sb.append(';');
      }
      final var names = new TreeSet<String>();
      for (final var k : kinds) {
        names.add(k.name());
      }
      sb.append(index).append(':').append(String.join(",", names));
    });
    return sb.toString();
  }

  // ------------------------------------------------------------------ checks

  @TestFactory
  Stream<DynamicTest> everyCapturedInstruction() {
    return GENERATED.stream().map(f -> DynamicTest.dynamicTest(f.file(), () -> check(f)));
  }

  private static void check(final Fixture fixture) {
    final var name = fixture.instruction();
    final var where = name + " @ " + fixture.signature() + " ix " + fixture.position();
    assertEquals(TOKEN_2022, fixture.programId(), where + ": not a Token-2022 instruction");
    assertFalse(fixture.accounts().isEmpty(), where + ": no accounts captured");

    // 1. dispatch: this instruction's constant matches, and no other one does
    final var field = constantName(name);
    final var discriminator = discriminator(name);
    assertTrue(discriminator.equals(fixture.data(), 0),
        where + ": " + field + " is not a prefix of the captured data");
    DISCRIMINATORS.forEach((otherField, other) -> {
      if (!otherField.equals(field)) {
        assertFalse(other.equals(fixture.data(), 0), where + ": " + otherField
            + " also matches the captured data, so the two cannot be told apart by their constants");
      }
    });

    // 2. decode, measure, re-encode
    final var ixData = ixDataClass(name);
    final Object decoded;
    try {
      decoded = readMethod(ixData).invoke(null, fixture.data(), 0);
    } catch (final IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (final InvocationTargetException e) {
      throw new AssertionError(where + ": " + ixData.getSimpleName() + ".read threw",
          e.getTargetException());
    }
    assertNotNull(decoded, where + ": " + ixData.getSimpleName() + ".read returned null");
    final var serDe = (SerDe) decoded;
    final int accounted = UNDER_DECLARED_ARGUMENTS.getOrDefault(fixture.file(), fixture.data().length);
    assertEquals(accounted, serDe.l(),
        where + ": the decoded instruction does not account for every captured byte");
    final byte[] rewritten = new byte[serDe.l()];
    assertEquals(rewritten.length, serDe.write(rewritten, 0), where + ": write returned a short length");
    assertArrayEquals(Arrays.copyOf(fixture.data(), accounted), rewritten,
        where + ": write did not reproduce the captured bytes");

    // 3. rebuild through the List<AccountMeta> overload
    final var args = argComponents(ixData);
    final var builder = builderMethod(name, args.size());
    final var invocation = new Object[args.size() + 2];
    invocation[0] = SolanaAccounts.MAIN_NET.invokedToken2022Program();
    invocation[1] = List.<AccountMeta>of();
    for (int i = 0; i < args.size(); ++i) {
      try {
        invocation[i + 2] = args.get(i).getAccessor().invoke(decoded);
      } catch (final ReflectiveOperationException e) {
        throw new AssertionError(where + ": cannot read " + args.get(i).getName(), e);
      }
    }
    final Instruction rebuilt;
    try {
      rebuilt = (Instruction) builder.invoke(null, invocation);
    } catch (final IllegalAccessException e) {
      throw new AssertionError(e);
    } catch (final InvocationTargetException e) {
      throw new AssertionError(where + ": the builder threw", e.getTargetException());
    }
    assertArrayEquals(Arrays.copyOf(fixture.data(), accounted), rebuilt.copyData(),
        where + ": the builder did not reproduce the captured bytes from the decoded fields");

    // 4. account list
    assertEquals(EXPECTED_DIFFERENCES.getOrDefault(fixture.file(), ""), encode(differences(fixture)),
        where + ": the account list differs from " + name + "Keys in unexpected ways");
  }

  private static Fixture fixture(final String file) {
    for (final var f : FIXTURES) {
      if (f.file().equals(file)) {
        return f;
      }
    }
    throw new AssertionError("fixture " + file + " is missing");
  }

  private static Object read(final Fixture fixture) {
    try {
      return readMethod(ixDataClass(fixture.instruction())).invoke(null, fixture.data(), 0);
    } catch (final ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  /// **`getAccountDataSize` loses its argument.** The program's `GetAccountDataSize` carries
  /// `extension_types: Vec<ExtensionType>` — packed as byte `21` followed by each type as a raw
  /// little-endian `u16` with no length prefix, exactly how `Reallocate` (byte `29`) packs the
  /// same list. Upstream's `idl.json` declares `reallocate`'s list and not this one, so the
  /// generated record has no field for it: it reads the first byte, reports `l() == 1`, and the
  /// builder cannot emit the types at all. The capture below asks for one extension and is three
  /// bytes long.
  ///
  /// Nothing is worked around here. The fix belongs upstream in the IDL; until then this pins
  /// what the client does and does not read, and fails the day it changes.
  @Test
  void getAccountDataSizeDropsItsExtensionTypeList() {
    final var f = fixture("getAccountDataSize-1.json");
    assertEquals(3, f.data().length, "one extension type: a discriminator byte and a u16");
    assertEquals(21, f.data()[0] & 0xFF, "GetAccountDataSize");
    assertEquals(7, (f.data()[1] & 0xFF) | ((f.data()[2] & 0xFF) << 8),
        "ExtensionType::ImmutableOwner, little-endian, at offset 1");
    assertEquals(1, ((SerDe) read(f)).l(), "the generated reader accounts for the discriminator only");
    assertEquals(0, argComponents(ixDataClass("getAccountDataSize")).size(),
        "GetAccountDataSizeIxData declares no argument to hold the extension types");
  }

  /// **`batch` is excluded on purpose, and it runs on mainnet.** The IDL declares a hundred
  /// instructions and `Token2022Program` carries ninety-nine: the Token 2022 entry in
  /// `main_net_programs.json` lists `"ignoreInstructions": ["batch"]`, because the payload —
  /// dispatch byte `255` followed by a remainder-counted array of an *inline* struct holding
  /// size-prefixed bytes — is a shape the generator cannot render. So the gap is configured
  /// rather than accidental, but it is still a gap a caller meets: the capture below is a
  /// `batch` the program executed, and nothing in this client can build or read it.
  @Test
  void theBatchInstructionIsExcludedByConfiguration() {
    final var f = fixture("batch-1.json");
    assertEquals(255, f.data()[0] & 0xFF, "Batch");
    assertEquals(99, DISCRIMINATORS.size(), "the hundredth instruction is the excluded one");
    assertFalse(DISCRIMINATORS.containsKey("BATCH_DISCRIMINATOR"), "no discriminator is generated");
    for (final var nested : Token2022Program.class.getClasses()) {
      assertFalse(nested.getSimpleName().equals("BatchIxData"), "no reader is generated");
    }
    for (final var m : Token2022Program.class.getMethods()) {
      assertFalse(m.getName().equals("batch") || m.getName().equals("batchKeys"),
          "no builder is generated");
    }
  }

  /// **The scaled-UI multiplier authority is declared writable and is not.** The program's
  /// `update_multiplier` builds `AccountMeta::new_readonly(authority, ..)` and the captured
  /// instruction carries it read-only; upstream's `idl.json` marks it `isWritable: true`, so
  /// `updateMultiplierScaledUiMintKeys` emits `createWritableSigner`. A caller following the
  /// helper asks for write access the program never uses — harmless to the transaction, wrong
  /// about the program, and again an IDL fix rather than one to make here.
  @Test
  void theScaledUiMultiplierAuthorityIsDeclaredWritableAndIsNot() {
    final var f = fixture("updateMultiplierScaledUiMint-1.json");
    final var authority = f.accounts().get(1);
    assertFalse(authority.writable(), "the captured multiplier authority is read-only");
    final var built = Token2022Program.updateMultiplierScaledUiMintKeys(
        f.accounts().getFirst().pubkey(), authority.pubkey());
    assertTrue(built.get(1).write(), "the helper still marks it writable");
    assertTrue(differences(f).get(1).contains(AccountDifference.DECLARED_WRITABLE_BUT_READ_ONLY));
  }

  /// **`EmptyAccount`'s proof slot cannot be overridden.** The IDL names account 1
  /// `instructionsSysvarOrContextState` and gives it the instructions sysvar as a *default*, not
  /// as a constant: the program accepts either that sysvar, when the zero-balance proof is
  /// verified in the same transaction, or a pre-verified context-state account. The generated
  /// helper takes no parameter for the slot and always writes the sysvar, so the captured
  /// instruction — which uses a context-state account — is not expressible through it.
  @Test
  void theEmptyAccountProofSlotCannotBeOverridden() {
    final var f = fixture("emptyConfidentialTransferAccount-1.json");
    final var built = Token2022Program.emptyConfidentialTransferAccountKeys(
        SolanaAccounts.MAIN_NET, f.accounts().getFirst().pubkey(), f.accounts().get(2).pubkey());
    assertEquals(SolanaAccounts.MAIN_NET.instructionsSysVar(), built.get(1).publicKey());
    assertNotEquals(SolanaAccounts.MAIN_NET.instructionsSysVar(), f.accounts().get(1).pubkey(),
        "the captured instruction used a context-state account, not the sysvar");
    assertTrue(differences(f).get(1).contains(AccountDifference.HELPER_DEFAULT_NOT_OVERRIDABLE));
  }

  /// **`syncNativeKeys` appends a rent sysvar the instruction does not carry.** The IDL declares
  /// `rent` optional for `SyncNative`, and the helper adds it whenever it is handed a non-null
  /// `SolanaAccounts` — which is how every other helper in the file is called. Real `SyncNative`
  /// instructions carry the token account alone. Passing `null` is the escape hatch, and it is
  /// not obvious from the signature.
  @Test
  void syncNativeAppendsARentSysvarThatIsNotUsed() {
    final var f = fixture("syncNative-1.json");
    assertEquals(1, f.accounts().size(), "SyncNative takes the native token account and nothing else");
    final var account = f.accounts().getFirst().pubkey();
    assertEquals(2, Token2022Program.syncNativeKeys(SolanaAccounts.MAIN_NET, account).size());
    assertEquals(SolanaAccounts.MAIN_NET.rentSysVar(),
        Token2022Program.syncNativeKeys(SolanaAccounts.MAIN_NET, account).get(1).publicKey());
    assertEquals(1, Token2022Program.syncNativeKeys(null, account).size(),
        "a null SolanaAccounts is the only way to build what the chain carries");
  }

  /// The manifest is the whole input: nothing may be listed twice, and every fixture must name the
  /// instruction its file name claims.
  @Test
  void theManifestAndTheFixturesAgree() {
    final var seen = new LinkedHashSet<String>();
    for (final var f : FIXTURES) {
      assertTrue(seen.add(f.file()), "duplicate fixture " + f.file());
      assertTrue(f.file().startsWith(f.instruction() + '-'),
          f.file() + " does not belong to " + f.instruction());
      assertTrue(f.slot() > 0, f.file() + " has no slot");
      assertFalse(f.signature().isBlank(), f.file() + " has no signature");
      assertNotNull(f.data(), f.file() + " has no data");
    }
  }

  /// Reported explicitly because it is the only on-chain evidence for `isSigner: "either"`: the
  /// authority slot holds a Token-2022 `Multisig` account, which never signs, and the member
  /// signers follow it as accounts the IDL does not declare. Every occurrence is listed with the
  /// multisig's address and the position it occupied.
  @Test
  void multisigOwnerOccurrences() {
    final var found = new TreeMap<String, String>();
    for (final var f : GENERATED) {
      differences(f).forEach((index, kinds) -> {
        if (kinds.contains(AccountDifference.MULTISIG_AUTHORITY)) {
          final var authority = f.accounts().get(index);
          assertFalse(authority.signer(), f.file() + ": a multisig signs nothing itself");
          assertTrue(f.accounts().size() > index + 1,
              f.file() + ": a multisig authority must be followed by its member signers");
          found.put(f.file(), index + ":" + authority.pubkey());
        }
      });
    }
    assertEquals(MULTISIG_OWNER_FIXTURES, found,
        "the set of captured multisignature-authority occurrences changed");
  }

  /// A fixture may only be excused a difference it actually has.
  @Test
  void noStaleExpectedDifferences() {
    final var files = new TreeSet<String>();
    for (final var f : FIXTURES) {
      files.add(f.file());
    }
    for (final var file : EXPECTED_DIFFERENCES.keySet()) {
      assertTrue(files.contains(file), "EXPECTED_DIFFERENCES names a fixture that is gone: " + file);
    }
    for (final var file : MULTISIG_OWNER_FIXTURES.keySet()) {
      assertTrue(files.contains(file), "MULTISIG_OWNER_FIXTURES names a fixture that is gone: " + file);
    }
    for (final var file : UNDER_DECLARED_ARGUMENTS.keySet()) {
      assertTrue(files.contains(file), "UNDER_DECLARED_ARGUMENTS names a fixture that is gone: " + file);
    }
  }
}
