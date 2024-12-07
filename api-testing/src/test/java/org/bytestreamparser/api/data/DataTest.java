package org.bytestreamparser.api.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bytestreamparser.api.testing.assertion.DataAssert.assertValue;

import java.util.Set;
import org.bytestreamparser.api.testing.data.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataTest {
  private static final String ID = "id";
  private TestData data;

  @BeforeEach
  void setUp() {
    data = new TestData();
  }

  @Test
  void fields() {
    assertThat(data.fields()).isEmpty();
    assertThat(data.set(ID, 1).fields()).isEqualTo(Set.of(ID));
  }

  @Test
  void get_and_set() {
    assertValue(data).hasValue(ID, null);
    assertValue(data.set(ID, 1)).hasValue(ID, 1);
  }

  @Test
  void clear() {
    assertThat(data.set(ID, 1).fields()).contains(ID);
    assertThat(data.clear(ID).fields()).isEmpty();
  }
}
