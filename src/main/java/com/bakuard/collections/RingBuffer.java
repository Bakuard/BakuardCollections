package com.bakuard.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
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

        return null;
    }


    private T[] values;
    private int firstItemIndex;
    private int currentSize;
    private int actualModCount;

    /**
     * Создает новый пустой циклический буфер с заданным максимальным размером.
     * @param maxSize максимальный размер циклического буфера.
     */
    @SuppressWarnings("unchecked")
    public RingBuffer(int maxSize) {
        if(maxSize < 0) {
            throw new NegativeArraySizeException(
                    "Expected: maxSize can't be less then zero. Actual: maxSize=" + maxSize);
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

        return null;
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

        return null;
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

        return false;
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

        return 0;
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

        return 0;
    }

    /**
     * Добавляет элемент в конец циклического буфера. Если циклический буфер полон ({@link #isFull()}),
     * то выбрасывает исключение.
     * @param value добавляемый элемент.
     * @throws com.bakuard.collections.exceptions.MaxSizeExceededException если циклический буфер полон.
     */
    public void tryPutLast(T value) {
        ++actualModCount;

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

        return null;
    }

    /**
     * Удаляет элемент из начала циклического буфера и возвращает его.
     * @throws NoSuchElementException если циклический буфер пуст.
     */
    public T tryRemoveFirst() {
        ++actualModCount;

        return null;
    }

    /**
     * Очищает циклический буфер удаляя все его элементы и уменьшая текущий размер до нуля.
     */
    public void clear() {
        ++actualModCount;
    }

    /**
     * Увеличивает максимальный размер ({@link #maxSize()}) циклического буфера на указанную величину.
     */
    public void grow(int extraSize) {
        ++actualModCount;
    }

    /**
     * Возвращает любой элемент циклического буфера по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует первый элемент циклического буфера, а элементу с индексом [{@link #size()} - 1] - последний.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < 0 или index >= {@link #size()}
     */
    @Override
    public T get(int index) {
        return null;
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
        return null;
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
        return ReadableLinearStructure.super.linearSearch(value);
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
        return ReadableLinearStructure.super.linearSearch(predicate);
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
        return ReadableLinearStructure.super.linearSearchLast(predicate);
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    @Override
    public int frequency(Predicate<T> predicate) {
        return ReadableLinearStructure.super.frequency(predicate);
    }

    /**
     * Создает и возвращает итератор, позволяющий последовательно перебрать циклический буфер в обоих направлениях.
     * Сразу после создания, курсор итератора установлен перед первым элементом.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return null;
    }

    /**
     * Выполняет линейный перебор элементов циклического буфера начиная с первого элемента в направлении
     * последнего. При этом для каждого элемента выполняется указанная операция action.
     * @param action действие выполняемое для каждого элемента хранящегося в данной циклическом буфере.
     * @throws ConcurrentModificationException если циклический буфер изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        ReadableLinearStructure.super.forEach(action);
    }
}
