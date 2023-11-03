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
              Consumer<ReadableLinearStructure<Integer>> mutator) {
        mutator.accept(linearStructure);

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
                 Consumer<ReadableLinearStructure<Integer>> mutator) {
        mutator.accept(linearStructure);

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


    private static boolean isExceptionType(Object obj) {
        return obj instanceof Class<?> && Throwable.class.isAssignableFrom((Class<?>)obj);
    }

    private static Stream<Arguments> provideForGetMethod() {
        return Stream.of(
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        -1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        -1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        -1, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        10, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        10, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        10, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        11, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        11, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        11, IndexOutOfBoundsException.class),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        0, 10),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        0, 10),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        0, 10),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        9, 100),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        9, 100),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        9, 100),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        5, 60),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        5, 60),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        5, 60),
                Arguments.of(Array.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        5, null),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        5, null),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        5, null),

                Arguments.of(Array.of(), -1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(), -1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(), -1, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(), 0, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(), 0, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(), 0, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(), 1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(), 1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(), 1, IndexOutOfBoundsException.class),

                Arguments.of(Array.of(1000), -1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(1000), -1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(1000), -1, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(1000), 0, 1000),
                Arguments.of(Stack.of(1000), 0, 1000),
                Arguments.of(Queue.of(1000), 0, 1000),
                Arguments.of(Array.of(1000), 1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(1000), 1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(1000), 1, IndexOutOfBoundsException.class)
        );
    }

    private static Stream<Arguments> provideForAtMethod() {
        return Stream.of(
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        -11, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        -11, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        -11, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        10, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        10, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        10, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        11, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        11, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, null),
                        11, IndexOutOfBoundsException.class),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        0, 10),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        0, 10),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        0, 10),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        9, 100),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        9, 100),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        9, 100),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        5, 60),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        5, 60),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        5, 60),
                Arguments.of(Array.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        5, null),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        5, null),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        5, null),
                Arguments.of(Array.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        -1, 100),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        -1, 100),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        -1, 100),
                Arguments.of(Array.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        -10, 10),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        -10, 10),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, null, 70, 80, 90, 100),
                        -10, 10),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        -5, 60),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        -5, 60),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100),
                        -5, 60),

                Arguments.of(Array.of(), -1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(), -1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(), -1, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(), 0, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(), 0, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(), 0, IndexOutOfBoundsException.class),
                Arguments.of(Array.of(), 1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(), 1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(), 1, IndexOutOfBoundsException.class),

                Arguments.of(Array.of(1000), -1, 1000),
                Arguments.of(Stack.of(1000), -1, 1000),
                Arguments.of(Queue.of(1000), -1, 1000),
                Arguments.of(Array.of(1000), 0, 1000),
                Arguments.of(Stack.of(1000), 0, 1000),
                Arguments.of(Queue.of(1000), 0, 1000),
                Arguments.of(Array.of(1000), 1, IndexOutOfBoundsException.class),
                Arguments.of(Stack.of(1000), 1, IndexOutOfBoundsException.class),
                Arguments.of(Queue.of(1000), 1, IndexOutOfBoundsException.class)
        );
    }

    private static Stream<Arguments> provideForGetFirstMethod() {
        return Stream.of(
                Arguments.of(Array.of(), null),
                Arguments.of(Stack.of(), null),
                Arguments.of(Queue.of(), null),

                Arguments.of(Array.of(1000), 1000),
                Arguments.of(Stack.of(1000), 1000),
                Arguments.of(Queue.of(1000), 1000),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6 ,7 , null, 9), null),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6 ,7 , null, 9), null),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6 ,7 , null, 9), null),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10)
        );
    }

    private static Stream<Arguments> provideForGetLastMethod() {
        return Stream.of(
                Arguments.of(Array.of(), null),
                Arguments.of(Stack.of(), null),
                Arguments.of(Queue.of(), null),

                Arguments.of(Array.of(1000), 1000),
                Arguments.of(Stack.of(1000), 1000),
                Arguments.of(Queue.of(1000), 1000),

                Arguments.of(Array.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), null),
                Arguments.of(Stack.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), null),
                Arguments.of(Queue.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), null),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 100),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 100),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 100)
        );
    }

    private static Stream<Arguments> provideForSizeMethod() {
        Consumer<ReadableLinearStructure<Integer>> mutator = readableLinearStructure -> {};

        return Stream.of(
                Arguments.of(Array.of(), 0, mutator),
                Arguments.of(Stack.of(), 0, mutator),
                Arguments.of(Queue.of(), 0, mutator),

                Arguments.of(Array.of(1000), 1, mutator),
                Arguments.of(Stack.of(1000), 1, mutator),
                Arguments.of(Queue.of(1000), 1, mutator),

                Arguments.of(Array.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), 10, mutator),
                Arguments.of(Stack.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), 10, mutator),
                Arguments.of(Queue.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), 10, mutator),

                Arguments.of(Array.of(0, 1, 2, 3, 4, null, 6, 7, null, null), 7,
                        (Consumer<ReadableLinearStructure<Integer>>) structure -> {
                            Array<Integer> array = (Array<Integer>) structure;
                            for(int i = 0; i < 7; i++) array.orderedRemove(0);
                            for(int i = 0; i < 4; i++) array.append(100);
                        }),
                Arguments.of(Stack.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), 7,
                        (Consumer<ReadableLinearStructure<Integer>>) structure -> {
                            Stack<Integer> stack = (Stack<Integer>) structure;
                            for(int i = 0; i < 7; i++) stack.removeLast();
                            for(int i = 0; i < 4; i++) stack.putLast(1000);
                        }),
                Arguments.of(Queue.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), 7,
                        (Consumer<ReadableLinearStructure<Integer>>) structure -> {
                            Queue<Integer> queue = (Queue<Integer>) structure;
                            for(int i = 0; i < 7; i++) queue.removeFirst();
                            for(int i = 0; i < 4; i++) queue.putLast(1000);
                        })
        );
    }

    private static Stream<Arguments> provideForEmptyMethod() {
        Consumer<ReadableLinearStructure<Integer>> mutator = readableLinearStructure -> {};

        return Stream.of(
                Arguments.of(Array.of(), true, mutator),
                Arguments.of(Stack.of(), true, mutator),
                Arguments.of(Queue.of(), true, mutator),

                Arguments.of(Array.of(1000), false, mutator),
                Arguments.of(Stack.of(1000), false, mutator),
                Arguments.of(Queue.of(1000), false, mutator),

                Arguments.of(Array.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), false, mutator),
                Arguments.of(Stack.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), false, mutator),
                Arguments.of(Queue.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), false, mutator),

                Arguments.of(Array.of(0, 1, 2, 3, 4, null, 6, 7, null, null), false,
                        (Consumer<ReadableLinearStructure<Integer>>) structure -> {
                            Array<Integer> array = (Array<Integer>) structure;
                            for(int i = 0; i < 10; i++) array.orderedRemove(0);
                            for(int i = 0; i < 1; i++) array.append(100);
                        }),
                Arguments.of(Stack.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), false,
                        (Consumer<ReadableLinearStructure<Integer>>) structure -> {
                            Stack<Integer> stack = (Stack<Integer>) structure;
                            for(int i = 0; i < 10; i++) stack.removeLast();
                            for(int i = 0; i < 1; i++) stack.putLast(1000);
                        }),
                Arguments.of(Queue.of(0, 1, 2, 3, 4, null, 6 ,7 , null, null), false,
                        (Consumer<ReadableLinearStructure<Integer>>) structure -> {
                            Queue<Integer> queue = (Queue<Integer>) structure;
                            for(int i = 0; i < 10; i++) queue.removeFirst();
                            for(int i = 0; i < 1; i++) queue.putLast(1000);
                        })
        );
    }

    private static Stream<Arguments> provideForInBoundMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 0, false),
                Arguments.of(Stack.of(), 0, false),
                Arguments.of(Queue.of(), 0, false),

                Arguments.of(Array.of(100), 0, true),
                Arguments.of(Stack.of(100), 0, true),
                Arguments.of(Queue.of(100), 0, true),
                Arguments.of(Array.of(100), 1, false),
                Arguments.of(Stack.of(100), 1, false),
                Arguments.of(Queue.of(100), 1, false),
                Arguments.of(Array.of(100), -1, false),
                Arguments.of(Stack.of(100), -1, false),
                Arguments.of(Queue.of(100), -1, false),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -1, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -1, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -1, false),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10, false),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 11, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 11, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 11, false),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 0, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 0, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 0, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 9, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 9, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 9, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 5, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 5, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 5, true)
        );
    }

    private static Stream<Arguments> provideForInBoundByModuloMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 0, false),
                Arguments.of(Stack.of(), 0, false),
                Arguments.of(Queue.of(), 0, false),

                Arguments.of(Array.of(100), 0, true),
                Arguments.of(Stack.of(100), 0, true),
                Arguments.of(Queue.of(100), 0, true),
                Arguments.of(Array.of(100), 1, false),
                Arguments.of(Stack.of(100), 1, false),
                Arguments.of(Queue.of(100), 1, false),
                Arguments.of(Array.of(100), -1, true),
                Arguments.of(Stack.of(100), -1, true),
                Arguments.of(Queue.of(100), -1, true),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -1, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -1, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -1, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -10, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -10, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -10, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -5, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -5, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -5, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -11, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -11, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), -11, false),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 10, false),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 11, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 11, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 11, false),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 0, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 0, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 0, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 9, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 9, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 9, true),
                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 5, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 5, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 5, true)
        );
    }

    private static Stream<Arguments> provideForLinearSearchMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 1000, -1),
                Arguments.of(Stack.of(), 1000, -1),
                Arguments.of(Queue.of(), 1000, -1),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 3),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 3),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 3),

                Arguments.of(Array.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 3),
                Arguments.of(Stack.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 3),
                Arguments.of(Queue.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 3)
        );
    }

    private static Stream<Arguments> provideForLinearSearchWithPredicateMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 1000, -1),
                Arguments.of(Stack.of(), 1000, -1),
                Arguments.of(Queue.of(), 1000, -1),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 3),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 3),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 3),

                Arguments.of(Array.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 3),
                Arguments.of(Stack.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 3),
                Arguments.of(Queue.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 3)
        );
    }

    private static Stream<Arguments> provideForLinearSearchLastMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 1000, -1),
                Arguments.of(Stack.of(), 1000, -1),
                Arguments.of(Queue.of(), 1000, -1),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, -1),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 6),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 6),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 6),

                Arguments.of(Array.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 6),
                Arguments.of(Stack.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 6),
                Arguments.of(Queue.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 6)
        );
    }

    private static Stream<Arguments> provideForContainsMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 1000, false),
                Arguments.of(Stack.of(), 1000, false),
                Arguments.of(Queue.of(), 1000, false),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, false),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, true),

                Arguments.of(Array.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, true),
                Arguments.of(Stack.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, true),
                Arguments.of(Queue.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, true)
        );
    }

    private static Stream<Arguments> provideForContainsWithPredicateMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 1000, false),
                Arguments.of(Stack.of(), 1000, false),
                Arguments.of(Queue.of(), 1000, false),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, false),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, false),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, false),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, true),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, true),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, true),

                Arguments.of(Array.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, true),
                Arguments.of(Stack.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, true),
                Arguments.of(Queue.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, true)
        );
    }

    private static Stream<Arguments> provideForFrequencyMethod() {
        return Stream.of(
                Arguments.of(Array.of(), 1000, 0),
                Arguments.of(Stack.of(), 1000, 0),
                Arguments.of(Queue.of(), 1000, 0),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, 0),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, 0),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 60, 70, 80, 90, 100), 1000, 0),

                Arguments.of(Array.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 2),
                Arguments.of(Stack.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 2),
                Arguments.of(Queue.of(10, 20, 30, 40, 50, 50, 40, 30, 20, 10), 40, 2),

                Arguments.of(Array.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 2),
                Arguments.of(Stack.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 2),
                Arguments.of(Queue.of(10, 20, 30, null, 50, 50, null, 30, 20, 10), null, 2)
        );
    }

}