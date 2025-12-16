module software.sava.idl.clients.drift {
  exports software.sava.idl.clients.drift.gen.events;
  exports software.sava.idl.clients.drift.gen.types;
  exports software.sava.idl.clients.drift.gen;
  exports software.sava.idl.clients.drift.merkle.distributor.gen.events;
  exports software.sava.idl.clients.drift.merkle.distributor.gen.types;
  exports software.sava.idl.clients.drift.merkle.distributor.gen;
  exports software.sava.idl.clients.drift.vaults.gen.events;
  exports software.sava.idl.clients.drift.vaults.gen.types;
  exports software.sava.idl.clients.drift.vaults.gen;
  exports software.sava.idl.clients.drift.vaults;
  exports software.sava.idl.clients.drift;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.idl.clients.spl;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
