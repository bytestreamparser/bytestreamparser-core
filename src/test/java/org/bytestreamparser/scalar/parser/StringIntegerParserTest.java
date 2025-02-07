package org.bytestreamparser.scalar.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class StringIntegerParserTest {
  private static StringIntegerParser createParser(String charset, int length, int radix) {
    CharStringParser stringParser = new CharStringParser("str", length, Charset.forName(charset));
    return new StringIntegerParser("str-int", stringParser, length, radix);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parse(
      String charset,
      @Randomize Integer value,
      @Randomize(intMin = Character.MIN_RADIX, intMax = Character.MAX_RADIX + 1) int radix)
      throws IOException {
    String string = Integer.toString(value, radix);
    InputStream input = new ByteArrayInputStream(string.getBytes(charset));
    StringIntegerParser parser = createParser(charset, string.length(), radix);
    assertThat(parser.parse(input)).isEqualTo(value);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parseTooLargeNumber(
      String charset,
      @Randomize(intMin = Character.MIN_RADIX, intMax = Character.MAX_RADIX + 1) int radix)
      throws IOException {
    String value = Long.toString(Long.MAX_VALUE, radix);
    InputStream input = new ByteArrayInputStream(value.getBytes(charset));
    StringIntegerParser parser = createParser(charset, value.length(), radix);

    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(NumberFormatException.class)
        .hasMessageContaining("For input string: \"%s\"", value);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack(
      String charset,
      @Randomize Integer value,
      @Randomize(intMin = Character.MIN_RADIX, intMax = Character.MAX_RADIX + 1) int radix)
      throws IOException {
    String string = Integer.toString(value, radix);
    StringIntegerParser parser = createParser(charset, string.length(), radix);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(string.getBytes(charset));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack_too_large_number(
      String charset,
      @Randomize(intMin = Character.MIN_RADIX, intMax = Character.MAX_RADIX + 1) int radix) {
    StringIntegerParser parser = createParser(charset, 1, radix);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    int value = Integer.MAX_VALUE;
    assertThatThrownBy(() -> parser.pack(value, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "str-int: value must be of length 1, but was [%d]",
            Integer.toString(value, radix).length());
  }
}
