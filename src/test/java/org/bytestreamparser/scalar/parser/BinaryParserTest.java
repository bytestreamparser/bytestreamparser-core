package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class BinaryParserTest {
  private static final String ID = "bin";

  @Test
  void pack(@Randomize byte[] value) throws IOException {
    BinaryParser parser = new BinaryParser(ID, value.length);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value);
  }

  @Test
  void pack_insufficient_data(@Randomize byte[] value) {
    BinaryParser parser = new BinaryParser(ID, value.length + 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bin: value must be of length %d, but was [%d]", value.length + 1, value.length);
  }

  @Test
  void pack_oversize_data(@Randomize byte[] value) {
    BinaryParser parser = new BinaryParser(ID, value.length - 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "bin: value must be of length %d, but was [%d]", value.length - 1, value.length);
  }

  @Test
  void parse(@Randomize byte[] value) throws IOException {
    BinaryParser parser = new BinaryParser(ID, value.length - 1);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input)).isEqualTo(Arrays.copyOfRange(value, 0, value.length - 1));
    assertThat(input.available()).isPositive();
  }

  @Test
  void parse_insufficient_data(@Randomize byte[] value) {
    BinaryParser parser = new BinaryParser(ID, value.length + 1);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d bytes, bytes expected [%d]",
            value.length, value.length + 1);
  }
}
