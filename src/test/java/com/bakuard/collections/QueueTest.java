package com.bakuard.collections;

import com.bakuard.collections.testUtil.Fabric;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class QueueTest {

    @DisplayName("Queue(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForQueueConstructor1")
    void Queue_copy(Queue<Integer> origin, Queue<Integer> expected) {
        Queue<Integer> actual = new Queue<>(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Queue(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0},
             change origin and copy after creation
             => expectedOrigin {1},
                expectedCopy {2}
            """)
    @MethodSource("provideForQueueConstructor2")
    void Queue_copy_doNotChangeOrigin(Queue<Integer> origin,
                                      Queue<Integer> expectedOrigin,
                                      Queue<Integer> expectedCopy,
                                      Consumer<Queue<Integer>> originMutator,
                                      Consumer<Queue<Integer>> copyMutator) {
        Queue<Integer> actualCopy = new Queue<>(origin);

        originMutator.accept(origin);
        copyMutator.accept(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @DisplayName("Queue(iterable):")
    @ParameterizedTest(name = """
             iterable is {0}
             => expected {1}
            """)
    @MethodSource("provideForConstructorWithIterable")
    public void Queue_iterable(Iterable<Integer> iterable, Queue<Integer> expected) {
        Queue<Integer> actual = new Queue<>(iterable);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("of(...data):")
    @ParameterizedTest(name = """
             data is {0}
             => expected {1}
            """)
    @MethodSource("provideForOf1")
    void of(Integer[] data, Queue<Integer> expected) {
        Queue<Integer> actual = Queue.of(data);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("of(...data): data and queue must be independent of each other")
    @ParameterizedTest(name = """
             data is {0},
             change data and queue after creation
             => expected data {1},
                expected queue {2}
            """)
    @MethodSource("provideForOf2")
    void of_doNotChangeOrigin(Integer[] data,
                              Integer[] expectedData,
                              Queue<Integer> expectedQueue,
                              Consumer<Integer[]> dataMutator,
                              Consumer<Queue<Integer>> queueMutator) {
        Queue<Integer> actual = Queue.of(data);

        dataMutator.accept(data);
        queueMutator.accept(actual);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedQueue);
        assertions.assertThat(data).isEqualTo(expectedData);
        assertions.assertAll();
    }

    @DisplayName("putLast(value):")
    @ParameterizedTest(name = """
             origin queue is {0}
             value is {1}
             => expected queue is {2}
            """)
    @MethodSource("provideForPutLast")
    void putLast(Queue<Integer> originQueue, Integer addedValue, Queue<Integer> expectedQueue) {
        originQueue.putLast(addedValue);

        Assertions.assertThat(originQueue).isEqualTo(expectedQueue);
    }

    @DisplayName("putAllOnLast(iterable):")
    @ParameterizedTest(name = """
             origin queue is {0}
             iterable is {1}
             => expected queue is {2}
            """)
    @MethodSource("provideForPutAllOnLastIterable")
    void putAllOnLast_Iterable(Queue<Integer> originQueue, Iterable<Integer> iterable, Queue<Integer> expectedQueue) {
        originQueue.putAllOnLast(iterable);

        Assertions.assertThat(originQueue).isEqualTo(expectedQueue);
    }

    @DisplayName("putAllOnLast(data):")
    @ParameterizedTest(name = """
             origin queue is {0}
             data is {1}
             => expected queue is {2}
            """)
    @MethodSource("provideForPutAllOnLastData")
    void putAllOnLast_Data(Queue<Integer> originQueue, Integer[] data, Queue<Integer> expectedQueue) {
        originQueue.putAllOnLast(data);

        Assertions.assertThat(originQueue).isEqualTo(expectedQueue);
    }

    @DisplayName("removeFirst():")
    @ParameterizedTest(name = """
             origin queue is {0}
             => expected queue is {1}
                returned item is {2}
            """)
    @MethodSource("provideForRemoveFirst")
    void removeFirst(Queue<Integer> originQueue, Queue<Integer> expectedQueue, Integer expectedItem) {
        Integer actualItem = originQueue.removeFirst();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(originQueue).isEqualTo(expectedQueue);
        assertions.assertThat(actualItem).isEqualTo(expectedItem);
        assertions.assertAll();
    }

    @DisplayName("tryRemoveFirst():")
    @ParameterizedTest(name = """
             origin queue is {0}
             => putLastOrReplace is {1}
                returned item is {2}
            """)
    @MethodSource("provideForTryRemoveFirst")
    void tryRemoveFirst(Queue<Integer> originQueue, Queue<Integer> expectedQueue, Integer expectedItem) {
        Integer actualItem = originQueue.tryRemoveFirst();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(originQueue).isEqualTo(expectedQueue);
        assertions.assertThat(actualItem).isEqualTo(expectedItem);
        assertions.assertAll();
    }

    @DisplayName("tryRemoveFirst(): queue is empty => exception")
    @Test
    void tryRemoveFirst_exception() {
        Queue<Integer> queue = new Queue<>();

        Assertions.assertThatThrownBy(queue::tryRemoveFirst).
                isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("clear():")
    @ParameterizedTest(name = """
             origin queue is {0}
             => putLastOrReplace is {1}
            """)
    @MethodSource("provideForClear")
    void clear(Queue<Integer> originQueue, Queue<Integer> expectedQueue) {
        originQueue.clear();

        Assertions.assertThat(originQueue).isEqualTo(expectedQueue);
    }

    @DisplayName("equals(Object):")
    @ParameterizedTest(name = """
             origin queue is {0},
             other queue is {1}
             => expected {2}
            """)
    @MethodSource("provideForEqual")
    void equals(Queue<Integer> origin, Queue<Integer> other, boolean expected) {
        boolean actual = origin.equals(other);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("equals(Object): idempotence property")
    @Test
    void equals_idempotence() {
        Queue<Integer> origin = Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThat(origin.equals(origin)).isTrue();
    }

    @DisplayName("equals(Object): commutative property")
    @Test
    void equals_commutative() {
        Queue<Integer> origin = Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Queue<Integer> other = Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThat(origin.equals(other) == other.equals(origin)).isTrue();
    }

    @DisplayName("equals(Object): transitive property")
    @Test
    void equals_transitive() {
        Queue<Integer> first = Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Queue<Integer> second = Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Queue<Integer> third = Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        Assertions.assertThat(first.equals(second) == second.equals(third) == first.equals(third)).isTrue();
    }


    private static Stream<Arguments> provideForQueueConstructor1() {
        return Stream.of(
                Arguments.of(
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        Queue.of(1),
                        Queue.of(1)
                ),
                Arguments.of(
                        new Queue<>(),
                        new Queue<>()
                )
        );
    }

    private static Stream<Arguments> provideForQueueConstructor2() {
        return Stream.of(
                Arguments.of(
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Queue.of(6, 7, 8, 9, 10),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<Queue<Integer>>) origin -> {
                            for(int i = 0; i < 5; i++) origin.removeFirst();
                        },
                        (Consumer<Queue<Integer>>) copy -> {}
                ),
                Arguments.of(
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Queue.of(6, 7, 8, 9, 10),
                        (Consumer<Queue<Integer>>) origin -> {},
                        (Consumer<Queue<Integer>>) copy -> {
                            for(int i = 0; i < 5; i++) copy.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForOf1() {
        Fabric<Integer, Queue<Integer>> fabric = (size, data) -> {
            Queue<Integer> queue = new Queue<>();
            for(Integer value : data) queue.putLast(value);
            return queue;
        };

        return Stream.of(
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        fabric.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        new Integer[]{1},
                        fabric.create(1)
                ),
                Arguments.of(
                        new Integer[0],
                        new Queue<>()
                )
        );
    }

    private static Stream<Arguments> provideForOf2() {
        Fabric<Integer, Queue<Integer>> fabric = (size, data) -> {
            Queue<Integer> queue = new Queue<>();
            for(Integer value : data) queue.putLast(value);
            return queue;
        };

        return Stream.of(
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{null, null, null, null, null, 6, 7, 8, 9, 10},
                        fabric.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {
                            for(int i = 0; i < 5; i++) data[i] = null;
                        },
                        (Consumer<Queue<Integer>>) queue -> {}
                ),
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        fabric.create(6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {},
                        (Consumer<Queue<Integer>>) queue -> {
                            for(int i = 0; i < 5; i++) queue.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForPutLast() {
        Queue<Integer> modifiedQueue = Queue.of(0, 1, 2, 3, 4, 5, 6, 7);
        for(int i = 0; i < 5; i++) modifiedQueue.putLast(modifiedQueue.removeFirst());

        return Stream.of(
                Arguments.of(
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        100,
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100)
                ),
                Arguments.of(
                        Queue.of(1),
                        100,
                        Queue.of(1, 100)
                ),
                Arguments.of(
                        new Queue<>(),
                        100,
                        Queue.of(100)
                ),
                Arguments.of(
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        null,
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, null)
                ),
                Arguments.of(
                        Queue.of(1),
                        null,
                        Queue.of(1, null)
                ),
                Arguments.of(
                        new Queue<>(),
                        null,
                        Queue.of(new Integer[]{null})
                ),
                Arguments.of(
                        modifiedQueue,
                        100,
                        Queue.of(5, 6, 7, 0, 1, 2, 3, 4, 100)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastIterable() {
        return Stream.of(
                Arguments.of(
                        new Queue<>(),
                        List.of(),
                        new Queue<>()
                ),
                Arguments.of(
                        new Queue<>(),
                        List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        List.of(),
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        List.of(10, 11, 12, 13, 14),
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastData() {
        Fabric<Integer, Queue<Integer>> fabric = (size, data) -> {
            Queue<Integer> queue = new Queue<>();
            for(Integer value : data) queue.putLast(value);
            return queue;
        };

        return Stream.of(
                Arguments.of(
                        new Queue<>(),
                        new Integer[0],
                        new Queue<>()
                ),
                Arguments.of(
                        new Queue<>(),
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        fabric.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        fabric.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        new Integer[0],
                        fabric.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        fabric.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        new Integer[]{10, 11, 12, 13, 14},
                        fabric.create(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
                )
        );
    }

    private static Stream<Arguments> provideForRemoveFirst() {
        Queue<Integer> modifiedQueue = Queue.of(0, 1, 2, 3, 4, 5, 6, 7);
        for(int i = 0; i < 5; i++) modifiedQueue.putLast(modifiedQueue.removeFirst());

        return Stream.of(
                Arguments.of(
                        new Queue<>(),
                        new Queue<>(),
                        null
                ),
                Arguments.of(
                        Queue.of(new Integer[]{null}),
                        new Queue<>(),
                        null
                ),
                Arguments.of(
                        Queue.of(null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        null
                ),
                Arguments.of(
                        Queue.of(100),
                        new Queue<>(),
                        100
                ),
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        0
                ),
                Arguments.of(
                        modifiedQueue,
                        Queue.of(6, 7, 0, 1, 2, 3, 4),
                        5
                )
        );
    }

    private static Stream<Arguments> provideForTryRemoveFirst() {
        return Stream.of(
                Arguments.of(
                        Queue.of(new Integer[]{null}),
                        new Queue<>(),
                        null
                ),
                Arguments.of(
                        Queue.of(null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        null
                ),
                Arguments.of(
                        Queue.of(100),
                        new Queue<>(),
                        100
                ),
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Queue.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        0
                )
        );
    }

    private static Stream<Arguments> provideForClear() {
        return Stream.of(
                Arguments.of(
                        new Queue<>(),
                        new Queue<>()
                ),
                Arguments.of(
                        Queue.of(null, 1, 2, null, 4, 5, 6, 7, 8, 9, null, 11, 12, 13, 14, 15),
                        new Queue<>()
                ),
                Arguments.of(
                        Queue.of(100),
                        new Queue<>()
                )
        );
    }

    private static Stream<Arguments> provideForEqual() {
        Queue<Integer> queue = new Queue<>();
        for(int i = 0; i < 1000; i++) queue.putLast(i);
        for(int i = 0; i < 1000; i++) queue.removeFirst();
        queue.putAllOnLast(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);

        return Stream.of(
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14),
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 100),
                        false
                ),
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14),
                        Queue.of(0, 10, 2, 3, 4, 5, 6, 7, 80, 9, 10, 11, 12, 130, 14),
                        false
                ),
                Arguments.of(
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14),
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14),
                        true
                ),
                Arguments.of(
                        queue,
                        Queue.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14),
                        true
                )
        );
    }

    private static Stream<Arguments> provideForConstructorWithIterable() {
        return Stream.of(
                Arguments.of(new DynamicArray<>(), new Queue<>()),
                Arguments.of(DynamicArray.of(new Integer[]{null}), Queue.of(new Integer[]{null})),
                Arguments.of(DynamicArray.of(100), Queue.of(100)),
                Arguments.of(
                        DynamicArray.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15),
                        Queue.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)
                )
        );
    }
}
