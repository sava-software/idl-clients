package software.sava.idl.clients.core.gen;

public interface Factory<T> {

  T read(final byte[] data, final int offset);
}
