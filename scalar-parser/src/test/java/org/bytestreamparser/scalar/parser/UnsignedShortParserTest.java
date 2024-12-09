package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class UnsignedShortParserTest {
  private UnsignedShortParser<TestData> parser;

  private static int convert(byte[] value) {
    return ((int) value[0] & 0xFF) << 8 | value[1] & 0xFF;
  }

  @BeforeEach
  void setUp() {
    parser = new UnsignedShortParser<>("unsigned short");
  }

  @Test
  void parse(@Randomize(length = 3) byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(convert(value));
    assertThat(input.available()).isPositive();
  }

  @Test
  void pack(@Randomize(intMin = 0, intMax = 0xFFFF) Integer value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    byte[] expected = ByteBuffer.allocate(Short.BYTES).putShort(value.shortValue()).array();
    assertThat(output.toByteArray()).isEqualTo(expected);
  }

  @Test
  void pack_throws_exception_if_too_large(@Randomize(intMin = 65536) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("unsigned short: value must be between 0 and 65535, but was [%d]", value);
  }

  @Test
  void pack_throws_exception_if_too_small(@Randomize(intMax = 0) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("unsigned short: value must be between 0 and 65535, but was [%d]", value);
  }
}
