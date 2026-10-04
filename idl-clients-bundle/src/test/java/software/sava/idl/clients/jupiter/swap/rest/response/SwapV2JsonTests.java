package software.sava.idl.clients.jupiter.swap.rest.response;

import org.junit.jupiter.api.Test;
import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import systems.comodal.jsoniter.JsonIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static software.sava.idl.clients.jupiter.swap.rest.response.JupiterSwapV2TestFixtures.*;
import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

/// The shared Swap V2 readers. Each value is read as the `v` field of `{"v":<value>,"after":7}`,
/// and every successful read also asserts that `after` still reads 7: json-iterator's `readInt` and
/// `readLong` stop at a `.` or an `e`, and a reader that leaves part of its token behind misreads
/// every field after it.
final class SwapV2JsonTests {

  /// Elements 0 to 30 of a blockhash, so one more element makes 32.
  private static final String FIRST_31_BYTES =
      "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30";

  private static <T> T read(final String value, final Function<JsonIterator, T> reader) {
    final var json = "{\"v\":" + value + ",\"after\":7}";
    final var values = new ArrayList<T>(1);
    final int[] after = {-1};
    JsonIterator.parse(json.getBytes(UTF_8)).testObject((buf, offset, len, ji) -> {
      if (fieldEquals("v", buf, offset, len)) {
        values.add(reader.apply(ji));
      } else if (fieldEquals("after", buf, offset, len)) {
        after[0] = ji.readInt();
      } else {
        fail("unexpected field " + new String(buf, offset, len));
      }
      return true;
    });
    assertEquals(7, after[0], () -> "the reader left part of " + value + " unconsumed");
    assertEquals(1, values.size());
    return values.getFirst();
  }

  private static long u64(final String value) {
    return read(value, SwapV2Json::readU64);
  }

  private static long integral(final String value) {
    return read(value, SwapV2Json::readIntegral);
  }

  private static int integralInt(final String value) {
    return read(value, SwapV2Json::readIntegralInt);
  }

  private static long requiredIntegral(final String value) {
    return read(value, ji -> SwapV2Json.readRequiredIntegral(ji, "code"));
  }

  private static int requiredIntegralInt(final String value) {
    return read(value, ji -> SwapV2Json.readRequiredIntegralInt(ji, "code"));
  }

  private static byte[] blockhash(final String value) {
    return read(value, SwapV2Json::readBlockhash);
  }

  @Test
  void readU64AcceptsStringsAndBareIntegersOverTheFullRange() {
    assertEquals(0L, u64("\"0\""));
    assertEquals(0L, u64("0"));
    assertEquals(100_000_000L, u64("\"100000000\""));
    assertEquals(100_000_000L, u64("100000000"));
    assertEquals(-1L, u64("\"18446744073709551615\""));
    assertEquals(Long.MIN_VALUE, u64("9223372036854775808"));
    assertEquals(Long.MAX_VALUE, u64("\"9223372036854775807\""));
  }

  @Test
  void readU64ReadsNullAndEmptyAsZero() {
    assertEquals(0L, u64("null"));
    assertEquals(0L, u64("\"\""));
  }

  @Test
  void readU64RejectsFractionsSignsExponentsGarbageAndOverflow() {
    for (final var value : List.of("\"1.5\"", "\"-1\"", "1e3", "\"abc\"", "\"18446744073709551616\"", "-1", "1.5")) {
      assertThrows(NumberFormatException.class, () -> u64(value), value);
    }
  }

  @Test
  void readIntegralAcceptsEveryIntegralSpelling() {
    for (final var value : List.of("26", "26.0", "\"26\"", "2.6e1", "\"26.0\"")) {
      assertEquals(26L, integral(value), value);
    }
    assertEquals(0L, integral("null"));
    assertEquals(0L, integral("\"\""));
    assertEquals(Long.MAX_VALUE, integral("9223372036854775807"));
    assertEquals(-5L, integral("-5"));
    assertEquals(-5L, integral("\"-5\""));
  }

  @Test
  void readIntegralRejectsFractionsGarbageAndOverflow() {
    assertThrows(ArithmeticException.class, () -> integral("26.5"));
    assertThrows(ArithmeticException.class, () -> integral("9223372036854775808"));
    assertThrows(ArithmeticException.class, () -> integral("-9223372036854775809"));
    assertThrows(NumberFormatException.class, () -> integral("\"abc\""));
  }

