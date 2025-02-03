package org.bytestreamparser.scalar.util;

/** Utility class for string operations. */
public final class Strings {
  private Strings() {}

  /**
   * Pads the start of the string with the given padding character until the string reaches the
   * given length.
   *
   * @param value the string to pad.
   * @param length the length to pad the string to.
   * @param padding the character to pad the string with.
   * @return the padded string.
   */
  public static String padStart(String value, int length, char padding) {
    if (value.length() >= length) {
      return value;
    } else {
      return String.valueOf(padding).repeat(length - value.length()) + value;
    }
  }
}
