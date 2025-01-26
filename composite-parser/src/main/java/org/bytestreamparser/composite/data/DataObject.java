package org.bytestreamparser.composite.data;

import java.util.Set;

public interface DataObject<T extends DataObject<T>> {
  Set<String> fields();

  <V> V get(String id);

  <V> T set(String id, V value);

  T clear(String id);
}
