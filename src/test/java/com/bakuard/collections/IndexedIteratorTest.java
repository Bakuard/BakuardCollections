package com.bakuard.collections;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

class IndexedIteratorTest {

    @DisplayName("hasNext() and next():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => next() return {1} and then throw NoSuchElementException,
                hasNext() return true for each next() value and then return false
            """)
    @MethodSource("provideForNextAndHasNextMethods1")
    void hasNextAndNext1(ReadableLinearStructure<Integer> linearStructure, Integer[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();

        SoftAssertions assertions = new SoftAssertions();
        for(Integer item : expected) {
            assertions.assertThat(iterator.hasNext()).isTrue();
            assertions.assertThat(iterator.next()).isEqualTo(item);
        }
        assertions.assertThat(iterator.hasNext()).isFalse();
        assertions.assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        assertions.assertAll();
    }

    @DisplayName("hasNext() and next():")
    @ParameterizedTest(name = """
             linearStructure is {0},
             linearStructure was updated
             => next() return {1} and then throw NoSuchElementException,
                hasNext() return true for each next() value and then return false
            """)
    @MethodSource("provideForNextAndHasNextMethods2")
    void hasNextAndNext2(ReadableLinearStructure<Integer> linearStructure, Integer[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();

        SoftAssertions assertions = new SoftAssertions();
        for(Integer item : expected) {
            assertions.assertThat(iterator.hasNext()).isTrue();
            assertions.assertThat(iterator.next()).isEqualTo(item);
        }
        assertions.assertThat(iterator.hasNext()).isFalse();
        assertions.assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        assertions.assertAll();
    }

    @DisplayName("hasPrevious() and previous():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => previous() return {1} and then throw NoSuchElementException,
                hasPrevious() return true for each previous() value and then return false
            """)
    @MethodSource("provideForPreviousAndHasPreviousMethods1")
    void hasPreviousAndPrevious1(ReadableLinearStructure<Integer> linearStructure, Integer[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();
        iterator.afterLast();

        SoftAssertions assertions = new SoftAssertions();
        for(Integer item : expected) {
            assertions.assertThat(iterator.hasPrevious()).isTrue();
            assertions.assertThat(iterator.previous()).isEqualTo(item);
        }
        assertions.assertThat(iterator.hasPrevious()).isFalse();
        assertions.assertThatThrownBy(iterator::previous).isInstanceOf(NoSuchElementException.class);
        assertions.assertAll();
    }

    @DisplayName("hasPrevious() and previous():")
    @ParameterizedTest(name = """
             linearStructure is {0},
             linearStructure was updated
             => previous() return {1} and then throw NoSuchElementException,
                hasPrevious() return true for each previous() value and then return false
            """)
    @MethodSource("provideForPreviousAndHasPreviousMethods2")
    void hasPreviousAndPrevious2(ReadableLinearStructure<Integer> linearStructure, Integer[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();
        iterator.afterLast();

        SoftAssertions assertions = new SoftAssertions();
        for(Integer item : expected) {
            assertions.assertThat(iterator.hasPrevious()).isTrue();
            assertions.assertThat(iterator.previous()).isEqualTo(item);
        }
        assertions.assertThat(iterator.hasPrevious()).isFalse();
        assertions.assertThatThrownBy(iterator::previous).isInstanceOf(NoSuchElementException.class);
        assertions.assertAll();
    }

    @DisplayName("jump(itemsNumber) and canJump(itemsNumber):")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForJumpAndCanJumpMethods")
    void canJumpAndJump(ReadableLinearStructure<Integer> linearStructure, ResultOfJumpMethod[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();

        SoftAssertions assertions = new SoftAssertions();
        for(ResultOfJumpMethod result : expected) {
            if(result.isResultExceptionType()) {
                assertions.assertThat(iterator.canJump(result.itemsNumber())).isFalse();
                assertions.assertThatThrownBy(() -> iterator.jump(result.itemsNumber())).
                        isInstanceOf(result.getResultAsClass());
            } else {
                assertions.assertThat(iterator.canJump(result.itemsNumber())).isTrue();
                assertions.assertThat(iterator.jump(result.itemsNumber())).isEqualTo(result.expectedResult());
            }
        }
        assertions.assertAll();
    }

    @DisplayName("recentIndex() and next():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForRecentIndexAndNextMethods")
    void recentIndexAndNext(ReadableLinearStructure<Integer> linearStructure, Integer[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(iterator.recentIndex()).isEqualTo(-1);
        for(Integer item : expected) {
            iterator.next();
            assertions.assertThat(iterator.recentIndex()).isEqualTo(item);
        }
        assertions.assertAll();
    }

    @DisplayName("recentIndex() and previous():")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForRecentIndexAndPreviousMethods")
    void recentIndexAndPrevious(ReadableLinearStructure<Integer> linearStructure, Integer[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();
        iterator.afterLast();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(iterator.recentIndex()).isEqualTo(-1);
        for(Integer item : expected) {
            iterator.previous();
            assertions.assertThat(iterator.recentIndex()).isEqualTo(item);
        }
        assertions.assertAll();
    }

    @DisplayName("recentIndex() and jump(itemsNumber):")
    @ParameterizedTest(name = """
             linearStructure is {0}
             => expected {1}
            """)
    @MethodSource("provideForRecentIndexAndJumpMethods")
    void recentIndexAndJump(ReadableLinearStructure<Integer> linearStructure, ResultOfJumpMethod[] expected) {
        IndexedIterator<Integer> iterator = linearStructure.iterator();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(iterator.recentIndex()).isEqualTo(-1);
        for(ResultOfJumpMethod result : expected) {
            iterator.jump(result.itemsNumber());
            assertions.assertThat(iterator.recentIndex()).isEqualTo(result.expectedResult());
        }
        assertions.assertAll();
    }


    private record ResultOfJumpMethod(int itemsNumber, Object expectedResult) {

        public static ResultOfJumpMethod[] by(Object... itemsNumberAndResult) {
            ResultOfJumpMethod[] result = new ResultOfJumpMethod[itemsNumberAndResult.length / 2];
            for(int i = 0; i < itemsNumberAndResult.length / 2; i++) {
                result[i] = new ResultOfJumpMethod(
                        (int) itemsNumberAndResult[i*2],
                        itemsNumberAndResult[i*2 + 1]
                );
            }
            return result;
        }

        public boolean isResultExceptionType() {
            return expectedResult instanceof Class<?> &&
                    Throwable.class.isAssignableFrom((Class<?>)expectedResult);
        }

        public Class<Throwable> getResultAsClass() {
            return (Class<Throwable>) expectedResult;
        }

    }

    //linear structures and itemsNumber for jump(itemsNumber) and canJump(itemsNumber) methods.
    private static Stream<Arguments> provideForJumpAndCanJumpMethods() {
        return Stream.of(
                Arguments.of(new Array<>(), ResultOfJumpMethod.by(0, NoSuchElementException.class)),
                Arguments.of(new Stack<>(), ResultOfJumpMethod.by(0, NoSuchElementException.class)),
                Arguments.of(new Queue<>(), ResultOfJumpMethod.by(0, NoSuchElementException.class)),

                Arguments.of(new Array<>(), ResultOfJumpMethod.by(1, NoSuchElementException.class)),
                Arguments.of(new Stack<>(), ResultOfJumpMethod.by(1, NoSuchElementException.class)),
                Arguments.of(new Queue<>(), ResultOfJumpMethod.by(1, NoSuchElementException.class)),

                Arguments.of(new Array<>(), ResultOfJumpMethod.by(-1, NoSuchElementException.class)),
                Arguments.of(new Stack<>(), ResultOfJumpMethod.by(-1, NoSuchElementException.class)),
                Arguments.of(new Queue<>(), ResultOfJumpMethod.by(-1, NoSuchElementException.class)),

                Arguments.of(Array.of(10), ResultOfJumpMethod.by(0, NoSuchElementException.class)),
                Arguments.of(Stack.of(10), ResultOfJumpMethod.by(0, NoSuchElementException.class)),
                Arguments.of(Queue.of(10), ResultOfJumpMethod.by(0, NoSuchElementException.class)),

                Arguments.of(Array.of(10), ResultOfJumpMethod.by(-1, NoSuchElementException.class)),
                Arguments.of(Stack.of(10), ResultOfJumpMethod.by(-1, NoSuchElementException.class)),
                Arguments.of(Queue.of(10), ResultOfJumpMethod.by(-1, NoSuchElementException.class)),

                Arguments.of(Array.of(10), ResultOfJumpMethod.by(1, 10)),
                Arguments.of(Stack.of(10), ResultOfJumpMethod.by(1, 10)),
                Arguments.of(Queue.of(10), ResultOfJumpMethod.by(1, 10)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(0, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(0, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(0, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                1, null,
                                1, 1,
                                1, 2,
                                1, 3,
                                1, 4,
                                1, null,
                                1, 6,
                                1, 7,
                                1, null,
                                1, null,
                                1, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                1, null,
                                1, 1,
                                1, 2,
                                1, 3,
                                1, 4,
                                1, null,
                                1, 6,
                                1, 7,
                                1, null,
                                1, null,
                                1, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                1, null,
                                1, 1,
                                1, 2,
                                1, 3,
                                1, 4,
                                1, null,
                                1, 6,
                                1, 7,
                                1, null,
                                1, null,
                                1, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                2, 1,
                                2, 3,
                                2, null,
                                2, 7,
                                2, null,
                                2, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                2, 1,
                                2, 3,
                                2, null,
                                2, 7,
                                2, null,
                                2, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                2, 1,
                                2, 3,
                                2, null,
                                2, 7,
                                2, null,
                                2, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                5, 4,
                                5, null,
                                1, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                5, 4,
                                5, null,
                                1, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                5, 4,
                                5, null,
                                1, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                10, null,
                                1, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                10, null,
                                1, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                10, null,
                                1, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(-1, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(-1, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(-1, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(11, NoSuchElementException.class)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(11, NoSuchElementException.class)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(11, NoSuchElementException.class)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(3, 2,
                                -1, 1,
                                1, 2,
                                -1, 1,
                                1, 2,
                                -1, 1,
                                1, 2,
                                -1, 1,
                                -1, null,
                                -1, NoSuchElementException.class)),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(10, null,
                                -9, null,
                                -100, NoSuchElementException.class)),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(10, null,
                                -10, NoSuchElementException.class)),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(10, null,
                                -1, null,
                                -1, 7,
                                -1, 6,
                                -1, null,
                                -1, 4,
                                -1, 3,
                                -1, 2,
                                -1, 1,
                                -1, null,
                                -1, NoSuchElementException.class))
        );
    }

    //linear structures for hasNext() and next() methods
    private static Stream<Arguments> provideForNextAndHasNextMethods1() {
        return Stream.of(
                Arguments.of(Array.of(), new Integer[0]),
                Arguments.of(Stack.of(), new Integer[0]),
                Arguments.of(Queue.of(), new Integer[0]),
                Arguments.of(Array.of(10), new Integer[]{10}),
                Arguments.of(Stack.of(10), new Integer[]{10}),
                Arguments.of(Queue.of(10), new Integer[]{10}),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                             new Integer[]{null, 1, 2, 3, 4, null, 6, 7, null, null}),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                             new Integer[]{null, 1, 2, 3, 4, null, 6, 7, null, null}),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                             new Integer[]{null, 1, 2, 3, 4, null, 6, 7, null, null})
        );
    }

    //updated linear structures for hasNext()  next() methods
    private static Stream<Arguments> provideForNextAndHasNextMethods2() {
        Queue<Integer> queue = Queue.of(1, 2, 3, 4, 5, null, 1, 2, 3, 4);
        Integer[] expected = {null, 1, 2, 3, 4, null, 6, 7, null, null};

        for(int i = 5; i < 10; i++) {
            queue.removeFirst();
            queue.putLast(expected[i]);
        }

        return Stream.of(Arguments.of(queue, expected));
    }

    //linear structures for hasPrevious() and previous() methods
    private static Stream<Arguments> provideForPreviousAndHasPreviousMethods1() {
        return Stream.of(
                Arguments.of(Array.of(), new Integer[0]),
                Arguments.of(Stack.of(), new Integer[0]),
                Arguments.of(Queue.of(), new Integer[0]),
                Arguments.of(Array.of(10), new Integer[]{10}),
                Arguments.of(Stack.of(10), new Integer[]{10}),
                Arguments.of(Queue.of(10), new Integer[]{10}),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{null, null, 7, 6, null, 4, 3, 2, 1, null}),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{null, null, 7, 6, null, 4, 3, 2, 1, null}),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{null, null, 7, 6, null, 4, 3, 2, 1, null})
        );
    }

    //updated linear structures for hasPrevious() and previous() methods
    private static Stream<Arguments> provideForPreviousAndHasPreviousMethods2() {
        Queue<Integer> queue = Queue.of(null, 6, 7, null, null, null, 1, 2, 3, 4);
        Integer[] expected = {null, null, 7, 6, null, 4, 3, 2, 1, null};

        for(int i = 4; i >= 0; --i) {
            queue.removeFirst();
            queue.putLast(expected[i]);
        }

        return Stream.of(Arguments.of(queue, expected));
    }

    //linear structures for recentIndex() and next()
    private static Stream<Arguments> provideForRecentIndexAndNextMethods() {
        return Stream.of(
                Arguments.of(Array.of(), new Integer[0]),
                Arguments.of(Stack.of(), new Integer[0]),
                Arguments.of(Queue.of(), new Integer[0]),
                Arguments.of(Array.of(10), new Integer[]{0}),
                Arguments.of(Stack.of(10), new Integer[]{0}),
                Arguments.of(Queue.of(10), new Integer[]{0}),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
        );
    }

    //linear structures for recentIndex() and previous()
    private static Stream<Arguments> provideForRecentIndexAndPreviousMethods() {
        return Stream.of(
                Arguments.of(Array.of(), new Integer[0]),
                Arguments.of(Stack.of(), new Integer[0]),
                Arguments.of(Queue.of(), new Integer[0]),
                Arguments.of(Array.of(10), new Integer[]{0}),
                Arguments.of(Stack.of(10), new Integer[]{0}),
                Arguments.of(Queue.of(10), new Integer[]{0}),
                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0})
        );
    }

