package com.bakuard.collections.immutable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация неизменяемого массива.
 * Неизменяемость в данном случае означает, что методы модифицирующие динамический массив возвращают новый объект
 * содержащий все данные исходного динамического массива с учетом внесенных ихменений, а исходный ассоциативный
 * массив остается неизменным.
 * @param <T> тип значений хранимых в данном массиве.
 */
public final class ImmutableArray<T> implements Iterable<T> {

    private final T[] VALUES;
    private final Class<T> TYPE;

    /**
     * Создает пустой объект ImmutableArray.
     * @param type тип объектов хранимых в массиве.
     */
    @SuppressWarnings("unchecked")
    public ImmutableArray(Class<T> type) {
        if(type != null) {
            TYPE = type;
            VALUES = (T[]) Array.newInstance(type, 0);
        } else {
            throw new IllegalArgumentException("Параметр type не должен равняться null.");
        }
    }

    /**
     * Создает пустой объект ImmutableArray указанной длины.
     * @param type тип объектов хранимых в массиве.
     * @param length длина массива.
     */
    @SuppressWarnings("unchecked")
    public ImmutableArray(Class<T> type, int length) {
        if(length < 0)
            throw new IllegalArgumentException("Длина массива не может быть отрицательной.");

        if(type != null) {
            TYPE = type;
            VALUES = (T[]) Array.newInstance(type, length);
        } else {
            throw new IllegalArgumentException("Параметр type не должен равняться null.");
        }
    }

    /**
     * Создает объект ImmutableArray содержащий все элементы переданной ему реализацией List.
     * @param type тип объектов хранимых в массиве.
     * @param data Реализация List содержащая данные, которые будут добавлены в созданный объект ImmutableArray.
     */
    @SuppressWarnings("unchecked")
    public ImmutableArray(Class<T> type, List<T> data) {
        TYPE = type;

        if(data != null && type != null) {
            VALUES = (T[]) Array.newInstance(type, data.size());
            ListIterator<T> iterator = data.listIterator();
            while(iterator.hasNext()) {
                int index = iterator.nextIndex();
                T value = iterator.next();
                VALUES[index] = value;
            }
        } else {
            throw new IllegalArgumentException("Параметры data и type не должны равняться null.");
        }
    }

    /**
     * Создает объект ImmutableArray содержащий все элементы переданной ему в массиве data.
     * @param type тип объектов хранимых в массиве.
     * @param data массив содержащий данные, которые будут добавлены в созданный объект ImmutableArray.
     */
    @SuppressWarnings("unchecked")
    public ImmutableArray(Class<T> type, T[] data) {
        TYPE = type;

        if(data != null && type != null) {
            VALUES = (T[]) Array.newInstance(type, data.length);
            System.arraycopy(data, 0, VALUES, 0, data.length);
        } else {
            throw new IllegalArgumentException("Параметры data и type не должны равняться null.");
        }
    }

    /**
     * Создает и возвращает копию данного объекта ImmutableArray. При этом возвращаемый объект будет иметь
     * следующие отличия от текущего объекта: значение под указанным индексом будет заменено значением
     * переданным в качестве аргумента данному методу.
     * @param index индекс, по которому будет записано указанное значение.
     * @param value новое значение, которое будет записано по указанному индексу.
     * @return новый объект ImmutableArray.
     * @throws IndexOutOfBoundsException если не выполняется условие: index >= 0 && index < getLength().
     */
    public ImmutableArray<T> set(int index, T value) {
        if(index < 0 || index >= VALUES.length)
            throw new IndexOutOfBoundsException("length=" + VALUES.length + ", index=" + index);

        ImmutableArray<T> copyArray = new ImmutableArray<>(TYPE, VALUES.length);
        System.arraycopy(VALUES, 0, copyArray.VALUES, 0, copyArray.VALUES.length);
        copyArray.VALUES[index] = value;
        return copyArray;
    }

    /**
     * Создает и возвращает копию данного объекта ImmutableArray. При этом возвращаемый объект будет иметь
     * следующие отличия от текущего объекта: длина будет увеличена на единицу и в конец массива будет добавлен
     * новый элемент.
     * @param value значение добавляемое в конец массива.
     * @return новый объект ImmutableArray.
     */
    public ImmutableArray<T> add(T value) {
        ImmutableArray<T> copyArray = new ImmutableArray<>(TYPE, VALUES.length + 1);
        System.arraycopy(VALUES, 0, copyArray.VALUES, 0, VALUES.length);
        copyArray.VALUES[VALUES.length] = value;
        return copyArray;
    }

