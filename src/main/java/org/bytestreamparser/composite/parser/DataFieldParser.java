package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Predicate;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.data.DataObject;

/**
 * A parser for a field of a data object.
 *
 * @param <D> the type of the data object.
 * @param <V> the type of the field value.
 */
public class DataFieldParser<D extends DataObject<D>, V> extends DataParser<V> {
  private final DataParser<V> valueParser;
  private final Predicate<D> applicable;

  /**
   * Creates a new DataFieldParser, where the parser is always applicable.
   *
   * @param id the ID of the parser.
   * @param valueParser the parser for the field value.
   */
  public DataFieldParser(String id, DataParser<V> valueParser) {
    this(id, valueParser, d -> true);
  }

  /**
   * Creates a new DataFieldParser.
   *
   * @param id the ID of the parser.
   * @param valueParser the parser for the field value.
   * @param applicable a predicate that determines whether this parser is applicable to a data
   *     object.
   */
  public DataFieldParser(String id, DataParser<V> valueParser, Predicate<D> applicable) {
    super(id);
    this.valueParser = valueParser;
    this.applicable = applicable;
  }

  /**
   * Returns whether this parser is applicable to the given data object.
   *
   * @param data the data object.
   * @return {@code true} if this parser is applicable, {@code false} otherwise.
   */
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
