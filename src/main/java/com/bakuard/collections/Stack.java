package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

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
        result.addAllOnLast(data);
        return result;
    }


    private DynamicArray<T> array;

    /**
     * Создает пустой стек.
     */
    public Stack() {
        array = new DynamicArray<>();
    }

    /**
     * Создает копию указанного стека. Выполняет поверхностное копирование.
     * @param other копируемый стек.
     */
    public Stack(Stack<T> other) {
        this.array = new DynamicArray<>(other.array);
    }

    /**
     * Создает новый стек копируя в него все элементы iterable в порядке их возвращения итератором.
     * @param iterable структура данных, элементы которой копируются в новый стек.
     */
    public Stack(Iterable<T> iterable) {
        this();
        addAllOnLast(iterable);
    }

    /**
     * Добавляет элемент на вершину стека увеличивая его длину ({@link #size()}) на единицу.
     * Добавляемый элемент может иметь значение null.
     * @param value добавляемый элемент.
     */
    public void addLast(T value) {
        array.addLast(value);
    }

    /**
     * Добавляет все элементы из указанной перебираемой структуры данных на вершину стека. Элементы
     * добавляются в порядке их возвращения итератором.
     * @param iterable структура данных, все элементы которой добавляются на вершину текущего стека.
     */
    public void addAllOnLast(Iterable<T> iterable) {
        array.addAllOnLast(iterable);
    }

    /**
     * Добавляет все элементы из указанного массива на вершину стека. Элементы добавляются
     * в порядке их следования в указанном массиве.
     * @param data массив, все элементы которого добавляются в текущий стек.
     */
    public void addAllOnLast(T... data) {
        array.addAllOnLast(data);
    }

    /**
     * Удаляет элемент с вершины стека и возвращает его. Если стек пуст - возвращает null. <br/>
     * <b>ВАЖНО!</b> Т.к. стек допускает хранение null элементов, то возвращение данным методом
     * null в качестве результата не гарантирует, что стек пуст. Для проверки наличия элементов
     * в стеке используйте методы {@link #size()} или {@link #isEmpty()}.
     */
    public T removeLast() {
        return array.removeLast();
    }

    /**
     * Удаляет элемент с вершины стека и возвращает его. Если стек пуст, выбрасывает исключение.
     * @throws NoSuchElementException если стек пуст.
     */
    public T tryRemoveLast() {
        if(array.isEmpty()) {
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
        array.clear();
    }

    /**
     * Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
     * длинной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
     * никаких изменений. Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем
     * памяти занимаемый объектом Stack.
     * @return true - если объем внутреннего массива был уменьшен, иначе - false.
     */
    public boolean trimToSize() {
        return array.trimToSize();
    }

    /**
     * Возвращает любой элемент этого стека по его индексу, не удаляя его. Элементу с индексом [0]
     * соответствует нижний элемент стека, а элементу с индексом [{@link #size()} - 1] - вершина.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < 0 или index >= {@link #size()}
     */
    public T get(int index) {
        return array.get(index);
    }

    /**
     * Данный метод расширяет поведение метода {@link #get(int)} допуская отрицательные индексы.
     * Элементу с индексом [-1] соответствует вершина стека, а элементу с индексом
     * [-({@link #size()})] - нижний элемент стека.
     * @param index индекс искомого элемента.
     * @throws IndexOutOfBoundsException если index < -({@link #size()}) или index >= {@link #size()}
     */
    public T at(int index) {
        return array.at(index);
    }

    /**
     * Возвращает кол-во элементов в стеке.
     */
    public int size() {
        return array.size();
    }

    /**
     * Находит и возвращает индекс первого элемента равного заданному. Выполняет линейный поиск
     * начиная с самого нижнего элемента стека в направлении вершины стека. Если нет элемента
     * равного заданному значению - возвращает -1.
     * @param value значение искомого элемента.
     * @return индекс первого встретившегося элемента с указанным значением.
     */
    public int linearSearch(T value) {
        return array.linearSearch(value);
    }

    /**
     * Находит и возвращает индекс первого элемента соответствующего заданному предикату. Выполняет линейный
     * поиск начиная с самого нижнего элемента стека в направлении вершины стека. Если нет подходящего
     * элемента - возвращает -1.
     * @param predicate условие, которому должен соответствовать искомый элемент.
     * @return индекс первого встретившегося элемента соответствующего заданному предикату.
     */
    public int linearSearch(Predicate<T> predicate) {
        return array.linearSearch(predicate);
    }

    /**
     * Возвращает кол-во элементов соответствующих заданному предикату.
     */
    public int frequency(Predicate<T> predicate) {
        return array.frequency(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Stack<R> cloneAndMap(IndexBiFunction<T, R> mapper) {
        Stack<R> result = new Stack<>();
        result.array = array.cloneAndMap(mapper);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stack<T> cloneAndFilter(IndexBiPredicate<T> predicate) {
        Stack<T> result = new Stack<>();
        result.array = array.cloneAndFilter(predicate);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T[] toArray(Class<T> itemType) {
        return array.toArray(itemType);
    }

    /**
     * Создает и возвращает итератор, позволяющий последовательно перебрать стек в обоих направлениях.
     * Сразу после создания, курсор итератора установлен перед элементом {@link #getFirst()}.
     */
    @Override
    public IndexedIterator<T> iterator() {
        return array.iterator();
    }

    /**
     * Выполняет линейный перебор элементов стека начиная с элемента {@link #getFirst()} в направлении
     * элемента {@link #getLast()}. При этом для каждого элемента выполняется указанная операция action.
     * @param action действие выполняемое для каждого элемента хранящегося в данном стеке.
     * @throws ConcurrentModificationException если стек изменяется в момент выполнения этого метода.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        array.forEach(action);
    }

    /**
     * Поведение этого метода расширяет контракт {@link #forEach(Consumer)}. Функция обратного вызова, помимо самих
     * элементов также принимает их индексы.
     */
    @Override
    public void forEach(IndexBiConsumer<? super T> action) {
        array.forEach(action);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stack<?> stack = (Stack<?>) o;
        return array.equals(stack.array);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(array);
    }

    @Override
    public String toString() {
        StringBuilder valuesToString = new StringBuilder("[");
        if(!array.isEmpty()) {
            valuesToString.append(array.getLast());
            for(int i = array.size() - 2; i >= 0; --i) valuesToString.append(',').append(array.get(i));
        }
        valuesToString.append(']');

        return "Stack{size=" + array.size() + ", " + valuesToString + '}';
    }
}
