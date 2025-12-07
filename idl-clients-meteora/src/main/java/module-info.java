module software.sava.idl.clients.meteora {
  exports software.sava.idl.clients.meteora.dlmm.gen.types;
  exports software.sava.idl.clients.meteora.dlmm.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
