package org.bytestreamparser.composite.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import org.bytestreamparser.api.parser.DataParser;

/**
 * A parser for a list of values.
 *
 * @param <V> the type of the values in the list.
 */
public class ListParser<V> extends DataParser<List<V>> {
  private final DataParser<V> itemParser;

  /**
   * Creates a new ListParser.
   *
   * @param id the ID of the parser.
   * @param itemParser the parser for the list items.
   */
  public ListParser(String id, DataParser<V> itemParser) {
    super(id);
    this.itemParser = itemParser;
  }

  @Override
  public void pack(List<V> values, OutputStream output) throws IOException {
    for (V value : values) {
      itemParser.pack(value, output);
    }
  }

  /**
   * Parses a list of values from the given {@link InputStream}. Note that this method reads all
   * available bytes from the input stream.
   *
   * @param input the {@link InputStream} to read the value from.
   * @return the parsed list of values.
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public List<V> parse(InputStream input) throws IOException {
    LinkedList<V> values = new LinkedList<>();
    while (input.available() > 0) {
      values.add(itemParser.parse(input));
    }
    return values;
  }
}
