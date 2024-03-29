package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;

import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Общий интерфейс для всех линейных структур данных. Индексация элементов начинается с 0.
 * Все реализации этого интерфейса могут содержать null.
 */
public interface ReadableLinearStructure<T> extends Iterable<T> {

    /**
     * Возвращает элемент по его индексу.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < 0 или index >= {@link #size()}
     */
    public T get(int index);

    /**
     * Возвращает элемент по его индексу. Данный метод представляет расширенную версию
     * метода {@link #get(int)}, которая также может принимать отрицательные значения.
     * Элементу с индексом [-1] соответствует последний элемент, а элементу с индексом
     * [-({@link #size()})] - первый элемент.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -{@link #size()} или index >= {@link #size()}
     */
    public T at(int index);

    /**
     * Возвращает первый элемент. Первому элементу соответствует элемент под индексом [0]. Если
     * структура данных пуста, то возвращает null.
     */
    public default T getFirst() {
        return isEmpty() ? null : get(0);
    }

    /**
     * Возвращает последний элемент. Последнему элементу соответствует элемент под индексом
     * [{@link #size()} - 1]. Если структура данных пуста, то возвращает null.
     */
    public default T getLast() {
        return isEmpty() ? null : get(size() - 1);
    }

    /**
     * Возвращает кол-во элементов.
     */
    public int size();

    /**
     * Возвращает true, если кол-во элементов равно нулю, иначе - false.
     */
    public default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Проверяет - выполняется ли для индекса условие: <br/>
     * index >= 0 && index < {@link #size()}
     * @param index проверяемый индекс.
     * @return true - если описанное выше условие выполняется, иначе - false.
     */
    public default boolean inBound(int index) {
        return index >= 0 && index < size();
    }

    /**
     * Проверяет - выполняется ли для индекса условие: <br/>
     * index >= -({@link #size()}) && index < {@link #size()}. <br/>
     * Данный метод может использоваться для проверки корректности индекса
     * принимаемого методом {@link #at(int)}.
     * @param index проверяемый индекс.
     * @return true - если описанное выше условие выполняется, иначе - false.
     */
    public default boolean inBoundByModulo(int index) {
        return index >= -size() && index < size();
    }

    /**
     * Возвращает индекс первого встретившегося элемента равного заданному
     * значению. Выполняет линейный поиск начиная с элемента {@link #getFirst()}
     * в направлении элемента {@link #getLast()}.
     * Если нет элемента равного заданному значению - возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс первого встретившегося элемента равного значению value.
     */
    public default int linearSearch(T value) {
        return linearSearch(current -> Objects.equals(current, value));
    }

    /**
     * Возвращает индекс первого встретившегося элемента соответствующего заданному
     * предикату. Выполняет линейный поиск начиная с элемента {@link #getFirst()}
     * в направлении элемента {@link #getLast()}.
     * Если нет элемента соответствующего заданному предикату - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
     */
    public default int linearSearch(Predicate<T> predicate) {
        int index = 0;
        while(index < size() && !predicate.test(get(index))) ++index;
        return index >= size() ? -1 : index;
    }

    /**
     * Возвращает индекс первого встретившегося с конца элемента соответствующего заданному
     * предикату. Выполняет линейный поиск начиная с элемента {@link #getLast()}
     * в направлении элемента {@link #getFirst()}.
     * Если нет элемента соответствующего заданному предикату - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося с конца элемента соответствующего заданному предикату.
     */
    public default int linearSearchLast(Predicate<T> predicate) {
        int index = size() - 1;
        while(index >= 0 && !predicate.test(get(index))) --index;
        return index;
    }

    /**
     * Проверяет - содержит ли структура данных элемент с заданным значением. Если это верно - возвращает
     * true, иначе - false.
     * @param value значение искомого элемента.
     */
    public default boolean contains(T value) {
        return linearSearch(value) != -1;
    }

    /**
     * Проверяет - содержит ли структура данных элемент удовлетворяющий заданному предикату.
     * Если это верно - возвращает true, иначе - false.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     */
    public default boolean contains(Predicate<T> predicate) {
        return linearSearch(predicate) != -1;
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    public default int frequency(Predicate<T> predicate) {
        int result = 0;
        for(int i = 0; i < size(); ++i) {
            if(predicate.test(get(i))) ++result;
        }
        return result;
    }

    /**
     * Создает и возвращает копию этой структуры данных, где каждый элемент исходной структуры данных заменен
     * результатом функции mapper.
     * @param mapper функция обратного вызова заменяющая каждый элемент скопированный из исходной структуры данных.
     * @throws ConcurrentModificationException при попытке изменить структуру данных из mapper.
     */
    public <R> ReadableLinearStructure<R> cloneAndMap(IndexBiFunction<T, R> mapper);

    /**
     * Создает и возвращает новый статический массив содержащий все элементы этой структуры данных
     * в том же порядке.
     */
    @SuppressWarnings("unchecked")
    public default T[] toArray(Class<T> itemType) {
        T[] array = (T[]) Array.newInstance(itemType, size());
        forEach((item, index) -> array[index] = item);
        return array;
    }

    /**
     * Создает и возвращает итератор, позволяющий последовательно перебирать линейные структуру данных в
     * обоих направлениях. Сразу после создания, курсор итератора установлен перед элементом {@link #getFirst()}.
     */
    @Override
    public IndexedIterator<T> iterator();

    /**
     * Поведение этого метода расширяет контракт {@link #forEach(Consumer)}. Функция обратного вызова, помимо самих
     * элементов также принимает их индексы.
     * @throws ConcurrentModificationException при попытке изменить структуру данных из action.
     */
    public void forEach(IndexBiConsumer<? super T> action);
}
