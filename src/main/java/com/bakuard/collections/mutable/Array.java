package com.bakuard.collections.mutable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

/**
 * Реализация динамического массива с объектами произвольного типа.
 * @param <T> тип объектов хранимых в массиве.
 */
public final class Array<T> implements Iterable<T> {

    private static final int MIN_CAPACITY = 10;

    private T[] values;
    private int length;
    private int actualModCount;

    /**
     * Создает пустой массив нулевой длины.
     * @param type тип объектов хранимых в массиве.
     */
    @SuppressWarnings("unchecked")
    public Array(Class<T> type) {
        values = (T[]) java.lang.reflect.Array.newInstance(type, MIN_CAPACITY);
    }

    /**
     * Создвет пустой массив указанной длины.
     * @param type тип объектов хранимых в массиве.
     * @param length длина массива.
     * @throws IllegalArgumentException если указанная длина меньше нуля.
     */
    @SuppressWarnings("unchecked")
    public Array(Class<T> type, int length){
        if(length < 0)
            throw new IllegalArgumentException("Длина массива не может быть отрицательной.");

        this.length = length;
        values = (T[]) java.lang.reflect.Array.newInstance(type, Math.max(calculateCapacity(length), MIN_CAPACITY));
    }

    /**
     * Создает массив содержащий указанные элемнеты. Итоговый объект Array будет содержать копию передаваемого
     * массива, а не сам массив. Длина создаваемого объекта ({@link #getLength()}) будет равна кол-ву передаваемых
     * элементов. Если передаваемый массив не содержит ни одного элемента - создает пустой объект Array.
     * @param data элементы включаемые в создаваемый объект.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public Array(T... data) {
        if(data == null) throw new NullPointerException("data[] ca not be null.");

        length = data.length;
        values = Arrays.copyOf(data, calculateCapacity(length));
    }

    /**
     * Создает копию указанного массива. Выполняет поверхностное копирование.
     * @param other массив для которого создается копия.
     */
    public Array(Array<T> other) {
        values = other.values.clone();
        length = other.length;
    }

