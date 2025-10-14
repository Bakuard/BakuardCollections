package com.bakuard.collections;

import com.bakuard.collections.exception.NegativeSizeException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Расширяя {@link ReadableBits} добавляет следующие функции:<br/>
 * 1. Возможность напрямую менять состояние отдельных битов.</br>
 * 2. Операции над множеством битов - and, or, not и xor.<br/>
 * 3. Может менять свой размер путем явного вызова методов {@link #growToIndex(int)} и {@link #truncateToSize(int)}.
 * <br/><br/>
 * Данный класс не является потокобезопасным.
 */
public final class Bits implements ReadableBits {

	/**
	 * Создает и возвращает новый объект Bits зарезервированный для хранения указанного кол-ва бит. Все биты
	 * данного объекта будут установлены в единицу.
	 * @param numberBits емкость создаваемого объекта Bits.
	 */
	public static Bits filled(int numberBits) {
		Bits result = new Bits(numberBits);
		result.setAll();
		return result;
	}

	/**
	 * Создает и возвращает новый объект Bits зарезервированный для хранения указанного кол-ва бит. Все биты индексы
	 * которых указаны в массиве indexes, будут установлены в единицу. Значение остальных бит будет установленно в ноль.
	 * @param numberBits емкость создаваемого объекта Bits.
	 * @param indexes индексы бит устанавливаемых в единицу.
	 * @throws NegativeSizeException  если numberBits меньше нуля.
	 * @throws IndexOutOfBoundsException если хотя бы для одного из индексов выполняется условие:
	 *                                   index < 0 || index >= numberBits
	 */
	public static Bits of(int numberBits, int... indexes) {
		Bits result = new Bits(numberBits);
		result.setAll(indexes);
		return result;
	}

	private long[] words = {0L};
	private int size;

	/**
	 * Создает пустой объект Bits размер которого({@link #size()}) равен 0.
	 */
	public Bits() {}

	/**
	 * Создает объект Bits зарезервированный для хранения указанного кол-ва бит. Значение любого бита в заданном
	 * диапазоне, после вызова этого конструктора, будет равняться 0.
	 * @param numberBits емкость создаваемого объекта Bits.
	 * @throws NegativeSizeException если numberBits меньше нуля.
	 */
	public Bits(int numberBits) {
		assertNotNegativeSize(numberBits);
		growToIndexOrDoNothing(numberBits - 1);
	}

	/**
	 * Создает точную копию переданного объекта Bits.
	 * @param other объект bits, для которого создается копия.
	 */
	public Bits(ReadableBits other) {
		Bits bits = (Bits)other;
		size = bits.size;
		words = bits.words.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean get(int index) throws IndexOutOfBoundsException {
		assertInHalfOpenInterval(index);
		return unsafeGet(index);
	}

	/**
	 * Устанавливает бит с указанным индексом в единицу.
	 * @param index индекс бита устанавливаемого в единицу.
	 * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #size()}
	 */
	public void set(int index) throws IndexOutOfBoundsException {
		assertInHalfOpenInterval(index);
		words[index >>> 6] |= 1L << index;
	}

	/**
	 * Устанавливает значение для каждого бита, индекс которого указан в параметре indexes, в единицу. Если хотя
	 * бы один из индексов не соответствует условию index >= 0 && index < {@link #size()}, выполнения метода
	 * будет прервано и ни один из указанных бит не будет изменен. Если метод вызывается без аргументов - он не
	 * вносит никаких изменений.
	 * @return ссылку на этот же объект.
	 * @param indexes индексы бит устанавливаемых в единицу.
	 * @throws IndexOutOfBoundsException если для одного из указанных индексов не выполняется
	 *                                   условие index >= 0 && index < {@link #size()}.
	 */
	public Bits setAll(int... indexes) throws IndexOutOfBoundsException {
		for(int i = 0; i < indexes.length; i++) assertInHalfOpenInterval(indexes[i]);
		for(int i = 0; i < indexes.length; i++) {
			int index = indexes[i];
			words[index >>> 6] |= 1L << index;
		}
		return this;
	}

	/**
	 * Устанавливает все биты в диапазоне [fromIndex, toIndex) в единицу. В случае, если fromIndex == toIndex, метод
	 * не делает никаких изменений.
	 * @param fromIndex индекс задающий начало заполняемого диапазонна.
	 * @param toIndex индекс задающий конец заполняемого диапазонна.
	 * @return ссылку на этот же объект.
	 * @throws IndexOutOfBoundsException генерируется в одном из следующих случаев: <br/>
	 *                                   1. Если fromIndex > toIndex; <br/>
	 *                                   2. Если fromIndex < 0; <br/>
	 *                                   3. Если toIndex > {@link #size()}.
	 *
	 */
	public Bits setRange(int fromIndex, int toIndex) throws IndexOutOfBoundsException {
		if(fromIndex > toIndex || fromIndex < 0 || toIndex > size) {
			throw new IndexOutOfBoundsException("Incorrect interval [fromIndex=" + fromIndex +
					", toIndex=" + toIndex + ')');
		}
		while(fromIndex < toIndex) {
			words[fromIndex >>> 6] |= 1L << fromIndex;
			++fromIndex;
		}
		return this;
	}

	/**
	 * Устанавливает все биты в единицу, при этом размер объекта Bits не изменяется.
	 * @return ссылку на этот же объект.
	 */
	public Bits setAll() {
		if(size > 0) {
			Arrays.fill(words, -1L);
			words[words.length - 1] &= -1L >>> (64 - size);
		}
		return this;
	}

	/**
	 * Устанавливает бит с указанным индексом в ноль.
	 * @param index индекс бита устанавливаемого в ноль.
	 * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #size()}.
	 */
	public void clear(int index) throws IndexOutOfBoundsException {
		assertInHalfOpenInterval(index);
		words[index >>> 6] &= ~(1L << index);
	}

	/**
	 * Уста наливает значение для каждого бита, индекс которого указан в параметре indexes, в ноль. Если хотя бы
	 * один из индексов не соответствует условию index >= 0 && index < {@link #size()}, выполнения метода будет
	 * прервано и ни один из указанных бит не будет изменен. Если метод вызывается без аргументов - он не вносит
	 * никаких изменений.
	 * @param indexes индексы бит устанавливаемых в ноль.
	 * @return ссылку на этот же объект.
	 * @throws IndexOutOfBoundsException если для одного из переданных индексов не выполняется
	 *                                   условие index >= 0 && index < {@link #size()}.
	 */
	public Bits clearAll(int... indexes) throws IndexOutOfBoundsException {
		for(int i = 0; i < indexes.length; i++) assertInHalfOpenInterval(indexes[i]);
		for(int i = 0; i < indexes.length; i++) {
			int index = indexes[i];
			words[index >>> 6] &= ~(1L << index);
		}
		return this;
	}

	/**
	 * Устанавливает все биты в диапазоне [fromIndex, toIndex) в ноль. В случае, если fromIndex == toIndex, метод
	 * не делает никаких изменений.
	 * @param fromIndex индекс задающий начало заполняемого диапазонна.
	 * @param toIndex индекс задающий конец заполняемого диапазонно.
	 * @return ссылку на этот же объект.
	 * @throws IndexOutOfBoundsException генерируется в одном из следующих случаев: <br/>
	 *                                   1. Если fromIndex > toIndex; <br/>
	 *                                   2. Если fromIndex < 0; <br/>
	 *                                   3. Если toIndex > {@link #size()}.
	 */
	public Bits clearRange(int fromIndex, int toIndex) throws IndexOutOfBoundsException {
		if(fromIndex > toIndex || fromIndex < 0 || toIndex > size) {
			throw new IndexOutOfBoundsException("Incorrect interval [fromIndex=" + fromIndex +
					", toIndex=" + toIndex + ')');
		}
		while (fromIndex < toIndex) {
			words[fromIndex >>> 6] &= ~(1L << fromIndex);
			++fromIndex;
		}
		return this;
	}

	/**
	 * Устанавливает значение всех бит в ноль, при этом размер объекта Bits не изменяется.
	 * @return ссылку на этот же объект.
	 */
	public Bits clearAll() {
		Arrays.fill(words, 0L);
		return this;
	}

	/**
	 * Инвертирует бит под указанным индексом.
	 * @param index индекс инвертируемого бита.
	 * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #size()}.
	 */
	public void flip(int index) throws IndexOutOfBoundsException {
		assertInHalfOpenInterval(index);
		words[index >>> 6] ^= (1L << index);
	}

	/**
	 * Выполняет операцию пересечения двух множеств. Метод записывает результат операции в объект Bits, у которого
	 * был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда выступает объект,
	 * у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве аргумента. Если
	 * операнды имеют разный размер, то операция выполняется таким образом, как будто недостающие биты более
	 * короткого операнда заполнены нулями.
	 * @param other второй операнд операции пересечения множеств.
	 * @return объект, у которого был вызван данный метод.
	 */
	public Bits and(ReadableBits other) {
		Bits otherBits = (Bits)other;
		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = words.length - 1; i >= commonWords; --i) words[i] = 0L;
		for(int i = 0; i < commonWords; ++i) words[i] &= otherBits.words[i];
		return this;
	}

	/**
	 * Выполняет операцию объединения двух множеств. Метод записывает результат операции в объект Bits, у которого
	 * был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда выступает объект,
	 * у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве аргумента. Если размер
	 * объекта (см. {@link #size()}), у которого вызван метод, меньше чем other, то его размер увеличивается до
	 * размера other. Если размер передаваемого объекта меньше, то операция выполняется таким образом, как будто
	 * недостающие биты второго операнда заполнены нулями.
	 * @param other второй операнд операции объединения множеств.
	 * @return объект, у которого был вызван данный метод.
	 */
	public Bits or(ReadableBits other) {
		Bits otherBits = (Bits)other;
		growToIndexOrDoNothing(otherBits.size - 1);
		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = 0; i < commonWords; ++i) words[i] |= otherBits.words[i];
		return this;
	}

	/**
	 * Выполняет операцию симметричной разности двух множеств. Метод записывает результат операции в объект Bits,
	 * у которого был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда
	 * выступает объект, у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве
	 * аргумента. Если размер объекта (см. {@link #size()}), у которого вызван метод, меньше чем other, то его
	 * размер увеличивается до размера other. Если размер передаваемого объекта меньше, то операция выполняется
	 * таким образом, как будто недостающие биты второго операнда заполнены нулями.
	 * @param other второй операнд для операции xor.
	 * @return объект, у которого был вызван данный метод.
	 */
	public Bits xor(ReadableBits other) {
		Bits otherBits = (Bits)other;
		growToIndexOrDoNothing(otherBits.size - 1);
		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = 0; i < commonWords; ++i) words[i] ^= otherBits.words[i];
		return this;
	}

	/**
	 * Выполняет операцию вычитания двух множеств. Метод записывает результат операции в объект Bits,
	 * у которого был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда
	 * выступает объект, у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве
	 * аргумента. Если операнды имеют разный размер, то операция выполняется таким образом, как будто недостающие
	 * биты более короткого операнда заполнены нулями.
	 * @param other второй операнд для операции разности множеств.
	 * @return объект, у которого был вызван данный метод.
	 */
	public Bits andNot(ReadableBits other) {
		Bits otherBits = (Bits)other;
		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = 0; i < commonWords; ++i) words[i] &= ~otherBits.words[i];
		return this;
	}

	/**
	 * Выполняет операцию дополнения множества. Метод записывает результат операции в объект Bits, у которого
	 * был вызван данный метод и возвращает ссылку на этот же объект.
	 * @return объект, у которого был вызван данный метод.
	 */
	public Bits not() {
		if(size > 0) {
			for (int i = 0; i < words.length; ++i) words[i] = ~words[i];
			words[words.length - 1] &= -1L >>> (64 - size);
		}
		return this;
	}

	/**
	 * Перезаписывает состояние текущего объекта копируя состояние переданного объекта src. Метод возвращает
	 * ссылку на тот же объект, у которого он был вызван.
	 * @param src объект Bits, состояние которого копируется.
	 * @return объект, у которого был вызван данный метод.
	 */
	public Bits copyFullStateFrom(ReadableBits src) {
		Bits srcBits = (Bits) src;
		if(words.length != srcBits.words.length) words = srcBits.words.clone();
		else System.arraycopy(srcBits.words, 0, words, 0, srcBits.words.length);
		size = srcBits.size;
		return this;
	}

	/**
	 * Копирует заданный диапазон бит из объекта src в текущий объект. Копируемая область задается индексом
	 * самого первого её бита и кол-вом копируемых бит. Область целевого объекта Bits, которая будет заменена
	 * копируемым диапазонном бит, задается индексом самого первого бита этой области, а её размер равен кол-ву
	 * копируемых бит. Метод возвращает кол-во бит, которые были перезаписаны у целевого объекта.<br/><br/>
	 * Особые случаи:<br/>
	 * 1. Если length равен нулю - метод не вносит никаких изменений.<br/>
	 * 2. Если length больше чем максимальный возможный размер копируемой области (с учетом srcPos), то
	 * в качестве размера копируемой области будет взято максимальное доступное значение. В таком случае,
	 * область вставки целевого объекта будет иметь тот же размер, что и копируемая область.<br/>
	 * 3. Если копируемая область не помещается целиком в заменяемую область вставки целевого объекта, то все
	 * биты для которых не хватило места будут отброшены.
	 *
	 * @param src     объект Bits из которого копируется заданный диапазон бит.
	 * @param srcPos  индекс задающий начало копируемого диапазона из объекта src.
	 * @param destPos индекс задающий начало заменяемого диапазона в текущем объекте.
	 * @param length  кол-во копируемых бит.
	 * @return кол-во перезаписанных бит у объекта вызвавшего данный метод (целевого объекта).
	 * @throws NullPointerException      если src имеет значение null.
	 * @throws IndexOutOfBoundsException если выполняется хотя бы одно из следующих условий:<br/>
	 *                                   1. srcPos < 0 <br/>
	 *                                   2. srcPos >= src.{@link #size()} <br/>
	 *                                   3. destPos < 0 <br/>
	 *                                   4. destPos >= this.{@link #size()} <br/>
	 *                                   5. length < 0
	 */
	public int copyRangeFrom(ReadableBits src, int srcPos, int destPos, int length) {
		Objects.requireNonNull(src, "src can not be null.");

		Bits srcBits = (Bits)src;

		if(srcPos < 0 || destPos < 0 || srcPos >= srcBits.size() || destPos >= size() || length < 0) {
			throw new IndexOutOfBoundsException(
					"srcPos=" + srcPos +
							", destPos=" + destPos +
							", length=" + length +
							", src.size()=" + srcBits.size() +
							", dest.size()=" + size()
			);
		}

		int srcRange = Math.min(srcBits.size() - srcPos, length);
		int destRange = Math.min(size() - destPos, length);
		int actualRange = Math.min(srcRange, destRange);

		if(srcPos >= destPos) {
			for(int i = 0; i < actualRange; ++i) {
				int srcIndex = srcPos + i;
				int destIndex = destPos + i;
				if ((srcBits.words[srcIndex >>> 6] & (1L << srcIndex)) != 0L) {
					words[destIndex >>> 6] |= 1L << destIndex;
				} else {
					words[destIndex >>> 6] &= ~(1L << destIndex);
				}
			}
		} else {
			for(int i = actualRange - 1; i >= 0; --i) {
				int srcIndex = srcPos + i;
				int destIndex = destPos + i;
				if ((srcBits.words[srcIndex >>> 6] & (1L << srcIndex)) != 0L) {
					words[destIndex >>> 6] |= 1L << destIndex;
				} else {
					words[destIndex >>> 6] &= ~(1L << destIndex);
				}
			}
		}

		return actualRange;
	}

	/**
	 * Увеличивает емкость текущего объекта Bits таким образом, чтобы индекс самого старшего бита был равен index.
	 * Если index >= 0 и при этом меньше {@link #size()}, то метод ничего не делает.
	 * Все старшие биты добавленные в результате вызова этого метода будут установленны в 0.
	 * @param index индекс бита, до которого нужно увеличить емкость текущего объекта Bits.
	 * @return ссылку на этот же объект Bits.
	 * @throws IndexOutOfBoundsException если index меньше нуля.
	 */
	public Bits growToIndex(int index) {
		assertNotNegativeIndex(index);
		growToIndexOrDoNothing(index);
		return this;
	}

	/**
	 * Обрезает текущий объект Bits до указанного кол-ва бит. Если передаваемый аргумент больше или равен текущему
	 * кол-ву бит - не оказывает никакого эффекта.
	 * @param newSize кол-во бит, до которого нужно обрезать емкость текущего объекта Bits.
	 * @return ссылку на этот же объект.
	 * @throws NegativeSizeException если newSize меньше нуля.
	 */
	public Bits truncateToSize(int newSize) {
		assertNotNegativeSize(newSize);

		if(newSize < size) {
			size = newSize;

			int numberWords = (Math.max(newSize - 1, 0) >>> 6) + 1;
			if(numberWords < words.length) {
				long[] newWords = new long[numberWords];
				System.arraycopy(words, 0, newWords, 0, numberWords);
				words = newWords;
			}

			if(newSize > 0) words[words.length - 1] &= -1L >>> (64 - newSize);
			else words[0] = 0L;
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public int cardinality() {
		int countBits = 0;
		for(int i = 0; i < words.length; ++i) countBits += Long.bitCount(words[i]);
		return countBits;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getHighBitIndex() {
		int index = -1;
		for(int i = words.length - 1; i >= 0 && index == -1; --i) {
			if(words[i] != 0) {
				index = (i << 6) + (63 - Long.numberOfLeadingZeros(words[i]));
			}
		}
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isClear() {
		boolean result = true;
		for(int i = 0; i < words.length && result; i++) {
			result = words[i] == 0L;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	public int nextSetBit(int fromIndex) {
		assertNotNegativeIndex(fromIndex);

		if(fromIndex < size) {
			int wordIndex = fromIndex >>> 6;
			long word = words[wordIndex] >>> fromIndex;
			if(word != 0) return fromIndex + Long.numberOfTrailingZeros(word);

			wordIndex += 1;
			while(wordIndex < words.length && words[wordIndex] == 0) ++wordIndex;

			if(wordIndex < words.length) {
				return (wordIndex << 6) + Long.numberOfTrailingZeros(words[wordIndex]);
			}
		}

		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public int nextClearBit(int fromIndex) {
		assertNotNegativeIndex(fromIndex);

		if(fromIndex < size) {
			int wordIndex = fromIndex >>> 6;
			long word = ~(words[wordIndex] >> fromIndex);
			if(word != 0) return fromIndex + Long.numberOfTrailingZeros(word);

			wordIndex += 1;
			while(wordIndex < words.length && ~words[wordIndex] == 0) ++wordIndex;

			if(wordIndex < words.length) {
				int result = (wordIndex << 6) + Long.numberOfTrailingZeros(~words[wordIndex]);
				if(result < size) return result;
			}
		}

		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(ReadableBits other) {
		Bits otherBits = (Bits)other;

		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = 0; i < commonWords; i++) {
			if((words[i] & otherBits.words[i]) != otherBits.words[i]) return false;
		}

		for(int i = words.length; i < otherBits.words.length; i++) {
			if(otherBits.words[i] != 0L) return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean intersect(ReadableBits other) {
		Bits otherBits = (Bits)other;

		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = 0; i < commonWords; i++) {
			if((words[i] & otherBits.words[i]) != 0L) return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean inBound(int index) {
		return index >= 0 && index < size;
	}

	/**
	 * Два объекта Bits считаются одинаковыми если их размеры (значения возвращаемые методом {@link #size()})
	 * равны и значения всех бит попарно равны.
	 * @param other объект типа Bits с которым производится сравнение.
	 * @return true - если объекты равны, false - в противном случае.
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Bits otherBits = (Bits) other;

		boolean isEqual = size == otherBits.size;
		for(int i = 0; i < words.length && isEqual; i++) {
			isEqual = words[i] == otherBits.words[i];
		}

		return isEqual;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equalsIgnoreSize(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Bits otherBits = (Bits) other;

		boolean isEqual = true;
		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = 0; i < commonWords && isEqual; i++) {
			isEqual = words[i] == otherBits.words[i];
		}

		long[] biggest = words.length > otherBits.words.length ? words : otherBits.words;
		for(int i = commonWords; i < biggest.length && isEqual; i++) {
			isEqual = biggest[i] == 0;
		}

		return isEqual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(ReadableBits other) {
		Bits otherBits = (Bits)other;
		int result = size - otherBits.size;
		for(int i = words.length - 1; i >= 0 && result == 0; --i) {
			result = Long.compareUnsigned(words[i], otherBits.words[i]);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareIgnoreSize(ReadableBits other) {
		Bits otherBits = (Bits) other;

		int result = 0;

		for(int i = words.length - 1; i >= otherBits.words.length && result == 0; --i) {
			result = Long.compareUnsigned(words[i], 0L);
		}

		for(int i = otherBits.words.length - 1; i >= words.length && result == 0; --i) {
			result = Long.compareUnsigned(0L, otherBits.words[i]);
		}

		int commonWords = Math.min(words.length, otherBits.words.length);
		for(int i = commonWords - 1; i >= 0 && result == 0; --i) {
			result = Long.compareUnsigned(words[i], otherBits.words[i]);
		}

		return result;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = result * 31 + Arrays.hashCode(words);
		result = result * 31 + size;
		return result;
	}

	public int hashCodeIgnoreSize() {
		int result = 17;
		result = result * 31 + Arrays.hashCode(words);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder array = new StringBuilder();
		array.append(Bits.toBinaryString(words[0], 64));
		for(int i = 1; i < words.length; i++) array.append(',').append(Bits.toBinaryString(words[i], 64));
		return "Bits {size=" + size + ", count words=" + words.length + ", words=[" + array + "]}";
	}

	/**
	 * {@inheritDoc}
	 */
	public String toBinaryString() {
		StringBuilder result = new StringBuilder();

		if(size > 0) {
			long lastWord = words[words.length - 1];
			int lastWordBitsNumber = (size & 63) == 0 ? 64 : size & 63;
			result.append(Bits.toBinaryString(lastWord, lastWordBitsNumber));

			for(int i = words.length - 2; i >= 0; --i) {
				result.append(Bits.toBinaryString(words[i], 64));
			}
		}

		return result.toString();
	}


	private void assertInHalfOpenInterval(int index) {
		if(index < 0 || index >= size)
			throw new IndexOutOfBoundsException(
					"Expected: index >= 0 && index < size; Actual: index=" + index + ", size=" + size);
	}

	private void assertNotNegativeIndex(int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException("Expected: index >= 0; Actual: index=" + index);
		}
	}

	private void assertNotNegativeSize(int size) {
		if(size < 0) {
			throw new NegativeSizeException("Expected: size >= 0; Actual: size=" + size);
		}
	}

	private void growToIndexOrDoNothing(int index) {
		if(index >= size) {
			size = index + 1;

			int numberWords = (index >>> 6) + 1;
			if(numberWords > words.length) {
				long[] newWords = new long[numberWords];
				System.arraycopy(words, 0, newWords, 0, words.length);
				words = newWords;
			}
		}
	}

	private boolean unsafeGet(int index) {
		return (words[index >>> 6] & (1L << index)) != 0L;
	}

	private static String toBinaryString(long value, final int bitsNumber) {
		char[] chars = new char[bitsNumber];
		for(int i = 0; i < bitsNumber; i++) {
			chars[bitsNumber - 1 - i] = (char)(((value >> i) & 1L) + '0');
		}
		return new String(chars);
	}
}
