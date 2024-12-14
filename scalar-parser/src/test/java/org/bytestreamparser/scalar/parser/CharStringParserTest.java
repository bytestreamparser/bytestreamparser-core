package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.*;
import java.nio.charset.Charset;
import org.bytestreamparser.api.testing.data.TestData;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class CharStringParserTest {
  private static CharStringParser<TestData> createParser(String charset) {
    return new CharStringParser<>("txt", 3, Charset.forName(charset));
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void parse_single_byte_charset(String charset, @Randomize String value) throws IOException {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    String parsed = parser.parse(input);
    assertThat(parsed).isEqualTo(value.substring(0, 3));
    assertThat(input.available()).isEqualTo(2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void parse_multi_byte_charset(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS") String value) throws IOException {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    String parsed = parser.parse(input);
    assertThat(parsed.codePoints().toArray()).isEqualTo(value.codePoints().limit(3).toArray());
    assertThat(input.available()).isPositive();
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void parse_single_byte_charset_insufficient_data(
      String charset, @Randomize(length = 2) String value) throws IOException {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage("End of stream reached after reading 2 chars, chars expected [3]");
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void parse_multi_byte_charset_insufficient_data(
      String charset, @Randomize(length = 2, unicodeBlocks = "EMOTICONS") String value)
      throws IOException {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage("End of stream reached after reading 2 chars, chars expected [3]");
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void pack_single_byte_charset(String charset, @Randomize(length = 3) String value)
      throws IOException {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value.getBytes(charset));
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void pack_multi_byte_charset(
      String charset, @Randomize(length = 3, unicodeBlocks = "EMOTICONS") String value)
      throws IOException {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value.getBytes(charset));
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1"})
  void pack_single_byte_insufficient_data(String charset, @Randomize(length = 2) String value) {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "txt: value must be of length 3, but was [%d]", value.codePoints().count());
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16"})
  void pack_multi_byte_insufficient_data(
      String charset, @Randomize(length = 2, unicodeBlocks = "EMOTICONS") String value) {
    CharStringParser<TestData> parser = createParser(charset);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "txt: value must be of length 3, but was [%d]", value.codePoints().count());
  }
}
