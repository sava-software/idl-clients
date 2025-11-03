module software.sava.idl.clients.kamino {
  exports software.sava.idl.clients.kamino.farms.gen.types;
  exports software.sava.idl.clients.kamino.farms.gen;
  exports software.sava.idl.clients.kamino.lend.gen.types;
  exports software.sava.idl.clients.kamino.lend.gen;
  exports software.sava.idl.clients.kamino.scope.gen.types;
  exports software.sava.idl.clients.kamino.scope.gen;
  exports software.sava.idl.clients.kamino.vaults.gen.types;
  exports software.sava.idl.clients.kamino.vaults.gen;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
