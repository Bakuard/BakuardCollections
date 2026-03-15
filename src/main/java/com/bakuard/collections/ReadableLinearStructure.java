package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>Общий интерфейс для всех линейных структур данных.</p>
 *
 * <p>Первому элементу соответствует элемент под индексом [0]. Все реализации этого интерфейса могут содержать null.</p>
 */
public interface ReadableLinearStructure<T> extends Iterable<T> {

	/**
	 * Возвращает элемент по его индексу.
	 * @param index индекс искомого элемента.
	 * @throws IndexOutOfBoundsException если {@code index < 0 или index >=} {@link #size()}
	 */
	public T get(int index);

	/**
	 * <p>Возвращает элемент по его индексу.</p>
	 *
	 * <p>Данный метод представляет расширенную версию метода {@link #get(int)}, которая также может принимать отрицательные значения.
	 * Элементу с индексом [-1] соответствует последний элемент, а элементу с индексом [-({@link #size()})] - первый элемент.</p>
	 *
	 * @param index индекс искомого элемента.
	 * @throws IndexOutOfBoundsException если {@code index < -}{@link #size()} или {@code index >= } {@link #size()}
	 */
	public T at(int index);

	/**
	 * <p>Возвращает первый элемент (элемент под индексом [0]).</p>
	 *
	 * <p>Если структура данных пуста, то возвращает null.</p>
	 */
	public default T getFirst() {
		return isEmpty() ? null : get(0);
	}

	/**
	 * <p>Возвращает последний элемент (элемент под индексом [{@link #size()} - 1]).</p>
	 *
	 * <p>Если структура данных пуста, то возвращает null.</p>
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
	 * Проверяет - выполняется ли для индекса условие: {@code index >= 0 && index < } {@link #size()}
	 * @param index проверяемый индекс.
	 * @return true - если описанное выше условие выполняется, иначе - false.
	 */
	public default boolean inBound(int index) {
		return index >= 0 && index < size();
	}

	/**
	 * <p>Проверяет - выполняется ли для индекса условие: {@code index >= -}{@link #size()} {@code && index < } {@link #size()}.</p>
	 *
	 * <p>Данный метод может использоваться для проверки корректности индекса принимаемого методом {@link #at(int)}.</p>
	 *
	 * @param index проверяемый индекс.
	 * @return true - если описанное выше условие выполняется, иначе - false.
	 */
	public default boolean inBoundByModulo(int index) {
		return index >= -size() && index < size();
	}

	/**
	 * <p>Возвращает индекс первого встретившегося элемента равного заданному значению.</p>
	 *
	 * <p>Выполняет линейный поиск начиная с элемента {@link #getFirst()} в направлении элемента {@link #getLast()}.</p>
	 *
	 * <p>Если нет элемента равного заданному значению - возвращает -1.</p>
	 *
	 * @param value значение искомого элемента.
	 * @return индекс первого встретившегося элемента равного значению value.
	 */
	public default int linearSearch(T value) {
		return linearSearch(current -> Objects.equals(current, value));
	}

	/**
	 * <p>Возвращает индекс первого встретившегося элемента соответствующего заданному предикату.</p>
	 *
	 * <p>Выполняет линейный поиск начиная с элемента {@link #getFirst()} в направлении элемента {@link #getLast()}.</p>
	 *
	 * <p>Если нет элемента соответствующего заданному предикату - возвращает -1.</p>
	 *
	 * @param predicate условие, которому должен соответствовать искомый элемент.
	 * @return индекс первого встретившегося элемента соответствующего заданному предикату.
	 * @throws NullPointerException если predicate равен null.
	 */
	public default int linearSearch(Predicate<T> predicate) {
		int index = 0;
		while(index < size() && !predicate.test(get(index))) ++index;
		return index >= size() ? -1 : index;
	}

	/**
	 * <p>Возвращает индекс первого встретившегося с конца элемента соответствующего заданному предикату.</p>
	 *
	 * <p>Выполняет линейный поиск начиная с элемента {@link #getLast()} в направлении элемента {@link #getFirst()}.</p>
	 *
	 * <p>Если нет элемента соответствующего заданному предикату - возвращает -1.</p>
	 *
	 * @param predicate условие, которому должен соответствовать искомый элемент.
	 * @return индекс первого встретившегося с конца элемента соответствующего заданному предикату.
	 * @throws NullPointerException если predicate равен null.
	 */
	public default int linearSearchLast(Predicate<T> predicate) {
		int index = size() - 1;
		while(index >= 0 && !predicate.test(get(index))) --index;
		return index;
	}

	/**
	 * <p>Возвращает первый встретившейся элемент, соответствующий заданному предикату.</p>
	 *
	 * <p>Выполняет линейный поиск начиная с элемента {@link #getFirst()} в направлении элемента {@link #getLast()}.</p>
	 *
	 * <p>Если нет элемента соответствующего заданному предикату - возвращает null.</p>
	 *
	 * @param predicate условие, которому должен соответствовать искомый элемент.
	 * @return первый встретившейся элемент, соответствующий предикату или null.
	 * @throws NullPointerException если predicate равен null.
	 */
	public default T linearSearchObj(Predicate<T> predicate) {
		int index = linearSearch(predicate);
		return index == -1 ? null : get(index);
	}

