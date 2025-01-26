package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class BinaryParserTest {
  private static final int LENGTH = 3;
  private static final String ID = "bin";
  private BinaryParser parser;

  @BeforeEach
  void setUp() {
    parser = new BinaryParser(ID, LENGTH);
  }

  @Test
  void pack(@Randomize(length = LENGTH) byte[] value) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value);
  }

  @Test
  void pack_insufficient_data(@Randomize(length = LENGTH - 1) byte[] value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bin: value must be of length %d, but was [%d]", LENGTH, value.length);
  }

  @Test
  void pack_oversize_data(@Randomize(length = LENGTH + 1) byte[] value) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bin: value must be of length %d, but was [%d]", LENGTH, value.length);
  }

  @Test
  void parse(@Randomize(length = LENGTH + 1) byte[] value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    byte[] expected = Arrays.copyOfRange(value, 0, LENGTH);
    assertThat(parser.parse(input)).isEqualTo(expected);
    assertThat(input.available()).isPositive();
  }

  @Test
  void parse_insufficient_data(@Randomize(length = LENGTH - 1) byte[] value) {
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d bytes, bytes expected [%d]",
            value.length, LENGTH);
  }
}
