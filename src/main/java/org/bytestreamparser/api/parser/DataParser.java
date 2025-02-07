package org.bytestreamparser.api.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link DataParser} is the abstract API for building {@link InputStream} and {@link OutputStream}
 * oriented parsers.
 *
 * @param <V> the type of the value to be parsed
 */
public abstract class DataParser<V> {
  private final String id;

  protected DataParser(String id) {
    this.id = id;
  }

  /**
   * Get the ID of the parser.
   *
   * @return the ID of the parser
   */
  public String getId() {
    return id;
  }

  /**
   * Packs the value into the output stream.
   *
   * @param value the value to be packed
   * @param output the {@link OutputStream} to write the packed value
   * @throws IOException if an I/O error occurs
   */
  public abstract void pack(V value, OutputStream output) throws IOException;

  /**
   * Parses the value from the input stream.
   *
   * @param input the {@link InputStream} to read the value from
   * @return the parsed value
   * @throws IOException if an I/O error occurs
   */
  public abstract V parse(InputStream input) throws IOException;
}
