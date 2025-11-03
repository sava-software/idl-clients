module software.sava.idl.clients.oracles {
  exports software.sava.idl.clients.pyth.lazer.gen.types;
  exports software.sava.idl.clients.pyth.lazer.gen;
  exports software.sava.idl.clients.pyth.push.gen.types;
  exports software.sava.idl.clients.pyth.push.gen;
  exports software.sava.idl.clients.pyth.receiver.gen.types;
  exports software.sava.idl.clients.pyth.receiver.gen;
  exports software.sava.idl.clients.switchboard.on_demand.gen.types;
  exports software.sava.idl.clients.switchboard.on_demand.gen;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
