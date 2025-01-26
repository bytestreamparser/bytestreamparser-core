package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

public class BcdStringParser extends HexStringParser {
  private static final Pattern BCD_STRING = Pattern.compile("^\\d+$");
  private static final String ERROR_MESSAGE = "%s: Invalid BCD String [%s]";

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
