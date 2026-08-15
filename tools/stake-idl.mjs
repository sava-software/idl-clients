// Derives idls/codama/spl/stake.json from solana-program/stake.
//
// The idl.json at that repository's root is an *intermediate* artifact, not a usable IDL: it
// declares a u8 instruction discriminator for a program that serializes with bincode (u32), and it
// references six defined types it never declares. Their codama.mjs carries a `before` pipeline that
// fixes both — rewriting the discriminator, injecting the epoch and unixTimestamp aliases, renaming
// four …Args types, deleting the never-activated redelegate instruction — and every client they
// publish is generated after it runs. This applies that same pipeline and writes the result.
//
//   (cd <solana-program/stake checkout> && pnpm install --frozen-lockfile)
//   node tools/stake-idl.mjs <that checkout> [output path]
//
// Run it from anywhere. Re-run when upstream changes idl.json or codama.mjs; the output is
// committed, so the diff is the review.
import { readFileSync, writeFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import { dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

const repo = process.argv[2];
if (!repo) {
  console.error('usage: node tools/stake-idl.mjs <solana-program/stake checkout> [output]');
  process.exit(2);
}

// Every codama import has to resolve from the *checkout's* node_modules. A bare specifier in ESM
// resolves against the importing module's own path — which is this repository's tools/, where
// codama is not installed — and the working directory does not enter into it, so `cd`-ing to the
// checkout first does not help and used to be documented as though it did.
const requireFromCheckout = createRequire(resolve(repo, 'package.json'));
const fromCheckout = spec => import(pathToFileURL(requireFromCheckout.resolve(spec)).href);
const { createFromRoot } = await fromCheckout('codama');

// The default output belongs to this repository wherever the caller happens to be standing, so it
// is resolved from this file rather than from the working directory.
const out = process.argv[3]
  ?? resolve(dirname(fileURLToPath(import.meta.url)), '..', 'idls/codama/spl/stake.json');
const config = (await import(pathToFileURL(resolve(repo, 'codama.mjs')).href)).default;
const codama = createFromRoot(JSON.parse(readFileSync(resolve(repo, config.idl ?? 'idl.json'), 'utf8')));

// What the codama CLI does for `before`: each entry is a bare 'module#export' string, or
// { from: 'module#export', args: [...] }. Only `before` is applied — `scripts` renders their
// JS and Rust clients, which is not what we want.
for (const entry of config.before ?? []) {
  const spec = typeof entry === 'string' ? entry : entry.from;
  const args = typeof entry === 'string' ? [] : (entry.args ?? []);
  const [mod, name] = spec.split('#');
  const imported = await fromCheckout(mod);
  const factory = imported[name] ?? imported.default?.[name];
  if (typeof factory !== 'function') {
    throw new Error(`codama.mjs references ${spec}, which is not a visitor factory`);
  }
  codama.update(factory(...args));
}

// No trailing newline: idl-src-gen copies this file verbatim into each package's gen/idl.json,
// and both artifacts are committed without one. Adding it here would show up as a one-byte diff
// in generated output on the next run, which is exactly the noise 'the diff is the review' needs
// to be free of.
writeFileSync(out, JSON.stringify(codama.getRoot(), null, 2));
console.log(`wrote ${out}`);
