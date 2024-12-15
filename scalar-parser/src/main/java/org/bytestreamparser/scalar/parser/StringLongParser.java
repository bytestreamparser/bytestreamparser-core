package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.scalar.util.Strings;

public class StringLongParser<P extends Data<P>> extends StringNumberParser<P, Long> {
  private static final String INVALID_LENGTH = "%s: value must be of length %d, but was [%d]";
  private final int length;
  private final int radix;

  public StringLongParser(String id, StringParser<?> stringParser, int length, int radix) {
    this(id, alwaysTrue(), stringParser, length, radix);
  }

  public StringLongParser(
      String id, Predicate<P> applicable, StringParser<?> stringParser, int length, int radix) {
    super(id, applicable, stringParser);
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
