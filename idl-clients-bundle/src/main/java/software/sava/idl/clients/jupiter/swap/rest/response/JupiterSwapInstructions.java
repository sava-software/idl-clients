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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
    return ji.readList(PublicKeyEncoding::parseBase58Encoded);
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
    return AccountMeta.createAccountsMap(64, feePayer());
  }

  /// The fee payer is the user's wallet. It was previously read unconditionally
  /// from the first setup instruction's first account, but Jupiter returns
  /// `setupInstructions: []` — or omits the field — whenever no associated token
  /// account has to be created and no SOL wrapping is required, which made
  /// [#serializeTransaction] throw `NoSuchElementException` on a perfectly valid
  /// response.
  ///
  /// The setup instruction is still preferred, since it funds the ATA and so its
  /// first account is the payer by construction. Otherwise the wallet is
  /// recovered from the swap instruction, which it always signs.
  private PublicKey feePayer() {
    if (setupInstructions != null && !setupInstructions.isEmpty()) {
      return setupInstructions.getFirst().accounts().getFirst().publicKey();
    }
    for (final var account : swapInstruction.accounts()) {
      if (account.signer()) {
        return account.publicKey();
      }
    }
    throw new IllegalStateException(
        "Cannot determine the fee payer: no setup instructions and no signer on the swap instruction.");
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
    return ji.parseObject(new Parser());
  }

  public static List<Instruction> parseInstructionsList(final JsonIterator ji) {
    final var instructions = ji.readList(JupiterSwapInstructions::parseInstruction);
    return instructions.isEmpty() ? NO_INSTRUCTIONS : instructions;
  }

  public static Instruction parseInstruction(final JsonIterator ji) {
    return ji.parseObject(new InstructionParser());
  }

  private static AccountMeta parseAccount(final JsonIterator ji) {
    return ji.parseObject(new AccountParser());
  }

  private static final class InstructionParser implements FieldBufferPredicate, Supplier<Instruction> {

    private PublicKey programId;
    private List<AccountMeta> accounts;
    private byte[] data;

    private InstructionParser() {
    }

    @Override
    public Instruction get() {
      return Instruction.createInstruction(programId, accounts, data);
    }

    @Override
    public boolean test(final char[] buf, final int offset, final int len, final JsonIterator ji) {
      if (fieldEquals("programId", buf, offset, len)) {
        programId = parseBase58Encoded(ji);
      } else if (fieldEquals("accounts", buf, offset, len)) {
        accounts = ji.readList(JupiterSwapInstructions::parseAccount);
      } else if (fieldEquals("data", buf, offset, len)) {
        data = ji.decodeBase64String();
      } else {
        ji.skip();
      }
      return true;
    }
  }

  private static final class AccountParser implements FieldBufferPredicate, Supplier<AccountMeta> {

    private PublicKey pubKey;
    private boolean signer;
    private boolean writable;

    private AccountParser() {
    }

    @Override
    public AccountMeta get() {
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

  private static final class Parser implements FieldBufferPredicate, Supplier<JupiterSwapInstructions> {

    private List<Instruction> computeBudgetInstructions;
    private List<Instruction> setupInstructions;
    private Instruction swapInstruction;
    private Instruction cleanupInstruction;
    private List<Instruction> otherInstructions;
    private List<PublicKey> addressLookupTableAddresses;

    private Parser() {
    }

    @Override
    public JupiterSwapInstructions get() {
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
        cleanupInstruction = ji.readOrNull(ValueType.OBJECT, JupiterSwapInstructions::parseInstruction);
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
