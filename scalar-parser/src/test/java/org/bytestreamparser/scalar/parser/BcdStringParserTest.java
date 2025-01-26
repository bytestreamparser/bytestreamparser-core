package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HexFormat;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RandomParametersExtension.class)
class BcdStringParserTest {
  private static final HexFormat HEX_FORMAT = HexFormat.of();

  @Test
  void pack(@Randomize(intMin = 0, intMax = 100) int value) throws IOException {
    BcdStringParser parser = new BcdStringParser("bcd", 2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String bcdString = String.format("%02d", value);
    parser.pack(bcdString, output);
    assertThat(output.toByteArray()).isEqualTo(HEX_FORMAT.parseHex(bcdString));
  }

  @Test
  void pack_odd_length_value(@Randomize(intMin = 0, intMax = 1000) int value) throws IOException {
    BcdStringParser parser = new BcdStringParser("bcd", 3);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(String.format("%03d", value), output);
    assertThat(output.toByteArray()).isEqualTo(HEX_FORMAT.parseHex(String.format("%04d", value)));
  }

  @Test
  void pack_oversize_data(@Randomize(intMin = 0, intMax = 1000) int value) {
    BcdStringParser parser = new BcdStringParser("bcd", 2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    String data = String.format("%03d", value);
    assertThatThrownBy(() -> parser.pack(data, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bcd: value length must be less than or equal to %d, but was [%d]", 2, 3);
  }

  @Test
  void pack_invalid_bcd_string() {
    BcdStringParser parser = new BcdStringParser("bcd", 2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack("ABC", output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bcd: Invalid BCD String [ABC]");
  }

  @Test
  void parse(@Randomize(intMin = 0, intMax = 100) int value) throws IOException {
    BcdStringParser parser = new BcdStringParser("bcd", 2);
    String bcdString = String.format("%02d", value);
    ByteArrayInputStream input = new ByteArrayInputStream(HEX_FORMAT.parseHex(bcdString));
    assertThat(parser.parse(input)).isEqualTo(bcdString);
  }

  @Test
  void parse_odd_length_string(@Randomize(intMin = 0, intMax = 1000) int value) throws IOException {
    BcdStringParser parser = new BcdStringParser("bcd", 3);
    String expected = String.format("%03d", value);
    ByteArrayInputStream input =
        new ByteArrayInputStream(HEX_FORMAT.parseHex(String.format("%04d", value)));
    assertThat(parser.parse(input)).isEqualTo(expected);
  }

  @Test
  void parse_insufficient_data(@Randomize(intMin = 0, intMax = 100) int value) {
    BcdStringParser parser = new BcdStringParser("bcd", 3);
    String bcdString = String.format("%02d", value);
    ByteArrayInputStream input = new ByteArrayInputStream(HEX_FORMAT.parseHex(bcdString));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage("End of stream reached after reading %d bytes, bytes expected [%d]", 1, 2);
  }

  @Test
  void parse_invalid_bcd_string() {
    BcdStringParser parser = new BcdStringParser("bcd", 3);
    ByteArrayInputStream input = new ByteArrayInputStream(HEX_FORMAT.parseHex("0abc"));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("bcd: Invalid BCD String [abc]");
  }
}
