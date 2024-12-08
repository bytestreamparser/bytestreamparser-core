package org.bytestreamparser.api.testing.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class TestParserTest {
  private TestParser<TestData> parser;
  private String id;

  @BeforeEach
  void setUp(@Randomize String id) {
    this.id = id;
    parser = new TestParser<>(id);
  }

  @Test
  void getId() {
    assertThat(parser.getId()).isEqualTo(id);
  }

  @Test
  void pack(@Randomize byte[] value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value);
  }

  @Test
  void parse(@Randomize byte[] value) throws IOException {
    byte[] parsed = parser.parse(new ByteArrayInputStream(value));
    assertThat(parsed).isEqualTo(value);
  }

  @Test
  void applicable(@Randomize String field, @Randomize int value) {
    TestData testData = new TestData();

    assertThat(parser.applicable(testData)).isTrue();

    parser = new TestParser<>(id, parent -> parent.fields().isEmpty());
    assertThat(parser.applicable(testData)).isTrue();

    testData.set(field, value);
    assertThat(parser.applicable(testData)).isFalse();
  }
}
