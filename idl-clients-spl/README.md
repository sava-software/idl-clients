# SPL

Java clients and generated types for Solana Program Library (SPL) programs including Token, Associated Token, System, and Attestation Service programs.

## Quick Start

```java
var solanaAccounts = SolanaAccounts.MAIN_NET;
var splClient = SPLClient.createClient(solanaAccounts);

// Create an account-specific client
var owner = PublicKey.fromBase58Encoded(/* your public key */);
var feePayer = PublicKey.fromBase58Encoded(/* fee payer public key */);
var splAccountClient = splClient.createAccountClient(owner, feePayer);
```

## SPLClient

The `SPLClient` interface provides core SPL functionality for working with tokens, accounts, and lookup tables.

### Creating Clients

```java
// Main net client
var splClient = SPLClient.createClient();

// Custom network
var splClient = SPLClient.createClient(SolanaAccounts.DEV_NET);
```

### Finding Associated Token Accounts (ATAs)

```java
var owner = PublicKey.fromBase58Encoded(/* owner public key */);
var mint = PublicKey.fromBase58Encoded(/* token mint */);

// Find ATA for standard Token Program
ProgramDerivedAddress ata = splClient.findATA(owner, mint);

// Find ATA for Token-2022 Program
ProgramDerivedAddress ata2022 = splClient.find2022ATA(owner, mint);

// Find ATA with explicit token program
ProgramDerivedAddress ata = splClient.findATA(owner, tokenProgram, mint);
```

### Account Creation

```java
var payerKey = PublicKey.fromBase58Encoded(/* payer */);
var newAccountKey = PublicKey.fromBase58Encoded(/* new account */);
long lamports = 1_000_000;
long space = 165; // Token account size
var programOwner = solanaAccounts.tokenProgram();

Instruction createAccountIx = splClient.createAccount(payerKey, newAccountKey, lamports, space, programOwner);

// With seed
var accountWithSeed = AccountWithSeed.createAccount(/* base, seed, programId */);
Instruction createWithSeedIx = splClient.createAccountWithSeed(payerKey, accountWithSeed, lamports, space, programOwner);
```

### Address Lookup Tables

```java
var authority = PublicKey.fromBase58Encoded(/* authority */);
long recentSlot = 123456789L;

// Find lookup table address
ProgramDerivedAddress tableAddress = splClient.findLookupTableAddress(authority, recentSlot);

// Create lookup table
Instruction createTableIx = splClient.createLookupTable(tableAddress, authority, recentSlot);

// Extend lookup table with new addresses
List<PublicKey> newAddresses = List.of(/* addresses to add */);
Instruction extendTableIx = splClient.extendLookupTable(tableAddress.publicKey(), authority, newAddresses);

// Deactivate, close, or freeze lookup table
Instruction deactivateIx = splClient.deactivateLookupTable(tableAddress.publicKey(), authority);
Instruction closeIx = splClient.closeLookupTable(tableAddress.publicKey(), authority);
Instruction freezeIx = splClient.freezeLookupTable(tableAddress.publicKey(), authority);
```

### Compute Budget

```java
Instruction limitIx = splClient.computeBudgetLimit(200_000);
Instruction priceIx = splClient.computeBudgetPrice(1_000); // micro-lamports per compute unit
```

## SPLAccountClient

The `SPLAccountClient` interface provides owner-specific operations. It extends the functionality of `SPLClient` with a bound owner and fee payer.

### Creating Account Clients

```java
var solanaAccounts = SolanaAccounts.MAIN_NET;
var owner = PublicKey.fromBase58Encoded(/* owner */);
var feePayer = AccountMeta.createFeePayer(/* fee payer */);

var splAccountClient = SPLAccountClient.createClient(solanaAccounts, owner, feePayer);

// Or from an existing SPLClient
var splAccountClient = splClient.createAccountClient(owner, feePayer);
```

### Wrapping and Unwrapping SOL

```java
// Wrap SOL to native token account
long lamports = 1_000_000_000; // 1 SOL
List<Instruction> wrapIxs = splAccountClient.wrapSOL(lamports);

// Sync native balance after receiving SOL
Instruction syncIx = splAccountClient.syncNative();

// Unwrap SOL (close wrapped SOL account)
Instruction unwrapIx = splAccountClient.unwrapSOL();

// Get wrapped SOL PDA
ProgramDerivedAddress wrappedSolPda = splAccountClient.wrappedSolPDA();
```

### Token Transfers

```java
var fromTokenAccount = PublicKey.fromBase58Encoded(/* source token account */);
var toTokenAccount = PublicKey.fromBase58Encoded(/* destination token account */);
var tokenMint = PublicKey.fromBase58Encoded(/* mint */);
var invokedTokenProgram = solanaAccounts.invokedTokenProgram();
long amount = 1_000_000;
int decimals = 6;

Instruction transferIx = splAccountClient.transferTokenChecked(
    invokedTokenProgram,
    fromTokenAccount,
    toTokenAccount,
    amount,
    decimals,
    tokenMint
);
```

