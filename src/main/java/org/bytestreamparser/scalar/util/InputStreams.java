package org.bytestreamparser.scalar.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetDecoder;

/** Utility class for reading from input streams. */
public final class InputStreams {
  private static final String END_OF_BYTE_STREAM_REACHED =
      "End of stream reached after reading %d bytes, bytes expected [%d]";
  private static final String END_OF_CHAR_STREAM_REACHED =
      "End of stream reached after reading %d chars, chars expected [%d]";

  private InputStreams() {}

  /**
   * Reads the specified number of bytes from the input stream.
   *
   * @param input The input stream to read from.
   * @param length The number of bytes to read. If the input stream does not contain enough bytes,
   *     an {@link EOFException} is thrown.
   * @return The bytes read from the input stream.
   * @throws IOException If an I/O error occurs.
   */
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

  /**
   * Reads the specified number of characters from the input stream.
   *
   * @param input The input stream to read from.
   * @param length The number of characters to read. If the input stream does not contain enough
   *     characters, an {@link EOFException} is thrown.
   * @param decoder The {@link CharsetDecoder} to use for decoding the characters.
   * @return The characters read from the input stream.
   * @throws IOException If an I/O error occurs.
   */
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
