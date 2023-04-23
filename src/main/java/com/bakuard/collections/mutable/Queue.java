package com.bakuard.collections.mutable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация динамической очереди с объектами произвольного типа.
 */
public class Queue<T> implements Iterable<T> {

    /**
     * Создает и возвращает очередь содержащую указанные элементы в указанном порядке. Итоговая очередь будет содержать
     * копию передаваемого массива, а не сам массив. Длина создаваемого объекта ({@link #size()})
     * будет равна кол-ву передаваемых элементов. Если передаваемый массив не содержит ни одного элемента -
     * создает пустую очередь.
     * @param data элементы включаемые в создаваемую очередь.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> Queue<T> of(T... data) {
        return null;
    }


    private int size;
    private int actualModCount;

    public Queue() {

    }

    public Queue(Queue<T> other) {

    }

    public void putLast(T value) {
        ++actualModCount;
    }

    public void putAllOnLast(Iterable<T> iterable) {
        for(T value: iterable) putLast(value);
    }

    public void putAllOnLast(T... value) {
        ++actualModCount;
    }

    public T removeFirst() {
        ++actualModCount;

        return null;
    }

    public T tryRemoveFirst() {
        ++actualModCount;

        return null;
    }

    public void clear() {
        ++actualModCount;
    }

    public boolean trimToLength() {
        ++actualModCount;

        return false;
    }

    public T getFirst() {
        return null;
    }

    public T get(int index) {
        return null;
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public int linearSearch(T value) {
        return 0;
    }

    public int linearSearch(Predicate<T> predicate) {
        return 0;
    }

    public int frequency(Predicate<T> predicate) {
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {

    }

}
