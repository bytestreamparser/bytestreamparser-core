package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

/**
 * A parser for fixed length BCD strings.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Binary-coded_decimal">Binary Coded Decimal</a>
 */
public class BcdStringParser extends HexStringParser {
  private static final Pattern BCD_STRING = Pattern.compile("^\\d+$");
  private static final String ERROR_MESSAGE = "%s: Invalid BCD String [%s]";

  /**
   * Creates a new BcdStringParser.
   *
   * @param id ID of the parser.
   * @param length Length of the BCD string. Note it is the number of digits, not the number of
   *     bytes.
   */
  public BcdStringParser(String id, int length) {
    super(id, length);
  }

  @Override
  public void pack(String value, OutputStream output) throws IOException {
    check(BCD_STRING.matcher(value).matches(), ERROR_MESSAGE, getId(), value);
    super.pack(value, output);
  }

  @Override
  public String parse(InputStream input) throws IOException {
    String parsed = super.parse(input);
    check(BCD_STRING.matcher(parsed).matches(), ERROR_MESSAGE, getId(), parsed);
    return parsed;
  }
}
