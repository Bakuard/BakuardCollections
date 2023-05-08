package com.bakuard.collections.mutable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Класс Bits представляет собой КОНЕЧНОЕ упорядоченное множество логических значений. Позволяет выполнять
 * операции над множеством битов - and, or, not, xor, а также комбинировать эти операции. Также позволяет проверять
 * отношение между множествами такие как включение, строгое включение, пересечение, эквивалентность и
 * линейный порядок. Используется как аналог boolean массивов расходующий меньше памяти (на одно значение - один
 * бит). В отличие от массива может менять свой размер путем явного вызова методов expandTo() и compressTo().
 */
public final class Bits implements Comparable<Bits> {

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
     * @throws IndexOutOfBoundsException если numberBits меньше нуля.
     */
    public Bits(int numberBits) {
        if(numberBits < 0)
            throw new IndexOutOfBoundsException("numberBits must be greater or equal 0. numberBits = " + numberBits);
        expandTo(numberBits);
    }

    /**
     * Создает точную копию переданного объекта Bits.
     * @param other объект bits, для которого создается копия.
     */
    public Bits(Bits other) {
        size = other.size;
        words = new long[other.words.length];
        System.arraycopy(other.words, 0, words, 0, other.words.length);
    }

    /**
     * Возвращает значение бита с указанным индексом. Возвращает true - если бит установлен в 1, false - в противном
     * случае.
     * @param index индекс считываемого бита.
     * @return true - если бит установлен в 1, false - в противном случае.
     * @throws IndexOutOfBoundsException если не выполняется условие index >= 0 && index < {@link #size()}
     */
    public boolean get(int index) throws IndexOutOfBoundsException {
        assertInHalfOpenInterval(index);
        return (words[index >>> 6] & (1L << index)) != 0L;
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
     * @param indexes индексы бит устанавливаемых в единицу.
     * @throws IndexOutOfBoundsException если для одного из указанных индексов не выполняется
     *                                   условие index >= 0 && index < {@link #size()}.
     */
    public void setAll(int... indexes) throws IndexOutOfBoundsException {
        for(int i = 0; i < indexes.length; i++) assertInHalfOpenInterval(indexes[i]);
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            words[index >>> 6] |= 1L << index;
        }
    }

    /**
     * Устанавливает все биты в диапазоне [fromIndex, toIndex) в единицу. В случае, если fromIndex == toIndex, метод
     * не делает никаких изменений.
     * @param fromIndex индекс задающий начало заполняемого диапазонна.
     * @param toIndex индекс задающий конец заполняемого диапазонна.
     * @throws IndexOutOfBoundsException генерируется в одном из следующих случаев: <br/>
     *                                   1. Если fromIndex > toIndex; <br/>
     *                                   2. Если fromIndex < 0; <br/>
     *                                   3. Если toIndex > {@link #size()}.
     */
    public void setRange(int fromIndex, int toIndex) throws IndexOutOfBoundsException {
        if(fromIndex > toIndex || fromIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException("Incorrect interval [fromIndex=" + fromIndex +
                    ", toIndex=" + toIndex + ')');
        }
        while(fromIndex < toIndex) {
            words[fromIndex >>> 6] |= 1L << fromIndex;
            ++fromIndex;
        }
    }

    /**
     * Устанавливает все биты в единицу, при этом размер объекта Bits не изменяется.
     */
    public void setAll() {
        Arrays.fill(words, -1L);
        words[words.length - 1] &= -1L >>> (64 - size);
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
     * @throws IndexOutOfBoundsException если для одного из переданных индексов не выполняется
     *                                   условие index >= 0 && index < {@link #size()}.
     */
    public void clearAll(int... indexes) throws IndexOutOfBoundsException {
        for(int i = 0; i < indexes.length; i++) assertInHalfOpenInterval(indexes[i]);
        for(int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            words[index >>> 6] &= ~(1L << index);
        }
    }

    /**
     * Устанавливает все биты в диапазоне [fromIndex, toIndex) в ноль. В случае, если fromIndex == toIndex, метод
     * не делает никаких изменений.
     * @param fromIndex индекс задающий начало заполняемого диапазонна.
     * @param toIndex индекс задающий конец заполняемого диапазонно.
     * @throws IndexOutOfBoundsException генерируется в одном из следующих случаев: <br/>
     *                                   1. Если fromIndex > toIndex; <br/>
     *                                   2. Если fromIndex < 0; <br/>
     *                                   3. Если toIndex > {@link #size()}.
     */
    public void clearRange(int fromIndex, int toIndex) throws IndexOutOfBoundsException {
        if(fromIndex > toIndex || fromIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException("Incorrect interval [fromIndex=" + fromIndex +
                    ", toIndex=" + toIndex + ')');
        }
        while (fromIndex < toIndex) {
            words[fromIndex >>> 6] &= ~(1L << fromIndex);
            ++fromIndex;
        }
    }

    /**
     * Устанавливает значение всех бит в ноль, при этом размер объекта Bits не изменяется.
     */
    public void clearAll() {
        Arrays.fill(words, 0L);
    }

    /**
     * Выполняет операцию пересечения двух множеств. Метод записывает результат операции в объект Bits, у которого
     * был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда выступает объект
     * у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве аргумента. Если
     * операнды имеют разный размер, то операция выполняется таким образом, как будто недостающие биты более
     * короткого операнда заполнены нулями.
     * @param other второй операнд операции пересечения множеств.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits and(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = words.length - 1; i >= commonWords; --i) words[i] = 0L;
        for(int i = 0; i < commonWords; ++i) words[i] &= other.words[i];
        return this;
    }

    /**
     * Выполняет операцию объединения двух множеств. Метод записывает результат операции в объект Bits, у которого
     * был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда выступает объект
     * у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве аргумента. Если размер
     * объекта (см. {@link #size()}), у которого вызван метод, меньше чем other, то его размер увеличивается до
     * размера other. Если размер передаваемого объекта меньше, то операция выполняется таким образом, как будто
     * недостающие биты второго операнда заполнены нулями.
     * @param other второй операнд операции объединения множеств.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits or(Bits other) {
        expandTo(other.size);
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; ++i) words[i] |= other.words[i];
        return this;
    }

    /**
     * Выполняет операцию симметричной разности двух множеств. Метод записывает результат операции в объект Bits,
     * у которого был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда
     * выступает объект у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве
     * аргумента. Если размер объекта (см. {@link #size()}), у которого вызван метод, меньше чем other, то его
     * размер увеличивается до размера other. Если размер передаваемого объекта меньше, то операция выполняется
     * таким образом, как будто недостающие биты второго операнда заполнены нулями.
     * @param other второй операнд для операции xor.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits xor(Bits other) {
        expandTo(other.size);
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; ++i) words[i] ^= other.words[i];
        return this;
    }

    /**
     * Выполняет операцию вычитания двух множеств. Метод записывает результат операции в объект Bits,
     * у которого был вызван данный метод и возвращает ссылку на этот же объект. В качестве первого операнда
     * выступает объект у которого вызывается данный метод, а второго операнда - объект передаваемый в качестве
     * аргумента. Если операнды имеют разный размер, то операция выполняется таким образом, как будто недостающие
     * биты более короткого операнда заполнены нулями.
     * @param other второй операнд для операции разности множеств.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits andNot(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; ++i) words[i] &= ~other.words[i];
        return this;
    }

    /**
     * Выполняет операцию дополнения множества. Метод записывает результат операции в объект Bits, у которого
     * был вызван данный метод и возвращает ссылку на этот же объект.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits not() {
        for(int i = 0; i < words.length; ++i) words[i] = ~words[i];
        words[words.length - 1] &= -1L >>> (64 - size);
        return this;
    }

    /**
     * Перезаписывает состояние текущего объекта копируя состояние переданного объекта src. Метод возвращает
     * ссылку на тот же объект, у которого он был вызван.
     * @param src объект Bits, состояние которого копируется.
     * @return объект, у которого был вызван данный метод.
     */
    public Bits copyAll(Bits src) {
        if(words.length != src.words.length) words = Arrays.copyOf(src.words, src.words.length);
        else System.arraycopy(src.words, 0, words, 0, src.words.length);
        size = src.size;
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
     *    в качестве размера копируемой области будет взято максимальное доступное значение. В таком случае,
     *    область вставки целевого объекта будет иметь тот же размер, что и копируемая область.<br/>
     * 3. Если копируемая область не помещается целиком в заменяемую область вставки целевого объекта, то все
     *    биты для которых не хватило места будут отброшены.
     * @param src объект Bits из которого копируется заданный диапазон бит.
     * @param srcPos индекс задающий начало копируемого диапазона.
     * @param length кол-во копируемых бит.
     * @param destPos индекс задающий область целевого объекта, которая будет заменена копируемым диапазонном.
     * @return кол-во перезаписанных бит у объекта вызвавшего данный метод (целевого объекта).
     * @throws NullPointerException если src имеет значение null.
     * @throws IndexOutOfBoundsException если выполняется хотя бы одно из следующих условий:<br/>
     *                                   1. srcPos < 0 <br/>
     *                                   2. srcPos >= src.{@link #size()} <br/>
     *                                   3. destPos < 0 <br/>
     *                                   4. destPos >= this.{@link #size()} <br/>
     *                                   5. length < 0
     */
    public int copyRange(Bits src, int srcPos, int length, int destPos) {
        Objects.requireNonNull(src, "src can not be null.");

        if(srcPos < 0 || destPos < 0 || srcPos >= src.size() || destPos >= size() || length < 0) {
            throw new IndexOutOfBoundsException(
                    "srcPos=" + srcPos +
                            ", destPos=" + destPos +
                            ", length=" + length +
                            ", src.size()=" + src.size() +
                            ", dest.size()=" + size()
            );
        }

        int srcRange = Math.min(src.size() - srcPos, length);
        int destRange = Math.min(size() - destPos, length);
        int actualRange = Math.min(srcRange, destRange);

        if(srcPos >= destPos) {
            for(int i = 0; i < actualRange; ++i) {
                int srcIndex = srcPos + i;
                int destIndex = destPos + i;
                if ((src.words[srcIndex >>> 6] & (1L << srcIndex)) != 0L) {
                    words[destIndex >>> 6] |= 1L << destIndex;
                } else {
                    words[destIndex >>> 6] &= ~(1L << destIndex);
                }
            }
        } else {
            for(int i = actualRange - 1; i >= 0; --i) {
                int srcIndex = srcPos + i;
                int destIndex = destPos + i;
                if ((src.words[srcIndex >>> 6] & (1L << srcIndex)) != 0L) {
                    words[destIndex >>> 6] |= 1L << destIndex;
                } else {
                    words[destIndex >>> 6] &= ~(1L << destIndex);
                }
            }
        }

        return actualRange;
    }

    /**
     * Увеличивает емкость текущего объекта Bits до указанного кол-ва бит. Если передаваемый аргумент меньше или
     * равен текущему кол-ву бит - не оказывает никакого эффекта. Все старшие биты добавленные в результате
     * вызова этого метода будут установленны в 0.
     * @param newSize кол-во бит до которого нужно расширить текущий объект Bits.
     * @return true - передаваемый аргумент больше текущего размера Bits ({@link #size()}) и, как следствие,
     *                вызов метода изменил размер Bits, иначе - false.
     */
    public boolean expandTo(int newSize) {
        boolean isExpand = newSize > size;

        if(isExpand) {
            size = newSize;

            int numberWords = (newSize >>> 6) + 1;
            if(numberWords > words.length) {
                long[] newWords = new long[numberWords];
                System.arraycopy(words, 0, newWords, 0, words.length);
                words = newWords;
            }
        }

        return isExpand;
    }

    /**
     * Уменьшает емкость текущего объекта Bits до указанного кол-ва бит. Вызов данного метода может привести к
     * уменьшению объема памяти, занимаемому данным объектом. Если передаваемый аргумент больше или равен текущему
     * кол-ву бит, или меньше нуля - не оказывает никакого эффекта.
     * @param newSize кол-во бит до которого нужно сузить текущий объект Bits.
     * @return true - передаваемый аргумент меньше текущего размера Bits ({@link #size()}) и вызов метода
     *                изменил размер Bits, иначе - false.
     */
    public boolean compressTo(int newSize) {
        boolean isCompress = newSize < size && newSize >= 0;

        if(isCompress) {
            size = newSize;

            int numberWords = (newSize >>> 6) + 1;
            if(numberWords < words.length) {
                long[] newWords = new long[numberWords];
                System.arraycopy(words, 0, newWords, 0, numberWords);
                words = newWords;
            }

            int indexBitInWord = (newSize - 1) % 64;
            if(newSize > 0) words[newSize >>> 6] &= -1L >>> (63 - indexBitInWord);
            else words[0] = 0L;
        }

        return isCompress;
    }

    /**
     * Возвращает кол-во бит, установленных в единицу.
     * @return кол-во бит установленных в единицу.
     */
    public int cardinality() {
        int countBits = 0;
        for(int i = 0; i < words.length; ++i) countBits += Long.bitCount(words[i]);
        return countBits;
    }

    /**
     * Возвращает индекс самого старшего бита установленного в единицу. Если все биты данного объекта Bits
     * имеют значение 0, то метод вернет -1.
     * @return индекс самого старшего бита установленного в единицу или -1.
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
     * Возвращает true, если для всех значений начиная с индекса 0 и заканчивая индексом
     * {@link #size()} - 1 метод {@link #get(int)} возвращает false.
     * @return true - если Bits не содержит ни одной единицы, false - в противном случае.
     */
    public boolean isClean() {
        for(int i = 0; i < words.length; i++) {
            if(words[i] != 0L) return false;
        }
        return true;
    }

    /**
     * Возвращает размер Bits в битах доступных для изменения. Обратите внимание - данный метод
     * НЕ возвращает логический размер объекта Bits. Данный метод выполняет ту же функцию, что и переменная
     * length у массивов.
     * @return кол-во бит доступных для изменения.
     */
    public int size() {
        return size;
    }

    /**
     * Возвращает индекс первого встретившегося бита установленного в единицу. Поиск ведется начиная с бита,
     * индекс которого указан в качестве аргумента, включая его. Биты перебираются в порядке возрастания их
     * индексов. Если начиная с бита с указанным индексом нет ни одного единичного бита - метод вернет -1.
     * Если fromIndex >= {@link #size()}, то метод вернет -1.
     * @param fromIndex индекс бита с которого начинается поиск.
     * @return индекс первого встретившегося бита установленного в единицу или -1.
     * @throws IndexOutOfBoundsException если fromIndex < 0.
     */
    public int nextSetBit(int fromIndex) {
        if(fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        } else if(fromIndex < size) {
            int indexWord = fromIndex >>> 6;
            long word = words[indexWord] >>> fromIndex;
            if(word != 0) return fromIndex + Long.numberOfTrailingZeros(word);

            indexWord += 1;
            for(; indexWord < words.length; ++indexWord) {
                word = words[indexWord];
                if(word != 0) return (indexWord << 6) + Long.numberOfTrailingZeros(word);
            }
        }
        return -1;
    }

    /**
     * Возвращает индекс паевого встретившегося бита установленного в ноль. Поиск ведется начиная с бита,
     * индекс которого указан в качестве аргумента, включая его. Биты перебираются в порядке возрастания их
     * индексов. Если начиная с бита с указанным индексом нет ни одного нулевого бита - метод вернет -1.
     * Если fromIndex >= {@link #size()}, то метод вернет -1.
     * @param fromIndex индекс бита с которого начинается поиск.
     * @return индекс первого встретившегося бита установленного в ноль или -1.
     * @throws IndexOutOfBoundsException если fromIndex < 0.
     */
    public int nextClearBit(int fromIndex) {
        if(fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        } else if(fromIndex < size) {
            int indexWord = fromIndex >>> 6;
            long word = ~(words[indexWord] >> fromIndex);
            if(word != 0) return fromIndex + Long.numberOfTrailingZeros(word);

            indexWord += 1;
            while(indexWord < words.length && ~words[indexWord] == 0) {
                ++indexWord;
            }

            word = ~words[indexWord];
            if(word != 0) {
                int result = (indexWord << 6) + Long.numberOfTrailingZeros(word);
                if(result < size) return result;
            }
        }
        return -1;
    }

    /**
     * Проверяет - является ли множество other не строгим подмножеством данного множества бит. Все нулевые биты
     * идущие за самым старшим единичным битом у обоих операндов - не участвуют в проверке выполняемой этим методом.
     * @param other объект Bits для которого проверяется - является ли он не строгим подмножеством текущего объекта.
     * @return true, если other является не строгим подмножеством текущего объекта, иначе возвращает false.
     */
    public boolean contains(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; i++) {
            if((words[i] & other.words[i]) != other.words[i]) return false;
        }

        for(int i = words.length; i < other.words.length; i++) {
            if(other.words[i] != 0L) return false;
        }

        return true;
    }

    /**
     * Проверяют - пересекаются ли два множества бит представленных объектами other и текущим объектом Bits.
     * @param other объект, с которым проверяется наличие пересечения.
     * @return true, если оба объекта имеют биты установленные в единицу на совпадающих позициях, иначе - false.
     */
    public boolean intersect(Bits other) {
        int commonWords = Math.min(words.length, other.words.length);
        for(int i = 0; i < commonWords; i++) {
            if((words[i] & other.words[i]) != 0L) return true;
        }
        return false;
    }

    /**
     * Два объекта Bits считаются одинаковыми если их размеры (значения возвращаемые методом {@link #size()})
     * равны и значения всех бит попарно равны.
     * @param o объект типа Bits с которым производится сравнение.
     * @return true - если объекты равны, false - в противном случае.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bits bits = (Bits) o;

        boolean isEqual = size == bits.size;
        for(int i = 0; i < words.length && isEqual; i++) {
            isEqual = words[i] == bits.words[i];
        }

        return isEqual;
    }

    /**
     * Данный метод сравнивает два объекта Bits, как целые беззнаковые числа без учета их размеров
     * (значения возвращаемые методом {@link #size()}).
     * @param o объект типа Bits с которым производится сравнение.
     * @return true - если логические значения объектов Bits равны, false - в противном случае.
     */
    public boolean equalsIgnoreSize(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bits bits = (Bits) o;

        boolean isEqual = true;
        int commonWords = Math.min(words.length, bits.words.length);
        for(int i = 0; i < commonWords && isEqual; i++) {
            isEqual = words[i] == bits.words[i];
        }

        long[] biggest = words.length > bits.words.length ? words : bits.words;
        for(int i = commonWords; i < biggest.length && isEqual; i++) {
            isEqual = biggest[i] == 0;
        }

        return isEqual;
    }

    /**
     * Выполняет упорядочивающее сравнение двух объектов Bits. Сперва сравниваются размеры обоих объектов Bits,
     * и если их размеры равны, тогда два объекта сравниваются как два беззнаковых целых числа.
     * @param o объект Bits с которым производится сравнение.
     * @return отрицательное число, ноль или положительное число, если объект у которого вызывается
     *         данный метод меньше, равен или больше указанного соответственно.
     */
    @Override
    public int compareTo(Bits o) {
        int result = size - o.size;
        for(int i = words.length - 1; i >= 0 && result == 0; --i) {
            result = Long.compareUnsigned(words[i], o.words[i]);
        }
        return result;
    }

    /**
     * Выполняет упорядочивающее сравнение двух объектов Bits. Два объекта Bits сравниваются как целые
     * беззнаковые числа, без учета их размеров.
     * @param o объект Bits с которым производится сравнение.
     * @return отрицательное число, ноль или положительное число, если объект у которого вызывается
     *         данный метод меньше, равен или больше указанного соответственно.
     */
    public int compareIgnoreSize(Bits o) {
        int result = 0;

        for(int i = words.length - 1; i >= o.words.length && result == 0; --i) {
            result = Long.compareUnsigned(words[i], 0L);
        }

        for(int i = o.words.length - 1; i >= words.length && result == 0; --i) {
            result = Long.compareUnsigned(0L, o.words[i]);
        }

        int commonWords = Math.min(words.length, o.words.length);
        for(int i = commonWords - 1; i >= 0 && result == 0; --i) {
            result = Long.compareUnsigned(words[i], o.words[i]);
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

    @Override
    public String toString() {
        StringBuilder array = new StringBuilder();
        for(int i = 0; i < words.length; i++) array.append(Long.toBinaryString(words[i])).append(',');
        return "Bits {size=" + size + ", count words=" + words.length + ", words=" + array + '}';
    }

    /**
     * Возвращает двоичное представление данного объекта Bits.
     */
    public String toBinaryString() {
        StringBuilder result = new StringBuilder();

        if(size > 0) {
            long lastWord = words[words.length - 1];
            for(int i = (size & 63) - 1; i >= 0; --i) {
                result.append((lastWord >> i) & 1L);
            }

            for(int i = words.length - 2; i >= 0; --i) {
                long word = words[i];
                for(int j = 63; j >= 0; --j) result.append((word >> j) & 1L);
            }
        }

        return result.toString();
    }


    private void assertInHalfOpenInterval(int index) {
        if(index < 0 || index >= size)
            throw new IndexOutOfBoundsException("expected: index >= 0 && index < size; actual: index=" + size);
    }

}
