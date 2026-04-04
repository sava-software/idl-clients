package software.sava.idl.clients.spl.token_2022;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;
import software.sava.idl.clients.core.gen.SerDe;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.putInt32LE;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class Token2022Instructions {

  public static final Discriminator UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR = toDiscriminator(221, 233, 49, 45, 181, 202, 220, 200);

  public enum TokenMetadataField {

    Name,
    Symbol,
    Uri,
    Key;

    private static final TokenMetadataField[] VALUES = values();
  }

  public static List<AccountMeta> updateTokenMetadataFieldKeys(final PublicKey metadataKey,
                                                               final PublicKey updateAuthorityKey) {
    return List.of(
        createWrite(metadataKey),
        createReadOnlySigner(updateAuthorityKey)
    );
  }

  /// Updates a field in a token-metadata account.
  ///
  /// The field can be one of the required fields (name, symbol, URI), or a
  /// totally new field denoted by a "key" string.
  ///
  /// @param field Field to update in the metadata.
  /// @param value Value to write for the field.
  public static Instruction updateTokenMetadataField(final AccountMeta invokedToken2022ProgramMeta,
                                                     final PublicKey metadataKey,
                                                     final PublicKey updateAuthorityKey,
                                                     final TokenMetadataField field,
                                                     final String key,
                                                     final String value) {
    final var keys = updateTokenMetadataFieldKeys(
        metadataKey,
        updateAuthorityKey
    );
    return updateTokenMetadataField(
        invokedToken2022ProgramMeta,
        keys,
        field,
        key,
        value
    );
  }

  /// Updates a field in a token-metadata account.
  ///
  /// The field can be one of the required fields (name, symbol, URI), or a
  /// totally new field denoted by a "key" string.
  ///
  /// @param field Field to update in the metadata.
  /// @param value Value to write for the field.
  public static Instruction updateTokenMetadataField(final AccountMeta invokedToken2022ProgramMeta,
                                                     final List<AccountMeta> keys,
                                                     final TokenMetadataField field,
                                                     final String key,
                                                     final String value) {
    final byte[] _value = value.getBytes(UTF_8);
    final byte[] _key;
    final int fieldSize;
    if (field == TokenMetadataField.Key) {
      _key = key.getBytes(UTF_8);
      fieldSize = 1 + Integer.BYTES + _key.length;
    } else {
      _key = null;
      fieldSize = 1;
    }
    final byte[] _data = new byte[8 + fieldSize + Integer.BYTES + _value.length];
    int i = UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) field.ordinal();
    ++i;
    if (_key != null) {
      putInt32LE(_data, i, _key.length);
      i += Integer.BYTES;
      System.arraycopy(_key, 0, _data, i, _key.length);
      i += _key.length;
    }
    putInt32LE(_data, i, _value.length);
    i += Integer.BYTES;
    System.arraycopy(_value, 0, _data, i, _value.length);

    return Instruction.createInstruction(invokedToken2022ProgramMeta, keys, _data);
  }

  public static Instruction updateTokenMetadataField(final AccountMeta invokedToken2022ProgramMeta,
                                                     final PublicKey metadataKey,
                                                     final PublicKey updateAuthorityKey,
                                                     final TokenMetadataField field,
                                                     final String value) {
    final var keys = updateTokenMetadataFieldKeys(
        metadataKey,
        updateAuthorityKey
    );
    return updateTokenMetadataField(invokedToken2022ProgramMeta, keys, field, null, value);
  }

  public static Instruction updateTokenMetadataName(final AccountMeta invokedToken2022ProgramMeta,
                                                    final PublicKey metadataKey,
                                                    final PublicKey updateAuthorityKey,
                                                    final String value) {
    return updateTokenMetadataField(invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, TokenMetadataField.Name, value);
  }

  public static Instruction updateTokenMetadataSymbol(final AccountMeta invokedToken2022ProgramMeta,
                                                      final PublicKey metadataKey,
                                                      final PublicKey updateAuthorityKey,
                                                      final String value) {
    return updateTokenMetadataField(invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, TokenMetadataField.Symbol, value);
  }

  public static Instruction updateTokenMetadataUri(final AccountMeta invokedToken2022ProgramMeta,
                                                   final PublicKey metadataKey,
                                                   final PublicKey updateAuthorityKey,
                                                   final String value) {
    return updateTokenMetadataField(invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, TokenMetadataField.Uri, value);
  }

  public static Instruction updateTokenMetadataCustomField(final AccountMeta invokedToken2022ProgramMeta,
                                                           final PublicKey metadataKey,
                                                           final PublicKey updateAuthorityKey,
                                                           final String key,
                                                           final String value) {
    return updateTokenMetadataField(invokedToken2022ProgramMeta, metadataKey, updateAuthorityKey, TokenMetadataField.Key, key, value);
  }

  public record UpdateTokenMetadataFieldIxData(byte[] discriminator,
                                               TokenMetadataField field,
                                               String key, byte[] _key,
                                               String value, byte[] _value) implements SerDe {

    public static UpdateTokenMetadataFieldIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static UpdateTokenMetadataFieldIxData read(final byte[] _data, final int offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = new byte[8];
      System.arraycopy(_data, offset, discriminator, 0, 8);
      int i = offset + 8;
      final int fieldOrdinal = _data[i] & 0xFF;
      final var field = fieldOrdinal < TokenMetadataField.VALUES.length
          ? TokenMetadataField.VALUES[fieldOrdinal]
          : null;
      ++i;
      final String key;
      final byte[] _key;
      if (field == TokenMetadataField.Key) {
        final int keyLen = getInt32LE(_data, i);
        i += Integer.BYTES;
        _key = new byte[keyLen];
        System.arraycopy(_data, i, _key, 0, keyLen);
        key = new String(_key, UTF_8);
        i += keyLen;
      } else {
        key = null;
        _key = null;
      }
      final int valueLen = getInt32LE(_data, i);
      i += Integer.BYTES;
      final byte[] _value = new byte[valueLen];
      System.arraycopy(_data, i, _value, 0, valueLen);
      final var value = new String(_value, UTF_8);
      return new UpdateTokenMetadataFieldIxData(discriminator, field, key, _key, value, _value);
    }

    @Override
    public int write(final byte[] _data, final int offset) {
      int i = offset + UPDATE_TOKEN_METADATA_FIELD_DISCRIMINATOR.write(_data, offset);
      _data[i] = (byte) field.ordinal();
      ++i;
      if (_key != null) {
        putInt32LE(_data, i, _key.length);
        i += Integer.BYTES;
        System.arraycopy(_key, 0, _data, i, _key.length);
        i += _key.length;
      }
      putInt32LE(_data, i, _value.length);
      i += Integer.BYTES;
      System.arraycopy(_value, 0, _data, i, _value.length);
      i += _value.length;
      return i - offset;
    }

    @Override
    public int l() {
      int len = 8 + 1 + Integer.BYTES + _value.length;
      if (_key != null) {
        len += Integer.BYTES + _key.length;
      }
      return len;
    }
  }

  private Token2022Instructions() {
  }
}
