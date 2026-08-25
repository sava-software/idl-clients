// Emits differential encoding vectors for the Stake program, from solana-program/stake's own
// generated JavaScript client.
//
// The Stake tests here are otherwise round-trip: an instruction is built with the generated
// builder and read back with the generated `IxData`. Builder and reader are emitted from the same
// IDL by the same generator, so they move together — a systematic change to the wire format
// (a discriminator width, a size-prefix width, a field order) leaves every round-trip passing.
// Only a *second, independent* encoder can see it.
//
// That encoder is upstream's: `clients/js/src/generated/instructions/*.ts` is rendered by
// @codama/renderers-js from the same `idl.json` this repository generates from, so it is the
// closest thing to a peer implementation that exists — closer than it was, since as of 2026-08-25
// the two clients share one input file rather than a common pipeline. It is not ground truth —
// for that see the mainnet fixture in StakeOnChainInstructionTests, which is the only thing here
// that says upstream's encoder matches what is actually deployed.
//
//   cd <solana-program/stake checkout>/clients/js && pnpm install --frozen-lockfile
//   node tools/stake-vectors.mjs <that checkout> [output path]
//
// The client is TypeScript and `getStakeAuthorizeEncoder` needs the `enum StakeAuthorize` at
// runtime, which Node's type stripping cannot erase, so this transpiles the client with the
// checkout's own `tsc` into a temporary directory (removed on exit) whose `node_modules` is a
// symlink back into `clients/js`. Nothing is written inside the checkout.
//
// The output is committed as a test resource, and the diff is the review. Do not regenerate on a
// schedule: the deployed program is immutable — its programData account
// 6WU8Nxarf9fudRK5atWwjLY4vFaw5UrrWhL88qz7iCMJ carries `authority: null`, checked against mainnet
// on 2026-08-25 at slot 441627724 — so the wire format cannot change, and upstream's repository
// moving is not a reason to refresh. Re-running on 2026-08-25 across upstream #498/#500/#501 and
// a client bump from 0.8.0 to 0.9.0 reproduced all 38 vectors byte for byte. Run it to adjudicate
// a comparison that has failed, or to cover an instruction the IDL has grown — hand-authoring
// those bytes would void the point of them. See tools/README.md.
//
// A vector's name is `<instruction>.<case>`; StakeReferenceEncodingTests requires
// every instruction StakeProgram declares to have at least one.
import { spawnSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { existsSync, mkdtempSync, readFileSync, rmSync, symlinkSync, writeFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { pathToFileURL } from 'node:url';

const repo = process.argv[2];
if (!repo) {
  console.error('usage: node tools/stake-vectors.mjs <solana-program/stake checkout> [output]');
  process.exit(2);
}
const out = process.argv[3] ?? 'idl-clients-spl/src/test/resources/stake/reference-vectors.txt';

const clientsJs = resolve(repo, 'clients/js');
const modules = resolve(clientsJs, 'node_modules');
const tsc = resolve(modules, '.bin/tsc');
if (!existsSync(tsc)) {
  console.error(`${tsc} is missing — run \`pnpm install --frozen-lockfile\` in ${clientsJs}`);
  process.exit(2);
}

const work = mkdtempSync(join(tmpdir(), 'stake-vectors-'));
let client;
try {
  symlinkSync(modules, join(work, 'node_modules'), 'dir');
  // --ignoreConfig: their tsconfig declares `include`, which tsc refuses to combine with a named
  // entry file. Everything it sets that matters to *emit* is restated here; the type-only options
  // it drops (strict, noUnusedLocals) cannot change the bytes. CommonJS output keeps TypeScript's
  // extensionless relative imports resolvable without rewriting them.
  const tsvc = spawnSync(tsc, [
    '--ignoreConfig', resolve(clientsJs, 'src/index.ts'),
    '--outDir', work,
    '--rootDir', resolve(clientsJs, 'src'),
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
/// This used to record only the last commit touching `clients/js/src/generated`, on the reasoning
/// that unrelated upstream commits should not churn the header. That identified too little: the
/// encoders are codec combinators from `@solana/kit`, so a dependency bump changes what they emit
/// without touching that directory at all — upstream went from Kit 6 to Kit 7 exactly that way.
/// Two runs could then disagree byte for byte under identical provenance, which is the one thing
/// provenance exists to prevent.
///
/// So: the checkout revision, the generated client's own revision, and a digest of the lockfile
/// that pins every encoder actually loaded. A dirty tree under either input is refused rather than
/// recorded, because there is no revision that describes it.
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
  // A key whose bytes all differ and ascend: byte i is (seed + i * 7) & 0xff. A pubkey of
  // repeated bytes would survive being written backwards; this one does not. Mirrored by
  // StakeReferenceEncodingTests.key(int).
  const labels = new Map();
  const k = seed => {
    const bytes = new Uint8Array(32);
    for (let i = 0; i < 32; ++i) {
      bytes[i] = (seed + i * 7) & 0xff;
    }
    const address = kit.getAddressDecoder().decode(bytes);
    labels.set(address, `k(0x${seed.toString(16).padStart(2, '0')})`);
    return address;
  };
  const { some, none } = kit;

  const STAKER = 0; // StakeAuthorize::Staker
  const WITHDRAWER = 1; // StakeAuthorize::Withdrawer

  // Exercises a 2-, a 3- and a 4-byte UTF-8 sequence, so a seed length measured in chars (8)
  // rather than in encoded bytes (14) is a mismatch. Escaped on both sides — javac's source
  // encoding is a build setting, and the Java constant should not depend on one.
  const MULTIBYTE_SEED = 'seed-\u00e9\u20ac\u{1d11e}';

  const vectors = [
    // Initialize is the one instruction with a mainnet fixture (StakeOnChainInstructionTests);
    // these add the argument values that capture happens not to carry.
    ix('initialize.basic', client.getInitializeInstructionDataEncoder(), {
      arg0: { staker: k(0x11), withdrawer: k(0x22) },
      arg1: { unixTimestamp: 1_700_000_000n, epoch: 512n, custodian: k(0x33) },
    }),
    // Lockup.unixTimestamp is i64 upstream and a plain long here, so a negative one pins that
    // neither side treats it as unsigned.
    ix('initialize.negative-timestamp', client.getInitializeInstructionDataEncoder(), {
      arg0: { staker: k(0x11), withdrawer: k(0x22) },
      arg1: { unixTimestamp: -1n, epoch: 0n, custodian: k(0x33) },
    }),

    // StakeAuthorize is a u32 enum, not the u8 an Anchor-shaped IDL would give it.
    ix('authorize.staker', client.getAuthorizeInstructionDataEncoder(), { arg0: k(0x44), arg1: STAKER }),
    ix('authorize.withdrawer', client.getAuthorizeInstructionDataEncoder(), { arg0: k(0x44), arg1: WITHDRAWER }),

    ix('delegateStake.bare', client.getDelegateStakeInstructionDataEncoder(), {}),

    ix('split.lamports', client.getSplitInstructionDataEncoder(), { args: 5_000n }),
    // u64 max: Java has no unsigned long, so this is -1L there. If either side sign-extended or
    // narrowed, these eight bytes would not be all-ones.
    ix('split.max-u64', client.getSplitInstructionDataEncoder(), { args: 18_446_744_073_709_551_615n }),

    ix('withdraw.lamports', client.getWithdrawInstructionDataEncoder(), { args: 4_230_000_000_000n }),

    ix('deactivate.bare', client.getDeactivateInstructionDataEncoder(), {}),

    // SetLockup's three fields were one wrapped `LockupArgs` before upstream's pipeline flattened
    // them; each is an independent Option with a one-byte prefix, and absent means "leave it".
    // All eight presence combinations are here — the presence byte of a later field moves by the
    // size of an earlier one, so the mixed cases are where an off-by-one shows.
    ix('setLockup.all', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: some(1_700_000_000n), epoch: some(512n), custodian: some(k(0x33)),
    }),
    ix('setLockup.none', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: none(), epoch: none(), custodian: none(),
    }),
    ix('setLockup.timestamp-only', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: some(1_700_000_000n), epoch: none(), custodian: none(),
    }),
    ix('setLockup.epoch-only', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: none(), epoch: some(512n), custodian: none(),
    }),
    ix('setLockup.custodian-only', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: none(), epoch: none(), custodian: some(k(0x33)),
    }),
    // The two mixed cases that put the custodian's presence byte after exactly one earlier
    // optional. Without them the custodian is only ever seen at offset 6 (nothing before it) or 22
    // (both before it), and the offset an off-by-one would land on is never encoded.
    ix('setLockup.timestamp-and-custodian', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: some(1_700_000_000n), epoch: none(), custodian: some(k(0x33)),
    }),
    ix('setLockup.epoch-and-custodian', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: none(), epoch: some(512n), custodian: some(k(0x33)),
    }),
    ix('setLockup.negative-timestamp', client.getSetLockupInstructionDataEncoder(), {
      unixTimestamp: some(-1n), epoch: some(0n), custodian: none(),
    }),

    ix('merge.bare', client.getMergeInstructionDataEncoder(), {}),

    // authoritySeed is a sizePrefixTypeNode with a *u64* prefix. A u32 prefix reads the same for
    // any seed shorter than 4GiB on little-endian hardware and then starts authorityOwner four
    // bytes early; the recorded regression (SPLClientTests.authorizeStakeAccountWithSeed) was the
    // matching under-allocation, `l()` short by exactly those eight bytes.
    ix('authorizeWithSeed.staker', client.getAuthorizeWithSeedInstructionDataEncoder(), {
      newAuthorizedPubkey: k(0x55), stakeAuthorize: STAKER,
      authoritySeed: 'authority-seed', authorityOwner: k(0x66),
    }),
    ix('authorizeWithSeed.withdrawer', client.getAuthorizeWithSeedInstructionDataEncoder(), {
      newAuthorizedPubkey: k(0x55), stakeAuthorize: WITHDRAWER,
      authoritySeed: 'stake:9', authorityOwner: k(0x66),
    }),
    // An empty seed is eight zero bytes and then the owner, not a skipped prefix.
    ix('authorizeWithSeed.empty-seed', client.getAuthorizeWithSeedInstructionDataEncoder(), {
      newAuthorizedPubkey: k(0x55), stakeAuthorize: STAKER,
      authoritySeed: '', authorityOwner: k(0x66),
    }),
    ix('authorizeWithSeed.multibyte-seed', client.getAuthorizeWithSeedInstructionDataEncoder(), {
      newAuthorizedPubkey: k(0x55), stakeAuthorize: STAKER,
      authoritySeed: MULTIBYTE_SEED, authorityOwner: k(0x66),
    }),
    // The maximum a seed can be: PublicKey::create_with_seed rejects anything longer.
    ix('authorizeWithSeed.max-length-seed', client.getAuthorizeWithSeedInstructionDataEncoder(), {
      newAuthorizedPubkey: k(0x55), stakeAuthorize: STAKER,
      authoritySeed: 'x'.repeat(32), authorityOwner: k(0x66),
    }),

    ix('initializeChecked.bare', client.getInitializeCheckedInstructionDataEncoder(), {}),

    ix('authorizeChecked.staker', client.getAuthorizeCheckedInstructionDataEncoder(), { stakeAuthorize: STAKER }),
    ix('authorizeChecked.withdrawer', client.getAuthorizeCheckedInstructionDataEncoder(), { stakeAuthorize: WITHDRAWER }),

    // Same u64 prefix as authorizeWithSeed, but with no pubkey ahead of it, so the seed starts at
    // offset 8 and there is nothing to absorb a shifted prefix.
    ix('authorizeCheckedWithSeed.withdrawer', client.getAuthorizeCheckedWithSeedInstructionDataEncoder(), {
      stakeAuthorize: WITHDRAWER, authoritySeed: 'checked-seed', authorityOwner: k(0x77),
    }),
    ix('authorizeCheckedWithSeed.empty-seed', client.getAuthorizeCheckedWithSeedInstructionDataEncoder(), {
      stakeAuthorize: STAKER, authoritySeed: '', authorityOwner: k(0x77),
    }),
    ix('authorizeCheckedWithSeed.multibyte-seed', client.getAuthorizeCheckedWithSeedInstructionDataEncoder(), {
      stakeAuthorize: STAKER, authoritySeed: MULTIBYTE_SEED, authorityOwner: k(0x77),
    }),
    ix('authorizeCheckedWithSeed.max-length-seed', client.getAuthorizeCheckedWithSeedInstructionDataEncoder(), {
      stakeAuthorize: WITHDRAWER, authoritySeed: 'x'.repeat(32), authorityOwner: k(0x77),
    }),

    // SetLockupChecked takes the custodian as an account rather than an argument, so it is the
    // two-Option case: the same flattening, one field fewer.
    ix('setLockupChecked.both', client.getSetLockupCheckedInstructionDataEncoder(), {
      unixTimestamp: some(1_700_000_000n), epoch: some(512n),
    }),
    ix('setLockupChecked.none', client.getSetLockupCheckedInstructionDataEncoder(), {
      unixTimestamp: none(), epoch: none(),
    }),
    ix('setLockupChecked.timestamp-only', client.getSetLockupCheckedInstructionDataEncoder(), {
      unixTimestamp: some(1_700_000_000n), epoch: none(),
    }),
    ix('setLockupChecked.epoch-only', client.getSetLockupCheckedInstructionDataEncoder(), {
      unixTimestamp: none(), epoch: some(512n),
    }),

    ix('getMinimumDelegation.bare', client.getGetMinimumDelegationInstructionDataEncoder(), {}),

    ix('deactivateDelinquent.bare', client.getDeactivateDelinquentInstructionDataEncoder(), {}),

    ix('moveStake.lamports', client.getMoveStakeInstructionDataEncoder(), { args: 1_000_000_000n }),

    ix('moveLamports.lamports', client.getMoveLamportsInstructionDataEncoder(), { args: 2_500_000_000n }),
  ];

  const lines = [
    '# Differential encoding vectors for the Stake program. Generated — do not hand-edit.',
    '#',
    "# Produced by tools/stake-vectors.mjs from solana-program/stake's own generated JavaScript",
    '# client, which @codama/renderers-js renders from the same idl.json this repository generates',
    '# from. It is an independent encoder, not ground truth: the mainnet fixture in',
    '# StakeOnChainInstructionTests is what ties that IDL to the deployed program.',
    '#',
    `# upstream: solana-program/stake @ ${head}`,
    `# client:   @solana-program/stake ${version}, generated at ${generated}`,
    `# lockfile: sha256 ${lock}`,
    '#',
    '# One vector per line, "<instruction>.<case> <base64 instruction data>", preceded by the',
    '# arguments it encodes. k(0xNN) is the 32-byte key whose byte i is (0xNN + i * 7) & 0xff.',
    '',
  ];
  for (const { name, args, data } of vectors) {
    lines.push(`# ${describe(args, labels)}`, `${name} ${data}`, '');
  }
  return lines.join('\n');

  function ix(name, encoder, args) {
    return { name, args, data: Buffer.from(encoder.encode(args)).toString('base64') };
  }
}

/// The arguments as they were passed, so a reviewer can check the Java literals against them
/// without running anything. Derived from the same object that was encoded, so it cannot drift
/// from the bytes on the next line.
function describe(args, labels) {
  const value = v => {
    if (typeof v === 'string') {
      return labels.get(v) ?? JSON.stringify(v);
    }
    if (typeof v === 'bigint') {
      return v.toString();
    }
    if (v && typeof v === 'object') {
      if ('__option' in v) {
        return v.__option === 'None' ? 'none' : `some(${value(v.value)})`;
      }
      return `{${Object.entries(v).map(([f, x]) => `${f}: ${value(x)}`).join(', ')}}`;
    }
    return String(v);
  };
  const fields = Object.entries(args).map(([f, v]) => `${f}: ${value(v)}`);
  return fields.length === 0 ? '(no arguments)' : fields.join(', ');
}
