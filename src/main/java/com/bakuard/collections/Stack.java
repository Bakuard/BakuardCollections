package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>Реализация динамического стека с объектами произвольного типа.</p>
 *
 * <p>Данный класс не является потокобезопасным.</p>
 */
public final class Stack<T> implements ReadableLinearStructure<T> {

	/**
	 * <p>Создает и возвращает стек, содержащий указанные элементы в указанном порядке.</p>
	 *
	 * <p>Итоговый стек будет содержать копию передаваемого массива, а не сам массив.</p>
	 *
	 * <p>Длина создаваемого объекта ({@link #size()}) будет равна кол-ву передаваемых элементов.
	 * Если передаваемый массив не содержит ни одного элемента - создает пустой стек.</p>
	 *
	 * @param data элементы, включаемые в создаваемый стек.
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
	 * @throws NullPointerException если other равен null.
	 */
	public Stack(Stack<T> other) {
		this.array = new DynamicArray<>(other.array);
	}

	/**
	 * Создает новый стек, копируя в него все элементы iterable в порядке их возвращения итератором.
	 * @param iterable структура данных, элементы которой копируются в новый стек.
	 * @throws NullPointerException если iterable равен null.
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
	 * <p>Добавляет все элементы из указанной перебираемой структуры данных на вершину стека.</p>
	 *
	 * <p>Элементы добавляются в порядке их возвращения итератором.</p>
	 *
	 * @param iterable структура данных, все элементы которой добавляются на вершину текущего стека.
	 * @throws NullPointerException если iterable равен null.
	 */
	public void addAllOnLast(Iterable<T> iterable) {
		array.addAllOnLast(iterable);
	}

	/**
	 * <p>Добавляет все элементы из указанного массива на вершину стека.</p>
	 *
	 * <p>Элементы добавляются в порядке их следования в указанном массиве.</p>
	 *
	 * @param data массив, все элементы которого добавляются в текущий стек.
	 * @throws NullPointerException если передаваемый массив data равен null.
	 */
	public void addAllOnLast(T... data) {
		array.addAllOnLast(data);
	}

	/**
	 * <p>Удаляет элемент с вершины стека и возвращает его. Если стек пуст - возвращает null.</p>
	 *
	 * <p><b>ВАЖНО!</b> Т.к. стек допускает хранение null элементов, то возвращение данным методом
	 * null в качестве результата не гарантирует, что стек пуст. Для проверки наличия элементов
	 * в стеке используйте методы {@link #size()} или {@link #isEmpty()}.</p>
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
	 * <p>Удаляет все элементы из стека и уменьшает его длину до нуля.</p>
	 *
	 * <p>Данный метод не уменьшает емкость внутреннего хранилища.
	 * Если вам необходимо уменьшить объем памяти, занимаемый данным объектом, используйте метод {@link #trimToSize()}.</p>
	 */
	public void clear() {
		array.clear();
	}

	/**
	 * <p>Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
	 * длиной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
	 * никаких изменений.</p>
	 *
	 * <p>Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем памяти, занимаемый объектом Stack.</p>
	 *
	 * @return true - если объем внутреннего массива был уменьшен, иначе - false.
	 */
	public boolean trimToSize() {
		return array.trimToSize();
	}

	/**
	 * {@inheritDoc}
	 */
	public T get(int index) {
		return array.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public T at(int index) {
		return array.at(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return array.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public int linearSearch(T value) {
		return array.linearSearch(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public int linearSearch(Predicate<T> predicate) {
		return array.linearSearch(predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	public int frequency(Predicate<T> predicate) {
		return array.frequency(predicate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Stack<R> mappedCopy(IndexBiFunction<T, R> mapper) {
		Stack<R> result = new Stack<>();
		result.array = array.mappedCopy(mapper);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stack<T> filteredCopy(IndexBiPredicate<T> predicate) {
		Stack<T> result = new Stack<>();
		result.array = array.filteredCopy(predicate);
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
	 * {@inheritDoc}
	 */
	@Override
	public IndexedIterator<T> iterator() {
		return array.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void forEach(Consumer<? super T> action) {
		array.forEach(action);
	}

	/**
	 * {@inheritDoc}
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
