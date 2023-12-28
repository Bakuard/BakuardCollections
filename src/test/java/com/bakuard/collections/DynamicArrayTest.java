package com.bakuard.collections;

import com.bakuard.collections.exceptions.NegativeSizeException;
import com.bakuard.collections.testUtil.ClosedRange;
import com.bakuard.collections.testUtil.Fabric;
import com.bakuard.collections.testUtil.Mutator;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

class DynamicArrayTest {

    @Test
    @DisplayName("DynamicArray(size): negative size => exception")
    public void DynamicArray_size_exception() {
        Assertions.assertThatThrownBy(() -> new DynamicArray<>(-1))
                .isInstanceOf(NegativeSizeException.class);
    }

    @DisplayName("DynamicArray(size):")
    @ParameterizedTest(name = """
             size is {0}
             => expectedSize {1},
                all items is null
            """)
    @MethodSource("provideForConstructorWithSize")
    public void DynamicArray_size(int size, int expectedSize) {
        DynamicArray<Integer> actual = new DynamicArray<>(size);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).satisfiesAnyOf(
                array -> Assertions.assertThat(array).isEmpty(),
                array -> Assertions.assertThat(array).containsOnlyNulls()
        );
        assertions.assertThat(actual.size()).isEqualTo(expectedSize);
        assertions.assertAll();
    }

    @DisplayName("DynamicArray(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForCopyConstructor1")
    public void DynamicArray_copy(DynamicArray<Integer> origin, DynamicArray<Integer> expected) {
        DynamicArray<Integer> actual = new DynamicArray<>(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("DynamicArray(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0},
             change origin and copy after creation
             => expectedSize is {1},
                expectedCopy is {2}
            """)
    @MethodSource("provideForCopyConstructor2")
    public void DynamicArray_copy_doNotChangeOrigin(DynamicArray<Integer> origin,
                                                    DynamicArray<Integer> expectedOrigin,
                                                    DynamicArray<Integer> expectedCopy,
                                                    Mutator<Integer, DynamicArray<Integer>> originMutator,
                                                    Mutator<Integer, DynamicArray<Integer>> expectedMutator) {
        DynamicArray<Integer> actualCopy = new DynamicArray<>(origin);

        originMutator.mutate(origin);
        expectedMutator.mutate(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @DisplayName("replace(index, value):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1},
             value is {2}
             => exception
            """)
    @MethodSource("provideForMethodWithIndexParam_twoParams_openInterval_exceptionCase")
    public void replace_exception(DynamicArray<Integer> origin, int index, Integer value) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.replace(index, value));
    }

    @DisplayName("replace(index, value):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1},
             value is {2},
             expectedReturnedValue is {3}
             => expected is {4}
            """)
    @MethodSource("provideForReplace")
    public void replace(DynamicArray<Integer> origin,
                        int index,
                        Integer value,
                        Integer expectedReturnedValue,
                        DynamicArray<Integer> expected) {
        Integer actualReturnedValue = origin.replace(index, value);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertAll();
    }

    @DisplayName("setWithoutBound(index, value):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1},
             value is {2}
             => exception
            """)
    @MethodSource("provideForMethodWithIndexParam_twoParams_notNegative_exceptionCase")
    public void setWithoutBound_exception(DynamicArray<Integer> origin, int index, Integer value) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.setWithoutBound(index, value));
    }

    @DisplayName("setWithoutBound(index, value):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1},
             value is {2},
             expectedReturnedValue is {3}
             => expected is {4}
            """)
    @MethodSource("provideForSetWithoutBound")
    public void setWithoutBound(DynamicArray<Integer> origin,
                                int index,
                                Integer value,
                                Integer expectedReturnedValue,
                                DynamicArray<Integer> expected) {
        Integer actualReturnedValue = origin.setWithoutBound(index, value);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertAll();
    }

    @DisplayName("append(value):")
    @ParameterizedTest(name = """
             origin is {0},
             value is {1},
             => expected is {2}
            """)
    @MethodSource("provideForAppend")
    public void append(DynamicArray<Integer> origin, Integer value, DynamicArray<Integer> expected) {
        origin.append(value);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("appendAll(data):")
    @ParameterizedTest(name = """
             origin is {0},
             data is {1},
             => expected is {2}
            """)
    @MethodSource("provideForAppendAll")
    public void appendAll(DynamicArray<Integer> origin, Integer[] data, DynamicArray<Integer> expected) {
        origin.appendAll(data);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("appendAll(iterable):")
    @ParameterizedTest(name = """
             origin is {0},
             iterable is {1},
             => expected is {2}
            """)
    @MethodSource("provideForAppendAll_iterable")
    public void appendAll_Iterable(DynamicArray<Integer> origin, Iterable<Integer> iterable, DynamicArray<Integer> expected) {
        origin.appendAll(iterable);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("insert(index, value):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1},
             value is {2}
             => exception
            """)
    @MethodSource("provideForMethodWithIndexParam_twoParams_closedInterval_exceptionCase")
    public void insert_exception(DynamicArray<Integer> origin, int index, Integer value) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.replace(index, value));
    }

    @DisplayName("insert(index, value):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1},
             value is {2}
             => expected is {3}
            """)
    @MethodSource("provideForInsert")
    public void insert(DynamicArray<Integer> origin, int index, Integer value, DynamicArray<Integer> expected) {
        origin.insert(index, value);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("binaryInsert(value, comparator):")
    @ParameterizedTest(name = """
             origin is {0},
             value is {1},
             acceptableIndexes is {2}
             => expected is {3}
            """)
    @MethodSource("provideForBinaryInsert")
    public void binaryInsert(DynamicArray<Integer> origin,
                             Integer value,
                             ClosedRange acceptableIndexes,
                             DynamicArray<Integer> expected) {
        int actualIndex = origin.binaryInsert(value, Integer::compare);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actualIndex).isBetween(acceptableIndexes.from(), acceptableIndexes.to());
        assertions.assertAll();
    }

    @DisplayName("swap(firstIndex, secondIndex):")
    @ParameterizedTest(name = """
             origin is {0},
             firstIndex is {1},
             secondIndex is {2}
             => exception
            """)
    @MethodSource("provideForSwap_ExceptionCases")
    public void swap_exception(DynamicArray<Integer> origin, int firstIndex, int secondIndex) {
        Assertions.assertThatIndexOutOfBoundsException()
                .isThrownBy(() -> origin.swap(firstIndex, secondIndex));
    }

    @DisplayName("swap(firstIndex, secondIndex):")
    @ParameterizedTest(name = """
             origin is {0},
             firstIndex is {1},
             secondIndex is {2}
             => expected is {3}
            """)
    @MethodSource("provideForSwap")
    public void swap(DynamicArray<Integer> origin, int firstIndex, int secondIndex, DynamicArray<Integer> expected) {
        origin.swap(firstIndex, secondIndex);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("quickRemove(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForMethodWithIndexParam_singleParams_openInterval_exceptionCase")
    public void quickRemove_exception(DynamicArray<Integer> origin, int index) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.quickRemove(index));
    }

    @DisplayName("quickRemove(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => expected is {3},
                expectedValue is {4}
            """)
    @MethodSource("provideForQuickRemove")
    public void quickRemove(DynamicArray<Integer> origin, int index, int expectedValue, DynamicArray<Integer> expected) {
        Integer actual = origin.quickRemove(index);
        
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actual).isEqualTo(expectedValue);
        origin.growToSize(origin.size() + 1);
        assertions.assertThat(origin.get(origin.size() - 1)).isNull();
        assertions.assertAll();
    }

    @DisplayName("orderedRemove(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => exception
            """)
    @MethodSource("provideForMethodWithIndexParam_singleParams_openInterval_exceptionCase")
    public void orderedRemove_exception(DynamicArray<Integer> origin, int index) {
        Assertions.assertThatIndexOutOfBoundsException().isThrownBy(() -> origin.orderedRemove(index));
    }

    @DisplayName("orderedRemove(index):")
    @ParameterizedTest(name = """
             origin is {0},
             index is {1}
             => expected is {3},
                expectedValue is {4}
            """)
    @MethodSource("provideForOrderedRemove")
    public void orderedRemove(DynamicArray<Integer> origin, int index, int expectedValue, DynamicArray<Integer> expected) {
        Integer actual = origin.orderedRemove(index);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actual).isEqualTo(expectedValue);
        origin.growToSize(origin.size() + 1);
        assertions.assertThat(origin.get(origin.size() - 1)).isNull();
        assertions.assertAll();
    }

    @DisplayName("removeLast():")
    @ParameterizedTest(name = """
             origin is {0}
             => expectedValue is {1},
                expected is {2}
            """)
    @MethodSource("provideForRemoveLast")
    public void removeLast(DynamicArray<Integer> origin, Integer expectedValue, DynamicArray<Integer> expected) {
        Integer actual = origin.removeLast();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actual).isEqualTo(expectedValue);
        origin.growToSize(origin.size() + 1);
        assertions.assertThat(origin.getLast()).isNull();
        assertions.assertAll();
    }

    @DisplayName("clear():")
    @ParameterizedTest(name = """
             origin is {0}
             => expected is {1}
            """)
    @MethodSource("provideForClear")
    public void clear(DynamicArray<Integer> origin, DynamicArray<Integer> expected) {
        origin.clear();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        origin.growToSize(100);
        assertions.assertThat(origin).containsOnlyNulls();
        assertions.assertAll();
    }

    @DisplayName("sort(comparator):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected is {2}
            """)
    @MethodSource("provideForSort")
    public void sort(DynamicArray<Integer> origin, Comparator<Integer> comparator, DynamicArray<Integer> expected) {
        origin.sort(comparator);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("binarySearch(comparator):")
    @ParameterizedTest(name = """
             origin is {0},
             value is {1}
             => acceptableIndexes is {2}
            """)
    @MethodSource("provideForBinarySearch")
    public void binarySearch(DynamicArray<Integer> origin,
                             Integer value,
                             ClosedRange acceptableIndexes) {
        ToIntFunction<Integer> comparator = v -> v == null ? 1 : value - v;

        int actualIndex = origin.binarySearch(comparator);

        Assertions.assertThat(actualIndex).isBetween(acceptableIndexes.from(), acceptableIndexes.to());
    }

    @DisplayName("""
            growToSize(newSize):
                newSize < 0
                => exception
            """)
    @Test
    public void growToSize_exception() {
        DynamicArray<Integer> origin = DynamicArray.of(0,1,2,3,4,5,6,7,8,9);

        Assertions.assertThatThrownBy(() -> origin.growToSize(-1)).isInstanceOf(NegativeSizeException.class);
    }

    @DisplayName("growToSize(newSize):")
    @ParameterizedTest(name = """
             origin is {0},
             newSize is {1}
             => expected is {2},
                expectedReturnedResult is {3}
            """)
    @MethodSource("provideForGrowToSize")
    public void growToSize(DynamicArray<Integer> origin,
                           int newSize,
                           DynamicArray<Integer> expected) {
        origin.growToSize(newSize);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("equals(Object o):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForEquals")
    public void equals(DynamicArray<Integer> origin, DynamicArray<Integer> other, boolean expected) {
        boolean actual = origin.equals(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    public void equals_idempotence() {
        DynamicArray<Integer> origin = DynamicArray.of(10,20,30,40,50,60,70,80,90,100);

        Assertions.assertThat(origin.equals(origin)).isTrue();
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    public void equals_commutative() {
        DynamicArray<Integer> first = DynamicArray.of(10,20,30,40,50,60,70,80,90,100);
        DynamicArray<Integer> second = DynamicArray.of(10,20,30,40,50,60,70,80,90,100);

        Assertions.assertThat(first.equals(second) == second.equals(first)).isTrue();
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    public void equals_transitive() {
        DynamicArray<Integer> first = DynamicArray.of(10,20,30,40,50,60,70,80,90,100);
        DynamicArray<Integer> second = DynamicArray.of(10,20,30,40,50,60,70,80,90,100);
        DynamicArray<Integer> third = DynamicArray.of(10,20,30,40,50,60,70,80,90,100);

        Assertions.assertThat(first.equals(second) == second.equals(third) == first.equals(third)).isTrue();
    }


    private static Stream<Arguments> provideForConstructorWithSize() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(1, 1),
                Arguments.of(100, 100)
        );
    }

    private static Stream<Arguments> provideForCopyConstructor1() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), new DynamicArray<>()),
                Arguments.of(new DynamicArray<>(1), new DynamicArray<>(1)),
                Arguments.of(DynamicArray.of(100), DynamicArray.of(100)),
                Arguments.of(new DynamicArray<>(100), new DynamicArray<>(100)),
                Arguments.of(
                        DynamicArray.of(0,1,2,5,null,9,10,13,14,22,null,null,27,34),
                        DynamicArray.of(0,1,2,5,null,9,10,13,14,22,null,null,27,34)
                )
        );
    }

    private static Stream<Arguments> provideForCopyConstructor2() {
        return Stream.of(
                Arguments.of(
                        DynamicArray.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        new DynamicArray<>(),
                        DynamicArray.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        (Mutator<Integer, DynamicArray<Integer>>) DynamicArray::clear,
                        (Mutator<Integer, DynamicArray<Integer>>) array -> {}
                ),
                Arguments.of(
                        DynamicArray.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        DynamicArray.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        new DynamicArray<>(),
                        (Mutator<Integer, DynamicArray<Integer>>) array -> {},
                        (Mutator<Integer, DynamicArray<Integer>>) DynamicArray::clear
                )
        );
    }

    private static Stream<Arguments> provideForMethodWithIndexParam_twoParams_openInterval_exceptionCase() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), -1, 100),
                Arguments.of(new DynamicArray<>(), 0, 100),
                Arguments.of(new DynamicArray<>(), 1, 100),
                Arguments.of(new DynamicArray<>(1), -1, 100),
                Arguments.of(new DynamicArray<>(1), 1, 100),
                Arguments.of(new DynamicArray<>(1), 2, 100),
                Arguments.of(new DynamicArray<>(1000), -1, 100),
                Arguments.of(new DynamicArray<>(1000), 1000, 100),
                Arguments.of(new DynamicArray<>(1000), 1001, 100)
        );
    }

    private static Stream<Arguments> provideForMethodWithIndexParam_singleParams_openInterval_exceptionCase() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), -1),
                Arguments.of(new DynamicArray<>(), 0),
                Arguments.of(new DynamicArray<>(), 1),
                Arguments.of(new DynamicArray<>(1), -1),
                Arguments.of(new DynamicArray<>(1), 1),
                Arguments.of(new DynamicArray<>(1), 2),
                Arguments.of(new DynamicArray<>(1000), -1),
                Arguments.of(new DynamicArray<>(1000), 1000),
                Arguments.of(new DynamicArray<>(1000), 1001)
        );
    }

    private static Stream<Arguments> provideForMethodWithIndexParam_twoParams_notNegative_exceptionCase() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), -1, 100),
                Arguments.of(new DynamicArray<>(1), -1, 100),
                Arguments.of(new DynamicArray<>(1000), -1, 100)
        );
    }

    private static Stream<Arguments> provideForMethodWithIndexParam_twoParams_closedInterval_exceptionCase() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), -1, 100),
                Arguments.of(new DynamicArray<>(), 1, 100),
                Arguments.of(new DynamicArray<>(1), -1, 100),
                Arguments.of(new DynamicArray<>(1), 2, 100),
                Arguments.of(new DynamicArray<>(1000), -1, 100),
                Arguments.of(new DynamicArray<>(1000), 1001, 100)
        );
    }

    private static Stream<Arguments> provideForReplace() {
        return Stream.of(
                Arguments.of(DynamicArray.of(new Integer[]{null}), 0, 100, null, DynamicArray.of(100)),
                Arguments.of(DynamicArray.of(77), 0, 100, 77, DynamicArray.of(100)),
                Arguments.of(DynamicArray.of(77), 0, null, 77, DynamicArray.of(new Integer[]{null})),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,null,60,101,121,317,560,666,917),
                        5, 55, null,
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        5, 77, 55,
                        DynamicArray.of(0,1,2,7,12,77,60,101,121,317,560,666,917)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        5, null, 55,
                        DynamicArray.of(0,1,2,7,12,null,60,101,121,317,560,666,917)
                )
        );
    }

    private static Stream<Arguments> provideForSetWithoutBound() {
        return Stream.of(
                Arguments.of(DynamicArray.of(new Integer[]{null}), 0, 100, null, DynamicArray.of(100)),
                Arguments.of(DynamicArray.of(77), 0, 100, 77, DynamicArray.of(100)),
                Arguments.of(DynamicArray.of(77), 0, null, 77, DynamicArray.of(new Integer[]{null})),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,null,60,101,121,317,560,666,917),
                        5, 55, null,
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        5, 77, 55,
                        DynamicArray.of(0,1,2,7,12,77,60,101,121,317,560,666,917)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        5, null, 55,
                        DynamicArray.of(0,1,2,7,12,null,60,101,121,317,560,666,917)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        13, 1000, null,
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917,1000)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        14, 1000, null,
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917,null,1000)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917),
                        16, 1000, null,
                        DynamicArray.of(0,1,2,7,12,55,60,101,121,317,560,666,917,null,null,null,1000)
                )
        );
    }

    private static Stream<Arguments> provideForAppend() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), null, DynamicArray.of(new Integer[]{null})),
                Arguments.of(new DynamicArray<>(), 100, DynamicArray.of(100)),
                Arguments.of(DynamicArray.of(10), null, DynamicArray.of(10, null)),
                Arguments.of(DynamicArray.of(10), 100, DynamicArray.of(10, 100)),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        100,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        null,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,null)
                )
        );
    }

    private static Stream<Arguments> provideForAppendAll() {
        Fabric<Integer, DynamicArray<Integer>> fabric = data -> {
            DynamicArray<Integer> result = new DynamicArray<>();
            for(Integer value : data) result.append(value);
            return result;
        };

        return Stream.of(
                Arguments.of(new DynamicArray<>(), new Integer[0], new DynamicArray<>()),
                Arguments.of(new DynamicArray<>(), new Integer[]{null}, fabric.create(new Integer[]{null})),
                Arguments.of(new DynamicArray<>(), new Integer[]{100}, fabric.create(100)),
                Arguments.of(new DynamicArray<>(), new Integer[]{100, null, 1000}, fabric.create(100, null, 1000)),
                Arguments.of(fabric.create(10), new Integer[0], fabric.create(10)),
                Arguments.of(fabric.create(10), new Integer[]{null}, fabric.create(10, null)),
                Arguments.of(fabric.create(10), new Integer[]{100}, fabric.create(10, 100)),
                Arguments.of(fabric.create(10), new Integer[]{100, null, 1000}, fabric.create(10, 100, null, 1000)),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[0],
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[]{null},
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,null)
                ),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[]{100},
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100)
                ),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[]{100,null,1000,1001,1200,1307,null,null,1400},
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
                                100,null,1000,1001,1200,1307,null,null,1400)
                )
        );
    }

    private static Stream<Arguments> provideForAppendAll_iterable() {
        Fabric<Integer, List<Integer>> fabric = data -> new ArrayList<>(Arrays.asList(data));

        return Stream.of(
                Arguments.of(new DynamicArray<>(), fabric.create(), new DynamicArray<>()),
                Arguments.of(new DynamicArray<>(), fabric.create(new Integer[]{null}), DynamicArray.of(new Integer[]{null})),
                Arguments.of(new DynamicArray<>(), fabric.create(100), DynamicArray.of(100)),
                Arguments.of(new DynamicArray<>(), fabric.create(100, null, 1000), DynamicArray.of(100, null, 1000)),
                Arguments.of(DynamicArray.of(10), fabric.create(), DynamicArray.of(10)),
                Arguments.of(DynamicArray.of(10), fabric.create(new Integer[]{null}), DynamicArray.of(10, null)),
                Arguments.of(DynamicArray.of(10), fabric.create(100), DynamicArray.of(10, 100)),
                Arguments.of(DynamicArray.of(10), fabric.create(100, null, 1000), DynamicArray.of(10, 100, null, 1000)),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        fabric.create(),
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        fabric.create(new Integer[]{null}),
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,null)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        fabric.create(100),
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        fabric.create(100,null,1000,1001,1200,1307,null,null,1400),
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
                                100,null,1000,1001,1200,1307,null,null,1400)
                )
        );
    }

    private static Stream<Arguments> provideForInsert() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), 0, 100, DynamicArray.of(100)),
                Arguments.of(DynamicArray.of(10), 0, 100, DynamicArray.of(100, 10)),
                Arguments.of(DynamicArray.of(10), 1, 100, DynamicArray.of(10, 100)),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        0, 100,
                        DynamicArray.of(100,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        10, 100,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,100,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        21, 100,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        0, null,
                        DynamicArray.of(null,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        10, null,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,null,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        21, null,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,null)
                )
        );
    }

    private static Stream<Arguments> provideForBinaryInsert() {
        return Stream.of(
                Arguments.of(
                        new DynamicArray<>(),
                        100, new ClosedRange(0, 0),
                        DynamicArray.of(100)
                ),
                Arguments.of(
                        DynamicArray.of(10),
                        1, new ClosedRange(0, 0),
                        DynamicArray.of(1, 10)
                ),
                Arguments.of(
                        DynamicArray.of(10),
                        100, new ClosedRange(1, 1),
                        DynamicArray.of(10, 100)
                ),
                Arguments.of(
                        DynamicArray.of(10),
                        10, new ClosedRange(0, 1),
                        DynamicArray.of(10, 10)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        1, new ClosedRange(0, 0),
                        DynamicArray.of(1, 10, 20)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        100, new ClosedRange(2, 2),
                        DynamicArray.of(10, 20, 100)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        15, new ClosedRange(1, 1),
                        DynamicArray.of(10, 15, 20)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        10, new ClosedRange(0, 1),
                        DynamicArray.of(10, 10, 20)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        20, new ClosedRange(1, 2),
                        DynamicArray.of(10, 20, 20)
                ),
                Arguments.of(
                        DynamicArray.of(10, 10),
                        10, new ClosedRange(0, 2),
                        DynamicArray.of(10, 10, 10)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        1, new ClosedRange(0, 0),
                        DynamicArray.of(1,10,20,30,40,50,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        1000, new ClosedRange(10, 10),
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,1000)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        55, new ClosedRange(5, 5),
                        DynamicArray.of(10,20,30,40,50,55,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        10, new ClosedRange(0, 1),
                        DynamicArray.of(10,10,20,30,40,50,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        100, new ClosedRange(9, 10),
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        50, new ClosedRange(4, 5),
                        DynamicArray.of(10,20,30,40,50,50,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,10,10,10,10,10,10,10,10,10),
                        10, new ClosedRange(0, 10),
                        DynamicArray.of(10,10,10,10,10,10,10,10,10,10,10)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        1, new ClosedRange(0, 0),
                        DynamicArray.of(1,10,20,30,40,50,60,70,80,90,100,110)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        1000, new ClosedRange(11, 11),
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,1000)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        55, new ClosedRange(5, 5),
                        DynamicArray.of(10,20,30,40,50,55,60,70,80,90,100,110)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        10, new ClosedRange(0, 1),
                        DynamicArray.of(10,10,20,30,40,50,60,70,80,90,100,110)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        110, new ClosedRange(10, 11),
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,110)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        50, new ClosedRange(4, 5),
                        DynamicArray.of(10,20,30,40,50,50,60,70,80,90,100,110)
                ),
                Arguments.of(
                        DynamicArray.of(10,10,10,10,10, 10,10,10,10,10, 10),
                        10, new ClosedRange(0, 11),
                        DynamicArray.of(10,10,10,10,10, 10,10,10,10,10, 10,10)
                )
        );
    }

    private static Stream<Arguments> provideForSwap_ExceptionCases() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), 0, 0),
                Arguments.of(new DynamicArray<>(), -1, 0),
                Arguments.of(new DynamicArray<>(), 0, -1),
                Arguments.of(new DynamicArray<>(), 0, 1),
                Arguments.of(new DynamicArray<>(), 1, 0),
                Arguments.of(DynamicArray.of(100), 0, -1),
                Arguments.of(DynamicArray.of(100), -1, 0),
                Arguments.of(DynamicArray.of(100), 1, 0),
                Arguments.of(DynamicArray.of(100), 0, 1),
                Arguments.of(DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), -1, 0),
                Arguments.of(DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), 21, 0),
                Arguments.of(DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), 22, 0),
                Arguments.of(DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), 0, -1),
                Arguments.of(DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), 0, 21),
                Arguments.of(DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20), 0, 22)
        );
    }

    private static Stream<Arguments> provideForSwap() {
        return Stream.of(
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        10, 10,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        10, 11,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,11,10,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        0, 20,
                        DynamicArray.of(20,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,0)
                )
        );
    }

    private static Stream<Arguments> provideForQuickRemove() {
        return Stream.of(
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        0, 10,
                        DynamicArray.of(200,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        10, 110,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,200,120,130,140,150,160,170,180,190)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        19, 200,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190)
                ),
                Arguments.of(
                        DynamicArray.of(100),
                        0, 100,
                        new DynamicArray<>()
                )
        );
    }

    private static Stream<Arguments> provideForOrderedRemove() {
        return Stream.of(
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        0, 10,
                        DynamicArray.of(20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        10, 110,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,120,130,140,150,160,170,180,190,200)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        19, 200,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190)
                ),
                Arguments.of(
                        DynamicArray.of(100),
                        0, 100,
                        new DynamicArray<>()
                )
        );
    }

    private static Stream<Arguments> provideForClear() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), new DynamicArray<>()),
                Arguments.of(DynamicArray.of(100), new DynamicArray<>()),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        new DynamicArray<>()
                )
        );
    }

    private static Stream<Arguments> provideForSort() {
        return Stream.of(
                Arguments.of(
                        new DynamicArray<>(),
                        (Comparator<Integer>) Integer::compare,
                        new DynamicArray<>()
                ),
                Arguments.of(
                        DynamicArray.of(100),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(100)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10, 20)
                ),
                Arguments.of(
                        DynamicArray.of(20, 10),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10, 20)
                ),
                Arguments.of(
                        DynamicArray.of(10, 10),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10, 10)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200)
                ),
                Arguments.of(
                        DynamicArray.of(200,190,180,170,160,150,140,130,120,110,100,90,80,70,60,50,40,30,20,10),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200)
                ),
                Arguments.of(
                        DynamicArray.of(10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10)
                ),
                Arguments.of(
                        DynamicArray.of(130,10,20,120,150,70,110,200,190,60,30,80,140,100,90,160,170,40,180,50),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200)
                ),
                Arguments.of(
                        DynamicArray.of(170,70,100,80,10,90,10,60,40,200,100,190,170,140,160,50,150,90,90,10),
                        (Comparator<Integer>) Integer::compare,
                        DynamicArray.of(10,10,10,40,50,60,70,80,90,90,90,100,100,140,150,160,170,170,190,200)
                )
        );
    }

    private static Stream<Arguments> provideForBinarySearch() {
        return Stream.of(
                Arguments.of(
                        new DynamicArray<>(),
                        10,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10),
                        10,
                        new ClosedRange(0, 0)
                ),
                Arguments.of(
                        DynamicArray.of(10),
                        1,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10),
                        100,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        1,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        100,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        15,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        10,
                        new ClosedRange(0, 0)
                ),
                Arguments.of(
                        DynamicArray.of(10, 20),
                        20,
                        new ClosedRange(1, 1)
                ),
                Arguments.of(
                        DynamicArray.of(10, 10),
                        10,
                        new ClosedRange(0, 1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        1,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        1000,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        55,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        10,
                        new ClosedRange(0, 0)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        50,
                        new ClosedRange(4, 4)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        100,
                        new ClosedRange(9, 9)
                ),
                Arguments.of(
                        DynamicArray.of(10,10,10,40,50,60,70,80,90,100),
                        10,
                        new ClosedRange(0, 2)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,50,50,80,90,100),
                        50,
                        new ClosedRange(4, 6)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,50,50,100,100,100),
                        100,
                        new ClosedRange(7, 9)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        1,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        1000,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        55,
                        new ClosedRange(-1, -1)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        10,
                        new ClosedRange(0, 0)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        50,
                        new ClosedRange(4, 4)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        110,
                        new ClosedRange(10, 10)
                ),
                Arguments.of(
                        DynamicArray.of(10,10,10,40,50,60,70,80,90,100,110),
                        10,
                        new ClosedRange(0, 2)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,50,50,80,90,100,110),
                        50,
                        new ClosedRange(4, 6)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,50,50,80,110,110,110),
                        110,
                        new ClosedRange(8, 10)
                )
        );
    }

    private static Stream<Arguments> provideForGrowToSize() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), 0, new DynamicArray<>()),
                Arguments.of(new DynamicArray<>(), 1, new DynamicArray<>(1)),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        0,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        9,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        10,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        11,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,null)
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        15,
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,null,null,null,null,null)
                )
        );
    }

    private static Stream<Arguments> provideForEquals() {
        DynamicArray<Integer> arrayWithBigEntrySize = new DynamicArray<>(1000);
        for(int i = 0; i < 1000; i++) arrayWithBigEntrySize.quickRemove(0);
        arrayWithBigEntrySize.appendAll(10,20,30,40,50,60,70,80,90,100);

        return Stream.of(
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        DynamicArray.of(10,20,30,null,50,60,null,80,90,100),
                        false
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        true
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100,110),
                        false
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        DynamicArray.of(10,20,30,null,50,60,null,null,90,100,110),
                        false
                ),
                Arguments.of(
                        DynamicArray.of(10,20,30,40,50,60,70,80,90,100),
                        arrayWithBigEntrySize,
                        true
                )
        );
    }

    private static Stream<Arguments> provideForRemoveLast() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), null, new DynamicArray<>()),
                Arguments.of(DynamicArray.of(new Integer[]{null}), null, new DynamicArray<>()),
                Arguments.of(DynamicArray.of(100), 100, new DynamicArray<>()),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,null),
                        null,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
                ),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15),
                        15,
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14)
                )
        );
    }
}