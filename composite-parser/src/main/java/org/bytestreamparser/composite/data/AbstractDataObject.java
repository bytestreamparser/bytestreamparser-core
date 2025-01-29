package org.bytestreamparser.composite.data;

import java.util.Map;
import java.util.Set;

public abstract class AbstractDataObject<T extends AbstractDataObject<T>> implements DataObject<T> {
  private final Map<String, Object> fields;

  protected AbstractDataObject(Map<String, Object> fields) {
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
  @SuppressWarnings("unchecked")
  public <V> T set(String id, V value) {
    fields.put(id, value);
    return (T) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T clear(String id) {
    fields.remove(id);
    return (T) this;
  }
}
