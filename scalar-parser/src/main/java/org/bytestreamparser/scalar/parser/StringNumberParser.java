package org.bytestreamparser.scalar.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;

public abstract class StringNumberParser<V extends Number>
    extends org.bytestreamparser.api.parser.DataParser<V> {
  private final DataParser<String> stringParser;

  protected StringNumberParser(String id, DataParser<String> stringParser) {
    super(id);
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
