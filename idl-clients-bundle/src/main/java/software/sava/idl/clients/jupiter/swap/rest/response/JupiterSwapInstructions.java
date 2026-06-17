package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.lookup.AddressLookupTable;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.accounts.meta.LookupTableAccountMeta;
import software.sava.core.tx.Instruction;
import software.sava.core.tx.Transaction;
import software.sava.rpc.json.PublicKeyEncoding;
import systems.comodal.jsoniter.FieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.jsoniter.ValueType;

import java.util.*;

import static software.sava.core.tx.Transaction.sortLegacyAccounts;
import static software.sava.rpc.json.PublicKeyEncoding.parseBase58Encoded;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

public record JupiterSwapInstructions(List<Instruction> computeBudgetInstructions,
                                      List<Instruction> setupInstructions,
                                      Instruction swapInstruction,
                                      Instruction cleanupInstruction,
                                      List<Instruction> otherInstructions,
                                      List<PublicKey> addressLookupTableAddresses) {

  public static Instruction parseSwapInstruction(final JsonIterator jsonResponseBody) {
    if (jsonResponseBody.skipUntil("swapInstruction") == null) {
      if (jsonResponseBody.reset(0).skipUntil("swapInstruction") == null) {
        return null;
      }
    }
    return JupiterSwapInstructions.parseInstruction(jsonResponseBody);
  }

  static List<PublicKey> parseKeys(final JsonIterator ji) {
    final var keys = new ArrayList<PublicKey>();
    while (ji.readArray()) {
      keys.add(PublicKeyEncoding.parseBase58Encoded(ji));
    }
    return keys;
  }

  public static Collection<PublicKey> parseLookupTables(final JsonIterator ji) {
    if (ji.skipUntil("addressLookupTableAddresses") == null) {
      if (ji.reset(0).skipUntil("addressLookupTableAddresses") == null) {
        return List.of();
      }
    }
    return parseKeys(ji);
  }

  public Map<PublicKey, AccountMeta> createAccountsMap() {
    final var feePayer = setupInstructions.getFirst().accounts().getFirst().publicKey();
    return AccountMeta.createAccountsMap(64, feePayer);
  }

  public int numInstructions() {
    return computeBudgetInstructions.size()
        + setupInstructions.size()
        + 1
        + (cleanupInstruction == null ? 0 : 1)
        + otherInstructions.size();
  }

  public int mergeAllAccounts(final Instruction[] instructions, final Map<PublicKey, AccountMeta> accounts) {
    int ix = 0;
    int serializedInstructionLength = 0;

    for (final var instruction : computeBudgetInstructions) {
      serializedInstructionLength += instruction.mergeAccounts(accounts);
      instructions[ix] = instruction;
      ++ix;
    }

    for (final var instruction : setupInstructions) {
      serializedInstructionLength += instruction.mergeAccounts(accounts);
      instructions[ix] = instruction;
      ++ix;
    }

    serializedInstructionLength += swapInstruction.mergeAccounts(accounts);
    instructions[ix] = swapInstruction;

    if (cleanupInstruction != null) {
      serializedInstructionLength += cleanupInstruction.mergeAccounts(accounts);
      instructions[++ix] = cleanupInstruction;
    }

    for (final var instruction : otherInstructions) {
      serializedInstructionLength += instruction.mergeAccounts(accounts);
      instructions[++ix] = instruction;
    }

    return serializedInstructionLength;
  }

  public Transaction serializeTransaction() {
    final var accounts = createAccountsMap();
    final var instructions = new Instruction[numInstructions()];
    final int serializedInstructionLength = mergeAllAccounts(instructions, accounts);
    return Transaction.createTx(Arrays.asList(instructions), serializedInstructionLength, sortLegacyAccounts(accounts));
  }

  public Transaction serializeTransaction(final AddressLookupTable lookupTable) {
    final var accounts = createAccountsMap();
    final var instructions = new Instruction[numInstructions()];
    final int serializedInstructionLength = mergeAllAccounts(instructions, accounts);
    return Transaction.createTx(Arrays.asList(instructions), serializedInstructionLength, accounts, lookupTable);
  }

  public Transaction serializeTransaction(final LookupTableAccountMeta[] tableAccountMetas) {
    final var accounts = createAccountsMap();
    final var instructions = new Instruction[numInstructions()];
    final int serializedInstructionLength = mergeAllAccounts(instructions, accounts);
    return Transaction.createTx(Arrays.asList(instructions), serializedInstructionLength, accounts, tableAccountMetas);
  }

  private static final List<Instruction> NO_INSTRUCTIONS = List.of();

  public static JupiterSwapInstructions parseInstructions(final JsonIterator ji) {
    final var parser = new Parser();
    ji.testObject(parser);
    return parser.create();
  }

  public static List<Instruction> parseInstructionsList(final JsonIterator ji) {
    if (ji.readArray()) {
      final var instructions = new ArrayList<Instruction>();
      do {
        instructions.add(parseInstruction(ji));
      } while (ji.readArray());
      return instructions;
    } else {
      return NO_INSTRUCTIONS;
    }
  }

  public static Instruction parseInstruction(final JsonIterator ji) {
    final var parser = new InstructionParser();
    ji.testObject(parser);
    return parser.create();
  }

  private static AccountMeta parseAccount(final JsonIterator ji) {
    final var parser = new AccountParser();
    ji.testObject(parser);
    return parser.create();
  }

  private static final class InstructionParser implements FieldBufferPredicate {

    private PublicKey programId;
    private List<AccountMeta> accounts;
    private byte[] data;

    private InstructionParser() {
    }

    private Instruction create() {
      return Instruction.createInstruction(programId, accounts, data);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("programId", buf, offset, len)) {
        programId = parseBase58Encoded(ji);
      } else if (fieldEquals("accounts", buf, offset, len)) {
        final var accounts = new ArrayList<AccountMeta>();
        while (ji.readArray()) {
          accounts.add(parseAccount(ji));
        }
        this.accounts = accounts;
      } else if (fieldEquals("data", buf, offset, len)) {
        data = ji.decodeBase64String();
      } else {
        ji.skip();
      }
      return true;
    }
  }

  private static final class AccountParser implements FieldBufferPredicate {

    private PublicKey pubKey;
    private boolean signer;
    private boolean writable;

    private AccountParser() {
    }

    private AccountMeta create() {
      if (signer) {
        return writable
            ? AccountMeta.createWritableSigner(pubKey)
            : AccountMeta.createReadOnlySigner(pubKey);
      } else if (writable) {
        return AccountMeta.createWrite(pubKey);
      } else {
        return AccountMeta.createRead(pubKey);
      }
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("pubkey", buf, offset, len)) {
        pubKey = parseBase58Encoded(ji);
      } else if (fieldEquals("isSigner", buf, offset, len)) {
        signer = ji.readBoolean();
      } else if (fieldEquals("isWritable", buf, offset, len)) {
        writable = ji.readBoolean();
      } else {
        ji.skip();
      }
      return true;
    }
  }

  private static final class Parser implements FieldBufferPredicate {

    private List<Instruction> computeBudgetInstructions;
    private List<Instruction> setupInstructions;
    private Instruction swapInstruction;
    private Instruction cleanupInstruction;
    private List<Instruction> otherInstructions;
    private List<PublicKey> addressLookupTableAddresses;

    private Parser() {
    }

    private JupiterSwapInstructions create() {
      return new JupiterSwapInstructions(
          computeBudgetInstructions,
          setupInstructions,
          swapInstruction,
          cleanupInstruction,
          otherInstructions,
          addressLookupTableAddresses
      );
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("computeBudgetInstructions", buf, offset, len)) {
        computeBudgetInstructions = parseInstructionsList(ji);
      } else if (fieldEquals("setupInstructions", buf, offset, len)) {
        setupInstructions = parseInstructionsList(ji);
      } else if (fieldEquals("swapInstruction", buf, offset, len)) {
        swapInstruction = parseInstruction(ji);
      } else if (fieldEquals("cleanupInstruction", buf, offset, len)) {
        if (ji.whatIsNext() == ValueType.OBJECT) {
          cleanupInstruction = parseInstruction(ji);
        } else {
          ji.skip();
        }
      } else if (fieldEquals("otherInstructions", buf, offset, len)) {
        otherInstructions = parseInstructionsList(ji);
      } else if (fieldEquals("addressLookupTableAddresses", buf, offset, len)) {
        addressLookupTableAddresses = parseKeys(ji);
      } else {
        ji.skip();
      }
      return true;
    }
  }
}
