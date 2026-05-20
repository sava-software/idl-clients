module software.sava.idl.clients.orca {
  exports software.sava.idl.clients.orca;
  exports software.sava.idl.clients.orca.quote;
  exports software.sava.idl.clients.orca.whirlpools.gen.events;
  exports software.sava.idl.clients.orca.whirlpools.gen.types;
  exports software.sava.idl.clients.orca.whirlpools.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires software.sava.idl.clients.spl;
  requires transitive software.sava.rpc;
}
