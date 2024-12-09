package org.bytestreamparser.api.testing.extension;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class RandomParametersExtension implements ParameterResolver {
  public static final Map<Class<?>, BiFunction<ParameterContext, ExtensionContext, Object>>
      GENERATORS =
          Map.ofEntries(
              Map.entry(int.class, RandomParametersExtension::generateInt),
              Map.entry(Integer.class, RandomParametersExtension::generateInt),
              Map.entry(byte[].class, RandomParametersExtension::generateBytes),
              Map.entry(String.class, RandomParametersExtension::generateString));

  private static Randomize getAnnotation(ParameterContext parameterContext) {
    return parameterContext.getParameter().getAnnotation(Randomize.class);
  }

  private static Random getRandom(ExtensionContext extensionContext) {
    return extensionContext.getRoot().getStore(GLOBAL).getOrComputeIfAbsent(Random.class);
  }

  private static int generateInt(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    Random random = getRandom(extensionContext);
    return random.nextInt(annotation.intMin(), annotation.intMax());
  }

  private static byte[] generateBytes(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    Randomize annotation = getAnnotation(parameterContext);
    Random random = getRandom(extensionContext);
    byte[] bytes = new byte[annotation.length()];
    random.nextBytes(bytes);
    return bytes;
  }

  private static String generateString(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    return new String(generateBytes(parameterContext, extensionContext));
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

    int length() default 5;
  }
}
