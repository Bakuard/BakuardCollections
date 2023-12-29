package com.bakuard.collections;

import com.bakuard.collections.testUtil.Mutator;
import com.bakuard.collections.testUtil.Pair;
import com.bakuard.collections.testUtil.StructAndMutator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        if(isExceptionType(expected)) {
            Assertions.assertThatThrownBy(() -> linearStructure.get(index)).isInstanceOf((Class<?>) expected);
        } else {
            Integer actual = linearStructure.get(index);

            Assertions.assertThat(actual).isEqualTo(expected);
        }
    }

    @DisplayName("at(index):")
    @ParameterizedTest(name = """
             linearStructure is {0},
             index = {1}
             => expected {2}
            """)
    @MethodSource("provideForAtMethod")
    void at(ReadableLinearStructure<Integer> linearStructure, int index, Object expected) {
        if(isExceptionType(expected)) {
            Assertions.assertThatThrownBy(() -> linearStructure.at(index)).isInstanceOf((Class<?>) expected);
        } else {
            Integer actual = linearStructure.at(index);

            Assertions.assertThat(actual).isEqualTo(expected);
        }
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
    void size(ReadableLinearStructure<Integer> linearStructure, Integer expected,
              Mutator<Integer, ReadableLinearStructure<Integer>> mutator) {
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
    void isEmpty(ReadableLinearStructure<Integer> linearStructure, boolean expected,
                 Mutator<Integer, ReadableLinearStructure<Integer>> mutator) {
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


    private static boolean isExceptionType(Object obj) {
        return obj instanceof Class<?> && Throwable.class.isAssignableFrom((Class<?>)obj);
    }

    private static <T> Stream<ReadableLinearStructure<T>> structures(T... data) {
        return Stream.of(
                DynamicArray.of(data),
                Stack.of(data),
                Queue.of(data),
                RingBuffer.of(data.length, data)
        );
    }

    private static <T> Stream<StructAndMutator<T, ? extends ReadableLinearStructure<T>>> structuresWithMutators(
            T[] data, int removedItemsNumber, T[] addedData) {
        return Stream.of(
                new StructAndMutator<>(
                        DynamicArray.of(data),
                        array -> {
                            for(int i = 0; i < removedItemsNumber; i++) array.orderedRemove(0);
                            for(T item : addedData) array.append(item);
                        }
                ),
                new StructAndMutator<>(
                        Stack.of(data),
                        stack -> {
                            for(int i = 0; i < removedItemsNumber; i++) stack.removeLast();
                            for(T item : addedData) stack.putLast(item);
                        }
                ),
                new StructAndMutator<>(
                        Queue.of(data),
                        queue -> {
                            for(int i = 0; i < removedItemsNumber; i++) queue.removeFirst();
                            for(T item : addedData) queue.putLast(item);
                        }
                ),
                new StructAndMutator<>(
                        RingBuffer.of(data.length, data),
                        buffer -> {
                            for(int i = 0; i < removedItemsNumber; i++) buffer.removeFirst();
                            for(T item : addedData) buffer.putLastOrReplace(item);
                        }
                )
        );
    }

    private static Stream<Arguments> provideForGetMethod() {
        return Stream.of(
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, -1, IndexOutOfBoundsException.class)),
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, 10, IndexOutOfBoundsException.class)),
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, 11, IndexOutOfBoundsException.class)),

                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 0, 10)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 9, 100)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 5, 60)),
                structures(10, 20, 30, 40, 50, null, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 5, null)),

                structures().map(struct -> Arguments.of(struct, -1, IndexOutOfBoundsException.class)),
                structures().map(struct -> Arguments.of(struct, 0, IndexOutOfBoundsException.class)),
                structures().map(struct -> Arguments.of(struct, 1, IndexOutOfBoundsException.class)),

                structures(1000).map(struct -> Arguments.of(struct, -1, IndexOutOfBoundsException.class)),
                structures(1000).map(struct -> Arguments.of(struct, 0, 1000)),
                structures(1000).map(struct -> Arguments.of(struct, 1, IndexOutOfBoundsException.class))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForAtMethod() {
        return Stream.of(
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, -11, IndexOutOfBoundsException.class)),
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, 10, IndexOutOfBoundsException.class)),
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, 11, IndexOutOfBoundsException.class)),

                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 0, 10)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 9, 100)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 5, 60)),
                structures(10, 20, 30, 40, 50, null, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 5, null)),
                structures(10, 20, 30, 40, 50, null, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -1, 100)),
                structures(10, 20, 30, 40, 50, null, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -10, 10)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -5, 60)),

                structures().map(struct -> Arguments.of(struct, -1, IndexOutOfBoundsException.class)),
                structures().map(struct -> Arguments.of(struct, 0, IndexOutOfBoundsException.class)),
                structures().map(struct -> Arguments.of(struct, 1, IndexOutOfBoundsException.class)),

                structures(1000).map(struct -> Arguments.of(struct, -1, 1000)),
                structures(1000).map(struct -> Arguments.of(struct, 0, 1000)),
                structures(1000).map(struct -> Arguments.of(struct, 1, IndexOutOfBoundsException.class))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForGetFirstMethod() {
        return Stream.of(
                structures().
                        map(struct -> Arguments.of(struct, null)),
                structures(1000).
                        map(struct -> Arguments.of(struct, 1000)),
                structures(null, 1, 2, 3, 4, null, 6 ,7 , null, 9).
                        map(struct -> Arguments.of(struct, null)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 10))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForGetLastMethod() {
        return Stream.of(
                structures().
                        map(struct -> Arguments.of(struct, null)),
                structures(1000).
                        map(struct -> Arguments.of(struct, 1000)),
                structures(0, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, null)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 100))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForSizeMethod() {
        Mutator<Integer, ReadableLinearStructure<Integer>> mutator = struct -> {};
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 0, mutator)),
                structures(1000).map(struct -> Arguments.of(struct, 1, mutator)),
                structures(0, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, 10, mutator)),
                structuresWithMutators(new Integer[]{0, 1, 2, 3, 4, null, 6, 7, null, null},
                        7,
                        new Integer[] {100, 100, 100, 100}).
                        map(structAndMutator -> Arguments.of(structAndMutator.struct(), 7, structAndMutator.mutator()))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForEmptyMethod() {
        Mutator<Integer, ReadableLinearStructure<Integer>> mutator = struct -> {};
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, true, mutator)),
                structures(1000).map(struct -> Arguments.of(struct, false, mutator)),
                structures(0, 1, 2, 3, 4, null, 6 ,7 , null, null).
                        map(struct -> Arguments.of(struct, false, mutator)),
                structuresWithMutators(new Integer[]{0, 1, 2, 3, 4, null, 6, 7, null, null},
                        7,
                        new Integer[] {100, 100, 100, 100}).
                        map(structAndMutator -> Arguments.of(structAndMutator.struct(), false, structAndMutator.mutator()))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForInBoundMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 0, false)),

                structures(100).map(struct -> Arguments.of(struct, 0, true)),
                structures(100).map(struct -> Arguments.of(struct, 1, false)),
                structures(100).map(struct -> Arguments.of(struct, -1, false)),

                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -1, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 10, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 11, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 0, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 9, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 5, true))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForInBoundByModuloMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 0, false)),

                structures(100).map(struct -> Arguments.of(struct, 0, true)),
                structures(100).map(struct -> Arguments.of(struct, 1, false)),
                structures(100).map(struct -> Arguments.of(struct, -1, true)),

                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -1, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -10, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -5, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, -11, false)),

                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 10, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 11, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 0, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 9, true)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 5, true))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForLinearSearchMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 1000, -1)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 1000, -1)),
                structures(10, 20, 30, 40, 50, 50, 40, 30, 20, 10).
                        map(struct -> Arguments.of(struct, 40, 3)),
                structures(10, 20, 30, null, 50, 50, null, 30, 20, 10).
                        map(struct -> Arguments.of(struct, null, 3))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForLinearSearchWithPredicateMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 1000, -1)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 1000, -1)),
                structures(10, 20, 30, 40, 50, 50, 40, 30, 20, 10).
                        map(struct -> Arguments.of(struct, 40, 3)),
                structures(10, 20, 30, null, 50, 50, null, 30, 20, 10).
                        map(struct -> Arguments.of(struct, null, 3))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForLinearSearchLastMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 1000, -1)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 1000, -1)),
                structures(10, 20, 30, 40, 50, 50, 40, 30, 20, 10).
                        map(struct -> Arguments.of(struct, 40, 6)),
                structures(10, 20, 30, null, 50, 50, null, 30, 20, 10).
                        map(struct -> Arguments.of(struct, null, 6))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForContainsMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 1000, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 1000, false)),
                structures(10, 20, 30, 40, 50, 50, 40, 30, 20, 10).
                        map(struct -> Arguments.of(struct, 40, true)),
                structures(10, 20, 30, null, 50, 50, null, 30, 20, 10).
                        map(struct -> Arguments.of(struct, null, true))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForContainsWithPredicateMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 1000, false)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 1000, false)),
                structures(10, 20, 30, 40, 50, 50, 40, 30, 20, 10).
                        map(struct -> Arguments.of(struct, 40, true)),
                structures(10, 20, 30, null, 50, 50, null, 30, 20, 10).
                        map(struct -> Arguments.of(struct, null, true))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForFrequencyMethod() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, 1000, 0)),
                structures(10, 20, 30, 40, 50, 60, 70, 80, 90, 100).
                        map(struct -> Arguments.of(struct, 1000, 0)),
                structures(10, 20, 30, 40, 50, 50, 40, 30, 20, 10).
                        map(struct -> Arguments.of(struct, 40, 2)),
                structures(10, 20, 30, null, 50, 50, null, 30, 20, 10).
                        map(struct -> Arguments.of(struct, null, 2))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForEachWithIndex() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, List.of())),
                structures(10).map(struct -> Arguments.of(struct, List.of(Pair.of(10, 0)))),
                structures(new Integer[]{null}).map(struct -> Arguments.of(struct, List.of(Pair.of(null, 0)))),
                structures(null, 10, 20, 30, 40, null, 60 ,70 , null, null, 100, 110, 120, 130, 140)
                        .map(struct -> Arguments.of(struct, List.of(
                                Pair.of(null, 0), Pair.of(10, 1), Pair.of(20, 2), Pair.of(30, 3), Pair.of(40, 4),
                                Pair.of(null, 5), Pair.of(60, 6), Pair.of(70, 7), Pair.of(null, 8), Pair.of(null, 9),
                                Pair.of(100, 10), Pair.of(110, 11), Pair.of(120, 12), Pair.of(130, 13), Pair.of(140, 14)
                        )))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForToArray() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, new Integer[0])),
                structures(new Integer[]{null}).map(struct -> Arguments.of(struct,new Integer[]{null})),
                structures(100).map(struct -> Arguments.of(struct,new Integer[]{100})),
                structures(0,10).map(struct -> Arguments.of(struct,new Integer[]{0,10})),
                structures(0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150)
                        .map(struct -> Arguments.of(struct,new Integer[]{0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150}))
        ).flatMap(stream -> stream);
    }
}
