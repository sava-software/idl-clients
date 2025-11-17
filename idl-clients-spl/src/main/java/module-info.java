module software.sava.idl.clients.spl {
  exports software.sava.idl.clients.spl.associated_token.gen;
  exports software.sava.idl.clients.spl.attestation_service.gen.types;
  exports software.sava.idl.clients.spl.attestation_service.gen;
  exports software.sava.idl.clients.spl.system.gen.types;
  exports software.sava.idl.clients.spl.system.gen;
  exports software.sava.idl.clients.spl.token.gen.types;
  exports software.sava.idl.clients.spl.token.gen;
  exports software.sava.idl.clients.spl;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
