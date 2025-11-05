# Drift

## Quick Start

1) Set up a Solana RPC client, keypair, and the NativeProgramAccountClient.

```java
import software.sava.core.accounts.KeyPair;
import software.sava.core.accounts.PublicKey;
import software.sava.rpc.json.http.SolanaRpcClient;
import software.sava.solana.programs.clients.NativeProgramAccountClient;
import software.sava.idl.clients.drift.*;

var rpc = SolanaRpcClient.create("https://api.mainnet-beta.solana.com");
var feePayer = KeyPair.fromBase58Secret(/* your secret key */);
var nativeClient = new NativeProgramAccountClient(rpc, feePayer);
var driftAccounts = DriftAccounts.MAIN_NET;
var drift = DriftProgramClient.createClient(nativeClient, driftAccounts);
```

## Accounts and PDAs

```java
var driftAccounts = DriftAccounts.MAIN_NET;
var programId =driftAccounts.driftProgram();
var signerPda = driftAccounts.driftSignerPDA();
var stateKey = driftAccounts.stateKey();

var mainUser = drift.mainUserAccount(); // 0
var user0 = drift.deriveUserAccount(0);
```

## Market Metadata and Lookup Tables

```java
import java.net.http.HttpClient;

var http = HttpClient.newHttpClient();
var perpMarkets = DynamicPerpMarkets.fetchMarkets(http).join(); // PerpMarkets (mainnet/devnet)
var spotMarkets = DynamicSpotMarkets.fetchMarkets(http).join(); // SpotMarkets (mainnet/devnet)
```

```java
import software.sava.core.accounts.lookup.AddressLookupTable;
import software.sava.rpc.json.http.AccountInfo;

var tables = drift.fetchMarketLookupTables(rpc).join();
```

## Creating a User and User Stats

```java
import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;

var authority = drift.authority();
var payer = drift.feePayer();
int subAccountId = 0;
byte[] name = drift.fixedUserName("bot-0");

var ixCreateStats = drift.initializeUserStats(authority, payer);
var ixCreateUser = drift.initializeUser(
    drift.deriveUserAccount(authority, subAccountId).publicKey(),
    authority,
    payer,
    subAccountId,
    name
);
```

## Depositing and Withdrawing Collateral (Spot)

```java
import software.sava.idl.clients.drift.DynamicSpotMarkets;
import software.sava.idl.clients.drift.SpotMarketConfig;
import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;

var http = HttpClient.newHttpClient();
var spotMarkets = DynamicSpotMarkets.fetchMarkets(http).join();
var usdc = spotMarkets.mainNet().marketConfig(0);

var userTokenAccount = PublicKey.fromBase58Encoded(/* your SPL token account for USDC */);
var tokenProgram = PublicKey.fromBase58Encoded("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
long amount = 1_000_000; // in native units (e.g., 1 USDC = 1_000_000 for 6 decimals)

Instruction depositIx = drift.deposit(userTokenAccount, tokenProgram, usdc, amount);
Instruction withdrawIx = drift.withdraw(userTokenAccount, tokenProgram, usdc, amount);
```

## Placing Orders (Spot and Perp)

```java
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.drift.OrderParamsBuilder;

int marketIndex = 0;

var params = new OrderParamsBuilder(
    PositionDirection.Long,
    1_000_000, // baseAssetAmount
    100_000_000, // price in native units
    0, // marketIndex
    OrderTriggerCondition.Above
);
params
    .orderType(OrderType.Above)
    .marketType(MarketType.Spot)
    .reduceOnly(false)
    .createParams();

Instruction placeOrderIx = drift.placeOrder(params);
```

## Cancel orders:

```java
int orderId = 123;
Instruction cancelIx = drift.cancelOrder(orderId);
```

## Settling PnL and Funding

```java
Instruction settlePnlIx = drift.settlePnl(/* marketIndex */ 0);

// Or for multiple markets
Instruction settleManyIx = drift.settlePnl(new short[]{0, 1}, SettlePnlMode.ALL);

// Funding payments
Instruction settleFundingIx = drift.settleFundingPayment();
```

## Reading Accounts and Data

```java
import software.sava.idl.clients.drift.gen.types.User;
import software.sava.rpc.json.http.AccountInfo;

AccountInfo<User> userInfo = drift.fetchUser(rpc).join();
User user = userInfo.data();
```
