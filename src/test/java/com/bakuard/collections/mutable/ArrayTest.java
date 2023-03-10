package com.bakuard.collections.mutable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

class ArrayTest {

    @Test
    @DisplayName("Array(type): => array length equal 0")
    public void Array_empty1() {
        Array<String> emptyArray = new Array<>(String.class);

        Assertions.assertEquals(0, emptyArray.getLength());
    }

    @Test
    @DisplayName("Array(type): try get item after creating => exception")
    public void Array_empty2() {
        Array<String> emptyArray = new Array<>(String.class);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> emptyArray.get(0));
    }

    @Test
    @DisplayName("Array(type, length): => all items after create is null")
    public void Array_length1() {
        Array<String> array = new Array<>(String.class, 100);

        for(int i = 0; i < array.getLength(); i++) {
            Assertions.assertNull(array.get(i));
        }
    }

    @Test
    @DisplayName("Array(type, length): => array length equal length param")
    public void Array_length2() {
        Array<String> array = new Array<>(String.class, 100);

        Assertions.assertEquals(100, array.getLength());
    }

    @Test
    @DisplayName("Array(type, length): negative length => exception")
    public void Array_length3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Array<>(String.class, -1));
    }

    @Test
    @DisplayName("Array(type, length): length == 0 => array length equal 0")
    public void Array_length4() {
        Array<String> emptyArray = new Array<>(String.class, 0);

        Assertions.assertEquals(0, emptyArray.getLength());
    }

    @Test
    @DisplayName("Array(type, length): length == 0, try get item after creating => exception")
    public void Array_length5() {
        Array<String> emptyArray = new Array<>(String.class, 0);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> emptyArray.get(0));
    }

    @Test
    @DisplayName("Array(other): not empty Array -> copy is equivalent to original")
    public void Array_copy1() {
        Array<Integer> original = new Array<>(Integer.class);
        original.addAll(100000,220000,300000,470000,560000);
        Array<Integer> copy = new Array<>(original);

        Assertions.assertEquals(original, copy);
    }

    @Test
    @DisplayName("Array(other): empty Array -> copy is equivalent to original")
    public void Array_copy2() {
        Array<Integer> original = new Array<>(Integer.class);
        Array<Integer> copy = new Array<>(original);

        Assertions.assertEquals(original, copy);
    }

    @Test
    @DisplayName("Array(other): not empty Array -> copy contains the same objects")
    public void Array_copy3() {
        Array<Object> original = new Array<>(Object.class);
        original.addAll(new Object(), new Object(), new Object());
        Array<Object> copy = new Array<>(original);

        Assertions.assertSame(original.get(0), copy.get(0));
        Assertions.assertSame(original.get(1), copy.get(1));
        Assertions.assertSame(original.get(2), copy.get(2));
    }

    @Test
    @DisplayName("Array(other): changes to the original must not affect the copy")
    public void Array_copy4() {
        Array<Integer> original = new Array<>(Integer.class);
        original.addAll(1,2,3);
        Array<Integer> copy = new Array<>(original);
        Array<Integer> expected = new Array<>(Integer.class);
        expected.addAll(1,2,3);

        original.addAll(4,5,6);
        original.quickRemove(0);

        Assertions.assertEquals(expected, copy);
    }

    @Test
    @DisplayName("Array(data[]): data[] is null => exception")
    public void Array_data1() {
        Integer[] data = null;

        Assertions.assertThrows(NullPointerException.class, () -> new Array<>(data));
    }

    @Test
    @DisplayName("Array(data[]): data[] is empty => create empty array")
    public void Array_data2() {
        Array<Integer> array = new Array<>(new Integer[]{});

        Assertions.assertEquals(0, array.getLength());
    }

    @Test
    @DisplayName("Array(data[]): data[] contains several item => create array with these items.")
    public void Array_data3() {
        Array<Integer> actual = new Array<>(10, null, 30, 30);
        Array<Integer> expected = new Array<>(Integer.class, 4);
        expected.set(0, 10);
        expected.set(1, null);
        expected.set(2, 30);
        expected.set(3, 30);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Array(data[]): change array data[] after creating IntArray => new object IntArray don't change")
    public void Array_data4() {
        Integer[] data = {10, 20, 30, 40, 50, 60};
        Array<Integer> actual = new Array<>(data);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60);

        data[0] = 1000;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("get(index): index < 0 => exception")
    public void get1() {
        Array<Integer> array = new Array<>(Integer.class, 3);
        array.set(0, 10);
        array.set(1, 20);
        array.set(2, 30);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
    }

    @Test
    @DisplayName("get(index): index == array length => exception")
    public void get2() {
        Array<Integer> array = new Array<>(Integer.class, 3);
        array.set(0, 10);
        array.set(1, 20);
        array.set(2, 30);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(3));
    }

    @Test
    @DisplayName("get(index): index > array length => exception")
    public void get3() {
        Array<Integer> array = new Array<>(Integer.class, 3);
        array.set(0, 10);
        array.set(1, 20);
        array.set(2, 30);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.get(4));
    }

    @Test
    @DisplayName("set(index, value): index < 0 => exception")
    public void set1() {
        Array<Integer> array = new Array<>(1, 120, 12);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(-1, 100));
    }

    @Test
    @DisplayName("set(index, value): index == array length => exception")
    public void set2() {
        Array<Integer> array = new Array<>(0,0,1,12);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(4, 100));
    }

    @Test
    @DisplayName("set(index, value): index >= array length => exception")
    public void set3() {
        Array<Integer> array = new Array<>(0,0,1,12);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.set(5, 100));
    }

    @Test
    @DisplayName("set(index, value): old value by this index is null => return null")
    public void set4() {
        Array<Integer> array = new Array<>(10, 20, null, 40);

        Assertions.assertNull(array.get(2));
    }

    @Test
    @DisplayName("set(index, value): old value by this index is not null => return old value")
    public void set5() {
        Array<Integer> array = new Array<>(10, 20, null, 40);

        Assertions.assertEquals(10, array.get(0));
    }

    @Test
    @DisplayName("set(index, value): value is null => set null by this index")
    public void set6() {
        Array<Integer> array = new Array<>(10, 20, 30, 40);

        array.set(1, null);

        Assertions.assertNull(array.get(1));
    }

    @Test
    @DisplayName("set(index, value): value is not null => set value by this index")
    public void set7() {
        Array<Integer> array = new Array<>(10, 20, 30, 40);

        array.set(1, 1000);

        Assertions.assertEquals(1000, array.get(1));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index < 0 => exception")
    public void setAndExpend1() {
        Array<Integer> array = new Array<>(0, 10, 100);

        Assertions.assertThrows(IndexOutOfBoundsException.class, ()-> array.setAndExpand(-1, 20));
    }

    @Test
    @DisplayName("setAndExpend(index, value): index == length => add new item")
    public void setAndExpend2() {
        Array<Integer> array = new Array<>(0, 10, 25, 26);

        array.setAndExpand(4, 100);

        Assertions.assertEquals(100, array.get(4));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index == length => length == index + 1")
    public void setAndExpand3() {
        Array<Integer> array = new Array<>(0, 10, 25, 26, 67);

        array.setAndExpand(5, 1000);

        Assertions.assertEquals(6, array.getLength());
    }

    @Test
    @DisplayName("setAdnExpand(index, value): index > length => add new item")
    public void setAndExpand4() {
        Array<Integer> array = new Array<>(0, 10, 25, 26, 68);

        array.setAndExpand(10, 100);

        Assertions.assertEquals(100, array.get(10));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index > length => length == index + 1")
    public void setAndExpand5() {
        Array<Integer> array = new Array<>(1, 1, 20, 90, 900);

        array.setAndExpand(10, 1000);

        Assertions.assertEquals(11, array.getLength());
    }

    @Test
    @DisplayName("setAndExpand(index, value): old value by this index is null => return null")
    public void setAndExpand6() {
        Array<Integer> array = new Array<>(0, 10, null, 45);

        Assertions.assertNull(array.setAndExpand(2, 1000));
    }

    @Test
    @DisplayName("setAndExpand(index, value): old value by this index is not null => return old value")
    public void setAndExpand7() {
        Array<Integer> array = new Array<>(0, 100, 120);

        Assertions.assertEquals(120, array.setAndExpand(2, 1000));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index == length => return null")
    public void setAndExpand8() {
        Array<Integer> array = new Array<>(0, 120, 111);

        Assertions.assertNull(array.setAndExpand(3, 1000));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index > length => return null")
    public void setAndExpand9() {
        Array<Integer> array = new Array<>(0, 10, 25, 26, 67);

        Assertions.assertNull(array.setAndExpand(10, 1000));
    }

    @Test
    @DisplayName("setAndExpand(index, value): value == null => set null")
    public void setAndExpand10() {
        Array<Integer> array = new Array<>(0, 120, 340, 700);

        array.setAndExpand(1, null);

        Assertions.assertNull(array.get(1));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index == length => old values are preserved")
    public void setAndExpand11() {
        Array<Integer> array = new Array<>(0, 100, 200);

        array.setAndExpand(3, 250);

        Assertions.assertEquals(0, array.get(0));
        Assertions.assertEquals(100, array.get(1));
        Assertions.assertEquals(200, array.get(2));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index > length => old values are preserved")
    public void setAndExpand12() {
        Array<Integer> array = new Array<>(0, 100, 200);

        array.setAndExpand(6, 250);

        Assertions.assertEquals(0, array.get(0));
        Assertions.assertEquals(100, array.get(1));
        Assertions.assertEquals(200, array.get(2));
    }

    @Test
    @DisplayName("setAndExpand(index, value): index > length => all new added items is null")
    public void setAndExpand13() {
        Array<Integer> array = new Array<>(0, 10, 120, 300);

        array.setAndExpand(10, 1000);

        for(int i = 4; i < 10; i++) {
            Assertions.assertNull(array.get(i));
        }
    }

    @Test
    @DisplayName("add(value): => add value and increase length")
    public void add1() {
        Array<Integer> actual = new Array<>(0, 10, 120);
        Array<Integer> expected = new Array<>(0, 10, 120, 1000);

        actual.add(1000);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(T... data): data is empty => array don't change")
    public void addAll_varargs1() {
        Array<Integer> actual = new Array<>(0, 12, 45);
        Array<Integer> expected = new Array<>(actual);

        actual.addAll();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(T... data): array is empty, add several items => add all values in same order")
    public void addAll_varargs2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(0, 120, 200);

        actual.addAll(0, 120, 200);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(T... data): array is empty, add one item => add item")
    public void addAll_varargs3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(1000);

        actual.addAll(1000);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(T... data): array is not empty, add several item => add values in same order")
    public void addAll_varargs4() {
        Array<Integer> actual = new Array<>(0, 120, 34);
        Array<Integer> expected = new Array<>(0, 120, 34, 56, 0, 10);

        actual.addAll(56, 0, 10);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(T... data): array is not empty, add one item => add item to the end")
    public void addAll_varargs5() {
        Array<Integer> actual = new Array<>(0, 10 ,120);
        Array<Integer> expected = new Array<>(0, 10, 120, 120);

        actual.addAll(120);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(T... data): data contains null => add all values in same order include null")
    public void addAll_vararg6() {
        Array<Integer> actual = new Array<>(0, 0, 10);
        Array<Integer> expected = new Array<>(0, 0, 10, 100, null, 100, null, null);

        actual.addAll(100, null, 100, null, null);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(Array<T> data): data is empty => array don't change")
    public void addAll_Array1() {
        Array<Integer> actual = new Array<>(0, 10, 100);
        Array<Integer> expected = new Array<>(actual);

        actual.addAll(new Array<>());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(Array<T> data): array is empty, data contains several items => add all values in same order")
    public void addAll_Array2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(0, 10, 90);

        actual.addAll(new Array<>(0, 10, 90));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(Array<T> data): array is empty, data contains one item => add item")
    public void addAll_array3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(1000);

        actual.addAll(new Array<>(1000));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(Array<T> data): array is not empty, data contains several items => add all values in same order")
    public void addAll_Array4() {
        Array<Integer> actual = new Array<>(10, 10, 20);
        Array<Integer> expected = new Array<>(10, 10, 20, 30, 0, 10);

        actual.addAll(new Array<>(30, 0, 10));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(Array<T> data): array is not empty, data contains one item => add item")
    public void addAll_Array5() {
        Array<Integer> actual = new Array<>(10, 10, 100);
        Array<Integer> expected = new Array<>(10, 10, 100, 0);

        actual.addAll(new Array<>(0));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("addAll(Array<T> data): data contains null => add all item in same order include null")
    public void addAll_Array6() {
        Array<Integer> actual = new Array<>(10, 20, 25);
        Array<Integer> expected = new Array<>(10, 20, 25, null, 1000, null, null);

        actual.addAll(new Array<>(null, 1000, null, null));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert(index, value): index < 0 => exception")
    public void insert1() {
        Array<Integer> array = new Array<>(10, 20, 30, 40);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.insert(-1, 0));
    }

    @Test
    @DisplayName("insert(index, value): index > length => exception")
    public void insert2() {
        Array<Integer> array = new Array<>(10, 20, 30, 40);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> array.insert(5, 100));
    }

    @Test
    @DisplayName("insert(index, value): array is empty, index == 0 => add item")
    public void insert3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(1000);

        actual.insert(0, 1000);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert(index, value): array contains one item, index == 0 => insert item, shift old item")
    public void insert4() {
        Array<Integer> actual = new Array<>(1000);
        Array<Integer> expected = new Array<>(0, 1000);

        actual.insert(0, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert(index, value): array contains one item, index == 1 => insert item, don't shift olf item")
    public void insert5() {
        Array<Integer> actual = new Array<>(1000);
        Array<Integer> expected = new Array<>(1000, 0);

        actual.insert(1, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert(index, value): array contains several items, index == 0 => insert item, shift all old items")
    public void insert6() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(0, 10, 20, 30, 40, 50);

        actual.insert(0, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            insert(index, value):
             array contains several items,
             insert in the middle =>
             insert item,
             shift all item after index,
             all item before index don't shift
            """)
    public void insert7() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(10, 20, 30, 35, 40, 50);

        actual.insert(3, 35);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert(index, value): array contains several items, insert == length => insert item, don't shift all old items")
    public void insert8() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60);

        actual.insert(5, 60);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("insert(index, value): array contains several items, insert null => insert null")
    public void insert9() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(10, 20, null, 30, 40, 50);

        actual.insert(2, null);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("binaryInsert(value, comparator): array is empty => add value, return 0")
    public void binaryInsert1() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(1000);

        int index = actual.binaryInsert(1000, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains one item,
             value less then all items
             => add value,
                return 0
            """)
    public void binaryInsert2() {
        Array<Integer> actual = new Array<>(100);
        Array<Integer> expected = new Array<>(10, 100);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains one item,
             value greater then all items
             => add value,
                return 1
            """)
    public void binaryInsert3() {
        Array<Integer> actual = new Array<>(100);
        Array<Integer> expected = new Array<>(100, 110);

        int index = actual.binaryInsert(110, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains one item,
             value equal this item
             => add value,
                return 0 or 1
            """)
    public void binaryInsert4() {
        Array<Integer> actual = new Array<>(100);
        Array<Integer> expected = new Array<>(100, 100);

        int index = actual.binaryInsert(100, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 1);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains two item,
             value less then first item,
             value is unique
             => add value,
                return 0
            """)
    public void binaryInsert5() {
        Array<Integer> actual = new Array<>(10, 20);
        Array<Integer> expected = new Array<>(0, 10, 20);

        int index = actual.binaryInsert(0, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains two item,
             values greater then last item,
             value is unique
             => add value,
                return 2
            """)
    public void binaryInsert6() {
        Array<Integer> actual = new Array<>(10, 20);
        Array<Integer> expected = new Array<>(10, 20, 80);

        int index = actual.binaryInsert(80, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(2, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains two item,
             value must be inserted in the middle,
             value is unique
             => add value,
                return 1
            """)
    public void binaryInsert7() {
        Array<Integer> actual = new Array<>(10, 50);
        Array<Integer> expected = new Array<>(10, 25, 50);

        int index = actual.binaryInsert(25, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(1, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains two item,
             values equal the smallest item
             => add value,
                return 0 or 1
            """)
    public void binaryInsert8() {
        Array<Integer> actual = new Array<>(10, 20);
        Array<Integer> expected = new Array<>(10, 10, 20);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 1);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains two items,
             values equal the biggest item
             => add value,
                return 1 or 2
            """)
    public void binaryInsert9() {
        Array<Integer> actual = new Array<>(10, 20);
        Array<Integer> expected = new Array<>(10, 20, 20);

        int index = actual.binaryInsert(20, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 1 && index <= 2);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains two items,
             all array items are equal,
             value is not unique
              => add value,
                 return value belongs to [0, 2]
            """)
    public void binaryInsert10() {
        Array<Integer> actual = new Array<>(10, 10);
        Array<Integer> expected = new Array<>(10, 10, 10);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 2);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more then two items,
             array contains an even number of elements,
             value less than first item,
             value is unique
             => add value,
                return 0
            """)
    public void binaryInsert11() {
        Array<Integer> actual = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>(0, 10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);

        int index = actual.binaryInsert(0, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value greater than last item,
             value is unique
             => add value,
                return value equal old length of array
            """)
    public void binaryInsert12() {
        Array<Integer> actual = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(110, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(10, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value must be inserted in the middle,
             value is unique
             => add value,
                return value is middle index
            """)
    public void binaryInsert13() {
        Array<Integer> actual = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>(10, 20 ,30, 40, 50, 55, 60, 70, 80, 90, 100);

        int index = actual.binaryInsert(55, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(5, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value equal smallest item
             => add value,
                return value belongs to [0, last smallest item index + 1]
            """)
    public void binaryInsert14() {
        Array<Integer> actual = new Array<>(10, 10 ,10, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>(10, 10, 10 ,10, 40, 50, 60, 70, 80, 90, 100);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 3);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value equal the biggest item
             => add value,
                return value belongs to [first the biggest item index, old length]
            """)
    public void binaryInsert15() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 100, 100, 100);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60, 70, 100, 100, 100, 100);

        int index = actual.binaryInsert(100, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 7 && index <= 10);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value equal middle item
             => add value,
                return value belongs to [first middle item index, last middle item index + 1]
            """)
    public void binaryInsert16() {
        Array<Integer> actual = new Array<>(10, 20, 30, 50, 50, 50, 50, 80, 90, 100);
        Array<Integer> expected = new Array<>(10, 20, 30, 50, 50, 50, 50, 50, 80, 90, 100);

        int index = actual.binaryInsert(50, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 3 && index <= 7);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             all array items are equal,
             value is not unique
             => add value,
                return value belongs to [0, array old length]
            """)
    public void binaryInsert17() {
        Array<Integer> actual = new Array<>(67, 67, 67, 67, 67, 67, 67, 67, 67, 67);
        Array<Integer> expected = new Array<>(67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67);

        int index = actual.binaryInsert(67, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 10);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more then two items,
             array contains an odd number of elements,
             value less than first item,
             value is unique
             => add value,
                return 0
            """)
    public void binaryInsert18() {
        Array<Integer> actual = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = new Array<>(0, 10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(0, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(0, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value greater than last item,
             value is unique
             => add value,
                return value equal old length of array
            """)
    public void binaryInsert19() {
        Array<Integer> actual = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110, 120);

        int index = actual.binaryInsert(120, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(11, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value must be inserted in the middle,
             value is unique
             => add value,
                return value is middle index
            """)
    public void binaryInsert20() {
        Array<Integer> actual = new Array<>(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = new Array<>(10, 20 ,30, 40, 50, 55, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(55, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(5, index);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value equal smallest item
             => add value,
                return value belongs to [0, last smallest item index + 1]
            """)
    public void binaryInsert21() {
        Array<Integer> actual = new Array<>(10, 10 ,10, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = new Array<>(10, 10, 10 ,10, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 3);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value equal the biggest item
             => add value,
                return value belongs to [first the biggest item index, old length]
            """)
    public void binaryInsert22() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110, 110);

        int index = actual.binaryInsert(110, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 8 && index <= 11);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value equal middle item
             => add value,
                return value belongs to [first middle item index, last middle item index + 1]
            """)
    public void binaryInsert23() {
        Array<Integer> actual = new Array<>(10, 20, 30, 50, 50, 50, 50, 80, 90, 100, 110);
        Array<Integer> expected = new Array<>(10, 20, 30, 50, 50, 50, 50, 50, 80, 90, 100, 110);

        int index = actual.binaryInsert(50, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 3 && index <= 7);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             all array items are equal,
             value is not unique
             => add value,
                return value belongs to [0, array old length]
            """)
    public void binaryInsert24() {
        Array<Integer> actual = new Array<>(67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67);
        Array<Integer> expected = new Array<>(67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67);

        int index = actual.binaryInsert(67, Integer::compareTo);

        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(index >= 0 && index <= 11);
    }

    @Test
    @DisplayName("quickRemove(index): index < 0 => exception")
    public void quickRemove1() {
        Array<Integer> actual = new Array<>(0, 10, 20);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> actual.quickRemove(-1));
    }

    @Test
    @DisplayName("quickRemove(index): index == array.getLength() => exception")
    public void quickRemove2() {
        Array<Integer> actual = new Array<>(0, 10, 20);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> actual.quickRemove(3));
    }

    @Test
    @DisplayName("quickRemove(index): index > array.getLength() => exception")
    public void quickRemove3() {
        Array<Integer> actual = new Array<>(10, 20, 30);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> actual.quickRemove(4));
    }

    @Test
    @DisplayName("quickRemove(index): index == 0 => delete item")
    public void quickRemove4() {
        Array<Integer> actual = new Array<>(0, 10, 20, 30);
        Array<Integer> expected = new Array<>(30, 10, 20);

        actual.quickRemove(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("quickRemove(index): index in the middle => delete item")
    public void quickRemove5() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);
        Array<Integer> expected = new Array<>(10, 40, 30);

        actual.quickRemove(1);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("quickRemove(index): index of last item => delete item")
    public void quickRemove6() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(10, 20, 30, 40);

        actual.quickRemove(4);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("quickRemove(index): array contains one item, index == 0 => empty array")
    public void quickRemove7() {
        Array<Integer> actual = new Array<>(1);
        Array<Integer> expected = new Array<>();

        actual.quickRemove(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("quickRemove(index): index of last item => set null for the last item")
    public void quickRemove8() {
        Array<Integer> actual = new Array<>(10, 20, 30);

        actual.quickRemove(2);
        actual.expandTo(5);

        Assertions.assertNull(actual.get(2));
        Assertions.assertNull(actual.get(3));
        Assertions.assertNull(actual.get(4));
    }

    @Test
    @DisplayName("quickRemove(index): index in the middle => set last item to this index")
    public void quickRemove9() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);

        actual.quickRemove(1);

        Assertions.assertEquals(40, actual.get(1));
    }

    @Test
    @DisplayName("quickRemove(index): index is the middle => return removed value")
    public void quickRemove10() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);

        Assertions.assertEquals(30, actual.quickRemove(2));
    }

    @Test
    @DisplayName("quickRemove(index): index of last item => return removed value")
    public void quickRemove11() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertEquals(50, actual.quickRemove(4));
    }

    @Test
    @DisplayName("quickRemove(index): remove all items => empty array")
    public void quickRemove12() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60);
        Array<Integer> expected = new Array<>();

        while(actual.getLength() != 0) actual.quickRemove(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("orderedRemove(index): index < 0 => exception")
    public void orderedRemove1() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> actual.orderedRemove(-1));
    }

    @Test
    @DisplayName("orderedRemove(index): index == array length => exception")
    public void orderedRemove2() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> actual.orderedRemove(5));
    }

    @Test
    @DisplayName("orderedRemove(index): index > array length => exception")
    public void orderedRemove3() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> actual.orderedRemove(6));
    }

    @Test
    @DisplayName("orderedRemove(index): index == 0 => delete item")
    public void orderedRemove4() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(20, 30, 40, 50);

        actual.orderedRemove(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("orderedRemove(index): index in the middle => delete item")
    public void orderedRemove5() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(10, 20, 40, 50);

        actual.orderedRemove(2);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("orderedRemove(index): index of last item => delete item")
    public void orderedRemove6() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> expected = new Array<>(10, 20, 30, 40);

        actual.orderedRemove(4);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("orderedRemove(index): array contains one item, index == 0 => empty array")
    public void orderedRemove7() {
        Array<Integer> actual = new Array<>(1);
        Array<Integer> expected = new Array<>();

        actual.orderedRemove(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("orderedRemove(index): index of last item => set null for the last item")
    public void orderedRemove8() {
        Array<Integer> actual = new Array<>(10, 20, 30);

        actual.orderedRemove(2);
        actual.expandTo(5);

        Assertions.assertNull(actual.get(2));
        Assertions.assertNull(actual.get(3));
        Assertions.assertNull(actual.get(4));
    }

    @Test
    @DisplayName("orderedRemove(index): index in the middle => return removed value")
    public void orderedRemove9() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60);

        Assertions.assertEquals(40, actual.orderedRemove(3));
    }

    @Test
    @DisplayName("orderedRemove(index): index of last item => return removed value")
    public void orderedRemove10() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60);

        Assertions.assertEquals(60, actual.orderedRemove(5));
    }

    @Test
    @DisplayName("orderedRemove(index): remove all item => empty array")
    public void orderedRemove11() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60);
        Array<Integer> expected = new Array<>();

        while(actual.getLength() != 0) actual.orderedRemove(0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("clear(): array contains items => empty array")
    public void clear1() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>();

        actual.clear();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("clear(): array is empty => do nothing")
    public void clear2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>();

        actual.clear();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("clear(): array contains items => set null for each item")
    public void clear3() {
        Array<Integer> actual = new Array<>(10,20,30,40,50,60,70,80,90,100);

        actual.clear();
        actual.expandTo(10);

        for(int i = 0; i < 10; i++) Assertions.assertNull(actual.get(i));
    }

    @Test
    @DisplayName("sort(comparator): array is empty => do nothing")
    public void sort1() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>();

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains one item => do nothing")
    public void sort2() {
        Array<Integer> actual = new Array<>(512);
        Array<Integer> expected = new Array<>(512);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(actual, expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains two different items, order equal comparator order => do nothing")
    public void sort3() {
        Array<Integer> actual = new Array<>(10, 20);
        Array<Integer> expected = new Array<>(10, 20);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains two different items, order don't equal comparator order => sort")
    public void sort4() {
        Array<Integer> actual = new Array<>(20, 10);
        Array<Integer> expected = new Array<>(10, 20);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains two equal items => do nothing")
    public void sort5() {
        Array<Integer> actual = new Array<>(100, 100);
        Array<Integer> expected = new Array<>(100, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains several different items, order equal comparator order => do nothing")
    public void sort6() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>(actual);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains several different items, order is inverse to comparator order => sort")
    public void sort7() {
        Array<Integer> actual = new Array<>(100, 90, 80, 70, 60, 50, 40, 30, 20, 10);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains several items, all items is equal => do nothing")
    public void sort8() {
        Array<Integer> actual = new Array<>(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100);
        Array<Integer> expected = new Array<>(actual);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains several item, all items is unique, order is random => sort")
    public void sort9() {
        Array<Integer> actual = new Array<>(100, 10, 20, 30, 50, 40, 90, 80, 60, 70);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("sort(comparator): array contains several item, there are equal items, order is random => sort")
    public void sort10() {
        Array<Integer> actual = new Array<>(10, 20, 30, 100, 100, 100, 90, 100, 80, 70, 60, 70, 50, 40);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, 50, 60, 70, 70, 80, 90, 100, 100, 100, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("linearSearch(T value): array empty => return -1")
    public void linearSearch1() {
        Array<Integer> actual = new Array<>();

        Assertions.assertEquals(-1, actual.linearSearch(100));
    }

    @Test
    @DisplayName("linearSearch(T value): array contains null item => return index first null value")
    public void linearSearch2() {
        Array<Integer> actual = new Array<>(10, 20, null, null, 30, null);

        Assertions.assertEquals(2, actual.linearSearch(null));
    }

    @Test
    @DisplayName("linearSearch(T value): array contains several equal items => return index first item equal value")
    public void linearSearch3() {
        Array<Integer> actual = new Array<>(20, 10, 0, 40, null, null, 50, 50, 40);

        Assertions.assertEquals(3, actual.linearSearch(40));
    }

    @Test
    @DisplayName("linearSearch(T value): array is not empty, array don't contain item => return -1")
    public void linearSearch4() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50, 90);

        Assertions.assertEquals(-1, array.linearSearch(45));
    }

    @Test
    @DisplayName("binarySearch(comparator): array is empty => return -1")
    public void binarySearch1() {
        Array<Integer> actual = new Array<>();

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(100)));
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains one item, item equal value => return 0")
    public void binarySearch2() {
        Array<Integer> actual = new Array<>(10);

        Assertions.assertEquals(0, actual.binarySearch(i -> -i.compareTo(10)));
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains one item, value less than the smallest item => return -1")
    public void binarySearch3() {
        Array<Integer> actual = new Array<>(1);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(0)));
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains one item, value greater than the biggest item => return -1")
    public void binarySearch4() {
        Array<Integer> actual = new Array<>(10);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(20)));
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains two items, value less than the smallest item => return -1")
    public void binarySearch5() {
        Array<Integer> actual = new Array<>(10, 20);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(0)));
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains two items, value greater than the biggest item => return -1")
    public void binarySearch6() {
        Array<Integer> actual = new Array<>(10, 20);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(30)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains two items,
             array doesn't contain value,
             value must be located in the middle of the array
             => return -1
            """)
    public void binarySearch7() {
        Array<Integer> actual = new Array<>(10, 20);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(15)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains two items,
             value equal the smallest item
             => return 0
            """)
    public void binarySearch8() {
        Array<Integer> actual = new Array<>(10, 20);

        Assertions.assertEquals(0, actual.binarySearch(i -> -i.compareTo(10)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains two items,
             value equal the biggest item
             => return 1
            """)
    public void binarySearch9() {
        Array<Integer> actual = new Array<>(10, 20);

        Assertions.assertEquals(1, actual.binarySearch(i -> -i.compareTo(20)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains two items,
             all array items are equal,
             value equal each array item
             => return value must belong [0, 1]
            """)
    public void binarySearch10() {
        Array<Integer> actual = new Array<>(10, 10);

        int index = actual.binarySearch(i -> -i.compareTo(10));

        Assertions.assertTrue(index >= 0 && index <= 1);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             value less than the smallest item
             => return -1
            """)
    public void binarySearch11() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(0)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             value greater than the biggest item
             = return -1
            """)
    public void binarySearch12() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(110)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             array doesn't contain item,
             value must be located in the middle of the array
             => return -1
            """)
    public void binarySearch13() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(55)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             value equal the smallest item,
             the smallest item is unique
             => return 0
            """)
    public void binarySearch14() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        Assertions.assertEquals(0, actual.binarySearch(i -> -i.compareTo(10)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             array contains item equal value,
             value in the middle of the array,
             value is unique
             => return index of item equal value
            """)
    public void binarySearch15() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        Assertions.assertEquals(4, actual.binarySearch(i -> -i.compareTo(50)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two  items,
             items number is even,
             value equal the greater item,
             the greater item is unique
             => return index the greater item
            """)
    public void binarySearch16() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        Assertions.assertEquals(9, actual.binarySearch(i -> -i.compareTo(100)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             value equal the smallest item,
             array contains several items equal value
             => return value must belong [first the smallest item index, last the smallest item index]
            """)
    public void binarySearch17() {
        Array<Integer> actual = new Array<>(20, 20, 20, 40, 50, 60, 70, 80, 90, 100);

        int index = actual.binarySearch(i -> -i.compareTo(20));

        Assertions.assertTrue(index >= 0 && index <= 2);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             value equal the middle item,
             array contains several items equal value
             => return value must belong [first middle item, last middle item]
            """)
    public void binarySearch18() {
        Array<Integer> actual = new Array<>(10, 20, 30, 50, 50, 50, 70, 80, 90, 100);

        int index = actual.binarySearch(i -> -i.compareTo(50));

        Assertions.assertTrue(index >= 3 && index <= 5);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is even,
             value equal the biggest item,
             array contains several items equal value
             => return value must belong [first the biggest item index, last the biggest item index]
            """)
    public void binarySearch19() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 100, 100, 100);

        int index = actual.binarySearch(i -> -i.compareTo(100));

        Assertions.assertTrue(index >= 7 && index <= 9);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value less than the smallest item
             => return -1
            """)
    public void binarySearch20() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(0)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value greater than the biggest item
             = return -1
            """)
    public void binarySearch21() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(120)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             array doesn't contain item,
             value must be located in the middle of the array
             => return -1
            """)
    public void binarySearch22() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        Assertions.assertEquals(-1, actual.binarySearch(i -> -i.compareTo(55)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value equal the smallest item,
             the smallest item is unique
             => return 0
            """)
    public void binarySearch23() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        Assertions.assertEquals(0, actual.binarySearch(i -> -i.compareTo(10)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             array contains item equal value,
             value in the middle of the array,
             value is unique
             => return index value of item equal value
            """)
    public void binarySearch24() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        Assertions.assertEquals(4, actual.binarySearch(i -> -i.compareTo(50)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value equal the greater item,
             the greater item is unique
             => return index the greater item
            """)
    public void binarySearch25() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        Assertions.assertEquals(10, actual.binarySearch(i -> -i.compareTo(110)));
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value equal the smallest item,
             array contains several items equal value
             => return value must belong [first the smallest item index, last the smallest item index]
            """)
    public void binarySearch26() {
        Array<Integer> actual = new Array<>(20, 20, 20, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binarySearch(i -> -i.compareTo(20));

        Assertions.assertTrue(index >= 0 && index <= 2);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value equal the middle item,
             array contains several items equal value
             => return value must belong [first middle item, last middle item]
            """)
    public void binarySearch27() {
        Array<Integer> actual = new Array<>(10, 20, 30, 50, 50, 50, 70, 80, 90, 100, 110);

        int index = actual.binarySearch(i -> -i.compareTo(50));

        Assertions.assertTrue(index >= 3 && index <= 5);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains more than two items,
             items number is odd,
             value equal the biggest item,
             array contains several items equal value
             => return value must belong [first the biggest item index, last the biggest item index]
            """)
    public void binarySearch28() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);

        int index = actual.binarySearch(i -> -i.compareTo(110));

        Assertions.assertTrue(index >= 8 && index <= 10);
    }

    @Test
    @DisplayName("expandTo(newLength): newLength < length => return false")
    public void expandTo1() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);

        Assertions.assertFalse(actual.expandTo(3));
    }

    @Test
    @DisplayName("expandTo(newLength): newLength == length => return false")
    public void expandTo2() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);

        Assertions.assertFalse(actual.expandTo(4));
    }

    @Test
    @DisplayName("expandTo(newLength): newLength > length => return true")
    public void expandTo3() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);

        Assertions.assertTrue(actual.expandTo(5));
    }

    @Test
    @DisplayName("expandTo(newLength): newLength < length => don't change target object")
    public void expandTo4() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);
        Array<Integer> expected = new Array<>(actual);

        actual.expandTo(3);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("expandTo(newLength): newLength == length => don't change target object")
    public void expandTo5() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);
        Array<Integer> expected = new Array<>(actual);

        actual.expandTo(4);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("expandTo(newLength): newLength > length => change target object")
    public void expandTo6() {
        Array<Integer> actual = new Array<>(10, 20, 30, 40);
        Array<Integer> expected = new Array<>(10, 20, 30, 40, null, null, null, null, null, null, null, null);

        actual.expandTo(12);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("iterator(), Iterator#hasNext(): array is empty => return false")
    public void iterator1() {
        Array<Integer> array = new Array<>();

        Assertions.assertFalse(array.iterator().hasNext());
    }

    @Test
    @DisplayName("iterator(), Iterator#hasNext(): array contains several items => return correct result")
    public void iterator2() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Array<Boolean> actual = new Array<>();
        Array<Boolean> expected = new Array<>(true, true, true, true, true, false);

        for(Integer integer : array)  actual.add(true);
        actual.add(false);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): array is empty => exception")
    public void iterator3() {
        Array<Integer> array = new Array<>();

        Assertions.assertThrows(NoSuchElementException.class, () -> array.iterator().next());
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): array contains several item => return correct result")
    public void iterator4() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(array);

        for(Integer integer : array) actual.add(integer);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): array contains several items, next after last item => exception")
    public void iterator5() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        while(iterator.hasNext()) iterator.next();

        Assertions.assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after set() => exception")
    public void iterator6() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.set(0, 10);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after setAndExpand() => exception")
    public void iterator7() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.setAndExpand(100, 10);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after add() => exception")
    public void iterator8() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.add(10);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after addAll(T... values), values is not empty => exception")
    public void iterator9() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.addAll(0, 10);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after addAll(Array<T> values), values is not empty => exception")
    public void iterator10() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.addAll(new Array<>(10, 20, 30));

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after insert() => exception")
    public void iterator11() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.insert(1, 10);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after binaryInsert() => exception")
    public void iterator12() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.binaryInsert(55, Integer::compareTo);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after quickRemove() => exception")
    public void iterator13() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.quickRemove(1);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after orderedRemove() => exception")
    public void iterator14() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.orderedRemove(2);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after clear() => exception")
    public void iterator15() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.clear();

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after sort() => exception")
    public void iterator16() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.sort(Integer::compareTo);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after expandTo(), newLength > current length => exception")
    public void iterator17() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.expandTo(100);

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after trimToLength() => exception")
    public void iterator18() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.trimToLength();

        Assertions.assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    @DisplayName("forEach(): empty array => do not anything")
    public void forEach1() {
        Array<Integer> array = new Array<>();
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>();

        array.forEach(actual::add);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("forEach(): empty contains several item => get each item in correct order")
    public void forEach2() {
        Array<Integer> array = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(array);

        array.forEach(actual::add);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("equals(Object o): first operand don't equal second => return false")
    public void equals1() {
        Array<Integer> firstOperand = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = new Array<>(10, 20, null, 50, 40);

        Assertions.assertNotEquals(firstOperand, secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry arrays length is equal => return true")
    public void equals2() {
        Array<Integer> firstOperand = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertEquals(firstOperand, secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry array length isn't equal => return true")
    public void equals3() {
        Array<Integer> firstOperand = new Array<>(Integer.class, 1000);
        Array<Integer> secondOperand = new Array<>(10, 20, 30, 40, 50);

        for(int i = 0; i < 1000; i++) firstOperand.quickRemove(0);
        firstOperand.addAll(10, 20, 30, 40, 50);

        Assertions.assertEquals(firstOperand, secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    public void equals4() {
        Array<Integer> firstOperand = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertEquals(firstOperand, firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    public void equals5() {
        Array<Integer> firstOperand = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertEquals(firstOperand, secondOperand);
        Assertions.assertEquals(secondOperand, firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    public void equals6() {
        Array<Integer> firstOperand = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = new Array<>(10, 20, 30, 40, 50);
        Array<Integer> thirdOperand = new Array<>(10, 20, 30, 40, 50);

        Assertions.assertEquals(firstOperand, secondOperand);
        Assertions.assertEquals(secondOperand, thirdOperand);
        Assertions.assertEquals(firstOperand, thirdOperand);
    }

}