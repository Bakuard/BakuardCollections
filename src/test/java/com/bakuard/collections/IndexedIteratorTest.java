package com.bakuard.collections;

import com.bakuard.collections.testUtil.Mutator;
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
    @MethodSource("provideForNextAndHasNextMethods")
    void hasNextAndNext(ReadableLinearStructure<Integer> linearStructure, Integer[] expected,
                         Mutator<Integer, ReadableLinearStructure<Integer>> mutator) {
        mutator.mutate(linearStructure);
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
    @MethodSource("provideForPreviousAndHasPreviousMethods")
    void hasPreviousAndPrevious(ReadableLinearStructure<Integer> linearStructure, Integer[] expected,
                                Mutator<Integer, ReadableLinearStructure<Integer>> mutator) {
        mutator.mutate(linearStructure);
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
            if(result.isException()) {
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

        public boolean isException() {
            return expectedResult instanceof Class<?> &&
                    Throwable.class.isAssignableFrom((Class<?>)expectedResult);
        }

        public Class<Throwable> getResultAsClass() {
            return (Class<Throwable>) expectedResult;
        }

    }

    private static <T> Stream<ReadableLinearStructure<T>> structures(T... data) {
        return Stream.of(
                Array.of(data),
                Stack.of(data),
                Queue.of(data),
                RingBuffer.of(data)
        );
    }

    private static Stream<Arguments> provideForJumpAndCanJumpMethods() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(0, NoSuchElementException.class))),
                structures().map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(1, NoSuchElementException.class))),
                structures().map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(-1, NoSuchElementException.class))),

                structures(10).map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(0, NoSuchElementException.class))),
                structures(10).map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(-1, NoSuchElementException.class))),
                structures(10).map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(1, 10))),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(0, NoSuchElementException.class))),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
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
                                        1, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(
                                        2, 1,
                                        2, 3,
                                        2, null,
                                        2, 7,
                                        2, null,
                                        2, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(
                                        5, 4,
                                        5, null,
                                        1, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(
                                        10, null,
                                        1, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(-1, NoSuchElementException.class))),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(11, NoSuchElementException.class))),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(3, 2,
                                        -1, 1,
                                        1, 2,
                                        -1, 1,
                                        1, 2,
                                        -1, 1,
                                        1, 2,
                                        -1, 1,
                                        -1, null,
                                        -1, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(10, null,
                                        -9, null,
                                        -100, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(10, null,
                                        -10, NoSuchElementException.class)
                                )
                        ),

                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
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
                                        -1, NoSuchElementException.class)
                                )
                        )
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForNextAndHasNextMethods() {
        Mutator<Integer, ReadableLinearStructure<Integer>> mutator = struct -> {};
        Integer[] expected = {null, 1, 2, 3, 4, null, 6, 7, null, null};
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, new Integer[0], mutator)),
                structures(10).map(struct -> Arguments.of(struct, new Integer[]{10}, mutator)),
                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, expected, mutator)),
                Stream.of(
                    Arguments.of(
                            Queue.of(10, 10, 10, 10, 10, null, 1, 2, 3, 4),
                            expected,
                            (Mutator<Integer, Queue<Integer>>) queue -> {
                                for(int i = 5; i < 10; i++) {
                                    queue.removeFirst();
                                    queue.putLast(expected[i]);
                                }
                            }
                    ),
                    Arguments.of(
                            RingBuffer.of(10, 10, 10, 10, 10, null, 1, 2, 3, 4),
                            expected,
                            (Mutator<Integer, RingBuffer<Integer>>) buffer -> {
                                for(int i = 5; i < 10; i++) buffer.putLastOrReplace(expected[i]);
                            }
                    )
                )
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForPreviousAndHasPreviousMethods() {
        Mutator<Integer, ReadableLinearStructure<Integer>> mutator = struct -> {};
        Integer[] expected = {null, null, 7, 6, null, 4, 3, 2, 1, null};
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, new Integer[0], mutator)),
                structures(10).map(struct -> Arguments.of(struct, new Integer[]{10}, mutator)),
                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, expected, mutator)),
                Stream.of(
                        Arguments.of(
                                Queue.of(10, 10, 10, 10, 10, null, 1, 2, 3, 4),
                                expected,
                                (Mutator<Integer, Queue<Integer>>) queue -> {
                                    for(int i = 4; i >= 0; i--) {
                                        queue.removeFirst();
                                        queue.putLast(expected[i]);
                                    }
                                }
                        ),
                        Arguments.of(
                                RingBuffer.of(10, 10, 10, 10, 10, null, 1, 2, 3, 4),
                                expected,
                                (Mutator<Integer, RingBuffer<Integer>>) buffer -> {
                                   buffer.putAllOnLastOrReplace(null, 6, 7, null, null);
                                }
                        )
                )
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForRecentIndexAndNextMethods() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, new Integer[0])),
                structures(10).map(struct -> Arguments.of(struct, new Integer[]{0})),
                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForRecentIndexAndPreviousMethods() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, new Integer[0])),
                structures(10).map(struct -> Arguments.of(struct, new Integer[]{0})),
                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct, new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}))
        ).flatMap(stream -> stream);
    }

    private static Stream<Arguments> provideForRecentIndexAndJumpMethods() {
        return Stream.of(
                structures().map(struct -> Arguments.of(struct, ResultOfJumpMethod.by())),
                structures(10).map(struct -> Arguments.of(struct, ResultOfJumpMethod.by(1, 0))),
                structures(null, 1, 2, 3, 4, null, 6, 7, null, null).
                        map(struct -> Arguments.of(struct,
                                ResultOfJumpMethod.by(
                                        1, 0,
                                        3, 3,
                                        -2, 1,
                                        7, 8,
                                        -1, 7,
                                        -7, 0)
                                )
                        )
        ).flatMap(stream -> stream);
    }

}