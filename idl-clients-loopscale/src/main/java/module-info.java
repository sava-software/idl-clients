module software.sava.idl.clients.loopscale {
  exports software.sava.idl.clients.loopscale.gen.events;
  exports software.sava.idl.clients.loopscale.gen.types;
  exports software.sava.idl.clients.loopscale.gen;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
}