    /**
     * Возвращает элемент хранящийся в ячейке с указаным индексом.
     * @param index индекс ячейки массива.
     * @return элемент хранящийся в ячейке с указаным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T get(int index) {
        halfOpenIntervalCheck(index);
        return values[index];
    }

    /**
     * Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
     * ячейке до вызова этого метода. При вызове данного метода длина массива не изменяется.
     * @param index индекс ячейки массива куда будет записан элемент.
     * @param value добавляемое значение.
     * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T set(int index, T value) {
        halfOpenIntervalCheck(index);

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
    public T setAndExpand(int index, T value) {
        if(index < 0) throw new IndexOutOfBoundsException("index=" + index);
        ++actualModCount;

        expandTo(index + 1);
        T oldValue = values[index];
        values[index] = value;
        return oldValue;
    }

    /**
     * Увеличевает длину массива на единицу и затем записывает элемент в конец массива.
     * @param value добавляемое значение.
     */
    public void add(T value) {
        ++actualModCount;

        int index = length;
        expandTo(length + 1);
        values[index] = value;
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличевая его длину на кол-во переданных элементов.
     * Порядок в котором элементы передаются методу сохраняется.
     * @param data добавляемые элементы.
     */
    public void addAll(T... data) {
        if(data.length > 0) {
            ++actualModCount;

            int lastIndex = length;
            expandTo(length + data.length);
            System.arraycopy(data, 0, this.values, lastIndex, data.length);
        }
    }

    /**
     * Добавляет все переданные элементы в конец массива увеличевая его длину на кол-во переданных элементов.
     * Порядок в котором элементы передаются методу сохраняется.
     * @param data добавляемые элементы.
     */
    public void addAll(Array<T> data) {
        if(data.getLength() > 0) {
            ++actualModCount;

            int lastIndex = length;
            expandTo(length + data.getLength());
            System.arraycopy(data.values, 0, this.values, lastIndex, data.getLength());
        }
    }

    /**
     * Вставляет указанный элемент в указанную позицию. При этом - элемент, который ранее находился на данной
     * позиции и все элементы следующие за ним сдвигаются вверх на одну позицию.
     * @param index позиция, в которую будет добавлен элемент
     * @param value добавляемое значение
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index <= length
     */
    public void insert(int index, T value) {
        closedIntervalCheck(index);

        ++actualModCount;

        int oldLength = length;
        expandTo(length + 1);
        if(index < oldLength) {
            System.arraycopy(values, index, values, index + 1, oldLength - index);
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
     * не соблюдается - результ не определен.
     * @param value добавляемое значение.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс вставки добавляемого элемента.
     */
    public int binaryInsert(T value, Comparator<T> comparator) {
        ++actualModCount;

        int fromIndex = 0;
        int toIndex = length;
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
     * Удаляет элемент под указанным индексом и возвращает его. На место удаленного элемента будет записан
     * последний элемент массива и длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #trimToLength()}. <br/>
     * Данный метод работает быстрее {@link #orderedRemove(int)}. Если порядок элементов в массиве для вас не
     * важен - для удаления рекомендуется использовать этот метод.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T quickRemove(int index) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        T removableItem = values[index];
        values[index] = values[--length];
        values[length] = null;
        return removableItem;
    }

    /**
     * Удаляет элемент под указанным индексом и возвращает его. Все элементы, индекс которых больше указанного,
     * сдвигаются вниз на одну позицию. Иначе  говоря, данный метод выполняет удаление элемента с сохранением
     * порядка для оставшихся элементов. Длина массива будет уменьшена на единицу. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объекм памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #trimToLength()}.
     * @param index индекс удаляемого элемента.
     * @return удаляемый элемент под указанным индексом.
     * @throws IndexOutOfBoundsException если не соблюдается условие index >= 0 && index < length
     */
    public T orderedRemove(int index) {
        halfOpenIntervalCheck(index);

        ++actualModCount;

        T removableItem = values[index];
        if(--length > index) {
            System.arraycopy(values, index + 1, values, index, length - index);
        }
        values[length] = null;
        return removableItem;
    }

    /**
     * Удаляет все элементы массива и уменьшает его длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом {@link Array},
     * используйте метод {@link #trimToLength()}.
     */
    public void clear() {
        ++actualModCount;
        for(int to = length, i = length = 0; i < to; ++i) values[i] = null;
    }

    /**
     * Сортирует массив в соответствии с заданным порядком.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     */
    public void sort(Comparator<T> comparator) {
        ++actualModCount;

        Arrays.sort(values, 0, length, comparator);
    }

    /**
     * Возвращает длину массива. Возвращаемое значение меньше фактической длины внутреннего массива.
     * Это сделано, чтобы избежать слишком частого создания новых внутренних массивов и перезаписи
     * в них значений из старых.
     * @return длина массива.
     */
    public int getLength() {
        return length;
    }

    /**
     * Возвращает индекс первого(с начала массива) встретевшегося элемента с указанным значением или -1,
     * если массив не содержит элемент с указанным значением. Выполняет линейный поиск.
     * @param value значение элеменета, для которого осуществляется поиск.
     * @return индекс первого встретевшегося элемента с указанным значением.
     */
    public int linearSearch(T value) {
        return linearSearchInRange(value, 0, length);
    }

    /**
     * Возвращает индекс первого встретевшегося элемента часть полей которого имеет нужные значение или -1,
     * если массив не содержит такого элемента. Если массив содержит несколько подходящих элементов - метод
     * не дает гарантий, индекс какого именно из этих элементов будет возвращен. Поиск искомого элемента
     * осуществялется с использованием двоичного поиска.<br/>
     * В качестве компаратора выступает объект типа ToIntFunction, а не Comparator. Такое решеие было принято
     * исходя из того, что в некоторых случаях необходимо осущестить бинарный поиск по одному или нескольким полям
     * объекта, по которым для типа объектов содержащихся в массиве задан линейный порядок. При этом у вызывающего
     * кода есть данные поля в виде самостоятельных объектов или примитивов, но нет "цельного" объекта с которым
     * можно было бы осуществить двоичный поиск, а создание фиктивного объекта неудобно или затратно.<br/>
     * Данный метод требует, чтобы массив был предварительно отсортирован и передаваемый компаратор задавал
     * этот же линейный порядок. Если это условие не соблюдается - результ не определен.
     * @param comparator объект выполняющий упорядочивающее сравнение элементов массива.
     * @return индекс элемента часть полей которого имеет нужные значение или -1, если таковой не был найден.
     */
    public int binarySearch(ToIntFunction<T> comparator) {
        return binarySearchByPropertyInRange(0, length, comparator);
    }

    /**
     * Если newLength больше длины массива ({@link #getLength()}), то увеличевает внутреннюю емкость массива
     * таким образом, чтобы вмещать кол-во элементов как минимум равное newLength, а длинна массива станет
     * равна newLength. Если значение newLength меньше или равно длине массива - метод не вносит никаких
     * изменений.
     * @param newLength новая длина массива.
     * @return true - передаваемый аргумент больше длины массива {@link #getLength()}, иначе - false.
     */
    public boolean expandTo(int newLength) {
        boolean isExpand = newLength > length;

        if(isExpand) {
            ++actualModCount;

            length = newLength;
            if(newLength > values.length) {
                values = Arrays.copyOf(values, calculateCapacity(newLength));
            }
        }

        return isExpand;
    }

    /**
     * Если размер внутреннего массива больше его минимально допустимого значения в соответствии с текущей
     * длинной объекта ({@link #getLength()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Array.
     * @return true - если размер внутреннего массива больше его минимально допустимого значения в соответствии
     *                с текущей длинной объекта ({@link #getLength()}), и как следствие объем внутреннего массива
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
     * Выполняет переданную операцию реализованную объектом типа Consumer для каждого элемента
     * хранящегося в массиве. Порядок перебора элементов соответсвует порядку их следования в массиве.
     * @param action действие выполняемое для каждого элемента хранящегося в данном массиве.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < length; i++) {
            action.accept(values[i]);
            if(EXPECTED_COUNT_MOD != actualModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Возвращает итератор для одностороннего перебора элементов данного массива. Порядок перебора соответсвует
     * порядку элементов в массиве.
     * @return итератор для одностороннего перебора элементов данного массива.
     */
    @Override
    public Iterator<T> iterator() {

        return new Iterator<>() {

            private final int EXPECTED_COUNT_MOD = actualModCount;
            private int currentIndex;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Array<?> array = (Array<?>) o;

        if(array.length != length) return false;
        for(int i = 0; i < length; i++) {
            if(!Objects.equals(array.values[i], values[i])) return false;
        }
        return true;
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
        for(int i = 0; i < length; ++i) valuesToString.append(values[i]).append(',');
        valuesToString.deleteCharAt(valuesToString.length() - 1).append(']');

        return "Array{length=" + length + ", " + valuesToString + '}';
    }


    private int calculateCapacity(int length) {
        return length + (length >>> 1);
    }

    private void halfOpenIntervalCheck(int index) {
        if(index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index < length. Actual: length=" + length + ", index=" + index);
        }
    }

    private void closedIntervalCheck(int index) {
        if(index < 0 || index > length) {
            throw new IndexOutOfBoundsException(
                    "Expected: index >= 0 && index <= length. Actual: length=" + length + ", index=" + index);
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

    private int binarySearchByPropertyInRange(int fromIndex, int toIndex, ToIntFunction<T> comparator) {
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

