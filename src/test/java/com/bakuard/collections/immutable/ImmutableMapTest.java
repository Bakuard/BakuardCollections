package com.bakuard.collections.immutable;

import com.bakuard.collections.immutable.ImmutableArray;
import com.bakuard.collections.immutable.ImmutableMap;
import com.bakuard.collections.immutable.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class ImmutableMapTest {

    @Test
    public void constructor1() {
        ImmutableMap<Integer, String> map = new ImmutableMap<>();

        Assertions.assertEquals(0, map.getSize(),
                "Размер ImmutableMap должен быть равен нулю после его создания через конструктор " +
                        "без аргументов.");
    }

    @Test
    public void constructor2() {
        Map<Integer, String> constructorArgument = Map.of(1, "a", 2, "b", 3, "c");
        ImmutableMap<Integer, String> map = new ImmutableMap<>(constructorArgument);
        ImmutableMap<Integer, String> emptyMap = new ImmutableMap<>(new HashMap<>());

        Assertions.assertThrows(NullPointerException.class, () -> new ImmutableMap<>(null),
                "При предаче конструктору ImmutableMap(Map<K, V> map) значения null, должно гернерироваться " +
                        "исключение.");

        Assertions.assertEquals(3, map.getSize(),
                "Не верно расчитывается размер ImmutableMap после его создания через конструктор " +
                        "ImmutableMap(Map<K, V> map).");
        Assertions.assertEquals(0, emptyMap.getSize(),
                "Не верно расчитывается размер ImmutableMap после его создания через конструктор " +
                        "ImmutableMap(Map<K, V> map), в случае передачи пустого Map.");

        Assertions.assertEquals("a", map.get(1),
                "Для каждой пары ключ-значение добавленных в Map, метод get(K key) по ключу из этой пары " +
                        "должен возвращать значение из этой пары.");
        Assertions.assertEquals("b", map.get(2),
                "Для каждой пары ключ-значение добавленных в Map, метод get(K key) по ключу из этой пары " +
                        "должен возвращать значение из этой пары.");
        Assertions.assertEquals("c", map.get(3),
                "Для каждой пары ключ-значение добавленных в Map, метод get(K key) по ключу из этой пары " +
                        "должен возвращать значение из этой пары.");

        Assertions.assertTrue(map.containsKey(1),
                "Для каждого ключа из пары ключ-значение добавленных в Map, метод containsKey(K key) должен " +
                        "возвращать true.");
        Assertions.assertTrue(map.containsKey(2),
                "Для каждого ключа из пары ключ-значение добавленных в Map, метод containsKey(K key) должен " +
                        "возвращать true.");
        Assertions.assertTrue(map.containsKey(3),
                "Для каждого ключа из пары ключ-значение добавленных в Map, метод containsKey(K key) должен " +
                        "возвращать true.");
    }

    @Test
    public void put() {
        ImmutableMap<Integer, String> originMap = new ImmutableMap<>(Map.of(1,"a",2,"b",3,"c"));
        ImmutableMap<Integer, String> expectedOriginMap = new ImmutableMap<>(Map.of(1,"a",2,"b",3,"c"));

        ImmutableMap<Integer, String> actualMap = originMap.put(4, "d").put(5, "e");

        Assertions.assertEquals(5, actualMap.getSize(),
                "Не верно вычисляется размер ImmutableMap после использования метода put(K key, V value).\n" +
                        actualMap);

        Assertions.assertTrue(actualMap.containsKey(4),
                "Метод containsKey(K key) должен возвращать true для ключей добавленных с помощью " +
                        "put(K key, V value).\n" + actualMap);
        Assertions.assertTrue(actualMap.containsKey(5),
                "Метод containsKey(K key) должен возвращать true для ключей добавленных с помощью " +
                        "put(K key, V value).\n" + actualMap);

        Assertions.assertNotSame(originMap, actualMap,
                "Если добавление пары ключ-значение с помощью put(K key, V value) прошло успешно, то этот " +
                        "метод не должен возвращать ссылку на один и тот же объект.\n" + actualMap);
        Assertions.assertEquals(expectedOriginMap, originMap,
                "Методы нацеленные на изменение ImmutableMap не должны изменять объект у которого они " +
                        "вызываются.\n" + actualMap);

        Assertions.assertEquals("d", actualMap.get(4),
                "После добавления пары ключ-значение с помощью метода put(K key, V value), метод " +
                        "get(K key) должен возвращать значение из этой пары по ключу из этой пары.\n" + actualMap);
        Assertions.assertEquals("e", actualMap.get(5),
                "После добавления пары ключ-значение с помощью метода put(K key, V value), метод " +
                        "get(K key) должен возвращать значение из этой пары по ключу из этой пары.\n" + actualMap);
    }

    @Test
    public void put_duplicateKey() {
        ImmutableMap<Integer, String> originMap = new ImmutableMap<>(Map.of(1,"a",2,"b",3,"c"));
        ImmutableMap<Integer, String> actualMap = originMap.put(2, "bagful");
        Assertions.assertEquals(3, actualMap.getSize(),
                "При попытке добавить через put(K key, V value) пару ключ-значение с ключом, который уже " +
                        "содержится в ImmutableMap, размер ImmutableMap не должен изменяться.\n" + actualMap);
        Assertions.assertEquals("bagful", actualMap.get(2),
                "При попытке добавить через put(K key, V value) пару ключ-значение с ключом, который уже " +
                        "содержится в ImmutableMap, метод get(K key) должен возвращать новое значение по этому " +
                        "ключу.\n" + actualMap);
        Assertions.assertNotSame(originMap, actualMap,
                "При попытке добавить через put(K key, V value) пару ключ-значение с ключом, который уже " +
                        "содержится в ImmutableMap, метод метод put(K key, V value) должен вернуть новый объект " +
                        "ImmutableMap.\n" + actualMap);

        ImmutableMap<Integer, String> originMap2 = new ImmutableMap<>(Map.of(1,"a",2,"b",3,"c"));
        ImmutableMap<Integer, String> map2 = originMap2.put(1, "a");
        Assertions.assertSame(map2, map2,
                "При попытке добавить в ImmutableMap пару ключ-значение через put(K key, V value), которая " +
                        "уже содержится в этом ImmutableMap, метод put(K key, V value) должен вернуть ссылку на " +
                        "тот же самый объект.");
        Assertions.assertSame(originMap2, map2,
                "При попытке добавить в ImmutableMap пару ключ-значение через put(K key, V value), которая " +
                        "уже содержится в этом ImmutableMap, метод put(K key, V value) должен вернуть ImmutableMap " +
                        "равный исходному.");
    }

    @Test
    public void get() {
        ImmutableMap<Integer, String> actualMap = new ImmutableMap<>();

        actualMap = actualMap.put(1, "cat").put(null, "dog").put(3, null);

        Assertions.assertEquals("cat", actualMap.get(1),
                "После добавления пары ключ-значение с помощью метода put(K key, V value), метод " +
                        "get(K key) должен возвращать значение из этой пары по ключу из этой пары.\n" + actualMap);
        Assertions.assertEquals("dog", actualMap.get(null),
                "После добавления пары ключ-значение с помощью метода put(K key, V value), метод " +
                        "get(K key) должен возвращать значение из этой пары по ключу из этой пары.\n" + actualMap);
        Assertions.assertNull(actualMap.get(3),
                "После добавления пары ключ-значение с помощью метода put(K key, V value), метод get(K key) " +
                        "должен возвращать значение из этой пары по ключу из этой пары.\n" + actualMap);
    }

    @Test
    public void remove_existsKey() {
        ImmutableMap<Integer, String> originMap = new ImmutableMap<>(
                Map.of(1,"a",2,"b",3,"c"));
        ImmutableMap<Integer, String> expectedOriginMap = new ImmutableMap<>(
                Map.of(1,"a",2,"b",3,"c"));

        ImmutableMap<Integer, String> actualMap = originMap.remove(2).remove(3);

        Assertions.assertEquals(1, actualMap.getSize(),
                "Не верно вычисляется размер ImmutableMap после использования метода remove(K key).\n" + actualMap);

        Assertions.assertFalse(actualMap.containsKey(2),
                "Метод containsKey(K key) должен возвращать false для ключей удаленых с помощью " +
                        "remove(K key, V value).\n" + actualMap);
        Assertions.assertFalse(actualMap.containsKey(3),
                "Метод containsKey(K key) должен возвращать false для ключей удаленых с помощью " +
                        "remove(K key, V value).\n" + actualMap);
        Assertions.assertTrue(actualMap.containsKey(1),
                "Метод containsKey(K key) должен возвращать true для ключей, которые не удалялись с помощью " +
                        "remove(K key, V value).\n" + actualMap);

        Assertions.assertNull(actualMap.get(3),
                "Метод get(K key) должен возвращать null для для ключей удаленых из ImmutableMap через " +
                        "remove(K key).");
        Assertions.assertNull(actualMap.get(2),
                "Метод get(K key) должен возвращать null для для ключей удаленых из ImmutableMap через " +
                        "remove(K key).");
        Assertions.assertEquals("a", actualMap.get(1),
                "Метод get(K key) должен возвращать null для для ключей удаленых из ImmutableMap через " +
                        "remove(K key).");

        Assertions.assertNotSame(originMap, actualMap,
                "Если добавление пары ключ-значение с помощью put(K key, V value) прошло успешно, то этот " +
                        "метод не должен возвращать ссылку на один и тот же объект.\n" + actualMap);
        Assertions.assertEquals(expectedOriginMap, originMap,
                "Методы нацеленные на изменение ImmutableMap не должны изменять объект у которого они " +
                        "вызываются.\n" + actualMap);
    }

    @Test
    public void remove_notExistsKeys() {
        ImmutableMap<Integer, String> originMap = new ImmutableMap<>(
                Map.of(1,"a",2,"b",3,"c"));
        ImmutableMap<Integer, String> expectedOriginMap = new ImmutableMap<>(
                Map.of(1,"a",2,"b",3,"c"));

        ImmutableMap<Integer, String> actualMap = originMap.remove(100).remove(200);

        Assertions.assertEquals(3, actualMap.getSize(),
                "При удалении элементов из ImmutableMap с помощью remove(K key) с использлванием ключа, " +
                        "который не содержится в ImmutableMap, размер результатирующего ImmutableMap не должен " +
                        "измениться.");
        Assertions.assertEquals(expectedOriginMap, originMap,
                "При удалении элементов из ImmutableMap с помощью remove(K key) с использлванием ключа, " +
                        "который не содержится в ImmutableMap, метод remove(K key) должен вернуть ImmutableMap " +
                        "равный исходному.");
        Assertions.assertSame(originMap, actualMap,
                "При удалении элементов из ImmutableMap с помощью remove(K key) с использованием ключа, " +
                        "который не содержится в ImmutableMap, метод remove(K key) должен вернуть сылку на тот же " +
                        "ImmutableMap.");
    }

    @Test
    public void isEmpty() {
        ImmutableMap<Integer, String> emptyMap = new ImmutableMap<>();
        ImmutableMap<Integer, String> map = new ImmutableMap<>(Map.of(1,"1",2,"2"));

        Assertions.assertTrue(emptyMap.isEmpty());
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertFalse(emptyMap.put(3, "3").isEmpty());
        Assertions.assertTrue(map.remove(1).remove(2).isEmpty());
    }

    @Test
    public void getKeys() {
        ImmutableMap<Integer, String> emptyMap = new ImmutableMap<>();
        ImmutableMap<Integer, String> initMap = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3")
        );
        ImmutableMap<Integer, String> mapWithAddedItems = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3")
        ).put(4,"4").put(null, "5");
        ImmutableMap<Integer, String> mapWithRemovedItems = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3")
        ).remove(3).remove(2);
        ImmutableMap<Integer, String> mapWithUpdatedItems = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3")
        ).put(1,"one").put(2,"two");

        Assertions.assertTrue(emptyMap.getKeys().isEmpty(),
                "Пустой объект ImmutableMap должен возвращать пустое множество ключей.");
        Assertions.assertEquals(new ImmutableSet<>(1,2,3), initMap.getKeys());
        Assertions.assertEquals(new ImmutableSet<>(1,2,3,4,null), mapWithAddedItems.getKeys());
        Assertions.assertEquals(new ImmutableSet<>(1), mapWithRemovedItems.getKeys());
        Assertions.assertEquals(new ImmutableSet<>(1,2,3), mapWithUpdatedItems.getKeys());
    }

    @Test
    public void fillArrayWithValues() {
        ImmutableMap<Integer, String> emptyMap = new ImmutableMap<>();
        Assertions.assertDoesNotThrow(() -> emptyMap.fillArrayWithValues(new String[0]),
                "Если ImmutableMap пуст и передавамеый массив имеет нулевую длину, то не должно генерироваться " +
                        "исключений.");

        ImmutableMap<Integer, String> map = new ImmutableMap<>(Map.of(1,"1",2,"2",3,"3"));
        String[] lengthArray = {null, null, null, "4", "5", "6"};
        map.fillArrayWithValues(lengthArray);
        Assertions.assertThrows(IllegalArgumentException.class, () -> map.fillArrayWithValues(new String[0]),
                "Если длина передаваемого массива меньше размера ImmutableMap, должно генерироваться " +
                        "исключение.");
        Assertions.assertEquals(Set.of("1", "2", "3"), Set.of(Arrays.copyOfRange(lengthArray, 0,3)),
                "Не верно заполняется массив, если его длина больше размера ImmutableMap.");
        Assertions.assertArrayEquals(new String[]{"4", "5", "6"}, Arrays.copyOfRange(lengthArray, 3,6),
                "В случае, когда длина передаваемого массива больше размера ImmutableMap, метод " +
                        "fillArrayWithValues(V[] array) не может перезаписывать элементы, индекс которых " +
                        "больше или равен размеру ImmutableMap.");
        String[] array = {"4", null, "1"};
        map.fillArrayWithValues(array);
        Assertions.assertEquals(Set.of("1", "2", "3"), Set.of(array),
                "Метод fillArrayWithValues(V[] array) не верно заполняет массив, когда длина передаваемого " +
                        "массива равна кол-ву элементов ImmutableMap.");

        ImmutableMap<Integer, String> rewriteMap = new ImmutableMap<>(Map.of(1,"1",2,"2",3,"3")).
                put(1, "one").put(5, "5").remove(2).remove(3);
        String[] arrayForRewriteMap = new String[2];
        rewriteMap.fillArrayWithValues(arrayForRewriteMap);
        Assertions.assertEquals(Set.of("one", "5"), Set.of(arrayForRewriteMap),
                "Не верно работает метод fillArrayWithValues(V[] array) для ImmutableMap, полученного " +
                        "в результате добавления, удаления и перезаписи существующих элементов.");
    }

    @Test
    public void getValues() {
        ImmutableMap<Integer, Integer> emptyMap = new ImmutableMap<>();
        ImmutableArray<Integer> values = emptyMap.getValues(Integer.class);
        Assertions.assertEquals(0, values.getLength(),
                "Метод getValues() для пустого объекта ImmutableMap должен возвращать " +
                        "пустой объект ImmutableArray.");

        ImmutableMap<Integer, Integer> map = new ImmutableMap<>(Map.of(1, 10));
        ImmutableArray<Integer> values1 = map.getValues(Integer.class);
        Assertions.assertEquals(map.getSize(), values1.getLength(),
                "Объект ImmutableArray возвращаемый методом getValues() должен содержать кол-во элементов " +
                        "равное кол-ву элементов объекта ImmutableMap, у которого вызывался метод. Данное правило " +
                        "должно соблюдаться и для случая с одним элементом.");
        Assertions.assertEquals(map.get(1), values1.get(0),
                "Возвращаемый объект ImmutableArray должен содержать все значения содержащиеся " +
                        "в объектк ImmutableMap у кторого вызывался метод getValues(). Данное правило " +
                        "должно соблюдаться и для случая с одним элементом.");

        ImmutableMap<Integer, Integer> map2 = new ImmutableMap<>(Map.of(1,10,2,20,3,30, 4, 40));
        ImmutableArray<Integer> values2 = map2.getValues(Integer.class);
        Assertions.assertEquals(map2.getSize(), values2.getLength(),
                "Объект ImmutableArray возвращаемый методом getValues() должен содержать кол-во элементов " +
                        "равное кол-ву элементов объекта ImmutableMap, у которого вызывался метод.");
        map2.forEach((ImmutableMap.Node<Integer, Integer> node) -> {
            Assertions.assertTrue(values2.contains(node.getValue()),
                    "Возвращаемый объект ImmutableArray должен содержать все значения содержащиеся " +
                            "в объектк ImmutableMap у кторого вызывался метод getValues().");
        });
    }

    @Test
    public void forEach() {
        ImmutableMap<Integer, String> emptyMap = new ImmutableMap<>();
        emptyMap.forEach((ImmutableMap.Node<Integer, String> node) -> Assertions.fail(
                "Если ImmutableMap пуст, метод forEach(Consumer<? super Node<K, V>> action) не должен сделать " +
                        "ни одной итерации."));

        ImmutableMap<Integer, String> map = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3",4,"4"));
        final int[] countIterations = new int[1];

        map.forEach((node) -> ++countIterations[0]);
        Assertions.assertEquals(4, countIterations[0],
                "Кол-во итераций метода forEach(Consumer<? super Node<K, V>> action) должно равняться " +
                        "кол-ву элементов ImmutableMap.");

        HashSet<String> actualItems = new HashSet<>();
        map.forEach((ImmutableMap.Node<Integer, String> node) -> actualItems.add(node.getValue()));
        Assertions.assertEquals(Set.of("1","2","3","4"), actualItems,
                "Метод forEach(Consumer<? super Node<K, V>> action) должен перебрать все элементы множества.");
    }

    @Test
    public void iterator() {
        ImmutableMap<Integer, String> emptyMap = new ImmutableMap<>();
        Assertions.assertFalse(emptyMap.iterator().hasNext(),
                "Для пустого ImmutableMap метод hasNext() итератора сразу должен возвращать false.");
        int countIterations = 0;
        for(ImmutableMap.Node<Integer, String> node : emptyMap) ++countIterations;
        Assertions.assertEquals(0, countIterations,
                "Кол-во итераций итератора пустого ImmutableMap должно равняться нулю.");

        ImmutableMap<Integer, String> map = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3",4,"4"));
        countIterations = 0;
        for(ImmutableMap.Node<Integer, String> node : map) ++countIterations;
        Assertions.assertEquals(4, countIterations,
                "Кол-во итераций итератора должно равняться кол-ву элементов множества.");
        countIterations = 0;
        for(ImmutableMap.Node<Integer, String> node : emptyMap) ++countIterations;
        Assertions.assertEquals(0, countIterations,
                "Кол-во итераций итератора пустого ImmutableMap должно равняться нулю.");

        HashSet<String> actualValues = new HashSet<>();
        for(ImmutableMap.Node<Integer, String> node : map) actualValues.add(node.getValue());
        Assertions.assertEquals(Set.of("1","2","3","4"), actualValues,
                "Итератор должен перебрать все элементы множества.");
    }

    @Test
    public void equals_properties() {
        ImmutableMap<Integer, String> map1 = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3",4,"4"));
        ImmutableMap<Integer, String> map2 = new ImmutableMap<>(
                Map.of(2,"2",3,"3",4,"4", 1, "1"));
        ImmutableMap<Integer, String> map3 = new ImmutableMap<>(
                Map.of(1,"1",2,"2",4,"4", 3, "3"));

        Assertions.assertEquals(map1, map2,
                "Метод equals() должен возвращать true для ImmutableMap имеющих одинаковый набор элементов, в " +
                        "независимости от порядка их хранения.");

        Assertions.assertEquals(map1, map1,
                "Не соблюдается свойство рефлексивности для метода equals()");
        Assertions.assertEquals(map2, map2,
                "Не соблюдается свойство рефлексивности для метода equals()");

        Assertions.assertEquals(map1.equals(map2), map2.equals(map1),
                "Не соблюдается свойство симметричност для метода equals(), в случае, когда множества равны.");

        map2 = map2.put(100, "100");
        Assertions.assertNotEquals(map1, map2,
                "Метод equals() должен возвращать false для множеств имеющих разный размер.");
        map1 = map1.put(100, "100 cats");
        Assertions.assertNotEquals(map1, map2,
                "Метод equals() должен возвращать false для множеств имеющих разный набор элементов, в " +
                        "независимости от порядка их хранения.");
        Assertions.assertEquals(map1.equals(map2), map2.equals(map1),
                "Не соблюдается свойство симметричност для метода equals(), в случае, когда множества " +
                        "не равны.");

        map1 = map1.remove(100);
        map2 = map2.remove(100);
        Assertions.assertTrue(map1.equals(map2) && map2.equals(map3) && map1.equals(map3),
                "Не соблюдается своство транзитивности для метода equals().");
    }

    @Test
    public void hashCode_properties() {
        ImmutableMap<Integer, String> map1 = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3",4,"4"));
        ImmutableMap<Integer, String> map2 = new ImmutableMap<>(
                Map.of(1,"1",2,"2",3,"3",4,"4"));

        Assertions.assertEquals(map1.hashCode(), map2.hashCode(),
                "Если объекты равны по equals(), то их хеш-коды тоже должны быть равны.");

        map2 = map2.put(100, "100");
        if(map1.hashCode() != map1.hashCode()) {
            Assertions.assertNotEquals(map1, map2,
                    "Если хеш коды объектов не равны, то объекты гарантированно отличаются по equals().");
        }

        int hashCode = map1.hashCode();
        for(int i = 0; i < 100000; ++i) {
            Assertions.assertEquals(hashCode, map1.hashCode(),
                    "Для одного и того же состояния объекта hashCode() должен возвращать одно и тоже значение.");
        }
    }

}