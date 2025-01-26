package org.bytestreamparser.api.testing.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;

public class TestParser extends DataParser<byte[]> {

  public TestParser(String id) {
    super(id);
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
