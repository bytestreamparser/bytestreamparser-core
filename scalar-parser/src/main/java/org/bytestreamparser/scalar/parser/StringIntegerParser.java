package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.util.Strings;

public class StringIntegerParser extends StringNumberParser<Integer> {
  private static final String INVALID_LENGTH = "%s: value must be of length %d, but was [%d]";
  private final int length;
  private final int radix;

  public StringIntegerParser(String id, DataParser<String> stringParser, int length, int radix) {
    super(id, stringParser);
    this.length = length;
    this.radix = radix;
  }

  @Override
  protected String fromNumber(Integer value) {
    String padded = Strings.padStart(Integer.toString(value, radix), length, '0');
    check(padded.length() == length, INVALID_LENGTH, getId(), length, padded.length());
    return padded;
  }

  @Override
  protected Integer toNumber(String value) {
    return Integer.valueOf(value.strip(), radix);
  }
}
