#!/usr/bin/env python3
"""Diff a generated client's account order against the program's Rust source.

    python3 tools/ground_truth.py anchor <rust-dir> <Program.java>
    python3 tools/ground_truth.py shank  <instructions.rs> <Program.java>

Exit status is 0 when nothing differs, 1 otherwise.

WHAT THIS IS FOR
The IDL is a lossy artifact; the program's Rust is ground truth. Comparing the
two positionally is what surfaced most of the account-ordering defects this
repo has fixed — a transposed pair of same-typed `PublicKey` accounts compiles
cleanly and fails only on chain.

WHAT THIS IS NOT
An oracle. It is an *assistive* diff: every reported difference needs triage,
and in practice most are artifacts. Read docs/PROGRAM_VERIFICATION.md before
acting on output. The recurring traps, all of which have produced false alarms:

  * Auto-wired accounts. The client resolves well-known programs and sysvars
    internally instead of taking them as parameters, so `rent` reads as
    `solanaAccounts.rentSysVar()`. Normalised here, but the list below is not
    exhaustive — extend AUTOWIRED rather than "fixing" the client.
  * Wrong-struct matches. A monorepo holds several programs and structs are
    matched by name alone, so `PostMessage` from an example program can pair
    with a real one and report every slot as different. Check the file a struct
    came from before believing a wholesale mismatch.
  * Per-program struct naming. CCTP suffixes its account structs `Context`
    (`AcceptOwnershipContext` -> `acceptOwnership`); `--strip-suffix` handles
    that shape. A run reporting "compared 0" is a failure to compare, not a
    pass, so the compared count is always printed.
  * Published IDLs that do not match the repo. Orca's IDL declares a trailing
    `whirlpool_program` on all 66 instructions and its Rust declares it on none.
    Verify against the on-chain IDL before treating that as a defect.
"""
import json
import pathlib
import re
import sys

# ---------------------------------------------------------------------------
# Rust: Anchor `#[derive(Accounts)]`
# ---------------------------------------------------------------------------

FIELD = re.compile(r'^(?:pub\s+)?(\w+)\s*:\s*(.+?),?\s*$')


def strip_attrs(body):
    """Drop #[...] attributes (brace/bracket aware) and // comments, keeping
    a marker for cfg-gated fields so duplicates can be de-duped."""
    out, i, n = [], 0, len(body)
    while i < n:
        if body[i] == '#' and i + 1 < n and body[i+1] == '[':
            depth, j = 0, i + 1
            while j < n:
                if body[j] == '[': depth += 1
                elif body[j] == ']':
                    depth -= 1
                    if depth == 0: break
                j += 1
            i = j + 1
            continue
        if body.startswith('//', i):
            j = body.find('\n', i)
            i = n if j < 0 else j
            continue
        out.append(body[i]); i += 1
    return ''.join(out)

def struct_bodies(root):
    bodies, event_cpi = {}, set()
    for path in pathlib.Path(root).rglob('*.rs'):
        text = path.read_text(errors='ignore')
        for m in re.finditer(r'#\[derive\(([^)]*)\)\]', text):
            if 'Accounts' not in m.group(1):
                continue
            sm = re.search(r'pub struct (\w+)\s*<', text[m.end():m.end() + 800])
            if not sm:
                continue
            name = sm.group(1)
            start = text.index('{', m.end() + sm.end() - 1)
            depth, i = 0, start
            while i < len(text):
                if text[i] == '{': depth += 1
                elif text[i] == '}':
                    depth -= 1
                    if depth == 0: break
                i += 1
            bodies[name] = strip_attrs(text[start + 1:i])
            # #[event_cpi] appends event_authority + program to this struct
            if 'event_cpi' in text[max(0, m.start() - 200):m.start()]:
                event_cpi.add(name)
    return bodies, event_cpi

def fields_of(body):
    """[(name, type)] at depth 0, de-duplicated by name (cfg twins)."""
    out, seen, depth, cur = [], set(), 0, ''
    for ch in body:
        if ch in '<({[': depth += 1
        elif ch in '>)}]': depth -= 1
        if ch == ',' and depth == 0:
            f = FIELD.match(cur.strip())
            if f and f.group(1) not in seen:
                seen.add(f.group(1)); out.append((f.group(1), f.group(2).strip()))
            cur = ''
        else:
            cur += ch
    f = FIELD.match(cur.strip())
    if f and f.group(1) not in seen:
        out.append((f.group(1), f.group(2).strip()))
    return out

