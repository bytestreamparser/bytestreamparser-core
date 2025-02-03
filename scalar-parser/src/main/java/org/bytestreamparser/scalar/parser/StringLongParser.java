package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.scalar.util.Preconditions.check;

import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.util.Strings;

/** A {@link Long} implementation of {@link StringNumberParser<Long>}. */
public class StringLongParser extends StringNumberParser<Long> {
  private static final String INVALID_LENGTH = "%s: value must be of length %d, but was [%d]";
  private final int length;
  private final int radix;

  public StringLongParser(String id, DataParser<String> stringParser, int length, int radix) {
    super(id, stringParser);
    this.length = length;
    this.radix = radix;
  }

  @Override
  protected String fromNumber(Long value) {
    String padded = Strings.padStart(Long.toString(value, radix), length, '0');
    check(padded.length() == length, INVALID_LENGTH, getId(), length, padded.length());
    return padded;
  }

  @Override
  protected Long toNumber(String value) {
    return Long.valueOf(value, radix);
  }
}
