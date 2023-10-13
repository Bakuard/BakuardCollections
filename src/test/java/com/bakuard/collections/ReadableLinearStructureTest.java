package com.bakuard.collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

class ReadableLinearStructureTest {

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            get(index):
             linearStructure is not empty,
             index < 0
             => exception
            """)
    void get1(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.get(-1));
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            get(index):
             linearStructure is not empty,
             index = linearStructure.size()
             => exception
            """)
    void get2(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.get(10));
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            get(index):
             linearStructure is not empty,
             index > linearStructure.size()
             => exception
            """)
    void get3(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.get(11));
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            get(index):
             linearStructure is empty,
             index = 0
             => exception
            """)
    void get4(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.get(0));
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            get(index):
             linearStructure is not empty,
             index = 0
             => return first item
            """)
    void get5(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.get(0);

        Assertions.assertThat(actual).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            get(index):
             linearStructure is not empty,
             index = linearStructure.size() - 1
             => return last item
            """)
    void get6(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.get(9);

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            get(index):
             linearStructure is not empty,
             index in the middle of stack
             => return correct item
            """)
    void get7(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.get(5);

        Assertions.assertThat(actual).isEqualTo(6);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructureWithSingleItem")
    @DisplayName("""
            get(index):
             linearStructure contains single item,
             index = 0
             => return this item
            """)
    void get8(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.get(0);

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index = linearStructure.size()
             => exception
            """)
    void at1(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.at(10));
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index > linearStructure.size()
             => exception
            """)
    void at2(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.at(11));
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index < -linearStructure.size()
             => exception
            """)
    void at3(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.at(-11));
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            at(index):
             linearStructure is empty,
             index = 0
             => exception
            """)
    void at4(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.at(0));
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            at(index):
             linearStructure is empty,
             index < 0
             => exception
            """)
    void at5(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.at(-1));
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            at(index):
             linearStructure is empty,
             index > 0
             => exception
            """)
    void at6(ReadableLinearStructure<Integer> linearStructure) {
        Assertions.assertThatIndexOutOfBoundsException().
                isThrownBy(() -> linearStructure.at(1));
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index = 0
             => return first item
            """)
    void at7(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(0);

        Assertions.assertThat(actual).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index = linearStructure.size() - 1
             => return last item
            """)
    void at8(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(9);

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index for middle item,
             index is positive
             => return correct item
            """)
    void at9(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(5);

        Assertions.assertThat(actual).isEqualTo(6);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index = -1
             => return last item
            """)
    void at10(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(-1);

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index = -linearStructure.size()
             => return first item
            """)
    void at11(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(-10);

        Assertions.assertThat(actual).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            at(index):
             linearStructure is not empty,
             index for middle item,
             index is negative
             => return correct item
            """)
    void at12(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(-5);

        Assertions.assertThat(actual).isEqualTo(6);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructureWithSingleItem")
    @DisplayName("""
            at(index):
             linearStructure contains single item,
             index = 0
             => return this item
            """)
    void at13(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(0);

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructureWithSingleItem")
    @DisplayName("""
            at(index):
             linearStructure contains single item,
             index = -1
             => return this item
            """)
    void at14(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.at(-1);

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            getFirst():
             linearStructure is empty
             => return null
            """)
    void getFirst1(ReadableLinearStructure<Integer> linearStructure) {
        Integer actual = linearStructure.getFirst();

        Assertions.assertThat(actual).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            getFirst():
             linearStructure is not empty
             => return first
            """)
    void getFirst2(ReadableLinearStructure<Integer> linearStructure) {
        Integer actual = linearStructure.getFirst();

        Assertions.assertThat(actual).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            getFirst():
             linearStructure is not empty,
             first item is null
             => return null
            """)
    void getFirst3(ReadableLinearStructure<Integer> linearStructure) {
        Integer actual = linearStructure.getFirst();

        Assertions.assertThat(actual).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructureWithSingleItem")
    @DisplayName("""
            getFirst():
             linearStructure contains single item
             => return this item
            """)
    void getFirst4(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.getFirst();

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            getLast():
             linearStructure is empty
             => return null
            """)
    void getLast1(ReadableLinearStructure<Integer> linearStructure) {
        Integer actual = linearStructure.getLast();

        Assertions.assertThat(actual).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            getLast():
             linearStructure is not empty
             => return last
            """)
    void getLast2(ReadableLinearStructure<Integer> linearStructure) {
        Integer actual = linearStructure.getLast();

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            getLast():
             linearStructure is not empty,
             last item is null
             => return null
            """)
    void getLast3(ReadableLinearStructure<Integer> linearStructure) {
        Integer actual = linearStructure.getLast();

        Assertions.assertThat(actual).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructureWithSingleItem")
    @DisplayName("""
            getLast():
             linearStructure contains single item
             => return this item
            """)
    void getLast4(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.getLast();

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            size():
             linearStructure is empty
             => return 0
            """)
    void size1(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.size();

        Assertions.assertThat(actual).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            size():
             linearStructure not is empty
             => return correct result
            """)
    void size2(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.size();

        Assertions.assertThat(actual).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsAndAddOperation")
    @DisplayName("""
            size():
             linearStructure not is empty,
             add several items
             => return correct result after items were added
            """)
    void size3(ReadableLinearStructure<Integer> linearStructure,
               Consumer<Integer> addItem) {
        for(int i = 0; i < 100; i++) addItem.accept(i);

        int actual = linearStructure.size();

        Assertions.assertThat(actual).isEqualTo(110);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsAndRemoveOperation")
    @DisplayName("""
            size():
             linearStructure not is empty,
             remove several items
             => return correct result after items were removed
            """)
    void size4(ReadableLinearStructure<Integer> linearStructure,
               Runnable removeItem) {
        for(int i = 0; i < 10; i++) removeItem.run();

        int actual = linearStructure.size();

        Assertions.assertThat(actual).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            isEmpty():
             linearStructure is empty
             => return true
            """)
    void isEmpty1(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.isEmpty();

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            isEmpty():
             linearStructure is not empty
             => return false
            """)
    void isEmpty2(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.isEmpty();

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructuresWithAddOperation")
    @DisplayName("""
            isEmpty():
             linearStructure is empty,
             add several items
             => return false after items were added
            """)
    void isEmpty3(ReadableLinearStructure<Integer> linearStructure,
                  Consumer<Integer> addItem) {
        for(int i = 0; i < 100; i++) addItem.accept(i);

        boolean actual = linearStructure.isEmpty();

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsAndRemoveOperation")
    @DisplayName("""
            isEmpty():
             linearStructure is not empty,
             remove several items
             => return true after items were removed
            """)
    void isEmpty4(ReadableLinearStructure<Integer> linearStructure,
                  Runnable removeItem) {
        for(int i = 0; i < 10; i++) removeItem.run();

        boolean actual = linearStructure.isEmpty();

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inBound(index):
             linearStructure is not empty,
             index < 0
             => return false
            """)
    void inBound1(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(-1);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inBound(index):
             linearStructure is not empty,
             index = linearStructures.size()
             => return false
            """)
    void inBound2(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(10);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inBound(index):
             linearStructure is not empty,
             index > linearStructures.size()
             => return false
            """)
    void inBound3(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(11);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inBound(index):
             linearStructure is not empty,
             index = 0
             => return true
            """)
    void inBound4(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(0);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inBound(index):
             linearStructure is not empty,
             index = linearStructure.size() - 1
             => return true
            """)
    void inBound5(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(9);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inBound(index):
             linearStructure is not empty,
             index in the middle
             => return true
            """)
    void inBound6(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(4);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            inBound(index):
             linearStructure is empty,
             index = 0
             => return false
            """)
    void inBound7(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inBound(0);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is not empty,
             index = 0
             => return false
            """)
    void inNegativeBound1(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(0);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is not empty,
             index > 0
             => return false
            """)
    void inNegativeBound2(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(1);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is not empty,
             index < -linearStructures.size()
             => return false
            """)
    void inNegativeBound3(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(-11);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is not empty,
             index = -1
             => return true
            """)
    void inNegativeBound4(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(-1);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is not empty,
             index = -10
             => return true
            """)
    void inNegativeBound5(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(-10);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is not empty,
             index between -1 and -linearStructure.size()
             => return true
            """)
    void inNegativeBound6(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(-5);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is empty,
             index = -1
             => return false
            """)
    void inNegativeBound7(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(-1);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            inNegativeBound(index):
             linearStructure is empty,
             index = 0
             => return false
            """)
    void inNegativeBound8(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.inNegativeBound(0);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            linearSearch(value):
             linearStructure is empty
             => return -1
            """)
    void linearSearch1(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            linearSearch(value):
             linearStructure is not empty,
             linearStructure doesn't contain item with such value
             => return -1
            """)
    void linearSearch2(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(100);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            linearSearch(value):
             linearStructure is not empty,
             linearStructure contains item with such value
             => return correct index
            """)
    void linearSearch3(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(3);

        Assertions.assertThat(actual).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            linearSearch(value):
             linearStructure is not empty,
             linearStructure contains item with such value,
             value is null
             => return correct index
            """)
    void linearSearch4(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch((Integer)null);

        Assertions.assertThat(actual).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            linearSearch(predicate):
             linearStructure is empty
             => return -1
            """)
    void linearSearchWithPredicate1(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(value -> 100 == value);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            linearSearch(predicate):
             linearStructure is not empty,
             linearStructure doesn't contain item with such value
             => return -1
            """)
    void linearSearchWithPredicate2(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(value -> 100 == value);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            linearSearch(predicate):
             linearStructure is not empty,
             linearStructure contains item with such value
             => return correct index
            """)
    void linearSearchWithPredicate3(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(value -> 3 == value);

        Assertions.assertThat(actual).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            linearSearch(predicate):
             linearStructure is not empty,
             linearStructure contains item with such value,
             value is null
             => return correct index
            """)
    void linearSearchWithPredicate4(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearch(Objects::isNull);

        Assertions.assertThat(actual).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            linearSearchLast(predicate):
             linearStructure is empty
             => return -1
            """)
    void linearSearchLast1(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearchLast(value -> 100 == value);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            linearSearchLast(predicate):
             linearStructure is not empty,
             linearStructure doesn't contain item with such value
             => return -1
            """)
    void linearSearchLast2(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearchLast(value -> 100 == value);

        Assertions.assertThat(actual).isEqualTo(-1);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            linearSearchLast(predicate):
             linearStructure is not empty,
             linearStructure contains item with such value
             => return correct index
            """)
    void linearSearchLast3(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearchLast(value -> 4 == value);

        Assertions.assertThat(actual).isEqualTo(6);
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            linearSearchLast(predicate):
             linearStructure is not empty,
             linearStructure contains item with such value,
             value is null
             => return correct index
            """)
    void linearSearchLast4(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.linearSearchLast(Objects::isNull);

        Assertions.assertThat(actual).isEqualTo(9);
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            contains(value):
             linearStructure is empty
             => return false
            """)
    void contains1(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(100);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            contains(value):
             linearStructure is not empty,
             linearStructure doesn't contain item with such value
             => return false
            """)
    void contains2(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(100);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            contains(value):
             linearStructure is not empty,
             linearStructure contains item with such value
             => return true
            """)
    void contains3(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(5);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            contains(value):
             linearStructure is not empty,
             linearStructure contains item with such value,
             value is null
             => return true
            """)
    void contains4(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains((Integer)null);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            contains(predicate):
             linearStructure is empty
             => return false
            """)
    void containsWithPredicate1(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(value -> value == 100);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            contains(predicate):
             linearStructure is not empty,
             linearStructure doesn't contain item with such value
             => return false
            """)
    void containsWithPredicate2(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(value -> value == 100);

        Assertions.assertThat(actual).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10NotUniqueItems")
    @DisplayName("""
            contains(predicate):
             linearStructure is not empty,
             linearStructure contains item with such value
             => return true
            """)
    void containsWithPredicate3(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(value -> value == 5);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10ItemsIncludeNull")
    @DisplayName("""
            contains(predicate):
             linearStructure is not empty,
             linearStructure contains item with such value,
             value is null
             => return true
            """)
    void containsWithPredicate4(ReadableLinearStructure<Integer> linearStructure) {
        boolean actual = linearStructure.contains(Objects::isNull);

        Assertions.assertThat(actual).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideEmptyLinearStructures")
    @DisplayName("""
            frequency(predicate):
             linearStructure is empty
             => return 0
            """)
    void frequency1(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.frequency(value -> value == 100);

        Assertions.assertThat(actual).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            frequency(predicate):
             linearStructure is not empty,
             linearStructure doesn't contain items matching predicate
             => return 0
            """)
    void frequency2(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.frequency(value -> value == 100);

        Assertions.assertThat(actual).isZero();
    }

    @ParameterizedTest
    @MethodSource("provideLinearStructuresWith10Items")
    @DisplayName("""
            frequency(predicate):
             linearStructure is not empty,
             linearStructure contains items matching predicate
             => return correct result
            """)
    void frequency3(ReadableLinearStructure<Integer> linearStructure) {
        int actual = linearStructure.frequency(value -> value % 2 == 0);

        Assertions.assertThat(actual).isEqualTo(5);
    }


    private static Stream<Arguments> provideEmptyLinearStructures() {
        return Stream.of(
                Arguments.of(new Array<>()),
                Arguments.of(new Stack<>()),
                Arguments.of(new Queue<>())
        );
    }

    private static Stream<Arguments> provideEmptyLinearStructuresWithAddOperation() {
        Array<Integer> array = new Array<>();
        Stack<Integer> stack = new Stack<>();
        Queue<Integer> queue = new Queue<>();
        return Stream.of(
                Arguments.of(array, (Consumer<Integer>) array::append),
                Arguments.of(stack, (Consumer<Integer>) stack::putLast),
                Arguments.of(queue, (Consumer<Integer>) queue::putLast)
        );
    }

    private static Stream<Arguments> provideLinearStructureWithSingleItem() {
        return Stream.of(
                Arguments.of(Array.of(10)),
                Arguments.of(Stack.of(10)),
                Arguments.of(Queue.of(10))
        );
    }

    private static Stream<Arguments> provideLinearStructuresWith10Items() {
        return Stream.of(
                Arguments.of(Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of(Stack.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                Arguments.of(Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        );
    }

    private static Stream<Arguments> provideLinearStructuresWith10NotUniqueItems() {
        return Stream.of(
                Arguments.of(Array.of(1, 2, 3, 4, 5, 5, 4, 3, 2, 1)),
                Arguments.of(Stack.of(1, 2, 3, 4, 5, 5, 4, 3, 2, 1)),
                Arguments.of(Queue.of(1, 2, 3, 4, 5, 5, 4, 3, 2, 1))
        );
    }

    private static Stream<Arguments> provideLinearStructuresWith10ItemsIncludeNull() {
        return Stream.of(
                Arguments.of(Array.of(null, 2, 3, 4, 5, 6, null, 8, 9, null)),
                Arguments.of(Stack.of(null, 2, 3, 4, 5, 6, null, 8, 9, null)),
                Arguments.of(Queue.of(null, 2, 3, 4, 5, 6, null, 8, 9, null))
        );
    }

    private static Stream<Arguments> provideLinearStructuresWith10ItemsAndAddOperation() {
        Array<Integer> array = Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Queue<Integer> queue = Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return Stream.of(
                Arguments.of(array, (Consumer<Integer>) array::append),
                Arguments.of(stack, (Consumer<Integer>) stack::putLast),
                Arguments.of(queue, (Consumer<Integer>) queue::putLast)
        );
    }

    private static Stream<Arguments> provideLinearStructuresWith10ItemsAndRemoveOperation() {
        Array<Integer> array = Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stack<Integer> stack = Stack.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Queue<Integer> queue = Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return Stream.of(
                Arguments.of(array, (Runnable)() -> array.quickRemove(0)),
                Arguments.of(stack, (Runnable) stack::removeLast),
                Arguments.of(queue, (Runnable) queue::removeFirst)
        );
    }

}