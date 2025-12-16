module software.sava.idl.clients.cctp {
  exports software.sava.idl.clients.cctp.message_transmitter.v2.gen.events;
  exports software.sava.idl.clients.cctp.message_transmitter.v2.gen.types;
  exports software.sava.idl.clients.cctp.message_transmitter.v2.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
