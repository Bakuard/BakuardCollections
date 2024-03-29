package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация динамического стека с объектами произвольного типа.
 */
public final class Stack<T> implements ReadableLinearStructure<T> {

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
    public Stack() {
        this(MIN_CAPACITY, 0);
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
     * Создает новый стек копируя в него все элементы iterable в порядке их возвращения итератором.
     * @param iterable структура данных, элементы которой копируются в новый стек.
     */
    public Stack(Iterable<T> iterable) {
        this();
        putAllOnLast(iterable);
    }

    @SuppressWarnings("unchecked")
    private Stack(int capacity, int size) {
        this.values = (T[]) new Object[capacity];
        this.size = size;
    }

    /**
     * Добавляет элемент на вершину стека увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавляемый элемент.
     */
    public void putLast(T value) {
        ++actualModCount;

        int lastIndex = size;
        growToSizeOrDoNothing(size + 1);
        values[lastIndex] = value;
    }

    /**
     * Добавляет все элементы из указанной перебираемой структуры данных на вершину стека. Элементы
     * добавляются в порядке их возвращения итератором.
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
            growToSizeOrDoNothing(size + data.length);
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
            T removableItem = values[--size];
            values[size] = null;
            return removableItem;
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
     * используйте метод {@link #trimToSize()}.
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
    public boolean trimToSize() {
        ++actualModCount;

        boolean isTrim = size < values.length;

        if(isTrim) values = Arrays.copyOf(values, size);

        return isTrim;
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
     * Данный метод расширяет поведение метода {@link #get(int)} допуская отрицательные индексы.
     * Элементу с индексом [-1] соответствует вершина стека, а элементу с индексом
     * [-({@link #size()})] - нижний элемент стека.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    public T at(int index) {
        assertInBoundByModulo(index);

        return index < 0 ? values[size + index] : values[index];
    }

    /**
     * Возвращает кол-во элементов в стеке.
     */
    public int size() {
        return size;
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с самого нижнего элемента стека в направлении вершины стека. Если нет элемента
     * равного заданному значению - возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс первого встретившегося элемента с указанным значением.
     */
    public int linearSearch(T value) {
        int index = 0;
        while(index < size && !Objects.equals(values[index], value)) ++index;
        return index >= size ? -1 : index;
    }

    /**
     * Находит и возвращает индекс первого элемента соответствующего заданному предикату. Выполняет линейный
     * поиск начиная с самого нижнего элемента стека в направлении вершины стека. Если нет подходящего
     * элемента - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
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
     * {@inheritDoc}
     */
    @Override
    public <R> Stack<R> cloneAndMap(IndexBiFunction<T, R> mapper) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        Stack<R> result = new Stack<>(size, size);
        for(int i = 0; i < size; ++i) {
            result.values[i] = mapper.apply(values[i], i);
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException();
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public T[] toArray(Class<T> itemType) {
        T[] result = (T[]) Array.newInstance(itemType, size);
        System.arraycopy(values, 0, result, 0, size);
        return result;
    }

    /**
     * Создает и возвращает итератор, позволяющий последовательно перебрать стек в обоих направлениях.
     * Сразу после создания, курсор итератора установлен перед элементом {@link #getFirst()}.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return new IndexedIteratorImpl<>(actualModCount, size);
    }

    /**
     * Выполняет линейный перебор элементов стека начиная с элемента {@link #getFirst()} в направлении
     * элемента {@link #getLast()}. При этом для каждого элемента выполняется указанная операция action.
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

    /**
     * Поведение этого метода расширяет контракт {@link #forEach(Consumer)}. Функция обратного вызова, помимо самих
     * элементов также принимает их индексы.
     */
    @Override
    public void forEach(IndexBiConsumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < size; ++i) {
            action.accept(values[i], i);
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


    private int calculateCapacity(int size) {
        return size + (size >>> 1);
    }

    private void growToSizeOrDoNothing(int newSize) {
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
                    "Expected: index >= 0 && index < size. Actual: size=" + size + ", index=" + index
            );
        }
    }

    private void assertInBoundByModulo(int index) {
        if(index < -size || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= -size && index < size. Actual: size=" + size + ", index=" + index
            );
        }
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
                        "There is no item for jump. Detail: itemsNumber=%d, totalItems=%d, currentIndex=%d".
                                formatted(itemsNumber, totalItems, cursor)
                );
            }
        }

        private void assertHasNext() {
            if(!hasNext()) {
                throw new NoSuchElementException(
                        "There is no next item. Detail: totalItems=%d, currentIndex=%d".
                                formatted(totalItems, cursor)
                );
            }
        }

        private void assertHasPrevious() {
            if(!hasPrevious()) {
                throw new NoSuchElementException(
                        "There is no previous item. Detail: totalItems=%d, currentIndex=%d".
                                formatted(totalItems, cursor)
                );
            }
        }

    }

}