### SOL Transfers

```java
var toPublicKey = PublicKey.fromBase58Encoded(/* recipient */);
long lamports = 1_000_000_000;

Instruction transferSolIx = splAccountClient.transferSolLamports(toPublicKey, lamports);
```

### Creating Associated Token Accounts

```java
var ata = PublicKey.fromBase58Encoded(/* ATA address */);
var mint = PublicKey.fromBase58Encoded(/* token mint */);
var tokenProgram = solanaAccounts.tokenProgram();

// Create ATA (idempotent - won't fail if already exists)
Instruction createAtaIx = splAccountClient.createATAForOwnerFundedByFeePayer(
    true,  // idempotent
    ata,
    mint,
    tokenProgram
);
```

### Closing Token Accounts

```java
var tokenAccount = PublicKey.fromBase58Encoded(/* token account to close */);
var invokedTokenProgram = solanaAccounts.invokedTokenProgram();

Instruction closeIx = splAccountClient.closeTokenAccount(invokedTokenProgram, tokenAccount);
```

## Generated Program Classes

### TokenProgram

Low-level instruction builders for the SPL Token program.

```java
var invokedTokenProgram = solanaAccounts.invokedTokenProgram();

// Initialize a new mint
Instruction initMintIx = TokenProgram.initializeMint(
    invokedTokenProgram,
    solanaAccounts,
    mintKey,
    decimals,
    mintAuthority,
    freezeAuthority
);

// Transfer tokens
Instruction transferIx = TokenProgram.transfer(
    invokedTokenProgram,
    sourceKey,
    destinationKey,
    authorityKey,
    amount
);

// Transfer with decimal check
Instruction transferCheckedIx = TokenProgram.transferChecked(
    invokedTokenProgram,
    sourceKey,
    mintKey,
    destinationKey,
    authorityKey,
    amount,
    decimals
);

// Mint tokens
Instruction mintToIx = TokenProgram.mintTo(
    invokedTokenProgram,
    mintKey,
    tokenKey,
    mintAuthorityKey,
    amount
);

// Burn tokens
Instruction burnIx = TokenProgram.burn(
    invokedTokenProgram,
    accountKey,
    mintKey,
    authorityKey,
    amount
);

// Set authority
Instruction setAuthIx = TokenProgram.setAuthority(
    invokedTokenProgram,
    ownedKey,
    ownerKey,
    AuthorityType.MintTokens,
    newAuthority
);

// Freeze/thaw accounts
Instruction freezeIx = TokenProgram.freezeAccount(invokedTokenProgram, accountKey, mintKey, ownerKey);
Instruction thawIx = TokenProgram.thawAccount(invokedTokenProgram, accountKey, mintKey, ownerKey);

// Close account
Instruction closeIx = TokenProgram.closeAccount(invokedTokenProgram, accountKey, destinationKey, ownerKey);

// Sync native token balance
Instruction syncNativeIx = TokenProgram.syncNative(invokedTokenProgram, accountKey);
```

### AssociatedTokenProgram

Instruction builders for the Associated Token Account program.

```java
// Find ATA
ProgramDerivedAddress ata = AssociatedTokenPDAs.associatedTokenAddress(
    solanaAccounts.associatedTokenAccountProgram(),
    owner,
    tokenProgram,
    mint
);

// Create ATA instruction
Instruction createAtaIx = AssociatedTokenProgram.create(
    solanaAccounts,
    fundingAccount,
    ata.publicKey(),
    owner,
    mint,
    tokenProgram
);

// Create idempotent (won't fail if exists)
Instruction createIdempotentIx = AssociatedTokenProgram.createIdempotent(
    solanaAccounts,
    fundingAccount,
    ata.publicKey(),
    owner,
    mint,
    tokenProgram
);
```

### System  Program

Instruction builders for the System program.

```java
// Create account
Instruction createAccountIx = SystemProgram.createAccount(
    solanaAccounts.invokedSystemProgram(),
    fromKey,
    newAccountKey,
    lamports,
    space,
    programOwner
);

// Transfer SOL
Instruction transferIx = SystemProgram.transfer(
    solanaAccounts.invokedSystemProgram(),
    fromKey,
    toKey,
    lamports
);

// Allocate space
Instruction allocateIx = SystemProgram.allocate(
    solanaAccounts.invokedSystemProgram(),
    accountKey,
    space
);

// Assign to program
Instruction assignIx = SystemProgram.assign(
    solanaAccounts.invokedSystemProgram(),
    accountKey,
    programOwner
);
```

## Account Types

### Mint

```java
// Read mint account data
Mint mint = Mint.read(accountData, offset);

int decimals = mint.decimals();
long supply = mint.supply();
PublicKey mintAuthority = mint.mintAuthority();
PublicKey freezeAuthority = mint.freezeAuthority();
```

### Token (Token Account)

```java
// Read token account data
Token token = Token.read(accountData, offset);

PublicKey mint = token.mint();
PublicKey owner = token.owner();
long amount = token.amount();
AccountState state = token.state();
```
