package org.bytestreamparser.scalar.util;

public final class Strings {
  private Strings() {}

  public static String padStart(String value, int length, char padding) {
    if (value.length() >= length) {
      return value;
    } else {
      return String.valueOf(padding).repeat(length - value.length()) + value;
    }
  }
}
