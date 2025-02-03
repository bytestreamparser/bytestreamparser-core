package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;

/** A parser for fixed length binary data. */
public class BinaryParser extends DataParser<byte[]> {
  private static final String ERROR_MESSAGE = "%s: value must be of length %d, but was [%d]";
  private final int length;

  /**
   * Creates a new BinaryParser.
   *
   * @param id ID of the parser.
   * @param length Length of the binary data, i.e., the number of bytes.
   */
  public BinaryParser(String id, int length) {
    super(id);
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
