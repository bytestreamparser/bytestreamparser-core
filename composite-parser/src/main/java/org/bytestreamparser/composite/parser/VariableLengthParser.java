package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import org.bytestreamparser.api.parser.DataParser;

/**
 * A parser for a variable-length field.
 *
 * @param <V> the type of the field value.
 */
public class VariableLengthParser<V> extends DataParser<V> {
  private final DataParser<Integer> lengthParser;
  private final Function<Integer, DataParser<V>> valueParserProvider;
  private final Function<V, Integer> lengthProvider;

  /**
   * Creates a new VariableLengthParser.
   *
   * @param id the ID of the parser.
   * @param lengthParser the parser for the length of the field.
   * @param valueParserProvider a function that provides the parser for the field value based on the
   *     length.
   * @param lengthProvider a function that provides the length of the field value when packing.
   */
  public VariableLengthParser(
      String id,
      DataParser<Integer> lengthParser,
      Function<Integer, DataParser<V>> valueParserProvider,
      Function<V, Integer> lengthProvider) {
    super(id);
    this.lengthParser = lengthParser;
    this.valueParserProvider = valueParserProvider;
    this.lengthProvider = lengthProvider;
  }

  @Override
  public void pack(V value, OutputStream output) throws IOException {
    Integer length = lengthProvider.apply(value);
    lengthParser.pack(length, output);
    valueParserProvider.apply(length).pack(value, output);
  }

  @Override
  public V parse(InputStream input) throws IOException {
    Integer length = lengthParser.parse(input);
    return valueParserProvider.apply(length).parse(input);
  }
}
