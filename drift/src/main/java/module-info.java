module software.sava.idl.clients.software.sava.idl.clientsdrift {
  exports software.sava.idl.clients.drift.gen.types;
  exports software.sava.idl.clients.drift.gen;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