  /// A regression pin for the guard `BigDecimal#longValueExact()` applies before scaling: neither
  /// value is ever expanded. There is deliberately no timing assertion.
  @Test
  void integralReadersRejectExponentBombsWithoutExpanding() {
    for (final var value : List.of("1e999999999", "1e-999999999")) {
      assertThrows(ArithmeticException.class, () -> integral(value), value);
      assertThrows(ArithmeticException.class, () -> integralInt(value), value);
      assertThrows(ArithmeticException.class, () -> requiredIntegral(value), value);
      assertThrows(ArithmeticException.class, () -> requiredIntegralInt(value), value);
    }
  }

  @Test
  void readIntegralIntNarrowsExactly() {
    assertEquals(50, integralInt("50.0"));
    assertEquals(50, integralInt("\"50\""));
    assertEquals(Integer.MAX_VALUE, integralInt("2147483647"));
    assertEquals(Integer.MIN_VALUE, integralInt("-2147483648"));
    assertThrows(ArithmeticException.class, () -> integralInt("2147483648"));
    assertThrows(ArithmeticException.class, () -> integralInt("-2147483649"));
    assertThrows(ArithmeticException.class, () -> integralInt("50.5"));
    assertEquals(0, integralInt("null"));
    assertEquals(0, integralInt("\"\""));
  }

  @Test
  void readOptionalKeyMapsNullAndEmptyToNull() {
    assertNull(read("null", SwapV2Json::readOptionalKey));
    assertNull(read("\"\"", SwapV2Json::readOptionalKey));
    assertEquals(TAKER_KEY, read("\"GkwFnmMDvn3HGMpJpWBg8tgJxr3NxNvg3AXxvXVPbRGJ\"", SwapV2Json::readOptionalKey));
  }

  @Test
  void readOptionalKeyRejectsAnInvalidKey() {
    assertThrows(IllegalArgumentException.class, () -> read("\"taker-address\"", SwapV2Json::readOptionalKey));
  }

  @Test
  void readBlockhashRequiresExactly32BytesInRange() {
    final byte[] expected = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, (byte) 255
    };
    assertArrayEquals(expected, blockhash("[" + FIRST_31_BYTES + ",255]"));
    // integral spellings of 7 read as 7 and leave the iterator aligned
    for (final var seven : List.of("\"7\"", "7.0", "7e0")) {
      final var json = "[" + FIRST_31_BYTES.replace(",7,", "," + seven + ",") + ",255]";
      assertArrayEquals(expected, blockhash(json), json);
    }

