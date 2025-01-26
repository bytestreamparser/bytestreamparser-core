package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Supplier;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.composite.data.DataObject;

public class ObjectParser<V extends DataObject<V>> extends DataParser<V> {
  private final Supplier<V> instanceSupplier;
  private final List<DataFieldParser<V, ?>> fieldParsers;

  public ObjectParser(
      String id, Supplier<V> instanceSupplier, List<DataFieldParser<V, ?>> fieldParsers) {
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
