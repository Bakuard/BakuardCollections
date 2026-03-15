package com.bakuard.collections;

import com.bakuard.collections.exception.MaxSizeExceededException;
import com.bakuard.collections.exception.NegativeSizeException;
import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * <p>Реализация циклического буфера с объектами произвольного типа.</p>
 *
 * <p>Данный класс не является потокобезопасным.</p>
 */
public final class RingBuffer<T> implements ReadableLinearStructure<T> {

	/**
	 * Создает и возвращает циклический буфер, максимальный размер которого равен maxSize.
	 * Созданный буфер будет содержать все элементы из data в том же порядке.
	 * @param maxSize максимальный размер создаваемого буфера.
	 * @param data элементы, включаемые в создаваемый циклический буфер.
	 * @throws NullPointerException если передаваемый массив элементов равен null.
	 * @throws NegativeSizeException если {@code maxSize < 0}
	 * @throws MaxSizeExceededException если {@code data.length > maxSize}
	 */
	public static <T> RingBuffer<T> of(int maxSize, T... data) {
		if(data == null) {
			throw new NullPointerException("data[] can not be null.");
		}
		if(maxSize < 0) {
			throw new NegativeSizeException("Expected: maxSize >= 0. Actual: maxSize=" + maxSize);
		}
		if(maxSize < data.length) {
			throw new MaxSizeExceededException(
					"Expected: maxSize < data.length. Actual: maxSize=%d, data.length=%d".formatted(maxSize, data.length)
			);
		}

		RingBuffer<T> result = new RingBuffer<>(maxSize);
		result.addAllOnLastOrSkip(data);
		return result;
	}


	private T[] values;
	private int firstItemIndex;
	private int currentSize;
	private int actualModCount;

	/**
	 * Создает новый пустой циклический буфер с заданным максимальным размером.
	 * @param maxSize максимальный размер циклического буфера.
	 * @throws NegativeSizeException если {@code maxSize < 0}
	 */
	@SuppressWarnings("unchecked")
	public RingBuffer(int maxSize) {
		assertNotNegativeSize(maxSize);
		values = (T[]) new Object[maxSize];
	}

	/**
	 * Создает копию циклического буфера. Выполняет поверхностное копирование.
	 * @param other копируемый циклический буфер.
	 * @throws NullPointerException если other равен null.
	 */
	public RingBuffer(RingBuffer<T> other) {
		this.values = other.values.clone();
		this.firstItemIndex = other.firstItemIndex;
		this.currentSize = other.currentSize;
	}

	/**
	 * Создает новый циклический буфер, копируя в него все элементы iterable в порядке их возвращения итератором.
	 * Максимальный размер буфера будет равен кол-ву элементов, возвращенных итератором.
	 * @param iterable структура данных, элементы которой копируются в новый буфер.
	 * @throws NullPointerException если iterable равен null.
	 */
	@SuppressWarnings("unchecked")
	public RingBuffer(Iterable<T> iterable) {
		DynamicArray<T> tempBuffer = new DynamicArray<>();
		tempBuffer.addAllOnLast(iterable);

		values = (T[]) new Object[tempBuffer.size()];
		tempBuffer.forEach((item, index) -> values[index] = item);
		currentSize = tempBuffer.size();
	}

	/**
	 * <p>Добавляет элемент в конец циклического буфера.</p>
	 * <ol>
	 *     <li> Если {@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code  == false}, перезаписывает первый элемент,
	 *     делая его последним. Возвращает предыдущее значение перезаписанного элемента.</li>
	 *     <li> Если {@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code == true}, добавляет элемент в конец циклического
	 *     буфера и возвращает null.</li>
	 *     <li> Если {@link #maxSize()} {@code == 0}, возвращает добавляемый элемент, не изменяя циклический буфер.</li>
	 * </ol>
	 *
	 * <p>Пример ({@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code == false}):</p>
	 * <pre>
	 * {@code
	 *      RingBuffer<Integer> buffer = RingBuffer.of(10, 1,2,3,4,5,6,7,8,9,10);
	 *
	 *      Integer oldValue = buffer.addLastOrReplace(100);
	 *
	 *      System.out.println(oldValue); // 1
	 *      System.out.println(buffer); // RingBuffer{currentSize=10, [2,3,4,5,6,7,8,9,10,100]}
	 *      System.out.println(buffer.getFirst()); // 2
	 * }
	 * </pre>
	 *
	 * <p><b>ВАЖНО!</b> Т.к. циклический буфер допускает хранение null элементов, то возвращение данным методом
	 * null в качестве результата не гарантирует, что в буфере оставалось свободное место.
	 * Для проверки текущего размера буфера, а также заполнен он или же является пустым, используйте методы {@link #size()},
	 * {@link #maxSize()}, {@link #isEmpty()} или {@link #hasAvailableSpace()}.</p>
	 *
	 * @param value добавляемый элемент.
	 */
	public T addLastOrReplace(T value) {
		++actualModCount;

		T rewritingValue = null;
		if(maxSize() == 0) {
			rewritingValue = value;
		} else if(hasAvailableSpace()) {
			values[(firstItemIndex + currentSize++) % values.length] = value;
		} else {
			rewritingValue = values[firstItemIndex];
			values[firstItemIndex] = value;
			firstItemIndex = (firstItemIndex + 1) % values.length;
		}

		return rewritingValue;
	}

