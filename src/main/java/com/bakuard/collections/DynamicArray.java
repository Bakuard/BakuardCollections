package com.bakuard.collections;

import com.bakuard.collections.exception.NegativeSizeException;
import com.bakuard.collections.function.IndexBiConsumer;
import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.random.RandomGenerator;

/**
 * <p>Реализация динамического массива с объектами произвольного типа.</p>
 * <p>Данный класс не является потокобезопасным.</p>
 */
public final class DynamicArray<T> implements ReadableLinearStructure<T> {

	/**
	 * <p>Создает и возвращает массив, содержащий указанные элементы в указанном порядке.</p>
	 *
	 * <p>Длина создаваемого объекта ({@link #size()}) будет равна кол-ву передаваемых элементов. Если передаваемый
	 * массив не содержит ни одного элемента - создает пустой объект DynamicArray.</p>
	 *
	 * <p>Итоговый объект DynamicArray будет содержать копию передаваемого массива, а не сам массив.</p>
	 *
	 * @param data элементы, включаемые в создаваемый объект.
	 * @throws NullPointerException если передаваемый массив элементов равен null.
	 */
	public static <T> DynamicArray<T> of(T... data) {
		if(data == null) throw new NullPointerException("data[] can not be null.");

		DynamicArray<T> result = new DynamicArray<>();
		result.addAllOnLast(data);
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
	public DynamicArray() {
		values = (T[]) new Object[MIN_CAPACITY];
	}

	 /**
	 * Создает массив указанной длины, все элементы которого равны null.
	 * @param size длина массива.
	 * @throws NegativeSizeException если указанная длина меньше нуля.
	 */
	 @SuppressWarnings("unchecked")
	public DynamicArray(int size){
		assertNotNegativeSize(size);

		this.size = size;
		values = (T[]) new Object[Math.max(calculateCapacity(size), MIN_CAPACITY)];
	}

	/**
	 * Создает копию указанного массива. Выполняет поверхностное копирование.
	 * @param other массив, для которого создается копия.
	 * @throws NullPointerException если other равен null.
	 */
	public DynamicArray(DynamicArray<T> other) {
		values = other.values.clone();
		size = other.size;
	}

	/**
	 * Создает новый массив, копируя в него все элементы iterable в порядке их возвращения итератором.
	 * @param iterable структура данных, элементы которой копируются в новый массив.
	 * @throws NullPointerException если iterable равен null.
	 */
	public DynamicArray(Iterable<T> iterable) {
		this();
		addAllOnLast(iterable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		assertInBound(index);

		return values[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T at(int index) {
		assertInExpandBound(index);

		return index < 0 ? values[size + index] : values[index];
	}

	/**
	 * Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
	 * ячейке до вызова этого метода. При вызове данного метода длина массива не изменяется.
	 * @param index индекс ячейки массива, куда будет записан элемент.
	 * @param value добавляемое значение.
	 * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
	 * @throws IndexOutOfBoundsException если не соблюдается условие {@code index >= 0 && index < } {@link #size()}
	 */
	public T replace(int index, T value) {
		assertInBound(index);

		++actualModCount;

		T oldValue = values[index];
		values[index] = value;
		return oldValue;
	}

	/**
	 * <p>Записывает элемент в ячейку с указанным индексом и возвращает элемент, который находился в этой
	 * ячейке до вызова этого метода.</p>
	 *
	 * <p>Если {@code index >= } {@link #size()}, то увеличивает размер массива до {@code index + 1}, затем записывает
	 * указанное значение по заданному индексу, а затем возвращает null.</p>
	 *
	 * @param index индекс ячейки массива, куда будет записан элемент.
	 * @param value добавляемое значение.
	 * @return элемент, который находился в массиве под указанным индексом до вызова этого метода.
	 * @throws IndexOutOfBoundsException если {@code index < 0}.
	 */
	public T replaceWithGrow(int index, T value) {
		++actualModCount;

		assertNotNegativeIndex(index);
		growToSizeOrDoNothing(index + 1);

		T oldValue = values[index];
		values[index] = value;
		return oldValue;
	}

	/**
	 * Заменяет каждый элемент в массиве результатом вызова для него функции mapper.
	 * @param mapper функция обратного вызова, заменяющая каждый элемент массива.
	 * @throws ConcurrentModificationException при попытке изменить сам массив из mapper.
	 * @throws NullPointerException если mapper равен null.
	 */
	public void replaceAll(IndexBiFunction<T, T> mapper) {
		final int EXPECTED_COUNT_MOD = ++actualModCount;

		for(int i = 0; i < size; ++i) {
			T newItem = mapper.apply(values[i], i);
			if(EXPECTED_COUNT_MOD != actualModCount) throw new ConcurrentModificationException();
			values[i] = newItem;
		}
	}

	/**
	 * Увеличивает длину массива на единицу и затем записывает элемент в конец массива.
	 * @param value добавляемое значение.
	 */
	public void addLast(T value) {
		++actualModCount;

		int lastIndex = size;
		growToSizeOrDoNothing(size + 1);
		values[lastIndex] = value;
	}

	/**
	 * Добавляет все переданные элементы в конец массива, увеличивая его длину на кол-во переданных элементов.
	 * Порядок, в котором элементы передаются методу, сохраняется.
	 * @param data добавляемые элементы.
	 * @throws NullPointerException если передаваемый массив элементов равен null.
	 */
	public void addAllOnLast(T... data) {
		++actualModCount;
		if(data.length > 0) {

			int lastIndex = size;
			growToSizeOrDoNothing(size + data.length);
			System.arraycopy(data, 0, this.values, lastIndex, data.length);
		}
	}

	/**
	 * Добавляет все переданные элементы в конец массива, увеличивая его длину на кол-во переданных элементов.
	 * Элементы добавляются в порядке их возвращения итератором.
	 * @param iterable структура данных, все элементы которой добавляются в данный массив.
	 * @throws NullPointerException если iterable равен null.
	 */
	public void addAllOnLast(Iterable<T> iterable) {
		for(T value : iterable) addLast(value);
	}

	/**
	 * Вставляет указанный элемент в указанную позицию. При этом - элемент, который ранее находился на данной
	 * позиции, и все элементы, следующие за ним, сдвигаются вверх на одну позицию.
	 * @param index позиция, в которую будет добавлен элемент
	 * @param value добавляемое значение
	 * @throws IndexOutOfBoundsException если не соблюдается условие {@code index >= 0 && index <= } {@link #size()}
	 */
	public void insert(int index, T value) {
		assertInClosedBound(index);

		++actualModCount;

		int oldSize = size;
		growToSizeOrDoNothing(size + 1);
		if(index < oldSize) {
			System.arraycopy(values, index, values, index + 1, oldSize - index);
		}
		values[index] = value;
	}

	/**
	 * <p>Добавляет указанный элемент в массив, сохраняя заданный порядок элементов, и возвращает индекс
	 * добавленного элемента. Если массив содержит несколько элементов с тем же значением, что и добавляемый
	 * элемент - метод не дает гарантий, куда будет вставлен элемент относительно элементов с тем же значением.
	 * Выполняет вставку элемента с использованием двоичного поиска.</p>
	 *
	 * <p><b>ВАЖНО!</b> Данный метод требует, чтобы массив был предварительно отсортирован и для сравнения использовался
	 * Comparator задающий тот же линейный порядок, что и порядок отсортированного массива. Если это условие
	 * не соблюдается - результат не определен.</p>
	 * @param value добавляемое значение.
	 * @param comparator объект, выполняющий упорядочивающее сравнение элементов массива.
	 * @return индекс вставки добавляемого элемента.
	 * @throws NullPointerException если comparator равен null.
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
	 * @throws IndexOutOfBoundsException если хотя бы для одного из индексов не соблюдается
	 *                                   условие {@code index >= 0 && index <= } {@link #size()}
	 */
	public void swap(int firstIndex, int secondIndex) {
		assertInBound(firstIndex);
		assertInBound(secondIndex);

		++actualModCount;

		swapAtUncheckedIndexes(firstIndex, secondIndex);
	}

	/**
	 * <p>Удаляет элемент под указанным индексом и возвращает его. На место удаленного элемента будет записан
	 * последний элемент массива и длина массива будет уменьшена на единицу.</p>
	 *
	 * <p>Данный метод не уменьшает емкость внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый
	 * данным объектом {@link DynamicArray}, используйте метод {@link #trimToSize()}.</p>
	 *
	 * <p>Данный метод работает быстрее {@link #orderedRemove(int)}. Если порядок элементов в массиве для вас не
	 * важен - для удаления рекомендуется использовать этот метод.</p>
	 *
	 * @param index индекс удаляемого элемента.
	 * @return удаляемый элемент под указанным индексом.
	 * @throws IndexOutOfBoundsException если не соблюдается условие {@code index >= 0 && index < } {@link #size()}
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
	 * <p>Удаляет элемент под указанным индексом и возвращает его. Все элементы, индекс которых больше указанного,
	 * сдвигаются вниз на одну позицию. Иначе говоря, данный метод выполняет удаление элемента с сохранением
	 * порядка для оставшихся элементов.</p>
	 *
	 * <p>Данный метод не уменьшает емкость внутреннего хранилища. Если вам необходимо уменьшить объект памяти занимаемый
	 * данным объектом {@link DynamicArray}, используйте метод {@link #trimToSize()}.</p>
	 *
	 * @param index индекс удаляемого элемента.
	 * @return удаляемый элемент под указанным индексом.
	 * @throws IndexOutOfBoundsException если не соблюдается условие {@code index >= 0 && index < } {@link #size()}
	 */
	public T orderedRemove(int index) {
		assertInBound(index);

		++actualModCount;

		return orderedRemoveAtUncheckedIndex(index);
	}

	/**
	 * <p>Удаляет последний элемент и возвращает его. Если массив пуст - возвращает null.</p>
	 *
	 * <p><b>ВАЖНО!</b> Т.к. массив допускает хранение null элементов, то возвращение данным методом
	 * null в качестве результата не гарантирует, что массив пуст. Для проверки наличия элементов
	 * в массиве используйте методы {@link #size()} или {@link #isEmpty()}.</p>
	 *
	 * @return удаленный элемент или null.
	 */
	public T removeLast() {
		++actualModCount;

		if(size > 0) {
			T removableItem = values[--size];
			values[size] = null;
			return removableItem;
		}
		return null;
	}

	/**
	 * Удаляет все элементы массива, удовлетворяющие заданному предикату.
	 * @param predicate проверяет, нужно ли удалять элемент. Вызывается для
	 *                  каждого элемента массива.
	 * @return кол-во удаленных элементов.
	 * @throws ConcurrentModificationException при попытке изменить массив из predicate.
	 * @throws NullPointerException если predicate равен null.
	 */
	public int removeIf(IndexBiPredicate<T> predicate) {
		final int EXPECTED_COUNT_MOD = ++actualModCount;

		int result = 0;
		for(int i = size - 1; i >= 0; --i) {
			if(predicate.test(values[i], i)) {
				orderedRemoveAtUncheckedIndex(i);
				++result;
			}
			if(EXPECTED_COUNT_MOD != actualModCount) throw new ConcurrentModificationException();
		}
		return result;
	}

	/**
	 * <p>Удаляет все элементы массива и уменьшает его длину до нуля.</p>
	 *
	 * <p>Данный метод не уменьшает емкость внутреннего хранилища. Если вам необходимо уменьшить объем памяти занимаемый
	 * данным объектом {@link DynamicArray}, используйте метод {@link #trimToSize()}.</p>
	 */
	public void clear() {
		++actualModCount;
		for(int to = size, i = size = 0; i < to; ++i) values[i] = null;
	}

	/**
	 * Сортирует массив в соответствии с заданным порядком.
	 * @param comparator объект, выполняющий упорядочивающее сравнение элементов массива.
	 * @throws NullPointerException если comparator равен null.
	 */
	public void sort(Comparator<T> comparator) {
		++actualModCount;

		Arrays.sort((T[]) values, 0, size, comparator);
	}

	/**
	 * Случайным образом меняет элементы местами друг с другом. Использует для выбора новых позиций элементов
	 * переданный генератор случайных или псевдослучайных чисел.
	 * @param randomGenerator генератор случайных или псевдослучайных чисел.
	 * @throws NullPointerException если randomGenerator равен null.
	 */
	public void shuffle(RandomGenerator randomGenerator) {
		for(int i = 0; i < size; ++i) {
			int randomIndex = randomGenerator.nextInt(size - i) + i;
			swapAtUncheckedIndexes(i, randomIndex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int linearSearch(T value) {
		return linearSearchInRange(value, 0, size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int linearSearch(Predicate<T> predicate) {
		return linearSearchInRange(predicate, 0, size);
	}

	/**
	 * <p>Возвращает индекс первого встретившегося элемента, часть полей которого имеет нужное значение или -1,
	 * если массив не содержит такого элемента. Если массив содержит несколько подходящих элементов - метод
	 * не дает гарантий, индекс какого именно из этих элементов будет возвращен. Поиск искомого элемента
	 * осуществляется с использованием двоичного поиска.</p>
	 *
	 * <p>В качестве компаратора выступает объект типа ToIntFunction, а не Comparator. Такое решение было принято
	 * исходя из того, что в некоторых случаях необходимо осуществить бинарный поиск по одному или нескольким полям
	 * объекта, по которым и был задан линейный порядок. При этом у вызывающего кода есть данные поля в виде самостоятельных
	 * объектов или примитивов, но нет "цельного" объекта, с которым можно было бы осуществить двоичный поиск, а создание
	 * фиктивного объекта неудобно или затратно.</p>
	 *
	 * <p>Данный метод требует, чтобы массив был предварительно отсортирован, и передаваемый компаратор задавал
	 * этот же линейный порядок. Если это условие не соблюдается - результат не определен.</p>
	 *
	 * @param comparator объект, выполняющий упорядочивающее сравнение элементов массива.
	 * @return индекс элемента, часть полей которого имеет нужное значение или -1, если таковой не был найден.
	 * @throws NullPointerException если comparator равен null.
	 */
	public int binarySearch(ToIntFunction<T> comparator) {
		return binarySearchInRange(0, size, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int frequency(Predicate<T> predicate) {
		int result = 0;
		for(int i = 0; i < size; ++i) {
			if(predicate.test(values[i])) ++result;
		}
		return result;
	}

	/**
	 * <p>Если newSize больше длины массива ({@link #size()}), то увеличивает внутреннюю емкость массива
	 * таким образом, чтобы вмещать кол-во элементов как минимум равное newSize, а длина массива станет
	 * равна newSize.</p>
	 *
	 * <p>Если значение {@code newSize >= 0 и newSize <= } {@link #size()} - метод не вносит никаких изменений.</p>
	 *
	 * @param newSize новая длина массива.
	 * @return ссылку на этот же объект.
	 * @throws NegativeSizeException если {@code newSize < 0}
	 */
	public DynamicArray<T> growToSize(int newSize) {
		++actualModCount;

		assertNotNegativeSize(newSize);
		growToSizeOrDoNothing(newSize);
		return this;
	}

	/**
	 * <p>Если index больше или равен длине массива ({@link #size()}), то увеличивает внутреннюю емкость массива
	 * таким образом, чтобы вместить элемент с указанным индексом.</p>
	 *
	 * <p>Если значение {@code index >= 0 и index < } {@link #size()} - метод не вносит никаких изменений.</p>
	 *
	 * @param index индекс, до которого увеличивается размер массива.
	 * @return ссылку на этот же объект.
	 * @throws IndexOutOfBoundsException если {@code index < 0}
	 */
	public DynamicArray<T> growToIndex(int index) {
		++actualModCount;

		assertNotNegativeIndex(index);
		growToSizeOrDoNothing(index + 1);
		return this;
	}

	/**
	 * <p>Если размер внутреннего массива больше его минимально необходимого значения в соответствии с текущей
	 * длиной объекта ({@link #size()}), то уменьшает емкость внутреннего массива, иначе - не вносит
	 * никаких изменений.</p>
	 *
	 * <p>Данный метод следует использовать в тех случаях, когда необходимо минимизировать объем памяти, занимаемый
	 * объектом DynamicArray.</p>
	 *
	 * @return true - если объем внутреннего массива был уменьшен, иначе - false.
	 */
	public boolean trimToSize() {
		++actualModCount;

		boolean isTrim = size < values.length && size >= MIN_CAPACITY;

		if(isTrim) values = Arrays.copyOf(values, size);

		return isTrim;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <R> DynamicArray<R> mappedCopy(IndexBiFunction<T, R> mapper) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		DynamicArray<R> result = new DynamicArray<>(size);
		for(int i = 0; i < size; ++i) {
			result.values[i] = mapper.apply(values[i], i);
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
	public DynamicArray<T> filteredCopy(IndexBiPredicate<T> predicate) {
		final int EXPECTED_COUNT_MOD = actualModCount;

		DynamicArray<T> result = new DynamicArray<>();
		for(int i = 0; i < size; ++i) {
			if(predicate.test(values[i], i)) result.addLast(values[i]);
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

		T result = null;
		if(size > 0) {
			result = values[0];
			for(int i = 1; i < size; ++i) {
				result = accumulator.apply(result, values[i]);
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

		T result = initValue;
		for(int i = 0; i < size; ++i) {
			result = accumulator.apply(result, values[i]);
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
		T[] result = (T[]) Array.newInstance(itemType, size);
		System.arraycopy(values, 0, result, 0, size);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndexedIterator<T> iterator() {
		return new IndexedIteratorImpl<>(actualModCount, size);
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

		for(int i = 0; i < size; ++i) {
			action.accept(values[i], i);
			if(EXPECTED_COUNT_MOD != actualModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DynamicArray<?> array = (DynamicArray<?>) o;

		boolean result = array.size == size;
		for(int i = 0; i < size && result; ++i) {
			result = Objects.equals(array.values[i], values[i]);
		}
		return result;
	}

	@Override
	public int hashCode() {
		int result = size;
		for(int i = 0; i < size; ++i) result = result * 31 + Objects.hashCode(values[i]);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder valuesToString = new StringBuilder("[");
		if(size > 0) {
			valuesToString.append(values[0]);
			for(int i = 1; i < size; ++i) valuesToString.append(',').append(values[i]);
		}
		valuesToString.append(']');

		return "DynamicArray{size=" + size + ", " + valuesToString + '}';
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
		if(!inBoundByModulo(index)) {
			throw new IndexOutOfBoundsException(
					"Expected: index >= -size() && index < size. Actual: size=" + size + ", index=" + index);
		}
	}

	private void assertNotNegativeSize(int size) {
		if(size < 0) {
			throw new NegativeSizeException("Expected: size >= 0; Actual: size=" + size);
		}
	}

	private void assertNotNegativeIndex(int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("Expected: index >= 0. Actual: index=" + index);
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

	private void growToSizeOrDoNothing(int newSize) {
		if(newSize > size) {
			size = newSize;
			if(newSize > values.length) {
				values = Arrays.copyOf(values, calculateCapacity(newSize));
			}
		}
	}

	private T orderedRemoveAtUncheckedIndex(int index) {
		T removableItem = values[index];
		if(--size > index) {
			System.arraycopy(values, index + 1, values, index, size - index);
		}
		values[size] = null;
		return removableItem;
	}

	private void swapAtUncheckedIndexes(int firstIndex, int secondIndex) {
		T first = values[firstIndex];
		values[firstIndex] = values[secondIndex];
		values[secondIndex] = first;
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
			return (E) DynamicArray.this.get(recentIndex);
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
			return (E) DynamicArray.this.get(recentIndex);
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
			return (E) DynamicArray.this.get(recentIndex);
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
			if(DynamicArray.this.actualModCount != expectedModCount) {
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
