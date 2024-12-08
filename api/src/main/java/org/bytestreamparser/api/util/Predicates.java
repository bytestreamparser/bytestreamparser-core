package org.bytestreamparser.api.util;

import java.util.function.Predicate;

public final class Predicates {
  private Predicates() {}

  public static <T> Predicate<T> alwaysTrue() {
    return ignored -> true;
  }
}
