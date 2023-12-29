package com.bakuard.collections.function;

/**
 * Представляет функция двух аргументов, где первый аргумент это элемент линейной структуры данных, а второй - индекс
 * этого элемента.
 */
@FunctionalInterface
public interface IndexBiFunction<T, R> {

    /**
     * Применяет данную функцию к элементу и его индексу и возвращает результат.
     * @param item элемент линейной структуры данных.
     * @param index индекс элемента в линейной структуре данных.
     */
    public R apply(T item, int index);
}
