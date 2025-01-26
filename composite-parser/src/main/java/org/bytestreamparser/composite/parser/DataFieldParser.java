package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.data.DataObject;

public class DataFieldParser<D extends DataObject<D>, V> extends DataParser<V> {
  private final DataParser<V> valueParser;
  private final Predicate<D> applicable;

  public DataFieldParser(String id, DataParser<V> valueParser) {
    this(id, valueParser, d -> true);
  }

  public DataFieldParser(String id, DataParser<V> valueParser, Predicate<D> applicable) {
    super(id);
    this.valueParser = valueParser;
    this.applicable = applicable;
  }

  public boolean applicable(D data) {
    return applicable.test(data);
  }

  @Override
  public void pack(V value, OutputStream output) throws IOException {
    valueParser.pack(value, output);
  }

  @Override
  public V parse(InputStream input) throws IOException {
    return valueParser.parse(input);
  }
}
