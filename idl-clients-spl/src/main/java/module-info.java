module software.sava.idl.clients.spl {
  exports software.sava.idl.clients.associated_token.gen;
  exports software.sava.idl.clients.attestation_service.gen.types;
  exports software.sava.idl.clients.attestation_service.gen;
  exports software.sava.idl.clients.stake.gen.types;
  exports software.sava.idl.clients.stake.gen;
  exports software.sava.idl.clients.token.gen.types;
  exports software.sava.idl.clients.token.gen;
  requires java.net.http;
  requires org.bouncycastle.provider;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.rpc;
  requires transitive systems.comodal.json_iterator;
}
