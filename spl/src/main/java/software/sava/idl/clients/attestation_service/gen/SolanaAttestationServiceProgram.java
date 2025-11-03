package software.sava.idl.clients.attestation_service.gen;

import java.lang.String;

import java.util.List;

import software.sava.core.accounts.PublicKey;
import software.sava.core.accounts.meta.AccountMeta;
import software.sava.core.borsh.Borsh;
import software.sava.core.programs.Discriminator;
import software.sava.core.tx.Instruction;

import static java.nio.charset.StandardCharsets.UTF_8;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.accounts.meta.AccountMeta.createRead;
import static software.sava.core.accounts.meta.AccountMeta.createReadOnlySigner;
import static software.sava.core.accounts.meta.AccountMeta.createWritableSigner;
import static software.sava.core.accounts.meta.AccountMeta.createWrite;
import static software.sava.core.encoding.ByteUtil.getInt16LE;
import static software.sava.core.encoding.ByteUtil.getInt32LE;
import static software.sava.core.encoding.ByteUtil.getInt64LE;
import static software.sava.core.encoding.ByteUtil.putInt16LE;
import static software.sava.core.encoding.ByteUtil.putInt64LE;
import static software.sava.core.programs.Discriminator.createAnchorDiscriminator;
import static software.sava.core.programs.Discriminator.toDiscriminator;

public final class SolanaAttestationServiceProgram {

  public static final Discriminator CREATE_CREDENTIAL_DISCRIMINATOR = toDiscriminator(205, 74, 60, 212, 63, 198, 196, 109);

