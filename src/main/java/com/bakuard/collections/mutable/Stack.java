package com.bakuard.collections.mutable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация динамического стека с объектами произвольного типа.
 */
public class Stack<T> implements Iterable<T> {

    /**
     * Создает и возвращает стек содержащий указанные элементы в указанном порядке. Итоговый стек будет содержать
     * копию передаваемого массива, а не сам массив. Длина создаваемого объекта ({@link #size()})
     * будет равна кол-ву передаваемых элементов. Если передаваемый массив не содержит ни одного элемента -
     * создает пустой стек.
     * @param data элементы включаемые в создаваемый стек.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> Stack<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        Stack<T> result = new Stack<>();
        result.putAllOnLast(data);
        return result;
    }

    private static final int MIN_CAPACITY = 10;


    private T[] values;
    private int length;
    private int actualModCount;

    /**
     * Создает пустой стек.
     */
    @SuppressWarnings("unchecked")
    public Stack() {
        values = (T[]) new Object[MIN_CAPACITY];
    }

    /**
     * Создает копию указанного стека. Выполняет поверхностное копирование.
     * @param other копируемый стек.
     */
    public Stack(Stack<T> other) {
        this.length = other.length;
        this.values = other.values.clone();
    }

    /**
     * Добавляет элемент на вершину стека увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавляемый элемент.
     */
    public void putLast(T value) {
        ++actualModCount;

        int lastIndex = length;
        grow(length + 1);
        values[lastIndex] = value;
    }

    /**
     * Добавляет все элементы из указанной перебираемой структуры данных на вершину стека. Элементы
     * добавляются в порядке из возвращения итератором.
     * @param iterable структура данных, все элементы которой добавляются на вершину текущего стека.
     */
    public void putAllOnLast(Iterable<T> iterable) {
        for(T value : iterable) putLast(value);
    }

    /**
     * Добавляет все элементы из указанного массива на вершину стека. Элементы добавляются
     * в порядке их следования в указанном массиве.
     * @param data массив, все элементы которого добавляются в текущий.
     */
    public void putAllOnLast(T... data) {
        if(data.length > 0) {
            ++actualModCount;

            int lastIndex = length;
            grow(length + data.length);
            System.arraycopy(data, 0, this.values, lastIndex, data.length);
        }
    }

    /**
     * Удаляет элемент с вершины стека и возвращает его. Если стек пуст - возвращает null. <br/>
     * <b>ВАЖНО!</b> Т.к. стек допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что стек пуст. Для проверки наличия элементов
     * в стеке используйте методы {@link #size()} или {@link #isEmpty()}.
     */
    public T removeLast() {
        ++actualModCount;
        return length > 0 ? values[--length] : null;
    }

    /**
     * Удаляет элемент с вершины стека и возвращает его. Если стек пуст, выбрасывает исключение.
     * @throws NoSuchElementException если стек пуст.
     */
    public T tryRemoveLast() {
        if(length == 0) {
            throw new NoSuchElementException("Fail to remove top: stack is empty.");
        }

        return removeLast();
    }

