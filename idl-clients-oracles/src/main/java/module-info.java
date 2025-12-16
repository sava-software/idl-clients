module software.sava.idl.clients.oracles {
  exports software.sava.idl.clients.oracles.pyth.lazer.gen.types;
  exports software.sava.idl.clients.oracles.pyth.lazer.gen;
  exports software.sava.idl.clients.oracles.pyth.push.gen.types;
  exports software.sava.idl.clients.oracles.pyth.push.gen;
  exports software.sava.idl.clients.oracles.pyth.receiver.gen.types;
  exports software.sava.idl.clients.oracles.pyth.receiver.gen;
  exports software.sava.idl.clients.oracles.switchboard.on_demand.gen.events;
  exports software.sava.idl.clients.oracles.switchboard.on_demand.gen.types;
  exports software.sava.idl.clients.oracles.switchboard.on_demand.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
