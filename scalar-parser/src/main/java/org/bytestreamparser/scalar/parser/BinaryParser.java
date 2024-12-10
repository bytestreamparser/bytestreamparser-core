package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;
import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;

public class BinaryParser<P extends Data<P>> extends DataParser<P, byte[]> {
  private static final String ERROR_MESSAGE = "%s: value must be of length %d, but was [%d]";
  private final int length;

  public BinaryParser(String id, int length) {
    this(id, alwaysTrue(), length);
  }

  public BinaryParser(String id, Predicate<P> applicable, int length) {
    super(id, applicable);
    this.length = length;
  }

  @Override
  public void pack(byte[] value, OutputStream output) throws IOException {
    check(value.length == length, ERROR_MESSAGE, getId(), length, value.length);
    output.write(value);
  }

  @Override
  public byte[] parse(InputStream input) throws IOException {
    return readFully(input, length);
  }
}
