package org.bytestreamparser.scalar.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;

public abstract class StringNumberParser<P extends Data<P>, V extends Number>
    extends NumberParser<P, V> {
  private final StringParser<?> stringParser;

  protected StringNumberParser(String id, Predicate<P> applicable, StringParser<?> stringParser) {
    super(id, applicable);
    this.stringParser = stringParser;
  }

  @Override
  public void pack(V value, OutputStream output) throws IOException {
    stringParser.pack(fromNumber(value), output);
  }

  @Override
  public V parse(InputStream input) throws IOException {
    return toNumber(stringParser.parse(input));
  }

  protected abstract String fromNumber(V value);

  protected abstract V toNumber(String value);
}
