package com.bakuard.collections;

import java.util.NoSuchElementException;

/**
 * Реализация динамической двусторонней очереди с объектами произвольного типа.
 */
public final class Deque<T> extends Queue<T> {

    public static <T> Deque<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        Deque<T> deque = new Deque<>();
        deque.putAllOnLast(data);
        return deque;
    }

    private static final int MIN_CAPACITY = 10;


    /**
     * Создает пустую двустороннюю очередь.
     */
    @SuppressWarnings("unchecked")
    public Deque() {
        values = (T[]) new Object[MIN_CAPACITY];
    }

    /**
     * Создает копию переданной двусторонней очереди. Выполняет поверхностное копирование.
     * @param other копируемая двусторонняя очередь.
     */
    public Deque(Deque<T> other) {
        this.values = other.values.clone();
        this.firstItemIndex = other.firstItemIndex;
        this.lastItemIndex = other.lastItemIndex;
    }

    /**
     * Добавляет элемент в начало двусторонней очереди увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавляемый элемент.
     */
    public void putFirst(T value) {
        ++actualModCount;

        int currentSize = size();
        grow(currentSize, currentSize + 1);
        firstItemIndex = firstItemIndex - 1 >= 0 ? firstItemIndex - 1 : values.length - 1;
        values[firstItemIndex] = value;
    }

    /**
     * Добавляет каждый элемент из указанной перебираемой структуры данных в начало двусторонней очереди.
     * После добавления элементов в двустороннюю очередь - порядок их следования будет тот же, что и
     * порядок их возвращения итератором.
     * @param iterable структура данных, все элементы которого добавляются в текущую двустороннюю очередь.
     */
    public void putAllOnFirst(Iterable<T> iterable) {
        ++actualModCount;

        Stack<T> stack = new Stack<>();
        stack.putAllOnLast(iterable);

        int currentSize = size();
        grow(currentSize, currentSize + stack.size());
        while(!stack.isEmpty()) {
            firstItemIndex = firstItemIndex - 1 >= 0 ? firstItemIndex - 1 : values.length - 1;
            values[firstItemIndex] = stack.removeLast();
        }
    }

    /**
     * Добавляет каждый элемент из указанного массива в начало двусторонней очереди. После добавления
     * элементов в двустороннюю очередь - порядок их следования будет тот же, что и в массиве.
     * @param data массив, все элементы которого добавляются в текущую двустороннюю очередь.
     */
    public void putAllOnFirst(T... data) {
        ++actualModCount;

        int currentSize = size();
        grow(currentSize, currentSize + data.length);
        for(int i = data.length - 1; i >= 0; --i) {
            firstItemIndex = firstItemIndex - 1 >= 0 ? firstItemIndex - 1 : values.length - 1;
            values[firstItemIndex] = data[i];
        }
    }

    /**
     * Удаляет элемент с конца двусторонней очереди и возвращает его. Если двусторонняя очередь пуста -
     * возвращает null. <br/>
     * <b>ВАЖНО!</b> Т.к. двусторонняя очередь допускает хранение null элементов, то возвращение данным
     * методом null в качестве результата не гарантирует, что двусторонняя очередь пуста. Для проверки
     * наличия элементов в двусторонней очереди используйте методы {@link #size()} или {@link #isEmpty()}.
     */
    public T removeLast() {
        ++actualModCount;

        T result = null;
        if(!isEmpty()) {
            lastItemIndex = lastItemIndex - 1 >= 0 ? lastItemIndex - 1 : values.length - 1;
            result = values[lastItemIndex];
            values[lastItemIndex] = null;
        }

        return result;
    }

    /**
     * Удаляет элемент с конца двусторонней очереди и возвращает его.
     * @throws NoSuchElementException если очередь пуста.
     */
    public T tryRemoveLast() {
        if(isEmpty()) {
            throw new NoSuchElementException("Fail to remove last item: deque is empty.");
        }

        return removeLast();
    }

    public String toString() {
        final int size = size();
        StringBuilder valuesToString = new StringBuilder("[");
        if(size > 0) {
            valuesToString.append(unsafeGet(0));
            for(int i = 1; i < size; ++i) valuesToString.append(',').append(unsafeGet(i));
        }
        valuesToString.append(']');

        return "Deque{size=" + size + ", " + valuesToString + '}';
    }
}
