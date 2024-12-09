package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;
import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;

public class UnsignedShortParser<P extends Data<P>> extends NumberParser<P, Integer> {
  private static final String ERROR_MESSAGE = "%s: value must be between 0 and 65535, but was [%d]";

  public UnsignedShortParser(String id) {
    this(id, alwaysTrue());
  }

  public UnsignedShortParser(String id, Predicate<P> applicable) {
    super(id, applicable);
  }

  @Override
  public void pack(Integer value, OutputStream output) throws IOException {
    check(value <= 0xFFFF, ERROR_MESSAGE, getId(), value);
    check(value >= 0x0000, ERROR_MESSAGE, getId(), value);
    output.write(ByteBuffer.allocate(Short.BYTES).putShort(value.shortValue()).array());
  }

  @Override
  public Integer parse(InputStream input) throws IOException {
    byte[] bytes = readFully(input, 2);
    return (bytes[0] & 0xFF) << 8 | bytes[1] & 0xFF;
  }
}
