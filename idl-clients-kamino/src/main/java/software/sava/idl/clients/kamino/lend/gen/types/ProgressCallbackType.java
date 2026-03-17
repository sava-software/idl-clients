package software.sava.idl.clients.kamino.lend.gen.types;

import software.sava.idl.clients.core.gen.RustEnum;
import software.sava.idl.clients.core.gen.SerDeUtil;

/// A callback to be notified when the ticket is being processed.
/// 
/// ## Why an enum?
/// 
/// Only reliable programs may be used for callbacks (since any error or panic returned from a CPI
/// aborts an entire transaction, which would stall the queue progress). Hence, we need a whitelist,
/// and the simplest initial implementation is a hardcoded enum. If we want to be able to add new
/// whitelist items without SC updates, we can implement such support using a special enum value
/// (e.g. `SPECIFIED_BY_PDA = 255`).
public enum ProgressCallbackType implements RustEnum {

  None,
  KlendQueueAccountingHandlerOnKvault;

  public static ProgressCallbackType read(final byte[] _data, final int _offset) {
    return SerDeUtil.read(1, ProgressCallbackType.values(), _data, _offset);
  }
}