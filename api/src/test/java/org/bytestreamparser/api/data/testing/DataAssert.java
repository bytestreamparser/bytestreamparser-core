package org.bytestreamparser.api.data.testing;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractAssert;
import org.bytestreamparser.api.data.Data;

public class DataAssert<T extends Data<T>> extends AbstractAssert<DataAssert<T>, T> {
  public DataAssert(T actual) {
    super(actual, DataAssert.class);
  }

  public static <T extends Data<T>> DataAssert<T> assertValue(T actual) {
    return new DataAssert<>(actual);
  }

  public <V> DataAssert<T> hasValue(String id, V expected) {
    isNotNull();
    assertThat(actual.<V>get(id)).isEqualTo(expected);
    return this;
  }
}
