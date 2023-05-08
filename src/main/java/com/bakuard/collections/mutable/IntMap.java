package com.bakuard.collections.mutable;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Ассоциативный массив на основне хеш-таблицы с разрешением коллизий методом цепочек. В качестве ключей
 * выступают целые числа типа int.
 * @param <T> тип значений.
 */
public final class IntMap<T> implements Iterable<IntMap.Node<T>> {

    private static final float LOAD_FACTOR = 0.75F;
    private static final int MAX_CAPACITY = 1073741824;

    private int size;
    private Node<T>[] table;
    private int actualModCount;

    /**
     *  Создает пустой ассоциативный массив.
     */
    public IntMap() {
        table = new Node[16];
    }

    /**
     * Создает копию указанного ассоциативного массива. Выполняет поверхностное копирование.
     * @param other ассоциативный массив для которого создается копия.
     */
    public IntMap(IntMap<T> other) {
        size = other.size;

        table = new Node[other.table.length];
        for(int i = 0; i < other.table.length; i++) {
            Node<T> currentNode = other.table[i];
            while(currentNode != null) {
                Node<T> head = table[i];
                table[i] = new Node<>(currentNode);
                table[i].next = head;

                currentNode = currentNode.next;
            }
        }
    }

    /**
     * Добавляет или перезаписывает элемент по указанному ключу и возвращает значение, которое хранилось
     * по указаному ключу перед вызовом этого метода или null, если по даному ключу не хранилось
     * ни одного значения.
     * @param key ключ.
     * @param value добавляемое или перезаписываемое значение.
     * @return  значение, которое хранилось по указаному ключу перед вызовом этого метода или null,
     *          если по даному ключу не хранилось ни одного значения.
     */
    public T put(int key, T value) {
        if((float)size/table.length >= LOAD_FACTOR && table.length < MAX_CAPACITY) resize();

        T oldValue = null;
        Node<T> duplicate = getNode(key);
        if(duplicate != null) {
            oldValue = duplicate.value;
            duplicate.value = value;
        } else {
            int bucketIndex = keyToBucketIndex(key, table.length);
            Node<T> newNode = new Node<>(key, value, table[bucketIndex]);
            table[bucketIndex] = newNode;
            ++size;
        }

        ++actualModCount;

        return oldValue;
    }

    /**
     * Возвращает значение хранящееся по указаному ключу или null, если по указаному ключу не хранится
     * на одного значения.
     * @param key ключ.
     * @return значение хранящееся по указаному ключу или null, если по указаному ключу не хранится
     *         на одного значения.
     */
    public T get(int key) {
        Node<T> result = getNode(key);
        return result != null ? result.value : null;
    }

    /**
     * Удаляет значение хранящееся по указаному ключу и возвращает его. Если по указаному ключу не хранится
     * ни одного значение - возвращает null.
     * @param key ключ.
     * @return удаляемое значение или null, если по указаном ключу ничего не хранилось.
     */
    public T remove(int key) {
        int bucketIndex = keyToBucketIndex(key, table.length);

        Node<T> currentNode = table[bucketIndex];
        Node<T> previousNode = currentNode;
        while(currentNode != null && currentNode.KEY != key) {
            previousNode = currentNode;
            currentNode = currentNode.next;
        }

        if(currentNode != null) {
            if(previousNode == table[bucketIndex]) {
                table[bucketIndex] = currentNode.next;
            } else {
                previousNode.next = currentNode.next;
            }
            --size;
            ++actualModCount;
            return currentNode.value;
        }
        return null;
    }

    /**
     * Очищает ассоциативный массив. После вызово данного метода размер ассоциативного массива будет равен 0.
     */
    public void clear() {
        size = 0;
        for(int i = 0; i < table.length; ++i) table[i] = null;
        ++actualModCount;
    }

    /**
     * Возвращает кол-во пар ключ-значение хранящихся в ассоциативном массиве на момент вызова данного метода.
     * @return кол-во пар ключ-значение хранящихся в ассоциативном массиве на момент вызова данного метода.
     */
    public int getSize() {
        return size;
    }

    /**
     * Возвращает true - если ассоциативный массив пуст, иначе - false.
     * @return true - если ассоциативный массив пуст, иначе - false.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Возвращает true, если ассоциативный массив хранит значение по указаному ключу, иначе - false.
     * @param key ключ.
     * @return true, если ассоциативный массив хранит значение по указаному ключу, иначе - false.
     */
    public boolean containsKey(int key) {
        return getNode(key) != null;
    }

    /**
     * Заполняет переданный массив всеми значениями хранящимеся в ассоциативном массиве. Каждое значение
     * ассоциативного массива будет записано в передаваемый массив столько раз, сколько оно встречается в
     * ассоциативном массиве. Переданный массив будет заполняться с элемента под индексом 0. Если длина
     * переданного массива больше размера ассоциативного массива, то все элементы переданного массива,
     * начиная с индекса равного размеру ассоциативного массива, сохранят теже значения, что и до вызова этого метода.
     * @param array заполняемый массив значениями из ассоциативного массива.
     * @throws IllegalArgumentException если длина переданного массива меньше размера ассоциативного массива.
     */
    public void fillArrayWithValues(T[] array) throws IllegalArgumentException {
        if(array.length < size) {
            throw new IllegalArgumentException(
                    "Размер передаваемого массива не может быть меньше значения возвращаемого " +
                            "getSize(). Значение getSize() = " + size + ", array.length = " + array.length);
        }

        int indexArray = 0;
        int indexTable = 0;
        while(indexArray < size) {
            Node<T> currentNode = table[indexTable++];
            while(currentNode != null) {
                array[indexArray++] = currentNode.value;
                currentNode = currentNode.next;
            }
        }
    }

