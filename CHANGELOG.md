# Changelog

## [25.16.0](https://github.com/sava-software/idl-clients/compare/25.15.1...25.16.0) (2026-07-15)


### ⚠ BREAKING CHANGES

* memoProgramKey parameters removed from OrcaWhirlpoolsClient methods and the generated instruction builders; callers must pass SolanaAccounts instead.

### Bug Fixes

* auto-wire memoProgramV2 via SolanaAccounts in generated programs ([e363a1c](https://github.com/sava-software/idl-clients/commit/e363a1c18c15034aafc5efad11d23eccc815341a))


### Miscellaneous Chores

* release 25.16.0 ([40fd212](https://github.com/sava-software/idl-clients/commit/40fd212ea9551c071d6e066fc9848d4db909ad68))

## [25.15.1](https://github.com/sava-software/idl-clients/compare/25.15.0...25.15.1) (2026-07-11)


### Bug Fixes

* re-generate with fixed anchor event discriminators ([cc062a7](https://github.com/sava-software/idl-clients/commit/cc062a7151c07f744802cb437e24ff1d8905a23a))

## [25.15.0](https://github.com/sava-software/idl-clients/compare/25.14.3...25.15.0) (2026-07-10)


### Features

* **idl-clients-bundle:** sync PythLazerEMA and IDL schema updates ([f037e5d](https://github.com/sava-software/idl-clients/commit/f037e5d124180d293393d765ed1cd9c1f606f3bd))


### Miscellaneous Chores

* release 25.15.0 ([9bc9c5f](https://github.com/sava-software/idl-clients/commit/9bc9c5fda2b5daf8b10e49b21928c196de4ca356))

## [25.14.3](https://github.com/sava-software/idl-clients/compare/25.14.2...25.14.3) (2026-07-09)


### Features

* **idl-clients-bundle:** sync Jupiter Borrow IDL types ([1118f57](https://github.com/sava-software/idl-clients/commit/1118f571b46e179f29ebe7decdadb7d94e7a0e4d))
* **idl-clients-bundle:** sync Jupiter Doves IDL types ([4642e2c](https://github.com/sava-software/idl-clients/commit/4642e2c3a8139b39dd66b762038f77fbc53c386a))
* **idl-clients-bundle:** sync Jupiter Perpetuals IDL types ([f3c9aed](https://github.com/sava-software/idl-clients/commit/f3c9aedfa7226bd81904f405e3f44965be1390a2))
* **idl-clients-bundle:** sync LoopscaleProtocol admin and state IDL types ([3ace272](https://github.com/sava-software/idl-clients/commit/3ace27204034fa16b052746d5ca40dc725a73b60))

## [25.14.2](https://github.com/sava-software/idl-clients/compare/25.14.1...25.14.2) (2026-07-01)


### Features

* **idl-clients-bundle:** add support for `TotalMintSupply` with `emaTypes` ([35acb1c](https://github.com/sava-software/idl-clients/commit/35acb1c0a57fda555708e972f14199ad2ca95776))
* **idl-clients-bundle:** sync Jupiter Offerbook IDL types ([eb4fedc](https://github.com/sava-software/idl-clients/commit/eb4fedc2183066fa535d9851713fdcc2b9d8dcff))

## [25.14.1](https://github.com/sava-software/idl-clients/compare/25.14.0...25.14.1) (2026-06-30)


### Features

* **idl-clients-bundle:** add new `Swap` types and sync Jupiter Swap IDL ([4956ad9](https://github.com/sava-software/idl-clients/commit/4956ad93f3e4fdb00cf22f977e843dddd79d4297))
* **idl-clients-bundle:** add optional annotations to IDL-generated param docs ([430a6bb](https://github.com/sava-software/idl-clients/commit/430a6bbcc65ea8ec5db065a3c6a6598e5b24f419))

## [25.14.0](https://github.com/sava-software/idl-clients/compare/25.13.3...25.14.0) (2026-06-29)


### ⚠ BREAKING CHANGES

* u32 fields and generated APIs now use Java long to represent the full unsigned 32-bit range. Widening is now consistent for both u16 and u32 types. Only u8 scalar values are widened, u8[] and vec<u8> are not. Neither u64 scalar or collections are widened as there is no larger primitive type in Java.
* **idl-clients-bundle:** Pod wrappers have been removed; generated types now directly use primitive types. Code relying on `Pod` methods must be updated to use utility methods from `ByteUtil` or equivalent.

### Features

* **idl-clients-bundle:** add `investWithMaxAmount` function and `WithdrawQueue` support ([03901c2](https://github.com/sava-software/idl-clients/commit/03901c26f1b3799be28b21a30e7e4b9f8ac7252d))
* **idl-clients-bundle:** update types to use primitives for serialization ([5e16518](https://github.com/sava-software/idl-clients/commit/5e165184a620e6cefd7151ee78e405794eb977fe))
* widen u32 values to Java long ([8a1317d](https://github.com/sava-software/idl-clients/commit/8a1317d725b7d17f20f488e6cbd83b182df8088f))


### Bug Fixes

* **idl-clients-bundle:** use unsigned int conversion for u16 values in generated types ([3ecd4d1](https://github.com/sava-software/idl-clients/commit/3ecd4d11ea86a12b522931a214681cfad7696d27))
* **idl-clients-spl:** correct method for writing `isNative` long values ([62c3fb2](https://github.com/sava-software/idl-clients/commit/62c3fb258f8f075ac7858124eda0c010bdbaa788))


### Miscellaneous Chores

* release 25.14.0 ([0104b3c](https://github.com/sava-software/idl-clients/commit/0104b3ccc75c0d64b7508dc27bf9978dccc9d4e6))

## [25.13.3](https://github.com/sava-software/idl-clients/compare/25.13.2...25.13.3) (2026-06-20)


### Features

* **idl-clients-bundle:** sync Marginfi v2 IDL types ([5264a3a](https://github.com/sava-software/idl-clients/commit/5264a3a9edaf6e71e81f4bb6d2a8d8107b5f6b41))
* **marginfi:** extend MarginfiClient with PDA account operations and conditional orders ([72a59c5](https://github.com/sava-software/idl-clients/commit/72a59c51a7942bba1ed4d541606305a375236f9f))

## [25.13.2](https://github.com/sava-software/idl-clients/compare/25.13.1...25.13.2) (2026-06-20)


### Miscellaneous Chores

* release 25.13.2 ([6d87717](https://github.com/sava-software/idl-clients/commit/6d8771797687648400d8b496b9614cecd09ff40a))

## [25.13.1](https://github.com/sava-software/idl-clients/compare/25.13.0...25.13.1) (2026-06-20)


### Features

* **phoenix:** add support for usdcMint in PhoenixAccounts ([85d3b80](https://github.com/sava-software/idl-clients/commit/85d3b80725e1f02d724ec3f6154e8f9554a76693))


### Bug Fixes

* **phoenix:** fix duplicate discriminator serde ([fc615ef](https://github.com/sava-software/idl-clients/commit/fc615ef53177a39af7cf83111f8ce443d930f7e6))

## [25.13.0](https://github.com/sava-software/idl-clients/compare/25.12.14...25.13.0) (2026-06-17)


### ⚠ BREAKING CHANGES

* the per-program Maven artifacts and JPMS modules have been removed. Depend on software.sava.idl.clients.spl for core/SPL types and software.sava.idl.clients.bundle for all other programs. The corresponding `requires` directives in module-info.java must be updated accordingly.
* the per-program Maven artifacts and JPMS modules have been removed. Depend on software.sava.idl.clients.spl for core/SPL types and software.sava.idl.clients.bundle for all other programs. The corresponding `requires` directives in module-info.java must be updated accordingly.

### Miscellaneous Chores

* release 25.13.0 ([c09e6c1](https://github.com/sava-software/idl-clients/commit/c09e6c1d6936222146d48d9c41933cacc9eba499))


### Code Refactoring

* consolidate modules into idl-clients-spl and idl-clients-bundle ([d62c489](https://github.com/sava-software/idl-clients/commit/d62c489fb18a4eebdc429dd913deed28342fcf35))
* consolidate modules into idl-clients-spl and idl-clients-bundle ([48920b4](https://github.com/sava-software/idl-clients/commit/48920b4f3c3821b00414819b4bec3936df58017e))

## [25.12.14](https://github.com/sava-software/idl-clients/compare/25.12.13...25.12.14) (2026-06-15)


### Features

* **idl-clients-orca:** add tick index validation and support BigInteger deltas ([2d919f0](https://github.com/sava-software/idl-clients/commit/2d919f01aa3cef93532bbc801a100c150890d89d))

## [25.12.13](https://github.com/sava-software/idl-clients/compare/25.12.12...25.12.13) (2026-06-14)


### Features

* **idl-clients-jupiter:** add BisonFiPredict, ByrealDynamicV3, and Flux swap types ([1eda314](https://github.com/sava-software/idl-clients/commit/1eda314c3bd7c066f959b7914d0c4164023b2e15))
* **idl-clients-loopscale:** add LoopscalePDAs utility and refactor PDA logic ([5dff911](https://github.com/sava-software/idl-clients/commit/5dff91164a8c4e370ae2c94b940eb73bb26ab299))

## [25.12.12](https://github.com/sava-software/idl-clients/compare/25.12.11...25.12.12) (2026-06-08)


### Features

* **build:** refine settings.gradle and dependabot schedule ([673cd1b](https://github.com/sava-software/idl-clients/commit/673cd1b7b5991a4b622118b531003aebfd6ecf5c))
* **build:** update dependencies and workflows for enhanced Gradle handling ([0c69af6](https://github.com/sava-software/idl-clients/commit/0c69af694c411170aac83a2ccd8aace9a1191b8a))
* **idl-clients-oracles:** sync Wormhole Post Message and Verify VAA IDLs ([d0d7bf1](https://github.com/sava-software/idl-clients/commit/d0d7bf1c6e9619d57be54ab0999bf03ddd300300))
* **spl:** Add KaminoVaultPDAs ([d128886](https://github.com/sava-software/idl-clients/commit/d128886e946d3e3dae44d80891cfd4b83f8b2c1b))
* **spl:** sync token-2022 ([e8a748e](https://github.com/sava-software/idl-clients/commit/e8a748e3e8737d7799bc8ba830321baa0cb6f708))

## [25.12.11](https://github.com/sava-software/idl-clients/compare/25.12.10...25.12.11) (2026-05-30)


### Features

* **kamino:** add `permissionedOps` to ReserveConfig ([111eb15](https://github.com/sava-software/idl-clients/commit/111eb1585821c47fddc7d39d9e1d1f7a48d068da))
* **spl:** add createAccountAllowPrefund support to SystemProgram ([9975eab](https://github.com/sava-software/idl-clients/commit/9975eab08b749fc3bcaa58a6fe2757b338794995))
* **spl:** add stake, memo, and signature validation support ([6cfa155](https://github.com/sava-software/idl-clients/commit/6cfa155872f3b90f1a8970249fbccc92454e8f33))

## [25.12.10](https://github.com/sava-software/idl-clients/compare/25.12.9...25.12.10) (2026-05-27)


### Features

* **spl:** add address lookup table IDL support ([40e1e6c](https://github.com/sava-software/idl-clients/commit/40e1e6c5a2f2bd7d9b358a07f34790cfc955778b))
* **spl:** add Compute Budget program support ([21047bc](https://github.com/sava-software/idl-clients/commit/21047bc6c4efa0cf7153f26c801b3f64face2d7f))
* **spl:** add stake-related IDL types ([bbcb91d](https://github.com/sava-software/idl-clients/commit/bbcb91de48dca457234b04d61d2fcf1cc0eb4e7a))

## [25.12.9](https://github.com/sava-software/idl-clients/compare/25.12.8...25.12.9) (2026-05-26)


### Features

* add generated comment to all generated source files. ([469851f](https://github.com/sava-software/idl-clients/commit/469851f66d385a645a6914c43fd204a64d17f3e0))
* **kamino:** add Conditional and TotalMintSupply entries ([0fc722d](https://github.com/sava-software/idl-clients/commit/0fc722d675e7380d1efdc187741f72adb8e3314e))

## [25.12.8](https://github.com/sava-software/idl-clients/compare/25.12.7...25.12.8) (2026-05-22)


### Features

* **jupiter:** add jupiter borrow client ([bfbb866](https://github.com/sava-software/idl-clients/commit/bfbb86672a2893148b167011528fce4f9487fc2a))
* **jupiter:** implement price client for Jupiter API ([db38388](https://github.com/sava-software/idl-clients/commit/db3838891bdb2ca3d1edd515e7a0b45eb2271883))
* **kamino:** extend FarmsProgram support in lend client ([8d9693b](https://github.com/sava-software/idl-clients/commit/8d9693bffcf200422fcb13a42d0a35a16a700cad))
* **kamino:** extend vaults client with deposit, withdraw & redeem methods ([de6e1ce](https://github.com/sava-software/idl-clients/commit/de6e1ce6af3dfcc0ba2a02385ad4d51ce929fb4f))
* **kamino:** implement Lending client and add helper for remaining accounts ([0ff8c43](https://github.com/sava-software/idl-clients/commit/0ff8c4375402e973cd9f4105a76565eac88119f9))
* **marinade:** extend stake pool client with deposit, withdrawal & liquidity ops ([d24233b](https://github.com/sava-software/idl-clients/commit/d24233b161b62b6bbb6f054b48f74ab8b080659d))
* **meteora:** add DlmmRemainingAccounts helpers and extend Dlmm client ([b2e9ea1](https://github.com/sava-software/idl-clients/commit/b2e9ea1b0aa5db3718fd4e6db33b924837a5bd9c))
* **orca:** add Whirlpool quote helpers and tests ([0057a75](https://github.com/sava-software/idl-clients/commit/0057a7574bc79b83ac36200f1eb01da24c861f18))
* **orca:** sync Whirlpool program IDL ([81fbce0](https://github.com/sava-software/idl-clients/commit/81fbce0e0ecfd8ab9b8b1da7ba1be3f380944238))
* **phoenix:** complete Phoenix client implementation ([9c13af0](https://github.com/sava-software/idl-clients/commit/9c13af0baef7a6b75d20a01ee684224b9aff7922))

## [25.12.7](https://github.com/sava-software/idl-clients/compare/25.12.6...25.12.7) (2026-05-19)


### Features

* **ci:** add release-please automation and PR checks workflows ([97a616c](https://github.com/sava-software/idl-clients/commit/97a616c0403941533ace1e2ad483b00380969920))
* **phoenix:** sync perpetuals program IDL ([62118f4](https://github.com/sava-software/idl-clients/commit/62118f445718471070d125f030e3613a3285c2c0))
* **phoenix:** sync with dev perpetuals program IDL ([402d52f](https://github.com/sava-software/idl-clients/commit/402d52f36a5349d2facff838c15bf068070bd13b))
