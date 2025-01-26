package org.bytestreamparser.api.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class DataParser<V> {
  private final String id;

  protected DataParser(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public abstract void pack(V value, OutputStream output) throws IOException;

  public abstract V parse(InputStream input) throws IOException;
}
