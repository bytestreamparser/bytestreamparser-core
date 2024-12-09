package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
class UnsignedByteParserTest {

  private UnsignedByteParser<TestData> parser;

  @BeforeEach
  void setUp() {
    parser = new UnsignedByteParser<>("unsigned byte");
  }

  @Test
  void parse(@Randomize byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(value[0] & 0xFF);
    assertThat(input.available()).isPositive();
  }

  @Test
  void pack(@Randomize(intMin = 0, intMax = 256) int value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(new byte[] {(byte) value});
  }

  @Test
  void pack_throws_exception_if_too_large(@Randomize(intMin = 256) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("unsigned byte: value must be between 0 and 255, but was [%d]", value);
  }

  @Test
  void pack_throws_exception_if_too_small(@Randomize(intMax = 0) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("unsigned byte: value must be between 0 and 255, but was [%d]", value);
  }
}
