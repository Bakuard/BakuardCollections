package com.bakuard.collections;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация динамического стека с объектами произвольного типа.
 */
public class Stack<T> implements ReadableLinearStructure<T> {

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
    private int size;
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
        this.size = other.size;
        this.values = other.values.clone();
    }

    /**
     * Добавляет элемент на вершину стека увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавляемый элемент.
     */
    public void putLast(T value) {
        ++actualModCount;

        int lastIndex = size;
        grow(size + 1);
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
     * @param data массив, все элементы которого добавляются в текущий стек.
     */
    public void putAllOnLast(T... data) {
        if(data.length > 0) {
            ++actualModCount;

            int lastIndex = size;
            grow(size + data.length);
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
        if(size > 0) {
            T value = values[--size];
            values[size] = null;
            return value;
        }
        return null;
    }

    /**
     * Удаляет элемент с вершины стека и возвращает его. Если стек пуст, выбрасывает исключение.
     * @throws NoSuchElementException если стек пуст.
     */
    public T tryRemoveLast() {
        if(size == 0) {
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
        for(int to = size, i = size = 0; i < to; ++i) values[i] = null;
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Stack.
     * @return true - если объем внутреннего массива был уменьшен, иначе - false.
     */
    public boolean trimToLength() {
        ++actualModCount;

        int capacity = calculateCapacity(size);
        boolean isTrim = capacity < values.length;

        if(isTrim) values = Arrays.copyOf(values, capacity);

        return isTrim;
    }

    /**
     * Возвращает вершину стека не удаляя её. Если стек пуст - возвращает null.
     */
    public T getLast() {
        return size > 0 ? values[size - 1] : null;
    }

    /**
     * Возвращает любой элемент этого стека по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует нижний элемент стека, а элементу с индексом [{@link #size()} - 1] - вершина.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < 0 или index >= {@link #size()}
     */
    public T get(int index) {
        assertInBound(index);

        return values[index];
    }

    /**
     * Возвращает любой элемент этого стека по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует нижний элемент стека, а элементу с индексом [{@link #size()} - 1] - вершина. <br/>
     * Метод также допускает отрицательные индексы. Элементу с индексом [-1] соответствует вершина стека,
     * а элементу с индексом [-({@link #size()})] - нижний элемент стека.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    public T at(int index) {
        assertInExpandBound(index);

        return index < 0 ? values[size + index] : values[index];
    }

    /**
     * Возвращает кол-во элементов в стеке.
     */
    public int size() {
        return size;
    }

    /**
     * Если стек пуст - возвращает true, иначе - false.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с самого нижнего элемента стека в направлении вершины стека. Выполняет линейный поиск.
     * @param value искомый элемент
     */
    public int linearSearch(T value) {
        int index = 0;
        while(index < size && !Objects.equals(values[index], value)) ++index;
        return index >= size ? -1 : index;
    }

    /**
     * Возвращает индекс первого встретившегося элемента соответствующего заданному предикату.
     * Если такого элемента нет - возвращает -1. Выполняет линейный поиск начиная с самого нижнего
     * элемента стека в направлении вершины стека. Выполняет линейный поиск.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     */
    public int linearSearch(Predicate<T> predicate) {
        int index = 0;
        while(index < size && !predicate.test(values[index])) ++index;
        return index >= size ? -1 : index;
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    public int frequency(Predicate<T> predicate) {
        int result = 0;
        for(int i = 0; i < size; ++i) {
            if(predicate.test(values[i])) ++result;
        }
        return result;
    }

    /**
     * Возвращает итератор для одностороннего перебора элементов данного стека. Элементы перебираются
     * в направлении от нижнего элемента к его вершине.
     * @return итератор для одностороннего перебора элементов данного стека.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return new IndexedIteratorImpl<>(actualModCount, size);
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

        for(int i = 0; i < size; ++i) {
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

        boolean result = stack.size == size;
        for(int i = 0; i < size && result; i++) {
            result = Objects.equals(stack.values[i], values[i]);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = size;
        for(int i = 0; i < size; i++) result = result * 31 + Objects.hashCode(values[i]);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder valuesToString = new StringBuilder("[");
        if(size > 0) {
            valuesToString.append(values[size - 1]);
            for(int i = size - 2; i >= 0; --i) valuesToString.append(',').append(values[i]);
        }
        valuesToString.append(']');

        return "Stack{size=" + size + ", " + valuesToString + '}';
    }


    private final class IndexedIteratorImpl<E> implements IndexedIterator<E> {

        private final int expectedModCount;
        private final int totalItems;
        private int cursor;
        private int recentIndex;

        public IndexedIteratorImpl(int actualModCount, int itemsNumber) {
            this.expectedModCount = actualModCount;
            this.totalItems = itemsNumber;
            this.cursor = - 1;
            this.recentIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return cursor + 1 < totalItems;
        }

        @Override
        public E next() {
            assertLinearStructureWasNotBeenChanged();
            assertHasNext();
            recentIndex = ++cursor;
            return (E) Stack.this.get(recentIndex);
        }

        @Override
        public boolean hasPrevious() {
            return cursor >= 0;
        }

        @Override
        public E previous() {
            assertLinearStructureWasNotBeenChanged();
            assertHasPrevious();
            recentIndex = cursor--;
            return (E) Stack.this.get(recentIndex);
        }

        @Override
        public boolean canJump(int itemsNumber) {
            return cursor + itemsNumber >= 0 && cursor + itemsNumber < totalItems;
        }

        @Override
        public E jump(int itemsNumber) {
            assertLinearStructureWasNotBeenChanged();
            assertCanJump(itemsNumber);
            recentIndex = cursor += itemsNumber;
            return (E) Stack.this.get(recentIndex);
        }

        @Override
        public void beforeFirst() {
            cursor = -1;
            recentIndex = -1;
        }

        @Override
        public void afterLast() {
            cursor = totalItems - 1;
            recentIndex = -1;
        }

        @Override
        public int recentIndex() {
            return recentIndex;
        }


        private void assertLinearStructureWasNotBeenChanged() {
            if(Stack.this.actualModCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        private void assertCanJump(int itemsNumber) {
            if(!canJump(itemsNumber)) {
                throw new NoSuchElementException(
                        "There is no item for itemsNumber=%d, totalItems=%d, currentIndex=%d".
                                formatted(itemsNumber, totalItems, cursor)
                );
            }
        }

        private void assertHasNext() {
            if(!hasNext()) {
                throw new NoSuchElementException(
                        "There is no next item for totalItems=%d, currentIndex=%d".
                                formatted(totalItems, cursor)
                );
            }
        }

        private void assertHasPrevious() {
            if(!hasPrevious()) {
                throw new NoSuchElementException(
                        "There is no previous item for totalItems=%d, currentIndex=%d".
                                formatted(totalItems, cursor)
                );
            }
        }

    }

    private int calculateCapacity(int size) {
        return size + (size >>> 1);
    }

    private void grow(int newSize) {
        if(newSize > size) {
            size = newSize;
            if(newSize > values.length) {
                values = Arrays.copyOf(values, calculateCapacity(newSize));
            }
        }
    }

    private void assertInBound(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index < size. Actual: size=" + size + ", index=" + index);
        }
    }

    private void assertInExpandBound(int index) {
        if(index < -size || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= -size && index < size. Actual: size=" + size + ", index=" + index);
        }
    }

}