    /**
     * Создает и возвращает копию данного объекта ImmutableArray. При этом возвращаемый объект будет иметь
     * следующие отличия от текущего объекта: длина нового массива будет уменьшена на единицу и из массива
     * будет удален элемент под указанным индексом.
     * @param index индекс удаляемого элемента.
     * @return новый объект ImmutableArray.
     * @throws IndexOutOfBoundsException если не выполняется условие: index >= 0 && index < getLength().
     */
    public ImmutableArray<T> remove(int index) {
        if(index < 0 || index >= VALUES.length)
            throw new IndexOutOfBoundsException("length=" + VALUES.length + ", index=" + index);

        ImmutableArray<T> copyArray = new ImmutableArray<>(TYPE, VALUES.length - 1);
        System.arraycopy(VALUES, 0, copyArray.VALUES, 0, copyArray.VALUES.length);
        if(index < copyArray.VALUES.length) {
            System.arraycopy(VALUES, index + 1, copyArray.VALUES, index, copyArray.VALUES.length - index);
        }
        return copyArray;
    }

    /**
     * Если в данном массиве присутствует элемент с указанным значением, то возвращает новый объект ImmutableArray
     * являющийся копией текущего объекта, но с удаленным элементом имеющим указанное значение. В противном случае
     * возвращает текущий объект.
     * @param value значение элемента удаляемого из данного объекта ImmutableArray.
     * @return новый объект ImmutableArray или текущий объект.
     */
    public ImmutableArray<T> remove(T value) {
        int index = linearSearch(value);
        if(index != -1) return remove(index);
        else return this;
    }

    /**
     * Возвращает элемент под указанным индексом.
     * @param index индекс возвращаемого элемента.
     * @return элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не выполняется условие: index >= 0 && index < getLength().
     */
    public T get(int index) {
        if(index < 0 || index >= VALUES.length)
            throw new IndexOutOfBoundsException("length=" + VALUES.length + ", index=" + index);

        return VALUES[index];
    }

    /**
     * Возвращает текущую длину массива. Длина массива равна кол-ву его элементов.
     * @return текущая длина массива.
     */
    public int getLength() {
        return VALUES.length;
    }

    /**
     * Возвращает индекс элемента массива имеющего указанное значение. Если в массиве нет такого элемента -
     * возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс элемента массива имеющего указанное значение.
     */
    public int linearSearch(T value) {
        int index = -1;
        for(int i = 0; i < VALUES.length && index == -1; i++) {
            if(Objects.equals(VALUES[i], value)) index = i;
        }
        return index;
    }

    /**
     * Проверяет - содержится ли в массиве элемент с указанным значением.
     * @param value значение искомого элемента.
     * @return true, если массив содержит элемент с указанным значением, иначе - false.
     */
    public boolean contains(T value) {
        return linearSearch(value) >= 0;
    }

    /**
     * Предоставляет декларотивный способ перебора элементов массива.
     * @param action объект реализующий функцию обратного вызова выполняемую для каждого элемента массива.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        for(int i = 0; i < VALUES.length; ++i) action.accept(VALUES[i]);
    }

    /**
     * Предоставляет императивный способ перебора элементов массива. Возвращает одностороний итератор для
     * перебора всех элементов массива.
     * @return односторонний итератор.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < VALUES.length;
            }

            @Override
            public T next() {
                return VALUES[currentIndex++];
            }
        };
    }

    /**
     * Создает и возвращает копию данного объекта ImmutableArray. При этом возвращаемый объект будет иметь
     * следующие отличия от текущего объекта: в новом объекте будут удалены все элементы, для которых переданная
     * функция обратного вызова вернула true.
     * @param predicate объект реализующий функция обратного вызова опеределяющую какой элемент массива следует
     *                  удалить.
     * @return новый объект ImmutableArray.
     */
    public ImmutableArray<T> removeAll(Predicate<T> predicate) {
        ArrayList<T> buffer = new ArrayList<>();
        for(int i = 0; i < VALUES.length; ++i) {
            if(!predicate.test(VALUES[i])) buffer.add(VALUES[i]);
        }
        return new ImmutableArray<>(TYPE, buffer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableArray<?> array = (ImmutableArray<?>) o;

        if(array.VALUES.length != VALUES.length) return false;
        for(int i = 0; i < VALUES.length; i++) {
            if(!Objects.equals(array.VALUES[i], VALUES[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = VALUES.length;
        for(int i = 0; i < VALUES.length; ++i) result = result * 31 + Objects.hashCode(VALUES[i]);
        return result;
    }

    @Override
    public String toString() {
        return "ImmutableArray{" + Arrays.toString(VALUES) + '}';
    }

}
