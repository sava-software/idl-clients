module software.sava.idl.clients.neutral_trade {
  exports software.sava.idl.clients.nt.bundle.gen.events;
  exports software.sava.idl.clients.nt.bundle.gen.types;
  exports software.sava.idl.clients.nt.bundle.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