	/**
	 * Проверяет - содержит ли структура данных элемент с заданным значением.
	 * Если это верно - возвращает true, иначе - false.
	 * @param value значение искомого элемента.
	 */
	public default boolean contains(T value) {
		return linearSearch(value) != -1;
	}

	/**
	 * Проверяет - содержит ли структура данных элемент, удовлетворяющий заданному предикату.
	 * Если это верно - возвращает true, иначе - false.
	 * @param predicate условие, которому должен соответствовать искомый элемент.
	 * @throws NullPointerException если predicate равен null.
	 */
	public default boolean contains(Predicate<T> predicate) {
		return linearSearch(predicate) != -1;
	}

	/**
	 * Возвращает кол-во элементов соответствующих заданному предикату.
	 * @throws NullPointerException если predicate равен null.
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
	 * результатом применения к нему функции mapper.
	 * @param mapper функция обратного вызова, заменяющая каждый элемент, скопированный из исходной структуры данных.
	 * @throws ConcurrentModificationException при попытке изменить структуру данных из mapper.
	 * @throws NullPointerException если mapper равен null.
	 */
	public <R> ReadableLinearStructure<R> mappedCopy(IndexBiFunction<T, R> mapper);

	/**
	 * Создает и возвращает новую структуру данных того же самого типа. Возвращаемая структура
	 * будет содержать все элементы искомой структуры, которые удовлетворяют заданному предикату.
	 * @param predicate условие, по которому отбираются элементы в возвращаемую структуру данных.
	 * @throws ConcurrentModificationException при попытке изменить структуру данных из predicate.
	 * @throws NullPointerException если predicate равен null.
	 */
	public ReadableLinearStructure<T> filteredCopy(IndexBiPredicate<T> predicate);

	/**
	 * <p>Сводит все элементы этой структуры данных в один элемент и возвращает его.</p>
	 *
	 * <p>Каждый элемент последовательно объединяется с результатом сведения всех предыдущих элементов.
	 * Сведение элементов начинается с элемента {@link #getFirst()} в направлении элемента {@link #getLast()}.</p>
	 *
	 * <p>Если структура данных пуста - возвращает null.</p>
	 *
	 * @param accumulator функция для сведения всех элементов в один элемент.
	 * @throws ConcurrentModificationException при попытке изменить структуру данных из accumulator.
	 * @throws NullPointerException если accumulator равен null.
	 */
	public default T reduce(BinaryOperator<T> accumulator) {
		T result = null;

		IndexedIterator<T> iterator = iterator();
		if(iterator.hasNext()) {
			result = iterator.next();
			while(iterator.hasNext()) {
				T item = iterator.next();
				result = accumulator.apply(result, item);
			}
		}

		return result;
	}

	/**
	 * <p>Сводит все элементы этой структуры данных в один элемент и возвращает его.</p>
	 *
	 * <p>Каждый элемент последовательно объединяется с результатом сведения всех предыдущих элементов.</p>
	 *
	 * <p>Если структура данных пуста - возвращает null.</p>
	 *
	 * <p>Поведение данного метода семантически эквивалентно следующему коду:</p>
	 * <pre><code>
	 *     T result = initValue;
	 *     for (T item : thisLinearStructure)
	 *         result = accumulator.apply(result, item);
	 *     return result;
	 * </code></pre>
	 * @param initValue начальное значение.
	 * @param accumulator функция для сведения всех элементов в один элемент.
	 * @throws ConcurrentModificationException при попытке изменить структуру данных из accumulator.
	 * @throws NullPointerException если accumulator равен null.
	 */
	public default T reduce(T initValue, BinaryOperator<T> accumulator) {
		T result = initValue;
		for(T item : this) {
			result = accumulator.apply(result, item);
		}
		return result;
	}

	/**
	 * Создает и возвращает новый статический массив, содержащий все элементы этой структуры данных
	 * в том же порядке.
	 * @throws NullPointerException если itemType равен null.
	 */
	@SuppressWarnings("unchecked")
	public default T[] toArray(Class<T> itemType) {
		T[] array = (T[]) Array.newInstance(itemType, size());
		forEach((item, index) -> array[index] = item);
		return array;
	}

	/**
	 * <p>Создает и возвращает итератор, позволяющий последовательно перебирать линейную структуру данных в
	 * обоих направлениях.
	 *
	 * <p>Сразу после создания, курсор итератора установлен перед элементом {@link #getFirst()}.</p>
	 */
	@Override
	public IndexedIterator<T> iterator();

	/**
	 * {@inheritDoc}
	 * @param action операция, выполняемая над каждым элементом, хранящимся в данном массиве.
	 * @throws ConcurrentModificationException если структура данных изменяется в момент выполнения этого метода.
	 */
	@Override
	public default void forEach(Consumer<? super T> action) {
		Iterable.super.forEach(action);
	}

	/**
	 * Поведение этого метода расширяет контракт {@link #forEach(Consumer)}. Функция обратного вызова, помимо самих
	 * элементов также принимает их индексы.
	 * @throws ConcurrentModificationException при попытке изменить структуру данных из action.
	 * @throws NullPointerException если action равен null.
	 */
	public default void forEach(IndexBiConsumer<? super T> action) {
		IndexedIterator<T> iterator = iterator();
		while(iterator.hasNext()) {
			T item = iterator.next();
			int itemIndex = iterator.recentIndex();
			action.accept(item, itemIndex);
		}
	}
}
