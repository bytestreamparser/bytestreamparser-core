package org.bytestreamparser.api.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bytestreamparser.api.testing.assertion.DataAssert.assertValue;

import java.util.Set;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class DataTest {
  private TestData data;

  @BeforeEach
  void setUp() {
    data = new TestData();
  }

  @Test
  void fields(@Randomize String id) {
    assertThat(data.fields()).isEmpty();
    assertThat(data.set(id, 1).fields()).isEqualTo(Set.of(id));
  }

  @Test
  void get_and_set(@Randomize String id, @Randomize int value) {
    assertValue(data).hasValue(id, null);
    assertValue(data.set(id, value)).hasValue(id, value);
  }

  @Test
  void clear(@Randomize String id, @Randomize int value) {
    assertThat(data.set(id, value).fields()).contains(id);
    assertThat(data.clear(id).fields()).isEmpty();
  }
}
