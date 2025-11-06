# Drift

## Quick Start

```java
var rpc = SolanaRpcClient.create("https://api.mainnet-beta.solana.com");
var feePayer = KeyPair.fromBase58Secret(/* your secret key */);
var nativeClient = new NativeProgramAccountClient(rpc, feePayer);
var driftAccounts = DriftAccounts.MAIN_NET;
var drift = DriftProgramClient.createClient(nativeClient, driftAccounts);
```

## Accounts and PDAs

```java
var driftAccounts = DriftAccounts.MAIN_NET;
var programId = driftAccounts.driftProgram();
var signerPda = driftAccounts.driftSignerPDA();
var stateKey = driftAccounts.stateKey();

var mainUser = drift.mainUserAccount(); // User Account 0
var user0 = drift.deriveUserAccount(0);
```

## Market Metadata

Fetch relevant market context which does not exist onchain.

```java
var http = HttpClient.newHttpClient();

// https://github.com/drift-labs/protocol-v2/blob/master/sdk/src/constants/spotMarkets.ts
var spotMarkets = DynamicSpotMarkets.fetchMarkets(http).join();

// https://github.com/drift-labs/protocol-v2/blob/master/sdk/src/constants/perpMarkets.ts
var perpMarkets = DynamicPerpMarkets.fetchMarkets(http).join();
```

## Lookup Tables

Drift managed lookup tables to reduce transaction size.

```java
var tables = drift.fetchMarketLookupTables(rpc).join();
```

## Creating a User and User Stats

```java
var authority = drift.authority();
var payer = drift.feePayer();
int subAccountId = 0;
byte[] name = drift.fixedUserName("bot-0");

var createStatsIx = drift.initializeUserStats(authority, payer);
var createUserIx = drift.initializeUser(
    drift.deriveUserAccount(authority, subAccountId).publicKey(),
    authority,
    payer,
    subAccountId,
    name
);
```

## Depositing and Withdrawing Collateral

```java
var http = HttpClient.newHttpClient();
var spotMarkets = DynamicSpotMarkets.fetchMarkets(http).join();
var usdc = spotMarkets.mainNet().marketConfig(0);

var userTokenAccount = PublicKey.fromBase58Encoded(/* your SPL token account for USDC */);
var tokenProgram = PublicKey.fromBase58Encoded("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA");
long amount = 1_000_000; // in native units (e.g., 1 USDC = 1_000_000 for 6 decimals)

Instruction depositIx = drift.deposit(userTokenAccount, tokenProgram, usdc, amount);

Instruction withdrawIx = drift.withdraw(userTokenAccount, tokenProgram, usdc, amount);
```

## Order Execution

```java
int marketIndex = 0;

var builder = new OrderParamsBuilder(
    PositionDirection.Long,
    1_000_000, // baseAssetAmount
    100_000_000, // price in native units
    0, // marketIndex
    OrderTriggerCondition.Above
);
byte orderId = 123;
var params = builder.orderType(OrderType.Limit)
    .userOrderId(orderId)
    .marketType(MarketType.Spot)
    .reduceOnly(false)
    .postOnly(PostOnlyParam.MustPostOnly)
    .createParams();

Instruction placeOrderIx = drift.placeOrder(params);

Instruction cancelOrderIx = drift.cancelOrder(orderId);
```

## Settling PnL and Funding

```java
Instruction settlePnlIx = drift.settlePnl(/* marketIndex */ 0);

// Settle multiple markets
Instruction settleManyIx = drift.settlePnl(new short[]{0, 1}, SettlePnlMode.ALL);

Instruction settleFundingIx = drift.settleFundingPayment();
```

## Reading Accounts and Data

```java
AccountInfo<User> userInfo = drift.fetchUser(rpc).join();
User user = userInfo.data();
```