    //linear structures for recentIndex() and jump(itemsNumber)
    private static Stream<Arguments> provideForRecentIndexAndJumpMethods() {
        return Stream.of(
                Arguments.of(new Array<>(), ResultOfJumpMethod.by()),
                Arguments.of(new Stack<>(), ResultOfJumpMethod.by()),
                Arguments.of(new Queue<>(), ResultOfJumpMethod.by()),

                Arguments.of(Array.of(10), ResultOfJumpMethod.by(1, 0)),
                Arguments.of(Stack.of(10), ResultOfJumpMethod.by(1, 0)),
                Arguments.of(Queue.of(10), ResultOfJumpMethod.by(1, 0)),

                Arguments.of(Array.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                1, 0,
                                3, 3,
                                -2, 1,
                                7, 8,
                                -1, 7,
                                -7, 0)),
                Arguments.of(Stack.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                1, 0,
                                3, 3,
                                -2, 1,
                                7, 8,
                                -1, 7,
                                -7, 0)),
                Arguments.of(Queue.of(null, 1, 2, 3, 4, null, 6, 7, null, null),
                        ResultOfJumpMethod.by(
                                1, 0,
                                3, 3,
                                -2, 1,
                                7, 8,
                                -1, 7,
                                -7, 0))
        );
    }

}