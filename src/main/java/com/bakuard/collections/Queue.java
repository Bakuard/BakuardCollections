package com.bakuard.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация динамической очереди с объектами произвольного типа.
 */
public sealed class Queue<T> implements ReadableLinearStructure<T> permits Deque {

    /**
     * Создает и возвращает очередь содержащую указанные элементы в указанном порядке. Итоговая очередь будет содержать
     * копию передаваемого массива, а не сам массив. Длина создаваемого объекта ({@link #size()})
     * будет равна кол-ву передаваемых элементов. Если передаваемый массив не содержит ни одного элемента -
     * создает пустую очередь.
     * @param data элементы включаемые в создаваемую очередь.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> Queue<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        Queue<T> queue = new Queue<>();
        queue.putAllOnLast(data);
        return queue;
    }

    private static final int MIN_CAPACITY = 10;


    protected T[] values;
    protected int firstItemIndex;
    protected int lastItemIndex;
    protected int actualModCount;

    /**
     * Создает новую пустую очередь.
     */
    @SuppressWarnings("unchecked")
    public Queue() {
        values = (T[]) new Object[MIN_CAPACITY];
    }

    /**
     * Создает копию переданной очереди. Выполняет поверхностное копирование.
     * @param other копируемая очередь.
     */
    public Queue(Queue<T> other) {
        this.values = other.values.clone();
        this.firstItemIndex = other.firstItemIndex;
        this.lastItemIndex = other.lastItemIndex;
    }

    /**
     * Добавляет элемент в конец очереди увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавляемый элемент.
     */
    public void putLast(T value) {
        ++actualModCount;

        int currentSize = size();
        grow(currentSize, currentSize + 1);
        values[lastItemIndex] = value;
        lastItemIndex = ++lastItemIndex % values.length;
    }

    /**
     * Добавляет каждый элемент из указанной перебираемой структуры данных в конец очереди. Элементы
     * добавляются в порядке их возвращения итератором.
     * @param iterable структура данных, все элементы которого добавляются в текущую очередь.
     */
    public void putAllOnLast(Iterable<T> iterable) {
        for(T value: iterable) putLast(value);
    }

    /**
     * Добавляет каждый элемент из указанного массива в конец очереди. Элементы добавляются в порядке
     * их следования в массиве.
     * @param data массив, все элементы которого добавляются в текущую очередь.
     */
    public void putAllOnLast(T... data) {
        ++actualModCount;

        int currentSize = size();
        grow(currentSize, currentSize + data.length);
        for(T item : data) {
            values[lastItemIndex] = item;
            lastItemIndex = ++lastItemIndex % values.length;
        }
    }

    /**
     * Удаляет элемент из начала очереди и возвращает его. Если очередь пуста - возвращает null. <br/>
     * <b>ВАЖНО!</b> Т.к. очередь допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что очередь пуста. Для проверки наличия элементов
     * в очереди используйте методы {@link #size()} или {@link #isEmpty()}.
     */
    public T removeFirst() {
        ++actualModCount;

        T result = null;
        if(!isEmpty()) {
            result = values[firstItemIndex];
            values[firstItemIndex] = null;
            firstItemIndex = ++firstItemIndex % values.length;
        }

        return result;
    }

    /**
     * Удаляет элемент из начала очереди и возвращает его.
     * @throws NoSuchElementException если очередь пуста.
     */
    public T tryRemoveFirst() {
        if(isEmpty()) {
            throw new NoSuchElementException("Fail to remove first item: queue is empty.");
        }

        return removeFirst();
    }