    /**
     * Создает и возвращает объект {@link Array} содержащий все значения находящиеся в данном объекте
     * IntMap.
     * @return объект {@link Array} содержащий все значения находящиеся в данном объекте IntMap.
     */
    public Array<T> getValues() {
        Array<T> array = new Array<>(size);

        for(int i = 0, arrayIndex = 0; i < table.length; i++) {
            Node<T> currentNode = table[i];
            while(currentNode != null) {
                array.replace(arrayIndex++, currentNode.value);
                currentNode = currentNode.next;
            }
        }

        return array;
    }

    /**
     * Возвращает итератор для одностороннего перебора всех пар ключ-значение хранящихся в данном
     * ассоциативном массиве. Порядок перебора может отличаться для каждого итераторв полученного через
     * данный метод.
     * @return итератор для одностороннего перебора всех пар ключ-значение хранящихся в данном ассоциативном массиве.
     */
    @Override
    public Iterator<Node<T>> iterator() {
        return new Iterator<>() {

            private int currentIndex;
            private Node<T> currentNode;
            private final int EXPECTED_MOD_COUNT;

            {
                EXPECTED_MOD_COUNT = actualModCount;

                while(currentNode == null && currentIndex < table.length) {
                    currentNode = table[currentIndex++];
                }
            }

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public Node<T> next() {
                if(EXPECTED_MOD_COUNT != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя вызвать метод next() после модификации объекта IntMap, с которым связан данный итератор."
                    );
                } else if(currentNode == null) {
                    throw new NoSuchElementException();
                } else {
                    Node<T> result = currentNode;
                    currentNode = currentNode.next;
                    while(currentNode == null && currentIndex < table.length) {
                        currentNode = table[currentIndex++];
                    }
                    return result;
                }
            }
        };
    }

    /**
     * Выполняет переданную операцию реализованную объектом типа Consumer для каждой пары ключ-значение
     * хранящейся в ассоциативном массиве. Порядок перебора пар ключ-значение может отличаться для каждого
     * вызова данного метода.
     * @param action действие выполняемое для каждой пары ключ-значение.
     */
    @Override
    public void forEach(Consumer<? super Node<T>> action) {
        final int EXPECTED_COUNT_MOD = actualModCount;

        for(int i = 0; i < table.length; i++) {
            Node<T> currentNode = table[i];
            while(currentNode != null) {
                action.accept(currentNode);
                if(EXPECTED_COUNT_MOD != actualModCount) {
                    throw new ConcurrentModificationException(
                            "Нельзя модифицировать объект IntMap во время работы метода forEach()."
                    );
                }
                currentNode = currentNode.next;
            }
        }
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = result * 31 + size;
        for(int i = 0; i < table.length; i++) {
            Node<T> currentNode = table[i];
            while(currentNode != null) {
                result += currentNode.hashCode();
                currentNode = currentNode.next;
            }
        }
        return result;
    }

    /**
     * Два объекта IntMap считаются равными, если их размеры одинаковы и они содержат одни и теже
     * пары ключ-значение, при этом внутренний порядок следования этих пар не имеет значения.
     * @param o объект типа IntMap с которым производится сравнение.
     * @return true - если объекты равны, false - в противном случае.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntMap<T> that = (IntMap<T>) o;

        if(size != that.size) return false;

        boolean result = true;
        for(int i = 0; i < table.length && result; i++) {
            Node<T> currentNode = table[i];
            while(currentNode != null && result) {
                result = currentNode.equals(that.getNode(currentNode.KEY));
                currentNode = currentNode.next;
            }
        }

        return result;
    }


    private Node<T> getNode(int key) {
        Node<T> currentNode = table[keyToBucketIndex(key, table.length)];
        while(currentNode != null && currentNode.KEY != key) currentNode = currentNode.next;
        return currentNode;
    }

    private void resize() {
        Node<T>[] newTable = new Node[table.length << 1];

        for(int i = 0; i < table.length; ++i) {
            Node<T> currentNode = null;
            while((currentNode = table[i]) != null) {
                int bucketIndex = keyToBucketIndex(currentNode.KEY, newTable.length);
                table[i] = currentNode.next;
                currentNode.next = newTable[bucketIndex];
                newTable[bucketIndex] = currentNode;
            }
        }

        table = newTable;

        ++actualModCount;
    }

    private int keyToBucketIndex(int key, int lengthTable) {
        return (lengthTable - 1) & hash(key);
    }

    private int hash(int key) {
        return key ^ key >>> 16;
    }


    /**
     * Данный клас пердназначен для хранения пары ключ-значение хранящихся в ассоцитаивном массиве.
     * @param <T> тип значения.
     */
    public static final class Node<T> {
        final int KEY;
        T value;
        Node<T> next;

        Node(int key, T value, Node<T> next) {
            KEY = key;
            this.value = value;
            this.next = next;
        }

        Node(Node<T> other) {
            KEY = other.KEY;
            value = other.value;
        }

        /**
         * Возвращает ключ из пары ключ-значение хранящейся в данном объекте Node.
         * @return ключ из пары ключ-значение хранящейся в данном объекте Node.
         */
        public int getKey() {
            return KEY;
        }

        /**
         * Возвращает значение из пары ключ-значение хранящейся в данном объекте Node.
         * @return значение из пары ключ-значение хранящейся в данном объекте Node.
         */
        public T getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return KEY == node.KEY && Objects.equals(value, node.value);
        }

        @Override
        public int hashCode() {
            return KEY ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return KEY + "=" + value;
        }

    }

}
