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

public class DequeTest {

    @DisplayName("Deque(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForDequeConstructor1")
    void Deque_copy(Deque<Integer> origin, Deque<Integer> expected) {
        Deque<Integer> actual = new Deque<>(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Deque(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0},
             change origin and copy after creation
             => expectedOrigin {1},
                expectedCopy {2}
            """)
    @MethodSource("provideForDequeConstructor2")
    void Deque_copy_doNotChangeOrigin(Deque<Integer> origin,
                                      Deque<Integer> expectedOrigin,
                                      Deque<Integer> expectedCopy,
                                      Consumer<Deque<Integer>> originMutator,
                                      Consumer<Deque<Integer>> copyMutator) {
        Deque<Integer> actualCopy = new Deque<>(origin);

        originMutator.accept(origin);
        copyMutator.accept(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @DisplayName("of(...data):")
    @ParameterizedTest(name = """
             data is {0}
             => expected {1}
            """)
    @MethodSource("provideForOf1")
    void of(Integer[] data, Deque<Integer> expected) {
        Deque<Integer> actual = Deque.of(data);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("of(...data): data and deque must be independent of each other")
    @ParameterizedTest(name = """
             data is {0},
             change data and deque after creation
             => expected data {1},
                expected queue {2}
            """)
    @MethodSource("provideForOf2")
    void of_doNotChangeOrigin(Integer[] data,
                              Integer[] expectedData,
                              Deque<Integer> expectedQueue,
                              Consumer<Integer[]> dataMutator,
                              Consumer<Deque<Integer>> queueMutator) {
        Deque<Integer> actual = Deque.of(data);

        dataMutator.accept(data);
        queueMutator.accept(actual);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedQueue);
        assertions.assertThat(data).isEqualTo(expectedData);
        assertions.assertAll();
    }

    @DisplayName("putFirst(value):")
    @ParameterizedTest(name = """
             origin deque is {0}
             value is {1}
             => expected deque {2}
            """)
    @MethodSource("provideForPutFirst")
    void putFirst(Deque<Integer> originDeque, Integer addedValue, Deque<Integer> expectedDeque) {
        originDeque.putFirst(addedValue);

        Assertions.assertThat(originDeque).isEqualTo(expectedDeque);
    }

    @DisplayName("putAllOnFirst(iterable):")
    @ParameterizedTest(name = """
             origin deque is {0}
             iterable is {1}
             => expected deque {2}
            """)
    @MethodSource("provideForPutAllOnFirstIterable")
    void putAllOnFirst_Iterable(Deque<Integer> originDeque,
                                Iterable<Integer> iterable,
                                Deque<Integer> expectedDeque) {
        originDeque.putAllOnFirst(iterable);

        Assertions.assertThat(originDeque).isEqualTo(expectedDeque);
    }

    @DisplayName("putAllOnFirst(data):")
    @ParameterizedTest(name = """
             origin deque is {0}
             data is {1}
             => expected deque {2}
            """)
    @MethodSource("provideForPutAllOnFirstData")
    void putAllOnFirst_Data(Deque<Integer> originDeque, Integer[] data, Deque<Integer> expectedDeque) {
        originDeque.putAllOnFirst(data);

        Assertions.assertThat(originDeque).isEqualTo(expectedDeque);
    }

    @DisplayName("removeLast():")
    @ParameterizedTest(name = """
             origin deque is {0}
             => expected deque {1}
                returned item {2}
            """)
    @MethodSource("provideForRemoveLast")
    void removeLast(Deque<Integer> originDeque, Deque<Integer> expectedDeque, Integer expectedItem) {
        Integer actualItem = originDeque.removeLast();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(originDeque).isEqualTo(expectedDeque);
        assertions.assertThat(actualItem).isEqualTo(expectedItem);
        assertions.assertAll();
    }

    @DisplayName("tryRemoveLast():")
    @ParameterizedTest(name = """
             origin deque is {0}
             => expected deque {1}
                returned item {2}
            """)
    @MethodSource("provideForTryRemoveLast")
    void tryRemoveLast(Deque<Integer> originDeque, Deque<Integer> expectedDeque, Integer expectedItem) {
        Integer actualItem = originDeque.tryRemoveLast();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(originDeque).isEqualTo(expectedDeque);
        assertions.assertThat(actualItem).isEqualTo(expectedItem);
        assertions.assertAll();
    }

    @DisplayName("tryRemoveLast(): deque is empty => exception")
    @Test
    void tryRemoveLast_exception() {
        Deque<Integer> deque = new Deque<>();

        Assertions.assertThatThrownBy(deque::tryRemoveLast).
                isInstanceOf(NoSuchElementException.class);
    }


    private static Stream<Arguments> provideForDequeConstructor1() {
        return Stream.of(
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        Deque.of(1),
                        Deque.of(1)
                ),
                Arguments.of(
                        new Deque<>(),
                        new Deque<>()
                )
        );
    }

    private static Stream<Arguments> provideForDequeConstructor2() {
        return Stream.of(
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Deque.of(6, 7, 8, 9, 10),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<Deque<Integer>>) origin -> {
                            for(int i = 0; i < 5; i++) origin.removeFirst();
                        },
                        (Consumer<Deque<Integer>>) copy -> {}
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Deque.of(6, 7, 8, 9, 10),
                        (Consumer<Deque<Integer>>) origin -> {},
                        (Consumer<Deque<Integer>>) copy -> {
                            for(int i = 0; i < 5; i++) copy.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForOf1() {
        Fabric<Integer, Deque<Integer>> fabric = data -> {
            Deque<Integer> deque = new Deque<>();
            for(Integer value : data) deque.putLast(value);
            return deque;
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
                        new Deque<>()
                )
        );
    }

    private static Stream<Arguments> provideForOf2() {
        Fabric<Integer, Deque<Integer>> fabric = data -> {
            Deque<Integer> deque = new Deque<>();
            for(Integer value : data) deque.putLast(value);
            return deque;
        };

        return Stream.of(
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{null, null, null, null, null, 6, 7, 8, 9, 10},
                        fabric.create(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {
                            for(int i = 0; i < 5; i++) data[i] = null;
                        },
                        (Consumer<Deque<Integer>>) deque -> {}
                ),
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        fabric.create(6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {},
                        (Consumer<Deque<Integer>>) deque -> {
                            for(int i = 0; i < 5; i++) deque.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForPutFirst() {
        Deque<Integer> modifiedDeque = Deque.of(0, 1, 2, 3, 4, 5, 6, 7);
        for(int i = 0; i < 5; i++) modifiedDeque.putLast(modifiedDeque.removeFirst());

        return Stream.of(
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        100,
                        Deque.of(100, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        Deque.of(1),
                        100,
                        Deque.of(100, 1)
                ),
                Arguments.of(
                        new Deque<>(),
                        100,
                        Deque.of(100)
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        null,
                        Deque.of(null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        Deque.of(1),
                        null,
                        Deque.of(null, 1)
                ),
                Arguments.of(
                        new Deque<>(),
                        null,
                        Deque.of(new Integer[]{null})
                ),
                Arguments.of(
                        modifiedDeque,
                        100,
                        Deque.of(100, 5, 6, 7, 0, 1, 2, 3, 4)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnFirstIterable() {
        return Stream.of(
                Arguments.of(
                        new Deque<>(),
                        List.of(),
                        new Deque<>()
                ),
                Arguments.of(
                        new Deque<>(),
                        List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        List.of(),
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        List.of(10, 11, 12, 13, 14),
                        Deque.of(10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnFirstData() {
        return Stream.of(
                Arguments.of(
                        new Deque<>(),
                        new Integer[0],
                        new Deque<>()
                ),
                Arguments.of(
                        new Deque<>(),
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        new Integer[0],
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                ),
                Arguments.of(
                        Deque.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                        new Integer[]{10, 11, 12, 13, 14},
                        Deque.of(10, 11, 12, 13, 14, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                )
        );
    }

    private static Stream<Arguments> provideForRemoveLast() {
        Deque<Integer> modifiedDeque = Deque.of(0, 1, 2, 3, 4, 5, 6, 7);
        for(int i = 0; i < 5; i++) modifiedDeque.putLast(modifiedDeque.removeFirst());

        return Stream.of(
                Arguments.of(
                        new Deque<>(),
                        new Deque<>(),
                        null
                ),
                Arguments.of(
                        Deque.of(new Integer[]{null}),
                        new Deque<>(),
                        null
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, null),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        null
                ),
                Arguments.of(
                        Deque.of(100),
                        new Deque<>(),
                        100
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        100
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        100
                ),
                Arguments.of(
                        modifiedDeque,
                        Deque.of(5, 6, 7, 0, 1, 2, 3),
                        4
                )
        );
    }

    private static Stream<Arguments> provideForTryRemoveLast() {
        return Stream.of(
                Arguments.of(
                        Deque.of(new Integer[]{null}),
                        new Deque<>(),
                        null
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, null),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        null
                ),
                Arguments.of(
                        Deque.of(100),
                        new Deque<>(),
                        100
                ),
                Arguments.of(
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100),
                        Deque.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        100
                )
        );
    }
}
