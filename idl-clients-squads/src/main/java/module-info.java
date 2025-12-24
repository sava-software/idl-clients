module software.sava.idl.clients.squads {
  exports software.sava.idl.clients.squads.v4.gen.types;
  exports software.sava.idl.clients.squads.v4.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
