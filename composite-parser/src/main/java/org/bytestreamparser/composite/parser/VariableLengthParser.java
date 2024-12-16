package org.bytestreamparser.composite.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.NumberParser;

public class VariableLengthParser<P extends Data<P>, V> extends DataParser<P, V> {
  private final NumberParser<?, Integer> lengthParser;
  private final Function<Integer, DataParser<?, V>> valueParserProvider;
  private final Function<V, Integer> lengthProvider;

  public VariableLengthParser(
      String id,
      NumberParser<?, Integer> lengthParser,
      Function<Integer, DataParser<?, V>> valueParserProvider,
      Function<V, Integer> lengthProvider) {
    this(id, alwaysTrue(), lengthParser, valueParserProvider, lengthProvider);
  }

  public VariableLengthParser(
      String id,
      Predicate<P> applicable,
      NumberParser<?, Integer> lengthParser,
      Function<Integer, DataParser<?, V>> valueParserProvider,
      Function<V, Integer> lengthProvider) {
    super(id, applicable);
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
