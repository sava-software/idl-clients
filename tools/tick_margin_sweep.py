"""Exhaustive divergence sweep for OrcaUtil.sqrtPriceX64ToTickIndex:681's
lower-error-margin mutants (NakedReceiver dropping the subtract, and the
BigInteger subtract->add sibling).

Analysis (see session notes): both mutants change only tickLow, and with
frac(x) < 0.01 the fast-path collapse tickLow' == tickHigh == floor(x) is the
only reachable divergence -- it returns floor(x) where the original's
refinement would return floor(x) - 1. That happens iff the 14-bit log
approximation OVERSHOOTS: for some price p with true tick k-1 (p < boundary(k)),
x(p) >= k. Because x(p) is (weakly) monotone in p, overshoot at any boundary k
is equivalent to x(boundary(k) - 1) >= k. One evaluation per tick boundary is
therefore an exhaustive search; monotonicity is spot-checked along the way.
"""

MIN_TICK = -443_636
MAX_TICK = 443_636
U128 = (1 << 128) - 1

POS_BASE_EVEN = 79228162514264337593543950336
POS_BASE_ODD = 79232123823359799118286999567
POS_FACTORS = [
    79236085330515764027303304731,
    79244008939048815603706035061,
    79259858533276714757314932305,
    79291567232598584799939703904,
    79355022692464371645785046466,
    79482085999252804386437311141,
    79736823300114093921829183326,
    80248749790819932309965073892,
    81282483887344747381513967011,
    83390072131320151908154831281,
    87770609709833776024991924138,
    97234110755111693312479820773,
    119332217159966728226237229890,
    179736315981702064433883588727,
    407748233172238350107850275304,
    2098478828474011932436660412517,
    55581415166113811149459800483533,
    38992368544603139932233054999993551,
]
NEG_BASE_EVEN = 18446744073709551616
NEG_BASE_ODD = 18445821805675392311
NEG_FACTORS = [
    18444899583751176498,
    18443055278223354162,
    18439367220385604838,
    18431993317065449817,
    18417254355718160513,
    18387811781193591352,
    18329067761203520168,
    18212142134806087854,
    17980523815641551639,
    17526086738831147013,
    16651378430235024244,
    15030750278693429944,
    12247334978882834399,
    8131365268884726200,
    3584323654723342297,
    696457651847595233,
    26294789957452057,
    37481735321082,
]

LOG_B_2_X32 = 59543866431248
LOWER = 184467440737095516
UPPER = 15793534762490258745
BIT_PRECISION = 14


def sqrt_price(tick):
    if tick >= 0:
        ratio = POS_BASE_ODD if tick & 1 else POS_BASE_EVEN
        for i, f in enumerate(POS_FACTORS):
            if tick & (2 << i):
                ratio = (ratio * f >> 96) & U128
        return ratio >> 32
    a = -tick
    ratio = NEG_BASE_ODD if a & 1 else NEG_BASE_EVEN
    for i, f in enumerate(NEG_FACTORS):
        if a & (2 << i):
            ratio = (ratio * f >> 64) & U128
    return ratio


def logbp_x64(p):
    msb = p.bit_length() - 1
    int_x32 = (msb - 64) << 32
    r = p >> (msb - 63) if msb >= 64 else p << (63 - msb)
    bit = 1 << 63
    frac = 0
    precision = 0
    while bit > 0 and precision < BIT_PRECISION:
        r = r * r
        is2 = (r >> 127) & 1
        r >>= 63 + is2
        if is2:
            frac += bit
        bit >>= 1
        precision += 1
    return (int_x32 + (frac >> 32)) * LOG_B_2_X32


def full_variants(p):
    x = logbp_x64(p)
    tl_orig = (x - LOWER) >> 64
    tl_naked = x >> 64
    tl_add = (x + LOWER) >> 64
    th = (x + UPPER) >> 64
    outs = []
    for tl in (tl_orig, tl_naked, tl_add):
        if tl == th:
            outs.append(tl)
        else:
            outs.append(th if sqrt_price(th) <= p else tl)
    return outs


def main():
    overshoots = []
    non_monotonic = []
    prev_x = None
    for k in range(MIN_TICK + 1, MAX_TICK + 1):
        b = sqrt_price(k)
        x = logbp_x64(b - 1)
        if x >= (k << 64):
            o, n, a = full_variants(b - 1)
            overshoots.append((k, b - 1, x / 2**64, o, n, a))
        if prev_x is not None and x < prev_x:
            non_monotonic.append(k)
        prev_x = x
    print("boundaries checked:", MAX_TICK - MIN_TICK)
    print("overshoots (divergence candidates):", len(overshoots))
    for k, p, xf, o, n, a in overshoots[:10]:
        print(f"  k={k} p={p} x={xf:.6f} original={o} naked={n} add={a} diverges={o != n or o != a}")
    print("monotonicity violations at boundary-1 samples:", len(non_monotonic), non_monotonic[:5])


main()
