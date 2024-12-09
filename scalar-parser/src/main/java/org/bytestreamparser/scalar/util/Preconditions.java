package org.bytestreamparser.scalar.util;

public final class Preconditions {
  private Preconditions() {}

  public static void check(boolean condition, String errorTemplate, Object... args) {
    if (!condition) {
      throw new IllegalArgumentException(String.format(errorTemplate, args));
    }
  }
}
