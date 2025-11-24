module software.sava.idl.clients.kamino {
  exports software.sava.idl.clients.kamino.farms.gen.types;
  exports software.sava.idl.clients.kamino.farms.gen;
  exports software.sava.idl.clients.kamino.farms;
  exports software.sava.idl.clients.kamino.lend.gen.types;
  exports software.sava.idl.clients.kamino.lend.gen;
  exports software.sava.idl.clients.kamino.lend;
  exports software.sava.idl.clients.kamino.scope.entries;
  exports software.sava.idl.clients.kamino.scope.gen.types;
  exports software.sava.idl.clients.kamino.scope.gen;
  exports software.sava.idl.clients.kamino.scope;
  exports software.sava.idl.clients.kamino.vaults.gen.types;
  exports software.sava.idl.clients.kamino.vaults.gen;
  exports software.sava.idl.clients.kamino.vaults;
  exports software.sava.idl.clients.kamino;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.idl.clients.spl;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
