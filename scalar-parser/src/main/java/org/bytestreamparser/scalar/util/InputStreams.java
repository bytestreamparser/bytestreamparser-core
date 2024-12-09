package org.bytestreamparser.scalar.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreams {
  private static final String END_OF_BYTE_STREAM_REACHED =
      "End of stream reached after reading %d bytes, bytes expected [%d]";

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
}
