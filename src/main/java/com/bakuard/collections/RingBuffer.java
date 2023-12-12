package com.bakuard.collections;

import com.bakuard.collections.exceptions.MaxSizeExceededException;
import com.bakuard.collections.exceptions.NegativeSizeException;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация циклического буфера с объектами произвольного типа.
 */
public class RingBuffer<T> implements ReadableLinearStructure<T> {

    /**
     * Создает и возвращает циклический буфер, максимальный размер которого равен кол-ву
     * элементов массива data. Созданный буфер будет содержать все указанные элементы, при этом
     * сохраняется порядок их следования заданный в data. Если массив пуст, то будет создан пустой
     * циклический буфер максимальный размер которого равен 0.
     * @param data элементы включаемые в создаваемый циклический буфер.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> RingBuffer<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        RingBuffer<T> result = new RingBuffer<>(data.length);
        result.putAllOnLastOrSkip(data);
        return result;
    }


    private T[] values;
    private int firstItemIndex;
    private int currentSize;
    private int actualModCount;

    /**
     * Создает новый пустой циклический буфер с заданным максимальным размером.
     * @param maxSize максимальный размер циклического буфера.
     * @throws NegativeSizeException если maxSize < 0
     */
    @SuppressWarnings("unchecked")
    public RingBuffer(int maxSize) {
        if(maxSize < 0) {
            throw new NegativeSizeException("Expected: maxSize can't be less then zero. Actual: maxSize=" + maxSize);
        }
        values = (T[]) new Object[maxSize];
    }

    /**
     * Создает копию циклического буфера. Выполняет поверхностное копирование.
     * @param other копируемый циклический буфер.
     */
    public RingBuffer(RingBuffer<T> other) {
        this.values = other.values.clone();
        this.firstItemIndex = other.firstItemIndex;
        this.currentSize = other.currentSize;
    }

    /**
     * Создает новый циклический буфер копируя в него все элементы iterable в порядке их возвращения итератором.
     * Максимальный размер буфера будет равен кол-ву элементов возвращенных итератором.
     * @param iterable структура данных, элементы которой копируются в новый буфер.
     */
    public RingBuffer(Iterable<T> iterable) {
        this(0);

        Array<T> tempBuffer = new Array<>();
        tempBuffer.appendAll(iterable);

        if(!tempBuffer.isEmpty()) {
            values = tempBuffer.toArray();
            currentSize = tempBuffer.size();
        }
    }

    /**
     * Добавляет элемент в конец циклического буфера. Если на момент вызова этого метода циклический буфер полон
     * (см. {@link #isFull()}), то первый элемент будет перезаписан, а его исходное значение возращено данным методом.
     * В противном случае текущий размер буфера будет увеличен на единицу, а данный метод вернет null.
     * <br/><br/>
     * <b>ВАЖНО!</b> Т.к. циклический буфер допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что буфер НЕ заполнен. Для проверки текущего размера буфера, а также
     * заполнен он или же является пустым, используйте методы {@link #size()}, {@link #maxSize()}, {@link #isEmpty()}
     * или {@link #isFull()}.
     * @param value добавляемый элемент.
     */
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

    /**
     * Добавляет в конец циклического буфера все элементы возвращаемые итератором. Порядок добавления элементов
     * соответствует порядку их возвращения итератором. Для каждого добавляемого элемента выполняется порядок
     * действий описанный для метода {@link #putLastOrReplace(Object)}. Возвращает все перезаписанные элементы.
     * @param iterable структура данных, все элементы которого добавляются в текущий циклический буфер.
     * @return все перезаписанные элементы.
     */
    public Array<T> putAllOnLastOrReplace(Iterable<T> iterable) {
        ++actualModCount;

        Array<T> rewritingValues = new Array<>();
        for(T value : iterable) {
            boolean valueWasRewriting = isFull();
            T rewritingValue = putLastOrReplace(value);
            if(valueWasRewriting) rewritingValues.append(rewritingValue);
        }

        return rewritingValues;
    }

    /**
     * Добавляет в конец циклического буфера все элементы из массива data. Порядок добавления соответствует порядку
     * следования элементов в массиве. Для каждого добавляемого элемента выполняется порядок действий описанный
     * для метода {@link #putLastOrReplace(Object)}. Возвращает все перезаписанные элементы.
     * @param data массив, все элементы которого добавляются в текущий циклический буфер.
     * @return все перезаписанные элементы.
     */
    public Array<T> putAllOnLastOrReplace(T... data) {
        ++actualModCount;

        Array<T> rewritingValues = new Array<>();
        for(T value : data) {
            boolean valueWasRewriting = isFull();
            T rewritingValue = putLastOrReplace(value);
            if(valueWasRewriting) rewritingValues.append(rewritingValue);
        }

        return rewritingValues;
    }

