module software.sava.idl.clients.orca {
  exports software.sava.idl.clients.orca.quote;
  exports software.sava.idl.clients.orca.whirlpools.gen.events;
  exports software.sava.idl.clients.orca.whirlpools.gen.types;
  exports software.sava.idl.clients.orca.whirlpools.gen;
  exports software.sava.idl.clients.orca;
  requires java.net.http;
  requires software.sava.idl.clients.spl;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
