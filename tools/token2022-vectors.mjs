// Emits differential encoding vectors for the Token 2022 program, from solana-program/token-2022's
// own generated JavaScript client.
//
// Same argument as tools/stake-vectors.mjs, one program over: every other Token 2022 test here is a
// round trip, an instruction built with the generated builder and read back with the generated
// `IxData`. Builder and reader are emitted from one IDL by one generator, so they move together —
// a systematic change to the wire format (a discriminator width, a size-prefix width, a field
// order, a u16 enum narrowed to u8) leaves every round trip passing. Only a *second, independent*
// encoder can see it.
//
// That encoder is upstream's: `clients/js/src/generated/instructions/*.ts` is rendered by
// @codama/renderers-js from the same codama IDL this repository generates from, so it is the
// closest thing to a peer implementation that exists. It is not ground truth — both sides descend
// from one document, and nothing here says that document matches the deployed program.
//
//   cd <solana-program/token-2022 checkout>/clients/js && pnpm install --frozen-lockfile
//   node tools/token2022-vectors.mjs <that checkout> [output path]
//
// The client is TypeScript and its `AccountState`, `AuthorityType` and `ExtensionType` encoders
// need those `enum`s at runtime, which Node's type stripping cannot erase, so this transpiles
// `src/generated` with the checkout's own `tsc` into a temporary directory (removed on exit) whose
// `node_modules` is a symlink back into `clients/js`. Nothing is written inside the checkout, and
// `src/generated` rather than `src` is compiled deliberately: the hand-written helpers beside it
// are not the encoder under comparison.
//
// Unlike the Stake vectors, each line also records the account list the JavaScript builder
// produced — address, writable, signer, in order — because Token 2022 is where the account side
// has the interesting cases: an optional account the caller omits, a sysvar the builder fills in,
// a signer that is only a signer when a signer was passed. `multiSigners` is always empty and the
// `signers` / `sources` remaining-account arrays are always `[]`: those are appended past the
// declared account list, which no IDL expresses and no generated builder emits, so including them
// would manufacture a difference rather than measure one.
//
// Four instructions the JavaScript client exposes have no vector, because Token2022Program does
// not declare them and there is nothing to compare against:
//
//   batch                             a client-side helper that packs several instructions into
//                                     one; idl-src-gen skips it, so the Java client has 99
//                                     instructions where the IDL has 100
//   createAssociatedToken             the Associated Token Account program's instructions, carried
//   createAssociatedTokenIdempotent   in this IDL's `additionalPrograms` and generated here into
//   recoverNestedAssociatedToken      the associated_token package, at a different program address
//
// The output is committed as a test resource, and the diff is the review. Do not regenerate on a
// schedule: these vectors are instruction data and account lists, and upstream's repository moving
// is not evidence that either moved. Run it to adjudicate a comparison that has failed, or to
// cover an instruction the IDL has grown — hand-authoring those bytes would void the point of
// them, which is that they come from an implementation neither this repository nor its generator
// produced. See tools/README.md.
//
// A vector's name is `<instruction>.<case>`; Token2022ReferenceEncodingTests requires every
// instruction Token2022Program declares to have at least one, and pins each case's Java literals
// against the constants defined at the top of `render()` below.
import { spawnSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { existsSync, mkdtempSync, readFileSync, rmSync, symlinkSync, writeFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { pathToFileURL } from 'node:url';

const repo = process.argv[2];
if (!repo) {
  console.error('usage: node tools/token2022-vectors.mjs <solana-program/token-2022 checkout> [output]');
  process.exit(2);
}
const out = process.argv[3] ?? 'idl-clients-spl/src/test/resources/token_2022/reference-vectors.txt';

const clientsJs = resolve(repo, 'clients/js');
const modules = resolve(clientsJs, 'node_modules');
const tsc = resolve(modules, '.bin/tsc');
if (!existsSync(tsc)) {
  console.error(`${tsc} is missing — run \`pnpm install --frozen-lockfile\` in ${clientsJs}`);
  process.exit(2);
}

const work = mkdtempSync(join(tmpdir(), 'token2022-vectors-'));
let client;
try {
  symlinkSync(modules, join(work, 'node_modules'), 'dir');
  // --ignoreConfig: their tsconfig declares `include`, which tsc refuses to combine with a named
  // entry file. Everything it sets that matters to *emit* is restated here; the type-only options
  // it drops (strict, noUnusedLocals) cannot change the bytes. CommonJS output keeps TypeScript's
  // extensionless relative imports resolvable without rewriting them.
  const generated = resolve(clientsJs, 'src/generated');
  const tsvc = spawnSync(tsc, [
    '--ignoreConfig', resolve(generated, 'index.ts'),
    '--outDir', work,
    '--rootDir', generated,
    '--module', 'commonjs',
    '--target', 'es2022',
    '--skipLibCheck',
    '--esModuleInterop',
  ], { stdio: 'inherit' });
  if (tsvc.status !== 0) {
    throw new Error(`tsc exited ${tsvc.status}`);
  }
  client = await import(pathToFileURL(join(work, 'index.js')).href);

  const kit = createRequire(resolve(clientsJs, 'package.json'))('@solana/kit');
  writeFileSync(out, render(client, kit, provenance(repo, clientsJs)));
  console.log(`wrote ${out}`);
} finally {
  rmSync(work, { recursive: true, force: true });
}

/// What the bytes were actually produced from.
///
/// The checkout revision, the generated client's own revision, and a digest of the lockfile that
/// pins every encoder actually loaded — the encoders are codec combinators from `@solana/kit`, so
/// a dependency bump changes what they emit without touching `src/generated` at all. A dirty tree
/// under either input is refused rather than recorded, because there is no revision that describes
/// it.
function provenance(repo, clientsJs) {
  const git = (...args) => spawnSync('git', args, { cwd: repo, encoding: 'utf8' }).stdout?.trim();

  const dirty = git('status', '--porcelain', '--', 'clients/js/src/generated', 'clients/js/pnpm-lock.yaml');
  if (dirty) {
    console.error('the checkout has uncommitted changes under the inputs these vectors depend on:\n'
        + dirty + '\ncommit or stash them — a recorded revision that does not describe the bytes is'
        + ' worse than none.');
    process.exit(2);
  }

  const lock = createHash('sha256')
      .update(readFileSync(resolve(clientsJs, 'pnpm-lock.yaml')))
      .digest('hex');
  return {
    head: git('rev-parse', 'HEAD') || '(unknown)',
    generated: git('log', '-1', '--format=%H', '--', 'clients/js/src/generated') || '(unknown)',
    lock,
    version: createRequire(resolve(clientsJs, 'package.json'))('./package.json').version,
  };
}

function render(client, kit, { head, generated, lock, version }) {
  const labels = new Map();

  // A key whose bytes all differ and ascend: byte i is (seed + i * 7) & 0xff. A pubkey of repeated
  // bytes would survive being written backwards; this one does not. Mirrored by
  // Token2022ReferenceEncodingTests.key(int).
  const k = seed => {
    const bytes = new Uint8Array(32);
    for (let i = 0; i < 32; ++i) {
      bytes[i] = (seed + i * 7) & 0xff;
    }
    const address = kit.getAddressDecoder().decode(bytes);
    labels.set(address, `k(0x${seed.toString(16).padStart(2, '0')})`);
    return address;
  };

  // A ciphertext byte is 1 + ((seed + i * 7) % 255), so every byte is non-zero and the pattern
  // repeats only after 255 — a 36- or 64-byte window of it is recognisable on sight and cannot be
  // confused with the zero fill a mis-sized copy would leave. Mirrored by
  // Token2022ReferenceEncodingTests.cipher(int, int).
  const cipher = (seed, length) => {
    const bytes = new Uint8Array(length);
    for (let i = 0; i < length; ++i) {
      bytes[i] = 1 + ((seed + i * 7) % 255);
    }
    labels.set(bytes, `cipher(0x${seed.toString(16).padStart(2, '0')}, ${length})`);
    return bytes;
  };

  const { some, none, createNoopSigner } = kit;
  // An account is a signer in the built instruction only when a signer object was passed for it.
  // The generated Java builders mark the same accounts `createReadOnlySigner` / `createWritableSigner`
  // unconditionally, so every account the IDL declares as a signer gets one here.
  const s = address => createNoopSigner(address);

  // Accounts the builders fill in themselves, named so the vector comments read.
  const RENT = 'SysvarRent111111111111111111111111111111111';
  const INSTRUCTIONS = 'Sysvar1nstructions1111111111111111111111111';
  const SYSTEM = '11111111111111111111111111111111';
  labels.set(RENT, 'rentSysVar');
  labels.set(INSTRUCTIONS, 'instructionsSysVar');
  labels.set(SYSTEM, 'systemProgram');

  // ---------------------------------------------------------------------------
  // The values every vector is built from. Token2022ReferenceEncodingTests defines the same set,
  // one constant per line here, so the two files can be read side by side.
  // ---------------------------------------------------------------------------
  const MINT = k(0x11);
  const TOKEN = k(0x22);
  const AUTHORITY = k(0x33);
  const DESTINATION = k(0x44);
  const DELEGATE = k(0x55);
  const PAYER = k(0x66);
  const METADATA = k(0x77);
  const GROUP = k(0x88);
  const MEMBER = k(0x99);
  const EQUALITY_RECORD = k(0xaa);
  const VALIDITY_RECORD = k(0xbb);
  const RANGE_RECORD = k(0xcc);
  const FEE_SIGMA_RECORD = k(0xdd);
  const FEE_VALIDITY_RECORD = k(0xee);
  const ELGAMAL_REGISTRY = k(0x0f);
  const NEW_AUTHORITY = k(0x1f);
  const ELGAMAL_PUBKEY = k(0x2f);
  const HOOK_PROGRAM = k(0x3f);
  const SOURCE = k(0x4f);
  const FEE_RECEIVER = k(0x5f);
  const NATIVE_MINT = k(0x6f);
  const SECOND_AUTHORITY = k(0x7f);
  const POINTER_ADDRESS = k(0x8f);
  const MULTISIG = k(0x9f);

  const AMOUNT = 4_230_000_000_000n;
  // u64 max: Java has no unsigned long, so this is -1L there. If either side sign-extended or
  // narrowed, these eight bytes would not be all-ones.
  const MAX_U64 = 18_446_744_073_709_551_615n;
  const DECIMALS = 9;
  const M = 3;
  const FEE = 7_777n;
  const MAXIMUM_FEE = 5_000_000n;
  const FEE_BASIS_POINTS = 1_234; // u16
  const RATE = 4_321; // i16
  const NEGATIVE_RATE = -1_234; // i16 — a plain `int` on the Java side, so the sign is the test
  const MULTIPLIER = 3.141592653589793; // f64, a value no narrower float can hold
  const NEGATIVE_MULTIPLIER = -0.5;
  const EFFECTIVE_TIMESTAMP = 1_700_000_000n; // i64
  const PROOF_OFFSET = 5; // i8
  const NEGATIVE_PROOF_OFFSET = -3; // i8 — a plain `int` on the Java side
  const CREDIT_COUNTER = 1_024n;
  const MAX_PENDING_CREDITS = 65_536n;
  const NUM_TOKEN_ACCOUNTS = 7;
  const MAX_SIZE = 9_000n;
  const START = 16n;
  const END = 48n;

  const AVAILABLE_BALANCE = cipher(0xc3, 36); // decryptableBalance, 36 bytes
  const SUPPLY_BALANCE = cipher(0xd4, 36);
  const AUDITOR_LO = cipher(0xa1, 64); // encryptedBalance, 64 bytes
  const AUDITOR_HI = cipher(0xb2, 64);

  // "name-" followed by U+00E9, U+20AC and U+1D11E: a 2-, a 3- and a 4-byte UTF-8 sequence, so a
  // string measured in code units (9) rather than in encoded bytes (14) is a mismatch. Escaped on
  // both sides — javac's source encoding is a build setting, and the Java constant should not
  // depend on one.
  const MULTIBYTE = 'name-é€\u{1d11e}';
  const NAME = 'Sava Token';
  const SYMBOL = 'SAVA';
  const URI = 'https://sava.software/token.json';
  const FIELD_KEY = 'website';
  const UI_AMOUNT = '12.345';

  const vectors = [
    // -------------------------------------------------------------------------
    // Base token instructions
    // -------------------------------------------------------------------------
    // freezeAuthority is an `optionTypeNode` with a one-byte prefix — absent is a single zero
    // byte, not 32 zeroes, which is the other option shape this program uses.
    ix('initializeMint.with-freeze-authority', client.getInitializeMintInstruction, {
      mint: MINT, decimals: DECIMALS, mintAuthority: AUTHORITY, freezeAuthority: some(SECOND_AUTHORITY),
    }),
    ix('initializeMint.no-freeze-authority', client.getInitializeMintInstruction, {
      mint: MINT, decimals: DECIMALS, mintAuthority: AUTHORITY, freezeAuthority: none(),
    }),

    ix('initializeAccount.basic', client.getInitializeAccountInstruction, {
      account: TOKEN, mint: MINT, owner: AUTHORITY,
    }),

    ix('initializeMultisig.m3', client.getInitializeMultisigInstruction, {
      multisig: MULTISIG, m: M, signers: [],
    }),

    ix('transfer.amount', client.getTransferInstruction, {
      source: SOURCE, destination: DESTINATION, authority: s(AUTHORITY), amount: AMOUNT,
    }),
    ix('transfer.max-u64', client.getTransferInstruction, {
      source: SOURCE, destination: DESTINATION, authority: s(AUTHORITY), amount: MAX_U64,
    }),

    ix('approve.amount', client.getApproveInstruction, {
      source: SOURCE, delegate: DELEGATE, owner: s(AUTHORITY), amount: AMOUNT,
    }),

    ix('revoke.bare', client.getRevokeInstruction, { source: SOURCE, owner: s(AUTHORITY) }),

    // AuthorityType is a u8 enum with eighteen variants; the last of them is the one an ordinal
    // written as a signed byte, or a variant list that has fallen behind, gets wrong.
    ix('setAuthority.mint-tokens', client.getSetAuthorityInstruction, {
      owned: MINT, owner: s(AUTHORITY),
      authorityType: client.AuthorityType.MintTokens, newAuthority: some(NEW_AUTHORITY),
    }),
    ix('setAuthority.close-account-none', client.getSetAuthorityInstruction, {
      owned: TOKEN, owner: s(AUTHORITY),
      authorityType: client.AuthorityType.CloseAccount, newAuthority: none(),
    }),
    ix('setAuthority.permissioned-burn', client.getSetAuthorityInstruction, {
      owned: MINT, owner: s(AUTHORITY),
      authorityType: client.AuthorityType.PermissionedBurn, newAuthority: some(NEW_AUTHORITY),
    }),

    ix('mintTo.amount', client.getMintToInstruction, {
      mint: MINT, token: TOKEN, mintAuthority: s(AUTHORITY), amount: AMOUNT,
    }),

    ix('burn.amount', client.getBurnInstruction, {
      account: TOKEN, mint: MINT, authority: s(AUTHORITY), amount: AMOUNT,
    }),

    ix('closeAccount.bare', client.getCloseAccountInstruction, {
      account: TOKEN, destination: DESTINATION, owner: s(AUTHORITY),
    }),

    ix('freezeAccount.bare', client.getFreezeAccountInstruction, {
      account: TOKEN, mint: MINT, owner: s(AUTHORITY),
    }),
    ix('thawAccount.bare', client.getThawAccountInstruction, {
      account: TOKEN, mint: MINT, owner: s(AUTHORITY),
    }),

    ix('transferChecked.amount', client.getTransferCheckedInstruction, {
      source: SOURCE, mint: MINT, destination: DESTINATION, authority: s(AUTHORITY),
      amount: AMOUNT, decimals: DECIMALS,
    }),
    ix('approveChecked.amount', client.getApproveCheckedInstruction, {
      source: SOURCE, mint: MINT, delegate: DELEGATE, owner: s(AUTHORITY),
      amount: AMOUNT, decimals: DECIMALS,
    }),
    ix('mintToChecked.amount', client.getMintToCheckedInstruction, {
      mint: MINT, token: TOKEN, mintAuthority: s(AUTHORITY), amount: AMOUNT, decimals: DECIMALS,
    }),
    ix('burnChecked.amount', client.getBurnCheckedInstruction, {
      account: TOKEN, mint: MINT, authority: s(AUTHORITY), amount: AMOUNT, decimals: DECIMALS,
    }),

    // The rent sysvar is optional upstream and defaulted when omitted, which is the account-list
    // half of what this file is for: the data is one byte and says nothing.
    ix('syncNative.bare', client.getSyncNativeInstruction, { account: TOKEN }),

    ix('getAccountDataSize.bare', client.getGetAccountDataSizeInstruction, { mint: MINT }),

    ix('initializeImmutableOwner.bare', client.getInitializeImmutableOwnerInstruction, { account: TOKEN }),

    ix('amountToUiAmount.amount', client.getAmountToUiAmountInstruction, { mint: MINT, amount: AMOUNT }),

    // uiAmount is a bare utf8 `stringTypeNode` — no length prefix at all, the string runs to the
    // end of the instruction data. An encoder that added a prefix would still round trip here.
    ix('uiAmountToAmount.decimal', client.getUiAmountToAmountInstruction, { mint: MINT, uiAmount: UI_AMOUNT }),
    ix('uiAmountToAmount.multibyte', client.getUiAmountToAmountInstruction, { mint: MINT, uiAmount: MULTIBYTE }),
    ix('uiAmountToAmount.empty', client.getUiAmountToAmountInstruction, { mint: MINT, uiAmount: '' }),

    ix('initializeAccount2.basic', client.getInitializeAccount2Instruction, {
      account: TOKEN, mint: MINT, owner: AUTHORITY,
    }),
    ix('initializeAccount3.basic', client.getInitializeAccount3Instruction, {
      account: TOKEN, mint: MINT, owner: AUTHORITY,
    }),
    ix('initializeMultisig2.m3', client.getInitializeMultisig2Instruction, {
      multisig: MULTISIG, m: M, signers: [],
    }),
    ix('initializeMint2.with-freeze-authority', client.getInitializeMint2Instruction, {
      mint: MINT, decimals: DECIMALS, mintAuthority: AUTHORITY, freezeAuthority: some(SECOND_AUTHORITY),
    }),
    ix('initializeMint2.no-freeze-authority', client.getInitializeMint2Instruction, {
      mint: MINT, decimals: DECIMALS, mintAuthority: AUTHORITY, freezeAuthority: none(),
    }),

    // -------------------------------------------------------------------------
    // Transfer fee extension
    // -------------------------------------------------------------------------
    ix('initializeMintCloseAuthority.some', client.getInitializeMintCloseAuthorityInstruction, {
      mint: MINT, closeAuthority: some(NEW_AUTHORITY),
    }),
    ix('initializeMintCloseAuthority.none', client.getInitializeMintCloseAuthorityInstruction, {
      mint: MINT, closeAuthority: none(),
    }),

    // Two prefixed options back to back: the second one's presence byte moves by 32 when the first
    // is present, which is the offset an off-by-one lands on.
    ix('initializeTransferFeeConfig.both-authorities', client.getInitializeTransferFeeConfigInstruction, {
      mint: MINT, transferFeeConfigAuthority: some(AUTHORITY), withdrawWithheldAuthority: some(SECOND_AUTHORITY),
      transferFeeBasisPoints: FEE_BASIS_POINTS, maximumFee: MAXIMUM_FEE,
    }),
    ix('initializeTransferFeeConfig.no-authorities', client.getInitializeTransferFeeConfigInstruction, {
      mint: MINT, transferFeeConfigAuthority: none(), withdrawWithheldAuthority: none(),
      transferFeeBasisPoints: FEE_BASIS_POINTS, maximumFee: MAXIMUM_FEE,
    }),
    ix('initializeTransferFeeConfig.config-authority-only', client.getInitializeTransferFeeConfigInstruction, {
      mint: MINT, transferFeeConfigAuthority: some(AUTHORITY), withdrawWithheldAuthority: none(),
      transferFeeBasisPoints: FEE_BASIS_POINTS, maximumFee: MAXIMUM_FEE,
    }),
    ix('initializeTransferFeeConfig.withdraw-authority-only', client.getInitializeTransferFeeConfigInstruction, {
      mint: MINT, transferFeeConfigAuthority: none(), withdrawWithheldAuthority: some(SECOND_AUTHORITY),
      transferFeeBasisPoints: FEE_BASIS_POINTS, maximumFee: MAXIMUM_FEE,
    }),

    ix('transferCheckedWithFee.fee', client.getTransferCheckedWithFeeInstruction, {
      source: SOURCE, mint: MINT, destination: DESTINATION, authority: s(AUTHORITY),
      amount: AMOUNT, decimals: DECIMALS, fee: FEE,
    }),

    ix('withdrawWithheldTokensFromMint.bare', client.getWithdrawWithheldTokensFromMintInstruction, {
      mint: MINT, feeReceiver: FEE_RECEIVER, withdrawWithheldAuthority: s(AUTHORITY),
    }),
    ix('withdrawWithheldTokensFromAccounts.count', client.getWithdrawWithheldTokensFromAccountsInstruction, {
      mint: MINT, feeReceiver: FEE_RECEIVER, withdrawWithheldAuthority: s(AUTHORITY),
      numTokenAccounts: NUM_TOKEN_ACCOUNTS, sources: [],
    }),
    ix('harvestWithheldTokensToMint.bare', client.getHarvestWithheldTokensToMintInstruction, {
      mint: MINT, sources: [],
    }),
    ix('setTransferFee.fee', client.getSetTransferFeeInstruction, {
      mint: MINT, transferFeeConfigAuthority: s(AUTHORITY),
      transferFeeBasisPoints: FEE_BASIS_POINTS, maximumFee: MAXIMUM_FEE,
    }),

    // -------------------------------------------------------------------------
    // Confidential transfer extension
    // -------------------------------------------------------------------------
    // authority and auditorElgamalPubkey are `zeroableOptionTypeNode`s: absent is 32 zero bytes
    // with no prefix, so an absent one costs exactly as much as a present one. The boolean between
    // them is what an off-by-one moves.
    ix('initializeConfidentialTransferMint.auto-approve', client.getInitializeConfidentialTransferMintInstruction, {
      mint: MINT, authority: some(AUTHORITY), autoApproveNewAccounts: true,
      auditorElgamalPubkey: some(ELGAMAL_PUBKEY),
    }),
    ix('initializeConfidentialTransferMint.no-auto-approve', client.getInitializeConfidentialTransferMintInstruction, {
      mint: MINT, authority: none(), autoApproveNewAccounts: false, auditorElgamalPubkey: none(),
    }),

    ix('updateConfidentialTransferMint.enable', client.getUpdateConfidentialTransferMintInstruction, {
      mint: MINT, authority: s(AUTHORITY), autoApproveNewAccounts: true,
      auditorElgamalPubkey: some(ELGAMAL_PUBKEY),
    }),
    ix('updateConfidentialTransferMint.disable', client.getUpdateConfidentialTransferMintInstruction, {
      mint: MINT, authority: s(AUTHORITY), autoApproveNewAccounts: false, auditorElgamalPubkey: none(),
    }),

    ix('configureConfidentialTransferAccount.basic', client.getConfigureConfidentialTransferAccountInstruction, {
      token: TOKEN, mint: MINT, authority: s(AUTHORITY),
      decryptableZeroBalance: AVAILABLE_BALANCE,
      maximumPendingBalanceCreditCounter: MAX_PENDING_CREDITS,
      proofInstructionOffset: PROOF_OFFSET,
    }),

    ix('approveConfidentialTransferAccount.bare', client.getApproveConfidentialTransferAccountInstruction, {
      token: TOKEN, mint: MINT, authority: s(AUTHORITY),
    }),

    // proofInstructionOffset is an i8, and the offsets a caller actually uses are negative — the
    // proof instruction sits *before* this one in the transaction.
    ix('emptyConfidentialTransferAccount.offset', client.getEmptyConfidentialTransferAccountInstruction, {
      token: TOKEN, authority: s(AUTHORITY), proofInstructionOffset: PROOF_OFFSET,
    }),
    ix('emptyConfidentialTransferAccount.negative-offset', client.getEmptyConfidentialTransferAccountInstruction, {
      token: TOKEN, authority: s(AUTHORITY), proofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),

    ix('confidentialDeposit.amount', client.getConfidentialDepositInstruction, {
      token: TOKEN, mint: MINT, authority: s(AUTHORITY), amount: AMOUNT, decimals: DECIMALS,
    }),

    // The proof-record accounts are optional and omitted when absent, so the two cases differ by
    // three accounts and not one byte of data.
    ix('confidentialWithdraw.all-records', client.getConfidentialWithdrawInstruction, {
      token: TOKEN, mint: MINT, instructionsSysvar: INSTRUCTIONS,
      equalityRecord: EQUALITY_RECORD, rangeRecord: RANGE_RECORD, authority: s(AUTHORITY),
      amount: AMOUNT, decimals: DECIMALS, newDecryptableAvailableBalance: AVAILABLE_BALANCE,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET, rangeProofInstructionOffset: PROOF_OFFSET,
    }),
    ix('confidentialWithdraw.no-records', client.getConfidentialWithdrawInstruction, {
      token: TOKEN, mint: MINT, authority: s(AUTHORITY),
      amount: AMOUNT, decimals: DECIMALS, newDecryptableAvailableBalance: AVAILABLE_BALANCE,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET, rangeProofInstructionOffset: PROOF_OFFSET,
    }),

    ix('confidentialTransfer.all-records', client.getConfidentialTransferInstruction, {
      sourceToken: SOURCE, mint: MINT, destinationToken: DESTINATION, instructionsSysvar: INSTRUCTIONS,
      equalityRecord: EQUALITY_RECORD, ciphertextValidityRecord: VALIDITY_RECORD,
      rangeRecord: RANGE_RECORD, authority: s(AUTHORITY),
      newSourceDecryptableAvailableBalance: AVAILABLE_BALANCE,
      transferAmountAuditorCiphertextLo: AUDITOR_LO, transferAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('confidentialTransfer.no-records', client.getConfidentialTransferInstruction, {
      sourceToken: SOURCE, mint: MINT, destinationToken: DESTINATION, authority: s(AUTHORITY),
      newSourceDecryptableAvailableBalance: AVAILABLE_BALANCE,
      transferAmountAuditorCiphertextLo: AUDITOR_LO, transferAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),

    ix('applyConfidentialPendingBalance.counter', client.getApplyConfidentialPendingBalanceInstruction, {
      token: TOKEN, authority: s(AUTHORITY),
      expectedPendingBalanceCreditCounter: CREDIT_COUNTER,
      newDecryptableAvailableBalance: AVAILABLE_BALANCE,
    }),

    ix('enableConfidentialCredits.bare', client.getEnableConfidentialCreditsInstruction, {
      token: TOKEN, authority: s(AUTHORITY),
    }),
    ix('disableConfidentialCredits.bare', client.getDisableConfidentialCreditsInstruction, {
      token: TOKEN, authority: s(AUTHORITY),
    }),
    ix('enableNonConfidentialCredits.bare', client.getEnableNonConfidentialCreditsInstruction, {
      token: TOKEN, authority: s(AUTHORITY),
    }),
    ix('disableNonConfidentialCredits.bare', client.getDisableNonConfidentialCreditsInstruction, {
      token: TOKEN, authority: s(AUTHORITY),
    }),

    ix('confidentialTransferWithFee.all-records', client.getConfidentialTransferWithFeeInstruction, {
      sourceToken: SOURCE, mint: MINT, destinationToken: DESTINATION, instructionsSysvar: INSTRUCTIONS,
      equalityRecord: EQUALITY_RECORD, transferAmountCiphertextValidityRecord: VALIDITY_RECORD,
      feeSigmaRecord: FEE_SIGMA_RECORD, feeCiphertextValidityRecord: FEE_VALIDITY_RECORD,
      rangeRecord: RANGE_RECORD, authority: s(AUTHORITY),
      newSourceDecryptableAvailableBalance: AVAILABLE_BALANCE,
      transferAmountAuditorCiphertextLo: AUDITOR_LO, transferAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      transferAmountCiphertextValidityProofInstructionOffset: PROOF_OFFSET,
      feeSigmaProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      feeCiphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('confidentialTransferWithFee.no-records', client.getConfidentialTransferWithFeeInstruction, {
      sourceToken: SOURCE, mint: MINT, destinationToken: DESTINATION, authority: s(AUTHORITY),
      newSourceDecryptableAvailableBalance: AVAILABLE_BALANCE,
      transferAmountAuditorCiphertextLo: AUDITOR_LO, transferAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      transferAmountCiphertextValidityProofInstructionOffset: PROOF_OFFSET,
      feeSigmaProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      feeCiphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),

    ix('configureConfidentialTransferAccountWithRegistry.with-payer',
        client.getConfigureConfidentialTransferAccountWithRegistryInstruction, {
      token: TOKEN, mint: MINT, elgamalRegistry: ELGAMAL_REGISTRY, payer: s(PAYER), systemProgram: SYSTEM,
    }),
    ix('configureConfidentialTransferAccountWithRegistry.no-payer',
        client.getConfigureConfidentialTransferAccountWithRegistryInstruction, {
      token: TOKEN, mint: MINT, elgamalRegistry: ELGAMAL_REGISTRY,
    }),

    // -------------------------------------------------------------------------
    // Default account state, memo transfer, CPI guard, reallocate
    // -------------------------------------------------------------------------
    ix('initializeDefaultAccountState.frozen', client.getInitializeDefaultAccountStateInstruction, {
      mint: MINT, state: client.AccountState.Frozen,
    }),
    ix('initializeDefaultAccountState.uninitialized', client.getInitializeDefaultAccountStateInstruction, {
      mint: MINT, state: client.AccountState.Uninitialized,
    }),
    ix('updateDefaultAccountState.initialized', client.getUpdateDefaultAccountStateInstruction, {
      mint: MINT, freezeAuthority: s(AUTHORITY), state: client.AccountState.Initialized,
    }),

    // newExtensionTypes is a remainder-counted array of u16 enums: no length prefix, so an empty
    // one is a one-byte instruction and a wrong element width shifts every element after the first.
    ix('reallocate.two-extensions', client.getReallocateInstruction, {
      token: TOKEN, payer: s(PAYER), owner: s(AUTHORITY),
      newExtensionTypes: [client.ExtensionType.MemoTransfer, client.ExtensionType.CpiGuard],
    }),
    ix('reallocate.empty', client.getReallocateInstruction, {
      token: TOKEN, payer: s(PAYER), owner: s(AUTHORITY), newExtensionTypes: [],
    }),
    // The highest ordinal declared, which is the one a u8 element would still encode correctly and
    // a stale variant list would not.
    ix('reallocate.high-ordinal', client.getReallocateInstruction, {
      token: TOKEN, payer: s(PAYER), owner: s(AUTHORITY),
      newExtensionTypes: [client.ExtensionType.PermissionedBurn],
    }),

    ix('enableMemoTransfers.bare', client.getEnableMemoTransfersInstruction, {
      token: TOKEN, owner: s(AUTHORITY),
    }),
    ix('disableMemoTransfers.bare', client.getDisableMemoTransfersInstruction, {
      token: TOKEN, owner: s(AUTHORITY),
    }),

    ix('createNativeMint.bare', client.getCreateNativeMintInstruction, {
      payer: s(PAYER), nativeMint: NATIVE_MINT,
    }),

    ix('initializeNonTransferableMint.bare', client.getInitializeNonTransferableMintInstruction, { mint: MINT }),

    // rate is an i16 and negative rates are the point of the extension.
    ix('initializeInterestBearingMint.positive-rate', client.getInitializeInterestBearingMintInstruction, {
      mint: MINT, rateAuthority: some(AUTHORITY), rate: RATE,
    }),
    ix('initializeInterestBearingMint.negative-rate', client.getInitializeInterestBearingMintInstruction, {
      mint: MINT, rateAuthority: some(AUTHORITY), rate: NEGATIVE_RATE,
    }),
    ix('initializeInterestBearingMint.no-authority', client.getInitializeInterestBearingMintInstruction, {
      mint: MINT, rateAuthority: none(), rate: RATE,
    }),
    ix('updateRateInterestBearingMint.negative-rate', client.getUpdateRateInterestBearingMintInstruction, {
      mint: MINT, rateAuthority: s(AUTHORITY), rate: NEGATIVE_RATE,
    }),

    ix('enableCpiGuard.bare', client.getEnableCpiGuardInstruction, { token: TOKEN, owner: s(AUTHORITY) }),
    ix('disableCpiGuard.bare', client.getDisableCpiGuardInstruction, { token: TOKEN, owner: s(AUTHORITY) }),

    ix('initializePermanentDelegate.bare', client.getInitializePermanentDelegateInstruction, {
      mint: MINT, delegate: DELEGATE,
    }),

    // -------------------------------------------------------------------------
    // Transfer hook, confidential transfer fee, excess lamports
    // -------------------------------------------------------------------------
    ix('initializeTransferHook.both', client.getInitializeTransferHookInstruction, {
      mint: MINT, authority: some(AUTHORITY), programId: some(HOOK_PROGRAM),
    }),
    ix('initializeTransferHook.none', client.getInitializeTransferHookInstruction, {
      mint: MINT, authority: none(), programId: none(),
    }),
    ix('initializeTransferHook.authority-only', client.getInitializeTransferHookInstruction, {
      mint: MINT, authority: some(AUTHORITY), programId: none(),
    }),
    ix('updateTransferHook.some', client.getUpdateTransferHookInstruction, {
      mint: MINT, authority: s(AUTHORITY), programId: some(HOOK_PROGRAM),
    }),
    ix('updateTransferHook.none', client.getUpdateTransferHookInstruction, {
      mint: MINT, authority: s(AUTHORITY), programId: none(),
    }),

    ix('initializeConfidentialTransferFee.some-authority', client.getInitializeConfidentialTransferFeeInstruction, {
      mint: MINT, authority: some(AUTHORITY), withdrawWithheldAuthorityElGamalPubkey: ELGAMAL_PUBKEY,
    }),
    ix('initializeConfidentialTransferFee.no-authority', client.getInitializeConfidentialTransferFeeInstruction, {
      mint: MINT, authority: none(), withdrawWithheldAuthorityElGamalPubkey: ELGAMAL_PUBKEY,
    }),

    ix('withdrawWithheldTokensFromMintForConfidentialTransferFee.offset',
        client.getWithdrawWithheldTokensFromMintForConfidentialTransferFeeInstruction, {
      mint: MINT, destination: DESTINATION, instructionsSysvarOrContextState: INSTRUCTIONS,
      authority: s(AUTHORITY), proofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      newDecryptableAvailableBalance: AVAILABLE_BALANCE,
    }),
    ix('withdrawWithheldTokensFromAccountsForConfidentialTransferFee.offset',
        client.getWithdrawWithheldTokensFromAccountsForConfidentialTransferFeeInstruction, {
      mint: MINT, destination: DESTINATION, instructionsSysvarOrContextState: INSTRUCTIONS,
      authority: s(AUTHORITY), numTokenAccounts: NUM_TOKEN_ACCOUNTS,
      proofInstructionOffset: NEGATIVE_PROOF_OFFSET, newDecryptableAvailableBalance: AVAILABLE_BALANCE,
    }),
    ix('harvestWithheldTokensToMintForConfidentialTransferFee.bare',
        client.getHarvestWithheldTokensToMintForConfidentialTransferFeeInstruction, { mint: MINT, sources: [] }),
    ix('enableHarvestToMint.bare', client.getEnableHarvestToMintInstruction, {
      mint: MINT, authority: s(AUTHORITY),
    }),
    ix('disableHarvestToMint.bare', client.getDisableHarvestToMintInstruction, {
      mint: MINT, authority: s(AUTHORITY),
    }),
    ix('withdrawExcessLamports.bare', client.getWithdrawExcessLamportsInstruction, {
      source: SOURCE, destination: DESTINATION, authority: s(AUTHORITY),
    }),

    // -------------------------------------------------------------------------
    // Pointer extensions — all six are a pair of zeroable-option pubkeys
    // -------------------------------------------------------------------------
    ix('initializeMetadataPointer.both', client.getInitializeMetadataPointerInstruction, {
      mint: MINT, authority: some(AUTHORITY), metadataAddress: some(POINTER_ADDRESS),
    }),
    ix('initializeMetadataPointer.none', client.getInitializeMetadataPointerInstruction, {
      mint: MINT, authority: none(), metadataAddress: none(),
    }),
    ix('initializeMetadataPointer.address-only', client.getInitializeMetadataPointerInstruction, {
      mint: MINT, authority: none(), metadataAddress: some(POINTER_ADDRESS),
    }),
    ix('updateMetadataPointer.some', client.getUpdateMetadataPointerInstruction, {
      mint: MINT, metadataPointerAuthority: s(AUTHORITY), metadataAddress: some(POINTER_ADDRESS),
    }),
    ix('updateMetadataPointer.none', client.getUpdateMetadataPointerInstruction, {
      mint: MINT, metadataPointerAuthority: s(AUTHORITY), metadataAddress: none(),
    }),

    ix('initializeGroupPointer.both', client.getInitializeGroupPointerInstruction, {
      mint: MINT, authority: some(AUTHORITY), groupAddress: some(POINTER_ADDRESS),
    }),
    ix('initializeGroupPointer.none', client.getInitializeGroupPointerInstruction, {
      mint: MINT, authority: none(), groupAddress: none(),
    }),
    ix('updateGroupPointer.some', client.getUpdateGroupPointerInstruction, {
      mint: MINT, groupPointerAuthority: s(AUTHORITY), groupAddress: some(POINTER_ADDRESS),
    }),
    ix('updateGroupPointer.none', client.getUpdateGroupPointerInstruction, {
      mint: MINT, groupPointerAuthority: s(AUTHORITY), groupAddress: none(),
    }),

    ix('initializeGroupMemberPointer.both', client.getInitializeGroupMemberPointerInstruction, {
      mint: MINT, authority: some(AUTHORITY), memberAddress: some(POINTER_ADDRESS),
    }),
    ix('initializeGroupMemberPointer.none', client.getInitializeGroupMemberPointerInstruction, {
      mint: MINT, authority: none(), memberAddress: none(),
    }),
    ix('updateGroupMemberPointer.some', client.getUpdateGroupMemberPointerInstruction, {
      mint: MINT, groupMemberPointerAuthority: s(AUTHORITY), memberAddress: some(POINTER_ADDRESS),
    }),
    ix('updateGroupMemberPointer.none', client.getUpdateGroupMemberPointerInstruction, {
      mint: MINT, groupMemberPointerAuthority: s(AUTHORITY), memberAddress: none(),
    }),

    // -------------------------------------------------------------------------
    // Confidential mint/burn extension
    // -------------------------------------------------------------------------
    ix('initializeConfidentialMintBurn.basic', client.getInitializeConfidentialMintBurnInstruction, {
      mint: MINT, supplyElgamalPubkey: ELGAMAL_PUBKEY, decryptableSupply: SUPPLY_BALANCE,
    }),
    ix('rotateSupplyElgamalPubkey.offset', client.getRotateSupplyElgamalPubkeyInstruction, {
      mint: MINT, authority: s(AUTHORITY), newSupplyElgamalPubkey: ELGAMAL_PUBKEY,
      proofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('updateConfidentialMintBurnDecryptableSupply.basic',
        client.getUpdateConfidentialMintBurnDecryptableSupplyInstruction, {
      mint: MINT, authority: s(AUTHORITY), newDecryptableSupply: SUPPLY_BALANCE,
    }),

    ix('confidentialMint.all-records', client.getConfidentialMintInstruction, {
      token: TOKEN, mint: MINT, instructionsSysvar: INSTRUCTIONS, equalityRecord: EQUALITY_RECORD,
      ciphertextValidityRecord: VALIDITY_RECORD, rangeRecord: RANGE_RECORD, authority: s(AUTHORITY),
      newDecryptableSupply: SUPPLY_BALANCE,
      mintAmountAuditorCiphertextLo: AUDITOR_LO, mintAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('confidentialMint.no-records', client.getConfidentialMintInstruction, {
      token: TOKEN, mint: MINT, authority: s(AUTHORITY),
      newDecryptableSupply: SUPPLY_BALANCE,
      mintAmountAuditorCiphertextLo: AUDITOR_LO, mintAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('confidentialBurn.all-records', client.getConfidentialBurnInstruction, {
      token: TOKEN, mint: MINT, instructionsSysvar: INSTRUCTIONS, equalityRecord: EQUALITY_RECORD,
      ciphertextValidityRecord: VALIDITY_RECORD, rangeRecord: RANGE_RECORD, authority: s(AUTHORITY),
      newDecryptableAvailableBalance: AVAILABLE_BALANCE,
      burnAmountAuditorCiphertextLo: AUDITOR_LO, burnAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('confidentialBurn.no-records', client.getConfidentialBurnInstruction, {
      token: TOKEN, mint: MINT, authority: s(AUTHORITY),
      newDecryptableAvailableBalance: AVAILABLE_BALANCE,
      burnAmountAuditorCiphertextLo: AUDITOR_LO, burnAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('applyConfidentialPendingBurn.bare', client.getApplyConfidentialPendingBurnInstruction, {
      mint: MINT, authority: s(AUTHORITY),
    }),

    // -------------------------------------------------------------------------
    // Scaled UI amount and pausable extensions
    // -------------------------------------------------------------------------
    // multiplier is the program's only f64. A value that is exact in binary64 and in nothing
    // narrower is what separates a correct little-endian double from a float widened at the end.
    ix('initializeScaledUiAmountMint.multiplier', client.getInitializeScaledUiAmountMintInstruction, {
      mint: MINT, authority: some(AUTHORITY), multiplier: MULTIPLIER,
    }),
    ix('initializeScaledUiAmountMint.negative-multiplier', client.getInitializeScaledUiAmountMintInstruction, {
      mint: MINT, authority: some(AUTHORITY), multiplier: NEGATIVE_MULTIPLIER,
    }),
    ix('initializeScaledUiAmountMint.no-authority', client.getInitializeScaledUiAmountMintInstruction, {
      mint: MINT, authority: none(), multiplier: MULTIPLIER,
    }),
    ix('updateMultiplierScaledUiMint.multiplier', client.getUpdateMultiplierScaledUiMintInstruction, {
      mint: MINT, authority: s(AUTHORITY), multiplier: MULTIPLIER, effectiveTimestamp: EFFECTIVE_TIMESTAMP,
    }),
    // effectiveTimestamp is an i64 and a plain long here, so a negative one pins that neither side
    // treats it as unsigned.
    ix('updateMultiplierScaledUiMint.negative-timestamp', client.getUpdateMultiplierScaledUiMintInstruction, {
      mint: MINT, authority: s(AUTHORITY), multiplier: NEGATIVE_MULTIPLIER, effectiveTimestamp: -1n,
    }),

    ix('initializePausableConfig.some', client.getInitializePausableConfigInstruction, {
      mint: MINT, authority: some(AUTHORITY),
    }),
    ix('initializePausableConfig.none', client.getInitializePausableConfigInstruction, {
      mint: MINT, authority: none(),
    }),
    ix('pause.bare', client.getPauseInstruction, { mint: MINT, authority: s(AUTHORITY) }),
    ix('resume.bare', client.getResumeInstruction, { mint: MINT, authority: s(AUTHORITY) }),

    // -------------------------------------------------------------------------
    // Token metadata interface — eight-byte discriminators, u32-prefixed strings
    // -------------------------------------------------------------------------
    ix('initializeTokenMetadata.basic', client.getInitializeTokenMetadataInstruction, {
      metadata: METADATA, updateAuthority: NEW_AUTHORITY, mint: MINT, mintAuthority: s(AUTHORITY),
      name: NAME, symbol: SYMBOL, uri: URI,
    }),
    // Three prefixed strings back to back: a length counted in code units rather than UTF-8 bytes
    // moves the two after it.
    ix('initializeTokenMetadata.multibyte', client.getInitializeTokenMetadataInstruction, {
      metadata: METADATA, updateAuthority: NEW_AUTHORITY, mint: MINT, mintAuthority: s(AUTHORITY),
      name: MULTIBYTE, symbol: SYMBOL, uri: URI,
    }),
    // Empty strings are three zero prefixes, not three skipped fields.
    ix('initializeTokenMetadata.empty', client.getInitializeTokenMetadataInstruction, {
      metadata: METADATA, updateAuthority: NEW_AUTHORITY, mint: MINT, mintAuthority: s(AUTHORITY),
      name: '', symbol: '', uri: '',
    }),

    // TokenMetadataField is a data enum: three unit variants and one carrying a prefixed string,
    // so the value's offset depends on which variant precedes it.
    ix('updateTokenMetadataField.name', client.getUpdateTokenMetadataFieldInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY),
      field: client.tokenMetadataField('Name'), value: NAME,
    }),
    ix('updateTokenMetadataField.symbol', client.getUpdateTokenMetadataFieldInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY),
      field: client.tokenMetadataField('Symbol'), value: SYMBOL,
    }),
    ix('updateTokenMetadataField.uri', client.getUpdateTokenMetadataFieldInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY),
      field: client.tokenMetadataField('Uri'), value: URI,
    }),
    ix('updateTokenMetadataField.key', client.getUpdateTokenMetadataFieldInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY),
      field: client.tokenMetadataField('Key', [FIELD_KEY]), value: URI,
    }),
    ix('updateTokenMetadataField.multibyte-key', client.getUpdateTokenMetadataFieldInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY),
      field: client.tokenMetadataField('Key', [MULTIBYTE]), value: MULTIBYTE,
    }),

    ix('removeTokenMetadataKey.idempotent', client.getRemoveTokenMetadataKeyInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY), idempotent: true, key: FIELD_KEY,
    }),
    ix('removeTokenMetadataKey.not-idempotent', client.getRemoveTokenMetadataKeyInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY), idempotent: false, key: FIELD_KEY,
    }),
    ix('removeTokenMetadataKey.multibyte-key', client.getRemoveTokenMetadataKeyInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY), idempotent: true, key: MULTIBYTE,
    }),

    ix('updateTokenMetadataUpdateAuthority.some', client.getUpdateTokenMetadataUpdateAuthorityInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY), newUpdateAuthority: some(NEW_AUTHORITY),
    }),
    ix('updateTokenMetadataUpdateAuthority.none', client.getUpdateTokenMetadataUpdateAuthorityInstruction, {
      metadata: METADATA, updateAuthority: s(AUTHORITY), newUpdateAuthority: none(),
    }),

    // Two prefixed options after an eight-byte discriminator; all four presence combinations,
    // because the second one's presence byte moves by eight when the first is present.
    ix('emitTokenMetadata.range', client.getEmitTokenMetadataInstruction, {
      metadata: METADATA, start: some(START), end: some(END),
    }),
    ix('emitTokenMetadata.none', client.getEmitTokenMetadataInstruction, {
      metadata: METADATA, start: none(), end: none(),
    }),
    ix('emitTokenMetadata.start-only', client.getEmitTokenMetadataInstruction, {
      metadata: METADATA, start: some(START), end: none(),
    }),
    ix('emitTokenMetadata.end-only', client.getEmitTokenMetadataInstruction, {
      metadata: METADATA, start: none(), end: some(END),
    }),

    // -------------------------------------------------------------------------
    // Token group interface
    // -------------------------------------------------------------------------
    ix('initializeTokenGroup.some-authority', client.getInitializeTokenGroupInstruction, {
      group: GROUP, mint: MINT, mintAuthority: s(AUTHORITY),
      updateAuthority: some(NEW_AUTHORITY), maxSize: MAX_SIZE,
    }),
    ix('initializeTokenGroup.no-authority', client.getInitializeTokenGroupInstruction, {
      group: GROUP, mint: MINT, mintAuthority: s(AUTHORITY), updateAuthority: none(), maxSize: MAX_SIZE,
    }),
    ix('updateTokenGroupMaxSize.max-size', client.getUpdateTokenGroupMaxSizeInstruction, {
      group: GROUP, updateAuthority: s(AUTHORITY), maxSize: MAX_SIZE,
    }),
    ix('updateTokenGroupUpdateAuthority.some', client.getUpdateTokenGroupUpdateAuthorityInstruction, {
      group: GROUP, updateAuthority: s(AUTHORITY), newUpdateAuthority: some(NEW_AUTHORITY),
    }),
    ix('updateTokenGroupUpdateAuthority.none', client.getUpdateTokenGroupUpdateAuthorityInstruction, {
      group: GROUP, updateAuthority: s(AUTHORITY), newUpdateAuthority: none(),
    }),
    ix('initializeTokenGroupMember.bare', client.getInitializeTokenGroupMemberInstruction, {
      member: MEMBER, memberMint: MINT, memberMintAuthority: s(AUTHORITY),
      group: GROUP, groupUpdateAuthority: s(NEW_AUTHORITY),
    }),

    // -------------------------------------------------------------------------
    // Unwrap lamports and the permissioned burn extension
    // -------------------------------------------------------------------------
    // amount is a prefixed option over u64: absent means "all of it", which is not the same
    // instruction as a present zero.
    ix('unwrapLamports.amount', client.getUnwrapLamportsInstruction, {
      source: SOURCE, destination: DESTINATION, authority: s(AUTHORITY), amount: some(AMOUNT),
    }),
    ix('unwrapLamports.none', client.getUnwrapLamportsInstruction, {
      source: SOURCE, destination: DESTINATION, authority: s(AUTHORITY), amount: none(),
    }),

    ix('initializePermissionedBurn.bare', client.getInitializePermissionedBurnInstruction, {
      mint: MINT, authority: AUTHORITY,
    }),
    ix('permissionedBurn.amount', client.getPermissionedBurnInstruction, {
      account: TOKEN, mint: MINT, permissionedBurnAuthority: s(SECOND_AUTHORITY),
      authority: s(AUTHORITY), amount: AMOUNT,
    }),
    ix('permissionedBurnChecked.amount', client.getPermissionedBurnCheckedInstruction, {
      account: TOKEN, mint: MINT, permissionedBurnAuthority: s(SECOND_AUTHORITY),
      authority: s(AUTHORITY), amount: AMOUNT, decimals: DECIMALS,
    }),
    ix('permissionedConfidentialBurn.all-records', client.getPermissionedConfidentialBurnInstruction, {
      token: TOKEN, mint: MINT, instructionsSysvar: INSTRUCTIONS, equalityRecord: EQUALITY_RECORD,
      ciphertextValidityRecord: VALIDITY_RECORD, rangeRecord: RANGE_RECORD,
      permissionedBurnAuthority: s(SECOND_AUTHORITY), authority: s(AUTHORITY),
      newDecryptableAvailableBalance: AVAILABLE_BALANCE,
      burnAmountAuditorCiphertextLo: AUDITOR_LO, burnAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
    ix('permissionedConfidentialBurn.no-records', client.getPermissionedConfidentialBurnInstruction, {
      token: TOKEN, mint: MINT, permissionedBurnAuthority: s(SECOND_AUTHORITY), authority: s(AUTHORITY),
      newDecryptableAvailableBalance: AVAILABLE_BALANCE,
      burnAmountAuditorCiphertextLo: AUDITOR_LO, burnAmountAuditorCiphertextHi: AUDITOR_HI,
      equalityProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
      ciphertextValidityProofInstructionOffset: PROOF_OFFSET,
      rangeProofInstructionOffset: NEGATIVE_PROOF_OFFSET,
    }),
  ];

  // The three fields whose value is a numeric enum ordinal, so the comment can name the variant.
  const enums = {
    state: client.AccountState,
    authorityType: client.AuthorityType,
    newExtensionTypes: client.ExtensionType,
  };

  const covered = new Set(vectors.map(({ name }) => name.substring(0, name.indexOf('.'))));
  const lines = [
    '# Differential encoding vectors for the Token 2022 program. Generated — do not hand-edit.',
    '#',
    "# Produced by tools/token2022-vectors.mjs from solana-program/token-2022's own generated",
    '# JavaScript client, which @codama/renderers-js renders from the same IDL this repository',
    '# generates from. It is an independent encoder, not ground truth: both sides descend from one',
    '# document, and nothing here says that document matches the deployed program.',
    '#',
    `# upstream: solana-program/token-2022 @ ${head}`,
    `# client:   @solana-program/token-2022 ${version}, generated at ${generated}`,
    `# lockfile: sha256 ${lock}`,
    `# covers:   ${covered.size} instructions, ${vectors.length} vectors`,
    '#',
    '# One vector per line, "<instruction>.<case> <base64 instruction data> <accounts>", preceded',
    '# by the input it was built from and the accounts spelled out by name. <accounts> is a',
    '# comma-separated list of <base58 address>:<flags> in the order the builder emitted them,',
    '# where flags is "w" for writable, "s" for signer, and "-" for a readonly non-signer.',
    '# k(0xNN) is the 32-byte key whose byte i is (0xNN + i * 7) & 0xff; cipher(0xNN, L) is the',
    '# L-byte value whose byte i is 1 + ((0xNN + i * 7) % 255).',
    '',
  ];
  for (const { name, args, data, accounts } of vectors) {
    lines.push(`# input:    ${describe(args, labels, enums)}`,
        `# accounts: ${accountComment(accounts, labels)}`,
        `${name} ${data} ${accounts.map(a => `${a.address}:${a.flags}`).join(',')}`, '');
  }
  return lines.join('\n');

  function ix(name, builder, args) {
    if (typeof builder !== 'function') {
      throw new Error(`${name}: the client exposes no builder for it`);
    }
    const built = builder(args);
    return {
      name,
      args,
      data: Buffer.from(built.data).toString('base64'),
      accounts: built.accounts.map(({ address, role }) => ({
        address,
        // AccountRole is a two-bit set: bit 0 writable, bit 1 signer.
        flags: ((role & 1) === 1 ? 'w' : '') + ((role & 2) === 2 ? 's' : '') || '-',
      })),
    };
  }
}

/// The accounts as the builder emitted them, by the name the vector passed them under, so a
/// reviewer can read an account list without decoding base58.
function accountComment(accounts, labels) {
  return accounts.map(({ address, flags }) => `${labels.get(address) ?? address}:${flags}`).join(', ');
}

/// The input as it was passed, so a reviewer can check the Java literals against it without
/// running anything. Derived from the same object that was encoded, so it cannot drift from the
/// bytes on the next line.
function describe(args, labels, enums) {
  const value = (v, enumeration) => {
    if (typeof v === 'string') {
      return labels.get(v) ?? JSON.stringify(v);
    }
    if (typeof v === 'bigint') {
      return v.toString();
    }
    if (typeof v === 'number' && enumeration) {
      return enumeration[v] ?? String(v);
    }
    if (v instanceof Uint8Array) {
      return labels.get(v) ?? `bytes[${v.length}]`;
    }
    if (Array.isArray(v)) {
      return `[${v.map(x => value(x, enumeration)).join(', ')}]`;
    }
    if (v && typeof v === 'object') {
      if ('__option' in v) {
        return v.__option === 'None' ? 'none' : `some(${value(v.value, enumeration)})`;
      }
      if ('__kind' in v) {
        return v.fields === undefined ? v.__kind : `${v.__kind}(${v.fields.map(x => value(x)).join(', ')})`;
      }
      if ('address' in v) { // a noop signer, passed for an account the IDL declares as a signer
        return `signer(${value(v.address)})`;
      }
      return `{${Object.entries(v).map(([f, x]) => `${f}: ${value(x)}`).join(', ')}}`;
    }
    return String(v);
  };
  const fields = Object.entries(args).map(([f, v]) => `${f}: ${value(v, enums[f])}`);
  return fields.length === 0 ? '(no input)' : fields.join(', ');
}
