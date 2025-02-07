package org.bytestreamparser.api.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataParserTest {
  private TestParser parser;
  private RandomGenerator randomGenerator;

  @BeforeEach
  void setUp() {
    randomGenerator = RandomGenerator.getDefault();
    parser = new TestParser("id");
  }

  @Test
  void getId() {
    assertThat(parser.getId()).isEqualTo("id");
  }

  @Test
  void pack() throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] value = new byte[randomGenerator.nextInt(1, 100)];
    randomGenerator.nextBytes(value);
    parser.pack(value, output);
    assertThat(output.toByteArray()).isEqualTo(value);
  }

  @Test
  void parse() throws IOException {
    byte[] value = new byte[randomGenerator.nextInt(1, 100)];
    randomGenerator.nextBytes(value);
    byte[] parsed = parser.parse(new ByteArrayInputStream(value));
    assertThat(parsed).isEqualTo(value);
  }

  static class TestParser extends DataParser<byte[]> {
    TestParser(String id) {
      super(id);
    }

    @Override
    public void pack(byte[] value, OutputStream output) throws IOException {
      output.write(value);
    }

    @Override
    public byte[] parse(InputStream input) throws IOException {
      return input.readAllBytes();
    }
  }
}
