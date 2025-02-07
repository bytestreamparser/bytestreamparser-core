package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class UnsignedShortParserTest {
  private static final int UNSIGNED_SHORT_MAX = 0xFFFF;
  private UnsignedShortParser parser;

  private static int convert(byte[] value) {
    return ((int) value[0] & 0xFF) << 8 | value[1] & 0xFF;
  }

  @BeforeEach
  void setUp() {
    parser = new UnsignedShortParser("unsigned short");
  }

  @Test
  void parse(@Randomize(length = 3) byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(convert(value));
    assertThat(input.available()).isPositive();
  }

  @Test
  void pack(@Randomize(intMin = 0, intMax = UNSIGNED_SHORT_MAX + 1) Integer value)
      throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    byte[] expected = ByteBuffer.allocate(Short.BYTES).putShort(value.shortValue()).array();
    assertThat(output.toByteArray()).isEqualTo(expected);
  }

  @Test
  void pack_throws_exception_if_too_large(@Randomize(intMin = 1) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(UNSIGNED_SHORT_MAX + value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "unsigned short: value must be between 0 and %d, but was [%d]",
            UNSIGNED_SHORT_MAX, UNSIGNED_SHORT_MAX + value);
  }

  @Test
  void pack_throws_exception_if_too_small(@Randomize(intMin = 1) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(-value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "unsigned short: value must be between 0 and %d, but was [%d]",
            UNSIGNED_SHORT_MAX, -value);
  }
}
