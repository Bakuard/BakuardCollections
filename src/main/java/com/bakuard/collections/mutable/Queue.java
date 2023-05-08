package com.bakuard.collections.mutable;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Реализация динамической очереди с объектами произвольного типа.
 */
public class Queue<T> implements Iterable<T> {

    /**
     * Создает и возвращает очередь содержащую указанные элементы в указанном порядке. Итоговая очередь будет содержать
     * копию передаваемого массива, а не сам массив. Длина создаваемого объекта ({@link #size()})
     * будет равна кол-ву передаваемых элементов. Если передаваемый массив не содержит ни одного элемента -
     * создает пустую очередь.
     * @param data элементы включаемые в создаваемую очередь.
     * @throws NullPointerException если передаваемый массив элементов равен null.
     */
    public static <T> Queue<T> of(T... data) {
        return null;
    }


    private int size;
    private int actualModCount;

    /**
     * Создает новую пустую очередь.
     */
    public Queue() {

    }

    /**
     * Создает копию переданной очереди. Выполняет поверхностное копирование.
     * @param other копируемая очередь.
     */
    public Queue(Queue<T> other) {

    }

    /**
     * Добавляет элемент в конец очереди увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавялемый элемент.
     */
    public void putLast(T value) {
        ++actualModCount;
    }

    /**
     * Добавляет каждый элемент из указанной перебираемой структуры данных в конец очереди. Элементы
     * добавляются в порядке их возвращения итератором.
     * @param iterable структура данных, все элементы которой добавляются на вершину текущего стека.
     */
    public void putAllOnLast(Iterable<T> iterable) {
        for(T value: iterable) putLast(value);
    }

    /**
     * Добавляет каждый элемент из указанного массива в конец очереди. Элементы добавляются в порядке
     * их следования в массиве.
     * @param value массив, все элементы которого добавляются в текущую очередь.
     */
    public void putAllOnLast(T... value) {
        ++actualModCount;
    }

    /**
     * Удаляет элемент из начала очереди и возвращает его. Если очередь пуста - возвращает null. <br/>
     * <b>ВАЖНО!</b> Т.к. очередь допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что очередь пуста. Для проверки наличия элементов
     * в очереди используйте методы {@link #size()} или {@link #isEmpty()}.
     */
    public T removeFirst() {
        ++actualModCount;

        return null;
    }

    /**
     * Удаляет элемент из начала очереди и возвращает его.
     * @throws NoSuchElementException если очередь пуста.
     */
    public T tryRemoveFirst() {
        ++actualModCount;

        return null;
    }

    /**
     * Удаляет все элементы из очереди и уменьшает её длину до нуля. Данный метод не уменьшает емкость
     * внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый данным объектом,
     * используйте метод {@link #trimToLength()}.
     */
    public void clear() {
        ++actualModCount;
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Queue.
     * @return true - если объем внутреннего массива был уменьшен, иначе - false.
     */
    public boolean trimToLength() {
        ++actualModCount;

        return false;
    }

    /**
     * Возвращает первый элемент очереди не удаляя его. Если очередь пуста - возвращает null.
     */
    public T getFirst() {
        return null;
    }

    /**
     * Возвращает любой элемент очереди по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует первый элемент очереди, а элементу с индексом [{@link #size()} - 1] - последний элемент
     * очереди. <br/>
     * Метод также допускает отрицательные индексы. Элементу с индексом [-1] соответствует последний элемент,
     * а элементу с индексом [-({@link #size()})] - первый элемент очереди.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    public T at(int index) {
        return null;
    }

    /**
     * Возвращает кол-во элементов очереди.
     */
    public int size() {
        return 0;
    }

    /**
     * Если очередь пуста - возвращает true, иначе - false.
     */
    public boolean isEmpty() {
        return false;
    }

    public int linearSearch(T value) {
        return 0;
    }

    public int linearSearch(Predicate<T> predicate) {
        return 0;
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    public int frequency(Predicate<T> predicate) {
        return 0;
    }

    @Override
    public IndexedIterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {

    }

}
