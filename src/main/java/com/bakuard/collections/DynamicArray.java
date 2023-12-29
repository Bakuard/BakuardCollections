package com.bakuard.collections;

import com.bakuard.collections.exceptions.NegativeSizeException;
import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.random.RandomGenerator;

/**
 * Реализация динамического массива с объектами произвольного типа.
 */
public final class DynamicArray<T> implements ReadableLinearStructure<T> {

    /**
     * Создает и возвращает массив содержащий указанные элементы в указанном порядке. Итоговый объект DynamicArray
     * будет содержать копию передаваемого массива, а не сам массив. Длина создаваемого объекта
     * ({@link #size()}) будет равна кол-ву передаваемых элементов. Если передаваемый массив не содержит
     * ни одного элемента - создает пустой объект DynamicArray.
     * @param data элементы включаемые в создаваемый объект.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> DynamicArray<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        DynamicArray<T> result = new DynamicArray<>();
        result.appendAll(data);
        return result;
    }

    private static final int MIN_CAPACITY = 10;


    private T[] values;
    private int size;
    private int actualModCount;

    /**
     * Создает пустой массив нулевой длины.
     */
    @SuppressWarnings("unchecked")
    public DynamicArray() {
        values = (T[]) new Object[MIN_CAPACITY];
    }

     /**
     * Создает пустой массив указанной длины. Все значения в пределах заданного диапазона будут равны null.
     * @param size длина массива.
     * @throws NegativeSizeException если указанная длина меньше нуля.
     */
     @SuppressWarnings("unchecked")
    public DynamicArray(int size){
        assertNotNegativeSize(size);

        this.size = size;
        values = (T[]) new Object[Math.max(calculateCapacity(size), MIN_CAPACITY)];
    }

    /**
     * Создает копию указанного массива. Выполняет поверхностное копирование.
     * @param other массив, для которого создается копия.
     */
    public DynamicArray(DynamicArray<T> other) {
        values = other.values.clone();
        size = other.size;
    }

    /**
     * Создает новый массив копируя в него все элементы iterable в порядке их возвращения итератором.
     * @param iterable структура данных, элементы которой копируются в новый массив.
     */
    public DynamicArray(Iterable<T> iterable) {
        this();
        appendAll(iterable);
    }

    /**
     * Возвращает элемент по его индексу.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < 0 или index >= {@link #size()}
     */
    @Override
    public T get(int index) {
        assertInBound(index);

        return values[index];
    }

