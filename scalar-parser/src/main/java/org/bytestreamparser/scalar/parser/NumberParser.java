package org.bytestreamparser.scalar.parser;

import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;

public abstract class NumberParser<P extends Data<P>, V extends Number> extends DataParser<P, V> {
  protected NumberParser(String id, Predicate<P> applicable) {
    super(id, applicable);
  }
}
