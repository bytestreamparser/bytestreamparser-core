package org.bytestreamparser.composite.data;

import java.util.Set;

/**
 * Interface for generic data objects.
 *
 * @param <T> the type of the data object.
 */
public interface DataObject<T extends DataObject<T>> {
  /** Returns the set of field identifiers. */
  Set<String> fields();

  /**
   * Returns the value of the field with the given identifier.
   *
   * @param id the field identifier.
   * @return the value of the field.
   * @param <V> the type of the value.
   */
  <V> V get(String id);

  /**
   * Sets the value of the field with the given identifier.
   *
   * @param id the field identifier.
   * @param value the value to set.
   * @return the data object itself.
   * @param <V> the type of the value.
   */
  <V> T set(String id, V value);

  /**
   * Clears the value of the field with the given identifier.
   *
   * @param id the field identifier.
   * @return the data object itself.
   */
  T clear(String id);
}