    /**
     * Удаляет все элементы из стека и уменьшает его длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом,
     * используйте метод {@link #trimToLength()}.
     */
    public void clear() {
        ++actualModCount;

        length = 0;
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Stack.
     * @return true - если размер внутреннего массива больше его минимально допустимого значения в соответствии
     *                с текущей длинной объекта ({@link #size()}), и как следствие объем внутреннего массива
     *                был уменьшен, иначе - false.
     */
    public boolean trimToLength() {
        ++actualModCount;

        int capacity = calculateCapacity(length);
        boolean isTrim = capacity < values.length;

        if(isTrim) values = Arrays.copyOf(values, capacity);

        return isTrim;
    }

    /**
     * Возвращает вершину стека не удаляя её. Если стек пуст - возвращает null.
     */
    public T getLast() {
        return length > 0 ? values[length - 1] : null;
    }

    /**
     * Возвращает любой элемент этого стека по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует вершина стека, а элементу с индексом [{@link #size()} - 1] - самый нижний
     * элемент стека. <br/>
     * Метод также допускает отрицательные индексы. Элементу с индексом [-1] соответствует самый
     * нижний элемент стека, а элементу с индексом [-({@link #size()})] - вершина стека.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < [-({@link #size()})] или index >= [{@link #size()}]
     */
    public T get(int index) {
        assertInHalfOpenInterval(index);

        if(index < 0) return values[Math.abs(index) - 1];
        return values[length - 1 - index];
    }

    /**
     * Возвращает кол-во элементов в стеке.
     */
    public int size() {
        return length;
    }

    /**
     * Если стек пуст - возвращает true, иначе - false.
     */
    public boolean isEmpty() {
        return length == 0;
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с самого нижнего элемента стека в направлении вершины стека. Выполняет линейный поиск.
     * @param value искомый элемент
     */
    public int linearSearch(T value) {
        int index = 0;
        while(index < length && !Objects.equals(values[index], value)) ++index;
        return index >= length ? -1 : index;
    }

    /**
     * Возвращает индекс первого встретившегося элемента соответствующего заданному предикату.
     * Если такого элемента нет - возвращает -1. Выполняет линейный поиск начиная с самого нижнего
     * элемента стека в направлении вершины стека. Выполняет линейный поиск.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     */
    public int linearSearch(Predicate<T> predicate) {
        int index = 0;
        while(index < length && !predicate.test(values[index])) ++index;
        return index >= length ? -1 : index;
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    public int frequency(Predicate<T> predicate) {
        int result = 0;
        for(int i = 0; i < length; ++i) {
            if(predicate.test(values[i])) ++result;
        }
        return result;
    }

    /**
     * Возвращает итератор для одностороннего перебора элементов данного стека. Элементы перебираются
     * начиная с нижнего элемента стека в направлении его вершины.
     * @return итератор для одностороннего перебора элементов данного стека.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private final int EXPECTED_COUNT_MOD = actualModCount;
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < length;
            }

            @Override
            public T next() {
                if(EXPECTED_COUNT_MOD != actualModCount) {
                    throw new ConcurrentModificationException();
                } else if(currentIndex >= length) {
                    throw new NoSuchElementException();
                } else {
                    return values[currentIndex++];
                }
            }
        };
    }

    /**
     * Выполняет переданную операцию, реализованную объектом типа Consumer, для каждого элемента
     * хранящегося в стеке. Элементы перебираются начиная с нижнего элемента стека в направлении его
     * вершины.
     * @param action действие выполняемое для каждого элемента хранящегося в данном стеке.
     * @throws ConcurrentModificationException если стек изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < length; ++i) {
            action.accept(values[i]);
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stack<?> stack = (Stack<?>) o;

        boolean result = stack.length == length;
        for(int i = 0; i < length && result; i++) {
            result = Objects.equals(stack.values[i], values[i]);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = length;
        for(int i = 0; i < length; i++) result = result * 31 + Objects.hashCode(values[i]);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder valuesToString = new StringBuilder("[");
        for(int i = length - 1; i >= 0; --i) valuesToString.append(values[i]).append(',');
        valuesToString.deleteCharAt(valuesToString.length() - 1).append(']');

        return "Stack{length=" + length + ", " + valuesToString + '}';
    }


    private int calculateCapacity(int length) {
        return length + (length >>> 1);
    }

    private void grow(int newLength) {
        if(newLength > length) {
            length = newLength;
            if(newLength > values.length) {
                values = Arrays.copyOf(values, calculateCapacity(newLength));
            }
        }
    }

    private void assertInHalfOpenInterval(int index) {
        if(index < -length || index >= length) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= -length && index < length. Actual: length=" + length + ", index=" + index);
        }
    }

}
