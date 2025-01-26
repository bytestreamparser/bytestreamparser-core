package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.*;
import java.nio.charset.Charset;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class CharStringParserTest {
  private static CharStringParser createParser(String charset, int length) {
    return new CharStringParser("txt", length, Charset.forName(charset));
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void parse_single_byte_charset(
      String charset, @Randomize String value, @Randomize(intMin = 1, intMax = 4) int length)
      throws IOException {
    CharStringParser parser = createParser(charset, length);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    String parsed = parser.parse(input);
    assertThat(parsed).isEqualTo(value.substring(0, length));
    assertThat(input.available()).isPositive();
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void parse_multi_byte_charset(
      String charset,
      @Randomize(unicodeBlocks = "EMOTICONS") String value,
      @Randomize(intMin = 1, intMax = 2) int length)
      throws IOException {
    CharStringParser parser = createParser(charset, length);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    String parsed = parser.parse(input);
    assertThat(parsed.codePoints().limit(length).toArray())
        .isEqualTo(value.codePoints().limit(length).toArray());
    assertThat(input.available()).isPositive();
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void parse_single_byte_charset_insufficient_data(String charset, @Randomize String value)
      throws IOException {
    CharStringParser parser = createParser(charset, value.length() + 1);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d chars, chars expected [%d]",
            value.length(), value.length() + 1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void parse_multi_byte_charset_insufficient_data(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS") String value) throws IOException {
    int length = (int) (value.codePoints().count());
    CharStringParser parser = createParser(charset, length + 1);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage(
            "End of stream reached after reading %d chars, chars expected [%d]",
            length, length + 1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void pack_single_byte_charset(String charset, @Randomize String value) throws IOException {
    CharStringParser parser = createParser(charset, value.length());
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value.getBytes(charset));
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void pack_multi_byte_charset(String charset, @Randomize(unicodeBlocks = "EMOTICONS") String value)
      throws IOException {
    CharStringParser parser = createParser(charset, 2);
    StringBuilder builder = new StringBuilder();
    value.codePoints().limit(2).forEach(builder::appendCodePoint);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(builder.toString(), output);
    assertThat(output.toByteArray()).isEqualTo(builder.toString().getBytes(charset));
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void pack_single_byte_insufficient_data(String charset, @Randomize String value) {
    CharStringParser parser = createParser(charset, value.length() + 1);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "txt: value must be of length %d, but was [%d]", value.length() + 1, value.length());
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void pack_multi_byte_insufficient_data(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS") String value) {
    CharStringParser parser = createParser(charset, (int) (value.codePoints().count() + 1));
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "txt: value must be of length %d, but was [%d]",
            value.codePoints().count() + 1, value.codePoints().count());
  }
}