    final var tooShort = assertThrowsExactly(IllegalStateException.class, () -> blockhash("[" + FIRST_31_BYTES + "]"));
    assertTrue(tooShort.getMessage().contains("31"), tooShort.getMessage());
    assertThrowsExactly(IllegalStateException.class, () -> blockhash("[" + FIRST_31_BYTES + ",255,1]"));
    assertThrowsExactly(IllegalStateException.class, () -> blockhash("[]"));
    assertThrowsExactly(IllegalStateException.class, () -> blockhash("[" + FIRST_31_BYTES + ",256]"));
    assertThrowsExactly(IllegalStateException.class, () -> blockhash("[" + FIRST_31_BYTES + ",-1]"));
    assertThrowsExactly(ArithmeticException.class, () -> blockhash("[" + FIRST_31_BYTES + ",1.5]"));
    assertThrowsExactly(IllegalStateException.class, () -> blockhash("[" + FIRST_31_BYTES + ",null]"));
    assertThrowsExactly(IllegalStateException.class, () -> blockhash("[" + FIRST_31_BYTES + ",\"\"]"));
    assertNull(blockhash("null"));
  }

  @Test
  void readRequiredIntegralConsumesEveryIntegralSpelling() {
    for (final var value : List.of("-1000", "-1000.0", "\"-1000\"", "-1e3")) {
      assertEquals(-1000L, requiredIntegral(value), value);
      assertEquals(-1000, requiredIntegralInt(value), value);
    }
    assertEquals(0L, requiredIntegral("0"));
    assertEquals(9_223_372_036_854_775_807L, requiredIntegral("9223372036854775807"));
    assertEquals(Integer.MAX_VALUE, requiredIntegralInt("2147483647"));
    assertThrows(ArithmeticException.class, () -> requiredIntegralInt("2147483648"));
  }

  @Test
  void readRequiredIntegralRejectsAbsenceAndFractions() {
    for (final var absent : List.of("null", "\"\"")) {
      final var e = assertThrowsExactly(IllegalStateException.class, () -> requiredIntegral(absent), absent);
      assertEquals("code is required", e.getMessage());
      final var narrowed = assertThrowsExactly(IllegalStateException.class, () -> requiredIntegralInt(absent), absent);
      assertEquals("code is required", narrowed.getMessage());
    }
    final var named = assertThrowsExactly(IllegalStateException.class,
        () -> read("null", ji -> SwapV2Json.readRequiredIntegral(ji, "blockhash")));
    assertEquals("blockhash is required", named.getMessage());
    assertThrows(ArithmeticException.class, () -> requiredIntegral("1.5"));
    assertThrows(NumberFormatException.class, () -> requiredIntegral("\"abc\""));
    assertThrows(ArithmeticException.class, () -> requiredIntegral("1e999999999"));
  }

  @Test
  void readInstructionRequiresAnAccountsList() {
    final var noAccounts = assertThrowsExactly(IllegalStateException.class, () -> read(
        "{\"programId\":\"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4\",\"data\":\"AQ==\"}", SwapV2Json::readInstruction));
    assertEquals("instruction has no accounts list", noAccounts.getMessage());

    final var empty = read(
        "{\"programId\":\"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4\",\"accounts\":[],\"data\":\"AQ==\"}",
        SwapV2Json::readInstruction);
    assertEquals(JUP_KEY, empty.programId().publicKey());
    assertEquals(List.of(), empty.accounts());
    assertArrayEquals(new byte[]{1}, empty.copyData());

    // a JSON-null accounts list reads as empty and is accepted: only a missing key is rejected
    final var nullAccounts = read(
        "{\"programId\":\"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4\",\"accounts\":null,\"data\":\"AQ==\"}",
        SwapV2Json::readInstruction);
    assertEquals(JUP_KEY, nullAccounts.programId().publicKey());
    assertEquals(List.of(), nullAccounts.accounts());
    assertArrayEquals(new byte[]{1}, nullAccounts.copyData());

    final var withAccount = read("""
        {"programId":"TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
         "accounts":[{"pubkey":"HTvjzsfX3yU6BUodCjZ5vZkUrAxMDTrBs3CJaq43ashR","isSigner":false,"isWritable":true}],
         "data":"EQ=="}""", SwapV2Json::readInstruction);
    assertEquals(TOKEN_PROGRAM_KEY, withAccount.programId().publicKey());
    assertEquals(1, withAccount.accounts().size());
    assertEquals(WSOL_ATA_KEY, withAccount.accounts().getFirst().publicKey());
    assertTrue(withAccount.accounts().getFirst().write());
    assertFalse(withAccount.accounts().getFirst().signer());
    assertArrayEquals(new byte[]{17}, withAccount.copyData());

    assertThrows(NullPointerException.class, () -> read("{\"accounts\":[],\"data\":\"AQ==\"}", SwapV2Json::readInstruction));
    assertThrows(NullPointerException.class, () -> read(
        "{\"programId\":\"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4\",\"accounts\":[]}", SwapV2Json::readInstruction));

    // an instruction array that is JSON null reads as an empty list
    assertEquals(List.of(), read("null", SwapV2Json::readInstructions));
    final List<Instruction> two = read("""
        [{"programId":"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4","accounts":[],"data":"AQ=="},
         {"programId":"MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr","accounts":[],"data":"aGk="}]""",
        SwapV2Json::readInstructions);
    assertEquals(2, two.size());
    assertEquals(JUP_KEY, two.getFirst().programId().publicKey());
    assertEquals(MEMO_KEY, two.getLast().programId().publicKey());
    assertThrowsExactly(IllegalStateException.class, () -> read(
        "[{\"programId\":\"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4\",\"data\":\"AQ==\"}]", SwapV2Json::readInstructions));
  }

  @Test
  void readOptionalInstructionReadsNullAndNonObjectsAsAbsent() {
    for (final var absent : List.of("null", "\"x\"", "[1,2]", "5", "true", "\"\"")) {
      assertNull(read(absent, SwapV2Json::readOptionalInstruction), absent);
    }
    final var present = read(
        "{\"programId\":\"MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr\",\"accounts\":[],\"data\":\"aGk=\"}",
        SwapV2Json::readOptionalInstruction);
    assertEquals(MEMO_KEY, present.programId().publicKey());
    assertArrayEquals(new byte[]{'h', 'i'}, present.copyData());
    // an object is still read strictly
    assertThrowsExactly(IllegalStateException.class, () -> read(
        "{\"programId\":\"MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr\",\"data\":\"aGk=\"}",
        SwapV2Json::readOptionalInstruction));
  }

  /// The three tables are sent in an order a `HashMap` of these keys does not iterate in, so only
  /// an order-keeping map passes.
  @Test
  void readLookupTablesKeepsResponseOrderAndIsUnmodifiable() {
    final var tables = read("""
        {"675kPX9MHTjS2zt1qfr1NYHuzeLXfQM9H24wFSUt1Mp8":["3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3"],
         "3NAM1YJMhSPvtAkmGTRABe1hYZN3aE2hZHKy3JZy9fHk":[],
         "3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT":["3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3",
           "GGztQqQ6pCPaJQnNpXBgELr5cs3WwDakRbh1iEMzjgSJ"]}""", SwapV2Json::readLookupTables);
    assertEquals(List.of(AMM_B_KEY, VAULT_B_KEY, ALT_KEY), List.copyOf(tables.keySet()));
    assertEquals(List.of(VAULT_A_KEY), tables.get(AMM_B_KEY));
    assertEquals(List.of(), tables.get(VAULT_B_KEY));
    assertEquals(List.of(VAULT_A_KEY, TIP_KEY), tables.get(ALT_KEY));

    assertThrows(UnsupportedOperationException.class, () -> tables.put(MEMO_KEY, List.of()));
    assertThrows(UnsupportedOperationException.class, () -> tables.remove(ALT_KEY));
    assertThrows(UnsupportedOperationException.class, () -> tables.get(ALT_KEY).add(MEMO_KEY));

    assertNull(read("null", SwapV2Json::readLookupTables));
    assertNull(read("\"x\"", SwapV2Json::readLookupTables));
    assertNull(read("[]", SwapV2Json::readLookupTables));
    final Map<PublicKey, List<PublicKey>> none = read("{}", SwapV2Json::readLookupTables);
    assertEquals(Map.of(), none);
    assertThrows(UnsupportedOperationException.class, () -> none.put(MEMO_KEY, List.of()));
  }

  /// Only the tolerant readers read `""` as absent: a program id, an account key, a lookup table
  /// key and a lookup table address are decoded strictly, though Jupiter's spec types each as a
  /// `string`, so `""` there is an invalid key like any other.
  @Test
  void anEmptyKeyIsInvalidWhereAKeyIsRequired() {
    for (final var instruction : List.of(
        "{\"programId\":\"\",\"accounts\":[],\"data\":\"AQ==\"}",
        "{\"programId\":\"JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4\","
            + "\"accounts\":[{\"pubkey\":\"\",\"isSigner\":false,\"isWritable\":true}],\"data\":\"AQ==\"}")) {
      assertThrows(IllegalArgumentException.class, () -> read(instruction, SwapV2Json::readInstruction), instruction);
    }
    for (final var tables : List.of(
        "{\"\":[\"3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3\"]}",
        "{\"3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT\":[\"\"]}")) {
      assertThrows(IllegalArgumentException.class, () -> read(tables, SwapV2Json::readLookupTables), tables);
    }
  }

  /// A JSON-null address list reads as empty, as `readList` reads any JSON-null array, but a
  /// JSON-null address inside one throws NullPointerException from the list copy, not the
  /// IllegalArgumentException of an invalid key: json-iterator returns a JSON null as null without
  /// calling the key decoder.
  @Test
  void aJsonNullAddressListIsEmptyButAJsonNullAddressThrows() {
    assertEquals(Map.of(ALT_KEY, List.of()),
        read("{\"3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT\":null}", SwapV2Json::readLookupTables));
    for (final var tables : List.of(
        "{\"3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT\":[null]}",
        "{\"3S5e9qmNHjhA2G1Ghkk5UWnTniaFFHiX7gzd6gcZtzcT\":"
            + "[\"3JF3sEqM796hk5WFqA6EtmEwJQ9quALszsfJyvXNQKy3\",null]}")) {
      assertThrowsExactly(NullPointerException.class, () -> read(tables, SwapV2Json::readLookupTables), tables);
    }
  }
}
