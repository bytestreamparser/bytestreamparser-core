package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import org.bytestreamparser.api.parser.DataParser;

public class VariableLengthParser<V> extends DataParser<V> {
  private final DataParser<Integer> lengthParser;
  private final Function<Integer, DataParser<V>> valueParserProvider;
  private final Function<V, Integer> lengthProvider;

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
