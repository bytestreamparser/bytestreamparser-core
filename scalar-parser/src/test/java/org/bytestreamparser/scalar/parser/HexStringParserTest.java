package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HexFormat;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class HexStringParserTest {
  private static final HexFormat HEX_FORMAT = HexFormat.of();

  @Test
  void pack(@Randomize(intMin = 0, intMax = 0xFF) int value) throws IOException {
    HexStringParser parser = new HexStringParser("hex", 2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String expected = String.format("%02x", value);
    parser.pack(expected, output);
    assertThat(output.toByteArray()).isEqualTo(HEX_FORMAT.parseHex(expected));
  }

  @Test
  void pack_odd_length_value(@Randomize(intMin = 0, intMax = 0x0FFF) int value) throws IOException {
    HexStringParser parser = new HexStringParser("hex", 3);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String expected = String.format("%03x", value);
    parser.pack(expected, output);
    assertThat(output.toByteArray()).isEqualTo(HEX_FORMAT.parseHex(String.format("%04x", value)));
  }

  @Test
  void pack_oversize_data(@Randomize(intMin = 0, intMax = 0x0FFF) int value) {
    HexStringParser parser = new HexStringParser("hex", 2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String data = String.format("%03x", value);
    assertThatThrownBy(() -> parser.pack(data, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("hex: value length must be less than or equal to %d, but was [%d]", 2, 3);
  }

  @Test
  void parse(@Randomize(length = 3) byte[] value) throws IOException {
    HexStringParser parser = new HexStringParser("hex", 2);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThat(parser.parse(input))
        .isEqualTo(HEX_FORMAT.formatHex(Arrays.copyOfRange(value, 0, 1)));
    assertThat(input.available()).isPositive();
  }

  @Test
  void parse_odd_length_value(@Randomize(length = 3) byte[] value) throws IOException {
    HexStringParser parser = new HexStringParser("hex", 3);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    String expected = HEX_FORMAT.formatHex(Arrays.copyOfRange(value, 0, 2)).substring(1, 4);
    assertThat(parser.parse(input)).isEqualTo(expected);
  }

  @Test
  void parse_insufficient_data(@Randomize(length = 1) byte[] value) {
    HexStringParser parser = new HexStringParser("hex", 3);
    ByteArrayInputStream input = new ByteArrayInputStream(value);
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d bytes, bytes expected [%d]", value.length, 2);
  }
}
