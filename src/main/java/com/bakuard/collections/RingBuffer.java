package com.bakuard.collections;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация циклического буфера с объектами произвольного типа.
 */
public class RingBuffer<T> implements ReadableLinearStructure<T> {

    /**
     * Создает и возвращает циклический буфер, максимальный размер которого равен кол-ву
     * элементов массива data. Созданный буфер будет содержать все указанные элементы, при этом
     * сохраняется порядок их следования заданный в data. Если массив пустой - будет создан пустой
     * кольцевой буфер максимальный размер которого равен 0.
     * @param data элементы включаемые в создаваемый кольцевой буфер.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> RingBuffer<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        return null;
    }


    private T[] values;
    private int firstItemIndex;
    private int currentSize;
    private int actualModCount;

    @SuppressWarnings("unchecked")
    public RingBuffer(int maxSize) {
        if(maxSize < 0) {
            throw new NegativeArraySizeException(
                    "Expected: maxSize can't be less then zero. Actual: maxSize=" + maxSize);
        }
        values = (T[]) new Object[maxSize];
    }

    public RingBuffer(RingBuffer<T> other) {
        this.values = other.values.clone();
        this.firstItemIndex = other.firstItemIndex;
        this.currentSize = other.currentSize;
    }

    public RingBuffer(Iterable<T> iterable) {

    }

    public T putLastOrReplace(T value) {
        ++actualModCount;

        T rewritingValue = null;
        if(isFull()) {
            rewritingValue = values[firstItemIndex];
            values[firstItemIndex] = value;
            firstItemIndex = (firstItemIndex + 1) % values.length;
        } else {
            values[(firstItemIndex + currentSize++) % values.length] = value;
        }

        return rewritingValue;
    }

    public Array<T> putAllOnLastOrReplace(Iterable<T> iterable) {
        ++actualModCount;

        return null;
    }

    public Array<T> putAllOnLastOrReplace(T... data) {
        ++actualModCount;

        return null;
    }

    public boolean putLastOrSkip(T value) {
        ++actualModCount;

        return false;
    }

    public int putAllOnLastOrSkip(Iterable<T> iterable) {
        ++actualModCount;

        return 0;
    }

    public int putAllOnLastOrSkip(T... data) {
        ++actualModCount;

        return 0;
    }

    public void tryPutLast(T value) {
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

    public void grow(int extraSize) {
        ++actualModCount;
    }

    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public T at(int index) {
        return null;
    }

    @Override
    public T getFirst() {
        return ReadableLinearStructure.super.getFirst();
    }

    @Override
    public T getLast() {
        return ReadableLinearStructure.super.getLast();
    }

    @Override
    public int size() {
        return currentSize;
    }

    public int maxSize() {
        return values.length;
    }

    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    public boolean isFull() {
        return currentSize == values.length;
    }

    @Override
    public int linearSearch(T value) {
        return ReadableLinearStructure.super.linearSearch(value);
    }

    @Override
    public int linearSearch(Predicate<T> predicate) {
        return ReadableLinearStructure.super.linearSearch(predicate);
    }

    @Override
    public int linearSearchLast(Predicate<T> predicate) {
        return ReadableLinearStructure.super.linearSearchLast(predicate);
    }

    @Override
    public boolean contains(T value) {
        return ReadableLinearStructure.super.contains(value);
    }

    @Override
    public boolean contains(Predicate<T> predicate) {
        return ReadableLinearStructure.super.contains(predicate);
    }

    @Override
    public int frequency(Predicate<T> predicate) {
        return ReadableLinearStructure.super.frequency(predicate);
    }

    @Override
    public IndexedIterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        ReadableLinearStructure.super.forEach(action);
    }

}
