// Emits reference *account* decodes for the Token 2022 program, from solana-program/token-2022's
// own generated JavaScript client.
//
// The companion to tools/token2022-vectors.mjs, on the other half of the wire format: those vectors
// are instruction data and account lists, these are account *data* — what upstream's reader makes
// of a `Mint`, a `Token` and a `Multisig` buffer. Every other account test here is a round trip,
// reading a buffer with the generated reader and asserting the fields it produced, so a systematic
// misreading (a u16 TLV length read as u32, a `COption` prefix width, a padding rule, an extension
// ordinal that has drifted) is invisible: there is only one reader. Only a *second, independent*
// reader can see it.
//
// That reader is upstream's: `clients/js/src/generated/accounts/{mint,token}.ts` is rendered by
// @codama/renderers-js from the same codama IDL this repository generates from. So it is a peer
// implementation and **not ground truth** — both sides descend from one document, and nothing here
// says that document matches the deployed program. What ties these buffers to chain is that 25 of
// them were captured off mainnet at a recorded slot; what ties the *interpretation* to the program
// is nothing in this file. The validator's own `jsonParsed` decode sits beside each account as
// `<address>.parsed.json` and is a genuinely different oracle, deliberately not read here.
//
//   cd <solana-program/token-2022 checkout>/clients/js && pnpm install --frozen-lockfile
//   node tools/token2022-account-vectors.mjs <that checkout> [output path]
//
// Transpiled rather than type-stripped, for the reason the sibling script gives: the client's
// `enum`s cannot be erased by Node. **Unlike that script this compiles `src`, not `src/generated`.**
// Both account decoders import `getExtensionsDecoder` from `../../hooked` — `src/hooked/
// extensions.ts`, hand-written, and the thing that actually walks the TLV region — so `--rootDir
// src/generated` fails outright: the emit for `hooked` lands outside the root. Naming
// `src/generated/index.ts` as the entry under `--rootDir src` is the minimal fix, because those two
// imports are the only ones in `src/generated` that escape it: tsc emits `generated/**` plus
// `hooked/**` and leaves the rest of `src` (the plugin, the confidential-transfer helpers, the ATA
// wrappers) uncompiled. Note what that means for the comparison — the TLV walk on the JavaScript
// side is hand-written, so over the extension region this measures against a human's reading of the
// program rather than against a second rendering of the IDL. Nothing is written inside the checkout,
// and a dirty tree under `src/generated`, `src/hooked` or the lockfile is refused, not recorded.
//
// A decoded value is serialized so the JSON is stable across runs and diffable: bigint as a decimal
// string, `Uint8Array` as base64, `Map` as an array of [key, value] pairs in iteration order,
// non-finite number as its `String()` form (JSON has no NaN), and kit's own shapes kept for the
// rest — `{"__option":"Some","value":..}` / `{"__option":"None"}`, `"__kind"` on a union variant,
// base58 for an address, the numeric ordinal for an enum-valued field (`state: 2` is Frozen).
// Object key order is the decoder's field order, so it is the IDL's. A failed decode records
// `{"ok": false, "error": ..}`, the text being `SolanaError[<code name>]: <message>`, first line
// only and verbatim — kit's constant-mismatch message embeds a hex dump of the whole buffer, which
// is why some are long, and those are the same bytes as the vector's own `data`.
//
// The output is committed as a test resource, and the diff is the review. Do not regenerate on a
// schedule, for the reason tools/README.md gives under the sibling script.
import { spawnSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { existsSync, mkdtempSync, readFileSync, rmSync, symlinkSync, writeFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { pathToFileURL } from 'node:url';

const CORPUS = 'idl-clients-spl/src/test/resources/token_2022/accounts';

const repo = process.argv[2];
if (!repo) {
  console.error('usage: node tools/token2022-account-vectors.mjs <solana-program/token-2022 checkout> [output]');
  process.exit(2);
}
const out = process.argv[3] ?? join(CORPUS, 'reference-decodes.json');

const clientsJs = resolve(repo, 'clients/js');
const modules = resolve(clientsJs, 'node_modules');
const tsc = resolve(modules, '.bin/tsc');
if (!existsSync(tsc)) {
  console.error(`${tsc} is missing — run \`pnpm install --frozen-lockfile\` in ${clientsJs}`);
  process.exit(2);
}

const work = mkdtempSync(join(tmpdir(), 'token2022-account-vectors-'));
try {
  symlinkSync(modules, join(work, 'node_modules'), 'dir');
  // --ignoreConfig: their tsconfig declares `include`, which tsc refuses to combine with a named
  // entry file. Everything it sets that matters to *emit* is restated; the type-only options it
  // drops cannot change what a decoder reads. CommonJS output keeps TypeScript's extensionless
  // relative imports resolvable without rewriting them. --rootDir src, entry src/generated: see
  // the header.
  const src = resolve(clientsJs, 'src');
  const tsvc = spawnSync(tsc, [
    '--ignoreConfig', resolve(src, 'generated/index.ts'),
    '--outDir', work,
    '--rootDir', src,
    '--module', 'commonjs',
    '--target', 'es2022',
    '--skipLibCheck',
    '--esModuleInterop',
  ], { stdio: 'inherit' });
  if (tsvc.status !== 0) {
    throw new Error(`tsc exited ${tsvc.status}`);
  }
  const client = await import(pathToFileURL(join(work, 'generated/index.js')).href);
  const kit = createRequire(resolve(clientsJs, 'package.json'))('@solana/kit');

  const vectors = render(client, errorNames(kit));
  const provenance = describe(repo, clientsJs, vectors);
  writeFileSync(out, `${JSON.stringify({ provenance, vectors }, null, 2)}\n`);
  console.log(`wrote ${out}: ${vectors.length} vectors`);
} finally {
  rmSync(work, { recursive: true, force: true });
}

/// What the decodes were produced from, on the pattern tools/token2022-vectors.mjs sets: the
/// checkout revision, the revisions of both inputs that decide how a buffer reads, and a digest of
/// the lockfile pinning every codec combinator actually loaded — those come from `@solana/kit`, so
/// a dependency bump changes what a decoder accepts without touching this checkout at all.
/// `src/hooked` joins `src/generated` here because the TLV walk lives there.
function describe(repo, clientsJs, vectors) {
  const git = (...args) => spawnSync('git', args, { cwd: repo, encoding: 'utf8' }).stdout?.trim();

  const inputs = ['clients/js/src/generated', 'clients/js/src/hooked', 'clients/js/pnpm-lock.yaml'];
  const dirty = git('status', '--porcelain', '--', ...inputs);
  if (dirty) {
    console.error('the checkout has uncommitted changes under the inputs these decodes depend on:\n'
        + dirty + '\ncommit or stash them — a recorded revision that does not describe the decodes'
        + ' is worse than none.');
    process.exit(2);
  }

  return {
    upstream: `solana-program/token-2022 @ ${git('rev-parse', 'HEAD') || '(unknown)'}`,
    client: `@solana-program/token-2022 ${createRequire(resolve(clientsJs, 'package.json'))('./package.json').version}`,
    generated: git('log', '-1', '--format=%H', '--', 'clients/js/src/generated') || '(unknown)',
    hooked: git('log', '-1', '--format=%H', '--', 'clients/js/src/hooked') || '(unknown)',
    lock: `sha256 ${createHash('sha256').update(readFileSync(resolve(clientsJs, 'pnpm-lock.yaml'))).digest('hex')}`,
    vectors: vectors.length,
    format: 'Generated by tools/token2022-account-vectors.mjs — do not hand-edit. One vector per'
        + ' buffer decoded by upstream\'s generated JavaScript client: `data` is its base64, `mint`'
        + ' is getMintDecoder().decode(data) and `token` is getTokenDecoder().decode(data), each'
        + ' {"ok":true,"value":..} or {"ok":false,"error":".."}; a key is absent where that decoder'
        + ' was not attempted. bigint is a decimal string, byte arrays base64, Map an array of'
        + ' [key,value] pairs in iteration order; options keep kit\'s {"__option":..} shape, union'
        + ' variants their "__kind", enum-valued fields their numeric ordinal, and object keys the'
        + ' decoder\'s field order. An error is `SolanaError[<code name>]: <message>`, first line'
        + ' only and verbatim — kit\'s constant-mismatch message embeds a hex dump of the buffer.'
        + ' The 25 mainnet rows are named by address and carry the applicable decoder only (both,'
        + ' for the multisig); synthetic rows carry both. mint-base-only-82 and multisig-355 re-use'
        + ' mainnet bytes under a case name, so those buffers appear twice. A peer implementation,'
        + ' not ground truth: both readers descend from one IDL.',
  };
}

/// kit numbers its error codes and names them as separate exports; this inverts that so the
/// recorded error says `SOLANA_ERROR__CODECS__INVALID_CONSTANT` rather than `8078018`.
function errorNames(kit) {
  return new Map(Object.keys(kit)
      .filter(k => k.startsWith('SOLANA_ERROR__') && typeof kit[k] === 'number')
      .map(k => [kit[k], k]));
}

function render(client, codes) {
  const mint = client.getMintDecoder();
  const token = client.getTokenDecoder();
  const corpus = name => new Uint8Array(readFileSync(join(CORPUS, name)));
  const cat = (...parts) => new Uint8Array(Buffer.concat(parts.map(p => Buffer.from(p))));
  const zeros = n => new Uint8Array(n);

  /// `which` says which decoders were attempted. A mainnet mint is not a candidate `Token`, so
  /// recording that certain failure would say nothing; but every synthetic buffer is here *because*
  /// its kind is arguable, so both run for all of them — `mint-wrong-type-byte-2` and
  /// `token-wrong-type-byte-1` exist precisely to record what the other reader does.
  const row = (name, source, data, which) => ({
    name,
    source,
    data: Buffer.from(data).toString('base64'),
    ...(which !== 'token' ? { mint: attempt(mint, data) } : {}),
    ...(which !== 'mint' ? { token: attempt(token, data) } : {}),
  });

  function attempt(decoder, data) {
    try {
      return { ok: true, value: plain(decoder.decode(data)) };
    } catch (e) {
      const code = e?.context?.__code;
      const tag = codes.has(code) ? `${e.name}[${codes.get(code)}]` : (e?.name ?? 'Error');
      return { ok: false, error: `${tag}: ${String(e?.message ?? e).split('\n')[0]}` };
    }
  }

  const vectors = [];

  // The mainnet corpus, in manifest order. `kind` decides which reader applies; the multisig is
  // 355 bytes of a third account type and both readers are expected to refuse it.
  const manifest = JSON.parse(readFileSync(join(CORPUS, 'manifest.json'), 'utf8'));
  for (const { address, kind } of manifest.accounts) {
    vectors.push(row(address, `mainnet:${address}`, corpus(address),
        kind === 'multisig' ? 'both' : (kind === 'mint' ? 'mint' : 'token')));
  }

  // The two mainnet buffers the synthetic cases are built on.
  const MINT_82 = corpus('113Dbys19a4PMNYt5v1153Kwta5aQSD6VFu5sqQw5tz'); // extension-free mint, Mint::LEN
  const TOKEN_165 = corpus('1TeeextCMbWvAYDShphXyg9macWQCJkowqeU4iR9nUa'); // extension-free token, Account::LEN
  const META_357 = corpus('1DMqxdD2LQF8dR8qh5ULVK7pVx616DggBT3pKEKDPJ5'); // MetadataPointer + TokenMetadata

  // A mint's extended header is [82 base][83 zeroes][0x01]; a token's is [165 base][0x02]. Both
  // land the account-type byte at offset 165 and open the TLV region at 166. A TLV entry is
  // [type u16 LE][length u16 LE][length bytes]; ImmutableOwner is type 7 with no payload.
  const MINT_HEADER = cat(MINT_82, zeros(83), [0x01]); //                        82 + 83 + 1 = 166
  const TOKEN_HEADER = cat(TOKEN_165, [0x02]); //                                    165 + 1 = 166
  const IMMUTABLE_OWNER = new Uint8Array([0x07, 0x00, 0x00, 0x00]); //  type 7, length 0, no payload
  const UNINITIALIZED = new Uint8Array([0x00, 0x00]); //             type 0: the program's TLV padding

  const synthetic = [
    // 82 bytes, no account-type byte and no TLV region at all — the pre-extension `Mint::LEN`.
    ['mint-base-only-82', 'mainnet:113Dbys19a4PMNYt5v1153Kwta5aQSD6VFu5sqQw5tz', MINT_82],
    // 166 bytes: the extended header and an empty TLV region.
    ['mint-type-byte-no-tlv-166', 'synthetic', MINT_HEADER],
    ['token-type-byte-no-tlv-166', 'synthetic', TOKEN_HEADER],
    // 170 bytes: header + one zero-length entry, the shape every ATA on mainnet has.
    ['token-immutable-owner-170', 'synthetic', cat(TOKEN_HEADER, IMMUTABLE_OWNER)],
    // 172: + an Uninitialized type word, which is how the program pads. 171 and 173 and 176 are
    // the same entry followed by 1, 3 and 6 spare bytes — an odd tail cannot hold a type word, and
    // the walk has to stop rather than read past the end.
    ['token-immutable-owner-plus-two-zero-bytes', 'synthetic', cat(TOKEN_HEADER, IMMUTABLE_OWNER, zeros(2))],
    ['token-immutable-owner-plus-one-zero-byte', 'synthetic', cat(TOKEN_HEADER, IMMUTABLE_OWNER, zeros(1))],
    ['token-immutable-owner-plus-three-zero-bytes', 'synthetic', cat(TOKEN_HEADER, IMMUTABLE_OWNER, zeros(3))],
    ['token-immutable-owner-plus-six-zero-bytes', 'synthetic', cat(TOKEN_HEADER, IMMUTABLE_OWNER, zeros(6))],
    // 172: padding *first*, then a real entry. The program's walk stops at type 0, so the
    // ImmutableOwner behind it is unreachable — a reader that instead skipped 4 bytes and kept
    // going would report one extension where the program reports none.
    ['token-uninitialized-then-nonzero', 'synthetic', cat(TOKEN_HEADER, UNINITIALIZED, IMMUTABLE_OWNER)],
    // 178: an entry whose type (99) no `ExtensionType` names, carrying 4 bytes, ahead of a known
    // one. Forward compatibility: a program that has grown an extension this client has not.
    ['token-unknown-extension-99-then-immutable-owner', 'synthetic',
      cat(TOKEN_HEADER, [0x63, 0x00, 0x04, 0x00, 0xde, 0xad, 0xbe, 0xef], IMMUTABLE_OWNER)],
    // 170: a mint body under a *token*'s account-type byte, and the mirror. Nothing but that one
    // byte at offset 165 distinguishes the two account kinds once both are extended.
    ['mint-wrong-type-byte-2', 'synthetic', cat(MINT_82, zeros(83), [0x02], IMMUTABLE_OWNER)],
    ['token-wrong-type-byte-1', 'synthetic', cat(TOKEN_165, [0x01], IMMUTABLE_OWNER)],
    // 234 bytes, all zero but for one MetadataPointer entry: [166]=18 type, [168]=64 length,
    // [170..201]=0x01 authority, [202..233]=0 address. A mint between InitializeMetadataPointer
    // and InitializeMint — the base is still zeroed, so the account-type byte at 165 is 0, not 1.
    ['mint-pre-init-metadata-pointer-234', 'synthetic', (() => {
      const b = zeros(234);
      b[166] = 18;
      b[168] = 64;
      b.fill(0x01, 170, 202);
      return b;
    })()],
    // 233: a MetadataPointer declaring 63 bytes where its two pubkeys need 64, with 63 present.
    ['mint-metadata-pointer-short-length-63', 'synthetic',
      cat(MINT_HEADER, [0x12, 0x00, 0x3f, 0x00], new Uint8Array(63).fill(0x01))],
    // 180: the same entry declaring its true 64 bytes with only 10 in the buffer.
    ['mint-metadata-pointer-length-exceeds-remaining', 'synthetic',
      cat(MINT_HEADER, [0x12, 0x00, 0x40, 0x00], new Uint8Array(10).fill(0x01))],
    // 100 bytes: a real extended mint cut off inside the 83 zero bytes, so the base struct is
    // whole and the account-type byte is missing.
    ['mint-truncated-in-padding-100', 'synthetic', META_357.slice(0, 100)],
    // 357 bytes: the same mint with the TokenMetadata length word at offset 236 raised from 119
    // to 120. NOTE the corpus buffer has no trailing pad — its TLV runs [166] MetadataPointer
    // (len 64), [234] TokenMetadata (len 119), ending at 357 exactly, the manifest's "two-byte
    // pad" note notwithstanding — so +1 makes the declared region reach one byte past the end
    // rather than swallow a pad byte.
    ['mint-token-metadata-length-slack', 'synthetic', (() => {
      const b = META_357.slice();
      new DataView(b.buffer, b.byteOffset).setUint16(236, 120, true);
      return b;
    })()],
    // 355 bytes of `Multisig`: m, n, isInitialized, then 11 signer slots. Byte 165 falls in the
    // middle of a signer, so neither account-type byte can match.
    ['multisig-355', 'mainnet:et1Arzfg3zufiKMyNtudiM7QVWzG4F9e3ukWxiHAZMs',
      corpus('et1Arzfg3zufiKMyNtudiM7QVWzG4F9e3ukWxiHAZMs')],
  ];
  for (const [name, source, data] of synthetic) {
    vectors.push(row(name, source, data, 'both'));
  }
  return vectors;
}

/// A decoded value in a form JSON can hold and a diff can read. See the header for the rules.
function plain(v) {
  if (typeof v === 'bigint') return v.toString();
  if (typeof v === 'number') return Number.isFinite(v) ? v : String(v);
  if (v instanceof Uint8Array) return Buffer.from(v).toString('base64');
  if (v instanceof Map) return [...v].map(([k, x]) => [plain(k), plain(x)]);
  if (Array.isArray(v)) return v.map(plain);
  if (v && typeof v === 'object') return Object.fromEntries(Object.entries(v).map(([f, x]) => [f, plain(x)]));
  return v;
}
