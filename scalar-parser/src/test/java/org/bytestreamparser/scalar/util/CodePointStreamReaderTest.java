package org.bytestreamparser.scalar.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.*;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class CodePointStreamReaderTest {
  private static CharsetDecoder getDecoder(String charset) {
    return Charset.forName(charset)
        .newDecoder()
        .onMalformedInput(CodingErrorAction.REPLACE)
        .onUnmappableCharacter(CodingErrorAction.REPLACE);
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8"})
  void read_single_byte_code_point(String charset, @Randomize String value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));

    assertThat(reader.read()).isEqualTo(value.codePointAt(0));
    assertThat(input.available()).isEqualTo(4);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8"})
  void read_utf8_multi_byte_code_point(
      String charset, @Randomize(unicodeBlocks = "CJK_UNIFIED_IDEOGRAPHS") String value)
      throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));
    assertThat(reader.read()).isEqualTo(value.codePointAt(0));

    String substring = value.substring(value.offsetByCodePoints(0, 1));
    assertThat(input.available()).isEqualTo(substring.getBytes(charset).length);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8"})
  void read_utf8_surrogate_pair(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS") String value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));

    assertThat(Character.toString(reader.read()))
        .isEqualTo(Character.toString(value.codePointAt(0)));

    String substring = value.substring(value.offsetByCodePoints(0, 1));
    assertThat(input.available()).isEqualTo(substring.getBytes(charset).length);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-16", "UTF-16BE", "UTF-16LE"})
  void read_utf16_multi_byte_code_point(
      String charset, @Randomize(unicodeBlocks = "CJK_UNIFIED_IDEOGRAPHS") String value)
      throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));
    assertThat(reader.read()).isEqualTo(value.codePointAt(0));

    String substring = value.substring(value.offsetByCodePoints(0, 1));
    assertThat(input.available()).isEqualTo(substring.codePoints().count() * 2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-16", "UTF-16BE", "UTF-16LE"})
  void read_utf16_surrogate_pair(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS") String value) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(value.getBytes(charset));
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));

    assertThat(Character.toString(reader.read()))
        .isEqualTo(Character.toString(value.codePointAt(0)));

    String substring = value.substring(value.offsetByCodePoints(0, 1));
    assertThat(input.available()).isEqualTo(substring.codePoints().count() * 4);
  }

  @ParameterizedTest
  @ValueSource(strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8"})
  void read_insufficient_data(String charset) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));

    assertThat(reader.read()).isEqualTo(-1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void read_invalid_surrogate_pair(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS", length = 1) String value)
      throws IOException {
    byte[] bytes = value.getBytes(charset);
    bytes[bytes.length - 2] = bytes[bytes.length - 4];
    bytes[bytes.length - 1] = bytes[bytes.length - 3];

    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
    CodePointStreamReader reader = new CodePointStreamReader(input, getDecoder(charset));

    assertThat(reader.read())
        .isEqualTo(Charset.forName(charset).newDecoder().replacement().codePointAt(0));
  }

  @ParameterizedTest
  @ValueSource(strings = {"UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void read_invalid_surrogate_pair_without_replacement(
      String charset, @Randomize(unicodeBlocks = "EMOTICONS", length = 1) String value)
      throws IOException {
    byte[] bytes = value.getBytes(charset);
    bytes[bytes.length - 2] = bytes[bytes.length - 4];
    bytes[bytes.length - 1] = bytes[bytes.length - 3];

    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
    CodePointStreamReader reader =
        new CodePointStreamReader(input, Charset.forName(charset).newDecoder());

    assertThatThrownBy(reader::read).isInstanceOf(MalformedInputException.class);
  }

  @ParameterizedTest
  @ValueSource(strings = {"windows-1251"})
  void read_unmappable_characters(String charset) {
    ByteArrayInputStream input = new ByteArrayInputStream(new byte[] {(byte) -104});
    CodePointStreamReader reader =
        new CodePointStreamReader(input, Charset.forName(charset).newDecoder());

    assertThatThrownBy(reader::read).isInstanceOf(UnmappableCharacterException.class);
  }
}
