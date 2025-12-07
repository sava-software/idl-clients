package software.sava.idl.clients.metaplex.token.metadata.gen.types;

import software.sava.core.accounts.PublicKey;
import software.sava.core.borsh.RustEnum;

import static software.sava.core.accounts.PublicKey.readPubKey;
import static software.sava.core.encoding.ByteUtil.getInt64LE;

public sealed interface PayloadType extends RustEnum permits
  PayloadType.Pubkey,
  PayloadType.Seeds,
  PayloadType.MerkleProof,
  PayloadType.Number {

  static PayloadType read(final byte[] _data, final int _offset) {
    final int ordinal = _data[_offset] & 0xFF;
    final int i = _offset + 1;
    return switch (ordinal) {
      case 0 -> Pubkey.read(_data, i);
      case 1 -> Seeds.read(_data, i);
      case 2 -> MerkleProof.read(_data, i);
      case 3 -> Number.read(_data, i);
      default -> throw new IllegalStateException(java.lang.String.format(
          "Unexpected ordinal [%d] for enum [PayloadType]", ordinal
      ));
    };
  }

  record Pubkey(PublicKey val) implements EnumPublicKey, PayloadType {

    public static Pubkey read(final byte[] _data, int i) {
      return new Pubkey(readPubKey(_data, i));
    }

    @Override
    public int ordinal() {
      return 0;
    }
  }

  record Seeds(SeedsVec val) implements BorshEnum, PayloadType {

    public static Seeds read(final byte[] _data, final int _offset) {
      return new Seeds(SeedsVec.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 1;
    }
  }

  record MerkleProof(ProofInfo val) implements BorshEnum, PayloadType {

    public static MerkleProof read(final byte[] _data, final int _offset) {
      return new MerkleProof(ProofInfo.read(_data, _offset));
    }

    @Override
    public int ordinal() {
      return 2;
    }
  }

  record Number(long val) implements EnumInt64, PayloadType {

    public static Number read(final byte[] _data, int i) {
      return new Number(getInt64LE(_data, i));
    }

    @Override
    public int ordinal() {
      return 3;
    }
  }
}
