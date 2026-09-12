# Token 2022 with Sava

Token 2022 (Token Extensions) functionality is spread across multiple sava projects. This guide explains what each project
provides and how to use them together.

## Project Overview

| Project             | Maven coordinate                | Java module                    | Purpose                                                                                     |
|---------------------|---------------------------------|--------------------------------|---------------------------------------------------------------------------------------------|
| **sava-core**       | `software.sava:sava-core`       | `software.sava.core`           | Account deserialization for Token 2022 mints, token accounts, and every token extension type |
| **solana-programs** | `software.sava:solana-programs` | `software.sava.solana_programs` | Hand-written instruction helpers for a subset of the program. Deprecated                     |
| **idl-clients-spl** | `software.sava:idl-clients-spl` | `software.sava.idl.clients.spl` | Generated instruction builders with instruction data parsers, `Token2022Error`, and generated `Mint`, `Token` and `Extension` account decoders |

This page describes the code as it is built here: against **sava-core 25.11.0**, the version the Solana BOM pinned in
`gradle/sava.properties` resolves to.

Account decoding exists twice, deliberately: sava-core's hand-written `Token2022` and `Token2022Account`, and the
`Mint`, `Token` and `Extension` types generated here from the program's IDL. They read the same bytes and, for every
account the program itself wrote, agree; [how they differ](#generated-account-types-versus-sava-core) is a short table.

## sava-core — Account (De)Serialization

sava-core provides hand-written serialization and deserialization support for Token 2022 mint accounts, token accounts,
and all token extension types.

### Key Classes

- `software.sava.core.accounts.token.Token2022` — Token 2022 mint accounts:
  `Token2022(Mint mint, AccountType accountType, Set<TokenExtension> tokenExtensions)`
- `software.sava.core.accounts.token.Token2022Account` — Token 2022 token accounts:
  `Token2022Account(TokenAccount tokenAccount, AccountType type, Set<TokenExtension> tokenExtensions)`
- `software.sava.core.accounts.token.extensions.TokenExtension` — sealed interface permitting `MintTokenExtension`,
  `AccountTokenExtension` and `UnknownTokenExtension`; every extension is a record
- `software.sava.core.accounts.token.extensions.UnknownTokenExtension` — an extension type this library has not been
  synced with yet, carrying its raw bytes

`TokenExtension.ordinal()` is the **on-chain `u16` extension type ID**, not an index into a Java enum. Extensions are
therefore matched by concrete record type rather than looked up by key: 25.11.0 removed the `ExtensionType` enum along
with the `extensions()` maps that were keyed by it. An extension type the library does not know arrives as
`UnknownTokenExtension`, which keeps the bytes verbatim — preserve those when you read an account and write it back.

### Reading a Token 2022 Mint

```java
Token2022 mint = Token2022.read(publicKey, accountData);

// Access the base mint data
Mint baseMint = mint.mint();

// Iterate the extensions and match the concrete type
for (var extension : mint.tokenExtensions()) {
  switch (extension) {
    case TransferFeeConfig transferFees -> handleFees(transferFees);
    case TokenMetadata metadata -> handleMetadata(metadata);
    case PermanentDelegate(PublicKey delegate) -> handleDelegate(delegate);
    case UnknownTokenExtension unknown -> preserve(unknown.ordinal(), unknown.data());
    default -> {
    }
  }
}
```

An extension's authority and address fields are **never null**. The program stores an absent authority as thirty-two
zero bytes rather than as an option, and sava-core reads every such field unconditionally — so `MetadataPointer`'s
`authority` and `metadataAddress`, `TransferHook`'s `authority` and `programId`, `PermanentDelegate`'s `delegate`, and
every other pubkey an extension declares decode to an all-zero `PublicKey`. Compare against `PublicKey.NONE` to test for
absence:

```java
for (var extension : mint.tokenExtensions()) {
  if (extension instanceof TransferHook(var authority, var programId) && !PublicKey.NONE.equals(programId)) {
    // this mint really does route transfers through a hook program
  }
}
```

### Reading a Token 2022 Token Account

```java
Token2022Account account = Token2022Account.read(publicKey, accountData);

// Access the base token account data
TokenAccount baseAccount = account.tokenAccount();

// Extensions are matched the same way as on a mint
for (var extension : account.tokenExtensions()) {
  if (extension instanceof TransferFeeAmount feeAmount) {
    handleWithheld(feeAmount);
  }
}
```

## idl-clients-spl

This project contains code generated from the Token 2022 program's IDL. It provides instruction builders **and**
instruction data parsers (deserialization), making it the preferred choice for most instruction construction.

### Key Classes

- `software.sava.idl.clients.spl.token_2022.gen.Token2022Program` — 99 instructions, every one the IDL declares except
  `batch`, each with builders and an `IxData` record that parses the instruction back
- `software.sava.idl.clients.spl.token_2022.gen.Token2022Error` — the program error codes the IDL declares, and their
  messages
- `software.sava.idl.clients.spl.token_2022.gen.types.Mint` and `gen.types.Token` — the mint and token account
  decoders: the base state, then the account-type byte (behind the mint's 83 bytes of padding) and the TLV extension
  list as an `Extension[]` — `null` for an account that stops at its base state, empty for one that carries the type
  byte and nothing after it
- `software.sava.idl.clients.spl.token_2022.gen.types.Extension` — the sealed extension enum, one nested record per
  variant named as the IDL spells it (`Extension.transferFeeConfig`, `Extension.tokenMetadata`, …, and
  `Extension.uninitialized` for a zero type word), each reading, checking, writing and counting its `u16` length word
- `software.sava.idl.clients.spl.token_2022.gen.types.Multisig` — the multisig account decoder (355 bytes: `m`, `n`,
  `isInitialized`, eleven signer slots), with `Filter` helpers for `getProgramAccounts`. sava-core has no multisig
  decoder, so this and its sibling in the SPL Token package are the only ones here
- `software.sava.idl.clients.spl.token_2022.gen.types.*` — the remaining types: the `AccountState`, `AuthorityType` and
  `ExtensionType` enums, the fixed-length `EncryptedBalance` (64 bytes) and `DecryptableBalance` (36 bytes) ciphertext
  wrappers, the sealed `TokenMetadataField`, and `TransferFee`
- `software.sava.idl.clients.spl.token_2022.Token2022Instructions` — hand-written, and today only a second
  implementation of `updateTokenMetadataField` (see below)

Note that `gen.types.ExtensionType` is the IDL's `u16` enum, used as an instruction argument by `reallocate`. It names
the same ids `Extension.ordinal()` returns, but is a plain enum with no payload.

### Reading accounts with the generated types

```java
import software.sava.idl.clients.spl.token_2022.gen.types.Extension;
import software.sava.idl.clients.spl.token_2022.gen.types.Mint;

Mint mint = Mint.read(publicKey, accountData);
if (mint.extensions() != null) {
  for (var extension : mint.extensions()) {
    switch (extension) {
      case Extension.transferFeeConfig fees -> handleFees(fees.newerTransferFee());
      case Extension.tokenMetadata metadata -> handleMetadata(metadata.name(), metadata.additionalMetadata());
      case Extension.metadataPointer(var authority, var metadataAddress) when metadataAddress != null -> follow(metadataAddress);
      default -> {
      }
    }
  }
}
byte[] bytes = mint.write(); // byte for byte what the program wrote, for every account it wrote
```

`Token.read` is the same shape over a token account. Both readers apply the IDL literally: the account-type byte must
be the one the IDL declares (`1` for a mint, `2` for a token account), every extension's length word must equal the
bytes its fields consume, and a type id the client was generated before is an error rather than a silently shortened
list — the same rules the program's own JavaScript client applies, which is rendered from the same IDL.

One thing the IDL gets wrong, and both generated and JS clients inherit: eight authority fields the program stores as
`OptionalNonZeroPubkey` are declared plain `publicKeyTypeNode` rather than `zeroableOptionTypeNode` —
`transferFeeConfig`'s two authorities, `mintCloseAuthority.closeAuthority`, `interestBearingConfig.rateAuthority`,
`permanentDelegate.delegate`, `transferHook.authority` and `programId`, and `scaledUiAmountConfig.authority`. Those
decode to `PublicKey.NONE` when unset, where the thirteen declared zeroable decode to `null`.
`Token2022AccountConformanceTests` keeps the two lists and checks every variant against them; the fix belongs in the
upstream IDL.

### Generated account types versus sava-core

Both decode every account the program has written and reproduce it byte for byte. The representation differs in these
ways, and `Token2022AccountConformanceTests` asserts each one rather than skipping it:

| Wire fact                                                                       | sava-core `Token2022` / `Token2022Account`                       | generated `Mint` / `Token`                                                                                    |
|---------------------------------------------------------------------------------|------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| An absent zeroable authority (`MetadataPointer.authority`, `TransferHook.programId`, …) is 32 zero bytes | `PublicKey.NONE`, never `null`                                   | `null`                                                                                                        |
| An absent `COption` (`mintAuthority`, `freezeAuthority`, `delegate`, `closeAuthority`) | `null`                                                           | `null`                                                                                                        |
| The extension list                                                              | `Set<TokenExtension>`, matched by record type                    | `Extension[]` in wire order, matched by nested record type                                                    |
| A zero type word (`Uninitialized` padding)                                      | the walk stops there, as the program's does; the entry is dropped unless it is the only one | kept as `Extension.uninitialized`, so the bytes round trip; the walk continues, so an entry *after* a zero word — which the program never writes and never reads — is decoded rather than ignored |
| An extension id this library does not know                                      | `UnknownTokenExtension` carrying the bytes                       | `IllegalArgumentException`, as with the upstream JS client                                                    |
| The account-type byte                                                           | must match the base state: the declared one behind an initialized base, `Uninitialized` behind a zeroed one (an account between two initializers) | must be the declared one, whatever the base says — a pre-initialization account throws, and a mint's bytes labelled `2` read as an uninitialized token account, both as with the upstream JS client |
| An extension-free account (82 or 165 bytes)                                     | 25.11.0 throws; decodes on sava `main`, unreleased               | `extensions()` is `null`                                                                                      |
| A tail shorter than a type word                                                 | 25.11.0 throws; ignored on sava `main`                           | ignored; a longer zero tail is `uninitialized` entries                                                        |
| A length word disagreeing with the value                                        | rejected, except for token metadata                              | rejected for every variant                                                                                    |
| A 355-byte buffer (`Multisig::LEN`)                                             | 25.11.0 decodes it as an uninitialized account; refused by length on sava `main` | no length rule; a real multisig fails on the first `COption` tag its signer bytes land on, before the account-type check |
| The 83 padding bytes between a mint and its type byte                           | not inspected                                                    | must be zero, as the program and the upstream JS client require                                               |
| A boolean byte other than 0 or 1                                                | `true` (`!= 0`, the program's `PodBool` rule)                    | `false` (`== 1`, the Kit codec's rule); the program never writes one                                          |
| An extension list that lands the account on `Multisig::LEN`                     | `l()` and `write` add the program's two-byte pad                 | `l()` is the bytes as declared; allocate with the program's `getAccountDataSize`, not `l()`                    |
| A truncated buffer                                                              | 25.11.0 decodes a short base state; refused on sava `main`      | refused: every slot, read or skipped, is bounds-checked                                                       |
| Token metadata `additionalMetadata`                                             | unmodifiable `Map` in wire order                                 | `Map` in wire order, written back in iteration order                                                          |

### Configuration

The generator is driven by this program's entry in [main_net_programs.json](../main_net_programs.json):

```json
{
  "ignoreInstructions": [
    "batch"
  ]
}
```

Until 2026-09-12 the entry also mapped the IDL's `Mint`, `Token` and `Extension` to sava-core's classes through
`externalTypes`, because the generator could not render a `u16`-size-prefixed struct, the hidden account-type constant
or a remainder option. It can now, so the three types are generated like everything else; nothing in
`Token2022Program` changed, because no instruction references them.

`batch` is the single ignored instruction. It dispatches on discriminator 255, declares no accounts of its own, and
takes a remainder array of sub-instructions — each a `u8` account count followed by a `u8`-length-prefixed instruction
payload — whose accounts are sliced out of the surrounding account list. None of that is expressible as a generated
builder, so the entry declines it rather than emitting something misleading.

One generated instruction is under-declared by the IDL itself: `getAccountDataSize` packs a trailing list of `u16`
extension types after its discriminator (the program sizes the account for them), but the IDL declares only the
discriminator, so the generated builder can request only the base size and `GetAccountDataSizeIxData.read` reports a
one-byte length for a real instruction that carries more. The upstream IDL is the place to fix that.

### Verification against the chain

Three test suites keep the instruction side honest against things it did not generate itself:
`Token2022ReferenceEncodingTests` compares every instruction's bytes and account list with the program's own JavaScript
client (`tools/token2022-vectors.mjs` regenerates the vectors); `Token2022OnChainInstructionTests` decodes and rebuilds
real mainnet instructions pinned under `src/test/resources/token_2022/mainnet/`, including multisig-owner and CPI-signed
cases; and `Token2022IxDataFuzz` drives all 99 instruction readers over arbitrary bytes.

The account side has its own three, over the 25 mainnet accounts pinned under `src/test/resources/token_2022/accounts/`
(raw bytes, the validator's `jsonParsed` decode of each, and a manifest naming the slot and the TLV chain):
`Token2022AccountConformanceTests` compares every field the node prints with the generated record, cross-checks
sava-core's decode of the same bytes, and requires a byte-exact round trip; `Token2022ReferenceDecodeTests` compares
the generated readers with the program's own JavaScript client over those accounts and over hand-built malformed
buffers — pre-initialization, odd tails, unknown ids, wrong type bytes, bad length words — so that what is accepted
and what is rejected is pinned to an implementation this repository did not produce
(`tools/token2022-account-vectors.mjs` regenerates the fixture); and `Token2022AccountFuzz` drives both readers over
arbitrary bytes, checking every decode against sava-core's.

### updateTokenMetadataField

There are two implementations of this instruction, and they encode identical bytes.

The **generated** one is the recommended entry point. Its field argument is `gen.types.TokenMetadataField`, a sealed
interface over `name`, `symbol`, `uri` and `key` — the `key` variant carries the custom key string, so the field and its
key travel together:

```java
import software.sava.idl.clients.spl.token_2022.gen.Token2022Program;
import software.sava.idl.clients.spl.token_2022.gen.types.TokenMetadataField;

// Update the name field
Instruction ix = Token2022Program.updateTokenMetadataField(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    TokenMetadataField.name.INSTANCE,
    "New Token Name"
);

// Update a custom key-value pair
Instruction ix = Token2022Program.updateTokenMetadataField(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    TokenMetadataField.key.createRecord("website"),
    "https://example.com"
);

var parsed = Token2022Program.UpdateTokenMetadataFieldIxData.read(instruction);
TokenMetadataField field = parsed.field();
String value = parsed.value();
```

The **hand-written** `Token2022Instructions` predates generation of this instruction — the generated version has been
available since 2026-08-15 — and is kept as an independent oracle: it was written from the token-metadata interface
rather than from the IDL, and `Token2022InstructionsTests` asserts that the two produce the same bytes and decode the
same payloads. Its field argument is a plain enum, `Name`/`Symbol`/`Uri`/`Key`, with the custom key passed separately,
and it offers named shortcuts:

```java
import software.sava.idl.clients.spl.token_2022.Token2022Instructions;
import software.sava.idl.clients.spl.token_2022.Token2022Instructions.TokenMetadataField;

Instruction ix = Token2022Instructions.updateTokenMetadataName(
    invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, "New Token Name");
Instruction ix = Token2022Instructions.updateTokenMetadataSymbol(
    invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, "NTK");
Instruction ix = Token2022Instructions.updateTokenMetadataUri(
    invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, "https://example.com/metadata.json");
Instruction ix = Token2022Instructions.updateTokenMetadataCustomField(
    invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, "website", "https://example.com");

var parsed = Token2022Instructions.UpdateTokenMetadataFieldIxData.read(instruction);
TokenMetadataField field = parsed.field();
String value = parsed.value();
String key = parsed.key(); // non-null only when field == Key
```

The two `TokenMetadataField` types share a simple name, so import one and qualify the other.

### Building Instructions

Each instruction has two overloads: one accepting individual account keys, and one accepting a pre-built
`List<AccountMeta>`.

```java
// Create a transfer checked instruction
Instruction ix = Token2022Program.transferChecked(
        invokedToken2022ProgramMeta,
        sourceKey,
        mintKey,
        destinationKey,
        authorityKey,
        amount,
        decimals
    );

// Initialize token metadata
Instruction ix = Token2022Program.initializeTokenMetadata(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    mintKey,
    mintAuthorityKey,
    "My Token",
    "MTK",
    "https://example.com/metadata.json"
);

// Initialize mint with close authority extension
Instruction ix = Token2022Program.initializeMintCloseAuthority(
    invokedToken2022ProgramMeta,
    mintKey,
    closeAuthority
);

// Initialize transfer fee config
Instruction ix = Token2022Program.initializeTransferFeeConfig(
    invokedToken2022ProgramMeta,
    mintKey,
    transferFeeConfigAuthority,
    withdrawWithheldAuthority,
    transferFeeBasisPoints,
    maximumFee
);
```

Ten of the key-argument overloads take a leading `SolanaAccounts` as well, because their account lists include a sysvar
or a well-known program: `initializeMint`, `initializeAccount`, `initializeMultisig`, `initializeAccount2`,
`syncNative`, `configureConfidentialTransferAccount`, `emptyConfidentialTransferAccount`, `reallocate`,
`createNativeMint` and `rotateSupplyElgamalPubkey`.

Every instruction also exposes a `*Keys` helper returning the declared account list, and a builder overload taking a
`List<AccountMeta>`. **That overload is how you reach anything the IDL cannot declare**, and there are three such cases
here:

- **Multisig owners.** The IDL declares the M signer accounts as remaining accounts on 58 instructions, and marks 56
  owner or authority accounts `"isSigner": "either"`. The generated key helpers express neither: they mark the owner a
  required signer and accept no trailing accounts. For a multisig owner, build the list yourself — the owner read-only
  and *not* a signer, then each of its signers read-only and a signer:

  ```java
  var keys = new ArrayList<AccountMeta>();
  keys.add(AccountMeta.createWrite(sourceKey));
  keys.add(AccountMeta.createWrite(destinationKey));
  keys.add(AccountMeta.createRead(multisigOwnerKey));
  for (var signer : signerKeys) {
    keys.add(AccountMeta.createReadOnlySigner(signer));
  }
  Instruction ix = Token2022Program.transfer(invokedToken2022ProgramMeta, keys, amount);
  ```

- **Transfer hooks.** A mint with the transfer-hook extension needs the hook program's extra account metas appended to
  every transfer, resolved from its extra-account-meta list.
- **Confidential transfers.** The proof-verification and context-state accounts a confidential instruction reads, and
  the proof-instruction offsets that point at them, are the caller's to assemble.

### Parsing Instruction Data

A key advantage of the generated code is that every instruction includes an `IxData` record with a static `read` method
for parsing instruction data from on-chain transactions.

```java
// Parse a transfer checked instruction from a transaction
var parsed = Token2022Program.TransferCheckedIxData.read(instruction);
long amount = parsed.amount();
int decimals = parsed.decimals();

// Parse an initialize mint instruction
var parsed = Token2022Program.InitializeMintIxData.read(instruction);
int decimals = parsed.decimals();
PublicKey mintAuthority = parsed.mintAuthority();
```

**`read` decodes the discriminator into a field; it does not validate it.** Handing an instruction to the wrong
`IxData.read` returns a populated record rather than throwing, so the caller is responsible for dispatching to the right
one. Every instruction's `*_DISCRIMINATOR` constant is the full key the program dispatches on: one byte for the base
instructions, two for the 58 that live under an extension — the extension's instruction index followed by its
sub-instruction index, so `INITIALIZE_TRANSFER_FEE_CONFIG_DISCRIMINATOR` is `toDiscriminator(26, 0)` and
`SET_TRANSFER_FEE_DISCRIMINATOR` is `toDiscriminator(26, 5)` — and eight for the token-metadata and token-group interface
instructions. All 99 are distinct, so comparing the constant against the instruction's own bytes identifies it before
anything is decoded:

```java
final byte[] data = instruction.copyData(); // this instruction's own bytes, not the transaction's

if (Token2022Program.SET_TRANSFER_FEE_DISCRIMINATOR.equals(data, 0)) {
  var parsed = Token2022Program.SetTransferFeeIxData.read(data, 0);
  long maximumFee = parsed.maximumFee();
}
```

The parsed records still carry every discriminator byte as a field — `discriminator`, and for the extension
instructions `transferFeeDiscriminator`, `confidentialTransferDiscriminator` and so on — so a record says which
instruction it was read as. Before the client was regenerated on 2026-09-06 the constants carried only the first byte,
so 58 of them were shared with their sibling sub-instructions; code that compared against those one-byte values needs
the two-byte constants now. The instruction bytes the builders write did not change.

### Error Handling

`Token2022Error` is a sealed interface over the error codes the IDL declares:

```java
Token2022Error error = Token2022Error.getInstance(errorCode);
if (error == null) {
  // not one of the 20 codes the IDL declares
} else if (error instanceof Token2022Error.InsufficientFunds) {
  // handle insufficient funds
}
System.out.println(error.msg()); // Human-readable error message
```

The IDL declares 20 errors, codes 0 through 19 — the original SPL Token set. The deployed program's `TokenError` has
**69**, codes 0 through 68, and everything the extensions added lives in that tail. `getInstance` returns `null` for 20
through 68, so guard the result rather than dereferencing it.

One message is worth knowing about: code 9 reads `State is unititialized`. The typo is in the published IDL, which is
where the generated messages come from; the program's own Rust spells it correctly.

## solana-programs — Manual Instruction Helpers

`sava-software/solana-programs` still exists and its 25.0.2 release is still published, but it is **deprecated**: its
last commit, on 2026-06-01, moved its functionality here and records that the repository will no longer be updated. New
code should use idl-clients-spl.

`software.sava.solana.programs.token.Token2022Program` there is hand-written and covers a curated subset: 56 methods
returning an `Instruction`, under 45 names, encoding 31 distinct instructions. Those are the SPL Token base
instructions, re-exported against the Token 2022 program id, plus the extension calls a mint-creation flow needs —
mint close authority, non-transferable mint, permanent delegate, metadata pointer initialize and update, transfer hook
initialize and update, the token-metadata `Initialize`, `Reallocate`, `CreateNativeMint` and `WithdrawExcessLamports`.
There is nothing for transfer fees, confidential transfers, confidential transfer fees, confidential mint/burn, default
account state, memo transfer, interest-bearing mints, CPI guard, group or group-member pointers, scaled UI amount, or
pausable mints. It builds only — it has no instruction-data parsers and no error enum.

The one thing it offers that the generated client does not is ready-made multisig account lists: its `*Multisig`
variants — fifteen of them, plus `reallocateMultisig` and `withdrawExcessLamports` — take a `List<PublicKey>` of signer
accounts and append them as read-only signers.

### Key Class

- `software.sava.solana.programs.token.Token2022Program` — manually written instruction helpers

### Initialize Token Metadata

```java
Instruction ix = Token2022Program.initializeTokenMetadataInstruction(
    solanaAccounts,
    metadataAccount,
    updateAuthority,
    mintAuthority,
    mintAccount,
    "My Token",
    "MTK",
    "https://example.com/metadata.json"
);
```

## Whats Where

| Use Case                                   | Recommended Project                                                          |
|--------------------------------------------|------------------------------------------------------------------------------|
| Token 2022 mint accounts                   | **sava-core** (`Token2022`) or **idl-clients-spl** (`gen.types.Mint`); see the differences table |
| Token 2022 token accounts                  | **sava-core** (`Token2022Account`) or **idl-clients-spl** (`gen.types.Token`) |
| Reading token extension data               | **sava-core** (the `TokenExtension` records) or **idl-clients-spl** (the `Extension` variants) |
| Multisig accounts                          | **idl-clients-spl** (`gen.types.Multisig`)                                   |
| Building instructions                      | **idl-clients-spl** (`Token2022Program`)                                     |
| Multisig-owner instructions                | **idl-clients-spl**, through a hand-built `List<AccountMeta>`                |
| `updateTokenMetadataField` instruction     | **idl-clients-spl** (`Token2022Program`; `Token2022Instructions` cross-checks it) |
| Parsing instruction data from transactions | **idl-clients-spl** (`IxData.read()`, then check the sub-discriminator)      |
| Handling program errors                    | **idl-clients-spl** (`Token2022Error`, for codes 0–19)                       |
