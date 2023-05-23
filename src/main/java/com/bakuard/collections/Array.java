package com.bakuard.collections;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Реализация динамического массива с объектами произвольного типа.
 */
public final class Array<T> implements ReadableLinearStructure<T> {

    /**
     * Создает и возвращает массив содержащий указанные элементы в указанном порядке. Итоговый объект Array
     * будет содержать копию передаваемого массива, а не сам массив. Длина создаваемого объекта
     * ({@link #size()}) будет равна кол-ву передаваемых элементов. Если передаваемый массив не содержит
     * ни одного элемента - создает пустой объект Array.
     * @param data элементы включаемые в создаваемый объект.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> Array<T> of(T... data) {
        if(data == null) throw new NullPointerException("data[] can not be null.");

        Array<T> result = new Array<>();
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
    public Array() {
        values = (T[]) new Object[MIN_CAPACITY];
    }

    /**
     * Создает пустой массив указанной длины.
     * @param size длина массива.
     * @throws IllegalArgumentException если указанная длина меньше нуля.
     */
    @SuppressWarnings("unchecked")
    public Array(int size){
        if(size < 0)
            throw new IllegalArgumentException("Длина массива не может быть отрицательной.");

        this.size = size;
        values = (T[]) new Object[Math.max(calculateCapacity(size), MIN_CAPACITY)];
    }

