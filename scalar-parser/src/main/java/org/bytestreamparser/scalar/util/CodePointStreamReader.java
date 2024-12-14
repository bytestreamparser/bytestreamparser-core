package org.bytestreamparser.scalar.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

public class CodePointStreamReader {
  private final InputStream input;
  private final CharsetDecoder decoder;
  private final int maxBytesPerChar;

  public CodePointStreamReader(InputStream input, CharsetDecoder decoder) {
    this.input = input;
    this.decoder = decoder;
    maxBytesPerChar = (int) decoder.charset().newEncoder().maxBytesPerChar();
  }

  private static int surrogatePair(int high, int low) {
    return Character.toCodePoint((char) high, (char) low);
  }

  private static int convertToCodePoint(CharBuffer charBuffer) {
    char ch = charBuffer.flip().get();
    if (Character.isSurrogate(ch)) {
      return surrogatePair(ch, charBuffer.get());
    } else {
      return Character.codePointAt(new char[] {ch}, 0);
    }
  }

  private static void handleCoderResult(CoderResult result) throws CharacterCodingException {
    if (result.isMalformed()) {
      throw new MalformedInputException(result.length());
    } else if (result.isUnmappable()) {
      throw new UnmappableCharacterException(result.length());
    }
  }

  public int read() throws IOException {
    ByteBuffer byteBuffer = ByteBuffer.allocate(maxBytesPerChar * 2);
    CharBuffer charBuffer = CharBuffer.allocate(2);
    int offset = 0;
    while (true) {
      if (input.read(byteBuffer.array(), offset++, 1) == -1) {
        return -1;
      } else {
        CoderResult result = decode(byteBuffer, offset, charBuffer);
        if (charBuffer.position() > 0) {
          return convertToCodePoint(charBuffer);
        } else {
          handleCoderResult(result);
        }
      }
    }
  }

  private CoderResult decode(ByteBuffer byteBuffer, int offset, CharBuffer charBuffer) {
    byteBuffer.limit(offset);
    return decoder.decode(byteBuffer, charBuffer, offset == byteBuffer.capacity());
  }
}
