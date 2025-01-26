package org.bytestreamparser.composite.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class ListParserTest {
  private static ListParser<String> listParser(String charset, int length) {
    CharStringParser stringParser = new CharStringParser("item", length, Charset.forName(charset));
    return new ListParser<>("list", stringParser);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack(String charset, @Randomize String value1, @Randomize String value2) throws IOException {
    ListParser<String> parser = listParser(charset, value1.length());

    byte[] bytes1 = value1.getBytes(charset);
    byte[] bytes2 = value2.getBytes(charset);
    ByteBuffer byteBuffer = ByteBuffer.allocate(bytes1.length + bytes2.length);
    byteBuffer.put(bytes1).put(bytes2);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser.pack(List.of(value1, value2), output);
    assertThat(output.toByteArray()).isEqualTo(byteBuffer.array());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack_incompatible_values(String charset) {
    ListParser<String> parser = listParser(charset, 5);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    List<String> values = List.of("12345", "abc");
    assertThatThrownBy(() -> parser.pack(values, output))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("item: value must be of length 5, but was [3]");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parse(String charset, @Randomize String value1, @Randomize String value2)
      throws IOException {
    ListParser<String> parser = listParser(charset, value1.length());
    List<String> parsed =
        parser.parse(new ByteArrayInputStream((value1 + value2).getBytes(charset)));
    assertThat(parsed).isEqualTo(List.of(value1, value2));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parse_insufficient_data(String charset, @Randomize(length = 4) String value)
      throws UnsupportedEncodingException {
    ListParser<String> parser = listParser(charset, 5);
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    assertThatThrownBy(() -> parser.parse(input))
        .isInstanceOf(EOFException.class)
        .hasMessage("End of stream reached after reading 4 chars, chars expected [5]");
  }
}
