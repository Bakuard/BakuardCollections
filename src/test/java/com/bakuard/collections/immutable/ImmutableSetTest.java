package com.bakuard.collections.immutable;

import com.bakuard.collections.immutable.ImmutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class ImmutableSetTest {

    @Test
    public void constructor1() {
        ImmutableSet<Integer> set = new ImmutableSet<>();

        Assertions.assertEquals(0, set.getSize(),
                "Размер неизменяемого множества должен быть равен нулю после его создания через конструктор " +
                        "без аргументов.");
    }

    @Test
    public void constructor2() {
        ImmutableSet<Integer> set = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> setWithDuplicatesArguments = new ImmutableSet<>(1,2,3,4,4,4,4,4,4);
        ImmutableSet<Integer> setEmpty = new ImmutableSet<>(new Integer[]{});

        Assertions.assertEquals(4, set.getSize(),
                "Не верно расчитывается размер неизменяемого множества после его создания через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertEquals(4, setWithDuplicatesArguments.getSize(),
                "Не верно расчитывается размер неизменяемого множества после его создания через конструктор " +
                        "ImmutableSet(T... values), в случае, когда передаются несколько одинаковых аргументов");
        Assertions.assertEquals(0, setEmpty.getSize(),
                "Не верно расчитывается размер неизменяемого множества после его создания через конструктор " +
                        "ImmutableSet(T... values), в случае, когда передаются пустой массив.");

        Assertions.assertTrue(set.contains(1),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertTrue(set.contains(2),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertTrue(set.contains(3),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertTrue(set.contains(4),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");

        Assertions.assertTrue(setWithDuplicatesArguments.contains(1),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertTrue(setWithDuplicatesArguments.contains(2),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertTrue(setWithDuplicatesArguments.contains(3),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");
        Assertions.assertTrue(setWithDuplicatesArguments.contains(4),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(T... values)");

        Assertions.assertThrows(NullPointerException.class, () -> new ImmutableSet<>((Integer[])null));
    }

    @Test
    public void constructor3() {
        ImmutableSet<Integer> set = new ImmutableSet<>(List.of(1,2,3,4));
        ImmutableSet<Integer> setWithDuplicatesArguments = new ImmutableSet<>(List.of(1,2,3,4,4,4,4,3,3,4,3));
        ImmutableSet<Integer> setEmpty = new ImmutableSet<>(new Integer[]{});

        Assertions.assertEquals(4, set.getSize(),
                "Не верно расчитывается размер неизменяемого множества после его создания через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertEquals(4, setWithDuplicatesArguments.getSize(),
                "Не верно расчитывается размер неизменяемого множества после его создания через конструктор " +
                        "ImmutableSet(List<T> values), в случае, когда передаются несколько одинаковых аргументов");
        Assertions.assertEquals(0, setEmpty.getSize(),
                "Не верно расчитывается размер неизменяемого множества после его создания через конструктор " +
                        "ImmutableSet(List<T> values), в случае, когда передаются пустой массив._");

        Assertions.assertTrue(set.contains(1),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertTrue(set.contains(2),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertTrue(set.contains(3),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertTrue(set.contains(4),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");

        Assertions.assertTrue(setWithDuplicatesArguments.contains(1),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertTrue(setWithDuplicatesArguments.contains(2),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertTrue(setWithDuplicatesArguments.contains(3),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");
        Assertions.assertTrue(setWithDuplicatesArguments.contains(4),
                "Метод contains(T value) должен возвращать true для элементов добавленных через конструктор " +
                        "ImmutableSet(List<T> values)");

        Assertions.assertThrows(NullPointerException.class, () -> new ImmutableSet<>((List<Integer>)null));
    }

    @Test
    public void constructor4() {
        ImmutableSet<Integer> set = new ImmutableSet<>(Set.of(1,2,3,4));

        Assertions.assertEquals(4, set.getSize(),
                """
                        Не верно расчитывается размер неизменяемого множества после его создания через 
                        конструктор ImmutableSet(Set<T> values)
                        """);

        Assertions.assertTrue(set.contains(1),
                """
                        Метод contains(T value) должен возвращать true для элементов добавленных через 
                        конструктор ImmutableSet(List<T> values)
                        """);
        Assertions.assertTrue(set.contains(2),
                """
                        Метод contains(T value) должен возвращать true для элементов добавленных через 
                        конструктор ImmutableSet(List<T> values)
                        """);
        Assertions.assertTrue(set.contains(3),
                """
                        Метод contains(T value) должен возвращать true для элементов добавленных через 
                        конструктор ImmutableSet(List<T> values)
                        """);
        Assertions.assertTrue(set.contains(4),
                """
                        Метод contains(T value) должен возвращать true для элементов добавленных через 
                        конструктор ImmutableSet(List<T> values)
                        """);
    }

    @Test
    public void add() {
        Box<String> boxCat = new Box<>("cat", "label1");
        Box<String> boxDog = new Box<>("dog", "label2");
        Box<String> boxCrocodile = new Box<>("crocodile", "label3");
        ImmutableSet<Box<String>> originSet = new ImmutableSet<>();
        ImmutableSet<Box<String>> expectedOriginSet = new ImmutableSet<>();

        ImmutableSet<Box<String>> set = originSet.add(boxCat).add(boxDog).add(boxCrocodile);

        Assertions.assertEquals(3, set.getSize(),
                "Не верно вычисляется размер неизменяемого множества после использования метода " +
                        "add(T value).\n" + set);

        Assertions.assertTrue(set.contains(boxCat),
                "После добавления элемента через метод add(T value) метод contains() должен возвращать true " +
                        "для этого элемента.\n" + set);
        Assertions.assertTrue(set.contains(boxDog),
                "После добавления элемента через метод add(T value) метод contains() должен возвращать true " +
                        "для этого элемента.\n" + set);
        Assertions.assertTrue(set.contains(boxCrocodile),
                "После добавления элемента через метод add(T value) метод contains() должен возвращать true " +
                        "для этого элемента.\n" + set);

        Assertions.assertNotSame(originSet, set,
                "Если добавление элемента через add(T value) прошло успешно, то этот метод не должен " +
                        "возвращать ссылку на этот же объект.\n" + set);
        Assertions.assertEquals(expectedOriginSet, originSet,
                "Методы нацеленные на изменение состава множества не должны изменять объект у которого они " +
                        "вызываются.");

        ImmutableSet<Box<String>> expectedSet = new ImmutableSet<>(boxCat, boxDog, boxCrocodile);
        Box<String> duplicateCat = new Box<>("cat", "duplicate label");
        set = expectedSet.add(duplicateCat);
        Assertions.assertEquals(3, set.getSize(),
                "При передаче значения, которое уже содержится в неизменяемом множестве размер множества не " +
                        "должен изменятсья.\n" + set);
        set.forEach((Box<String> value) -> {
            if(value.equals(duplicateCat)) {
                Assertions.assertNotEquals(duplicateCat.getLabel(), value.getLabel(),
                        "Если добавляемый элемент через add(T value) уже содержится в неизменяемом множестве, " +
                                "то элемент-дубликат не должен его его заменять.");
            }
        });
        Assertions.assertSame(expectedSet, set,
                "Если добавляемый элемент через add(T value) уже содержится в неизменяемом множестве, то " +
                        "метод add(T value) должен вернуть ссылку на этот же объект-множество.\n" + set);
    }

    @Test
    public void addWithNull() {
        ImmutableSet<Integer> originSet = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> expectedOriginSet = new ImmutableSet<>(1,2,3,4);

        ImmutableSet<Integer> set = originSet.add(null);

        Assertions.assertEquals(5, set.getSize(),
                "Не верно вычисляется размер неизменяемого множества после использования метода " +
                        "add(T value) со значением null.\n" + set);
        Assertions.assertTrue(set.contains(null),
                "После добавления null через метод add(T value) метод contains() должен возвращать true " +
                        "для null.\n" + set);
        Assertions.assertNotSame(originSet, set,
                "Если добавление элемента null через add(T value) прошло успешно, то этот метод не должен " +
                        "возвращать ссылку на этот же объект.\n" + set);
        Assertions.assertEquals(expectedOriginSet, originSet,
                "Методы нацеленные на изменение состава множества не должны изменять объект у которого они " +
                        "вызываются.");
    }

    @Test
    public void removeWithNull() {
        ImmutableSet<Integer> originSet = new ImmutableSet<>(1,2,3,null);
        ImmutableSet<Integer> expectedOriginSet = new ImmutableSet<>(1,2,3,null);

        ImmutableSet<Integer> set = originSet.remove(null);

        Assertions.assertEquals(3, set.getSize(),
                "Не верно вычисляется размер неизменяемого множества после использования метода " +
                        "remove(T value) со значением null.\n" + set);
        Assertions.assertFalse(set.contains(null),
                "После удаления null через метод remove(T value) метод contains() должен возвращать false " +
                        "для null.\n" + set);
        Assertions.assertNotSame(originSet, set,
                "Если удаления удаления null через remove(T value) прошло успешно, то этот метод не должен " +
                        "возвращать ссылку на этот же объект.\n" + set);
        Assertions.assertEquals(expectedOriginSet, originSet,
                "Методы нацеленные на изменение состава множества не должны изменять объект у которого они " +
                        "вызываются.");
    }

    @Test
    public void remove() {
        ImmutableSet<Integer> originSet = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> expectedOriginSet = new ImmutableSet<>(1,2,3,4);

        ImmutableSet<Integer> set = originSet.remove(3);

        Assertions.assertEquals(3, set.getSize(),
                "Не верно расчитывается размер неизменяемого множества после вызова метода " +
                        "remove(T value).\n" + set);
        Assertions.assertTrue(set.contains(1),
                "После вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertTrue(set.contains(2),
                "После вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertTrue(set.contains(4),
                "Посл вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertFalse(set.contains(3),
                "Посл вызова метода remove(T value) элемент для которого был вызван этот метод, должен " +
                        "бысть удален из коллекции.\n" + set);
        Assertions.assertNotSame(originSet, set,
                "После успешного вызова метода remove(T value) данный метод должен вернуть ссылку на " +
                        "другой объект неизменяемого множества.\n" + set);
        Assertions.assertEquals(expectedOriginSet, originSet,
                "Методы нацеленные на изменение состава множества не должны изменять объект у которого они " +
                        "вызываются.");

        originSet = new ImmutableSet<>(1,2,3,4);
        set = originSet.remove(5);
        Assertions.assertEquals(4, set.getSize(),
                "Не верно расчитывается размер неизменяемого множества после вызова метода remove(T value) " +
                        "в случае, когда удаляемый элемент отсутсвует в неизменяемом множестве.\n" + set);
        Assertions.assertTrue(set.contains(1),
                "После вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertTrue(set.contains(2),
                "Посл вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertTrue(set.contains(3),
                "Посл вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertTrue(set.contains(4),
                "Посл вызова метода remove(T value) элементы для которых этот метод не вызывался должны " +
                        "остаться в коллекции.\n" + set);
        Assertions.assertSame(originSet, set,
                "После вызова метода remove(T value) в случае, когда удаляемый элемент отсутсвует в " +
                        "неизменяемом множестве, метод должен вернуть ссылку на тотже объект неизменяемого " +
                        "множества.\n" + set);
    }

    @Test
    public void contains() {
        ImmutableSet<Integer> set = new ImmutableSet<>(1000, 2000, 3000, 4000);

        Assertions.assertTrue(set.contains(1000),
                "Метод contains() должен возвращать true дле элементов находящихся в неизменяемом множестве.");
        Assertions.assertTrue(set.contains(2000),
                "Метод contains() должен возвращать true дле элементов находящихся в неизменяемом множестве.");
        Assertions.assertTrue(set.contains(3000),
                "Метод contains() должен возвращать true дле элементов находящихся в неизменяемом множестве.");
        Assertions.assertTrue(set.contains(4000),
                "Метод contains() должен возвращать true дле элементов находящихся в неизменяемом множестве.");
        Assertions.assertFalse(set.contains(0),
                "Метод contains() должен возвращать false дле элементов находящихся в неизменяемом множестве.");
    }

    @Test
    public void isEmpty() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1000, 2000, 3000, 4000);
        ImmutableSet<Integer> set2 = new ImmutableSet<>();

        Assertions.assertFalse(set1.isEmpty(),
                "После создания множества через конструктор ImmutableSet(T... values) isEmpty() должен " +
                        "возвращать false.");
        Assertions.assertTrue(set2.isEmpty(),
                "После создания множества через конструктор ImmutableSet() isEmpty() должен " +
                        "возвращать true.");
        Assertions.assertTrue(set1.remove(1000).remove(2000).remove(3000).remove(4000).isEmpty(),
                "После удаления всех элементов isEmpty() должен возвращать true.");
        Assertions.assertFalse(set2.add(1).isEmpty(),
                "После успешного добавления элемента в пустое множество isEmpty() должен возвращать false.");
    }

    @Test
    public void equals() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5,6,7);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(7,6,5,4,3,2,1);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(set1, set2);

        Assertions.assertEquals(set1.add(10).add(2).add(11), set2.add(2).add(11).add(10));

        Assertions.assertNotEquals(set1, emptySet);

        Assertions.assertEquals(emptySet, emptySet);

        for(int i = 200; i < 5000; ++i) set1 = set1.add(i);
        for(int i = 200; i < 5000; ++i) set1 = set1.remove(i);
        Assertions.assertEquals(set1, set2,
                "После многократного добавления и удаления одних и тех же элементов в множество, " +
                        "конечное множество должно быть равно исходному.");
    }

    @Test
    public void equals_properties() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5,6,7);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(7,6,5,4,3,2,1);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(1,7,2,6,3,5,4);

        Assertions.assertEquals(set1, set2,
                "Метод equals() должен возвращать true для множеств имеющих одинаковый набор элементов, в " +
                        "независимости от порядка их хранения.");

        Assertions.assertEquals(set1, set1,
                "Не соблюдается свойство рефлексивности для метода equals()");
        Assertions.assertEquals(set2, set2,
                "Не соблюдается свойство рефлексивности для метода equals()");

        Assertions.assertEquals(set1.equals(set2), set2.equals(set1),
                "Не соблюдается свойство симметричност для метода equals(), в случае, когда множества равны.");

        set2 = set2.add(100);
        Assertions.assertNotEquals(set1, set2,
                "Метод equals() должен возвращать false для множеств имеющих разный размер.");
        set1 = set1.add(1000);
        Assertions.assertNotEquals(set1, set2,
                "Метод equals() должен возвращать false для множеств имеющих разный набор элементов, в " +
                        "независимости от порядка их хранения.");
        Assertions.assertEquals(set1.equals(set2), set2.equals(set1),
                "Не соблюдается свойство симметричност для метода equals(), в случае, когда множества " +
                        "не равны.");

        set1 = set1.remove(1000);
        set2 = set2.remove(100);
        Assertions.assertTrue(set1.equals(set2) && set2.equals(set3) && set1.equals(set3),
                "Не соблюдается своство транзитивности для метода equals().");
    }

    @Test
    public void hashCode_properties() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(1,2,3,4);

        Assertions.assertEquals(set1.hashCode(), set2.hashCode(),
                "Если объекты равны по equals(), то их хеш-коды тоже должны быть равны.");

        set2 = set2.add(100);
        if(set1.hashCode() != set2.hashCode()) {
            Assertions.assertNotEquals(set1, set2,
                    "Если хеш коды объектов не равны, то объекты гарантированно отличаются по equals().");
        }

        int hashCode = set1.hashCode();
        for(int i = 0; i < 100000; ++i) {
            Assertions.assertEquals(hashCode, set1.hashCode(),
                    "Для одного и того же состояния объекта hashCode() должен возвращать одно и тоже значение.");
        }
    }

    @Test
    public void intersect() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(10,11,12,13);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertTrue(set1.intersect(set2),
                "Для двух множеств имеющих одинаковые элементы, метод intersect() должен возвращать true.");
        Assertions.assertFalse(set1.intersect(set3),
                "Для двух множеств не имеющих одинаковые элементы, метод intersect() должен возвращать false.");
        Assertions.assertFalse(set1.intersect(emptySet),
                "Для пустого и не пустого множества метод intersect() должен возвращать false.");
        Assertions.assertFalse(emptySet.intersect(emptySet),
                "Если оба операнда метода intersect() пустые множества, то метод должен возвращать false.");
    }

    @Test
    public void and() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6,7);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(new ImmutableSet<>(3,4,5), set1.and(set2));

        Assertions.assertEquals(emptySet, set1.and(set3));

        Assertions.assertEquals(emptySet, emptySet.and(emptySet));
    }

    @Test
    public void and_operands() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> expectedSet1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6);
        ImmutableSet<Integer> expectedSet2 = new ImmutableSet<>(3,4,5,6);

        ImmutableSet<Integer> result1 = set1.and(set1);

        Assertions.assertEquals(expectedSet1, set1,
                "Вызов метода and() не должен изменять состояние объекта у которого вызывается.");
        Assertions.assertNotSame(set1, result1,
                "Метод and() должен всегда возвращать новый объект. Это правило не соблюдается, если оба " +
                        "операнда - один и тот же объект.");

        ImmutableSet<Integer> result2 = set1.and(set2);

        Assertions.assertEquals(expectedSet1, set1,
                "Вызов метода and() не должен изменять состояние объекта у которого вызывается.");
        Assertions.assertEquals(expectedSet2, set2,
                "Вызов метода and() не должен изменять состояние у передаваемого объекта.");
        Assertions.assertNotSame(result2, set1,
                "Метод and() должен всегда возвращать новый объект. Это правило не соблюдается, если оба " +
                        "операнда разные объекты. Возвращается ссылка на первый операнд.");
        Assertions.assertNotSame(result2, set2,
                "Метод and() должен всегда возвращать новый объект. Это правило не соблюдается, если оба " +
                        "операнда разные объекты. Возвращается ссылка на второй операнд.");
    }

    @Test
    public void add_properties() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6,7);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(5,6,7,8);
        ImmutableSet<Integer> universalSet = new ImmutableSet<>(1,2,3,4,5,6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(set1, set1.and(set1),
                "Не соблюдается своство идемпотентности для метода and().");

        Assertions.assertEquals(set1.and(set2), set2.and(set1),
                "Не соблюдается свойство коммутативности для метода and().");

        Assertions.assertEquals(set1.and(set2).and(set3), set2.and(set3).and(set1),
                "Не соблюдается своство ассоциативности для метода and().");

        Assertions.assertEquals(emptySet, set1.and(emptySet),
                "Не соблюдается своство нуля для метода and().");

        Assertions.assertEquals(set1, set1.and(universalSet),
                "Не соблюдается свойство единицы для метода and().");
    }

    @Test
    public void or() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6,7);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(new ImmutableSet<>(1,2,3,4,5,6,7), set1.or(set2));

        Assertions.assertEquals(new ImmutableSet<>(1,2,3,4,5,6,7,8,9,10), set1.or(set3));

        Assertions.assertEquals(emptySet, emptySet.or(emptySet));
    }

    @Test
    public void or_operands() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> expectedSet1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6);
        ImmutableSet<Integer> expectedSet2 = new ImmutableSet<>(3,4,5,6);

        ImmutableSet<Integer> result1 = set1.or(set1);

        Assertions.assertEquals(expectedSet1, set1,
                "Вызов метода or() не должен изменять состояние объекта у которого вызывается.");
        Assertions.assertNotSame(set1, result1,
                "Метод or() должен всегда возвращать новый объект. Это правило не соблюдается, если оба " +
                        "операнда - один и тот же объект.");

        ImmutableSet<Integer> result2 = set1.or(set2);

        Assertions.assertEquals(expectedSet1, set1,
                "Вызов метода or() не должен изменять состояние объекта у которого вызывается.");
        Assertions.assertEquals(expectedSet2, set2,
                "Вызов метода or() не должен изменять состояние у передаваемого объекта.");
        Assertions.assertNotSame(result2, set1,
                "Метод or() должен всегда возвращать новый объект. Это правило не соблюдается, если оба " +
                        "операнда разные объекты. Возвращается ссылка на первый операнд.");
        Assertions.assertNotSame(result2, set2,
                "Метод or() должен всегда возвращать новый объект. Это правило не соблюдается, если оба " +
                        "операнда разные объекты. Возвращается ссылка на второй операнд.");
    }

    @Test
    public void or_properties() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6,7);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(5,6,7,8);
        ImmutableSet<Integer> universalSet = new ImmutableSet<>(1,2,3,4,5,6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(set1, set1.or(set1),
                "Не соблюдается своство идемпотентности для метода or().");

        Assertions.assertEquals(set1.or(set2), set2.or(set1),
                "Не соблюдается свойство коммутативности для метода or().");

        Assertions.assertEquals(set1.or(set2).or(set3), set2.or(set3).or(set1),
                "Не соблюдается своство ассоциативности для метода or().");

        Assertions.assertEquals(set1, set1.or(emptySet),
                "Не соблюдается своство нуля для метода or().");

        Assertions.assertEquals(universalSet, set1.or(universalSet),
                "Не соблюдается свойство единицы для метода or().");
    }

    @Test
    public void difference() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6,7);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(new ImmutableSet<>(1,2), set1.difference(set2));
        Assertions.assertEquals(new ImmutableSet<>(6,7), set2.difference(set1));

        Assertions.assertEquals(set1, set1.difference(set3));

        Assertions.assertEquals(emptySet, emptySet.difference(set1));
        Assertions.assertEquals(emptySet, emptySet.difference(emptySet));
    }

    @Test
    public void difference_operands() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> expectedSet1 = new ImmutableSet<>(1,2,3,4);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(3,4,5,6);
        ImmutableSet<Integer> expectedSet2 = new ImmutableSet<>(3,4,5,6);

        ImmutableSet<Integer> result1 = set1.difference(set1);

        Assertions.assertEquals(expectedSet1, set1,
                "Вызов метода difference() не должен изменять состояние объекта у которого вызывается.");
        Assertions.assertNotSame(set1, result1,
                "Метод difference() должен всегда возвращать новый объект. Это правило не соблюдается, " +
                        "если оба операнда - один и тот же объект.");

        ImmutableSet<Integer> result2 = set1.difference(set2);

        Assertions.assertEquals(expectedSet1, set1,
                "Вызов метода difference() не должен изменять состояние объекта у которого вызывается.");
        Assertions.assertEquals(expectedSet2, set2,
                "Вызов метода difference() не должен изменять состояние у передаваемого объекта.");
        Assertions.assertNotSame(result2, set1,
                "Метод difference() должен всегда возвращать новый объект. Это правило не соблюдается, " +
                        "если оба операнда разные объекты. Возвращается ссылка на первый операнд.");
        Assertions.assertNotSame(result2, set2,
                "Метод difference() должен всегда возвращать новый объект. Это правило не соблюдается, " +
                        "если оба операнда разные объекты. Возвращается ссылка на второй операнд.");
    }

    @Test
    public void difference_properties() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> universalSet = new ImmutableSet<>(1,2,3,4,5,6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertEquals(emptySet, set1.difference(set1),
                "Вычитание множества из самого себя даёт в результате пустое множество");

        Assertions.assertEquals(emptySet, emptySet.difference(set1),
                "Свойства пустого множества относительно разности: при вычитании из пустого множества " +
                        "не пустого множества, должно получитсья пустое множество.");
        Assertions.assertEquals(set1, set1.difference(emptySet),
                "Свойства пустого множества относительно разности: при вычитании из не пустого множества " +
                        "пустого множества, должно получитсья исходное не пустое множество.");

        Assertions.assertEquals(emptySet, set1.difference(universalSet),
                "Разность множеств равна пустому множеству тогда и только тогда, когда уменьшаемое " +
                        "содержится в вычитаемом");


    }

    @Test
    public void forEach() {
        ImmutableSet<Integer> set = new ImmutableSet<>(1,2,3,4,5,6,7,8);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();
        final int[] countIterations = new int[1];

        set.forEach((Integer value) -> ++countIterations[0]);
        Assertions.assertEquals(8, countIterations[0],
                "Кол-во итераций метода forEach() должно равняться кол-ву элементов множества.");

        emptySet.forEach((Integer value) -> Assertions.fail(
                "Если множество пусто, метод forEach() не должен сделать ни одной итерации."));

        HashSet<Integer> actualItems = new HashSet<>();
        set.forEach(actualItems::add);
        Assertions.assertEquals(Set.of(1,2,3,4,5,6,7,8), actualItems,
                "Метод forEach( должен перебрать все элементы множества.");
    }

    @Test
    public void iterator() {
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();
        ImmutableSet<Integer> set = new ImmutableSet<>(1,2,3,4,5,6,7,8);

        Assertions.assertFalse(emptySet.iterator().hasNext(),
                "Для пустого множества метод hasNext() итератора сразу должен возвращать false.");

        int countIterations = 0;
        for(Integer i : set) ++countIterations;
        Assertions.assertEquals(8, countIterations,
                "Кол-во итераций итератора должно равняться кол-ву элементов множества.");
        countIterations = 0;
        for(Integer i : emptySet) ++countIterations;
        Assertions.assertEquals(0, countIterations,
                "Кол-во итераций итератора пустого множества должно равняться нулю.");

        ImmutableSet<Integer> actualItems = new ImmutableSet<>();
        for(Integer i : set) actualItems = actualItems.add(i);
        Assertions.assertEquals(set, actualItems,
                "Итератор должен перебрать все элементы множества.");
    }

    @Test
    public void containsSet() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(1,2,3);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(6,7,8,9,10);
        ImmutableSet<Integer> set4 = new ImmutableSet<>(2,3,4,5,6);
        ImmutableSet<Integer> universalSet = new ImmutableSet<>(1,2,3,4,5,6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertTrue(set1.containsSet(set1),
                "Любое множество является собственным нестрогим подмножеством.");

        Assertions.assertTrue(set1.containsSet(set2),
                "Если все элемента множества-аргумента также содержаться в исходном множестве, метод " +
                        "containsSet должен возвращать true.");

        Assertions.assertTrue(set1.containsSet(emptySet),
                "Не соблюдается свойство: пустое множество является подмножеством любого другого множества.");

        Assertions.assertTrue(emptySet.containsSet(emptySet),
                "Не соблюдается свойство: пустое множество является подмножеством самого себя.");

        Assertions.assertFalse(set1.containsSet(set3));

        Assertions.assertFalse(set1.containsSet(set4));

        Assertions.assertFalse(set1.containsSet(universalSet));
    }

    @Test
    public void strictlyContainsSet() {
        ImmutableSet<Integer> set1 = new ImmutableSet<>(1,2,3,4,5);
        ImmutableSet<Integer> set2 = new ImmutableSet<>(1,2,3);
        ImmutableSet<Integer> set3 = new ImmutableSet<>(6,7,8,9,10);
        ImmutableSet<Integer> set4 = new ImmutableSet<>(2,3,4,5,6);
        ImmutableSet<Integer> universalSet = new ImmutableSet<>(1,2,3,4,5,6,7,8,9,10);
        ImmutableSet<Integer> emptySet = new ImmutableSet<>();

        Assertions.assertFalse(set1.strictlyContainsSet(set1),
                "Любое множество не является собственным строгим подмножеством.");

        Assertions.assertTrue(set1.strictlyContainsSet(set2),
                "Если все элемента множества-аргумента также содержаться в исходном множестве и при этом " +
                        "мощность множества-аргумента меньше исходного, метод strictlyContainsSet должен " +
                        "возвращать true.");

        Assertions.assertTrue(set1.strictlyContainsSet(emptySet),
                "Не соблюдается свойство: пустое множество является строгим подмножеством любого другого " +
                        "множества.");

        Assertions.assertFalse(emptySet.strictlyContainsSet(emptySet),
                "Не соблюдается свойство: пустое множество не является строгим подмножеством самого себя.");

        Assertions.assertFalse(set1.strictlyContainsSet(set3));

        Assertions.assertFalse(set1.strictlyContainsSet(set4));

        Assertions.assertFalse(set1.strictlyContainsSet(universalSet));
    }


    private static class Box<T> {

        private T value;
        private String label;

        Box(T value, String label) {
            this.value = value;
            this.label = label;
        }

        T getValue() {
            return value;
        }

        String getLabel() {
            return label;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Box<?> box = (Box<?>) o;
            return Objects.equals(value, box.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

    }

}