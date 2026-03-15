package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * <p>Реализация динамической двусторонней очереди с объектами произвольного типа.</p>
 * <p>Данный класс не является потокобезопасным.</p>
 */
public final class Deque<T> extends Queue<T> {

	/**
	 * <p>Создает и возвращает двустороннюю очередь, содержащую указанные элементы в указанном порядке.</p>
	 *
	 * <p>Длина создаваемого объекта ({@link #size()}) будет равна на кол-ву передаваемых элементов.
	 * Если передаваемый массив не содержит ни одного элемента, то создает пустую двустороннюю очередь.</p>
	 *
	 * <p>Итоговая двусторонняя очередь будет содержать копию передаваемого массива, а не сам массив.</p>
	 *
	 * @param data элементы, включаемые в создаваемую двустороннюю очередь.
	 * @throws NullPointerException если передаваемый массив элементов равен null.
	 */
	public static <T> Deque<T> of(T... data) {
		if(data == null) throw new NullPointerException("data[] can not be null.");

		Deque<T> deque = new Deque<>();
		deque.addAllOnLast(data);
		return deque;
	}

	private static final int MIN_CAPACITY = 10;


	/**
	 * Создает пустую двустороннюю очередь.
	 */
	public Deque() {
		this(0);
	}

	/**
	 * Создает копию переданной двусторонней очереди. Выполняет поверхностное копирование.
	 * @param other копируемая двусторонняя очередь.
	 * @throws NullPointerException если other равен null.
	 */
	public Deque(Deque<T> other) {
		this.values = other.values.clone();
		this.firstItemIndex = other.firstItemIndex;
		this.lastItemIndex = other.lastItemIndex;
	}

	/**
	 * Создает новую двустороннюю очередь, копируя в неё все элементы iterable в порядке их возвращения итератором.
	 * @param iterable структура данных, элементы которой копируются в новую двустороннюю очередь.
	 * @throws NullPointerException если iterable равен null.
	 */
	public Deque(Iterable<T> iterable) {
		this();
		addAllOnFirst(iterable);
	}

	@SuppressWarnings("unchecked")
	private Deque(int size) {
		int capacity = Math.max(calculateCapacity(size), MIN_CAPACITY);
		this.values = (T[]) new Object[calculateCapacity(capacity)];
		this.lastItemIndex = size;
	}

	/**
	 * Добавляет элемент в начало двусторонней очереди увеличивая его длину ({@link #size()}) на единицу.
	 * Добавляемый элемент может иметь значение null.
	 * @param value добавляемый элемент.
	 */
	public void addFirst(T value) {
		++actualModCount;

		int currentSize = size();
		grow(currentSize, currentSize + 1);
		firstItemIndex = firstItemIndex - 1 >= 0 ? firstItemIndex - 1 : values.length - 1;
		values[firstItemIndex] = value;
	}

	/**
	 * Добавляет каждый элемент из указанной перебираемой структуры данных в начало двусторонней очереди.
	 * Элементы добавляются в порядке их возвращения итератором.
	 * @param iterable структура данных, все элементы которого добавляются в текущую двустороннюю очередь.
	 * @throws NullPointerException если iterable равен null.
	 */
	public void addAllOnFirst(Iterable<T> iterable) {
		++actualModCount;

		Stack<T> stack = new Stack<>();
		stack.addAllOnLast(iterable);

		int currentSize = size();
		grow(currentSize, currentSize + stack.size());
		while(!stack.isEmpty()) {
			firstItemIndex = firstItemIndex - 1 >= 0 ? firstItemIndex - 1 : values.length - 1;
			values[firstItemIndex] = stack.removeLast();
		}
	}

	/**
	 * Добавляет каждый элемент из указанного массива в начало двусторонней очереди. Элементы добавляются в порядке
	 * их следования в массиве.
	 * @param data массив, все элементы которого добавляются в текущую двустороннюю очередь.
	 * @throws NullPointerException если передаваемый массив элементов равен null.
	 */
	public void addAllOnFirst(T... data) {
		++actualModCount;

		int currentSize = size();
		grow(currentSize, currentSize + data.length);
		for(int i = data.length - 1; i >= 0; --i) {
			firstItemIndex = firstItemIndex - 1 >= 0 ? firstItemIndex - 1 : values.length - 1;
			values[firstItemIndex] = data[i];
		}
	}

	/**
	 * <p>Удаляет элемент с конца двусторонней очереди и возвращает его. Если двусторонняя очередь пуста, то
	 * возвращает null.</p>
	 *
	 * <p><b>ВАЖНО!</b> Т.к. двусторонняя очередь допускает хранение null элементов, то возвращение данным
	 * методом null в качестве результата не гарантирует, что двусторонняя очередь пуста. Для проверки
	 * наличия элементов в двусторонней очереди используйте методы {@link #size()} или {@link #isEmpty()}.</p>
	 */
	public T removeLast() {
		++actualModCount;

		T result = null;
		if(!isEmpty()) {
			lastItemIndex = lastItemIndex - 1 >= 0 ? lastItemIndex - 1 : values.length - 1;
			result = values[lastItemIndex];
			values[lastItemIndex] = null;
		}

		return result;
	}

	/**
	 * Удаляет элемент с конца двусторонней очереди и возвращает его.
	 * @throws NoSuchElementException если очередь пуста.
	 */
	public T tryRemoveLast() {
		if(isEmpty()) {
			throw new NoSuchElementException("Fail to remove last item: deque is empty.");
		}

		return removeLast();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> Deque<R> mappedCopy(IndexBiFunction<T, R> mapper) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		int size = size();
		Deque<R> result = new Deque<>(size);
		for(int i = 0; i < size; ++i) {
			result.values[i] = mapper.apply(unsafeGet(i), i);
			if(EXPECTED_COUNT_MOD != actualModCount) {
				throw new ConcurrentModificationException();
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Deque<T> filteredCopy(IndexBiPredicate<T> predicate) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		int size = size();
		Deque<T> result = new Deque<>();
		for(int i = 0; i < size; ++i) {
			T item = unsafeGet(i);
			if(predicate.test(item, i)) result.addLast(item);
			if(EXPECTED_COUNT_MOD != actualModCount) {
				throw new ConcurrentModificationException();
			}
		}
		return result;
	}

	public String toString() {
		final int size = size();
		StringBuilder valuesToString = new StringBuilder("[");
		if(size > 0) {
			valuesToString.append(unsafeGet(0));
			for(int i = 1; i < size; ++i) valuesToString.append(',').append(unsafeGet(i));
		}
		valuesToString.append(']');

		return "Deque{size=" + size + ", " + valuesToString + '}';
	}
}
