package software.sava.idl.clients.jupiter.swap.rest.response;

import software.sava.core.accounts.PublicKey;
import software.sava.core.tx.Instruction;
import systems.comodal.jsoniter.CharBufferFunction;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.jsoniter.ValueType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static software.sava.rpc.json.PublicKeyEncoding.PARSE_BASE58_PUBLIC_KEY;

/// Readers shared by the Swap V2 parsers; each consumes the whole JSON value it reads.
///
/// [#readU64], [#readIntegral] and [#readIntegralInt] read JSON null and `""` as 0, and
/// [#readOptionalKey(JsonIterator)] reads them as null, exactly as if the field were absent;
/// [#readRequiredIntegral] and [#readRequiredIntegralInt] throw on both. An instruction's
/// `programId`, each account `pubkey`, and every lookup-table key and address are decoded
/// strictly even though Jupiter's spec types them as `string`: `""` there throws
/// IllegalArgumentException from `PublicKey.fromBase58Encoded`, like any other invalid key.
/// Garbage still throws. A strict integral reader is used only for values that are integral by
/// protocol; every other JSON number is read as a `BigDecimal` and kept as sent.
final class SwapV2Json {

  private static final int BLOCKHASH_LENGTH = 32;

  private static final CharBufferFunction<PublicKey> OPTIONAL_BASE58_KEY =
      (buf, offset, len) -> len == 0 ? null : PublicKey.fromBase58Encoded(buf, offset, len);

  /// A u64 from a JSON string or bare integer, returned as the unsigned bit pattern; JSON null and `""` read as 0, like an absent field; a minus sign, a fraction, an exponent, garbage or a value above 2^64-1 throws NumberFormatException.
  static long readU64(final JsonIterator ji) {
    final var s = ji.readNumberOrNumberString();
    return s == null || s.isEmpty() ? 0 : Long.parseUnsignedLong(s);
  }

  /// An integer in any integral spelling (`26`, `26.0`, `"26"`, `2.6e1`); JSON null and `""` read as 0; a fraction or a value outside long throws ArithmeticException, garbage throws NumberFormatException. Only for values integral by protocol.
  ///
  /// `BigDecimal#longValueExact()` rejects an exponent such as `1e999999999` or `1e-999999999`
  /// before scaling, so neither is ever expanded.
  static long readIntegral(final JsonIterator ji) {
    final var s = ji.readNumberOrNumberString();
    return s == null || s.isEmpty() ? 0 : new BigDecimal(s).longValueExact();
  }

  /// [#readIntegral] narrowed exactly to int.
  static int readIntegralInt(final JsonIterator ji) {
    return Math.toIntExact(readIntegral(ji));
  }

  /// An integer in any integral spelling (`-1000`, `-1000.0`, `"-1000"`, `-1e3`) that must be present: JSON null or `""` throws IllegalStateException naming `field`; a fraction or a value outside long throws ArithmeticException; garbage throws NumberFormatException. Consumes the whole token, so the next field still reads.
  static long readRequiredIntegral(final JsonIterator ji, final String field) {
    final var s = ji.readNumberOrNumberString();
    if (s == null || s.isEmpty()) {
      throw new IllegalStateException(field + " is required");
    }
    return new BigDecimal(s).longValueExact();
  }

  /// [#readRequiredIntegral] narrowed exactly to int.
  static int readRequiredIntegralInt(final JsonIterator ji, final String field) {
    return Math.toIntExact(readRequiredIntegral(ji, field));
  }

  /// A base58 key; JSON null and `""` read as null; an invalid key throws.
  static PublicKey readOptionalKey(final JsonIterator ji) {
    return ji.applyChars(OPTIONAL_BASE58_KEY);
  }

  /// One instruction through `JupiterSwapInstructions.parseInstruction`, rejecting one with no `accounts` key.
  ///
  /// A JSON-null `accounts` is not rejected: `readList` reads it as an empty list. Either way the
  /// result's `accounts()` is never null. A missing `programId`, `data` or account `pubkey` fails
  /// inside sava-core with a NullPointerException. A `""` programId or pubkey throws
  /// IllegalArgumentException.
  static Instruction readInstruction(final JsonIterator ji) {
    final var ix = JupiterSwapInstructions.parseInstruction(ji);
    if (ix.accounts() == null) {
      throw new IllegalStateException("instruction has no accounts list");
    }
    return ix;
  }

  /// [#readInstruction] for a nullable slot: JSON null or a non-object reads as null.
  static Instruction readOptionalInstruction(final JsonIterator ji) {
    return ji.readOrNull(ValueType.OBJECT, SwapV2Json::readInstruction);
  }

  /// An instruction array; JSON null reads as an empty list.
  static List<Instruction> readInstructions(final JsonIterator ji) {
    return ji.readList(SwapV2Json::readInstruction);
  }

  /// `addressesByLookupTableAddress` as an unmodifiable `LinkedHashMap` in response order; JSON null or a non-object reads as null.
  ///
  /// Each address list is unmodifiable; a repeated table key keeps its last list, and a JSON-null
  /// list reads as empty. A `""` or invalid table key or address throws IllegalArgumentException; a
  /// JSON-null address throws NullPointerException.
  static Map<PublicKey, List<PublicKey>> readLookupTables(final JsonIterator ji) {
    return ji.readOrNull(ValueType.OBJECT, SwapV2Json::readLookupTableObject);
  }

  private static Map<PublicKey, List<PublicKey>> readLookupTableObject(final JsonIterator ji) {
    final var tables = ji.readMap(
        new LinkedHashMap<PublicKey, List<PublicKey>>(),
        PARSE_BASE58_PUBLIC_KEY,
        (table, j) -> List.copyOf(JupiterSwapInstructions.parseKeys(j))
    );
    return Collections.unmodifiableMap(tables);
  }

  /// A 32-element blockhash array; JSON null reads as null.
  ///
  /// Each element may be spelled `7`, `"7"`, `7.0` or `7e0`; a fraction throws
  /// ArithmeticException.
  ///
  /// @throws IllegalStateException for another length, an element outside 0..255, or an element that is JSON null or `""`
  static byte[] readBlockhash(final JsonIterator ji) {
    if (ji.readNull()) {
      return null;
    }
    final byte[] blockhash = new byte[BLOCKHASH_LENGTH];
    int i = 0;
    while (ji.readArray()) {
      if (i == BLOCKHASH_LENGTH) {
        throw new IllegalStateException("blockhash has more than " + BLOCKHASH_LENGTH + " elements");
      }
      final int b = readRequiredIntegralInt(ji, "blockhash");
      if (b < 0 || b > 255) {
        throw new IllegalStateException("blockhash element outside 0..255: " + b);
      }
      blockhash[i++] = (byte) b;
    }
    if (i != BLOCKHASH_LENGTH) {
      throw new IllegalStateException("blockhash must have " + BLOCKHASH_LENGTH + " elements, had " + i);
    }
    return blockhash;
  }

  private SwapV2Json() {
  }
}
