package org.bytestreamparser.composite.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bytestreamparser.composite.assertion.DataAssert.assertValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.bytestreamparser.api.parser.DataParser;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension;
import org.bytestreamparser.api.testing.extension.RandomParametersExtension.Randomize;
import org.bytestreamparser.composite.data.DataMap;
import org.bytestreamparser.scalar.parser.BinaryParser;
import org.bytestreamparser.scalar.parser.CharStringParser;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(RandomParametersExtension.class)
class ObjectParserTest {
  private static final String F1 = "F1";
  private static final String F2 = "F2";

  private static DataParser<String> stringParser(String charset, int length) {
    return new CharStringParser(F1, length, Charset.forName(charset));
  }

  private static DataParser<byte[]> binaryParser(int length) {
    return new BinaryParser(F2, length);
  }

  private static <V> DataFieldParser<DataMap, V> dataFieldParser(DataParser<V> parser) {
    return new DataFieldParser<>(parser.getId(), parser);
  }

  private static <V> DataFieldParser<DataMap, V> dataFieldParser(
      DataParser<V> parser, Predicate<DataMap> applicable) {
    return new DataFieldParser<>(parser.getId(), parser, applicable);
  }

  private static ObjectParser<DataMap> objectParser(
      DataFieldParser<DataMap, ?> parser1, DataFieldParser<DataMap, ?> parser2) {
    return new ObjectParser<>("object", DataMap::new, List.of(parser1, parser2));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack(String charset, @Randomize(length = 3) String f1, @Randomize(length = 6) byte[] f2)
      throws IOException {
    DataFieldParser<DataMap, String> parser1 =
        dataFieldParser(stringParser(charset, (int) f1.codePoints().count()));
    DataFieldParser<DataMap, byte[]> parser2 = dataFieldParser(binaryParser(f2.length));
    ObjectParser<DataMap> objectParser = objectParser(parser1, parser2);

    DataMap value = new DataMap();
    value.set(F1, f1).set(F2, f2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    objectParser.pack(value, output);

    ByteArrayOutputStream expected = new ByteArrayOutputStream();
    parser1.pack(f1, expected);
    parser2.pack(f2, expected);

    assertThat(output.toByteArray()).isEqualTo(expected.toByteArray());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void pack_when_inapplicable(
      String charset, @Randomize(length = 3) String f1, @Randomize(length = 6) byte[] f2)
      throws IOException {
    CharStringParser stringParser =
        new CharStringParser(F1, (int) f1.codePoints().count(), Charset.forName(charset));
    DataFieldParser<DataMap, String> parser1 = dataFieldParser(stringParser, d -> false);
    DataFieldParser<DataMap, byte[]> parser2 = dataFieldParser(binaryParser(f2.length));
    ObjectParser<DataMap> objectParser = objectParser(parser1, parser2);

    DataMap value = new DataMap();
    value.set(F1, f1).set(F2, f2);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    objectParser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(f2);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parse(String charset, @Randomize(length = 3) String f1, @Randomize(length = 6) byte[] f2)
      throws IOException {
    DataFieldParser<DataMap, String> parser1 =
        dataFieldParser(stringParser(charset, (int) f1.codePoints().count()));
    DataFieldParser<DataMap, byte[]> parser2 = dataFieldParser(binaryParser(f2.length));
    ObjectParser<DataMap> objectParser = objectParser(parser1, parser2);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser1.pack(f1, output);
    parser2.pack(f2, output);
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

    DataMap parsed = objectParser.parse(input);
    assertValue(parsed).hasValue(F1, f1).hasValue(F2, f2);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"US-ASCII", "IBM1047", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE"})
  void parse_when_inapplicable(
      String charset, @Randomize(length = 3) String f1, @Randomize(length = 6) byte[] f2)
      throws IOException {
    DataFieldParser<DataMap, String> parser1 =
        dataFieldParser(stringParser(charset, (int) f1.codePoints().count()));
    DataParser<byte[]> binaryParser = binaryParser(f2.length);
    DataFieldParser<DataMap, byte[]> parser2 = dataFieldParser(binaryParser, d -> false);
    ObjectParser<DataMap> objectParser = objectParser(parser1, parser2);

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    parser1.pack(f1, output);
    parser2.pack(f2, output);
    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

    DataMap parsed = objectParser.parse(input);
    assertValue(parsed).hasValue(F1, f1).hasValue(F2, null);
    assertThat(parsed.fields()).isEqualTo(Set.of(F1));
  }
}
