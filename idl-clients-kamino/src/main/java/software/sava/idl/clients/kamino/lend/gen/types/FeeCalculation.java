package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.core.borsh.Borsh;

/// Calculate fees exlusive or inclusive of an amount
public enum FeeCalculation implements Borsh.Enum {

  Exclusive,
  Inclusive;

  public static FeeCalculation read(final byte[] _data, final int _offset) {
    return Borsh.read(FeeCalculation.values(), _data, _offset);
  }
}