    /**
     * Удаляет все элементы из очереди и уменьшает её длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом,
     * используйте метод {@link #trimToSize()}.
     */
    public void clear() {
        ++actualModCount;

        for(int i = 0; i < values.length; ++i) values[i] = null;
        firstItemIndex = 0;
        lastItemIndex = 0;
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Queue.
     * @return true - если объем внутреннего массива был уменьшен, иначе - false.
     */
    public boolean trimToSize() {
        ++actualModCount;

        int size = size();
        boolean isTrim = size < values.length;

        if(isTrim) repackInnerArray(size, size + 1);

        return isTrim;
    }

    /**
     * Возвращает любой элемент этой очереди по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует первый элемент очереди, а элементу с индексом [{@link #size()} - 1] - последний.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < 0 или index >= {@link #size()}
     */
    @Override
    public T get(int index) {
        assertInBound(index);

        return unsafeGet(index);
    }

    /**
     * Данный метод расширяет поведение метода {@link #get(int)} допуская отрицательные индексы.
     * Элементу с индексом [-1] соответствует последний элемент очереди, а элементу с индексом
     * [-({@link #size()})] - первый элемент очереди.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    @Override
    public T at(int index) {
        assertInBoundByModulo(index);

        return index < 0 ?
                values[(firstItemIndex + size() + index) % values.length] :
                unsafeGet(index);
    }

    /**
     * Возвращает кол-во элементов очереди.
     */
    public int size() {
        int size = lastItemIndex - firstItemIndex;
        if(size < 0) size = values.length + size;
        return size;
    }

    /**
     * Возвращает true, если кол-во элементов равно нулю, иначе - false.
     */
    @Override
    public boolean isEmpty() {
        return lastItemIndex == firstItemIndex;
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с первого элемента очереди в направлении последнего элемента. Если нет элемента
     * равного заданному значению - возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс первого встретившегося элемента с указанным значением.
     */
    @Override
    public int linearSearch(T value) {
        final int size = size();
        int index = 0;
        while(index < size && !Objects.equals(unsafeGet(index), value)) ++index;
        return index >= size ? -1 : index;
    }

    /**
     * Находит и возвращает индекс первого элемента соответствующего заданному предикату. Выполняет линейный
     * поиск начиная с первого элемента очереди в направлении последнего элемента. Если нет подходящего
     * элемента - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
     */
    @Override
    public int linearSearch(Predicate<T> predicate) {
        final int size = size();
        int index = 0;
        while(index < size && !predicate.test(unsafeGet(index))) ++index;
        return index >= size ? -1 : index;
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    @Override
    public int frequency(Predicate<T> predicate) {
        int result = 0;

        for(int i = 0, size = size(); i < size; ++i) {
            if(predicate.test(unsafeGet(i))) ++result;
        }

        return result;
    }

    /**
     * Создает и возвращает итератор, позволяющий последовательно перебрать очередь в обоих направлениях.
     * Сразу после создания, курсор итератора установлен перед элементом {@link #getFirst()}.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return new IndexedIteratorImpl<>(actualModCount, size());
    }

    /**
     * Выполняет линейный перебор элементов очереди начиная с элемента {@link #getFirst()} в направлении
     * элемента {@link #getLast()}. При этом для каждого элемента выполняется указанная операция action.
     * @param action действие выполняемое для каждого элемента хранящегося в данной очереди.
     * @throws ConcurrentModificationException если очередь изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0, size = size(); i < size; ++i) {
            action.accept(unsafeGet(i));
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Queue<?> queue = (Queue<?>) o;

        final int size = size();
        boolean result = queue.size() == size;
        for(int i = 0; i < size && result; i++) {
            result = Objects.equals(queue.unsafeGet(i), unsafeGet(i));
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int size = size();
        int result = size;
        for(int i = 0; i < size; i++) result = result * 31 + Objects.hashCode(unsafeGet(i));
        return result;
    }

    @Override
    public String toString() {
        final int size = size();
        StringBuilder valuesToString = new StringBuilder("[");
        if(size > 0) {
            valuesToString.append(unsafeGet(0));
            for(int i = 1; i < size; ++i) valuesToString.append(',').append(unsafeGet(i));
        }
        valuesToString.append(']');

        return "Queue{size=" + size + ", " + valuesToString + '}';
    }


    protected T unsafeGet(int index) {
        return values[(firstItemIndex + index) % values.length];
    }

    protected void grow(int currentSize, int newSize) {
        if(newSize >= values.length) {
            repackInnerArray(currentSize, calculateCapacity(newSize));
        }
    }

    @SuppressWarnings("unchecked")
    private void repackInnerArray(int currentSize, int newSize) {
        T[] newValues = (T[]) new Object[newSize];

        if(firstItemIndex < lastItemIndex) {
            System.arraycopy(values, firstItemIndex, newValues, 0, currentSize);
        } else if(firstItemIndex > lastItemIndex) {
            int firstHalfSize = values.length - firstItemIndex;
            System.arraycopy(values, firstItemIndex, newValues, 0, firstHalfSize);
            System.arraycopy(values, 0, newValues, firstHalfSize, lastItemIndex);
        }

        values = newValues;
        firstItemIndex = 0;
        lastItemIndex = currentSize;
    }

    private int calculateCapacity(int size) {
        return size + (size >>> 1);
    }

    private void assertInBound(int index) {
        int size = size();
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index < size. Actual: size=" + size + ", index=" + index
            );
        }
    }

    private void assertInBoundByModulo(int index) {
        int size = size();
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
            return (E) Queue.this.unsafeGet(recentIndex);
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
            return (E) Queue.this.unsafeGet(recentIndex);
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
            return (E) Queue.this.unsafeGet(recentIndex);
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
            if(actualModCount != expectedModCount) {
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
