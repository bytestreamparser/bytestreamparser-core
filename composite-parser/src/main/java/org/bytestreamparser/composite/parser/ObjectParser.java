package org.bytestreamparser.composite.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;

public class ObjectParser<P extends Data<P>, V extends Data<V>> extends DataParser<P, V> {
  private final Supplier<V> instanceSupplier;
  private final List<DataParser<V, ?>> fieldParsers;

  public ObjectParser(
      String id, Supplier<V> instanceSupplier, List<DataParser<V, ?>> fieldParsers) {
    this(id, alwaysTrue(), instanceSupplier, fieldParsers);
  }

  public ObjectParser(
      String id,
      Predicate<P> applicable,
      Supplier<V> instanceSupplier,
      List<DataParser<V, ?>> fieldParsers) {
    super(id, applicable);
    this.instanceSupplier = instanceSupplier;
    this.fieldParsers = fieldParsers;
  }

  @Override
  public void pack(V value, OutputStream output) throws IOException {
    for (DataParser<V, ?> fieldParser : fieldParsers) {
      if (fieldParser.applicable(value)) {
        fieldParser.pack(value.get(fieldParser.getId()), output);
      }
    }
  }

  @Override
  public V parse(InputStream input) throws IOException {
    V instance = instanceSupplier.get();
    for (DataParser<V, ?> fieldParser : fieldParsers) {
      if (fieldParser.applicable(instance)) {
        instance.set(fieldParser.getId(), fieldParser.parse(input));
      }
    }
    return instance;
  }
}
