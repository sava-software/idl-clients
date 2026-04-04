# Token 2022 with Sava

Token 2022 (Token Extensions) functionality is spread across multiple sava projects. This guide explains what each project
provides and how to use them together.

## Project Overview

| Project             | Maven Module                    | Purpose                                                                                   |
|---------------------|---------------------------------|-------------------------------------------------------------------------------------------|
| **sava-core**       | `software.sava.core`            | Account deserialization for Token2022 mints, token accounts, and all token extension types |
| **solana-programs** | `software.sava.solana-programs` | Manually written instruction helpers                                                      |
| **idl-clients-spl** | `software.sava.idl-clients-spl` | Generated instruction helpers with instruction data parsers and `Token2022Error`          |

## sava-core — Account (De)Serialization

sava-core provides full serialization and deserialization support for Token 2022 mint accounts, token accounts, and all
token extension types.

### Key Classes

- `software.sava.core.accounts.token.Token2022` — Token 2022 mint accounts with extensions
- `software.sava.core.accounts.token.Token2022Account` — Token 2022 token accounts with extensions
- `software.sava.core.accounts.token.extensions.TokenExtension` — Sealed interface for all extension types
- `software.sava.core.accounts.token.extensions.ExtensionType` — Enum of all supported extension types

### Reading a Token 2022 Mint

```java
Token2022 mint = Token2022.read(publicKey, accountData);

// Access the base mint data
Mint baseMint = mint.mint();

// Access extensions
Map<ExtensionType, TokenExtension> extensions = mint.extensions();

// Get a specific extension
// Note: 
TokenMetadata metadata = (TokenMetadata) extensions.get(ExtensionType.TokenMetadata);
TransferFeeConfig feeConfig = (TransferFeeConfig) extensions.get(ExtensionType.TransferFeeConfig);

// All extensions are sealed records so that they can be deconstructed and switched over.
if(extensions.get(ExtensionType.PermanentDelegate) instanceof PermanentDelegate(PublicKey delegate)) {
}
```

### Reading a Token 2022 Token Account

```java
Token2022Account account = Token2022Account.read(publicKey, accountData);

// Access the base token account data
TokenAccount baseAccount = account.tokenAccount();

// Access extensions
TransferFeeAmount feeAmount = (TransferFeeAmount) account.extensions().get(ExtensionType.TransferFeeAmount);
```

## idl-clients-spl

This project contains code generated from the Token 2022 program's IDL. It provides instruction builders **and**
instruction data parsers (deserialization), making it the preferred choice for most instruction construction.

### Key Classes

- `software.sava.idl.clients.spl.token_2022.gen.Token2022Program` — All instruction helpers with data parsers
- `software.sava.idl.clients.spl.token_2022.gen.Token2022Error` — Program error codes and messages
- `software.sava.idl.clients.spl.token_2022.gen.types.*` — Supporting types (`AuthorityType`, `ExtensionType`,
  `AccountState`, etc.)

### Configuration

Some types from the IDL are excluded because the generator doesn't fully support their codama definition and they are
already available via sava-core. See [main_net_programs.json](../main_net_programs.json) for the full generator
configuration.

```json
{
  "externalTypes": {
    "Mint": "software.sava.core.accounts.token.Token2022",
    "Token": "software.sava.core.accounts.token.Token2022Account",
    "Extension": "software.sava.core.accounts.token.extensions.TokenExtension"
  }
}
```

The `updateTokenMetadataField` instruction and `TokenMetadataField` type are excluded from generation because the field type
is an enum with a tuple variant (`Key(String)`) which requires special handling. A manual implementation is provided in
`Token2022Instructions`.

```json
{
  "ignoreInstructions": ["updateTokenMetadataField"],
  "excludeTypes": ["TokenMetadataField"]
}
```

### updateTokenMetadataField (Token2022Instructions)

The `updateTokenMetadataField` instruction is manually implemented in `Token2022Instructions` since it is excluded from
code generation. It supports updating the standard fields (name, symbol, URI) as well as custom key-value pairs.

```java
import software.sava.idl.clients.spl.token_2022.Token2022Instructions;
import software.sava.idl.clients.spl.token_2022.Token2022Instructions.TokenMetadataField;

// Update the name field
Instruction ix = Token2022Instructions.updateTokenMetadataName(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    "New Token Name"
);

// Update the symbol field
Instruction ix = Token2022Instructions.updateTokenMetadataSymbol(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    "NTK"
);

// Update the URI field
Instruction ix = Token2022Instructions.updateTokenMetadataUri(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    "https://example.com/new-metadata.json"
);

// Update a custom key-value pair
Instruction ix = Token2022Instructions.updateTokenMetadataCustomField(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    "website",
    "https://example.com"
);

// Or use the general method with explicit field type
Instruction ix = Token2022Instructions.updateTokenMetadataField(
    invokedToken2022ProgramMeta,
    metadataKey,
    updateAuthorityKey,
    TokenMetadataField.Key,
    "description",
    "A great token"
);
```

Parsing instruction data is also supported:

```java
var parsed = Token2022Instructions.UpdateTokenMetadataFieldIxData.read(instruction);
TokenMetadataField field = parsed.field();
String value = parsed.value();
String key = parsed.key(); // non-null only when field == Key
```

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

### Error Handling

`Token2022Error` provides a sealed interface with all program error codes:

```java
Token2022Error error = Token2022Error.getInstance(errorCode);
if(error instanceof Token2022Error.InsufficientFunds){
    // handle insufficient funds
}
System.out.println(error.msg()); // Human-readable error message
```

## solana-programs — Manual Instruction Helpers

The solana-programs project provides manually written instruction helpers which are mostly the equivalent of what idl-clients-spl
provides.

### Key Class

- `software.sava.solana.programs.token.Token2022Program` — Manually written instruction helpers

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

| Use Case                                   | Recommended Project                      |
|--------------------------------------------|------------------------------------------|
| Token 2022 mint accounts                   | **sava-core** (`Token2022`)              |
| Token 2022 token accounts                  | **sava-core** (`Token2022Account`)       |
| Reading token extension data               | **sava-core** (extension types)          |
| Building instructions                      | **idl-clients-spl** (`Token2022Program`) |
| `updateTokenMetadataField` instruction     | **idl-clients-spl** (`Token2022Instructions`) |
| Parsing instruction data from transactions | **idl-clients-spl** (`IxData.read()`)    |
| Handling program errors                    | **idl-clients-spl** (`Token2022Error`)   |
