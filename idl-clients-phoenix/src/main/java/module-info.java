module software.sava.idl.clients.phoenix {
  exports software.sava.idl.clients.phoenix.dev.perpetuals.gen.types;
  exports software.sava.idl.clients.phoenix.dev.perpetuals.gen;
  exports software.sava.idl.clients.phoenix.ember.gen.types;
  exports software.sava.idl.clients.phoenix.ember.gen;
  exports software.sava.idl.clients.phoenix.perpetuals.gen.types;
  exports software.sava.idl.clients.phoenix.perpetuals.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
