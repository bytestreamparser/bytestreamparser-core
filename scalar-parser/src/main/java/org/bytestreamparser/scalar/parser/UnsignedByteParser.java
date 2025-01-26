package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UnsignedByteParser extends org.bytestreamparser.api.parser.DataParser<Integer> {
  private static final String ERROR_MESSAGE = "%s: value must be between 0 and 255, but was [%d]";

  public UnsignedByteParser(String id) {
    super(id);
  }

  @Override
  public void pack(Integer value, OutputStream output) throws IOException {
    check(value <= 0xFF, ERROR_MESSAGE, getId(), value);
    check(value >= 0x00, ERROR_MESSAGE, getId(), value);
    output.write(value.byteValue());
  }

  @Override
  public Integer parse(InputStream input) throws IOException {
    return readFully(input, 1)[0] & 0xFF;
  }
}
