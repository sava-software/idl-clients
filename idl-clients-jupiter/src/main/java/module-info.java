module software.sava.idl.clients.jupiter {
  exports software.sava.idl.clients.jupiter.governance.gen.types;
  exports software.sava.idl.clients.jupiter.governance.gen;
  exports software.sava.idl.clients.jupiter.lend_borrow.gen.types;
  exports software.sava.idl.clients.jupiter.lend_borrow.gen;
  exports software.sava.idl.clients.jupiter.merkle_distributor.gen.types;
  exports software.sava.idl.clients.jupiter.merkle_distributor.gen;
  exports software.sava.idl.clients.jupiter.order_engine.gen;
  exports software.sava.idl.clients.jupiter.swap.gen.types;
  exports software.sava.idl.clients.jupiter.swap.gen;
  exports software.sava.idl.clients.jupiter.swap.rest.request;
  exports software.sava.idl.clients.jupiter.swap.rest.response;
  exports software.sava.idl.clients.jupiter.swap.rest;
  exports software.sava.idl.clients.jupiter.voter.gen.types;
  exports software.sava.idl.clients.jupiter.voter.gen;
  exports software.sava.idl.clients.jupiter.voter.rest.response;
  exports software.sava.idl.clients.jupiter.voter;
  exports software.sava.idl.clients.jupiter;
  requires java.net.http;
  requires transitive software.sava.solana_web2;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.idl.clients.spl;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
