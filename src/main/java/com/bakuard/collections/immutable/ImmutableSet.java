package com.bakuard.collections.immutable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Реализация неупорядоченного множества.
 * Неизменяемость в данном случае означает, что методы модифицирующие неупорядоченное множество возвращают
 * новый объект содержащий все данные исходного динамического массива с учетом внесенных ихменений, а исходный
 * ассоциативный массив остается неизменным.
 * @param <T> тип значений хранимых в данном множестве.
 */
public final class ImmutableSet<T> implements Iterable<T> {

    private static final float LOAD_FACTOR = 0.75F;
    private static final int MAX_CAPACITY = 1073741824;

    private final int SIZE;
    private final Node<T>[] TABLE;

    /**
     * Создает пустое неизменяемое множество.
     */
    @SuppressWarnings("unchecked")
    public ImmutableSet() {
        SIZE = 0;
        TABLE = new Node[16];
    }

    /**
     * Создает неизменяемое множество содержащее только уникальные (по equals()) элементы из переданного массива.
     * @param values массив элементов добавляемых в множество.
     */
    @SuppressWarnings("unchecked")
    public ImmutableSet(T... values) {
        int newSize = 0;
        Node<T>[] newTable = new Node[16];

        for(T value : values) {
            if(getNode(value, newTable) == null) {
                ++newSize;

                int newCapacity = calculateCapacity(newSize);
                if(newCapacity > newTable.length) newTable = resizeTable(newTable, newCapacity);

                int bucketIndex = valueToBucketIndex(value, newCapacity);
                newTable[bucketIndex] = new Node<>(value, newTable[bucketIndex]);
            }
        }

        SIZE = newSize;
        TABLE = newTable;
    }

    /**
     * Создает неизменяемое множество содержащее только уникальные (по equals()) элементы из переданного списка.
     * @param values список элементов добавляемых в множество.
     */
    @SuppressWarnings("unchecked")
    public ImmutableSet(List<T> values) {
        int newSize = 0;
        Node<T>[] newTable = new Node[16];

        for(T value : values) {
            if(getNode(value, newTable) == null) {
                ++newSize;

                int newCapacity = calculateCapacity(newSize);
                if(newCapacity > newTable.length) newTable = resizeTable(newTable, newCapacity);

                int bucketIndex = valueToBucketIndex(value, newCapacity);
                newTable[bucketIndex] = new Node<>(value, newTable[bucketIndex]);
            }
        }

        SIZE = newSize;
        TABLE = newTable;
    }

    /**
     * Создает неизменяемое множество содержащее все элементы переданного множества типа java.util.Set.
     * @param values множество типа java.util.Set, элементы которого добавляются в создаваемое неизменяемое множество.
     */
    @SuppressWarnings("unchecked")
    public ImmutableSet(Set<T> values) {
        SIZE = values.size();
        TABLE = new Node[calculateCapacity(SIZE)];

        values.forEach((T value) -> {
            int bucketIndex = valueToBucketIndex(value, TABLE.length);
            TABLE[bucketIndex] = new Node<>(value, TABLE[bucketIndex]);
        });
    }

    private ImmutableSet(Node<T>[] table, int size) {
        TABLE = table;
        SIZE = size;
    }

    /**
     * Создает и возвращает копию данного объекта с небольшим отличием - добавленным новым элементом. Если
     * добавляемый элемент уже содержится в данном множестве, то метод просто вернет ссылку на этот же
     * объект множества.
     * @param value добавляемый элемент.
     * @return новый объект ImmutableSet или этот же объект.
     */
    public ImmutableSet<T> add(T value) {
        ImmutableSet<T> newSet = this;
        if(getNode(value, TABLE) == null) {
            newSet = new ImmutableSet<>(copyTable(calculateCapacity(SIZE + 1)), SIZE + 1);
            int bucketIndex = valueToBucketIndex(value, newSet.TABLE.length);
            newSet.TABLE[bucketIndex] = new Node<>(value, newSet.TABLE[bucketIndex]);
        }
        return newSet;
    }

    /**
     * Создает и возвращает копию данного объекта с небольшим отличием - одним удаленным элементом. Если
     * удаляемый элемент не содержится в данном множестве, то метод просто вернет ссылку на этот же
     * объект множества.
     * @param value удаляемый элемент.
     * @return новый объект ImmutableSet или этот же объект.
     */
    public ImmutableSet<T> remove(T value) {
        ImmutableSet<T> newSet = this;
        if(getNode(value, TABLE) != null) {
            Node<T>[] newTable = new Node[calculateCapacity(SIZE - 1)];
            forEach((T v) -> {
                if(!Objects.equals(v, value)) {
                    int bucketIndex = valueToBucketIndex(v, newTable.length);
                    newTable[bucketIndex] = new Node<>(v, newTable[bucketIndex]);
                }
            });
            newSet = new ImmutableSet<>(newTable, SIZE - 1);
        }
        return newSet;
    }

