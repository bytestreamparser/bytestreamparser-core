package org.bytestreamparser.scalar.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bytestreamparser.api.parser.DataParser;

/**
 * Abstract class for fixed length {@link Number} parsers that pack and parse the value as a {@link
 * String}.
 *
 * @param <V>
 */
public abstract class StringNumberParser<V extends Number> extends DataParser<V> {
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

  /**
   * Converts the value to a {@link String}.
   *
   * @param value the value to be converted.
   * @return the {@link String} representation of the value.
   */
  protected abstract String fromNumber(V value);

  /**
   * Converts the value to a {@link V}.
   *
   * @param value the value to be converted.
   * @return the {@link V} representation of the value.
   */
  protected abstract V toNumber(String value);
}
