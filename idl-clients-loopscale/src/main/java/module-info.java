module software.sava.idl.clients.loopscale {
  requires java.net.http;

  exports software.sava.idl.clients.loopscale.gen.events;
  exports software.sava.idl.clients.loopscale.gen.types;
  exports software.sava.idl.clients.loopscale.gen;
  exports software.sava.idl.clients.loopscale;

  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.idl.clients.spl;
  requires transitive software.sava.rpc;
}