def flatten(name, bodies, event_cpi, depth=0):
    """Anchor flattens nested Accounts structs into the account list, and
    #[event_cpi] appends event_authority + program to whichever struct
    carries it — including a nested one, landing mid-list."""
    if depth > 6 or name not in bodies:
        return []
    out = []
    for fname, ftype in fields_of(bodies[name]):
        base = re.sub(r"<.*", "", ftype).strip()
        if base in bodies:                       # composite -> inline its fields
            out.extend(f"{fname}.{s}" for s in flatten(base, bodies, event_cpi, depth + 1))
        else:
            out.append(fname)
    if name in event_cpi:
        out.extend(['event_authority', 'program'])
    return out

def anchor_structs(root):
    """-> {StructName: {'fields': [..], 'file': None}} with Anchor semantics.

    Anchor inlines a nested `#[derive(Accounts)]` composite into the account
    list, and `#[event_cpi]` appends `event_authority` + `program` to whichever
    struct carries it — including a nested one, where the pair lands mid-list.
    A naive field scan gets both wrong and reports phantom length mismatches.
    """
    bodies, event_cpi = struct_bodies(root)
    return {k: {'fields': flatten(k, bodies, event_cpi), 'file': None} for k in bodies}


# ---------------------------------------------------------------------------
# Rust: Shank indexed attributes
# ---------------------------------------------------------------------------

def _account_blocks(text):
    """Yield (end_offset, body) per `#[account(..)]`, quote- and nest-aware."""
    for m in re.finditer(r'#\[account\(', text):
        i, depth, in_str, esc = m.end(), 1, False, False
        while i < len(text) and depth:
            ch = text[i]
            if in_str:
                if esc:
                    esc = False
                elif ch == '\\':
                    esc = True
                elif ch == '"':
                    in_str = False
            elif ch == '"':
                in_str = True
            elif ch == '(':
                depth += 1
            elif ch == ')':
                depth -= 1
            i += 1
        yield i, text[m.end():i - 1]


def shank_instructions(path):
    """-> {VariantName: {'fields': [..], 'file': path}}.

    Shank carries an explicit index per account, which the caller should use as
    a parse check: indices must read 0..n-1 or the parse drifted (attributes
    wrap across lines and `desc` strings may contain parentheses).
    """
    text = pathlib.Path(path).read_text(errors='ignore')
    variants = [(m.start(1), m.group(1))
                for m in re.finditer(r'\n\s*([A-Z]\w*)\s*(?:\{|\(|=|,)', text)]
    out = {}
    for end, body in _account_blocks(text):
        idx = re.match(r'\s*(\d+)', body)
        name = re.search(r'name\s*=\s*"([^"]+)"', body)
        if not idx or not name:
            continue
        nxt = next((n for p, n in variants if p >= end), None)
        if nxt:
            out.setdefault(nxt, {'fields': [], 'idx': [], 'file': str(path)})
            out[nxt]['fields'].append(name.group(1))
            out[nxt]['idx'].append(int(idx.group(1)))
    return out


# ---------------------------------------------------------------------------
# Java: generated `*Keys` builders
# ---------------------------------------------------------------------------

def _split_top_level(body):
    out, depth, cur = [], 0, ''
    for ch in body:
        if ch == ',' and depth == 0:
            out.append(cur)
            cur = ''
            continue
        if ch in '([':
            depth += 1
        elif ch in ')]':
            depth -= 1
        cur += ch
    if cur.strip():
        out.append(cur)
    return out


def _account_of(entry):
    """The caller-supplied key, seeing through Anchor's absent-optional shapes."""
    e = entry.strip()
    m = re.match(r'([A-Za-z_]\w*)\s*==\s*null\s*\?', e)          # ternary sentinel
    if m:
        return m.group(1)
    m = re.search(r'requireNonNullElse\(\s*([A-Za-z_][\w.()]*)', e)
    if m:
        return m.group(1)
    m = re.search(r'create\w+\(\s*([A-Za-z_][\w.()]*)', e)
    return m.group(1) if m else None


def java_builders(path):
    text = pathlib.Path(path).read_text()
    out = {}
    for m in re.finditer(
            r'List<AccountMeta> (\w+)Keys\(.*?\)\s*\{\s*return List\.of\((.*?)\n\s*\);', text, re.S):
        accts = [_account_of(e) for e in _split_top_level(m.group(2))]
        out[m.group(1)] = [a for a in accts if a]
    return out


# ---------------------------------------------------------------------------
# Comparison
# ---------------------------------------------------------------------------

