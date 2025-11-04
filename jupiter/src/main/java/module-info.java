module software.sava.idl.clients.jupiter {
  exports software.sava.idl.clients.jupiter.governance.gen.types;
  exports software.sava.idl.clients.jupiter.governance.gen;
  exports software.sava.idl.clients.jupiter.merkle_distributor.gen.types;
  exports software.sava.idl.clients.jupiter.merkle_distributor.gen;
  exports software.sava.idl.clients.jupiter.swap.gen.types;
  exports software.sava.idl.clients.jupiter.swap.gen;
  exports software.sava.idl.clients.jupiter.voter.gen.types;
  exports software.sava.idl.clients.jupiter.voter.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
