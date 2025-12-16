module software.sava.idl.clients.marinade {
  exports software.sava.idl.clients.marinade.stake_pool.gen.events;
  exports software.sava.idl.clients.marinade.stake_pool.gen.types;
  exports software.sava.idl.clients.marinade.stake_pool.gen;
  exports software.sava.idl.clients.marinade.stake_pool;
  requires java.net.http;
  requires transitive software.sava.core;
  requires transitive software.sava.idl.clients.core;
  requires transitive software.sava.idl.clients.spl;
  requires transitive software.sava.rpc;
  requires transitive software.sava.solana_programs;
}
