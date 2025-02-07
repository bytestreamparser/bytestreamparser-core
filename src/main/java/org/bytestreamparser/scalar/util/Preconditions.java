package org.bytestreamparser.scalar.util;

/** Utility class for checking preconditions. */
public final class Preconditions {
  private Preconditions() {}

  /**
   * Checks if the condition is true, otherwise throws an {@link IllegalArgumentException} with the
   * error message.
   *
   * @param condition the condition to check.
   * @param errorTemplate the error message template.
   * @param args the arguments to the error message template.
   */
  public static void check(boolean condition, String errorTemplate, Object... args) {
    if (!condition) {
      throw new IllegalArgumentException(String.format(errorTemplate, args));
    }
  }
}
