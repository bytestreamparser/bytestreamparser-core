package org.bytestreamparser.scalar.parser;

import static org.bytestreamparser.api.util.Predicates.alwaysTrue;
import static org.bytestreamparser.scalar.util.InputStreams.readFully;
import static org.bytestreamparser.scalar.util.Preconditions.check;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.function.Predicate;
import org.bytestreamparser.api.data.Data;

public class CharStringParser<P extends Data<P>> extends StringParser<P> {
  private static final String INVALID_LENGTH = "%s: value must be of length %d, but was [%d]";
  private static final String STREAM_CHECK = "%s: %s#markSupported() required to parse %s charset";
  private final int length;
  private final CharsetDecoder decoder;

  public CharStringParser(String id, int length, Charset charset) {
    this(id, alwaysTrue(), length, charset);
  }

  public CharStringParser(String id, Predicate<P> applicable, int length, Charset charset) {
    super(id, applicable);
    this.length = length;
    decoder =
        charset
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);
  }

  @Override
  public void pack(String value, OutputStream output) throws IOException {
    check(
        value.codePoints().count() == length,
        INVALID_LENGTH,
        getId(),
        length,
        value.codePoints().count());
    output.write(value.getBytes(decoder.charset()));
  }

  @Override
  public String parse(InputStream input) throws IOException {
    check(input.markSupported(), STREAM_CHECK, getId(), input.getClass(), decoder.charset().name());
    return readFully(input, length, decoder);
  }
}
