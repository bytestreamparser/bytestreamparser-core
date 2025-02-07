package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class UnsignedByteParserTest {

  public static final int UNSIGNED_BYTE_MAX = 0xFF;
  private UnsignedByteParser parser;

  @BeforeEach
  void setUp() {
    parser = new UnsignedByteParser("unsigned byte");
  }

  @Test
  void parse(@Randomize byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(value[0] & UNSIGNED_BYTE_MAX);
    assertThat(input.available()).isPositive();
  }

  @Test
  void pack(@Randomize(intMin = 0, intMax = UNSIGNED_BYTE_MAX + 1) int value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(new byte[] {(byte) value});
  }

  @Test
  void pack_throws_exception_if_too_large(@Randomize(intMin = 1) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(0xFF + value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "unsigned byte: value must be between 0 and %d, but was [%d]",
            UNSIGNED_BYTE_MAX, UNSIGNED_BYTE_MAX + value);
  }

  @Test
  void pack_throws_exception_if_too_small(@Randomize(intMin = 1) int value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(-value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "unsigned byte: value must be between 0 and %d, but was [%d]",
            UNSIGNED_BYTE_MAX, -value);
  }
}
