package org.bytestreamparser.composite.parser;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.lyang.randomparamsresolver.RandomParametersExtension;
import io.github.lyang.randomparamsresolver.RandomParametersExtension.Randomize;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.bytestreamparser.scalar.parser.UnsignedByteParser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class VariableLengthParserTest {
  private static DataParser<Integer> lengthParser() {
    return new UnsignedByteParser("length");
  }

  private static Function<Integer, DataParser<String>> contentParser(String charset) {
    return length -> new CharStringParser("content", length, Charset.forName(charset));
  }

  private static VariableLengthParser<String> varParser(String charset) {
    return new VariableLengthParser<>(
        "var",
        lengthParser(),
        contentParser(charset),
        content -> Math.toIntExact(content.codePoints().count()));
  }

  private static InputStream prepareInput(String value, String charset, int length)
      throws UnsupportedEncodingException {
    byte[] content = value.getBytes(charset);
    byte[] bytes = new byte[content.length + 1];
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    buffer.put((byte) length);
    buffer.put(content);
    return new ByteArrayInputStream(buffer.array());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack_dynamic_length(String charset, @Randomize String value) throws IOException {
    VariableLengthParser<String> parser = varParser(charset);
    int codePoints = Math.toIntExact(value.codePoints().count());

    for (int length = 1; length < codePoints; length++) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      parser.pack(value.substring(0, length), output);

      byte[] bytes = output.toByteArray();
      assertThat(Integer.valueOf(bytes[0])).isEqualTo(length);
      assertThat(Arrays.copyOfRange(bytes, 1, bytes.length))
          .isEqualTo(value.substring(0, length).getBytes(charset));
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parse_dynamic_length(String charset, @Randomize String value) throws IOException {
    VariableLengthParser<String> parser = varParser(charset);
    int codePoints = Math.toIntExact(value.codePoints().count());

    for (int length = 0; length < codePoints; length++) {
      String parsed = parser.parse(prepareInput(value, charset, length));
      assertThat(parsed).isEqualTo(value.substring(0, length));
    }
  }
}