    /**
     * Проверяет - содержится ли указанный элемент в данном множестве.
     * @param value значение искомого элемента.
     * @return true, если множество содержит элемент с указанным значением, иначе - false.
     */
    public boolean contains(T value) {
        return getNode(value, TABLE) != null;
    }

    /**
     * Проверяет - является ли указанное множество нестрогим подмножеством данного множества.
     * @param other предполагаемое нестрогое подмножество данного множества.
     * @return true, если указанное множество является нестрогим подмножеством данного множества, иначе - false.
     */
    public boolean containsSet(ImmutableSet<T> other) {
        boolean containsItem = true;
        for(int i = 0; i < other.TABLE.length && containsItem; ++i) {
            Node<T> currentNode = other.TABLE[i];
            while(currentNode != null && containsItem) {
                containsItem = contains(currentNode.VALUE);
                currentNode = currentNode.next;
            }
        }
        return containsItem;
    }

    /**
     * Проверяет - является ли указанное множество строгим подмножеством данного множества.
     * @param other предполагаемое строгое подмножество данного множества.
     * @return true, если указанное множество является строгим подмножеством данного множества, иначе - false.
     */
    public boolean strictlyContainsSet(ImmutableSet<T> other) {
        return SIZE > other.SIZE && containsSet(other);
    }

    /**
     * Проверяет - имеют ли общие элементы данное множество и указанное множество.
     * @param other множество с которым проводится проверка на наличие общих элементов.
     * @return true, если указанное множество имеет общие элементы с данным множеством, иначе - false.
     */
    public boolean intersect(ImmutableSet<T> other) {
        boolean isIntersect = false;
        for(int i = 0; i < TABLE.length && !isIntersect; ++i) {
            Node<T> currentNode = TABLE[i];
            while(currentNode != null && !isIntersect) {
                isIntersect = other.contains(currentNode.VALUE);
                currentNode = currentNode.next;
            }
        }
        return isIntersect;
    }

    /**
     * Возвращает кол-во элементов данного множества.
     * @return кол-во элементов данного множества.
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Проверяет - является ли данное множество пустым.
     * @return true, если данное множество является пустым, иначе - false.
     */
    public boolean isEmpty() {
        return SIZE == 0;
    }

    /**
     * Создает и возвращает новый объект множества являющийся результатом пересечения данного множества с указанным.
     * @param other второй операнд операции пересечения. Первым является текущий объект множества.
     * @return новый объект множества являющийся результатом пересечения данного множества с указанным.
     */
    public ImmutableSet<T> and(ImmutableSet<T> other) {
        int newSize = 0;
        Node<T>[] newTable = new Node[16];

        for(int i = 0; i < TABLE.length; ++i) {
            Node<T> currentNode = TABLE[i];
            while(currentNode != null) {
                Node<T> otherNode = other.getNode(currentNode.VALUE, other.TABLE);
                if(otherNode != null) {
                    ++newSize;

                    int newCapacity = calculateCapacity(newSize);
                    if(newCapacity > newTable.length) newTable = resizeTable(newTable, newCapacity);

                    int bucketIndex = valueToBucketIndex(currentNode.VALUE, newCapacity);
                    newTable[bucketIndex] = new Node<>(currentNode.VALUE, newTable[bucketIndex]);
                }

                currentNode = currentNode.next;
            }
        }

        return new ImmutableSet<>(newTable, newSize);
    }

    /**
     * Создает и возвращает новый объект множества являющийся результатом объединения данного множества с указанным.
     * @param other второй операнд операции объединения. Первым является текущий объект множества.
     * @return новый объект множества являющийся результатом объединения данного множества с указанным.
     */
    public ImmutableSet<T> or(ImmutableSet<T> other) {
        int newSize = other.SIZE;
        Node<T>[] newTable = other.copyTable(other.TABLE.length);

        for(int i = 0; i < TABLE.length; ++i) {
            Node<T> currentNode = TABLE[i];
            while(currentNode != null) {
                if(getNode(currentNode.VALUE, newTable) == null) {
                    ++newSize;

                    int newCapacity = calculateCapacity(newSize);
                    if(newCapacity > newTable.length) newTable = resizeTable(newTable, newCapacity);

                    int bucketIndex = valueToBucketIndex(currentNode.VALUE, newCapacity);
                    newTable[bucketIndex] = new Node<>(currentNode.VALUE, newTable[bucketIndex]);
                }

                currentNode = currentNode.next;
            }
        }

        return new ImmutableSet<>(newTable, newSize);
    }