	/**
	 * <p>Добавляет в конец циклического буфера все элементы, возвращаемые итератором. Порядок добавления элементов
	 * соответствует порядку их возвращения итератором.</p>
	 *
	 * <p>Для каждого добавляемого элемента, в момент его добавления, выполняется один из следующих сценариев:</p>
	 * <ol>
	 *     <li>Если {@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code == false}, перезаписывает первый элемент
	 *     циклического буфера, делая его последним. Предыдущее значение перезаписанного элемента добавляет к
	 *     возвращаемому результату.</li>
	 *     <li>Если {@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code == true}, добавляет элемент в конец циклического
	 *     буфера. Ничего не добавляет к возвращаемому результату./li>
	 *     <li>Если {@link #maxSize()} {@code == 0}, сразу же добавляет элемент к возвращаемому результату.</li>
	 * </ol>
	 *
	 * @param iterable структура данных, все элементы которой добавляются в текущий циклический буфер.
	 * @return все перезаписанные элементы.
	 * @throws NullPointerException если iterable равен null.
	 */
	public DynamicArray<T> addAllOnLastOrReplace(Iterable<T> iterable) {
		++actualModCount;

		DynamicArray<T> rewritingValues = new DynamicArray<>();
		for(T value : iterable) {
			boolean valueWasRewriting = !hasAvailableSpace();
			T rewritingValue = addLastOrReplace(value);
			if(valueWasRewriting) rewritingValues.addLast(rewritingValue);
		}

		return rewritingValues;
	}

	/**
	 * <p>Добавляет в конец циклического буфера все элементы массива data. Порядок добавления элементов
	 * соответствует порядку их следования в массиве.</p>
	 *
	 * <p>Для каждого добавляемого элемента, в момент его добавления, выполняется один из следующих сценариев:</p>
	 * <ol>
	 *     <li>Если {@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code == false}, перезаписывает первый элемент
	 *     циклического буфера, делая его последним. Предыдущее значение перезаписанного элемента добавляет к
	 *     возвращаемому результату.</li>
	 *     <li>Если {@link #maxSize()} {@code > 0} и {@link #hasAvailableSpace()} {@code == true}, добавляет элемент в конец циклического
	 *     буфера. Ничего не добавляет к возвращаемому результату.</li>
	 *     <li>Если {@link #maxSize()} {@code == 0}, сразу же добавляет элемент к возвращаемому результату.</li>
	 * </ol>
	 *
	 * @param data массив, все элементы которого добавляются в текущий циклический буфер.
	 * @return все перезаписанные элементы.
	 * @throws NullPointerException если передаваемый массив data равен null.
	 */
	public DynamicArray<T> addAllOnLastOrReplace(T... data) {
		++actualModCount;

		DynamicArray<T> rewritingValues = new DynamicArray<>();
		for(T value : data) {
			boolean valueWasRewriting = !hasAvailableSpace();
			T rewritingValue = addLastOrReplace(value);
			if(valueWasRewriting) rewritingValues.addLast(rewritingValue);
		}

		return rewritingValues;
	}

	/**
	 * Пробует добавить элемент в конец циклического буфера. Если на момент вызова этого метода
	 * {@link #hasAvailableSpace()} возвращает false, то элемент не будет добавлен, а метод вернет false.
	 * В противном случае элемент будет добавлен в конец циклического буфера, текущий размер буфера будет увеличен на
	 * единицу, а данный метод вернет true.
	 * @param value добавляемый элемент.
	 * @return true - если удалось добавить элемент, иначе - false.
	 */
	public boolean addLastOrSkip(T value) {
		++actualModCount;

		boolean canBeAdded = hasAvailableSpace();
		if(canBeAdded) values[(firstItemIndex + currentSize++) % values.length] = value;
		return canBeAdded;
	}

