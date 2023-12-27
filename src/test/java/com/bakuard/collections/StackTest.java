package com.bakuard.collections;

import com.bakuard.collections.testUtil.Fabric;
import com.bakuard.collections.testUtil.Mutator;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

class StackTest {

    @DisplayName("Stack(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForCopyConstructor1")
    public void Stack_copy(Stack<Integer> origin, Stack<Integer> expected) {
        Stack<Integer> actual = new Stack<>(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Stack(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0},
             change origin and copy after creation
             => expectedOrigin is {1},
                expectedCopy is {2}
            """)
    @MethodSource("provideForCopyConstructor2")
    public void Stack_copy_doNotChangeOrigin(Stack<Integer> origin,
                                             Stack<Integer> expectedOrigin,
                                             Stack<Integer> expectedCopy,
                                             Mutator<Integer, Stack<Integer>> originMutator,
                                             Mutator<Integer, Stack<Integer>> expectedMutator) {
        Stack<Integer> actualCopy = new Stack<>(origin);

        originMutator.mutate(origin);
        expectedMutator.mutate(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @DisplayName("putLast(value):")
    @ParameterizedTest(name = """
             origin is {0},
             value is {1}
             => expected is {2}
            """)
    @MethodSource("provideForPutLast")
    public void putLast(Stack<Integer> origin, Integer value, Stack<Integer> expected) {
        origin.putLast(value);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("putAllOnLast(iterable):")
    @ParameterizedTest(name = """
             origin is {0},
             iterable is {1}
             => expected is {2}
            """)
    @MethodSource("provideForPutAllOnLast_iterable")
    public void putAllOnLast_iterable(Stack<Integer> origin, Iterable<Integer> iterable, Stack<Integer> expected) {
        origin.putAllOnLast(iterable);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("putAllOnLast(data):")
    @ParameterizedTest(name = """
             origin is {0},
             data is {1}
             => expected is {2}
            """)
    @MethodSource("provideForPutAllOnLast")
    public void putAllOnLast(Stack<Integer> origin, Integer[] data, Stack<Integer> expected) {
        origin.putAllOnLast(data);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("removeLast():")
    @ParameterizedTest(name = """
             origin is {0}
             => expectedReturnedValue is {1},
                expected is {2}
            """)
    @MethodSource("provideForRemoveLast")
    public void removeLast(Stack<Integer> origin, Integer expectedReturnedValue, Stack<Integer> expected) {
        Integer actual = origin.removeLast();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedReturnedValue);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @Test
    @DisplayName("""
            tryRemoveLast():
             stack is empty
             => throw exception
            """)
    public void tryRemoveLast_exception() {
        Stack<Integer> stack = new Stack<>();

        Assertions.assertThatExceptionOfType(NoSuchElementException.class).
                isThrownBy(stack::tryRemoveLast);
    }

    @DisplayName("tryRemoveLast():")
    @ParameterizedTest(name = """
             origin is {0}
             => expectedReturnedValue is {1},
                expected is {2}
            """)
    @MethodSource("provideForTryRemoveLast")
    public void tryRemoveLast(Stack<Integer> origin, Integer expectedReturnedValue, Stack<Integer> expected) {
        Integer actual = origin.tryRemoveLast();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedReturnedValue);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("equals(Object o):")
    @ParameterizedTest(name = """
             origin is {0},
             other is {1}
             => expected is {2}
            """)
    @MethodSource("provideForEquals")
    public void equals(Stack<Integer> origin, Stack<Integer> other, boolean expected) {
        boolean actual = origin.equals(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("equals(Object o): idempotence property")
    public void equals_idempotence() {
        Stack<Integer> origin = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(origin.equals(origin)).isTrue();
    }

    @Test
    @DisplayName("equals(Object o): commutative property")
    public void equals_commutative() {
        Stack<Integer> first = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> second = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(first.equals(second) == second.equals(first)).isTrue();
    }

    @Test
    @DisplayName("equals(Object o): transitive property")
    public void equals_transitive() {
        Stack<Integer> first = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> second = Stack.of(10, 20, 30, 40, 50);
        Stack<Integer> third = Stack.of(10, 20, 30, 40, 50);

        Assertions.assertThat(first.equals(second) == second.equals(third) == first.equals(third)).isTrue();
    }


    private static Stream<Arguments> provideForCopyConstructor1() {
        return Stream.of(
                Arguments.of(new Stack<>(), new Stack<>()),
                Arguments.of(Stack.of(100), Stack.of(100)),
                Arguments.of(
                        Stack.of(10,20,30,40,50,60,70,80,90,100),
                        Stack.of(10,20,30,40,50,60,70,80,90,100)
                )
        );
    }

    private static Stream<Arguments> provideForCopyConstructor2() {
        return Stream.of(
                Arguments.of(
                        Stack.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        new Stack<>(),
                        Stack.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        (Mutator<Integer, Stack<Integer>>) Stack::clear,
                        (Mutator<Integer, Stack<Integer>>) array -> {}
                ),
                Arguments.of(
                        Stack.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        Stack.of(0, 1, 2, 23, 24, 212, 604, null, 15),
                        new Stack<>(),
                        (Mutator<Integer, Stack<Integer>>) array -> {},
                        (Mutator<Integer, Stack<Integer>>) Stack::clear
                )
        );
    }

    private static Stream<Arguments> provideForPutLast() {
        return Stream.of(
                Arguments.of(new Stack<>(), 100, Stack.of(100)),
                Arguments.of(new Stack<>(), null, Stack.of(new Integer[]{null})),
                Arguments.of(Stack.of(10,20,30), 100, Stack.of(10,20,30,100)),
                Arguments.of(Stack.of(10,20,30), null, Stack.of(10,20,30,null))
        );
    }

    private static Stream<Arguments> provideForPutAllOnLast_iterable() {
        return Stream.of(
                Arguments.of(new Stack<>(), new Array<>(), new Stack<>()),
                Arguments.of(new Stack<>(), Array.of(100), Stack.of(100)),
                Arguments.of(
                        new Stack<>(),
                        Array.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Array<>(),
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        Array.of(100),
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100)
                ),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        Array.of(100,101,102,110,120,147,177,250),
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100,101,102,110,120,147,177,250)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLast() {
        Fabric<Integer, Stack<Integer>> fabric = data -> {
            Stack<Integer> stack = new Stack<>();
            for(Integer value : data) stack.putLast(value);
            return stack;
        };

        return Stream.of(
                Arguments.of(new Stack<>(), new Integer[0], new Stack<>()),
                Arguments.of(new Stack<>(), new Integer[]{100}, fabric.create(100)),
                Arguments.of(
                        new Stack<>(),
                        new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20},
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[0],
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
                ),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[]{100},
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100)
                ),
                Arguments.of(
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        new Integer[]{100,101,102,110,120,147,177,250},
                        fabric.create(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,100,101,102,110,120,147,177,250)
                )
        );
    }

    private static Stream<Arguments> provideForRemoveLast() {
        return Stream.of(
                Arguments.of(new Stack<>(), null, new Stack<>()),
                Arguments.of(Stack.of(100), 100, new Stack<>()),
                Arguments.of(Stack.of(new Integer[]{null}), null, new Stack<>()),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10),
                        10,
                        Stack.of(0,1,2,3,4,5,6,7,8,9)
                ),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,null),
                        null,
                        Stack.of(0,1,2,3,4,5,6,7,8,9)
                )
        );
    }

    private static Stream<Arguments> provideForTryRemoveLast() {
        return Stream.of(
                Arguments.of(Stack.of(100), 100, new Stack<>()),
                Arguments.of(Stack.of(new Integer[]{null}), null, new Stack<>()),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10),
                        10,
                        Stack.of(0,1,2,3,4,5,6,7,8,9)
                ),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,null),
                        null,
                        Stack.of(0,1,2,3,4,5,6,7,8,9)
                )
        );
    }

    private static Stream<Arguments> provideForEquals() {
        Stack<Integer> stackWithBigEntrySize = new Stack<>();
        for(int i = 0; i < 1000; i++) stackWithBigEntrySize.putLast(i);
        for(int i = 0; i < 1000; i++) stackWithBigEntrySize.removeLast();
        stackWithBigEntrySize.putAllOnLast(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);

        return Stream.of(
                Arguments.of(new Stack<>(), new Stack<>(), true),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        true
                ),
                Arguments.of(
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,null,14,15,16,17,18,19,20),
                        false
                ),
                Arguments.of(
                        stackWithBigEntrySize,
                        Stack.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20),
                        true
                )
        );
    }
}