AUTOWIRED = {
    'systemprogram', 'tokenprogram', 'token2022program', 'rent', 'rentsysvar', 'sysvarrent',
    'clock', 'clocksysvar', 'instructionssysvar', 'sysvarinstructions', 'instructionsysvaraccount',
    'associatedtokenprogram', 'associatedtokenaccountprogram', 'memoprogram', 'memoprogramv2',
}


def _camel(sn):
    parts = [p for p in sn.split('_') if p]
    return parts[0] + ''.join(w[:1].upper() + w[1:] for w in parts[1:])


def _core(t):
    t = t.strip().rstrip(')')
    t = re.sub(r'Key$', '', t)
    t = re.sub(r'^solanaAccounts\.', '', t)
    t = re.sub(r'\(\)$', '', t)
    return re.sub(r'[^a-z0-9]', '', t.lower())


def compare(rust, java, strip_suffix=None):
    if strip_suffix:
        rust = {re.sub(strip_suffix + r'$', '', k): v for k, v in rust.items()}
    jl = {k.lower(): k for k in java}
    compared = matched = 0
    findings = []
    for struct, info in sorted(rust.items()):
        ix = struct[0].lower() + struct[1:]
        key = jl.get(ix.lower())
        if key is None:
            continue
        compared += 1
        exp = [_camel(f) for f in info['fields']]
        act = list(java[key])
        if len(exp) != len(act):
            findings.append((ix, 'LENGTH', exp, act, [], info.get('file')))
            continue
        bad = [(i, x, y) for i, (x, y) in enumerate(zip(exp, act))
               if _core(x) != _core(y) and not (_core(x) in AUTOWIRED and _core(y) in AUTOWIRED)]
        if bad:
            findings.append((ix, 'ORDER', exp, act, bad, info.get('file')))
        else:
            matched += 1
    return compared, matched, findings


def main():
    if len(sys.argv) < 4:
        sys.exit(__doc__)
    mode, src, java_path = sys.argv[1], sys.argv[2], sys.argv[3]
    strip = None
    drop_trailing = None
    for arg in sys.argv[4:]:
        if arg.startswith('--strip-suffix='):
            strip = arg.split('=', 1)[1]
        elif arg.startswith('--drop-trailing='):
            drop_trailing = arg.split('=', 1)[1]

    if mode == 'anchor':
        rust = anchor_structs(src)
    elif mode == 'shank':
        rust = shank_instructions(src)
        drifted = [k for k, v in rust.items() if v['idx'] != list(range(len(v['idx'])))]
        if drifted:
            sys.exit(f"shank parse drifted (indices not 0..n-1) for: {drifted[:5]}\n"
                     "The attribute scan lost accounts — fix the parser, do not trust this diff.")
    else:
        sys.exit(f"unknown mode {mode!r}; expected 'anchor' or 'shank'")

    java = java_builders(java_path)
    if drop_trailing:
        # Some published IDLs append an account the repo's Rust never declares —
        # Orca adds `whirlpool_program` to all 66 instructions. Verify against the
        # on-chain IDL first, then normalise it away so it does not swamp the diff.
        want = _core(drop_trailing)
        java = {k: (v[:-1] if v and _core(v[-1]) == want else v) for k, v in java.items()}
    compared, matched, findings = compare(rust, java, strip)

    print(f"rust structs {len(rust)}  java builders {len(java)}  "
          f"compared {compared}  match {matched}  differ {len(findings)}")
    if compared == 0:
        print("\n  NOTHING WAS COMPARED — this is a failure to match names, not a pass.\n"
              "  Check whether the program suffixes its structs (try --strip-suffix=Context).")
        return 1
    for ix, kind, exp, act, bad, f in findings:
        print(f"\n### {ix} [{kind}] rust={len(exp)} java={len(act)}")
        if f:
            print(f"    from {f}")
        if kind == 'LENGTH':
            for i in range(max(len(exp), len(act))):
                a = exp[i] if i < len(exp) else '—'
                b = act[i] if i < len(act) else '—'
                print(f"   [{i:2}] rust={a:32} java={b}" + ("  <<<" if _core(a) != _core(b) else ""))
        else:
            for i, x, y in bad:
                print(f"   [{i:2}] rust={x:32} java={y}")
    if findings:
        print("\nTriage before acting — see the traps in this file's docstring and "
              "docs/PROGRAM_VERIFICATION.md.")
    return 1 if findings else 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except BrokenPipeError:      # piped into head/less
        try:
            sys.stdout.close()
        finally:
            sys.exit(0)
