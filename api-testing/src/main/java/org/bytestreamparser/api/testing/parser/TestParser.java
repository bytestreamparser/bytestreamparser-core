package org.bytestreamparser.api.testing.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.api.util.Predicates;

public class TestParser<P extends Data<P>> extends DataParser<P, byte[]> {
  public TestParser(String id) {
    this(id, Predicates.alwaysTrue());
  }

  public TestParser(String id, Predicate<P> applicable) {
    super(id, applicable);
  }

  @Override
  public void pack(byte[] value, OutputStream output) throws IOException {
    output.write(value);
  }

  @Override
  public byte[] parse(InputStream input) throws IOException {
    return input.readAllBytes();
  }
}