    /**
     * Пробует добавить элемент в конец циклического буфера. Если на момент вызова этого метода циклический буфер полон
     * (см. {@link #isFull()}), то элемент не будет добавлен, а метод вернет false. В противном случае элемент будет
     * добавлен в конец циклического буфера, текущий размер буфера будет увеличен на единицу, а данный метод вернет true.
     * @param value добавляемый элемент.
     * @return true - если удалось добавить элемент, иначе - false.
     */
    public boolean putLastOrSkip(T value) {
        ++actualModCount;

        boolean canBeAdded = !isFull();
        if(canBeAdded) values[(firstItemIndex + currentSize++) % values.length] = value;
        return canBeAdded;
    }

    /**
     * Пробует добавить в конец циклического буфера все элементы возвращаемые итератором. Порядок добавления элементов
     * соответствует порядку их возвращения итератором. Для каждого добавляемого элемента выполняется порядок
     * действий описанный для метода {@link #putLastOrSkip(Object)}. Возвращает кол-во элементов, которые удалось
     * добавить.
     * @param iterable структура данных, все элементы которого добавляются в текущий циклический буфер.
     * @return кол-во элементов, которые удалось добавить.
     */
    public int putAllOnLastOrSkip(Iterable<T> iterable) {
        ++actualModCount;

        int addedValuesNumber = 0;
        boolean wasAdded = true;
        Iterator<T> iterator = iterable.iterator();
        while(wasAdded && iterator.hasNext()) {
            wasAdded = putLastOrSkip(iterator.next());
            if(wasAdded) ++addedValuesNumber;
        }

        return addedValuesNumber;
    }

    /**
     * Пробует добавить в конец циклического буфера все элементы массива data. Порядок добавления элементов
     * соответствует порядку их следования в массиве. Для каждого добавляемого элемента выполняется порядок
     * действий описанный для метода {@link #putLastOrSkip(Object)}. Возвращает кол-во элементов, которые удалось
     * добавить.
     * @param data массив, все элементы которого добавляются в текущий циклический буфер.
     * @return кол-во элементов, которые удалось добавить.
     */
    public int putAllOnLastOrSkip(T... data) {
        ++actualModCount;

        final int addedValuesNumber = values.length - currentSize;
        for(int i = 0; i < addedValuesNumber && i < data.length; i++) {
            putLastOrSkip(data[i]);
        }

        return Math.min(addedValuesNumber, data.length);
    }

    /**
     * Добавляет элемент в конец циклического буфера. Если циклический буфер полон ({@link #isFull()}),
     * то выбрасывает исключение.
     * @param value добавляемый элемент.
     * @throws com.bakuard.collections.exceptions.MaxSizeExceededException если циклический буфер полон.
     */
    public void tryPutLast(T value) {
        ++actualModCount;

        if(isFull()) {
            throw new MaxSizeExceededException(
                    "Adding items to a filled RingBuffer with the 'tryPutLast' method is prohibited. MaxSize: " +
                            maxSize() + ", added item: " + value
            );
        }
        values[(firstItemIndex + currentSize++) % values.length] = value;
    }

    /**
     * Удаляет элемент из начала циклического буфера и возвращает его. Если циклический буфер пуст, то возвращает null.
     * <br/><br/>
     * <b>ВАЖНО!</b> Т.к. циклический буфер допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что буфер не пуст. Для проверки текущего размера буфера, а также
     * заполнен он или же является пустым, используйте методы {@link #size()}, {@link #maxSize()}, {@link #isEmpty()}
     * или {@link #isFull()}.
     */
    public T removeFirst() {
        ++actualModCount;

        T removedValue = null;
        if(!isEmpty()) {
            removedValue = values[firstItemIndex];
            values[firstItemIndex] = null;
            firstItemIndex = (firstItemIndex + 1) % values.length;
            --currentSize;
        }
        return removedValue;
    }

    /**
     * Удаляет элемент из начала циклического буфера и возвращает его.
     * @throws NoSuchElementException если циклический буфер пуст.
     */
    public T tryRemoveFirst() {
        if(isEmpty()) {
            throw new NoSuchElementException("Fail to remove first item: ring buffer is empty.");
        }

        return removeFirst();
    }

    /**
     * Очищает циклический буфер удаляя все его элементы и уменьшая текущий размер до нуля.
     */
    public void clear() {
        ++actualModCount;

        for(int i = 0; i < values.length; ++i) values[i] = null;
        currentSize = 0;
        firstItemIndex = 0;
    }

    /**
     * Увеличивает максимальный размер ({@link #maxSize()}) циклического буфера на указанную величину.
     * @throws NegativeSizeException если extraSize < 0
     */
    @SuppressWarnings("unchecked")
    public void grow(int extraSize) {
        ++actualModCount;

        if(extraSize > 0) {
            T[] newValues = (T[]) new Object[values.length + extraSize];

            if(currentSize > 0) {
                int lengthBeforeWrap = values.length - firstItemIndex;
                System.arraycopy(values, firstItemIndex, newValues, 0, lengthBeforeWrap);
                System.arraycopy(values, 0, newValues, lengthBeforeWrap, values.length - lengthBeforeWrap);
            }

            values = newValues;
            firstItemIndex = 0;
        } else if(extraSize < 0) {
            throw new NegativeSizeException("Expected: extraSize can't be negative. Actual: extraSize = " + extraSize);
        }
    }

