package org.bytestreamparser.api.data.testing;

import java.util.LinkedHashMap;
import java.util.Set;
import org.bytestreamparser.api.data.Data;

public class TestData implements Data<TestData> {

  private final LinkedHashMap<String, Object> fields;

  public TestData() {
    fields = new LinkedHashMap<>();
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
  public <V> TestData set(String id, V value) {
    fields.put(id, value);
    return this;
  }

  @Override
  public TestData clear(String id) {
    fields.remove(id);
    return this;
  }
}
