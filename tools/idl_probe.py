#!/usr/bin/env python3
"""Probe every configured program for instructions our generated IDL declares
but the deployed program does not dispatch.

Run from the repo root:  python3 tools/idl_probe.py

Read-only: it only simulates transactions, never signs or sends. Roughly one
RPC batch per 20 instructions, so a full sweep is a few minutes against the
public endpoint.

An Anchor program answers an unknown 8-byte discriminator with
InstructionFallbackNotFound (custom error 101). A known one logs
"Instruction: <Name>" and then fails on account/argument validation. So a
declared instruction that returns 101 is *dead*: the IDL we generated from is
stale, and any client method wrapping it can never succeed.

Each program is calibrated with a garbage discriminator first. If that does not
return 101, the program is not Anchor-dispatch-shaped and is reported as
INCONCLUSIVE rather than guessed at.
"""
import json, os, base64, subprocess, sys, time

import os
RPC = os.environ.get("SOLANA_RPC", "https://api.mainnet-beta.solana.com")
# any funded system account works: the probe instruction takes zero accounts,
# so the payer is only the fee payer. Simulation aborts early if it doesn't exist.
PAYER_B58 = "CYXEgwbPHu2f9cY3mcUkinzDoDcsSan7myh1uBvYRbEw"
GARBAGE = bytes([0xDE, 0xAD, 0xBE, 0xEF, 0xDE, 0xAD, 0xBE, 0xEF])
BATCH = 20

# Instructions a program declares but does not dispatch, which are known-benign.
# Anything NOT listed here is a finding: the IDL is describing an instruction the
# deployed program does not have, so any client method wrapping it is dead.
ACCEPTED_UNDEPLOYED = {
    # a stub that exists only to force the IDL to emit zero-copy types
    ("Meteora DLMM", "for_idl_type_generation_do_not_call"),
    # an other-SVM-chain variant not enabled on Solana mainnet; its four
    # pull_feed_submit_response* siblings all dispatch
    ("Switchboard On-Demand", "pull_feed_submit_response_svm"),
}

ALPH = '123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz'


def b58d(s):
    n = 0
    for c in s:
        n = n * 58 + ALPH.index(c)
    return n.to_bytes(32, 'big')


PAYER = b58d(PAYER_B58)


def tx_for(pid: bytes, disc: bytes) -> str:
    msg = bytes([1, 0, 1]) + bytes([2]) + PAYER + pid + bytes(32)
    msg += bytes([1, 1, 0]) + bytes([len(disc)]) + disc
    return base64.b64encode(bytes([1]) + bytes(64) + msg).decode()


def rpc_batch(reqs, attempt=0):
    r = subprocess.run(
        ['curl', '-s', '--max-time', '90', RPC, '-X', 'POST',
         '-H', 'Content-Type: application/json', '-d', json.dumps(reqs)],
        capture_output=True, text=True)
    try:
        out = json.loads(r.stdout)
    except Exception:
        out = None
    if not isinstance(out, list) or len(out) != len(reqs):
        if attempt < 4:
            time.sleep(2 ** attempt)
            return rpc_batch(reqs, attempt + 1)
        return [{} for _ in reqs]
    return out


def probe_many(pid: bytes, discs):
    """-> list of 'DEAD' | 'LIVE' | 'UNKNOWN', aligned with discs."""
    results = []
    for i in range(0, len(discs), BATCH):
        chunk = discs[i:i + BATCH]
        reqs = [{"jsonrpc": "2.0", "id": j, "method": "simulateTransaction",
                 "params": [tx_for(pid, d),
                            {"encoding": "base64", "sigVerify": False,
                             "replaceRecentBlockhash": True}]}
                for j, d in enumerate(chunk)]
        resp = rpc_batch(reqs)
        by_id = {r.get('id'): r for r in resp if isinstance(r, dict)}
        for j in range(len(chunk)):
            v = (by_id.get(j) or {}).get('result', {})
            v = v.get('value', {}) if isinstance(v, dict) else {}
            logs = v.get('logs') or []
            if any('FallbackNotFound' in l for l in logs):
                results.append('DEAD')
            elif logs:
                results.append('LIVE')
            else:
                results.append('UNKNOWN')
        time.sleep(0.35)
    return results


def main():
    if not os.path.exists('main_net_programs.json'):
        sys.exit("run from the repo root (main_net_programs.json not found)")
    cfg = json.load(open('main_net_programs.json'))
    base = cfg['basePackage'].replace('.', '/')
    report = []

    for p in cfg['programs']:
        pkg = p.get('package', '').replace('.', '/')
        path = os.path.join(p['source'], 'src/main/java', base, pkg, 'gen/idl.json')
        try:
            idl = json.load(open(path))
            ixs = idl['instructions']
        except Exception:
            continue  # native / non-Anchor IDL shape
        pid_b58 = p.get('program') or idl.get('address')
        if not pid_b58:
            continue
        pid = b58d(pid_b58)

        calib = probe_many(pid, [GARBAGE])[0]
        if calib != 'DEAD':
            report.append((p['name'], pid_b58, 'INCONCLUSIVE', len(ixs), [],
                           f'garbage discriminator -> {calib}, not Anchor-dispatch'))
            print(f"  {p['name']:34} INCONCLUSIVE ({calib})", file=sys.stderr)
            continue

        named = [(i['name'], bytes(i['discriminator'])) for i in ixs
                 if i.get('discriminator')]
        states = probe_many(pid, [d for _, d in named])
        dead = [n for (n, _), s in zip(named, states) if s == 'DEAD']
        unk = sum(1 for s in states if s == 'UNKNOWN')
        status = 'STALE' if dead else ('OK' if unk == 0 else 'OK*')
        report.append((p['name'], pid_b58, status, len(named), dead,
                       f'{unk} inconclusive' if unk else ''))
        print(f"  {p['name']:34} {status:6} {len(named):3} ix, {len(dead)} dead"
              + (f", {unk} unknown" if unk else ""), file=sys.stderr)

    print("\n" + "=" * 72)
    stale = [r for r in report if r[2] == 'STALE']
    unexpected = [(name, pid, n, [d for d in dead if (name, d) not in ACCEPTED_UNDEPLOYED])
                  for name, pid, st, n, dead, note in stale]
    unexpected = [u for u in unexpected if u[3]]
    accepted = sum(1 for name, _, _, _, dead, _ in stale for d in dead
                   if (name, d) in ACCEPTED_UNDEPLOYED)
    print(f"programs probed: {len(report)}   "
          f"undeployed instructions: {sum(len(d) for *_, d, _ in stale)} "
          f"({accepted} accepted, {sum(len(u[3]) for u in unexpected)} unexpected)")
    for name, pid, n, dead in unexpected:
        print(f"\n### {name}  ({pid})  <<< UNEXPECTED")
        print(f"    {len(dead)}/{n} declared instructions are NOT deployed:")
        for dname in dead:
            print(f"      - {dname}")
        print("    Either the IDL is stale (see docs/PROGRAM_VERIFICATION.md) or")
        print("    this is benign and belongs in ACCEPTED_UNDEPLOYED with a reason.")
    inc = [r for r in report if r[2] == 'INCONCLUSIVE']
    if inc:
        print(f"\ninconclusive (not Anchor-dispatch): {', '.join(r[0] for r in inc)}")
        print("  These use native/Shank/pinocchio dispatch, so the fallback signal")
        print("  does not apply. Verify them against their Rust instead — see")
        print("  docs/PROGRAM_VERIFICATION.md.")
    sys.exit(1 if unexpected else 0)


if __name__ == '__main__':
    main()