	/**
	 * <p>Пробует добавить в конец циклического буфера все элементы, возвращаемые итератором.
	 * Для каждого добавляемого элемента выполняется порядок действий описанный для метода {@link #addLastOrSkip(Object)}.</p>
	 *
	 * <p>Порядок добавления элементов соответствует порядку их возвращения итератором.</p>
	 *
	 * <p>Возвращает кол-во добавленных элементов.</p>
	 *
	 * @param iterable структура данных, все элементы которого добавляются в текущий циклический буфер.
	 * @return кол-во добавленных элементов.
	 * @throws NullPointerException если iterable равен null.
	 */
	public int addAllOnLastOrSkip(Iterable<T> iterable) {
		++actualModCount;

		int addedValuesNumber = 0;
		boolean wasAdded = true;
		Iterator<T> iterator = iterable.iterator();
		while(wasAdded && iterator.hasNext()) {
			wasAdded = addLastOrSkip(iterator.next());
			if(wasAdded) ++addedValuesNumber;
		}

		return addedValuesNumber;
	}

	/**
	 * <p>Пробует добавить в конец циклического буфера все элементы массива data.
	 * Для каждого добавляемого элемента выполняется порядок действий описанный для метода {@link #addLastOrSkip(Object)}.</p>
	 *
	 * <p>Порядок добавления элементов соответствует порядку их следования в массиве.</p>
	 *
	 * <p>Возвращает кол-во добавленных элементов.</p>
	 *
	 * @param data массив, все элементы которого добавляются в текущий циклический буфер.
	 * @return кол-во добавленных элементов.
	 * @throws NullPointerException если передаваемый массив data равен null.
	 */
	public int addAllOnLastOrSkip(T... data) {
		++actualModCount;

		final int addedValuesNumber = Math.min(values.length - currentSize, data.length);
		if(addedValuesNumber > 0) {
			final int startIndex = (firstItemIndex + currentSize) % values.length;
			System.arraycopy(data, 0, values, startIndex, addedValuesNumber);
			currentSize += addedValuesNumber;
		}

		return addedValuesNumber;
	}

	/**
	 * Добавляет элемент в конец циклического буфера. Если в циклическом буфере нет свободного места для добавления
	 * этого элемента ({@link #hasAvailableSpace()} равен false), то выбрасывает исключение.
	 * @param value добавляемый элемент.
	 * @throws com.bakuard.collections.exception.MaxSizeExceededException если в циклическом буфере нет свободного
	 *                                                                     места для добавления этого элемента.
	 */
	public void tryAddLast(T value) {
		++actualModCount;

		if(!hasAvailableSpace()) {
			throw new MaxSizeExceededException(
					"There is not enough available space to add this value. MaxSize: " +
							maxSize() + ", added item: " + value
			);
		}
		values[(firstItemIndex + currentSize++) % values.length] = value;
	}

	/**
	 * <p>Удаляет элемент из начала циклического буфера и возвращает его. Если циклический буфер пуст, то возвращает null.</p>
	 *
	 * <p><b>ВАЖНО!</b> Т.к. циклический буфер допускает хранение null элементов, то возвращение данным методом
	 * null в качестве результата не гарантирует, что буфер пуст. Для проверки текущего размера буфера, а также
	 * заполнен он или же является пустым, используйте методы {@link #size()}, {@link #maxSize()}, {@link #isEmpty()}
	 * или {@link #hasAvailableSpace()}.</p>
	 */
	public T removeFirst() {
		++actualModCount;

		T removedValue = null;
		if(!isEmpty()) {
			removedValue = values[firstItemIndex];
			values[firstItemIndex] = null;
			firstItemIndex = (firstItemIndex + 1) % values.length;
			--currentSize;
		}
		return removedValue;
	}

	/**
	 * Удаляет элемент из начала циклического буфера и возвращает его.
	 * @throws NoSuchElementException если циклический буфер пуст.
	 */
	public T tryRemoveFirst() {
		if(isEmpty()) {
			throw new NoSuchElementException("Fail to remove first item: ring buffer is empty.");
		}

		return removeFirst();
	}

	/**
	 * Очищает циклический буфер, удаляя все его элементы и уменьшая текущий размер до нуля.
	 */
	public void clear() {
		++actualModCount;

		for(int i = 0; i < values.length; ++i) values[i] = null;
		currentSize = 0;
		firstItemIndex = 0;
	}

