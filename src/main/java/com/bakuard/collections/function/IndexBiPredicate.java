package com.bakuard.collections.function;

/**
 * Используется при переборе линейных структур данных для проверки элементов на соответствие некоторому условию.
 */
@FunctionalInterface
public interface IndexBiPredicate<T> {

    /**
     * Проверяет, соответствует ли указанный элемент под указанным индексом некоторому условию.
     * @param item проверяемый элемент.
     * @param index индекс проверяемого элемента.
     */
    public boolean test(T item, int index);
}