    /**
     * Создает и возвращает новый объект множество являющийся результатом вычитания указанного множества из
     * данного множества.
     * @param other второй операнд операции вычитания. Первым является текущий объект множества.
     * @return новый объект множества являющийся результатом объединения данного множества с указанным.
     */
    public ImmutableSet<T> difference(ImmutableSet<T> other) {
        int newSize = SIZE;
        Node<T>[] newTable = copyTable(TABLE.length);

        for(int i = 0; i < newTable.length; ++i) {
            Node<T> currentNode = newTable[i];
            Node<T> previousNode = currentNode;
            while(currentNode != null) {
                if(other.contains(currentNode.VALUE)) {
                    if(currentNode == newTable[i]) newTable[i] = currentNode.next;
                    else previousNode.next = currentNode.next;

                    --newSize;

                    int newCapacity = calculateCapacity(newSize);
                    if(newCapacity < newTable.length) newTable = resizeTable(newTable, newCapacity);
                }

                previousNode = currentNode;
                currentNode = currentNode.next;
            }
        }

        return new ImmutableSet<>(newTable, newSize);
    }

    /**
     * Предоставляет императивный способ перебора всех элементов данного множества через паттерн Итератор.
     * @return односторонний итератор.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex;
            private Node<T> currentNode;

            {
                while(currentNode == null && currentIndex < TABLE.length) {
                    currentNode = TABLE[currentIndex++];
                }
            }

            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public T next() {
                if(currentNode == null) {
                    throw new NoSuchElementException();
                } else {
                    Node<T> result = currentNode;
                    currentNode = currentNode.next;
                    while(currentNode == null && currentIndex < TABLE.length) {
                        currentNode = TABLE[currentIndex++];
                    }
                    return result.VALUE;
                }
            }
        };
    }

    /**
     * Предоставляет декларотивный способ перебора элементов множества.
     * @param action объект реализующий функцию обратного вызова выполняемую для каждого элемента множества.
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        for(int i = 0; i < TABLE.length; ++i) {
            Node<T> currentNode = TABLE[i];
            while(currentNode != null) {
                action.accept(currentNode.VALUE);
                currentNode = currentNode.next;
            }
        }
    }

    /**
     * Два объекта множества считаются одинаковыми, если их размеры одинаковы и они содержат одни и теже элементы,
     * при этом внутренний порядок следования этих элементов не важен.
     * @param o объект типа ImmutableSet с которым производится сравнение.
     * @return true - если объекты равны, false - в противном случае.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableSet<T> that = (ImmutableSet<T>) o;

        if(SIZE != that.SIZE) return false;

        return containsSet(that);
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = result * 31 + SIZE;
        for(int i = 0; i < TABLE.length; ++i) {
            Node<T> currentNode = TABLE[i];
            while(currentNode != null) {
                result += Objects.hashCode(currentNode.VALUE);
                currentNode = currentNode.next;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder items = new StringBuilder();
        forEach((T value) -> items.append(value).append(','));
        if(items.length() > 0) items.deleteCharAt(items.length() - 1);
        return "ImmutableSet{SIZE=" + SIZE + ", [" + items + "]}";
    }


    private Node<T>[] resizeTable(Node<T>[] table, int newCapacity) {
        Node<T>[] newTable = new Node[newCapacity];

        for(int i = 0; i < table.length; ++i) {
            Node<T> currentNode = null;
            while((currentNode = table[i]) != null) {
                int bucketIndex = valueToBucketIndex(currentNode.VALUE, newCapacity);
                table[i] = currentNode.next;
                currentNode.next = newTable[bucketIndex];
                newTable[bucketIndex] = currentNode;
            }
        }

        return newTable;
    }

    private Node<T>[] copyTable(int newCapacity) {
        Node<T>[] newTable = new Node[newCapacity];

        forEach((T value) -> {
            int bucketIndex = valueToBucketIndex(value, newTable.length);
            newTable[bucketIndex] = new Node<>(value, newTable[bucketIndex]);
        });

        return newTable;
    }

    private Node<T> getNode(T value, Node<T>[] table) {
        Node<T> currentNode = table[valueToBucketIndex(value, table.length)];
        while(currentNode != null && !Objects.equals(currentNode.VALUE, value)) currentNode = currentNode.next;
        return currentNode;
    }

    private int calculateCapacity(int newSize) {
        int newCapacity = (int)(newSize/LOAD_FACTOR);

        if(newCapacity > MAX_CAPACITY) newCapacity = MAX_CAPACITY;
        else if(newCapacity < 16) newCapacity = 16;
        else if((newCapacity & (newCapacity - 1)) != 0) newCapacity = roundUpToPowerOfTwo(newCapacity);

        return newCapacity;
    }

    private int roundUpToPowerOfTwo(int size) {
        size |= size >> 1;
        size |= size >> 2;
        size |= size >> 4;
        size |= size >> 8;
        size |= size >> 16;
        return ++size;
    }

    private int valueToBucketIndex(T value, int tableLength) {
        return (tableLength - 1) & hash(value);
    }

    private int hash(T value) {
        int hash = Objects.hashCode(value);
        return hash ^ hash >>> 16;
    }


    private static final class Node<T> {

        final T VALUE;
        Node<T> next;

        Node(T value, Node<T> next) {
            VALUE = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Node{VALUE=" + VALUE + '}';
        }

    }

}
