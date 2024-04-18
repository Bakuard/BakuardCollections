package com.bakuard.collections;

import com.bakuard.collections.function.IndexBiFunction;
import com.bakuard.collections.function.IndexBiPredicate;
import com.bakuard.collections.testUtil.ArgumentsBuilder;
import com.bakuard.collections.testUtil.Fabric;
import com.bakuard.collections.testUtil.Mutator;
import com.bakuard.collections.testUtil.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

class ReadableLinearStructureTest {

    @DisplayName("get(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => expected {2}
            """)
    @MethodSource("provideForGetMethod")
    void get(ReadableLinearStructure<Integer> linearStructure, int index, Object expected) {
        Integer actual = linearStructure.get(index);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("get(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => exception
            """)
    @MethodSource("provideForGetMethod_Exception")
    void get_exception(ReadableLinearStructure<Integer> linearStructure,
                       int index,
                       Class<? extends Throwable> expectedException) {
        Assertions.assertThatThrownBy(() -> linearStructure.get(index))
                .isInstanceOf(expectedException);
    }

    @DisplayName("at(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => expected {2}
            """)
    @MethodSource("provideForAtMethod")
    void at(ReadableLinearStructure<Integer> linearStructure, int index, Object expected) {
        Integer actual = linearStructure.at(index);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("at(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => exception
            """)
    @MethodSource("provideForAtMethod_Exception")
    void at_exception(ReadableLinearStructure<Integer> linearStructure,
                      int index,
                      Class<? extends Throwable> expectedException) {
        Assertions.assertThatThrownBy(() -> linearStructure.at(index))
                .isInstanceOf(expectedException);
    }

