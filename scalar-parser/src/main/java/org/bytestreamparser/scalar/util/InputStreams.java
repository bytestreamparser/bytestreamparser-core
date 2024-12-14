package org.bytestreamparser.scalar.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetDecoder;

public final class InputStreams {
  private static final String END_OF_BYTE_STREAM_REACHED =
      "End of stream reached after reading %d bytes, bytes expected [%d]";
  private static final String END_OF_CHAR_STREAM_REACHED =
      "End of stream reached after reading %d chars, chars expected [%d]";

  private InputStreams() {}

  public static byte[] readFully(InputStream input, int length) throws IOException {
    byte[] bytes = new byte[length];
    int total = 0;
    while (total < length) {
      int read = input.read(bytes, total, length - total);
      if (read == -1) {
        break;
      } else {
        total += read;
      }
    }
    if (total != length) {
      throw new EOFException(String.format(END_OF_BYTE_STREAM_REACHED, total, length));
    }
    return bytes;
  }

  public static String readFully(InputStream input, int length, CharsetDecoder decoder)
      throws IOException {
    StringBuilder builder = new StringBuilder(length);
    CodePointStreamReader reader = new CodePointStreamReader(input, decoder);
    int read = 0;
    while (read < length) {
      int codePoint = reader.read();
      if (codePoint == -1) {
        throw new EOFException(String.format(END_OF_CHAR_STREAM_REACHED, read, length));
      } else {
        builder.appendCodePoint(codePoint);
        read++;
      }
    }
    return builder.toString();
  }
}
