package org.bytestreamparser.api.testing.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Executable;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class RandomParametersExtension implements ParameterResolver {
  private static final Logger LOGGER = Logger.getLogger(RandomParametersExtension.class.getName());
  static final Map<Class<?>, BiFunction<ParameterContext, ExtensionContext, Object>> GENERATORS =
      Map.ofEntries(
          Map.entry(RandomGenerator.class, RandomParametersExtension::getRandom),
          Map.entry(int.class, RandomParametersExtension::generateInt),
          Map.entry(Integer.class, RandomParametersExtension::generateInt),
          Map.entry(long.class, RandomParametersExtension::generateLong),
          Map.entry(Long.class, RandomParametersExtension::generateLong),
          Map.entry(byte[].class, RandomParametersExtension::generateBytes),
          Map.entry(String.class, RandomParametersExtension::generateString));

  private static Randomize getAnnotation(ParameterContext parameterContext) {
    return parameterContext.getParameter().getAnnotation(Randomize.class);
  }

  private static RandomGenerator getRandom(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    long seed = annotation.seed() == Long.MIN_VALUE ? System.nanoTime() : annotation.seed();
    LOGGER.info(String.format("Using seed %d for %s", seed, getDescription(parameterContext)));
    return new Random(seed);
  }

  private static String getDescription(ParameterContext parameterContext) {
    Executable executable = parameterContext.getParameter().getDeclaringExecutable();
    return new StringJoiner("#")
        .add(executable.getDeclaringClass().getSimpleName())
        .add(executable.getName())
        .add(parameterContext.getParameter().getName())
        .toString();
  }

  private static int generateInt(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    RandomGenerator random = getRandom(parameterContext, extensionContext);
    return random.nextInt(annotation.intMin(), annotation.intMax());
  }

  private static long generateLong(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    RandomGenerator random = getRandom(parameterContext, extensionContext);
    return random.nextLong(annotation.longMin(), annotation.longMax());
  }

  private static byte[] generateBytes(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    RandomGenerator random = getRandom(parameterContext, extensionContext);
    byte[] bytes = new byte[annotation.length()];
    random.nextBytes(bytes);
    return bytes;
  }

  private static String generateString(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    RandomGenerator random = getRandom(parameterContext, extensionContext);
    Set<String> blocks = Set.of(annotation.unicodeBlocks());
    StringBuilder builder = new StringBuilder(annotation.length());
    random
        .ints(Character.MIN_CODE_POINT, Character.MAX_CODE_POINT)
        .filter(codepoint -> validCodePoint(codepoint, blocks))
        .limit(annotation.length())
        .forEach(builder::appendCodePoint);
    return builder.toString();
  }

  private static boolean validCodePoint(int codepoint, Set<String> blocks) {
    return Optional.ofNullable(Character.UnicodeBlock.of(codepoint))
        .map(Character.Subset::toString)
        .filter(blocks::contains)
        .isPresent();
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.isAnnotated(Randomize.class)
        && GENERATORS.containsKey(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return GENERATORS
        .get(parameterContext.getParameter().getType())
        .apply(parameterContext, extensionContext);
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  public @interface Randomize {
    int intMin() default Integer.MIN_VALUE;

    int intMax() default Integer.MAX_VALUE;

    long longMin() default Long.MIN_VALUE;

    long longMax() default Long.MAX_VALUE;

    int length() default 5;

    long seed() default Long.MIN_VALUE;

    String[] unicodeBlocks() default {"BASIC_LATIN"};
  }
}
