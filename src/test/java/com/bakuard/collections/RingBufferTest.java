package com.bakuard.collections;

import com.bakuard.collections.exceptions.NegativeSizeException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RingBufferTest {

    @DisplayName("RingBuffer(other):")
    @ParameterizedTest(name = """
             origin is {0}
             => expected {1}
            """)
    @MethodSource("provideForCopyConstructor1")
    void RingBuffer_copy1(RingBuffer<Integer> origin, RingBuffer<Integer> expected) {
        RingBuffer<Integer> actual = new RingBuffer<>(origin);

        Assertions.assertThat(actual).isEqualTo(expected);
     }

    @DisplayName("RingBuffer(other): origin and copy must be independent of each other")
    @ParameterizedTest(name = """
             origin is {0},
             change origin and copy after creation
             => expectedOrigin {1},
                expectedCopy {2}
            """)
    @MethodSource("provideForCopyConstructor2")
    void RingBuffer_copy2(RingBuffer<Integer> origin,
                          RingBuffer<Integer> expectedOrigin,
                          RingBuffer<Integer> expectedCopy,
                          Consumer<RingBuffer<Integer>> originMutator,
                          Consumer<RingBuffer<Integer>> copyMutator) {
        RingBuffer<Integer> actualCopy = new RingBuffer<>(origin);

        originMutator.accept(origin);
        copyMutator.accept(actualCopy);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualCopy).isEqualTo(expectedCopy);
        assertions.assertThat(origin).isEqualTo(expectedOrigin);
        assertions.assertAll();
    }

    @DisplayName("RingBuffer(iterable):")
    @ParameterizedTest(name = """
             iterable is {0}
             => expected {1}
            """)
    @MethodSource("provideForIterableConstructor1")
    void RingBuffer_iterable1(Iterable<Integer> iterable, RingBuffer<Integer> expected) {
        RingBuffer<Integer> actual = new RingBuffer<>(iterable);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("RingBuffer(iterable): iterable and buffer must be independent of each other")
    @ParameterizedTest(name = """
             iterable is {0},
             change iterable and buffer after creation
             => expectedIterable {1},
                expectedBuffer {2}
            """)
    @MethodSource("provideForIterableConstructor2")
    void RingBuffer_iterable2(Iterable<Integer> iterable,
                              Iterable<Integer> expectedIterable,
                              RingBuffer<Integer> expectedBuffer,
                              Consumer<Iterable<Integer>> iterableMutator,
                              Consumer<RingBuffer<Integer>> bufferMutator) {
        RingBuffer<Integer> actual = new RingBuffer<>(iterable);

        iterableMutator.accept(iterable);
        bufferMutator.accept(actual);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedBuffer);
        assertions.assertThat(iterable).isEqualTo(expectedIterable);
        assertions.assertAll();
    }

    @DisplayName("of(...data):")
    @ParameterizedTest(name = """
             data is {0}
             => expected {1}
            """)
    @MethodSource("provideForOf1")
    void of1(Integer[] data, RingBuffer<Integer> expected) {
        RingBuffer<Integer> actual = RingBuffer.of(data);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("of(...data): data and ring buffer must be independent of each other")
    @ParameterizedTest(name = """
             data is {0},
             change data and ring buffer after creation
             => expected data {1},
                expected ring buffer {2}
            """)
    @MethodSource("provideForOf2")
    void of2(Integer[] data,
             Integer[] expectedData,
             RingBuffer<Integer> expectedBuffer,
             Consumer<Integer[]> dataMutator,
             Consumer<RingBuffer<Integer>> bufferMutator) {
        RingBuffer<Integer> actual = RingBuffer.of(data);

        dataMutator.accept(data);
        bufferMutator.accept(actual);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual).isEqualTo(expectedBuffer);
        assertions.assertThat(data).isEqualTo(expectedData);
        assertions.assertAll();
    }

    @DisplayName("putLastOrReplace(value):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             addedValue is {1}
             => expected buffer is {2},
                expected value is {3}
            """)
    @MethodSource("provideForPutLastOrReplace")
    void putLastOrReplace(RingBuffer<Integer> origin,
                          Integer addedValue,
                          RingBuffer<Integer> expected,
                          Integer expectedReturnedValue) {
        Integer actualReturnedValue = origin.putLastOrReplace(addedValue);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrReplace(iterable):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrReplace_iterable")
    void putAllOnLastOrReplace_iterable(RingBuffer<Integer> origin,
                                        Array<Integer> addedValues,
                                        RingBuffer<Integer> expected,
                                        Array<Integer> expectedReturnedValues) {
        Array<Integer> actualReturnedValues = origin.putAllOnLastOrReplace(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrReplace(data):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrReplace_array")
    void putAllOnLastOrReplace_array(RingBuffer<Integer> origin,
                                     Integer[] addedValues,
                                     RingBuffer<Integer> expected,
                                     Array<Integer> expectedReturnedValues) {
        Array<Integer> actualReturnedValues = origin.putAllOnLastOrReplace(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putLastOrSkip(value):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             addedValue is {1}
             => expected buffer is {2},
                expected value is {3}
            """)
    @MethodSource("provideForPutLastOrSkip")
    void putLastOrSkip(RingBuffer<Integer> origin,
                       Integer addedValue,
                       RingBuffer<Integer> expected,
                       boolean expectedReturnedValue) {
        boolean actualReturnedValue = origin.putLastOrSkip(addedValue);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrSkip(iterable):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrSkip_iterable")
    void putAllOnLastOrSkip_iterable(RingBuffer<Integer> origin,
                                     Array<Integer> addedValues,
                                     RingBuffer<Integer> expected,
                                     int expectedReturnedValues) {
        int actualReturnedValues = origin.putAllOnLastOrSkip(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("putAllOnLastOrSkip(data):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             added values is {1}
             => expected buffer is {2},
                expected values is {3}
            """)
    @MethodSource("provideForPutAllOnLastOrSkip_array")
    void putAllOnLastOrSkip_array(RingBuffer<Integer> origin,
                                  Integer[] addedValues,
                                  RingBuffer<Integer> expected,
                                  int expectedReturnedValues) {
        int actualReturnedValues = origin.putAllOnLastOrSkip(addedValues);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actualReturnedValues).isEqualTo(expectedReturnedValues);
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertAll();
    }

    @DisplayName("removeFirst():")
    @ParameterizedTest(name = """
             origin buffer is {0}
             => expected buffer is {1},
                expected value is {2}
            """)
    @MethodSource("provideForRemoveFirst")
    void removeFirst(RingBuffer<Integer> origin,
                     RingBuffer<Integer> expected,
                     Integer expectedReturnedValue) {
        Integer actualReturnedValue = origin.removeFirst();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(origin).isEqualTo(expected);
        assertions.assertThat(actualReturnedValue).isEqualTo(expectedReturnedValue);
        assertions.assertAll();
    }

    @DisplayName("grow(extraSize):")
    @ParameterizedTest(name = """
             origin buffer is {0}
             extraSize is {1}
             => expected buffer is {2}
            """)
    @MethodSource("provideForGrow")
    void grow(RingBuffer<Integer> origin, int extraSize, RingBuffer<Integer> expected) {
        origin.grow(extraSize);

        Assertions.assertThat(origin).isEqualTo(expected);
    }

    @DisplayName("""
            grow(extraSize):
                extraSize < 0
                => exception
            """)
    @Test
    void grow_exception() {
        RingBuffer<Integer> origin = RingBuffer.of(1, 2, 3, 4, 5);

        Assertions.assertThatThrownBy(() -> origin.grow(-1))
                .isInstanceOf(NegativeSizeException.class);
    }


    private static Stream<Arguments> provideForCopyConstructor1() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        RingBuffer.of(1),
                        RingBuffer.of(1)
                ),
                Arguments.of(
                        new RingBuffer<>(100),
                        new RingBuffer<>(100)
                )
        );
    }

    private static Stream<Arguments> provideForCopyConstructor2() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(6, 7, 8, 9, 10),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<RingBuffer<Integer>>) origin -> {
                            for(int i = 0; i < 5; i++) origin.removeFirst();
                        },
                        (Consumer<RingBuffer<Integer>>) copy -> {}
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(6, 7, 8, 9, 10),
                        (Consumer<RingBuffer<Integer>>) origin -> {},
                        (Consumer<RingBuffer<Integer>>) copy -> {
                            for(int i = 0; i < 5; i++) copy.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForIterableConstructor1() {
        return Stream.of(
                Arguments.of(
                        List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        List.of(1),
                        RingBuffer.of(1)
                ),
                Arguments.of(
                        List.of(),
                        new RingBuffer<>(0)
                )
        );
    }

    private static Stream<Arguments> provideForIterableConstructor2() {
        return Stream.of(
                Arguments.of(
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Array.of(null, null, null, null, null, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        (Consumer<Array<Integer>>) iterable -> {
                            for(int i = 0; i < 5; i++) iterable.replace(i, null);
                        },
                        (Consumer<RingBuffer<Integer>>) buffer -> {}
                ),
                Arguments.of(
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        (Consumer<Array<Integer>>) iterable -> {},
                        (Consumer<RingBuffer<Integer>>) buffer -> {
                            for(int i = 0; i < 5; i++) buffer.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForOf1() {
        return Stream.of(
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        new Integer[]{1},
                        RingBuffer.of(1)
                ),
                Arguments.of(
                        new Integer[0],
                        new RingBuffer<>(0)
                )
        );
    }

    private static Stream<Arguments> provideForOf2() {
        return Stream.of(
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{null, null, null, null, null, 6, 7, 8, 9, 10},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {
                            for(int i = 0; i < 5; i++) data[i] = null;
                        },
                        (Consumer<RingBuffer<Integer>>) buffer -> {}
                ),
                Arguments.of(
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(6, 7, 8, 9, 10),
                        (Consumer<Integer[]>) data -> {},
                        (Consumer<RingBuffer<Integer>>) buffer -> {
                            for(int i = 0; i < 5; i++) buffer.removeFirst();
                        }
                )
        );
    }

    private static Stream<Arguments> provideForPutLastOrReplace() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(),
                        1,
                        RingBuffer.of(),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(1),
                        2,
                        RingBuffer.of(2),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        11,
                        RingBuffer.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        1
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        1,
                        RingBuffer.of(1),
                        null
                ),
                Arguments.of(
                        RingBuffer.withExtraSize(5, 1, 2, 3, 4, 5),
                        6,
                        RingBuffer.withExtraSize(4, 1, 2, 3, 4, 5, 6),
                        null
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrReplace_iterable() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of()
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(11),
                        RingBuffer.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        Array.of(1)
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(11, 12, 13, 14, 15),
                        RingBuffer.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Array.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        RingBuffer.of(16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        Array.of(1, 2, 3, 4, 5, 6),
                        RingBuffer.withExtraSize(4, 1, 2, 3, 4, 5, 6),
                        Array.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Array.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.withExtraSize(5, 1, 2, 3, 4, 5),
                        Array.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                        RingBuffer.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrReplace_array() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[0],
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of()
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11},
                        RingBuffer.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        Array.of(1)
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15},
                        RingBuffer.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Array.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25},
                        RingBuffer.of(16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6},
                        RingBuffer.withExtraSize(4, 1, 2, 3, 4, 5, 6),
                        Array.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of()
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        RingBuffer.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        Array.of(1, 2, 3, 4, 5)
                ),
                Arguments.of(
                        RingBuffer.withExtraSize(5, 1, 2, 3, 4, 5),
                        new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20},
                        RingBuffer.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )
        );
    }

    private static Stream<Arguments> provideForPutLastOrSkip() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        11,
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        false
                ),
                Arguments.of(
                        RingBuffer.of(1),
                        2,
                        RingBuffer.of(1),
                        false
                ),
                Arguments.of(
                        RingBuffer.of(),
                        1,
                        RingBuffer.of(),
                        false
                ),
                Arguments.of(
                        RingBuffer.withExtraSize(5, 1, 2, 3, 4, 5),
                        6,
                        RingBuffer.withExtraSize(4, 1, 2, 3, 4, 5, 6),
                        true
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        1,
                        RingBuffer.of(1),
                        true
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrSkip_iterable() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(11),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(11, 12, 13, 14, 15),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        Array.of(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        Array.of(1, 2, 3, 4, 5, 6),
                        RingBuffer.withExtraSize(4, 1, 2, 3, 4, 5, 6),
                        6
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        RingBuffer.withExtraSize(6, 1, 2, 3, 4, 5),
                        Array.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        6
                ),
                Arguments.of(
                        RingBuffer.of(),
                        Array.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.of(),
                        0
                )
        );
    }

    private static Stream<Arguments> provideForPutAllOnLastOrSkip_array() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[0],
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        new Integer[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6},
                        RingBuffer.withExtraSize(4, 1, 2, 3, 4, 5, 6),
                        6
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10
                ),
                Arguments.of(
                        RingBuffer.withExtraSize(6, 1, 2, 3, 4, 5),
                        new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                        6
                ),
                Arguments.of(
                        RingBuffer.of(),
                        new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                        RingBuffer.of(),
                        0
                )
        );
    }

    private static Stream<Arguments> provideForRemoveFirst() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(),
                        RingBuffer.of(),
                        null
                ),
                Arguments.of(
                        RingBuffer.of(1),
                        new RingBuffer<>(1),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.withExtraSize(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        1
                ),
                Arguments.of(
                        RingBuffer.of(new Integer[]{null}),
                        new RingBuffer<>(1),
                        null
                ),
                Arguments.of(
                        RingBuffer.of(null, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        RingBuffer.withExtraSize(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        null
                )
        );
    }

    private static Stream<Arguments> provideForGrow() {
        return Stream.of(
                Arguments.of(
                        RingBuffer.of(),
                        0,
                        RingBuffer.of()
                ),
                Arguments.of(
                        RingBuffer.of(),
                        10,
                        new RingBuffer<>(10)
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        0,
                        new RingBuffer<>(10)
                ),
                Arguments.of(
                        new RingBuffer<>(10),
                        10,
                        new RingBuffer<>(20)
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        0,
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                ),
                Arguments.of(
                        RingBuffer.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                        10,
                        RingBuffer.withExtraSize(10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                )
        );
    }
}
