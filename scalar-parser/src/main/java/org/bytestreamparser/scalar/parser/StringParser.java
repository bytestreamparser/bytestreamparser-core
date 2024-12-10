package org.bytestreamparser.scalar.parser;

import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;
import org.bytestreamparser.api.parser.DataParser;

public abstract class StringParser<P extends Data<P>> extends DataParser<P, String> {
  protected StringParser(String id, Predicate<P> applicable) {
    super(id, applicable);
  }
}
