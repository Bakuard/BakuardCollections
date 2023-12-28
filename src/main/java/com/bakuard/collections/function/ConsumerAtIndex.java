package com.bakuard.collections.function;

/**
 * Используется при переборе линейных структур данных для обработки отдельных элементов.
 */
public interface ConsumerAtIndex<T> {

    /**
     * Обрабатывает указанный элемент под указанным индексом.
     * @param item обрабатываемый элемент.
     * @param index индекс обрабатываемого элемента.
     */
    void accept(T item, int index);
}