    /**
     * Возвращает любой элемент циклического буфера по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует первый элемент циклического буфера, а элементу с индексом [{@link #size()} - 1] - последний.
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
     * Элементу с индексом [-1] соответствует последний элемент циклического буфера, а элементу с индексом
     * [-({@link #size()})] - первый элемент.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    @Override
    public T at(int index) {
        assertInBoundByModulo(index);

        return index < 0 ?
                values[(firstItemIndex + currentSize + index) % values.length] :
                unsafeGet(index);
    }

    /**
     * Возвращает текущее кол-во элементов циклического буфера.
     */
    @Override
    public int size() {
        return currentSize;
    }

    /**
     * Возвращает максимально возможное кол-во элементов, которое может хранить циклический буфер.
     */
    public int maxSize() {
        return values.length;
    }

    /**
     * Возвращает true, если текущее кол-во элементов (см. {@link #size()}) равно 0. Иначе возвращает false.
     */
    @Override
    public boolean isEmpty() {
        return currentSize == 0;
    }

    /**
     * Возвращает true, если текущее кол-во элементов (см. {@link #size()}) равно максимально возможному
     * (см. {@link #maxSize()}). Иначе возвращает false.
     */
    public boolean isFull() {
        return currentSize == values.length;
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с первого элемента циклического буфера в направлении последнего элемента. Если нет элемента
     * равного заданному значению - возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс первого встретившегося элемента с указанным значением.
     */
    @Override
    public int linearSearch(T value) {
        int index = 0;
        while(index < currentSize && !Objects.equals(unsafeGet(index), value)) ++index;
        return index >= currentSize ? -1 : index;
    }

    /**
     * Находит и возвращает индекс первого элемента соответствующего заданному предикату. Выполняет линейный
     * поиск начиная с первого элемента циклического буфера в направлении последнего элемента. Если нет подходящего
     * элемента - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
     */
    @Override
    public int linearSearch(Predicate<T> predicate) {
        int index = 0;
        while(index < currentSize && !predicate.test(unsafeGet(index))) ++index;
        return index >= currentSize ? -1 : index;
    }

    /**
     * Находит и возвращает индекс первого встретившегося с конца элемента соответствующего заданному предикату.
     * Выполняет линейный поиск начиная с последнего элемента циклического буфера в направлении первого элемента.
     * Если нет подходящего элемента - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося с конца элемента соответствующего заданному предикату.
     */
    @Override
    public int linearSearchLast(Predicate<T> predicate) {
        int index = currentSize - 1;
        while(index >= 0 && !predicate.test(unsafeGet(index))) --index;
        return index;
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    @Override
    public int frequency(Predicate<T> predicate) {
        int result = 0;

        for(int i = 0; i < currentSize; ++i) {
            if(predicate.test(unsafeGet(i))) ++result;
        }

        return result;
    }

    /**
     * Создает и возвращает итератор, позволяющий последовательно перебрать циклический буфер в обоих направлениях.
     * Сразу после создания, курсор итератора установлен перед первым элементом.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return new IndexedIteratorImpl<>(actualModCount, currentSize);
    }

    /**
     * Выполняет линейный перебор элементов циклического буфера начиная с первого элемента в направлении
     * последнего. При этом для каждого элемента выполняется указанная операция action.
     * @param action действие выполняемое для каждого элемента хранящегося в данной циклическом буфере.
     * @throws ConcurrentModificationException если циклический буфер изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < currentSize; ++i) {
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
        RingBuffer<?> buffer = (RingBuffer<?>) o;

        boolean result = buffer.currentSize == currentSize;
        for(int i = 0; i < currentSize && result; i++) {
            result = Objects.equals(buffer.unsafeGet(i), unsafeGet(i));
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = currentSize;
        for(int i = 0; i < currentSize; i++) result = result * 31 + Objects.hashCode(unsafeGet(i));
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

        return "RingBuffer{currentSize=" + currentSize + ", " + valuesToString + '}';
    }


    protected T unsafeGet(int index) {
        return values[(firstItemIndex + index) % values.length];
    }

    private void assertInBound(int index) {
        if(index < 0 || index >= currentSize) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index < currentSize. Actual: currentSize=" + currentSize + ", index=" + index
            );
        }
    }

    private void assertInBoundByModulo(int index) {
        if(index < -currentSize || index >= currentSize) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= -currentSize && index < currentSize. Actual: currentSize=" + currentSize + ", index=" + index
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
            return (E) RingBuffer.this.unsafeGet(recentIndex);
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
            return (E) RingBuffer.this.unsafeGet(recentIndex);
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
            return (E) RingBuffer.this.unsafeGet(recentIndex);
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
