// Derives idls/codama/spl/stake.json from solana-program/stake.
//
// The idl.json at that repository's root is an *intermediate* artifact, not a usable IDL: it
// declares a u8 instruction discriminator for a program that serializes with bincode (u32), and it
// references six defined types it never declares. Their codama.mjs carries a `before` pipeline that
// fixes both — rewriting the discriminator, injecting the epoch and unixTimestamp aliases, renaming
// four …Args types, deleting the never-activated redelegate instruction — and every client they
// publish is generated after it runs. This applies that same pipeline and writes the result.
//
//   cd <solana-program/stake checkout> && pnpm install
//   node <this>/tools/stake-idl.mjs <that checkout> [output path]
//
// Run it from the upstream checkout so `codama` resolves from its node_modules. Re-run when
// upstream changes idl.json or codama.mjs; the output is committed, so the diff is the review.
import { readFileSync, writeFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { createFromRoot } from 'codama';

const repo = process.argv[2];
if (!repo) {
  console.error('usage: node tools/stake-idl.mjs <solana-program/stake checkout> [output]');
  process.exit(2);
}
const out = process.argv[3] ?? 'idls/codama/spl/stake.json';
const config = (await import(resolve(repo, 'codama.mjs'))).default;
const codama = createFromRoot(JSON.parse(readFileSync(resolve(repo, config.idl ?? 'idl.json'), 'utf8')));

// What the codama CLI does for `before`: each entry is a bare 'module#export' string, or
// { from: 'module#export', args: [...] }. Only `before` is applied — `scripts` renders their
// JS and Rust clients, which is not what we want.
for (const entry of config.before ?? []) {
  const spec = typeof entry === 'string' ? entry : entry.from;
  const args = typeof entry === 'string' ? [] : (entry.args ?? []);
  const [mod, name] = spec.split('#');
  const imported = await import(mod);
  const factory = imported[name] ?? imported.default?.[name];
  if (typeof factory !== 'function') {
    throw new Error(`codama.mjs references ${spec}, which is not a visitor factory`);
  }
  codama.update(factory(...args));
}

writeFileSync(out, JSON.stringify(codama.getRoot(), null, 2) + '\n');
console.log(`wrote ${out}`);