  public static List<AccountMeta> createCredentialKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                       ,
                                                       final PublicKey payerKey,
                                                       final PublicKey credentialKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createWrite(credentialKey),
      createReadOnlySigner(authorityKey),
      createRead(systemProgramKey)
    );
  }

  public static Instruction createCredential(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                             final PublicKey payerKey,
                                             final PublicKey credentialKey,
                                             final PublicKey authorityKey,
                                             final PublicKey systemProgramKey,
                                             final String name,
                                             final PublicKey[] signers) {
    final var keys = createCredentialKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      credentialKey,
      authorityKey,
      systemProgramKey
    );
    return createCredential(invokedSolanaAttestationServiceProgramMeta, keys, name, signers);
  }

  public static Instruction createCredential(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                             ,
                                             final List<AccountMeta> keys,
                                             final String name,
                                             final PublicKey[] signers) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _data = new byte[12 + Borsh.lenVector(_name) + Borsh.lenVector(signers)];
    int i = CREATE_CREDENTIAL_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(_name, _data, i);
    Borsh.writeVector(signers, _data, i);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record CreateCredentialIxData(Discriminator discriminator, String name, byte[] _name, PublicKey[] signers) implements Borsh {  

    public static CreateCredentialIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateCredentialIxData createRecord(final Discriminator discriminator, final String name, final PublicKey[] signers) {
      return new CreateCredentialIxData(discriminator, name, name.getBytes(UTF_8), signers);
    }

    public static CreateCredentialIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var name = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var signers = Borsh.readPublicKeyVector(_data, i);
      return new CreateCredentialIxData(discriminator, name, name.getBytes(UTF_8), signers);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_name, _data, i);
      i += Borsh.writeVector(signers, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(_name) + Borsh.lenVector(signers);
    }
  }

  public static final Discriminator CREATE_SCHEMA_DISCRIMINATOR = toDiscriminator(105, 171, 40, 140, 30, 91, 30, 134);

  /// @param credentialKey Credential the Schema is associated with
  public static List<AccountMeta> createSchemaKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                   ,
                                                   final PublicKey payerKey,
                                                   final PublicKey authorityKey,
                                                   final PublicKey credentialKey,
                                                   final PublicKey schemaKey,
                                                   final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createWrite(schemaKey),
      createRead(systemProgramKey)
    );
  }

  /// @param credentialKey Credential the Schema is associated with
  public static Instruction createSchema(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                         final PublicKey payerKey,
                                         final PublicKey authorityKey,
                                         final PublicKey credentialKey,
                                         final PublicKey schemaKey,
                                         final PublicKey systemProgramKey,
                                         final String name,
                                         final String description,
                                         final byte[] layout,
                                         final String[] fieldNames) {
    final var keys = createSchemaKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      schemaKey,
      systemProgramKey
    );
    return createSchema(
      invokedSolanaAttestationServiceProgramMeta,
      keys,
      name,
      description,
      layout,
      fieldNames
    );
  }

  public static Instruction createSchema(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                         ,
                                         final List<AccountMeta> keys,
                                         final String name,
                                         final String description,
                                         final byte[] layout,
                                         final String[] fieldNames) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _description = description.getBytes(UTF_8);
    final byte[] _data = new byte[16 + Borsh.lenVector(_name) + Borsh.lenVector(_description) + Borsh.lenVector(layout) + Borsh.lenVector(fieldNames)];
    int i = CREATE_SCHEMA_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(_name, _data, i);
    i += Borsh.writeVector(_description, _data, i);
    i += Borsh.writeVector(layout, _data, i);
    Borsh.writeVector(fieldNames, _data, i);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record CreateSchemaIxData(Discriminator discriminator,
                                   String name, byte[] _name,
                                   String description, byte[] _description,
                                   byte[] layout,
                                   String[] fieldNames, byte[][] _fieldNames) implements Borsh {  

    public static CreateSchemaIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateSchemaIxData createRecord(final Discriminator discriminator,
                                                  final String name,
                                                  final String description,
                                                  final byte[] layout,
                                                  final String[] fieldNames) {
      return new CreateSchemaIxData(discriminator,
                                    name, name.getBytes(UTF_8),
                                    description, description.getBytes(UTF_8),
                                    layout,
                                    fieldNames, Borsh.getBytes(fieldNames));
    }

    public static CreateSchemaIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var name = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var description = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var layout = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(layout);
      final var fieldNames = Borsh.readStringVector(_data, i);
      return new CreateSchemaIxData(discriminator,
                                    name, name.getBytes(UTF_8),
                                    description, description.getBytes(UTF_8),
                                    layout,
                                    fieldNames, Borsh.getBytes(fieldNames));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_name, _data, i);
      i += Borsh.writeVector(_description, _data, i);
      i += Borsh.writeVector(layout, _data, i);
      i += Borsh.writeVector(fieldNames, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(_name) + Borsh.lenVector(_description) + Borsh.lenVector(layout) + Borsh.lenVector(fieldNames);
    }
  }

  public static final Discriminator CHANGE_SCHEMA_STATUS_DISCRIMINATOR = toDiscriminator(192, 74, 106, 154, 28, 210, 26, 87);

  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Credential the Schema is associated with
  public static List<AccountMeta> changeSchemaStatusKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                         ,
                                                         final PublicKey authorityKey,
                                                         final PublicKey credentialKey,
                                                         final PublicKey schemaKey) {
    return List.of(
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createWrite(schemaKey)
    );
  }

  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Credential the Schema is associated with
  public static Instruction changeSchemaStatus(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                               final PublicKey authorityKey,
                                               final PublicKey credentialKey,
                                               final PublicKey schemaKey,
                                               final boolean isPaused) {
    final var keys = changeSchemaStatusKeys(
      invokedSolanaAttestationServiceProgramMeta,
      authorityKey,
      credentialKey,
      schemaKey
    );
    return changeSchemaStatus(invokedSolanaAttestationServiceProgramMeta, keys, isPaused);
  }

  public static Instruction changeSchemaStatus(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                               ,
                                               final List<AccountMeta> keys,
                                               final boolean isPaused) {
    final byte[] _data = new byte[9];
    int i = CHANGE_SCHEMA_STATUS_DISCRIMINATOR.write(_data, 0);
    _data[i] = (byte) (isPaused ? 1 : 0);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record ChangeSchemaStatusIxData(Discriminator discriminator, boolean isPaused) implements Borsh {  

    public static ChangeSchemaStatusIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 9;

    public static ChangeSchemaStatusIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var isPaused = _data[i] == 1;
      return new ChangeSchemaStatusIxData(discriminator, isPaused);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      _data[i] = (byte) (isPaused ? 1 : 0);
      ++i;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CHANGE_AUTHORIZED_SIGNERS_DISCRIMINATOR = toDiscriminator(98, 184, 218, 252, 193, 76, 188, 159);

  /// @param credentialKey Credential the Schema is associated with
  public static List<AccountMeta> changeAuthorizedSignersKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                              ,
                                                              final PublicKey payerKey,
                                                              final PublicKey authorityKey,
                                                              final PublicKey credentialKey,
                                                              final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createWrite(credentialKey),
      createRead(systemProgramKey)
    );
  }

  /// @param credentialKey Credential the Schema is associated with
  public static Instruction changeAuthorizedSigners(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                                    final PublicKey payerKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey credentialKey,
                                                    final PublicKey systemProgramKey,
                                                    final PublicKey[] signers) {
    final var keys = changeAuthorizedSignersKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      systemProgramKey
    );
    return changeAuthorizedSigners(invokedSolanaAttestationServiceProgramMeta, keys, signers);
  }

  public static Instruction changeAuthorizedSigners(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                    ,
                                                    final List<AccountMeta> keys,
                                                    final PublicKey[] signers) {
    final byte[] _data = new byte[8 + Borsh.lenVector(signers)];
    int i = CHANGE_AUTHORIZED_SIGNERS_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(signers, _data, i);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record ChangeAuthorizedSignersIxData(Discriminator discriminator, PublicKey[] signers) implements Borsh {  

    public static ChangeAuthorizedSignersIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ChangeAuthorizedSignersIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var signers = Borsh.readPublicKeyVector(_data, i);
      return new ChangeAuthorizedSignersIxData(discriminator, signers);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(signers, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(signers);
    }
  }

  public static final Discriminator CHANGE_SCHEMA_DESCRIPTION_DISCRIMINATOR = toDiscriminator(23, 198, 58, 25, 69, 192, 93, 204);

  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Credential the Schema is associated with
  public static List<AccountMeta> changeSchemaDescriptionKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                              ,
                                                              final PublicKey payerKey,
                                                              final PublicKey authorityKey,
                                                              final PublicKey credentialKey,
                                                              final PublicKey schemaKey,
                                                              final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createWrite(schemaKey),
      createRead(systemProgramKey)
    );
  }

  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Credential the Schema is associated with
  public static Instruction changeSchemaDescription(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                                    final PublicKey payerKey,
                                                    final PublicKey authorityKey,
                                                    final PublicKey credentialKey,
                                                    final PublicKey schemaKey,
                                                    final PublicKey systemProgramKey,
                                                    final String description) {
    final var keys = changeSchemaDescriptionKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      schemaKey,
      systemProgramKey
    );
    return changeSchemaDescription(invokedSolanaAttestationServiceProgramMeta, keys, description);
  }

  public static Instruction changeSchemaDescription(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                    ,
                                                    final List<AccountMeta> keys,
                                                    final String description) {
    final byte[] _description = description.getBytes(UTF_8);
    final byte[] _data = new byte[12 + Borsh.lenVector(_description)];
    int i = CHANGE_SCHEMA_DESCRIPTION_DISCRIMINATOR.write(_data, 0);
    Borsh.writeVector(_description, _data, i);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record ChangeSchemaDescriptionIxData(Discriminator discriminator, String description, byte[] _description) implements Borsh {  

    public static ChangeSchemaDescriptionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ChangeSchemaDescriptionIxData createRecord(final Discriminator discriminator, final String description) {
      return new ChangeSchemaDescriptionIxData(discriminator, description, description.getBytes(UTF_8));
    }

    public static ChangeSchemaDescriptionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var description = Borsh.string(_data, i);
      return new ChangeSchemaDescriptionIxData(discriminator, description, description.getBytes(UTF_8));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(_description, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(_description);
    }
  }

  public static final Discriminator CHANGE_SCHEMA_VERSION_DISCRIMINATOR = toDiscriminator(151, 8, 113, 1, 168, 170, 69, 139);

  /// @param credentialKey Credential the Schema is associated with
  public static List<AccountMeta> changeSchemaVersionKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                          ,
                                                          final PublicKey payerKey,
                                                          final PublicKey authorityKey,
                                                          final PublicKey credentialKey,
                                                          final PublicKey existingSchemaKey,
                                                          final PublicKey newSchemaKey,
                                                          final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createRead(existingSchemaKey),
      createWrite(newSchemaKey),
      createRead(systemProgramKey)
    );
  }

  /// @param credentialKey Credential the Schema is associated with
  public static Instruction changeSchemaVersion(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                                final PublicKey payerKey,
                                                final PublicKey authorityKey,
                                                final PublicKey credentialKey,
                                                final PublicKey existingSchemaKey,
                                                final PublicKey newSchemaKey,
                                                final PublicKey systemProgramKey,
                                                final byte[] layout,
                                                final String[] fieldNames) {
    final var keys = changeSchemaVersionKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      existingSchemaKey,
      newSchemaKey,
      systemProgramKey
    );
    return changeSchemaVersion(invokedSolanaAttestationServiceProgramMeta, keys, layout, fieldNames);
  }

  public static Instruction changeSchemaVersion(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                ,
                                                final List<AccountMeta> keys,
                                                final byte[] layout,
                                                final String[] fieldNames) {
    final byte[] _data = new byte[8 + Borsh.lenVector(layout) + Borsh.lenVector(fieldNames)];
    int i = CHANGE_SCHEMA_VERSION_DISCRIMINATOR.write(_data, 0);
    i += Borsh.writeVector(layout, _data, i);
    Borsh.writeVector(fieldNames, _data, i);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record ChangeSchemaVersionIxData(Discriminator discriminator, byte[] layout, String[] fieldNames, byte[][] _fieldNames) implements Borsh {  

    public static ChangeSchemaVersionIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static ChangeSchemaVersionIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var layout = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(layout);
      final var fieldNames = Borsh.readStringVector(_data, i);
      return new ChangeSchemaVersionIxData(discriminator, layout, fieldNames, Borsh.getBytes(fieldNames));
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      i += Borsh.writeVector(layout, _data, i);
      i += Borsh.writeVector(fieldNames, _data, i);
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + Borsh.lenVector(layout) + Borsh.lenVector(fieldNames);
    }
  }

  public static final Discriminator CREATE_ATTESTATION_DISCRIMINATOR = toDiscriminator(49, 24, 67, 80, 12, 249, 96, 239);

  /// @param authorityKey Authorized signer of the Schema's Credential
  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Schema the Attestation is associated with
  public static List<AccountMeta> createAttestationKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                        ,
                                                        final PublicKey payerKey,
                                                        final PublicKey authorityKey,
                                                        final PublicKey credentialKey,
                                                        final PublicKey schemaKey,
                                                        final PublicKey attestationKey,
                                                        final PublicKey systemProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createRead(schemaKey),
      createWrite(attestationKey),
      createRead(systemProgramKey)
    );
  }

  /// @param authorityKey Authorized signer of the Schema's Credential
  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Schema the Attestation is associated with
  public static Instruction createAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                              final PublicKey payerKey,
                                              final PublicKey authorityKey,
                                              final PublicKey credentialKey,
                                              final PublicKey schemaKey,
                                              final PublicKey attestationKey,
                                              final PublicKey systemProgramKey,
                                              final PublicKey nonce,
                                              final byte[] data,
                                              final long expiry) {
    final var keys = createAttestationKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      schemaKey,
      attestationKey,
      systemProgramKey
    );
    return createAttestation(
      invokedSolanaAttestationServiceProgramMeta,
      keys,
      nonce,
      data,
      expiry
    );
  }

  public static Instruction createAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                              ,
                                              final List<AccountMeta> keys,
                                              final PublicKey nonce,
                                              final byte[] data,
                                              final long expiry) {
    final byte[] _data = new byte[48 + Borsh.lenVector(data)];
    int i = CREATE_ATTESTATION_DISCRIMINATOR.write(_data, 0);
    nonce.write(_data, i);
    i += 32;
    i += Borsh.writeVector(data, _data, i);
    putInt64LE(_data, i, expiry);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record CreateAttestationIxData(Discriminator discriminator,
                                        PublicKey nonce,
                                        byte[] data,
                                        long expiry) implements Borsh {  

    public static CreateAttestationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateAttestationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var nonce = readPubKey(_data, i);
      i += 32;
      final var data = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(data);
      final var expiry = getInt64LE(_data, i);
      return new CreateAttestationIxData(discriminator, nonce, data, expiry);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      nonce.write(_data, i);
      i += 32;
      i += Borsh.writeVector(data, _data, i);
      putInt64LE(_data, i, expiry);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 32 + Borsh.lenVector(data) + 8;
    }
  }

  public static final Discriminator CLOSE_ATTESTATION_DISCRIMINATOR = toDiscriminator(249, 84, 133, 23, 48, 175, 252, 221);

  /// @param authorityKey Authorized signer of the Schema's Credential
  public static List<AccountMeta> closeAttestationKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                       ,
                                                       final PublicKey payerKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey credentialKey,
                                                       final PublicKey attestationKey,
                                                       final PublicKey eventAuthorityKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey attestationProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createWrite(attestationKey),
      createRead(eventAuthorityKey),
      createRead(systemProgramKey),
      createRead(attestationProgramKey)
    );
  }

  /// @param authorityKey Authorized signer of the Schema's Credential
  public static Instruction closeAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                             final PublicKey payerKey,
                                             final PublicKey authorityKey,
                                             final PublicKey credentialKey,
                                             final PublicKey attestationKey,
                                             final PublicKey eventAuthorityKey,
                                             final PublicKey systemProgramKey,
                                             final PublicKey attestationProgramKey) {
    final var keys = closeAttestationKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      attestationKey,
      eventAuthorityKey,
      systemProgramKey,
      attestationProgramKey
    );
    return closeAttestation(invokedSolanaAttestationServiceProgramMeta, keys);
  }

  public static Instruction closeAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                             ,
                                             final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, CLOSE_ATTESTATION_DISCRIMINATOR);
  }

  public static final Discriminator TOKENIZE_SCHEMA_DISCRIMINATOR = toDiscriminator(126, 227, 119, 227, 60, 162, 243, 251);

  /// @param credentialKey Credential the Schema is associated with
  /// @param mintKey Mint of Schema Token
  /// @param sasPdaKey Program derived address used as program signer authority
  public static List<AccountMeta> tokenizeSchemaKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                     ,
                                                     final PublicKey payerKey,
                                                     final PublicKey authorityKey,
                                                     final PublicKey credentialKey,
                                                     final PublicKey schemaKey,
                                                     final PublicKey mintKey,
                                                     final PublicKey sasPdaKey,
                                                     final PublicKey systemProgramKey,
                                                     final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createRead(schemaKey),
      createWrite(mintKey),
      createRead(sasPdaKey),
      createRead(systemProgramKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param credentialKey Credential the Schema is associated with
  /// @param mintKey Mint of Schema Token
  /// @param sasPdaKey Program derived address used as program signer authority
  public static Instruction tokenizeSchema(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                           final PublicKey payerKey,
                                           final PublicKey authorityKey,
                                           final PublicKey credentialKey,
                                           final PublicKey schemaKey,
                                           final PublicKey mintKey,
                                           final PublicKey sasPdaKey,
                                           final PublicKey systemProgramKey,
                                           final PublicKey tokenProgramKey,
                                           final long maxSize) {
    final var keys = tokenizeSchemaKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      schemaKey,
      mintKey,
      sasPdaKey,
      systemProgramKey,
      tokenProgramKey
    );
    return tokenizeSchema(invokedSolanaAttestationServiceProgramMeta, keys, maxSize);
  }

  public static Instruction tokenizeSchema(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                           ,
                                           final List<AccountMeta> keys,
                                           final long maxSize) {
    final byte[] _data = new byte[16];
    int i = TOKENIZE_SCHEMA_DISCRIMINATOR.write(_data, 0);
    putInt64LE(_data, i, maxSize);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record TokenizeSchemaIxData(Discriminator discriminator, long maxSize) implements Borsh {  

    public static TokenizeSchemaIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static final int BYTES = 16;

    public static TokenizeSchemaIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var maxSize = getInt64LE(_data, i);
      return new TokenizeSchemaIxData(discriminator, maxSize);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      putInt64LE(_data, i, maxSize);
      i += 8;
      return i - _offset;
    }

    @Override
    public int l() {
      return BYTES;
    }
  }

  public static final Discriminator CREATE_TOKENIZED_ATTESTATION_DISCRIMINATOR = toDiscriminator(122, 136, 219, 186, 101, 126, 20, 154);

  /// @param authorityKey Authorized signer of the Schema's Credential
  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Schema the Attestation is associated with
  /// @param schemaMintKey Mint of Schema Token
  /// @param attestationMintKey Mint of Attestation Token
  /// @param sasPdaKey Program derived address used as program signer authority
  /// @param recipientTokenAccountKey Associated token account of Recipient for Attestation Token
  /// @param recipientKey Wallet to receive Attestation Token
  public static List<AccountMeta> createTokenizedAttestationKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                                 ,
                                                                 final PublicKey payerKey,
                                                                 final PublicKey authorityKey,
                                                                 final PublicKey credentialKey,
                                                                 final PublicKey schemaKey,
                                                                 final PublicKey attestationKey,
                                                                 final PublicKey systemProgramKey,
                                                                 final PublicKey schemaMintKey,
                                                                 final PublicKey attestationMintKey,
                                                                 final PublicKey sasPdaKey,
                                                                 final PublicKey recipientTokenAccountKey,
                                                                 final PublicKey recipientKey,
                                                                 final PublicKey tokenProgramKey,
                                                                 final PublicKey associatedTokenProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createRead(schemaKey),
      createWrite(attestationKey),
      createRead(systemProgramKey),
      createWrite(schemaMintKey),
      createWrite(attestationMintKey),
      createRead(sasPdaKey),
      createWrite(recipientTokenAccountKey),
      createRead(recipientKey),
      createRead(tokenProgramKey),
      createRead(associatedTokenProgramKey)
    );
  }

  /// @param authorityKey Authorized signer of the Schema's Credential
  /// @param credentialKey Credential the Schema is associated with
  /// @param schemaKey Schema the Attestation is associated with
  /// @param schemaMintKey Mint of Schema Token
  /// @param attestationMintKey Mint of Attestation Token
  /// @param sasPdaKey Program derived address used as program signer authority
  /// @param recipientTokenAccountKey Associated token account of Recipient for Attestation Token
  /// @param recipientKey Wallet to receive Attestation Token
  public static Instruction createTokenizedAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                                       final PublicKey payerKey,
                                                       final PublicKey authorityKey,
                                                       final PublicKey credentialKey,
                                                       final PublicKey schemaKey,
                                                       final PublicKey attestationKey,
                                                       final PublicKey systemProgramKey,
                                                       final PublicKey schemaMintKey,
                                                       final PublicKey attestationMintKey,
                                                       final PublicKey sasPdaKey,
                                                       final PublicKey recipientTokenAccountKey,
                                                       final PublicKey recipientKey,
                                                       final PublicKey tokenProgramKey,
                                                       final PublicKey associatedTokenProgramKey,
                                                       final PublicKey nonce,
                                                       final byte[] data,
                                                       final long expiry,
                                                       final String name,
                                                       final String uri,
                                                       final String symbol,
                                                       final int mintAccountSpace) {
    final var keys = createTokenizedAttestationKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      schemaKey,
      attestationKey,
      systemProgramKey,
      schemaMintKey,
      attestationMintKey,
      sasPdaKey,
      recipientTokenAccountKey,
      recipientKey,
      tokenProgramKey,
      associatedTokenProgramKey
    );
    return createTokenizedAttestation(
      invokedSolanaAttestationServiceProgramMeta,
      keys,
      nonce,
      data,
      expiry,
      name,
      uri,
      symbol,
      mintAccountSpace
    );
  }

  public static Instruction createTokenizedAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                       ,
                                                       final List<AccountMeta> keys,
                                                       final PublicKey nonce,
                                                       final byte[] data,
                                                       final long expiry,
                                                       final String name,
                                                       final String uri,
                                                       final String symbol,
                                                       final int mintAccountSpace) {
    final byte[] _name = name.getBytes(UTF_8);
    final byte[] _uri = uri.getBytes(UTF_8);
    final byte[] _symbol = symbol.getBytes(UTF_8);
    final byte[] _data = new byte[62 + Borsh.lenVector(data) + Borsh.lenVector(_name) + Borsh.lenVector(_uri) + Borsh.lenVector(_symbol)];
    int i = CREATE_TOKENIZED_ATTESTATION_DISCRIMINATOR.write(_data, 0);
    nonce.write(_data, i);
    i += 32;
    i += Borsh.writeVector(data, _data, i);
    putInt64LE(_data, i, expiry);
    i += 8;
    i += Borsh.writeVector(_name, _data, i);
    i += Borsh.writeVector(_uri, _data, i);
    i += Borsh.writeVector(_symbol, _data, i);
    putInt16LE(_data, i, mintAccountSpace);

    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, _data);
  }

  public record CreateTokenizedAttestationIxData(Discriminator discriminator,
                                                 PublicKey nonce,
                                                 byte[] data,
                                                 long expiry,
                                                 String name, byte[] _name,
                                                 String uri, byte[] _uri,
                                                 String symbol, byte[] _symbol,
                                                 int mintAccountSpace) implements Borsh {  

    public static CreateTokenizedAttestationIxData read(final Instruction instruction) {
      return read(instruction.data(), instruction.offset());
    }

    public static CreateTokenizedAttestationIxData createRecord(final Discriminator discriminator,
                                                                final PublicKey nonce,
                                                                final byte[] data,
                                                                final long expiry,
                                                                final String name,
                                                                final String uri,
                                                                final String symbol,
                                                                final int mintAccountSpace) {
      return new CreateTokenizedAttestationIxData(discriminator,
                                                  nonce,
                                                  data,
                                                  expiry,
                                                  name, name.getBytes(UTF_8),
                                                  uri, uri.getBytes(UTF_8),
                                                  symbol, symbol.getBytes(UTF_8),
                                                  mintAccountSpace);
    }

    public static CreateTokenizedAttestationIxData read(final byte[] _data, final int _offset) {
      if (_data == null || _data.length == 0) {
        return null;
      }
      final var discriminator = createAnchorDiscriminator(_data, _offset);
      int i = _offset + discriminator.length();
      final var nonce = readPubKey(_data, i);
      i += 32;
      final var data = Borsh.readbyteVector(_data, i);
      i += Borsh.lenVector(data);
      final var expiry = getInt64LE(_data, i);
      i += 8;
      final var name = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var uri = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var symbol = Borsh.string(_data, i);
      i += (Integer.BYTES + getInt32LE(_data, i));
      final var mintAccountSpace = getInt16LE(_data, i);
      return new CreateTokenizedAttestationIxData(discriminator,
                                                  nonce,
                                                  data,
                                                  expiry,
                                                  name, name.getBytes(UTF_8),
                                                  uri, uri.getBytes(UTF_8),
                                                  symbol, symbol.getBytes(UTF_8),
                                                  mintAccountSpace);
    }

    @Override
    public int write(final byte[] _data, final int _offset) {
      int i = _offset + discriminator.write(_data, _offset);
      nonce.write(_data, i);
      i += 32;
      i += Borsh.writeVector(data, _data, i);
      putInt64LE(_data, i, expiry);
      i += 8;
      i += Borsh.writeVector(_name, _data, i);
      i += Borsh.writeVector(_uri, _data, i);
      i += Borsh.writeVector(_symbol, _data, i);
      putInt16LE(_data, i, mintAccountSpace);
      i += 2;
      return i - _offset;
    }

    @Override
    public int l() {
      return 8 + 32
           + Borsh.lenVector(data)
           + 8
           + Borsh.lenVector(_name)
           + Borsh.lenVector(_uri)
           + Borsh.lenVector(_symbol)
           + 2;
    }
  }

  public static final Discriminator CLOSE_TOKENIZED_ATTESTATION_DISCRIMINATOR = toDiscriminator(12, 88, 1, 126, 90, 2, 191, 48);

  /// @param authorityKey Authorized signer of the Schema's Credential
  /// @param attestationMintKey Mint of Attestation Token
  /// @param sasPdaKey Program derived address used as program signer authority
  /// @param attestationTokenAccountKey Associated token account of the related Attestation Token
  public static List<AccountMeta> closeTokenizedAttestationKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                                ,
                                                                final PublicKey payerKey,
                                                                final PublicKey authorityKey,
                                                                final PublicKey credentialKey,
                                                                final PublicKey attestationKey,
                                                                final PublicKey eventAuthorityKey,
                                                                final PublicKey systemProgramKey,
                                                                final PublicKey attestationProgramKey,
                                                                final PublicKey attestationMintKey,
                                                                final PublicKey sasPdaKey,
                                                                final PublicKey attestationTokenAccountKey,
                                                                final PublicKey tokenProgramKey) {
    return List.of(
      createWritableSigner(payerKey),
      createReadOnlySigner(authorityKey),
      createRead(credentialKey),
      createWrite(attestationKey),
      createRead(eventAuthorityKey),
      createRead(systemProgramKey),
      createRead(attestationProgramKey),
      createWrite(attestationMintKey),
      createRead(sasPdaKey),
      createWrite(attestationTokenAccountKey),
      createRead(tokenProgramKey)
    );
  }

  /// @param authorityKey Authorized signer of the Schema's Credential
  /// @param attestationMintKey Mint of Attestation Token
  /// @param sasPdaKey Program derived address used as program signer authority
  /// @param attestationTokenAccountKey Associated token account of the related Attestation Token
  public static Instruction closeTokenizedAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta,
                                                      final PublicKey payerKey,
                                                      final PublicKey authorityKey,
                                                      final PublicKey credentialKey,
                                                      final PublicKey attestationKey,
                                                      final PublicKey eventAuthorityKey,
                                                      final PublicKey systemProgramKey,
                                                      final PublicKey attestationProgramKey,
                                                      final PublicKey attestationMintKey,
                                                      final PublicKey sasPdaKey,
                                                      final PublicKey attestationTokenAccountKey,
                                                      final PublicKey tokenProgramKey) {
    final var keys = closeTokenizedAttestationKeys(
      invokedSolanaAttestationServiceProgramMeta,
      payerKey,
      authorityKey,
      credentialKey,
      attestationKey,
      eventAuthorityKey,
      systemProgramKey,
      attestationProgramKey,
      attestationMintKey,
      sasPdaKey,
      attestationTokenAccountKey,
      tokenProgramKey
    );
    return closeTokenizedAttestation(invokedSolanaAttestationServiceProgramMeta, keys);
  }

  public static Instruction closeTokenizedAttestation(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                      ,
                                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, CLOSE_TOKENIZED_ATTESTATION_DISCRIMINATOR);
  }

  public static final Discriminator EMIT_EVENT_DISCRIMINATOR = toDiscriminator(82, 133, 188, 136, 167, 139, 209, 52);

  public static List<AccountMeta> emitEventKeys(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                                ,
                                                final PublicKey eventAuthorityKey) {
    return List.of(
      createReadOnlySigner(eventAuthorityKey)
    );
  }

  public static Instruction emitEvent(final AccountMeta invokedSolanaAttestationServiceProgramMeta, final PublicKey eventAuthorityKey) {     final var keys = emitEventKeys(
      invokedSolanaAttestationServiceProgramMeta,
      eventAuthorityKey
    );
    return emitEvent(invokedSolanaAttestationServiceProgramMeta, keys);
  }

  public static Instruction emitEvent(final AccountMeta invokedSolanaAttestationServiceProgramMeta                                      ,
                                      final List<AccountMeta> keys) {
    return Instruction.createInstruction(invokedSolanaAttestationServiceProgramMeta, keys, EMIT_EVENT_DISCRIMINATOR);
  }

  private SolanaAttestationServiceProgram() {
  }
}
