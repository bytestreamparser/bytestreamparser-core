package org.bytestreamparser.composite.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataMap implements DataObject<DataMap> {
  private final Map<String, Object> fields;

  public DataMap() {
    this(new HashMap<>());
  }

  public DataMap(Map<String, Object> fields) {
    this.fields = fields;
  }

  @Override
  public Set<String> fields() {
    return fields.keySet();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V get(String id) {
    return (V) fields.get(id);
  }

  @Override
  public <V> DataMap set(String id, V value) {
    fields.put(id, value);
    return this;
  }

  @Override
  public DataMap clear(String id) {
    fields.remove(id);
    return this;
  }
}
