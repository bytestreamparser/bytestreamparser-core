package org.bytestreamparser.composite.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractAssert;
import org.bytestreamparser.composite.data.DataObject;

public class DataAssert<T extends DataObject<T>> extends AbstractAssert<DataAssert<T>, T> {
  public DataAssert(T actual) {
    super(actual, DataAssert.class);
  }

  public static <T extends DataObject<T>> DataAssert<T> assertValue(T actual) {
    return new DataAssert<>(actual);
  }

  public <V> DataAssert<T> hasValue(String id, V expected) {
    isNotNull();
    assertThat(actual.<V>get(id)).isEqualTo(expected);
    return this;
  }
}