    /**
     * Данный метод расширяет поведение метода {@link #get(int)} допуская отрицательные индексы.
     * Элементу с индексом [-1] соответствует последний элемент массива, а элементу с индексом
     * [-({@link #size()})] - первый элемент массива.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    @Override
    public T at(int index) {
        assertInExpandBound(index);

        return index < 0 ? values[size + index] : values[index];
    }

    /**
     * Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
     * ячейке до вызова этого метода. При вызове данного метода длина массива не изменяется.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < size
     */
    public T replace(int index, T value) {
        assertInBound(index);

        ++actualModCount;

        T oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Увеличивает длину массива на единицу и затем записывает элемент в конец массива.
     * @param value добавляемое значение.
     */
    public void append(T value) {
        ++actualModCount;

        int lastIndex = size;
        growToSizeOrDoNothing(size + 1);
        values[lastIndex] = value;
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличивая его длину на кол-во переданных элементов.
     * Порядок, в котором элементы передаются методу, сохраняется.
     * @param data добавляемые элементы.
     */
    public void appendAll(T... data) {
        ++actualModCount;
        if(data.length > 0) {

            int lastIndex = size;
            growToSizeOrDoNothing(size + data.length);
            System.arraycopy(data, 0, this.values, lastIndex, data.length);
        }
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличивая его длину на кол-во переданных элементов.
     * Элементы добавляются в порядке их возвращения итератором.
     * @param iterable структура данных, все элементы которой добавляются в данный массив.
     */
    public void appendAll(Iterable<T> iterable) {
        for(T value : iterable) append(value);
    }

    /**
     * Вставляет указанный элемент в указанную позицию. При этом - элемент, который ранее находился на данной
     * позиции и все элементы следующие за ним сдвигаются вверх на одну позицию.
     * @param index позиция, в которую будет добавлен элемент
     * @param value добавляемое значение
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index <= size
     */
    public void insert(int index, T value) {
        assertInClosedBound(index);

        ++actualModCount;

        int oldSize = size;
        growToSizeOrDoNothing(size + 1);
        if(index < oldSize) {
            System.arraycopy(values, index, values, index + 1, oldSize - index);
        }
        values[index] = value;
    }

    /**
     * Добавляет указанный элемент в массив сохраняя заданный порядок элементов и возвращает индекс
     * добавленного элемента. Если массив содержит несколько элементов с тем же значением, что и добавляемый
     * элемент - метод не дает гарантий, куда будет вставлен элемент относительно элементов с тем же значением.
     * Выполняет вставку элемента с использованием двоичного поиска.<br/>
     * Данный метод требует, чтобы массив был предварительно отсортирован и для сравнения использовался
     * Comparator задающий тот же линейный порядок, что и порядок отсортированного массива. Если это условие
     * не соблюдается - результат не определен.
     * @param value добавляемое значение.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс вставки добавляемого элемента.
     */
    public int binaryInsert(T value, Comparator<T> comparator) {
        ++actualModCount;

        int fromIndex = 0;
        int toIndex = size;
        int middle = 0;
        while (fromIndex < toIndex) {
            middle = (fromIndex + toIndex) >>> 1;
            int different = comparator.compare(value, values[middle]);

            if (different == 0) break;
            else if (different > 0) fromIndex = middle + 1;
            else toIndex = middle;
        }

        if(fromIndex == toIndex) {
            insert(fromIndex, value);
            return fromIndex;
        } else {
            insert(middle, value);
            return middle;
        }
    }

    /**
     * Меняет местами два элемента.
     * @param firstIndex индекс первого элемента
     * @param secondIndex индекс второго элемента
     * @throws IndexOutOfBoundsException если хотя бы для одного из индексов не соблюдается
     *                                   условие index >= 0 && index <= size
     */
    public void swap(int firstIndex, int secondIndex) {
        assertInBound(firstIndex);
        assertInBound(secondIndex);

        ++actualModCount;

        swapAtUncheckedIndexes(firstIndex, secondIndex);
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. На место удаленного элемента будет записан
     * последний элемент массива и длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом {@link DynamicArray},
     * используйте метод {@link #trimToSize()}. <br/>
     * Данный метод работает быстрее {@link #orderedRemove(int)}. Если порядок элементов в массиве для вас не
     * важен - для удаления рекомендуется использовать этот метод.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < size
     */
    public T quickRemove(int index) {
        assertInBound(index);

        ++actualModCount;

        T removableItem = values[index];
        values[index] = values[--size];
        values[size] = null;
        return removableItem;
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. Все элементы, индекс которых больше указанного,
     * сдвигаются вниз на одну позицию. Иначе говоря, данный метод выполняет удаление элемента с сохранением
     * порядка для оставшихся элементов. Длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объект памяти занимаемый данным объектом {@link DynamicArray},
     * используйте метод {@link #trimToSize()}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < size
     */
    public T orderedRemove(int index) {
        assertInBound(index);

        ++actualModCount;

        return orderedRemoveAtUncheckedIndex(index);
    }

    /**
     * Удаляет последний элемент и возвращает его. Если массив пуст - возвращает null.
     * <b>ВАЖНО!</b> Т.к. массив допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что массив пуст. Для проверки наличия элементов
     * в массиве используйте методы {@link #size()} или {@link #isEmpty()}.
     * @return удаленный элемент или null.
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
     * Удаляет все элементы массива удовлетворяющие заданному предикату.
     * @param predicate проверяет, нужно ли удалять элемент. Вызывается для
     *                  каждого элемента массива.
     * @return кол-во удаленных элементов.
     * @throws ConcurrentModificationException при попытке изменить массив из predicate.
     */
    public int removeIf(IndexBiPredicate<T> predicate) {
        final int EXPECTED_COUNT_MOD = ++actualModCount;

        int result = 0;
        for(int i = size - 1; i >= 0; --i) {
            if(predicate.test(values[i], i)) {
                orderedRemoveAtUncheckedIndex(i);
                ++result;
            }
            if(EXPECTED_COUNT_MOD != actualModCount) throw new ConcurrentModificationException();
        }
        return result;
    }

    /**
     * Удаляет все элементы массива и уменьшает его длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом {@link DynamicArray},
     * используйте метод {@link #trimToSize()}.
     */
    public void clear() {
        ++actualModCount;
        for(int to = size, i = size = 0; i < to; ++i) values[i] = null;
    }

    /**
     * Сортирует массив в соответствии с заданным порядком.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     */
    public void sort(Comparator<T> comparator) {
        ++actualModCount;

        Arrays.sort((T[]) values, 0, size, comparator);
    }

    /**
     * Случайным образом меняет элементы местами друг с другом. Использует для выбора новых позиций элементов
     * переданный генератор случайных или псевдослучайных чисел.
     * @param randomGenerator генератор случайных чисел.
     */
    public void shuffle(RandomGenerator randomGenerator) {
        for(int i = 1; i < size; ++i) {
            int randomIndex = randomGenerator.nextInt(size - i) + i;
            swapAtUncheckedIndexes(i, randomIndex);
        }
    }

    /**
     * Возвращает длину массива. Возвращаемое значение меньше фактической длины внутреннего массива.
     * Это сделано, чтобы избежать слишком частого создания новых внутренних массивов и перезаписи
     * в них значений из старых.
     * @return длина массива.
     */
    public int size() {
        return size;
    }

    /**
     * Возвращает true если данный массив пуст, иначе - false.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с первого элемента массива в направлении последнего элемента. Если нет элемента
     * равного заданному значению - возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс первого встретившегося элемента с указанным значением.
     */
    public int linearSearch(T value) {
        return linearSearchInRange(value, 0, size);
    }

    /**
     * Находит и возвращает индекс первого элемента соответствующего заданному предикату. Выполняет линейный
     * поиск начиная с первого элемента массива в направлении последнего элемента. Если нет подходящего
     * элемента - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
     */
    public int linearSearch(Predicate<T> predicate) {
        return linearSearchInRange(predicate, 0, size);
    }

    /**
     * Возвращает индекс первого встретившегося элемента, часть полей которого имеет нужное значение или -1,
     * если массив не содержит такого элемента. Если массив содержит несколько подходящих элементов - метод
     * не дает гарантий, индекс какого именно из этих элементов будет возвращен. Поиск искомого элемента
     * осуществляется с использованием двоичного поиска.
     * <br/><br/>
     * В качестве компаратора выступает объект типа ToIntFunction, а не Comparator. Такое решение было принято
     * исходя из того, что в некоторых случаях необходимо осуществить бинарный поиск по одному или нескольким полям
     * объекта, по которым для типа объектов содержащихся в массиве задан линейный порядок. При этом у вызывающего
     * кода есть данные поля в виде самостоятельных объектов или примитивов, но нет "цельного" объекта, с которым
     * можно было бы осуществить двоичный поиск, а создание фиктивного объекта неудобно или затратно.<br/><br/>
     * Данный метод требует, чтобы массив был предварительно отсортирован, и передаваемый компаратор задавал
     * этот же линейный порядок. Если это условие не соблюдается - результат не определен.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс элемента часть полей которого имеет нужное значение или -1, если таковой не был найден.
     */
    public int binarySearch(ToIntFunction<T> comparator) {
        return binarySearchInRange(0, size, comparator);
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
     * Если newSize больше длины массива ({@link #size()}), то увеличивает внутреннюю емкость массива
     * таким образом, чтобы вмещать кол-во элементов как минимум равное newSize, а длинна массива станет
     * равна newSize. Если значение newSize >= 0 и newSize <= ({@link #size()}) - метод не вносит никаких
     * изменений.
     * @param newSize новая длина массива.
     * @return ссылку на этот же объект.
     */
    public DynamicArray<T> growToSize(int newSize) {
        ++actualModCount;

        assertNotNegativeSize(newSize);
        growToSizeOrDoNothing(newSize);
        return this;
    }

    /**
     * Если index больше или равен длине массива ({@link #size()}), то увеличивает внутреннюю емкость массива
     * таким образом, чтобы вместить элемент с указанным индексом. Если значение index >= 0 и index < ({@link #size()}) -
     * метод не вносит никаких изменений.
     * @param index индекс, до которого увеличивается размер массива.
     * @return ссылку на этот же объект.
     */
    public DynamicArray<T> growToIndex(int index) {
        ++actualModCount;

        assertNotNegativeIndex(index);
        growToSizeOrDoNothing(index + 1);
        return this;
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом DynamicArray.
     * @return true - если объем внутреннего массива был уменьшен, иначе - false.
     */
    public boolean trimToSize() {
        ++actualModCount;

        boolean isTrim = size < values.length;

        if(isTrim) values = Arrays.copyOf(values, size);

        return isTrim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> DynamicArray<R> cloneAndMap(IndexBiFunction<T, R> mapper) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        DynamicArray<R> result = new DynamicArray<>(size);
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
     * Создает и возвращает итератор, позволяющий последовательно перебрать массив в обоих направлениях.
     * Сразу после создания, курсор итератора установлен перед первым элементом.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return new IndexedIteratorImpl<>(actualModCount, size);
    }

    /**
     * Выполняет линейный перебор элементов массива начиная с первого элемента в направлении
     * последнего. При этом для каждого элемента выполняется указанная операция action.
     * @param action действие выполняемое для каждого элемента хранящегося в данном массиве.
     * @throws ConcurrentModificationException если массив изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        forEach((item, index) -> action.accept(item));
    }

    /**
     * {@inheritDoc}
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
        DynamicArray<?> array = (DynamicArray<?>) o;

        boolean result = array.size == size;
        for(int i = 0; i < size && result; ++i) {
            result = Objects.equals(array.values[i], values[i]);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = size;
        for(int i = 0; i < size; ++i) result = result * 31 + Objects.hashCode(values[i]);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder valuesToString = new StringBuilder("[");
        if(size > 0) {
            valuesToString.append(values[0]);
            for(int i = 1; i < size; ++i) valuesToString.append(',').append(values[i]);
        }
        valuesToString.append(']');

        return "DynamicArray{size=" + size + ", " + valuesToString + '}';
    }


    private int calculateCapacity(int size) {
        return size + (size >>> 1);
    }

    private void assertInBound(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index < size. Actual: size=" + size + ", index=" + index);
        }
    }

    private void assertInClosedBound(int index) {
        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index <= size. Actual: size=" + size + ", index=" + index);
        }
    }

    private void assertInExpandBound(int index) {
        if(!inBoundByModulo(index)) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= -size() && index < size. Actual: size=" + size + ", index=" + index);
        }
    }

    private void assertNotNegativeSize(int size) {
        if(size < 0) {
            throw new NegativeSizeException("Expected: size >= 0; Actual: size=" + size);
        }
    }

    private void assertNotNegativeIndex(int index) {
        if(index < 0) {
            throw new IndexOutOfBoundsException("Expected: index >= 0. Actual: index=" + index);
        }
    }

    private int linearSearchInRange(T value, int fromIndex, int toIndex) {
        Object[] vs = values;
        if(value == null) {
            for(int i = fromIndex; i < toIndex; ++i) if(vs[i] == null) return i;
        } else {
            for(int i = fromIndex; i < toIndex; ++i) if(value.equals(vs[i])) return i;
        }
        return -1;
    }

    private int linearSearchInRange(Predicate<T> predicate, int fromIndex, int toIndex) {
        Object[] vs = values;
        int index = fromIndex;
        while(index < toIndex && !predicate.test((T) vs[index])) {
            ++index;
        }
        if(index == toIndex) index = -1;
        return index;
    }

    private int binarySearchInRange(int fromIndex, int toIndex, ToIntFunction<T> comparator) {
        while(fromIndex < toIndex) {
            int middle = (fromIndex + toIndex) >>> 1;
            int different = comparator.applyAsInt(values[middle]);

            if(different == 0) return middle;
            else if(different > 0) fromIndex = middle + 1;
            else toIndex = middle;
        }
        return -1;
    }

    private void growToSizeOrDoNothing(int newSize) {
        if(newSize > size) {
            size = newSize;
            if(newSize > values.length) {
                values = Arrays.copyOf(values, calculateCapacity(newSize));
            }
        }
    }

    private T orderedRemoveAtUncheckedIndex(int index) {
        T removableItem = values[index];
        if(--size > index) {
            System.arraycopy(values, index + 1, values, index, size - index);
        }
        values[size] = null;
        return removableItem;
    }

    private void swapAtUncheckedIndexes(int firstIndex, int secondIndex) {
        T first = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = first;
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
            return (E) DynamicArray.this.get(recentIndex);
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
            return (E) DynamicArray.this.get(recentIndex);
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
            return (E) DynamicArray.this.get(recentIndex);
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
            if(DynamicArray.this.actualModCount != expectedModCount) {
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
