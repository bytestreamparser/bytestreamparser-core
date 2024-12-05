package org.bytestreamparser.api.data;

import java.util.Set;

public interface Data<T extends Data<T>> {

  Set<String> fields();

  <V> V get(String id);

  <V> T set(String id, V value);

  T clear(String id);
}
