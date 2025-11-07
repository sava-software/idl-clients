module software.sava.idl.clients.drift {
  exports software.sava.idl.clients.drift.gen.types;
  exports software.sava.idl.clients.drift.gen;
  exports software.sava.idl.clients.drift;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive software.sava.solana_programs;
  requires transitive systems.comodal.json_iterator;
}
