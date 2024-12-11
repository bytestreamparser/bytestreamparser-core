package org.bytestreamparser.scalar.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;

public class CodePointStreamReader {

  private final InputStreamReader reader;
  private final int replacement;

  public CodePointStreamReader(InputStream input, CharsetDecoder decoder) {
    this(input, decoder, getReplacement(decoder));
  }

  public CodePointStreamReader(InputStream input, CharsetDecoder decoder, int replacement) {
    reader = new InputStreamReader(input, decoder);
    this.replacement = replacement;
  }

  private static int getReplacement(CharsetDecoder decoder) {
    return decoder
        .replacement()
        .codePoints()
        .reduce(CodePointStreamReader::surrogatePair)
        .orElse('?');
  }

  private static int surrogatePair(int high, int low) {
    return Character.toCodePoint((char) high, (char) low);
  }

  public int read() throws IOException {
    int codePoint = reader.read();
    if (codePoint == -1) {
      return codePoint;
    } else {
      if (Character.isHighSurrogate((char) codePoint)) {
        return surrogatePair(codePoint, reader.read());
      } else if (Character.isLowSurrogate((char) codePoint)) {
        return replacement;
      } else {
        return codePoint;
      }
    }
  }
}
