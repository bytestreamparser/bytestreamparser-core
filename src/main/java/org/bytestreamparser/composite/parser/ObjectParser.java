package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Supplier;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.data.DataObject;

/**
 * A parser for a data object.
 *
 * @param <V> the type of the data object.
 */
public class ObjectParser<V extends DataObject<V>> extends DataParser<V> {
  private final Supplier<V> instanceSupplier;
  private final List<? extends DataFieldParser<V, ?>> fieldParsers;

  /**
   * Creates a new ObjectParser.
   *
   * @param id the ID of the parser.
   * @param instanceSupplier a supplier for creating new instances of the data object.
   * @param fieldParsers the parsers for the fields of the data object.
   */
  public ObjectParser(
      String id, Supplier<V> instanceSupplier, List<? extends DataFieldParser<V, ?>> fieldParsers) {
    super(id);
    this.instanceSupplier = instanceSupplier;
    this.fieldParsers = fieldParsers;
  }

  @Override
  public void pack(V value, OutputStream output) throws IOException {
    for (DataFieldParser<V, ?> fieldParser : fieldParsers) {
      if (fieldParser.applicable(value)) {
        fieldParser.pack(value.get(fieldParser.getId()), output);
      }
    }
  }

  @Override
  public V parse(InputStream input) throws IOException {
    V instance = instanceSupplier.get();
    for (DataFieldParser<V, ?> fieldParser : fieldParsers) {
      if (fieldParser.applicable(instance)) {
        instance.set(fieldParser.getId(), fieldParser.parse(input));
      }
    }
    return instance;
  }
}
