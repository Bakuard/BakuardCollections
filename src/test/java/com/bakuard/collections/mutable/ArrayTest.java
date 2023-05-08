package com.bakuard.collections.mutable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

class ArrayTest {

    @Test
    @DisplayName("Array(type): => array size equal 0")
    public void Array_empty1() {
        Array<String> emptyArray = new Array<>();

        Assertions.assertThat(emptyArray.size()).isZero();
    }

    @Test
    @DisplayName("Array(type): try get item after creating => exception")
    public void Array_empty2() {
        Array<String> emptyArray = new Array<>();

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> emptyArray.at(0));
    }

    @Test
    @DisplayName("Array(size): => all items after create is null")
    public void Array_size1() {
        Array<String> array = new Array<>(100);

        Assertions.assertThat(array).containsOnlyNulls();
    }

    @Test
    @DisplayName("Array(size): => array size equal size param")
    public void Array_size2() {
        Array<String> array = new Array<>(100);

        Assertions.assertThat(array.size()).isEqualTo(100);
    }

    @Test
    @DisplayName("Array(size): negative size => exception")
    public void Array_size3() {
        Assertions.assertThatIllegalArgumentException().
                isThrownBy(() -> new Array<>(-1));
    }

    @Test
    @DisplayName("Array(size): size == 0 => array size equal 0")
    public void Array_size4() {
        Array<String> emptyArray = new Array<>(0);

        Assertions.assertThat(emptyArray.size()).isZero();
    }

    @Test
    @DisplayName("Array(size): size == 0, try get item after creating => exception")
    public void Array_size5() {
        Array<String> emptyArray = new Array<>(0);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> emptyArray.at(0));
    }

    @Test
    @DisplayName("Array(other): not empty Array -> copy is equivalent to original")
    public void Array_copy1() {
        Array<Integer> original = Array.of(100000,220000,300000,470000,560000);
        Array<Integer> copy = new Array<>(original);

        Assertions.assertThat(copy).isEqualTo(original);
    }

    @Test
    @DisplayName("Array(other): empty Array -> copy is equivalent to original")
    public void Array_copy2() {
        Array<Integer> original = new Array<>();
        Array<Integer> copy = new Array<>(original);

        Assertions.assertThat(copy).isEqualTo(original);
    }

    @Test
    @DisplayName("Array(other): not empty Array -> copy contains the same objects")
    public void Array_copy3() {
        Array<Object> original = new Array<>();
        original.appendAll(new Object(), new Object(), new Object());
        Array<Object> copy = new Array<>(original);

        Assertions.assertThat(copy.at(0)).isSameAs(original.at(0));
        Assertions.assertThat(copy.at(1)).isSameAs(original.at(1));
        Assertions.assertThat(copy.at(2)).isSameAs(original.at(2));
    }

    @Test
    @DisplayName("Array(other): changes to the original must not affect the copy")
    public void Array_copy4() {
        Array<Integer> original = new Array<>();
        original.appendAll(1,2,3);
        Array<Integer> copy = new Array<>(original);
        Array<Integer> expected = new Array<>();
        expected.appendAll(1,2,3);

        original.appendAll(4,5,6);
        original.quickRemove(0);

        Assertions.assertThat(copy).isEqualTo(expected);
    }

    @Test
    @DisplayName("Array.of(...data): data[] is null => exception")
    public void of1() {
        Integer[] data = null;

        Assertions.assertThatNullPointerException().isThrownBy(() -> Array.of(data));
    }

    @Test
    @DisplayName("Array.of(...data): data[] is empty => create empty array")
    public void of2() {
        Array<Integer> array = Array.of(new Integer[]{});

        Assertions.assertThat(array.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Array.of(...data): data[] contains several item => create array with these items.")
    public void of3() {
        Array<Integer> array = Array.of(10, null, 30, 30);

        Assertions.assertThat(array).containsExactly(10, null, 30, 30);
    }

    @Test
    @DisplayName("Array.of(...data): change array data[] after creating Array => new object Array don't change")
    public void of4() {
        Array<Integer> expected = new Array<>();
        expected.appendAll(10, 20, 30, 40, 50, 60);

        Integer[] data = {10, 20, 30, 40, 50, 60};
        Array<Integer> actual = Array.of(data);
        data[0] = 1000;

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("at(index): array is not empty, index < -size => exception")
    public void at1() {
        Array<Integer> array = new Array<>(3);
        array.replace(0, 10);
        array.replace(1, 20);
        array.replace(2, 30);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.at(-4));
    }

    @Test
    @DisplayName("at(index): array is not empty, index == array size => exception")
    public void at2() {
        Array<Integer> array = new Array<>(3);
        array.replace(0, 10);
        array.replace(1, 20);
        array.replace(2, 30);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.at(3));
    }

    @Test
    @DisplayName("at(index): array is not empty, index > array size => exception")
    public void at3() {
        Array<Integer> array = new Array<>(3);
        array.replace(0, 10);
        array.replace(1, 20);
        array.replace(2, 30);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.at(4));
    }

    @Test
    @DisplayName("""
            at(index):
             array is empty,
             index is zero
             => exception
            """)
    public void at4() {
        Array<Integer> array = new Array<>();

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.at(0));
    }

    @Test
    @DisplayName("""
            at(index):
             array is empty,
             index is positive
             => exception
            """)
    public void at5() {
        Array<Integer> array = new Array<>();

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.at(1));
    }

    @Test
    @DisplayName("""
            at(index):
             array is empty,
             index is negative
             => exception
            """)
    public void at6() {
        Array<Integer> array = new Array<>();

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.at(-1));
    }

    @Test
    @DisplayName("""
            at(index):
             array is not empty,
             index = 0
             => return first item
            """)
    public void at7() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = array.at(0);

        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("""
            at(index):
             array is not empty,
             index = array.size() - 1
             => return last item
            """)
    public void at8() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = array.at(8);

        Assertions.assertThat(actual).isEqualTo(8);
    }

    @Test
    @DisplayName("""
            at(index):
             array is not empty,
             index for middle item,
             index is positive
             => return correct item
            """)
    public void at9() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = array.at(3);

        Assertions.assertThat(actual).isEqualTo(3);
    }

    @Test
    @DisplayName("""
            at(index):
             array is not empty,
             index = -1
             => return top
            """)
    public void at10() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = array.at(-1);

        Assertions.assertThat(actual).isEqualTo(8);
    }

    @Test
    @DisplayName("""
            at(index):
             array is not empty,
             index = -array.size()
             => return first item
            """)
    public void at11() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = array.at(-9);

        Assertions.assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("""
            at(index):
             array is not empty,
             index for middle item,
             index is negative
             => return correct item
            """)
    public void at12() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8);

        int actual = array.at(-3);

        Assertions.assertThat(actual).isEqualTo(6);
    }

    @Test
    @DisplayName("replace(index, value): index < 0 => exception")
    public void replace1() {
        Array<Integer> array = Array.of(1, 120, 12);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.replace(-1, 100));
    }

    @Test
    @DisplayName("replace(index, value): index == array size => exception")
    public void replace2() {
        Array<Integer> array = Array.of(0,0,1,12);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.replace(4, 100));
    }

    @Test
    @DisplayName("replace(index, value): index > array size => exception")
    public void replace3() {
        Array<Integer> array = Array.of(0,0,1,12);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.replace(5, 100));
    }

    @Test
    @DisplayName("replace(index, value): old value by this index is null => return null")
    public void replace4() {
        Array<Integer> array = Array.of(10, 20, null, 40);

        Integer actual = array.replace(2, 100);

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("replace(index, value): old value by this index is not null => return old value")
    public void replace5() {
        Array<Integer> array = Array.of(10, 20, null, 40);

        int actual = array.replace(1, 100);

        Assertions.assertThat(actual).isEqualTo(20);
    }

    @Test
    @DisplayName("replace(index, value): value is null => replace null by this index")
    public void replace6() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        array.replace(1, null);

        Assertions.assertThat(array.at(1)).isNull();
    }

    @Test
    @DisplayName("replace(index, value): value is not null => replace value by this index")
    public void replace7() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        array.replace(1, 1000);

        Assertions.assertThat(array.at(1)).isEqualTo(1000);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index < 0 => exception")
    public void setAndExpend1() {
        Array<Integer> array = Array.of(0, 10, 100);

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(()-> array.setWithoutBound(-1, 20));
    }

    @Test
    @DisplayName("setAndExpend(index, value): index == size => add new item")
    public void setAndExpend2() {
        Array<Integer> array = Array.of(0, 10, 25, 26);

        array.setWithoutBound(4, 100);

        Assertions.assertThat(array.at(4)).isEqualTo(100);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index == size => size == index + 1")
    public void setWithoutBound3() {
        Array<Integer> array = Array.of(0, 10, 25, 26, 67);

        array.setWithoutBound(4, 1000);

        Assertions.assertThat(array.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("setAdnExpand(index, value): index > size + 1 => add new item")
    public void setWithoutBound4() {
        Array<Integer> array = Array.of(0, 10, 25, 26, 68);

        array.setWithoutBound(10, 100);

        Assertions.assertThat(array.at(10)).isEqualTo(100);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index > size + 1 => size == index + 1")
    public void setWithoutBound5() {
        Array<Integer> array = Array.of(1, 1, 20, 90, 900);

        array.setWithoutBound(10, 1000);

        Assertions.assertThat(array.size()).isEqualTo(11);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): old value by this index is null => return null")
    public void setWithoutBound6() {
        Array<Integer> array = Array.of(0, 10, null, 45);

        Integer actual = array.setWithoutBound(2, 1000);

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("setWithoutBound(index, value): old value by this index is not null => return old value")
    public void setWithoutBound7() {
        Array<Integer> array = Array.of(0, 100, 120);

        int actual = array.setWithoutBound(2, 1000);

        Assertions.assertThat(actual).isEqualTo(120);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index == size => return null")
    public void setWithoutBound8() {
        Array<Integer> array = Array.of(0, 120, 111);

        Integer actual = array.setWithoutBound(3, 1000);

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index > size + 1 => return null")
    public void setWithoutBound9() {
        Array<Integer> array = Array.of(0, 10, 25, 26, 67);

        Integer actual = array.setWithoutBound(10, 1000);

        Assertions.assertThat(actual).isNull();
    }

    @Test
    @DisplayName("setWithoutBound(index, value): value == null => set null")
    public void setWithoutBound10() {
        Array<Integer> array = Array.of(0, 120, 340, 700);

        array.setWithoutBound(1, null);

        Assertions.assertThat(array.at(1)).isNull();
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index == size => old values are preserved")
    public void setWithoutBound11() {
        Array<Integer> array = Array.of(0, 100, 200);

        array.setWithoutBound(3, 250);

        Assertions.assertThat(array).
                elements(0, 1, 2).
                containsExactly(0, 100, 200);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index > size + 1 => old values are preserved")
    public void setWithoutBound12() {
        Array<Integer> array = Array.of(0, 100, 200);

        array.setWithoutBound(6, 250);

        Assertions.assertThat(array).
                elements(0, 1, 2).
                containsExactly(0, 100, 200);
    }

    @Test
    @DisplayName("setWithoutBound(index, value): index > size => all new added items is null")
    public void setWithoutBound13() {
        Array<Integer> array = Array.of(0, 10, 120, 300);

        array.setWithoutBound(10, 1000);

        Assertions.assertThat(array).
                elements(4, 5, 6, 7, 8, 9).
                containsOnlyNulls();
    }

    @Test
    @DisplayName("append(value): => add value")
    public void append1() {
        Array<Integer> actual = Array.of(0, 10, 120);
        Array<Integer> expected = Array.of(0, 10, 120, 1000);

        actual.append(1000);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            append(value): => increase size
            """)
    public void append2() {
        Array<Integer> actual = Array.of(0, 10, 120);

        actual.append(1000);

        Assertions.assertThat(actual.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("""
            append(value):
             add several items
            """)
    public void append3() {
        Array<Integer> actual = Array.of(0, 1, 2, 3);

        for(int i = 4; i < 1000; i++) actual.append(i);

        Assertions.assertThat(actual).
                hasSameElementsAs(IntStream.range(0, 1000).boxed().toList());
        Assertions.assertThat(actual.size()).isEqualTo(1000);
    }

    @Test
    @DisplayName("appendAll(...data): data is empty => array don't change")
    public void appendAll1() {
        Array<Integer> actual = Array.of(0, 12, 45);
        Array<Integer> expected = new Array<>(actual);

        actual.appendAll();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(...data): array is empty, add several items => add all values in same order")
    public void appendAll2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(0, 120, 200);

        actual.appendAll(0, 120, 200);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(...data): array is empty, add one item => add item")
    public void appendAll3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(1000);

        actual.appendAll(1000);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(...data): array is not empty, add several item => add values in same order")
    public void appendAll4() {
        Array<Integer> actual = Array.of(0, 120, 34);
        Array<Integer> expected = Array.of(0, 120, 34, 56, 0, 10);

        actual.appendAll(56, 0, 10);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(...data): array is not empty, add one item => add item to the end")
    public void appendAll5() {
        Array<Integer> actual = Array.of(0, 10 ,120);
        Array<Integer> expected = Array.of(0, 10, 120, 120);

        actual.appendAll(120);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(...data): data contains null => add all values in same order include null")
    public void appendAll6() {
        Array<Integer> actual = Array.of(0, 0, 10);
        Array<Integer> expected = Array.of(0, 0, 10, 100, null, 100, null, null);

        actual.appendAll(100, null, 100, null, null);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(iterable): iterable is empty => array don't change")
    public void appendAll_Iterable1() {
        Array<Integer> actual = Array.of(0, 12, 45);
        Array<Integer> expected = new Array<>(actual);

        actual.appendAll(List.of());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(iterable): array is empty, iterable is not empty => add all values in same order")
    public void appendAll_Iterable2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(0, 120, 200);

        actual.appendAll(List.of(0, 120, 200));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(iterable): array is empty, iterable contains one item => add item")
    public void appendAll_Iterable3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(1000);

        actual.appendAll(List.of(1000));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(iterable): array is not empty, iterable is not empty => add values in same order")
    public void appendAll_Iterable4() {
        Array<Integer> actual = Array.of(0, 120, 34);
        Array<Integer> expected = Array.of(0, 120, 34, 56, 0, 10);

        actual.appendAll(List.of(56, 0, 10));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(iterable): array is not empty, iterable contains one item => add item to the end")
    public void appendAll_Iterable5() {
        Array<Integer> actual = Array.of(0, 10 ,120);
        Array<Integer> expected = Array.of(0, 10, 120, 120);

        actual.appendAll(List.of(120));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("appendAll(iterable): iterable contains null => add all values in same order include null")
    public void appendAll_Iterable6() {
        Array<Integer> actual = Array.of(0, 0, 10);
        Array<Integer> expected = Array.of(0, 0, 10, 100, null, 100, null, null);

        actual.appendAll(Array.of(100, null, 100, null, null));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("concat(array): data is empty => array don't change")
    public void concat1() {
        Array<Integer> actual = Array.of(0, 10, 100);
        Array<Integer> expected = new Array<>(actual);

        actual.concat(new Array<>());

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("concat(array): array is empty, data contains several items => add all values in same order")
    public void concat2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(0, 10, 90);

        actual.concat(Array.of(0, 10, 90));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("concat(array): array is empty, data contains one item => add item")
    public void concat3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(1000);

        actual.concat(new Array<>(1000));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("concat(array): array is not empty, data contains several items => add all values in same order")
    public void concat4() {
        Array<Integer> actual = Array.of(10, 10, 20);
        Array<Integer> expected = Array.of(10, 10, 20, 30, 0, 10);

        actual.concat(Array.of(30, 0, 10));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("concat(array): array is not empty, data contains one item => add item")
    public void concat5() {
        Array<Integer> actual = Array.of(10, 10, 100);
        Array<Integer> expected = Array.of(10, 10, 100, 0);

        actual.concat(Array.of(0));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("concat(array): data contains null => add all item in same order include null")
    public void concat6() {
        Array<Integer> actual = Array.of(10, 20, 25);
        Array<Integer> expected = Array.of(10, 20, 25, null, 1000, null, null);

        actual.concat(Array.of(null, 1000, null, null));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("insert(index, value): index < 0 => exception")
    public void insert1() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> array.insert(-1, 0));
    }

    @Test
    @DisplayName("insert(index, value): index > size => exception")
    public void insert2() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> array.insert(5, 100));
    }

    @Test
    @DisplayName("insert(index, value): array is empty, index == 0 => add item")
    public void insert3() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(1000);

        actual.insert(0, 1000);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("insert(index, value): array contains one item, index == 0 => insert item, shift old item")
    public void insert4() {
        Array<Integer> actual = Array.of(1000);
        Array<Integer> expected = Array.of(0, 1000);

        actual.insert(0, 0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("insert(index, value): array contains one item, index == 1 => insert item, don't shift olf item")
    public void insert5() {
        Array<Integer> actual = Array.of(1000);
        Array<Integer> expected = Array.of(1000, 0);

        actual.insert(1, 0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("insert(index, value): array contains several items, index == 0 => insert item, shift all old items")
    public void insert6() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(0, 10, 20, 30, 40, 50);

        actual.insert(0, 0);

        Assertions.assertThat(actual).isEqualTo(expected);
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
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(10, 20, 30, 35, 40, 50);

        actual.insert(3, 35);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("insert(index, value): array contains several items, insert == size => insert item, don't shift all old items")
    public void insert8() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(10, 20, 30, 40, 50, 60);

        actual.insert(5, 60);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("insert(index, value): array contains several items, insert null => insert null")
    public void insert9() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(10, 20, null, 30, 40, 50);

        actual.insert(2, null);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("binaryInsert(value, comparator): array is empty => add value, return 0")
    public void binaryInsert1() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = Array.of(1000);

        int index = actual.binaryInsert(1000, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isZero();
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
        Array<Integer> actual = Array.of(100);
        Array<Integer> expected = Array.of(10, 100);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isZero();
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
        Array<Integer> actual = Array.of(100);
        Array<Integer> expected = Array.of(100, 110);

        int index = actual.binaryInsert(110, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isOne();
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
        Array<Integer> actual = Array.of(100);
        Array<Integer> expected = Array.of(100, 100);

        int index = actual.binaryInsert(100, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 1);
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
        Array<Integer> actual = Array.of(10, 20);
        Array<Integer> expected = Array.of(0, 10, 20);

        int index = actual.binaryInsert(0, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isZero();
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
        Array<Integer> actual = Array.of(10, 20);
        Array<Integer> expected = Array.of(10, 20, 80);

        int index = actual.binaryInsert(80, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isEqualTo(2);
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
        Array<Integer> actual = Array.of(10, 50);
        Array<Integer> expected = Array.of(10, 25, 50);

        int index = actual.binaryInsert(25, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isOne();
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
        Array<Integer> actual = Array.of(10, 20);
        Array<Integer> expected = Array.of(10, 10, 20);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 1);
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
        Array<Integer> actual = Array.of(10, 20);
        Array<Integer> expected = Array.of(10, 20, 20);

        int index = actual.binaryInsert(20, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(1, 2);
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
        Array<Integer> actual = Array.of(10, 10);
        Array<Integer> expected = Array.of(10, 10, 10);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 2);
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
        Array<Integer> actual = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = Array.of(0, 10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);

        int index = actual.binaryInsert(0, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isZero();
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value greater than last item,
             value is unique
             => add value,
                return value equal old size of array
            """)
    public void binaryInsert12() {
        Array<Integer> actual = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(110, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isEqualTo(10);
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
        Array<Integer> actual = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = Array.of(10, 20 ,30, 40, 50, 55, 60, 70, 80, 90, 100);

        int index = actual.binaryInsert(55, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isEqualTo(5);
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
        Array<Integer> actual = Array.of(10, 10 ,10, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = Array.of(10, 10, 10 ,10, 40, 50, 60, 70, 80, 90, 100);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 3);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             value equal the biggest item
             => add value,
                return value belongs to [first the biggest item index, old size]
            """)
    public void binaryInsert15() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60, 70, 100, 100, 100);
        Array<Integer> expected = Array.of(10, 20, 30, 40, 50, 60, 70, 100, 100, 100, 100);

        int index = actual.binaryInsert(100, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(7, 10);
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
        Array<Integer> actual = Array.of(10, 20, 30, 50, 50, 50, 50, 80, 90, 100);
        Array<Integer> expected = Array.of(10, 20, 30, 50, 50, 50, 50, 50, 80, 90, 100);

        int index = actual.binaryInsert(50, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(3, 7);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an even number of elements,
             all array items are equal,
             value is not unique
             => add value,
                return value belongs to [0, array old size]
            """)
    public void binaryInsert17() {
        Array<Integer> actual = Array.of(67, 67, 67, 67, 67, 67, 67, 67, 67, 67);
        Array<Integer> expected = Array.of(67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67);

        int index = actual.binaryInsert(67, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 10);
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
        Array<Integer> actual = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = Array.of(0, 10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(0, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isZero();
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value greater than last item,
             value is unique
             => add value,
                return value equal old size of array
            """)
    public void binaryInsert19() {
        Array<Integer> actual = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110, 120);

        int index = actual.binaryInsert(120, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isEqualTo(11);
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
        Array<Integer> actual = Array.of(10, 20 ,30, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = Array.of(10, 20 ,30, 40, 50, 55, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(55, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isEqualTo(5);
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
        Array<Integer> actual = Array.of(10, 10 ,10, 40, 50, 60, 70, 80, 90, 100, 110);
        Array<Integer> expected = Array.of(10, 10, 10 ,10, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binaryInsert(10, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 3);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             value equal the biggest item
             => add value,
                return value belongs to [first the biggest item index, old size]
            """)
    public void binaryInsert22() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);
        Array<Integer> expected = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110, 110);

        int index = actual.binaryInsert(110, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(8, 11);
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
        Array<Integer> actual = Array.of(10, 20, 30, 50, 50, 50, 50, 80, 90, 100, 110);
        Array<Integer> expected = Array.of(10, 20, 30, 50, 50, 50, 50, 50, 80, 90, 100, 110);

        int index = actual.binaryInsert(50, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(3, 7);
    }

    @Test
    @DisplayName("""
            binaryInsert(value, comparator):
             array contains more than two items,
             array contains an odd number of elements,
             all array items are equal,
             value is not unique
             => add value,
                return value belongs to [0, array old size]
            """)
    public void binaryInsert24() {
        Array<Integer> actual = Array.of(67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67);
        Array<Integer> expected = Array.of(67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67, 67);

        int index = actual.binaryInsert(67, Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(index).isBetween(0, 11);
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             firstIndex < 0
             => exception
            """)
    public void swap1() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.swap(-1, 0));
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             secondIndex < 0
             => exception
            """)
    public void swap2() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.swap(0, -1));
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             firstIndex == array.size
             => exception
            """)
    public void swap3() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.swap(10, 0));
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             secondIndex == array.size
             => exception
            """)
    public void swap4() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.swap(0, 10));
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             firstIndex > array.size
             => exception
            """)
    public void swap5() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.swap(11, 0));
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             secondIndex > array.size
             => exception
            """)
    public void swap6() {
        Array<Integer> array = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.swap(0, 11));
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             firstIndex == secondIndex
             => don't change array
            """)
    public void swap7() {
        Array<Integer> expected = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Array<Integer> actual = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        actual.swap(3, 3);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            swap(firstIndex, secondIndex):
             firstIndex != secondIndex
             => swap items
            """)
    public void swap8() {
        Array<Integer> expected = Array.of(0, 1, 2, 9, 4, 5, 6, 7, 8, 3);

        Array<Integer> actual = Array.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        actual.swap(3, 9);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("quickRemove(index): index < 0 => exception")
    public void quickRemove1() {
        Array<Integer> array = Array.of(0, 10, 20);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.quickRemove(-1));
    }

    @Test
    @DisplayName("quickRemove(index): index == array.size() => exception")
    public void quickRemove2() {
        Array<Integer> array = Array.of(0, 10, 20);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.quickRemove(3));
    }

    @Test
    @DisplayName("quickRemove(index): index > array.size() => exception")
    public void quickRemove3() {
        Array<Integer> array = Array.of(10, 20, 30);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.quickRemove(4));
    }

    @Test
    @DisplayName("quickRemove(index): index == 0 => delete item")
    public void quickRemove4() {
        Array<Integer> actual = Array.of(0, 10, 20, 30);
        Array<Integer> expected = Array.of(30, 10, 20);

        actual.quickRemove(0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("quickRemove(index): index in the middle => delete item")
    public void quickRemove5() {
        Array<Integer> actual = Array.of(10, 20, 30, 40);
        Array<Integer> expected = Array.of(10, 40, 30);

        actual.quickRemove(1);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("quickRemove(index): index of last item => delete item")
    public void quickRemove6() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(10, 20, 30, 40);

        actual.quickRemove(4);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("quickRemove(index): array contains one item, index == 0 => empty array")
    public void quickRemove7() {
        Array<Integer> expected = new Array<>();

        Array<Integer> actual = new Array<>(1);
        actual.quickRemove(0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("quickRemove(index): index of last item => set null for the last item")
    public void quickRemove8() {
        Array<Integer> array = Array.of(10, 20, 30);

        array.quickRemove(2);
        array.expandTo(5);

        Assertions.assertThat(array).
                elements(2, 3, 4).
                containsOnlyNulls();
    }

    @Test
    @DisplayName("quickRemove(index): index in the middle => set last item to this index")
    public void quickRemove9() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        array.quickRemove(1);

        Assertions.assertThat(array.at(1)).isEqualTo(40);
    }

    @Test
    @DisplayName("quickRemove(index): index is the middle => return removed value")
    public void quickRemove10() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        int actual = array.quickRemove(2);

        Assertions.assertThat(actual).isEqualTo(30);
    }

    @Test
    @DisplayName("quickRemove(index): index of last item => return removed value")
    public void quickRemove11() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);

        int actual = array.quickRemove(4);

        Assertions.assertThat(actual).isEqualTo(50);
    }

    @Test
    @DisplayName("quickRemove(index): remove all items => empty array")
    public void quickRemove12() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60);
        Array<Integer> expected = new Array<>();

        while(actual.size() != 0) actual.quickRemove(0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("orderedRemove(index): index < 0 => exception")
    public void orderedRemove1() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.orderedRemove(-1));
    }

    @Test
    @DisplayName("orderedRemove(index): index == array size => exception")
    public void orderedRemove2() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.orderedRemove(5));
    }

    @Test
    @DisplayName("orderedRemove(index): index > array size => exception")
    public void orderedRemove3() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);

        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> array.orderedRemove(6));
    }

    @Test
    @DisplayName("orderedRemove(index): index == 0 => delete item")
    public void orderedRemove4() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(20, 30, 40, 50);

        actual.orderedRemove(0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("orderedRemove(index): index in the middle => delete item")
    public void orderedRemove5() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(10, 20, 40, 50);

        actual.orderedRemove(2);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("orderedRemove(index): index of last item => delete item")
    public void orderedRemove6() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50);
        Array<Integer> expected = Array.of(10, 20, 30, 40);

        actual.orderedRemove(4);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("orderedRemove(index): array contains one item, index == 0 => empty array")
    public void orderedRemove7() {
        Array<Integer> actual = new Array<>(1);
        Array<Integer> expected = new Array<>();

        actual.orderedRemove(0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("orderedRemove(index): index of last item => set null for the last item")
    public void orderedRemove8() {
        Array<Integer> array = Array.of(10, 20, 30);

        array.orderedRemove(2);
        array.expandTo(5);

        Assertions.assertThat(array).
                elements(2, 3, 4).
                containsOnlyNulls();
    }

    @Test
    @DisplayName("orderedRemove(index): index in the middle => return removed value")
    public void orderedRemove9() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60);

        int actual = array.orderedRemove(3);

        Assertions.assertThat(actual).isEqualTo(40);
    }

    @Test
    @DisplayName("orderedRemove(index): index of last item => return removed value")
    public void orderedRemove10() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60);

        int actual = array.orderedRemove(5);

        Assertions.assertThat(actual).isEqualTo(60);
    }

    @Test
    @DisplayName("orderedRemove(index): remove all item => empty array")
    public void orderedRemove11() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60);
        Array<Integer> expected = new Array<>();

        while(actual.size() != 0) actual.orderedRemove(0);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("clear(): array contains items => empty array")
    public void clear1() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>();

        actual.clear();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("clear(): array is empty => do nothing")
    public void clear2() {
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>();

        actual.clear();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("clear(): array contains items => set null for each item")
    public void clear3() {
        Array<Integer> array = Array.of(10,20,30,40,50,60,70,80,90,100);

        array.clear();
        array.expandTo(10);

        Assertions.assertThat(array).containsOnlyNulls();
    }

    @Test
    @DisplayName("sort(comparator): array is empty => do nothing")
    public void sort1() {
        Array<Integer> array = new Array<>();
        Array<Integer> expected = new Array<>();

        array.sort(Integer::compareTo);

        Assertions.assertThat(array).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains one item => do nothing")
    public void sort2() {
        Array<Integer> array = Array.of(512);
        Array<Integer> expected = Array.of(512);

        array.sort(Integer::compareTo);

        Assertions.assertThat(array).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains two different items, order equal comparator order => do nothing")
    public void sort3() {
        Array<Integer> actual = Array.of(10, 20);
        Array<Integer> expected = Array.of(10, 20);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains two different items, order don't equal comparator order => sort")
    public void sort4() {
        Array<Integer> actual = Array.of(20, 10);
        Array<Integer> expected = Array.of(10, 20);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains two equal items => do nothing")
    public void sort5() {
        Array<Integer> actual = Array.of(100, 100);
        Array<Integer> expected = Array.of(100, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains several different items, order equal comparator order => do nothing")
    public void sort6() {
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
        Array<Integer> expected = new Array<>(actual);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains several different items, order is inverse to comparator order => sort")
    public void sort7() {
        Array<Integer> actual = Array.of(100, 90, 80, 70, 60, 50, 40, 30, 20, 10);
        Array<Integer> expected = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains several items, all items is equal => do nothing")
    public void sort8() {
        Array<Integer> actual = Array.of(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100);
        Array<Integer> expected = new Array<>(actual);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains several item, all items is unique, order is random => sort")
    public void sort9() {
        Array<Integer> actual = Array.of(100, 10, 20, 30, 50, 40, 90, 80, 60, 70);
        Array<Integer> expected = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("sort(comparator): array contains several item, there are equal items, order is random => sort")
    public void sort10() {
        Array<Integer> actual = Array.of(10, 20, 30, 100, 100, 100, 90, 100, 80, 70, 60, 70, 50, 40);
        Array<Integer> expected = Array.of(10, 20, 30, 40, 50, 60, 70, 70, 80, 90, 100, 100, 100, 100);

        actual.sort(Integer::compareTo);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("linearSearch(T value): array empty => return -1")
    public void linearSearch1() {
        Array<Integer> array = new Array<>();

        Integer actual = array.linearSearch(100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("linearSearch(T value): array contains null item => return index first null value")
    public void linearSearch2() {
        Array<Integer> array = Array.of(10, 20, null, null, 30, null);

        int actual = array.linearSearch((Integer)null);

        Assertions.assertThat(actual).isEqualTo(2);
    }

    @Test
    @DisplayName("linearSearch(T value): array contains several equal items => return index first item equal value")
    public void linearSearch3() {
        Array<Integer> array = Array.of(20, 10, 0, 40, null, null, 50, 50, 40);

        int actual = array.linearSearch(40);

        Assertions.assertThat(actual).isEqualTo(3);
    }

    @Test
    @DisplayName("linearSearch(T value): array is not empty, array don't contain item => return -1")
    public void linearSearch4() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 90);

        int actual = array.linearSearch(45);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("linearSearch(predicate): array empty => return -1")
    public void linearSearchByPredicate1() {
        Array<Integer> array = new Array<>();

        int actual = array.linearSearch(item -> item == 100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("linearSearch(predicate): array contains several items for predicate => return index first item by predicate")
    public void linearSearchByPredicate2() {
        Array<Integer> array = Array.of(10, 20, null, null, 30, null, 40, null, 50);

        int actual = array.linearSearch(item -> item != null && item > 20);

        Assertions.assertThat(actual).isEqualTo(4);
    }

    @Test
    @DisplayName("linearSearch(predicate): array is not empty, array don't contain item for predicate => return -1")
    public void linearSearchByPredicate3() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 90);

        int actual = array.linearSearch(item -> item == 45);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("binarySearch(comparator): array is empty => return -1")
    public void binarySearch1() {
        Array<Integer> array = new Array<>();

        int actual = array.binarySearch(i -> -i.compareTo(100));

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains one item, item equal value => return 0")
    public void binarySearch2() {
        Array<Integer> array = Array.of(10);

        int actual = array.binarySearch(i -> -i.compareTo(10));

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains one item, value less than the smallest item => return -1")
    public void binarySearch3() {
        Array<Integer> array = Array.of(1);

        int actual = array.binarySearch(i -> -i.compareTo(0));

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains one item, value greater than the biggest item => return -1")
    public void binarySearch4() {
        Array<Integer> array = Array.of(10);

        int actual = array.binarySearch(i -> -i.compareTo(20));

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains two items, value less than the smallest item => return -1")
    public void binarySearch5() {
        Array<Integer> array = Array.of(10, 20);

        int actual = array.binarySearch(i -> -i.compareTo(0));

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("binarySearch(comparator): array contains two items, value greater than the biggest item => return -1")
    public void binarySearch6() {
        Array<Integer> array = Array.of(10, 20);

        int actual = array.binarySearch(i -> -i.compareTo(30));

        Assertions.assertThat(actual).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20);

        int actual = array.binarySearch(i -> -i.compareTo(15));

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains two items,
             value equal the smallest item
             => return 0
            """)
    public void binarySearch8() {
        Array<Integer> array = Array.of(10, 20);

        int actual = array.binarySearch(i -> -i.compareTo(10));

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("""
            binarySearch(comparator):
             array contains two items,
             value equal the biggest item
             => return 1
            """)
    public void binarySearch9() {
        Array<Integer> array = Array.of(10, 20);

        int actual = array.binarySearch(i -> -i.compareTo(20));

        Assertions.assertThat(actual).isEqualTo(1);
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
        Array<Integer> actual = Array.of(10, 10);

        int index = actual.binarySearch(i -> -i.compareTo(10));

        Assertions.assertThat(index).isBetween(0, 1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        int actual = array.binarySearch(i -> -i.compareTo(0));

        Assertions.assertThat(actual).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        int actual = array.binarySearch(i -> -i.compareTo(110));

        Assertions.assertThat(actual).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        int actual = array.binarySearch(i -> -i.compareTo(55));

        Assertions.assertThat(actual).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        int actual = array.binarySearch(i -> -i.compareTo(10));

        Assertions.assertThat(actual).isZero();
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        int actual = array.binarySearch(i -> -i.compareTo(50));

        Assertions.assertThat(actual).isEqualTo(4);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);

        int actual = array.binarySearch(i -> -i.compareTo(100));

        Assertions.assertThat(actual).isEqualTo(9);
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
        Array<Integer> actual = Array.of(20, 20, 20, 40, 50, 60, 70, 80, 90, 100);

        int index = actual.binarySearch(i -> -i.compareTo(20));

        Assertions.assertThat(index).isBetween(0, 2);
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
        Array<Integer> array = Array.of(10, 20, 30, 50, 50, 50, 70, 80, 90, 100);

        int index = array.binarySearch(i -> -i.compareTo(50));
        
        Assertions.assertThat(index).isBetween(3, 5);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 100, 100, 100);

        int index = array.binarySearch(i -> -i.compareTo(100));

        Assertions.assertThat(index).isBetween(7, 9);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = array.binarySearch(i -> -i.compareTo(0));
        
        Assertions.assertThat(index).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        int actual = array.binarySearch(i -> -i.compareTo(120));
        
        Assertions.assertThat(actual).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        int actual = array.binarySearch(i -> -i.compareTo(55));
        
        Assertions.assertThat(actual).isEqualTo(-1);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        int actual = array.binarySearch(i -> -i.compareTo(10));
        
        Assertions.assertThat(actual).isZero();
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        int actual = array.binarySearch(i -> -i.compareTo(50));
        
        Assertions.assertThat(actual).isEqualTo(4);
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
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110);

        int actual = array.binarySearch(i -> -i.compareTo(110));
        
        Assertions.assertThat(actual).isEqualTo(10);
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
        Array<Integer> actual = Array.of(20, 20, 20, 40, 50, 60, 70, 80, 90, 100, 110);

        int index = actual.binarySearch(i -> -i.compareTo(20));
        
        Assertions.assertThat(index).isBetween(0, 2);
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
        Array<Integer> actual = Array.of(10, 20, 30, 50, 50, 50, 70, 80, 90, 100, 110);

        int index = actual.binarySearch(i -> -i.compareTo(50));

        Assertions.assertThat(index).isBetween(3, 5);
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
        Array<Integer> actual = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);

        int index = actual.binarySearch(i -> -i.compareTo(110));

        Assertions.assertThat(index).isBetween(8, 10);
    }

    @Test
    @DisplayName("""
            frequency(predicate):
             array not contains items for predicate
             => return 0
            """)
    public void frequency1() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);

        int actual = array.frequency(i -> i < 0);

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("""
            frequency(predicate):
             array is empty
             => return 0
            """)
    public void frequency2() {
        Array<Integer> array = new Array<>();

        int actual = array.frequency(i -> i > 0);

        Assertions.assertThat(actual).isZero();
    }

    @Test
    @DisplayName("""
            frequency(predicate):
             array contains items for predicate
             => return correct result
            """)
    public void frequency3() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50, 60, 70, 80, 110, 110, 110);

        int actual = array.frequency(i -> i % 20 == 0);

        Assertions.assertThat(actual).isEqualTo(4);
    }

    @Test
    @DisplayName("expandTo(newSize): newSize < size => return false")
    public void expandTo1() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        boolean actual = array.expandTo(3);
        
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("expandTo(newSize): newSize == size => return false")
    public void expandTo2() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        boolean actual = array.expandTo(4);
        
        Assertions.assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("expandTo(newSize): newSize > size => return true")
    public void expandTo3() {
        Array<Integer> array = Array.of(10, 20, 30, 40);

        boolean actual = array.expandTo(5);
        
        Assertions.assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("expandTo(newSize): newSize < size => don't change target object")
    public void expandTo4() {
        Array<Integer> actual = Array.of(10, 20, 30, 40);
        Array<Integer> expected = new Array<>(actual);

        actual.expandTo(3);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("expandTo(newSize): newSize == size => don't change target object")
    public void expandTo5() {
        Array<Integer> actual = Array.of(10, 20, 30, 40);
        Array<Integer> expected = new Array<>(actual);

        actual.expandTo(4);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("expandTo(newSize): newSize > size => change target object")
    public void expandTo6() {
        Array<Integer> actual = Array.of(10, 20, 30, 40);
        Array<Integer> expected = Array.of(10, 20, 30, 40, null, null, null, null, null, null, null, null);

        actual.expandTo(12);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("iterator(), Iterator#hasNext(): array is empty => return false")
    public void iterator1() {
        Array<Integer> array = new Array<>();

        Assertions.assertThat(array.iterator().hasNext()).isFalse();
    }

    @Test
    @DisplayName("iterator(), Iterator#hasNext(): array contains several items => return correct result")
    public void iterator2() {
        Array<Integer> array = Array.of(1, 2, 3, 4, 5);

        Iterator<Integer> iterator = array.iterator();
        Array<Boolean> actual = new Array<>();
        for(int i = 0; i < 10; i++) {
            actual.append(iterator.hasNext());
            if(iterator.hasNext()) iterator.next();
        }

        Assertions.assertThat(actual).
                containsExactly(true, true, true, true, true, false, false, false, false, false);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): array is empty => exception")
    public void iterator3() {
        Array<Integer> array = new Array<>();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(() -> array.iterator().next());
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): array contains several item => return all items in correct order")
    public void iterator4() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(array);

        for(Integer integer : array) actual.append(integer);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): array contains several items, next after last item => exception")
    public void iterator5() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        while(iterator.hasNext()) iterator.next();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after set() => exception")
    public void iterator6() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.replace(0, 10);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after setWithoutBound() => exception")
    public void iterator7() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.setWithoutBound(100, 10);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after add() => exception")
    public void iterator8() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.append(10);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after addAll(T... values), values is not empty => exception")
    public void iterator9() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.appendAll(0, 10);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after addAll(Array<T> values), values is not empty => exception")
    public void iterator10() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.concat(Array.of(10, 20, 30));

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after insert() => exception")
    public void iterator11() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.insert(1, 10);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after binaryInsert() => exception")
    public void iterator12() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.binaryInsert(55, Integer::compareTo);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after quickRemove() => exception")
    public void iterator13() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.quickRemove(1);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after orderedRemove() => exception")
    public void iterator14() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.orderedRemove(2);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after clear() => exception")
    public void iterator15() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.clear();

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after sort() => exception")
    public void iterator16() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.sort(Integer::compareTo);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after expandTo(), newSize > current size => exception")
    public void iterator17() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.expandTo(100);

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("iterator(), Iterator#next(): next() after trimToLength() => exception")
    public void iterator18() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Iterator<Integer> iterator = array.iterator();

        array.trimToLength();

        Assertions.assertThatExceptionOfType(ConcurrentModificationException.class).
                isThrownBy(iterator::next);
    }

    @Test
    @DisplayName("forEach(): empty array => do not anything")
    public void forEach1() {
        Array<Integer> array = new Array<>();
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>();

        array.forEach(actual::append);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("forEach(): array contains several item => get each item in correct order")
    public void forEach2() {
        Array<Integer> array = Array.of(10, 20, 30, 40, 50);
        Array<Integer> actual = new Array<>();
        Array<Integer> expected = new Array<>(array);

        array.forEach(actual::append);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("equals(Object o): first operand don't equal second => return false")
    public void equals1() {
        Array<Integer> firstOperand = Array.of(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = Array.of(10, 20, null, 50, 40);

        Assertions.assertThat(firstOperand).isNotEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry arrays size is equal => return true")
    public void equals2() {
        Array<Integer> firstOperand = Array.of(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = Array.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): first operand equal second, entry array size isn't equal => return true")
    public void equals3() {
        Array<Integer> firstOperand = new Array<>(1000);
        Array<Integer> secondOperand = Array.of(10, 20, 30, 40, 50);

        for(int i = 0; i < 1000; i++) firstOperand.quickRemove(0);
        firstOperand.appendAll(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    public void equals4() {
        Array<Integer> firstOperand = Array.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    public void equals5() {
        Array<Integer> firstOperand = Array.of(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = Array.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
        Assertions.assertThat(secondOperand).isEqualTo(firstOperand);
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    public void equals6() {
        Array<Integer> firstOperand = Array.of(10, 20, 30, 40, 50);
        Array<Integer> secondOperand = Array.of(10, 20, 30, 40, 50);
        Array<Integer> thirdOperand = Array.of(10, 20, 30, 40, 50);

        Assertions.assertThat(firstOperand).isEqualTo(secondOperand);
        Assertions.assertThat(secondOperand).isEqualTo(thirdOperand);
        Assertions.assertThat(firstOperand).isEqualTo(thirdOperand);
    }

}