	/**
	 * Если {@code newSize >} {@link #size()}, то увеличивает максимальный размер ({@link #maxSize()}) циклического буфера
	 * до указанной величины (включительно).
	 * @throws NegativeSizeException если {@code newSize < 0}
	 */
	@SuppressWarnings("unchecked")
	public RingBuffer<T> growToSize(int newSize) {
		++actualModCount;

		assertNotNegativeSize(newSize);

		if(newSize > values.length) {
			T[] newValues = (T[]) new Object[newSize];
			fillArray(newValues);
			values = newValues;
			firstItemIndex = 0;
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		assertInBound(index);

		return unsafeGet(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T at(int index) {
		assertInBoundByModulo(index);

		return index < 0 ?
				values[(firstItemIndex + currentSize + index) % values.length] :
				unsafeGet(index);
	}

	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return currentSize == 0;
	}

	/**
	 * Проверяет, выполняется ли условие {@link #size()} {@code <} {@link #maxSize()}.
	 */
	public boolean hasAvailableSpace() {
		return currentSize < values.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int linearSearch(T value) {
		int index = 0;
		while(index < currentSize && !Objects.equals(unsafeGet(index), value)) ++index;
		return index >= currentSize ? -1 : index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int linearSearch(Predicate<T> predicate) {
		int index = 0;
		while(index < currentSize && !predicate.test(unsafeGet(index))) ++index;
		return index >= currentSize ? -1 : index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int linearSearchLast(Predicate<T> predicate) {
		int index = currentSize - 1;
		while(index >= 0 && !predicate.test(unsafeGet(index))) --index;
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int frequency(Predicate<T> predicate) {
		int result = 0;

		for(int i = 0; i < currentSize; ++i) {
			if(predicate.test(unsafeGet(i))) ++result;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> RingBuffer<R> mappedCopy(IndexBiFunction<T, R> mapper) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		RingBuffer<R> result = new RingBuffer<>(maxSize());
		result.currentSize = currentSize;
		for(int i = 0; i < currentSize; ++i) {
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
	public RingBuffer<T> filteredCopy(IndexBiPredicate<T> predicate) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		RingBuffer<T> result = new RingBuffer<>(maxSize());
		for(int i = 0, j = 0; i < currentSize; ++i) {
			T item = unsafeGet(i);
			if(predicate.test(item, i)) {
				result.values[j++] = item;
				++result.currentSize;
			}
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
	public T reduce(BinaryOperator<T> accumulator) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		int size = size();
		T result = null;
		if(size > 0) {
			result = unsafeGet(0);
			for(int i = 1; i < size; ++i) {
				result = accumulator.apply(result, unsafeGet(i));
				if(EXPECTED_COUNT_MOD != actualModCount) {
					throw new ConcurrentModificationException();
				}
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T reduce(T initValue, BinaryOperator<T> accumulator) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		int size = size();
		T result = initValue;
		for(int i = 0; i < size; ++i) {
			result = accumulator.apply(result, unsafeGet(i));
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
		int size = size();
		T[] result = (T[]) Array.newInstance(itemType, size);
		fillArray(result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndexedIterator<T> iterator() {
		return new IndexedIteratorImpl<>(actualModCount, currentSize);
	}

	/**
	 * {@inheritDoc}
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

		for(int i = 0; i < currentSize; ++i) {
			action.accept(unsafeGet(i), i);
			if(EXPECTED_COUNT_MOD != actualModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RingBuffer<?> buffer = (RingBuffer<?>) o;

		boolean result = buffer.currentSize == currentSize;
		for(int i = 0; i < currentSize && result; i++) {
			result = Objects.equals(buffer.unsafeGet(i), unsafeGet(i));
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = currentSize;
		for(int i = 0; i < currentSize; i++) result = result * 31 + Objects.hashCode(unsafeGet(i));
		return result;
	}

	@Override
	public String toString() {
		StringBuilder valuesToString = new StringBuilder("[");
		if(currentSize > 0) {
			valuesToString.append(unsafeGet(0));
			for(int i = 1; i < currentSize; ++i) valuesToString.append(',').append(unsafeGet(i));
		}
		valuesToString.append(']');

		return "RingBuffer{currentSize=" + currentSize + ", " + valuesToString + '}';
	}


	private T unsafeGet(int index) {
		return values[(firstItemIndex + index) % values.length];
	}

	private void assertInBound(int index) {
		if(index < 0 || index >= currentSize) {
			throw new IndexOutOfBoundsException(
					"Expected: index >= 0 && index < currentSize. Actual: currentSize=" + currentSize + ", index=" + index
			);
		}
	}

	private void assertInBoundByModulo(int index) {
		if(index < -currentSize || index >= currentSize) {
			throw new IndexOutOfBoundsException(
					"Expected: index >= -currentSize && index < currentSize. Actual: currentSize=" + currentSize + ", index=" + index
			);
		}
	}

	private void assertNotNegativeSize(int size) {
		if(size < 0) {
			throw new NegativeSizeException("Expected: size >= 0; Actual: size=" + size);
		}
	}

	private void fillArray(T[] array) {
		if(currentSize > 0) {
			int lengthBeforeWrap = values.length - firstItemIndex;
			System.arraycopy(values, firstItemIndex, array, 0, lengthBeforeWrap);
			System.arraycopy(values, 0, array, lengthBeforeWrap, values.length - lengthBeforeWrap);
		}
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
			return (E) RingBuffer.this.unsafeGet(recentIndex);
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
			return (E) RingBuffer.this.unsafeGet(recentIndex);
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
			return (E) RingBuffer.this.unsafeGet(recentIndex);
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
			if(actualModCount != expectedModCount) {
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
