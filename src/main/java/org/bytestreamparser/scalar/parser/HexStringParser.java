package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HexFormat;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.util.Strings;

/** A parser for fixed length hexadecimal strings. */
public class HexStringParser extends DataParser<String> {
  private static final String ERROR_MESSAGE =
      "%s: value length must be less than or equal to %d, but was [%d]";
  private static final HexFormat HEX_FORMAT = HexFormat.of();
  private final int length;

  /**
   * Creates a new HexStringParser.
   *
   * @param id ID of the parser.
   * @param length Length of the hexadecimal string. Note it is the number of digits, not the number
   *     of bytes.
   */
  public HexStringParser(String id, int length) {
    super(id);
    this.length = length;
  }

  private static int toByteSize(int digits) {
    return (digits + 1) / 2;
  }

  /**
   * Packs the given value into the output stream.
   *
   * @param value the value to be packed. Note that odd length values will be left padded with '0'.
   * @param output the {@link OutputStream} to write the packed value.
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public void pack(String value, OutputStream output) throws IOException {
    check(value.length() <= length, ERROR_MESSAGE, getId(), length, value.length());
    String padded = Strings.padStart(value, toByteSize(length) * 2, '0');
    output.write(HEX_FORMAT.parseHex(padded));
  }

  /**
   * Parses the value from the input stream.
   *
   * @param input the {@link InputStream} to read the value from.
   * @return the parsed value.
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public String parse(InputStream input) throws IOException {
    String parsed = HEX_FORMAT.formatHex(readFully(input, toByteSize(length)));
    return parsed.substring(parsed.length() - length);
  }
}