    /**
     * Создает копию указанного массива. Выполняет поверхностное копирование.
     * @param other массив для которого создается копия.
     */
    public Array(Array<T> other) {
        values = other.values.clone();
        size = other.size;
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
     * Возвращает элемент хранящийся в ячейке с указанным индексом. Первому элементу массива
     * соответствует индекс [0], последнему - индекс [{@link #size()} - 1]. <br/>
     * Метод также допускает отрицательные индексы. Индексу [-1] соответствует последний элемент,
     * а индексу [-({@link #size()})] - первый элемент.
     * @param index индекс ячейки массива.
     * @return элемент хранящийся в ячейке с указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= -({@link #size()})  && index < size
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
     * Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
     * ячейке до вызова этого метода. Если указанный индекс меньше длины массива - то вызов метода не
     * изменяет размер массива. Если указанный индекс больше или равен длине массива - то длина массива
     * станет равна index + 1.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @throws IndexOutOfBoundsException если значение индекса меньше нуля.
     * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
     */
    public T setWithoutBound(int index, T value) {
        if(index < 0) throw new IndexOutOfBoundsException("index=" + index);
        ++actualModCount;

        expandTo(index + 1);
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
        expandTo(size + 1);
        values[lastIndex] = value;
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличивая его длину на кол-во переданных элементов.
     * Порядок в котором элементы передаются методу сохраняется.
     * @param data добавляемые элементы.
     */
    public void appendAll(T... data) {
        if(data.length > 0) {
            ++actualModCount;

            int lastIndex = size;
            expandTo(size + data.length);
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
        expandTo(size + 1);
        if(index < oldSize) {
            System.arraycopy(values, index, values, index + 1, oldSize - index);
        }
        values[index] = value;
    }

    /**
     * Добавляет указанный элемент в массив сохраняя заданный порядок элементов и возвращает индекс
     * вставки добавляемого элемента. Если массив содержит несколько элементов с тем же значением, что и добавляемый
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
     * @throws IndexOutOfBoundsException если хотя бы для одного зи индексов не соблюдается
     *                                   условие index >= 0 && index <= size
     */
    public void swap(int firstIndex, int secondIndex) {
        assertInBound(firstIndex);
        assertInBound(secondIndex);

        ++actualModCount;

        T first = values[firstIndex];
        values[firstIndex] = values[secondIndex];
        values[secondIndex] = first;
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличивая его длину на кол-во переданных элементов.
     * Порядок в котором элементы передаются методу сохраняется.
     * @param array добавляемые элементы.
     */
    public void concat(Array<T> array) {
        if(array.size() > 0) {
            ++actualModCount;

            int lastIndex = size;
            expandTo(size + array.size());
            System.arraycopy(array.values, 0, this.values, lastIndex, array.size());
        }
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. На место удаленного элемента будет записан
     * последний элемент массива и длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #trimToLength()}. <br/>
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
     * внутреннего хранилища. Если вам необходимо уменьшить объект памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #trimToLength()}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < size
     */
    public T orderedRemove(int index) {
        assertInBound(index);

        ++actualModCount;

        T removableItem = values[index];
        if(--size > index) {
            System.arraycopy(values, index + 1, values, index, size - index);
        }
        values[size] = null;
        return removableItem;
    }

    /**
     * Удаляет все элементы массива и уменьшает его длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #trimToLength()}.
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

        Arrays.sort(values, 0, size, comparator);
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
     * Возвращает индекс первого(с начала массива) встретившегося элемента с указанным значением или -1,
     * если массив не содержит элемент с указанным значением. Выполняет линейный поиск.
     * @param value значение элемента, для которого осуществляется поиск.
     * @return индекс первого встретившегося элемента с указанным значением.
     */
    public int linearSearch(T value) {
        return linearSearchInRange(value, 0, size);
    }

    /**
     * Возвращает индекс первого(с начала массива) встретившегося элемента соответствующего заданному
     * предикату. Если такого элемента нет - возвращает -1. Выполняет линейный поиск.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
     */
    public int linearSearch(Predicate<T> predicate) {
        return linearSearchInRange(predicate, 0, size);
    }

    /**
     * Возвращает индекс первого встретившегося элемента часть полей которого имеет нужное значение или -1,
     * если массив не содержит такого элемента. Если массив содержит несколько подходящих элементов - метод
     * не дает гарантий, индекс какого именно из этих элементов будет возвращен. Поиск искомого элемента
     * осуществляется с использованием двоичного поиска.<br/>
     * В качестве компаратора выступает объект типа ToIntFunction, а не Comparator. Такое решение было принято
     * исходя из того, что в некоторых случаях необходимо осуществить бинарный поиск по одному или нескольким полям
     * объекта, по которым для типа объектов содержащихся в массиве задан линейный порядок. При этом у вызывающего
     * кода есть данные поля в виде самостоятельных объектов или примитивов, но нет "цельного" объекта с которым
     * можно было бы осуществить двоичный поиск, а создание фиктивного объекта неудобно или затратно.<br/>
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
     * равна newSize. Если значение newSize меньше или равно длине массива - метод не вносит никаких
     * изменений.
     * @param newSize новая длина массива.
     * @return true - передаваемый аргумент больше длины массива {@link #size()}, иначе - false.
     */
    public boolean expandTo(int newSize) {
        boolean isExpand = newSize > size;

        if(isExpand) {
            ++actualModCount;

            size = newSize;
            if(newSize > values.length) {
                values = Arrays.copyOf(values, calculateCapacity(newSize));
            }
        }

        return isExpand;
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Array.
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
     * Выполняет переданную операцию, реализованную объектом типа Consumer, для каждого элемента
     * хранящегося в массиве. Порядок перебора элементов соответствует порядку их следования в массиве.
     * @param action действие выполняемое для каждого элемента хранящегося в данном массиве.
     * @throws ConcurrentModificationException если массив изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < size; i++) {
            action.accept(values[i]);
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Возвращает итератор для одностороннего перебора элементов данного массива. Порядок перебора соответствует
     * порядку элементов в массиве.
     * @return итератор для одностороннего перебора элементов данного массива.
     */
    @Override
    public IndexedIterator<T> iterator() {

        return null;

        /*return new Iterator<>() {

            private final int EXPECTED_COUNT_MOD = actualModCount;
            private int currentIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                if(EXPECTED_COUNT_MOD != actualModCount) {
                    throw new ConcurrentModificationException();
                } else if(currentIndex >= size) {
                    throw new NoSuchElementException();
                } else {
                    return values[currentIndex++];
                }
            }

        };*/

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Array<?> array = (Array<?>) o;

        boolean result = array.size == size;
        for(int i = 0; i < size && result; i++) {
            result = Objects.equals(array.values[i], values[i]);
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
        for(int i = 0; i < size; ++i) valuesToString.append(values[i]).append(',');
        valuesToString.deleteCharAt(valuesToString.length() - 1).append(']');

        return "Array{size=" + size + ", " + valuesToString + '}';
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
        if(index < -size || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= -size() && index < size. Actual: size=" + size + ", index=" + index);
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

}

