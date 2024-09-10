package techguns2.util;

import java.util.function.Predicate;

public interface IConsumableIngredient<T> extends Predicate<T>
{
    /**
     * Tests whether or not {@code value} is valid for this
     * ingredient, returning the amount to consume.
     * @param value The value to test
     * @return The amount of {@code value} to consume.
     */
    int testWithConsumption(T value);
}
