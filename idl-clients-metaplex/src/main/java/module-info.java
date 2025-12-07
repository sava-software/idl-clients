module software.sava.idl.clients.metaplex {
  exports software.sava.idl.clients.metaplex.token.metadata.gen.types;
  exports software.sava.idl.clients.metaplex.token.metadata.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
