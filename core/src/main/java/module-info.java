module software.sava.idl.clients.core {
  exports software.sava.idl.clients.core.gen;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
