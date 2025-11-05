package software.sava.idl.clients.drift.gen.types;

import software.sava.core.borsh.Borsh;

public enum OrderTriggerCondition implements Borsh.Enum {

  Above,
  Below,
  TriggeredAbove,
  TriggeredBelow;

  public static OrderTriggerCondition read(final byte[] _data, final int _offset) {
    return Borsh.read(OrderTriggerCondition.values(), _data, _offset);
  }
}