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
return 101 the probe reports INCONCLUSIVE rather than guessing.

PENDING REWRITE (2026-08-13). This tool is being ported to Java so it can use sava-core for
transaction building rather than hand-rolling base58 and the message layout, which is the part of
it least safe to duplicate. Two known defects are deliberately left for that port rather than
patched here: the error classification below treats 102 / NotEnoughAccountKeys as universally
meaning "dispatched" and InvalidAccountData as universally meaning "not", which holds for Anchor
programs and is not a general rule; and the undeployed-instruction total mis-reads its report row.
Neither is covered by a test, because this file has none — also a gap the port should close.

INCONCLUSIVE means only that the control did not produce error 101. It does not
establish how the program dispatches: a native, Shank or pinocchio program
emits no fallback error, and neither does an Anchor program whose own #[fallback]
handles the unknown discriminator itself — they are indistinguishable from out
here. Settling it needs the deployed source.
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



# Anchor's dispatch errors. 102 means the discriminator matched and the *arguments* did not
# deserialize, which is what a probe carrying no args should expect from a live instruction;
# NotEnoughAccountKeys means it matched and validated accounts first. Either proves dispatch.
DISPATCHED_CODES = frozenset([102, 'NotEnoughAccountKeys'])
# 101 is Anchor's explicit "no such instruction". InvalidAccountData is what a program without a
# fallback handler returns once dispatch has already failed.
NOT_DISPATCHED_CODES = frozenset([101, 'InvalidAccountData'])


def _error_code(err):
    """The instruction error, as a Custom int or a bare string, or None."""
    if not isinstance(err, dict):
        return None
    ie = err.get('InstructionError')
    if not isinstance(ie, list) or len(ie) < 2:
        return None
    detail = ie[1]
    return detail.get('Custom') if isinstance(detail, dict) else detail


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
            # Dispatch is decided by the *error*, not by the logs.
            #
            # 101 InstructionFallbackNotFound is one shape of "no such instruction" and the probe
            # used to treat it as the only one. Jupiter Swap answers a garbage discriminator with
            # InvalidAccountData instead, which is just as decisive: a declared discriminator there
            # returns 102 InstructionDidNotDeserialize or NotEnoughAccountKeys, because dispatch
            # succeeded and only the empty args or account list failed afterwards.
            #
            # Deliberately NOT keyed on `Program log: Instruction: <Name>`. That line is emitted by
            # Anchor's dispatch and can be stripped: Jupiter's `route` and `route_v2` — its two
            # busiest instructions — emit none, so a log-based check calls them dead. That failure
            # is quiet and convincing, which makes it worse than an inconclusive answer.
            code = _error_code(v.get('err'))
            if code in DISPATCHED_CODES:
                results.append('LIVE')
            elif code in NOT_DISPATCHED_CODES:
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
            continue  # no instruction list to probe
        pid_b58 = p.get('program') or idl.get('address')
        if not pid_b58:
            continue
        pid = b58d(pid_b58)

        calib = probe_many(pid, [GARBAGE])[0]
        if calib == 'UNKNOWN':
            # An exhausted or malformed RPC response, not a fact about the program. Reported as an
            # operational failure rather than folded into INCONCLUSIVE: with the endpoint down,
            # every control answers UNKNOWN, every program is "structurally inconclusive", and the
            # sweep exits 0 having probed nothing. A tool whose whole job is to notice a stale IDL
            # must not report success because it could not ask.
            report.append((p['name'], pid_b58, 'UNREACHABLE', len(ixs), [],
                           'control did not answer; RPC exhausted or malformed', 0))
            print(f"  {p['name']:34} UNREACHABLE (no control response)", file=sys.stderr)
            continue
        if calib != 'DEAD':
            report.append((p['name'], pid_b58, 'INCONCLUSIVE', len(ixs), [],
                           f'garbage discriminator -> {calib}, no fallback error 101', 0))
            print(f"  {p['name']:34} INCONCLUSIVE ({calib})", file=sys.stderr)
            continue

        named = [(i['name'], bytes(i['discriminator'])) for i in ixs
                 if i.get('discriminator')]
        states = probe_many(pid, [d for _, d in named])
        dead = [n for (n, _), s in zip(named, states) if s == 'DEAD']
        unk = sum(1 for s in states if s == 'UNKNOWN')
        status = 'STALE' if dead else ('OK' if unk == 0 else 'OK*')
        report.append((p['name'], pid_b58, status, len(named), dead,
                       f'{unk} inconclusive' if unk else '', unk))
        print(f"  {p['name']:34} {status:6} {len(named):3} ix, {len(dead)} dead"
              + (f", {unk} unknown" if unk else ""), file=sys.stderr)

    print("\n" + "=" * 72)
    stale = [r for r in report if r[2] == 'STALE']
    unexpected = [(name, pid, n, [d for d in dead if (name, d) not in ACCEPTED_UNDEPLOYED])
                  for name, pid, st, n, dead, note, _ in stale]
    unexpected = [u for u in unexpected if u[3]]
    accepted = sum(1 for name, _, _, _, dead, _, _ in stale for d in dead
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
        print(f"\ninconclusive (no fallback error 101): {', '.join(r[0] for r in inc)}")
        print("  The control did not return 101, so this signal does not apply. That is")
        print("  all it establishes: a native, Shank or pinocchio program has no fallback")
        print("  error, and neither does an Anchor program whose own #[fallback] handles it.")
        print("  Verify these against their Rust instead — see docs/PROGRAM_VERIFICATION.md.")
    unreachable = [r for r in report if r[2] == 'UNREACHABLE']
    if unreachable:
        print(f"\nUNREACHABLE ({len(unreachable)}): {', '.join(r[0] for r in unreachable)}")
        print("  The control never answered for these, so nothing was probed. This is an RPC")
        print("  failure, not a result: re-run, or point SOLANA_RPC at an endpoint that answers.")

    # An instruction that never answered is not an instruction that dispatched. Counting it as a
    # pass would let a partly-failed sweep read exactly like a clean one.
    partial = [(name, unk) for name, _, st, _, _, _, unk in report if unk]
    if partial:
        print(f"\nincomplete ({len(partial)} program(s)): some instructions never answered")
        for name, unk in partial:
            print(f"    {name}: {unk} instruction(s) with no response")
        print("  An instruction that did not answer is not one that dispatched, so this is a")
        print("  failed sweep rather than a clean one.")

    sys.exit(1 if (unexpected or unreachable or partial) else 0)


if __name__ == '__main__':
    main()