    @DisplayName("getFirst():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForGetFirstMethod")
    void getFirst(ReadableLinearStructure<Integer> linearStructure, Integer expected) {
        Integer actual = linearStructure.getFirst();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("getLast():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForGetLastMethod")
    void getLast(ReadableLinearStructure<Integer> linearStructure, Integer expected) {
        Integer actual = linearStructure.getLast();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("size():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForSizeMethod")
    void size(ReadableLinearStructure<Integer> linearStructure,
              Mutator<Integer, ReadableLinearStructure<Integer>> mutator,
              Integer expected) {
        mutator.mutate(linearStructure);

        Integer actual = linearStructure.size();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("isEmpty():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForEmptyMethod")
    void isEmpty(ReadableLinearStructure<Integer> linearStructure,
                 Mutator<Integer, ReadableLinearStructure<Integer>> mutator,
                 boolean expected) {
        mutator.mutate(linearStructure);

        boolean actual = linearStructure.isEmpty();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("inBound(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => expected {2}
            """)
    @MethodSource("provideForInBoundMethod")
    void inBound(ReadableLinearStructure<Integer> linearStructure, int index, boolean expected) {
        boolean actual = linearStructure.inBound(index);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("inBoundByModulo(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => expected {2}
            """)
    @MethodSource("provideForInBoundByModuloMethod")
    void inBoundByModulo(ReadableLinearStructure<Integer> linearStructure, int index, boolean expected) {
        boolean actual = linearStructure.inBoundByModulo(index);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("linearSearch(value):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             value = {1}
             => expected {2}
            """)
    @MethodSource("provideForLinearSearchMethod")
    void linearSearch(ReadableLinearStructure<Integer> linearStructure, Integer value, int expected) {
        int actual = linearStructure.linearSearch(value);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("linearSearch(predicate):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             value = {1}
             => expected {2}
            """)
    @MethodSource("provideForLinearSearchWithPredicateMethod")
    void linearSearchWithPredicate(ReadableLinearStructure<Integer> linearStructure, Integer value, int expected) {
        int actual = linearStructure.linearSearch(v -> Objects.equals(v, value));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("linearSearchLast(value):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             value = {1}
             => expected {2}
            """)
    @MethodSource("provideForLinearSearchLastMethod")
    void linearSearchLast(ReadableLinearStructure<Integer> linearStructure, Integer value, int expected) {
        int actual = linearStructure.linearSearchLast(v -> Objects.equals(v, value));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("contains(value):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             value = {1}
             => expected {2}
            """)
    @MethodSource("provideForContainsMethod")
    void contains(ReadableLinearStructure<Integer> linearStructure, Integer value, boolean expected) {
        boolean actual = linearStructure.contains(value);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("contains(predicate):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             value = {1}
             => expected {2}
            """)
    @MethodSource("provideForContainsWithPredicateMethod")
    void containsWithPredicate(ReadableLinearStructure<Integer> linearStructure, Integer value, boolean expected) {
        boolean actual = linearStructure.contains(v -> Objects.equals(v, value));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("frequency(predicate):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             value = {1}
             => expected {2}
            """)
    @MethodSource("provideForFrequencyMethod")
    void frequency(ReadableLinearStructure<Integer> linearStructure, Integer value, int expected) {
        int actual = linearStructure.frequency(v -> Objects.equals(v, value));

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("forEach(action):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             => expected sequence {1}
            """)
    @MethodSource("provideForEachWithIndex")
    void forEachWithIndex(ReadableLinearStructure<Integer> linearStructure, List<Pair<Integer, Integer>> expectedSequence) {
        List<Pair<Integer, Integer>> actualSequence = new ArrayList<>();

        linearStructure.forEach((item, index) -> actualSequence.add(new Pair<>(item, index)));

        Assertions.assertThat(actualSequence).isEqualTo(expectedSequence);
    }

    @DisplayName("toArray():")
    @ParameterizedTest(name = """
             linearStructure is {0},
             => expectedArray is {1}
            """)
    @MethodSource("provideForToArray")
    void toArray(ReadableLinearStructure<Integer> linearStructure, Integer[] expectedArray) {
        Integer[] actualArray = linearStructure.toArray(Integer.class);

        Assertions.assertThat(actualArray).isEqualTo(expectedArray);
    }

    @DisplayName("cloneAndMap(mapper):")
    @ParameterizedTest(name = """
             origin is {0},
             => expected is {2}
            """)
    @MethodSource("provideForCloneAndMap")
    void cloneAndMap(ReadableLinearStructure<Integer> origin,
                     IndexBiFunction<Integer, Integer> mapper,
                     ReadableLinearStructure<Integer> expected) {
        ReadableLinearStructure<Integer> actual = origin.cloneAndMap(mapper);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("cloneAndMap(mapper):")
    @ParameterizedTest(name = """
             origin is {0},
             change origin structure while map
             => exception
            """)
    @MethodSource("provideForCloneAndMap_Exception")
    void cloneAndMap_exception(ReadableLinearStructure<Integer> origin,
                               IndexBiFunction<Integer, Integer> mapper,
                               Class<? extends Throwable> expectedException) {
        Assertions.assertThatThrownBy(() -> origin.cloneAndMap(mapper))
                .isInstanceOf(expectedException);
    }

    @DisplayName("cloneAndFilter(predicate):")
    @ParameterizedTest(name = """
             origin is {0},
             => expected is {2}
            """)
    @MethodSource("provideForCloneAndFilter")
    void cloneAndFilter(ReadableLinearStructure<Integer> origin,
                        IndexBiPredicate<Integer> predicate,
                        ReadableLinearStructure<Integer> expected) {
        ReadableLinearStructure<Integer> actual = origin.cloneAndFilter(predicate);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("cloneAndFilter(predicate):")
    @ParameterizedTest(name = """
             origin is {0},
             change origin structure while filter
             => exception
            """)
    @MethodSource("provideForCloneAndFilter_Exception")
    void cloneAndFilter_exception(ReadableLinearStructure<Integer> origin,
                                  IndexBiPredicate<Integer> predicate,
                                  Class<? extends Throwable> expectedException) {
        Assertions.assertThatThrownBy(() -> origin.cloneAndFilter(predicate))
                .isInstanceOf(expectedException);
    }

    @DisplayName("reduce(accumulator):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             => expected is {2}
            """)
    @MethodSource("provideForReduce")
    void reduce(ReadableLinearStructure<Integer> linearStructure,
                BinaryOperator<Integer> accumulator,
                Integer expected) {
        Integer actual = linearStructure.reduce(accumulator);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("reduce(accumulator):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             change origin structure while reduce
             => exception
            """)
    @MethodSource("provideForReduce_Exception")
    void reduce_exception(ReadableLinearStructure<Integer> linearStructure,
                          BinaryOperator<Integer> accumulator,
                          Class<? extends Throwable> expectedException) {
        Assertions.assertThatThrownBy(() -> linearStructure.reduce(accumulator))
                .isInstanceOf(expectedException);
    }

    @DisplayName("reduce(initValue, accumulator):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             initValue is {1}
             => expected is {3}
            """)
    @MethodSource("provideForReduceWithInitValue")
    void reduceWithInitValue(ReadableLinearStructure<Integer> linearStructure,
                             Integer initValue,
                             BinaryOperator<Integer> accumulator,
                             Integer expected) {
        Integer actual = linearStructure.reduce(initValue, accumulator);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("reduce(initValue, accumulator):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             initValue is {1},
             change origin structure while reduce
             => exception
            """)
    @MethodSource("provideForReduceWithInitValue_Exception")
    void reduceWithInitValue_exception(ReadableLinearStructure<Integer> linearStructure,
                                       Integer initValue,
                                       BinaryOperator<Integer> accumulator,
                                       Class<? extends Throwable> expectedException) {
        Assertions.assertThatThrownBy(() -> linearStructure.reduce(initValue, accumulator))
                .isInstanceOf(expectedException);
    }


    private static <T> Stream<Fabric<T, ReadableLinearStructure<T>>> structureFabrics() {
        return Stream.of(
                new Fabric<>() {
                    @Override
                    public ReadableLinearStructure<T> createWithSize(int size, T... data) {
                        return DynamicArray.of(data);
                    }

                    @Override
                    public Class<?> getType() {
                        return DynamicArray.class;
                    }
                },
                new Fabric<>() {
                    @Override
                    public ReadableLinearStructure<T> createWithSize(int size, T... data) {
                        return Stack.of(data);
                    }

                    @Override
                    public Class<?> getType() {
                        return Stack.class;
                    }
                },
                new Fabric<>() {
                    @Override
                    public ReadableLinearStructure<T> createWithSize(int size, T... data) {
                        return Queue.of(data);
                    }

                    @Override
                    public Class<?> getType() {
                        return Queue.class;
                    }
                },
                new Fabric<>() {
                    @Override
                    public ReadableLinearStructure<T> createWithSize(int size, T... data) {
                        return Deque.of(data);
                    }

                    @Override
                    public Class<?> getType() {
                        return Deque.class;
                    }
                },
                new Fabric<>() {
                    @Override
                    public ReadableLinearStructure<T> createWithSize(int size, T... data) {
                        return RingBuffer.of(size, data);
                    }

                    @Override
                    public Class<?> getType() {
                        return RingBuffer.class;
                    }
                }
        );
    }

    private static Stream<Arguments> provideForGetMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(1000)
                    .addArgs(0).expectedValue(1000)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(0).expectedValue(10)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(9).expectedValue(100)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(5).expectedValue(60)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, null, 70, 80, 90, 100)
                    .addArgs(5).expectedValue(null)
                .build();
    }

    private static Stream<Arguments> provideForGetMethod_Exception() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(-1)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(10)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(11)
                    .expectedException(IndexOutOfBoundsException.class)

                .newTest()
                    .originStruct()
                    .addArgs(-1)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct()
                    .addArgs(0)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct()
                    .addArgs(1)
                    .expectedException(IndexOutOfBoundsException.class)

                .newTest()
                    .originStruct(1000)
                    .addArgs(-1)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct(1000)
                    .addArgs(1)
                    .expectedException(IndexOutOfBoundsException.class)
                .build();
    }

    private static Stream<Arguments> provideForAtMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(0).expectedValue(10)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(9).expectedValue(100)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(5).expectedValue(60)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, null, 70, 80, 90, 100)
                    .addArgs(5).expectedValue(null)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-1).expectedValue(100)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-10).expectedValue(10)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-5).expectedValue(60)
                .newTest()
                    .originStruct(1000)
                    .addArgs(-1).expectedValue(1000)
                .newTest()
                    .originStruct(1000)
                    .addArgs(0).expectedValue(1000)
                .build();
    }

    private static Stream<Arguments> provideForAtMethod_Exception() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(-11)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(10)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(11)
                    .expectedException(IndexOutOfBoundsException.class)

                .newTest()
                    .originStruct()
                    .addArgs(-1)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct()
                    .addArgs(0)
                    .expectedException(IndexOutOfBoundsException.class)
                .newTest()
                    .originStruct()
                    .addArgs(1)
                    .expectedException(IndexOutOfBoundsException.class)

                .newTest()
                    .originStruct(1000)
                    .addArgs(1)
                    .expectedException(IndexOutOfBoundsException.class)
                .build();
    }

    private static Stream<Arguments> provideForGetFirstMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .expectedValue(null)
                .newTest()
                    .originStruct(1000)
                    .expectedValue(1000)
                .newTest()
                    .originStruct(null, 1, 2, 3, 4, null, 6 ,7 , null, 9)
                    .expectedValue(null)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .expectedValue(10)
                .build();
    }

    private static Stream<Arguments> provideForGetLastMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .expectedValue(null)
                .newTest()
                    .originStruct(1000)
                    .expectedValue(1000)
                .newTest()
                    .originStruct(0, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .expectedValue(null)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .expectedValue(100)
                .build();
    }

    private static Stream<Arguments> provideForSizeMethod() {
        Mutator<Integer, ReadableLinearStructure<Integer>> doNothing = struct -> {};
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct().addArgs(doNothing).expectedValue(0)
                .newTest()
                    .originStruct(1000).addArgs(doNothing).expectedValue(1)
                .newTest()
                    .originStruct(0, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(doNothing).expectedValue(10)
                .build();
    }

    private static Stream<Arguments> provideForEmptyMethod() {
        Mutator<Integer, ReadableLinearStructure<Integer>> doNothing = struct -> {};
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct().addArgs(doNothing).expectedValue(true)
                .newTest()
                    .originStruct(1000).addArgs(doNothing).expectedValue(false)
                .newTest()
                    .originStruct(0, 1, 2, 3, 4, null, 6 ,7 , null, null)
                    .addArgs(doNothing).expectedValue(false)
                .build();
    }

    private static Stream<Arguments> provideForInBoundMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest().originStruct().addArgs(0).expectedValue(false)

                .newTest().originStruct(100).addArgs(0).expectedValue(true)
                .newTest().originStruct(100).addArgs(1).expectedValue(false)
                .newTest().originStruct(100).addArgs(-1).expectedValue(false)

                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-1).expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(10).expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(11).expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(0).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(9).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(5).expectedValue(true)
                .build();
    }

    private static Stream<Arguments> provideForInBoundByModuloMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest().originStruct().addArgs(0).expectedValue(false)

                .newTest().originStruct(100).addArgs(0).expectedValue(true)
                .newTest().originStruct(100).addArgs(1).expectedValue(false)
                .newTest().originStruct(100).addArgs(-1).expectedValue(true)

                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-1).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-10).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-5).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(-11).expectedValue(false)

                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(10).expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(11).expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(0).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(9).expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(5).expectedValue(true)
                .build();
    }

    private static Stream<Arguments> provideForLinearSearchMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(1000)
                    .expectedValue(-1)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(1000)
                    .expectedValue(-1)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 50, 40, 30, 20, 10)
                    .addArgs(40)
                    .expectedValue(3)
                .newTest()
                    .originStruct(10, 20, 30, null, 50, 50, null, 30, 20, 10)
                    .addArgs(new Integer[]{null})
                    .expectedValue(3)
                .build();
    }

    private static Stream<Arguments> provideForLinearSearchWithPredicateMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(1000)
                    .expectedValue(-1)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(1000)
                    .expectedValue(-1)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 50, 40, 30, 20, 10)
                    .addArgs(40)
                    .expectedValue(3)
                .newTest()
                    .originStruct(10, 20, 30, null, 50, 50, null, 30, 20, 10)
                    .addArgs(new Integer[]{null})
                    .expectedValue(3)
                .build();
    }

    private static Stream<Arguments> provideForLinearSearchLastMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(1000)
                    .expectedValue(-1)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(1000)
                    .expectedValue(-1)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 50, 40, 30, 20, 10)
                    .addArgs(40)
                    .expectedValue(6)
                .newTest()
                    .originStruct(10, 20, 30, null, 50, 50, null, 30, 20, 10)
                    .addArgs(new Integer[]{null})
                    .expectedValue(6)
                .build();
    }

    private static Stream<Arguments> provideForContainsMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(1000)
                    .expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(1000)
                    .expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 50, 40, 30, 20, 10)
                    .addArgs(40)
                    .expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, null, 50, 50, null, 30, 20, 10)
                    .addArgs(new Integer[]{null})
                    .expectedValue(true)
                .build();
    }

    private static Stream<Arguments> provideForContainsWithPredicateMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(1000)
                    .expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(1000)
                    .expectedValue(false)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 50, 40, 30, 20, 10)
                    .addArgs(40)
                    .expectedValue(true)
                .newTest()
                    .originStruct(10, 20, 30, null, 50, 50, null, 30, 20, 10)
                    .addArgs(new Integer[]{null})
                    .expectedValue(true)
                .build();
    }

    private static Stream<Arguments> provideForFrequencyMethod() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(1000)
                    .expectedValue(0)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
                    .addArgs(1000)
                    .expectedValue(0)
                .newTest()
                    .originStruct(10, 20, 30, 40, 50, 50, 40, 30, 20, 10)
                    .addArgs(40)
                    .expectedValue(2)
                .newTest()
                    .originStruct(10, 20, 30, null, 50, 50, null, 30, 20, 10)
                    .addArgs(new Integer[]{null})
                    .expectedValue(2)
                .build();
    }

    private static Stream<Arguments> provideForEachWithIndex() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .expectedList()
                .newTest()
                    .originStruct(new Integer[]{null})
                    .expectedList(Pair.of(null, 0))
                .newTest()
                    .originStruct(10)
                    .expectedList(Pair.of(10, 0))
                .newTest()
                    .originStruct(null, 10, 20, 30, 40, null, 60 ,70 , null, null, 100, 110, 120, 130, 140)
                    .expectedList(
                            Pair.of(null, 0), Pair.of(10, 1), Pair.of(20, 2), Pair.of(30, 3), Pair.of(40, 4),
                            Pair.of(null, 5), Pair.of(60, 6), Pair.of(70, 7), Pair.of(null, 8), Pair.of(null, 9),
                            Pair.of(100, 10), Pair.of(110, 11), Pair.of(120, 12), Pair.of(130, 13), Pair.of(140, 14)
                    )
                .build();
    }

    private static Stream<Arguments> provideForToArray() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct()
                    .expectedArray()
                .newTest()
                    .originStruct(new Integer[]{null})
                    .expectedArray(new Integer[]{null})
                .newTest()
                    .originStruct(100)
                    .expectedArray(100)
                .newTest()
                    .originStruct(0,10)
                    .expectedArray(0,10)
                .newTest()
                    .originStruct(0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150)
                    .expectedArray(0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150)
                .build();
    }

    private static Stream<Arguments> provideForCloneAndMap() {
        return ArgumentsBuilder.of(structureFabrics())
                .newTest()
                    .originStruct()
                    .expectedStruct()
                    .addArgs((IndexBiFunction<Integer, Integer>) (item, index) -> item)
                .newTest()
                    .originStruct(new Integer[]{null})
                    .expectedStruct(new Integer[]{null})
                    .addArgs((IndexBiFunction<Integer, Integer>) (item, index) -> item)
                .newTest()
                    .originStruct(1)
                    .expectedStruct(10)
                    .addArgs((IndexBiFunction<Integer, Integer>) (item, index) -> item * 10)
                .newTest()
                    .originStruct(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
                    .expectedStruct(0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150)
                    .addArgs((IndexBiFunction<Integer, Integer>) (item, index) -> item * 10)
                .build();
    }

    private static Stream<Arguments> provideForCloneAndMap_Exception() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(1,2,3,4,5,6,7,8,9,10)
                    .addArgsFor(
                            DynamicArray.class,
                            (DynamicArray<Integer> array) -> List.of((IndexBiFunction<Integer, Integer>) (item, index) -> {
                                    array.clear();
                                    array.trimToSize();
                                    return item;
                            })
                    )
                    .addArgsFor(
                            Stack.class,
                            (Stack<Integer> stack) -> List.of((IndexBiFunction<Integer, Integer>) (item, index) -> {
                                stack.clear();
                                stack.trimToSize();
                                return item;
                            })
                    )
                    .addArgsFor(
                            Queue.class,
                            (Queue<Integer> queue) -> List.of((IndexBiFunction<Integer, Integer>) (item, index) -> {
                                queue.clear();
                                queue.trimToSize();
                                return item;
                            })
                    )
                    .addArgsFor(
                            Deque.class,
                            (Deque<Integer> deque) -> List.of((IndexBiFunction<Integer, Integer>) (item, index) -> {
                                deque.clear();
                                deque.trimToSize();
                                return item;
                            })
                    )
                    .addArgsFor(
                            RingBuffer.class,
                            (RingBuffer<Integer> buffer) -> List.of((IndexBiFunction<Integer, Integer>) (item, index) -> {
                                buffer.clear();
                                return item;
                            })
                    )
                    .expectedValue(ConcurrentModificationException.class)
                .build();
    }

    private static Stream<Arguments> provideForCloneAndFilter() {
        return ArgumentsBuilder.of(structureFabrics())
                .newTest()
                    .originStruct()
                    .expectedStruct()
                    .addArgs((IndexBiPredicate<Integer>) (item, index) -> item != null)
                .newTest()
                    .originStruct(new Integer[]{null})
                    .expectedStruct(new Integer[]{null})
                    .addArgs((IndexBiPredicate<Integer>) (item, index) -> item == null)
                .newTest()
                    .originStruct(new Integer[]{null})
                    .expectedStruct()
                    .addArgs((IndexBiPredicate<Integer>) (item, index) -> item != null)
                .newTest()
                    .originStruct(1)
                    .expectedStruct(1)
                    .addArgs((IndexBiPredicate<Integer>) (item, index) -> item == 1)
                .newTest()
                    .originStruct(1)
                    .expectedStruct()
                    .addArgs((IndexBiPredicate<Integer>) (item, index) -> item != 1)
                .newTest()
                    .originStruct(1,2,3,null,5,6,7,8,9,10,11,12,13,14,null,16,17,18,19,null)
                    .expectedStruct(11,12,13,14,16,17,18,19)
                    .addArgs((IndexBiPredicate<Integer>) (item, index) -> item != null && item > 10)
                .build();
    }

    private static Stream<Arguments> provideForCloneAndFilter_Exception() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(1,2,3,4,5,6,7,8,9,10)
                    .addArgsFor(
                            DynamicArray.class,
                            (DynamicArray<Integer> array) -> List.of((IndexBiPredicate<Integer>) (item, index) -> {
                                array.clear();
                                array.trimToSize();
                                return false;
                            })
                    )
                    .addArgsFor(
                            Stack.class,
                            (Stack<Integer> stack) -> List.of((IndexBiPredicate<Integer>) (item, index) -> {
                                stack.clear();
                                stack.trimToSize();
                                return false;
                            })
                    )
                    .addArgsFor(
                            Queue.class,
                            (Queue<Integer> queue) -> List.of((IndexBiPredicate<Integer>) (item, index) -> {
                                queue.clear();
                                queue.trimToSize();
                                return false;
                            })
                    )
                    .addArgsFor(
                            Deque.class,
                            (Deque<Integer> deque) -> List.of((IndexBiPredicate<Integer>) (item, index) -> {
                                deque.clear();
                                deque.trimToSize();
                                return false;
                            })
                    )
                    .addArgsFor(
                            RingBuffer.class,
                            (RingBuffer<Integer> buffer) -> List.of((IndexBiPredicate<Integer>) (item, index) -> {
                                buffer.clear();
                                return false;
                            })
                    )
                    .expectedValue(ConcurrentModificationException.class)
                .build();
    }

    private static Stream<Arguments> provideForReduce() {
        return ArgumentsBuilder.of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs((BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(null)
                .newTest()
                    .originStruct(1)
                    .addArgs((BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(1)
                .newTest()
                    .originStruct(1,2)
                    .addArgs((BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(3)
                .newTest()
                    .originStruct(1,2,3,4,5,6,7,8,9,10)
                    .addArgs((BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(55)
                .build();
    }

    private static Stream<Arguments> provideForReduce_Exception() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(1,2,3,4,5,6,7,8,9,10)
                    .addArgsFor(
                            DynamicArray.class,
                            (DynamicArray<Integer> array) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                array.clear();
                                array.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            Stack.class,
                            (Stack<Integer> stack) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                stack.clear();
                                stack.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            Queue.class,
                            (Queue<Integer> queue) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                queue.clear();
                                queue.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            Deque.class,
                            (Deque<Integer> deque) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                deque.clear();
                                deque.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            RingBuffer.class,
                            (RingBuffer<Integer> buffer) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                buffer.clear();
                                return a + b;
                            })
                    )
                    .expectedValue(ConcurrentModificationException.class)
                .build();
    }

    private static Stream<Arguments> provideForReduceWithInitValue() {
        return ArgumentsBuilder.of(structureFabrics())
                .newTest()
                    .originStruct()
                    .addArgs(100, (BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(100)
                .newTest()
                    .originStruct(1)
                    .addArgs(100, (BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(101)
                .newTest()
                    .originStruct(1,2)
                    .addArgs(100, (BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(103)
                .newTest()
                    .originStruct(1,2,3,4,5,6,7,8,9,10)
                    .addArgs(100, (BinaryOperator<Integer>) Integer::sum)
                    .expectedValue(155)
                .build();
    }

    private static Stream<Arguments> provideForReduceWithInitValue_Exception() {
        return ArgumentsBuilder.<Integer>of(structureFabrics())
                .newTest()
                    .originStruct(1,2,3,4,5,6,7,8,9,10)
                    .addArgs(100)
                    .addArgsFor(
                            DynamicArray.class,
                            (DynamicArray<Integer> array) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                array.clear();
                                array.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            Stack.class,
                            (Stack<Integer> stack) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                stack.clear();
                                stack.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            Queue.class,
                            (Queue<Integer> queue) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                queue.clear();
                                queue.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            Deque.class,
                            (Deque<Integer> deque) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                deque.clear();
                                deque.trimToSize();
                                return a + b;
                            })
                    )
                    .addArgsFor(
                            RingBuffer.class,
                            (RingBuffer<Integer> buffer) -> List.of((BinaryOperator<Integer>) (a, b) -> {
                                buffer.clear();
                                return a + b;
                            })
                    )
                    .expectedValue(ConcurrentModificationException.class)
                .build();
    }
}
